package com.dsc.spos.service.imp.json;

import java.util.List;

import org.json.JSONObject;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderDelteBufferReq;
import com.dsc.spos.json.cust.res.DCP_OrderDelteBufferRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderDelteBuffer extends SPosAdvanceService<DCP_OrderDelteBufferReq,DCP_OrderDelteBufferRes>
{

	@Override
	protected void processDUID(DCP_OrderDelteBufferReq req, DCP_OrderDelteBufferRes res) throws Exception
	{
		// TODO Auto-generated method stub
		String eId = req.getRequest().geteId();
		String shopNo = req.getRequest().getShopNo();
		String orderNo = req.getRequest().getOrderNo();
		String reqStatus = req.getRequest().getStatus();
		RedisPosPub redis = new RedisPosPub();
		String redis_key = orderRedisKeyInfo.redis_OrderTableName + ":" + eId + ":" + shopNo;
		String hash_key = orderNo;
        /*String redis_key_printError = orderRedisKeyInfo.redis_OrderPrintError + ":" + eId + ":" + shopNo;
       //删除打印异常外卖单缓存
		String shopLogFileName = "WaimaiOrderPrintError";
        try
        {
            HelpTools.writelog_waimai("【删除门店打印异常外卖单Redis】开始，redis_key：" + redis_key_printError+" hash_key:"+hash_key);
			HelpTools.writelog_fileName("【删除门店打印异常外卖单Redis】开始，redis_key：" + redis_key_printError+" hash_key:"+hash_key, shopLogFileName);
            redis.DeleteHkey(redis_key_printError, hash_key);
            HelpTools.writelog_waimai("【删除门店打印异常外卖单Redis】成功，redis_key：" + redis_key_printError+" hash_key:"+hash_key);
			HelpTools.writelog_fileName("【删除门店打印异常外卖单Redis】成功，redis_key：" + redis_key_printError+" hash_key:"+hash_key, shopLogFileName);
        }
        catch (Exception e)
        {
            HelpTools.writelog_waimai("【删除门店打印异常外卖单Redis】异常："+e.getMessage()+"，redis_key：" + redis_key_printError+" hash_key:"+hash_key);
			HelpTools.writelog_fileName("【删除门店打印异常外卖单Redis】异常："+e.getMessage()+"，redis_key：" + redis_key_printError+" hash_key:"+hash_key, shopLogFileName);
        }*/

        HelpTools.writelog_waimai("第三方调用DCP_OrderDelteBuffer接口【开始删除缓存】"+"redis_key:"+redis_key+" hash_key:"+hash_key+" 传入的Status:"+reqStatus);

		try
		{
			String ordermap = redis.getHashMap(redis_key, hash_key);
			try
			{
				if (ordermap != null && ordermap.isEmpty() == false)
				{
					JSONObject obj = new JSONObject(ordermap);
					String redis_status = obj.get("status").toString();
					String redis_refundStatus = obj.get("refundStatus").toString();
					if (redis_status.equals(reqStatus) == false)
					{
						HelpTools.writelog_waimai("第三方调用DCP_OrderDelteBuffer接口【对比缓存中stuas不一致不能删除】" + "redis_key:" + redis_key
								+ " hash_key:" + hash_key + " redis_status:" + redis_status);
						// redis.Close();
						res.setSuccess(true);
						res.setServiceStatus("000");
						res.setServiceDescription("该订单删除缓存失败（与缓存中订单状态不一致）！");
						return;
					}

				}

			} 
			catch (Exception e)
			{
				// redis.Close();

			}

			redis.DeleteHkey(redis_key, hash_key);//
			HelpTools.writelog_waimai(
					"第三方调用DCP_OrderDelteBuffer接口【删除存在hash_key的缓存】成功！" + "redis_key:" + redis_key + " hash_key:" + hash_key);
			// redis.Close();
			res.setServiceDescription("该订单删除缓存成功！");

		} 
		catch (Exception e)
		{
			// TODO: handle exception
		}


		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderDelteBufferReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderDelteBufferReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderDelteBufferReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderDelteBufferReq req) throws Exception
	{
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

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
			errMsg.append("订单号orderNo不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderDelteBufferReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderDelteBufferReq>(){};
	}

	@Override
	protected DCP_OrderDelteBufferRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderDelteBufferRes();
	}

}
