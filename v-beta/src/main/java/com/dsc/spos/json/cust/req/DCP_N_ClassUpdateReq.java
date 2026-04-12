package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_ClassUpdate
 * 服务说明：N-销售分组编辑
 * @author jinzma
 * @since  2024-04-17
 */
@Data
public class DCP_N_ClassUpdateReq extends JsonBasicReq {

    private Request request;

    @Data
    public class Request {
        private String classType;
        private String classNo;
        private String levelId;
        private String upClassNo;
        private String status;
        private String sortId;
        private String memo;

        // 新增分组图片名称 2021/3/5 wangzyc
        private String classImage; // 分组图片名称
        private String remind; // 是否开启点单提醒Y/N
        private String remindType; // 提醒类型，0.必须 1.提醒
        private String label; // 推荐标签Y/N
        private String labelName; // 标签内容

        // 新增是否可分享 2021/6/18 wangzyc
        private String isShare; // 是否可分享：0.否 1.是

        private String beginDate;
        private String endDate;
        private String goodsSortType;

        private String restrictShop;
        private String restrictChannel;
        private String restrictAppType;
        private String restrictPeriod;
        private String isPublic;

        private List<className> className_lang;
        private List<displayName> displayName_lang;
        private List<range> rangeList;

    }
    @Data
    public class className {
        private String langType;
        private String name;
    }
    @Data
    public class displayName {
        private String langType;
        private String name;
    }
    @Data
    public class range {
        private String rangeType;
        private String id;
        private String name;
    }



}
