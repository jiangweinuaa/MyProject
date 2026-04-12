package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_MenuQueryRes extends JsonRes {

    private List<DCP_MenuQueryRes.level1Elm> datas ;

    public List<level1Elm> getDatas() {
        return datas;
    }

    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }

    public class level1Elm{

        private String modularNo;
        private String modularName;
        private String modularNameCht;
        private String modularNameEng;
        private String modularLevel;
        private String upperModular;
        private String upModularName;
        private String sType;
        private String fType;
        private String proName;
        private String parameter;
        private String status;
        //2018-11-07  yyy 添加priority字段，用于上下移动排序
        private String priority;
        private String ownership;  //0:普通用户 1:管理员  2:开发者
        private String Insert_inner;
        private String Modify_inner;
        private String Delete_inner;
        private String RFUNCNO;
        private String RPATTERN;
        private String REGEDISTREX;
        private String isReport;
        private String onSale;
        private List<DCP_MenuQueryRes.function> function ;

        private List<DCP_MenuQueryRes.level1Elm> children;


        public String getOnSale() {
            return onSale;
        }

        public void setOnSale(String onSale) {
            this.onSale = onSale;
        }

        public String getModularNameCht() {
            return modularNameCht;
        }

        public void setModularNameCht(String modularNameCht) {
            this.modularNameCht = modularNameCht;
        }

        public String getModularNameEng() {
            return modularNameEng;
        }

        public void setModularNameEng(String modularNameEng) {
            this.modularNameEng = modularNameEng;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public List<DCP_MenuQueryRes.function> getFunction() {
            return function;
        }

        public void setFunction(List<DCP_MenuQueryRes.function> function) {
            this.function = function;
        }

        public List<DCP_MenuQueryRes.level1Elm> getChildren() {
            return children;
        }

        public void setChildren(List<DCP_MenuQueryRes.level1Elm> children) {
            this.children = children;
        }

        public String getRFUNCNO() {
            return RFUNCNO;
        }

        public void setRFUNCNO(String rFUNCNO) {
            RFUNCNO = rFUNCNO;
        }

        public String getRPATTERN() {
            return RPATTERN;
        }

        public void setRPATTERN(String rPATTERN) {
            RPATTERN = rPATTERN;
        }

        public String getREGEDISTREX() {
            return REGEDISTREX;
        }

        public void setREGEDISTREX(String rEGEDISTREX) {
            REGEDISTREX = rEGEDISTREX;
        }

        public String getIsReport() {
            return isReport;
        }

        public void setIsReport(String isReport) {
            this.isReport = isReport;
        }

        public String getModularNo() {
            return modularNo;
        }

        public void setModularNo(String modularNo) {
            this.modularNo = modularNo;
        }

        public String getModularName() {
            return modularName;
        }

        public void setModularName(String modularName) {
            this.modularName = modularName;
        }

        public String getModularLevel() {
            return modularLevel;
        }

        public void setModularLevel(String modularLevel) {
            this.modularLevel = modularLevel;
        }

        public String getUpperModular() {
            return upperModular;
        }

        public void setUpperModular(String upperModular) {
            this.upperModular = upperModular;
        }

        public String getUpModularName() {
            return upModularName;
        }

        public void setUpModularName(String upModularName) {
            this.upModularName = upModularName;
        }

        public String getsType() {
            return sType;
        }

        public void setsType(String sType) {
            this.sType = sType;
        }

        public String getfType() {
            return fType;
        }

        public void setfType(String fType) {
            this.fType = fType;
        }

        public String getProName() {
            return proName;
        }

        public void setProName(String proName) {
            this.proName = proName;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOwnership() {
            return ownership;
        }

        public void setOwnership(String ownership) {
            this.ownership = ownership;
        }

        public String getInsert_inner() {
            return Insert_inner;
        }

        public void setInsert_inner(String insert_inner) {
            Insert_inner = insert_inner;
        }

        public String getModify_inner() {
            return Modify_inner;
        }

        public void setModify_inner(String modify_inner) {
            Modify_inner = modify_inner;
        }

        public String getDelete_inner() {
            return Delete_inner;
        }

        public void setDelete_inner(String delete_inner) {
            Delete_inner = delete_inner;
        }
    }

    public class function{

        private String funcNo;
        private String funcName;
        private String funcNameCht;
        private String funcNameEng;
        private String funcFType;
        private String funcProName;
        private String funcParameter;
        private String funcStatus;
        private String onSale;

        public String getFuncNo() {
            return funcNo;
        }
        public void setFuncNo(String funcNo) {
            this.funcNo = funcNo;
        }
        public String getFuncNameCht() {
            return funcNameCht;
        }
        public void setFuncNameCht(String funcNameCht) {
            this.funcNameCht = funcNameCht;
        }
        public String getFuncNameEng() {
            return funcNameEng;
        }
        public void setFuncNameEng(String funcNameEng) {
            this.funcNameEng = funcNameEng;
        }
        public String getOnSale() {
            return onSale;
        }
        public void setOnSale(String onSale) {
            this.onSale = onSale;
        }
        public String getFuncName() {
            return funcName;
        }
        public void setFuncName(String funcName) {
            this.funcName = funcName;
        }
        public String getFuncFType() {
            return funcFType;
        }
        public void setFuncFType(String funcFType) {
            this.funcFType = funcFType;
        }
        public String getFuncProName() {
            return funcProName;
        }
        public void setFuncProName(String funcProName) {
            this.funcProName = funcProName;
        }
        public String getFuncParameter() {
            return funcParameter;
        }
        public void setFuncParameter(String funcParameter) {
            this.funcParameter = funcParameter;
        }
        public String getFuncStatus() {
            return funcStatus;
        }
        public void setFuncStatus(String funcStatus) {
            this.funcStatus = funcStatus;
        }


    }
}
