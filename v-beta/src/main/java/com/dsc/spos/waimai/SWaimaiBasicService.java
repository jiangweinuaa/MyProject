package com.dsc.spos.waimai;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;

public abstract class SWaimaiBasicService  {
	protected DsmDAO dao;
	private String key="";
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	public abstract String execute(String json) throws Exception;
	
	public void setDao(DsmDAO dao) {
		this.dao = dao;
	}

	
	public boolean needTokenVerify() {
		return Boolean.TRUE;
	}
	
	protected Map<String, Object> processJson(String req) throws Exception 
	{
		Map<String, Object> res = new HashMap<String, Object>();
		
		try 
		{		
			this.pData.clear(); //清空之前残留
			
			this.processDUID(req, res);
			
		} 
 		catch (Exception e) 
		{
			this.pData.clear(); //清空异常残留
			
			StringWriter errors = new StringWriter();
			PrintWriter pw=new PrintWriter(errors);
			e.printStackTrace(pw);			
			
			pw.flush();
			pw.close();			
			
			errors.flush();
			errors.close();
			
			//logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******processDUID方法执行异常：" + e.getMessage()+"\r\n" +errors.toString()+"******\r\n");
			
			pw=null;
			errors=null;
			
			throw e;
		}	
		
		this.doExecuteDataToDB(); //將資料寫到 DB
		return res;
	}

	
	protected void processRow(String row) throws Exception {
	}

	
	protected String getQuerySql(String req) throws Exception {
		return null;
	}


	protected abstract void processDUID(String req, Map<String, Object> res) throws Exception;

	protected abstract List<InsBean> prepareInsertData(String req) throws Exception;

	protected abstract List<UptBean> prepareUpdateData(String req) throws Exception;

	protected abstract List<DelBean> prepareDeleteData(String req) throws Exception;

	/**
	 * 取得 response.
	 * @return
	 * @throws Exception
	 */
	protected Map<String, Object> getResponse() throws Exception {
		Map<String, Object> res = new HashMap<String, Object>();
		return res;
	}

	/**
	 * 新增要處理的資料(先加入的, 先處理)
	 * @param row
	 */
	protected final void addProcessData(DataProcessBean row) {
		this.pData.add(row);
	}
	
	/**
	 * 查詢資料
	 * @param sql
	 * @param conditionValues
	 * @return
	 * @throws Exception
	 */
	protected List<Map<String, Object>> doQueryData(String sql, String[] conditionValues) throws Exception {
		return this.dao.executeQuerySQL(sql, conditionValues);		
	}

	
	
	/**
	 * into data to db
	 * @throws Exception
	 */
	protected void doExecuteDataToDB() throws Exception {
		if (this.pData.size() == 0) {
			return;
		}
		this.dao.useTransactionProcessData(this.pData);
		this.pData.clear(); //清空
	}
	
	/**
	 * 執行新增
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected void doInsert(String req) throws Exception {
		List<InsBean> data = this.prepareInsertData(req);
//		this.doInsert(data);
		for (InsBean ib : data) {
			for (DataValue[] row : ib.getValues()) {
				if (ib.getColumns().length != row.length) {
					throw new Exception("insert的栏位数与资料栏位数不一致!");
				}
			}
			this.addProcessData(new DataProcessBean(ib));
		}
	}
	
	/**
	 * 更新資料
	 * @param req
	 * @throws Exception
	 */
	protected void doUpdate(String req) throws Exception {
		List<UptBean> data = this.prepareUpdateData(req);
		for(UptBean ub : data) {
//			this.dao.update(ub.getTableName(), ub.getUpdateValues(), ub.getConditions());
			this.addProcessData(new DataProcessBean(ub));
		}	
	}
	
	
	/**
	 * 刪除資料
	 * @param req
	 * @throws Exception
	 */
	protected void doDelete(String req) throws Exception {
		List<DelBean> data = this.prepareDeleteData(req);
		for (DelBean db : data) {
//			this.dao.doDelete(db.getTableName(), db.getConditions());
			this.addProcessData(new DataProcessBean(db));
		}
	}

	protected int getInt(String num) throws Exception {
		if (num == null) {
			return 0;
		} else if (num.trim().length() == 0) {
			return 0;
		} else {
			return Integer.parseInt(num);	
		}
	}

	protected double getDouble(String num) throws Exception {
		if (num == null) {
			return 0;
		} else if (num.trim().length() == 0) {
			return 0;
		} else {
			return Double.parseDouble(num);			
		}
	}

}
