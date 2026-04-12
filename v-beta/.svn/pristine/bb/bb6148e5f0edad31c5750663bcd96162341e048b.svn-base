package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BankDeleteReq;
import com.dsc.spos.json.cust.req.DCP_BankDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_BankDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

public class DCP_BankDelete extends SPosAdvanceService<DCP_BankDeleteReq, DCP_BankDeleteRes> {

  @Override
  protected void processDUID(DCP_BankDeleteReq req, DCP_BankDeleteRes res) throws Exception {
    // TODO Auto-generated method stub
    try {
      List<level1Elm> bankList = req.getRequest().getBanklist();
      String eId = req.geteId();
      for (level1Elm bank : bankList) {
        DelBean db1 = new DelBean("DCP_BANK");
        DelBean db2 = new DelBean("DCP_BANK_LANG");
        db1.addCondition("BANKNO", new DataValue(bank.getBankcode(), Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("BANKNO", new DataValue(bank.getBankcode(), Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));
        this.addProcessData(new DataProcessBean(db2));
      }

      this.doExecuteDataToDB();

      res.setSuccess(true);
      res.setServiceStatus("000");
      res.setServiceDescription("服务执行成功");

    } catch (Exception e) {
      // TODO: handle exception
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
    }
  }

  @Override
  protected List<InsBean> prepareInsertData(DCP_BankDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<UptBean> prepareUpdateData(DCP_BankDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<DelBean> prepareDeleteData(DCP_BankDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isVerifyFail(DCP_BankDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");
    if (req.getRequest() == null) {
      errMsg.append("request不能为空值 ");
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    for (level1Elm par : req.getRequest().getBanklist()) {
      String bankNo = par.getBankcode();

      if (Check.Null(bankNo)) {
        errMsg.append("网点编号不能为空值 ");
        isFail = true;
      }

    }

    if (isFail) {
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    return isFail;
  }

  @Override
  protected TypeToken<DCP_BankDeleteReq> getRequestType() {
    // TODO Auto-generated method stub
    return new TypeToken<DCP_BankDeleteReq>() {
    };
  }

  @Override
  protected DCP_BankDeleteRes getResponseType() {
    // TODO Auto-generated method stub
    return new DCP_BankDeleteRes();
  }

}
