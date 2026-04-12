package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;

/**
 * 服务函数：ContactWay
 * 服务说明：配置客户联系「联系我」方式
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96348
 * @author jinzma
 * @since  2024-02-26
 */
@Data
public class ContactWay {
    private String config_id;     // 是 企业联系方式的配置id
    private String type;          // 是	联系方式类型,1-单人, 2-多人
    private String scene;         // 是	场景，1-在小程序中联系，2-通过二维码联系
    private String remark;        // 否	联系方式的备注信息，用于助记，不超过30个字符
    private boolean skip_verify;   // 否	外部客户添加时是否无需验证，默认为true
    private String state;         // 否	企业自定义的state参数，用于区分不同的添加渠道，在调用“获取外部联系人详情”时会返回该参数值，不超过30个字符
    private String[] user;        // 否	使用该联系方式的用户userID列表，在type为1时为必填，且只能有一个
}
