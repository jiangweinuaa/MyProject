package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_KdsCookCreate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_KdsCookCreate_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: KDS机器人新增
 * @author: wangzyc
 * @create: 2021-09-13
 */
public class DCP_KdsCookCreate_Open extends SPosAdvanceService<DCP_KdsCookCreate_OpenReq, DCP_KdsCookCreate_OpenRes> {
    @Override
    protected void processDUID(DCP_KdsCookCreate_OpenReq req, DCP_KdsCookCreate_OpenRes res) throws Exception {
        DCP_KdsCookCreate_OpenReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String shopId = request.getShopId();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] columns = {"EID", "SHOPID", "MACHINEID", "SORTID", "COOKID", "COOKNAME", "STATUS", "CREATEOPID", "CREATEOPNAME", "CREATETIME"};
        String lastmodiopId = req.getApiUser().getUserCode();

        try {
            if(checkCookId(req)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该编号已存在");
            }

            DataValue[] insValue = {
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(request.getMachineId(), Types.VARCHAR),
                    new DataValue(request.getSortId(), Types.VARCHAR),
                    new DataValue(request.getCookId(), Types.VARCHAR),
                    new DataValue(request.getCookName(), Types.VARCHAR),
                    new DataValue(request.getStatus(), Types.VARCHAR),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(df.format(new Date()), Types.VARCHAR)
            };
            InsBean ins = new InsBean("DCP_KDSCOOKSET", columns);
            ins.addValues(insValue);
            this.addProcessData(new DataProcessBean(ins));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_KdsCookCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_KdsCookCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_KdsCookCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_KdsCookCreate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsCookCreate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsCookCreate_OpenReq> getRequestType() {
        return new TypeToken<DCP_KdsCookCreate_OpenReq>() {
        };
    }

    @Override
    protected DCP_KdsCookCreate_OpenRes getResponseType() {
        return new DCP_KdsCookCreate_OpenRes();
    }

    /**
     * 效验机器人编号是否存在
     *
     * @param req
     * @return
     */
    private boolean checkCookId(DCP_KdsCookCreate_OpenReq req) throws Exception {
        DCP_KdsCookCreate_OpenReq.level1Elm request = req.getRequest();
        boolean isExist = false;

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_KDSCOOKSET  " +
                " WHERE eid = ? AND SHOPID = ?  AND COOKID = ? ");
        String[] columns = {req.geteId(), request.getShopId(), request.getCookId()};
        List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), columns);
        if(!CollectionUtils.isEmpty(data)){
            isExist = true;
        }

        return isExist;
    }

}
