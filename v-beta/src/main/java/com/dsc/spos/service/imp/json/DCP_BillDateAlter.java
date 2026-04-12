package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BillDateAlterReq;
import com.dsc.spos.json.cust.res.DCP_BillDateAlterRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 结算日期
 * @date   2024-09-19
 * @author 01029 
 */
public class DCP_BillDateAlter extends SPosAdvanceService<DCP_BillDateAlterReq, DCP_BillDateAlterRes> {

    @Override
    protected void processDUID(DCP_BillDateAlterReq req, DCP_BillDateAlterRes res) throws Exception {
        
        try {
        	String oprType = req.getRequest().getOprType();//I insert U update

            if(oprType.equals("I")){
                processOnCreate(req,res);
            }else if(oprType.equals("U")) {
                processOnUpdate(req,res);
            }else {
            	res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败:  操作类型 传值异常  ");
                return;
			}
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());

        }
    }
    
    private void processOnCreate(DCP_BillDateAlterReq req, DCP_BillDateAlterRes res) throws Exception{
    	String billDateNo = req.getRequest().getBillDateNo();
        String eId = req.geteId();

        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        sql = this.isRepeat(eId, billDateNo);
        List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
        if (mDatas.isEmpty()) {
        	ColumnDataValue columns = new ColumnDataValue();
        	String[] columns1 =null;
        	DataValue[] insValue1 =null;
            // 新增多语言表
            List<DCP_BillDateAlterReq.NameLang> nameLang = req.getRequest().getName_Lang();
            String sName =null; 
            if (nameLang != null && nameLang.size() > 0) {
                for (DCP_BillDateAlterReq.NameLang par : nameLang) {
                    String langType = par.getLangType();
                    sName = par.getName();
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
            		columns.Add("BILLDATENO", billDateNo, Types.VARCHAR);
            		columns.Add("LANG_TYPE", langType, Types.VARCHAR);
            		columns.Add("NAME", sName, Types.VARCHAR);               	              

            		 columns1 = columns.Columns.toArray(new String[0]);
            		 insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    // 添加多语言信息
                    InsBean ib2 = new InsBean("DCP_BILLDATE_LANG", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
            columns.Columns.clear();
            columns.DataValues.clear();
            columns.Add("EID", eId, Types.VARCHAR);
    		columns.Add("BILLDATENO", billDateNo, Types.VARCHAR);
    		columns.Add("NAME", sName, Types.VARCHAR);
    		columns.Add("BILLTYPE", req.getRequest().getBillDateType(), Types.VARCHAR);
    		columns.Add("FDATE", req.getRequest().getFDate(), Types.VARCHAR);
    		columns.Add("ADDMONTHS", String.valueOf(req.getRequest().getAddMonths()), Types.VARCHAR);   
    		columns.Add("ADDDAYS", String.valueOf(req.getRequest().getAddDays()), Types.VARCHAR); 
    
    		columns.Add("STATUS", Integer.valueOf(req.getRequest().getStatus()), Types.INTEGER); 
    		columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR); 
    		columns.Add("CREATEDEPTID", req.getDepartmentNo() ,Types.VARCHAR); 
    		columns.Add("CREATETIME", lastmoditime, Types.DATE);
            columns.Add("LASTMODIOPID", req.getEmployeeNo(), Types.VARCHAR);
            columns.Add("LASTMODITIME", lastmoditime, Types.DATE);

            columns1 = columns.Columns.toArray(new String[0]);
   		    insValue1 = columns.DataValues.toArray(new DataValue[0]);
            InsBean ib1 = new InsBean("DCP_BILLDATE", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 结算日期条件信息：" + billDateNo + "已存在 ");
            return;
        }
    }
    
    private void processOnUpdate(DCP_BillDateAlterReq req, DCP_BillDateAlterRes res) throws Exception {
    	String billDateNo = req.getRequest().getBillDateNo();
        String eId = req.geteId();

        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        DelBean db2 = new DelBean("DCP_BILLDATE_LANG");
        db2.addCondition("BILLDATENO", new DataValue(billDateNo, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));
		String sql = null;

		ColumnDataValue columns = new ColumnDataValue();
		String[] columns1 = null;
		DataValue[] insValue1 = null;
		// 新增多语言表
		List<DCP_BillDateAlterReq.NameLang> nameLang = req.getRequest().getName_Lang();
		String sName =null; 
		if (nameLang != null && nameLang.size() > 0) {
			for (DCP_BillDateAlterReq.NameLang par : nameLang) {
				String langType = par.getLangType();
				sName = par.getName();
				columns.Columns.clear();
				columns.DataValues.clear();
				columns.Add("EID", eId, Types.VARCHAR);
				columns.Add("BILLDATENO", billDateNo, Types.VARCHAR);
				columns.Add("LANG_TYPE", langType, Types.VARCHAR);
				columns.Add("NAME", sName, Types.VARCHAR); 

				columns1 = columns.Columns.toArray(new String[0]);
				insValue1 = columns.DataValues.toArray(new DataValue[0]);
				// 添加多语言信息
				InsBean ib2 = new InsBean("DCP_BILLDATE_LANG", columns1);
				ib2.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib2));
			}

			UptBean ub1 = null;
			ub1 = new UptBean("DCP_BILLDATE");
			// add Value
			ub1.addUpdateValue("BILLTYPE", new DataValue(req.getRequest().getBillDateType(), Types.VARCHAR));
			ub1.addUpdateValue("FDATE", new DataValue(req.getRequest().getFDate(), Types.VARCHAR));
			ub1.addUpdateValue("ADDMONTHS",
					new DataValue(String.valueOf(req.getRequest().getAddMonths()), Types.VARCHAR));
			ub1.addUpdateValue("ADDDAYS", new DataValue(String.valueOf(req.getRequest().getAddDays()), Types.VARCHAR));

			ub1.addUpdateValue("STATUS", new DataValue(Integer.valueOf(req.getRequest().getStatus()), Types.INTEGER));
			ub1.addUpdateValue("NAME", new DataValue(sName, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("BILLDATENO", new DataValue(billDateNo, Types.VARCHAR));
             
            this.addProcessData(new DataProcessBean(ub1)); // update

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 结算日期条件信息：" + billDateNo + "已存在 ");
            return;
        }
    	
    }
 

    @Override
    protected List<InsBean> prepareInsertData(DCP_BillDateAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BillDateAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BillDateAlterReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_BillDateAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        String billDate = req.getRequest().getBillDateNo();
        List<DCP_BillDateAlterReq.NameLang> nameLang = req.getRequest().getName_Lang();

        if (nameLang == null || nameLang.isEmpty()  ) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
 

        if (Check.Null(billDate)) {
            errMsg.append("结算日条件编号不能为空值 ");
            isFail = true;
        }

        for (DCP_BillDateAlterReq.NameLang par : nameLang) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BillDateAlterReq> getRequestType() {
        return new TypeToken<DCP_BillDateAlterReq>() {
        };
    }

    @Override
    protected DCP_BillDateAlterRes getResponseType() {
        return new DCP_BillDateAlterRes();
    }


    /**
     * 判断 信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_BillDate WHERE EID='%s' AND BILLDATENO='%s'  ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
