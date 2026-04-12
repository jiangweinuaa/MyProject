package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AccountUpdateReq;
import com.dsc.spos.json.cust.req.DCP_AccountUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AccountUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_AccountUpdate extends SPosAdvanceService<DCP_AccountUpdateReq, DCP_AccountUpdateRes> {

    @Override
    protected void processDUID(DCP_AccountUpdateReq req, DCP_AccountUpdateRes res) throws Exception {

        try {
            String eId = req.geteId();
            DCP_AccountUpdateReq.levelRequest request = req.getRequest();

            String lastmoditime = DateFormatUtils.getNowDateTime();

            List<level1Elm> datas = req.getRequest().getAccountName_lang();
            DelBean db2 = new DelBean("DCP_ACCOUNT_LANG");
            db2.addCondition("ACCOUNTNO", new DataValue(request.getAccountCode(), Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db2));
            // 新增原因码多语言表
            if (datas != null && !datas.isEmpty()) {
                for (DCP_AccountUpdateReq.level1Elm par : datas) {

                    ColumnDataValue dcp_account_lang = new ColumnDataValue();
                    dcp_account_lang.add("EID", DataValues.newString(eId));
                    dcp_account_lang.add("ACCOUNTNO", DataValues.newString(request.getAccountCode()));
                    dcp_account_lang.add("LANG_TYPE", DataValues.newString(par.getLangType()));
                    dcp_account_lang.add("NAME", DataValues.newString(par.getName()));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ACCOUNT_LANG", dcp_account_lang)));
                }
            }

            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue dcp_account = new ColumnDataValue();

            condition.add("EID", DataValues.newString(eId));
            condition.add("ACCOUNTNO", DataValues.newString(request.getAccountCode()));

            dcp_account.add("ORGANIZATION", DataValues.newString(request.getOrganizationNo()));
            dcp_account.add("BANKNO", DataValues.newString(request.getBankCode()));
            dcp_account.add("BANKACCOUNT", DataValues.newString(request.getBankAccount()));
            dcp_account.add("ACCOUNTNAME", DataValues.newString(request.getAccountName()));
            dcp_account.add("STATUS", DataValues.newString(request.getStatus()));

            dcp_account.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_account.add("LASTMODITIME", DataValues.newDate(lastmoditime));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_ACCOUNT", condition, dcp_account)));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AccountUpdateReq req) throws Exception {

        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AccountUpdateReq req) throws Exception {

        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AccountUpdateReq req) throws Exception {

        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AccountUpdateReq req) throws Exception {

        boolean isFail = false;

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AccountUpdateReq> getRequestType() {

        return new TypeToken<DCP_AccountUpdateReq>() {
        };
    }

    @Override
    protected DCP_AccountUpdateRes getResponseType() {

        return new DCP_AccountUpdateRes();
    }

//    private boolean isBankExist(String... key) {
//        String sql = null;
//
//        try {
//            sql = " SELECT * FROM DCP_BANK WHERE BANKNO='%s' ";
//            sql = String.format(sql, key);
//            List<Map<String, Object>> bankDatas = this.doQueryData(sql, null);
//            if (bankDatas != null && bankDatas.size() > 0) {
//                return true;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return false;
//    }


}
