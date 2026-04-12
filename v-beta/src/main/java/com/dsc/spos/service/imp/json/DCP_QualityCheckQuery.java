package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_QualityCheckQueryReq;
import com.dsc.spos.json.cust.res.DCP_QualityCheckQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.SUtil;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DCP_QualityCheckQuery extends SPosBasicService<DCP_QualityCheckQueryReq, DCP_QualityCheckQueryRes> {

    public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();

    @Override
    protected boolean isVerifyFail(DCP_QualityCheckQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_QualityCheckQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_QualityCheckQueryReq>() {
        };
    }

    @Override
    protected DCP_QualityCheckQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_QualityCheckQueryRes();
    }

    @Override
    protected DCP_QualityCheckQueryRes processJson(DCP_QualityCheckQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_QualityCheckQueryRes res = null;
        res = this.getResponse();
        String sql = null;

        sql = this.getCountSql(req);
        String[] condCountValues = {}; //查詢條件
        List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
        int totalRecords;                                //总笔数
        int totalPages;                                    //总页数
        if (getReasonCount != null && getReasonCount.isEmpty() == false) {
            Map<String, Object> total = getReasonCount.get(0);
            String num = total.get("NUM").toString();
            totalRecords = Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
        } else {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        sql = this.getQuerySql(req);

        String[] conditionValues1 = {}; //查詢條件

        List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);

        List<Map<String, Object>> getQLangData = null;
        res.setDatas(new ArrayList<>());
        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {


            for (Map<String, Object> oneData : getQDataDetail) {
                DCP_QualityCheckQueryRes.Datas oneLv1 = res.new Datas();

                oneLv1.setStatus(oneData.get("STATUS").toString());
                oneLv1.setSourceBillNo(oneData.get("SOURCEBILLNO").toString());
                oneLv1.setOItem(oneData.get("OITEM").toString());
                oneLv1.setQcBillNo(oneData.get("QCBILLNO").toString());
                //oneLv1.setOItem2(oneData.get("OITEM2").toString());
                oneLv1.setPluNo(oneData.get("PLUNO").toString());
                oneLv1.setPluName(oneData.get("PLU_NAME").toString());
                oneLv1.setFeatureNo(oneData.get("FEATURENO").toString());
                oneLv1.setFeatureName(oneData.get("FEATURENAME").toString());
                oneLv1.setBatchNo(oneData.get("BATCHNO").toString());
                oneLv1.setProdDate(oneData.get("PROD_DATE").toString());
                oneLv1.setExpDate(oneData.get("EXP_DATE").toString());
                oneLv1.setTestUnit(oneData.get("TESTUNIT").toString());
                oneLv1.setTestUnitName(oneData.get("TESTUNITNAME").toString());
                oneLv1.setDeliverQty(oneData.get("DELIVERQTY").toString());
                oneLv1.setPassQty(oneData.get("PASSQTY").toString());
                oneLv1.setRejectQty(oneData.get("REJECTQTY").toString());
                oneLv1.setResult(oneData.get("RESULT").toString());
                oneLv1.setMemo(oneData.get("MEMO").toString());
                oneLv1.setInspector(oneData.get("INSPECTOR").toString());
                oneLv1.setInspectorName(oneData.get("INSPECTORNAME").toString());
                oneLv1.setInspectDate(oneData.get("INSPECT_DATE").toString());
                oneLv1.setInspectTime(oneData.get("INSPECT_TIME").toString());

                oneLv1.setOwnOpId(oneData.get("OWNOPID").toString());
                oneLv1.setOwnOpName(oneData.get("OWNOPNAME").toString());
                oneLv1.setOwnDeptId(oneData.get("OWNDEPTID").toString());
                oneLv1.setOwnDeptName(oneData.get("OWNDEPTNAME").toString());
                oneLv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
                oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
                oneLv1.setCreateOpId(oneData.get("CREATEOPID").toString());
                oneLv1.setCreateOpName(oneData.get("CREATEOPNAME").toString());
                oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
                oneLv1.setLastModiOpId(oneData.get("LASTMODIOPID").toString());
                oneLv1.setLastModiOpName(oneData.get("LASTMODIOPNAME").toString());
                oneLv1.setLastModiTime(oneData.get("LASTMODITIME").toString());
                oneLv1.setConfirmBy(oneData.get("CONFIRMBY").toString());
                oneLv1.setConfirmByName(oneData.get("CONFIRMBYNAME").toString());
                oneLv1.setConfirmDateTime(oneData.get("CONFIRMTIME").toString());
                oneLv1.setCancelBy(oneData.get("CANCELBY").toString());
                oneLv1.setCancelByName(oneData.get("CANCELBYNAME").toString());
                oneLv1.setCancelTime(oneData.get("CANCELTIME").toString());


                res.getDatas().add(oneLv1);
                oneLv1 = null;
            }
        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_QualityCheckQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        String langType = req.getLangType();
        if (langType == null || langType.isEmpty()) {
            langType = "zh_CN";
        }

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String key = null;
        String qcType = null;
        String dateType = null;
        String status = null;
        String beginDate = null;
        String endDate = null;
        String eId = req.geteId();

        if (req.getRequest() != null) {
            key = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
            qcType = req.getRequest().getQcType();
            dateType = req.getRequest().getDateType();
            beginDate = req.getRequest().getBeginDate();
            endDate = req.getRequest().getEndDate();
//            if (StringUtils.isEmpty(beginDate))
//                beginDate = "20241001";
//            if (StringUtils.isEmpty(endDate))
//                endDate = "20641001";
        }

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM ("
                + " SELECT ROWNUM AS rn ,  a.*  "
                + " ,F.PLU_NAME,H.FEATURENAME, d1.UNAME as TESTUNITNAME, p1.NAME as INSPECTORNAME "
                + " ,p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName "
                + " ,p2.NAME CONFIRMBYNAME,p3.NAME CANCELBYNAME, p4.NAME OWNOPNAME,d2.DEPARTNAME OWNDEPTNAME "
                + " FROM DCP_QUALITYCHECK  a "
                + " LEFT JOIN DCP_GOODS E ON E.PLUNO=a.PLUNO "
                + " LEFT JOIN DCP_GOODS_LANG F ON F.PLUNO=a.PLUNO AND F.LANG_TYPE='" + langType + "' "
                + " LEFT JOIN DCP_GOODS_FEATURE_LANG H ON H.PLUNO=a.PLUNO AND H.FEATURENO=a.FEATURENO AND H.LANG_TYPE='" + langType + "' "
                + "	LEFT JOIN DCP_UNIT_LANG d1 ON d1.UNIT=a.TESTUNIT AND a.EID=d1.EID and d1.LANG_TYPE='" + langType + "' "
                + " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
                + " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
                + " left join DCP_EMPLOYEE  p1 on a.eid=p1.eid and p1.EMPLOYEENO=a.INSPECTOR "
                + " left join DCP_EMPLOYEE  p2 on a.eid=p2.eid and p2.EMPLOYEENO=a.CONFIRMBY "
                + " left join DCP_EMPLOYEE  p3 on a.eid=p3.eid and p3.EMPLOYEENO=a.CANCELBY "
                + " left join DCP_EMPLOYEE  p4 on a.eid=p4.eid and p4.EMPLOYEENO=a.OWNOPID "
                + "  left join DCP_DEPARTMENT_LANG d on a.eid=d.eid and a.CREATEDEPTID=d.DEPARTNO and d.lang_type='" + langType + "' "
                + "  left join DCP_DEPARTMENT_LANG d2 on a.eid=d2.eid and a.OWNDEPTID=d2.DEPARTNO and d2.lang_type='" + langType + "' "
                + " WHERE a.EID = '" + eId + "' ");

//        入参keyTxt需支持：单号/来源单号/供应商/品号/品名/条码/批号模糊搜索

        if (StringUtils.isNotEmpty(key)) {
            sqlbuf.append(" and ( a.QCBILLNO  like  ").append(SUtil.RetLikeStr(key));
            sqlbuf.append(" or  a.SOURCEBILLNO  like  ").append(SUtil.RetLikeStr(key));
            sqlbuf.append(" or  a.SUPPLIER  like  ").append(SUtil.RetLikeStr(key));
            sqlbuf.append(" or  a.PLUNO  like  ").append(SUtil.RetLikeStr(key));
            sqlbuf.append(" or  F.PLU_NAME  like  ").append(SUtil.RetLikeStr(key));
            sqlbuf.append(" or  E.MAINBARCODE  like  ").append(SUtil.RetLikeStr(key));
            sqlbuf.append(" or  a.BATCHNO  like  ").append(SUtil.RetLikeStr(key));

            sqlbuf.append(" ) ");
        }
        if (qcType != null && !qcType.isEmpty())
            sqlbuf.append(" and a.QCTYPE = '" + qcType + "' ");
        if ("receiptdate".equals(dateType)) {
            if (StringUtils.isNotEmpty(beginDate)) {
                sqlbuf.append(" and a.INSPECT_DATE >= '" + DateFormatUtils.getPlainDate(beginDate) + "' ");
            }
            if (StringUtils.isNotEmpty(endDate)) {
                sqlbuf.append(" and a.INSPECT_DATE <= '" + DateFormatUtils.getPlainDate(endDate) + "' ");
            }
        } else if ("bdate".equals(dateType)) {
            if (StringUtils.isNotEmpty(beginDate)) {
                sqlbuf.append(" and a.BDATE >= '" + DateFormatUtils.getPlainDate(beginDate) + "' ");
            }
            if (StringUtils.isNotEmpty(endDate)) {
                sqlbuf.append(" and a.BDATE <= '" + DateFormatUtils.getPlainDate(endDate) + "' ");
            }
        }

        //sqlbuf.append(" WHERE CREATETIME between to_date('"+beginDate +"','yyyymmdd') and to_date('"+endDate+" 23:59:59','yyyymmdd hh24:mi:ss') ");
        if (status != null && !status.isEmpty())
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append(" ) DBL  WHERE rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" order by QCBILLNO ");


        sql = sqlbuf.toString();
        //logger.info(sql);
        return sql;
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
     * 计算总数
     *
     * @param req
     * @return
     * @throws Exception
     */
    protected String getCountSql(DCP_QualityCheckQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String key = null;
        String qcType = null;
        String dateType = null;
        String status = null;
        String beginDate = null;
        String endDate = null;
        String eId = req.geteId();

        if (req.getRequest() != null) {
            key = req.getRequest().getKeyTxt();
            status = req.getRequest().getStatus();
            qcType = req.getRequest().getQcType();
            dateType = req.getRequest().getDateType();
            beginDate = req.getRequest().getBeginDate();
            endDate = req.getRequest().getEndDate();
            if (StringUtils.isEmpty(beginDate))
                beginDate = "20241001";
            if (StringUtils.isEmpty(endDate))
                endDate = "20641001";
        }

        StringBuffer sqlbuf = new StringBuffer("");
        String sql = "";

        sqlbuf.append("select num from( select count(* ) AS num    "
                + " FROM DCP_QUALITYCHECK  a"
                + " where a.EID='" + eId + "'  ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and ( a.QCBILLNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.SUPPLIER  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    a.PLUNO  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" ) ");
        }
        if (qcType != null && qcType.length() > 0)
            sqlbuf.append(" and a.QCTYPE = '" + qcType + "' ");
        if ("receiptdate".equals(dateType))
            sqlbuf.append(SUtil.RetDateCon8("a.INSPECT_DATE", beginDate, endDate));
        if ("bdate".equals(dateType))
            sqlbuf.append(SUtil.RetDateCon8("a.BDATE", beginDate, endDate));

        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append(" ) ");

        sql = sqlbuf.toString();
        return sql;
    }


}
