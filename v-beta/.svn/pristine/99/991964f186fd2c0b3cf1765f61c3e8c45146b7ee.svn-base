package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TGCommissionCreateReq;
import com.dsc.spos.json.cust.req.DCP_TGCommissionCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_TGCommissionCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：TGCommissionCreateDCP
 * 服务说明：团务拆账新增
 * @author jinzma	 
 * @since  2019-02-11
 */
public class DCP_TGCommissionCreate  extends SPosAdvanceService<DCP_TGCommissionCreateReq,DCP_TGCommissionCreateRes > {

	@Override
	protected void processDUID(DCP_TGCommissionCreateReq req, DCP_TGCommissionCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String travelNO = req.getTravelNO();
		String tgCategoryNO =req.getTgCategoryNO();
		String shopBonus = req.getShopBonus();
		String tempTourGroup = req.getTempTourGroup();
		String memo = req.getMemo();
		String status = req.getStatus();
		try 
		{
			if (checkExist(req) == false)
			{
				DataValue[] insValue = null;
			  
				//新增单身
				List<level1Elm> jsonDatas = req.getDatas();
				for (level1Elm par : jsonDatas) {
					String[] columns_detail = {
							"EID", "TRAVELNO","TGCATEGORYNO","GUIDENO",
							"TRAVELRATE", "GUIDERATE","MEMO","STATUS" };
					
					String guideNO= par.getGuideNO();
					String travelRate= par.getTravelRate();
					String guideRate = par.getGuideRate();
					String detailmemo=par.getMemo();
					
					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(travelNO, Types.VARCHAR), 
							new DataValue(tgCategoryNO, Types.VARCHAR),
							new DataValue(guideNO, Types.VARCHAR),								
							new DataValue(travelRate, Types.FLOAT),
							new DataValue(guideRate, Types.FLOAT),
							new DataValue(detailmemo, Types.VARCHAR),
							new DataValue("100", Types.VARCHAR)
					};
					InsBean ib_detail = new InsBean("DCP_TGCOMMISSION_DETAIL", columns_detail);
					ib_detail.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib_detail)); 	
				}
				
			  //新增单头
				String[] columns = {
						"EID", "TRAVELNO","TGCATEGORYNO","SHOPBONUS",
						"TEMPTOURGROUP", "MEMO", "STATUS" };
				insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(travelNO, Types.VARCHAR), 
						new DataValue(tgCategoryNO, Types.VARCHAR),
						new DataValue(shopBonus, Types.VARCHAR),								
						new DataValue(tempTourGroup, Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
				};
				InsBean ib = new InsBean("DCP_TGCOMMISSION", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); //	
				this.doExecuteDataToDB();
			}
		
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");		
		
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		
		
	}

	private boolean checkExist(DCP_TGCommissionCreateReq req)  throws Exception {
		
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String travelNO = req.getTravelNO();
		String tgCategoryNO =req.getTgCategoryNO();
		
		String[] conditionValues = { eId,travelNO,tgCategoryNO }; 				
		sql = " select * from DCP_TGCOMMISSION where EID=?  and TRAVELNO=? and TGCATEGORYNO=? " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_TGCommissionCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TGCommissionCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TGCommissionCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TGCommissionCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> datas = req.getDatas();

		if (Check.Null(req.getTravelNO()) ) 
		{
			errMsg.append("旅行社编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getTgCategoryNO()) ) 
		{
			errMsg.append("分类编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getShopBonus()) ) 
		{
			errMsg.append("是否前台领奖金不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getTempTourGroup()) ) 
		{
			errMsg.append("是否临时团不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level1Elm par : datas) 
		{			
			if (Check.Null(par.getGuideNO())) 
			{
				errMsg.append("导游编号不可为空值, ");
				isFail = true;
			}	
			if (Check.Null(par.getTravelRate())) 
			{
				errMsg.append("旅行社佣金比率不可为空值, ");
				isFail = true;
			}
			else 
			{
				if (!PosPub.isNumericType(par.getTravelRate()))
				{
					errMsg.append("旅行社佣金比率必须为数值, ");
					isFail = true;
				}

			}
			
			if (Check.Null(par.getGuideRate())) 
			{
				errMsg.append("导游佣金比率不可为空值, ");
				isFail = true;
			}
			else 
			{
				if (!PosPub.isNumericType(par.getGuideRate()))
				{
					errMsg.append("导游佣金比率必须为数值, ");
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
	protected TypeToken<DCP_TGCommissionCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TGCommissionCreateReq>(){} ;
	}

	@Override
	protected DCP_TGCommissionCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TGCommissionCreateRes();
	}

}
