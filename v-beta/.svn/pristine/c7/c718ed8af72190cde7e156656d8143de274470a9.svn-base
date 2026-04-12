package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BankAccountEnableReq;
import com.dsc.spos.json.cust.res.DCP_BankAccountEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_BankAccountEnable extends SPosAdvanceService<DCP_BankAccountEnableReq, DCP_BankAccountEnableRes> {

  @Override
  protected void processDUID(DCP_BankAccountEnableReq req, DCP_BankAccountEnableRes res) throws Exception {
    try {
      String status;//状态：-1未启用100已启用 0已禁用

      if ("1".equals(req.getRequest().getOprType())) { //操作类型：1-启用2-禁用
        status = "100";
      } else {
        status = "0";
      }

      for (DCP_BankAccountEnableReq.Account par : req.getRequest().getAccountList()) {
        String accountCode = par.getAccountCode();
        UptBean up1 = new UptBean("DCP_ACCOUNT");
        up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        up1.addCondition("ACCOUNTNO", new DataValue(accountCode, Types.VARCHAR));
        if (status.equals("0")) {
          up1.addCondition("STATUS", new DataValue("100", Types.VARCHAR));

          //if(isShopAccountExist(accountCode)){
            UptBean up2 = new UptBean("DCP_SHOP_ACCOUNT");
            up2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            up2.addCondition("ACCOUNT", new DataValue(accountCode, Types.VARCHAR));
            up2.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            up2.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            up2.addUpdateValue("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
          //}
            this.addProcessData(new DataProcessBean(up2));

        }
        up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));


        this.addProcessData(new DataProcessBean(up1));

        //禁用：检测账户是否存在有效的【组织缴款账户】（DCP_SHOP_ACCOUNT），若组织数>0，则同步禁用【组织缴款账户】状态


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
  protected List<InsBean> prepareInsertData(DCP_BankAccountEnableReq req) throws Exception {

    return null;
  }

  @Override
  protected List<UptBean> prepareUpdateData(DCP_BankAccountEnableReq req) throws Exception {

    return null;
  }

  @Override
  protected List<DelBean> prepareDeleteData(DCP_BankAccountEnableReq req) throws Exception {

    return null;
  }

  @Override
  protected boolean isVerifyFail(DCP_BankAccountEnableReq req) throws Exception {

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

    List<DCP_BankAccountEnableReq.Account> stausList = req.getRequest().getAccountList();

    if (stausList == null || stausList.isEmpty()) {
      errMsg.append("银行列表不可为空, ");
      isFail = true;
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    for (DCP_BankAccountEnableReq.Account par : stausList) {
      if (Check.Null(par.getAccountCode())) {
        errMsg.append("银行账户编码不可为空值, ");
        isFail = true;
        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
      }
    }

    return false;
  }

  private boolean isShopAccountExist(String account) throws Exception{

    String sql=String.format("select * from DCP_SHOP_ACCOUNT WHERE ACCOUNT='%s'",account);
    List<Map<String, Object>> list = this.doQueryData(sql, null);
    if(list.size()>0){
      return true;
    }
    return false;
  }

  @Override
  protected TypeToken<DCP_BankAccountEnableReq> getRequestType() {

    return new TypeToken<DCP_BankAccountEnableReq>() {
    };
  }

  @Override
  protected DCP_BankAccountEnableRes getResponseType() {
    return new DCP_BankAccountEnableRes();
  }

}
