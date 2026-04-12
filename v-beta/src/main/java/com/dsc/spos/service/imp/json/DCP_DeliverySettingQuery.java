package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_DeliverySettingQueryReq;
import com.dsc.spos.json.cust.res.DCP_DeliverySettingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 货运厂商查询
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_DeliverySettingQuery extends SPosBasicService<DCP_DeliverySettingQueryReq, DCP_DeliverySettingQueryRes> 
{

	@Override
	protected boolean isVerifyFail(DCP_DeliverySettingQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DeliverySettingQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DeliverySettingQueryReq>(){};
	}

	@Override
	protected DCP_DeliverySettingQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DeliverySettingQueryRes();
	}

	@Override
	protected DCP_DeliverySettingQueryRes processJson(DCP_DeliverySettingQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		DCP_DeliverySettingQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try 
		{
			sql = this.getQuerySql(req);
			String[] conditionValues = {}; //查詢條件
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues);

			String num = getQDataDetail.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(res.new level1Elm());
				res.getDatas().setDeliveryList(new ArrayList<DCP_DeliverySettingQueryRes.delivery>());

				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_DeliverySettingQueryRes.delivery dl = res.new delivery();

					dl.setApiUrl(oneData.get("APIURL").toString());
					dl.setApiUrlThree(oneData.get("APIURLTHREE").toString());
					dl.setApiUrlTwo(oneData.get("APIURLTWO").toString());
					dl.setAppId(oneData.get("APPID").toString());
					dl.setAppSecret(oneData.get("APPSECRET").toString());
					dl.setAppSignKey(oneData.get("APPSIGNKEY").toString());
					dl.setCreateOpId(oneData.get("CREATEOPID").toString());
					dl.setCreateOpName(oneData.get("CREATEOPNAME").toString());
					dl.setCreateTime(oneData.get("CREATETIME").toString());
					dl.setDeliveryMode(oneData.get("DELIVERYMODE").toString());
					dl.setPrintMode(oneData.get("PRINTMODE").toString());
					dl.setDeliveryType(oneData.get("DELIVERYTYPE").toString());
					dl.setIv(oneData.get("IV").toString());
					dl.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
					dl.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
					dl.setLastModiTime(oneData.get("LASTMODITIME").toString());
					dl.setShopCode(oneData.get("SHOPCODE").toString());
					dl.setStatus(oneData.get("STATUS").toString());
					dl.setV(oneData.get("V").toString());
                    dl.setAppName(oneData.getOrDefault("APPNAME","").toString());
                    dl.setShopType(oneData.getOrDefault("SHOPTYPE","").toString());
                    dl.setYtoType(oneData.getOrDefault("YTOTYPE","N").toString());
                    dl.setProductType(oneData.getOrDefault("PRODUCTTYPE","").toString());
                    dl.setQueryType(oneData.getOrDefault("QUERYTYPE","8002").toString());
                    dl.setShipperType(oneData.getOrDefault("SHIPPERTYPE","5").toString());
                    dl.setShipMode(oneData.getOrDefault("SHIPMODE","0").toString());//kdn物流类型 0全国快递，1同城快递
                    dl.setInstantConfigType(oneData.getOrDefault("INSTANTCONFIGTYPE","").toString());
                    dl.setLevelCategory(oneData.getOrDefault("LEVELCATEGORY","").toString());
                    dl.setIsTest(oneData.getOrDefault("ISTEST","N").toString());
                    dl.setDelayTimeSpan(oneData.getOrDefault("DELAYTIME_SPAN","60").toString());
					dl.setIsDelay(oneData.getOrDefault("ISDELAY","N").toString());
                    res.getDatas().getDeliveryList().add(dl);
					dl = null;
				}
			}

		} catch (Exception e) 
		{

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
	protected String getQuerySql(DCP_DeliverySettingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String deliveryType = req.getRequest().getDeliveryType();
		String status = req.getRequest().getStatus();

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append("SELECT * "
				+ " FROM ("
				+ " select count(*) over() num, row_number() over(order by a.deliverytype ,a.APPID) rn,a.* from DCP_OUTSALESET a "
				+ " WHERE a.EID = '"+eId+"' " );

		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append("and ( a.APPID like '%%"+keyTxt+"%%' OR a.APPNAME like '%%"+keyTxt+"%%' ) ");
		}
		if (status != null && status.length()>0)
		{
			sqlbuf.append("and a.STATUS= '"+status+"' ");
		}
		if (deliveryType != null && deliveryType.length()>0)
		{
			sqlbuf.append("and a.DELIVERYTYPE= '"+deliveryType+"' ");
		}

		sqlbuf.append( " ) "
				+ " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ "  ");

		sql = sqlbuf.toString();
		return sql;
	}


}
