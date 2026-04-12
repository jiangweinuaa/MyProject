package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SaleOrderToPorderReq;
import com.dsc.spos.json.cust.req.DCP_SaleOrderToPorderReq.levelRequest;
import com.dsc.spos.json.cust.res.DCP_SaleOrderToPorderRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 销售订单转要货
 * @author yuanyy 2019-07-17
 *
 */
public class DCP_SaleOrderToPorder extends SPosAdvanceService<DCP_SaleOrderToPorderReq, DCP_SaleOrderToPorderRes> {

	private String mDate="";
	private String mTime="";

	@Override
	protected void processDUID(DCP_SaleOrderToPorderReq req, DCP_SaleOrderToPorderRes res) throws Exception {

		String sql = "";
		String porderNO = "";

		String shopId = req.getRequest().getShopId();
		String organizationNO = shopId;
		String eId = req.getRequest().geteId();

		String ofNo = req.getRequest().getOfNo();


		sql = "select * from dcp_order where eid='"+eId+"' and orderno='"+ofNo+"'";
		List<Map<String, Object>> orderDatas = this.doQueryData(sql, null);
		if(orderDatas == null ||orderDatas.isEmpty()){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单不存在！");
		}
		Map<String, Object> orderMap = orderDatas.get(0);
        String PORDERNO_DB = orderMap.getOrDefault("PORDERNO","").toString();
        if (!PORDERNO_DB.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单已要货，不能再要货！");
        }
		String orderStatus = orderMap.getOrDefault("STATUS","").toString();
		if (orderStatus.equals("0") || orderStatus.equals("1")|| orderStatus.equals("2"))
		{
			//订单状态status为：0待审核或1订单开立；才可进行订单编辑，其他订单状态不允许编辑（后端需对订单状态校验）
		}
		else
		{
			StringBuilder statusTypeName = new StringBuilder("");
			String statusType = "1";
			String statusName = HelpTools.GetOrderStatusName(statusType, orderStatus, statusTypeName);
			HelpTools.writelog_waimai("【调用DCP_SaleOrderToPorder订单转要货接口】该订单状态是" + statusName + "，不能要货！ 数据库订单状态status="+orderStatus+"，单号OrderNO=" + ofNo);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单状态是" + statusName + "，不能要货！");
		}
		String isShipcompany = orderMap.getOrDefault("ISSHIPCOMPANY", "").toString();//是否总部生产
		if (!"Y".equals(isShipcompany))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "非总部生产，不能要货！");
		}

		String shipdate = orderMap.getOrDefault("SHIPDATE", "").toString();//配送日期
		if (shipdate.trim().isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配送日期为空，不能要货！");
		}
		boolean pOrderFlg = false;
		StringBuffer error = new StringBuffer("");
		Calendar calendar = Calendar.getInstance();
		String sdate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
		String stime = new SimpleDateFormat("HHmmss").format(calendar.getTime());
		if (PosPub.compare_date(shipdate,sdate)<0)
		{
			pOrderFlg = true;
			error.append("配送日期小于当前日期，");
		}
		if (pOrderFlg)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, error.toString()+"不能要货！");
		}

		String machShop = orderMap.getOrDefault("MACHSHOP", "").toString();
		String memo = orderMap.getOrDefault("MEMO", "").toString();
		if (memo.trim().isEmpty())
		{
			memo = "订单转要货("+ofNo+")";
		}
		else
		{
			memo = "订单转要货("+ofNo+"),"+memo;
		}
		if (memo.length()>250)
		{
			memo = memo.substring(0,250);
		}

		DCP_SaleOrderToPorderReq.levelRequest request = req.getRequest();
		String pTemplateNO = request.getpTemplateNo();
		String isAdd = request.getIsAdd();
		if (!"Y".equals(isAdd)){
			isAdd="N";
		}
		String porderID = PosPub.getGUID(false);
		String status = "2";  // 2待收货   // 3已完成
		String StrISUrgentOrder = request.getIsUrgentOrder();
		if (!"Y".equals(StrISUrgentOrder)){
			StrISUrgentOrder="N";
		}

		//需求日期
		String rDate = shipdate;
		String rTime = "000000";//默认


		Calendar tempcalPre = Calendar.getInstance();//获得当前时间
		Calendar tempcalAdd = Calendar.getInstance();//获得当前时间

		SimpleDateFormat tempdfPre=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat tempdfAdd=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat tempdfAdd1=new SimpleDateFormat("yyyyMMdd");

		//要货默认需求日期   Pre_Demand_Days  参数里已经没有了
		tempcalPre.add(Calendar.DATE, 0);
		String preDemandDate = tempdfPre.format(tempcalPre.getTime());

		//追加要货默认需求日期
		int addDemandDates ;
		String addDemandDate=PosPub.getPARA_SMS(dao, eId, shopId, "Add_Demand_Days");
		if (addDemandDate == null || Check.Null(addDemandDate)|| !PosPub.isNumeric(addDemandDate)) {
			tempcalAdd.add(Calendar.DATE, 0);
			addDemandDate = tempdfAdd.format(tempcalAdd.getTime());
		} else {
			addDemandDates  = Integer.parseInt(addDemandDate);
			tempcalAdd.add(Calendar.DATE, addDemandDates);
			addDemandDate = tempdfAdd1.format(tempcalAdd.getTime());
		}

		String StrISPRedict = "N";//request.getISPRedict()==null?"N":request.getISPRedict();
		// 用于要货单显示 营业预估的调整量和预估量
		String isForecast = "N";//request.getIsForecast() == null?"N":request.getIsForecast();
		String oType = "1";//单据类型：0: 普通要货 1：订单转要货 2:计划报单转要货 3:蛋糕要货 4:千元用量周期要货 5:节日要货 6:购物车要货 7：总部强配

		if("Y".equals(isAdd))
		{
			Date d1=tempdfAdd.parse(rDate);
			Date d2=tempdfAdd.parse(addDemandDate);
			if( d1.compareTo(d2)<0) {
				if(!StrISUrgentOrder.equals("Y")) {
					String tempStr="当前时间已经过了要货截止时间，需求日期不能小于"
							+ addDemandDate.substring(4,6)+"月"+addDemandDate.substring(6,8)+"日";
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,tempStr);
				} else {
					request.setIsUrgentOrder("Y");
				}
			} else {
				request.setIsUrgentOrder("N");
			}
		}
		else
		{
			if (pTemplateNO==null||pTemplateNO.trim().isEmpty())
			{
				Date d1=tempdfAdd.parse(rDate);
				Date d2=tempdfAdd.parse(preDemandDate);
				if( d1.compareTo(d2)<0) {
					if(!StrISUrgentOrder.equals("Y")) {
						String tempStr="当前时间已经过了要货截止时间，需求日期不能小于"
								+ preDemandDate.substring(4,6)+"月"+preDemandDate.substring(6,8)+"日";
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,tempStr);
					} else {
						request.setIsUrgentOrder("Y");
					}
				} else {
					request.setIsUrgentOrder("N");
				}
			}
			else
			{
				//直接改成查询前置日期和前置时间
				String optionalTime = this.GetOptionalTime(req);
				SimpleDateFormat dfDate_cur=new SimpleDateFormat("yyyyMMdd");
				String sallcurtime=rDate;
				//需求日期+现在的时间
				Date d1=dfDate_cur.parse(sallcurtime);
				Date d2=dfDate_cur.parse(optionalTime);
				if( d1.compareTo(d2)<0) {
					if(!StrISUrgentOrder.equals("Y")) {
						String tempDate=tempdfAdd.format(d2);
						String tempStr;
						if (tempDate.length()<8) {
							tempStr="此要货模板当前时间已经过了要货截止时间,需求日期不能小于"+tempDate;
						} else {
							tempStr="此要货模板当前时间已经过了要货截止时间，需求日期不能小于"
									+ tempDate.substring(4,6)+"月"+tempDate.substring(6,8)+"日";
						}

						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,tempStr);
					} else {
						request.setIsUrgentOrder("Y");
					}
				} else {
					request.setIsUrgentOrder("N");
				}
			}

		}

		StrISUrgentOrder = request.getIsUrgentOrder();
		if (!"Y".equals(StrISUrgentOrder)){
			StrISUrgentOrder="N";
		}

		//查询模板对应的发货组织编号
		String receiptOrg ="";
		String rdate_type="";
		String rdate_add="";
		String rdate_values="";
		String rdate_times="";
		String rtimestype="";
		String bccode="";
		//目前没有模板
		receiptOrg = machShop;//发货组织==生产机构
		String createBy = request.getOpNo();
		String bDate = request.getbDate();
		if(Check.Null(bDate)) {
			bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		}
		bDate = sdate;//金大爷说这个是单据日期
		String createDate = sdate;
		String createTime = stime;
		String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		sql = "";
		sql = this.getQueryOrderDetailSql(req);
		HelpTools.writelog_waimai("【调用DCP_SaleOrderToPorder订单转要货接口】查询可要货的商品数量sql语句:"+sql+"，单号OrderNO=" + ofNo);
		List<Map<String,Object>> getQDataDetail = this.doQueryData(sql,null);
		if (getQDataDetail==null||getQDataDetail.isEmpty())
		{
			HelpTools.writelog_waimai("【调用DCP_SaleOrderToPorder订单转要货接口】查询可要货的商品数量=0，(商品数量qty-本门店已有数量shopqty)，单号OrderNO=" + ofNo);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"该订单可要货的商品数量为0");
		}
		int scaleCount = 2;//默认金额小数位
		int scaleCount_qty = 3;//默认数量小数位
		Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
		condition.put("PLUNO", true);
		condition.put("SUNIT", true);
		condition.put("FEATURENO", true);
		List<Map<String,Object>> getHeads = MapDistinct.getMap(getQDataDetail,condition);
		if(getHeads == null || getHeads.isEmpty())
		{
			HelpTools.writelog_waimai("【调用DCP_SaleOrderToPorder订单转要货接口】查询订单可要货的商品数量去重合并相同PLUNO返回为空，单号OrderNO=" + ofNo);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"查询订单可要货的商品数量去重合并异常:返回为空!");
		}

		if (getHeads.size()==getQDataDetail.size())
		{
			//相等，就不用合拼了
		}
		else
		{
			//这里合拼下数量，
			for (Map<String,Object> map_head : getHeads)
			{
				String pluNo = map_head.get("PLUNO").toString();
				String pUnit = map_head.get("SUNIT").toString();
				String featureNo = map_head.get("FEATURENO").toString();
				BigDecimal pqty_b = new BigDecimal("0");
				BigDecimal qty_b = new BigDecimal("0");
				for (Map<String,Object> map_detail : getQDataDetail)
				{
					String pluNo_detail = map_detail.get("PLUNO").toString();
					String pUnit_detail = map_detail.get("SUNIT").toString();
					String featureNo_detail = map_detail.get("FEATURENO").toString();
					String pqty_detail = map_detail.get("PQTY").toString();
					String qty_detail = map_detail.get("QTY").toString();
					BigDecimal pqty_detail_b = new BigDecimal(pqty_detail);
					BigDecimal qty_detail_b = new BigDecimal(qty_detail);
					if (pluNo_detail.equals(pluNo) && pUnit_detail.equals(pUnit) && featureNo_detail.equals(featureNo))
					{
						pqty_b = pqty_b.add(pqty_detail_b);//合计PQTY数量
						qty_b = qty_b.add(qty_detail_b);//合计QTY数量
					}
					else
					{
						continue;
					}

				}

				map_head.put("PQTY",pqty_b.setScale(scaleCount_qty,BigDecimal.ROUND_HALF_UP).toString());
				map_head.put("QTY",qty_b.setScale(scaleCount_qty,BigDecimal.ROUND_HALF_UP).toString());

			}
		}
		//获取进货价，以及一些异常提示
		List<Map<String, Object>> pluList = new ArrayList<>();
		for (Map<String,Object> map_head : getHeads)
		{
			String pluNo = map_head.get("PLUNO").toString();
			String pluName = map_head.get("PLUNAME").toString();
			String pUnit = map_head.get("SUNIT").toString();
			String featureNo = map_head.get("FEATURENO").toString();
			String baseUnit = map_head.get("UNIT").toString();
			String unitRatio = map_head.get("UNITRATIO").toString();
			String errorStart = "";
			StringBuffer errorDetail = new StringBuffer("");
			if (pluNo.trim().isEmpty())
			{
				errorStart = "商品名称:"+pluName;
				pOrderFlg = true;
				errorDetail.append(",对应的商品编码为空");
			}
			else
			{
				errorStart = "商品编码:"+pluNo;
				if (pUnit.trim().isEmpty())
				{
					pOrderFlg = true;
					errorDetail.append(",对应的单位为空");
				}
				else
				{
					errorStart = "商品编码:"+pluNo+"，单位编码:"+pUnit;
					if (baseUnit.trim().isEmpty()||unitRatio.trim().isEmpty())
					{
						pOrderFlg = true;
						errorDetail.append(",对应的基准单位为空");
					}

				}
			}

			if (pOrderFlg)
			{
				error.append(errorStart+errorDetail.toString());
			}
			Map<String, Object> pluMap = new HashMap<>();
			pluMap.put("PLUNO", pluNo);
			pluMap.put("PUNIT", pUnit); //订单上的销售单位
			pluMap.put("BASEUNIT",baseUnit);
			pluMap.put("UNITRATIO",unitRatio);
			pluList.add(pluMap);
		}

		if (pOrderFlg)
		{
			HelpTools.writelog_waimai("【调用DCP_SaleOrderToPorder订单转要货接口】查询可要货的商品资料异常:"+error.toString()+",单号OrderNO=" + ofNo);
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品资料异常:"+error.toString());
		}
		//查询下要货门店对应的公司
		String companyId = "";
		String orgSql = "select belfirm from DCP_ORG where EID ='"+eId+"' and organizationNo = '"+shopId+"'";
		List<Map<String, Object>> getBelfirmDatas = this.doQueryData(orgSql, null);
		if(getBelfirmDatas != null && getBelfirmDatas.isEmpty() == false){
			companyId = getBelfirmDatas.get(0).get("BELFIRM").toString();
		}
		//获取商品零售价和进货价
		MyCommon MC = new MyCommon();
		List<Map<String, Object>> getPluPrice = MC.getSalePrice_distriPrice(dao,eId,companyId, shopId,pluList,companyId);

		porderNO = getPorderNO(req);
		BigDecimal tot_pqty = new BigDecimal("0");//总数量，
		BigDecimal tot_amt = new BigDecimal("0"); // 零售价总金额 ，
		BigDecimal tot_distriAmt = new BigDecimal("0"); //进货价总金额
		//组装数据
		String[] columnsName = {
				"porderNO", "SHOPID", "item", "pluNO", "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
				"price", "amt", "EID", "organizationNO","Detail_status",
				"REF_WQTY","REF_SQTY","REF_PQTY","SO_QTY","MUL_QTY","MIN_QTY","MAX_QTY","propQty","MEMO"
				,"KQTY" , "KADJQTY", "PROPADJQTY","DISTRIPRICE","DISTRIAMT","BDATE","HEADSTOCKQTY","FEATURENO","UDISTRIPRICE",
				"ISNEWGOODS","ISHOTGOODS"
		};
		int item = 1;
		for (Map<String,Object> map_head : getHeads)
		{
			String pluNo = map_head.get("PLUNO").toString();
			String pluName = map_head.get("PLUNAME").toString();
			String pUnit = map_head.get("SUNIT").toString();
			String featureNo = map_head.get("FEATURENO").toString();
			String baseUnit = map_head.get("UNIT").toString();
			String unitRatio = map_head.get("UNITRATIO").toString();
			String pQty = map_head.get("PQTY").toString();
			String price = map_head.get("PRICE").toString(); //默认取订单里面的单价  by jinzma 20230525

			Map<String, Object> condiV = new HashMap<String, Object>();
			condiV.put("PLUNO",pluNo);
			condiV.put("PUNIT",pUnit); //原料用料单位
			List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
			String distriPrice = "0";
			if(priceList!=null && priceList.size()>0 )
			{
				//price=priceList.get(0).get("PRICE").toString(); //零售价
				distriPrice=priceList.get(0).get("DISTRIPRICE").toString();//进货价
			}
			BigDecimal amt = new BigDecimal("0");
			amt = new BigDecimal(pQty).multiply(new BigDecimal(price));

			BigDecimal distriAmt = new BigDecimal("0");
			distriAmt = new BigDecimal(pQty).multiply(new BigDecimal(distriPrice));

			BigDecimal baseQty = new BigDecimal("0");
			baseQty = new BigDecimal(pQty).multiply(new BigDecimal(unitRatio));

			tot_pqty = new BigDecimal(pQty).add(tot_pqty);
			tot_amt =  amt.add(tot_amt);
			tot_distriAmt = distriAmt.add(tot_distriAmt);

			String Detail_status = "0";//金大爷说 购物车用到

			DataValue[] insValue1 = new DataValue[]{
					new DataValue(porderNO, Types.VARCHAR),//porderNO
					new DataValue(shopId, Types.VARCHAR),//SHOPID
					new DataValue(item, Types.VARCHAR),//item
					new DataValue(pluNo, Types.VARCHAR), //pluNO
					new DataValue(pUnit, Types.VARCHAR),
					new DataValue(pQty, Types.VARCHAR),
					new DataValue(baseUnit, Types.VARCHAR),//BASEUNIT
					new DataValue(baseQty, Types.VARCHAR),//BASEQTY
					new DataValue(unitRatio, Types.VARCHAR),//unit_Ratio
					new DataValue(price, Types.VARCHAR),//price
					new DataValue(amt, Types.VARCHAR),
					new DataValue(eId, Types.VARCHAR),//EID
					new DataValue(shopId, Types.VARCHAR),//organizationNO
					new DataValue(Detail_status, Types.VARCHAR),// Detail_status
					new DataValue("0", Types.VARCHAR),// REF_WQTY
					new DataValue("0", Types.VARCHAR),// REF_SQTY
					new DataValue("0", Types.VARCHAR),// REF_PQTY
					new DataValue("0", Types.VARCHAR),// SO_QTY
					new DataValue("0", Types.VARCHAR),// MUL_QTY
					new DataValue("0", Types.VARCHAR),// MIN_QTY
					new DataValue("0", Types.VARCHAR),// MAX_QTY
					new DataValue("0", Types.VARCHAR),// propQty
					new DataValue(memo, Types.VARCHAR),// MEMO
					new DataValue("0", Types.VARCHAR),// KQTY
					new DataValue("0", Types.VARCHAR),// KADJQTY
					new DataValue("0", Types.VARCHAR),// PROPADJQTY
					new DataValue(distriPrice, Types.VARCHAR), // DISTRIPRICE 进货价
					new DataValue(distriAmt, Types.VARCHAR), // DISTRIAMT 进货价总金额
					new DataValue(bDate, Types.VARCHAR), // bDate 单据日期
					new DataValue("0", Types.VARCHAR),//HEADSTOCKQTY
					new DataValue(featureNo, Types.VARCHAR), // featureNo 特征码
					new DataValue("", Types.VARCHAR),//UDISTRIPRICE
					new DataValue("N", Types.VARCHAR),//ISNEWGOODS
					new DataValue("N", Types.VARCHAR)//ISHOTGOODS

			};
			InsBean ib2 = new InsBean("DCP_PORDER_DETAIL", columnsName);
			ib2.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
			item = item + 1;
		}

		String[] columnsHead = {
				"SHOPID", "OrganizationNO","EID","porderNO","BDate", "MEMO",  "Status","CreateBy",
				"Create_Date", "Create_time","ConfirmBy","Confirm_Date","Confirm_Time","submitBy","submit_Date","submit_Time","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","TOT_PQTY", "TOT_AMT", "TOT_CQTY", "rDate", "rTime",
				"Porder_ID", "PTEMPLATENO", "IS_ADD",
				"PRedictAMT", "BeginDate", "EndDate", "avgsaleAMT", "modifRatio", "ISUrgentOrder",
				"ISPREDICT","RECEIPT_ORG","TOT_DISTRIAMT","ISFORECAST","OFNO","OTYPE","UTOTDISTRIAMT",
				"CREATE_CHATUSERID","BCCODE","UPDATE_TIME","TRAN_TIME"
		};

		DataValue[] insValueHead = new DataValue[]{
				new DataValue(shopId, Types.VARCHAR),
				new DataValue(organizationNO, Types.VARCHAR),
				new DataValue(eId, Types.VARCHAR),
				new DataValue(porderNO, Types.VARCHAR),//porderNO
				new DataValue(bDate, Types.VARCHAR),
				new DataValue(memo, Types.VARCHAR),
				new DataValue(status, Types.VARCHAR),//Status 默认已提交
				new DataValue(createBy, Types.VARCHAR),
				new DataValue(createDate, Types.VARCHAR),//Create_Date
				new DataValue(createTime, Types.VARCHAR),
				new DataValue(createBy, Types.VARCHAR),//ConfirmBy
				new DataValue(createDate, Types.VARCHAR),
				new DataValue(createTime, Types.VARCHAR),//Confirm_Time
				new DataValue(createBy, Types.VARCHAR),//submitBy
				new DataValue(createDate, Types.VARCHAR),//submit_Date
				new DataValue(createTime, Types.VARCHAR),
				new DataValue(createBy, Types.VARCHAR),//ACCOUNTBY
				new DataValue(accountDate, Types.VARCHAR),
				new DataValue(createTime, Types.VARCHAR),
				new DataValue(tot_pqty, Types.VARCHAR),
				new DataValue(tot_amt, Types.VARCHAR),
				new DataValue(item, Types.VARCHAR),//TOT_CQTY 种类=单身明细
				new DataValue(rDate, Types.VARCHAR),
				new DataValue(rTime, Types.VARCHAR),
				new DataValue(porderID, Types.VARCHAR),
				new DataValue(pTemplateNO, Types.VARCHAR),
				new DataValue(isAdd, Types.VARCHAR),
				new DataValue("0", Types.DOUBLE),//PRedictAMT
				new DataValue("", Types.VARCHAR),//BeginDate
				new DataValue("", Types.VARCHAR),//EndDate
				new DataValue("0", Types.DOUBLE),//avgsaleAMT
				new DataValue("0", Types.DOUBLE),//modifRatio
				new DataValue(StrISUrgentOrder, Types.VARCHAR),
				new DataValue(StrISPRedict, Types.VARCHAR),
				new DataValue(receiptOrg, Types.VARCHAR),
				new DataValue(tot_distriAmt, Types.VARCHAR),
				new DataValue(isForecast, Types.VARCHAR),
				new DataValue(ofNo, Types.VARCHAR),
				new DataValue(oType, Types.VARCHAR),
				new DataValue("", Types.VARCHAR),
				new DataValue(req.getChatUserId(), Types.VARCHAR),
				new DataValue(bccode, Types.VARCHAR),
				new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR),
				new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR)
		};

		InsBean ib1 = new InsBean("DCP_PORDER", columnsHead);
		ib1.addValues(insValueHead);
		this.addProcessData(new DataProcessBean(ib1));

		//更新订单
		UptBean ub1 = null;
		ub1 = new UptBean("DCP_ORDER");
		ub1.addUpdateValue("PORDERNO", new DataValue(porderNO, Types.VARCHAR));

		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("ORDERNO", new DataValue(ofNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));

		this.doExecuteDataToDB();


		DCP_SaleOrderToPorderRes.level1Elm datas = res.new level1Elm();
		datas.setpOrderNo(porderNO);
		datas.setShopId(shopId);
		res.setDatas(datas);
		res.setSuccess(true);
		res.setServiceDescription("服务执行成功！");
		res.setServiceStatus("000");

		//写下订单历程
		try
		{
			//region 写订单日志
			List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
			orderStatusLog onelv1 = new orderStatusLog();
			onelv1.setLoadDocType(orderMap.get("LOADDOCTYPE").toString());
			onelv1.setChannelId(orderMap.get("CHANNELID").toString());
			onelv1.setLoadDocBillType(orderMap.get("LOADDOCBILLTYPE").toString());
			onelv1.setLoadDocOrderNo(orderMap.get("LOADDOCORDERNO").toString());
			onelv1.seteId(eId);

			onelv1.setOpName(request.getOpName());
			onelv1.setOpNo(createBy);
			onelv1.setShopNo(shopId);
			onelv1.setOrderNo(ofNo);
			onelv1.setMachShopNo(orderMap.get("MACHSHOP").toString());
			onelv1.setShippingShopNo(orderMap.get("SHIPPINGSHOP").toString());
			String statusType_log = "99";// 其他状态
			String updateStaus_log = "99";// 订单修改

			onelv1.setStatusType(statusType_log);
			onelv1.setStatus(updateStaus_log);
			String statusName_log = "订单转要货";
			String statusTypeName_log = "其他状态";
			onelv1.setStatusTypeName(statusTypeName_log);
			onelv1.setStatusName(statusName_log);

			StringBuffer memoLogBuffer = new StringBuffer("");
			memoLogBuffer.append(statusTypeName_log + "-->" + statusName_log);
			memoLogBuffer.append("<br>要货门店:"+shopId);
			memoLogBuffer.append("<br>要货单号:"+porderNO);
			onelv1.setMemo(memoLogBuffer.toString());
			onelv1.setDisplay("0");

			String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			onelv1.setUpdate_time(updateDatetime);

			orderStatusLogList.add(onelv1);

			StringBuilder errorStatusLogMessage = new StringBuilder();
			boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
			if (nRet) {
				HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + ofNo);
			} else {
				HelpTools.writelog_waimai(
						"【写表DCP_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + ofNo);
			}
			//endregion

		}
		catch (Exception e)
		{

		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SaleOrderToPorderReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SaleOrderToPorderReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SaleOrderToPorderReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SaleOrderToPorderReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		DCP_SaleOrderToPorderReq.levelRequest request = req.getRequest();
		if (request==null)
		{
			errMsg.append("request节点不能为空,");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (Check.Null(request.geteId()))
		{
			isFail = true;
			errMsg.append("企业eId不能为空,");
		}
		if (Check.Null(request.getShopId()))
		{
			isFail = true;
			errMsg.append("操作要货门店shopId不能为空,");
		}
		if (Check.Null(request.getOfNo()))
		{
			isFail = true;
			errMsg.append("订单号ofNo不能为空,");
		}
		if (Check.Null(request.getOpNo()))
		{
			isFail = true;
			errMsg.append("操作员opNo不能为空,");
		}


		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_SaleOrderToPorderReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleOrderToPorderReq>(){};
	}

	@Override
	protected DCP_SaleOrderToPorderRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleOrderToPorderRes();
	}


	protected String GetOptionalTime(DCP_SaleOrderToPorderReq req) throws Exception {
		String eId = req.geteId();
		DCP_SaleOrderToPorderReq.levelRequest request = req.getRequest();
		String ptemplateNO = request.getpTemplateNo();
		if (ptemplateNO != null && !ptemplateNO.isEmpty()) {
			String sql = "select PRE_DAY,OPTIONAL_TIME from DCP_ptemplate where ptemplateno='" + ptemplateNO + "' and EID='" + eId + "'";
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData != null && !getQData.isEmpty()) {
				String prdate = getQData.get(0).get("PRE_DAY").toString();
				String prtime = getQData.get(0).get("OPTIONAL_TIME").toString();
				Calendar cal = Calendar.getInstance();//获得当前时间
				SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
				String submitTime = dfTime.format(cal.getTime());
				if (prdate == null || prdate.equals("")) {
					prdate = "0";//默认当前日期
				}
				if (prtime == null || prtime.equals("")) {
					prtime = "235959";
				}
				//过了当天的要货时间，最大能要货的时间需要+1
				if (Integer.parseInt(submitTime) > Integer.parseInt(prtime)) {
					int irdate = Integer.parseInt(prdate) + 1;
					prdate = String.valueOf(irdate);
				}
				//当天只能要多少天的多少货
				Calendar cal_cur = Calendar.getInstance();//获得当前时间
				cal_cur.add(Calendar.DAY_OF_YEAR, Integer.parseInt(prdate));
				SimpleDateFormat dfDate_cur = new SimpleDateFormat("yyyyMMdd");
				String sdate = dfDate_cur.format(cal_cur.getTime());
				return sdate;

			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	private String getPorderNO(DCP_SaleOrderToPorderReq req) throws Exception {
		String shopId = req.getRequest().getShopId();
		String eId = req.getRequest().geteId();
		String porderNO = PosPub.getBillNo(dao, eId, shopId, "YHSQ");
		return porderNO;
	}

	/**
	 * 查询该订单要货商品明细
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getQueryOrderDetailSql(DCP_SaleOrderToPorderReq req) throws Exception
	{
		String eId = req.getRequest().geteId();
		String ofNo = req.getRequest().getOfNo();
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("");
		sqlbuf.append(" select * from (");
		sqlbuf.append(" select B.*,(B.QTY-B.SHOPQTY) AS PQTY,C.OUNIT,C.UNIT,C.UNITRATIO from dcp_order_detail B ");
		sqlbuf.append(" left join dcp_goods_unit C on B.EID=C.EID AND B.PLUNO=C.PLUNO AND B.SUNIT=C.OUNIT ");
		sqlbuf.append(" WHERE B.EID='"+eId+"' AND B.ORDERNO='"+ofNo+"' and (B.PACKAGETYPE='1' or B.PACKAGETYPE='3' or B.PACKAGETYPE is null) ");
		sqlbuf.append(" ) where PQTY>0");
		sql = sqlbuf.toString();
		return sql;
	}



}
