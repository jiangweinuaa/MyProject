package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderCategoryUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrderCategoryUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderCategoryUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：OrderCategoryUpdateDCP
 * 服务说明：外卖分类修改
 * @author jinzma	 
 * @since  2019-03-11
 */
public class DCP_OrderCategoryUpdate extends SPosAdvanceService<DCP_OrderCategoryUpdateReq,DCP_OrderCategoryUpdateRes>
{

	@Override
	protected void processDUID(DCP_OrderCategoryUpdateReq req, DCP_OrderCategoryUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String belFirm = req.getOrganizationNO();
		if(belFirm==null)
		{
			belFirm ="";
		}
		try 
		{
			//判断菜品池的商品是否不在新的商品分类里面
			StringBuffer categorySB = new StringBuffer("");  
			String sql ;
			List<level1Elm> CategoryNO = req.getDatas();
			for (level1Elm par : CategoryNO) {
				categorySB.append("'"+par.getCategoryNO().trim()+"',");	
			}
			categorySB=categorySB.deleteCharAt(categorySB.length()-1);
			sql=" select categoryno from OC_goods where categoryno not in ("+categorySB+" ) and EID='"+eId+"'";
			if (belFirm != null && belFirm.length() > 0)
			{
				sql+=" and BELFIRM = '"+belFirm+"'";
			}
			//String[] conditionValues = {}; //查询报损单单号
			List<Map<String, Object>> getQData = this.doQueryData(sql, null) ;
			if (getQData != null && getQData.isEmpty() == false) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"请先删除分类下面的外卖商品");
			} 
			else
			{
				//删除OC_CATEGORY
				DelBean db = new DelBean("OC_CATEGORY");
				db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				if (belFirm != null && belFirm.length() > 0)
				{
					db.addCondition("BELFIRM", new DataValue(belFirm, Types.VARCHAR));
				}
				this.addProcessData(new DataProcessBean(db));

				//新增OC_CATEGORY
				DataValue[] insValue = null;
				List<level1Elm> jsonDatas = req.getDatas();
				for (level1Elm par : jsonDatas) {
					String[] columns = {
							"EID", "CATEGORYNO","CATEGORYNAME","PRIORITY","BELFIRM","STATUS" };
					String categoryNO= par.getCategoryNO();
					String categoryName= par.getCategoryName();
					String priority= par.getPriority();

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(categoryNO, Types.VARCHAR), 
							new DataValue(categoryName, Types.VARCHAR),
							new DataValue(priority, Types.INTEGER),		
							new DataValue(belFirm, Types.VARCHAR),
							new DataValue("100", Types.VARCHAR)
					};
					InsBean ib = new InsBean("OC_CATEGORY", columns);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib)); 
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
	protected List<InsBean> prepareInsertData(DCP_OrderCategoryUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCategoryUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCategoryUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderCategoryUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");  
		List<level1Elm> jsonHead=req.getDatas();
		for(level1Elm itemhead : jsonHead)
		{
			if(Check.Null(itemhead.getCategoryNO()))
			{
				errMsg.append("分类编号不可为空值, ");
				isFail = true;
			}
			if(Check.Null(itemhead.getCategoryName()))
			{
				errMsg.append("分类名称不可为空值, ");
				isFail = true;
			}
			if(Check.Null(itemhead.getPriority()))
			{
				errMsg.append("分类优先级不可为空值, ");
				isFail = true;
			}
			else 
			{
				if(!PosPub.isNumeric(itemhead.getPriority()))
				{
					errMsg.append("分类优先级必须为数值, ");
					isFail = true;
				}
			}
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}

		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderCategoryUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderCategoryUpdateReq>(){};
	}

	@Override
	protected DCP_OrderCategoryUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderCategoryUpdateRes();
	}

}
