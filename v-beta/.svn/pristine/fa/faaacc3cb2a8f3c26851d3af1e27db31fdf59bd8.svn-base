package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderBasicSettingQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderBasicSettingQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderBasicSettingQueryRes.responseDatas;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderBasicSettingQuery extends SPosBasicService<DCP_OrderBasicSettingQueryReq,DCP_OrderBasicSettingQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderBasicSettingQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderBasicSettingQueryReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderBasicSettingQueryReq>(){};
	}

	@Override
	protected DCP_OrderBasicSettingQueryRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderBasicSettingQueryRes();
	}

	@Override
	protected DCP_OrderBasicSettingQueryRes processJson(DCP_OrderBasicSettingQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		DCP_OrderBasicSettingQueryRes res = this.getResponse();
		responseDatas datas = res.new responseDatas();
		datas.setSettingList(new ArrayList<DCP_OrderBasicSettingQueryRes.level1Elm>());
		res.setDatas(datas);
		
		String sql = this.getQuerySql(req);
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			for (Map<String, Object> map : getQData)
			{
				DCP_OrderBasicSettingQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setSettingNo(map.get("SETTINGNO").toString());
				oneLv1.setSettingName(map.get("SETTINGNO").toString());
				oneLv1.setSettingValue(map.get("SETTINGVALUE").toString());
				oneLv1.setConType(map.get("CONTYPE").toString());//控件类型（1.文本格式 2.数字格式 3.日期格式 4.时间格式 5.下拉框）
				oneLv1.setSelectType(map.get("SELECTTYPE").toString());//选择类型（0单选；1复选）
				datas.getSettingList().add(oneLv1);
				
			}
			
			res.setDatas(datas);
			
		}
		
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderBasicSettingQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		
		String eId = req.geteId();
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append("select * from DCP_ORDERBASICSETTING ");
		sqlBuff.append(" where EID='"+eId+"'");
		sqlBuff.append(" order by SETTINGNO ");
		
		return sqlBuff.toString();
	}

}
