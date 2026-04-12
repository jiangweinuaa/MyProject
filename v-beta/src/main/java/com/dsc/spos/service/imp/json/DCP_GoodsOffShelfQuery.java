package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsOffShelfQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsOffShelfQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsOffShelfQuery extends SPosBasicService<DCP_GoodsOffShelfQueryReq, DCP_GoodsOffShelfQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsOffShelfQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_GoodsOffShelfQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsOffShelfQueryReq>(){};
	}

	@Override
	protected DCP_GoodsOffShelfQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsOffShelfQueryRes();
	}

	@Override
	protected DCP_GoodsOffShelfQueryRes processJson(DCP_GoodsOffShelfQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String sql = null;		
		//查詢資料
		DCP_GoodsOffShelfQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);	
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_GoodsOffShelfQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件				
			condition.put("PLUNO", true);
		  //调用过滤函数
			List<Map<String, Object>> getHead=MapDistinct.getMap(getQData, condition);
			
			condition.clear();		
			condition.put("PLUNO", true);
			condition.put("CLASSNO", true);
		  //调用过滤函数
			List<Map<String, Object>> getClassList=MapDistinct.getMap(getQData, condition);
			
			condition.clear();		
			condition.put("PLUNO", true);
			condition.put("RANGETYPE", true);
			condition.put(" ID", true);
		  //调用过滤函数
			List<Map<String, Object>> getOffShelfRangeList=MapDistinct.getMap(getQData, condition);
			
			for (Map<String, Object> oneData : getHead) 
			{
				DCP_GoodsOffShelfQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String pluNo = oneData.get("PLUNO").toString();
				oneLv1.setPluNo(pluNo);
				oneLv1.setPluName(oneData.get("PLU_NAME").toString());
				oneLv1.setPluType(oneData.get("PLUTYPE").toString());
				oneLv1.setListImage(oneData.get("LISTIMAGE").toString());
				oneLv1.setListImageUrl(oneData.get("LISTIMAGE").toString());
				oneLv1.setMaxPrice(oneData.get("MAXPRICE").toString());
				oneLv1.setMinPrice(oneData.get("MINPRICE").toString());
				
				String offShelfStatus = "N";
				String status_offShelf = oneData.get("STATUS_OFFSHELF").toString();				
				if(status_offShelf.equals("0"))
				{
					offShelfStatus = "N";
				}
				else
				{
					offShelfStatus = "Y";
				}
				oneLv1.setOffShelfStatus(offShelfStatus);
				
				oneLv1.setClassList(new ArrayList<DCP_GoodsOffShelfQueryRes.classMemu>());
				for (Map<String, Object> map : getClassList) 
				{
					String pluNo_class = map.get("PLUNO").toString();
					String classNo = map.get("CLASSNO").toString();
					String className = map.get("CLASSNAME").toString();
					if(classNo==null||classNo.isEmpty())
					{
						continue;
					}
					if(pluNo_class.equals(pluNo)==false)
					{
						continue;
					}
					DCP_GoodsOffShelfQueryRes.classMemu mapModel = res.new classMemu();
					mapModel.setClassNo(pluNo_class);
					mapModel.setClassName(className);
					oneLv1.getClassList().add(mapModel);								
		
				}									
				oneLv1.setOffShelfRangeList(new ArrayList<DCP_GoodsOffShelfQueryRes.offShelfRange>());
				for (Map<String, Object> map : getOffShelfRangeList) 
				{
					String pluNo_offShelf = map.get("PLUNO").toString();
					String rangeType = map.get("RANGETYPE").toString();
					String id = map.get("ID").toString();
					String name = map.get("NAME").toString();
					if(rangeType==null||rangeType.isEmpty())
					{
						continue;
					}
					if(pluNo_offShelf.equals(pluNo)==false)
					{
						continue;
					}
					DCP_GoodsOffShelfQueryRes.offShelfRange mapModel = res.new offShelfRange();
					mapModel.setRangeType(rangeType);
					mapModel.setId(id);
					mapModel.setName(name);
					oneLv1.getOffShelfRangeList().add(mapModel);								
		
				}
				
				res.getDatas().add(oneLv1);
				
			}
			
		}		
		else
		{
			totalRecords = 0;
			totalPages = 0;			
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
	
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_GoodsOffShelfQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		
		
		String classType = "ONLINE";
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" select * from ( ");
		sqlbuf.append(" select count(Distinct pluno) over() num,dense_rank() over(ORDER BY pluno) rn,A.* from (");
		
		sqlbuf.append(" select A.*,B.PRICE MINPRICE,B.PRICE MAXPRICE,BL.PLU_NAME,C.CLASSNO,CL.DISPLAYNAME CLASSNAME,D.STATUS STATUS_OFFSHELF,R.RANGETYPE,R.ID,R.NAME,P.LISTIMAGE ");
		sqlbuf.append(" from DCP_GOODS_ONLINE A ");
		sqlbuf.append(" left join  DCP_GOODS B on A.EID=B.EID and A.PLUNO=B.PLUNO and A.PLUTYPE=B.PLUTYPE ");
		sqlbuf.append(" left join  DCP_GOODS_LANG BL on A.EID=BL.EID and B.PLUNO=BL.PLUNO and BL.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" left join  DCP_CLASS_GOODS C ON A.EID=C.EID and A.PLUNO=C.PLUNO AND C.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join  DCP_CLASS_GOODS_LANG CL ON A.EID=CL.EID and C.CLASSNO=CL.CLASSNO and C.PLUNO=CL.PLUNO AND CL.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join  DCP_GOODS_OFFSHELF D ON A.EID=D.EID and A.PLUNO=D.PLUNO and A.PLUTYPE=D.PLUTYPE ");
		//sqlbuf.append(" inner join goodstemplate  on A.EID=goodstemplate.EID and A.PLUNO=goodstemplate.PLUNO");
		sqlbuf.append(" left join DCP_GOODS_OFFSHELF_RANGE R ON A.EID=R.EID and A.PLUNO=R.PLUNO and A.PLUTYPE=R.PLUTYPE");
		sqlbuf.append(" left join Dcp_Goodsimage P ON  A.EID=P.EID and A.PLUNO=P.PLUNO and A.PLUTYPE=P.PLUTYPE AND P.APPTYPE='ALL'");
		sqlbuf.append(" where A.EID='"+eId+"'  and A.PLUTYPE in ( 'FEATURE','NORMAL','PACKAGE') ");
		
		sqlbuf.append(" Union All");
		
		sqlbuf.append(" select A.*,B.MINPRICE,B.MAXPRICE,BL.MASTERPLUNAME as PLU_NAME,CL.CLASSNO,CL.DISPLAYNAME  CLASSNAME,D.STATUS STATUS_OFFSHELF,R.RANGETYPE,R.ID,R.NAME ");
		sqlbuf.append(" from DCP_GOODS_ONLINE A");
		sqlbuf.append(" left join  DCP_MSPECGOODS B on A.EID=B.EID and A.PLUNO=B.MASTERPLUNO ");
		sqlbuf.append(" left join  DCP_MSPECGOODS_LANG BL on A.EID=BL.EID and B.MASTERPLUNO=BL.MASTERPLUNO and BL.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" left join  DCP_CLASS_GOODS C ON A.EID=C.EID and A.PLUNO=C.PLUNO AND C.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join  DCP_CLASS_GOODS_LANG CL ON A.EID=CL.EID and C.CLASSNO=CL.CLASSNO and C.PLUNO=CL.PLUNO and CL.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join  DCP_MSPECGOODS_SUBGOODS sub on A.EID=sub.EID and A.PLUNO=sub.MASTERPLUNO ");
		sqlbuf.append(" left join  DCP_GOODS_OFFSHELF D ON A.EID=D.EID and A.PLUNO=D.PLUNO  and A.PLUTYPE=D.PLUTYPE ");
		sqlbuf.append(" left join DCP_GOODS_OFFSHELF_RANGE R ON  A.EID=R.EID and A.PLUNO=R.PLUNO and A.PLUTYPE=R.PLUTYPE");
		//sqlbuf.append(" inner join goodstemplate  on A.EID=goodstemplate.EID and sub.PLUNO=goodstemplate.PLUNO ");
		sqlbuf.append(" left join Dcp_Goodsimage P ON  A.EID=P.EID and A.PLUNO=P.PLUNO and A.PLUTYPE=P.PLUTYPE AND P.APPTYPE='ALL'");
		sqlbuf.append(" where A.EID='"+eId+"' and A.PLUTYPE='MULTISPEC' ");
		
		sqlbuf.append(" ) A");
		
		
		
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		sql = sqlbuf.toString();
	  return sql;
	
	}

}
