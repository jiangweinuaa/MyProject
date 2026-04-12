package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsFlavorCreateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsFlavorCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 新增商品口味信息  2018-09-20	
 * @author yuanyy
 *
 */
public class DCP_GoodsFlavorCreate extends SPosAdvanceService<DCP_GoodsFlavorCreateReq,DCP_GoodsFlavorCreateRes> {

	@Override
	protected void processDUID(DCP_GoodsFlavorCreateReq req, DCP_GoodsFlavorCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try 
		{
			String flavorNO = req.getRequest().getFlavorNo();
			String flavorName = req.getRequest().getFlavorName();
			String priority = req.getRequest().getPriority();
			String status = req.getRequest().getStatus();
			String eId = req.geteId();

			sql = this.isRepeat(flavorNO, eId);
			List<Map<String, Object>> flavorDatas = this.doQueryData(sql, null);
			if(flavorDatas.isEmpty())
			{
				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_FLAVOR");
				//add Value
				ub1.addUpdateValue("PRIORITY", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
				//condition
				ub1.addCondition("PRIORITY", new DataValue(priority, Types.VARCHAR,DataExpression.GreaterEQ));
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
				this.addProcessData(new DataProcessBean(ub1));

				String[] columns1 = { "FLAVORNO","FLAVORNAME","PRIORITY","STATUS","EID" };
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(flavorNO, Types.VARCHAR),
						new DataValue(flavorName, Types.VARCHAR),
						new DataValue(priority, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(eId, Types.VARCHAR)
				};

				InsBean ib1 = new InsBean("DCP_FLAVOR", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); 
				
				this.doExecuteDataToDB();	

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "编码:" +flavorNO+"的口味信息已存在");
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsFlavorCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsFlavorCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsFlavorCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsFlavorCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String flavorNO = req.getRequest().getFlavorNo();
		if(Check.Null(flavorNO)){
			errMsg.append("口味编码不能为空值 ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsFlavorCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsFlavorCreateReq>(){};
	}

	@Override
	protected DCP_GoodsFlavorCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsFlavorCreateRes();
	}

	/**
	 * 判断口味信息是否已存在或重复
	 * @param pluNO
	 * @param flavorNO
	 * @param eId
	 * @return
	 */
	private String isRepeat(String flavorNO , String eId ){
		String sql = null;
		sql = "select * from DCP_FLAVOR "
				+ " where flavorNO = '"+flavorNO +"' "
				+ " and EID = '"+eId+"'";
		return sql;
	}

}	
