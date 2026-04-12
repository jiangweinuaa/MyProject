package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Generated;
import java.util.List;

/**
 * 网点信息
 *
 * @author liyyd
 */

@Getter
@Setter
public class DCP_BankCreateReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    @Getter
    @Setter
    public class levelRequest {
        private String nation;

        private String bankCode;

        private String ebankCode;

        private String status;

        private List<BankNameLang> bankName_lang;

        private List<BankFNameLang> bankFname_lang;

    }

    @Getter
    @Setter
    public static class BankNameLang {
        private String langType;
        private String name;
    }

    @Getter
    @Setter
    public static class BankFNameLang {
        private String langType;
        private String name;
    }

}
