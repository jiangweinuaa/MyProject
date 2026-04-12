package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MoConfirmReq;
import com.dsc.spos.json.cust.req.DCP_ROrderStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_MoConfirmRes;
import com.dsc.spos.json.cust.res.DCP_ROrderStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.Constant;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ROrderStatusUpdate extends SPosAdvanceService<DCP_ROrderStatusUpdateReq, DCP_ROrderStatusUpdateRes>
{


    @Override
    protected void processDUID(DCP_ROrderStatusUpdateReq req, DCP_ROrderStatusUpdateRes res) throws Exception
    {

        String eId = req.geteId();
        String opType = req.getRequest().getOpType();
        String rOrderNo = req.getRequest().getROrderNo();
        String organizationNO = req.getOrganizationNO();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String nowDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String rOrderSql="select a.status,a.rdate,a.bdate,a.employeeid,a.departid " +
                " from dcp_rorder a " +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.rorderno='"+rOrderNo+"'";
        List<Map<String, Object>> rOrderList = this.executeQuerySQL_BindSQL(rOrderSql, null);
        if(CollUtil.isEmpty(rOrderList)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据不存在");
        }
        Map<String, Object> rOrderMap = rOrderList.get(0);
        String status = rOrderMap.get("STATUS").toString();
        String rDate = rOrderMap.get("RDATE").toString();
        String bDate = rOrderMap.get("BDATE").toString();
        String employeeId = rOrderMap.get("EMPLOYEEID").toString();
        String departId = rOrderMap.get("DEPARTID").toString();

        if(opType.equals(Constant.OPR_TYPE_CONFIRM)){
            if(!status.equals("0")){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据状态非新建状态不可审核！");
            }

            String rOrderDetailSql="select * from dcp_rorder_detail a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.rorderno='"+rOrderNo+"'";
            List<Map<String, Object>> rOrderDetailList = this.executeQuerySQL_BindSQL(rOrderDetailSql, null);
            if(CollUtil.isEmpty(rOrderDetailList)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据明细为空！");
            }

            for (Map<String, Object> detail : rOrderDetailList) {
                ColumnDataValue demandColumns=new ColumnDataValue();
                demandColumns.add("EID", DataValues.newString(eId));
                demandColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                demandColumns.add("ORDERTYPE", DataValues.newString("3"));
                demandColumns.add("ITEM", DataValues.newString(detail.get("ITEM").toString()));
                demandColumns.add("ORDERNO", DataValues.newString(detail.get("RORDERNO").toString()));
                demandColumns.add("PLUBARCODE", DataValues.newString(detail.get("PLUBARCODE").toString()));
                demandColumns.add("PLUNO", DataValues.newString(detail.get("PLUNO").toString()));
                demandColumns.add("FEATURENO", DataValues.newString(detail.get("FEATURENO").toString()));
                demandColumns.add("PUNIT", DataValues.newString(detail.get("PUNIT").toString()));
                demandColumns.add("PQTY", DataValues.newString(detail.get("PQTY").toString()));
                demandColumns.add("BASEUNIT", DataValues.newString(detail.get("BASEUNIT").toString()));
                demandColumns.add("BASEQTY", DataValues.newString(detail.get("BASEQTY").toString()));
                demandColumns.add("RDATE", DataValues.newString(rDate));
                demandColumns.add("OBJECTTYPE", DataValues.newString("1"));
                demandColumns.add("OBJECTID", DataValues.newString(organizationNO));
                demandColumns.add("EMPLOYEEID", DataValues.newString(employeeId));
                demandColumns.add("DEPARTID", DataValues.newString(departId));
                demandColumns.add("BDATE", DataValues.newString(bDate));
                demandColumns.add("DELIVERYORGNO",DataValues.newString(detail.get("ORGANIZATIONNO").toString()));

                demandColumns.add("STOCKOUTNOQTY", DataValues.newString(0));
                demandColumns.add("PRODUCEQTY", DataValues.newString(0));
                demandColumns.add("PURQTY", DataValues.newString(0));
                demandColumns.add("STOCKINQTY", DataValues.newString(0));
                demandColumns.add("STOCKOUTQTY", DataValues.newString(0));
                demandColumns.add("CLOSESTATUS", DataValues.newString(0));
                demandColumns.add("STATUS", DataValues.newString(1));
                demandColumns.add("RANK", DataValues.newString(0));
                demandColumns.add("SUBMITTIME",DataValues.newString(nowDate));

                String[] demandColumnNames =demandColumns.getColumns().toArray(new String[0]);
                DataValue[] demandDataValues = demandColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ibDemand=new InsBean("DCP_DEMAND",demandColumnNames);
                ibDemand.addValues(demandDataValues);
                this.addProcessData(new DataProcessBean(ibDemand));
            }

            UptBean ub1 = new UptBean("DCP_RORDER");
            ub1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("MODIFYTIME", new DataValue(createTime, Types.DATE));
            ub1.addUpdateValue("CONFIRMBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMTIME", new DataValue(createTime, Types.DATE));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("RORDERNO", new DataValue(rOrderNo, Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

        }
        if(opType.equals(Constant.OPR_TYPE_UNCONFIRM)){

            String demandSql="select * from dcp_demand a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.orderno='"+rOrderNo+"' and a.produceqty>0 ";
            List<Map<String, Object>> demandList = this.doQueryData(demandSql, null);
            if(demandList.size()>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "已排产不能取消审核！");
            }
            String detailSql="select * from dcp_rorder_detail a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.rorderno='"+rOrderNo+"' and a.status!='1'";
            List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);
            if(detailList.size()>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据明细状态非审核状态不可取消审核！");
            }

            DelBean db1 = new DelBean("DCP_DEMAND");
            db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
            db1.addCondition("ORDERNO", new DataValue(rOrderNo, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("ORDERTYPE", new DataValue("3", Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            UptBean ub1 = new UptBean("DCP_RORDER");
            ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("MODIFYTIME", new DataValue(createTime, Types.DATE));
            ub1.addUpdateValue("CONFIRMBY", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMTIME", new DataValue(null, Types.DATE));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("RORDERNO", new DataValue(rOrderNo, Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

        }

        if(opType.equals(Constant.OPR_TYPE_CANCEL)){
            if(!status.equals("0")){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据状态非新建状态不可作废！");
            }

            UptBean ub1 = new UptBean("DCP_RORDER");
            ub1.addUpdateValue("STATUS", new DataValue("5", Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("MODIFYTIME", new DataValue(createTime, Types.DATE));
            ub1.addUpdateValue("CANCELBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CANCELTIME", new DataValue(createTime, Types.DATE));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("RORDERNO", new DataValue(rOrderNo, Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

        }

        if(opType.equals(Constant.OPR_TYPE_CLOSE)){
            if(!(status.equals("0")||status.equals("5"))){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "单据状态非新建或取消状态不可结案！");
            }

            UptBean ub1 = new UptBean("DCP_RORDER");
            ub1.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));
            ub1.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("MODIFYTIME", new DataValue(createTime, Types.DATE));
            ub1.addUpdateValue("CLOSEBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CLOSETIME", new DataValue(createTime, Types.DATE));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("RORDERNO", new DataValue(rOrderNo, Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ROrderStatusUpdateReq req) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ROrderStatusUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ROrderStatusUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ROrderStatusUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ROrderStatusUpdateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_ROrderStatusUpdateReq>(){};
    }

    @Override
    protected DCP_ROrderStatusUpdateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_ROrderStatusUpdateRes();
    }

}

