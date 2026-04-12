package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BankCreateReq;
import com.dsc.spos.json.cust.res.DCP_BankCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_BankCreate extends SPosAdvanceService<DCP_BankCreateReq, DCP_BankCreateRes> {

    @Override
    protected void processDUID(DCP_BankCreateReq req, DCP_BankCreateRes res) throws Exception {
        String sql = null;
        try {
            String nation = req.getRequest().getNation();
            String bankCode = req.getRequest().getBankCode();
            String status = req.getRequest().getStatus();
            String ebankCode = req.getRequest().getEbankCode();
            String eId = req.geteId();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            sql = this.isRepeat(eId, bankCode);
            List<Map<String, Object>> bankDatas = this.doQueryData(sql, null);
            if (bankDatas.isEmpty()) {
                // 新增原因码多语言表
                List<DCP_BankCreateReq.BankNameLang> nameLang = req.getRequest().getBankName_lang();
                List<DCP_BankCreateReq.BankFNameLang> fNameLang = req.getRequest().getBankFname_lang();

                if (nameLang != null && nameLang.size() > 0) {
                    for (DCP_BankCreateReq.BankNameLang par : nameLang) {
                        String langType = par.getLangType();
                        String shortName = par.getName();
                        String fullName = "";
                        for (DCP_BankCreateReq.BankFNameLang par1 : fNameLang) {
                            if (langType.equals(par1.getLangType())) {
                                fullName = par1.getName();
                                break;
                            }
                        }

                        ColumnDataValue langData = new ColumnDataValue();
                        langData.add("BANKNO", DataValues.newString(bankCode));
                        langData.add("SHORTNAME", DataValues.newString(shortName));
                        langData.add("LANG_TYPE", DataValues.newString(langType));
                        langData.add("EID", DataValues.newString(eId));
                        langData.add("FULLNAME", DataValues.newString(fullName));

                        // 添加原因码多语言信息
                        InsBean ib2 = DataBeans.getInsBean("DCP_BANK_LANG", langData);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }

                ColumnDataValue bankData = new ColumnDataValue();
                bankData.add("BANKNO", DataValues.newString(bankCode));
                bankData.add("EID", DataValues.newString(eId));
                bankData.add("NATION", DataValues.newString(nation));
                bankData.add("EBANKNO", DataValues.newString(ebankCode));
                bankData.add("STATUS", DataValues.newInteger(status));
                bankData.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                bankData.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                bankData.add("CREATETIME", DataValues.newDate(lastmoditime));
                bankData.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                bankData.add("LASTMODITIME", DataValues.newDate(lastmoditime));

                InsBean ib1 = DataBeans.getInsBean("DCP_BANK", bankData);
                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            } else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败: 单位信息：" + bankCode + "已存在 ");
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
    protected List<InsBean> prepareInsertData(DCP_BankCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankCreateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_BankCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        String bankCode = req.getRequest().getBankCode();
        List<DCP_BankCreateReq.BankNameLang> nameLang = req.getRequest().getBankName_lang();
        List<DCP_BankCreateReq.BankFNameLang> fNameLang = req.getRequest().getBankFname_lang();

        if (nameLang == null || null == fNameLang || nameLang.isEmpty() || fNameLang.isEmpty()) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (nameLang.size() != fNameLang.size()) {
            errMsg.append("多语音全称、简称无法匹配");
            isFail = true;
        }

        if (Check.Null(bankCode)) {
            errMsg.append("银行网点编号不能为空值 ");
            isFail = true;
        }

        for (DCP_BankCreateReq.BankNameLang par : nameLang) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

        for (DCP_BankCreateReq.BankFNameLang par : fNameLang) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言全称类型不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BankCreateReq> getRequestType() {
        return new TypeToken<DCP_BankCreateReq>() {
        };
    }

    @Override
    protected DCP_BankCreateRes getResponseType() {
        return new DCP_BankCreateRes();
    }


    /**
     * 判断单位信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_BANK WHERE EID='%s' AND BANKNO='%s' ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
