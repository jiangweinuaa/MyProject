package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_ScanOrderBaseSetUpdateReq extends JsonBasicReq
{
	private level1Elm request;
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm
	{
		private String ruleNo;
		private String ruleName;
		private String scanType;// 点餐模式 0.正餐(后结) 1.快餐/街饮(先结)
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

		private String isAutoFold; // 售罄商品折叠 0.禁用 1.启用 默认启用
        private String isEvaluateRemind; // 是否启用用餐评价提醒，0.禁用 1.启用
        private String remindTime; // 下单X分钟后提醒

        private String recharge; // 结账提示充值 0.禁用 1.启用
		private String evaluation;  //支付成功页评价 0.禁用 1.启用，默认禁用，空为禁用
        private String isPayCard;//支付信息卡片样式0.不使用1.使用：


        // 增加支付设置 BY wangzyc 2021/3/8
        private List<PaySet> paySet; //支付设置

		private String restrictShop;//适用门店：0-所有门店1-指定门店2-排除门店
		private String restrictChannel;//适用渠道：0-所有渠道1-指定渠道2-排除渠道
		
		private String restrictRegister;
		private List<RangeList> rangeList;


		private String counterPay; 
		private String register;
  
		private String isGoodsDetailDisplay;   //是否显示商品详情页0.否 1.是


		public String getIsPayCard()
		{
			return isPayCard;
		}

		public void setIsPayCard(String isPayCard)
		{
			this.isPayCard = isPayCard;
		}

		public String getRecharge() {
            return recharge;
        }

        public void setRecharge(String recharge) {
            this.recharge = recharge;
        }

        public String getIsAutoProm() {
			return isAutoProm;
		}

        public String getChoosableTime() {
            return choosableTime;
        }

        public void setChoosableTime(String choosableTime) {
            this.choosableTime = choosableTime;
        }

        public String getIsEvaluateRemind() {
            return isEvaluateRemind;
        }

        public void setIsEvaluateRemind(String isEvaluateRemind) {
            this.isEvaluateRemind = isEvaluateRemind;
        }

        public String getRemindTime() {
            return remindTime;
        }

        public void setRemindTime(String remindTime) {
            this.remindTime = remindTime;
        }

        public void setIsAutoProm(String isAutoProm) {
			this.isAutoProm = isAutoProm;
		}

		public String getIsAutoRegister() {
            return isAutoRegister;
        }

        public String getBeforOrder() {
            return beforOrder;
        }

        public void setBeforOrder(String beforOrder) {
            this.beforOrder = beforOrder;
        }

        public void setIsAutoRegister(String isAutoRegister) {
            this.isAutoRegister = isAutoRegister;
        }

        public String getCounterPay() {
			return counterPay;
		}

		public String getRegister() {
			return register;
		}

		public String getIsAutoFold() {
			return isAutoFold;
		}

		public void setIsAutoFold(String isAutoFold) {
			this.isAutoFold = isAutoFold;
		}

		public void setCounterPay(String counterPay) {
			this.counterPay = counterPay;
		}

		public void setRegister(String register) {
			this.register = register;
		}

		public String getRestrictRegister() {
			return restrictRegister;
		}

		public void setRestrictRegister(String restrictRegister) {
			this.restrictRegister = restrictRegister;
		}

		public String getRuleNo() {
			return ruleNo;
		}

		public String getRuleName() {
			return ruleName;
		}

		public String getScanType() {
			return scanType;
		}

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
        }
        //		public String getPackNo() {
//			return packNo;
//		}
//
//		public String getPackName() {
//			return packName;
//		}

		public String getRestrictTable() {
			return restrictTable;
		}

		public String getTableSet() {
			return tableSet;
		}

		public String getCheckType() {
			return checkType;
		}

//		public String getRestrictLike() {
//			return restrictLike;
//		}

		public String getRestrictAdvanceOrder() {
			return restrictAdvanceOrder;
		}

		public String getRetainTime() {
			return retainTime;
		}

		public String getQrCode() {
			return qrCode;
		}

		public String getLastModiOpId() {
			return lastModiOpId;
		}

		public String getLastModiOpName() {
			return lastModiOpName;
		}

		public String getStatus() {
			return status;
		}

//		public Invoice getInvoice() {
//			return invoice;
//		}

		public String getRecommendedDishes() {
			return recommendedDishes;
		}

		public String getDescription() {
			return description;
		}

		public String getRestrictShop() {
			return restrictShop;
		}

		public String getRestrictChannel() {
			return restrictChannel;
		}

		public void setRuleNo(String ruleNo) {
			this.ruleNo = ruleNo;
		}

		public void setRuleName(String ruleName) {
			this.ruleName = ruleName;
		}

		public void setScanType(String scanType) {
			this.scanType = scanType;
		}

//		public void setPackNo(String packNo) {
//			this.packNo = packNo;
//		}
//
//		public void setPackName(String packName) {
//			this.packName = packName;
//		}

		public void setRestrictTable(String restrictTable) {
			this.restrictTable = restrictTable;
		}

		public void setTableSet(String tableSet) {
			this.tableSet = tableSet;
		}

		public void setCheckType(String checkType) {
			this.checkType = checkType;
		}

//		public void setRestrictLike(String restrictLike) {
//			this.restrictLike = restrictLike;
//		}

		public void setRestrictAdvanceOrder(String restrictAdvanceOrder) {
			this.restrictAdvanceOrder = restrictAdvanceOrder;
		}

		public void setRetainTime(String retainTime) {
			this.retainTime = retainTime;
		}

		public void setQrCode(String qrCode) {
			this.qrCode = qrCode;
		}

		public void setLastModiOpId(String lastModiOpId) {
			this.lastModiOpId = lastModiOpId;
		}

		public void setLastModiOpName(String lastModiOpName) {
			this.lastModiOpName = lastModiOpName;
		}

		public void setStatus(String status) {
			this.status = status;
		}

//		public void setInvoice(Invoice invoice) {
//			this.invoice = invoice;
//		}

		public void setRecommendedDishes(String recommendedDishes) {
			this.recommendedDishes = recommendedDishes;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}

		public void setRestrictChannel(String restrictChannel) {
			this.restrictChannel = restrictChannel;
		}

		public List<RangeList> getRangeList() {
			return rangeList;
		}

		public void setRangeList(List<RangeList> rangeList) {
			this.rangeList = rangeList;
		}

		public String getTakeAway() {
			return takeAway;
		}

		public void setTakeAway(String takeAway) {
			this.takeAway = takeAway;
		}

		public String getCoupon() {
			return coupon;
		}

		public void setCoupon(String coupon) {
			this.coupon = coupon;
		}

		public String getIntegral() {
			return integral;
		}

		public void setIntegral(String integral) {
			this.integral = integral;
		}

		public String getFirstRegister() {
			return firstRegister;
		}

		public void setFirstRegister(String firstRegister) {
			this.firstRegister = firstRegister;
		}

		public String getOrderMemo() {
			return orderMemo;
		}

		public void setOrderMemo(String orderMemo) {
			this.orderMemo = orderMemo;
		}

//		private String restrictCheck;
//		private String restrictOrder;
//		private String printMode;


        public List<PaySet> getPaySet() {
            return paySet;
        }

        public void setPaySet(List<PaySet> paySet) {
            this.paySet = paySet;
        }
		
		public String getEvaluation() {
			return evaluation;
		}
		
		public void setEvaluation(String evaluation) {
			this.evaluation = evaluation;
		}
		
		public String getIsGoodsDetailDisplay() {
			return isGoodsDetailDisplay;
		}
		
		public void setIsGoodsDetailDisplay(String isGoodsDetailDisplay) {
			this.isGoodsDetailDisplay = isGoodsDetailDisplay;
		}
	}

//	public class Invoice
//	{
//		private String isinvoice;//是否开启发票，0-否 1-是
//		private String area;//0-大陆 1-台湾
//		private String reservedInvoiceinfo;//预留发票信息，0-否 1-是
//		private String orderInvoice;//下单开票，0-否，1-是
//		private String memberCarrier;//会员载具，0-否 1-是
//		public String getIsinvoice() {
//			return isinvoice;
//		}
//		public void setIsinvoice(String isinvoice) {
//			this.isinvoice = isinvoice;
//		}
//		public String getArea() {
//			return area;
//		}
//		public void setArea(String area) {
//			this.area = area;
//		}
//		public String getReservedInvoiceinfo() {
//			return reservedInvoiceinfo;
//		}
//		public void setReservedInvoiceinfo(String reservedInvoiceinfo) {
//			this.reservedInvoiceinfo = reservedInvoiceinfo;
//		}
//		public String getOrderInvoice() {
//			return orderInvoice;
//		}
//		public void setOrderInvoice(String orderInvoice) {
//			this.orderInvoice = orderInvoice;
//		}
//		public String getMemberCarrier() {
//			return memberCarrier;
//		}
//		public void setMemberCarrier(String memberCarrier) {
//			this.memberCarrier = memberCarrier;
//		}
//	}

	public class RangeList{
		
		private String rangeType; //适用范围：1-公司2-门店3-渠道
		private String id; //编码	
		private String name;//名称
		
		public String getRangeType() {
			return rangeType;
		}
		public String getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public void setRangeType(String rangeType) {
			this.rangeType = rangeType;
		}
		public void setId(String id) {
			this.id = id;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}

	public class PaySet{
	    private String sortId; // 显示顺序，按顺序从小到大返回
        private String payType; // 款别编码
        private String payName; // 显示名称

        public String getSortId() {
            return sortId;
        }

        public void setSortId(String sortId) {
            this.sortId = sortId;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getPayName() {
            return payName;
        }

        public void setPayName(String payName) {
            this.payName = payName;
        }
    }


	
}
