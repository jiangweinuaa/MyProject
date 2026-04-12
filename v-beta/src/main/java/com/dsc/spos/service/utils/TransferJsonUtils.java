package com.dsc.spos.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;


import com.dsc.spos.json.utils.ParseJson;

import com.google.gson.reflect.TypeToken;

public class TransferJsonUtils
{

    Logger logger = LogManager.getLogger(TransferJsonUtils.class);

    public TransferJsonUtils()
    {

    }

    /**
     * 轉換Json function POS Msg -> wgs Msg
     * @param json Json字串
     * @return 重組轉換後的Json字串
     * @throws Exception
     */
    public String transferWGSJsonMsg(String json) throws Exception
    {

        //System.out.println("WGS MSG : " + json);
        logger.info("\r\nWGS MSG : " + json);

        ParseJson pj=new ParseJson();

        Map<String,Object> jsonMap = pj.jsonToBean(json, new TypeToken<Map<String,Object>>(){});
        @SuppressWarnings("unchecked")
        Map<String,Object> serviceMap = (Map<String, Object>) jsonMap.get("service");
        StringBuffer newJson = new StringBuffer("");
        Map<String,Object> posJsonMap = new HashMap<String,Object>();
        String servicename="SaleCreate";//服务名称
        String dataname="sale";//第一个节点名称
        if(serviceMap.get("name").toString().equals("pos.sale.create"))
        {
            servicename="SaleCreate";
            dataname="sale";
        }
        posJsonMap.put("serviceId", servicename);
        newJson.append("\""+"serviceId"+"\""+":"+"\""+servicename+"\"");

        //處理token 取得T100的key
        if(jsonMap.containsKey("key")){
            posJsonMap.put("token", jsonMap.get("key"));
            newJson.append(","+"\""+"token"+"\""+":"+"\""+jsonMap.get("key")+"\"");
            newJson.append(","+"\""+"keyid"+"\""+":"+"\""+jsonMap.get("key")+"\"");
        }

        //處理企業編號
        if(jsonMap.containsKey("datakey")){
            Map<String, Object> datakeyMap = (Map<String,Object>) jsonMap.get("datakey");
            posJsonMap.put("eId", datakeyMap.get("EntId").toString());
            posJsonMap.put("organizationNO", datakeyMap.get("CompanyId").toString());
            newJson.append(","+"\""+"eId"+"\""+":"+"\""+datakeyMap.get("EntId").toString()+"\"");
            newJson.append(","+"\""+"organizationNO"+"\""+":"+"\""+datakeyMap.get("CompanyId").toString()+"\"");
        }

        //取得payload
        Map<String,Object> payloadMap = jsonMap.get("payload") != null ? (Map<String, Object>) jsonMap.get("payload") : null;
        Map<String,Object> stdDataMap = null;
        Map<String,Object> parameterMap = null;

        //格式防呆
        if(payloadMap == null){
            throw new Exception("Json格式錯誤，請檢查T100之Json格式");
        }else{
            //取得std_data
            stdDataMap = payloadMap.get("std_data") != null ? (Map<String, Object>) payloadMap.get("std_data") : null;
            if(stdDataMap == null){
                throw new Exception("Json格式錯誤，請檢查T100之Json格式");
            }else{
                //取得parameter
                parameterMap = stdDataMap.get("parameter") != null ? (Map<String, Object>) stdDataMap.get("parameter") : null;
                if(parameterMap == null){
                    throw new Exception("Json格式錯誤，請檢查T100之Json格式");
                }
            }
        }

        StringBuffer datastring = new StringBuffer("");
        @SuppressWarnings("unchecked")
        List<Object> salecreateList = (List<Object>) parameterMap.get("sale");
        for (Object object : salecreateList)
        {
            @SuppressWarnings("unchecked")
            Map<String,Object> salecreateMap = (Map<String, Object>) object;
            datastring .append( pj.beanToJson(salecreateMap)+",");
        }
        if (datastring.length()>0)
        {
            datastring.insert(0,"[");
            datastring.deleteCharAt(datastring.length()-1);
            datastring.append("]");
        }
        newJson.append(","+"\""+dataname+"\""+":"+datastring.toString());
        newJson.insert(0,"{");
        newJson.append("}");

        pj=null;
        logger.info("\r\nPOS MSG : " + newJson.toString());
        return newJson.toString();
    }

    public String transferCCBJsonMsg(String json) throws Exception{
        //System.out.println("WGS MSG : " + json);
        logger.info("\r\nWGS MSG : " + json);
        ParseJson pj=new ParseJson();

        Map<String,Object> jsonMap = pj.jsonToBean(json, new TypeToken<Map<String,Object>>(){});
        @SuppressWarnings("unchecked")
        Map<String,Object> serviceMap = (Map<String, Object>) jsonMap.get("service");
        StringBuffer newJson = new StringBuffer("");
        Map<String,Object> posJsonMap = new HashMap<String,Object>();
        String servicename="CCBEnDecrypt";//服务名称

        if(serviceMap.get("name").toString().equals("pos.sale.ccbparam"))
        {
            servicename="CCBEnDecrypt";
            //dataname="sale";
        }
        posJsonMap.put("serviceId", servicename);
        newJson.append("\""+"serviceId"+"\""+":"+"\""+servicename+"\"");

        //處理token 取得T100的key
        if(jsonMap.containsKey("key")){
            posJsonMap.put("token", jsonMap.get("key"));
            newJson.append(","+"\""+"token"+"\""+":"+"\""+jsonMap.get("key")+"\"");
            newJson.append(","+"\""+"keyid"+"\""+":"+"\""+jsonMap.get("key")+"\"");
        }

        //處理企業編號
        if(jsonMap.containsKey("datakey")){
            Map<String, Object> datakeyMap = (Map<String,Object>) jsonMap.get("datakey");
            posJsonMap.put("eId", datakeyMap.get("EntId").toString());
            posJsonMap.put("organizationNO", datakeyMap.get("CompanyId").toString());
            newJson.append(","+"\""+"eId"+"\""+":"+"\""+datakeyMap.get("EntId").toString()+"\"");
            newJson.append(","+"\""+"organizationNO"+"\""+":"+"\""+datakeyMap.get("CompanyId").toString()+"\"");
        }

        //取得payload
        Map<String,Object> payloadMap = jsonMap.get("payload") != null ? (Map<String, Object>) jsonMap.get("payload") : null;
        Map<String,Object> stdDataMap = null;
        Map<String,Object> parameterMap = null;

        //格式防呆
        if(payloadMap == null){
            throw new Exception("Json格式錯誤，請檢查T100之Json格式");
        }else{
            //取得std_data
            stdDataMap = payloadMap.get("std_data") != null ? (Map<String, Object>) payloadMap.get("std_data") : null;
            if(stdDataMap == null){
                throw new Exception("Json格式錯誤，請檢查T100之Json格式");
            }else{
                //取得parameter
                parameterMap = stdDataMap.get("parameter") != null ? (Map<String, Object>) stdDataMap.get("parameter") : null;
                if(parameterMap == null){
                    throw new Exception("Json格式錯誤，請檢查T100之Json格式");
                }
            }
        }

        newJson.append(","+"\""+"getType"+"\""+":"+"\""+parameterMap.get("getType").toString()+"\"");
        newJson.append(","+"\""+"strSrcParas"+"\""+":"+"\""+parameterMap.get("strSrcParas").toString()+"\"");
        newJson.append(","+"\""+"strKey"+"\""+":"+"\""+parameterMap.get("strKey").toString()+"\"");

        newJson.insert(0,"{");
        newJson.append("}");

        pj=null;

        logger.info("\r\nPOS MSG : " + newJson.toString());
        return newJson.toString();
    }


    /**
     * 轉換Json function T100->POS
     * @param json Json字串
     * @param replaceMap 需要取代並取出的Map集
     * @return 重組轉換後的Json字串
     * @throws Exception
     */
    public String transferPosJson(String json , Map<String,Object> replaceMap) throws Exception{
        //System.out.println("ERP JSON : " + json);
        logger.info("\r\nERP JSON：  "+json+"\n");

        ParseJson pj=new ParseJson();

        Map<String,Object> jsonMap = pj.jsonToBean(json, new TypeToken<Map<String,Object>>(){});
        Map<String,Object> transJsonMap = transferPos(jsonMap, replaceMap);
        String newJson = pj.beanToJson(transJsonMap);
        pj=null;

        logger.info("\r\nPOS JSON : " + newJson);
        return newJson;
    }

    /**
     * 轉換Json function POS Msg -> T100 Msg
     * @param json Json字串
     * @return 重組轉換後的Json字串
     * @throws Exception
     */
    public String transferT100JsonMsg(String json) throws Exception{
        //System.out.println("POS MSG : " + json);
        logger.info("\r\nPOS MSG : " + json);
        ParseJson pj=new ParseJson();

        Map<String,Object> jsonMap = pj.jsonToBean(json, new TypeToken<Map<String,Object>>(){});
        Map<String,Object> transJsonMap = transferT100Msg(jsonMap);
        String newJson = pj.beanToJson(transJsonMap);
        pj=null;

        logger.info("\r\nT100 MSG : " + newJson);
        return newJson;
    }

    /**
     * 轉換成Pos端的Json格式
     * @param jsonMap T100的Json字串轉為Map
     * @param replaceMap 需要取代並取出的Map集
     * @return 重組轉換成Pos端格式的Map
     * @throws Exception
     */
    private Map<String,Object> transferPos(Map<String,Object> jsonMap,Map<String,Object> replaceMap) throws Exception{
        Map<String,Object> posJsonMap = new HashMap<String,Object>();
        //處理service名稱部分 替換Value
        if (jsonMap != null) {
            if(jsonMap.containsKey("service")){
                Map<String, Object> serviceMap = (Map<String, Object>) replaceMap.get("service");
                for(String key : serviceMap.keySet()){
                    posJsonMap.put(key, serviceMap.get(key));
                }
            }
            //處理token 取得T100的key
            if(jsonMap.containsKey("key")){
                posJsonMap.put("token", jsonMap.get("key"));
            }
            //處理企業編號
            if(jsonMap.containsKey("datakey")){
                Map<String, Object> datakeyMap = (Map<String, Object>) jsonMap.get("datakey");
                posJsonMap.put("eId", datakeyMap.get("EntId").toString());
                //posJsonMap.put("organizationNO", datakeyMap.get("CompanyId").toString());
            }

            //處理單頭
            //取得payload
            Map<String,Object> payloadMap = jsonMap.get("payload") != null ? (Map<String, Object>) jsonMap.get("payload") : null;
            Map<String,Object> stdDataMap = null;
            Map<String,Object> parameterMap = null;
            //格式防呆
            if(payloadMap == null){
                throw new Exception("Json格式錯誤，請檢查T100之Json格式");
            }else{
                //取得std_data
                stdDataMap = payloadMap.get("std_data") != null ? (Map<String, Object>) payloadMap.get("std_data") : null;
                if(stdDataMap == null){
                    throw new Exception("Json格式錯誤，請檢查T100之Json格式");
                }else{
                    //取得parameter
                    parameterMap = stdDataMap.get("parameter") != null ? (Map<String, Object>) stdDataMap.get("parameter") : null;
                    if(parameterMap == null){
                        throw new Exception("Json格式錯誤，請檢查T100之Json格式");
                    }
                }
            }

            //取得單頭List
            List<Object> receivingList = (List<Object>) parameterMap.get("receiving");
            if(receivingList != null && receivingList.size() > 0){
                for (Object object : receivingList) {
                    //單頭需取代的欄位
                    Map<String,Object> receivingReplaceMap = (Map<String, Object>) replaceMap.get("receiving");
                    //單頭Map
                    Map<String,Object> receivingMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String rcvKey : receivingReplaceMap.keySet()){
                        if(receivingMap.containsKey(rcvKey)){
                            posJsonMap.put(receivingReplaceMap.get(rcvKey).toString(), receivingMap.get(rcvKey));
                        }
                    }
                    //取得單身List
                    List<Object> receivingDetailList = (List<Object>) receivingMap.get("receiving_detail");
                    if(receivingDetailList != null && receivingDetailList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : receivingDetailList) {
                            //單身需取代的欄位
                            Map<String,Object> receivingDeatilReplaceMap = (Map<String, Object>) replaceMap.get("receiving_detail");
                            //單身Map
                            Map<String,Object> receivingDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : receivingDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(receivingDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(receivingDeatilReplaceMap.get(rcvdKey).toString(), receivingDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("datas", posDetailList);
                    }
                }
            }

            //region pos.sale.create
            //取得單頭List
            List<Object> salecreateList = (List<Object>) parameterMap.get("sale");
            if(salecreateList != null && salecreateList.size() > 0){
                //組合存放pos端Json的單身List
                List<Object> SaleHeaderDetailList = new ArrayList<Object>();
                for (Object object : salecreateList) {
                    //單頭需取代的欄位
                    Map<String,Object> salecreateReplaceMap = (Map<String, Object>) replaceMap.get("sale");
                    //單頭Map
                    Map<String,Object> salecreateMap = (Map<String, Object>) object;
                    Map<String,Object> headerDetailMap = new HashMap<String,Object>();
                    //比對是否有符合需取代的欄位
                    for(String rcvKey : salecreateReplaceMap.keySet()){
                        if(salecreateMap.containsKey(rcvKey)){
                            headerDetailMap.put(salecreateReplaceMap.get(rcvKey).toString(), salecreateMap.get(rcvKey));
                        }
                    }
                    //SaleHeaderDetailList.add(headerDetailMap);
                    posJsonMap.put("sale", headerDetailMap);

                    //region 取得單身List
                    List<Object> salegoodsDetailList = (List<Object>) salecreateMap.get("sale_goods_detail");
                    if(salegoodsDetailList != null && salegoodsDetailList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : salegoodsDetailList) {
                            //單身需取代的欄位
                            Map<String,Object> salegoodsDeatilReplaceMap = (Map<String, Object>) replaceMap.get("sale_goods_detail");
                            //單身Map
                            Map<String,Object> salegoodsDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : salegoodsDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(salegoodsDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(salegoodsDeatilReplaceMap.get(rcvdKey).toString(), salegoodsDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("sale_goods_detail", posDetailList);
                    }
                    //endregion

                    //region 取得單身List
                    List<Object> payDetailList = (List<Object>) salecreateMap.get("sale_pay_detail");
                    if(payDetailList != null && payDetailList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : payDetailList) {
                            //單身需取代的欄位
                            Map<String,Object> payDeatilReplaceMap = (Map<String, Object>) replaceMap.get("sale_pay_detail");
                            //單身Map
                            Map<String,Object> payDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : payDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(payDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(payDeatilReplaceMap.get(rcvdKey).toString(), payDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("sale_pay_detail", posDetailList);
                    }
                    //enregion
                }
            }
            //endregion

            //取得盘点单List
            List<Object> countingList = (List<Object>) parameterMap.get("counting");
            if(countingList != null && countingList.size() > 0){
                for (Object object : countingList) {
                    //單頭需取代的欄位
                    Map<String,Object> countingReplaceMap = (Map<String, Object>) replaceMap.get("counting");
                    //單頭Map
                    Map<String,Object> countingMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String rcvKey : countingReplaceMap.keySet()){
                        if(countingMap.containsKey(rcvKey)){
                            posJsonMap.put(countingReplaceMap.get(rcvKey).toString(), countingMap.get(rcvKey));
                        }
                    }
                    //取得單身List
                    List<Object> counting_shopList = (List<Object>) countingMap.get("counting_shop");
                    if(counting_shopList != null && counting_shopList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : counting_shopList) {
                            //單身需取代的欄位
                            Map<String,Object> countingDeatilReplaceMap = (Map<String, Object>) replaceMap.get("counting_shop");
                            //單身Map
                            Map<String,Object> countingDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : countingDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(countingDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(countingDeatilReplaceMap.get(rcvdKey).toString(), countingDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("stocktaskshop", posDetailList);
                    }

                    //取得單身List
                    List<Object> counting_rangeList = (List<Object>) countingMap.get("counting_range");
                    if(counting_rangeList != null && counting_rangeList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : counting_rangeList) {
                            //單身需取代的欄位
                            Map<String,Object> countingDeatilReplaceMap = (Map<String, Object>) replaceMap.get("counting_range");
                            //單身Map
                            Map<String,Object> countingDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : countingDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(countingDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(countingDeatilReplaceMap.get(rcvdKey).toString(), countingDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("stocktaskrange", posDetailList);
                    }

                    //取得單身List
                    List<Object> counting_detailList = (List<Object>) countingMap.get("counting_detail");
                    if(counting_detailList != null && counting_detailList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : counting_detailList) {
                            //單身需取代的欄位
                            Map<String,Object> countingDeatilReplaceMap = (Map<String, Object>) replaceMap.get("counting_detail");
                            //單身Map
                            Map<String,Object> countingDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : countingDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(countingDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(countingDeatilReplaceMap.get(rcvdKey).toString(), countingDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("stocktasklist", posDetailList);
                    }
                }
            }

            //取得退货單
            List<Object> returnList = (List<Object>) parameterMap.get("return");
            if(returnList != null && returnList.size() > 0){
                for (Object object : returnList) {
                    //單頭需取代的欄位
                    Map<String,Object> returnReplaceMap = (Map<String, Object>) replaceMap.get("return");
                    //單頭Map
                    Map<String,Object> returnMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String skey : returnMap.keySet())
                    {
                        if(returnReplaceMap.containsKey(skey))
                        {
                            posJsonMap.put(returnReplaceMap.get(skey).toString(), returnMap.get(skey));
                        }
                        else
                        {
                            posJsonMap.put(skey, returnMap.get(skey));
                        }
                    }
                }
            }

            //取得日结單頭List
            List<Object> dayendshopsList = (List<Object>) parameterMap.get("day_end");
            if(dayendshopsList != null && dayendshopsList.size() > 0){
                for (Object object : dayendshopsList) {
                    //單頭需取代的欄位
                    Map<String,Object> dayendshopsReplaceMap = (Map<String, Object>) replaceMap.get("day_end");
                    //單頭Map
                    Map<String,Object> dayendshopsMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String rcvKey : dayendshopsReplaceMap.keySet()){
                        if(dayendshopsMap.containsKey(rcvKey)){
                            posJsonMap.put(dayendshopsReplaceMap.get(rcvKey).toString(), dayendshopsMap.get(rcvKey));
                        }
                    }
                    //取得單身List
                    List<Object> dayendshopsDetailList = (List<Object>) dayendshopsMap.get("day_end_shops");
                    if(dayendshopsDetailList != null && dayendshopsDetailList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : dayendshopsDetailList) {
                            //單身需取代的欄位
                            Map<String,Object> dayendshopsDeatilReplaceMap = (Map<String, Object>) replaceMap.get("day_end_shops");
                            //單身Map
                            Map<String,Object> dayendshopsDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : dayendshopsDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(dayendshopsDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(dayendshopsDeatilReplaceMap.get(rcvdKey).toString(), dayendshopsDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("dayEndShops", posDetailList);
                    }
                }
            }

            //库存调整新增
            List<Object> adjustCreateList = (List<Object>) parameterMap.get("adjust");
            if(adjustCreateList != null && adjustCreateList.size()>0) {
                //循环T100单头
                for (Object object : adjustCreateList)
                {
                    //门店管理單頭需取代的欄位
                    Map<String,Object> adjustReplaceMap = (Map<String, Object>) replaceMap.get("adjust");
                    //T100单头内容
                    Map<String,Object> adjustMap = (Map<String, Object>) object;
                    for (String adjustKey : adjustReplaceMap.keySet())
                    {
                        if(adjustMap.containsKey(adjustKey))
                        {
                            posJsonMap.put(adjustReplaceMap.get(adjustKey).toString(), adjustMap.get(adjustKey));
                        }
                    }

                    //单身明细信息
                    List<Object> adjustDetailList = (List<Object>)adjustMap.get("adjust_detail");
                    if(adjustDetailList!=null && adjustDetailList.size()>0)
                    {
                        //組合存放POS端JSON的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : adjustDetailList)
                        {
                            //單身需取代的欄位
                            Map<String,Object> adjustDeatilReplaceMap = (Map<String, Object>) replaceMap.get("adjust_detail");
                            //單身Map
                            Map<String,Object> adjustDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放POS端JSON的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String adjustKey : adjustDeatilReplaceMap.keySet())
                            {
                                //比對是否有符合需取代的欄位
                                if(adjustDetailMap.containsKey(adjustKey))
                                {
                                    posDetailMap.put(adjustDeatilReplaceMap.get(adjustKey).toString(), adjustDetailMap.get(adjustKey));
                                }
                            }
                            posDetailList.add(posDetailMap);

                        }
                        posJsonMap.put("datas", posDetailList);
                    }
                }
            }

            //门店上传异常查询
            List<Object> conditionList = (List<Object>) parameterMap.get("get_condition");
            if(conditionList != null && conditionList.size() > 0){
                for (Object object : conditionList) {
                    //單頭需取代的欄位
                    Map<String,Object> conditionReplaceMap = (Map<String, Object>) replaceMap.get("get_condition");
                    //單頭Map
                    Map<String,Object> conditionMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String rcvKey : conditionReplaceMap.keySet()){
                        if(conditionMap.containsKey(rcvKey)){
                            posJsonMap.put(conditionReplaceMap.get(rcvKey).toString(), conditionMap.get(rcvKey));
                        }
                    }
                    //取得單身List
                    List<Object> datesList = (List<Object>) conditionMap.get("dates");
                    if(datesList != null && datesList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : datesList) {
                            //單身需取代的欄位
                            Map<String,Object> conditionDeatilReplaceMap = (Map<String, Object>) replaceMap.get("dates");
                            //單身Map
                            Map<String,Object> conditionDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : conditionDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(conditionDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(conditionDeatilReplaceMap.get(rcvdKey).toString(), conditionDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("dates", posDetailList);
                    }

                    //取得單身List
                    List<Object> shopsList = (List<Object>) conditionMap.get("shops");
                    if(shopsList != null && shopsList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : shopsList) {
                            //單身需取代的欄位
                            Map<String,Object> conditionDeatilReplaceMap = (Map<String, Object>) replaceMap.get("shops");
                            //單身Map
                            Map<String,Object> conditionDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : conditionDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(conditionDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(conditionDeatilReplaceMap.get(rcvdKey).toString(), conditionDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("shops", posDetailList);
                    }

                    //取得單身List
                    List<Object> doctypesList = (List<Object>) conditionMap.get("doc_types");
                    if(doctypesList != null && doctypesList.size() > 0){
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : doctypesList) {
                            //單身需取代的欄位
                            Map<String,Object> doctypesDeatilReplaceMap = (Map<String, Object>) replaceMap.get("doc_types");
                            //單身Map
                            Map<String,Object> doctypesDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : doctypesDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(doctypesDetailMap.containsKey(rcvdKey)){
                                    posDetailMap.put(doctypesDeatilReplaceMap.get(rcvdKey).toString(), doctypesDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("docTypes", posDetailList);
                    }
                }
            }

            //报废单更新
            List<Object> scrapUpdateList = (List<Object>) parameterMap.get("scrap");
            if(scrapUpdateList != null && scrapUpdateList.size()>0) {
                //循环T100单头
                for (Object object : scrapUpdateList)
                {
                    //门店管理單頭需取代的欄位
                    Map<String,Object> scrapReplaceMap = (Map<String, Object>) replaceMap.get("scrap");
                    //T100单头内容
                    Map<String,Object> scrapMap = (Map<String, Object>) object;
                    for (String scrapKey : scrapReplaceMap.keySet())
                    {
                        if(scrapMap.containsKey(scrapKey))
                        {
                            posJsonMap.put(scrapReplaceMap.get(scrapKey).toString(), scrapMap.get(scrapKey));
                        }
                    }

                    //单身明细信息
                    List<Object> scrapDetailList = (List<Object>)scrapMap.get("scrap_detail");
                    if(scrapDetailList!=null && scrapDetailList.size()>0)
                    {
                        //組合存放POS端JSON的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : scrapDetailList)
                        {
                            //單身需取代的欄位
                            Map<String,Object> scrapDeatilReplaceMap = (Map<String, Object>) replaceMap.get("scrap_detail");
                            //單身Map
                            Map<String,Object> scrapDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放POS端JSON的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String scrapKey : scrapDeatilReplaceMap.keySet())
                            {
                                //比對是否有符合需取代的欄位
                                if(scrapDetailMap.containsKey(scrapKey))
                                {
                                    posDetailMap.put(scrapDeatilReplaceMap.get(scrapKey).toString(), scrapDetailMap.get(scrapKey));
                                }
                            }
                            posDetailList.add(posDetailMap);

                        }
                        posJsonMap.put("datas", posDetailList);
                    }
                }
            }

            //调拨通知单新增
            List<Object> transferCreateList = (List<Object>) parameterMap.get("transfer");
            if(transferCreateList != null && transferCreateList.size()>0) {
                //循环T100单头
                for (Object object : transferCreateList)
                {
                    //门店管理單頭需取代的欄位
                    Map<String,Object> transferReplaceMap = (Map<String, Object>) replaceMap.get("transfer");
                    //T100单头内容
                    Map<String,Object> transferMap = (Map<String, Object>) object;
                    for (String transferKey : transferReplaceMap.keySet())
                    {
                        if(transferMap.containsKey(transferKey))
                        {
                            posJsonMap.put(transferReplaceMap.get(transferKey).toString(), transferMap.get(transferKey));
                        }
                    }

                    //单身明细信息
                    List<Object> transferDetailList = (List<Object>)transferMap.get("transfer_detail");
                    if(transferDetailList != null && transferDetailList.size() > 0)
                    {
                        //組合存放POS端JSON的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : transferDetailList)
                        {
                            //單身需取代的欄位
                            Map<String,Object> transferDeatilReplaceMap = (Map<String, Object>) replaceMap.get("transfer_detail");
                            //單身Map
                            Map<String,Object> transferDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放POS端JSON的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String transferKey : transferDeatilReplaceMap.keySet())
                            {
                                //比對是否有符合需取代的欄位
                                if(transferDetailMap.containsKey(transferKey))
                                {
                                    posDetailMap.put(transferDeatilReplaceMap.get(transferKey).toString(), transferDetailMap.get(transferKey));
                                }
                            }
                            posDetailList.add(posDetailMap);

                        }
                        posJsonMap.put("datas", posDetailList);
                    }
                }
            }

            //取得退货單
            List<Object> orgorderList = (List<Object>) parameterMap.get("orgorder");
            if(orgorderList != null && orgorderList.size() > 0){
                for (Object object : orgorderList) {
                    //單頭需取代的欄位
                    Map<String,Object> orgorderMap = (Map<String, Object>) replaceMap.get("orgorder");
                    //單頭Map
                    Map<String,Object> returnMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String skey : returnMap.keySet())
                    {
                        posJsonMap.put(skey, returnMap.get(skey));
                        //						if(orgorderMap.containsKey(skey))
                        //						{
                        //							posJsonMap.put(orgorderMap.get(skey).toString(), returnMap.get(skey));
                        //						}
                        //						else
                        //						{
                        //							posJsonMap.put(skey, returnMap.get(skey));
                        //						}
                    }
                }
            }

            //取得驳回单
            List<Object>  rejectList = (List<Object>) parameterMap.get("reject");
            if(rejectList != null && rejectList.size() > 0){
                for (Object object : rejectList) {
                    //單頭需取代的欄位
                    Map<String,Object> rejectReplaceMap = (Map<String, Object>) replaceMap.get("reject");
                    //單頭Map
                    Map<String,Object> rejectMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String skey : rejectMap.keySet())
                    {
                        if(rejectReplaceMap.containsKey(skey))
                        {
                            posJsonMap.put(rejectReplaceMap.get(skey).toString(), rejectMap.get(skey));
                        }
                        else
                        {
                            posJsonMap.put(skey, rejectMap.get(skey));
                        }
                    }

                    //单身明细信息
                    List<Object> rejectDetailList = (List<Object>)rejectMap.get("reject_detail");
                    if(rejectDetailList != null && rejectDetailList.size() > 0)
                    {
                        //組合存放POS端JSON的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : rejectDetailList)
                        {
                            //單身需取代的欄位
                            Map<String,Object> rejectDeatilReplaceMap = (Map<String, Object>) replaceMap.get("reject_detail");
                            //單身Map
                            Map<String,Object> rejectDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放POS端JSON的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String transferKey : rejectDeatilReplaceMap.keySet())
                            {
                                //比對是否有符合需取代的欄位
                                if(rejectDetailMap.containsKey(transferKey))
                                {
                                    posDetailMap.put(rejectDeatilReplaceMap.get(transferKey).toString(), rejectDetailMap.get(transferKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("datas", posDetailList);
                    }
                }
            }

            //取得撤销单
            List<Object>  undoList = (List<Object>) parameterMap.get("undo");
            if(undoList != null && undoList.size() > 0){
                for (Object object : undoList) {
                    //單頭需取代的欄位
                    Map<String,Object> undoReplaceMap = (Map<String, Object>) replaceMap.get("undo");
                    //單頭Map
                    Map<String,Object> undoMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String skey : undoMap.keySet())
                    {
                        if(undoReplaceMap.containsKey(skey))
                        {
                            posJsonMap.put(undoReplaceMap.get(skey).toString(), undoMap.get(skey));
                        }
                        else
                        {
                            posJsonMap.put(skey, undoMap.get(skey));
                        }
                    }

                    //单身明细信息
                    List<Object> undoDetailList = (List<Object>)undoMap.get("undo_detail");
                    if(undoDetailList != null && undoDetailList.size() > 0)
                    {
                        //組合存放POS端JSON的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : undoDetailList)
                        {
                            //單身需取代的欄位
                            Map<String,Object> undoDeatilReplaceMap = (Map<String, Object>) replaceMap.get("undo_detail");
                            //單身Map
                            Map<String,Object> undoDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放POS端JSON的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String transferKey : undoDeatilReplaceMap.keySet())
                            {
                                //比對是否有符合需取代的欄位
                                if(undoDetailMap.containsKey(transferKey))
                                {
                                    posDetailMap.put(undoDeatilReplaceMap.get(transferKey).toString(), undoDetailMap.get(transferKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("datas", posDetailList);
                    }
                }
            }

            //取得订单状态更新
            List<Object>  orderStatusUpdateList = (List<Object>) parameterMap.get("request");
            if(orderStatusUpdateList != null && orderStatusUpdateList.size() > 0){
                for (Object object : orderStatusUpdateList) {
                    //單頭需取代的欄位
                    Map<String,Object> requestReplaceMap = (Map<String, Object>) replaceMap.get("request");
                    //單頭Map
                    Map<String,Object> requestMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String skey : requestMap.keySet())
                    {
                        if(requestReplaceMap.containsKey(skey))
                        {
                            posJsonMap.put(requestReplaceMap.get(skey).toString(), requestMap.get(skey));
                        }
                        else
                        {
                            posJsonMap.put(skey, requestMap.get(skey));
                        }
                    }
                }
            }

            //取得單頭List
            List<Object> requisitionUpdateList = (List<Object>) parameterMap.get("requisitionupdate");
            if(requisitionUpdateList != null && requisitionUpdateList.size() > 0) {
                for (Object object : requisitionUpdateList)
                {
                    //單頭需取代的欄位
                    Map<String,Object> requisitionReplaceMap = (Map<String, Object>) replaceMap.get("requisitionupdate");
                    //單頭Map
                    Map<String,Object> requisitionMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String rcvKey : requisitionReplaceMap.keySet()){
                        if(requisitionMap.containsKey(rcvKey)){
                            posJsonMap.put(requisitionReplaceMap.get(rcvKey).toString(), requisitionMap.get(rcvKey));
                        }
                    }
                    //取得單身List
                    List<Object> requisitionDetailList = (List<Object>) requisitionMap.get("requisitionupdate_detail");
                    if(requisitionDetailList != null && requisitionDetailList.size() > 0)
                    {
                        //組合存放pos端Json的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : requisitionDetailList)
                        {
                            //單身需取代的欄位
                            Map<String,Object> requisitionDeatilReplaceMap = (Map<String, Object>) replaceMap.get("requisitionupdate_detail");
                            //單身Map
                            Map<String,Object> requisitionDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放pos端Json的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String rcvdKey : requisitionDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(requisitionDetailMap.containsKey(rcvdKey))
                                {
                                    posDetailMap.put(requisitionDeatilReplaceMap.get(rcvdKey).toString(), requisitionDetailMap.get(rcvdKey));
                                }
                            }
                            posDetailList.add(posDetailMap);
                        }
                        posJsonMap.put("datas", posDetailList);
                    }
                }
            }

            //取得單頭List
            List<Object> retransList = (List<Object>) parameterMap.get("retrans");
            if(retransList != null && retransList.size() > 0){
                for (Object object : retransList) {
                    //單頭需取代的欄位
                    Map<String,Object> retransReplaceMap = (Map<String, Object>) replaceMap.get("retrans");
                    //單頭Map
                    Map<String,Object> retransMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String rcvKey : retransReplaceMap.keySet()){
                        if(retransReplaceMap.containsKey(rcvKey)){
                            posJsonMap.put(retransReplaceMap.get(rcvKey).toString(), retransMap.get(rcvKey));
                        }
                    }
                }
            }

            //取得單頭List
            List<Object>feeUpdateList = (List<Object>) parameterMap.get("feeupdate");
            if(feeUpdateList != null && feeUpdateList.size() > 0){
                for (Object object : feeUpdateList) {
                    //單頭需取代的欄位
                    Map<String,Object> feeUpdateReplaceMap = (Map<String, Object>) replaceMap.get("feeupdate");
                    //單頭Map
                    Map<String,Object> feeUpdateMap = (Map<String, Object>) object;
                    //比對是否有符合需取代的欄位
                    for(String rcvKey : feeUpdateReplaceMap.keySet()){
                        if(feeUpdateReplaceMap.containsKey(rcvKey)){
                            posJsonMap.put(feeUpdateReplaceMap.get(rcvKey).toString(), feeUpdateMap.get(rcvKey));
                        }
                    }
                }
            }

            //门店要货单新增
            List<Object>requisitionCreateList = (List<Object>) parameterMap.get("requisitionCreate");
            if(requisitionCreateList != null && requisitionCreateList.size()>0) {
                //循环T100单头
                for (Object object : requisitionCreateList) {
                    //门店管理單頭需取代的欄位
                    Map<String,Object> requisitionReplaceMap = (Map<String, Object>) replaceMap.get("requisitionCreate");
                    //T100单头内容
                    Map<String,Object> requisitionMap = (Map<String, Object>) object;
                    for (String requisitionKey : requisitionReplaceMap.keySet()) {
                        if(requisitionMap.containsKey(requisitionKey)) {
                            posJsonMap.put(requisitionReplaceMap.get(requisitionKey).toString(), requisitionMap.get(requisitionKey));
                        }
                    }
                    //单身明细信息
                    List<Object> requisitionDetailList = (List<Object>)requisitionMap.get("requisitionCreate_detail");
                    if(requisitionDetailList != null && requisitionDetailList.size() > 0){
                        //組合存放POS端JSON的單身List
                        List<Object> posDetailList = new ArrayList<Object>();
                        for (Object objectDetail : requisitionDetailList) {
                            //單身需取代的欄位
                            Map<String,Object> requisitionDeatilReplaceMap = (Map<String, Object>) replaceMap.get("requisitionCreate_detail");
                            //單身Map
                            Map<String,Object> requisitionDetailMap = (Map<String, Object>) objectDetail;
                            //組合存放POS端JSON的單身Map
                            Map<String,Object> posDetailMap = new HashMap<String,Object>();
                            for(String requisitionKey : requisitionDeatilReplaceMap.keySet()){
                                //比對是否有符合需取代的欄位
                                if(requisitionDetailMap.containsKey(requisitionKey)) {
                                    posDetailMap.put(requisitionDeatilReplaceMap.get(requisitionKey).toString(), requisitionDetailMap.get(requisitionKey));
                                }
                            }
                            posDetailList.add(posDetailMap);

                        }
                        posJsonMap.put("datas", posDetailList);
                    }
                }
            }

            //门店要货单结案
            List<Object>requisitionEcsflgList = (List<Object>) parameterMap.get("requisitionClose");
            if(requisitionEcsflgList != null && requisitionEcsflgList.size()>0) {
                //循环T100单头
                for (Object object : requisitionEcsflgList) {
                    //门店管理單頭需取代的欄位
                    Map<String,Object> requisitionEcsflgReplaceMap = (Map<String, Object>) replaceMap.get("requisitionClose");
                    //T100单头内容
                    Map<String,Object> requisitionEcsflgMap = (Map<String, Object>) object;
                    for (String requisitionEcsflgKey : requisitionEcsflgReplaceMap.keySet()) {
                        if(requisitionEcsflgMap.containsKey(requisitionEcsflgKey)) {
                            posJsonMap.put(requisitionEcsflgReplaceMap.get(requisitionEcsflgKey).toString(), requisitionEcsflgMap.get(requisitionEcsflgKey));
                        }
                    }
                }
            }



            //MES工单新增--单头目前是对象类型，不是数组
            Map<String,Object> mes_MoCreateReplaceMap = (Map<String, Object>) replaceMap.get("mesmo");
            if (mes_MoCreateReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_MoCreateMap = (Map<String, Object>) parameterMap.get("mesmo");
                for (String requisitionKey : mes_MoCreateReplaceMap.keySet()) {
                    if(mes_MoCreateMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_MoCreateReplaceMap.get(requisitionKey).toString(), mes_MoCreateMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_MoCreateDetailList = (List<Object>)mes_MoCreateMap.get("mesmo_detail");
                if(mes_MoCreateDetailList != null && mes_MoCreateDetailList.size() > 0){
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_MoCreateDetailList) {
                        //單身需取代的欄位
                        Map<String,Object> mes_MoCreateDeatilReplaceMap = (Map<String, Object>) replaceMap.get("mesmo_detail");
                        //單身Map
                        Map<String,Object> mes_MoCreateDetailMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_MoCreateDeatilReplaceMap.keySet()){
                            //比對是否有符合需取代的欄位
                            if(mes_MoCreateDetailMap.containsKey(requisitionKey)) {
                                posDetailMap.put(mes_MoCreateDeatilReplaceMap.get(requisitionKey).toString(), mes_MoCreateDetailMap.get(requisitionKey));
                            }
                        }
                        posDetailList.add(posDetailMap);

                    }
                    posRequestMap.put("detailDatas", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }


            //MES erp下发盘点单--单头目前是对象类型，不是数组
            Map<String,Object> mes_erpStockTakeReplaceMap = (Map<String, Object>) replaceMap.get("messtocktake");
            if (mes_erpStockTakeReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_erpStockTakeMap = (Map<String, Object>) parameterMap.get("messtocktake");
                for (String requisitionKey : mes_erpStockTakeReplaceMap.keySet())
                {
                    if(mes_erpStockTakeMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_erpStockTakeReplaceMap.get(requisitionKey).toString(), mes_erpStockTakeMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_erpStockTakeDetailList = (List<Object>)mes_erpStockTakeMap.get("messtocktake_detail");
                if(mes_erpStockTakeDetailList != null && mes_erpStockTakeDetailList.size() > 0)
                {
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_erpStockTakeDetailList)
                    {
                        //單身需取代的欄位
                        Map<String,Object> mes_erpStockTakeDeatilReplaceMap = (Map<String, Object>) replaceMap.get("messtocktake_detail");
                        //單身Map
                        Map<String,Object> mes_erpStockTakeDetailMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_erpStockTakeDeatilReplaceMap.keySet())
                        {
                            //比對是否有符合需取代的欄位
                            if(mes_erpStockTakeDetailMap.containsKey(requisitionKey))
                            {
                                posDetailMap.put(mes_erpStockTakeDeatilReplaceMap.get(requisitionKey).toString(), mes_erpStockTakeDetailMap.get(requisitionKey));
                            }
                        }


                        //組合存放POS端JSON的單身List
                        List<Object> messtocktakeunitList = new ArrayList<Object>();
                        //第三层
                        List<Object> messtocktakeunit_detailList = (List<Object>) mes_erpStockTakeDetailMap.get("messtocktakeunit_detail");

                        if (messtocktakeunit_detailList != null)
                        {
                            for (Object objectThird : messtocktakeunit_detailList)
                            {
                                //第三层，單身需取代的欄位
                                Map<String,Object> messtocktakeunitReplaceMap = (Map<String, Object>) replaceMap.get("messtocktakeunit_detail");

                                //單身Map
                                Map<String,Object> messtocktakeunitMap = (Map<String, Object>) objectThird;

                                for (String thirdKey : messtocktakeunitReplaceMap.keySet())
                                {
                                    //比對是否有符合需取代的欄位
                                    if(messtocktakeunitMap.containsKey(thirdKey))
                                    {
                                        messtocktakeunitMap.put(messtocktakeunitReplaceMap.get(thirdKey).toString(), messtocktakeunitMap.get(thirdKey));
                                    }
                                }

                                //
                                messtocktakeunitList.add(messtocktakeunitMap);
                            }
                        }

                        //
                        posDetailMap.put("unitList",messtocktakeunitList);

                        posDetailList.add(posDetailMap);

                    }

                    posRequestMap.put("detailList", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }




            //MES 分拣底稿数据创建--单头目前是对象类型，不是数组
            Map<String,Object> mes_sortdataReplaceMap = (Map<String, Object>) replaceMap.get("messortdata");
            if (mes_sortdataReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_sortdataMap = (Map<String, Object>) parameterMap.get("messortdata");
                for (String requisitionKey : mes_sortdataReplaceMap.keySet()) {
                    if(mes_sortdataMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_sortdataReplaceMap.get(requisitionKey).toString(), mes_sortdataMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_sortdataDetailList = (List<Object>)mes_sortdataMap.get("messortdata_detail");
                if(mes_sortdataDetailList != null && mes_sortdataDetailList.size() > 0){
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_sortdataDetailList) {
                        //單身需取代的欄位
                        Map<String,Object> mes_sortdataDeatilReplaceMap = (Map<String, Object>) replaceMap.get("messortdata_detail");
                        //單身Map
                        Map<String,Object> mes_sortdataDetailMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_sortdataDeatilReplaceMap.keySet()){
                            //比對是否有符合需取代的欄位
                            if(mes_sortdataDetailMap.containsKey(requisitionKey)) {
                                posDetailMap.put(mes_sortdataDeatilReplaceMap.get(requisitionKey).toString(), mes_sortdataDetailMap.get(requisitionKey));
                            }
                        }
                        posDetailList.add(posDetailMap);

                    }
                    posRequestMap.put("dataList", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }



            //MES采购计划单创建--单头目前是对象类型，不是数组
            Map<String,Object> mes_receiveReplaceMap = (Map<String, Object>) replaceMap.get("mesreceiving");
            if (mes_receiveReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_receiveMap = (Map<String, Object>) parameterMap.get("mesreceiving");
                for (String requisitionKey : mes_receiveReplaceMap.keySet()) {
                    if(mes_receiveMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_receiveReplaceMap.get(requisitionKey).toString(), mes_receiveMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_receiveDetailList = (List<Object>)mes_receiveMap.get("mesreceiving_detail");
                if(mes_receiveDetailList != null && mes_receiveDetailList.size() > 0){
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_receiveDetailList) {
                        //單身需取代的欄位
                        Map<String,Object> mes_receiveDeatilReplaceMap = (Map<String, Object>) replaceMap.get("mesreceiving_detail");
                        //單身Map
                        Map<String,Object> mes_receiveDetailMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_receiveDeatilReplaceMap.keySet()){
                            //比對是否有符合需取代的欄位
                            if(mes_receiveDetailMap.containsKey(requisitionKey)) {
                                posDetailMap.put(mes_receiveDeatilReplaceMap.get(requisitionKey).toString(), mes_receiveDetailMap.get(requisitionKey));
                            }
                        }
                        posDetailList.add(posDetailMap);

                    }
                    posRequestMap.put("datas", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }


            //MES销售订单新增--单头目前是对象类型，不是数组
            Map<String,Object> mes_SalesOrderCreateReplaceMap = (Map<String, Object>) replaceMap.get("mesSalesOrder");
            if (mes_SalesOrderCreateReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_SalesOrderCreateMap = (Map<String, Object>) parameterMap.get("mesSalesOrder");
                for (String requisitionKey : mes_SalesOrderCreateReplaceMap.keySet()) {
                    if(mes_SalesOrderCreateMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_SalesOrderCreateReplaceMap.get(requisitionKey).toString(), mes_SalesOrderCreateMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_SalesOrderCreateDetailList = (List<Object>)mes_SalesOrderCreateMap.get("mesSalesOrder_detail");
                if(mes_SalesOrderCreateDetailList != null && mes_SalesOrderCreateDetailList.size() > 0){
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_SalesOrderCreateDetailList) {
                        //單身需取代的欄位
                        Map<String,Object> mes_SalesOrderCreateDeatilReplaceMap = (Map<String, Object>) replaceMap.get("mesSalesOrder_detail");
                        //單身Map
                        Map<String,Object> mes_SalesOrderCreateDetailMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_SalesOrderCreateDeatilReplaceMap.keySet()){
                            //比對是否有符合需取代的欄位
                            if(mes_SalesOrderCreateDetailMap.containsKey(requisitionKey)) {
                                posDetailMap.put(mes_SalesOrderCreateDeatilReplaceMap.get(requisitionKey).toString(), mes_SalesOrderCreateDetailMap.get(requisitionKey));
                            }
                        }
                        posDetailList.add(posDetailMap);

                    }
                    posRequestMap.put("orderDetailList", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }


            //MES销售订单新增--单头目前是对象类型，不是数组
            Map<String,Object> mes_StockOutApplicationCreateReplaceMap = (Map<String, Object>) replaceMap.get("mesStockOutApplication");
            if (mes_StockOutApplicationCreateReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_StockOutApplicationCreateMap = (Map<String, Object>) parameterMap.get("mesStockOutApplication");
                for (String requisitionKey : mes_StockOutApplicationCreateReplaceMap.keySet()) {
                    if(mes_StockOutApplicationCreateMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_StockOutApplicationCreateReplaceMap.get(requisitionKey).toString(), mes_StockOutApplicationCreateMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_StockOutApplicationCreateDetailList = (List<Object>)mes_StockOutApplicationCreateMap.get("mesStockOutApplication_detail");
                if(mes_StockOutApplicationCreateDetailList != null && mes_StockOutApplicationCreateDetailList.size() > 0){
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_StockOutApplicationCreateDetailList) {
                        //單身需取代的欄位
                        Map<String,Object> mes_StockOutApplicationCreateDeatilReplaceMap = (Map<String, Object>) replaceMap.get("mesStockOutApplication_detail");
                        //單身Map
                        Map<String,Object> mes_StockOutApplicationCreateDeatilMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_StockOutApplicationCreateDeatilReplaceMap.keySet()){
                            //比對是否有符合需取代的欄位
                            if(mes_StockOutApplicationCreateDeatilMap.containsKey(requisitionKey)) {
                                posDetailMap.put(mes_StockOutApplicationCreateDeatilReplaceMap.get(requisitionKey).toString(), mes_StockOutApplicationCreateDeatilMap.get(requisitionKey));
                            }
                        }
                        posDetailList.add(posDetailMap);

                    }
                    posRequestMap.put("detailList", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }


            //MES销退单创建--单头目前是对象类型，不是数组
            Map<String,Object> mes_SalesReturnCreateReplaceMap = (Map<String, Object>) replaceMap.get("mesSalesReturn");
            if (mes_SalesReturnCreateReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_SalesReturnCreateMap = (Map<String, Object>) parameterMap.get("mesSalesReturn");
                for (String requisitionKey : mes_SalesReturnCreateReplaceMap.keySet()) {
                    if(mes_SalesReturnCreateMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_SalesReturnCreateReplaceMap.get(requisitionKey).toString(), mes_SalesReturnCreateMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_SalesReturnCreateDetailList = (List<Object>)mes_SalesReturnCreateMap.get("mesSalesReturn_detail");
                if(mes_SalesReturnCreateDetailList != null && mes_SalesReturnCreateDetailList.size() > 0){
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_SalesReturnCreateDetailList) {
                        //單身需取代的欄位
                        Map<String,Object> mes_SalesReturnCreateDeatilReplaceMap = (Map<String, Object>) replaceMap.get("mesSalesReturn_detail");
                        //單身Map
                        Map<String,Object> mes_SalesReturnCreateDeatilMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_SalesReturnCreateDeatilReplaceMap.keySet()){
                            //比對是否有符合需取代的欄位
                            if(mes_SalesReturnCreateDeatilMap.containsKey(requisitionKey)) {
                                posDetailMap.put(mes_SalesReturnCreateDeatilReplaceMap.get(requisitionKey).toString(), mes_SalesReturnCreateDeatilMap.get(requisitionKey));
                            }
                        }
                        posDetailList.add(posDetailMap);

                    }
                    posRequestMap.put("detailList", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }



            //MES组合拆解单创建--单头目前是对象类型，不是数组
            Map<String,Object> mes_ComposeDisCreateReplaceMap = (Map<String, Object>) replaceMap.get("mesPurchaseReturn");
            if (mes_ComposeDisCreateReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_ComposeDisCreateMap = (Map<String, Object>) parameterMap.get("mesPurchaseReturn");
                for (String requisitionKey : mes_ComposeDisCreateReplaceMap.keySet()) {
                    if(mes_ComposeDisCreateMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_ComposeDisCreateReplaceMap.get(requisitionKey).toString(), mes_ComposeDisCreateMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_ComposeDisCreateDetailList = (List<Object>)mes_ComposeDisCreateMap.get("mesPurchaseReturn_detail");
                if(mes_ComposeDisCreateDetailList != null && mes_ComposeDisCreateDetailList.size() > 0){
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_ComposeDisCreateDetailList) {
                        //單身需取代的欄位
                        Map<String,Object> mes_ComposeDisCreateDeatilReplaceMap = (Map<String, Object>) replaceMap.get("mesPurchaseReturn_detail");
                        //單身Map
                        Map<String,Object> mes_ComposeDisCreateDeatilMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_ComposeDisCreateDeatilReplaceMap.keySet()){
                            //比對是否有符合需取代的欄位
                            if(mes_ComposeDisCreateDeatilMap.containsKey(requisitionKey)) {
                                posDetailMap.put(mes_ComposeDisCreateDeatilReplaceMap.get(requisitionKey).toString(), mes_ComposeDisCreateDeatilMap.get(requisitionKey));
                            }
                        }
                        posDetailList.add(posDetailMap);

                    }
                    posRequestMap.put("detailList", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }


            //MES采退单创建--单头目前是对象类型，不是数组
            Map<String,Object> mes_PurchaseReturnCreateReplaceMap = (Map<String, Object>) replaceMap.get("mesComposeDis");
            if (mes_PurchaseReturnCreateReplaceMap != null)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                //T100单头内容
                Map<String,Object> mes_PurchaseReturnCreateMap = (Map<String, Object>) parameterMap.get("mesComposeDis");
                for (String requisitionKey : mes_PurchaseReturnCreateReplaceMap.keySet()) {
                    if(mes_PurchaseReturnCreateMap.containsKey(requisitionKey))
                    {
                        posRequestMap.put(mes_PurchaseReturnCreateReplaceMap.get(requisitionKey).toString(), mes_PurchaseReturnCreateMap.get(requisitionKey));
                    }
                }
                //单身明细信息
                List<Object> mes_PurchaseReturnCreateDetailList = (List<Object>)mes_PurchaseReturnCreateMap.get("mesComposeDis_detail");
                if(mes_PurchaseReturnCreateDetailList != null && mes_PurchaseReturnCreateDetailList.size() > 0){
                    //組合存放POS端JSON的單身List
                    List<Object> posDetailList = new ArrayList<Object>();
                    for (Object objectDetail : mes_PurchaseReturnCreateDetailList) {
                        //單身需取代的欄位
                        Map<String,Object> mes_PurchaseReturnCreateDeatilReplaceMap = (Map<String, Object>) replaceMap.get("mesComposeDis_detail");
                        //單身Map
                        Map<String,Object> mes_PurchaseReturnCreateDeatilMap = (Map<String, Object>) objectDetail;
                        //組合存放POS端JSON的單身Map
                        Map<String,Object> posDetailMap = new HashMap<String,Object>();
                        for(String requisitionKey : mes_PurchaseReturnCreateDeatilReplaceMap.keySet()){
                            //比對是否有符合需取代的欄位
                            if(mes_PurchaseReturnCreateDeatilMap.containsKey(requisitionKey)) {
                                posDetailMap.put(mes_PurchaseReturnCreateDeatilReplaceMap.get(requisitionKey).toString(), mes_PurchaseReturnCreateDeatilMap.get(requisitionKey));
                            }
                        }
                        posDetailList.add(posDetailMap);

                    }
                    posRequestMap.put("detailList", posDetailList);
                }
                //
                posJsonMap.put("request",posRequestMap);
            }


            //库存流水新增--数组类型
            List<Object> mes_stockDetailCreateList = (List<Object>) parameterMap.get("mesStockDetail");
            if(mes_stockDetailCreateList != null && mes_stockDetailCreateList.size()>0)
            {
                //多1层request
                Map<String,Object> posRequestMap = new HashMap<String,Object>();

                List<Object> posDetailList = new ArrayList<Object>();

                //循环T100单头
                for (Object object : mes_stockDetailCreateList)
                {
                    Map<String,Object> posStockDetailMap = new HashMap<String,Object>();

                    //门店管理單頭需取代的欄位
                    Map<String,Object> mes_stockDetailCreateReplaceMap = (Map<String, Object>) replaceMap.get("mesStockDetail");
                    //T100单头内容
                    Map<String,Object> mes_stockDetailCreateMap = (Map<String, Object>) object;
                    for (String mes_stockDetailCreateKey : mes_stockDetailCreateReplaceMap.keySet())
                    {
                        if(mes_stockDetailCreateMap.containsKey(mes_stockDetailCreateKey)) {
                            posStockDetailMap.put(mes_stockDetailCreateReplaceMap.get(mes_stockDetailCreateKey).toString(), mes_stockDetailCreateMap.get(mes_stockDetailCreateKey));
                        }
                    }
                    posDetailList.add(posStockDetailMap);
                }
                posRequestMap.put("mesStockDetail", posDetailList);

                posJsonMap.put("request",posRequestMap);
            }


        }

        //这段ERP调门店的，如果没有自动产生
        if (!posJsonMap.containsKey("requestId"))
        {
            posJsonMap.put("requestId", PosPub.getGUID(false));
        }
        if (!posJsonMap.containsKey("version"))
        {
            posJsonMap.put("version", "3.0");
        }
        if (!posJsonMap.containsKey("plantType"))
        {
            posJsonMap.put("plantType", "erp");
        }

        return posJsonMap;
    }

    /**
     * 轉換成T100端的Json Msg格式
     * @param
     * @return
     * @throws Exception
     */
    private Map<String,Object> transferT100Msg(Map<String,Object> jsonMap) throws Exception{
        Map<String,Object> JsonMsgMap = new HashMap<String,Object>();
        if(jsonMap != null){
            Map<String,Object> replaceMap = setReplaceMsg();
            //處理服務元件版本
            JsonMsgMap.put("srvver", "1.0");
            //處理服務執行結果狀態碼
            JsonMsgMap.put("srvcode", "000");

            //增加payload Map
            Map<String,Object> payloadMap = new HashMap<String,Object>();
            //增加std_data Map
            Map<String,Object> stdDataMap = new HashMap<String,Object>();
            //增加execution Map
            Map<String,Object> executionMap = new HashMap<String,Object>();

            for (String key : replaceMap.keySet()) {
                if(jsonMap.containsKey(key)){
                    if(key.toString().equals("success")){
                        if((boolean) jsonMap.get(key)){
                            executionMap.put(replaceMap.get(key).toString(), "0");
                        }else{
                            executionMap.put(replaceMap.get(key).toString(), "-1");
                        }
                    }else{
                        //System.out.println("********"+key.toString());
                        if(!key.toString().equals("datas") && !key.toString().equals("upErrorLogDatas") && !key.toString().equals("dayEndCheckDatas")){
                            executionMap.put(replaceMap.get(key).toString(), jsonMap.get(key).toString());
                        }
                    }
                }
            }
            executionMap.put("sql_code", "");

            //增加parameter Map
            Map<String,Object> parameterMap = new HashMap<String,Object>();
            for (String key : replaceMap.keySet()) {
                if(jsonMap.containsKey(key)){
                    if(key.toString().equals("doc_no")){
                        parameterMap.put("doc_no", jsonMap.get(key));
                    }
                    if(key.toString().equals("org_no")){
                        parameterMap.put("org_no", jsonMap.get(key));
                    }
                }
            }
            Map<String,Object> shopsMap = new HashMap<String,Object>();
            //增加up_error_log
            Map<String,Object> upErrorLogMap = new HashMap<String,Object>();
            for (String key : replaceMap.keySet()) {
                if(jsonMap.containsKey(key)){
                    if(key.toString().equals("dayEndCheckDatas")){
                        shopsMap.put("day_end_shops", jsonMap.get(key));
                    }
                    if(key.toString().equals("upErrorLogDatas")){
                        upErrorLogMap.put("up_error_log", jsonMap.get(key));
                    }
                }
            }

            //組合回傳格式階層
            stdDataMap.put("execution", executionMap);
            if (shopsMap != null && shopsMap.size()>0){
                stdDataMap.put("parameter", shopsMap);
            }
            if (upErrorLogMap != null && upErrorLogMap.size()>0){
                stdDataMap.put("parameter", upErrorLogMap);
            }
            if(!parameterMap.isEmpty() && parameterMap.size()>0)
            {
                stdDataMap.put("parameter", parameterMap);
            }
            //stdDataMap.put("up_error_log", upErrorLogMap);
            //parameterMap.put("day_end_shops", shopsMap);
            payloadMap.put("std_data", stdDataMap);
            JsonMsgMap.put("payload", payloadMap);
        }
        return JsonMsgMap;
    }

    /**
     * 設定取代的Msg Map
     * @return
     */
    private Map<String,Object> setReplaceMsg(){
        //取代的Map
        Map<String,Object> replaceMap = new HashMap<String,Object>();
        replaceMap.put("success", "code");
        replaceMap.put("serviceDescription", "description");
        replaceMap.put("dayEndCheckDatas", "day_end_shops");
        replaceMap.put("upErrorLogDatas", "up_error_log");
        replaceMap.put("doc_no","doc_no");
        replaceMap.put("org_no", "org_no");
        return replaceMap;
    }
}
