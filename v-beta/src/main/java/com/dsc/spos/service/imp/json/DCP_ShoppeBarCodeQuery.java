package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ShoppeBarCodeQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShoppeBarCodeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 专柜条码设置
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_ShoppeBarCodeQuery extends SPosBasicService<DCP_ShoppeBarCodeQueryReq, DCP_ShoppeBarCodeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ShoppeBarCodeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String shopId = req.getRequest().getShopId();
		
		if (Check.Null(shopId)  ) 
		{
			errMsg.append("门店编号不能为空！ ");
			isFail = true;
		}	
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ShoppeBarCodeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ShoppeBarCodeQueryReq>(){};
	}

	@Override
	protected DCP_ShoppeBarCodeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ShoppeBarCodeQueryRes();
	}

	@Override
	protected DCP_ShoppeBarCodeQueryRes processJson(DCP_ShoppeBarCodeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ShoppeBarCodeQueryRes res = null;
		res = this.getResponse();
		
		String sql = null;
		sql = this.getQuerySql(req);
		String[] conditionValues = {};
		
		List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);
		
		if(getDatas.size() > 0){
			for (Map<String, Object> oneData : getDatas) 
			{
				String shopId = oneData.get("SHOPID").toString();
				String barSecEn1 = oneData.get("BARSECEN1").toString();
				String barSec1 = oneData.get("BARSEC1").toString();
				String barSecEn2= oneData.get("BARSECEN2").toString();
				String barSec2= oneData.get("BARSEC2").toString();
				String barSecEn3= oneData.get("BARSECEN3").toString();
				String barSec3= oneData.get("BARSEC3").toString();
				String barSecEn4= oneData.get("BARSECEN4").toString();
				String barSec4 = oneData.get("BARSEC4").toString();
				String barSecEn5 = oneData.get("BARSECEN5").toString();
				String barSec5 = oneData.get("BARSEC5").toString();
				
				String amtLen = oneData.get("AMTLEN").toString();
				String createDate = oneData.get("CREATEDATE").toString();
				String createTime = oneData.get("CREATETIME").toString();
				String createBy = oneData.get("CREATEBY").toString();
				String createByName = oneData.get("CREATEBYNAME").toString();
				String modifyDate = oneData.get("MODIFYDATE").toString();
				String modifyTime = oneData.get("MODIFYTIME").toString();
				String modifyBy = oneData.get("MODIFYBY").toString();
				String modifyByName = oneData.get("MODIFYBYNAME").toString();
				String status = oneData.get("STATUS").toString();
				String updateTime = oneData.get("UPDATETIME").toString();
				
				res.setShopId(shopId);
				res.setBarSec1(barSec1);
				res.setBarSec2(barSec2);
				res.setBarSec3(barSec3);
				res.setBarSec4(barSec4);
				res.setBarSec5(barSec5);
				res.setBarSecEn1(barSecEn1);
				res.setBarSecEn2(barSecEn2);
				res.setBarSecEn3(barSecEn3);
				res.setBarSecEn4(barSecEn4);
				res.setBarSecEn5(barSecEn5);
				
				res.setAmtLen(amtLen);
				res.setCreateBy(createBy);
				res.setCreateByName(createByName);
				res.setCreateDate(createDate);
				res.setCreateTime(createTime);
				res.setModifyBy(modifyBy);
				res.setModifyByName(modifyByName);
				res.setModifyDate(modifyDate);
				res.setModifyTime(modifyTime);
				res.setStatus(status);
				res.setUpdateTime(updateTime);
				
			}
		}
		else{
			
		}
			
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ShoppeBarCodeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String shopId = req.getRequest().getShopId();
		String langType = req.getLangType();
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		
		sqlbuf.append("select * from ( select a.EID, a.SHOPID as SHOPID , BAR_SECTION1_ENABLE as barSecEn1 , BAR_SECTION1 as barSec1 , "
		+ " BAR_SECTION2_ENABLE as barSecEn2 , BAR_SECTION2 as barSec2 , BAR_SECTION3_ENABLE as barSecEn3 ,"
		+ " BAR_SECTION3 as barSec3 , BAR_SECTION4_ENABLE as barSecEn4 , BAR_SECTION4 as barSec4 , "
		+ " BAR_SECTION5_ENABLE as barSecEn5 , BAR_SECTION5 as barSec5 , AMT_LEN  as AMTLEN ,"
		+ " a.CREATEBY ,a.CREATE_DATE as createDate ,a.CREATE_TIME as createTime, a.MODIFYBY ,a.MODIFY_DATE as modifyDate , "
		+ " a.MODIFY_TIME as modifyTime ,a.status , b.op_Name as createByName , c.op_Name as modifyByName , "
		+ " a.UPDATE_TIME as upDateTime from  DCP_SHOPPE_BARCODE a "
		+ " left join platform_staffs_lang b on a.EID = b.EID and a.createBy = b.opNO and b.lang_Type = '"+langType+"' " 
		+ " left join platform_staffs_lang c on a.EID = c.EID and a.modifyBy = c.opNO and c.lang_Type = '"+langType+"' " 
		+ " ) where SHOPID = '"+shopId+"' and EID='"+req.geteId()+"' " );
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	
}
