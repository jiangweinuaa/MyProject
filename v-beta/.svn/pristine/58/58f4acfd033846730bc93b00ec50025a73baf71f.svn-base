package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FuncButtonCreateReq;
import com.dsc.spos.json.cust.req.DCP_FuncButtonCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_FuncButtonCreateReq.level1shopsElm;
import com.dsc.spos.json.cust.res.DCP_FuncButtonCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
/**
 * 服務函數：FuncButtonCreateDCP
 * 服务说明：功能按键新增
 * @author jinzma 
 * @since  2019-02-20
 */
public class DCP_FuncButtonCreate extends SPosAdvanceService<DCP_FuncButtonCreateReq,DCP_FuncButtonCreateRes > {

	@Override
	protected void processDUID(DCP_FuncButtonCreateReq req, DCP_FuncButtonCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String ptemplateNO = req.getRequest().getPtemplateNo();
		String ptemplateName =req.getRequest().getPtemplateName();
		String businessType = req.getRequest().getBusinessType();
		
		try 
		{
			if (checkExist(req) == false)
			{
				DataValue[] insValue = null;

				//新增主表
				List<level1Elm> jsonDatas = req.getRequest().getDatas();			
				for (level1Elm par : jsonDatas) {
					String[] columns_detail = {
							"EID","PTEMPLATENO","PTEMPLATENAME","FUNCNO",
							"ICON","FUNCGROUP","PRIORITY","BUTTONQSS","APPROVENEED","BUSINESSTYPE","STATUS"						   											
					};					
					String funcNO= par.getFuncNo();
					String icon= par.getIcon();
					String funcGroup = par.getFuncGroup();
					String priority=par.getPriority();
					String qss=par.getQss();
					String approveneed=par.getApproveneed();
					String status="100";

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(ptemplateNO, Types.VARCHAR), 
							new DataValue(ptemplateName, Types.VARCHAR),
							new DataValue(funcNO, Types.VARCHAR),								
							new DataValue(icon, Types.VARCHAR),
							new DataValue(funcGroup, Types.INTEGER),
							new DataValue(priority, Types.INTEGER),
							new DataValue(qss, Types.VARCHAR),
							new DataValue(approveneed, Types.VARCHAR),	
							new DataValue(businessType, Types.VARCHAR),	
							new DataValue(status, Types.VARCHAR)
					};

					InsBean ib_detail = new InsBean("DCP_FUNCBUTTON", columns_detail);
					ib_detail.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib_detail)); 
				}

				//新增门店范围
				List<level1shopsElm> jsonDatasshop = req.getRequest().getShops();
				for (level1shopsElm parshop : jsonDatasshop) {
					String[] columns_shop = {
							"EID","PTEMPLATENO","SHOPID","STATUS"				   											
					};
					String shopId=parshop.getShopId();
					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(ptemplateNO, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR),								
							new DataValue("100", Types.VARCHAR),
					};

					InsBean ib_shop = new InsBean("DCP_FUNCBUTTON_SHOP", columns_shop);
					ib_shop.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib_shop)); 
				}
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

	@Override
	protected List<InsBean> prepareInsertData(DCP_FuncButtonCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FuncButtonCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FuncButtonCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FuncButtonCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> datas = req.getRequest().getDatas();

		if (Check.Null(req.getRequest().getPtemplateNo()) ) 
		{
			errMsg.append("模板编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getBusinessType())) 
		{
			errMsg.append("商业类型不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level1Elm par : datas) 
		{			
			if (Check.Null(par.getFuncNo())) 
			{
				errMsg.append("功能编号不可为空值, ");
				isFail = true;
			}	

			if (Check.Null(par.getFuncGroup())) 
			{
				errMsg.append("功能分组不可为空值, ");
				isFail = true;
			}
			else
			{
				if (!PosPub.isNumeric(par.getFuncGroup())) 
				{
					errMsg.append("功能分组必须为数值, ");
					isFail = true;
				}
			}

			if (Check.Null(par.getPriority())) 
			{
				errMsg.append("优先级不可为空值, ");
				isFail = true;
			}
			else
			{
				if (!PosPub.isNumeric(par.getPriority())) 
				{
					errMsg.append("优先级必须为数值, ");
					isFail = true;
				}
			}	

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}	

		}

		List<level1shopsElm> shops = req.getRequest().getShops();
		for (level1shopsElm par : shops) {
			String shopId=par.getShopId();
			if (Check.Null(shopId)) 
			{
				errMsg.append("门店编号不可为空值, ");
				isFail = true;
			}	
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}				
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_FuncButtonCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_FuncButtonCreateReq>(){};
	}

	@Override
	protected DCP_FuncButtonCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_FuncButtonCreateRes();
	}

	private boolean checkExist(DCP_FuncButtonCreateReq req)  throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String ptemplateNO = req.getRequest().getPtemplateNo();

		String[] conditionValues = { eId,ptemplateNO }; 				
		sql = " select * from DCP_FUNCBUTTON where EID=?  and PTEMPLATENO=?  " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

}
