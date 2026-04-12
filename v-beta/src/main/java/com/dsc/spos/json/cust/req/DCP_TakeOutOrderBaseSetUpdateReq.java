package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 外卖基础设置修改
 * @author yuanyy 2020-03-16
 *
 */
@Data
public class DCP_TakeOutOrderBaseSetUpdateReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public  class level1Elm{
        // isregister改为isRegister 且移入promSet节点，cityDeliverFreight改为array，
        // 新增packPrice，packNo与packName改为非必传；删除发票信息、shopType、shops、notifyUrl By wangzyc 2021/2/24
        private String baseSetNo;       // 模板编号
        private String baseSetName;     // 模板名称
        private String choosableTime;   // 可选时间范围：天

        private String prepareTime;     // 备餐时长，分钟
        private String deliveryTime;    // 配送时长，分钟
        private String lowestMoney;     // 最低起送金额
        private String freightWay;      // 配送费类型：0.统一配送费 ，1.不同距离不同配送费
        private String freight;         // 统一配送费 ，元
        private String freeShippingPrice;//包邮价

        private List<level2Elm> cityDeliverFreight;  //  同城配送距离运费
        private String payCountdown;    // 待支付倒计时，分钟
        private String beforOrder;    // 预约点单 0.禁用 1.启用
        private level3Elm promSet;      // 促销设置

        private String status;          // 状态: -1未启用、100已启用、0已禁用
        private String packPriceType;   // 打包费类型0.无 1.统一基础打包费 2.打包费商品
        private String packPrice;       // 基础打包费
//        private String pluNo;           // 打包费对应商品编号
        
        private String isTableware;     // 启用餐具 0.禁用 1.启用
        private String restrictShop;    // 适用门店：0-所有门店1-指定门店2-排除门店


        private List<level4Elm> orderTimes;// 外卖接单时间
        private List<level5Elm> paySet; // 支付设置
        private List<level6Elm> rangeList;  // 适用明细


    }

    @Data
    public  class level2Elm{
        private String serialNo;   // 必传，序号
        private String maxDistance;// %S公里以内
        private String freight;    // 运费
        /**
         * 距离区间起送价
         */
        private String lowestMoney;
        /**
         * 距离区间包邮价
         */
        private String freeShippingPrice;
        /**
         * 距离区间配送时长
         */
        private String deliveryTime;
    }

    @Data
    public  class level3Elm{
        private String isRegister;   // 点餐需注册会员：0.禁用，1.启用
        private String coupon;       // 优惠券 0.禁用 1.启用
        private String integral;     // 积分 0.禁用 1.启用
        private String restrictRegister; // 会员注册引导 0.禁用 1.启用

        // Add 2021-06-07 王欢 新增isAutoProm、isAutoRegister字段
        private String isAutoProm;        // 点单页促销实时提示 0.禁用 1.启用
        private String isAutoRegister; // 是否自动注册 0.禁用 1.启用
        private String isAutoFold; // 售罄商品折叠，0.禁用 1.启用
        private String isEvaluateRemind; // 是否启用用餐评价提醒，0.禁用 1.启用
        private String remindTime; // 下单X分钟后提醒
        private String isPayCard;//支付信息卡片样式0.不使用1.使用：
        private String isGoodsDetailDisplay; //是否显示商品详情页0.否 1.是

    }

    @Data
    public  class level4Elm{
        private String startTime; // 开始时间  如 11:00
        private String endTime;   // 截止时间
        private String item;      // 项次
    }

    @Data
    public  class level5Elm{
        private String sortId;   // 显示顺序，按顺序从小到大返回
        private String payType;  // 款别编码
        private String payName;  // 显示名称
    }

    @Data
    public  class level6Elm{
        private String shopId;   // 门店编码
        private String name;     // 门店名称
    }


}
