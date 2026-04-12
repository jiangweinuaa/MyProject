package com.dsc.spos.waimai.dianwoda;

/**
 * 订单状态变更[消息]
 * @author Typingly
 *
 */
public class DianwodaMessageOrderStatusUpdate {

	/**
	 * 订单号
	 */
	private String order_original_id;
	
	/**
	 * 订单状态:
	 * created-已下单,dispatched-已接单,arrived-已到店,obtained-已离店,completed-已完成（完结）,
	 * abnormal-异常（完结）,canceled-已取消（完结）,
	 */
	private String order_status;
	
	/**
	 * 变更事件:
	 * rider_switch-骑手转单,rider_dispatch-骑手接单,rider_arrive-骑手到店,rider_obtain-骑手离店,customer_sign-货品送达
	 * customer_sign_abnormal-签收异常(还有不同类型事件原因码（action_sub_code）)	，
	 * rider_cancel-骑手取消订单 - 商家要求取消
	 * customerservice_cancel-点我达客服关闭订单 - 商家原因
	 * platform_cancel-点我达系统关闭订单（还有不同类型事件原因码（action_sub_code））
	 */
	private String action_code;	
	
	/**
	 * 变更事件原因码:
	 * customer_reject-签收异常 - 客户拒收,customer_not_connectable-签收异常 - 联系不到客户,other-签收异常 - 系统检测或其他特殊原因
	 * rider_at_merchant_request-骑手取消订单 - 商家要求取消,
	 * customerservice_at_merchant_request-点我达客服关闭订单 - 商家原因,
	 * platform_for_dispatch_timeout-点我达系统关闭订单 - 派单超时,platform_for_arrive_timeout-点我达系统关闭订单 - 长时间未到店,
	 * platform_for_obtain_timeout,点我达系统关闭订单 - 长时间未离店,platform_for_complete_timeout点我达系统关闭订单 - 长时间未完成,
	 * other-点我达系统关闭订单 - 系统检测或其他特殊原因
	 * 
	 */
	private String action_sub_code;
	
	/**
	 * 变更事件说明
	 */
	private String action_detail;
	
	/**
	 * 配送员编号
	 */
	private String rider_code;
	
	/**
	 * 配送员姓名
	 */
	private String rider_name;
	
	/**
	 * 配送员手机号
	 */
	private String rider_mobile;
	
	/**
	 * 配送状态更新时间戳
	 */
	private Long time_status_update;

	public String getOrder_original_id() {
		return order_original_id;
	}

	public void setOrder_original_id(String order_original_id) {
		this.order_original_id = order_original_id;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getAction_code() {
		return action_code;
	}

	public void setAction_code(String action_code) {
		this.action_code = action_code;
	}

	public String getAction_sub_code() {
		return action_sub_code;
	}

	public void setAction_sub_code(String action_sub_code) {
		this.action_sub_code = action_sub_code;
	}

	public String getAction_detail() {
		return action_detail;
	}

	public void setAction_detail(String action_detail) {
		this.action_detail = action_detail;
	}

	public String getRider_code() {
		return rider_code;
	}

	public void setRider_code(String rider_code) {
		this.rider_code = rider_code;
	}

	public String getRider_name() {
		return rider_name;
	}

	public void setRider_name(String rider_name) {
		this.rider_name = rider_name;
	}

	public String getRider_mobile() {
		return rider_mobile;
	}

	public void setRider_mobile(String rider_mobile) {
		this.rider_mobile = rider_mobile;
	}

	public Long getTime_status_update() {
		return time_status_update;
	}

	public void setTime_status_update(Long time_status_update) {
		this.time_status_update = time_status_update;
	}
	
	
	
	
}
