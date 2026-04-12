package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PromImportGoodsCheckReq;
import com.dsc.spos.json.cust.res.DCP_PromImportGoodsCheckRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @description: 促销商品导入检测
 * @author: wangzyc
 * @create: 2021-06-24
 */
public class DCP_PromImportGoodsCheck extends SPosBasicService<DCP_PromImportGoodsCheckReq, DCP_PromImportGoodsCheckRes> {
    @Override
    protected boolean isVerifyFail(DCP_PromImportGoodsCheckReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        //必传值不为空
        DCP_PromImportGoodsCheckReq.level1Elm request = req.getRequest();
        List<DCP_PromImportGoodsCheckReq.level2Elm> goodsList = request.getGoodsList();
        String promCategory = request.getPromCategory();
        String codeTypeNo = request.getCodeTypeNo();


        if (Check.Null(codeTypeNo)) {
            errMsg.append("资料类型不可为空值, ");
            isFail = true;
        }

        if (Check.Null(promCategory)) {
            errMsg.append("促销类型不可为空值, ");
            isFail = true;
        }

        if (CollectionUtils.isEmpty(goodsList)) {
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PromImportGoodsCheckReq> getRequestType() {
        return new TypeToken<DCP_PromImportGoodsCheckReq>() {
        };
    }

    @Override
    protected DCP_PromImportGoodsCheckRes getResponseType() {
        return new DCP_PromImportGoodsCheckRes();
    }

    @Override
    protected DCP_PromImportGoodsCheckRes processJson(DCP_PromImportGoodsCheckReq req) throws Exception {
        /**
         * 处理逻辑  根据传入的资料 去检核品号 和条码
         */

        DCP_PromImportGoodsCheckRes res = null;
        res = this.getResponse();
        DCP_PromImportGoodsCheckRes.level1Elm level1Elm = res.new level1Elm();
        res.setDatas(level1Elm);
        String eId = req.geteId();
        String langType = req.getLangType(); // 拿到当前的语言别
        DCP_PromImportGoodsCheckReq.level1Elm request = req.getRequest();
        String codeTypeNo = request.getCodeTypeNo(); // 资料类型0-条码3-编码，特价作业为编码
        String promCategory = request.getPromCategory();    // 促销类型：SHOP_TJ：特价 originalPrice需大于等于0； specialPrice需大于等于0；
        List<DCP_PromImportGoodsCheckReq.level2Elm> goodsList = request.getGoodsList();
        String codeType = "";
        if (codeTypeNo.equals("0")) {
            codeType = "条码";
        } else if (codeTypeNo.equals("3")) {
            codeType = "编码";
        }

        try {
            level1Elm.setRightGoodsList(new ArrayList<DCP_PromImportGoodsCheckRes.level2Elm>());
            level1Elm.setErrorGoodsList(new ArrayList<DCP_PromImportGoodsCheckRes.level3Elm>());
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> data = this.doQueryData(sql, null);
            Set<String> codes = new HashSet<>();
            Set<String> unitIds = new HashSet<>();
            StringBuffer sqlbuf = new StringBuffer("");
            for (DCP_PromImportGoodsCheckReq.level2Elm level2Elm : goodsList) {
                StringBuffer errBuf = new StringBuffer("");
                String code = level2Elm.getCode();
                String codeName = level2Elm.getCodeName();
                String unitId = level2Elm.getUnitId();
                String specialPricestr = level2Elm.getSpecialPrice();
                String originalPricestr = level2Elm.getOriginalPrice();

                float specialPrice = Float.parseFloat(Check.Null(specialPricestr) ? "0" : specialPricestr);
                float originalPrice = Float.parseFloat(Check.Null(originalPricestr) ? "0" : originalPricestr);

                boolean exist = false;  // 是否存在库里
                boolean pass = true;    // 是否通过检测

                for (Map<String, Object> oneData : data) {

                    String code2 = oneData.get("CODE").toString();
                    if (code2.equals(code)) {
                        exist = true;
                    }
                }

                if (!exist) {
                    errBuf.append(codeType + "不存在,");
                    pass = false;
                }

                if (Check.Null(codeName)) {
                    errBuf.append("名称为空,");
                    pass = false;
                }

                List<String> units = new ArrayList<>();//只能有一条记录，后面根据这个循环返回数据集


                if (promCategory.equals("SHOP_TJ")) {

                    if (specialPrice <= 0) {
                        errBuf.append("特价需大于等于0,");
                        pass = false;
                    }
                    if (originalPrice <= 0) {
                       //errBuf.append("原价需大于等于0,");
                        // pass = false;
                    }
                    if (Check.Null(unitId)) {
                        errBuf.append("特价时商品单位不可为空,");
                        pass = false;
                    } else {
                        // 20210908 应SA 需求增加 单位检核 单位存在于DCP_UNIT.UNIT中【当前企业】

                        sqlbuf.setLength(0);
                        sqlbuf = checkUnit(eId,code, unitId, langType);
                        List<Map<String, Object>> getUnit = this.doQueryData(sqlbuf.toString(), null);
                        String unitNo = "";//单位编码
                        if (!CollectionUtils.isEmpty(getUnit)) {
                            for (Map<String, Object> map : getUnit) {
                                String unit = map.get("UNIT").toString();
                                unitNo = unit;
                                units.add(unit);
                                break;
                            }
                        }

                        if (CollectionUtils.isEmpty(getUnit)) {
                            errBuf.append("特价时商品单位不可为空且需存在,");
                            pass = false;
                        } else {
                            if (exist) {
                                String code_unitId = code + unitId;
                                if (!unitIds.contains(code_unitId)) {
                                    unitIds.add(code_unitId);
                                } else {
                                    errBuf.append("特价时资料的" + codeType + "和单位不允许重复,");
                                    pass = false;
                                }

                                try
                                {
                                    //特价，检查导入的excel中商品单位对应的零售价与数据库中该单位对应的零售价是否一致。
                                    sqlbuf.setLength(0);
                                    sqlbuf = checkOriginalPrice_SHOP_TJ(eId, code,unitNo);
                                    List<Map<String, Object>> getUnitPirce = this.doQueryData(sqlbuf.toString(), null);
                                    if (!CollectionUtils.isEmpty(getUnitPirce))
                                    {
                                        String price_sunit = getUnitPirce.get(0).getOrDefault("PRICE_SUNIT","0").toString();
                                        String sunit = getUnitPirce.get(0).get("SUNIT").toString();
                                        String baseUnitRatio_sunit = getUnitPirce.get(0).getOrDefault("BASEUNITRATIO_SUNIT", "1").toString();//dcp_goods里面price对应的基准单位换算
                                        String oUnit_UnitRatio = getUnitPirce.get(0).getOrDefault("UNITRATIO", "").toString();
                                        BigDecimal price_sunit_b = new BigDecimal(price_sunit);// //dcp_goods.sunit---》对应的价格
                                        BigDecimal price = new BigDecimal("0"); //excel单位对应的数据库中零售价
                                        if (unitNo.equals(sunit))
                                        {
                                            price = price_sunit_b;
                                        }
                                        else
                                        {
                                            if (oUnit_UnitRatio.isEmpty())
                                            {
                                                errBuf.append("特价时商品单位换算率不存在,");
                                                pass = false;
                                            }
                                            else
                                            {
                                                try
                                                {

                                                    BigDecimal  baseUnitRatio_sunit_b = new BigDecimal(baseUnitRatio_sunit);
                                                    BigDecimal  oUnit_UnitRatio_b = new BigDecimal(oUnit_UnitRatio);

                                                    price = price_sunit_b.multiply(oUnit_UnitRatio_b).divide(baseUnitRatio_sunit_b,2, RoundingMode.HALF_UP);
                                                }
                                                catch (Exception e)
                                                {
                                                    // TODO: handle exception
                                                }
                                            }

                                        }

                                        BigDecimal originalPrice_b = new BigDecimal(originalPrice).setScale(2,RoundingMode.HALF_UP);
                                        price = price.setScale(2,RoundingMode.HALF_UP);
                                        if (originalPrice_b.compareTo(price)==0)
                                        {

                                        }
                                        else
                                        {
                                            //errBuf.append("特价时商品单位["+unitId+"]零售价="+originalPrice_b+"与标准零售价("+price+")不一致,");
                                            originalPricestr = price+"";
                                            //pass = false;
                                        }

                                    }

                                }
                                catch (Exception e)
                                {

                                }

                            }
                        }
                    }

                } else {
                    if (!codes.contains(code)) {
                        codes.add(code);
                    } else {
                        errBuf.append("资料" + codeType + "不允许重复,");
                        pass = false;
                    }
                }

                if (pass) {
                    // 通过检测
                    if (CollectionUtils.isEmpty(units)) {
                        DCP_PromImportGoodsCheckRes.level2Elm lv2 = res.new level2Elm();
                        lv2.setCode(code);
                        lv2.setCodeName(codeName);
                        lv2.setUnitId("");
                        lv2.setUnitName(unitId);
                        lv2.setSpecialPrice(specialPricestr);
                        lv2.setOriginalPrice(originalPricestr);
                        res.getDatas().getRightGoodsList().add(lv2);
                    } else {
                        for (String unit : units) {
                            DCP_PromImportGoodsCheckRes.level2Elm lv2 = res.new level2Elm();
                            lv2.setCode(code);
                            lv2.setCodeName(codeName);
                            lv2.setUnitId(unit);
                            lv2.setUnitName(unitId);
                            lv2.setSpecialPrice(specialPricestr);
                            lv2.setOriginalPrice(originalPricestr);
                            res.getDatas().getRightGoodsList().add(lv2);
                        }
                    }

                } else {
                    // 不通过
                    errBuf.deleteCharAt(errBuf.length() - 1);
                    errBuf.append("。");

                    if (CollectionUtils.isEmpty(units)) {
                        DCP_PromImportGoodsCheckRes.level3Elm lv3 = res.new level3Elm();
                        lv3.setCode(code);
                        lv3.setCodeName(codeName);
                        lv3.setUnitId("");
                        lv3.setUnitName(unitId);
                        lv3.setSpecialPrice(specialPricestr);
                        lv3.setOriginalPrice(originalPricestr);
                        lv3.setErrorDesc(errBuf.toString());
                        res.getDatas().getErrorGoodsList().add(lv3);
                    } else {
                        for (String unit : units) {
                            DCP_PromImportGoodsCheckRes.level3Elm lv3 = res.new level3Elm();
                            lv3.setCode(code);
                            lv3.setCodeName(codeName);
                            lv3.setUnitId(unit);
                            lv3.setUnitName(unitId);
                            lv3.setSpecialPrice(specialPricestr);
                            lv3.setOriginalPrice(originalPricestr);
                            lv3.setErrorDesc(errBuf.toString());
                            res.getDatas().getErrorGoodsList().add(lv3);
                        }
                    }
                }

            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_PromImportGoodsCheckReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        String eId = req.geteId();
        DCP_PromImportGoodsCheckReq.level1Elm request = req.getRequest();
        List<DCP_PromImportGoodsCheckReq.level2Elm> goodsList = request.getGoodsList();
        String codeTypeNo = request.getCodeTypeNo();// 资料类型0-条码3-编码，特价作业为编码

        List<String> codes = new ArrayList<>();
        goodsList.forEach(goods -> codes.add(goods.getCode()));
        String[] codes2 = codes.toArray(new String[codes.size()]);
        StringBuffer sb = new StringBuffer();

        String column = "";
        if (codeTypeNo.equals("0")) {
            column = "PLUBARCODE";
        } else if (codeTypeNo.equals("3")) {
            column = "PLUNO";
        }

        int inNum = 1; //已拼装IN条件数量
        for (int i = 0; i < codes2.length; i++) {
            String s = codes2[i];
            if (Check.Null(codes2[i])) continue;

            //这里不要犯低级错误而写成：if(i == codes2.length)
            if (i == (codes2.length - 1))
                sb.append("'" + codes2[i] + "'");    //SQL拼装，最后一条不加“,”。
            else if (inNum == 1000 && i > 0) {
                sb.append("'" + codes2[i] + "' ) OR " + column + " IN ( ");    //解决ORA-01795问题
                inNum = 1;
            } else {
                sb.append("'" + codes2[i] + "', ");
                inNum++;
            }

        }
        if (codeTypeNo.equals("0")) {
            // 条码
            sqlbuf.append("SELECT PLUBARCODE AS code FROM DCP_GOODS_BARCODE a WHERE a.eid  = '" + eId + "' AND a.PLUBARCODE IN (" + sb.toString() + ") ");
        } else if (codeTypeNo.equals("3")) {
            // 编码
            sqlbuf.append("SELECT PLUNO AS code FROM DCP_GOODS a WHERE a.eid  = '" + eId + "' AND a.PLUNO IN (" + sb.toString() + ") ");
        }

        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 单位存在于DCP_UNIT.UNIT中【当前企业】
     *
     * @param eid
     * @param pluNo
     * @param unit
     * @param langType
     * @return
     */
    private StringBuffer checkUnit(String eid,String pluNo, String unit, String langType) {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.UNIT,b.UNAME ,b.LANG_TYPE FROM DCP_UNIT a " +
                " LEFT JOIN DCP_UNIT_LANG b ON a.EID  = b.EID  AND a.UNIT  = b.UNIT  " +
                " LEFT JOIN dcp_goods_unit c on a.eid=c.eid and a.unit=c.ounit " +
                " WHERE a.EID  = '" + eid + "' AND b.UNAME  = '" + unit + "' AND C.PLUNO='"+pluNo+"' AND b.LANG_TYPE  = '" + langType + "' ");
        return sqlbuf;
    }


    /**
     * 特价促销时检查商品零售价与excel中导入的零售价
     * @param eid
     * @param pluNo
     * @return
     */
    private StringBuffer checkOriginalPrice_SHOP_TJ(String eid, String pluNo,String unit ) {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT  a.pluno,a.sunit,a.price price_sunit,b.unitratio as BASEUNITRATIO_SUNIT,c.unitratio FROM DCP_GOODS a " +
                " LEFT JOIN DCP_GOODS_UNIT b ON a.EID  = b.EID  AND a.pluno  = b.pluno and a.sunit=b.ounit " +
                " LEFT JOIN DCP_GOODS_UNIT c ON a.EID  = c.EID  AND a.pluno  = c.pluno and c.ounit='"+unit+"' "+
                " WHERE a.EID  = '" + eid + "' AND a.pluno  = '" + pluNo + "' ");
        return sqlbuf;
    }

}
