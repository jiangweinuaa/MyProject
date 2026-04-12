package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SingleStoreUpdateReq;
import com.dsc.spos.json.cust.res.DCP_SingleStoreUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_SingleStoreUpdate extends SPosAdvanceService<DCP_SingleStoreUpdateReq, DCP_SingleStoreUpdateRes> {
    @Override
    protected void processDUID(DCP_SingleStoreUpdateReq req, DCP_SingleStoreUpdateRes res) throws Exception {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 创建时间格式化
        String eId = req.geteId();
        String opName = req.getOpName();
        String lastmodiTime = dfs.format(new Date());

        for (DCP_SingleStoreUpdateReq.Shop shop : req.getRequest().getShopList()) {
            DelBean db1 = new DelBean("DCP_SINGLESTORE");
            db1.addCondition("SHOPID", new DataValue(shop.getShopId(), Types.VARCHAR));
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            String[] columns1 = { "EID","SHOPID","ENABLESINGLESTORE","LASTMODIOPID","LASTMODITIME" };
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shop.getShopId(), Types.VARCHAR),
                    new DataValue(req.getRequest().getEnableSingleStore(), Types.VARCHAR),
                    new DataValue(opName, Types.VARCHAR),
                    new DataValue(lastmodiTime, Types.DATE)
            };
            InsBean ib1 = new InsBean("DCP_SINGLESTORE", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SingleStoreUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SingleStoreUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SingleStoreUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SingleStoreUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        List<DCP_SingleStoreUpdateReq.Shop> shopList = req.getRequest().getShopList();

        if(CollectionUtils.isEmpty(shopList)){
            errMsg.append("门店列表不能为空值 ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getEnableSingleStore())){
            errMsg.append("是否启用单店收银模式不能为空值 ");
            isFail = true;
        }
        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SingleStoreUpdateReq> getRequestType() {
        return new TypeToken<DCP_SingleStoreUpdateReq>(){};
    }

    @Override
    protected DCP_SingleStoreUpdateRes getResponseType() {
        return new DCP_SingleStoreUpdateRes();
    }
}
