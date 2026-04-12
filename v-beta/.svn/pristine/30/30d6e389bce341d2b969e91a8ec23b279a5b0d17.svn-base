package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_VendorAdjStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_VendorAdjStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_VendorAdjStatusUpdate extends SPosAdvanceService<DCP_VendorAdjStatusUpdateReq, DCP_VendorAdjStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_VendorAdjStatusUpdateReq req, DCP_VendorAdjStatusUpdateRes res) throws Exception {

        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_VendorAdjStatusUpdateReq.levelElm request = req.getRequest();
        if(Check.Null(request.getEId())){
            request.setEId(req.geteId());
        }
        String adjustNO = request.getAdjustNO();
        String adjustSql="select * from DCP_VENDORADJ a where a.eid='"+request.getEId()+"' " +
                " and a.adjustno='"+adjustNO+"' ";
        List<Map<String, Object>> list = this.doQueryData(adjustSql, null);
        if(list.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在！");
        }
        String adjustStatus = list.get(0).get("STATUS").toString();

        //confirm审核 unConfirm取消审核 cancel作废
        String opType = request.getOpType();
        String status = request.getStatus();//不知道干嘛用

        if("confirm".equals(opType)){
            if(!"0".equals(adjustStatus)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "不可提交审核！");
            }
            UptBean ub1 = new UptBean("DCP_VENDORADJ");
            ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_DATE", new DataValue(createDate, Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_TIME", new DataValue(createTime, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            ub1.addCondition("ADJUSTNO", new DataValue(adjustNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            //生成对应的结算底稿
            String adjDetailSql="select a.*,b.category from DCP_VENDORADJ_DETAIL a " +
                    " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno " +
                    " where a.eid='"+request.getEId()+"' " +
                    " and a.adjustno='"+adjustNO+"' ";
            List<Map<String, Object>> adjDetailList = this.doQueryData(adjDetailSql, null);

            String adjOrgNo = list.get(0).get("ORGANIZATIONNO").toString();
            String supplier = list.get(0).get("SUPPLIER").toString();
            String billDateNo = list.get(0).get("BILLDATENO").toString();
            String payDateNo = list.get(0).get("PAYDATENO").toString();
            String bDate = list.get(0).get("BDATE").toString();
            String year="";
            String month="";
            if(bDate.length()>6){
                year = bDate.substring(0,4);
                month = bDate.substring(4,6);
            }

            String currency = list.get(0).get("CURRENCY").toString();


            for(Map<String, Object> adjDetail : adjDetailList){

                int direction=1;
                BigDecimal adjAmt = new BigDecimal(adjDetail.get("ADJAMT").toString());
                if(adjAmt.compareTo(BigDecimal.ZERO)<0){
                    direction=-1;
                }

                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(req.geteId()));
                detailColumns.add("ORGANIZATIONNO", DataValues.newString(adjOrgNo));
                detailColumns.add("BDATE", DataValues.newString(bDate));
                detailColumns.add("BTYPE", DataValues.newString("7"));
                detailColumns.add("BILLNO", DataValues.newString(adjustNO));
                detailColumns.add("ITEM", DataValues.newString(adjDetail.get("ITEM").toString()));
                detailColumns.add("BIZTYPE", DataValues.newString("1"));
                detailColumns.add("BIZPARTNERNO", DataValues.newString(supplier));
                detailColumns.add("PAYORGNO", DataValues.newString(adjOrgNo));
                detailColumns.add("BILLDATENO", DataValues.newString(billDateNo));
                detailColumns.add("PAYDATENO", DataValues.newString(payDateNo));
                detailColumns.add("INVOICECODE", DataValues.newString(""));//todo
                detailColumns.add("BILLDATE", DataValues.newString(""));//todo
                detailColumns.add("PAYDATE", DataValues.newString(""));//todo
                detailColumns.add("MONTH", DataValues.newString(month));
                detailColumns.add("YEAR", DataValues.newString(year));
                detailColumns.add("CURRENCY", DataValues.newString(currency));
                detailColumns.add("TAXCODE", DataValues.newString(adjDetail.get("TAXCODE").toString()));
                detailColumns.add("TAXRATE", DataValues.newString(adjDetail.get("TAXRATE").toString()));
                detailColumns.add("DIRECTION", DataValues.newString(direction));
                detailColumns.add("PLUNO", DataValues.newString(adjDetail.get("PLUNO").toString()));
                detailColumns.add("PRICEUNIT", DataValues.newString(adjDetail.get("PUNIT").toString()));
                detailColumns.add("FEATURENO", DataValues.newString(adjDetail.get("FEATURENO").toString()));
                detailColumns.add("BILLQTY", DataValues.newString(adjDetail.get("PQTY").toString()));
                detailColumns.add("FEE", DataValues.newString(""));
                detailColumns.add("BILLPRICE", DataValues.newString(adjDetail.get("ADJPRICE").toString()));
                detailColumns.add("PRETAXAMT", DataValues.newString(adjDetail.get("ADJAMTPRETAX").toString()));
                detailColumns.add("TAXAMT", DataValues.newString(adjDetail.get("ADJTAXAMT").toString()));
                detailColumns.add("BILLAMT", DataValues.newString(adjDetail.get("ADJAMT").toString()));
                detailColumns.add("UNSETTLEAMT", DataValues.newString(adjDetail.get("ADJAMT").toString()));
                detailColumns.add("SETTLEAMT", DataValues.newString(0));
                detailColumns.add("UNPAIDAMT", DataValues.newString(adjDetail.get("ADJAMT").toString()));
                detailColumns.add("PAIDAMT", DataValues.newString(0));
                detailColumns.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
                detailColumns.add("CATEGORY", DataValues.newString(adjDetail.get("CATEGORY").toString()));
                detailColumns.add("STATUS", DataValues.newString("0"));
                detailColumns.add("APQTY", DataValues.newString(0));
                detailColumns.add("APAMT", DataValues.newString(0));
                detailColumns.add("UNAPAMT", DataValues.newString(0));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailIb=new InsBean("DCP_SETTLEDATA",detailColumnNames);
                detailIb.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailIb));
            }



        }
        else if ("unConfirm".equals(opType)){
            //判断结算底稿 状态是否为0
            String settleSql="select * from DCP_SETTLEDATA a " +
                    " where a.eid='"+req.geteId()+"' " +
                    " and a.billno='"+req.getRequest().getAdjustNO()+"' " +
                    " and a.status!='0'";
            List<Map<String, Object>> settleList = this.doQueryData(settleSql, null);
            if(settleList.size()>0){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "底稿状态不为【未对账】不可取消审核！");
            }

            if(!"2".equals(adjustStatus)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "不可取消审核！");
            }
            UptBean ub1 = new UptBean("DCP_VENDORADJ");
            ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMBY", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_DATE", new DataValue("", Types.VARCHAR));
            ub1.addUpdateValue("CONFIRM_TIME", new DataValue("", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            ub1.addCondition("ADJUSTNO", new DataValue(adjustNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            //删除生成的底稿
            DelBean db1 = new DelBean("DCP_SETTLEDATA");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("BILLNO", new DataValue(adjustNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

        }
        else if ("cancel".equals(opType)){
            if(!"0".equals(adjustStatus)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "不可取消！");
            }
            UptBean ub1 = new UptBean("DCP_VENDORADJ");
            ub1.addUpdateValue("STATUS", new DataValue("-1", Types.VARCHAR));
            ub1.addUpdateValue("CANCELBY", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CANCEL_DATE", new DataValue(createDate, Types.VARCHAR));
            ub1.addUpdateValue("CANCEL_TIME", new DataValue(createTime, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            ub1.addCondition("ADJUSTNO", new DataValue(adjustNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_VendorAdjStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_VendorAdjStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_VendorAdjStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_VendorAdjStatusUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_VendorAdjStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_VendorAdjStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_VendorAdjStatusUpdateRes getResponseType() {
        return new DCP_VendorAdjStatusUpdateRes();
    }
}

