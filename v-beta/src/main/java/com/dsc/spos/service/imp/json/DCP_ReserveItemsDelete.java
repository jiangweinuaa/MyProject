package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveItemsDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ReserveItemsDeleteRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.List;

/**
 * @description: 预约项目删除
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ReserveItemsDelete extends SPosAdvanceService<DCP_ReserveItemsDeleteReq, DCP_ReserveItemsDeleteRes> {
    @Override
    protected void processDUID(DCP_ReserveItemsDeleteReq req, DCP_ReserveItemsDeleteRes res) throws Exception {
        try {
            List<DCP_ReserveItemsDeleteReq.level2Elm> itemsNoList = req.getRequest().getItemsNoList();
            String eId = req.geteId();
            String shopId = req.getRequest().getShopId();
            if(!CollectionUtils.isEmpty(itemsNoList)){
                for (DCP_ReserveItemsDeleteReq.level2Elm item : itemsNoList) {
                    String itemsNo = item.getItemsNo();

                    DelBean del = new DelBean("DCP_RESERVEITEMS");
                    del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del.addCondition("ITEMSNO", new DataValue(itemsNo, Types.VARCHAR));
                    del.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(del));

                    DelBean del2 = new DelBean("DCP_RESERVEADVISOR");
                    del2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del2.addCondition("ITEMSNO", new DataValue(itemsNo, Types.VARCHAR));
                    del2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(del2));
                }
            }

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveItemsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveItemsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveItemsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveItemsDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveItemsDeleteReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(CollectionUtils.isEmpty(request.getItemsNoList())){
            errMsg.append("预约服务项目编号不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ReserveItemsDeleteReq> getRequestType() {
        return new TypeToken<DCP_ReserveItemsDeleteReq>(){};
    }

    @Override
    protected DCP_ReserveItemsDeleteRes getResponseType() {
        return new DCP_ReserveItemsDeleteRes();
    }
}
