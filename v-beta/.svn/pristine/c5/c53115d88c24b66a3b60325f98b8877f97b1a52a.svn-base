package com.dsc.spos.thirdpart.wecom.entity;

import lombok.Data;
import java.util.List;

/**
 * 服务函数：WelcomeMsg
 * 服务说明：发送新客户欢迎语
 * 企微URL：https://developer.work.weixin.qq.com/document/path/96356
 * @author jinzma
 * @since  2024-02-03
 */
@Data
public class WelcomeMsg {
    private String welcome_code;
    private Text text;
    private List<Attachment> attachments;

    @Data
    public class Text {
        private String content;
    }
    @Data
    public class Attachment {
        private String msgtype;
        private Image image;
        private Link link;
        private MiniProgram miniprogram;
        private Video video;
        private File file;
    }
    @Data
    public class Image {
        private String media_id;
        private String pic_url;
    }
    @Data
    public class Link {
        private String title;
        private String picurl;
        private String desc;
        private String url;
    }
    @Data
    public class MiniProgram {
        private String title;
        private String pic_media_id;
        private String appid;
        private String page;
    }
    @Data
    public class Video {
        private String media_id;
    }
    @Data
    public class File{
        private String media_id;
    }


}
