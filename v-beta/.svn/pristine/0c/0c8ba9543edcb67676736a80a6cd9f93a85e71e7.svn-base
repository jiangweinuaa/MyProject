package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

import lombok.Getter;
import lombok.Setter;

/**
 * POrderGet 專用的 response json
 * @author panjing
 * @since  2016-10-8
 */

public class DCP_POrderQueryRes extends JsonRes{
    
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

        public String getuTotDistriAmt() {
            return uTotDistriAmt;
        }
        public void setuTotDistriAmt(String uTotDistriAmt) {
            this.uTotDistriAmt = uTotDistriAmt;
        }
        public String getProType() {
            return proType;
        }
        public void setProType(String proType) {
            this.proType = proType;
        }
        public String getpTemplateName() {
            return pTemplateName;
        }
        public void setpTemplateName(String pTemplateName) {
            this.pTemplateName = pTemplateName;
        }
        public String getCreateBy() {
            return createBy;
        }
        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }
        public String getCreateDate() {
            return createDate;
        }
        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }
        public String getCreateTime() {
            return createTime;
        }
        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
        public String getSubmitBy() {
            return submitBy;
        }
        public void setSubmitBy(String submitBy) {
            this.submitBy = submitBy;
        }
        public String getSubmitDate() {
            return submitDate;
        }
        public void setSubmitDate(String submitDate) {
            this.submitDate = submitDate;
        }
        public String getSubmitTime() {
            return submitTime;
        }
        public void setSubmitTime(String submitTime) {
            this.submitTime = submitTime;
        }
        public String getModifyBy() {
            return modifyBy;
        }
        public void setModifyBy(String modifyBy) {
            this.modifyBy = modifyBy;
        }
        public String getModifyDate() {
            return modifyDate;
        }
        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }
        public String getModifyTime() {
            return modifyTime;
        }
        public void setModifyTime(String modifyTime) {
            this.modifyTime = modifyTime;
        }
        public String getConfirmBy() {
            return confirmBy;
        }
        public void setConfirmBy(String confirmBy) {
            this.confirmBy = confirmBy;
        }
        public String getConfirmDate() {
            return confirmDate;
        }
        public void setConfirmDate(String confirmDate) {
            this.confirmDate = confirmDate;
        }
        public String getConfirmTime() {
            return confirmTime;
        }
        public void setConfirmTime(String confirmTime) {
            this.confirmTime = confirmTime;
        }
        public String getCancelBy() {
            return cancelBy;
        }
        public void setCancelBy(String cancelBy) {
            this.cancelBy = cancelBy;
        }
        public String getCancelDate() {
            return cancelDate;
        }
        public void setCancelDate(String cancelDate) {
            this.cancelDate = cancelDate;
        }
        public String getCancelTime() {
            return cancelTime;
        }
        public void setCancelTime(String cancelTime) {
            this.cancelTime = cancelTime;
        }
        public String getProcessERPNo() {
            return processERPNo;
        }
        public void setProcessERPNo(String processERPNo) {
            this.processERPNo = processERPNo;
        }
        public String getbDate() {
            return bDate;
        }
        public void setbDate(String bDate) {
            this.bDate = bDate;
        }
        public String getIsAdd() {
            return isAdd;
        }
        public void setIsAdd(String isAdd) {
            this.isAdd = isAdd;
        }
        public String getMemo() {
            return memo;
        }
        public void setMemo(String memo) {
            this.memo = memo;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getCreateByName() {
            return createByName;
        }
        public void setCreateByName(String createByName) {
            this.createByName = createByName;
        }
        public String getrDate() {
            return rDate;
        }
        public void setrDate(String rDate) {
            this.rDate = rDate;
        }
        public String getrTime() {
            return rTime;
        }
        public void setrTime(String rTime) {
            this.rTime = rTime;
        }
        public String getTotPqty() {
            return totPqty;
        }
        public void setTotPqty(String totPqty) {
            this.totPqty = totPqty;
        }
        public String getTotAmt() {
            return totAmt;
        }
        public void setTotAmt(String totAmt) {
            this.totAmt = totAmt;
        }
        public String getTotCqty() {
            return totCqty;
        }
        public void setTotCqty(String totCqty) {
            this.totCqty = totCqty;
        }
        public void setPreDay(String preDay) {
            this.preDay = preDay;
        }
        public String getPreDay() {
            return preDay;
        }
        public List<level2Elm> getDatas() {
            return datas;
        }
        public void setDatas(List<level2Elm> datas) {
            this.datas = datas;
        }
        public double getPRedictAMT() {
            return PRedictAMT;
        }
        public void setPRedictAMT(double pRedictAMT) {
            PRedictAMT = pRedictAMT;
        }
        public String getBeginDate() {
            return BeginDate;
        }
        public void setBeginDate(String beginDate) {
            BeginDate = beginDate;
        }
        public double getAvgsaleAMT() {
            return avgsaleAMT;
        }
        public void setAvgsaleAMT(double avgsaleAMT) {
            this.avgsaleAMT = avgsaleAMT;
        }
        public double getModifRatio() {
            return modifRatio;
        }
        public void setModifRatio(double modifRatio) {
            this.modifRatio = modifRatio;
        }
        public String getEndDate() {
            return EndDate;
        }
        public void setEndDate(String endDate) {
            EndDate = endDate;
        }
        public String getISPRedict() {
            return ISPRedict;
        }
        public void setISPRedict(String iSPRedict) {
            ISPRedict = iSPRedict;
        }
        public String getIsForecast() {
            return isForecast;
        }
        public void setIsForecast(String isForecast) {
            this.isForecast = isForecast;
        }
        public String getOfNo() {
            return ofNo;
        }
        public void setOfNo(String ofNo) {
            this.ofNo = ofNo;
        }
        public String getOptionalTime() {
            return optionalTime;
        }
        public void setOptionalTime(String optionalTime) {
            this.optionalTime = optionalTime;
        }
        public String getUpdate_time() {
            return update_time;
        }
        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }
        public String getProcess_status() {
            return process_status;
        }
        public void setProcess_status(String process_status) {
            this.process_status = process_status;
        }
        public String getIsUrgentOrder() {
            return isUrgentOrder;
        }
        public void setIsUrgentOrder(String isUrgentOrder) {
            this.isUrgentOrder = isUrgentOrder;
        }
        public String getModifyByName() {
            return modifyByName;
        }
        public void setModifyByName(String modifyByName) {
            this.modifyByName = modifyByName;
        }
        public String getSubmitByName() {
            return submitByName;
        }
        public void setSubmitByName(String submitByName) {
            this.submitByName = submitByName;
        }
        public String getConfirmByName() {
            return confirmByName;
        }
        public void setConfirmByName(String confirmByName) {
            this.confirmByName = confirmByName;
        }
        public String getCancelByName() {
            return cancelByName;
        }
        public void setCancelByName(String cancelByName) {
            this.cancelByName = cancelByName;
        }
        public String getAccountBy() {
            return accountBy;
        }
        public void setAccountBy(String accountBy) {
            this.accountBy = accountBy;
        }
        public String getAccountByName() {
            return accountByName;
        }
        public void setAccountByName(String accountByName) {
            this.accountByName = accountByName;
        }
        public String getAccountDate() {
            return accountDate;
        }
        public void setAccountDate(String accountDate) {
            this.accountDate = accountDate;
        }
        public String getAccountTime() {
            return accountTime;
        }
        public void setAccountTime(String accountTime) {
            this.accountTime = accountTime;
        }
        public String getCal_type() {
            return cal_type;
        }
        public void setCal_type(String cal_type) {
            this.cal_type = cal_type;
        }
        public String getMateral_type() {
            return materal_type;
        }
        public void setMateral_type(String materal_type) {
            this.materal_type = materal_type;
        }
        public String getReason() {
            return reason;
        }
        public void setReason(String reason) {
            this.reason = reason;
        }
        public String getTotDistriAmt() {
            return totDistriAmt;
        }
        public void setTotDistriAmt(String totDistriAmt) {
            this.totDistriAmt = totDistriAmt;
        }
        public String getRdate_Type() {
            return rdate_Type;
        }
        public void setRdate_Type(String rdate_Type) {
            this.rdate_Type = rdate_Type;
        }
        public String getRdate_Add() {
            return rdate_Add;
        }
        public void setRdate_Add(String rdate_Add) {
            this.rdate_Add = rdate_Add;
        }
        public String getRdate_Values() {
            return rdate_Values;
        }
        public void setRdate_Values(String rdate_Values) {
            this.rdate_Values = rdate_Values;
        }
        public String getRevoke_Day() {
            return revoke_Day;
        }
        public void setRevoke_Day(String revoke_Day) {
            this.revoke_Day = revoke_Day;
        }
        public String getRevoke_Time() {
            return revoke_Time;
        }
        public void setRevoke_Time(String revoke_Time) {
            this.revoke_Time = revoke_Time;
        }
        public String getRdate_Times() {
            return rdate_Times;
        }
        public void setRdate_Times(String rdate_Times) {
            this.rdate_Times = rdate_Times;
        }
        public String getIsAddGoods() {
            return isAddGoods;
        }
        public void setIsAddGoods(String isAddGoods) {
            this.isAddGoods = isAddGoods;
        }
        public String getIsShowHeadStockQty() {
            return isShowHeadStockQty;
        }
        public void setIsShowHeadStockQty(String isShowHeadStockQty) {
            this.isShowHeadStockQty = isShowHeadStockQty;
        }
        public String getPorderNo() {
            return porderNo;
        }
        public void setPorderNo(String porderNo) {
            this.porderNo = porderNo;
        }
        public String getpTemplateNo() {
            return pTemplateNo;
        }
        public void setpTemplateNo(String pTemplateNo) {
            this.pTemplateNo = pTemplateNo;
        }
        public String getReceiptOrgNo() {
            return receiptOrgNo;
        }
        public void setReceiptOrgNo(String receiptOrgNo) {
            this.receiptOrgNo = receiptOrgNo;
        }
        public String getoType() {
            return oType;
        }
        public void setoType(String oType) {
            this.oType = oType;
        }
        public String getStockCare() {
            return stockCare;
        }
        public void setStockCare(String stockCare) {
            this.stockCare = stockCare;
        }
        public String getNotRepeatGoods() {
            return notRepeatGoods;
        }
        public void setNotRepeatGoods(String notRepeatGoods) {
            this.notRepeatGoods = notRepeatGoods;
        }
        
        
        
    }
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
        
        public String getuDistriPrice() {
            return uDistriPrice;
        }
        public void setuDistriPrice(String uDistriPrice) {
            this.uDistriPrice = uDistriPrice;
        }
        public int getItem() {
            return item;
        }
        public void setItem(int item) {
            this.item = item;
        }
        public String getPluNo() {
            return pluNo;
        }
        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }
        public String getGroupNo() {
            return groupNo;
        }
        public void setGroupNo(String groupNo) {
            this.groupNo = groupNo;
        }
        public String getPluName() {
            return pluName;
        }
        public void setPluName(String pluName) {
            this.pluName = pluName;
        }
        public String getPunit() {
            return punit;
        }
        public void setPunit(String punit) {
            this.punit = punit;
        }
        public String getPunitName() {
            return punitName;
        }
        public void setPunitName(String punitName) {
            this.punitName = punitName;
        }
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
        public String getAmt() {
            return amt;
        }
        public void setAmt(String amt) {
            this.amt = amt;
        }
        public String getDetailStatus() {
            return detailStatus;
        }
        public void setDetailStatus(String detailStatus) {
            this.detailStatus = detailStatus;
        }
        public float getMaxQty() {
            return maxQty;
        }
        public void setMaxQty(float maxQty) {
            this.maxQty = maxQty;
        }
        public float getMinQty() {
            return minQty;
        }
        public void setMinQty(float minQty) {
            this.minQty = minQty;
        }
        public float getMulQty() {
            return mulQty;
        }
        public void setMulQty(float mulQty) {
            this.mulQty = mulQty;
        }
        public float getRefSQty() {
            return refSQty;
        }
        public void setRefSQty(float refSQty) {
            this.refSQty = refSQty;
        }
        public float getRefWQty() {
            return refWQty;
        }
        public void setRefWQty(float refWQty) {
            this.refWQty = refWQty;
        }
        public float getRefPQty() {
            return refPQty;
        }
        public void setRefPQty(float refPQty) {
            this.refPQty = refPQty;
        }
        public float getSoQty() {
            return soQty;
        }
        public void setSoQty(float soQty) {
            this.soQty = soQty;
        }
        public String getSpec() {
            return spec;
        }
        public void setSpec(String spec) {
            this.spec = spec;
        }
        public String getMemo() {
            return memo;
        }
        public void setMemo(String memo) {
            this.memo = memo;
        }
        public String getListImage() {
            return listImage;
        }
        public void setListImage(String listImage) {
            this.listImage = listImage;
        }
        public float getPropQty() {
            return propQty;
        }
        public void setPropQty(float propQty) {
            this.propQty = propQty;
        }
        public float getkQty() {
            return kQty;
        }
        public void setkQty(float kQty) {
            this.kQty = kQty;
        }
        public float getkAdjQty() {
            return kAdjQty;
        }
        public void setkAdjQty(float kAdjQty) {
            this.kAdjQty = kAdjQty;
        }
        public float getPropAdjQty() {
            return propAdjQty;
        }
        public void setPropAdjQty(float propAdjQty) {
            this.propAdjQty = propAdjQty;
        }
        public String getBaseUnit() {
            return baseUnit;
        }
        public void setBaseUnit(String baseUnit) {
            this.baseUnit = baseUnit;
        }
        public String getBaseUnitName() {
            return baseUnitName;
        }
        public void setBaseUnitName(String baseUnitName) {
            this.baseUnitName = baseUnitName;
        }
        public String getPqty() {
            return pqty;
        }
        public void setPqty(String pqty) {
            this.pqty = pqty;
        }
        public String getStockInqty() {
            return stockInqty;
        }
        public void setStockInqty(String stockInqty) {
            this.stockInqty = stockInqty;
        }
        public String getUnitRatio() {
            return unitRatio;
        }
        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
        }
        public String getBaseQty() {
            return baseQty;
        }
        public void setBaseQty(String baseQty) {
            this.baseQty = baseQty;
        }
        public String getFeatureNo() {
            return featureNo;
        }
        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }
        public String getDistriPrice() {
            return distriPrice;
        }
        public void setDistriPrice(String distriPrice) {
            this.distriPrice = distriPrice;
        }
        public String getDistriAmt() {
            return distriAmt;
        }
        public void setDistriAmt(String distriAmt) {
            this.distriAmt = distriAmt;
        }
        public String getPunitUdLength() {
            return punitUdLength;
        }
        public void setPunitUdLength(String punitUdLength) {
            this.punitUdLength = punitUdLength;
        }
        public String getGroupType() {
            return groupType;
        }
        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }
        public String getGroupReachCount() {
            return groupReachCount;
        }
        public void setGroupReachCount(String groupReachCount) {
            this.groupReachCount = groupReachCount;
        }
        public String getReview_Qty() {
            return review_Qty;
        }
        public void setReview_Qty(String review_Qty) {
            this.review_Qty = review_Qty;
        }
        public String getHeadStockQty() {
            return headStockQty;
        }
        public void setHeadStockQty(String headStockQty) {
            this.headStockQty = headStockQty;
        }
        public String getFeatureName() {
            return featureName;
        }
        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }
        public String getMaxOrderSpec()
        {
            return maxOrderSpec;
        }
        public void setMaxOrderSpec(String maxOrderSpec)
        {
            this.maxOrderSpec = maxOrderSpec;
        }
        public String getIsNewGoods() {
            return isNewGoods;
        }
        public void setIsNewGoods(String isNewGoods) {
            this.isNewGoods = isNewGoods;
        }
        public String getIsHotGoods() {
            return isHotGoods;
        }
        public void setIsHotGoods(String isHotGoods) {
            this.isHotGoods = isHotGoods;
        }
        public String getCanRequire() {
            return canRequire;
        }
        public void setCanRequire(String canRequire) {
            this.canRequire = canRequire;
        }
        public String getBaseUnitUdLength() {
            return baseUnitUdLength;
        }
        public void setBaseUnitUdLength(String baseUnitUdLength) {
            this.baseUnitUdLength = baseUnitUdLength;
        }
        public String getWarningQty() {
            return warningQty;
        }
        public void setWarningQty(String warningQty) {
            this.warningQty = warningQty;
        }
    }
    
}
