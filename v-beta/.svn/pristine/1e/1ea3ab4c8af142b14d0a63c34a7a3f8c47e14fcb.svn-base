package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_Acount_SetingUpdateReq;
import com.dsc.spos.json.cust.res.DCP_Acount_SetingUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_Acount_SetingUpdate extends SPosAdvanceService<DCP_Acount_SetingUpdateReq, DCP_Acount_SetingUpdateRes> {
    @Override
    protected void processDUID(DCP_Acount_SetingUpdateReq req, DCP_Acount_SetingUpdateRes res) throws Exception {


        if ("1".equals(req.getRequest().getAcctType())){
            String querySql = " SELECT * FROM DCP_ACOUNT_SETTING " +
                    " WHERE EID='"+req.geteId() + "' AND STATUS='100' and CORP='"+req.getRequest().getCorp() + "' AND ACCTTYPE='1' AND ACCOUNTID!='"+req.getRequest().getAccountID()+"'";
            List<Map<String, Object>> exists = doQueryData(querySql,null);

            if (CollectionUtils.isNotEmpty(exists)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,req.getRequest().getCorp() +"已存在其它主帐套");
            }
        }

        ColumnDataValue condition = new ColumnDataValue();
        ColumnDataValue dcp_account_setting = new ColumnDataValue();

        String lastModiTime = DateFormatUtils.getNowDateTime();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
//        condition.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));

        dcp_account_setting.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
        dcp_account_setting.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
        dcp_account_setting.add("CURRENCY", DataValues.newString(req.getRequest().getCurrency()));
        dcp_account_setting.add("PAYMENTMETHOD", DataValues.newString(req.getRequest().getPaymentMethod()));
        dcp_account_setting.add("CURRENTYEAR", DataValues.newString(req.getRequest().getCurrentYear()));
        dcp_account_setting.add("CURRENTPERIOD", DataValues.newString(req.getRequest().getCurrentPeriod()));
        dcp_account_setting.add("CLOSINGDATE", DataValues.newDate(req.getRequest().getClosingDate()));
        dcp_account_setting.add("ACCTTYPE", DataValues.newString(req.getRequest().getAcctType()));
        dcp_account_setting.add("INVCURRENTYEAR", DataValues.newString(req.getRequest().getInvCurrentYear()));
        dcp_account_setting.add("INVCURRENTPERIOD", DataValues.newString(req.getRequest().getInvCurrentperiod()));
        dcp_account_setting.add("INVCLOSINGDATE", DataValues.newDate(req.getRequest().getInvClosingDate()));
        dcp_account_setting.add("FUNDCURYEAR", DataValues.newString(req.getRequest().getFundCurYear()));
        dcp_account_setting.add("FUNDCURPERIOD", DataValues.newString(req.getRequest().getFundCurPeriod()));
        dcp_account_setting.add("FUNDCLOSEDATE", DataValues.newDate(req.getRequest().getFundCloseDate()));
        dcp_account_setting.add("LABORCOSTALLOC", DataValues.newString(req.getRequest().getLaborCostAlloc()));
        dcp_account_setting.add("LABORCOSTRULE", DataValues.newString(req.getRequest().getLaborCostRule()));
        dcp_account_setting.add("ISSALERETINFLCOST", DataValues.newString(req.getRequest().getIsSaleRetInflCost()));
        dcp_account_setting.add("WIPLABORCOSTRULE", DataValues.newString(req.getRequest().getWipLaborCostRule()));
        dcp_account_setting.add("ISTRANSFERINCOST", DataValues.newString(req.getRequest().getIsTransferInCost()));
        dcp_account_setting.add("COSTCURRENTYEAR", DataValues.newString(req.getRequest().getCostCurrentYear()));
        dcp_account_setting.add("COSTCURRENTMTH", DataValues.newString(req.getRequest().getCostCurrentMth()));
        dcp_account_setting.add("COSTCLOSINGDATE", DataValues.newDate(req.getRequest().getCostClosingDate()));
        dcp_account_setting.add("DEPRECIATIONITEM", DataValues.newString(req.getRequest().getDepreciationItem()));
        dcp_account_setting.add("ASSETTRANSFPRICE", DataValues.newString(req.getRequest().getAssetTransfPrice()));
        dcp_account_setting.add("ISCARDAUTOCODE", DataValues.newString(req.getRequest().getIsCardAutoCode()));
        dcp_account_setting.add("ISASSETDISPTRANSFER", DataValues.newString(req.getRequest().getIsAssetDispTransfer()));
        dcp_account_setting.add("ISASSETDISPPROFIT", DataValues.newString(req.getRequest().getIsAssetDispProfit()));
        dcp_account_setting.add("ISIMPAIRMENTREVERSE", DataValues.newString(req.getRequest().getIsImpairmentReverse()));
        dcp_account_setting.add("ISPROPAUTOCODE", DataValues.newString(req.getRequest().getIsPropAutoCode()));
        dcp_account_setting.add("ISPROPASSETCODEMATCH", DataValues.newString(req.getRequest().getIsPropAssetCodeMatch()));
        dcp_account_setting.add("LEASEDASSETRECOG", DataValues.newString(req.getRequest().getLeasedAssetRecog()));
        dcp_account_setting.add("ARPARAMETER", DataValues.newString(req.getRequest().getArParameter()));
        dcp_account_setting.add("ISDEPOSITBYORDER", DataValues.newString(req.getRequest().getIsDepositByOrder()));
        dcp_account_setting.add("ARCLOSINGDATESET", DataValues.newString(req.getRequest().getArClosingDateSet()));
        dcp_account_setting.add("ARRECEIPTDATE", DataValues.newString(req.getRequest().getArReceiptDate()));
        dcp_account_setting.add("ARACCTDATE", DataValues.newString(req.getRequest().getArAcctDate()));
        dcp_account_setting.add("ARCLOSINGDATE", DataValues.newDate(req.getRequest().getArClosingDate()));
        dcp_account_setting.add("APPARAMETER", DataValues.newString(req.getRequest().getApParameter()));
        dcp_account_setting.add("ISDESPOSITBYPURCH", DataValues.newString(req.getRequest().getIsDepositByPurch()));
        dcp_account_setting.add("APCLOSINGDATESET", DataValues.newString(req.getRequest().getApClosingDateSet()));
        dcp_account_setting.add("APPAYMENTDATE", DataValues.newString(req.getRequest().getApPaymentDate()));
        dcp_account_setting.add("APACCTDATE", DataValues.newString(req.getRequest().getApAcctDate()));
        dcp_account_setting.add("APCLOSINGDATE", DataValues.newDate(req.getRequest().getApClosingDate()));
        dcp_account_setting.add("APPROVISIONALTAXOPT", DataValues.newString(req.getRequest().getApProvisionalTaxOpt()));
        dcp_account_setting.add("APPROVISIONALTAXTYPE", DataValues.newString(req.getRequest().getApProvisionalTaxType()));
        dcp_account_setting.add("ASSETCURYEAR", DataValues.newString(req.getRequest().getAssetCurYear()));
        dcp_account_setting.add("ASSETCURMTH", DataValues.newString(req.getRequest().getAssetCurMth()));
        dcp_account_setting.add("ASSETCLOSINGDATE", DataValues.newDate(req.getRequest().getAssetClosingDate()));
        dcp_account_setting.add("corp", DataValues.newString(req.getRequest().getCorp()));
        dcp_account_setting.add("ENABLEDATE", DataValues.newDate(req.getRequest().getEnableDate()));
        dcp_account_setting.add("ISLABORCOSTALLOC", DataValues.newString(req.getRequest().getIsLaborcoStalloc()));

        dcp_account_setting.add("COAREFID", DataValues.newString(req.getRequest().getCoaRefID()));
        dcp_account_setting.add("FXREFID", DataValues.newString(req.getRequest().getFXRefID()));
        dcp_account_setting.add("FXTYPE", DataValues.newString(req.getRequest().getFxType()));
        dcp_account_setting.add("FXSOURCE", DataValues.newString(req.getRequest().getFxSource()));

        dcp_account_setting.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
        dcp_account_setting.add("LASTMODITIME", DataValues.newDate(lastModiTime));


        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_ACOUNT_SETTING",condition, dcp_account_setting)));
        this.doExecuteDataToDB();


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_Acount_SetingUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_Acount_SetingUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_Acount_SetingUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_Acount_SetingUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_Acount_SetingUpdateReq> getRequestType() {
        return new TypeToken<DCP_Acount_SetingUpdateReq>(){};
    }

    @Override
    protected DCP_Acount_SetingUpdateRes getResponseType() {
        return new DCP_Acount_SetingUpdateRes();
    }
}
