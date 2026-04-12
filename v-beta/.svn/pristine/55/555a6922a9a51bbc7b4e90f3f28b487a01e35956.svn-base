package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SortingTaskQueryReq;
import com.dsc.spos.json.cust.res.DCP_SortingTaskQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.BigDecimalUtils;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_SortingTaskQuery extends SPosBasicService<DCP_SortingTaskQueryReq, DCP_SortingTaskQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_SortingTaskQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SortingTaskQueryReq> getRequestType() {
        return new TypeToken<DCP_SortingTaskQueryReq>() {
        };
    }

    @Override
    protected DCP_SortingTaskQueryRes getResponseType() {
        return new DCP_SortingTaskQueryRes();
    }

    @Override
    protected DCP_SortingTaskQueryRes processJson(DCP_SortingTaskQueryReq req) throws Exception {
        DCP_SortingTaskQueryRes res = this.getResponseType();
        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData =
                this.doQueryData(getQuerySql(req), null);
        res.setDatas(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            //总页数
            String num = getData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("OFNO", true);
            List<Map<String, Object>> taskNo = MapDistinct.getMap(getData, distinct);

            res.setDatas(new ArrayList<>());
            for (Map<String, Object> data : taskNo) {

                Map<String, Object> detailCondition = new HashMap<>();
                detailCondition.put("OFNO", data.get("OFNO"));
                List<Map<String, Object>> taskList = MapDistinct.getWhereMap(getData, detailCondition, true);

                DCP_SortingTaskQueryRes.Data oneData = res.new Data();
                oneData.setTaskList(new ArrayList<>());
                res.getDatas().add(oneData);

                oneData.setBillNo(data.get("BILLNO").toString());
                oneData.setBDate(data.get("BDATE").toString());

                oneData.setWarehouse(data.get("WAREHOUSE").toString());
                oneData.setWarehouseName(data.get("WAREHOUSE_NAME").toString());
                oneData.setStatus(data.get("STATUS").toString());

                for (Map<String, Object> task : taskList) {
                    DCP_SortingTaskQueryRes.TaskList oneTask = res.new TaskList();
                    oneData.getTaskList().add(oneTask);

                    oneTask.setTaskNo(task.get("TASKNO").toString());
//                    oneTask.setRequireType(task.get("REQUIRETYPE").toString());
//                    oneTask.setRequireNo(task.get("REQUIRENO").toString());
//                    oneTask.setRequireName(task.get("REQUIRENAME").toString());
                    oneTask.setRDate(task.get("RDATE").toString());
                    oneTask.setStatus(task.get("MSTATUS").toString());
                    oneTask.setIsStockOut(task.get("ISSTOCKOUT").toString());
                    oneTask.setWarehouse(task.get("MWAREHOUSENO").toString());
                    oneTask.setWarehouseName(task.get("MWAREHOUSE_NAME").toString());
                    oneTask.setTotRQty(task.get("TOTRQTY").toString());
                    oneTask.setTotAQty(task.get("TOTAQTY").toString());
                    oneTask.setTotDQty(task.get("TOTDQTY").toString());

//                    double dQty = BigDecimalUtils.sub(Double.parseDouble(task.get("QTY").toString()), Double.parseDouble(task.get("AQTY").toString()));
//
//                    oneTask.setTotDQty(String.valueOf(dQty));


                }



            }
        }

        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_SortingTaskQueryReq req) throws Exception {

        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sb.append(" SELECT * FROM( ")
                .append(" SELECT COUNT(*) OVER() NUM, row_number() OVER (ORDER BY a.TASKNO DESC) AS RN, ")
                .append(" a.* ")
                .append(" ,c.BILLNO,c.BDATE,c.WAREHOUSE,c.STATUS,wl1.WAREHOUSE_NAME ")
                .append(" ,b.STATUS MSTATUS,b.RDATE,b.WAREHOUSENO MWAREHOUSENO,wl2.WAREHOUSE_NAME MWAREHOUSE_NAME,b.ISSTOCKOUT ")
//                .append(" ,CASE WHEN REQUIRETYPE='0' THEN ol1.ORG_NAME ELSE co1.CUSTOMER_NAME END  REQUIRENAME ")
                .append(" FROM ( SELECT ORGANIZATIONNO,EID,TASKNO,OFNO,SUM(AQTY) TOTAQTY,SUM(TOTAQTY) TOTRQTY,SUM(AQTY-TOTAQTY) TOTDQTY FROM MES_SORTINGTASK_DETAIL GROUP BY EID,ORGANIZATIONNO,TASKNO,OFNO )  a ")
                .append(" INNER JOIN MES_SORTINGTASK b ON a.eid=b.eid and a.TASKNO=b.TASKNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO ")
                .append(" LEFT JOIN DCP_SORTINGASSIGN c on a.eid=c.eid and a.OFNO=c.BILLNO ")
                .append(" LEFT JOIN DCP_WAREHOUSE_LANG wl1 on wl1.eid=c.eid and wl1.WAREHOUSE=c.WAREHOUSE and wl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_WAREHOUSE_LANG wl2 on wl2.eid=b.eid and wl2.WAREHOUSE=b.WAREHOUSENO and wl2.LANG_TYPE='").append(req.getLangType()).append("'")
//                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.REQUIRENO AND REQUIRETYPE='0' and ol1.LANG_TYPE='").append(req.getLangType()).append("'")
//                .append(" LEFT JOIN DCP_CUSTOMER co1 on co1.eid=a.eid and co1.CUSTOMERNO=a.REQUIRENO AND REQUIRETYPE='1' ")

        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            sb.append("  and ( ")
                    .append("  a.OFNO like '%%").append(req.getRequest().getBillNo()).append("%%'")
                    .append(")");

        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY TASKNO ");

        return sb.toString();
    }
}
