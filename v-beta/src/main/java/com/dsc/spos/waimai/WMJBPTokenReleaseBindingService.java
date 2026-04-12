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

public class WMJBPTokenReleaseBindingService extends SWaimaiBasicService {

	@Override
	public String execute(String json) throws Exception {
		// TODO Auto-generated method stub
		String res_json = HelpTools.GetJBPTokenReleaseBindingResponse(json);
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
			String businessId = obj.get("businessId").toString();
			String mappingShopNo = obj.get("mappingShopNo").toString();
			String customerNo = obj.optString("customerNo"," ");
			String loadDocType = orderLoadDocType.MEITUAN;//美团渠道类型
			if (mappingShopNo==null||mappingShopNo.trim().isEmpty())
			{
				HelpTools.writelog_fileName("【解绑JBP门店映射】"+" 映射的门店编号mappingShopNO为空",shopLogFileName);
				return;
			}
			DelBean db1 = null;	
			db1 = new DelBean("DCP_MAPPINGSHOP");
			//db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			//db1.addCondition("ORGANIZATIONNO", new DataValue(erpShopNO, Types.VARCHAR));
			//db1.addCondition("SHOP", new DataValue(erpShopNO, Types.VARCHAR));
			db1.addCondition("LOAD_DOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
			db1.addCondition("MAPPINGSHOPNO", new DataValue(mappingShopNo, Types.VARCHAR));
			db1.addCondition("BUSINESSID", new DataValue(businessId, Types.VARCHAR));
			db1.addCondition("ISJBP", new DataValue("Y", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
									
			this.doExecuteDataToDB();	
			HelpTools.writelog_fileName("【解绑JBP门店映射数据库删除成功】"+" 映射的门店编号mappingShopNO="+mappingShopNo,shopLogFileName);
		
	  } 
		catch (SQLException e) 
		{
			HelpTools.writelog_fileName("【解绑JBP门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req,shopLogFileName);
	  }
		catch (Exception e) 
		{
		  // TODO: handle exception
			HelpTools.writelog_fileName("【解绑JBP门店映射执行语句】异常：" + e.getMessage() + "\r\n req请求内容:" + req,shopLogFileName);
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

}
