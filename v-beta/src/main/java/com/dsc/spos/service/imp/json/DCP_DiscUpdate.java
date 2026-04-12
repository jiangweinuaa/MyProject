package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DiscUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DiscUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_DiscUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_DiscUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：ParaDefineUpdate
 *   說明：参数定义修改
 * 服务说明：参数定义修改
 * @author Jinzma 
 * @since  2017-03-03
 */
public class DCP_DiscUpdate extends SPosAdvanceService<DCP_DiscUpdateReq, DCP_DiscUpdateRes> 
{

	@Override
	protected void processDUID(DCP_DiscUpdateReq req, DCP_DiscUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		try 
		{
			String eId=req.geteId();
			//删除原有单身
			DelBean db1 = new DelBean("DCP_DISC");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			//删除原有单身
			DelBean db2 = new DelBean("DCP_DISC_SHOP");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));


			//新增新的单身（DCP_DISC）

			if (req.getRequest().getDatas() != null && req.getRequest().getDatas().isEmpty() == false && req.getRequest().getDatas().size() > 0)	
			{
				DataValue[] insValue = null ;
				for (DCP_DiscUpdateReq.level1Elm par : req.getRequest().getDatas()) 
				{
					String keyValue=par.getKeyValue();
					String priority =par.getPriority();

					//新增子单身
					List<level2Elm> jsonDetailDatas = par.getShops();
					for (level2Elm pardetail : jsonDetailDatas) {
						String[] columns1 = {"EID","KEYVALUE","SHOPID","STATUS"};
						String shopId=pardetail.getShopId();		
						insValue = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(keyValue, Types.FLOAT), 
								new DataValue(shopId, Types.VARCHAR),
								new DataValue("100", Types.VARCHAR),								
						};
						InsBean ib1 = new InsBean("DCP_DISC_SHOP", columns1);
						ib1.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib1)); //			
					}

					String[] columns = {"EID","KEYVALUE","PRIORITY","STATUS"};
					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(keyValue, Types.FLOAT),		
							new DataValue(priority, Types.INTEGER),
							new DataValue("100", Types.VARCHAR)
					};
					InsBean ib = new InsBean("DCP_DISC", columns);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib));
				}
			}

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
	protected List<InsBean> prepareInsertData(DCP_DiscUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DiscUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DiscUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DiscUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> jsonDatas = req.getRequest().getDatas();

		for (level1Elm par : jsonDatas){ 
			//keyName必须为数值型
			String keyValue = par.getKeyValue();
			String priority = par.getPriority();
			
			if (Check.Null(keyValue) || !PosPub.isNumericType(keyValue) ) 
			{
				errMsg.append("折扣率不可为空值或非数值, ");
				isFail = true;
			}	
			if (Check.Null(priority) || !PosPub.isNumeric(priority) ) 
			{
				errMsg.append("优先序不可为空值或非数值, ");
				isFail = true;
			}	
			
			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_DiscUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DiscUpdateReq>(){};
	}

	@Override
	protected DCP_DiscUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DiscUpdateRes();
	}




}
