package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApBillDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ApBillDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ApBillDelete extends SPosAdvanceService<DCP_ApBillDeleteReq, DCP_ApBillDeleteRes> {

    @Override
    protected void processDUID(DCP_ApBillDeleteReq req, DCP_ApBillDeleteRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String apNo = req.getRequest().getApNo();
        String accountId = req.getRequest().getAccountId();
        List<String> itemList = req.getRequest().getItemList();

        //DCP_ApBillDelete应付单-删除
        //1. 作业类型taskId：枚举：1：预付账款单，2：预付账款单，3：其他应付单，4 ：内部应付单，5：员工报销单，6：员工借款单，7：采购暂估单；只有这 7 个类型单据在未审核下可以做删除

        //2. 枚举值为：1：预付账款单；
        //3. 【未审核】状态下才可做删除
        //4. 删除涉及的表：DCP_APBILL 应付单单据，DCP_APBILLDETAIL应付单明细，DCP_APPERD应付单账期，DCP_APBILLWRITEOFF应付单核销，DCP_APBILLPMT应付单付款，DCP_APBILLESTDTL应付单冲暂估；DCP_PURINV进项发票档；用应付单为关联对应；
        //5. 删除成功后，单据来源采购单，需将DCP_PURORDER_PAY 中应付单号+已付金额同步删除

        List<String> canDeleteList = new ArrayList();
        canDeleteList.add("1");
        canDeleteList.add("2");
        canDeleteList.add("3");
        canDeleteList.add("4");
        canDeleteList.add("5");
        canDeleteList.add("6");
        canDeleteList.add("7");

        String sql = "select * from DCP_APBILL a where a.eid='" + req.geteId() + "' " +
                " and a.accountid='" + req.getRequest().getAccountId() + "' and a.apno='" + req.getRequest().getApNo() + "' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if (list.size() > 0) {
            String status = list.get(0).get("STATUS").toString();
            String taskId = list.get(0).get("TASKID").toString();
            if (!canDeleteList.contains(taskId)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "该单据不可做删除".toString());
            }
            if (!status.equals("1")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "【未审核】状态下才可做删除".toString());
            }

            if (CollUtil.isEmpty(itemList)) {
                //4. 删除涉及的表：DCP_APBILL 应付单单据，
                DelBean db1 = new DelBean("DCP_APBILL");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                db1.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
                // DCP_APBILLDETAIL应付单明细，
                DelBean db2 = new DelBean("DCP_APBILLDETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                db2.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                DelBean db21 = new DelBean("DCP_APBILLDETAILSUM");
                db21.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db21.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                db21.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db21));

                // DCP_APPERD应付单账期，
                DelBean db3 = new DelBean("DCP_APPERD");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                db3.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));
                // DCP_APBILLWRITEOFF应付单核销，
                DelBean db4 = new DelBean("DCP_APBILLWRTOFF");
                db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db4.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                db4.addCondition("WRTOFFNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db4));
                // DCP_APBILLPMT应付单付款，
                DelBean db5 = new DelBean("DCP_APBILLPMT");
                db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db5.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                db5.addCondition("WRTOFFNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db5));
                // DCP_APBILLESTDTL应付单冲暂估；
                DelBean db6 = new DelBean("DCP_APBILLESTDTL");
                db6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db6.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                db6.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db6));
                // DCP_PURINV进项发票档；用应付单为关联对应；
                DelBean db7 = new DelBean("DCP_PURINV");
                db7.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db7.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db7));
                //5. 删除成功后，单据来源采购单，需将DCP_PURORDER_PAY 中应付单号+已付金额同步删除

                DelBean db8 = new DelBean("DCP_PURORDER_PAY");
                db8.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db8.addCondition("PAYBILLNO", new DataValue(apNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db8));
            } else {
                for (String item : itemList) {
                    DelBean db2 = new DelBean("DCP_APBILLDETAIL");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
                    db2.addCondition("APNO", new DataValue(apNo, Types.VARCHAR));
                    db2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));
                    //可能删其他表  todo
                }
            }

        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApBillDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApBillDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApBillDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApBillDeleteReq req) throws Exception {
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
    protected TypeToken<DCP_ApBillDeleteReq> getRequestType() {
        return new TypeToken<DCP_ApBillDeleteReq>() {
        };
    }

    @Override
    protected DCP_ApBillDeleteRes getResponseType() {
        return new DCP_ApBillDeleteRes();
    }
}

