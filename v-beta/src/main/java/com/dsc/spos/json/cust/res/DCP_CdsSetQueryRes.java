package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: CDS叫号屏设置查询
 * @author: wangzyc
 * @create: 2022-05-25
 */
@Data
public class DCP_CdsSetQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String baseSetNo; // 模板编号
        private String status; // 状态100已启用、0已禁用
        private String updateTime; // 自动刷新时间，秒
        private String isCustomColour; // 是否启用自定义颜色Y/N
        private String colourId; // 配色ID
        private String themeColor; // 主题色
        private String subColor; // 辅助色
        private String memo; // 备注
        private String fileType; // 广告图类型 0-静态图 1-视频
        private String rollTime ; // 图片轮播时间
        private String fontColorType; // 字色类型0.反白1.正黑
        private String serviceUrl; // 服务地址
        private String updateUrl; // 更新地址
        private List<level2Elm> fileList; // 文件列表
        private String voiceTimes; // 语音播报次数
        private String voiceText; // 语音播报文案
        private String restrictShop; // 适用门店：0-所有门店1-指定门店
        private String showWaimai; // Y/N大屏是否展示外卖订单
        private List<level3Elm> rangeList; // 适用明细
    }

    @Data
    public class level2Elm{
        private String item; // 项次
        private String fileName; // 文件名
        private String fileUrl; // 文件地址路径，需拼接

    }

    @Data
    public class level3Elm{
        private String shopId; // 门店编码
        private String shopName; // 门店名称
    }
}
