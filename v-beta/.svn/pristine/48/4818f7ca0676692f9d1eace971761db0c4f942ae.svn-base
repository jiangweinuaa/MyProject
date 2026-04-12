package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECShippingQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECShippingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 出货单查询
 * @author yuanyy 2019-03-19
 *
 */
public class DCP_OrderECShippingQuery extends SPosBasicService<DCP_OrderECShippingQueryReq, DCP_OrderECShippingQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		//		boolean isFail = false;
		//		StringBuffer errMsg = new StringBuffer("");
		//		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		//		String lgPlatformNo = req.getLgPlatformNo();
		//		
		//		if (Check.Null(lgPlatformNo)) 
		//		{
		//			errCt++;
		//			errMsg.append("货运厂商编码不可为空值  ");
		//			isFail = true;
		//		}
		//		if (isFail)
		//		{
		//			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		//		}
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECShippingQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECShippingQueryReq>(){};
	}

	@Override
	protected DCP_OrderECShippingQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECShippingQueryRes();
	}

	@Override
	protected DCP_OrderECShippingQueryRes processJson(DCP_OrderECShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECShippingQueryRes res = this.getResponse();
		String sql = null;
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			sql = this.getQuerySql(req);
			String[] conditionValues = {}; //查詢條件
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues);

			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				String num = getQDataDetail.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				int pageNumber = req.getPageNumber();
				int pageSize = req.getPageSize();

				if(pageNumber == 0){
					pageNumber = 1;
				}
				if(pageSize == 0){
					pageSize = 50;
					totalPages = 0;
				}else
				{
					//算總頁數
					totalPages = totalRecords / pageSize;
					totalPages = (totalRecords % pageSize > 0) ? totalPages + 1 : totalPages;	
				}

				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("EID", true);	
				condition.put("SHOPID", true);	
				condition.put("SHIPMENTNO", true);	
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);

				res.setDatas(new ArrayList<DCP_OrderECShippingQueryRes.level1Elm>());

				//避免多次執行SQL
				List<Map<String, Object>> listmapShopPara=new ArrayList<Map<String, Object>>();

				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_OrderECShippingQueryRes.level1Elm lev1 = res.new level1Elm();

					//
					String shopId = oneData.get("SHOPID").toString();


					//
					String ecName="";
					String ecHotline="";
					String ecWebsite="";
					String ecEGSExpress="";
					String SenderName="";
					String SenderSuDa5="";
					String SenderPhone="";
					String SenderAddress="";			
					String productName="";			

					Map<String, Object> map_condition = new HashMap<String, Object>();
					map_condition.put("shopId", shopId);		
					List<Map<String, Object>> getQTemp=MapDistinct.getWhereMap(listmapShopPara,map_condition,false);
					map_condition.clear();
					
					//
					if (getQTemp!=null && getQTemp.isEmpty()==false) 
					{
						ecName=getQTemp.get(0).get("ecName").toString();
						ecHotline=getQTemp.get(0).get("ecHotline").toString();
						ecWebsite=getQTemp.get(0).get("ecWebsite").toString();
						ecEGSExpress=getQTemp.get(0).get("ecEGSExpress").toString();
						SenderName=getQTemp.get(0).get("SenderName").toString();
						SenderSuDa5=getQTemp.get(0).get("SenderSuDa5").toString();
						SenderPhone=getQTemp.get(0).get("SenderPhone").toString();
						SenderAddress=getQTemp.get(0).get("SenderAddress").toString();
						productName=getQTemp.get(0).get("productName").toString();
					}
					else 
					{						
						//先取门店参数platform_baseset，取不到就取模板参数platform_basesettemp,union会自动根据第一列排序
						String sqlDef="select 1 as ID,t.item,t.itemvalue as def from platform_baseset t where item IN ('ECName' , 'ECHotline' , 'ECWebsite', 'ecEGSExpress','SenderName','SenderSuDa5','SenderPhone','SenderAddress','ecEGSProductName') AND status='100' " 
								+" and EID = '"+req.geteId()+"' and shopId='"+shopId+"' "
								+" union "
								+ " select 2 as ID,t.item,t.def from platform_basesettemp t where item IN ('ECName' , 'ECHotline' , 'ECWebsite', 'ecEGSExpress','SenderName','SenderSuDa5','SenderPhone','SenderAddress','ecEGSProductName') AND status='100' "
								+ " and EID = '"+req.geteId()+"'";


						List<Map<String, Object>> getQData = this.doQueryData(sqlDef, null);

						if (getQData != null && getQData.isEmpty() == false) 
						{
							for (Map<String, Object> par : getQData) 
							{
								if(par.get("ITEM").toString().equals("ECName") && ecName.equals(""))
								{
									ecName=par.get("DEF").toString();
									continue;
								}
								if(par.get("ITEM").toString().equals("ECHotline")&& ecHotline.equals(""))
								{
									ecHotline=par.get("DEF").toString();
									continue;
								}
								if(par.get("ITEM").toString().equals("ECWebsite")&& ecWebsite.equals(""))
								{
									ecWebsite=par.get("DEF").toString();
									continue;
								}

								if(par.get("ITEM").toString().equals("ecEGSExpress")&& ecEGSExpress.equals(""))
								{
									ecEGSExpress=par.get("DEF").toString();
									continue;
								}

								if(par.get("ITEM").toString().equals("SenderName")&& SenderName.equals(""))
								{
									SenderName=par.get("DEF").toString();
									continue;
								}

								if(par.get("ITEM").toString().equals("SenderSuDa5")&& SenderSuDa5.equals(""))
								{
									SenderSuDa5=par.get("DEF").toString();
									continue;
								}

								if(par.get("ITEM").toString().equals("SenderPhone")&& SenderPhone.equals(""))
								{
									SenderPhone=par.get("DEF").toString();
									continue;
								}

								if(par.get("ITEM").toString().equals("SenderAddress")&& SenderAddress.equals(""))
								{
									SenderAddress=par.get("DEF").toString();
									continue;
								}
								if(par.get("ITEM").toString().equals("ecEGSProductName")&& productName.equals(""))
								{
									productName=par.get("DEF").toString();
									continue;
								}
							}						
						}	

						Map<String, Object> mapPara=new HashMap<>();
						mapPara.put("shopId", shopId);
						mapPara.put("ecName", ecName);
						mapPara.put("ecHotline", ecHotline);
						mapPara.put("ecWebsite", ecWebsite);
						mapPara.put("ecEGSExpress", ecEGSExpress);
						mapPara.put("SenderName", SenderName);
						mapPara.put("SenderSuDa5", SenderSuDa5);
						mapPara.put("SenderPhone", SenderPhone);
						mapPara.put("SenderAddress", SenderAddress);
						mapPara.put("productName", productName);

						listmapShopPara.add(mapPara);
					}

					String receiverSiteno = oneData.get("RECEIVER_SITENO").toString();
					String distanceNo = oneData.get("DISTANCENO").toString();
					String distanceName = oneData.get("DISTANCENAME").toString();
					String receiverFivecode = oneData.get("RECEIVER_FIVECODE").toString();
					String receiverSevencode = oneData.get("RECEIVER_SEVENCODE").toString();
					String senderSiteno = oneData.get("SENDER_SITENO").toString();
					String expressBilltype = oneData.get("EXPRESSBILLTYPE").toString();


					String shipmentNo = oneData.get("SHIPMENTNO").toString();
					lev1.setShipmentNo(oneData.get("SHIPMENTNO").toString());
					lev1.setShopId(shopId);

					//取到 货运厂商的代码，用于判断  大智通 和 便利达康的母子厂商 
					String lgPlatformNo = oneData.get("LGPLATFORMNO").toString();
					lev1.setLgPlatformNo(lgPlatformNo);

					lev1.setLgPlatformName(oneData.get("LGPLATFORMNAME").toString());
					lev1.setExpressNo(oneData.get("EXPRESSNO").toString());
					
					String  shipDate=oneData.get("SHIPDATE").toString();
					//配送日期处理
					if (shipDate.equals("")) 
					{
						shipDate=oneData.get("ORDER_SHIPDATE").toString();
					}	
					
					lev1.setShipDate(shipDate);					
					
					lev1.setShipHourType(oneData.get("SHIPHOURTYPE").toString());
					lev1.setPackageNo(oneData.get("PACKAGENO").toString());
					lev1.setPackageName(oneData.get("PACKAGENAME").toString());
					lev1.setMeasureNo(oneData.get("MEASURENO").toString());
					lev1.setMeasureName(oneData.get("MEASURENAME").toString());
					lev1.setTemperateNo(oneData.get("TEMPERATENO").toString());
					lev1.setTemperateName(oneData.get("TEMPERATENAME").toString());
					lev1.setTransFee(oneData.get("TRANSFEE").toString());
					lev1.setTotBoxes(oneData.get("TOTBOXES").toString());
					lev1.setBoxNo(oneData.get("BOXNO").toString());
					lev1.setEcPlatformNo(oneData.get("ECPLATFORMNO").toString());
					lev1.setEcPlatformName(oneData.get("ECPLATFORMNAME").toString());
					lev1.setEcOrderNo(oneData.get("ECORDERNO").toString());
					lev1.setRealAmt(oneData.get("REALAMT").toString());
					lev1.setWeight(oneData.get("WEIGHT").toString());
					lev1.setPieces(oneData.get("PIECES").toString());
					lev1.setOrderer(oneData.get("ORDERER").toString());
					lev1.setOrdererPhone(oneData.get("ORDERERPHONE").toString());
					lev1.setReceiver(oneData.get("RECEIVER").toString());
					lev1.setReceiverMobile(oneData.get("RECEIVERMOBILE").toString());
					lev1.setReceiverPhone(oneData.get("RECEIVERPHONE").toString());
					lev1.setReceiverAddress(oneData.get("RECEIVERADDRESS").toString());
					lev1.setReceiverEmail(oneData.get("RECEIVEREMAIL").toString());
					lev1.setGetshopNo(oneData.get("GETSHOPNO").toString());
					lev1.setGetshopName(oneData.get("GETSHOPNAME").toString());
					lev1.setCollectAmt(oneData.get("COLLECTAMT").toString());
					lev1.setSupermarketStatus(oneData.get("SUPERMARKETSTATUS").toString());
					lev1.setMemo(oneData.get("MEMO").toString());
					
					lev1.setDelMemo(oneData.get("ORDER_DELMEMO").toString());
					lev1.setStatus(oneData.get("STATUS").toString());

					lev1.setExportDoc(oneData.get("EXPORTDOC").toString());
					lev1.setDeliveryType(oneData.get("DELIVERYTYPE").toString());

					lev1.setExpressBilltype(expressBilltype);
					lev1.setReceiverSiteno(receiverSiteno);
					lev1.setSenderSiteno(senderSiteno);
					lev1.setDistanceNo(distanceNo);
					lev1.setDistanceName(distanceName);
					lev1.setReceiverFivecode(receiverFivecode);
					lev1.setReceiverSevencode(receiverSevencode);
					lev1.setRtnExpressno(oneData.get("GREENWORLD_RTNORDERNO").toString());

					String motherVendorNo = "";
					String sonVendorNo = "";
					String egsCustomerNo="";

					if(lgPlatformNo.toUpperCase().equals("DZT"))
					{
						motherVendorNo = oneData.get("DZT_MOTHERVENDORNO").toString();
						sonVendorNo = oneData.get("DZT_SONVENDORNO").toString();
					}
					else if (lgPlatformNo.toUpperCase().equals("CVS"))
					{
						motherVendorNo = oneData.get("CVS_MOTHERVENDORNO").toString();
						// 便利达康 没有子厂商， 默认给母厂商的值
						sonVendorNo = motherVendorNo; 
					}
					else if (lgPlatformNo.toUpperCase().equals("EGS"))
					{
						egsCustomerNo = oneData.get("CUSTOMERNO").toString();
					}

					lev1.setMotherVendorNo(motherVendorNo);
					lev1.setSonVendorNo(sonVendorNo);

					lev1.setLargeLogisticsNo(oneData.get("LARGELOGISTICSNO").toString());
					lev1.setCollectNo(oneData.get("COLLECTNO").toString());
					lev1.setEcName(ecName);
					lev1.setEcHotline(ecHotline);
					lev1.setEcWebsite(ecWebsite);
					lev1.setEcEGSExpress(ecEGSExpress);
					lev1.setSenderRealname(SenderName);
					lev1.setSenderPostcode(SenderSuDa5);
					lev1.setSenderTel(SenderPhone);
					lev1.setSenderAddress(SenderAddress);
					lev1.setEgsCustomerNo(egsCustomerNo);
					lev1.setProductName(productName);

					lev1.setDatas(new ArrayList<DCP_OrderECShippingQueryRes.level2Elm>());

					for (Map<String, Object> oneData2 : getQDataDetail) 
					{
						if(shipmentNo.equals(oneData2.get("SHIPMENTNO"))){
							DCP_OrderECShippingQueryRes.level2Elm lev2 = res.new level2Elm();

							String item = oneData2.get("ITEM").toString();
							String pluNo = oneData2.get("PLUNO").toString();
							String pluName = oneData2.get("PLUNAME").toString();
							String barcode = oneData2.get("BARCODE").toString();
							String qty = oneData2.get("QTY").toString();
							String specName = oneData2.get("SPECNAME").toString();
							String amt = oneData2.get("AMT").toString();
							String fileName = oneData2.get("FILENAME").toString();

							lev2.setItem(item);
							lev2.setPluNo(pluNo);
							lev2.setPluName(pluName);
							lev2.setBarcode(barcode);
							lev2.setQty(qty);
							lev2.setSpecName(specName);
							lev2.setAmt(amt);
							lev2.setFileName(fileName);

							lev1.getDatas().add(lev2);
							lev2 = null;
						}
					}

					res.getDatas().add(lev1);
					lev1 =null;

				}
				//
				if (listmapShopPara!=null) 
				{
					listmapShopPara.clear();
					listmapShopPara=null;
				}

			}
			else{
				res.setDatas(new ArrayList<DCP_OrderECShippingQueryRes.level1Elm>());
			}
		} catch (Exception e) {

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
	protected String getQuerySql(DCP_OrderECShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String shopId = req.getShopId();
		String lgPlatformNo = "";
		String ecPlatformNo = "";
		String expressBillType = ""; //托运单类别  A:一般托运单 B:代收托运单
		String expressNo = "";
		String temperateLayerNo = "";
		String shipmentNo = "";

		String startDate = "";
		String endDate = "";
		String printStatus = "";
		String shipStatus = "";
		String ecOrderNo = "";
		String telphone = "";
		String exportDoc = "";

		if(req.getTelphone() == null || req.getTelphone().equals("")){
			telphone = "";
		}
		else{
			telphone = req.getTelphone();
		}

		if(req.getShipStatus() == null || req.getShipStatus().equals("")){
			shipStatus = "";
		}
		else{
			shipStatus = req.getShipStatus();
		}

		if(req.getLgPlatformNo() == null || req.getLgPlatformNo().equals("")){
			lgPlatformNo = "";
		}
		else{
			lgPlatformNo = req.getLgPlatformNo();
		}

		if(req.getEcPlatformNo() == null || req.getEcPlatformNo().equals("")){
			ecPlatformNo = "";
		}
		else{
			ecPlatformNo = req.getEcPlatformNo();
		}
		if(req.getExpressBilltype() == null || req.getExpressBilltype().equals("")){
			expressBillType = "";
		}
		else{
			expressBillType = req.getExpressBilltype();//托运单类别  A:一般托运单 B:代收托运单
		}
		if(req.getExpressNo() == null || req.getExpressNo().equals("")){
			expressNo = "";
		}
		else{
			expressNo = req.getExpressNo();
		}
		if(req.getTemperateLayerNo() == null || req.getTemperateLayerNo().equals("")){
			temperateLayerNo = "";
		}
		else{
			temperateLayerNo = req.getTemperateLayerNo();
		}
		if(req.getShipmentNo() == null || req.getShipmentNo().equals("")){
			shipmentNo = "";
		}
		else{
			shipmentNo = req.getShipmentNo();
		}
		String createBy = "''";
		String createByList[] = {};
		if(req.getCreateBy() == null || req.getCreateBy().equals("")){
			createByList = createByList;
		}
		else{
			createByList = req.getCreateBy();
			for (int i = 0; i < createByList.length; i++) {
				createBy += ",'"+createByList[i]+"'";
			}
		}
		if(req.getStartDate() == null || req.getStartDate().equals("")){
			startDate = "";
		}
		else{
			startDate = req.getStartDate();
		}

		if(req.getEndDate() == null || req.getEndDate().equals("")){
			endDate = "";
		}
		else{
			endDate = req.getEndDate();
		}

		if(req.getPrintStatus() == null || req.getPrintStatus().equals("")){
			printStatus = "";
		}
		else{
			printStatus = req.getPrintStatus();
		}

		if(req.getEcOrderNo() == null || req.getEcOrderNo().equals("")){
			ecOrderNo = "";
		}
		else{
			ecOrderNo = req.getEcOrderNo();
		}

		if( req.getExportDoc() == null || req.getExportDoc().equals("")){
			exportDoc = "";
		}
		else{
			exportDoc = req.getExportDoc();
		}


		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		if(pageNumber == 0){
			pageNumber = 1;
		}
		if(pageSize == 0){
			pageSize = 50;
		}

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT COUNT(SSD.shipmentNO  ) OVER() NUM ,dense_rank() over(ORDER BY  SSD.shipmentNO ) rn , SSD.* from ( "
				+ " select a.EID,a.shipmentNO,a.SHOPID,f.shippingshop, a.lgPLatformNo , a.lgPlatformName , a.expressNO , "
				+ " a.shipDate,f.SHIPDATE as ORDER_SHIPDATE,f.DELMEMO as ORDER_DELMEMO, a.shipHourType , a.packageNo , d.packageName, a.measureNo,"
				+ " a.measureName,a.TEMPERATELAYERNO AS temperateNo,a.TEMPERATELAYERNAME AS temperateName,a.transFee,"
				+ " a.tot_Boxes AS totBoxes,  a.boxNo, a.ecplatformNo, a.ecplatformName,"
				+ " a.ec_OrderNo AS  ecOrderNo, a.realAmt, a.weight, a.pieces, a.orderer, a.orderer_Phone AS ordererPhone,"
				+ " a.receiver, a.receiver_Mobile AS receiverMobile, "
				+ " a.receiver_Phone AS  receiverPhone,  a.receiver_Address AS receiverAddress,  "
				+ " a.receiver_Email AS receiverEmail,"
				+ " a.getshop AS getShopNo , a.getshopName,  a.collectAmt, a.memo,  "
				+ " a.RECEIVER_SITENO,a.DISTANCENO,a.DISTANCENAME,a.RECEIVER_FIVECODE,a.RECEIVER_SEVENCODE,"
				+ " a.SENDER_SITENO,a.EXPRESSBILLTYPE, "
				+ " CASE WHEN a.printCount > 0 THEN 2  ELSE 1  END printStatus , a.sdate ,a.createBy , "
				+ " a.GREENWORLD_RTNORDERNO, "
				+ " b.item , b.pluNo , b.pluName , b. plubarCode AS barCode   , "
				+ " b.qty , b.specName , b.amt , c.fileName, a.status,f.status as ORDERSTATUS,  "
				+ " a.supermarketStatus , a.ORDERER_PHONE as telphone ,"
				+ " a.exportDoc ,a.deliveryType, e.DZT_MOTHERVENDORNO as motherVendorNo,"
				+ " e.dzt_mothervendorno , e.dzt_sonvendorno , e.cvs_largelogisticsno as largelogisticsno, "
				+ " e.cvs_mothervendorNo , "
				+ " e.cvs_collectno as collectno, "
				+ " e.CUSTOMERNO "
				+ " FROM DCP_shipment a "
				+ " inner join OC_order f on a.EID=f.EID and a.SHOPID=f.SHOPID and a.EC_ORDERNO=f.orderno "
				+ " LEFT JOIN DCP_shipment_original b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.shipmentno = b.shipmentno "
				+ " LEFT JOIN DCP_GOODS c ON b.EID = c.EID AND b.pluNo = c.pluNo "
				+ " LEFT JOIN OC_SHIPPACKAGESET d ON a.EID = d.EID AND a.packageno = d.packageNo "
				+ " LEFT JOIN OC_LOGISTICS e on a.EID = e.EID and a.lgPLatformNo = e.lgPLatformNo "
				+ " WHERE a.EID = '"+eId+"' and (f.SHOPID = '"+shopId+"' or f.SHIPPINGSHOP='"+shopId+"') "
				+ " ORDER BY a.shipmentno , a.lgPLatformNo ,  a.expressNO , a.ecplatformNo  ) SSD "
				+ " where 1=1 " );

		if (lgPlatformNo != null && lgPlatformNo.length()!=0) { 	
			sqlbuf.append( " AND  SSD.lgPlatformNo = '"+lgPlatformNo+"' ");
		}

		if (shipStatus != null && shipStatus.length()!=0) 
		{ 	
			sqlbuf.append( " AND  SSD.Status = '"+shipStatus+"' ");

			if (shipStatus.equals("1"))//待发货 
			{
				sqlbuf.append( " AND  SSD.ORDERSTATUS = '13' ");//已点货订单
			}
			else if (shipStatus.equals("1"))//待取件
			{
				sqlbuf.append( " AND  SSD.ORDERSTATUS = '10' ");//已发货订单
			}
		}	


		if (ecPlatformNo != null && ecPlatformNo.length()!=0) { 	
			sqlbuf.append( " AND  SSD.ecPlatformNo = '"+ecPlatformNo+"' ");
		}
		if (expressBillType != null && expressBillType.length()!=0) { 	
			sqlbuf.append( " AND  SSD.expressBillType = '"+expressBillType+"' ");
		}
		if (expressNo != null && expressNo.length()!=0) { 	
			sqlbuf.append( " AND  SSD.expressNo = '"+expressNo+"' ");
		}
		if (temperateLayerNo != null && temperateLayerNo.length()!=0) { 	
			sqlbuf.append( " AND  SSD.temperateNo = '"+temperateLayerNo+"' ");
		}
		if (shipmentNo != null && shipmentNo.length()!=0) { 	
			sqlbuf.append( " AND  SSD.shipmentNo = '"+shipmentNo+"' ");
		}
		if (createByList != null &&  createByList.length > 0 ) { 	
			sqlbuf.append( " AND  SSD.createBy in ( "+createBy+" )");
		}

		if (startDate != null && startDate.length()!=0 && endDate != null && endDate.length()!=0 ) { 	
			sqlbuf.append( " AND  SSD.sdate between '"+startDate+"' and '"+endDate+"' ");
		}

		if (printStatus != null && printStatus.length()!=0 ) { 	
			sqlbuf.append( " AND  SSD.printStatus = '"+printStatus+"'");
		}

		if (ecOrderNo != null && ecOrderNo.length()!=0) { 	
			sqlbuf.append( " AND  SSD.ecOrderNo = '"+ecOrderNo+"' ");
		}

		if (telphone != null && telphone.length()!=0) { 	
			sqlbuf.append( " AND  SSD.telphone = '"+telphone+"' ");
		}

		if (exportDoc != null && exportDoc.length()!=0) { 	
			sqlbuf.append( " AND  SSD.exportDoc = '"+exportDoc+"' ");
		}


		sqlbuf.append("  ) " 
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by shipmentno , lgPLatformNo ,  expressNO , ecplatformNo  ");

		sql = sqlbuf.toString();
		return sql;
	}

}
