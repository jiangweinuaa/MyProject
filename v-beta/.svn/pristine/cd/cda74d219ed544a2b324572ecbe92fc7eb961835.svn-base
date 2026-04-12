package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BatchCreateReq;
import com.dsc.spos.json.cust.res.DCP_BatchCreateRes;
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
import java.util.stream.Collectors;

public class DCP_BatchCreate extends SPosAdvanceService<DCP_BatchCreateReq, DCP_BatchCreateRes> {

    @Override
    protected void processDUID(DCP_BatchCreateReq req, DCP_BatchCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        if(Check.Null(req.getRequest().getFeatureNo())){
            req.getRequest().setFeatureNo(" ");
        }


        DCP_BatchCreateReq.RequestLevel request = req.getRequest();

        //● 批号不允许重复！
        //● 品号保质期天数>0，生产日期、有效日期不允许为空！
        //● 品号保质期天数为空或等于0，生产日期不允许为空！

        String batchSql="select * from mes_batch a" +
                " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.pluno='"+req.getRequest().getPluNo()+"' and a.featureno='"+req.getRequest().getFeatureNo()+"'" +
                " and a.batchno='"+req.getRequest().getBatchNo()+"' ";
        List<Map<String, Object>> batchList = this.doQueryData(batchSql, null);
        if(batchList.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "批号"+req.getRequest().getBatchNo()+"已存在！");
        }

        String goodsSql="select * from dcp_goods a where a.eid='"+req.geteId()+"' and a.pluno='"+req.getRequest().getPluNo()+"' ";
        List<Map<String, Object>> goodsList = this.doQueryData(goodsSql, null);
        if(goodsList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "品号"+req.getRequest().getPluNo()+"不存在！");
        }

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
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        ColumnDataValue mainColumns = new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(req.geteId()));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        mainColumns.add("PLUNO", DataValues.newString(request.getPluNo()));
        mainColumns.add("FEATURENO", DataValues.newString(request.getFeatureNo()));
        mainColumns.add("BATCHNO", DataValues.newString(request.getBatchNo()));
        mainColumns.add("PRODUCTDATE", DataValues.newDate(LocalDate.parse(request.getProductDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        mainColumns.add("LOSEDATE", DataValues.newDate(LocalDate.parse(request.getProductDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        mainColumns.add("SUPPLIERTYPE", DataValues.newString(request.getSupplierType()));
        mainColumns.add("SUPPLIERID", DataValues.newString(request.getSupplierId()));
        mainColumns.add("PRODUCEAREA", DataValues.newString(request.getProduceArea()));
        mainColumns.add("MANUFACTURER", DataValues.newString(request.getManufacturer()));
        mainColumns.add("BILLTYPE", DataValues.newString(""));
        mainColumns.add("BILLNO", DataValues.newString(""));

        mainColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));     // 当前操作人
        mainColumns.add("CREATEOPNAME", DataValues.newString(req.getOpName()));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));
        mainColumns.add("TRAN_TIME", DataValues.newString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib = new InsBean("MES_BATCH", mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BatchCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BatchCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BatchCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BatchCreateReq req) throws Exception {
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
    protected TypeToken<DCP_BatchCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_BatchCreateReq>(){};
    }

    @Override
    protected DCP_BatchCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_BatchCreateRes();
    }

}


