package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsClassQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsClassQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsClassQuery extends SPosBasicService<DCP_GoodsClassQueryReq, DCP_GoodsClassQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
		String classType = req.getRequest().getClassType();
		
		if(Check.Null(classType))
		{
			errMsg.append("菜单分组类型不能为空值, ");
			isFail = true;
		}
		
    String pluNo = req.getRequest().getPluNo();
		
		if(Check.Null(classType))
		{
			errMsg.append("商品编码不能为空值 ");
			isFail = true;
		}
		
		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsClassQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsClassQueryReq>(){};
	}

	@Override
	protected DCP_GoodsClassQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsClassQueryRes();
	}

	@Override
	protected DCP_GoodsClassQueryRes processJson(DCP_GoodsClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_GoodsClassQueryRes res = this.getResponse();
		String sql = this.getQuerySql(req);
		res.setDatas(new ArrayList<DCP_GoodsClassQueryRes.level1Elm>());
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			for (Map<String, Object> map : getQData) 
			{
				DCP_GoodsClassQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setClassNo(map.get("CLASSNO").toString());
				oneLv1.setClassName(map.get("CLASSNAME").toString());
				
				res.getDatas().add(oneLv1);
		
			}
		}
		
		
		
		
	 return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_GoodsClassQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		
		sqlbuf.append(" select * from ( ");
		sqlbuf.append(" select A.*,B.CLASSNAME,B.DISPLAYNAME from DCP_CLASS_GOODS A");
		sqlbuf.append(" left join DCP_CLASS_lang B on A.EID=B.EID AND A.CLASSTYPE=B.CLASSTYPE AND A.CLASSNO=B.CLASSNO  and B.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" WHERE A.EID='"+eId+"' AND A.CLASSTYPE='"+req.getRequest().getClassType()+"' AND A.PLUNO='"+req.getRequest().getPluNo()+"' ");
		sqlbuf.append(" )");
		
		sql = sqlbuf.toString();
		return sql;
	}

}
