package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AccountDeleteReq;
import com.dsc.spos.json.cust.req.DCP_AccountDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AccountDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_AccountDelete extends SPosAdvanceService<DCP_AccountDeleteReq, DCP_AccountDeleteRes> {

  @Override
  protected void processDUID(DCP_AccountDeleteReq req, DCP_AccountDeleteRes res) throws Exception {
    // TODO Auto-generated method stub
    try {
      List<level1Elm> accountList = req.getRequest().getAccountlist();
      String eId = req.geteId();
      for (level1Elm account : accountList) {
        DelBean db1 = new DelBean("DCP_ACCOUNT");
        db1.addCondition("ACCOUNTNO", new DataValue(account.getAccountCode(), Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_ACCOUNT_LANG");

        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ACCOUNTNO", new DataValue(account.getAccountCode(), Types.VARCHAR));
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
  protected List<InsBean> prepareInsertData(DCP_AccountDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<UptBean> prepareUpdateData(DCP_AccountDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<DelBean> prepareDeleteData(DCP_AccountDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isVerifyFail(DCP_AccountDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");

    if (req.getRequest() == null) {
      errMsg.append("request不能为空值 ");
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    for (level1Elm par : req.getRequest().getAccountlist()) {
      String accountCode = par.getAccountCode();

      if (Check.Null(accountCode)) {
        errMsg.append("账户编号不能为空值 ");
        isFail = true;
      }

      //  校验已存在关联组织缴款账户数据，提示不可删除！
      if(isShopAccountExist(accountCode)){
        errMsg.append("账户编号:"+accountCode+"已存在关联组织缴款账户数据，不可删除！ ");
        isFail=true;
      }
    }

    if (isFail) {
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    return isFail;
  }

  @Override
  protected TypeToken<DCP_AccountDeleteReq> getRequestType() {
    // TODO Auto-generated method stub
    return new TypeToken<DCP_AccountDeleteReq>() {
    };
  }

  @Override
  protected DCP_AccountDeleteRes getResponseType() {
    // TODO Auto-generated method stub
    return new DCP_AccountDeleteRes();
  }

  private boolean isShopAccountExist(String account) throws Exception{

    String sql=String.format("select * from DCP_SHOP_ACCOUNT WHERE ACCOUNT='%s'",account);
    List<Map<String, Object>> list = this.doQueryData(sql, null);
    if(list.size()>0){
        return true;
    }
    return false;
  }

}
