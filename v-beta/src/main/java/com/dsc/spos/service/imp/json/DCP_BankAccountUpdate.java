package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BankAccountUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BankAccountUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_BankAccountUpdate extends SPosAdvanceService<DCP_BankAccountUpdateReq, DCP_BankAccountUpdateRes> {

    @Override
    protected void processDUID(DCP_BankAccountUpdateReq req, DCP_BankAccountUpdateRes res) throws Exception {
        // TODO Auto-generated method stub
        try {
            String eId = req.geteId();
            DCP_BankAccountUpdateReq.levelRequest request = req.getRequest();
            String accountCode = request.getAccountCode();//银行账户编码
            String bankCode = request.getBankCode();//网点编号
            String accountName = request.getAccountName();//户名
            String organizationNo = request.getOrganizationNo();//开户人(组织)
            String bankAccount = request.getBankAccount();//银行账号
            String status = request.getStatus();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


            List<DCP_BankAccountUpdateReq.level1Elm> datas = req.getRequest().getAccountName_lang();
            DelBean db2 = new DelBean("DCP_ACCOUNT_LANG");
            db2.addCondition("ACCOUNTNO", new DataValue(accountCode, Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db2));
            if (datas != null && datas.size() > 0) {
                for (DCP_BankAccountUpdateReq.level1Elm par : datas) {
                    int insColCt = 0;
                    String[] columnsName = {"ACCOUNTNO", "NAME", "LANG_TYPE", "EID"};
                    // 获取
                    String name = par.getName();
                    String langType = par.getLangType();
                    DataValue[] insValueDetail = new DataValue[]
                            {
                                    new DataValue(accountCode, Types.VARCHAR),
                                    new DataValue(name, Types.VARCHAR),
                                    new DataValue(langType, Types.VARCHAR),
                                    new DataValue(eId, Types.VARCHAR)
                            };


                    InsBean ib2 = new InsBean("DCP_ACCOUNT_LANG", columnsName);
                    ib2.addValues(insValueDetail);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_ACCOUNT");
            //add Value
            ub1.addUpdateValue("ACCOUNTNO", new DataValue(accountCode, Types.VARCHAR));
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("ORGANIZATION", new DataValue(organizationNo, Types.VARCHAR));

            ub1.addUpdateValue("BANKNO", new DataValue(bankCode, Types.VARCHAR));
            ub1.addUpdateValue("BANKACCOUNT", new DataValue(bankAccount, Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNTNAME", new DataValue(accountName, Types.VARCHAR));


            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
            //condition
            ub1.addCondition("ACCOUNTNO", new DataValue(accountCode, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BankAccountUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankAccountUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankAccountUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BankAccountUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        DCP_BankAccountUpdateReq.levelRequest request = req.getRequest();
        String accountCode = request.getAccountCode();
        String organizationNo = request.getOrganizationNo();
        String bankAccount = request.getBankAccount();
        String bankCode = request.getBankCode();
        String status = request.getStatus();
        List<DCP_BankAccountUpdateReq.level1Elm> datas = request.getAccountName_lang();

        if (datas == null) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_BankAccountUpdateReq.level1Elm par : datas) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

        if (Check.Null(accountCode)) {
            errMsg.append("账户编号编号不能为空值 ");
            isFail = true;
        }
        if (Check.Null(organizationNo)) {
            errMsg.append("开户人(组织)不能为空值 ");
            isFail = true;
        }
        if (Check.Null(bankAccount)) {
            errMsg.append("银行账号不能为空值 ");
            isFail = true;
        }
        if (Check.Null(bankCode)) {
            errMsg.append("网点编号不能为空值 ");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("状态不能为空值 ");
            isFail = true;
        }

        //校验网点编号是否存在
        if (!isBankExist(bankCode)) {
            errMsg.append("网点编号不存在 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BankAccountUpdateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BankAccountUpdateReq>() {
        };
    }

    @Override
    protected DCP_BankAccountUpdateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BankAccountUpdateRes();
    }

    private boolean isBankExist(String... key) {
        String sql = null;

        try {
            sql = " SELECT * FROM DCP_BANK WHERE BANKNO='%s' ";
            sql = String.format(sql, key);
            List<Map<String, Object>> bankDatas = this.doQueryData(sql, null);
            if (bankDatas != null && bankDatas.size() > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


}
