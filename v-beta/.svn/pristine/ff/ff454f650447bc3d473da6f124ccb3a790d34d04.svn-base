package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PowerShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_PowerShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：PowerShopGet
 *    說明：门店查询
 * 服务说明：门店查询
 * @author ycl
 * @since  2017-03-09
 */
public class DCP_PowerShopQuery extends SPosBasicService<DCP_PowerShopQueryReq, DCP_PowerShopQueryRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_PowerShopQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String getType = req.getRequest().getGetType();

		if(Check.Null(getType)){
			errCt++;
			errMsg.append("查询分类不可为空值, ");
			isFail = true;
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PowerShopQueryReq> getRequestType() {
		return new TypeToken<DCP_PowerShopQueryReq>(){};
	}

	@Override
	protected DCP_PowerShopQueryRes getResponseType() {
		return new DCP_PowerShopQueryRes();
	}

	@Override
	protected DCP_PowerShopQueryRes processJson(DCP_PowerShopQueryReq req) throws Exception {
		//取得 SQL
		String sql = null;
		String getType=req.getRequest().getGetType();

		//查詢條件
		String eId = req.geteId();;
		String opNO = req.getOpNO();
		String langType = req.getLangType();
		String keyTxt = req.getRequest().getKeyTxt();

		//查詢資料
		DCP_PowerShopQueryRes res = null;
		res = this.getResponse();

		//给分页字段赋值
		if(getType.equals("1"))//1:分页
		{
			sql = this.getQuerySql_Count(req);			//查询总笔数
			String[] conditionValues_Count = {langType,eId,opNO}; 			//查詢條件
			List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQData_Count != null && getQData_Count.isEmpty() == false) { 
				Map<String, Object> oneData2 = getQData_Count.get(0);
				String num = oneData2.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			}else{
				totalRecords = 0;
				totalPages = 0;
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

			//查詢資料
			sql = this.getQuerySql1(req);

			String[] conditionValues1 = {langType,eId,opNO}; 		//查詢條件	
			List<Map<String, Object>> getQData1 = this.doQueryData(sql, conditionValues1);

			if (getQData1 != null && getQData1.isEmpty() == false) { // 有資料，取得詳細內容
				res.setDatas(new ArrayList<DCP_PowerShopQueryRes.level1Elm>());
				for (Map<String, Object> oneData1 : getQData1) {
					DCP_PowerShopQueryRes.level1Elm oneLv1 = res.new level1Elm();

					// 取得第一層資料庫搜尋結果
					String shopId = oneData1.get("SHOPID").toString();
					String shopName = oneData1.get("SHOPNAME").toString();
					String telephone = oneData1.get("TELEPHONE").toString();
					String address = oneData1.get("ADDRESS").toString();

					String in_cost_warehouse = oneData1.get("IN_COST_WAREHOUSE").toString();
					String in_non_cost_warehouse = oneData1.get("IN_NON_COST_WAREHOUSE").toString();
					String out_cost_warehouse = oneData1.get("OUT_COST_WAREHOUSE").toString();
					String out_non_cost_warehouse = oneData1.get("OUT_NON_COST_WAREHOUSE").toString();
					String inv_cost_warehouse = oneData1.get("INV_COST_WAREHOUSE").toString();
					String inv_non_cost_warehouse = oneData1.get("INV_NON_COST_WAREHOUSE").toString();

					String in_cost_warehouseName = oneData1.get("IN_COST_WAREHOUSENAME").toString();
					String in_non_cost_warehouseName = oneData1.get("IN_NON_COST_WAREHOUSENAME").toString();
					String out_cost_warehouseName = oneData1.get("OUT_COST_WAREHOUSENAME").toString();
					String out_non_cost_warehouseName = oneData1.get("OUT_NON_COST_WAREHOUSENAME").toString();
					String inv_cost_warehouseName = oneData1.get("INV_COST_WAREHOUSENAME").toString();
					String inv_non_cost_warehouseName = oneData1.get("INV_NON_COST_WAREHOUSENAME").toString();

					// 處理調整回傳值；
					oneLv1.setShopId(shopId);
					oneLv1.setShopName(shopName);
					oneLv1.setTelephone(telephone);
					oneLv1.setAddress(address);

					oneLv1.setIn_cost_warehouse(in_cost_warehouse);
					oneLv1.setIn_cost_warehouse_name(in_cost_warehouseName);
					oneLv1.setIn_non_cost_warehouse(in_non_cost_warehouse);
					oneLv1.setIn_non_cost_warehouse_name(in_non_cost_warehouseName);
					oneLv1.setOut_cost_warehouse(out_cost_warehouse);
					oneLv1.setOut_cost_warehouse_name(out_cost_warehouseName);
					oneLv1.setOut_non_cost_warehouse(out_non_cost_warehouse);
					oneLv1.setOut_non_cost_warehouse_name(out_non_cost_warehouseName);
					oneLv1.setInv_cost_warehouse(inv_cost_warehouse);
					oneLv1.setInv_cost_warehouse_name(inv_cost_warehouseName);
					oneLv1.setInv_non_cost_warehouse(inv_non_cost_warehouse);
					oneLv1.setInv_non_cost_warehouse_name(inv_non_cost_warehouseName);

					res.getDatas().add(oneLv1);
					oneLv1=null;
				}			
			}
			else{
				res.setDatas(new ArrayList<DCP_PowerShopQueryRes.level1Elm>());
			}
		}

		if(getType.equals("2"))//2:不分页
		{	    
			//查詢資料
			sql = this.getQuerySql2(req);

			String[] conditionValues1 = {langType,eId,opNO}; 		//查詢條件	
			List<Map<String, Object>> getQData1 = this.doQueryData(sql, conditionValues1);

			if (getQData1 != null && getQData1.isEmpty() == false) { // 有資料，取得詳細內容
				res.setDatas(new ArrayList<DCP_PowerShopQueryRes.level1Elm>());
				for (Map<String, Object> oneData1 : getQData1) {
					DCP_PowerShopQueryRes.level1Elm oneLv1 = res.new level1Elm();

					// 取得第一層資料庫搜尋結果
					String shopId = oneData1.get("SHOPID").toString();
					String shopName = oneData1.get("SHOPNAME").toString();
					String telephone = oneData1.get("TELEPHONE").toString();
					String address = oneData1.get("ADDRESS").toString();

					String in_cost_warehouse = oneData1.get("IN_COST_WAREHOUSE").toString();
					String in_non_cost_warehouse = oneData1.get("IN_NON_COST_WAREHOUSE").toString();
					String out_cost_warehouse = oneData1.get("OUT_COST_WAREHOUSE").toString();
					String out_non_cost_warehouse = oneData1.get("OUT_NON_COST_WAREHOUSE").toString();
					String inv_cost_warehouse = oneData1.get("INV_COST_WAREHOUSE").toString();
					String inv_non_cost_warehouse = oneData1.get("INV_NON_COST_WAREHOUSE").toString();

					String in_cost_warehouseName = oneData1.get("IN_COST_WAREHOUSENAME").toString();
					String in_non_cost_warehouseName = oneData1.get("IN_NON_COST_WAREHOUSENAME").toString();
					String out_cost_warehouseName = oneData1.get("OUT_COST_WAREHOUSENAME").toString();
					String out_non_cost_warehouseName = oneData1.get("OUT_NON_COST_WAREHOUSENAME").toString();
					String inv_cost_warehouseName = oneData1.get("INV_COST_WAREHOUSENAME").toString();
					String inv_non_cost_warehouseName = oneData1.get("INV_NON_COST_WAREHOUSENAME").toString();

					// 處理調整回傳值；
					oneLv1.setShopId(shopId);
					oneLv1.setShopName(shopName);
					oneLv1.setTelephone(telephone);
					oneLv1.setAddress(address);

					oneLv1.setIn_cost_warehouse(in_cost_warehouse);
					oneLv1.setIn_cost_warehouse_name(in_cost_warehouseName);
					oneLv1.setIn_non_cost_warehouse(in_non_cost_warehouse);
					oneLv1.setIn_non_cost_warehouse_name(in_non_cost_warehouseName);
					oneLv1.setOut_cost_warehouse(out_cost_warehouse);
					oneLv1.setOut_cost_warehouse_name(out_cost_warehouseName);
					oneLv1.setOut_non_cost_warehouse(out_non_cost_warehouse);
					oneLv1.setOut_non_cost_warehouse_name(out_non_cost_warehouseName);
					oneLv1.setInv_cost_warehouse(inv_cost_warehouse);
					oneLv1.setInv_cost_warehouse_name(inv_cost_warehouseName);
					oneLv1.setInv_non_cost_warehouse(inv_non_cost_warehouse);
					oneLv1.setInv_non_cost_warehouse_name(inv_non_cost_warehouseName);

					res.getDatas().add(oneLv1);
					oneLv1=null;
				}				
			}
			else{
				res.setDatas(new ArrayList<DCP_PowerShopQueryRes.level1Elm>());
			}
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		//調整查出來的資料
	}

	@Override
	protected String getQuerySql(DCP_PowerShopQueryReq req) throws Exception {

		return null;
	}

	protected String getQuerySql1(DCP_PowerShopQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		String keyTxt = req.getRequest().getKeyTxt();

		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		sqlbuf.append(""
				+ "SELECT rn,SHOPID,shopName,telephone,address,"
				+ " in_cost_warehouse,in_non_cost_warehouse, out_cost_warehouse,out_non_cost_warehouse,inv_cost_warehouse,inv_non_cost_warehouse, "
				+ " in_cost_warehouseName,in_non_cost_warehouseName, out_cost_warehouseName,out_non_cost_warehouseName,inv_cost_warehouseName,inv_non_cost_warehouseName "
				+ " from ("
				+ " SELECT ROWNUM rn,SHOPID,shopName,phone,address,"
				+ " in_cost_warehouse,in_non_cost_warehouse, out_cost_warehouse,out_non_cost_warehouse,inv_cost_warehouse,inv_non_cost_warehouse,"
				+ " in_cost_warehouseName,in_non_cost_warehouseName, out_cost_warehouseName,out_non_cost_warehouseName,inv_cost_warehouseName,inv_non_cost_warehouseName "
				+ " from ("
				+ " select distinct a.SHOPID,c.ORG_NAME as shopName,b.phone,b.address,"
				+ " B.in_cost_warehouse,B.in_non_cost_warehouse,B.out_cost_warehouse,B.out_non_cost_warehouse, B.inv_cost_warehouse, B.inv_non_cost_warehouse,"
				+ " d.warehouse_name as in_cost_warehouseName, e.warehouse_name as in_non_cost_warehouseName, f.warehouse_name as out_cost_warehouseName,"
				+ " g.warehouse_name as out_non_cost_warehouseName,h.warehouse_name as inv_cost_warehouseName, i.warehouse_name as inv_non_cost_warehouseName "
				+ " from platform_staffs_shop A "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") B "			
				+ " left join DCP_WAREHOUSE_lang d on B.in_cost_warehouse=d.warehouse and a.EID=d.EID "
				+ " left join DCP_WAREHOUSE_lang e on B.in_non_cost_warehouse=e.warehouse and a.EID=e.EID "
				+ " left join DCP_WAREHOUSE_lang f on B.out_cost_warehouse=f.warehouse and a.EID=f.EID "
				+ " left join DCP_WAREHOUSE_lang g on B.out_non_cost_warehouse=g.warehouse and a.EID=g.EID "
				+ " left join DCP_WAREHOUSE_lang h on B.inv_cost_warehouse=h.warehouse and a.EID=h.EID "
				+ " left join DCP_WAREHOUSE_lang i on B.inv_non_cost_warehouse=i.warehouse and a.EID=i.EID "
				+ " WHERE B.status='100'  AND  C.status='100' AND c.lang_Type=?  AND a.EID=?"
				+ " AND A.OPNO=? " 
				//			+ " where 1=1"
				);

		if (keyTxt != null && keyTxt.length()!=0) { 		
			sqlbuf.append(""
					+ "AND (a.SHOPID like '%%"+ keyTxt +"%%'  "
					+ "OR c.shopname like '%%"+ keyTxt +"%%')   "
					);
		}
		sqlbuf.append(" order by SHOPID ");
		sqlbuf.append(" ) TBL ");
		sqlbuf.append(") where 1=1 AND (rn > " + startRow + " AND rn <= " + (startRow+pageSize) + " ) ");
		//sqlbuf.append(" limit  "+startRow+","+pageSize+" ");  MYSQL

		sql = sqlbuf.toString();

		return sql;
	}	

	protected String getQuerySql_Count(DCP_PowerShopQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append(""
				+ "SELECT num "
				+ " from ("
				+ " SELECT count(*) as num "
				+ " from ("
				+ " select distinct a.SHOPID,B.shopName,b.telephone,b.address "
				+ " from platform_staffs_shop A "
				+ " INNER JOIN "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") B "
				+ " WHERE B.lang_Type=?  AND a.EID=?"
				+ " AND A.OPNO=? " 
				//				+ " where 1=1"
				);				

		String keyTxt = req.getRequest().getKeyTxt();		
		if (keyTxt != null && keyTxt.length()!=0) { 		
			sqlbuf.append(""
					+ "AND (a.SHOPID like '%%"+ keyTxt +"%%'  "
					+ "OR  B.shopname like '%%"+ keyTxt +"%%')   "
					);
		}			
		sqlbuf.append(" ) ");
		sqlbuf.append(" ) TBL ");

		sql = sqlbuf.toString();

		return sql;
	}

	/**
	 * 不分页查询
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getQuerySql2(DCP_PowerShopQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		String keyTxt = req.getRequest().getKeyTxt();

		sqlbuf.append(""
				+ "SELECT rn,SHOPID,shopName,telephone,address, "
				+ " in_cost_warehouse,in_non_cost_warehouse, out_cost_warehouse,out_non_cost_warehouse,inv_cost_warehouse,inv_non_cost_warehouse, "
				+ " in_cost_warehouseName,in_non_cost_warehouseName, out_cost_warehouseName,out_non_cost_warehouseName,inv_cost_warehouseName,inv_non_cost_warehouseName "
				+ " from ("
				+ " SELECT ROWNUM rn,SHOPID,shopName,telephone,address, "
				+ " in_cost_warehouse,in_non_cost_warehouse, out_cost_warehouse,out_non_cost_warehouse,inv_cost_warehouse,inv_non_cost_warehouse,"
				+ " in_cost_warehouseName,in_non_cost_warehouseName, out_cost_warehouseName,out_non_cost_warehouseName,inv_cost_warehouseName,inv_non_cost_warehouseName "
				+ " from ("
				+ " select distinct a.SHOPID,B.shopName,b.telephone,b.address, "
				+ " B.in_cost_warehouse,B.in_non_cost_warehouse,B.out_cost_warehouse,B.out_non_cost_warehouse, B.inv_cost_warehouse, B.inv_non_cost_warehouse,"
				+ " d.warehouse_name as in_cost_warehouseName, e.warehouse_name as in_non_cost_warehouseName, f.warehouse_name as out_cost_warehouseName,"
				+ " g.warehouse_name as out_non_cost_warehouseName,h.warehouse_name as inv_cost_warehouseName, i.warehouse_name as inv_non_cost_warehouseName "
				+ " from platform_staffs_shop A "
				+ " INNER JOIN "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") B "
				+ " left join DCP_WAREHOUSE_lang d on B.in_cost_warehouse=d.warehouse and a.EID=d.EID "
				+ " left join DCP_WAREHOUSE_lang e on B.in_non_cost_warehouse=e.warehouse and a.EID=e.EID "
				+ " left join DCP_WAREHOUSE_lang f on B.out_cost_warehouse=f.warehouse and a.EID=f.EID "
				+ " left join DCP_WAREHOUSE_lang g on B.out_non_cost_warehouse=g.warehouse and a.EID=g.EID "
				+ " left join DCP_WAREHOUSE_lang h on B.inv_cost_warehouse=h.warehouse and a.EID=h.EID "
				+ " left join DCP_WAREHOUSE_lang i on B.inv_non_cost_warehouse=i.warehouse and a.EID=i.EID "
				+ " WHERE c.lang_Type=? AND a.EID=?"
				+ " AND A.OPNO=? " 
				//			+ " where 1=1"
				);

		if (keyTxt != null && keyTxt.length()!=0) { 		
			sqlbuf.append(""
					+ "AND (a.SHOPID like '%%"+ keyTxt +"%%'  "
					+ "OR  B.shopname like '%%"+ keyTxt +"%%')   "
					);
		}
		sqlbuf.append(" order by SHOPID ");
		sqlbuf.append(" ) TBL ");
		sqlbuf.append(") where 1=1 ");

		sql = sqlbuf.toString();

		return sql;
	}	
}
