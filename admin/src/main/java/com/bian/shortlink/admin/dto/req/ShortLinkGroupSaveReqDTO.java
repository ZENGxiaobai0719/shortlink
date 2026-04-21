package com.bian.shortlink.admin.dto.req;


import lombok.Data;

/**
 * 短链分组保存请求参数
 */
@Data
public class ShortLinkGroupSaveReqDTO {
    /**
     * 分组名称
     */
    private String name;
}
