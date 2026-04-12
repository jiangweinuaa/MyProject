package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import java.util.List;

/**
 * @apiNote 商品配送收货查询
 * @since 2021-04-20
 * @author jinzma
 */
public class DCP_GoodsStockInQueryRes extends JsonRes {
    private levelElm datas;

    public levelElm getDatas() {
        return datas;
    }

    public void setDatas(levelElm datas) {
        this.datas = datas;
    }

    public class levelElm{
        private List<level1Elm> pluList;

        public List<level1Elm> getPluList() {
            return pluList;
        }

        public void setPluList(List<level1Elm> pluList) {
            this.pluList = pluList;
        }
    }
    public class level1Elm{
        private String item;
        private String bDate;
        private String receivingNo;
        private String load_docNo;
        private String packingNo;
        private String stockInNo;
        private String stockInStatus;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String spec;
        private String listImage;
        private String punit;
        private String punitName;
        private String baseUnit;
        private String baseUnitName;
        private String punitUdLength;
        private String unitRatio;
        private String price;
        private String distriPrice;
        private String pqty;
        private String poQty;
        private String stockInQty;
        private String baseQty;
        private String status;
        private String baseUnitUdLength;

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getbDate() {
            return bDate;
        }

        public void setbDate(String bDate) {
            this.bDate = bDate;
        }

        public String getReceivingNo() {
            return receivingNo;
        }

        public void setReceivingNo(String receivingNo) {
            this.receivingNo = receivingNo;
        }

        public String getLoad_docNo() {
            return load_docNo;
        }

        public void setLoad_docNo(String load_docNo) {
            this.load_docNo = load_docNo;
        }

        public String getPackingNo() {
            return packingNo;
        }

        public void setPackingNo(String packingNo) {
            this.packingNo = packingNo;
        }

        public String getStockInNo() {
            return stockInNo;
        }

        public void setStockInNo(String stockInNo) {
            this.stockInNo = stockInNo;
        }

        public String getStockInStatus() {
            return stockInStatus;
        }

        public void setStockInStatus(String stockInStatus) {
            this.stockInStatus = stockInStatus;
        }

        public String getPluNo() {
            return pluNo;
        }

        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }

        public String getPluName() {
            return pluName;
        }

        public void setPluName(String pluName) {
            this.pluName = pluName;
        }

        public String getFeatureNo() {
            return featureNo;
        }

        public void setFeatureNo(String featureNo) {
            this.featureNo = featureNo;
        }

        public String getFeatureName() {
            return featureName;
        }

        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public String getListImage() {
            return listImage;
        }

        public void setListImage(String listImage) {
            this.listImage = listImage;
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

        public String getPunitUdLength() {
            return punitUdLength;
        }

        public void setPunitUdLength(String punitUdLength) {
            this.punitUdLength = punitUdLength;
        }

        public String getUnitRatio() {
            return unitRatio;
        }

        public void setUnitRatio(String unitRatio) {
            this.unitRatio = unitRatio;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDistriPrice() {
            return distriPrice;
        }

        public void setDistriPrice(String distriPrice) {
            this.distriPrice = distriPrice;
        }

        public String getPqty() {
            return pqty;
        }

        public void setPqty(String pqty) {
            this.pqty = pqty;
        }

        public String getPoQty() {
            return poQty;
        }

        public void setPoQty(String poQty) {
            this.poQty = poQty;
        }

        public String getStockInQty() {
            return stockInQty;
        }

        public void setStockInQty(String stockInQty) {
            this.stockInQty = stockInQty;
        }

        public String getBaseQty() {
            return baseQty;
        }

        public void setBaseQty(String baseQty) {
            this.baseQty = baseQty;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    
        public String getBaseUnitUdLength() {
            return baseUnitUdLength;
        }
    
        public void setBaseUnitUdLength(String baseUnitUdLength) {
            this.baseUnitUdLength = baseUnitUdLength;
        }
    }
}
