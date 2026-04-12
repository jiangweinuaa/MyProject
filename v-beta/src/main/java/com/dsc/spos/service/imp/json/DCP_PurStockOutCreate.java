package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurStockOutCreateReq;
import com.dsc.spos.json.cust.res.DCP_PurStockOutCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PurStockOutCreate extends SPosAdvanceService<DCP_PurStockOutCreateReq, DCP_PurStockOutCreateRes> {
    @Override
    protected void processDUID(DCP_PurStockOutCreateReq req, DCP_PurStockOutCreateRes res) throws Exception {

        String orderNo = this.getOrderNO(req, "CTCK");
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        DCP_PurStockOutCreateReq.Request request = req.getRequest();
        String sourceType = request.getSourceType();//1.采购订单 2.采购收货 3.采购入库 4.采购收货入库 5.自采收货入6.退货申请
        String sourceBillNo = request.getSourceBillNo();
        String supplierNo = request.getSupplierNo();

        //获取采购模板
        StringBuilder ptSb=new StringBuilder("SELECT * FROM DCP_PURCHASETEMPLATE_GOODS a" +
                " INNER JOIN DCP_PURCHASETEMPLATE b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO where b.SUPPLIERNO='"+supplierNo+"'" );
        List<Map<String, Object>> ptList=this.doQueryData(ptSb.toString(), null);

        StringBuilder bizSb=new StringBuilder("" +
                "select * from DCP_BIZPARTNER where eid='"+eId+"' and BIZPARTNERNO='"+supplierNo+"'");
        List<Map<String, Object>> bizList=this.doQueryData(bizSb.toString(), null);
        if(bizList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "供应商已失效，请检查!");
        }
        else {
            Map<String, Object> stringObjectMap = bizList.get(0);
            String begindate = stringObjectMap.get("BEGINDATE").toString();
            String enddate = stringObjectMap.get("ENDDATE").toString();
            Date beginDatef = formatter.parse(begindate);
            Date endDatef = formatter.parse(enddate);
            Date bdatef = formatter.parse(request.getBDate());
            if(bdatef.before(beginDatef)||bdatef.after(endDatef)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "供应商已失效，请检查!");
            }
        }

        List<DCP_PurStockOutCreateReq.Detail> dataList = request.getDataList();
        if(dataList==null||dataList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无商品明细，请检查!");
        }


        //③ 单据类型=“5.采购退货”时，检查退货数量是否在可退范围，超过可退数量则提示”#项次 退货量超出可退量，请检查！“，以下情况为异常
        //● 通知单退货：本次退货量> 通知出货量 - 已转出货量-已登打量（排除本单录入量） 6
        //● 入库单退货：本次退货量>源单入库量 - 非作废状态【采退出库单】单据的退货量 4
        List<Map<String, Object>> pstockinList=new ArrayList<>();
        if("4".equals(sourceType)){
            //入库单退货：采购基准价 *（1-下调比例）~ 源单入库单价
            StringBuilder sssb=new StringBuilder("select b.* from DCP_PURSTOCKIN_DETAIL " +
                    "where eid='"+eId+"' and organizationno='"+organizationNO+"' and PSTOCKINNO='"+sourceBillNo+"'");
            pstockinList=this.doQueryData(sssb.toString(), null);
        }

        List<Map<String,Object>> noticeList=new ArrayList<>();//通知单退货用
        if("4".equals(sourceType)){
            StringBuilder noticeSb=new StringBuilder("select * from DCP_STOCKOUTNOTICE_DETAIL " +
                    " where eid='"+eId+"' and organizationno='"+organizationNO+"' and billno='"+request.getReceivingNo()+"'");
            noticeList=this.doQueryData(noticeSb.toString(), null);
        }

        //收集其它出库单
        List<Map<String,Object>> otherList=new ArrayList<>();
        if(!(Check.Null(sourceType)||Check.Null(sourceBillNo))){
            StringBuilder outSb=new StringBuilder("SELECT a.* from DCP_PURSTOCKIN_DETAIL a" +
                    " inner join DCP_PURSTOCKIN b on a.eid=b.eid and a.organizationno=b.organizationno and a.PSTOCKINNO=b.PSTOCKINNO " +
                    " where b.SOURCETYPE='"+sourceType+"' and a.eid='"+eId+"' and b.SOURCEBILLNO='"+sourceBillNo+"' and b.status='0' ");
            otherList=this.doQueryData(outSb.toString(), null);
        }
        List<Map<String,Object>> otherRList=new ArrayList<>();
        if(!Check.Null(request.getReceivingNo())){
            StringBuilder outSb=new StringBuilder("SELECT a.* from DCP_PURSTOCKIN_DETAIL a" +
                    " inner join DCP_PURSTOCKIN b on a.eid=b.eid and a.organizationno=b.organizationno and a.PSTOCKINNO=b.PSTOCKINNO " +
                    " where b.RECEIVINGNO='"+request.getReceivingNo()+"' and a.eid='"+eId+"' and b.status='0' ");
            otherRList=this.doQueryData(outSb.toString(), null);
        }


        int item=0;

        BigDecimal totCqty=new BigDecimal(0);
        BigDecimal totPqty=new BigDecimal(0);
        BigDecimal totPurAmt=new BigDecimal(0);
        BigDecimal totPreTaxAmt=new BigDecimal(0);
        BigDecimal totTaxAmt=new BigDecimal(0);

        List<String> plunos = dataList.stream().map(x -> x.getPluNo()).distinct().collect(Collectors.toList());
        totCqty=new BigDecimal(plunos.size());

        MyCommon cm=new MyCommon();
        StringBuffer sJoinPluNo=new StringBuffer("");
        for(String plu:plunos){
            sJoinPluNo.append(plu+",");
        }
        Map<String, String> mapPlu=new HashMap<String, String>();
        mapPlu.put("PLUNO", sJoinPluNo.toString());

        String withasSql_plu="";
        withasSql_plu=cm.getFormatSourceMultiColWith(mapPlu);
        mapPlu=null;

        if (withasSql_plu.equals("")) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找品号失败!");
        }
        StringBuilder sb = new StringBuilder();

        sb.append("with p AS ( " + withasSql_plu + ") " +
                    " select * from dcp_goods a " +
                    " inner join p on p.pluno=a.pluno " +
                    "  ")
                    ;
        List<Map<String, Object>> getPluData=this.doQueryData(sb.toString(), null);

        if(getPluData.size()==0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找品号失败!");
        }



        for (DCP_PurStockOutCreateReq.Detail detail : dataList){
            item++;
            String wunit="";
            String baseUnit="";
            String location="";
            String batchNo="";
            String category="";

            List<Map<String, Object>> pluno = getPluData.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).distinct().collect(Collectors.toList());
            if(pluno.size()<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"品号"+ detail.getPluNo()+"不存在!");
            }


            wunit = pluno.get(0).get("WUNIT").toString();
            baseUnit = pluno.get(0).get("BASEUNIT").toString();
            category=pluno.get(0).get("CATEGORY").toString();

            BigDecimal wqtySum=new BigDecimal(0);
            BigDecimal baseQtySum=new BigDecimal(0);

            List<DCP_PurStockOutCreateReq.MulitiLosts> mulitiLotsList = detail.getMulitiLotsList();
            if(mulitiLotsList != null&& mulitiLotsList.size()>0){
                if(mulitiLotsList.size()==1){
                    location=mulitiLotsList.get(0).getLocation();
                    batchNo=mulitiLotsList.get(0).getBatchNo();
                    //expDate=mulitiLotsList.get(0).getExpDate();
                }
                int mulItem=0;
                for (DCP_PurStockOutCreateReq.MulitiLosts mulitiLots : mulitiLotsList){
                    mulItem++;
                    BigDecimal pQty = new BigDecimal(mulitiLots.getPQty());
                    if(pQty.compareTo(BigDecimal.ZERO)<=0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"退货数量异常，请检查！");
                    }

                    Map<String, Object> mapBase = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), detail.getPUnit(), mulitiLots.getPQty());
                    if (mapBase == null)
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+detail.getPUnit()+"无记录！");
                    }

                    Map<String, Object> mapWunit = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), wunit,"1");
                    if (mapWunit == null)
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+wunit+"无记录！");
                    }

                    String baseQty = mapBase.get("baseQty").toString();
                    int unitdlength=PosPub.getUnitUDLength(dao, eId, baseUnit);
                    BigDecimal wunitRatio = new BigDecimal(mapWunit.get("unitRatio").toString());
                    String wqty=(new BigDecimal(baseQty)).divide(wunitRatio,4).setScale(unitdlength, RoundingMode.HALF_UP).toString();

                    wqtySum=wqtySum.add(new BigDecimal(wqty));
                    baseQtySum=baseQtySum.add(new BigDecimal(baseQty));

                    ColumnDataValue multiColumns=new ColumnDataValue();
                    multiColumns.add("EID", DataValues.newString(eId));
                    multiColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                    multiColumns.add("PSTOCKINNO", DataValues.newString(orderNo));
                    multiColumns.add("ITEM", DataValues.newString(String.valueOf(mulItem)));
                    multiColumns.add("ITEM2", DataValues.newString(mulitiLots.getItem2()));
                    multiColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                    multiColumns.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
                    multiColumns.add("WAREHOUSE", DataValues.newString(detail.getWareHouse()));
                    multiColumns.add("LOCATION", DataValues.newString(mulitiLots.getLocation()));
                    multiColumns.add("BATCHNO", DataValues.newString(mulitiLots.getBatchNo()));
                    multiColumns.add("PROD_DATE", DataValues.newDate(mulitiLots.getProdDate()));
                    multiColumns.add("EXP_DATE", DataValues.newDate(mulitiLots.getExpDate()));
                    multiColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
                    multiColumns.add("PQTY", DataValues.newDecimal(mulitiLots.getPQty()));
                    multiColumns.add("PASSQTY", DataValues.newDecimal(0));
                    multiColumns.add("REJECTQTY", DataValues.newDecimal(0));
                    multiColumns.add("STOCKINQTY", DataValues.newDecimal(0));
                    multiColumns.add("WUNIT", DataValues.newString(wunit));
                    multiColumns.add("WQTY", DataValues.newDecimal(wqty));
                    multiColumns.add("BASEUNIT", DataValues.newString(baseUnit));
                    multiColumns.add("BASEQTY", DataValues.newDecimal(baseQty));

                    String[] multiColumnNames = multiColumns.getColumns().toArray(new String[0]);
                    DataValue[] multiDataValues = multiColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib1=new InsBean("DCP_PURSTOCKIN_LOTS",multiColumnNames);
                    ib1.addValues(multiDataValues);
                    this.addProcessData(new DataProcessBean(ib1));
                }
            }
            else {
                Map<String, Object> mapBase = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), detail.getPUnit(), detail.getPQty());
                if (mapBase == null)
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+detail.getPUnit()+"无记录！");
                }

                Map<String, Object> mapWunit = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), wunit,"1");
                if (mapWunit == null)
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+wunit+"无记录！");
                }

                String baseQty = mapBase.get("baseQty").toString();
                int unitdlength=PosPub.getUnitUDLength(dao, eId, baseUnit);
                BigDecimal wunitRatio = new BigDecimal(mapWunit.get("unitRatio").toString());
                String wqty=(new BigDecimal(baseQty)).divide(wunitRatio,4).setScale(unitdlength, RoundingMode.HALF_UP).toString();

                wqtySum=wqtySum.add(new BigDecimal(wqty));
                baseQtySum=baseQtySum.add(new BigDecimal(baseQty));

                ColumnDataValue multiColumns=new ColumnDataValue();
                multiColumns.add("EID", DataValues.newString(eId));
                multiColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                multiColumns.add("PSTOCKINNO", DataValues.newString(orderNo));
                multiColumns.add("ITEM", DataValues.newString("1"));
                multiColumns.add("ITEM2", DataValues.newString("1"));
                multiColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                multiColumns.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
                multiColumns.add("WAREHOUSE", DataValues.newString(detail.getWareHouse()));
                multiColumns.add("LOCATION", DataValues.newString(""));
                multiColumns.add("BATCHNO", DataValues.newString(""));
                multiColumns.add("PROD_DATE", DataValues.newDate(detail.getProdDate()));
                multiColumns.add("EXP_DATE", DataValues.newDate(detail.getExpDate()));
                multiColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
                multiColumns.add("PQTY", DataValues.newDecimal(detail.getPQty()));
                multiColumns.add("PASSQTY", DataValues.newDecimal(0));
                multiColumns.add("REJECTQTY", DataValues.newDecimal(0));
                multiColumns.add("STOCKINQTY", DataValues.newDecimal(0));
                multiColumns.add("WUNIT", DataValues.newString(wunit));
                multiColumns.add("WQTY", DataValues.newDecimal(wqty));
                multiColumns.add("BASEUNIT", DataValues.newString(baseUnit));
                multiColumns.add("BASEQTY", DataValues.newDecimal(baseQty));

                String[] multiColumnNames = multiColumns.getColumns().toArray(new String[0]);
                DataValue[] multiDataValues = multiColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1=new InsBean("DCP_PURSTOCKIN_LOTS",multiColumnNames);
                ib1.addValues(multiDataValues);
                this.addProcessData(new DataProcessBean(ib1));

            }

            BigDecimal pQty = new BigDecimal(detail.getPQty());
            if(pQty.compareTo(BigDecimal.ZERO)<=0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"退货数量异常，请检查！");
            }

            //② 检查退货单价是否在允许价格范围，低于最低价格或超出最大允许价格则提示”#项次 退货价不在允许价格范围（minprice~maxprice），请检查！“，退货价格允许范围分以下情况判断：
            //价格下调比例从采购模板获取，转换为使用退货单位对应价格范围再来判断
            //● 入库单退货：采购基准价 *（1-下调比例）~ 源单入库单价
            //● 无源退货：采购基准价 *（1-下调比例）~ 采购基准价
            List<Map<String, Object>> ptFillter = ptList.stream().filter(pt -> pt.get("PLUNO").equals(detail.getPluNo()) && pt.get("PURTEMPLATENO").equals(detail.getPurTemplateNo())).distinct().collect(Collectors.toList());
            if(ptFillter.size()==0){//先不限制
                //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"找不到对应的模板，请检查！");
            }
            else{
                Map<String, Object> pt = ptFillter.get(0);
                String purbaseprice = pt.get("PURBASEPRICE").toString();
                String minrate = pt.get("MINRATE").toString();
                String maxrate = pt.get("MAXRATE").toString();
                String purunit = pt.get("PURUNIT").toString();

                //比较价格先转换成基准单位的
                Map<String, Object> mapBase1 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), purunit, purbaseprice);
                if (mapBase1 == null)
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+purunit+"无记录！");
                }
                Map<String, Object> mapBase2 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), detail.getPUnit(), purbaseprice);
                if (mapBase2 == null)
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+detail.getPUnit()+"无记录！");
                }

                BigDecimal purbasepriceBase=new BigDecimal(mapBase1.get("baseQty").toString());//基准价格
                BigDecimal purPriceBase=new BigDecimal(mapBase2.get("baseQty").toString());//比较价格

                //下限比较
                int compareLess = purbasepriceBase.multiply(new BigDecimal("1").subtract(new BigDecimal(minrate))).compareTo(purPriceBase);
                if(compareLess>0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"退货单价不能低于"+purbaseprice+"，请检查！");
                }

                //上限比较
                if("4".equals(sourceType)){
                    //取原单
                    List<Map<String, Object>> resourList = pstockinList.stream().filter(x -> x.get("PLUNO").equals(detail.getPluNo())).collect(Collectors.toList());
                    if(resourList.size()==0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"找不到对应的退货单，请检查！");
                    }else{
                        Map<String, Object> resour = resourList.get(0);
                        String rpurprice = resour.get("PURPRICE").toString();
                        int compareMore = new BigDecimal("purbaseprice").compareTo(new BigDecimal(rpurprice));
                        if(compareMore<0){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"退货单价不能高于"+purbaseprice+"，请检查！");
                        }
                    }

                }else{
                    int compareMore = purbasepriceBase.compareTo(purPriceBase);
                    if(compareMore<0){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"退货单价不能高于"+purbaseprice+"，请检查！");
                    }
                }


            }

            //● 通知单退货：本次退货量> 通知出货量 - 已转出货量-已登打量（排除本单录入量） 6
            if(Check.Null(request.getReceivingNo())){
                List<Map<String, Object>> noticeItem = noticeList.stream().filter(x -> x.get("ITEM").toString().equals(detail.getRItem())).collect(Collectors.toList());
                if(noticeItem.size()==0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"找不到对应的通知单，请检查！");
                }else{
                    Map<String, Object> notice = noticeItem.get(0);
                    String punit = notice.get("PUNIT").toString();//交易单位
                    String pqty = notice.get("PQTY").toString();//交易数量
                    String stockoutqty = notice.get("STOCKOUTQTY").toString();//已退货量

                    Map<String, Object> mapBase = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), punit, pqty);
                    if (mapBase == null)
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+punit+"无记录！");
                    }
                    Map<String, Object> mapBase1 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), punit, stockoutqty);
                    if (mapBase1 == null)
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+punit+"无记录！");
                    }
                    BigDecimal basepQty = new BigDecimal(mapBase.get("baseQty").toString());
                    BigDecimal basestockoutqty = new BigDecimal(mapBase1.get("baseQty").toString());


                    String pQtynow = detail.getPQty();//本次退货量
                    String pUnitnow = detail.getPUnit();//本次退货单位
                    Map<String, Object> mapBase2 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), pUnitnow, pQtynow);
                    if (mapBase2 == null)
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+pUnitnow+"无记录！");
                    }
                    BigDecimal basepQtynow= new BigDecimal(mapBase2.get("baseQty").toString());


                    BigDecimal ddqty=new BigDecimal(0);//登打量

                    List<Map<String, Object>> others = otherRList.stream().filter(x -> x.get("RECEIVINGITEM").equals(detail.getRItem())).collect(Collectors.toList());
                    //收集其他通知单的退货数量
                    if(others.size()>0){
                       for (Map<String, Object> other : others){
                           String punitOther = other.get("PUNIT").toString();
                           String pqtyOther = other.get("PQTY").toString();
                           Map<String, Object> mapBase3 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), punitOther, pqtyOther);
                           if (mapBase2 == null)
                           {
                               throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+punitOther+"无记录！");
                           }
                           BigDecimal basepqtyOther= new BigDecimal(mapBase3.get("baseQty").toString());
                           ddqty=ddqty.add(basepqtyOther);

                       }
                    }
                    int compare = basestockoutqty.add(basepQtynow).add(ddqty).compareTo(basepQty);

                    if(compare>0){
                        //超了
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"本次退货数量不能大于通知单退货数量，请检查！");
                    }

                }
            }else if("4".equals(sourceType)){//入库单退货

                //找入库单
                List<Map<String, Object>> psInList = pstockinList.stream().filter(x -> x.get("ITEM").toString().equals(detail.getOItem())).collect(Collectors.toList());
                if(psInList.size()==0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"找不到对应的通知单，请检查！");
                }else{
                    Map<String, Object> ps = psInList.get(0);
                    String punit = ps.get("PUNIT").toString();//入库单位
                    String pqty = ps.get("PQTY").toString();//入库数量

                    Map<String, Object> mapBase = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), punit, pqty);
                    if (mapBase == null)
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+punit+"无记录！");
                    }
                    BigDecimal basepQty = new BigDecimal(mapBase.get("baseQty").toString());


                    String pQtynow = detail.getPQty();//本次退货量
                    String pUnitnow = detail.getPUnit();//本次退货单位
                    Map<String, Object> mapBase2 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), pUnitnow, pQtynow);
                    if (mapBase2 == null)
                    {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+pUnitnow+"无记录！");
                    }
                    BigDecimal basepQtynow= new BigDecimal(mapBase2.get("baseQty").toString());


                    BigDecimal ddqty=new BigDecimal(0);//登打量

                    List<Map<String, Object>> others = otherList.stream().filter(x -> x.get("RECEIVINGITEM").equals(detail.getRItem())).collect(Collectors.toList());
                    //收集其他通知单的退货数量
                    if(others.size()>0){
                        for (Map<String, Object> other : others){
                            String punitOther = other.get("PUNIT").toString();
                            String pqtyOther = other.get("PQTY").toString();
                            Map<String, Object> mapBase3 = PosPub.getBaseQty(dao, req.geteId(), detail.getPluNo(), punitOther, pqtyOther);
                            if (mapBase2 == null)
                            {
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+punitOther+"无记录！");
                            }
                            BigDecimal basepqtyOther= new BigDecimal(mapBase3.get("baseQty").toString());
                            ddqty=ddqty.add(basepqtyOther);

                        }
                    }
                    int compare =basepQtynow.add(ddqty).compareTo(basepQty);

                    if(compare>0){
                        //超了
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "项次"+detail.getItem()+"本次退货数量不能大于通知单退货数量，请检查！");
                    }

                }

            }


            totPqty = totPqty.add(new BigDecimal(detail.getPQty()));
            totPurAmt=totPurAmt.add(new BigDecimal(detail.getPurAmt()));
            totTaxAmt=totTaxAmt.add(new BigDecimal(detail.getTaxAmt()));
            totPreTaxAmt=totPreTaxAmt.add(new BigDecimal(detail.getPreTaxAmt()));

            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
            detailColumns.add("PSTOCKINNO",DataValues.newString(orderNo));
            detailColumns.add("ITEM", DataValues.newInteger(item));
            detailColumns.add("RECEIVINGNO", DataValues.newString(request.getReceivingNo()));
            //detailColumns.add("RECEIVINGITEM", DataValues.newInteger(detail.getReceivingItem())); todo
            detailColumns.add("PURORDERNO", DataValues.newString(detail.getPurOrderNo()));
            detailColumns.add("POITEM", DataValues.newString(detail.getPoItem()));
            detailColumns.add("POITEM2", DataValues.newString(detail.getPoItem2()));
            detailColumns.add("OITEM", DataValues.newString(detail.getOItem()));
            detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
            detailColumns.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
            detailColumns.add("CATEGORY", DataValues.newString(category));
            detailColumns.add("ISFREE", DataValues.newString(detail.getIsFree()));
            detailColumns.add("WAREHOUSE", DataValues.newString(detail.getWareHouse()));
            detailColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
            detailColumns.add("PQTY", DataValues.newDecimal(detail.getPQty()));
            detailColumns.add("LOCATION", DataValues.newString(location));
            detailColumns.add("BATCHNO", DataValues.newString(batchNo));
            detailColumns.add("PROD_DATE", DataValues.newDate(detail.getProdDate()));
            detailColumns.add("EXP_DATE", DataValues.newDate(detail.getExpDate()));
            detailColumns.add("TAXCODE", DataValues.newString(detail.getTaxCode()));
            detailColumns.add("TAXRATE", DataValues.newDecimal(detail.getTaxRate()));
            detailColumns.add("INCLTAX", DataValues.newString(detail.getInclTax()));
            detailColumns.add("PURPRICE", DataValues.newDecimal(detail.getPurPrice()));
            detailColumns.add("PURAMT", DataValues.newDecimal(detail.getPurAmt()));
            detailColumns.add("PRETAXAMT", DataValues.newDecimal(detail.getPreTaxAmt()));
            detailColumns.add("TAXAMT", DataValues.newDecimal(detail.getTaxAmt()));
            //detailColumns.add("IS_QUALITYCHECK", DataValues.newString(detail.getIsQc()));
            detailColumns.add("PASSQTY", DataValues.newDecimal(0));
            detailColumns.add("REJECTQTY", DataValues.newDecimal(0));
            detailColumns.add("STOCKINQTY", DataValues.newDecimal(0));
            detailColumns.add("WUNIT", DataValues.newString(wunit));
            detailColumns.add("WQTY", DataValues.newDecimal(wqtySum));
            detailColumns.add("BASEUNIT", DataValues.newString(baseUnit));
            detailColumns.add("BASEQTY", DataValues.newDecimal(baseQtySum));
            detailColumns.add("MEMO", DataValues.newString(detail.getMemo()));
            detailColumns.add("PURTEMPLATENO", DataValues.newString(detail.getPurTemplateNo()));


            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ib1=new InsBean("DCP_PURSTOCKIN_DETAIL",detailColumnNames);
            ib1.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(ib1));
        }

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
        mainColumns.add("BDATE",DataValues.newDate(request.getBDate()));
        //ACCOUNT_DATE  null
        mainColumns.add("PSTOCKINNO",DataValues.newString(orderNo));
        mainColumns.add("BILLTYPE",DataValues.newString("5"));//枚举值 1.采购收货单 2.采购入库单 3.采购收货入库 4.无来源收货入库单 5.采购退货固定5.采购退货
        mainColumns.add("RECEIVINGNO",DataValues.newString(request.getReceivingNo()));
        mainColumns.add("SOURCETYPE",DataValues.newString(request.getSourceType()));
        mainColumns.add("SOURCEBILLNO",DataValues.newString(request.getSourceBillNo()));
        mainColumns.add("SUPPLIER",DataValues.newString(request.getSupplierNo()));
        mainColumns.add("EMPLOYEEID",DataValues.newString(employeeNo));
        mainColumns.add("DEPARTID",DataValues.newString(departmentNo));
        mainColumns.add("PURORGNO",DataValues.newString(request.getPurOrgNo()));
        mainColumns.add("TOT_CQTY",DataValues.newDecimal(totCqty));
        mainColumns.add("TOT_PQTY",DataValues.newDecimal(totPqty));
        mainColumns.add("TOT_PURAMT",DataValues.newDecimal(totPurAmt));
        mainColumns.add("TOT_PRETAXAMT",DataValues.newDecimal(totPreTaxAmt));
        mainColumns.add("TOT_TAXAMT",DataValues.newDecimal(totTaxAmt));
        mainColumns.add("PAYTYPE",DataValues.newString(request.getPayType()));
        mainColumns.add("PAYORGNO",DataValues.newString(request.getPayOrgNo()));
        mainColumns.add("BILLDATENO",DataValues.newDate(request.getBillDateNo()));
        mainColumns.add("PAYDATENO",DataValues.newDate(request.getPayDateNo()));
        mainColumns.add("INVOICECODE",DataValues.newString(request.getInvoiceCode()));
        mainColumns.add("CURRENCY",DataValues.newString(request.getCurrency()));
        //mainColumns.add("DELIVERYFEE",DataValues.newDecimal(request.getDeliveryFee()));
        mainColumns.add("STATUS",DataValues.newString("0"));//新增
        mainColumns.add("MEMO",DataValues.newString(request.getMemo()));
        mainColumns.add("OWNOPID",DataValues.newString(employeeNo));
        mainColumns.add("OWNDEPTID",DataValues.newString(departmentNo));
        mainColumns.add("CREATEOPID",DataValues.newString(employeeNo));
        mainColumns.add("CREATEDEPTID",DataValues.newString(departmentNo));
        mainColumns.add("CREATETIME",DataValues.newDate(lastmoditime));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib1=new InsBean("DCP_PURSTOCKIN",mainColumnNames);
        ib1.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib1));

        res.setPStockOutNo(orderNo);
        this.doExecuteDataToDB();

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurStockOutCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurStockOutCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurStockOutCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PurStockOutCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurStockOutCreateReq> getRequestType() {
        return new TypeToken<DCP_PurStockOutCreateReq>() {

        };
    }

    @Override
    protected DCP_PurStockOutCreateRes getResponseType() {
        return new DCP_PurStockOutCreateRes();
    }

    @Override
    protected String getQuerySql(DCP_PurStockOutCreateReq req) throws Exception {
        return null;
    }
}
