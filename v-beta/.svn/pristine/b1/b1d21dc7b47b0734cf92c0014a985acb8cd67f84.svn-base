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
import com.dsc.spos.json.cust.req.DCP_ShopGroupDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ShopGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_ShopGroupDelete extends SPosAdvanceService<DCP_ShopGroupDeleteReq, DCP_ShopGroupDeleteRes> {

	@Override
	protected void processDUID(DCP_ShopGroupDeleteReq req, DCP_ShopGroupDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try {

			if (req.getRequest().getShopChildren() != null && !req.getRequest().getShopChildren().isEmpty()) {
				// 删除店群的门店
				DelBean del = new DelBean("DCP_ShopGroup");
				del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				del.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
				del.addCondition("SHOPID", new DataValue(req.getRequest().getShopChildren(), Types.VARCHAR));
				del.addCondition("ShopGroupType", new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR));

				//考虑到兼容
				int childPriority = 1;
				if( !StringUtils.isBlank(req.getRequest().getPriority()) ){
					childPriority = Integer.parseInt(req.getRequest().getChildPriority().toString());

					UptBean ub1 = new UptBean("DCP_ShopGroup");			
					ub1.addCondition("PRIORITY",new DataValue( childPriority , Types.VARCHAR,DataExpression.GreaterEQ));
					ub1.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
					ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ub1.addCondition("ShopGroupType", new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR));
					ub1.addUpdateValue("PRIORITY",new DataValue(1,Types.INTEGER,DataExpression.SubSelf)); 
					this.pData.add(new DataProcessBean(ub1));

				}


				this.pData.add(new DataProcessBean(del));

			} else {
				// 删除店群主表
				DelBean del = new DelBean("DCP_ShopGHead");
				del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				del.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
				del.addCondition("ShopGroupType", new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR));
				this.pData.add(new DataProcessBean(del));

				// 删除店群的门店
				DelBean del2 = new DelBean("DCP_ShopGroup");
				del2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				del2.addCondition("ShopGroupNo", new DataValue(req.getRequest().getShopGroupNo(), Types.VARCHAR));
				del.addCondition("ShopGroupType", new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR));
				this.pData.add(new DataProcessBean(del2));

				int priority = 1;
				if( !StringUtils.isBlank(req.getRequest().getPriority() )){

					priority = Integer.parseInt(req.getRequest().getPriority().toString());

					UptBean ub2 = new UptBean("DCP_ShopGHead");			
					ub2.addCondition("PRIORITY",new DataValue( priority , Types.VARCHAR,DataExpression.GreaterEQ));
					ub2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
					ub2.addCondition("ShopGroupType", new DataValue(req.getRequest().getShopGroupType(), Types.VARCHAR));
					ub2.addUpdateValue("PRIORITY",new DataValue(1,Types.INTEGER,DataExpression.SubSelf)); 
					this.pData.add(new DataProcessBean(ub2));

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
	protected List<InsBean> prepareInsertData(DCP_ShopGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ShopGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ShopGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ShopGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ShopGroupDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ShopGroupDeleteReq>() {
		};
	}

	@Override
	protected DCP_ShopGroupDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ShopGroupDeleteRes();
	}

}
