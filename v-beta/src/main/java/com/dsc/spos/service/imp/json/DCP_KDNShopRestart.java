package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_KDNShopRestartReq;
import com.dsc.spos.json.cust.res.DCP_KDNShopRestartRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.ThirdpartConstants;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.kdniao.kdnTCService;
import com.dsc.spos.waimai.kdniao.kdnTCStoreReq;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_KDNShopRestart extends SPosAdvanceService<DCP_KDNShopRestartReq, DCP_KDNShopRestartRes> {
    @Override
    protected void processDUID(DCP_KDNShopRestartReq req, DCP_KDNShopRestartRes res) throws Exception {

        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId = req.geteId();
        String shopId = req.getRequest().getShopId();
        String deliveryType = ThirdpartConstants.kdn_deliveryType;
        String shopMode = "1";//KDN物流对应的枚举值， 0全国快递，1同城快递
        Map<String,Object> mapShop = checkExist(eId,shopId);
        if (mapShop==null||mapShop.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该门店:"+shopId+"不存在，无法修改!");
        }
        String uploadstatus = mapShop.get("UPLOADSTATUS").toString();//上传状态（0创建成功,1上传失败）
        if ("0".equals(uploadstatus))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该门店:"+shopId+"已经创建成功，无需重试!");
        }
        String contactName = mapShop.get("CONTACTNAME").toString();//联系人
        String idCard = mapShop.get("IDCARD").toString();
        String idCardName = mapShop.get("IDCARDNAME").toString();
        String idCardFront = mapShop.get("IDCARDFRONT_IMAGE").toString();
        String idCardBack = mapShop.get("IDCARDBACK_IMAGE").toString();
        String license = mapShop.get("LICENSE_IMAGE").toString();
        String creditCode = mapShop.get("CREDITCODE").toString();
        String shopPicture = mapShop.get("SHOP_IMAGE").toString();
        if (Check.Null(contactName))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "联系人为空，烦请维护后再重试");
        }

        String sql = " select A.*,B.ORG_NAME from dcp_org A left join dcp_org_lang B on A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='zh_CN' where A.EID='"+eId+"' and A.ORGANIZATIONNO='"+shopId+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData== null||getQData.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "查询该门店:"+shopId+"信息不存在!");
        }
        Map<String,Object> outSalesetMap = this.getKDNDeliverySetByShippingShop(eId,deliveryType,shopId,shopMode);
        if (outSalesetMap==null||outSalesetMap.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "查询该门店:"+shopId+"对应的快递鸟同城配送设置参数为空!");
        }
        String storeGoodsType = outSalesetMap.getOrDefault("PRODUCTTYPE","").toString();
        String customerCode = outSalesetMap.getOrDefault("APPSIGNKEY","").toString();
        if (Check.Null(storeGoodsType))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "快递鸟同城配送应用["+customerCode+"]未设置物品类型!");
        }

        Map<String,Object> SendShopInfoMap = getQData.get(0);
        String storeName = SendShopInfoMap.getOrDefault("ORG_NAME","").toString();
        String sendProvinceName = SendShopInfoMap.getOrDefault("PROVINCE","").toString();
        String sendCityName = SendShopInfoMap.getOrDefault("CITY","").toString();
        String sendExpAreaName = SendShopInfoMap.getOrDefault("COUNTY","").toString();
        String sendAddress = SendShopInfoMap.getOrDefault("ADDRESS","").toString();
        String senderMobile = SendShopInfoMap.getOrDefault("PHONE", "").toString();//寄件人联系电话
        String longitude = SendShopInfoMap.getOrDefault("LONGITUDE", "").toString();//经度
        String latitude = SendShopInfoMap.getOrDefault("LATITUDE", "").toString();//维度

        boolean isInvokeKDN = true;
        String messStr_send = "门店("+shopId+")组织信息不完整烦请维护以下内容后再创建<br>";

        if(Check.Null(sendProvinceName))
        {
            isInvokeKDN = false;
            messStr_send += "省份未维护,";
        }
        if(Check.Null(sendCityName))
        {
            isInvokeKDN = false;
            messStr_send += "城市未维护,";
        }
        if(Check.Null(sendExpAreaName))
        {
            isInvokeKDN = false;
            messStr_send += "区/县未维护,";
        }
        if(Check.Null(sendAddress))
        {
            isInvokeKDN = false;
            messStr_send += "详细地址未维护,";
        }
        if(Check.Null(senderMobile))
        {
            isInvokeKDN = false;
            messStr_send += "联系电话未维护,";
        }
        if(Check.Null(latitude)||Check.Null(longitude))
        {
            isInvokeKDN = false;
            messStr_send += "经纬度未维护,";
        }

        if(!Check.Null(latitude)&&!Check.Null(longitude))
        {
            BigDecimal lat = new BigDecimal("0");
            BigDecimal lng = new BigDecimal("0");
            try
            {
                lat = new BigDecimal(latitude);
                lng = new BigDecimal(longitude);
                if (lat.compareTo(BigDecimal.ZERO)==1&&lng.compareTo(BigDecimal.ZERO)==1)
                {
                    //门店经度（精确到小数点后5位）
                    latitude = lat.setScale(5,BigDecimal.ROUND_HALF_UP)+"";
                    longitude = lng.setScale(5,BigDecimal.ROUND_HALF_UP)+"";
                }
                else
                {
                    isInvokeKDN = false;
                    messStr_send += "经纬度维护的不正确,";
                }
            }
            catch (Exception e)
            {
                isInvokeKDN = false;
                messStr_send += "经纬度维护的不正确,";
            }


        }

        if (!isInvokeKDN)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, messStr_send);
        }

        //下面开始调用接口
        kdnTCStoreReq requestData = new kdnTCStoreReq();
        requestData.setStoreCode(shopId);
        requestData.setStoreName(storeName);
        requestData.setStoreGoodsType(storeGoodsType);
        requestData.setProvinceName(sendProvinceName);
        requestData.setCityName(sendCityName);
        requestData.setAreaName(sendExpAreaName);
        requestData.setAddress(sendAddress);
        requestData.setLbsType(2);//1：百度地图 2：高德地图 3：腾讯地图
        requestData.setLongitude(longitude);
        requestData.setLatitude(latitude);
        requestData.setContactName(contactName);
        if (senderMobile.trim().startsWith("1") && senderMobile.trim().length() == 11)
        {
            requestData.setMobile(senderMobile.trim());
        }
        else {
            requestData.setPhone(senderMobile);
        }
        if (!Check.Null(idCardFront))
        {
            requestData.setAttachType("1");//附件类型(有上传附件时，该字段必填)1：URL地址 2：Base64(暂不支持) 3：图片hash值
            requestData.setIdCardFront(idCardFront);
        }
        if (!Check.Null(idCardBack))
        {
            requestData.setAttachType("1");//附件类型(有上传附件时，该字段必填)1：URL地址 2：Base64(暂不支持) 3：图片hash值
            requestData.setIdCardBack(idCardBack);
        }
        if (!Check.Null(shopPicture))
        {
            requestData.setAttachType("1");//附件类型(有上传附件时，该字段必填)1：URL地址 2：Base64(暂不支持) 3：图片hash值
            requestData.setShopPicture(shopPicture);
        }
        requestData.setIdCard(idCard);
        requestData.setIdCardName(idCardName);
        requestData.setCreditCode(creditCode);
        kdnTCService kdnService = null;
        if ("1663339".equals(customerCode))
        {
            kdnService = new kdnTCService(true);
        }
        else
        {
            kdnService = new kdnTCService();
        }
        StringBuffer errorBuffer = new StringBuffer("");
        boolean nResult = false;
        String errorMessage = "";
        String resKdn = kdnService.kdnStoreCreate(outSalesetMap,requestData,errorBuffer);
        if (!Check.Null(resKdn))
        {
            lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            JSONObject jsonRes = JSONObject.parseObject(resKdn);
            String resultCode = jsonRes.get("resultCode").toString();
            String reason = jsonRes.getOrDefault("reason","")==null?"":jsonRes.getOrDefault("reason","").toString();
            if ("100".equals(resultCode))
            {
                nResult = true;
                JSONObject dataJson =  jsonRes.getJSONObject("data");
                String kdnStoreCode = dataJson.getString("kdnStoreCode");
                String kdnStoreStatus = dataJson.getOrDefault("kdnStoreStatus","")==null?"":dataJson.getOrDefault("kdnStoreStatus","").toString();
                try
                {
                    UptBean up1 = new UptBean("DCP_KDNSHOPMAPPING");
                    up1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                    up1.addCondition("SHOPID",new DataValue(shopId,Types.VARCHAR));

                    //up1.addUpdateValue("PLATFORMID",new DataValue(kdnStoreCode,Types.VARCHAR));//平台门店编码
                    //up1.addUpdateValue("PLATFORMSTATUS",new DataValue(kdnStoreStatus,Types.VARCHAR));//平台状态0已失效，1已激活
                    if (!Check.Null(kdnStoreCode)&&!"null".equalsIgnoreCase(kdnStoreCode))
                    {
                        up1.addUpdateValue("PLATFORMID",new DataValue(kdnStoreCode,Types.VARCHAR));//平台门店编码
                    }
                    if (!Check.Null(kdnStoreStatus))
                    {
                        up1.addUpdateValue("PLATFORMSTATUS",new DataValue(kdnStoreStatus,Types.VARCHAR));//平台状态0已失效，1已激活
                    }
                    up1.addUpdateValue("UPLOADSTATUS",new DataValue("0",Types.VARCHAR));//上传状态（0创建成功,1上传失败）
                    up1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime,Types.DATE));//上传状态（0创建成功,1上传失败）
                    this.pData.clear();
                    this.addProcessData(new DataProcessBean(up1));
                    this.doExecuteDataToDB();
                }
                catch (Exception e)
                {
                    this.Log("【快递鸟门店创建成功后】更新数据库异常:"+e.getMessage()+",门店编码shopId="+shopId+",快递鸟返回的门店编码kdnStoreCode="+kdnStoreCode+",快递鸟返回的门店状态kdnStoreStatus="+kdnStoreStatus);
                }
            }
            else
            {
                errorMessage = reason;
            }

        }
        else
        {
            errorMessage = errorBuffer.toString();
        }

        res.setSuccess(nResult);
        if (nResult)
        {
            res.setServiceStatus("000");
            res.setServiceDescription("重试后上传快递鸟成功！");
        }
        else
        {
            res.setServiceStatus("100");
            res.setServiceDescription("重试后上传快递鸟失败:"+errorMessage);
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_KDNShopRestartReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_KDNShopRestartReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_KDNShopRestartReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_KDNShopRestartReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().getShopId()))
        {
            errMsg.append("门店编码shopId不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KDNShopRestartReq> getRequestType() {
        return new TypeToken<DCP_KDNShopRestartReq>(){};
    }

    @Override
    protected DCP_KDNShopRestartRes getResponseType() {
        return new DCP_KDNShopRestartRes();
    }

    private Map<String,Object> checkExist(String eId,String shopId) throws Exception
    {
        boolean nRet = false;
        String sql = " select * from dcp_kdnshopmapping where eid='"+eId+"' and shopid='"+shopId+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&!getQData.isEmpty())
        {
            return getQData.get(0);

        }
        else
        {
            return null;
        }

    }

    private Map<String, Object> getKDNDeliverySetByShippingShop(String eId,String deliveryType,String shopId,String shipMode) throws  Exception
    {
        Map<String, Object> resultMap = null;
        if (!"1".equals(shipMode))
        {
            shipMode = "0";//0全国快递，1同城快递
        }
        try {
            StringBuffer sqlBuffer = new StringBuffer("");
            sqlBuffer.append(" select * from (");
            //先查询适用于指定门店的参数
            sqlBuffer.append(" select  CAST('2' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " LEFT JOIN dcp_outsaleset_shop B  on A.EID=B.EID AND A.APPID=B.APPID AND A.DELIVERYTYPE=B.DELIVERYTYPE"
                    + " WHERE A.STATUS='100' AND A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='2'  AND B.SHOPID='" + shopId + "' AND SHIPMODE='"+shipMode+"' ");

            sqlBuffer.append(" UNION ALL ");
            //查询下适用全部门店
            sqlBuffer.append(" select  CAST('1' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " WHERE A.STATUS='100' AND A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='1' AND SHIPMODE='"+shipMode+"' ");

            sqlBuffer.append(") A ORDER BY A.IDX DESC");
            String sql = sqlBuffer.toString();
            this.Log("【创建快递鸟门店】查询物流设置参数sql=" + sql+",企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (getDatas != null && getDatas.isEmpty() == false) {
                resultMap = getDatas.get(0);
                this.Log("【创建快递鸟门店】查询物流设置参数内容=" + resultMap.toString());
            }
            else
            {
                this.Log("【创建快递鸟门店】查询物流设置参数为空！企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
            }
        }

        catch (Exception e)
        {
            this.Log("ExpressOrderCreate定时任务。【快递鸟物流类型】查询物流设置参数，异常:"+e.getMessage());
        }

        return resultMap;
    }

    private void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "KDNShopCreate");

        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }
}
