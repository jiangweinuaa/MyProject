package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_KDNShopUpdateReq;
import com.dsc.spos.json.cust.res.DCP_KDNShopUpdateRes;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_KDNShopUpdate extends SPosAdvanceService<DCP_KDNShopUpdateReq, DCP_KDNShopUpdateRes> {
    @Override
    protected void processDUID(DCP_KDNShopUpdateReq req, DCP_KDNShopUpdateRes res) throws Exception {

        String opNo = req.getOpNO();
        String opName = req.getOpName();
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
        String platformStatus = mapShop.get("PLATFORMSTATUS").toString();
        if (Check.Null(req.getRequest().getPlatformStatus()))
        {
            req.getRequest().setPlatformStatus(platformStatus);
        }
        String sql = " select A.*,B.ORG_NAME from dcp_org A left join dcp_org_lang B on A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='zh_CN' where A.EID='"+eId+"' and A.ORGANIZATIONNO='"+shopId+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData== null||getQData.isEmpty())
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "查询该门店:"+shopId+"信息不存在!");
        }
        Map<String,Object> SendShopInfoMap = getQData.get(0);
        String storeName = SendShopInfoMap.getOrDefault("ORG_NAME","").toString();
        if (Check.Null(req.getRequest().getShopName()))
        {
            req.getRequest().setShopName(storeName);
        }
        String sendProvinceName = SendShopInfoMap.getOrDefault("PROVINCE","").toString();
        String sendCityName = SendShopInfoMap.getOrDefault("CITY","").toString();
        String sendExpAreaName = SendShopInfoMap.getOrDefault("COUNTY","").toString();
        String sendAddress = SendShopInfoMap.getOrDefault("ADDRESS","").toString();
        String senderMobile = SendShopInfoMap.getOrDefault("PHONE", "").toString();//寄件人联系电话
        if (Check.Null(req.getRequest().getPhone()))
        {
            req.getRequest().setPhone(senderMobile);
        }
        String longitude = SendShopInfoMap.getOrDefault("LONGITUDE", "").toString();//经度
        String latitude = SendShopInfoMap.getOrDefault("LATITUDE", "").toString();//维度
        req.getRequest().setProvince(sendProvinceName);
        req.getRequest().setCity(sendCityName);
        req.getRequest().setCounty(sendExpAreaName);
        req.getRequest().setAddress(sendAddress);
        req.getRequest().setLatitude(latitude);
        req.getRequest().setLongitude(longitude);
        UptBean up1 = new UptBean("DCP_KDNSHOPMAPPING");
        up1.addCondition("EID",new DataValue(eId,Types.VARCHAR));
        up1.addCondition("SHOPID",new DataValue(shopId,Types.VARCHAR));

        if (req.getRequest().getIdCard()!=null)
        {
            up1.addUpdateValue("IDCARD",new DataValue(req.getRequest().getIdCard(),Types.VARCHAR));
        }
        if (req.getRequest().getIdCardName()!=null)
        {
            up1.addUpdateValue("IDCARDNAME",new DataValue(req.getRequest().getIdCardName(),Types.VARCHAR));
        }
        if (req.getRequest().getIdCardFront()!=null)
        {
            up1.addUpdateValue("IDCARDFRONT_IMAGE",new DataValue(req.getRequest().getIdCardFront(),Types.VARCHAR));
        }
        if (req.getRequest().getIdCardBack()!=null)
        {
            up1.addUpdateValue("IDCARDBACK_IMAGE",new DataValue(req.getRequest().getIdCardBack(),Types.VARCHAR));
        }
        if (req.getRequest().getCreditCode()!=null)
        {
            up1.addUpdateValue("CREDITCODE",new DataValue(req.getRequest().getCreditCode(),Types.VARCHAR));
        }
        if (req.getRequest().getLicense()!=null)
        {
            up1.addUpdateValue("LICENSE_IMAGE",new DataValue(req.getRequest().getLicense(),Types.VARCHAR));
        }

        if (req.getRequest().getShopPicture()!=null)
        {
            up1.addUpdateValue("SHOP_IMAGE",new DataValue(req.getRequest().getShopPicture(),Types.VARCHAR));
        }
        up1.addUpdateValue("LASTMODIOPID",new DataValue(opNo,Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPNAME",new DataValue(opName,Types.VARCHAR));
        up1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime,Types.DATE));
        this.addProcessData(new DataProcessBean(up1));
        this.doExecuteDataToDB();

        boolean nResult = true;
        res.setSuccess(nResult);
        res.setServiceStatus("000");
        res.setServiceDescription("门店修改保存本地成功");

        if ("0".equals(uploadstatus))
        {
            nResult = false;
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
            boolean isInvokeKDN = true;
            String messStr_send = "门店("+shopId+")组织信息不完整烦请维护以下内容后再创建<br>";

            if(Check.Null(req.getRequest().getProvince()))
            {
                isInvokeKDN = false;
                messStr_send += "省份未维护,";
            }
            if(Check.Null(req.getRequest().getCity()))
            {
                isInvokeKDN = false;
                messStr_send += "城市未维护,";
            }
            if(Check.Null(req.getRequest().getCounty()))
            {
                isInvokeKDN = false;
                messStr_send += "区/县未维护,";
            }
            if(Check.Null(req.getRequest().getAddress()))
            {
                isInvokeKDN = false;
                messStr_send += "详细地址未维护,";
            }
            if(Check.Null(req.getRequest().getPhone()))
            {
                isInvokeKDN = false;
                messStr_send += "联系电话未维护,";
            }
            if(Check.Null(req.getRequest().getLatitude())||Check.Null(req.getRequest().getLongitude()))
            {
                isInvokeKDN = false;
                messStr_send += "经纬度未维护,";
            }

            if(!Check.Null(req.getRequest().getLatitude())&&!Check.Null(req.getRequest().getLongitude()))
            {
                BigDecimal lat = new BigDecimal("0");
                BigDecimal lng = new BigDecimal("0");
                try
                {
                    lat = new BigDecimal(req.getRequest().getLatitude());
                    lng = new BigDecimal(req.getRequest().getLongitude());
                    if (lat.compareTo(BigDecimal.ZERO)==1&&lng.compareTo(BigDecimal.ZERO)==1)
                    {
                        //门店经度（精确到小数点后5位）
                        req.getRequest().setLatitude(lat.setScale(5,BigDecimal.ROUND_HALF_UP)+"");
                        req.getRequest().setLongitude(lng.setScale(5,BigDecimal.ROUND_HALF_UP)+"");
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

            lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //下面开始调用接口
            kdnTCStoreReq requestData = new kdnTCStoreReq();
            requestData.setStoreCode(shopId);
            requestData.setStoreName(req.getRequest().getShopName());
            requestData.setStoreGoodsType(storeGoodsType);
            requestData.setProvinceName(req.getRequest().getProvince());
            requestData.setCityName(req.getRequest().getCity());
            requestData.setAreaName(req.getRequest().getCounty());
            requestData.setAddress(req.getRequest().getAddress());
            requestData.setLbsType(2);//1：百度地图 2：高德地图 3：腾讯地图
            requestData.setLongitude(req.getRequest().getLongitude());
            requestData.setLatitude(req.getRequest().getLatitude());
            requestData.setContactName(req.getRequest().getContactName());
            requestData.setStoreStatus(req.getRequest().getPlatformStatus());
            //必传节点，否则返回为空
            if (Check.Null(req.getRequest().getPlatformStatus()))
            {
               requestData.setStoreStatus("1");//0：已失效，1：已激活
            }
            if (req.getRequest().getPhone().trim().startsWith("1") && req.getRequest().getPhone().trim().length() == 11)
            {
                requestData.setMobile(req.getRequest().getPhone().trim());
            }
            else {
                requestData.setPhone(req.getRequest().getPhone());
            }
            if (!Check.Null(req.getRequest().getIdCardFront()))
            {
                requestData.setAttachType("1");//附件类型(有上传附件时，该字段必填)1：URL地址 2：Base64(暂不支持) 3：图片hash值
                requestData.setIdCardFront(req.getRequest().getIdCardFront());
            }
            if (!Check.Null(req.getRequest().getIdCardBack()))
            {
                requestData.setAttachType("1");//附件类型(有上传附件时，该字段必填)1：URL地址 2：Base64(暂不支持) 3：图片hash值
                requestData.setIdCardBack(req.getRequest().getIdCardBack());
            }
            if (!Check.Null(req.getRequest().getShopPicture()))
            {
                requestData.setAttachType("1");//附件类型(有上传附件时，该字段必填)1：URL地址 2：Base64(暂不支持) 3：图片hash值
                requestData.setShopPicture(req.getRequest().getShopPicture());
            }
            requestData.setIdCard(req.getRequest().getIdCard());
            requestData.setIdCardName(req.getRequest().getIdCardName());
            requestData.setCreditCode(req.getRequest().getCreditCode());
            kdnTCService  kdnService = null;
            if ("1663339".equals(customerCode))
            {
                kdnService = new kdnTCService(true);
            }
            else
            {
                kdnService = new kdnTCService();
            }
            StringBuffer errorBuffer = new StringBuffer("");
            nResult = false;
            String errorMessage = "";
            String resKdn = kdnService.kdnStoreUpdate(outSalesetMap,requestData,errorBuffer);
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
                    String kdnStoreCode = dataJson.getString("kdnStoreCode");//"kdnStoreCode":null，用getString会返回String值"null"，用getOrDefault会返回对象为null，getOrDefault(...).toString()会报错
                    String kdnStoreStatus = dataJson.getOrDefault("kdnStoreStatus","")==null?"":dataJson.getOrDefault("kdnStoreStatus","").toString();
                    try
                    {
                        UptBean up2 = new UptBean("DCP_KDNSHOPMAPPING");
                        up2.addCondition("EID",new DataValue(eId,Types.VARCHAR));
                        up2.addCondition("SHOPID",new DataValue(shopId,Types.VARCHAR));
                        if (!Check.Null(kdnStoreCode)&&!"null".equalsIgnoreCase(kdnStoreCode))
                        {
                            up2.addUpdateValue("PLATFORMID",new DataValue(kdnStoreCode,Types.VARCHAR));//平台门店编码
                        }
                        if (!Check.Null(kdnStoreStatus))
                        {
                            up2.addUpdateValue("PLATFORMSTATUS",new DataValue(kdnStoreStatus,Types.VARCHAR));//平台状态0已失效，1已激活
                        }

                        up2.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime,Types.DATE));
                        this.pData.clear();
                        this.addProcessData(new DataProcessBean(up2));
                        this.doExecuteDataToDB();
                    }
                    catch (Exception e)
                    {
                        this.Log("【修改快递鸟门店成功后】更新数据库异常:"+e.getMessage()+",门店编码shopId="+shopId+",快递鸟返回的门店编码kdnStoreCode="+kdnStoreCode+",快递鸟返回的门店状态kdnStoreStatus="+kdnStoreStatus);
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
                res.setServiceDescription("修改门店保存本地成功且调用快递鸟成功！");
            }
            else
            {
                res.setServiceStatus("100");
                res.setServiceDescription("修改门店保存本地成功，但调用快递鸟失败:"+errorMessage);
            }
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_KDNShopUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_KDNShopUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_KDNShopUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_KDNShopUpdateReq req) throws Exception {
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
        if (Check.Null(req.getRequest().getContactName()))
        {
            errMsg.append("门店联系人contactName不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KDNShopUpdateReq> getRequestType() {
        return new TypeToken<DCP_KDNShopUpdateReq>(){};
    }

    @Override
    protected DCP_KDNShopUpdateRes getResponseType() {
        return new DCP_KDNShopUpdateRes();
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
