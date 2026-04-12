package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_ClassDetail
 * 服务说明：N-销售分组详情
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_ClassDetailRes extends JsonRes {

    private List<Datas> datas;

    @Data
    public class Datas {
        private String classType;
        private String classNo;
        private String className;
        private String displayName;
        private String levelId;
        private String upClassNo;
        private String upClassName;
        private String subClassCount;//下级分类数量
        private String status;
        private String sortId;//显示顺序，排序第一列
        private String memo;
        private String classImageUrl; // 分组图片地址
        private String classImage;  // 分组图片名称
        private String remind; // 是否开启点单提醒Y/N，classType=POS必返
        private String remindType; // 提醒类型，0.必须 1.提醒 仅限classType=POS
        private String label; // 推荐标签Y/N
        private String labelName; // 标签内容
        // 新增是否可分享 2021/6/18 wangzyc
        private String isShare; // 是否可分享：0.否 1.是
        private String beginDate;//生效日期YYYY-MM-DD
        private String endDate;//失效日期YYYY-MM-DD
        private String goodsSortType;//商品排序：1-默认顺序 2-销量降序 3-价格升序 4-价格降序 5-上架时间降序
        private String restrictPeriod;//适用时段：0-所有时段1-指定时段
        private String restrictShop;//适用门店：0-所有门店1-指定门店2-排除门店
        private String restrictChannel;//适用渠道：0-所有渠道1-指定渠道2-排除渠道
        private String restrictAppType;//适用应用：0-所有应用1-指定应用
        private String createtime;
        private String createopid;//创建人编号
        private String createopname;
        private String lastmoditime;
        private String lastmodiopid;//最后修改人编码
        private String lastmodiname;
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
