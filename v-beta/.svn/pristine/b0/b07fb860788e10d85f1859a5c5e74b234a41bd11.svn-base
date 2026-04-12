package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BatchingDocUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BatchingDocUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_BatchingDocUpdate extends SPosAdvanceService<DCP_BatchingDocUpdateReq, DCP_BatchingDocUpdateRes> {

    @Override
    protected void processDUID(DCP_BatchingDocUpdateReq req, DCP_BatchingDocUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_BatchingDocUpdateReq.LevelElm request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String isBatchNo= PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");

        String batchNo = req.getRequest().getBatchNo();
        String sql="select * from MES_BATCHING a where a.eid='"+eId+"' " +
                " and a.organizationno='"+req.getOrganizationNO()+"' and a.batchno='"+batchNo+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(list.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在！".toString());
        }
        String status = list.get(0).get("STATUS").toString();
        if(!"0".equals(status)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不能修改！".toString());
        }

        UptBean ub1 = new UptBean("MES_BATCHING");
        ub1.addUpdateValue("BDATE", DataValues.newString(request.getBDate()));
        ub1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
        ub1.addUpdateValue("LASTMODIOPNAME", DataValues.newString(req.getOpName()));
        ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(createTime));
        ub1.addUpdateValue("DOC_TYPE", DataValues.newString(request.getDocType()));
        ub1.addUpdateValue("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        ub1.addUpdateValue("DEPARTID", DataValues.newString(request.getDepartId()));
        ub1.addUpdateValue("TOT_PQTY", DataValues.newString(request.getTotPQty()));
        ub1.addUpdateValue("TOT_CQTY", DataValues.newString(request.getTotCQty()));
        ub1.addUpdateValue("STATUS", DataValues.newString(request.getStatus()));
        ub1.addUpdateValue("OOTYPE", DataValues.newString(request.getOOType()));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("BATCHNO", new DataValue(batchNo, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        DelBean db2 = new DelBean("MES_BATCHING_DETAIL");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("BATCHNO", new DataValue(batchNo, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("MES_BATCHING_DETAIL_MO");
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("BATCHNO", new DataValue(batchNo, Types.VARCHAR));
        db3.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        List<DCP_BatchingDocUpdateReq.Datas> datas = request.getDatas();

        List<String> pluNos = datas.stream().map(x -> "'"+x.getPluNo()+"'").distinct().collect(Collectors.toList());
        String pluNoJoin = String.join(",", pluNos);
        String pluSql="select * from dcp_goods a where a.eid='"+req.geteId()+"' and a.pluno in ("+pluNoJoin+")";
        List<Map<String, Object>> pluList = this.doQueryData(pluSql,null);

        List<String> punits = datas.stream().map(x -> "'"+x.getPUnit()+"'").distinct().collect(Collectors.toList());
        String punitJoin = String.join(",", punits);
        String punitSql="select * from dcp_unit a where a.eid='"+req.geteId()+"' and a.unit in ("+punitJoin+")";
        List<Map<String, Object>> punitList = this.doQueryData(punitSql,null);

        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("select  a.pluno,a.warehouse, a.BATCHNO,to_char(a.PRODDATE,'yyyy-MM-dd') PRODDATE,a.qty " +
                " from MES_BATCH_STOCK_DETAIL a " +
                " where a.eid='"+req.geteId()+"' " +
                " and a.organizationno='"+req.getOrganizationNO()+"' " +
                " and a.pluno in ("+pluNoJoin+")" +
                " and a.qty>0 " +
                " order by PRODDATE asc" +
                "");
        List<Map<String, Object>> batchStockList = this.doQueryData(sqlbuf.toString(),null);

        int batchMoItem=0;
        for (DCP_BatchingDocUpdateReq.Datas data : datas) {
            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
            detailColumns.add("BATCHNO", DataValues.newString(batchNo));
            detailColumns.add("ITEM", DataValues.newString(data.getItem()));
            detailColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
            detailColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
            detailColumns.add("PQTY", DataValues.newString(data.getPQty()));
            detailColumns.add("BENCOPIES", DataValues.newString(data.getBenCopies()));
            // detailColumns.add("BATCHTASKNO", DataValues.newString(data.getba()));
            detailColumns.add("PROCESSNO", DataValues.newString(data.getProcessNo()));
            detailColumns.add("OOFNO", DataValues.newString(data.getOOfNo()));
            detailColumns.add("OOITEM", DataValues.newString(data.getOOItem()));
            detailColumns.add("OFNO", DataValues.newString(data.getOfNo()));
            detailColumns.add("OITEM", DataValues.newString(data.getOItem()));
            detailColumns.add("PITEM", DataValues.newString(data.getPItem()));
            detailColumns.add("SITEM", DataValues.newString(data.getSItem()));
            detailColumns.add("FROMWAREHOUSE", DataValues.newString(data.getFromWarehouse()));
            detailColumns.add("TOWAREHOUSE", DataValues.newString(data.getToWarehouse()));
            detailColumns.add("ZITEM", DataValues.newString(data.getZItem()));
            detailColumns.add("OOTYPE", DataValues.newString(data.getOOType()));
            detailColumns.add("MPLUNO", DataValues.newString(data.getMPluNo()));
            detailColumns.add("BATCHQTY", DataValues.newString(data.getBatchQty()));
            detailColumns.add("MPUNIT", DataValues.newString(data.getMPUnit()));
            detailColumns.add("ISREPLACE", DataValues.newString(data.getIsReplace()));
            detailColumns.add("RITEM", DataValues.newString(data.getRItem()));
            detailColumns.add("REPLACERATIO", DataValues.newString(data.getReplaceRatio()));
            detailColumns.add("ISBUCKLE", DataValues.newString(data.getIsBuckle()));

            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean detailIb=new InsBean("MES_BATCHING_DETAIL",detailColumnNames);
            detailIb.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(detailIb));

            List<DCP_BatchingDocUpdateReq.BatchList> batchList = data.getBatchList();
            if(CollUtil.isNotEmpty(batchList)){
                for (DCP_BatchingDocUpdateReq.BatchList batch : batchList) {
                    batchMoItem++;
                    ColumnDataValue batchColumns=new ColumnDataValue();
                    batchColumns.add("EID", DataValues.newString(eId));
                    batchColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                    batchColumns.add("BATCHNO", DataValues.newString(batchNo));
                    batchColumns.add("ITEM", DataValues.newString(batchMoItem));
                    batchColumns.add("OITEM", DataValues.newString(data.getItem()));
                    batchColumns.add("SHAREPQTY", DataValues.newString(batch.getSharePQty()));
                    batchColumns.add("BASEUNIT", DataValues.newString(batch.getBaseUnit()));
                    batchColumns.add("BASEQTY", DataValues.newString(batch.getBaseQty()));
                    batchColumns.add("SOURCENO", DataValues.newString(""));//来源工单号
                    batchColumns.add("MITEM", DataValues.newString(""));//来源主件项次
                    batchColumns.add("PITEM", DataValues.newString(data.getPItem()));
                    batchColumns.add("SITEM", DataValues.newString(data.getSItem()));
                    batchColumns.add("ZITEM", DataValues.newString(data.getZItem()));
                    batchColumns.add("FROMWAREHOUSE", DataValues.newString(batch.getFromWarehouse()));
                    batchColumns.add("TOWAREHOUSE", DataValues.newString(batch.getToWarehouse()));
                    batchColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
                    batchColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
                    batchColumns.add("ISMOUT", DataValues.newString("N"));
                    batchColumns.add("BATCHTASKNO", DataValues.newString(""));//任务单号
                    batchColumns.add("BATCH", DataValues.newString(batch.getBatch()));

                    String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                    DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean batchlIb=new InsBean("MES_BATCHING_DETAIL_MO",batchColumnNames);
                    batchlIb.addValues(batchDataValues);
                    this.addProcessData(new DataProcessBean(batchlIb));
                }
            }
            else{
                List<Map<String, Object>> pluNoFilter = pluList.stream().filter(x -> x.get("PLUNO").toString().equals(data.getPluNo())).collect(Collectors.toList());
                if(pluNoFilter.size()>0){
                    String isBatch = pluNoFilter.get(0).get("ISBATCH").toString();
                    if("Y".equals(isBatch)&&"Y".equals(isBatchNo)){
                        Map<String, Object> baseMap = PosPub.getBaseQty(dao, req.geteId(), data.getPluNo(), data.getPUnit(), data.getPQty());

                        String baseUnit = baseMap.get("baseUnit").toString();
                        String unitRatio = baseMap.get("unitRatio").toString();
                        BigDecimal baseQty = new BigDecimal(baseMap.get("baseQty").toString());

                        List<Map<String, Object>> unitList = punitList.stream().filter(x -> x.get("UNIT").toString().equals(data.getPUnit())).collect(Collectors.toList());
                        int udLength=2;
                        if(unitList.size()>0){
                            udLength=Integer.valueOf(unitList.get(0).get("UDLENGTH").toString());
                        }

                        List<Map<String, Object>> stockFilterRows = batchStockList.stream().filter(x -> x.get("PLUNO").toString().equals(data.getPluNo()) && x.get("WAREHOUSE").equals(data.getFromWarehouse())).collect(Collectors.toList());

                        int batchItem=0;
                        if(stockFilterRows.size()>0){
                            for (Map<String, Object> stockRow : stockFilterRows){
                                if(baseQty.compareTo(BigDecimal.ZERO)<=0){
                                    continue;
                                }
                                BigDecimal shareBaseQty=new BigDecimal(0);
                                BigDecimal stockQty = new BigDecimal(stockRow.get("QTY").toString());
                                if(stockQty.compareTo(baseQty)>=0){
                                    shareBaseQty=baseQty;
                                }else{
                                    shareBaseQty=stockQty;
                                }
                                baseQty=baseQty.subtract(shareBaseQty);
                                BigDecimal shareQty=shareBaseQty.divide(new BigDecimal(unitRatio),udLength, RoundingMode.HALF_UP);
                                batchMoItem++;
                                ColumnDataValue batchColumns=new ColumnDataValue();
                                batchColumns.add("EID", DataValues.newString(eId));
                                batchColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                                batchColumns.add("BATCHNO", DataValues.newString(batchNo));
                                batchColumns.add("ITEM", DataValues.newString(batchMoItem));
                                batchColumns.add("OITEM", DataValues.newString(data.getItem()));
                                batchColumns.add("SHAREPQTY", DataValues.newString(shareQty));
                                batchColumns.add("BASEUNIT", DataValues.newString(baseUnit));
                                batchColumns.add("BASEQTY", DataValues.newString(shareBaseQty));
                                batchColumns.add("SOURCENO", DataValues.newString(""));//来源工单号
                                batchColumns.add("MITEM", DataValues.newString(""));//来源主件项次
                                batchColumns.add("PITEM", DataValues.newString(data.getPItem()));
                                batchColumns.add("SITEM", DataValues.newString(data.getSItem()));
                                batchColumns.add("ZITEM", DataValues.newString(data.getZItem()));
                                batchColumns.add("FROMWAREHOUSE", DataValues.newString(data.getFromWarehouse()));
                                batchColumns.add("TOWAREHOUSE", DataValues.newString(data.getToWarehouse()));
                                batchColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
                                batchColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
                                batchColumns.add("ISMOUT", DataValues.newString("N"));
                                batchColumns.add("BATCHTASKNO", DataValues.newString(""));//任务单号
                                batchColumns.add("BATCH", DataValues.newString(stockRow.get("BATCHNO").toString()));

                                String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                                DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean batchlIb=new InsBean("MES_BATCHING_DETAIL_MO",batchColumnNames);
                                batchlIb.addValues(batchDataValues);
                                this.addProcessData(new DataProcessBean(batchlIb));
                            }
                        }
                    }else{
                        Map<String, Object> baseMap = PosPub.getBaseQty(dao, req.geteId(), data.getPluNo(), data.getPUnit(), data.getPQty());
                        batchMoItem++;
                        String baseUnit = baseMap.get("baseUnit").toString();
                        String unitRatio = baseMap.get("unitRatio").toString();
                        String baseQty = baseMap.get("baseQty").toString();
                        ColumnDataValue batchColumns=new ColumnDataValue();
                        batchColumns.add("EID", DataValues.newString(eId));
                        batchColumns.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                        batchColumns.add("BATCHNO", DataValues.newString(batchNo));
                        batchColumns.add("ITEM", DataValues.newString(batchMoItem));
                        batchColumns.add("OITEM", DataValues.newString(data.getItem()));
                        batchColumns.add("SHAREPQTY", DataValues.newString(data.getPQty()));
                        batchColumns.add("BASEUNIT", DataValues.newString(baseUnit));
                        batchColumns.add("BASEQTY", DataValues.newString(baseQty));
                        batchColumns.add("SOURCENO", DataValues.newString(""));//来源工单号
                        batchColumns.add("MITEM", DataValues.newString(""));//来源主件项次
                        batchColumns.add("PITEM", DataValues.newString(data.getPItem()));
                        batchColumns.add("SITEM", DataValues.newString(data.getSItem()));
                        batchColumns.add("ZITEM", DataValues.newString(data.getZItem()));
                        batchColumns.add("FROMWAREHOUSE", DataValues.newString(data.getFromWarehouse()));
                        batchColumns.add("TOWAREHOUSE", DataValues.newString(data.getToWarehouse()));
                        batchColumns.add("PLUNO", DataValues.newString(data.getPluNo()));
                        batchColumns.add("PUNIT", DataValues.newString(data.getPUnit()));
                        batchColumns.add("ISMOUT", DataValues.newString("N"));
                        batchColumns.add("BATCHTASKNO", DataValues.newString(""));//任务单号
                        batchColumns.add("BATCH", DataValues.newString(""));

                        String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                        DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean batchlIb=new InsBean("MES_BATCHING_DETAIL_MO",batchColumnNames);
                        batchlIb.addValues(batchDataValues);
                        this.addProcessData(new DataProcessBean(batchlIb));
                    }
                }
            }

        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BatchingDocUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BatchingDocUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BatchingDocUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BatchingDocUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_BatchingDocUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_BatchingDocUpdateReq>(){};
    }

    @Override
    protected DCP_BatchingDocUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_BatchingDocUpdateRes();
    }

}
