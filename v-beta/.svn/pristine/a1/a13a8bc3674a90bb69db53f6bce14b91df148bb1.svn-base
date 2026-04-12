package com.dsc.spos.thirdpart.duandian;

import cn.hutool.http.HttpUtil;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.thirdpart.qimai.QiMaiUtils;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class duandianService {
    private static final String logFileName = "duandianLog";
    //超时时间，默认10秒
    private int timeout = 10000;
    private String HOST = "http://open-dev.4008618618.com/crm/";
    //正向订单
    private String orderApi = "api/weigang/gateway/channelOrder/syncChannelOrder";
    //逆向订单
    private String orderApi_Reverse = "api/weigang/gateway/channelOrder/syncChannelReverseOrder";

    //营销活动积分
    private String marketingPointUpdateApi = "api/account/open/trans/marketingVirgoChange";

    /**
     * 同步订单
     * @param setMap
     * @param maps
     * @return
     * @throws Exception
     */
    public String syncChannelOrder(Map<String, Object> setMap,List<Map<String,Object>> maps) throws Exception
    {
        String res = "";
        try
        {
            String appKey = setMap.getOrDefault("APIKEY","").toString();//190633ce38293d2863e89f7ce274ba25
            String appSecret = setMap.getOrDefault("APISECRET","").toString();
            String url = setMap.getOrDefault("APIURL","").toString();
            if (url.trim().isEmpty())
            {
                url = HOST;
            }
            if (!url.endsWith("/"))
            {
                url = url+"/";
            }
            String billType = maps.get(0).get("TYPE").toString();
            if ("0".equals(billType))
            {
                url = url+orderApi;
            }
            else
            {
                url = url+orderApi_Reverse;
            }
            channelOrderDTO orderDTO = convertSaleMapToOrder(maps);
            String dataJsonStr = com.alibaba.fastjson.JSON.toJSONString(orderDTO);
            requestDTO reqDTO = new requestDTO();
            reqDTO.setAppKey(appKey);
            reqDTO.setData(dataJsonStr);
            reqDTO = getReqDTO(reqDTO,appSecret);
            String req = com.alibaba.fastjson.JSON.toJSONString(reqDTO);
            res = post(url,req,timeout);

        }
        catch (Exception e)
        {

        }
        return res;
    }
    /**
     * 销售单查询的map转换成实体类
     * @param maps
     * @return
     * @throws Exception
     */
    public channelOrderDTO convertSaleMapToOrder(List<Map<String,Object>> maps) throws Exception
    {
        /**
         *
         SELECT A.*,B.ITEM,B.PLUNO,B.PNAME,B.PLUBARCODE,B.UNIT,B.QTY,B.PRICE,B.AMT FROM DCP_SALE A inner join DCP_SALE_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO
         WHERE A.EID='99' and A.SALENO='0107141920230112142232775' and A.SHOPID='01'
         */
        channelOrderDTO orderDTO = new channelOrderDTO();
        try
        {
            boolean isRefundOrder = false;//是否退单
            Map<String,Object> mapHeader = maps.get(0);
            List<Map<String,Object>> mapGoodsDetail = maps;
            String billType = mapHeader.getOrDefault("TYPE","").toString();//单据类型:0-销售单1-凭单退货2-无单退货
            if (!"0".equals(billType))
            {
                isRefundOrder = true;
            }
            String saleNo = mapHeader.getOrDefault("SALENO","").toString();
            String shopId = mapHeader.getOrDefault("SHOPID","").toString();
            String cardNo = mapHeader.getOrDefault("CARDNO","").toString();
            String memberId = mapHeader.getOrDefault("MEMBERID","").toString();
            String memberName = mapHeader.getOrDefault("MEMBERNAME","").toString();

            String contMan = mapHeader.getOrDefault("CONTMAN","").toString();
            String contTel = mapHeader.getOrDefault("CONTTEL","").toString();
            String receiverName = mapHeader.getOrDefault("GETMAN","").toString();
            String receiverPhone = mapHeader.getOrDefault("GETMANTEL","").toString();
            String receiverAddress = mapHeader.getOrDefault("SHIPADD","").toString();
            String tot_amt = mapHeader.getOrDefault("TOT_AMT","0").toString();
            String point = mapHeader.getOrDefault("POINT_QTY","0").toString();
            String sdate = mapHeader.getOrDefault("SDATE","").toString();
            String STime = mapHeader.getOrDefault("STIME","").toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createDatetime = sdf.format(new Date());
            try
            {
                Date date1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(sdate+STime);
                createDatetime = sdf.format(date1);
            }
            catch (Exception e)
            {

            }
            orderDTO.setSiteCode(shopId);
            orderDTO.setChannelOrderCreateTime(createDatetime);
            orderDTO.setChannelOrderPayTime(createDatetime);
            orderDTO.setChannelOrderUpdateTime(createDatetime);
            if (isRefundOrder)
            {
                orderDTO.setChannelReverseOrderType("RETAIL_ORDER_RETURN_REFUND");
                orderDTO.setChannelReverseOrderDetailType("UNSUBSCRIBE");
                orderDTO.setChannelReverseOrderStatus("UNSUBSCRIBE");
                orderDTO.setChannelReverseOrderList(new ArrayList<>());
                orderDTO.setChannelReverseOrderCode(saleNo);
                orderDTO.setChannelReverseOrderCreateTime(createDatetime);
                orderDTO.setRefundAmt(tot_amt);
            }
            else
            {
                orderDTO.setChannelOrderCode(saleNo);
                orderDTO.setBuyerId(memberId);
                orderDTO.setBuyerAccount(cardNo);
                orderDTO.setBuyerName(memberName);
                orderDTO.setBuyerPhone("");
                orderDTO.setReceiverName(receiverName);
                orderDTO.setReceiverAddress(receiverAddress);
                orderDTO.setReceiverPhone(receiverPhone);
                orderDTO.setPoint(point);
                orderDTO.setActualPrice(tot_amt);
                orderDTO.setChannelOrderLineList(new ArrayList<>());
            }

            for (Map<String,Object> mapGoods : mapGoodsDetail)
            {
                String item = mapGoods.getOrDefault("ITEM","").toString();
                String pluNo = mapGoods.getOrDefault("PLUNO","").toString();
                String pluBarcode = mapGoods.getOrDefault("PLUBARCODE","").toString();
                String pName = mapGoods.getOrDefault("PNAME","").toString();
                String unit = mapGoods.getOrDefault("UNIT","").toString();
                String qty = mapGoods.getOrDefault("QTY","1").toString();
                String price = mapGoods.getOrDefault("PRICE","0").toString();
                String amt = mapGoods.getOrDefault("AMT","0").toString();
                channelOrderDTO.channelOrderLineDTO orderLineDTO = orderDTO.new channelOrderLineDTO();
                if (isRefundOrder)
                {
                    orderLineDTO.setChannelOrderLineCode(item);
                    orderLineDTO.setOriginalErpGoodsCode(pluNo);
                    orderLineDTO.setOriginalErpGoodsName(pluNo);
                    orderLineDTO.setRefundAmt(amt);
                    orderDTO.getChannelReverseOrderList().add(orderLineDTO);
                }
                else
                {
                    orderLineDTO.setChannelOrderCode(saleNo);
                    orderLineDTO.setChannelOrderLineCode(item);
                    orderLineDTO.setErpGoodsCode(pluNo);
                    orderLineDTO.setErpGoodsName(pName);
                    orderLineDTO.setErpGoodsUnit(unit);
                    orderLineDTO.setErpGoodsUnitPrice(price);
                    orderLineDTO.setGoodsQty(qty);
                    orderLineDTO.setActualPayAmt(amt);
                    orderDTO.getChannelOrderLineList().add(orderLineDTO);
                }
            }



        }
        catch (Exception e)
        {

        }
        return orderDTO;
    }

    /**
     * 同步积分
     * @param setMap
     * @param reqData
     * @return
     * @throws Exception
     */
    public String syncMarketingPoint(Map<String, Object> setMap,marketingVirgoChangeReq reqData) throws Exception
    {
        String res = "";
        try
        {
            String appKey = setMap.getOrDefault("APIKEY","").toString();//190633ce38293d2863e89f7ce274ba25
            String appSecret = setMap.getOrDefault("APISECRET","").toString();
            String url = setMap.getOrDefault("APIURL","").toString();
            if (url.trim().isEmpty())
            {
                url = HOST;
            }
            if (!url.endsWith("/"))
            {
                url = url+"/";
            }
            url = url+marketingPointUpdateApi;

            requestDTO reqDTO = new requestDTO();
            reqDTO.setAppKey(appKey);
            String dataJsonStr = com.alibaba.fastjson.JSON.toJSONString(reqData);
            reqDTO.setData(dataJsonStr);
            reqDTO = getReqDTO(reqDTO,appSecret);
            String req = com.alibaba.fastjson.JSON.toJSONString(reqDTO);
            res = post(url,req,timeout);

        }
        catch (Exception e)
        {

        }
        return res;
    }

    public Map<String, Object> getBasicMap(String eId) throws Exception {
        Map<String, Object> basicMap=null;
        StringBuffer sql=new StringBuffer("SELECT A.*"
                + " FROM DCP_ECOMMERCE A "
                + " WHERE A.LOADDOCTYPE='"+ orderLoadDocType.DUANDIANOMS+"' "
                + " AND A.EID='" + eId + "'");
        List<Map<String, Object>> basicList = null;
        try{
            basicList = StaticInfo.dao.executeQuerySQL(sql.toString(), null);
        }catch(Exception e){
            HelpTools.writelog_fileName("查询渠道资料sql:"+sql.toString()+",异常:"+e.getMessage(),logFileName);
        }
        if(basicList==null||basicList.size()==0){
            HelpTools.writelog_fileName("查询渠道DCP_ECOMMERCE 资料未配置！",logFileName);
        }else if(basicList.size()>1){
            HelpTools.writelog_fileName("查询渠道DCP_ECOMMERCE 资料配置资料异常,生效数量大于一笔!！",logFileName);
        }else{
            basicMap=basicList.get(0);
        }
        return basicMap;
    }

    /**
     * post请求
     * @param url
     * @param body
     * @param timeout
     * @return
     * @throws Exception
     */
    private String post(String url,String body,int timeout) throws Exception
    {
        String requestId= UUID.randomUUID().toString();
        String logStart = "["+requestId+"]";
        HelpTools.writelog_fileName(logStart+"请求Url:"+url + ",请求Req:"+body,logFileName);
        try {
            String res = HttpUtil.post(url, body, timeout);
            /*try
            {
                res = Convert.unicodeToStr(res);
            }
            catch (Exception e)
            {
            }*/

            HelpTools.writelog_fileName(logStart+"请求Url:"+url + ",返回Res:"+res,logFileName);
            return res;
        }
        catch (Exception e)
        {
            HelpTools.writelog_fileName(logStart+"请求tUrl:"+url + ",返回异常:"+e.getMessage(),logFileName);
            return "";
        }

    }

    /**
     * 获取sign，返回实体类
     * @param reqDTO
     * @param appSecret
     * @return
     * @throws Exception
     */
    private requestDTO getReqDTO (requestDTO reqDTO,String appSecret) throws Exception
    {
        try
        {
            /************按顺序拼接key=value最后加上appSecret然后加密即可**************/
            Map<String,String> args = new HashMap<>();
            args.put("appKey",reqDTO.getAppKey());
            args.put("data",reqDTO.getData());
            List<String> keyList = args.entrySet().stream().map(Map.Entry::getKey).sorted().collect(Collectors.toList());
            String argsStr = "";
            for (String key: keyList)
            {
                if (argsStr.isEmpty())
                {
                    argsStr = key +"="+args.get(key);
                }
                else
                {
                    argsStr = argsStr +"&"+ key +"="+args.get(key);
                }

            }
            argsStr = argsStr + appSecret;
            String sign = string2SHA512(argsStr);
            reqDTO.setSign(sign);
        }
        catch (Exception e)
        {

        }
        return reqDTO;
    }

    /**
     * 加密
     * @param str
     * @return
     * @throws Exception
     */
    private String string2SHA512(String str) throws Exception
    {
        MessageDigest messageDigest;
        String encdeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            encdeStr = Hex.encodeHexString(hash);
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            //e.printStackTrace();
        }
        catch ( Exception e)
        {

        }
        return encdeStr;
    }
}
