package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProcessTaskPendingMaterialReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskPendingMaterialRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ProcessTaskPendingMaterial extends SPosBasicService<DCP_ProcessTaskPendingMaterialReq, DCP_ProcessTaskPendingMaterialRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProcessTaskPendingMaterialReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessTaskPendingMaterialReq> getRequestType() {
        return new TypeToken<DCP_ProcessTaskPendingMaterialReq>(){};
    }

    @Override
    protected DCP_ProcessTaskPendingMaterialRes getResponseType() {
        return new DCP_ProcessTaskPendingMaterialRes();
    }

    @Override
    protected DCP_ProcessTaskPendingMaterialRes processJson(DCP_ProcessTaskPendingMaterialReq req) throws Exception {
        DCP_ProcessTaskPendingMaterialRes res = this.getResponse();
        DCP_ProcessTaskPendingMaterialRes.level1Elm level1Elm = res.new level1Elm();
        level1Elm.setDataList(new ArrayList<>());
        //根据配方编号和版本号获取配方原料信息，
        //如果semiWoType为1或2，则直接获取当前配方原料返回；
        //如果semiWoType为3，查询当前配方原料中是否存在有配方的，存在配方则继续展开，一直展开到不存在配方为止
        //如A的组成有2B和C，其中B有配方组成是D和E，则A的semiWoType为3是，原料组成为C、2D、2E，既在展开B时，需用计算得到的B用量去计算D和E的用量
        //materialPQty=(pQtyDCP_BOM_MATERIAL.MATERIAL_QTY(1+DCP_BOM_MATERIAL.LOSS_RATE0.01))/(DCP_BOM_MATERIAL.QTYDCP_BOM.BATCHQTY)
        //子件数量根据materialPUnit保留小数位数
        //
        //materialIsBatch=组织库存参数Is_BatchNO为Y且子件商品参数material_isBatch为Y则为Y，否则为N
        //isLocation=配料仓库pWarehouse仓库信息ISLOCATION
        //
        String isBatchNo=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");

        //查询所以配方
        String bomSql="select a.bomno,a.pluno,b.MATERIAL_PLUNO,b.MATERIAL_UNIT,b.MATERIAL_QTY,c1.pickunit,nvl(c1.PICKMINQTY,'0') PICKMINQTY,nvl(c1.PICKMULQTY,'0') PICKMULQTY," +
                " a.versionnum,e.UNITRATIO as mUnitRatio,g.udlength,g.roundtype,d.udlength as mudlength,d.roundtype as mroundtype,f.UNITRATIO as unitratio,h.warehouse as pwarehouse,i.warehouse as kwarehouse,h1.WAREHOUSE_NAME as pwarehousename,i1.WAREHOUSE_NAME as kwarehousename,j.ISLOCATION," +
                " c1.isbatch,l.UNITRATIO as punitratio,k.udlength as pudlength,k.roundtype as proundtype,c2.plu_name as material_pluname,c1.spec,d1.uname as material_unitname,k1.uname as pickunitname,B.ISBUCKLE,c1.baseunit,b.LOSS_RATE,b.qty,a.BATCHQTY " +
                " from dcp_bom a " +
                " left join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno " +
                " inner join dcp_goods c on c.eid=a.eid and c.pluno=a.pluno " +
                " inner join dcp_goods c1 on c1.eid=a.eid and c1.pluno=b.MATERIAL_PLUNO " +
                " left join dcp_goods_lang c2 on c2.eid=a.eid and c2.pluno=b.material_pluno and c2.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit d on a.eid=d.eid and b.MATERIAL_UNIT=d.unit " +
                " left join dcp_unit_lang d1 on d1.eid=a.eid and d1.unit=b.material_unit and d1.lang_type='"+req.getLangType()+"'" +
                " left join dcp_goods_unit e on e.eid=a.eid and e.pluno=b.material_pluno and e.ounit=b.material_unit " +
                " left join dcp_goods_unit f on f.eid=a.eid and f.pluno=a.pluno and f.ounit=a.unit " +
                " left join dcp_unit g on g.eid=a.eid and g.unit=a.unit " +
                " left join MES_WAREHOUSE_GROUP_DETAIL h on a.eid=e.eid and b.PWGROUPNO=h.WGROUPNO and h.ORGANIZATIONNO='"+req.getOrganizationNO()+"' " +
                " left join dcp_warehouse_lang h1 on h1.eid=h.eid and h1.organizationno=h.organizationno and h1.warehouse=h.warehouse and h1.lang_type='"+req.getLangType()+"'" +
                " left join MES_WAREHOUSE_GROUP_DETAIL i on a.eid=f.eid and b.KWGROUPNO=i.WGROUPNO and i.ORGANIZATIONNO='"+req.getOrganizationNO()+"'" +
                " left join dcp_warehouse_lang i1 on i1.eid=i.eid and i1.organizationno=i.organizationno and i1.warehouse=i.warehouse and i1.lang_type='"+req.getLangType()+"'" +
                " left join dcp_warehouse j on j.eid=a.eid and j.warehouse=h.warehouse  " +
                " left join dcp_unit k on k.eid=a.eid and k.unit=c1.pickunit " +
                " left join dcp_unit_lang k1 on k1.eid=a.eid and k1.unit=c1.pickunit and k1.lang_type='"+req.getLangType()+"'" +
                " left join dcp_goods_unit l on l.eid=a.eid and l.pluno=c1.pluno and l.ounit=c1.pickunit " +
                " where a.eid='"+req.geteId()+"' ";

        String bomVSql="select a.bomno,a.pluno,b.MATERIAL_PLUNO,b.MATERIAL_UNIT,b.MATERIAL_QTY,c1.pickunit,nvl(c1.PICKMINQTY,'0') PICKMINQTY,nvl(c1.PICKMULQTY,'0') PICKMULQTY," +
                " a.versionnum,e.unitratio as mUnitRatio,g.udlength,g.roundtype ,d.udlength as mudlength,d.roundType as mroundtype,f.unitratio as unitratio,h.warehouse as pwarehouse,i.warehouse as kwarehouse,h1.WAREHOUSE_NAME as pwarehousename,i1.WAREHOUSE_NAME as kwarehousename,j.ISLOCATION," +
                " c1.isbatch,l.unitratio as punitratio,k.udlength as pudlength,k.roundtype as proundtype,c2.plu_name as material_pluname,c1.spec,d1.uname as material_unitname,k1.uname as pickunitname,B.ISBUCKLE,c1.baseunit,b.LOSS_RATE,b.qty,a.BATCHQTY " +
                " from dcp_bom_v a " +
                " left join dcp_bom_material_v b on a.eid=b.eid and a.bomno=b.bomno " +
                " inner join dcp_goods c on c.eid=a.eid and c.pluno=a.pluno " +
                " inner join dcp_goods c1 on c1.eid=a.eid and c1.pluno=b.MATERIAL_PLUNO " +
                " left join dcp_goods_lang c2 on c2.eid=a.eid and c2.pluno=b.material_pluno and c2.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit d on a.eid=d.eid and b.MATERIAL_UNIT=d.unit " +
                " left join dcp_unit_lang d1 on d1.eid=a.eid and d1.unit=b.material_unit and d1.lang_type='"+req.getLangType()+"'" +
                " left join dcp_goods_unit e on e.eid=a.eid and e.pluno=b.material_pluno and e.ounit=b.material_unit " +
                " left join dcp_goods_unit f on f.eid=a.eid and f.pluno=a.pluno and f.ounit=a.unit " +
                " left join dcp_unit g on g.eid=a.eid and g.unit=a.unit " +
                " left join MES_WAREHOUSE_GROUP_DETAIL h on a.eid=e.eid and b.PWGROUPNO=h.WGROUPNO and h.ORGANIZATIONNO='"+req.getOrganizationNO()+"' " +
                " left join dcp_warehouse_lang h1 on h1.eid=h.eid and h1.organizationno=h.organizationno and h1.warehouse=h.warehouse and h1.lang_type='"+req.getLangType()+"'" +
                " left join MES_WAREHOUSE_GROUP_DETAIL i on a.eid=f.eid and b.KWGROUPNO=i.WGROUPNO and i.ORGANIZATIONNO='"+req.getOrganizationNO()+"'" +
                " left join dcp_warehouse_lang i1 on i1.eid=i.eid and i1.organizationno=i.organizationno and i1.warehouse=i.warehouse and i1.lang_type='"+req.getLangType()+"'" +
                " left join dcp_warehouse j on j.eid=a.eid and j.warehouse=h.warehouse  " +
                " left join dcp_unit k on k.eid=a.eid and k.unit=c1.pickunit " +
                " left join dcp_unit_lang k1 on k1.eid=a.eid and k1.unit=c1.pickunit and k1.lang_type='"+req.getLangType()+"'" +
                " left join dcp_goods_unit l on l.eid=a.eid and l.pluno=c1.pluno and l.ounit=c1.pickunit " +
                " where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> bomList = this.executeQuerySQL_BindSQL(bomSql, null);
        List<Map<String, Object>> bomVList = this.executeQuerySQL_BindSQL(bomVSql, null);

        List<Map<String, Object>> bomListAll = new ArrayList<>();
        List<DCP_ProcessTaskPendingMaterialReq.levelElm2> datas = req.getRequest().getDatas();
        for (DCP_ProcessTaskPendingMaterialReq.levelElm2 data : datas){
            List<Map<String, Object>> bomFilterRows = bomList.stream().filter(x -> x.get("BOMNO").toString().equals(data.getBomNo())
                            && (Check.Null(data.getVersionNum())|| x.get("VERSIONNUM").toString().equals(data.getVersionNum()))
                            &&x.get("PLUNO").toString().equals(data.getPluNo()))
                    .distinct().collect(Collectors.toList());
            Map<String, Object> baseMap = PosPub.getBaseQty(dao, req.geteId(), data.getPluNo(), data.getPunit(), data.getPQty());
            BigDecimal baseQty = new BigDecimal(Check.Null(baseMap.get("baseQty").toString())?"0":baseMap.get("baseQty").toString());

            if(bomFilterRows.size()>0){
                //bomListAll.addAll(bomFilterRows);//先把所有的加进来
                for (Map<String, Object> bomFilterRow : bomFilterRows){
                   // Map<String, Object> newBomFilterRow=new HashMap<>();

                    //算material_pqty
                    String udLength = bomFilterRow.get("UDLENGTH").toString();

                    RoundingMode roundingMode = PosPub.getRoundingMode(bomFilterRow.get("ROUNDTYPE").toString());

                    String mudLength = bomFilterRow.get("MUDLENGTH").toString();
                    RoundingMode mroundingMode = PosPub.getRoundingMode(bomFilterRow.get("MROUNDTYPE").toString());
                    BigDecimal unitRatio = new BigDecimal(bomFilterRow.get("UNITRATIO").toString());
                    BigDecimal munitRatio = new BigDecimal(bomFilterRow.get("MUNITRATIO").toString());
                    BigDecimal punitRatio = new BigDecimal(bomFilterRow.get("PUNITRATIO").toString());
                    BigDecimal materialQty = new BigDecimal(bomFilterRow.get("MATERIAL_QTY").toString());
                    String materialUnit = bomFilterRow.get("MATERIAL_UNIT").toString();
                    String isBatch = bomFilterRow.get("ISBATCH").toString();
                    BigDecimal loss_rate = new BigDecimal(bomFilterRow.get("LOSS_RATE").toString());
                    BigDecimal qty = new BigDecimal(bomFilterRow.get("QTY").toString());
                    BigDecimal batchQty = new BigDecimal(bomFilterRow.get("BATCHQTY").toString());

                    //先换成基准单位  再换成主件单位  再换成子件单位
                    //基准数量/主件单位  * materialQty =子件的pqty
                    //materialPQty=(pQty×DCP_BOM_MATERIAL.MATERIAL_QTY×(1+DCP_BOM_MATERIAL.LOSS_RATE×0.01))/(DCP_BOM_MATERIAL.QTY×DCP_BOM.BATCHQTY)

                    BigDecimal materialPqty = baseQty
                            .multiply(materialQty)
                            .multiply(BigDecimal.ONE.add(loss_rate.multiply(new BigDecimal("0.01"))))
                            .divide(qty.multiply(batchQty),6, RoundingMode.HALF_UP)
                            .divide(unitRatio, Integer.valueOf(udLength), roundingMode);

                    //BigDecimal materialPqty = baseQty.multiply(materialQty).divide(unitRatio, Integer.valueOf(udLength), RoundingMode.HALF_UP);

                    BigDecimal pickPqty = materialPqty.multiply(munitRatio).divide(punitRatio, 6, RoundingMode.HALF_UP);//小数位数给大点

                    Map<String, Object> newRow = new HashMap<>(bomFilterRow);

                    newRow.put("MATERIAL_PQTY", materialPqty.toString());
                    newRow.put("PICK_PQTY", pickPqty.toString());
                    newRow.put("OOTYPE",data.getOOType());
                    newRow.put("OOITEM",data.getOOItem());
                    newRow.put("OOFNO",data.getOOfNo());
                    bomListAll.add(newRow);

                    if(data.getSemiWoType().equals("3")){
                        //子件的pqty + materialUnit 接下来算
                        this.addBomMaterialList(req,bomList,bomListAll,newRow.get("BOMNO").toString(),newRow.get("PLUNO").toString(),materialUnit,materialPqty,data.getOOType(),data.getOOfNo(),data.getOOItem());
                    }
                }
            }
            else{
                List<Map<String, Object>> bomFilterRowsV = bomVList.stream().filter(x -> x.get("BOMNO").toString().equals(data.getBomNo()) && x.get("VERSIONNUM").toString().equals(data.getVersionNum())&&x.get("PLUNO").toString().equals(data.getPluNo()))
                        .distinct().collect(Collectors.toList());

                if(bomFilterRowsV.size()>0){
                    for (Map<String, Object> bomFilterRow : bomFilterRowsV){
                        //算material_pqty
                        String udLength = bomFilterRow.get("UDLENGTH").toString();
                        String mudLength = bomFilterRow.get("MUDLENGTH").toString();
                        BigDecimal unitRatio = new BigDecimal(bomFilterRow.get("UNITRATIO").toString());
                        BigDecimal munitRatio = new BigDecimal(bomFilterRow.get("MUNITRATIO").toString());
                        BigDecimal materialQty = new BigDecimal(bomFilterRow.get("MATERIAL_QTY").toString());
                        String materialUnit = bomFilterRow.get("MATERIAL_UNIT").toString();
                        BigDecimal punitRatio = new BigDecimal(bomFilterRow.get("PUNITRATIO").toString());
                        //先换成基准单位  再换成主件单位  再换成子件单位
                        //基准数量/主件单位  * materialQty =子件的pqty
                        BigDecimal materialPqty = baseQty.multiply(materialQty).divide(unitRatio, Integer.valueOf(udLength), RoundingMode.HALF_UP);
                        BigDecimal pickPqty = materialPqty.multiply(munitRatio).divide(punitRatio, 6, RoundingMode.HALF_UP);//小数位数给大点

                        Map<String, Object> newRow = new HashMap<>(bomFilterRow);
                        newRow.put("MATERIAL_PQTY", materialPqty.toString());
                        newRow.put("PICK_PQTY", pickPqty.toString());
                        newRow.put("OOTYPE",data.getOOType());
                        newRow.put("OOITEM",data.getOOItem());
                        newRow.put("OOFNO",data.getOOfNo());

                        bomListAll.add(newRow);

                        if(data.getSemiWoType().equals("3")){
                            //子件的pqty + materialUnit 接下来算
                            this.addBomMaterialList(req,bomList,bomListAll,newRow.get("BOMNO").toString(),newRow.get("PLUNO").toString(),materialUnit,materialPqty,data.getOOType(),data.getOOfNo(),data.getOOItem());
                        }
                    }
                }
            }

        }

        if(bomListAll.size()>0){
            List<String> pluNos = bomListAll.stream().map(x -> x.get("MATERIAL_PLUNO").toString()).distinct().collect(Collectors.toList());
            MyCommon mc = new MyCommon();
            String withPlu = "";
            if (pluNos !=null && pluNos.size()>0 ) {

                Map<String,String> map = new HashMap<>();
                String sJoinPlu = "";
                for(String s :pluNos) {
                    sJoinPlu += s +",";
                }
                map.put("PLU", sJoinPlu);
                withPlu = mc.getFormatSourceMultiColWith(map);
            }
            String sql = " select a.pluno,sum(a.qty) as baseqty,sum(a.qty-a.lockqty-a.onlineqty) as availableqty from dcp_stock a"
                    + " inner join (" + withPlu + ") b on a.pluno=b.plu "
                    + " where a.eid='" + req.geteId() + "' and a.organizationno='" + req.getOrganizationNO() + "'  ";
            if(Check.NotNull(req.getRequest().getToWarehouse())){
                sql+= " and a.WAREHOUSE='"+req.getRequest().getToWarehouse()+"' ";
            }

            sql+=" group by a.pluno";
            List<Map<String, Object>> getStock = this.doQueryData(sql, null);

            bomListAll.forEach(x->{
                String isBatch = x.get("ISBATCH").toString();
                if("Y".equals(isBatch)&&"Y".equals(isBatchNo)){
                    x.put("ISBATCH", "Y");
                }else{
                    x.put("ISBATCH", "N");
                }
            });

            if("1".equals(req.getRequest().getOpType())){
                List<Map<String, String>> mpList = bomListAll.stream().map(x -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("MPLUNO", x.get("MATERIAL_PLUNO").toString());
                    map.put("PICKUNIT", x.get("PICKUNIT").toString());

                    return map;
                }).distinct().collect(Collectors.toList());
                if(mpList.size()>0){
                    for (Map<String, String> mp : mpList){
                        String mpluno = mp.get("MPLUNO");
                        String pickUnit = mp.get("PICKUNIT");
                        List<Map<String, Object>> filterRows = bomListAll.stream().filter(x -> x.get("MATERIAL_PLUNO").toString().equals(mpluno) && x.get("PICKUNIT").toString().equals(pickUnit)).collect(Collectors.toList());

                        BigDecimal pUnitRatio = new BigDecimal(filterRows.get(0).get("PUNITRATIO").toString());

                        DCP_ProcessTaskPendingMaterialRes.DataList dataList = res.new DataList();
                        BigDecimal sumQty = new BigDecimal(0);
                        for (Map<String, Object> filterRow : filterRows){
                            sumQty=sumQty.add(new BigDecimal(filterRow.get("PICK_PQTY").toString()));
                        }
                        BigDecimal pickMinQty = new BigDecimal(filterRows.get(0).get("PICKMINQTY").toString());
                        BigDecimal pickMulQty = new BigDecimal(filterRows.get(0).get("PICKMULQTY").toString());
                        List<Map<String, Object>> pluFilterRows = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(mpluno)).distinct().collect(Collectors.toList());
                        BigDecimal availableQty=new BigDecimal(0);
                        if(pluFilterRows.size()>0){
                             availableQty = new BigDecimal(pluFilterRows.get(0).get("AVAILABLEQTY").toString());

                        }
                        if("N".equals(req.getRequest().getIsDeductBatchStock())){

                        }else{
                            BigDecimal divide = availableQty.divide(pUnitRatio, 8, RoundingMode.HALF_UP);
                            sumQty=sumQty.subtract( divide);
                        }
                        //检查adviceQty是否>DCP_GOODS.PICKMINQTY且为DCP_GOODS.PICKMULQTY的整倍数，如果满足则按结果返回，不满足则转换为满足的值
                        if(sumQty.compareTo(pickMinQty)>=0 && (pickMulQty.compareTo(BigDecimal.ZERO)==0||sumQty.remainder(pickMulQty).compareTo(new BigDecimal(0))==0)){
                            dataList.setAdviceQty(sumQty.toString());
                        }else {
                            if(pickMulQty.compareTo(BigDecimal.ZERO)==0){
                                dataList.setAdviceQty(sumQty.toString());
                            }else {
                                BigDecimal sumQty2 = pickMulQty.multiply(sumQty.divide(pickMulQty, 0, RoundingMode.HALF_UP));
                                dataList.setAdviceQty(sumQty2.toString());
                            }
                        }

                        //字段给值
                        dataList.setMaterialPluNo(mpluno);
                        dataList.setMaterialPluName(filterRows.get(0).get("MATERIAL_PLUNAME").toString());
                        dataList.setSpec(filterRows.get(0).get("SPEC").toString());
                        dataList.setMaterialPQty("");
                        dataList.setMaterialPUnit("");
                        dataList.setMaterialPUName("");
                        dataList.setMaterialPUdLength("");
                        dataList.setIsBuckle(filterRows.get(0).get("ISBUCKLE").toString());
                        dataList.setPickUnit(filterRows.get(0).get("PICKUNIT").toString());
                        dataList.setPickUName(filterRows.get(0).get("PICKUNITNAME").toString());
                        dataList.setPickUdLength(filterRows.get(0).get("PUDLENGTH").toString());
                        dataList.setPickRoundingMode(PosPub.getRoundingMode(filterRows.get(0).get("PROUNDTYPE").toString()));
                        dataList.setStockQty(availableQty.toString());
                        dataList.setPickMulQty(pickMinQty.toString());
                        dataList.setPickMinQty(pickMulQty.toString());
                        dataList.setBaseUnit(filterRows.get(0).get("BASEUNIT").toString());
                        dataList.setPickUnitRatio(pUnitRatio.toString());
                        dataList.setPrice("");
                        dataList.setDistriPrice("");
                        dataList.setPWarehouse(filterRows.get(0).get("PWAREHOUSE").toString());
                        dataList.setKWarehouse(filterRows.get(0).get("KWAREHOUSE").toString());
                        dataList.setPWarehouseName(filterRows.get(0).get("PWAREHOUSENAME").toString());
                        dataList.setKWarehouseName(filterRows.get(0).get("KWAREHOUSENAME").toString());

                        dataList.setOOType("");
                        dataList.setOOfNo("");
                        dataList.setOOItem("");
                        dataList.setMaterialIsBatch(filterRows.get(0).get("ISBATCH").toString());
                        dataList.setIsLocation(filterRows.get(0).get("ISLOCATION").toString());
                        level1Elm.getDataList().add(dataList);

                    }
                }

            }
            else{
                for (Map<String, Object> singleBom : bomListAll){
                    DCP_ProcessTaskPendingMaterialRes.DataList dataList = res.new DataList();

                    BigDecimal pickMinQty = new BigDecimal(singleBom.get("PICKMINQTY")==null?"0":singleBom.get("PICKMINQTY").toString());
                    BigDecimal pickMulQty = new BigDecimal(singleBom.get("PICKMULQTY")==null?"0":singleBom.get("PICKMULQTY").toString());
                    List<Map<String, Object>> pluFilterRows = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(singleBom.get("MATERIAL_PLUNO").toString())).distinct().collect(Collectors.toList());
                    BigDecimal availableQty=new BigDecimal(0);
                    if(pluFilterRows.size()>0){
                        availableQty = new BigDecimal(pluFilterRows.get(0).get("AVAILABLEQTY").toString());
                    }
                    BigDecimal pickPqty = new BigDecimal(singleBom.get("PICK_PQTY").toString());

                    //检查adviceQty是否>DCP_GOODS.PICKMINQTY且为DCP_GOODS.PICKMULQTY的整倍数，如果满足则按结果返回，不满足则转换为满足的值
                    if(pickPqty.compareTo(pickMinQty)>=0
                            && (pickMulQty.compareTo(BigDecimal.ZERO)==0||pickPqty.remainder(pickMulQty).compareTo(new BigDecimal(0))==0)){
                        dataList.setAdviceQty(pickPqty.toString());
                    }else {
                        if(pickMulQty.compareTo(BigDecimal.ZERO)==0) {
                            dataList.setAdviceQty(pickPqty.toString());
                        }else{
                            BigDecimal pickPqty2 = pickMulQty.multiply(pickPqty.divide(pickMulQty, 0, RoundingMode.HALF_UP));
                            dataList.setAdviceQty(pickPqty2.toString());
                        }
                    }

                    //字段给值
                    dataList.setMaterialPluNo(singleBom.get("MATERIAL_PLUNO").toString());
                    dataList.setMaterialPluName(singleBom.get("MATERIAL_PLUNAME").toString());
                    dataList.setSpec(singleBom.get("SPEC").toString());
                    dataList.setMaterialPQty(singleBom.get("MATERIAL_PQTY").toString());
                    dataList.setMaterialPUnit(singleBom.get("MATERIAL_UNIT").toString());
                    dataList.setMaterialPUName(singleBom.get("MATERIAL_UNITNAME").toString());
                    dataList.setMaterialPUdLength(singleBom.get("MUDLENGTH").toString());
                    dataList.setIsBuckle(singleBom.get("ISBUCKLE").toString());
                    dataList.setPickUnit(singleBom.get("PICKUNIT").toString());
                    dataList.setPickUName(singleBom.get("PICKUNITNAME").toString());
                    dataList.setPickUdLength(singleBom.get("PUDLENGTH").toString());
                    dataList.setPickRoundingMode(PosPub.getRoundingMode(singleBom.get("PROUNDTYPE").toString()));
                    dataList.setStockQty(availableQty.toString());
                    dataList.setPickMulQty(pickMinQty.toString());
                    dataList.setPickMinQty(pickMulQty.toString());
                    dataList.setBaseUnit(singleBom.get("BASEUNIT").toString());
                    dataList.setPickUnitRatio(singleBom.get("PUNITRATIO").toString());
                    dataList.setPrice("0");
                    dataList.setDistriPrice("0");
                    dataList.setPWarehouse(singleBom.get("PWAREHOUSE").toString());
                    dataList.setKWarehouse(singleBom.get("KWAREHOUSE").toString());
                    dataList.setPWarehouseName(singleBom.get("PWAREHOUSENAME").toString());
                    dataList.setKWarehouseName(singleBom.get("KWAREHOUSENAME").toString());

                    dataList.setOOType(singleBom.get("OOTYPE").toString());
                    dataList.setOOfNo(singleBom.get("OOFNO").toString());
                    dataList.setOOItem(singleBom.get("OOITEM").toString());
                    dataList.setMaterialIsBatch(singleBom.get("ISBATCH").toString());
                    dataList.setIsLocation(singleBom.get("ISLOCATION").toString());
                    level1Elm.getDataList().add(dataList);

                }
            }
            List<Map<String, Object>> plus = new ArrayList<>();
            level1Elm.getDataList().forEach(x->{
                Map<String, Object> plu = new HashMap<>();
                plu.put("PLUNO", x.getMaterialPluNo());
                plu.put("PUNIT", x.getPickUnit());
                plu.put("BASEUNIT", x.getBaseUnit());
                plu.put("UNITRATIO", x.getPickUnitRatio());
                plu.put("SUPPLIERID",req.getOrganizationNO());
                plus.add(plu);
            });

            //价格给值
            List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(dao,req.geteId(),req.getBELFIRM(), req.getOrganizationNO(),plus,req.getBELFIRM());

            level1Elm.getDataList().forEach(x->{
                String price="0";
                String distriPrice="0";
                Map<String, Object> condiV= new HashMap<>();
                condiV.put("PLUNO",x.getMaterialPluNo());
                condiV.put("PUNIT",x.getPickUnit());
                List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
                if(priceList!=null && priceList.size()>0 ) {
                    price = priceList.get(0).get("PRICE").toString();
                    distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                }
                x.setPrice(price);
                x.setDistriPrice(distriPrice);

                Integer pickUdLength = Integer.valueOf(Check.Null(x.getPickUdLength())?"0":x.getPickUdLength());
                BigDecimal adviceQty = new BigDecimal(x.getAdviceQty()).setScale(pickUdLength, x.getPickRoundingMode());
                x.setAdviceQty(adviceQty.toString());

            });

        }


        //1.opType为1时
        //过滤结果集中配方原料设置ISPICK='Y'的原料，按原料汇总后得到值，oOType，oOfNo，oOItem，pWarehouse，kWarehouse无需返回
        //pickUnit=DCP_GOODS.PICKUNIT
        //IF isDeductBatchStock='N' THEN
        //adviceQty=单位换算为pickUnit数量，按照单位小数位数四舍五入，检查adviceQty是否>DCP_GOODS.PICKMINQTY且为DCP_GOODS.PICKMULQTY的整倍数，如果满足则按结果返回，不满足则转换为满足的值
        //ELSE
        //adviceQty=单位换算为pickUnit数量，按照单位小数位数四舍五入，扣减转入仓库该原料库存后，检查adviceQty是否>DCP_GOODS.PICKMINQTY且为DCP_GOODS.PICKMULQTY的整倍数，如果满足则按结果返回，不满足则转换为满足的值
        //END
        //如PICKMINQTY为6，PICKMULQTY为3，则当得到领料量为8时，结果转换为9（大于6且为3的整倍数）返回，当得到领料量为12，则返回12
        //
        //2.opType为2时
        //不过滤条件，且无需汇总，oOType，oOfNo，oOItem，pWarehouse，kWarehouse需返回

        res.setDatas(level1Elm);
        return res;
    }

    public void addBomMaterialList(DCP_ProcessTaskPendingMaterialReq req,List<Map<String, Object>> bomMaterialList,List<Map<String, Object>> bomListAll,String bomNo,String pluNo,String pUnit,BigDecimal pQty,String ooType,String oofNo,String ooItem) throws Exception{
        Map<String, Object> baseMap = PosPub.getBaseQty(dao, req.geteId(), pluNo, pUnit, pQty.toString());
        BigDecimal baseQty = new BigDecimal(baseMap.get("baseQty").toString());

        List<Map<String, Object>> filterRows = bomMaterialList.stream().filter(x -> x.get("BOMNO").toString().equals(bomNo) && x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
        if(filterRows.size()>0){
            bomListAll.addAll(filterRows);
            for (Map<String, Object> filterRow : filterRows){
                String udLength = filterRow.get("UDLENGTH").toString();
                String mudLength = filterRow.get("MUDLENGTH").toString();
                BigDecimal unitRatio = new BigDecimal(filterRow.get("UNITRATIO").toString());
                BigDecimal munitRatio = new BigDecimal(filterRow.get("MUNITRATIO").toString());
                BigDecimal punitRatio = new BigDecimal(filterRow.get("PUNITRATIO").toString());
                BigDecimal materialQty = new BigDecimal(filterRow.get("MATERIAL_QTY").toString());
                String materialUnit = filterRow.get("MATERIAL_UNIT").toString();
                //先换成基准单位  再换成主件单位  再换成子件单位
                //基准数量/主件单位  * materialQty =子件的pqty
                BigDecimal loss_rate = new BigDecimal(filterRow.get("LOSS_RATE").toString());
                BigDecimal qty = new BigDecimal(filterRow.get("QTY").toString());
                BigDecimal batchQty = new BigDecimal(filterRow.get("BATCHQTY").toString());


                BigDecimal materialPqty = baseQty
                        .multiply(materialQty)
                        .multiply(BigDecimal.ONE.add(loss_rate.multiply(new BigDecimal("0.01"))))
                        .divide(qty.multiply(batchQty),6, RoundingMode.HALF_UP)
                        .divide(unitRatio, Integer.valueOf(udLength), RoundingMode.HALF_UP);
                //BigDecimal materialPqty = baseQty.multiply(materialQty).divide(unitRatio, Integer.valueOf(udLength), RoundingMode.HALF_UP);
                BigDecimal pickPqty = materialPqty.multiply(munitRatio).divide(punitRatio, 6, RoundingMode.HALF_UP);//小数位数给大点

                Map<String, Object> newRow = new HashMap<>(filterRow);
                newRow.put("MATERIAL_PQTY", materialPqty.toString());
                newRow.put("PICK_PQTY",pickPqty.toString());
                newRow.put("OOTYPE",ooType);
                newRow.put("OOITEM",ooItem);
                newRow.put("OOFNO",oofNo);
                bomListAll.add(newRow);

                this.addBomMaterialList(req,bomMaterialList,bomListAll,newRow.get("BOMNO").toString(),newRow.get("PLUNO").toString(),materialUnit,materialPqty,ooType,oofNo,ooItem);
            }
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ProcessTaskPendingMaterialReq req) throws Exception {
        return null;
    }


}