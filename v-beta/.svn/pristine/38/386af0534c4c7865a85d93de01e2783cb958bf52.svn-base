package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MiniChargeQueryReq;
import com.dsc.spos.json.cust.res.DCP_MiniChargeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 低消信息查询
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeQuery extends SPosBasicService<DCP_MiniChargeQueryReq, DCP_MiniChargeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_MiniChargeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_MiniChargeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MiniChargeQueryReq>(){};
	}

	@Override
	protected DCP_MiniChargeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MiniChargeQueryRes();
	}

	@Override
	protected DCP_MiniChargeQueryRes processJson(DCP_MiniChargeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_MiniChargeQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};

			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);

			if(getDatas.size() > 0){
				res.setDatas(new ArrayList<DCP_MiniChargeQueryRes.level1Elm>());

				String num = getDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("MINICHARGENO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getDatas, condition);


				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_MiniChargeQueryRes.level1Elm lev1 = res.new level1Elm();
					lev1.setDatas(new ArrayList<DCP_MiniChargeQueryRes.level2Elm>());

					String miniChargeNO = oneData.get("MINICHARGENO").toString();
					String mcType = oneData.get("MCTYPE").toString();
					String adultQty = oneData.get("ADULTQTY").toString();
					String childQty = oneData.get("CHILDQTY").toString();
					String priceClean = oneData.get("PRICECLEAN").toString();
					String amtMini = oneData.get("AMTMINI").toString();
					String createDate = oneData.get("CREATEDATE").toString();
					String createTime = oneData.get("CREATETIME").toString();
					String createBy = oneData.get("CREATEBY").toString();
					String createByName = oneData.get("CREATEBYNAME").toString();
					String modifyDate = oneData.get("MODIFYDATE").toString();
					String modifyTime = oneData.get("MODIFYTIME").toString();
					String modifyBy = oneData.get("MODIFYBY").toString();
					String modifyByName = oneData.get("MODIFYBYNAME").toString();
					String status = oneData.get("STATUS").toString();
					String updateTime = oneData.get("UPDATETIME").toString();

					lev1.setMiniChargeNo(miniChargeNO);
					lev1.setMcType(mcType);
					lev1.setAdultQty(adultQty);
					lev1.setChildQty(childQty);
					lev1.setPriceClean(priceClean);
					lev1.setAmtMini(amtMini);
					lev1.setCreateDate(createDate);
					lev1.setCreateTime(createTime);
					lev1.setCreateBy(createBy);
					lev1.setCreateByName(createByName);
					lev1.setModifyDate(modifyDate);
					lev1.setModifyTime(modifyTime); 
					lev1.setModifyBy(modifyBy);
					lev1.setModifyByName(modifyByName);
					lev1.setStatus(status);
					lev1.setUpdateTime(updateTime);

					for (Map<String, Object> oneData2 : getDatas) 
					{
						//过滤属于此单头的明细
						if(miniChargeNO.equals(oneData2.get("MINICHARGENO")))
						{	
							DCP_MiniChargeQueryRes.level2Elm lev2 =  res.new level2Elm();

							String pluNO = oneData2.get("PLUNO").toString();
							String pluName = oneData2.get("PLUNAME").toString();
							lev2.setPluNo(pluNO);
							lev2.setPluName(pluName);
							lev1.getDatas().add(lev2);
						}
					}

					res.getDatas().add(lev1);
				}
			}


		}
		catch (Exception e) {


		}
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);

		return res;

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_MiniChargeQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String minichargeNO = req.getRequest().getMiniChargeNo();
		String mcType = req.getRequest().getMcType();
		String status = req.getRequest().getStatus();
		String langType = req.getLangType();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append( "SELECT * FROM ( "
				+ " SELECT COUNT(DISTINCT a.minichargeNO ) OVER() NUM , "
				+ " dense_rank() over(ORDER BY a.minichargeNO) rn ,"
				+ " a.minichargeNO , a.mcType , a.adult_Qty AS adultQty , a.child_qty AS childQty ,"
				+ " a.price_clean AS priceClean , a.amt_mini AS amtMini , "
				+ " a.createBy , a.create_date AS createDate, a.create_Time AS createTime ,   d.op_name AS createByName ,"
				+ " a.modifyby , a.modify_date AS modifyDate , a.modify_time AS modifytime ,  e.op_Name AS modifyByName , "
				+ " a.status , a.update_time AS updateTime ,"
				+ " b.pluNo , c.plu_name as pluname "
				+ " FROM DCP_MINICHARGE  a "
				+ " LEFT JOIN DCP_MINICHARGE_detail b ON a.EID = b.EID AND a.minichargeNO = b.minichargeNO "
				+ " LEFT JOIN DCP_GOODS_lang c ON b.EID = c.EID AND b.pluNo = c.pluNO  AND c.lang_Type = '"+langType+"'"
				+ " LEFT JOIN platform_staffs_lang d ON a.EID = d.EID AND a.createby = d.opNO AND d.lang_Type  = '"+langType+"'"
				+ " LEFT JOIN platform_staffs_lang e ON a.EID = e.EID AND a.modifyby = e.opNO  AND e.lang_Type  = '"+langType+"'"
				+ " WHERE a.EID = '"+eId+"'  " );

		if (minichargeNO != null && minichargeNO.length()!=0) { 	
			sqlbuf.append( " AND a.minichargeNO LIKE '%%"+minichargeNO+"%%'  ");
		}
		if (mcType != null && mcType.length()!=0) { 	
			sqlbuf.append( " AND a.mcType = '"+mcType+"'  ");
		}
		if (status != null && status.length()!=0) { 	
			sqlbuf.append( " AND a.status = '"+status+"'  ");
		}

		sqlbuf.append( " )   where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by minichargeNO ");
		sql = sqlbuf.toString();
		return sql;
	}

}
