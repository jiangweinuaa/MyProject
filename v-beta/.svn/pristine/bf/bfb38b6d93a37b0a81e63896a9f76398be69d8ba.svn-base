package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_EmployeeInfoQuery_Open
 *   說明：员工信息查询
 * 服务说明：员工信息查询
 * @author wangzyc
 * @since  2021-4-14
 */
@Data
public class DCP_EmployeeInfoQuery_OpenRes extends JsonBasicRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String opNo;            // 员工编号
        private String opName;          // 员工名称
        private String departNo;        // 所属部门
        private String goodsNum;        // 制作数量（商品条数*qty）
        private String fraction;        // 绩效分
        private String activation;      // 是否激活Y/N，若表为空默认返回N

    }
}
