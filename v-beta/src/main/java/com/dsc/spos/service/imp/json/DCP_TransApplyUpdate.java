package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TransApplyCreateReq;
import com.dsc.spos.json.cust.req.DCP_TransApplyUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TransApplyUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
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

public class DCP_TransApplyUpdate  extends SPosAdvanceService<DCP_TransApplyUpdateReq, DCP_TransApplyUpdateRes> {

    @Override
    protected void processDUID(DCP_TransApplyUpdateReq req, DCP_TransApplyUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String langType = req.getLangType();
        DCP_TransApplyUpdateReq.level1Elm request = req.getRequest();
        if(Check.Null(request.getApproveOrgNo())){
            request.setApproveOrgNo(request.getTransOutOrgNo());
        }

        String billNo = req.getRequest().getBillNo();

        DelBean db2 = new DelBean("DCP_TRANSAPPLY_DETAIL");
        db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_TRANSAPPLY_SOURCE");
        db3.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));


        BigDecimal totCqty=BigDecimal.ZERO;
        BigDecimal totPqty=BigDecimal.ZERO;
        BigDecimal totAmt=BigDecimal.ZERO;
        BigDecimal totDistriAmt=BigDecimal.ZERO;

        List<DCP_TransApplyUpdateReq.Detail> details = request.getDetail();
        if(CollUtil.isNotEmpty(details)){

            List<Map> pfCollection = details.stream().map(x -> {
                Map pfMap = new HashMap();
                pfMap.put("pluNo",x.getPluNo());
                pfMap.put("featureNo",x.getFeatureNo());

                return pfMap;
            }).distinct().collect(Collectors.toList());
            totCqty=new BigDecimal(pfCollection.size());

            int detailItem=0;
            for (DCP_TransApplyUpdateReq.Detail detail : details){
                detailItem++;

                Map<String, Object> baseMap = PosPub.getBaseQty(dao, eId, detail.getPluNo(), detail.getPUnit(), detail.getPoQty());
                if (baseMap == null)
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "查找DCP_GOODS_UNIT单位转换表pluno="+detail.getPluNo()+",OUNIT="+detail.getPUnit()+"无记录！");
                }
                if(Check.Null(detail.getBaseUnit())){
                    detail.setBaseQty(baseMap.get("baseUnit").toString());
                }
                if(Check.Null(detail.getUnitRatio())){
                    detail.setUnitRatio(baseMap.get("unitRatio").toString());
                }
                if(Check.Null(detail.getBaseQty())){
                    detail.setBaseQty(baseMap.get("baseQty").toString());
                }

                totPqty=totPqty.add(new BigDecimal(0));
                totAmt=totAmt.add(new BigDecimal(detail.getAmt()));
                totDistriAmt=totDistriAmt.add(new BigDecimal(detail.getDistriAmt()));

                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                detailColumns.add("BILLNO", DataValues.newString(billNo));

                detailColumns.add("ITEM", DataValues.newString(detailItem));
                detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                detailColumns.add("PLUBARCODE", DataValues.newString(detail.getPluBarcode()));
                detailColumns.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
                detailColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
                detailColumns.add("POQTY", DataValues.newString(detail.getPoQty()));
                detailColumns.add("PQTY", DataValues.newString("0"));
                detailColumns.add("BASEUNIT", DataValues.newString(detail.getBaseUnit()));
                detailColumns.add("BASEQTY", DataValues.newString(detail.getBaseQty()));
                detailColumns.add("UNITRATIO", DataValues.newString(detail.getUnitRatio()));

                detailColumns.add("PRICE", DataValues.newString(detail.getPrice()));
                detailColumns.add("AMT", DataValues.newString(detail.getAmt()));
                detailColumns.add("DISTRIPRICE", DataValues.newString(detail.getDistriPrice()));
                detailColumns.add("DISTRIAMT", DataValues.newString(detail.getDistriAmt()));
                detailColumns.add("MEMO", DataValues.newString(detail.getMemo()));
                detailColumns.add("REASON", DataValues.newString(detail.getReason()));
                detailColumns.add("STATUS", DataValues.newString("0"));

                detailColumns.add("PICKMINQTY", DataValues.newString(detail.getPickMinQty()));
                detailColumns.add("PICKMULQTY", DataValues.newString(detail.getPickMulQty()));


                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_TRANSAPPLY_DETAIL",detailColumnNames);
                ib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(ib));

            }
        }

        List<DCP_TransApplyUpdateReq.Source> sourceList = request.getSource();
        if(CollUtil.isNotEmpty(sourceList)) {
            for (DCP_TransApplyUpdateReq.Source source : sourceList) {
                ColumnDataValue sourceColumns = new ColumnDataValue();
                sourceColumns.add("EID", DataValues.newString(eId));
                sourceColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                sourceColumns.add("BILLNO", DataValues.newString(billNo));

                sourceColumns.add("ITEM", DataValues.newString(source.getItem()));
                sourceColumns.add("OFNO", DataValues.newString(source.getOfNo()));
                sourceColumns.add("OTYPE", DataValues.newString(source.getOType()));
                sourceColumns.add("OITEM", DataValues.newString(source.getOItem()));
                sourceColumns.add("PLUNO", DataValues.newString(source.getPluNo()));
                sourceColumns.add("FEATURENO", DataValues.newString(source.getFeatureNo()));
                sourceColumns.add("PQTY", DataValues.newString(source.getPQty()));
                sourceColumns.add("PUNIT", DataValues.newString(source.getPUnit()));


                String[] sourceColumnNames = sourceColumns.getColumns().toArray(new String[0]);
                DataValue[] sourceDataValues = sourceColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib = new InsBean("DCP_TRANSAPPLY_SOURCE", sourceColumnNames);
                ib.addValues(sourceDataValues);
                this.addProcessData(new DataProcessBean(ib));
            }
        }


        UptBean ub1 = null;
        ub1 = new UptBean("DCP_TRANSAPPLY");
        //add Value

        ub1.addUpdateValue("BDATE", new DataValue(request.getBDate(), Types.VARCHAR));
        ub1.addUpdateValue("RDATE", new DataValue(request.getRDate(), Types.VARCHAR));
        ub1.addUpdateValue("TRANSTYPE", DataValues.newString(request.getTransType()));
        ub1.addUpdateValue("TRANSOUTORGNO", DataValues.newString(request.getTransOutOrgNo()));
        ub1.addUpdateValue("APPROVEORGNO", DataValues.newString(request.getApproveOrgNo()));
        ub1.addUpdateValue("TRANSOUTWAREHOUSE", DataValues.newString(request.getTransOutWarehouse()));
        ub1.addUpdateValue("TRANSINORGNO", DataValues.newString(request.getTransInOrgNo()));
        ub1.addUpdateValue("TRANSINWAREHOUSE", DataValues.newString(request.getTransInWarehouse()));
        ub1.addUpdateValue("PTEMPLATENO", DataValues.newString(request.getPTemplateNo()));
        ub1.addUpdateValue("ISTRANINCONFIRM", DataValues.newString(request.getIsTranInConfirm()));
        ub1.addUpdateValue("TOTCQTY", DataValues.newString(totCqty.toString()));
        ub1.addUpdateValue("TOTPQTY", DataValues.newString(totPqty.toString()));
        ub1.addUpdateValue("TOTPOQTY", DataValues.newString(request.getTotPoQty()));
        ub1.addUpdateValue("TOTAMT", DataValues.newString(request.getTotAmt().toString()));
        ub1.addUpdateValue("TOTDISTRIAMT", DataValues.newString(request.getTotDistriAmt().toString()));
        ub1.addUpdateValue("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        ub1.addUpdateValue("DEPARTID", DataValues.newString(request.getDepartId()));
        ub1.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
        ub1.addUpdateValue("MODIFYTIME", new DataValue(createTime, Types.DATE));
        ub1.addUpdateValue("MEMO", DataValues.newString(request.getMemo()));
        ub1.addUpdateValue("APPLYTYPE", DataValues.newString(request.getApplyType()));

        //condition
        ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TransApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TransApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TransApplyUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TransApplyUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_TransApplyUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_TransApplyUpdateReq>(){};
    }

    @Override
    protected DCP_TransApplyUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_TransApplyUpdateRes();
    }

}

