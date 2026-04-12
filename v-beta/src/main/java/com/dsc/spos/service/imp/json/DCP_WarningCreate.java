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
import com.dsc.spos.json.cust.req.DCP_WarningCreateReq;
import com.dsc.spos.json.cust.req.DCP_WarningCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_WarningCreateReq.level2PushMan;
import com.dsc.spos.json.cust.req.DCP_WarningCreateReq.level2Shop;
import com.dsc.spos.json.cust.req.DCP_WarningCreateReq.level3PushWay;
import com.dsc.spos.json.cust.res.DCP_WarningCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningCreate extends SPosAdvanceService<DCP_WarningCreateReq, DCP_WarningCreateRes> {

	@Override
	protected void processDUID(DCP_WarningCreateReq req, DCP_WarningCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		level1Elm requestModel =	req.getRequest();
		String companyId = requestModel.getCompanyId();
		String shopId = requestModel.getShopId();
		
		String billDate = requestModel.getBillDate();
		
		
		
		String warningType = requestModel.getWarningType();
		String warningItem = requestModel.getWarningItem();
		String pushTimeType = requestModel.getPushTimeType();
		List<level2PushMan> pushManList = requestModel.getPushManList();
		String restrictShop = requestModel.getRestrictShop();
		String templateType = requestModel.getTemplateType();
		String status = requestModel.getStatus();
		String createopId = requestModel.getCreateopid();
		String createopName = req.getOpName();
		String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//requestModel.getCreateTime();
		
		String billNo = this.getBillNo(req);
		String billType = "YJJK";
		res.setDatas(new ArrayList<DCP_WarningCreateRes.level1Elm>());
		//DCP_WARNING预警
		String[] columns_warning ={"EID","BILLNO","BILLNAME","BILLTYPE","BILLDATE","COMPANYID","SHOPID","CHANNELID",
				"EMPLOYEEID","DEPARTID","WARNINGTYPE","WARNINGITEM","STARTTIME","ENDTIME","ORDERQTY","ORDERAMT","POINTQTY",
				"TEMPLATETYPE","PUSHTIMETYPE","PUSHTIME","RESTRICTSHOP","MEMO","STATUS","CREATEOPID","CREATEOPNAME","CREATETIME"
		};
		DataValue[] insValue_warning = new DataValue[] 
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(billNo, Types.VARCHAR), 
						new DataValue(requestModel.getBillName(), Types.VARCHAR),
						new DataValue(billType, Types.VARCHAR),
						new DataValue(billDate, Types.DATE),
						new DataValue(companyId==null?"":companyId, Types.VARCHAR),
						new DataValue(shopId==null?"":shopId, Types.VARCHAR),
						new DataValue(requestModel.getChannelId()==null?"":requestModel.getChannelId(), Types.VARCHAR),
						new DataValue(requestModel.getEmployeeId()==null?"":requestModel.getEmployeeId(), Types.VARCHAR),
						new DataValue(requestModel.getDepartId()==null?"":requestModel.getDepartId(), Types.VARCHAR),
						new DataValue(warningType==null?"":warningType, Types.VARCHAR),
						new DataValue(warningItem==null?"":warningItem, Types.VARCHAR),
						new DataValue(requestModel.getStartTime()==null?"":requestModel.getStartTime(), Types.VARCHAR),
						new DataValue(requestModel.getEndTime()==null?"":requestModel.getEndTime(), Types.VARCHAR),
						new DataValue(requestModel.getOrderQty()==null?"0":requestModel.getOrderQty(), Types.VARCHAR),
						new DataValue(requestModel.getOrderAmt()==null?"0":requestModel.getOrderAmt(), Types.VARCHAR),
						new DataValue(requestModel.getPointQty()==null?"0":requestModel.getPointQty(), Types.VARCHAR),
						new DataValue(templateType==null?"":templateType, Types.VARCHAR),
						new DataValue(pushTimeType==null?"":pushTimeType, Types.VARCHAR),
						new DataValue(requestModel.getPushTime()==null?"":requestModel.getPushTime(), Types.VARCHAR),
						new DataValue(restrictShop==null?"":restrictShop, Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(createopId, Types.VARCHAR),
						new DataValue(createopName, Types.VARCHAR),
						new DataValue(createTime, Types.DATE)				
				};
		
		InsBean ib_warning = new InsBean("DCP_WARNING", columns_warning);
		ib_warning.addValues(insValue_warning);
		this.addProcessData(new DataProcessBean(ib_warning)); 
	
		//
		if(pushManList!=null&&pushManList.isEmpty()==false)
		{
		
			String[] columns_warning_pushMan = { "EID", "BILLNO", "SERIALNO", "OPNO", "MOBILEPHONE", "EMAIL", "LASTMODIOPID",
				"LASTMODIOPNAME", "LASTMODITIME"
	
			};
			int serialNo_pushMan = 1;
			int serialNo_pushWay = 1;//全局
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
							new DataValue(pushMan.getMobilePhone()==null?"":pushMan.getMobilePhone(), Types.VARCHAR),
							new DataValue(pushMan.getEmail()==null?"":pushMan.getEmail(), Types.VARCHAR),
							new DataValue(createopId, Types.VARCHAR),
							new DataValue(createopName, Types.VARCHAR),
							new DataValue(createTime, Types.DATE)
								
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
					//int serialNo_pushWay = 1;
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
									new DataValue(createopId, Types.VARCHAR),
									new DataValue(createopName, Types.VARCHAR),
									new DataValue(createTime, Types.DATE)
										
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
			int serialNo_pushShop = 1;
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
			DCP_WarningCreateRes.level1Elm res_level1Elm =  res.new level1Elm();
			res_level1Elm.setBillNo(billNo);
			res.getDatas().add(res_level1Elm);
			
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
	protected List<InsBean> prepareInsertData(DCP_WarningCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_WarningCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_WarningCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_WarningCreateReq req) throws Exception {
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
	protected TypeToken<DCP_WarningCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningCreateReq>(){};
	}

	@Override
	protected DCP_WarningCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningCreateRes();
	}
	
	private String getBillNo(DCP_WarningCreateReq req)  throws Exception 
	{
		String sql = null;
		String billNo = null;
		String shopId = req.getShopId();
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer("");
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

	
		billNo = "YJJK" + bDate;
		sqlbuf.append("" + "select BILLNO  from ( " + "select max(BILLNO) as BILLNO "
				+ "  from DCP_WARNING " + " where eid ='" + eId
				+ "' and BILLNO like '%%" + billNo + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

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
