package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_InvoiceQueryReq;
import com.dsc.spos.json.cust.res.DCP_InvoiceQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：InvoiceGetDCP
 * 服务说明：发票簿查询
 * @author jinzma 
 * @since  2019-03-01
 */
public class DCP_InvoiceQuery  extends  SPosBasicService<DCP_InvoiceQueryReq,DCP_InvoiceQueryRes > {


	@Override
	protected boolean isVerifyFail(DCP_InvoiceQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String year = req.getRequest().getYear();

		if(Check.Null(year)){
			errMsg.append("发票年度不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_InvoiceQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_InvoiceQueryReq>(){};
	}

	@Override
	protected DCP_InvoiceQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_InvoiceQueryRes();
	}


	@Override
	protected DCP_InvoiceQueryRes processJson(DCP_InvoiceQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;			
		DCP_InvoiceQueryRes res = this.getResponse();	
		try
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				res.setDatas(new ArrayList<DCP_InvoiceQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQData) 
				{
					DCP_InvoiceQueryRes.level1Elm oneLv1 = res.new level1Elm(); 
					
					String sellerGuiNO = oneData.get("SELLERGUINO").toString();
					String year = oneData.get("YEAR").toString();
					String startMonth = oneData.get("STARTMONTH").toString();
					String endMonth = oneData.get("ENDMONTH").toString();
					String invStartNO = oneData.get("INVSTARTNO").toString();
					String invEndNO = oneData.get("INVENDNO").toString();
					String invType = oneData.get("INVTYPE").toString();
					String invshopNO = oneData.get("SHOPID").toString();
					String invshopName = oneData.get("ORG_NAME").toString();
					String invMachineNO = oneData.get("MACHINE").toString();
					String isUsed = oneData.get("ISUSED").toString();
					if(Check.Null(isUsed) ||isUsed.isEmpty() )
					{
						isUsed="N";
					}
					String invCount = oneData.get("INVCOUNT").toString();
					String invLastNO = oneData.get("INVLASTNO").toString();
					String invRecipient = oneData.get("INVRECIPIENT").toString();
					String invLoad = oneData.get("INVLOAD").toString();
					String status = oneData.get("STATUS").toString();
					
					oneLv1.setSellerGuiNo(sellerGuiNO);
					oneLv1.setYear(year);
					oneLv1.setStartMonth(startMonth);
					oneLv1.setEndMonth(endMonth);					
					oneLv1.setInvStartNo(invStartNO);
					oneLv1.setInvEndNo(invEndNO);
					oneLv1.setInvType(invType);
					oneLv1.setInvshopNo(invshopNO);					
					oneLv1.setInvshopName(invshopName);
					oneLv1.setInvMachineNo(invMachineNO);
					oneLv1.setIsUsed(isUsed);
					oneLv1.setInvCount(invCount);					
					oneLv1.setInvLastNo(invLastNO);
					oneLv1.setInvRecipient(invRecipient);
					oneLv1.setInvLoad(invLoad);
					oneLv1.setStatus(status);

					res.getDatas().add(oneLv1);
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_InvoiceQueryRes.level1Elm>());				
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
	protected String getQuerySql(DCP_InvoiceQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		DCP_InvoiceQueryReq.levelElm request = req.getRequest();
		String year=request.getYear();
		String startMonth = request.getStartMonth();
		String endMonth = request.getEndMonth();
		String sellerGuiNO = request.getSellerGuiNo();
		String isUsed= request.getIsUsed();
		String invLoad=request.getInvLoad();
		String langType=req.getLangType();
		
		String oShopId = request.getoShopId() == null?"": request.getoShopId();
		
		
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append( " select * from ( "
				+ " select count(*) over() num,row_number() over (order by a.year,a.startmonth) rn,a.*, b.org_name from DCP_INVOICEBOOK a "
				+ " left join DCP_ORG_lang b on a.EID=b.EID and a.SHOPID=b.organizationno and b.status='100' and b.lang_type='"+langType+"' "
				+ " where a.EID='"+eId+"' and a.year='"+year+"' and a.status='100' " );

		if (!Check.Null(startMonth))
		{
			sqlbuf.append(" and a.startmonth>='"+startMonth+"'  ");
		}
		if (!Check.Null(endMonth))
		{
			sqlbuf.append(" and a.endmonth<='"+endMonth+"'   ");
		}
		if (!Check.Null(sellerGuiNO))
		{
			sqlbuf.append(" and a.sellerguino='"+sellerGuiNO+"'  ");
		}
		if (!Check.Null(isUsed))
		{
			sqlbuf.append(" and a.isused='"+isUsed+"'  ");
		}
		if (!Check.Null(invLoad))
		{
			sqlbuf.append(" and a.Invload='"+invLoad+"'  ");
		}
		
		if (!Check.Null(invLoad))
		{
			sqlbuf.append(" and a.Invload='"+invLoad+"'  ");
		}
		
		if (!Check.Null(oShopId))
		{
			sqlbuf.append(" and a.SHOPID = '"+oShopId+"'  ");
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn> "+ startRow +"  and rn<= "+ (startRow+pageSize) );
		sqlbuf.append(" order by year,startmonth,endmonth,sellerguino ");

		sql = sqlbuf.toString();
		return sql;		
	}





}
