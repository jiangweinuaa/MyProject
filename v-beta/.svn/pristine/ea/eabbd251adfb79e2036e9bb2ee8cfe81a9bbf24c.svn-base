package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SupLicenseApplyCreateReq;
import com.dsc.spos.json.cust.req.DCP_PayDateAlterReq;
import com.dsc.spos.json.cust.res.DCP_SupLicenseApplyRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.SUtil;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 交易对象
 * @date   2024-10-23
 * @author 01029 
 */
public class DCP_SupLicenseApplyUpdate extends SPosAdvanceService<DCP_SupLicenseApplyCreateReq, DCP_SupLicenseApplyRes> {

    @Override
    protected void processDUID(DCP_SupLicenseApplyCreateReq req, DCP_SupLicenseApplyRes res) throws Exception {
        
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
    
    private void processOnCreate(DCP_SupLicenseApplyCreateReq req, DCP_SupLicenseApplyRes res) throws Exception{
    
        String eId = req.geteId();
        String billNo = req.getRequest().getBillNo_ID();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        sql = this.isRepeat(eId, billNo);
      
        List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
        if (!SUtil.EmptyList(mDatas)) {
        	ColumnDataValue columns = new ColumnDataValue();
        	String[] columns1 =null;
        	DataValue[] insValue1 =null;
        	UptBean ub1 = null;
    		ub1 = new UptBean("DCP_SUPLICENSECHANGE");
        	ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
    		
    		ub1.addUpdateValue("OPTYPE", new DataValue(req.getRequest().getOprType(),Types.VARCHAR));
    		ub1.addUpdateValue("ORGANIZATIONNO", new DataValue(req.getRequest().getOrgNo(),Types.VARCHAR));
    		ub1.addUpdateValue("BDATE", new DataValue(req.getRequest().getBDate(),Types.DATE));
    		//ub1.addUpdateValue("BILLNO_ID", new DataValue(req.getRequest().getBillNo_ID(),Types.VARCHAR));
    		ub1.addUpdateValue("EMPLOYEEID", new DataValue(req.getRequest().getEmployeeID(),Types.VARCHAR));
    		ub1.addUpdateValue("DEPARTID", new DataValue(req.getRequest().getDepartID(),Types.VARCHAR));
    		ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(),Types.VARCHAR));

    		ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
     
         		    	    		
			this.addProcessData(new DataProcessBean(ub1));

         // 交易对象主数据档_组织范围
            List<DCP_SupLicenseApplyCreateReq.Detail1> detailLists = req.getRequest().getDetail();
            DelBean db2 = new DelBean("DCP_SUPLICENSECHANGE_DETAIL");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
            //db2.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrgNo(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));
            if (!SUtil.EmptyList(detailLists)) {
                for (DCP_SupLicenseApplyCreateReq.Detail1 par : detailLists) {
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
                    columns.Add("BILLNO", billNo, Types.VARCHAR);
                    columns.Add("ITEM", par.getItem(), Types.VARCHAR);
                    columns.Add("SUPPLIER", par.getSupplierNo(), Types.VARCHAR);
                    columns.Add("LICENSETYPE", par.getLicenseType(), Types.VARCHAR);
                    columns.Add("LICENSENO", par.getLicenseNo(), Types.VARCHAR);
                    columns.Add("BEGINDATE", DateFormatUtils.getDateTime(par.getBeginDate()), Types.DATE);
                    columns.Add("ENDDATE", DateFormatUtils.getDateTime(par.getEndDate()), Types.DATE);
                    columns.Add("LICENSEIMG", par.getLicenseImg(), Types.VARCHAR);
                    //columns.Add("STATUS", par.getStatus(), Types.VARCHAR);


            		 columns1 = columns.Columns.toArray(new String[0]);
            		 insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    // 交易对象主数据档_组织范围
                    InsBean ib2 = new InsBean("DCP_SUPLICENSECHANGE_DETAIL", columns1);
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
            res.setServiceDescription("服务执行失败: 单号：" + billNo + "不存在 ");
            return;
        }
    }
     
 

    @Override
    protected List<InsBean> prepareInsertData(DCP_SupLicenseApplyCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SupLicenseApplyCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SupLicenseApplyCreateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_SupLicenseApplyCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String sql = " SELECT * FROM DCP_SUPLICENSECHANGE_DETAIL WHERE SUPPLIER='%s'   AND LICENSETYPE='%s' AND LICENSENO='%s'";
        
        // 必传值不为空
        String billNo = req.getRequest().getBillNo_ID();
  
        if (Check.Null(billNo)) {
            errMsg.append("交易对象编号不能为空值 ");
            isFail = true;
        }
        List<DCP_SupLicenseApplyCreateReq.Detail1> detailLists = req.getRequest().getDetail();
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
    protected TypeToken<DCP_SupLicenseApplyCreateReq> getRequestType() {
        return new TypeToken<DCP_SupLicenseApplyCreateReq>() {
        };
    }

    @Override
    protected DCP_SupLicenseApplyRes getResponseType() {
        return new DCP_SupLicenseApplyRes();
    }


    /**
     * 判断 信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_SUPLICENSECHANGE WHERE EID='%s'   AND BILLNO='%s' ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
