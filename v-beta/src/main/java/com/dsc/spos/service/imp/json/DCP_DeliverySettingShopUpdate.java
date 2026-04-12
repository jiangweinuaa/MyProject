package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DeliverySettingShopUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DeliverySettingShopUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DeliverySettingShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：ParaDefineUpdate
 *   說明：参数定义修改
 * 服务说明：参数定义修改
 * @author Jinzma 
 * @since  2017-03-03
 */
public class DCP_DeliverySettingShopUpdate extends SPosAdvanceService<DCP_DeliverySettingShopUpdateReq, DCP_DeliverySettingShopUpdateRes>
{
	@Override
	protected void processDUID(DCP_DeliverySettingShopUpdateReq req, DCP_DeliverySettingShopUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		try 
		{
			String sql=getdataExist_SQL(req);	
			String[] conditionValues = null ; //查詢條件
			List<Map<String, Object>> getQData_checkNO = this.doQueryData(sql,conditionValues);
			if(getQData_checkNO!=null && !getQData_checkNO.isEmpty())
			{
				String eId=req.geteId();
				String appId=req.getRequest().getAppId();
				String deliveryType =req.getRequest().getDeliveryType();

				//删除原有单身
				DelBean db1 = new DelBean("DCP_OUTSALESET_SHOP");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("DELIVERYTYPE", new DataValue(deliveryType, Types.VARCHAR));
				db1.addCondition("APPID", new DataValue(appId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));


				if (req.getRequest().getDatas()!=null)
				{
					String[] columnsName = {"EID","DELIVERYTYPE","APPID","SHOPID"};
					for (level1Elm par : req.getRequest().getDatas())
					{

						DataValue[] columnsVal = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(deliveryType, Types.VARCHAR),
								new DataValue(appId, Types.VARCHAR),
								new DataValue(par.getShopId(), Types.VARCHAR)
						};

						InsBean ib1 = new InsBean("DCP_OUTSALESET_SHOP", columnsName);
						ib1.addValues(columnsVal);
						this.addProcessData(new DataProcessBean(ib1));
					}

				}

				//更新单头
				String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_OUTSALESET");
				ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
				ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
				ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("DELIVERYTYPE", new DataValue(deliveryType, Types.VARCHAR));
				ub1.addCondition("APPID", new DataValue(appId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				res.setSuccess(false);
				res.setServiceDescription("物流设置不存在！");
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DeliverySettingShopUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DeliverySettingShopUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DeliverySettingShopUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DeliverySettingShopUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}
		
		if (Check.Null(req.getRequest().getAppId()))
		{
			errCt++;
			errMsg.append("物流ID不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getDeliveryType()))
		{
			errCt++;
			errMsg.append("物流类型不可为空值, ");
			isFail = true;
		}

		if (req.getRequest().getDatas()!=null&&!req.getRequest().getDatas().isEmpty())
		{
			for (level1Elm par : req.getRequest().getDatas())
			{
				if (Check.Null(par.getShopId()))
				{
					errCt++;
					errMsg.append("生效门店ID不可为空值, ");
					isFail = true;
					break;
				}
			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_DeliverySettingShopUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DeliverySettingShopUpdateReq>(){};
	}

	@Override
	protected DCP_DeliverySettingShopUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DeliverySettingShopUpdateRes();
	}

	protected String getdataExist_SQL(DCP_DeliverySettingShopUpdateReq req)
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId=req.geteId();
		String appId=req.getRequest().getAppId();
		String deliveryType =req.getRequest().getDeliveryType();

		sqlbuf.append("select * from DCP_OUTSALESET "
				+ " WHERE EID='"+eId +"'  "
				+ " AND APPID='"+appId +"' AND DELIVERYTYPE='"+deliveryType +"'  ");

		sql = sqlbuf.toString(); 	
		return sql;	
	}



}
