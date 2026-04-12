package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 外卖菜单修改
 * @author Typingly
 *
 */
public class DCP_OrderMenuUpdateReq extends JsonBasicReq 
{
	private String menuID;
	private String menuName;
	private String menuDescription;
	private String menuMemo;
	
	public String getMenuID() {
		return menuID;
	}
	public void setMenuID(String menuID) {
		this.menuID = menuID;
	}
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
	
}
