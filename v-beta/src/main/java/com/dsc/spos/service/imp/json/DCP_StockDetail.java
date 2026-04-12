package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_StockDetailReq;
import com.dsc.spos.json.cust.req.DCP_StockDetailReq.OrganizationList;
import com.dsc.spos.json.cust.res.DCP_StockDetailRes;
import com.dsc.spos.json.cust.res.DCP_StockDetailRes.levelRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 库存结存查询
 * @author 2020-06-08
 *
 */
public class DCP_StockDetail extends SPosBasicService<DCP_StockDetailReq, DCP_StockDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_StockDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockDetailReq>(){};
	}

	@Override
	protected DCP_StockDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockDetailRes();
	}

	@Override
	protected DCP_StockDetailRes processJson(DCP_StockDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_StockDetailRes res = null;
		res = this.getResponse();
			
		try {
            String sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
			levelRes lvRes = res.new levelRes();
			lvRes.setPluList(new ArrayList<DCP_StockDetailRes.PluList>());
			
			int totalRecords = 0;
			int totalPages = 0;
			if(queryDatas.size() > 0 && !queryDatas.isEmpty()){
				String num = queryDatas.get(0).get("NUM").toString();
				if(req.getPageSize() != 0 && req.getPageNumber() != 0){
					totalRecords=Integer.parseInt(num);
					totalPages = totalRecords / req.getPageSize();
					totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				}

                // 拼接返回图片路径  by jinzma 20210705
                String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
				
				for (Map<String, Object> map : queryDatas) {
					DCP_StockDetailRes.PluList lvPlu = res.new PluList();
					String organizationNo = map.getOrDefault("ORGANIZATIONNO","").toString();
					String organizationName = map.getOrDefault("ORGANIZATIONNAME","").toString();
					String pluNo = map.getOrDefault("PLUNO","").toString();
					String pluName = map.getOrDefault("PLUNAME","").toString();
					String featureNo = map.getOrDefault("FEATURENO","").toString();
					String featureName = map.getOrDefault("FEATURENAME","").toString();
					String sUnit = map.getOrDefault("SUNIT","").toString();
					String sUnitName = map.getOrDefault("SUNITNAME","").toString();
					String warehouse = map.getOrDefault("WAREHOUSE","").toString();
					String warehouseName = map.getOrDefault("WAREHOUSENAME","").toString();
					String listImage = map.getOrDefault("LISTIMAGE","").toString();
					if (!Check.Null(listImage)){
                        listImage = domainName + listImage;
                    }
					String qty = map.getOrDefault("QTY","0").toString();
					String onlineQty = map.getOrDefault("ONLINEQTY","0").toString();
					String lockQty = map.getOrDefault("LOCKQTY","0").toString();
					String avalibleQty = map.getOrDefault("AVALIBLEQTY","0").toString();
					
					lvPlu.setOrganizationNo(organizationNo);
					lvPlu.setOrganizationName(organizationName);
					lvPlu.setPluNo(pluNo);
					lvPlu.setPluName(pluName);
					lvPlu.setFeatureNo(featureNo);
					lvPlu.setFeatureName(featureName);
					lvPlu.setsUnit(sUnit);
					lvPlu.setsUnitName(sUnitName);
					lvPlu.setWarehouse(warehouse);
					lvPlu.setWarehouseName(warehouseName);
					lvPlu.setListImage(listImage);
					lvPlu.setQty(qty);
					lvPlu.setOnlineQty(onlineQty);
					lvPlu.setLockQty(lockQty);
					lvPlu.setAvalibleQty(avalibleQty);

					lvRes.getPluList().add(lvPlu);
					
				}
			}
			
			res.setDatas(lvRes);
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_StockDetailReq req) throws Exception {
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
		List<OrganizationList> orgDatas = req.getRequest().getOrganizationList();
		
		sqlbuf.append(""
				+ " SELECT * from ( "
				+ " SELECT "
				+ " count( * ) OVER( ) AS NUM,  dense_rank() over (order BY a.organizationno, a.pluNo, a.warehouse,a.featureNo  ) rn,"
				+ " a.eId , a.organizationNO , a.pluNo , a.baseUnit , a.featureNo , a.warehouse , a.qty , a.lockQty , a.onlineQty , "
				+ " b.org_name AS organizationName , c.plu_name AS pluName , d.featurename , e.uname AS baseUnitName , "
				+ " f.warehouse_name AS warehouseName , h.Listimage "
				+ " FROM Dcp_Stock a "
				+ " LEFT JOIN DCP_org_lang b ON a.eId = b.eId AND a.organizationNo = b.organizationNo AND b.lang_type = '"+langType+"' "
				+ " LEFT JOIN Dcp_Goods_Lang c ON a.eId = c.eId AND a.pluNo = c.pluNo AND c.lang_type = '"+langType+"'"
				+ " LEFT JOIN Dcp_Goods_Feature_lang d ON a.eid = d.eid AND a.pluNo = d.pluNo AND a.featureNo = d.featureno and d.lang_type = '"+langType+"'  "
				+ " LEFT JOIN dcp_unit_lang e ON a.eId = e.eid AND a.baseUnit = e.unit AND e.lang_type = '"+langType+"' "
				+ " LEFT JOIN dcp_warehouse_lang f ON a.eId = f.eId AND a.organizationNO = f.organizationNO  AND a.warehouse = f.warehouse AND  f.lang_type = '"+langType+"'"
				+ "	LEFT JOIN DCP_GOODSIMAGE h ON a.eid = h.eid AND a.pluno = h.pluNo AND h.APPTYPE='ALL'     "
//				+ " LEFT JOIN ("
//				+ " SELECT a.eId, a.templateId , b.pluNo , b.listImage    FROM Dcp_Imagetemplate  a "
//				+ " INNER JOIN   Dcp_Imagetemplate_Goods b ON a.eid = b.eId AND a.templateId = b.templateId "
//				+ " WHERE a.Eid = '99' AND a.templateType = 'COMMON'  AND a.status = '100'"
//				+ " ) h ON a.eId = h.eId AND a.pluNo = h.pluNo "
				+ "	"
				+ " WHERE a.eId = '"+eId+"' "  );
		
		if(!Check.Null(req.getRequest().getPluNo())){
			String pluNo = req.getRequest().getPluNo();
			sqlbuf.append(" AND a.pluNo = '"+ pluNo +"' ");
		}
		
		if(!Check.Null(req.getRequest().getKeyTxt())){ // 
			String keyTxt = req.getRequest().getKeyTxt();
			sqlbuf.append(" AND ( b.org_name like '%%"+ keyTxt +"%%' or c.plu_Name like '%%"+ keyTxt +"%%' or a.featureNo like '%%"+ keyTxt +"%%' "
					+ " or d.featureName like '%%"+keyTxt+"%%'  "
					+ " or f.warehouse_name like '%%"+ keyTxt +"%%' or a.warehouse like '%%"+ keyTxt +"%%'     )");
		}
		
		if(orgDatas != null && !orgDatas.isEmpty() && orgDatas.size() > 0 ){
		
			String[] orgArr = new String[orgDatas.size()] ;
			int i=0;
			for (OrganizationList lvOrg : orgDatas) 
			{
				String org = "";
				if(!Check.Null(lvOrg.getOrganizationNo())){
					org = lvOrg.getOrganizationNo();
				}
				orgArr[i] = org;
				i++;
			}
			String orgStr = getString(orgArr);
			
			sqlbuf.append(" AND a.organizationno in( "+ orgStr +" ) ");
		}
		
		sqlbuf.append(" order by a.pluNO , a.organizationno , a.warehouse   "
				+ " )  where  rn > "+startRow+" AND rn < = "+(startRow+pageSize)+"  "
				+ " order by  pluNO , organizationno ,  warehouse  ");
		
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
