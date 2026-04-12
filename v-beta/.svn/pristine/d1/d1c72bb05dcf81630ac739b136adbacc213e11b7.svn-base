package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务函数：DCP_FaPiaoTemplateQuery
 * 服务说明：发票模板查询
 *
 * @author wangzyc
 * @since 2021-1-27
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DCP_FaPiaoTemplateQueryRes extends JsonRes {

    private List<level1Elm> datas;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level1Elm{
        private String templateId; // 模板编码
        private String templateName; // 模板名称
        private String platformType; // 发票平台编码
        private String platformName; // 发票平台名称
        private String templateType; // 模板类型1-通用2-专用
        private String memo; // 备注
        private String status; // 状态：-1未启用100已启用0已禁用
        private String createTime; // 创建时间，降序第一列
    }

}
