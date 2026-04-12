package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_WoStatusQueryReq;
import com.dsc.spos.json.cust.res.DCP_WoStatusQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_WoStatusQuery extends SPosBasicService<DCP_WoStatusQueryReq, DCP_WoStatusQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_WoStatusQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WoStatusQueryReq> getRequestType() {
        return new TypeToken<DCP_WoStatusQueryReq>() {
        };
    }

    @Override
    protected DCP_WoStatusQueryRes getResponseType() {
        return new DCP_WoStatusQueryRes();
    }

    @Override
    protected DCP_WoStatusQueryRes processJson(DCP_WoStatusQueryReq req) throws Exception {

        DCP_WoStatusQueryRes res = this.getResponseType();

        int totalRecords = 0; //总笔数
        int totalPages = 0;

        String sql = this.getQuerySql(req);
        String[] conditionValues = {};
        List<Map<String, Object>> workDatas = this.doQueryData(sql, conditionValues);
        res.setDatas(new ArrayList<>());

        if (!workDatas.isEmpty()) {

            String num = workDatas.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : workDatas) {

                DCP_WoStatusQueryRes.Datas workData = res.new Datas();

                workData.setStatus(oneData.get("STATUS").toString());
                workData.setAccountID(oneData.get("ACCOUNTID").toString());
                workData.setAccount(oneData.get("ACCOUNT").toString());
                workData.setCost_Calculation(oneData.get("COST_CALCULATION").toString());
                workData.setItem(oneData.get("RN").toString());
                workData.setOrganizationNo(oneData.get("ORGANIZATIONNO").toString());
                workData.setOrg_Name(oneData.get("ORG_NAME").toString());
                workData.setBatchTaskNo(oneData.get("BATCHSTATUS").toString());
                workData.setBDate(oneData.get("BDATE").toString());
                workData.setDoc_Type(oneData.get("DOC_TYPE").toString());
                workData.setProductStatus(oneData.get("PRODUCTSTATUS").toString());
                workData.setPluNo(oneData.get("PLUNO").toString());
                workData.setPluName(oneData.get("PLUNAME").toString());
                workData.setSpec(oneData.get("SPEC").toString());
                workData.setPqty(oneData.get("PQTY").toString());
                workData.setSetQty("0");
                workData.setInvQtyEndMth(oneData.get("TOT_PQTY").toString());
                workData.setIsRework(oneData.get("ISREWORK").toString());
                workData.setCostCloseDate(oneData.get("COSTCLOSEDATE").toString());
                workData.setTaskCloseDate(oneData.get("TASKCLOSEDATE").toString());
                workData.setPmcloseStatus(oneData.get("PMCLOSESTATUS").toString());


                res.getDatas().add(workData);


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
    protected String getQuerySql(DCP_WoStatusQueryReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT * FROM ( " +
                " SELECT COUNT(*) OVER() NUM,  row_number() OVER(ORDER BY BATCHTASKNO ) rn , " +
                " a.*,b.ACCOUNT_DATE,NVL(b.TOT_PQTY,0) TOT_PQTY " +
                " ,d.SPEC,c.PLU_NAME PLUNAME,ol1.ORG_NAME,ac.ACCOUNTID,ac.ACCOUNT" +
                " ,o.COST_CALCULATION  " +
                " FROM MES_BATCHTASK  a  " +
                " LEFT JOIN DCP_PSTOCKIN b ON a.EID=b.EID and a.BATCHTASKNO = b.OFNO " +
                " LEFT JOIN DCP_GOODS_LANG c ON a.eid=c.eid and a.PLUNO=c.PLUNO and c.LANG_TYPE='" + req.getLangType() + "'" +
                " LEFT JOIN DCP_GOODS d on d.eid = a.eid and d.PLUNO=a.PLUNO " +
                " LEFT JOIN DCP_ACOUNT_SETTING ac on ac.eid=a.eid and ac.CORP=a.ORGANIZATIONNO and ACCTTYPE='1' " +
                " LEFT JOIN DCP_ORG_LANG ol1 on a.EID=ol1.eid and a.ORGANIZATIONNO=ol1.ORGANIZATIONNO and ol1.LANG_TYPE='" + req.getLangType() + "'" +
                " LEFT JOIN DCP_ORG o on a.EID=o.eid and a.ORGANIZATIONNO=o.ORGANIZATIONNO "

        );
        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getCorp()).append("'");
        }

        String year = req.getRequest().getYear();
        String period = req.getRequest().getPeriod();

        if (StringUtils.isNotEmpty(period)) {
            if (period.length() < 2) {
                period = "0" + period;
            }
            querySql.append(" AND b.ACCOUNT_DATE like '%%").append(year).append(period).append("%%'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBDate())) {
            querySql.append(" AND a.BDATE = '").append(req.getRequest().getBDate()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getWorkType())) {
            String workType = req.getRequest().getWorkType();


        }


        querySql.append(")  WHERE rn > " + startRow + " and rn <= " + (startRow + pageSize) + " order by BATCHTASKNO  ");
        return querySql.toString();
    }
}
