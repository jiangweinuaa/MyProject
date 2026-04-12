package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_SquadQueryReq;
import com.dsc.spos.json.cust.res.DCP_SquadQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_SquadQuery extends SPosBasicService<DCP_SquadQueryReq, DCP_SquadQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SquadQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SquadQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SquadQueryReq>(){};
	}

	@Override
	protected DCP_SquadQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SquadQueryRes();
	}

	@Override
	protected DCP_SquadQueryRes processJson(DCP_SquadQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		DCP_SquadQueryRes res = null;
		res = this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};

			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);

			if(getDatas.size() > 0){
				res.setDatas(new ArrayList<DCP_SquadQueryRes.level1Elm>());

				String num = getDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

				for (Map<String, Object> oneData : getDatas) 
				{
					DCP_SquadQueryRes.level1Elm lev1 = new DCP_SquadQueryRes().new level1Elm();
					String shopId = oneData.get("SHOPID").toString();
					String shopName = oneData.get("SHOPNAME").toString();
					String opNO = oneData.get("OPNO").toString();
					String opName = oneData.get("OPNAME").toString();
					String squadNo = oneData.get("SQUADNO").toString();
					String machine = oneData.get("MACHINE").toString();
					String machineName = oneData.get("MACHINENAME").toString();
					String bDate = oneData.get("BDATE").toString();
					String workNo = oneData.get("WORKNO").toString();
					String workName = oneData.get("WORKNAME").toString();
					String tc_name = oneData.get("TC_NAME").toString();
					String endSquad = oneData.get("ENDSQUAD").toString();

					// 以下几个字段也许会用到，给查出来显示
					String tc_cash = oneData.get("TC_CASH").toString();
					String PDA_cash = oneData.get("PDA_CASH").toString();
					String saleCash = oneData.get("SALECASH").toString();
					String extraCash = oneData.get("EXTRACASH").toString();
					String pettyCash = oneData.get("PETTYCASH").toString();
					String largeCash = oneData.get("LARGECASH").toString();

					lev1.setShopId(shopId);
					lev1.setShopName(shopName);
					lev1.setOffOPNo(opNO);
					lev1.setOffOPName(opName);
					lev1.setSquadNo(squadNo);
					lev1.setMachine(machine);
					lev1.setMachineName(machineName);
					lev1.setbDate(bDate);
					lev1.setWorkNo(workNo);
					lev1.setWorkName(workName);
					lev1.setTc_name(tc_name);
					lev1.setEndSquad(endSquad);

					lev1.setTc_cash(tc_cash);
					lev1.setPDA_cash(PDA_cash);
					lev1.setSaleCash(saleCash);
					lev1.setExtraCash(extraCash);
					lev1.setPettyCash(pettyCash);
					lev1.setLargeCash(largeCash);

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
	protected String getQuerySql(DCP_SquadQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String keyTxt = "";
		String endSquad = "";
		String bDate = "";
		String eDate = "";

		if (req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			endSquad = req.getRequest().getEndSquad();
			bDate = req.getRequest().getbDate();
			eDate = req.getRequest().geteDate();
		}

		String langType = req.getLangType();

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append( " select * from ("
				+ " SELECT "
				+ " COUNT( * ) OVER( ) NUM ,dense_rank() over(ORDER BY a.squadNo, a.opNo, a.bdate, a.SHOPID , a.machine  ) rn ,"
				+ " a.squadno , a.opno , d.op_name AS opName , a.SHOPID , b.org_name AS shopName , a.machine , c.machinename ,"
				+ " a.bdate , a.endsquad , a.tc_name , a.workNo , a.workName ,"
				+ " a.cash , a.tc_cash , a.PDA_CASH, a.saleCash , a.extraCash , a.pettyCash , a.largeCash "
				+ " "
				+ " FROM DCP_SQUAD  a "
				+ " LEFT JOIN DCP_ORG_lang b ON a.EID = b.EID AND a.SHOPID = b.organizationNO  AND b.lang_type = '"+langType+"' "
				+ " LEFT JOIN PLATFORM_MACHINE c ON a.EID = c.EID AND a.SHOPID = c.SHOPID AND a.machine = c.machine "
				+ " LEFT JOIN platform_staffs_lang d ON a.EID = d.EID AND a.opno = d.opno  AND d.lang_type = '"+langType+"' "
				+ " WHERE a.EID = '"+eId+"' " );

		if (keyTxt != null && keyTxt.length()!=0) { 	
			sqlbuf.append( " AND ( a.OPNO LIKE '%%"+keyTxt+"%%' or a.squadNo like '%%"+keyTxt+"%%' "
					+ " or d.op_name like '%%"+keyTxt+"%%' or a.SHOPID like '%%"+keyTxt+"%%' "
					+ " or b.organizationNo like '%%"+keyTxt+"%%' or a.machine like '%%"+keyTxt+"%%' "
					+ " or c.machineName like '%%"+keyTxt+"%%' ) ");
		}
		if (endSquad != null && endSquad.length()!=0) { 	
			sqlbuf.append( " AND a.endsquad = '"+endSquad+"'  ");
		}

		if( bDate!= null && bDate.length()!=0 && eDate != null && eDate.length()!=0){
			sqlbuf.append( " AND a.bdate between '"+bDate+"' and '"+eDate+"' ");
		}

		sqlbuf.append( " GROUP BY a.squadno , a.opno , d.op_name  , a.SHOPID , b.org_name  , a.machine , c.machinename ,"
				+ " a.bdate , a.endsquad ,a.tc_name , a.workNo, a.workName ,"
				+ " a.cash , a.tc_cash , a.PDA_CASH, a.saleCash , a.extraCash , a.pettyCash , a.largeCash  " );
		sqlbuf.append( " )  "
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by bdate desc, squadNo , opNO , SHOPID , machine  ");
		sql = sqlbuf.toString();
		return sql;
	}

}
