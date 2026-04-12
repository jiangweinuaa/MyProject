package com.dsc.spos.service.imp.json;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.dsc.spos.utils.Check;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_PurOrderReturnListQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurOrderReturnListQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;
 

public class DCP_PurOrderReturnListQuery extends SPosBasicService<DCP_PurOrderReturnListQueryReq, DCP_PurOrderReturnListQueryRes > {
	
	Logger logger = LogManager.getLogger(SPosAdvanceService.class);
	public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();
	
	@Override
	protected boolean isVerifyFail(DCP_PurOrderReturnListQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PurOrderReturnListQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PurOrderReturnListQueryReq>(){};
	}

	@Override
	protected DCP_PurOrderReturnListQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PurOrderReturnListQueryRes();
	}

	@Override
	protected DCP_PurOrderReturnListQueryRes processJson(DCP_PurOrderReturnListQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PurOrderReturnListQueryRes res = null;
		res = this.getResponse();
		String sql = null;
			
			sql = this.getCountSql(req);	
			String[] condCountValues = { }; //查詢條件
			//List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
			sql = this.getQuerySql(req);
			
			String[] conditionValues1 = { }; //查詢條件
			
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false) 
			{ 			
				Map<String, Object> total = getQDataDetail.get(0);
				String num = total.get("NUM").toString();
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
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
						
			sql = this.getLangTypeQuerySql(req);
	 
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(new ArrayList<DCP_PurOrderReturnListQueryRes.PayDate>());      
				
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_PurOrderReturnListQueryRes.PayDate oneLv1 = res.new PayDate();

					BigDecimal returnQtyBase = new BigDecimal(oneData.get("RETURNQTYBASE").toString());
					BigDecimal unitRatio = new BigDecimal(oneData.get("UNITRATIO").toString());
					BigDecimal returnQty = returnQtyBase.divide(unitRatio, 4, BigDecimal.ROUND_HALF_UP);
					BigDecimal stockInQty = new BigDecimal(oneData.get("STOCKINQTY").toString());
					BigDecimal canReturnQty = stockInQty.subtract(returnQty);

					BigDecimal purPrice = new BigDecimal(oneData.get("PURPRICE").toString());
					BigDecimal purPriceBase = purPrice.divide(unitRatio, 4, BigDecimal.ROUND_HALF_UP);

					oneLv1.setPurOrderNo(oneData.get("PURORDERNO").toString());
					oneLv1.setBDate(oneData.get("BDATE").toString());
					oneLv1.setItem(oneData.get("ITEM").toString());
					oneLv1.setListImage(oneData.get("LISTIMAGE").toString());
					oneLv1.setPluNo(oneData.get("PLUNO").toString());
					oneLv1.setPluName(oneData.get("PLU_NAME").toString());
					oneLv1.setSpec(oneData.get("SPEC").toString());
					oneLv1.setPluBarcode(oneData.get("PLUBARCODE").toString());
					oneLv1.setFeatureNo(oneData.get("FEATURENO").toString());
					oneLv1.setFeatureName(oneData.get("FEATURENAME").toString());
					oneLv1.setPurUnit(oneData.get("PURUNIT").toString());
					oneLv1.setPurUnitName(oneData.get("UNAME").toString());
					oneLv1.setPurQty(oneData.get("PURQTY").toString());
					oneLv1.setStockInQty(oneData.get("STOCKINQTY").toString());
					oneLv1.setReturnQty(returnQty.toString());
					oneLv1.setCanReturnQty(canReturnQty.toString());
					oneLv1.setRefPurPrice(purPriceBase.toString());
					oneLv1.setPurPrice(oneData.get("PURPRICE").toString());
					oneLv1.setTaxCode(oneData.get("TAXCODE").toString());
					oneLv1.setTaxRate(oneData.get("TAXRATE").toString());
					oneLv1.setInclTax(oneData.get("INCLTAX").toString());
					oneLv1.setBaseUnit(oneData.get("BASEUNIT").toString());
					oneLv1.setWUnit(oneData.get("WUNIT").toString());
					oneLv1.setBaseUnitName(oneData.get("BASEUNITNAME").toString());
					oneLv1.setUnitRatio(oneData.get("UNITRATIO").toString());
					oneLv1.setBaseUnitUdLength(oneData.get("UDLENGTH").toString());
					oneLv1.setTaxCalType(oneData.get("TAXCALTYPE").toString());
					oneLv1.setPurTemplateNo(oneData.get("PURTEMPLATENO").toString());
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
			}
			

		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PurOrderReturnListQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String purOrderNo = null;
		String receiptOrgNo = null; 
		String purOrgNo = null; 
		String eId = req.geteId();
		String supplier = null;
		String dateType = null;
		String bDate = null;
		String eDate = null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			supplier = req.getRequest().getSupplierNo();
			purOrderNo = req.getRequest().getPurOrderNo();
			receiptOrgNo =  req.getRequest().getReceiptOrgNo();
			purOrgNo =  req.getRequest().getPurOrgNo();
			dateType =  req.getRequest().getDateType();
			bDate =  req.getRequest().getBeginDate();
			eDate =  req.getRequest().getEndDate();
		}

		//采购单已入库量是业务单位  退货量是基准单位
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT COUNT( a.PURORDERNO ) OVER() NUM ,row_number()  over (order by  a.PURORDERNO, a.BDATE, b.ITEM ) AS rn    "					  
		+ "  , A.PURORDERNO,A.BDATE,B.ITEM,E.LISTIMAGE,B.PLUNO,F.PLU_NAME,G.SPEC,B.PLUBARCODE,B.PURTEMPLATENO "
		+ " ,B.FEATURENO,H.FEATURENAME,B.PURUNIT,I.UNAME,B.PURQTY,SUM(BB.STOCKINQTY) AS STOCKINQTY,SUM(BB.RETURNQTY) AS RETURNQTYBASE "
		+ " ,SUM(BB.STOCKINQTY*BB.UNITRATIO-BB.RETURNQTY) as CANRETURNBASE,nvl(sum(C.PQTY+d.PQTY),0) as DJ,B.PURPRICE,B.TAXCODE,B.TAXRATE,B.INCLTAX,G.BASEUNIT,G.WUNIT "
		+ " ,t.TAXCALTYPE,t1.UDLENGTH ,BB.UNITRATIO,i1.UNAME as baseUnitName FROM DCP_PURORDER A "
		+ " INNER JOIN DCP_PURORDER_DETAIL B ON A.EID=B.EID AND A.PURORDERNO=B.PURORDERNO "
		+ " INNER JOIN DCP_PURORDER_DELIVERY BB ON B.EID=BB.EID AND B.PURORDERNO=BB.PURORDERNO AND B.ITEM =BB.ITEM2 "
		//+ " LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL C ON c.SOURCEBILLNO=b.PURORDERNO and c.OITEM=b.ITEM "
		//+ " LEFT JOIN DCP_STOCKOUTNOTICE C1 ON c.EID=c1.EID and c.BILLNO=c1.BILLNO  and c1.STATUS='0' "
		+ "LEFT JOIN ( SELECT ZC1.SOURCEBILLNO, OITEM,ZC.EID,SUM(PQTY) PQTY "
		+ "        FROM DCP_STOCKOUTNOTICE ZC INNER JOIN DCP_STOCKOUTNOTICE_DETAIL ZC1 "
        + "          ON     Zc.EID = Zc1.EID  AND Zc.BILLNO = Zc1.BILLNO AND Zc1.STATUS = '0' "
        + "        GROUP BY  ZC1.SOURCEBILLNO, OITEM ,ZC.EID ) C   ON c.SOURCEBILLNO = b.PURORDERNO AND c.OITEM = b.ITEM  "         
		+ " LEFT JOIN DCP_SSTOCKOUT_DETAIL D ON D.ORIGINNO=b.PURORDERNO and D.ORIGINITEM=b.ITEM "
		+ " LEFT JOIN DCP_SSTOCKOUT  D1 ON D.EID=d1.EID and D.SSTOCKOUTNO=d1.SSTOCKOUTNO and d1.STATUS='0'  "
		+ " LEFT JOIN DCP_GOODSIMAGE E ON E.PLUNO=B.PLUNO "
		+ " LEFT JOIN DCP_GOODS_LANG F ON F.PLUNO=B.PLUNO AND F.LANG_TYPE='"+langType+"' "
		+ " LEFT JOIN DCP_GOODS G ON G.PLUNO=B.PLUNO  "
		+ " LEFT JOIN DCP_GOODS_FEATURE_LANG H ON H.PLUNO=B.PLUNO AND H.FEATURENO=B.FEATURENO AND H.LANG_TYPE='"+langType+"' " 
		+ " LEFT JOIN DCP_UNIT_LANG I  ON I.UNIT=B.PURUNIT AND I.LANG_TYPE='"+langType+"' "
		+ " LEFT JOIN DCP_TAXCATEGORY T ON t.EID=B.EID AND t.TAXCODE=B.TAXCODE "
		+ " LEFT JOIN DCP_UNIT T1 ON t1.EID=B.EID AND t1.UNIT=B.BASEUNIT   "
		+ " LEFT JOIN DCP_UNIT_LANG I1  ON I1.UNIT=B.BASEUNIT AND I1.LANG_TYPE='"+langType+"' "
		+ " LEFT JOIN DCP_GOODS_UNIT T2 ON t2.EID=B.EID AND t2.PLUNO=B.PLUNO  AND t2.OUNIT=B.PURUNIT   "
		+ "  WHERE  A.EID = '"+eId+"' " ); 
		if(supplier != null && supplier.length() > 0)
			sqlbuf.append(" and  A.SUPPLIER = '"+supplier+"' ");
		if(purOrderNo != null && purOrderNo.length() > 0)
			sqlbuf.append(" and A.PURORDERNO = '"+purOrderNo+"' ");
		if(receiptOrgNo != null && receiptOrgNo.length() > 0)
			sqlbuf.append(" and  A.RECEIPTORGNO = '"+receiptOrgNo+"' ");
		if(purOrgNo != null && purOrgNo.length() > 0)
			sqlbuf.append(" and A.ORGANIZATIONNO = '"+purOrgNo+"' ");
		if(key != null && key.length() > 0){
			sqlbuf.append(" and ( A.PURORDERNO  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" or  B.PLUNO  like  "+SUtil.RetLikeStr(key) ); 
			sqlbuf.append(" or  F.PLU_NAME  like  "+SUtil.RetLikeStr(key) ); 
			sqlbuf.append(" or  B.PLUBARCODE  like  "+SUtil.RetLikeStr(key)+" )"); 
		}
		if(Check.Null(receiptOrgNo)&& Check.Null(purOrgNo)){
			sqlbuf.append(" and ORGANIZATIONNO = '"+req.getOrganizationNO()+"' ");
		}
		if(dateType != null && dateType.length() > 0){
			if ("bDate".equals(dateType))
			  sqlbuf.append( SUtil.RetDateCon8("A.BDATE",bDate,eDate));
			if ("arrivalDate".equals(dateType))
				  sqlbuf.append( SUtil.RetDateCon8("BB.ARRIVALDATE",bDate,eDate));
		}
			
		sqlbuf.append("   GROUP BY A.PURORDERNO,A.BDATE,B.ITEM,E.LISTIMAGE,B.PLUNO,F.PLU_NAME,G.SPEC,B.PLUBARCODE,B.PURTEMPLATENO "
		+ " ,B.FEATURENO,H.FEATURENAME,B.PURUNIT,I.UNAME,B.PURQTY,BB.STOCKINQTY ,B.PURPRICE,B.TAXCODE,B.TAXRATE,B.INCLTAX,G.BASEUNIT,G.WUNIT "
		+ " ,t.TAXCALTYPE,t1.UDLENGTH ,BB.UNITRATIO,i1.UNAME "
		+ "  having SUM (BB.STOCKINQTY*BB.UNITRATIO - BB.RETURNQTY)- nvl(SUM (C.PQTY + d.PQTY),0)>0 "
				); 
		sqlbuf.append(" )AA where CANRETURNBASE-DJ>0 and rn > "+startRow +" AND rn  <= "+(startRow + pageSize)+" ");
		sqlbuf.append(" order by PURORDERNO,BDATE,ITEM  " );
				
		sql = sqlbuf.toString();
		//logger.info(sql);  
		return sql;
	}
	
	
	
	/**
	 * 新增要處理的資料(先加入的, 先處理)
	 * 
	 * @param row
	 */
	protected final void addProcessData(DataProcessBean row) {
		this.pData.add(row);
	}
	
	/**
	 * 计算总数
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected String getCountSql(DCP_PurOrderReturnListQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String key = null;
		String purOrderNo = null;
		String receiptOrgNo = null; 
		String purOrgNo = null; 
		String eId = req.geteId();
		String supplier = null;
		if(req.getRequest()!=null)
		{
			key =  req.getRequest().getKeyTxt();
			supplier = req.getRequest().getSupplierNo();
			purOrderNo = req.getRequest().getPurOrderNo();
			receiptOrgNo =  req.getRequest().getReceiptOrgNo();
			purOrgNo =  req.getRequest().getPurOrgNo();
		}
		
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}		
		StringBuffer sqlbuf=new StringBuffer("");
		String sql = "";
		
		sqlbuf.append("select count(*) AS num  from( select a.PURORDERNO   "
	 + " FROM DCP_PURORDER A "
	+ " INNER JOIN DCP_PURORDER_DETAIL B ON A.EID=B.EID AND A.PURORDERNO=B.PURORDERNO "
	+ " INNER JOIN DCP_PURORDER_DELIVERY BB ON B.EID=BB.EID AND B.PURORDERNO=BB.PURORDERNO AND B.ITEM =BB.ITEM2 "
	+ " LEFT JOIN DCP_STOCKOUTNOTICE_DETAIL C ON c.SOURCEBILLNO=b.PURORDERNO and c.OITEM=b.ITEM "
	+ " LEFT JOIN DCP_SSTOCKOUT_DETAIL D ON D.ORIGINNO=b.PURORDERNO and D.ORIGINITEM=b.ITEM "
	+ " LEFT JOIN DCP_GOODSIMAGE E ON E.PLUNO=B.PLUNO "
	+ " LEFT JOIN DCP_GOODS_LANG F ON F.PLUNO=B.PLUNO AND F.LANG_TYPE='"+langType+"' "
	+ " LEFT JOIN DCP_GOODS G ON G.PLUNO=B.PLUNO  "
	+ " LEFT JOIN DCP_GOODS_FEATURE_LANG H ON H.PLUNO=B.PLUNO AND H.FEATURENO=B.FEATURENO AND H.LANG_TYPE='"+langType+"' " 
	+ " LEFT JOIN DCP_UNIT_LANG I  ON I.UNIT=B.PURUNIT AND I. LANG_TYPE='"+langType+"' "
	+ "  WHERE  a.EID = '"+eId+"' " );
	if(supplier != null && supplier.length() > 0)
		sqlbuf.append(" and  a.SUPPLIER = '"+supplier+"' ");
	if(purOrderNo != null && purOrderNo.length() > 0)
		sqlbuf.append(" and PURORDERNO = '"+purOrderNo+"' ");
	if(receiptOrgNo != null && receiptOrgNo.length() > 0)
		sqlbuf.append(" and  RECEIPTORGNO = '"+receiptOrgNo+"' ");
	if(purOrgNo != null && purOrgNo.length() > 0)
		sqlbuf.append(" and a.ORGANIZATIONNO = '"+purOrgNo+"' ");
	if(key != null && key.length() > 0){
		sqlbuf.append(" and ( PURORDERNO  like  "+SUtil.RetLikeStr(key)); 
		sqlbuf.append(" or  PLUNO  like  "+SUtil.RetLikeStr(key) ); 
		sqlbuf.append(" or  PLU_NAME  like  "+SUtil.RetLikeStr(key) ); 
		sqlbuf.append(" or  PLUBARCODE  like  "+SUtil.RetLikeStr(key)+" )"); 
  
	}
	if(Check.Null(receiptOrgNo)&& Check.Null(purOrgNo)){
		sqlbuf.append(" and ORGANIZATIONNO = '"+req.getOrganizationNO()+"' ");
	}
	sqlbuf.append( " GROUP BY A.PURORDERNO,A.BDATE,B.ITEM,E.LISTIMAGE,B.PLUNO,F.PLU_NAME,G.SPEC,B.PLUBARCODE "
	+ " ,B.FEATURENO,H.FEATURENAME,B.PURUNIT,I.UNAME,B.PURQTY,BB.STOCKINQTY ,B.PURPRICE,B.TAXCODE,B.TAXRATE,B.INCLTAX,G.BASEUNIT,G.WUNIT "
	+ " ) AA  " ); 
		sql= sqlbuf.toString();
		return sql;
 
	}
	
	
	 
	protected String getLangTypeQuerySql(DCP_PurOrderReturnListQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType =req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType="zh_CN";
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String key = null;
		String status = null; 
		String eId = req.geteId();
		String type = null;
		  
		return sql;
	}
	
	
}
