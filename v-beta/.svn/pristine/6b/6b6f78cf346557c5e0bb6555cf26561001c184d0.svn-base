package com.dsc.spos.waimai;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.waimai.entity.orderLoadDocType;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;

public class WMJBPTokenService extends SWaimaiBasicService {

	@Override
	public String execute(String json) throws Exception {
		// TODO Auto-generated method stub
		String res_json = HelpTools.GetJBPTokenResponse(json);
		if (res_json == null || res_json.length() == 0) {
			return null;
		}
		Map<String, Object> res = new HashMap<String, Object>();
		this.processDUID(res_json, res);

		return null;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
	// TODO Auto-generated method stub
		String shopLogFileName = "ShopsSaveLocal";
		try 
		{
			JSONObject obj = new JSONObject(req);
			String eId = obj.get("eId").toString();
			String erpShopNo = obj.get("erpShopNo").toString();
			String erpShopName = obj.optString("erpShopName","");
			String orderShopNo = obj.get("orderShopNo").toString();
			String orderShopName = obj.get("orderShopName").toString();
			String appAuthToken = obj.get("appAuthToken").toString();
			String businessId = obj.get("businessId").toString();
			String appKey = obj.get("appKey").toString();
			String appName = obj.get("appName").toString();
			String appSecret = obj.get("appSecret").toString();
			String isTest = obj.get("isTest").toString();
			String isJbp = obj.get("isJbp").toString();
			String channelId = obj.get("channelId").toString();
			String mappingShopNo = obj.get("mappingShopNo").toString();
			String customerNo = obj.optString("customerNo"," ");
			String loadDocType = orderLoadDocType.MEITUAN;//美团渠道类型
			String mappingShopInfo = obj.toString();

			erpShopName = getErpShopName(eId, erpShopNo);

			if (erpShopName != null && erpShopName.length() > 255)
			{
				erpShopName = erpShopName.substring(0, 254);
			}
			if (orderShopName != null && orderShopName.length() > 255)
			{
				orderShopName = orderShopName.substring(0, 254);
			}

			// 映射的门店资料保存到数据库 存在就更新，不存在就插入
			if (this.IsExistOnlineShop(eId, loadDocType, orderShopNo, businessId))
			{
				UptBean ub1 = new UptBean("DCP_MAPPINGSHOP");
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
				ub1.addCondition("ORDERSHOPNO", new DataValue(orderShopNo, Types.VARCHAR));
				ub1.addCondition("BUSINESSID", new DataValue(businessId, Types.VARCHAR));


				if(channelId!=null&&channelId.isEmpty()==false)
				{
					ub1.addUpdateValue("CHANNELID", new DataValue(channelId, Types.VARCHAR));
				}
				ub1.addUpdateValue("ORGANIZATIONNO", new DataValue(erpShopNo, Types.VARCHAR));
				ub1.addUpdateValue("SHOPID", new DataValue(erpShopNo, Types.VARCHAR));
				ub1.addUpdateValue("SHOPNAME", new DataValue(erpShopName, Types.VARCHAR));
				ub1.addUpdateValue("ORDERSHOPNAME", new DataValue(orderShopName, Types.VARCHAR));
				ub1.addUpdateValue("APPKEY", new DataValue(appKey, Types.VARCHAR));
				ub1.addUpdateValue("APPSECRET", new DataValue(appSecret, Types.VARCHAR));
				ub1.addUpdateValue("APPNAME", new DataValue(appName, Types.VARCHAR));
				ub1.addUpdateValue("ISTEST", new DataValue(isTest, Types.VARCHAR));
				ub1.addUpdateValue("APPAUTHTOKEN", new DataValue(appAuthToken, Types.VARCHAR));
				ub1.addUpdateValue("ISJBP", new DataValue(isJbp, Types.VARCHAR));
				ub1.addUpdateValue("MAPPINGSHOPNO", new DataValue(mappingShopNo, Types.VARCHAR));
				ub1.addUpdateValue("MAPPINGSHOPINFO", new DataValue(mappingShopInfo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));
				this.doExecuteDataToDB();
				HelpTools.writelog_fileName("【门店映射保存成功】" + " 映射后门店编号mappingShopNO=" + mappingShopNo, shopLogFileName);
			}
			else
			{
				String[] columns1 =
						{ "EID","CUSTOMERNO", "ORGANIZATIONNO", "SHOPID", "LOAD_DOCTYPE", "BUSINESSID", "SHOPNAME", "ORDERSHOPNO",
								"ORDERSHOPNAME", "APPAUTHTOKEN", "MAPPINGSHOPNO", "MAPPINGSHOPINFO", "APPKEY", "APPSECRET",
								"APPNAME", "ISTEST", "ISJBP","CHANNELID" };
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]
						{ new DataValue(eId, Types.VARCHAR),
								new DataValue(customerNo, Types.VARCHAR),
								new DataValue(erpShopNo, Types.VARCHAR), // 组织编号=门店编号
								new DataValue(erpShopNo, Types.VARCHAR), // ERP门店
								new DataValue(loadDocType, Types.VARCHAR), //渠道类型
								new DataValue(businessId, Types.VARCHAR), // 1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
								new DataValue(erpShopName, Types.VARCHAR), // ERP门店名称
								new DataValue(orderShopNo, Types.VARCHAR), // 外卖平台门店ID
								new DataValue(orderShopName, Types.VARCHAR), // 外卖平台门店名称
								new DataValue(appAuthToken, Types.VARCHAR), // token
								new DataValue(mappingShopNo, Types.VARCHAR), // 缓存里面的key（99_10001）
								new DataValue(mappingShopInfo, Types.VARCHAR), // 缓存里面的value(json格式)
								new DataValue(appKey, Types.VARCHAR), new DataValue(appSecret, Types.VARCHAR),
								new DataValue(appName, Types.VARCHAR), new DataValue(isTest, Types.VARCHAR),
								new DataValue(isJbp, Types.VARCHAR),new DataValue(channelId, Types.VARCHAR) };

				InsBean ib1 = new InsBean("DCP_MAPPINGSHOP", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));

				this.doExecuteDataToDB();
				HelpTools.writelog_fileName("【门店映射保存成功】" + "美团门店id='"+orderShopNo+",映射后门店编号mappingShopNO:" + mappingShopNo, shopLogFileName);
			}

		}
		catch (SQLException e)
		{
			HelpTools.writelog_fileName("【门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req, shopLogFileName);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			HelpTools.writelog_fileName("【门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req, shopLogFileName);
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(String req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}
	
	private String getErpShopName (String eId,String erpShopNo) throws Exception
	{
		try 
		{
			String sql =" select * from dcp_org_lang where EID='"+eId+"' and organizationno='"+erpShopNo+"' and LANG_TYPE='zh_CN'";
		  List<Map<String, Object>> shopsName =	this.doQueryData(sql, null);
			if (shopsName != null && shopsName.isEmpty() == false)
		  {
				return shopsName.get(0).get("ORG_NAME").toString();		  	
		  }
		
	  } 
		catch (Exception e) 
		{
		
	  }
		  
	  return "";
	}

	/**
	 * 线上的门店是否已经存在本地了
	 * @param eId
	 * @param loadDocType
	 * @param orderShopNO
	 * @param businessID
	 * @return
	 * @throws Exception
	 */
	private boolean IsExistOnlineShop(String eId,String loadDocType,String orderShopNO,String businessID) throws Exception
	{
		boolean isFlag = false;
		String sql = " select * from DCP_MAPPINGSHOP where EID='"+eId+"'";
		sql += " and LOAD_DOCTYPE='"+loadDocType+"' and ORDERSHOPNO='"+orderShopNO+"'";
		if (businessID != null && businessID.isEmpty() == false)
		{
			sql += " and BUSINESSID='"+businessID+"'";
		}
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			isFlag = true;
		}

		return isFlag;

	}

}
