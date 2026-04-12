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
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_BankUpdate extends SPosAdvanceService<DCP_BankCreateReq, DCP_BankCreateRes> {

    @Override
    protected void processDUID(DCP_BankCreateReq req, DCP_BankCreateRes res) throws Exception {
        // TODO Auto-generated method stub
        try {
            String nation = req.getRequest().getNation();
            String bankCode = req.getRequest().getBankCode();
            String status = req.getRequest().getStatus();
            String ebankCode = req.getRequest().getEbankCode();
            String eId = req.geteId();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


            List<DCP_BankCreateReq.BankNameLang> nameLang = req.getRequest().getBankName_lang();
            List<DCP_BankCreateReq.BankFNameLang> fNameLang = req.getRequest().getBankFname_lang();
            ColumnDataValue condition = new ColumnDataValue();

            condition.add("BANKNO", DataValues.newString(bankCode));
            condition.add("EID", DataValues.newString(eId));
            DelBean db2 = DataBeans.getDelBean("DCP_BANK_LANG",condition);

            this.addProcessData(new DataProcessBean(db2));

            String[] columnsName = {"BANKNO", "SHORTNAME", "LANG_TYPE", "EID", "FULLNAME"};
            if (nameLang != null && nameLang.size() > 0) {
                for (DCP_BankCreateReq.BankNameLang par : nameLang) {
                    int insColCt = 0;
                    // 获取
                    String shortName = par.getName();
                    String fullName = "";
                    String langType = par.getLangType();

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

                    InsBean ib2 = DataBeans.getInsBean("DCP_BANK_LANG", langData);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }

            ColumnDataValue bankData = new ColumnDataValue();
            bankData.add("NATION", DataValues.newString(nation));
            bankData.add("EBANKNO", DataValues.newString(ebankCode));
            bankData.add("STATUS", DataValues.newInteger(status));
            bankData.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            bankData.add("LASTMODITIME", DataValues.newDate(lastmoditime));

            UptBean ub1 = DataBeans.getUptBean("DCP_BANK",condition,bankData);

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
    protected List<InsBean> prepareInsertData(DCP_BankCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BankCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        String bankNo = req.getRequest().getBankCode();
        List<DCP_BankCreateReq.BankNameLang> nameLang = req.getRequest().getBankName_lang();
        List<DCP_BankCreateReq.BankFNameLang> fNameLang = req.getRequest().getBankFname_lang();

        if (nameLang == null) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_BankCreateReq.BankNameLang par : nameLang) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

        for (DCP_BankCreateReq.BankFNameLang par : fNameLang) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

        if (Check.Null(bankNo)) {
            errMsg.append("银行网点编号不能为空值 ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BankCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_BankCreateReq>() {
        };
    }

    @Override
    protected DCP_BankCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_BankCreateRes();
    }
}
