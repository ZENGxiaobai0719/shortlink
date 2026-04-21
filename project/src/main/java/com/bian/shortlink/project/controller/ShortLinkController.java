package com.bian.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bian.shortlink.project.common.convention.result.Result;
import com.bian.shortlink.project.common.convention.result.Results;
import com.bian.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.bian.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.bian.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.bian.shortlink.project.dto.resp.ShortLinkPageResqDTO;
import com.bian.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/shor-tlink/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 短链接分页
     */
    @GetMapping("/api/shor-tlink/v1/page")
    public Result<IPage<ShortLinkPageResqDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }
}
