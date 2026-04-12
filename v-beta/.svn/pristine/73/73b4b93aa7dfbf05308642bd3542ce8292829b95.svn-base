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

public class DCP_InvoiceTypeUpdate extends SPosAdvanceService<DCP_InvoiceTypeCreateReq, DCP_InvoiceTypeCreateRes> {

    @Override
    protected void processDUID(DCP_InvoiceTypeCreateReq req, DCP_InvoiceTypeCreateRes res) throws Exception {
        // TODO Auto-generated method stub
        try {
        	String taxArea = req.getRequest().getTaxArea();
            String invoiceCode = req.getRequest().getInvoiceCode();
            String eId = req.geteId();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            List<DCP_InvoiceTypeCreateReq.InvoiceNameLang> nameLang = req.getRequest().getInvoiceName_lang();
         
            DelBean db2 = new DelBean("DCP_InvoiceType_LANG");
            db2.addCondition("INVOICECODE", new DataValue(invoiceCode, Types.VARCHAR));
            db2.addCondition("TAXAREA", new DataValue(taxArea, Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            ColumnDataValue columns = new ColumnDataValue();
        	String[] columns1 =null;
        	DataValue[] insValue1 =null;
        	String sName = null;
            if (nameLang != null && nameLang.size() > 0) {
                for (DCP_InvoiceTypeCreateReq.InvoiceNameLang par : nameLang) {
                    int insColCt = 0;
                    // 获取
                    sName = par.getName();
            
                    String langType = par.getLangType();
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

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_INVOICETYPE");
            //add Value
            ub1.addUpdateValue("INVOICE_TYPE", new DataValue(req.getRequest().getInvoiceType() , Types.VARCHAR));
            ub1.addUpdateValue("INVOICE_GENRE", new DataValue(req.getRequest().getInvoiceGenre(), Types.VARCHAR));   
            ub1.addUpdateValue("TAXCALTYPE", new DataValue(req.getRequest().getTaxCalType(), Types.VARCHAR)); 
            ub1.addUpdateValue("STATUS", new DataValue( Integer.valueOf(req.getRequest().getStatus()), Types.VARCHAR));
            ub1.addUpdateValue("NAME", new DataValue(sName, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
            //condition
            ub1.addCondition("INVOICECODE", new DataValue(invoiceCode, Types.VARCHAR));
            ub1.addCondition("TAXAREA", new DataValue(taxArea, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InvoiceTypeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InvoiceTypeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InvoiceTypeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_InvoiceTypeCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        String InvoiceTypeNo = req.getRequest().getInvoiceCode();
        List<DCP_InvoiceTypeCreateReq.InvoiceNameLang> nameLang = req.getRequest().getInvoiceName_lang();
     
        if (nameLang == null) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_InvoiceTypeCreateReq.InvoiceNameLang par : nameLang) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

      

        if (Check.Null(InvoiceTypeNo)) {
            errMsg.append("发票类型不能为空值 ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_InvoiceTypeCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_InvoiceTypeCreateReq>() {
        };
    }

    @Override
    protected DCP_InvoiceTypeCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_InvoiceTypeCreateRes();
    }
}
