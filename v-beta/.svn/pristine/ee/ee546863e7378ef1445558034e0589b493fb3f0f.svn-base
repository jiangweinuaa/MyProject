package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TransApplyCreateReq;
import com.dsc.spos.json.cust.res.DCP_TransApplyCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_TransApplyCreate extends SPosAdvanceService<DCP_TransApplyCreateReq, DCP_TransApplyCreateRes> {

    @Override
    protected void processDUID(DCP_TransApplyCreateReq req, DCP_TransApplyCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String langType = req.getLangType();
        DCP_TransApplyCreateReq.level1Elm request = req.getRequest();
        if(Check.Null(request.getApproveOrgNo())){
            request.setApproveOrgNo(request.getTransOutOrgNo());
        }

        String billNo = "";//0 调拨申请  1领料申请
        if("1".equals(req.getRequest().getApplyType())){
            billNo = this.getOrderNO(req, "LLSQ");
        }else {
            billNo=this.getOrderNO(req, "DBSQ");
        }
        BigDecimal totCqty=BigDecimal.ZERO;
        BigDecimal totPqty=BigDecimal.ZERO;
        BigDecimal totPOqty=BigDecimal.ZERO;
        BigDecimal totAmt=BigDecimal.ZERO;
        BigDecimal totDistriAmt=BigDecimal.ZERO;

        List<DCP_TransApplyCreateReq.Detail> details = request.getDetail();
        if(CollUtil.isNotEmpty(details)){

            List<Map> pfCollection = details.stream().map(x -> {
                Map pfMap = new HashMap();
                pfMap.put("pluNo",x.getPluNo());
                pfMap.put("featureNo",x.getFeatureNo());

                return pfMap;
            }).distinct().collect(Collectors.toList());
            totCqty=new BigDecimal(pfCollection.size());

            int detailItem=0;
            for (DCP_TransApplyCreateReq.Detail detail : details){
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
                totPOqty=totPOqty.add(new BigDecimal(detail.getPoQty()));
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
                detailColumns.add("REASON", DataValues.newString(detail.getReason()));

                detailColumns.add("PRICE", DataValues.newString(detail.getPrice()));
                detailColumns.add("AMT", DataValues.newString(detail.getAmt()));
                detailColumns.add("DISTRIPRICE", DataValues.newString(detail.getDistriPrice()));
                detailColumns.add("DISTRIAMT", DataValues.newString(detail.getDistriAmt()));
                detailColumns.add("MEMO", DataValues.newString(detail.getMemo()));
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

        List<DCP_TransApplyCreateReq.Source> sourceList = request.getSource();

        if(CollUtil.isNotEmpty(sourceList)) {
            for (DCP_TransApplyCreateReq.Source source : sourceList) {
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


        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));

        mainColumns.add("BDATE", DataValues.newString(request.getBDate()));
        mainColumns.add("BILLNO", DataValues.newString(billNo));
        mainColumns.add("RDATE", DataValues.newString(request.getRDate()));
        mainColumns.add("TRANSTYPE", DataValues.newString(request.getTransType()));
        mainColumns.add("TRANSOUTORGNO", DataValues.newString(request.getTransOutOrgNo()));
        mainColumns.add("APPROVEORGNO", DataValues.newString(request.getApproveOrgNo()));
        mainColumns.add("TRANSOUTWAREHOUSE", DataValues.newString(request.getTransOutWarehouse()));
        mainColumns.add("TRANSINORGNO", DataValues.newString(request.getTransInOrgNo()));
        mainColumns.add("TRANSINWAREHOUSE", DataValues.newString(request.getTransInWarehouse()));
        mainColumns.add("PTEMPLATENO", DataValues.newString(request.getPTemplateNo()));
        mainColumns.add("ISTRANINCONFIRM", DataValues.newString(Check.Null(request.getIsTranInConfirm())?"N":request.getIsTranInConfirm()));
        mainColumns.add("TOTCQTY", DataValues.newString(totCqty.toString()));
        mainColumns.add("TOTPQTY", DataValues.newString(totPqty.toString()));
        mainColumns.add("TOTPOQTY", DataValues.newString(request.getTotPoQty()));

        mainColumns.add("TOTDISTRIAMT", DataValues.newString(request.getTotDistriAmt()));
        mainColumns.add("TOTAMT", DataValues.newString(request.getTotAmt()));
        mainColumns.add("REASON", DataValues.newString(""));
        mainColumns.add("STATUS", DataValues.newString("0"));

        mainColumns.add("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        mainColumns.add("DEPARTID", DataValues.newString(request.getDepartId()));
        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
        mainColumns.add("CREATETIME", new DataValue(createTime,Types.DATE));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("APPLYTYPE", DataValues.newString(request.getApplyType()));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_TRANSAPPLY",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));


        this.doExecuteDataToDB();
        res.setBillNo(billNo);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TransApplyCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TransApplyCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TransApplyCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TransApplyCreateReq req) throws Exception {
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
    protected TypeToken<DCP_TransApplyCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_TransApplyCreateReq>(){};
    }

    @Override
    protected DCP_TransApplyCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_TransApplyCreateRes();
    }

}

