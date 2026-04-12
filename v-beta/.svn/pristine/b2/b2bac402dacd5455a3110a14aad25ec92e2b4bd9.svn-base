package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsSaleRankQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSaleRankQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSaleRankQuery extends SPosBasicService<DCP_GoodsSaleRankQueryReq, DCP_GoodsSaleRankQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_GoodsSaleRankQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；	

		if (req.getStartDate1()==null) 
		{
			errCt++;
			errMsg.append("开始日期不能为Null, ");
			isFail = true;
		} 

		if (req.getEndDate1()==null) 
		{
			errCt++;
			errMsg.append("结束日期不能为Null, ");
			isFail = true;
		} 

		if (req.getGetType()==null) 
		{
			errCt++;
			errMsg.append("查询类型不能为Null, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsSaleRankQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsSaleRankQueryReq>(){};
	}

	@Override
	protected DCP_GoodsSaleRankQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsSaleRankQueryRes();
	}

	@Override
	protected DCP_GoodsSaleRankQueryRes processJson(DCP_GoodsSaleRankQueryReq req) throws Exception 
	{		
		String sql = null;

		DCP_GoodsSaleRankQueryRes res=null;

		res = this.getResponse();

		//单头总数
		sql = this.getCountSql(req);				

		String[] condCountValues = { }; //查詢條件
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, condCountValues);
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false) 
		{ 			
			Map<String, Object> oneData_Count = getQData_Count.get(0);
			String num = oneData_Count.get("NUM").toString();
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

		sql=this.getQuerySql(req);

		String[] conditionValues1 = {}; //查詢條件
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues1);

		List<DCP_GoodsSaleRankQueryRes.level1Elm> oneLv1 = new ArrayList<DCP_GoodsSaleRankQueryRes.level1Elm>();

		if (getQData != null && getQData.isEmpty() == false)	
		{
			res.setDatas(new ArrayList<DCP_GoodsSaleRankQueryRes.level1Elm>());
			for (Map<String, Object> oneData : getQData) 
			{
				DCP_GoodsSaleRankQueryRes.level1Elm oneLv=res.new level1Elm();

				String pluno = oneData.get("PLUNO").toString();
				String pluName = oneData.get("PLUNAME").toString();
				String featureNO = oneData.get("FEATURENO").toString();
				String FeatureName="";
				String FEATURE_SET = oneData.get("FEATURE_SET").toString();
				String FIRST_NAME = oneData.get("FIRST_NAME").toString();
				String LAST_NAME = oneData.get("LAST_NAME").toString();
				//取特征码名称(条码表特征码是组合起来的 红色_XL)
				if(Check.Null(featureNO)==false && featureNO.trim().equals("")==false)
				{
					String sqlFeaturename=getFeaturename(req,FEATURE_SET,FIRST_NAME,LAST_NAME);
					List<Map<String, Object>> getQDataFeaturename = this.doQueryData(sqlFeaturename, null);
					if (getQDataFeaturename != null && getQDataFeaturename.isEmpty() == false)	
					{						
						FeatureName=getQDataFeaturename.get(0).get("FEATURE_NAME").toString();
					}
				}				


				String sAmt = oneData.get("AMT").toString();
				String sQty = oneData.get("SQTY").toString();
				String wqty = oneData.get("WQTY").toString();			

				oneLv.setPluno(pluno);
				oneLv.setPluName(pluName);
				oneLv.setsAmt(sAmt);
				oneLv.setsQty(sQty);
				oneLv.setFeatureNO(featureNO);
				oneLv.setFeatureName(FeatureName);
				oneLv.setwQty(wqty);
				res.getDatas().add(oneLv);
				oneLv = null;
			}		
		}
		else{
			res.setDatas(new ArrayList<DCP_GoodsSaleRankQueryRes.level1Elm>());
		}		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_GoodsSaleRankQueryReq req) throws Exception 
	{		
		String sql = null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow = (pageNumber-1) * pageSize;
		String StartDate1 = req.getStartDate1();
		String EndDate1 = req.getEndDate1();
		String oShopId = req.getoShopId();
		String GetType = req.getGetType();
		String langType = req.getLangType();

		if(StartDate1==null) StartDate1="";
		if(EndDate1==null) EndDate1="";
		if(oShopId==null) oShopId="";
		if(GetType==null) GetType="";
		String getInstantData = null;
		sql = this.getParaSql(req);			//是否实时查询参数
		String[] getParaSql = {oShopId}; 			//查詢條件
		List<Map<String, Object>> getInstantData_sql = this.doQueryData(sql, getParaSql);
		if (getInstantData_sql != null && getInstantData_sql.isEmpty() == false) 
		{
			getInstantData = (String) getInstantData_sql.get(0).get("ITEMVALUE");			
		}
		else
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置Get_Instant_Data参数");
		}

		StringBuffer sqlbuf=new StringBuffer("");

		if (GetType.equals("1"))   ////畅销查询
		{
			sqlbuf.append("select EID,pluName,pluNO,featureNO,sQty,amt,wQty,feature_set,FIRST_NAME,LAST_NAME from ("
					+ " select a.EID,b.plu_Name as pluName,a.pluno,a.featureNO,sum(a.qty) as sqty,sum(a.amt) as amt,sum(wqty) as wqty,t2.feature_set,SUBSTR(a.featureNO, 1, INSTR(a.featureNO, '_')-1) FIRST_NAME,   SUBSTR(a.featureNO, INSTR(a.featureNO, '_')+1) LAST_NAME from ( "
					+ " select EID,SHOPID,pluno,featureNO,SUM(qty) as qty,sum(amt) as amt,sum(wqty) as wqty from( "
					+ " select b.EID,b.SHOPID ,pluNO,nvl(featureNO,cast(' ' as nvarchar2(40))) as featureno ,(case when doc_type='0' then qty else -qty end) as qty,(case when doc_type='0' then amt else -amt end) as amt,0 as wqty from DCP_settlement_detail a "
					+ " inner join DCP_settlement b on a.EID=b.EID and a.SHOPID=b.SHOPID "
					+ " and a.settlementno=b.settlementno and a.type=b.type "
					+ " where a.EID='"+req.geteId()+"' "
					);

			if (oShopId != null && oShopId.length()>0)
			{
				sqlbuf.append("and b.SHOPID='"+oShopId+"' ");
			}
			if (StartDate1 != null && StartDate1.length()>0)
			{
				sqlbuf.append( "and b.edate>='"+StartDate1+"' ");
			}

			if (EndDate1 != null && EndDate1.length()>0)
			{
				sqlbuf.append( "and b.edate<='"+EndDate1+"' ");
			}

			if(getInstantData.equals("Y"))
			{			
				sqlbuf.append(" union all  "
						+ " select EID,organizationno as SHOPID,pluNO,nvl(featureNO,cast(' ' as nvarchar2(40))) as featureno ,(case when doc_type='20' then wqty when doc_type='21' then -wqty else 0 end) as qty,(case when doc_type='20' then amt when doc_type='21' then -amt  else 0 end) as amt ,(case when stock_type='0' then wqty else -wqty end) as wqty from DCP_stock_detail a "
						+ " where a.EID='"+req.geteId()+"' and a.doc_type in ('20','21')  "
						);

				if (oShopId != null && oShopId.length()>0)
				{
					sqlbuf.append("and a.organizationno='"+oShopId+"' ");
				}
				if (StartDate1 != null && StartDate1.length()>0)
				{
					sqlbuf.append( "and bdate>='"+StartDate1+"' ");
				}

				if (EndDate1 != null && EndDate1.length()>0)
				{
					sqlbuf.append( "and bdate<='"+EndDate1+"' ");
				}
			}

			sqlbuf.append( " ) a group by pluno,featureno,EID,SHOPID "
					+ " ) a inner join DCP_GOODS_lang b on a.EID=b.EID and a.pluno=b.pluno and b.lang_Type= '"+langType+"' "
					+ " left join (select distinct PLUNO, FEATURENO,EID, FEATURENAME  from DCP_BARCODE WHERE status='100' ) e on a.EID=e.EID and a.pluno=e.pluno and a.featureNO=e.featureno "
					+ " left join DCP_stock_day c on a.EID=c.EID and a.SHOPID=c.organizationno and a.pluno=c.pluno and a.featureno=c.featureno "
					+ " LEFT JOIN DCP_GOODS t2 on a.EID=t2.EID and a.pluno=t2.pluno "
					+ " where a.pluno||a.featureno in "
					+ " (select plunofeature from ( "
					+ "    select rn,plunofeature from ( "
					+ "      select rownum rn ,plunofeature from ( "
					+ "        select pluno||featureno as plunofeature,qty from ( "
					+ "         select  pluno, featureno ,sum(qty) as qty from ( " 
					+ " select  pluno,nvl(featureNO,cast(' ' as nvarchar2(40))) as featureno ,(case when doc_type='0' then qty else -qty end) as qty from DCP_settlement_detail a "
					+ " inner join DCP_settlement b  on a.EID=b.EID  and a.SHOPID=b.SHOPID "
					+ " and a.settlementno=b.settlementno and a.type=b.type "
					+ " where a.EID='"+req.geteId()+"' ");

			if (oShopId != null && oShopId.length()>0)
			{
				sqlbuf.append("and b.SHOPID='"+oShopId+"' ");
			}
			if (StartDate1 != null && StartDate1.length()>0)
			{
				sqlbuf.append( "and b.edate>='"+StartDate1+"' ");
			}
			if (EndDate1 != null && EndDate1.length()>0)
			{
				sqlbuf.append( "and b.edate<='"+EndDate1+"' ");
			}				

			if(getInstantData.equals("Y"))
			{
				sqlbuf.append(" union all  "
						+ "  select pluNO ,nvl(featureNO,cast(' ' as nvarchar2(40))) as featureno ,(case when doc_type='20' then wqty when doc_type='21' then -wqty else 0 end) as qty from DCP_stock_detail a "
						+ " where a.EID='"+req.geteId()+"' and a.doc_type in ('20','21') "
						);

				if (oShopId != null && oShopId.length()>0)
				{
					sqlbuf.append("and a.organizationno='"+oShopId+"' ");
				}

				if (StartDate1 != null && StartDate1.length()>0)
				{
					sqlbuf.append( "and bdate>='"+StartDate1+"' ");
				}

				if (EndDate1 != null && EndDate1.length()>0)
				{
					sqlbuf.append( "and bdate<='"+EndDate1+"' ");
				}
			}
			sqlbuf.append(" ) a ");
			sqlbuf.append( "group by  a.pluno,a.featureno ) "
					+ "order by qty desc,pluno asc ,featureNO asc  ) ");
			sqlbuf.append("  )) where rn>"+startRow+" and rn<="+(startRow+pageSize)+" )  	");
			sqlbuf.append( "group by b.plu_Name,a.pluno,a.featureno,e.featurename,a.EID,t2.feature_set "
					+ " order by sqty desc, pluno asc , featureNO asc   " );
			sqlbuf.append( " )  "); 
		}
		else    ///滞销查询 
		{
			sqlbuf.append("select EID,pluName,pluNO,featureNO,sQty,amt,wQty,feature_set,FIRST_NAME,LAST_NAME from "
					+ "(select  a.EID,b.plu_Name as pluName,a.pluno,a.featureNO,sum(a.qty) as sqty,sum(a.amt) as amt,"
					+ "sum(wqty) as wqty,t2.feature_set,SUBSTR(a.featureNO, 1, INSTR(a.featureNO, '_')-1) FIRST_NAME,   SUBSTR(a.featureNO, INSTR(a.featureNO, '_')+1) LAST_NAME"
					+ " from (select EID,SHOPID,pluno,featureNO,"
					+ "SUM(nvl(qty,0)) as qty,sum(amt) as amt,sum(wqty) as wqty  "
					+ "from ( select ff.EID,ff.organizationno as SHOPID ,ff.pluNO,"
					+ "nvl(ff.featureNO,cast(' ' as nvarchar2(40))) as featureno ,  "
					+ "(case when doc_type='0' then nvl(qty,0) else -nvl(qty,0) end) as qty,"
					+ "(case when doc_type='0' then nvl(amt,0) else -nvl(amt,0) end) as amt,"
					+ "0 as wqty from DCP_settlement_detail a  ");
			sqlbuf.append("inner join DCP_settlement b on a.EID=b.EID and a.SHOPID=b.SHOPID "
					+ " and a.settlementno=b.settlementno and a.type=b.type and  "
					+ "(b.edate>='"+StartDate1+"' and b.edate<='"+EndDate1+"' ) ");
			sqlbuf.append(" right join  (select bb.EID,bb.pluno,aa.organizationno,bb.featureno,"
					+ "bb.featurename,bb.unit from DCP_GOODS_shop aa inner join (select distinct PLUNO, FEATURENO,EID, FEATURENAME,UNIT,status  from DCP_BARCODE WHERE status='100' ) bb on aa.EID=bb.EID "
					+ "and aa.pluno=bb.pluno and aa.fsal='Y'and aa.status='100' and bb.status='100' )  ff "
					+ "on a.EID=ff.EID and a.SHOPID=ff.organizationno and a.pluno=ff.pluno and "
					+ "a.featureno=ff.featureno and a.unit=ff.unit where ff.EID='"+req.geteId()+"' and "
					+ "ff.organizationno='"+oShopId+"'     ");

			if(getInstantData.equals("Y"))
			{ 
				sqlbuf.append("union all select EID,organizationno as SHOPID,pluNO,"
						+ "nvl(featureNO,cast(' ' as nvarchar2(40))) as featureno , "
						+ "(case when doc_type='20' then wqty when doc_type='21' then -wqty else 0 end) as qty,"
						+ "(case when doc_type='20' then amt when doc_type='21' then -amt  else 0 end) as amt ,"
						+ "(case when stock_type='0' then wqty else -wqty end) as wqty from DCP_stock_detail a   "
						+ "where a.EID='"+req.geteId()+"'  and a.doc_type in ('20','21') "
						+ "and a.organizationno='"+oShopId+"' "
						+ "and bdate>='"+StartDate1+"' and bdate<='"+EndDate1+"' ");
			}
			sqlbuf.append("  ) a group by pluno,featureno,EID,SHOPID  ) a "
					+ " inner join DCP_GOODS_lang b on a.EID=b.EID and a.pluno=b.pluno and b.lang_Type= '"+langType+"' "
					//+ " left join DCP_BARCODE e on a.EID=e.EID and a.pluno=e.pluno and "
					//+ " a.featureNO=e.featureno   "
					+ " left join (select distinct PLUNO, FEATURENO,EID, FEATURENAME  from DCP_BARCODE WHERE status='100' ) e on a.EID=e.EID and a.pluno=e.pluno and "
					+ " a.featureNO=e.featureno   "
					+ "left join DCP_stock_day c on a.EID=c.EID "					
					+ " and a.SHOPID=c.organizationno and a.pluno=c.pluno and a.featureno=c.featureno  "
					+ " LEFT JOIN DCP_GOODS t2 on a.EID=t2.EID and a.pluno=t2.pluno ");

			sqlbuf.append("where a.pluno||a.featureno in  ( select plunofeature from ( "
					+ "select rn,plunofeature from ( select rownum rn ,plunofeature from (  "
					+ "select pluno||featureno as plunofeature,qty from ( select  pluno, featureno ,"
					+ "sum( nvl(qty,0)) as qty from ( select  ff.pluno,nvl(ff.featureNO,"
					+ "cast(' ' as nvarchar2(40))) as featureno ,  "
					+ "(case when doc_type='0' then nvl(qty,0) else -nvl(qty,0) end) as qty "
					+ "from DCP_settlement_detail a  "
					+ "inner join DCP_settlement b  on a.EID=b.EID  and a.SHOPID=b.SHOPID  "
					+ "and a.settlementno=b.settlementno and a.type=b.type "
					+ "and (b.edate>='"+StartDate1+"' and b.edate<='"+EndDate1+"')");

			sqlbuf.append("	right join  (select bb.EID,bb.pluno,aa.organizationno,"
					+ "bb.featureno,bb.featurename,bb.unit from DCP_GOODS_shop aa inner join (select distinct PLUNO, FEATURENO,EID, FEATURENAME,UNIT,status  from DCP_BARCODE WHERE status='100' ) bb "
					+ "on aa.EID=bb.EID and aa.pluno=bb.pluno and aa.fsal='Y'and aa.status='100' "
					+ "and bb.status='100' ) ff on a.EID=ff.EID and a.SHOPID=ff.organizationno "
					+ "and a.pluno=ff.pluno and a.featureno=ff.featureno and a.unit=ff.unit "
					+ "inner join DCP_GOODS_lang cc on ff.EID=cc.EID and ff.pluno=cc.pluno and cc.lang_Type= '"+langType+"'   "
					+ "where ff.EID='"+req.geteId()+"'  and ff.organizationno='"+oShopId+"'   ");

			if(getInstantData.equals("Y"))
			{
				sqlbuf.append(" union all select pluNO ,nvl(featureNO,cast(' ' as nvarchar2(40))) as featureno ,"
						+ "(case when doc_type='20' then wqty when doc_type='21' then -wqty else 0 end) as qty "
						+ "from DCP_stock_detail a   where a.EID='"+req.geteId()+"' and a.doc_type in ('20','21') "
						+ "and a.organizationno='"+oShopId+"' and bdate>='"+StartDate1+"' and bdate<='"+EndDate1+"' ");
			}

			sqlbuf.append(" ) a group by  a.pluno,a.featureno  ) order by qty asc ,pluno asc ,featureNO asc )"
					+ "  ) ) where rn>"+startRow+" and rn<="+(startRow+pageSize)+"  )  group by b.plu_Name,a.pluno,a.featureno,e.featurename,a.EID,t2.feature_set "
					+ " order by sqty asc ,pluno asc ,featureNO asc   )               ");
		}

		sql=sqlbuf.toString();

		return sql;
	}
	protected String getCountSql(DCP_GoodsSaleRankQueryReq req) throws Exception
	{
		String sql=null;
		String StartDate1=req.getStartDate1();
		String EndDate1=req.getEndDate1();
		String oShopId= req.getoShopId();
		String GetType= req.getGetType();
		String langType = req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");

		if (GetType.equals("1"))  ///畅销品查询时传1    滞销品查询时传2
		{
			if(StartDate1==null) 
				StartDate1="";
			if(EndDate1==null) 
				EndDate1="";
			if(oShopId==null) 
				oShopId="";
			String getInstantData = null;

			sql = this.getParaSql(req);	//查询总笔数
			String[] getParaSql = {oShopId}; //查詢條件
			List<Map<String, Object>> getInstantData_sql = this.doQueryData(sql, getParaSql);
			if (getInstantData_sql != null && getInstantData_sql.isEmpty() == false) 
			{
				getInstantData = (String) getInstantData_sql.get(0).get("ITEMVALUE");			
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置Get_Instant_Data参数");
			}
			sqlbuf.append("select num from ("
					+ "select count(*) as num from ("
					+ "select distinct pluNO,featureno,unit  from("
					+ " select  pluno,featureno,unit from DCP_settlement_detail a "
					+ " inner join DCP_settlement b  on a.EID=b.EID  and a.SHOPID=b.SHOPID "
					+ " and a.settlementno=b.settlementno and a.type=b.type "
					+ " where a.EID='"+req.geteId()+"' ");

			if (oShopId != null && oShopId.length()>0)
			{
				sqlbuf.append("and b.SHOPID='"+oShopId+"' ");
			}
			if (StartDate1 != null && StartDate1.length()>0)
			{
				sqlbuf.append( "and b.edate>='"+StartDate1+"' ");
			}

			if (EndDate1 != null && EndDate1.length()>0)
			{
				sqlbuf.append( "and b.edate<='"+EndDate1+"' ");
			}
			if(getInstantData.equals("Y"))
			{
				sqlbuf.append(" union all  "
						+ "            select pluNO,featureno,wunit  from DCP_stock_detail a "
						+ " where a.EID='"+req.geteId()+"' and a.doc_type in ('20','21') "
						);

				if (oShopId != null && oShopId.length()>0)
				{
					sqlbuf.append("and a.organizationno='"+oShopId+"' ");
				}

				if (StartDate1 != null && StartDate1.length()>0)
				{
					sqlbuf.append( "and bdate>='"+StartDate1+"' ");
				}

				if (EndDate1 != null && EndDate1.length()>0)
				{
					sqlbuf.append( "and bdate<='"+EndDate1+"' ");
				}
			}
			sqlbuf.append( "))) ");

		}
		else
		{
			sqlbuf.append("select num from ( select count(*) as num from "
					+ "(select bb.pluno,bb.featureno,bb.featurename from DCP_GOODS_shop aa  inner join DCP_BARCODE bb "
					+ "on aa.EID=bb.EID and aa.pluno=bb.pluno inner join DCP_GOODS_lang cc on aa.EID=cc.EID "
					+ "and bb.pluno=cc.pluno and cc.lang_Type= '"+langType+"' "
					+ "where aa.EID='"+req.geteId()+"' and aa.organizationno='"+oShopId+"'  and "
					+ "aa.fsal='Y' and aa.status='100' and bb.status='100' group by bb.pluno,bb.featureno,bb.featurename  ) A) ");
		}

		sql=sqlbuf.toString();
		return sql;		
	}

	protected String getParaSql(DCP_GoodsSaleRankQueryReq req) throws Exception
	{
		StringBuffer sqlbuf=new StringBuffer("");
		String sql="";

		sqlbuf.append("select  ITEMVALUE from Platform_BaseSetTemp " 
				+"where item='Get_Instant_Data'  and EID='"+req.geteId()+"' ");

		sql=sqlbuf.toString();

		return sql;
	}

	protected String getFeaturename(DCP_GoodsSaleRankQueryReq req,String feature_set,String Firstname,String Lastname) throws Exception
	{
		StringBuffer sqlbuf=new StringBuffer("");
		String sql="";

		sqlbuf.append("SELECT  listagg(feature_name,'_') within GROUP (order by item) as feature_name   "
				+ "FROM tb_fgroup_detail_lang "
				+ "WHERE EID='"+req.geteId()+"' "
				+ "AND feature_set='"+feature_set+"' "
				+ "AND LANG_TYPE='"+req.getLangType()+"' "
				+ "and status='100' "
				+ "and (featureno='"+Firstname +"' or featureno='"+Lastname+"')");

		sql=sqlbuf.toString();

		return sql;

	}


}
