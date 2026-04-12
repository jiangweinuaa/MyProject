package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CostLevelQueryReq;
import com.dsc.spos.json.cust.res.DCP_CostLevelQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CostLevelQuery extends SPosBasicService<DCP_CostLevelQueryReq, DCP_CostLevelQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_CostLevelQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostLevelQueryReq> getRequestType() {
        return new TypeToken<DCP_CostLevelQueryReq>(){

        };
    }

    @Override
    protected DCP_CostLevelQueryRes getResponseType() {
        return new DCP_CostLevelQueryRes();
    }

    @Override
    protected DCP_CostLevelQueryRes processJson(DCP_CostLevelQueryReq req) throws Exception {
        DCP_CostLevelQueryRes res = this.getResponseType();
        res.setDatas(new ArrayList<>());
        List<Map<String,Object>> queryData = doQueryData(getQuerySql(req),null);
        for (Map<String,Object> oneData : queryData) {

            DCP_CostLevelQueryRes.Datas data = res.new Datas();

            data.setCostGroupingId(oneData.get("COSTGROUPINGID").toString());
            data.setCostGroupingId_Name(oneData.get("COSTGROUPINGID_NAME").toString());
            data.setStatus(oneData.get("STATUS").toString());
            data.setStart_CostLevel(oneData.get("START_COSTLEVEL").toString());
            data.setEnd_CostLevel(oneData.get("END_COSTLEVEL").toString());

            res.getDatas().add(data);

        }

        res.setSuccess(true);
        res.setServiceStatus("000");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CostLevelQueryReq req) throws Exception {
        String eId = req.geteId();

        String costGroupingId = req.getRequest().getCostGroupingId();
        String status = req.getRequest().getStatus();
        String keyTxt = req.getRequest().getKeyTxt();

        //計算起啟位置
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        int startRow = (pageNumber - 1) * pageSize;

        StringBuilder sqlBuff = new StringBuilder();

        sqlBuff.append(" select * from ("
                + " select count(*) over() num, row_number() over (order by a.COSTGROUPINGID) rn,"
                + " a.* FROM DCP_COSTLEVEL a"
                + " WHERE a.EID='" + eId + "' ");


        if (keyTxt != null && !keyTxt.isEmpty()) {
            sqlBuff.append(" AND (a.COSTGROUPINGID_NAME like '%%").append(keyTxt).append("%%' or a.COSTGROUPINGID like '%%").append(keyTxt).append("%%' or a.STATUS like '%%").append(keyTxt).append("%%'  ) ");
        }
        if (costGroupingId != null && !costGroupingId.isEmpty()) {
            sqlBuff.append(" AND a.COSTGROUPINGID = '").append(costGroupingId).append("' ");
        }

        if (status != null && !status.isEmpty()) {
            sqlBuff.append(" AND a.status = '").append(status).append("' ");
        }

        sqlBuff.append(" ) where rn>").append(startRow).append(" and rn<=").append(startRow + pageSize);
        return sqlBuff.toString();

    }
}
