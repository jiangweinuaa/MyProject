package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 外卖菜单查询
 * @author Typingly
 *
 */
public class DCP_OrderMenuCreateReq extends JsonBasicReq 
{
	/**
	 * 操作类型，0-新建空白菜单、1-复制连锁菜单
	 */
	private String operateType;
	private String menuName;
	private String menuDescription;
	private String menuMemo;
	private String originMenuID;//复制时，来源的菜单ID
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getMenuDescription() {
		return menuDescription;
	}
	public void setMenuDescription(String menuDescription) {
		this.menuDescription = menuDescription;
	}
	public String getMenuMemo() {
		return menuMemo;
	}
	public void setMenuMemo(String menuMemo) {
		this.menuMemo = menuMemo;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public String getOriginMenuID() {
		return originMenuID;
	}
	public void setOriginMenuID(String originMenuID) {
		this.originMenuID = originMenuID;
	}
	
}
