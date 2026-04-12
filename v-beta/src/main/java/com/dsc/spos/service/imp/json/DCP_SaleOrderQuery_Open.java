package com.dsc.spos.service.imp.json;

import com.amazonaws.event.DeliveryMode;
import com.dsc.spos.json.cust.req.DCP_SaleOrderQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_SaleOrderQuery_OpenRes;
import com.dsc.spos.json.cust.res.DCP_SaleOrderQuery_OpenRes.Good;
import com.dsc.spos.json.cust.res.DCP_SaleOrderQuery_OpenRes.Level1Elm;
import com.dsc.spos.json.cust.res.DCP_SaleOrderQuery_OpenRes.Pay;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_SaleOrderQuery_Open extends SPosBasicService<DCP_SaleOrderQuery_OpenReq, DCP_SaleOrderQuery_OpenRes> {

	@Override
	protected boolean isVerifyFail(DCP_SaleOrderQuery_OpenReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) { 
			errMsg.append("request不可为空值, ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().getSaleNo()))
		{
			//如果传了销售单号，可以不用管时间
			if (Check.Null(req.getRequest().getsOrderTime())) {
				errMsg.append("查询开始时间不可为空值, ");
				isFail = true;
			}
			if (Check.Null(req.getRequest().geteOrderTime())) {
				errMsg.append("查询结束时间不可为空值, ");
				isFail = true;
			}
		}

		if (Check.Null(req.getRequest().getEid())) {
			errMsg.append("企业编号eid不可为空值, ");
			isFail = true;
		} 
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_SaleOrderQuery_OpenReq> getRequestType() {
		return new TypeToken<DCP_SaleOrderQuery_OpenReq>() {};
	}

	@Override
	protected DCP_SaleOrderQuery_OpenRes getResponseType() {
		return new DCP_SaleOrderQuery_OpenRes();
	}

	@Override
	protected DCP_SaleOrderQuery_OpenRes processJson(DCP_SaleOrderQuery_OpenReq req) throws Exception {
		DCP_SaleOrderQuery_OpenRes res = new DCP_SaleOrderQuery_OpenRes();
		String logFileName = "DCP_SaleOrderQuery_Open";
		long start_dt = System.currentTimeMillis();
		long dt_spwn = 0;
		int totalRecords = 0;
		int totalPages = 0;

		if (req.getPageNumber()<=0)
		{
			req.setPageNumber(1);//默认10条
		}
		if (req.getPageSize()<=0)
		{
			req.setPageSize(10);//默认10条
		}

		if (req.getPageSize()>100)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "分页时每页数量不能大于100");
		}

		String sql = getQuerySql(req);
		long dt1 = System.currentTimeMillis();
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
		long dt2 = System.currentTimeMillis();
		dt_spwn = dt2-dt1;
		//if (dt_spwn>=1000)
		{
			HelpTools.writelog_fileName("【查询分页】耗时:[" + dt_spwn+"]MS，请求ID="+req.getRequestId()+",查询sql:"+sql,logFileName);
		}

		List<Level1Elm> datas = new ArrayList<>();
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			Map<String, Object> oneData_Count = getQDataDetail.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			dt1 = System.currentTimeMillis();
			for (Map<String, Object> par : getQDataDetail)
			{
				String saleNo = par.get("SALENO").toString();
				String shopId = par.get("SHOPID").toString();
				String saleSql = " SELECT "
						+ "	A.MEMO,A.MACHINE,A.BDATE,A.CARDNO,A.MEMBERID,A.TOT_QTY,A.TOT_AMT,A.TOT_OLDAMT,A.TOT_CHANGED,A.SDATE,A.STIME,A.OFNO,A.TOT_DISC,A.TYPE,A.GETMODE,"
						+ "	B.ITEM,B.PLUNO,B.PLUBARCODE,B.PNAME,B.OLDPRICE,B.PRICE,B.QTY,B.DISC,B.AMT,B.RQTY,B.MEMO MEMO_D,B.ISPACKAGE,B.PACKAGEMASTER,B.UNIT,B.PACKAGEAMT,B.PACKAGEQTY,B.UPITEM,"
						+ " C.ITEM ITEM_P,C.PAYCODE,C.PAYNAME,C.CARDNO CARDNO_P,C.PAYSERNUM,C.PAY,C.CHANGED,C.EXTRA,C.SERIALNO,C.REFNO,C.TERIMINALNO "
						+ " FROM "
						+ "	DCP_SALE A LEFT JOIN DCP_SALE_DETAIL B on A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO "
						+ " LEFT JOIN DCP_SALE_PAY C on A.EID=C.EID AND A.SHOPID=C.SHOPID AND A.SALENO=C.SALENO"
						+ " WHERE A.EID = '"+req.getRequest().getEid()+"' AND A.SHOPID = '"+shopId+"' AND A.SALENO = '"+saleNo+"' ";
				List<Map<String,Object>> saleDatas = this.doQueryData(saleSql, null);
				if (saleDatas==null||saleDatas.isEmpty())
				{
					continue;
				}
				Map<String, Object> oneData = saleDatas.get(0);
				Level1Elm level1Elm = res.new Level1Elm();
				level1Elm.setSaleNo(saleNo);
				level1Elm.setMemo(oneData.get("MEMO").toString());
				level1Elm.setMachine(oneData.get("MACHINE").toString());
				level1Elm.setBdate(oneData.get("BDATE").toString());
				level1Elm.setCardNo(oneData.get("CARDNO").toString());
				level1Elm.setMemberId(oneData.get("MEMBERID").toString());
//				level1Elm.setMobilePhone(oneData.get("MEMO").toString());
				level1Elm.setTot_qty(oneData.get("TOT_QTY").toString());
				level1Elm.setAmt(oneData.get("TOT_AMT").toString());
				level1Elm.setTot_oldAmt(oneData.get("TOT_OLDAMT").toString());
				level1Elm.setTot_changed(oneData.get("TOT_CHANGED").toString());
				level1Elm.setSdate(oneData.get("SDATE").toString());
				level1Elm.setStime(oneData.get("STIME").toString());
				level1Elm.setShopNo(shopId);
				level1Elm.setCrateTime(oneData.get("SDATE").toString()+oneData.get("STIME").toString()+"000");
				level1Elm.setO_saleNo(oneData.get("OFNO").toString());
				level1Elm.setAmtDiscount(oneData.get("TOT_DISC").toString());
				level1Elm.setType(oneData.get("TYPE").toString());
				level1Elm.setShipType(oneData.get("GETMODE").toString());

				/*//DCP_ORDER
				if(oneData.get("OFNO") != null){
					String orderSql = "SELECT SHIPTYPE FROM TV_ORDER WHERE COMPANYNO = ? AND ORDERNO = ?  ";
					String[] orderConditionValues = { req.getRequest().getEid(), oneData.get("OFNO").toString() };
					List<Map<String,Object>> orderDatas = this.doQueryData(orderSql, orderConditionValues);
					if(CollectionUtils.isNotEmpty(orderDatas)){
						level1Elm.setShipType(orderDatas.get(0).get("SHIPTYPE").toString());
					}
				}*/

				//商品列表TD_SALE_DETAIL
				List<Good> goodList = new ArrayList<>();
				/*String goodSql = " SELECT "
				+ "	B.ITEM,B.PLUNO,B.PLUBARCODE,B.PNAME,B.OLDPRICE,B.PRICE,B.QTY,B.DISC,B.AMT,B.RQTY,B.MEMO MEMO_D,B.ISPACKAGE,B.PACKAGEMASTER,B.UNIT,B.PACKAGEAMT,B.PACKAGEQTY,B.UPITEM"
				+ " FROM "
				+ "	DCP_SALE_DETAIL B "
				+ " WHERE B.EID = ? AND B.SHOPID = ? AND B.SALENO = ? ";
				String[] goodConditionValues = { req.getRequest().getEid(), shopId,saleNo };
				List<Map<String,Object>> goodDatas = this.doQueryData(goodSql, goodConditionValues);*/
				Map<String, Boolean> conditionValues = new HashMap<>();
				conditionValues.put("ITEM",true);
				List<Map<String,Object>> goodDatas = MapDistinct.getMap(saleDatas,conditionValues);
				for (Map<String,Object> goodMap : goodDatas) {
					if (goodMap.get("ITEM")==null||goodMap.get("ITEM").toString().isEmpty())
					{
						continue;
					}
					Good good = res.new Good();
					good.setItem(goodMap.get("ITEM").toString());
					good.setPluno(goodMap.get("PLUNO").toString());
					good.setPluBarcode(goodMap.get("PLUBARCODE").toString());
					good.setPluName(goodMap.get("PNAME").toString());
					good.setOldPrice(goodMap.get("OLDPRICE").toString());
					good.setPrice(goodMap.get("PRICE").toString());
					good.setQty(goodMap.get("QTY").toString());
					good.setDisc(goodMap.get("DISC").toString());
					good.setAmt(goodMap.get("AMT").toString());
					good.setRqty(goodMap.get("RQTY").toString());
					good.setMemo(goodMap.get("MEMO_D").toString());
					good.setIsPackage(goodMap.get("ISPACKAGE").toString());
					good.setPackageMaster(goodMap.get("PACKAGEMASTER").toString());
					good.setUnit(goodMap.get("UNIT").toString());
					good.setPackageAmt(goodMap.get("PACKAGEAMT").toString());
					good.setPackageQty(goodMap.get("PACKAGEQTY").toString());
					good.setUpItem(goodMap.get("UPITEM").toString());
					
					goodList.add(good);
				}
				level1Elm.setGoodList(goodList);
				
				//支付列表TD_SALE_PAY
				List<Pay> payList = new ArrayList<>();
				/*String paySql = "SELECT C.ITEM,C.PAYCODE,C.PAYNAME,C.CARDNO,C.PAYSERNUM,C.PAY,C.CHANGED,C.EXTRA,C.SERIALNO,C.REFNO,C.TERIMINALNO FROM DCP_SALE_PAY C WHERE EID = ? AND SHOPID = ? AND SALENO = ? ";
				String[] payConditionValues = { req.getRequest().getEid(),shopId,saleNo };
				List<Map<String,Object>> payDatas = this.doQueryData(paySql, payConditionValues);*/
				conditionValues.clear();
				conditionValues.put("ITEM_P",true);
				List<Map<String,Object>> payDatas = MapDistinct.getMap(saleDatas,conditionValues);
				for (Map<String,Object> payMap : payDatas) {
					if (payMap.get("ITEM_P")==null||payMap.get("ITEM_P").toString().isEmpty())
					{
						continue;
					}
					Pay pay = res.new Pay();
					pay.setItem(payMap.get("ITEM_P").toString());
					pay.setPayCode(payMap.get("PAYCODE").toString());
					pay.setPayName(payMap.get("PAYNAME").toString());
					pay.setCardNo(payMap.get("CARDNO_P").toString());
					pay.setPayserNum(payMap.get("PAYSERNUM").toString());
					pay.setPay(payMap.get("PAY").toString());
					pay.setChanged(payMap.get("CHANGED").toString());
					pay.setExtra(payMap.get("EXTRA").toString());
					pay.setSerialNo(payMap.get("SERIALNO").toString());
					pay.setRefNo(payMap.get("REFNO").toString());
					pay.setTeriminalNo(payMap.get("TERIMINALNO").toString());
					
					payList.add(pay);
				}
				level1Elm.setPayList(payList);
				
				datas.add(level1Elm);
			}
			dt2 = System.currentTimeMillis();
			dt_spwn = dt2-dt1;
			//if (dt_spwn>=1000)
			{
				HelpTools.writelog_fileName("【查询明细】耗时:[" + dt_spwn+"]MS，请求ID="+req.getRequestId(),logFileName);
			}
		}
		res.setDatas(datas);
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		long end_dt = System.currentTimeMillis();
		dt_spwn = end_dt-start_dt;
		//if (dt_spwn>=1000)
		{
			HelpTools.writelog_fileName("【本次查询】总耗时:[" + dt_spwn+"]MS，请求ID="+req.getRequestId(),logFileName);
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		
	}

	@Override
	protected String getQuerySql(DCP_SaleOrderQuery_OpenReq req) throws Exception {
		String sql = "";
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		int endRow = startRow+pageSize;
		//分页处理
		StringBuffer sqlbuf = new StringBuffer();
		
		/*sqlbuf.append(" SELECT * FROM ( "
			+ " SELECT count(*) over() NUM,ROWNUM RN, t.* FROM ( "
			+ " select ts.*, TO_CHAR(ts.SDATE || ts.STIME || '000') CREATE_DATETIME "
			+ "	FROM DCP_SALE ts "
			+ "	WHERE ts.EID = '"+req.getRequest().getEid()+"' "
		);*/
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT count(*) over() NUM,ROWNUM RN, t.* FROM ( "
				+ " select ts.SHOPID,TS.SALENO "
				+ "	FROM DCP_SALE ts "
				+ "	WHERE ts.EID = '"+req.getRequest().getEid()+"' "
		);

		//销售单号 不关联单号岂止时间
		if (!Check.Null(req.getRequest().getSaleNo()))
		{
			sqlbuf.append(" and ts.SALENO = '"+req.getRequest().getSaleNo()+"' ");
		}
		else
		{
			sqlbuf.append(" and ts.SDATE <= '"+req.getRequest().geteOrderTime()+"' AND ts.SDATE >= '"+req.getRequest().getsOrderTime()+"' ");
			//是否只查询会员销售单 Y表示是 N或者不填表示不是
			if (!Check.Null(req.getRequest().getIsMember()))
			{
				if("Y".equals(req.getRequest().getIsMember())) {
					sqlbuf.append(" and ts.MEMBERID is not null ");
				}
				if("N".equals(req.getRequest().getIsMember())) {
					sqlbuf.append(" and ts.MEMBERID is null ");
				}
			}

			//会员卡号
			if (!Check.Null(req.getRequest().getCardNo()))
			{
				sqlbuf.append(" and ts.CARDNO = '"+req.getRequest().getCardNo()+"' ");
			}

			//会员ID
			if (!Check.Null(req.getRequest().getMemberId()))
			{
				sqlbuf.append(" and ts.MEMBERID = '"+req.getRequest().getMemberId()+"' ");
			}



			//下单门店编码
			if (!Check.Null(req.getRequest().getShopId()))
			{
				sqlbuf.append(" and ts.SHOPID = '"+req.getRequest().getShopId()+"' ");
			}

			//0 POS,1 扫码，2 APP,3 外卖，4 PDA，5 小程序 30.订单中心 31.美团 32.饿了么 33.微商城 34.官网 35.舞像 36.京东到家 不传表示查询全部类型
			if (!Check.Null(req.getRequest().getOrderChannel()))
			{
				sqlbuf.append(" and ts.APPTYPE = '"+req.getRequest().getOrderChannel()+"' ");
			}

			//是否是退单，Y是退单 N是原单，不传查全部
			if (!Check.Null(req.getRequest().getIsReturn()))
			{
				if("Y".equals(req.getRequest().getIsReturn())) {
					sqlbuf.append(" and ts.TYPE = 1 ");
				}
				else
				{
					sqlbuf.append(" and ts.TYPE = 0 ");
				}
			}
			else
			{
				sqlbuf.append(" and (ts.TYPE = 0  or ts.TYPE = 1 )");
			}
		}


		//排序会影响效能，不传或者传的不对，不排序
		if (!Check.Null(req.getRequest().getSort()))
		{
			//0：由近到远；1由远到近
			if("0".equals(req.getRequest().getSort())) {
				sqlbuf.append(" order by ts.TRAN_TIME  ");
			}
			else if ("1".equals(req.getRequest().getSort()))
			{
				sqlbuf.append(" order by ts.TRAN_TIME desc ");
			}
			else
			{

			}
		}

		
		sqlbuf.append(" ) t ) WHERE rn > "+startRow+"  and RN <= "+endRow);
		
		sql = sqlbuf.toString();
		return sql;
	}

}
