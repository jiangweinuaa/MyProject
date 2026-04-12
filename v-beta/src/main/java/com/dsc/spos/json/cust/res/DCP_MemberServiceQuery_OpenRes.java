package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

/**
 * @description: 服务记录查询
 * @author: wangzyc
 * @create: 2021-08-09
 */
@Data
public class DCP_MemberServiceQuery_OpenRes extends JsonRes {
    private List<level1Elm> datas;

    @Data
    public class level1Elm{
        private String itemsNo; // 项目编号
        private String itemsName; // 项目名称
        private String imageUrl; // 商品图片
        private String serviceTime; // 服务时长（分钟）
        private String shopId; // 所属门店
        private String shopName; // 门店名称
        private String memberId; // 会员编号
        private String opNo; // 顾问编号
        private String opName; // 顾问名称
        private String professionalName; // 职称名称
        private String date; // 预约日期
        private String time; // 预约时间段
        private String memo; // 捎话（备注）
        private String createTime; // 创建时间
    }
}
