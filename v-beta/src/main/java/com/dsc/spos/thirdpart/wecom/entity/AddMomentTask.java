package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;

import java.util.List;

/**
 * 服务函数：AddMomentTask
 * 服务说明：企业发表内容到客户的朋友圈
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96351
 * @author jinzma
 * @since  2024-03-05
 */
@Data
public class AddMomentTask {
    private Text text;                  //否  文本消息
    private List<Attachment> attachments;     //否  附件，不能与text.content同时为空，最多支持9个图片类型，或者1个视频，或者1个链接。类型只能三选一，若传了不同类型，报错'invalid attachments msgtype'
    private VisibleRange visible_range; //否  指定的发表范围；若未指定，则表示执行者为应用可见范围内所有成员

    @Data
    public class Text {
        private String content;        //否  消息文本内容，不能与附件同时为空，最多支持传入2000个字符，若超出长度报错'invalid text size'
    }

    @Data
    public class Attachment {
        private String msgtype;        //是	 附件类型，可选image、link或者video
        private Image image;           //否  图片消息附件。最多支持传入9个；超过9个报错'invalid attachments size'
        private Link link;             //否	 图文消息附件。只支持1个；若超过1个报错'invalid attachments size'
        private Video video;           //否	 视频消息附件。最长不超过30S，最大不超过10MB。只支持1个；若超过1个报错'invalid attachments size'
    }

    @Data
    public class Image {
        private String media_id;       //是	 图片的素材id，长边不超过10800像素，短边不超过1080像素。可通过上传附件资源接口获得
    }

    @Data
    public class Link {
        private String title;          //否	 图文消息标题，最多64个字节
        private String url;            //是	 图文消息链接
        private String media_id;       //是	 图片链接封面，长边不超过10800像素，短边不超过1080像素，可通过上传附件资源接口获得
    }

    @Data
    public class Video {
        private String media_id;       //是	视频的素材id，未填写报错"invalid msg"。可通过上传附件资源接口获得
    }

    @Data
    public class VisibleRange {
        private SenderList sender_list;                      //否  发表任务的执行者列表，详见下文的“可见范围说明”
        private ExternalContactList external_contact_list;   //否  可见到该朋友圈的客户列表，详见下文的“可见范围说明”
    }

    @Data
    public class SenderList {
        private String[] user_list;          //否  发表任务的执行者用户列表，最多支持10万个
        private Integer[] department_list;   //否  发表任务的执行者部门列表
    }

    @Data
    public class ExternalContactList {
        private String[] tag_list;          //否  可见到该朋友圈的客户标签列表。注：这里仅支持企业客户标签，不支持规则组标签
    }


}

