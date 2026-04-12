package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_StockChannelDetailReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelDetailReq.OrganizationList;
import com.dsc.spos.json.cust.res.DCP_StockChannelDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道库存分配单据列表查询
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelDetail extends SPosBasicService<DCP_StockChannelDetailReq, DCP_StockChannelDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_StockChannelDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockChannelDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockChannelDetailReq>(){};
	}

	@Override
	protected DCP_StockChannelDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockChannelDetailRes();
	}

	@Override
	protected DCP_StockChannelDetailRes processJson(DCP_StockChannelDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_StockChannelDetailRes res = null;
		res = this.getResponse();

		try {

			String sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
			DCP_StockChannelDetailRes.levelRes lvRes = res.new levelRes();

			lvRes.setPluList( new ArrayList<DCP_StockChannelDetailRes.PluList>());

			int totalRecords = 0;
			int totalPages = 0;

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
					DCP_StockChannelDetailRes.PluList lvPlu = res.new PluList();

					String channelId = map.get("CHANNELID").toString();
					String channelName = map.getOrDefault("CHANNELNAME","").toString();
					String orgNo = map.getOrDefault("ORGANIZATIONNO","").toString();
					String orgName = map.getOrDefault("ORGNAME","").toString();
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
						listImage = domainName+listImage;
					}

					String baseUnit = map.getOrDefault("BASEUNIT","").toString();
					String sUnit_use = map.get("SUNIT_USE").toString();
					String unitRatioStr = map.get("UNIT_RATIO").toString();
					String avalibleQty = map.get("AVALIBLEQTY").toString();//查出来的是基准单位对应数量， 需要换算为销售单位
					String onlineQty = map.get("ONLINEQTY").toString();//销售单位
					String lockQty = map.get("LOCKQTY").toString();//销售单位
					String sUnitLength =  map.get("SUNITLENGTH").toString();

					// 总剩余可用=实际库存-总预留-总锁定（表DCP_STOCK： QTY-LOCKQTY-ONLINEQTY）

					//如果没有单位长度，默认为2
					if(Check.Null(sUnitLength)){
						sUnitLength = "2";
					}

					if(Check.Null(sUnit_use) ){
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + pluNo + " 找不到对应的 "+sUnit+" 到"+baseUnit+" 的单位换算关系");
					}
					else{
						double unitRatio = Double.parseDouble(unitRatioStr);
						double avalibleQtyDou = Double.parseDouble(avalibleQty) * unitRatio;

						avalibleQty = String.format("%."+sUnitLength+"f", avalibleQtyDou);

					}

					lvPlu.setChannelId(channelId);
					lvPlu.setChannelName(channelName);
					lvPlu.setOrganizationNo(orgNo);
					lvPlu.setOrganizationName(orgName);
					lvPlu.setPluNo(pluNo);
					lvPlu.setPluName(pluName);
					lvPlu.setFeatureNo(featureNo);
					lvPlu.setFeatureName(featureName);
					lvPlu.setsUnit(sUnit);
					lvPlu.setsUnitName(sUnitName);
					lvPlu.setWarehouse(warehouse);
					lvPlu.setWarehouseName(warehouseName);
					lvPlu.setListImage(listImage);
					lvPlu.setAvalibleQty(avalibleQty);
					lvPlu.setOnlineQty(onlineQty);
					lvPlu.setLockQty(lockQty);
					lvPlu.setBaseUnit(baseUnit);

					lvPlu.setUnitRatio(unitRatioStr);
					lvPlu.setsUnitLength(sUnitLength);

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
	protected String getQuerySql(DCP_StockChannelDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		if(pageNumber == 0 || pageSize == 0){
			pageNumber = 1;
			pageSize = 99999;
		}
		// 計算起啟位置
		int startRow = (pageNumber - 1) * pageSize;
		String langType = req.getLangType();

		String channelId = req.getRequest().getChannelId();
		String pluNo = req.getRequest().getPluNo();
		String featureNo = req.getRequest().getFeatureNo();
		String sUnit = req.getRequest().getsUnit();
		String warehouse = req.getRequest().getWarehouse();
		String keyTxt = req.getRequest().getKeyTxt();

		String orgStr = "''";

		if(req.getRequest().getOrganizationList() != null ){
			String[] OrgArr = new String[req.getRequest().getOrganizationList().size()] ;
			int i=0;
			for (OrganizationList lv1 : req.getRequest().getOrganizationList())
			{
				String orgNo = "";
				if(!Check.Null(lv1.getOrganizationNo())){
					orgNo = lv1.getOrganizationNo();
				}
				OrgArr[i] = orgNo;
				i++;
			}
			orgStr = getString(OrgArr);
		}


		sqlbuf.append(""
				+ " SELECT * from ( "
				+ " SELECT "
				+ " count(*) OVER() AS NUM,  dense_rank() over (order BY  a.channelId , a.organizationno, a.pluNo, a.sUnit, a.warehouse,a.featureNo  ) rn,"
				+ " a.eID , a.channelId , a.organizationNo , a.pluNo , a.sunit , a.featureNo , a.warehouse ,a.onlineqty , a.lockQty , a.baseunit , a.bOnlineQty, a.blockqty , "
				+ " nvl( S.QTY - S.LOCKQTY - S.Onlineqty  , 0) AS avalibleQty , "
				+ " b.plu_name AS pluname , c.org_name AS orgName , d.uname AS sunitName , e.warehouse_name AS warehouseName "
				+ " , f.listImage , g.featureName , i.channelName "
				+ " , h.sunit_use ,  nvl(h.QTY,1)/nvl(h.OQTY,1) AS UNIT_RATIO , u1.udLength as sUnitLength  "
				+ " FROM Dcp_Stock_Channel a "
				+ " LEFT JOIN DCP_stock s ON a.eId = s.eId  AND a.organizationno = s.organizationNO  "
				+ " AND A.ORGANIZATIONNO = S.ORGANIZATIONNO AND A.warehouse = S.Warehouse AND a.pluNo = s.pluNo AND A.Featureno = S.Featureno  "
				+ " LEFT JOIN DCP_goods_lang b ON a.eId = b.eid AND a.pluNo = b.pluno AND b.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_org_lang c  ON a.eId = c.eid AND a.organizationno = c.organizationno AND c.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_UNIT_LANG d ON a.eId = d.eid AND a.sunit = d.unit AND d.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_warehouse_LANG e ON a.eId = e.eid AND a.warehouse = e.warehouse AND A.ORGANIZATIONNO = E.ORGANIZATIONNO  AND e.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_GOODS_FEATURE_LANG g ON a.eid = g.eid AND a.pluNo = g.pluNo AND a.featureNo = g.featureno and g.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_GOODS_UNIT h ON a.eid = h.eid AND a.pluno = h.pluno AND a.sunit = h.ounit AND h.sunit_use = 'Y' "
				+ " LEFT JOIN DCP_UNIT u1 on a.eid = u1.eid and a.sunit = u1.unit  "
				+ " LEFT JOIN  DCP_GOODSIMAGE f ON a.eid = f.eid AND a.pluno = f.pluNo AND f.APPTYPE='ALL' "
				+ " LEFT JOIN Crm_Channel i ON a.eid = i.eid AND a.channelid = i.channelid "
//				+ " LEFT JOIN  (  "
//				+ " SELECT a.eId, a.templateId , b.pluNo , b.listImage    FROM Dcp_Imagetemplate  a "
//				+ " INNER JOIN   Dcp_Imagetemplate_Goods b ON a.eid = b.eId AND a.templateId = b.templateId "
//				+ " WHERE a.Eid = '"+eId+"' AND a.templateType = 'COMMON'  AND a.status = '100' "
//				+ " ) f ON a.Eid = f.eId AND a.pluNo = f.pluNo "
				+ "	"
				+ " WHERE a.eId = '"+eId+"' "  );

		if(!Check.Null(channelId)
//				&& !channelId.toUpperCase().equals("ALL")
		){
			sqlbuf.append(" AND a.channelid  = '"+channelId+"' ");
		}

		if(req.getRequest().getOrganizationList() != null && req.getRequest().getOrganizationList().size() > 0 ){
			sqlbuf.append(" AND a.organizationNo in ("+orgStr+") ");
		}



		if(!Check.Null(pluNo)){
			sqlbuf.append(" AND a.pluNo  = '"+pluNo+"' ");
		}

		if(!Check.Null(featureNo)){
			sqlbuf.append(" AND a.featureNo  = '"+featureNo+"' ");
		}
		if(!Check.Null(sUnit)){
			sqlbuf.append(" AND a.sUnit  = '"+sUnit+"' ");
		}
		if(!Check.Null(warehouse)){
			sqlbuf.append(" AND a.warehouse  = '"+warehouse+"' ");
		}

		if(!Check.Null(keyTxt)){
			sqlbuf.append(" AND ( A.pluNo like '%%"+keyTxt+"%%' or A.warehouse like '%%"+keyTxt+"%%' "
					+ " or a.channelId = '%%"+keyTxt+"%%'"
					+ " or b.plu_name like  '%%"+keyTxt+"%%' "
					+ " or e.warehouse_name  like  '%%"+keyTxt+"%%' "
					+ " or c.org_name like  '%%"+keyTxt+"%%'    ) ");
		}

		sqlbuf.append(" order by  a.channelId , a.organizationNO, a.pluNo, a.sunit , a.warehouse  "
				+ " )  t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)
				+ " order by channelId  ,organizationNO , pluNo, sunit ,warehouse ");

		sql = sqlbuf.toString();
		return sql;
	}

	protected String getString(String[] str) {
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
