package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CdsOrderCallTaskQUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_CdsOrderCallTaskQUpdate_OpenRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: CDS叫号任务更新
 * @author: wangzyc
 * @create: 2022-05-24
 */
public class DCP_CdsOrderCallTaskQUpdate_Open extends SPosAdvanceService<DCP_CdsOrderCallTaskQUpdate_OpenReq, DCP_CdsOrderCallTaskQUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_CdsOrderCallTaskQUpdate_OpenReq req, DCP_CdsOrderCallTaskQUpdate_OpenRes res) throws Exception {
        try {
            String eId = req.getRequest().getEId();
            String shopId = req.getRequest().getShopId();
            List<DCP_CdsOrderCallTaskQUpdate_OpenReq.level2Elm> taskList = req.getRequest().getTaskList();
            String key="cdsCallTask" + ":" +eId +":" + shopId;

            List<String> billNoList = taskList.stream().map(DCP_CdsOrderCallTaskQUpdate_OpenReq.level2Elm::getBillNo).collect(Collectors.toList());
            String[] billNos = billNoList.toArray(new String[billNoList.size()]);

            //删除缓存
            RedisPosPub Rpp = new RedisPosPub();
            Rpp.DeleteHighkey(key,billNos);

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CdsOrderCallTaskQUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CdsOrderCallTaskQUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CdsOrderCallTaskQUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CdsOrderCallTaskQUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_CdsOrderCallTaskQUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }
        if (CollectionUtils.isEmpty(request.getTaskList())) {
            errMsg.append("叫号列表不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CdsOrderCallTaskQUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_CdsOrderCallTaskQUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_CdsOrderCallTaskQUpdate_OpenRes getResponseType() {
        return new DCP_CdsOrderCallTaskQUpdate_OpenRes();
    }
}
