package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProdScheduleCreateReq;
import com.dsc.spos.json.cust.res.DCP_ProdScheduleCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DCP_ProdScheduleCreate extends SPosAdvanceService<DCP_ProdScheduleCreateReq, DCP_ProdScheduleCreateRes> {

    @Override
    protected void processDUID(DCP_ProdScheduleCreateReq req, DCP_ProdScheduleCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ProdScheduleCreateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String billNo = this.getOrderNO(req, "PCJH");

        ColumnDataValue mainColumns = new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
        mainColumns.add("BILLNO", DataValues.newString(billNo));
        mainColumns.add("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(request.getBDate())));
        mainColumns.add("BEGINDATE", DataValues.newString(DateFormatUtils.getPlainDate(request.getBeginDate())));
        mainColumns.add("ENDATE", DataValues.newString(DateFormatUtils.getPlainDate(request.getEndDate())));
        mainColumns.add("EMPLOYEEID", DataValues.newString(employeeNo));
        mainColumns.add("DEPARTID", DataValues.newString(departmentNo));
        mainColumns.add("SEMIWOGENTYPE", DataValues.newString(request.getSemiWOGenType()));
        mainColumns.add("TOTPQTY", DataValues.newDecimal(request.getTotPqty()));
        mainColumns.add("TOTCQTY", DataValues.newDecimal(request.getTotCqty()));
        mainColumns.add("TOTWOQTY", DataValues.newDecimal(0));
        mainColumns.add("STATUS", DataValues.newString("0"));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("CREATEBY", DataValues.newString(employeeNo));
        mainColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib = new InsBean("DCP_PRODSCHEDULE", mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        List<DCP_ProdScheduleCreateReq.Detail> detail = request.getDetail();
        if (CollUtil.isNotEmpty(detail)) {
            int item = 0;
            for (DCP_ProdScheduleCreateReq.Detail d : detail) {
                ColumnDataValue detailColumns = new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                detailColumns.add("BILLNO", DataValues.newString(billNo));
                detailColumns.add("ITEM", DataValues.newString(d.getItem()));
                detailColumns.add("PLUNO", DataValues.newString(d.getPluNo()));
                detailColumns.add("FEATURENO", DataValues.newString(d.getFeatureNo()));
                detailColumns.add("UPPLUNO", DataValues.newString(""));
                detailColumns.add("RDATE", DataValues.newString(DateFormatUtils.getPlainDate(d.getRDate())));
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
                InsBean detailib = new InsBean("DCP_PRODSCHEDULE_DETAIL", detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));

                List<DCP_ProdScheduleCreateReq.SourceList> sourceList = d.getSourceList();
                if (CollUtil.isNotEmpty(sourceList)) {
                    BigDecimal allPoQty = new BigDecimal(0);
                    for (DCP_ProdScheduleCreateReq.SourceList oneList: sourceList){
                          if (StringUtils.isNotEmpty(oneList.getPoQty())){
                              allPoQty = allPoQty.add(new BigDecimal(oneList.getPoQty()));
                          }
                    }

                    BigDecimal yetPoQty = new BigDecimal(0);
                    int sourceItem = 0;
                    for (DCP_ProdScheduleCreateReq.SourceList sl : sourceList) {
                        sourceItem++;
                        ColumnDataValue sourceListColumns = new ColumnDataValue();
                        sourceListColumns.add("EID", DataValues.newString(eId));
                        sourceListColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                        sourceListColumns.add("BILLNO", DataValues.newString(billNo));
                        sourceListColumns.add("ITEM", DataValues.newString(++item));
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
                        sourceListColumns.add("RDATE", DataValues.newString(DateFormatUtils.getPlainDate(d.getRDate())));
                        sourceListColumns.add("PUNIT", DataValues.newString(sl.getPUnit()));
                        sourceListColumns.add("POQTY", DataValues.newDecimal(sl.getPoQty()));
                        sourceListColumns.add("PTEMPLATENO", DataValues.newString(sl.getPtemplateNo()));

                        BigDecimal allotPoqty = new BigDecimal(0);
                        if (sourceItem != sourceList.size()) {
                            if (!Check.Null(sl.getPoQty()) && BigDecimal.ZERO.compareTo(allotPoqty)!=0) {
                                allotPoqty = new BigDecimal(d.getPQty()).multiply(new BigDecimal(sl.getPoQty())).divide(allPoQty, 2, RoundingMode.HALF_UP);
                            }
                        } else {
                            allotPoqty = allPoQty.subtract(yetPoQty);
                        }
                        yetPoQty = yetPoQty.add(allotPoqty);
                        sourceListColumns.add("ALLOTPQTY", DataValues.newDecimal(allotPoqty));
                        String[] sourceListColumnNames = sourceListColumns.getColumns().toArray(new String[0]);
                        DataValue[] sourceListDataValues = sourceListColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean sourceListib = new InsBean("DCP_PRODSCHEDULE_SOURCE", sourceListColumnNames);
                        sourceListib.addValues(sourceListDataValues);
                        this.addProcessData(new DataProcessBean(sourceListib));

                    }
                }
            }
        }

        res.setBillNo(billNo);

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProdScheduleCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProdScheduleCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProdScheduleCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProdScheduleCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProdScheduleCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ProdScheduleCreateReq>() {
        };
    }

    @Override
    protected DCP_ProdScheduleCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ProdScheduleCreateRes();
    }

}


