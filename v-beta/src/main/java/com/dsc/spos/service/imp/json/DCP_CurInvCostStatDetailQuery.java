package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CurInvCostStatDetilQueryReq;
import com.dsc.spos.json.cust.res.DCP_CurInvCostStatDetilQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DecimalFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CurInvCostStatDetailQuery extends SPosBasicService<DCP_CurInvCostStatDetilQueryReq, DCP_CurInvCostStatDetilQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_CurInvCostStatDetilQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CurInvCostStatDetilQueryReq> getRequestType() {
        return new TypeToken<DCP_CurInvCostStatDetilQueryReq>() {
        };
    }

    @Override
    protected DCP_CurInvCostStatDetilQueryRes getResponseType() {
        return new DCP_CurInvCostStatDetilQueryRes();
    }

    @Override
    protected DCP_CurInvCostStatDetilQueryRes processJson(DCP_CurInvCostStatDetilQueryReq req) throws Exception {
        DCP_CurInvCostStatDetilQueryRes res = this.getResponseType();
        String[] conditionValues1 = {}; //查詢條件
        List<Map<String, Object>> getData = this.doQueryData(getQuerySql(req), conditionValues1);
        res.setChkList(new ArrayList<>());
        if (getData != null && !getData.isEmpty()) {
            int priceDigit = 0;
            int amtDigit = 0;
//            DecimalFormatUtils priceFormat = new DecimalFormatUtils(priceDigit, RoundingMode.HALF_UP);
            DecimalFormatUtils amtFormat = new DecimalFormatUtils(amtDigit, RoundingMode.HALF_UP);
            for (Map<String, Object> data : getData) {
                DCP_CurInvCostStatDetilQueryRes.ChkList chkList = res.new ChkList();
                res.getChkList().add(chkList);

//                priceDigit = Integer.parseInt(data.get("COSTPRICEDIGIT").toString());
                amtDigit = Integer.parseInt(data.get("COSTAMOUNTDIGIT").toString());
//                priceFormat.setNumberDigit(priceDigit);
                amtFormat.setNumberDigit(amtDigit);

                chkList.setCorp(data.get("CORP").toString());
                chkList.setCorpName(data.get("CORPNAME").toString());
                chkList.setYear(data.get("YEAR").toString());
                chkList.setPeriod(data.get("PERIOD").toString());
                chkList.setAccountID(data.get("ACCOUNTID").toString());
                chkList.setAccount(data.get("ACCOUNT").toString());
                chkList.setCost_Calculation(data.get("COST_CALCULATION").toString());
                chkList.setBaseUnit(data.get("BASEUNIT").toString());
                chkList.setBaseUnitName(data.get("BASEUNITNAME").toString());
                chkList.setPluNo(data.get("PLUNO").toString());
                chkList.setPluName(data.get("PLUNAME").toString());
                chkList.setFeatureNo(data.get("FEATURENO").toString());
                chkList.setFeatureName(data.get("FEATURENAME").toString());
                chkList.setCostDomainID(data.get("COSTDOMAINID").toString());
                chkList.setCostDomainName(data.get("COSTDOMAINNAME").toString());
                chkList.setLastBalQty(data.get("LASTBALQTY").toString());
                chkList.setLastBalAmt(amtFormat.format(data.get("LASTBALAMT").toString()));
                chkList.setLastBalMat(amtFormat.format(data.get("LASTBAL_MAT").toString()));
                chkList.setLastBalLabor(amtFormat.format(data.get("LASTBAL_LABOR").toString()));
                chkList.setLastBalOem(amtFormat.format(data.get("LASTBAL_OEM").toString()));
                chkList.setLastBalOH1(amtFormat.format(data.get("LASTBAL_OH1").toString()));
                chkList.setLastBalOH2(amtFormat.format(data.get("LASTBAL_OH2").toString()));
                chkList.setLastBalOH3(amtFormat.format(data.get("LASTBAL_OH3").toString()));
                chkList.setLastBalOH4(amtFormat.format(data.get("LASTBAL_OH4").toString()));
                chkList.setLastBalOH5(amtFormat.format(data.get("LASTBAL_OH5").toString()));
                chkList.setCurPurInQty(data.get("CURPURINQTY").toString());

                chkList.setCurPurinAmt(amtFormat.format(data.get("CURPURINAMT").toString()));
                chkList.setCurPurinMat(amtFormat.format(data.get("CURPURIN_MAT").toString()));
                chkList.setCurPurinLabor(amtFormat.format(data.get("CURPURIN_LABOR").toString()));
                chkList.setCurPurinOem(amtFormat.format(data.get("CURPURIN_OEM").toString()));
                chkList.setCurPurinOH1(amtFormat.format(data.get("CURPURIN_OH1").toString()));
                chkList.setCurPurinOH2(amtFormat.format(data.get("CURPURIN_OH2").toString()));
                chkList.setCurPurinOH3(amtFormat.format(data.get("CURPURIN_OH3").toString()));
                chkList.setCurPurinOH4(amtFormat.format(data.get("CURPURIN_OH4").toString()));
                chkList.setCurPurinOH5(amtFormat.format(data.get("CURPURIN_OH5").toString()));
                chkList.setCurOutSourcInQty(data.get("CUROUTSOURCINQTY").toString());
                chkList.setCurOUtSourcInAmt(amtFormat.format(data.get("CUROUTSOURCINAMT").toString()));
                chkList.setCurOUtSourcInMat(amtFormat.format(data.get("CUROUTSOURCIN_MAT").toString()));
                chkList.setCurOUtSourcInLabor(amtFormat.format(data.get("CUROUTSOURCIN_LABOR").toString()));
                chkList.setCurOUtSourcInOem(amtFormat.format(data.get("CUROUTSOURCIN_OEM").toString()));
                chkList.setCurOUtSourcInOH1(amtFormat.format(data.get("CUROUTSOURCIN_OH1").toString()));
                chkList.setCurOUtSourcInOH2(amtFormat.format(data.get("CUROUTSOURCIN_OH2").toString()));
                chkList.setCurOUtSourcInOH3(amtFormat.format(data.get("CUROUTSOURCIN_OH3").toString()));
                chkList.setCurOUtSourcInOH4(amtFormat.format(data.get("CUROUTSOURCIN_OH4").toString()));
                chkList.setCurOUtSourcInOH5(amtFormat.format(data.get("CUROUTSOURCIN_OH5").toString()));
                chkList.setCurWOInQty(data.get("CURWOINQTY").toString());
                chkList.setCurWOInAmt(amtFormat.format(data.get("CURWOINAMT").toString()));
                chkList.setCurWOInMat(amtFormat.format(data.get("CURWOIN_MAT").toString()));
                chkList.setCurWOInLabor(amtFormat.format(data.get("CURWOIN_LABOR").toString()));
                chkList.setCurWOInOem(amtFormat.format(data.get("CURWOIN_OEM").toString()));
                chkList.setCurWOInOH1(amtFormat.format(data.get("CURWOIN_OH1").toString()));
                chkList.setCurWOInOH2(amtFormat.format(data.get("CURWOIN_OH2").toString()));
                chkList.setCurWOInOH3(amtFormat.format(data.get("CURWOIN_OH3").toString()));
                chkList.setCurWOInOH4(amtFormat.format(data.get("CURWOIN_OH4").toString()));
                chkList.setCurWOInOH5(amtFormat.format(data.get("CURWOIN_OH5").toString()));
                chkList.setCurReWOQty(data.get("CURREWOOUTQTY").toString());
                chkList.setCurReWOAmt(amtFormat.format(data.get("CURREWOOUTAMT").toString()));
                chkList.setCurReWOMat(amtFormat.format(data.get("CURREWOOUT_MAT").toString()));
                chkList.setCurReWOLabor(amtFormat.format(data.get("CURREWOOUT_LABOR").toString()));
                chkList.setCurReWOOem(amtFormat.format(data.get("CURREWOOUT_OEM").toString()));
                chkList.setCurReWOOH1(amtFormat.format(data.get("CURREWOOUT_OH1").toString()));
                chkList.setCurReWOOH2(amtFormat.format(data.get("CURREWOOUT_OH2").toString()));
                chkList.setCurReWOOH3(amtFormat.format(data.get("CURREWOOUT_OH3").toString()));
                chkList.setCurReWOOH4(amtFormat.format(data.get("CURREWOOUT_OH4").toString()));
                chkList.setCurReWOOH5(amtFormat.format(data.get("CURREWOOUT_OH5").toString()));
                chkList.setCurReWOInQty(data.get("CURREWOINQTY").toString());
                chkList.setCurReWOInAmt(amtFormat.format(data.get("CURREWOINAMT").toString()));
                chkList.setCurReWOInMat(amtFormat.format(data.get("CURREWOIN_MAT").toString()));
                chkList.setCurReWOInLabor(amtFormat.format(data.get("CURREWOIN_LABOR").toString()));
                chkList.setCurReWOInOem(amtFormat.format(data.get("CURREWOIN_OEM").toString()));
                chkList.setCurReWOInOH1(amtFormat.format(data.get("CURREWOIN_OH1").toString()));
                chkList.setCurReWOInOH2(amtFormat.format(data.get("CURREWOIN_OH2").toString()));
                chkList.setCurReWOInOH3(amtFormat.format(data.get("CURREWOIN_OH3").toString()));
                chkList.setCurReWOInOH4(amtFormat.format(data.get("CURREWOIN_OH4").toString()));
                chkList.setCurReWOInOH5(amtFormat.format(data.get("CURREWOIN_OH5").toString()));
                chkList.setCurMiscInQty(data.get("CURMISCINQTY").toString());
                chkList.setCurMiscInAmt(amtFormat.format(data.get("CURMISCINAMT").toString()));
                chkList.setCurMiscInMat(amtFormat.format(data.get("CURMISCIN_MAT").toString()));
                chkList.setCurMiscInLabor(amtFormat.format(data.get("CURMISCIN_LABOR").toString()));
                chkList.setCurMiscInOem(amtFormat.format(data.get("CURMISCIN_OEM").toString()));
                chkList.setCurMiscInOH1(amtFormat.format(data.get("CURMISCIN_OH1").toString()));
                chkList.setCurMiscInOH2(amtFormat.format(data.get("CURMISCIN_OH2").toString()));
                chkList.setCurMiscInOH3(amtFormat.format(data.get("CURMISCIN_OH3").toString()));
                chkList.setCurMiscInOH4(amtFormat.format(data.get("CURMISCIN_OH4").toString()));
                chkList.setCurMiscInOH5(amtFormat.format(data.get("CURMISCIN_OH5").toString()));
                chkList.setCurAdjInQty(data.get("CURADJINQTY").toString());
                chkList.setCurAdjInAmt(amtFormat.format(data.get("CURADJINAMT").toString()));
                chkList.setCurAdjInAmtMat(amtFormat.format(data.get("CURADJIN_MAT").toString()));
                chkList.setCurAdjInAmtLabor(amtFormat.format(data.get("CURADJIN_LABOR").toString()));
                chkList.setCurAdjInAmtOem(amtFormat.format(data.get("CURADJIN_OEM").toString()));
                chkList.setCurAdjInAmtOH1(amtFormat.format(data.get("CURADJIN_OH1").toString()));
                chkList.setCurAdjInAmtOH2(amtFormat.format(data.get("CURADJIN_OH2").toString()));
                chkList.setCurAdjInAmtOH3(amtFormat.format(data.get("CURADJIN_OH3").toString()));
                chkList.setCurAdjInAmtOH4(amtFormat.format(data.get("CURADJIN_OH4").toString()));
                chkList.setCurAdjInAmtOH5(amtFormat.format(data.get("CURADJIN_OH5").toString()));
                chkList.setCurCancelInQty(data.get("CURCANCELINQTY").toString());
                chkList.setCurCancelInAmt(amtFormat.format(data.get("CURCANCELINAMT").toString()));
                chkList.setCurCancelInMat(amtFormat.format(data.get("CURCANCELIN_MAT").toString()));
                chkList.setCurCancelInLabor(amtFormat.format(data.get("CURCANCELIN_LABOR").toString()));
                chkList.setCurCancelInOem(amtFormat.format(data.get("CURCANCELIN_OEM").toString()));
                chkList.setCurCancelInOH1(amtFormat.format(data.get("CURCANCELIN_OH1").toString()));
                chkList.setCurCancelInOH2(amtFormat.format(data.get("CURCANCELIN_OH2").toString()));
                chkList.setCurCancelInOH3(amtFormat.format(data.get("CURCANCELIN_OH3").toString()));
                chkList.setCurCancelInOH4(amtFormat.format(data.get("CURCANCELIN_OH4").toString()));
                chkList.setCurCancelInOH5(amtFormat.format(data.get("CURCANCELIN_OH5").toString()));
                chkList.setCurTransInQty(data.get("CURTRANSINQTY").toString());
                chkList.setCurTransInAmt(amtFormat.format(data.get("CURTRANSINAMT").toString()));
                chkList.setCurTransInMat(amtFormat.format(data.get("CURTRANSIN_MAT").toString()));
                chkList.setCurTransInLabor(amtFormat.format(data.get("CURTRANSIN_LABOR").toString()));
                chkList.setCurTransInOem(amtFormat.format(data.get("CURTRANSIN_OEM").toString()));
                chkList.setCurTransInOH1(amtFormat.format(data.get("CURTRANSIN_OH1").toString()));
                chkList.setCurTransInOH2(amtFormat.format(data.get("CURTRANSIN_OH2").toString()));
                chkList.setCurTransInOH3(amtFormat.format(data.get("CURTRANSIN_OH3").toString()));
                chkList.setCurTransInOH4(amtFormat.format(data.get("CURTRANSIN_OH4").toString()));
                chkList.setCurTransInOH5(amtFormat.format(data.get("CURTRANSIN_OH5").toString()));
                chkList.setCurAvgPrice(data.get("CURAVGPRICE").toString());
                chkList.setCurAvgPriceMat(amtFormat.format(data.get("CURAVGPRICE_MAT").toString()));
                chkList.setCurAvgPriceLabor(amtFormat.format(data.get("CURAVGPRICE_LABOR").toString()));
                chkList.setCurAvgPriceOem(amtFormat.format(data.get("CURAVGPRICE_OEM").toString()));
                chkList.setCurAvgPriceOH1(amtFormat.format(data.get("CURAVGPRICE_OH1").toString()));
                chkList.setCurAvgPriceOH2(amtFormat.format(data.get("CURAVGPRICE_OH2").toString()));
                chkList.setCurAvgPriceOH3(amtFormat.format(data.get("CURAVGPRICE_OH3").toString()));
                chkList.setCurAvgPriceOH4(amtFormat.format(data.get("CURAVGPRICE_OH4").toString()));
                chkList.setCurAvgPriceOH5(amtFormat.format(data.get("CURAVGPRICE_OH5").toString()));
                chkList.setCurWOOutQty(data.get("CURWOOUTQTY").toString());
                chkList.setCurWOOutAmt(amtFormat.format(data.get("CURWOOUTAMT").toString()));
                chkList.setCurWOOutMat(amtFormat.format(data.get("CURWOOUT_MAT").toString()));
                chkList.setCurWOOutLabor(amtFormat.format(data.get("CURWOOUT_LABOR").toString()));
                chkList.setCurWOOutOem(amtFormat.format(data.get("CURWOOUT_OEM").toString()));
                chkList.setCurWOOutOH1(amtFormat.format(data.get("CURWOOUT_OH1").toString()));
                chkList.setCurWOOutOH2(amtFormat.format(data.get("CURWOOUT_OH2").toString()));
                chkList.setCurWOOutOH3(amtFormat.format(data.get("CURWOOUT_OH3").toString()));
                chkList.setCurWOOutOH4(amtFormat.format(data.get("CURWOOUT_OH4").toString()));
                chkList.setCurWOOutOH5(amtFormat.format(data.get("CURWOOUT_OH5").toString()));
                chkList.setCurSalesQty(data.get("CURSALESQTY").toString());
                chkList.setCurSalesAmt(amtFormat.format(data.get("CURSALESAMT").toString()));
                chkList.setCurSalesMat(amtFormat.format(data.get("CURSALESCOST_MAT").toString()));
                chkList.setCurSalesLabor(amtFormat.format(data.get("CURSALESCOST_LABOR").toString()));
                chkList.setCurSalesOem(amtFormat.format(data.get("CURSALESCOST_OEM").toString()));
                chkList.setCurSalesOH1(amtFormat.format(data.get("CURSALESCOST_OH1").toString()));
                chkList.setCurSalesOH2(amtFormat.format(data.get("CURSALESCOST_OH2").toString()));
                chkList.setCurSalesOH3(amtFormat.format(data.get("CURSALESCOST_OH3").toString()));
                chkList.setCurSalesOH4(amtFormat.format(data.get("CURSALESCOST_OH4").toString()));
                chkList.setCurSalesOH5(amtFormat.format(data.get("CURSALESCOST_OH5").toString()));
                chkList.setCurReturnQty(data.get("CURRETURNQTY").toString());
                chkList.setCurReturnAmt(amtFormat.format(data.get("CURRETURNAMT").toString()));
                chkList.setCurReturnMat(amtFormat.format(data.get("CURRETURNCOST_MAT").toString()));
                chkList.setCurReturnLabor(amtFormat.format(data.get("CURRETURNCOST_LABOR").toString()));
                chkList.setCurReturnOem(amtFormat.format(data.get("CURRETURNCOST_OEM").toString()));
                chkList.setCurReturnOH1(amtFormat.format(data.get("CURRETURNCOST_OH1").toString()));
                chkList.setCurReturnOH2(amtFormat.format(data.get("CURRETURNCOST_OH2").toString()));
                chkList.setCurReturnOH3(amtFormat.format(data.get("CURRETURNCOST_OH3").toString()));
                chkList.setCurReturnOH4(amtFormat.format(data.get("CURRETURNCOST_OH4").toString()));
                chkList.setCurReturnOH5(amtFormat.format(data.get("CURRETURNCOST_OH5").toString()));
                chkList.setCurSalesFeeQty(data.get("CURSALESFEEQTY").toString());
                chkList.setCurSalesFeeAmt(amtFormat.format(data.get("CURSALESFEEAMT").toString()));
                chkList.setCurSalesFeeMat(amtFormat.format(data.get("CURSALESFEECOST_MAT").toString()));
                chkList.setCurSalesFeeLabor(amtFormat.format(data.get("CURSALESFEECOST_LABOR").toString()));
                chkList.setCurSalesFeeOem(amtFormat.format(data.get("CURSALESFEECOST_OEM").toString()));
                chkList.setCurSalesFeeOH1(amtFormat.format(data.get("CURSALESFEECOST_OH1").toString()));
                chkList.setCurSalesFeeOH2(amtFormat.format(data.get("CURSALESFEECOST_OH2").toString()));
                chkList.setCurSalesFeeOH3(amtFormat.format(data.get("CURSALESFEECOST_OH3").toString()));
                chkList.setCurSalesFeeOH4(amtFormat.format(data.get("CURSALESFEECOST_OH4").toString()));
                chkList.setCurSalesFeeOH5(amtFormat.format(data.get("CURSALESFEECOST_OH5").toString()));
                chkList.setCurMiscOutQty(data.get("CURMISCOUTQTY").toString());
                chkList.setCurMiscOutAmt(amtFormat.format(data.get("CURMISCOUTAMT").toString()));
                chkList.setCurMiscOutMat(amtFormat.format(data.get("CURMISCOUT_MAT").toString()));
                chkList.setCurMiscOutLabor(amtFormat.format(data.get("CURMISCOUT_LABOR").toString()));
                chkList.setCurMiscOutOem(amtFormat.format(data.get("CURMISCOUT_OEM").toString()));
                chkList.setCurMiscOutOH1(amtFormat.format(data.get("CURMISCOUT_OH1").toString()));
                chkList.setCurMiscOutOH2(amtFormat.format(data.get("CURMISCOUT_OH2").toString()));
                chkList.setCurMiscOutOH3(amtFormat.format(data.get("CURMISCOUT_OH3").toString()));
                chkList.setCurMiscOutOH4(amtFormat.format(data.get("CURMISCOUT_OH4").toString()));
                chkList.setCurMiscOutOH5(amtFormat.format(data.get("CURMISCOUT_OH5").toString()));
                chkList.setCurInvAdjQty(data.get("CURINVADJQTY").toString());
                chkList.setCurInvAdjAmt(amtFormat.format(data.get("CURINVADJAMT").toString()));
                chkList.setCurInvAdjMat(amtFormat.format(data.get("CURINVADJ_MAT").toString()));
                chkList.setCurInvAdjLabor(amtFormat.format(data.get("CURINVADJ_LABOR").toString()));
                chkList.setCurInvAdjOem(amtFormat.format(data.get("CURINVADJ_OEM").toString()));
                chkList.setCurInvAdjOH1(amtFormat.format(data.get("CURINVADJ_OH1").toString()));
                chkList.setCurInvAdjOH2(amtFormat.format(data.get("CURINVADJ_OH2").toString()));
                chkList.setCurInvAdjOH3(amtFormat.format(data.get("CURINVADJ_OH3").toString()));
                chkList.setCurInvAdjOH4(amtFormat.format(data.get("CURINVADJ_OH4").toString()));
                chkList.setCurInvAdjOH5(amtFormat.format(data.get("CURINVADJ_OH5").toString()));
                chkList.setCurTransOutQty(data.get("CURTRANSOUTQTY").toString());
                chkList.setCurTransOutAmt(amtFormat.format(data.get("CURTRANSOUTAMT").toString()));
                chkList.setCurTransOutMat(amtFormat.format(data.get("CURTRANSOUT_MAT").toString()));
                chkList.setCurTransOutLabor(amtFormat.format(data.get("CURTRANSOUT_LABOR").toString()));
                chkList.setCurTransOutOem(amtFormat.format(data.get("CURTRANSOUT_OEM").toString()));
                chkList.setCurTransOutOH1(amtFormat.format(data.get("CURTRANSOUT_OH1").toString()));
                chkList.setCurTransOutOH2(amtFormat.format(data.get("CURTRANSOUT_OH2").toString()));
                chkList.setCurTransOutOH3(amtFormat.format(data.get("CURTRANSOUT_OH3").toString()));
                chkList.setCurTransOutOH4(amtFormat.format(data.get("CURTRANSOUT_OH4").toString()));
                chkList.setCurTransOutOH5(amtFormat.format(data.get("CURTRANSOUT_OH5").toString()));
                chkList.setEndingBalQty(data.get("ENDINGBALQTY").toString());
                chkList.setEndingBalAmt(amtFormat.format(data.get("ENDINGBALAMT").toString()));
                chkList.setEndingBalMat(amtFormat.format(data.get("ENDINGBAL_MAT").toString()));
                chkList.setEndingBalLabor(amtFormat.format(data.get("ENDINGBAL_LABOR").toString()));
                chkList.setEndingBalOem(amtFormat.format(data.get("ENDINGBAL_OEM").toString()));
                chkList.setEndingBalOH1(amtFormat.format(data.get("ENDINGBAL_OH1").toString()));
                chkList.setEndingBalOH2(amtFormat.format(data.get("ENDINGBAL_OH2").toString()));
                chkList.setEndingBalOH3(amtFormat.format(data.get("ENDINGBAL_OH3").toString()));
                chkList.setEndingBalOH4(amtFormat.format(data.get("ENDINGBAL_OH4").toString()));
                chkList.setEndingBalOH5(amtFormat.format(data.get("ENDINGBAL_OH5").toString()));
                chkList.setEndingBalAdjAmt(amtFormat.format(data.get("ENDINGBALADJAMT").toString()));
                chkList.setEndingBalAdjMat(amtFormat.format(data.get("ENDINGBALADJ_MAT").toString()));
                chkList.setEndingBalAdjLabor(amtFormat.format(data.get("ENDINGBALADJ_LABOR").toString()));
                chkList.setEndingBalAdjOem(amtFormat.format(data.get("ENDINGBALADJ_OEM").toString()));
                chkList.setEndingBalAdjOH1(amtFormat.format(data.get("ENDINGBALADJ_OH1").toString()));
                chkList.setEndingBalAdjOH2(amtFormat.format(data.get("ENDINGBALADJ_OH2").toString()));
                chkList.setEndingBalAdjOH3(amtFormat.format(data.get("ENDINGBALADJ_OH3").toString()));
                chkList.setEndingBalAdjOH4(amtFormat.format(data.get("ENDINGBALADJ_OH4").toString()));
                chkList.setEndingBalAdjOH5(amtFormat.format(data.get("ENDINGBALADJ_OH5").toString()));


            }

        }
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CurInvCostStatDetilQueryReq req) throws Exception {
        StringBuilder sb = new StringBuilder();

//        int pageNumber = req.getPageNumber();
//        int pageSize = req.getPageSize();
//
//        //計算起啟位置
//        int startRow = (pageNumber - 1) * pageSize;

        sb.append(" SELECT a.* " +
                        " ,ol1.ORG_NAME CORPNAME,cs.ACCOUNT,gl1.PLU_NAME PLUNAME" +
                        " ,ol2.ORG_NAME COSTDOMAINNAME,fl1.FEATURENAME,dg.BASEUNIT,dul1.UNAME BASEUNITNAME ")
                .append(" ,dc.PRICEDIGIT,dc.AMOUNTDIGIT,dc.COSTPRICEDIGIT,dc.COSTAMOUNTDIGIT ")
                .append(" FROM DCP_CURINVCOSTSTAT a ")
                .append(" LEFT JOIN DCP_GOODS dg on dg.eid=a.eid and dg.PLUNO=a.PLUNO")
                .append(" LEFT JOIN DCP_UNIT_LANG dul1 on dul1.eid=dg.eid and  dul1.UNIT=dg.BASEUNIT AND dul1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol1 on ol1.eid=a.eid and ol1.ORGANIZATIONNO=a.CORP AND ol1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ORG_LANG ol2 on ol2.eid=a.eid and ol2.ORGANIZATIONNO=a.COSTDOMAINID AND ol2.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_ACOUNT_SETTING cs on cs.eid=a.eid and cs.ACCOUNTID=a.ACCOUNTID ")
                .append(" LEFT JOIN DCP_CURRENCY dc on cs.eid=dc.eid and cs.CURRENCY=dc.CURRENCY and dc.NATION='CN' ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl1 on gl1.eid=a.eid AND gl1.PLUNO=a.PLUNO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS_FEATURE_LANG fl1 on fl1.eid=a.eid AND fl1.PLUNO=a.PLUNO and fl1.FEATURENO=a.FEATURENO and gl1.LANG_TYPE='").append(req.getLangType()).append("'")

//                .append(" LEFT JOIN DCP_COSTDOMAIN cd on cd.eid=a.eid and cd.COSTDOMAINID=a.COSTDOMAINID ")
        ;

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getYear())) {
            sb.append(" and a.YEAR='").append(req.getRequest().getYear()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getPeriod())) {
            sb.append(" and a.PERIOD='").append(req.getRequest().getPeriod()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getAccountID())) {
            sb.append(" and a.ACCOUNTID='").append(req.getRequest().getAccountID()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getAccount())) {
            sb.append(" and as.ACCOUNT like '%%").append(req.getRequest().getAccount()).append("%%'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            sb.append(" and a.CORP='").append(req.getRequest().getCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCorp_Name())) {
            sb.append(" and ol1.CORP_NAME like '%%").append(req.getRequest().getCorp_Name()).append("%%'");
        }


        if (StringUtils.isNotEmpty(req.getRequest().getPluNo())) {
            sb.append(" and a.PLUNO='").append(req.getRequest().getPluNo()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getCostDomainID())) {
            sb.append(" and a.COSTDOMAINID='").append(req.getRequest().getCostDomainID()).append("'");
        }

//        sb.append("  ) a "
//                + "    WHERE rn> " + startRow + " and rn<= " + (startRow + pageSize)
//                + " ORDER BY REFERENCENO "
//        );

        return sb.toString();
    }
}
