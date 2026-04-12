package com.dsc.spos.waimai;

import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;

public class WMJBPService extends SWaimaiBasicService {

	@Override
	public String execute(String json) throws Exception {
		// TODO Auto-generated method stub
		String res_json = HelpTools.GetJBPResponse(json);
		if(res_json ==null ||res_json.length()==0)
		{
			return null;
		}
		Map<String, Object> res = new HashMap<String, Object>();
		this.processDUID(res_json, res);

		return null;

	}

	@Override
	protected void processDUID(String req, Map<String, Object> res) throws Exception {
		WMMTService wmmtService = new WMMTService();
		wmmtService.setDao(this.dao);
		wmmtService.processDUID(req,res);
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
