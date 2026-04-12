package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PinPeiGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_PinPeiGoodsDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PinPeiGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_PinPeiGoodsDeleteReq
 * 服务说明：拼胚商品删除
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiGoodsDelete extends SPosAdvanceService<DCP_PinPeiGoodsDeleteReq,DCP_PinPeiGoodsDeleteRes>{

	@Override
	protected void processDUID(DCP_PinPeiGoodsDeleteReq req, DCP_PinPeiGoodsDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId=req.geteId();
		try 
		{
			List<level1Elm> pluList = req.getRequest().getPluList();
			for (level1Elm par:pluList)
			{
				String pluNo=par.getPluNo();
				//删除资料
				DelBean db = new DelBean("DCP_PINPEI_GOODS");
				db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db));
			}
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");		
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PinPeiGoodsDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PinPeiGoodsDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PinPeiGoodsDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PinPeiGoodsDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> pluList = req.getRequest().getPluList();

		if(pluList==null || pluList.isEmpty())
		{
			errMsg.append("商品列表不能为空, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PinPeiGoodsDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_PinPeiGoodsDeleteReq>(){};
	}

	@Override
	protected DCP_PinPeiGoodsDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_PinPeiGoodsDeleteRes();
	}

}
