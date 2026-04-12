package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CounterWarehouseQueryReq;
import com.dsc.spos.json.cust.res.DCP_CounterWarehouseQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_CounterWarehouseQuery extends SPosBasicService<DCP_CounterWarehouseQueryReq,DCP_CounterWarehouseQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_CounterWarehouseQueryReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		String useType=req.getRequest().getUseType();
		if(Check.Null(useType) )
		{
			isFail = true;
			errMsg.append("库区用途分类不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_CounterWarehouseQueryReq> getRequestType()
	{
		return new TypeToken<DCP_CounterWarehouseQueryReq>() {};
	}

	@Override
	protected DCP_CounterWarehouseQueryRes getResponseType()
	{
		return new DCP_CounterWarehouseQueryRes();
	}

	@Override
	protected DCP_CounterWarehouseQueryRes processJson(DCP_CounterWarehouseQueryReq req) throws Exception
	{
		DCP_CounterWarehouseQueryRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	

		String sql = this.getQuerySql(req);				
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_CounterWarehouseQueryRes.level1Elm>());
		if(getQData!=null && getQData.isEmpty()==false)
		{			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			for (Map<String, Object> map : getQData) 
			{
				DCP_CounterWarehouseQueryRes.level1Elm lv1=res. new level1Elm();
				lv1.setCounterName(map.get("SHORTNAME").toString());
				lv1.setCounterNo(map.get("COUNTERNO").toString());
				lv1.setDeductionRate(map.get("DEDUCTIONRATE").toString());
				lv1.setShopId(map.get("SHOPID").toString());
				lv1.setShopName(map.get("ORG_NAME").toString());
				lv1.setUseType(map.get("USETYPE").toString());
				lv1.setWarehouse(map.get("WAREHOUSE").toString());
				lv1.setWarehouseName(map.get("WAREHOUSE_NAME").toString());

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
	protected String getQuerySql(DCP_CounterWarehouseQueryReq req) throws Exception
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String useType=req.getRequest().getUseType();
		String status=req.getRequest().getStatus();

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		

		String sql = "";
		StringBuffer sqlBuff = new StringBuffer();
		sqlBuff.append("select * from ("
				+ "select count(*) over() num, rownum rn,a.*,b.shortname,b.fullname,d.warehouse,d.deductionrate,d.usetype,e.warehouse_name,f.org_name "
				+ "from DCP_COUNTER a "
				+ "left join DCP_COUNTER_LANG b on a.eid=b.eid and a.shopid=b.shopid and a.counterno=b.counterno and b.lang_type='"+langtype+"' "
				+ "left join  "
				+ "( "
				+ "select distinct eid,counterno,shopid,warehouse from DCP_COUNTER_GOODS "
				+ ") c on a.eid=c.eid and a.shopid=c.shopid and a.counterno=c.counterno "
				+ "left join DCP_WAREHOUSE d on a.eid=d.eid and a.shopid=d.organizationno and c.warehouse=d.warehouse "
				+ "left join DCP_WAREHOUSE_LANG e on a.eid=e.eid and d.organizationno=e.organizationno and d.warehouse=e.warehouse and e.lang_type='"+langtype+"' "
				+ "left join DCP_ORG_LANG f on a.eid=f.eid and a.shopid=f.organizationno and f.lang_type='"+langtype+"' "
				+ "where a.eid='"+eId+"' ");

		if(useType != null && useType.length() >0)
		{
			sqlBuff.append("and d.usetype='"+useType+"' ");
		}				
		if(status != null && status.length() >0)
		{
			sqlBuff.append("and a.status='"+status+"' ");
		}

		sqlBuff.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlBuff.toString();
		return sql;

	}



}
