package com.dsc.spos.service;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SPosAdvanceService<REQ extends JsonBasicReq, RES extends JsonBasicRes> extends SPosBasicService<REQ, RES> {

    Logger logger = LogManager.getLogger(SPosAdvanceService.class);

    public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();

    @Override
    protected RES processJson(REQ req) throws Exception {
        RES res = this.getResponse();

        try {
            this.pData.clear(); //清空之前残留

            this.processDUID(req, res);

        } catch (Exception e) {
            this.pData.clear(); //清空异常残留

            StringWriter errors = new StringWriter();
            PrintWriter pw = new PrintWriter(errors);
            e.printStackTrace(pw);

            pw.flush();
            pw.close();

            errors.flush();
            errors.close();

            logger.error("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "******processDUID方法执行异常：" + e.getMessage() + "\r\n" + errors.toString() + "******\r\n");

            pw = null;
            errors = null;

            //清理
            res = null;

            throw e;
        }

        //this.doExecuteDataToDB(); //將資料寫到 DB
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(REQ req) throws Exception {
        return null;
    }


    protected abstract void processDUID(REQ req, RES res) throws Exception;

    protected abstract List<InsBean> prepareInsertData(REQ req) throws Exception;

    protected abstract List<UptBean> prepareUpdateData(REQ req) throws Exception;

    protected abstract List<DelBean> prepareDeleteData(REQ req) throws Exception;

    /**
     * 取得 response.
     *
     * @return
     * @throws Exception
     */
    protected RES getResponse() throws Exception {
        RES res = super.getResponse();
        res.setServiceStatus(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E202.getCodeType());
        res.setServiceDescription(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E202.toString());
        return res;
    }

    /**
     * 新增要處理的資料(先加入的, 先處理)
     *
     * @param row
     */
    protected final void addProcessData(DataProcessBean row) {
        this.pData.add(row);
    }

    /**
     * into data to db
     *
     * @throws Exception
     */
    protected void doExecuteDataToDB() throws Exception {
        if (this.pData.size() == 0) {
            return;
        }
        this.dao.useTransactionProcessData(this.pData);
        this.pData.clear(); //清空
    }

    /**
     * 執行新增
     *
     * @param req
     * @return
     * @throws Exception
     */
    protected void doInsert(REQ req) throws Exception {
        List<InsBean> data = this.prepareInsertData(req);
//		this.doInsert(data);
        for (InsBean ib : data) {
            for (DataValue[] row : ib.getValues()) {
                if (ib.getColumns().length != row.length) {
                    throw new Exception("insert的栏位数与资料栏位数不一致!");
                }
            }
            this.addProcessData(new DataProcessBean(ib));
        }
    }

    /**
     * 更新資料
     *
     * @param req
     * @throws Exception
     */
    protected void doUpdate(REQ req) throws Exception {
        List<UptBean> data = this.prepareUpdateData(req);
        for (UptBean ub : data) {
//			this.dao.update(ub.getTableName(), ub.getUpdateValues(), ub.getConditions());
            this.addProcessData(new DataProcessBean(ub));
        }
    }


    /**
     * 刪除資料
     *
     * @param req
     * @throws Exception
     */
    protected void doDelete(REQ req) throws Exception {
        List<DelBean> data = this.prepareDeleteData(req);
        for (DelBean db : data) {
//			this.dao.doDelete(db.getTableName(), db.getConditions());
            this.addProcessData(new DataProcessBean(db));
        }
    }

    protected int getInt(String num) throws Exception {
        if (num == null) {
            return 0;
        } else if (num.trim().length() == 0) {
            return 0;
        } else {
            return Integer.parseInt(num);
        }
    }

    protected double getDouble(String num) throws Exception {
        if (num == null) {
            return 0;
        } else if (num.trim().length() == 0) {
            return 0;
        } else {
            return Double.parseDouble(num);
        }
    }


    /**
     * exec StoredProcedure
     *
     * @throws Exception
     */

    protected void doExecuteStoredProcedure(String procedure, Map<Integer, Object> inputParameter, Map<Integer, Integer> outParameter) throws Exception {
        if (Check.Null(procedure)) {
            return;
        }
        if (inputParameter.size() <= 0 || inputParameter.isEmpty()) {
            return;
        }
        if (outParameter.size() <= 0 || outParameter.isEmpty()) {
            return;
        }

        this.dao.storedProcedure(procedure, inputParameter, outParameter);
        procedure = ""; //清空
        inputParameter.clear(); //清空
        outParameter.clear(); //清空

    }

    /**
     * 单号获取
     *
     * @author 01029
     * * @return 单号
     * 测试用例1： select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','"+orgno+"SHQC') TEMPLATENO FROM dual --DCP_QualityCheckCreate
     * 测试用例2   select F_DCP_GETBILLNO('99','001','DCP_PROCESSTASK') FROM dual
     * orgno+"SHQC 可以拼在一起 作为 orgId
     */
    protected String getProcessTaskNO(String eId, String shopId, String orgId) throws Exception {
        String sql = null;
        String templateNo = null;
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('" + eId + "','" + shopId + "','" + orgId + "') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取单号失败！");
        }
        return templateNo;
    }

    protected String getOrderNO(JsonBasicReq req, String preFix) throws Exception {
        String billCode = "";
        String sql = null;
        String templateNo = null;
        String shopId = req.getShopId();
        String eId = req.geteId();

        StringBuffer sqlbufOrg = new StringBuffer("select billcode FROM dcp_org where eid='" + req.geteId() + "' and organizationno='" + req.getOrganizationNO() + "' ");
        sql = sqlbufOrg.toString();
        List<Map<String, Object>> getQDataOrg = this.doQueryData(sql, null);
        if (getQDataOrg != null && getQDataOrg.isEmpty() == false) {
            billCode = (String) getQDataOrg.get(0).get("BILLCODE");
        }

        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('" + eId + "','" + shopId + "','" + billCode + "-" + preFix + "') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }

    protected String getOrderNO(JsonBasicReq req, String organizationNo, String preFix) throws Exception {
        String billCode = "";
        String sql = null;
        String templateNo = null;
//        String shopId = req.getShopId();
        String shopId = organizationNo;
        String eId = req.geteId();

        StringBuffer sqlbufOrg = new StringBuffer("select billcode FROM dcp_org where eid='" + req.geteId() + "' and organizationno='" + organizationNo + "' ");
        sql = sqlbufOrg.toString();
        List<Map<String, Object>> getQDataOrg = this.doQueryData(sql, null);
        if (getQDataOrg != null && getQDataOrg.isEmpty() == false) {
            billCode = (String) getQDataOrg.get(0).get("BILLCODE");
        }

        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('" + eId + "','" + shopId + "','" + billCode + "-" + preFix + "') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }

    protected String getNormalNO(JsonBasicReq req, String preFix) throws Exception {
        String sql = null;
        String templateNo = null;
        String shopId = req.getShopId();
        String eId = req.geteId();


        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('" + eId + "','" + shopId + "','" + preFix + "') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }

    protected String getNormalNOWithDate(JsonBasicReq req, String orgNo, String preFix, String date) throws Exception {
        String sql = null;
        String templateNo = null;
        String shopId = orgNo;
        String eId = req.geteId();

        date = DateFormatUtils.getPlainDate(date);

        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO2('" + eId + "','" + shopId + "','" + preFix + "','" + date + "') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }

    protected String getNormalNOWithDate(JsonBasicReq req, String preFix, String date) throws Exception {
        return getNormalNOWithDate(req, req.getOrganizationNO(), preFix, date);
    }

    protected String getOrderNOWithDate(JsonBasicReq req, String orgNo, String preFix, String date) throws Exception {
        if (Check.Null(date)) {
            return getOrderNO(req, preFix);
        }

        String billCode = "";
        String sql = null;
        String templateNo = null;
        String shopId = orgNo;
        String eId = req.geteId();

        StringBuffer sqlbufOrg = new StringBuffer("select billcode FROM dcp_org where eid='" + req.geteId() + "' and organizationno='" + orgNo + "' ");
        sql = sqlbufOrg.toString();
        List<Map<String, Object>> getQDataOrg = this.doQueryData(sql, null);
        if (getQDataOrg != null && getQDataOrg.isEmpty() == false) {
            billCode = (String) getQDataOrg.get(0).get("BILLCODE");
        }

        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO2('" + eId + "','" + shopId + "','" + billCode + "-" + preFix + "','" + date + "') TEMPLATENO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {
            templateNo = (String) getQData.get(0).get("TEMPLATENO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return templateNo;
    }

    protected String getOrderNOWithDate(JsonBasicReq req, String preFix, String date) throws Exception {

        return getOrderNOWithDate(req, req.getOrganizationNO(), preFix, date);
    }

}
