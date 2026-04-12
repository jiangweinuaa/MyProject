package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrgAccountEnableReq;
import com.dsc.spos.json.cust.res.DCP_OrgAccountEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_OrgAccouintEnable extends SPosAdvanceService<DCP_OrgAccountEnableReq, DCP_OrgAccountEnableRes> {

    @Override
    protected void processDUID(DCP_OrgAccountEnableReq req, DCP_OrgAccountEnableRes res) throws Exception {
        try {
            String status;//状态：-1未启用100已启用 0已禁用

            if ("1".equals(req.getRequest().getOprType())) { //操作类型：1-启用2-禁用
                status = "100";
            } else {
                status = "0";
            }

            for (DCP_OrgAccountEnableReq.OrgAccount par : req.getRequest().getOrgAccountList()) {
                String account = par.getAccount();
                String bankNo = par.getBankNo();
                String shopID = par.getShopID();
                UptBean up1 = new UptBean("DCP_SHOP_ACCOUNT");
                up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                up1.addCondition("ACCOUNT", new DataValue(account, Types.VARCHAR));
                up1.addCondition("BANKNO", new DataValue(bankNo, Types.VARCHAR));
                up1.addCondition("SHOPID", new DataValue(shopID, Types.VARCHAR));

                if (status.equals("0")) {
                    up1.addCondition("STATUS", new DataValue("100", Types.VARCHAR));
                }
                up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(up1));

            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常：" + e.getMessage());

        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrgAccountEnableReq req) throws Exception {

        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrgAccountEnableReq req) throws Exception {

        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrgAccountEnableReq req) throws Exception {

        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrgAccountEnableReq req) throws Exception {

        boolean isFail;
        StringBuilder errMsg = new StringBuilder();

        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getOprType())) {
            errMsg.append("操作类型不可为空值, ");
            isFail = true;
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        List<DCP_OrgAccountEnableReq.OrgAccount> stausList = req.getRequest().getOrgAccountList();

        if (stausList == null || stausList.isEmpty()) {
            errMsg.append("银行列表不可为空, ");
            isFail = true;
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (DCP_OrgAccountEnableReq.OrgAccount par : stausList) {

            if (Check.Null(par.getShopID())) {
                errMsg.append("组织编码啊不可为空值, ");
                isFail = true;
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
            if (Check.Null(par.getAccount())) {
                errMsg.append("银行账号不可为空值, ");
                isFail = true;
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }

            if (Check.Null(par.getBankNo())) {
                errMsg.append("银行编号不可为空值, ");
                isFail = true;
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_OrgAccountEnableReq> getRequestType() {

        return new TypeToken<DCP_OrgAccountEnableReq>() {
        };
    }

    @Override
    protected DCP_OrgAccountEnableRes getResponseType() {
        return new DCP_OrgAccountEnableRes();
    }

}
