package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DinnerAreaCreateReq;
import com.dsc.spos.json.cust.res.DCP_DinnerAreaCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_DinnerAreaCreate extends SPosAdvanceService<DCP_DinnerAreaCreateReq, DCP_DinnerAreaCreateRes>
{

	@Override
	protected void processDUID(DCP_DinnerAreaCreateReq req, DCP_DinnerAreaCreateRes res) throws Exception
	{

		String eId = req.geteId();
        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(eId);
        String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
        List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
        String apiUserCode = "";
        String apiUserKey = "";
        if (result != null && result.size() == 2) {
            for (Map<String, Object> map : result) {
                if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                    apiUserCode = map.get("ITEMVALUE").toString();
                } else {
                    apiUserKey = map.get("ITEMVALUE").toString();
                }
            }
        }
        PosPub.clearTableBaseInfoCache(posUrl, apiUserCode, apiUserKey,eId,req.getShopId());

		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		String dinnerGroup = req.getRequest().getDinnerGroup();
		
		String shopId = req.getShopId();

		String sql = null;
		sql = this.getDINNERGROUPNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);

		//查询当前门店区域最大优先级
		String maxPrioritySql = " SELECT  nvl(MAX(priority) + 1 , '1') AS maxPriority FROM DCP_DINNER_area"
				+ " WHERE EID = '"+eId+"' AND organizationNo = '"+shopId+"'   ";

		List<Map<String, Object>> priorityDatas = this.doQueryData(maxPrioritySql,null);
		String maxPriority = "1";

		if(priorityDatas!=null && priorityDatas.size() > 0){
			maxPriority = priorityDatas.get(0).get("MAXPRIORITY").toString();
		}

		if(getQData_check==null || getQData_check.isEmpty())
		{

			//DCP_DINNER_AREA
			String[] columnsModular =
				{
						"EID",
						"ORGANIZATIONNO",
						"DINNERGROUP",
						"DINNERGROUPNAME",
						"STATUS",
						"UPDATE_TIME",
						"PRIORITY",
                        "GROUPTYPE",
                        "RESTRICTGROUP"
				};


			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]
					{
							new DataValue(req.geteId(), Types.VARCHAR),
							new DataValue(req.getShopId(), Types.VARCHAR),
							new DataValue(req.getRequest().getDinnerGroup(), Types.VARCHAR),
							new DataValue(req.getRequest().getDinnerGroupName(), Types.VARCHAR),
							new DataValue(req.getRequest().getStatus(), Types.VARCHAR),
							new DataValue(mySysTime, Types.VARCHAR)	,
							new DataValue(maxPriority, Types.VARCHAR),
							new DataValue(req.getRequest().getGroupType(), Types.VARCHAR),
							new DataValue(req.getRequest().getRestrictGroup(), Types.VARCHAR)

					};

			InsBean ib1 = new InsBean("DCP_DINNER_AREA", columnsModular);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		else
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("桌台区域编码已经存在！");
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DinnerAreaCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DinnerAreaCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DinnerAreaCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DinnerAreaCreateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String DINNERGROUP =req.getRequest().getDinnerGroup();
		String DINNERGROUPNAME =req.getRequest().getDinnerGroupName();
		String status =req.getRequest().getStatus();

		if (Check.Null(DINNERGROUP))
		{
			errMsg.append("桌台区域编码不可为空值, ");
			isFail = true;
		}

		if (Check.Null(DINNERGROUPNAME))
		{
			errMsg.append("桌台区域名称不可为空值, ");
			isFail = true;
		}

		if (Check.Null(status))
		{
			errMsg.append("状态不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DinnerAreaCreateReq> getRequestType()
	{
		return new TypeToken<DCP_DinnerAreaCreateReq>(){};
	}

	@Override
	protected DCP_DinnerAreaCreateRes getResponseType()
	{
		return new DCP_DinnerAreaCreateRes();
	}


	protected String getDINNERGROUPNO_SQL(DCP_DinnerAreaCreateReq req)
	{
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_DINNER_AREA WHERE EID='"+req.geteId()+"' AND ORGANIZATIONNO='"+req.getShopId()+"' AND DINNERGROUP='"+req.getRequest().getDinnerGroup()+"' ");

		sql = sqlbuf.toString();

		return sql;
	}



}
