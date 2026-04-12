package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockOutTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockOutTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_StockOutTemplateDelete
 *    說明：调拨模板删除
 * 服务说明：调拨模板删除
 * @author 袁云洋 
 * @since  2019-12-18
 */
public class DCP_StockOutTemplateDelete extends SPosAdvanceService<DCP_StockOutTemplateDeleteReq, DCP_StockOutTemplateDeleteRes> 
{

	@Override
	protected boolean isVerifyFail(DCP_StockOutTemplateDeleteReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String templateNo = req.getRequest().getTemplateNo();
		
		if (Check.Null(templateNo)) {
			errCt++;
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		} 

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockOutTemplateDeleteReq> getRequestType() {
		return new TypeToken<DCP_StockOutTemplateDeleteReq>(){};
	}

	@Override
	protected DCP_StockOutTemplateDeleteRes getResponseType() {
		return new DCP_StockOutTemplateDeleteRes();
	}

	@Override
	protected void processDUID(DCP_StockOutTemplateDeleteReq req,DCP_StockOutTemplateDeleteRes res) throws Exception {		
		String eId = req.geteId();
		String createBy = req.getOpNO();
		String templateNo = req.getRequest().getTemplateNo();
		
		String shopType = req.getRequest().getShopType();
		//向下兼容，前端可能没更新，默认值2
		if(shopType==null||shopType.isEmpty())
		{
			shopType = "2";
		}
		
		try 
		{
			//DCP_PTEMPLATE
			DelBean db1 = new DelBean("DCP_PTEMPLATE");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PTEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			db1.addCondition("DOC_TYPE", new DataValue("5", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			//DCP_PTEMPLATE_DETAIL
			DelBean db2 = new DelBean("DCP_PTEMPLATE_DETAIL");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("PTEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			db2.addCondition("DOC_TYPE", new DataValue("5", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));

			//DCP_PTEMPLATE_SHOP
			DelBean db3 = new DelBean("DCP_PTEMPLATE_SHOP");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("PTEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			db3.addCondition("DOC_TYPE", new DataValue("5", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));

			this.doExecuteDataToDB();	
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockOutTemplateDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockOutTemplateDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockOutTemplateDeleteReq req) throws Exception {
		return null;
	}	

	@Override
	protected String getQuerySql(DCP_StockOutTemplateDeleteReq req) throws Exception {
		return null;
	}


}
