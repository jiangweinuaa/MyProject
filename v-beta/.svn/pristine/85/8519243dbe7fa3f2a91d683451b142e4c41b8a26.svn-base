package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Generated;
import java.util.List;

/**
 * 发票类型
 * @date   2024-09-13
 * @author 01029 
 */

@Getter
@Setter
public class DCP_InvoiceTypeCreateReq extends JsonBasicReq {

    private levelRequest request;

 

    @Getter
    @Setter
    public class levelRequest {
        private String nation;

        private String invoiceCode;

        private String invoiceType;

        private String invoiceGenre;
        
        private String taxCalType;

        private String status;

        private String taxArea;

        private List<InvoiceNameLang> InvoiceName_lang;

 

    }

    @Getter
    @Setter
    public static class InvoiceNameLang {
        private String langType;
        private String name;
    }

 

}
