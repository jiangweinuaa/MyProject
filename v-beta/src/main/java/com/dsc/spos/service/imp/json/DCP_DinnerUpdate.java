package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_DinnerCreateReq;
import com.dsc.spos.utils.PosPub;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DinnerUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DinnerUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

public class DCP_DinnerUpdate extends SPosAdvanceService<DCP_DinnerUpdateReq,DCP_DinnerUpdateRes>
{

	@Override
	protected void processDUID(DCP_DinnerUpdateReq req, DCP_DinnerUpdateRes res) throws Exception 
	{
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

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

		
		String organizationNO = req.getShopId();
		String dinnerNO = req.getRequest().getDinnerNo();
		String priority = req.getRequest().getPriority();
		String dinnerGroup = req.getRequest().getDinnerGroup();
		String toDinnerGroup = req.getRequest().getToDinnerGroup();
//		String toDinnerNO = req.getToDinnerNO();
		String toPriority = req.getRequest().getToPriority();
		
		String tissueQty = req.getRequest().getTissueQty()==null?"0":req.getRequest().getTissueQty();
		String teaQty = req.getRequest().getTeaQty()==null?"0":req.getRequest().getTeaQty();
		String riceQty = req.getRequest().getRiceQty()==null?"0":req.getRequest().getRiceQty();
		String useType = req.getRequest().getUseType()==null?"0":req.getRequest().getUseType();
        int  maxGuestNum= req.getRequest().getMaxGuestNum();
		
		String sql = null;
		sql = this.getDINNERNO_SQL(req);

		List<Map<String, Object>> getQData_check = this.doQueryData(sql,null);		

		if(getQData_check==null || getQData_check.isEmpty())
		{			
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("找不到桌台信息！");			
		}
		else
		{
			
			if(Check.Null(toPriority)){ // 非上下移动修改，需判断区域有没有变动
				
				if(Check.Null(toDinnerGroup)){
					//更新单头
					UptBean ub1 = new UptBean("DCP_DINNERTABLE");			
					ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
					ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
					ub1.addCondition("DINNERNO", new DataValue(req.getRequest().getDinnerNo(), Types.VARCHAR));
					
					ub1.addUpdateValue("DINNERGROUP",new DataValue(req.getRequest().getDinnerGroup(), Types.VARCHAR)); 
					ub1.addUpdateValue("GUESTNUM",new DataValue(req.getRequest().getGuestNum(), Types.VARCHAR)); 
					ub1.addUpdateValue("PRIORITY",new DataValue(req.getRequest().getPriority(), Types.VARCHAR)); 
					ub1.addUpdateValue("TEAPLUNO",new DataValue(req.getRequest().getTeaPluNo(), Types.VARCHAR)); 
					ub1.addUpdateValue("STATUS",new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
					ub1.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR));
					ub1.addUpdateValue("DINNERCLASS",new DataValue(req.getRequest().getDinnerClass(), Types.VARCHAR));
					
					ub1.addUpdateValue("TISSUEPLUNO",new DataValue(req.getRequest().getTissuePluNo(), Types.VARCHAR)); 
					ub1.addUpdateValue("TISSUEQTY",new DataValue(tissueQty, Types.VARCHAR));
					ub1.addUpdateValue("RICEPLUNO",new DataValue(req.getRequest().getRicePluNo(), Types.VARCHAR));
					ub1.addUpdateValue("RICEQTY",new DataValue(riceQty, Types.VARCHAR)); 
					ub1.addUpdateValue("TEAQTY",new DataValue(teaQty, Types.VARCHAR));
					ub1.addUpdateValue("USETYPE",new DataValue(useType, Types.VARCHAR));
                    ub1.addUpdateValue("MAXGUESTNUM",new DataValue(maxGuestNum, Types.INTEGER));
					
					this.addProcessData(new DataProcessBean(ub1));

                    //DCP_DINNERTABLE_MEAL默认菜品
                    List<DCP_DinnerUpdateReq.levelElmMeal> mealList=req.getRequest().getMealList();
                    if (mealList != null)
                    {
						//DCP_DINNERTABLE_MEAL
						DelBean db1_meal = new DelBean("DCP_DINNERTABLE_MEAL");
						db1_meal.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						db1_meal.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
						db1_meal.addCondition("DINNERNO", new DataValue(req.getRequest().getDinnerNo(), Types.VARCHAR));

						this.addProcessData(new DataProcessBean(db1_meal)); //

						String[] columnsMeal =
								{
										"EID",
										"ORGANIZATIONNO",
										"DINNERNO",
										"PLUNO",
										"UNITID",
										"QTY",
										"DEFMODE",
										"LASTMODIOPID",
										"LADTMODITIME"
								};
						for (DCP_DinnerUpdateReq.levelElmMeal Meal : mealList)
						{
							DataValue[] insMealValue1 = new DataValue[]
									{
											new DataValue(req.geteId(), Types.VARCHAR),
											new DataValue(req.getShopId(), Types.VARCHAR),
											new DataValue(req.getRequest().getDinnerNo(), Types.VARCHAR),
											new DataValue(Meal.getPluNo(), Types.VARCHAR),
											new DataValue(Meal.getUnitId(), Types.VARCHAR),
											new DataValue(Meal.getQty(), Types.VARCHAR),
											new DataValue(Meal.getDefMode(), Types.VARCHAR),
											new DataValue(req.getOpNO(), Types.VARCHAR),
											new DataValue(lastmoditime, Types.DATE),
									};

							InsBean ib1_meal = new InsBean("DCP_DINNERTABLE_MEAL", columnsMeal);
							ib1_meal.addValues(insMealValue1);
							this.addProcessData(new DataProcessBean(ib1_meal)); // 新增單頭
						}
                    }

                }else if(!Check.Null(toDinnerGroup) && !dinnerGroup.equals(toDinnerGroup)){
					
					String maxPrioritySql = " SELECT  nvl(MAX(priority) + 1 , '1') AS maxPriority FROM DCP_DINNERTable "
							+ " WHERE EID = '"+eId+"' AND organizationNo = '"+organizationNO+"'  AND dinnerGroup = '"+toDinnerGroup+"'  ";

					List<Map<String, Object>> priorityDatas = this.doQueryData(maxPrioritySql,null);	
					String maxPriority = "1";
					
					if(priorityDatas!=null && priorityDatas.size() > 0){
						maxPriority = priorityDatas.get(0).get("MAXPRIORITY").toString();
					}
					
					//严谨一点： 如果目标区域不为空，且目标区域和原始区域不相等
					// 需要修改两个地方： 原始区域中大于该桌台优先级的全部优先级降低1； 桌台区域改为目标区域，且优先级改为目标区域最大优先级
					UptBean ub2 = new UptBean("DCP_DINNERTABLE");	
					ub2.addUpdateValue("PRIORITY ", new DataValue(1, Types.INTEGER,DataExpression.SubSelf)); 
					
					ub2.addCondition("PRIORITY", new DataValue(priority, Types.VARCHAR,DataExpression.Greater));
					ub2.addCondition("DINNERGROUP", new DataValue(dinnerGroup, Types.VARCHAR));//原始区域
					ub2.addCondition("EID", new DataValue( eId, Types.VARCHAR));
					ub2.addCondition("ORGANIZATIONNO", new DataValue( organizationNO, Types.VARCHAR));
					
					
					//更新单头
					UptBean ub1 = new UptBean("DCP_DINNERTABLE");			
					ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
					ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
					ub1.addCondition("DINNERNO", new DataValue(req.getRequest().getDinnerNo(), Types.VARCHAR));
					
					ub1.addUpdateValue("DINNERGROUP",new DataValue(toDinnerGroup, Types.VARCHAR)); //目标区域
					ub1.addUpdateValue("PRIORITY",new DataValue(maxPriority, Types.VARCHAR));//优先级改为目标区域最大优先级
					
					ub1.addUpdateValue("GUESTNUM",new DataValue(req.getRequest().getGuestNum(), Types.VARCHAR)); 
					ub1.addUpdateValue("TEAPLUNO",new DataValue(req.getRequest().getTeaPluNo(), Types.VARCHAR)); 
					ub1.addUpdateValue("STATUS",new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
					ub1.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR));
					ub1.addUpdateValue("DINNERCLASS",new DataValue(req.getRequest().getDinnerClass(), Types.VARCHAR));
					ub1.addUpdateValue("TISSUEPLUNO",new DataValue(req.getRequest().getTissuePluNo(), Types.VARCHAR)); 
					ub1.addUpdateValue("TISSUEQTY",new DataValue(tissueQty, Types.VARCHAR));
					ub1.addUpdateValue("RICEPLUNO",new DataValue(req.getRequest().getRicePluNo(), Types.VARCHAR));
					ub1.addUpdateValue("RICEQTY",new DataValue(riceQty, Types.VARCHAR)); 
					ub1.addUpdateValue("TEAQTY",new DataValue(teaQty, Types.VARCHAR));
					ub1.addUpdateValue("USETYPE",new DataValue(useType, Types.VARCHAR));
                    ub1.addUpdateValue("MAXGUESTNUM",new DataValue(maxGuestNum, Types.INTEGER));
					
					this.addProcessData(new DataProcessBean(ub2));	
					this.addProcessData(new DataProcessBean(ub1));

					//DCP_DINNERTABLE_MEAL默认菜品
					List<DCP_DinnerUpdateReq.levelElmMeal> mealList=req.getRequest().getMealList();
					if (mealList != null)
					{
						//DCP_DINNERTABLE_MEAL
						DelBean db1_meal = new DelBean("DCP_DINNERTABLE_MEAL");
						db1_meal.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						db1_meal.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
						db1_meal.addCondition("DINNERNO", new DataValue(req.getRequest().getDinnerNo(), Types.VARCHAR));

						this.addProcessData(new DataProcessBean(db1_meal)); //

						String[] columnsMeal =
								{
										"EID",
										"ORGANIZATIONNO",
										"DINNERNO",
										"PLUNO",
										"UNITID",
										"QTY",
										"DEFMODE",
										"LASTMODIOPID",
										"LADTMODITIME"
								};
						for (DCP_DinnerUpdateReq.levelElmMeal Meal : mealList)
						{
							DataValue[] insMealValue1 = new DataValue[]
									{
											new DataValue(req.geteId(), Types.VARCHAR),
											new DataValue(req.getShopId(), Types.VARCHAR),
											new DataValue(req.getRequest().getDinnerNo(), Types.VARCHAR),
											new DataValue(Meal.getPluNo(), Types.VARCHAR),
											new DataValue(Meal.getUnitId(), Types.VARCHAR),
											new DataValue(Meal.getQty(), Types.VARCHAR),
											new DataValue(Meal.getDefMode(), Types.VARCHAR),
											new DataValue(req.getOpNO(), Types.VARCHAR),
											new DataValue(lastmoditime, Types.DATE),
									};

							InsBean ib1_meal = new InsBean("DCP_DINNERTABLE_MEAL", columnsMeal);
							ib1_meal.addValues(insMealValue1);
							this.addProcessData(new DataProcessBean(ib1_meal)); // 新增單頭
						}
					}
				}
				
				
				
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
					
					UptBean ub2 = new UptBean("DCP_DINNERTABLE");		
					
					if(toPt > pt){ //相当于 下移
						String[] priorityList = new String[toPt-pt];
						for(int i=0; i < toPt-pt ; i++) {
							priorityList[i] = String.valueOf(pt +i+ 1);
						}
						String str1 = StringUtils.join(priorityList,",");
						
						ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
						ub2.addUpdateValue("PRIORITY ", new DataValue(1, Types.INTEGER,DataExpression.SubSelf)); 
						
					}
					else{   //相当于上移 
						
						String[] priorityList = new String[pt-toPt];
						for(int i=0; i < pt- toPt ; i++) {
							priorityList[i] = String.valueOf(toPt + i);
						}
						String str1 = StringUtils.join(priorityList,",");
						
						ub2.addCondition("PRIORITY", new DataValue(str1, Types.ARRAY,DataExpression.IN));//Greater表示大于
						ub2.addUpdateValue("PRIORITY ", new DataValue(1, Types.INTEGER,DataExpression.UpdateSelf)); 
					}
					
					ub2.addCondition("DINNERGROUP", new DataValue(dinnerGroup, Types.VARCHAR));
					ub2.addCondition("EID", new DataValue( eId, Types.VARCHAR));
					ub2.addCondition("ORGANIZATIONNO", new DataValue( organizationNO, Types.VARCHAR));
					
					
					UptBean ub3 = new UptBean("DCP_DINNERTABLE");
					ub3.addCondition("DINNERNO", new DataValue(dinnerNO, Types.VARCHAR));
					ub3.addCondition("DINNERGROUP", new DataValue(dinnerGroup, Types.VARCHAR));
					ub3.addCondition("EID", new DataValue( eId, Types.VARCHAR));
					ub3.addCondition("ORGANIZATIONNO", new DataValue( organizationNO, Types.VARCHAR));
					ub3.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR));
					
					ub3.addUpdateValue("PRIORITY ", new DataValue(toPriority,Types.VARCHAR)); 
					
					this.addProcessData(new DataProcessBean(ub2));	
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
	protected List<InsBean> prepareInsertData(DCP_DinnerUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DinnerUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DinnerUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DinnerUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String DINNERNO =req.getRequest().getDinnerNo();
		String DINNERGROUP =req.getRequest().getDinnerGroup();
		String GUESTNUM =req.getRequest().getGuestNum();
		String PRIORITY =req.getRequest().getPriority();
		String status =req.getRequest().getStatus();		
		
		
		if (Check.Null(DINNERNO)) 
		{
			errMsg.append("桌台号不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(DINNERGROUP)) 
		{
			errMsg.append("所属区域不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(PRIORITY)) 
		{
			errMsg.append("优先级排序不可为空值, ");
			isFail = true;
		} 
		 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DinnerUpdateReq> getRequestType()
	{
		return new TypeToken<DCP_DinnerUpdateReq>(){};
	}

	@Override
	protected DCP_DinnerUpdateRes getResponseType() 
	{
		return new DCP_DinnerUpdateRes();
	}

	
	protected String getDINNERNO_SQL(DCP_DinnerUpdateReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_DINNERTABLE WHERE EID='"+req.geteId()+"' AND organizationNo ='"+req.getShopId()+"' AND DINNERNO='"+req.getRequest().getDinnerNo()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	
	
}
