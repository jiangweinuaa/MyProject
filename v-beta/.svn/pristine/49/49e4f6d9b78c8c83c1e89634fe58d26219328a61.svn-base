package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsShelfShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsShelfShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsShelfShopQuery extends SPosBasicService<DCP_GoodsShelfShopQueryReq, DCP_GoodsShelfShopQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsShelfShopQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_GoodsShelfShopQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsShelfShopQueryReq>(){};
	}

	@Override
	protected DCP_GoodsShelfShopQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsShelfShopQueryRes();
	}

	@Override
	protected DCP_GoodsShelfShopQueryRes processJson(DCP_GoodsShelfShopQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String sql = null;		
		//查詢資料
		DCP_GoodsShelfShopQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);	
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords = 0 ; //总笔数
		int totalPages = 0 ;				
		
		res.setDatas(new ArrayList<DCP_GoodsShelfShopQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{	

			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);


			if(req.getPageNumber() != 0 && req.getPageSize() !=0){
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;		
			}
			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件				
			condition.put("CHANNELID", true);
		  //调用过滤函数
			List<Map<String, Object>> getHead=MapDistinct.getMap(getQData, condition);
					
			for (Map<String, Object> oneData : getHead) 
			{
				DCP_GoodsShelfShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String channelId = oneData.get("CHANNELID").toString();
				
				String status_channel = oneData.get("STATUS_CHANNEL").toString(); 
				String status_shop = oneData.get("STATUS_SHOP").toString();

				String onShelfAuto = Check.Null(oneData.get("ONSHELFAUTO").toString() ) ? "0" : oneData.get("ONSHELFAUTO").toString();
				String onShelfDate = oneData.get("ONSHELFDATE").toString();
				String onShelfTime = oneData.get("ONSHELFTIME").toString();
				//OFFSHELFAUTO
				String offShelfAuto = Check.Null(oneData.get("OFFSHELFAUTO").toString() ) ? "0" : oneData.get("OFFSHELFAUTO").toString();
				String offShelfDate = oneData.get("OFFSHELFDATE").toString();
				String offShelfTime = oneData.get("OFFSHELFTIME").toString();
				
				oneLv1.setChannelId(channelId);
				oneLv1.setChannelName(oneData.get("CHANNELNAME").toString());
				oneLv1.setShopId(oneData.get("SHOPID").toString());	
				oneLv1.setShopName(oneData.get("SHOPNAME").toString());

				oneLv1.setOnShelfAuto(onShelfAuto);
				oneLv1.setOnShelfDate(onShelfDate);
				oneLv1.setOnShelfTime(onShelfTime);
				oneLv1.setOffShelfAuto(offShelfAuto);
				oneLv1.setOffShelfDate(offShelfDate);
				oneLv1.setOffShelfTime(offShelfTime);

				oneLv1.setStatus_channel(status_channel);
				oneLv1.setStatus_shop(status_shop);
				
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
	protected String getQuerySql(DCP_GoodsShelfShopQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		if (langType == null || langType.isEmpty())
		{
			langType = "zh_CN";
		}
		String shopId = req.getRequest().getShopId();
		String keyTxt = req.getRequest().getKeyTxt();
		String pluNo = req.getRequest().getPluNo();
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();

		int pageNumber = 1;
		int pageSize = 99999;
		
		if(req.getPageNumber() != 0 && req.getPageSize() !=0){
			pageNumber = req.getPageNumber();
			pageSize = req.getPageSize();
		}
		
		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		
		// 以下sql 语句摘录自 SA-武小凤之设计。  地址： http://183.233.190.204:10004/project/144/interface/api/2877
		if(!Check.Null(pluNo)){
			sqlbuf.append(" SELECT * FROM  (  "
					 + " SELECT COUNT(* ) OVER( ORDER BY shopId, channelid ) NUM , dense_Rank() OVER(ORDER BY shopId, channelId  ) rn , temp.* " 
					 + " FROM ("
					 + " SELECT A.*, F.ORG_NAME SHOPNAME, B.CHANNELID, B.CHANNELNAME, "
					 + " NVL(C.STATUS, 0) STATUS_CHANNEL, "
					// + " NVL(E.STATUS, NVL(C.STATUS, 0)) STATUS_SHOP "
					 + "  CASE NVL(C.status,0) WHEN 100 THEN NVL(E.status, NVL(C.status , 0 )) ELSE 0 END status_shop,G.onShelfAuto, G.onShelfDate, G.onShelfTime, G.offShelfAuto, G.offShelfDate, G.offShelfTime "
                     + " FROM (SELECT EID, ORGANIZATIONNO SHOPID "
					 + " FROM DCP_ORG"
					 + " WHERE EID = '"+eId+"' "
					 + " AND ORGANIZATIONNO = '"+shopId+"') A "
					 + " LEFT JOIN (SELECT B1.* "
					 + " FROM CRM_CHANNEL B1 "
					 + " INNER JOIN PLATFORM_APP B2 "
					 + " ON B1.APPNO = B2.APPNO "
					 + " WHERE B2.ISTHIRD = 'N' "
					 + " AND B2.ISONLINE = 'Y' " );
			
			if( !Check.Null(keyTxt) ){
				sqlbuf.append(" and ( B1.channelId = '"+keyTxt+"'  or  B1.channelname like '%%"+keyTxt+"%%' ) ");
			}
			
			sqlbuf.append( " ) B "
					 + " ON A.EID = B.EID "
					 + " LEFT JOIN (SELECT * FROM DCP_GOODS_SHELF_RANGE WHERE PLUNO = '"+pluNo+"' AND SHOPID = 'ALL') C "
					 + " ON B.EID = C.EID "
					 + " AND B.CHANNELID = C.CHANNELID "
					 + " LEFT JOIN CRM_CHANNELSHOP D "
					 + " ON B.EID = D.EID "
					 + " AND B.CHANNELID = D.CHANNELID "
					 + " AND A.SHOPID = D.SHOPID "
					 + " LEFT JOIN (SELECT * "
					 + " FROM DCP_GOODS_SHELF_RANGE "
					 + " WHERE PLUNO = '"+pluNo+"' "
					 + " AND SHOPID <> 'ALL') E "
					 + " ON B.EID = E.EID "
					 + " AND B.CHANNELID = E.CHANNELID "
					 + " AND A.SHOPID = E.SHOPID "
					 + " LEFT JOIN DCP_ORG_LANG F "
					 + " ON A.EID = F.EID "
					 + " AND A.SHOPID = F.ORGANIZATIONNO "
					 + " AND F.LANG_TYPE = '"+langType+"' " +
                     " LEFT JOIN DCP_GOODS_SHELF_SHOPDATE G ON G.EID = E.EID AND E.CHANNELID =G.CHANNELID  AND E.SHOPID = G.SHOPID AND E.PLUNO = G.PLUNO AND G.PLUNO = '"+pluNo+"'"
					 + "  ) temp ) WHERE  rn>"+startRow+" and rn<="+(startRow+pageSize)   );
		}
		else{
			sqlbuf.append(" "
					+ " SELECT * FROM  (  "
					+ " SELECT COUNT(* ) OVER( ORDER BY shopId, channelid ) NUM , dense_Rank() OVER(ORDER BY shopId, channelId  ) rn , temp.* " 
					+ " FROM ("
					+ " SELECT A.*,F.ORG_NAME SHOPNAME,B.CHANNELID,B.CHANNELNAME,0 STATUS_CHANNEL, '0' STATUS_SHOP,E.onShelfAuto, E.onShelfDate, E.onShelfTime, E.offShelfAuto, E.offShelfDate, E.offShelfTime FROM "
					+ " (SELECT EID,ORGANIZATIONNO SHOPID FROM DCP_ORG WHERE EID='"+eId+"' AND ORGANIZATIONNO ='"+shopId+"') A " 
					+ " LEFT JOIN ("
					+ " SELECT B1.* FROM CRM_CHANNEL B1 "
					+ " INNER JOIN PLATFORM_APP B2 ON B1.APPNO=B2.APPNO "
					+ " WHERE B2.ISTHIRD='N' AND B2.ISONLINE='Y' " );
			
			if( !Check.Null(keyTxt) ){
				sqlbuf.append(" and ( B1.channelId = '"+keyTxt+"'  or  B1.channelname like '%%"+keyTxt+"%%' ) ");
			}
			
			sqlbuf.append( ") B " 
					+ " ON A.EID=B.EID "
					+ " LEFT JOIN CRM_CHANNELSHOP D ON B.EID=D.EID AND B.CHANNELID=D.CHANNELID AND A.SHOPID=D.SHOPID " 
					+ " LEFT JOIN DCP_GOODS_SHELF_SHOPDATE E ON D.EID = E.EID AND E.CHANNELID =D.CHANNELID AND E.SHOPID = D.SHOPID  "
					+ " LEFT JOIN DCP_ORG_LANG F ON A.EID=F.EID AND A.SHOPID=F.ORGANIZATIONNO AND F.LANG_TYPE='"+langType+"' "
					+ "  ) temp  ) WHERE  rn>"+startRow+" and rn<="+(startRow+pageSize)   );
			
		}
		
		
//		sqlbuf.append(" select * from ( ");
//		sqlbuf.append(" SELECT CHANNELID,SHOPID,CHANNELNAME,SHOPNAME, NVL(STATUS_SHOP,STATUS_CHANNEL) STATUS  ");
//		sqlbuf.append(" FROM ( ");
//		sqlbuf.append(" select A.CHANNELID,A.SHOPID,B.CHANNELNAME,C.ORG_NAME AS SHOPNAME,NVL(R1.STATUS,'0') AS STATUS_CHANNEL,R2.STATUS AS STATUS_SHOP  ");
//		sqlbuf.append(" from CRM_CHANNELSHOP A ");
//		sqlbuf.append(" left join CRM_CHANNEL B on A.EID=B.EID AND A.CHANNELID=B.CHANNELID");
//		sqlbuf.append(" left join DCP_ORG_LANG C on A.EID=C.EID AND A.SHOPID=C.ORGANIZATIONNO AND C.LANG_TYPE='" + langType + "' ");
//		sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R1 on A.EID=R1.EID AND A.CHANNELID=R1.CHANNELID AND R1.SHOPID='ALL' ");
//		sqlbuf.append(" left join DCP_GOODS_SHELF_RANGE R2 on A.EID=R2.EID AND A.CHANNELID=R2.CHANNELID AND R2.SHOPID=A.SHOPID ");
//		sqlbuf.append(" where A.EID='" + eId + "' and A.SHOPID='" + shopId + "' ");
//		if (keyTxt != null && keyTxt.isEmpty() == false)
//		{
//			sqlbuf.append(" AND (A.CHANNELID like '%%" + keyTxt + "%%' or B.CHANNELNAME like '%%" + keyTxt + "%%') ");
//		}
//		if (pluNo != null && pluNo.isEmpty() == false)
//		{
//			sqlbuf.append(" AND R1.PLUNO='" + pluNo + "' ");
//			sqlbuf.append(" AND R2.PLUNO='" + pluNo + "' ");
//		}
//		sqlbuf.append(" )");
//		sqlbuf.append(" )");

		sql = sqlbuf.toString();
		return sql;

	}

}
