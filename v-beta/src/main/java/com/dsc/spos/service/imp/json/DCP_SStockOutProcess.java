package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataProcessReq;
import com.dsc.spos.json.cust.req.DCP_SStockOutProcessReq;
import com.dsc.spos.json.cust.req.DCP_SStockOutProcessReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataProcessRes;
import com.dsc.spos.json.cust.res.DCP_SStockOutProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.batchLocation.BatchLocationPlu;
import com.dsc.spos.utils.batchLocation.BatchLocationStockAlloc;
import com.dsc.spos.utils.batchLocation.WarehouseLocationPlu;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.dsc.spos.utils.tax.TaxAmount;
import com.dsc.spos.utils.tax.TaxAmountCalculation;
import com.google.gson.reflect.TypeToken;
/**
 * 服務函數：SStockOutProcess
 *   說明：自采出库处理
 * 服务说明：自采出库处理
 * @author JZMA
 * @since  2018-11-20
 */
public class DCP_SStockOutProcess extends SPosAdvanceService<DCP_SStockOutProcessReq, DCP_SStockOutProcessRes>{

	@Override
	protected boolean isVerifyFail(DCP_SStockOutProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		levelElm request = req.getRequest();
		String sStockOutNO = request.getsStockOutNo();

		if(Check.Null(sStockOutNO)){
			errMsg.append("自采出库单号不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected void processDUID(DCP_SStockOutProcessReq req, DCP_SStockOutProcessRes res) throws Exception {
		// TODO Auto-generated method stub
		String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String confirmBy = req.getEmployeeNo();
		String submitBy = req.getEmployeeNo();
		String accountBy = req.getEmployeeNo();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String confirmDate = df.format(cal.getTime());
		String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		String submitDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String confirmTime = df.format(cal.getTime());
		String accountTime = df.format(cal.getTime());
		String submitTime = df.format(cal.getTime());

		levelElm request = req.getRequest();
		String sStockOutNO = request.getsStockOutNo();
        String opType = request.getOpType();//post-过账,cancel-作废



        //try
		//{
			String sstockoutSql=getSStockoutInfoSql(req);
			List<Map<String, Object>> stockoutData=this.doQueryData(sstockoutSql, null);
			String transferInfoSql = getTransferInfoSql(req);
			List<Map<String, Object>> transferData=this.doQueryData(transferInfoSql, null);
			if (stockoutData == null || stockoutData.isEmpty())
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"出库单不存在");
			}
			Map<String, Object> stockoutMap=stockoutData.get(0);
			String status = stockoutMap.get("STATUS").toString();
        String corp = stockoutMap.get("CORP").toString();
        String bizOrgCorp = stockoutMap.get("BIZCORP").toString();
        String currency = stockoutData.get(0).get("CURRENCY").toString();
        String bizIcCostWarehouse=stockoutData.get(0).get("ICCOSTWAREHOUSE").toString();


        //加入库存流水账信息
        // IF 参数是否启用批号==N ，库存流水的批号和日期字段不给值 BY JZMA 20191101
        String isBatchPara = PosPub.getPARA_SMS(dao, eId, req.getOrganizationNO(), "Is_BatchNO");
        if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")){
            isBatchPara="N";
        }

        String isBatchParaBiz = PosPub.getPARA_SMS(dao, eId, bizOrgCorp, "Is_BatchNO");
        if (Check.Null(isBatchParaBiz) || !isBatchParaBiz.equals("Y")){
            isBatchParaBiz="N";
        }

        List<Map<String, Object>> interSettleDataList=new ArrayList<>();
        if(!corp.equals(bizOrgCorp)){
            String interSql="select * from DCP_INTERSETTLE_DETAIL a" +
                    " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.billno='"+sStockOutNO+"' ";
            interSettleDataList=this.doQueryData(interSql, null);
        }

        int amountDigit = 2;
        if (Check.NotNull(currency)) {
            String currencySql = "select * from DCP_CURRENCY where eid='" + eId + "' and" +
                    " CURRENCY='" + currency + "' ";
            List<Map<String, Object>> currencies = this.doQueryData(currencySql, null);
            if (currencies.size() <= 0) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "币种:" + currency + "不存在!");
            }
            amountDigit = Integer.parseInt(currencies.get(0).get("AMOUNTDIGIT").toString());
        }
        if (opType.equals(Constant.OPR_TYPE_POST))
			{
				if (!status.equals("0"))
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"非新建不可过账");
				}

                Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                }


				String sql=this.GetDCP_SSTOCKOUT_SQL(req);
				List<Map<String, Object>> getQData=this.doQueryData(sql,null);
				if (getQData != null && getQData.isEmpty() == false)
				{
					//如果是退货出库并且ofno有值的需要去判断一下原单的数量等是否满足，并且需要更新原单的已退货数量
					String ofNO=getQData.get(0).get("OFNO").toString();
                    String supplier=getQData.get(0).get("SUPPLIER").toString();
                    String customer=getQData.get(0).get("CUSTOMER").toString();
					String bDate = getQData.get(0).get("BDATE").toString();
					String stockOutType = getQData.get(0).get("STOCKOUTTYPE").toString();

                    String stockBillType="06";
                    if(stockOutType.equals("1")){
                        stockBillType="06";
                    }else{
                        stockBillType="20";
                    }

                    String bizPartner=stockOutType.equals("1")?supplier:customer;

                    //重新分配批号
                    DelBean db3 = new DelBean("DCP_SSTOCKOUT_BATCH");
                    db3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db3.addCondition("SSTOCKOUTNO", new DataValue(sStockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db3));
                    String sBatchSql = "select a.* ,NVL(uc1.UNIT_RATIO,1) UNIT_RATIO,d.price,d.DISTRIPRICE,d.taxcaltype,d.incltax,d.taxcode,d.taxrate,d.PURPRICE,d.custprice " +
                            " from DCP_SSTOCKOUT_BATCH a " +
                            " LEFT JOIN DCP_UNITCONVERT uc1 on uc1.eid=a.eid and uc1.OUNIT=a.PUNIT AND uc1.UNIT=a.BASEUNIT " +
                            " left join DCP_SSTOCKOUT_DETAIL d on d.eid=a.eid and d.organizationno=a.organizationno and d.sstockoutno=a.sstockoutno and d.item=a.item2 " +
                            " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' " +
                            " and a.sStockoutno='" + sStockOutNO + "' ";
                    List<Map<String, Object>> batchData=this.doQueryData(sBatchSql, null);
                    List<BatchLocationPlu> batchLocationPlus = new ArrayList<>();
                    for (Map<String, Object> singleBatch : batchData) {
                        BatchLocationPlu onePlu = new BatchLocationPlu();
                        onePlu.setId(Integer.parseInt(singleBatch.get("ITEM").toString()));
                        onePlu.setWarehouse(singleBatch.get("WAREHOUSE").toString());
                        onePlu.setPQty(singleBatch.get("PQTY").toString());
                        onePlu.setPUnit(singleBatch.get("PUNIT").toString());
                        onePlu.setPluNo(singleBatch.get("PLUNO").toString());
                        onePlu.setFeatureNo(singleBatch.get("FEATURENO").toString());

                        onePlu.setBatchNo(singleBatch.get("BATCHNO").toString());
                        onePlu.setLocation(singleBatch.get("LOCATION").toString());

                        batchLocationPlus.add(onePlu);
                    }

                    List<WarehouseLocationPlu> allocList = BatchLocationStockAlloc.batchLocationStockAlloc(batchLocationPlus);
                    for (WarehouseLocationPlu oneAlloc : allocList) {
                        if (Check.NotNull(oneAlloc.getProdDate())) {
                            oneAlloc.setProdDate(new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oneAlloc.getProdDate())));
                        }
                        if (Check.NotNull(oneAlloc.getValidDate())) {
                            oneAlloc.setValidDate(new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oneAlloc.getValidDate())));
                        }
                    }

                    int batchItem = 0;
                    for (Map<String, Object> row : batchData) {
                        String thisItem = row.get("ITEM").toString();
                        String thisItem2 = row.get("ITEM2").toString();
                        String thisPluNo = row.get("PLUNO").toString();
                        String thisFeatureNo = row.get("FEATURENO").toString();
                        String thisWarehouse = row.get("WAREHOUSE").toString();
                        String thisLocation = row.get("LOCATION").toString();
                        String thisBatchNo = row.get("BATCHNO").toString();
                        String thisProdDate = row.get("PRODDATE").toString();
                        String thisExpDate = row.get("EXPDATE").toString();
                        String thisPUnit = row.get("PUNIT").toString();
                        String thisPQty = row.get("PQTY").toString();
                        String thisBaseUnit = row.get("BASEUNIT").toString();
                        String thisBaseQty = row.get("BASEQTY").toString();
                        String thisUnitRatio = row.get("UNIT_RATIO").toString();
                        double amt = BigDecimalUtils.mul(Double.parseDouble(row.get("PRICE").toString()), Double.parseDouble(row.get("PQTY").toString()), 2);
                        double distriAmt = BigDecimalUtils.mul(Double.parseDouble(row.get("DISTRIPRICE").toString()), Double.parseDouble(row.get("PQTY").toString()), 2);

                        String distriPrice = row.get("DISTRIPRICE").toString();
                        String purPrice = row.get("PURPRICE").toString();//采购价格
                        String custPrice = row.get("CUSTPRICE").toString();//大客销售价格
                        String taxCalType = row.get("TAXCALTYPE").toString();
                        String taxCode = row.get("TAXCODE").toString();
                        String taxRate = row.get("TAXRATE").toString();
                        String inclTax = row.get("INCLTAX").toString();


                        List<WarehouseLocationPlu> filterAlloc = allocList.stream().filter(x -> String.valueOf(x.getId()).equals(thisItem)).collect(Collectors.toList());
                        if (!filterAlloc.isEmpty()&&Check.Null(thisBatchNo)) {
                            for (WarehouseLocationPlu onePlu : filterAlloc) {
                                batchItem++;

                                Map<String, Object> baseMap = PosPub.getBaseQty(this.dao, req.geteId(), onePlu.getPluNo(), onePlu.getPUnit(), onePlu.getAllocQty());

                                String baseQty = baseMap.get("baseQty").toString();

                                thisBatchNo=onePlu.getBatchNo();

                                ColumnDataValue batchColumns = new ColumnDataValue();
                                batchColumns.add("EID", DataValues.newString(eId));
                                batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                batchColumns.add("SSTOCKOUTNO", DataValues.newString(sStockOutNO));
                                batchColumns.add("ITEM", DataValues.newString(batchItem));
                                batchColumns.add("ITEM2", DataValues.newString(thisItem2));
                                batchColumns.add("PLUNO", DataValues.newString(onePlu.getPluNo()));
                                batchColumns.add("FEATURENO", DataValues.newString(thisFeatureNo));
                                batchColumns.add("WAREHOUSE", DataValues.newString(onePlu.getWarehouse()));
                                batchColumns.add("LOCATION", DataValues.newString(onePlu.getLocation()));
                                batchColumns.add("BATCHNO", DataValues.newString(thisBatchNo));
                                batchColumns.add("PRODDATE", DataValues.newString(onePlu.getProdDate()));
                                batchColumns.add("EXPDATE", DataValues.newString(onePlu.getValidDate()));
                                batchColumns.add("PUNIT", DataValues.newString(onePlu.getPUnit()));
                                batchColumns.add("PQTY", DataValues.newString(onePlu.getAllocQty()));
                                batchColumns.add("BASEUNIT", DataValues.newString(thisBaseUnit));
                                batchColumns.add("BASEQTY", DataValues.newString(baseQty));
                                batchColumns.add("UNITRATIO", DataValues.newString(thisUnitRatio));

                                amt = BigDecimalUtils.mul(Double.parseDouble(row.get("PRICE").toString()), Double.parseDouble(onePlu.getAllocQty()), 2);
                                distriAmt = BigDecimalUtils.mul(Double.parseDouble(row.get("DISTRIPRICE").toString()), Double.parseDouble(onePlu.getAllocQty()), 2);

                                String batchPrice=stockOutType.equals("1") ? purPrice:custPrice;
                                BigDecimal batchAmount = new BigDecimal(batchPrice).multiply(new BigDecimal(onePlu.getAllocQty()));
                                String batchIcPrice=distriPrice;
                                BigDecimal batchIcAmount = new BigDecimal(batchIcPrice).multiply(new BigDecimal(onePlu.getAllocQty()));
                                //stockOutType=1 采购 ： 客户

                                TaxAmount taxAmount = TaxAmountCalculation.calculateAmount(
                                        "Y".equals(inclTax),
                                        Double.parseDouble(batchAmount.toString()),
                                        Double.parseDouble(taxRate),
                                        taxCalType,
                                        amountDigit
                                );

                                batchColumns.add("PRICE", DataValues.newString(batchPrice));
                                batchColumns.add("AMOUNT", DataValues.newString(batchAmount));
                                batchColumns.add("PRETAXAMT", DataValues.newString(taxAmount.getPreAmount()));
                                batchColumns.add("TAXAMT", DataValues.newString(taxAmount.getTaxAmount()));
                                batchColumns.add("TAXCODE", DataValues.newString(taxCode));
                                batchColumns.add("TAXRATE", DataValues.newString(taxRate));
                                batchColumns.add("IC_PRICE", DataValues.newString(batchIcPrice));
                                batchColumns.add("IC_AMOUNT", DataValues.newString(batchIcAmount));

                                String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                                DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibb = new InsBean("DCP_SSTOCKOUT_BATCH", batchColumnNames);
                                ibb.addValues(batchDataValues);
                                this.addProcessData(new DataProcessBean(ibb));

                                BcReq bcReq=new BcReq();
                                bcReq.setServiceType("SStockOutProcess");
                                bcReq.setStockInOutType(stockOutType);
                                bcReq.setBillType(stockBillType);
                                BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                                if(Check.Null(bcMap.getBType())||Check.Null(bcMap.getCostCode())){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                                }

                                //1.同法人采购：
                                //进货价 = 采购价DCP_SSTOCKOUT_BATCH.PRICE,
                                //进货金额（含税）=采购金额DCP_SSTOCKOUT_BATCH.AMOUNT,
                                //未税金额=未税金额DCP_SSTOCKOUT_BATCH.PRETAXAMT,
                                //税额 = 税额DCP_SSTOCKOUT_BATCH.TAXAMT,
                                //税别 = 税别DCP_SSTOCKOUT_BATCH.TAXCODE,
                                //税率 = 税率DCP_SSTOCKOUT_DETAIL.TAXRATE;
                                //
                                String stockDisPrice=batchPrice;
                                String stockDisAmt=batchAmount.toString();
                                String stockTaxAmt="";
                                String stockPreTaxAmt="";

                                if("1".equals(stockOutType)) {
                                    if (!corp.equals(bizOrgCorp)) {
                                        //2.跨法人采购：
                                        //进货价 = 内部采购进货价DCP_INTERSETTLE_DETAIL.RECEIVEPRICE,
                                        //进货金额（含税）=内部采购进货金额DCP_INTERSETTLE_DETAIL.RECEIVEAMT,
                                        //未税金额 = 内部采购未税金额DCP_INTERSETTLE_DETAIL.PRETAXAMT,
                                        //税额 = 内部采购未税金额DCP_INTERSETTLE_DETAIL.TAXAMT,
                                        //税别编码 = 内部采购税别编码DCP_INTERSETTLE_DETAIL.TAXCODE,
                                        //税率%= 内部采购税率DCP_INTERSETTLE_DETAIL.TAXRATE;
                                        //价格重取  再重算
                                        List<Map<String, Object>> filterInterRows = interSettleDataList.stream().filter(x -> x.get("ITEM").toString().equals(thisItem2)).collect(Collectors.toList());
                                        if (filterInterRows.size() <= 0) {
                                            //不同法人 取不到内部结算数据 就有问题
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到内部结算数据！");
                                        }
                                        String receivePrice = filterInterRows.get(0).get("RECEIVEPRICE").toString();
                                        batchAmount = new BigDecimal(receivePrice).multiply(new BigDecimal(onePlu.getAllocQty()));

                                        stockDisPrice = receivePrice;
                                        stockDisAmt = batchAmount.toString();

                                        taxCode = filterInterRows.get(0).get("TAXCODE").toString();
                                        taxRate = filterInterRows.get(0).get("TAXRATE").toString();
                                        taxAmount = TaxAmountCalculation.calculateAmount(
                                                "Y".equals(inclTax),
                                                Double.parseDouble(batchAmount.toString()),
                                                Double.parseDouble(Check.Null(taxRate)?"0":taxRate),
                                                taxCalType,
                                                amountDigit
                                        );
                                        stockTaxAmt=String.valueOf(taxAmount.getTaxAmount());
                                        stockPreTaxAmt=String.valueOf(taxAmount.getPreAmount());
                                    }
                                    else{
                                        //同法人
                                        stockTaxAmt=String.valueOf(taxAmount.getTaxAmount());
                                        stockPreTaxAmt=String.valueOf(taxAmount.getPreAmount());
                                    }
                                }else{
                                    stockDisPrice="0";
                                    stockDisAmt="0";
                                    taxCode="";
                                    taxRate="0";
                                    stockTaxAmt="0";
                                    stockPreTaxAmt="0";
                                }

                                String procedure = "SP_DCP_STOCKCHANGE_V35";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                            //--企业ID
                                inputParameter.put(2, null);
                                inputParameter.put(3, organizationNO);                                         //--组织
                                inputParameter.put(4, bcMap.getBType());
                                inputParameter.put(5, bcMap.getCostCode());
                                inputParameter.put(6, stockBillType);                                //--单据类型
                                inputParameter.put(7, sStockOutNO);                                        //--单据号
                                inputParameter.put(8, thisItem2);
                                inputParameter.put(9, "0");
                                inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, thisPluNo);         //--品号
                                inputParameter.put(13, thisFeatureNo);                                   //--特征码
                                inputParameter.put(14, thisWarehouse);    //--仓库
                                inputParameter.put(15, thisBatchNo);     //批号
                                inputParameter.put(16, onePlu.getLocation());     //--location
                                inputParameter.put(17, onePlu.getPUnit());        //--交易单位
                                inputParameter.put(18, onePlu.getAllocQty());         //--交易数量
                                inputParameter.put(19, thisBaseUnit);     //--基准单位
                                inputParameter.put(20, baseQty);      //--基准数量
                                inputParameter.put(21, thisUnitRatio);   //--换算比例
                                inputParameter.put(22, row.get("PRICE").toString());        //--零售价
                                inputParameter.put(23, amt);          //--零售金额
                                inputParameter.put(24, stockDisPrice);  //--进货价
                                inputParameter.put(25, stockDisAmt);    //--进货金额

                                inputParameter.put(26,stockPreTaxAmt);
                                inputParameter.put(27,stockTaxAmt);
                                inputParameter.put(28,taxCode);
                                inputParameter.put(29,taxRate);

                                inputParameter.put(30, accountDate);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(31, onePlu.getProdDate());    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(32, bDate);            //--单据日期
                                inputParameter.put(33, "销货/退货出库扣库存");             //--异动原因
                                inputParameter.put(34, "");             //--异动描述
                                inputParameter.put(35, req.getOpNO());          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));

                                if(!corp.equals(bizOrgCorp)){

                                    String bizBatchNo = "";
                                    if ("Y".equals(isBatchParaBiz)) {
                                        if (Check.NotNull(thisBatchNo)) {
                                            bizBatchNo = thisBatchNo;
                                        } else {
                                            bizBatchNo = "DEFAULTBATCH";
                                        }
                                    }

                                    if("1".equals(stockOutType)) {
                                        stockDisPrice=batchPrice;
                                        stockDisAmt=batchAmount.toString();
                                        stockTaxAmt=String.valueOf(taxAmount.getTaxAmount());
                                        stockPreTaxAmt=String.valueOf(taxAmount.getPreAmount());
                                    }else{
                                        stockDisPrice="0";
                                        stockDisAmt="0";
                                        taxCode="";
                                        taxRate="0";
                                        stockTaxAmt="0";
                                        stockPreTaxAmt="0";
                                    }

                                    Map<Integer, Object> inputParameter2 = new HashMap<Integer, Object>();
                                    inputParameter2.put(1, eId);                                            //--企业ID
                                    inputParameter2.put(2, null);
                                    inputParameter2.put(3, bizOrgCorp);                                         //--组织
                                    if ("1".equals(stockOutType)) {
                                        inputParameter2.put(4, "06");
                                        inputParameter2.put(5, "S");
                                        inputParameter2.put(6, "06");
                                    }else{
                                        inputParameter2.put(4, "20");
                                        inputParameter2.put(5, "U");
                                        inputParameter2.put(6, "20");
                                    }                        //--单据类型
                                    inputParameter2.put(7, sStockOutNO);                                        //--单据号
                                    inputParameter2.put(8, thisItem2);
                                    inputParameter2.put(9, "0");
                                    inputParameter2.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                    inputParameter2.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                    inputParameter2.put(12, thisPluNo);         //--品号
                                    inputParameter2.put(13, thisFeatureNo);                                   //--特征码
                                    inputParameter2.put(14, bizIcCostWarehouse);    //--仓库
                                    inputParameter2.put(15, thisBatchNo);     //批号
                                    inputParameter2.put(16, " ");     //--location
                                    inputParameter2.put(17, onePlu.getPUnit());        //--交易单位
                                    inputParameter2.put(18, onePlu.getAllocQty());         //--交易数量
                                    inputParameter2.put(19, thisBaseUnit);     //--基准单位
                                    inputParameter2.put(20, baseQty);      //--基准数量
                                    inputParameter2.put(21, thisUnitRatio);   //--换算比例
                                    inputParameter2.put(22, row.get("PRICE").toString());        //--零售价
                                    inputParameter2.put(23, amt);          //--零售金额
                                    inputParameter2.put(24, stockDisPrice);  //--进货价
                                    inputParameter2.put(25, stockDisAmt);    //--进货金额

                                    inputParameter2.put(26,stockPreTaxAmt);
                                    inputParameter2.put(27,stockTaxAmt);
                                    inputParameter2.put(28,taxCode);
                                    inputParameter2.put(29,taxRate);

                                    inputParameter2.put(30, accountDate);                                   //--入账日期 yyyy-MM-dd
                                    inputParameter2.put(31, onePlu.getProdDate());    //--批号的生产日期 yyyy-MM-dd
                                    inputParameter2.put(32, bDate);            //--单据日期
                                    inputParameter2.put(33, "销货/退货出库扣库存");             //--异动原因
                                    inputParameter2.put(34, "");             //--异动描述
                                    inputParameter2.put(35, req.getOpNO());          //--操作员

                                    ProcedureBean pdb2 = new ProcedureBean(procedure, inputParameter2);
                                    this.addProcessData(new DataProcessBean(pdb2));
                                }


                            }
                        }
                        else {
                            batchItem++;
                            ColumnDataValue batchColumns = new ColumnDataValue();
                            batchColumns.add("EID", DataValues.newString(eId));
                            batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                            batchColumns.add("SSTOCKOUTNO", DataValues.newString(sStockOutNO));
                            batchColumns.add("ITEM", DataValues.newString(batchItem));
                            batchColumns.add("ITEM2", DataValues.newString(thisItem2));
                            batchColumns.add("PLUNO", DataValues.newString(thisPluNo));
                            batchColumns.add("FEATURENO", DataValues.newString(thisFeatureNo));
                            batchColumns.add("WAREHOUSE", DataValues.newString(thisWarehouse));
                            batchColumns.add("LOCATION", DataValues.newString(thisLocation));
                            batchColumns.add("BATCHNO", DataValues.newString(thisBatchNo));
                            batchColumns.add("PRODDATE", DataValues.newString(thisProdDate));
                            batchColumns.add("EXPDATE", DataValues.newString(thisExpDate));
                            batchColumns.add("PUNIT", DataValues.newString(thisPUnit));
                            batchColumns.add("PQTY", DataValues.newString(thisPQty));
                            batchColumns.add("BASEUNIT", DataValues.newString(thisBaseUnit));
                            batchColumns.add("BASEQTY", DataValues.newString(thisBaseQty));
                            batchColumns.add("UNITRATIO", DataValues.newString(thisUnitRatio));


                            String batchPrice=stockOutType.equals("1") ? purPrice:custPrice;
                            BigDecimal batchAmount = new BigDecimal(batchPrice).multiply(new BigDecimal(thisPQty));
                            String batchIcPrice=distriPrice;
                            BigDecimal batchIcAmount = new BigDecimal(batchIcPrice).multiply(new BigDecimal(thisPQty));
                            //stockOutType=1 采购 ： 客户

                            TaxAmount taxAmount = TaxAmountCalculation.calculateAmount(
                                    "Y".equals(inclTax),
                                    Double.parseDouble(batchAmount.toString()),
                                    Double.parseDouble(taxRate),
                                    taxCalType,
                                    amountDigit
                            );

                            batchColumns.add("PRICE", DataValues.newString(batchPrice));
                            batchColumns.add("AMOUNT", DataValues.newString(batchAmount));
                            batchColumns.add("PRETAXAMT", DataValues.newString(taxAmount.getPreAmount()));
                            batchColumns.add("TAXAMT", DataValues.newString(taxAmount.getTaxAmount()));
                            batchColumns.add("TAXCODE", DataValues.newString(taxCode));
                            batchColumns.add("TAXRATE", DataValues.newString(taxRate));
                            batchColumns.add("IC_PRICE", DataValues.newString(batchIcPrice));
                            batchColumns.add("IC_AMOUNT", DataValues.newString(batchIcAmount));

                            String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                            DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ibb = new InsBean("DCP_SSTOCKOUT_BATCH", batchColumnNames);
                            ibb.addValues(batchDataValues);
                            this.addProcessData(new DataProcessBean(ibb));

                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("SStockOutProcess");
                            bcReq.setStockInOutType(stockOutType);
                            bcReq.setBillType(stockBillType);
                            BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                            if(Check.Null(bcMap.getBType())||Check.Null(bcMap.getCostCode())){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                            }

                            //1.同法人采购：
                            //进货价 = 采购价DCP_SSTOCKOUT_BATCH.PRICE,
                            //进货金额（含税）=采购金额DCP_SSTOCKOUT_BATCH.AMOUNT,
                            //未税金额=未税金额DCP_SSTOCKOUT_BATCH.PRETAXAMT,
                            //税额 = 税额DCP_SSTOCKOUT_BATCH.TAXAMT,
                            //税别 = 税别DCP_SSTOCKOUT_BATCH.TAXCODE,
                            //税率 = 税率DCP_SSTOCKOUT_DETAIL.TAXRATE;
                            //
                            String stockDisPrice=batchPrice;
                            String stockDisAmt=batchAmount.toString();
                            String stockTaxAmt="0";
                            String stockPreTaxAmt="0";
                            if("1".equals(stockOutType)) {
                                if (!corp.equals(bizOrgCorp)) {
                                    //2.跨法人采购：
                                    //进货价 = 内部采购进货价DCP_INTERSETTLE_DETAIL.RECEIVEPRICE,
                                    //进货金额（含税）=内部采购进货金额DCP_INTERSETTLE_DETAIL.RECEIVEAMT,
                                    //未税金额 = 内部采购未税金额DCP_INTERSETTLE_DETAIL.PRETAXAMT,
                                    //税额 = 内部采购未税金额DCP_INTERSETTLE_DETAIL.TAXAMT,
                                    //税别编码 = 内部采购税别编码DCP_INTERSETTLE_DETAIL.TAXCODE,
                                    //税率%= 内部采购税率DCP_INTERSETTLE_DETAIL.TAXRATE;
                                    //价格重取  再重算
                                    List<Map<String, Object>> filterInterRows = interSettleDataList.stream().filter(x -> x.get("ITEM").toString().equals(thisItem2)).collect(Collectors.toList());
                                    if (filterInterRows.size() <= 0) {
                                        //不同法人 取不到内部结算数据 就有问题
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到内部结算数据！");
                                    }
                                    String receivePrice = filterInterRows.get(0).get("RECEIVEPRICE").toString();
                                    batchAmount = new BigDecimal(receivePrice).multiply(new BigDecimal(thisPQty));

                                    stockDisPrice = receivePrice;
                                    stockDisAmt = batchAmount.toString();

                                    taxCode = filterInterRows.get(0).get("TAXCODE").toString();
                                    taxRate = filterInterRows.get(0).get("TAXRATE").toString();
                                    taxAmount = TaxAmountCalculation.calculateAmount(
                                            "Y".equals(inclTax),
                                            Double.parseDouble(batchAmount.toString()),
                                            Double.parseDouble(Check.Null(taxRate)?"0":taxRate),
                                            taxCalType,
                                            amountDigit
                                    );
                                    stockTaxAmt=String.valueOf(taxAmount.getTaxAmount());
                                    stockPreTaxAmt=String.valueOf(taxAmount.getPreAmount());
                                }
                                else{
                                    //同法人
                                    stockTaxAmt=String.valueOf(taxAmount.getTaxAmount());
                                    stockPreTaxAmt=String.valueOf(taxAmount.getPreAmount());
                                }
                            }
                            else{
                                stockDisPrice="0";
                                stockDisAmt="0";
                                taxCode="";
                                taxRate="0";
                                stockTaxAmt="0";
                                stockPreTaxAmt="0";
                            }


                            String procedure = "SP_DCP_STOCKCHANGE_V35";
                            Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1, eId);                                            //--企业ID
                            inputParameter.put(2, null);
                            inputParameter.put(3, organizationNO);                                         //--组织
                            inputParameter.put(4, bcMap.getBType());
                            inputParameter.put(5, bcMap.getCostCode());
                            inputParameter.put(6, stockBillType);                                //--单据类型
                            inputParameter.put(7, sStockOutNO);                                        //--单据号
                            inputParameter.put(8, thisItem2);
                            inputParameter.put(9, "0");
                            inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                            inputParameter.put(12, thisPluNo);         //--品号
                            inputParameter.put(13, thisFeatureNo);                                   //--特征码
                            inputParameter.put(14, thisWarehouse);    //--仓库
                            inputParameter.put(15, thisBatchNo);     //批号
                            inputParameter.put(16, thisLocation);     //--location
                            inputParameter.put(17, thisPUnit);        //--交易单位
                            inputParameter.put(18, thisPQty);         //--交易数量
                            inputParameter.put(19, thisBaseUnit);     //--基准单位
                            inputParameter.put(20, thisBaseQty);      //--基准数量
                            inputParameter.put(21, thisUnitRatio);   //--换算比例
                            inputParameter.put(22, row.get("PRICE").toString());        //--零售价
                            inputParameter.put(23, amt);          //--零售金额
                            inputParameter.put(24, stockDisPrice);  //--进货价
                            inputParameter.put(25, stockDisAmt);    //--进货金额

                            inputParameter.put(26,stockTaxAmt);
                            inputParameter.put(27,stockPreTaxAmt);
                            inputParameter.put(28,taxCode);
                            inputParameter.put(29,taxRate);

                            inputParameter.put(30, accountDate);                                   //--入账日期 yyyy-MM-dd
                            inputParameter.put(31, thisProdDate);    //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(32, bDate);            //--单据日期
                            inputParameter.put(33, "销货/退货出库扣库存");             //--异动原因
                            inputParameter.put(34, "");             //--异动描述
                            inputParameter.put(35, req.getOpNO());          //--操作员

                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));


                            if(!corp.equals(bizOrgCorp)) {

                                String bizBatchNo = "";
                                if ("Y".equals(isBatchParaBiz)) {
                                    if (Check.NotNull(thisBatchNo)) {
                                        bizBatchNo = thisBatchNo;
                                    } else {
                                        bizBatchNo = "DEFAULTBATCH";
                                    }
                                }

                                if ("1".equals(stockOutType)) {
                                    stockDisPrice = batchPrice;
                                    stockDisAmt = batchAmount.toString();
                                    stockTaxAmt = String.valueOf(taxAmount.getTaxAmount());
                                    stockPreTaxAmt = String.valueOf(taxAmount.getPreAmount());
                                } else {
                                    stockDisPrice = "0";
                                    stockDisAmt = "0";
                                    taxCode = "";
                                    taxRate = "0";
                                    stockTaxAmt = "0";
                                    stockPreTaxAmt = "0";
                                }

                                Map<Integer, Object> inputParameter2 = new HashMap<Integer, Object>();
                                inputParameter2.put(1, eId);                                            //--企业ID
                                inputParameter2.put(2, null);
                                inputParameter2.put(3, bizOrgCorp);                                         //--组织

                                if ("1".equals(stockOutType)) {
                                    inputParameter2.put(4, "06");
                                    inputParameter2.put(5, "S");
                                    inputParameter2.put(6, "06");
                                }else{
                                    inputParameter2.put(4, "20");
                                    inputParameter2.put(5, "U");
                                    inputParameter2.put(6, "20");
                                }
                                inputParameter2.put(7, sStockOutNO);                                        //--单据号
                                inputParameter2.put(8, thisItem2);
                                inputParameter2.put(9, "0");
                                inputParameter2.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter2.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                inputParameter2.put(12, thisPluNo);         //--品号
                                inputParameter2.put(13, thisFeatureNo);                                   //--特征码
                                inputParameter2.put(14, bizIcCostWarehouse);    //--仓库
                                inputParameter2.put(15, bizBatchNo);     //批号
                                inputParameter2.put(16, " ");     //--location
                                inputParameter2.put(17, thisPUnit);        //--交易单位
                                inputParameter2.put(18, thisPQty);         //--交易数量
                                inputParameter2.put(19, thisBaseUnit);     //--基准单位
                                inputParameter2.put(20, thisBaseQty);      //--基准数量
                                inputParameter2.put(21, thisUnitRatio);   //--换算比例
                                inputParameter2.put(22, row.get("PRICE").toString());        //--零售价
                                inputParameter2.put(23, amt);          //--零售金额
                                inputParameter2.put(24, stockDisPrice);  //--进货价
                                inputParameter2.put(25, stockDisAmt);    //--进货金额

                                inputParameter2.put(26,stockTaxAmt);
                                inputParameter2.put(27,stockPreTaxAmt);
                                inputParameter2.put(28,taxCode);
                                inputParameter2.put(29,taxRate);

                                inputParameter2.put(30, accountDate);                                   //--入账日期 yyyy-MM-dd
                                inputParameter2.put(31, thisProdDate);    //--批号的生产日期 yyyy-MM-dd
                                inputParameter2.put(32, bDate);            //--单据日期
                                inputParameter2.put(33, "销货/退货出库扣库存");             //--异动原因
                                inputParameter2.put(34, "");             //--异动描述
                                inputParameter2.put(35, req.getOpNO());          //--操作员

                                ProcedureBean pdb2 = new ProcedureBean(procedure, inputParameter2);
                                this.addProcessData(new DataProcessBean(pdb2));


                            }

                        }
                    }

                    if (!Check.Null(ofNO))
					{
						String sqlstockin=" select * from DCP_SSTOCKIN_DETAIL A,DCP_SSTOCKOUT_DETAIL B "
								+ " where A.EID='"+eId+"' and A.SHOPID='"+shopId+"' and A.SSTOCKINNO='"+ofNO+"' and A.EID=B.EID and A.SHOPID=B.SHOPID  "
								+ " and B.SSTOCKOUTNO='"+sStockOutNO+"' and A.item=B.oitem  and A.baseqty<(nvl(A.RETWQTY,0)+B.baseqty) ";
						List<Map<String, Object>> stockInDetail=this.doQueryData(sqlstockin, null);
						if (stockInDetail != null && !stockInDetail.isEmpty())
						{
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"商品:"+getQData.get(0).get("PLUNO").toString()+"大于可退货数量");
						}
					}





					for (Map<String, Object> oneData : getQData)
					{
						//增加退货允收管控
						String prodDate=oneData.get("PROD_DATE").toString();
						String batchNO=oneData.get("BATCH_NO").toString();
						String stockOutAllowType=oneData.get("STOCKOUTALLOWTYPE").toString();
						String shelfLife=oneData.get("SHELFLIFE").toString();
						String stockOutValidDay=oneData.get("STOCKOUTVALIDDAY").toString();
						String isBatch=oneData.get("ISBATCH").toString();

						//Y.启用批号不检查库存量  T.启用批号且检查库存量     生产日期或批号必须填写
						//if (!Check.Null(isBatch) && (isBatch.equals("Y")||isBatch.equals("T")) && isBatchPara.equals("Y"))
						//{
							//if (Check.Null(prodDate) || Check.Null(batchNO))
							//{
							//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编号:" + oneData.get("PLUNO").toString() + " 的生产日期或批号为空！");
							//}
						//}

						//退货允收管制方式： 1.依退货效期管控     //必须开启批号管理  BY JZMA 20191112
						if(isBatchPara.equals("Y"))
						{
							if (!Check.Null(stockOutAllowType) && stockOutAllowType.equals("1")  )
							{
								if (PosPub.isNumeric(shelfLife) && PosPub.isNumeric(stockOutValidDay) )
								{
									if (Integer.parseInt(stockOutValidDay)>0)
									{
										String shelfLifeDate = PosPub.GetStringDate(prodDate,Integer.parseInt(shelfLife));
										String stockOutDate = PosPub.GetStringDate(prodDate,Integer.parseInt(stockOutValidDay));
										if (PosPub.compare_date(confirmDate, shelfLifeDate)>0)
										{
											throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编号:" + oneData.get("PLUNO").toString() + " 已超过保质期，不能退货 ");
										}
										if (PosPub.compare_date(confirmDate, stockOutDate)>0)
										{
											throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编号:" + oneData.get("PLUNO").toString() + " 已超过退货允收日期，不能退货 ");
										}
									}
								}
							}
						}

						if (!Check.Null(ofNO))
						{
							//这里要更新原单的RETWQTY
							UptBean upstockin=new UptBean("DCP_SSTOCKIN_DETAIL");
							upstockin.addUpdateValue("RETWQTY", new DataValue(Float.parseFloat(oneData.get("BASEQTY").toString()), Types.FLOAT, DataExpression.UpdateSelf));
							// condition
							upstockin.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
							upstockin.addCondition("EID", new DataValue(eId, Types.VARCHAR));
							upstockin.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							upstockin.addCondition("SSTOCKINNO", new DataValue(ofNO, Types.VARCHAR));
							upstockin.addCondition("OITEM", new DataValue(Integer.parseInt(oneData.get("OITEM").toString()), Types.INTEGER));
							this.addProcessData(new DataProcessBean(upstockin));


                            UptBean upCustomer=new UptBean("DCP_CUSTOMERPORDER_DETAIL");
                            upCustomer.addUpdateValue("STOCKOUTQTY", new DataValue(Float.parseFloat(oneData.get("PQTY").toString()), Types.FLOAT, DataExpression.UpdateSelf));
                            // condition
                            upCustomer.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            upCustomer.addCondition("SHOPNO", new DataValue(shopId, Types.VARCHAR));
                            upCustomer.addCondition("PORDERNO", new DataValue(ofNO, Types.VARCHAR));
                            upCustomer.addCondition("ITEM", new DataValue(Integer.parseInt(oneData.get("OITEM").toString()), Types.INTEGER));
                            this.addProcessData(new DataProcessBean(upCustomer));

						}

						//String warehouse=oneData.get("WAREHOUSE").toString();
						//判断仓库不能为空或空格  BY JZMA 20191118
						//if (Check.Null(warehouse)||warehouse.equals(" "))
						//{
						//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"商品编号:" + oneData.get("PLUNO").toString() + "的仓库不能为空或空格");
						//}

						//String featureNo =oneData.get("FEATURENO").toString(); //特征码为空给空格
						//if (Check.Null(featureNo))
						//	featureNo=" ";

						//String procedure="SP_DCP_StockChange";
						//Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
						//inputParameter.put(1,eId);                                       //--企业ID
						//inputParameter.put(2,shopId);                                    //--组织
						//inputParameter.put(3,"06");                                      //--单据类型
						//inputParameter.put(4,sStockOutNO);	                             //--单据号
						//inputParameter.put(5,oneData.get("ITEM").toString());            //--单据行号
						//inputParameter.put(6,"-1");                                      //--异动方向 1=加库存 -1=减库存
						//inputParameter.put(7,oneData.get("BDATE").toString());           //--营业日期 yyyy-MM-dd
						//inputParameter.put(8,oneData.get("PLUNO").toString());           //--品号
						//inputParameter.put(9,featureNo);                                 //--特征码
					    //inputParameter.put(10,warehouse);                                //--仓库
						//inputParameter.put(11,oneData.get("BATCH_NO").toString());       //--批号
						//inputParameter.put(12,oneData.get("PUNIT").toString());          //--交易单位
						//inputParameter.put(13,oneData.get("PQTY").toString());           //--交易数量
						//inputParameter.put(14,oneData.get("BASEUNIT").toString());       //--基准单位
						//inputParameter.put(15,oneData.get("BASEQTY").toString());        //--基准数量
						//inputParameter.put(16,oneData.get("UNIT_RATIO").toString());     //--换算比例
						//inputParameter.put(17,oneData.get("PRICE").toString());          //--零售价
						//inputParameter.put(18,oneData.get("AMT").toString());            //--零售金额
						//inputParameter.put(19,oneData.get("DISTRIPRICE").toString());    //--进货价
						//inputParameter.put(20,oneData.get("DISTRIAMT").toString());      //--进货金额
						//inputParameter.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
						//inputParameter.put(22,oneData.get("PROD_DATE").toString());      //--批号的生产日期 yyyy-MM-dd
						//inputParameter.put(23,oneData.get("BDATE").toString());          //--单据日期
						//inputParameter.put(24,"");                                       //--异动原因
						//inputParameter.put(25,oneData.get("MEMO").toString());           //--异动描述
						//inputParameter.put(26,accountBy);                                //--操作员

						//ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
						//this.addProcessData(new DataProcessBean(pdb));
					}

					//  ○ 更新采退通知单：按【通知单单号+通知单项次】汇总【退货量】（pqty_sum），匹配通知单对应项次更新栏位【已出货量】；当【已发货量】>=【通知出货量】，更新行状态=【出货结束】；如果整单所有行状态都等于【出货结束】，更新单头状态=【出货结束】；
					//目标表：DCP_STOCKOUTNOTICE、DCP_STOCKOUTNOTICE_DETAIL
					String detailSql="select a.ofno as NOTICENO,A.oitem as NOTICEITEM,sum(a.pqty) pqty " +
                            " from DCP_SSTOCKOUT_DETAIL a " +
							" where a.eid='"+eId+"' " +
							" and a.organizationno='"+organizationNO+"' " +
							" and a.SSTOCKOUTNO='"+sStockOutNO+"' " +
							//" and a.ofno is not null and a.ofno !='' " +
							//" and a.oitem is not null and a.oitem !='' " +
							" group by a.ofno,a.oitem ";
					List<Map<String, Object>> detailNoticeList = this.doQueryData(detailSql, null);
					if(detailNoticeList.size()>0){
						for(Map<String, Object> oneDetailNotice:detailNoticeList){

							String noticeDetailSql="select * from DCP_STOCKOUTNOTICE_DETAIL a where a.eid='"+eId+"'" +
									" and a.organizationno='"+organizationNO+"' and a.BILLNO='"+oneDetailNotice.get("NOTICENO").toString()+"'" +
									" and a.item='"+oneDetailNotice.get("NOTICEITEM").toString()+"' ";
							List<Map<String, Object>> noticeDetailList = this.doQueryData(noticeDetailSql, null);
							if(noticeDetailList.size()>0){
								Map<String, Object> oneNoticeDetail=noticeDetailList.get(0);
								BigDecimal stockoutqty = new BigDecimal(oneNoticeDetail.get("STOCKOUTQTY").toString());
								BigDecimal noticePqty = new BigDecimal(oneNoticeDetail.get("PQTY").toString());
								BigDecimal pqty = new BigDecimal(oneDetailNotice.get("PQTY").toString());
								stockoutqty=stockoutqty.add(pqty);
								//更新通知单
								UptBean ub2 = new UptBean("DCP_STOCKOUTNOTICE_DETAIL");
								//add Value
								ub2.addUpdateValue("STOCKOUTQTY", new DataValue(stockoutqty, Types.VARCHAR));
								if(noticePqty.compareTo(stockoutqty)<=0){
									ub2.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));//已经出货
								}
								ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								ub2.addCondition("ORGANIZATIONNO",new DataValue(organizationNO,Types.VARCHAR));
								ub2.addCondition("BILLNO", new DataValue(oneDetailNotice.get("NOTICENO").toString(), Types.VARCHAR));
								ub2.addCondition("ITEM", new DataValue(oneDetailNotice.get("NOTICEITEM").toString(), Types.VARCHAR));

								this.addProcessData(new DataProcessBean(ub2));

							}
						}
					}

					//  ○ 更新入库单：来源类型= "2.采购入库"/"3.采购收货入库"/"4.无源采购入库"，按【来源单号+来源项次】汇总【退货量】更新来源入库明细【已退货量-RETWQTY】
					//目标表：DCP_SSTOCKIN_DETAIL
                     detailSql="select a.ofno,a.oitem,sum(a.pqty) pqty " +
                            " from DCP_SSTOCKOUT_DETAIL a" +
                            " where a.eid='"+eId+"' " +
                            " and a.organizationno='"+organizationNO+"' " +
                            " and a.SSTOCKOUTNO='"+sStockOutNO+"' " +
                            " and a.otype in ('2','3','4') " +
                            //" and a.ofno is not null and a.ofno !='' " +
                            //" and a.oitem is not null and a.oitem !='' " +
                            " group by a.ofno,a.oitem ";
                    List<Map<String, Object>> outDetailOfInList = this.doQueryData(detailSql, null);
                    if(outDetailOfInList.size()>0){
                        for(Map<String, Object> outOfIn:outDetailOfInList){
                            String stockinDetailSql="select * from DCP_SSTOCKIN_DETAIL a where a.eid='"+eId+"'" +
									" and a.organizationno='"+organizationNO+"' and a.SSTOCKINNO='"+outOfIn.get("OFNO").toString()+"'" +
									" and a.item='"+outOfIn.get("OITEM").toString()+"' ";
                            List<Map<String, Object>> sstockInDetailList = this.doQueryData(stockinDetailSql, null);
                            if(sstockInDetailList.size()>0){
                                Map<String, Object> oneStockInDetail=sstockInDetailList.get(0);
                                BigDecimal returnqty = new BigDecimal(Check.Null(oneStockInDetail.get("RETWQTY").toString())?"0":oneStockInDetail.get("RETWQTY").toString());
                                BigDecimal pqty = new BigDecimal(outOfIn.get("PQTY").toString());
                                returnqty=returnqty.add(pqty);

                                UptBean ub2 = new UptBean("DCP_SSTOCKIN_DETAIL");
                                //add Value
                                ub2.addUpdateValue("RETWQTY", new DataValue(returnqty, Types.VARCHAR));

                                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub2.addCondition("ORGANIZATIONNO",new DataValue(organizationNO,Types.VARCHAR));
                                ub2.addCondition("SSTOCKINNO", new DataValue(outOfIn.get("OFNO").toString(), Types.VARCHAR));
                                ub2.addCondition("ITEM", new DataValue(outOfIn.get("OITEM").toString(), Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(ub2));
                            }

						}
                    }

					//  ○ 更新采购订单：按【原始单号+原始项次】汇总【退货量】更新采购订单交期明细表【仓退量RETURNQTY】=原值+本次出库数量（按项序依次冲销）
					//目标表：DCP_PURORDER_DELIVERY
                    detailSql="select a.originno,a.originitem,sum(a.pqty) pqty " +
                            " from DCP_SSTOCKOUT_DETAIL a" +
                            " where a.eid='"+eId+"' " +
                            " and a.organizationno='"+organizationNO+"' " +
                            " and a.SSTOCKOUTNO='"+sStockOutNO+"' " +
                            //" and a.oofno is not null and a.ofno !='' " +
                            //" and a.ooitem is not null and a.oitem !='' " +
                            " group by a.originno,a.originitem ";
                    List<Map<String, Object>> outDetailOfOrderList = this.doQueryData(detailSql, null);
                    if(outDetailOfOrderList.size()>0){
                        for(Map<String, Object> outOfOrder:outDetailOfOrderList){
                            String oofno = outOfOrder.get("ORIGINNO").toString();
                            String ooitem = outOfOrder.get("ORIGINITEM").toString();
                            if(Check.Null(oofno)||Check.Null(ooitem)){
                                continue;
                            }

                            if("1".equals(stockOutType)) {

                                String deliveryDetailSql = "select * from DCP_PURORDER_DELIVERY a where a.eid='" + eId + "'" +
                                        //" and a.organizationno='" + organizationNO + "' " +
                                        "and a.PURORDERNO='" + oofno + "'" +
                                        " and a.item='" + ooitem + "' " +
                                        " order by a.item2 asc";
                                BigDecimal pqty = Convert.toBigDecimal(outOfOrder.get("PQTY").toString(), BigDecimal.ZERO);
                                List<Map<String, Object>> purOrderDeliveryList = this.doQueryData(deliveryDetailSql, null);
                                for (Map<String, Object> oneDeliveryDetail : purOrderDeliveryList) {
                                    BigDecimal returnqty = Convert.toBigDecimal(oneDeliveryDetail.get("RETURNQTY").toString(), BigDecimal.ZERO);
                                    BigDecimal stockinqty = Convert.toBigDecimal(oneDeliveryDetail.get("STOCKINQTY"), BigDecimal.ZERO);
                                    if (returnqty.compareTo(stockinqty) >= 0) {
                                        continue;
                                    }
                                    BigDecimal tempQty = returnqty.add(pqty);
                                    if (tempQty.compareTo(stockinqty) <= 0) {
                                        //分配完
                                        returnqty = tempQty;
                                        pqty = BigDecimal.ZERO;
                                    } else {
                                        returnqty = stockinqty;
                                        BigDecimal subQty = stockinqty.subtract(returnqty);
                                        pqty = pqty.subtract(subQty);
                                    }

                                    UptBean ub2 = new UptBean("DCP_PURORDER_DELIVERY");
                                    //add Value
                                    ub2.addUpdateValue("RETURNQTY", new DataValue(returnqty, Types.VARCHAR));

                                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    //ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                                    ub2.addCondition("PURORDERNO", new DataValue(oofno, Types.VARCHAR));
                                    ub2.addCondition("ITEM", new DataValue(ooitem, Types.VARCHAR));
                                    ub2.addCondition("ITEM2", new DataValue(oneDeliveryDetail.get("ITEM2").toString(), Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(ub2));

                                }

                                //分配循环完还多了
                                if (pqty.compareTo(BigDecimal.ZERO) > 0) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "上上层来源单号:" + oofno + "上上层来源项次" + ooitem + " 退货数量超过入库数量！");
                                }
                            }

                            if("2".equals(stockOutType)) {
                                //销货出库更新大客订单的数据
                                UptBean upCustomer = new UptBean("DCP_CUSTOMERPORDER_DETAIL");
                                upCustomer.addUpdateValue("STOCKOUTQTY", new DataValue(Float.parseFloat(outOfOrder.get("PQTY").toString()), Types.FLOAT, DataExpression.UpdateSelf));
                                // condition
                                upCustomer.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                upCustomer.addCondition("SHOPNO", new DataValue(shopId, Types.VARCHAR));
                                upCustomer.addCondition("PORDERNO", new DataValue(oofno, Types.VARCHAR));
                                upCustomer.addCondition("ITEM", new DataValue(Integer.parseInt(ooitem), Types.INTEGER));
                                this.addProcessData(new DataProcessBean(upCustomer));
                            }

						}
                    }

                    //String procedure="SP_DCP_SSTOCKOUT_CHECK";
                    //Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                    //inputParameter.put(1,eId);                                       //--企业ID
                    //inputParameter.put(2,shopId);                                    //--组织
                    //inputParameter.put(3,sStockOutNO);                                      //--单据类型
                    //inputParameter.put(4,confirmBy);
                    //ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                    //this.addProcessData(new DataProcessBean(pdb));

                    String bType="";
                    String bizType="";
                    String bizPartnerNo="";
                    String setDirection="1";
                    String setBType="";
                    if(stockOutType.equals("1")){
                        bType="2";
                        bizType="1";
						bizPartnerNo=supplier;
                        setDirection="-1";
                        setBType="2";
                    }
                    if(stockOutType.equals("2")){
                        bType="5";
                        bizType="2";
						bizPartnerNo=customer;
                        setDirection="1";
                        setBType="4";
                    }

					//退货出库单结账日期
					String accountDateS =bDate;// transferData.get(0).get("ACCOUNT_DATE").toString();
					String payDate="";
					String month="";
					String year="";
					if(Check.NotNull(accountDateS)){
						if(accountDateS.length()==8){
							year=accountDateS.substring(0,4);
							month=accountDateS.substring(4,6);
						}
						int pseasons = Integer.parseInt(transferData.get(0).get("PSEASONS").toString());
						int pmonths = Integer.parseInt(transferData.get(0).get("PMONTHS").toString());
						int pdays = Integer.parseInt(transferData.get(0).get("PDAYS").toString());

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
						LocalDate parseDate = LocalDate.parse(accountDateS, formatter);
						parseDate = parseDate.plusMonths(pseasons*3);
						parseDate = parseDate.plusMonths(pmonths);
						parseDate=parseDate.plusDays(pdays);
						payDate= parseDate.format(formatter);

					}

                    String billSql="select to_char(a.BILLDATE,'yyyyMMdd') as billDate from DCP_BIZPARTNER_BILL a where a.eid='"+req.geteId()+"'" +
                            " and a.BIZPARTNERNO='"+bizPartnerNo+"' and " +
                            " to_char(a.BDATE,'yyyyMMdd')<='"+accountDateS+"'" +
                            " and to_char(a.EDATE,'yyyyMMdd')>='"+accountDateS+"' ";
                    List<Map<String, Object>> billData = this.doQueryData(billSql,null);
                    String billDate=confirmDate;
                    if(billData.size()>0){
                        billDate=billData.get(0).get("BILLDATE").toString();
                    }

                    for (Map<String, Object> oneData : transferData){

                        //增加检查税率是否相等
                        BigDecimal transferTaxAmt = new BigDecimal(oneData.get("TAXAMT").toString());
                        BigDecimal trasnferAmt = new BigDecimal(oneData.get("AMT").toString());
                        BigDecimal transferPreTaxAmt = new BigDecimal(oneData.get("PRETAXAMT").toString());

                        //未税金额+税额<>含税金额，不允许过账！
                        if(transferPreTaxAmt.add(transferTaxAmt).compareTo(trasnferAmt)!=0){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "供应商结算金额校验失败！供应商结算的【未税金额】与【税额】≠【含税金额】");
                        }

						ColumnDataValue setColumns=new ColumnDataValue();
                        setColumns.add("EID",eId,Types.VARCHAR);
                        setColumns.add("ORGANIZATIONNO",organizationNO,Types.VARCHAR);
                        setColumns.add("BDATE",oneData.get("BDATE").toString(),Types.VARCHAR);
                        setColumns.add("BTYPE",setBType,Types.VARCHAR);//单据类型	BILLTYPE	2.采购退货	1.采购入库,2.采购退货,3.费用单,4.销货单,5.销退单,6.供应商往来调整,7.大客往来调整
                        setColumns.add("BILLNO",oneData.get("BILLNO").toString(),Types.VARCHAR);
                        setColumns.add("ITEM",oneData.get("ITEM").toString(),Types.VARCHAR);
                        setColumns.add("BIZTYPE",bizType,Types.VARCHAR);//1.供应商
                        setColumns.add("BIZPARTNERNO",bizPartnerNo,Types.VARCHAR);
                        setColumns.add("PAYORGNO",oneData.get("PAYORGNO").toString(),Types.VARCHAR);
                        setColumns.add("BILLDATENO",oneData.get("BILLDATENO").toString(),Types.VARCHAR);
                        setColumns.add("PAYDATENO",oneData.get("PAYDATENO").toString(),Types.VARCHAR);
                        setColumns.add("INVOICECODE",oneData.get("INVOICECODE").toString(),Types.VARCHAR);
                        setColumns.add("CURRENCY",oneData.get("CURRENCY").toString(),Types.VARCHAR);
                        setColumns.add("BILLDATE",billDate,Types.VARCHAR);
                        setColumns.add("PAYDATE",payDate,Types.VARCHAR);
                        setColumns.add("MONTH",month,Types.VARCHAR);
                        setColumns.add("YEAR",year,Types.VARCHAR);
                        setColumns.add("TAXCODE",oneData.get("TAXCODE").toString(),Types.VARCHAR);
                        setColumns.add("TAXRATE",oneData.get("TAXRATE").toString(),Types.VARCHAR);
                        setColumns.add("DIRECTION",setDirection,Types.VARCHAR);//1:正向 -1:负向
                        setColumns.add("PRETAXAMT",oneData.get("PRETAXAMT").toString(),Types.VARCHAR);
                        setColumns.add("BILLAMT",oneData.get("AMT").toString(),Types.VARCHAR);
                        setColumns.add("TAXAMT",oneData.get("TAXAMT").toString(),Types.VARCHAR);
                        setColumns.add("UNSETTLEAMT",oneData.get("AMT").toString(),Types.VARCHAR);
                        setColumns.add("SETTLEAMT","0",Types.VARCHAR);
                        setColumns.add("UNPAIDAMT",oneData.get("AMT").toString(),Types.VARCHAR);
                        setColumns.add("PAIDAMT","0",Types.VARCHAR);
                        setColumns.add("APQTY","0",Types.VARCHAR);
                        setColumns.add("BILLQTY",oneData.get("PQTY").toString(),Types.VARCHAR);
                        setColumns.add("BILLPRICE",oneData.get("PRICE").toString(),Types.VARCHAR);
                        setColumns.add("PRICEUNIT",oneData.get("PUNIT").toString(),Types.VARCHAR);
                        setColumns.add("DEPARTID",oneData.get("DEPARTID").toString(),Types.VARCHAR);
                        setColumns.add("CATEGORY",oneData.get("CATEGORY").toString(),Types.VARCHAR);
                        setColumns.add("PLUNO",oneData.get("PLUNO").toString(),Types.VARCHAR);
                        setColumns.add("FEATURENO",oneData.get("FEATURENO").toString(),Types.VARCHAR);
                        setColumns.add("STATUS","0",Types.VARCHAR);//0-未对账 1-对账中 2-对账完成 3-部分付款 4-付款完成

                        setColumns.add("UNPAIDAMT",oneData.get("AMT").toString(),Types.VARCHAR);
                        setColumns.add("UNAPAMT",oneData.get("AMT").toString(),Types.VARCHAR);
                        setColumns.add("APAMT","0",Types.VARCHAR);

                        String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
                        DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib1=new InsBean("DCP_SETTLEDATA",setColumnNames);
                        ib1.addValues(setDataValues);
                        this.addProcessData(new DataProcessBean(ib1));
                    }

					//更新原单 存储过程里面更新了
					UptBean ub1 = new UptBean("DCP_SSTOCKOUT");
					//add Value
					ub1.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
					ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
					ub1.addUpdateValue("ConfirmBy", new DataValue(confirmBy, Types.VARCHAR));
					ub1.addUpdateValue("Confirm_Date", new DataValue(confirmDate, Types.VARCHAR));
					ub1.addUpdateValue("Confirm_Time", new DataValue(confirmTime, Types.VARCHAR));
					ub1.addUpdateValue("accountBy", new DataValue(accountBy, Types.VARCHAR));
					ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
					ub1.addUpdateValue("account_Time", new DataValue(accountTime, Types.VARCHAR));
					ub1.addUpdateValue("SUBMITBY", new DataValue(submitBy, Types.VARCHAR));
					ub1.addUpdateValue("SUBMIT_DATE", new DataValue(submitDate, Types.VARCHAR));
					ub1.addUpdateValue("SUBMIT_TIME", new DataValue(submitTime, Types.VARCHAR));
					ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
					ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

					//condition
					ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub1.addCondition("sStockOutNO", new DataValue(sStockOutNO, Types.VARCHAR));
					ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(ub1));


					this.doExecuteDataToDB();

                    //post 之后更新通知单状态
                    String stockoutNoticeSql="select * from dcp_stockoutnotice_detail a where" +
                            " a.eid='"+eId+"' " +
                            " and a.organizationno='"+organizationNO+"'" +
                            " and a.billno='"+ofNO+"' and a.status='1' ";
                    List<Map<String, Object>> stockoutNoticeDList = this.doQueryData(stockoutNoticeSql, null);
                    if(stockoutNoticeDList.size()<=0){
                        UptBean ub2 = new UptBean("DCP_STOCKOUTNOTICE");
                        //add Value
                        ub2.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));

                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("ORGANIZATIONNO",new DataValue(organizationNO,Types.VARCHAR));
                        ub2.addCondition("BILLNO", new DataValue(ofNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub2));
                        this.doExecuteDataToDB();
                    }



                    res.setSuccess(true);
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");

					//***********调用库存同步给三方，这是个异步，不会影响效能*****************
					try
					{
						WebHookService.stockSync(eId,shopId,sStockOutNO);
					}
					catch (Exception e)
					{

					}
				}
				else
				{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
				}


			}

            if(opType.equals(Constant.OPR_TYPE_CANCEL))
            {

                if (!status.equals("0"))
                {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"非新建不可取消");
                }

				UptBean ub2 = new UptBean("DCP_SSTOCKOUT");
				ub2.addUpdateValue("STATUS", DataValues.newString("3"));
				ub2.addUpdateValue("CANCELBY", DataValues.newString(req.getEmployeeNo()));
				ub2.addUpdateValue("CANCEL_DATE",DataValues.newString(confirmDate));
				ub2.addUpdateValue("CANCEL_TIME",DataValues.newString(confirmTime));
				ub2.addCondition("EID", DataValues.newString(eId));
				ub2.addCondition("ORGANIZATIONNO",DataValues.newString(organizationNO));
				ub2.addCondition("SSTOCKOUTNO",DataValues.newString(sStockOutNO));
				this.addProcessData(new DataProcessBean(ub2));

				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}

            //内部交易状态修改
        DCP_InterSettleDataProcessReq isdpr = new DCP_InterSettleDataProcessReq();
        isdpr.setServiceId("DCP_InterSettleDataProcess");
        if (Constant.OPR_TYPE_POST.equals(req.getRequest().getOpType())) {
            isdpr.setToken(req.getToken());
            isdpr.setRequest(isdpr.new Request());
            isdpr.getRequest().setBillNo(req.getRequest().getsStockOutNo());
            isdpr.getRequest().setOprType("confirm");
        }
        else {
            isdpr.setToken(req.getToken());
            isdpr.setRequest(isdpr.new Request());
            isdpr.getRequest().setBillNo(req.getRequest().getsStockOutNo());
            isdpr.getRequest().setOprType("unConfirm");
        }
        ServiceAgentUtils<DCP_InterSettleDataProcessReq, DCP_InterSettleDataProcessRes> serviceAgent = new ServiceAgentUtils<>();
        if (!serviceAgent.agentServiceSuccess(isdpr, new TypeToken<DCP_InterSettleDataProcessRes>() {
        })) {
            res.setServiceDescription("调用内部交易结算数据审核/反审核失败");
        }


		//}
		//catch (Exception e)
		//{
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
			//【ID1032555】【乐沙儿3.3.0.3】在门店管理出库单据点确定时，存在负库存时的提示返回error executing work，
			// 需要能够返回SP_DCP_StockChange返回的报错，提示门店  by jinzma 20230526
			//String description=e.getMessage();
			//try {
			//	StringWriter errors = new StringWriter();
			//	PrintWriter pw=new PrintWriter(errors);
			//	e.printStackTrace(pw);
				
			//	pw.flush();
			//	pw.close();
				
			//	errors.flush();
			//	errors.close();
				
			//	description = errors.toString();
				
			//	if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
			//		description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
			//	}
				
			//} catch (Exception ignored) {
			
			//}
			
			//throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);
		//}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SStockOutProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SStockOutProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SStockOutProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TypeToken<DCP_SStockOutProcessReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SStockOutProcessReq>(){};
	}

	@Override
	protected DCP_SStockOutProcessRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SStockOutProcessRes();
	}

	protected String GetDCP_SSTOCKOUT_SQL(DCP_SStockOutProcessReq req) throws Exception{
		String sql="select a.SSTOCKOUTNO,a.SUPPLIER,a.BDATE,a.MEMO,a.CREATEBY,a.CREATE_DATE,a.CREATE_TIME,a.ACCOUNTBY,a.ACCOUNT_DATE,"
				+ " a.ACCOUNT_TIME,a.ofno,b.ITEM,b.PLUNO,b.PUNIT,b.PQTY,b.BASEUNIT,b.UNIT_RATIO,b.BASEQTY,b.PRICE,b.AMT,b.WAREHOUSE,b.OITEM, "
				+ " b.BATCH_NO,b.PROD_DATE,b.DISTRIPRICE,b.DISTRIAMT,b.FEATURENO, "
				+ " d.STOCKOUTALLOWTYPE,c.SHELFLIFE,c.STOCKOUTVALIDDAY,c.isbatch,a.supplier,a.customer,a.stockouttype "
				+ " from DCP_SSTOCKOUT a "
				+ " inner join DCP_SSTOCKOUT_DETAIL b on a.SSTOCKOUTNO=b.SSTOCKOUTNO and a.EID=b.EID and a.SHOPID=b.SHOPID and a.BDATE=b.BDATE "
				+ " left join DCP_GOODS c on b.EID=c.EID and b.pluno=c.pluno and c.status='100' "
				+ " left join DCP_SUPPLIER d on a.EID=d.EID and a.supplier=d.supplier and d.status='100' "
				+ " where a.EID='"+req.geteId()+"'   "
				+ " and a.SHOPID='"+req.getShopId()+"'  "
				+ " and a.SSTOCKOUTNO='"+req.getRequest().getsStockOutNo()+"' "
				+ " and a.status='0' ";
		return sql;
	}

	private String getSStockoutInfoSql(DCP_SStockOutProcessReq req){
		String sql="select a.*,b.IC_COST_WAREHOUSE as ICCOSTWAREHOUSE from DCP_SSTOCKOUT a " +
                " left join dcp_org b on b.eid=a.eid and b.organizationno=a.bizcorp " +
				" where a.EID='"+req.geteId()+"' "  +
				" and a.SHOPID='"+req.getShopId()+"'" +
				" and a.organizationno='"+req.getOrganizationNO()+"'  "+
				" and a.SSTOCKOUTNO='"+req.getRequest().getsStockOutNo()+"' ";
		return sql;
	}

	private String getTransferInfoSql(DCP_SStockOutProcessReq req){
		String sql="select distinct a.eid,a.organizationno,a.bdate,b.billno,b.item,a.supplier,a.customer,a.payorgno,a.billdateno,a.paydateno,a.invoicecode,a.currency," +
				" b.taxcode,b.taxrate,b.pretaxamt,b.amt,b.taxamt,b.pqty,b.price,b.punit,a.departid,c.category,b.pluno,b.featureno,a.ACCOUNT_DATE," +
				" nvl(d.PSEASONS,0) as PSEASONS,nvl(d.PMONTHS,0) as pmonths,nvl(d.pdays,0) as pdays  " +
				" from DCP_SSTOCKOUT a " +
				" left join DCP_TRANSACTION b on a.eid=b.eid and a.organizationno=b.organizationno and a.sstockoutno=b.billno " +
				" left join DCP_SSTOCKOUT_detail c on a.eid=c.eid and a.organizationno=c.organizationno and a.sstockoutno=c.sstockoutno and b.pluno=c.pluno and b.featureno=c.featureno  " +
				" left join dcp_paydate d on a.eid=d.eid and a.paydateno=d.paydateno " +
				" where a.EID='"+req.geteId()+"' "  +
				" and a.SHOPID='"+req.getShopId()+"'" +
				" and a.organizationno='"+req.getOrganizationNO()+"'  "+
				" and a.SSTOCKOUTNO='"+req.getRequest().getsStockOutNo()+"' ";

		return sql;
	}






}
