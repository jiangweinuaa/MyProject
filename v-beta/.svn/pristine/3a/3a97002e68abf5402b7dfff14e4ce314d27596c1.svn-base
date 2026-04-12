package com.dsc.spos.service.imp.json;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.hql.internal.ast.SqlASTFactory;
import org.hibernate.id.uuid.Helper;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.cust.req.DCP_OrderStatementQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatementQueryRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

/**
 * 服务OrderStatementGet ,对账查询
 * @author 08546
 *
 */
public class DCP_OrderStatementQuery extends SPosBasicService<DCP_OrderStatementQueryReq, DCP_OrderStatementQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderStatementQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");

		if (Check.Null(req.getRequest().getStartDate())) {
			errMsg.append("开始日期startDate不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getEndDate())) {
			errMsg.append("结束日期endDate不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderStatementQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderStatementQueryReq>(){};
	}

	@Override
	protected DCP_OrderStatementQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderStatementQueryRes();
	}

	@Override
	protected DCP_OrderStatementQueryRes processJson(DCP_OrderStatementQueryReq req) throws Exception 
	{			
		// TODO Auto-generated method stub
		if(req.getRequest().getThirdType().equals("10")||req.getRequest().getThirdType().equals("11"))
		{
			return processJson_WeChat(req);
		}

		try
		{
			DecimalFormat df = new DecimalFormat("0.00");
			DCP_OrderStatementQueryRes res= this.getResponse();
			List<DCP_OrderStatementQueryRes.Data> datas=new ArrayList<DCP_OrderStatementQueryRes.Data>();
			String conditionSql=getQuerySql(req);
			String countSql =getCountSql(req, conditionSql);
			List<Map<String, Object>> countList=this.doQueryData(countSql, null);
			Number num = (Number) countList.get(0).get("COUNTNO");
			int count = num.intValue();
			// startPage 页码
			int startPage = req.getPageNumber();
			// PgSize 每页数据笔数
			int pgSize = req.getPageSize();
			if(count>0){

				int newScale=2;
				RoundingMode rm=RoundingMode.HALF_UP;
				//計算起啟位置
				int startRow = ((startPage - 1) * pgSize);
				startRow = ((startPage - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
				startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
				int endRow=startRow+pgSize;

				String mainSql=getMainSql(req,conditionSql, startRow, endRow);
				List<Map<String, Object>> returnList = this.doQueryData(mainSql.toString(), null);
				for(Map<String, Object> returnMap:returnList){
					//对账结果
					String accountStatus=req.getRequest().getAccountStatus();
					//1-未对账；2-已对账
					if(accountStatus!=null&&!accountStatus.trim().isEmpty()&&accountStatus.equals("2")){

					}else{
						//1-正向；2-负向
						String orderType=returnMap.get("ORDERTYPE").toString();

						//第三方结算金额
						BigDecimal settlementAmtBig=new BigDecimal(0);
						Object settlementAmt=returnMap.get("THIRDSETTLEMENTAMT");
						if(settlementAmt!=null&&!settlementAmt.toString().trim().isEmpty()){
							settlementAmtBig=new BigDecimal(settlementAmt.toString().trim()).setScale(newScale, rm);
						}

						//订单中心
						Object oOrderNo=returnMap.get("OORDERNO");
						BigDecimal oIncomeAmtBig=new BigDecimal(0);
						//订单中心对应单据存在
						Boolean type1=false;

						//正向订单时，取值
						//订单中心结算金额		ORDERSETTLEMENTAMT
						//OC_ORDER.INCOMEAMT
						Object oIncomeAmt=returnMap.get("OINCOMEAMT");
						if(oOrderNo!=null&&!oOrderNo.toString().trim().isEmpty()){
							type1=true;
							if(oIncomeAmt!=null&&!oIncomeAmt.toString().trim().isEmpty()){
								oIncomeAmtBig=new BigDecimal(oIncomeAmt.toString().trim());
							}
							//2-负向订单时，取值OC_ORDER.PARTREFUNDAMT，若为0，则表示全部退，取OC_ORDER.INCOMEAMT*-1
							if("2".equals(orderType)){
								//OC_ORDER的PARTREFUNDAMT-部分退款金额大于0
								Object opartRefundAmt=returnMap.get("OPARTREFUNDAMT");
								if(opartRefundAmt!=null&&!opartRefundAmt.toString().trim().isEmpty()&&new BigDecimal(opartRefundAmt.toString().trim()).compareTo(BigDecimal.ZERO)>0){
									oIncomeAmtBig=new BigDecimal(opartRefundAmt.toString().trim()).multiply(new BigDecimal(-1));
								}else{
									oIncomeAmtBig=oIncomeAmtBig.multiply(new BigDecimal(-1));
								}
							}
							oIncomeAmtBig=oIncomeAmtBig.setScale(newScale, rm);
							oIncomeAmt=oIncomeAmtBig;
						}
						returnMap.put("ORDERSETTLEMENTAMT", oIncomeAmt);

						//POS
						Object sSaleNo=returnMap.get("SSALENO");
						BigDecimal sPayAmtBig=new BigDecimal(0);
						Boolean type2=false;
						//正向订单时取值
						//POS结算金额		POSSETTLEMENTAMT
						//DCP_SALE.SHOPINCOME
						Object sPayAmt=returnMap.get("SPAY_AMT");
						if(sSaleNo!=null&&!sSaleNo.toString().trim().isEmpty()){
							type2=true;
							if(sPayAmt!=null&&!sPayAmt.toString().trim().isEmpty()){
								sPayAmtBig=new BigDecimal(sPayAmt.toString().trim());
							}
							//2-负向订单时，取值 DCP_SALE.TOT_AMT
							if("2".equals(orderType)){
								Object sTotAmt=returnMap.get("STOT_AMT");
								if(sTotAmt!=null&&!sTotAmt.toString().trim().isEmpty()&&new BigDecimal(sTotAmt.toString().trim()).compareTo(BigDecimal.ZERO)>0){
									sPayAmtBig=new BigDecimal(sTotAmt.toString().trim()).multiply(new BigDecimal(-1));
								}
							}
							sPayAmtBig=sPayAmtBig.setScale(newScale, rm);
							sPayAmt=sPayAmtBig;
						}
						returnMap.put("POSSETTLEMENTAMT", sPayAmt);


						//DIVERSITYTYPE1 外卖和订单中心 1-无差异 2-订单中心无 3-金额不匹配
						if(type1){
							if(settlementAmtBig.compareTo(oIncomeAmtBig)==0){
								returnMap.put("DIVERSITYTYPE1", "1");
							}else{
								returnMap.put("DIVERSITYTYPE1", "3");
							}
							returnMap.put("DIVERSITYAMT1", df.format(settlementAmtBig.subtract(oIncomeAmtBig)));
						}else{
							returnMap.put("DIVERSITYTYPE1", "2");
						}


						//订单中心和POS 1-无差异 2-全部无 3-订单中心无 4-POS无 5-金额不匹配
						if(type1&&type2){
							if(oIncomeAmtBig.compareTo(sPayAmtBig)==0){
								returnMap.put("DIVERSITYTYPE2", "1");
							}else{
								returnMap.put("DIVERSITYTYPE2", "5");
							}
							returnMap.put("DIVERSITYAMT2", df.format(oIncomeAmtBig.subtract(sPayAmtBig)));
						}else{
							if(!type1&&!type2){
								returnMap.put("DIVERSITYTYPE2", "2");
							}else if(!type1){
								returnMap.put("DIVERSITYTYPE2", "3");
							}else{
								returnMap.put("DIVERSITYTYPE2", "4");
							}
						}

						//						BigDecimal thirdRefundAmt=new BigDecimal(0);
						//						Object thirdRefundAmtObj=returnMap.get("STTHIRDSETTLEMENTAMT");
						//						if(thirdRefundAmtObj!=null&&!thirdRefundAmtObj.toString().trim().isEmpty()){
						//							thirdRefundAmt=new BigDecimal(thirdRefundAmtObj.toString().trim());
						//						}
						//						returnMap.put("THIRDREFUNDAMT", df.format(thirdRefundAmt));
					}
					DCP_OrderStatementQueryRes.Data data=res.new Data();
					data=map2Bean(returnMap, data.getClass());
					datas.add(data);
				}
			}
			res.setTotalPages(count%pgSize== 0 ? count / pgSize : count / pgSize + 1);
			res.setTotalRecords(count);
			res.setPageNumber(startPage);
			res.setPageSize(pgSize);
			res.setDatas(datas);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			return res;
		}catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected String getQuerySql(DCP_OrderStatementQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append(" AND T.EID ='" + req.geteId() + "' ");
		//对账类型
		String thirdType = req.getRequest().getThirdType();
		if(thirdType!=null&&!thirdType.trim().isEmpty()&&!thirdType.equals("ALL")){
			sqlbuf.append(" AND T.THIRDTYPE ='" + thirdType + "' ");
		}
		//开始日期
		String startDate=req.getRequest().getStartDate();
		if(startDate!=null&&!startDate.trim().isEmpty()){
			sqlbuf.append(" AND T.THIRDSTATEMENTDATE >='" + startDate + "' ");
		}
		//结束日期
		String endDate=req.getRequest().getEndDate();
		if(endDate!=null&&!endDate.trim().isEmpty()){
			sqlbuf.append(" AND T.THIRDSTATEMENTDATE <='" + endDate + "' ");
		}
		//门店
		String shopId=req.getShopId();
		if(shopId!=null&&!shopId.trim().isEmpty()){
			sqlbuf.append(" AND T.SHOPID ='" + shopId + "' ");
		}
		//关键字
		String keyTxt=req.getRequest().getKeyTxt();
		if(keyTxt!=null&&!keyTxt.trim().isEmpty()){
			sqlbuf.append(" AND (UPPER(T.ORDERNO) like UPPER('%%"+keyTxt+"%%'))");
		}

		if(thirdType.equals("10")||thirdType.equals("11"))//微信支付宝对账没有差异类型1
		{

		}
		else
		{
			//差异类型1
			String diversityType1 = req.getRequest().getDiversityType1();
			if(diversityType1!=null&&!diversityType1.trim().isEmpty()&&!diversityType1.equals("ALL")){
				sqlbuf.append(" AND T.DIVERSITYTYPE1 ='" + diversityType1 + "' ");
			}
		}

		//差异类型2
		String diversityType2 = req.getRequest().getDiversityType2();
		if(diversityType2!=null&&!diversityType2.trim().isEmpty()&&!diversityType2.equals("ALL")){
			sqlbuf.append(" AND T.DIVERSITYTYPE2 ='" + diversityType2 + "' ");
		}
		//对账结果
		String accountStatus=req.getRequest().getAccountStatus();
		if(accountStatus!=null&&!accountStatus.trim().isEmpty()&&!accountStatus.equals("ALL")){
			sqlbuf.append(" AND T.ACCOUNTSTATUS ='" + accountStatus + "' ");
		}

		return sqlbuf.toString();
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	public String getCountSql(DCP_OrderStatementQueryReq req,String conditionSql)throws Exception {
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT count(1) as COUNTNO FROM OC_STATEMENT T WHERE 1=1 ");
		sqlbuf.append(conditionSql);
		return sqlbuf.toString();

	}

	public String getMainSql(DCP_OrderStatementQueryReq req,String conditionSql,int startRow,int endRow)throws Exception {
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT * FROM ");
		sqlbuf.append(" ( ");
		//对账结果
		String accountStatus=req.getRequest().getAccountStatus();
		//1-未对账；2-已对账
		if(accountStatus!=null&&!accountStatus.trim().isEmpty()&&accountStatus.equals("2")){
			sqlbuf.append(" SELECT ROWNUM AS RN, T.* FROM OC_STATEMENT T ");
		}else{
			SimpleDateFormat sDF1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sDF2 = new SimpleDateFormat("yyyy-MM-dd");
			String startDate=sDF1.format(sDF2.parse(req.getRequest().getStartDate()));
			String endDate=sDF1.format(sDF2.parse(req.getRequest().getEndDate()));

			sqlbuf.append(" SELECT ROWNUM AS RN, T.* ,O.ORDERNO AS OORDERNO,O.INCOMEAMT AS OINCOMEAMT,O.PARTREFUNDAMT AS OPARTREFUNDAMT, ");
			sqlbuf.append(" S.PAY_AMT AS SPAY_AMT,S.SALENO AS SSALENO,S.TOT_AMT AS STOT_AMT ");
			sqlbuf.append(" FROM OC_STATEMENT T ");
			sqlbuf.append(" LEFT JOIN OC_ORDER O ON T.EID=O.EID AND T.ORDERNO=O.ORDERNO ");
			sqlbuf.append(" LEFT JOIN (SELECT EID,ORDER_ID,SHOPID,PAY_AMT,SALENO,TOT_AMT,(CASE TYPE WHEN 0 THEN '1' ELSE '2' END) AS NEWTYPE,TAKEAWAY FROM DCP_SALE "
					+ "WHERE EID = '"+req.geteId()+"' AND SDATE>='"+startDate+"' AND SDATE<='"+endDate+"' ) S ");
			sqlbuf.append("  ON T.EID = S.EID AND T.ORDERNO = S.ORDER_ID AND T.SHOPID = S.SHOPID AND T.THIRDTYPE=S.TAKEAWAY AND T.ORDERTYPE=S.NEWTYPE ");

			//			sqlbuf.append(" LEFT JOIN ( ");
			//			sqlbuf.append("SELECT * FROM ");
			//			sqlbuf.append(" (SELECT ROWNUM AS RN, T.* FROM OC_STATEMENT T ");
			//	    	sqlbuf.append(" WHERE T.ORDERTYPE='2' ");
			//	    	sqlbuf.append(" AND ROWNUM <="+endRow+" ");
			//	    	sqlbuf.append(conditionSql);
			//	    	sqlbuf.append(" ) TA ");
			//	    	sqlbuf.append(" WHERE TA.RN > "+startRow);
			//	    	sqlbuf.append(" ) ST ON T.ORDERNO=ST.ORDERNO");
		}
		sqlbuf.append(" WHERE 1=1 ");
		sqlbuf.append(conditionSql);
		sqlbuf.append(" AND ROWNUM <="+endRow+" ");
		sqlbuf.append(" ) TA ");
		sqlbuf.append(" WHERE TA.RN > "+startRow);
		return sqlbuf.toString();
	}

	public static <T> T map2Bean(Map<String, Object> source, Class<T> instance) {
		try {
			T object = instance.newInstance();
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if(field.getType() == List.class){
					field.set(object, null);
				}else{
					field.set(object, source.get(field.getName().toUpperCase())==null?null:source.get(field.getName().toUpperCase()).toString());
				}
			}
			return object;
		} catch (InstantiationException | IllegalAccessException e) {

		}
		return null;
	}


	protected DCP_OrderStatementQueryRes processJson_WeChat(DCP_OrderStatementQueryReq req) throws Exception 
	{	
		try
		{
			//对账结果
			String accountStatus=req.getRequest().getAccountStatus();

			DecimalFormat df = new DecimalFormat("0.00");
			DCP_OrderStatementQueryRes res= this.getResponse();
			List<DCP_OrderStatementQueryRes.Data> datas=new ArrayList<DCP_OrderStatementQueryRes.Data>();
			String conditionSql=getQuerySql(req);
			String countSql =getCountSql(req, conditionSql);
			List<Map<String, Object>> countList=this.doQueryData(countSql, null);
			Number num = (Number) countList.get(0).get("COUNTNO");
			int count = num.intValue();
			// startPage 页码
			int startPage = req.getPageNumber();
			// PgSize 每页数据笔数
			int pgSize = req.getPageSize();
			if(count>0){

				int newScale=2;
				RoundingMode rm=RoundingMode.HALF_UP;

				//记录pos结账前就撤销的付款交易记录号，
				List<String> repeatOrderNoList = new ArrayList<String>();
				//1-未对账；2-已对账
				if(accountStatus!=null&&!accountStatus.trim().isEmpty()&&accountStatus.equals("2"))
				{

				}
				else
				{
					//查询下在pos结账前就撤销的付款交易记录号，
					repeatOrderNoList = getRepeatOrderNo(req, conditionSql);
				}


				List<Map<String, Object>> wechatJsPayList = null;

				if(req.getRequest().getThirdType()!=null&&req.getRequest().getThirdType().equals("10"))
				{
					//已经对账完成的，不需要查询CRM库pay_list
					if(req.getRequest().getAccountStatus()==null||req.getRequest().getAccountStatus().equals("2")==false)
					{
						wechatJsPayList = getCRMWechatJsPay(req);
					}

				}

				//計算起啟位置
				int startRow = ((startPage - 1) * pgSize);
				startRow = ((startPage - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
				startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
				int endRow=startRow+pgSize;

				String mainSql=getWeChatSql(req,conditionSql, startRow, endRow);
				List<Map<String, Object>> returnList = this.doQueryData(mainSql.toString(), null);
				for(Map<String, Object> returnMap:returnList)
				{

					//1-未对账；2-已对账
					if(accountStatus!=null&&!accountStatus.trim().isEmpty()&&accountStatus.equals("2"))
					{

					}
					else
					{
						//1-正向；2-负向
						String orderType=returnMap.get("ORDERTYPE").toString();

						String typeName =  returnMap.get("TYPENAME").toString();

						String orderNo = returnMap.get("ORDERNO").toString();

						//第三方订单金额 
						BigDecimal orderAmtBig=new BigDecimal(0);
						String orderAmt=returnMap.get("THIRDORDERAMT").toString().trim();
						if(orderAmt!=null&&!orderAmt.isEmpty()){
							orderAmtBig=new BigDecimal(orderAmt).setScale(newScale, rm);
						}



						//商户交易号  订单中心 为空
						String oOrderNo=returnMap.get("OORDERNO").toString();

						BigDecimal oIncomeAmtBig=new BigDecimal(0);
						//订单中心 对应单据不存在
						Boolean type1=false;
						//订单中心 没有金额 默认 0
						String oIncomeAmt=returnMap.get("OINCOMEAMT").toString();

						returnMap.put("ORDERSETTLEMENTAMT", oIncomeAmt);

						//POS 销售单号
						String sSaleNo=returnMap.get("SSALENO").toString();

						returnMap.put("POSSALENO", sSaleNo);
						//POS 商户交易号
						String refNo = returnMap.get("REFNO").toString();

						BigDecimal sPayAmtBig=new BigDecimal(0);
						Boolean type2=false;
						//正向订单时取值
						//POS结算金额		POSSETTLEMENTAMT				
						String sPayAmt=returnMap.get("SPAY_AMT").toString().trim();

						if(sPayAmt==null||sPayAmt.isEmpty())
						{
							sPayAmt = "0";
						}

						if(refNo!=null&&!refNo.toString().trim().isEmpty())
						{
							type2=true;
							if(sPayAmt!=null&&!sPayAmt.isEmpty())
							{							
								sPayAmtBig=new BigDecimal(sPayAmt);
							}

							sPayAmtBig=sPayAmtBig.setScale(newScale, rm);

						}
						returnMap.put("POSSETTLEMENTAMT", sPayAmt);


						//DIVERSITYTYPE1 第三方和订单中心 1-无差异 2-订单中心无 3-金额不匹配
						if(type1){
							if(orderAmtBig.compareTo(oIncomeAmtBig)==0){
								returnMap.put("DIVERSITYTYPE1", "1");
							}else{
								returnMap.put("DIVERSITYTYPE1", "3");
							}
							returnMap.put("DIVERSITYAMT1", df.format(orderAmtBig.subtract(oIncomeAmtBig)));
						}else{
							returnMap.put("DIVERSITYTYPE1", "2");
						}

						//第三方 和 pos比较 1-无差异 4-pos无 5-金额不匹配
						if(type2)
						{
							if(orderAmtBig.compareTo(sPayAmtBig)==0)
							{
								returnMap.put("DIVERSITYTYPE2", "1");
							}
							else
							{
								returnMap.put("DIVERSITYTYPE2", "5");
							}
							returnMap.put("DIVERSITYAMT2", df.format(orderAmtBig.subtract(sPayAmtBig)));
						}
						else
						{
							//pos没有记录交易号，可能是没结账前就撤销了，所以也是正常的无差异的					

							if(repeatOrderNoList!=null&&repeatOrderNoList.size()>0&&repeatOrderNoList.contains(orderNo))
							{
								returnMap.put("DIVERSITYTYPE2", "1");
								returnMap.put("DIVERSITYAMT2", df.format(0));
							}
							else
							{
								returnMap.put("DIVERSITYTYPE2", "4");
								//差异金额		DIVERSITYAMT2
								returnMap.put("DIVERSITYAMT2", df.format(orderAmtBig.subtract(sPayAmtBig)));
							}
							//returnMap.put("DIVERSITYTYPE2", "4");

							//处理下微信的JSAPI 
							if(req.getRequest().getThirdType().equals("10")&&wechatJsPayList!=null&&wechatJsPayList.isEmpty()==false)
							{
								if(typeName!=null&&typeName.toUpperCase().equals("JSAPI"))
								{
									HelpTools.writelog_waimaiException("typeName=JSAPI");
									Boolean type_crm=false;//crm数据库是否存在
									for (Map<String, Object> map_jspay : wechatJsPayList) 
									{
										if(orderNo!=null&&orderNo.equals(map_jspay.get("TRADENO").toString()))
										{
											HelpTools.writelog_waimaiException("orderNo=TRADENO");
											type_crm = true;
											String AMOUNT = map_jspay.get("AMOUNT").toString();
											if(AMOUNT==null||AMOUNT.isEmpty())
											{
												AMOUNT = "0";
											}

											String REFUNDAMOUNT = map_jspay.get("REFUNDAMOUNT").toString();
											if(REFUNDAMOUNT==null||REFUNDAMOUNT.isEmpty())
											{
												REFUNDAMOUNT = "0";
											}			

											BigDecimal sPayAmtBig_CRM=new BigDecimal(0);

											if(orderType.equals("1"))//正向
											{									
												sPayAmtBig_CRM = new BigDecimal(AMOUNT);
											}
											else
											{
												sPayAmtBig_CRM = new BigDecimal(REFUNDAMOUNT);//这里是正数
												sPayAmtBig_CRM = sPayAmtBig_CRM.negate();//取相反数。变成负数												
											}	


											if(orderAmtBig.compareTo(sPayAmtBig_CRM)==0)
											{
												returnMap.put("DIVERSITYTYPE2", "1");
											}
											else
											{
												returnMap.put("DIVERSITYTYPE2", "5");
											}
											returnMap.put("DIVERSITYAMT2", df.format(orderAmtBig.subtract(sPayAmtBig_CRM)));
											HelpTools.writelog_waimaiException("orderAmtBig.subtract(sPayAmtBig_CRM)="+df.format(orderAmtBig.subtract(sPayAmtBig_CRM)));										
											break;

										}


									}

								}

							}


						}


					}
					DCP_OrderStatementQueryRes.Data data=res.new Data();
					data=map2Bean(returnMap, data.getClass());
					datas.add(data);
				}
			}
			res.setTotalPages(count%pgSize== 0 ? count / pgSize : count / pgSize + 1);
			res.setTotalRecords(count);
			res.setPageNumber(startPage);
			res.setPageSize(pgSize);
			res.setDatas(datas);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			return res;
		}catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	public String getWeChatSql(DCP_OrderStatementQueryReq req,String conditionSql,int startRow,int endRow) throws Exception
	{
		StringBuffer sqlbuf = new StringBuffer("");
		//对账结果
		String accountStatus=req.getRequest().getAccountStatus();

		if(accountStatus!=null&&!accountStatus.trim().isEmpty()&&accountStatus.equals("2"))
		{
			sqlbuf.append(" select * from ( ");
			sqlbuf.append("SELECT rownum as rn, T.* FROM ");
			sqlbuf.append(" ( ");

			sqlbuf.append(" SELECT  T.* FROM OC_STATEMENT T ");
			sqlbuf.append(" WHERE 1=1 ");
			sqlbuf.append(conditionSql);
			sqlbuf.append(" order by T.thirdcreatetime,T.orderno ");

			sqlbuf.append(" ) T");
			sqlbuf.append(" ) ");
			sqlbuf.append(" where 1=1");
			sqlbuf.append(" AND rn <="+endRow+" ");
			sqlbuf.append(" AND rn >"+startRow+" ");
		}
		else
		{
			String payCode = getPayCode(req);//获取微信支付宝对应的paycode
			String eId = req.geteId();
			SimpleDateFormat sDF1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sDF2 = new SimpleDateFormat("yyyy-MM-dd");
			String startDate=sDF1.format(sDF2.parse(req.getRequest().getStartDate()));
			String endDate=sDF1.format(sDF2.parse(req.getRequest().getEndDate()));

			sqlbuf.append(" select * from ( ");

			sqlbuf.append("SELECT rownum as rn, T.* FROM ");
			sqlbuf.append(" ( ");

			sqlbuf.append(" select T.*,B.Saleno as SSALENO,B.refno,B.pay as SPAY_AMT,'' as OORDERNO,'0' as OINCOMEAMT from OC_statement T ");
			sqlbuf.append(" left join");
			sqlbuf.append(" (");  	
			sqlbuf.append(" select * from (");
			//开始查询 DCP_SALE_pay type=0-销售 其他退单,
			sqlbuf.append(" select A.EID,A.SHOPID,A.saleno,A.PAYSERNUM,A.Refno,A.Paycode,A.payname,(case B.Type when 0 then A.pay else 0-A.pay end) as pay,(case B.Type when 0 then '1' else '2' end) as saletype");
			sqlbuf.append(" from DCP_SALE_pay A inner join DCP_SALE B on  A.EID=B.EID and A.SHOPID=B.SHOPID and A.Saleno=B.saleno");
			sqlbuf.append(" where A.Isorderpay<>'Y' and A.EID='"+eId+"' and A.SDATE>='"+startDate+"' AND A.SDATE<='"+endDate+"' and A.paycode='"+payCode+"'");
			//订单oc_order_pay type=3-订单  4-订单退单
			sqlbuf.append(" union all");
			sqlbuf.append(" select A.EID,A.SHOPID,A.orderno as saleno,A.PAYSERNUM,A.Refno,A.Paycode,A.payname,(case B.Type when 3 then A.pay else 0-A.pay end) as pay,(case B.Type when 3 then '1' else '2' end) as saletype");
			sqlbuf.append(" from oc_order_pay A inner join oc_order B on  A.EID=B.EID and A.SHOPID=B.SHOPID and A.orderno=B.orderno");
			sqlbuf.append(" where  A.EID='"+eId+"' and A.SDATE>='"+startDate+"' AND A.SDATE<='"+endDate+"' and A.paycode='"+payCode+"'");

			//充值表 tg_recharge_pay
			sqlbuf.append(" union all");
			sqlbuf.append(" select A.EID,A.SHOPID,A.Rechargeno as saleno,A.PAYSERNUM,A.Refno,A.Paycode,A.payname,(case B.billtype when 0 then A.pay else 0-A.pay end) as pay,(case B.billtype when 0 then '1' else '2' end) as saletype");
			sqlbuf.append(" from tg_recharge_pay A inner join tg_recharge B on  A.EID=B.EID and A.SHOPID=B.SHOPID and A.Rechargeno=B.Rechargeno");
			sqlbuf.append(" where  A.EID='"+eId+"' and A.SDATE>='"+startDate+"' AND A.SDATE<='"+endDate+"' and A.paycode='"+payCode+"'");

			//售卡表 tg_salecard_pay
			sqlbuf.append(" union all");
			sqlbuf.append(" select A.EID,A.SHOPID,A.Salecardno as saleno,A.PAYSERNUM,A.Refno,A.Paycode,A.payname,(case B.type when 0 then A.pay else 0-A.pay end) as pay,(case B.type when 0 then '1' else '2' end) as saletype");
			sqlbuf.append(" from tg_salecard_pay A inner join tg_salecard B on  A.EID=B.EID and A.SHOPID=B.SHOPID and A.Salecardno=B.Salecardno");
			sqlbuf.append(" where  A.EID='"+eId+"' and A.SDATE>='"+startDate+"' AND A.SDATE<='"+endDate+"' and A.paycode='"+payCode+"'");

			//售券表 tg_salecoupon_pay
			sqlbuf.append(" union all");
			sqlbuf.append(" select A.EID,A.SHOPID,A.Salecouponno as saleno,A.PAYSERNUM,A.Refno,A.Paycode,A.payname,(case B.type when 0 then A.pay else 0-A.pay end) as pay,(case B.type when 0 then '1' else '2' end) as saletype");
			sqlbuf.append(" from tg_salecoupon_pay A inner join tg_salecoupon B on  A.EID=B.EID and A.SHOPID=B.SHOPID and A.Salecouponno=B.Salecouponno");
			sqlbuf.append(" where  A.EID='"+eId+"' and A.SDATE>='"+startDate+"' AND A.SDATE<='"+endDate+"' and A.paycode='"+payCode+"'");

			/*//还有其他的
    	sqlbuf.append(" union all");*/

			sqlbuf.append(" )");

			sqlbuf.append(" ) B on T.EID=B.EID and T.Orderno=B.refno and T.Ordertype=B.saletype ");
			sqlbuf.append(" WHERE 1=1 ");
			sqlbuf.append(conditionSql);  	
			sqlbuf.append(" order by T.thirdcreatetime,T.orderno ");

			sqlbuf.append(" ) T ");
			sqlbuf.append(" )");
			sqlbuf.append(" where 1=1");
			sqlbuf.append(" AND rn <="+endRow+" ");
			sqlbuf.append(" AND rn >"+startRow+" ");

		}
		return sqlbuf.toString();
	}

	public String getPayCode(DCP_OrderStatementQueryReq req) throws Exception
	{
		String thirdType = req.getRequest().getThirdType();
		String eId = req.geteId();
		String funcno = "201";//201-微信，202-支付宝
		if(thirdType.equals("10"))
		{
			funcno = "201";
		}
		else if (thirdType.equals("11")) 
		{
			funcno = "202";
		}
		else 
		{
			funcno ="";

		}
		String payCode ="";
		String sql = "select * FROM DCP_PAYFUNCnoinfo WHERE FUNCNO='"+funcno+"' AND EID='"+eId+"'";

		List<Map<String, Object>> queryData = this.doQueryData(sql, null);
		if(queryData!=null&&queryData.isEmpty()==false)
		{
			payCode = queryData.get(0).get("PAYCODE").toString();
		}

		return payCode;

	}

	/**
	 * 查询重复的交易号&&金额相加=0，在pos结账前就撤销的微信支付宝交易订单号
	 * @param req
	 * @param conditionSql
	 * @return
	 * @throws Exception
	 */
	public List<String> getRepeatOrderNo(DCP_OrderStatementQueryReq req,String conditionSql) throws Exception {

		List<String> orderNoList = new ArrayList<String>();

		StringBuffer sqlbuf = new StringBuffer("");
		try 
		{
			sqlbuf.append(" select orderno from (");

			sqlbuf.append(" select orderno,sum(thirdorderAMT) as AMT from OC_statement where orderno in (");

			sqlbuf.append(" select orderno from OC_statement T WHERE 1=1 ");
			sqlbuf.append(conditionSql);
			sqlbuf.append(" group by orderno having count(orderno)>1 ");

			sqlbuf.append(" ) group by orderno");

			sqlbuf.append(") where AMT=0");

			List<Map<String, Object>> repeatOrderNoList = this.doQueryData(sqlbuf.toString(), null);
			if(repeatOrderNoList!=null&&repeatOrderNoList.isEmpty()==false)
			{
				for (Map<String, Object> map : repeatOrderNoList) 
				{
					orderNoList.add(map.get("ORDERNO").toString());			
				}
			}


		} 
		catch (Exception e) 
		{


		}		
		return orderNoList;
	}

	/**
	 * 查询crm数据库，jsapi支付方式
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getCRMWechatJsPay(DCP_OrderStatementQueryReq req) throws Exception
	{
		try 
		{
			if(req.getRequest().getThirdType()==null||req.getRequest().getThirdType().equals("10")==false)
			{
				return null;
			}

			String startDate=req.getRequest().getStartDate();
			String endDate=req.getRequest().getEndDate();

			String sql = "";//"select * from pay_list";
			StringBuffer sqlbuf = new StringBuffer("");

			sqlbuf.append(" select * from pay_list where  isjspay=1 and paytype='#P1'");//只需要微信对JSAPI的
			sqlbuf.append(" and to_char(createtime, 'YYYY-MM-DD') >='"+startDate+"'");
			sqlbuf.append(" and to_char(createtime, 'YYYY-MM-DD') <='"+endDate+"'");

			sql = sqlbuf.toString();
			HelpTools.writelog_waimaiException("sql语句:"+sql);
			List<Map<String, Object>> maplist =	StaticInfo.dao.executeQuerySQL(sql, null);
			if(maplist!=null)
			{
				HelpTools.writelog_waimaiException("sql语句返回:"+maplist.size());
			}

			return maplist;


		} 
		catch (Exception e) 
		{
			HelpTools.writelog_waimaiException("查询异常："+e.getMessage());

		}

		return null;	  
	}


}
