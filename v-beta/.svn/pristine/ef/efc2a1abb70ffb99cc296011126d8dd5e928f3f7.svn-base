package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InvoiceTypeCreateReq;
import com.dsc.spos.json.cust.res.DCP_InvoiceTypeCreateRes;
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
 * 发票类型
 * @date   2024-09-13
 * @author 01029 
 */
public class DCP_InvoiceTypeCreate extends SPosAdvanceService<DCP_InvoiceTypeCreateReq, DCP_InvoiceTypeCreateRes> {

    @Override
    protected void processDUID(DCP_InvoiceTypeCreateReq req, DCP_InvoiceTypeCreateRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        try {
            String taxArea = req.getRequest().getTaxArea();
            String invoiceCode = req.getRequest().getInvoiceCode();
            String eId = req.geteId();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            sql = this.isRepeat(eId, taxArea,invoiceCode);
            List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
            if (mDatas.isEmpty()) {
            	ColumnDataValue columns = new ColumnDataValue();
            	String[] columns1 =null;
            	DataValue[] insValue1 =null;
                // 新增多语言表
                List<DCP_InvoiceTypeCreateReq.InvoiceNameLang> nameLang = req.getRequest().getInvoiceName_lang();
                String sName  =null;
                if (nameLang != null && nameLang.size() > 0) {
                    for (DCP_InvoiceTypeCreateReq.InvoiceNameLang par : nameLang) {
                        String langType = par.getLangType();
                        sName = par.getName();
                        columns.Columns.clear();
                        columns.DataValues.clear();
                        columns.Add("EID", eId, Types.VARCHAR);
                		columns.Add("TAXAREA", taxArea, Types.VARCHAR);
                		columns.Add("INVOICECODE", invoiceCode, Types.VARCHAR);
                		columns.Add("LANG_TYPE", langType, Types.VARCHAR);
                		columns.Add("INVOICE_NAME", sName, Types.VARCHAR);               	              

                		 columns1 = columns.Columns.toArray(new String[0]);
                		 insValue1 = columns.DataValues.toArray(new DataValue[0]);
                        // 添加多语言信息
                        InsBean ib2 = new InsBean("DCP_INVOICETYPE_LANG", columns1);
                        ib2.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }
                columns.Columns.clear();
                columns.DataValues.clear();
                columns.Add("EID", eId, Types.VARCHAR);
        		columns.Add("TAXAREA", taxArea, Types.VARCHAR);
        		columns.Add("INVOICECODE", invoiceCode, Types.VARCHAR);
        		columns.Add("NAME", sName, Types.VARCHAR);
        		columns.Add("INVOICE_TYPE", req.getRequest().getInvoiceType(), Types.VARCHAR);
        		columns.Add("INVOICE_GENRE", req.getRequest().getInvoiceGenre(), Types.VARCHAR);   
        		columns.Add("TAXCALTYPE", req.getRequest().getTaxCalType(), Types.VARCHAR); 
        
        		columns.Add("STATUS", Integer.valueOf(req.getRequest().getStatus()), Types.INTEGER); 
        		columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR); 
        		columns.Add("CREATEDEPTID", req.getDepartmentNo() ,Types.VARCHAR); 
        		columns.Add("CREATETIME", lastmoditime, Types.DATE); 
        		 
                columns1 = columns.Columns.toArray(new String[0]);
       		    insValue1 = columns.DataValues.toArray(new DataValue[0]);
                InsBean ib1 = new InsBean("DCP_INVOICETYPE", columns1);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1)); // 新增

                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            } else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败: 发票信息：" + invoiceCode + "已存在 ");
                return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());

        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InvoiceTypeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InvoiceTypeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InvoiceTypeCreateReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_InvoiceTypeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        String invoiceCode = req.getRequest().getInvoiceCode();
        List<DCP_InvoiceTypeCreateReq.InvoiceNameLang> nameLang = req.getRequest().getInvoiceName_lang();

        if (nameLang == null || nameLang.isEmpty()  ) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
 

        if (Check.Null(invoiceCode)) {
            errMsg.append("发票类型不能为空值 ");
            isFail = true;
        }

        for (DCP_InvoiceTypeCreateReq.InvoiceNameLang par : nameLang) {
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
    protected TypeToken<DCP_InvoiceTypeCreateReq> getRequestType() {
        return new TypeToken<DCP_InvoiceTypeCreateReq>() {
        };
    }

    @Override
    protected DCP_InvoiceTypeCreateRes getResponseType() {
        return new DCP_InvoiceTypeCreateRes();
    }


    /**
     * 判断 信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_INVOICETYPE WHERE EID='%s' AND TAXAREA='%s' AND INVOICECODE='%s' ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
