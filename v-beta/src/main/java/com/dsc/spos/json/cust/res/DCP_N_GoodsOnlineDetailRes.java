package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_GoodsOnlineDetail
 * 服务说明：N-商城商品详情  (Yapi没有规格)
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_GoodsOnlineDetailRes extends JsonRes {
    private Datas datas;

    @Data
    public class Datas {
        private String pluNo;
        private String pluName;
        private String pluType;//商品类型：NORMAL-普通商品 FEATURE-特征码商品 PACKAGE -套餐商品MULTISPEC-多规格商品
        private String stockDisplay;//页面是否显示库存0-否1-是
        private String attrGroupId;//商品属性分组编码
        private String attrGroupName;//商品属性分组编码
        private String shopPickUp;//是否支持自提0-否1-是
        private String cityDeliver;//是否支持同城配送0-否1-是
        private String expressDeliver;//是否支持全国快递0-否1-是
        private String freightFree;//是否包邮0-否1-是
        private String freightTemplateId;//运费模板编码
        private String preSale;//是否预订，需提前预订0-否1-是
        private String deliveryDateType;//发货时机类型1：付款成功后发货2：指定日期发货
        private String deliveryDateType2;//发货时间类型1：小时 2：天
        private String deliveryDateValue;//付款后%S天后发货
        private String deliveryDate;//预计发货日期
        private String onShelfAuto;//是否自动下架0-否1-是
        private String onShelfDate;//上架日期
        private String onShelfTime;//上架时间
        private String offShelfAuto;//是否自动下架0-否1-是
        private String offShelfDate;//下架日期  自动时不可空，YYYY-MM-DD
        private String offShelfTime;//下架时间 自动时不可空,HH:MI:SS
        private String memo;
        private String status;//状态：-1未上架100-已上架0-已下架
        private String sortId;
        private String restrictShop;//适用门店：0-所有门店1-指定门店2-排除门店
        private String restrictChannel;//适用渠道：0-所有渠道1-指定渠道2-排除渠道
        private String restrictAppType;//适用应用：0-所有应用1-指定应用
        private String displayName;//商品显示名称
        private String simpleDescription;
        private String shareDescription;
        private String deliveryStartDate;  //预计发货开始日期
        private String deliveryEndDate;    //预计发货截止日期
        private String isRestaurant;       //是否堂食0-否1-是

        private List<displayNameLang> displayName_lang;//商品显示名称
        private List<simpleDescription> simpleDescription_lang;
        private List<shareDescription> shareDescription_lang;
        private List<classMemu> classList;//隶属菜单
        private List<refClassMemu> refClassList;//关联推荐菜单
        private List<range> rangeList;//适用范围
        private List<intro> introList;//商品介绍
        private List<msgKind> msgKindList;//商品留言
        private List<Tags> tags;


    }
    @Data
    public class displayNameLang {
        private String langType;
        private String name;
    }
    @Data
    public class simpleDescription {
        private String langType;
        private String name;
    }
    @Data
    public class shareDescription {
        private String langType;
        private String name;
    }
    @Data
    public class classMemu {
        private String classNo;//菜单编码
        private String className;//菜单名称
    }
    @Data
    public class refClassMemu {
        private String classNo;//菜单编码
        private String className;//菜单名称
    }
    @Data
    public class intro {
        private String attrId;//属性编码
        private String attrName;//属性名称
        private String intro;//商品介绍
    }
    @Data
    public class msgKind {
        private String msgKindId;//留言项编码
        private String need;//是否必须0-否1-是
    }
    @Data
    public class range {
        private String rangeType;//1-公司2-门店3-渠道4-应用
        private String id;
        private String name;
    }
    @Data
    public class Tags{
        private String onliLaSorting;
        private String tagNo;
        private String tagName;
    }
}

