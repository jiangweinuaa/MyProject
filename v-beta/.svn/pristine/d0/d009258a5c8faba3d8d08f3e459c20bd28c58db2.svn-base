package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SaleRecordListQueryReq;
import com.dsc.spos.json.cust.res.DCP_SaleRecordListQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 面销记录
 * @author yuanyy 
 *
 */
public class DCP_SaleRecordListQuery extends SPosBasicService<DCP_SaleRecordListQueryReq, DCP_SaleRecordListQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SaleRecordListQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SaleRecordListQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleRecordListQueryReq>(){};
	}

	@Override
	protected DCP_SaleRecordListQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleRecordListQueryRes();
	}

	@Override
	protected DCP_SaleRecordListQueryRes processJson(DCP_SaleRecordListQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_SaleRecordListQueryRes res = null;
		res = this.getResponse();
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_SaleRecordListQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> map : queryDatas) {
					
					DCP_SaleRecordListQueryRes.level1Elm lv1= res.new level1Elm() ;
					String salesRecordNo = map.get("SALESRECORDNO").toString();
					String customName = map.get("CUSTOMNAME").toString();
					String sex = map.get("SEX").toString();
					String mobile = map.get("MOBILE").toString();
					String customType = map.get("CUSTOMTYPE").toString();
					String receptionTime = map.get("RECEPTIONTIME").toString();
					String labels = map.get("LABELS").toString();
					String createTime = map.get("CREATETIME").toString();
					String noteKeeperName = map.get("NOTEKEEPERNAME").toString();
					String remark = map.getOrDefault("REMARK", "").toString();
					lv1.setSalesRecordNo(salesRecordNo);
					lv1.setCustomName(customName);
					lv1.setSex(sex);
					lv1.setMobile(mobile);
					lv1.setCustomType(customType);
					lv1.setReceptionTime(receptionTime);
					lv1.setLabels(labels);
					lv1.setCreateTime(createTime);
					lv1.setNotekeeperName(noteKeeperName);
					lv1.setRemark(remark);
					
					res.getDatas().add(lv1);
					
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！");
			res.setServiceStatus("200");
			res.setSuccess(false);
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getQuerySql(DCP_SaleRecordListQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		String langType = req.getLangType();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String opNo = req.getOpNO();
		
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String keyTxt = req.getRequest().getKeyTxt();
		String labels = req.getRequest().getLabels();
		
		//查询门店当前登陆人员的面销记录单号
		sqlbuf.append(" SELECT * FROM ( "
			+ " SELECT count(*) OVER() AS NUM,  row_number() over (order BY  a.salesRecordNo ) rn, a.billDate , "
			+ " a.salesRecordNo , a.customName, a.Sex  ,  a.mobile , a.customType,  a.receptiontime , a.remark ,"
			+ " a.labels , a.opNo , "
			+ "  to_char( a.createtime , 'yyyy-MM-dd hh24:mi:ss' ) AS createtime , "
			+ "  to_char( a.lastmoditime , 'yyyy-MM-dd hh24:mi:ss' ) AS lastmoditime, "
			+ " b.op_name AS noteKeeperName "
			+ " FROM DCP_SALESRECORD a  "
			+ " LEFT JOIN platform_staffs_lang b ON a.Eid = b.EID AND a.opno = b.opno AND b.status='100' AND b.lang_type = '"+langType+"' "
			+ " WHERE a.eid = '"+eId+"'  "
			+ " and a.shopId = '"+shopId+"' and a.opNo = '"+opNo+"' " );
		
		if (!Check.Null(beginDate) && !Check.Null(endDate))
		{
			sqlbuf.append(" and a.billDate between to_Date('"+beginDate+"', 'yyyy-MM-dd') and  to_Date('"+endDate+"','yyyy-MM-dd') ");
		}
			
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and ( a.salesRecordNo like '%%"+keyTxt+"%%' or a.customName like '%%"+keyTxt+"%%' "
					+ " or a.mobile like '%%"+keyTxt+"%%' or a.remark like '%%"+keyTxt+"%%') ");
		}
		
		if (!Check.Null(labels))
		{
			sqlbuf.append(" and   a.labels like '%%"+labels+"%%'   ");
		}
		
		sqlbuf.append( " ORDER BY a.salesrecordno DESC "
			+ " ) t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize) 
			+ " ORDER BY  salesrecordno  DESC "
				);
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
