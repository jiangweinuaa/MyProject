package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SaleSumQueryReq;
import com.dsc.spos.json.cust.res.DCP_SaleSumQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_SaleSumQuery extends SPosBasicService<DCP_SaleSumQueryReq,DCP_SaleSumQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_SaleSumQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if (req.getoShopId() == null) 
		{
			errCt++;
			errMsg.append("门店不能为Null, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_SaleSumQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SaleSumQueryReq>(){};
	}

	@Override
	protected DCP_SaleSumQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SaleSumQueryRes();
	}

	@Override
	protected DCP_SaleSumQueryRes processJson(DCP_SaleSumQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;
		//查询条件
		String eId = req.geteId();;
		String langType = req.getLangType();

		String oShopId = req.getoShopId();
		String qty = null;
		String amt = null;

		//查询资料
		DCP_SaleSumQueryRes res = null;
		res = this.getResponse();

		//给分页字段赋值
		sql = this.getQuerySql_Count(req);	//查询总笔
		String[] conditionValues_Count = {eId,oShopId,eId,oShopId};//查询条件
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
		int totalRecords;	//总笔数
		int totalPages;		//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false)
		{ 
			Map<String, Object> oneData2 = getQData_Count.get(0);
			String num = oneData2.get("NUM").toString();
			qty = oneData2.get("QTY").toString();
			amt = oneData2.get("AMT").toString();

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

		sql=this.getQueryDatasSql(req);

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//计算起始位置
		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

		String startRn = String.valueOf(startRow);
		String endRn = String.valueOf(startRow+pageSize);

		String[] condCountValues={eId,oShopId,eId,oShopId,langType,langType,langType,langType,eId,oShopId,eId,oShopId,startRn,endRn};//查询条件
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,condCountValues);
		res.setDatas(new ArrayList<DCP_SaleSumQueryRes.level1Elm>());
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false) { // 有資料，取得詳細內容
			//res.setDatas(new ArrayList<SaleSumGetRes.level1Elm>());

			for (Map<String, Object> oneData_B1 : getQDataDetail) {
				DCP_SaleSumQueryRes.level1Elm oneLv_B1 = res.new level1Elm();
				oneLv_B1.setDatas(new ArrayList<DCP_SaleSumQueryRes.level2Elm>());
				// 取得第一層資料庫搜尋結果
				String shopId = oneData_B1.get("SHOPID").toString();
				String shopName = oneData_B1.get("SHOPNAME").toString();

				boolean isExist_B1 = false;
				for (DCP_SaleSumQueryRes.level1Elm sl_B1 : res.getDatas()) {
					String sl_shop_B1 = sl_B1.getShopId().toString();
					if (sl_shop_B1.equals(shopId)){
						isExist_B1 = true;
					}
				}

				if (isExist_B1 == false ){
					for (Map<String, Object> oneData_B2 : getQDataDetail) {
						if (shopId.equals(oneData_B2.get("SHOPID").toString())){
							DCP_SaleSumQueryRes.level2Elm oneLv_B2 = res.new level2Elm();
							oneLv_B2.setDatas(new ArrayList<DCP_SaleSumQueryRes.level3Elm>());

							String category = oneData_B2.get("SNO").toString();
							String categoryName = oneData_B2.get("CATEGORYNAME").toString();

							//oneLv_B2.setCategory(category);
							//oneLv_B2.setCategoryName(categoryName);

							boolean isExist_B2 = false;
							for (DCP_SaleSumQueryRes.level2Elm sl_B2 : oneLv_B1.getDatas()) {
								String sl_category = sl_B2.getCategory().toString();
								if (sl_category.equals(category)){
									isExist_B2 = true;
								}
							}

							if (isExist_B2 ==  false){
								for (Map<String, Object> oneData_B3 : getQDataDetail) {
									if (shopId.equals(oneData_B3.get("SHOPID").toString()) && category.equals(oneData_B3.get("SNO").toString())){
										DCP_SaleSumQueryRes.level3Elm oneLv_B3 = res.new level3Elm();

										String ApluNO = oneData_B3.get("PLUNO").toString();
										String ApluName = oneData_B3.get("PLU_NAME").toString();
										String AfeatureNO = oneData_B3.get("FEATURENO").toString();
										String AfeatureName = oneData_B3.get("FEATURENAME").toString();
										String AwunitName = oneData_B3.get("WUNITNAME").toString();
										String Aqty = oneData_B3.get("QTY").toString();
										String Aamt = oneData_B3.get("AMT").toString();

										float Fradio = 0;
										if (amt.equals("0") || amt==null) {
											Fradio = 0;
										}
										else
										{
											Fradio = Float.parseFloat(Aamt)/Float.parseFloat(amt);
										}
										//String Aradio = Fradio.toString();

										oneLv_B3.setPluNO(ApluNO);
										oneLv_B3.setPluName(ApluName);
										oneLv_B3.setFeatureNO(AfeatureNO);
										oneLv_B3.setFeatureName(AfeatureName);
										oneLv_B3.setWunitName(AwunitName);
										oneLv_B3.setQty(Aqty);
										oneLv_B3.setAmt(Aamt);
										oneLv_B3.setRatio(Fradio);

										oneLv_B2.getDatas().add(oneLv_B3);											
									}
								}	

								oneLv_B2.setCategory(category);
								oneLv_B2.setCategoryName(categoryName);
								oneLv_B1.getDatas().add(oneLv_B2);
							}
						}
					}

					// 處理調整回傳值；
					oneLv_B1.setShopId(shopId);
					oneLv_B1.setShopName(shopName);

					res.getDatas().add(oneLv_B1);
				}
			}
		}
		return res;
	}

	private String getQuerySql_Count(DCP_SaleSumQueryReq req) throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String oShopId = req.getoShopId();
		String bDate = req.getbDate();
		String eDate = req.geteDate();
		String[] pluNO = req.getPluNO();
		String keyTxt = req.getKeyTxt();

		if(oShopId == null) 
			oShopId = "";
		if(bDate == null) 
			bDate = "";
		if(eDate == null) 
			eDate = "";
		if(pluNO == null) 
			pluNO = new String[]{};

		String sPLUNO = getString(pluNO);

		//获取Get_Instant_Data参数
		String getInstantData = PosPub.getPARA_SMS(dao, req.geteId(), "", "Get_Instant_Data");
		if (Check.Null(getInstantData))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置Get_Instant_Data参数");
		}

		sqlbuf.append("select count(*) as num,sum(qty) as qty,sum(amt) as amt from ("
				+ "select SHOPID,SUM(qty) as qty,sum(amt) as amt from (" );

		sqlbuf.append("select b.EID,a.SHOPID,(case when doc_type='0' then qty else -qty end) as qty,"
				+ " (case when (doc_type='0' and e.ispackage='N') then amt when (doc_type='1' and e.ispackage='N') then -amt else 0 end) as amt "
				+ " from DCP_settlement_detail a"
				+ " inner join DCP_settlement b  on a.EID=b.EID  and a.SHOPID=b.SHOPID"
				+ " and a.settlementno=b.settlementno and a.type=b.type"
				+ " inner join DCP_GOODS_lang c on a.EID=c.EID and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'"
				+ " inner join DCP_GOODS e on a.EID=e.EID and a.pluno=e.pluno "
				+ " where a.EID=? and  a.SHOPID=? and a.type='1'"
				);
		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.edate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.edate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}	
		if(keyTxt!=null && keyTxt.length()>0)
		{
			sqlbuf.append("and ((lower(a.pluNO) like lower('%%"+keyTxt+"%%')) or (lower(c.PLU_NAME) like lower('%%"+keyTxt+"%%')) or (lower(c.spec) like lower('%%"+keyTxt+"%%'))) ");
		}
		if(getInstantData.equals("Y"))
		{
			sqlbuf.append(" union all  "
					+ "select a.EID,a.organizationno as SHOPID,(case when doc_type='20' then pqty else -pqty end) as qty,"
					+ " (case when (doc_type='20' and e.ispackage='N') then amt when (doc_type='21' and e.ispackage='N') then -amt else 0 end) as amt "
					+ " from DCP_stock_detail a"
					+ " inner join DCP_GOODS_lang c on a.EID=c.EID and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'"
					+ " inner join DCP_GOODS e on a.EID=e.EID and a.pluno=e.pluno "
					+ " where a.EID=? and a.organizationno=? "
					+ " and a.doc_type in ('20','21')"
					);

			if (bDate != null && bDate.length() > 0)
			{
				sqlbuf.append(" and a.account_date>='"+bDate+"'");
			}
			if (eDate != null && eDate.length() > 0)
			{
				sqlbuf.append(" and a.account_date<='"+eDate+"'");
			}
			if (pluNO != null && pluNO.length > 0)
			{
				sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
			}
			if(keyTxt!=null && keyTxt.length()>0)
			{
				sqlbuf.append("and ((lower(a.pluNO) like lower('%%"+keyTxt+"%%')) or (lower(c.PLU_NAME) like lower('%%"+keyTxt+"%%')) or (lower(c.spec) like lower('%%"+keyTxt+"%%'))) ");
			}
		}

		sqlbuf.append(") a group by SHOPID) a group by SHOPID");
		sql = sqlbuf.toString();
		return sql;		
	}

	private String getQueryDatasSql(DCP_SaleSumQueryReq req) throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String oShopId = req.getoShopId();
		String bDate = req.getbDate();
		String eDate = req.geteDate();
		String[] pluNO = req.getPluNO();
		String keyTxt = req.getKeyTxt();

		if(oShopId==null) 
			oShopId="";
		if(bDate==null) 
			bDate="";
		if(eDate==null) 
			eDate="";
		if(pluNO==null) 
			pluNO=new String[]{};

		String sPLUNO = getString(pluNO);


		//获取Get_Instant_Data参数
		String getInstantData = PosPub.getPARA_SMS(dao, req.geteId(), "", "Get_Instant_Data");
		if (Check.Null(getInstantData))
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置Get_Instant_Data参数");
		}

		//查询结果根据pluno，featureno去掉重复
		sqlbuf.append("select distinct pluno,featureno,aaa.* from (");

		sqlbuf.append("select a.SHOPID,f.shopName,a.pluno,l.plu_name,a.featureno,a.qty,a.amt,b.sno,c.category_name as categoryName,a.unit,d.unit_name as wunitName,e.featurename from "
				+ "("
				+ "select EID,SHOPID,pluno,featureNO,unit,SUM(qty) as qty,sum(amt) as amt from "
				+ "(");

		sqlbuf.append("select b.EID,b.SHOPID ,a.pluNO,nvl(featureNO,cast(' ' as nvarchar2(40))) as featureno,a.unit unit "
				+ ",(case when doc_type='0' then qty when doc_type='1' then -qty else 0 end) as qty"
				+ ",(case when (doc_type='0' and e.ispackage='N') then amt when (doc_type='1' and e.ispackage='N') then -amt else 0 end) as amt "
				+ " from DCP_settlement_detail a"
				+ " inner join DCP_settlement b  on a.EID=b.EID  and a.SHOPID=b.SHOPID and a.settlementno=b.settlementno and a.type=b.type "
				+ " inner join DCP_GOODS_lang c on a.EID=c.EID and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'"
				+ " inner join DCP_GOODS e on a.EID=e.EID and a.pluno=e.pluno "
				+ " where a.EID=? and a.SHOPID=?"
				+ " and a.type='1'"
				);
		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.edate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.edate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}	

		if(keyTxt!=null && keyTxt.length()>0)
		{
			sqlbuf.append("and ((lower(a.pluNO) like lower('%%"+keyTxt+"%%')) or (lower(c.PLU_NAME) like lower('%%"+keyTxt+"%%')) or (lower(c.spec) like lower('%%"+keyTxt+"%%'))) ");
		}

		if(getInstantData.equals("Y"))
		{
			sqlbuf.append(" union all  ");
			sqlbuf.append("select a.EID,a.organizationno as SHOPID,a.pluNO,nvl(featureNO,cast(' ' as nvarchar2(40))) as featureno,a.punit unit "
					+ ",(case when doc_type='20' then pqty else -pqty end) as qty "
					+ ",(case when (doc_type='20' and e.ispackage='N') then amt when (doc_type='21' and e.ispackage='N') then -amt else 0 end) as amt "
					+ "from DCP_stock_detail a"
					+ " inner join DCP_GOODS_lang c on a.EID=c.EID and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'"
					+ " inner join DCP_GOODS e on a.EID=e.EID and a.pluno=e.pluno "
					+ " where a.EID=? and a.organizationno=? "
					+ " and a.doc_type in ('20','21')"
					);

			if (bDate != null && bDate.length() > 0)
			{
				sqlbuf.append(" and a.account_date>='"+bDate+"'");
			}
			if (eDate != null && eDate.length() > 0)
			{
				sqlbuf.append(" and a.account_date<='"+eDate+"'");
			}
			if (pluNO != null && pluNO.length > 0)
			{
				sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
			}
			if(keyTxt!=null && keyTxt.length()>0)
			{
				sqlbuf.append("and ((lower(a.pluNO) like lower('%%"+keyTxt+"%%')) or (lower(c.PLU_NAME) like lower('%%"+keyTxt+"%%')) or (lower(c.spec) like lower('%%"+keyTxt+"%%'))) ");
			}
		}

		sqlbuf.append(" ) a group by pluno,featureno,unit,EID,SHOPID"
				+ " ) a inner join DCP_GOODS b on a.EID=b.EID and a.pluno=b.pluno "
				+ " left join DCP_CATEGORY_lang c  on b.EID=c.EID and b.sno=c.category and c.lang_type=?"
				+ " left join DCP_UNIT_lang d on b.EID=d.EID and a.unit=d.unit and d.lang_type=?"
				+ " left join DCP_BARCODE e on a.EID=e.EID and a.pluno=e.pluno  and a.featureno=e.featureno"
				+ " left join "
				+ "("
				+ "SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,B.LANG_TYPE FROM DCP_ORG A "
				+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+req.getLangType()+"' AND B.status='100' "
				+ "WHERE A.EID='"+req.geteId()+"' AND A.ORG_FORM='2' AND A.status='100' "
				+ ") f on a.EID=f.EID and a.SHOPID=f.SHOPID and f.lang_type=?"
				+ " left join DCP_GOODS_lang l on a.EID=l.EID and l.lang_type=? and a.pluno=l.pluno"
				+ " where a.SHOPID in (select SHOPID from ("
				+ "select rn,SHOPID from ("
				+ "select rownum rn ,SHOPID from ("
				+ "select distinct SHOPID from (" );

		sqlbuf.append("select a.EID, a.SHOPID from DCP_settlement_detail a inner join DCP_settlement b on a.EID=b.EID and a.SHOPID=b.SHOPID"
				+ " and a.settlementno=b.settlementno and a.type=b.type"
				+ " inner join DCP_GOODS_lang c on a.EID=c.EID and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'"
				+ " where a.EID=? and a.SHOPID=?"
				+ " and a.type='1'"
				);

		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.edate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.edate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}
		if(keyTxt!=null && keyTxt.length()>0)
		{
			sqlbuf.append("and ((lower(a.pluNO) like lower('%%"+keyTxt+"%%')) or (lower(c.PLU_NAME) like lower('%%"+keyTxt+"%%')) or (lower(c.spec) like lower('%%"+keyTxt+"%%'))) ");
		}

		if(getInstantData.equals("Y"))
		{
			sqlbuf.append(" union all  ");
			sqlbuf.append(" select a.EID,a.organizationno as SHOPID from DCP_stock_detail a"
					+ " inner join DCP_GOODS_lang c on a.EID=c.EID and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"'"
					+ " where a.EID=? and a.organizationno=?"
					+ " and a.doc_type in ('20','21')"
					);
			if (bDate != null && bDate.length() > 0)
			{
				sqlbuf.append(" and a.account_date>='"+bDate+"'");
			}
			if (eDate != null && eDate.length() > 0)
			{
				sqlbuf.append(" and a.account_date<='"+eDate+"'");
			}
			if (pluNO != null && pluNO.length > 0)
			{
				sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
			}

			if(keyTxt!=null && keyTxt.length()>0)
			{
				sqlbuf.append("and ((lower(a.pluNO) like lower('%%"+keyTxt+"%%')) or (lower(c.PLU_NAME) like lower('%%"+keyTxt+"%%')) or (lower(c.spec) like lower('%%"+keyTxt+"%%'))) ");
			}
		}

		sqlbuf.append(") a order by SHOPID");
		sqlbuf.append(")) ");
		sqlbuf.append("where rn>? and rn<=?)) order by b.sno,a.pluno");
		//查询结果根据pluno，featureno去掉重复
		sqlbuf.append(") AAA ");

		sql = sqlbuf.toString();
		return sql;
	}

	protected String getString(String[] str)
	{
		String str2 = "";

		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		//System.out.println(str2);

		return str2;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_SaleSumQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
