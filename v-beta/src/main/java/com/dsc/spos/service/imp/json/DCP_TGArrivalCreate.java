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
import com.dsc.spos.json.cust.req.DCP_TGArrivalCreateReq;
import com.dsc.spos.json.cust.res.DCP_TGArrivalCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：TGArrivalCreateDCP
 * 服务说明：预计到团新增
 * @author jinzma	 
 * @since  2019-02-14
 */
public class DCP_TGArrivalCreate extends SPosAdvanceService<DCP_TGArrivalCreateReq,DCP_TGArrivalCreateRes>  {

	@Override
	protected void processDUID(DCP_TGArrivalCreateReq req, DCP_TGArrivalCreateRes res) throws Exception {
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
		String createBy = req.getOpNO();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
		String createDate = dfDate.format(cal.getTime());
		String createTime = dfTime.format(cal.getTime());			

		try 
		{
			if (checkExist(req) == false)
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
							String[] conditionValues = { eId,oShopId,orderDate,tourGroupNO }; 				
							sql = " select * from DCP_TGARRIVAL where EID=? and shopId=? and orderdate=? and tourgroupno=? ";
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

				//保存资料
				String[] columns = {
						"EID", "SHOPID","ORDERNO","TRAVELNO","GUIDENO", "GUIDETEL","COUNTRYCODE",
						"ORDERDATE","ORDERTIME","PEOPLENUM","STATUS","MEMO","ARRIVALTIME",
						"LEAVETIME","TOURGROUPNO","ADDRESS","STATUS","CREATEBY","CREATE_DATE","CREATE_TIME"
				};

				DataValue[]	insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(oShopId, Types.VARCHAR), 
						new DataValue(orderNO, Types.VARCHAR),
						new DataValue(travelNO, Types.VARCHAR),								
						new DataValue(guideNO, Types.VARCHAR),
						new DataValue(guideTel, Types.VARCHAR),
						new DataValue(countryCode, Types.VARCHAR),						
						new DataValue(orderDate, Types.VARCHAR),
						new DataValue(orderTime, Types.VARCHAR),
						new DataValue(peopleNum, Types.INTEGER),
						new DataValue(status, Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR),
						new DataValue(arrivalTime, Types.VARCHAR),
						new DataValue(leaveTime, Types.VARCHAR),
						new DataValue(tourGroupNO, Types.VARCHAR),
						new DataValue(address, Types.VARCHAR),
						new DataValue("100", Types.VARCHAR),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(createDate, Types.VARCHAR),
						new DataValue(createTime, Types.VARCHAR),	
				};
				InsBean ib = new InsBean("DCP_TGARRIVAL", columns);
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

	private boolean checkExist(DCP_TGArrivalCreateReq req)  throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String oShopId = req.getoShopId();
		String orderNO =req.getOrderNO();

		String[] conditionValues = { eId,oShopId,orderNO }; 				
		sql = " select * from DCP_TGARRIVAL where EID=? and shopId=? and orderno=? ";

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}


	@Override
	protected List<InsBean> prepareInsertData(DCP_TGArrivalCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TGArrivalCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TGArrivalCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TGArrivalCreateReq req) throws Exception {
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
	protected TypeToken<DCP_TGArrivalCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TGArrivalCreateReq>(){};
	}

	@Override
	protected DCP_TGArrivalCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TGArrivalCreateRes();
	}

}
