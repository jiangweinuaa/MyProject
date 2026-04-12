package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShopDateWeatherQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopDateWeatherQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ShopDateWeatherQuery extends SPosBasicService<DCP_ShopDateWeatherQueryReq, DCP_ShopDateWeatherQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_ShopDateWeatherQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        else
        {
            if(req.getRequest().getDataList()==null || req.getRequest().getDataList().size()==0)
            {
                isFail = true;
                errMsg.append("dataList不能为空 ");
            }
            else
            {
                for (DCP_ShopDateWeatherQueryReq.level2Elm level2Elm : req.getRequest().getDataList())
                {
                    if (Check.Null(level2Elm.getSDate()))
                    {
                        isFail = true;
                        errMsg.append("sDate不能为空 ");
                    }
                }
            }

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ShopDateWeatherQueryReq> getRequestType()
    {
        return new TypeToken<DCP_ShopDateWeatherQueryReq>(){};
    }

    @Override
    protected DCP_ShopDateWeatherQueryRes getResponseType()
    {
        return new DCP_ShopDateWeatherQueryRes();
    }

    @Override
    protected DCP_ShopDateWeatherQueryRes processJson(DCP_ShopDateWeatherQueryReq req) throws Exception
    {
        DCP_ShopDateWeatherQueryRes res=this.getResponseType();
        res.setDatas(res.new level1Elm());
        res.getDatas().setDataList(new ArrayList<>());

        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        if (getQData != null && !getQData.isEmpty())
        {
            for (Map<String, Object> oneData : getQData)
            {
                DCP_ShopDateWeatherQueryRes.level2Elm level2Elm=res.new level2Elm();
                level2Elm.setCity(oneData.get("CITY").toString());
                level2Elm.setDayPower(oneData.get("DAY_POWER").toString());
                level2Elm.setDayTemperature(oneData.get("DAY_TEMPERATURE").toString());
                level2Elm.setDayWeather(oneData.get("DAY_WEATHER").toString());
                level2Elm.setDayWind(oneData.get("DAY_WIND").toString());
                level2Elm.setDistrict(oneData.get("DISTRICT").toString());
                level2Elm.setFestival(oneData.get("FESTIVAL").toString());
                level2Elm.setLunar(oneData.get("LUNAR").toString());
                level2Elm.setNightPower(oneData.get("NIGHT_POWER").toString());
                level2Elm.setNightTemperature(oneData.get("NIGHT_TEMPERATURE").toString());
                level2Elm.setNightWeather(oneData.get("NIGHT_WEATHER").toString());
                level2Elm.setNightWind(oneData.get("NIGHT_WIND").toString());
                level2Elm.setSDate(oneData.get("SDATE").toString());
                level2Elm.setWeek(oneData.get("WEEK").toString());
                res.getDatas().getDataList().add(level2Elm);
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_ShopDateWeatherQueryReq req) throws Exception
    {
        StringBuffer sqlbuf=new StringBuffer();


        String[] strs=new String[req.getRequest().getDataList().size()];
        for (int i = 0; i < req.getRequest().getDataList().size(); i++)
        {
            strs[i]=req.getRequest().getDataList().get(i).getSDate();
        }
        String sDate_Str = PosPub.getArrayStrSQLIn(strs);

        sqlbuf.append("select b.*,c.lunar,c.festival from Dcp_Org a " +
                              "left join DCP_AREAWEATHER b on a.city=b.city and a.COUNTY=b.district " +
                              "left join DCP_CALENDAR c on a.eid=c.eid and b.sdate=c.d_date " +
                              "where a.eid='"+req.geteId()+"' " +
                              "and a.organizationno='"+req.getShopId()+"' " +
                              "and b.sdate in ("+sDate_Str+") " +
                              "order by b.sdate ");

        return sqlbuf.toString();
    }



}
