package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ModularWorkCloseReq;
import com.dsc.spos.json.cust.res.DCP_ModularWorkCloseRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
public class DCP_ModularWorkClose extends SPosAdvanceService<DCP_ModularWorkCloseReq,DCP_ModularWorkCloseRes> {

	@Override
	protected boolean isVerifyFail(DCP_ModularWorkCloseReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(Check.isEmpty(req.getModularNo()))
		{
			errMsg.append("菜单编码[modularNo]不可空值 ");
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	@Override
	protected boolean AuthCheck(DCP_ModularWorkCloseReq req) throws Exception {
	// TODO Auto-generated method stub
	return true;
	}
	@Override
	protected TypeToken<DCP_ModularWorkCloseReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_ModularWorkCloseReq>(){};
	}

	@Override
	protected DCP_ModularWorkCloseRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_ModularWorkCloseRes();
	}

	@Override
	protected void  processDUID(DCP_ModularWorkCloseReq req,DCP_ModularWorkCloseRes res) throws Exception {
	// TODO Auto-generated method stub	
	    String modularNo=req.getModularNo();
	    String token = req.getToken();
        String sql = "select * from DCP_MODULAR A WHERE A.EID='"+req.geteId()+"' and a.MODULARNO='"+modularNo+"' ";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&getQData.isEmpty()==false)
        {
        	String rfuncNo=getQData.get(0).get("RFUNCNO").toString();
        	if(Check.isNotEmpty(rfuncNo))
        	{
        		//sql="SELECT * FROM DCP_REGEDISTMODU A INNER JOIN DCP_REGEDISTMODULAR B ON A.RMODULARNO=B.RMODULARNO AND B.RFUNCNO='"+rfuncNo+"' ";
				sql="SELECT * FROM  DCP_REGEDISTMODULAR B where B.RFUNCNO='"+rfuncNo+"' ";
				getQData = this.doQueryData(sql,null);
        		if (getQData!=null&&getQData.isEmpty()==false)
        		{
        			String rmodularNo=getQData.get(0).get("RMODULARNO").toString();
					String rFuncno=getQData.get(0).get("RFUNCNO").toString();
					String rtypeInfo=getQData.get(0).get("RTYPEINFO").toString();
//        			String modularAuth=getQData.get(0).get("MODULAR_AUTH").toString();
//        			String modularRtypeInfo=getQData.get(0).get("MODULAR_RTYPEINFO").toString();
        			if("4".equals(rtypeInfo))//模块授权 and 并发数
        			{
        				DelBean db1 = new DelBean("DCP_MODULAR_WORKING_AUTH");		
        				db1.addCondition("MODULARNO", new DataValue(modularNo, Types.VARCHAR));
        				db1.addCondition("TOKEN", new DataValue(token, Types.VARCHAR));
        				this.addProcessData(new DataProcessBean(db1));
        				sql="SELECT * FROM DCP_MODULAR_WORKING_AUTH WHERE TOKEN='"+token+"' AND RFUNCNO='"+rFuncno+"' ";
        				List<Map<String,Object>> getQDataAuth=this.doQueryData(sql, null);
        				if (getQDataAuth==null || getQDataAuth.isEmpty())
        				{
        					sql="SELECT * FROM DCP_REGEDISTMODULAR WHERE RFUNCNO='"+rFuncno+"' ";
        					List<Map<String ,Object>> getQDataRegistModular=this.doQueryData(sql, null);
        					if (getQDataRegistModular!=null && !getQDataRegistModular.isEmpty())
        					{
        						for(Map<String,Object> map:getQDataRegistModular)
        						{
        							UptBean up1 = new UptBean("PLATFORM_CREGISTERDETAIL"); 
        							up1.addUpdateValue("TOKEN", new DataValue("", Types.VARCHAR));
        							up1.addUpdateValue("EID", new DataValue("", Types.VARCHAR));
									up1.addUpdateValue("OPNO", new DataValue("", Types.VARCHAR));
									up1.addUpdateValue("OPNAME", new DataValue("", Types.VARCHAR));
									up1.addUpdateValue("LASTMODITIME", new DataValue("", Types.VARCHAR));
        							up1.addCondition("TOKEN", new DataValue(token, Types.VARCHAR));				
        							up1.addCondition("PRODUCTTYPE", new DataValue(map.get("RFUNCNO").toString(), Types.VARCHAR));				
        							this.addProcessData(new DataProcessBean(up1));
        						}
        					}
        				}
                		this.doExecuteDataToDB();
						res.setServiceStatus("000");
        			}
        		}
        	}
        }
	}
	@Override
	protected List<InsBean> prepareInsertData(DCP_ModularWorkCloseReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ModularWorkCloseReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ModularWorkCloseReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}
}
