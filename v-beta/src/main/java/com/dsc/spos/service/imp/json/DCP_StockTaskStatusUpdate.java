package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockTaskStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_StockTaskStatusUpdate extends SPosAdvanceService<DCP_StockTaskStatusUpdateReq, DCP_StockTaskStatusUpdateRes> {

    private static final String TYPE_CONFIRM = "confirm";
    private static final String TYPE_UNCONFIRM = "unconfirm";
    private static final String TYPE_CANCEL = "cancel";

    @Override
    protected boolean isVerifyFail(DCP_StockTaskStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockTaskStatusUpdateReq.LevelElm request = req.getRequest();

        if (Check.Null(request.getStockTaskNo())){
            errMsg.append("任务单号不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getOprType())){
            errMsg.append("操作类型不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockTaskStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_StockTaskStatusUpdateReq>(){};
    }

    @Override
    protected DCP_StockTaskStatusUpdateRes getResponseType() {
        return new DCP_StockTaskStatusUpdateRes();
    }

    @Override
    public void processDUID(DCP_StockTaskStatusUpdateReq req,DCP_StockTaskStatusUpdateRes res) throws Exception {
        //枚举: confirm：审核,unconfirm：取消审核,cancel：作废
        String isStockMultipleUnit = PosPub.getPARA_SMS(dao, req.geteId(), req.getOrganizationNO(), "IsStockMultipleUnit");

        String employeeNo = req.getEmployeeNo();
        String eId = req.geteId();
        String oprType = req.getRequest().getOprType();
        String stockTaskNo = req.getRequest().getStockTaskNo();

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
        String createDate = df.format(cal.getTime());
        df=new SimpleDateFormat("HHmmss");
        String createTime = df.format(cal.getTime());

        String sql = this.getQuerySql(req);
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if (CollUtil.isEmpty(list)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E404, "任务单不存在");
        }
        Map<String, Object> singleData = list.get(0);
        String status = singleData.get("STATUS").toString();
        String bDate = singleData.get("BDATE").toString();
        String pTemplateNo = singleData.get("PTEMPLATENO").toString();
        String shopId = singleData.get("SHOPID").toString();
        String docType = singleData.get("DOC_TYPE").toString();
        String memo = singleData.get("MEMO").toString();
        String totCqty = singleData.get("TOTCQTY").toString();
        String isBtake = singleData.get("IS_BTAKE").toString();
        String taskWay = singleData.get("TASKWAY").toString();
        String notGoodsMode = singleData.get("NOTGOODSMODE").toString();
        String isAdjustStock = singleData.get("IS_ADJUST_STOCK").toString();
        String employeeId = singleData.get("EMPLOYEEID").toString();
        String departId = singleData.get("DEPARTID").toString();

        //opType=cancel(作废）：
        //① 作废前数据检查：
        //● 单据状态STATUS<>"0"(新建)不可作废；
        //
        //② 作废数据处理：
        //● 更新单据状态STATUS="8"(已作废)；
        //● 记录单据异动字段：作废人、作废日期、作废时间、修改人、修改日期、修改时间
        if(TYPE_CANCEL.equals(oprType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "任务单状态非新建，不可作废");
            }

            UptBean ub2 = new UptBean("DCP_STOCKTASK");
            ub2.addUpdateValue("STATUS", DataValues.newString("8"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("MODIFY_DATE",DataValues.newString(createDate));
            ub2.addUpdateValue("MODIFY_TIME",DataValues.newString(createTime));
            ub2.addUpdateValue("CANCELBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("CANCEL_DATE", DataValues.newString(createDate));
            ub2.addUpdateValue("CANCEL_TIME", DataValues.newString(createTime));


            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("STOCKTASKNO",DataValues.newString(stockTaskNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        //opType=unConfirm(取消审核）:
        //① 取消审核前数据检查：
        //● 单据状态STATUS<>"6"(待盘点)不可取消审核；
        //● DCP_STOCKTASK_ORG关联库存盘点单DCP_STOCKTAKE检查单据状态，非所有单据状态=“0”（待盘点）不可取消审核！（只要有一张库存盘点开始盘点则不可取消）
        //
        //② 取消审核处理：
        //● 删除关联的库存盘点单数据，表：DCP_STOCKTAKE、DCP_STOCKTAKE_DETAIL、DCP_STOCKTAKE_DETAIL_UNIT
        //● 清空字段值：子任务单号DCP_STOCKTASK_ORG.SUBTASKNO=NULL、库存盘点单号：DCP_STOCKTASK_ORG.STOCKTAKENO=NULL;
        //● 更新单据状态STATUS="0"(新建)；
        //● 更新单据异动字段：修改人、修改日期、修改时间；清空字段值：审核人、审核日期、审核时间、
        if(TYPE_UNCONFIRM.equals(oprType)){
            if(!status.equals("6")){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "任务单状态非待盘点，不可取消审核");
            }


            UptBean ub2 = new UptBean("DCP_STOCKTASK");
            ub2.addUpdateValue("STATUS", DataValues.newString("0"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("MODIFY_DATE",DataValues.newString(createDate));
            ub2.addUpdateValue("MODIFY_TIME",DataValues.newString(createTime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(""));
            ub2.addUpdateValue("CONFIRM_DATE", DataValues.newString(""));
            ub2.addUpdateValue("CONFIRM_TIME", DataValues.newString(""));


            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("STOCKTASKNO",DataValues.newString(stockTaskNo));
            this.addProcessData(new DataProcessBean(ub2));


            //② 取消审核处理：
            //● 删除关联的库存盘点单数据，表：DCP_STOCKTAKE、DCP_STOCKTAKE_DETAIL、DCP_STOCKTAKE_DETAIL_UNIT
            //● 清空字段值：子任务单号DCP_STOCKTASK_ORG.SUBTASKNO=NULL、库存盘点单号：DCP_STOCKTASK_ORG.STOCKTAKENO=NULL;

            String taskOrgSql="select nvl(b.stocktakeno,'') as stocktakeno,a.organizationno from DCP_STOCKTASK_ORG a " +
                    " left join dcp_stocktake b on a.eid=b.eid and a.stocktakeno=b.stocktakeno and a.organizationno=b.organizationno  " +
                    " where a.eid='"+req.geteId()+"' " +
                    " and a.STOCKTASKNO='"+req.getRequest().getStockTaskNo()+"'";
            List<Map<String, Object>> taskOrgList = this.doQueryData(taskOrgSql, null);
            if(taskOrgList.size()>0){
                for(Map<String, Object> taskOrg:taskOrgList){
                    String stockTakeNo = taskOrg.get("STOCKTAKENO").toString();
                    String sOrganizationNo = taskOrg.get("ORGANIZATIONNO").toString();
                    if(Check.Null(stockTakeNo)){
                        continue;
                    }
                    //DCP_STOCKTAKE_DETAIL
                    DelBean db2 = new DelBean("DCP_STOCKTAKE_DETAIL");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db2.addCondition("StockTakeNO", new DataValue(stockTakeNo, Types.VARCHAR));
                    db2.addCondition("OrganizationNO", new DataValue(sOrganizationNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));

                    //DCP_STOCKTAKE
                    DelBean db1 = new DelBean("DCP_STOCKTAKE");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("StockTakeNO", new DataValue(stockTakeNo, Types.VARCHAR));
                    db1.addCondition("OrganizationNO", new DataValue(sOrganizationNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    //DCP_STOCKTAKE_DETAIL_UNIT
                    DelBean db3 = new DelBean("DCP_STOCKTAKE_DETAIL_UNIT");
                    db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db3.addCondition("StockTakeNO", new DataValue(stockTakeNo, Types.VARCHAR));
                    db3.addCondition("OrganizationNO", new DataValue(sOrganizationNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db3));

                }
            }

            UptBean ub3 = new UptBean("DCP_STOCKTASK_ORG");
            ub3.addUpdateValue("SUBTASKNO", DataValues.newString(""));
            ub3.addUpdateValue("STOCKTAKENO", DataValues.newString(""));

            ub3.addCondition("EID", DataValues.newString(eId));
            ub3.addCondition("STOCKTASKNO",DataValues.newString(stockTaskNo));
            this.addProcessData(new DataProcessBean(ub3));


        }

        //opType=confirm(审核）:
        //① 审核前数据检查：
        //● 单据状态STATUS<>"0"(新建)不可审核；
        //● 盘点日期BDATE< 系统日期不可审核；
        //● 盘点日期+盘点模板+组织编号+仓库编号已存在有效库存盘点单不可审核！
        //
        //② 审核处理：
        //● 生成盘点子任务单号：更新DCP_STOCKTASK_ORG.SUBTASKNO，编码格式=STOCKTASKNO_ITEM；
        //● 每行子任务单生成一张【待盘点】的库存盘点单，回写更新DCP_STOCKTASK_ORG.STOCKTAKENO=库存盘点单号（注意：库存盘点商品明细需展开至商品-批号-库位）
        //● 更新单据状态STATUS="6"(待盘点)；
        //● 更新单据异动字段：审核人、审核日期、审核时间、修改人、修改日期、修改时间
        if(TYPE_CONFIRM.equals(oprType)){
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "任务单状态非新建，不可审核");
            }
            if(bDate.compareTo(createDate)<0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "任务单日期小于系统日期，不可审核");
            }

            String validSql1="select a.organizationno,a.warehouse from DCP_STOCKTAKE a where a.bdate='"+bDate+"' and a.PTEMPLATENO='"+pTemplateNo+"'";
            List<Map<String, Object>> list1 = this.doQueryData(validSql1, null);
            String orgSql=" select * from DCP_STOCKTASK_ORG a where a.eid='"+eId+"' and a.stocktaskno='"+stockTaskNo+"' ";
            List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);

            String detailSql="select * from DCP_STOCKTASK_DETAIL a where a.eid='"+eId+"' and a.stocktaskno='"+stockTaskNo+"'";
            List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

            String detailUnitSql="select b.ounit,b.qty,b.oqty from DCP_STOCKTASK_DETAIL a " +
                    " inner join DCP_GOODS_UNIT b on a.eid=b.eid and a.pluno=b.pluno where a.eid='"+eId+"'  and a.stocktaskno='"+stockTaskNo+"'";
            List<Map<String, Object>> unitList = this.doQueryData(detailUnitSql, null);

            if(CollUtil.isEmpty(orgList)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未找到盘点任务范围");
            }
            String priceLength = PosPub.getPARA_SMS(dao, eId, shopId, "priceLength");
            String distriPriceLength = PosPub.getPARA_SMS(dao, eId, shopId, "distriPriceLength");
            if (Check.Null(priceLength)||!PosPub.isNumeric(priceLength)) {
                priceLength="2";
            }
            if (Check.Null(distriPriceLength)||!PosPub.isNumeric(distriPriceLength)) {
                distriPriceLength="2";
            }
            int priceLengthInt = Integer.parseInt(priceLength);
            int distriPriceLengthInt = Integer.parseInt(distriPriceLength);


            //处理成品零售价和配送价
            List<Map<String, Object>> plus = new ArrayList<>();
            Map<String, Boolean> condition = new HashMap<>(); //查詢條件
            condition.put("PLUNO", true);
            List<Map<String, Object>> getQPlu=MapDistinct.getMap(detailList, condition);
            for (Map<String, Object> onePlu :getQPlu ) {
                Map<String, Object> plu = new HashMap<>();
                plu.put("PLUNO", onePlu.get("PLUNO").toString());
                plu.put("PUNIT", onePlu.get("PUNIT").toString());
                plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
                plu.put("UNITRATIO", onePlu.get("UNIT_RATIO").toString());
                plus.add(plu);
            }

            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(dao,eId, req.getBELFIRM(), shopId,plus, req.getBELFIRM());


            for (Map<String, Object> map : orgList){
                String orgNo = map.get("ORGANIZATIONNO").toString();
                String warehouse = map.get("WAREHOUSE").toString();
                String stockTakeNo = map.get("STOCKTAKENO").toString();
                if(!Check.Null(stockTakeNo)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已生成盘点单");
                }

                List<Map<String, Object>> collect = list1.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(orgNo) & x.get("WAREHOUSE").toString().equals(warehouse)).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(collect)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已生成盘点单");
                }

            }

            for (Map<String, Object> map : orgList){
                String orgNo = map.get("ORGANIZATIONNO").toString();
                String warehouse = map.get("WAREHOUSE").toString();
                String subTaskNo = map.get("SUBTASKNO").toString();
                String item = map.get("ITEM").toString();
                //每一条subtaskNo生成一条盘点单
                String stockTakeNo = this.getOrderNO(req, orgNo, "KCPD");

                String batchSql="select a.*,to_char(a.PRODDATE,'yyyyMMdd') PRODDATESTR from MES_BATCH_STOCK_DETAIL a where a.eid='"+eId+"' " +
                        " and a.organizationno='"+orgNo+"' and a.warehouse='"+warehouse+"' ";
                List<Map<String, Object>> batchList = this.doQueryData(batchSql, null);


                if(CollUtil.isNotEmpty(detailList)){
                    int detailItem=0;
                    for (Map<String, Object> detail : detailList){
                        String pluNo = detail.get("PLUNO").toString();
                        String baseUnit = detail.get("BASEUNIT").toString();
                        String oItem = detail.get("ITEM").toString();
                        String pUnit = detail.get("PUNIT").toString();
                        String unitRatio = detail.get("UNIT_RATIO").toString();
                        String featureNo = detail.get("FEATURENO").toString();
                        if(Check.Null(featureNo)){
                            featureNo=" ";
                        }
                        List<Map<String, Object>> batchPlu = batchList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                        String batchNo=" ";
                        String prodDate="";
                        String refQty="0";
                        String location=" ";
                        if(CollUtil.isNotEmpty(batchPlu)){
                            batchNo = batchPlu.get(0).get("BATCHNO").toString();
                            prodDate = batchPlu.get(0).get("PRODDATESTR").toString();
                            refQty=batchPlu.get(0).get("QTY").toString();
                            location=batchPlu.get(0).get("LOCATION").toString();
                        }

                        String price="0";
                        String distriPrice="0";
                        Map<String, Object> condiV= new HashMap<>();
                        condiV.put("PLUNO",pluNo);
                        condiV.put("PUNIT",pUnit);
                        List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
                        if(priceList!=null && priceList.size()>0 ) {
                            price=priceList.get(0).get("PRICE").toString();
                            distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                            BigDecimal price_b=new BigDecimal(price);
                            price_b=price_b.setScale(priceLengthInt, RoundingMode.HALF_UP);
                            price=price_b.toPlainString();
                            BigDecimal distriPrice_b=new BigDecimal(distriPrice);
                            distriPrice_b=distriPrice_b.setScale(distriPriceLengthInt, RoundingMode.HALF_UP);
                            distriPrice=distriPrice_b.toPlainString();
                        }



                        detailItem++;
                        ColumnDataValue detailColumns=new ColumnDataValue();
                        detailColumns.add("STOCKTAKENO",stockTakeNo, Types.VARCHAR);
                        detailColumns.add("SHOPID",shopId,Types.VARCHAR);

                        detailColumns.add("ITEM",detailItem,Types.VARCHAR);
                        detailColumns.add("EID",eId,Types.VARCHAR);
                        detailColumns.add("ORGANIZATIONNO",orgNo,Types.VARCHAR);
                        detailColumns.add("WAREHOUSE",warehouse,Types.VARCHAR);
                        detailColumns.add("PLUNO",pluNo,Types.VARCHAR);
                        detailColumns.add("BATCH_NO",batchNo,Types.VARCHAR);
                        detailColumns.add("PROD_DATE",prodDate,Types.VARCHAR);
                        detailColumns.add("BASEUNIT",baseUnit,Types.VARCHAR);
                        detailColumns.add("OITEM",oItem,Types.VARCHAR);
                        detailColumns.add("PQTY",0,Types.VARCHAR);
                        detailColumns.add("REF_BASEQTY",refQty,Types.VARCHAR);
                        detailColumns.add("PRICE",price,Types.VARCHAR);
                        detailColumns.add("DISTRIPRICE",distriPrice,Types.VARCHAR);
                        detailColumns.add("BASEQTY",0,Types.VARCHAR);
                        detailColumns.add("AMT",0,Types.VARCHAR);
                        detailColumns.add("DISTRIAMT",0,Types.VARCHAR);
                        detailColumns.add("PUNIT",pUnit,Types.VARCHAR);
                        detailColumns.add("UNIT_RATIO",unitRatio,Types.VARCHAR);
                        detailColumns.add("MEMO","",Types.VARCHAR);
                        detailColumns.add("BDATE",bDate,Types.VARCHAR);
                        detailColumns.add("FEATURENO",featureNo,Types.VARCHAR);
                        detailColumns.add("FQTY",0,Types.VARCHAR);
                        detailColumns.add("FBASEQTY",0,Types.VARCHAR);
                        detailColumns.add("RQTY",0,Types.VARCHAR);
                        detailColumns.add("RBASEQTY",0,Types.VARCHAR);
                        detailColumns.add("PARTITION_DATE",createDate,Types.VARCHAR);
                        detailColumns.add("LOCATION",location,Types.VARCHAR);
                        //detailColumns.add("ISBATCHADD","",Types.VARCHAR);
                        //detailColumns.add("SHAREQTY",",Types.VARCHAR);
                        //detailColumns.add("MESCONFIRM",",Types.VARCHAR);



                        String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                        DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib1=new InsBean("DCP_STOCKTAKE_DETAIL",detailColumnNames);
                        ib1.addValues(detailDataValues);
                        this.addProcessData(new DataProcessBean(ib1));

                        if("Y".equals(isStockMultipleUnit)&&CollUtil.isNotEmpty(unitList)){
                            int unitItem=0;
                            for (Map<String, Object> unit : unitList){

                                String unitQty = unit.get("QTY").toString();
                                String unitOqty = unit.get("OQTY").toString();
                                String unitUnitRatio="0";
                                if(!Check.Null(unitOqty)){
                                    unitUnitRatio=new BigDecimal(unitQty).divide(new BigDecimal(unitOqty),2, RoundingMode.HALF_UP).toString();
                                }
                                unitItem++;
                                ColumnDataValue unitColumns=new ColumnDataValue();
                                unitColumns.add("STOCKTAKENO",stockTakeNo, Types.VARCHAR);
                                unitColumns.add("SHOPID",shopId,Types.VARCHAR);
                                unitColumns.add("ITEM",unitItem,Types.VARCHAR);
                                unitColumns.add("EID",eId,Types.VARCHAR);
                                unitColumns.add("ORGANIZATIONNO",orgNo,Types.VARCHAR);

                                unitColumns.add("OITEM",detailItem,Types.VARCHAR);
                                unitColumns.add("PQTY",0,Types.VARCHAR);
                                unitColumns.add("PUNIT",unit.get("OUNIT").toString(),Types.VARCHAR);
                                unitColumns.add("PARTITION_DATE",createDate,Types.VARCHAR);
                                unitColumns.add("UNIT_RATIO",unitUnitRatio,Types.VARCHAR);

                                String[] unitColumnNames = unitColumns.getColumns().toArray(new String[0]);
                                DataValue[] unitDataValues = unitColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ib2=new InsBean("DCP_STOCKTAKE_DETAIL_UNIT",unitColumnNames);
                                ib2.addValues(unitDataValues);
                                this.addProcessData(new DataProcessBean(ib2));
                            }
                        }

                    }
                }

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("SHOPID",orgNo,Types.VARCHAR);
                mainColumns.add("STOCKTAKENO",stockTakeNo,Types.VARCHAR);
                mainColumns.add("STOCKTAKE_ID",PosPub.getGUID(false),Types.VARCHAR);
                mainColumns.add("EID",eId,Types.VARCHAR);
                mainColumns.add("ORGANIZATIONNO",orgNo,Types.VARCHAR);
                mainColumns.add("CREATEBY",employeeNo,Types.VARCHAR);
                mainColumns.add("CREATE_DATE",createDate,Types.VARCHAR);
                mainColumns.add("CREATE_TIME",createTime,Types.VARCHAR);
                mainColumns.add("OTYPE","1",Types.VARCHAR);
                mainColumns.add("DOC_TYPE",docType,Types.VARCHAR);
                mainColumns.add("MEMO",memo,Types.VARCHAR);
                //mainColumns.add("LOAD_DOCTYPE",stockTakeNo,Types.VARCHAR);
                //mainColumns.add("LOAD_DOCNO",stockTakeNo,Types.VARCHAR);
                mainColumns.add("TOT_CQTY",totCqty,Types.VARCHAR);
                mainColumns.add("BDATE",bDate,Types.VARCHAR);
                mainColumns.add("IS_BTAKE",isBtake,Types.VARCHAR);
                mainColumns.add("TOT_PQTY","0",Types.VARCHAR);
                mainColumns.add("TOT_AMT","0",Types.VARCHAR);
                mainColumns.add("TOT_DISTRIAMT","0",Types.VARCHAR);
                mainColumns.add("OFNO",subTaskNo,Types.VARCHAR);
                mainColumns.add("PTEMPLATENO",pTemplateNo,Types.VARCHAR);
                mainColumns.add("WAREHOUSE",warehouse,Types.VARCHAR);
                mainColumns.add("EMPLOYEEID",employeeId,Types.VARCHAR);
                mainColumns.add("DEPARTID",departId,Types.VARCHAR);
                mainColumns.add("TASKWAY",taskWay,Types.VARCHAR);
                mainColumns.add("NOTGOODSMODE",notGoodsMode,Types.VARCHAR);
                mainColumns.add("IS_ADJUST_STOCK",isAdjustStock,Types.VARCHAR);
                mainColumns.add("STATUS","0",Types.VARCHAR);
                mainColumns.add("PARTITION_DATE",createDate,Types.VARCHAR);


                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKTAKE",mainColumnNames);
                ib1.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib1));


                UptBean ub2 = new UptBean("DCP_STOCKTASK_ORG");
                ub2.addUpdateValue("STOCKTAKENO", DataValues.newString(stockTakeNo));

                ub2.addCondition("EID", DataValues.newString(eId));
                ub2.addCondition("STOCKTASKNO",DataValues.newString(stockTaskNo));
                ub2.addCondition("ITEM",DataValues.newString(item));
                this.addProcessData(new DataProcessBean(ub2));
            }

            UptBean ub2 = new UptBean("DCP_STOCKTASK");
            ub2.addUpdateValue("STATUS", DataValues.newString("6"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("MODIFY_DATE",DataValues.newString(createDate));
            ub2.addUpdateValue("MODIFY_TIME",DataValues.newString(createTime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(employeeNo));
            ub2.addUpdateValue("CONFIRM_DATE", DataValues.newString(createDate));
            ub2.addUpdateValue("CONFIRM_TIME", DataValues.newString(createTime));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("STOCKTASKNO",DataValues.newString(stockTaskNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockTaskStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockTaskStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockTaskStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_StockTaskStatusUpdateReq req) throws Exception {
        StringBuilder sb=new StringBuilder();
        sb.append("select * from DCP_STOCKTASK a where a.eid='"+req.geteId()+"' " +
                " and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"' " +
                " and a.STOCKTASKNO='"+req.getRequest().getStockTaskNo()+"' ");

        return sb.toString();
    }



}

