package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;
import java.util.List;

/**
 * 服务函数：MsgTemplate
 * 服务说明：企业消息群发
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96366
 * @author jinzma
 * @since  2024-02-29
 */
@Data
public class MsgTemplate {

    private String chat_type;                 //否	群发任务的类型，默认为single，表示发送给客户，group表示发送给客户群
    private String[] external_userid;         //否	客户的externaluserid列表，仅在chat_type为single时有效，最多可一次指定1万个客户
    private String[] chat_id_list;            //否	客户群id列表，仅在chat_type为group时有效，最多可一次指定2000个客户群。指定群id之后，收到任务的群主无须再选择客户群，仅对4.1.10及以上版本的企业微信终端生效
    private TagFilter tag_filter;             //否	要进行群发的客户标签列表，同组标签之间按或关系进行筛选，不同组标签按且关系筛选，每组最多指定100个标签，支持规则组标签
    private String sender;                    //否	发送企业群发消息的成员userid，当类型为发送给客户群时必填
    private boolean allow_select;             //否	是否允许成员在待发送客户列表中重新进行选择，默认为false，仅支持客户群发场景
    private Text text;                        //否	消息文本内容，最多4000个字节
    private List<Attachment> attachments;     //否	附件，最多支持添加9个附件

    @Data
    public class TagFilter{
        private List<Group> group_list;       //否	要进行群发的客户标签列表，同组标签之间按或关系进行筛选，不同组标签按且关系筛选，每组最多指定100个标签，支持规则组标签
    }
    @Data
    public class Group {
        private String[] tag_list;           //否	要进行群发的客户标签列表，同组标签之间按或关系进行筛选，不同组标签按且关系筛选，每组最多指定100个标签，支持规则组标签
    }
    @Data
    public class Text {
        private String content;              //否    消息文本内容，最多4000个字节
    }
    @Data
    public class Attachment {
        private String msgtype;             //是	附件类型，可选image、link、miniprogram或者video
        private Image image;
        private Link link;
        private MiniProgram miniprogram;
        private Video video;
        private File file;
    }
    @Data
    public class Image {
        private String media_id;           //否	图片的media_id，可以通过素材管理接口获得
        private String pic_url;            //否	图片的链接，仅可使用上传图片接口得到的链接
    }
    @Data
    public class Link {
        private String title;             //是	图文消息标题，最长128个字节
        private String picurl;            //否	图文消息封面的url，最长2048个字节
        private String desc;              //否	图文消息的描述，最多512个字节
        private String url;               //是	图文消息的链接，最长2048个字节
    }
    @Data
    public class MiniProgram {
        private String title;             //是	小程序消息标题，最多64个字节
        private String pic_media_id;      //是	小程序消息封面的mediaid，封面图建议尺寸为520*416
        private String appid;             //是	小程序appid（可以在微信公众平台上查询），必须是关联到企业的小程序应用
        private String page;              //是	小程序page路径
    }
    @Data
    public class Video {
        private String media_id;          //是	视频的media_id，可以通过素材管理接口获得
    }
    @Data
    public class File{
        private String media_id;          //是	文件的media_id，可以通过素材管理接口获得
    }



}
