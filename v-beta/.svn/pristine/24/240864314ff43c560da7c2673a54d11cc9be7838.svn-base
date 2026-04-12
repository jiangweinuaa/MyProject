package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BillDateEnableReq;
import com.dsc.spos.json.cust.req.DCP_BillDateEnableReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_BillDateEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 结算日期
 * @date   2024-09-19
 * @author 01029 
 */
public class DCP_BillDateEnable extends SPosAdvanceService<DCP_BillDateEnableReq, DCP_BillDateEnableRes> {

  @Override
  protected void processDUID(DCP_BillDateEnableReq req, DCP_BillDateEnableRes res) throws Exception {
   
    try {
 
      String eId = req.geteId();
      String status = "100";//状态：-1未启用100已启用 0已禁用
		
		if(req.getRequest().getOprType().equals("1"))//操作类型：1-启用2-禁用
		{
			status = "100";
		}
		else
		{
			status = "0";
		}
		
		for (level1Elm par : req.getRequest().getEnableList()) 
		{
			String keyNo = par.getBillDateNo();
	 
			String oPId = req.getEmployeeNo();
			UptBean up1 = new UptBean("DCP_BillDate");
			up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			up1.addCondition("BILLDATENO", new DataValue(keyNo, Types.VARCHAR));
	 
			up1.addUpdateValue("LASTMODIOPID", new DataValue(oPId, Types.VARCHAR));	
			
			String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));	
			if(status.equals("0"))
			{
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
       
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
    }
  }

  @Override
  protected List<InsBean> prepareInsertData(DCP_BillDateEnableReq req) throws Exception {
   
    return null;
  }

  @Override
  protected List<UptBean> prepareUpdateData(DCP_BillDateEnableReq req) throws Exception {
 
    return null;
  }

  @Override
  protected List<DelBean> prepareDeleteData(DCP_BillDateEnableReq req) throws Exception {
     
    return null;
  }

  @Override
  protected boolean isVerifyFail(DCP_BillDateEnableReq req) throws Exception {
 
    boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");
    if (req.getRequest() == null) {
      errMsg.append("request不能为空值 ");
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    if (req.getRequest().getOprType() == null) {
        errMsg.append("操作类型不能为空值 ");
        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
      }
    for (level1Elm par : req.getRequest().getEnableList()) {
      String sCode = par.getBillDateNo();

      if (Check.Null(sCode)) {
        errMsg.append("结算日条件编号不能为空值 ");
        isFail = true;
      }

    }

    if (isFail) {
      throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
    return isFail;
  }

  @Override
  protected TypeToken<DCP_BillDateEnableReq> getRequestType() {
     
    return new TypeToken<DCP_BillDateEnableReq>() {
    };
  }

  @Override
  protected DCP_BillDateEnableRes getResponseType() {
  
    return new DCP_BillDateEnableRes();
  }

}
