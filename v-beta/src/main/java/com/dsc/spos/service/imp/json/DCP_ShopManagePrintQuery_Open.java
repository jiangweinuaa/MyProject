package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShopManagePrintQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_ShopManagePrintQuery_OpenRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.Map;

public class DCP_ShopManagePrintQuery_Open extends SPosBasicService<DCP_ShopManagePrintQuery_OpenReq, DCP_ShopManagePrintQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_ShopManagePrintQuery_OpenReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

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

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ShopManagePrintQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_ShopManagePrintQuery_OpenReq>(){};
    }

    @Override
    protected DCP_ShopManagePrintQuery_OpenRes getResponseType() {
        return new DCP_ShopManagePrintQuery_OpenRes();
    }

    @Override
    protected DCP_ShopManagePrintQuery_OpenRes processJson(DCP_ShopManagePrintQuery_OpenReq req) throws Exception {
        String logFileName = "DCP_ShopManagePrintCreate";
        DCP_ShopManagePrintQuery_OpenRes res = this.getResponse();
        res.setSuccess(true);
        String eId = req.getRequest().geteId();
        String shopId = req.getRequest().getShopId();
        String redis_key = "shopManageOrderPrint" + ":" + eId + ":" + shopId;
        String batchKey = req.getRequest().getBatchKey();
        DCP_ShopManagePrintQuery_OpenRes.level1Elm datas = res.new level1Elm();
        res.setDatas(datas);
        datas.setPrintList(new ArrayList<DCP_ShopManagePrintQuery_OpenRes.level2Elm>());
        try
        {
            RedisPosPub redis = new RedisPosPub();
            //HelpTools.writelog_fileName("【获取门店管理打印订单缓存】【开始】,redis_key:"+redis_key+",hash_key:"+batchKey,logFileName);
            if (!Check.Null(batchKey))
            {
                HelpTools.writelog_fileName("【获取门店管理打印订单缓存】【开始】,redis_key:"+redis_key+",hash_key:"+batchKey,logFileName);
                //精确查询
               String hash_value = redis.getHashMap(redis_key,batchKey);
               if (hash_value==null)
               {
                   hash_value = "";
               }
               DCP_ShopManagePrintQuery_OpenRes.level2Elm onelv2 = res.new level2Elm();
               onelv2.setBatchKey(batchKey);
               String[] ss = batchKey.split("_");
               onelv2.setPrintBillType(ss[1]);
               onelv2.setBillNo(ss[2]);
               onelv2.setPrintData(hash_value);
               datas.getPrintList().add(onelv2);
               res.setDatas(datas);
               HelpTools.writelog_fileName("【获取门店管理打印订单缓存】【结束】,redis_key:"+redis_key+",hash_key:"+batchKey,logFileName);
            }
            else
            {
                HelpTools.writelog_fileName("【获取门店管理打印订单缓存】【开始】,redis_key:"+redis_key,logFileName);
                Map<String, String> printMap = redis.getALLHashMap_Hscan(redis_key);
                for (Map.Entry<String, String> entry : printMap.entrySet())
                {
                    if (entry.getValue() != null)
                    {
                        try
                        {
                            String hash_key = entry.getKey();
                            String printData = entry.getValue();
                            DCP_ShopManagePrintQuery_OpenRes.level2Elm onelv2 = res.new level2Elm();
                            onelv2.setBatchKey(hash_key);
                            String[] ss = hash_key.split("_");
                            onelv2.setPrintBillType(ss[1]);
                            onelv2.setBillNo(ss[2]);
                            onelv2.setPrintData(printData);
                            datas.getPrintList().add(onelv2);

                        }
                        catch (Exception e)
                        {
                            HelpTools.writelog_fileName("【获取门店管理打印订单缓存】【异常】"+e.getMessage()+" 异常订单json:"+entry.getValue()+",Redis主键："+redis_key,logFileName);
                            continue;
                        }

                    }
                }
                res.setDatas(datas);
                HelpTools.writelog_fileName("【获取门店管理打印订单缓存】【结束】,redis_key:"+redis_key,logFileName);

            }


        }
        catch (Exception e)
        {
            res.setSuccess(false);
            res.setServiceDescription(e.getMessage());
            HelpTools.writelog_fileName("【获取门店管理打印订单缓存】异常:"+e.getMessage()+",redis_key:"+redis_key+",hash_key:"+batchKey,logFileName);
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ShopManagePrintQuery_OpenReq req) throws Exception {
        return null;
    }
}
