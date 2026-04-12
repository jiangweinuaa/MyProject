package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShopGoodsSupPriceQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopGoodsSupPriceQueryRes;
import com.dsc.spos.json.cust.res.DCP_ShopGoodsSupPriceQueryRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ShopGoodsSupPriceQueryRes.level2Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @apiNote 门店商品供货价查询
 * @since 2021-05-06
 * @author jinzma
 */
public class DCP_ShopGoodsSupPriceQuery extends SPosBasicService<DCP_ShopGoodsSupPriceQueryReq, DCP_ShopGoodsSupPriceQueryRes> {
    
    @Override
    protected boolean isVerifyFail(DCP_ShopGoodsSupPriceQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String shopId = req.getRequest().getShopId();
        String supDate = req.getRequest().getSupDate(); //YYYY-MM-DD
        
        if(Check.Null(shopId)){
            errMsg.append("门店id不可为空值, ");
            isFail = true;
        }
        if (!Check.Null(supDate)){
            if (supDate.length()==10) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date tempDate = sdf.parse(supDate);
                } catch (ParseException e) {
                    errMsg.append("销售日期格式错误, ");
                    isFail = true;
                }
            }else{
                errMsg.append("销售日期格式错误, ");
                isFail = true;
            }
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ShopGoodsSupPriceQueryReq> getRequestType() {
        return new TypeToken<DCP_ShopGoodsSupPriceQueryReq>(){};
    }
    
    @Override
    protected DCP_ShopGoodsSupPriceQueryRes getResponseType() {
        return new DCP_ShopGoodsSupPriceQueryRes();
    }
    
    @Override
    protected DCP_ShopGoodsSupPriceQueryRes processJson(DCP_ShopGoodsSupPriceQueryReq req) throws Exception {
        DCP_ShopGoodsSupPriceQueryRes res = this.getResponse();
        level1Elm oneLv1 = res.new level1Elm();
        String eId = req.geteId();
        String shopId = req.getRequest().getShopId();
        String companyId = "";
        try {
            //总部查询作业，无法从TOKEN里获取，必须查数据库
            String sql=" select org_form,belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData==null || getQData.isEmpty()){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "门店在组织表(DCP_ORG)中不存在!");
            }else {
                String orgForm = getQData.get(0).get("ORG_FORM").toString();
                companyId = getQData.get(0).get("BELFIRM").toString();
                if (Check.Null(companyId) && orgForm.equals("0")){
                    companyId = shopId;
                }
            }
            getQData.clear();
            sql = this.getQuerySql(req,companyId);
            getQData = this.doQueryData(sql, null);
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            if (getQData != null && getQData.isEmpty() == false){
                //统计未关联到供货价模板中的商品
                List<String> plus = new ArrayList<String>();
                List<Map<String, Object>> getQSupPriceTemplate = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> oneData:getQData) {
                    if (Check.Null(oneData.get("PRICETEMPLATE_TEMPLATEID").toString())){
                        plus.add(oneData.get("PLUNO").toString());
                    }
                }
                if (plus!=null && plus.size()>0){
                    sql = this.getSupPrice(req,plus);
                    getQSupPriceTemplate = this.doQueryData(sql, null);
                }
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                oneLv1.setGoodsList(new ArrayList<level2Elm>());
                for (Map<String, Object> oneData:getQData) {
                    level2Elm oneLv2 = res.new level2Elm();
                    String pluNo = oneData.get("PLUNO").toString();
                    String pluName = oneData.get("PLU_NAME").toString();
                    String templateId = oneData.get("PRICETEMPLATE_TEMPLATEID").toString();
                    String templateName = oneData.get("PRICETEMPLATE_TEMPLATENAME").toString();
                    String supplierType = oneData.get("PRICETEMPLATE_SUPPLIERTYPE").toString();
                    String supplierId = oneData.get("PRICETEMPLATE_SUPPLIERID").toString();
                    String supplierName = oneData.get("PRICETEMPLATE_SUPPLIERNAME").toString();
                    String unit = oneData.get("OUNIT").toString();
                    String unitName = oneData.get("OUNITNAME").toString();
                    String price = oneData.get("PRICETEMPLATE_PRICE").toString();
                    String beginDate = oneData.get("PRICETEMPLATE_BEGINDATE").toString();
                    String endDate = oneData.get("PRICETEMPLATE_ENDDATE").toString();
                    
                    //是否可用Y/N：商品状态为100且模板商品状态为100时可用，否则不可用
                    String canUse = "Y";
                    String status = oneData.get("STATUS").toString();
                    String goodsTempStatus = oneData.get("GOODSTEMP_STATUS").toString();
                    if (Check.Null(status)||Check.Null(goodsTempStatus)||!status.equals("100")||!goodsTempStatus.equals("100")){
                        canUse="N";
                    }

                    /*单价取价顺序说明
                       1、取价格模板对应的单价，单位同价格模板单位
                       2、商品在价格模板里其他单位的单价，通过单位转换率进行转换
                       3、取商品标准供货价，单位同销售单位
                       4、用标准供货价通过单位转换率进行转换
                    */
                    if (Check.Null(price)){
                        Map<String, Object> condiV=new HashMap<String, Object>();
                        condiV.put("PLUNO",pluNo);
                        List<Map<String, Object>> supPriceTemplateList= MapDistinct.getWhereMap(getQSupPriceTemplate, condiV, false);
                        if(supPriceTemplateList!=null && supPriceTemplateList.size()>0 ){
                            //取价格模板单价
                            String supPriceTemplate_unit = supPriceTemplateList.get(0).get("UNIT").toString();
                            String supPriceTemplate_price = supPriceTemplateList.get(0).get("PRICE").toString();
                            String supPriceTemplate_unitRatio = supPriceTemplateList.get(0).get("UNITRATIO").toString();
                            if (Check.Null(supPriceTemplate_unitRatio)){
                                supPriceTemplate_unitRatio="0";
                            }
                            String oUnit_unitRatio = oneData.get("OUNIT_UNITRATIO").toString();
                            String udLength = oneData.get("UDLENGTH").toString();
                            if (!PosPub.isNumeric(udLength)) {
                                udLength = "0";
                            }
                            
                            BigDecimal supPriceTemplate_price_b = new BigDecimal(supPriceTemplate_price);
                            BigDecimal supPriceTemplate_unitRatio_b = new BigDecimal(supPriceTemplate_unitRatio);
                            BigDecimal oUnit_unitRatio_b = new BigDecimal(oUnit_unitRatio);
                            if (unit.equals(supPriceTemplate_unit)){
                                price= supPriceTemplate_price;
                            }else{
                                if (supPriceTemplate_unitRatio_b.compareTo(BigDecimal.ZERO)==0 || oUnit_unitRatio_b.compareTo(BigDecimal.ZERO)==0 ){
                                    price="0";
                                }else{
                                    BigDecimal price_b = new BigDecimal("0");
                                    price_b = supPriceTemplate_price_b.multiply(oUnit_unitRatio_b.divide(supPriceTemplate_unitRatio_b,6, RoundingMode.HALF_UP));
                                    price_b = price_b.setScale(Integer.parseInt(udLength), RoundingMode.HALF_UP);
                                    price=price_b.toPlainString();
                                }
                            }
                        }else{
                            //取商品标准供货价
                            String sUnit = oneData.get("SUNIT").toString();
                            String sUnit_unitRatio = oneData.get("SUNIT_UNITRATIO").toString();
                            if (Check.Null(sUnit_unitRatio)){
                                sUnit_unitRatio = "0";
                            }
                            
                            String defSupPrice = oneData.get("DEFSUPPRICE").toString();
                            String oUnit_unitRatio = oneData.get("OUNIT_UNITRATIO").toString();
                            String udLength = oneData.get("UDLENGTH").toString();
                            if (!PosPub.isNumeric(udLength)) {
                                udLength = "0";
                            }
                            
                            BigDecimal sUnit_unitRatio_b = new BigDecimal(sUnit_unitRatio);
                            BigDecimal defSupPrice_b = new BigDecimal(defSupPrice);
                            BigDecimal oUnit_unitRatio_b = new BigDecimal(oUnit_unitRatio);
                            
                            if (unit.equals(sUnit)){
                                price = defSupPrice;
                            }else {
                                if (sUnit_unitRatio_b.compareTo(BigDecimal.ZERO) == 0 || oUnit_unitRatio_b.compareTo(BigDecimal.ZERO) == 0) {
                                    price = "0";
                                } else {
                                    BigDecimal price_b = new BigDecimal("0");
                                    price_b = defSupPrice_b.multiply(oUnit_unitRatio_b.divide(sUnit_unitRatio_b,6, RoundingMode.HALF_UP));
                                    price_b = price_b.setScale(Integer.parseInt(udLength), RoundingMode.HALF_UP);
                                    price=price_b.toPlainString();
                                }
                            }
                        }
                    }
                    
                    oneLv2.setPluNo(pluNo);
                    oneLv2.setPluName(pluName);
                    oneLv2.setTemplateId(templateId);
                    oneLv2.setTemplateName(templateName);
                    oneLv2.setSupplierType(supplierType);
                    oneLv2.setSupplierId(supplierId);
                    oneLv2.setSupplierName(supplierName);
                    oneLv2.setUnit(unit);
                    oneLv2.setUnitName(unitName);
                    oneLv2.setPrice(price);
                    oneLv2.setBeginDate(beginDate);
                    oneLv2.setEndDate(endDate);
                    oneLv2.setCanUse(canUse);
                    
                    oneLv1.getGoodsList().add(oneLv2);
                }
            }else{
                totalRecords = 0;
                totalPages = 0;
            }
            
            res.setDatas(oneLv1);
            
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_ShopGoodsSupPriceQueryReq req) throws Exception {
        return null;
    }
    
    protected String getQuerySql(DCP_ShopGoodsSupPriceQueryReq req,String companyId) throws Exception {
        String eId=req.geteId();
        String shopId = req.getRequest().getShopId();
        String keyTxt = req.getRequest().getKeyTxt();
        String supplierId = req.getRequest().getSupplierId();
        String supDate = req.getRequest().getSupDate();
        String langType = req.getLangType();
        
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" "
                + " with goods as("
                + " select a.eid,a.pluno,a.baseunit,a.sunit,a.price as defprice,a.supprice as defsupprice,b.plu_name,a.status,"
                + " c.ounit,c.unitratio as ounit_unitratio,d.uname as ounitname,e.unitratio as sunit_unitratio,f.udlength"
                + " from dcp_goods a"
                + " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"'"
                + " left join dcp_goods_unit c on a.eid=c.eid and a.pluno=c.pluno"
                + " left join dcp_unit_lang  d on a.eid=d.eid and c.ounit=d.unit and d.lang_type='"+langType+"'"
                + " left join dcp_goods_unit e on a.eid=e.eid and a.pluno=e.pluno and a.sunit=e.ounit"
                + " inner join dcp_unit f on a.eid=f.eid and c.ounit=f.unit and f.status='100'"
                + " where a.eid='"+eId+"'"
                + " ");
        if (!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.pluno like '%"+keyTxt+"%' or a.shortcut_code like '%"+keyTxt+"%' or b.plu_name like '%"+keyTxt+"%')");
        }
        sqlbuf.append(" )");
        sqlbuf.append(" "
                + " ,goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                + " where a.eid='"+eId+"' and a.status='100'"
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " )a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid"
                + " ");
        /*if (!Check.Null(keyTxt)){
            sqlbuf.append(" inner join goods on b.pluno=goods.pluno");
        }*/
        sqlbuf.append(" where a.rn=1");
        sqlbuf.append(" )");
        sqlbuf.append(" "
                + " ,suppricetemplate as ("
                + " select * from ("
                + " select a.suppliertype,a.supplierid,a.receivertype,a.receiveridrange,"
                + " b.templateid,b.pluno,b.unit,b.featureno,b.price,"
                + " to_char(b.begindate,'YYYY-MM-DD') as begindate,to_char(b.enddate,'YYYY-MM-DD') as enddate,"
                + " d.templatename,f.unitratio,g.org_name as suppliername,"
                + " row_number() over (partition by b.pluno,b.unit order by a.createtime desc,b.item desc) as rn"
                + " from dcp_suppricetemplate a"
                + " inner join dcp_suppricetemplate_price b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                //+ " left  join dcp_suppricetemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.id='"++"'"
                + " left  join dcp_suppricetemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.id='"+shopId+"'"
                + " left  join dcp_suppricetemplate_lang  d on d.eid=a.eid and d.templateid=a.templateid and d.lang_type='"+langType+"'"
                + " left  join dcp_goods_unit f on a.eid=f.eid and b.pluno=f.pluno and b.unit=f.ounit"
                + " left  join dcp_org_lang g on a.eid=g.eid and a.supplierid=g.organizationno and g.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.status='100'"
                + " and a.receivertype='2' and (a.receiveridrange='0' or c2.id is not null)"
                + " ");
        if (Check.Null(supDate)){
            sqlbuf.append(" and trunc(b.begindate)<=trunc(sysdate) and trunc(b.enddate)>=trunc(sysdate)");
        }else {
            sqlbuf.append(" and trunc(b.begindate)<=to_date('"+supDate+"','YYYY-MM-DD') and trunc(b.enddate)>=to_date('"+supDate+"','YYYY-MM-DD')");
        }
        if (!Check.Null(supplierId)){
            sqlbuf.append(" and a.supplierid='"+supplierId+"'");
        }
        sqlbuf.append(" ) where rn=1");
        sqlbuf.append(" )");
        sqlbuf.append(" "
                + " select * from ("
                + " select a.*,b.status as goodstemp_status,"
                + " c.templateid as pricetemplate_templateid,c.templatename as pricetemplate_templatename,"
                + " c.unit as pricetemplate_unit,c.price as pricetemplate_price,"
                + " c.begindate as pricetemplate_begindate,"
                + " c.enddate as pricetemplate_enddate,"
                + " c.unitratio as pricetemplate_unitratio,"
                + " c.suppliertype as pricetemplate_suppliertype,"
                + " c.supplierid as pricetemplate_supplierid,"
                + " c.suppliername as pricetemplate_suppliername,"
                + " count(*) over() num,row_number() over (order by a.pluno) as rn"
                + " from goods a"
                + " left join goodstemplate b on a.pluno=b.pluno"
                + " left join suppricetemplate c on a.pluno=c.pluno and a.ounit=c.unit"
                + " ) where rn>"+startRow+" and rn<="+(startRow+pageSize)
                + " ");
        
        return sqlbuf.toString();
    }
    
    private String getSupPrice(DCP_ShopGoodsSupPriceQueryReq req,List<String>plus) throws Exception{
        String eId=req.geteId();
        String shopId = req.getRequest().getShopId();
        String keyTxt = req.getRequest().getKeyTxt();
        String supplierId = req.getRequest().getSupplierId();
        String supDate = req.getRequest().getSupDate();
        String langType = req.getLangType();
        StringBuffer sqlbuf = new StringBuffer();
        //处理plus
        MyCommon mc = new MyCommon();
        Map<String,String> map = new HashMap<String, String>();
        StringBuffer sJoinPlu = new StringBuffer();
        for(String pluNo :plus){
            sJoinPlu.append(pluNo).append(",");
        }
        map.put("PLUNO", sJoinPlu.toString());
        sqlbuf.append(" with goods as ("+mc.getFormatSourceMultiColWith(map)+")");
        
        sqlbuf.append(" "
                + " select * from ("
                + " select b.pluno,b.unit,b.featureno,b.price,f.unitratio,"
                + " row_number() over (partition by b.pluno,b.unit order by a.createtime desc,b.item desc) as rn"
                + " from dcp_suppricetemplate a"
                + " inner join dcp_suppricetemplate_price b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " inner join goods on b.pluno=goods.pluno"
                //+ " left  join dcp_suppricetemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.id='"++"'"
                + " left  join dcp_suppricetemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.id='"+shopId+"'"
                + " left  join dcp_goods_unit f on a.eid=f.eid and b.pluno=f.pluno and b.unit=f.ounit"
                + " where a.eid='"+eId+"' and a.status='100'"
                + " and a.receivertype='2' and (a.receiveridrange='0' or c2.id is not null)"
                + " ");
        if (Check.Null(supDate)){
            sqlbuf.append(" and trunc(b.begindate)<=trunc(sysdate) and trunc(b.enddate)>=trunc(sysdate)");
        }else {
            sqlbuf.append(" and trunc(b.begindate)<=to_date('"+supDate+"','YYYY-MM-DD') and trunc(b.enddate)>=to_date('"+supDate+"','YYYY-MM-DD')");
        }
        if (!Check.Null(supplierId)){
            sqlbuf.append(" and a.supplierid='"+supplierId+"'");
        }
        sqlbuf.append(" ) where rn=1");
        
        return sqlbuf.toString();
    }
}
