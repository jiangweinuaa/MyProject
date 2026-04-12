package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockAdjustUpdateReq;
import com.dsc.spos.json.cust.res.DCP_StockAdjustUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DCP_StockAdjustUpdate extends SPosAdvanceService<DCP_StockAdjustUpdateReq, DCP_StockAdjustUpdateRes> {

    @Override
    protected void processDUID(DCP_StockAdjustUpdateReq req, DCP_StockAdjustUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_StockAdjustUpdateReq.Request request = req.getRequest();
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());
        String createDateTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String uptime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format( Calendar.getInstance().getTime());

        List<DCP_StockAdjustUpdateReq.Detail> details = request.getDetail();

        String adjustno =request.getAdjustNo();
        DelBean db2 = new DelBean("DCP_ADJUST_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db2.addCondition("ADJUSTNO", new DataValue(adjustno, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));



        UptBean ub1 = new UptBean("DCP_ADJUST");
        ub1.addCondition("EID", DataValues.newString(eId));
        ub1.addCondition("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        ub1.addCondition("SHOPID", DataValues.newString(req.getShopId()));
        ub1.addCondition("ADJUSTNO", DataValues.newString(adjustno));
        ub1.addUpdateValue("DOC_TYPE", DataValues.newString(request.getDocType()));
        ub1.addUpdateValue("BDATE", DataValues.newString(request.getBDate()));
        ub1.addUpdateValue("OTYPE", DataValues.newString(request.getOType()));
        ub1.addUpdateValue("OFNO", DataValues.newString(request.getOfNo()));
        ub1.addUpdateValue("LOAD_DOCNO", DataValues.newString(request.getLoadDocNo()));
        ub1.addUpdateValue("LOAD_DOCTYPE", DataValues.newString(request.getLoadDocType()));
        ub1.addUpdateValue("WAREHOUSE", DataValues.newString(request.getWarehouse()));
        ub1.addUpdateValue("TOT_CQTY", DataValues.newDecimal(request.getTotCqty()));
        ub1.addUpdateValue("TOT_PQTY", DataValues.newDecimal(request.getTotPqty()));
        ub1.addUpdateValue("TOT_AMT", DataValues.newDecimal(request.getTotAmt()));
        ub1.addUpdateValue("TOT_DISTRIAMT", DataValues.newDecimal(request.getTotDistriAmt()));
        ub1.addUpdateValue("STATUS", DataValues.newString("0"));
        ub1.addUpdateValue("MEMO", DataValues.newString(request.getMemo()));
        ub1.addUpdateValue("PROCESS_STATUS", DataValues.newString("N"));
        ub1.addUpdateValue("UPDATE_TIME", DataValues.newString(uptime));
        ub1.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
        ub1.addUpdateValue("MODIFY_DATE", DataValues.newString(createDate));
        ub1.addUpdateValue("MODIFY_TIME", DataValues.newString(createTime));
        ub1.addUpdateValue("TRAN_TIME", DataValues.newString(uptime));
        ub1.addUpdateValue("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        ub1.addUpdateValue("DEPARTID", DataValues.newString(request.getDepartId()));
        ub1.addUpdateValue("ACCOUNT_DATE", DataValues.newString(request.getAccountDate()));
        this.addProcessData(new DataProcessBean(ub1));


        if(CollUtil.isNotEmpty(details)){
            for (DCP_StockAdjustUpdateReq.Detail detail : details){

                if(Check.Null(detail.getFeatureNo())){
                    detail.setFeatureNo(" ");
                }

                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                detailColumns.add("SHOPID", DataValues.newString(req.getShopId()));
                detailColumns.add("ADJUSTNO", DataValues.newString(adjustno));
                detailColumns.add("ITEM", DataValues.newString(detail.getItem()));
                detailColumns.add("OITEM", DataValues.newString(detail.getOItem()));
                //detailColumns.add("PLU_BARCODE", DataValues.newString(detail.getpl()));
                detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                detailColumns.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
                detailColumns.add("PQTY", DataValues.newDecimal(detail.getPQty()));
                detailColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
                detailColumns.add("BASEUNIT", DataValues.newString(detail.getBaseUnit()));
                detailColumns.add("BASEQTY", DataValues.newDecimal(detail.getBaseQty()));
                detailColumns.add("UNIT_RATIO", DataValues.newDecimal(detail.getUnitRatio()));
                detailColumns.add("PRICE", DataValues.newDecimal(detail.getPrice()));
                detailColumns.add("AMT", DataValues.newDecimal(detail.getAmt()));
                detailColumns.add("DISTRIPRICE", DataValues.newDecimal(detail.getDistriPrice()));
                detailColumns.add("DISTRIAMT", DataValues.newDecimal(detail.getDistriAmt()));
                detailColumns.add("WAREHOUSE", DataValues.newString(request.getWarehouse()));
                detailColumns.add("BATCH_NO", DataValues.newString(detail.getBatchNo()));
                detailColumns.add("PROD_DATE", DataValues.newString(detail.getProdDate()));
                detailColumns.add("BDATE", DataValues.newString(request.getBDate()));
                detailColumns.add("TRAN_TIME", DataValues.newString(uptime));
                detailColumns.add("LOCATION", DataValues.newString(detail.getLocation()));
                detailColumns.add("EXPDATE", DataValues.newString(detail.getExpDate()));
                //detailColumns.add("CATEGORY", DataValues.newString(detail.getca()));
                detailColumns.add("MEMO", DataValues.newString(detail.getMemo()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_ADJUST_DETAIL",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockAdjustUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockAdjustUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockAdjustUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockAdjustUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockAdjustUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_StockAdjustUpdateReq>(){};
    }

    @Override
    protected DCP_StockAdjustUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_StockAdjustUpdateRes();
    }

}


