package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;


/*
 * 服务函数：DCP_SubStockTakeRangeQuery
 * 服务说明：盘点子任务范围查询
 * @author jinzma
 * @since  2021-03-08
 */
public class DCP_SubStockTakeRangeQueryRes extends JsonRes {
    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private List<level2Elm> pluList;

        public List<level2Elm> getPluList() {
            return pluList;
        }

        public void setPluList(List<level2Elm> pluList) {
            this.pluList = pluList;
        }
    }
    public class level2Elm{
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String punit;
        private String punitName;
        private String status;
        private String listImage;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getListImage() {
            return listImage;
        }

        public void setListImage(String listImage) {
            this.listImage = listImage;
        }
    }
}
