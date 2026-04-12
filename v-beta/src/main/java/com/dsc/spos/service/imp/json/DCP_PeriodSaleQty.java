package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dsc.spos.utils.MyCommon;
import org.springframework.expression.ParseException;


import com.dsc.spos.json.cust.req.DCP_PeriodSaleQtyReq;
import com.dsc.spos.json.cust.req.DCP_PeriodSaleQtyReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PeriodSaleQtyRes;

import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;


public class DCP_PeriodSaleQty extends SPosBasicService<DCP_PeriodSaleQtyReq, DCP_PeriodSaleQtyRes> {

	@Override
	protected boolean isVerifyFail(DCP_PeriodSaleQtyReq req) throws Exception {
		// TODO Auto-generated method stub
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> datas = req.getRequest().getDatas();
	    if(datas.size() == 0){
	    	errMsg.append ( "商品列表不能为空");
	    	isFail = true;
	    }
	    if (isFail)
	    {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PeriodSaleQtyReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PeriodSaleQtyReq>(){};
	}

	@Override
	protected DCP_PeriodSaleQtyRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PeriodSaleQtyRes();
	}

	@Override
	protected DCP_PeriodSaleQtyRes processJson(DCP_PeriodSaleQtyReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_PeriodSaleQtyRes res = null;
		res = this.getResponse();
		String sql=getQuerySql(req);
		List<Map<String, Object>> getQData1 = this.doQueryData(sql,null);
		res.setDatas(new ArrayList<DCP_PeriodSaleQtyRes.level1Elm>());

		for (int i = 0; i < getQData1.size(); i++) {
			DCP_PeriodSaleQtyRes.level1Elm oneLv1 = res.new level1Elm();
			oneLv1.setPluNo(getQData1.get(i).get("PLUNO").toString());
			oneLv1.setPunit(getQData1.get(i).get("UNIT").toString());
			oneLv1.setLastPeriodSaleQty(getQData1.get(i).get("LASTPERIODSALEQTY").toString());
			oneLv1.setPeriodSaleQty(getQData1.get(i).get("PERIODSALEQTY").toString());
			res.getDatas().add(oneLv1);
			oneLv1=null;
		}
		
		return res;
	}
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
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
	protected String getQuerySql(DCP_PeriodSaleQtyReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		String[] gpluno = new String[req.getRequest().getDatas().size()] ;
		int i=0;
		for (DCP_PeriodSaleQtyReq.level1Elm item : req.getRequest().getDatas()) 
		{
			String spluno="";
			if(!item.getPluNo().isEmpty()&&item.getPluNo()!=null)
			{
				spluno=item.getPluNo();
			}
			gpluno[i]=spluno;
			i++;
		}
		String ggpluno=getString(gpluno);
		String sql = "SELECT A.PLUNO,A.UNIT,NVL(WK1,0) AS LASTPERIODSALEQTY,ROUND((NVL(WK1,0)+NVL(WK2,0)+NVL(WK3,0)+NVL(WK4,0))/4,2) AS PERIODSALEQTY   FROM DCP_PORDER_PREDICTSALE a "
						+ " WHERE a.eId = '"+eId+"'  AND  a.shopId = '"+shopId+"'"
						+ " and to_char(a.BDATE,'yyyymmdd') = '" + req.getRequest().getrDate()+"'"
						+ " and a.PLUNO IN ("+ggpluno+") ";
				return sql;
	}
}
