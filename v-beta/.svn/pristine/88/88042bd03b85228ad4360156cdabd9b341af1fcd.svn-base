package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StockTakeCheckReq;
import com.dsc.spos.json.cust.res.DCP_StockTakeCheckRes;
import com.dsc.spos.json.cust.res.DCP_StockTakeCheckRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * @apiNote 盘点新增检查
 * @author jinzma
 * @since  2021-07-13
 */
public class DCP_StockTakeCheck extends SPosBasicService<DCP_StockTakeCheckReq, DCP_StockTakeCheckRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockTakeCheckReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_StockTakeCheckReq> getRequestType() {
        return new TypeToken<DCP_StockTakeCheckReq>(){};
    }

    @Override
    protected DCP_StockTakeCheckRes getResponseType() {
        return new DCP_StockTakeCheckRes();
    }

    @Override
    protected DCP_StockTakeCheckRes processJson(DCP_StockTakeCheckReq req) throws Exception {
        DCP_StockTakeCheckRes res = this.getResponse();
        level1Elm oneLv1 = res.new level1Elm();
        try{
            StringBuffer errorMessage = new StringBuffer();
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String docNo="";
            String docType="";
            boolean isSuccess = true;
            String eId = req.geteId();
            String shopId = req.getShopId();
            String Is_Take_CreateCheck = PosPub.getPARA_SMS(dao, eId, shopId, "Is_Take_CreateCheck");
            if (Check.Null(Is_Take_CreateCheck) || Is_Take_CreateCheck.equals("N")){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参数:盘点单新增检查(Is_Take_CreateCheck)未开启!");
            }

            //以下注释,仅判断单据是否已提交,和调拨方式没关系 by jinzma 20210714
            /*String transferStock = PosPub.getPARA_SMS(dao, eId, shopId, "Transfer_Stock");
            if(Check.Null(transferStock)){
                transferStock="1";
            }*/
            //未确认（所有）+预计到货日单据
            if (Is_Take_CreateCheck.equals("W") || Is_Take_CreateCheck.equals("S")){
                //收货通知单预计已经到货未收货-配送收货
                String sql = " "
                        + " select a.receivingno,a.load_docno,a.doc_type,a.receiptdate from dcp_receiving a"
                        + " left join dcp_stockin b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.ofno"
                        + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='6' and a.doc_type in ('0','1')"
                        + " and (a.receiptdate<='"+sDate+"' or a.receiptdate is null)"
                        + " and b.stockinno is null"
                        + " order by a.doc_type,a.receivingno";
                List<Map<String, Object>> getQData = this.doQueryData(sql,null);
                if (getQData != null && getQData.isEmpty()==false) {
                    for (Map<String, Object> par : getQData) {
                        docNo   = par.get("LOAD_DOCNO").toString();
                        docType = par.get("DOC_TYPE").toString();
                        if (!Check.Null(docNo)){
                            if (docType.equals("0")) {
                                errorMessage.append("配送收货未收货,发货单号：" +docNo+"<br/>" );
                                isSuccess = false;
                            }
                            if (docType.equals("1")) {
                                errorMessage.append("调拨收货未收货,发货单号：" +docNo+"<br/>"  );
                                isSuccess = false;
                            }
                        }
                    }
                }

                // 2-统采直供通知
                sql= " "
                        + " select a.receivingno,a.load_docno,a.doc_type,a.receiptdate from dcp_receiving a"
                        + " left join dcp_sstockin b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.ofno"
                        + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='6' and a.doc_type='2'"
                        + " and (a.receiptdate <='"+sDate+"' or a.receiptdate is null)"
                        + " and b.sstockinno is null"
                        + " order by a.receivingno"
                        + " ";
                getQData.clear();
                getQData = this.doQueryData(sql,null);
                if (getQData != null && getQData.isEmpty()==false) {
                    for (Map<String, Object> par : getQData){
                        docNo = par.get("LOAD_DOCNO").toString();
                        if (!Check.Null(docNo) ) {
                            errorMessage.append("采购入库未收货,发货单号：" +docNo+"<br/>" ) ;
                            isSuccess = false;
                        }
                    }
                }
            }

            //0 配送收货 1调拨收货 3其他入库
            String sql= " select stockinno,doc_type from dcp_stockin "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by doc_type,stockinno " ;
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty()==false) {
                for (Map<String, Object> par : getQData) {
                    docType = par.get("DOC_TYPE").toString();
                    docNo = par.get("STOCKINNO").toString();
                    if (!Check.Null(docNo) && !Check.Null(docType)) {
                        if (docType.equals("0")) {
                            errorMessage.append("配送收货单保存未确认,单号：" +docNo+"<br/>"  );
                            isSuccess = false;
                        }
                        if (docType.equals("1")) {
                            errorMessage.append("调拨收货单保存未确认,单号：" +docNo+"<br/>"  );
                            isSuccess = false;
                        }
                        if (docType.equals("3")) {
                            errorMessage.append("其他入库单保存未确认,单号：" +docNo+"<br/>" );
                            isSuccess = false;
                        }
                    }
                }
            }

            //0-换季退货  1-调拨出库  2-次品退货 3-其他出库 4-移仓出库
            sql= " select stockoutno,doc_type,nvl(sourcemenu,0) as sourcemenu from dcp_stockout "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' "
                    + " and status='0' "
                    + " order by doc_type,sourcemenu,stockoutno ";

            //以下注释,仅判断单据是否已提交,和调拨方式没关系 by jinzma 20210714
            /*if (transferStock.equals("2")) {
                sql= " select stockoutno,doc_type,status from dcp_stockout "
                        + " where eid='"+eId+"' and shopid='"+shopId+"' "
                        + " and ( status='0' or ( doc_type='1' and status='2') )  "
                        + " order by doc_type,status,stockoutno ";
            }*/
            getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty()==false) {
                for (Map<String, Object> par : getQData) {
                    docType = par.get("DOC_TYPE").toString();
                    docNo = par.get("STOCKOUTNO").toString();
                    String sourceMenu = par.get("SOURCEMENU").toString();
                    if (Check.Null(sourceMenu)){
                        sourceMenu = "0";      //0其他出库单，1试吃出库，2赠送出库
                    }
                    if (!Check.Null(docNo) && !Check.Null(docType)) {
                        if (docType.equals("0") || docType.equals("2")) {
                            errorMessage.append("退货出库单保存未确认,单号：" +docNo+"<br/>")  ;
                            isSuccess = false;
                        }
                        if (docType.equals("1")) {
                            errorMessage.append("调拨出库单保存未确认,单号：" +docNo+"<br/>" ) ;
                            isSuccess = false;
                        }
                        if (docType.equals("4")) {
                            errorMessage.append("移仓出库单保存未确认,单号：" +docNo+"<br/>" ) ;
                            isSuccess = false;
                        }
                        if (docType.equals("3") && sourceMenu.equals("0")) {
                            errorMessage.append("其他出库单保存未确认,单号：" +docNo+"<br/>")  ;
                            isSuccess = false;
                        }
                        if (docType.equals("3") && sourceMenu.equals("1")) {
                            errorMessage.append("试吃出库单保存未确认,单号：" +docNo+"<br/>")  ;
                            isSuccess = false;
                        }
                        if (docType.equals("3") && sourceMenu.equals("2")) {
                            errorMessage.append("赠送出库单保存未确认,单号：" +docNo+"<br/>")  ;
                            isSuccess = false;
                        }
                    }
                }
            }

            //0-完工入库 1-组合单 2-拆解单 3-转换合并单 4-转换拆解单
            sql = " select doc_type,pstockinno from dcp_pstockin  "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by doc_type,pstockinno ";
            getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty()==false) {
                for (Map<String, Object> par : getQData) {
                    docType = par.get("DOC_TYPE").toString();
                    docNo = par.get("PSTOCKINNO").toString();
                    if (!Check.Null(docNo) && !Check.Null(docType)) {
                        if (docType.equals("0")) {
                            errorMessage.append("完工入库单保存未确认,单号：" +docNo+"<br/>" ) ;
                            isSuccess = false;
                        }
                        if (docType.equals("1")) {
                            errorMessage.append("组合单保存未确认,单号：" +docNo+"<br/>" ) ;
                            isSuccess = false;
                        }
                        if (docType.equals("2")) {
                            errorMessage.append("拆解单保存未确认,单号：" +docNo+"<br/>" ) ;
                            isSuccess = false;
                        }
                        if (docType.equals("3")) {
                            errorMessage.append("转换合并单保存未确认,单号：" +docNo+"<br/>" ) ;
                            isSuccess = false;
                        }
                        if (docType.equals("4")) {
                            errorMessage.append("转换拆解单保存未确认,单号：" +docNo+"<br/>" ) ;
                            isSuccess = false;
                        }
                    }
                }
            }

            //采购入库  1.自采 2.统采 3.门店直供
            sql = " select sstockinno,doc_type from dcp_sstockin"
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by doc_type,sstockinno ";
            getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty()==false) {
                for (Map<String, Object> par : getQData) {
                    //docType = par.get("DOC_TYPE").toString();
                    docNo = par.get("SSTOCKINNO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("采购入库单保存未确认,单号：" + docNo + "<br/>");
                        isSuccess = false;
                    }
                }
            }

            //采购退货
            sql = " select sstockoutno from dcp_sstockout "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by sstockoutno" ;
            getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty()==false) {
                for (Map<String, Object> par : getQData) {
                    docNo = par.get("SSTOCKOUTNO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("采购退货单保存未确认,单号：" +docNo+"<br/>")  ;
                        isSuccess = false;
                    }
                }
            }

            //报损单
            sql = " select lstockoutno from dcp_lstockout "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by lstockoutno " ;
            getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty()==false) {
                for (Map<String, Object> par : getQData) {
                    docNo = par.get("LSTOCKOUTNO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("报损单保存未确认,单号：" +docNo+"<br/>")  ;
                        isSuccess = false;
                    }
                }
            }

            //拼胚单
            sql = " select pinpeino from dcp_pinpei"
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and status='0' "
                    + " order by pinpeino " ;
            getQData = this.doQueryData(sql,null);
            if (getQData != null && getQData.isEmpty()==false) {
                for (Map<String, Object> par : getQData) {
                    docNo = par.get("PINPEINO").toString();
                    if (!Check.Null(docNo)) {
                        errorMessage.append("拼胚单保存未确认,单号：" +docNo+"<br/>")  ;
                        isSuccess = false;
                    }
                }
            }

            oneLv1.setMemo(errorMessage.toString());
            res.setDatas(oneLv1);
            return res;

        }catch (Exception e){
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_StockTakeCheckReq req) throws Exception {
        return null;
    }
}
