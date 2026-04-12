package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CounterQueryReq;
import com.dsc.spos.json.cust.res.DCP_CounterQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_CounterQuery extends SPosBasicService<DCP_CounterQueryReq,DCP_CounterQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_CounterQueryReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_CounterQueryReq> getRequestType()
	{
		return new TypeToken<DCP_CounterQueryReq>() {};
	}

	@Override
	protected DCP_CounterQueryRes getResponseType()
	{
		return new DCP_CounterQueryRes();
	}

	@Override
	protected DCP_CounterQueryRes processJson(DCP_CounterQueryReq req) throws Exception
	{
		DCP_CounterQueryRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	

		String sql = this.getQuerySql(req);				
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_CounterQueryRes.level1Elm>());
		if(getQData!=null && getQData.isEmpty()==false)
		{			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			Map<String, Boolean> condv=new HashMap<String, Boolean>();
			condv.put("COUNTERNO", true);
			condv.put("SHOPID", true);
			List<Map<String, Object>> getQDataHeader=MapDistinct.getMap(getQData, condv);
			for (Map<String, Object> map : getQDataHeader)
			{
				DCP_CounterQueryRes.level1Elm lv1=res.new level1Elm();

				lv1.setCounterNo(map.get("COUNTERNO").toString());
				lv1.setMemo(map.get("MEMO").toString());
				lv1.setOprMode(map.get("OPRMODE").toString());
				lv1.setShopId(map.get("SHOPID").toString());
				lv1.setShopName(map.get("ORG_NAME").toString());
				lv1.setSupplierId(map.get("SUPPLIERID").toString());
				lv1.setSupplierName(map.get("SUPPLIER_NAME").toString());

				//多语言
				Map<String, Object> condi=new HashMap<>();
				condi.put("COUNTERNO", map.get("COUNTERNO").toString());
				condi.put("SHOPID", map.get("SHOPID").toString());				
				List<Map<String, Object>> getQLang=MapDistinct.getWhereMap(getQData, condi, true);

				lv1.setCounterName_lang(new ArrayList<DCP_CounterQueryRes.counterNameLang>());
				String countername="";
				for (Map<String, Object> map2 : getQLang)
				{
					DCP_CounterQueryRes.counterNameLang lang=res. new counterNameLang();
					lang.setLangType(map2.get("LANG_TYPE").toString());
					lang.setName(map2.get("FULLNAME").toString());

					if (map2.get("LANG_TYPE").toString().equals(req.getLangType()))
					{
						countername=map2.get("FULLNAME").toString();
					}
					lv1.getCounterName_lang().add(lang);
					lang=null;
				}

				lv1.setCounterName(countername);		

				res.getDatas().add(lv1);
				lv1=null;
			}

		}
		
		res.setTotalPages(totalPages);
		res.setTotalRecords(totalRecords);
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_CounterQueryReq req) throws Exception
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String counterNo=req.getRequest().getCounterNo();
		String keyTxt=req.getRequest().getKeyTxt();
		String shopId= req.getRequest().getShopId();
		String status=req.getRequest().getStatus();


		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		

		String sql = "";
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("select a.*,b.lang_type,b.shortname,b.fullname,c.org_name,d.supplier_name,p1.NUM  from DCP_COUNTER a "
				+ "inner join "
				+ "( "
				+ "select * from ( "
				+ "select count(*) over() num, rownum rn,eid,counterno,shopid from "
				+ "( "
				+ "select distinct a.eid,a.counterno,a.shopid "
				+ "from DCP_COUNTER a "
				+ "left join DCP_COUNTER_LANG b on a.eid=b.eid and a.counterno=b.counterno and a.shopid=b.shopid "
				+ "where a.eid='"+eId+"' ");

		if (counterNo!=null && counterNo.length()>0)
		{
			sqlBuff.append("and a.counterno='"+counterNo+"' ");
		}

		if (shopId!=null && shopId.length()>0)
		{
			sqlBuff.append("and a.shopid='"+shopId+"' ");
		}

		if (status!=null && status.length()>0)
		{
			sqlBuff.append("and a.status='"+status+"' ");
		}

		if (keyTxt!=null && keyTxt.length()>0)
		{
			sqlBuff.append("and (a.counterno like '%"+keyTxt+"%' or b.shortname like '%"+keyTxt+"%' or b.fullname like '%"+keyTxt+"%') ");
		}

		sqlBuff.append(") "
				+ ") where rn>"+startRow+" and rn<="+(startRow+pageSize) 
				+ ") p1 on a.eid=p1.eid and a.counterno=p1.counterno and a.shopid=p1.shopid "
				+ "left join DCP_COUNTER_LANG b on a.eid=b.eid and a.counterno=b.counterno and a.shopid=b.shopid "
				+ "left join DCP_ORG_LANG c on a.eid=c.eid and a.shopid=c.organizationno and c.lang_type='"+langtype+"' "
				+ "left join DCP_SUPPLIER_LANG d on a.eid=d.eid and a.supplierid=d.supplier and d.lang_type='"+langtype+"' "
				+ "");

		sql = sqlBuff.toString();
		return sql;

	}

}
