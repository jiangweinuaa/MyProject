package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AfterCostCalChkProcessReq;
import com.dsc.spos.json.cust.res.DCP_AfterCostCalChkProcessRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_AfterCostCalChkProcess extends SPosBasicService<DCP_AfterCostCalChkProcessReq, DCP_AfterCostCalChkProcessRes> {

    @Override
    protected boolean isVerifyFail(DCP_AfterCostCalChkProcessReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_AfterCostCalChkProcessReq> getRequestType() {
        return new TypeToken<DCP_AfterCostCalChkProcessReq>() {
        };
    }

    @Override
    protected DCP_AfterCostCalChkProcessRes getResponseType() {
        return new DCP_AfterCostCalChkProcessRes();
    }

    @Override
    protected DCP_AfterCostCalChkProcessRes processJson(DCP_AfterCostCalChkProcessReq req) throws Exception {

        //        1. 涉及表：DCP_CURINVCOSTSTAT 本期库存成本统计表
//        2. 条件：账套+法人+年度+期别+成本域+类型，按不同维度抓取对应差异来源单据；

//        3. 效能优化在 120 秒内
//        4. 后端做分页，每页先显示 100 行号数据；客户点击下一页查询后 100 行数据；

        DCP_AfterCostCalChkProcessRes res = this.getResponseType();

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);

        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到对应数据，无需检查");
        }
        res.setChkList(new ArrayList<>());
        //检查类型
        String type = req.getRequest().getBType();

        for (Map<String, Object> q : qData) {

            StringBuilder errorMsg = new StringBuilder();

            if ("1".equals(type) || StringUtils.isEmpty(type)) {
//        a. 【基础数据异常检查】
//        ⅰ.  【201】 检查采购单价是否为0，CURPURINQTY=0 或CURPURINAMT=0
                if (Double.parseDouble(q.get("CURPURINQTY").toString()) == 0
                        || Double.parseDouble(q.get("CURPURINAMT").toString()) == 0
                ) {
                    errorMsg.append("[201]");
                }
//        ⅱ. 【202】检查料件编号无产品分类，调用/DCP_GoodsSetQuery   pluno 关联对应category为空
                if (StringUtils.isEmpty(q.get("CATEGORY").toString())) {
                    errorMsg.append("[202]");
                }
//        ⅲ. 【203】检查投入与发料不相等，CURREWOOUTAMT本期返工领出金额+CURWOOUTAMT本期工单领用金额<>
//              DCP_DISWIPCOMPCOSTVARSTAT拆件在制组件成本期异动统计档中CURTRANSOUTAMT本期转出金额+VARTRANSOUTAMT差异转出金额、
//              DCP_WIPCOMPVARSTAT在制元件成本期异动统计档中CURTHISINPUTAMT本期本阶投入金额+CURNORETURNAMT本期一般退料成本+CUROVERTKREAMT本期超领退金额
                double curReWoOutAmt = Double.parseDouble(q.get("CURREWOOUTAMT").toString());
                double curTransOutAmt = Double.parseDouble(q.get("CURTRANSOUTAMT").toString())
                        + Double.parseDouble(q.get("VARTRANSOUTAMT").toString());
                double curThisInputAmt = Double.parseDouble(q.get("CURTHISINPUTAMT").toString())
                        + Double.parseDouble(q.get("CURNORETURNAMT").toString())
                        + Double.parseDouble(q.get("CUROVERTKREAMT").toString());
                if (curReWoOutAmt != curTransOutAmt || curThisInputAmt != curReWoOutAmt) {
                    errorMsg.append("[203]");
                }
//        ⅳ. 【204】检查销货有数量，无金额；CURSALESQTY<>0,CURSALESAMT=0
                if (Double.parseDouble(q.get("CURSALESQTY").toString()) != 0
                        && Double.parseDouble(q.get("CURSALESAMT").toString()) == 0
                ) {
                    errorMsg.append("[204]");
                }
//        ⅴ. 【205】检查销货有金额，无数量;CURSALESQTY=0,CURSALESAMT<>0
                if (Double.parseDouble(q.get("CURSALESQTY").toString()) == 0
                        && Double.parseDouble(q.get("CURSALESAMT").toString()) != 0
                ) {
                    errorMsg.append("[205]");
                }
//        ⅵ. 【206】检查发料单有数量无金额:CURWOOUTQTY<>0,CURWOOUTAMT=0
                if (Double.parseDouble(q.get("CURWOOUTQTY").toString()) != 0
                        && Double.parseDouble(q.get("CURWOOUTAMT").toString()) == 0
                ) {
                    errorMsg.append("[206]");
                }
//        ⅶ. 【207】检查发料单有金额没数量:CURWOOUTQTY=0,CURWOOUTAMT<>0
                if (Double.parseDouble(q.get("CURWOOUTQTY").toString()) == 0
                        && Double.parseDouble(q.get("CURWOOUTAMT").toString()) != 0
                ) {
                    errorMsg.append("[207]");
                }
//        ⅷ. 【208】检查返工领出有数量无金额:CURREWOOUTQTY<>0,CURREWOOUTAMT=0
                if (Double.parseDouble(q.get("CURREWOOUTQTY").toString()) != 0
                        && Double.parseDouble(q.get("CURREWOOUTAMT").toString()) == 0
                ) {
                    errorMsg.append("[208]");
                }
//        ⅸ. 【209】检查杂项领用有数量无金额:CURMISCOUTQTY<>0,CURMISCOUTAMT=0
                if (Double.parseDouble(q.get("CURMISCOUTQTY").toString()) != 0
                        && Double.parseDouble(q.get("CURMISCOUTAMT").toString()) == 0
                ) {
                    errorMsg.append("[209]");
                }
//        X. 【210】检查杂项领用有金额无数量:CURMISCOUTQTY=0,CURMISCOUTAMT<>0
                if (Double.parseDouble(q.get("CURMISCOUTQTY").toString()) == 0
                        && Double.parseDouble(q.get("CURMISCOUTAMT").toString()) != 0
                ) {
                    errorMsg.append("[210]");
                }


            }

            if ("2".equals(type) || StringUtils.isEmpty(type)) {
//        b. 【计算后结存量与结存金额检查】
//        ⅰ. 【211】检查本月结存有金额无数量：ENDINGBALQTY=0，ENDINGBALAMT<>0
                if (Double.parseDouble(q.get("ENDINGBALQTY").toString()) == 0
                        && Double.parseDouble(q.get("ENDINGBALAMT").toString()) != 0
                ) {
                    errorMsg.append("[211]");
                }
//        ⅱ. 【212】检查本月结存数量为负:ENDINGBALQTY<0
                if (Double.parseDouble(q.get("ENDINGBALQTY").toString()) < 0
                ) {
                    errorMsg.append("[212]");
                }
//        ⅲ. 【213】检查本月结存合计金额为负:ENDINGBALAMT<0
                if (Double.parseDouble(q.get("ENDINGBALAMT").toString()) < 0
                ) {
                    errorMsg.append("[213]");
                }
//        ⅳ. 【214】检查本月结存材料金额为负:ENDINGBAL_MAT<0
                if (Double.parseDouble(q.get("ENDINGBAL_MAT").toString()) < 0
                ) {
                    errorMsg.append("[214]");
                }
//        ⅴ. 【215】检查本月结存人工成本为负:ENDINGBAL_LABOR<0
                if (Double.parseDouble(q.get("ENDINGBAL_LABOR").toString()) < 0
                ) {
                    errorMsg.append("[215]");
                }
//        ⅵ. 【216】检查本月结存制费成本为负:ENDINGBAL_OH1-5  <0
                if (Double.parseDouble(q.get("ENDINGBAL_OH1").toString()) < 0
                        || Double.parseDouble(q.get("ENDINGBAL_OH2").toString()) < 0
                        || Double.parseDouble(q.get("ENDINGBAL_OH3").toString()) < 0
                        || Double.parseDouble(q.get("ENDINGBAL_OH4").toString()) < 0
                        || Double.parseDouble(q.get("ENDINGBAL_OH5").toString()) < 0
                ) {
                    errorMsg.append("[216]");
                }
//        ⅶ. 【217】检查本月结存加工成本为负:ENDINGBAL_OEM<0
                if (Double.parseDouble(q.get("ENDINGBAL_OEM").toString()) < 0
                ) {
                    errorMsg.append("[217]");
                }
//        ⅷ. 【218】检查本月结存数量与MFG之期末数量不合:调用：涉及表DCP_INVMTHCLOSING中 pluno 关联ENDQTY<>ENDINGBALQTY;注意账套启用成本域时需按成本域为关联；
//                if (Double.parseDouble(q.get("ENDINGBAL_OEM").toString()) < 0
//                ) {
//                    errorMsg.append("[217]");
//                }
//        ⅸ. 【219】检查本月平均单价为零：CURAVGPRICE=0
                if (Double.parseDouble(q.get("CURAVGPRICE").toString()) == 0
                ) {
                    errorMsg.append("[219]");
                }
//        X. 【220】检查本月材料平均单价为负：CURAVGPRICE_MAT<0
                if (Double.parseDouble(q.get("CURAVGPRICE_MAT").toString()) < 0
                ) {
                    errorMsg.append("[220]");
                }
//        Xⅰ. 【221】检查本月人工平均单价为负:CURAVGPRICE_LABOR<0
                if (Double.parseDouble(q.get("CURAVGPRICE_LABOR").toString()) < 0
                ) {
                    errorMsg.append("[221]");
                }
//        Xⅱ. 【222】检查本月制费平均单价为负:CURAVGPRICE_OH1_5  <0
                if (Double.parseDouble(q.get("CURAVGPRICE_OH1").toString()) < 0
                        || Double.parseDouble(q.get("CURAVGPRICE_OH2").toString()) < 0
                        || Double.parseDouble(q.get("CURAVGPRICE_OH3").toString()) < 0
                        || Double.parseDouble(q.get("CURAVGPRICE_OH4").toString()) < 0
                        || Double.parseDouble(q.get("CURAVGPRICE_OH5").toString()) < 0
                ) {
                    errorMsg.append("[222]");
                }
//        Xⅲ. 【223】检查本月加工平均单价为负:CURAVGPRICE_OEM<0
                if (Double.parseDouble(q.get("CURAVGPRICE_OEM").toString()) < 0
                ) {
                    errorMsg.append("[223]");
                }
            }

            if ("3".equals(type) || StringUtils.isEmpty(type)) {
//        c. 【在制工单异常检查】__>先将在制工单本期有余额的部分做筛选；涉及表：DCP_WIPMSTVARSTAT，查询异常时需显示单号
//        ⅰ. 【224】检查本月有工时,但无工单资料或已结案:涉及表：DCP_WOPRODREPSTAT 本期内有存在工单报工，但对应工单已在本期前结案或本期工单对应DCP_WIPMSTVARSTAT表中：上期結存金額+本期本階投入金額+本期下階投入金額=0
//        ⅱ. 【225】检查主件 本月转出>上月结存+本月投入:涉及表：本期工单对应DCP_WIPMSTVARSTAT表中本月转出数量>上月结存数量+本月投入数量
//        ⅲ. 检查主件 在制成本(主件)期末数量为零，金额不为零：ENDINGBALAMT<>0,ENDINGBALQTY=0【关联DCP_WIPMSTVARSTAT存在余额的主件】
//        ⅳ. 检查主件 在制成本(主件)期末金额为零，数量不为零：ENDINGBALAMT=0，ENDINGBALQTY<>0【【关联DCP_WIPMSTVARSTAT存在余额的主件】
//        ⅴ. 检查主件 在制成本(主件)期末金额为负:ENDINGBALAMT<0【关联DCP_WIPMSTVARSTAT存在余额的主件】
//        ⅵ. 在制成本(主件)期末材料金额为负:ENDINGBAL_MAT<0【关联DCP_WIPMSTVARSTAT存在余额的主件】
//        ⅶ. 在制成本(主件)期末人工金额为负:ENDINGBAL_LABOR<0【关联DCP_WIPMSTVARSTAT存在余额的主件】
//        ⅷ. 在制成本(主件)期末制费金额为负:ENDINGBAL_OH1_5 <0【关联DCP_WIPMSTVARSTAT存在余额的主件】
//        ⅸ. 在制成本(主件)期末加工金额为负:ENDINGBAL_OEM<0【关联DCP_WIPMSTVARSTAT存在余额的主件】
//        X. 主件(期初+投入)量=0,但(期初+投入)金额<>0:上月结存数量+本月投入数量 =0,上月结存金额+本月投入金额<>0涉及表：DCP_WIPMSTVARSTAT
//        Xⅰ. 检查主件 在制转出(主件)金额为负值:涉及表：DCP_WIPMSTVARSTAT，CURTRANSOUTAMT<0
//        Xⅱ. 在制主件期初不等于上月期末，差异为***::涉及表：DCP_WIPMSTVARSTAT，本期LASTBALAMT<> 上期ENDINGBALAMT，差异金额：本期LASTBALAMT_ 上期ENDINGBALAMT按工单关联，需显示差异金额
//        Xⅲ. 在制投入(主件)金额不等于在制投入(元件)金额，差异为***：涉及表：DCP_WIPMSTVARSTAT中CURTHISINPUTAMT+CURNORETURNAMT本期<> DCP_WIPCOMPVARSTAT表中CURTHISINPUTAMT+CURNORETURNAMT，差异金额：主件-元件，按工单关联，需显示差异金额
//        Xⅳ. 在制转出(主件)金额不等于在制转出(元件)金额，差异为***：涉及表：DCP_WIPMSTVARSTAT中CURTRANSOUTAMT本期<> DCP_WIPCOMPVARSTAT表中CURTRANSOUTAMT，差异金额：主件-元件，按工单关联，需显示差异金额
//        Xⅴ. 在制期初(主件)金额不等于在制期初(元件)金额，差异为***：涉及表：DCP_WIPMSTVARSTAT中LASTBALAMT<> DCP_WIPCOMPVARSTAT表中LASTBALAMT，差异金额：主件-元件，按工单关联，需显示差异金额
//        Xⅵ. 在制期未(主件)金额不等于在制期未(元件)金额，差异为***：涉及表：DCP_WIPMSTVARSTAT中ENDINGBALAMT<> DCP_WIPCOMPVARSTAT表中ENDINGBALAMT，差异金额：主件-元件，按工单关联，需显示差异金额
//        Xⅶ. 在制差异(主件)金额不等于在制差异(元件)金额，差异为***：涉及表：DCP_WIPMSTVARSTAT中VARTRANSOUTAMT本期<> DCP_WIPCOMPVARSTAT表中VARTRANSOUTAMT，差异金额：主件-元件，按工单关联，需显示差异金额

            }
            if (errorMsg.length() > 1){
              DCP_AfterCostCalChkProcessRes.ChkList oneChkList = res.new ChkList();
                oneChkList.setCorp(q.get("CORP").toString());
                oneChkList.setCorpName(q.get("CORPNAME").toString());
                oneChkList.setYear(q.get("YEAR").toString());
                oneChkList.setPeriod(q.get("PERIOD").toString());
                oneChkList.setAccountID(q.get("ACCOUNTID").toString());
                oneChkList.setAccount(q.get("ACCOUNT").toString());
                oneChkList.setPluNo(q.get("PLUNO").toString());
                oneChkList.setPluName(q.get("PLUNAME").toString());
                oneChkList.setFeatureNo(q.get("FEATURENO").toString());
                oneChkList.setErrorMessage(errorMsg.toString());

//                private String bType;
//                private String bNo;

              res.getChkList().add(oneChkList);
            }
        }

        res.setSuccess(true);


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_AfterCostCalChkProcessReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append("select a.*,b.CATEGORY ")
                .append(" FROM DCP_CURINVCOSTSTAT a")
                .append(" LEFT JOIN DCP_GOODS b on a.eid=b.eid and a.PLUNO=b.PLUNO ")
        ;
        sb.append(" WHERE a.cust_id='").append(req.geteId()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" AND a.corp='").append(req.getRequest().getCorp()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" AND a.year='").append(req.getRequest().getYear()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" AND a.period='").append(req.getRequest().getPeriod()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" AND a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }


        return sb.toString();
    }
}
