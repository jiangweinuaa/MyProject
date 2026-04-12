package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;

/**
 * 服务函数：JoinWay
 * 服务说明：配置客户群进群方式
 * 企微URL：https://developer.work.weixin.qq.com/document/path/92229
 * @author jinzma
 * @since  2024-02-27
 */
@Data
public class JoinWay {
    private String config_id;         //企业联系方式的配置id(修改必传)
    private String scene;              //是	场景。1 - 群的小程序插件 2 - 群的二维码插件
    private String remark;             //否	联系方式的备注信息，用于助记，超过30个字符将被截断
    private String auto_create_room;  //否	当群满了后，是否自动新建群。0-否；1-是。 默认为1
    private String room_base_name;    //否	自动建群的群名前缀，当auto_create_room为1时有效。最长40个utf8字符
    private String room_base_id;      //否	自动建群的群起始序号，当auto_create_room为1时有效
    private String[] chat_id_list;    //是	使用该配置的客户群ID列表，最多支持5个。
    private String state;             //否	企业自定义的state参数，用于区分不同的入群渠道。不超过30个UTF-8字符
}
