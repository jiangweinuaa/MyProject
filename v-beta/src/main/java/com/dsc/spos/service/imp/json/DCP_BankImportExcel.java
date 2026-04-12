package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BankImportExcelReq;
import com.dsc.spos.json.cust.res.DCP_BankImportExcelRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_BankImportExcel extends SPosAdvanceService<DCP_BankImportExcelReq, DCP_BankImportExcelRes> {

    @Override
    protected void processDUID(DCP_BankImportExcelReq req, DCP_BankImportExcelRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        try {

            List<DCP_BankImportExcelReq.Bank> bankList = req.getRequest().getBanklist();
            String eId = req.geteId();
            String langType = req.getLangType();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            res.setTotalRecords(String.valueOf(bankList.size()));
            res.setFailureDatas(new ArrayList<>());
            int successRecord = 0;
            for (DCP_BankImportExcelReq.Bank bank : bankList) {
                try {
                    List<Map<String, Object>> bankData = this.doQueryData(isRepeat(eId, bank.getBankCode()), null);
                    if (!bankData.isEmpty()) {
                        DCP_BankImportExcelRes.FailureData failureData = res.new FailureData();
                        failureData.setBankCode(bank.getBankCode());
                        failureData.setFName(bank.getFname());
                        failureData.setSName(bank.getSname());
                        failureData.setFailureDesc(bank.getBankCode() + "已存在！");
                        res.getFailureDatas().add(failureData);
                        continue;
                    }

                    String[] columnsBank = {"EID", "BANKNO", "NATION", "EBANKNO", "STATUS", "CREATEOPID", "CREATEDEPTID", "CREATETIME", "LASTMODIOPID", "LASTMODITIME"};
                    String[] columnsLang = {"BANKNO", "SHORTNAME", "LANG_TYPE", "EID", "FULLNAME"};

                    InsBean langIns = new InsBean("DCP_BANK_LANG", columnsLang);

                    langIns.addValues(new DataValue[]{
                            new DataValue(bank.getBankCode(), Types.VARCHAR),
                            new DataValue(bank.getSname(), Types.VARCHAR),
                            new DataValue(langType, Types.VARCHAR),
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(bank.getFname(), Types.VARCHAR)
                    });
                    this.addProcessData(new DataProcessBean(langIns));

                    InsBean bankIns = new InsBean("DCP_BANK", columnsBank);
                    bankIns.addValues(new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(bank.getBankCode(), Types.VARCHAR),
                            new DataValue(bank.getNation(), Types.VARCHAR),
                            new DataValue(bank.getEbank(), Types.VARCHAR),
                            new DataValue(100, Types.INTEGER),
                            new DataValue(req.getOpNO(), Types.VARCHAR),
                            new DataValue(req.getDepartmentNo(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                            new DataValue(req.getOpNO(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE)
                    });

                    this.addProcessData(new DataProcessBean(bankIns));
                    successRecord++;
                } catch (Exception e) {
                    DCP_BankImportExcelRes.FailureData failureData = res.new FailureData();
                    failureData.setBankCode(bank.getBankCode());
                    failureData.setFName(bank.getFname());
                    failureData.setSName(bank.getSname());
                    failureData.setFailureDesc(e.getMessage());
                    res.getFailureDatas().add(failureData);
                }

            }
            res.setFailureRecords(String.valueOf(res.getFailureDatas().size()));
            res.setSuccessRecords(String.valueOf(successRecord));

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
    protected List<InsBean> prepareInsertData(DCP_BankImportExcelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankImportExcelReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankImportExcelReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_BankImportExcelReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        List<DCP_BankImportExcelReq.Bank> bankList = req.getRequest().getBanklist();


        if (bankList == null || bankList.isEmpty()) {
            errMsg.append("网点列表不能为空!");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (DCP_BankImportExcelReq.Bank bank : bankList) {
            if (Check.Null(bank.getBankCode())) {
                errMsg.append(bank.getFname()).append("银行网点编号不能为空值 ");
                isFail = true;
            }
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BankImportExcelReq> getRequestType() {
        return new TypeToken<DCP_BankImportExcelReq>() {
        };
    }

    @Override
    protected DCP_BankImportExcelRes getResponseType() {
        return new DCP_BankImportExcelRes();
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
	
