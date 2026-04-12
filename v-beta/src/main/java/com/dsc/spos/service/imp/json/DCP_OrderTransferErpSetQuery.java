package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_OrderTransferErpSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderTransferErpSetQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderTransferErpSetQueryRes.levelElm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_OrderTransferErpSetQuery
 * 服务说明：订单上传ERP白名单查询
 * @author jinzma 
 * @since  2020-12-03
 */
public class DCP_OrderTransferErpSetQuery extends SPosBasicService<DCP_OrderTransferErpSetQueryReq,DCP_OrderTransferErpSetQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_OrderTransferErpSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderTransferErpSetQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderTransferErpSetQueryReq>(){};
	}

	@Override
	protected DCP_OrderTransferErpSetQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderTransferErpSetQueryRes();
	}

	@Override
	protected DCP_OrderTransferErpSetQueryRes processJson(DCP_OrderTransferErpSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_OrderTransferErpSetQueryRes res = this.getResponse();
		try {
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			levelElm datas = res.new levelElm();			
			datas.setOrgList(new ArrayList<DCP_OrderTransferErpSetQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false) 
			{
				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				for (Map<String, Object> oneData : getQData) 
				{
					DCP_OrderTransferErpSetQueryRes.level1Elm oneLv1 = res.new level1Elm(); 
					String shop = oneData.get("SHOP").toString();
					String shopName = oneData.get("SHOPNAME").toString();
					String createOpId = oneData.get("CREATEOPID").toString();
					String createOpName = oneData.get("CREATEOPNAME").toString();
					String createTime = oneData.get("CREATETIME").toString();

					oneLv1.setShop(shop);
					oneLv1.setShopName(shopName);
					oneLv1.setCreateOpId(createOpId);
					oneLv1.setCreateOpName(createOpName);
					oneLv1.setCreateTime(createTime);

					datas.getOrgList().add(oneLv1);
				}
			}
			else{
				totalRecords = 0;
				totalPages = 0;			
			}

			res.setDatas(datas);
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			return res;		

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_OrderTransferErpSetQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		
		String sql="";	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String langType=req.getLangType();
		String keyTxt= req.getRequest().getKeyTxt();

		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		sqlbuf.append(""
				+ " select * from ("
				+ " select count(*) over() num,row_number() over (order by a.shop) rn,"
				+ " a.shop,b.org_name as shopname,a.createopid,a.createopname,"
				+ " to_char(a.createtime,'yyyy-mm-dd hh24:mi:ss') as createtime from dcp_ordertransfererpset a"
				+ " inner join dcp_org_lang b on a.eid=b.eid and a.shop=b.organizationno and b.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"'"
				+ " ");
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.shop LIKE '%%"+keyTxt+"%%' or b.org_name LIKE '%%"+keyTxt+"%%') ");
		}
		sqlbuf.append(")");
		sqlbuf.append("where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		sql = sqlbuf.toString();
		return sql;

	}

}
