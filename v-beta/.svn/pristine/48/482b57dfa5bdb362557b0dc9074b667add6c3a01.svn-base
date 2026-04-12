package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_GoodsStuffDeleteReq;
import com.dsc.spos.json.cust.res.DCP_GoodsStuffDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 删除统一加料信息 
 * @author yuanyy
 *
 */
public class DCP_GoodsStuffDelete extends SPosAdvanceService<DCP_GoodsStuffDeleteReq, DCP_GoodsStuffDeleteRes> {

	@Override
	protected void processDUID(DCP_GoodsStuffDeleteReq req, DCP_GoodsStuffDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String eId = req.geteId();
			String stuffNO = req.getRequest().getStuffNo();
			String priority = req.getRequest().getPriority();

			DelBean db1 = new DelBean("DCP_STUFF");
			db1.addCondition("STUFFNO", new DataValue(stuffNO,Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			UptBean ub2 = null;	
			ub2 = new UptBean("DCP_STUFF");
			//add Value
			ub2.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.SubSelf));
			//condition
			ub2.addCondition("PRIORITY", new DataValue(priority, Types.VARCHAR,DataExpression.Greater));
			ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));	
			this.addProcessData(new DataProcessBean(ub2));

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsStuffDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsStuffDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsStuffDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsStuffDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String stuffNO = req.getRequest().getStuffNo();
		if(Check.Null(stuffNO)){
			errMsg.append("加料编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsStuffDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsStuffDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsStuffDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsStuffDeleteRes();
	}

}
