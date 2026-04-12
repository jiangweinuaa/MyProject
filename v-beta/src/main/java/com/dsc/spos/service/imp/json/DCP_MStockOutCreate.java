package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MStockOutCreateReq;
import com.dsc.spos.json.cust.res.DCP_MStockOutCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_MStockOutCreate extends SPosAdvanceService<DCP_MStockOutCreateReq, DCP_MStockOutCreateRes> {

    @Override
    protected void processDUID(DCP_MStockOutCreateReq req, DCP_MStockOutCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_MStockOutCreateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String orderNO = this.getOrderNO(req, "SCKL");

        List<DCP_MStockOutCreateReq.Datas> datas = request.getDatas();
        BigDecimal totCqty=new BigDecimal(0);
        BigDecimal totAmt=new BigDecimal(0);
        BigDecimal totPQty=new BigDecimal(0);
        BigDecimal totDistriAmt=new BigDecimal(0);
        for (DCP_MStockOutCreateReq.Datas data : datas){

            Map<String, Object> baseMap = PosPub.getBaseQty(dao, req.geteId(), data.getPluNo(), data.getPUnit(), data.getPQty());
            String baseQty = baseMap.get("baseQty").toString();
            String unitRatio = baseMap.get("unitRatio").toString();
            String baseUnit = baseMap.get("baseUnit").toString();

            totPQty=totPQty.add(new BigDecimal(data.getPQty()));
            totAmt=totAmt.add(new BigDecimal(data.getAmt()));
            totDistriAmt=totDistriAmt.add(new BigDecimal(data.getDistriAmt()));


            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("SHOPID", DataValues.newString(req.getShopId()));
            detailColumns.add("MSTOCKOUTNO", DataValues.newString(orderNO));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            detailColumns.add("WAREHOUSE", DataValues.newString(data.getWarehouse()));
            detailColumns.add("ITEM", DataValues.newString(data.getItem()));
            detailColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
            detailColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
            detailColumns.add("PQTY", DataValues.newString(data.getPQty()));
            detailColumns.add("BASEUNIT", DataValues.newString(baseUnit));
            detailColumns.add("BASEQTY", DataValues.newString(baseQty));
            detailColumns.add("UNIT_RATIO", DataValues.newString(unitRatio));
            detailColumns.add("PRICE", DataValues.newString(data.getPrice()));
            detailColumns.add("AMT", DataValues.newString(data.getAmt()));
            detailColumns.add("DISTRIPRICE", DataValues.newString(data.getDistriPrice()));
            detailColumns.add("DISTRIAMT", DataValues.newString(data.getDistriAmt()));
            detailColumns.add("BATCHNO", DataValues.newString(data.getBatchNo()));
            detailColumns.add("PRODDATE", DataValues.newString(data.getProdDate()));
            detailColumns.add("EXPDATE", DataValues.newString(data.getLoseDate()));
            detailColumns.add("ISBUCKLE", DataValues.newString(data.getIsBuckle()));
            detailColumns.add("FEATURENO", DataValues.newString(data.getFeatureNo()));
            detailColumns.add("LOCATION", DataValues.newString(data.getLocation()));
            detailColumns.add("OTYPE", DataValues.newString(data.getOType()));
            detailColumns.add("OFNO", DataValues.newString(data.getOfNo()));
            detailColumns.add("OITEM", DataValues.newString(data.getOItem()));
            detailColumns.add("OOTYPE", DataValues.newString(data.getOOType()));
            detailColumns.add("OOFNO", DataValues.newString(data.getOOfNo()));
            detailColumns.add("OOITEM", DataValues.newString(data.getOOItem()));
            detailColumns.add("LOAD_DOCITEM", DataValues.newString(data.getLoadDocItem()));
            detailColumns.add("PITEM", DataValues.newString(data.getPItem()));
            detailColumns.add("PROCESSNO", DataValues.newString(data.getProcessNo()));
            detailColumns.add("SITEM", DataValues.newString(data.getSItem()));
            detailColumns.add("ZITEM", DataValues.newString(data.getZItem()));
            detailColumns.add("OPQTY", DataValues.newString(data.getOPQty()));

            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean detailib=new InsBean("DCP_MSTOCKOUT_DETAIL",detailColumnNames);
            detailib.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(detailib));

        }

        List<Map> collect = datas.stream().map(x -> {
            Map m = new HashMap();
            m.put("PLUNO", x.getPluNo());
            m.put("FEATURENO", x.getFeatureNo());
            return m;
        }).distinct().collect(Collectors.toList());
        totCqty=new BigDecimal(collect.size());


        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("SHOPID", DataValues.newString(req.getShopId()));
        mainColumns.add("MSTOCKOUTNO", DataValues.newString(orderNO));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
        //mainColumns.add("WAREHOUSE", DataValues.newString(""));
        mainColumns.add("DOC_TYPE", DataValues.newString(request.getDocType()));
        mainColumns.add("BDATE", DataValues.newString(request.getBDate()));
        mainColumns.add("SDATE", DataValues.newString(createDate));
        mainColumns.add("OTYPE", DataValues.newString(request.getOType()));
        mainColumns.add("OFNO", DataValues.newString(request.getOfNo()));
        mainColumns.add("OOTYPE", DataValues.newString(request.getOOType()));
        mainColumns.add("OOFNO", DataValues.newString(request.getOOfNo()));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("STATUS", DataValues.newInteger(0));
        mainColumns.add("ADJUSTSTATUS", DataValues.newString(request.getAdjustStatus()));
        mainColumns.add("OMSTOCKOUTNO", DataValues.newString(request.getOMStockOutNo()));
        mainColumns.add("TOT_CQTY", DataValues.newDecimal(totCqty));
        mainColumns.add("TOT_PQTY", DataValues.newDecimal(totPQty));
        mainColumns.add("TOT_AMT", DataValues.newDecimal(totAmt));
        mainColumns.add("TOT_DISTRIAMT", DataValues.newDecimal(totDistriAmt));
        mainColumns.add("LOAD_DOCTYPE", DataValues.newString(request.getLoadDocType()));
        mainColumns.add("LOAD_DOCNO", DataValues.newString(request.getLoadDocNo()));
        mainColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));
        mainColumns.add("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        mainColumns.add("DEPARTID", DataValues.newString(request.getDepartId()));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_MSTOCKOUT",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        this.doExecuteDataToDB();
        DCP_MStockOutCreateRes.Datas rDatas = res.new Datas();
        rDatas.setMStockOutNo(orderNO);
        res.setDatas(rDatas);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MStockOutCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MStockOutCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MStockOutCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MStockOutCreateReq req) throws Exception {
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
    protected TypeToken<DCP_MStockOutCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_MStockOutCreateReq>(){};
    }

    @Override
    protected DCP_MStockOutCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_MStockOutCreateRes();
    }

}


