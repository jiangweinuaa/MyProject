package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_PayDateQueryRes extends JsonRes {

    @Getter
    @Setter
    private List<PayDate> datas;

    @Getter
    @Setter
    public  class PayDate {

    	private String payDateType;
    	private String payDateName;
    	private String payDateNo;  
    	private String payDateBase;
    	private String pSeasons;   
    	private String pMonths;    
    	private String pDays;      
    	private String dueDateBase;
    	private String dSeasons;   
    	private String dMonths;    
    	private String dDays;      
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
