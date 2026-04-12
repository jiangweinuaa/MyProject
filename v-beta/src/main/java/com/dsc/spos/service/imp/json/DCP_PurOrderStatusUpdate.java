package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.*;
import com.dsc.spos.json.cust.res.DCP_CRegisterCheckRes;
import com.dsc.spos.json.cust.res.DCP_PurOrderStatusUpdateRes;
import com.dsc.spos.json.cust.res.DCP_PurOrderUpdateRes;
import com.dsc.spos.json.cust.res.DCP_ReceivingStatusUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
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

public class DCP_PurOrderStatusUpdate extends SPosAdvanceService<DCP_PurOrderStatusUpdateReq, DCP_PurOrderStatusUpdateRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurOrderStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_PurOrderStatusUpdateReq.levelElm request = req.getRequest();

        if (Check.Null(request.getPurOrderNo())){
            errMsg.append("采购单号不可为空值, ");
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
    protected TypeToken<DCP_PurOrderStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_PurOrderStatusUpdateReq>(){};
    }

    @Override
    protected DCP_PurOrderStatusUpdateRes getResponseType() {
        return new DCP_PurOrderStatusUpdateRes();
    }

    @Override
    public void processDUID(DCP_PurOrderStatusUpdateReq req,DCP_PurOrderStatusUpdateRes res) throws Exception {

        //cancel：单据作废,
        // confirm：单据审核,
        // confRevoke：取消审核,
        // close：单据结案,
        // closeRevoke：取消结案
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");

        List<String> confirmReceivingNos=new ArrayList<>();
        List<String> unConfirmReceivingNos=new ArrayList<>();


        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String oprType = req.getRequest().getOprType();
        String purOrderNo = req.getRequest().getPurOrderNo();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //【作废】：作废单据不可使用！
        //● 作废前检查：单据状态非[0-新建]不可执行！
        //● 执行处理：更新单据状态码=【3-作废】，作废人=当前用户，作废时间=系统时间
        if("cancel".equals(oprType)){
            String orderSql=String.format("select * from DCP_PURORDER where eid='%s' and purorderno='%s' and status='0'",eId,purOrderNo);
            List<Map<String, Object>> orderData = dao.executeQuerySQL(orderSql, null);
            if(orderData==null||orderData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[新建]不可执行！");
            }
            UptBean ub1 = new UptBean("DCP_PURORDER");
            ub1.addUpdateValue("STATUS", DataValues.newString("3"));
            ub1.addUpdateValue("CANCELBY", DataValues.newString(req.getOpNO()));
            ub1.addUpdateValue("CANCELTIME",DataValues.newDate(lastmoditime));

            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
            this.addProcessData(new DataProcessBean(ub1));

            String psSql=" select * from DCP_PURORDER_SOURCE a where a.eid='"+eId+"' " +
                    " and a.organizationno='"+organizationNO+"' and a.purorderno='"+purOrderNo+"' ";
            List<Map<String, Object>> getPsData=this.doQueryData(psSql, null);
            for (Map<String, Object> row : getPsData){
                //更新底稿 DISTRISTATUS
                UptBean ub2 = new UptBean("DCP_DEMAND");
                ub2.addUpdateValue("DISTRISTATUS", new DataValue("00", Types.VARCHAR));

                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub2.addCondition("ORDERNO", new DataValue(row.get("SOURCEBILLNO").toString(), Types.VARCHAR));
                ub2.addCondition("ITEM", new DataValue(row.get("OITEM").toString(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub2));
            }


        }

        //【审核】：审核订单，推送采购收货通知>>
        //● 审核前检查项：以下满足其中一种情况均视为检核失败
        //  ○ 单据状态非[0-新建]
        //  ○ 单据必要栏位为空（同新增检查）
        //  ○ 单身明细列表为空（同新增检查）
        //  ○ 单身明细到库日不可小于系统日期！（同新增检查）
        //  ○ 单据明细以及汇总金额检查：含税金额-未税金额<>税额
        //  ○ 供应商状态：
        //    ■ 供应商资料状态码非[100-启用]，来源表：DCP_BIZPARTNER
        //    ■ 供应商交易截止日期<系统日期，来源表：DCP_BIZPARTNER
        //    ■ 供应商生命周期的【可采】属性为N，来源表：DCP_LIFEVALUE（生命周期为空即不管控）
        //  ○ 收货组织状态：
        //    ■ 收货组织状态码非[100-启用]，来源表：DCP_ORG
        //    ■ 收货组织在所属采购模板状态非[100-启用]，来源表：DCP_PURTEMPLATE_ORG 采购模板
        //  ○ 商品状态：
        //    ■ 商品资料状态码非[100-启用]，来源表：DCP_GOODS
        //    ■ 供应商+采购类型+商品编码+收货组织未查询到有效采购模板！
        //    ■ 商品在供应商所属采购模板状态非[100-启用]，来源表：DCP_PURTEMPLATE_GOODS 采购模板
        //● 审核处理：主要处理以下：
        //  ○ 更新单据状态码=【1-已审核】，审核人=当前用户ID，审核时间=系统时间；
        //  ○ 产生采购收货通知：[参数设置收货通知方式：1.订单确认后自动预约（默认），2.订单收货前人工预约]
        //    ■ 1.订单确认后自动预约，即根据订单交期明细中的【预计到货日】自动拆分产生【待收货】状态的“采购收货通知”；提交成功后回写对应【通知收货量】至采购订单交期明细
        //    ■ 2.订单收货前人工预约，即人工发起采购收货通知，维护订单到货日、时间段以及送货明细
        //相关表：DCP_RECEIVING/DCP_RECEIVING_DETAIL
        //  ○ 更新来源关联单据的【已转采购量】（需求转采流程补充）
        if("confirm".equals(oprType)){



            String orderSql=String.format("select * from DCP_PURORDER where eid='%s' and purorderno='%s' and organizationno='%s' and status='0'",eId,purOrderNo,organizationNO);
            List<Map<String, Object>> orderData = dao.executeQuerySQL(orderSql, null);
            if(orderData==null||orderData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[新建]不可执行！");
            }

            Map<String, Object> singleOrder = orderData.get(0);
            String bdate = singleOrder.get("BDATE").toString();
            String version = singleOrder.get("VERSION").toString();
            String employeeid = singleOrder.get("EMPLOYEEID").toString();
            String departid = singleOrder.get("DEPARTID").toString();
            String supplier = singleOrder.get("SUPPLIER").toString();
            String purtype = singleOrder.get("PURTYPE").toString();
            String districenter = singleOrder.get("DISTRICENTER").toString();
            String paytype = singleOrder.get("PAYTYPE").toString();
            String payorgno = singleOrder.get("PAYORGNO").toString();
            String billdateno = singleOrder.get("BILLDATENO").toString();
            String paydateno = singleOrder.get("PAYDATENO").toString();
            String currency = singleOrder.get("CURRENCY").toString();
            String expiredate = singleOrder.get("EXPIREDATE").toString();
            String corp = singleOrder.get("CORP").toString();
            String receiptCorp = singleOrder.get("RECEIPTCORP").toString();
            String payee = singleOrder.get("PAYEE").toString();
            String invoicecode = singleOrder.get("INVOICECODE").toString();
            String address = singleOrder.get("ADDRESS").toString();

            boolean isColumnCheck=false;
            StringBuffer errorBuff=new StringBuffer("");
            if(Check.Null(bdate)){
                isColumnCheck=true;
                errorBuff.append("采购日期不能为空!");
            }
            if(Check.Null(version)){
                isColumnCheck=true;
                errorBuff.append("版次不能为空!");
            }
            if(Check.Null(employeeid)){
                isColumnCheck=true;
                errorBuff.append("采购人员不能为空!");
            }
            if(Check.Null(departid)){
                isColumnCheck=true;
                errorBuff.append("采购部门不能为空!");
            }
            if(Check.Null(supplier)){
                isColumnCheck=true;
                errorBuff.append("供应商编号不能为空!");
            }
            if(Check.Null(purtype)){
                isColumnCheck=true;
                errorBuff.append("采购类型不能为空!");
            }
            if(Check.Null(paytype)){
                isColumnCheck=true;
                errorBuff.append("结算方式不能为空!");
            }
            if(Check.Null(payorgno)){
                isColumnCheck=true;
                errorBuff.append("结算法人不能为空!");
            }
            if(Check.Null(billdateno)){
                isColumnCheck=true;
                errorBuff.append("结算条件不能为空!");
            }
            if(Check.Null(paydateno)){
                isColumnCheck=true;
                errorBuff.append("付款条件不能为空!");
            }
            if(Check.Null(currency)){
                isColumnCheck=true;
                errorBuff.append("币种不能为空!");
            }
            if(Check.Null(expiredate)){
                isColumnCheck=true;
                errorBuff.append("最终有效日不能为空!");
            }
            if(isColumnCheck){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errorBuff.toString());
            }

            String orderDetailSql=String.format("select b.*,nvl(c.status,'0') plustatus,d.ISGIFT from " +
                    " DCP_PURORDER a" +
                    " left join DCP_PURORDER_DETAIL d on d.eid=a.eid and d.purorderno=a.purorderno " +
                    " left join DCP_PURORDER_DELIVERY b on a.eid=b.eid and a.purorderno=b.purorderno and  d.item=b.ITEM2 " +//and a.item=b.item
                    " left join DCP_GOODS c on a.eid=c.eid and b.pluno=c.pluno "+
                    " where a.eid='%s' and a.purorderno='%s' and a.organizationno='%s' ",eId,purOrderNo,organizationNO);
            List<Map<String, Object>> orderDetailData = dao.executeQuerySQL(orderDetailSql, null);
            if(orderDetailData==null||orderDetailData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单身明细列表不能为空！");
            }

            String receiptorgno = singleOrder.get("RECEIPTORGNO").toString();
            //收货通知单.收货组织的字段赋值调整：DCP_RECEIVING.RECEIPTORGNO收货组织
            //当采购类型=”0.自订货“或”1.统采直供“时，收货组织=DCP_PURORDER.RECEIPTORGNO
            //当采购类型=”2.统采越库“时，收货组织=DCP_PURORDER.DISTRICENTER
            if("2".equals(purtype)){
                receiptorgno=districenter;
            }

            String purReceiveBookingType= PosPub.getPARA_SMS(dao, req.geteId(), receiptorgno, "PurReceiveBookingType");
            String purReceiveType= PosPub.getPARA_SMS(dao, req.geteId(), receiptorgno, "PurReceiveType");

            String receivingDocType="4";
            if("1".equals(purReceiveType)){
                receivingDocType="4";
            }
            if("2".equals(purReceiveType)){
                receivingDocType="3";
            }

            String purTempSql="select b.pluno from DCP_PURCHASETEMPLATE a" +
                    " left join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO "+
                    " left join DCP_PURCHASETEMPLATE_ORG c on c.eid=a.eid and c.PURTEMPLATENO=a.PURTEMPLATENO "+
                    " where a.SUPPLIERNO='"+supplier+"' and a.PURTYPE='"+purtype+"' and a.status='100' and c.organizationno='"+receiptorgno+"' ";
            List<Map<String, Object>> getPurTempData=this.doQueryData(purTempSql, null);
            if(getPurTempData==null||getPurTempData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "采购模板不存在！");
            }
            List<Object> explunos = getPurTempData.stream().map(row -> row.get("PLUNO")).collect(Collectors.toList());

            for(Map<String, Object> singleOrderDetail : orderDetailData){
                String arrivaldate = singleOrderDetail.get("ARRIVALDATE").toString();
                String isGift = singleOrderDetail.get("ISGIFT").toString();
                if("Y".equals(isGift)){
                    continue;
                }
                //单身明细到库日不可小于系统日期
                if(DateFormatUtils.lessNowDate(arrivaldate)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单身明细到库日不可小于系统日期！");
                }

                String plustatus = singleOrderDetail.get("PLUSTATUS").toString();
                if(!plustatus.equals("100")){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品资料状态码非[启用]！");
                }

                if(!explunos.contains(singleOrderDetail.get("PLUNO").toString())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品在供应商所属采购模板不存在或状态非[启用]！");
                }
            }

            //含税金额-未税金额<>税额
            String sumSql="select a.item ,sum(nvl(b.puramt,0)) as puramt,sum(nvl(b.pretaxamt,0)) as pretaxamt,sum(nvl(b.taxamt,0)) as taxamt from DCP_PURORDER_DETAIL a " +
                    " left join DCP_PURORDER_DELIVERY b on a.eid=b.eid and a.purorderno=b.purorderno and a.item=b.item " +
                    " where a.purorderno='"+purOrderNo+"' and a.eid='"+eId+"' and a.organizationno='"+organizationNO+"'" +
                    " group by a.item";
            List<Map<String, Object>> sumData = dao.executeQuerySQL(sumSql, null);
            for(Map<String, Object> singleSum : sumData){
                BigDecimal puramt = new BigDecimal(singleSum.get("PURAMT").toString());
                BigDecimal pretaxamt = new BigDecimal(singleSum.get("PRETAXAMT").toString());
                BigDecimal taxamt = new BigDecimal(singleSum.get("TAXAMT").toString());
                if(puramt.subtract(pretaxamt).compareTo(taxamt)!=0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "含税金额-未税金额<>税额！");
                }
            }

            String bizSql="select a.* from DCP_BIZPARTNER a" +
                     " where a.status='100' and a.BIZPARTNERNO='"+supplier+"' and a.eid='"+eId+"'";
            List<Map<String, Object>> bizData = dao.executeQuerySQL(bizSql, null);
            if(bizData==null||bizData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "供应商不存在！");
            }
            String enddate=bizData.get(0).get("ENDDATE").toString();
            if(DateFormatUtils.lessNowDate(enddate)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "供应商已过期！");
            }
            //if(!bizData.get(0).get("CANPURCHASE").toString().equals("Y")){
            //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "供应商禁止采购！");
            //}

            String orgSql="select * from dcp_org a where a.eid='"+eId+"' and a.status='100' and a.ORGANIZATIONNO='"+receiptorgno+"' ";
            List<Map<String, Object>> orgData = dao.executeQuerySQL(orgSql, null);
            if(orgData==null||orgData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "收货组织状态非[启用]！");
            }
            String receiptCompany=orgData.get(0).get("BELFIRM").toString();

            String purTempOrgSql="select a.*,b.status as purtempstatus from DCP_PURCHASETEMPLATE_ORG a" +
                    " left join DCP_PURCHASETEMPLATE b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO" +
                    " where a.eid='"+eId+"' and a.ORGANIZATIONNO='"+receiptorgno+"' AND b.status='100'";
            List<Map<String, Object>> purTempOrgData = dao.executeQuerySQL(purTempOrgSql, null);
            if(purTempOrgData==null||purTempOrgData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "收货组织在所属采购模板状态非[启用]！");
            }

            UptBean ub1 = new UptBean("DCP_PURORDER");
            ub1.addUpdateValue("STATUS", DataValues.newString("1"));
            ub1.addUpdateValue("CONFIRMBY",DataValues.newString(req.getOpNO()));
            ub1.addUpdateValue("CONFIRMTIME",DataValues.newDate(lastmoditime));

            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
            this.addProcessData(new DataProcessBean(ub1));

            if(purReceiveBookingType.equals("1")){//订单确认后自动预约
                Calendar cal = Calendar.getInstance();//获得当前时间
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String bDate = df.format(cal.getTime());
                String createDate = df.format(cal.getTime());
                df = new SimpleDateFormat("HHmmss");
                String createTime = df.format(cal.getTime());

                //根据预计到货日自动拆分
                String deliverySql="select b.*,a.baseunit,nvl(c.procrate,0) as PROC_RATE,a.PLUBARCODE,a.category " +
                        " from DCP_PURORDER_DETAIL a" +
                        " inner join DCP_PURORDER_DELIVERY b on a.eid=b.eid and a.purorderno=b.purorderno and a.item=b.item2" +
                        " left join DCP_GOODS c on b.pluno=c.PLUNO "+
                        " where a.purorderno='"+purOrderNo+"' and a.eid='"+eId+"' and a.organizationno='"+organizationNO+"'";
                List<Map<String, Object>> deliveryData = dao.executeQuerySQL(deliverySql, null);
                List<String> arrivalDates = deliveryData.stream().map(x -> x.get("ARRIVALDATE").toString()).distinct().collect(Collectors.toList());
                //List<String> receivingNos=new ArrayList<>();


                List<Map<String, Object>> pluCollection = deliveryData.stream().map(x -> {
                    Map<String, Object> xx = new HashMap<>();
                    xx.put("PLUNO", x.get("PLUNO").toString());
                    xx.put("PUNIT", x.get("PURUNIT").toString());
                    xx.put("BASEUNIT", x.get("BASEUNIT").toString());
                    xx.put("UNITRATIO",  x.get("UNITRATIO").toString());
                    return xx;
                }).distinct().collect(Collectors.toList());

                MyCommon mc = new MyCommon();
                List<Map<String, Object>> getPrice = mc.getPrice(dao, eId, receiptCompany, receiptorgno, pluCollection,2);


                for (String arrivalDate : arrivalDates){

                    String arrivalDateStr = arrivalDate;//new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd").parse(arrivalDate));

                    String receivingNO = getReceivingNO(req);
                    //明细
                    List<Map<String, Object>> singleDeliverys = deliveryData.stream().filter(x -> x.get("ARRIVALDATE").toString().equals(arrivalDate)).collect(Collectors.toList());
                    int item=0;
                    BigDecimal totPQty = new BigDecimal(0);
                    BigDecimal totDistriAmt = new BigDecimal(0);
                    BigDecimal totAmt=new BigDecimal(0);
                    BigDecimal totPurAmt=new BigDecimal(0);
                    for (Map<String, Object> singleDelivery : singleDeliverys){
                        BigDecimal purAmt = new BigDecimal(singleDelivery.get("PURAMT").toString());
                        totPurAmt=totPurAmt.add(purAmt);
                        item++;
                        ColumnDataValue receivingDetailColumns=new ColumnDataValue();
                        receivingDetailColumns.add("SHOPID",singleOrder.get("ORGANIZATIONNO").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("RECEIVINGNO",receivingNO,Types.VARCHAR);
                        receivingDetailColumns.add("ITEM",item,Types.VARCHAR);
                        receivingDetailColumns.add("EID",eId,Types.VARCHAR);
                        receivingDetailColumns.add("ORGANIZATIONNO",singleOrder.get("ORGANIZATIONNO").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("OTYPE","2",Types.VARCHAR);//2-采购订单
                        receivingDetailColumns.add("PLUNO",singleDelivery.get("PLUNO").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("PUNIT",singleDelivery.get("PURUNIT").toString(),Types.VARCHAR);

                        Map<String, Object> mapBase = PosPub.getBaseQty(dao, req.geteId(), singleDelivery.get("PLUNO").toString(), singleDelivery.get("PURUNIT").toString(), singleDelivery.get("PURQTY").toString());
                        if (mapBase == null)
                        {
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+singleDelivery.get("PLUNO").toString()+",OUNIT="+singleDelivery.get("PURUNIT").toString()+"无记录！");
                        }
                        String baseUnit = mapBase.get("baseUnit").toString();
                        String baseQty = mapBase.get("baseQty").toString();
                        String unitRatio = mapBase.get("unitRatio").toString();

                        receivingDetailColumns.add("BASEQTY",baseQty,Types.DECIMAL);
                        receivingDetailColumns.add("UNIT_RATIO",unitRatio,Types.DECIMAL);
                        receivingDetailColumns.add("BASEUNIT",baseUnit,Types.VARCHAR);
                        receivingDetailColumns.add("OITEM",singleDelivery.get("ITEM").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("OITEM2",singleDelivery.get("ITEM2").toString(),Types.VARCHAR);
                        //receivingDetailColumns.add("POQTY",,Types.VARCHAR);
                        receivingDetailColumns.add("WAREHOUSE",orgData.get(0).get("IN_COST_WAREHOUSE").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("PQTY",singleDelivery.get("PURQTY").toString(),Types.DECIMAL);
                        totPQty=totPQty.add(new BigDecimal(singleDelivery.get("PURQTY").toString()));
                        //receivingDetailColumns.add("TRAN_TIME",,Types.VARCHAR);
                        receivingDetailColumns.add("OFNO",purOrderNo,Types.VARCHAR);
                        receivingDetailColumns.add("PROC_RATE",singleDelivery.get("PROC_RATE").toString(),Types.DECIMAL);
                        List<Map<String, Object>> priceFilter = getPrice.stream().filter(a -> a.get("PLUNO").toString().equals(singleDelivery.get("PLUNO").toString()) && a.get("PUNIT").toString().equals(singleDelivery.get("PURUNIT").toString())).collect(Collectors.toList());
                        BigDecimal price = new BigDecimal(0);
                        if(priceFilter.size()>0){
                            price=new BigDecimal(priceFilter.get(0).get("PRICE").toString());
                        }
                        BigDecimal amt=price.multiply(new BigDecimal(singleDelivery.get("PURQTY").toString()));
                        totAmt=totAmt.add(amt);
                        receivingDetailColumns.add("AMT",amt.toString(),Types.DECIMAL);
                        receivingDetailColumns.add("PRICE",price.toString(),Types.DECIMAL);
                        receivingDetailColumns.add("PLU_BARCODE",singleDelivery.get("PLUBARCODE").toString(),Types.VARCHAR);
                        //receivingDetailColumns.add("PLU_MEMO",,Types.VARCHAR);
                        receivingDetailColumns.add("STOCKIN_QTY",0,Types.DECIMAL);
                        receivingDetailColumns.add("BATCH_NO","",Types.VARCHAR);
                        //receivingDetailColumns.add("PROD_DATE",,Types.VARCHAR);
                        receivingDetailColumns.add("DISTRIPRICE",singleDelivery.get("RECEIVEPRICE").toString(),Types.DECIMAL);
                        receivingDetailColumns.add("DISTRIAMT",singleDelivery.get("RECEIVEAMT").toString(),Types.DECIMAL);
                        receivingDetailColumns.add("PURPRICE",singleDelivery.get("PURPRICE").toString(),Types.DECIMAL);
                        receivingDetailColumns.add("PURAMT",singleDelivery.get("PURAMT").toString(),Types.DECIMAL);
                        receivingDetailColumns.add("SUPPRICE",singleDelivery.get("SUPPRICE").toString(),Types.DECIMAL);
                        receivingDetailColumns.add("SUPAMT",singleDelivery.get("SUPAMT").toString(),Types.DECIMAL);

                        totDistriAmt=totDistriAmt.add(new BigDecimal(Check.Null(singleDelivery.get("RECEIVEAMT").toString())?"0":singleDelivery.get("RECEIVEAMT").toString()));
                        receivingDetailColumns.add("BDATE",bdate,Types.VARCHAR);
                        receivingDetailColumns.add("FEATURENO",singleDelivery.get("FEATURENO").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("CATEGORY",singleDelivery.get("CATEGORY").toString(),Types.VARCHAR);
                        //receivingDetailColumns.add("PACKINGNO",,Types.VARCHAR);
                        receivingDetailColumns.add("PARTITION_DATE",bdate,Types.VARCHAR);
                        receivingDetailColumns.add("STATUS","0",Types.VARCHAR);
                        //TAXCODE,TAXRATE,INCLTAX
                        receivingDetailColumns.add("TAXCODE",singleDelivery.get("TAXCODE").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("TAXRATE",singleDelivery.get("TAXRATE").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("INCLTAX",singleDelivery.get("INCLTAX").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("TAXCALTYPE",singleDelivery.get("TAXCALTYPE").toString(),Types.VARCHAR);
                        receivingDetailColumns.add("ISGIFT",singleDelivery.get("ISGIFT").toString(),Types.VARCHAR);


                        String[] receivingDetailColumnNames = receivingDetailColumns.getColumns().toArray(new String[0]);
                        DataValue[] receivingDetailDataValues = receivingDetailColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib1=new InsBean("DCP_RECEIVING_DETAIL",receivingDetailColumnNames);
                        ib1.addValues(receivingDetailDataValues);
                        this.addProcessData(new DataProcessBean(ib1));
                    }

                    //商品加特征码去重
                    List<String> pfs = singleDeliverys.stream().map(x -> x.get("PLUNO").toString() + x.get("FEATURENO").toString()).distinct().collect(Collectors.toList());

                    ColumnDataValue receivingColumns=new ColumnDataValue();
                    receivingColumns.add("SHOPID",singleOrder.get("ORGANIZATIONNO").toString(),Types.VARCHAR);
                    receivingColumns.add("RECEIVINGNO",receivingNO,Types.VARCHAR);
                    receivingColumns.add("EID",eId,Types.VARCHAR);
                    receivingColumns.add("ORGANIZATIONNO",singleOrder.get("ORGANIZATIONNO").toString(),Types.VARCHAR);
                    receivingColumns.add("LOAD_DOCNO",purOrderNo,Types.VARCHAR);
                    receivingColumns.add("RECEIPTORGNO",receiptorgno,Types.VARCHAR);
                    receivingColumns.add("CORP",corp,Types.VARCHAR);
                    receivingColumns.add("RECEIPTCORP",receiptCorp,Types.VARCHAR);
                    receivingColumns.add("TOTPURAMT",totPurAmt.toString(),Types.VARCHAR);
                    //if("2".equals(purtype)){
                    //    receivingColumns.add("RECEIPTORGNO",districenter,Types.VARCHAR);
                    //}
                    receivingColumns.add("OTYPE","2",Types.VARCHAR);
                    receivingColumns.add("OFNO",purOrderNo,Types.VARCHAR);
                    receivingColumns.add("EMPLOYEEID",employeeNo,Types.VARCHAR);
                    receivingColumns.add("DEPARTID",departmentNo,Types.VARCHAR);
                    receivingColumns.add("OWNOPID",req.getOpNO(),Types.VARCHAR);
                    receivingColumns.add("OWNDEPTID",departmentNo,Types.VARCHAR);
                    receivingColumns.add("TOT_CQTY",pfs.size(),Types.DECIMAL);
                    receivingColumns.add("SUPPLIER",supplier,Types.VARCHAR);
                    receivingColumns.add("LOAD_DOCTYPE","3",Types.VARCHAR);
                    receivingColumns.add("RECEIPTDATE",arrivalDateStr,Types.VARCHAR);
                    receivingColumns.add("CREATEBY",req.getOpNO(),Types.VARCHAR);
                    receivingColumns.add("CREATE_DATE",createDate,Types.VARCHAR);
                    receivingColumns.add("CREATE_TIME",createTime,Types.VARCHAR);

                    receivingColumns.add("DOC_TYPE",receivingDocType,Types.VARCHAR);
                    receivingColumns.add("TOT_PQTY",totPQty,Types.DECIMAL);
                    receivingColumns.add("WAREHOUSE",orgData.get(0).get("IN_COST_WAREHOUSE").toString(),Types.VARCHAR);
                    receivingColumns.add("BDATE",bDate,Types.VARCHAR);
                    receivingColumns.add("TOT_AMT",totAmt,Types.DECIMAL);
                    receivingColumns.add("TOT_DISTRIAMT",totDistriAmt,Types.DECIMAL);
                    receivingColumns.add("STATUS","0",Types.VARCHAR);//6-待收货
                    receivingColumns.add("PARTITION_DATE",bDate,Types.VARCHAR);

                    receivingColumns.add("PAYTYPE",paytype,Types.VARCHAR);
                    receivingColumns.add("PAYORGNO",payorgno,Types.VARCHAR);
                    receivingColumns.add("BILLDATENO",billdateno,Types.VARCHAR);
                    receivingColumns.add("PAYDATENO",paydateno,Types.VARCHAR);
                    receivingColumns.add("INVOICECODE",invoicecode,Types.VARCHAR);
                    receivingColumns.add("CURRENCY",currency,Types.VARCHAR);
                    receivingColumns.add("PAYEE",payee,Types.VARCHAR);
                    receivingColumns.add("RECEIPTADDRESS",address,Types.VARCHAR);


                    String[] receivingColumnNames = receivingColumns.getColumns().toArray(new String[0]);
                    DataValue[] receivingDataValues = receivingColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_RECEIVING",receivingColumnNames);
                    ib1.addValues(receivingDataValues);
                    this.addProcessData(new DataProcessBean(ib1));
                    if(!confirmReceivingNos.contains(receivingNO)){
                        confirmReceivingNos.add(receivingNO);
                    }
                }


            }
            //  更新来源关联单据的【已转采购量】（需求转采流程补充）

            String mDate =new SimpleDateFormat("yyyyMMdd").format(new Date());
            String mTime = new SimpleDateFormat("HHmmss").format(new Date());


            // source purqty  采购数量转换为需求数量  PURUNIT  -> ounit
            String sourceSql="select b.baseunit,a.purunit,a.ounit,c.unitratio as punitratio,d.unitratio as ounitratio,e.UDLENGTH as pudlength,f.udlength as oudlength," +
                    " a.CONVERTPURQTY,a.SOURCEBILLNO,a.OITEM,g.review_qty  " +
                    " from DCP_PURORDER_SOURCE a " +
                    " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                    " left join DCP_GOODS_UNIT c on c.eid=a.eid and c.unit=b.baseunit and a.purunit=c.ounit  and c.pluno=a.pluno " +
                    " left join dcp_goods_unit d on d.eid=a.eid and d.unit=b.baseunit and a.ounit=d.ounit    and d.pluno=a.pluno " +
                    " left join dcp_unit e on e.eid=a.eid and e.unit=a.purunit " +
                    " left join dcp_unit f on f.eid=a.eid and f.unit=a.ounit " +
                    " left join dcp_porder_detail g on g.eid=a.eid and g.porderno=a.sourcebillno and g.item=a.oitem " +
                    " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                    " and a.PURORDERNO='"+purOrderNo+"' ";
            List<Map<String, Object>> sourceData=this.doQueryData(sourceSql,null);
            if(sourceData.size()>0){
                List<Map<String, Object>> demandList = sourceData.stream().map(x -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("SOURCEBILLNO", x.get("SOURCEBILLNO").toString());
                    map.put("OITEM", x.get("OITEM").toString());
                    return map;
                }).distinct().collect(Collectors.toList());

                for (Map<String, Object> demand : demandList){
                    String sourcebillno = demand.get("SOURCEBILLNO").toString();
                    String oitem = demand.get("OITEM").toString();
                    List<Map<String, Object>> sourceFilter = sourceData.stream().filter(x -> x.get("SOURCEBILLNO").toString().equals(sourcebillno) && x.get("OITEM").toString().equals(oitem)).collect(Collectors.toList());
                    BigDecimal sumQty=BigDecimal.ZERO;
                    for (Map<String, Object> source : sourceFilter){
                        String purunit = source.get("PURUNIT").toString();
                        String ounit = source.get("OUNIT").toString();
                        BigDecimal convertpurqty =new BigDecimal( source.get("CONVERTPURQTY").toString());
                        BigDecimal ounitratio = new BigDecimal(source.get("OUNITRATIO").toString());
                        BigDecimal punitratio = new BigDecimal(source.get("PUNITRATIO").toString());
                        String oudlength = source.get("OUDLENGTH").toString();
                        String pudlength = source.get("PUDLENGTH").toString();

                        BigDecimal oqty = convertpurqty.multiply(punitratio).divide(ounitratio, Integer.parseInt(oudlength));
                        sumQty=sumQty.add(oqty);
                    }

                    //感觉不用
                    //BigDecimal reviewQty = new BigDecimal(sourceFilter.get(0).get("REVIEW_QTY").toString());

                    //ub1.addUpdateValue("PRODUCEDQTY", new DataValue(Check.Null(getQData_process_report.get(0).get("PPQTY").toString())?"0":getQData_process_report.get(0).get("PPQTY").toString(), Types.DECIMAL, DataValue.DataExpression.SubSelf));//减掉

                    StringBuffer demandSqlSb=new StringBuffer("" +
                            " select * from DCP_DEMAND a" +
                            " where a.eid='"+eId+"' " +
                            //" and a.organizationno='"+organizationNO+"' " +
                            " and a.ORDERNO='"+sourcebillno+"' " +
                            " and a.item='"+oitem+"' ");
                    List<Map<String, Object>> demadList = this.doQueryData(demandSqlSb.toString(), null);
                    if(demadList.size()>0){
                        BigDecimal purqty = new BigDecimal(demadList.get(0).get("PURQTY").toString());
                        purqty=purqty.add(sumQty);
                        String distristatus = demadList.get(0).get("DISTRISTATUS").toString();
                        UptBean ub2 = new UptBean("DCP_DEMAND");
                        ub2.addUpdateValue("PURQTY", DataValues.newDecimal(purqty));
                        ub2.addUpdateValue("STATUS", DataValues.newString("1"));
                        if(!"22".equals(distristatus)){//22.采购审核
                            ub2.addUpdateValue("DISTRISTATUS", DataValues.newString("22"));
                        }
                        ub2.addCondition("EID", DataValues.newString(eId));
                        //ub2.addCondition("ORGANIZATIONNO",DataValues.newString(organizationNO));
                        ub2.addCondition("ORDERNO", DataValues.newString(sourcebillno));
                        ub2.addCondition("ITEM", DataValues.newString(oitem));
                        this.addProcessData(new DataProcessBean(ub2));

                        UptBean ub6 = new UptBean("DCP_PORDER_DETAIL");
                        ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                        ub6.addCondition("PORDERNO", new DataValue(sourcebillno, Types.VARCHAR));
                        ub6.addCondition("ITEM", new DataValue(oitem, Types.VARCHAR));
                        ub6.addUpdateValue("DETAIL_STATUS", new DataValue("1", Types.VARCHAR));
                        ub6.addUpdateValue("REVIEW_QTY", new DataValue(purqty, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub6));

                    }

                }

                List<String> sourceBillNos = demandList.stream().map(x -> x.get("SOURCEBILLNO").toString()).collect(Collectors.toList());
                for (String sourceBillNo : sourceBillNos){
                    String pOrderSql="select a.item,a.detail_status from dcp_porder_detail a  where a.eid='"+req.geteId()+"' " +
                            " and a.porderno='"+sourceBillNo+"'";
                    List<Map<String, Object>> pOrderList = this.doQueryData(pOrderSql, null);
                    for (Map<String, Object> pOrder : pOrderList){
                        List<Map<String, Object>> collect = demandList.stream().filter(x -> x.get("SOURCEBILLNO").toString().equals(sourceBillNo)
                                && x.get("OITEM").toString().equals(pOrder.get("ITEM").toString())).distinct().collect(Collectors.toList());
                        if(collect.size()>0){
                            pOrder.put("DETAIL_STATUS","1");
                        }
                    }

                    List<String> detailStatusList = pOrderList.stream().map(x -> x.get("DETAIL_STATUS").toString()).distinct().collect(Collectors.toList());
                    UptBean ub6 = new UptBean("DCP_PORDER");
                    ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                    ub6.addCondition("PORDERNO", new DataValue(sourceBillNo, Types.VARCHAR));

                    ub6.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
                    ub6.addUpdateValue("MODIFY_DATE", new DataValue(mDate, Types.VARCHAR));
                    ub6.addUpdateValue("MODIFY_TIME", new DataValue(mTime, Types.VARCHAR));
                    if(detailStatusList.size()==1){
                        String singleStatus = detailStatusList.get(0).toString();
                        if(singleStatus.equals("1")){
                            ub6.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));
                        }else{
                            ub6.addUpdateValue("STATUS", new DataValue("5", Types.VARCHAR));
                        }
                    }
                    else if(detailStatusList.size()>1){
                        ub6.addUpdateValue("STATUS", new DataValue("9", Types.VARCHAR));
                    }
                    this.addProcessData(new DataProcessBean(ub6));

                }


            }

        }

        //【取消审核】：取消审核订单，删除关联采购收货通知>>
        //● 取消前检查：
        //  ○ 单据状态非[1-已审核]不可取消！
        //  ○ 单据已收货不可取消！
        //  ○ 单据已存在采购变更不可取消！
        //● 取消处理：
        //  ○ 更新单据状态码=【0-新建】，资料异动：审核人/审核时间字段值更新空，最后更改人/最后更改时间刷新；
        //  ○ 联动删除“采购收货通知”相关单据明细，并刷新采购明细对应【已预约收货量】
        //  ○ 更新来源关联单据【已转采购量】=来源项次已转采购量-本单对应采购量（需求转采流程补充）
        if("confRevoke".equals(oprType)){
            String sql="select a.purorderno,'' as  CHANGEPURORDERNO,nvl(c.RECEIVEQTY,0) as RECEIVEQTY from dcp_purorder a " +//nvl(b.PURORDERNO,'')
                    //" left join DCP_PURORDERCHANGE b on a.eid=b.eid and a.purorderno=b.purorderno "+
                    " left join DCP_PURORDER_DELIVERY c on a.eid=c.eid and a.purorderno=c.purorderno "+
                    "where a.eid='"+eId+"' and a.purorderno='"+purOrderNo+"' and a.status='1'";
            List<Map<String, Object>> purOrderData = dao.executeQuerySQL(sql, null);
            if(purOrderData==null||purOrderData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[1-已审核]不可取消！");
            }
            String changePurOrderNo=purOrderData.get(0).get("CHANGEPURORDERNO").toString();
            if(!changePurOrderNo.equals("")){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据已存在采购变更不可取消！");
            }
            BigDecimal receiveqty = new BigDecimal(purOrderData.get(0).get("RECEIVEQTY").toString());
            if(receiveqty.compareTo(new BigDecimal("0"))>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据已收货不可取消！");
            }

            //todo
            //有未作废的采购收货 或者采购收货入库  不能审核
            //存在确定的收货预约单  不能审核

            String prSql="select b.billno from DCP_PURRECEIVE_DETAIL a " +
                    " inner join DCP_PURRECEIVE b on a.eid=b.eid and a.billno=b.billno " +
                    " where a.eid='"+eId+"' and a.purorderno='"+purOrderNo+"' and b.status<>'3' " +
                    " union (" +
                    " select a.sstockinno as billno from dcp_sstockin_detail a " +
                    " inner join dcp_sstockin b on a.eid=b.eid and a.sstockinno=b.sstockinno " +
                    " where a.eid='"+eId+"' and a.ORIGINNO='"+purOrderNo+"' and b.status<>'3' " +
                    ")" +
                    " union (" +
                    " select a.receivingno as billno from dcp_receiving a where a.eid='"+eId+"' and a.ofno='"+purOrderNo+"' and a.status='1' " +
                    ")";
            List<Map<String, Object>> purReceiveData = dao.executeQuerySQL(prSql, null);
            if(purReceiveData.size()>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据存在收货不可取消！");
            }




            UptBean ub1 = new UptBean("DCP_PURORDER");
            ub1.addUpdateValue("STATUS", DataValues.newString("0"));
            ub1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            ub1.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub1.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
            ub1.addUpdateValue("CONFIRMTIME", DataValues.newDate(null));

            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_PURORDER_DELIVERY");
            ub2.addUpdateValue("BOOKQTY", DataValues.newDecimal(0));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
            this.addProcessData(new DataProcessBean(ub2));


            String receivingSql="select * from DCP_RECEIVING a where a.eid='"+eId+"' and a.load_docno='"+purOrderNo+"'";
            List<Map<String, Object>> receivingData = dao.executeQuerySQL(receivingSql, null);
            if(receivingData!=null&&receivingData.size()>0){

                    ParseJson pj = new ParseJson();
                    for (Map<String, Object> row:receivingData){
                        String receivingNo=row.get("RECEIVINGNO").toString();
                        String receivingStatus = row.get("STATUS").toString();
                        //反审核要状态6  不是审核状态  直接删了
                        if(!receivingStatus.equals("6")){
                            continue;
                        }
                        DCP_ReceivingStatusUpdateReq receivingReq=new DCP_ReceivingStatusUpdateReq();
                        receivingReq.setServiceId("DCP_ReceivingStatusUpdate");
                        receivingReq.setToken(req.getToken());
                        DCP_ReceivingStatusUpdateReq.Request request = receivingReq.new Request();
                        request.setOpType("unconfirm");
                        request.setReceivingNo(receivingNo);
                        request.setDocType("3");
                        receivingReq.setRequest(request);

                        String jsontemp= pj.beanToJson(receivingReq);

                        //直接调用CRegisterDCP服务
                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_ReceivingStatusUpdateRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_ReceivingStatusUpdateRes>(){});
                        if(resserver.isSuccess()==false)
                        {
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "通知单撤销审核失败！");
                        }

                    }


                for(Map<String, Object> row:receivingData){
                    String receivingNo=row.get("RECEIVINGNO").toString();
                    DelBean db1 = new DelBean("DCP_RECEIVING");
                    db1.addCondition("RECEIVINGNO", new DataValue(receivingNo,Types.VARCHAR));
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("ORGANIZATIONNO",new DataValue(organizationNO,Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    DelBean db2 = new DelBean("DCP_RECEIVING_DETAIL");
                    db2.addCondition("RECEIVINGNO", new DataValue(receivingNo,Types.VARCHAR));
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("ORGANIZATIONNO",new DataValue(organizationNO,Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));
                }
            }

            String mDate =new SimpleDateFormat("yyyyMMdd").format(new Date());
            String mTime = new SimpleDateFormat("HHmmss").format(new Date());
            //  ○ 更新来源关联单据【已转采购量】=来源项次已转采购量-本单对应采购量（需求转采流程补充）
            String sourceSql="select distinct b.baseunit,a.purunit,a.ounit,c.unitratio as punitratio,d.unitratio as ounitratio,e.UDLENGTH as pudlength,f.udlength as oudlength," +
                    " a.CONVERTPURQTY,a.SOURCEBILLNO,a.OITEM  from DCP_PURORDER_SOURCE a " +
                    " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                    " left join DCP_GOODS_UNIT c on c.eid=a.eid and c.unit=b.baseunit and a.purunit=c.ounit " +
                    " left join dcp_goods_unit d on d.eid=a.eid and d.unit=b.baseunit and a.ounit=d.ounit " +
                    " left join dcp_unit e on e.eid=a.eid and e.unit=a.purunit " +
                    " left join dcp_unit f on f.eid=a.eid and f.unit=a.ounit " +
                    " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                    " and a.PURORDERNO='"+purOrderNo+"' ";
            List<Map<String, Object>> sourceData=this.doQueryData(sourceSql,null);
            if(sourceData.size()>0){
                List<Map<String, Object>> demandList = sourceData.stream().map(x -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("SOURCEBILLNO", x.get("SOURCEBILLNO").toString());
                    map.put("OITEM", x.get("OITEM").toString());
                    return map;
                }).distinct().collect(Collectors.toList());

                for (Map<String, Object> demand : demandList){
                    String sourcebillno = demand.get("SOURCEBILLNO").toString();
                    String oitem = demand.get("OITEM").toString();
                    List<Map<String, Object>> sourceFilter = sourceData.stream().filter(x -> x.get("SOURCEBILLNO").toString().equals(sourcebillno) && x.get("OITEM").toString().equals(oitem)).collect(Collectors.toList());
                    BigDecimal sumQty=BigDecimal.ZERO;
                    for (Map<String, Object> source : sourceFilter){
                        String purunit = source.get("PURUNIT").toString();
                        String ounit = source.get("OUNIT").toString();
                        BigDecimal convertpurqty =new BigDecimal( source.get("CONVERTPURQTY").toString());
                        BigDecimal ounitratio = new BigDecimal(source.get("OUNITRATIO").toString());
                        BigDecimal punitratio = new BigDecimal(source.get("PUNITRATIO").toString());
                        String oudlength = source.get("OUDLENGTH").toString();
                        String pudlength = source.get("PUDLENGTH").toString();

                        BigDecimal oqty = convertpurqty.multiply(punitratio).divide(ounitratio, Integer.parseInt(oudlength));
                        sumQty=sumQty.add(oqty);
                    }

                    //ub1.addUpdateValue("PRODUCEDQTY", new DataValue(Check.Null(getQData_process_report.get(0).get("PPQTY").toString())?"0":getQData_process_report.get(0).get("PPQTY").toString(), Types.DECIMAL, DataValue.DataExpression.SubSelf));//减掉

                    StringBuffer demandSqlSb=new StringBuffer("" +
                            " select * from DCP_DEMAND a" +
                            " where a.eid='"+eId+"'  and a.ORDERNO='"+sourcebillno+"' " +
                            " and a.item='"+oitem+"' ");
                    List<Map<String, Object>> demadList = this.doQueryData(demandSqlSb.toString(), null);

                    //判断有没有其他的有效采购订单
                    StringBuffer demandSsb=new StringBuffer(" select * from dcp_purorder_source a" +
                            " inner join dcp_purorder b on a.eid=b.eid and a.organizationno=b.organizationno and a.purorderno=b.purorderno" +
                            " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                            " and b.status='2' and a.SOURCEBILLNO='"+sourcebillno+"' and a.oitem='"+oitem+"' " +
                            " and a.purorderno !='"+purOrderNo+"'");
                    List<Map<String, Object>> otherDemandList = this.doQueryData(demandSsb.toString(), null);

                    if(demadList.size()>0){
                        BigDecimal purqty = new BigDecimal(demadList.get(0).get("PURQTY").toString());
                        purqty=purqty.subtract(sumQty);
                        String distristatus = demadList.get(0).get("DISTRISTATUS").toString();
                        UptBean ub3 = new UptBean("DCP_DEMAND");
                        ub3.addUpdateValue("PURQTY", DataValues.newDecimal("0"));
                        if("22".equals(distristatus)&&otherDemandList.size()<=0){//22.采购审核
                            ub3.addUpdateValue("DISTRISTATUS", DataValues.newString("21"));//21.已转采购订单
                        }
                        ub3.addCondition("EID", DataValues.newString(eId));
                        //ub3.addCondition("ORGANIZATIONNO",DataValues.newString(organizationNO));
                        ub3.addCondition("ORDERNO", DataValues.newString(sourcebillno));
                        ub3.addCondition("ITEM", DataValues.newString(oitem));
                        this.addProcessData(new DataProcessBean(ub3));


                        UptBean ub6 = new UptBean("DCP_PORDER_DETAIL");
                        ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                        ub6.addCondition("PORDERNO", new DataValue(sourcebillno, Types.VARCHAR));
                        ub6.addCondition("ITEM", new DataValue(oitem, Types.VARCHAR));
                        ub6.addUpdateValue("DETAIL_STATUS", new DataValue("0", Types.VARCHAR));
                        ub6.addUpdateValue("REVIEW_QTY", new DataValue("0", Types.VARCHAR));//得看是不是只能核准一次
                        this.addProcessData(new DataProcessBean(ub6));


                    }

                }


                List<String> sourceBillNos = demandList.stream().map(x -> x.get("SOURCEBILLNO").toString()).distinct().collect(Collectors.toList());
                for (String sourceBillNo : sourceBillNos){
                    String pOrderSql="select a.item,a.detail_status from dcp_porder_detail a  where a.eid='"+req.geteId()+"' " +
                            " and a.porderno='"+sourceBillNo+"'";
                    List<Map<String, Object>> pOrderList = this.doQueryData(pOrderSql, null);
                    for (Map<String, Object> pOrder : pOrderList){
                        List<Map<String, Object>> collect = demandList.stream().filter(x -> x.get("SOURCEBILLNO").toString().equals(sourceBillNo)
                                && x.get("OITEM").toString().equals(pOrder.get("ITEM").toString())).distinct().collect(Collectors.toList());
                        if(collect.size()>0){
                            pOrder.put("DETAIL_STATUS","0");
                        }
                    }

                    List<String> detailStatusList = pOrderList.stream().map(x -> x.get("DETAIL_STATUS").toString()).distinct().collect(Collectors.toList());
                    UptBean ub6 = new UptBean("DCP_PORDER");
                    ub6.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                    ub6.addCondition("PORDERNO", new DataValue(sourceBillNo, Types.VARCHAR));

                    ub6.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
                    ub6.addUpdateValue("MODIFY_DATE", new DataValue(mDate, Types.VARCHAR));
                    ub6.addUpdateValue("MODIFY_TIME", new DataValue(mTime, Types.VARCHAR));
                    if(detailStatusList.size()==1){
                        String singleStatus = detailStatusList.get(0).toString();
                        if(singleStatus.equals("1")){
                            ub6.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));
                        }else{
                            ub6.addUpdateValue("STATUS", new DataValue("5", Types.VARCHAR));
                        }
                    }
                    else if(detailStatusList.size()>1){
                        ub6.addUpdateValue("STATUS", new DataValue("9", Types.VARCHAR));
                    }
                    this.addProcessData(new DataProcessBean(ub6));

                }

            }



        }

        //【结案】：结案分为自动结案和手动结案
        //● 自动结案：①流程触发：采购量=收货量=入库量（完全收货入库）② 排程任务：部分收货完成且订单已逾期（预估到货日+X天<系统日期，X天为自定义缓冲天数），自动结案！
        //● 手动结案：① 部分完成收货且订单未逾期，提前结案 ② 部分收货完成且订单已逾期结案 ③ 其他情况
        //  ○ 结案前检查：
        //    ■ 单据状态非[1-已审核]不可结案！
        //    ■ 存在未完成采购收货相关单据，不可结案！数据源：DCP_PURSTOCKIIN(待创建) todo
        //  ○ 结案更新行结束码状态、单据状态以及结案人、结案时间：
        //    ■ 行结束码：1-已结束
        //    ■ 单据状态：2-已结案
        if("close".equals(oprType)){
            String orderSql=String.format("select * from DCP_PURORDER where eid='%s' and purorderno='%s' and status='1'",eId,purOrderNo);
            List<Map<String, Object>> orderData = dao.executeQuerySQL(orderSql, null);
            if(orderData==null||orderData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[审核]不可执行！");
            }

            UptBean ub1 = new UptBean("DCP_PURORDER_DETAIL");
            ub1.addUpdateValue("CLOSE_STATUS", DataValues.newString("1"));

            ub1.addCondition("EID", DataValues.newString(eId));
            ub1.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_PURORDER");
            ub2.addUpdateValue("STATUS", DataValues.newString("2"));
            //ub2.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getopno()));
            //ub2.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
            ub2.addUpdateValue("CLOSEBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CLOSETIME", DataValues.newDate(lastmoditime));

            ub2.addCondition("EID", DataValues.newString(eId));
            ub2.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
            this.addProcessData(new DataProcessBean(ub2));


        }

        //【取消结案】：已结案订单需撤回继续收货，或下游单据需求的源头单据撤回
        //● 单据状态：1-已审核
        //● 行结束码：判断采购量<=已收货量=已入库量，则结束码保持1-已结束不动；否则返回至0-已结束状态
        //● 更新结案人、结案时间为空，同时更新最近更改人、最近更改时间
        if("closeRevoke".equals(oprType)){
            String orderSql=String.format("select * from DCP_PURORDER where eid='%s' and purorderno='%s' and status='2'",eId,purOrderNo);
            List<Map<String, Object>> orderData = dao.executeQuerySQL(orderSql, null);
            if(orderData==null||orderData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[审核]不可执行！");
            }

            StringBuffer detailSql=new StringBuffer();
            detailSql.append("select a.purorderno,a.item,sum(nvl(b.purqty,0)) as purqtysum,sum(nvl(b.receiveqty,0)) as receiveqtysum ,sum(nvl(b.stockinqty,0)) as stockinqtysum from DCP_PURORDER_DETAIL a" +
                    " left join DCP_PURORDER_DELIVERY b on a.eid=b.eid and a.purorderno=b.purorderno and a.item=b.item " +
                    " where a.purorderno='"+purOrderNo+"' and a.eid='"+eId+"'" +
                    " group by a.purorderno,a.item ");

            List<Map<String, Object>> detailData = dao.executeQuerySQL(detailSql.toString(), null);
            if(detailData==null||detailData.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到相应单据明细,不可执行！");
            }

            boolean isClose=false;
            for(Map<String, Object> row:detailData){
                String item=row.get("ITEM").toString();
                BigDecimal purqtysum=new BigDecimal(row.get("PURQTYSUM").toString());
                BigDecimal receiveqtysum=new BigDecimal(row.get("RECEIVEQTYSUM").toString());
                BigDecimal stockinqtysum=new BigDecimal(row.get("STOCKINQTYSUM").toString());

                if(purqtysum.compareTo(receiveqtysum)<=0&&receiveqtysum.compareTo(stockinqtysum)==0){
                    //结束码保持1-已结束不动
                }else{
                    isClose=true;
                    UptBean ub1 = new UptBean("DCP_PURORDER_DETAIL");
                    ub1.addUpdateValue("CLOSE_STATUS", DataValues.newString("0"));

                    ub1.addCondition("EID", DataValues.newString(eId));
                    ub1.addCondition("ITEM", DataValues.newString(item));
                    ub1.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
                    this.addProcessData(new DataProcessBean(ub1));
                }

            }

            if(isClose){
                UptBean ub1 = new UptBean("DCP_PURORDER");
                ub1.addUpdateValue("STATUS", DataValues.newString("1"));
                ub1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                ub1.addUpdateValue("LASTMODITIME",DataValues.newDate(lastmoditime));
                ub1.addUpdateValue("CLOSEBY", DataValues.newString(req.getOpNO()));
                ub1.addUpdateValue("CLOSETIME", DataValues.newDate(null));

                ub1.addCondition("EID", DataValues.newString(eId));
                ub1.addCondition("PURORDERNO",DataValues.newString(purOrderNo));
                this.addProcessData(new DataProcessBean(ub1));
            }
        }


        this.doExecuteDataToDB();

        if(CollUtil.isNotEmpty(confirmReceivingNos)){
            ParseJson pj = new ParseJson();
            for (String receivingNo:confirmReceivingNos){
                DCP_ReceivingStatusUpdateReq receivingReq=new DCP_ReceivingStatusUpdateReq();
                receivingReq.setServiceId("DCP_ReceivingStatusUpdate");
                receivingReq.setToken(req.getToken());
                DCP_ReceivingStatusUpdateReq.Request request = receivingReq.new Request();
                request.setOpType("confirm");
                request.setReceivingNo(receivingNo);
                request.setDocType("3");
                receivingReq.setRequest(request);

                String jsontemp= pj.beanToJson(receivingReq);

                //直接调用CRegisterDCP服务
                DispatchService ds = DispatchService.getInstance();
                String resXml = ds.callService(jsontemp, StaticInfo.dao);
                DCP_ReceivingStatusUpdateRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_ReceivingStatusUpdateRes>(){});
                if(resserver.isSuccess()==false)
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "通知单审核失败！");
                }

            }
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurOrderStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurOrderStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurOrderStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_PurOrderStatusUpdateReq req) throws Exception {
        return null;
    }

    private String getReceivingNO(DCP_PurOrderStatusUpdateReq req) throws Exception  {
        //billcode
        String billCode="";
        String sql = null;
        String templateNo = null;
        String shopId = req.getShopId();
        String eId = req.geteId();

        StringBuffer sqlbufOrg = new StringBuffer("select billcode FROM dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ");
        sql = sqlbufOrg.toString();
        List<Map<String, Object>> getQDataOrg = this.doQueryData(sql, null);
        if(getQDataOrg!=null&&getQDataOrg.isEmpty()==false){
            billCode = (String) getQDataOrg.get(0).get("BILLCODE");
        }

        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','"+billCode+"-"+"SHTZ') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        }
        else
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }




}

