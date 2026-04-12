package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_HrExpDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_HrExpDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_HrExpDetailQuery extends SPosBasicService<DCP_HrExpDetailQueryReq, DCP_HrExpDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_HrExpDetailQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_HrExpDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_HrExpDetailQueryReq>() {
        };
    }

    @Override
    protected DCP_HrExpDetailQueryRes getResponseType() {
        return new DCP_HrExpDetailQueryRes();
    }

    @Override
    protected DCP_HrExpDetailQueryRes processJson(DCP_HrExpDetailQueryReq req) throws Exception {
        DCP_HrExpDetailQueryRes res = this.getResponseType();
        int totalRecords = 0;
        int totalPages = 0;
        //单头查询
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            Map<String, Boolean> distinct = new HashMap<>();
            distinct.put("ACCOUNTID", true);
            distinct.put("YEAR", true);
            distinct.put("PERIOD", true);
            distinct.put("COST_CALCULATION", true);
            List<Map<String, Object>> distinctData = MapDistinct.getMap(getQData, distinct);
            for (Map<String, Object> row : distinctData) {
                DCP_HrExpDetailQueryRes.Datas oneData = res.new Datas();
                res.getDatas().add(oneData);

                oneData.setCorp(row.get("CORP").toString());
                oneData.setYear(row.get("YEAR").toString());
                oneData.setPeriod(row.get("PERIOD").toString());
                oneData.setAccountID(row.get("ACCOUNTID").toString());
                oneData.setAccount(row.get("ACCOUNT").toString());
                oneData.setCost_Calculation(row.get("COST_CALCULATION").toString());
                oneData.setStatus(row.get("STATUS").toString());

                oneData.setWorkList(new ArrayList<>());

                Map<String, Object> condition = new HashMap<>();
                condition.put("CORP", row.get("CORP").toString());
                condition.put("ACCOUNTID", row.get("ACCOUNTID").toString());
                condition.put("YEAR", row.get("YEAR").toString());
                condition.put("PERIOD", row.get("PERIOD").toString());
                condition.put("COST_CALCULATION", row.get("COST_CALCULATION").toString());
                List<Map<String, Object>> detail = MapDistinct.getWhereMap(getQData, condition, true);
                for (Map<String, Object> oneDetail : detail) {
                    DCP_HrExpDetailQueryRes.WorkList oneWork = res.new WorkList();
                    oneData.getWorkList().add(oneWork);

                    oneWork.setItem(oneDetail.get("RN").toString());
                    oneWork.setCostCenter(oneDetail.get("COSTCENTERNAME").toString());
                    oneWork.setOrganizationID(oneDetail.get("CORP").toString());
                    oneWork.setAllocAmount(oneDetail.get("ALLOCAMOUT").toString());
                    oneWork.setAllocQty(oneDetail.get("ALLOCQTY").toString());
                    oneWork.setCostCenterNo(oneDetail.get("COSTCENTERNO").toString());
                    oneWork.setTaskId(oneDetail.get("TASKID").toString());

                    oneWork.setUnitCost(oneDetail.get("UNITCOST").toString());

                    oneWork.setAllocFormula(oneDetail.get("ALLOCFORMULA").toString());
                    oneWork.setAllocType(oneDetail.get("ALLOCTYPE").toString());
                }
            }

        }
        res.setSuccess(true);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_HrExpDetailQueryReq req) throws Exception {
        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY b.YEAR,b.PERIOD,b.TASKID) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" b.*,NVL(ol1.ORG_NAME,DEPARTNAME) COSTCENTERNAME")
                .append(" FROM DCP_HREXPSTAT a ")
                .append(" LEFT JOIN DCP_HREXPDETAIL b on a.eid=b.eid and a.CORP=b.CORP and a.ACCOUNTID=b.ACCOUNTID and a.YEAR=b.YEAR and a.PERIOD=b.PERIOD and a.COSTCENTERNO=b.COSTCENTERNO and a.ALLOCTYPE=b.ALLOCTYPE ")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.COSTCENTERNO AND ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dl1 on dl1.eid=a.eid and dl1.DEPARTNO=a.COSTCENTERNO AND dl1.LANG_TYPE='").append(req.getLangType()).append("'")
        ;
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" AND a.YEAR='").append(req.getRequest().getYear()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" AND a.PERIOD='").append(Integer.parseInt(req.getRequest().getPeriod())).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCost_Calculation())) {
            sb.append(" AND a.COST_CALCULATION='").append(req.getRequest().getCost_Calculation()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getAllocType())) {
            sb.append(" AND a.ALLOCTYPE='").append(req.getRequest().getAllocType()).append("'");
        }

        sb.append("  ) a ");
        if (pageSize >= 0) {
            sb.append("  WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize));
        }
        sb.append(" ORDER BY rn ");

        return sb.toString();
    }
}
