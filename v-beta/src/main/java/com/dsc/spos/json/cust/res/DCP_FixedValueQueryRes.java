package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_FixedValueQueryRes extends JsonRes {

    private List<Level1Elm> datas;

    public List<Level1Elm> getDatas() {
        return datas;
    }

    public void setDatas(List<Level1Elm> datas) {
        this.datas = datas;
    }

    public static class Level1Elm {
        private String valueId;
        private String valueName;

        public String getValueId() {
            return valueId;
        }

        public void setValueId(String valueId) {
            this.valueId = valueId;
        }

        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }
    }
}
