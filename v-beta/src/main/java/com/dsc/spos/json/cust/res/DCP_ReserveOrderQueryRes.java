package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 预约列表查询
 * @author: wangzyc
 * @create: 2021-07-28
 */
@Data
public class DCP_ReserveOrderQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String reserveNo;         // 预约单号
        private String loadDocType;       // 来源渠道类型
        private String loadDocName;       // 来源渠道名称
        private String status;            // 单据状态 0待审核 1待消费 2已消费 3已取消 4已过期
        private String shopId;            // 所属门店
        private String shopName;          // 门店名称
        private String phone;             // 门店电话
        private String itemsNo;           // 项目编号
        private String itemsName;         // 项目名称
        private String imageUrl;          // 商品图片
        private String serviceTime;       // 服务时长（分钟）
        private String shopDistribution;  // 是否到店分配Y/N
        private String opNo;              // 顾问编号
        private String opName;            // 顾问名称
        private String professionalName;  // 职称名称
        private String headImage;         // 头像
        private String couponCode;        // 券号
        private String memberId;          // 预约人，会员ID
        private String name;              // 会员名称，若无则用微信昵称
        private String mobile;            // 手机号
        private String date;              // 预约日期
        private String time;              // 预约时间段
        private String memo;              // 捎话（备注）
        private String isEvaluate;        // 是否评价Y/N，空返N
        private String qrCode;            // 券二维码
        private String createOpId;       // 创建人ID，若为商城渠道传openId
        private String createOpName;      // 创建人姓名，会员名称若无则用微信昵称
        private String createTime;       // 创建时间
    }

}
