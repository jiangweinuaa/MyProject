package com.dsc.spos.waimai.yto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Date 2021/6/11 15:45
 * @Author mustang
 */
@Data
@ToString
@Accessors(chain = true)
public class DispatchEntity extends BaseEntity {

    /**
     * 时间戳 格式为毫秒表示的字符串
     * @see System#currentTimeMillis()
     */
    String timestamp;

    /**
     * 请求参数
     */
    String param;

    /**
     * 请求加密
     */
    String sign;

    /**
     * 请求的参数类型以及返回的参数类型
     */
    String format;

    /**
     * 调用服务的版本信息
     */
    String v;

}
