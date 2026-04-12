package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerPOrderDeleteReq;
import com.dsc.spos.json.cust.req.DCP_CustomerPOrderDeleteReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_CustomerPOrderDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;

public class DCP_CustomerPOrderDelete extends SPosAdvanceService<DCP_CustomerPOrderDeleteReq,DCP_CustomerPOrderDeleteRes>
{
	@Override
	protected void processDUID(DCP_CustomerPOrderDeleteReq req, DCP_CustomerPOrderDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.getRequest().geteId();
		String org = req.getRequest().getShopId();
		
		try
		{
			for(level2Elm data :req.getRequest().getDatas())
			{
				String pOrderNo=data.getpOrderNo().toString();
				String sql = "select status from DCP_CUSTOMERPORDER "
						+ "where eId='"+eId+"' and SHOPNO='"+org+"' and PORDERNO='"+pOrderNo+"' and status='0' ";
				List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
				if (getQDataDetail != null && getQDataDetail.isEmpty() == false)	
				{
					DelBean db1 = new DelBean("DCP_CUSTOMERPORDER_DETAIL");
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("SHOPNO", new DataValue(org, Types.VARCHAR));
					db1.addCondition("PORDERNO", new DataValue(pOrderNo, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db1));

					DelBean db2 = new DelBean("DCP_CUSTOMERPORDER");
					db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db2.addCondition("SHOPNO", new DataValue(org, Types.VARCHAR));
					db2.addCondition("PORDERNO", new DataValue(pOrderNo, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db2));

					//删除内部结算
					ColumnDataValue condition = new ColumnDataValue();
					condition.add("EID", req.geteId());
					condition.add("BILLNO", pOrderNo);

					this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition)));
					this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition)));


				}
				else 
				{
					this.pData.clear();
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据["+pOrderNo+"]不存在或已确认！");
				}
			}
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
	protected List<InsBean> prepareInsertData(DCP_CustomerPOrderDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CustomerPOrderDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CustomerPOrderDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CustomerPOrderDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(Check.Null(req.getRequest().geteId())){
			errMsg.append("eId不可为空值, ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().getShopId())){
			errMsg.append("shopId不可为空值, ");
			isFail = true;
		}
		if(req.getRequest().getDatas()==null || req.getRequest().getDatas().isEmpty())
		{
			errMsg.append("datas不可为空值, ");
			isFail = true;
		}else
		{
            for(level2Elm data : req.getRequest().getDatas())
            {
            	if(Check.Null(data.getpOrderNo()))
            	{
            		errMsg.append("pOderNo不可为空值, ");
            		isFail = true;
            	}
            }

		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_CustomerPOrderDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_CustomerPOrderDeleteReq>(){};
	}

	@Override
	protected DCP_CustomerPOrderDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_CustomerPOrderDeleteRes();
	}

}
