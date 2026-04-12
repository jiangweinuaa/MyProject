package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_OrderParaUpdate
 * 服务说明：N_订单参数修改    就加一个字段isDinerIn，其余代码全部继承DCP_OrderParaUpdateReq
 * @author jinzma
 * @since  2024-05-23
 */
@Data
public class DCP_N_OrderParaUpdateReq extends JsonBasicReq {

    private levelRequest request;

    @Data
    public class levelRequest {
        private String organizationNo;
        private String organizationName;
        private String orgForm;
        private String shopBeginTime;
        private String shopEndTime;
        private String province;
        private String city;
        private String county;
        private String street;
        private String address;
        private String phone;
        private String isSelfPick;
        private String selfBeginTime;
        private String selfEndTime;
        private String isCityDelivery;
        private String isProduction;
        //private String radiateShippingShop;
        private String range_type;
        private String range;
        private String chaoQuQuanJiaShop;
        private String chaoQuOkShop;
        private String chaoQuSevenShop;
        private String chaoQuLaiErFuShop;
        private String radiateType;
        private String longitude;
        private String latitude;
        private String deliverShop;        // 指定快递发货门店
        private String isSpare;            // 是否为备用快递发货机构0否1是
        // 入参新增kdsSet节点
        private level2Elm kdsSet; // KDS参数设置
        private String isDinerIn; //是否堂食0否1是

        private List<level1Elm> autoTakeList;
        private List<level1ShippingOrg> radiateShippingOrgList;


    }

    @Data
    public class level1Elm {
        private String loadDocType;
        private String isAutoDelivery;
        private String cityDeliveryType;
        private String nationalDeliveryType;
    }

    @Data
    public class level1ShippingOrg {
        private String radiateShippingShop;
        private String radiateShippingShopName;
    }

    @Data
    public class level2Elm{
        private String kds;         // KDS是否启用 Y/N
        private List<level3Elm> stallList;
        private String everyPiece;  // 每X件商品
        private String worthScore;  // 抵Y分

        private String completeImage;   // 是否启用生产完成插图Y/N
        private String isQrcode;        // 是否生成订单二维码Y/N
        private List<level4Elm> imageList; // 图片列表
    }

    @Data
    public class level3Elm{
        private String stallId;             // 档口编号，数组中的编号不可重复
        private String stallName;           // 档口名称
        private String tagType;             // 生产标签范围 0.全部标签 1.指定标签
        private List<subTag> subTagList;    // 指定生产标签
        private func funcList;      // 功能清单

    }

    @Data
    public class subTag{
        private String tagNo;               // 生产标签编号
    }

    @Data
    public class func{
        private String isOrder;             // 订单中心Y/N
        private String achievements;        // 绩效统计Y/N
        private String employee;            // 员工认证Y/N
        private String goodsDetails;        // 商品明细Y/N
    }

    @Data
    public class level4Elm{
        private String imageName;           // 图片名称fileName
    }



}
