package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PayMentNRCQueryReq;
import com.dsc.spos.json.cust.res.DCP_PGoodsClassQueryRes;
import com.dsc.spos.json.cust.res.DCP_PayMentNRCQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import com.spreada.utils.chinese.ZHConverter;
/**
 * 云中台与CRM支付方式映射查询
 * @author yuanyy 2019-08-27
 *
 */
public class DCP_PayMentNRCQuery extends SPosBasicService<DCP_PayMentNRCQueryReq, DCP_PayMentNRCQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PayMentNRCQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PayMentNRCQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PayMentNRCQueryReq>(){};
	}

	@Override
	protected DCP_PayMentNRCQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PayMentNRCQueryRes();
	}

	@Override
	protected DCP_PayMentNRCQueryRes processJson(DCP_PayMentNRCQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PayMentNRCQueryRes res = this.getResponse();
		String sql = "";
		try {
			sql = this.getQuerySql(req);
			List<Map<String, Object>> allDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_PayMentNRCQueryRes.level1Elm>());
			
			int totalRecords = 0;								//总笔数
			int totalPages = 0;									//总页数
			if (allDatas != null && allDatas.isEmpty() == false) 
			{
				String num = allDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			if(allDatas.size() > 0){
				for (Map<String, Object> map : allDatas) {
					DCP_PayMentNRCQueryRes.level1Elm lv1 = res.new level1Elm();
					String platformPayCode = map.get("PLATFORMPAYCODE").toString();
					String platformPayName = map.get("PLATFORMPAYNAME").toString(); 
					if(req.getLangType().equals("zh_TW"))
					{
						platformPayName =ZHConverter.convert(platformPayName,0);
					}
					String crmPayCode = map.get("CRMPAYCODE").toString();
					String crmPayName = map.get("CRMPAYNAME").toString();
					if(req.getLangType().equals("zh_TW"))
					{
						 crmPayName =ZHConverter.convert(crmPayName,0); 
					}
					String priority = map.get("PRIORITY").toString();
					String status = map.get("STATUS").toString(); 
					
					String createBy = map.get("CREATEBY").toString();
					String createByName = map.get("CREATEBYNAME").toString(); 
					String createTime = map.get("CREATETIME").toString(); 
					
					String updateBy = map.get("UPDATEBY").toString();
					String updateByName = map.get("UPDATEBYNAME").toString(); 
					String updateTime = map.get("UPDATETIME").toString(); 
					
					String onSale = map.getOrDefault("ONSALE","Y").toString();
					
					lv1.setPlatformPayCode(platformPayCode);
					lv1.setPlatformPayName(platformPayName);
					lv1.setCrmPayCode(crmPayCode);
					lv1.setCrmPayName(crmPayName);
					lv1.setPriority(priority);
					lv1.setStatus(status);
					lv1.setCreateBy(createBy);
					lv1.setCreateByName(createByName);
					lv1.setCreateTime(createTime);
					lv1.setUpdateBy(updateBy);
					lv1.setUpdateByName(updateByName);
					lv1.setUpdateTime(updateTime);
					lv1.setOnSale(onSale);
					
					res.getDatas().add(lv1);
					lv1=null;
				}
				res.setSuccess(true);
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PayMentNRCQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String status = req.getStatus();
		String keyTxt = req.getKeyTxt();
		String eId = req.geteId();
		String langType = req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append(" SELECT * from ( "
				+ " select count(*) over() num, dense_rank() OVER( ORDER BY  priority , a.platform_paycode , a.crm_paycode) rn ,"
				+ " platform_payCode AS platformPayCode , platform_payName AS  platformPayname , " 
				+ " priority , a.status , crm_payCode AS crmPayCode , crm_PayName AS crmPayName , "
				+ " createBy , b.op_name AS createByName , createTime ,  updateBY , c.op_Name AS  updateByName , updateTime ,"
				+ " a.onSale "
				+ " FROM DCP_PAYMENT_NRC a "
				+ " LEFT JOIN Platform_Staffs_Lang b ON a.EID = b.EID AND a.createby = b.opno  "
				+ " AND b.lang_type = '"+langType+"' "
				+ " LEFT JOIN Platform_Staffs_Lang c  ON a.EID = c.EID AND a.updateby = c.opno  "
				+ " AND c.lang_type = '"+langType+"' "
				+ " WHERE a.EID = '"+eId+"' " );
		
		if(keyTxt != null && keyTxt.length() > 0)
			sqlbuf.append(" and ( platform_payCode like '%%"+keyTxt+"%%' OR CRM_PAYCODE like '%%"+keyTxt+"%%' "
					+ " OR PLATFORM_PAYNAME like '%%"+keyTxt+"%%' OR CRM_PAYNAME like '%%"+keyTxt+"%%' ) ");
		
		if(status != null && status.length() > 0)
			sqlbuf.append(" and a.status = '"+status+"' ");
		
		sqlbuf.append(" order by priority , platform_payCode "
				+ "  ) DBL  WHERE rn > "+startRow +" AND rn  <= "+(startRow + pageSize)+" ");
		
		sql = sqlbuf.toString();
		return sql;
	}

}
