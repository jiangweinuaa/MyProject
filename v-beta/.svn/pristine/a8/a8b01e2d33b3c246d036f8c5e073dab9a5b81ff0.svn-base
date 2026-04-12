package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_CounterGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_CounterGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_CounterGoodsQuery extends SPosBasicService<DCP_CounterGoodsQueryReq,DCP_CounterGoodsQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_CounterGoodsQueryReq req) throws Exception
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
	protected TypeToken<DCP_CounterGoodsQueryReq> getRequestType()
	{
		return new TypeToken<DCP_CounterGoodsQueryReq>() {};
	}

	@Override
	protected DCP_CounterGoodsQueryRes getResponseType()
	{
		return new DCP_CounterGoodsQueryRes();
	}

	@Override
	protected DCP_CounterGoodsQueryRes processJson(DCP_CounterGoodsQueryReq req) throws Exception
	{
		DCP_CounterGoodsQueryRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	

		String sql = this.getQuerySql(req);				
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_CounterGoodsQueryRes.level1Elm>());
		if(getQData!=null && getQData.isEmpty()==false)
		{
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			for (Map<String, Object> map : getQData)
			{
				DCP_CounterGoodsQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setCounterName(map.get("FULLNAME").toString());
				lv1.setCounterNo(map.get("COUNTERNO").toString());
				lv1.setPluBarcode(map.get("PLUBARCODE").toString());
				lv1.setPluName(map.get("PLU_NAME").toString());
				lv1.setPluNo(map.get("PLUNO").toString());
				lv1.setShopId(map.get("SHOPID").toString());
				lv1.setShopName(map.get("ORG_NAME").toString());
				lv1.setStatus(map.get("STATUS").toString());
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
	protected String getQuerySql(DCP_CounterGoodsQueryReq req) throws Exception
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

		sqlBuff.append("select * from ( "
				+ "select count(*) over() num, rownum rn,a.*,b.shortname,b.fullname,c.org_name,d.warehouse_name,e.plu_name,f.status "
				+ "from DCP_COUNTER_GOODS a  "
				+ "left join DCP_COUNTER_LANG b on a.eid=b.eid and a.counterno=b.counterno and a.shopid=b.shopid and b.lang_type='"+langtype+"' "
				+ "left join DCP_ORG_LANG c on a.eid=c.eid and a.shopid=c.organizationno and c.lang_type='zh_CN' "
				+ "left join DCP_WAREHOUSE_LANG d on a.eid=d.eid and a.shopid=d.organizationno and a.warehouse=d.warehouse and d.lang_type='"+langtype+"' "
				+ "left join DCP_GOODS_LANG e on a.eid=e.eid and a.pluno=e.pluno and e.lang_type='"+langtype+"' "
				+ "left join DCP_GOODS_BARCODE f on a.eid=f.eid and a.pluno=f.pluno and a.plubarcode=f.plubarcode "
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
			sqlBuff.append("and f.status="+status+" ");
		}

		if (keyTxt!=null && keyTxt.length()>0)
		{
			sqlBuff.append("and (a.counterno like '%"+keyTxt+"%' or b.shortname like '%"+keyTxt+"%' or b.fullname like '%"+keyTxt+"%') ");
		}

		sqlBuff.append(") where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlBuff.toString();
		return sql;

	}



}
