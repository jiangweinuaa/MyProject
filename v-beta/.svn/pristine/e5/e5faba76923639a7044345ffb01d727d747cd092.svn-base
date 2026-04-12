package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVMTShopReleaseBindURLGetReq;
import com.dsc.spos.json.cust.res.DCP_ISVMTShopReleaseBindURLGetRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.isv.ISV_HelpTools;
import com.dsc.spos.waimai.meituanJBP.SignUtil;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class DCP_ISVMTShopReleaseBindURLGet extends SPosBasicService<DCP_ISVMTShopReleaseBindURLGetReq, DCP_ISVMTShopReleaseBindURLGetRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVMTShopReleaseBindURLGetReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
       /* if (Check.Null(req.getRequest().getShopNo()))
        {
            errMsg.append("门店编码shopNo不可为空值, ");
            isFail = true;
        }*/
        if (Check.Null(req.getRequest().getAppAuthToken()))
        {
            errMsg.append("门店令牌appAuthToken不可为空值,请确认该门店是不是非服务商模式绑定");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ISVMTShopReleaseBindURLGetReq> getRequestType() {
        return new TypeToken<DCP_ISVMTShopReleaseBindURLGetReq>(){};
    }

    @Override
    protected DCP_ISVMTShopReleaseBindURLGetRes getResponseType() {
        return new DCP_ISVMTShopReleaseBindURLGetRes();
    }

    @Override
    protected DCP_ISVMTShopReleaseBindURLGetRes processJson(DCP_ISVMTShopReleaseBindURLGetReq req) throws Exception {
        DCP_ISVMTShopReleaseBindURLGetRes res = this.getResponseType();
        res.setSuccess(false);
        res.setServiceDescription("200");
        res.setServiceDescription("获取美团外卖门店解绑URL失败！");
        DCP_ISVMTShopReleaseBindURLGetRes.responseDatas datas = res.new responseDatas();
        String eId = req.geteId();
       /* String shopNo = req.getRequest().getShopNo();
        String shopName = req.getRequest().getShopName();*/
        //先查询有没有，客户唯一标识 这个不区分 企业ID
        String clientNoSql =" SELECT * from DCP_ISVWM_CLIENT WHERE eid = '"+eId+"'";
        List<Map<String, Object>> getQDatas = this.doQueryData(clientNoSql, null);
        if (getQDatas==null||getQDatas.isEmpty())
        {
            //没有的话，需要进行应用申请
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请联系管理员先申请应用(美团外卖服务商模式)！");
        }
        Map<String,Object> map = getQDatas.get(0);
        String status = map.get("STATUS").toString();//(0-待审核，100-审核通过，-1-审核失败)
        //解绑不需要，
       /* if (!"100".equals(status))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "应用未审核，无法绑定门店！");
        }*/
        String clientNo = map.get("CLIENTNO").toString();
        String WaiMai_MT_ISV = map.get("MEITUAN_REGISTER").toString();;
        StringBuffer errMsg = new StringBuffer("");
        boolean nRet = false;
        if (!"Y".equals(WaiMai_MT_ISV))
        {
            errMsg.append("该应用未注册美团外卖平台类型,");
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

        //https://open-erp.meituan.com/releasebinding?sign=be5efea400375af52d228ecf15dc4e8791874bfa&businessId=2&appAuthToken=c5c7d46e3a041f6011c1aebd5c261277abf6aa8691d992f5215b939231c33b3dd39998b2ff78db82bdab8f14a7f016b2&timestamp=1676863733327
        String mainUrl = "https://open-erp.meituan.com/releasebinding?";//自助解绑
        String appAuthToken = req.getRequest().getAppAuthToken();//认领门店返回的token【一店一token】
        Map<String,String> param = new HashMap();
        long timestamp = System.currentTimeMillis();
        int businessId = 2;
        String developerId = StaticInfo.waimai_digiwin_ISV_MT_developerId;//105533 味多美的
        String signKey = StaticInfo.waimai_digiwin_ISV_MT_signKey;//w32ftp1tmtp12py5 味多美的
        //下方的参数为每次调用api需要的参数
        param.put("timestamp",timestamp+"");
        param.put("appAuthToken", appAuthToken);
        param.put("businessId",businessId+"");
        String sign= SignUtil.getSign(signKey,param);
        param.put("sign",sign);
        StringBuilder strB = new StringBuilder();
        Set<String> sortedParams = new TreeSet<>(param.keySet());
        for (String key : sortedParams) {
            String value = param.get(key);
            strB.append(key).append("=").append(value).append("&");
        }
        String url = mainUrl+strB.deleteCharAt(strB.length()-1).toString();
        datas.setUrl(url);
        res.setDatas(datas);
        res.setSuccess(true);
        res.setServiceDescription("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVMTShopReleaseBindURLGetReq req) throws Exception {
        return null;
    }
}
