package com.dsc.spos.json.utils;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * 20251213 add by 11217
 *
 * @author 李杨杨
 */
public final class FastJsonParseJson {

    public <T> String beanToJson(T obj) {
        return JSON.toJSONString(obj);
    }

    public <T> T jsonToBean(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 将json转为Map
     */
    public Map<String, Object> jsonToMap(String json) {
        return jsonToBean(json, Map.class);
    }
    /**
     * 将map转为json
     */
    public String mapToJson(Map<String, Object> map) {
        return beanToJson(map);
    }


}
