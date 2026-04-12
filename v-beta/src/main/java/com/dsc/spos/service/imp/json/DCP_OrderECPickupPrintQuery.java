package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECPickupPrintQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECPickupPrintQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单拣货查询
 * @author yuanyy 2019-04-23
 *
 */
public class DCP_OrderECPickupPrintQuery extends SPosBasicService<DCP_OrderECPickupPrintQueryReq, DCP_OrderECPickupPrintQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECPickupPrintQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String[] ecPlatformNo = req.getEcPlatformNo();		

		if (ecPlatformNo==null || ecPlatformNo.length == 0) 
		{
			errCt++;
			errMsg.append("电商平台不可为空值  ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECPickupPrintQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECPickupPrintQueryReq>(){};
	}

	@Override
	protected DCP_OrderECPickupPrintQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECPickupPrintQueryRes();
	}

	@Override
	protected DCP_OrderECPickupPrintQueryRes processJson(DCP_OrderECPickupPrintQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		DCP_OrderECPickupPrintQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try 
		{
			//sql = this.getQuerySql(req);
			sql = this.getQueryPlunoPackageSql(req);
			String[] conditionValues = {};
			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);

			if(getDatas.size() > 0){
				res.setDatas(new ArrayList<DCP_OrderECPickupPrintQueryRes.level1Elm>());
				String num = getDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("ECORDERNO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getDatas, condition);

				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_OrderECPickupPrintQueryRes.level1Elm lev1 = res.new level1Elm();
					lev1.setDatas(new ArrayList<DCP_OrderECPickupPrintQueryRes.level2Elm>());

					String ecOrderNo = oneData.get("ECORDERNO").toString();
					String shipDate = oneData.get("SHIPDATE").toString();
					String shipHourType = oneData.get("SHIPHOURTYPE").toString();
					String orderer = oneData.get("ORDERER").toString();
					String ordererPhone = oneData.get("ORDERERPHONE").toString();
					String receiver = oneData.get("RECEIVER").toString();
					String receiverMobile = oneData.get("RECEIVERMOBILE").toString();
					//					String receiverPhone = oneData.get("RECEIVERPHONE").toString();
					String receiverAddress = oneData.get("RECEIVERADDRESS").toString();
					String receiverEmail = oneData.get("RECEIVEREMAIL").toString();
					String memo = oneData.get("MEMO").toString();
					String totQty = oneData.get("TOTQTY").toString();
					String getShop = oneData.get("GETSHOP").toString();
					String getShopName = oneData.get("GETSHOPNAME").toString();
					String payStatus = oneData.get("PAYSTATUS").toString();
					String payAmt = oneData.get("PAYAMT").toString();
					String totAmt = oneData.get("TOTAMT").toString();
					String ecPlatformNo = oneData.get("ECPLATFORMNO").toString();
					String ecPlatformName = oneData.get("ECPLATFORMNAME").toString();

					lev1.setEcOrderNo(ecOrderNo);
					lev1.setShipDate(shipDate);
					lev1.setShipHourType(shipHourType);
					lev1.setOrderer(orderer);
					lev1.setOrdererPhone(ordererPhone);
					lev1.setReceiver(receiver);
					lev1.setReceiverMobile(receiverMobile);
					//					lev1.setReceiverPhone(receiverPhone);
					lev1.setReceiverAddress(receiverAddress);
					lev1.setReceiverEmail(receiverEmail);
					lev1.setMemo(memo);
					lev1.setTotQty(totQty);
					lev1.setGetShop(getShop);
					lev1.setGetShopName(getShopName);
					lev1.setPayStatus(payStatus);
					lev1.setPayAmt(payAmt);
					lev1.setTotAmt(totAmt);
					lev1.setEcPlatformNo(ecPlatformNo);
					lev1.setEcPlatformName(ecPlatformName);

					//接下来是第二层 datas 
					for (Map<String, Object> oneData2 : getDatas) 
					{
						//过滤属于此单头的明细
						if(ecOrderNo.equals(oneData2.get("ECORDERNO")))
						{	
							DCP_OrderECPickupPrintQueryRes.level2Elm lev2 = res.new level2Elm();
							String item = oneData2.get("ITEM").toString();
							String pluNo = oneData2.get("PLUNO").toString();
							String pluName = oneData2.get("PLUNAME").toString();
							String barcode = oneData2.get("BARCODE").toString();
							String qty = oneData2.get("QTY").toString();
							String specName = oneData2.get("SPECNAME").toString();
							String amt = oneData2.get("AMT").toString();

							lev2.setItem(item);
							lev2.setPluNo(pluNo);
							lev2.setPluName(pluName);
							lev2.setBarcode(barcode);
							lev2.setQty(qty);
							lev2.setSpecName(specName);
							lev2.setAmt(amt);
							lev1.getDatas().add(lev2);
							lev2 = null;

						}
					}
					res.getDatas().add(lev1);
					lev1 = null;

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
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
	protected String getQuerySql(DCP_OrderECPickupPrintQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String shopId=req.getShopId();
		String startDate = req.getStartDate();
		String endDate = req.getEndDate();
		String[] ecPlatformNo = req.getEcPlatformNo();

		String sPlatformNoInSQL=PosPub.getArrayStrSQLIn(ecPlatformNo);


		String printStatus = req.getPrintStatus();
		String pickupWay = req.getPickupWay();

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append("  SELECT * FROM ( " 
				+ " SELECT  COUNT(DISTINCT a.orderno  ) OVER() NUM ,dense_rank() over(ORDER BY a.orderNo) rn ,"
				+ " a.orderNo AS ecOrderNo , a.shipDate , a.Shiphourtype , a.contman AS orderer , a.contTel AS ordererPhone , "
				+ " a.getMan AS receiver , a.getmantel AS receiverMobile , '' AS receiverPhone , a.address AS receiverAddress , "
				+ " a.getmanemail AS receiverEmail , a.memo , a.tot_qty AS totQty, a.shippingshop AS getShop , a.shippingshopname AS getShopName ,"
				+ " a.paystatus , a.payamt , a.tot_amt AS totAmt ,a.load_doctype ecPlatformNo ,"
				+ " (case when a.load_doctype= 'shopee' then '虾皮' " 
				+ " when a.load_doctype= '91app' then '91APP' " 
				+ " when a.load_doctype='yahoosuper' then 'Yahoo' " 
				+ " when a.load_doctype='letian' then '乐天' " 
				+ " when a.load_doctype='pchome' then 'PChome' " 
				+ " when a.load_doctype='momo' then 'MOMO' " 
				+ " when a.load_doctype='1' then '饿了么'  " 
				+ " when a.load_doctype='2' then '美团外卖'  " 
				+ " when a.load_doctype='3' then '微商城'  " 
				+ " when a.load_doctype='4' then '云POS' " 
				+ " when a.load_doctype='5' then '总部' " 
				+ " when a.load_doctype='6' then '官网' " 
				+ " when a.load_doctype='7' then '舞象' " 
				+ " when a.load_doctype='8' then '京东到家' " 
				+ " when a.load_doctype='9' then '小程序' " 
				+ " when a.load_doctype='37' then '户户送' " 
				+ " when a.load_doctype='90' then '商户平台' " 
				+ " else '其他' end ) ecPlatformName , "
				+ " b.item , b.pluno , b.pluname , b.plubarcode AS barCode , b.qty , b.specname , b.amt "
				+ " FROM OC_ORDER a "
				+ " LEFT JOIN  OC_ORDER_DETAIL b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.orderNo = b.orderno"
				+ " WHERE a.EID = '"+eId+"'"
				+ " and (a.SHOPID='"+shopId+"' or a.shippingshop='"+shopId+"') " );

		if (startDate != null && startDate.length()!=0 && endDate != null && endDate.length()!=0) { 	
			sqlbuf.append( " AND a.sdate  BETWEEN '"+startDate+"'  AND '"+endDate+"'  ");
		}		


		if (sPlatformNoInSQL != null && sPlatformNoInSQL.length()!=0 ) 
		{ 	
			sqlbuf.append( " AND a.load_doctype  in ("+sPlatformNoInSQL+")");
		}


		if (pickupWay != null && pickupWay.length()!=0 ) { 	
			sqlbuf.append( " AND a.pickupway  = '"+pickupWay+"'");
		}

		if (printStatus != null && printStatus.length()!=0 ) 
		{ 			
			if (printStatus.equals("2")) 
			{				
				sqlbuf.append( " AND a.Pickupdocprint  = 'Y'");
			}
			else 
			{
				sqlbuf.append( " AND (a.Pickupdocprint  IS NULL  OR a.Pickupdocprint<>'Y') ");
			}			
		}

		sqlbuf.append( "  ORDER BY a.orderNo, b.item  )  "
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by ecOrderNo ,item ");
		sql = sqlbuf.toString();
		return sql;
	}


	/**
	 * 支持套餐商品(主商品+子商品都查出来union all)
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getQueryPlunoPackageSql(DCP_OrderECPickupPrintQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String shopId=req.getShopId();
		String startDate = req.getStartDate();
		String endDate = req.getEndDate();
		String[] ecPlatformNo = req.getEcPlatformNo();

		String sPlatformNoInSQL=PosPub.getArrayStrSQLIn(ecPlatformNo);


		String printStatus = req.getPrintStatus();
		String pickupWay = req.getPickupWay();

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append("  SELECT * FROM ( " 
				+ " SELECT  COUNT(a.orderno  ) OVER() NUM ,dense_rank() over(ORDER BY a.orderNo) rn ,"
				+ " a.orderNo AS ecOrderNo , a.shipDate , a.Shiphourtype , a.contman AS orderer , a.contTel AS ordererPhone , "
				+ " a.getMan AS receiver , a.getmantel AS receiverMobile , '' AS receiverPhone , a.address AS receiverAddress , "
				+ " a.getmanemail AS receiverEmail , a.memo , a.tot_qty AS totQty, a.shippingshop AS getShop , a.shippingshopname AS getShopName ,"
				+ " a.paystatus , a.payamt , a.tot_amt AS totAmt ,a.load_doctype ecPlatformNo ,"
				+ " (case when a.load_doctype= 'shopee' then '虾皮' " 
				+ " when a.load_doctype= '91app' then '91APP' " 
				+ " when a.load_doctype='yahoosuper' then 'Yahoo' " 
				+ " when a.load_doctype='letian' then '乐天' " 
				+ " when a.load_doctype='pchome' then 'PChome' " 
				+ " when a.load_doctype='momo' then 'MOMO' " 
				+ " when a.load_doctype='1' then '饿了么'  " 
				+ " when a.load_doctype='2' then '美团外卖'  " 
				+ " when a.load_doctype='3' then '微商城'  " 
				+ " when a.load_doctype='4' then '云POS' " 
				+ " when a.load_doctype='5' then '总部' " 
				+ " when a.load_doctype='6' then '官网' " 
				+ " when a.load_doctype='7' then '舞象' " 
				+ " when a.load_doctype='8' then '京东到家' " 
				+ " when a.load_doctype='9' then '小程序' " 
				+ " when a.load_doctype='37' then '户户送' " 
				+ " when a.load_doctype='90' then '商户平台' " 
				+ " else '其他' end ) ecPlatformName , "
				+ " h.item , h.pluno , h.pluname , h.barCode , h.qty , h.specname , h.amt,h.EID,h.SHOPID,h.orderno "
				+ " FROM OC_ORDER a "
				+ " LEFT JOIN  "
				+ "(" + 
				"  select ROW_NUMBER() OVER (ORDER BY item desc) AS item, pluno," + 
				"  pluname,barCode,qty,specname,amt ,EID,SHOPID,orderno" + 
				"   from (" + 
				"   select  b.item,b.pluno,b.pluname,b.plubarcode AS barCode,b.qty,to_char(b.specname) specname,b.amt ,b.EID,b.SHOPID,b.orderno " + 
				"   from OC_ORDER_DETAIL b "
				+ " inner join OC_ORDER i on b.EID=i.EID and b.SHOPID=i.SHOPID and b.orderno=i.orderno "
				+ " and i.EID = '"+eId+"' "
				+ " and (i.SHOPID='"+shopId+"' or i.shippingshop='"+shopId+"') ");

		if (startDate != null && startDate.length()!=0 && endDate != null && endDate.length()!=0) 
		{			
			sqlbuf.append( " AND i.sdate  BETWEEN '"+startDate+"'  AND '"+endDate+"'  ");
		}	

		if (sPlatformNoInSQL != null && sPlatformNoInSQL.length()!=0 ) 
		{ 	
			sqlbuf.append( " AND i.load_doctype  in ("+sPlatformNoInSQL+")");
		}


		sqlbuf.append("   union all" + 				
				"   select  0 as item,e.dpluno,f.plu_name, g.plubarcode as barCode,e.qty*c.qty as qty,'' as specname," + 
				"   0 as amt ,c.EID,c.SHOPID,c.orderno " + 
				"   from OC_ORDER_DETAIL c "
				+ " inner join OC_ORDER i on c.EID=i.EID and c.SHOPID=i.SHOPID and c.orderno=i.orderno "
				+ " and i.EID = '"+eId+"' "
				+ " and (i.SHOPID='"+shopId+"' or i.shippingshop='"+shopId+"') "); 
		if (startDate != null && startDate.length()!=0 && endDate != null && endDate.length()!=0) 
		{			
			sqlbuf.append( " AND i.sdate  BETWEEN '"+startDate+"'  AND '"+endDate+"'  ");
		}	
		if (sPlatformNoInSQL != null && sPlatformNoInSQL.length()!=0 ) 
		{ 	
			sqlbuf.append( " AND i.load_doctype  in ("+sPlatformNoInSQL+")");
		}

		sqlbuf.append("  left join DCP_GOODS d on c.EID=d.EID and c.pluno=d.pluno and d.status='100' " + 
				"   left join DCP_PGOODSCLASS_detail e on d.EID=e.EID and d.pluno=e.pluno and e.status='100' " + 
				"   left join DCP_GOODS_lang f on e.EID=f.EID and e.dpluno=f.pluno and f.status='100' and f.lang_type='"+req.getLangType()+"'" + 
				"   left join DCP_BARCODE g on f.EID=g.EID and f.pluno=g.pluno " + 
				"   where  e.dpluno is not null    ) " + 
				"   ) h" + 
				" ON a.EID = h.EID AND a.SHOPID = h.SHOPID AND a.orderNo = h.orderno"
				+ " WHERE a.EID = '"+eId+"'  "
				+ " and (a.SHOPID='"+shopId+"' or a.shippingshop='"+shopId+"' )" );

		if (startDate != null && startDate.length()!=0 && endDate != null && endDate.length()!=0) { 	
			sqlbuf.append( " AND a.sdate  BETWEEN '"+startDate+"'  AND '"+endDate+"'  ");
		}		


		if (sPlatformNoInSQL != null && sPlatformNoInSQL.length()!=0 ) 
		{ 	
			sqlbuf.append( " AND a.load_doctype  in ("+sPlatformNoInSQL+")");
		}


		if (pickupWay != null && pickupWay.length()!=0 ) { 	
			sqlbuf.append( " AND a.pickupway  = '"+pickupWay+"'");
		}

		if (printStatus != null && printStatus.length()!=0 ) 
		{ 			
			if (printStatus.equals("2")) 
			{				
				sqlbuf.append( " AND a.Pickupdocprint  = 'Y'");
			}
			else 
			{
				sqlbuf.append( " AND (a.Pickupdocprint  IS NULL  OR a.Pickupdocprint<>'Y') ");
			}			
		}

		sqlbuf.append( "  ORDER BY a.orderNo, h.item  )  "
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by ecOrderNo ,item ");
		sql = sqlbuf.toString();
		return sql;
	}



}
