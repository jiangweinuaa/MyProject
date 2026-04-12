package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PayInProcessReq;
import com.dsc.spos.json.cust.res.DCP_PayInProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DCP_PayInProcess extends SPosAdvanceService<DCP_PayInProcessReq, DCP_PayInProcessRes> {

    @Override
    protected void processDUID(DCP_PayInProcessReq req, DCP_PayInProcessRes res) throws Exception {

        String eId = req.geteId();
        String shopId = req.getShopId();
        String createBy = req.getOpNO();
        String createByName = req.getOpName();
        String lastmoditime = DateFormatUtils.getNowDateTime();

        String payInNo = req.getRequest().getPayInNo();


        List<Map<String, Object>> getPayIn = this.getExistPayIn(req);
        if (getPayIn == null || getPayIn.isEmpty()) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，无法提交！ ");
        }

        String status = getPayIn.get(0).get("STATUS").toString();//状态（0新增 1已提交 2已上传  3已同意  4已驳回）
        if (!"0".equals(status)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非新增状态，无法提交！ ");
        }
        String status_update = "1";

        UptBean up1 = new UptBean("DCP_PAYIN");
        up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        up1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
        up1.addCondition("PAYINNO", new DataValue(payInNo, Types.VARCHAR));

        up1.addUpdateValue("STATUS", new DataValue(status_update, Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPID", new DataValue(createBy, Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPNAME", new DataValue(createByName, Types.VARCHAR));
        up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
        up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
        up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

        this.addProcessData(new DataProcessBean(up1));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PayInProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PayInProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PayInProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PayInProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String payInNo = req.getRequest().getPayInNo();


        if (Check.Null(payInNo)) {
            errMsg.append("单据payInNo不能为空值， ");
            isFail = true;

        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_PayInProcessReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_PayInProcessReq>() {
        };
    }

    @Override
    protected DCP_PayInProcessRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_PayInProcessRes();
    }

    private List<Map<String, Object>> getExistPayIn(DCP_PayInProcessReq req) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String payInNo = req.getRequest().getPayInNo();

        String sql = "select * from DCP_PAYIN where EID='" + eId + "' and SHOPID='" + shopId + "' and PAYINNO = '" + payInNo + "' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        return getQData;
    }


}
