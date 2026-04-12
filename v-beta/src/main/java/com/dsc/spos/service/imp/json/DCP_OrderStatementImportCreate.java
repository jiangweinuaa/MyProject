package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderStatementImportCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatementImportCreateRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 对账导入功能
 * @author 08546
 */
public class DCP_OrderStatementImportCreate
extends SPosAdvanceService<DCP_OrderStatementImportCreateReq, DCP_OrderStatementImportCreateRes> {

	@Override
	protected void processDUID(DCP_OrderStatementImportCreateReq req, DCP_OrderStatementImportCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		Logger logger = LogManager.getLogger(DCP_OrderStatementImportCreate.class.getName());

		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		String eId = req.geteId();
		String langType = req.getLangType();


		Workbook workbook=null;
		try {
			int subSize = 1000;
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String nowTime=sdf.format(date);
			//对账类型 1-饿了么 2-美团 8-京东 10-微信 11-支付宝 等
			String thirdType = req.getRequest().getThirdType();

			Map<String, Map<String, Object>> shopMap=new HashMap<String, Map<String, Object>>();
			List<Map<String, Object>> mapShopList= null;
			if(thirdType.equals("1")||thirdType.equals("2")||thirdType.equals("8"))
			{
				mapShopList=this.doQueryData("SELECT SHOPID,ORDERSHOPNO,SHOPNAME FROM OC_MAPPINGSHOP WHERE SHOPID IS NOT NULL AND ORDERSHOPNO IS NOT NULL AND status='100' AND LOAD_DOCTYPE='"+req.getRequest().getThirdType()+"'", null);
				if(mapShopList!=null&&mapShopList.size()>0){
					for(Map<String, Object> mapShop:mapShopList){
						shopMap.put(mapShop.get("ORDERSHOPNO").toString(), mapShop);
					}
				}
			}		
			else if (thirdType.equals("10") || thirdType.equals("11")) 
			{
				String sql_shop = "select * from (";
				sql_shop += "SELECT distinct A.ORGANIZATIONNO as SHOPID,A.ORGANIZATIONNO as ORDERSHOPNO,B.ORG_NAME AS SHOPNAME FROM DCP_ORG A LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.status='100' AND B.LANG_TYPE='"
						+ langType + "'";
				sql_shop += "WHERE A.ORG_FORM='2' AND A.status='100' AND A.EID='" + eId + "'";
				sql_shop += ")";
				mapShopList = this.doQueryData(sql_shop, null);

				if (mapShopList != null && mapShopList.size() > 0) 
				{
					for (Map<String, Object> mapShop : mapShopList) {
						shopMap.put(mapShop.get("SHOPID").toString(), mapShop);
					}

				}
			}


			//列表SQL  需要执行的sql列表
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();

			//			String ecPlatformNo="";
			//			if("1".equals(thirdType)){
			//				ecPlatformNo="ELEME";
			//			}else if("2".equals(thirdType)){
			//				ecPlatformNo="MEITUAN";
			//			}else if("8".equals(thirdType)){
			//				ecPlatformNo="JDDJ";
			//			}
			String fileName = req.getRequest().getExcelFileName();
			// 传进来的文件名 + 绝对路径 ， 组合成文件完成路径
			String filePath = System.getProperty("catalina.home") + "\\webapps\\EC\\" + req.getRequest().getThirdType() + "\\import\\";
			// 暂且设置为 固定 EC + 平台名 + 文件名， 如果后期规划需要变更 平台编码，修改服务器文件夹名即可
			File file = new File(filePath);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				boolean bl = file.mkdirs();
			}
			file = null;

			InputStream inputStream = new FileInputStream(filePath + fileName);
			workbook = Workbook.getWorkbook(inputStream);
			Sheet sheet = workbook.getSheet(0);
			int rows = sheet.getRows();
			int columns = sheet.getColumns();
			//如美团的商服赔付单，不记录进数据库，ignoreCount用来记录此类数量
			int ignoreCount=0;
			//1-饿了么
			if("1".equals(thirdType)){
				//需要跳过的单据编号
				//针对饿了么——"补单"单据，饿了么单据，退单失败情况下，会生成"外卖订单"、"退单"、"补单"三张单据，
				//因此针对饿了么excel导入单据，从后往前遍历，将补单写入，其他两种忽略
				List<String> ignoreOrderNoList=new ArrayList<String>();
				Cell[] headRow = sheet.getRow(0);
				boolean isContinue1=false;
				boolean isContinue2=false;
				boolean isContinue3=false;
				for (int j = 0; j < columns; j++) {
					//第一行 即栏位说明
					String headRowName = headRow[j].getContents();
					if("订单编号".equals(headRowName.trim())){
						isContinue1=true;
					}else if("结算金额".equals(headRowName.trim())){
						isContinue2=true;
					}else if("账单日期".equals(headRowName.trim())){
						isContinue3=true;
					}
				}
				if(isContinue1&&isContinue2&isContinue3){
				}else{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "excel格式有误，非饿了么单据");
				}
				flagELM:for (int i = rows-1; i >0; i--) {
					Cell[] row = sheet.getRow(i);
					// OC_ORDER
					//表栏位名称
					ArrayList<String> columnsFName = getColumnsFNameList();
					//表栏位值
					ArrayList<String> columnsFValue = getColumnsFValueList(req,nowTime);//8-京东
					//栏位类型
					ArrayList<Integer> columnsFType = getColumnsFTypeList();

					//第三方订单原价		THIRDORDERAMT   订单原价=总补贴+用户支付金额
					BigDecimal thirdOrderAmt=new BigDecimal(0);
					//第三方总补贴		THIRDPROMOTIONAMT  (平台+商家承担补贴)
					BigDecimal thirdPromotionAmt=new BigDecimal(0);
					//第三方用户支付金额		THIRDPAIDAMT   用户支付金额=总佣金+结算金额
					BigDecimal thirdPaidAmt=new BigDecimal(0);
					//第三方总佣金		THIRDCOMMISSIONAMT  (货款佣金+运费佣金+餐盒费佣金)
					BigDecimal thirdCommissionAmt=new BigDecimal(0);
					//第三方结算金额		THIRDSETTLEMENTAMT
					BigDecimal thirdSettlementAmt=new BigDecimal(0);

					Map<String, Object> shopId=new HashMap<String, Object>();
					boolean ignore=false;
					for (int j = 0; j < columns; j++) {
						//第一行 即栏位说明
						String headRowName = headRow[j].getContents();
						//当前栏位值
						String fieldValue = row[j].getContents();
						if("分店ID".equals(headRowName.trim())){
							//第三方门店编号		THIRDSHOP
							columnsFName.add("THIRDSHOP");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							shopId=shopMap.get(fieldValue.trim());
							columnsFName.add("SHOPID");
							if(shopId!=null&&shopId.size()>0){
								columnsFValue.add(shopId.get("SHOPID").toString());
							}else{
								columnsFValue.add(fieldValue.trim());
							}
							columnsFType.add(Types.VARCHAR);
						}else if("分店名称".equals(headRowName.trim())){
							//第三方门店名称		THIRDSHOPNAME
							columnsFName.add("THIRDSHOPNAME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							columnsFName.add("SHOPNAME");
							if(shopId!=null&&shopId.size()>0){
								columnsFValue.add(shopId.get("SHOPNAME").toString());
							}else{
								columnsFValue.add(fieldValue.trim());
							}
							columnsFType.add(Types.VARCHAR);
						}else if("订单编号".equals(headRowName.trim())){
							//ORDERNO
							columnsFName.add("ORDERNO");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
							if(ignore){
								ignoreOrderNoList.add(fieldValue.trim());
							}else{
								if(ignoreOrderNoList.contains(fieldValue.trim())){
									ignoreCount++;
									continue flagELM;
								}
							}
						}else if("订单创建时间".equals(headRowName.trim())){
							//第三方下单时间		THIRDCREATETIME
							columnsFName.add("THIRDCREATETIME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("订单完成时间".equals(headRowName.trim())){
							//第三方完成时间		THIRDCOMPLETETIME
							columnsFName.add("THIRDCOMPLETETIME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("结算金额".equals(headRowName.trim())){
							//34.12
							thirdSettlementAmt=str2BigDecimal(fieldValue.trim());
							//第三方结算金额		THIRDSETTLEMENTAMT
							columnsFName.add("THIRDSETTLEMENTAMT");
							columnsFValue.add(thirdSettlementAmt.toString());
							columnsFType.add(Types.DECIMAL);

							//							BigDecimal income=new BigDecimal(fieldValue.trim());
							//订单类型	Y	ORDERTYPE
							//1-正向；2-负向
							if(thirdSettlementAmt.compareTo(BigDecimal.ZERO)>=0){
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("1");
								columnsFType.add(Types.VARCHAR);
							}else{
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("2");
								columnsFType.add(Types.VARCHAR);
							}
						}else if("菜价".equals(headRowName.trim())){
							//41.4
							thirdOrderAmt=str2BigDecimal(fieldValue.trim());
							//第三方订单原价		THIRDORDERAMT
							columnsFName.add("THIRDORDERAMT");
							columnsFValue.add(thirdOrderAmt.toString());
							columnsFType.add(Types.DECIMAL);
						}else if("账单日期".equals(headRowName.trim())){
							//第三方账单日期		THIRDSTATEMENTDATE
							columnsFName.add("THIRDSTATEMENTDATE");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("结算（入账）日期".equals(headRowName.trim())){
							//第三方结算（入账）日期		THIRDSETTLEMENTDATE
							columnsFName.add("THIRDSETTLEMENTDATE");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("订单类型".equals(headRowName.trim())){
							columnsFName.add("TYPENAME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
							if("补单".equals(fieldValue.trim())){
								ignore=true;
							}else if("赔偿单".equals(fieldValue.trim())){
								ignoreCount++;
								continue flagELM;
							}
						}
					}
					InsBean ib1 = getInsBean(columnsFName, columnsFValue, columnsFType);
					lstData.add(new DataProcessBean(ib1));
					if(lstData.size()==subSize){
						boolean isSuccess=saveData(lstData);
						if(isSuccess){
							lstData.clear();
						}else{
							isFail=true;
							errMsg.append("导入失败");
							break flagELM;
						}
					}

				}
			}
			// 2-美团
			else if("2".equals(thirdType)){
				Cell[] headRow = sheet.getRow(0);

				boolean isContinue1=false;
				boolean isContinue2=false;
				boolean isContinue3=false;
				for (int j = 0; j < columns; j++) {
					//第一行 即栏位说明
					String headRowName = headRow[j].getContents();
					if("订单号".equals(headRowName.trim())){
						isContinue1=true;
					}else if("商家应收款".equals(headRowName.trim())){
						isContinue2=true;
					}else if("账单日期".equals(headRowName.trim())){
						isContinue3=true;
					}
				}
				if(isContinue1&&isContinue2&isContinue3){
				}else{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "excel格式有误，非美团单据");
				}


				flagMEITUAN:for (int i = 1; i < rows; i++) {
					Cell[] row = sheet.getRow(i);
					// OC_ORDER
					//表栏位名称
					ArrayList<String> columnsFName = getColumnsFNameList();
					//表栏位值
					ArrayList<String> columnsFValue = getColumnsFValueList(req,nowTime);//8-京东
					//栏位类型
					ArrayList<Integer> columnsFType = getColumnsFTypeList();

					//第三方订单原价		THIRDORDERAMT   订单原价=总补贴+用户支付金额
					BigDecimal thirdOrderAmt=new BigDecimal(0);
					//第三方总补贴		THIRDPROMOTIONAMT  (平台+商家承担补贴)
					BigDecimal thirdPromotionAmt=new BigDecimal(0);
					//第三方用户支付金额		THIRDPAIDAMT   用户支付金额=总佣金+结算金额
					BigDecimal thirdPaidAmt=new BigDecimal(0);
					//第三方总佣金		THIRDCOMMISSIONAMT  (货款佣金+运费佣金+餐盒费佣金)
					BigDecimal thirdCommissionAmt=new BigDecimal(0);
					//第三方结算金额		THIRDSETTLEMENTAMT
					BigDecimal thirdSettlementAmt=new BigDecimal(0);

					//美团活动补贴 3.00
					BigDecimal promotion1=new BigDecimal(0);
					//商家活动支出 -3.00
					BigDecimal promotion2=new BigDecimal(0);
					Map<String, Object> shopId=new HashMap<String, Object>();
					for (int j = 0; j < columns; j++) {
						//第一行 即栏位说明
						String headRowName = headRow[j].getContents();
						//当前栏位值
						String fieldValue = row[j].getContents();
						if("门店id".equals(headRowName.trim())){
							//第三方门店编号		THIRDSHOP
							columnsFName.add("THIRDSHOP");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							shopId=shopMap.get(fieldValue.trim());
							columnsFName.add("SHOPID");
							if(shopId!=null&&shopId.size()>0){
								columnsFValue.add(shopId.get("SHOPID").toString());
							}else{
								columnsFValue.add(fieldValue.trim());
							}
							columnsFType.add(Types.VARCHAR);
						}else if("门店名称".equals(headRowName.trim())){
							//第三方门店名称		THIRDSHOPNAME
							columnsFName.add("THIRDSHOPNAME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							columnsFName.add("SHOPNAME");
							if(shopId!=null&&shopId.size()>0){
								columnsFValue.add(shopId.get("SHOPNAME").toString());
							}else{
								columnsFValue.add(fieldValue.trim());
							}
							columnsFType.add(Types.VARCHAR);
						}else if("订单号".equals(headRowName.trim())){
							//ORDERNO
							columnsFName.add("ORDERNO");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("商家应收款".equals(headRowName)){
							//34.12
							thirdSettlementAmt=str2BigDecimal(fieldValue.trim());
							//第三方结算金额		THIRDSETTLEMENTAMT
							columnsFName.add("THIRDSETTLEMENTAMT");
							columnsFValue.add(thirdSettlementAmt.toString());
							columnsFType.add(Types.DECIMAL);

							//							BigDecimal income=new BigDecimal(fieldValue.trim());
							//订单类型	Y	ORDERTYPE
							//1-正向；2-负向
							if(thirdSettlementAmt.compareTo(BigDecimal.ZERO)>=0){
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("1");
								columnsFType.add(Types.VARCHAR);
							}else{
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("2");
								columnsFType.add(Types.VARCHAR);
							}
						}else if("下单时间".equals(headRowName.trim())){
							//2019-05-30 17:53:23
							//第三方下单时间		THIRDCREATETIME
							columnsFName.add("THIRDCREATETIME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("完成时间".equals(headRowName.trim())){
							//2019-05-30 23:59:47
							//第三方完成时间		THIRDCOMPLETETIME
							columnsFName.add("THIRDCOMPLETETIME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("用户线上支付金额".equals(headRowName.trim())){
							//							if(fieldValue!=null&&!fieldValue.trim().isEmpty()&&!"-".equals(fieldValue.trim())){
							//32.15
							thirdPaidAmt=str2BigDecimal(fieldValue.trim());
							//第三方用户支付金额		THIRDPAIDAMT
							columnsFName.add("THIRDPAIDAMT");
							columnsFValue.add(thirdPaidAmt.toString());
							columnsFType.add(Types.DECIMAL);
							//							}
						}else if("商品总价".equals(headRowName.trim())){
							//							if(fieldValue!=null&&!fieldValue.trim().isEmpty()&&!"-".equals(fieldValue.trim())){
							//41.4
							thirdOrderAmt=str2BigDecimal(fieldValue.trim());
							//第三方订单原价		THIRDORDERAMT
							columnsFName.add("THIRDORDERAMT");
							columnsFValue.add(thirdOrderAmt.toString());
							columnsFType.add(Types.DECIMAL);
							//							}
						}else if("美团活动补贴".equals(headRowName.trim())){
							//							if(fieldValue!=null&&!fieldValue.trim().isEmpty()&&!"-".equals(fieldValue.trim())){
							promotion2=str2BigDecimal(fieldValue.trim());
							//							}
						}else if("商家活动支出".equals(headRowName.trim())){
							//							if(fieldValue!=null&&!fieldValue.trim().isEmpty()&&!"-".equals(fieldValue.trim())){
							promotion1=str2BigDecimal(fieldValue.trim());
							//							}
						}else if("平台服务费".equals(headRowName.trim())){
							//							if(fieldValue!=null&&!fieldValue.trim().isEmpty()&&!"-".equals(fieldValue.trim())){
							//-6.03
							thirdCommissionAmt=str2BigDecimal(fieldValue.trim());
							//第三方总佣金		THIRDCOMMISSIONAMT
							columnsFName.add("THIRDCOMMISSIONAMT");
							columnsFValue.add(thirdCommissionAmt.toString());
							columnsFType.add(Types.DECIMAL);
							//							}
						}else if("账单日期".equals(headRowName.trim())){
							//2019-05-30
							//第三方账单日期		THIRDSTATEMENTDATE
							columnsFName.add("THIRDSTATEMENTDATE");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("交易类型".equals(headRowName.trim())){
							//外卖订单、商服赔付
							columnsFName.add("TYPENAME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
							if("餐损赔付".equals(fieldValue.trim())){
								ignoreCount++;
								continue flagMEITUAN;
							}else if("商服赔付".equals(fieldValue.trim())){
								ignoreCount++;
								continue flagMEITUAN;
							}
						}
						//						else if("配送状态".equals(headRowName.trim())){
						//							//商服赔付的情况忽略，不记录本条记录
						//							if("已取消".equals(fieldValue.trim())){
						//								ignoreCount++;
						//								continue flagMEITUAN;
						//							}
						//						}

					}
					//9.25
					thirdPromotionAmt=promotion1.subtract(promotion2);
					//第三方总补贴		THIRDPROMOTIONAMT
					columnsFName.add("THIRDPROMOTIONAMT");
					columnsFValue.add(thirdPromotionAmt.toString());
					columnsFType.add(Types.DECIMAL);

					InsBean ib1 = getInsBean(columnsFName, columnsFValue, columnsFType);
					lstData.add(new DataProcessBean(ib1));
					if(lstData.size()==subSize){
						boolean isSuccess=saveData(lstData);
						if(isSuccess){
							lstData.clear();
						}else{
							isFail=true;
							errMsg.append("导入失败");
							break flagMEITUAN;
						}
					}

				}
			}
			//8-京东
			else if("8".equals(thirdType)){
				Cell[] headRow = sheet.getRow(0);
				//可通过第一行中文说明，来提前校验excel导入模板

				boolean isContinue1=false;
				boolean isContinue2=false;
				boolean isContinue3=false;
				for (int j = 0; j < columns; j++) {
					//第一行 即栏位说明
					String headRowName = headRow[j].getContents();
					if("订单号".equals(headRowName.trim())){
						isContinue1=true;
					}else if("应结金额".equals(headRowName.trim())){
						isContinue2=true;
					}else if("账期".equals(headRowName.trim())){
						isContinue3=true;
					}
				}
				if(isContinue1&&isContinue2&isContinue3){
				}else{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "excel格式有误，非京东单据");
				}


				flagJD:for (int i = 1; i < rows; i++) {
					Cell[] row = sheet.getRow(i);
					// OC_ORDER
					//表栏位名称
					ArrayList<String> columnsFName = getColumnsFNameList();
					//表栏位值
					ArrayList<String> columnsFValue = getColumnsFValueList(req,nowTime);//8-京东
					//栏位类型
					ArrayList<Integer> columnsFType = getColumnsFTypeList();

					//第三方订单原价		THIRDORDERAMT   订单原价=总补贴+用户支付金额
					BigDecimal thirdOrderAmt=new BigDecimal(0);
					//第三方总补贴		THIRDPROMOTIONAMT  (平台+商家承担补贴)
					BigDecimal thirdPromotionAmt=new BigDecimal(0);
					//第三方用户支付金额		THIRDPAIDAMT   用户支付金额=总佣金+结算金额
					BigDecimal thirdPaidAmt=new BigDecimal(0);
					//第三方总佣金		THIRDCOMMISSIONAMT  (货款佣金+运费佣金+餐盒费佣金)
					BigDecimal thirdCommissionAmt=new BigDecimal(0);
					//第三方结算金额		THIRDSETTLEMENTAMT
					BigDecimal thirdSettlementAmt=new BigDecimal(0);
					Map<String, Object> shopId=new HashMap<String, Object>();
					for (int j = 0; j < columns; j++) {
						//第一行 即栏位说明
						String headRowName = headRow[j].getContents();
						//当前栏位值
						String fieldValue = row[j].getContents();
						if("门店id".equals(headRowName.trim())){
							//第三方门店编号		THIRDSHOP
							columnsFName.add("THIRDSHOP");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							shopId=shopMap.get(fieldValue.trim());
							columnsFName.add("SHOPID");
							if(shopId!=null&&shopId.size()>0){
								columnsFValue.add(shopId.get("SHOPID").toString());
							}else{
								columnsFValue.add(fieldValue.trim());
							}
							columnsFType.add(Types.VARCHAR);
						}else if("门店名称".equals(headRowName.trim())){
							//第三方门店名称		THIRDSHOPNAME
							columnsFName.add("THIRDSHOPNAME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							columnsFName.add("SHOPNAME");
							if(shopId!=null&&shopId.size()>0){
								columnsFValue.add(shopId.get("SHOPNAME").toString());
							}else{
								columnsFValue.add(fieldValue.trim());
							}
							columnsFType.add(Types.VARCHAR);
						}else if("订单号".equals(headRowName.trim())){
							//ORDERNO
							columnsFName.add("ORDERNO");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("订单类型".equals(headRowName)){
							//订单类型	Y	ORDERTYPE
							//1-正向；2-负向
							if("正向订单".equals(fieldValue.trim())){
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("1");
								columnsFType.add(Types.VARCHAR);
							}else if("负向订单".equals(fieldValue.trim())){
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("2");
								columnsFType.add(Types.VARCHAR);
							}
						}else if("下单时间".equals(headRowName.trim())){
							//第三方下单时间		THIRDCREATETIME
							columnsFName.add("THIRDCREATETIME");
							String formatDate=getFormatDate(fieldValue.trim(), "yyyyMMdd HH:mm", "yyyy-MM-dd HH:mm:ss");
							columnsFValue.add(formatDate);
							columnsFType.add(Types.VARCHAR);
						}else if("完成时间".equals(headRowName.trim())){
							//第三方完成时间		THIRDCOMPLETETIME
							columnsFName.add("THIRDCOMPLETETIME");
							String formatDate=getFormatDate(fieldValue.trim(), "yyyyMMdd HH:mm", "yyyy-MM-dd HH:mm:ss");
							columnsFValue.add(formatDate);
							columnsFType.add(Types.VARCHAR);
						}else if("应结金额".equals(headRowName.trim())){
							//34.12
							thirdSettlementAmt=str2BigDecimal(fieldValue.trim());
							//第三方结算金额		THIRDSETTLEMENTAMT
							columnsFName.add("THIRDSETTLEMENTAMT");
							columnsFValue.add(thirdSettlementAmt.toString());
							columnsFType.add(Types.DECIMAL);
						}else if("用户支付货款".equals(headRowName.trim())){
							//32.15
							thirdPaidAmt=str2BigDecimal(fieldValue.trim());
							//第三方用户支付金额		THIRDPAIDAMT
							columnsFName.add("THIRDPAIDAMT");
							columnsFValue.add(thirdPaidAmt.toString());
							columnsFType.add(Types.DECIMAL);
						}else if("订单原价".equals(headRowName.trim())){
							//41.4
							thirdOrderAmt=str2BigDecimal(fieldValue.trim());
							//第三方订单原价		THIRDORDERAMT
							columnsFName.add("THIRDORDERAMT");
							columnsFValue.add(thirdOrderAmt.toString());
							columnsFType.add(Types.DECIMAL);
						}else if("总补贴(平台+商家承担补贴)".equals(headRowName.trim())){
							//9.25
							thirdPromotionAmt=str2BigDecimal(fieldValue.trim());
							//第三方总补贴		THIRDPROMOTIONAMT
							columnsFName.add("THIRDPROMOTIONAMT");
							columnsFValue.add(thirdPromotionAmt.toString());
							columnsFType.add(Types.DECIMAL);
						}else if("总佣金(货款佣金+运费佣金+餐盒费佣金)(可开票)".equals(headRowName.trim())){
							//-6.03
							thirdCommissionAmt=str2BigDecimal(fieldValue.trim());
							//第三方总佣金		THIRDCOMMISSIONAMT
							columnsFName.add("THIRDCOMMISSIONAMT");
							columnsFValue.add(thirdCommissionAmt.toString());
							columnsFType.add(Types.DECIMAL);
						}else if("账期".equals(headRowName.trim())){
							//第三方账单日期		THIRDSTATEMENTDATE
							columnsFName.add("THIRDSTATEMENTDATE");
							String formatDate=getFormatDate(fieldValue.trim(), "yyyyMMdd", "yyyy-MM-dd");
							columnsFValue.add(formatDate);
							columnsFType.add(Types.VARCHAR);
						}else if("结算完成时间".equals(headRowName.trim())){
							//第三方结算（入账）日期		THIRDSETTLEMENTDATE
							columnsFName.add("THIRDSETTLEMENTDATE");
							String formatDate=getFormatDate(fieldValue.trim(), "yyyyMMdd HH:mm", "yyyy-MM-dd");
							columnsFValue.add(formatDate);
							columnsFType.add(Types.VARCHAR);
						}else if("结算单号".equals(headRowName.trim())){
							//第三方结算单编号		THIRDSETTLEMENTNO
							columnsFName.add("THIRDSETTLEMENTNO");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}else if("收付款单号".equals(headRowName.trim())){
							//第三方收付款单号		THIRDPAYMENTNO
							columnsFName.add("THIRDPAYMENTNO");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}

					}
					//					//金额校验
					//					//用户支付金额=总佣金+结算金额
					//					if((thirdSettlementAmt.subtract(thirdCommissionAmt)).compareTo(thirdPaidAmt)!=0){
					//						errMsg.append("第"+(i+1)+"行金额不匹配,应为:|用户支付货款|=|应结金额|+|总佣金|");
					//						isFail = true;
					//					}
					//					//订单原价=总补贴+用户支付金额
					//					if((thirdPaidAmt.add(thirdPromotionAmt)).compareTo(thirdOrderAmt)!=0){
					//						errMsg.append("第"+(i+1)+"行金额不匹配,应为:|订单原价|=|总补贴|+|用户支付货款|");
					//						isFail = true;
					//					}
					InsBean ib1 = getInsBean(columnsFName, columnsFValue, columnsFType);
					lstData.add(new DataProcessBean(ib1));
					if(lstData.size()==subSize){
						boolean isSuccess=saveData(lstData);
						if(isSuccess){
							lstData.clear();
						}else{
							isFail=true;
							errMsg.append("导入失败");
							break flagJD;
						}
					}

				}
			}
			//10-微信
			else if("10".equals(thirdType)){
				Cell[] headRow = sheet.getRow(0);

				boolean isContinue1=false;
				boolean isContinue2=false;
				boolean isContinue3=false;
				for (int j = 0; j < columns; j++) {
					//第一行 即栏位说明
					String headRowName = headRow[j].getContents();
					if("商户号".equals(headRowName.trim())){
						isContinue1=true;
					}else if("特约商户号".equals(headRowName.trim())){
						isContinue2=true;
					}else if("微信订单号".equals(headRowName.trim())){
						isContinue3=true;
					}
				}
				if(isContinue1&&isContinue2&isContinue3){
				}else{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "excel格式有误，非微信对账单");
				}


				flagWeChat:for (int i = 1; i < rows; i++) {
					Cell[] row = sheet.getRow(i);
					// OC_ORDER
					//表栏位名称
					ArrayList<String> columnsFName = getColumnsFNameList();
					//表栏位值
					ArrayList<String> columnsFValue = getColumnsFValueList(req,nowTime);//8-京东
					//栏位类型
					ArrayList<Integer> columnsFType = getColumnsFTypeList();
					//订单类型 1-正向 2-反向
					String orderType = "1";

					//第三方订单金额		THIRDORDERAMT   
					BigDecimal thirdOrderAmt=new BigDecimal(0);
					//第三方总补贴		THIRDPROMOTIONAMT  
					//BigDecimal thirdPromotionAmt=new BigDecimal(0);
					//第三方用户支付金额		THIRDPAIDAMT   
					//BigDecimal thirdPaidAmt=new BigDecimal(0);
					//第三方手续费		THIRDCOMMISSIONAMT  
					BigDecimal thirdCommissionAmt=new BigDecimal(0);
					//第三方应结订单金额		THIRDSETTLEMENTAMT
					BigDecimal thirdSettlementAmt=new BigDecimal(0);

					//退款金额 
					BigDecimal 	thirdrefundAmt =new BigDecimal(0);

					//微信代金券金额
					BigDecimal promotion1=new BigDecimal(0);
					//商家活动支出 -3.00
					BigDecimal promotion2=new BigDecimal(0);
					Map<String, Object> shopId=new HashMap<String, Object>();
					for (int j = 0; j < columns; j++) {
						//第一行 即栏位说明
						String headRowName = headRow[j].getContents();
						//当前栏位值
						String fieldValue = row[j].getContents().replace("`", "").replace("'", "");
						if("交易时间".equals(headRowName.trim())){

							if("总交易单数".equals(fieldValue.trim()))//后面是合计内容，不用导入了
							{
								break flagWeChat;
							}
							//2019-05-30 17:53:23
							//第三方交易时间		THIRDCREATETIME
							columnsFName.add("THIRDCREATETIME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							//完成时间
							columnsFName.add("THIRDCOMPLETETIME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							//第三方账单日期		THIRDSTATEMENTDATE 2019-01-01 需要转换			
							SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd");
							String startDate=sDF.format(sDF.parse(fieldValue.trim()));

							columnsFName.add("THIRDSTATEMENTDATE");
							columnsFValue.add(startDate);
							columnsFType.add(Types.VARCHAR);

						}else if("设备号".equals(headRowName.trim())){
							//第三方门店编号		THIRDSHOP
							columnsFName.add("THIRDSHOP");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);


							String fieldShopId = fieldValue.trim();

							columnsFName.add("SHOPID");						
							columnsFType.add(Types.VARCHAR);
							if(fieldShopId==null||fieldShopId.isEmpty())
							{
								//主键不能为空
								columnsFValue.add(" ");
							}
							else
							{
								shopId=shopMap.get(fieldValue.trim());
								if(shopId!=null&&shopId.size()>0){
									columnsFValue.add(shopId.get("SHOPID").toString());
								}else{
									columnsFValue.add(fieldValue.trim());
								}
							}


						}else if("微信订单号".equals(headRowName.trim())){
							//ORDERNO
							columnsFName.add("ORDERNO");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						}
						else if("应结订单金额".equals(headRowName)){
							//后面根据 单据类型 赋值
							//thirdSettlementAmt=str2BigDecimal(fieldValue.trim());

							/*	//第三方结算金额		THIRDSETTLEMENTAMT 后面根据 单据类型 获取对应的值
							columnsFName.add("THIRDSETTLEMENTAMT");
							columnsFValue.add(thirdSettlementAmt.toString());
							columnsFType.add(Types.DECIMAL);*/

						}else if("代金券金额".equals(headRowName.trim())){
							/*//2019-05-30 23:59:47
							//第三方完成时间		THIRDCOMPLETETIME
							columnsFName.add("THIRDCOMPLETETIME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);*/
						}else if("微信退款单号".equals(headRowName.trim())){
							//退单单号是主键，不能为空
							String orderno_refund = fieldValue;
							if(orderno_refund==null||orderno_refund.trim().isEmpty())
							{
								orderno_refund = " ";
							}
							columnsFName.add("ORDERNO_REFUND");				
							columnsFValue.add(orderno_refund);
							columnsFType.add(Types.VARCHAR);
						}else if("退款金额".equals(headRowName.trim())){
							//退款金额 
							thirdrefundAmt=str2BigDecimal(fieldValue.trim());
							/*//第三方订单原价		THIRDORDERAMT
								columnsFName.add("THIRDORDERAMT");
								columnsFValue.add(thirdOrderAmt.toString());
								columnsFType.add(Types.DECIMAL);	*/						
							//订单类型	Y	ORDERTYPE
							//1-正向；2-负向
							if(thirdrefundAmt.compareTo(BigDecimal.ZERO)==0){
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("1");
								columnsFType.add(Types.VARCHAR);
								orderType = "1";
							}else{
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("2");
								columnsFType.add(Types.VARCHAR);
								orderType = "2";
							}

						}else if("商品名称".equals(headRowName.trim())){
							//第三方门店名称		THIRDSHOPNAME
							columnsFName.add("THIRDSHOPNAME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							columnsFName.add("SHOPNAME");
							if(shopId!=null&&shopId.size()>0){
								columnsFValue.add(shopId.get("SHOPNAME").toString());
							}else{
								columnsFValue.add("");
							}
							columnsFType.add(Types.VARCHAR);
						}
						else if("手续费".equals(headRowName.trim())){
							//6.03
							thirdCommissionAmt=str2BigDecimal(fieldValue.trim());
							//第三方手续费   微信消费是正，退款是负，转换成 消费是负  退款是正
							thirdCommissionAmt = thirdCommissionAmt.negate();//取下相反数
							columnsFName.add("THIRDCOMMISSIONAMT");
							columnsFValue.add(thirdCommissionAmt.toString());
							columnsFType.add(Types.DECIMAL);

						}else if("订单金额".equals(headRowName.trim())){
							//2019-05-30
							//订单金额  
							thirdOrderAmt = str2BigDecimal(fieldValue.trim());
							thirdSettlementAmt = thirdOrderAmt;
							/*columnsFName.add("THIRDORDERAMT");
							columnsFValue.add(thirdOrderAmt.toString());
							columnsFType.add(Types.DECIMAL);*/
						}else if("交易类型".equals(headRowName.trim())){
							//外卖订单、商服赔付
							columnsFName.add("TYPENAME");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
							if("餐损赔付".equals(fieldValue.trim())){
								ignoreCount++;
								continue flagWeChat;
							}else if("商服赔付".equals(fieldValue.trim())){
								ignoreCount++;
								continue flagWeChat;
							}
						}
						//						else if("配送状态".equals(headRowName.trim())){
						//							//商服赔付的情况忽略，不记录本条记录
						//							if("已取消".equals(fieldValue.trim())){
						//								ignoreCount++;
						//								continue flagMEITUAN;
						//							}
						//						}

					}

					if(orderType.equals("1"))
					{
						//第三方结算金额		THIRDSETTLEMENTAMT 后面根据 单据类型 获取对应的值
						columnsFName.add("THIRDSETTLEMENTAMT");
						columnsFValue.add(thirdSettlementAmt.toString());
						columnsFType.add(Types.DECIMAL);
						//第三方订单原价		THIRDORDERAMT
						columnsFName.add("THIRDORDERAMT");
						columnsFValue.add(thirdOrderAmt.toString());
						columnsFType.add(Types.DECIMAL);

					}
					else if (orderType.equals("2")) 
					{
						//退单  负
						if(thirdrefundAmt.compareTo(BigDecimal.ZERO)>0)
						{
							thirdrefundAmt = thirdrefundAmt.negate();
						}
						//第三方结算金额		
						columnsFName.add("THIRDSETTLEMENTAMT");
						columnsFValue.add(thirdrefundAmt.toString());
						columnsFType.add(Types.DECIMAL);

						//第三方订单原价		THIRDORDERAMT
						columnsFName.add("THIRDORDERAMT");
						columnsFValue.add(thirdrefundAmt.toString());
						columnsFType.add(Types.DECIMAL);

					}
					else 
					{					
						//第三方结算金额		THIRDSETTLEMENTAMT 后面根据 单据类型 获取对应的值
						columnsFName.add("THIRDSETTLEMENTAMT");
						columnsFValue.add(thirdSettlementAmt.toString());
						columnsFType.add(Types.DECIMAL);
						//第三方订单原价		THIRDORDERAMT
						columnsFName.add("THIRDORDERAMT");
						columnsFValue.add(thirdOrderAmt.toString());
						columnsFType.add(Types.DECIMAL);		
					}

					InsBean ib1 = getInsBean(columnsFName, columnsFValue, columnsFType);
					lstData.add(new DataProcessBean(ib1));
					if(lstData.size()==subSize){
						boolean isSuccess=saveData(lstData);
						if(isSuccess){
							lstData.clear();
						}else{
							isFail=true;
							errMsg.append("导入失败");
							break flagWeChat;
						}
					}

				}
			}
			//11-支付宝
			else if("11".equals(thirdType)){
				int realRow = 0;//真正的数据索引
				Cell[] headRow = sheet.getRow(0);
				for(int k = 0; k < rows;k++)
				{

					headRow = sheet.getRow(k);//当前行的列数=最大列数，就是真正的列头
					if(headRow.length == columns )
					{
						realRow = k;
						break;
					}
				}



				boolean isContinue1=false;
				boolean isContinue2=false;
				boolean isContinue3=false;
				for (int j = 0; j < columns; j++) {
					//第一行 即栏位说明
					String headRowName = headRow[j].getContents();
					if("支付宝交易号".equals(headRowName.trim())){
						isContinue1=true;
					}else if("业务类型".equals(headRowName.trim())){
						isContinue2=true;
					}else if("商户订单号".equals(headRowName.trim())){
						isContinue3=true;
					}
				}
				if(isContinue1&&isContinue2&isContinue3){
				}else{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "excel格式有误，非微信对账单");
				}

				int realRow_data = realRow +1;

				flagAlipay:for (int i = realRow_data; i < rows; i++) {
					Cell[] row = sheet.getRow(i);
					int curRowColumnCount = row.length;//当前行多少列
					// OC_ORDER
					//表栏位名称
					ArrayList<String> columnsFName = getColumnsFNameList();
					//表栏位值
					ArrayList<String> columnsFValue = getColumnsFValueList(req,nowTime);//8-京东
					//栏位类型
					ArrayList<Integer> columnsFType = getColumnsFTypeList();
					//订单类型 1-正向 2-反向

					//第三方订单金额		THIRDORDERAMT   
					BigDecimal thirdOrderAmt=new BigDecimal(0);
					//第三方总补贴		THIRDPROMOTIONAMT  
					//BigDecimal thirdPromotionAmt=new BigDecimal(0);
					//第三方用户支付金额		THIRDPAIDAMT   
					//BigDecimal thirdPaidAmt=new BigDecimal(0);
					//第三方手续费		THIRDCOMMISSIONAMT  
					BigDecimal thirdCommissionAmt=new BigDecimal(0);
					//第三方应结订单金额（商家实收）		THIRDSETTLEMENTAMT
					BigDecimal thirdSettlementAmt=new BigDecimal(0);

					//退款金额 
					BigDecimal 	thirdrefundAmt =new BigDecimal(0);

					//支付宝优惠总金额
					BigDecimal promotion_alipay=new BigDecimal(0);
					//商家活动优惠总金额 
					BigDecimal promotion_poi=new BigDecimal(0);
					Map<String, Object> shopId=new HashMap<String, Object>();
					for (int j = 0; j < columns; j++) {
						//第一行 即栏位说明
						String headRowName = headRow[j].getContents();
						//第一行列头可能比下面列多			
						if(j>curRowColumnCount-1)
						{
							continue;
						}

						if(row[j]==null)
						{
							continue;
						}
						//当前栏位值
						String fieldValue = row[j].getContents();
						if(fieldValue==null)
						{
							continue;
						}

						if("支付宝交易号".equals(headRowName.trim())){
							if (fieldValue.trim().contains("业务明细列表结束")) //后面都是合计内容不导入
							{
								break flagAlipay;				
							}
							//ORDERNO
							columnsFName.add("ORDERNO");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);
						} 
						else	if("创建时间".equals(headRowName.trim())){
							//     8/1/19 19:20  这种格式哎
							String dateImport_str = fieldValue.trim();
							try 
							{
								int indexof_1 = dateImport_str.indexOf(" ");
								String s_mmddyy = dateImport_str.substring(0, indexof_1);//日期
								String s_hhmm = dateImport_str.substring(indexof_1 + 1, dateImport_str.length());//时间
								String[] ss = s_mmddyy.split("/");
								String s_month = ss[0];
								String s_day = ss[1];
								String s_year = ss[2];
								if(s_month.length()==1)
								{
									s_month="0"+s_month;
								}
								if(s_day.length()==1)
								{
									s_day="0"+s_day;
								}
								if(s_year.length()==2)//简单处理下，鬼知道还能不能搞到2100年
								{
									s_year="20"+s_year;
								}
								//2019-08-01 17:40
								String date_str=s_year+"-"+s_month+"-"+s_day+" "+s_hhmm;
								SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");									
								Date date1 = sDF.parse(date_str);

								SimpleDateFormat sDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");								  
								dateImport_str = sDF2.format(date1);
							} 
							catch (Exception e) 
							{


							}


							//第三方交易时间		THIRDCREATETIME
							columnsFName.add("THIRDCREATETIME");
							columnsFValue.add(dateImport_str);
							columnsFType.add(Types.VARCHAR);								
						}
						else	if("完成时间".equals(headRowName.trim())){
							// 8/1/19 19:20 	

							String dateImport_str = fieldValue.trim();
							String dateImport_statementdate = dateImport_str;//THIRDSTATEMENTDATE
							try 
							{
								int indexof_1 = dateImport_str.indexOf(" ");
								String s_mmddyy = dateImport_str.substring(0, indexof_1);//日期
								String s_hhmm = dateImport_str.substring(indexof_1 + 1, dateImport_str.length());//时间
								String[] ss = s_mmddyy.split("/");
								String s_month = ss[0];
								String s_day = ss[1];
								String s_year = ss[2];
								if(s_month.length()==1)
								{
									s_month="0"+s_month;
								}
								if(s_day.length()==1)
								{
									s_day="0"+s_day;
								}
								if(s_year.length()==2)//简单处理下，鬼知道还能不能搞到2100年
								{
									s_year="20"+s_year;
								}
								//2019-08-01 17:40
								String date_str=s_year+"-"+s_month+"-"+s_day+" "+s_hhmm;
								SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");									
								Date date1 = sDF.parse(date_str);

								SimpleDateFormat sDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");								  
								dateImport_str = sDF2.format(date1);

								SimpleDateFormat sDF3 = new SimpleDateFormat("yyyy-MM-dd");
								dateImport_statementdate = sDF3.format(date1);
							} 
							catch (Exception e) 
							{


							}

							//完成时间
							columnsFName.add("THIRDCOMPLETETIME");
							columnsFValue.add(dateImport_str);
							columnsFType.add(Types.VARCHAR);

							//第三方账单日期		THIRDSTATEMENTDATE
							columnsFName.add("THIRDSTATEMENTDATE");
							columnsFValue.add(dateImport_statementdate);
							columnsFType.add(Types.VARCHAR);

						}
						else if("门店编号".equals(headRowName.trim())){
							//第三方门店编号		THIRDSHOP
							columnsFName.add("THIRDSHOP");
							columnsFValue.add(fieldValue.trim());
							columnsFType.add(Types.VARCHAR);

							columnsFName.add("SHOPID");						
							columnsFType.add(Types.VARCHAR);
							if(fieldValue==null||fieldValue.isEmpty()||fieldValue.trim().equals(""))
							{
								//主键不能为空
								columnsFValue.add(" ");
							}
							else
							{				
								columnsFValue.add(fieldValue.trim());						
							}


						}
						else if("订单金额（元）".equals(headRowName.trim())){
							//2019-05-30
							//订单金额  
							thirdOrderAmt = str2BigDecimal(fieldValue.trim());
							columnsFName.add("THIRDORDERAMT");
							columnsFValue.add(thirdOrderAmt.toString());
							columnsFType.add(Types.DECIMAL);

							//第三方结算金额商家实收 搞成一样吧
							columnsFName.add("THIRDSETTLEMENTAMT");
							columnsFValue.add(thirdOrderAmt.toString());
							columnsFType.add(Types.DECIMAL);

							if(thirdOrderAmt.compareTo(BigDecimal.ZERO)>=0)
							{
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("1");
								columnsFType.add(Types.VARCHAR);							
							}	
							else
							{
								columnsFName.add("ORDERTYPE");
								columnsFValue.add("2");
								columnsFType.add(Types.VARCHAR);
							}

						}
						else if("商家实收（元）".equals(headRowName)){
							/*//后面根据 单据类型 赋值
							thirdSettlementAmt=str2BigDecimal(fieldValue.trim());

						  //第三方结算金额商家实收
							columnsFName.add("THIRDSETTLEMENTAMT");
							columnsFValue.add(thirdSettlementAmt.toString());
							columnsFType.add(Types.DECIMAL);*/

						}
						else if("支付宝红包（元）".equals(headRowName.trim())){
							BigDecimal hongbao = str2BigDecimal(fieldValue.trim());
							promotion_alipay = promotion_alipay.add(hongbao);
						}
						else if("集分宝（元）".equals(headRowName.trim())){
							BigDecimal hongbao = str2BigDecimal(fieldValue.trim());
							promotion_alipay = promotion_alipay.add(hongbao);
						}
						else if("支付宝优惠（元）".equals(headRowName.trim())){
							BigDecimal hongbao = str2BigDecimal(fieldValue.trim());
							promotion_alipay = promotion_alipay.add(hongbao);
						}
						else if("商家优惠（元）".equals(headRowName.trim())){
							BigDecimal youhui = str2BigDecimal(fieldValue.trim());
							promotion_poi=youhui;
						}
						else if("券名称".equals(headRowName.trim())){
							String youhui_name = fieldValue.trim();
							if(youhui_name!=null&&youhui_name.isEmpty()==false)
							{
								if(youhui_name.length()>60)
								{
									youhui_name = youhui_name.substring(0, 60);
								}							
								columnsFName.add("CUSTOMFIELD2");
								columnsFValue.add(youhui_name);
								columnsFType.add(Types.VARCHAR);							
							}

						}					
						else if("退款批次号/请求号".equals(headRowName.trim())){
							//退单单号是主键，不能为空
							String orderno_refund = fieldValue;
							if(orderno_refund==null||orderno_refund.trim().isEmpty())
							{
								orderno_refund = " ";
							}
							columnsFName.add("ORDERNO_REFUND");				
							columnsFValue.add(orderno_refund);
							columnsFType.add(Types.VARCHAR);
						}
						else if("商品名称".equals(headRowName.trim())){
							//第三方门店名称		THIRDSHOPNAME
							String str_get = fieldValue.trim();
							if(str_get!=null&&str_get.length()>60)
							{
								str_get = str_get.substring(0, 60);
							}	
							columnsFName.add("THIRDSHOPNAME");
							columnsFValue.add(str_get);
							columnsFType.add(Types.VARCHAR);

						}
						else if("门店名称".equals(headRowName.trim())){
							String str_get = fieldValue.trim();
							if(str_get!=null&&str_get.length()>60)
							{
								str_get = str_get.substring(0, 60);
							}	
							columnsFName.add("SHOPNAME");
							columnsFValue.add(str_get);
							columnsFType.add(Types.VARCHAR);
						}

						else if("服务费（元）".equals(headRowName.trim())){
							//6.03
							thirdCommissionAmt=str2BigDecimal(fieldValue.trim());
							//第三方手续费   支付宝 消费是负  退款是正						
							columnsFName.add("THIRDCOMMISSIONAMT");
							columnsFValue.add(thirdCommissionAmt.toString());
							columnsFType.add(Types.DECIMAL);

						}else if("业务类型".equals(headRowName.trim())){

							String str_get = fieldValue.trim();
							if(str_get!=null&&str_get.length()>30)
							{
								str_get = str_get.substring(0, 30);
							}
							columnsFName.add("TYPENAME");
							columnsFValue.add(str_get);
							columnsFType.add(Types.VARCHAR);

						}


					}



					InsBean ib1 = getInsBean(columnsFName, columnsFValue, columnsFType);
					lstData.add(new DataProcessBean(ib1));
					if(lstData.size()==subSize){
						boolean isSuccess=saveData(lstData);
						if(isSuccess){
							lstData.clear();
						}else{
							isFail=true;
							errMsg.append("导入失败");
							break flagAlipay;
						}
					}

				}
			}


			if (isFail){
				this.pData.clear();
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}else{
				String description="";
				//执行数据保存操作 使用事务，避免脏数据
				int subCount = lstData.size();
				if(subCount>0){
					int subPageTotal = (subCount / subSize) + ((subCount % subSize > 0) ? 1 : 0);
					StringBuffer sb=new StringBuffer(); 
					for (int i = 0, len = subPageTotal - 1; i <= len; i++) {
						// 分页计算
						int fromIndex = i * subSize;
						int toIndex = ((i == len) ? subCount : ((i + 1) * subSize));
						List<DataProcessBean> newList = lstData.subList(fromIndex, toIndex);
						boolean isSuccess=saveData(newList);
						if(isSuccess){
							continue;
						}else{
							sb.append("导入失败");
							if(i==0){
								sb.append("导入失败");
							}else if("1".equals(thirdType)){
								int successRow=rows-fromIndex-ignoreCount;
								sb.append("第2行至"+successRow+"行导入失败,请删除"+(successRow+1)+"行至结尾后继续导入");
							}else{
								int successRow=fromIndex+ignoreCount+2;
								sb.append("第"+successRow+"行起至结尾导入失败,请删除第2行至第"+(successRow-1)+"行后继续导入");
							}
							break;
						}
					}
					
					if(sb.length()>0){
						description=sb.toString();
						res.setSuccess(false);
						res.setServiceStatus("200");
						res.setServiceDescription(description);
					}else{
						description="导入成功";
						res.setSuccess(true);
						res.setServiceStatus("000");
						res.setServiceDescription(description);
					}
				}else{
					description="导入成功";
					res.setSuccess(true);
					res.setServiceStatus("000");
					res.setServiceDescription(description);
				}
			}

			this.doExecuteDataToDB();				
		} catch (BiffException e) {
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"对账导入执行失败",e);
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("文件读取失败,请另存为excel 97-2003 xls格式后重试");
		}catch (Exception e) {
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"对账导入执行失败",e);
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败：" + e.getMessage());
		}finally{
			workbook.close();
		}

	}

	public boolean saveData(List<DataProcessBean> lstData) throws Exception{
		boolean isSuccess=false;
		try{
			isSuccess=StaticInfo.dao.useTransactionProcessData(lstData);
		} catch (Exception e) {
		}
		return isSuccess;
	}


	public InsBean getInsBean(ArrayList<String> columnsFName,ArrayList<String> columnsFValue,ArrayList<Integer> columnsFType)throws Exception {
		DataValue[] columnsVal = new DataValue[columnsFName.size()];
		int insColCt = columnsFName.size();
		String[] columns1 = new String[insColCt];
		DataValue[] insValue = new DataValue[insColCt];
		int ENO1 = 0;
		for (int k = 0; k < columnsVal.length; k++) {
			String keyValue = columnsFName.get(k).toString();
			if (keyValue != null) {
				columnsVal[k] = new DataValue(keyValue, columnsFType.get(k));
			} else {
				columnsVal[k] = null;
			}

			if (columnsVal[k] != null) {
				columns1[ENO1] = columnsFName.get(k).toString();
				String fValue = columnsFValue.get(k).toString();
				columnsVal[k] = new DataValue(fValue, columnsFType.get(k));
				insValue[ENO1] = columnsVal[k];
				ENO1++;
				if (ENO1 >= insValue.length)
					break;
			}
		}
		InsBean ib1 = new InsBean("OC_STATEMENT", columns1);
		ib1.addValues(insValue);
		return ib1;
	}

	public ArrayList<String> getColumnsFNameList()throws Exception {
		ArrayList<String> columnsFName = new ArrayList<String>();
		columnsFName.add("EID");
		columnsFName.add("THIRDTYPE");
		//资料建立者		DATACREATEDBY
		columnsFName.add("DATACREATEDBY");
		//资料创建日		DATACREATEDDATE
		columnsFName.add("DATACREATEDDATE");
		//资料修改者		DATAMODIFIEDBY
		columnsFName.add("DATAMODIFIEDBY");

		//最近修改日		LASTMODIFIEDDATE
		columnsFName.add("LASTMODIFIEDDATE");
		//对账结果		ACCOUNTSTATUS
		columnsFName.add("ACCOUNTSTATUS");
		//使用自定义栏位1，作为标记，当导入资料产生脏数据时，可根据已导入单号查询导入时间，再根据时间删除本次导入数据
		columnsFName.add("CUSTOMFIELD1");

		return columnsFName;
	}
	public ArrayList<String> getColumnsFValueList(DCP_OrderStatementImportCreateReq req,String nowTime)throws Exception {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String nowDate=sdf.format(date);
		ArrayList<String> columnsFValue = new ArrayList<String>();
		columnsFValue.add(req.geteId());
		columnsFValue.add(req.getRequest().getThirdType());
		columnsFValue.add(req.getOpNO());
		columnsFValue.add(nowDate);
		columnsFValue.add(req.getOpNO());

		columnsFValue.add(nowDate);
		//1-未对账；2-已对账
		columnsFValue.add("1");
		columnsFValue.add(nowTime);
		return columnsFValue;
	}

	public ArrayList<Integer> getColumnsFTypeList()throws Exception {
		ArrayList<Integer> columnsFType = new ArrayList<Integer>();
		columnsFType.add(Types.VARCHAR);
		columnsFType.add(Types.VARCHAR);
		columnsFType.add(Types.VARCHAR);
		columnsFType.add(Types.VARCHAR);
		columnsFType.add(Types.VARCHAR);

		columnsFType.add(Types.VARCHAR);
		columnsFType.add(Types.VARCHAR);
		columnsFType.add(Types.VARCHAR);
		return columnsFType;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderStatementImportCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderStatementImportCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderStatementImportCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderStatementImportCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; //
		String thirdType = req.getRequest().getThirdType();
		if (Check.Null(thirdType)) {
			errCt++;
			errMsg.append("对账类型不可为空值, ");
			isFail = true;
		}
		String excelFileName = req.getRequest().getExcelFileName();
		if (Check.Null(excelFileName)) {
			errCt++;
			errMsg.append("excel文件名不可为空值, ");
			isFail = true;
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderStatementImportCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderStatementImportCreateReq>() {
		};
	}

	@Override
	protected DCP_OrderStatementImportCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderStatementImportCreateRes();
	}

	public String getFormatDate(String date,String format1,String format2)throws Exception {
		//yyyy-MM-dd HH:mm:ss  yyyy-MM-dd HH:mm:ss.SSS
		return new SimpleDateFormat(format2).format(new SimpleDateFormat(format1).parse(date));
	}

	public BigDecimal str2BigDecimal(String str)throws Exception {
		BigDecimal big=new BigDecimal(0);
		try{
			big=new BigDecimal(str.trim());
		}catch(Exception e){

		}
		return big;
	}

}
