package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DualPlayTemDetailReq;
import com.dsc.spos.json.cust.res.DCP_DualPlayTemDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 服務函數：DualPlayGetDCP
 *   說明：双屏播放查询DCP
 * 服务说明：双屏播放查询DCP
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayTemDetail extends SPosBasicService<DCP_DualPlayTemDetailReq, DCP_DualPlayTemDetailRes>  {

	@Override
	protected boolean isVerifyFail(DCP_DualPlayTemDetailReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("request不能为空值 ");
			throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().getTemplateNo()))
		{
			errMsg.append("模板编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DualPlayTemDetailReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DualPlayTemDetailReq>(){};
	}

	@Override
	protected DCP_DualPlayTemDetailRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DualPlayTemDetailRes();
	}

	@Override
	protected DCP_DualPlayTemDetailRes processJson(DCP_DualPlayTemDetailReq req) throws Exception
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_DualPlayTemDetailRes res = null;
		res = this.getResponse();
		DCP_DualPlayTemDetailRes.level1Elm oneLv1 = res.new level1Elm();

		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);


			if (getQData != null && getQData.isEmpty() == false)
			{
				MyCommon cm=new MyCommon();
				String imgBaseUrl = cm.getDualplayImgBaseUrl(req.geteId());
				Map<String, Object> oneData1 = getQData.get(0);

				String templateNo = oneData1.get("TEMPLATENO").toString();
				String templateName = oneData1.get("TEMPLATENAME").toString();
				String platformType = oneData1.get("PLATFORMTYPE").toString();
				String shopType = oneData1.get("SHOPTYPE").toString();
				String timeType= oneData1.get("TIMETYPE").toString();
				String memo = oneData1.get("MEMO").toString();
				String pollTime = oneData1.get("POLLTIME").toString();
				String status = oneData1.get("STATUS").toString();

				//设置响应
				oneLv1.setTemplateNo(templateNo);
				oneLv1.setTemplateName(templateName);
				oneLv1.setPlatformType(platformType);
				oneLv1.setShopType(shopType);
				oneLv1.setTimeType(timeType);
				oneLv1.setMemo(memo);
				oneLv1.setStatus(status);
				oneLv1.setPollTime(pollTime);

				oneLv1.setFileList(new ArrayList<DCP_DualPlayTemDetailRes.level1File>());
				oneLv1.setShopList(new ArrayList<DCP_DualPlayTemDetailRes.level1Shop>());
				oneLv1.setTimeList(new ArrayList<DCP_DualPlayTemDetailRes.level1Time>());
				Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查询条件
				condition1.put("FILENAME", true);
				List<Map<String, Object>> getFiles= MapDistinct.getMap(getQData, condition1);
				if (getFiles!=null&&getFiles.isEmpty()==false)
				{
					for (Map<String, Object> par : getFiles)
					{
						String fileName = par.getOrDefault("FILENAME","").toString();
						if (fileName==null||fileName.isEmpty())
						{
							continue;
						}

						DCP_DualPlayTemDetailRes.level1File oneLv2 = res.new level1File();
						oneLv2.setFileName(fileName);
						oneLv2.setFileUrl(imgBaseUrl+"/"+fileName);
						oneLv2.setPriority(par.getOrDefault("PRIORITY","0").toString());
                        oneLv2.setFileType(par.getOrDefault("FILETYPE","").toString());
						oneLv1.getFileList().add(oneLv2);
					}
				}

				condition1.clear();
				condition1.put("SHOPID", true);
				List<Map<String, Object>> getShops= MapDistinct.getMap(getQData, condition1);
				if (getShops!=null&&getShops.isEmpty()==false)
				{
					for (Map<String, Object> par : getShops)
					{
						String shopId = par.getOrDefault("SHOPID","").toString();
						if (shopId==null||shopId.isEmpty())
						{
							continue;
						}

						DCP_DualPlayTemDetailRes.level1Shop oneLv2 = res.new level1Shop();
						oneLv2.setShopId(shopId);
						oneLv2.setShopName(par.getOrDefault("ORG_NAME","").toString());
						oneLv1.getShopList().add(oneLv2);
					}
				}

				condition1.clear();
				condition1.put("ITEM", true);
				List<Map<String, Object>> getTimes= MapDistinct.getMap(getQData, condition1);
				if (getTimes!=null&&getTimes.isEmpty()==false)
				{
					for (Map<String, Object> par : getTimes)
					{
						String item = par.getOrDefault("ITEM","").toString();
						if (item==null||item.isEmpty())
						{
							continue;
						}
						String lbDate = par.getOrDefault("LBDATE","").toString();
						String lbTime = par.getOrDefault("LBTIME","").toString();

						String leDate = par.getOrDefault("LEDATE","").toString();
						String leTime = par.getOrDefault("LETIME","").toString();

						String startTime = "";
						String endTime = "";
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

						try
						{
							Date date1 = sdf1.parse(lbDate+lbTime);

							startTime = sdf2.format(date1);

						}
						catch (Exception e)
						{
							startTime = "";
						}

						try
						{
							Date date1 = sdf1.parse(leDate+leTime);

							endTime = sdf2.format(date1);

						}
						catch (Exception e)
						{
							endTime = "";
						}

						DCP_DualPlayTemDetailRes.level1Time oneLv2 = res.new level1Time();
						oneLv2.setStartTime(startTime);
						oneLv2.setEndTime(endTime);
						oneLv1.getTimeList().add(oneLv2);
					}
				}



			}

			res.setDatas(oneLv1);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DualPlayTemDetailReq req) throws Exception {
		String sql=null;			
		String eId = req.geteId();
		String templateNo = req.getRequest().getTemplateNo();
		String langType = req.getLangType();
		if (langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append(" select * from (");
		sqlbuf.append(" select a.*,b.FILENAME,B.PRIORITY,B.FILETYPE,C.ITEM,C.LBDATE,C.LBTIME,C.LEDATE,C.LETIME,D.SHOPID,E.ORG_NAME from DCP_DUALPLAY_TEMPLATE a ");
		sqlbuf.append(" left join DCP_DUALPLAY_TEMPLATE_FILE b on b.eid=a.eid and b.templateno=a.templateno ");
		sqlbuf.append(" left join DCP_DUALPLAY_TEMPLATE_TIME c on c.eid=a.eid and c.templateno=a.templateno ");
		sqlbuf.append(" left join DCP_DUALPLAY_TEMPLATE_SHOP d on d.eid=a.eid and d.templateno=a.templateno ");
		sqlbuf.append(" left join DCP_ORG_LANG e on e.eid=d.eid and e.organizationno=d.shopid and e.lang_type='"+langType+"' ");
		sqlbuf.append(" where a.EID='"+ eId +"' and a.templateno='"+templateNo+"' ");
		sqlbuf.append(" ) order by PRIORITY, ITEM" );
		sql = sqlbuf.toString();
		return sql;

	}

}


