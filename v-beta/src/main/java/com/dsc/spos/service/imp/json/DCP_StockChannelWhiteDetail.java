package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StockChannelWhiteDetailReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelWhiteDetailReq.OrganizationList;
import com.dsc.spos.json.cust.res.DCP_StockChannelWhiteDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道商品分货白名单查询
 *
 * @author 2020-06-02
 *
 */
public class DCP_StockChannelWhiteDetail
		extends SPosBasicService<DCP_StockChannelWhiteDetailReq, DCP_StockChannelWhiteDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_StockChannelWhiteDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockChannelWhiteDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockChannelWhiteDetailReq>() {
		};
	}

	@Override
	protected DCP_StockChannelWhiteDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockChannelWhiteDetailRes();
	}

	@Override
	protected DCP_StockChannelWhiteDetailRes processJson(DCP_StockChannelWhiteDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_StockChannelWhiteDetailRes res = null;
		res = this.getResponse();

		try {

			String sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			int totalRecords = 0;
			int totalPages = 0;

			DCP_StockChannelWhiteDetailRes.levelRes levelRes = res.new levelRes();

			levelRes.setPluList(new ArrayList<DCP_StockChannelWhiteDetailRes.PluList>());
			if(queryDatas.size() > 0 && !queryDatas.isEmpty()){
				// 拼接返回图片路径  by jinzma 20210705
				String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
				String httpStr=isHttps.equals("1")?"https://":"http://";
				String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
				if (domainName.endsWith("/")) {
					domainName = httpStr + domainName + "resource/image/";
				}else{
					domainName = httpStr + domainName + "/resource/image/";
				}

				String num = queryDatas.get(0).get("NUM").toString();
				if(req.getPageSize() != 0 && req.getPageNumber() != 0){
					totalRecords=Integer.parseInt(num);
					totalPages = totalRecords / req.getPageSize();
					totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				}

				for (Map<String, Object> map : queryDatas) {
					DCP_StockChannelWhiteDetailRes.PluList lvPlu = res.new PluList();

					String channelId = map.get("CHANNELID").toString();
					String channelName = map.getOrDefault("CHANNELNAME","").toString();
//					String organizationNo = map.get("ORGANIZATIONNO").toString(); 
//					String organizationName = map.get("ORGANIZATIONNAME").toString(); 
					String pluNo = map.get("PLUNO").toString();
					String pluName = map.get("PLUNAME").toString();
//					String featureNo = map.get("FEATURENO").toString(); 
//					String featureName = map.get("FEATURENAME").toString(); 
//					String sUnit = map.get("SUNIT").toString(); 
//					String sUnitName = map.get("SUNITNAME").toString(); 
//					String warehouse = map.get("WAREHOUSE").toString(); 
//					String warehouseName = map.get("WAREHOUSENAME").toString(); 
//					String fileName = map.getOrDefault("FILENAME","").toString(); 
					String listImage = map.getOrDefault("LISTIMAGE","").toString();
                    if (!Check.Null(listImage)){
						listImage = domainName+listImage;
					}
					String lastModiOpId = map.get("LASTMODIOPID").toString();
					String lastModiOpName = map.get("LASTMODIOPNAME").toString();
					String lastModiTime = map.get("LASTMODITIME").toString();

					lvPlu.setChannelId(channelId);
					lvPlu.setChannelName(channelName);
//					lvPlu.setOrganizationNo(organizationNo);
//					lvPlu.setOrganizationName(organizationName);
					lvPlu.setPluNo(pluNo);
					lvPlu.setPluName(pluName);
//					lvPlu.setFeatureNo(featureNo);
//					lvPlu.setFeatureName(featureName);
//					lvPlu.setsUnit(sUnit);
//					lvPlu.setsUnitName(sUnitName);
//					lvPlu.setWarehouse(warehouse);
//					lvPlu.setWarehouseName(warehouseName);
					lvPlu.setListImage(listImage);
					lvPlu.setLastModiOpId(lastModiOpId);
					lvPlu.setLastModiOpName(lastModiOpName);
					lvPlu.setLastModiTime(lastModiTime);

					levelRes.getPluList().add(lvPlu);
				}

			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

			res.setDatas( levelRes);
			res.setServiceStatus("000");
			res.setSuccess(true);
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_StockChannelWhiteDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;

		String eId = req.geteId();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		if(pageNumber ==0 || pageSize == 0){
			pageNumber = 1;
			pageSize = 99999;
		}

		// 計算起啟位置
		int startRow = (pageNumber - 1) * pageSize;
		String langType = req.getLangType();
		StringBuffer sqlbuf = new StringBuffer("");
//		List<OrganizationList> orgDatas = req.getRequest().getOrganizationList();

		sqlbuf.append(""
				+ " SELECT * from ( "
				+ " select "
				+ " count( * ) OVER( ) AS NUM,  dense_rank() over (order BY a.channelId  , a.pluNo  ) rn,"
				+ " a.eID, a.channelid  , a.pluNo, a.lastmodiopid , a.lastmodiopname , a.lastmoditime , "
				+ " b.plu_name AS pluName , f.listimage , c.channelName "
				+ " FROM  DCP_STOCK_CHANNEL_WHITE  a "
				+ " LEFT JOIN DCP_goods_lang b ON a.Eid = b.Eid AND a.Pluno = b.pluNO AND b.lang_type = '"+langType+"' "
				+ " LEFT JOIN CRM_CHANNEL c on a.eId = c.eId and a.channelId = c.channelId  "
				+ " LEFT JOIN  DCP_GOODSIMAGE f ON a.eid = f.eid AND a.pluno = f.pluNo AND f.APPTYPE='ALL' "
//				+ " LEFT JOIN  (  "
//				+ " SELECT a.eId, a.templateId , b.pluNo , b.listImage    FROM Dcp_Imagetemplate  a "
//				+ " INNER JOIN   Dcp_Imagetemplate_Goods b ON a.eid = b.eId AND a.templateId = b.templateId "
//				+ " WHERE a.Eid = '"+eId+"' AND a.templateType = 'COMMON'  AND a.status = '100' "
//				+ " ) f ON a.Eid = f.eId AND a.pluNo = f.pluNo "
				+ "	"
				+ " WHERE a.eId = '"+eId+"' "  );

		if(!Check.Null(req.getRequest().getChannelId()) && !req.getRequest().getChannelId().toUpperCase().equals("ALL")){
			String channelId = req.getRequest().getChannelId();
			sqlbuf.append(" AND a.channelid = '"+ channelId +"' ");
		}

		if(!Check.Null(req.getRequest().getPluNo())){
			String pluNo = req.getRequest().getPluNo();
			sqlbuf.append(" AND a.pluNo = '"+ pluNo +"' ");
		}

//		if(!Check.Null(req.getRequest().getKeyTxt())){ // 
//			String keyTxt = req.getRequest().getKeyTxt();
//			sqlbuf.append(" AND (  b.plu_Name like '%%"+ keyTxt +"%%'  )");
//		}

//		if(orgDatas != null && !orgDatas.isEmpty() && orgDatas.size() > 0 ){
//		
//			String[] orgArr = new String[orgDatas.size()] ;
//			int i=0;
//			for (OrganizationList lvOrg : orgDatas) 
//			{
//				String org = "";
//				if(!Check.Null(lvOrg.getOrganizationNo())){
//					org = lvOrg.getOrganizationNo();
//				}
//				orgArr[i] = org;
//				i++;
//			}
//			String orgStr = getString(orgArr);
//			
//			sqlbuf.append(" AND a.organizationno in( "+ orgStr +" ) ");
//		}

		sqlbuf.append(" order by a.channelId , a.pluNO  "
				+ " )  where  rn > "+startRow+" AND rn < = "+(startRow+pageSize)+"  "
				+ " order by  channelId , pluNO  ");

		sql = sqlbuf.toString();
		return sql;
	}

	protected String getString(String[] str)
	{
		String str2 = "";
		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		return str2;
	}




}
