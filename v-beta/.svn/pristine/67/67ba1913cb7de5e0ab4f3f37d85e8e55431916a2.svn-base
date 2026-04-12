package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_POrderListQueryReq;
import com.dsc.spos.json.cust.res.DCP_POrderListQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服務函數：POrderCreate
 *   說明：要货单列表查询
 * 服务说明：要货单列表查询
 * @author wangzyc
 * @since  2021-05-11
 */
public class DCP_POrderListQuery extends SPosBasicService<DCP_POrderListQueryReq, DCP_POrderListQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_POrderListQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_POrderListQueryReq.level1Elm request = req.getRequest();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String dateType = request.getDateType();

        if (Check.Null(beginDate)) {
            errMsg.append("开始日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(endDate)) {
            errMsg.append("截止日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(dateType)) {
            errMsg.append("日期类型不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_POrderListQueryReq> getRequestType() {
        return new TypeToken<DCP_POrderListQueryReq>(){};
    }

    @Override
    protected DCP_POrderListQueryRes getResponseType() {
        return new DCP_POrderListQueryRes();
    }

    @Override
    protected DCP_POrderListQueryRes processJson(DCP_POrderListQueryReq req) throws Exception {
        DCP_POrderListQueryRes res = null;
        res = this.getResponseType();

        res.setDatas(res.new level1Elm());

        DCP_POrderListQueryRes.level1Elm datas = res.getDatas();

        datas.setOrderList(new ArrayList<DCP_POrderListQueryRes.level2Elm>());

        int totalRecords = 0;								//总笔数
        int totalPages = 0;									//总页数

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);

            // 过滤
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("PORDERNO", true);
            // 调用过滤函数
            List<Map<String, Object>> getHeader = MapDistinct.getMap(getDatas, condition);
            if(!CollectionUtils.isEmpty(getHeader)){
                Map<String, Object> oneData_Count = getHeader.get(0);
                String num = oneData_Count.get("NUM").toString();
                totalRecords=Integer.parseInt(num);

                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //【ID1018250】【3.0货郎】要货撤销无要货模板下也要支持
                String revoke_Day_para = PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(),"revoke_Day");
                String revoke_Time_para = PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(),"revoke_Time");

                for (Map<String, Object> header : getHeader) {
                    DCP_POrderListQueryRes.level2Elm level2Elm = res.new level2Elm();

                    //【ID1018250】【3.0货郎】要货撤销无要货模板下也要支持
                    String ptemplateNo = header.get("PTEMPLATENO").toString();
                    String revoke_Day = header.get("REVOKE_DAY").toString();
                    String revoke_Time = header.get("REVOKE_TIME").toString();
                    if (Check.Null(ptemplateNo)) {
                        revoke_Day = revoke_Day_para ;
                        revoke_Time = revoke_Time_para;
                    }


                    level2Elm.setProcessERPNo(header.get("PROCESS_ERP_NO").toString());
                    level2Elm.setPorderNo(header.get("PORDERNO").toString());
                    level2Elm.setOType(header.get("OTYPE").toString());
                    level2Elm.setOfNo(header.get("OFNO").toString());
                    level2Elm.setBDate(header.get("BDATE").toString());
                    level2Elm.setPTemplateNo(header.get("PTEMPLATENO").toString());
                    level2Elm.setPTemplateName(header.get("PTEMPLATE_NAME").toString());
                    level2Elm.setIsAdd(header.get("IS_ADD").toString());
                    level2Elm.setMemo(header.get("MEMO").toString());
                    level2Elm.setStatus(header.get("STATUS").toString());
                    level2Elm.setCreateByName(header.get("OPNAME").toString());
                    level2Elm.setRDate(header.get("RDATE").toString());
                    level2Elm.setRTime(header.get("RTIME").toString());
                    level2Elm.setTotPqty(header.get("TOT_PQTY").toString());
                    level2Elm.setTotCqty(header.get("TOT_CQTY").toString());
                    level2Elm.setTotAmt(header.get("TOT_AMT").toString());
                    level2Elm.setTotDistriAmt(header.get("TOT_DISTRIAMT").toString());
                    level2Elm.setIsUrgentOrder(header.get("ISURGENTORDER").toString());
                    level2Elm.setReceiptOrgNo(header.get("RECEIPT_ORG").toString());
                    level2Elm.setRevoke_Day(revoke_Day);
                    level2Elm.setRevoke_Time(revoke_Time);

                    datas.getOrderList().add(level2Elm);
                }

                res.setDatas(datas);
            }

            Calendar tempcalPre = Calendar.getInstance();//获得当前时间
            SimpleDateFormat tempdfPre=new SimpleDateFormat("yyyyMMdd");
            String _DATE = tempdfPre.format(tempcalPre.getTime());

            res.setSysDate(_DATE);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_POrderListQueryReq req) throws Exception {

        DCP_POrderListQueryReq.level1Elm request = req.getRequest();
        String keyTxt = request.getKeyTxt();
        String status = request.getStatus();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String shopId = req.getShopId();

        String dateType = request.getDateType(); // RDATE / BDATE
        if (Check.Null(dateType) || !dateType.equals("rDate"))  dateType="bDate";

        //計算起啟位置
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        int pageSize = req.getPageSize();

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM ( " +
                " SELECT count (DISTINCT a.EID||a.PORDERNO||a.SHOPID) OVER() NUM,DENSE_RANK () over(ORDER BY a.PORDERNO desc,a.SHOPID,a.EID) rn , " +
                " a.PROCESS_ERP_NO, a.PORDERNO, a.OTYPE, a.OFNO, a.BDATE , a.PTEMPLATENO, b.PTEMPLATE_NAME,b.revoke_Day,b.revoke_Time,a.IS_ADD, a.MEMO, " +
                " a.STATUS , a.CREATEBY, c.OPNAME, a.RDATE, a.RTIME, a.TOT_PQTY , a.TOT_CQTY, a.TOT_AMT, a.TOT_DISTRIAMT, a.ISURGENTORDER, a.RECEIPT_ORG " +
                " FROM DCP_PORDER a " +
                " LEFT JOIN DCP_PTEMPLATE b ON a.EID = b.EID AND a.PTEMPLATENO = b.PTEMPLATENO AND b.DOC_TYPE='0'" +
                " LEFT JOIN PLATFORM_STAFFS c ON a.EID = c.EID AND a.CREATEBY = c.OPNO " +
                " WHERE a.EID = '"+req.geteId()+"' and a.shopId = '"+shopId+"'");

        if (dateType.equals("bDate"))
            sqlbuf.append(" AND A.BDATE BETWEEN "+beginDate +" AND " +endDate+ " ");
        if (dateType.equals("rDate"))
            sqlbuf.append(" AND A.RDATE BETWEEN "+beginDate +" AND " +endDate+ " ");

        if (status != null) {
            sqlbuf.append("  AND a.status = '"+status+"' ");
        }

        if (keyTxt != null && keyTxt.length()!=0) {
            sqlbuf.append(""
                    + "AND (A.TOT_AMT like '%%"+ keyTxt +"%%'  "
                    + "OR  A.TOT_PQTY like '%%"+ keyTxt +"%%'   "
                    + "OR  A.PORDERNO like '%%"+ keyTxt +"%%'    "
                    + "OR  A.MEMO like '%%"+ keyTxt +"%%'   "
                    + "OR  b.PTEMPLATENO like '%%"+ keyTxt +"%%'  "
                    + "OR  b.PTEMPLATE_NAME like '%%"+ keyTxt +"%%' "
                    + " OR  A.process_ERP_No like '%%"+keyTxt+"%%')   "
            );
        }

        if (dateType.equals("bDate"))
            sqlbuf.append(" order by a.status ASC,a.bDate DESC, a.porderNO DESC ");
        if (dateType.equals("rDate"))
            sqlbuf.append(" order by a.status ASC,a.rDate DESC, a.porderNO DESC ");
        sqlbuf.append(") where 1=1 AND (rn > " + startRow + " AND rn <= " + (startRow+pageSize) + " ) ");
        sql = sqlbuf.toString();
        return sql;
    }
}
