package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Null;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatementConfirmReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatementConfirmRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfoSet;

/**
 * 服务OrderStatementConfirm ,对账完成
 * @author 08546
 */
public class DCP_OrderStatementConfirm extends SPosAdvanceService<DCP_OrderStatementConfirmReq,DCP_OrderStatementConfirmRes> 
{

	@Override
	protected void processDUID(DCP_OrderStatementConfirmReq req, DCP_OrderStatementConfirmRes res) throws Exception {
		// TODO Auto-generated method stub
		Logger logger = LogManager.getLogger(DCP_OrderStatementConfirm.class.getName());
		if(req.getRequest().getThirdType().equals("10")||req.getRequest().getThirdType().equals("11"))
		{
			processDUID_WeChat(req,res);
			return;
		}
		try{
			DecimalFormat df = new DecimalFormat("0.00");
			String mainSql=getMainSql(req);
			List<Map<String, Object>> returnList = this.doQueryData(mainSql.toString(), null);

			List<String> orderNoList=new ArrayList<String>();
			Date date = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate=sdf2.format(date);
			String nowTime=sdf1.format(date);
			String modifiedBy=req.getOpNO();
			String accountStatusConfirm="2";
			int newScale=2;
			RoundingMode rm=RoundingMode.HALF_UP;
			//列表SQL  需要执行的sql列表
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
			for(Map<String, Object> returnMap:returnList){

				Object accountStatus =returnMap.get("ACCOUNTSTATUS");
				//1-未对账
				if(accountStatus!=null&&accountStatus.toString().trim().equals("1")){
					UptBean ub1 = new UptBean("OC_STATEMENT");
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

					//DIVERSITYTYPE1 外卖和订单中心 1-无差异 2-订单中心无 3-金额不匹配
					if(type1){
						//订单中心结算金额
						ub1.addUpdateValue("ORDERSETTLEMENTAMT", new DataValue(df.format(oIncomeAmtBig), Types.DECIMAL));
						if(settlementAmtBig.compareTo(oIncomeAmtBig)==0){
							returnMap.put("DIVERSITYTYPE1", "1");
						}else{
							returnMap.put("DIVERSITYTYPE1", "3");
						}
						//差异金额		DIVERSITYAMT2
						ub1.addUpdateValue("DIVERSITYAMT1", new DataValue(df.format(settlementAmtBig.subtract(oIncomeAmtBig)), Types.DECIMAL));
					}else{
						returnMap.put("DIVERSITYTYPE1", "2");
					}

					if(type2){
						//POS单据编号
						ub1.addUpdateValue("POSSALENO", new DataValue(sSaleNo, Types.VARCHAR));
						//POS结算金额
						ub1.addUpdateValue("POSSETTLEMENTAMT", new DataValue(df.format(sPayAmtBig), Types.DECIMAL));
					}

					//订单中心和POS 1-无差异 2-全部无 3-订单中心无 4-POS无 5-金额不匹配
					if(type1&&type2){
						if(oIncomeAmtBig.compareTo(sPayAmtBig)==0){
							returnMap.put("DIVERSITYTYPE2", "1");
						}else{
							returnMap.put("DIVERSITYTYPE2", "5");
						}
						//差异金额		DIVERSITYAMT1
						ub1.addUpdateValue("DIVERSITYAMT2", new DataValue(df.format(oIncomeAmtBig.subtract(sPayAmtBig)), Types.DECIMAL));
					}else{
						if(!type1&&!type2){
							returnMap.put("DIVERSITYTYPE2", "2");
						}else if(!type1){
							returnMap.put("DIVERSITYTYPE2", "3");
						}else{
							returnMap.put("DIVERSITYTYPE2", "4");
						}
					}

					//退货金额
					BigDecimal thirdRefundAmtBig=new BigDecimal(0);
					Object stThirdSettlementAmt=returnMap.get("STTHIRDSETTLEMENTAMT");
					if(stThirdSettlementAmt!=null&&!stThirdSettlementAmt.toString().trim().isEmpty()){
						thirdRefundAmtBig=new BigDecimal(stThirdSettlementAmt.toString().trim());
					}
					ub1.addUpdateValue("THIRDREFUNDAMT", new DataValue(df.format(thirdRefundAmtBig), Types.DECIMAL));


					//差异类型		DIVERSITYTYPE1
					ub1.addUpdateValue("DIVERSITYTYPE1", new DataValue(returnMap.get("DIVERSITYTYPE1"), Types.VARCHAR));
					//差异类型		DIVERSITYTYPE2
					ub1.addUpdateValue("DIVERSITYTYPE2", new DataValue(returnMap.get("DIVERSITYTYPE2"), Types.VARCHAR));

					//差异原因		DIVERSITYREASON
					ub1.addUpdateValue("DIVERSITYREASON", new DataValue(returnMap.get("DIVERSITYREASON"), Types.VARCHAR));
					//对账结果		ACCOUNTSTATUS
					//1-未对账；2-已对账
					ub1.addUpdateValue("ACCOUNTSTATUS", new DataValue(accountStatusConfirm, Types.VARCHAR));
					//第三方对账日期		THIRDACCOUNTDATE
					ub1.addUpdateValue("THIRDACCOUNTDATE", new DataValue(nowDate, Types.VARCHAR));
					//资料修改者		DATAMODIFIEDBY
					ub1.addUpdateValue("THIRDACCOUNTDATE", new DataValue(modifiedBy, Types.VARCHAR));
					//最近修改日		LASTMODIFIEDDATE
					ub1.addUpdateValue("LASTMODIFIEDDATE", new DataValue(nowTime, Types.VARCHAR));


					//条件部分
					ub1.addCondition("EID", new DataValue(returnMap.get("EID"), Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(returnMap.get("SHOPID"), Types.VARCHAR));
					ub1.addCondition("THIRDSHOP", new DataValue(returnMap.get("THIRDSHOP"), Types.VARCHAR));
					ub1.addCondition("ORDERNO", new DataValue(returnMap.get("ORDERNO"), Types.VARCHAR));
					ub1.addCondition("ORDERTYPE", new DataValue(returnMap.get("ORDERTYPE"), Types.VARCHAR));
					ub1.addCondition("THIRDTYPE", new DataValue(returnMap.get("THIRDTYPE"), Types.VARCHAR));
					ub1.addCondition("ACCOUNTSTATUS", new DataValue(accountStatus, Types.VARCHAR));

					if("3".equals(returnMap.get("DIVERSITYTYPE1"))||"5".equals(returnMap.get("DIVERSITYTYPE2"))){
						//外卖和订单中心 3-金额不匹配
						//订单中心和POS 5-金额不匹配
						//校验
						Object diversityReason=returnMap.get("DIVERSITYREASON");
						if(diversityReason!=null&&!diversityReason.toString().trim().isEmpty()){
							//							this.dao.update(ub1.getTableName(), ub1.getUpdateValues(), ub1.getConditions());
							lstData.add(new DataProcessBean(ub1));
						}else{
							orderNoList.add(returnMap.get("ORDERNO").toString());
						}
					}else{
						//						this.dao.update(ub1.getTableName(), ub1.getUpdateValues(), ub1.getConditions());
						lstData.add(new DataProcessBean(ub1));
					}
				}else{
					continue;
				}

				//				this.pData.clear();
			}
			if(lstData.size()>0){
				int subSize = 1000;
				int subCount = lstData.size();
				int subPageTotal = (subCount / subSize) + ((subCount % subSize > 0) ? 1 : 0);
				//		        StringBuffer sb=new StringBuffer(); 
				for (int i = 0, len = subPageTotal - 1; i <= len; i++) {
					// 分页计算
					int fromIndex = i * subSize;
					int toIndex = ((i == len) ? subCount : ((i + 1) * subSize));
					List<DataProcessBean> newList = lstData.subList(fromIndex, toIndex);
					saveData(newList);
				}
			}


			String des="";
			if(orderNoList!=null&&orderNoList.size()>0){
				//				des=",单据:"+getStringList2Str(orderNoList)+"";
				des=",部分单据需填写差异原因";
			}
			String description="保存成功"+des;
			
			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription(description);

		}catch(Exception e){
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"对账保存执行失败",e);
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败：" + e.getMessage());
		}


	}

	public void saveData(List<DataProcessBean> lstData) throws Exception{
		//		boolean isSuccess=false;
		try{
			this.dao.useTransactionProcessData(lstData);
		} catch (Exception e) {
		}
		//		return isSuccess;
	}

	public String getMainSql(DCP_OrderStatementConfirmReq req)throws Exception {
		StringBuffer sqlbuf = new StringBuffer("");
		String conditionSql=getQuerySql(req);

		SimpleDateFormat sDF1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sDF2 = new SimpleDateFormat("yyyy-MM-dd");
		String startDate=sDF1.format(sDF2.parse(req.getRequest().getStartDate()));
		String endDate=sDF1.format(sDF2.parse(req.getRequest().getEndDate()));

		sqlbuf.append(" SELECT T.* ,O.ORDERNO AS OORDERNO,O.INCOMEAMT AS OINCOMEAMT,O.PARTREFUNDAMT AS OPARTREFUNDAMT, ");
		sqlbuf.append(" S.PAY_AMT AS SPAY_AMT,S.SALENO AS SSALENO,S.TOT_AMT AS STOT_AMT ");
		sqlbuf.append(" FROM OC_STATEMENT T ");
		sqlbuf.append(" LEFT JOIN OC_ORDER O ON T.EID=O.EID AND T.ORDERNO=O.ORDERNO ");

		sqlbuf.append(" LEFT JOIN (SELECT EID,ORDER_ID,SHOPID,PAY_AMT,SALENO,TOT_AMT,(CASE TYPE WHEN 0 THEN '1' ELSE '2' END) AS NEWTYPE,TAKEAWAY FROM DCP_SALE "
				+ "WHERE EID = '"+req.geteId()+"' AND SDATE>='"+startDate+"' AND SDATE<='"+endDate+"' ) S ");
		sqlbuf.append("  ON T.EID = S.EID AND T.ORDERNO = S.ORDER_ID AND T.SHOPID = S.SHOPID AND T.THIRDTYPE=S.TAKEAWAY AND T.ORDERTYPE=S.NEWTYPE ");

		sqlbuf.append(" WHERE 1=1 ");
		sqlbuf.append(conditionSql);

		return sqlbuf.toString();
	}

	protected void processDUID_WeChat(DCP_OrderStatementConfirmReq req, DCP_OrderStatementConfirmRes res) throws Exception {
		// TODO Auto-generated method stub
		Logger logger = LogManager.getLogger(DCP_OrderStatementConfirm.class.getName());

		try{
			String conditionSql=getQuerySql(req);
			String countSql =getCountSql(req, conditionSql);
			List<Map<String, Object>> countList=this.doQueryData(countSql, null);
			Number num = (Number) countList.get(0).get("COUNTNO");
			int count = num.intValue();

			if(count>50000)//查询的数据
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需要更新的数据太多（"+count+"条)！请重新选择时间范围（最好一天）");
			}

			//查询下在pos结账前就撤销的付款交易记录号，
			List<String> repeatOrderNoList = getRepeatOrderNo(req, conditionSql);


			List<Map<String, Object>> wechatJsPayList = null;

			if(req.getRequest().getThirdType()!=null&&req.getRequest().getThirdType().equals("10"))
			{
				wechatJsPayList = getCRMWechatJsPay(req);
			}

			DecimalFormat df = new DecimalFormat("0.00");
			String mainSql=getWeChatSql(req);
			List<Map<String, Object>> returnList = this.doQueryData(mainSql.toString(), null);

			List<String> orderNoList=new ArrayList<String>();
			Date date = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate=sdf2.format(date);
			String nowTime=sdf1.format(date);
			String modifiedBy=req.getOpNO();
			String accountStatusConfirm="2";
			int newScale=2;
			RoundingMode rm=RoundingMode.HALF_UP;
			//列表SQL  需要执行的sql列表
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
			for(Map<String, Object> returnMap:returnList){

				Object accountStatus =returnMap.get("ACCOUNTSTATUS");
				//1-未对账
				if(accountStatus!=null&&accountStatus.toString().trim().equals("1")){
					UptBean ub1 = new UptBean("OC_STATEMENT");
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

					/*//第三方结算金额
					BigDecimal settlementAmtBig=new BigDecimal(0);
					Object settlementAmt=returnMap.get("THIRDSETTLEMENTAMT");
					if(settlementAmt!=null&&!settlementAmt.toString().trim().isEmpty()){
						settlementAmtBig=new BigDecimal(settlementAmt.toString().trim()).setScale(newScale, rm);
					}*/

					//订单中心
					Object oOrderNo=returnMap.get("OORDERNO");
					BigDecimal oIncomeAmtBig=new BigDecimal(0);
					//订单中心对应单据存在
					Boolean type1=false;

					//正向订单时，取值
					//订单中心结算金额		ORDERSETTLEMENTAMT
					//OC_ORDER.INCOMEAMT
					Object oIncomeAmt=returnMap.get("OINCOMEAMT");

					returnMap.put("ORDERSETTLEMENTAMT", oIncomeAmt);

					//POS
					Object sSaleNo=returnMap.get("SSALENO");
					returnMap.put("POSSALENO", sSaleNo);

					//POS 商户交易号
					String refNo = returnMap.get("REFNO").toString();

					BigDecimal sPayAmtBig=new BigDecimal(0);
					Boolean type2=false;

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

					//DIVERSITYTYPE1 外卖和订单中心 1-无差异 2-订单中心无 3-金额不匹配
					if(type1)
					{
						//订单中心结算金额
						ub1.addUpdateValue("ORDERSETTLEMENTAMT", new DataValue(df.format(oIncomeAmtBig), Types.DECIMAL));
						if(orderAmtBig.compareTo(oIncomeAmtBig)==0){
							returnMap.put("DIVERSITYTYPE1", "1");
						}else{
							returnMap.put("DIVERSITYTYPE1", "3");
						}
						//差异金额		DIVERSITYAMT2
						ub1.addUpdateValue("DIVERSITYAMT1", new DataValue(df.format(orderAmtBig.subtract(oIncomeAmtBig)), Types.DECIMAL));
					}
					else
					{
						returnMap.put("DIVERSITYTYPE1", "2");
					}

					//第三方和POS 1-无差异  4-POS无 5-金额不匹配
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
						//差异金额		DIVERSITYAMT2
						//ub1.addUpdateValue("DIVERSITYAMT2", new DataValue(df.format(orderAmtBig.subtract(sPayAmtBig)), Types.DECIMAL));
						//POS单据编号
						ub1.addUpdateValue("POSSALENO", new DataValue(sSaleNo, Types.VARCHAR));
						//POS结算金额
						ub1.addUpdateValue("POSSETTLEMENTAMT", new DataValue(df.format(sPayAmtBig), Types.DECIMAL));
					}
					else
					{
						//pos没有记录交易号，可能是没结账前就撤销了，所以也是正常的无差异的					
						if(repeatOrderNoList!=null&&repeatOrderNoList.size()>0&&repeatOrderNoList.contains(orderNo))
						{
							returnMap.put("DIVERSITYTYPE2", "1");
							returnMap.put("DIVERSITYAMT2", df.format(0));
							//ub1.addUpdateValue("DIVERSITYAMT2", new DataValue(df.format(0), Types.DECIMAL));
						}
						else
						{
							returnMap.put("DIVERSITYTYPE2", "4");
							//差异金额		DIVERSITYAMT2
							returnMap.put("DIVERSITYAMT2", df.format(orderAmtBig.subtract(sPayAmtBig)));
							//ub1.addUpdateValue("DIVERSITYAMT2", new DataValue(df.format(orderAmtBig.subtract(sPayAmtBig)), Types.DECIMAL));
						}

						//处理下微信的JSAPI 
						if(req.getRequest().getThirdType().equals("10")&&wechatJsPayList!=null&&wechatJsPayList.isEmpty()==false)
						{
							if(typeName!=null&&typeName.toUpperCase().equals("JSAPI"))
							{

								Boolean type_crm=false;//crm数据库是否存在
								for (Map<String, Object> map_jspay : wechatJsPayList) 
								{
									if(orderNo!=null&&orderNo.equals(map_jspay.get("TRADENO").toString()))
									{
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
										//ub1.addUpdateValue("DIVERSITYAMT2", new DataValue(df.format(orderAmtBig.subtract(sPayAmtBig_CRM)), Types.DECIMAL));							
										break;

									}


								}

							}

						}



					}




					/*//退货金额
					BigDecimal thirdRefundAmtBig=new BigDecimal(0);
					Object stThirdSettlementAmt=returnMap.get("STTHIRDSETTLEMENTAMT");
					if(stThirdSettlementAmt!=null&&!stThirdSettlementAmt.toString().trim().isEmpty()){
						thirdRefundAmtBig=new BigDecimal(stThirdSettlementAmt.toString().trim());
					}
					ub1.addUpdateValue("THIRDREFUNDAMT", new DataValue(df.format(thirdRefundAmtBig), Types.DECIMAL));
					 */

					//差异类型		DIVERSITYTYPE1
					ub1.addUpdateValue("DIVERSITYTYPE1", new DataValue(returnMap.get("DIVERSITYTYPE1"), Types.VARCHAR));
					//差异类型		DIVERSITYTYPE2
					ub1.addUpdateValue("DIVERSITYTYPE2", new DataValue(returnMap.get("DIVERSITYTYPE2"), Types.VARCHAR));

					ub1.addUpdateValue("DIVERSITYAMT2", new DataValue(returnMap.get("DIVERSITYAMT2"), Types.DECIMAL));

					//差异原因		DIVERSITYREASON
					ub1.addUpdateValue("DIVERSITYREASON", new DataValue(returnMap.get("DIVERSITYREASON"), Types.VARCHAR));
					//对账结果		ACCOUNTSTATUS
					//1-未对账；2-已对账
					ub1.addUpdateValue("ACCOUNTSTATUS", new DataValue(accountStatusConfirm, Types.VARCHAR));
					//第三方对账日期		THIRDACCOUNTDATE
					//ub1.addUpdateValue("THIRDACCOUNTDATE", new DataValue(nowDate, Types.VARCHAR));
					//资料修改者		DATAMODIFIEDBY
					ub1.addUpdateValue("DATAMODIFIEDBY", new DataValue(modifiedBy, Types.VARCHAR));
					//最近修改日		LASTMODIFIEDDATE
					ub1.addUpdateValue("LASTMODIFIEDDATE", new DataValue(nowTime, Types.VARCHAR));


					//条件部分
					ub1.addCondition("EID", new DataValue(returnMap.get("EID"), Types.VARCHAR));
					ub1.addCondition("SHOPID", new DataValue(returnMap.get("SHOPID"), Types.VARCHAR));
					//ub1.addCondition("THIRDSHOP", new DataValue(returnMap.get("THIRDSHOP"), Types.VARCHAR));
					if(returnMap.get("THIRDSHOP")!=null&&returnMap.get("THIRDSHOP").toString().isEmpty()==false)
					{
						ub1.addCondition("THIRDSHOP", new DataValue(returnMap.get("THIRDSHOP"), Types.VARCHAR));
					}				
					ub1.addCondition("ORDERNO", new DataValue(returnMap.get("ORDERNO"), Types.VARCHAR));
					ub1.addCondition("ORDERTYPE", new DataValue(returnMap.get("ORDERTYPE"), Types.VARCHAR));
					ub1.addCondition("THIRDTYPE", new DataValue(returnMap.get("THIRDTYPE"), Types.VARCHAR));
					ub1.addCondition("ACCOUNTSTATUS", new DataValue(accountStatus, Types.VARCHAR));

					lstData.add(new DataProcessBean(ub1));

					if("5".equals(returnMap.get("DIVERSITYTYPE2")))
					{
						//外卖和订单中心 3-金额不匹配
						//订单中心和POS 5-金额不匹配
						//校验
						Object diversityReason=returnMap.get("DIVERSITYREASON");
						if(diversityReason!=null&&!diversityReason.toString().trim().isEmpty())
						{
							//							this.dao.update(ub1.getTableName(), ub1.getUpdateValues(), ub1.getConditions());
							//lstData.add(new DataProcessBean(ub1));
						}
						else
						{
							orderNoList.add(returnMap.get("ORDERNO").toString());
						}
					}
					else
					{
						//						this.dao.update(ub1.getTableName(), ub1.getUpdateValues(), ub1.getConditions());
						//lstData.add(new DataProcessBean(ub1));
					}
				}
				else
				{
					continue;
				}

				//				this.pData.clear();
			}
			if(lstData.size()>0){
				int subSize = 1000;
				int subCount = lstData.size();
				int subPageTotal = (subCount / subSize) + ((subCount % subSize > 0) ? 1 : 0);
				//		        StringBuffer sb=new StringBuffer(); 
				for (int i = 0, len = subPageTotal - 1; i <= len; i++) {
					// 分页计算
					int fromIndex = i * subSize;
					int toIndex = ((i == len) ? subCount : ((i + 1) * subSize));
					List<DataProcessBean> newList = lstData.subList(fromIndex, toIndex);
					saveData(newList);
				}
			}


			String des="";
			if(orderNoList!=null&&orderNoList.size()>0){
				//				des=",单据:"+getStringList2Str(orderNoList)+"";
				des=",部分单据需填写差异原因";
			}
			String description="保存成功"+des;

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription(description);
			
		}catch(Exception e){
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"对账保存执行失败",e);
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败：" + e.getMessage());
		}


	}

	public String getWeChatSql(DCP_OrderStatementConfirmReq req)throws Exception {
		StringBuffer sqlbuf = new StringBuffer("");
		String conditionSql=getQuerySql(req);

		SimpleDateFormat sDF1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sDF2 = new SimpleDateFormat("yyyy-MM-dd");
		String startDate=sDF1.format(sDF2.parse(req.getRequest().getStartDate()));
		String endDate=sDF1.format(sDF2.parse(req.getRequest().getEndDate()));

		String payCode = getPayCode(req);//获取微信支付宝对应的paycode
		String eId = req.geteId();

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
		sqlbuf.append(" AND T.ACCOUNTSTATUS <>'2'");//已经对账完成的，不需要查询
		sqlbuf.append(" order by T.thirdcreatetime,T.orderno ");

		sqlbuf.append(" ) T ");
		sqlbuf.append(" )");
		sqlbuf.append(" where 1=1");
		/*sqlbuf.append(" AND rn <="+endRow+" ");
	sqlbuf.append(" AND rn >"+startRow+" ");*/

		return sqlbuf.toString();
	}

	public String getPayCode(DCP_OrderStatementConfirmReq req) throws Exception
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

	public String getQuerySql(DCP_OrderStatementConfirmReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer("");

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

		if(thirdType.equals("10")||thirdType.equals("11"))//只有差异类型1
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
	protected List<InsBean> prepareInsertData(DCP_OrderStatementConfirmReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderStatementConfirmReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderStatementConfirmReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderStatementConfirmReq req) throws Exception {
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

		String accountStatus=req.getRequest().getAccountStatus();
		if(accountStatus!=null&&accountStatus.equals("2"))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前选择的是已对账完成状态，无需执行对账完成！");
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderStatementConfirmReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderStatementConfirmReq>(){};
	}

	@Override
	protected DCP_OrderStatementConfirmRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderStatementConfirmRes();
	}

	public static String getStringList2Str(List<String> strList) throws Exception{
		StringBuffer idsStr = new StringBuffer("");
		for (int i = 0; i < strList.size(); i++) {
			if (i > 0) {
				idsStr.append(",");
			}
			String str=strList.get(i);
			idsStr.append(str);
		}
		return idsStr.toString();
	}

	public String getCountSql(DCP_OrderStatementConfirmReq req,String conditionSql)throws Exception {
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT count(1) as COUNTNO FROM OC_STATEMENT T WHERE 1=1 ");
		sqlbuf.append(conditionSql);
		sqlbuf.append(" AND T.ACCOUNTSTATUS <>'2'");//已经对账完成的，不需要查询
		return sqlbuf.toString();

	}

	/**
	 * 查询重复的交易号&&金额相加=0，在pos结账前就撤销的微信支付宝交易订单号
	 * @param req
	 * @param conditionSql
	 * @return
	 * @throws Exception
	 */
	public List<String> getRepeatOrderNo(DCP_OrderStatementConfirmReq req,String conditionSql) throws Exception {

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
	public List<Map<String, Object>> getCRMWechatJsPay(DCP_OrderStatementConfirmReq req) throws Exception
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

			List<Map<String, Object>> maplist =	StaticInfo.dao.executeQuerySQL(sql, null);
			return maplist;

		} 
		catch (Exception e) 
		{


		}

		return null;	  
	}


}
