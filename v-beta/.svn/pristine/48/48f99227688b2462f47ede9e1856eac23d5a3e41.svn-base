package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_EstClearGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_EstClearGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_EstClearGoodsQuery extends SPosBasicService<DCP_EstClearGoodsQueryReq, DCP_EstClearGoodsQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_EstClearGoodsQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_EstClearGoodsQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_EstClearGoodsQueryReq>(){};
	}

	@Override
	protected DCP_EstClearGoodsQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_EstClearGoodsQueryRes();
	}

	@Override
	protected DCP_EstClearGoodsQueryRes processJson(DCP_EstClearGoodsQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_EstClearGoodsQueryRes res=new DCP_EstClearGoodsQueryRes();
		String sql="select A.*,B.PLU_NAME,C.UNIT_NAME from DCP_GOODS_CLEAR A left join "
			+ " DCP_GOODS_LANG B on A.EID=B.EID and A.pluno=B.pluno and B.LANG_TYPE='"+req.getLangType()+"' left join "
			+ " DCP_UNIT_LANG C on A.EID=B.EID and A.unit=C.unit and C.lang_type='"+req.getLangType()+"'  ";
		List<Map<String, Object>> goodsclearlist=this.doQueryData(sql, null);
		if(goodsclearlist!=null&&!goodsclearlist.isEmpty())
		{
			for (Map<String, Object> map : goodsclearlist) 
			{
				String ISCLEAR= map.get("ISCLEAR").toString();
				String QTY=map.get("QTY").toString().isEmpty()?"0":map.get("QTY").toString();
				double dqty=Double.parseDouble(QTY);
				if(ISCLEAR.equals("Y")||dqty<=0)
				{
					ISCLEAR="Y";
				}
				DCP_EstClearGoodsQueryRes.level1Elm lev1=res.new level1Elm();
				lev1.setPluName(map.get("PLU_NAME").toString());
				lev1.setIsClear(ISCLEAR);
				lev1.setPluno(map.get("PLUNO").toString());
				lev1.setQty(map.get("QTY").toString());
				lev1.setUnit(map.get("UNIT").toString());
				lev1.setUnitName(map.get("UNIT_NAME").toString());	
				res.getDatas().add(lev1);
				lev1 = null;
		 }
			
		}	
	return null;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_EstClearGoodsQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}
}
