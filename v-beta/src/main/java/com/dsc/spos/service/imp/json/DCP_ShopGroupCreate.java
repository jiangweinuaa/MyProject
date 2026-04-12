package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ShopGroupCreateReq;
import com.dsc.spos.json.cust.res.DCP_ShopGroupCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_ShopGroupCreate extends SPosAdvanceService<DCP_ShopGroupCreateReq, DCP_ShopGroupCreateRes> {

	@Override
	protected void processDUID(DCP_ShopGroupCreateReq req, DCP_ShopGroupCreateRes res) throws Exception {
		// TODO Auto-generated method stub

		//考虑到兼容性
		try {
			int priority = 1;
			if( !StringUtils.isBlank(req.getRequest().getPriority()) ){
				priority = Integer.parseInt(req.getRequest().getPriority().toString());
			}

			// 先delete然后再insert
			DelBean del = new DelBean("DCP_ShopGHead");
			del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			del.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
			this.pData.add(new DataProcessBean(del));
			String[] colname = { "EID", "ShopGroupNo", "ShopGroupName", "STATUS", "ShopGroupType","PRIORITY" };
			DataValue[] insValue = { new DataValue(req.geteId(), Types.VARCHAR),
					new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR),
					new DataValue(req.getRequest().getShopGroupName(), Types.VARCHAR), 
					new DataValue(req.getRequest().getStatus(), Types.VARCHAR),
					new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR),
					new DataValue( priority , Types.VARCHAR)
			};
			InsBean ins = new InsBean("DCP_ShopGHead", colname);
			ins.addValues(insValue);
			this.pData.add(new DataProcessBean(ins));
			if (req.getRequest().getChildren() != null && !req.getRequest().getChildren().isEmpty()) {
				del = new DelBean("DCP_ShopGroup");
				del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				del.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
				del.addCondition("ShopGroupType", new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR));

				this.pData.add(new DataProcessBean(del));

				for (DCP_ShopGroupCreateReq.level1Elm shopinfo : req.getRequest().getChildren()) {

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
							new DataValue(childPriority, Types.VARCHAR)};
					ins = new InsBean("DCP_ShopGroup", colnamelev);
					ins.addValues(insValuelev);
					this.pData.add(new DataProcessBean(ins));
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
	protected List<InsBean> prepareInsertData(DCP_ShopGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ShopGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ShopGroupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ShopGroupCreateReq req) throws Exception {
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
	protected TypeToken<DCP_ShopGroupCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ShopGroupCreateReq>() {
		};
	}

	@Override
	protected DCP_ShopGroupCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ShopGroupCreateRes();
	}

}
