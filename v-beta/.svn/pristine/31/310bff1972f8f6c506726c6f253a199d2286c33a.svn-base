package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_POrderDeleteReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_POrderDeleteReq;
import com.dsc.spos.json.cust.res.DCP_POrderDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：POrderDelete
 *    說明：要货单删除
 * 服务说明：要货单删除
 * @author panjing 
 * @since  2016-10-08
 */

public class DCP_POrderDelete extends SPosAdvanceService<DCP_POrderDeleteReq, DCP_POrderDeleteRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_POrderDeleteReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		levelElm request = req.getRequest();
		String porderNO = request.getPorderNo();

		if (Check.Null(porderNO)) {
			isFail = true;
			errMsg.append("要货单编号不可为空值, ");
		}  

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_POrderDeleteReq> getRequestType() {
		return new TypeToken<DCP_POrderDeleteReq>(){};
	}

	@Override
	protected DCP_POrderDeleteRes getResponseType() {
		return new DCP_POrderDeleteRes();
	}


	@Override
	protected void processDUID(DCP_POrderDeleteReq req,DCP_POrderDeleteRes res) throws Exception {
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		levelElm request = req.getRequest();
		String porderNO = request.getPorderNo();
		//try
		//{
			//查詢單據狀態
			String sql = this.getQuery();				//查询收货单单头数据
			String[] conditionValues1 = {shopId,organizationNO, eId, porderNO}; //查詢條件
			List<Map<String, Object>> getQData = this.doQueryData(sql,conditionValues1);

			if (getQData != null && getQData.isEmpty() == false) 
			{ 
				DelBean db1 = new DelBean("DCP_PORDER");
				db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("PORDERNO", new DataValue(porderNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				DelBean db2 = new DelBean("DCP_PORDER_DETAIL");
				db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db2.addCondition("PORDERNO", new DataValue(porderNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));

                //删除内部交易
                ColumnDataValue condition = new ColumnDataValue();
                condition.add("EID", req.geteId());
                condition.add("BILLNO", req.getRequest().getPorderNo());
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition)));

            }
			else 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
			}
			
			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
		//}
		//catch (Exception e)
		//{
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		//}

	}


	@Override
	protected List<InsBean> prepareInsertData(DCP_POrderDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_POrderDeleteReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_POrderDeleteReq req) throws Exception {
		return null;
	}

	private String getQuery() throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(""   
				+ "select status "
				+ " from DCP_Porder "
				+ " where STATUS='0' and SHOPID = ? AND OrganizationNO = ? AND EID = ? and PorderNO = ? "
				);

		sql = sqlbuf.toString();

		return sql;
	}	

}


