package com.dsc.spos.waimai.shansong;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: wangzyc
 * @create: 2022-01-14
 */
@Data
@Accessors(chain = true)
public class ShanSongEntity {
//    private String appSecret;
    private String clientId; // App-key
    private String shopId; // 商户ID
    private Long timestamp; // 毫秒级时间戳
    private String data; // 业务入参
    private String sign; // 签名

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer()
                .append("clientId").append(clientId)
                .append("data").append(data)
                .append("shopId").append(shopId)
                .append("timestamp").append(timestamp);
        return sb.toString();
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("clientId", clientId);
        map.put("shopId", shopId);
        map.put("timestamp", timestamp);
        map.put("sign", sign);
        map.put("data", data);
        return map;
    }
}
