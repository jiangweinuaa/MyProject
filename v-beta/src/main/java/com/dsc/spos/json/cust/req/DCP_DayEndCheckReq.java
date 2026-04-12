package com.dsc.spos.json.cust.req;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DayEndCheckReq
 *   說明：收货通知单新增
 * 服务说明：收货通知单新增
 * @author chensong
 * @since  2016-10-12
 */
public class DCP_DayEndCheckReq extends JsonBasicReq{

	private List<Map<String, String>> dayEndShops;

	public List<Map<String, String>> getDayEndShops() {
		return dayEndShops;
	}
	public void setDayEndShops(List<Map<String, String>> dayEndShops) {
		this.dayEndShops = dayEndShops;
	}
}
