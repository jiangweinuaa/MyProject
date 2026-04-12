package com.dsc.spos.service.imp.json;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_QualityCheckPendingListQueryReq;
import com.dsc.spos.json.cust.res.DCP_QualityCheckPendingListQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.SUtil;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DCP_QualityCheckPendingListQuery extends SPosBasicService<DCP_QualityCheckPendingListQueryReq, DCP_QualityCheckPendingListQueryRes> {

    public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();

    @Override
    protected boolean isVerifyFail(DCP_QualityCheckPendingListQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_QualityCheckPendingListQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_QualityCheckPendingListQueryReq>() {
        };
    }

    @Override
    protected DCP_QualityCheckPendingListQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_QualityCheckPendingListQueryRes();
    }

    @Override
    protected DCP_QualityCheckPendingListQueryRes processJson(DCP_QualityCheckPendingListQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_QualityCheckPendingListQueryRes res = null;
        res = this.getResponse();
        String sql = null;
        try {

            sql = this.getCountSql(req);
            String[] condCountValues = {}; //查詢條件
            //List<Map<String, Object>> getReasonCount = this.doQueryData(sql, condCountValues);
            sql = this.getQuerySql(req);

            String[] conditionValues1 = {}; //查詢條件

            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, conditionValues1);
            int totalRecords;                                //总笔数
            int totalPages;                                    //总页数
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                Map<String, Object> total = getQDataDetail.get(0);
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


            if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                res.setDatas(new ArrayList<DCP_QualityCheckPendingListQueryRes.DataDetail>());

                for (Map<String, Object> oneData : getQDataDetail) {
                    DCP_QualityCheckPendingListQueryRes.DataDetail oneLv1 = res.new DataDetail();

                    oneLv1.setSouceBillNo(oneData.get("BILLNO").toString());
                    oneLv1.setOItem(oneData.get("ITEM").toString());
                    oneLv1.setOItem2("");
                    oneLv1.setListImage(oneData.get("LISTIMAGE").toString());
                    oneLv1.setPluNo(oneData.get("PLUNO").toString());
                    oneLv1.setPluName(oneData.get("PLUNAME").toString());
                    oneLv1.setSpec(oneData.get("SPEC").toString());
                    //oneLv1.setPluBarcode(oneData.get("PLUBARCODE").toString());
                    oneLv1.setFeatureNo(oneData.get("FEATURENO").toString());
                    oneLv1.setFeatureName(oneData.get("FEATURENAME").toString());
                    oneLv1.setOUnit(oneData.get("PUNIT").toString());
                    oneLv1.setOUnitName(oneData.get("UNAME").toString());
                    oneLv1.setOQty(oneData.get("PQTY").toString());
                    oneLv1.setSupplier(oneData.get("SUPPLIER").toString());
                    oneLv1.setBatchNo(oneData.get("BATCHNO").toString());
                    oneLv1.setProdDate(oneData.get("PRODDATE").toString());
                    oneLv1.setExpDate(oneData.get("EXPDATE").toString());
                    oneLv1.setBDate(oneData.get("BDATE").toString());
                    res.getDatas().add(oneLv1);
                    oneLv1 = null;
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setDatas(new ArrayList<DCP_QualityCheckPendingListQueryRes.DataDetail>());
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());//add by 01029 20240703
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_QualityCheckPendingListQueryReq req) throws Exception {
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
        String status = "1";
        String beginDate = null;
        String endDate = null;
        String eId = req.geteId();

        if (req.getRequest() != null) {
            key = req.getRequest().getKeyTxt();
            qcType = req.getRequest().getQcType();
            beginDate = req.getRequest().getBeginDate();
            endDate = req.getRequest().getEndDate();
            if (StringUtils.isEmpty(beginDate))
                beginDate = "20241001";
            if (StringUtils.isEmpty(endDate))
                endDate = "20641001";
        }

        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append("SELECT * FROM ("
                + " SELECT COUNT( 1 ) OVER() NUM ,ROWNUM AS rn ,  a.* ,c1.PLU_NAME as pluName,image.listimage,gul.spec,fn.featurename,dw.UNAME "
                + "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName ,b.SUPPLIER,b.BDATE "
                + " FROM DCP_PURRECEIVE_DETAIL   a "
                + " inner join DCP_PURRECEIVE  b on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.BILLNO=b.BILLNO  "
                + " INNER JOIN DCP_GOODS b1 ON a.PLUNO=b1.PLUNO AND a.EID=b1.EID "
                + " LEFT JOIN DCP_GOODS_LANG  c1 ON a.PLUNO=c1.PLUNO AND  a.EID=c1.EID  and c1.LANG_TYPE='" + langType + "'  "
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL' "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type='" + langType + "' "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno"
                + "	LEFT JOIN DCP_UNIT_LANG dw ON a.PUnit=dw.UNIT AND a.EID=dw.EID and dw.LANG_TYPE='" + langType + "' "
                + " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=b.CREATEOPID "
                + " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=b.LASTMODIOPID "
                + "  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='" + langType + "' "
                + " WHERE a.EID = '" + eId + "' ");

//		if(key != null && key.length() > 0){
//			sqlbuf.append(" and ( a.BILLNO  like  "+SUtil.RetLikeStr(key) ) ; 
//			sqlbuf.append(" or    a.SUPPLIER  like  "+SUtil.RetLikeStr(key) ) ; 
//			sqlbuf.append(" or    a.PLUNO  like  "+SUtil.RetLikeStr(key) ) ; 
//			sqlbuf.append(" ) "  ) ; 
//		}

        sqlbuf.append(" AND b.STATUS='1'")
                .append(" AND a.QCSTATUS='1' ")
                .append(" AND a.ORGANIZATIONNO='").append(req.getOrganizationNO()).append("'");

        sqlbuf.append(" AND NOT EXISTS( SELECT SOURCEBILLNO,OITEM FROM DCP_QUALITYCHECK qc WHERE qc.SOURCEBILLNO = a.BILLNO AND qc.OITEM=a.ITEM ) ");

        if (!Check.Null(beginDate) && !Check.Null(endDate)) {
            sqlbuf.append(" and b.BDATE>='" + beginDate + "' and b.BDATE<='" + endDate + "' ");
        }
        if (status != null && status.length() > 0) {
            sqlbuf.append(" and b.STATUS = '" + status + "' ");
            sqlbuf.append(" and a.QCSTATUS = '" + qcType + "' ");
            sqlbuf.append(" ) DBL  WHERE rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
            //sqlbuf.append(" order by SUPPLIER,IMGTYPE,ENDDATE " );
        }

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
    protected String getCountSql(DCP_QualityCheckPendingListQueryReq req) throws Exception {
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
            qcType = req.getRequest().getQcType();
            beginDate = req.getRequest().getBeginDate();
            endDate = req.getRequest().getEndDate();
            if (StringUtils.isEmpty(beginDate))
                beginDate = "20241001";
            if (StringUtils.isEmpty(endDate))
                endDate = "20641001";
        }

        StringBuffer sqlbuf = new StringBuffer("");
        String sql = "";

        sqlbuf.append("select num from( select count(*) AS num  from DCP_BIZPARTNER a "
                + "where a.EID='" + eId + "'  ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and   a.SUPPLIER  like  " + SUtil.RetLikeStr(key));

        }

        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append(" )");

        sql = sqlbuf.toString();
        return sql;
    }


}
