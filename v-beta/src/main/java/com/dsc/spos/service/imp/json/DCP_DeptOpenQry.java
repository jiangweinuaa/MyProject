package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DeptOpenQryReq;
import com.dsc.spos.json.cust.res.DCP_DeptOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_DeptOpenQry extends SPosBasicService<DCP_DeptOpenQryReq, DCP_DeptOpenQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_DeptOpenQryReq req) throws Exception {
        boolean isFail = false;
        DCP_DeptOpenQryReq.levelElm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer("");
        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DeptOpenQryReq> getRequestType() {
        return new TypeToken<DCP_DeptOpenQryReq>() {
        };
    }

    @Override
    protected DCP_DeptOpenQryRes getResponseType() {
        return new DCP_DeptOpenQryRes();
    }

    @Override
    protected DCP_DeptOpenQryRes processJson(DCP_DeptOpenQryReq req) throws Exception {
        DCP_DeptOpenQryRes res = null;
        res = this.getResponse();

        int totalRecords = 0;                                //总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<DCP_DeptOpenQryRes.level1Elm>());
        try {
            String sql = "";
            sql = this.getQuerySql(req);
            List<Map<String, Object>> getDepartList = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getDepartList)) {

                String num = getDepartList.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);

                // 计算页数
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> depart : getDepartList) {
                    DCP_DeptOpenQryRes.level1Elm level1Elm = res.new level1Elm();
                    String departNo = depart.get("DEPARTNO").toString();
                    String departName = depart.get("DEPARTNAME").toString();
                    String fullname = depart.get("FULLNAME").toString();
                    String status = depart.get("STATUS").toString();
                    level1Elm.setDeptNo(departNo);
                    level1Elm.setSName(departName);
                    level1Elm.setFullName(fullname);
                    level1Elm.setStatus(status);
                    level1Elm.setIsProductGroup(depart.get("ISPRODUCTGROUP").toString());
                    level1Elm.setCorp(depart.get("CORP").toString());
                    level1Elm.setCorpName(depart.get("CORPNAME").toString());
                    res.getDatas().add(level1Elm);
                }
            }

            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_DeptOpenQryReq req) throws Exception {
        String langType = req.getLangType();
        String eid = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        //String status = req.getRequest().getStatus();
//        String orgNo = req.getOrganizationNO();
        String orgNo = req.getRequest().getOrgNo();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");

        String cost = req.getRequest().getIsCostAlloc();

        if ("Y".equals(cost)) {


        }

        sqlbuf.append("SELECT * FROM ( " +
                " SELECT COUNT(DISTINCT DEPARTNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY DEPARTNO) AS rn , a.* FROM ( " +
                " SELECT a.DEPARTNO, b.DEPARTNAME, b.FULLNAME, a.STATUS,a.ISPRODUCTGROUP,c.corp,d.org_name as corpname " +
                " FROM DCP_DEPARTMENT a " +
                " LEFT JOIN DCP_DEPARTMENT_lang b ON a.EID = b.EID AND a.DEPARTNO = b.DEPARTNO AND b.LANG_TYPE = '" + langType + "' " +
                " left join dcp_org c on a.eid=c.eid and a.organizationno=c.organizationno" +
                " left join dcp_org_lang d on a.eid=d.eid and c.corp=d.organizationno and d.lang_type='"+req.getLangType()+"'" +
                " WHERE a.EID = '" + eid + "' and a.status='100' and a.organizationno='"+req.getOrganizationNO()+"'  " +
                " ");

        //if (StringUtils.isNotEmpty(status)) {
        //    sqlbuf.append(" AND a.status = '").append(status).append("'");

        //}

        if (StringUtils.isNotEmpty(keyTxt)) {
            sqlbuf.append(" AND (b.DEPARTNO LIKE '%%").append(keyTxt).append("%%' ").append(" or b.DEPARTNAME LIKE '%%").append(keyTxt).append("%%' ").append(")");
        }

        //if (!Check.Null(orgNo)) {
        //    sqlbuf.append(" AND a.ORGANIZATIONNO = '").append(orgNo).append("'");
        //} else {
        //    sqlbuf.append(" and a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("' ");
        //}

        sqlbuf.append("  ) a ) WHERE RN > ").append(startRow).append(" AND rn <= ").append(startRow + pageSize).append(" ORDER BY DEPARTNO DESC ");

        sql = sqlbuf.toString();
        return sql;
    }

}
