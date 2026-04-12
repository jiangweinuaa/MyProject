package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WarningUpdateReq;
import com.dsc.spos.json.cust.req.DCP_WarningUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_WarningUpdateReq.level2PushMan;
import com.dsc.spos.json.cust.req.DCP_WarningUpdateReq.level2Shop;
import com.dsc.spos.json.cust.req.DCP_WarningUpdateReq.level3PushWay;
import com.dsc.spos.json.cust.res.DCP_WarningUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningUpdate extends SPosAdvanceService<DCP_WarningUpdateReq, DCP_WarningUpdateRes> {

	@Override
	protected void processDUID(DCP_WarningUpdateReq req, DCP_WarningUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		
		level1Elm requestModel =	req.getRequest();
		String companyId = requestModel.getCompanyId();
		String shopId = requestModel.getShopId();		
		String billNo = requestModel.getBillNo();	
		String billDate = requestModel.getBillDate();	
		String warningType = requestModel.getWarningType();
		String warningItem = requestModel.getWarningItem();
		String pushTimeType = requestModel.getPushTimeType();
		List<level2PushMan> pushManList = requestModel.getPushManList();
		String restrictShop = requestModel.getRestrictShop();
		String templateType = requestModel.getTemplateType();
		String status = requestModel.getStatus();
		String lastmodiopid = requestModel.getLastmodiopid()==null?req.getOpNO():requestModel.getLastmodiopid();
		String lastmodiname = req.getOpName();
		String lastmoditime = requestModel.getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		//更新主表
		UptBean ub_warning = null;	
		ub_warning = new UptBean("DCP_Warning");		
	  // condition
		ub_warning.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub_warning.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
		
		ub_warning.addUpdateValue("BILLNAME",new DataValue(requestModel.getBillName(), Types.VARCHAR));
		ub_warning.addUpdateValue("BILLDATE",new DataValue(billDate, Types.DATE));
		ub_warning.addUpdateValue("COMPANYID",new DataValue(companyId, Types.VARCHAR));
		ub_warning.addUpdateValue("SHOPID",new DataValue(shopId, Types.VARCHAR));
		ub_warning.addUpdateValue("CHANNELID",new DataValue(requestModel.getChannelId(), Types.VARCHAR));
		ub_warning.addUpdateValue("EMPLOYEEID",new DataValue(requestModel.getEmployeeId(), Types.VARCHAR));
		ub_warning.addUpdateValue("DEPARTID",new DataValue(requestModel.getDepartId(), Types.VARCHAR));
		ub_warning.addUpdateValue("WARNINGTYPE",new DataValue(warningType, Types.VARCHAR));
		ub_warning.addUpdateValue("WARNINGITEM",new DataValue(warningItem, Types.VARCHAR));
		ub_warning.addUpdateValue("STARTTIME",new DataValue(requestModel.getStartTime(), Types.VARCHAR));
		ub_warning.addUpdateValue("ENDTIME",new DataValue(requestModel.getEndTime(), Types.VARCHAR));
		ub_warning.addUpdateValue("ORDERQTY",new DataValue(requestModel.getOrderQty()==null?"0":requestModel.getOrderQty(), Types.VARCHAR));
		ub_warning.addUpdateValue("ORDERAMT",new DataValue(requestModel.getOrderAmt()==null?"0":requestModel.getOrderAmt(), Types.VARCHAR));
		ub_warning.addUpdateValue("POINTQTY",new DataValue(requestModel.getPointQty()==null?"0":requestModel.getPointQty(), Types.VARCHAR));
		ub_warning.addUpdateValue("TEMPLATETYPE",new DataValue(templateType, Types.VARCHAR));
		ub_warning.addUpdateValue("PUSHTIMETYPE",new DataValue(pushTimeType, Types.VARCHAR));
		ub_warning.addUpdateValue("PUSHTIME",new DataValue(requestModel.getPushTime(), Types.VARCHAR));
		ub_warning.addUpdateValue("RESTRICTSHOP",new DataValue(restrictShop, Types.VARCHAR));
		//ub_warning.addUpdateValue("MEMO",new DataValue("", Types.VARCHAR));
		ub_warning.addUpdateValue("STATUS",new DataValue(status, Types.VARCHAR));
		ub_warning.addUpdateValue("LASTMODIOPID",new DataValue(lastmodiopid, Types.VARCHAR));
		ub_warning.addUpdateValue("LASTMODIOPNAME",new DataValue(lastmodiname, Types.VARCHAR));
		ub_warning.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime, Types.DATE));
	
		this.addProcessData(new DataProcessBean(ub_warning));
		
		//下面删除子表，再新增
		//推送门店
		DelBean del_warning_pushShop = new DelBean("DCP_WARNING_PICKSHOP");
		del_warning_pushShop.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		del_warning_pushShop.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));	
		this.addProcessData(new DataProcessBean(del_warning_pushShop));
		
		//推送人
		DelBean del_warning_pushMan = new DelBean("DCP_WARNING_PUSHMAN");
		del_warning_pushMan.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		del_warning_pushMan.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(del_warning_pushMan));
		
		//推送 方式
		DelBean del_warning_pushWay = new DelBean("DCP_WARNING_PUSHWAY");
		del_warning_pushWay.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		del_warning_pushWay.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(del_warning_pushWay));
		
		
		//
		if(pushManList!=null&&pushManList.isEmpty()==false)
		{
		
			String[] columns_warning_pushMan = { "EID", "BILLNO", "SERIALNO", "OPNO", "MOBILEPHONE", "EMAIL", "LASTMODIOPID",
				"LASTMODIOPNAME", "LASTMODITIME"
	
			};
			int serialNo_pushMan = 1;
			int serialNo_pushWay =1;//全局
			for (level2PushMan pushMan : pushManList) 
			{
				
				//String serialNo_pushMan = pushMan.getSerialNo();
				String opNo_pushMan = pushMan.getOpNo();
				
				DataValue[] insValue_warning_pushMan = new DataValue[] 
						{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(billNo, Types.VARCHAR),
							new DataValue(serialNo_pushMan, Types.VARCHAR),
							new DataValue(opNo_pushMan, Types.VARCHAR),
							new DataValue(pushMan.getMobilePhone(), Types.VARCHAR),
							new DataValue(pushMan.getEmail(), Types.VARCHAR),
							new DataValue(lastmodiopid, Types.VARCHAR),
							new DataValue(lastmodiname, Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE)
								
						};
				
				InsBean ib_warning_pushMan = new InsBean("DCP_WARNING_PUSHMAN", columns_warning_pushMan);
				ib_warning_pushMan.addValues(insValue_warning_pushMan);
				this.addProcessData(new DataProcessBean(ib_warning_pushMan)); 
				serialNo_pushMan ++;
				List<level3PushWay> pushWayList = pushMan.getPushWayList();
				if(pushWayList!=null&&pushWayList.isEmpty()==false)
				{
					String[] columns_warning_pushWay = { "EID", "BILLNO", "SERIALNO", "OPNO", "PUSHWAY", "LASTMODIOPID",
							"LASTMODIOPNAME", "LASTMODITIME"
				
						};
					
					for (level3PushWay pushWay : pushWayList) 
					{
						//String serialNo_pushWay = pushWay.getPushWay();
						DataValue[] insValue_warning_pushWay = new DataValue[] 
								{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(billNo, Types.VARCHAR),
									new DataValue(serialNo_pushWay, Types.VARCHAR),
									new DataValue(opNo_pushMan, Types.VARCHAR),
									new DataValue(pushWay.getPushWay(), Types.VARCHAR),							
									new DataValue(lastmodiopid, Types.VARCHAR),
									new DataValue(lastmodiname, Types.VARCHAR),
									new DataValue(lastmoditime, Types.DATE)
										
								};
						
						InsBean ib_warning_pushWay = new InsBean("DCP_WARNING_PUSHWAY", columns_warning_pushWay);
						ib_warning_pushWay.addValues(insValue_warning_pushWay);
						this.addProcessData(new DataProcessBean(ib_warning_pushWay)); 
						serialNo_pushWay++;
					}
				}
				
			}
			
		}
		
		List<level2Shop> pushShopList = requestModel.getShopList();
		if(pushShopList!=null&&pushShopList.isEmpty()==false)
		{
			String[] columns_warning_pushShop = { "EID", "BILLNO", "SERIALNO", "SHOPID", "LASTMODIOPID",
					"LASTMODIOPNAME", "LASTMODITIME"
		
				};
			int serialNo_pushShop =1;
			for (level2Shop pushShop : pushShopList) 
			{
				//String serialNo_pushShop = pushShop.getSerialNo();
				DataValue[] insValue_warning_pushShop = new DataValue[] 
						{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(billNo, Types.VARCHAR),
							new DataValue(serialNo_pushShop, Types.VARCHAR),
							new DataValue(pushShop.getShopId(), Types.VARCHAR),											
							new DataValue("", Types.VARCHAR),
							new DataValue("", Types.VARCHAR),
							new DataValue("", Types.VARCHAR)
								
						};
				InsBean ib_warning_pushShop = new InsBean("DCP_WARNING_PICKSHOP", columns_warning_pushShop);
				ib_warning_pushShop.addValues(insValue_warning_pushShop);
				this.addProcessData(new DataProcessBean(ib_warning_pushShop));
				serialNo_pushShop++;
		
			}
			
			
		}

		
		try 
		{
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			this.pData.clear();
			res.setSuccess(false);
			res.setServiceDescription(e.getMessage());
	
		}
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_WarningUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_WarningUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_WarningUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_WarningUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");
    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；  
    level1Elm requestModel =	req.getRequest();
    if(requestModel==null)
    {
    	errCt++;
	 	  errMsg.append("请求节点request不存在, ");
		  isFail = true;
		  throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
    
    if(Check.Null(requestModel.getWarningType()))
    {
    	errCt++;
	 	  errMsg.append("监控类型warningType不能为空, ");
		  isFail = true;
    	
    }
    if(Check.Null(requestModel.getWarningItem()))
    {
    	errCt++;
	 	  errMsg.append("监控项warningItem不能为空, ");
		  isFail = true;
    	
    }
    if(Check.Null(requestModel.getPushTimeType()))
    {
    	errCt++;
	 	  errMsg.append("监控时间类型pushTimeType不能为空, ");
		  isFail = true;
    	
    }
    
    
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }    
    return isFail;
	
	}

	@Override
	protected TypeToken<DCP_WarningUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningUpdateReq>(){};
	}

	@Override
	protected DCP_WarningUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningUpdateRes();
	}
	
	private String getBillNo(DCP_WarningUpdateReq req)  throws Exception 
	{
		String sql = null;
		String billNo = null;
		String shopId = req.getShopId();
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer("");
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

		String[] conditionValues = { eId, shopId }; // 查询要货单号
		billNo = "YJJK" + bDate;
		sqlbuf.append("" + "select BILLNO  from ( " + "select max(BILLNO) as BILLNO "
				+ "  from DCP_WARNING " + " where EID = ? " + " and SHOPID = ? "
				+ " and BILLNO like '%%" + billNo + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		if (getQData != null && getQData.isEmpty() == false) {
			billNo = (String) getQData.get(0).get("BILLNO");
			if (billNo != null && billNo.length() > 0) {
				long i;
				billNo = billNo.substring(4, billNo.length());
				i = Long.parseLong(billNo) + 1;
				billNo = i + "";
				billNo = "YJJK" + billNo;    
			} 
			else {
				billNo = "YJJK" + bDate + "00001";
			}
		} 
		else {
			billNo = "YJJK" + bDate + "00001";
		}

		return billNo;
	}

}
