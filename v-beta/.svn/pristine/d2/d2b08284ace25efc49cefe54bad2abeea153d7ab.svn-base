package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Generated;
import java.util.List;

/**
 * 结算日期
 * @date   2024-09-19
 * @author 01029 
 */

@Getter
@Setter
public class DCP_BillDateAlterReq extends JsonBasicReq {

    private levelRequest request;

 

    @Getter
    @Setter
    public class levelRequest {
    	
    	private String oprType;     
    	private String billDateType;
    	private String billDateNo;  
    	private String fDate;       
    	private double addMonths;   
    	private double addDays;     
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
