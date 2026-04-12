package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ShoppeBarCodeUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ShoppeBarCodeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 专柜条码设置
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_ShoppeBarCodeUpdate extends SPosAdvanceService<DCP_ShoppeBarCodeUpdateReq, DCP_ShoppeBarCodeUpdateRes> {

	@Override
	protected void processDUID(DCP_ShoppeBarCodeUpdateReq req, DCP_ShoppeBarCodeUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId(); 
			String shopId = req.getShopId();
			String barSecEn1 = req.getRequest().getBarSecEn1();
			String barSec1 = req.getRequest().getBarSec1();
			String barSecEn2 = req.getRequest().getBarSecEn2();
			String barSec2 = req.getRequest().getBarSec2();
			String barSecEn3 = req.getRequest().getBarSecEn3();
			String barSec3 = req.getRequest().getBarSec3();
			String barSecEn4 = req.getRequest().getBarSecEn4();
			String barSec4 = req.getRequest().getBarSec4(); 
			String barSecEn5 = req.getRequest().getBarSecEn5();
			String barSec5 = req.getRequest().getBarSec5();
			
			String amtLen = req.getRequest().getAmtLen();
			
			String opNO = req.getOpNO();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
			String modifyDate = dfDate.format(cal.getTime());
			String modifyTime = dfTime.format(cal.getTime());
			String status = req.getRequest().getStatus();
			
			DelBean db1 = new DelBean("DCP_SHOPPE_BARCODE");
			db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));		
			
			
			int insColCt = 0;  
			String[] columnsName = {
					 "EID","SHOPID",
					 "BAR_SECTION1_ENABLE","BAR_SECTION1",
					 "BAR_SECTION2_ENABLE","BAR_SECTION2",
					 "BAR_SECTION3_ENABLE","BAR_SECTION3",
					 "BAR_SECTION4_ENABLE","BAR_SECTION4",
					 "BAR_SECTION5_ENABLE","BAR_SECTION5",
					 "AMT_LEN","CREATEBY","CREATE_DATE","CREATE_TIME",
					 "MODIFYBY","MODIFY_DATE","MODIFY_TIME",
					 "STATUS"
					 
			};
		
			DataValue[] columnsVal = new DataValue[columnsName.length];
			for (int i = 0; i < columnsVal.length; i++) { 
				String keyVal = null;
				switch (i) { 
				case 0:
					keyVal = eId;
					break;
				case 1:
					keyVal = shopId;
					break;
				case 2:
					keyVal = barSecEn1;
					break;
				case 3:
					keyVal = barSec1;
					break;
				case 4:
					keyVal = barSecEn2;
					break;
				case 5:
					keyVal = barSec2;
					break;
				case 6:
					keyVal = barSecEn3;
					break;
				case 7:
					keyVal = barSec3;
					break;
				case 8:
					keyVal = barSecEn4;
					break;
				case 9:
					keyVal = barSec4;
					break;
				case 10:
					keyVal = barSecEn5;
					break;
				case 11:
					keyVal = barSec5;
					break;
					
				case 12:
					keyVal = amtLen;
					break;
				case 13:
					keyVal = opNO;
					break;
				case 14:
					keyVal = modifyDate;
					break;
				case 15:
					keyVal = modifyTime;
					break;
				case 16:
					keyVal = opNO;
					break;
				case 17:
					keyVal = modifyDate;
					break;
				case 18:
					keyVal = modifyTime;
					break;
				case 19:
					keyVal = status;
					break;
				default:
					break;
				}
				if (keyVal != null) 
	          	{
	        	  	insColCt++;
	        	  	columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
	          	} 
			  	else 
			  	{
				  	columnsVal[i] = null;
			  	}
				
			}
			String[] columns2  = new String[insColCt];
			DataValue[] insValue2 = new DataValue[insColCt];
			// 依照傳入參數組譯要insert的欄位與數值；
			insColCt = 0;

			for (int i=0;i<columnsVal.length;i++){
				if (columnsVal[i] != null){
					columns2[insColCt] = columnsName[i];
					insValue2[insColCt] = columnsVal[i];
					insColCt ++;
					if (insColCt >= insValue2.length) 
						break;
				}
			}
			InsBean ib2 = new InsBean("DCP_SHOPPE_BARCODE", columns2);
			ib2.addValues(insValue2);
			this.addProcessData(new DataProcessBean(ib2));
			
			this.doExecuteDataToDB();
			if (res.isSuccess()) 
			{
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");						
			} 
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ShoppeBarCodeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ShoppeBarCodeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ShoppeBarCodeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ShoppeBarCodeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String shopId = req.getShopId();
		
		if (Check.Null(shopId)  ) 
		{
			errMsg.append("门店编号不能为空！ ");
			isFail = true;
		}	
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ShoppeBarCodeUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ShoppeBarCodeUpdateReq>(){};
	}

	@Override
	protected DCP_ShoppeBarCodeUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ShoppeBarCodeUpdateRes();
	}

}
