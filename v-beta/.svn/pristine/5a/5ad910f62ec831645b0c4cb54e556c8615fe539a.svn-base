package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PinPeiDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PinPeiDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_PinPeiDelete
 * 服务说明：拼胚删除
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiDelete extends SPosAdvanceService<DCP_PinPeiDeleteReq,DCP_PinPeiDeleteRes>{

	@Override
	protected void processDUID(DCP_PinPeiDeleteReq req, DCP_PinPeiDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId=req.geteId();
		String shopId=req.getShopId();
		String pinPeiNo=req.getRequest().getPinPeiNo();
		try 
		{
			String sql = " select pinpeino from dcp_pinpei "
					+ " where eid='"+eId+"' and shopid='"+shopId+"' and pinpeino='"+pinPeiNo+"' and status='0' ";
			List<Map<String, Object>> getQData = this.doQueryData(sql,null);
			if (getQData != null && getQData.isEmpty()==false) 
			{
				//删除单据单身
				DelBean db1 = new DelBean("DCP_PINPEI");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("PINPEINO", new DataValue(pinPeiNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				//删除单据单身
				DelBean db2 = new DelBean("DCP_PINPEI_DETAIL");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db2.addCondition("PINPEINO", new DataValue(pinPeiNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");	
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已提交，请重新输入！");	
			}
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PinPeiDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PinPeiDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PinPeiDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PinPeiDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String pinPeiNo=req.getRequest().getPinPeiNo();
		if (Check.Null(pinPeiNo))
		{
			errMsg.append("单据编号不能为空,");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		} 
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PinPeiDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_PinPeiDeleteReq>(){};
	}

	@Override
	protected DCP_PinPeiDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_PinPeiDeleteRes();
	}

}
