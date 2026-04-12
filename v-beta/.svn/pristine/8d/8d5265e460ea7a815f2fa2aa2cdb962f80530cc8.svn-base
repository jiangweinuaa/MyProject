package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_AccountCreateReq;
import com.dsc.spos.json.cust.res.DCP_AccountCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class DCP_AccountCreate extends SPosAdvanceService<DCP_AccountCreateReq, DCP_AccountCreateRes> {

    @Override
    protected void processDUID(DCP_AccountCreateReq req, DCP_AccountCreateRes res) throws Exception {

        String sql = null;
        try {
            String eId = req.geteId();
            DCP_AccountCreateReq.levelRequest request = req.getRequest();

            String lastmoditime = DateFormatUtils.getNowDateTime();
            sql = this.isRepeat(eId, request.getAccountCode());

            List<Map<String, Object>> accountDatas = this.doQueryData(sql, null);
            if (accountDatas.isEmpty()) {

                ColumnDataValue dcp_account = new ColumnDataValue();
                dcp_account.add("EID", DataValues.newString(eId));
                dcp_account.add("ACCOUNTNO", DataValues.newString(request.getAccountCode()));
                dcp_account.add("ORGANIZATION", DataValues.newString(request.getOrganizationNo()));
                dcp_account.add("BANKNO", DataValues.newString(request.getBankCode()));
                dcp_account.add("BANKACCOUNT", DataValues.newString(request.getBankAccount()));
                dcp_account.add("ACCOUNTNAME", DataValues.newString(request.getAccountName()));
                dcp_account.add("STATUS", DataValues.newString(request.getStatus()));
                dcp_account.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_account.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dcp_account.add("CREATETIME", DataValues.newDate(lastmoditime));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ACCOUNT", dcp_account))); // 新增單頭

                // 新增原因码多语言表
                List<DCP_AccountCreateReq.level1Elm> datas = request.getAccountName_lang();
                if (datas != null && !datas.isEmpty()) {
                    for (DCP_AccountCreateReq.level1Elm par : datas) {

                        ColumnDataValue dcp_account_lang = new ColumnDataValue();
                        dcp_account_lang.add("EID", DataValues.newString(eId));
                        dcp_account_lang.add("ACCOUNTNO", DataValues.newString(request.getAccountCode()));
                        dcp_account_lang.add("LANG_TYPE", DataValues.newString(par.getLangType()));
                        dcp_account_lang.add("NAME", DataValues.newString(par.getName()));


                        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ACCOUNT_LANG", dcp_account_lang)));
                    }
                }

                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            } else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败: 单位信息：" + request.getBankCode() + "已存在 ");
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
    protected List<InsBean> prepareInsertData(DCP_AccountCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AccountCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AccountCreateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_AccountCreateReq req) throws Exception {

        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder();


        return isFail;
    }

    @Override
    protected TypeToken<DCP_AccountCreateReq> getRequestType() {
        return new TypeToken<DCP_AccountCreateReq>() {
        };
    }

    @Override
    protected DCP_AccountCreateRes getResponseType() {
        return new DCP_AccountCreateRes();
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
	
