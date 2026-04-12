package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.json.cust.req.ISV_WMClientELMShopQueryReq;
import com.dsc.spos.json.cust.res.ISV_WMClientELMShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.isv.ISV_HelpTools;
import com.dsc.spos.waimai.isv.ISV_WMUtils;
import com.google.gson.reflect.TypeToken;
import eleme.openapi.sdk.api.entity.user.OAuthorizedShop;
import eleme.openapi.sdk.api.entity.user.OUser;
import eleme.openapi.sdk.api.service.UserService;
import eleme.openapi.sdk.config.Config;
import eleme.openapi.sdk.oauth.response.Token;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ISV_WMClientELMShopQuery extends SPosBasicService<ISV_WMClientELMShopQueryReq, ISV_WMClientELMShopQueryRes> {
   private  String goodsLogFileName = "ISV_WMClientELMShopQuery";
    @Override
    protected boolean isVerifyFail(ISV_WMClientELMShopQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().getClientNo()))
        {
            errMsg.append("应用唯一标识clientNo不可为空值, ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<ISV_WMClientELMShopQueryReq> getRequestType() {
        return new TypeToken<ISV_WMClientELMShopQueryReq>(){};
    }

    @Override
    protected ISV_WMClientELMShopQueryRes getResponseType() {
        return new ISV_WMClientELMShopQueryRes();
    }

    @Override
    protected ISV_WMClientELMShopQueryRes processJson(ISV_WMClientELMShopQueryReq req) throws Exception {
        ISV_WMClientELMShopQueryRes res = this.getResponseType();
        String clientNo = req.getRequest().getClientNo();
        String isOnline = req.getRequest().getIsOnline();
        String loadDocType = orderLoadDocType.ELEME;
        if (!"Y".equals(isOnline))
        {
            isOnline ="N";
        }
        String sql = " select * from ISV_WM_ELM_TOKEN where clientno='"+clientNo+"'";
        List<Map<String,Object>> getQDatas = this.doQueryData(sql,null);
        if (getQDatas==null||getQDatas.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务端查询该应用("+clientNo+")对应的商户绑定信息失败！");
        }
        ISV_WMClientELMShopQueryRes.responseDatas datas = res.new responseDatas();
        datas.setClientNo(clientNo);
        datas.setDatas(new ArrayList<>());
        if ("Y".equals(isOnline))
        {
            for (Map<String,Object> mapToken : getQDatas)
            {
                String userId = mapToken.getOrDefault("USERID","").toString();//没啥用，只是对比下，和在线查询返回的是否一致
                String logFileName = clientNo+"-"+goodsLogFileName;
                try
                {
                    String appKey = mapToken.getOrDefault("APPKEY","").toString();
                    String appSecret = mapToken.getOrDefault("APPSECRET","").toString();
                    String accessToken = mapToken.getOrDefault("ACCESS_TOKEN","").toString();
                    String isTest = mapToken.getOrDefault("ISTEST","").toString();
                    boolean isSandbox = false;
                    if ("Y".equals(isTest))
                    {
                        isSandbox = true;
                    }
                    if ("ZssNWsz5".equalsIgnoreCase(clientNo))
                    {
                        isSandbox = true;
                    }
                    if (appKey.isEmpty()||appSecret.isEmpty())
                    {
                        appKey = ISV_WMUtils.elm_appKey;
                        appSecret = ISV_WMUtils.elm_appSecret;
                    }
                    eleme.openapi.sdk.config.Config config = null;
                    if (isSandbox)
                    {
                        config = new Config(isSandbox, ISV_WMUtils.elm_appKey_sandbox,ISV_WMUtils.elm_appSecret_sandbox);
                    }
                    else
                    {
                        config = new Config(isSandbox,appKey,appSecret);
                    }
                    ISV_HelpTools.writelog_fileName("循序查询【开始】【商户账户信息授权的店铺列表】客户端唯一标识clientNo="+clientNo+",商户授权useId="+userId+",应用appKey="+appKey+",应用appSecret="+appSecret+",令牌token="+accessToken,logFileName);
                    Token token = new Token();
                    token.setAccessToken(accessToken);
                    UserService userService = new UserService(config, token);
                    OUser oUser = userService.getUser();
                    String oUserStr = "";
                    try {
                        oUserStr = com.alibaba.fastjson.JSON.toJSONString(oUser);
                    }
                    catch (Exception e)
                    {
                    }
                    ISV_HelpTools.writelog_fileName("循序查询【完成】【商户账户信息授权的店铺列表】客户端唯一标识clientNo="+clientNo+",商户授权useId="+userId+",返回商户信息OUser="+oUserStr,logFileName);
                    if (oUser==null||oUser.getAuthorizedShops()==null||oUser.getAuthorizedShops().isEmpty())
                    {
                        continue;
                    }
                    saveELMShops(clientNo,config,oUser);
                }
                catch (Exception e)
                {
                    ISV_HelpTools.writelog_fileName("循序查询【异常】【商户账户信息授权的店铺列表】客户端唯一标识clientNo="+clientNo+",商户授权useId="+userId+",返回异常："+e.getMessage(),logFileName);
                }
            }
        }

        //保存到数据库之后，查询返回给客户端
        sql = "select * from ISV_WM_MAPPINGSHOP where BUSINESSID='2' and LOAD_DOCTYPE='"+loadDocType+"' and CLIENTNO='"+clientNo+"'";
        List<Map<String,Object>> getShopList = this.doQueryData(sql,null);
        if (getShopList!=null&&!getShopList.isEmpty())
        {
            for (Map<String,Object> map : getShopList)
            {
                try
                {
                    ISV_WMClientELMShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    String channelId = map.get("CHANNELID").toString();//渠道编码 对应dcp_ecommerce的channelId
                    String appAuthToken = map.get("APPAUTHTOKEN").toString();// 美团绑定的token
                    String appKey = map.get("APPKEY").toString();// 饿了么每个门店的应用
                    String appName = map.get("APPNAME").toString();// 饿了么每个门店的应用
                    String appSecret = map.get("APPSECRET").toString();// 饿了么每个门店的应用
                    String isTest = map.get("ISTEST").toString();// 是否测试环境
                    String erpShopNo = map.get("SHOPID").toString();// ERP门店
                    String erpShopName = map.get("SHOPNAME").toString();// ERP门店名称
                    String orderShopNo = map.get("ORDERSHOPNO").toString();// 平台门店ID
                    String orderShopName = map.get("ORDERSHOPNAME").toString();// 平台门店名称
                    String isJbp = map.get("ISJBP").toString();// 是否聚宝盆
                    String mappingShopNo = map.get("MAPPINGSHOPNO").toString();// 美团ePoiId映射的门店名称
                    String userId = map.get("USERID").toString();// 是否聚宝盆

                    oneLv1.setChannelId(channelId);
                    oneLv1.setAppAuthToken(appAuthToken);
                    oneLv1.setAppKey(appKey);
                    oneLv1.setAppName(appName);
                    oneLv1.setAppSecret(appSecret);
                    oneLv1.setIsTest(isTest);
                    oneLv1.setErpShopNo(erpShopNo);
                    oneLv1.setErpShopName(erpShopName);
                    oneLv1.setOrderShopNo(orderShopNo);
                    oneLv1.setOrderShopName(orderShopName);
                    oneLv1.setIsJbp(isJbp);
                    oneLv1.setMappingShopNo(mappingShopNo);
                    oneLv1.setUserId(userId);

                    datas.getDatas().add(oneLv1);

                }
                catch (Exception e)
                {

                }

            }

        }
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
    protected String getQuerySql(ISV_WMClientELMShopQueryReq req) throws Exception {
        return null;
    }

    /**
     * 保存查询授权门店到服务端
     * @param clientNo
     * @param config
     * @param oUser
     * @throws Exception
     */
    private void saveELMShops (String clientNo,Config config,OUser oUser ) throws Exception
    {
        String logFileName = clientNo+"-"+goodsLogFileName;
        String userIdStr = "";
        try
        {

            String appKey = "";
            String appSecret = "";
            if (config!=null)
            {
                appKey = config.getApp_key();
                appSecret = config.getApp_secret();
            }
            long userId = oUser.getUserId();
            userIdStr = userId+"";
            List<DataProcessBean> pData_del = new ArrayList<DataProcessBean>();
            List<DataProcessBean> pData_ins = new ArrayList<DataProcessBean>();
            String[] columns1 =
                    { "CLIENTNO","LOAD_DOCTYPE", "BUSINESSID", "ORDERSHOPNO", "ORDERSHOPNAME","APPKEY", "APPSECRET", "APPNAME", "ISTEST",
                            "MEMO", "ISJBP", "CHANNELID","USERID"};
            DataValue[] insValue1 = null;
            String loadDocType = orderLoadDocType.ELEME;
            String channelId = orderLoadDocType.ELEME+"001";//默认渠道ID
            String isTest = "N";
            String isJbp = "Y";//服务商都是Y
            String appName = "饿了么外卖(服务商)";
            String businessId = "2";//外卖默认2
            String memo = "饿了么外卖授权的店铺列表";
            List<OAuthorizedShop> authorizedShops = oUser.getAuthorizedShops();
            if (authorizedShops==null||authorizedShops.isEmpty())
            {
                ISV_HelpTools.writelog_fileName("保存数据库【开始】【商户账户信息授权的店铺列表】店铺列表为空,客户端唯一标识="+clientNo+",授权商户userId="+userId,logFileName);
                return;
            }
            for (OAuthorizedShop elmShop : authorizedShops)
            {
                long shopId = elmShop.getId();
                String shopName = elmShop.getName();
                if (shopName != null && shopName.length() > 255)
                {
                    shopName = shopName.substring(0, 255);
                }
                DelBean del = new DelBean("ISV_WM_MAPPINGSHOP");
                del.addCondition("CLIENTNO",new DataValue(clientNo, Types.VARCHAR));
                del.addCondition("LOAD_DOCTYPE",new DataValue(loadDocType, Types.VARCHAR));
                del.addCondition("ORDERSHOPNO",new DataValue(shopId, Types.VARCHAR));
                pData_del.add(new DataProcessBean(del));

                insValue1 = new DataValue[]
                        {
                                new DataValue(clientNo, Types.VARCHAR),
                                new DataValue(loadDocType, Types.VARCHAR),
                                new DataValue(businessId, Types.VARCHAR), // 1团购、2外卖、3闪惠、5支付、7预定、8全渠道会员
                                new DataValue(shopId, Types.VARCHAR),// 平台门店ID
                                new DataValue(shopName, Types.VARCHAR),// 平台门店名称
                                new DataValue(appKey, Types.VARCHAR),
                                new DataValue(appSecret, Types.VARCHAR),
                                new DataValue(appName, Types.VARCHAR),
                                new DataValue(isTest, Types.VARCHAR),
                                new DataValue(memo, Types.VARCHAR),
                                new DataValue(isJbp, Types.VARCHAR),
                                new DataValue(channelId, Types.VARCHAR),
                                new DataValue(userId, Types.VARCHAR)
                        };

                InsBean ib1 = new InsBean("ISV_WM_MAPPINGSHOP", columns1);
                ib1.addValues(insValue1);
                pData_ins.add(new DataProcessBean(ib1));
            }

            //先删后保存
            List<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
            if (!pData_ins.isEmpty())
            {
                for (DataProcessBean del : pData_del)
                {
                    DPB.add(del);
                }
                for (DataProcessBean ib : pData_ins)
                {
                    DPB.add(ib);
                }
                this.dao.useTransactionProcessData(DPB);

                ISV_HelpTools.writelog_fileName("保存数据库【完成】【商户账户信息授权的店铺列表】,客户端唯一标识="+clientNo+",授权商户userId="+userId,logFileName);
            }


        }
        catch (Exception e)
        {
            ISV_HelpTools.writelog_fileName("保存数据库【异常】【商户账户信息授权的店铺列表】异常："+e.getMessage()+",客户端唯一标识="+clientNo+",授权商户userId="+userIdStr,logFileName);
        }
    }
}
