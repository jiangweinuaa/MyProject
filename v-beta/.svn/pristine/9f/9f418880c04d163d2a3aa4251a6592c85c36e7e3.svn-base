package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SubStockTakeQueryReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeQueryRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeQueryRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeQueryRes.level2Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_SubStockTakeQuery
 * 服务说明：盘点子任务列表查询
 * @author jinzma
 * @since  2021-02-25
 */
public class DCP_SubStockTakeQuery extends SPosBasicService<DCP_SubStockTakeQueryReq, DCP_SubStockTakeQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        if (Check.Null(beginDate)) {
            errMsg.append("起始日期不能为空,");
            isFail = true;
        }
        if (Check.Null(endDate)) {
            errMsg.append("截止日期不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeQueryReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeQueryReq>(){};
    }

    @Override
    protected DCP_SubStockTakeQueryRes getResponseType() {
        return new DCP_SubStockTakeQueryRes();
    }

    @Override
    protected DCP_SubStockTakeQueryRes processJson(DCP_SubStockTakeQueryReq req) throws Exception {
        DCP_SubStockTakeQueryRes res = this.getResponse();
        level1Elm datas = res.new level1Elm();
        datas.setStockTakeList(new ArrayList<level2Elm>());
        try {
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            if (getQData != null && !getQData.isEmpty()) {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                for (Map<String, Object> oneData : getQData) {
                    level2Elm oneLv2 = res.new level2Elm();
                    String subStockTakeNo=oneData.get("SUBSTOCKTAKENO").toString();
                    String shopId=oneData.get("SHOPID").toString();
                    String bDate=oneData.get("BDATE").toString();
                    String stockTakeNo=oneData.get("STOCKTAKENO").toString();
                    String docType=oneData.get("DOC_TYPE").toString();
                    String isBTake=oneData.get("IS_BTAKE").toString();
                    String taskWay=oneData.get("TASKWAY").toString();
                    String deviceNo=oneData.get("DEVICENO").toString();
                    String stockType=oneData.get("STOCKTYPE").toString();
                    String memo=oneData.get("MEMO").toString();
                    String status=oneData.get("STATUS").toString();
                    String importStatus=oneData.get("IMPORTSTATUS").toString();
                    String createOpId=oneData.get("CREATEOPID").toString();
                    String createOpName=oneData.get("CREATEOPNAME").toString();
                    String createTime=oneData.get("CREATE_TIME").toString();
                    String lastModiOpId=oneData.get("LASTMODIOPID").toString();
                    String lastModiOpName=oneData.get("LASTMODIOPNAME").toString();
                    String lastModiTime=oneData.get("LASTMODI_TIME").toString();

                    oneLv2.setbDate(bDate);
                    oneLv2.setCreateOpId(createOpId);
                    oneLv2.setCreateOpName(createOpName);
                    oneLv2.setCreateTime(createTime);
                    oneLv2.setDeviceNo(deviceNo);
                    oneLv2.setDocType(docType);
                    oneLv2.setImportStatus(importStatus);
                    oneLv2.setIsBTake(isBTake);
                    oneLv2.setLastModiOpId(lastModiOpId);
                    oneLv2.setLastModiOpName(lastModiOpName);
                    oneLv2.setLastModiTime(lastModiTime);
                    oneLv2.setMemo(memo);
                    oneLv2.setShopId(shopId);
                    oneLv2.setStatus(status);
                    oneLv2.setStockTakeNo(stockTakeNo);
                    oneLv2.setStockType(stockType);
                    oneLv2.setSubStockTakeNo(subStockTakeNo);
                    oneLv2.setTaskWay(taskWay);

                    datas.getStockTakeList().add(oneLv2);
                }
            }else{
                totalRecords = 0;
                totalPages = 0;
            }

            res.setDatas(datas);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_SubStockTakeQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String shopid = req.getShopId();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String deviceNo = req.getRequest().getDeviceNo();
        String stockType = req.getRequest().getStockType();
        String keyTxt = req.getRequest().getKeyTxt();
        String status = req.getRequest().getStatus();
        String importStatus = req.getRequest().getImportStatus();

        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;

        sqlbuf.append(""
                + " select * from ("
                + " select count(*) over() num,row_number() over (order by a.createtime desc) rn,"
                + " a.*,"
                + " to_char(a.createtime, 'yyyy-MM-dd hh24:mi:ss' ) AS create_time,"
                + " to_char(a.lastmodiTime , 'yyyy-MM-dd hh24:mi:ss' ) AS lastmodi_time,"
                + " b.doc_type,b.is_btake,b.taskway"
                + " from dcp_substocktake a"
                + " inner join dcp_stocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno"
                + " where a.eid='"+eId+"' and a.shopid='"+shopid+"'"
                + " and to_char(a.createtime,'yyyymmdd')>='"+beginDate+"' and to_char(a.createtime,'yyyymmdd')<='"+endDate+"'"
                + " ");
        if (!Check.Null(deviceNo)){
            sqlbuf.append(" and a.deviceno='"+deviceNo+"'");
        }
        if (!Check.Null(stockType)){
            sqlbuf.append(" and a.stockType='"+stockType+"'");
        }
        if (!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.substocktakeno like '%"+keyTxt+"%' or a.stocktakeno like '%"+keyTxt+"%')");
        }
        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"'");
        }
        if (!Check.Null(importStatus)){
            sqlbuf.append(" and a.importstatus='"+importStatus+"'");
        }
        sqlbuf.append(" ) where  rn>"+startRow+" and rn<="+(startRow+pageSize));

        return sqlbuf.toString();
    }
}
