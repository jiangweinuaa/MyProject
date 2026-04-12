package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_PinPeiGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_PinPeiGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_PinPeiGoodsQuery
 * 服务说明：拼胚商品查询
 * @author jinzma
 * @since  2020-07-13
 */
public class DCP_PinPeiGoodsQuery extends SPosBasicService<DCP_PinPeiGoodsQueryReq,DCP_PinPeiGoodsQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_PinPeiGoodsQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_PinPeiGoodsQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_PinPeiGoodsQueryReq>(){};
	}

	@Override
	protected DCP_PinPeiGoodsQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_PinPeiGoodsQueryRes();
	}

	@Override
	protected DCP_PinPeiGoodsQueryRes processJson(DCP_PinPeiGoodsQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		DCP_PinPeiGoodsQueryRes res = this.getResponse();
		try
		{
			String sql=this.getQuerySql(req);
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			res.setDatas(new ArrayList<DCP_PinPeiGoodsQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false)
			{
				// 拼接返回图片路径  by jinzma 20210705
				String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
				String httpStr=isHttps.equals("1")?"https://":"http://";
				String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
				if (domainName.endsWith("/")) {
					domainName = httpStr + domainName + "resource/image/";
				}else{
					domainName = httpStr + domainName + "/resource/image/";
				}

				//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				for (Map<String, Object> oneData : getQData) {
					DCP_PinPeiGoodsQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String pluNo=oneData.get("PLUNO").toString();
					String pluName=oneData.get("PLU_NAME").toString();
					String listImage=oneData.get("LISTIMAGE").toString();
					if (!Check.Null(listImage)){
						listImage = domainName+listImage;
					}
					String status=oneData.get("STATUS").toString();
					String createOpId=oneData.get("CREATEOPID").toString();
					String createOpName=oneData.get("CREATEOPNAME").toString();
					String createTime=oneData.get("CREATETIME").toString();
					String lastModiOpId=oneData.get("LASTMODIOPID").toString();
					String lastModiOpName=oneData.get("LASTMODIOPNAME").toString();
					String lastModiTime=oneData.get("LASTMODITIME").toString();

					oneLv1.setPluNo(pluNo);
					oneLv1.setPluName(pluName);
					oneLv1.setListImage(listImage);
					oneLv1.setStatus(status);
					oneLv1.setCreateOpId(createOpId);
					oneLv1.setCreateOpName(createOpName);
					oneLv1.setCreateTime(createTime);
					oneLv1.setLastModiOpId(lastModiOpId);
					oneLv1.setLastModiOpName(lastModiOpName);
					oneLv1.setLastModiTime(lastModiTime);

					res.getDatas().add(oneLv1);
				}
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

			return res;
		}
		catch (Exception e)
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_PinPeiGoodsQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		String langType = req.getLangType();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append(""
				+ " select a.*,image.listimage from ("
				+ " select count(*) over() num,row_number() over (order by a.createtime) rn,"
				+ " a.eid,a.pluno,a.status,a.createopid,a.createopname,"
				+ " to_char(a.createtime,'YYYY-MM-DD hh24:mi:ss') as createtime,"
				+ " a.lastmodiopid,a.lastmodiopname,"
				+ " to_char(a.lastmoditime,'YYYY-MM-DD hh24:mi:ss') as lastmoditime,"
				+ " b.plu_name from dcp_pinpei_goods a"
				+ " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"'"
				+ " where a.eid='"+eId+"' "
				+ " ");

		if (!Check.Null(status))
		{
			sqlbuf.append(" and a.status='"+status+"' ");
		}
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and (a.pluno like '%%"+keyTxt+"%%' or b.plu_name like '%%"+keyTxt+"%%') ");
		}
		sqlbuf.append(""
				+ " )a"
				+ " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL'"
				+ " where rn>"+startRow+" and rn<="+(startRow+pageSize)
				+ " ");

		sql=sqlbuf.toString();
		return sql;
	}

}
