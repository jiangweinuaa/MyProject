package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_BatchingQueryReq;
import com.dsc.spos.json.cust.res.DCP_BatchingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_BatchingQuery extends SPosBasicService<DCP_BatchingQueryReq, DCP_BatchingQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_BatchingQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BatchingQueryReq> getRequestType() {
        return new TypeToken<DCP_BatchingQueryReq>() {
        };
    }

    @Override
    protected DCP_BatchingQueryRes getResponseType() {
        return new DCP_BatchingQueryRes();
    }

    @Override
    protected DCP_BatchingQueryRes processJson(DCP_BatchingQueryReq req) throws Exception {
        DCP_BatchingQueryRes res = getResponseType();
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

                DCP_BatchingQueryRes.Datas oneData = res.new Datas();

                oneData.setBDate(StringUtils.toString(data.get("BDATE"), ""));
                oneData.setBatchNo(StringUtils.toString(data.get("BATCHNO"), ""));
                oneData.setDocType(StringUtils.toString(data.get("DOC_TYPE"), ""));

                oneData.setTotCQty(StringUtils.toString(data.get("TOTCQTY"), ""));
                oneData.setTotPQty(StringUtils.toString(data.get("TOTPQTY"), ""));

                oneData.setCreateBy(StringUtils.toString(data.get("CREATEOPID"), ""));
                oneData.setCreateByName(StringUtils.toString(data.get("CREATEOPNAME"), ""));
                oneData.setCreateTime(StringUtils.toString(data.get("CREATETIME"), ""));

                oneData.setModifyBy(StringUtils.toString(data.get("LASTMODIOPID"), ""));
                oneData.setModifyByName(StringUtils.toString(data.get("LASTMODIOPNAME"), ""));
                oneData.setModifyTime(StringUtils.toString(data.get("LASTMODITIME"), ""));

                oneData.setConfirmBy(StringUtils.toString(data.get("ACCOUNTOPID"), ""));
                oneData.setConfirmByName(StringUtils.toString(data.get("ACCOUNTOPNAME"), ""));
                oneData.setConfirmTime(StringUtils.toString(data.get("ACCOUNTTIME"), ""));

                oneData.setProcessStatus(StringUtils.toString(data.get("PROCESS_STATUS"), ""));
                oneData.setProcessErpNo(StringUtils.toString(data.get("PROCESS_ERP_NO"), ""));
                oneData.setProcessErpOrg(StringUtils.toString(data.get("PROCESS_ERP_ORG"), ""));

                oneData.setAccountDate(data.get("ACCOUNTDATE").toString());
                oneData.setEmployeeId(data.get("EMPLOYEEID").toString());
                oneData.setEmployeeName(data.get("EMPLOYEENAME").toString());
                oneData.setDepartId(data.get("DEPARTID").toString());
                oneData.setDepartName(data.get("DEPARTNAME").toString());
                oneData.setStatus(data.get("STATUS").toString());
                oneData.setOOType(data.get("OOTYPE").toString());

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
    protected String getQuerySql(DCP_BatchingQueryReq req) throws Exception {

        StringBuilder sb = new StringBuilder();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        sb.append(" SELECT * FROM( ")
                .append(" SELECT row_number() OVER (ORDER BY a.BDATE DESC,a.BATCHNO ASC) AS RN, ")
                .append(" COUNT(*) OVER ( ) NUM,")
                .append(" a.*,sd.TOTPQTY,sd.TOTCQTY ")
                .append(" ,gl.ORG_NAME PROCESS_ERP_NAME,b.name as employeename,c.departname ")
                .append(" FROM MES_BATCHING a ")
                .append(" LEFT JOIN DCP_ORG_LANG gl on gl.EID=a.EID and gl.ORGANIZATIONNO=a.PROCESS_ERP_NO and gl.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN ( SELECT EID,ORGANIZATIONNO,BATCHNO,SUM(PQTY) TOTPQTY,COUNT(DISTINCT PLUNO) TOTCQTY " +
                        "   FROM MES_BATCHING_DETAIL" +
                        "   GROUP BY EID,ORGANIZATIONNO,BATCHNO ) sd on a.EID=sd.EID and a.BATCHNO=sd.BATCHNO and a.ORGANIZATIONNO=sd.ORGANIZATIONNO ")
                .append(" left join dcp_employee b on b.eid=a.eid and a.employeeid=b.employeeno " +
                        " left join DCP_DEPARTMENT_LANG c on c.eid=a.eid and a.departid=c.departno and c.lang_type='").append(req.getLangType()).append("'")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");

        if("bDate".equals(req.getRequest().getDateType())) {
            if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
                sb.append(" AND a.BDATE>='").append(req.getRequest().getBeginDate()).append("'");
            }
            if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
                sb.append(" AND a.BDATE<='").append(req.getRequest().getEndDate()).append("'");
            }
        }
        if("accountDate".equals(req.getRequest().getDateType())){
            if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())) {
                sb.append(" AND a.ACCOUNTDATE>='").append(req.getRequest().getBeginDate()).append("'");
            }
            if (StringUtils.isNotEmpty(req.getRequest().getEndDate())) {
                sb.append(" AND a.ACCOUNTDATE<='").append(req.getRequest().getEndDate()).append("'");
            }
        }

        if (StringUtils.isNotEmpty(req.getRequest().getKeyTxt())){
            sb.append(" AND ( a.BATCHNO like '%%").append(req.getRequest().getKeyTxt()).append("%%' ")
                    .append(" or a.PROCESS_ERP_NO like '%%").append(req.getRequest().getKeyTxt()).append("%%' ")
                    .append(")");
        }
        if(Check.NotNull(req.getRequest().getStatus())){
            sb.append(" AND a.PROCESS_STATUS='").append(req.getRequest().getStatus()).append("'");
        }
        if(Check.NotNull(req.getRequest().getDepartId())){
            sb.append(" AND a.DEPARTID='").append(req.getRequest().getDepartId()).append("'");
        }

        sb.append("  )  a "
                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
                + " ORDER BY a.BDATE DESC,a.BATCHNO ASC ");

        return sb.toString();
    }
}
