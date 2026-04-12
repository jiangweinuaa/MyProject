package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ECStockQueryReq;
import com.dsc.spos.json.cust.res.DCP_ECStockQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：DCP_ECStockGet
 * 服务说明：电商平台库存上下架查询
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockQuery extends SPosBasicService<DCP_ECStockQueryReq,DCP_ECStockQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_ECStockQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		if (Check.Null(beginDate)) 
		{
			errMsg.append("开始日期不可为空值, ");
			isFail = true;
		}
		if (Check.Null(endDate)) 
		{
			errMsg.append("结束日期不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	

	}

	@Override
	protected TypeToken<DCP_ECStockQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ECStockQueryReq>(){};
	}

	@Override
	protected DCP_ECStockQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ECStockQueryRes();
	}

	@Override
	protected DCP_ECStockQueryRes processJson(DCP_ECStockQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;
		DCP_ECStockQueryRes res = this.getResponse();	
		try 
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			res.setDatas(new ArrayList<DCP_ECStockQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("ECSTOCKNO", true);	
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData,condition);
				for (Map<String, Object> oneData : getQHeader) {
					DCP_ECStockQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String ecStockNo = oneData.get("ECSTOCKNO").toString();
					String createDate = oneData.get("CREATERDATE").toString();
					String createBy = oneData.get("CREATERBY").toString();
					String createByName = oneData.get("CREATERBYNAME").toString();
					String modifyDate = oneData.get("MODIFYDATE").toString();
					String modifyBy = oneData.get("MODIFYBY").toString();
					String modifyByName = oneData.get("MODIFYBYNAME").toString();
					String accountDate = oneData.get("ACCOUNTDATE").toString();
					String accountBy = oneData.get("ACCOUNTBY").toString();
					String accountByName = oneData.get("ACCOUNTBYNAME").toString();
					String status = oneData.get("STATUS").toString();
					String docType = oneData.get("DOCTYPE").toString();
					String opType = oneData.get("OPTYPE").toString();
					String loadDocType = oneData.get("LOAD_DOCTYPE").toString();
					String loadDocNo = oneData.get("LOAD_DOCNO").toString();
					String loadDocShop = oneData.get("LOAD_DOCSHOP").toString();
					String loadDocShopName = oneData.get("LOAD_DOCSHOPNAME").toString();
					String totQty = oneData.get("TOT_QTY").toString();

					oneLv1.setCreateBy(createBy);
					oneLv1.setCreateByName(createByName);
					oneLv1.setCreateDate(createDate);
					oneLv1.setModifyBy(modifyBy);
					oneLv1.setModifyByName(modifyByName);
					oneLv1.setModifyDate(modifyDate);
					oneLv1.setAccountBy(accountBy);
					oneLv1.setAccountByName(accountByName);
					oneLv1.setAccountDate(accountDate);
					oneLv1.setDocType(docType);
					oneLv1.setEcStockNo(ecStockNo);
					oneLv1.setLoadDocNo(loadDocNo);
					oneLv1.setLoadDocShop(loadDocShop);
					oneLv1.setLoadDocShopName(loadDocShopName);
					oneLv1.setLoadDocType(loadDocType);
					oneLv1.setOpType(opType);
					oneLv1.setStatus(status);
					oneLv1.setTotQty(totQty);
					oneLv1.setGoodsDatas(new ArrayList<DCP_ECStockQueryRes.level2Elm>());
					
					condition.clear();
					condition = new HashMap<String, Boolean>(); //查詢條件
					condition.put("ECSTOCKNO", true);
					condition.put("ITEM", true);
					condition.put("PLUNO", true);
					condition.put("UNIT", true);
					//调用过滤函数
					List<Map<String, Object>> getQDetail=MapDistinct.getMap(getQData,condition);
					for (Map<String, Object> oneDataDetail : getQDetail) {
						DCP_ECStockQueryRes.level2Elm oneLv2 = res.new level2Elm();
						String ecStockNo_Detail = oneDataDetail.get("ECSTOCKNO").toString();
						if (ecStockNo_Detail.equals(ecStockNo))
						{
							String item = oneDataDetail.get("ITEM").toString();
							String pluNo = oneDataDetail.get("PLUNO").toString();
							String pluName = oneDataDetail.get("PLU_NAME").toString();
							String pluBarcode = oneDataDetail.get("PLUBARCODE").toString();
							String unit = oneDataDetail.get("UNIT").toString();
							String unitName = oneDataDetail.get("UNIT_NAME").toString();
							String allQty = oneDataDetail.get("ALLQTY").toString();
							
							oneLv2.setAllQty(allQty);
							oneLv2.setItem(item);
							oneLv2.setPluBarcode(pluBarcode);
							oneLv2.setPluName(pluName);
							oneLv2.setPluNo(pluNo);
							oneLv2.setUnit(unit);
							oneLv2.setUnitName(unitName);
							oneLv2.setStockDatas(new ArrayList<DCP_ECStockQueryRes.level3Elm>());
							
							for (Map<String, Object> oneAll : getQData) 
							{
								DCP_ECStockQueryRes.level3Elm oneLv3 = res.new level3Elm();
								String ecStockNo_platform = oneAll.get("ECSTOCKNO").toString();
								String item_platform = oneAll.get("ITEM").toString();
								String pluNo_platform = oneAll.get("PLUNO").toString();
								String unit_platform = oneAll.get("UNIT").toString();
								
								if (ecStockNo_platform.equals(ecStockNo_Detail) && item_platform.equals(item) && pluNo_platform.equals(pluNo) && unit_platform.equals(unit))
								{
									String ecPlatformNo = oneAll.get("ECPLATFORMNO").toString();
									String ecPlatformName = oneAll.get("ECPLATFORMNAME").toString();
									String qty = oneAll.get("QTY").toString();
									oneLv3.setEcPlatformNo(ecPlatformNo);
									oneLv3.setEcPlatformName(ecPlatformName);
									oneLv3.setQty(qty);
									oneLv2.getStockDatas().add(oneLv3);
								}
							}
							oneLv1.getGoodsDatas().add(oneLv2);
						}		
					}
					res.getDatas().add(oneLv1);
				}
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;			
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			return res;			
		} 
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}


	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_ECStockQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		StringBuffer sqlbuf=new StringBuffer("");
		String sql=null;	
		String eId=req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		String docType = req.getRequest().getDocType();
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append( " SELECT a.*,c.org_name as load_docshopname,d.plu_name,e.unit_name,f.ecplatformname from "
				+ " ("
				+ " select count(DISTINCT a.ECSTOCKNO) over() num,dense_rank() over(ORDER BY a.ECSTOCKNO ) rn,a.*, "
				+ " to_char(a.CREATER_DATE,'YYYY-MM-DD hh24:mi:ss') as CREATERDATE,"
				+ " to_char(a.MODIFY_DATE,'YYYY-MM-DD hh24:mi:ss') as MODIFYDATE,"
				+ " to_char(a.ACCOUNT_DATE,'YYYY-MM-DD hh24:mi:ss') as ACCOUNTDATE,"
				+ " b.ecplatformno,b.item,b.pluno,b.plubarcode,b.unit,b.allqty,b.qty from DCP_ECSTOCK a "
				+ " inner join dcp_ecstock_detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.ecstockno=b.ecstockno "
				+ " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' and to_char(a.CREATER_DATE,'YYYY-MM-DD') >='"+beginDate+"'  "
				+ " and to_char(a.CREATER_DATE,'YYYY-MM-DD') <='"+endDate+"' ");
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.ecstockno like '%%"+keyTxt+"%%' or a.load_docno like '%%"+keyTxt+"%%' "
					+ " or a.createrby like '%%"+keyTxt+"%%' or a.modifyby like '%%"+keyTxt+"%%' or a.accountby like '%%"+keyTxt+"%%' "
					+ " or a.createrbyname like '%%"+keyTxt+"%%' or a.modifybyname like '%%"+keyTxt+"%%' or a.accountbyname like '%%"+keyTxt+"%%') ");
		}
		if (!Check.Null(status))
		{
			sqlbuf.append(" and a.status='"+status+"' ");
		}
		if (!Check.Null(docType))
		{
			sqlbuf.append(" and a.doctype='"+docType+"' ");
		}  

		sqlbuf.append(")a "
				+ " left join DCP_ORG_lang c on a.EID=c.EID and a.load_docshop=c.organizationno and c.lang_type='"+langType+"' "
				+ " left join DCP_GOODS_lang d on a.EID=d.EID and a.pluno=d.pluno and d.lang_type='"+langType+"' "
				+ " left join DCP_UNIT_lang e on e.EID=a.EID and a.unit=e.unit and e.lang_type='"+langType+"' "
				+ " left join OC_ECOMMERCE f on a.EID=f.EID and a.ecplatformno=f.ecplatformno "
				+ " where rn>"+startRow+" and rn<="+(startRow+pageSize) + " "
				+ " order by a.ECSTOCKNO");

		sql=sqlbuf.toString();
		return sql;
	}

}
