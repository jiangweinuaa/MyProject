package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;

/**
 * 服务函数：MessageSend
 * 服务说明：发送应用消息
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96458
 * @author jinzma
 * @since  2024-03-15
 */
@Data
public class MessageSend {
    private String touser;                     //否	指定接收消息的成员，成员ID列表（多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为"@all"，则向该企业应用的全部成员发送
    private String toparty;                    //否	指定接收消息的部门，部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为"@all"时忽略本参数
    private String totag;                      //否	指定接收消息的标签，标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为"@all"时忽略本参数
    private String msgtype;                    //是	消息类型，此时固定为：text
    private String agentid;                    //是	企业应用的id，整型。企业内部开发，可在应用的设置页面查看；第三方服务商，可通过接口 获取企业授权信息 获取该参数值
    private Text text;
    private String safe;                       //否	表示是否是保密消息，0表示可对外分享，1表示不能分享且内容显示水印，默认为0
    private String enable_id_trans;            //否	表示是否开启id转译，0表示否，1表示是，默认0。仅第三方应用需要用到，企业自建应用可以忽略。
    private String enable_duplicate_check;     //否	表示是否开启重复消息检查，0表示否，1表示是，默认0
    private String duplicate_check_interval;   //否	表示是否重复消息检查的时间间隔，默认1800s，最大不超过4小时

    //其中text参数的content字段可以支持换行、以及A标签，即可打开自定义的网页（可参考以上示例代码）(注意：换行符请用转义过的\n)
    @Data
    public class Text {
        private String content;               //是	消息内容，最长不超过2048个字节，超过将截断（支持id转译）
    }

}


