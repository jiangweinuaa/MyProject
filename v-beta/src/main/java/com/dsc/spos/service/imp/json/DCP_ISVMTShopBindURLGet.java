package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVMTShopBindURLGetReq;
import com.dsc.spos.json.cust.res.DCP_ISVMTShopBindURLGetRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.isv.ISV_HelpTools;
import com.dsc.spos.waimai.isv.ISV_WMUtils;
import com.dsc.spos.waimai.meituanJBP.SignUtil;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_ISVMTShopBindURLGet extends SPosBasicService<DCP_ISVMTShopBindURLGetReq, DCP_ISVMTShopBindURLGetRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVMTShopBindURLGetReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().getShopNo()))
        {
            errMsg.append("门店编码shopNo不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getShopName()))
        {
            errMsg.append("门店名称shopName不可为空值, ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ISVMTShopBindURLGetReq> getRequestType() {
        return new TypeToken<DCP_ISVMTShopBindURLGetReq>(){};
    }

    @Override
    protected DCP_ISVMTShopBindURLGetRes getResponseType() {
        return new DCP_ISVMTShopBindURLGetRes();
    }

    @Override
    protected DCP_ISVMTShopBindURLGetRes processJson(DCP_ISVMTShopBindURLGetReq req) throws Exception {
        DCP_ISVMTShopBindURLGetRes res = this.getResponseType();
        res.setSuccess(false);
        res.setServiceStatus("200");
        res.setServiceDescription("获取美团外卖门店绑定URL失败！");
        DCP_ISVMTShopBindURLGetRes.responseDatas datas = res.new responseDatas();
        String eId = req.geteId();
        String shopNo = req.getRequest().getShopNo();
        String shopName = req.getRequest().getShopName();
        //
        String scurdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        String sql="SELECT *  FROM Platform_CregisterDetail "
        		+ " WHERE EID='"+eId+"' AND SHOPID='"+ shopNo+"' AND PRODUCTTYPE='47' AND bdate <= '"+scurdate+"' AND eDate >= '"+scurdate+"' ";
        List<Map<String, Object>> getQregistrShops = this.doQueryData(sql, null);
        if (getQregistrShops==null||getQregistrShops.isEmpty())
        {
        	 throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先将门店["+shopNo+"]在(通用设置->系统管理->注册信息->注册门店)进行注册");
        }
        
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
        if (!"100".equals(status))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "应用未审核，无法绑定门店！");
        }
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
        //https://open-erp.meituan.com/storemap?developerId=100019&businessId=1&ePoiId=8859&ePoiName=湘北人家&timestamp=1520563340000&sign=637a76c869933b8e6c391956fbb5c0528f48e350386cd523
        //https://open-erp.meituan.com/login?developerId=105533&businessId=2&ePoiId=99_SHG0704&sign=59cd956f760b2359cd8659cede21a547fcc7bcfd&timestamp=1676627816778&
        String mainUrl = "https://open-erp.meituan.com/storemap?";
        String ePoiId = clientNo+"_"+eId+"_"+shopNo;//鼎捷分配客户唯一编码_企业ID_门店ID
        Map<String,String> param = new HashMap();
        long timestamp = System.currentTimeMillis();
        int businessId = 2;
        String developerId = StaticInfo.waimai_digiwin_ISV_MT_developerId;
        String signKey = StaticInfo.waimai_digiwin_ISV_MT_signKey;
        //下方的参数为每次调用api需要的参数
        param.put("timestamp",timestamp+"");
        param.put("developerId", developerId);
        param.put("ePoiId",ePoiId);
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
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVMTShopBindURLGetReq req) throws Exception {
        return null;
    }
}
