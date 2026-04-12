package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_POAdiseQueryReq;
import com.dsc.spos.json.cust.res.DCP_POAdiseQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_POAdiseQuery extends SPosBasicService<DCP_POAdiseQueryReq, DCP_POAdiseQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_POAdiseQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤
		
		//必传值不为空
		String[] gDate = req.getgDate();
		if(gDate == null){
			errCt++;
			errMsg.append("参考日期不可为空值, ");
			isFail = true;
		}
		
		String rDate = req.getrDate();
		if(Check.Null(rDate)){
			errCt++;
			errMsg.append("需求日期不可为空值, ");
			isFail = true;
		}
		
		String[] pluNO = req.getPluNO();
		if(pluNO == null){
			errCt++;
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_POAdiseQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_POAdiseQueryReq>(){};
	}

	@Override
	protected DCP_POAdiseQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_POAdiseQueryRes();
	}

	@Override
	protected DCP_POAdiseQueryRes processJson(DCP_POAdiseQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		//查询条件
		String shopId=req.getShopId();
		String organizationNO = req.getOrganizationNO();
		String eId = req.geteId();;

		DCP_POAdiseQueryRes res = null;
		res = this.getResponse();
		
		sql=this.getQuerySqlRefWPQty(req);

		String[] conditionValues1 = {organizationNO,eId ,eId, organizationNO,eId, organizationNO}; //查詢條件

		List<Map<String, Object>> getQuerySqlRefWPQty=this.doQueryData(sql, conditionValues1);
				
		if (getQuerySqlRefWPQty != null && getQuerySqlRefWPQty.isEmpty() == false) { // 有資料，取得詳細內容
			res.setDatas(new ArrayList<DCP_POAdiseQueryRes.level1Elm>());
			for (Map<String, Object> oneData : getQuerySqlRefWPQty) {
				DCP_POAdiseQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setDatas(new ArrayList<DCP_POAdiseQueryRes.level2Elm>());
				// 取出第一层
				String pluNO = oneData.get("PLUNO").toString();
				//String featureNO = oneData.get("FEATURENO").toString();
				String refWQty = oneData.get("REFWQTY").toString();
				String refPQty = oneData.get("REFPQTY").toString();
				float f_refWQty;
				float f_refPQty;
				if (refWQty == null || refWQty.length()==0 ){
					f_refWQty=0;
				} else 	
					f_refWQty = Float.parseFloat(refWQty);
				
				if (refPQty == null || refPQty.length()==0 ){
					f_refPQty=0;
				} else 	
					f_refPQty = Float.parseFloat(refPQty);
				
				//设置响应
				oneLv1.setPluNO(pluNO);
				//oneLv1.setFeatureNO(featureNO);
				oneLv1.setRefWQty(f_refWQty);
				oneLv1.setRefPQty(f_refPQty);

				sql = this.getQuerySqlRefSQty(req);
				String[] condRefSQty = {eId, organizationNO,pluNO}; //查詢條件
				List<Map<String, Object>> getRefSQty = this.doQueryData(sql, condRefSQty);
				if(getRefSQty != null && getRefSQty.isEmpty() == false){
					for (Map<String, Object> oneData1 : getRefSQty) 
					{
						DCP_POAdiseQueryRes.level2Elm oneLv2 = res.new level2Elm();
		
						String refDate = oneData1.get("REFDATE").toString();
						String refSQty = oneData1.get("REFSQTY").toString();
					
						float f_refSQty;
						if (refSQty == null || refSQty.length()==0 ){
							f_refSQty=0;
						} else 
							f_refSQty = Float.parseFloat(refSQty);
					
						oneLv2.setRefDate(refDate);
						oneLv2.setRefSQty(f_refSQty);
						oneLv1.getDatas().add(oneLv2);
						oneLv2=null;
					}
				}
				res.getDatas().add(oneLv1);
				oneLv1=null;
			}
		}else{    	
			res.setDatas(new ArrayList<DCP_POAdiseQueryRes.level1Elm>());
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_POAdiseQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	//取参考销量
	protected String getQuerySqlRefSQty(DCP_POAdiseQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String[] edate = req.getgDate();
		String Aedate = getString(edate);
		String[] featureno = req.getPluNO();
		//String ApluNO = getString(featureno);

		sqlbuf.append("select bdate as refDate,pluno,sum(wqty) as refSQty from ("
					+ "select EID,organizationno,bdate,pluno, wqty from DCP_stock_detail_static"
					+ " where EID=? and organizationno=? and pluno=? "
			);
		
		if(Aedate != null && Aedate.length() > 0){
			sqlbuf.append(" and bdate in ("+Aedate+")");
		}
		//if(ApluNO != null && ApluNO.length() > 0){
		//	sqlbuf.append(" and pluno||featureno in ("+ApluNO+")");
		//}
		sqlbuf.append(") group by bdate,pluno");
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
	
	//取库存量和已要货量
	protected String getQuerySqlRefWPQty(DCP_POAdiseQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		//String[] featureno = req.getPluNOFeatureNO();
		//String ApluNO = getString(featureno);
		String[] gpluno = req.getPluNO();
		String ggpluno = getString(gpluno);
		String rdate = req.getrDate();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String _DATE = df.format(cal.getTime());
		
		//库存
		sqlbuf.append("select pluno,sum(wqty) as refWQty,sum(pqty) as refPQty from ("
				+ "select a.pluno,nvl(qty,0) as wqty,0 as pqty from DCP_GOODS a left join  "
				+ "  DCP_stock_day b on a.EID=b.EID and a.pluno=b.pluno and b.organizationno=?  ");
			
		/*if(ApluNO != null && ApluNO.length() > 0){
					sqlbuf.append(" and b.pluno||b.featureno in ("+ApluNO+")");
				}*/		
		sqlbuf.append(" where a.EID=? and a.pluno in  ("+ggpluno+")");
	
		//取未日结库存
		sqlbuf.append(" union all ");
		
		sqlbuf.append("select pluno,(case when stock_type='0' then wqty else -wqty end) as wqty,0 as pqty from DCP_stock_detail"
				+ " where EID=? and organizationno=?"
		);
		/*if(ApluNO != null && ApluNO.length() > 0){
			sqlbuf.append(" and pluno||featureno in ("+ApluNO+")");
		}*/
		sqlbuf.append(" and pluno in ("+ggpluno+")");

		//取已要货量
		sqlbuf.append(" union all ");
		
		sqlbuf.append("select a.pluno,0 as wqty,nvl(wqty,0) as pqty from DCP_porder_detail a"
				+ " inner join DCP_porder b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.porderno=b.porderno and a.BDATE=b.BDATE "
				+ " where a.EID=? and a.organizationno=?"
		);
		/*if(ApluNO != null && ApluNO.length() > 0){
			sqlbuf.append(" and a.pluno||a.featureno in ("+ApluNO+")");
		}*/
		sqlbuf.append("  and a.pluno in  ("+ggpluno+")");
		sqlbuf.append(" and b.status in ('0','2') and b.is_add='N' and b.bdate='"+_DATE+"' ");
		
		if(rdate != null && rdate.length() > 0){
			sqlbuf.append(" and b.rdate='"+rdate+"'");
		}
		
		sqlbuf.append(") group by pluno");
		sql = sqlbuf.toString();
	
		return sql;
	}
}
