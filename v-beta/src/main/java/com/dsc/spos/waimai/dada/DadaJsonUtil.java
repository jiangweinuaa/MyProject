package com.dsc.spos.waimai.dada;

import com.alibaba.fastjson.JSON;

public class DadaJsonUtil 
{
	public static String toJson(Object object) {
    return object == null ? "" : JSON.toJSONString(object);
}

public static <T> T fromJson(String json, Class<T> tClass){
    return JSON.parseObject(json, tClass);
}
}
