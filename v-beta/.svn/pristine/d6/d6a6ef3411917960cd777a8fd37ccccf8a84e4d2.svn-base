package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BankAccountCreateReq;
import com.dsc.spos.json.cust.res.DCP_BankAccountCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_BankAccountCreate extends SPosAdvanceService<DCP_BankAccountCreateReq, DCP_BankAccountCreateRes> {

    @Override
    protected void processDUID(DCP_BankAccountCreateReq req, DCP_BankAccountCreateRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        try {
            String eId = req.geteId();
            DCP_BankAccountCreateReq.levelRequest request = req.getRequest();
            String accountCode = request.getAccountCode();//银行账户编码
            String bankCode = request.getBankCode();//网点编号
            String accountName = request.getAccountName();//户名
            String organizationNo = request.getOrganizationNo();//开户人(组织)
            String bankAccount = request.getBankAccount();//银行账号
            String status = request.getStatus();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            sql = this.isRepeat(eId, accountCode);
            List<Map<String, Object>> accountDatas = this.doQueryData(sql, null);
            if (accountDatas.isEmpty()) {
                // 新增原因码多语言表
                List<DCP_BankAccountCreateReq.level1Elm> datas = request.getAccountName_lang();
                if (datas != null && datas.size() > 0) {
                    for (DCP_BankAccountCreateReq.level1Elm par : datas) {

                        String langType = par.getLangType();
                        String name = par.getName();

                        ColumnDataValue lang = new ColumnDataValue();
                        lang.add("EID", DataValues.newString(eId));
                        lang.add("LANG_TYPE", DataValues.newString(langType));
                        lang.add("NAME", DataValues.newString(name));
                        lang.add("ACCOUNTNO", DataValues.newString(accountCode));


                        // 添加原因码多语言信息
                        InsBean ib2 = DataBeans.getInsBean("DCP_ACCOUNT_LANG", lang);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }
                ColumnDataValue dataValue = new ColumnDataValue();

                dataValue.add("EID", DataValues.newString(eId));
                dataValue.add("ACCOUNTNO", DataValues.newString(accountCode));
                dataValue.add("ORGANIZATION", DataValues.newString(organizationNo));
                dataValue.add("BANKNO", DataValues.newString(bankCode));
                dataValue.add("BANKACCOUNT", DataValues.newString(bankAccount));
                dataValue.add("ACCOUNTNAME", DataValues.newString(accountName));
                dataValue.add("STATUS", DataValues.newInteger(status));
                dataValue.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dataValue.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dataValue.add("CREATETIME", DataValues.newDate(lastmoditime));
                dataValue.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                dataValue.add("LASTMODITIME", DataValues.newDate(lastmoditime));

                InsBean ib1 = DataBeans.getInsBean("DCP_ACCOUNT", dataValue);

                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            } else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败: 账户信息：" + accountCode + "已存在 ");
                return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BankAccountCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankAccountCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankAccountCreateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_BankAccountCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        DCP_BankAccountCreateReq.levelRequest request = req.getRequest();
        String accountCode = request.getAccountCode();
        String organizationNo = request.getOrganizationNo();
        String bankAccount = request.getBankAccount();
        String bankCode = request.getBankCode();
        String status = request.getStatus();
        List<DCP_BankAccountCreateReq.level1Elm> datas = request.getAccountName_lang();

        if (datas == null) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_BankAccountCreateReq.level1Elm par : datas) {
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
    protected TypeToken<DCP_BankAccountCreateReq> getRequestType() {
        return new TypeToken<DCP_BankAccountCreateReq>() {
        };
    }

    @Override
    protected DCP_BankAccountCreateRes getResponseType() {
        return new DCP_BankAccountCreateRes();
    }


    /**
     * 检查账户是否重复的辅助方法。
     * 通过传入的员工ID（EID）和账号编号（ACCOUNTNO）查询DCP_ACCOUNT表中是否存在相同的记录。
     * 这个方法主要用于数据验证，确保新增或更新的账户信息不与现有数据冲突。
     *
     * @param key 一个包含两个字符串元素的可变参数，第一个元素是员工ID（EID），第二个元素是账号编号（ACCOUNTNO）。
     * @return 返回一个格式化的SQL查询语句，用于查询指定EID和ACCOUNTNO的记录是否存在。
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_ACCOUNT WHERE EID='%s' AND ACCOUNTNO='%s' ";
        sql = String.format(sql, key);
        return sql;
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
	
