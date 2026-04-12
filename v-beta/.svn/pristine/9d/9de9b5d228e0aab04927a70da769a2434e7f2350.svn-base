package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ShopDateWeatherQueryRes extends JsonRes
{

    private level1Elm datas;

    @Data
    public class level1Elm
    {
        private List<level2Elm> dataList;
    }

    @Data
    public class level2Elm
    {
        private String sDate;
        private String city;
        private String district;
        private String week;
        private String dayWeather;
        private String nightWeather;
        private String dayTemperature;
        private String nightTemperature;
        private String dayWind;
        private String nightWind;
        private String dayPower;
        private String nightPower;
        private String lunar;
        private String festival;
    }



}
