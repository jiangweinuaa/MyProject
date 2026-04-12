package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurOrderTrackGetReq;
import com.dsc.spos.json.cust.res.DCP_PurOrderTrackGetRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PurOrderTrackGet extends SPosBasicService<DCP_PurOrderTrackGetReq, DCP_PurOrderTrackGetRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurOrderTrackGetReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        //if(Check.Null(req.getRequest().getStatus())){
           // errMsg.append("收货状态不能为空！");
           // isFail = true;
        //}

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PurOrderTrackGetReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PurOrderTrackGetReq>(){};
    }

    @Override
    protected DCP_PurOrderTrackGetRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PurOrderTrackGetRes();
    }

    @Override
    protected DCP_PurOrderTrackGetRes processJson(DCP_PurOrderTrackGetReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_PurOrderTrackGetRes res = this.getResponse();
            int totalRecords;		//总笔数
            int totalPages;
            //单头查询
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            res.setDatas(new ArrayList<DCP_PurOrderTrackGetRes.level1Elm>());
            if (getQData != null && getQData.isEmpty() == false)
            {
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                //查询收货明细
                List<String> purOrderNos=new ArrayList();
                StringBuffer sJoinno=new StringBuffer("");
                for (Map<String, Object> oneData : getQData){
                    String purorderno = oneData.get("PURORDERNO").toString();
                    sJoinno.append(purorderno+",");
                    if(!purOrderNos.contains(purorderno)){
                        purOrderNos.add(purorderno);
                    }
                }
                Map<String, String> mapOrder=new HashMap<String, String>();
                mapOrder.put("PURORDERNO", sJoinno.toString());
                MyCommon cm=new MyCommon();
                String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

                if (withasSql_mono.equals(""))
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
                }

                String detailSql=getDetailSql(req, withasSql_mono);
                List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);

                //提取receivingno receivingitem
                StringBuffer sJoinReceivingNo=new StringBuffer("");
                StringBuffer sJoinReceivingItem=new StringBuffer("");
                for (Map<String, Object> oneData : getDetailData){

                    String receivingNo = oneData.get("RECEIVINGNO").toString();
                    String receivingItem = oneData.get("RECEIVINGITEM").toString();
                    if(Check.Null(receivingNo)||Check.Null(receivingItem)){
                        continue;
                    }
                    sJoinReceivingNo.append(receivingNo+",");
                    sJoinReceivingItem.append(receivingItem+",");

                }
                Map<String, String> mapROrder=new HashMap<String, String>();
                mapROrder.put("RECEIVINGNO", sJoinReceivingNo.toString());
                mapROrder.put("RECEIVINGITEM", sJoinReceivingItem.toString());

                String withasSql_mono_r=cm.getFormatSourceMultiColWith(mapROrder);
                List<Map<String, Object>> list1 = new ArrayList<>();
                List<Map<String, Object>> list2 = new ArrayList<>();

                if (!withasSql_mono_r.equals(""))
                {
                    String sStockInSql1 = this.getSStockInSql1(req, withasSql_mono_r);
                    String sStockInSql2 = this.getSStockInSql2(req, withasSql_mono_r);
                    list1 = this.doQueryData(sStockInSql1, null);
                    list2 = this.doQueryData(sStockInSql2, null);
                }


                for (Map<String, Object> oneData : getQData){
                    String purorderno = oneData.get("PURORDERNO").toString();
                    String receivingno = oneData.get("RECEIVINGNO").toString();
                    String receiveorgno = oneData.get("RECEIVEORGNO").toString();
                    String supplier = oneData.get("SUPPLIER").toString();
                    String suppliername = oneData.get("SUPPLIERNAME").toString();
                    String receiptdate = oneData.get("RECEIPTDATE").toString();
                    String expiredate = oneData.get("EXPIREDATE").toString();

                    List<Map<String, Object>> collects = getDetailData.stream()
                            .filter(var1 -> var1.get("PURORDERNO").toString().equals(purorderno)
                                    && var1.get("RECEIVINGNO").toString().equals(receivingno)).distinct()
                            .collect(Collectors.toList());
                    //品种数合计
                    List<Object> plunos = collects.stream().map(x -> {
                        Map map=new HashMap();
                        map.put("pluno",x.get("PLUNO").toString());
                        map.put("featureno",x.get("FEATURENO").toString());
                        return map;
                    }).distinct().collect(Collectors.toList());


                    DCP_PurOrderTrackGetRes.level1Elm level1Elm = res.new level1Elm();

                    level1Elm.setArrivalDate(receiptdate);
                    level1Elm.setPurOrderNo(purorderno);
                    level1Elm.setReceivingNO(receivingno);
                    level1Elm.setSupplier(supplier);
                    level1Elm.setSupplierName(suppliername);
                    level1Elm.setReceiveOrgno(receiveorgno);
                    level1Elm.setExpireDate(expiredate);

                    BigDecimal totPurQty=new BigDecimal(0);

                    //BigDecimal totcQty=new BigDecimal(0);
                    BigDecimal totpQty=new BigDecimal(0);
                    BigDecimal totrQty=new BigDecimal(0);
                    BigDecimal totsQty=new BigDecimal(0);
                    BigDecimal tot_non_pQty=new BigDecimal(0);
                    BigDecimal tot_non_sQty=new BigDecimal(0);
                    BigDecimal tot_non_rQty=new BigDecimal(0);

                    level1Elm.setDetail(new ArrayList<>());
                    List statusList=new ArrayList();//待收货、部分收货、收货结束
                    for (Map<String, Object> oneDetail : collects){
                        String doc_type = oneDetail.get("DOC_TYPE").toString();
                        String receivingitem = oneDetail.get("RECEIVINGITEM").toString();
                        List<Map<String, Object>> collect = new ArrayList<>();
                        if("3".equals(doc_type)){
                            collect = list1.stream().filter(x -> x.get("RECEIVINGNO").toString().equals(receivingno) && x.get("ITEM").toString().equals(receivingitem)).collect(Collectors.toList());
                        }else{
                            collect = list2.stream().filter(x -> x.get("RECEIVINGNO").toString().equals(receivingno) && x.get("ITEM").toString().equals(receivingitem)).collect(Collectors.toList());
                        }


                        DCP_PurOrderTrackGetRes.Detail detail = res.new Detail();

                        BigDecimal purqty = new BigDecimal(oneDetail.get("PURQTY").toString());
                        BigDecimal noticeqty = new BigDecimal(oneDetail.get("NOTICEQTY").toString());//通知量
                        BigDecimal rqty = new BigDecimal(oneDetail.get("RQTY").toString());//已收货量
                        BigDecimal sqty = new BigDecimal(0);
                        for (Map<String, Object> one : collect){
                            sqty = sqty.add(new BigDecimal(one.get("PQTY").toString()));
                        }

                        BigDecimal nonnoticeqty = purqty.subtract(noticeqty);
                        BigDecimal nonrqty = purqty.subtract(rqty);
                        BigDecimal nonsqty = purqty.subtract(sqty);

                        totPurQty = totPurQty.add(purqty);
                        totpQty = totpQty.add(noticeqty);
                        totrQty = totrQty.add(rqty);
                        totsQty = totsQty.add(sqty);

                        tot_non_pQty=tot_non_pQty.add(nonnoticeqty);
                        tot_non_rQty=tot_non_rQty.add(nonrqty);
                        tot_non_sQty=tot_non_sQty.add(nonsqty);


                        detail.setItem(oneDetail.get("ITEM").toString());
                        detail.setItem2(oneDetail.get("ITEM2").toString());
                        detail.setPluNo(oneDetail.get("PLUNO").toString());
                        detail.setPluName(oneDetail.get("PLUNAME").toString());
                        detail.setSpec(oneDetail.get("SPEC").toString());
                        detail.setFeatureNo(oneDetail.get("FEATURENO").toString());
                        detail.setFeatureName(oneDetail.get("FEATURENAME").toString());
                        detail.setPluBarCode(oneDetail.get("PLUBARCODE").toString());
                        detail.setPurUnit(oneDetail.get("PURUNIT").toString());
                        detail.setPurUnitName(oneDetail.get("PURUNITNAME").toString());
                        detail.setPurQty(oneDetail.get("PURQTY").toString());
                        detail.setNoticeQty(oneDetail.get("NOTICEQTY").toString());
                        detail.setRQty(oneDetail.get("RQTY").toString());
                        detail.setSQty(sqty.toString());
                        detail.setNon_noticeQty(nonnoticeqty.toString());
                        detail.setNon_rQty(nonrqty.toString());
                        detail.setNon_sQty(nonsqty.toString());
                        level1Elm.getDetail().add(detail);

                        if(rqty.compareTo(new BigDecimal(0))==0){
                            if(!statusList.contains(0)){
                                statusList.add(0);//待收货
                            }
                        }else {
                            int i = rqty.compareTo(purqty);
                            if(i<0){
                                if(!statusList.contains(1)){
                                    statusList.add(1);
                                }
                            }else {
                                if(!statusList.contains(2)){
                                    statusList.add(2);
                                }
                            }
                        }
                    }

                    if(statusList.size()>0) {
                        if (statusList.contains(0) || statusList.contains(1)) {
                            if (statusList.contains(1)) {
                                //部分收货
                                level1Elm.setReceiveStatus("1");
                            } else {
                                //待收货
                                level1Elm.setReceiveStatus("0");
                            }
                        } else {
                            //收货结束
                            level1Elm.setReceiveStatus("2");
                        }
                    }else{
                        level1Elm.setReceiveStatus("0");
                    }

                    level1Elm.setTotcQty(String.valueOf(plunos.size()));
                    level1Elm.setTotpQty(totpQty.toString());
                    level1Elm.setTotrQty(totrQty.toString());
                    level1Elm.setTotsQty(totsQty.toString());
                    level1Elm.setTot_non_sQty(tot_non_sQty.toString());
                    level1Elm.setTot_non_rQty(tot_non_rQty.toString());
                    level1Elm.setTot_non_pQty(tot_non_pQty.toString());

                    if(totPurQty.compareTo(new BigDecimal(0))!=0) {
                        BigDecimal divide = totrQty.divide(totPurQty, 4);
                        level1Elm.setRate(divide.toString());
                    }
                    level1Elm.setTotPurQty(totPurQty.toString());

                    res.getDatas().add(level1Elm);

                }

            }
            else
            {
                totalRecords = 0;
                totalPages = 0;
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }


    @Override
    protected String getQuerySql(DCP_PurOrderTrackGetReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
        DCP_PurOrderTrackGetReq.levelElm request = req.getRequest();
        String purOrderNo = request.getPurOrderNo();
        String supplier = request.getSupplier();
        String beginDate = request.getPurdate_e();
        String endDate = request.getPurdate_s();
        String receiveStatus = request.getStatus();
        String plu = request.getPlu();
        String status = request.getStatus();

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
        StringBuffer sqlbufDetail=new StringBuffer();
        sqlbufDetail.append(" "
                + " select a.eid,a.purorderno,f.item,sum(f.RECEIVEQTY) receiveqtysum " +
                "  from DCP_PURORDER a"
                + " left join DCP_PURORDER_DETAIL d on d.eid=a.eid and d.purorderno=a.purorderno "
                + " left join DCP_PURORDER_DELIVERY f on f.eid=d.eid and f.purorderno=d.purorderno and d.item=f.item "
                + " where  a.eid= '"+ eId +"'  "//and (f.RECEIVEQTY<d.PURQTY or f.RECEIVEQTY is null )
                + " "
                + " ");



        if(!Check.Null(purOrderNo)){
            sqlbufDetail.append(" and a.purorderno='"+purOrderNo+"'");
        }
        if(!Check.Null(supplier)){
            sqlbufDetail.append(" and a.supplier='"+supplier+"'");
        }


        if(!Check.Null(beginDate)){
            sqlbufDetail.append(" and a.bdate>='"+beginDate+"'");
        }
        if(!Check.Null(endDate)){
            sqlbufDetail.append(" and a.bdate<='"+endDate+"'");
        }

        sqlbufDetail.append(" group by a.eid,a.purorderno,f.item");

        //先查明细
        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,ac.* from (");
        sqlbuf.append(" select purorder.* from (");
        sqlbuf.append(" "
                + " select distinct a.*,b.sname as supplierName,nvl(i.receivingno,'') as receivingno,nvl(i.ORGANIZATIONNO,'') as receiveOrgNo,nvl(i.RECEIPTDATE,'') as RECEIPTDATE  " +
                "  from DCP_PURORDER a"
                + " left join DCP_PURORDER_DETAIL d on d.eid=a.eid and d.purorderno=a.purorderno "
                + " left join ("+sqlbufDetail.toString()+") f on f.eid=d.eid and f.purorderno=d.purorderno and d.item=f.item "
                + " left join dcp_bizpartner b  on b.BIZPARTNERNO=a.SUPPLIER and b.eid=a.eid  "//and a.organizationno=b.organizationno
                + " left join DCP_GOODS_BARCODE c on c.eid=d.eid and c.pluno=d.pluno "
                + " left join dcp_goods_lang e on c.pluno=e.pluno and c.eid=e.eid and e.lang_type='"+req.getLangType()+"'"
                + " left join DCP_PURORDER_DELIVERY g on g.eid=a.eid and g.purorderno=a.purorderno and g.item=d.item "
                + " left join DCP_RECEIVING_detail h on h.eid=a.eid and h.organizationno=a.organizationno and h.ofno=a.purorderno and h.oitem=g.item  "
                + " left join dcp_receiving i on i.eid=h.eid and i.organizationno=h.organizationno and h.receivingno=i.receivingno " +
                " where  a.eid= '"+ eId +"'  and a.status!='0' and a.status!='3'   "
                + " "
                + " ");


       // if(!Check.Null(status)){
           // sqlbuf.append(" and a.status='"+status+"'");
       // }

        if(!Check.Null(purOrderNo)){
            sqlbuf.append(" and a.purorderno='"+purOrderNo+"'");
        }
        if(!Check.Null(supplier)){
            sqlbuf.append(" and a.supplier='"+supplier+"'");
        }



        if(!Check.Null(beginDate)){
            sqlbuf.append(" and g.ARRIVALDATE>='"+beginDate+"'");
        }
        if(!Check.Null(endDate)){
            sqlbuf.append(" and g.ARRIVALDATE<='"+endDate+"'");
        }

        if(!Check.Null(receiveStatus)){
            //待收货、部分收货、收货结束
            if(receiveStatus.equals("0")){
                //待收货  已收货量=0
                sqlbuf.append(" and f.receiveqtysum=0 ");
            }
            if(receiveStatus.equals("1")){
                //部分收货 已收货量>0 且已收货量<采购量
                sqlbuf.append(" and f.receiveqtysum>0 and f.receiveqtysum<d.purqty  ");
            }
            if(receiveStatus.equals("2")){
                //收货结束  已收货量>0 且已收货量>=采购量
                //不存在以上两种情况
                sqlbuf.append(" and (f.purorderno is null or f.purorderno='' )");
            }

        }

        if(!Check.Null(plu)){
            sqlbuf.append(" and (c.pluno like '%%"+plu+"%%' or c.PLUBARCODE like '%%"+plu+"%%' or e.PLU_NAME like '%%"+plu+"%%')");
        }

        sqlbuf.append(" ) purorder");


        sqlbuf.append(" "
                + " order by purorder.purorderno"
                + " )ac"
                + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
                + " ");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_PurOrderTrackGetReq req,String withSql){
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
//        DCP_PurOrderTrackGetReq.levelElm request = req.getRequest();
//        String purOrderNo = request.getPurOrderNo();
//        String supplier = request.getSupplier();
//        String beginDate = request.getPurdate_e();
//        String endDate = request.getPurdate_s();
//        String receiveStatus = request.getStatus();
//        String plu = request.getPlu();
//
//        StringBuffer sqlbufDetail=new StringBuffer();
//        sqlbufDetail.append(" "
//                + " select a.eid,a.purorderno,d.item,sum(f.RECEIVEQTY) receiveqtysum " +
//                "  from DCP_PURORDER a"
//                + " left join DCP_PURORDER_DETAIL d on d.eid=a.eid and d.purorderno=a.purorderno "
//                + " left join DCP_PURORDER_DELIVERY f on f.eid=d.eid and f.purorderno=d.purorderno and d.item=f.item2 "
//                + " where  a.eid= '"+ eId +"' and (f.RECEIVEQTY<d.PURQTY or f.RECEIVEQTY is null )  "
//                + " "
//                + " ");
//
//
//        if(!Check.Null(purOrderNo)){
//            sqlbufDetail.append(" and a.purorderno='"+purOrderNo+"'");
//        }
//        if(!Check.Null(supplier)){
//            sqlbufDetail.append(" and a.supplier='"+supplier+"'");
//        }
//
//
//        if(!Check.Null(beginDate)){
//            sqlbufDetail.append(" and a.bdate>='"+beginDate+"'");
//        }
//        if(!Check.Null(endDate)){
//            sqlbufDetail.append(" and a.bdate<='"+endDate+"'");
//        }
//
//        sqlbufDetail.append(" group by a.eid,a.purorderno,f.item");

        sqlbuf.append("with p as ("+withSql+") "
                + " select distinct a.purorderno,l1.doc_type, g.item,g.item2,g.pluno,e.PLU_NAME as pluname,c.spec,g.featureno,j.featurename,d.plubarcode,k.unit as purunit,k.uname as purunitname," +
                "   nvl(g.purqty,0) as purqty,nvl(l.pqty,0) as noticeqty,nvl(g.RECEIVEQTY,0) as rqty,l.receivingno,l.item as receivingitem  " +
                "   from DCP_PURORDER a"
                + " inner join p on p.purorderno=a.purorderno "
                + " left join DCP_PURORDER_DETAIL d on d.eid=a.eid and a.organizationno=d.organizationno and d.purorderno=a.purorderno "
                //+ " left join ("+sqlbufDetail.toString()+") f on f.eid=d.eid and f.purorderno=d.purorderno and d.item=f.item "
                + " left join DCP_BIZPARTNER b  on b.BIZPARTNERNO=a.SUPPLIER and b.eid=a.eid   "
                + " left join DCP_GOODS_unit_lang c on c.eid=d.eid and c.pluno=d.pluno  and d.purunit=c.ounit and c.lang_type='"+req.getLangType()+"' "
                + " left join dcp_goods_lang e on d.pluno=e.pluno and d.eid=e.eid and e.lang_type='"+req.getLangType()+"'"
                + " left join DCP_PURORDER_DELIVERY g on g.eid=a.eid and g.purorderno=a.purorderno and g.item2=d.item and a.organizationno=g.organizationno "
               // + " left join DCP_RECEIVING h on h.eid=a.eid and h.load_docno=a.purorderno "
                + " left join dcp_goods i on i.eid=a.eid and i.pluno=d.pluno "
                + " left join DCP_GOODS_FEATURE_LANG j on j.eid=a.eid and j.pluno=g.pluno and j.featureno=g.featureno and j.lang_type='"+req.getLangType()+"'"
                + " left join DCP_UNIT_LANG k on k.eid=a.eid and k.unit=g.purunit and k.lang_type='"+req.getLangType()+"'"
                + " left join DCP_RECEIVING_DETAIL l on l.eid=a.eid and a.organizationno=l.organizationno and l.ofno=a.purorderno and l.oitem=g.item  "
                + " left join DCP_RECEIVING l1 on l1.eid=a.eid and l1.organizationno=a.organizationno and l1.receivingno=l.receivingno   "
                + " where  a.eid= '"+ eId +"'   order by purorderno,item "//and a.status='1'
                + " "
                + " ");


//
//        if(!Check.Null(purOrderNo)){
//            sqlbuf.append(" and a.purorderno='"+purOrderNo+"'");
//        }
//        if(!Check.Null(supplier)){
//            sqlbuf.append(" and a.supplier='"+supplier+"'");
//        }
//
//
//
//        if(!Check.Null(beginDate)){
//            sqlbuf.append(" and g.ARRIVALDATE>='"+beginDate+"'");
//        }
//        if(!Check.Null(endDate)){
//            sqlbuf.append(" and g.ARRIVALDATE<='"+endDate+"'");
//        }
//
//        if(!Check.Null(receiveStatus)){
//            //待收货、部分收货、收货结束
//            if(receiveStatus.equals("0")){
//                //待收货  已收货量=0
//                sqlbuf.append(" and f.receiveqtysum=0 ");
//            }
//            if(receiveStatus.equals("1")){
//                //部分收货 已收货量>0 且已收货量<采购量
//                sqlbuf.append(" and f.receiveqtysum>0 and f.receiveqtysum<d.purqty  ");
//            }
//            if(receiveStatus.equals("2")){
//                //收货结束  已收货量>0 且已收货量>=采购量
//                //不存在以上两种情况
//                sqlbuf.append(" and (f.purorderno is null or f.purorderno='' )");
//            }
//
//        }
//
//        if(!Check.Null(plu)){
//            sqlbuf.append(" and (c.pluno like '%%"+plu+"%%' or c.PLUBARCODE like '%%"+plu+"%%' or e.PLU_NAME like '%%"+plu+"%%')");
//        }

        return sqlbuf.toString();
    }

    private String getSStockInSql1(DCP_PurOrderTrackGetReq req,String withSql){
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("with p as ("+withSql+") " +
                " select a.receivingno,a.item,sum(nvl(c.pqty,0)) pqty from dcp_receiving_detail a " +
                " inner join p on p.receivingno=a.receivingno and p.receivingitem=a.item " +
                " left join DCP_PURRECEIVE_DETAIL b on a.eid=b.eid  and  a.receivingno=b.receivingno and a.item=b.receivingitem" +
                " left join  DCP_SSTOCKIN_DETAIL c on a.eid=c.eid  and  b.billno=c.ofno and b.item=c.oitem " +
                " where a.eid='"+req.geteId()+"' "+// and a.organizationno='"+req.getOrganizationNO()+"'
                "" +
                " group by a.receivingno,a.item ");

        return sqlbuf.toString();
    }

    private String getSStockInSql2(DCP_PurOrderTrackGetReq req,String withSql){
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("with p as ("+withSql+") " +
                " select a.receivingno,a.item,sum(nvl(c.pqty,0)) pqty from dcp_receiving_detail a " +
                " inner join p on p.receivingno=a.receivingno and p.receivingitem=a.item " +
                " left join  DCP_SSTOCKIN_DETAIL c on a.eid=c.eid  and  a.receivingno=c.ofno and a.item=c.oitem " +
                " where a.eid='"+req.geteId()+"'  "+ //and a.organizationno='"+req.getOrganizationNO()+"'
                "" +
                " group by a.receivingno,a.item ");

        return sqlbuf.toString();
    }

}
