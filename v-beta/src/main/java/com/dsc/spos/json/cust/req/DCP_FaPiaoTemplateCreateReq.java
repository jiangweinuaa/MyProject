package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务函数：DCP_FaPiaoTemplateCreate
 * 服务说明：发票模板新增
 *
 * @author wangzyc
 * @since 2021-1-27
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DCP_FaPiaoTemplateCreateReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level1Elm {
        private String templateId; // 模板编码
        private String templateName; // 模板名称
        private String platformType; // 发票平台
        private String templateType; // 模板类型1-通用2-专用
        private List<level2Elm> shopList; // 专用时不可空
        private String memo; // 备注
        private String status; // 状态：-1未启用100已启用0已禁用
        private List<level3Elm> params; // 参数详细
        private String isDeleteShop; // 是否删除门店：Y/N，当前门店存在于系统中时，前端确认删除后传Y
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level2Elm {
        private String shopId; // 门店id
        private String shopName; // 门店名称
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level3Elm {
        private String param; // 参数编码
        private String value; // 参数值
    }
}
