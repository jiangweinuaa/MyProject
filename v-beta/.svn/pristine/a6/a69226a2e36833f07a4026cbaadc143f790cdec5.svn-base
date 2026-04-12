package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PinPeiGoodsCreateReq;
import com.dsc.spos.json.cust.req.DCP_PinPeiGoodsCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PinPeiGoodsCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;
/**
 * 服务函数：DCP_PinPeiGoodsCreate
 * 服务说明：拼胚商品新增
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiGoodsCreate extends SPosAdvanceService<DCP_PinPeiGoodsCreateReq,DCP_PinPeiGoodsCreateRes>{

	@Override
	protected void processDUID(DCP_PinPeiGoodsCreateReq req, DCP_PinPeiGoodsCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId=req.geteId();
		String createBy = req.getOpNO();
		String createByName = req.getOpName();
		String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String status= "100";  // 100启用,-1禁用
		try 
		{
			List<level1Elm> pluList = req.getRequest().getPluList();
			for (level1Elm par:pluList)
			{
				String pluNo=par.getPluNo();
				//删除资料
				DelBean db = new DelBean("DCP_PINPEI_GOODS");
				db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db));

				//新增资料
				String[] columns = {
						"EID","PLUNO","STATUS","CREATEOPID","CREATEOPNAME","CREATETIME"
				};
				DataValue[]	insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(pluNo, Types.VARCHAR), 
						new DataValue(status, Types.VARCHAR), 
						new DataValue(createBy, Types.VARCHAR), 
						new DataValue(createByName, Types.VARCHAR), 
						new DataValue(createTime, Types.DATE), 
				};
				InsBean ib = new InsBean("DCP_PINPEI_GOODS", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 
			}

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");		

		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PinPeiGoodsCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PinPeiGoodsCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PinPeiGoodsCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PinPeiGoodsCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> pluList = req.getRequest().getPluList();

		if(pluList==null || pluList.isEmpty())
		{
			errMsg.append("商品列表不能为空, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;

	}

	@Override
	protected TypeToken<DCP_PinPeiGoodsCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_PinPeiGoodsCreateReq>(){};
	}

	@Override
	protected DCP_PinPeiGoodsCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_PinPeiGoodsCreateRes();
	}

}
