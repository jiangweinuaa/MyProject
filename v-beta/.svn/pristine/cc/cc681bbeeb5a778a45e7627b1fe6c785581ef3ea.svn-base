package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApBillStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ApBillStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ApBillStatusUpdate extends SPosAdvanceService<DCP_ApBillStatusUpdateReq, DCP_ApBillStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_ApBillStatusUpdateReq req, DCP_ApBillStatusUpdateRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String opType = req.getRequest().getOpType();
        String apNo = req.getRequest().getApNo();
        String accountId = req.getRequest().getAccountId();

        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());

        String apSql = "select a.*,to_char(a.pdate,'yyyyMMdd') as pdates " +
                " from DCP_APBILL a " +
                " where a.eid='" + eId + "' and a.accountid='" + accountId + "' and a.apno='" + apNo + "' ";
        List<Map<String, Object>> list = this.doQueryData(apSql, null);
        if (list.size() == 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的单据！".toString());
        }

        String status = list.get(0).get("STATUS").toString();
        String pDate = list.get(0).get("PDATES").toString();

        //confirm审核 unConfirm取消审核 cancel作废
        if ("confirm".equals(opType)) {
            //单据日期不得小于应付关账日期；不可审核
            if (!"1".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "不可审核".toString());
            }
            String accSql = "select a.*  from DCP_ACOUNT_SETTING a where a.eid='" + eId + "' and a.accountid='" + accountId + "' and to_char(a.APCLOSINGDATE,'yyyyMMdd')>'" + pDate + "' ";
            List<Map<String, Object>> accList = this.doQueryData(accSql, null);
            if (accList.size() > 0) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于应付关账日期".toString());
            }

            UptBean ub1 = new UptBean("DCP_APBILL");
            ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_DATE", new DataValue(createDate, Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_TIME", new DataValue(createTime, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
            ub1.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        } else if ("unConfirm".equals(opType)) {
            if (!"2".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "不可取消审核".toString());
            }
            String accSql = "select a.*  from DCP_ACOUNT_SETTING a where a.eid='" + eId + "' and a.accountid='" + accountId + "' and to_char(a.APCLOSINGDATE,'yyyyMMdd')>'" + pDate + "' ";
            List<Map<String, Object>> accList = this.doQueryData(accSql, null);
            if (accList.size() > 0) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于应付关账日期".toString());
            }

            //DCP_APBILLPMT  判断是否已付款
            String pmtSql = "select a.*  from DCP_APBILLPMT a where a.eid='" + eId + "' and a.accountid='" + accountId + "' and a.apno='" + apNo + "' ";
            List<Map<String, Object>> pmtList = this.doQueryData(pmtSql, null);
            if (pmtList.size() > 0) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已付款，不可取消".toString());
            }
            UptBean ub1 = new UptBean("DCP_APBILL");
            ub1.addUpdateValue("CONFIRMBY", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_DATE", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_TIME", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
            ub1.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        } else if ("cancel".equals(opType)) {
            if (!"1".equals(status)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "不可取消".toString());
            }
            UptBean ub1 = new UptBean("DCP_APBILL");
            ub1.addUpdateValue("STATUS", new DataValue("-1", Types.VARCHAR));
            ub1.addUpdateValue("CANCELBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CANCEL_DATE", new DataValue(createDate, Types.VARCHAR));
            ub1.addUpdateValue("CANCEL_TIME", new DataValue(createTime, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
            ub1.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApBillStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApBillStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApBillStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApBillStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ApBillStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ApBillStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ApBillStatusUpdateRes getResponseType() {
        return new DCP_ApBillStatusUpdateRes();
    }
}

