package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_DifferenceQueryReq;
import com.dsc.spos.json.cust.req.DCP_DifferenceQueryReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_DifferenceQueryRes;
import com.dsc.spos.json.cust.res.DCP_DifferenceQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_DifferenceQuery extends SPosBasicService<DCP_DifferenceQueryReq,DCP_DifferenceQueryRes>
{
    @Override
    protected boolean isVerifyFail(DCP_DifferenceQueryReq req) throws Exception
    {
        boolean isFail = false;
        levelElm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer();
        if (request.getOfNo() == null)
        {
            errMsg.append("来源单号不能为Null, ");
            isFail = true;
        }

        if (request.getDocType() == null)
        {
            errMsg.append("类型不能为Null, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DifferenceQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_DifferenceQueryReq>(){};
    }

    @Override
    protected DCP_DifferenceQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_DifferenceQueryRes();
    }

    @Override
    protected DCP_DifferenceQueryRes processJson(DCP_DifferenceQueryReq req) throws Exception
    {
        try {
            int totalRecords;		//总笔数
            int totalPages;
            DCP_DifferenceQueryRes res = this.getResponse();
            res.setDatas(new ArrayList<>());
            String sql = this.getQueryMainSql(req);
            String[] condCountValues = { }; //查詢條件
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, condCountValues);
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
            {

                String num = getQDataDetail.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


                for(Map<String, Object> oneData : getQDataDetail){
                    DCP_DifferenceQueryRes.levelTElm levelTElm = res.new levelTElm();
                    String bDate = oneData.get("BDATE").toString();
                    String differenceNO = oneData.get("DIFFERENCENO").toString();
                    String docType = oneData.get("DOCTYPE").toString();
                    String loadDocNO = oneData.get("LOADDOCNO").toString();
                    String loadDocType = oneData.get("LOADDOCTYPE").toString();
                    String memo = oneData.get("MEMO").toString();
                    String ofNO = oneData.get("OFNO").toString();
                    String oType = oneData.get("OTYPE").toString();
                    String status = oneData.get("STATUS").toString();
                    String warehouse = oneData.get("WAREHOUSE").toString();
                    String warehouseName = oneData.get("WAREHOUSENAME").toString();
                    String createBy =oneData.get("CREATEBY").toString();
                    String createByName =oneData.get("CREATEBYNAME").toString();
                    String createDate =oneData.get("CREATEDATE").toString();
                    String createTime =oneData.get("CREATETIME").toString();
                    String modifyBy =oneData.get("MODIFYBY").toString();
                    String modifyByName =oneData.get("MODIFYBYNAME").toString();
                    String modifyDate =oneData.get("MODIFYDATE").toString();
                    String modifyTime =oneData.get("MODIFYTIME").toString();
                    String submitBy =oneData.get("SUBMITBY").toString();
                    String submitByName =oneData.get("SUBMITBYNAME").toString();
                    String submitDate =oneData.get("SUBMITDATE").toString();
                    String submitTime =oneData.get("SUBMITTIME").toString();
                    String confirmBy =oneData.get("CONFIRMBY").toString();
                    String confirmByName =oneData.get("CONFIRMBYNAME").toString();
                    String confirmDate =oneData.get("CONFIRMDATE").toString();
                    String confirmTime =oneData.get("CONFIRMTIME").toString();
                    String cancelBy =oneData.get("CANCELBY").toString();
                    String cancelByName =oneData.get("CANCELBYNAME").toString();
                    String cancelDate =oneData.get("CANCELDATE").toString();
                    String cancelTime =oneData.get("CANCELTIME").toString();
                    String accountBy =oneData.get("ACCOUNTBY").toString();
                    String accountByName =oneData.get("ACCOUNTBYNAME").toString();
                    String accountDate =oneData.get("ACCOUNTDATE").toString();
                    String accountTime =oneData.get("ACCOUNTTIME").toString();
                    String transferShop=oneData.get("TRANSFERSHOP").toString();
                    String transferShopName=oneData.get("TRANSFERSHOPNAME").toString();

                    String totPqty = oneData.get("TOTPQTY").toString();
                    String totAmt = oneData.get("TOTAMT").toString();
                    String totCqty = oneData.get("TOTCQTY").toString();
                    String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();

                    String orgNo = oneData.get("ORGANIZATIONNO").toString();
                    String orgName = oneData.get("ORGANIZATIONNAME").toString();


                    levelTElm.setBDate(bDate);
                    levelTElm.setCreateByName(createByName);
                    levelTElm.setDifferenceNo(differenceNO);
                    levelTElm.setDocType(docType);
                    levelTElm.setLoadDocNo(loadDocNO);
                    levelTElm.setLoadDocType(loadDocType);
                    levelTElm.setMemo(memo);
                    levelTElm.setOfNo(ofNO);
                    levelTElm.setOType(oType);
                    levelTElm.setStatus(status);
                    levelTElm.setWarehouse(warehouse);
                    levelTElm.setWarehouseName(warehouseName);

                    levelTElm.setCreateBy(createBy);
                    levelTElm.setCreateByName(createByName);
                    levelTElm.setCreateDate(createDate);
                    levelTElm.setCreateTime(createTime);
                    levelTElm.setModifyBy(modifyBy);
                    levelTElm.setModifyByName(modifyByName);
                    levelTElm.setModifyDate(modifyDate);
                    levelTElm.setModifyTime(modifyTime);
                    levelTElm.setSubmitBy(submitBy);
                    levelTElm.setSubmitByName(submitByName);
                    levelTElm.setSubmitDate(submitDate);
                    levelTElm.setSubmitTime(submitTime);
                    levelTElm.setConfirmBy(confirmBy);
                    levelTElm.setConfirmByName(confirmByName);
                    levelTElm.setConfirmDate(confirmDate);
                    levelTElm.setConfirmTime(confirmTime);
                    levelTElm.setCancelBy(cancelBy);
                    levelTElm.setCancelByName(cancelByName);
                    levelTElm.setCancelDate(cancelDate);
                    levelTElm.setCancelTime(cancelTime);
                    levelTElm.setAccountBy(accountBy);
                    levelTElm.setAccountByName(accountByName);
                    levelTElm.setAccountDate(accountDate);
                    levelTElm.setAccountTime(accountTime);

                    levelTElm.setTotPqty(totPqty);
                    levelTElm.setTotAmt(totAmt);
                    levelTElm.setTotCqty(totCqty);
                    levelTElm.setTransferShopName(transferShopName);
                    levelTElm.setTransferShop(transferShop);
                    levelTElm.setTotDistriAmt(totDistriAmt);
                    levelTElm.setOrgName(orgName);
                    levelTElm.setOrgNo(orgNo);
                    levelTElm.setOrganizationName(orgName);

                    String UPDATE_TIME;
                    SimpleDateFormat simptemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat allsimptemp=new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    if(oneData.get("UPDATE_TIME")==null||oneData.get("UPDATE_TIME").toString().isEmpty())
                    {
                        UPDATE_TIME=allsimptemp.format(Calendar.getInstance().getTime());
                    }
                    else
                    {
                        UPDATE_TIME=oneData.get("UPDATE_TIME").toString();
                    }
                    levelTElm.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));

                    levelTElm.setProcess_status(oneData.get("PROCESS_STATUS").toString());
                    res.getDatas().add(levelTElm);
                }



            }
            else
            {
                totalRecords = 0;
                totalPages = 0;
                res.setDatas(new ArrayList<>());
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_DifferenceQueryReq req) throws Exception
    {
        String langType = req.getLangType();
        String sql=null;
        StringBuffer sqlbuf=new StringBuffer("");
        levelElm request = req.getRequest();

        String ofNoStr=" 1=1 ";
        if(Check.NotNull(request.getOfNo())){
            ofNoStr=" (a.ofno='"+request.getOfNo()+"' or a.load_docno='"+request.getOfNo()+"')  ";
        }

        sqlbuf.append(" "
                + " SELECT differenceNO,bDate,memo,status,oType,ofNO,docType,loadDocType,loadDocNO,createDate,createTime,submitDate,"
                + " submitBy,submitTime,completeDate,totPqty,totAmt,TOT_DISTRIAMT,totCqty,item,oItem,pluNO,featureNo,FEATURENAME,"
                + " pqty,punit,BASEUNIT,baseUnitName,unitRatio,req_qty,"
                + " price,amt,DISTRIAMT,bsNO,processMode,bsName,pluName,punitName,"
                + " createby,createByName,stockOutQty,"
                + " stockInQty,warehouse,warehouseName,"
                //2018-11-21    添加 createDate 等字段
                + " process_status,update_time,modifyDate,modifyTime,modifyBy, "
                + " confirmBy,confirmDate,ConfirmTime,cancelBy,cancelDate,cancelTime, "
                + " accountBY,accountDate,accountTime,modifyByName,ConfirmByName, "
                + " cancelByName , submitByName,  accountByName,"
                + " LISTIMAGE,spec,transferShop,TRANSFERSHOPNAME,BATCH_NO,PROD_DATE,DISTRIPRICE,ISBATCH,baseqty  "
                + " from "
                + " ("
                + " SELECT a.differenceNO,a.bDate,a.memo,a.status,a.oType,a.ofNO,a.doc_type as docType,a.load_doctype as loadDocType,"
                + " a.load_docno as loadDocNO,a.create_date as createDate,a.create_time as createTime,a.submit_date as submitDate,a.submitBy, "
                + " a.submit_time as submitTime,a.complete_date as completeDate,a.tot_pqty as totPqty, a.tot_amt as totAmt,a.TOT_DISTRIAMT,"
                + " a.tot_cqty as totCqty, "
                + " b.item ,b.oitem as oItem,b.pluNO,b.pqty,b.punit,b.baseunit,b.unitRatio,b.reqQty as req_qty, "
                + " b.price,b.pqty*b.price as amt,b.DISTRIAMT,b.bsNO,g.reason_name as bsName,b.processMode, "
                + " d.plu_name as pluName,e.uname as punitname,e1.uname as baseunitName,a.createby,"
                + " f.op_name as createByName,b.stockOutQty,b.stockInQty, "
                + " a.warehouse, w.warehouse_name as warehouseName,a.process_status,a.update_time, "
                + " a.modify_Date  AS modifyDate ,  a.modify_time AS modifyTime  , a.modifyby , "
                + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
                + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
                + " a.accountby, a.account_date AS accountDate, a.account_time AS accountTime , "
                + " f1.op_name as modifyByName ,f2.op_name as cancelByName , f3.op_name as ConfirmByName , "
                + " f4.op_name as submitByName ,  f5.op_name as accountByName , "
                + " image.listimage, gul.spec,a.transfer_shop as transferShop, "
                + " b.BATCH_NO,b.PROD_DATE,b.DISTRIPRICE,c.ISBATCH,ORG_LANG.ORG_NAME AS TRANSFERSHOPNAME,"
                + " fn.featurename,b.featureno,b.baseqty "
                + " FROM DCP_DIFFERENCE a inner join ("
                + " select "
                + " eid,organizationno,shopid,differenceno, item ,oitem,pluno,punit, "
                + " baseunit,max(unitratio) as unitratio,sum(pqty) as pqty,sum(baseqty) as baseqty,"
                + " sum(reqqty) as reqqty,max(price) as price,max(bsno) as bsno, "
                + " sum(stockoutqty) as stockoutqty,max(processmode) as processmode, "
                + " sum(stockinqty) as stockinqty, "
                + " batch_no,prod_date, max(distriprice) as distriprice,sum(distriamt) as distriamt,featureno "
                + " from (  "
                + " select a.eid,a.organizationno,a.shopid,a.differenceno,b.item,b.oitem,b.pluno,nvl(e.pqty,0) as pqty,b.punit, "
                + " nvl(e.baseqty,0) as baseqty,"
                + " b.baseunit,b.unit_ratio as unitratio,b.req_qty as reqqty,b.price,b.amt,b.bsno,nvl(c.receiving_qty,0) as stockoutqty, "
                + " nvl(c.pqty,0) as stockinqty,b.process_mode as processmode,a.warehouse as warehouse,"
                + " b.batch_no,b.prod_date,b.distriprice,b.distriamt,b.featureno  "
                + " from dcp_difference a  "
                + " inner join dcp_difference_detail b on a.differenceno=b.differenceno and a.eid=b.eid and a.shopid=b.shopid  "
                + " left join dcp_stockin_detail c "
                + " on a.eid=c.eid and a.ofno=c.stockinno and a.shopid=c.shopid and b.pluno=c.pluno and b.oitem=c.item "
                + " left join dcp_adjust d on a.eid=d.eid and a.organizationno=d.organizationno and d.ofno=a.differenceno "
                + " left join dcp_adjust_detail e "
                + " on d.adjustno=e.adjustno and d.eid=e.eid and d.shopid=e.shopid and b.pluno=e.pluno "
                + " and (b.featureno=e.featureno or (trim(b.featureno) is null and trim(e.featureno) is null) )"
                + " where a.EID='"+req.geteId()+"' and a.organizationno='"+req.getShopId()+"' "
                + " and  "+ofNoStr
                + " ) group by EID,organizationno,SHOPID,differenceno,pluNO,BATCH_NO,PROD_DATE,punit,baseunit,item,oitem,featureno "
                + " ) b ON a.DIFFERENCENO=b.DIFFERENCENO and a.EID=b.EID and a.SHOPID=b.SHOPID "
                + " INNER JOIN DCP_GOODS c ON b.PLUNO=c.PLUNO AND b.EID=c.EID  "
                + " LEFT  JOIN DCP_GOODS_LANG  d ON b.PLUNO=d.PLUNO AND  b.EID=d.EID  AND d.LANG_TYPE='"+req.getLangType()+"' "
                + " LEFT  JOIN DCP_UNIT_LANG e ON b.PUNIT=e.UNIT AND b.EID=e.EID AND e.LANG_TYPE='"+req.getLangType()+"'  "
                + " LEFT  JOIN DCP_UNIT_LANG e1 ON b.baseunit=e1.UNIT AND b.EID=e1.EID AND e1.LANG_TYPE='"+req.getLangType()+"'  "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f on a.EID=f.EID and f.lang_type='"+req.getLangType()+"' and (a.createby=f.opno ) "
                //2018-11-20 新增以下几行， 用于查modifyByName 等字段
                + " LEFT JOIN PLATFORM_STAFFS_LANG f1 ON A .EID = f1.EID AND A .modifyBy = f1.opno AND F1.LANG_TYPE  = '" + langType + "' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f2 ON A .EID = f2.EID AND A .cancelby = f2.opno AND F2.LANG_TYPE = '" + langType + "' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f3 ON A .EID = f3.EID AND A .confirmby = f3.opno AND F3.LANG_TYPE = '" + langType + "' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f4 ON A .EID = f4.EID AND A .submitby = f4.opno AND F4.LANG_TYPE = '" + langType + "' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG f5 ON A .EID = f5.EID AND A .accountby = f5.opno AND F5.LANG_TYPE = '" + langType + "' "
                + " LEFT JOIN DCP_REASON_lang g on b.EID=g.EID and b.bsno=g.bsno and g.lang_type='"+req.getLangType()+"' "
                + " left join DCP_WAREHOUSE_lang w on a.warehouse=w.warehouse and b.EID=w.EID and b.ORGANIZATIONNO=w.ORGANIZATIONNO and w.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN DCP_ORG_LANG ORG_LANG ON A.EID=ORG_LANG.EID AND A.TRANSFER_SHOP=ORG_LANG.ORGANIZATIONNO AND ORG_LANG.LANG_TYPE='"+req.getLangType()+"' "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
                + " WHERE "+ofNoStr
                + " AND a.SHOPID='"+req.getShopId()+"' "
                + " AND a.EID='"+req.geteId()+"' "
                + (Check.Null(request.getDocType())?" ":" and a.doc_type='"+request.getDocType()+"'  ")
                + " order by  status asc，bdate desc，differenceno desc,item "
                + " )");


        sql=sqlbuf.toString();

        return sql;
    }
    private String getQueryMainSql(DCP_DifferenceQueryReq req) throws Exception
    {
        String langType = req.getLangType();
        String sql=null;
        StringBuffer sqlbuf=new StringBuffer("");
        levelElm request = req.getRequest();

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        String ofNoStr=" 1=1 ";
        if(Check.NotNull(request.getOfNo())){
            ofNoStr=" (a.ofno='"+request.getOfNo()+"' or a.load_docno='"+request.getOfNo()+"')  ";
        }

        if(Check.NotNull(request.getBeginDate())){
            ofNoStr+=" and a.bdate>='"+request.getBeginDate()+"' ";
        }
        if(Check.NotNull(request.getEndDate())){
            ofNoStr+=" and a.bdate<='"+request.getEndDate()+"' ";
        }

        if(Check.NotNull(request.getQueryType())&&"1".equals(request.getQueryType())){
            ofNoStr+=" and a.TRANSFER_SHOP='"+req.getOrganizationNO()+"' ";
        }else{
            ofNoStr+=" and a.organizationNo='"+req.getOrganizationNO()+"' ";
        }

        if(Check.NotNull(request.getKeyTxt())){
            ofNoStr+=" and ( "
                    + " a.differenceno like '%%"+request.getKeyTxt()+"%%' "
                    + " or a.ofno like '%%"+request.getKeyTxt()+"%%' "
                    + " or a.load_docno like '%%"+request.getKeyTxt()+"%%' ) ";
        }

        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,ac.* from (");
        sqlbuf.append(" select a.* from (");

        sqlbuf.append(" "
                + " SELECT differenceNO,bDate,memo,status,oType,ofNO,docType,loadDocType,loadDocNO,createDate,createTime,submitDate,"
                + " submitBy,submitTime,completeDate,totPqty,totAmt,TOT_DISTRIAMT,totCqty,"
                + " createby,createByName,"
                + " warehouse,warehouseName,"
                + " process_status,update_time,modifyDate,modifyTime,modifyBy, "
                + " confirmBy,confirmDate,ConfirmTime,cancelBy,cancelDate,cancelTime, "
                + " accountBY,accountDate,accountTime,modifyByName,ConfirmByName, "
                + " cancelByName , submitByName,  accountByName"
                + " ,transferShop,TRANSFERSHOPNAME,organizationno,organizationname  "
                + " from "
                + " ("
                + " SELECT a.differenceNO,a.bDate,a.memo,a.status,a.oType,a.ofNO,a.doc_type as docType,a.load_doctype as loadDocType,"
                + " a.load_docno as loadDocNO,a.create_date as createDate,a.create_time as createTime,a.submit_date as submitDate,a.submitBy, "
                + " a.submit_time as submitTime,a.complete_date as completeDate,a.tot_pqty as totPqty, a.tot_amt as totAmt,a.TOT_DISTRIAMT,"
                + " a.tot_cqty as totCqty, "
                + " a.createby,"
                + " f.name as createByName, "
                + " a.warehouse, w.warehouse_name as warehouseName,a.process_status,a.update_time, "
                + " a.modify_Date  AS modifyDate ,  a.modify_time AS modifyTime  , a.modifyby , "
                + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
                + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
                + " a.accountby, a.account_date AS accountDate, a.account_time AS accountTime , "
                + " f1.name as modifyByName ,f2.name as cancelByName , f3.name as ConfirmByName , "
                + " f4.name as submitByName ,  f5.name as accountByName , "
                + " a.transfer_shop as transferShop, "
                + " ORG_LANG.ORG_NAME AS TRANSFERSHOPNAME ,a.organizationno,o1.org_name as organizationname "
                + " FROM DCP_DIFFERENCE a "
                + " LEFT JOIN dcp_employee f on a.EID=f.EID and (a.createby=f.employeeno ) "
                + " LEFT JOIN dcp_employee f1 ON A .EID = f1.EID AND A .modifyBy = f1.employeeno "
                + " LEFT JOIN dcp_employee f2 ON A .EID = f2.EID AND A .cancelby = f2.employeeno  "
                + " LEFT JOIN dcp_employee f3 ON A .EID = f3.EID AND A .confirmby = f3.employeeno  "
                + " LEFT JOIN dcp_employee f4 ON A .EID = f4.EID AND A .submitby = f4.employeeno "
                + " LEFT JOIN dcp_employee f5 ON A .EID = f5.EID AND A .accountby = f5.employeeno  "
                + " left join DCP_WAREHOUSE_lang w on a.warehouse=w.warehouse and a.EID=w.EID and a.ORGANIZATIONNO=w.ORGANIZATIONNO and w.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN DCP_ORG_LANG ORG_LANG ON A.EID=ORG_LANG.EID AND A.TRANSFER_SHOP=ORG_LANG.ORGANIZATIONNO AND ORG_LANG.LANG_TYPE='"+req.getLangType()+"' "
                + " LEFT JOIN DCP_ORG_LANG o1 ON A.EID=o1.EID AND A.organizationno=o1.ORGANIZATIONNO AND o1.LANG_TYPE='"+req.getLangType()+"' "

                + " WHERE "+ofNoStr
                //+ " AND a.SHOPID='"+req.getShopId()+"' "
                + " AND a.EID='"+req.geteId()+"' "
                + (Check.Null(request.getDocType())?" ":" and a.doc_type='"+request.getDocType()+"'  ")
                + " order by  status asc，bdate desc，differenceno desc "
                + " )");

        sqlbuf.append(" ) a "
                + " )ac"
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");



        sql=sqlbuf.toString();

        return sql;
    }


}
