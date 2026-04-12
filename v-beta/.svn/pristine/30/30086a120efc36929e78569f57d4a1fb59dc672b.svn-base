package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 扫码点餐基础设置新增
 * 3.0 重新做， 有问题找SA-王欢
 * http://183.233.190.204:10004/project/144/interface/api/1928
 */
@Data
public class DCP_ScanOrderBaseSetCreateReq extends JsonBasicReq
{
	private level1Elm request;


	@Data
	public class level1Elm
	{
		private String ruleNo;
		private String ruleName;
		private String scanType;//点餐模式 0.正餐(后结) 1.快餐/街饮(先结)
//		private String packNo; //包装编号
//		private String packName;//包装名称
		private String restrictTable; //是否启用桌台，0.否 1.是
		private String tableSet; //开台设置
		private String checkType; //结账类型    0可点餐，可结账       1可点餐，不可结账
//		private String restrictLike;//允许点赞 0.允许 1.不允许
		private String restrictAdvanceOrder ; //是否允许提前选菜0.否 1.是
		private String retainTime ;// 保留时长​，单位小时

		// 新增开关takeAway外带打包，coupon优惠券、integral积分 BY WANGZYC 20201123
		private String takeAway; // 打包带走 0.禁用 1.启用

		// 新增orderMemo订单备注开关 BY WANGZYC 20201214
		private String orderMemo;

        // 新增 search点单页搜索  BY WANGZYC 20220215
        private String search;

		private String beforOrder; // 预约点单 0.禁用 1.启用
		private String choosableTime; // 可选时间范围：近X天

		private String qrCode; //二维码
		private String lastModiOpId;

		private String lastModiOpName;
		private String status; //状态: -1未启用、100已启用、0已禁用
//		private Invoice invoice;//发票信息

		private String coupon;// 优惠券 0.禁用 1.启用
		private String integral; // 积分 0.禁用 1.启用

		// 新增firstRegister点餐前会员注册 BY WANGZYC 20201130
		private String firstRegister; // 点餐前会员注册 0.禁用 1.启用

		private String recommendedDishes;//是否开启推荐菜品提示的开关
		private String description; //对应菜品提示编辑文案

        // Add 2021-06-07 王欢 新增isAutoProm、isAutoRegister字段
        private String isAutoProm;        // 点单页促销实时提示 0.禁用 1.启用
        private String isAutoRegister; // 是否自动注册 0.禁用 1.启用

        private String isAutoFold; // 售罄商品折叠 0.禁用 1.启用

        private String isEvaluateRemind; // 是否启用用餐评价提醒，0.禁用 1.启用
        private String remindTime; // 下单X分钟后提醒

		private String restrictShop;//适用门店：0-所有门店1-指定门店2-排除门店
		private String restrictChannel;//适用渠道：0-所有渠道1-指定渠道2-排除渠道

		private String restrictRegister;

		private String counterPay;
		private String register;

        private String recharge; // 结账提示充值 0.禁用 1.启用
		private String evaluation;  //支付成功页评价 0.禁用 1.启用，默认禁用，空为禁用

        private String isPayCard;//支付信息卡片样式0.不使用1.使用：
        
		private String isGoodsDetailDisplay;  //是否显示商品详情页0.否 1.是


        // 新增支付设置 BY wangzyc 2021/3/5
		private List<level2Elm> paySet; // 支付设置

		private List<RangeList> rangeList;

    }

//    @Data
//	public class Invoice
//	{
//		private String isinvoice;//是否开启发票，0-否 1-是
//		private String area;//0-大陆 1-台湾
//		private String reservedInvoiceinfo;//预留发票信息，0-否 1-是
//		private String orderInvoice;//下单开票，0-否，1-是
//		private String memberCarrier;//会员载具，0-否 1-是
//	}

	@Data
	public class RangeList{

		private String rangeType; //适用范围：1-公司2-门店3-渠道
		private String id; //编码
		private String name;//名称
	}

	@Data
	public class level2Elm{
	    private String sortId; // 显示顺序，按顺序从小到大返回
        private String payType; // 款别编码
        private String payName; // 显示名称
    }

}
