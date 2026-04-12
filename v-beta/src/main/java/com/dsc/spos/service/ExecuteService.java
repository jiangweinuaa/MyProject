package com.dsc.spos.service;

import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.JsonReq;

public interface ExecuteService {
	
	/**
	 * 取得執行結果
	 * @param json 前端傳入的資料
	 * @return
	 */
	public String execute(String json) throws Exception;

	/**
	 * 設定
	 * @param dao
	 */
	public void setDao(DsmDAO dao);
	
	/**
	 * 是否要經過 token 的驗証
	 * @return
	 */
	public boolean needTokenVerify();
	


	
}
