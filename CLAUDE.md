# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

短链接 (Short Link) SaaS platform — a URL shortening service with admin management, a gateway, and analytics. Multi-module Maven project with a Vue 3 frontend.

## Build & run commands

```bash
# Build all modules (Java 17, Spring Boot 3.0.7, Spring Cloud 2022.0.3)
mvn clean compile

# Build with spotless formatting applied
mvn compile

# Package individual Spring Boot apps
mvn clean package -pl gateway
mvn clean package -pl project
mvn clean package -pl aggregation

# Run tests
mvn test
mvn test -pl project

# Frontend
cd frontend/console
npm run dev      # Dev server on port 5173, proxies /api to gateway at 8000
npm run build    # Production build
```

## Module architecture

### `gateway` (port 8000)
Spring Cloud Gateway. The single entry point for all HTTP traffic. Validates user tokens via Redis lookup in `TokenValidateGatewayFilterFactory` (checks `short-link:login:{username}` hash). Has two profile modes configured in `application.yaml`:
- **`dev`**: Routes to `lb://short-link-admin` and `lb://short-link-project` via Nacos service discovery
- **`aggregation`**: Routes to `lb://short-link-aggregation` (single merged service)

### `admin` (port 8002)
Admin/management backend. Controllers follow path pattern `/api/short-link/admin/v1/*`. Handles user registration/login, group CRUD, short-link CRUD (via OpenFeign to `project`), recycle bin, and stats. Uses MyBatis-Plus with ShardingSphere for the `t_user` table.

The admin module delegates all short-link operations to the `project` module through `ShortLinkActualRemoteService` (OpenFeign client targeting `short-link-project`). It does NOT directly manipulate short-link data.

### `project` (port 8001)
Core short-link engine. Controllers use `/api/short-link/v1/*`. Handles short-link creation, redirection (`GET /{short-uri}`), stats collection, and recycle bin. Key components:
- **URL shortening**: MurmurHash32 → base62 conversion (`HashUtil.hashToBase62`)
- **Bloom filter**: Redisson RBloomFilter guards against duplicate short links
- **Stats pipeline**: Access events are recorded via Redis Stream → consumers persist to MySQL stats tables (`t_link_access_stats`, `t_link_browser_stats`, etc.)
- **Sentinel**: Flow control on `createShortLink` endpoint

### `aggregation`
Combines `admin` and `project` into a single deployable service. Scans both packages (`admin`, `com.bian.shortlink.project`). Used for simpler deployment — pair with gateway profile `aggregation` so the gateway routes all traffic here.

### `frontend/console`
Vue 3 + Vite + TypeScript + Element Plus + ECharts. Uses Pinia for state, vue-router, vue-i18n. Dev server proxies `/api` to `localhost:8000` (gateway). Environment variable `VITE_API_BASE=/api/short-link/admin/v1`.

## Database

Single MySQL database `link` with ShardingSphere HASH_MOD sharding (16 shards) on three tables:
- `t_link_{0..15}` — sharded by `gid`
- `t_link_goto_{0..15}` — sharded by `full_short_url`
- `t_group_{0..15}` — sharded by `username`

Configs at `**/resources/shardingsphere-config-{env}.yaml`, selected via `database.env` property (defaults to `dev`).

## Key conventions

- **Package naming**: `admin` module uses short package names (e.g. `admin.controller`, `admin.service`). `project` uses full package names (`com.bian.shortlink.project.controller`).
- **Result wrapper**: All API responses use `Result<T>` with code `"0"` for success. Built via `Results.success(data)` / `Results.failure(code, msg)`.
- **Exception hierarchy**: `AbstractException` → `ClientException` / `ServiceException` / `RemoteException`. Each implements `IErrorCode` (provides `code()` and `message()`).
- **Base DO**: All entities extend `BaseDO` which provides `createTime`, `updateTime`, `delFlag` (logical delete).
- **User context**: `UserContext` holds per-request user info via Alibaba `TransmittableThreadLocal` (supports thread pool propagation). Set by `UserTransmitFilter` in admin, `UserTransmitInterceptor` in project.
- **Services extend MyBatis-Plus `IService`/`ServiceImpl`** (e.g., `GroupService extends IService<GroupDO>`).
- **Lombok**: Used everywhere — `@Data`, `@RequiredArgsConstructor`, `@Slf4j`, `@SneakyThrows`.
- **Infrastructure**: Nacos for service discovery (default `127.0.0.1:8848`), Redis (`127.0.0.1:6379`), Redisson for distributed locks and Bloom filters.

## Important note

There are duplicate class definitions between `admin` and `project` for common infrastructure (e.g., `Result`, `Results`, `BaseDO`, `IErrorCode`, exception classes). The aggregation module compiles both, so Spring bean name conflicts are resolved with explicit names like `@Component("globalExceptionHandlerByAdmin")` and `@RestController(value = "shortLinkControllerByAdmin")`.
