package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGpMsgQuery
 * 服务说明：群发消息查询
 * @author jinzma
 * @since  2024-03-01
 */
@Data
public class DCP_ISVWeComGpMsgQueryRes extends JsonRes {
    private List<Datas> datas;
    @Data
    public class Datas{
        private String gpMsgId;
        private String name;
        private String msg;
        private String remark;
        private String type;
        private String sendType;
        private String status;
        private String createTime;
        private String lastModiTime;

        private List<User> userList;
        private List<Chat> chatList;
        private List<Tag> tagList;
        private List<Annex> annexList;
    }
    @Data
    public class User {
        private String userId;
        private String userName;
    }
    @Data
    public class Chat{
        private String chatId;
        private String chatName;
    }
    @Data
    public class Tag{
        private String item;
        private String tagId;
        private String tagName;
    }
    @Data
    public class Annex{
        private String serialNo;
        private String msgType;
        private String msgId;
        private String linkDescription;   // 链接摘要
        private String linkUrl;           // 链接地址
        private String linkMediaId;       // 链接封面图片文件名
        private String linkMediaUrl;      // 链接封面图片url地址
        private String linkTitle;         // 链接标题
        private String linkPicMediaId;    // 链接封面图片上传后获取的唯一标识3天内有效
        private String miniAppId;         // 小程序appid
        private String miniUrl;           // 小程序url路径
        private String miniMediaId;       // 小程序封面图片文件名
        private String miniMediaUrl;      // 小程序封面图片URL (取系统参数拼接)
        private String miniPicMediaId;    // 小程序封面图片上传后获取的唯一标识3天内有效
        private String miniTitle;         // 小程序标题
        private String imageMediaId;      // 图片文件名
        private String imageMediaUrl;     // 图片URL (取系统参数拼接)
        private String imageWeComPicUrl;  // 图片永久链接
        private String imagePicMediaId;   // 图片上传后获取的唯一标识3天内有效

    }
}
