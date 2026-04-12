package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutNoticeStatusUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TransApplyStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_StockOutNoticeRes;
import com.dsc.spos.json.cust.res.DCP_TransApplyStatusUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_TransApplyStatusUpdate extends SPosAdvanceService<DCP_TransApplyStatusUpdateReq, DCP_TransApplyStatusUpdateRes> {

    @Override
    protected boolean isVerifyFail(DCP_TransApplyStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_TransApplyStatusUpdateReq.LevelElm request = req.getRequest();

        List<DCP_TransApplyStatusUpdateReq.GoodsList> goodsList = request.getGoodsList();
        if(CollUtil.isNotEmpty(goodsList)){
            for (DCP_TransApplyStatusUpdateReq.GoodsList goods : goodsList) {
                if(Check.Null(goods.getItem())){
                    errMsg.append("项次不能为空");
                    isFail = true;
                }
                if(Check.Null(goods.getPQty())){
                    errMsg.append("核准数量不能为空");
                    isFail = true;
                }
            }
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_TransApplyStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_TransApplyStatusUpdateReq>(){};
    }

    @Override
    protected DCP_TransApplyStatusUpdateRes getResponseType() {
        return new DCP_TransApplyStatusUpdateRes();
    }

    @Override
    public void processDUID(DCP_TransApplyStatusUpdateReq req,DCP_TransApplyStatusUpdateRes res) throws Exception {
        //枚举: submit：提交,withdraw：撤销,cancel：作废,approve：核准,reject：驳回,close：结案

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String opType = req.getRequest().getOpType();
        String employeeNo = req.getEmployeeNo();
        String billNo = req.getRequest().getBillNo();
        String departmentNo = req.getDepartmentNo();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String bDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String dbtz="";
        String querySql = this.getQuerySql(req);
        List<Map<String, Object>> list = this.doQueryData(querySql, null);
        if(CollUtil.isEmpty(list)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在");
        }
        Map<String, Object> tp = list.get(0);
        String tpStatus = tp.get("STATUS").toString();
        String pTemplateNo = tp.get("PTEMPLATENO").toString();
        String transOutWarehouse = tp.get("TRANSOUTWAREHOUSE").toString();
        String transInOrgNo = tp.get("TRANSINORGNO").toString();
        String rDate = tp.get("RDATE").toString();
        String transOutOrgNo = tp.get("TRANSOUTORGNO").toString();
        String mainMemo = tp.get("MEMO").toString();
        String transInWarehouse = tp.get("TRANSINWAREHOUSE").toString();
        String isTraninConfirm = tp.get("ISTRANINCONFIRM").toString();
        String transType = tp.get("TRANSTYPE").toString();
        String applyType = tp.get("APPLYTYPE").toString();
        String billType="";
        //TRANSTYPE=1.调拨，BILLTYPE='5';5-调拨通知
        //TRANSTYPE=2.移仓，BILLTYPE='6';6-移仓通知
        if(transType.equals("1")){
            billType = "5";
        }else if(transType.equals("2")){
            billType = "6";
        }

        if(opType.equals(Constant.OPR_TYPE_SUBMIT)) {
            //单据状态非“0新建”不可提交单据！
                if (!tpStatus.equals("0")) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[新建]不可提交单据！");
                }
                //● 更新单据行状态DCP_TRANSAPPLY_DETAIL.STATUS=“1待核准”;
                // ● 更新单据状态DCP_TRANSAPPLY.STATU=“1待核准”，更新提交人、提交时间、修改人、修改时间；
                //● 判断调出组织参数【IsTransApplyAutoConfirm-调拨申请是否自动核准Y-是N-否】，当参数值=N，结束处理；当参数值=Y时，则调用"approve"(核准)处理逻辑生成调拨出货通知单并背景执行审核
                UptBean ub2 = new UptBean("DCP_TRANSAPPLY");
                ub2.addUpdateValue("STATUS", DataValues.newString("1"));
                ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("MODIFYTIME", DataValues.newDate(lastmoditime));
                ub2.addUpdateValue("SUBMITBY", DataValues.newString(req.getOpNO()));
                ub2.addUpdateValue("SUBMITTIME", DataValues.newDate(lastmoditime));

                ub2.addCondition("EID", DataValues.newString(eId));
                ub2.addCondition("BILLNO", DataValues.newString(billNo));
                this.addProcessData(new DataProcessBean(ub2));

                //如果DCP_TRANSAPPLY_SOURCE不为空
            //OTYPE=0，更新DCP_PROCESSTASK_DETAIL.PICKSTATUS='Y'，
            // 其中DCP_PROCESSTASK.PROCESSTASKNO=DCP_TRANSAPPLY_SOURCE.OFNO,DCP_PROCESSTASK.ITEM=DCP_TRANSAPPLY_SOURCE.OITEM;
            //OTYPE=1，更新MES_BATCHTASK_MATERIAL.PICKSTATUS='Y'，
            // 其中MES_BATCHTASK_MATERIAL.PROCESSTASKNO=DCP_TRANSAPPLY_SOURCE.OFNO,MES_BATCHTASK_MATERIAL.MATERIAL_PLUNO=DCP_TRANSAPPLY_DETAIL.PLUNO
            String sourceSql="select * from dcp_transapply_source a where a.eid='"+eId+"' and a.billno='"+billNo+"' ";
            List<Map<String, Object>> sourceList = this.doQueryData(sourceSql, null);
            if(CollUtil.isNotEmpty(sourceList)){
                for (Map<String, Object> source : sourceList){
                    String oType = source.get("OTYPE").toString();
                    String ofNo = source.get("OFNO").toString();
                    String oItem = source.get("OITEM").toString();
                    String pluno = source.get("PLUNO").toString();
                    if("0".equals(oType)){
                        UptBean ub3 = new UptBean("DCP_PROCESSTASK_DETAIL");
                        ub3.addUpdateValue("PICKSTATUS", DataValues.newString("Y"));

                        ub3.addCondition("EID", DataValues.newString(eId));
                        ub3.addCondition("PROCESSTASKNO", DataValues.newString(ofNo));
                        ub3.addCondition("ITEM", DataValues.newString(oItem));
                        this.addProcessData(new DataProcessBean(ub3));
                    }else if("1".equals(oType)){
                        UptBean ub3 = new UptBean("MES_BATCHTASK_MATERIAL");
                        ub3.addUpdateValue("PICKSTATUS", DataValues.newString("Y"));

                        ub3.addCondition("EID", DataValues.newString(eId));
                        ub3.addCondition("PROCESSTASKNO", DataValues.newString(ofNo));
                        ub3.addCondition("MATERIAL_PLUNO", DataValues.newString(pluno));
                        this.addProcessData(new DataProcessBean(ub3));
                    }
                }
            }


        }

        if(opType.equals(Constant.OPR_TYPE_WITHDRAW)){
            if (!tpStatus.equals("1")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[待核准]不可撤销单据！");
            }
            UptBean ub2 = new UptBean("DCP_TRANSAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("0"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("MODIFYTIME", DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("SUBMITBY", DataValues.newString(""));
            ub2.addUpdateValue("SUBMITTIME", DataValues.newDate(null));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO", DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));

            String sourceSql="select * from dcp_transapply_source a where a.eid='"+eId+"' and a.billno='"+billNo+"' ";
            List<Map<String, Object>> sourceList = this.doQueryData(sourceSql, null);
            if(CollUtil.isNotEmpty(sourceList)){
                for (Map<String, Object> source : sourceList){
                    String oType = source.get("OTYPE").toString();
                    String ofNo = source.get("OFNO").toString();
                    String oItem = source.get("OITEM").toString();
                    String pluno = source.get("PLUNO").toString();
                    if("0".equals(oType)){
                        UptBean ub3 = new UptBean("DCP_PROCESSTASK_DETAIL");
                        ub3.addUpdateValue("PICKSTATUS", DataValues.newString("N"));

                        ub3.addCondition("EID", DataValues.newString(eId));
                        ub3.addCondition("PROCESSTASKNO", DataValues.newString(ofNo));
                        ub3.addCondition("ITEM", DataValues.newString(oItem));
                        this.addProcessData(new DataProcessBean(ub3));
                    }else if("1".equals(oType)){
                        UptBean ub3 = new UptBean("MES_BATCHTASK_MATERIAL");
                        ub3.addUpdateValue("PICKSTATUS", DataValues.newString("N"));

                        ub3.addCondition("EID", DataValues.newString(eId));
                        ub3.addCondition("PROCESSTASKNO", DataValues.newString(ofNo));
                        ub3.addCondition("MATERIAL_PLUNO", DataValues.newString(pluno));
                        this.addProcessData(new DataProcessBean(ub3));
                    }
                }
            }

        }

        if(opType.equals(Constant.OPR_TYPE_CANCEL)){
            if (!tpStatus.equals("0")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[新建]不可作废单据！");
            }

            UptBean ub2 = new UptBean("DCP_TRANSAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("6"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("MODIFYTIME", DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CANCELBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CANCELTIME", DataValues.newDate(lastmoditime));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO", DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));

            UptBean ub3 = new UptBean("DCP_TRANSAPPLY_DETAIL");
            ub3.addUpdateValue("STATUS", DataValues.newString(""));

            ub3.addCondition("EID", DataValues.newString(eId));
            ub3.addCondition("BILLNO", DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub3));
        }

      //检查项：
        //1. 单据状态非“1待核准”不可作废单据！
        //
        //处理逻辑：（执行前单据锁定不可被其他事务异动）
        //● 传入goodsList[]为空，则整单核准：则更新单据所有行状态DCP_TRANSAPPLY_DETAIL.STATUS=“2.已核准”，并且更新pQty=poQty；
        //● 传入goodsList[]不为空，则按明细核准：逐行item更新字段pQty=传入值，当pQty<=0时，则更新行状态=“3已驳回”以及驳回原因，否则更新行状态="2已核准"；（备注：提交笔数必须与行数一致）
        //● 判断单据所有行状态更新单据状态：所有行状态=“2.已核准”，更新单据状态=“2.已核准”；否则更新单据状态=“3部分核准”
        //● 更新单头字段：审核人、审核时间、修改人、修改时间；
        //● 提取pQty>0的明细生成出货通知单，并自动执行出货通知单【审核】逻辑

        if(opType.equals(Constant.OPR_TYPE_APPROVE)){
            if (!tpStatus.equals("1")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[待核准]不可核准单据！");
            }
            String detailSql="select * from dcp_transapply_detail a where a.eid='"+eId+"' and a.billno='"+billNo+"' ";
            List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

            String updateStatus="2";
            List<DCP_TransApplyStatusUpdateReq.GoodsList> goodsList = req.getRequest().getGoodsList();
            List<String> detailStatus=new ArrayList();
            BigDecimal totPQtyt=new BigDecimal(0);
            if(CollUtil.isNotEmpty(goodsList)){
                for (Map<String, Object> map : detailList){
                    String item = map.get("ITEM").toString();
                    List<DCP_TransApplyStatusUpdateReq.GoodsList> items = goodsList.stream().filter(x -> x.getItem().equals(item)).collect(Collectors.toList());
                    if(items.size()!=1){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "传入的明细行数与单据行数不一致！");
                    }
                    String pQty = items.get(0).getPQty();
                    String reason = items.get(0).getReason();
                    totPQtyt=totPQtyt.add(new BigDecimal(pQty));

                    UptBean ub3 = new UptBean("DCP_TRANSAPPLY_DETAIL");
                    if(new BigDecimal(pQty).compareTo(BigDecimal.ZERO)>0){
                        ub3.addUpdateValue("STATUS", DataValues.newString("2"));
                        if(!detailStatus.contains("2")){
                            detailStatus.add("2");
                        }
                    }else{
                        ub3.addUpdateValue("STATUS", DataValues.newString("3"));
                        if(!detailStatus.contains("3")){
                            detailStatus.add("3");
                        }
                        updateStatus="3";
                    }

                    ub3.addUpdateValue("PQTY", DataValues.newString(pQty));
                    ub3.addUpdateValue("REASON", DataValues.newString(reason));

                    ub3.addCondition("EID", DataValues.newString(eId));
                    ub3.addCondition("BILLNO", DataValues.newString(billNo));
                    ub3.addCondition("ITEM", DataValues.newString(map.get("ITEM").toString()));
                    this.addProcessData(new DataProcessBean(ub3));

                }
            }else{
                for (Map<String, Object> map : detailList){
                    UptBean ub3 = new UptBean("DCP_TRANSAPPLY_DETAIL");
                    ub3.addUpdateValue("STATUS", DataValues.newString("2"));
                    ub3.addUpdateValue("PQTY", DataValues.newString(map.get("POQTY").toString()));

                    ub3.addCondition("EID", DataValues.newString(eId));
                    ub3.addCondition("BILLNO", DataValues.newString(billNo));
                    ub3.addCondition("ITEM", DataValues.newString(map.get("ITEM").toString()));
                    this.addProcessData(new DataProcessBean(ub3));
                    totPQtyt=totPQtyt.add(new BigDecimal(map.get("POQTY").toString()));
                }
                updateStatus="2";//整单的 已核准
            }

            if(detailStatus.size()>0){
                if(detailStatus.size()==1){
                    if(detailStatus.contains("2")){
                        updateStatus="2";
                    }
                    if(detailStatus.contains("3")){
                        updateStatus="4";//已驳回
                    }
                }else{
                    updateStatus="3";//部分审核
                }
            }

            UptBean ub2 = new UptBean("DCP_TRANSAPPLY");
            ub2.addUpdateValue("TOTPQTY", DataValues.newString(totPQtyt));
            ub2.addUpdateValue("STATUS", DataValues.newString(updateStatus));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("MODIFYTIME", DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CONFIRMTIME", DataValues.newDate(lastmoditime));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO", DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
            this.doExecuteDataToDB();//先执行本单的更新

            //出货仓库warehouse=单头出货仓库，若为空等于出货组织默认出货成本仓（单头单身一致）
            if(Check.Null(transOutWarehouse)){
                //transOutOrgNo
                String orgSql="select * from dcp_org where eid='"+eId+"' and organizationno='"+transOutOrgNo+"'";
                List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
                if(CollUtil.isNotEmpty(orgList)){
                    transOutWarehouse=orgList.get(0).get("OUT_COST_WAREHOUSE").toString();
                }
            }

            //receiptWarehouse=等于单头收货仓库，若为空等于收货组织默认收货成本仓（单头单身一致）
            if(Check.Null(transInWarehouse)){
                transInWarehouse=req.getIn_cost_warehouse();
            }

            //● 提取pQty>0的明细生成出货通知单，并自动执行出货通知单【审核】逻辑
            String detailSql2=" select  * from dcp_transapply_detail a where a.eid='"+eId+"' and a.billno='"+billNo+"' and a.pqty>0 ";
            List<Map<String, Object>> detailList2 = this.doQueryData(detailSql2, null);
            if(CollUtil.isNotEmpty(detailList2)){
                if("5".equals(billType)) {
                    dbtz = this.getOrderNO(req, "DBTZ");
                }
                if("6".equals(billType)) {
                    dbtz = this.getOrderNO(req, "YCTZ");
                }
                int item=0;
                BigDecimal totCQty=BigDecimal.ZERO;
                BigDecimal totPQty = BigDecimal.ZERO;
                BigDecimal totAmt = BigDecimal.ZERO;
                BigDecimal totRetailAmt=BigDecimal.ZERO;

                totCQty=new BigDecimal(detailList2.stream().map(x->{
                    Map map = new HashMap<>();
                    map.put("pluNo",x.get("PLUNO").toString());
                    map.put("featureNo",x.get("FEATURENO").toString());
                    return map;
                }).distinct().collect(Collectors.toList()).size());

                for (Map<String, Object> map : detailList2){

                    String appDetailItem  = map.get("ITEM").toString();
                    String pluNo = map.get("PLUNO").toString();
                    String featureNo = map.get("FEATURENO").toString();
                    String pluBarcode = map.get("PLUBARCODE").toString();
                    String pUnit = map.get("PUNIT").toString();
                    String pQty = map.get("PQTY").toString();
                    String distriPrice = map.get("DISTRIPRICE").toString();
                    BigDecimal amount = new BigDecimal(distriPrice).multiply(new BigDecimal(pQty)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    String baseUnit = map.get("BASEUNIT").toString();
                    String memo = map.get("MEMO").toString();
                    String retailPrice = map.get("PRICE").toString();
                    String unitRatio = map.get("UNITRATIO").toString();
                    BigDecimal retailAmt = new BigDecimal(retailPrice).multiply(new BigDecimal(pQty)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    String poQty = map.get("POQTY").toString();
                    String baseQty = new BigDecimal(pQty).multiply(new BigDecimal(unitRatio)).toString();

                    totPQty=totPQty.add(new BigDecimal(pQty));
                    totAmt=totAmt.add(amount);
                    totRetailAmt=totRetailAmt.add(retailAmt);

                    item++;
                    ColumnDataValue detailColumns=new ColumnDataValue();
                    detailColumns.add("EID",eId,Types.VARCHAR);
                    detailColumns.add("ORGANIZATIONNO",organizationNO,Types.VARCHAR);
                    detailColumns.add("BILLNO",dbtz,Types.VARCHAR);
                    detailColumns.add("ITEM",item+"",Types.VARCHAR);
                    detailColumns.add("SOURCETYPE","5",Types.VARCHAR);
                    detailColumns.add("SOURCEBILLNO",billNo,Types.VARCHAR);
                    detailColumns.add("OITEM",appDetailItem,Types.VARCHAR);
                    detailColumns.add("PLUNO",pluNo,Types.VARCHAR);
                    detailColumns.add("FEATURENO",featureNo,Types.VARCHAR);
                    detailColumns.add("PLUBARCODE",pluBarcode,Types.VARCHAR);
                    detailColumns.add("TEMPLATENO",pTemplateNo,Types.VARCHAR);
                    detailColumns.add("PUNIT",pUnit,Types.VARCHAR);
                    detailColumns.add("PQTY",pQty,Types.VARCHAR);
                    detailColumns.add("WAREHOUSE",transOutWarehouse,Types.VARCHAR);
                    detailColumns.add("RECEIPTWAREHOUSE",transInWarehouse,Types.VARCHAR);
                    detailColumns.add("PRICE",distriPrice,Types.VARCHAR);
                    detailColumns.add("AMOUNT",amount,Types.VARCHAR);
                    detailColumns.add("BASEUNIT",baseUnit,Types.VARCHAR);
                    detailColumns.add("BASEQTY",baseQty,Types.VARCHAR);
                    detailColumns.add("UNITRATIO",unitRatio,Types.VARCHAR);
                    detailColumns.add("STOCKOUTQTY","0",Types.VARCHAR);
                    detailColumns.add("STATUS","1",Types.VARCHAR);
                    detailColumns.add("MEMO",memo,Types.VARCHAR);
                    detailColumns.add("OBJECTTYPE","3",Types.VARCHAR);
                    detailColumns.add("OBJECTID",transInOrgNo,Types.VARCHAR);
                    detailColumns.add("TEMPLATETYPE","4",Types.VARCHAR);
                    detailColumns.add("RETAILPRICE",retailPrice,Types.VARCHAR);
                    detailColumns.add("RETAILAMT",retailAmt,Types.VARCHAR);
                    detailColumns.add("POQTY",poQty,Types.VARCHAR);
                    detailColumns.add("ISGIFT","0",Types.VARCHAR);
                    detailColumns.add("NOQTY",pQty,Types.VARCHAR);

                    String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                    DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE_DETAIL",detailColumnNames);
                    ib1.addValues(detailDataValues);
                    this.addProcessData(new DataProcessBean(ib1));

                }
                String sourceType="";
                if("0".equals(applyType)){
                    sourceType="5";
                }else if("1".equals(applyType)){
                    sourceType="6";
                }

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID",eId,Types.VARCHAR);
                mainColumns.add("ORGANIZATIONNO",organizationNO,Types.VARCHAR);
                mainColumns.add("BDATE",bDate,Types.VARCHAR);
                mainColumns.add("BILLTYPE",billType,Types.VARCHAR);
                mainColumns.add("BILLNO",dbtz,Types.VARCHAR);
                mainColumns.add("SOURCETYPE",sourceType,Types.VARCHAR);
                mainColumns.add("SOURCEBILLNO",billNo,Types.VARCHAR);
                mainColumns.add("RDATE",rDate,Types.VARCHAR);
                mainColumns.add("OBJECTTYPE","3",Types.VARCHAR);
                mainColumns.add("OBJECTID",transInOrgNo,Types.VARCHAR);
                mainColumns.add("DELIVERORGNO",transOutOrgNo,Types.VARCHAR);
                mainColumns.add("WAREHOUSE",transOutWarehouse,Types.VARCHAR);
                mainColumns.add("TOTCQTY",totCQty.toString(),Types.VARCHAR);
                mainColumns.add("TOTPQTY",totPQty.toString(),Types.VARCHAR);

                mainColumns.add("TOTSTOCKOUTQTY","0",Types.VARCHAR);
                mainColumns.add("TOTAMT",totAmt.toString(),Types.VARCHAR);
                mainColumns.add("EMPLOYEEID",employeeNo,Types.VARCHAR);
                mainColumns.add("DEPARTID",departmentNo,Types.VARCHAR);
                mainColumns.add("STATUS","0",Types.VARCHAR);

                mainColumns.add("MEMO",mainMemo,Types.VARCHAR);
                mainColumns.add("OWNOPID",req.getOpNO(),Types.VARCHAR);
                mainColumns.add("OWNDEPTID",departmentNo,Types.VARCHAR);
                mainColumns.add("CREATEOPID",req.getOpNO(),Types.VARCHAR);
                mainColumns.add("CREATEDEPTID",departmentNo,Types.VARCHAR);
                mainColumns.add("CREATETIME",lastmoditime,Types.DATE);
                mainColumns.add("RECEIPTWAREHOUSE",transInWarehouse,Types.VARCHAR);
                if("Y".equals(isTraninConfirm)){
                    mainColumns.add("INVWAREHOUSE",req.getInv_cost_warehouse(),Types.VARCHAR);
                }
                mainColumns.add("ISTRANINCONFIRM",isTraninConfirm,Types.VARCHAR);
                mainColumns.add("DELIVERYDATE",bDate,Types.VARCHAR);
                mainColumns.add("TOTRETAILAMT",totRetailAmt,Types.VARCHAR);


                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_STOCKOUTNOTICE",mainColumnNames);
                ib1.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib1));
                this.doExecuteDataToDB();//生成通知单
            }
        }

        if(opType.equals(Constant.OPR_TYPE_REJECT)){
            if (!tpStatus.equals("1")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[待核准]不可驳回单据！");
            }

            UptBean ub1 = new UptBean("DCP_TRANSAPPLY_DETAIL");
            ub1.addUpdateValue("STATUS", DataValues.newString("3"));

            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("BILLNO", DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_TRANSAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("4"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("MODIFYTIME", DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("REASON", DataValues.newString(req.getRequest().getReason()));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO", DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));

        }

        if(opType.equals(Constant.OPR_TYPE_CLOSE)){
            if (!tpStatus.equals("2") && !tpStatus.equals("3")) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[待核准]或[部分核准]不可结束单据！");
            }

            UptBean ub1 = new UptBean("DCP_TRANSAPPLY_DETAIL");
            ub1.addUpdateValue("STATUS", DataValues.newString("4"));

            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("BILLNO", DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_TRANSAPPLY");
            ub2.addUpdateValue("STATUS", DataValues.newString("5"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("MODIFYTIME", DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CLOSEBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CLOSETIME", DataValues.newDate(lastmoditime));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("BILLNO", DataValues.newString(billNo));
            this.addProcessData(new DataProcessBean(ub2));
        }

        this.doExecuteDataToDB();

        if(!Check.Null(dbtz)){
            //自动审核
            ParseJson pj = new ParseJson();
            DCP_StockOutNoticeStatusUpdateReq noticeReq=new DCP_StockOutNoticeStatusUpdateReq();
            noticeReq.setServiceId("DCP_StockOutNoticeStatusUpdate");
            noticeReq.setToken(req.getToken());
            DCP_StockOutNoticeStatusUpdateReq.Request request = noticeReq.new Request();
            request.setOpType("confirm");
            request.setBillNo(dbtz);
            request.setBillType(billType);
            noticeReq.setRequest(request);

            String jsontemp= pj.beanToJson(noticeReq);

            //直接调用CRegisterDCP服务
            DispatchService ds = DispatchService.getInstance();
            String resXml = ds.callService(jsontemp, StaticInfo.dao);
            DCP_StockOutNoticeRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_StockOutNoticeRes>(){});
            if(resserver.isSuccess()==false)
            {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "通知单审核失败！");
            }
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TransApplyStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TransApplyStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TransApplyStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_TransApplyStatusUpdateReq req) throws Exception {
        StringBuilder sb=new StringBuilder();
        sb.append("select * from dcp_transapply a where a.eid='"+req.geteId()+"' " +
                " and a.BILLNO='"+req.getRequest().getBillNo()+"' ");

        return sb.toString();
    }




}

