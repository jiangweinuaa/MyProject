package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DifferenceDeleteReq;
import com.dsc.spos.json.cust.req.DCP_DifferenceDeleteReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_DifferenceDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_DifferenceDelete extends SPosAdvanceService<DCP_DifferenceDeleteReq,DCP_DifferenceDeleteRes>
{
	@Override
	protected void processDUID(DCP_DifferenceDeleteReq req, DCP_DifferenceDeleteRes res) throws Exception 
	{		
		String eId = req.geteId();
		String organizationNO = req.getOrganizationNO();
		levelElm request = req.getRequest();
		String DifferenceNO = request.getDifferenceNo();
		String sql = "select status from DCP_difference "
				+ "where EID='"+eId+"' and organizationno='"+organizationNO+"' and differenceno='"+DifferenceNO+"'";
		try
		{
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)	
			{
				String status = getQDataDetail.get(0).get("STATUS").toString();
				if(!status.equals("0")){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此单已提交，不能删除！");
				}
				else 
				{
					DelBean db1 = new DelBean("DCP_DIFFERENCE_DETAIL");
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
					db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
					db1.addCondition("DIFFERENCENO", new DataValue(DifferenceNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));

					DelBean db2 = new DelBean("DCP_DIFFERENCE");
					db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db2.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
					db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
					db2.addCondition("DIFFERENCENO", new DataValue(DifferenceNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db2));
				}
			}
			else 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已删除！");
			}

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		
		}
		catch(Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}


	@Override
	protected List<InsBean> prepareInsertData(DCP_DifferenceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DifferenceDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DifferenceDeleteReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DifferenceDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		levelElm request = req.getRequest();
		String DifferenceNO = request.getDifferenceNo();

		if (Check.Null(DifferenceNO)) 
		{
			isFail = true;
			errMsg.append("单据编号不可为空值, ");
		}  

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DifferenceDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DifferenceDeleteReq>(){};
	}

	@Override
	protected DCP_DifferenceDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DifferenceDeleteRes();
	}
}
