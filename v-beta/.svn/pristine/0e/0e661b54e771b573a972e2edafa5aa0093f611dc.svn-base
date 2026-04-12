package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 原因查询
 * @author: wangzyc
 * @create: 2022-03-08
 */
@Data
public class DCP_ReasonQuery_OpenRes extends JsonBasicRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String reasonType; // 原因类型
        private List<level2Elm> reasonList; //
    }

    @Data
    public class level2Elm{
        private String reasonName; // 原因名称
        private String reasonId; // 原因比那吗
    }
}
