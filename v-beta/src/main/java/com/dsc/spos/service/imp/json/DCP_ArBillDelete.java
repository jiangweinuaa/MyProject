package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ArBillDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ArBillDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ArBillDelete extends SPosAdvanceService<DCP_ArBillDeleteReq, DCP_ArBillDeleteRes> {
    @Override
    protected boolean isVerifyFail(DCP_ArBillDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArBillDeleteReq> getRequestType() {
        return new TypeToken<DCP_ArBillDeleteReq>() {
        };
    }

    @Override
    protected DCP_ArBillDeleteRes getResponseType() {
        return new DCP_ArBillDeleteRes();
    }


    @Override
    protected String getQuerySql(DCP_ArBillDeleteReq req) throws Exception {
        String querySql = " SELECT ARNO,STATUS FROM DCP_ARBILL a " +
                " WHERE a.EID='" + req.geteId() + "'";

        if (StringUtils.isNotEmpty(req.getRequest().getArNo())) {
            querySql += " AND a.ARNO='" + req.getRequest().getArNo() + "'";
        }
        if (StringUtils.isNotEmpty(req.getRequest().getAccountId())) {
            querySql += " AND a.ACCOUNTID='" + req.getRequest().getAccountId() + "'";
        }

        return querySql;
    }

    @Override
    protected void processDUID(DCP_ArBillDeleteReq req, DCP_ArBillDeleteRes res) throws Exception {


        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无法查询到对应数据！");
        }

        String status = qData.get(0).get("STATUS").toString();
        if ("1".equals(status)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可删除！");
        }

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("ARNO", req.getRequest().getArNo());

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLDETAIL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARPERD", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLWRTOFF", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARWRTOFF", condition)));
//        if (CollectionUtils.isNotEmpty(req.getRequest().getItemList())) {
//
//            for (DCP_ArBillDeleteReq.ItemList item : req.getRequest().getItemList()) {
//                ColumnDataValue detailDel = new ColumnDataValue();
//
//                detailDel.add("EID", req.geteId());
//                detailDel.add("ARNO", req.getRequest().getArNo());
//                detailDel.add("ITEM", item.getItem());
//
//                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLDETAIL", detailDel)));
//            }
//
//        } else {
//
//        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ArBillDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ArBillDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ArBillDeleteReq req) throws Exception {
        return Collections.emptyList();
    }


}
