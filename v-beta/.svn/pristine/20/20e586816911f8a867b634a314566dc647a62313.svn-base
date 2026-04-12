package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BFeeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BFeeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_BFeeDelete extends SPosAdvanceService<DCP_BFeeDeleteReq,DCP_BFeeDeleteRes>
{
	@Override
	protected void processDUID(DCP_BFeeDeleteReq req, DCP_BFeeDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();;
		String organizationNO = req.getOrganizationNO();
		String bFeeNO = req.getRequest().getbFeeNo();
		try
		{
			String sql = "select status from DCP_BFEE "
					+ "where eId='"+eId+"' and organizationno='"+organizationNO+"' and BFEENO='"+bFeeNO+"' and status='0' ";
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)	
			{
				DelBean db1 = new DelBean("DCP_BFEE_DETAIL");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
				db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
				db1.addCondition("BFEENO", new DataValue(bFeeNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				DelBean db2 = new DelBean("DCP_BFEE");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
				db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
				db2.addCondition("BFEENO", new DataValue(bFeeNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));

				this.doExecuteDataToDB();
				
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
			}
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BFeeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BFeeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BFeeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BFeeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String bFeeNO = req.getRequest().getbFeeNo();

		if(Check.Null(bFeeNO)){
			errMsg.append("费用编码不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_BFeeDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BFeeDeleteReq>(){};
	}

	@Override
	protected DCP_BFeeDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BFeeDeleteRes();
	}

}
