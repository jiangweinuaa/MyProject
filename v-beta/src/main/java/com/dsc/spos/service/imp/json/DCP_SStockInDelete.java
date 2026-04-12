package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SStockInDeleteReq;
import com.dsc.spos.json.cust.req.DCP_SStockInDeleteReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_SStockInDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：SStockInDelete
 * 說明：自采入库单删除
 * 服务说明：自采入库单删除
 *
 * @author jinzma
 * @since 2018-11-21
 */
public class DCP_SStockInDelete extends SPosAdvanceService<DCP_SStockInDeleteReq, DCP_SStockInDeleteRes> {

    @Override
    protected boolean isVerifyFail(DCP_SStockInDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        levelElm request = req.getRequest();
        String sStockInNO = request.getsStockInNo();

        if (Check.Null(sStockInNO)) {
            isFail = true;
            errMsg.append("自采入货单单号不可为空值, ");
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected void processDUID(DCP_SStockInDeleteReq req, DCP_SStockInDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        levelElm request = req.getRequest();
        String sStockInNO = request.getsStockInNo();

        //try {
            sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {

                if ("2".equals(getQData.get(0).get("OTYPE"))) {

                    sql = " UPDATE DCP_RECEIVING a SET STATUS='6' " +
                            " WHERE exists( SELECT 1 FROM DCP_SSTOCKIN_DETAIL b " +
                            " where b.EID='" + eId + "' and b.organizationno='" + organizationNO + "' and b.sstockinno='" + sStockInNO + "' " +
                            " and a.EID=b.EID and a.RECEIVINGNO=b.RECEIVINGNO  " +
                            " )";
                    ExecBean execBean = DataBeans.getExecBean(sql);
                    this.addProcessData(new DataProcessBean(execBean));
                }

                // DCP_SSTOCKIN
                DelBean db1 = new DelBean("DCP_SSTOCKIN");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                db1.addCondition("SStockInNO", new DataValue(sStockInNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                // DCP_SSTOCKIN_DETAIL
                DelBean db2 = new DelBean("DCP_SSTOCKIN_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                db2.addCondition("SStockInNO", new DataValue(sStockInNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));


                // DCP_TRANSACTION
                DelBean db3 = new DelBean("DCP_TRANSACTION");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db3.addCondition("BILLNO", new DataValue(sStockInNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

                ColumnDataValue condition1 = new ColumnDataValue();
                condition1.add("EID", req.geteId());
                condition1.add("BILLNO", sStockInNO);
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition1)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition1)));


                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }
        //} catch (Exception e) {
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SStockInDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SStockInDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SStockInDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TypeToken<DCP_SStockInDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_SStockInDeleteReq>() {
        };
    }

    @Override
    protected DCP_SStockInDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_SStockInDeleteRes();
    }

    @Override
    protected String getQuerySql(DCP_SStockInDeleteReq req) {
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        levelElm request = req.getRequest();
        String sStockInNO = request.getsStockInNo();

        String sql = "select OTYPE,status from DCP_sstockin "
                + " where EID='" + eId + "' " +
                " and organizationno='" + organizationNO + "' " +
                " and sstockinno='" + sStockInNO + "' and status='0' ";
        return sql;
    }

}
