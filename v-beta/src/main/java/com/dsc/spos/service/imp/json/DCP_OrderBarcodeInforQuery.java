package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrderBarcodeInforQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderBarcodeInforQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;


/**
 * 服務函數：BarcodeInforGet
 *    說明：商品查询
 * 服务说明：商品查询
 * @author chensong 
 * @since  2016-07-27
 */

public class DCP_OrderBarcodeInforQuery extends SPosBasicService<DCP_OrderBarcodeInforQueryReq, DCP_OrderBarcodeInforQueryRes> 
{
	Logger logger = LogManager.getLogger(DCP_OrderBarcodeInforQuery.class);

	@Override
	protected boolean isVerifyFail(DCP_OrderBarcodeInforQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；


		String[] pluBarcode = req.getPluBarcode();

		if (pluBarcode == null || pluBarcode.length == 0) 
		{ 
			errCt++;
			errMsg.append("条码不可为空值, ");
			isFail = true;
		}
		if(Check.Null(req.getoShopId()))
		{
			errCt++;
			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}



		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderBarcodeInforQueryReq> getRequestType() {
		return new TypeToken<DCP_OrderBarcodeInforQueryReq>(){};
	}

	@Override
	protected DCP_OrderBarcodeInforQueryRes getResponseType() {
		return new DCP_OrderBarcodeInforQueryRes();
	}

	@Override
	protected DCP_OrderBarcodeInforQueryRes processJson(DCP_OrderBarcodeInforQueryReq req) throws Exception 
	{
		String textsql="";
		if(req.getKeyTxt()!=null&&!req.getKeyTxt().isEmpty())
		{
			textsql+=" and (A.pluno like '%"+req.getKeyTxt()+"%' or A.pluname like '%"+req.getKeyTxt()+"%'  ) ";
		}

		DCP_OrderBarcodeInforQueryRes res =new DCP_OrderBarcodeInforQueryRes();
		String sql="select A.pluno,A.PLUNAME,A.CATEGORYNO,A.FILENAME,B.SPECNO,B.SPECNAME,C.CATEGORYNAME  from OC_GOODS A left join OC_GOODS_SPEC B on A.EID=B.EID and A.pluno=B.pluno "
				+ " left join OC_category C on A.EID=C.EID and  A.CATEGORYNO=C.CATEGORYNO   where A.status='100' and B.status='100' and B.ISONSHELF='Y'  " +textsql;
		List<Map<String, Object>> listsql=this.doQueryData(sql, null);
		if(listsql!=null&&!listsql.isEmpty())
		{
			res.setDatas(new ArrayList<DCP_OrderBarcodeInforQueryRes.level1Elm>());
			for (Map<String, Object> map : listsql) 
			{
				DCP_OrderBarcodeInforQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setPluBarcode(map.get("SPECNO").toString());
				lv1.setPluNO(map.get("PLUNO").toString());
				lv1.setPluName(map.get("PLUNAME").toString());
				lv1.setCategory(map.get("CATEGORYNO").toString());
				lv1.setCategoryName(map.get("CATEGORYNAME").toString());
				lv1.setSpec(map.get("SPECNO").toString());
				lv1.setSpecNanme(map.get("SPECNAME").toString());
				lv1.setFileName(map.get("FILENAME").toString());
				res.getDatas().add(lv1);
				lv1 = null;
			}

		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		//調整查出來的資料
	}

	@Override
	protected String getQuerySql(DCP_OrderBarcodeInforQueryReq req) throws Exception {


		return null;
	}

	protected String getQuerySql4() throws Exception {
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append(""   
				+ "SELECT count(*) "
				+ " FROM DCP_BARCODE a INNER JOIN DCP_GOODS_SHOP b ON a.PLUNO=b.PLUNO AND a.EID=b.EID "
				+ " WHERE a.status='100' AND b.status='100' and a.PLUBARCODE=? AND  a.EID=? AND b.ORGANIZATIONNO=? "
				);

		sql = sqlbuf.toString();

		return sql;
	}

	protected String getQuerySql_check() throws Exception {
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append(""   
				+ " select count(*) as c from DCP_stocktask_list where pluno=( "
				+ " select pluno from DCP_BARCODE where plubarcode=? and EID=?) "
				+ " and EID=? and organizationno=? and stocktaskno=? "
				);

		sql = sqlbuf.toString();

		return sql;
	}	



	protected String getString(String[] str){
		String str2 = "";

		for (String s:str){
			str2 = str2 + s + ",";
		}
		if (str2.length()>0){
			str2=str2.substring(0,str2.length()-1);
		}

		//System.out.println(str2);

		return str2;
	}



	/**
	 * 返回SQL语句
	 * @param multiplubarcode '1611131195814','1611131195812'
	 * @return
	 */
	protected String getMultiBarcode(String multiplubarcode)
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		//异常判断，in条件不能为空
		if(multiplubarcode==null || multiplubarcode.length()<2)
			multiplubarcode="''";

		sqlbuf.append(""   
				+ "SELECT a.PLUBARCODE "
				+ " FROM DCP_BARCODE a INNER JOIN DCP_GOODS_SHOP b ON a.PLUNO=b.PLUNO AND a.EID=b.EID "
				+ " WHERE a.status='100' AND b.status='100' "
				+ " and a.PLUBARCODE in (" +multiplubarcode+") "
				+ " AND  a.EID=? "
				+ " AND b.ORGANIZATIONNO=? "
				);

		sql = sqlbuf.toString();

		return sql;

	}



}
