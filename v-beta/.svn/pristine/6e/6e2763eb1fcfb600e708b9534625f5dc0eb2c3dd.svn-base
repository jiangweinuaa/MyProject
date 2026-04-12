package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_InvoiceTypeCreateReq.InvoiceNameLang;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_InvoiceTypeQueryRes extends JsonRes {

    @Getter
    @Setter
    private List<InvoiceType> datas;

    @Getter
    @Setter
    public  class InvoiceType {

        private String invoiceCode;
        
        private String invoiceName;

        private String invoiceType;

        private String invoiceGenre;
        
        private String taxCalType;

        private String status;

        private String taxArea;
        
        private String creatorID;
        
        private String creatorName;
        
        private String creatorDeptID;
        
        private String creatorDeptName;
        
        private String createDatetime;
        
        private String lastmodifyID;
        
        private String lastmodifyName;
        
        private String lastmodifyDatetime;
        
        private List<InvoiceTypeName> InvoiceName_lang;

    }

    @Getter
    @Setter
    public  class InvoiceTypeName{
        private String langType;
        private String name;
    }

 
}
