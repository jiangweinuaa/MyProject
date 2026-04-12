package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderPrintErrorSaveBufferReq;
import com.dsc.spos.json.cust.res.DCP_OrderPrintErrorSaveBufferRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_OrderPrintErrorSaveBuffer extends SPosAdvanceService<DCP_OrderPrintErrorSaveBufferReq, DCP_OrderPrintErrorSaveBufferRes>
{

	private String shopLogFileName = "WaimaiOrderPrintError";
	@Override
	protected void processDUID(DCP_OrderPrintErrorSaveBufferReq req, DCP_OrderPrintErrorSaveBufferRes res) throws Exception {

		String eId = req.getRequest().geteId();
		String shopNo = req.getRequest().getShopNo();
		String orderNo = req.getRequest().getOrderNo();
		String redis_key = orderRedisKeyInfo.redis_OrderPrintError+":"+eId+":"+shopNo;
		String hash_key = orderNo;
		String hash_value = orderNo;
		this.SaveRedisErrorOrderNo(redis_key,hash_key,hash_value);

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功!");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderPrintErrorSaveBufferReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderPrintErrorSaveBufferReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderPrintErrorSaveBufferReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderPrintErrorSaveBufferReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().geteId()))
		{
			errCt++;
			errMsg.append("企业编号eId不可为空值, ");
			isFail = true;
		}

		if (Check.Null(req.getRequest().getShopNo()))
		{
			errCt++;
			errMsg.append("当前门店shopNo不可为空值, ");
			isFail = true;
		}

		if (Check.Null(req.getRequest().getOrderNo()))
		{
			errCt++;
			errMsg.append("单号orderNo不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderPrintErrorSaveBufferReq> getRequestType() {
		return new TypeToken<DCP_OrderPrintErrorSaveBufferReq>(){};
	}

	@Override
	protected DCP_OrderPrintErrorSaveBufferRes getResponseType() {
		return new DCP_OrderPrintErrorSaveBufferRes();
	}

	private void SaveRedisErrorOrderNo(String redis_key, String hash_key, String hash_value) throws Exception
	{
		try
		{
			HelpTools.writelog_waimai("【打印异常的外卖订单写缓存】开始，" + "redis_key:" + redis_key + "，hash_key:" + hash_key
					+ " hash_value:" + hash_value);
			HelpTools.writelog_fileName("【打印异常的外卖订单写缓存】开始，" + "redis_key:" + redis_key + "，hash_key:" + hash_key
					+ " hash_value:" + hash_value, shopLogFileName);
			RedisPosPub redis = new RedisPosPub();
			boolean isexistHashkey = redis.IsExistHashKey(redis_key, hash_key);
			if (isexistHashkey)
			{

				redis.DeleteHkey(redis_key, hash_key);//
				HelpTools.writelog_waimai("【打印异常的外卖订单删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + "，hash_key:"
						+ hash_key + "，hash_value:" + hash_value);
				HelpTools.writelog_fileName("【打印异常的外卖订单删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + "，hash_key:"
						+ hash_key + "，hash_value:" + hash_value, shopLogFileName);
			}
			boolean nret = redis.setHashMap(redis_key, hash_key, hash_value);
			if (nret)
			{
				HelpTools.writelog_waimai("【打印异常的外卖订单写缓存】OK" + "，redis_key:" + redis_key + "，hash_key:" + hash_key
						+ "，hash_value:" + hash_value);
				HelpTools.writelog_fileName("【打印异常的外卖订单写缓存】OK" + "，redis_key:" + redis_key + "，hash_key:" + hash_key
						+ "，hash_value:" + hash_value, shopLogFileName);
			}
			else
			{
				HelpTools.writelog_waimai("【打印异常的外卖订单写缓存】Error" + "，redis_key:" + redis_key + "，hash_key:" + hash_key
						+ "，hash_value:" + hash_value);
				HelpTools.writelog_fileName("【打印异常的外卖订单写缓存】Error" + "，redis_key:" + redis_key + "，hash_key:" + hash_key
						+ "，hash_value:" + hash_value, shopLogFileName);
			}

		}
		catch (Exception e)
		{
			HelpTools.writelog_waimai("【打印异常的外卖订单写缓存】Exception:" + e.getMessage());
			HelpTools.writelog_fileName("【打印异常的外卖订单写缓存】Exception:" + e.getMessage(), shopLogFileName);
		}
	}
}


