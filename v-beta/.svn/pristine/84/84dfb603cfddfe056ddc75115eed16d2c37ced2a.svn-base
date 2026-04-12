package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DemandPreAllotCalculateReq;
import com.dsc.spos.json.cust.res.DCP_DemandPreAllotCalculateRes;
import com.dsc.spos.json.cust.res.DCP_SupplierGoodsOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_DemandPreAllotCalculate extends SPosBasicService<DCP_DemandPreAllotCalculateReq, DCP_DemandPreAllotCalculateRes> {

    @Override
    protected boolean isVerifyFail(DCP_DemandPreAllotCalculateReq req) throws Exception {
        StringBuilder errMsg = new StringBuilder();
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_DemandPreAllotCalculateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_DemandPreAllotCalculateReq>() {
        };
    }

    @Override
    protected DCP_DemandPreAllotCalculateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_DemandPreAllotCalculateRes();
    }

    @Override
    protected DCP_DemandPreAllotCalculateRes processJson(DCP_DemandPreAllotCalculateReq req) throws Exception {
        // TODO Auto-generated method stub
        //开始查询所有部门的列表
        DCP_DemandPreAllotCalculateRes res = new DCP_DemandPreAllotCalculateRes();
        int totalRecords=0;                //总笔数
        int totalPages=0;
        DCP_DemandPreAllotCalculateRes.level1Elm level1Elm = res.new level1Elm();
        level1Elm.setPluList(new ArrayList<>());
        level1Elm.setObjectList(new ArrayList<>());

        String noticeDetailSql = getNoticeDetailSql(req);
        List<Map<String, Object>> noticeDetailData=this.doQueryData(noticeDetailSql, null);
        StringBuffer sJoinno=new StringBuffer("");
        StringBuffer sJoinitem=new StringBuffer("");
        if(noticeDetailData.size()>0) {
            for (Map<String, Object> noticeDetail : noticeDetailData) {
                String orderNo = noticeDetail.get("ORDERNO").toString();
                String item = noticeDetail.get("ITEM").toString();
                sJoinno.append(orderNo + ",");
                sJoinitem.append(item + ",");
            }
        }else{
            sJoinno.append("tempOrder" + ",");
            sJoinitem.append("999" + ",");
        }

        Map<String, String> mapOrder=new HashMap<String, String>();
        mapOrder.put("ORDERNO", sJoinno.toString());
        mapOrder.put("ITEM", sJoinitem.toString());
        MyCommon cm=new MyCommon();
        String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

        String mainSql = this.getMainSql(req,withasSql_mono);
        List<Map<String, Object>> mainData=this.doQueryData(mainSql, null);

        String queryType = req.getRequest().getQueryType();
        String allotType = req.getRequest().getAllotType();

        //库存存的是基准单位  分配要转换成需求单位
        //先查分组的数据 再查条件内的数据 再数据归类
        List<DCP_DemandPreAllotCalculateRes.DemandDetail> demandList=new ArrayList();
        List<Map<String, Object>> getQData=new ArrayList<>();
        if("1".equals(queryType)){
            String sql=getQuerySqlForItem(req,withasSql_mono);
            getQData=this.doQueryData(sql, null);
            if (getQData != null && getQData.isEmpty() == false) {
                String num = getQData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                for (Map<String, Object> row : getQData){
                    String pluNo = row.get("PLUNO").toString();
                    String featureNo = row.get("FEATURENO").toString();
                    String pUnit = row.get("PUNIT").toString();
                    String warehouse = row.get("WAREHOUSE").toString();

                    List<Map<String, Object>> singleFilterList = mainData.stream().filter(x ->
                            x.get("PLUNO").toString().equals(pluNo) &&
                            x.get("FEATURENO").toString().equals(featureNo) &&
                            x.get("PUNIT").toString().equals(pUnit) &&
                            x.get("WAREHOUSE").toString().equals(warehouse)).collect(Collectors.toList());

                    setDemandList(singleFilterList,demandList,res);

                }
            }
        }
        else {
            String sql = getQuerySqlForOrg(req,withasSql_mono);
            getQData = this.doQueryData(sql, null);
            if (getQData != null && getQData.isEmpty() == false) {
                String num = getQData.get(0).get("NUM").toString();
                totalRecords = Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> row : getQData){
                    String orderno = row.get("ORDERNO").toString();
                    List<Map<String, Object>> singleFilterList = mainData.stream().filter(x -> x.get("ORDERNO").equals(orderno)).collect(Collectors.toList());

                    setDemandList(singleFilterList,demandList,res);
                }

            }
        }

        if(demandList.size()>0){
            //进行分配
            List<String> plunos = demandList.stream().map(x -> x.getPluNo()).distinct().collect(Collectors.toList());

            List<String> templateNos = demandList.stream().map(x -> x.getTemplateNo()).distinct().collect(Collectors.toList());

            StringBuffer sJoinPluNo = new StringBuffer();
            for (String pluno : plunos) {
                sJoinPluNo.append(pluno + ",");
            }
            Map<String, String> map = new HashMap<>();
            map.put("PLUNO", sJoinPluNo.toString());

            StringBuffer sJoinTemplateNo = new StringBuffer();
            for (String templateNo : templateNos) {
                sJoinTemplateNo.append(templateNo + ",");
            }
            Map<String, String> tmap = new HashMap<>();
            tmap.put("TEMPLATENO", sJoinTemplateNo.toString());

            String withPlu = cm.getFormatSourceMultiColWith(map);

            String withTemplate = cm.getFormatSourceMultiColWith(tmap);


            String sql = " select a.pluno,a.featureno,a.warehouse,sum(a.qty) as baseqty from dcp_stock a"
                    + " inner join ("+withPlu+") b on a.pluno=b.pluno "
                    + " inner join dcp_warehouse c on a.eid=c.eid  and a.warehouse=c.warehouse and c.warehouse_type<>'3'" //and a.organizationno=c.organizationno 先去掉来测试
                    + " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.qty>0 "
                    + " group by a.pluno,a.featureno,a.warehouse";
            List<Map<String, Object>> getStock = this.doQueryData(sql, null);

            String templateSql="select a.ALLOTTYPE,a.ptemplateno from DCP_PTEMPLATE a" +
                    " inner join ("+withTemplate+") b on a.PTEMPLATENO=b.TEMPLATENO "+
                    " where a.eid='"+req.geteId()+"'";
            List<Map<String, Object>> getTemplate = this.doQueryData(templateSql, null);

            List<DCP_DemandPreAllotCalculateRes.PluAngle> pluAngles = demandList.stream().map(x -> {
                DCP_DemandPreAllotCalculateRes.PluAngle pluAngle = res.new PluAngle();
                String pluno = x.getPluNo();
                String featureno = x.getFeatureNo();
                String warehouse = x.getWareHouse();
                String baseunit = x.getBaseUnit();
                pluAngle.setPluNo(pluno);
                pluAngle.setFeatureNo(featureno);
                pluAngle.setWarehouse(warehouse);
                pluAngle.setBaseUnit(baseunit);
                //List<String> pluTemplateNos=new ArrayList<>();

                BigDecimal availableStockQtyBase=BigDecimal.ZERO;//可用库存量
                List<Map<String, Object>> pluStock = getStock.stream().filter(z -> z.get("PLUNO").toString().equals(pluno) && z.get("FEATURENO").toString().equals(featureno)&&z.get("WAREHOUSE").toString().equals(warehouse)).distinct().collect(Collectors.toList());
                if(pluStock.size()>0){
                    availableStockQtyBase=new BigDecimal( pluStock.get(0).get("BASEQTY").toString());
                }
                pluAngle.setStockQty(availableStockQtyBase);
                return pluAngle;
            }).distinct().collect(Collectors.toList());

            for (DCP_DemandPreAllotCalculateRes.PluAngle pluAngle : pluAngles){

                String pluNo = pluAngle.getPluNo();
                String featureNo = pluAngle.getFeatureNo();
                String warehouse = pluAngle.getWarehouse();

                //BigDecimal availableStockQty=BigDecimal.ZERO;//可用库存量
                BigDecimal availableStockQtyBase=BigDecimal.ZERO;
                List<Map<String, Object>> pluStock = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo) && x.get("FEATURENO").toString().equals(featureNo)&&x.get("WAREHOUSE").toString().equals(warehouse)).distinct().collect(Collectors.toList());
                if(pluStock.size()>0){
                    availableStockQtyBase=new BigDecimal( pluStock.get(0).get("BASEQTY").toString());
                }


                //分配方式：①-按比例均衡分配 ②-优先级分配  ④-按待配量分配，其中①、④可单独指定以满足无模板需求来源进行分配
                //③-循环分配 -模板上面的

                List<DCP_DemandPreAllotCalculateRes.DemandDetail> filterPluRows = demandList.stream().filter(x ->
                        x.getPluNo().equals(pluNo) &&
                                x.getFeatureNo().equals(featureNo) &&
                                x.getWareHouse().equals(warehouse)).collect(Collectors.toList());

                List<String> filterTemplateNos = filterPluRows.stream().map(x -> x.getTemplateNo()).distinct().collect(Collectors.toList());
                //循环模板的分配才行
                for (String templateNo : filterTemplateNos) {
                    List<Map<String, Object>> yetTemplates = getTemplate.stream().filter(x -> x.get("PTEMPLATENO").toString().equals(templateNo)).collect(Collectors.toList());
                    String thisAllotType=req.getRequest().getAllotType();
                    Boolean isCircle=false;

                    if("1".equals(thisAllotType)) {//按模板才走下面
                        if (yetTemplates.size() > 0) {
                            thisAllotType = yetTemplates.get(0).get("ALLOTTYPE").toString();
                            //有模板 还等于3 就是循环 否则就是传进来的按待配量
                            if (thisAllotType.equals("3")) {
                                isCircle = true;
                            }
                        }
                    }

                    List<DCP_DemandPreAllotCalculateRes.DemandDetail> filterRows = filterPluRows.stream().filter(x ->
                            x.getTemplateNo().equals(templateNo) ).collect(Collectors.toList());
                    if("2".equals(thisAllotType)||isCircle){
                        //正序  从小到大
                        filterRows.sort(Comparator.comparing((DCP_DemandPreAllotCalculateRes.DemandDetail m) -> (m.getSortId())));
                    }else{
                        filterRows.sort(Comparator.comparing((DCP_DemandPreAllotCalculateRes.DemandDetail m) -> (m.getOrderItem())));
                    }

                    BigDecimal bpqty=BigDecimal.ZERO;//必配  用基准单位合计
                    BigDecimal dpqty=BigDecimal.ZERO;//待配
                    BigDecimal sydpqty=BigDecimal.ZERO;//剩余待配总量
                    BigDecimal sydpqtyFloat=BigDecimal.ZERO;//剩余待配总量浮动
                    List<DCP_DemandPreAllotCalculateRes.DemandDetail> bpList = filterPluRows.stream().filter(x -> x.getIsMustAllot().equals("Y")).collect(Collectors.toList());
                    List<DCP_DemandPreAllotCalculateRes.DemandDetail> fbpList = filterPluRows.stream().filter(x -> !x.getIsMustAllot().equals("Y")).collect(Collectors.toList());

                    for (DCP_DemandPreAllotCalculateRes.DemandDetail bp : filterRows){
                        BigDecimal processQtyBase = bp.getProcessQtyBase();
                        dpqty=dpqty.add(processQtyBase);
                    }
                    for (DCP_DemandPreAllotCalculateRes.DemandDetail bp : bpList){
                        BigDecimal processQtyBase = bp.getProcessQtyBase();
                        bpqty=bpqty.add(processQtyBase);
                    }

                    sydpqty=dpqty.subtract(bpqty);//剩余待配
                    //浮动 FLOATSCALE  非必配的才有用
                    for (DCP_DemandPreAllotCalculateRes.DemandDetail fbp : fbpList){
                        BigDecimal processQtyBase = fbp.getProcessQtyBase();
                        BigDecimal floatscale = new BigDecimal(fbp.getFloatScale().toString());
                        //向下取整
                        BigDecimal floatQty = processQtyBase.multiply(new BigDecimal("1").add(floatscale)).setScale(0,RoundingMode.HALF_DOWN);
                        sydpqtyFloat=sydpqtyFloat.add(floatQty);
                    }

                    //分配allotQty ->基准单位数量
                    //1、按比例均衡分配
                    if(!isCircle) {
                        if ("1".equals(thisAllotType)) {
                            //库存量大于待配  按照待配量分配结束
                            //否则
                            if (availableStockQtyBase.compareTo(dpqty) > 0) {
                                //先把比配的配完  再看浮动比例
                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {

                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();
                                    BigDecimal processQtyBase = singleItem.getProcessQty();
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;
                                    if ("Y".equals(ismustallot)) {
                                        if (availableStockQtyBase.compareTo(processQtyBase) >= 0) {
                                            allotQtyBase = processQtyBase;
                                            availableStockQtyBase = availableStockQtyBase.subtract(processQtyBase);
                                        }
                                    }

                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }

                                if (availableStockQtyBase.compareTo(sydpqtyFloat) > 0) {
                                    for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                        if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                            continue;
                                        }

                                        String ismustallot = singleItem.getIsMustAllot();
                                        BigDecimal float_scale = singleItem.getFloatScale();

                                        if ("Y".equals(ismustallot)) {
                                            continue;
                                        }
                                        BigDecimal processQtyBase = singleItem.getProcessQtyBase();//待配量
                                        BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量

                                        allotQtyBase = processQtyBase.multiply(new BigDecimal("1").add(float_scale)).setScale(0, BigDecimal.ROUND_HALF_DOWN);//向下取整
                                        availableStockQtyBase = availableStockQtyBase.subtract(allotQtyBase);
                                        singleItem.setAllotQtyBase(allotQtyBase);
                                    }
                                }
                                else {
                                    for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                        if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                            continue;
                                        }
                                        String ismustallot = singleItem.getIsMustAllot();

                                        BigDecimal processQtyBase = singleItem.getProcessQtyBase();//待配量
                                        BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量

                                        if(bpqty.compareTo(BigDecimal.ZERO)>0){
                                            allotQtyBase = availableStockQtyBase.multiply(processQtyBase).divide(bpqty, 0, BigDecimal.ROUND_HALF_UP);//向上取整
                                        }else{
                                            allotQtyBase = processQtyBase;
                                        }
                                        singleItem.setAllotQtyBase(allotQtyBase);
                                    }
                                }

                            }
                            else if (availableStockQtyBase.compareTo(bpqty) <= 0) {
                                //库存比必须配总量还小 将库存总量分配完
                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();
                                    BigDecimal processQtyBase = singleItem.getProcessQtyBase();
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;
                                    if ("Y".equals(ismustallot)) {
                                        if (availableStockQtyBase.compareTo(processQtyBase) >= 0) {
                                            allotQtyBase = processQtyBase;
                                            availableStockQtyBase = availableStockQtyBase.subtract(processQtyBase);
                                        } else {
                                            allotQtyBase = availableStockQtyBase;
                                            availableStockQtyBase = BigDecimal.ZERO;//分配完了

                                        }
                                    }
                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }
                            }
                            else {
                                //库存比必配总量还大 比待配总量小 必配量能配  剩余待配配一部分
                                //剩余库存不可能比浮动后的剩余待配大   按比例分配
                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();

                                    BigDecimal processQtyBase = singleItem.getProcessQtyBase();
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;
                                    if ("Y".equals(ismustallot)) {
                                        if (availableStockQtyBase.compareTo(processQtyBase) >= 0) {
                                            allotQtyBase = processQtyBase;
                                            availableStockQtyBase = availableStockQtyBase.subtract(processQtyBase);
                                        }
                                    }

                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }
                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();

                                    if ("Y".equals(ismustallot)) {
                                        continue;
                                    }
                                    BigDecimal processQtyBase = singleItem.getProcessQtyBase();//待配量
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量
                                    //这边应该用剩余待配做比例均衡  比配的前面已经分配过了 剩下的部分都是剩余待配量
                                    if(sydpqty.compareTo(BigDecimal.ZERO)>0){
                                        allotQtyBase = availableStockQtyBase.multiply(processQtyBase).divide(sydpqty, 0, BigDecimal.ROUND_HALF_UP);//向上取整
                                    }else{
                                        allotQtyBase = processQtyBase;//这边其实都是0 了
                                    }

                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }

                            }
                        }

                        //2.按优先级
                        if ("2".equals(thisAllotType)) {
                            if (availableStockQtyBase.compareTo(dpqty) > 0) {
                                //先把比配的配完  再看浮动比例
                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();

                                    BigDecimal processQtyBase = singleItem.getProcessQtyBase();
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;
                                    if ("Y".equals(ismustallot)) {
                                        if (availableStockQtyBase.compareTo(processQtyBase) >= 0) {
                                            allotQtyBase = processQtyBase;
                                            availableStockQtyBase = availableStockQtyBase.subtract(processQtyBase);
                                        }
                                    }
                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }

                                if (availableStockQtyBase.compareTo(sydpqtyFloat) > 0) {
                                    for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                        if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                            continue;
                                        }
                                        String ismustallot = singleItem.getIsMustAllot();
                                        if ("Y".equals(ismustallot)) {
                                            continue;
                                        }

                                        BigDecimal processQtyBase = singleItem.getProcessQtyBase();//待配量
                                        BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量

                                        allotQtyBase = processQtyBase.multiply(new BigDecimal("1").add(singleItem.getFloatScale())).setScale(0, BigDecimal.ROUND_HALF_DOWN);//向下取整
                                        availableStockQtyBase = availableStockQtyBase.subtract(allotQtyBase);

                                        singleItem.setAllotQtyBase(allotQtyBase);
                                    }
                                } else {
                                    for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                        if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                            continue;
                                        }
                                        String ismustallot = singleItem.getIsMustAllot();
                                        if ("Y".equals(ismustallot)) {
                                            continue;
                                        }
                                        BigDecimal processQtyBase = singleItem.getProcessQtyBase();//待配量
                                        BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量
                                        if(bpqty.compareTo(BigDecimal.ZERO)>0){
                                            allotQtyBase = availableStockQtyBase.multiply(processQtyBase).divide(bpqty, 0, BigDecimal.ROUND_HALF_UP);//向上取整
                                        }else{
                                            allotQtyBase = processQtyBase;
                                        }
                                        singleItem.setAllotQtyBase(allotQtyBase);
                                    }
                                }

                            } else {
                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();
                                    BigDecimal processQtyBase = singleItem.getProcessQtyBase();
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;

                                    if ("Y".equals(ismustallot)) {
                                        if (availableStockQtyBase.compareTo(processQtyBase) >= 0) {
                                            allotQtyBase = processQtyBase;
                                            availableStockQtyBase = availableStockQtyBase.subtract(processQtyBase);
                                        }
                                    }


                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }
                                //剩余待配的不一定够了  直接分配拉到
                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();
                                    if ("Y".equals(ismustallot)) {
                                        continue;
                                    }
                                    BigDecimal processQtyBase = singleItem.getProcessQtyBase();//待配量
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量
                                    if (availableStockQtyBase.compareTo(processQtyBase) >= 0) {
                                        allotQtyBase = processQtyBase;
                                    } else {
                                        allotQtyBase = availableStockQtyBase;
                                    }
                                    availableStockQtyBase = availableStockQtyBase.subtract(allotQtyBase);
                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }
                            }
                        }

                        //3.按待配量分配
                        if ("3".equals(thisAllotType)) {
                            for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                    continue;
                                }
                                singleItem.setAllotQtyBase(singleItem.getProcessQtyBase());
                            }
                        }
                    }
                    else{
                        //循环分配
                            if (availableStockQtyBase.compareTo(dpqty) > 0) {
                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();

                                    BigDecimal processQtyBase = singleItem.getProcessQtyBase();
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;
                                    BigDecimal diffQtyBase = BigDecimal.ZERO;
                                    if ("Y".equals(ismustallot)) {
                                        if (availableStockQtyBase.compareTo(processQtyBase) >= 0) {
                                            allotQtyBase = processQtyBase;
                                            availableStockQtyBase = availableStockQtyBase.subtract(processQtyBase);
                                        }
                                    }
                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }

                                //剩余的大于剩余待配*浮动比例
                                if (availableStockQtyBase.compareTo(sydpqtyFloat) > 0) {

                                    for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                        if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                            continue;
                                        }
                                        String ismustallot = singleItem.getIsMustAllot();

                                        if ("Y".equals(ismustallot)) {
                                            continue;
                                        }


                                        BigDecimal processQtyBase = singleItem.getProcessQtyBase();//待配量
                                        BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量

                                        allotQtyBase = processQtyBase.multiply(new BigDecimal("1").add(singleItem.getFloatScale())).setScale(0, BigDecimal.ROUND_HALF_DOWN);//向下取整
                                        availableStockQtyBase = availableStockQtyBase.subtract(allotQtyBase);
                                        singleItem.setAllotQtyBase(allotQtyBase);
                                    }
                                } else {

                                    BigDecimal sydpCount = new BigDecimal(filterRows.size()).subtract(new BigDecimal(bpList.size()));
                                    if (sydpCount.compareTo(BigDecimal.ZERO) > 0) {
                                        //减去剩余待配的 再平均
                                        BigDecimal pjQtyBase = availableStockQtyBase.subtract(sydpqty).divide(sydpCount, 0, RoundingMode.HALF_DOWN);
                                        BigDecimal pjQty2Base = availableStockQtyBase.subtract(pjQtyBase.multiply(sydpCount));//减去平均的还剩的
                                        for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {
                                            if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                                continue;
                                            }
                                            String ismustallot = singleItem.getIsMustAllot();

                                            if ("Y".equals(ismustallot)) {
                                                continue;
                                            }


                                            BigDecimal processQtyBase = singleItem.getProcessQtyBase();//待配量
                                            BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量
                                            BigDecimal diffQtyBase = BigDecimal.ZERO;

                                            allotQtyBase = allotQtyBase.add(pjQtyBase);
                                            if (pjQty2Base.compareTo(BigDecimal.ZERO) > 0) {
                                                allotQtyBase = allotQtyBase.add(new BigDecimal(1));
                                                pjQty2Base = pjQty2Base.subtract(new BigDecimal(1));
                                            }


                                            availableStockQtyBase = availableStockQtyBase.subtract(allotQtyBase);
                                            singleItem.setAllotQtyBase(allotQtyBase);
                                        }
                                    }

                                }

                            } else {

                                for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : filterRows) {
                                    if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                        continue;
                                    }
                                    String ismustallot = singleItem.getIsMustAllot();

                                    BigDecimal processQtyBase = singleItem.getProcessQtyBase();
                                    BigDecimal allotQtyBase = BigDecimal.ZERO;

                                    if ("Y".equals(ismustallot)) {
                                        if (availableStockQtyBase.compareTo(processQtyBase) >= 0) {
                                            allotQtyBase = processQtyBase;
                                            availableStockQtyBase = availableStockQtyBase.subtract(processQtyBase);
                                        }
                                    }
                                    singleItem.setAllotQtyBase(allotQtyBase);
                                }

                                //库存量比待配总量小 循环分配剩余待配的
                                BigDecimal sydpCount = new BigDecimal(filterRows.size()).subtract(new BigDecimal(bpList.size()));
                                if (sydpCount.compareTo(BigDecimal.ZERO) > 0) {
                                    BigDecimal pjQtyBase = availableStockQtyBase.divide(sydpCount, 0, RoundingMode.HALF_DOWN);
                                    BigDecimal pjQty2Base = availableStockQtyBase.subtract(pjQtyBase.multiply(sydpCount));//减去平均的还剩的

                                    for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : filterRows) {
                                        if (!(singleItem.getPluNo().equals(pluNo) && singleItem.getFeatureNo().equals(featureNo) && singleItem.getWareHouse().equals(warehouse))) {
                                            continue;
                                        }
                                        String ismustallot = singleItem.getIsMustAllot();

                                        if ("Y".equals(ismustallot)) {
                                            continue;
                                        }


                                        BigDecimal allotQtyBase = BigDecimal.ZERO;//本次分配量

                                        allotQtyBase = allotQtyBase.add(pjQtyBase);
                                        if (pjQty2Base.compareTo(BigDecimal.ZERO) > 0) {
                                            allotQtyBase = allotQtyBase.add(new BigDecimal(1));
                                            pjQty2Base = pjQty2Base.subtract(new BigDecimal(1));
                                        }


                                        availableStockQtyBase = availableStockQtyBase.subtract(allotQtyBase);
                                        singleItem.setAllotQtyBase(allotQtyBase);
                                    }

                                }
                            }


                    }


                }

            }

            //基准单位数量转成需求单位数量

            List<Map<String, Object>> plus = new ArrayList<Map<String, Object>>();

            List<DCP_DemandPreAllotCalculateRes.DemandDetail> collect = demandList.stream()
                    .filter(PosPub.distinctByKeys(p -> p.getPluNo(), p -> p.getPUnit(), p -> p.getBaseUnit(), p -> p.getUnitRatio())).collect(Collectors.toList());

            for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : collect){

                Map<String, Object> plu = new HashMap<String, Object>();
                plu.put("PLUNO", singleItem.getPluNo());
                plu.put("PUNIT", singleItem.getPUnit());
                plu.put("BASEUNIT", singleItem.getBaseUnit());
                plu.put("UNITRATIO", singleItem.getUnitRatio());
                plus.add(plu);
            }

            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, req.geteId(), req.getBELFIRM(), req.getOrganizationNO(), plus,req.getBELFIRM());


            for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : demandList) {

                BigDecimal allotQtyBase = singleItem.getAllotQtyBase();
                String unitRatio = singleItem.getUnitRatio();
                String udLegth = singleItem.getUdLegth();
                BigDecimal allotQty = allotQtyBase.divide(new BigDecimal(unitRatio),6).setScale(Integer.parseInt(udLegth), RoundingMode.DOWN);
                singleItem.setAllotQty(allotQty);
                singleItem.setDiffQty(singleItem.getAllotQty().subtract(singleItem.getProcessQty()));

                String price="0";
                String distriPrice="0";
                Map<String, Object> condiV = new HashMap<String, Object>();
                condiV.put("PLUNO", singleItem.getPluNo());
                condiV.put("PUNIT", singleItem.getPUnit());
                List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                if (priceList != null && priceList.size() > 0) {
                    price = priceList.get(0).get("PRICE").toString();
                    distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                }
                singleItem.setRetailPrice(price);
                singleItem.setDistriPrice(distriPrice);

            }

            if("1".equals(queryType)) {//商品视角
                if (getQData != null && getQData.isEmpty() == false) {
                    int item=0;
                    for (Map<String, Object> row : getQData){
                        item++;
                        DCP_DemandPreAllotCalculateRes.PluList pluList = res.new PluList();
                        //String item = row.get("ITEM").toString();
                        String pluNo = row.get("PLUNO").toString();
                        String pluBarCode = row.get("PLUBARCODE").toString();
                        String pluName = row.get("PLUNAME").toString();
                        String spec = row.get("SPEC").toString();
                        String featureNo = row.get("FEATURENO").toString();
                        String featureName = row.get("FEATURENAME").toString();
                        String pUnit = row.get("PUNIT").toString();
                        String pUnitName = row.get("PUNITNAME").toString();
                        String warehouse = row.get("WAREHOUSE").toString();
                        String warehousename = row.get("WAREHOUSENAME").toString();
                        String udlength = row.get("UDLENGTH").toString();
                        String unit_ratio = row.get("UNITRATIO").toString();
                        BigDecimal totPqty=BigDecimal.ZERO;
                        BigDecimal totStockOutNoQty=BigDecimal.ZERO;
                        BigDecimal totProcessQty=BigDecimal.ZERO;
                        BigDecimal totAllotQty=BigDecimal.ZERO;
                        BigDecimal totDiffQty=BigDecimal.ZERO;
                        BigDecimal availableStockQty=BigDecimal.ZERO;//可用库存量
                        BigDecimal availableStockQtyBase=BigDecimal.ZERO;

                        pluList.setObjectList(new ArrayList<>());
                        //找库存  找punit和baseunit 比例
                        List<Map<String, Object>> pluStock = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo) && x.get("FEATURENO").toString().equals(featureNo)&&x.get("WAREHOUSE").toString().equals(warehouse)).distinct().collect(Collectors.toList());
                        if(pluStock.size()>0){
                            availableStockQtyBase=new BigDecimal( pluStock.get(0).get("BASEQTY").toString());
                        }
                        availableStockQty = availableStockQtyBase.divide(new BigDecimal(unit_ratio), Integer.parseInt(udlength), RoundingMode.HALF_UP).setScale(Integer.parseInt(udlength), RoundingMode.HALF_UP);

                        //明细
                        List<DCP_DemandPreAllotCalculateRes.DemandDetail> filterRows = demandList.stream().filter(x ->
                                x.getPluNo().equals(pluNo) &&
                                x.getFeatureNo().equals(featureNo) &&
                                x.getPUnit().equals(pUnit) &&
                                x.getWareHouse().equals(warehouse)).collect(Collectors.toList());

                        int detailItem=0;
                        for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : filterRows){
                            DCP_DemandPreAllotCalculateRes.ObjectListDetail oDetail = res.new ObjectListDetail();

                            detailItem++;
                            String objectType = singleItem.getObjectType();
                            String objectId = singleItem.getObjectId();
                            String objectName = singleItem.getObjectName();
                            String orderNo = singleItem.getOrderNo();
                            String orderItem = singleItem.getOrderItem();
                            String orderType = singleItem.getOrderType();
                            String templateNo = singleItem.getTemplateNo();
                            String templateName = singleItem.getTemplateName();
                            String isUrgent = singleItem.getIsUrgent();
                            String isAdd = singleItem.getIsAdd();
                            String ismustallot = singleItem.getIsMustAllot();
                            String sortid = singleItem.getSortId();
                            String submitdatetime = singleItem.getSubmitDateTime();
                            String bdate = singleItem.getBDate();
                            String rdate = singleItem.getRDate();
                            BigDecimal stockOutNoQty = singleItem.getStockOutNoQty();
                            BigDecimal processQty = singleItem.getProcessQty();
                            BigDecimal pQty = singleItem.getPQty();
                            BigDecimal allotQty = singleItem.getAllotQty();
                            BigDecimal diffQty = singleItem.getDiffQty();
                            totPqty=totPqty.add(pQty);
                            totStockOutNoQty=totStockOutNoQty.add(stockOutNoQty);
                            totProcessQty=totProcessQty.add(processQty);
                            totAllotQty=totAllotQty.add(allotQty);
                            totDiffQty=totDiffQty.add(diffQty);

                            oDetail.setOrderItem(singleItem.getOrderItem());
                            oDetail.setItem(String.valueOf(detailItem));
                            oDetail.setObjectType(objectType);
                            oDetail.setObjectId(objectId);
                            oDetail.setObjectName(objectName);
                            oDetail.setOrderNo(orderNo);
                            oDetail.setOrderItem(orderItem);
                            oDetail.setOrderType(orderType);
                            oDetail.setTemplateNo(templateNo);
                            oDetail.setTemplateName(templateName);
                            oDetail.setIsUrgent(isUrgent);
                            oDetail.setIsAdd(isAdd);
                            oDetail.setPQty(Double.parseDouble(pQty.toString()));
                            oDetail.setIsMustAllot(ismustallot);
                            oDetail.setSortId(sortid);
                            oDetail.setSubmitDateTime(submitdatetime);
                            oDetail.setBDate(bdate);
                            oDetail.setRDate(rdate);
                            oDetail.setStockOutNoQty(Double.parseDouble(stockOutNoQty.toString()));
                            oDetail.setProcessQty(Double.parseDouble(processQty.toString()));
                            oDetail.setAllotQty(Double.parseDouble(allotQty.toString()));
                            oDetail.setDiffQty(Double.parseDouble(diffQty.toString()));
                            oDetail.setDistriPrice(singleItem.getDistriPrice());
                            oDetail.setRetailPrice(singleItem.getRetailPrice());

                            pluList.getObjectList().add(oDetail);
                        }

                        pluList.setItem(item+"");
                        pluList.setPluNo(pluNo);
                        pluList.setPluBarCode(pluBarCode);
                        pluList.setPluName(pluName);
                        pluList.setSpec(spec);
                        pluList.setFeatureNo(featureNo);
                        pluList.setFeatureName(featureName);
                        pluList.setPUnit(pUnit);
                        pluList.setPUnitName(pUnitName);
                        pluList.setWareHouse(warehouse);
                        pluList.setWareHouseName(warehousename);
                        pluList.setTotPqty(Double.parseDouble(totPqty.toString()));
                        pluList.setTotStockOutNoQty(Double.parseDouble(totStockOutNoQty.toString()));
                        pluList.setTotProcessQty(Double.parseDouble(totProcessQty.toString()));
                        pluList.setTotAllotQty(Double.parseDouble(totAllotQty.toString()));
                        pluList.setTotDiffQty(Double.parseDouble(totDiffQty.toString()));
                        pluList.setAvailableStockQty(Double.parseDouble(availableStockQty.toString()));

                        List<Map<String, Object>> collect1 = pluList.getObjectList().stream().map(z -> {
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("objectType", z.getObjectType());
                            map1.put("objectId", z.getObjectId());
                            return map1;

                        }).distinct().collect(Collectors.toList());
                        pluList.setTotCqty(collect1.size());

                        level1Elm.getPluList().add(pluList);
                    }
                }
            }
            else{

                if (getQData != null && getQData.isEmpty() == false) {
                    int orgItem=0;
                    for (Map<String, Object> row : getQData) {
                        orgItem++;
                        DCP_DemandPreAllotCalculateRes.ObjectList objectList = res.new ObjectList();

                        String objectType = row.get("OBJECTTYPE").toString();
                        String objectId = row.get("OBJECTID").toString();
                        String objectName = row.get("OBJECTNAME").toString();
                        String orderNo = row.get("ORDERNO").toString();
                        String orderType = row.get("ORDERTYPE").toString();
                        String bDate = row.get("BDATE").toString();
                        String rDate = row.get("RDATE").toString();
                        String submitdatetime = row.get("SUBMITDATETIME").toString();
                        String isUrgent = row.get("ISURGENT").toString();
                        String isAdd = row.get("ISADD").toString();
                        String isMustAllot = row.get("ISMUSTALLOT").toString();
                        String sortid = row.get("SORTID").toString();
                        String distriOrgNo = row.get("DISTRIORGNO").toString();
                        String distriOrgName = row.get("DISTRIORGNAME").toString();
                        String pTemplateNo = row.get("PTEMPLATENO").toString();
                        String pTemplateName = row.get("PTEMPLATENAME").toString();

                        BigDecimal totPqty=BigDecimal.ZERO;
                        BigDecimal totStockOutNoQty=BigDecimal.ZERO;
                        BigDecimal totProcessQty=BigDecimal.ZERO;
                        BigDecimal totAllotQty=BigDecimal.ZERO;
                        BigDecimal totDiffQty=BigDecimal.ZERO;
                        objectList.setPluList(new ArrayList<>());

                        String orderno = row.get("ORDERNO").toString();
                        List<DCP_DemandPreAllotCalculateRes.DemandDetail> pluNoList
                                = demandList.stream().filter(x -> x.getOrderNo().equals(orderno)).collect(Collectors.toList());

                        int item=0;
                        for (DCP_DemandPreAllotCalculateRes.DemandDetail singleItem : pluNoList){
                            DCP_DemandPreAllotCalculateRes.PluListDetail pluListDetail = res.new PluListDetail();
                            item++;
                            //String item = singleItem.getItem();
                            String pluNo = singleItem.getPluNo();
                            String pluName = singleItem.getPluName();
                            String pluBarCode = singleItem.getPluBarCode();
                            String spec = singleItem.getSpec();
                            String featureNo = singleItem.getFeatureNo();
                            String featureName = singleItem.getFeatureName();
                            String pUnit = singleItem.getPUnit();
                            String pUnitName = singleItem.getPUnitName();
                            BigDecimal pQty = singleItem.getPQty();
                            BigDecimal stockOutNoQty = singleItem.getStockOutNoQty();
                            BigDecimal processQty = singleItem.getProcessQty();
                            BigDecimal allotQty = singleItem.getAllotQty();
                            BigDecimal diffQty = singleItem.getDiffQty();
                            String wareHouse = singleItem.getWareHouse();
                            String wareHouseName = singleItem.getWareHouseName();
                            String distriPrice = singleItem.getDistriPrice();
                            String retailPrice = singleItem.getRetailPrice();
                            String unitRatio = singleItem.getUnitRatio();
                            String udLegth = singleItem.getUdLegth();

                            BigDecimal availableStockQty=BigDecimal.ZERO;//可用库存量
                            BigDecimal availableStockQtyBase=BigDecimal.ZERO;

                            //找库存  找punit和baseunit 比例
                            List<Map<String, Object>> pluStock = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo) && x.get("FEATURENO").toString().equals(featureNo)&&x.get("WAREHOUSE").toString().equals(wareHouse)).distinct().collect(Collectors.toList());
                            if(pluStock.size()>0){
                                availableStockQtyBase=new BigDecimal( pluStock.get(0).get("BASEQTY").toString());
                            }
                            availableStockQty = availableStockQtyBase.divide(new BigDecimal(unitRatio), Integer.parseInt(udLegth), RoundingMode.HALF_UP).setScale(Integer.parseInt(udLegth), RoundingMode.HALF_UP);


                            totPqty=totPqty.add(pQty);
                            totStockOutNoQty=totStockOutNoQty.add(stockOutNoQty);
                            totProcessQty=totProcessQty.add(processQty);
                            totAllotQty=totAllotQty.add(allotQty);
                            totDiffQty=totDiffQty.add(diffQty);

                            pluListDetail.setItem(item+"");
                            pluListDetail.setPluNo(pluNo);
                            pluListDetail.setPluName(pluName);
                            pluListDetail.setPluBarCode(pluBarCode);
                            pluListDetail.setSpec(spec);
                            pluListDetail.setFeatureNo(featureNo);
                            pluListDetail.setFeatureName(featureName);
                            pluListDetail.setPUnit(pUnit);
                            pluListDetail.setPUnitName(pUnitName);
                            pluListDetail.setPQty(Double.parseDouble(pQty.toString()));
                            pluListDetail.setStockOutNoQty(Double.parseDouble(stockOutNoQty.toString()));
                            pluListDetail.setProcessQty(Double.parseDouble(processQty.toString()));
                            pluListDetail.setAllotQty(Double.parseDouble(allotQty.toString()));
                            pluListDetail.setDiffQty(Double.parseDouble(diffQty.toString()));
                            pluListDetail.setWareHouse(wareHouse);
                            pluListDetail.setWareHouseName(wareHouseName);
                            pluListDetail.setDistriPrice(distriPrice);
                            pluListDetail.setRetailPrice(retailPrice);
                            pluListDetail.setOrderItem(singleItem.getOrderItem());
                            pluListDetail.setAvailableStockQty(Double.parseDouble(availableStockQty.toString()));


                            objectList.getPluList().add(pluListDetail);

                        }
                        objectList.setItem(orgItem+"");
                        objectList.setObjectId(objectId);
                        objectList.setObjectName(objectName);
                        objectList.setObjectType(objectType);
                        objectList.setOrderType(orderType);
                        objectList.setOrderNo(orderNo);
                        objectList.setBDate(bDate);
                        objectList.setRDate(rDate);
                        objectList.setSubmitDateTime(submitdatetime);
                        objectList.setIsUrgent(isUrgent);
                        objectList.setIsAdd(isAdd);
                        objectList.setIsMustAllot(isMustAllot);
                        objectList.setSortId(sortid);
                        objectList.setDistriOrgNo(distriOrgNo);
                        objectList.setDistriOrgName(distriOrgName);
                        objectList.setTotPqty(Double.parseDouble(totPqty.toString()));
                        objectList.setTotProcessQty(Double.parseDouble(totProcessQty.toString()));
                        objectList.setTotStockOutNoQty(Double.parseDouble(totStockOutNoQty.toString()));
                        objectList.setTotAllotQty(Double.parseDouble(totAllotQty.toString()));
                        objectList.setTotDiffQty(Double.parseDouble(totDiffQty.toString()));
                        objectList.setTemplateNo(pTemplateNo);
                        objectList.setTemplateName(pTemplateName);

                        List<Map<String, Object>> collect1 = objectList.getPluList().stream().map(z -> {
                            Map<String, Object> pluListDetail = new HashMap<>();
                            pluListDetail.put("featureNo", z.getFeatureNo());
                            pluListDetail.put("pluNo", z.getPluNo());
                            return pluListDetail;
                        }).distinct().collect(Collectors.toList());
                        objectList.setTotCqty(collect1.size());

                        level1Elm.getObjectList().add(objectList);
                    }
                }
            }

        }

        res.setDatas(level1Elm);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }





    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_DemandPreAllotCalculateReq req) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String eId = req.geteId();
        DCP_DemandPreAllotCalculateReq.levelRequest request = req.getRequest();
        String dateType = request.getDateType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String orderType = request.getOrderType();
        String includeTemplate = request.getIncludeTemplate();
        String distriOrgNo = request.getDistriOrgNo();
        String templateNo = request.getTemplateNo();
        String keyTxt = request.getKeyTxt();
        //1.按商品 2.按对象


        StringBuilder builder = new StringBuilder();
        builder.append("select * from (select count(*) over () num,rownum rn,a.* from (" +
                " select distinct   a.pluno,a.PLUBARCODE,a.baseunit,a.pluname,a.spec,a.featureno,a.FEATURENAME,a.punit,a.punitname,a.warehouse, " +
                "a.warehousename, a.UDLENGTH,a.UNITRATIO from ( ");
        builder.append("select a.item, a.pluno,a.PLUBARCODE,e.baseunit,d.plu_name as pluname,e.spec,a.featureno,f.FEATURENAME,a.punit,g.uname as punitname,h.warehouse,h.warehouse_name as warehousename," +
                " nvl(i.UDLENGTH,'0') as UDLENGTH,j.UNITRATIO,case when l.status='0' then nvl(k.pqty,0) else 0 end as noticeqty,(a.pqty-nvl(a.STOCKOUTNOQTY,0)) as lastqty " +
                " from DCP_DEMAND a " +
                " left join dcp_org_lang b on a.OBJECTID=b.organizationno and a.eid=b.eid and b.lang_type='"+req.getLangType()+"' and a.OBJECTTYPE='1' " +
                " left join DCP_BIZPARTNER c on c.eid=a.eid and c.organizationno=a.organizationno and a.objectid=c.BIZPARTNERNO and a.OBJECTTYPE='2' and c.biztype in ('2','3') " +
                " left join dcp_goods_lang d on a.eid=d.eid and a.pluno=d.pluno and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods e on a.eid=e.eid and a.pluno=e.pluno " +
                " left join DCP_GOODS_FEATURE_LANG f on f.eid=a.eid and f.pluno=a.pluno and f.featureno=a.featureno and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=a.punit and g.lang_type='"+req.getLangType()+"' " +
                " left join dcp_warehouse_lang h on h.eid=a.eid and h.warehouse=a.DELIVERYWAREHOUSE AND a.DELIVERYORGNO=h.ORGANIZATIONNO and h.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit i on i.eid=a.eid and i.unit=a.punit  " +
                " left join dcp_goods_unit j on j.eid=a.eid and j.ounit=a.punit and j.pluno=a.pluno and j.unit=a.baseunit  " +
                " LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL k on k.eid=a.eid and k.sourcebillno=a.orderno and k.oitem=a.item " +
                " left join dcp_stockoutnotice l on l.eid=k.eid and l.organizationno=k.organizationno and l.billno=k.billno " +
                " where a.eid='"+req.geteId()+"' " +//and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"'
                " and a.CLOSESTATUS='0' and a.pqty>nvl(a.STOCKOUTNOQTY,0) " +
                " and a.SUPPLIERTYPE='FACTORY' ");

        if(!Check.Null(orderType)){
            builder.append(" and a.ORDERTYPE='"+orderType+"'");
        }
        if(!Check.Null(distriOrgNo)){
            builder.append(" and a.DELIVERYORGNO='"+distriOrgNo+"'");
        }
        if(!Check.Null(templateNo)){
            builder.append(" and a.TEMPLATENO='"+templateNo+"'");
        }
        if(!Check.Null(dateType)){
            if("bDate".equals(dateType)){
                if(!Check.Null(beginDate)){
                    builder.append(" and a.bdate>='"+beginDate+"'");
                }
                if(!Check.Null(endDate)){
                    builder.append(" and a.bdate<='"+endDate+"'");

                }

            }else if("rDate".equals(dateType)){
                if(!Check.Null(beginDate)){
                    builder.append(" and a.rdate>='"+beginDate+"'");
                }
                if(!Check.Null(endDate)){
                    builder.append(" and a.rdate<='"+endDate+"'");

                }
            }
        }

        if("Y".equals(includeTemplate)){
            builder.append(" and a.TEMPLATENO is not null   ");
        }else{
            builder.append(" and (a.TEMPLATENO is null or a.TEMPLATENO ='')  ");
        }

        if(!Check.Null(keyTxt)){
            builder.append(" and (a.objectid like '%%"+keyTxt+"%%' " +
                    " or b.org_name like '%%"+keyTxt+"%%' " +
                    " or c.sname like '%%"+keyTxt+"%%' " +
                    " or c.fname like '%%"+keyTxt+"%%' " +
                    " or a.ORDERNO like '%%"+keyTxt+"%%' ) ");
        }


        builder.append(" order by a.item asc) a where a.lastqty>a.noticeqty )  a )  "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + "  ");

        return builder.toString();
    }

    protected String getQuerySqlForItem(DCP_DemandPreAllotCalculateReq req,String with) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String eId = req.geteId();
        DCP_DemandPreAllotCalculateReq.levelRequest request = req.getRequest();
        String dateType = request.getDateType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String orderType = request.getOrderType();
        String includeTemplate = request.getIncludeTemplate();
        String distriOrgNo = request.getDistriOrgNo();
        String templateNo = request.getTemplateNo();
        String keyTxt = request.getKeyTxt();
        //1.按商品 2.按对象
        StringBuilder builder = new StringBuilder();
        builder.append(" with p as ("+with+") " +
                "select * from (select count(*) over () num,rownum rn,a.* from (" +
                " select distinct   a.pluno,a.PLUBARCODE,a.baseunit,a.pluname,a.spec,a.featureno,a.FEATURENAME,a.punit,a.punitname,a.warehouse, " +
                "a.warehousename, a.UDLENGTH,a.UNITRATIO from ( ");
        builder.append("select a.item, a.pluno,a.PLUBARCODE,e.baseunit,d.plu_name as pluname,e.spec,a.featureno,f.FEATURENAME,a.punit,g.uname as punitname,h.warehouse,h.warehouse_name as warehousename," +
                " nvl(i.UDLENGTH,'0') as UDLENGTH,j.UNITRATIO,(a.pqty-nvl(a.STOCKOUTNOQTY,0)) as lastqty " +
                " from DCP_DEMAND a " +
                " inner join p on p.orderno=a.orderno and p.item=a.item " +
                " left join dcp_org_lang b on a.OBJECTID=b.organizationno and a.eid=b.eid and b.lang_type='"+req.getLangType()+"' and a.OBJECTTYPE='1' " +
                " left join DCP_BIZPARTNER c on c.eid=a.eid and c.organizationno=a.organizationno and a.objectid=c.BIZPARTNERNO and a.OBJECTTYPE='2' and c.biztype in ('2','3') " +
                " left join dcp_goods_lang d on a.eid=d.eid and a.pluno=d.pluno and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods e on a.eid=e.eid and a.pluno=e.pluno " +
                " left join DCP_GOODS_FEATURE_LANG f on f.eid=a.eid and f.pluno=a.pluno and f.featureno=a.featureno and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=a.punit and g.lang_type='"+req.getLangType()+"' " +
                " left join dcp_warehouse_lang h on h.eid=a.eid and h.warehouse=a.DELIVERYWAREHOUSE AND a.DELIVERYORGNO=h.ORGANIZATIONNO and h.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit i on i.eid=a.eid and i.unit=a.punit  " +
                " left join dcp_goods_unit j on j.eid=a.eid and j.ounit=a.punit and j.pluno=a.pluno and j.unit=a.baseunit  " +
                " where a.eid='"+req.geteId()+"' " +//and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"'
                " and a.CLOSESTATUS='0' and a.pqty>nvl(a.STOCKOUTNOQTY,0) " +
                " and a.SUPPLIERTYPE='FACTORY' ");

        if(!Check.Null(orderType)){
            builder.append(" and a.ORDERTYPE='"+orderType+"'");
        }
        if(!Check.Null(distriOrgNo)){
            builder.append(" and a.DELIVERYORGNO='"+distriOrgNo+"'");
        }
        if(!Check.Null(templateNo)){
            builder.append(" and a.TEMPLATENO='"+templateNo+"'");
        }
        if(!Check.Null(dateType)){
            if("bDate".equals(dateType)){
                if(!Check.Null(beginDate)){
                    builder.append(" and a.bdate>='"+beginDate+"'");
                }
                if(!Check.Null(endDate)){
                    builder.append(" and a.bdate<='"+endDate+"'");

                }

            }else if("rDate".equals(dateType)){
                if(!Check.Null(beginDate)){
                    builder.append(" and a.rdate>='"+beginDate+"'");
                }
                if(!Check.Null(endDate)){
                    builder.append(" and a.rdate<='"+endDate+"'");

                }
            }
        }

        if("Y".equals(includeTemplate)){
            builder.append(" and a.TEMPLATENO is not null   ");
        }else{
            builder.append(" and (a.TEMPLATENO is null or a.TEMPLATENO ='')  ");
        }

        if(!Check.Null(keyTxt)){
            builder.append(" and (a.objectid like '%%"+keyTxt+"%%' " +
                    " or b.org_name like '%%"+keyTxt+"%%' " +
                    " or c.sname like '%%"+keyTxt+"%%' " +
                    " or c.fname like '%%"+keyTxt+"%%' " +
                    " or a.ORDERNO like '%%"+keyTxt+"%%' ) ");
        }


        builder.append(" order by a.item asc) a )  a )  "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + "  ");

        return builder.toString();
    }

    private String getNoticeDetailSql(DCP_DemandPreAllotCalculateReq req) throws Exception {
        StringBuilder builder = new StringBuilder();

        builder.append("select distinct orderno,item from (" +
                " select orderno,item,sum(noticeqty) as noticeqty,lastqty from (" +
                " select a.orderno,a.item," +
                " case when l.status='0' then nvl(k.pqty,0) else 0 end as noticeqty,(a.pqty-nvl(a.STOCKOUTNOQTY,0)) as lastqty " +
                " from dcp_demand a" +
                " LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL k on k.eid=a.eid and k.sourcebillno=a.orderno and k.oitem=a.item " +
                " left join dcp_stockoutnotice l on l.eid=k.eid and l.organizationno=k.organizationno and l.billno=k.billno " +
                " where a.eid='"+req.geteId()+"' " +
                " and a.CLOSESTATUS='0' and a.pqty>nvl(a.STOCKOUTNOQTY,0) " +
                " and a.SUPPLIERTYPE='FACTORY' ");

        if(!Check.Null(req.getRequest().getOrderType())){
            builder.append(" and a.ORDERTYPE='"+req.getRequest().getOrderType()+"'");
        }
        if(!Check.Null(req.getRequest().getDistriOrgNo())){
            builder.append(" and a.DELIVERYORGNO='"+req.getRequest().getDistriOrgNo()+"'");
        }
        if(!Check.Null(req.getRequest().getTemplateNo())){
            builder.append(" and a.TEMPLATENO='"+req.getRequest().getTemplateNo()+"'");
        }
        if(!Check.Null(req.getRequest().getDateType())){
            if("bDate".equals(req.getRequest().getDateType())){
                if(!Check.Null(req.getRequest().getBeginDate())){
                    builder.append(" and a.bdate>='"+req.getRequest().getBeginDate()+"'");
                }
                if(!Check.Null(req.getRequest().getEndDate())){
                    builder.append(" and a.bdate<='"+req.getRequest().getEndDate()+"'");

                }

            }else if("rDate".equals(req.getRequest().getDateType())){
                if(!Check.Null(req.getRequest().getBeginDate())){
                    builder.append(" and a.rdate>='"+req.getRequest().getBeginDate()+"'");
                }
                if(!Check.Null(req.getRequest().getEndDate())){
                    builder.append(" and a.rdate<='"+req.getRequest().getEndDate()+"'");

                }
            }
        }

        if("Y".equals(req.getRequest().getIncludeTemplate())){
            builder.append(" and a.TEMPLATENO is not null   ");
        }else{
            builder.append(" and (a.TEMPLATENO is null or a.TEMPLATENO ='')  ");
        }

        if(!Check.Null(req.getRequest().getKeyTxt())){
            builder.append(" and (a.objectid like '%%"+req.getRequest().getKeyTxt()+"%%' " +
                    " or b.org_name like '%%"+req.getRequest().getKeyTxt()+"%%' " +
                    " or c.sname like '%%"+req.getRequest().getKeyTxt()+"%%' " +
                    " or c.fname like '%%"+req.getRequest().getKeyTxt()+"%%' " +
                    " or a.ORDERNO like '%%"+req.getRequest().getKeyTxt()+"%%' ) ");
        }
        builder.append(") a group by orderno,item,lastqty");
        builder.append(" ) a where lastqty>noticeqty");

        return builder.toString();
    }

    protected String getQuerySqlForOrg(DCP_DemandPreAllotCalculateReq req,String with) throws Exception {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String eId = req.geteId();
        DCP_DemandPreAllotCalculateReq.levelRequest request = req.getRequest();
        String dateType = request.getDateType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String orderType = request.getOrderType();
        String includeTemplate = request.getIncludeTemplate();
        String distriOrgNo = request.getDistriOrgNo();
        String templateNo = request.getTemplateNo();
        String keyTxt = request.getKeyTxt();
        StringBuilder builder = new StringBuilder();
        builder.append(" with p as ("+with+") " );
        builder.append(" select * from ( select count(*) over () num,rownum rn,a.* from (");
        builder.append("  select distinct a.objectType,a.objectId,a.ordertype,a.objectName,a.orderno,a.bdate,a.rdate,a.submitDateTime,a.isUrgent,a.isAdd,a.isMustAllot,a.sortId,a.distriOrgNo,a.distriOrgName,a.ptemplateno,a.ptemplatename from (" ) ;
        builder.append("select  a.item,a.objectType,a.objectId,a.ordertype," +
                " case when a.OBJECTTYPE='1' then b.org_name else c.sname end as objectName,a.orderno,a.bdate,a.rdate,a.SUBMITTIME as submitDateTime,a.isUrgent,a.isAdd,a.isMustAllot,a.rank as sortId,a.DELIVERYORGNO as distriOrgNo,d.org_name as distriOrgName ," +
                " a.TEMPLATENO as ptemplateno,e.PTEMPLATE_NAME as ptemplatename,(a.pqty-nvl(a.STOCKOUTNOQTY,0)) as lastqty  " +
                " from DCP_DEMAND a" +
                " inner join p on p.orderno=a.orderno and p.item=a.item " +
                " left join dcp_org_lang b on a.OBJECTID=b.organizationno and a.eid=b.eid and b.lang_type='"+req.getLangType()+"' and a.OBJECTTYPE='1' " +
                " left join DCP_BIZPARTNER c on c.eid=a.eid and c.organizationno=a.organizationno and a.objectid=c.BIZPARTNERNO and a.OBJECTTYPE='2' and c.biztype in ('2','3') " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.DELIVERYORGNO and d.lang_type='"+req.getLangType()+"' " +
                " left join DCP_PTEMPLATE e on e.eid=a.eid and e.PTEMPLATENO=a.TEMPLATENO and e.doc_type='0' "+

                //" LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL k on k.eid=a.eid and k.sourcebillno=a.orderno and k.oitem=a.item " +
                //" left join dcp_stockoutnotice l on l.eid=k.eid and l.organizationno=k.organizationno and l.billno=k.billno " +
                " where a.eid='"+eId+"'  " +//and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"'
                " and a.CLOSESTATUS='0' and a.pqty>a.STOCKOUTNOQTY " +
                " and a.SUPPLIERTYPE='FACTORY'  " +
                "");

        if(!Check.Null(orderType)){
            builder.append(" and a.ORDERTYPE='"+orderType+"'");
        }
        if(!Check.Null(distriOrgNo)){
            builder.append(" and a.DELIVERYORGNO='"+distriOrgNo+"'");
        }
        if(!Check.Null(templateNo)){
            builder.append(" and a.TEMPLATENO='"+templateNo+"'");
        }
        if(!Check.Null(dateType)){
            if("bDate".equals(dateType)){
                if(!Check.Null(beginDate)){
                    builder.append(" and a.bdate>='"+beginDate+"'");
                }
                if(!Check.Null(endDate)){
                    builder.append(" and a.bdate<='"+endDate+"'");

                }

            }else if("rDate".equals(dateType)){
                if(!Check.Null(beginDate)){
                    builder.append(" and a.rdate>='"+beginDate+"'");
                }
                if(!Check.Null(endDate)){
                    builder.append(" and a.rdate<='"+endDate+"'");

                }
            }
        }

        if("Y".equals(includeTemplate)){
            builder.append(" and a.TEMPLATENO is not null   ");
        }else{
            builder.append(" and (a.TEMPLATENO is null or a.TEMPLATENO ='')  ");
        }

        if(!Check.Null(keyTxt)){
            builder.append(" and (a.objectid like '%%"+keyTxt+"%%' " +
                    " or b.org_name like '%%"+keyTxt+"%%' " +
                    " or c.sname like '%%"+keyTxt+"%%' " +
                    " or c.fname like '%%"+keyTxt+"%%' " +
                    " or a.ORDERNO like '%%"+keyTxt+"%%' ) ");
        }


        builder.append("  order by a.orderno,a.item ) a  )  a  ) "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY orderno ");

        return builder.toString();
    }
    protected String getMainSql(DCP_DemandPreAllotCalculateReq req,String with) throws Exception {
        String eId = req.geteId();
        DCP_DemandPreAllotCalculateReq.levelRequest request = req.getRequest();
        String dateType = request.getDateType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String orderType = request.getOrderType();
        String includeTemplate = request.getIncludeTemplate();
        String distriOrgNo = request.getDistriOrgNo();
        String templateNo = request.getTemplateNo();
        String keyTxt = request.getKeyTxt();
        //1.按商品 2.按对象


        StringBuilder builder = new StringBuilder();
        builder.append(" with p as ("+with+")" +
                " select a.* from (select a.item,a.pluno,a.plubarcode,a.ordertype,d.plu_name as pluname,e.spec,a.featureno,f.FEATURENAME,a.punit,g.uname as punitname,h.warehouse,h.warehouse_name as warehousename ,a.objectType,a.objectId, " +
                " case when a.OBJECTTYPE='1' then b.org_name else c.sname end as objectName,a.orderno,a.bdate,a.rdate,a.SUBMITTIME as submitDateTime,a.isUrgent,a.isAdd,k.ISMUSTALLOT,k.sortId,a.DELIVERYORGNO as distriOrgNo,i.org_name as distriOrgName," +
                " a.TEMPLATENO,j.PTEMPLATE_NAME as TEMPLATENAME,a.pqty,nvl(j.FLOATSCALE,'0') as FLOATSCALE,e.baseunit,nvl(g1.UDLENGTH,'0') as UDLENGTH,l.UNITRATIO,nvl(a.STOCKOUTNOQTY,0) as STOCKOUTNOQTY ,(a.pqty-nvl(a.STOCKOUTNOQTY,0)) as lastqty    " +
                " from DCP_DEMAND a " +
                " inner join p on a.orderno=p.orderno and a.item=p.item " +
                " left join dcp_org_lang b on a.OBJECTID=b.organizationno and a.eid=b.eid and b.lang_type='"+req.getLangType()+"' and a.OBJECTTYPE='1' " +
                " left join DCP_BIZPARTNER c on c.eid=a.eid and c.organizationno=a.organizationno and a.objectid=c.BIZPARTNERNO and a.OBJECTTYPE='2' and c.biztype in ('2','3') " +
                " left join dcp_goods_lang d on a.eid=d.eid and a.pluno=d.pluno and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods e on a.eid=e.eid and a.pluno=e.pluno " +
                " left join DCP_GOODS_FEATURE_LANG f on f.eid=a.eid and f.pluno=a.pluno and f.featureno=a.featureno and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=a.punit and g.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit g1 on g1.eid=a.eid and g1.unit=a.punit  " +
                " left join dcp_warehouse_lang h on h.eid=a.eid and h.warehouse=a.DELIVERYWAREHOUSE AND a.DELIVERYORGNO=h.ORGANIZATIONNO and h.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang i on i.eid=a.eid and i.organizationno=a.DELIVERYORGNO and i.lang_type='"+req.getLangType()+"' " +
                " left join DCP_PTEMPLATE j on j.eid=a.eid and j.PTEMPLATENO=a.TEMPLATENO and j.doc_type='0'"+
                " left join DCP_PTEMPLATE_SHOP k on k.eid=a.eid and k.PTEMPLATENO=j.PTEMPLATENO and k.shopid=a.OBJECTID " +
                " left join dcp_goods_unit l on l.eid=a.eid and a.pluno=l.pluno and l.ounit=a.punit and l.unit=e.baseunit " +
                //" LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL m on m.eid=a.eid and m.sourcebillno=a.orderno and m.oitem=a.item " +
                //" left join dcp_stockoutnotice n on n.eid=m.eid and n.organizationno=m.organizationno and n.billno=m.billno " +
                " where a.eid='"+eId+"'  " + //and a.ORGANIZATIONNO='"+req.getOrganizationNO()+"'
                " and a.CLOSESTATUS='0' and a.pqty>a.STOCKOUTNOQTY " +
                " and a.SUPPLIERTYPE='FACTORY'  ");

        if(!Check.Null(orderType)){
            builder.append(" and a.ORDERTYPE='"+orderType+"'");
        }
        if(!Check.Null(distriOrgNo)){
            builder.append(" and a.DELIVERYORGNO='"+distriOrgNo+"'");
        }
        if(!Check.Null(templateNo)){
            builder.append(" and a.TEMPLATENO='"+templateNo+"'");
        }
        if(!Check.Null(dateType)){
            if("bDate".equals(dateType)){
                if(!Check.Null(beginDate)){
                    builder.append(" and a.bdate>='"+beginDate+"'");
                }
                if(!Check.Null(endDate)){
                    builder.append(" and a.bdate<='"+endDate+"'");

                }

            }else if("rDate".equals(dateType)){
                if(!Check.Null(beginDate)){
                    builder.append(" and a.rdate>='"+beginDate+"'");
                }
                if(!Check.Null(endDate)){
                    builder.append(" and a.rdate<='"+endDate+"'");

                }
            }
        }

        if("Y".equals(includeTemplate)){
            builder.append(" and a.TEMPLATENO is not null   ");
        }else{
            builder.append(" and (a.TEMPLATENO is null or a.TEMPLATENO ='')  ");
        }

        if(!Check.Null(keyTxt)){
            builder.append(" and (a.objectid like '%%"+keyTxt+"%%' " +
                    " or b.org_name like '%%"+keyTxt+"%%' " +
                    " or c.sname like '%%"+keyTxt+"%%' " +
                    " or c.fname like '%%"+keyTxt+"%%' " +
                    " or a.ORDERNO like '%%"+keyTxt+"%%' ) ");
        }

        builder.append(" order by a.orderno,a.item ");

        builder.append(" ) a   ");


        return builder.toString();
    }

    private void setDemandList(List<Map<String, Object>> singleFilterList,List<DCP_DemandPreAllotCalculateRes.DemandDetail> demandList,DCP_DemandPreAllotCalculateRes res) throws Exception{
        for (Map<String, Object> filterRow : singleFilterList){
            DCP_DemandPreAllotCalculateRes.DemandDetail demandDetail = res.new DemandDetail();

            demandDetail.setOrderItem(filterRow.get("ITEM").toString());
            demandDetail.setObjectType(filterRow.get("OBJECTTYPE").toString());
            demandDetail.setObjectId(filterRow.get("OBJECTID").toString());
            demandDetail.setObjectName(filterRow.get("OBJECTNAME").toString());
            demandDetail.setOrderNo(filterRow.get("ORDERNO").toString());
            demandDetail.setOrderType(filterRow.get("ORDERTYPE").toString());
            demandDetail.setBDate(filterRow.get("BDATE").toString());
            demandDetail.setRDate(filterRow.get("RDATE").toString());
            demandDetail.setSubmitDateTime(filterRow.get("SUBMITDATETIME").toString());
            demandDetail.setIsUrgent(filterRow.get("ISURGENT").toString());
            demandDetail.setIsAdd(filterRow.get("ISADD").toString());
            demandDetail.setIsMustAllot(filterRow.get("ISMUSTALLOT").toString());
            demandDetail.setSortId(filterRow.get("SORTID").toString());
            demandDetail.setDistriOrgNo(filterRow.get("DISTRIORGNO").toString());
            demandDetail.setDistriOrgName(filterRow.get("DISTRIORGNAME").toString());
            demandDetail.setFloatScale(new BigDecimal(filterRow.get("FLOATSCALE").toString()));
            demandDetail.setPluNo(filterRow.get("PLUNO").toString());
            demandDetail.setPluName(filterRow.get("PLUNAME").toString());
            demandDetail.setPluBarCode(filterRow.get("PLUBARCODE").toString());
            demandDetail.setSpec(filterRow.get("SPEC").toString());
            demandDetail.setFeatureNo(filterRow.get("FEATURENO").toString());
            demandDetail.setFeatureName(filterRow.get("FEATURENAME").toString());
            demandDetail.setPUnit(filterRow.get("PUNIT").toString());
            demandDetail.setPUnitName(filterRow.get("PUNITNAME").toString());
            demandDetail.setUdLegth(filterRow.get("UDLENGTH").toString());
            demandDetail.setUnitRatio(filterRow.get("UNITRATIO").toString());
            demandDetail.setPQty(new BigDecimal(filterRow.get("PQTY").toString()));
            demandDetail.setStockOutNoQty(new BigDecimal(filterRow.get("STOCKOUTNOQTY").toString()));
            BigDecimal processQty = demandDetail.getPQty().subtract(demandDetail.getStockOutNoQty());
            demandDetail.setProcessQty(new BigDecimal(processQty.toString()));
            //需求单位数量转换成基准单位数量
            BigDecimal processQtyBase = processQty.multiply(new BigDecimal(demandDetail.getUnitRatio()));
            demandDetail.setProcessQtyBase(processQtyBase);
            demandDetail.setAllotQty(BigDecimal.ZERO);
            demandDetail.setAllotQtyBase(BigDecimal.ZERO);
            demandDetail.setDiffQty(BigDecimal.ZERO);
            demandDetail.setWareHouse(filterRow.get("WAREHOUSE").toString());
            demandDetail.setWareHouseName(filterRow.get("WAREHOUSENAME").toString());
            demandDetail.setTemplateNo(filterRow.get("TEMPLATENO").toString());
            demandDetail.setTemplateName(filterRow.get("TEMPLATENAME").toString());
            demandList.add(demandDetail);
        }

    }

}
