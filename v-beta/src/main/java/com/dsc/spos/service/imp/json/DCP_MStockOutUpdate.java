package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MStockOutUpdateReq;
import com.dsc.spos.json.cust.res.DCP_MStockOutUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_MStockOutUpdate extends SPosAdvanceService<DCP_MStockOutUpdateReq, DCP_MStockOutUpdateRes> {

    @Override
    protected void processDUID(DCP_MStockOutUpdateReq req, DCP_MStockOutUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();

        DCP_MStockOutUpdateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String orderNO = req.getRequest().getMStockOutNo();

        DelBean db2 = new DelBean("DCP_MSTOCKOUT_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        db2.addCondition("MSTOCKOUTNO", new DataValue(orderNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));


        List<DCP_MStockOutUpdateReq.Datas> datas = request.getDatas();
        BigDecimal totCqty=new BigDecimal(0);
        BigDecimal totAmt=new BigDecimal(0);
        BigDecimal totPQty=new BigDecimal(0);
        BigDecimal totDistriAmt=new BigDecimal(0);
        for (DCP_MStockOutUpdateReq.Datas data : datas){

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

        UptBean ub1 = new UptBean("DCP_MSTOCKOUT");
        ub1.addUpdateValue("DOC_TYPE", new DataValue(request.getDocType(), Types.VARCHAR));
        ub1.addUpdateValue("BDATE", new DataValue(request.getBDate(), Types.VARCHAR));
        ub1.addUpdateValue("OTYPE", new DataValue(request.getOType(), Types.VARCHAR));
        ub1.addUpdateValue("OFNO", new DataValue(request.getOfNo(), Types.VARCHAR));
        ub1.addUpdateValue("OOTYPE", new DataValue(request.getOOType(), Types.VARCHAR));
        ub1.addUpdateValue("OOFNO", new DataValue(request.getOOfNo(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("ADJUSTSTATUS", new DataValue(request.getAdjustStatus(), Types.VARCHAR));
        ub1.addUpdateValue("OMSTOCKOUTNO", new DataValue(request.getOMStockOutNo(), Types.VARCHAR));
        ub1.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
        ub1.addUpdateValue("TOT_PQTY", new DataValue(totPQty, Types.VARCHAR));
        ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
        ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
        ub1.addUpdateValue("LOAD_DOCTYPE", new DataValue(request.getLoadDocType(), Types.VARCHAR));
        ub1.addUpdateValue("LOAD_DOCNO", new DataValue(request.getLoadDocNo(), Types.VARCHAR));
        ub1.addUpdateValue("EMPLOYEEID", new DataValue(request.getEmployeeId(), Types.VARCHAR));
        ub1.addUpdateValue("DEPARTID", new DataValue(request.getDepartId(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        ub1.addCondition("MSTOCKOUTNO", new DataValue(orderNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MStockOutUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MStockOutUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MStockOutUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MStockOutUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_MStockOutUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_MStockOutUpdateReq>(){};
    }

    @Override
    protected DCP_MStockOutUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_MStockOutUpdateRes();
    }

}
