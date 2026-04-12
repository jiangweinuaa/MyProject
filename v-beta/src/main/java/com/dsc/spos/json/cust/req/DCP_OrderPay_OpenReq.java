package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 修改原来服务，改为移动支付 定金补录
 * @author wwangzyc
 *
 */
@Data
public class DCP_OrderPay_OpenReq extends JsonBasicReq {

	private level1Elm request;



	@Data
	public class level1Elm
	{
		private String orderNo;             // 业务订单号
		private String workNo;              // 班号
		private String squadNo;             // 班次
		private String machineNo;           // 机台号
		private String opNo;                // 操作员ID
		private String opName;              // 操作员名称
		private String orgType;             // 组织类型 1=公司  2=门店 3=渠道
		private String orgId;               // 如：公司编号 门店号 渠道编号
		private String loadDocType;         // 订单类型 鼎捷外卖：12
        private String openId;              // 第三方应用ID
        private String orderAmount;         // 订单总金额，会员支付必传
        private String pointAmount;         // 订单可积分金额，会员支付必传
        private String sendMsg;             // 是否发送消费消息 1=发送 0=不发送 空则默认为发送

        private level2Elm createPay;        // 移动支付
		private List<level3Elm> pay;        // 支付明细，会员支付组payList  存库逻辑参照DCP_OrderCreate.pay节点

	}


	@Data
	public class level2Elm{
	    private String pay_type;             // 支付方式 #P1=微信 #P2=支付宝 #P103=LinePay
	    private String shop_code;            // 交易机构
	    private String pos_code;             // 终端设备编号
	    private String order_name;           // 订单标题
	    private String order_des;            // 订单描述
	    private String pay_amt;              // 支付金额
	    private String pay_nodiscountamt;    // 不可打折金额
	    private String ip;                   // 终端IP，微信需要
	    private String operation_id;         // 操作员
	    private String notify_url;           // 服务器异步通知页面异步接收微信支付结果通知的回调地址，通知url必须 为外网可访问的url，不能携带参数。
        private String allow_pay_type;       // 逗号隔开，可空，空表示都允许 当pay_type为空时，POS端有促销时跟支 付方式互斥时填写。
        private String trade_type;           // NATIVE -Native支付 JSAPI -JSAPI支付  空默认是NATIVE
        private String appid;                // 微信appid，微信JSAPI时必填
        private String openid;               // 微信openid微信JSAPI时必填

	}

	@Data
	public class level3Elm{
		private String item;                 // 项次 (正整数不能重复)
		private String funcNo;               // 接口类型编码 会员卡：301 积分：302 现金券：304 折扣券：305
		private String payType;              // 新零售支付类型 会员卡和积分传固定值 会员卡：#03 积分：#05 券：券详情中的paycode
		private String payCode;              // 支付方式（小类）
		private String payCodeErp;           // 支付类型(大类)
        private String payName;              // 付款名称
        private String cardNo;               // 支付卡券号
        private String ctType;               // 卡券类型
        private String paySerNum;            // 支付订单号（后端生成，前端不传）
        private String serialNo;             // 银联卡流水号
        private String refNo;                // 银联卡交易流水号
        private String teriminalNo;          // 银联终端号
        private String descore;              // 积分抵现扣减
        private String pay;                  // 付款金额
        private String extra;                // 溢收金额(主要针对券)
        private String changed;              // 找零
        private String bDate;                // 收银营业日期(日期格式yyyyMMdd)
        private String isOrderpay;           // 是否订金
        private String isOnlinePay;          // 是否平台支付
        private String order_PayCode;        // 平台支付方式
        private String couponQty;            // 券数量
        private String isVerification;       // 券是否核销:Y/N
        private String couponMarketPrice;    // 券面值
        private String couponPrice;          // 券售价
        private String canInvoice;           // 开票类型 0不开发票 1可开发票 2已开发票 3第三方开发票

	}

}
