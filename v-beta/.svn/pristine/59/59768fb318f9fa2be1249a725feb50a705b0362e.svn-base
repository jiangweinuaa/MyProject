package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_QualityCheckUpdateReq;
import com.dsc.spos.json.JsonBasicRes;
 
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
 
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 质检单
 * @date   2024-10-23
 * @author 01029 
 */
public class DCP_QualityCheckUpdate extends SPosAdvanceService<DCP_QualityCheckUpdateReq, JsonBasicRes> {

    @Override
    protected void processDUID(DCP_QualityCheckUpdateReq req, JsonBasicRes res) throws Exception {
        
        try {
        	//String oprType = req.getRequest().getOprType();//I insert U update
  
           processOnCreate(req,res);    
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());

        }
    }
    
    private void processOnCreate(DCP_QualityCheckUpdateReq req, JsonBasicRes res) throws Exception{
    
        String eId = req.geteId();        
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        
        List<Map<String, Object>> mDatas =null;
        if (SUtil.EmptyList(mDatas)) {
        	ColumnDataValue columns = new ColumnDataValue();
        	UptBean ub1 = null;
    		 
            List<DCP_QualityCheckUpdateReq.Detail1> detailLists = req.getRequest().getDataList();
            StringBuffer errMsg = new StringBuffer("");
            if (!SUtil.EmptyList(detailLists)) {
                for (DCP_QualityCheckUpdateReq.Detail1 par : detailLists) {
                    columns.Columns.clear();
                    columns.DataValues.clear();
                
            		ub1 = new UptBean("DCP_QUALITYCHECK");
                    String billNo = par.getQcBillNo();
                    sql = this.isRepeat(eId, billNo);
                    List<Map<String, Object>> mDatasQry = this.doQueryData(sql, null);
                    if (SUtil.EmptyList(mDatasQry)){
                    	String status = (String) mDatasQry.get(0).get("STATUS");
                    	if (!"0".equals(status)){
                    		errMsg.append("单号:"+billNo+"状态不是 检验中 不可更新！");
                    		continue;
                    	}
                    }
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        			ub1.addCondition("QCBILLNO", new DataValue(billNo, Types.VARCHAR));
                    ub1.addUpdateValue("PASSQTY", new DataValue(par.getPassQty(),Types.VARCHAR));
                    ub1.addUpdateValue("REJECTQTY", new DataValue(par.getRejectQty(),Types.VARCHAR));
                    ub1.addUpdateValue("RESULT", new DataValue(par.getResult(),Types.VARCHAR));
                    ub1.addUpdateValue("MEMO", new DataValue(par.getMemo(),Types.VARCHAR));
                    ub1.addUpdateValue("INSPECTOR", new DataValue(par.getInspector(),Types.VARCHAR));
                    ub1.addUpdateValue("INSPECT_DATE", new DataValue(par.getInspectDate(),Types.VARCHAR));
                    ub1.addUpdateValue("INSPECT_TIME", new DataValue(par.getInspectTime(),Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
        			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
                    this.addProcessData(new DataProcessBean(ub1)); // update
                }
            }
                           
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription(errMsg.toString()+"服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败"  );
            return;
        }
    }
     
 

    @Override
    protected List<InsBean> prepareInsertData(DCP_QualityCheckUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_QualityCheckUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_QualityCheckUpdateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_QualityCheckUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
         
        List<DCP_QualityCheckUpdateReq.Detail1> detailLists = req.getRequest().getDataList();
        if (detailLists == null || detailLists.size() <= 0) {
        	errMsg.append("单据明细缺失！ ");
            isFail = true;
        }
         
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_QualityCheckUpdateReq> getRequestType() {
        return new TypeToken<DCP_QualityCheckUpdateReq>() {
        };
    }

    @Override
    protected JsonBasicRes getResponseType() {
        return new JsonBasicRes();
    }


    /**
     * 判断 信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_QUALITYCHECK WHERE EID='%s'   AND QCBILLNO='%s' ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
