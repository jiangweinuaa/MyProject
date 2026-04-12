package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TGArrivalUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TGArrivalUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_TGArrivalUpdate extends SPosAdvanceService<DCP_TGArrivalUpdateReq,DCP_TGArrivalUpdateRes > {

	@Override
	protected void processDUID(DCP_TGArrivalUpdateReq req, DCP_TGArrivalUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String sql= null ;
		String eId = req.geteId();
		String oShopId = req.getoShopId();
		String orderNO =req.getOrderNO();
		String travelNO = req.getTravelNO();
		String guideNO = req.getGuideNO();
		String guideTel=req.getGuideTel();
		String countryCode = req.getCountryCode();
		String orderDate = req.getOrderDate();
		String orderTime = req.getOrderTime();
		String peopleNum = req.getPeopleNum();
		String memo =req.getMemo();
		String arrivalTime = req.getArrivalTime();
		String leaveTime = req.getLeaveTime();
		String tourGroupNO = req.getTourGroupNO();
		String address = req.getAddress();
		String status= req.getStatus();
		String modifyBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
		String modifyDate = dfDate.format(cal.getTime());
		String modifyTime = dfTime.format(cal.getTime());		

		try 
		{
			if (checkExist(req) == true)
			{
				//根据参数判断团号录入是否正确    接团依据 <TGReceive >   1. 预约编号 2.团号（不可重） 3.团号（可重复）
				String TGReceivePara = PosPub.getPARA_SMS(dao, eId, "", "TGReceive") ;
				if (Check.Null(TGReceivePara))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "接团依据参数未维护");		
				}	
				else
				{
					if (TGReceivePara.equals("2"))
					{
						if (Check.Null(tourGroupNO))
						{
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "团号不可为空值");		
						}
						else
						{
							String[] conditionValues = { eId,oShopId,orderDate,tourGroupNO,orderNO }; 				
							sql = " select * from DCP_TGARRIVAL where EID=? and SHOPID=? and orderdate=? and tourgroupno=? and ORDERNO<>? ";
							List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
							if (getQData != null && getQData.isEmpty() == false) {
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "团号重复，请重新录入");		
							}
						}
					}
					if (TGReceivePara.equals("3"))
					{
						if (Check.Null(tourGroupNO))
						{
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "团号不可为空值");		
						}
					}
				}

				//更新单头
				UptBean ub = null;	
				ub = new UptBean("DCP_TGARRIVAL");
				
				ub.addUpdateValue("TRAVELNO", new DataValue(travelNO, Types.VARCHAR));
				ub.addUpdateValue("GUIDENO", new DataValue(guideNO, Types.VARCHAR));
				ub.addUpdateValue("GUIDETEL", new DataValue(guideTel, Types.VARCHAR));
				ub.addUpdateValue("COUNTRYCODE", new DataValue(countryCode, Types.VARCHAR));
				ub.addUpdateValue("ORDERDATE", new DataValue(orderDate, Types.VARCHAR));
				ub.addUpdateValue("ORDERTIME", new DataValue(orderTime, Types.VARCHAR));				
				ub.addUpdateValue("PEOPLENUM", new DataValue(peopleNum, Types.INTEGER));
				ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				ub.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
				ub.addUpdateValue("ARRIVALTIME", new DataValue(arrivalTime, Types.VARCHAR));
				ub.addUpdateValue("LEAVETIME", new DataValue(leaveTime, Types.VARCHAR));
				ub.addUpdateValue("TOURGROUPNO", new DataValue(tourGroupNO, Types.VARCHAR));
				ub.addUpdateValue("ADDRESS", new DataValue(address, Types.VARCHAR));
				ub.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
				ub.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
				ub.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
				ub.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));				
				ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("SHOPID", new DataValue(oShopId, Types.VARCHAR));
				ub.addCondition("ORDERNO", new DataValue(orderNO, Types.VARCHAR));

				this.addProcessData(new DataProcessBean(ub));
				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "资料不存在，请重新输入！");
			}


		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}




	}

	private boolean checkExist(DCP_TGArrivalUpdateReq req)  throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String oShopId = req.getoShopId();
		String orderNO =req.getOrderNO();

		String[] conditionValues = { eId,oShopId,orderNO }; 				
		sql = " select * from DCP_TGARRIVAL where EID=? and SHOPID=? and orderno=? ";

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TGArrivalUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TGArrivalUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TGArrivalUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TGArrivalUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (Check.Null(req.getoShopId()) ) 
		{
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getOrderNO()) ) 
		{
			errMsg.append("预约编号不可为空值, ");
			isFail = true;
		}		
		if (Check.Null(req.getTravelNO()) ) 
		{
			errMsg.append("旅行社编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getGuideNO()) ) 
		{
			errMsg.append("导游编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getCountryCode()) ) 
		{
			errMsg.append("国家代码不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getOrderDate()) ) 
		{
			errMsg.append("预约日期不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getOrderTime()) ) 
		{
			errMsg.append("预约时间不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getStatus()) ) 
		{
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;

	}

	@Override
	protected TypeToken<DCP_TGArrivalUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TGArrivalUpdateReq>(){};
	}

	@Override
	protected DCP_TGArrivalUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TGArrivalUpdateRes();
	}

}
