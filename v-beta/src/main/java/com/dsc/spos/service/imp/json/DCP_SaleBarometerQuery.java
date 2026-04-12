package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SaleBarometerQueryReq;
import com.dsc.spos.json.cust.res.DCP_SaleBarometerQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_SaleBarometerQuery extends SPosBasicService<DCP_SaleBarometerQueryReq, DCP_SaleBarometerQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_SaleBarometerQueryReq req) throws Exception 
	{	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；		
		
		if (req.getStartDate1()==null) 
		{
			errCt++;
			errMsg.append("开始日期1不能为Null, ");
			isFail = true;
		} 
		
		if (req.getEndDate1()==null) 
		{
			errCt++;
			errMsg.append("结束日期1不能为Null, ");
			isFail = true;
		} 
		
		if (req.getStartDate2()==null) 
		{
			errCt++;
			errMsg.append("开始日期2不能为Null, ");
			isFail = true;
		} 
		
		if (req.getEndDate2()==null) 
		{
			errCt++;
			errMsg.append("结束日期2不能为Null, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	
		return false;
		
	}

	@Override
	protected TypeToken<DCP_SaleBarometerQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleBarometerQueryReq>(){};
	}

	@Override
	protected DCP_SaleBarometerQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleBarometerQueryRes();
	}

	@Override
	protected DCP_SaleBarometerQueryRes processJson(DCP_SaleBarometerQueryReq req) throws Exception 
	{
		String sql=null;
		DCP_SaleBarometerQueryRes res=null;
		res = this.getResponse();
		
		sql = this.getQuerySql(req);
		String[] condCountValues = { }; //查詢條件
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, condCountValues);
		List<DCP_SaleBarometerQueryRes.level1Elm> oneLv1=new ArrayList<DCP_SaleBarometerQueryRes.level1Elm>();
		
		if (getQData_Count != null && getQData_Count.isEmpty() == false) 
		{
			Map<String, Object> oneData_CountX1 = getQData_Count.get(0);
						
			DCP_SaleBarometerQueryRes.level1Elm oneLv= res.new level1Elm();
			
			String count1=oneData_CountX1.get("COUNT1").toString();
			String count2=oneData_CountX1.get("COUNT2").toString();
			
			String samt1=oneData_CountX1.get("SAMT1").toString();
			String sqty1=oneData_CountX1.get("SQTY1").toString();
			String samt2=oneData_CountX1.get("SAMT2").toString();
			String sqty2=oneData_CountX1.get("SQTY2").toString();
			
			String ramt2=oneData_CountX1.get("RAMT2").toString();
			String rqty2=oneData_CountX1.get("RQTY2").toString();
			String rcount2=oneData_CountX1.get("RCOUNT2").toString();
			String ramt1=oneData_CountX1.get("RAMT1").toString();
			String rqty1=oneData_CountX1.get("RQTY1").toString();
			String rcount1=oneData_CountX1.get("RCOUNT1").toString();
			
			oneLv.setCount1(count1);
			oneLv.setCount2(count2);
			
			oneLv.setsAmt1(samt1);
			oneLv.setsAmt2(samt2);
			oneLv.setsQty1(sqty1);
			oneLv.setsQty2(sqty2);
			
			oneLv.setrAmt1(ramt1);
			oneLv.setrAmt2(ramt2);
			oneLv.setrQty1(rqty1);
			oneLv.setrQty2(rqty2);
			
			oneLv1.add(oneLv);			
		}
		res.setDatas(oneLv1);
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
	
	}

	/**
	 * 查询汇总
	 */
	@Override
	protected String getQuerySql(DCP_SaleBarometerQueryReq req) throws Exception 
	{
		String sql = null;

		String oShopId = req.getoShopId();
		String StartDate1 = req.getStartDate1();
		String StartDate2 = req.getStartDate2();
		String EndDate1 = req.getEndDate1();
		String EndDate2 = req.getEndDate2();

		if(oShopId == null)
			oShopId = "";
		if(StartDate1 == null) 
			StartDate1 = "";
		if(StartDate2 == null) 
			StartDate2 = "";
		if(EndDate1 == null) 
			EndDate1 = "";
		if(EndDate2 == null) 
			EndDate2 = "";

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select samt1,sqty1,count1,ramt1, rqty1,rcount1,samt2,sqty2,count2,ramt2,rqty2,rcount2 from "
				+ "("
				+ "select 1 as id,nvl(sum(tot_amt),0)  as samt1, nvl(sum(TOT_QTY),0) as sqty1,nvl(count(*),0) as count1 FROM  DCP_SALE "
				+ "WHERE TYPE in ('0','6') ");

		if (StartDate1 != null && StartDate1.length()>0)
		{
			sqlbuf.append("and bdate>='"+StartDate1+"' ");
		}
		if (EndDate1 != null && EndDate1.length()>0)
		{
			sqlbuf.append("and bdate<='"+EndDate1+"'");
		}
		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append("and SHOPID='"+oShopId+"' ");
		}

		sqlbuf.append("and EID='"+req.geteId()+"'"
				+ ") a inner join "
	
				+ "("
				+ "select 1 as id,nvl(sum(tot_amt),0) as ramt1,nvl(sum(TOT_QTY),0) as rqty1,nvl(count(*),0) as rcount1 FROM  DCP_SALE "
				+ "WHERE TYPE in ('1','2','5') ");

		if (StartDate1 != null && StartDate1.length()>0)
		{
			sqlbuf.append("and bdate>='"+StartDate1+"' ");
		}
		if (EndDate1 != null && EndDate1.length()>0)
		{
			sqlbuf.append("and bdate<='"+EndDate1+"'");
		}
		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append("and SHOPID='"+oShopId+"' ");
		}

		sqlbuf.append("and EID='"+req.geteId()+"'"
				+ ") c on a.id=c.id inner join "
				+ "("
				+ "select 1 as id ,nvl(sum(tot_amt),0)  as samt2, nvl(sum(TOT_QTY),0) as sqty2,nvl(count(*),0) as count2 FROM  DCP_SALE "
				+ "WHERE TYPE in ('0','6') ");

		if (StartDate2 != null && StartDate2.length()>0)
		{
			sqlbuf.append( "and bdate>='"+StartDate2+"' ");
		}
		if (EndDate2 != null && EndDate2.length()>0)
		{
			sqlbuf.append("and bdate<='"+EndDate2+"' ");
		}
		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append( "and SHOPID='"+oShopId+"' ");
		}

		sqlbuf.append("and EID='"+req.geteId()+"'"
				+ ") e on c.id=e.id inner join "
				+ "("
				+ "select 1 as id ,nvl(sum(tot_amt),0) as ramt2,nvl(sum(TOT_QTY),0) as rqty2, nvl(count(*),0) as rcount2 FROM  DCP_SALE "
				+ "WHERE TYPE in ('1','2','5') ");

		if (StartDate2 != null && StartDate2.length()>0)
		{
			sqlbuf.append( "and bdate>='"+StartDate2+"' ");
		}
		if (EndDate2 != null && EndDate2.length()>0)
		{
			sqlbuf.append("and bdate<='"+EndDate2+"' ");
		}
		if (oShopId != null && oShopId.length()>0)
		{
			sqlbuf.append( "and SHOPID='"+oShopId+"' ");
		}

		sqlbuf.append( "and EID='"+req.geteId()+"'"
				+ ") g on e.id=g.id ");

		sql=sqlbuf.toString();

		return sql;
	}
}
