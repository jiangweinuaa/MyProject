package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PayDateDeleteReq;
import com.dsc.spos.json.cust.req.DCP_PayDateDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PayDateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

/**
 * 结算日条件 DCP_PayDate
 * @date   2024-09-21
 * @author 01029 
 */
public class DCP_PayDateDelete extends SPosAdvanceService<DCP_PayDateDeleteReq, DCP_PayDateDeleteRes> {

  @Override
  protected void processDUID(DCP_PayDateDeleteReq req, DCP_PayDateDeleteRes res) throws Exception {
    // TODO Auto-generated method stub
    try {
      List<level1Elm> pList = req.getRequest().getDeleteList();
      String eId = req.geteId();
      for (level1Elm p : pList) {
        DelBean db1 = new DelBean("DCP_PayDate");
        DelBean db2 = new DelBean("DCP_PayDate_LANG");
        db1.addCondition("PayDateNO", new DataValue(p.getPayDateNo(), Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("PAYDATE_TYPE", new DataValue(p.getPayDateType(), Types.VARCHAR));
        db2.addCondition("PayDateNO", new DataValue(p.getPayDateNo(), Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("PAYDATE_TYPE", new DataValue(p.getPayDateType(), Types.VARCHAR));
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
  protected List<InsBean> prepareInsertData(DCP_PayDateDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<UptBean> prepareUpdateData(DCP_PayDateDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<DelBean> prepareDeleteData(DCP_PayDateDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isVerifyFail(DCP_PayDateDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");
    if (req.getRequest() == null) {
      errMsg.append("request不能为空值 ");
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    for (level1Elm par : req.getRequest().getDeleteList()) {
      String iCode = par.getPayDateNo();

      if (Check.Null(iCode)) {
        errMsg.append("收付款条件编号不能为空值 ");
        isFail = true;
      }

    }

    if (isFail) {
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    return isFail;
  }

  @Override
  protected TypeToken<DCP_PayDateDeleteReq> getRequestType() {
    // TODO Auto-generated method stub
    return new TypeToken<DCP_PayDateDeleteReq>() {
    };
  }

  @Override
  protected DCP_PayDateDeleteRes getResponseType() {
    // TODO Auto-generated method stub
    return new DCP_PayDateDeleteRes();
  }

}
