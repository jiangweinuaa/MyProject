package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_UpErrorLogQueryReq;
import com.dsc.spos.json.cust.res.DCP_UpErrorLogQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * UpErrorLogGet 專用的 response json
 *   說明：门店上传异常查询
 * 服务说明：门店上传异常查询
 * @author panjing 
 * @since  2016-12-13
 */
public class DCP_UpErrorLogQuery extends SPosAdvanceService<DCP_UpErrorLogQueryReq, DCP_UpErrorLogQueryRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_UpErrorLogQueryReq req) throws Exception {
		boolean isFail = false;
		return isFail;
	}

	@Override
	protected TypeToken<DCP_UpErrorLogQueryReq> getRequestType() {
		return new TypeToken<DCP_UpErrorLogQueryReq>(){};
	}

	@Override
	protected DCP_UpErrorLogQueryRes getResponseType() {
		return new DCP_UpErrorLogQueryRes();
	}

	@Override
	protected void processDUID(DCP_UpErrorLogQueryReq req,DCP_UpErrorLogQueryRes res) throws Exception {		
		res.setUpErrorLogDatas(new ArrayList<DCP_UpErrorLogQueryRes.level2Elm>());

		String sql = this.getQuerySql_Check01(req);			
		String[] conditionValues1 = {}; 
		List<Map<String, Object>> getQData1 = this.doQueryData(sql,conditionValues1);

		if (getQData1 != null && getQData1.isEmpty() == false) {
			for (Map<String, Object> oneData1 : getQData1) {
				DCP_UpErrorLogQueryRes.level2Elm oneLv2 = res.new level2Elm();
				String shopId = oneData1.get("ORGANIZATIONNO").toString();
				String doc_type = oneData1.get("DOCTYPE").toString();
				String doc_date = oneData1.get("BDATE").toString();
				String doc_no = oneData1.get("DOCNO").toString(); 	
				String first_up_date = oneData1.get("FIRSTUPDATE").toString();  	
				String first_up_time = oneData1.get("FIRSTUPTIME").toString(); 	
				String last_up_date = oneData1.get("LASTUPDATE").toString();	
				String last_up_time = oneData1.get("LASTUPTIME").toString(); 	
				//String service_type = oneData1.get("").toString(); 	
				String service_name = oneData1.get("SERVICENAME").toString();	
				String transfer_shop = oneData1.get("TRANSFERSHOP").toString();	
				String error_code = oneData1.get("ERRORCODE").toString();	
				String error_msg = oneData1.get("ERRORMSG").toString();	
				String creator = oneData1.get("CREATEBY").toString();
				String create_date = oneData1.get("CREATEDATE").toString();
				String create_time = oneData1.get("CREATETIME").toString();
				//String seq = oneData1.get("ITEM").toString();
				//String source_seq = oneData1.get("OITEM").toString();
				//String item_no = oneData1.get("PLUNO").toString();
				//String item_feature_no = oneData1.get("FEATURENO").toString();
				//String inventory_unit = oneData1.get("WUNIT").toString();
				//String unit_no = oneData1.get("PUNIT").toString();
				//String p_qty = oneData1.get("PQTY").toString();
				//String qty = oneData1.get("WQTY").toString();
				//String price = oneData1.get("PRICE").toString();
				//String amount = oneData1.get("AMT").toString();

				String response_xml="" ;
				if  (oneData1.get("RESPONSEXML")==null || Check.Null(oneData1.get("RESPONSEXML").toString())   )   
				{
					response_xml="" ;
				}
				else
				{
					response_xml = oneData1.get("RESPONSEXML").toString();
				}

				oneLv2.setShopId(shopId);
				oneLv2.setDoc_type(doc_type);
				oneLv2.setDoc_date(doc_date);
				oneLv2.setDoc_no(doc_no);
				oneLv2.setFirst_up_date(first_up_date);
				oneLv2.setFirst_up_time(first_up_time);
				oneLv2.setLast_up_date(last_up_date);
				oneLv2.setLast_up_time(last_up_time);
				//oneLv2.setService_type(service_type);
				oneLv2.setService_name(service_name);
				oneLv2.setResponse_xml(response_xml);
				oneLv2.setTransfer_shop(transfer_shop);
				oneLv2.setError_code(error_code);
				oneLv2.setError_msg(error_msg);
				oneLv2.setCreator(creator);
				oneLv2.setCreate_date(create_date);
				oneLv2.setCreate_time(create_time);

				//oneLv2.setSeq(seq);
				//oneLv2.setSource_seq(source_seq);
				//oneLv2.setItem_no(item_no);
				//oneLv2.setItem_feature_no(item_feature_no);
				//oneLv2.setInventory_unit(inventory_unit);
				//oneLv2.setUnit_no(unit_no);
				//oneLv2.setP_qty(p_qty);
				//oneLv2.setQty(qty);
				//oneLv2.setPrice(price);
				//oneLv2.setAmount(amount);

				res.getUpErrorLogDatas().add(oneLv2);
				
				oneLv2=null;

			}
		}	
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_UpErrorLogQueryReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_UpErrorLogQueryReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_UpErrorLogQueryReq req) throws Exception {

		return null;
	}

	@Override
	protected String getQuerySql(DCP_UpErrorLogQueryReq req) throws Exception {
		return null;
	}

	protected String getQuerySql_Check01(DCP_UpErrorLogQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		List<Map<String, String>> datesList = req.getDates();
		String dates = "";
		if (datesList != null && datesList.isEmpty() == false)
		{
			dates = getString(datesList);
		}

		List<Map<String, String>> shopsList = req.getShops();
		String shops = ""; 
		if (shopsList != null && shopsList.isEmpty() == false)
		{
			shops = getString(shopsList);
		}

		List<Map<String, String>> docTypesList = req.getDocTypes();
		String docTypes = "";
		if (docTypesList != null && docTypesList.isEmpty() == false)
		{
			docTypes=getString(docTypesList);
		}

		sqlbuf.append(""
				+ "select transferShop,EID,ORGANIZATIONNO,docType,bDate,docNO,firstUpDate,firstUpTime,serviceName,requsetXML,responseXML,errorCode,"
				+ " errorMsg,createDate,createTime,createBy,lastUpDate,lastUpTime "
				+ " FROM ( "
				+ " select transferShop,EID,ORGANIZATIONNO,docType,bDate,docNO,firstUpDate,firstUpTime,SERVICE_NAME AS serviceName,REQUEST_XML AS requsetXML,RESPONSE_XML AS responseXML,ERROR_CODE AS errorCode,"
				+ " ERROR_MSG AS errorMsg,CREATE_DATE as createDate,CREATE_TIME as createTime,createBy,lastUpDate,lastUpTime "
				+ " FROM ( " );

		sqlbuf.append(""
				+ "SELECT cast(N'' as nvarchar2(10)) as transferShop,A.EID,A.ORGANIZATIONNO,'1' as DOCTYPE,to_number(a.bdate) as bdate ,a.PORDERNO as DOCNO,A.submit_DATE as firstUpDate,A.submit_TIME AS firstUpTime,B.SERVICE_NAME,B.REQUEST_XML,B.RESPONSE_XML,"
				+ " B.ERROR_CODE,B.ERROR_MSG,B.CREATE_DATE,B.CREATE_TIME,a.createBy,nvl(B.MODIFY_DATE,A.submit_DATE) as lastUpDate,nvl(B.MODIFY_TIME,a.submit_time) as lastUpTime "
				+ " FROM DCP_PORDER A LEFT JOIN DCP_WSLOG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND A.PORDERNO=B.DOCNO "
				+ " WHERE A.STATUS='2' AND A.PROCESS_STATUS='N' AND B.SERVICE_TYPE='1' "
				);

		if (shops != "" || shops.length()>0) {
			sqlbuf.append("and A.SHOPID in ("+ shops +")");
		}
		if (dates != "" || dates.length()>0) {
			sqlbuf.append("and ( submit_date in ("+ dates +") OR B.CREATE_DATE in ("+ dates +") OR B.MODIFY_DATE in ("+ dates +") ) ");
		}

		sqlbuf.append(""
				+ " union all "
				+ "SELECT a.transfer_shop as transferShop,A.EID,A.ORGANIZATIONNO,'2' as DOCTYPE,to_number(a.bdate) as bdate ,a.STOCKOUTNO as DOCNO,A.account_Date as firstUpDate,A.account_time as firstUpTime,B.SERVICE_NAME,B.REQUEST_XML,"
				+ " B.RESPONSE_XML,B.ERROR_CODE,B.ERROR_MSG,B.CREATE_DATE,B.CREATE_TIME,a.createBy,nvl(B.MODIFY_DATE,A.account_DATE) as lastUpDate,nvl(B.MODIFY_TIME,a.account_time) as lastUpTime "
				+ " FROM DCP_STOCKOUT A LEFT JOIN DCP_WSLOG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO  AND A.STOCKOUTNO=B.DOCNO "
				+ " WHERE A.STATUS in('2','3') AND A.PROCESS_STATUS='N' AND B.SERVICE_TYPE='1' "
				);

		if (shops != "" || shops.length()>0) {
			sqlbuf.append("and A.SHOPID in ("+ shops +")");
		}
		if (dates != "" || dates.length()>0) {
			sqlbuf.append("and ( account_date in ("+ dates +") OR B.CREATE_DATE in ("+ dates +") OR B.MODIFY_DATE in ("+ dates +") ) ");
		}

		sqlbuf.append(""
				+ " union all "
				+ "SELECT a.transfer_shop as transferShop,A.EID,A.ORGANIZATIONNO,'3' as DOCTYPE,to_number(a.bdate) as bdate,a.STOCKINNO as DOCNO,A.account_Date as firstUpDate,A.account_time as firstUpTime,B.SERVICE_NAME,B.REQUEST_XML,B.RESPONSE_XML,"
				+ " B.ERROR_CODE,B.ERROR_MSG,B.CREATE_DATE,B.CREATE_TIME,a.createBy,nvl(B.MODIFY_DATE,A.account_DATE) as lastUpDate,nvl(B.MODIFY_TIME,a.account_time) as lastUpTime "
				+ " FROM DCP_STOCKIN A LEFT JOIN DCP_WSLOG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND A.STOCKINNO=B.DOCNO "
				+ " WHERE A.STATUS='2' AND A.PROCESS_STATUS='N' AND B.SERVICE_TYPE='1' "
				);

		if (shops != "" || shops.length()>0) {
			sqlbuf.append("and A.SHOPID in ("+ shops +")");
		}
		if (dates != "" || dates.length()>0) {
			sqlbuf.append("and ( account_date in ("+ dates +") OR B.CREATE_DATE in ("+ dates +") OR B.MODIFY_DATE in ("+ dates +") ) ");
		}

		sqlbuf.append(""
				+ " union all "
				+ "SELECT cast('' as nvarchar2(10)) as transferShop,A.EID,A.ORGANIZATIONNO,'4' as DOCTYPE,to_number(a.bdate) as bdate ,a.STOCKTAKENO as DOCNO,A.CONFIRM_DATE as firstUpDate,A.CONFIRM_TIME as firstUpTime,B.SERVICE_NAME,B.REQUEST_XML,B.RESPONSE_XML,"
				+ " B.ERROR_CODE,B.ERROR_MSG,B.CREATE_DATE,B.CREATE_TIME,a.createBy,nvl(B.MODIFY_DATE,A.confirm_DATE) as lastUpDate,nvl(B.MODIFY_TIME,a.confirm_time) as lastUpTime "
				+ " FROM DCP_STOCKTAKE A LEFT JOIN DCP_WSLOG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO  AND A.STOCKTAKENO=B.DOCNO "
				+ " WHERE A.STATUS in('2','3') AND A.PROCESS_STATUS='N' AND B.SERVICE_TYPE='1' "
				);

		if (shops != "" || shops.length()>0) {
			sqlbuf.append("and A.SHOPID in ("+ shops +")");
		}
		if (dates != "" || dates.length()>0) {
			sqlbuf.append("and ( confirm_date in ("+ dates +") OR B.CREATE_DATE in ("+ dates +") OR B.MODIFY_DATE in ("+ dates +") ) ");
		}

		////新增  5.自采入库单 6.自采退货单 7.完工入库单  9.报损单     By JZMA 20180116
		sqlbuf.append(""
				+ " union all "
				+ "SELECT cast('' as nvarchar2(10)) as transferShop,A.EID,A.ORGANIZATIONNO,'5' as DOCTYPE,to_number(a.bdate) as bdate,a.SSTOCKINNO as DOCNO,A.CONFIRM_DATE as firstUpDate,A.CONFIRM_TIME as firstUpTime,B.SERVICE_NAME,B.REQUEST_XML,B.RESPONSE_XML,"
				+ " B.ERROR_CODE,B.ERROR_MSG,B.CREATE_DATE,B.CREATE_TIME,a.createBy,nvl(B.MODIFY_DATE,A.confirm_DATE) as lastUpDate,nvl(B.MODIFY_TIME,a.confirm_time) as lastUpTime "
				+ " FROM DCP_SSTOCKIN A LEFT JOIN DCP_WSLOG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO  AND A.SSTOCKINNO=B.DOCNO "
				+ " WHERE A.STATUS in('2') AND A.PROCESS_STATUS='N' AND B.SERVICE_TYPE='1' "
				);

		if (shops != "" || shops.length()>0) {
			sqlbuf.append("and A.SHOPID in ("+ shops +")");
		}
		if (dates != "" || dates.length()>0) {
			sqlbuf.append("and ( confirm_date in ("+ dates +") OR B.CREATE_DATE in ("+ dates +") OR B.MODIFY_DATE in ("+ dates +") ) ");
		}

		sqlbuf.append(""
				+ " union all "
				+ "SELECT cast('' as nvarchar2(10)) as transferShop,A.EID,A.ORGANIZATIONNO,'6' as DOCTYPE,to_number(a.bdate) as bdate,a.SSTOCKOUTNO as DOCNO,A.CONFIRM_DATE as firstUpDate,A.CONFIRM_TIME as firstUpTime,B.SERVICE_NAME,B.REQUEST_XML,B.RESPONSE_XML,"
				+ " B.ERROR_CODE,B.ERROR_MSG,B.CREATE_DATE,B.CREATE_TIME,a.createBy,nvl(B.MODIFY_DATE,A.confirm_DATE) as lastUpDate,nvl(B.MODIFY_TIME,a.confirm_time) as lastUpTime "
				+ " FROM DCP_SSTOCKOUT A LEFT JOIN DCP_WSLOG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO  AND A.SSTOCKOUTNO=B.DOCNO "
				+ " WHERE A.STATUS in('2') AND A.PROCESS_STATUS='N' AND B.SERVICE_TYPE='1' "
				);

		if (shops != "" || shops.length()>0) {
			sqlbuf.append("and A.SHOPID in ("+ shops +")");
		}
		if (dates != "" || dates.length()>0) {
			sqlbuf.append("and ( confirm_date in ("+ dates +") OR B.CREATE_DATE in ("+ dates +") OR B.MODIFY_DATE in ("+ dates +") ) ");
		}

		sqlbuf.append(""
				+ " union all "
				+ "SELECT cast('' as nvarchar2(10)) as transferShop,A.EID,A.ORGANIZATIONNO,'7' as DOCTYPE,to_number(a.bdate) as bdate,a.PSTOCKINNO as DOCNO,A.CONFIRM_DATE as firstUpDate,A.CONFIRM_TIME as firstUpTime,B.SERVICE_NAME,B.REQUEST_XML,B.RESPONSE_XML,"
				+ " B.ERROR_CODE,B.ERROR_MSG,B.CREATE_DATE,B.CREATE_TIME,a.createBy,nvl(B.MODIFY_DATE,A.confirm_DATE) as lastUpDate,nvl(B.MODIFY_TIME,a.confirm_time) as lastUpTime "
				+ " FROM DCP_PSTOCKIN A LEFT JOIN DCP_WSLOG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO  AND A.PSTOCKINNO=B.DOCNO "
				+ " WHERE A.STATUS in('2') AND A.PROCESS_STATUS='N' AND B.SERVICE_TYPE='1' "
				);

		if (shops != "" || shops.length()>0) {
			sqlbuf.append("and A.SHOPID in ("+ shops +")");
		}
		if (dates != "" || dates.length()>0) {
			sqlbuf.append("and ( confirm_date in ("+ dates +") OR B.CREATE_DATE in ("+ dates +") OR B.MODIFY_DATE in ("+ dates +") ) ");
		}

		sqlbuf.append(""
				+ " union all "
				+ "SELECT cast('' as nvarchar2(10)) as transferShop,A.EID,A.ORGANIZATIONNO,'9' as DOCTYPE,to_number(a.bdate) as bdate,a.LSTOCKOUTNO as DOCNO,A.CONFIRM_DATE as firstUpDate,A.CONFIRM_TIME as firstUpTime,B.SERVICE_NAME,B.REQUEST_XML,B.RESPONSE_XML,"
				+ " B.ERROR_CODE,B.ERROR_MSG,B.CREATE_DATE,B.CREATE_TIME,a.createBy,nvl(B.MODIFY_DATE,A.confirm_DATE) as lastUpDate,nvl(B.MODIFY_TIME,a.confirm_time) as lastUpTime "
				+ " FROM DCP_LSTOCKOUT A LEFT JOIN DCP_WSLOG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO  AND A.LSTOCKOUTNO=B.DOCNO "
				+ " WHERE A.STATUS in('2') AND A.PROCESS_STATUS='N' AND B.SERVICE_TYPE='1' "
				);

		if (shops != "" || shops.length()>0) {
			sqlbuf.append("and A.SHOPID in ("+ shops +")");
		}
		if (dates != "" || dates.length()>0) {
			sqlbuf.append("and ( confirm_date in ("+ dates +") OR B.CREATE_DATE in ("+ dates +") OR B.MODIFY_DATE in ("+ dates +") ) ");
		}

		sqlbuf.append(")"); 		        
		if (docTypes != "" || docTypes.length()>0) {
			sqlbuf.append("  WHERE DOCTYPE IN ("+docTypes+")");
		}

		sqlbuf.append(" order by bdate desc,SERVICE_NAME "); 
		sqlbuf.append(" ) TBL ");

		if (sqlbuf.length() > 0)
			sql = sqlbuf.toString();

		return sql;
	}	

	protected String getString(List<Map<String, String>> list){
		String str = "";

		for (Map<String, String> list1:list){
			if (list1.get("getDate") != null && list1.get("getDate").length()>0) str = str + list1.get("getDate") + "','";		
			if (list1.get("shopId") != null && list1.get("shopId").length()>0) str = str + list1.get("shopId") + "','";
			if (list1.get("docType") != null && list1.get("docType").length()>0) str = str + list1.get("docType") + "','";
		}
		if (str.length()>0){
			str = "'" + str;
			str=str.substring(0,str.length()-2);
		}

		//System.out.println(str);

		return str;
	}
}
