package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BatchUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BatchUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_BatchUpdate  extends SPosAdvanceService<DCP_BatchUpdateReq, DCP_BatchUpdateRes> {

    @Override
    protected void processDUID(DCP_BatchUpdateReq req, DCP_BatchUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        if(Check.Null(req.getRequest().getFeatureNo())){
            req.getRequest().setFeatureNo(" ");
        }

        DCP_BatchUpdateReq.RequestLevel request = req.getRequest();

        String goodsSql="select * from dcp_goods a where a.eid='"+req.geteId()+"' and a.pluno='"+req.getRequest().getPluNo()+"' ";
        List<Map<String, Object>> goodsList = this.doQueryData(goodsSql, null);
        if(goodsList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+req.getRequest().getPluNo()+"不存在！");
        }

        String batchSql="select to_char(a.PRODUCTDATE,'yyyyMMdd') as productdate,to_char(a.losedate,'yyyyMMdd') as losedate from MES_BATCH a" +
                " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.pluno='"+req.getRequest().getPluNo()+"' and a.featureno='"+req.getRequest().getFeatureNo()+"'" +
                " and a.batchno='"+req.getRequest().getBatchNo()+"' ";
        List<Map<String, Object>> batchList = this.doQueryData(batchSql, null);
        if(batchList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "批号"+req.getRequest().getBatchNo()+"不存在！");
        }

        String batchStockSql="select * from MES_BATCH_STOCK_DETAIL a" +
                " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.pluno='"+req.getRequest().getPluNo()+"' and a.featureno='"+req.getRequest().getFeatureNo()+"'" +
                " and a.batchno='"+req.getRequest().getBatchNo()+"' ";
        List<Map<String, Object>> batchStockList = this.doQueryData(batchStockSql, null);
        if(batchStockList.size()>0){
            if(!batchList.get(0).get("PRODUCTDATE").toString().equals(request.getProductDate())||!batchList.get(0).get("LOSEDATE").toString().equals(request.getLoseDate())){
                //批号存在库存记录，不允许变更生产日期、有效日期！（需对比原值与新值）
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "批号存在库存记录，不允许变更生产日期、有效日期！");

            }

           }else{
            String shelflife = goodsList.get(0).get("SHELFLIFE").toString();
            if(!shelflife.equals("0")){
                if(Check.Null(request.getProductDate())||Check.Null(request.getLoseDate())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+req.getRequest().getPluNo()+"保质期天数>0，生产日期、有效日期不允许为空！");
                }
            }else{
                if(Check.Null(request.getProductDate())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+req.getRequest().getPluNo()+"保质期天数为空或等于0，生产日期不允许为空！");
                }
            }
        }

        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        UptBean ub1 = new UptBean("MES_BATCH");
        ub1.addUpdateValue("EID", DataValues.newString(req.geteId()));
        ub1.addUpdateValue("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        ub1.addUpdateValue("PLUNO", DataValues.newString(request.getPluNo()));
        ub1.addUpdateValue("FEATURENO", DataValues.newString(request.getFeatureNo()));
        ub1.addUpdateValue("BATCHNO", DataValues.newString(request.getBatchNo()));
        if(batchStockList.size()<=0) {
            ub1.addUpdateValue("PRODUCTDATE", DataValues.newDate(LocalDate.parse(request.getProductDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            ub1.addUpdateValue("LOSEDATE", DataValues.newDate(LocalDate.parse(request.getProductDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        ub1.addUpdateValue("SUPPLIERTYPE", DataValues.newString(request.getSupplierType()));
        ub1.addUpdateValue("SUPPLIERID", DataValues.newString(request.getSupplierId()));
        ub1.addUpdateValue("PRODUCEAREA", DataValues.newString(request.getProduceArea()));
        ub1.addUpdateValue("MANUFACTURER", DataValues.newString(request.getManufacturer()));

        ub1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));     // 当前操作人
        ub1.addUpdateValue("LASTMODIOPNAME", DataValues.newString(req.getOpName()));
        ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(createTime));
        ub1.addUpdateValue("TRAN_TIME", DataValues.newString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
        this.addProcessData(new DataProcessBean(ub1));



        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BatchUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BatchUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BatchUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BatchUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_BatchUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_BatchUpdateReq>(){};
    }

    @Override
    protected DCP_BatchUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_BatchUpdateRes();
    }

}


