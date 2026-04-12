package com.dsc.spos.service.imp.json;

 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 
 
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.json.cust.req.DCP_SupplierLicenseQueryReq;
import com.dsc.spos.json.cust.res.DCP_SupplierLicenseQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;
 

public class DCP_SupplierLicenseQuery extends SPosBasicService<DCP_SupplierLicenseQueryReq, DCP_SupplierLicenseQueryRes> {
 
    public List<DataProcessBean> pData = new ArrayList<DataProcessBean>();

    @Override
    protected boolean isVerifyFail(DCP_SupplierLicenseQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_SupplierLicenseQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_SupplierLicenseQueryReq>() {
        };
    }

    @Override
    protected DCP_SupplierLicenseQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_SupplierLicenseQueryRes();
    }

    @Override
    protected DCP_SupplierLicenseQueryRes processJson(DCP_SupplierLicenseQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_SupplierLicenseQueryRes res = null;
        res = this.getResponse();
        String sql = null;
        try {

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

            res.setDatas(new ArrayList<DCP_SupplierLicenseQueryRes.DataDetail>());
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false) {
                

                for (Map<String, Object> oneData : getQDataDetail) {
                    DCP_SupplierLicenseQueryRes.DataDetail oneLv1 = res.new DataDetail();


                    oneLv1.setItem(oneData.get("ITEM").toString());
                    oneLv1.setSupplierNo(oneData.get("SUPPLIER").toString());
                    oneLv1.setSName(oneData.get("SNAME").toString());
                    oneLv1.setFName(oneData.get("FNAME").toString());
                    //oneLv1.setSupplierName(oneData.get("SUPPLIERNAME").toString());
                    oneLv1.setLicenseType(oneData.get("IMGTYPE").toString());
                    oneLv1.setLicenseNo(oneData.get("LICENSENO").toString());
                    oneLv1.setBeginDate(oneData.get("BEGINDATE").toString());
                    oneLv1.setEndDate(oneData.get("ENDDATE").toString());
                    oneLv1.setLicenseStatus(oneData.get("STATUS").toString());
                    oneLv1.setLicenseImg(oneData.get("LICENSEIMG").toString());
                    oneLv1.setCreatorID(oneData.get("CREATEOPID").toString());
                    oneLv1.setCreatorName(oneData.get("CREATEOPNAME").toString());
                    oneLv1.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
                    oneLv1.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
                    oneLv1.setCreate_DateTime(oneData.get("CREATETIME").toString());
                    oneLv1.setLastModifyID(oneData.get("LASTMODIOPID").toString());
                    oneLv1.setLastModifyName(oneData.get("LASTMODIOPNAME").toString());
                    oneLv1.setLastModify_DateTime(oneData.get("LASTMODITIME").toString());


                    res.getDatas().add(oneLv1);
                    oneLv1 = null;
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setDatas(new ArrayList<DCP_SupplierLicenseQueryRes.DataDetail>());
             
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "DCP_SupplierLicenseQuery"+e.getMessage());//add by 01029 20240703
            
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_SupplierLicenseQueryReq req) throws Exception {
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
        String type = null;
        String status = null;
        String isBillQuery = null;
        String eId = req.geteId();

        if (req.getRequest() != null) {
            key = req.getRequest().getSupplier();
            status = req.getRequest().getStatus();
            type = req.getRequest().getLicenseType();

        }

        StringBuffer sqlbuf = new StringBuffer(""); //1-供应商 2-客户 3-交易对象
        sqlbuf.append("SELECT * FROM ("
                + " SELECT ROWNUM AS rn ,  a.* ,b.SNAME,b.FNAME "
                + "  , p.NAME as CREATEOPNAME,d.DEPARTNAME as CREATEDEPTNAME,c.NAME as lastModiOpName "
                + " FROM DCP_SUPPLIER_LICENSE a "
                + " inner join DCP_bizpartner  b on a.eid=b.eid and a.SUPPLIER=b.BIZPARTNERNO  and b.BIZTYPE='1' "
                + " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEOPID "
                + " left join DCP_EMPLOYEE  c on a.eid=c.eid and c.EMPLOYEENO=a.LASTMODIOPID "
                + "  left join DCP_DEPARTMENT_LANG d  on p.eid=d.eid and p.DEPARTMENTNO=d.DEPARTNO and d.lang_type='" + langType + "' "
                + " WHERE a.EID = '" + eId + "' and b.BIZTYPE='1' ");

        if (key != null && key.length() > 0) {
            sqlbuf.append(" and   ( a.SUPPLIER  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    b.FName  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" )  " ); 
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and a.IMGTYPE like " + SUtil.RetLikeStr(type));

        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append(" ) DBL  WHERE rn > " + startRow + " AND rn  <= " + (startRow + pageSize) + " ");
        sqlbuf.append(" order by SUPPLIER,IMGTYPE,ITEM ");


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
    protected String getCountSql(DCP_SupplierLicenseQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String key = null;
        String status = null;
        String eId = req.geteId();
        String type = null;
        if (req.getRequest() != null) {
            key = req.getRequest().getSupplier();
            status = req.getRequest().getStatus();
            type = req.getRequest().getLicenseType();
        }

        StringBuffer sqlbuf = new StringBuffer("");
        String sql = "";

        sqlbuf.append("  select num from( select count(*) AS num  from DCP_SUPPLIER_LICENSE a "
        		+ " inner join DCP_bizpartner  b on a.eid=b.eid and a.SUPPLIER=b.BIZPARTNERNO  and b.BIZTYPE='1' "
                + " where a.EID='" + eId + "'  ");

        if (key != null && key.length() > 0) {
        	sqlbuf.append(" and   ( a.SUPPLIER  like  " + SUtil.RetLikeStr(key));
            sqlbuf.append(" or    b.FName  like  "+SUtil.RetLikeStr(key)); 
			sqlbuf.append(" )  " ); 
        }
        if (type != null && type.length() > 0)
            sqlbuf.append(" and  a.IMGTYPE like " + SUtil.RetLikeStr(type));
        if (status != null && status.length() > 0)
            sqlbuf.append(" and a.STATUS = '" + status + "' ");
        sqlbuf.append(" )");

        sql = sqlbuf.toString();
        return sql;
    }


}
