package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SaleOrderToPReq;
import com.dsc.spos.json.cust.req.DCP_SaleOrderToPReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SaleOrderToPRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 销售订单转要货  
 * @author yuanyy 2019-07-17
 *
 */
public class DCP_SaleOrderToP extends SPosAdvanceService<DCP_SaleOrderToPReq, DCP_SaleOrderToPRes> {

	private String mDate="";
	private String mTime="";

	@Override
	protected void processDUID(DCP_SaleOrderToPReq req, DCP_SaleOrderToPRes res) throws Exception {

		String sql = "";
		String porderNO = "";

		String oShopId = req.getoShopId();
		String organizationNO = req.getoShopId();
		String eId = req.getoEId();
		String accountDate = PosPub.getAccountDate_SMS(dao, eId, oShopId);
		String ofNo = req.getOFNO();
		String loadShop = req.getLoad_shop();

		sql = this.getQuerySqlPStatus(req);
		List<Map<String, Object>> psDatas = this.doQueryData(sql, null);
		if(psDatas != null && !psDatas.isEmpty()){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该订单已要货！");
		}

		String pTemplateNO = req.getpTemplateNO();
		String isAdd = req.getIsAdd();
		String memo = req.getMemo();
		String porderID = req.getPorderID();
		String status = "2";  // 2待收货   // 3已完成

		//需求日期
		String rDate = req.getrDate();
		String rTime = req.getrTime();

		String preDemandDate = "";//要货默认需求日期
		int preDemandDates ;//要货默认需求日期
		String addDemandDate = "";//追加要货默认需求日期
		String isMustPTemplate = "";
		int addDemandDates ;//追加要货默认需求日期
		Calendar tempcalPre = Calendar.getInstance();//获得当前时间
		SimpleDateFormat tempdfPre=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat tempdf1Pre=new SimpleDateFormat("yyyyMMdd");

		Calendar tempcalAdd = Calendar.getInstance();//获得当前时间
		SimpleDateFormat tempdfAdd=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat tempdfAdd1=new SimpleDateFormat("yyyyMMdd");

		//调拨收货录入方式
		String sqlDef = this.getQuerySqlDef(req);	//
		String[] conditionpreDemandDate = { "Pre_Demand_Days", oShopId, eId }; //查詢條件
		List<Map<String, Object>> getQDatapreDemandDate = this.doQueryData(sqlDef, conditionpreDemandDate);
		if (getQDatapreDemandDate != null && getQDatapreDemandDate.isEmpty() == false)
		{
			preDemandDate = (String) getQDatapreDemandDate.get(0).get("ITEMVALUE");
			if(NumberUtils.isDigits(preDemandDate))
			{
				preDemandDates  = Integer.valueOf(preDemandDate);
				tempcalPre.add(Calendar.DATE, preDemandDates);
				preDemandDate = tempdfPre.format(tempcalPre.getTime());
			}else
			{
				tempcalPre.add(Calendar.DATE, 0);
				preDemandDate = tempdfPre.format(tempcalPre.getTime());
			}
		}
		else
		{
			sqlDef = this.getQuerySqlDef(req);	//
			String[] conditionpreDemandDatea = {"Pre_Demand_Days", eId}; //查詢條件
			List<Map<String, Object>> getQDatapreDemandDatea = this.doQueryData(sqlDef, conditionpreDemandDatea);
			if (getQDatapreDemandDatea != null && getQDatapreDemandDatea.isEmpty() == false)
			{
				preDemandDate = (String) getQDatapreDemandDatea.get(0).get("DEF");
				if(NumberUtils.isDigits(preDemandDate))
				{
					preDemandDates  = Integer.valueOf(preDemandDate);
					tempcalPre.add(Calendar.DATE, preDemandDates);
					preDemandDate = tempdfPre.format(tempcalPre.getTime());
				}else
				{
					tempcalPre.add(Calendar.DATE, 0);
					preDemandDate = tempdfPre.format(tempcalPre.getTime());
				}
			}
			else
			{
				tempcalPre.add(Calendar.DATE, 0);
				preDemandDate = tempdfPre.format(tempcalPre.getTime());
				//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置Pre_Demand_Days参数");
			}
		}

		//Add_Demand_Days
		sqlDef = this.getQuerySqlDef(req);
		String[] conditionaddDemandDate = { "Add_Demand_Days", oShopId, eId }; //查詢條件
		List<Map<String, Object>> getQDataaddDemandDate = this.doQueryData(sqlDef, conditionaddDemandDate);
		if (getQDataaddDemandDate != null && getQDataaddDemandDate.isEmpty() == false)
		{
			addDemandDate = (String) getQDataaddDemandDate.get(0).get("ITEMVALUE");
			if(NumberUtils.isDigits(addDemandDate))
			{
				addDemandDates  = Integer.valueOf(addDemandDate);
				tempcalAdd.add(Calendar.DATE, addDemandDates);
				addDemandDate = tempdfAdd1.format(tempcalAdd.getTime());
			}else
			{
				tempcalAdd.add(Calendar.DATE, 0);
				addDemandDate = tempdfAdd.format(tempcalAdd.getTime());
				//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "要货追单前置天数(Add_Demand_Days)值必须为整数不可为:"+addDemandDate);
			}
		}
		else
		{
			sqlDef = this.getQuerySqlDef(req);	//
			String[] conditionaddDemandDatea = {"Add_Demand_Days", eId}; //查詢條件
			List<Map<String, Object>> getQDataaddDemandDatea = this.doQueryData(sqlDef, conditionaddDemandDatea);
			if (getQDataaddDemandDatea != null && getQDataaddDemandDatea.isEmpty() == false)
			{
				addDemandDate = (String) getQDataaddDemandDatea.get(0).get("DEF");
				if(NumberUtils.isDigits(addDemandDate))
				{
					addDemandDates  = Integer.valueOf(addDemandDate);
					tempcalAdd.add(Calendar.DATE, addDemandDates);
					addDemandDate = tempdfAdd1.format(tempcalAdd.getTime());
				}else
				{
					tempcalAdd.add(Calendar.DATE, 0);
					addDemandDate = tempdfAdd.format(tempcalAdd.getTime());
					//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "要货追单前置天数(Add_Demand_Days)值必须为整数不可为:"+addDemandDate);
				}
			}
			else
			{
				tempcalAdd.add(Calendar.DATE, 0);
				addDemandDate = tempdfAdd.format(tempcalAdd.getTime());
				//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置Pre_Demand_Days参数");
			}
		}

		String StrISUrgentOrder = req.getISUrgentOrder()==null?"N":req.getISUrgentOrder();
//		String StrISPRedict = req.getISPRedict()==null?"N":req.getISPRedict();
		StringBuffer ISUrgentOrder = new StringBuffer(StrISUrgentOrder);
		if(checkGuid(req) == false){

			{
				if(isAdd.equals("Y")){
					//rDate = addDemandDate;
					Date d1=tempdfAdd.parse(rDate);
					Date d2=tempdfAdd.parse(addDemandDate);
					if( d1.compareTo(d2)<0)
					{
						if(!StrISUrgentOrder.equals("Y"))
						{
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需求日期不能小于"+addDemandDate);
						}
						else
						{
							req.setISUrgentOrder("Y");
						}
					}
					else
					{
						req.setISUrgentOrder("N");
					}

				}else if(isAdd.equals("N") && (pTemplateNO == null || pTemplateNO.isEmpty() == true)){
					//rDate = preDemandDate;
					Date d1=tempdfAdd.parse(rDate);
					Date d2=tempdfAdd.parse(preDemandDate);
					if( d1.compareTo(d2)<0)
					{
						if(!StrISUrgentOrder.equals("Y"))
						{
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需求日期不能小于"+preDemandDate);
						}
						else
						{
							req.setISUrgentOrder("Y");
						}
					}
					else
					{
						req.setISUrgentOrder("N");
					}

				}else if(isAdd.equals("N") && pTemplateNO != null && pTemplateNO.isEmpty() == false){
					//直接改成查询前置日期和前置时间
					Calendar curdate=Calendar.getInstance();
					String optionalTime = this.GetOptionalTime(req);
					SimpleDateFormat dfDate_cur=new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat stime=new SimpleDateFormat("HHmmss");
					String scurtime=stime.format(curdate.getTime());
					String sallcurtime=rDate;
					//需求日期+现在的时间
					//rDate = optionalTime;
					Date d1=dfDate_cur.parse(sallcurtime);
					Date d2=dfDate_cur.parse(optionalTime);
					if( d1.compareTo(d2)<0)
					{
						if(!StrISUrgentOrder.equals("Y"))
						{
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需求日期不能小于:"+tempdfAdd.format(d2));
						}
						else
						{
							req.setISUrgentOrder("Y");
						}
					}
					else
					{
						req.setISUrgentOrder("N");
					}

				}
			}

			//查询模板对应的发货组织编号
			String receiptOrg ="";
			sql=" SELECT RECEIPT_ORG  FROM DCP_PTEMPLATE "
					+ " WHERE EID='"+eId+"' AND  PTEMPLATENO='"+pTemplateNO+"' AND DOC_TYPE='0' " ;
			List<Map<String, Object>> getQDate = this.doQueryData(sql, null);

			String orgSql = "select belfirm from DCP_ORG where EID ='"+eId+"' and organizationNo = '"+organizationNO+"'"
					+ " and status='100' ";

			if (getQDate != null && getQDate.isEmpty() == false)
			{
				receiptOrg = getQDate.get(0).get("RECEIPT_ORG").toString();
			}

			else{

				List<Map<String, Object>> getBelfirmDatas = this.doQueryData(orgSql, null);
				if(getBelfirmDatas != null && getBelfirmDatas.isEmpty() == false){
					receiptOrg = getBelfirmDatas.get(0).get("BELFIRM").toString();
				}

			}

			StrISUrgentOrder = req.getISUrgentOrder()==null?"N":req.getISUrgentOrder();

			boolean isFail = false;
			String createBy = req.getO_opNo();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			//String bDate = df.format(cal.getTime());
			String bDate = req.getbDate();
			String createDate = df.format(cal.getTime());
			df=new SimpleDateFormat("HHmmss");
			String createTime = df.format(cal.getTime());

			porderNO = getPorderNO(req);

			String[] columns1 = {
					"SHOPID", "OrganizationNO","EID","porderNO","BDate", "MEMO",  "Status","CreateBy",
					"Create_Date", "Create_time","TOT_PQTY", "TOT_AMT", "TOT_CQTY", "rDate", "rTime", "Porder_ID", "PTEMPLATENO", "IS_ADD",
					"PRedictAMT", "BeginDate", "EndDate", "avgsaleAMT", "modifRatio", "ISUrgentOrder","ISPREDICT","RECEIPT_ORG",
					"OFNO","OTYPE","LOAD_SHOP", "ACCOUNT_DATE","CREATE_CHATUSERID","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
					"UPDATE_TIME","TRAN_TIME"
			};
			DataValue[] insValue1 = null;

			BigDecimal totPqty = new BigDecimal("0");
			BigDecimal totAmt = new BigDecimal("0");
			int totCqty = 0;
			float wqty = 0;
			BigDecimal unitRatio;
			float unitRatio1 = 0;

			//新增單身 (多筆)
			List<level1Elm> jsonDatas = req.getDatas();
			for (level1Elm par : jsonDatas) {

				if(Float.parseFloat(par.getPqty())==0)
				{
					continue;
				}
				int insColCt = 0;
				String[] columnsName = {
						"porderNO", "SHOPID", "item", "pluNO", "punit", "pqty", "wunit", "wqty", "unit_Ratio",
						"price", "amt", "EID", "organizationNO","Detail_status",
						"REF_WQTY","REF_SQTY","REF_PQTY","SO_QTY","MUL_QTY","MIN_QTY","MAX_QTY","propQty","MEMO"
						,"KQTY" , "KADJQTY", "PROPADJQTY","BDATE"
				};

				DataValue[] columnsVal = new DataValue[columnsName.length];

				String pluNO = "";
				String punit = "";
				String wunit = "";

				for (int i = 0; i < columnsVal.length; i++) {
					String keyVal = null;
					switch (i) {
						case 0:
							keyVal = porderNO;
							totCqty = totCqty + 1;
							break;
						case 1:
							keyVal = oShopId;
							break;
						case 2:
							keyVal = par.getItem(); //item
							break;
						case 3:
							keyVal = par.getPluNO(); //pluNO
							pluNO = keyVal;
							break;
						/*case 4:  
						keyVal = par.getFeatureNO(); //featureNO
						if (Check.Null(keyVal)) keyVal=" ";
						break;*/
						case 4:
							keyVal = par.getPunit(); //punit
							punit = keyVal;
							break;
						case 5:
							keyVal = par.getPqty(); //pqty
							totPqty = totPqty.add(new BigDecimal(keyVal));
							//取库存单位、换算率
							List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao,eId, pluNO, punit);
							if (getQData_Ratio != null && getQData_Ratio.isEmpty() == false) {
								wunit = (String) getQData_Ratio.get(0).get("WUNIT");
								unitRatio = (BigDecimal) getQData_Ratio.get(0).get("UNIT_RATIO");
								if(unitRatio.compareTo(BigDecimal.valueOf(0))==0)
								{
									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + pluNO +" 找不到对应的换算关系");
								}
								unitRatio1=unitRatio.floatValue();
								wqty = Float.parseFloat(keyVal) * unitRatio1;
							}else{
								throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + pluNO +" 找不到对应的换算关系");
							}
							break;
						case 6:
							keyVal = wunit;     //wunit
							break;
						case 7:
							keyVal = String.valueOf(wqty);   //wqty
							break;
						case 8:
							keyVal = String.valueOf(unitRatio1);     //unitRatio
							break;
						case 9:
							keyVal = par.getPrice();    //price
							if(par.getPrice()==null || par.getPrice().toString().isEmpty()){
								keyVal = "0";
							}
							break;
						case 10:
							keyVal = par.getAmt();    //amt
							totAmt = totAmt.add(new BigDecimal(keyVal));
							break;
						case 11:
							keyVal = eId;
							break;
						case 12:
							keyVal = organizationNO;
							break;
						case 13:
							keyVal = "0";    //Detail_status
							break;
						case 14:
//						keyVal = par.getRefWQty();
//						if(par.getRefWQty()==null || par.getRefWQty().toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 15:
//						keyVal = par.getRefSQty();
//						if(par.getRefSQty()==null || par.getRefSQty().toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 16:
//						keyVal = par.getRefPQty(); 
//						if(par.getRefPQty()==null || par.getRefPQty().toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 17:
//						keyVal = par.getSoQty();
//						if(par.getSoQty()==null || par.getSoQty().toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 18:
//						keyVal = par.getMulQty();
//						if(par.getMulQty()==null || par.getMulQty().toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 19:
//						keyVal = par.getMinQty(); 
//						if(par.getMinQty()==null || par.getMinQty().toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 20:
//						keyVal = par.getMaxQty(); 
//						if(par.getMaxQty()==null || par.getMaxQty().toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 21:
//						keyVal = Double.toString(par.getPropQty()); 
//						if(keyVal==null || keyVal.toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 22:
							keyVal = par.getMemo();
							if(par.getMemo()==null || par.getMemo().isEmpty()){
								keyVal = "";
							}
							break;

						case 23:
//						keyVal = par.getkQty() ; 
//						if(keyVal==null || keyVal.toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;

						case 24:
//						keyVal = par.getkAdjQty();
//						if(keyVal==null || keyVal.toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;

						case 25:
//						keyVal = par.getPropAdjQty();
//						if(keyVal==null || keyVal.toString().isEmpty()){
//							keyVal = "0";
//						}
							keyVal = "0";
							break;
						case 26:
							keyVal = bDate;
							break;

						default:
							break;
					}

					if (keyVal != null) {
						insColCt++;
						if (i == 2 ){
							columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
						}else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10|| i == 14|| i == 15|| i == 16|| i == 17|| i == 18 || i == 19 || i == 20|| i == 21  ){
							columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
						}else{
							columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
						}
					} else {
						columnsVal[i] = null;
					}
				}
				String[] columns2 = new String[insColCt];
				DataValue[] insValue2 = new DataValue[insColCt];
				// 依照傳入參數組譯要insert的欄位與數值；
				insColCt = 0;

				for (int i=0;i<columnsVal.length;i++){
					if (columnsVal[i] != null){
						columns2[insColCt] = columnsName[i];
						insValue2[insColCt] = columnsVal[i];
						insColCt ++;
						if (insColCt >= insValue2.length) break;
					}
				}

				InsBean ib2 = new InsBean("DCP_PORDER_DETAIL", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2));
			}

			insValue1 = new DataValue[]{
					new DataValue(oShopId, Types.VARCHAR),
					new DataValue(organizationNO, Types.VARCHAR),
					new DataValue(eId, Types.VARCHAR),
					new DataValue(porderNO, Types.VARCHAR),
					new DataValue(bDate, Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(createBy, Types.VARCHAR),
					new DataValue(createDate, Types.VARCHAR),
					new DataValue(createTime, Types.VARCHAR),
					new DataValue(totPqty.toString(), Types.VARCHAR),
					new DataValue(totAmt.toString(), Types.VARCHAR),
					new DataValue(totCqty, Types.VARCHAR),
					new DataValue(rDate, Types.VARCHAR),
					new DataValue(rTime, Types.VARCHAR),
					new DataValue(porderID, Types.VARCHAR),
					new DataValue(pTemplateNO, Types.VARCHAR),
					new DataValue(isAdd, Types.VARCHAR),
					new DataValue("0", Types.DOUBLE),
					new DataValue("", Types.VARCHAR),
					new DataValue("", Types.VARCHAR),
					new DataValue("0", Types.DOUBLE),
					new DataValue("0", Types.DOUBLE),
					new DataValue(StrISUrgentOrder, Types.VARCHAR), // 是否追加要货， 默认也应该为N 
					new DataValue("N", Types.VARCHAR), // 是否千元用量， 这里固定为N
					new DataValue(receiptOrg, Types.VARCHAR),
					new DataValue(ofNo, Types.VARCHAR), //来源单号
					new DataValue("1", Types.VARCHAR), // 来源类型   0：手工要货    1：销售订单转要货
					new DataValue(loadShop, Types.VARCHAR),
					new DataValue(accountDate, Types.VARCHAR),
					new DataValue(req.getChatUserId(), Types.VARCHAR),
					new DataValue(createBy, Types.VARCHAR),
					new DataValue(accountDate, Types.VARCHAR),
					new DataValue(createTime, Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
			};

			InsBean ib1 = new InsBean("DCP_PORDER", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			//****************要货之后， 回写订单状态***************
			UptBean ub1 = null;
			ub1 = new UptBean("OC_ORDER");
			ub1.addUpdateValue("PSTATUS", new DataValue("1", Types.VARCHAR));
			// pStatus  0:未要货    1:已要货

			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("ORGANIZATIONNO", new DataValue(loadShop, Types.VARCHAR));
			ub1.addCondition("SHOPID", new DataValue(loadShop, Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(ofNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();
		}else{
			res.setSuccess(true);
		}

		if (res.isSuccess()) {
			res.setDatas(new ArrayList<DCP_SaleOrderToPRes.level2Elm>());

			DCP_SaleOrderToPRes.level2Elm oneLv2 = res.new level2Elm();
			oneLv2.setPorderNO(porderNO);
			res.getDatas().add(oneLv2);
		} else {
			res.setDatas(new ArrayList<DCP_SaleOrderToPRes.level2Elm>());

			DCP_SaleOrderToPRes.level2Elm oneLv2 = res.new level2Elm();
			oneLv2.setPorderNO("");
			res.getDatas().add(oneLv2);
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SaleOrderToPReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SaleOrderToPReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SaleOrderToPReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SaleOrderToPReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		List<level1Elm> jsonDatas = req.getDatas();

		//必传值不为空
		String rDate = req.getrDate();
		String rTime = req.getrTime();

		//必传值可以为空
		String memo = req.getMemo();

		/** 必傳，門店編號，僅允許為單筆 */
		if (Check.Null(rDate)) {
			errCt++;
			errMsg.append("需求日期不可为空值, ");
			isFail = true;
		}

		if (Check.Null(rTime)) {
			errCt++;
			errMsg.append("需求时间不可为空值, ");
			isFail = true;
		}

		if (memo == null) {
			errCt++;
			errMsg.append("备注不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level1Elm par : jsonDatas) {
			String item = par.getItem();
			//String oItem = par.getoItem();
			String pluNO = par.getPluNO();
			String punit = par.getPunit();
			String pqty = par.getPqty();

			//必传值可以为空			
			String price = par.getPrice();
			String amt = par.getAmt();

			if (Check.Null(item)) {
				errCt++;
				errMsg.append("项次不可为空值, ");
				isFail = true;
			}

			//			if (Check.Null(oItem)) {
			//				errCt++;
			//				errMsg.append("来源项次不可为空值, ");
			//				isFail = true;
			//			} 

			if (Check.Null(pluNO)) {
				errCt++;
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}

			if (Check.Null(punit)) {
				errCt++;
				errMsg.append("商品"+par.getPluNO().toString()+"单位不可为空值, ");
				isFail = true;
			}

			if (Check.Null(pqty)) {
				errCt++;
				errMsg.append("商品"+par.getPluNO().toString()+"数量不可为空值, ");
				isFail = true;
			}

			if (price == null) {
				errCt++;
				errMsg.append("商品"+par.getPluNO().toString()+"单价不可为空值, ");
				isFail = true;
			}

			if (amt == null) {
				errCt++;
				errMsg.append("商品"+par.getPluNO().toString()+"金额不可为空值, ");
				isFail = true;
			}

			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SaleOrderToPReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleOrderToPReq>(){};
	}

	@Override
	protected DCP_SaleOrderToPRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleOrderToPRes();
	}


	protected String GetOptionalTime(DCP_SaleOrderToPReq req) throws Exception {
		String sql = null;
		String eId = req.getoEId();

		String ptemplateNO = req.getpTemplateNO();
		if(ptemplateNO!=null && ptemplateNO.isEmpty()==false)
		{
			sql="select PRE_DAY,OPTIONAL_TIME from DCP_ptemplate where ptemplateno='"+ptemplateNO+"' and EID='"+eId+"'";
			List<Map<String, Object>> getQData = this.doQueryData(sql,null);
			if(getQData != null && getQData.isEmpty() == false)
			{
				String prdate=getQData.get(0).get("PRE_DAY").toString();
				String prtime=getQData.get(0).get("OPTIONAL_TIME").toString();
				Calendar cal = Calendar.getInstance();//获得当前时间
				SimpleDateFormat dfDate=new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat dfTime=new SimpleDateFormat("HHmmss");
				String submitDate = dfDate.format(cal.getTime());
				String submitTime = dfTime.format(cal.getTime());
				if(prdate == null || prdate.equals(""))
				{
					prdate= "0";//默认当前日期     				
				}
				if(prtime==null || prtime.equals(""))
				{
					prtime= "235959";
				}
				//过了当天的要货时间，最大能要货的时间需要+1
				if(Integer.parseInt(submitTime)>Integer.parseInt(prtime))
				{
					int irdate=Integer.parseInt(prdate)+1;
					prdate=String.valueOf(irdate);
				}
				//当天只能要多少天的多少货
				Calendar cal_cur = Calendar.getInstance();//获得当前时间
				cal_cur.add(Calendar.DAY_OF_YEAR, Integer.parseInt(prdate) );
				SimpleDateFormat dfDate_cur=new SimpleDateFormat("yyyyMMdd");
				String sdate=dfDate_cur.format(cal_cur.getTime());
				return sdate;

			}
			else
				return "";
		}
		else
			return "";
	}




	private String getPorderNO(DCP_SaleOrderToPReq req) throws Exception {

		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：YHSQ
		 */
		String sql = null;
		String porderNO = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String oShopId = req.getoShopId();
		String organizationNO = req.getoShopId();
		String eId = req.getoEId();
		String bDate = PosPub.getAccountDate_SMS(dao, eId, oShopId);
		String[] conditionValues = { organizationNO, eId, oShopId }; // 查询要货单号
		sqlbuf.append(""
				+ "select PorderNO  from ( "
				+ "select max(PorderNO) as  PorderNO "
				+ "  from DCP_Porder " + " where OrganizationNO = ? " + " and EID = ? " + " and SHOPID = ? "
				+ " and PorderNO like '%%" + bDate + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();

		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		if (getQData != null && getQData.isEmpty() == false) {

			porderNO = (String) getQData.get(0).get("PORDERNO");

			if (porderNO != null && porderNO.length() > 0) {
				long i;
				porderNO = porderNO.substring(4, porderNO.length());
				i = Long.parseLong(porderNO) + 1;
				porderNO = i + "";
				porderNO = "YHSQ" + porderNO;

			} else {
				porderNO = "YHSQ" + bDate + "00001";
			}
		} else {
			porderNO = "YHSQ" + bDate + "00001";
		}

		return porderNO;
	}

	protected String getQuerySql_getGuid(String guid) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(""
				+ "select PORDER_ID "
				+ " from DCP_Porder "
				+ " where PORDER_ID = '"+guid+"' "
		);

		if (sqlbuf.length() > 0)
			sql = sqlbuf.toString();

		return sql;
	}

	private boolean checkGuid(DCP_SaleOrderToPReq req) throws Exception {
		String sql = null;
		String guid = req.getPorderID();
		boolean existGuid;

		sql = this.getQuerySql_getGuid(guid);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}

		return existGuid;
	}

	protected boolean CheckData(DCP_SaleOrderToPReq req ,StringBuffer ISUrgentOrder) throws Exception {
		String sql=null;
		String eId = req.getoEId();
		String OrganizationNO= req.getoShopId();

		String oShopId=req.getoShopId();

		String rdate = req.getrDate();
		String rtime = req.getrTime();

		String ptemplateNO = req.getpTemplateNO();
		String preDemandDate = "";//要货默认需求日期
		int preDemandDates ;//要货默认需求日期
		Calendar tempcalPre = Calendar.getInstance();//获得当前时间
		SimpleDateFormat tempdfPre=new SimpleDateFormat("yyyyMMdd");

		String sqlDef = this.getQuerySqlDef(req);	//
		String[] conditionpreDemandDate = { "Pre_Demand_Days", oShopId, eId }; //查詢條件
		List<Map<String, Object>> getQDatapreDemandDate = this.doQueryData(sqlDef, conditionpreDemandDate);
		if (getQDatapreDemandDate != null && getQDatapreDemandDate.isEmpty() == false)
		{
			preDemandDate = (String) getQDatapreDemandDate.get(0).get("ITEMVALUE");
			preDemandDates  = Integer.valueOf(preDemandDate);

			tempcalPre.add(Calendar.DATE, preDemandDates);
			preDemandDate = tempdfPre.format(tempcalPre.getTime());
		}
		else
		{
			sqlDef = this.getQuerySqlDef(req);
			String[] conditionpreDemandDatea = {"Pre_Demand_Days", eId}; //查詢條件
			List<Map<String, Object>> getQDatapreDemandDatea = this.doQueryData(sqlDef, conditionpreDemandDatea);
			if (getQDatapreDemandDatea != null && getQDatapreDemandDatea.isEmpty() == false)
			{
				preDemandDate = (String) getQDatapreDemandDatea.get(0).get("DEF");
				preDemandDates  = Integer.valueOf(preDemandDate);
				//
				tempcalPre.add(Calendar.DATE, preDemandDates);
				preDemandDate = tempdfPre.format(tempcalPre.getTime());
			}
			else
			{
				tempcalPre.add(Calendar.DATE, 0);
				preDemandDate = tempdfPre.format(tempcalPre.getTime());
				//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置Pre_Demand_Days参数");
			}
		}

		sql="select PRE_DAY,OPTIONAL_TIME from DCP_ptemplate where ptemplateno='"+ptemplateNO+"' and EID='"+eId+"'";
		List<Map<String, Object>> getQData = this.doQueryData(sql,null);
		if(getQData != null && getQData.isEmpty() == false)
		{
			String prdate=getQData.get(0).get("PRE_DAY").toString();
			String prtime=getQData.get(0).get("OPTIONAL_TIME").toString();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate=new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime=new SimpleDateFormat("HHmmss");
			String submitDate = dfDate.format(cal.getTime());
			String submitTime = dfTime.format(cal.getTime());
			if(prdate==null||prdate.equals(""))
			{
				prdate= "0";	 //默认当前日期     				
			}
			if(prtime==null||prtime.equals(""))
			{
				prtime= "235959";
			}

			int rdate_i=Integer.parseInt(rdate);
			int rtime_i=Integer.parseInt(rtime);

			int prdate_i=Integer.parseInt(prdate);
			int prtime_i=Integer.parseInt(prtime);

			int curdate_i=Integer.parseInt(submitDate);
			int curtime_i=Integer.parseInt(submitTime);

			//赋值提示的日期
			Calendar cal_cur = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate_cur=new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime_cur=new SimpleDateFormat("HHmmss");
			cal_cur.add(Calendar.DATE, prdate_i);
			mDate=dfDate_cur.format(cal_cur.getTime());
			if(prtime.length()==6)
			{
				String hh=prtime.substring(0,2);
				String mm=prtime.substring(2,4);
				String ss=prtime.substring(4,6);
				mTime=hh+":"+mm+":"+ss;
			}
			else
			{
				mTime="23:59:59";
			}

			//需求日期-采购前置期>当前日期 ==》允许
			//这里计算日期差rdate 单据上的需求日期    submitDate 当前的日期
			SimpleDateFormat difsimDate=new SimpleDateFormat("yyyyMMdd");
			java.util.Date date1=difsimDate.parse(submitDate);
			java.util.Date date2=difsimDate.parse(rdate);

			int difdate=PosPub.differentDaysByMillisecond(date1, date2);

			//if(rdate_i-curdate_i>prdate_i)        
			if(difdate>prdate_i)
			{
				return true;
			}
			//需求日期-采购前置期=当前日期且当前时间<=订货截止时间 ==》允许
			//if(rdate_i-curdate_i==prdate_i)
			if(difdate==prdate_i)
			{
				if(curtime_i<=prtime_i)
				{
					mDate = dfDate_cur.format(cal_cur.getTime());
					return true;
				}
				else
				{
					//赋值提示的日期
					cal_cur.add(Calendar.DATE, 1);
					mDate = dfDate_cur.format(cal_cur.getTime());
				}
			}
			if(difdate>=0)
			{
				if(CheckUrgentOrder(req.getO_opNo(),req.getoEId()))
				{
					ISUrgentOrder.delete(0, 1);
					ISUrgentOrder.append("Y");
					return true;
				}
			}
			//其他不允许
			return false;
		}
		else
		{
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate=new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime=new SimpleDateFormat("HHmmss");
			String submitDate = dfDate.format(cal.getTime());
			String submitTime = dfTime.format(cal.getTime());

			//需求日期-采购前置期>当前日期 ==》允许
			//这里计算日期差rdate 单据上的需求日期    submitDate 当前的日期
			SimpleDateFormat difsimDate=new SimpleDateFormat("yyyyMMdd");
			java.util.Date date1=difsimDate.parse(submitDate);
			java.util.Date date2=difsimDate.parse(preDemandDate);
			int difdate=PosPub.differentDaysByMillisecond(date1, date2);

			if(difdate>=0)
			{
				if(CheckUrgentOrder(req.getO_opNo(),req.getoEId()))
				{
					ISUrgentOrder.delete(0, 1);
					ISUrgentOrder.append("Y");
				}
				return true;
			}
			else{
				//赋值提示的日期
				mDate=preDemandDate;
				return false;
			}
		}
	}

	protected boolean CheckUrgentOrder(String opno,String eId) throws Exception
	{
		//查询是否有紧急加单的权限
		String sUrgentOrder= PosPub.getQueryModularFunctionSql(opno, eId, "30208", "UrgentOrder");
		List<Map<String, Object>> getQData = this.doQueryData(sUrgentOrder, null);
		if(getQData != null && getQData.isEmpty() == false)
		{
			return true;
		}
		return false;
	}

	protected String getQuerySqlDef(DCP_SaleOrderToPReq req) throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select DEF from Platform_BaseSetTemp "
				+ " where Item=? and EID=?"
		);
		sql = sqlbuf.toString();
		return sql;
	}

	/**
	 * 验证该订单是否已要货
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getQuerySqlPStatus(DCP_SaleOrderToPReq req) throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(""
				+ " SELECT '2' AS checkType , porderNo AS orderNo , '1' AS pStatus  FROM DCP_porder WHERE EID = '"+req.getoEId()+"' and SHOPID = '"+req.getoShopId()+"' AND ofNo = '"+req.getOFNO()+"' "
		);
		sql = sqlbuf.toString();
		return sql;
	}



}
