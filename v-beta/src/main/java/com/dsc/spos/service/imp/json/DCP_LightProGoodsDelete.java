package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_LightProGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_LightProGoodsDeleteReq.levelPlu;
import com.dsc.spos.json.cust.res.DCP_LightProGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DCP_LightProGoodsDelete extends SPosAdvanceService<DCP_LightProGoodsDeleteReq, DCP_LightProGoodsDeleteRes> {
    @Override
    protected void processDUID(DCP_LightProGoodsDeleteReq req, DCP_LightProGoodsDeleteRes res) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String lastModiTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        for (levelPlu pluInfo : req.getRequest().getPluList())
        {
            String pluNo = pluInfo.getPluNo();
            String featureNo = pluInfo.getFeatureNo();
            if (featureNo==null||featureNo.isEmpty())
            {
                featureNo = " ";
            }
            String shopNo = pluInfo.getShopNo();
            DelBean del = new DelBean("DCP_LIGHTPROGOODS");
            del.addCondition("EID",new DataValue(eId,Types.VARCHAR));
            del.addCondition("SHOPNO",new DataValue(shopNo,Types.VARCHAR));
            del.addCondition("PLUNO",new DataValue(pluNo,Types.VARCHAR));
            del.addCondition("FEATURENO",new DataValue(featureNo,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(del));

        }
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_LightProGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_LightProGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_LightProGoodsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_LightProGoodsDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest()==null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (req.getRequest().getPluList()==null||req.getRequest().getPluList().isEmpty())
        {
            errMsg.append("商品列表pluList不可为空, ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (levelPlu par :req.getRequest().getPluList())
        {
            if (par.getPluNo()==null||par.getPluNo().isEmpty())
            {
                isFail = true;
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品列表节点pluNo不可为空值,");
            }
            if (par.getShopNo()==null||par.getShopNo().isEmpty())
            {
                isFail = true;
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品列表节点shopNo不可为空值,");
            }
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_LightProGoodsDeleteReq> getRequestType() {
        return new TypeToken<DCP_LightProGoodsDeleteReq>(){};
    }

    @Override
    protected DCP_LightProGoodsDeleteRes getResponseType() {
        return new DCP_LightProGoodsDeleteRes();
    }
}
