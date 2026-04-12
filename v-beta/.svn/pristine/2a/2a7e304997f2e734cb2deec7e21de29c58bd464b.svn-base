package com.dsc.spos.waimai.yto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Date 2021/6/11 16:10
 * @Author mustang
 */
@Data
@Accessors(chain = true)
@JacksonXmlRootElement(localName = "KOrderCreateRequest")
public class KOrderCreateEntity extends BaseEntity {
    String customerCode;

    String logisticsNo;

    /**
     * 同步：下单处理使用K码的物料额度生成运单号返回
     * 缺点，性能低，取消订单物流额度占用，待回收后才能继续使用。
     * 优点，直接返回运单号，可以自行打印面单。
     * 异步：下单处理不使用物料额度不生成运单号，待业务员上门取件进行拉单打印使用物料；可在走件操作回推处理运单号。
     * 缺点，不能自行打印面单。
     * 优点，性能高，订单取消不占用额度。无需后续回收。
     * 结合业务场景自行选择对接模式。
     */
    Integer mode;

    String senderName;

    String senderProvinceName;

    String senderCityName;

    String senderCountyName;

    String senderTownName;

    String senderAddress;

    String senderMobile;

    String recipientName;

    String recipientProvinceName;

    String recipientCityName;

    String recipientCountyName;

    String recipientTownName;

    String recipientAddress;

    String recipientMobile;

    String remark;

    String gotCode;

    @JacksonXmlElementWrapper(localName = "increments")
    @JacksonXmlProperty(localName = "OrderIncrementDto")
    List<OrderIncrementDto> increments;

    @JacksonXmlElementWrapper(localName = "goods")
    @JacksonXmlProperty(localName = "OrderGoodsDto")
    List<OrderGoodsDto> goods;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String startTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String endTime;

    /**
     * 结算方式。下单时传过来的。1现付 ,2月结 ,3到付 ,4总部月结
     */
    Integer settlementType;

    BigDecimal weight;

    /**
     * 可以对客户订单进行业务或渠道区分。
     */
    String cstBusinessType;

    String cstOrderNo;

    @Data
    @Accessors(chain = true)
    public static class OrderIncrementDto {

        Integer type;
        BigDecimal amount;
        BigDecimal premium;

    }

    @Data
    @Accessors(chain = true)
    public static class OrderGoodsDto {

        String name;
        BigDecimal weight;
        BigDecimal length;
        BigDecimal width;
        BigDecimal height;
        BigDecimal price;
        Integer quantity;

    }
}
