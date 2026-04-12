package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProdScheduleUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProdScheduleUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DCP_ProdScheduleUpdate extends SPosAdvanceService<DCP_ProdScheduleUpdateReq, DCP_ProdScheduleUpdateRes> {

    @Override
    protected void processDUID(DCP_ProdScheduleUpdateReq req,DCP_ProdScheduleUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ProdScheduleUpdateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String billNo = request.getBillNo();

        DelBean db2 = new DelBean("DCP_PRODSCHEDULE_DETAIL");
        db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_PRODSCHEDULE_SOURCE");
        db3.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));


        UptBean ub1 = new UptBean("DCP_PRODSCHEDULE");
        //add Value
        ub1.addUpdateValue("SEMIWOGENTYPE", DataValues.newString(req.getRequest().getSemiWOGenType()));

        ub1.addUpdateValue("BDATE", new DataValue(DateFormatUtils.getPlainDate(request.getBDate()), Types.VARCHAR));
        ub1.addUpdateValue("BEGINDATE", new DataValue(DateFormatUtils.getPlainDate(request.getBeginDate()), Types.VARCHAR));
        ub1.addUpdateValue("ENDATE", new DataValue(DateFormatUtils.getPlainDate(request.getEndDate()), Types.VARCHAR));

        ub1.addUpdateValue("EMPLOYEEID", new DataValue(request.getEmployeeId(), Types.VARCHAR));
        ub1.addUpdateValue("DEPARTID", new DataValue(request.getDepartId(), Types.VARCHAR));
        ub1.addUpdateValue("TOTPQTY", new DataValue(request.getTotPqty(), Types.DECIMAL));
        ub1.addUpdateValue("TOTCQTY", new DataValue(request.getTotCqty(), Types.DECIMAL));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFYBY", new DataValue(employeeNo, Types.VARCHAR));
        ub1.addUpdateValue("MODIFYTIME", new DataValue(createTime, Types.DATE));

        //condition
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        List<DCP_ProdScheduleUpdateReq.Detail> detail = request.getDetail();
        if (CollUtil.isNotEmpty(detail))
        {
            for (DCP_ProdScheduleUpdateReq.Detail d : detail){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                detailColumns.add("BILLNO", DataValues.newString(billNo));
                detailColumns.add("ITEM", DataValues.newString(d.getItem()));
                detailColumns.add("PLUNO", DataValues.newString(d.getPluNo()));
                detailColumns.add("FEATURENO", DataValues.newString(d.getFeatureNo()));
                detailColumns.add("UPPLUNO", DataValues.newString(""));
                detailColumns.add("RDATE", DataValues.newString(d.getRDate()));
                detailColumns.add("PGROUPNO", DataValues.newString(d.getPGroupNo()));
                detailColumns.add("PUNIT", DataValues.newString(d.getPUnit()));
                detailColumns.add("PQTY", DataValues.newDecimal(d.getPQty()));
                detailColumns.add("POQTY", DataValues.newDecimal(d.getPoQty()));
                detailColumns.add("STOCKQTY", DataValues.newDecimal(d.getStockQty()));
                detailColumns.add("SHORTQTY", DataValues.newDecimal(d.getShortQty()));
                detailColumns.add("ADVICEQTY", DataValues.newDecimal(d.getAdviceQty()));
                detailColumns.add("MINQTY", DataValues.newDecimal(d.getMinQty()));
                detailColumns.add("MULQTY", DataValues.newDecimal(d.getMulQty()));
                detailColumns.add("REMAINTYPE", DataValues.newString(d.getRemainType()));
                detailColumns.add("PREDAYS", DataValues.newInteger(d.getPreDays()));
                detailColumns.add("BASEUNIT", DataValues.newString(d.getBaseUnit()));
                //detailColumns.add("BASEQTY", DataValues.newDecimal(d.getba()));
                detailColumns.add("UNITRATIO", DataValues.newDecimal(d.getUnitRatio()));
                detailColumns.add("MEMO", DataValues.newString(d.getMemo()));
                detailColumns.add("BOMNO", DataValues.newString(d.getBomNo()));
                detailColumns.add("VERSIONNUM", DataValues.newString(d.getVersionNum()));
                detailColumns.add("SOURCETYPE", DataValues.newString(d.getSourceType()));
                detailColumns.add("ODDVALUE", DataValues.newString(d.getOddValue()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_PRODSCHEDULE_DETAIL",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));

                List<DCP_ProdScheduleUpdateReq.SourceList> sourceList = d.getSourceList();
                if (CollUtil.isNotEmpty(sourceList))
                {
                    List<BigDecimal> poColl = sourceList.stream().map(x -> Check.NotNull(x.getPoQty()) ? new BigDecimal(0) : new BigDecimal(x.getPoQty())).collect(Collectors.toList());
                    BigDecimal allPoQty=new BigDecimal(0);
                    for (BigDecimal poQty : poColl){
                        allPoQty=allPoQty.add(poQty);
                    }
                    BigDecimal yetPoQty=new BigDecimal(0);
                    int sourceItem=0;
                    for (DCP_ProdScheduleUpdateReq.SourceList sl : sourceList){
                        sourceItem++;
                        ColumnDataValue sourceListColumns=new ColumnDataValue();
                        sourceListColumns.add("EID", DataValues.newString(eId));
                        sourceListColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                        sourceListColumns.add("BILLNO", DataValues.newString(billNo));
                        sourceListColumns.add("ITEM", DataValues.newString(sl.getItem()));
                        sourceListColumns.add("OITEM", DataValues.newDecimal(d.getItem()));
                        sourceListColumns.add("OBJECTTYPE", DataValues.newString(sl.getObjectType()));
                        sourceListColumns.add("OBJECTID", DataValues.newString(sl.getObjectId()));
                        sourceListColumns.add("ORDERTYPE", DataValues.newString(sl.getOrderType()));
                        sourceListColumns.add("ORDERNO", DataValues.newString(sl.getOrderNo()));
                        sourceListColumns.add("ORDERITEM", DataValues.newDecimal(sl.getOrderItem()));
                        sourceListColumns.add("PLUNO", DataValues.newString(d.getPluNo()));
                        sourceListColumns.add("FEATURENO", DataValues.newString(d.getFeatureNo()));
                        sourceListColumns.add("RUNIT", DataValues.newString(sl.getRUnit()));
                        sourceListColumns.add("RQTY", DataValues.newDecimal(sl.getRQty()));
                        sourceListColumns.add("RDATE", DataValues.newString(d.getRDate()));
                        sourceListColumns.add("PUNIT", DataValues.newString(sl.getPUnit()));
                        sourceListColumns.add("POQTY", DataValues.newDecimal(sl.getPoQty()));
                        sourceListColumns.add("PTEMPLATENO", DataValues.newString(sl.getPtemplateNo()));

                        BigDecimal allotPoqty=new BigDecimal(0);
                        if(sourceItem!=sourceList.size()) {
                            if (!Check.Null(sl.getPoQty())) {
                                allotPoqty = new BigDecimal(d.getPQty()).multiply(new BigDecimal(sl.getPoQty())).divide(allPoQty, 2, RoundingMode.HALF_UP);
                            }
                        }else{
                            allotPoqty=allPoQty.subtract(yetPoQty);
                        }
                        yetPoQty=yetPoQty.add(allotPoqty);
                        sourceListColumns.add("ALLOTPQTY", DataValues.newDecimal(allotPoqty));
                        String[] sourceListColumnNames = sourceListColumns.getColumns().toArray(new String[0]);
                        DataValue[] sourceListDataValues = sourceListColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean sourceListib=new InsBean("DCP_PRODSCHEDULE_SOURCE",sourceListColumnNames);
                        sourceListib.addValues(sourceListDataValues);
                        this.addProcessData(new DataProcessBean(sourceListib));

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
    protected List<InsBean> prepareInsertData(DCP_ProdScheduleUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProdScheduleUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProdScheduleUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProdScheduleUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_ProdScheduleUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ProdScheduleUpdateReq>(){};
    }

    @Override
    protected DCP_ProdScheduleUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ProdScheduleUpdateRes();
    }

}


