package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DualPlayTemCreateReq;
import com.dsc.spos.json.cust.req.DCP_DualPlayTemCreateReq.level1Shop;
import com.dsc.spos.json.cust.req.DCP_DualPlayTemCreateReq.level1Time;
import com.dsc.spos.json.cust.req.DCP_DualPlayTemCreateReq.level1File;
import com.dsc.spos.json.cust.res.DCP_DualPlayTemCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class DCP_DualPlayTemCreate extends SPosAdvanceService<DCP_DualPlayTemCreateReq, DCP_DualPlayTemCreateRes>
{

	@Override
	protected boolean isVerifyFail(DCP_DualPlayTemCreateReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

		String dualPlayID = req.getRequest().getTemplateId();
		String templateName = req.getRequest().getTemplateName();
		String platformType = req.getRequest().getPlatformType();
		String shopType = req.getRequest().getShopType();
		String timeType = req.getRequest().getTimeType();
		String status = req.getRequest().getStatus();
		String pollTime = req.getRequest().getPollTime();
		List<level1Shop> jsonShops = req.getRequest().getShopList();
		List<level1Time> jsonTimes = req.getRequest().getTimeList();
		List<level1File> jsonFiles = req.getRequest().getFileList();

		if (Check.Null(dualPlayID)) {
			errMsg.append("模板创建ID不可为空值, ");
			isFail = true;
		}

		if (Check.Null(templateName)) {
			errMsg.append("模板名称不可为空值, ");
			isFail = true;
		}


		if (Check.Null(platformType)) {
			errMsg.append("平台类型不可为空值, ");
			isFail = true;
		}
		if (Check.Null(shopType)) {
			errMsg.append("门店类型不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(timeType)) {
			errMsg.append("时间类型不可为空值, ");
			isFail = true;
		}

		if (Check.Null(status)) {
			errMsg.append("状态不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(pollTime))
		{
			errMsg.append("轮询时间不可为空值, ");
			isFail = true;
		}
		else
		{
			if (!PosPub.isNumeric(pollTime))
			{
				errMsg.append("轮询时间必须为数值, ");
				isFail = true;
			}
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
		if (jsonShops!=null&&jsonShops.isEmpty()==false)
		{
			for (level1Shop par : jsonShops) {
				if (Check.Null(par.getShopId())) {
					errMsg.append("门店编号不可为空值, ");
					isFail = true;
				}
				if (isFail){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}

			}
		}
		if (jsonTimes!=null&&jsonTimes.isEmpty()==false)
		{
			for (level1Time par : jsonTimes) {
				if (Check.Null(par.getStartTime())) {
					errMsg.append("开始时间不可为空值, ");
					isFail = true;
				}
				else
				{
					try
					{
						Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(par.getStartTime());
					}
					catch (Exception e)
					{
						errMsg.append("开始时间格式不正确(正确格式:yyyy-MM-dd HH:mm:ss),");
						isFail = true;
					}
				}

				if (Check.Null(par.getEndTime())) {
					errMsg.append("结束时间不可为空值, ");
					isFail = true;
				}
				else
				{
					try
					{
						Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(par.getEndTime());
					}
					catch (Exception e)
					{
						errMsg.append("结束时间格式不正确(正确格式:yyyy-MM-dd HH:mm:ss),");
						isFail = true;
					}
				}



				if (isFail){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}
			}

		}
		if (jsonFiles!=null&&jsonFiles.isEmpty()==false)
		{
			for (level1File par:jsonFiles)
			{
				if (Check.Null(par.getFileName())) {
					errMsg.append("文件名称不可为空值, ");
					isFail = true;
				}
				if (Check.Null(par.getFileType())) {
					errMsg.append("文件类型不可为空值, ");
					isFail = true;
				}
				if (Check.Null(par.getPriority())) {
					errMsg.append("优先级不可为空值, ");
					isFail = true;
				}
				else
				{
					if (!PosPub.isNumeric(par.getPriority()))
					{
						errMsg.append("优先级必须为数值, ");
						isFail = true;
					}
				}
				if (isFail){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}

			}
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DualPlayTemCreateReq> getRequestType() {
		return new TypeToken<DCP_DualPlayTemCreateReq>(){};
	}

	@Override
	protected DCP_DualPlayTemCreateRes getResponseType() {
		return new DCP_DualPlayTemCreateRes();
	}

	@Override
	protected void processDUID(DCP_DualPlayTemCreateReq req,DCP_DualPlayTemCreateRes res) throws Exception {		
		String eId = req.geteId();
		String opNo = req.getOpNO();
		String opName = req.getOpName();
		String templateId = req.getRequest().getTemplateId();
		String templateName = req.getRequest().getTemplateName();
		String platformType = req.getRequest().getPlatformType();
		String shopType = req.getRequest().getShopType();
		String timeType = req.getRequest().getTimeType();
		String status = req.getRequest().getStatus();
		String pollTime = req.getRequest().getPollTime();
		String memo = req.getRequest().getMemo();
		List<level1Shop> jsonShops = req.getRequest().getShopList();
		List<level1Time> jsonTimes = req.getRequest().getTimeList();
		List<level1File> jsonFiles = req.getRequest().getFileList();

		try 
		{
			if (checkGuid(req))
			{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败，该模板ID("+templateId+")已存在!");
				return;
			}

			String templateNo = "KXMB"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			String lastModifTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

			DataValue[] insValue = null;

			//新增生效门店
			if (jsonShops!=null&&jsonShops.isEmpty()==false)
			{
				String[] columns2 = {
						"EID", "TEMPLATENO","SHOPID"};
				InsBean ib2 = new InsBean("DCP_DUALPLAY_TEMPLATE_SHOP", columns2);
				for (level1Shop par : jsonShops) {
					String shopId= par.getShopId();
					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(templateNo, Types.VARCHAR),
							new DataValue(shopId, Types.VARCHAR)
					};
					ib2.addValues(insValue);
				}
				this.addProcessData(new DataProcessBean(ib2)); //
			}


			//新增生效时间
			if (jsonTimes!=null&&jsonTimes.isEmpty()==false)
			{
				String[] columns3 = {
						"EID", "TEMPLATENO","ITEM","LBDATE","LBTIME","LEDATE","LETIME"};
				InsBean ib3 = new InsBean("DCP_DUALPLAY_TEMPLATE_TIME", columns3);
				int item = 1;
				for (level1Time par : jsonTimes) {

					String startTime = par.getStartTime();
					Date date_start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
					String lbDate= new SimpleDateFormat("yyyyMMdd").format(date_start);
					String lbTime= new SimpleDateFormat("HHmmss").format(date_start);;

					String endTime = par.getEndTime();
					Date date_end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
					String leDate= new SimpleDateFormat("yyyyMMdd").format(date_end);
					String leTime= new SimpleDateFormat("HHmmss").format(date_end);;

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(templateNo, Types.VARCHAR),
							new DataValue(item, Types.INTEGER),
							new DataValue(lbDate, Types.VARCHAR),
							new DataValue(lbTime, Types.VARCHAR),
							new DataValue(leDate, Types.VARCHAR),
							new DataValue(leTime, Types.VARCHAR)
					};
					ib3.addValues(insValue);
					item++;
				}
				this.addProcessData(new DataProcessBean(ib3)); //
			}

			//新增生效门店
			if (jsonFiles!=null&&jsonFiles.isEmpty()==false)
			{
				String[] columns2 = {
						"EID", "TEMPLATENO","FILENAME","PRIORITY","FILETYPE"};
				InsBean ib2 = new InsBean("DCP_DUALPLAY_TEMPLATE_FILE", columns2);
				for (level1File par : jsonFiles) {

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(templateNo, Types.VARCHAR),
							new DataValue(par.getFileName(), Types.VARCHAR),
							new DataValue(par.getPriority(), Types.VARCHAR),
                            new DataValue(par.getFileType(), Types.VARCHAR)
					};
					ib2.addValues(insValue);
				}
				this.addProcessData(new DataProcessBean(ib2)); //
			}

			//新增单头
			String[] columns1 = {
					"EID", "TEMPLATENO","TEMPLATENAME","TEMPLATEID","PLATFORMTYPE","SHOPTYPE","TIMETYPE", "MEMO",
					"POLLTIME","STATUS","CREATEOPID","CREATEOPNAME","CREATETIME"
			};
			insValue = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(templateNo, Types.VARCHAR),
					new DataValue(templateName, Types.VARCHAR),
					new DataValue(templateId, Types.VARCHAR),
					new DataValue(platformType, Types.VARCHAR),
					new DataValue(shopType, Types.VARCHAR),
					new DataValue(timeType, Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue(pollTime, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(opName, Types.VARCHAR),
					new DataValue(lastModifTime, Types.DATE)
			};

			InsBean ib1 = new InsBean("DCP_DUALPLAY_TEMPLATE", columns1);
			ib1.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}	

	@Override
	protected List<InsBean> prepareInsertData(DCP_DualPlayTemCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DualPlayTemCreateReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DualPlayTemCreateReq req) throws Exception {
		return null;
	}	

	@Override
	protected String getQuerySql(DCP_DualPlayTemCreateReq req) throws Exception {
		String sql = null;
		return sql;
	}

	private boolean checkGuid(DCP_DualPlayTemCreateReq req) throws Exception {
		String sql = null;
		String eId = req.geteId();
		String guid = req.getRequest().getTemplateId();
		boolean existGuid;
		String[] conditionValues = {eId,guid};
		sql = "select *  from DCP_DUALPLAY_TEMPLATE  where EID=? AND TEMPLATEID = ? ";
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		return existGuid;
	}

}
