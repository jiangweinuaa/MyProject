package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PayDateAlterReq;
import com.dsc.spos.json.cust.res.DCP_PayDateAlterRes;
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
 * 收付款条件
 * @date   2024-09-23
 * @author 01029 
 */
public class DCP_PayDateAlter extends SPosAdvanceService<DCP_PayDateAlterReq, DCP_PayDateAlterRes> {

    @Override
    protected void processDUID(DCP_PayDateAlterReq req, DCP_PayDateAlterRes res) throws Exception {
        
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
    
    private void processOnCreate(DCP_PayDateAlterReq req, DCP_PayDateAlterRes res) throws Exception{
    	String PayDateNo = req.getRequest().getPayDateNo();
        String eId = req.geteId();
        String PayDateType = req.getRequest().getPayDateType();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        sql = this.isRepeat(eId, PayDateNo,PayDateType);
        List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
        if (mDatas.isEmpty()) {
        	ColumnDataValue columns = new ColumnDataValue();
        	String[] columns1 =null;
        	DataValue[] insValue1 =null;
            // 新增多语言表
            List<DCP_PayDateAlterReq.NameLang> nameLang = req.getRequest().getName_Lang();
            String sName =null; 
            if (nameLang != null && nameLang.size() > 0) {
                for (DCP_PayDateAlterReq.NameLang par : nameLang) {
                    String langType = par.getLangType();
                    sName = par.getName();
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
            		columns.Add("PayDateNO", PayDateNo, Types.VARCHAR);
            		columns.Add("PAYDATE_TYPE", PayDateType, Types.VARCHAR);
            		columns.Add("LANG_TYPE", langType, Types.VARCHAR);
            		columns.Add("NAME", sName, Types.VARCHAR);               	              

            		 columns1 = columns.Columns.toArray(new String[0]);
            		 insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    // 添加多语言信息
                    InsBean ib2 = new InsBean("DCP_PayDate_LANG", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
            columns.Columns.clear();
            columns.DataValues.clear();
            columns.Add("EID", eId, Types.VARCHAR);
    		columns.Add("PayDateNO", PayDateNo, Types.VARCHAR);
    		columns.Add("PAYDATE_TYPE", PayDateType, Types.VARCHAR);
    		columns.Add("NAME", sName, Types.VARCHAR);
    		columns.Add("PAYDATEBASE", req.getRequest().getPayDateBase(), Types.VARCHAR);
    		columns.Add("PSEASONS", String.valueOf(req.getRequest().getPSeasons()), Types.VARCHAR);  
    		columns.Add("PMONTHS", String.valueOf(req.getRequest().getPMonths()), Types.VARCHAR); 
    		columns.Add("PDAYS", String.valueOf(req.getRequest().getPDays()), Types.VARCHAR); 
    		columns.Add("DUEDATEBASE", String.valueOf(req.getRequest().getDueDateBase()), Types.VARCHAR); 
    		columns.Add("DSEASONS", String.valueOf(req.getRequest().getDSeasons()), Types.VARCHAR); 
    		columns.Add("DMONTHS", String.valueOf(req.getRequest().getDMonths()), Types.VARCHAR); 
    		columns.Add("DDAYS", String.valueOf(req.getRequest().getDDays()), Types.VARCHAR); 
     
    		columns.Add("STATUS", Integer.valueOf(req.getRequest().getStatus()), Types.INTEGER); 
    		columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR); 
    		columns.Add("CREATEDEPTID", req.getDepartmentNo() ,Types.VARCHAR); 
    		columns.Add("CREATETIME", lastmoditime, Types.DATE);
            columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR);
            columns.Add("LASTMODIOPID", req.getDepartmentNo() ,Types.VARCHAR);
            columns.Add("LASTMODITIME", lastmoditime, Types.DATE);

            columns1 = columns.Columns.toArray(new String[0]);
   		    insValue1 = columns.DataValues.toArray(new DataValue[0]);
            InsBean ib1 = new InsBean("DCP_PayDate", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 结算日期条件信息：" + PayDateNo + "已存在 ");
            return;
        }
    }
    
    private void processOnUpdate(DCP_PayDateAlterReq req, DCP_PayDateAlterRes res) throws Exception {
    	String PayDateNo = req.getRequest().getPayDateNo();
        String eId = req.geteId();
        String PayDateType = req.getRequest().getPayDateType();
      
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        DelBean db2 = new DelBean("DCP_PayDate_LANG");
        db2.addCondition("PayDateNO", new DataValue(PayDateNo, Types.VARCHAR));
        db2.addCondition("PAYDATE_TYPE", new DataValue(PayDateType, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));
		String sql = null;

		ColumnDataValue columns = new ColumnDataValue();
		String[] columns1 = null;
		DataValue[] insValue1 = null;
		// 新增多语言表
		List<DCP_PayDateAlterReq.NameLang> nameLang = req.getRequest().getName_Lang();
		String sName =null;
		if (nameLang != null && nameLang.size() > 0) {
			for (DCP_PayDateAlterReq.NameLang par : nameLang) {
				String langType = par.getLangType();
				sName = par.getName();
				columns.Columns.clear();
				columns.DataValues.clear();
				columns.Add("EID", eId, Types.VARCHAR);
				columns.Add("PayDateNO", PayDateNo, Types.VARCHAR);
				columns.Add("PAYDATE_TYPE", PayDateType, Types.VARCHAR);
				columns.Add("LANG_TYPE", langType, Types.VARCHAR);
				columns.Add("NAME", sName, Types.VARCHAR);

				columns1 = columns.Columns.toArray(new String[0]);
				insValue1 = columns.DataValues.toArray(new DataValue[0]);
				// 添加多语言信息
				InsBean ib2 = new InsBean("DCP_PayDate_LANG", columns1);
				ib2.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib2));
			}

			UptBean ub1 = null;
			ub1 = new UptBean("DCP_PayDate");
			// add Value
			ub1.addUpdateValue("PAYDATEBASE", new DataValue(req.getRequest().getPayDateBase(), Types.VARCHAR));
    		ub1.addUpdateValue("PSEASONS", new DataValue(String.valueOf(req.getRequest().getPSeasons()), Types.VARCHAR));  
    		ub1.addUpdateValue("PMONTHS", new DataValue(String.valueOf(req.getRequest().getPMonths()), Types.VARCHAR)); 
    		ub1.addUpdateValue("PDAYS", new DataValue(String.valueOf(req.getRequest().getPDays()), Types.VARCHAR)); 
    		ub1.addUpdateValue("DUEDATEBASE", new DataValue(String.valueOf(req.getRequest().getDueDateBase()), Types.VARCHAR)); 
    		ub1.addUpdateValue("DSEASONS", new DataValue(String.valueOf(req.getRequest().getDSeasons()), Types.VARCHAR)); 
    		ub1.addUpdateValue("DMONTHS", new DataValue(String.valueOf(req.getRequest().getDMonths()), Types.VARCHAR)); 
    		ub1.addUpdateValue("DDAYS", new DataValue(String.valueOf(req.getRequest().getDDays()), Types.VARCHAR));  
			
			ub1.addUpdateValue("STATUS", new DataValue(Integer.valueOf(req.getRequest().getStatus()), Types.INTEGER));
			ub1.addUpdateValue("NAME", new DataValue(sName, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
  
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("PayDateNO", new DataValue(PayDateNo, Types.VARCHAR));
			ub1.addCondition("PAYDATE_TYPE", new DataValue(PayDateType, Types.VARCHAR));
             
            this.addProcessData(new DataProcessBean(ub1)); // update

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 收付款日期条件信息：" + PayDateNo + "已存在 ");
            return;
        }
    	
    }
 

    @Override
    protected List<InsBean> prepareInsertData(DCP_PayDateAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PayDateAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PayDateAlterReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_PayDateAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        String PayDate = req.getRequest().getPayDateNo();
        List<DCP_PayDateAlterReq.NameLang> nameLang = req.getRequest().getName_Lang();

        if (nameLang == null || nameLang.isEmpty()  ) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
 

        if (Check.Null(PayDate)) {
            errMsg.append("收付款条件编号不能为空值 ");
            isFail = true;
        }

        for (DCP_PayDateAlterReq.NameLang par : nameLang) {
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
    protected TypeToken<DCP_PayDateAlterReq> getRequestType() {
        return new TypeToken<DCP_PayDateAlterReq>() {
        };
    }

    @Override
    protected DCP_PayDateAlterRes getResponseType() {
        return new DCP_PayDateAlterRes();
    }


    /**
     * 判断 信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_PayDate WHERE EID='%s' AND PayDateNO='%s'  AND PAYDATE_TYPE='%s' ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
