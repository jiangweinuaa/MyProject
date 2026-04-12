package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_EmployeeInfoQuery_OpenReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_EmployeeInfoQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_EmployeeInfoQuery_Open
 * 說明：员工信息查询
 * 服务说明：员工信息查询
 *
 * @author wangzyc
 * @since 2021-4-14
 */
public class DCP_EmployeeInfoQuery_Open extends SPosBasicService<DCP_EmployeeInfoQuery_OpenReq, DCP_EmployeeInfoQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_EmployeeInfoQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        DCP_EmployeeInfoQuery_OpenReq.level1Elm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer("");
        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if (Check.Null(request.getMachShopNo())) {
            errCt++;
            errMsg.append("生产门店编号不可为空值, ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_EmployeeInfoQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_EmployeeInfoQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_EmployeeInfoQuery_OpenRes getResponseType() {
        return new DCP_EmployeeInfoQuery_OpenRes();
    }

    @Override
    protected DCP_EmployeeInfoQuery_OpenRes processJson(DCP_EmployeeInfoQuery_OpenReq req) throws Exception {
        DCP_EmployeeInfoQuery_OpenRes res = null;
        res = this.getResponse();
        res.setDatas(new ArrayList<DCP_EmployeeInfoQuery_OpenRes.level1Elm>());
        try {
            String sql = this.getEnableKDS(req);
            List<Map<String, Object>> getEnableKDS = this.doQueryData(sql, null); // 实际只有一条数据
            String enableKds = "";
            if(!CollectionUtils.isEmpty(getEnableKDS)){
                Map<String, Object> enableKDS = getEnableKDS.get(0);
                 enableKds = enableKDS.get("KDS").toString(); // Y/N
            }

            sql = this.getQuerySql(req);
            List<Map<String, Object>> getEmployees = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getEmployees)){
                for (Map<String, Object> getEmployee : getEmployees) {
                    DCP_EmployeeInfoQuery_OpenRes.level1Elm level1Elm = res.new level1Elm();
                    String opNo = getEmployee.get("OPNO").toString();
                    String opName = getEmployee.get("OP_NAME").toString();
                    String departNo = getEmployee.get("DEPARTNO").toString();
                    String goodsNum = getEmployee.get("GOODSNUM").toString();
                    String fraction = getEmployee.get("FRACTION").toString();
                    String activation = getEmployee.get("ACTIVATION").toString();

                    if(!enableKds.equals("Y")){
                        fraction = "生产设备未开启";
                    }

                    if(Check.Null(activation)){
                        activation = "N";
                    }

                    level1Elm.setOpNo(opNo);
                    level1Elm.setOpName(opName);
                    level1Elm.setDepartNo(departNo);
                    level1Elm.setGoodsNum(goodsNum);
                    level1Elm.setFraction(fraction);
                    level1Elm.setActivation(activation);
                    res.getDatas().add(level1Elm);
                }
            }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!"+e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_EmployeeInfoQuery_OpenReq req) throws Exception {
        String langType = req.getLangType();
        DCP_EmployeeInfoQuery_OpenReq.level1Elm request = req.getRequest();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String activation = request.getActivation();
        String machShopNo = request.getMachShopNo();
        String eId = req.geteId();
        String opNo = request.getOpNo();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT * FROM ( " +
                " SELECT a.OPNO, b.OP_NAME, a.DEPARTNO, Sum(d.PQTY) AS goodsNum , Sum(d.PQTY * (e.WORTHSCORE / EVERYPIECE)) AS fraction , a.ACTIVATION " +
                " FROM PLATFORM_STAFFS a " +
                " LEFT JOIN PLATFORM_STAFFS_LANG b ON a.EID = b.EID AND a.OPNO = b.OPNO AND b.LANG_TYPE = '"+langType+"' AND b.STATUS = '100' " +
                " LEFT JOIN DCP_PROCESSTASK c ON a.EID = c.EID AND a.ORGANIZATIONNO = c.SHOPID " +
                " LEFT JOIN DCP_PROCESSTASK_DETAIL d ON a.EID = d.EID AND a.ORGANIZATIONNO = d.SHOPID AND c.PROCESSTASKNO = d.PROCESSTASKNO AND a.OPNO = d.OPNO " +
                " LEFT JOIN DCP_ORG_ORDERSET e ON a.EID = e.EID AND a.ORGANIZATIONNO = e.ORGANIZATIONNO AND e.KDS = 'Y' " +
                " WHERE a.EID = '"+eId+"' AND a.ORGANIZATIONNO = '"+machShopNo+"' " +
                " ");

        if(!Check.Null(activation)){
            sqlbuf.append(" AND （a.ACTIVATION = '"+activation+"'");

            if(activation.equals("N")){
                sqlbuf.append(" OR a.ACTIVATION is null");
            }
            sqlbuf.append(")");
        }
        if(!Check.Null(opNo)){
            sqlbuf.append(" AND a.OPNO LIKE '%%"+opNo+"%%'");
        }
        if(!Check.Null(beginDate)&&Check.Null(endDate)){
            sqlbuf.append(" AND to_date(substr(COMPLETETIME,1,8),'yyyyMMdd') = to_date('"+beginDate+"','yyyyMMdd') ");
        }
        if(!Check.Null(endDate)&&Check.Null(beginDate)){
            sqlbuf.append(" AND to_date(substr(COMPLETETIME,1,8),'yyyyMMdd') = to_date('"+endDate+"','yyyyMMdd')");
        }
        if(!Check.Null(beginDate)&&!Check.Null(endDate)){
            sqlbuf.append(" AND (to_date(substr(COMPLETETIME,1,8),'yyyyMMdd') >= to_date('"+beginDate+"','yyyyMMdd') and to_date(substr(COMPLETETIME,1,8),'yyyyMMdd') <= to_date('"+endDate+"','yyyyMMdd')) ");
        }

        sqlbuf.append(" GROUP BY a.OPNO, b.OP_NAME, a.DEPARTNO, a.ACTIVATION ) ");
        sqlbuf.append(" ORDER BY goodsNum DESC NULLS LAST, fraction DESC NULLS LAST");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询该生产门店 是否开启KDS
     *
     * @param req
     * @return
     */
    private String getEnableKDS(DCP_EmployeeInfoQuery_OpenReq req) {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_ORG_ORDERSET WHERE EID  = '" + req.geteId() + "' AND ORGANIZATIONNO = '" + req.getRequest().getMachShopNo() + "' ");
        sql = sqlbuf.toString();
        return sql;
    }
}
