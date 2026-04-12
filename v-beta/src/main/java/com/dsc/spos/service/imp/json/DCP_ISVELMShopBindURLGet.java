package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVELMShopBindURLGetReq;
import com.dsc.spos.json.cust.res.DCP_ISVELMShopBindURLGetRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.isv.ISV_HelpTools;
import com.dsc.spos.waimai.meituanJBP.SignUtil;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.util.*;

public class DCP_ISVELMShopBindURLGet extends SPosBasicService<DCP_ISVELMShopBindURLGetReq, DCP_ISVELMShopBindURLGetRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVELMShopBindURLGetReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ISVELMShopBindURLGetReq> getRequestType() {
        return new TypeToken<DCP_ISVELMShopBindURLGetReq>(){};
    }

    @Override
    protected DCP_ISVELMShopBindURLGetRes getResponseType() {
        return new DCP_ISVELMShopBindURLGetRes();
    }

    @Override
    protected DCP_ISVELMShopBindURLGetRes processJson(DCP_ISVELMShopBindURLGetReq req) throws Exception {
        DCP_ISVELMShopBindURLGetRes res = this.getResponseType();
        res.setSuccess(false);
        res.setServiceDescription("200");
        res.setServiceDescription("获取饿了么外卖授权URL失败！");
        DCP_ISVELMShopBindURLGetRes.responseDatas datas = res.new responseDatas();
        String eId = req.geteId();
        //先查询有没有，客户唯一标识 这个不区分 企业ID
        String clientNoSql =" SELECT * from DCP_ISVWM_CLIENT WHERE eid = '"+eId+"'";
        List<Map<String, Object>> getQDatas = this.doQueryData(clientNoSql, null);
        if (getQDatas==null||getQDatas.isEmpty())
        {
            //没有的话，需要进行应用申请
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请联系管理员先申请应用(饿了么外卖服务商模式)！");
        }
        Map<String,Object> map = getQDatas.get(0);
        String status = map.get("STATUS").toString();//(0-待审核，100-审核通过，-1-审核失败)
        if (!"100".equals(status))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "应用未审核，无法生成授权URL！");
        }
        String clientNo = map.get("CLIENTNO").toString();
        String WaiMai_ELM_ISV = map.get("ELEME_REGISTER").toString();;
        StringBuffer errMsg = new StringBuffer("");
        boolean nRet = false;
        if (!"Y".equals(WaiMai_ELM_ISV))
        {
            errMsg.append("该应用未注册饿了么外卖平台类型,");
            nRet = true;
        }
        if (Check.Null(clientNo))
        {
            errMsg.append("应用标识为空,");
            nRet = true;
        }
        if (nRet)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String isTest = "N";
        if (req.getRequest()!=null)
        {
           if ("Y".equalsIgnoreCase(req.getRequest().getIsTest()))
           {
               isTest = "Y";
           }
        }
        //检核鼎捷服务商的服务器是否正常访问
        String isv_Url = "http://eliutong2.digiwin.com.cn/dcpService/DCP/services/invoke";//暂时写死157的3.0
        if (!Check.Null(StaticInfo.microMarkHttpPost)&&StaticInfo.microMarkHttpPost.contains("DCP/services/invoke"))
        {
            isv_Url = StaticInfo.microMarkHttpPost;
        }
        if (isv_Url.endsWith("/invoke"))
        {
            isv_Url = isv_Url.replace("/invoke","");

        }
        if(isv_Url.toLowerCase().contains("localhost")||isv_Url.contains("127.0.0.1"))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务商接口地址配置错误:"+isv_Url);
        }
        if (!ISV_HelpTools.doGetUrlConnect(isv_Url))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务商接口地址无法访问:"+isv_Url);
        }
        String isv_auth_url = "";
        if (isv_Url.endsWith("/"))
        {
            isv_auth_url = isv_Url+"ISV/Waimai/ELMAuthUrl";
        }
        else
        {
            isv_auth_url = isv_Url+"/ISV/Waimai/ELMAuthUrl";
        }
        JSONObject reqObj = new JSONObject();
        reqObj.put("isTest",isTest);
        reqObj.put("state",clientNo);
        String reqStr = reqObj.toString();
        String resStr = HttpSend.Sendhttp("POST",reqStr,isv_auth_url);
        if (resStr==null||resStr.isEmpty())
        {
            return res;
        }
        JSONObject resJson = new JSONObject(resStr);
        String success = resJson.optString("success","");
        String serviceDescription = resJson.optString("serviceDescription","");
        String url = resJson.optString("url","");
        if ("true".equalsIgnoreCase(success)&&!url.isEmpty())
        {
            datas.setUrl(url);
            res.setDatas(datas);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            return res;
        }
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVELMShopBindURLGetReq req) throws Exception {
        return null;
    }
}
