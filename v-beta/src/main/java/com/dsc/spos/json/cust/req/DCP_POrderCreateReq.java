package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_SupLicenseApplyCreateReq.Detail1;

import lombok.Getter;
import lombok.Setter;

/**
 * 服務函數：POrderCreate
 *   說明：要货单保存
 * 服务说明：要货单保存
 * @author panjing
 * @since  2016-10-08
 */

public class DCP_POrderCreateReq extends JsonBasicReq{
    
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    @Getter
	@Setter
    public class levelElm{
        private String bDate;
        private String memo;
        private String porderID;
        private String rDate;
        private String rTime;
        private String pTemplateNo;
        private String isAdd;
        private String ISPRedict;
        private String isForecast;
        private String ofNo; //来源单号，用于记录计划报单号 pfNo
        private double PRedictAMT;
        private String BeginDate;
        private String EndDate;
        private double avgsaleAMT;
        private double modifRatio;
        private String ISUrgentOrder;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        private String uTotDistriAmt; // 原进货金额合计
        private String oType;
        private String isAppend; 
        private String supplierType;
        private String employeeID;
        private String departID;
        private String receiptOrgNo;

        private List<level1Elm> datas;
        
        public String getuTotDistriAmt() {
            return uTotDistriAmt;
        }
        public void setuTotDistriAmt(String uTotDistriAmt) {
            this.uTotDistriAmt = uTotDistriAmt;
        }
        public String getoType() {
            return oType;
        }
        public void setoType(String oType) {
            this.oType = oType;
        }
        public String getbDate() {
            return bDate;
        }
        public void setbDate(String bDate) {
            this.bDate = bDate;
        }
        public String getMemo() {
            return memo;
        }
        public void setMemo(String memo) {
            this.memo = memo;
        }
        public String getPorderID() {
            return porderID;
        }
        public void setPorderID(String porderID) {
            this.porderID = porderID;
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
        public String getpTemplateNo() {
            return pTemplateNo;
        }
        public void setpTemplateNo(String pTemplateNo) {
            this.pTemplateNo = pTemplateNo;
        }
        public String getIsAdd() {
            return isAdd;
        }
        public void setIsAdd(String isAdd) {
            this.isAdd = isAdd;
        }
        public List<level1Elm> getDatas() {
            return datas;
        }
        public void setDatas(List<level1Elm> datas) {
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
        public String getEndDate() {
            return EndDate;
        }
        public void setEndDate(String endDate) {
            EndDate = endDate;
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
        public String getISUrgentOrder() {
            return ISUrgentOrder;
        }
        public void setISUrgentOrder(String iSUrgentOrder) {
            ISUrgentOrder = iSUrgentOrder;
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
        public String getTotDistriAmt() {
            return totDistriAmt;
        }
        public void setTotDistriAmt(String totDistriAmt) {
            this.totDistriAmt = totDistriAmt;
        }
        public String getIsAppend() {
            return isAppend;
        }
        public void setIsAppend(String isAppend) {
            this.isAppend = isAppend;
        }
    }
    @Getter
	@Setter
    public class level1Elm {
        private String item;
        private String oItem;
        private String pluNo;
        private String punit;
        private String pqty;
        private String price;
        private String distriPrice;
        private String uDistriPrice; // 原进货价
        private String amt;
        private String maxQty;
        private String minQty;
        private String mulQty;
        private String refSQty;
        private String refWQty;
        private String refPQty;
        private String soQty;
        private double propQty;
        private String memo;
        private String kQty;
        private String kAdjQty;
        private String propAdjQty;
        private String distriAmt ;
        private String headStockQty;
        private String featureNo;
        private String baseUnit;
        private String baseQty;
        private String unitRatio;
        private String isNewGoods;
        private String isHotGoods;
        private String supplierType;
        private String supplierId;
        
        public String getkQty() {
            return kQty;
        }
        public void setkQty(String kQty) {
            this.kQty = kQty;
        }
        public String getkAdjQty() {
            return kAdjQty;
        }
        public void setkAdjQty(String kAdjQty) {
            this.kAdjQty = kAdjQty;
        }
        public String getPropAdjQty() {
            return propAdjQty;
        }
        public void setPropAdjQty(String propAdjQty) {
            this.propAdjQty = propAdjQty;
        }
        public String getItem() {
            return item;
        }
        public void setItem(String item) {
            this.item = item;
        }
        public String getoItem() {
            return oItem;
        }
        public void setoItem(String oItem) {
            this.oItem = oItem;
        }
        public String getPluNo() {
            return pluNo;
        }
        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }
        public String getPunit() {
            return punit;
        }
        public void setPunit(String punit) {
            this.punit = punit;
        }
        public String getPqty() {
            return pqty;
        }
        public void setPqty(String pqty) {
            this.pqty = pqty;
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
        public String getMaxQty() {
            return maxQty;
        }
        public void setMaxQty(String maxQty) {
            this.maxQty = maxQty;
        }
        public String getMinQty() {
            return minQty;
        }
        public void setMinQty(String minQty) {
            this.minQty = minQty;
        }
        public String getMulQty() {
            return mulQty;
        }
        public void setMulQty(String mulQty) {
            this.mulQty = mulQty;
        }
        public String getRefSQty() {
            return refSQty;
        }
        public void setRefSQty(String refSQty) {
            this.refSQty = refSQty;
        }
        public String getRefWQty() {
            return refWQty;
        }
        public void setRefWQty(String refWQty) {
            this.refWQty = refWQty;
        }
        public String getRefPQty() {
            return refPQty;
        }
        public void setRefPQty(String refPQty) {
            this.refPQty = refPQty;
        }
        public String getSoQty() {
            return soQty;
        }
        public void setSoQty(String soQty) {
            this.soQty = soQty;
        }
        public double getPropQty() {
            return propQty;
        }
        public void setPropQty(double propQty) {
            this.propQty = propQty;
        }
        public String getMemo() {
            return memo;
        }
        public void setMemo(String memo) {
            this.memo = memo;
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
        public String getHeadStockQty() {
            return headStockQty;
        }
        public void setHeadStockQty(String headStockQty) {
            this.headStockQty = headStockQty;
        }
        public String getFeatureNo() {
            return featureNo;
        }
        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }
        public String getBaseUnit() {
            return baseUnit;
        }
        public void setBaseUnit(String baseUnit) {
            this.baseUnit = baseUnit;
        }
        public String getBaseQty() {
            return baseQty;
        }
        public void setBaseQty(String baseQty) {
            this.baseQty = baseQty;
        }
        public String getUnitRatio() {
            return unitRatio;
        }
        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
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
        public String getuDistriPrice() {
            return uDistriPrice;
        }
        public void setuDistriPrice(String uDistriPrice) {
            this.uDistriPrice = uDistriPrice;
        }
        
    }
}
