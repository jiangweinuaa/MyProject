package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockAdjustCreateReq;
import com.dsc.spos.json.cust.res.DCP_StockAdjustCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_StockAdjustCreate extends SPosAdvanceService<DCP_StockAdjustCreateReq, DCP_StockAdjustCreateRes> {

    @Override
    protected void processDUID(DCP_StockAdjustCreateReq req, DCP_StockAdjustCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_StockAdjustCreateReq.Request request = req.getRequest();
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());
        String createDateTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String uptime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format( Calendar.getInstance().getTime());

        List<DCP_StockAdjustCreateReq.Detail> details = request.getDetail();

        String adjustno =this.getOrderNO(req,"KCTZ");


        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        mainColumns.add("SHOPID", DataValues.newString(req.getShopId()));
        mainColumns.add("ADJUSTNO", DataValues.newString(adjustno));
        mainColumns.add("DOC_TYPE", DataValues.newString(request.getDocType()));
        mainColumns.add("BDATE", DataValues.newString(request.getBDate()));
        mainColumns.add("OTYPE", DataValues.newString(request.getOType()));
        mainColumns.add("OFNO", DataValues.newString(request.getOfNo()));
        mainColumns.add("LOAD_DOCNO", DataValues.newString(request.getLoadDocNo()));
        mainColumns.add("LOAD_DOCTYPE", DataValues.newString(request.getLoadDocType()));
        mainColumns.add("WAREHOUSE", DataValues.newString(request.getWarehouse()));
        mainColumns.add("TOT_CQTY", DataValues.newDecimal(request.getTotCqty()));
        mainColumns.add("TOT_PQTY", DataValues.newDecimal(request.getTotPqty()));
        mainColumns.add("TOT_AMT", DataValues.newDecimal(request.getTotAmt()));
        mainColumns.add("TOT_DISTRIAMT", DataValues.newDecimal(request.getTotDistriAmt()));
        mainColumns.add("STATUS", DataValues.newString("0"));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("PROCESS_STATUS", DataValues.newString("N"));
        mainColumns.add("UPDATE_TIME", DataValues.newString(uptime));
        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
        mainColumns.add("CREATE_TIME", DataValues.newString(createTime));
        mainColumns.add("TRAN_TIME", DataValues.newString(uptime));
        mainColumns.add("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        mainColumns.add("DEPARTID", DataValues.newString(request.getDepartId()));
        mainColumns.add("ACCOUNT_DATE", DataValues.newString(request.getAccountDate()));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_ADJUST",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        if(CollUtil.isNotEmpty(details)){
            for (DCP_StockAdjustCreateReq.Detail detail : details){

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
        res.setAdjustNo(adjustno);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockAdjustCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockAdjustCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockAdjustCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockAdjustCreateReq req) throws Exception {
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
    protected TypeToken<DCP_StockAdjustCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_StockAdjustCreateReq>(){};
    }

    @Override
    protected DCP_StockAdjustCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_StockAdjustCreateRes();
    }

}


