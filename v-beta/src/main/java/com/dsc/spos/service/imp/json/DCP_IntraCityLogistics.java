package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.cust.req.DCP_IntraCityLogisticsReq;
import com.dsc.spos.json.cust.res.DCP_IntraCityLogisticsRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.ThirdpartConstants;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.candao.candaoService;
import com.dsc.spos.waimai.kdniao.kdnTCService;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_IntraCityLogistics extends SPosBasicService<DCP_IntraCityLogisticsReq, DCP_IntraCityLogisticsRes> {
    @Override
    protected boolean isVerifyFail(DCP_IntraCityLogisticsReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        DCP_IntraCityLogisticsReq.level1Elm request = req.getRequest();
        if (request==null)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不能为空！");
        }

        if (Check.Null(request.getOrderNo())) {
            errCt++;
            errMsg.append("订单号不能为空, ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_IntraCityLogisticsReq> getRequestType() {
        return new TypeToken<DCP_IntraCityLogisticsReq>(){};
    }

    @Override
    protected DCP_IntraCityLogisticsRes getResponseType() {
        return new DCP_IntraCityLogisticsRes();
    }

    @Override
    protected DCP_IntraCityLogisticsRes processJson(DCP_IntraCityLogisticsReq req) throws Exception {
        DCP_IntraCityLogisticsRes res = this.getResponse();
        res.setDatas(res.new level1Elm());
        String orderNo = req.getRequest().getOrderNo();
        String eId = req.geteId();
        String sql = "select * from DCP_order where EID='" + eId + "' and orderno='" + orderNo + "'";
        this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】查询开始：单号OrderNO=" + orderNo + " 传入的参数eid=" + eId + "  查询语句：" + sql);
        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
        if (getQDataDetail == null || getQDataDetail.isEmpty())
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("该订单不存在");
            this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】查询完成：该订单不存在！ 单号OrderNO=" + orderNo);
            return res;
        }
        Map<String, Object> map_order = getQDataDetail.get(0);
        String deliveryType = getQDataDetail.get(0).getOrDefault("DELIVERYTYPE", "").toString();
        String channelId = map_order.get("CHANNELID").toString();
        String machShopId = map_order.get("MACHSHOP").toString();
        String machShopName = map_order.get("MACHSHOPNAME").toString();
        String shippingId = map_order.get("SHIPPINGSHOP").toString();
        String shippingName = map_order.get("SHIPPINGSHOPNAME").toString();
        String shopId = map_order.get("SHOP").toString();
        String shopName = map_order.get("SHOPNAME").toString();
        String shipType = map_order.get("SHIPTYPE").toString();//配送方式( 1.订单来源渠道配送 2.全国快递 3.顾客自提 5ERP总部配送 6同城配送)
        String ref_deliveryNo = map_order.get("REF_DELIVERYNO").toString();//我们传入的商家物流单号
        String out_deliveryNo = map_order.get("OUTDOCORDERNO").toString();//对接的物流返回订单号
        Boolean isSupport = false;
        if (ThirdpartConstants.cangdao_deliveryType.equals(deliveryType))
        {
            //餐道支持
            isSupport = true;
        }
        else if (ThirdpartConstants.kdn_deliveryType.equals(deliveryType))
        {
            //快递鸟，同城才支持
            if ("6".equals(shipType))
            {
                isSupport = true;
            }

        }
        else
        {
            isSupport = false;
        }
        if (!isSupport)
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("暂不支持轨迹查询");
            this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】查询完成：物流类型deliveryType="+ deliveryType+",暂不支持查询骑手位置信息，单号OrderNO=" + orderNo);
            return res;
        }

        Map<String, Object> outSalesetMap = null;
        if (ThirdpartConstants.cangdao_deliveryType.equals(deliveryType))
        {
            outSalesetMap =  this.getDeliverySetByShippingShop(eId,deliveryType,shippingId);
        }
        else if (ThirdpartConstants.kdn_deliveryType.equals(deliveryType))
        {
            String shipMode = "1";//kdn 物流产品类型 0全国快递，1同城快递
            outSalesetMap=this.getKDNDeliverySetByShippingShop(eId,deliveryType,shippingId,shipMode);
        }

        if(outSalesetMap==null||outSalesetMap.isEmpty())
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("查询骑手位置信息失败(物流配置参数为空)");
            this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】查询物流配置参数为空，单号OrderNO=" + orderNo);
            return res;
        }

        if (ThirdpartConstants.cangdao_deliveryType.equals(deliveryType))
        {
            String isTest = outSalesetMap.getOrDefault("ISTEST","").toString();
            candaoService candao= null;
            if ("Y".equals(isTest))
            {
                candao = new candaoService(true);
            }
            else
            {
                candao = new candaoService();
            }
            StringBuffer apiErrorMessage = new StringBuffer();
            String resJson = candao.candaoGetRiderPosition(ref_deliveryNo,outSalesetMap,map_order,apiErrorMessage);
            this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】三方物流接口返回res:"+resJson+",单号OrderNO=" + orderNo);
            if(resJson == null||resJson.isEmpty())
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("查询骑手位置信息失败");
                this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】三方物流接口返回为空，单号OrderNO=" + orderNo);
                return res;
            }
            com.alibaba.fastjson.JSONObject json1 = JSONObject.parseObject(resJson);
            String code=json1.get("status").toString();//1成功，其他失败
            if(!"1".equals(code))
            {
                String reason = "";
                if(json1.containsKey("msg"))
                {
                    reason = json1.get("msg").toString();
                }
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("查询骑手位置信息失败:"+reason);
                //this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】三方物流接口返回为空，单号OrderNO=" + orderNo);
                return res;
            }
            JSONObject data = json1.getJSONObject("data");
            String riderName = data.getOrDefault("riderName","").toString();
            String riderPhone = data.getOrDefault("riderPhone","").toString();
            String longitude = data.getOrDefault("longitude","").toString();
            String latitude = data.getOrDefault("latitude","").toString();
            res.getDatas().setRiderName(riderName);
            res.getDatas().setRiderPhone(riderPhone);
            res.getDatas().setLongitude(longitude);
            res.getDatas().setLatitude(latitude);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
            return res;
        }
        else if (ThirdpartConstants.kdn_deliveryType.equals(deliveryType))
        {
            kdnTCService kdn = null;
            String EBusinessID = outSalesetMap.get("APPSIGNKEY").toString();//用户ID
            if ("1663339".equals(EBusinessID))
            {
                kdn = new kdnTCService(true);
            }
            else
            {
                kdn = new kdnTCService();
            }
            StringBuffer apiErrorMessage = new StringBuffer();
            String resJson = kdn.kdnGetRiderPosition(ref_deliveryNo,outSalesetMap,orderNo,apiErrorMessage);
            this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】三方物流接口返回res:"+resJson+",单号OrderNO=" + orderNo);
            if(resJson == null||resJson.isEmpty())
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("查询骑手位置信息失败");
                this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】三方物流接口返回为空，单号OrderNO=" + orderNo);
                return res;
            }
            JSONObject json1 = JSONObject.parseObject(resJson);
            String resultCode = json1.get("resultCode").toString();
            String reason = json1.getOrDefault("reason","")==null?"":json1.getOrDefault("reason","").toString();
            if (!"100".equalsIgnoreCase(resultCode))
            {
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("查询骑手位置信息失败:"+reason);
                //this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】三方物流接口返回为空，单号OrderNO=" + orderNo);
                return res;
            }
            JSONObject data =  json1.getJSONObject("data");
            String riderName = data.getOrDefault("courierName","").toString();
            String riderPhone = data.getOrDefault("courierMobile","").toString();
            String longitude = "";//data.getOrDefault("longitude","").toString();
            String latitude = "";//data.getOrDefault("latitude","").toString();
            //快递鸟，返回的是所有位置信息，取最新的位置
            JSONArray locationsJsonArray = data.getJSONArray("locations");
            if (locationsJsonArray!=null&&locationsJsonArray.size()>0)
            {
                long preTime = 0;//前一次的时间
                for (int i = 0; i < locationsJsonArray.size(); i++)
                {
                    JSONObject parObj = locationsJsonArray.getJSONObject(i);
                    String longitude_par = parObj.getOrDefault("longitude","").toString();
                    String latitude_par = parObj.getOrDefault("latitude","").toString();
                    String uploadTime = parObj.getOrDefault("latitude","").toString();
                    uploadTime = uploadTime.replace("-","").replace(" ","").replace(":","");
                    long curTime = 0;
                    try {
                        curTime = Long.parseLong(uploadTime);
                    }
                    catch (Exception e)
                    {

                    }
                    //第1次 赋值下
                    if (i ==0)
                    {
                        longitude = longitude_par;
                        latitude = latitude_par;
                        preTime = curTime;
                    }
                    else
                    {
                        //返回最新的时间
                        if (curTime>preTime)
                        {
                            longitude = longitude_par;
                            latitude = latitude_par;
                            preTime = curTime;
                        }
                    }

                }

            }
            res.getDatas().setRiderName(riderName);
            res.getDatas().setRiderPhone(riderPhone);
            res.getDatas().setLongitude(longitude);
            res.getDatas().setLatitude(latitude);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
            return res;
        }
        else
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("暂不支持轨迹查询");
            this.Log("【调用DCP_IntraCityLogistics接口，同城物流骑手位置信息】查询完成：物流类型deliveryType="+ deliveryType+",暂不支持查询骑手位置信息，单号OrderNO=" + orderNo);
            return res;
        }

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_IntraCityLogisticsReq req) throws Exception {
        return null;
    }

    protected void Log(String log)  {
        try
        {
            HelpTools.writelog_fileName(log, "DCP_IntraCityLogistics");
        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }

    /**
     * 根据物流类型以及配送门店，获取相应的物流类型设置的参数（先查询配送门店为指定生效门店的物流，再查询默认生效全部门店的物流）
     * @param eId 企业ID
     * @param deliveryType 物流类型
     * @param shopId 配送门店ID
     * @return
     * @throws Exception
     */
    public Map<String, Object> getDeliverySetByShippingShop(String eId,String deliveryType,String shopId) throws  Exception
    {
        Map<String, Object> resultMap = null;
        try {
            StringBuffer sqlBuffer = new StringBuffer("");
            sqlBuffer.append(" select * from (");
            //先查询适用于指定门店的参数
            sqlBuffer.append(" select  CAST('2' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " LEFT JOIN dcp_outsaleset_shop B  on A.EID=B.EID AND A.APPID=B.APPID AND A.DELIVERYTYPE=B.DELIVERYTYPE"
                    + " WHERE A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='2' and A.STATUS='100'  AND B.SHOPID='" + shopId + "' ");

            sqlBuffer.append(" UNION ALL ");
            //查询下适用全部门店
            sqlBuffer.append(" select  CAST('1' AS NVARCHAR2(1)) IDX, A.* from dcp_outsaleset A "
                    + " WHERE A.EID='" + eId + "' AND A.DELIVERYTYPE='" + deliveryType + "'  and A.SHOPTYPE='1' and A.STATUS='100' ");

            sqlBuffer.append(") A ORDER BY A.IDX DESC");
            String sql = sqlBuffer.toString();
            this.Log("【物流配置是否指定门店】查询物流设置参数sql=" + sql+",企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (getDatas != null && getDatas.isEmpty() == false) {
                resultMap = getDatas.get(0);
                this.Log("【物流配置是否指定门店】查询物流设置参数内容=" + resultMap.toString());
            }
            else
            {
                this.Log("【物流配置是否指定门店】查询物流设置参数为空！企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId);
            }
        }

        catch (Exception e)
        {
            this.Log("【物流配置是否指定门店】查询物流设置参数，异常:"+e.getMessage());
        }

        return resultMap;

    }

    /**
     * kdn快递鸟物流参数(全国快递和同城配送不一致)
     * @param eId
     * @param deliveryType
     * @param shopId
     * @param shipMode
     * @return
     * @throws Exception
     */
    public Map<String, Object> getKDNDeliverySetByShippingShop(String eId,String deliveryType,String shopId,String shipMode) throws  Exception
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
            this.Log("【快递鸟物流类型】查询物流设置参数sql=" + sql+",企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
            if (getDatas != null && getDatas.isEmpty() == false) {
                resultMap = getDatas.get(0);
                this.Log("【快递鸟物流类型】查询物流设置参数内容=" + resultMap.toString());
            }
            else
            {
                this.Log("【快递鸟物流类型】查询物流设置参数为空！企业eId=" +eId+",物流类型deliveryType="+deliveryType+",配送门店shopId="+shopId+",配送方式(0全国；1同城)shipMode="+shipMode);
            }
        }

        catch (Exception e)
        {
            this.Log("【快递鸟物流类型】查询物流设置参数，异常:"+e.getMessage());
        }

        return resultMap;
    }
}
