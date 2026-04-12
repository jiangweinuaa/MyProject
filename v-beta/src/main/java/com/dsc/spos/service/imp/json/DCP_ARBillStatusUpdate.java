package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ARBillStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ARBillStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ARBillStatusUpdate extends SPosAdvanceService<DCP_ARBillStatusUpdateReq, DCP_ARBillStatusUpdateRes> {

    private static final String OPTYPE_CONFIRM = "confirm";
    private static final String OPTYPE_UNCONFIRM = "unconfirm";
    private static final String OPTYPE_CANCEL = "cancel";

    @Override
    protected void processDUID(DCP_ARBillStatusUpdateReq req, DCP_ARBillStatusUpdateRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据!");
        }
        String accountId = qData.get(0).get("ACCOUNTID").toString();

        String querySql = " SELECT * FROM DCP_ACOUNT_SETTING WHERE  ACCOUNTID='" + accountId + "'";
        List<Map<String, Object>> asData = doQueryData(querySql, null);
        if (CollectionUtils.isEmpty(asData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, accountId + "对应主帐套未配置或未启用！");
        }

        String oStatus = qData.get(0).get("STATUS").toString();

        ColumnDataValue dcp_arBill = new ColumnDataValue();
        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("ARNO", req.getRequest().getApNo());

        if (OPTYPE_CONFIRM.equals(req.getRequest().getOpType())) {
            String pDate = qData.get(0).get("PDATE").toString();

            String arCloseDate = asData.get(0).get("ARCLOSINGDATE").toString();
            if (DateFormatUtils.compareDate(pDate, arCloseDate) < 0)
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "日期小于应收关账日:" + arCloseDate + ",不可审核!");

            if (!"0".equals(oStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可审核!");
            }
            dcp_arBill.add("STATUS", "1");
            dcp_arBill.add("CONFIRMBY", req.getEmployeeNo());
            dcp_arBill.add("CONFIRM_DATE", DateFormatUtils.getNowPlainDate());
            dcp_arBill.add("CONFIRM_TIME", DateFormatUtils.getNowPlainTime());
            dcp_arBill.add("MODIFYBY", req.getEmployeeNo());
            dcp_arBill.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_arBill.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());


        } else if (OPTYPE_UNCONFIRM.equals(req.getRequest().getOpType())) {
            String glNp = qData.get(0).get("GLNO").toString();

            if (StringUtils.isNotEmpty(glNp)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已抛转凭证，不可取消审核!");
            }

            if (!"0".equals(oStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可取消审核!");
            }

            dcp_arBill.add("STATUS", "0");
            dcp_arBill.add("CONFIRMBY", "");
            dcp_arBill.add("CONFIRM_DATE", "");
            dcp_arBill.add("CONFIRM_TIME", "");
            dcp_arBill.add("MODIFYBY", req.getEmployeeNo());
            dcp_arBill.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_arBill.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());


        }

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_ARBILL", condition, dcp_arBill)));
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_ARBILLDETAIL", condition, dcp_arBill)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_ARBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ARBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ARBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ARBillStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ARBillStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ARBillStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ARBillStatusUpdateRes getResponseType() {
        return new DCP_ARBillStatusUpdateRes();
    }

    @Override
    protected String getQuerySql(DCP_ARBillStatusUpdateReq req) throws Exception {
        String querySql = " SELECT * FROM DCP_ARBILL a ";
        querySql += " WHERE a.EID='" + req.geteId() + "'";

        if (StringUtils.isNotEmpty(req.getRequest().getApNo())) {
            querySql += " AND a.ARNO='" + req.getRequest().getApNo() + "'";
        }

        return querySql;
    }
}
