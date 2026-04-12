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
public class DCP_QualityCheckProQueryRes extends JsonRes {
	@Getter
    @Setter
    private List<DataDetail> datas;

    @Getter
    @Setter
    public  class DataDetail {

    	private List<QCDetail> proList; 

   
    }
    @Getter
    @Setter
    public  class QCDetail {

    	private String qcType;
    	private String pendingCqty;

   
    }
}
