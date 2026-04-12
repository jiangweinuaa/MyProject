package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PinPeiProcessReq;
import com.dsc.spos.json.cust.res.DCP_PinPeiProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_PinPeiProcess
 * 服务说明：拼胚确认
 * @author jinzma
 * @since  2020-07-13
 */
public class DCP_PinPeiProcess extends SPosAdvanceService<DCP_PinPeiProcessReq,DCP_PinPeiProcessRes>{

	@Override
	protected void processDUID(DCP_PinPeiProcessReq req, DCP_PinPeiProcessRes res) throws Exception {
		String eId=req.geteId();
		String shopId=req.getShopId();
		String pinPeiNo=req.getRequest().getPinPeiNo();
		boolean success;
		String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		try {
			String sql = " select a.stockinwearehouse,a.stockoutwearehouse,a.stockinbsno,a.stockoutbsno,a.memo,"
					+ " b.* from dcp_pinpei a"
					+ " inner join dcp_pinpei_detail b on a.eid=b.eid and a.shopid=b.shopid and a.pinpeino=b.pinpeino"
					+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.pinpeino='"+pinPeiNo+"' and a.status='0'";
			List<Map<String, Object>> getQData = this.doQueryData(sql,null);
			if (getQData != null && !getQData.isEmpty()) {
				String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
				String stockInWearehouse = getQData.get(0).get("STOCKINWEAREHOUSE").toString();
				String stockOutWearehouse = getQData.get(0).get("STOCKOUTWEAREHOUSE").toString();
				String stockInBsNo = getQData.get(0).get("STOCKINBSNO").toString();
				String stockOutBsNo = getQData.get(0).get("STOCKOUTBSNO").toString();
				String memo = getQData.get(0).get("MEMO").toString();
				String stockInNo = getStockInNo(eId,shopId,bDate);
				String stockOutNo = getStockOutNo(eId,shopId,bDate);

				//入库商品列表
				Map<String, Object> conditionValues = new HashMap<String, Object>(); //查詢條件
				conditionValues.put("STOCKTYPE", "STOCKIN");
				//调用过滤函数
				List<Map<String, Object>> getQStockIn=MapDistinct.getWhereMap(getQData, conditionValues, true);
				if (getQStockIn==null || getQStockIn.isEmpty()) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库商品不存在，请重新输入！");
				}
				//创建其他入库单及流水
				success = stockInCreate(req,getQStockIn,bDate,stockInWearehouse,stockInBsNo,stockInNo,memo);
				if (!success) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "创建其他入库单及流水异常！");
				}
				//出库商品列表
				conditionValues.clear();
				conditionValues.put("STOCKTYPE", "STOCKOUT");
				//调用过滤函数
				List<Map<String, Object>> getQStockOut=MapDistinct.getWhereMap(getQData, conditionValues, true);
				if (getQStockOut==null || getQStockOut.isEmpty()){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "出库商品不存在，请重新输入！");
				}
				//创建其他出库单及流水
				success = stockOutCreate(req,getQStockOut,bDate,stockOutWearehouse,stockOutBsNo,stockOutNo,memo);
				if (!success) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "创建其他出库单及流水异常！");
				}

				//回写拼胚单
				UptBean ub = new UptBean("DCP_PINPEI");
				ub.addUpdateValue("STOCKINNO", new DataValue(stockInNo, Types.VARCHAR));
				ub.addUpdateValue("STOCKOUTNO", new DataValue(stockOutNo, Types.VARCHAR));
				ub.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
				ub.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
				ub.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
				ub.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				ub.addCondition("PINPEINO", new DataValue(pinPeiNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			} else {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已提交，请重新输入！");
			}
		} catch (Exception e) {

			//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());

			//【ID1037668】【希悦-3.0】拼坯入库保存时报错：error executing work by jinzma 20240105
			String description=e.getMessage();
			try {
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);

				pw.flush();
				pw.close();

				errors.flush();
				errors.close();

				description = errors.toString();

				if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
					description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
				}

			} catch (Exception ignored) {

			}

			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);

		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PinPeiProcessReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PinPeiProcessReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PinPeiProcessReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PinPeiProcessReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		String pinPeiNo=req.getRequest().getPinPeiNo();
		if (Check.Null(pinPeiNo)) {
			errMsg.append("单据编号不能为空,");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_PinPeiProcessReq> getRequestType() {
		return new TypeToken<DCP_PinPeiProcessReq>(){};
	}

	@Override
	protected DCP_PinPeiProcessRes getResponseType() {
		return new DCP_PinPeiProcessRes();
	}

	private String getStockInNo(String eId,String shopId,String bDate) throws Exception{
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果doctype=0 则固定编码PSSH  如果doctype=1 则固定码为DBSH
		 */
		String stockInNo = "QTRK" + bDate;
		String sql = " select max(stockinno) as stockinno from dcp_stockin "
				+ " where shopid = '"+shopId+"' and eid ='"+eId+"' and organizationno = '"+shopId+"' "
				+ " and stockinno like '%%" + stockInNo + "%%' ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		stockInNo = getQData.get(0).get("STOCKINNO").toString();
		if (stockInNo != null && !stockInNo.isEmpty()) {
			stockInNo = stockInNo.substring(4);
			long i = Long.parseLong(stockInNo) + 1;
			stockInNo = "QTRK" + i + "";
		} else {
			stockInNo = "QTRK" + bDate + "00001";
		}
		return stockInNo;
	}

	private String getStockOutNo(String eId,String shopId,String bDate) throws Exception{
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果doctype=0 则固定编码PSSH  如果doctype=1 则固定码为DBSH
		 */
		String stockOutNo = "QTCK" + bDate;
		String sql = " select max(stockoutno) as stockoutno from dcp_stockout"
				+ " where organizationno ='"+shopId+"' and eid ='"+eId+"' and shopid ='"+shopId+"' "
				+ " and stockoutno like '%%" + stockOutNo + "%%' ";
		List<Map<String, Object>> getQData = this.doQueryData(sql,null);
		stockOutNo = getQData.get(0).get("STOCKOUTNO").toString();
		if (stockOutNo != null && !stockOutNo.isEmpty()) {
			stockOutNo = stockOutNo.substring(4);
			long i = Long.parseLong(stockOutNo) + 1;
			stockOutNo = "QTCK" + i + "";
		} else {
			stockOutNo = "QTCK" + bDate + "00001";
		}


		return stockOutNo;
	}

	private boolean stockInCreate(DCP_PinPeiProcessReq req,List<Map<String, Object>> stockIn,String bDate,String stockInWearehouse,String stockInBsNo,String stockInNo,String memo) {
		boolean success = true;
		try
		{
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sTime = df.format(cal.getTime());
			String opNo = req.getOpNO();
			String eId=req.geteId();
			String shopId=req.getShopId();
			String pinPeiNo=req.getRequest().getPinPeiNo();
			BigDecimal totPqty = new BigDecimal("0");
			BigDecimal totAmt = new BigDecimal("0");
			BigDecimal totDistriAmt = new BigDecimal("0");
			int totCqty = 0;


			String[] columns={
					"EID","SHOPID","ORGANIZATIONNO","STOCKINNO","BDATE",
					"MEMO","STATUS","DOC_TYPE", "OTYPE", "OFNO","BSNO", "WAREHOUSE",
					"CREATEBY","CREATE_DATE","CREATE_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
					"TOT_PQTY","TOT_AMT","TOT_CQTY","TOT_DISTRIAMT","PROCESS_STATUS","CREATE_CHATUSERID",
					"UPDATE_TIME","TRAN_TIME","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
					"SUBMITBY","SUBMIT_DATE","SUBMIT_TIME"};

			String[] columnsDetail={
					"EID","ORGANIZATIONNO","SHOPID","STOCKINNO","BDATE","WAREHOUSE",
					"ITEM","OITEM","PLUNO","BATCH_NO","PROD_DATE","PLU_MEMO",
					"PUNIT","PQTY","BASEUNIT","BASEQTY","UNIT_RATIO",
					"PRICE","AMT","DISTRIPRICE","DISTRIAMT","FEATURENO" };

			int item=1;
			for (Map<String, Object> oneData : stockIn)
			{
				String oItem=oneData.get("ITEM").toString();
				String pluNo=oneData.get("PLUNO").toString();
				String batchNo=oneData.get("BATCH_NO").toString();
				String prodDate=oneData.get("PROD_DATE").toString();
				String pluMemo=oneData.get("PLU_MEMO").toString();
				String punit=oneData.get("PUNIT").toString();
				String pqty=oneData.get("PQTY").toString();
				String baseUnit=oneData.get("BASEUNIT").toString();
				String baseQty=oneData.get("BASEQTY").toString();
				String unitRatio=oneData.get("UNIT_RATIO").toString();
				String price=oneData.get("PRICE").toString();
				String amt=oneData.get("AMT").toString();
				String distriPrice=oneData.get("DISTRIPRICE").toString();
				String distriAmt=oneData.get("DISTRIAMT").toString();
				String featureNo=oneData.get("FEATURENO").toString();
				if (Check.Null(featureNo))
					featureNo=" ";

				totPqty = totPqty.add(new BigDecimal(pqty));
				totAmt = totAmt.add(new BigDecimal(amt));
				totDistriAmt = totDistriAmt.add(new BigDecimal(distriAmt));
				totCqty = totCqty + 1;

				//写入其他入库单单身
				DataValue[]	insValueDetail = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(stockInNo, Types.VARCHAR),
						new DataValue(bDate, Types.VARCHAR),
						new DataValue(stockInWearehouse, Types.VARCHAR),
						new DataValue(item, Types.VARCHAR),
						new DataValue(oItem, Types.VARCHAR),
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(batchNo, Types.VARCHAR),
						new DataValue(prodDate, Types.VARCHAR),
						new DataValue(pluMemo, Types.VARCHAR),
						new DataValue(punit, Types.VARCHAR),
						new DataValue(pqty, Types.VARCHAR),
						new DataValue(baseUnit, Types.VARCHAR),
						new DataValue(baseQty, Types.VARCHAR),
						new DataValue(unitRatio, Types.VARCHAR),
						new DataValue(price, Types.VARCHAR),
						new DataValue(amt, Types.VARCHAR),
						new DataValue(distriPrice, Types.VARCHAR),
						new DataValue(distriAmt, Types.VARCHAR),
						new DataValue(featureNo, Types.VARCHAR),
				};
				InsBean ibDetail = new InsBean("DCP_STOCKIN_DETAIL", columnsDetail);
				ibDetail.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ibDetail));

				///写入库存流水
				String procedure="SP_DCP_StockChange";
				Map<Integer,Object> inputParameter = new HashMap<>();
				inputParameter.put(1,eId);                       //--企业ID
				inputParameter.put(2,shopId);                    //--组织
				inputParameter.put(3,"14");                      //--单据类型
				inputParameter.put(4,stockInNo);	               //--单据号
				inputParameter.put(5,item);                      //--单据行号
				inputParameter.put(6,"1");                       //--异动方向 1=加库存 -1=减库存
				inputParameter.put(7,bDate);                     //--营业日期 yyyy-MM-dd
				inputParameter.put(8,pluNo);                     //--品号
				inputParameter.put(9,featureNo);                 //--特征码
				inputParameter.put(10,stockInWearehouse);        //--仓库	
				inputParameter.put(11,batchNo);                  //--批号
				inputParameter.put(12,punit);                    //--交易单位
				inputParameter.put(13,pqty);                     //--交易数量
				inputParameter.put(14,baseUnit);                 //--基准单位
				inputParameter.put(15,baseQty);                  //--基准数量	
				inputParameter.put(16,unitRatio);                //--换算比例	
				inputParameter.put(17,price);                    //--零售价
				inputParameter.put(18,amt);                      //--零售金额
				inputParameter.put(19,distriPrice);              //--进货价
				inputParameter.put(20,distriAmt);                //--进货金额
				inputParameter.put(21,bDate);                    //--入账日期 yyyy-MM-dd
				inputParameter.put(22,prodDate);                 //--批号的生产日期 yyyy-MM-dd
				inputParameter.put(23,bDate);                    //--单据日期
				inputParameter.put(24,stockInBsNo);              //--异动原因
				inputParameter.put(25,pluMemo);                  //--异动描述
				inputParameter.put(26,opNo);                     //--操作员

				ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
				this.addProcessData(new DataProcessBean(pdb));

				item++;
			}

			//写入其他入库单单头			
			DataValue[]	insValue = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(shopId, Types.VARCHAR),
					new DataValue(shopId, Types.VARCHAR),
					new DataValue(stockInNo, Types.VARCHAR),
					new DataValue(bDate, Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue("2", Types.VARCHAR),
					new DataValue("3", Types.VARCHAR),
					new DataValue("1", Types.VARCHAR),
					new DataValue(pinPeiNo, Types.VARCHAR),
					new DataValue(stockInBsNo, Types.VARCHAR),
					new DataValue(stockInWearehouse, Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(sDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(bDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
					new DataValue(totPqty.toPlainString(), Types.VARCHAR),
					new DataValue(totAmt.toPlainString(), Types.VARCHAR),
					new DataValue(totCqty, Types.VARCHAR),
					new DataValue(totDistriAmt.toPlainString(), Types.VARCHAR),
					new DataValue("N", Types.VARCHAR),
					new DataValue(req.getChatUserId(), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(sDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(sDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
			};
			InsBean ib = new InsBean("DCP_STOCKIN", columns);
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib));
		}
		catch (Exception e)
		{
			success=false;
		}
		return success;
	}

	private boolean stockOutCreate(DCP_PinPeiProcessReq req,List<Map<String, Object>> stockOut,String bDate,String stockOutWearehouse,String stockOutBsNo,String stockOutNo,String memo) {
		boolean success = true;
		try {
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sTime = df.format(cal.getTime());
			String opNo = req.getOpNO();
			String eId=req.geteId();
			String shopId=req.getShopId();
			String pinPeiNo=req.getRequest().getPinPeiNo();
			BigDecimal totPqty = new BigDecimal("0");
			BigDecimal totAmt = new BigDecimal("0");
			BigDecimal totDistriAmt = new BigDecimal("0");
			int totCqty = 0;

			String[] columns={
					"EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","BDATE",
					"MEMO","STATUS","DOC_TYPE","OTYPE","OFNO","BSNO","WAREHOUSE",
					"CREATEBY","CREATE_DATE","CREATE_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
					"TOT_PQTY","TOT_AMT","TOT_CQTY","TOT_DISTRIAMT","PROCESS_STATUS",
					"ACCOUNT_CHATUSERID","UPDATE_TIME","TRAN_TIME","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
					"SUBMITBY","SUBMIT_DATE","SUBMIT_TIME"
			};

			String[] columnsDetail={
					"EID","ORGANIZATIONNO","SHOPID","STOCKOUTNO","BDATE","WAREHOUSE",
					"ITEM","OITEM","PLUNO","BATCH_NO","PROD_DATE","PLU_MEMO",
					"PUNIT","PQTY","BASEUNIT","BASEQTY","UNIT_RATIO",
					"PRICE","AMT","DISTRIPRICE","DISTRIAMT","BSNO","FEATURENO" };

			int item=1;
			for (Map<String, Object> oneData : stockOut)
			{
				String oItem=oneData.get("ITEM").toString();
				String pluNo=oneData.get("PLUNO").toString();
				String batchNo=oneData.get("BATCH_NO").toString();
				String prodDate=oneData.get("PROD_DATE").toString();
				String pluMemo=oneData.get("PLU_MEMO").toString();
				String punit=oneData.get("PUNIT").toString();
				String pqty=oneData.get("PQTY").toString();
				String baseUnit=oneData.get("BASEUNIT").toString();
				String baseQty=oneData.get("BASEQTY").toString();
				String unitRatio=oneData.get("UNIT_RATIO").toString();
				String price=oneData.get("PRICE").toString();
				String amt=oneData.get("AMT").toString();
				String distriPrice=oneData.get("DISTRIPRICE").toString();
				String distriAmt=oneData.get("DISTRIAMT").toString();
				String featureNo=oneData.get("FEATURENO").toString();
				if (Check.Null(featureNo))
					featureNo=" ";

				totPqty = totPqty.add(new BigDecimal(pqty));
				totAmt = totAmt.add(new BigDecimal(amt));
				totDistriAmt = totDistriAmt.add(new BigDecimal(distriAmt));
				totCqty = totCqty + 1;

				//写入其他出库单单身
				DataValue[]	insValueDetail = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(stockOutNo, Types.VARCHAR),
						new DataValue(bDate, Types.VARCHAR),
						new DataValue(stockOutWearehouse, Types.VARCHAR),
						new DataValue(item, Types.VARCHAR),
						new DataValue(oItem, Types.VARCHAR),
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(batchNo, Types.VARCHAR),
						new DataValue(prodDate, Types.VARCHAR),
						new DataValue(pluMemo, Types.VARCHAR),
						new DataValue(punit, Types.VARCHAR),
						new DataValue(pqty, Types.VARCHAR),
						new DataValue(baseUnit, Types.VARCHAR),
						new DataValue(baseQty, Types.VARCHAR),
						new DataValue(unitRatio, Types.VARCHAR),
						new DataValue(price, Types.VARCHAR),
						new DataValue(amt, Types.VARCHAR),
						new DataValue(distriPrice, Types.VARCHAR),
						new DataValue(distriAmt, Types.VARCHAR),
						new DataValue(stockOutBsNo, Types.VARCHAR),
						new DataValue(featureNo, Types.VARCHAR),
				};
				InsBean ibDetail = new InsBean("DCP_STOCKOUT_DETAIL", columnsDetail);
				ibDetail.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ibDetail));

				///写入库存流水
				String procedure="SP_DCP_StockChange";
				Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
				inputParameter.put(1,eId);                       //--企业ID
				inputParameter.put(2,shopId);                    //--组织
				inputParameter.put(3,"15");                      //--单据类型
				inputParameter.put(4,stockOutNo);	             //--单据号
				inputParameter.put(5,item);                      //--单据行号
				inputParameter.put(6,"-1");                      //--异动方向 1=加库存 -1=减库存
				inputParameter.put(7,bDate);                     //--营业日期 yyyy-MM-dd
				inputParameter.put(8,pluNo);                     //--品号
				inputParameter.put(9,featureNo);                 //--特征码
				inputParameter.put(10,stockOutWearehouse);       //--仓库	
				inputParameter.put(11,batchNo);                  //--批号
				inputParameter.put(12,punit);                    //--交易单位
				inputParameter.put(13,pqty);                     //--交易数量
				inputParameter.put(14,baseUnit);                 //--基准单位
				inputParameter.put(15,baseQty);                  //--基准数量	
				inputParameter.put(16,unitRatio);                //--换算比例	
				inputParameter.put(17,price);                    //--零售价
				inputParameter.put(18,amt);                      //--零售金额
				inputParameter.put(19,distriPrice);              //--进货价
				inputParameter.put(20,distriAmt);                //--进货金额
				inputParameter.put(21,bDate);                    //--入账日期 yyyy-MM-dd
				inputParameter.put(22,prodDate);                 //--批号的生产日期 yyyy-MM-dd
				inputParameter.put(23,bDate);                    //--单据日期
				inputParameter.put(24,stockOutBsNo);             //--异动原因
				inputParameter.put(25,pluMemo);                  //--异动描述
				inputParameter.put(26,opNo);                     //--操作员

				ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
				this.addProcessData(new DataProcessBean(pdb));

				item++;
			}

			//写入其他出库单单头
			DataValue[]	insValue = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(shopId, Types.VARCHAR),
					new DataValue(shopId, Types.VARCHAR),
					new DataValue(stockOutNo, Types.VARCHAR),
					new DataValue(bDate, Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue("2", Types.VARCHAR),
					new DataValue("3", Types.VARCHAR),
					new DataValue("1", Types.VARCHAR),
					new DataValue(pinPeiNo, Types.VARCHAR),
					new DataValue(stockOutBsNo, Types.VARCHAR) ,
					new DataValue(stockOutWearehouse, Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(sDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(bDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
					new DataValue(totPqty.toPlainString(), Types.VARCHAR),
					new DataValue(totAmt.toPlainString(), Types.VARCHAR),
					new DataValue(totCqty, Types.VARCHAR),
					new DataValue(totDistriAmt.toPlainString(), Types.VARCHAR),
					new DataValue("N", Types.VARCHAR),
					new DataValue(req.getChatUserId(), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(sDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(sDate, Types.VARCHAR),
					new DataValue(sTime, Types.VARCHAR),
			};
			InsBean ib = new InsBean("DCP_STOCKOUT", columns);
			ib.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib));

		} catch (Exception e) {
			success=false;
		}
		return success;
	}

}
