package com.dsc.spos.waimai;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;

public class WLRRKDProcess extends SWaimaiBasicService
{

	@Override
	public String execute(String json) throws Exception {
	// TODO Auto-generated method stub
		JSONObject obj = new JSONObject(json);
		//这里接受到信息之后需要更新单据的物流状态
		String msgType=obj.getString("msgType");
		String orderNo=obj.getString("orderNo");
		String businessNo=obj.getString("businessNo");
		UptBean up1=new UptBean("TV_ORDER");
		up1.addUpdateValue(msgType, new DataValue("DeliveryStutas", Types.VARCHAR));
		
		up1.addCondition(businessNo, new DataValue("ORDERNO", Types.VARCHAR));
		up1.addCondition(orderNo, new DataValue("DeliveryNO", Types.VARCHAR));
		this.pData.add(new DataProcessBean(up1));
		this.doExecuteDataToDB();
		
	  return null;
	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
	// TODO Auto-generated method stub
	
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
