package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PStockInProcessReq;
import com.dsc.spos.json.cust.res.DCP_PStockInProcessRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PStockInProcess extends SPosAdvanceService<DCP_PStockInProcessReq, DCP_PStockInProcessRes>
{
	@Override
	protected void processDUID(DCP_PStockInProcessReq req, DCP_PStockInProcessRes res) throws Exception
	{

        String isBatchNo=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");

        String shopId = req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();
		String confirmBy = req.getEmployeeNo();
		String accountBy = req.getEmployeeNo();
		String submitBy = req.getEmployeeNo();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String confirmDate = df.format(cal.getTime());
		String accountDate = req.getRequest().getAccountDate();

        String lastmodifyDatetime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(cal.getTime());

        String completeDate = df.format(cal.getTime());
		String submitDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String confirmTime = df.format(cal.getTime());
		String accountTime = df.format(cal.getTime());
		String submitTime = df.format(cal.getTime());
		String pStockInNO = req.getRequest().getPStockInNo();
		String companyId  = req.getBELFIRM();

        if(Check.Null(accountDate)){
            accountDate = completeDate;//PosPub.getAccountDate_SMS(dao, eId, shopId);
        }

        String docTypeR = req.getRequest().getDocType();
        String opType = req.getRequest().getOpType();

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
        String accountDateF = df1.format(df2.parse(accountDate));

        String pSql="select * from dcp_pstockin a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"'";
        List<Map<String, Object>> getQData = this.doQueryData(pSql, null);

        if(getQData.size()<=0){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在！");
        }

        if("0".equals(docTypeR)) {
            String status = getQData.get(0).get("STATUS").toString();
            String bDate = getQData.get(0).get("BDATE").toString();
            String memo = getQData.get(0).get("MEMO").toString();

            String bDateF = df1.format(df2.parse(bDate));

            //post  cancel
            //下面是完工入库的逻辑  先不动
        //    try {
            if("cancel".equals(opType)){
                if (!status.equals("0")) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非新建不可作废！");
                }

                UptBean ub1 = new UptBean("DCP_PSTOCKIN");
                //add Value
                ub1.addUpdateValue("status", new DataValue("3", Types.VARCHAR));
                ub1.addUpdateValue("MODIFYBY", new DataValue(submitBy, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_DATE", new DataValue(submitDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_TIME", new DataValue(submitTime, Types.VARCHAR));
                ub1.addUpdateValue("CANCELBY", new DataValue(submitBy, Types.VARCHAR));
                ub1.addUpdateValue("CANCEL_DATE", new DataValue(submitDate, Types.VARCHAR));
                ub1.addUpdateValue("CANCEL_TIME", new DataValue(submitTime, Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                //condition
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));

                //更新
                this.addProcessData(new DataProcessBean(ub1));

                this.doExecuteDataToDB();
            }

            if("post".equals(opType)) {
                if (!status.equals("0")) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非新建不可过账！");
                }

                Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                }

                String isMaterialReplace = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "MaterialReplace");
                if (isMaterialReplace == null || isMaterialReplace.isEmpty()) {
                    isMaterialReplace = "N";
                }

                String stockType = "";     //成品库存方向  1 OR -1
                String stockDocType = "";  //成品库存单据类型
                String matStockType = "";     //原料库存方向  1 OR -1
                String matStockDocType = "";  //原料库存单据类型

                switch (docTypeR) {
                    case "0":  //完工入库
                        stockType = "1";
                        stockDocType = "08";
                        matStockType = "-1";
                        matStockDocType = "11";  //加工原料倒扣
                        break;
                    case "1":  //组合单
                        stockType = "1";
                        stockDocType = "30";
                        matStockType = "-1";
                        matStockDocType = "31";  //组合原料出库
                        break;
                    case "2":  //拆解单
                        stockType = "-1";
                        stockDocType = "32";
                        matStockType = "1";
                        matStockDocType = "33";  //拆解原料入库
                        break;
                    case "3":  //转换合并
                        stockType = "1";
                        stockDocType = "35";
                        matStockType = "-1";
                        matStockDocType = "36";
                        break;
                    case "4":  //转换拆解
                        stockType = "-1";
                        stockDocType = "37";
                        matStockType = "1";
                        matStockDocType = "38"; //转换拆解入库 （原料）
                        break;
                    default:
                        break;
                }

                //过帐前检查：
                //● DCP_PSTOCKIN_BATCH与DCP_PSTOCKIN_MATERIAL数量勾稽检查：即DCP_PSTOCKIN_MATERIAL的数量与子件对应批号明细DCP_PSTOCKIN_BATCH数量之和需相等；若数量不等，根据以下判断处理：
                //  ○ 原料仓库不允许负库存，返回报错提示“库存不足！”；
                //  ○ 原料仓库允许负库存，DCP_PSTOCKIN_MATERIAL与DCP_PSTOCKIN_BATCH数量不等的明细写入一笔DCP_PSTOCKIN_BATCH：数量=DCP_PSTOCKIN_MATERIAL-DCP_PSTOCKIN_BATCH的差异量
                //过账处理：
                //● 库存交易记录DCP_STOCK_DETAIL：用DCP_PSTOCKIN_BATCH表传入生成BILLTYPE="31"类型的异动明细
                if (!status.equals("0")) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非新建不可过账！");
                }

                String materialSql="select a.*,b.ISCHECKSTOCK,c.isbatch,b.ISLOCATION,d.prodtype " +
                        " from DCP_PSTOCKIN_MATERIAL a " +
                        " left join dcp_pstockin_detail d on d.eid=a.eid and d.ORGANIZATIONNO=a.ORGANIZATIONNO and d.pstockinno=a.pstockinno and d.item=a.mitem " +
                        " left join dcp_goods c on a.pluno=c.pluno and a.eid=c.eid " +
                        " left join dcp_warehouse b on a.eid=b.eid and a.warehouse=b.warehouse " +
                        " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"'";
                List<Map<String, Object>> getMData = this.doQueryData(materialSql, null);
                String batchSql1 = "select a.oitem,sum(a.pqty) as pqty ,sum(a.baseqty) as baseqty from DCP_PSTOCKOUT_BATCH a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' group by a.oitem ";
                List<Map<String, Object>> getBData = this.doQueryData(batchSql1, null);
                String batchNormalSql = "select a.* from DCP_PSTOCKOUT_BATCH a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' order by a.item ";
                List<Map<String, Object>> getBDataN = this.doQueryData(batchNormalSql, null);

                //String detailSql="select a.* from DCP_PSTOCKIN_DETAIL a where a.eid='"+eId+"' and a.pstockinno='"+pStockInNO+"' ";
                //List<Map<String, Object>> getDData = this.doQueryData(detailSql, null);



                int batchItem=0;
                if(getBDataN.size()>0){
                    batchItem=getBDataN.stream().mapToInt(x->Integer.parseInt(x.get("ITEM").toString())).max().orElse(0);
                }
                //原料啊扣库存
                for (Map<String, Object> map : getMData){
                    String item = map.get("ITEM").toString();
                    String pluNo = map.get("PLUNO").toString();
                    String isCheckStock = map.get("ISCHECKSTOCK").toString();
                    String featureNo = map.get("FEATURENO").toString();
                    String pUnit = map.get("PUNIT").toString();
                    String pQty = map.get("PQTY").toString();
                    String baseUnit = map.get("BASEUNIT").toString();
                    String baseQty = map.get("BASEQTY").toString();
                    String unitRatio = map.get("UNIT_RATIO").toString();
                    String warehouse = map.get("WAREHOUSE").toString();
                    String batchNo = map.get("BATCH_NO").toString();
                    String isBatch = map.get("ISBATCH").toString();
                    String location = map.get("LOCATION").toString();
                    String isLocation = map.get("ISLOCATION").toString();
                    String expDate = map.get("EXPDATE").toString();
                    String prodDate = map.get("PROD_DATE").toString();
                    String price = map.get("PRICE").toString();
                    String distriPrice = map.get("DISTRIPRICE").toString();
                    String amt = map.get("AMT").toString();
                    String distriAmt = map.get("DISTRIAMT").toString();
                    String prodType = map.get("PRODTYPE").toString();
                    String prodDateF = "";
                    if(!Check.Null(prodDate)){
                        prodDateF=df1.format(df2.parse(prodDate));
                    }

                    if(isBatch.equals("Y")&&isBatchNo.equals("Y")){
                        if(Check.Null(batchNo)||" ".equals(batchNo)){
                            batchNo="DEFAULTBATCH";
                        }
                    }
                    if(Check.Null(batchNo)){
                        batchNo=" ";
                    }
                    if("Y".equals(isLocation)){
                        if(Check.Null(isLocation)||" ".equals(location)){
                            location="DEFAULTLOCATION";
                        }
                    }
                    if(Check.Null(location)){
                        location=" ";
                    }

                    BcReq bcReq=new BcReq();
                    bcReq.setServiceType("PStockInProcess");
                    bcReq.setDocType(docTypeR);
                    bcReq.setBillType(matStockDocType);
                    bcReq.setProdType(prodType);
                    bcReq.setDirection("-1");

                    BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                    String bType = bcMap.getBType();
                    String costCode = bcMap.getCostCode();
                    if(Check.Null(bType)||Check.Null(costCode)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                    }


                    List<Map<String, Object>> filterB = getBData.stream().filter(x -> x.get("OITEM").toString().equals(item)).collect(Collectors.toList());
                    List<Map<String, Object>> filterBn = getBDataN.stream().filter(x -> x.get("OITEM").toString().equals(item)).collect(Collectors.toList());

                    if (CollUtil.isNotEmpty(filterB)){
                        String batchPQty = filterB.get(0).get("PQTY").toString();
                        String batchBaseQty = filterB.get(0).get("BASEQTY").toString();

                        BigDecimal lastPQty = new BigDecimal(pQty).subtract(new BigDecimal(batchPQty));
                        BigDecimal lastBaseQty = new BigDecimal(baseQty).subtract(new BigDecimal(batchBaseQty));

                        BigDecimal lastAmt = lastPQty.multiply(new BigDecimal(price));
                        BigDecimal lastDistriAmt = lastPQty.multiply(new BigDecimal(distriPrice));

                        if(lastPQty.compareTo(BigDecimal.ZERO)>0){
                            if("Y".equals(isCheckStock)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "品号"+pluNo+"库存不足！");
                            }else{
                                //允许负库存
                                batchItem++;
                                ColumnDataValue mbColumns=new ColumnDataValue();
                                mbColumns.add("EID", DataValues.newString(eId));
                                mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                mbColumns.add("ITEM",DataValues.newString(batchItem));
                                mbColumns.add("OITEM",DataValues.newString(item));
                                mbColumns.add("PLUNO",DataValues.newString(pluNo));
                                mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                mbColumns.add("PQTY",DataValues.newString(lastPQty));
                                mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                mbColumns.add("BASEQTY",DataValues.newString(lastBaseQty));
                                mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                mbColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                                mbColumns.add("LOCATION",DataValues.newString(Check.Null(location)?" ":location));
                                mbColumns.add("BATCHNO",DataValues.newString(batchNo));
                                mbColumns.add("PRODDATE",DataValues.newString(prodDate));
                                mbColumns.add("EXPDATE",DataValues.newString(expDate));

                                String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                ibmb.addValues(mbDataValues);
                                this.addProcessData(new DataProcessBean(ibmb));

                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);
                                inputParameter.put(2, null);
                                inputParameter.put(3, shopId);                                         //--组织
                                inputParameter.put(4, bType);
                                inputParameter.put(5, costCode);
                                inputParameter.put(6, matStockDocType);                                //--单据类型
                                inputParameter.put(7, pStockInNO);                                        //--单据号
                                inputParameter.put(8, String.valueOf(batchItem));
                                inputParameter.put(9, "0");//没序号给个1
                                inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, pluNo);         //--品号
                                inputParameter.put(13, featureNo);                                   //--特征码
                                inputParameter.put(14, warehouse);    //--仓库
                                inputParameter.put(15, batchNo);  //--批号
                                inputParameter.put(16, location);
                                inputParameter.put(17, pUnit);        //--交易单位
                                inputParameter.put(18, lastPQty);         //--交易数量
                                inputParameter.put(19, baseUnit);     //--基准单位
                                inputParameter.put(20, lastBaseQty);      //--基准数量
                                inputParameter.put(21, unitRatio);   //--换算比例
                                inputParameter.put(22, price);        //--零售价
                                inputParameter.put(23, lastAmt.toString());          //--零售金额
                                inputParameter.put(24, distriPrice);  //--进货价
                                inputParameter.put(25, lastDistriAmt);    //--进货金额
                                inputParameter.put(26, accountDateF);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(27, prodDate);    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(28, bDate);            //--单据日期
                                inputParameter.put(29, "原料扣库存");             //--异动原因
                                inputParameter.put(30, memo);             //--异动描述
                                inputParameter.put(31, accountBy);          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }
                        }
                    }
                    else{
                        //
                        if("Y".equals(isCheckStock)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "品号"+pluNo+"库存不足！");
                        }
                        else{
                            batchItem++;
                            //允许负库存
                            ColumnDataValue mbColumns=new ColumnDataValue();
                            mbColumns.add("EID", DataValues.newString(eId));
                            mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                            mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                            mbColumns.add("ITEM",DataValues.newString(batchItem));
                            mbColumns.add("OITEM",DataValues.newString(item));
                            mbColumns.add("PLUNO",DataValues.newString(pluNo));
                            mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                            mbColumns.add("PUNIT",DataValues.newString(pUnit));
                            mbColumns.add("PQTY",DataValues.newString(pQty));
                            mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                            mbColumns.add("BASEQTY",DataValues.newString(baseQty));
                            mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                            mbColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                            mbColumns.add("LOCATION",DataValues.newString(Check.Null(location)?" ":location));
                            mbColumns.add("BATCHNO",DataValues.newString(batchNo));
                            mbColumns.add("PRODDATE",DataValues.newString(prodDate));
                            mbColumns.add("EXPDATE",DataValues.newString(expDate));

                            String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                            DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                            ibmb.addValues(mbDataValues);
                            this.addProcessData(new DataProcessBean(ibmb));


                            String procedure = "SP_DCP_STOCKCHANGE_VX";
                            Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1, eId);                                            //--企业ID
                            inputParameter.put(2, null);
                            inputParameter.put(3, shopId);                                         //--组织
                            inputParameter.put(4, bType);
                            inputParameter.put(5, costCode);
                            inputParameter.put(6, matStockDocType);                                //--单据类型
                            inputParameter.put(7, pStockInNO);                                        //--单据号
                            inputParameter.put(8, "1");
                            inputParameter.put(9, "0");
                            inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(11, bDateF);             //--营业日期 yyyy-MM-dd
                            inputParameter.put(12, pluNo);         //--品号
                            inputParameter.put(13, featureNo);                                   //--特征码
                            inputParameter.put(14, warehouse);    //--仓库
                            inputParameter.put(15, batchNo);     //--批号
                            inputParameter.put(16, location);
                            inputParameter.put(17, pUnit);        //--交易单位
                            inputParameter.put(18, pQty);         //--交易数量
                            inputParameter.put(19, baseUnit);     //--基准单位
                            inputParameter.put(20, baseQty);      //--基准数量
                            inputParameter.put(21, unitRatio);   //--换算比例
                            inputParameter.put(22, price);        //--零售价
                            inputParameter.put(23, amt);          //--零售金额
                            inputParameter.put(24, distriPrice);  //--进货价
                            inputParameter.put(25, distriAmt);    //--进货金额
                            inputParameter.put(26, accountDateF);                                   //--入账日期 yyyy-MM-dd
                            inputParameter.put(27, prodDateF);    //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(28, bDateF);            //--单据日期
                            inputParameter.put(29, "原料扣库存");             //--异动原因
                            inputParameter.put(30, memo);             //--异动描述
                            inputParameter.put(31, accountBy);          //--操作员

                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));
                        }
                    }

                    //扣库存  上面临时添加的先扣 再根据表中的扣

                    if(CollUtil.isNotEmpty(filterBn)){
                        for (Map<String, Object> bn : filterBn){

                            String bBatchNo = bn.get("BATCHNO").toString();
                            String bLocation = bn.get("LOCATION").toString();
                            String bPQty = bn.get("PQTY").toString();
                            String bBaseQty = bn.get("BASEQTY").toString();
                            String bProdDate = bn.get("PRODDATE").toString();
                            String bProdDateF =Check.Null(bProdDate)?"": df1.format(df2.parse(bProdDate));
                            BigDecimal bAmt = new BigDecimal(bPQty).multiply(new BigDecimal(price));
                            BigDecimal bDistriAmt =new BigDecimal(bPQty).multiply(new BigDecimal(distriPrice));
                            String procedure = "SP_DCP_STOCKCHANGE_VX";
                            Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1, eId);                                            //--企业ID
                            inputParameter.put(2, null);
                            inputParameter.put(3, shopId);                                         //--组织
                            inputParameter.put(4, bType);
                            inputParameter.put(5, costCode);
                            inputParameter.put(6, matStockDocType);                                //--单据类型
                            inputParameter.put(7, pStockInNO);                                        //--单据号
                            inputParameter.put(8, bn.get("ITEM").toString());
                            inputParameter.put(9, "0");
                            inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                            inputParameter.put(12, pluNo);         //--品号
                            inputParameter.put(13, featureNo);                                   //--特征码
                            inputParameter.put(14, warehouse);    //--仓库
                            inputParameter.put(15, Check.Null(bBatchNo)?" ":bBatchNo);     //批号
                            inputParameter.put(16, bLocation);     //--location
                            inputParameter.put(17, pUnit);        //--交易单位
                            inputParameter.put(18, bPQty);         //--交易数量
                            inputParameter.put(19, baseUnit);     //--基准单位
                            inputParameter.put(20, bBaseQty);      //--基准数量
                            inputParameter.put(21, unitRatio);   //--换算比例
                            inputParameter.put(22, price);        //--零售价
                            inputParameter.put(23, bAmt);          //--零售金额
                            inputParameter.put(24, distriPrice);  //--进货价
                            inputParameter.put(25, bDistriAmt);    //--进货金额
                            inputParameter.put(26, accountDateF);                                   //--入账日期 yyyy-MM-dd
                            inputParameter.put(27, bProdDateF);    //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(28, bDate);            //--单据日期
                            inputParameter.put(29, "原料扣库存");             //--异动原因
                            inputParameter.put(30, memo);             //--异动描述
                            inputParameter.put(31, accountBy);          //--操作员

                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));
                        }
                    }
                }


                //判断是否已提交审核
                String sql = this.GetDCP_PSTOCKIN_SQL(req);
                List<Map<String, Object>> getQData1 = this.doQueryData(sql, null);
                if (getQData1 != null && !getQData1.isEmpty()) {
                    String ofNO = getQData1.get(0).get("OFNO").toString();
                    String docType = getQData1.get(0).get("DOC_TYPE").toString();
                    String keyDate = getQData1.get(0).get("ACCOUNT_DATE").toString();
                    String oofNo=getQData1.get(0).get("OOFNO").toString();//任务单号 可能ofno也记任务单号
                    String oType = getQData1.get(0).get("OTYPE").toString();
                    String ooType = getQData1.get(0).get("OOTYPE").toString();

                    UptBean ub1 = new UptBean("DCP_PSTOCKIN");
                    //add Value
                    ub1.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                    ub1.addUpdateValue("ConfirmBy", new DataValue(confirmBy, Types.VARCHAR));
                    ub1.addUpdateValue("Confirm_Date", new DataValue(confirmDate, Types.VARCHAR));
                    ub1.addUpdateValue("Confirm_Time", new DataValue(confirmTime, Types.VARCHAR));
                    ub1.addUpdateValue("accountBy", new DataValue(accountBy, Types.VARCHAR));
                    if (!keyDate.equals(accountDate)) //分区字段需要更新
                    {
                        ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));

                        //更新单身DCP_PSTOCKIN_DETAIL.ACCOUNT_DATE
                        UptBean ub = new UptBean("DCP_PSTOCKIN_DETAIL");
                        ub.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                        // condition
                        ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                        ub.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub));


                        UptBean pstockin_material = new UptBean("DCP_PSTOCKIN_MATERIAL");
                        //更新单身DCP_PSTOCKIN_MATERIAL.ACCOUNT_DATE
                        pstockin_material.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                        // condition
                        pstockin_material.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        pstockin_material.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        pstockin_material.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                        pstockin_material.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(pstockin_material));

                    }

                    ub1.addUpdateValue("account_Time", new DataValue(accountTime, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMITBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMIT_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("SUBMIT_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    //condition
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                    ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    //更新
                    this.addProcessData(new DataProcessBean(ub1));

                    if(Check.NotNull(oofNo)&&"0".equals(ooType)){
                        //查询出完工入库明细，去更新加工任务明细的数量
                        String sql_PSTOCKIN_DETAIL = "select * from DCP_PSTOCKIN_DETAIL a " +
                                "where a.eid='" + req.geteId() + "' " +
                                "and a.organizationno='" + req.getOrganizationNO() + "' " +
                                "and a.pstockinno='" + pStockInNO + "' ";
                        List<Map<String, Object>> getQData_PSTOCKIN_DETAIL = this.doQueryData(sql_PSTOCKIN_DETAIL, null);


                        if (getQData_PSTOCKIN_DETAIL != null && getQData_PSTOCKIN_DETAIL.size() > 0) {
                            for (Map<String, Object> map : getQData_PSTOCKIN_DETAIL) {
                                UptBean ub_DCP_PROCESSTASK_DETAIL = new UptBean("DCP_PROCESSTASK_DETAIL");
                                //add Value
                                ub_DCP_PROCESSTASK_DETAIL.addUpdateValue("PSTOCKIN_QTY", new DataValue(Convert.toDouble(map.get("PQTY"), 0d), Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                                ub_DCP_PROCESSTASK_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub_DCP_PROCESSTASK_DETAIL.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                                ub_DCP_PROCESSTASK_DETAIL.addCondition("PROCESSTASKNO", new DataValue(oofNo, Types.VARCHAR));
                                ub_DCP_PROCESSTASK_DETAIL.addCondition("ITEM", new DataValue(Convert.toInt(map.get("OOITEM"), 0), Types.VARCHAR));
                                //更新
                                this.addProcessData(new DataProcessBean(ub_DCP_PROCESSTASK_DETAIL));
                            }
                        }

                    }
                    else{
                        String detailSql="select a.*,b.oddvalue,b.REMAINTYPE,b.SEMIWOTYPE,b.MINQTY " +
                                " from dcp_pstockin_detail a " +
                                " left join dcp_bom b on a.eid=b.eid and a.bomno=b.bomno " +
                                " where a.eid='"+req.geteId()+"' " +
                                " and a.organizationno='"+req.getOrganizationNO()+"'" +
                                " and a.pstockinno='"+pStockInNO+"' ";
                        List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

                        //生成任务单
                        String taskNo = this.getOrderNO(req, "JGRW");

                        int taskItem=0;
                        for (Map<String, Object> detail : detailList){
                            taskItem++;
                            ColumnDataValue detailColumns=new ColumnDataValue();
                            detailColumns.add("EID", DataValues.newString(eId));
                            detailColumns.add("PROCESSTASKNO", DataValues.newString(taskNo));
                            detailColumns.add("SHOPID", DataValues.newString(shopId));
                            detailColumns.add("ITEM", DataValues.newString(taskItem));
                            detailColumns.add("PLUNO", DataValues.newString(detail.get("PLUNO").toString()));
                            detailColumns.add("PUNIT", DataValues.newString(detail.get("PUNIT").toString()));
                            detailColumns.add("PQTY", DataValues.newString(detail.get("PQTY").toString()));
                            detailColumns.add("PSTOCKIN_QTY", DataValues.newString(detail.get("PQTY").toString()));
                            detailColumns.add("BASEUNIT", DataValues.newString(detail.get("BASEUNIT").toString()));
                            detailColumns.add("BASEQTY", DataValues.newString(detail.get("BASEQTY").toString()));
                            detailColumns.add("UNIT_RATIO", DataValues.newString(detail.get("UNIT_RATIO").toString()));
                            detailColumns.add("PRICE", DataValues.newString(detail.get("PRICE").toString()));
                            detailColumns.add("AMT", DataValues.newString(detail.get("AMT").toString()));
                            detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                            detailColumns.add("MUL_QTY", DataValues.newString(detail.get("MUL_QTY").toString()));
                            detailColumns.add("DISTRIPRICE", DataValues.newString(detail.get("DISTRIPRICE").toString()));
                            detailColumns.add("DISTRIAMT", DataValues.newString(detail.get("DISTRIAMT").toString()));
                            detailColumns.add("BDATE", DataValues.newString(accountDate));
                            detailColumns.add("FEATURENO", DataValues.newString(detail.get("FEATURENO").toString()));
                            detailColumns.add("MINQTY", DataValues.newString(detail.get("MINQTY").toString()));
                            detailColumns.add("DISPTYPE", DataValues.newString(detail.get("DISPTYPE").toString()));
                            detailColumns.add("SEMIWOTYPE", DataValues.newString(detail.get("SEMIWOTYPE").toString()));
                            detailColumns.add("ODDVALUE", DataValues.newString(detail.get("ODDVALUE").toString()));
                            detailColumns.add("REMAINTYPE", DataValues.newString(detail.get("REMAINTYPE").toString()));
                            detailColumns.add("BOMNO", DataValues.newString(detail.get("BOMNO").toString()));
                            detailColumns.add("VERSIONNUM", DataValues.newString(detail.get("VERSIONNUM").toString()));
                            detailColumns.add("BEGINDATE", DataValues.newString(accountDate));
                            detailColumns.add("ENDDATE", DataValues.newString(accountDate));

                            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ib=new InsBean("DCP_PROCESSTASK_DETAIL",detailColumnNames);
                            ib.addValues(detailDataValues);
                            this.addProcessData(new DataProcessBean(ib));

                            UptBean ub12 = new UptBean("DCP_PSTOCKIN_DETAIL");
                            ub12.addUpdateValue("OOTYPE", new DataValue("0", Types.VARCHAR));
                            ub12.addUpdateValue("OOFNO", new DataValue(taskNo, Types.VARCHAR));
                            ub12.addUpdateValue("OOITEM", new DataValue(taskItem, Types.VARCHAR));
                            ub12.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub12.addCondition("ITEM", new DataValue(detail.get("ITEM").toString(), Types.VARCHAR));
                            ub12.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                            ub12.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            //更新
                            this.addProcessData(new DataProcessBean(ub12));
                        }

                        UptBean ub12 = new UptBean("DCP_PSTOCKIN");
                        ub12.addUpdateValue("OOTYPE", new DataValue("0", Types.VARCHAR));
                        ub12.addUpdateValue("OOFNO", new DataValue(taskNo, Types.VARCHAR));
                        ub12.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub12.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                        ub12.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                        //更新
                        this.addProcessData(new DataProcessBean(ub12));


                        ColumnDataValue mainColumns=new ColumnDataValue();
                        mainColumns.add("EID", DataValues.newString(eId));

                        BigDecimal totPqty = new BigDecimal(getQData.get(0).get("TOT_PQTY").toString());
                        BigDecimal totAmt = new BigDecimal(getQData.get(0).get("TOT_AMT").toString());
                        BigDecimal totCqty = new BigDecimal(getQData.get(0).get("TOT_CQTY").toString());
                        String pTemplateNo = getQData.get(0).get("PTEMPLATENO").toString();
                        String warehouse = getQData.get(0).get("WAREHOUSE").toString();
                        String employeeId = getQData.get(0).get("EMPLOYEEID").toString();
                        String departId = getQData.get(0).get("DEPARTID").toString();
                        Object prodType = getQData.get(0).get("PRODTYPE");
                        BigDecimal totDistriamt = new BigDecimal(getQData.get(0).get("TOT_DISTRIAMT").toString());
                        mainColumns.add("SHOPID", DataValues.newString(shopId));
                        mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                        mainColumns.add("BDATE", DataValues.newString(accountDate));
                        mainColumns.add("PROCESSTASKNO", DataValues.newString(taskNo));
                        mainColumns.add("CREATEBY", DataValues.newString(eId));
                        mainColumns.add("CREATE_DATE", DataValues.newString(accountDate));
                        mainColumns.add("CREATE_TIME", DataValues.newString(accountTime));
                        mainColumns.add("TOT_PQTY", DataValues.newString(totPqty));
                        mainColumns.add("TOT_AMT", DataValues.newString(totAmt));
                        mainColumns.add("TOT_CQTY", DataValues.newString(totCqty));
                        mainColumns.add("EID", DataValues.newString(eId));
                        mainColumns.add("MEMO", DataValues.newString(memo));
                        mainColumns.add("STATUS", DataValues.newString("7"));
                        mainColumns.add("PROCESS_STATUS", DataValues.newString("N"));
                        mainColumns.add("PTEMPLATENO", DataValues.newString(pTemplateNo));
                        mainColumns.add("PDATE", DataValues.newString(accountDate));
                        mainColumns.add("WAREHOUSE", DataValues.newString(warehouse));
                        mainColumns.add("MATERIALWAREHOUSE", DataValues.newString(""));
                        mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriamt));
                        mainColumns.add("CREATE_CHATUSERID", DataValues.newString(req.getChatUserId()));
                        mainColumns.add("DTNO", DataValues.newString(""));
                        mainColumns.add("EMPLOYEEID", DataValues.newString(employeeId));
                        mainColumns.add("DEPARTID", DataValues.newString(departId));
                        mainColumns.add("PRODTYPE", DataValues.newString(prodType));

                        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib=new InsBean("DCP_PROCESSTASK",mainColumnNames);
                        ib.addValues(mainDataValues);
                        this.addProcessData(new DataProcessBean(ib));
                    }


                    //只搞完工入库单  这边应该没用了  不管
                  //  if (docType.equals("0") && !Check.Null(ofNO)&&"3".equals(oType)) {
                        //查询出完工入库明细，去更新加工任务明细的数量
                   //     String sql_PSTOCKIN_DETAIL = "select * from DCP_PSTOCKIN_DETAIL a " +
                    //            "where a.eid='" + req.geteId() + "' " +
                     //           "and a.organizationno='" + req.getOrganizationNO() + "' " +
                    //            "and a.pstockinno='" + pStockInNO + "' ";
                      //  List<Map<String, Object>> getQData_PSTOCKIN_DETAIL = this.doQueryData(sql_PSTOCKIN_DETAIL, null);
                      //  if (getQData_PSTOCKIN_DETAIL != null && getQData_PSTOCKIN_DETAIL.size() > 0) {
                           // for (Map<String, Object> map : getQData_PSTOCKIN_DETAIL) {
                             //   UptBean ub_DCP_PROCESSTASK_DETAIL = new UptBean("DCP_PROCESSTASK_DETAIL");
                                //add Value
                              //  ub_DCP_PROCESSTASK_DETAIL.addUpdateValue("PSTOCKIN_QTY", new DataValue(Convert.toDouble(map.get("PQTY"), 0d), Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                             //   ub_DCP_PROCESSTASK_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                             //   ub_DCP_PROCESSTASK_DETAIL.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                             //   ub_DCP_PROCESSTASK_DETAIL.addCondition("PROCESSTASKNO", new DataValue(ofNO, Types.VARCHAR));
                               // ub_DCP_PROCESSTASK_DETAIL.addCondition("ITEM", new DataValue(Convert.toInt(map.get("OITEM"), 0), Types.VARCHAR));
                                //更新
                               // this.addProcessData(new DataProcessBean(ub_DCP_PROCESSTASK_DETAIL));
                        //    }
                       // }
                 //   }

                    //1.加入库存流水账信息

                    Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                    condition.put("ITEM", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQDetail = MapDistinct.getMap(getQData1, condition);

                    //入库 加库存
                    for (Map<String, Object> oneData : getQDetail) {

                        BcReq bcReq=new BcReq();
                        bcReq.setServiceType("PStockInProcess");
                        bcReq.setDocType(docTypeR);
                        bcReq.setBillType(stockDocType);
                        bcReq.setProdType(oneData.get("PRODTYPE").toString());
                        bcReq.setDirection("1");
                        BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                        String bType = bcMap.getBType();
                        String costCode = bcMap.getCostCode();
                        if(Check.Null(bType)||Check.Null(costCode)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                        }
                        //报废不写库存流水   BY JZMA 20190802  有报废时录入数一定是零，报废单独一条记录
                        BigDecimal pqty_b = new BigDecimal(oneData.get("PQTY").toString());
                        if (pqty_b.compareTo(BigDecimal.ZERO) != 0) {
                            String featureNo = oneData.get("FEATURENO").toString(); //特征码为空给空格
                            if (Check.Null(featureNo))
                                featureNo = " ";
                            String procedure = "SP_DCP_STOCKCHANGE_VX";
                            Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1, eId);
                            inputParameter.put(2, null);
                            inputParameter.put(3, shopId);
                            inputParameter.put(4, bType);
                            inputParameter.put(5, costCode);
                            inputParameter.put(6, stockDocType);                             //--单据类型
                            inputParameter.put(7, pStockInNO);                                  //--单据号
                            inputParameter.put(8, oneData.get("ITEM").toString());           //--单据行号
                            inputParameter.put(9, "0");
                            inputParameter.put(10, stockType);                                //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(11, oneData.get("BDATE").toString());          //--营业日期 yyyy-MM-dd
                            inputParameter.put(12, oneData.get("PLUNO").toString());          //--品号
                            inputParameter.put(13, featureNo);                                //--特征码
                            inputParameter.put(14, oneData.get("WAREHOUSE").toString());     //--仓库
                            inputParameter.put(15, Check.Null(oneData.get("BATCH_NO").toString()) ? " " : oneData.get("BATCH_NO").toString());      //--批号
                            inputParameter.put(16, Check.Null(oneData.get("LOCATION").toString()) ? " " : oneData.get("LOCATION").toString());      //--批号
                            inputParameter.put(17, oneData.get("PUNIT").toString());         //--交易单位
                            inputParameter.put(18, oneData.get("PQTY").toString());          //--交易数量
                            inputParameter.put(19, oneData.get("BASEUNIT").toString());      //--基准单位
                            inputParameter.put(20, oneData.get("BASEQTY").toString());       //--基准数量
                            inputParameter.put(21, oneData.get("UNIT_RATIO").toString());    //--换算比例
                            inputParameter.put(22, oneData.get("PRICE").toString());         //--零售价
                            inputParameter.put(23, oneData.get("AMT").toString());           //--零售金额
                            inputParameter.put(24, oneData.get("DISTRIPRICE").toString());   //--进货价
                            inputParameter.put(25, oneData.get("DISTRIAMT").toString());     //--进货金额
                            inputParameter.put(26, accountDate);                             //--入账日期 yyyy-MM-dd
                            inputParameter.put(27, oneData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(28, oneData.get("BDATE").toString());         //--单据日期
                            inputParameter.put(29, oneData.get("BSNO").toString());          //--异动原因
                            inputParameter.put(30, oneData.get("MEMO").toString());          //--异动描述
                            inputParameter.put(31, accountBy);                               //--操作员

                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));
                        }
                        if (isMaterialReplace.equals("Y") && docType.equals("0"))
                            continue;
                        /**  原料扣库存根据batch表扣了
                        for (Map<String, Object> matOneData : getQData1) {
                            String matMItem = matOneData.get("MAT_MITEM").toString();
                            String isBuckle = matOneData.get("MAT_ISBUCKLE").toString();  //是否扣料件
                            if (Check.Null(isBuckle) || !isBuckle.equals("N"))
                                isBuckle = "Y";
                            if (matMItem.equals(oneData.get("ITEM").toString()) && isBuckle.equals("Y"))  //成品项次相同且必须是扣料件
                            {
                                String matFeatureNo = matOneData.get("MAT_FEATURENO").toString();  //特征码为空给空格
                                if (Check.Null(matFeatureNo))
                                    matFeatureNo = " ";
                                String procedure = "SP_DCP_StockChange";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                            //--企业ID
                                inputParameter.put(2, shopId);                                         //--组织
                                inputParameter.put(3, matStockDocType);                                //--单据类型
                                inputParameter.put(4, pStockInNO);                                        //--单据号
                                inputParameter.put(5, matOneData.get("MAT_ITEM").toString());          //--单据行号
                                inputParameter.put(6, matStockType);                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(7, matOneData.get("BDATE").toString());             //--营业日期 yyyy-MM-dd
                                inputParameter.put(8, matOneData.get("MAT_PLUNO").toString());         //--品号
                                inputParameter.put(9, matFeatureNo);                                   //--特征码
                                inputParameter.put(10, matOneData.get("MAT_WAREHOUSE").toString());    //--仓库
                                inputParameter.put(11, Check.Null(matOneData.get("MAT_BATCH_NO").toString()) ? " " : matOneData.get("MAT_BATCH_NO").toString());     //--批号
                                inputParameter.put(12, matOneData.get("MAT_PUNIT").toString());        //--交易单位
                                inputParameter.put(13, matOneData.get("MAT_PQTY").toString());         //--交易数量
                                inputParameter.put(14, matOneData.get("MAT_BASEUNIT").toString());     //--基准单位
                                inputParameter.put(15, matOneData.get("MAT_BASEQTY").toString());      //--基准数量
                                inputParameter.put(16, matOneData.get("MAT_UNIT_RATIO").toString());   //--换算比例
                                inputParameter.put(17, matOneData.get("MAT_PRICE").toString());        //--零售价
                                inputParameter.put(18, matOneData.get("MAT_AMT").toString());          //--零售金额
                                inputParameter.put(19, matOneData.get("MAT_DISTRIPRICE").toString());  //--进货价
                                inputParameter.put(20, matOneData.get("MAT_DISTRIAMT").toString());    //--进货金额
                                inputParameter.put(21, accountDate);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(22, matOneData.get("MAT_PROD_DATE").toString());    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(23, matOneData.get("BDATE").toString());            //--单据日期
                                inputParameter.put(24, matOneData.get("BSNO").toString());             //--异动原因
                                inputParameter.put(25, matOneData.get("MEMO").toString());             //--异动描述
                                inputParameter.put(26, accountBy);          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }
                        }
                         **/
                    }
                    this.doExecuteDataToDB();

                    if (docType.equals("0") && !Check.Null(oofNo) &&"0".equals(ooType)) {
                        //如果全部完工才更新
                        String sql_DCP_PROCESSTASK_DETAIL = "select a.item,nvl(a.pstockin_qty,0) pstockinqty,nvl(a.pqty,0) pqty from DCP_PROCESSTASK_DETAIL a " +
                                "where a.eid='" + eId + "' " +
                                "and a.organizationno='" + organizationNO + "' " +
                                "and a.processtaskno='" + oofNo + "' " +
                                " ";
                        List<Map<String, Object>> getQData_PROCESSTASK_DETAIL = this.doQueryData(sql_DCP_PROCESSTASK_DETAIL, null);
                        for (Map<String, Object> oneData : getQData_PROCESSTASK_DETAIL){
                            String item = oneData.get("ITEM").toString();
                            BigDecimal pStockInQty = new BigDecimal(oneData.get("PSTOCKINQTY").toString());
                            BigDecimal pQty = new BigDecimal(oneData.get("PQTY").toString());
                            if (pStockInQty.compareTo(pQty) >= 0) {
                                UptBean ub = null;
                                ub = new UptBean("DCP_PROCESSTASK_DETAIL");
                                //add Value
                                ub.addUpdateValue("GOODSSTATUS", new DataValue("2", Types.VARCHAR));

                                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                                ub.addCondition("PROCESSTASKNO", new DataValue(oofNo, Types.VARCHAR));
                                ub.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                                //更新
                                this.addProcessData(new DataProcessBean(ub));

                            }
                        }

                        this.doExecuteDataToDB();
                        String sql_DCP_PROCESSTASK_DETAIL2 = "select a.* from DCP_PROCESSTASK_DETAIL a " +
                                "where a.eid='" + eId + "' " +
                                "and a.organizationno='" + organizationNO + "' " +
                                "and a.processtaskno='" + oofNo + "'" +
                                " and a.goodsstatus not in ('2','3','4') " +
                                " ";
                        List<Map<String, Object>> getQData_PROCESSTASK_DETAIL2 = this.doQueryData(sql_DCP_PROCESSTASK_DETAIL2, null);

                        if (getQData_PROCESSTASK_DETAIL2 != null && getQData_PROCESSTASK_DETAIL2.size() == 0) {
                            UptBean ub2 = null;
                            ub2 = new UptBean("DCP_PROCESSTASK");
                            //add Value
                            ub2.addUpdateValue("status", new DataValue("7", Types.VARCHAR));
                            ub2.addUpdateValue("Complete_Date", new DataValue(completeDate, Types.VARCHAR));
                            ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            //condition
                            ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub2.addCondition("PROCESSTASKNO", new DataValue(ofNO, Types.VARCHAR));
                            ub2.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub2));
                        }
                        this.doExecuteDataToDB();
                    }

                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行成功");
                    //***********调用库存同步给三方，这是个异步，不会影响效能*****************
                    try {

                        WebHookService.stockSync(eId, shopId, pStockInNO);
                    } catch (Exception ignored) {

                    }
                }
                else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
                }



            }

        }

        if("1".equals(docTypeR)){

            if(CollUtil.isNotEmpty(getQData)) {
                String status = getQData.get(0).get("STATUS").toString();
                String bDate = getQData.get(0).get("BDATE").toString();
                String memo = getQData.get(0).get("MEMO").toString();

                String bDateF = df1.format(df2.parse(bDate));

                if ("cancel".equals(opType)) {
                    if (!status.equals("0")) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非新建不可作废！");
                    }

                    UptBean ub1 = new UptBean("DCP_PSTOCKIN");
                    //add Value
                    ub1.addUpdateValue("status", new DataValue("3", Types.VARCHAR));
                    ub1.addUpdateValue("MODIFYBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("MODIFY_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("MODIFY_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("CANCELBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("CANCEL_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("CANCEL_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    //condition
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                    ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));

                    //更新
                    this.addProcessData(new DataProcessBean(ub1));

                }
                if ("post".equals(opType)) {

                    Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                    if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                    }

                    //过帐前检查：
                    //● DCP_PSTOCKIN_BATCH与DCP_PSTOCKIN_MATERIAL数量勾稽检查：即DCP_PSTOCKIN_MATERIAL的数量与子件对应批号明细DCP_PSTOCKIN_BATCH数量之和需相等；若数量不等，根据以下判断处理：
                    //  ○ 原料仓库不允许负库存，返回报错提示“库存不足！”；
                    //  ○ 原料仓库允许负库存，DCP_PSTOCKIN_MATERIAL与DCP_PSTOCKIN_BATCH数量不等的明细写入一笔DCP_PSTOCKIN_BATCH：数量=DCP_PSTOCKIN_MATERIAL-DCP_PSTOCKIN_BATCH的差异量
                    //过账处理：
                    //● 库存交易记录DCP_STOCK_DETAIL：用DCP_PSTOCKIN_BATCH表传入生成BILLTYPE="31"类型的异动明细
                    if (!status.equals("0")) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非新建不可过账！");
                    }

                    String materialSql="select a.*,b.ISCHECKSTOCK,c.isbatch,b.ISLOCATION,d.PRODTYPE " +
                            " from DCP_PSTOCKIN_MATERIAL a " +
                            " left join dcp_pstockin_detail d on d.eid=a.eid and d.ORGANIZATIONNO=a.ORGANIZATIONNO and d.pstockinno=a.pstockinno and d.item=a.mitem " +
                            " left join dcp_goods c on a.pluno=c.pluno and a.eid=c.eid " +
                            " left join dcp_warehouse b on a.eid=b.eid and a.warehouse=b.warehouse " +
                            " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"'";
                    List<Map<String, Object>> getMData = this.doQueryData(materialSql, null);
                    String batchSql = "select a.oitem,sum(a.pqty) as pqty ,sum(a.baseqty) as baseqty from DCP_PSTOCKOUT_BATCH a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' group by a.oitem ";
                    List<Map<String, Object>> getBData = this.doQueryData(batchSql, null);
                    String batchNormalSql = "select a.* from DCP_PSTOCKOUT_BATCH a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' order by a.item ";
                    List<Map<String, Object>> getBDataN = this.doQueryData(batchNormalSql, null);

                    String detailSql="select a.* from DCP_PSTOCKIN_DETAIL a where a.eid='"+eId+"' and a.pstockinno='"+pStockInNO+"' ";
                    List<Map<String, Object>> getDData = this.doQueryData(detailSql, null);

                    UptBean ub1 = new UptBean("DCP_PSTOCKIN");
                    //add Value
                    ub1.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRMBY", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRM_DATE", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("CONFIRM_TIME", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    //condition
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                    ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));

                    //更新
                    this.addProcessData(new DataProcessBean(ub1));


                    int batchItem=0;
                    if(getBDataN.size()>0){
                        batchItem=getBDataN.stream().mapToInt(x->Integer.parseInt(x.get("ITEM").toString())).max().orElse(0);
                    }
                    for (Map<String, Object> map : getMData){
                        String item = map.get("ITEM").toString();
                        String pluNo = map.get("PLUNO").toString();
                        String isCheckStock = map.get("ISCHECKSTOCK").toString();
                        String featureNo = map.get("FEATURENO").toString();
                        String pUnit = map.get("PUNIT").toString();
                        String pQty = map.get("PQTY").toString();
                        String baseUnit = map.get("BASEUNIT").toString();
                        String baseQty = map.get("BASEQTY").toString();
                        String unitRatio = map.get("UNIT_RATIO").toString();
                        String warehouse = map.get("WAREHOUSE").toString();
                        String batchNo = map.get("BATCH_NO").toString();
                        String isBatch = map.get("ISBATCH").toString();
                        String location = map.get("LOCATION").toString();
                        String isLocation = map.get("ISLOCATION").toString();
                        String expDate = map.get("EXPDATE").toString();
                        String prodDate = map.get("PROD_DATE").toString();
                        String price = map.get("PRICE").toString();
                        String distriPrice = map.get("DISTRIPRICE").toString();
                        String amt = map.get("AMT").toString();
                        String distriAmt = map.get("DISTRIAMT").toString();
                        String prodType = map.get("PRODTYPE").toString();
                        String prodDateF = "";
                        if(!Check.Null(prodDate)){
                            prodDateF=df1.format(df2.parse(prodDate));
                        }

                        if(isBatch.equals("Y")&&isBatchNo.equals("Y")){
                            if(Check.Null(batchNo)||" ".equals(batchNo)){
                                batchNo="DEFAULTBATCH";
                            }
                        }
                        if(Check.Null(batchNo)){
                            batchNo=" ";
                        }
                        if("Y".equals(isLocation)){
                            if(Check.Null(isLocation)||" ".equals(location)){
                                location="DEFAULTLOCATION";
                            }
                        }
                        if(Check.Null(location)){
                            location=" ";
                        }

                        BcReq bcReq=new BcReq();
                        bcReq.setServiceType("PStockInProcess");
                        bcReq.setDocType(docTypeR);
                        bcReq.setBillType("31");
                        bcReq.setProdType(prodType);
                        bcReq.setDirection("-1");

                        BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                        String bType = bcMap.getBType();
                        String costCode = bcMap.getCostCode();
                        if(Check.Null(bType)||Check.Null(costCode)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                        }


                        List<Map<String, Object>> filterB = getBData.stream().filter(x -> x.get("OITEM").toString().equals(item)).collect(Collectors.toList());
                        List<Map<String, Object>> filterBn = getBDataN.stream().filter(x -> x.get("OITEM").toString().equals(item)).collect(Collectors.toList());

                        if (CollUtil.isNotEmpty(filterB)){
                            String batchPQty = filterB.get(0).get("PQTY").toString();
                            String batchBaseQty = filterB.get(0).get("BASEQTY").toString();

                            BigDecimal lastPQty = new BigDecimal(pQty).subtract(new BigDecimal(batchPQty));
                            BigDecimal lastBaseQty = new BigDecimal(baseQty).subtract(new BigDecimal(batchBaseQty));

                            BigDecimal lastAmt = lastPQty.multiply(new BigDecimal(price));
                            BigDecimal lastDistriAmt = lastPQty.multiply(new BigDecimal(distriPrice));

                            if(lastPQty.compareTo(BigDecimal.ZERO)>0){
                                if("Y".equals(isCheckStock)){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "品号"+pluNo+"库存不足！");
                                }else{
                                    //允许负库存
                                    batchItem++;
                                    ColumnDataValue mbColumns=new ColumnDataValue();
                                    mbColumns.add("EID", DataValues.newString(eId));
                                    mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                    mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                    mbColumns.add("ITEM",DataValues.newString(batchItem));
                                    mbColumns.add("OITEM",DataValues.newString(item));
                                    mbColumns.add("PLUNO",DataValues.newString(pluNo));
                                    mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                    mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                    mbColumns.add("PQTY",DataValues.newString(lastPQty));
                                    mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                    mbColumns.add("BASEQTY",DataValues.newString(lastBaseQty));
                                    mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                    mbColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                                    mbColumns.add("LOCATION",DataValues.newString(Check.Null(location)?" ":location));
                                    mbColumns.add("BATCHNO",DataValues.newString(batchNo));
                                    mbColumns.add("PRODDATE",DataValues.newString(prodDate));
                                    mbColumns.add("EXPDATE",DataValues.newString(expDate));

                                    String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                    DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                    InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                    ibmb.addValues(mbDataValues);
                                    this.addProcessData(new DataProcessBean(ibmb));

                                    String procedure = "SP_DCP_STOCKCHANGE_VX";
                                    Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                    inputParameter.put(1, eId);
                                    inputParameter.put(2, null);
                                    inputParameter.put(3, shopId);                                         //--组织
                                    inputParameter.put(4, bType);
                                    inputParameter.put(5, costCode);
                                    inputParameter.put(6, "31");                                //--单据类型
                                    inputParameter.put(7, pStockInNO);                                        //--单据号
                                    inputParameter.put(8, String.valueOf(batchItem));
                                    inputParameter.put(9, "0");//没序号给个1
                                    inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                    inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                    inputParameter.put(12, pluNo);         //--品号
                                    inputParameter.put(13, featureNo);                                   //--特征码
                                    inputParameter.put(14, warehouse);    //--仓库
                                    inputParameter.put(15, batchNo);  //--批号
                                    inputParameter.put(16, location);
                                    inputParameter.put(17, pUnit);        //--交易单位
                                    inputParameter.put(18, lastPQty);         //--交易数量
                                    inputParameter.put(19, baseUnit);     //--基准单位
                                    inputParameter.put(20, lastBaseQty);      //--基准数量
                                    inputParameter.put(21, unitRatio);   //--换算比例
                                    inputParameter.put(22, price);        //--零售价
                                    inputParameter.put(23, lastAmt.toString());          //--零售金额
                                    inputParameter.put(24, distriPrice);  //--进货价
                                    inputParameter.put(25, lastDistriAmt);    //--进货金额
                                    inputParameter.put(26, accountDateF);                                   //--入账日期 yyyy-MM-dd
                                    inputParameter.put(27, prodDate);    //--批号的生产日期 yyyy-MM-dd
                                    inputParameter.put(28, bDate);            //--单据日期
                                    inputParameter.put(29, "组合入库原料扣库存");             //--异动原因
                                    inputParameter.put(30, memo);             //--异动描述
                                    inputParameter.put(31, accountBy);          //--操作员

                                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                    this.addProcessData(new DataProcessBean(pdb));
                                }
                            }
                        }
                        else{
                            //
                            if("Y".equals(isCheckStock)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "品号"+pluNo+"库存不足！");
                            }
                            else{
                                batchItem++;
                                //允许负库存
                                ColumnDataValue mbColumns=new ColumnDataValue();
                                mbColumns.add("EID", DataValues.newString(eId));
                                mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                mbColumns.add("ITEM",DataValues.newString(batchItem));
                                mbColumns.add("OITEM",DataValues.newString(item));
                                mbColumns.add("PLUNO",DataValues.newString(pluNo));
                                mbColumns.add("FEATURENO",DataValues.newString(featureNo));
                                mbColumns.add("PUNIT",DataValues.newString(pUnit));
                                mbColumns.add("PQTY",DataValues.newString(pQty));
                                mbColumns.add("BASEUNIT",DataValues.newString(baseUnit));
                                mbColumns.add("BASEQTY",DataValues.newString(baseQty));
                                mbColumns.add("UNIT_RATIO",DataValues.newString(unitRatio));

                                mbColumns.add("WAREHOUSE",DataValues.newString(warehouse));
                                mbColumns.add("LOCATION",DataValues.newString(Check.Null(location)?" ":location));
                                mbColumns.add("BATCHNO",DataValues.newString(batchNo));
                                mbColumns.add("PRODDATE",DataValues.newString(prodDate));
                                mbColumns.add("EXPDATE",DataValues.newString(expDate));

                                String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                ibmb.addValues(mbDataValues);
                                this.addProcessData(new DataProcessBean(ibmb));


                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                            //--企业ID
                                inputParameter.put(2, null);
                                inputParameter.put(3, shopId);                                         //--组织
                                inputParameter.put(4, bType);
                                inputParameter.put(5, costCode);
                                inputParameter.put(6, "31");                                //--单据类型
                                inputParameter.put(7, pStockInNO);                                        //--单据号
                                inputParameter.put(8, "1");
                                inputParameter.put(9, "0");
                                inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, bDateF);             //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, pluNo);         //--品号
                                inputParameter.put(13, featureNo);                                   //--特征码
                                inputParameter.put(14, warehouse);    //--仓库
                                inputParameter.put(15, batchNo);     //--批号
                                inputParameter.put(16, location);
                                inputParameter.put(17, pUnit);        //--交易单位
                                inputParameter.put(18, pQty);         //--交易数量
                                inputParameter.put(19, baseUnit);     //--基准单位
                                inputParameter.put(20, baseQty);      //--基准数量
                                inputParameter.put(21, unitRatio);   //--换算比例
                                inputParameter.put(22, price);        //--零售价
                                inputParameter.put(23, amt);          //--零售金额
                                inputParameter.put(24, distriPrice);  //--进货价
                                inputParameter.put(25, distriAmt);    //--进货金额
                                inputParameter.put(26, accountDateF);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(27, prodDateF);    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(28, bDateF);            //--单据日期
                                inputParameter.put(29, "组合入库原料扣库存");             //--异动原因
                                inputParameter.put(30, memo);             //--异动描述
                                inputParameter.put(31, accountBy);          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }
                        }

                        //扣库存  上面临时添加的先扣 再根据表中的扣

                        if(CollUtil.isNotEmpty(filterBn)){
                            for (Map<String, Object> bn : filterBn){

                                String bBatchNo = bn.get("BATCHNO").toString();
                                String bLocation = bn.get("LOCATION").toString();
                                String bPQty = bn.get("PQTY").toString();
                                String bBaseQty = bn.get("BASEQTY").toString();
                                String bProdDate = bn.get("PRODDATE").toString();
                                String bProdDateF =Check.Null(bProdDate)?"": df1.format(df2.parse(bProdDate));
                                BigDecimal bAmt = new BigDecimal(bPQty).multiply(new BigDecimal(price));
                                BigDecimal bDistriAmt =new BigDecimal(bPQty).multiply(new BigDecimal(distriPrice));
                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                            //--企业ID
                                inputParameter.put(2, null);
                                inputParameter.put(3, shopId);                                         //--组织
                                inputParameter.put(4, bType);
                                inputParameter.put(5, costCode);
                                inputParameter.put(6, "31");                                //--单据类型
                                inputParameter.put(7, pStockInNO);                                        //--单据号
                                inputParameter.put(8, bn.get("ITEM").toString());
                                inputParameter.put(9, "0");
                                inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, bDate);             //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, pluNo);         //--品号
                                inputParameter.put(13, featureNo);                                   //--特征码
                                inputParameter.put(14, warehouse);    //--仓库
                                inputParameter.put(15, Check.Null(bBatchNo)?" ":bBatchNo);     //批号
                                inputParameter.put(16, bLocation);     //--location
                                inputParameter.put(17, pUnit);        //--交易单位
                                inputParameter.put(18, bPQty);         //--交易数量
                                inputParameter.put(19, baseUnit);     //--基准单位
                                inputParameter.put(20, bBaseQty);      //--基准数量
                                inputParameter.put(21, unitRatio);   //--换算比例
                                inputParameter.put(22, price);        //--零售价
                                inputParameter.put(23, bAmt);          //--零售金额
                                inputParameter.put(24, distriPrice);  //--进货价
                                inputParameter.put(25, bDistriAmt);    //--进货金额
                                inputParameter.put(26, accountDateF);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(27, bProdDateF);    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(28, bDate);            //--单据日期
                                inputParameter.put(29, "组合入库原料扣库存");             //--异动原因
                                inputParameter.put(30, memo);             //--异动描述
                                inputParameter.put(31, accountBy);          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }
                        }
                    }

                    for(Map<String, Object> dMap : getDData){
                        String pluNo = dMap.get("PLUNO").toString();
                        String featureNo = dMap.get("FEATURENO").toString();
                        String pUnit = dMap.get("PUNIT").toString();
                        String pQty = dMap.get("PQTY").toString();
                        String baseUnit = dMap.get("BASEUNIT").toString();
                        String baseQty = dMap.get("BASEQTY").toString();
                        String unitRatio = dMap.get("UNIT_RATIO").toString();
                        String warehouse = dMap.get("WAREHOUSE").toString();
                        String batchNo = dMap.get("BATCH_NO").toString();
                        String location = dMap.get("LOCATION").toString();
                        String prodDate = dMap.get("PROD_DATE").toString();
                        String price = dMap.get("PRICE").toString();
                        String distriPrice = dMap.get("DISTRIPRICE").toString();
                        String amt = dMap.get("AMT").toString();
                        String distriAmt = dMap.get("DISTRIAMT").toString();
                        String prodDateF =Check.Null(prodDate)?"":  df1.format(df2.parse(prodDate));

                        BcReq bcReq=new BcReq();
                        bcReq.setServiceType("PStockInProcess");
                        bcReq.setDocType(docTypeR);
                        bcReq.setBillType("30");
                        bcReq.setProdType(dMap.get("PRODTYPE").toString());
                        bcReq.setDirection("1");
                        BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                        String bType = bcMap.getBType();
                        String costCode = bcMap.getCostCode();
                        if(Check.Null(bType)||Check.Null(costCode)){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                        }


                        String procedure = "SP_DCP_STOCKCHANGE_VX";
                        Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1, eId);                                            //--企业ID
                        inputParameter.put(2, null);
                        inputParameter.put(3, shopId);                                         //--组织
                        inputParameter.put(4, bType);
                        inputParameter.put(5, costCode);
                        inputParameter.put(6, "30");                                //--单据类型
                        inputParameter.put(7, pStockInNO);                                        //--单据号
                        inputParameter.put(8, dMap.get("ITEM").toString());
                        inputParameter.put(9, "1");
                        inputParameter.put(10, "1");                                   //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(11, bDateF);             //--营业日期 yyyy-MM-dd
                        inputParameter.put(12, pluNo);         //--品号
                        inputParameter.put(13, featureNo);                                   //--特征码
                        inputParameter.put(14, warehouse);    //--仓库
                        inputParameter.put(15, Check.Null(batchNo)?" ":batchNo);     //--批号
                        inputParameter.put(16, location);     //--
                        inputParameter.put(17, pUnit);        //--交易单位
                        inputParameter.put(18, pQty);         //--交易数量
                        inputParameter.put(19, baseUnit);     //--基准单位
                        inputParameter.put(20, baseQty);      //--基准数量
                        inputParameter.put(21, unitRatio);   //--换算比例
                        inputParameter.put(22, price);        //--零售价
                        inputParameter.put(23, amt);          //--零售金额
                        inputParameter.put(24, distriPrice);  //--进货价
                        inputParameter.put(25, distriAmt);    //--进货金额
                        inputParameter.put(26, accountDateF);                                   //--入账日期 yyyy-MM-dd
                        inputParameter.put(27, prodDateF);    //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(28, bDateF);            //--单据日期
                        inputParameter.put(29, "拆解出库明细加库存");             //--异动原因
                        inputParameter.put(30, memo);             //--异动描述
                        inputParameter.put(31, accountBy);          //--操作员

                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));
                    }
                }

                this.doExecuteDataToDB();
            }
        }

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PStockInProcessReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PStockInProcessReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PStockInProcessReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PStockInProcessReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String pStockInNO = req.getRequest().getPStockInNo();
		if(Check.Null(pStockInNO)){
			errMsg.append("完工入库单单号不可为空值, ");
			isFail = true;
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_PStockInProcessReq> getRequestType() {
		return new TypeToken<DCP_PStockInProcessReq>(){};
	}

	@Override
	protected DCP_PStockInProcessRes getResponseType() {
		return new DCP_PStockInProcessRes();
	}

	/**
	 * 完工入库查询
	 * @param req
	 * @return
	 **/
	protected String GetDCP_PSTOCKIN_SQL(DCP_PStockInProcessReq req) {
		String sql=" "
				+ " select a.oofno,a.ootype,a.pstockinno,a.bdate,a.otype,a.ofno,a.memo,a.load_doctype,a.load_docno,a.doc_type,a.account_date,"
				+ " b.warehouse,b.item,b.oitem,b.pluno,b.punit,b.pqty,b.baseunit,b.unit_ratio,b.baseqty,b.batch_no,"
				+ " b.prod_date,b.price,b.amt,b.scrap_qty,b.bsno,b.distriprice,b.distriamt,b.featureno,"
				+ " c.warehouse as mat_warehouse,c.item as mat_item,c.mpluno as mat_mpluno,c.mitem as mat_mitem,"
				+ " c.pluno as mat_pluno,c.punit mat_punit,c.pqty as mat_pqty,c.baseunit as mat_baseunit,"
				+ " c.baseqty as mat_baseqty,c.unit_ratio as mat_unit_ratio,c.finalprodbaseqty as mat_finalprodbaseqty,"
				+ " c.rawmaterialbaseqty as mat_rawmaterialbaseqty,c.price as mat_price,c.amt as mat_amt,"
				+ " c.batch_no as mat_batch_no,c.prod_date as mat_prod_date,c.distriprice as mat_distriprice,"
				+ " c.distriamt as mat_distriamt,c.isbuckle as mat_isbuckle, c.featureno as mat_featureno,b.prodtype,b.LOCATION "
				+ " from dcp_pstockin a"
				+ " inner join dcp_pstockin_detail b on a.eid=b.eid and a.organizationno=b.organizationno "
				+ "  and a.pstockinno=b.pstockinno and a.account_date=b.account_date "
				+ " inner join dcp_pstockin_material c on a.eid=c.eid and a.organizationno=c.organizationno "
				+ "  and a.pstockinno=c.pstockinno and a.account_date=c.account_date and b.item=c.mitem "
				+ " where a.pstockinno='"+req.getRequest().getPStockInNo()+"' "
				+ " and a.eid='"+req.geteId()+"' and a.shopid='"+req.getShopId()+"' and status='0' ";
		return sql;
	}
	protected String GetTW_PSTOCKIN_MaterialSql(DCP_PStockInProcessReq req) {
		String sql="select a.PSTOCKINNO,a.BDATE,a.OTYPE,a.OFNO,a.MEMO,CREATEBY,CREATE_DATE,CREATE_TIME,ACCOUNTBY,"
				+ " a.ACCOUNT_DATE,ACCOUNT_TIME,LOAD_DOCTYPE,LOAD_DOCNO,m.MITEM AS MITEM,m.ITEM,OITEM,m.pluno as PLUNO,m.mpluno as MPLUNO,"
				+ " m.PUNIT,m.PQTY,m.BASEUNIT,m.UNIT_RATIO,m.BASEQTY,m.PRICE,m.AMT,b.SCRAP_QTY,BSNO,m.WAREHOUSE,"
				+ " m.BATCH_NO,m.PROD_DATE,m.DISTRIPRICE,NVL(m.ISBUCKLE,'Y') as ISBUCKLE "
				+ ",m.FINALPRODBASEQTY,m.RAWMATERIALBASEQTY  "
				+ " from DCP_PSTOCKIN a inner join DCP_PSTOCKIN_detail b on a.EID=b.EID "
				+ " and a.Organizationno=b.Organizationno and a.PSTOCKINNO=b.PSTOCKINNO and a.ACCOUNT_DATE=b.ACCOUNT_DATE "
				+ " inner join DCP_PSTOCKIN_material m on a.EID=m.EID and a.Organizationno=m.Organizationno "
				+ " and a.PSTOCKINNO=m.PSTOCKINNO and a.ACCOUNT_DATE=m.ACCOUNT_DATE "
				+ " and b.item = m.mitem and b.pluno = m.mpluno "
				+ " where a.PSTOCKINNO='"+req.getRequest().getPStockInNo()+"' and a.EID='"+req.geteId()+"' AND a.SHOPID='"+req.getShopId()+"' "
				//+ " and (m.ISBUCKLE='Y' or m.ISBUCKLE is null) 
				+ "";
		return sql;
	}


}
