package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import java.util.List;

/*
 * 服务函数：DCP_SubStockTakeLoadProcess
 * 服务说明：盘点子任务导入
 * @author jinzma
 * @since  2021-03-09
 */
public class DCP_SubStockTakeLoadProcessRes extends JsonRes {
    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private List<level2Elm> subStockTakeList ;

        public List<level2Elm> getSubStockTakeList() {
            return subStockTakeList;
        }

        public void setSubStockTakeList(List<level2Elm> subStockTakeList) {
            this.subStockTakeList = subStockTakeList;
        }
    }

    public class level2Elm{
        private String subStockTakeNo;

        public String getSubStockTakeNo() {
            return subStockTakeNo;
        }

        public void setSubStockTakeNo(String subStockTakeNo) {
            this.subStockTakeNo = subStockTakeNo;
        }
    }
}
