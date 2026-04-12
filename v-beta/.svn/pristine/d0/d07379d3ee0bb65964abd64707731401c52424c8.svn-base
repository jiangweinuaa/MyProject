package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;

import java.util.List;

/**
 * 服务函数：ExternaContactBatch
 * 服务说明：批量获取客户详情
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96316
 * @author jinzma
 * @since  2024-01-23
 */
@Data
public class ExternaContactBatch {
    private String errcode;
    private String errmsg;
    private String next_cursor;
    private Fail_info fail_info;

    private List<External_contact_list> external_contact_list;

    @Data
    public class External_contact_list {
        private External_contact external_contact;
        private Follow_info follow_info;
    }
    @Data
    public class External_contact {
        private String external_userid;              //外部联系人的userid
        private String name;                         //外部联系人的名称
        private String avatar;                       //外部联系人头像，代开发自建应用需要管理员授权才可以获取，第三方不可获取，上游企业不可获取下游企业客户该字段
        private String type;                         //外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户
        private String gender;                       //外部联系人性别 0-未知 1-男性 2-女性。第三方不可获取，上游企业不可获取下游企业客户该字段，返回值为0，表示未定义
        private String unionid;                      //外部联系人在微信开放平台的唯一身份标识（微信unionid），通过此字段企业可将外部联系人与公众号/小程序用户关联起来。仅当联系人类型是微信用户，且企业绑定了微信开发者ID有此字段。查看绑定方法。第三方应用和代开发应用均不可获取，上游企业不可获取下游企业客户的unionid字段
        private String position;                     //外部联系人的职位，如果外部企业或用户选择隐藏职位，则不返回，仅当联系人类型是企业微信用户时有此字段
        private String corp_name;                    //外部联系人所在企业的简称，仅当联系人类型是企业微信用户时有此字段
        private String corp_full_name;               //外部联系人所在企业的主体名称，仅当联系人类型是企业微信用户时有此字段。仅企业自建应用可获取；第三方应用、代开发应用、上下游应用不可获取，返回内容为企业名称，即corp_name。
        private External_profile external_profile;  //外部联系人的自定义展示信息，可以有多个字段和多种类型，包括文本，网页和小程序，仅当联系人类型是企业微信用户时有此字段，字段详情见对外属性；
    }
    @Data
    public class External_profile {
        private String external_corp_name;            //企业对外简称，需从已认证的企业简称中选填。可在“我的企业”页中查看企业简称认证状态。
        private Wechat_channels wechat_channels;      //视频号属性。须从企业绑定到企业微信的视频号中选择，可在“我的企业”页中查看绑定的视频号。第三方仅通讯录应用可获取；对于非第三方创建的成员，第三方通讯录应用也不可获取。注意：externalcontact/get不返回该字段
        private List<External_attr> external_attr;    //属性列表，目前支持文本、网页、小程序三种类型
    }
    @Data
    public class Follow_info {
        private String userid;                     //添加了此外部联系人的企业成员userid
        private String remark;                     //该成员对此外部联系人的备注
        private String description;                //该成员对此外部联系人的描述
        private String createtime;                 //该成员添加此外部联系人的时间
        private String[] tag_id;                    //该成员添加此外部联系人所打标签
        private String remark_corp_name;           //该成员对此微信客户备注的企业名称（仅微信客户有该字段）
        private String[] remark_mobiles;           //该成员对此客户备注的手机号码，代开发自建应用需要管理员授权才可以获取，第三方不可获取，上游企业不可获取下游企业客户该字段
        private Wechat_channels wechat_channels;   //该成员添加此客户的来源add_way为10时，对应的视频号信息
        private String state;                      //企业自定义的state参数，用于区分客户具体是通过哪个「联系我」或获客链接添加；由企业通过创建「联系我」或在获客链接中添加customer_channel参数进行指定
        private String oper_userid;                //发起添加的userid，如果成员主动添加，为成员的userid；如果是客户主动添加，则为客户的外部联系人userid；如果是内部成员共享/管理员分配，则为对应的成员/管理员userid
        private String add_way;                    //该成员添加此客户的来源，具体含义详见来源定义
    }
    @Data
    public class Wechat_channels {
        private String nickname;               //视频号名称
        private String source;                 //视频号添加场景，0-未知 1-视频号主页 2-视频号直播间 3-视频号留资服务（微信版本要求：iOS ≥ 8.0.20，Android ≥ 8.0.21，且添加时间不早于2022年4月21日。否则添加场景值为0）
        private String status;                //对外展示视频号状态。0表示企业视频号已被确认，可正常使用，1表示企业视频号待确认
    }
    @Data
    public class External_attr {
        private String type;              //属性类型: 0-文本 1-网页 2-小程序
        private String name;              //属性名称： 需要先确保在管理端有创建该属性，否则会忽略
        private Text text;                //文本类型的属性
        private Web web;                  //网页类型的属性，url和title字段要么同时为空表示清除该属性，要么同时不为空
        private Miniprogram miniprogram;  //小程序类型的属性，appid和title字段要么同时为空表示清除该属性，要么同时不为空
    }
    @Data
    public class Text {
        private String value;          //文本属性内容，长度限制32个UTF8字符
    }
    @Data
    public class Web {
        private String url;           //网页的url，必须包含http或者https头
        private String title;         //网页的展示标题，长度限制12个UTF8字符
    }
    @Data
    public class Miniprogram {
        private String appid;         //小程序appid，必须是有在本企业安装授权的小程序，否则会被忽略
        private String pagepath;      //小程序的页面路径
        private String title;         //小程序的展示标题，长度限制12个UTF8字符
    }
    @Data
    public class Fail_info {
      private String[] unlicensed_userid_list;
    }
}
