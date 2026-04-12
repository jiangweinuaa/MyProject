package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.utils.PosPub;
import org.apache.commons.lang.StringUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_DinnerAreaUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DinnerAreaUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_DinnerAreaUpdate extends SPosAdvanceService<DCP_DinnerAreaUpdateReq,DCP_DinnerAreaUpdateRes>
{

	@Override
	protected void processDUID(DCP_DinnerAreaUpdateReq req, DCP_DinnerAreaUpdateRes res) throws Exception 
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

		//2019-07-15 新增priority 用于排序
		String priority = req.getRequest().getPriority(); 
		String toPriority = req.getRequest().getToPriority();
		
		
		String organizationNO = req.getOrganizationNO();
		
		String sql = null;
		sql = this.getDINNERGROUPNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{			
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("找不到桌台区域信息！");			
		}
		else
		{
			if(Check.Null(toPriority)){ 
								
				//更新单头
				UptBean ub1 = new UptBean("DCP_DINNER_AREA");			
				ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
				ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
				ub1.addCondition("DINNERGROUP", new DataValue(req.getRequest().getDinnerGroup(), Types.VARCHAR));
				
				ub1.addUpdateValue("DINNERGROUPNAME",new DataValue(req.getRequest().getDinnerGroupName(), Types.VARCHAR)); 
				ub1.addUpdateValue("PRIORITY",new DataValue(req.getRequest().getPriority(), Types.VARCHAR));
				ub1.addUpdateValue("STATUS",new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
				ub1.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR));
				ub1.addUpdateValue("GROUPTYPE",new DataValue(req.getRequest().getGroupType(), Types.VARCHAR));
				ub1.addUpdateValue("RESTRICTGROUP",new DataValue(req.getRequest().getRestrictGroup(), Types.VARCHAR));

				this.addProcessData(new DataProcessBean(ub1));	
				
			}
			else{

				int toPt = 0;
				int pt = 0;
				
				if(Check.Null(priority) || Check.Null(toPriority)){
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("服务执行成失败：优先级序号 和 目标序号  均不能为空！！！");	
					return ;
				}
				else{
					toPt = Integer.parseInt(toPriority);
					pt = Integer.parseInt(priority);
					
					if(toPt!=pt){
						UptBean ub2 = new UptBean("DCP_DINNER_AREA");		
						
						if(toPt > pt){ //相当于 下移
							String[] priorityList = new String[toPt-pt];
							for(int i=0; i < toPt-pt ; i++) {
								priorityList[i] = String.valueOf(pt +i+ 1);
							}
							String str1 = StringUtils.join(priorityList,",");
							
							ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
							ub2.addUpdateValue("PRIORITY ", new DataValue(1, Types.INTEGER,DataExpression.SubSelf)); 
							
						}
						else {   //相当于上移 
							
							String[] priorityList = new String[pt-toPt];
							for(int i=0; i < pt- toPt ; i++) {
								priorityList[i] = String.valueOf(toPt + i );
							}
							String str1 = StringUtils.join(priorityList,",");
							
							ub2.addCondition("PRIORITY", new DataValue(str1, Types.ARRAY,DataExpression.IN));//Greater表示大于
							ub2.addUpdateValue("PRIORITY ", new DataValue(1, Types.INTEGER,DataExpression.UpdateSelf)); 
						}
						
						ub2.addCondition("EID", new DataValue( eId, Types.VARCHAR));
//						ub2.addCondition("DINNERGROUP", new DataValue( req.getDinnerGroup(), Types.VARCHAR));
						ub2.addCondition("ORGANIZATIONNO", new DataValue( organizationNO, Types.VARCHAR));
						
						this.addProcessData(new DataProcessBean(ub2));	
					}
					
					UptBean ub3 = new UptBean("DCP_DINNER_AREA");
					ub3.addCondition("DINNERGROUP", new DataValue( req.getRequest().getDinnerGroup(), Types.VARCHAR));
					ub3.addCondition("EID", new DataValue( eId, Types.VARCHAR));
					ub3.addCondition("ORGANIZATIONNO", new DataValue( organizationNO, Types.VARCHAR));
					
					ub3.addUpdateValue("DINNERGROUPNAME",new DataValue(req.getRequest().getDinnerGroupName(), Types.VARCHAR)); 
					ub3.addUpdateValue("STATUS",new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
					ub3.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR));
					ub3.addUpdateValue("PRIORITY ", new DataValue(toPriority,Types.VARCHAR)); 
					
					this.addProcessData(new DataProcessBean(ub3));	
					
				}
				
			}
			
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DinnerAreaUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DinnerAreaUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DinnerAreaUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DinnerAreaUpdateReq req) throws Exception
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
	protected TypeToken<DCP_DinnerAreaUpdateReq> getRequestType()
	{
		return new TypeToken<DCP_DinnerAreaUpdateReq>(){};
	}

	@Override
	protected DCP_DinnerAreaUpdateRes getResponseType() 
	{
		return new DCP_DinnerAreaUpdateRes();
	}
	
	
	protected String getDINNERGROUPNO_SQL(DCP_DinnerAreaUpdateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_DINNER_AREA WHERE EID='"+req.geteId()+"' AND ORGANIZATIONNO='"+req.getShopId()+"' AND DINNERGROUP='"+req.getRequest().getDinnerGroup()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	

}
