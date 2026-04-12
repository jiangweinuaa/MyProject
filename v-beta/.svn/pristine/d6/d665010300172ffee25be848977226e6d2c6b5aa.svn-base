package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsShelfQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsShelfQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsShelfQuery extends SPosBasicService<DCP_GoodsShelfQueryReq, DCP_GoodsShelfQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsShelfQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_GoodsShelfQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsShelfQueryReq>(){};
	}

	@Override
	protected DCP_GoodsShelfQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsShelfQueryRes();
	}

	@Override
	protected DCP_GoodsShelfQueryRes processJson(DCP_GoodsShelfQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String sql = null;		
		//查詢資料
		DCP_GoodsShelfQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);	
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_GoodsShelfQueryRes.level1Elm>());
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
			
			/*condition.clear();		
			condition.put("PLUNO", true);
			condition.put("RANGETYPE", true);
			condition.put(" ID", true);
		  //调用过滤函数
			List<Map<String, Object>> getOffShelfRangeList=MapDistinct.getMap(getQData, condition);*/
			
			for (Map<String, Object> oneData : getHead) 
			{
				DCP_GoodsShelfQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String pluNo = oneData.get("PLUNO").toString();
				oneLv1.setPluNo(pluNo);
				oneLv1.setPluName(oneData.get("PLU_NAME").toString());
				oneLv1.setPluType(oneData.get("PLUTYPE").toString());
				oneLv1.setListImage(oneData.get("LISTIMAGE").toString());
				oneLv1.setListImageUrl(oneData.get("LISTIMAGE").toString());
				oneLv1.setMaxPrice(oneData.get("MAXPRICE").toString());
				oneLv1.setMinPrice(oneData.get("MINPRICE").toString());
				
				/*String offShelfStatus = "N";
				String status_offShelf = oneData.get("STATUS_OFFSHELF").toString();				
				if(status_offShelf.equals("0"))
				{
					offShelfStatus = "N";
				}
				else
				{
					offShelfStatus = "Y";
				}*/
				oneLv1.setStatus(oneData.get("STATUS").toString());
				
				oneLv1.setClassList(new ArrayList<DCP_GoodsShelfQueryRes.classMemu>());
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
					DCP_GoodsShelfQueryRes.classMemu mapModel = res.new classMemu();
					mapModel.setClassNo(pluNo_class);
					mapModel.setClassName(className);
					oneLv1.getClassList().add(mapModel);								
		
				}									
				/*oneLv1.setOffShelfRangeList(new ArrayList<DCP_GoodsShelfQueryRes.offShelfRange>());
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
					DCP_GoodsShelfQueryRes.offShelfRange mapModel = res.new offShelfRange();
					mapModel.setRangeType(rangeType);
					mapModel.setId(id);
					mapModel.setName(name);
					oneLv1.getOffShelfRangeList().add(mapModel);								
		
				}*/
				
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
	protected String getQuerySql(DCP_GoodsShelfQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		String channelId =req.getRequest().getChannelId();
		String shopId = req.getRequest().getShopId();
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		
		
		String classType = "ONLINE";
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" select * from ( ");
		sqlbuf.append(" select count(Distinct pluno) over() num,dense_rank() over(ORDER BY pluno) rn,A.* from (");
		
		sqlbuf.append(" select A.*,B.PRICE MINPRICE,B.PRICE MAXPRICE,BL.PLU_NAME,C.CLASSNO,CL.DISPLAYNAME CLASSNAME,P.LISTIMAGE ");
		sqlbuf.append(" from DCP_GOODS_ONLINE A ");
		sqlbuf.append(" left join  DCP_GOODS B on A.EID=B.EID and A.PLUNO=B.PLUNO and A.PLUTYPE=B.PLUTYPE ");
		sqlbuf.append(" left join  DCP_GOODS_LANG BL on A.EID=BL.EID and B.PLUNO=BL.PLUNO and BL.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" left join  DCP_CLASS_GOODS C ON A.EID=C.EID and A.PLUNO=C.PLUNO AND C.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join  DCP_CLASS_GOODS_LANG CL ON A.EID=CL.EID and C.CLASSNO=CL.CLASSNO and C.PLUNO=CL.PLUNO AND CL.CLASSTYPE='"+classType+"' ");
		//sqlbuf.append(" left join  DCP_GOODS_OFFSHELF D ON A.EID=D.EID and A.PLUNO=D.PLUNO and A.PLUTYPE=D.PLUTYPE ");
		//sqlbuf.append(" inner join goodstemplate  on A.EID=goodstemplate.EID and A.PLUNO=goodstemplate.PLUNO");
		//sqlbuf.append(" left join DCP_GOODS_OFFSHELF_RANGE R ON A.EID=R.EID and A.PLUNO=R.PLUNO and A.PLUTYPE=R.PLUTYPE");
		sqlbuf.append(" left join Dcp_Goodsimage P ON  A.EID=P.EID and A.PLUNO=P.PLUNO and A.PLUTYPE=P.PLUTYPE AND P.APPTYPE='ALL'");
		if(channelId!=null&&channelId.isEmpty()==false)
		{
			//如果传入channelId不为空，则
	        //显示的商品必须是在DCP_GOODS_SHELF_RANGE中渠道=当前传入渠道、SHOP=ALL且状态为上架【status=100】的商品。
			sqlbuf.append(" inner join DCP_GOODS_SHELF_RANGE R1 ON A.EID=R1.EID and A.PLUNO=R1.PLUNO and A.PLUTYPE=R1.PLUTYPE and  R1.SHOPID='ALL' and R1.STATUS='100' and R1.CHANNELID='"+channelId+"' ");
		}
		if(shopId!=null&&shopId.isEmpty()==false)
		{
			// 不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
			sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R2 ON A.EID=R2.EID and A.PLUNO=R2.PLUNO and A.PLUTYPE=R2.PLUTYPE and R2.STATUS='0' and R2.SHOPID='"+shopId+"' ");
			if(channelId!=null&&channelId.isEmpty()==false)
			{
				sqlbuf.append(" and R2.CHANNELID='"+channelId+"' ");
			}
		}		
		sqlbuf.append(" where A.EID='"+eId+"'  and A.PLUTYPE in ( 'FEATURE','NORMAL','PACKAGE') ");
		
		if(shopId!=null&&shopId.isEmpty()==false)
		{
			//不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
			sqlbuf.append(" AND (R2.status is null or R2.status<>'0') ");
		}
		
		sqlbuf.append(" Union All");
		
		sqlbuf.append(" select A.*,B.MINPRICE,B.MAXPRICE,BL.MASTERPLUNAME as PLU_NAME,CL.CLASSNO,CL.DISPLAYNAME  CLASSNAME,P.LISTIMAGE ");
		sqlbuf.append(" from DCP_GOODS_ONLINE A");
		sqlbuf.append(" left join  DCP_MSPECGOODS B on A.EID=B.EID and A.PLUNO=B.MASTERPLUNO ");
		sqlbuf.append(" left join  DCP_MSPECGOODS_LANG BL on A.EID=BL.EID and B.MASTERPLUNO=BL.MASTERPLUNO and BL.LANG_TYPE='"+langType+"' ");
		sqlbuf.append(" left join  DCP_CLASS_GOODS C ON A.EID=C.EID and A.PLUNO=C.PLUNO AND C.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join  DCP_CLASS_GOODS_LANG CL ON A.EID=CL.EID and C.CLASSNO=CL.CLASSNO and C.PLUNO=CL.PLUNO and CL.CLASSTYPE='"+classType+"' ");
		sqlbuf.append(" left join  DCP_MSPECGOODS_SUBGOODS sub on A.EID=sub.EID and A.PLUNO=sub.MASTERPLUNO ");
		//sqlbuf.append(" left join  DCP_GOODS_OFFSHELF D ON A.EID=D.EID and A.PLUNO=D.PLUNO  and A.PLUTYPE=D.PLUTYPE ");
		//sqlbuf.append(" left join DCP_GOODS_OFFSHELF_RANGE R ON  A.EID=R.EID and A.PLUNO=R.PLUNO and A.PLUTYPE=R.PLUTYPE");
		//sqlbuf.append(" inner join goodstemplate  on A.EID=goodstemplate.EID and sub.PLUNO=goodstemplate.PLUNO ");
		sqlbuf.append(" left join Dcp_Goodsimage P ON  A.EID=P.EID and A.PLUNO=P.PLUNO and A.PLUTYPE=P.PLUTYPE AND P.APPTYPE='ALL'");
		
		if(channelId!=null&&channelId.isEmpty()==false)
		{
			//如果传入channelId不为空，则
	        //显示的商品必须是在DCP_GOODS_SHELF_RANGE中渠道=当前传入渠道、SHOP=ALL且状态为上架【status=100】的商品。
			sqlbuf.append(" inner join DCP_GOODS_SHELF_RANGE R1 ON A.EID=R1.EID and A.PLUNO=R1.PLUNO and A.PLUTYPE=R1.PLUTYPE and  R1.SHOPID='ALL' and R1.STATUS='100' and R1.CHANNELID='"+channelId+"' ");
		}
		
		if(shopId!=null&&shopId.isEmpty()==false)
		{
			// 不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
			sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R2 ON A.EID=R2.EID and A.PLUNO=R2.PLUNO and A.PLUTYPE=R2.PLUTYPE and R2.STATUS='0' and R2.SHOPID='"+shopId+"' ");
			if(channelId!=null&&channelId.isEmpty()==false)
			{
				sqlbuf.append(" and R2.CHANNELID='"+channelId+"' ");
			}
		}	
		
		sqlbuf.append(" where A.EID='"+eId+"' and A.PLUTYPE='MULTISPEC' ");
		
		if(shopId!=null&&shopId.isEmpty()==false)
		{
			//不存在与 DCP_GOODS_SHELF_RANGE中 传入渠道 传入门店 且状态为已下架商品中【staus=0】
			sqlbuf.append(" AND (R2.status is null or R2.status<>'0') ");
		}
		
		
		sqlbuf.append(" ) A");
		
		
		
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		sql = sqlbuf.toString();
	  return sql;
	
	}

}
