package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

import lombok.Getter;
import lombok.Setter;
 
@Getter
	@Setter
public class DCP_POrderDetailQueryRes extends JsonRes{
    
    private String sysDate;
    private List<level1Elm> datas;
    
    
    public String getSysDate() {
        return sysDate;
    }
    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }
    public List<level1Elm> getDatas() {
        return datas;
    }
    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }
    
    @Getter
	@Setter
    public class level1Elm{
        private String porderNo;
        private String processERPNo;
        private String bDate;
        private String pTemplateNo;
        private String pTemplateName;
        private String isAdd;
        private String memo;
        private String status;
        private String rDate;
        private String rTime;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        
        private String uTotDistriAmt; // 原进货金额合计
        
        private String preDay;
        private double PRedictAMT; //千元用量预估金额
        private String BeginDate;  //千元用量预估开始时间
        private String EndDate;    //千元用量预估结束时间
        private double avgsaleAMT; //千元用量平均销售额
        private double modifRatio; //千元用量调整系数
        private String ISPRedict;  //是否千元用量
        private String isForecast; // 是否营业预估
        private String ofNo;//来源单号
        private String proType;    //预估方式 1:营业额(BOM耗用预估) 2:盘点(盘差耗用预估)
        private String cal_type;   //1:预估量=计算量(千元用量)-库存量-未到货量 2:预估量=计算量(千元用量)
        private String materal_type; //1:销售量BOM推算 2:盘点损耗
        private String optionalTime;
        private String update_time;
        private String process_status;
        private String isUrgentOrder;
        private String createBy;
        private String createByName;
        private String createDate;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyDate;
        private String modifyTime;
        private String submitBy;
        private String submitByName;
        private String submitDate;
        private String submitTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmDate;
        private String confirmTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelDate;
        private String cancelTime;
        private String accountBy;
        private String accountByName;
        private String accountDate;
        private String accountTime;
        private String reason;
        private String rdate_Type;
        private String rdate_Add;
        private String rdate_Values;
        private String revoke_Day;
        private String revoke_Time;
        private String rdate_Times;
        private String isAddGoods;
        private String isShowHeadStockQty;
        private String receiptOrgNo;
        private List<level2Elm> datas;
        //2020-05-09 SA孙红艳 增加以下字段
        private String oType;
        private String stockCare;
        private String notRepeatGoods;
   
        private String receiptOrgName;
        private String employeeID;
        private String employeeName;
        private String departID;
        private String departName;
        private String ownOpID;
        private String ownOpName;
        private String ownDeptID;
        private String ownDeptName;
        private String createDeptID;
        private String createDeptName;
        private String supplierType;
        private String organizationNo;
        private String organizationName;

    }
    @Getter
  	@Setter
    public class level2Elm{
        private int item;
        private String pluNo;
        private String pluName;
        private String punit;
        private String punitName;
        private String baseUnit;
        private String baseUnitName;
        private String pqty;
        private String price;
        private String distriPrice;
        private String uDistriPrice; // 原进货价
        private String amt;
        private String distriAmt;
        private String stockInqty;
        private String detailStatus;
        private float maxQty;
        private float minQty;
        private float mulQty;
        private float refSQty;
        private float refWQty;
        private float refPQty;
        private float soQty;
        private String spec;
        private String memo;
        private float propQty;
        private String listImage;
        private float kQty;
        private float kAdjQty;
        private float propAdjQty;
        private String unitRatio;
        private String baseQty;
        private String punitUdLength;
        private String groupNo;//要货商品组别编号
        private String groupType;//组别类型
        private String groupReachCount;//达成条件
        private String review_Qty;//ERP已审核量
        private String headStockQty;
        private String featureNo;
        private String featureName;
        private String maxOrderSpec;
        private String isNewGoods;
        private String isHotGoods;
        private String canRequire;
        private String baseUnitUdLength;
        private String warningQty;
        private String supplierType;
        private String supplierId;
        private String supplierName;
        private String noQty;
        private String reason;
        
    }
    
}
