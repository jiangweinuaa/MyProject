package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_TagDetailQuery_Open
 *   說明：生产标签商品查询
 * 服务说明：生产标签商品查询
 * @author wangzyc
 * @since  2021-5-10
 */
@Data
public class DCP_TagDetailQuery_OpenRes extends JsonBasicRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
//        private String tagNo;       // 标签编号
//        private String tagName;     // 标签名称
        private String pluNo;       // 商品编号
        private String pluName;     // 商品名称
        private List<level2Elm> specList;   // 规格名称列表
        private List<level3Elm> featureList;    // 特征列表
    }

    @Data
    public class level2Elm{
        private String specName;    // 规格名称
    }

    @Data
    public class level3Elm{
        private String featureNo;   // 特征码编码
        private String featureName; // 特征码名称
    }
}
