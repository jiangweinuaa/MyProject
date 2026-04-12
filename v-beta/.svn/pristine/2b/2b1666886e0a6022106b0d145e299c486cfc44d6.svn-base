package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_QuotationQueryReq;
import com.dsc.spos.json.cust.res.DCP_QuotationQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 报价单查询
 * @author yuanyy
 *
 */
public class DCP_QuotationQuery extends SPosBasicService<DCP_QuotationQueryReq, DCP_QuotationQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_QuotationQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_QuotationQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_QuotationQueryReq>(){};
	}

	@Override
	protected DCP_QuotationQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_QuotationQueryRes();
	}

	@Override
	protected DCP_QuotationQueryRes processJson(DCP_QuotationQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_QuotationQueryRes res = null;
		res = this.getResponse();
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_QuotationQueryRes.level1Elm>());

			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("QUOTATIONRECORDNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader = MapDistinct.getMap(queryDatas, condition);

				//				Map<String, Object> map_condition = new HashMap<String, Object>();
				//				map_condition.put("PACKAGETYPE", "1");		
				//				map_condition.put("PACKAGETYPE", "2");	
				//				List<Map<String, Object>> mainDatas = MapDistinct.getWhereMap(queryDatas,map_condition,false);	

				for (Map<String, Object> map : getQHeader) {

					DCP_QuotationQueryRes.level1Elm lv1 = res.new level1Elm();
					String quotationRecordNo = map.get("QUOTATIONRECORDNO").toString();
					String createTime = map.get("CREATETIME").toString();
					String shopId = map.get("SHOPID").toString();
					String shopName = map.getOrDefault("SHOPNAME", "").toString();
					String telephone = map.get("TELEPHONE").toString();
					String shopEmail = map.get("SHOPEMAIL").toString();
					String address = map.get("ADDRESS").toString();
					String customName = map.get("CUSTOMNAME").toString();
					String mobile = map.get("MOBILE").toString();
					String customEmail = map.get("CUSTOMEMAIL").toString();
					String referees = map.get("REFEREES").toString();
					String remark = map.get("REMARK").toString();
					String tot_Amt = map.get("TOT_AMT").toString();

					lv1.setQuotationRecordNo(quotationRecordNo);
					lv1.setCreateTime(createTime);
					lv1.setShopId(shopId);
					lv1.setShopName(shopName);
					lv1.setTelephone(telephone);
					lv1.setShopEmail(shopEmail);
					lv1.setAddress(address);
					lv1.setCustomName(customName);
					lv1.setMobile(mobile);
					lv1.setCustomEmail(customEmail);
					lv1.setReferees(referees);
					lv1.setRemark(remark);
					lv1.setTot_Amt(tot_Amt);

					lv1.setQuotationGoods(new ArrayList<DCP_QuotationQueryRes.level2Elm>());

					for (Map<String, Object> map2 : queryDatas) {

						if(quotationRecordNo.equals(map2.get("QUOTATIONRECORDNO").toString())
								&& ( map2.get("PACKAGETYPE").toString().equals("1") || map2.get("PACKAGETYPE").toString().equals("2"))
								){
							DCP_QuotationQueryRes.level2Elm lv2 = res.new level2Elm();
							String pluNo = map2.get("PLUNO").toString();
							String pluName = map2.get("PLUNAME").toString();
							String item = map2.get("ITEM").toString();
							String wunit = map2.get("WUNIT").toString();
							String wunitName = map2.get("WUNITNAME").toString();
							String price = map2.get("PRICE").toString();
							String qty = map2.get("QTY").toString();
							String detailRemark = map2.get("DETAILREMARK").toString();
							String amt = map2.getOrDefault("AMT", "0").toString();
							String fileName = map2.getOrDefault("FILENAME","").toString();

							String pluBarcode = map2.getOrDefault("PLUBARCODE", "").toString();
							String unit = map2.getOrDefault("UNIT", "").toString();
							String specName = map2.getOrDefault("SPECNAME", "").toString();
							String attrName = map2.getOrDefault("ATTRNAME", "").toString();
							String goodsGroup = map2.getOrDefault("GOODSGROUP", "").toString();
							String isPackage = map2.getOrDefault("ISPACKAGE", "N").toString();
							String pClassNo = map2.getOrDefault("PCLASSNO", "").toString();
							String packageType = map2.getOrDefault("PACKAGETYPE", "").toString();
							String packageMitem = map2.getOrDefault("PACKAGEMITEM", "").toString();
							String disc = map2.getOrDefault("DISC", "0").toString();

							lv2.setPluNo(pluNo);
							lv2.setPluName(pluName);
							lv2.setItem(item);
							lv2.setWunit(wunit);
							lv2.setWunitName(wunitName);
							lv2.setPrice(price);
							lv2.setQty(qty);
							lv2.setRemark(detailRemark);
							lv2.setAmt(amt);
							//							lv2.setFileName(fileName);
							lv2.setImageFileName(fileName);

							lv2.setPluBarcode(pluBarcode);
							lv2.setSpecName(specName);
							lv2.setAttrName(attrName);
							lv2.setUnit(unit);
							lv2.setGoodsGroup(goodsGroup);
							lv2.setpClassNo(pClassNo);
							lv2.setIsPackage(isPackage);
							lv2.setPackageType(packageType);
							lv2.setPackageMitem(packageMitem);
							lv2.setDisc(disc);

							lv2.setChildGoodsList(new ArrayList<DCP_QuotationQueryRes.level2Elm>());
							for (Map<String, Object> cdDatas : queryDatas) {

								//过滤出主商品item 和 子商品item 相同，并且套餐类型为子商品的数据
								if(quotationRecordNo.equals(cdDatas.get("QUOTATIONRECORDNO").toString()) 
										&& item.equals(cdDatas.get("ITEM").toString()) 
										&& cdDatas.getOrDefault("PACKAGETYPE","").toString().equals("3")
										&& cdDatas.getOrDefault("ISPACKAGE","").toString().equals("Y")){
									DCP_QuotationQueryRes.level2Elm lv3 = res.new level2Elm();

									String cdpluNo = cdDatas.get("PLUNO").toString();
									String cdpluName = cdDatas.get("PLUNAME").toString();
									String cditem = cdDatas.get("ITEM").toString();
									String cdwunit = cdDatas.get("WUNIT").toString();
									String cdwunitName = cdDatas.get("WUNITNAME").toString();
									String cdprice = cdDatas.get("PRICE").toString();
									String cdqty = cdDatas.get("QTY").toString();
									String cddetailRemark = cdDatas.get("DETAILREMARK").toString();
									String cdamt = cdDatas.getOrDefault("AMT", "0").toString();
									String cdfileName = cdDatas.getOrDefault("FILENAME","").toString();

									String cdpluBarcode = cdDatas.getOrDefault("PLUBARCODE", "").toString();
									String cdunit = cdDatas.getOrDefault("UNIT", "").toString();
									String cdspecName = cdDatas.getOrDefault("SPECNAME", "").toString();
									String cdattrName = cdDatas.getOrDefault("ATTRNAME", "").toString();
									String cdgoodsGroup = cdDatas.getOrDefault("GOODSGROUP", "").toString();
									String cdisPackage = cdDatas.getOrDefault("ISPACKAGE", "N").toString();
									String cdpClassNo = cdDatas.getOrDefault("PCLASSNO", "").toString();
									String cdpackageType = cdDatas.getOrDefault("PACKAGETYPE", "").toString();
									String cdpackageMitem = cdDatas.getOrDefault("PACKAGEMITEM", "").toString();
									String cddisc = cdDatas.getOrDefault("DISC", "0").toString();

									lv3.setPluNo(cdpluNo);
									lv3.setPluName(cdpluName);
									lv3.setItem(cditem);
									lv3.setWunit(cdwunit);
									lv3.setWunitName(cdwunitName);
									lv3.setPrice(cdprice);
									lv3.setQty(cdqty);
									lv3.setRemark(cddetailRemark);
									lv3.setAmt(cdamt);
									//									lv3.setFileName(fileName);
									lv3.setImageFileName(cdfileName);

									lv3.setPluBarcode(cdpluBarcode);
									lv3.setSpecName(specName);
									lv3.setAttrName(attrName);
									lv3.setUnit(cdunit);
									//									lv3.setGoodsGroup(goodsGroup);
									//									lv3.setpClassNo(pClassNo);
									lv3.setIsPackage(cdisPackage);
									lv3.setPackageType(cdpackageType);
									lv3.setPackageMitem(cdpackageMitem);
									//									lv3.setDisc(disc);

									lv2.getChildGoodsList().add(lv3);
								}

							}

							lv1.getQuotationGoods().add(lv2);

						}

					}
					res.getDatas().add(lv1);

				}
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_QuotationQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		String langType = req.getLangType();
		String eId = req.geteId();

		//规格结构设计的有问题，表里不需要在设计shopId 字段
		String shopId = req.getRequest().getShopId();

		String opNo = req.getOpNO();

		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String keyTxt = req.getRequest().getKeyTxt();

		//查询门店当前登陆人员的面销记录单号
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT count(distinct a.quotationrecordno ) OVER() AS NUM,  dense_rank() over (order BY  a.quotationrecordno ) rn, "
				+ " a.quotationrecordno , a.shopId , a.telephone , a.shopEmail , a.address , a.customName , "
				+ " a.mobile , a.customEmail , a.referees , a.remark , "
				+ " nvl(a.tot_amt , '0') AS tot_Amt ,"
				+ "  to_char( a.createtime , 'yyyy-MM-dd hh24:mi:ss' ) AS createtime , "
				+ "  to_char( a.lastmoditime , 'yyyy-MM-dd hh24:mi:ss' ) AS lastmoditime, "
				+ " a.opNo , "
				+ " b.pluNo , c.plu_name AS pluName , b.item, b.wunit, b.price, b.qty, b.remark AS detailRemark "
				+ " , d.unit_name as wunitName , e.org_name as shopName , b.fileName , b.amt, "
				+ " b.PLUBARCODE,b.UNIT,b.SPECNAME,b.ATTRNAME,b.GOODSGROUP,nvl(b.ISPACKAGE,'N') as ISPACKAGE,"
				+ " b.PCLASSNO,b.PACKAGETYPE,"
				+ " b.PACKAGEMITEM, nvl(b.DISC,'0') as  disc  "
				+ " FROM DCP_quOTATIONRECORD a "
				+ " LEFT JOIN DCP_quOTATIONRECORD_detail b ON a.EID = b.eId AND b.quotationrecordno = a. quotationrecordno "
				+ " AND a.shopID = b.shopId "
				+ " LEFT JOIN DCP_ORG_LANG e on e.EID = a.EID and a.shopID = e.organizationNO "
				+ " and e.status = '100' and e.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_GOODS_lang c ON b.eid = c.EID AND b.pluNo = c.pluNo "
				+ " AND c.status = '100' AND c.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_UNIT_lang d on b.eid = d.EID and b.wunit = d.unit "
				+ " AND d.status = '100' AND d.lang_type = '"+langType+"' "
				+ " WHERE a.eId = '"+eId+"' AND a.opNo = '"+opNo+"' "
				+ "   " );

		if (!Check.Null(shopId))
		{
			sqlbuf.append(" AND a.shopId = '"+shopId+"' ");
		}

		if (!Check.Null(beginDate) && !Check.Null(endDate))
		{
			sqlbuf.append(" and  to_date( to_char(a.createtime,'yyyy-MM-dd' ), 'YYYY-MM-dd' )  between to_Date('"+beginDate+"', 'yyyy-MM-dd') and  to_Date('"+endDate+"','yyyy-MM-dd') ");
		}

		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and ( a.quotationrecordno like '%%"+keyTxt+"%%' or a.customName like '%%"+keyTxt+"%%' "
					+ " or a.mobile like '%%"+keyTxt+"%%'  ) ");
		}


		sqlbuf.append( " ORDER BY a.quotationrecordno DESC "
				+ " ) t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize) 
				+ " ORDER BY  quotationrecordno  DESC , item "
				);

		sql = sqlbuf.toString();
		return sql;
	}

}
