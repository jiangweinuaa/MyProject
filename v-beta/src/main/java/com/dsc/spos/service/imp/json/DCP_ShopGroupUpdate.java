package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_ShopGroupUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ShopGroupUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ShopGroupUpdate extends SPosAdvanceService<DCP_ShopGroupUpdateReq, DCP_ShopGroupUpdateRes> {

	@Override
	protected void processDUID(DCP_ShopGroupUpdateReq req, DCP_ShopGroupUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {

			int toPriority = 1;
			int priority = 1;
			if(!StringUtils.isBlank(req.getRequest().getToPriority()) && !StringUtils.isBlank(req.getRequest().getPriority()) ){
				toPriority = Integer.parseInt(req.getRequest().getToPriority().toString());
				priority = Integer.parseInt(req.getRequest().getPriority().toString());
			}

			// toPriority.equals("") || toPriority == null ||priority.equals(toPriority)
			// !Check.Null(req.getToPriority()) && !Check.Null(req.getPriority()) &&
			if( toPriority != priority){  

				UptBean ub2 = new UptBean("DCP_SHOPGHEAD");		

				if(toPriority > priority){ //相当于 下移
					String[] priorityList = new String[toPriority-priority];
					for(int i=0; i < toPriority-priority ; i++) {
						priorityList[i] = String.valueOf(priority +i+ 1);
					}
					String str1 = StringUtils.join(priorityList,",");

					ub2.addCondition("PRIORITY", new DataValue(str1, Types.VARCHAR,DataExpression.IN));
					ub2.addUpdateValue("PRIORITY ", new DataValue(1, Types.INTEGER,DataExpression.SubSelf)); 

				}
				else{   //相当于上移 

					String[] priorityList = new String[priority-toPriority];
					for(int i=0; i < priority- toPriority ; i++) {
						priorityList[i] = String.valueOf(toPriority + i);
					}
					String str1 = StringUtils.join(priorityList,",");

					ub2.addCondition("PRIORITY", new DataValue(str1, Types.ARRAY,DataExpression.IN));//Greater表示大于
					ub2.addUpdateValue("PRIORITY ", new DataValue(1, Types.INTEGER,DataExpression.UpdateSelf)); 
				}

				ub2.addCondition("EID", new DataValue( req.geteId(), Types.VARCHAR));
				ub2.addCondition("ShopGroupType", new DataValue( req.getRequest().getShopGroupType(), Types.VARCHAR));

				UptBean ub3 = new UptBean("DCP_SHOPGHEAD");
				ub3.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
				ub3.addCondition("EID", new DataValue( req.geteId(), Types.VARCHAR));
				ub3.addCondition("ShopGroupType", new DataValue( req.getRequest().getShopGroupType(), Types.VARCHAR));
				ub3.addUpdateValue("PRIORITY ", new DataValue(toPriority, Types.VARCHAR)); 

				this.pData.add(new DataProcessBean(ub2));
				this.pData.add(new DataProcessBean(ub3));

				//			this.addProcessData(new DataProcessBean(ub2));	
				//			this.addProcessData(new DataProcessBean(ub3));	

			}

			//————————————————————————————————————————————
			else{
				// 先delete然后再insert
				DelBean del = new DelBean("DCP_ShopGHead");
				del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				del.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
				del.addCondition("ShopGroupType", new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR));

				this.pData.add(new DataProcessBean(del));
				String[] colname = { "EID", "ShopGroupNo", "ShopGroupName", "STATUS", "ShopGroupType" ,"priority"};
				DataValue[] insValue = { new DataValue(req.geteId(), Types.VARCHAR),
						new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR),
						new DataValue(req.getRequest().getShopGroupName(), Types.VARCHAR), 
						new DataValue(req.getRequest().getStatus(), Types.VARCHAR),
						new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR),
						new DataValue(priority, Types.VARCHAR)};
				InsBean ins = new InsBean("DCP_ShopGHead", colname);
				ins.addValues(insValue);
				this.pData.add(new DataProcessBean(ins));
				if (req.getRequest().getChildren() != null && !req.getRequest().getChildren().isEmpty()) {

					del = new DelBean("DCP_ShopGroup");
					del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					del.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
					del.addCondition("ShopGroupType", new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR));
					this.pData.add(new DataProcessBean(del));

					for (DCP_ShopGroupUpdateReq.level1Elm shopinfo : req.getRequest().getChildren()) {

						int childPriority = 1;
						if(!StringUtils.isBlank(shopinfo.getPriority()) ){
							childPriority = Integer.parseInt(shopinfo.getPriority().toString());
						}

						String[] colnamelev = { "EID", "ShopGroupNo", "SHOPID", "STATUS", "ShopGroupType","PRIORITY" };
						DataValue[] insValuelev = { new DataValue(req.geteId(), Types.VARCHAR),
								new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR),
								new DataValue(shopinfo.getShopId(), Types.VARCHAR),
								new DataValue(shopinfo.getStatus(), Types.VARCHAR),
								new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR),
								new DataValue(childPriority, Types.VARCHAR)
						};
						ins = new InsBean("DCP_ShopGroup", colnamelev);
						ins.addValues(insValuelev);
						this.pData.add(new DataProcessBean(ins));
					}
				}
			}
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ShopGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ShopGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ShopGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ShopGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		//必传值不为空
		if(req.getRequest()==null){
			errMsg.append("request不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ShopGroupUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ShopGroupUpdateReq>() {
		};
	}

	@Override
	protected DCP_ShopGroupUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ShopGroupUpdateRes();
	}

}
