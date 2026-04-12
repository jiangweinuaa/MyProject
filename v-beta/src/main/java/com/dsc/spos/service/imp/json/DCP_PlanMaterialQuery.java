package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PlanMaterialQueryReq;
import com.dsc.spos.json.cust.res.DCP_PlanMaterialQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 生产计划原料更新
 * @author yuanyy 2019-10-28
 *
 */
public class DCP_PlanMaterialQuery extends SPosBasicService<DCP_PlanMaterialQueryReq, DCP_PlanMaterialQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PlanMaterialQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;
	    
	    if (Check.Null(req.getPlanNo())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("单据编号不可为空值, ");
	    }
	    
	    if (Check.Null(req.getfNo())) {
	    	isFail = true;
	    	errCt++;
	    	errMsg.append("班次/批次编码不可为空值, ");
	    }
	    
	    if (isFail){
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
	    return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanMaterialQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanMaterialQueryReq>(){};
	}

	@Override
	protected DCP_PlanMaterialQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanMaterialQueryRes();
	}

	@Override
	protected DCP_PlanMaterialQueryRes processJson(DCP_PlanMaterialQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PlanMaterialQueryRes res = this.getResponse();
		try {
			String sql = this.getQuerySql(req);
			
			List<Map<String, Object>> pluDatas = this.doQueryData(sql, null);
			int totalRecords = 0;		//总笔数				
			int totalPages = 0;   	//总页数
			int pageSize = req.getPageSize();
			int pageNumber = req.getPageNumber();
			if(pageNumber == 0 || pageSize == 0 ){
				pageNumber = 1;
				pageSize = 9999;
			}
			
			String priority = "1";
			if(req.getPriority() == null ){
				if(req.getfType().toString().equals("1")){
					String prioritySql = "select * from DCP_DINNERTime where EID = '"+req.geteId()+"' and SHOPID = '"+req.getShopId()+"' "
							+ " and dtNO = '"+req.getfNo()+"' ";
					
					List<Map<String,Object>> pDatas = this.doQueryData(prioritySql, null);
					if(pDatas != null){
						priority = pDatas.get(0).get("PRIORITY").toString();
					}
					
				}
				else{
					
				}
			}
			else{
				priority = req.getPriority();
			}
			
			res.setDatas(new ArrayList<DCP_PlanMaterialQueryRes.level1Elm>());
			
			if(pluDatas != null && pluDatas.size() > 0){
				
				Map<String, Object> oneData_Count = pluDatas.get(0);
				String num = oneData_Count.get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				//算總頁數
				totalPages = totalRecords / pageSize;
				totalPages = (totalRecords % pageSize > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> map : pluDatas) {
					DCP_PlanMaterialQueryRes.level1Elm lv1 = res.new level1Elm();
					
					lv1.setPluNo(map.getOrDefault("PLUNO", "").toString());
					lv1.setPluName(map.getOrDefault("PLUNAME", "").toString());
					lv1.setUnit(map.getOrDefault("PUNIT", "").toString());
					lv1.setSpec(map.getOrDefault("SPEC", "").toString());
					lv1.setAvgQty(map.getOrDefault("AVGQTY", "0").toString());
					lv1.setPredictQty(map.getOrDefault("PREDICTQTY", "0").toString());
					
					int nowQty = Integer.parseInt(map.getOrDefault("NOWQTY", "0").toString());
					
					if(map.getOrDefault("ISOUT", "").equals("Y")){
						nowQty = nowQty + Integer.parseInt(map.getOrDefault("ACTQTY", "0").toString());
					}
					
					if(nowQty < 0 ){
						nowQty = 0;
					}
					
					lv1.setNowQty(nowQty + "");
					
					if(priority.equals("1")){
						lv1.setResidueQty("0");
						lv1.setNowQty("0");
					}
					else{ 
						int residueqty = Integer.parseInt(map.getOrDefault("RESIDUEQTY", "0").toString());
						if(residueqty < 0 ){
							residueqty = 0;
						}
						lv1.setResidueQty(residueqty + "");
						
					}
					
					
					lv1.setOutQty(map.getOrDefault("OUTQTY", "0").toString());
					lv1.setChangeQty(map.getOrDefault("CHANGEQTY", "0").toString());
					lv1.setActQty(map.getOrDefault("ACTQTY", "0").toString());
					lv1.setPrice(map.getOrDefault("PRICE", "0").toString());
					lv1.setDistriPrice(map.getOrDefault("DISTRIPRICE", "0").toString());
					lv1.setTotAmt(map.getOrDefault("TOTAMT", "0").toString());
					lv1.setDistriAmt(map.getOrDefault("DISTRIAMT", "0").toString());
					lv1.setIsOut(map.getOrDefault("ISOUT", "N").toString());
					
					lv1.setUdLength(map.getOrDefault("PUNITUDLENGTH", "2").toString());
					
					res.getDatas().add(lv1);
					lv1=null;
				}
				
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PlanMaterialQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String planNo = req.getPlanNo();
		String fNo = req.getfNo();
		String eId = req.geteId();
		String shopId = req.getShopId();
		
		String fType = req.getfType();
		
		String preBeginTime = req.getPreBeginTime()==null ? "":req.getPreBeginTime();
		String preEndTime = req.getPreEndTime() == null ? "":req.getPreEndTime();
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		df = new SimpleDateFormat("HHmmss");
		String nowTime = df.format(cal.getTime()); 
		
		String bDate = PosPub.getAccountDate_SMS(dao, req.geteId(), req.getShopId());
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		
		if(pageNumber == 0 || pageSize == 0 ){
			pageNumber = 1;
			pageSize = 9999;
		}
		
		String[] pluArr = {};
		String pluStr = "";
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		
		sqlbuf.append(" SELECT p.*, (preActQty - preTotQty - outQty ) as residueQty , (preActQty - nowTotQty ) as nowQty   FROM (  "
				+ " SELECT "
				+ " count(DISTINCT a.pluNo ) over() num, dense_rank() OVER( ORDER BY  a.pluNo ) rn , "
				+ " a.EID , a.SHOPID , a.pluNo, b.plu_name AS pluName  , a.fType , a.fNo , "
				+ " a.punit , a.Avgqty , a.predictqty , nvl(so.pqty ,0) as  outQty, a.changeqty , nvl(a.actqty , 0 ) as actQty , a.price, a.distriprice , a.isOut ,   "
				+ " a.totAmt , a.distriamt ,"
				//+ " h.qty as residueQty , "
				+ " g.spec ,  nvl(u.udlength, 2) as punitUdLength  ,  "
				+ " nvl(r.preActQty , 0 ) as preActQty  , nvl(r.preTotQty , 0 ) as preTotQty , nvl(n.preTotQty, 0) as nowTotQty "
				+ " FROM DCP_plan_material a  "
				+ " LEFT JOIN DCP_GOODS_lang b ON a.EID = b.EID AND a.pluNo = b.pluNo and b.lang_type = '"+req.getLangType()+"'"
				
				+ " LEFT JOIN DCP_GOODS g on a.EID = g.EID and a.pluNO = g.pluNO "
				+ " LEFT JOIN DCP_UNIT u on a.EID = u.EID and a.punit = u.unit " 
				
				// 查上一批次生产量和上一批次的销售量，用于计算库存
				+ " LEFT JOIN ("
				+ " SELECT a.EID , a.SHOPID , a.planNo ,a.pluno , a.actqty as preActQty , NVL( d.totQty , 0 )  AS pretotQty " 
				+ " FROM DCP_plan_material a  " 
				+ " LEFT JOIN (  "
 
				+ " SELECT b.EID , b.SHOPID ,  b.bdate  , c.pluNo ,  NVL(sum(CASE when b.type = '1' or b.type = '2' or b.type = '4' THEN  -qty ELSE qty end), 0) as totQty "
				+ " FROM   DCP_SALE b    "
				+ " LEFT JOIN DCP_SALE_detail c ON b.EID = c.EID AND b.SHOPID = c.SHOPID AND b.saleNO = c.saleNo "
				+ " WHERE  b.EID = '"+eId+"' AND b.SHOPID = '"+shopId+"' AND b.bdate = '"+bDate+"' AND b.stime BETWEEN '"+preBeginTime+"' AND '"+preEndTime+"' " 
				+ " group BY  b.bdate , c.pluNo , b.EID , b.SHOPID "
				+ " ) d ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.pluNo = d.pluNo "
 
				+ " WHERE a.EID = '"+eId+"' AND a.SHOPID = '"+shopId+"' AND a.planNo = '"+planNo+"' "
				+ " and a.fNO = '"+req.getPreFNo()+"' and a.isOut = 'Y' and a.fType = '"+fType+"'"
				+ " ) R on a.EID = R.EID and a.SHOPID = r.SHOPID and a.planNo = r.planNo and a.pluNo = r.pluNo  "
				
				//计算到上一批次开始时间到目前时间为止的销售量。
				+ " LEFT JOIN ("
				+ " SELECT a.EID , a.SHOPID , a.planNo ,a.pluno , a.actqty as preActQty , NVL( d.totQty , 0 )  AS pretotQty " 
				+ " FROM DCP_plan_material a  " 
				+ " LEFT JOIN (  "
				
				+ " SELECT b.EID , b.SHOPID ,  b.bdate  , c.pluNo ,  NVL(sum(CASE when b.type = '1' or b.type = '2' or b.type = '4' THEN  -qty ELSE qty end), 0) as totQty "
				+ " FROM   DCP_SALE b    "
				+ " LEFT JOIN DCP_SALE_detail c ON b.EID = c.EID AND b.SHOPID = c.SHOPID AND b.saleNO = c.saleNo "
				+ " WHERE  b.EID = '"+eId+"' AND b.SHOPID = '"+shopId+"' AND b.bdate = '"+bDate+"' AND b.stime BETWEEN '"+preBeginTime+"' AND '"+nowTime+"' " 
				+ " group BY  b.bdate , c.pluNo , b.EID , b.SHOPID "
				+ " ) d ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.pluNo = d.pluNo "
				
				+ " WHERE a.EID = '"+eId+"' AND a.SHOPID = '"+shopId+"' AND a.planNo = '"+planNo+"' "
				+ " and a.fNO = '"+req.getPreFNo()+"' and a.isOut = 'Y'  and a.fType = '"+fType+"' "
				+ " ) N on a.EID = N.EID and a.SHOPID = N.SHOPID and a.planNo = N.planNo and a.pluNo = N.pluNo  "

				
				+ " LEFT JOIN ("
                + " SELECT  "
                + " a.EID , a.SHOPID , a.bDate , b.pluNo   , sum(b.pqty) AS pqty  " 
                + " FROM DCP_lstockout a "
                + " LEFT JOIN DCP_lstockout_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.lstockoutNO = b.lstockoutNo and a.BDATE=b.BDATE "
                + " WHERE a.EID = '"+req.geteId()+"' AND a.SHOPID = '"+req.getShopId()+"' AND a.status = '2'  "
                + " AND a.bdate = '"+bDate+"' AND a.confirm_Date = '"+bDate+"' AND a.confirm_TIme > '"+req.getPreBeginTime()+"'  AND a.confirm_Time <= '"+req.getPreEndTime()+"' "
                + " GROUP BY  a.EID , a.SHOPID , a.bDate , b.pluNo "
                + " )  so "
                + " ON  so.EID = a.EID and so.SHOPID = a.SHOPID and so.pluNO = a.pluNo "
                
				+ " WHERE a.EID = '"+req.geteId()+"' AND a.SHOPID = '"+req.getShopId()+"'  AND a.planNo = '"+planNo+"'  "
				+ " and a.fNo = '"+fNo+"'  and a.fType = '"+fType+"' " );
		
		/**
		 * 注意：pluNo、 pluName、isOut 这三个参数。如果前端受限于插件，无法过滤，就调用模板明细，传pluNo 进来，由后端过滤，效能会相对降低很多。
		 */
		if(req.getPluNo() != null && req.getPluNo().length > 0)
		{	
			pluArr = req.getPluNo();
			pluStr = PosPub.getArrayStrSQLIn(pluArr);
			sqlbuf.append(" and a.pluNO in ("+pluStr+")");
		}
		
		if(req.getPluName() != null && req.getPluName().length() > 0){
			sqlbuf.append(" and b.plu_name like '%%"+req.getPluName()+"%%'");
		}
		
		if(req.getIsOut() != null && req.getIsOut() .length() > 0){
			sqlbuf.append(" and a.isOut = '"+req.getIsOut()+"'");
		}
		
		
		sqlbuf.append( " order by a.pluNo  ");
				
		sqlbuf.append( " ) p "
				+ " WHERE p.rn > "+startRow +" AND p.rn  <= "+(startRow + pageSize) 
				+ " order by p.rn, p.pluNO  ");
		
		sql = sqlbuf.toString();
		return sql;
	}

}
