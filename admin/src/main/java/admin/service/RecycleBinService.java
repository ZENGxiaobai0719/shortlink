package admin.service;


import admin.common.convention.result.Result;
import admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * URL 回收站接口层
 */
public interface RecycleBinService {

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 请求参数
     * @return 返回参数包装
     */
    Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
