package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InvoiceTypeDeleteReq;
import com.dsc.spos.json.cust.req.DCP_InvoiceTypeDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_InvoiceTypeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

/**
 * 发票类型
 * @date   2024-09-13
 * @author 01029 
 */
public class DCP_InvoiceTypeDelete extends SPosAdvanceService<DCP_InvoiceTypeDeleteReq, DCP_InvoiceTypeDeleteRes> {

  @Override
  protected void processDUID(DCP_InvoiceTypeDeleteReq req, DCP_InvoiceTypeDeleteRes res) throws Exception {
    // TODO Auto-generated method stub
    try {
      List<level1Elm> invoiceList = req.getRequest().getInvoiceTypeList();
      String eId = req.geteId();
      for (level1Elm invoice : invoiceList) {
        DelBean db1 = new DelBean("DCP_INVOICETYPE");
        DelBean db2 = new DelBean("DCP_INVOICETYPE_LANG");
        db1.addCondition("INVOICECODE", new DataValue(invoice.getInvoiceCode(), Types.VARCHAR));
        db1.addCondition("TAXAREA", new DataValue(invoice.getTaxArea(), Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("INVOICECODE", new DataValue(invoice.getInvoiceCode(), Types.VARCHAR));
        db2.addCondition("TAXAREA", new DataValue(invoice.getTaxArea(), Types.VARCHAR));
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
  protected List<InsBean> prepareInsertData(DCP_InvoiceTypeDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<UptBean> prepareUpdateData(DCP_InvoiceTypeDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<DelBean> prepareDeleteData(DCP_InvoiceTypeDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isVerifyFail(DCP_InvoiceTypeDeleteReq req) throws Exception {
    // TODO Auto-generated method stub
    boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");
    if (req.getRequest() == null) {
      errMsg.append("request不能为空值 ");
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    for (level1Elm par : req.getRequest().getInvoiceTypeList()) {
      String invoiceCode = par.getInvoiceCode();

      if (Check.Null(invoiceCode)) {
        errMsg.append("发票类型不能为空值 ");
        isFail = true;
      }

    }

    if (isFail) {
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    return isFail;
  }

  @Override
  protected TypeToken<DCP_InvoiceTypeDeleteReq> getRequestType() {
    // TODO Auto-generated method stub
    return new TypeToken<DCP_InvoiceTypeDeleteReq>() {
    };
  }

  @Override
  protected DCP_InvoiceTypeDeleteRes getResponseType() {
    // TODO Auto-generated method stub
    return new DCP_InvoiceTypeDeleteRes();
  }

}
