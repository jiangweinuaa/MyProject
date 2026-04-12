package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_LineQuery
 * 服务说明：获取路线服务
 * @author jinzma
 * @since  2023-12-22
 */
@Data
public class DCP_LineQueryRes extends JsonRes {
    private List<Datas> datas;
    @Data
    public class Datas {
        private String lineNo;
        private String lineName;
    }
}
