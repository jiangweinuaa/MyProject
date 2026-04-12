package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_RegeisterModuleInfoQueryReq;
import com.dsc.spos.json.cust.res.DCP_RegeisterModuleInfoQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_RegeisterModuleInfoQuery extends SPosBasicService<DCP_RegeisterModuleInfoQueryReq,DCP_RegeisterModuleInfoQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_RegeisterModuleInfoQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_RegeisterModuleInfoQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_RegeisterModuleInfoQueryReq>() {};
	}

	@Override
	protected DCP_RegeisterModuleInfoQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_RegeisterModuleInfoQueryRes();
	}

	@Override
	protected DCP_RegeisterModuleInfoQueryRes processJson(DCP_RegeisterModuleInfoQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_RegeisterModuleInfoQueryRes res= this.getResponse();

		String langsqlString="";
		if(req.getLangType().equals("zh_CN"))
		{
			langsqlString=" and (A.RPATTERN='1' or A.RPATTERN='3' ) ";
		}
		else
		{
			langsqlString=" and (A.RPATTERN='2' or A.RPATTERN='3' ) ";
		}
		
		String sql="select A.*,B.RMODULAR_NAME,B.RFUNC_NAME,B.RFUNC_INFO from DCP_REGEDISTMODULAR A left join DCP_REGEDISTMODULAR_LANG B "
				+ " on A.RFUNCNO=B.RFUNCNO "
				+ " where B.lang_type='"+req.getLangType()+"' " + langsqlString +" order by A.RMODULARNO,cast(A.RFUNCNO as int)  ";
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
		if(getQDataDetail!=null&&!getQDataDetail.isEmpty())
		{
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			condition.put("RMODULARNO", true);
			res.setDatas(new ArrayList<DCP_RegeisterModuleInfoQueryRes.level1Elm>());
			//调用过滤函数
			List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
			for (Map<String, Object> map : getQHeader) 
			{
				DCP_RegeisterModuleInfoQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setrModularNo(map.get("RMODULARNO").toString());
				lv1.setrModularName(map.get("RMODULAR_NAME").toString());
				lv1.setDatas_Func(new ArrayList<DCP_RegeisterModuleInfoQueryRes.level2Elm>());
				for (Map<String, Object> map2 : getQDataDetail) 
				{
					if(map.get("RMODULARNO").toString().equals(map2.get("RMODULARNO").toString()))
					{
						DCP_RegeisterModuleInfoQueryRes.level2Elm lv2=res.new level2Elm();
						lv2.setrFuncNo(map2.get("RFUNCNO").toString());
						lv2.setrFuncName(map2.get("RFUNCNAME").toString());
						lv2.setrTypeInfo(map2.get("RTYPEINFO").toString());
						lv2.setrFuncInfo(map2.get("RFUNCINFO").toString());
						lv1.getDatas_Func().add(lv2);
					}
				}
				res.getDatas().add(lv1);
			}
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_RegeisterModuleInfoQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
