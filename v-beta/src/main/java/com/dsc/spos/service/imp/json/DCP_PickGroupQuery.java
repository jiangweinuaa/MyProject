package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PickGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_PickGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PickGroupQuery extends SPosBasicService<DCP_PickGroupQueryReq, DCP_PickGroupQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PickGroupQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PickGroupQueryReq> getRequestType() {
        return new TypeToken<DCP_PickGroupQueryReq>() {
        };
    }

    @Override
    protected DCP_PickGroupQueryRes getResponseType() {
        return new DCP_PickGroupQueryRes();
    }

    @Override
    protected DCP_PickGroupQueryRes processJson(DCP_PickGroupQueryReq req) throws Exception {
        DCP_PickGroupQueryRes res = this.getResponseType();

        int totalRecords;                //总笔数
        int totalPages;
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
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

            for (Map<String, Object> data : getData) {
                DCP_PickGroupQueryRes.Datas oneData = res.new Datas();

                oneData.setPickGroupNo(StringUtils.toString(data.get("PICKGROUPNO"),""));
                oneData.setPickGroupName(StringUtils.toString(data.get("PICKGROUPNAME"),""));
                oneData.setWarehouse(StringUtils.toString(data.get("WAREHOUSE"),""));
                oneData.setWarehouseName(StringUtils.toString(data.get("WAREHOUSE_NAME"),""));
                oneData.setWareRegionNo(StringUtils.toString(data.get("WAREREGIONNO"),""));
                oneData.setWareRegionName(StringUtils.toString(data.get("WAREREGIONNAME"),""));
                oneData.setPickType(StringUtils.toString(data.get("PICKTYPE"),""));
                oneData.setRangeType(StringUtils.toString(data.get("PICKRANGETYPE"),""));
                oneData.setObjectRange(StringUtils.toString(data.get("OBJECTRANGE"),""));
                oneData.setStatus(StringUtils.toString(data.get("STATUS"),""));
                oneData.setMemo(StringUtils.toString(data.get("MEMO"),""));
                oneData.setCreateOpId(StringUtils.toString(data.get("CREATEOPID"),""));
                oneData.setCreateOpName(StringUtils.toString(data.get("CREATEOPNAME"),""));
                oneData.setCreateTime(StringUtils.toString(data.get("CREATETIME"),""));
                oneData.setLastModiOpId(StringUtils.toString(data.get("LASTMODIOPID"),""));
                oneData.setLastModiOpName(StringUtils.toString(data.get("LASTMODIOPNAME"),""));
                oneData.setLastModiTime(StringUtils.toString(data.get("LASTMODITIME"),""));

                res.getDatas().add(oneData);

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
    protected String getQuerySql(DCP_PickGroupQueryReq req) throws Exception {

        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.PICKGROUPNO ) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.*, ")
                .append(" em1.name AS CREATEOPNAME,em2.name AS LASTMODIOPNAME,dd0.DEPARTNAME AS CREATEDEPTNAME ")
                .append(",wl1.WAREHOUSE_NAME,h.WAREREGIONNAME  ")
                .append(" FROM DCP_PICKGROUP a ")
                .append(" left join dcp_warehouse_lang wl1 on wl1.eid=a.eid and wl1.warehouse=a.warehouse and wl1.lang_type='").append(req.getLangType()).append("'")
                .append(" left join MES_WAREREGION h on h.eid=a.eid and h.WAREREGIONNO=a.WAREREGIONNO and a.warehouse=h.WAREHOUSENO ")
                .append(" LEFT JOIN DCP_employee em1 ON em1.eid = a.eid AND em1.employeeno = a.CREATEOPID ")
                .append(" LEFT JOIN DCP_employee em2 ON em2.eid = a.eid AND em2.employeeno = a.LASTMODIOPID ")
                .append(" LEFT JOIN dcp_department_lang dd0 ON dd0.eid = a.eid AND dd0.departno = a.CREATEDEPTID AND dd0.lang_type='").append(req.getLangType()).append("'");

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'")
                .append(" and a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getStatus())){
            sb.append(" AND a.STATUS='").append(req.getRequest().getStatus()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())) {
//            编号/名称
            sb.append(" AND (A.PICKGROUPNO like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(" OR A.PICKGROUPNAME like '%%").append(req.getRequest().getKeyTxt()).append("%%'")
                    .append(")");
        }

        sb.append("  ) a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY PICKGROUPNO ");

        return sb.toString();
    }
}
