package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_ShoppingCartUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ShoppingCartUpdateRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 购物车商品修改
 * @author: wangzyc
 * @create: 2021-05-27
 */
public class DCP_ShoppingCartUpdate extends SPosAdvanceService<DCP_ShoppingCartUpdateReq, DCP_ShoppingCartUpdateRes> {
    @Override
    protected void processDUID(DCP_ShoppingCartUpdateReq req, DCP_ShoppingCartUpdateRes res) throws Exception {
        /**
         * 用户编辑商品数量、修改选中状态，实时调用服务 DCP_ShoppingCartUpdate
         */
        try {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 创建时间格式化
            String eId = req.geteId();
            String shopId = req.getShopId();
            
            String opNO = req.getOpNO();
            String opName = req.getOpName();
            String lastmodiTime = dfs.format(new Date());
            
            List<DCP_ShoppingCartUpdateReq.level2Elm> pluList = req.getRequest().getPluList();
            
            String[] columns = {"EID", "SHOPID", "OPNO", "ITEM", "PLUNO", "FEATURENO", "PUNIT", "PQTY", "BASEUNIT", "BASEQTY",
                    "UNIT_RATIO", "PRICE", "AMT", "DISTRIPRICE", "DISTRIAMT", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME", "SELECTED"};
            
            Integer itemDB = 0;
            Set<String> items = new HashSet<>(); // 用来存储 购物车的item  方便后面的检索
            
            if (!CollectionUtils.isEmpty(pluList)) {
                
                for (DCP_ShoppingCartUpdateReq.level2Elm level2Elm : pluList) {
                    String pqty = level2Elm.getPqty();
                    String baseQty = level2Elm.getBaseQty();
                    String price = level2Elm.getPrice();
                    String distriPrice = level2Elm.getDistriPrice();
                    String baseUnit = level2Elm.getBaseUnit();
                    String featureNo = level2Elm.getFeatureNo();
                    String item = level2Elm.getItem();
                    String pluNo = level2Elm.getPluNo();
                    String unitRatio = level2Elm.getUnitRatio();
                    String punit = level2Elm.getPunit();
                    String selected = level2Elm.getSelected();
                    
                    // 查询库中购物车商品的 信息
                    StringBuffer sqlbuf = new StringBuffer("");
                    sqlbuf.append("SELECT * FROM DCP_SHOPPINGCART " +
                            " WHERE EID = '" + eId + "' AND SHOPID = '" + shopId + "' AND PLUNO = '" + pluNo + "' AND featureNo = '" + featureNo + "' " +
                            "  AND baseUnit = '" + baseUnit + "' AND OPNO = '" + opNO + "' ");
                    
                    List<Map<String, Object>> getInfo = this.doQueryData(sqlbuf.toString(), null);
                    
                    if (!CollectionUtils.isEmpty(getInfo)) {
                        Map<String, Object> info = getInfo.get(0);
                        String baseQtyDB = info.get("BASEQTY").toString();
                        double newBQty = 0; // 更新后的 基准单位数量
                        String newPQty = ""; // 根据 更新后的基准单位 换算 入参传入的 punit 后的数量
                        
                        if (Check.Null(item)) {
                            item = getInfo.get(0).get("ITEM").toString();
                        }
                        
                        newBQty = Double.parseDouble(baseQty);
                        
                        try {
                            // 单位换算 基准单位换算 要货单位
                            newPQty = PosPub.getUnitConvert(dao, eId, pluNo, baseUnit, punit, newBQty + "");
                        } catch (Exception e) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "要货购物车商品编号:"+pluNo+" 单位换算 基准单位:"+baseUnit+" 转化要货单位:"+punit+"异常！");
                        }
                        
                        // 计算零售总价 / 进货总价
                        BigDecimal newAmt = new BigDecimal(price).multiply(new BigDecimal(newPQty));
                        BigDecimal newDistriAmt = new BigDecimal(distriPrice).multiply(new BigDecimal(newPQty));
                        
                        UptBean ub1 = null;
                        ub1 = new UptBean("DCP_SHOPPINGCART");
                        //add Value
                        ub1.addUpdateValue("PUNIT", new DataValue(punit, Types.VARCHAR));
                        ub1.addUpdateValue("PQTY", new DataValue(newPQty, Types.VARCHAR));
                        ub1.addUpdateValue("BASEQTY", new DataValue(newBQty, Types.VARCHAR));
                        ub1.addUpdateValue("UNIT_RATIO", new DataValue(unitRatio, Types.VARCHAR));
                        ub1.addUpdateValue("PRICE", new DataValue(price, Types.VARCHAR));
                        ub1.addUpdateValue("AMT", new DataValue(newAmt, Types.VARCHAR));
                        ub1.addUpdateValue("DISTRIPRICE", new DataValue(distriPrice, Types.VARCHAR));
                        ub1.addUpdateValue("DISTRIAMT", new DataValue(newDistriAmt, Types.VARCHAR));
                        ub1.addUpdateValue("SELECTED", new DataValue(selected, Types.VARCHAR));
                        
                        ub1.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
                        ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
                        ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmodiTime, Types.DATE));
                        
                        //condition
                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        ub1.addCondition("OPNO", new DataValue(opNO, Types.VARCHAR));
                        ub1.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                        ub1.addCondition("BASEUNIT", new DataValue(baseUnit, Types.VARCHAR));
                        ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub1));
                        
                        items.add(item);
                    } else {
                        // 如果购物车中商品 不存在则新增 insert
                        
                        // 添加的时候 检核商品是否可要货 不可要货则提示 无效商品
                        Boolean aBoolean = checkCanrequire(pluNo, req);
                        if(!aBoolean){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "添加的商品无效！ ");
                        }
                        
                        // 如果ItemDB ==0 去库里查询下最大的item
                        if (itemDB == 0) {
                            sqlbuf.setLength(0);
                            sqlbuf.append(" select NVL(MAX(ITEM),0) ITEM FROM DCP_SHOPPINGCART WHERE EID = '" + eId + "' AND SHOPID = '" + shopId + "' " +
                                    " AND opNO = '" + opNO + "'");
                            List<Map<String, Object>> getItem = this.doQueryData(sqlbuf.toString(), null);
                            if (!CollectionUtils.isEmpty(getItem)) {
                                itemDB = Integer.parseInt(getItem.get(0).get("ITEM").toString());
                            }
                        }
                        itemDB++;
                        // 计算零售价合计 AMT = PRICE * pqty
                        BigDecimal amt = new BigDecimal(price).multiply(new BigDecimal(pqty));
                        // 计算进货价合计 DISTRIAMT = distriPrice * pqty
                        BigDecimal distriAmt = new BigDecimal(distriPrice).multiply(new BigDecimal(pqty));
                        
                        DataValue[] insValue1 = null;
                        insValue1 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(opNO, Types.VARCHAR),
                                new DataValue(itemDB, Types.VARCHAR), // Item
                                new DataValue(pluNo, Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(punit, Types.VARCHAR),
                                new DataValue(pqty, Types.VARCHAR),
                                new DataValue(baseUnit, Types.VARCHAR),
                                new DataValue(baseQty, Types.VARCHAR),
                                new DataValue(unitRatio, Types.VARCHAR),
                                new DataValue(price, Types.VARCHAR),
                                new DataValue(amt, Types.VARCHAR),
                                new DataValue(distriPrice, Types.VARCHAR),
                                new DataValue(distriAmt, Types.VARCHAR),
                                new DataValue(opNO, Types.VARCHAR),
                                new DataValue(opName, Types.VARCHAR),
                                new DataValue(lastmodiTime, Types.DATE),
                                new DataValue(selected, Types.VARCHAR)
                        };
                        
                        InsBean ib1 = new InsBean("DCP_SHOPPINGCART", columns);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                        
                        items.add(itemDB.toString());
                        
                    }
                }
            }
            this.doExecuteDataToDB();
            
            // *************************************** 查询下操作的商品 返回商品信息 Begin ****************
            DCP_ShoppingCartUpdateRes.level1Elm level1Elm = res.new level1Elm();
            res.setDatas(level1Elm);
            if (!CollectionUtils.isEmpty(items)) {
                
                String getShoppingInfoSQL = getShoppingInfoByItems(req, items);
                List<Map<String, Object>> getShoppingInfo = this.doQueryData(getShoppingInfoSQL, null);
                if (!CollectionUtils.isEmpty(getShoppingInfo)) {
                    Map<String, Object> getInfo = getShoppingInfo.get(0);
                    String totCqty = getInfo.get("TOTCQTY").toString();
                    String totDistriAmt = getInfo.get("TOTDISTRIAMT").toString();
                    
                    level1Elm.setTotCqty(totCqty);
                    level1Elm.setTotDistriAmt(totDistriAmt);
                    level1Elm.setPluList(new ArrayList<DCP_ShoppingCartUpdateRes.level2Elm>());
                    
                    for (Map<String, Object> ShoppingInfo : getShoppingInfo) {
                        DCP_ShoppingCartUpdateRes.level2Elm level2Elm = res.new level2Elm();
                        
                        level2Elm.setItem(ShoppingInfo.get("ITEM").toString());
                        level2Elm.setPqty(ShoppingInfo.get("PQTY").toString());
                        level2Elm.setBaseQty(ShoppingInfo.get("BASEQTY").toString());
                        level2Elm.setSelected(ShoppingInfo.get("SELECTED").toString());
                        
                        level1Elm.getPluList().add(level2Elm);
                    }
                }
            }
            // *************************************** 查询下操作的商品 返回商品信息 Eng ****************
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败:" + e.getMessage());
        }
        
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_ShoppingCartUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShoppingCartUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShoppingCartUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_ShoppingCartUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ShoppingCartUpdateReq.level1Elm request = req.getRequest();
        List<DCP_ShoppingCartUpdateReq.level2Elm> pluList = request.getPluList();
        if (CollectionUtils.isEmpty(pluList)) {
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        } else {
            for (DCP_ShoppingCartUpdateReq.level2Elm level2Elm : pluList) {
                if (Check.Null(level2Elm.getPluNo())) {
                    errMsg.append("品号不可为空值, ");
                    isFail = true;
                }
                
                if (Check.Null(level2Elm.getFeatureNo())) {
                    errMsg.append("特征码不可为空值, ");
                    isFail = true;
                }
                
                if (Check.Null(level2Elm.getPunit())) {
                    errMsg.append("要货单位不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getBaseUnit())) {
                    errMsg.append("基准单位不可为空值, ");
                    isFail = true;
                }
                
                if (Check.Null(level2Elm.getUnitRatio())) {
                    errMsg.append("单位换算率不可为空值, ");
                    isFail = true;
                }
                
                if (Check.Null(level2Elm.getPqty())) {
                    errMsg.append("要货单位要货量不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getBaseQty())) {
                    errMsg.append("基准单位要货量不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getPrice())) {
                    errMsg.append("零售价不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getDistriPrice())) {
                    errMsg.append("进货价不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getSelected())) {
                    errMsg.append("选中状态不可为空值, ");
                    isFail = true;
                }
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_ShoppingCartUpdateReq> getRequestType() {
        return new TypeToken<DCP_ShoppingCartUpdateReq>() {
        };
    }
    
    @Override
    protected DCP_ShoppingCartUpdateRes getResponseType() {
        return new DCP_ShoppingCartUpdateRes();
    }
    
    public String getShoppingInfoByItems(JsonBasicReq req, Set<String> items) throws Exception {
        String opNO = req.getOpNO();
        String sql = "";
        String companyId = req.getBELFIRM();
        String shopId = req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf = new StringBuffer("");
        String org_form = req.getOrg_Form(); // 0:公司  2:门店
        
        
        if (org_form.equals("0")) {
            companyId = shopId;
        } else if (Check.Null(companyId) || companyId.equals("null")) {
            // 查询下组织表 在服务器上回出现 Token 中的 request 的 belfirm 为null 的情况 本地有值
            sql = "select BELFIRM from DCP_ORG where EID = '" + eId + "' and ORG_FORM = '2' and ORGANIZATIONNO = '" + shopId + "'";
            List<Map<String, Object>> getBelfirm = StaticInfo.dao.executeQuerySQL(sql, null);
            HelpTools.writelog_fileName("*********** 要货购物车 查询公司 SQL：" + sql, "ShoppingCart");
            if (!CollectionUtils.isEmpty(getBelfirm)) {
                companyId = getBelfirm.get(0).get("BELFIRM").toString();
                
            }
        }
        
        
        // 商品模板表
       /* sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='" + companyId + "'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='" + shopId + "'"
                //and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店
                + " where a.eid='" + eId + "' and a.status='100' "
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
        
        sqlbuf.append(" SELECT NVL(j.totCqty, 0) AS totCqty , NVl(j.totDistriAmt, 0) AS totDistriAmt, a.ITEM , a.pqty, a.baseQty, a.selected  " +
                " FROM DCP_SHOPPINGCART a " +
                " LEFT JOIN ( " +
                " SELECT a1.EID, a1.SHOPID, OPNO, sum(a1.PQTY) AS totPqty , count(a1.ITEM) AS totCqty, sum(a1.AMT) AS totAmt , sum(a1.DISTRIAMT) AS totDistriAmt " +
                " FROM DCP_SHOPPINGCART a1  " +
                " INNER JOIN goodstemplate b1 ON a1.EID = b1.EID AND a1.PLUNO = b1.pluno AND b1.CANREQUIRE = 'Y' " +
                " WHERE SELECTED = '1' GROUP BY a1.EID, a1.SHOPID, a1.OPNO ) j ON a.EID = j.eId AND a.SHOPID = j.SHOPID AND a.OPNO = j.OPNO" +
                " WHERE a.OPNO = '" + opNO + "' AND a.EID = '" + eId + "' AND a.shopId = '" + shopId + "' ");
        
        if (!CollectionUtils.isEmpty(items)) {
            String temp_Item = PosPub.getArrayStrSQLIn(items.toArray(new String[items.size()]));
            sqlbuf.append(" AND a.ITEM IN (" + temp_Item + ")");
        }
        sqlbuf.append(" ORDER BY a.ITEM DESC");
        sql = sqlbuf.toString();
        HelpTools.writelog_fileName("*********** 要货购物车 查询 SQL：" + sql, "ShoppingCart");
        return sql;
    }
    
    /**
     * 检核该商品是否可要货
     *
     * @param pluno
     * @param req
     * @return
     */
    private Boolean checkCanrequire(String pluno, DCP_ShoppingCartUpdateReq req) throws Exception {
        String companyId = req.getBELFIRM();
        String shopId = req.getShopId();
        String eId = req.geteId();
        String org_form = req.getOrg_Form(); // 0:公司  2:门店
        StringBuffer sqlbuf = new StringBuffer("");
        
        if (org_form.equals("0")) {
            companyId = shopId;
        } else if (Check.Null(companyId) || companyId.equals("null")) {
            // 查询下组织表 在服务器上回出现 Token 中的 request 的 belfirm 为null 的情况 本地有值
            sqlbuf.append("select BELFIRM from DCP_ORG where EID = '" + eId + "' and ORG_FORM = '2' and ORGANIZATIONNO = '" + shopId + "'");
            List<Map<String, Object>> getBelfirm = this.doQueryData(sqlbuf.toString(), null);
            if (!org.apache.cxf.common.util.CollectionUtils.isEmpty(getBelfirm)) {
                companyId = getBelfirm.get(0).get("BELFIRM").toString();
            }
        }
        
        Boolean bool = false;
        sqlbuf.setLength(0);
        // 商品模板表
       /* sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='" + companyId + "'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='" + shopId + "'"
                //and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店
                + " where a.eid='" + eId + "' and a.status='100' "
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
                + " select b.CANREQUIRE from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')"
                + " and b.pluno='"+pluno+"' ");
        
        List<Map<String, Object>> isCanrequire = this.doQueryData(sqlbuf.toString(), null);
        
        if(!CollectionUtils.isEmpty(isCanrequire)){
            String iscanrequire = isCanrequire.get(0).get("CANREQUIRE").toString();
            if(iscanrequire.equals("Y")){
                // 可要货
                bool = true;
            }
        }
        
        return bool;
    }
}
