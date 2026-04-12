package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MarketAccessQueryReq;
import com.dsc.spos.json.cust.res.DCP_MarketAccessQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商场接入查询
 * 规格：http://183.233.190.204:10004/project/79/interface/api/2901
 */
public class DCP_MarketAccessQuery extends SPosBasicService<DCP_MarketAccessQueryReq, DCP_MarketAccessQueryRes> {
	
	Logger logger = Logger.getLogger(DCP_MarketAccessQuery.class.getName());

	@Override
	protected boolean isVerifyFail(DCP_MarketAccessQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_MarketAccessQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MarketAccessQueryReq>(){};
	}

	@Override
	protected DCP_MarketAccessQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MarketAccessQueryRes();
	}

	@Override
	protected DCP_MarketAccessQueryRes processJson(DCP_MarketAccessQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_MarketAccessQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0;								//总笔数
		int totalPages = 0;
		int pageNumber=req.getPageNumber();
		if(pageNumber==0){
			pageNumber=1;
		}
		int pageSize=req.getPageSize();
		if(pageSize==0){
			pageSize=100;
		}
		String eId=req.geteId();
		try
		{
			//单头查询
			String sql=getQuerySql(req, eId, pageNumber, pageSize);
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			
			List<DCP_MarketAccessQueryRes.level1Elm> datas=new ArrayList<DCP_MarketAccessQueryRes.level1Elm>();
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				Map<String, Object> oneData_Count = getQDataDetail.get(0);
				
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / pageSize;
				totalPages = (totalRecords % pageSize > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> map1 : getQDataDetail) {
					DCP_MarketAccessQueryRes.level1Elm lev1=new DCP_MarketAccessQueryRes.level1Elm();
					String shopId=map1.get("T1SHOPID")==null?"":map1.get("T1SHOPID").toString();
					lev1.setShopId(shopId);
					String shopName=map1.get("T2SHOPNAME")==null?"":map1.get("T2SHOPNAME").toString();
					lev1.setShopName(shopName);
					String status=map1.get("STATUS")==null?"":map1.get("STATUS").toString();
					if (status==null||status.trim().isEmpty())
					{
						status = "0";//默认0
					}
					lev1.setStatus(status);
					String marketType=map1.get("MARKETTYPE")==null?"":map1.get("MARKETTYPE").toString();
					lev1.setMarketType(marketType);
					String marketTypeName=map1.get("BMARKETNAME")==null?"":map1.get("BMARKETNAME").toString();
					lev1.setMarketTypeName(marketTypeName);
					String marketName=map1.get("MARKETNAME")==null?"":map1.get("MARKETNAME").toString();
					lev1.setMarketName(marketName);
					String uploadScale=map1.get("UPLOADSCALE")==null?"":map1.get("UPLOADSCALE").toString();
					lev1.setUploadScale(uploadScale);
					String serviceUrl=map1.get("SERVICEURL")==null?"":map1.get("SERVICEURL").toString();
					lev1.setServiceUrl(serviceUrl);
					String valiKey=map1.get("VALIKEY")==null?"":map1.get("VALIKEY").toString();
					lev1.setValiKey(valiKey);
					String userCode=map1.get("USERCODE")==null?"":map1.get("USERCODE").toString();
					lev1.setUserCode(userCode);
					String passWord=map1.get("PASSWORD")==null?"":map1.get("PASSWORD").toString();
					lev1.setPassword(passWord);
					String marketNo=map1.get("MARKETNO")==null?"":map1.get("MARKETNO").toString();
					lev1.setMarketNo(marketNo);
					String storeNo=map1.get("STORENO")==null?"":map1.get("STORENO").toString();
					lev1.setStoreNo(storeNo);
					String storeName=map1.get("STORENAME")==null?"":map1.get("STORENAME").toString();
					lev1.setStoreName(storeName);
					String corpNo=map1.get("CORPNO")==null?"":map1.get("CORPNO").toString();
					lev1.setCorpNo(corpNo);
					String corpName=map1.get("CORPNAME")==null?"":map1.get("CORPNAME").toString();
					lev1.setCorpName(corpName);
					String machineNo=map1.get("MACHINENO")==null?"":map1.get("MACHINENO").toString();
					lev1.setMachineNo(machineNo);
					String goodsNo=map1.get("GOODSNO")==null?"":map1.get("GOODSNO").toString();
					lev1.setGoodsNo(goodsNo);
					String payCode=map1.get("PAYCODE")==null?"":map1.get("PAYCODE").toString();
					lev1.setPayCode(payCode);
                    String params=map1.get("PARAMS")==null?"":map1.get("PARAMS").toString();
                    lev1.setParams(params);
					datas.add(lev1);		
					
				}
			}
				
			res.setDatas(datas);
			res.setPageNumber(pageNumber);
			res.setPageSize(pageSize);
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！！");
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_MarketAccessQuery服务执行失败:",e);
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getQuerySql(DCP_MarketAccessQueryReq req) throws Exception {
		return null;
	}
	
	
	protected String getQuerySql(DCP_MarketAccessQueryReq req, String eId, int pageNumber, int pageSize) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		//分页处理
		int startRow=(pageNumber-1) * pageSize;
		String langType = req.getLangType();
		StringBuffer sqlbuf = new StringBuffer();
		
		sqlbuf.append(" SELECT * FROM ( "
			+ " SELECT count(*) OVER() AS NUM,  row_number() over (order BY SHOPID  ) rn, s.* FROM (   "
			+ " SELECT T2.ORG_NAME AS T2SHOPNAME, B.VALUENAME AS BMARKETNAME,"
			+ " T1.ORGANIZATIONNO AS T1SHOPID, "
			+ " A.* " );
			
		sqlbuf.append(" FROM DCP_ORG T1 "
			+ " LEFT JOIN DCP_MARKETACCESS A ON A.EID=T1.EID AND A.SHOPID=T1.ORGANIZATIONNO "
			+ " LEFT JOIN DCP_ORG_LANG T2 ON T1.EID=T2.EID AND T1.ORGANIZATIONNO=T2.ORGANIZATIONNO  AND T2.LANG_TYPE='"+langType+"' "
			+ " LEFT JOIN DCP_FIXEDVALUE B ON B.KEY='MARKETTYPE' AND A.MARKETTYPE=B.VALUEID"
			+ " WHERE T1.ORG_FORM='2' AND T1.STATUS='100' "
		);
		DCP_MarketAccessQueryReq.Level1Elm lev1=req.getRequest();
		if(lev1!=null){
			String keyTxt=lev1.getKeyTxt();
			if(!Check.Null(keyTxt)){
				keyTxt=keyTxt+"%";
				sqlbuf.append(" AND (T1.ORGANIZATIONNO LIKE '"+keyTxt+"' OR T2.ORG_NAME LIKE '"+keyTxt+"')");
			}
		}
		
		if (!Check.Null(eId))
		{
			sqlbuf.append(" AND T1.EID = '"+eId+"' ");
		}
		
		sqlbuf.append( " ORDER BY T1.ORGANIZATIONNO "
			+ " )  s  )   t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize) 
			+ " ORDER BY T1SHOPID "
				);
		
		sql = sqlbuf.toString();
		return sql;
	}

}
