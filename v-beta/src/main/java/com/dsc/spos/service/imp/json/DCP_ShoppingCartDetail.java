package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShoppingCartDetailReq;
import com.dsc.spos.json.cust.res.DCP_ShoppingCartDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;
import java.util.*;

/**
 * @description: 购物车查询
 * @author: wangzyc
 * @create: 2021-05-27
 */
public class DCP_ShoppingCartDetail extends SPosBasicService<DCP_ShoppingCartDetailReq, DCP_ShoppingCartDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_ShoppingCartDetailReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ShoppingCartDetailReq> getRequestType() {
        return new TypeToken<DCP_ShoppingCartDetailReq>(){};
    }
    
    @Override
    protected DCP_ShoppingCartDetailRes getResponseType() {
        return new DCP_ShoppingCartDetailRes();
    }
    
    @Override
    protected DCP_ShoppingCartDetailRes processJson(DCP_ShoppingCartDetailReq req) throws Exception {
        /**
         * 根据当前请求的用户匹配
         */
        DCP_ShoppingCartDetailRes res = null;
        res = this.getResponse();
        DCP_ShoppingCartDetailRes.level1Elm level1Elm = res.new level1Elm();
        res.setDatas(level1Elm);
        
        try {
            
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getShoppingCart = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getShoppingCart)){
                level1Elm.setPluList(new ArrayList<DCP_ShoppingCartDetailRes.level2Elm>());
                level1Elm.setInvalidPluList(new ArrayList<DCP_ShoppingCartDetailRes.level3Elm>());
                Map<String, Object> getTot = getShoppingCart.get(0);
                String totPqty = getTot.get("TOTPQTY").toString();
                String totCqty = getTot.get("TOTCQTY").toString();
                String totAmt = getTot.get("TOTAMT").toString();
                String totDistriAmt = getTot.get("TOTDISTRIAMT").toString();
                
                level1Elm.setTotPqty(totPqty);
                level1Elm.setTotCqty(totCqty);
                level1Elm.setTotAmt(totAmt);
                level1Elm.setTotDistriAmt(totDistriAmt);
                
                String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=ISHTTPS.equals("1")?"https://":"http://";
                String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                
                for (Map<String, Object> shoppingCart : getShoppingCart) {
                    String item = shoppingCart.get("ITEM").toString();
                    String listImage = shoppingCart.get("LISTIMAGE").toString();
                    String maxOrderSpec = shoppingCart.get("MAXORDERSPEC").toString();
                    String pluNo = shoppingCart.get("PLUNO").toString();
                    String pluName = shoppingCart.get("PLU_NAME").toString();
                    String featureNo = shoppingCart.get("FEATURENO").toString();
                    String featureName = shoppingCart.get("FEATURENAME").toString();
                    String punit = shoppingCart.get("PUNIT").toString();
                    String punitName = shoppingCart.get("PUNITNAME").toString();
                    String pqty = shoppingCart.get("PQTY").toString();
                    String baseUnit = shoppingCart.get("BASEUNIT").toString();
                    String baseUnitName = shoppingCart.get("UNAME").toString();
                    String baseQty = shoppingCart.get("BASEQTY").toString();
                    String unitRatio = shoppingCart.get("UNIT_RATIO").toString();
                    String price = shoppingCart.get("PRICE").toString();
                    String amt = shoppingCart.get("AMT").toString();
                    String distriPrice = shoppingCart.get("DISTRIPRICE").toString();
                    String distriAmt = shoppingCart.get("DISTRIAMT").toString();
                    String minQty = shoppingCart.get("MINQTY").toString();
                    String maxQty = shoppingCart.get("MAXQTY").toString();
                    String mulQty = shoppingCart.get("MULQTY").toString();
                    String punitUdLength = shoppingCart.get("UDLENGTH").toString();
                    String selected = shoppingCart.get("SELECTED").toString();
                    String pluType = shoppingCart.get("PLUTYPE").toString();
                    String canrequire = shoppingCart.get("CANREQUIRE").toString();
                    String isNewGoods = shoppingCart.get("ISNEWGOODS").toString();
                    String isHotGoods = shoppingCart.get("ISHOTGOODS").toString();
                    //【ID1033707】【潮品3.0】门店要货量管控：当前补货申请量+当前库存+配送在途+要货在途量不可超过设定库存上限值----服务端 by jinzma 20230619
                    String warningQty = shoppingCart.get("WARNINGQTY").toString();
                    
                    if(canrequire.equals("Y")){
                        DCP_ShoppingCartDetailRes.level2Elm level2Elm = res.new level2Elm();
                        
                        // 拼接图片地址
                        if(!Check.Null(listImage)){
                            if (DomainName.endsWith("/"))
                            {
                                level2Elm.setListImage(httpStr+DomainName+"resource/image/" +listImage);
                            }
                            else
                            {
                                level2Elm.setListImage(httpStr+DomainName+"/resource/image/" +listImage);
                            }
                        }else{
                            level2Elm.setListImage("");
                        }
                        level2Elm.setItem(item);
                        level2Elm.setPluNo(pluNo);
                        level2Elm.setPluType(pluType);
                        level2Elm.setMaxOrderSpec(maxOrderSpec);
                        level2Elm.setPluName(pluName);
                        level2Elm.setFeatureNo(featureNo);
                        level2Elm.setFeatureName(featureName);
                        level2Elm.setPunit(punit);
                        level2Elm.setPunitName(punitName);
                        level2Elm.setPqty(pqty);
                        level2Elm.setBaseUnit(baseUnit);
                        level2Elm.setBaseUnitName(baseUnitName);
                        level2Elm.setBaseQty(baseQty);
                        level2Elm.setUnitRatio(unitRatio);
                        level2Elm.setPrice(price);
                        level2Elm.setAmt(amt);
                        level2Elm.setDistriPrice(distriPrice);
                        level2Elm.setDistriAmt(distriAmt);
                        level2Elm.setMinQty(minQty);
                        level2Elm.setMaxQty(maxQty);
                        level2Elm.setMulQty(mulQty);
                        level2Elm.setPunitUdLength(punitUdLength);
                        level2Elm.setSelected(selected);
                        level2Elm.setIsNewGoods(isNewGoods);
                        level2Elm.setIsHotGoods(isHotGoods);
                        //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                        level2Elm.setBaseUnitUdLength(shoppingCart.get("BASEUNITUDLENGTH").toString());
                        level2Elm.setWarningQty(warningQty);
                        
                        level1Elm.getPluList().add(level2Elm);
                    }else {
                        DCP_ShoppingCartDetailRes.level3Elm level3Elm = res.new level3Elm();
                        
                        // 拼接图片地址
                        if(!Check.Null(listImage)){
                            if (DomainName.endsWith("/"))
                            {
                                level3Elm.setListImage(httpStr+DomainName+"resource/image/" +listImage);
                            }
                            else
                            {
                                level3Elm.setListImage(httpStr+DomainName+"/resource/image/" +listImage);
                            }
                        }else{
                            level3Elm.setListImage("");
                        }
                        level3Elm.setItem(item);
                        level3Elm.setPluNo(pluNo);
                        level3Elm.setPluType(pluType);
                        level3Elm.setMaxOrderSpec(maxOrderSpec);
                        level3Elm.setPluName(pluName);
                        level3Elm.setFeatureNo(featureNo);
                        level3Elm.setFeatureName(featureName);
                        level3Elm.setPunit(punit);
                        level3Elm.setPunitName(punitName);
                        level3Elm.setPqty(pqty);
                        level3Elm.setBaseUnit(baseUnit);
                        level3Elm.setBaseUnitName(baseUnitName);
                        level3Elm.setBaseQty(baseQty);
                        level3Elm.setUnitRatio(unitRatio);
                        level3Elm.setPrice(price);
                        level3Elm.setAmt(amt);
                        level3Elm.setDistriPrice(distriPrice);
                        level3Elm.setDistriAmt(distriAmt);
                        level3Elm.setMinQty(minQty);
                        level3Elm.setMaxQty(maxQty);
                        level3Elm.setMulQty(mulQty);
                        level3Elm.setPunitUdLength(punitUdLength);
                        level3Elm.setSelected(selected);
                        //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                        level3Elm.setBaseUnitUdLength(shoppingCart.get("BASEUNITUDLENGTH").toString());
                        level3Elm.setWarningQty(warningQty);
                        
                        level1Elm.getInvalidPluList().add(level3Elm);
                    }
                    
                }
                
            }else{
                level1Elm.setPluList(new ArrayList<DCP_ShoppingCartDetailRes.level2Elm>());
                level1Elm.setInvalidPluList(new ArrayList<DCP_ShoppingCartDetailRes.level3Elm>());
                level1Elm.setTotAmt("0");
                level1Elm.setTotCqty("0");
                level1Elm.setTotPqty("0");
                level1Elm.setTotDistriAmt("0");
            }
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败:" + e.getMessage());
        }
        
        return res;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_ShoppingCartDetailReq req) throws Exception {
        String opNO = req.getOpNO();
        String sql = "";
        //String companyId = req.getBELFIRM();
        String shopId = req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf = new StringBuffer("");
        String org_form = req.getOrg_Form(); // 0:公司  2:门店

        /*if(org_form.equals("0")){
            companyId = shopId;
        }else if(Check.Null(companyId)||companyId.equals("null")){
            // 查询下组织表 在服务器上回出现 Token 中的 request 的 belfirm 为null 的情况 本地有值
            sql = "select BELFIRM from DCP_ORG where EID = '"+eId+"' and ORG_FORM = '2' and ORGANIZATIONNO = '"+shopId+"'";
            List<Map<String, Object>> getBelfirm = this.doQueryData(sql, null);
            if(!org.apache.cxf.common.util.CollectionUtils.isEmpty(getBelfirm)){
                companyId =  getBelfirm.get(0).get("BELFIRM").toString();
            }
        }*/

        /*// 商品模板表
        sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                //and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店
                + " where a.eid='"+eId+"' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 "
                + " )"
                + " ");*/
        
        
        
        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')"
                + " )");
        
        sqlbuf.append(" SELECT NVl(j.totPqty, 0) AS totPqty , NVL(j.totCqty, 0) AS totCqty, NVl(j.totAmt,0) totAmt,NVl(j.totDistriAmt,0) totDistriAmt, " +
                " a.item, b.LISTIMAGE,c.PLUTYPE, c.MAXORDERSPEC, a.PLUNO, d.PLU_NAME , a.FEATURENO, e.FEATURENAME, a.PUNIT, f.UNAME AS punitName, " +
                " a.PQTY , a.BASEUNIT, g.UNAME, a.BASEQTY, a.UNIT_RATIO, a.PRICE , a.AMT, a.DISTRIPRICE, a.DISTRIAMT, a.SELECTED,  " +
                " h.minQTY , h.MAXQTY, h.mulQty,h.warningqty, i.UDLENGTH,h.CANREQUIRE,h.isNewGoods,c.ISHOTGOODS,bul.udlength as baseunitudlength " +
                " FROM DCP_SHOPPINGCART a " +
                " LEFT JOIN DCP_GOODSIMAGE b ON a.EID = b.EID AND a.PLUNO = b.PLUNO AND b.APPTYPE = 'ALL' " +
                " LEFT JOIN DCP_GOODS c ON a.EID = c.EID AND a.PLUNO = c.PLUNO " +
                " LEFT JOIN DCP_GOODS_LANG d ON a.EID = d.EID AND a.PLUNO = d.PLUNO AND d.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODS_FEATURE_LANG e ON a.EID = e.EID AND a.PLUNO = e.PLUNO AND a.FEATURENO = e.FEATURENO AND e.LANG_TYPE='"+langType+"' " +
                " LEFT JOIN DCP_UNIT_LANG f ON a.EID = f.EID AND a.PUNIT = f.UNIT AND f.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_UNIT_LANG g ON a.EID = g.EID AND a.BASEUNIT = g.UNIT AND g.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN goodstemplate h ON a.EID = h.EID AND a.PLUNO = h.PLUNO " +
                " LEFT JOIN DCP_UNIT i ON a.EID = i.EID AND a.PUNIT = i.UNIT AND i.STATUS = '100'   " +
                //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0 by jinzma 20221107
                " LEFT JOIN DCP_UNIT bul ON a.EID = bul.EID AND a.BASEUNIT = bul.UNIT " +
                " LEFT JOIN ( " +
                " SELECT a1.EID, a1.SHOPID, OPNO, sum(a1.PQTY) AS totPqty , count(a1.ITEM) AS totCqty, sum(a1.AMT) AS totAmt , sum(a1.DISTRIAMT) AS totDistriAmt " +
                " FROM DCP_SHOPPINGCART a1  " +
                " INNER JOIN goodstemplate b1 ON a1.EID = b1.EID AND a1.PLUNO = b1.pluno AND b1.CANREQUIRE = 'Y' " +
                " WHERE SELECTED = '1' GROUP BY a1.EID, a1.SHOPID, a1.OPNO ) j ON a.EID = j.eId AND a.SHOPID = j.SHOPID AND a.OPNO = j.OPNO" +
                " WHERE a.OPNO = '"+opNO+"' AND a.EID = '"+eId+"' AND a.shopId = '"+shopId+"' " +
                " ORDER BY a.ITEM DESC");
        sql = sqlbuf.toString();
        return sql;
    }
}
