package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务函数：DCP_FaPiaoTemplateDetail
 * 服务说明：发票模板详情
 *
 * @author wangzyc
 * @since 2021-1-27
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DCP_FaPiaoPlatformQueryRes extends JsonBasicRes {
    private List<level1ELm> datas ;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level1ELm{
        private String platformType; //发票平台编码
        private String platformName; // 发票平台名称
        private String sortId; // 显示顺序，升序第一列
        private List<level2ELm> params ; // 参数详细
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class level2ELm{
        private String param; // 参数编码
        private String name; // 参数名称
        private String value; // 返回空字符串，用于页面端渲染
        private String memo; // 参数备注
        private String groupId; // 分组编码，升序第一列
        private String sortId; // 显示顺序，升序第二列
    }
}
