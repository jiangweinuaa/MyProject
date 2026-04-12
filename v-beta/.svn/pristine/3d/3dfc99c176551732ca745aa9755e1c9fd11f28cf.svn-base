package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约时间查询
 * @author: wangzyc
 * @create: 2021-07-20
 */
@Data
public class DCP_ReserveTimeQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm {
        private String item;
        private String cycle;
        private String beginTime;
        private String endTime;
        private String status;
    }
}
