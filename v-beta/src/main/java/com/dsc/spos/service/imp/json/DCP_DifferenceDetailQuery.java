package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DifferenceDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_DifferenceDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_DifferenceDetailQuery extends SPosBasicService<DCP_DifferenceDetailQueryReq, DCP_DifferenceDetailQueryRes>
{
    @Override
    protected boolean isVerifyFail(DCP_DifferenceDetailQueryReq req) throws Exception
    {
        boolean isFail = false;
        DCP_DifferenceDetailQueryReq.levelElm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer();
        if (request.getDifferenceNo() == null)
        {
            errMsg.append("差异申诉单号不能为空, ");
            isFail = true;
        }



        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DifferenceDetailQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_DifferenceDetailQueryReq>(){};
    }

    @Override
    protected DCP_DifferenceDetailQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_DifferenceDetailQueryRes();
    }

    @Override
    protected DCP_DifferenceDetailQueryRes processJson(DCP_DifferenceDetailQueryReq req) throws Exception
    {
        try {
            DCP_DifferenceDetailQueryRes res = this.getResponse();
            String sql = this.getQuerySql(req);
            String[] condCountValues = { }; //查詢條件
            List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, condCountValues);
            if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
            {
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }

                //单头主键字段
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("DIFFERENCENO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader = MapDistinct.getMap(getQDataDetail, condition);
                res.setDatas(new ArrayList<DCP_DifferenceDetailQueryRes.Datas>());
                for (Map<String, Object> oneData : getQHeader)
                {
                    // 取出第一层
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


                    res.setBDate(bDate);
                    res.setCreateByName(createByName);
                    res.setDifferenceNo(differenceNO);
                    res.setDocType(docType);
                    res.setLoadDocNo(loadDocNO);
                    res.setLoadDocType(loadDocType);
                    res.setMemo(memo);
                    res.setOfNo(ofNO);
                    res.setOType(oType);
                    res.setStatus(status);
                    res.setWarehouse(warehouse);
                    res.setWarehouseName(warehouseName);

                    res.setCreateBy(createBy);
                    res.setCreateByName(createByName);
                    res.setCreateDate(createDate);
                    res.setCreateTime(createTime);
                    res.setModifyBy(modifyBy);
                    res.setModifyByName(modifyByName);
                    res.setModifyDate(modifyDate);
                    res.setModifyTime(modifyTime);
                    res.setSubmitBy(submitBy);
                    res.setSubmitByName(submitByName);
                    res.setSubmitDate(submitDate);
                    res.setSubmitTime(submitTime);
                    res.setConfirmBy(confirmBy);
                    res.setConfirmByName(confirmByName);
                    res.setConfirmDate(confirmDate);
                    res.setConfirmTime(confirmTime);
                    res.setCancelBy(cancelBy);
                    res.setCancelByName(cancelByName);
                    res.setCancelDate(cancelDate);
                    res.setCancelTime(cancelTime);
                    res.setAccountBy(accountBy);
                    res.setAccountByName(accountByName);
                    res.setAccountDate(accountDate);
                    res.setAccountTime(accountTime);

                    res.setTotPqty(totPqty);
                    res.setTotAmt(totAmt);
                    res.setTotCqty(totCqty);
                    res.setTransferShopName(transferShopName);
                    res.setTransferShop(transferShop);
                    res.setTotDistriAmt(totDistriAmt);

                    res.setTransferWarehouse(oneData.get("TRANSFERWAREHOUSE").toString());
                    res.setTransferWarehouseName(oneData.get("TRANSFERWAREHOUSENAME").toString());
                    res.setInvWarehouse(oneData.get("INVWAREHOUSE").toString());
                    res.setInvWarehouseName(oneData.get("INVWAREHOUSENAME").toString());
                    res.setCreateType(oneData.get("CREATETYPE").toString());
                    res.setOrgNo(oneData.get("ORGANIZATIONNO").toString());
                    res.setOrgName(oneData.get("ORGANIZATIONNAME").toString());
                    res.setOrganizationName(oneData.get("ORGANIZATIONNAME").toString());


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
                    res.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));

                    res.setProcess_status(oneData.get("PROCESS_STATUS").toString());

                    for (Map<String, Object> oneData2 : getQDataDetail)
                    {
                        //过滤属于此单头的明细
                        if(differenceNO.equals(oneData2.get("DIFFERENCENO"))==false) continue;

                        DCP_DifferenceDetailQueryRes.Datas oneLv = res.new Datas();
                        String amt = oneData2.get("AMT").toString();
                        String bsName = oneData2.get("BSNAME").toString();
                        String bsNO = oneData2.get("BSNO").toString();
                        String item = oneData2.get("ITEM").toString();
                        String oItem = oneData2.get("OITEM").toString();
                        String pluName = oneData2.get("PLUNAME").toString();
                        String pluNO = oneData2.get("PLUNO").toString();
                        String pqty = oneData2.get("PQTY").toString();
                        String price = oneData2.get("PRICE").toString();
                        String punit = oneData2.get("PUNIT").toString();
                        String punitName = oneData2.get("PUNITNAME").toString();
                        String reqQty = oneData2.get("REQ_QTY").toString();
                        String stockOutQty = oneData2.get("STOCKOUTQTY").toString();
                        String stockInQty = oneData2.get("STOCKINQTY").toString();
                        String processMode = oneData2.get("PROCESSMODE").toString();
                        String baseQty = oneData2.get("BASEQTY").toString();
                        String baseUnit = oneData2.get("BASEUNIT").toString();
                        String baseUnitName = oneData2.get("BASEUNITNAME").toString();
                        String unitRatio = oneData2.get("UNITRATIO").toString();
                        String warehouseD = oneData2.get("WAREHOUSE").toString();
                        String spec = oneData2.get("SPEC").toString();
                        String listImage = oneData2.get("LISTIMAGE").toString();
                        if (!Check.Null(listImage)){
                            listImage = domainName+listImage;
                        }
                        String batchNO = oneData2.get("BATCH_NO").toString();
                        String isBatch = oneData2.get("ISBATCH").toString();
                        String prodDate = oneData2.get("PROD_DATE").toString();
                        String distriPrice = oneData2.get("DISTRIPRICE").toString();
                        String distriAmt = oneData2.get("DISTRIAMT").toString();
                        String featureNo = oneData2.get("FEATURENO").toString();
                        String featureName = oneData2.get("FEATURENAME").toString();
                        String transferBatchNo = oneData2.get("TRANSFERBATCHNO").toString();

                        oneLv.setAmt(amt);
                        oneLv.setBsName(bsName);
                        oneLv.setBsNo(bsNO);
                        oneLv.setItem(item);
                        oneLv.setOItem(oItem);
                        oneLv.setPluName(pluName);
                        oneLv.setPluNo(pluNO);
                        oneLv.setPqty(pqty);
                        oneLv.setPrice(price);
                        oneLv.setPunit(punit);
                        oneLv.setPunitName(punitName);
                        oneLv.setReqQty(reqQty);
                        oneLv.setReceiveDiffQty(reqQty);
                        oneLv.setStockOutQty(stockOutQty);
                        oneLv.setStockInQty(stockInQty);
                        oneLv.setProcessMode(processMode);
                        oneLv.setBaseUnit(baseUnit);
                        oneLv.setBaseUnitName(baseUnitName);
                        oneLv.setUnitRatio(unitRatio);
                        oneLv.setWarehouse(warehouseD);
                        oneLv.setSpec(spec);
                        oneLv.setListImage(listImage);
                        oneLv.setBatchNo(batchNO);
                        oneLv.setIsBatch(isBatch);
                        oneLv.setProdDate(prodDate);
                        oneLv.setDistriPrice(distriPrice);
                        oneLv.setDistriAmt(distriAmt);
                        oneLv.setBaseQty(baseQty);
                        oneLv.setFeatureNo(featureNo);
                        oneLv.setFeatureName(featureName);
                        oneLv.setTransferBatchNo(transferBatchNo);

                        res.getDatas().add(oneLv);
                        oneLv = null;
                    }
                    break;//只查询一条
                }
            }
            else
            {
                res.setDatas(new ArrayList<>());
            }

            return res;

        }catch (Exception e){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_DifferenceDetailQueryReq req) throws Exception
    {
        String langType = req.getLangType();
        String sql=null;
        StringBuffer sqlbuf=new StringBuffer("");
        DCP_DifferenceDetailQueryReq.levelElm request = req.getRequest();
        sqlbuf.append(" "
                + " SELECT differenceNO,bDate,memo,status,oType,ofNO,docType,loadDocType,loadDocNO,createDate,createTime,submitDate,"
                + " submitBy,submitTime,completeDate,totPqty,totAmt,TOT_DISTRIAMT,totCqty,item,oItem,pluNO,featureNo,FEATURENAME,"
                + " pqty,punit,BASEUNIT,baseUnitName,unitRatio,req_qty,"
                + " price,amt,DISTRIAMT,bsNO,processMode,bsName,pluName,punitName,"
                + " createby,createByName,stockOutQty,"
                + " stockInQty,warehouse,warehouseName,"
                + " process_status,update_time,modifyDate,modifyTime,modifyBy, "
                + " confirmBy,confirmDate,ConfirmTime,cancelBy,cancelDate,cancelTime, "
                + " accountBY,accountDate,accountTime,modifyByName,ConfirmByName, "
                + " cancelByName , submitByName,  accountByName,"
                + " LISTIMAGE,spec,transferShop,TRANSFERSHOPNAME,BATCH_NO,PROD_DATE,DISTRIPRICE,ISBATCH,baseqty,transferWarehouse,transferWarehouseName,invWarehouse,invWarehouseName, createType,transferbatchno," +
                "organizationno,organizationname  "
                + " from "
                + " ("
                + " SELECT a.differenceNO,a.bDate,a.memo,a.status,a.oType,a.ofNO,a.doc_type as docType,a.load_doctype as loadDocType,"
                + " a.load_docno as loadDocNO,a.create_date as createDate,a.create_time as createTime,a.submit_date as submitDate,a.submitBy, "
                + " a.submit_time as submitTime,a.complete_date as completeDate,a.tot_pqty as totPqty, a.tot_amt as totAmt,a.TOT_DISTRIAMT,"
                + " a.tot_cqty as totCqty, "
                + " b.item ,b.oitem as oItem,b.pluNO,b.pqty,b.punit,b.baseunit,b.unitRatio,b.reqQty as req_qty, "
                + " b.price,b.pqty*b.price as amt,b.DISTRIAMT,b.bsNO,g.reason_name as bsName,b.processMode, "
                + " d.plu_name as pluName,e.uname as punitname,e1.uname as baseunitName,a.createby,"
                + " f.name as createByName,b.stockOutQty,b.stockInQty, "
                + " a.warehouse, w.warehouse_name as warehouseName,a.process_status,a.update_time, "
                + " a.modify_Date  AS modifyDate ,  a.modify_time AS modifyTime  , a.modifyby , "
                + " a.confirmBy, a.confirm_Date AS  confirmDate , a.confirm_Time AS ConfirmTime , "
                + " a.cancelBy , a.cancel_date AS cancelDate , a.cancel_Time  AS  cancelTime  ,"
                + " a.accountby, a.account_date AS accountDate, a.account_time AS accountTime , "
                + " f1.name as modifyByName ,f2.name as cancelByName , f3.name as ConfirmByName , "
                + " f4.name as submitByName ,  f5.name as accountByName , "
                + " image.listimage, gul.spec,a.transfer_shop as transferShop, "
                + " b.BATCH_NO,b.PROD_DATE,b.DISTRIPRICE,c.ISBATCH,ORG_LANG.ORG_NAME AS TRANSFERSHOPNAME,"
                + " fn.featurename,b.featureno,b.transferBatchNo,b.baseqty,a.transfer_Warehouse as transferWarehouse,w1.warehouse_name as transferWarehouseName,a.invWarehouse,w2.warehouse_name as invWarehouseName, a.createType," +
                " o1.org_name as organizationname,a.organizationno "
                + " FROM DCP_DIFFERENCE a inner join ("
                + " select "
                + " eid,organizationno,shopid,differenceno, item ,oitem,pluno,punit, "
                + " baseunit,max(unitratio) as unitratio,sum(pqty) as pqty,sum(baseqty) as baseqty,"
                + " sum(reqqty) as reqqty,max(price) as price,max(bsno) as bsno, "
                + " sum(stockoutqty) as stockoutqty,max(processmode) as processmode, "
                + " sum(stockinqty) as stockinqty, "
                + " batch_no,prod_date, max(distriprice) as distriprice,sum(distriamt) as distriamt,featureno,transferBatchNo "
                + " from (  "
                + " select a.eid,a.organizationno,a.shopid,a.differenceno,b.item,b.oitem,b.pluno,nvl(b.pqty,0) as pqty,b.punit, "
                + " nvl(e.baseqty,0) as baseqty,"
                + " b.baseunit,b.unit_ratio as unitratio,b.req_qty as reqqty,b.price,b.amt,b.bsno,nvl(c.receiving_qty,0) as stockoutqty, "
                + " nvl(c.pqty,0) as stockinqty,b.process_mode as processmode,a.warehouse as warehouse,"
                + " b.batch_no,b.prod_date,b.distriprice,b.distriamt,b.featureno,b.TRANSFER_BATCHNO as transferBatchNo  "
                + " from dcp_difference a  "
                + " inner join dcp_difference_detail b on a.differenceno=b.differenceno and a.eid=b.eid and a.shopid=b.shopid  "
                + " left join dcp_stockin_detail c "
                + " on a.eid=c.eid and a.ofno=c.stockinno and a.shopid=c.shopid and b.pluno=c.pluno and b.oitem=c.item "
                + " left join dcp_adjust d on a.eid=d.eid and a.organizationno=d.organizationno and d.ofno=a.differenceno "
                + " left join dcp_adjust_detail e "
                + " on d.adjustno=e.adjustno and d.eid=e.eid and d.shopid=e.shopid and b.pluno=e.pluno "
                + " and (b.featureno=e.featureno or (trim(b.featureno) is null and trim(e.featureno) is null) )"
                + " where a.EID='"+req.geteId()+"'  "//and a.organizationno='"+req.getShopId()+"'
                + " and a.differenceno='"+request.getDifferenceNo()+"'    "
                + " ) group by EID,organizationno,SHOPID,differenceno,pluNO,BATCH_NO,PROD_DATE,punit,baseunit,item,oitem,featureno,transferBatchNo "
                + " ) b ON a.DIFFERENCENO=b.DIFFERENCENO and a.EID=b.EID and a.SHOPID=b.SHOPID "
                + " INNER JOIN DCP_GOODS c ON b.PLUNO=c.PLUNO AND b.EID=c.EID  "
                + " LEFT  JOIN DCP_GOODS_LANG  d ON b.PLUNO=d.PLUNO AND  b.EID=d.EID  AND d.LANG_TYPE='"+req.getLangType()+"' "
                + " LEFT  JOIN DCP_UNIT_LANG e ON b.PUNIT=e.UNIT AND b.EID=e.EID AND e.LANG_TYPE='"+req.getLangType()+"'  "
                + " LEFT  JOIN DCP_UNIT_LANG e1 ON b.baseunit=e1.UNIT AND b.EID=e1.EID AND e1.LANG_TYPE='"+req.getLangType()+"'  "
                + " LEFT JOIN dcp_employee f on a.EID=f.EID  and (a.createby=f.employeeno ) "
                + " LEFT JOIN dcp_employee f1 ON A .EID = f1.EID AND A .modifyBy = f1.employeeno  "
                + " LEFT JOIN dcp_employee f2 ON A .EID = f2.EID AND A .cancelby = f2.employeeno  "
                + " LEFT JOIN dcp_employee f3 ON A .EID = f3.EID AND A .confirmby = f3.employeeno  "
                + " LEFT JOIN dcp_employee f4 ON A .EID = f4.EID AND A .submitby = f4.employeeno  "
                + " LEFT JOIN dcp_employee f5 ON A .EID = f5.EID AND A .accountby = f5.employeeno "
                + " LEFT JOIN DCP_REASON_lang g on b.EID=g.EID and b.bsno=g.bsno and g.lang_type='"+req.getLangType()+"' "
                + " left join DCP_WAREHOUSE_lang w on a.warehouse=w.warehouse and b.EID=w.EID and b.ORGANIZATIONNO=w.ORGANIZATIONNO and w.lang_type='"+req.getLangType()+"' "

                + " left join DCP_WAREHOUSE_lang w1 on a.TRANSFER_WAREHOUSE=w1.warehouse and a.EID=w1.EID and a.TRANSFER_SHOP=w1.ORGANIZATIONNO and w1.lang_type='"+req.getLangType()+"' "
                + " left join DCP_WAREHOUSE_lang w2 on a.INVWAREHOUSE=w2.warehouse and a.EID=w2.EID and a.TRANSFER_SHOP=w2.ORGANIZATIONNO and w2.lang_type='"+req.getLangType()+"' "

                + " LEFT JOIN DCP_ORG_LANG ORG_LANG ON A.EID=ORG_LANG.EID AND A.TRANSFER_SHOP=ORG_LANG.ORGANIZATIONNO AND ORG_LANG.LANG_TYPE='"+req.getLangType()+"' "
                + " LEFT JOIN DCP_ORG_LANG o1 ON A.EID=o1.EID AND A.organizationno=o1.ORGANIZATIONNO AND o1.LANG_TYPE='"+req.getLangType()+"' "

                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno  and fn.lang_type='"+req.getLangType()+"' "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
                + " WHERE a.differenceno='"+request.getDifferenceNo()+"'   "
               // + " AND a.SHOPID='"+req.getShopId()+"' "
                + " AND a.EID='"+req.geteId()+"' "
                + " order by  status asc，bdate desc，differenceno desc,item "
                + " )");


        sql=sqlbuf.toString();

        return sql;
    }


}
