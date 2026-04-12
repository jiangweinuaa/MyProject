package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 顾问查询
 * @author: wangzyc
 * @create: 2021-08-02
 */
@Data
public class DCP_AdvisorQuery_OpenRes extends JsonRes {
    private level11Elm datas;

    @Data
    public class level11Elm{
        private List<level1Elm> opList ; // 顾问列表
        private List<level2Elm> labelList; // 顶部标签列表
    }

    @Data
    public class level1Elm{
        private String opNo; // 顾问编号
        private String opName; // 顾问名称
        private String headImage; // 头像
        private String professionalId; // 职称代号
        private String professionalName; // 职称名称
        private String ability; // 能力介绍
        private String highPraise; //  平均评分，保留整数(四舍五入) (1-5)
        private List<level3Elm> labelList; // 标签列表
    }

    @Data
    @Accessors(chain=true)
    public class level2Elm{
        private String labelId; // 标签编码
        private String labelName; // 标签名称
        private String number; // 汇总数量
    }

    @Data
    public class level3Elm{
        private String labelId; // 标签编码
        private String labelName; // 标签名称
    }
}
