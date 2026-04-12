package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReconliationBillUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReconliationBillUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_ReconliationBillUpdate extends SPosAdvanceService<DCP_ReconliationBillUpdateReq, DCP_ReconliationBillUpdateRes> {

    @Override
    protected void processDUID(DCP_ReconliationBillUpdateReq req, DCP_ReconliationBillUpdateRes res) throws Exception {

        String eId = req.geteId();
        String bizPartnerNo = req.getRequest().getBizPartnerNo();
        String bizType = req.getRequest().getBizType();
        String orgNo = req.getRequest().getOrgNo();
        String bdate = req.getRequest().getBdate();
        String edate = req.getRequest().getEdate();
        String isCheck = req.getRequest().getIsCheck();


        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        String sql="select a.status,b.sourceno,b.sourcenoseq " +
                " from DCP_RECONLIATION a " +
                " where a.eid='"+eId+"' and a.bizpartnerno='"+bizPartnerNo+"' " +
                " and a.bdate>='"+bdate+"' and a.bdate<='"+edate+"' " +
                " and a.corp='"+orgNo+"' and a.status='2' ";
        List<Map<String, Object>> list = this.doQueryData(sql,null);
        if(list.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "对账单不存在！");
        }
        List<String> reconNos = list.stream().map(y -> y.get("RECONNO").toString()).distinct().collect(Collectors.toList());

        //查询交易对象结算账期 DCP_BIZPARTNER_BILL
        String billSql="select * from DCP_BIZPARTNER_BILL a " +
                " where a.eid='"+eId+"' and a.BIZPARTNERNO='"+bizPartnerNo+"'" +
                " and a.billtype='"+bizType+"' " +
                " and a.organizationno='"+orgNo+"' ";
        List<Map<String, Object>> billList = this.doQueryData(billSql,null);

        if("Y".equals(isCheck)){
            if(CollUtil.isNotEmpty(billList)){
                String billNo = billList.get(0).get("BILLNO").toString();
                String item = billList.get(0).get("ITEM").toString();
                UptBean ub1 = new UptBean("DCP_BIZPARTNER_BILL");

                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));
                ub1.addCondition("BILLTYPE", new DataValue(bizType, Types.VARCHAR));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(orgNo, Types.VARCHAR));
                ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));

                if(billNo.length()>0) {
                    String[] splits = billNo.split(";");
                    //去掉list里面的reconNo
                    for (int i = 0; i < splits.length; i++) {
                        if (reconNos.contains(splits[i])) {
                            splits[i] = "";
                        }
                    }
                    List<String> filterRows = Arrays.stream(splits).filter(y -> y.length() > 0).distinct().collect(Collectors.toList());
                    filterRows.addAll(reconNos);
                    if(filterRows.size()>0) {
                        String billNoNew = String.join(";", filterRows);
                        ub1.addUpdateValue("BILLNO", new DataValue(billNoNew, Types.VARCHAR));
                    }else{
                        ub1.addUpdateValue("BILLNO", new DataValue("", Types.VARCHAR));
                        ub1.addUpdateValue("ISCHECK", new DataValue("N", Types.VARCHAR));
                    }
                    this.addProcessData(new DataProcessBean(ub1));
                }
            }else{
                String billNoNew = String.join(";", reconNos);
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("BIZPARTNERNO", DataValues.newString(bizPartnerNo));
                detailColumns.add("BILLTYPE", DataValues.newString(bizType));
                detailColumns.add("CONTRACTNO", DataValues.newString(""));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(orgNo));
                detailColumns.add("ITEM", DataValues.newString("1"));
                detailColumns.add("BDATE", DataValues.newString(bdate));
                detailColumns.add("EDATE", DataValues.newString(edate));
                detailColumns.add("BILLDATE", DataValues.newString(createDate));
                detailColumns.add("ISCHECK", DataValues.newString("Y"));
                detailColumns.add("BILLNO", DataValues.newString(billNoNew));


                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_BIZPARTNER_BILL",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }else if("N".equals(isCheck)){
            if(CollUtil.isNotEmpty(billList)){
                List<Map<String, Object>> collect = billList.stream().filter(x -> x.get("BDATE").toString().equals(bdate) && x.get("EDATE").equals(edate)).collect(Collectors.toList());
                if(collect.size()>0){
                    String billNo = collect.get(0).get("BILLNO").toString();
                    String item = collect.get(0).get("ITEM").toString();

                    UptBean ub1 = new UptBean("DCP_BIZPARTNER_BILL");

                    //condition
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));
                    ub1.addCondition("BILLTYPE", new DataValue(bizType, Types.VARCHAR));
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(orgNo, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));


                    if(billNo.length()>0){
                        String[] splits = billNo.split(";");
                        //去掉list里面的reconNo
                        for (int i = 0; i < splits.length; i++) {
                            if(reconNos.contains(splits[i])){
                                splits[i] = "";
                            }
                        }
                        List<String> filterRows = Arrays.stream(splits).filter(y -> y.length() > 0).distinct().collect(Collectors.toList());
                        if(filterRows.size()>0) {
                            String billNoNew = String.join(";", filterRows);
                            ub1.addUpdateValue("BILLNO", new DataValue(billNoNew, Types.VARCHAR));
                        }else{
                            ub1.addUpdateValue("BILLNO", new DataValue("", Types.VARCHAR));
                            ub1.addUpdateValue("ISCHECK", new DataValue("N", Types.VARCHAR));
                        }
                        this.addProcessData(new DataProcessBean(ub1));
                    }


                }
            }
        }

        res.setDatas(new ArrayList<>());
        for (String reconNo:reconNos){
            DCP_ReconliationBillUpdateRes.Datas datas = res.new Datas();
            datas.setBizPartnerNo(bizPartnerNo);
            datas.setBizType(bizType);
            datas.setOrgNo(orgNo);
            datas.setIsCheck(isCheck);
            datas.setReconNo(reconNo);
            res.getDatas().add(datas);
        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReconliationBillUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReconliationBillUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReconliationBillUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReconliationBillUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReconliationBillUpdateReq> getRequestType() {
        return new TypeToken<DCP_ReconliationBillUpdateReq>() {
        };
    }

    @Override
    protected DCP_ReconliationBillUpdateRes getResponseType() {
        return new DCP_ReconliationBillUpdateRes();
    }
}

