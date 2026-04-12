package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_QualityCheckCreateReq;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;
 
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 质量检验单新增(批量)
 * @date   2024-10-23
 * @author 01029 
 */
public class DCP_QualityCheckCreate extends SPosAdvanceService<DCP_QualityCheckCreateReq, JsonBasicRes> {

    @Override
    protected void processDUID(DCP_QualityCheckCreateReq req, JsonBasicRes res) throws Exception {
        
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
    
    private String getProcessTaskNO( String eId,String orgId) throws Exception  {
        String sql = null;
        String templateNo = null;
        String shopId =  "001";
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','"+orgId+"SHQC') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        }
        else
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取单号失败！");
        }
        return templateNo;
    }

    
    private void processOnCreate(DCP_QualityCheckCreateReq req, JsonBasicRes res) throws Exception{
    
        String eId = req.geteId(); 
        String orgId = req.getOrganizationNO();   
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        String billNo = null;
        //sql = this.isRepeat(eId, billNo);
        //List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
        List<Map<String, Object>> mDatas =null;
        
        if (SUtil.EmptyList(mDatas)) {
        	ColumnDataValue columns = new ColumnDataValue();
        	String[] columns1 =null;
        	DataValue[] insValue1 =null;
            
            columns.Columns.clear();
            columns.DataValues.clear();
             

         // 质量检验单新增(批量)
            List<DCP_QualityCheckCreateReq.Detail1> detailLists = req.getRequest().getDataList();

            if (!SUtil.EmptyList(detailLists)) {
                for (DCP_QualityCheckCreateReq.Detail1 par : detailLists) {
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
                    billNo = getProcessTaskNO(eId, orgId);
                    columns.Add("QCBILLNO", billNo, Types.VARCHAR);
                    columns.Add("ORGANIZATIONNO", orgId, Types.VARCHAR);
                    columns.Add("QCTYPE", par.getQcType(), Types.VARCHAR);
                    columns.Add("BDATE", par.getBDate(), Types.VARCHAR);
                    columns.Add("EMPLOYEEID", par.getEmployeeID(), Types.VARCHAR);
                    columns.Add("DEPARTID", par.getDepartID(), Types.VARCHAR);
                    columns.Add("INSPECT_DATE", par.getInspectDate(), Types.VARCHAR);
                    columns.Add("INSPECT_TIME", par.getInspectTime(), Types.VARCHAR);
                    columns.Add("SOURCEBILLNO", par.getSourceBillNo(), Types.VARCHAR);
                    columns.Add("OITEM", par.getOItem(), Types.VARCHAR);
                    //columns.Add("OITEM2", par.getOItem2(), Types.VARCHAR);
                    columns.Add("PLUNO", par.getPluNo(), Types.VARCHAR);
                    columns.Add("FEATURENO", par.getFeatureNo(), Types.VARCHAR);
                    columns.Add("OUNIT", par.getOUnit(), Types.VARCHAR);
                    columns.Add("OQTY", par.getOQty(), Types.VARCHAR);
                    columns.Add("SUPPLIER", par.getSupplier(), Types.VARCHAR);
                    columns.Add("BATCHNO", par.getBatchNo(), Types.VARCHAR);
                    columns.Add("PROD_DATE", par.getProdDate(), Types.DATE);
                    columns.Add("EXP_DATE", par.getExpDate(), Types.DATE);
                                                           
                    columns.Add("DELIVERQTY", par.getOQty(), Types.VARCHAR);
                    columns.Add("TESTQTY", par.getOQty(), Types.VARCHAR);
                    columns.Add("TESTUNIT", par.getOUnit(), Types.VARCHAR);
                    columns.Add("PASSQTY", par.getOQty(), Types.VARCHAR);
                    columns.Add("REJECTQTY", "0", Types.VARCHAR);
                    columns.Add("STATUS", "0", Types.VARCHAR);
                   
                    columns.Add("INSPECTOR", req.getEmployeeNo(), Types.VARCHAR); 
            		columns.Add("OWNOPID", req.getEmployeeNo(), Types.VARCHAR); 
            		columns.Add("OWNDEPTID", req.getDepartmentNo() ,Types.VARCHAR); 
            		
            		columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR); 
            		columns.Add("CREATEDEPTID", req.getDepartmentNo() ,Types.VARCHAR); 
            		columns.Add("CREATETIME", lastmoditime, Types.DATE); 
            	 
            		 columns1 = columns.Columns.toArray(new String[0]);
            		 insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    //质量检验单新增(批量)
                    InsBean ib2 = new InsBean("DCP_QUALITYCHECK", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
            
                
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 单号信息：" + billNo + "已存在 ");
            return;
        }
    }
     
 

    @Override
    protected List<InsBean> prepareInsertData(DCP_QualityCheckCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_QualityCheckCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_QualityCheckCreateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_QualityCheckCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
     
        List<DCP_QualityCheckCreateReq.Detail1> detailLists = req.getRequest().getDataList();
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
    protected TypeToken<DCP_QualityCheckCreateReq> getRequestType() {
        return new TypeToken<DCP_QualityCheckCreateReq>() {
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
        sql = " SELECT * FROM DCP_BIZPARTNER WHERE EID='%s'   AND BIZPARTNERNO='%s' ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
