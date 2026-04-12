package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BankAccountDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BankAccountDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_BankAccountDelete extends SPosAdvanceService<DCP_BankAccountDeleteReq, DCP_BankAccountDeleteRes> {

  @Override
  protected void processDUID(DCP_BankAccountDeleteReq req, DCP_BankAccountDeleteRes res) throws Exception {

    try {
      List<DCP_BankAccountDeleteReq.level1Elm> accountList = req.getRequest().getAccountlist();
      String eId = req.geteId();
      for (DCP_BankAccountDeleteReq.level1Elm account : accountList) {

        ColumnDataValue dataValue = new ColumnDataValue();
        dataValue.add("ACCOUNTNO", DataValues.newString(account.getAccountCode()));
        dataValue.add("EID", DataValues.newString(eId));
        DelBean db1 = DataBeans.getDelBean("DCP_ACCOUNT",dataValue);
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = DataBeans.getDelBean("DCP_ACCOUNT_LANG",dataValue);
        this.addProcessData(new DataProcessBean(db2));
      }
      this.doExecuteDataToDB();

      res.setSuccess(true);
      res.setServiceStatus("000");
      res.setServiceDescription("服务执行成功");

    } catch (Exception e) {

      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
    }
  }

  @Override
  protected List<InsBean> prepareInsertData(DCP_BankAccountDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<UptBean> prepareUpdateData(DCP_BankAccountDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<DelBean> prepareDeleteData(DCP_BankAccountDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isVerifyFail(DCP_BankAccountDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");

    if (req.getRequest() == null) {
      errMsg.append("request不能为空值 ");
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    for (DCP_BankAccountDeleteReq.level1Elm par : req.getRequest().getAccountlist()) {
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
  protected TypeToken<DCP_BankAccountDeleteReq> getRequestType() {
    // TODO Auto-generated method stub
    return new TypeToken<DCP_BankAccountDeleteReq>() {
    };
  }

  @Override
  protected DCP_BankAccountDeleteRes getResponseType() {
    // TODO Auto-generated method stub
    return new DCP_BankAccountDeleteRes();
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
