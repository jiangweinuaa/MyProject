package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeGoodsCreateReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeGoodsCreateRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeGoodsCreateRes.level1Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * 服务函数：DCP_SubStockTakeGoodsCreate
 * 服务说明：盘点子任务商品录入
 * @author jinzma
 * @since  2021-03-04
 */
public class DCP_SubStockTakeGoodsCreate extends SPosAdvanceService<DCP_SubStockTakeGoodsCreateReq, DCP_SubStockTakeGoodsCreateRes> {

    @Override
    protected void processDUID(DCP_SubStockTakeGoodsCreateReq req, DCP_SubStockTakeGoodsCreateRes res) throws Exception {
        String eId=req.geteId();
        String shopId=req.getShopId();
        String createBy = req.getOpNO();
        String createByName = req.getOpName();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        level1Elm datas = res.new level1Elm();
        try{
            String subStockTakeNo = req.getRequest().getSubStockTakeNo();
            String stockTakeNo = req.getRequest().getStockTakeNo();
            String docType = req.getRequest().getDocType();
            String taskWay = req.getRequest().getTaskWay();
            String isBTake = req.getRequest().getIsBTake();
            String goodsCreateId = req.getRequest().getGoodsCreateId();
            String pluNo = req.getRequest().getPluNo();
            String featureNo = req.getRequest().getFeatureNo();
            String pluBarCode = req.getRequest().getPluBarCode();
            String actionType = req.getRequest().getActionType();
            String punit = req.getRequest().getPunit();
            String baseUnit = req.getRequest().getBaseUnit();
            String unitRatio = req.getRequest().getUnitRatio();
            String location = req.getRequest().getLocation();
            String pqty = req.getRequest().getPqty();
            String baseQty = req.getRequest().getBaseQty();
            String warehouse = req.getRequest().getWarehouse();
            String price = req.getRequest().getPrice();
            String refBaseQty = getStockBaseQty(eId,shopId,pluNo,featureNo,warehouse);

            String sql=" select status,importstatus from dcp_substocktake "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and substocktakeno='"+subStockTakeNo+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                String status = getQData.get(0).get("STATUS").toString();  //0：新建（待盘点）； 2：已确定
                String importStatus = getQData.get(0).get("IMPORTSTATUS").toString(); //0：未导入；100：已导入
                if (status.equals("0") && importStatus.equals("0")) {
                    getQData.clear();
                    sql = " select * from dcp_substocktake_detailtrack "
                            + " where eid='" + eId + "' and shopid='" + shopId + "' and substocktakeno='" + subStockTakeNo + "' and goodscreateid='" + goodsCreateId + "' ";
                    getQData = this.doQueryData(sql, null);
                    if (getQData == null || getQData.isEmpty()) {
                        //添加库存盘点子任务明细历程数据
                        String[] columnsTrack = {
                                "EID", "SHOPID", "SUBSTOCKTAKENO", "ACTIONTYPE", "PLUNO", "FEATURENO", "PLUBARCODE",
                                "LOCATION", "PUNIT", "PQTY", "BASEUNIT", "BASEQTY", "UNIT_RATIO",
                                "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME",
                                "REF_BASEQTY", "GOODSCREATEID", "PRICE"
                        };
                        DataValue[] insValueTrack = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(subStockTakeNo, Types.VARCHAR),
                                new DataValue(actionType, Types.VARCHAR),
                                new DataValue(pluNo, Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(pluBarCode, Types.VARCHAR),
                                new DataValue(location, Types.VARCHAR),
                                new DataValue(punit, Types.VARCHAR),
                                new DataValue(pqty, Types.VARCHAR),
                                new DataValue(baseUnit, Types.VARCHAR),
                                new DataValue(baseQty, Types.VARCHAR),
                                new DataValue(unitRatio, Types.VARCHAR),
                                new DataValue(createBy, Types.VARCHAR),
                                new DataValue(createByName, Types.VARCHAR),
                                new DataValue(createTime, Types.DATE),
                                new DataValue(refBaseQty, Types.VARCHAR),
                                new DataValue(goodsCreateId, Types.VARCHAR),
                                new DataValue(price, Types.VARCHAR),
                        };
                        InsBean ibTrack = new InsBean("DCP_SUBSTOCKTAKE_DETAILTRACK", columnsTrack);
                        ibTrack.addValues(insValueTrack);
                        this.addProcessData(new DataProcessBean(ibTrack));

                        getQData.clear();
                        sql = " select * from dcp_substocktake_detail"
                                + " where eid='" + eId + "' and shopid='" + shopId + "' and substocktakeno='" + subStockTakeNo + "' and pluno='" + pluNo + "' and featureno='" + featureNo + "'";
                        getQData = this.doQueryData(sql, null);

                        // dcp_substocktake_detail是否为空判断，为空则新增
                        if (getQData == null || getQData.isEmpty()) {
                            //获取最大项次
                            sql = " select max(item) as item from dcp_substocktake_detail"
                                    + " where eid='" + eId + "' and shopid='" + shopId + "' and substocktakeno='" + subStockTakeNo + "' ";
                            getQData.clear();
                            getQData = this.doQueryData(sql, null);
                            String item = getQData.get(0).get("ITEM").toString();
                            if (Check.Null(item)) {
                                item = "1";
                            } else {
                                item = String.valueOf(Integer.parseInt(item) + 1);
                            }

                            sql=" select a.cunit,a.baseunit,b.unitratio,c.udlength from dcp_goods a"
                                    + " inner join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.cunit=b.ounit and a.baseunit=b.unit"
                                    + " left  join dcp_unit c on a.eid=c.eid and a.cunit=c.unit"
                                    + " where a.eid='"+eId+"' and a.pluno='"+pluNo+"' ";
                            getQData.clear();
                            getQData = this.doQueryData(sql, null);
                            if (getQData == null || getQData.isEmpty()){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品库存单位转换率查询失败!");
                            }
                            String getCunit = getQData.get(0).get("CUNIT").toString();
                            String getCunitUnitRatio = getQData.get(0).get("UNITRATIO").toString();
                            String getUdLength = getQData.get(0).get("UDLENGTH").toString();
                            if (!PosPub.isNumeric(getUdLength))
                                getUdLength="0";

                            if (!punit.equals(getCunit)){
                                //BigDecimal pqty_b = new BigDecimal(pqty);
                                //pqty_b = pqty_b.multiply(unitRatio_b.divide(getCunitUnitRatio_b,6,BigDecimal.ROUND_HALF_UP));
                                //pqty_b = pqty_b.setScale(Integer.parseInt(getUdLength),BigDecimal.ROUND_HALF_UP);

                                BigDecimal unitRatio_b = new BigDecimal(unitRatio);
                                BigDecimal getCunitUnitRatio_b = new BigDecimal(getCunitUnitRatio);

                                //每次更新时，由DCP_SUBSTOCKTAKE_DETAIL.BASEQTY根据换算率反算后更新 by jinzma 20210324
                                BigDecimal baseQty_b = new BigDecimal(baseQty);
                                BigDecimal pqty_b = baseQty_b.divide(getCunitUnitRatio_b,Integer.parseInt(getUdLength),BigDecimal.ROUND_HALF_UP);

                                //此处取单价用了简单方案，考虑效能 1* (193.33/3.3333)
                                BigDecimal price_b = new BigDecimal(price);
                                price_b = getCunitUnitRatio_b.multiply(price_b.divide(unitRatio_b,6,BigDecimal.ROUND_HALF_UP));
                                price_b = price_b.setScale(2,BigDecimal.ROUND_HALF_UP);

                                pqty = pqty_b.toPlainString();
                                price = price_b.toPlainString();
                                punit = getCunit;
                                unitRatio = getCunitUnitRatio;
                            }

                            //新增DCP_SUBSTOCKTAKE_DETAIL
                            String[] columnsDetail = {
                                    "EID", "SHOPID", "SUBSTOCKTAKENO", "ITEM", "PLUNO", "FEATURENO", "PUNIT", "PQTY", "BASEUNIT",
                                    "BASEQTY", "UNIT_RATIO", "REF_BASEQTY", "PRICE",
                                    "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME"
                            };
                            DataValue[] insValueDetail = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(shopId, Types.VARCHAR),
                                    new DataValue(subStockTakeNo, Types.VARCHAR),
                                    new DataValue(item, Types.VARCHAR),
                                    new DataValue(pluNo, Types.VARCHAR),
                                    new DataValue(featureNo, Types.VARCHAR),
                                    new DataValue(punit, Types.VARCHAR),
                                    new DataValue(pqty, Types.VARCHAR),
                                    new DataValue(baseUnit, Types.VARCHAR),
                                    new DataValue(baseQty, Types.VARCHAR),
                                    new DataValue(unitRatio, Types.VARCHAR),
                                    new DataValue(refBaseQty, Types.VARCHAR),
                                    new DataValue(price, Types.VARCHAR),
                                    new DataValue(createBy, Types.VARCHAR),
                                    new DataValue(createByName, Types.VARCHAR),
                                    new DataValue(createTime, Types.DATE),
                            };
                            InsBean ibDetail = new InsBean("DCP_SUBSTOCKTAKE_DETAIL", columnsDetail);
                            ibDetail.addValues(insValueDetail);
                            this.addProcessData(new DataProcessBean(ibDetail));

                        } else {
                            String item = getQData.get(0).get("ITEM").toString();
                            //累加baseQty
                            String getBaseQty = getQData.get(0).get("BASEQTY").toString();
                            BigDecimal baseQty_b = new BigDecimal(baseQty);
                            baseQty_b = baseQty_b.add(new BigDecimal(getBaseQty));
                            baseQty = baseQty_b.toPlainString();
                            BigDecimal pqty_b = new BigDecimal(pqty);

                            //计算pqty
                            /*String getPqty = getQData.get(0).get("PQTY").toString();
                            String getPunit = getQData.get(0).get("PUNIT").toString();
                            String getUnitRatio = getQData.get(0).get("UNIT_RATIO").toString();
                            BigDecimal pqty_b = new BigDecimal(pqty);
                            if (!punit.equals(getPunit)) {
                                sql=" select udlength from dcp_unit a"
                                        + " where a.eid='"+eId+"' and a.unit='"+getPunit+"' ";
                                getQData.clear();
                                getQData = this.doQueryData(sql, null);
                                String getUdLength = "0";

                                if (getQData != null && !getQData.isEmpty()){
                                    getUdLength = getQData.get(0).get("UDLENGTH").toString();
                                    if (!PosPub.isNumeric(getUdLength))
                                        getUdLength="0";
                                }

                                //pqty 转换成getPunit对应的数量
                                BigDecimal unitRatio_b = new BigDecimal(unitRatio);
                                BigDecimal getUnitRatio_b = new BigDecimal(getUnitRatio);
                                pqty_b = pqty_b.multiply(unitRatio_b.divide(getUnitRatio_b, 6, BigDecimal.ROUND_HALF_UP));
                                pqty_b = pqty_b.setScale(Integer.parseInt(getUdLength),BigDecimal.ROUND_HALF_UP);
                            }
                            pqty_b = pqty_b.add(new BigDecimal(getPqty));
                            pqty = pqty_b.toPlainString();*/

                            //计算pqty
                            String getPunit = getQData.get(0).get("PUNIT").toString();
                            String getUnitRatio = getQData.get(0).get("UNIT_RATIO").toString();
                            BigDecimal getUnitRatio_b = new BigDecimal(getUnitRatio);
                            String getPqty = getQData.get(0).get("PQTY").toString();

                            if (!punit.equals(getPunit)) {
                                sql=" select udlength from dcp_unit a"
                                        + " where a.eid='"+eId+"' and a.unit='"+getPunit+"' ";
                                getQData.clear();
                                getQData = this.doQueryData(sql, null);
                                String getUdLength = "0";
                                if (getQData != null && !getQData.isEmpty()){
                                    getUdLength = getQData.get(0).get("UDLENGTH").toString();
                                    if (!PosPub.isNumeric(getUdLength))
                                        getUdLength="0";
                                }
                                //每次更新时，由DCP_SUBSTOCKTAKE_DETAIL.BASEQTY根据换算率反算后更新 by jinzma 20210324
                                pqty_b = baseQty_b.divide(getUnitRatio_b,Integer.parseInt(getUdLength),BigDecimal.ROUND_HALF_UP);
                            }else{
                                //单位相同，不通过反算，直接相加
                                pqty_b = pqty_b.add(new BigDecimal(getPqty));
                            }
                            pqty = pqty_b.toPlainString();

                            //修改DCP_SUBSTOCKTAKE_DETAIL
                            UptBean ubDetail = new UptBean("DCP_SUBSTOCKTAKE_DETAIL");
                            //add Value
                            ubDetail.addUpdateValue("PQTY", new DataValue(pqty, Types.VARCHAR));      // PQTY
                            ubDetail.addUpdateValue("BASEQTY", new DataValue(baseQty, Types.VARCHAR));   // BASEQTY
                            ubDetail.addUpdateValue("LASTMODIOPID", new DataValue(createBy, Types.VARCHAR));
                            ubDetail.addUpdateValue("LASTMODIOPNAME", new DataValue(createByName, Types.VARCHAR));
                            ubDetail.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));

                            //condition
                            ubDetail.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ubDetail.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ubDetail.addCondition("SUBSTOCKTAKENO", new DataValue(subStockTakeNo, Types.VARCHAR));
                            ubDetail.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ubDetail));
                        }

                        this.doExecuteDataToDB();

                        res.setDatas(datas);
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");

                    } else {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品录入唯一标识已存在!");
                    }
                }else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "库存盘点子任务已确定!");
                }
            }else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "库存盘点子任务不存在!");
            }
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeGoodsCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String docType = req.getRequest().getDocType();
        String taskWay = req.getRequest().getTaskWay();
        String isBTake = req.getRequest().getIsBTake();
        String goodsCreateId = req.getRequest().getGoodsCreateId();
        String pluNo = req.getRequest().getPluNo();
        String featureNo = req.getRequest().getFeatureNo();
        String actionType = req.getRequest().getActionType();
        String punit = req.getRequest().getPunit();
        String baseUnit = req.getRequest().getBaseUnit();
        String unitRatio = req.getRequest().getUnitRatio();
        String pqty = req.getRequest().getPqty();
        String baseQty = req.getRequest().getBaseQty();
        String price = req.getRequest().getPrice();
        String warehouse = req.getRequest().getWarehouse();

        if (Check.Null(subStockTakeNo)) {
            errMsg.append("盘点子任务单号不能为空,");
            isFail = true;
        }
        if (Check.Null(stockTakeNo)) {
            errMsg.append("盘点单号不能为空,");
            isFail = true;
        }
        if (Check.Null(docType)) {
            errMsg.append("盘点单类型不能为空,");
            isFail = true;
        }
        if (Check.Null(taskWay)) {
            errMsg.append("盘点方式不能为空,");
            isFail = true;
        }
        if (Check.Null(isBTake)) {
            errMsg.append("盲盘标志不能为空,");
            isFail = true;
        }
        if (Check.Null(goodsCreateId)) {
            errMsg.append("商品录入唯一标识不能为空,");
            isFail = true;
        }
        if (Check.Null(pluNo)) {
            errMsg.append("商品编号不能为空,");
            isFail = true;
        }
        if (Check.Null(featureNo)) {
            errMsg.append("特征码编号不能为空,");
            isFail = true;
        }
        if (Check.Null(actionType)) {
            errMsg.append("操作类型不能为空,");
            isFail = true;
        }
        if (Check.Null(punit)) {
            errMsg.append("盘点单位不能为空,");
            isFail = true;
        }
        if (Check.Null(baseUnit)) {
            errMsg.append("基准单位不能为空,");
            isFail = true;
        }
        if (Check.Null(unitRatio)) {
            errMsg.append("单位换算比率不能为空,");
            isFail = true;
        }else{
            if (!PosPub.isNumericType(unitRatio)){
                errMsg.append("单位换算比率必须为数值,");
                isFail = true;
            }
        }
        if (Check.Null(pqty)) {
            errMsg.append("盘点单位数量不能为空,");
            isFail = true;
        }else{
            //负数处理
            pqty =pqty.replaceFirst("-", "");
            if (!PosPub.isNumericType(pqty)){
                errMsg.append("盘点单位数量必须为数值,");
                isFail = true;
            }
        }
        if (Check.Null(baseQty)) {
            errMsg.append("基准单位数量不能为空,");
            isFail = true;
        }else{
            //负数处理
            baseQty = baseQty.replaceFirst("-", "");
            if (!PosPub.isNumericType(baseQty)){
                errMsg.append("基准单位数量必须为数值,");
                isFail = true;
            }
        }
        if (Check.Null(price)) {
            errMsg.append("零售价不能为空,");
            isFail = true;
        }else{
            if (!PosPub.isNumericType(price)){
                errMsg.append("零售价必须为数值,");
                isFail = true;
            }
        }
        if (Check.Null(warehouse)) {
            errMsg.append("来源盘点单仓库不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeGoodsCreateReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeGoodsCreateReq>(){};
    }

    @Override
    protected DCP_SubStockTakeGoodsCreateRes getResponseType() {
        return new DCP_SubStockTakeGoodsCreateRes();
    }

    //获取库存数量
    private String getStockBaseQty(String eId,String shopId,String pluNo,String featureNo,String warehouse) throws Exception{
        String baseqty="0";
        String sql = " "
                + " select sum(a.baseqty) as baseqty"
                + " from ("
                + " select a.pluno,a.featureno,a.baseunit,a.qty as baseqty from dcp_stock_day a"
                + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+warehouse+"' and a.pluno='"+pluNo+"' and a.featureno='"+featureNo+"'"
                + " union all"
                + " select a.pluno,a.featureno,a.baseunit,a.baseqty*a.stocktype as baseqty"
                + " from dcp_stock_detail a"
                + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+warehouse+"' and a.pluno='"+pluNo+"' and a.featureno='"+featureNo+"'"
                + " and billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42')"
                + " )a"
                + " ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        if (getQData != null && !getQData.isEmpty()) {
            baseqty = getQData.get(0).get("BASEQTY").toString();
        }
        if (Check.Null(baseqty)){
            baseqty = "0";
        }
        return baseqty;
    }


}
