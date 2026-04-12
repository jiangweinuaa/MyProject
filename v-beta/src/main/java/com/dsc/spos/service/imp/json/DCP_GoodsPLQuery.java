package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsPLQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsPLQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsPLQuery extends SPosBasicService<DCP_GoodsPLQueryReq,DCP_GoodsPLQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_GoodsPLQueryReq req) throws Exception {
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
	protected TypeToken<DCP_GoodsPLQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsPLQueryReq>(){};
	}

	@Override
	protected DCP_GoodsPLQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsPLQueryRes();
	}

	@Override
	protected DCP_GoodsPLQueryRes processJson(DCP_GoodsPLQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;
		//查询条件
		String eId = req.geteId();;
		String langType = req.getLangType();

		String oShopId = req.getoShopId();


		//查询资料
		DCP_GoodsPLQueryRes res = null;
		res = this.getResponse();
		//给分页字段赋值
		sql = this.getQuerySql_Count(req);	//查询总笔
		String[] conditionValues_Count = {eId,oShopId,eId,oShopId,eId,oShopId};//查询条件
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
		int totalRecords;	//总笔数
		int totalPages;		//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false)
		{ 
			Map<String, Object> oneData2 = getQData_Count.get(0);
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

		String[] condCountValues = {eId,oShopId,eId,oShopId,eId,oShopId,langType,langType,langType,eId,oShopId,eId,oShopId,eId,oShopId,startRn,endRn};//查询条件
		List<Map<String, Object>> getQDataDetail = this.doQueryData(sql,condCountValues);
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false) { // 有資料，取得詳細內容
			res.setDatas(new ArrayList<DCP_GoodsPLQueryRes.level1Elm>());
			for (Map<String, Object> oneData_B1 : getQDataDetail) {
				DCP_GoodsPLQueryRes.level1Elm oneLv_B1 = res.new level1Elm();
				oneLv_B1.setDatas(new ArrayList<DCP_GoodsPLQueryRes.level2Elm>());
				// 取得第一層資料庫搜尋結果
				String AbDate = oneData_B1.get("BDATE").toString();				

				boolean isExist_B1 = false;
				for (DCP_GoodsPLQueryRes.level1Elm sl_B1 : res.getDatas()) {
					String sl_bdate_B1 = sl_B1.getbDate().toString();
					if (sl_bdate_B1.equals(AbDate)){
						isExist_B1 = true;
					}
				}

				if (isExist_B1 == false ){
					for (Map<String, Object> oneData_B2 : getQDataDetail) {
						if (AbDate.equals(oneData_B2.get("BDATE").toString())){
							DCP_GoodsPLQueryRes.level2Elm oneLv_B2 =res.new level2Elm();
							oneLv_B2.setDatas(new ArrayList<DCP_GoodsPLQueryRes.level3Elm>());

							String category = oneData_B2.get("SNO").toString();
							String categoryName = oneData_B2.get("CATEGORYNAME").toString();

							boolean isExist_B2 = false;
							for (DCP_GoodsPLQueryRes.level2Elm sl_B2 : oneLv_B1.getDatas()) {
								String sl_category = sl_B2.getCategory().toString();
								if (sl_category.equals(category)){
									isExist_B2 = true;
								}
							}

							if (isExist_B2 ==  false){
								for (Map<String, Object> oneData_B3 : getQDataDetail) {
									if (AbDate.equals(oneData_B3.get("BDATE").toString()) && category.equals(oneData_B3.get("b.sno").toString())){
										DCP_GoodsPLQueryRes.level3Elm oneLv_B3 =res.new level3Elm();

										String ApluNO = oneData_B3.get("PLUNO").toString();
										String ApluName = oneData_B3.get("PLU_NAME").toString();
										String AfeatureNO = oneData_B3.get("FEATURENO").toString();
										String AfeatureName = oneData_B3.get("FEATURENAME").toString();
										String AwunitName = oneData_B3.get("UNITNAME").toString();
										String ApScrapQty = oneData_B3.get("SCRAPQTY").toString();
										String AlossOutQty = oneData_B3.get("LOSSQTY").toString();
										String AplQty = oneData_B3.get("PLQTY").toString();
										String Aprice = oneData_B3.get("PRICE1").toString();
										String Aamt = oneData_B3.get("AMT").toString();

										oneLv_B3.setPluNO(ApluNO);
										oneLv_B3.setPluName(ApluName);
										oneLv_B3.setFeatureNO(AfeatureNO);
										oneLv_B3.setFeatureName(AfeatureName);
										oneLv_B3.setWunitName(AwunitName);
										oneLv_B3.setpScrapQty(ApScrapQty);
										oneLv_B3.setLossOutQty(AlossOutQty);
										oneLv_B3.setPlQty(AplQty);
										oneLv_B3.setPrice(Aprice);
										oneLv_B3.setAmt(Aamt);

										oneLv_B2.getDatas().add(oneLv_B3);
										oneLv_B3 = null;

									}
								}	

								oneLv_B2.setCategory(category);
								oneLv_B2.setCategoryName(categoryName);
								oneLv_B1.getDatas().add(oneLv_B2);
								
								oneLv_B2 = null;
							}
						}
					}

					// 處理調整回傳值；
					oneLv_B1.setbDate(AbDate);

					res.getDatas().add(oneLv_B1);														
				}
			}
		}
		return res;
	}


	private String getQuerySql_Count(DCP_GoodsPLQueryReq req) throws Exception{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select count(*) as num,sum(qty) as qty,sum(amt) as amt from ("
				+ "select bdate,sum(qty) as qty,sum(amt) as amt from ("
				+ "select b.bdate,a.scrap_qty as qty,a.amt"
				+ " from DCP_pstockin_detail a inner join DCP_pstockin b on a.EID=b.EID  and a.pstockinno=b.pstockinno and a.SHOPID=b.SHOPID and a.ACCOUNT_DATE=b.ACCOUNT_DATE "
				+ " where a.EID=? and a.SHOPID=? "
				);

		String oShopId = req.getoShopId();
		String bDate = req.getbDate();
		String eDate = req.geteDate();
		String[] pluNO = req.getPluNO();

		if(oShopId == null) 
			oShopId = "";
		if(bDate == null) 
			bDate = "";
		if(eDate == null) 
			eDate = "";
		if(pluNO == null) 
			pluNO = new String[]{};

		String sPLUNO = getString(pluNO);

		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(" union all ");

		sqlbuf.append("select b.bdate,a.wqty as qty, a.amt"
				+ " from DCP_adjust_detail a inner join DCP_adjust b on a.EID=b.EID and a.adjustno=b.adjustno and a.SHOPID=b.SHOPID"
				+ " where a.EID=? and a.SHOPID=?"
				);
		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(" union all ");

		sqlbuf.append("select b.bdate,a.wqty as qty,a.amt"
				+ " from DCP_lstockout_detail a inner join DCP_lstockout b on a.EID=b.EID and a.lstockoutno=b.lstockoutno and a.SHOPID=b.SHOPID and a.BDATE=b.BDATE "
				+ " where a.EID=? and a.SHOPID=?"
				);
		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(") a group by bdate)");
		sql = sqlbuf.toString();
		return sql;
	}

	private String getQueryDatasSql(DCP_GoodsPLQueryReq req) throws Exception{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select b.sno,c.category_name as categoryName,b.wunit as unit,d.unit_name as unitName,a.bdate,a.EID,a.organizationno,a.pluno,f.plu_name,a.featureno,e.featurename, "
				+ " scrapQty,plQty,lossQty,j.price1,(scrapqty+plqty+lossqty)*j.price1 as amt from ("
				+ "select a.bdate,a.EID,a.organizationno,a.pluno,a.featureno,sum(scrapQty) as scrapQty,sum(plQty) as plQty,sum(lossQty) as lossQty,sum(amt) as amt from ("
				+ "select b.bdate,b.EID,b.organizationno,a.pluno,a.featureno, a.scrap_qty as scrapQty,0 as plQty,0.lossQty ,a.price*a.scrap_qty as amt"
				+ " from DCP_pstockin_detail a inner join DCP_pstockin b on a.EID=b.EID and a.pstockinno=b.pstockinno and a.SHOPID=b.SHOPID and a.ACCOUNT_DATE=b.ACCOUNT_DATE "
				+ " where a.EID=? and a.SHOPID=?"
				);
		String oShopId = req.getoShopId();
		String bDate = req.getbDate();
		String eDate = req.geteDate();
		String[] pluNO = req.getPluNO();
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String sysDate = df.format(cal.getTime());
		String eId = req.geteId();;
		String organizationNO = req.getOrganizationNO();

		if(oShopId == null) 
			oShopId = "";
		if(bDate == null) 
			bDate = "";
		if(eDate == null) 
			eDate = "";
		if(pluNO == null) 
			pluNO = new String[]{};

		String sPLUNO = getString(pluNO);

		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(" union all ");

		sqlbuf.append("select b.bdate,b.EID,b.organizationno,a.pluno,a.featureno,0 as scrapQty,a.wqty as plQty,0 as lossQty,a.amt"
				+ " from DCP_adjust_detail a inner join DCP_adjust b on a.EID=b.EID and a.adjustno=b.adjustno and a.SHOPID=b.SHOPID"
				+ " where a.EID=? and a.SHOPID=?"
				);
		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(" union all ");

		sqlbuf.append("select b.bdate,b.EID,b.organizationno,a.pluno,a.featureno, 0 as scrapQty,0 as plQty,a.wqty as lossQty,a.amt"
				+ " from DCP_lstockout_detail a inner join DCP_lstockout b on a.EID=b.EID and a.lstockoutno=b.lstockoutno and a.SHOPID=b.SHOPID and a.BDATE=b.BDATE "
				+ " where a.EID=? and a.SHOPID=?"
				);
		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(") a  group by a.bdate,a.EID,a.organizationno,a.pluno,a.featureno");
		sqlbuf.append(") a");
		sqlbuf.append(" inner join DCP_GOODS b on a.EID=b.EID and a.pluno=b.pluno");
		sqlbuf.append(" inner join DCP_GOODS_lang f on a.EID=f.EID and a.pluno=f.pluno and f.lang_type=?");
		sqlbuf.append(" left join DCP_CATEGORY_lang c on b.EID=c.EID and b.sno=c.category and c.lang_type=?");
		sqlbuf.append(" left join DCP_UNIT_lang d on b.EID=d.EID and b.wunit=d.unit and d.lang_type=?");
		sqlbuf.append(" left join DCP_BARCODE e on a.EID=e.EID and a.pluno=e.pluno  and a.featureno=e.featureno "	+ " left join ( select P2.* from DCP_PRICE p2 "
				+ "             inner join (select EID,organizationno,pluno,unit,max(item) A1  "
				+ "             from DCP_PRICE "
				+ "             where EID='" + eId + "' and organizationNO ='" + organizationNO + "' and effdate<='" + sysDate + "'  and (LEDate>='" + sysDate + "' or LEDate is null) AND status='100' "
				+ "             group by EID,organizationno,pluno ,unit ) P1 on p1.pluno=p2.pluno and p1.EID=p2.EID and p1.organizationNO =p2.organizationNO and p1.A1=p2.item "
				+ " ) j  on a.PLUNO=j.PLUNO AND b.wUNIT=j.UNIT AND  a.EID=j.EID and a.organizationNO =j.organizationNO ");

		sqlbuf.append(" where bdate in ( ");
		sqlbuf.append("select bdate from (");
		sqlbuf.append("select rn,bdate from (");
		sqlbuf.append("select rownum rn ,bdate from (");
		sqlbuf.append("select distinct bdate from (");
		sqlbuf.append("select b.bdate from DCP_pstockin_detail a inner join DCP_pstockin b on a.EID=b.EID  and a.pstockinno=b.pstockinno and a.SHOPID=b.SHOPID");
		sqlbuf.append(" where a.EID=? and a.SHOPID=?");

		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(" union all ");

		sqlbuf.append("select b.bdate"
				+ " from DCP_adjust_detail a inner join DCP_adjust b on a.EID=b.EID and a.adjustno=b.adjustno and a.SHOPID=b.SHOPID"
				+ " where a.EID=? and a.SHOPID=?"
				);
		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(" union all ");

		sqlbuf.append("select b.bdate"
				+ " from DCP_lstockout_detail a inner join DCP_lstockout b on a.EID=b.EID and a.lstockoutno=b.lstockoutno and a.SHOPID=b.SHOPID and a.BDATE=b.BDATE "
				+ " where a.EID=? and a.SHOPID=?"
				);
		if (bDate != null && bDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate>='"+bDate+"'");
		}
		if (eDate != null && eDate.length() > 0)
		{
			sqlbuf.append(" and b.bdate<='"+eDate+"'");
		}
		if (pluNO != null && pluNO.length > 0)
		{
			sqlbuf.append(" and a.pluno in ("+sPLUNO+")");
		}

		sqlbuf.append(") a order by a.bdate");
		sqlbuf.append(")) ");
		sqlbuf.append("where rn>? and rn<=?))");

		sql = sqlbuf.toString();
		return sql;
	}

	protected String getString(String[] str){
		String str2 = "";

		for (String s:str){
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0){
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
	protected String getQuerySql(DCP_GoodsPLQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
