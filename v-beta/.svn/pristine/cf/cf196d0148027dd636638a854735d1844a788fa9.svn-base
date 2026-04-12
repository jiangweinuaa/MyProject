package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PFOrderOldQueryReq;
import com.dsc.spos.json.cust.res.DCP_PFOrderOldQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 营业预估查询 
 * @author yuanyy 2019-08-14
 *
 */
public class DCP_PFOrderOldQuery extends SPosBasicService<DCP_PFOrderOldQueryReq, DCP_PFOrderOldQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PFOrderOldQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PFOrderOldQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderOldQueryReq>(){};
	}

	@Override
	protected DCP_PFOrderOldQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderOldQueryRes();
	}

	@Override
	protected DCP_PFOrderOldQueryRes processJson(DCP_PFOrderOldQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		DCP_PFOrderOldQueryRes res = this.getResponse();
		try {
			sql = this.getQuerySql(req);
			List<Map<String, Object>> mainDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_PFOrderOldQueryRes.level1Elm>());
			if(mainDatas.size() > 0){
				 
				int totalRecords;	//总笔数
				int totalPages;		//总页数
				if (mainDatas != null && mainDatas.isEmpty() == false)
				{ 
					Map<String, Object> oneData2 = mainDatas.get(0);
					String num = oneData2.get("NUM").toString();
					totalRecords=Integer.parseInt(num);
					//算總頁數
					totalPages = totalRecords / req.getPageSize();
					totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				}
				else
				{
					totalRecords = 0;
					totalPages = 0;
				}

				res.setTotalRecords(totalRecords);
				res.setTotalPages(totalPages);
				
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("PFNO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(mainDatas, condition);
//				res.setDatas(new ArrayList<PFOrderGetDCPRes.level1Elm>());
				for (Map<String, Object> map : getQHeader) {
					DCP_PFOrderOldQueryRes.level1Elm lv1 = res.new level1Elm();
					
					String PFID = map.get("PFID").toString();
					String PFNO = map.get("PFNO").toString();
					String bDate = map.get("BDATE").toString();
					String memo = map.get("MEMO").toString();
					String rDate = map.get("RDATE").toString();
					String predictAmt = map.get("PREDICTAMT").toString();
					String beginDate = map.get("BEGINDATE").toString();
					String endDate = map.get("ENDDATE").toString();
					String avgSaleAmt = map.get("AVGSALEAMT").toString();
					String modifRatio = map.get("MODIFRATIO").toString();
					String pfDay = map.get("PFDAY").toString();
					String pfWeather = map.get("PFWEATHER").toString();
					String pfHoliday = map.get("PFHOLIDAY").toString();
					String pfMatter = map.get("PFMATTER").toString();
					String totPQty = map.get("TOTPQTY").toString();
					String totCQty = map.get("TOTCQTY").toString();
					String totAmt = map.get("TOTAMT").toString();
					String status = map.get("STATUS").toString();
					
					String pfWeatherNo = map.get("PFWEATHERNO").toString();
					String pfHolidayNo = map.get("PFHOLIDAYNO").toString();
					String pfMatterNo = map.get("PFMATTERNO").toString();
					
					String pfWeatherRatio = map.getOrDefault("PFWEATHERRATIO","0").toString();
					String pfHolidayRatio = map.getOrDefault("PFHOLIDAYRATIO","0").toString();
					String pfMatterRatio = map.getOrDefault("PFMATTERRATIO","0").toString();
					
					String createBy = map.get("CREATEBY").toString();
					String createDate = map.get("CREATEDATE").toString();	
					String createTime = map.get("CREATETIME").toString();	
					String createByName = map.get("CREATEBYNAME").toString();
					
					String confirmBy = map.get("CONFIRMBY").toString();	
					String confirmDate = map.get("CONFIRMDATE").toString();	
					String confirmTime = map.get("CONFIRMTIME").toString();	
					String confirmByName = map.get("CONFIRMBYNAME").toString();	

					String accountBy = map.get("ACCOUNTBY").toString();	
					String accountDate = map.get("ACCOUNTDATE").toString();	
					String accountTime = map.get("ACCOUNTTIME").toString();	
					String accountByName = map.get("ACCOUNTBYNAME").toString();	

					String cancelBy = map.get("CANCELBY").toString();	
					String cancelDate = map.get("CANCELDATE").toString();	
					String cancelTime = map.get("CANCELTIME").toString();
					String cancelByName = map.get("CANCELBYNAME").toString();

					String modifyBy = map.get("MODIFYBY").toString();	
					String modifyDate = map.get("MODIFYDATE").toString();
					String modifyTime = map.get("MODIFYTIME").toString();
					String modifyByName = map.get("MODIFYBYNAME").toString();

					String submitBy = map.get("SUBMITBY").toString();	
					String submitDate = map.get("SUBMITDATE").toString();
					String submitTime = map.get("SUBMITTIME").toString();
					String submitByName = map.get("SUBMITBYNAME").toString();
					
					lv1.setbDate(bDate);
					lv1.setMemo(memo);
					lv1.setPfId(PFID);
					lv1.setPfNo(PFNO);
					lv1.setrDate(rDate);
					lv1.setPredictAmt(predictAmt);
					lv1.setBeginDate(beginDate);
					lv1.setEndDate(endDate);
					lv1.setAvgSaleAmt(avgSaleAmt);
					
					lv1.setModifRatio(modifRatio);
					lv1.setPfDay(pfDay);
					lv1.setPfWeather(pfWeather);
					lv1.setPfHoliday(pfHoliday);
					lv1.setPfMatter(pfMatter);
					lv1.setTotAmt(totAmt);
					lv1.setTotCQty(totCQty);
					lv1.setTotPQty(totPQty);
					lv1.setStatus(status);
					
					lv1.setPfWeatherNo(pfWeatherNo);
					lv1.setPfWeatherRatio(pfWeatherRatio);
					lv1.setPfHolidayNo(pfHolidayNo);
					lv1.setPfHolidayRatio(pfHolidayRatio);
					lv1.setPfMatterNo(pfMatterNo);
					lv1.setPfMatterRatio(pfMatterRatio);
					
					lv1.setCreateBy(createBy);
					lv1.setCreateDate(createDate);
					lv1.setCreateTime(createTime);
					lv1.setCreateByName(createByName);

					lv1.setAccountBy(accountBy);
					lv1.setAccountDate(accountDate);
					lv1.setAccountTime(accountTime);
					lv1.setAccountByName(accountByName);

					lv1.setConfirmBy(confirmBy);
					lv1.setConfirmDate(confirmDate);
					lv1.setConfirmTime(confirmTime);
					lv1.setConfirmByName(confirmByName);

					lv1.setCancelBy(cancelBy);
					lv1.setCancelDate(cancelDate);
					lv1.setCancelTime(cancelTime);
					lv1.setCancelByName(cancelByName);

					lv1.setModifyBy(modifyBy);
					lv1.setModifyDate(modifyDate);
					lv1.setModifyTime(modifyTime);
					lv1.setModifyByName(modifyByName);

					lv1.setSubmitBy(submitBy);
					lv1.setSubmitDate(submitDate);
					lv1.setSubmitTime(submitTime);
					lv1.setSubmitByName(submitByName);
					
					lv1.setDatas(new ArrayList<DCP_PFOrderOldQueryRes.level2Elm>());
					
					Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
					condition2.put("PFNO", true);	
					condition2.put("PLUNO", true);	
					//调用过滤函数
					List<Map<String, Object>> detailDatas=MapDistinct.getMap(mainDatas, condition2);
					
					for (Map<String, Object> par : detailDatas) {
						if(par.get("PFNO").toString().equals(PFNO)){
							
							DCP_PFOrderOldQueryRes.level2Elm lv2 = res.new level2Elm();
							
							String pluNo = par.get("PLUNO").toString();
							lv2.setItem(par.get("ITEM").toString());
							lv2.setPluNo(par.get("PLUNO").toString());
							lv2.setPluName(par.get("PLUNAME").toString());
							lv2.setpUnit(par.get("PUNIT").toString());
							lv2.setpUnitName(par.getOrDefault("PUNITNAME", "").toString());
							lv2.setIsClear(par.get("ISCLEAR").toString());
							lv2.setLastSaleTime(par.get("LASTSALETIME").toString());
							lv2.setPreSaleQty(par.get("PRESALEQTY").toString());
							lv2.setPrice(par.get("PRICE").toString());
							lv2.setkQty(par.get("KQTY").toString());
							lv2.setkAmt(par.get("AMT").toString());
							lv2.setkAdjQty(par.get("KADJQTY").toString());
							lv2.setQty(par.get("PQTY").toString());
							lv2.setkAdjAmt(par.get("ADJAMT").toString());
							lv2.setMemo(par.get("BMEMO").toString());
							lv2.setTrueQty(par.get("TRUEQTY").toString());//实备数量
							lv2.setFileName(par.get("LISTIMAGE").toString());
							lv2.setAvgAmt(par.getOrDefault("AVGAMT", "0").toString());
							lv2.setAvgPrice(par.getOrDefault("AVGPRICE", "0").toString());
							lv2.setAvgQty(par.getOrDefault("AVGQTY", "0").toString());
							
							lv2.setDatas(new ArrayList<DCP_PFOrderOldQueryRes.level3Elm>());
							
//							for (Map<String, Object> par3 : mainDatas) {
//								if(par3.get("PFNO").toString().equals(PFNO) && par3.get("PLUNO").toString().equals(pluNo)){
//									PFOrderGetDCPRes.level3Elm lv3 = new PFOrderGetDCPRes.level3Elm();
//									lv3.setDtNo(par3.getOrDefault("DTNO", "").toString());
//									lv3.setDtName(par3.getOrDefault("DTNAME", "").toString());
//									lv3.setBeginTime(par3.getOrDefault("BEGINTIME", "").toString());
//									lv3.setEndTime(par3.getOrDefault("ENDTIME", "").toString());
//									lv3.setQty(par3.getOrDefault("QTY", "").toString());
//									lv2.getDatas().add(lv3);
//								}
//							}
							
							lv1.getDatas().add(lv2);
							lv2=null;
							
						}
					}
					
					res.getDatas().add(lv1);
					lv1=null;
				}
				
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			
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
	protected String getQuerySql(DCP_PFOrderOldQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String langType = req.getLangType();
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		String bDate = req.getRequest().getbDate();
		
		int pageSize = req.getPageSize();
		int pageNumber = req.getPageNumber();

		if(pageSize == 0){
			pageSize = 9999;
		}
		
		//計算起啟位置
		int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
		startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" SELECT * FROM ( "
			+ " SELECT COUNT(DISTINCT a.PFNO ) OVER() NUM ,dense_rank() over(ORDER BY a.PFNO desc ) rn,"
			+ " a.status,  a.pfno , a.pf_id AS pfid , a.bdate , a.rdate , a.memo , a.tot_pqty AS totPqty , a.tot_cqty AS totCqty , "
			+ " a.tot_amt AS totamt , a.createBy AS createBy , a.create_Date AS createDate, a.create_time AS createTime ,  "
			+ " a.modifyBy  , a.modify_Date AS modifyDate ,  a.modify_Time AS modifyTime , "
			+ " a.predictAmt , a.beginDate  , a.endDate , a.avgSaleAmt , a.ModifRatio , "
			+ " a.pfday , "
			+ " b.item , b.pluNo , c.plu_name AS pluName , b.punit , b.baseunit , b.pqty , b.baseqty  , b.price , "
			+ " b.amt , b.kqty , b.kadjQty , b.ref_Amt AS refAmt  , b.adj_Amt AS adjAmt , b.memo as bMemo , b.isClear, b.lastSaleTime ,b.presaleqty, "
//			+ " d.begin_time AS beginTime , d.end_time AS endTime , d.dtNo , d.qty , d.dtName , "
			+ "	f1.op_name as modifyByName ,f2.op_name as cancelByName , f3.op_name as ConfirmByName , " 
		    + " f4.op_name as submitByName ,  f5.op_name as accountByName ,f.op_name as createByName ,"
			+ " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
			+ " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
			+ " a.accountby, a.account_date AS accountDate, a.account_time AS accountTime , "
			+ " a.submitby , a.submit_date AS submitDate, a.submit_time AS submitTime "
			+ " ,a.UPDATE_TIME , b.trueqty ,gim.LISTIMAGE "
			
			// 2020-04-24 增加avgQty， avgPrice，avgAmt 三个字段
			+ " , b.AvgAmt , b.avgQty, b.avgPrice, ut.uname as pUnitName  "
			
			// 2019-11-04 增加影响因素信息
			+ " ,a.pfweather as pfWeatherNo, E1.e_name as pfWeather , E1.E_ratio as pfWeatherRatio  "
			+ " ,a.pfMatter as pfMatterNo, nvl(E2.e_name,a.pfMatter) as pfMatter , E2.E_ratio as pfMatterRatio  "
			+ " ,a.pfHoliday as pfHolidayNo, E3.e_name as pfHoliday , E3.E_ratio as pfHolidayRatio "
			
			+ " FROM DCP_porder_forecast a "
			+ " LEFT JOIN DCP_porder_forecast_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.pfno = b.pfNO "
			
			// 增加这一行，将餐段信息加到返回结果中， 每个商品都有餐段，方便前端对应 。如果没有这一段，前端就得判断是否返回有餐段信息，太麻烦
			+ " LEFT JOIN DCP_GOODS_lang c ON b.EID = c.EID AND b.pluNo = c.pluNo AND c.lang_type = '"+langType+"' "
			+ " LEFT JOIN DCP_GOODS d ON b.EID = d.EID AND b.pluNo = d.pluNo AND d.status='100'  "
			+ " LEFT JOIN PLATFORM_STAFFS_LANG f ON A .EID = f.EID AND A .createby = f.opno AND F.LANG_TYPE ='"+ langType +"' "
			//2018-11-20 新增以下几行， 用于查modifyByName 等字段
			+ " LEFT JOIN PLATFORM_STAFFS_LANG f1 ON A .EID = f1.EID AND A .modifyBy = f1.opno AND F1.LANG_TYPE  = '" + langType + "' "
			+ " LEFT JOIN PLATFORM_STAFFS_LANG f2 ON A .EID = f2.EID AND A .cancelby = f2.opno AND F2.LANG_TYPE = '" + langType + "' "
			+ " LEFT JOIN PLATFORM_STAFFS_LANG f3 ON A .EID = f3.EID AND A .confirmby = f3.opno AND F3.LANG_TYPE = '" + langType + "' "
			+ " LEFT JOIN PLATFORM_STAFFS_LANG f4 ON A .EID = f4.EID AND A .submitby = f4.opno AND F4.LANG_TYPE = '" + langType + "' "
			+ " LEFT JOIN PLATFORM_STAFFS_LANG f5 ON A .EID = f5.EID AND A .accountby = f5.opno AND F5.LANG_TYPE = '" + langType + "' "
			// 2019-11-04 增加影响因素信息
 			+ " LEFT JOIN DCP_ELEMENT E1 on a.EID = E1.EID and a.pfWeather = E1.e_No and E1.e_type = '0' and E1.status='100' " //0:天气
 			+ " LEFT JOIN DCP_ELEMENT E2 on a.EID = E2.EID and a.pfMatter = E2.e_No and E2.e_type = '1' and E1.status='100' " //1:特殊事件
 			+ " LEFT JOIN DCP_ELEMENT E3 on a.EID = E3.EID and a.pfHoliday = E3.e_No and E3.e_type = '2' and E1.status='100' " //2:节假日
 			+ " LEFT JOIN DCP_UNIT_LANG ut on b.EID = ut.EID and b.pUnit = ut.unit and ut.lang_type = '"+langType+"' "
 			+ " LEFT JOIN DCP_GOODSIMAGE GIM ON d.EID=GIM.EID AND d.PLUNO=GIM.PLUNO AND GIM.APPTYPE='ALL' "
 			
 			+ " WHERE a.EID = '"+eId+"'  AND a.SHOPID = '"+shopId+"'   ");
		
		if(!beginDate.equals("") && !endDate.equals("")){
			sqlbuf.append(" and a.bDate between '"+beginDate+"' and '"+endDate+"' ");
		}
		
		if(!bDate.equals("") ){
			sqlbuf.append(" and a.bDate = '"+bDate+"' ");
		}
		
		if(!keyTxt.equals("")){
			sqlbuf.append(" and ( a.PFNO like '%%"+keyTxt+"%%%' or b.pluNo like '%%"+keyTxt+"%%%' or  c.plu_name like '%%"+keyTxt+"%%%'  ) ");
		}
		
		if(!status.equals("")){
			sqlbuf.append(" and a.status = '"+status+"' ");
		}
		
		sqlbuf.append(" ORDER BY PFNO,item  ) where (rn > " + startRow + " AND rn <= " + (startRow+pageSize) + " ) "
				+ "  ORDER BY status ASC , bdate DESC ,  pfno DESC  ,  pluNO  ");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
