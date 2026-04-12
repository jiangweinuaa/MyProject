package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 门店休息日查询
 * @author: wangzyc
 * @create: 2021-07-27
 */
@Data
public class DCP_HolidayQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String item;
        private String beginDate;
        private String endDate;
        private String memo;
    }
}
