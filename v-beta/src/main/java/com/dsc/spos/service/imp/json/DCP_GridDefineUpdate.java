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
import com.dsc.spos.json.cust.req.DCP_GridDefineUpdateReq;
import com.dsc.spos.json.cust.req.DCP_GridDefineUpdateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_GridDefineUpdateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_GridDefineUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 表格自定义修改
 * @author yuanyy 2019-02-16
 *
 */
public class DCP_GridDefineUpdate extends SPosAdvanceService<DCP_GridDefineUpdateReq, DCP_GridDefineUpdateRes> {

	@Override
	protected void processDUID(DCP_GridDefineUpdateReq req, DCP_GridDefineUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			
			String lastModiOPId = req.getOpNO();
			String lastModiOPName = req.getOpName();
			
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime =  matter.format(dt);
			
			String EID = req.geteId();
			String modularNo = req.getRequest().getModularNo();
			String status = req.getRequest().getStatus();
			List<level2Elm> gridsDatas = req.getRequest().getGrids();
			
			DelBean db1 = new DelBean("DCP_GRIDDEFINE");
			db1.addCondition("EID", new DataValue(EID, Types.VARCHAR));
			db1.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			

			
			String[] columns_hms ={"EID","MODULARNO","GRIDNO","FIELDNAME","ISSHOW","ISMOVE","PRIORITY",
					"LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","STATUS"};
			
			if(gridsDatas != null && gridsDatas.size() > 0){
		
				for (level2Elm level2Elm : gridsDatas) {
					String gridNo = level2Elm.getGridNo();
					List<level3Elm> fieldsDatas = level2Elm.getFields();

					for (level3Elm level3Elm : fieldsDatas) {
						String fieldName = level3Elm.getFieldName();
						String isShow = level3Elm.getIsShow();
						String isMove = level3Elm.getIsMove();
						String priority = level3Elm.getPriority();
						
						DataValue[] insValue_hms = new DataValue[] 
						{
							new DataValue(EID, Types.VARCHAR),
							new DataValue(modularNo, Types.VARCHAR), 
							new DataValue(gridNo, Types.VARCHAR),
							new DataValue(fieldName, Types.VARCHAR),
							new DataValue(isShow, Types.VARCHAR),
							new DataValue(isMove, Types.VARCHAR),
							new DataValue(priority, Types.VARCHAR),
							new DataValue(lastModiOPId, Types.VARCHAR),
							new DataValue(lastModiOPName, Types.VARCHAR),
							new DataValue(createTime, Types.DATE),
							new DataValue(status, Types.VARCHAR) 
						};
						
						InsBean ib_hms = new InsBean("DCP_GRIDDEFINE", columns_hms);
						ib_hms.addValues(insValue_hms);
						this.addProcessData(new DataProcessBean(ib_hms));
						
						
					}
					
				}
			}
            this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GridDefineUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GridDefineUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GridDefineUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GridDefineUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GridDefineUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GridDefineUpdateReq>(){};
	}

	@Override
	protected DCP_GridDefineUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GridDefineUpdateRes();
	}

}
