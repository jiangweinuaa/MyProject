package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SortingAssignDetailAddReq;
import com.dsc.spos.json.cust.req.DCP_SortingAssignProcessReq;
import com.dsc.spos.json.cust.res.DCP_SortingAssignDetailAddRes;
import com.dsc.spos.json.cust.res.DCP_SortingAssignProcessRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_SortingAssignDetailAdd extends SPosAdvanceService<DCP_SortingAssignDetailAddReq, DCP_SortingAssignDetailAddRes> {
    @Override
    protected void processDUID(DCP_SortingAssignDetailAddReq req, DCP_SortingAssignDetailAddRes res) throws Exception {

        String billNo = req.getRequest().getBillNo();
        String employeeId = req.getRequest().getEmployeeId();
        String departId = req.getRequest().getDepartId();
        if (StringUtils.isEmpty(employeeId)) {
            employeeId = req.getEmployeeNo();
        }

        String querySql = "   SELECT a.EID, a.BILLNO, STATUS, MAX(ITEM) MITEM" +
                "      FROM DCP_SORTINGASSIGN a " +
                "  INNER JOIN   DCP_SORTINGASSIGN_DETAIL b on a.EID=b.eid and a.BILLNO=b.BILLNO " +
                " WHERE a.EID='" + req.geteId() + "' and a.BILLNO = '" + billNo + "' " +
                "  GROUP BY a.EID,a.BILLNO, STATUS ";
        List<Map<String, Object>> existed = doQueryData(querySql, null);
        int item = 0;
        if (null == existed || existed.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, billNo + "不存在!不可添加！");
        }
        String status = existed.get(0).get("STATUS").toString();
        if ("2".equals(status)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据已结束不可添加！");
        }

        item = Integer.parseInt(existed.get(0).get("MITEM").toString());
        for (DCP_SortingAssignDetailAddReq.Datas oneData : req.getRequest().getDatas()) {

            for (DCP_SortingAssignDetailAddReq.Detail oneDetail : oneData.getDetail()) {
                ColumnDataValue dcp_sortingassign_detail = new ColumnDataValue();

                dcp_sortingassign_detail.add("EID", DataValues.newString(req.geteId()));
                dcp_sortingassign_detail.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                dcp_sortingassign_detail.add("BILLNO", DataValues.newString(billNo));
                dcp_sortingassign_detail.add("ITEM", DataValues.newString(++item));
                dcp_sortingassign_detail.add("OFNO", DataValues.newString(oneData.getOfNo()));
                dcp_sortingassign_detail.add("OITEM", DataValues.newString(oneDetail.getOItem()));
                dcp_sortingassign_detail.add("ORDERTYPE", DataValues.newString(oneData.getOrderType()));
                dcp_sortingassign_detail.add("ORDERNO", DataValues.newString(oneData.getOrderNo()));
                dcp_sortingassign_detail.add("ORDERITEM", DataValues.newString(oneDetail.getOrderItem()));
                dcp_sortingassign_detail.add("RDATE", DataValues.newString(DateFormatUtils.getPlainDate(oneData.getRDate())));
                dcp_sortingassign_detail.add("DELIVERYDATE", DataValues.newString(DateFormatUtils.getPlainDate(oneData.getDeliveryDate())));
                dcp_sortingassign_detail.add("OBJECTTYPE", DataValues.newString(oneData.getObjectType()));
                dcp_sortingassign_detail.add("OBJECTID", DataValues.newString(oneData.getObjectId()));
                dcp_sortingassign_detail.add("PLUNO", DataValues.newString(oneDetail.getPluNo()));
                dcp_sortingassign_detail.add("FEATURENO", DataValues.newString(oneDetail.getFeatureNo()));
                dcp_sortingassign_detail.add("CATEGORY", DataValues.newString(oneDetail.getCategory()));
                dcp_sortingassign_detail.add("PUNIT", DataValues.newString(oneDetail.getPUnit()));
                dcp_sortingassign_detail.add("POQTY", DataValues.newString(oneDetail.getPoQty()));
                dcp_sortingassign_detail.add("WAREHOUSE", DataValues.newString(oneData.getWarehouse()));
                dcp_sortingassign_detail.add("NOQTY", DataValues.newString(oneDetail.getNoQty()));
                dcp_sortingassign_detail.add("PQTY", DataValues.newString(oneDetail.getPQty()));
                dcp_sortingassign_detail.add("BASEUNIT", DataValues.newString(oneDetail.getBaseUnit()));
                dcp_sortingassign_detail.add("BASEQTY", DataValues.newString(oneDetail.getBaseQty()));
                dcp_sortingassign_detail.add("UNITRATIO", DataValues.newString(oneDetail.getUnitRatio()));
                dcp_sortingassign_detail.add("PTEMPLATENO", DataValues.newString(oneDetail.getPTemplateNo()));
                dcp_sortingassign_detail.add("ROUTENO", DataValues.newString(oneData.getRouteNo()));

                dcp_sortingassign_detail.add("ISADDITIONAL", DataValues.newString("Y"));
                dcp_sortingassign_detail.add("ADDTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                dcp_sortingassign_detail.add("ADDBY", DataValues.newString(employeeId));

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_SORTINGASSIGN_DETAIL", dcp_sortingassign_detail)));

            }
        }
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        ParseJson pj = new ParseJson();
        DCP_SortingAssignProcessReq sapReq = new DCP_SortingAssignProcessReq();
        sapReq.setServiceId("DCP_SortingAssignProcess");
        sapReq.setToken(req.getToken());
        DCP_SortingAssignProcessReq.Request request = sapReq.new Request();
        sapReq.setRequest(request);
        request.setOpType("add");
        request.setBillNo(req.getRequest().getBillNo());

        String jsontemp = pj.beanToJson(sapReq);
        DispatchService ds = DispatchService.getInstance();
        String resXml = ds.callService(jsontemp, StaticInfo.dao);
        DCP_SortingAssignProcessRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_SortingAssignProcessRes>() {
        });

        if (!resserver.isSuccess()){
            res.setServiceDescription("追加执行失败！");
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SortingAssignDetailAddReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SortingAssignDetailAddReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SortingAssignDetailAddReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_SortingAssignDetailAddReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SortingAssignDetailAddReq> getRequestType() {
        return new TypeToken<DCP_SortingAssignDetailAddReq>() {
        };
    }

    @Override
    protected DCP_SortingAssignDetailAddRes getResponseType() {
        return new DCP_SortingAssignDetailAddRes();
    }
}
