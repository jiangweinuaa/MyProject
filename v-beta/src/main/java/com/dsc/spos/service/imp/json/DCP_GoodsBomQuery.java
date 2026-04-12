package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_GoodsBomQueryReq;
import com.dsc.spos.json.cust.req.DCP_GoodsBomQueryReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsBomQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_GoodsBomQuery
 * 服务说明：商品BOM查询
 * @author jinzma
 * @since 2020-08-14
 */
public class DCP_GoodsBomQuery extends SPosBasicService<DCP_GoodsBomQueryReq,DCP_GoodsBomQueryRes>{
    
    @Override
    protected boolean isVerifyFail(DCP_GoodsBomQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String bomType = req.getRequest().getBomType();
        List<level1Elm> pluList = req.getRequest().getPluList();
        
        if(Check.Null(bomType)){
            errMsg.append("BOM类型不可为空值, ");
            isFail = true;
        }
        
        if(pluList==null || pluList.size()==0){
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        for (level1Elm plu : pluList) {
            String pluNo = plu.getPluNo();
            String prodUnit = plu.getProdUnit();
            
            if(Check.Null(pluNo)){
                errMsg.append("商品编号不可为空值, ");
                isFail = true;
            }
            if(Check.Null(prodUnit)){
                errMsg.append("生产单位不可为空值, ");
                isFail = true;
            }
            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GoodsBomQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsBomQueryReq>(){};
    }
    
    @Override
    protected DCP_GoodsBomQueryRes getResponseType() {
        return new DCP_GoodsBomQueryRes();
    }
    
    @Override
    protected DCP_GoodsBomQueryRes processJson(DCP_GoodsBomQueryReq req) throws Exception {
        //try {
            DCP_GoodsBomQueryRes res = this.getResponse();
            String eId=req.geteId();
            String shopId = req.getShopId();
            String companyId = req.getBELFIRM();
            String isCheckMaterialStock = req.getRequest().getIsCheckMaterialStock();
            String warehouse = req.getRequest().getWarehouse();

            res.setDatas(new ArrayList<>());
            
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps= PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
                
                //商品取价计算
                if (!Check.Null(req.getRequest().getShopId())) {
                    shopId = req.getRequest().getShopId();
                    sql =" select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' and status='100'";
                    List<Map<String, Object>> company=this.doQueryData(sql, null);
                    if (company != null && !company.isEmpty())
                        companyId = company.get(0).get("BELFIRM").toString();
                }
                if (Check.Null(companyId)) {
                    sql =" select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' and status='100'";
                    List<Map<String, Object>> company=this.doQueryData(sql, null);
                    if (company != null && !company.isEmpty())
                        companyId = company.get(0).get("BELFIRM").toString();
                }
                
                List<Map<String, Object>> plus = new ArrayList<>();
                Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                condition.put("MATERIAL_PLUNO", true);
                List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);
                for (Map<String, Object> onePlu :getQPlu ) {
                    Map<String, Object> plu = new HashMap<>();
                    plu.put("PLUNO", onePlu.get("MATERIAL_PLUNO").toString());
                    plu.put("PUNIT", onePlu.get("MATERIAL_UNIT").toString());
                    plu.put("BASEUNIT", onePlu.get("MATERIALBASEUNIT").toString());
                    plu.put("UNITRATIO", onePlu.get("MATERIALUNITRATIO").toString());
                    plus.add(plu);
                }
                MyCommon mc = new MyCommon();
                List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(dao,eId,companyId, shopId,plus,companyId);
                
                condition.clear();
                condition = new HashMap<>(); //查詢條件
                condition.put("BOMNO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData,condition);
                List<String> materialPluNos=new ArrayList();
                for (Map<String, Object> oneData:getQHeader) {
                    DCP_GoodsBomQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    String bomNo = oneData.get("BOMNO").toString();
                    
                    oneLv1.setMaterialList(new ArrayList<>());
                    for (Map<String, Object> material:getQData) {
                        if(bomNo.equals(material.get("BOMNO").toString())) {
                            DCP_GoodsBomQueryRes.level2Elm oneLv2 = res.new level2Elm();
                            String materialPluNo = material.get("MATERIAL_PLUNO").toString();
                            String materialPluName = material.get("MATERIALPLUNAME").toString();
                            String qty = material.get("QTY").toString();
                            String materialUnit = material.get("MATERIAL_UNIT").toString();
                            String materialUnitName = material.get("MATERIALUNITNAME").toString();
                            String materialUnitLength = material.get("MATERIALUNITLENGTH").toString();
                            String materialQty = material.get("MATERIAL_QTY").toString();
                            String lossRate = material.get("LOSS_RATE").toString();
                            String isBuckle = material.get("ISBUCKLE").toString();
                            String isReplace = material.get("ISREPLACE").toString();
                            String sortId = material.get("SORTID").toString();
                            String materialBaseUnit = material.get("MATERIALBASEUNIT").toString();
                            String materialBaseUnitName = material.get("MATERIALBASEUNITNAME").toString();
                            String materialUnitRatio = material.get("MATERIALUNITRATIO").toString();
                            String materialIsBatch = material.get("MATERIALISBATCH").toString();
                            String materialImage = material.get("MATERIALIMAGE").toString();
                            if (!Check.Null(materialImage)){
                                materialImage = domainName+materialImage;
                            }

                            if(!materialPluNos.contains(materialPluNo)){
                                materialPluNos.add(materialPluNo);
                            }
                            
                            //获取原料的零售价和配送价
                            String materialPrice="0";
                            String materialDistriPrice="0";
                            Map<String, Object> condiV= new HashMap<>();
                            condiV.put("PLUNO",materialPluNo);
                            condiV.put("PUNIT",materialUnit);
                            List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
                            if(priceList!=null && priceList.size()>0 ) {
                                materialPrice=priceList.get(0).get("PRICE").toString();
                                materialDistriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                            }
                            
                            oneLv2.setMaterial_pluNo(materialPluNo);
                            oneLv2.setMaterial_pluName(materialPluName);
                            oneLv2.setMaterial_finalProdBaseQty(qty);
                            oneLv2.setMaterial_unit(materialUnit);
                            oneLv2.setMaterial_unitName(materialUnitName);
                            oneLv2.setMaterial_unitUdLength(materialUnitLength);
                            oneLv2.setMaterial_rawMaterialBaseQty(materialQty);
                            oneLv2.setLossRate(lossRate);
                            oneLv2.setIsBuckle(isBuckle);
                            oneLv2.setIsReplace(isReplace);
                            oneLv2.setSortId(sortId);
                            oneLv2.setMaterial_baseUnit(materialBaseUnit);
                            oneLv2.setMaterial_baseUnitName(materialBaseUnitName);
                            oneLv2.setMaterial_unitRatio(materialUnitRatio);
                            oneLv2.setMaterial_price(materialPrice);
                            oneLv2.setMaterial_distriPrice(materialDistriPrice);
                            oneLv2.setMaterial_isBatch(materialIsBatch);
                            oneLv2.setMaterial_listImage(materialImage);
                            //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221110
                            oneLv2.setMaterial_baseUnitUdLength(material.get("MATERIALBASEUNITUDLENGTH").toString());
                            oneLv2.setMaterial_stockQty("0");
                            oneLv2.setMaterial_spec(oneData.get("MATERIALSPEC").toString());
                            oneLv2.setKWarehouse(material.get("KWAREHOUSE").toString());
                            oneLv2.setKIsLocation(material.get("KISLOCATION").toString());
                            oneLv2.setKWarehouseName(material.get("KWAREHOUSENAME").toString());
                            oneLv2.setCostRate(material.get("COSTRATE").toString());
                            oneLv1.getMaterialList().add(oneLv2);
                        }
                    }
                    
                    oneLv1.setBomNo(bomNo);
                    oneLv1.setBomType(oneData.get("BOMTYPE").toString());
                    oneLv1.setPluNo(oneData.get("PLUNO").toString());
                    oneLv1.setUnit(oneData.get("UNIT").toString());
                    oneLv1.setMulQty(oneData.get("MULQTY").toString());
                    oneLv1.setVersionNum(oneData.get("VERSIONNUM").toString());
                    oneLv1.setProdType(oneData.get("PRODTYPE").toString());
                    oneLv1.setInWarehouse(oneData.get("INWAREHOUSE").toString());
                    oneLv1.setIsLocation(oneData.get("ISLOCATION").toString());
                    oneLv1.setInWarehouseName(oneData.get("INWAREHOUSENAME").toString());
                    res.getDatas().add(oneLv1);
                }

                if("1".equals(isCheckMaterialStock)){
                    StringBuffer sJoinno=new StringBuffer("");
                    for (String no :materialPluNos){
                        sJoinno.append(no+",");
                    }
                    Map<String, String> mapPlu=new HashMap<String, String>();
                    mapPlu.put("PLUNO", sJoinno.toString());
                    MyCommon cm=new MyCommon();
                    String withasSql_mono=cm.getFormatSourceMultiColWith(mapPlu);
                    if(!Check.Null(withasSql_mono)){
                        String stockSql="with p as ("+withasSql_mono+") "+
                                " select a.pluno,sum(a.qty-nvl(a.lockqty,0)-nvl(a.ONLINEQTY,0)) as stockqty" +
                                " from DCP_STOCK a " +
                                " inner join p on p.pluno=a.pluno "+
                                " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                                ;
                        if(!Check.Null(warehouse)){
                            stockSql+=" and a.warehouse='"+warehouse+"' ";
                        }
                        stockSql+= " GROUP BY a.pluno";
                        List<Map<String, Object>> getStockData=this.doQueryData(stockSql, null);

                        if(getStockData.size()>0){
                            for( DCP_GoodsBomQueryRes.level1Elm l1  :res.getDatas()){
                                for( DCP_GoodsBomQueryRes.level2Elm l2  :l1.getMaterialList()){
                                    List<Map<String, Object>> stocks = getStockData.stream().filter(x -> x.get("PLUNO").equals(l2.getMaterial_pluNo())).collect(Collectors.toList());
                                    if(stocks.size()>0){
                                        l2.setMaterial_stockQty(stocks.get(0).get("STOCKQTY").toString());
                                    }
                                }
                            }
                        }


                    }

                }

            }
            return res;
        //}
        //catch (Exception e) {
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_GoodsBomQueryReq req) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String bomType = req.getRequest().getBomType();
        if (!Check.Null(req.getRequest().getShopId())) {
            shopId = req.getRequest().getShopId();
        }
        String langType=req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        List<level1Elm> pluList = req.getRequest().getPluList();
        
        Map<String, String> map = new HashMap<>();
        StringBuffer sJoinPluNo=new StringBuffer();
        StringBuffer sJoinProdUnit=new StringBuffer();
        for (level1Elm par : pluList)
        {
            sJoinPluNo .append( par.getPluNo()+",");
            sJoinProdUnit .append( par.getProdUnit()+",");
        }
        map.put("PLUNO", sJoinPluNo.toString());
        map.put("PRODUNIT", sJoinProdUnit.toString());
        MyCommon mc = new MyCommon();
        String withPlu =  mc.getFormatSourceMultiColWith(map);
        
        sqlbuf.append(" with p1 as ("+withPlu+")");
        sqlbuf.append(""
                + " select a.bomno,a.versionnum,a.prodtype,a.bomtype,a.pluno,a.unit,a.mulqty,"
                + " c.material_pluno,c.qty,c.material_unit,c.material_qty,c.loss_rate,c.COSTRATE,c.isbuckle,c.isreplace,c.sortid,"
                + " mgl.plu_name as materialPluName,mul.uname as materialUnitName,mu.udlength as materialUnitLength,"
                + " mg.baseunit as materialBaseUnit,mbul.uname as materialBaseUnitName,mgu.unitratio as materialUnitRatio,"
                + " mg.isbatch as materialIsBatch,mgimage.listimage as materialImage,buludlength.udlength as materialbaseunitudlength,mg.spec as materialSpec," +
                " d1.warehouse as inwarehouse,d2.warehouse_name as inwarehousename,d1.islocation as islocation ,e1.warehouse as kwarehouse,e1.islocation as kislocation,e2.warehouse_name as kwarehousename,a.disptype "
                + " from ("
                + " select row_number() over (partition by a.pluno,a.unit order by effdate desc) as rn,a.* from dcp_bom a"
                + " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"'"
                + " inner join p1 on a.pluno=p1.pluno and a.unit=p1.produnit"
                + " where a.eId='"+eId+"' and (to_char(a.effdate,'yyyyMMdd')<=to_char(sysdate,'yyyyMMdd') or a.effdate is null or a.effdate='' ) and a.status='100' and a.bomtype = '"+bomType+"'"
                + " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))"
                + " )a"
                + " inner join dcp_bom_material c on a.bomno=c.bomno and c.eid='"+eId+"' and (to_char(c.material_bdate,'yyyyMMdd')<=to_char(sysdate,'yyyyMMdd') and to_char(material_edate,'yyyyMMdd')>=to_char(sysdate,'yyyyMMdd') or material_edate is null or material_edate=''  )"
                + " left  join dcp_goods_lang mgl on mgl.eid=a.eid and mgl.pluno=c.material_pluno and mgl.lang_type='"+langType+"'"
                + " left  join dcp_unit_lang mul on mul.eid=a.eid and mul.unit=c.material_unit and mul.lang_type='"+langType+"'"
                + " left  join dcp_unit mu on mu.eid=a.eid and mu.unit=c.material_unit and mu.status='100'"
                + " inner join dcp_goods mg on mg.eid=a.eid and mg.pluno=c.material_pluno and mg.status='100'"
                + " left  join dcp_unit_lang mbul on mbul.eid=a.eid and mbul.unit=mg.baseunit and mbul.lang_type='"+langType+"'"
                + " inner join dcp_goods_unit mgu on mgu.eid =a.eid and mgu.pluno=c.material_pluno and mgu.ounit=c.material_unit and mgu.unit=mg.baseunit"
                + " left  join dcp_goodsimage mgimage on mgimage.eid=a.eid and mgimage.pluno=c.material_pluno and mgimage.apptype='ALL'"
                + " left  join dcp_unit buludlength on a.eid=buludlength.eid and mg.baseunit=buludlength.unit" +
                " left join MES_WAREHOUSE_GROUP_DETAIL d on d.eid=a.eid and a.INWGROUPNO=d.WGROUPNO and d.organizationno='"+req.getOrganizationNO()+"' " +
                " left join dcp_warehouse d1 on d1.eid=a.eid and d1.organizationno=d.organizationno and d1.warehouse=d.warehouse " +
                " left join dcp_warehouse_lang d2 on d2.eid=a.eid and d2.organizationno=d.organizationno and d2.warehouse=d.warehouse and d2.lang_type='"+req.getLangType()+"'" +
                " left join MES_WAREHOUSE_GROUP_DETAIL e on e.eid=a.eid and e.WGROUPNO=c.KWGROUPNO and e.organizationno='"+req.getOrganizationNO()+"' " +
                " left join dcp_warehouse e1 on e1.eid=a.eid and e1.warehouse=e.warehouse and e1.organizationno=e.organizationno " +
                " left join dcp_warehouse_lang e2 on e2.eid=a.eid and e2.warehouse=e.warehouse and e2.organizationno=e.organizationno and e2.lang_type='"+langType+"'"
                + " where a.rn=1"
                + " order by a.pluno,c.sortid"
                + " ");
        
        return sqlbuf.toString();
    }
    
}
