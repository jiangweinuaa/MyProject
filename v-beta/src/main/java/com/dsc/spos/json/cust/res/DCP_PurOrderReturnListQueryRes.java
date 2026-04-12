package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_PurOrderReturnListQueryRes extends JsonRes {

    @Getter
    @Setter
    private List<PayDate> datas;

    @Getter
    @Setter
    public  class PayDate {

    	private String purOrderNo;
    	private String bDate;
    	private String item;
    	private String listImage;
    	private String pluNo;
    	private String pluName;
    	private String spec;
    	private String pluBarcode;
    	private String featureNo;
    	private String featureName;
    	private String purUnit;
    	private String purUnitName;
    	private String purQty;
    	private String stockInQty;
    	private String returnQty;
    	private String canReturnQty;
		private String purPrice;
    	private String refPurPrice;
    	private String taxCode;
    	private String taxRate;
    	private String inclTax;
    	private String baseUnit;
    	private String wUnit;

    	private String baseUnitName;
    	private String unitRatio;
    	private String baseUnitUdLength;
    	private String taxCalType;
    	
    	private String purTemplateNo;

        
    }

    

 
}
