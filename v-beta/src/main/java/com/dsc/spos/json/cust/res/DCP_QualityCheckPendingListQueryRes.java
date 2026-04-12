package com.dsc.spos.json.cust.res;

 
import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

import lombok.Getter;
import lombok.Setter;

/**
 *  
 * @author: 01029
 * @create: 2024-09-15
 */
@Getter
@Setter
public class DCP_QualityCheckPendingListQueryRes extends JsonRes {
	@Getter
    @Setter
    private List<DataDetail> datas;

    @Getter
    @Setter
    public  class DataDetail {

    	private String souceBillNo;
    	private String oItem;
    	private String oItem2;
    	private String listImage;
    	private String pluNo;
    	private String pluName;
    	private String spec;
    	private String pluBarcode;
    	private String featureNo;
    	private String featureName;
    	private String oUnit;
    	private String oUnitName;
    	private String oQty;
    	private String supplier;
    	private String batchNo;
    	private String prodDate;
    	private String expDate;
    	private String bDate;

   
    }
}
