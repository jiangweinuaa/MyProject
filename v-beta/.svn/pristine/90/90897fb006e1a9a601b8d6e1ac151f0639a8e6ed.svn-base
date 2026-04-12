package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DeliveryHistoryQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryHistoryQuery_OpenRes;
import com.dsc.spos.json.cust.res.DCP_DeliveryHistoryQuery_OpenRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DeliveryHistoryQuery_OpenRes.level2Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.kdniao.query.kdniaoQueryService;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_DeliveryHistoryQuery_Open extends SPosBasicService<DCP_DeliveryHistoryQuery_OpenReq, DCP_DeliveryHistoryQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_DeliveryHistoryQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        DCP_DeliveryHistoryQuery_OpenReq.level1Elm request = req.getRequest();
        if (request==null)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不能为空！");
        }

        if (Check.Null(request.getShipperNo())) {
            errCt++;
            errMsg.append("物流单号不能为空, ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DeliveryHistoryQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_DeliveryHistoryQuery_OpenReq>(){};
    }

    @Override
    protected DCP_DeliveryHistoryQuery_OpenRes getResponseType() {
        return new DCP_DeliveryHistoryQuery_OpenRes();
    }

    @Override
    protected DCP_DeliveryHistoryQuery_OpenRes processJson(DCP_DeliveryHistoryQuery_OpenReq req) throws Exception {
        DCP_DeliveryHistoryQuery_OpenRes res = this.getResponse();
        res.setDatas(res.new level1Elm());
        String ShipperCode = req.getRequest().getShipperCode();
        String LogisticCode = req.getRequest().getShipperNo();
        String delvieryType = "KDN";//快递鸟物流类型
        try
        {
            String sql = " select * from dcp_outsaleset where STATUS='100' and  DELIVERYTYPE='"+delvieryType+"'";
            if (req.geteId()!=null&&!req.geteId().isEmpty())
            {
                sql = sql +" and EID='"+req.geteId()+"'";
            }
            List<Map<String,Object>> getQData = this.doQueryData(sql,null);
            if (getQData==null||getQData.isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "快递鸟查询商户参数未配置或未启用，请检查物流参数配置！");
            }
            Map<String,Object> mapKDN = getQData.get(0);
            //如果多个KDN配置 ，优先取全国快递
            if (getQData.size()>1)
            {
                for (Map<String,Object> par : getQData)
                {
                  String SHIPMODE = par.getOrDefault("SHIPMODE","").toString();//物流产品类型 0全国快递，1同城快递
                  if ("0".equals(SHIPMODE))
                  {
                      mapKDN = par;
                      break;
                  }
                }

            }


            kdniaoQueryService kdniaoService = new kdniaoQueryService();
            String EBusinessID =mapKDN.getOrDefault("APPSIGNKEY","").toString();//"1769046";//即用户ID，登录快递鸟官网会员中心获取 https://www.kdniao.com/UserCenter/v4/UserHome.aspx
            String ApiKey = mapKDN.getOrDefault("APPSECRET","").toString();//"d7870752-8668-4209-8119-17f5a94c580d";//即API key
            String RequestType = mapKDN.getOrDefault("QUERYTYPE","8002").toString();//8001-在途监控；8002-快递查询
            StringBuffer errorBuf = new StringBuffer("");
            String result = kdniaoService.queryTrace(EBusinessID,ApiKey,RequestType,ShipperCode,LogisticCode,errorBuf);
            if (result==null||result.isEmpty())
            {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("调用快递鸟接口异常:"+errorBuf.toString());
                return res;
            }
            org.json.JSONObject js = new org.json.JSONObject(result);
            boolean Success = js.optBoolean("Success");
            if (!Success)
            {
                String Reason = js.optString("Reason","");
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("调用快递鸟接口返回:"+Reason);
                return res;
            }
            res.setSuccess(true);
            res.setServiceDescription("服务执行成功！");
            res.setServiceStatus("000");
            level1Elm datas = res.new level1Elm();
            datas.setTraces(new ArrayList<>());
            String State = js.optString("State","");
            String StateEx = js.optString("StateEx","");
            String ShipperCode_res = js.optString("ShipperCode","");
            String LogisticCode_res = js.optString("LogisticCode","");
            datas.setDeliveryStatus(StateEx);
            datas.setLogisticCode(LogisticCode_res);
            datas.setShipperCode(ShipperCode_res);
            if ("0".equals(State))
            {
                String Reason = js.optString("Reason","暂无轨迹信息");
                level2Elm item = res.new level2Elm();
                item.setDeliveryAction(State);
                item.setDeliveryAcceptStation(Reason);
                item.setDelieveryAcceptTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                item.setDeliveryLocation("");
                datas.getTraces().add(item);
            }
            else
            {
                org.json.JSONArray Traces_JsonArray = js.optJSONArray("Traces");
                if (Traces_JsonArray!=null&&Traces_JsonArray.length()>0)
                {
                    for (int i =0;i<Traces_JsonArray.length();i++)
                    {
                        try
                        {
                            org.json.JSONObject item_obj = Traces_JsonArray.getJSONObject(i);
                            level2Elm item = res.new level2Elm();
                            item.setDeliveryAction(item_obj.optString("Action",""));
                            item.setDeliveryAcceptStation(item_obj.optString("AcceptStation",""));
                            item.setDelieveryAcceptTime(item_obj.optString("AcceptTime",""));
                            item.setDeliveryLocation(item_obj.optString("Location",""));
                            datas.getTraces().add(item);

                        }
                        catch (Exception e)
                        {
                            continue;
                        }

                    }

                }
            }
            res.setDatas(datas);


        }
        catch (Exception e)
        {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:"+e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_DeliveryHistoryQuery_OpenReq req) throws Exception {
        return null;
    }
}
