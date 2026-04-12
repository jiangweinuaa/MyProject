package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_InvoiceTypeCreateReq.InvoiceNameLang;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_BillDateQueryRes extends JsonRes {

    @Getter
    @Setter
    private List<BillDate> datas;

    @Getter
    @Setter
    public  class BillDate {

    	private String billDateType;       
    	private String billDateNo;    
    	private String billDateName;     
    	private String fDate;              
    	private String addMonths;          
    	private String addDays;            
    	private String status;             
    	private String creatorID;          
    	private String creatorName;        
    	private String creatorDeptID;      
    	private String creatorDeptName;    
    	private String create_Datetime;    
    	private String lastModifyID;       
    	private String lastModifyName;     
    	private String lastModify_Datetime;
        
        private List<LangName> lang_List;

    }

    @Getter
    @Setter
    public  class LangName{
        private String langType;
        private String name;
    }

 
}
