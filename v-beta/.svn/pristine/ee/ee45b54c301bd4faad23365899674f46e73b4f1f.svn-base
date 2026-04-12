package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Generated;
import java.util.List;

/**
 * 收付款条件
 * @date   2024-09-23
 * @author 01029 
 */

@Getter
@Setter
public class DCP_PayDateAlterReq extends JsonBasicReq {

    private levelRequest request;

 

    @Getter
    @Setter
    public class levelRequest {
    	
    	private String oprType;    
    	private String payDateType;
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

        private List<NameLang> name_Lang;

    }

    @Getter
    @Setter
    public static class NameLang {
        private String langType;
        private String name;
    }

 

}
