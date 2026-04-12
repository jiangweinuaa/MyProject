package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ShoppingCartAddReq;
import com.dsc.spos.json.cust.res.DCP_ShoppingCartAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 购物车商品添加
 * @author: wangzyc
 * @create: 2021-05-27
 */
public class DCP_ShoppingCartAdd extends SPosAdvanceService<DCP_ShoppingCartAddReq, DCP_ShoppingCartAddRes> {
    @Override
    protected void processDUID(DCP_ShoppingCartAddReq req, DCP_ShoppingCartAddRes res) throws Exception {
        try {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 创建时间格式化
            String eId = req.geteId();
            String shopId = req.getShopId();

            String opNO = req.getOpNO();
            String opName = req.getOpName();
            String lastmodiTime = dfs.format(new Date());

            DCP_ShoppingCartAddReq.level1Elm request = req.getRequest();
            List<DCP_ShoppingCartAddReq.level2Elm> pluList = request.getPluList();
            if(!CollectionUtils.isEmpty(pluList)){
                String[] columns = { "EID","SHOPID","OPNO","ITEM","PLUNO","FEATURENO","PUNIT","PQTY","BASEUNIT","BASEQTY",
                         "UNIT_RATIO","PRICE","AMT","DISTRIPRICE","DISTRIAMT","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","SELECTED" };

                StringBuffer sqlbuf = new StringBuffer("");
                sqlbuf.append(" select NVL(MAX(ITEM),0) ITEM FROM DCP_SHOPPINGCART WHERE EID = '"+eId+"' AND SHOPID = '"+shopId+"' " +
                        " AND opNO = '"+opNO+"'");
                List<Map<String, Object>> getItem = this.doQueryData(sqlbuf.toString(), null);
                Integer item = 0;
                if(!CollectionUtils.isEmpty(getItem)){
                    item = Integer.parseInt(getItem.get(0).get("ITEM").toString());
                }
                for (DCP_ShoppingCartAddReq.level2Elm level2Elm : pluList) {
                    item++;
                    String pluNo = level2Elm.getPluNo();
                    String featureNo = level2Elm.getFeatureNo();
                    String baseUnit = level2Elm.getBaseUnit();
                    String punit = level2Elm.getPunit();
                    String pqty = level2Elm.getPqty();
                    String baseQty = level2Elm.getBaseQty();
                    String unitRatio = level2Elm.getUnitRatio();
                    String price = level2Elm.getPrice();
                    String distriPrice = level2Elm.getDistriPrice();


                    // 先查询一下 购物车中是否已经有此商品
                    sqlbuf.setLength(0);
                    sqlbuf.append("SELECT * FROM DCP_SHOPPINGCART " +
                            " WHERE EID = '"+eId+"' AND SHOPID = '"+shopId+"' AND PLUNO = '"+pluNo+"' AND featureNo = '"+featureNo+"' " +
                            "  AND baseUnit = '"+baseUnit+"' AND OPNO = '"+opNO+"'");

                    List<Map<String, Object>> getInfo = this.doQueryData(sqlbuf.toString(), null);
                    if(CollectionUtils.isEmpty(getInfo)){

                        // 计算零售价合计 AMT = PRICE * pqty
                        BigDecimal amt = new BigDecimal(price).multiply(new BigDecimal(pqty));
                        // 计算进货价合计 DISTRIAMT = distriPrice * pqty
                        BigDecimal distriAmt = new BigDecimal(distriPrice).multiply(new BigDecimal(pqty));

                        // 购物车中不存在 直接 insert DCP_SHOPPINGCART
                        DataValue[] insValue1 = null;

                        insValue1 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(opNO, Types.VARCHAR),
                                new DataValue(item, Types.VARCHAR), // Item
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
                                new DataValue(1, Types.VARCHAR)
                        };

                        InsBean ib1 = new InsBean("DCP_SHOPPINGCART", columns);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                    }else{
                        // 购物车中存在 找到对应商品 数组增量修改 （若请求的punit与库里的punit不一致，需要按请求的punit重新根据基础单位数量换算出要货单位数量）
                        // 库中的 punit 以最近一次请求的 punit

                        Map<String, Object> info = getInfo.get(0);
                        String punitDB = info.get("PUNIT").toString();
                        String baseQtyDB = info.get("BASEQTY").toString();
                        double newBQty = 0; // 增量添加后的 基准单位数量
                        String newPQty = ""; // 根据 更新后的基准单位 换算 入参传入的 punit 后的数量

                        newBQty = Double.parseDouble(baseQty)+Double.parseDouble(baseQtyDB);

                        // 单位换算 基准单位换算 要货单位
                        newPQty = PosPub.getUnitConvert(dao, eId, pluNo, baseUnit, punit, newBQty+"");

                        // 计算零售总价 / 进货总价
                        BigDecimal newAmt = new BigDecimal(price).multiply(new BigDecimal(newPQty));
                        BigDecimal  newDistriAmt = new BigDecimal(distriPrice).multiply(new BigDecimal(newPQty));

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
                        this.addProcessData(new DataProcessBean(ub1));
                    }
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ShoppingCartAddReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShoppingCartAddReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShoppingCartAddReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(@Valid DCP_ShoppingCartAddReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ShoppingCartAddReq.level1Elm request = req.getRequest();
        List<DCP_ShoppingCartAddReq.level2Elm> pluList = request.getPluList();
        if(CollectionUtils.isEmpty(pluList)){
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        }else{
            for (DCP_ShoppingCartAddReq.level2Elm level2Elm : pluList) {
                if (Check.Null(level2Elm.getPluNo()))
                {
                    errMsg.append("品号不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(level2Elm.getFeatureNo()))
                {
                    errMsg.append("特征码不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(level2Elm.getPunit()))
                {
                    errMsg.append("要货单位不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getBaseUnit()))
                {
                    errMsg.append("基准单位不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(level2Elm.getUnitRatio()))
                {
                    errMsg.append("单位换算率不可为空值, ");
                    isFail = true;
                }

                if (Check.Null(level2Elm.getPqty()))
                {
                    errMsg.append("要货单位要货量不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getBaseQty()))
                {
                    errMsg.append("基准单位要货量不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getPrice()))
                {
                    errMsg.append("零售价不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(level2Elm.getDistriPrice()))
                {
                    errMsg.append("进货价不可为空值, ");
                    isFail = true;
                }
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ShoppingCartAddReq> getRequestType() {
        return new TypeToken<DCP_ShoppingCartAddReq>(){};
    }

    @Override
    protected DCP_ShoppingCartAddRes getResponseType() {
        return new DCP_ShoppingCartAddRes();
    }
}
