package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettSettingCreateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettSettingCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_InterSettSettingCreate extends SPosAdvanceService<DCP_InterSettSettingCreateReq, DCP_InterSettSettingCreateRes> {

    @Override
    protected void processDUID(DCP_InterSettSettingCreateReq req, DCP_InterSettSettingCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_InterSettSettingCreateReq.LevelRequest request = req.getRequest();
        //String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        String processNo ="0001"; //req.getRequest().getProcessNo();

        //流程编码不得重复，类型+供货对象+需求对象 1/2 唯一性判断
        //String processNoCheckSql="select * from DCP_INTERSETTSETTING a where a.eid='"+eId+"' and a.processno='"+processNo+"'";
        //List<Map<String, Object>> processCheckList = this.doQueryData(processNoCheckSql, null);
        //if(processCheckList.size()>0){
        //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "流程编码不得重复！");
        //}

        String maxSql="select * from DCP_INTERSETTSETTING where eid='"+eId+"' ";
        List<Map<String, Object>> maxList = this.doQueryData(maxSql, null);
        if(maxList.size()>0){
            List<Integer> yetProcessNoList = maxList.stream().map(x -> {
                String yetProcessNo = x.get("PROCESSNO").toString();
                try {
                    return Integer.parseInt(yetProcessNo);
                } catch (Exception e) {
                    return 0;
                }
            }).distinct().collect(Collectors.toList());
            Integer maxProcessNo = yetProcessNoList.stream().max(Integer::compareTo).get();
            Integer minProcessNo = yetProcessNoList.stream().min(Integer::compareTo).get();
            if(minProcessNo==1){
                maxProcessNo=findMissingProcessNo(yetProcessNoList,minProcessNo,maxProcessNo);
                //maxProcessNo+=1;
                processNo = String.format("%04d", maxProcessNo);
            }
        }


        String otherCheckSql="select * from DCP_INTERSETTSETTING a where a.eid='"+eId+"' " +
                " and a.btype='"+req.getRequest().getBusinessType()+"'" +
                " and a.supplyobject='"+req.getRequest().getSupplyObject()+"' " +
                " and a.DEMANDOBJECT='"+req.getRequest().getDemandObject()+"' ";
        List<Map<String, Object>> otherCheckList = this.doQueryData(otherCheckSql, null);
        if(otherCheckList.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "类型+供货对象+需求对象 唯一性判断！");
        }


        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("CREATEBY", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
        mainColumns.add("CREATE_TIME", DataValues.newString(createTime));
        //mainColumns.add("MODIFYBY", DataValues.newString(employeeNo));
        //mainColumns.add("MODIFY_DATE", DataValues.newString(createTime));
        //mainColumns.add("MODIFY_TIME", DataValues.newString(createTime));
        mainColumns.add("STATUS", DataValues.newString(request.getStatus()));
        mainColumns.add("PROCESSNO", DataValues.newString(processNo));
        mainColumns.add("BTYPE", DataValues.newString(request.getBusinessType()));
        mainColumns.add("SUPPLYOBJECT", DataValues.newString(request.getSupplyObject()));
        mainColumns.add("DEMANDOBJECT", DataValues.newString(request.getDemandObject()));
        mainColumns.add("PRICETYPE", DataValues.newString(request.getPriceType()));
        mainColumns.add("RELATIONSHIP", DataValues.newString(request.getRelationship()));
        mainColumns.add("MEMO", DataValues.newString(request.getMemo()));
        mainColumns.add("VERSIONNUM", DataValues.newString("1"));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_INTERSETTSETTING",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        InsBean ib_v=new InsBean("DCP_INTERSETTSETTING_V",mainColumnNames);
        ib_v.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib_v));

        List<DCP_InterSettSettingCreateReq.ObjectList> objectList = req.getRequest().getObjectList();

        objectList.forEach(x->{
            if(x.getDemandObject()==null){
                x.setDemandObject("");
            }
        });

        List<DCP_InterSettSettingCreateReq.ObjectList> collect = objectList.stream().filter(x -> x.getDemandObject().equals(request.getDemandObject())).collect(Collectors.toList());
        if(collect.size()<=0){
            res.setErrorMsg("最后一项次的需求对象为单头的需求对象!");
            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "请选择正确的需求对象！");
        }

        for (DCP_InterSettSettingCreateReq.ObjectList objectListItem : objectList){
            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("PROCESSNO", DataValues.newString(processNo));
            detailColumns.add("ITEM", DataValues.newString(objectListItem.getItem()));
            detailColumns.add("OBJECT", DataValues.newString(objectListItem.getObject()));
            detailColumns.add("PRICETYPE", DataValues.newString(objectListItem.getPriceType()));
            detailColumns.add("PRICERATIO", DataValues.newString(objectListItem.getPriceRatio()));
            detailColumns.add("WAREHOUSE", DataValues.newString(objectListItem.getWarehouse()));
            detailColumns.add("STATUS", DataValues.newString(objectListItem.getStatus()));
            detailColumns.add("SUPPLYOBJECT", DataValues.newString(objectListItem.getSupplyObject()));
            detailColumns.add("DEMANDOBJECT1", DataValues.newString(objectListItem.getDemandObject()));
            detailColumns.add("VERSIONNUM", DataValues.newString("1"));
            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean detailIb=new InsBean("DCP_INTERSETTSETDETAIL",detailColumnNames);
            detailIb.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(detailIb));

            InsBean detailIb_V=new InsBean("DCP_INTERSETTSETDETAIL_V",detailColumnNames);
            detailIb_V.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(detailIb_V));
        }


        this.doExecuteDataToDB();
        res.setProcessNo(processNo);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    public int findMissingProcessNo(List<Integer> numbers, int min, int max) {
        boolean[] exists = new boolean[max - min + 1];
        for (int num : numbers) {
            if (num >= min && num <= max) {
                exists[num - min] = true;
            }
        }

        for (int i = 0; i < exists.length; i++) {
            if (!exists[i]) {
                return i + min;
            }
        }
        //没有缺返回最大的+1
        return max+1;
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettSettingCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettSettingCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettSettingCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettSettingCreateReq req) throws Exception {
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
    protected TypeToken<DCP_InterSettSettingCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_InterSettSettingCreateReq>(){};
    }

    @Override
    protected DCP_InterSettSettingCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_InterSettSettingCreateRes();
    }

}


