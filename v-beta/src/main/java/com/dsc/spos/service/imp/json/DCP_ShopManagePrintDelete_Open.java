package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ShopManagePrintDelete_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ShopManagePrintDelete_OpenRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class DCP_ShopManagePrintDelete_Open extends SPosAdvanceService<DCP_ShopManagePrintDelete_OpenReq, DCP_ShopManagePrintDelete_OpenRes>
{

    @Override
    protected void processDUID(DCP_ShopManagePrintDelete_OpenReq req, DCP_ShopManagePrintDelete_OpenRes res) throws Exception {

        String eId = req.getRequest().geteId();
        String shopNo = req.getRequest().getShopId();
        String batchKey = req.getRequest().getBatchKey();
        String hash_key = batchKey;
        String redis_key_printError = "shopManageOrderPrint" + ":" + eId + ":" + shopNo;
        //删除打印异常外卖单缓存
        String shopLogFileName = "DCP_ShopManagePrintCreate";
        try
        {
            RedisPosPub redis = new RedisPosPub();
            //HelpTools.writelog_waimai("【删除门店管理打印单据Redis】开始，redis_key：" + redis_key_printError+" hash_key:"+hash_key);
            HelpTools.writelog_fileName("【删除门店管理打印单据Redis】开始，redis_key:" + redis_key_printError+",hash_key:"+hash_key, shopLogFileName);
            redis.DeleteHkey(redis_key_printError, hash_key);
            //HelpTools.writelog_waimai("【删除门店管理打印单据Redis】成功，redis_key：" + redis_key_printError+" hash_key:"+hash_key);
            HelpTools.writelog_fileName("【删除门店管理打印单据Redis】成功，redis_key:" + redis_key_printError+",hash_key:"+hash_key, shopLogFileName);
        }
        catch (Exception e)
        {
            //HelpTools.writelog_waimai("【删除门店管理打印单据Redis】异常："+e.getMessage()+"，redis_key：" + redis_key_printError+" hash_key:"+hash_key);
            HelpTools.writelog_fileName("【删除门店打印异常外卖单Redis】异常:"+e.getMessage()+"，redis_key:" + redis_key_printError+",hash_key:"+hash_key, shopLogFileName);
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功!");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ShopManagePrintDelete_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShopManagePrintDelete_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShopManagePrintDelete_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ShopManagePrintDelete_OpenReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().geteId()))
        {
            errCt++;
            errMsg.append("企业编号eId不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getShopId()))
        {
            errCt++;
            errMsg.append("当前门店shopId不可为空值, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getBatchKey()))
        {
            errCt++;
            errMsg.append("打印编号batchKey不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ShopManagePrintDelete_OpenReq> getRequestType() {
        return new TypeToken<DCP_ShopManagePrintDelete_OpenReq>(){};
    }

    @Override
    protected DCP_ShopManagePrintDelete_OpenRes getResponseType() {
        return new DCP_ShopManagePrintDelete_OpenRes();
    }
}
