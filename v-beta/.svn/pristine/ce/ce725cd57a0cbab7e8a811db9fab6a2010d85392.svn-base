package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StockTakePLReq;
import com.dsc.spos.json.cust.req.DCP_StockTakePLReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_StockTakePLRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：StockTakePLDCP
 *    說明：商品查询
 * 服务说明：商品查询
 * @author JZMA
 * @since  2018-11-21
 */
public class DCP_StockTakePL extends SPosBasicService<DCP_StockTakePLReq, DCP_StockTakePLRes>
{
	@Override
	protected boolean isVerifyFail(DCP_StockTakePLReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		levelElm request = req.getRequest();
		String bDate = request.getbDate();
		String docType = request.getDocType();
		String plType = request.getPlType();
		String taskWay = request.getTaskWay();
		String notGoodsMode = request.getNotGoodsMode();
		String warehouse= request.getWarehouse();

		if (Check.Null(bDate)) {
			errMsg.append("盘点日期不可为空值, ");
			isFail = true;
		}
		if (Check.Null(docType)) {
			errMsg.append("盘点类型不可为空值, ");
			isFail = true;
		}
		if (Check.Null(plType)) {
			errMsg.append("盈亏类型不可为空值, ");
			isFail = true;
		}
		if (Check.Null(taskWay)) {
			errMsg.append("盘点方式不可为空值, ");
			isFail = true;
		}
		if (Check.Null(notGoodsMode)) {
			errMsg.append("未盘商品处理方式不可为空值, ");
			isFail = true;
		}
		if (Check.Null(warehouse)) {
			errMsg.append("盘点仓库不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockTakePLReq> getRequestType() {
		return new TypeToken<DCP_StockTakePLReq>(){};
	}

	@Override
	protected DCP_StockTakePLRes getResponseType() {
		return new DCP_StockTakePLRes();
	}

	@Override
	protected DCP_StockTakePLRes processJson(DCP_StockTakePLReq req) throws Exception {
		//取得 SQL
		String sql = null;
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String companyId = req.getBELFIRM();
		String shopId = req.getShopId();
		levelElm request = req.getRequest();
		String taskWay = request.getTaskWay();
		String plType = request.getPlType();
		BigDecimal sumPlQty=new BigDecimal(0);  //总盈亏数
		BigDecimal sumPlAmt=new BigDecimal(0);  //总盈亏金额
		BigDecimal sumPlDistriAmt=new BigDecimal(0);  //总盈亏进货价
		try
		{
			//查詢資料
			DCP_StockTakePLRes res = this.getResponse();

			///处理单价和金额小数位数  BY JZMA 20200401   
			String amtLength = PosPub.getPARA_SMS(dao, eId, organizationNO, "amtLength");
			String distriAmtLength = PosPub.getPARA_SMS(dao, eId, organizationNO, "distriAmtLength");
			if (Check.Null(amtLength)||!PosPub.isNumeric(amtLength)) {
				amtLength = "2";
			}
			if (Check.Null(distriAmtLength)||!PosPub.isNumeric(distriAmtLength)) {
				distriAmtLength = "2";
			}
			int amtLengthInt = Integer.parseInt(amtLength);
			int distriAmtLengthInt = Integer.parseInt(distriAmtLength);
			//查詢資料
			sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_StockTakePLRes.level1Elm>());

			if (getQData != null && getQData.isEmpty() == false)  // 有資料，取得詳細內容
			{
				// 拼接返回图片路径  by jinzma 20210705
				String isHttps=PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
				String httpStr=isHttps.equals("1")?"https://":"http://";
				String domainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
				if (domainName.endsWith("/")) {
					domainName = httpStr + domainName + "resource/image/";
				}else{
					domainName = httpStr + domainName + "/resource/image/";
				}

				///处理零售价和配送价
				String price="0";
				String distriPrice="0";
				String plamt ="0";
				String pldistriAmt ="0";
				List<Map<String, Object>> plus = new ArrayList<Map<String, Object>>();
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("PLUNO", true);
				List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);
				for (Map<String, Object> onePlu :getQPlu )
				{
					Map<String, Object> plu = new HashMap<String, Object>();
					plu.put("PLUNO", onePlu.get("PLUNO").toString());
					plu.put("PUNIT", onePlu.get("BASEUNIT").toString());
					plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
					plu.put("UNITRATIO", "1");
					plus.add(plu);
				}
				MyCommon mc = new MyCommon();

				if (Check.Null(companyId))
				{
					sql=" select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
					List<Map<String, Object>> getCompanyId = this.doQueryData(sql, null);
					companyId = getCompanyId.get(0).get("BELFIRM").toString();
				}
				List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(dao,eId,companyId, shopId,plus,companyId);

				for (Map<String, Object> oneData : getQData) {
					DCP_StockTakePLRes.level1Elm oneLv1 = res.new level1Elm();
					// 取得第一層資料庫搜尋結果
					String pluNo = oneData.get("PLUNO").toString();
					String batchNO = oneData.get("BATCHNO").toString();
					String prodDate = oneData.get("PRODDATE").toString();
					String pluName = oneData.get("PLU_NAME").toString();
					String baseQty = oneData.get("BASEQTY").toString();             ///账面数
					String baseUnit = oneData.get("BASEUNIT").toString();
					String baseUnitName = oneData.get("BASEUNITNAME").toString();
					String pqty = oneData.get("PQTY").toString();             ///录入数
					String plQty = oneData.get("PLQTY").toString();           ///差异数 
					String warehouse = oneData.get("WAREHOUSE").toString();
					String featureNo = oneData.get("FEATURENO").toString();
					String spec = oneData.get("SPEC").toString();
					String listImage = oneData.get("LISTIMAGE").toString();

                    String location = oneData.get("LOCATION").toString();
                    String locationName = oneData.get("LOCATIONNAME").toString();
                    String expDate = oneData.get("EXPDATE").toString();
					if (!Check.Null(listImage)){
						listImage = domainName+listImage;
					}
					String featureName = oneData.get("FEATURENAME").toString();

					// 零售价和配送价  by jzma 20190909

					Map<String, Object> condiV=new HashMap<String, Object>();
					condiV.put("PLUNO",pluNo);
					condiV.put("PUNIT",baseUnit);
					List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
					if(priceList!=null && priceList.size()>0 )
					{
						price=priceList.get(0).get("PRICE").toString();
						distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
						BigDecimal price_b=new BigDecimal(price);
						BigDecimal distriPrice_b=new BigDecimal(distriPrice);
						plamt = price_b.multiply(new BigDecimal(plQty)).setScale(amtLengthInt, BigDecimal.ROUND_HALF_UP).toString();
						pldistriAmt = distriPrice_b.multiply(new BigDecimal(plQty)).setScale(distriAmtLengthInt, BigDecimal.ROUND_HALF_UP).toString();
					}


					if ( plType.equals("0") && taskWay.equals("1"))      ///预盈亏且营业中盘点
					{
						String ref_baseqty = oneData.get("REF_BASEQTY").toString();
						String stocktake = oneData.get("STOCKTAKE").toString();
						if (stocktake.equals("1"))  //取盘点单单身的库存数  ELSE 取日结档里的库存数 BY JZMA 20190826
						{
							baseQty=ref_baseqty ;
							float F_plQty =Float.parseFloat(pqty)-Float.parseFloat(ref_baseqty);  //初盘差异数
							plQty = PosPub.GetdoubleScale(F_plQty,4);
							float F_plAmt =F_plQty * Float.parseFloat(price);
							plamt = PosPub.GetdoubleScale(F_plAmt,amtLengthInt);
							float F_pldistriAmt =F_plQty * Float.parseFloat(distriPrice);
							pldistriAmt = PosPub.GetdoubleScale(F_pldistriAmt,distriAmtLengthInt);

						}
					}

					if(Float.parseFloat(plQty)==0)
					{
						continue;
					}

					// 合计金额和合计数量
					BigDecimal a = new BigDecimal(plQty);
					BigDecimal b = new BigDecimal(plamt);
					BigDecimal d = new BigDecimal(pldistriAmt);
					sumPlQty = sumPlQty.add(a);
					sumPlAmt = sumPlAmt.add(b);
					sumPlDistriAmt = sumPlDistriAmt.add(d);

					// 處理調整回傳值；
					oneLv1.setPluNo(pluNo);
					oneLv1.setPluName(pluName);
					oneLv1.setBaseQty(baseQty);
					oneLv1.setBaseUnit(baseUnit);
					oneLv1.setBaseUnitName(baseUnitName);
					oneLv1.setPqty(pqty);
					oneLv1.setPrice(price);
					oneLv1.setWarehouse(warehouse);
					oneLv1.setPlQty(plQty);
					oneLv1.setBatchNo(batchNO);
					oneLv1.setDistriPrice(distriPrice);
					oneLv1.setProdDate(prodDate);
					oneLv1.setFeatureNo(featureNo);
					oneLv1.setSpec(spec);
					oneLv1.setListImage(listImage);
					oneLv1.setFeatureName(featureName);
                    oneLv1.setLocation(location);
                    oneLv1.setLocationName(locationName);
                    oneLv1.setExpDate(expDate);
					res.getDatas().add(oneLv1);
				}
				res.setSumAmt(sumPlAmt.toString());
				res.setSumQty(sumPlQty.toString());
				res.setSumDistriAmt(sumPlDistriAmt.toString());
			}
			else
			{
				res.setSumAmt("0");
				res.setSumQty("0");
			}

			return res;
		}
		catch (Exception e)
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		//調整查出來的資料
	}

	@Override
	protected String getQuerySql(DCP_StockTakePLReq req) throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String langType = req.getLangType();
		levelElm request = req.getRequest();
		String ofNO = request.getOfNo();
		String docType = request.getDocType();      ////0表示全盘 1表示抽盘  2表示按模板盘
		String plType = request.getPlType();
		String stockTakeNO = request.getStockTakeNo();
		String bDate = request.getbDate();
		String taskWay = request.getTaskWay();            ///1.营业中盘点  2.闭店盘点
		String notGoodsMode = request.getNotGoodsMode();  ///1.库存变成零 2.库存不变
		String warehouse = request.getWarehouse();
		String paraIsBatch = PosPub.getPARA_SMS(dao, eId, organizationNO, "Is_BatchNO");
		if (Check.Null(paraIsBatch) || !paraIsBatch.equals("Y"))
			paraIsBatch="N";


		if (plType.equals("0"))      ///盘点单预盈亏或盘点清单预盈亏
		{
			sqlbuf.append(" "
					+ " select distinct a.EID,a.organizationno,a.pluno,a.featureno,b.baseunit,a.ref_baseqty,a.baseqty,a.pqty,a.plqty, "
					+ " oitem,a.warehouse,stocktake,d.plu_name,fn.featurename,image.listimage,N'' spec,"
					+ " e.uname as baseunitName,batchno,proddate,a.location,a.expdate,f.locationname  "
					+ " from ( "
					+ " select a.pluno,a.featureno,a.organizationno,a.eid,sum(a.baseqty) as baseqty,sum(a.pqty) as pqty,"
					+ " sum(a.pqty)-sum(a.baseqty) as plqty,"
					+ " sum(a.ref_baseqty) as ref_baseqty ,max(a.oitem) as oitem,"
					+ " a.warehouse,sum(stocktake) as stocktake,");
			if (paraIsBatch.equals("Y"))  ///启用批号
			{
				sqlbuf.append( " trim(a.batchno) as batchno,max(a.proddate) as proddate,trim(a.location) as location,max(a.expdate) as expdate ");
			}
			else
			{
				sqlbuf.append( " N'' as batchno,N'' as proddate,N'' as location,N'' AS expdate");
			}
			sqlbuf.append( " from (" );

			if (taskWay.equals("1"))      ///营业中盘点
			{
				sqlbuf.append(" "
						+ " select a.pluno,trim(a.featureno) as featureno,a.qty as baseqty ,0 as pqty,0 as ref_baseqty, a.organizationno,a.eid ,0 as oitem,a.warehouse,"
						+ " '0' as stocktake,batchno,proddate,N'' as location,N'' as expdate from dcp_stock_day a "
						+ " where a.eid='"+ eId +"' and a.organizationno='"+organizationNO+"'  and warehouse='"+warehouse+"' "
						+ " union all"
						+ " select a.pluno,trim(a.featureno) as featureno,(a.baseqty*a.stocktype) as baseqty, 0 as pqty,0 as ref_baseqty,a.organizationno,a.eid,0 as oitem,"
						+ " a.warehouse,'0' as stocktake,batchno,"
						+ " proddate,a.location,N'' as  expdate  from dcp_stock_detail a"
						+ " where a.eid='"+ eId +"' and a.organizationno='"+organizationNO+"' and warehouse='"+warehouse+"' and"
						+ " billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','30','31','32','33','34','35','36','37','38','39','40','41','42') "
						+ " union all"
						+ " select a.pluno,trim(a.featureno) as featureno,0 as baseqty,a.baseqty as pqty,"
						+ " nvl(a.ref_baseqty,0) as ref_baseqty,a.organizationno,a.eid ,a.item as oitem,a.warehouse,"
						+ " '1' as stocktake,batch_no as batchno,prod_date as proddate,a.location,a.expdate  "
						+ " from dcp_stocktake_detail a"
						+ " inner join dcp_stocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno"
						+ " where a.eid='"+ eId +"' and a.shopid='"+organizationNO+"' and b.stocktakeno='"+stockTakeNO+"' and b.warehouse='"+warehouse+"'"
						+ " ") ;
			}

			if (taskWay.equals("2"))    ///闭店盘点
			{
				sqlbuf.append( " "
						+ " select a.pluno,trim(a.featureno) as featureno,a.qty as baseqty,0 as pqty,0 as ref_baseqty,a.organizationno,a.eid ,0 as oitem,a.warehouse,"
						+ " '0' as stocktake,batchno,proddate,N'' as location,N'' as expdate  from dcp_stock_day_static a"
						+ " where a.eid='"+ eId +"' and a.organizationno='"+organizationNO+"'  and warehouse='"+warehouse+"' "
						+ " and edate in "
						+ " (select max(edate) from dcp_stock_day_static where eid='"+eId+"' and organizationno='"+organizationNO+"' and edate<='"+bDate+"') "
						+ " union all"
						+ " select a.pluno,trim(a.featureno) as featureno,(a.baseqty*a.stocktype) as baseqty,0 as pqty,0 as ref_baseqty,a.organizationno,"
						+ " a.eid,0 as oitem,a.warehouse,'0' as stocktake,batchno,"
						+ " proddate,a.location,N'' AS expdate  from dcp_stock_detail a"
						+ " where a.eid='"+ eId +"' and a.organizationno='"+organizationNO+"' and"
						+ " billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42')"
						+ " and a.accountdate<=to_date('"+ bDate +"','yyyymmdd') and warehouse='"+warehouse+"'"
						+ " union all"
						+ " select a.pluno,trim(a.featureno) as featureno,0 as baseqty,a.baseqty as pqty,"
						+ " nvl(a.ref_baseqty,0) as ref_baseqty, a.organizationno,a.eid,a.item as oitem,"
						+ " a.warehouse,'1' as stocktake,batch_no as batchno,prod_date as proddate,a.location,a.expdate "
						+ " from dcp_stocktake_detail a"
						+ " inner join dcp_stocktake b on  a.eid=b.eid   and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno "
						+ " where  a.eid='"+ eId +"' and a.shopid='"+organizationNO+"' and b.stocktakeno='"+stockTakeNO+"'"
						+ " and b.warehouse='"+warehouse+"'"
						+ " ");
			}
			sqlbuf.append( " ) a " );

			///未盘商品库存不变   （包括计划单全盘或抽盘、盘点模板、全盘、抽盘） 或者按模板盘
			if ( notGoodsMode.equals("2") || docType.equals("2") )
			{
				sqlbuf.append(" inner join dcp_stocktake_detail b"
						+ " on a.eid=b.eid and a.organizationno=b.shopid and a.pluno=b.pluno "
						+ " and (a.featureno=b.featureno or (trim(a.featureno) is null and trim(b.featureno) is null))"
						+ " and b.stocktakeno='"+stockTakeNO+"'"
						+ " ");
				if (paraIsBatch.equals("Y")) //启用批号
				{
					sqlbuf.append(" and (trim(a.batchno)=trim(b.batch_no) or (trim(a.batchno) is null and trim(b.batch_no) is null))");
                }
                //sqlbuf.append(" and (trim(a.proddate)=trim(b.prod_date) or (trim(a.proddate) is null and trim(b.prod_date) is null))");
                sqlbuf.append(" and (trim(a.location)=trim(b.location) or (trim(a.location) is null and trim(b.location) is null))");
                //sqlbuf.append(" and (trim(a.expdate)=trim(b.expdate) or (trim(a.expdate) is null and trim(b.expdate) is null))");

            }

			///未盘商品库存变成零
			if (docType.equals("1") && notGoodsMode.equals("1") && !Check.Null(ofNO))
			{
				sqlbuf.append(" "
						+ " inner join dcp_stocktask_list b"
						+ " on a.eid=b.eid and a.organizationno=b.organizationno and b.warehouse='"+warehouse+"' "
						+ " and stocktaskno='"+ofNO+"' and a.pluno=b.pluno");
			}

			///全盘且未盘商品库存变成零,不需要处理
			if (paraIsBatch.equals("Y")) //启用批号
			{
				sqlbuf.append(" group by a.pluno,a.featureno,a.organizationno,a.eid,trim(a.location),a.warehouse,trim(a.batchno))a ");
			}
			else
			{
				sqlbuf.append(" group by a.pluno,a.featureno,a.organizationno,a.eid,a.warehouse)a ");
			}

			sqlbuf.append( " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno and b.status='100' "
					+ " left join DCP_GOODS_LANG d ON a.PLUNO=d.PLUNO AND a.EID=d.EID and d.lang_type= '"+langType+"' "
					+ " left join DCP_UNIT_LANG e on b.baseUNIT=e.UNIT AND b.EID=e.EID and e.lang_type='"+langType+"' "
					+ " left join dcp_goods_feature_lang fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno  and fn.lang_type='"+langType+"' "
					//+ " left join dcp_goods_unit_lang gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='"+langType+"'"
					+ " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL' "
                    + " left join DCP_LOCATION f on f.eid=a.eid and f.organizationno=a.organizationno and f.location=a.location and f.warehouse=a.warehouse "
                    + " ");
			sqlbuf.append("  order by oitem ");

		}

		if (plType.equals("1"))
		{
			sqlbuf.append(" select a.pluno,a.featureno,a.baseunit,a.baseqty as plqty,a.price,a.amt as plamt,"
					+ " nvl(b.baseqty,0) as pqty,"
					+ " nvl(b.baseqty,0)-a.baseqty as baseqty,"
					+ " d.plu_name,fn.featurename,gul.spec,image.listimage,"
					+ " e.uname as baseunitName,a.warehouse,a.batch_no as batchno,a.PROD_DATE as PRODDATE,a.DISTRIPRICE,b.location,b.expdate,f.locationname "
					+ " from DCP_adjust_detail a"
					+ " inner join DCP_adjust f on a.EID=f.EID and a.adjustno=f.adjustno and a.SHOPID=f.SHOPID"
					+ " left join DCP_stocktake_detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and f.ofno=b.stocktakeno"
					+ " and a.pluno=b.pluno and (trim(a.batch_no)=trim(b.batch_no) or (trim(a.batch_no) is null and trim(b.batch_no) is null))"
					+ " and (trim(a.featureno)=trim(b.featureno) or (trim(a.featureno) is null and trim(b.featureno) is null))"
					+ " and (trim(a.location)=trim(b.location) or (trim(a.location) is null and trim(b.location) is null)) "
					+ " inner join DCP_GOODS c on a.EID=c.EID and a.pluno=c.pluno and c.status='100'"
					+ " left join DCP_GOODS_lang d on a.EID=d.EID and a.pluno=d.pluno and d.lang_type='"+langType+"' "
					+ " left join DCP_UNIT_lang e on a.EID=e.EID and a.baseunit=e.unit and e.lang_type='"+langType+"'"
					+ " left join dcp_goods_feature_lang fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno  and fn.lang_type='"+langType+"' "
					+ " left join dcp_goods_unit_lang gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='"+langType+"'"
					+ " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL'"
					+ " inner join DCP_stocktake z on a.EID=z.EID and a.organizationno=z.organizationno and a.warehouse=z.warehouse and z.stocktakeno='"+ stockTakeNO + "' "
					+ " left join DCP_LOCATION f on f.eid=a.eid and f.organizationno=a.organizationno and f.location=b.location and f.warehouse=a.warehouse "
                    + " where a.EID='" + eId + "'  and a.organizationno='" + organizationNO + "'  and f.ofno='" + stockTakeNO + "'"
					+ " ");
		}

		return sqlbuf.toString();
	}

}
