package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettSettingUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettSettingUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DCP_InterSettSettingUpdate  extends SPosAdvanceService<DCP_InterSettSettingUpdateReq, DCP_InterSettSettingUpdateRes> {

    @Override
    protected void processDUID(DCP_InterSettSettingUpdateReq req, DCP_InterSettSettingUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_InterSettSettingUpdateReq.LevelRequest request = req.getRequest();
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        String processNo = req.getRequest().getProcessNo();

        //流程编码不得重复，类型+供货对象+需求对象 1/2 唯一性判断
        String otherCheckSql="select * from DCP_INTERSETTSETTING a where a.eid='"+eId+"' " +
                " and a.btype='"+req.getRequest().getBusinessType()+"'" +
                " and a.supplyobject='"+req.getRequest().getSupplyObject()+"' " +
                " and a.DEMANDOBJECT='"+req.getRequest().getDemandObject()+"' " +
                " and a.processNo!='"+req.getRequest().getProcessNo()+"'";
        List<Map<String, Object>> otherCheckList = this.doQueryData(otherCheckSql, null);
        if(otherCheckList.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "类型+供货对象+需求对象 1/2 唯一性判断！");
        }

        String sql1="select * from DCP_INTERSETTSETTING a where a.eid='"+req.geteId()+"' " +
                " and a.processno='"+processNo+"' ";
        List<Map<String, Object>> list1 = this.doQueryData(sql1, null);

        String sql2="select * from DCP_INTERSETTSETDETAIL a where a.eid='"+req.geteId()+"' " +
                " and a.processno='"+processNo+"'";
        List<Map<String, Object>> list2 = this.doQueryData(sql2, null);

        AtomicReference<Boolean> isUpdate= new AtomicReference<>(false);
        int yetVersionNum=0;
        if(list1.size()>0){
            yetVersionNum=Integer.parseInt(Check.Null(list1.get(0).get("VERSIONNUM").toString())?"1":list1.get(0).get("VERSIONNUM").toString());

            if(request.getBusinessType()==null){
                request.setBusinessType("");
            }
            if(request.getSupplyObject()==null){
                request.setSupplyObject("");
            }
            if(request.getDemandObject()==null){
                request.setDemandObject("");
            }
            if(request.getPriceType()==null){
                request.setPriceType("");
            }
            if(request.getRelationship()==null){
                request.setRelationship("");
            }
            if(!list1.get(0).get("BTYPE").toString().equals(request.getBusinessType())){
                isUpdate.set(true);
            }
            if(!list1.get(0).get("SUPPLYOBJECT").toString().equals(request.getSupplyObject())){
                isUpdate.set(true);
            }
            if(!list1.get(0).get("DEMANDOBJECT").toString().equals(request.getDemandObject())){
                isUpdate.set(true);
            }
            if(!list1.get(0).get("PRICETYPE").toString().equals(request.getPriceType())){
                isUpdate.set(true);
            }
            if(!list1.get(0).get("RELATIONSHIP").toString().equals(request.getRelationship())){
                isUpdate.set(true);
            }

            if(list2.size()!=request.getObjectList().size()){
                isUpdate.set(true);
            }

            request.getObjectList().forEach(x->{
                List<Map<String, Object>> filterRows = list2.stream().filter(y -> y.get("ITEM").toString().equals(x.getItem())).collect(Collectors.toList());
                if (filterRows.size() == 0){
                    isUpdate.set(true);
                }

                if(x.getObject()==null){
                    x.setObject("");
                }
                if(x.getPriceType()==null){
                    x.setPriceType("");
                }
                if(x.getPriceRatio()==null){
                    x.setPriceRatio("");
                }
                if(x.getWarehouse()==null){
                    x.setWarehouse("");
                }
                if(x.getSupplyObject()==null){
                    x.setSupplyObject("");
                }
                if(x.getDemandObject()==null){
                    x.setDemandObject("");
                }

                if(filterRows.size()>0) {
                    if (!filterRows.get(0).get("OBJECT").toString().equals(x.getObject())) {
                        isUpdate.set(true);
                    }
                    if (!filterRows.get(0).get("PRICETYPE").toString().equals(x.getPriceType())) {
                        isUpdate.set(true);
                    }
                    if (!filterRows.get(0).get("PRICERATIO").toString().equals(x.getPriceRatio())) {
                        isUpdate.set(true);
                    }
                    if (!filterRows.get(0).get("WAREHOUSE").toString().equals(x.getWarehouse())) {
                        isUpdate.set(true);
                    }
                    if (!filterRows.get(0).get("SUPPLYOBJECT").toString().equals(x.getSupplyObject())) {
                        isUpdate.set(true);
                    }
                    if (!filterRows.get(0).get("DEMANDOBJECT1").toString().equals(x.getDemandObject())) {
                        isUpdate.set(true);
                    }
                }

            });

        }
        int nowVersionNum=yetVersionNum+1;

        DelBean db1 = new DelBean("DCP_INTERSETTSETDETAIL");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("PROCESSNO", new DataValue(processNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        UptBean ub1 = new UptBean("DCP_INTERSETTSETTING");
        ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
        ub1.addUpdateValue("BTYPE", new DataValue(request.getBusinessType(), Types.VARCHAR));
        ub1.addUpdateValue("SUPPLYOBJECT", new DataValue(request.getSupplyObject(), Types.VARCHAR));
        ub1.addUpdateValue("DEMANDOBJECT", new DataValue(request.getDemandObject(), Types.VARCHAR));
        ub1.addUpdateValue("PRICETYPE", new DataValue(request.getPriceType(), Types.VARCHAR));
        ub1.addUpdateValue("RELATIONSHIP", new DataValue(request.getRelationship(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFYBY", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
        ub1.addUpdateValue("MODIFY_DATE", DataValues.newString(createDate));
        ub1.addUpdateValue("MODIFY_TIME", DataValues.newString(createTime));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("PROCESSNO", new DataValue(processNo, Types.VARCHAR));
        if(isUpdate.get()){
            ub1.addUpdateValue("VERSIONNUM", DataValues.newInteger(nowVersionNum));
        }
        this.addProcessData(new DataProcessBean(ub1));

        if(isUpdate.get()){
            //更新了添加一个版本
            ColumnDataValue mainColumns=new ColumnDataValue();
            mainColumns.add("EID", DataValues.newString(eId));
            mainColumns.add("CREATEBY", DataValues.newString(list1.get(0).get("CREATEBY").toString()));
            mainColumns.add("CREATE_DATE", DataValues.newString(list1.get(0).get("CREATE_DATE").toString()));
            mainColumns.add("CREATE_TIME", DataValues.newString(list1.get(0).get("CREATE_TIME").toString()));
            mainColumns.add("MODIFYBY", DataValues.newString(list1.get(0).get("MODIFYBY").toString()));
            mainColumns.add("MODIFY_DATE", DataValues.newString(list1.get(0).get("MODIFY_DATE").toString()));
            mainColumns.add("MODIFY_TIME", DataValues.newString(list1.get(0).get("MODIFY_TIME").toString()));
            mainColumns.add("STATUS", DataValues.newString(list1.get(0).get("STATUS").toString()));
            mainColumns.add("PROCESSNO", DataValues.newString(list1.get(0).get("PROCESSNO").toString()));
            mainColumns.add("BTYPE", DataValues.newString(list1.get(0).get("BTYPE").toString()));
            mainColumns.add("SUPPLYOBJECT", DataValues.newString(list1.get(0).get("SUPPLYOBJECT").toString()));
            mainColumns.add("DEMANDOBJECT", DataValues.newString(list1.get(0).get("DEMANDOBJECT").toString()));
            mainColumns.add("PRICETYPE", DataValues.newString(list1.get(0).get("PRICETYPE").toString()));
            mainColumns.add("RELATIONSHIP", DataValues.newString(list1.get(0).get("RELATIONSHIP").toString()));
            mainColumns.add("MEMO", DataValues.newString(list1.get(0).get("MEMO").toString()));
            mainColumns.add("VERSIONNUM", DataValues.newString(nowVersionNum));

            String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
            DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ib=new InsBean("DCP_INTERSETTSETTING_V",mainColumnNames);
            ib.addValues(mainDataValues);
            this.addProcessData(new DataProcessBean(ib));
            for (Map<String, Object> detail:list2){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("PROCESSNO", DataValues.newString(processNo));
                detailColumns.add("ITEM", DataValues.newString(detail.get("ITEM").toString()));
                detailColumns.add("OBJECT", DataValues.newString(detail.get("OBJECT").toString()));
                detailColumns.add("PRICETYPE", DataValues.newString(detail.get("PRICETYPE").toString()));
                detailColumns.add("PRICERATIO", DataValues.newString(detail.get("PRICERATIO").toString()));
                detailColumns.add("WAREHOUSE", DataValues.newString(detail.get("WAREHOUSE").toString()));
                detailColumns.add("STATUS", DataValues.newString(detail.get("STATUS").toString()));
                detailColumns.add("SUPPLYOBJECT", DataValues.newString(detail.get("SUPPLYOBJECT").toString()));
                detailColumns.add("DEMANDOBJECT1", DataValues.newString(detail.get("DEMANDOBJECT1").toString()));
                detailColumns.add("VERSIONNUM", DataValues.newInteger(nowVersionNum));


                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailIb=new InsBean("DCP_INTERSETTSETDETAIL_V",detailColumnNames);
                detailIb.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailIb));
            }
        }

        List<DCP_InterSettSettingUpdateReq.ObjectList> objectList = req.getRequest().getObjectList();
        for (DCP_InterSettSettingUpdateReq.ObjectList objectListItem : objectList){
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
            detailColumns.add("VERSIONNUM", DataValues.newInteger(nowVersionNum));

            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean detailIb=new InsBean("DCP_INTERSETTSETDETAIL",detailColumnNames);
            detailIb.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(detailIb));
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettSettingUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettSettingUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettSettingUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettSettingUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_InterSettSettingUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_InterSettSettingUpdateReq>(){};
    }

    @Override
    protected DCP_InterSettSettingUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_InterSettSettingUpdateRes();
    }

}


