package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BankEnableReq;
import com.dsc.spos.json.cust.res.DCP_BankEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_BankEnable extends SPosAdvanceService<DCP_BankEnableReq, DCP_BankEnableRes> {

  @Override
  protected void processDUID(DCP_BankEnableReq req, DCP_BankEnableRes res) throws Exception {
    try {
      String status;//状态：-1未启用100已启用 0已禁用

      if ("1".equals(req.getRequest().getOprType())) { //操作类型：1-启用2-禁用
        status = "100";
      } else {
        status = "0";
      }

      for (DCP_BankEnableReq.Bank par : req.getRequest().getBankList()) {
        String bankCode = par.getBankCode();
        UptBean up1 = new UptBean("DCP_BANK");
        up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        up1.addCondition("BANKNO", new DataValue(bankCode, Types.VARCHAR));

        up1.addCondition("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        up1.addCondition("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

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
  protected List<InsBean> prepareInsertData(DCP_BankEnableReq req) throws Exception {

    return null;
  }

  @Override
  protected List<UptBean> prepareUpdateData(DCP_BankEnableReq req) throws Exception {

    return null;
  }

  @Override
  protected List<DelBean> prepareDeleteData(DCP_BankEnableReq req) throws Exception {

    return null;
  }

  @Override
  protected boolean isVerifyFail(DCP_BankEnableReq req) throws Exception {

    boolean isFail;
    StringBuilder errMsg = new StringBuilder();

    if (req.getRequest() == null) {
      errMsg.append("request不能为空值 ");
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }

    if (Check.Null(req.getRequest().getOprType())) {
      errMsg.append("操作类型不可为空值, ");
      isFail = true;
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }

    List<DCP_BankEnableReq.Bank> stausList = req.getRequest().getBankList();

    if (stausList == null || stausList.isEmpty()) {
      errMsg.append("网点列表不可为空, ");
      isFail = true;
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    for (DCP_BankEnableReq.Bank par : stausList) {
      if (Check.Null(par.getBankCode())) {
        errMsg.append("网点编码不可为空值, ");
        isFail = true;
        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
      }
    }

    return false;
  }

  @Override
  protected TypeToken<DCP_BankEnableReq> getRequestType() {

    return new TypeToken<DCP_BankEnableReq>() {
    };
  }

  @Override
  protected DCP_BankEnableRes getResponseType() {
    return new DCP_BankEnableRes();
  }

}
