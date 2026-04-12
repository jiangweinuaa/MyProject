package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_PackageBySubProQuery_OpenRes extends JsonRes {
    private levelDatas datas;

    public levelDatas getDatas() {
        return datas;
    }

    public void setDatas(levelDatas datas) {
        this.datas = datas;
    }

    public class levelDatas
    {
        private List<level1Elm> goodList;

        public List<level1Elm> getGoodList() {
            return goodList;
        }

        public void setGoodList(List<level1Elm> goodList) {
            this.goodList = goodList;
        }
    }

    public class level1Elm
    {
        private String packageItemNo;
        private List<level2Elm> pluNoList;

        public String getPackageItemNo() {
            return packageItemNo;
        }

        public void setPackageItemNo(String packageItemNo) {
            this.packageItemNo = packageItemNo;
        }

        public List<level2Elm> getPluNoList() {
            return pluNoList;
        }

        public void setPluNoList(List<level2Elm> pluNoList) {
            this.pluNoList = pluNoList;
        }
    }

    public class level2Elm
    {
        private String pluNo;
        private String pluName;
        private String unit;
        private String unitName;
        private String conversionRatio;

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

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public String getConversionRatio() {
            return conversionRatio;
        }

        public void setConversionRatio(String conversionRatio) {
            this.conversionRatio = conversionRatio;
        }
    }
}
