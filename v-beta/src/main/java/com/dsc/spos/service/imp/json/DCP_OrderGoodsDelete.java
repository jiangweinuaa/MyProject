package com.dsc.spos.service.imp.json;

import java.io.File;
import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderGoodsDelete extends SPosAdvanceService<DCP_OrderGoodsDeleteReq,DCP_OrderGoodsDeleteRes> {

	@Override
	protected void processDUID(DCP_OrderGoodsDeleteReq req, DCP_OrderGoodsDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String belFirm = req.getOrganizationNO();
		
		try 
		{
			List<level1Elm> datas = req.getDatas();
			for (level1Elm par : datas) 
			{	
				String pluNO=par.getPluNo();
				//OC_GOODS
				DelBean db1 = new DelBean("OC_GOODS");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
				if (belFirm != null && belFirm.length() > 0)
				{
					db1.addCondition("BELFIRM", new DataValue(belFirm, Types.VARCHAR));
				}
				
				
				this.addProcessData(new DataProcessBean(db1));

				//OC_GOODS_SPEC
				DelBean db2 = new DelBean("OC_GOODS_SPEC");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
				if (belFirm != null && belFirm.length() > 0)
				{
					db2.addCondition("BELFIRM", new DataValue(belFirm, Types.VARCHAR));
				}
				this.addProcessData(new DataProcessBean(db2));

				//OC_GOODS_ATTR
				DelBean db3 = new DelBean("OC_GOODS_ATTR");
				db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db3.addCondition("PLUNO", new DataValue(pluNO, Types.VARCHAR));
				if (belFirm != null && belFirm.length() > 0)
				{
					db3.addCondition("BELFIRM", new DataValue(belFirm, Types.VARCHAR));
				}
				this.addProcessData(new DataProcessBean(db3));
			}
			this.doExecuteDataToDB();	
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

		//删除\\webapps\\ordergoods
		try
		{
			List<level1Elm> datas = req.getDatas();
			for (level1Elm par : datas) 
			{	
				String fileName = par.getFileName();
				if(!Check.Null(fileName))
				{
					String dirpath= System.getProperty("catalina.home")+"\\webapps\\ordergoods";
					File file =new File(dirpath +"\\" + fileName);    
					if(file.exists())
					{
						file.delete();
					}				
					file=null;
				}	
			}
		}
		catch (Exception ex)
		{
				
		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> datas = req.getDatas();
		for (level1Elm par : datas) 
		{	
			if (Check.Null(par.getPluNo()) ) 
			{
				errMsg.append("商品编号不可为空值, ");
				isFail = true;
			}		
			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		return isFail;				
	}

	@Override
	protected TypeToken<DCP_OrderGoodsDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderGoodsDeleteReq>(){};
	}

	@Override
	protected DCP_OrderGoodsDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderGoodsDeleteRes();
	}

}
