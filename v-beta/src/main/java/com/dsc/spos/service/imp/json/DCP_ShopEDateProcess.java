package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_ShopEDateProcessReq;
import com.dsc.spos.json.cust.req.DCP_ShopEDateProcessReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ShopEDateProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_ShopEDateProcess extends SPosAdvanceService< DCP_ShopEDateProcessReq,DCP_ShopEDateProcessRes> {

    Logger logger = LogManager.getLogger(DCP_ShopEDateProcess.class.getName());

    //【ID1022157】【大万3.0】数据修复 9月27到10月22的库存异动流水都没有  by jinzma 20211118
    private static boolean shopEdateRun=false;   //日结是否正在运行中

    @Override
    protected void processDUID(DCP_ShopEDateProcessReq req, DCP_ShopEDateProcessRes res) throws Exception {

        //【ID1022157】【大万3.0】数据修复 9月27到10月22的库存异动流水都没有  by jinzma 20211118
        if (shopEdateRun){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "门店日结正在运行中,请稍后再操作");
        }
        shopEdateRun = true;
 
        String eType = req.getRequest().geteType();	 // 0-日结   1-反日结
        List<level1Elm> datas = req.getRequest().getDatas();
        boolean result;
        res.setDatas(new ArrayList<>());

        // 0-日结
        if (eType.equals("0")) {
            for (level1Elm par : datas) {
                //支持自动日结  BY JZMA 2019-08-01
                String oEId = par.getoEId();
                String oShopId=par.getoShopId();
                String edate = par.geteDate() ;
                StringBuilder errorMessage = new StringBuilder();
                DCP_ShopEDateProcessRes.level1Elm oneLv1 = new DCP_ShopEDateProcessRes().new level1Elm();
                result = eDateFun(oEId,oShopId,edate,errorMessage,req);
                oneLv1.setoEId(oEId);
                oneLv1.setoShopId(oShopId);
                if (result) {
                    oneLv1.setResult("Y");
                } else {
                    oneLv1.setResult("N");
                    oneLv1.setDescription(errorMessage.toString());
                }
                res.getDatas().add(oneLv1);
            }
        }
        //1-反日结
        if (eType.equals("1")) {
            for ( level1Elm par : datas ) {
                String oEId = par.getoEId();
                String oShopId=par.getoShopId();
                String edate = par.geteDate() ;
                StringBuilder errorMessage = new StringBuilder();
                DCP_ShopEDateProcessRes.level1Elm oneLv1 = new DCP_ShopEDateProcessRes().new level1Elm();
                result = unEdateFun(oEId,oShopId,edate,errorMessage);
                oneLv1.setoEId(oEId);
                oneLv1.setoShopId(oShopId);
                if (result) {
                    oneLv1.setResult("Y");
                } else {
                    oneLv1.setResult("N");
                    oneLv1.setDescription(errorMessage.toString());
                }
                res.getDatas().add(oneLv1);
            }
        }

        shopEdateRun = false;
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ShopEDateProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShopEDateProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShopEDateProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ShopEDateProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String eType = req.getRequest().geteType();
        if(Check.Null(eType)){
            errMsg.append("日结类型不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        List <level1Elm> datas = req.getRequest().getDatas() ;
        for ( level1Elm par : datas ) {
            String oEId = par.getoEId();
            String oShopId = par.getoShopId();
            String eDate = par.geteDate();

            if(Check.Null(oEId)){
                errMsg.append("企业编号不可为空值, ");
                isFail = true;
            }
            if(Check.Null(oShopId)){
                errMsg.append("门店编号不可为空值, ");
                isFail = true;
            }
            if(Check.Null(eDate)){
                errMsg.append("日结日期不可为空值, ");
                isFail = true;
            }
            if (isFail){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }

        return false;

    }

    @Override
    protected TypeToken<DCP_ShopEDateProcessReq> getRequestType() {
        return new TypeToken<DCP_ShopEDateProcessReq>(){};
    }

    @Override
    protected DCP_ShopEDateProcessRes getResponseType() {
        return new DCP_ShopEDateProcessRes();
    }

    private boolean unEdateFun( String eId, String shopId,String edate,StringBuilder errorMessage) {
        try {
            String sql=this.GetMaxEdate_SQL(eId,shopId);
            List<Map<String, Object>> getQData=this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()) {
                String maxEdate= getQData.get(0).get("EDATE").toString();
                if (Check.Null(maxEdate)) {
                    errorMessage.append("不存在日结资料，反日结失败 ");
                    return false;
                }


                //反日结日期不能大于日结日期
                if (edate.compareTo(maxEdate)>0) {
                    errorMessage.append("反日结日期必须小于等于"+maxEdate+" ");
                    return false;
                }

                //2.0导入到3.0之后，只有日结档没有库存流水档，不允许反日结
                String minEdate= getQData.get(0).get("MINEDATE").toString();
                if (edate.compareTo(minEdate)==0){
                    sql=" select count(*) as num from dcp_stock_detail_static a where a.eid='"+eId+"' and a.organizationno='"+shopId+"' ";
                    List<Map<String, Object>> getNumQData=this.doQueryData(sql,null);
                    if (getNumQData.get(0).get("NUM").toString().equals("0")){
                        errorMessage.append("反日结日期必须大于"+minEdate+" ,只有日结档没有库存流水档，不允许反日结 ");
                        return false;
                    }
                }

                //********************开始反日结**************************
                //*******************************************************
                //*******************1.还原库存流水账*********************
                String sDCP_STOCK_DETAILSQL=this.GetDCP_STOCK_DETAIL_STATIC_SQL(eId,shopId,edate);
                List<Map<String, Object>> getDCP_STOCK_DETAIL=this.doQueryData(sDCP_STOCK_DETAILSQL,null);
                if (getDCP_STOCK_DETAIL != null && !getDCP_STOCK_DETAIL.isEmpty()) {
                    for (Map<String, Object> par : getDCP_STOCK_DETAIL) {
                        String[] columnsDCP_STOCK_DETAIL = {
                                "SERIALNO","EID","ORGANIZATIONNO","BILLTYPE","BILLNO",
                                "ITEM","STOCKTYPE","BDATE","PLUNO","FEATURENO",
                                "WAREHOUSE","BATCHNO","PRODDATE","UNIT","QTY",
                                "BASEUNIT","BASEQTY","UNITRATIO","PRICE","AMT",
                                "DISTRIPRICE","DISTRIAMT","ACCOUNTDATE","LASTMODIOPID","LASTMODIOPNAME",
                                "LASTMODITIME","CREATEOPID","CREATEOPNAME","CREATETIME","BTYPE","COSTCODE"
                        };

                        String featureNo= par.get("FEATURENO").toString();
                        if (Check.Null(featureNo))
                            featureNo=" ";

                        DataValue[]	columnsVal3 = new DataValue[]{
                                new DataValue(par.get("SERIALNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("EID").toString(), Types.VARCHAR),
                                new DataValue(par.get("ORGANIZATIONNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("BILLTYPE").toString(), Types.VARCHAR),
                                new DataValue(par.get("BILLNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("ITEM").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKTYPE").toString(), Types.VARCHAR),
                                new DataValue(par.get("BDATE").toString(), Types.DATE),
                                new DataValue(par.get("PLUNO").toString(), Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(par.get("WAREHOUSE").toString(), Types.VARCHAR),
                                new DataValue(par.get("BATCHNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("PRODDATE").toString(), Types.VARCHAR),
                                new DataValue(par.get("UNIT").toString(), Types.VARCHAR),
                                new DataValue(par.get("QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("BASEUNIT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BASEQTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("UNITRATIO").toString(), Types.VARCHAR),
                                new DataValue(par.get("PRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ACCOUNTDATE").toString(), Types.DATE),
                                new DataValue(par.get("LASTMODIOPID").toString(), Types.VARCHAR),
                                new DataValue(par.get("LASTMODIOPNAME").toString(), Types.VARCHAR),
                                new DataValue(par.get("LASTMODITIME").toString(), Types.DATE),
                                new DataValue(par.get("CREATEOPID").toString(), Types.VARCHAR),
                                new DataValue(par.get("CREATEOPNAME").toString(), Types.VARCHAR),
                                new DataValue(par.get("CREATETIME").toString(), Types.DATE),
                                new DataValue(par.get("BTYPE").toString(), Types.VARCHAR),
                                new DataValue(par.get("COSTCODE").toString(), Types.VARCHAR),
                        };

                        InsBean ib3 = new InsBean("DCP_STOCK_DETAIL", columnsDCP_STOCK_DETAIL);
                        ib3.addValues(columnsVal3);
                        this.addProcessData(new DataProcessBean(ib3));
                    }

                    //删除操作DCP_STOCK_DETAIL_STATIC
                    //					DelBean db1 = new DelBean("DCP_STOCK_DETAIL_STATIC");
                    //					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    //					db1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                    //					db1.addCondition("ACCOUNT_DATE", new DataValue(edate, Types.VARCHAR,DataExpression.GreaterEQ));
                    //					this.addProcessData(new DataProcessBean(db1));

                    sql =" delete from dcp_stock_detail_static"
                            + " where eid='"+eId+"' and organizationno='"+shopId+"' "
                            + " and to_char(accountdate,'yyyymmdd')>='"+edate+"' ";
                    ExecBean exec = new ExecBean(sql);
                    this.addProcessData(new DataProcessBean(exec));

                }


                //*******************************************************
                //*******************2.还原日库存档***********************
                //先删除DCP_STOCK_DAY 再插入DCP_STOCK_DAY
                DelBean db1 = new DelBean("DCP_STOCK_DAY");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //再查询要插入的数据
                String sSourceSQL=this.GetDCP_STOCK_DAY_STATIC_SQL(eId,shopId,edate);
                List<Map<String, Object>> getSource=this.doQueryData(sSourceSQL,null);
                if (getSource != null && !getSource.isEmpty()) {
                    for (Map<String, Object> par : getSource) {
                        String[] columnsDCP_STOCK_DAY = {
                                "EID","ORGANIZATIONNO","WAREHOUSE","EDATE","PLUNO","STOCKOUT_QTY","TRANSFEROUT_QTY",
                                "TRANSFERIN_AMT","PSTOCKOUT_AMT","PRICE","RDIFF_QTY","LOSS_AMT","SSTOCKOUT_QTY","STOCKIN_AMT",
                                "SALE_AMT","MATERIAL_AMT","STOCKOUT_AMT","BEGIN_PRICE","BEGIN_QTY","ADJUST_AMT","TRANSFEROUT_AMT",
                                "QTY","TDIFF_QTY","AMT","SSTOCKIN_QTY","OTHERIN_QTY","OTHEROUT_QTY","BASEUNIT",
                                "TRANSFERWAREOUT_AMT","TDIFF_AMT","STOCKIN_QTY","TRANSFERWAREOUT_QTY","ONORDER_AMT","SSTOCKOUT_AMT",
                                "OTHERIN_AMT","DIFF_AMT","TRANSFERWAREIN_AMT","PSTOCKIN_AMT","SALE_QTY","TRANSFERWAREIN_QTY","MATERIAL_QTY",
                                "PSTOCKIN_QTY","ONORDER_QTY","SSTOCKIN_AMT","LOSS_QTY","TRANSFERIN_QTY","OTHEROUT_AMT","PSTOCKOUT_QTY",
                                "ADJUST_QTY","RDIFF_AMT","DIFF_QTY","BEGIN_AMT","IN_TRANSIT_ADD_QTY","IN_TRANSIT_ADD_AMT",
                                "IN_TRANSIT_MINUS_QTY","IN_TRANSIT_MINUS_AMT","COMBIN_QTY","COMBIN_AMT","COMBIN_MATERIAL_QTY",
                                "COMBIN_MATERIAL_AMT","DISASS_QTY","DISASS_AMT","DISASS_MATERIAL_QTY","DISASS_MATERIAL_AMT",
                                "LDIFF_QTY","LDIFF_AMT","ZHHB_QTY","ZHHB_AMT","ZHHB_MATERIAL_QTY","ZHHB_MATERIAL_AMT","BATCHNO","PRODDATE",
                                "DISTRIPRICE","ZHCJ_QTY","ZHCJ_AMT","ZHCJ_MATERIAL_QTY","ZHCJ_MATERIAL_AMT","JFDH_QTY","JFDH_AMT",
                                "JFDH_CANCEL_QTY","JFDH_CANCEL_AMT","FEATURENO",
                                "KCZZS_QTY","KCZZS_AMT","KCZZS_CANCEL_QTY","KCZZS_CANCEL_AMT",
                                "TRANSFERIN_DISTRIAMT","STOCKIN_DISTRIAMT","STOCKOUT_DISTRIAMT","TRANSFEROUT_DISTRIAMT","SSTOCKIN_DISTRIAMT",
                                "SSTOCKOUT_DISTRIAMT","LOSS_DISTRIAMT","PSTOCKIN_DISTRIAMT","DIFF_DISTRIAMT","ADJUST_DISTRIAMT",
                                "MATERIAL_DISTRIAMT","IN_TRANSIT_ADD_DISTRIAMT","IN_TRANSIT_MINUS_DISTRIAMT","OTHERIN_DISTRIAMT","OTHEROUT_DISTRIAMT",
                                "TDIFF_DISTRIAMT","RDIFF_DISTRIAMT","TRANSFERWAREOUT_DISTRIAMT","TRANSFERWAREIN_DISTRIAMT","SALE_DISTRIAMT",
                                "ONORDER_DISTRIAMT","COMBIN_DISTRIAMT","COMBIN_MATERIAL_DISTRIAMT","DISASS_DISTRIAMT","DISASS_MATERIAL_DISTRIAMT",                            
                                "LDIFF_DISTRIAMT","ZHHB_DISTRIAMT","ZHHB_MATERIAL_DISTRIAMT","ZHCJ_DISTRIAMT","ZHCJ_MATERIAL_DISTRIAMT",  
                                "JFDH_DISTRIAMT","JFDH_CANCEL_DISTRIAMT","KCZZS_DISTRIAMT","KCZZS_CANCEL_DISTRIAMT","DISTRIAMT","BEGIN_DISTRIAMT"
                        };

                        String featureNo = par.get("FEATURENO").toString();
                        if (Check.Null(featureNo))
                            featureNo=" ";

                        DataValue[]	insValue3 = new DataValue[]{
                                new DataValue(par.get("EID").toString(), Types.VARCHAR),
                                new DataValue(par.get("ORGANIZATIONNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("WAREHOUSE").toString(), Types.VARCHAR),
                                new DataValue(par.get("EDATE").toString(), Types.VARCHAR),
                                new DataValue(par.get("PLUNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_PRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("BASEUNIT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ONORDER_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ONORDER_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("LDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BATCHNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("PRODDATE").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_AMT").toString(), Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(par.get("KCZZS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_AMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("TRANSFERIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("SSTOCKOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_DISTRIAMT").toString(), Types.VARCHAR),

                                new DataValue(par.get("MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("TDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("ONORDER_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),                         

                                new DataValue(par.get("LDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),

                                new DataValue(par.get("JFDH_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_DISTRIAMT").toString(), Types.VARCHAR),
                        };

                        //插入DCP_STOCK_DAY
                        InsBean ib3 = new InsBean("DCP_STOCK_DAY", columnsDCP_STOCK_DAY);
                        ib3.addValues(insValue3);
                        this.addProcessData(new DataProcessBean(ib3));
                    }
                }


                //再删日结DCP_STOCK_DAY_STATIC   >=反日结日期
                DelBean db2 = new DelBean("DCP_STOCK_DAY_STATIC");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("EDATE", new DataValue(edate , Types.VARCHAR,DataExpression.GreaterEQ));
                this.addProcessData(new DataProcessBean(db2));

                //***********************************************************************
                //4.2************删除DCP_SETTLEMENT_DETAIL 此表没有日期栏位，通主表关系删除***************************************
                //***********************************************************************
                //				DelBean db42 = new DelBean("DCP_SETTLEMENT_DETAIL");
                //				db42.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                //				db42.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                //				db42.addCondition("SETTLEMENTNO", new DataValue(" (select SETTLEMENTNO from DCP_SETTLEMENT where EDATE>='"+edate+"' and EID='"+eId+"' and SHOPID='"+shopId+"') " , Types.VARCHAR,DataExpression.IN));
                //				this.addProcessData(new DataProcessBean(db42));


                //***********************************************************************
                //4.3************删除DCP_SETTLEMENT_TAX 此表没有日期栏位，通主表关系删除***************************************
                //***********************************************************************
                //				DelBean db43 = new DelBean("DCP_SETTLEMENT_TAX");
                //				db43.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                //				db43.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                //				db43.addCondition("SETTLEMENTNO", new DataValue(" (select SETTLEMENTNO from DCP_SETTLEMENT where EDATE>='"+edate+"' and EID='"+eId+"' and SHOPID='"+shopId+"') " , Types.VARCHAR,DataExpression.IN));
                //				this.addProcessData(new DataProcessBean(db43));


                //***********************************************************************
                //4.4************删除DCP_SETTLEMENT_AGIO 此表没有日期栏位，通主表关系删除***************************************
                //***********************************************************************
                //				DelBean db44 = new DelBean("DCP_SETTLEMENT_AGIO");
                //				db44.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                //				db44.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                //				db44.addCondition("SETTLEMENTNO", new DataValue(" (select SETTLEMENTNO from DCP_SETTLEMENT where EDATE>='"+edate+"' and EID='"+eId+"' and SHOPID='"+shopId+"') " , Types.VARCHAR,DataExpression.IN));
                //				this.addProcessData(new DataProcessBean(db44));
                //

                //***********************************************************************
                //4.5************删除DCP_SETTLEMENT_PAY 此表没有日期栏位，通主表关系删除***************************************
                //***********************************************************************
                //				DelBean db45 = new DelBean("DCP_SETTLEMENT_PAY");
                //				db45.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                //				db45.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                //				db45.addCondition("SETTLEMENTNO", new DataValue(" (select SETTLEMENTNO from DCP_SETTLEMENT where EDATE>='"+edate+"' and EID='"+eId+"' and SHOPID='"+shopId+"') " , Types.VARCHAR,DataExpression.IN));
                //				this.addProcessData(new DataProcessBean(db45));


                //***********************************************************************
                //4.1************删除DCP_SETTLEMENT***************************************
                //***********************************************************************
                //				DelBean db41 = new DelBean("DCP_SETTLEMENT");
                //				db41.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                //				db41.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                //				db41.addCondition("EDATE", new DataValue(edate , Types.VARCHAR,DataExpression.GreaterEQ));
                //				this.addProcessData(new DataProcessBean(db41));

                //执行
                this.doExecuteDataToDB();

                return true;
            } else {
                errorMessage.append("不存在日结资料，反日结失败 ");
                return false;
            }
        } catch (Exception e) {
            this.pData.clear();
            errorMessage.append(e.getMessage());
            return false;
        }

    }

    private boolean eDateFun( String eId, String shopId,String edate,StringBuilder errorMessage,DCP_ShopEDateProcessReq req) {
        try {
            //String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            //String sTime = new SimpleDateFormat("HHmmss").format(new Date());


            //是否重计库存  by jinzma 20210914
            String isRecoverStock = PosPub.getPARA_SMS(dao, eId, "", "isRecoverStock");
            if (Check.Null(isRecoverStock)){
                isRecoverStock = "N";
            }

            //获取流水帐最小入账日期
            String minStockDetailDate="";
            String sqlHandminStockdetailDate=this.GetHandMinStock_Detail_Date_SQL(eId,shopId);
            List<Map<String, Object>> getHandminStockdetailDate=this.doQueryData(sqlHandminStockdetailDate,null);
            if (getHandminStockdetailDate != null && !getHandminStockdetailDate.isEmpty()) {
                minStockDetailDate=getHandminStockdetailDate.get(0).get("ACCOUNTDATE").toString();
            }

            //获取日结档最大日结日期
            String maxEdate="";
            String sqlHandMaxEDate=this.GetMaxEdate_SQL(eId,shopId);
            List<Map<String, Object>> getHandMaxEDate=this.doQueryData(sqlHandMaxEDate,null);
            if (getHandMaxEDate != null && !getHandMaxEDate.isEmpty()) {
                maxEdate = getHandMaxEDate.get(0).get("EDATE").toString();
            }

            /*
             * 日结逻辑整体说明：
             * 如果查询出来的最大日结日期为空，则先日结流水帐最小日期，直到日结到传入的日结日期
             * 如果查询出来的最大日结日期有值 则从查询出来的最大日结日期+1开始日结  直到日结到传入的日结日期
             */

            //日结档与流水档同时为空跳过日结
            if (Check.Null(minStockDetailDate)&& Check.Null(maxEdate)) {
                errorMessage.append("日结档与库存流水档没有查询到资料") ;
                return false ;
            }
            String startEdate;

            //日结档有资料，日结开始日期=最大日结日期+1天    ELSE 日结开始日期=流水帐最小入账日期
            if  (!Check.Null(maxEdate)) {
                startEdate=PosPub.GetStringDate(maxEdate,1);
            } else {
                startEdate=minStockDetailDate;
            }
            //POS销售闭店检查************************************//是否检查闭店表记录
            String Is_Check_CloseShop=PosPub.getPARA_SMS(dao, eId, shopId, "Is_Check_CloseShop");

            //启用自动闭店,启用就不再需要判断是否检查闭店记录  Y/N
            String AllowCloseShop = PosPub.getPARA_SMS(dao, eId, shopId, "AllowCloseShop");
            if (Check.Null(AllowCloseShop)){
                AllowCloseShop = "N";
            }


            //【ID1022996】【冠生园3.0】门店没有中午闭店功能 by jinzma 20220105
            String para_AccountDate = PosPub.getAccountDate_SMS(dao,eId,shopId);

            //从开始日结日期一直日结到当前日结日期
            while (startEdate.compareTo(edate)<=0) {

                //【ID1024960】【戴氏3.0】中台门店日结，反日结按钮灰色，不能点
                if (PosPub.compare_date(startEdate,para_AccountDate) >= 0) {
                    errorMessage.append("日结日期不能大于或等于系统入账日期") ;
                    return false ;
                }

                //必须检查闭店记录
                //【ID1031771】【货郎3.2.0.3】客户很多门店没日结，提示报表，看了门店pos是闭店的  by jinzma 20230313
                if(Is_Check_CloseShop.equals("Y") && !AllowCloseShop.equals("Y")) {
                    String sCloseshopDate=" select edate from dcp_close_shop "
                            //【ID1027760】 [荷家3.0]日结报错如图  by jinzma 20220809  日期闭店日期判断优化，改成 >=
                            + " where eid='"+eId+"' and shopid='"+shopId+"' and substr(edate,1,8)>='"+startEdate+"' "
                            + " ";
                    List<Map<String, Object>> getCloseshopDate=this.doQueryData(sCloseshopDate,null);
                    if (getCloseshopDate == null || getCloseshopDate.isEmpty()) {
                        //【ID1006693】	中台日结优化  客户门店假期会关闭，关闭期间就不会有闭店信息   by JZMA 20200316
                        sCloseshopDate = " "
                                + " select substr(bdate,1,8) bdate from dcp_order "
                                + " where eid='"+eId+"' and  shop='"+shopId+"' "
                                + " and substr(bdate,1,8) = '"+startEdate+"'  "
                                + " and rownum<=1 "
                                + " union all "
                                + " select bdate from dcp_sale "
                                + " where eid='"+eId+"' and  shopid='"+shopId+"' "
                                + " and bdate = '"+startEdate+"' "
                                + " and rownum<=1 ";

                        getCloseshopDate=this.doQueryData(sCloseshopDate,null);
                        if (getCloseshopDate != null && !getCloseshopDate.isEmpty()) {
                            //errorMessage.append("【日期："+startEdate+"】闭店检查表DCP_CLOSE_SHOP无对应日期记录但销售单或订单有记录  SQL=" + sCloseshopDate) ;
                            errorMessage.append("【日期："+startEdate+"】闭店检查表DCP_CLOSE_SHOP无对应记录但销售单或订单有记录" ) ;
                            return false ;
                        }
                    }
                }
                //startEdate-1天
                String sLitDay=PosPub.GetStringDate(startEdate, -1);
                //!!!!!!!!!!!!!!!!日结档异常处理:!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //!!!!!!!!!!!!!!!!昨日日结档记录数必须大于等于前一日的!!!!!!!!

                //日结档为空且日结开始日期=流水帐最小入账日期 则不进行异常处理，批量日结时只第一天不进入
                if (!(maxEdate == null && startEdate.equals(minStockDetailDate))){
                    //日结日期
                    String sqlMaxEdate=this.GetExceptionEDate_SQL(eId, shopId, startEdate,"0");
                    List<Map<String, Object>> getSqlMaxEdate=this.doQueryData(sqlMaxEdate,null);
                    if (getSqlMaxEdate != null && !getSqlMaxEdate.isEmpty()) {
                        //日结日期-1
                        String sqlMaxEdateSub1=this.GetExceptionEDate_SQL(eId, shopId, PosPub.GetStringDate(startEdate, -1),"1");
                        List<Map<String, Object>> getSqlMaxEdateSub1=this.doQueryData(sqlMaxEdateSub1,null);
                        if (getSqlMaxEdateSub1 != null && !getSqlMaxEdateSub1.isEmpty()) {
                            int intEdateSub1=Integer.parseInt(getSqlMaxEdateSub1.get(0).get("NUM").toString());
                            int intEdate=Integer.parseInt(getSqlMaxEdate.get(0).get("NUM").toString());
                            if (intEdateSub1>intEdate) {
                                //errorMessage.append("【日期："+startEdate+"】日结档记录数必须大于等于前一日的记录SQL=" +sqlMaxEdate) ;
                                errorMessage.append("【日期："+startEdate+"】日结档记录数必须大于等于前一日的记录") ;
                                return false ;
                            }
                        }

                    }
                }
                //
                updateStockdetailPriceProcess(eId,shopId,startEdate);
                //********************************
                //==============日结动作==========
                //1.日结档(插入DCP_stock_day、DCP_stock_day_static，同时删除DCP_stock_day表的前一日的日结记录)
                String sSourceSQL=this.GetEDate_SQL(eId, shopId,startEdate);
                //日结查询超时时间调整为2分钟  BY JZMA 20200107
                PosPub.iTimeoutTime=120;
                List<Map<String, Object>> getSource=this.doQueryData(sSourceSQL,null);
                //PosPub.iTimeoutTime=30;

                if (getSource != null && !getSource.isEmpty()) {
                    for (Map<String, Object> par : getSource) {

                        String[] columnsDCP_STOCK_DAY = {
                                "EID","ORGANIZATIONNO","WAREHOUSE","EDATE","PLUNO","BASEUNIT","BEGIN_QTY","BEGIN_AMT",
                                "BEGIN_PRICE","STOCKIN_QTY","STOCKIN_AMT","STOCKOUT_QTY","STOCKOUT_AMT","SSTOCKIN_QTY",
                                "SSTOCKIN_AMT","SSTOCKOUT_QTY","SSTOCKOUT_AMT","PSTOCKIN_QTY","PSTOCKIN_AMT","SALE_QTY",
                                "SALE_AMT","MATERIAL_QTY","MATERIAL_AMT","TRANSFEROUT_QTY","TRANSFEROUT_AMT","TRANSFERIN_QTY",
                                "TRANSFERIN_AMT","LOSS_QTY","LOSS_AMT","DIFF_QTY","DIFF_AMT","ADJUST_QTY",
                                "ADJUST_AMT","ONORDER_QTY","ONORDER_AMT","QTY","AMT","PRICE",
                                "OTHERIN_QTY","OTHERIN_AMT","OTHEROUT_QTY","OTHEROUT_AMT", //"PSCRAP_QTY","PSCRAP_AMT",
                                "RDIFF_QTY","RDIFF_AMT","TDIFF_QTY","TDIFF_AMT","TRANSFERWAREOUT_QTY","TRANSFERWAREOUT_AMT",
                                "TRANSFERWAREIN_QTY","TRANSFERWAREIN_AMT","IN_TRANSIT_ADD_QTY","IN_TRANSIT_ADD_AMT",
                                "IN_TRANSIT_MINUS_QTY","IN_TRANSIT_MINUS_AMT","COMBIN_QTY","COMBIN_AMT",
                                "COMBIN_MATERIAL_QTY","COMBIN_MATERIAL_AMT","DISASS_QTY","DISASS_AMT",
                                "DISASS_MATERIAL_QTY","DISASS_MATERIAL_AMT","LDIFF_QTY","LDIFF_AMT" , "ZHHB_QTY","ZHHB_AMT",
                                "ZHHB_MATERIAL_QTY","ZHHB_MATERIAL_AMT",
                                //by jzma 20190731
                                "BATCHNO","PRODDATE","DISTRIPRICE",
                                //by jzma 20200729 3.0新增
                                "ZHCJ_QTY","ZHCJ_AMT","ZHCJ_MATERIAL_QTY","ZHCJ_MATERIAL_AMT",
                                "JFDH_QTY","JFDH_AMT","JFDH_CANCEL_QTY","JFDH_CANCEL_AMT",
                                "FEATURENO","KCZZS_QTY","KCZZS_AMT","KCZZS_CANCEL_QTY","KCZZS_CANCEL_AMT",
                                //by tianhq 20240226 add
                                "TRANSFERIN_DISTRIAMT","STOCKIN_DISTRIAMT","STOCKOUT_DISTRIAMT","TRANSFEROUT_DISTRIAMT","SSTOCKIN_DISTRIAMT",
                                "SSTOCKOUT_DISTRIAMT","LOSS_DISTRIAMT","PSTOCKIN_DISTRIAMT","DIFF_DISTRIAMT","ADJUST_DISTRIAMT",
                                "MATERIAL_DISTRIAMT","IN_TRANSIT_ADD_DISTRIAMT","IN_TRANSIT_MINUS_DISTRIAMT","OTHERIN_DISTRIAMT","OTHEROUT_DISTRIAMT",
                                "TDIFF_DISTRIAMT","RDIFF_DISTRIAMT","TRANSFERWAREOUT_DISTRIAMT","TRANSFERWAREIN_DISTRIAMT","SALE_DISTRIAMT",
                                "ONORDER_DISTRIAMT","COMBIN_DISTRIAMT","COMBIN_MATERIAL_DISTRIAMT","DISASS_DISTRIAMT","DISASS_MATERIAL_DISTRIAMT",                            
                                "LDIFF_DISTRIAMT","ZHHB_DISTRIAMT","ZHHB_MATERIAL_DISTRIAMT","ZHCJ_DISTRIAMT","ZHCJ_MATERIAL_DISTRIAMT",  
                                "JFDH_DISTRIAMT","JFDH_CANCEL_DISTRIAMT","KCZZS_DISTRIAMT","KCZZS_CANCEL_DISTRIAMT","DISTRIAMT","BEGIN_DISTRIAMT"
                        };
                        //因为dcp_stock_day_static 加了一个分区字段，和dcp_stock_day表结构不一致，所以要单独处理
                        String[] columnsDCP_STOCK_DAY_STATIC = {
                                "EID","ORGANIZATIONNO","WAREHOUSE","EDATE","PLUNO","BASEUNIT","BEGIN_QTY","BEGIN_AMT",
                                "BEGIN_PRICE","STOCKIN_QTY","STOCKIN_AMT","STOCKOUT_QTY","STOCKOUT_AMT","SSTOCKIN_QTY",
                                "SSTOCKIN_AMT","SSTOCKOUT_QTY","SSTOCKOUT_AMT","PSTOCKIN_QTY","PSTOCKIN_AMT","SALE_QTY",
                                "SALE_AMT","MATERIAL_QTY","MATERIAL_AMT","TRANSFEROUT_QTY","TRANSFEROUT_AMT","TRANSFERIN_QTY",
                                "TRANSFERIN_AMT","LOSS_QTY","LOSS_AMT","DIFF_QTY","DIFF_AMT","ADJUST_QTY",
                                "ADJUST_AMT","ONORDER_QTY","ONORDER_AMT","QTY","AMT","PRICE",
                                "OTHERIN_QTY","OTHERIN_AMT","OTHEROUT_QTY","OTHEROUT_AMT", //"PSCRAP_QTY","PSCRAP_AMT",
                                "RDIFF_QTY","RDIFF_AMT","TDIFF_QTY","TDIFF_AMT","TRANSFERWAREOUT_QTY","TRANSFERWAREOUT_AMT",
                                "TRANSFERWAREIN_QTY","TRANSFERWAREIN_AMT","IN_TRANSIT_ADD_QTY","IN_TRANSIT_ADD_AMT",
                                "IN_TRANSIT_MINUS_QTY","IN_TRANSIT_MINUS_AMT","COMBIN_QTY","COMBIN_AMT",
                                "COMBIN_MATERIAL_QTY","COMBIN_MATERIAL_AMT","DISASS_QTY","DISASS_AMT",
                                "DISASS_MATERIAL_QTY","DISASS_MATERIAL_AMT","LDIFF_QTY","LDIFF_AMT" , "ZHHB_QTY","ZHHB_AMT",
                                "ZHHB_MATERIAL_QTY","ZHHB_MATERIAL_AMT",
                                //by jzma 20190731
                                "BATCHNO","PRODDATE","DISTRIPRICE",
                                //by jzma 20200729 3.0新增
                                "ZHCJ_QTY","ZHCJ_AMT","ZHCJ_MATERIAL_QTY","ZHCJ_MATERIAL_AMT",
                                "JFDH_QTY","JFDH_AMT","JFDH_CANCEL_QTY","JFDH_CANCEL_AMT",
                                "FEATURENO","KCZZS_QTY","KCZZS_AMT","KCZZS_CANCEL_QTY","KCZZS_CANCEL_AMT",
                                //by tianhq 20240226 add
                                "TRANSFERIN_DISTRIAMT","STOCKIN_DISTRIAMT","STOCKOUT_DISTRIAMT","TRANSFEROUT_DISTRIAMT","SSTOCKIN_DISTRIAMT",
                                "SSTOCKOUT_DISTRIAMT","LOSS_DISTRIAMT","PSTOCKIN_DISTRIAMT","DIFF_DISTRIAMT","ADJUST_DISTRIAMT",
                                "MATERIAL_DISTRIAMT","IN_TRANSIT_ADD_DISTRIAMT","IN_TRANSIT_MINUS_DISTRIAMT","OTHERIN_DISTRIAMT","OTHEROUT_DISTRIAMT",
                                "TDIFF_DISTRIAMT","RDIFF_DISTRIAMT","TRANSFERWAREOUT_DISTRIAMT","TRANSFERWAREIN_DISTRIAMT","SALE_DISTRIAMT",
                                "ONORDER_DISTRIAMT","COMBIN_DISTRIAMT","COMBIN_MATERIAL_DISTRIAMT","DISASS_DISTRIAMT","DISASS_MATERIAL_DISTRIAMT",                            
                                "LDIFF_DISTRIAMT","ZHHB_DISTRIAMT","ZHHB_MATERIAL_DISTRIAMT","ZHCJ_DISTRIAMT","ZHCJ_MATERIAL_DISTRIAMT",  
                                "JFDH_DISTRIAMT","JFDH_CANCEL_DISTRIAMT","KCZZS_DISTRIAMT","KCZZS_CANCEL_DISTRIAMT","DISTRIAMT","BEGIN_DISTRIAMT",
                                "PARTITION_DATE"
                        };

                        String batchNo=par.get("BATCHNO").toString();
                        if(Check.Null(batchNo)) {
                            batchNo = " ";
                        }
                        String featureNo=par.get("FEATURENO").toString();
                        if(Check.Null(featureNo)) {
                            featureNo = " ";
                        }

                        //【ID1027148】【货郎3.0】商品990100274的零售价为0.5元，为什么库存日结表C1022店的PRICE是200元？ by jinzma 20220707
                        String price = par.get("PRIORITYPRICE").toString();

                        //【ID1038292】【罗森尼娜3.0】有一个商品DCP_STOCK_DAY_STATIC和DCP_STOCK_DAY的单价和金额不对  by jinzma 20240108
                        //因为单价重新计算了，此处金额也要重新取
                        String amt = par.get("PRIORITYAMT").toString();


                        // String distriPrice = par.get("PRIORITYDISTRIPRICE").toString();
                        String distriPrice = par.get("DISTRIPRICE").toString();
                        if (Check.Null(price)||price.equals("0")){
                            price = par.get("PRICE").toString();
                            amt = par.get("AMT").toString();
                        }
                        if (Check.Null(distriPrice)||distriPrice.equals("0")) {
                            distriPrice = par.get("DISTRIPRICE").toString();
                        }


                        DataValue[]	columnsVal3 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(par.get("ORGANIZATIONNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("WAREHOUSE").toString(), Types.VARCHAR),
                                new DataValue(startEdate, Types.VARCHAR),
                                new DataValue(par.get("PLUNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("BASEUNIT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_PRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ONORDER_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ONORDER_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("QTY").toString(), Types.VARCHAR),
                                new DataValue(amt, Types.VARCHAR),
                                new DataValue(price, Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("LDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(batchNo, Types.VARCHAR),
                                new DataValue(par.get("PRODDATE").toString(), Types.VARCHAR),
                                new DataValue(distriPrice, Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_AMT").toString(), Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(par.get("KCZZS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_AMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("TRANSFERIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("SSTOCKOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_DISTRIAMT").toString(), Types.VARCHAR),

                                new DataValue(par.get("MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("TDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("ONORDER_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),                         

                                new DataValue(par.get("LDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),

                                new DataValue(par.get("JFDH_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_DISTRIAMT").toString(), Types.VARCHAR),
                        };

                        //插入DCP_STOCK_DAY
                        InsBean ib3 = new InsBean("DCP_STOCK_DAY", columnsDCP_STOCK_DAY);
                        ib3.addValues(columnsVal3);
                        this.addProcessData(new DataProcessBean(ib3));

                        //因为dcp_stock_day_static 加了一个分区字段，和dcp_stock_day表结构不一致，所以要单独处理
                        DataValue[]	columnsVal_Static = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(par.get("ORGANIZATIONNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("WAREHOUSE").toString(), Types.VARCHAR),
                                new DataValue(startEdate, Types.VARCHAR),
                                new DataValue(par.get("PLUNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("BASEUNIT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_PRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ONORDER_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ONORDER_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("AMT").toString(), Types.VARCHAR),
                                new DataValue(price, Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LDIFF_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("LDIFF_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(batchNo, Types.VARCHAR),
                                new DataValue(par.get("PRODDATE").toString(), Types.VARCHAR),
                                new DataValue(distriPrice, Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_AMT").toString(), Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(par.get("KCZZS_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_AMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("TRANSFERIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFEROUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SSTOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("SSTOCKOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOSS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("PSTOCKIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ADJUST_DISTRIAMT").toString(), Types.VARCHAR),

                                new DataValue(par.get("MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_ADD_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("IN_TRANSIT_MINUS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHERIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("OTHEROUT_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("TDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("RDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREOUT_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("TRANSFERWAREIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("SALE_DISTRIAMT").toString(), Types.VARCHAR),
                                
                                new DataValue(par.get("ONORDER_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("COMBIN_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISASS_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),                         

                                new DataValue(par.get("LDIFF_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHHB_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("ZHCJ_MATERIAL_DISTRIAMT").toString(), Types.VARCHAR),

                                new DataValue(par.get("JFDH_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("JFDH_CANCEL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("KCZZS_CANCEL_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BEGIN_DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(startEdate, Types.VARCHAR),
                        };

                        //插入DCP_STOCK_DAY_STATIC
                        InsBean ib4 = new InsBean("DCP_STOCK_DAY_STATIC", columnsDCP_STOCK_DAY_STATIC);
                        ib4.addValues(columnsVal_Static);
                        this.addProcessData(new DataProcessBean(ib4));
                    }

                    //删除前一日结DCP_STOCK_DAY
                    DelBean db1 = new DelBean("DCP_STOCK_DAY");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("EDATE", new DataValue(sLitDay , Types.VARCHAR, DataExpression.LessEQ));
                    this.addProcessData(new DataProcessBean(db1));

                } else {
                    //
                    continue;//无数据:跳出
                }


                //2.************插入库存流水账历史表，再删除库存流水账**************
                //**************插入库存流水账历史表，再删除库存流水账**************
                //**************插入库存流水账历史表，再删除库存流水账**************
                String sDCP_STOCK_DETAILSQL=this.GetEDateDCP_STOCK_DETAIL_SQL(eId, shopId, startEdate);
                List<Map<String, Object>> getDCP_STOCK_DETAIL=this.doQueryData(sDCP_STOCK_DETAILSQL,null);
                if (getDCP_STOCK_DETAIL != null && !getDCP_STOCK_DETAIL.isEmpty()) {
                    for (Map<String, Object> par : getDCP_STOCK_DETAIL) {
                        String[] columnsDCP_STOCK_DETAIL_STATIC = {
                                "SERIALNO","EID","ORGANIZATIONNO","BILLTYPE","BILLNO",
                                "ITEM","STOCKTYPE","BDATE","PLUNO","FEATURENO",
                                "WAREHOUSE","BATCHNO","UNIT","QTY","BASEUNIT",
                                "BASEQTY","UNITRATIO","PRICE","AMT","DISTRIPRICE","DISTRIAMT",
                                "ACCOUNTDATE","CREATEOPID","CREATEOPNAME","CREATETIME",
                                "LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","PRODDATE","PARTITION_DATE",
                                "OLDPRICE","OLDAMT","LOCATION","ITEM2","BTYPE","COSTCODE"
                        };
                        //if ACCOUNT_DATE<日结日期startEdate，then ACCOUNT_DATE=startEdate， 处理日结后写入日结日期前的流水 BY JZMA 20191106
                        String accountDate=par.get("ACCOUNTDATE").toString();
                        if (!accountDate.equals(startEdate)) {
                            accountDate = startEdate;
                        }

                        String featureNo = par.get("FEATURENO").toString();
                        if (Check.Null(featureNo)) {
                            featureNo = " ";
                        }

                        DataValue[]	columnsVal3 = new DataValue[]{
                                new DataValue(par.get("SERIALNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("EID").toString(), Types.VARCHAR),
                                new DataValue(par.get("ORGANIZATIONNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("BILLTYPE").toString(), Types.VARCHAR),
                                new DataValue(par.get("BILLNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("ITEM").toString(), Types.VARCHAR),
                                new DataValue(par.get("STOCKTYPE").toString(), Types.VARCHAR),
                                new DataValue(par.get("BDATE").toString(), Types.DATE),
                                new DataValue(par.get("PLUNO").toString(), Types.VARCHAR),
                                new DataValue(featureNo, Types.VARCHAR),
                                new DataValue(par.get("WAREHOUSE").toString(), Types.VARCHAR),
                                new DataValue(par.get("BATCHNO").toString(), Types.VARCHAR),
                                new DataValue(par.get("UNIT").toString(), Types.VARCHAR),
                                new DataValue(par.get("QTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("BASEUNIT").toString(), Types.VARCHAR),
                                new DataValue(par.get("BASEQTY").toString(), Types.VARCHAR),
                                new DataValue(par.get("UNITRATIO").toString(), Types.VARCHAR),
                                new DataValue(par.get("PRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("AMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("DISTRIAMT").toString(), Types.VARCHAR),
                                new DataValue(PosPub.GetStringDateLine(accountDate, 0), Types.DATE),  //康总干的日期必须带-
                                new DataValue(par.get("CREATEOPID").toString(), Types.VARCHAR),
                                new DataValue(par.get("CREATEOPNAME").toString(), Types.VARCHAR),
                                new DataValue(par.get("CREATETIME").toString(), Types.DATE),
                                new DataValue(par.get("LASTMODIOPID").toString(), Types.VARCHAR),
                                new DataValue(par.get("LASTMODIOPNAME").toString(), Types.VARCHAR),
                                new DataValue(par.get("LASTMODITIME").toString(), Types.DATE),
                                new DataValue(par.get("PRODDATE").toString(), Types.VARCHAR),
                                new DataValue(accountDate, Types.VARCHAR),
                                new DataValue(par.get("OLDPRICE").toString(), Types.VARCHAR),
                                new DataValue(par.get("OLDAMT").toString(), Types.VARCHAR),
                                new DataValue(par.get("LOCATION").toString(), Types.VARCHAR),
                                new DataValue(par.get("ITEM2").toString(), Types.VARCHAR),
                                new DataValue(par.get("BTYPE").toString(), Types.VARCHAR),
                                new DataValue(par.get("COSTCODE").toString(), Types.VARCHAR),
                        };
                        InsBean ib3 = new InsBean("DCP_STOCK_DETAIL_STATIC", columnsDCP_STOCK_DETAIL_STATIC);
                        ib3.addValues(columnsVal3);
                        this.addProcessData(new DataProcessBean(ib3));
                    }

                    //删除操作DCP_STOCK_DETAIL
                    //					DelBean db1 = new DelBean("DCP_STOCK_DETAIL");
                    //					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    //					db1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                    //					db1.addCondition("ACCOUNTDATE", new DataValue(startEdate , Types.DATE,DataExpression.LessEQ));
                    //					this.addProcessData(new DataProcessBean(db1));

                    String sql =" delete from dcp_stock_detail"
                            + " where eid='"+eId+"' and organizationno='"+shopId+"' "
                            + " and to_char(accountdate,'yyyymmdd')<= '"+startEdate+"' ";
                    ExecBean exec = new ExecBean(sql);
                    this.addProcessData(new DataProcessBean(exec));

                }


                this.doExecuteDataToDB();


                //新增单据汇总资料  BY JZMA 2019/6/4
                String isDocTotal = PosPub.getPARA_SMS(dao, eId, "", "isDocTotal"); //是否统计单据汇总
                if (Check.Null(isDocTotal)){
                    isDocTotal = "Y";
                }
                if (!isDocTotal.equals("N")) {
                    try {
                        docTotalProcess(eId, shopId, startEdate,req);
                        this.doExecuteDataToDB();
                    } catch (Exception e) {
                        this.pData.clear(); //清空异常残留
                        logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "单据汇总资料保存失败：" + e.getMessage() + "\r\n");
                    }
                }
                //新增销售商品日统计档
                REP_SALE_GOODS_DAY_Process(eId, shopId, startEdate,req);

                //【ID1038854】【海鹏3.0】进销存汇总表，查询一段时间的期末库存进货金额，超级大，一个加工门店月底存货金额达到了2000多万！ by jinzma 20240130
                //dcp_stock_day_price
                String isGoodsPrice = PosPub.getPARA_SMS(dao, eId, "", "isGoodsPrice");
                if("Y".equals(isGoodsPrice))
                {
                	logger.error("日结-GoodsPriceProcess-开始"+shopId);
                	GoodsPriceProcess(eId,shopId,startEdate);
                	logger.error("日结-GoodsPriceProcess-结束"+shopId);
                }
                //更新日期：自动+1天
                startEdate=PosPub.GetStringDate(startEdate, 1);

            }


            //重计库存和销售数量汇总
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());

            //减少不必要的统计   startEdate在循环最后会加1天，等于系统日期就是昨天的日结
            if (startEdate.equals(sDate)){

                //销售数量汇总（统计一个月内的）,解决货郎商城服务慢  by jinzma 20230220
                SumSaleQty(eId,shopId,req);
                SumSaleQty(eId,req);

                //是否重计库存  by jinzma 20210914
                if (isRecoverStock.equals("Y")){

                    //【ID1022996】【冠生园3.0】门店没有中午闭店功能 by jinzma 20220105
                    /*String shopEDateTime;
                String para_ShopEDateTimeCustomize = PosPub.getPARA_SMS(dao, eId,"", "ShopEDateTimeCustomize"); //是否启用门店自定义日结
                if (Check.Null(para_ShopEDateTimeCustomize) || !para_ShopEDateTimeCustomize.equals("Y")){
                    shopEDateTime = PosPub.getPARA_SMS(dao, eId, "", "ShopEDateTime");
                }else{
                    shopEDateTime = PosPub.getPARA_SMS(dao, eId, shopId, "ShopEDateTime");
                }*/
        
                    /*if (Check.Null(shopEDateTime)) {
                    shopEDateTime = "020000";   //日结时间默认2点
                }*/

                    //日结日期，其实可以不要这个日期，因为前面已经判断了这个日结必须是昨天的
                    String eDate = PosPub.GetStringDate(sDate,-1);

                    try {
                        String sql=""
                                + " update dcp_stock a set"
                                + " (a.qty,a.lastmodiopid,a.lastmodiopname,a.lastmoditime) = "
                                + " (select b.qty,'admin','shopedate',to_date('"+sDate+" "+sTime+"','yyyyMMddHH24miss') from dcp_stock_day b"
                                + " where a.pluno=b.pluno and a.featureno=b.featureno and a.warehouse=b.warehouse and a.baseunit=b.baseunit "
                                + " and b.eid='"+eId+"' and b.organizationno='"+shopId+"' and b.edate='"+eDate+"' and b.batchno=' ' "
                                + " )"
                                + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"'"
                                + " and a.warehouse||a.pluno||a.featureno||a.baseunit in ("
                                + "    select a.warehouse||a.pluno||a.featureno||a.baseunit from dcp_stock_day a"
                                + "    left join dcp_stock b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse"
                                + "    and a.pluno=b.pluno and a.featureno=b.featureno and a.baseunit=b.baseunit "
                                + "    where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.edate='"+eDate+"' and a.batchno=' ' and a.qty<>b.qty"
                                + " )"
                                // + " and ("
                                // + "   (to_char(a.lastmoditime,'yyyyMMdd')='"+recoverDate+"' and to_char(a.lastmoditime,'HH24MISS')<'"+shopEDateTime+"')"
                                // + "   or to_char(a.lastmoditime,'yyyyMMdd')<'"+recoverDate+"'"
                                // + " )"
                                //【ID1030876】【乐沙儿3.2.0.3】门店晚上闭店后日结前，又做了销售，中台开了重计库存，dcp_stock库存不正确（库存扣减后，
                                // 日结时又被DCP_STOCK_DAY覆盖了）。 by jinzma 20230307 以上注释


                                // + "   or (a.lastmoditime is null and (to_char(a.createtime,'yyyyMMdd')='"+recoverDate+"' and to_char(a.createtime,'HH24MISS')<'"+shopEDateTime+"'))"
                                // + "   or (a.lastmoditime is null and to_char(a.createtime,'yyyyMMdd')<'"+recoverDate+"')"

                                + "";
                        ExecBean exec = new ExecBean(sql);
                        this.addProcessData(new DataProcessBean(exec));


                        this.doExecuteDataToDB();



                    }catch (Exception e){
                        this.pData.clear(); //清空异常残留
                        logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"重计库存更新日结数量失败：" + e.getMessage() + "\r\n");
                    }

                    try{
                        String sql=""
                                + " insert into dcp_stock(eid,organizationno,pluno,featureno,warehouse,baseunit,qty,lockqty,onlineqty,createopid,createopname,createtime,lastmodiopid,lastmodiopname,lastmoditime)"
                                + " select eid,organizationno,pluno,featureno,warehouse,baseunit,qty,0,0,'admin','shopedate',sysdate,'admin','shopedate',to_date('"+sDate+" "+sTime+"','yyyyMMddHH24miss') from dcp_stock_day"
                                + " where eid='"+eId+"' and organizationno='"+shopId+"'"
                                + " and eid||organizationno||warehouse||pluno||featureno not in (select eid||organizationno||warehouse||pluno||featureno from dcp_stock)"
                                + " ";
                        ExecBean exec = new ExecBean(sql);
                        this.addProcessData(new DataProcessBean(exec));

                        this.doExecuteDataToDB();

                    }catch (Exception e){
                        this.pData.clear(); //清空异常残留
                        logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"重计库存插入日结数据失败：" + e.getMessage() + "\r\n");
                    }

                    try{
                        //【ID1030876】【乐沙儿3.2.0.3】门店晚上闭店后日结前，又做了销售，中台开了重计库存，dcp_stock库存不正确（库存扣减后，日结时又被DCP_STOCK_DAY覆盖了）。 by jinzma 20230307
                        String sql = " select a.pluno from dcp_stock_detail a where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and (a.batchno is null or a.batchno=' ') ";
                        List<Map<String, Object>> getStockDetail=this.doQueryData(sql,null);
                        if (!CollectionUtils.isEmpty(getStockDetail)){
                            sql=" update dcp_stock a set (a.qty,a.lastmodiopid,a.lastmodiopname,a.lastmoditime)"
                                    + " =("
                                    + " select nvl(sum(b.baseqty*b.stocktype),0) + nvl(a.qty,0) as qty,'admin','shopedate',to_date('"+sDate+" "+sTime+"','yyyyMMddHH24miss') "
                                    + " from dcp_stock_detail b"
                                    + " where a.eid=b.eid and a.organizationno=b.organizationno and a.pluno=b.pluno"
                                    + " and a.featureno=b.featureno and a.warehouse=b.warehouse and a.baseunit=b.baseunit"
                                    + " and (b.batchno is null or b.batchno=' ') "
                                    + " and b.billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42')"
                                    + " and b.eid='"+eId+"' and b.organizationno='"+shopId+"' "
                                    + " group by b.pluno,b.featureno,b.warehouse,b.baseunit"
                                    + " )"
                                    + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' "
                                    // + " and ("
                                    // + "  (to_char(a.lastmoditime,'yyyyMMdd')='"+recoverDate+"' and to_char(a.lastmoditime,'HH24MISS')<'"+shopEDateTime+"' "
                                    // + "   ) or to_char(a.lastmoditime,'yyyyMMdd')<'"+recoverDate+"'"
                                    // + " )"
                                    + " and a.warehouse||a.pluno||a.featureno||a.baseunit in "
                                    + " (select a.warehouse||a.pluno||a.featureno||a.baseunit from dcp_stock_detail a"
                                    + " inner join dcp_stock_day b on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse and a.pluno=b.pluno and a.featureno=b.featureno and a.baseunit=b.baseunit"
                                    + " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and (a.batchno is null or a.batchno=' ') "
                                    + " ) "
                                    + " "
                                    + " "
                                    + " ";

                            ExecBean exec = new ExecBean(sql);
                            this.addProcessData(new DataProcessBean(exec));

                            this.doExecuteDataToDB();

                        }


                    }catch (Exception e){
                        this.pData.clear(); //清空异常残留
                        logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"重计库存更新流水数量失败：" + e.getMessage() + "\r\n");
                    }
                }
            }


            return true;



        } catch (Exception e) {
            this.pData.clear();//清理数据
            errorMessage.append(e.getMessage());
            return false;
        }
        finally {
            //日结查询超时时间恢复为30秒  BY JZMA 20200107
            PosPub.iTimeoutTime=30;
        }
    }

    /**
     * 获取最大日结日期
     * @param eId 企业编号
     * @param OrganizationNO 组织编号
     * @return String 最大日期
     */
    private String GetMaxEdate_SQL(String eId, String OrganizationNO) {
        String sql=" select max(edate) as edate,min(edate) as minedate from dcp_stock_day where eid='"+eId+"' and organizationno='"+OrganizationNO+"' ";
        return sql;
    }

    private String GetDCP_STOCK_DETAIL_STATIC_SQL(String eId, String OrganizationNO,String eDate ) {
        String sql = " SELECT * from DCP_STOCK_DETAIL_STATIC "
                + " where EID='" + eId + "' and organizationno='" + OrganizationNO + "'  and to_char(accountdate,'yyyymmdd')>='" + eDate + "' ";
        return sql;
    }

    private String GetDCP_STOCK_DAY_STATIC_SQL(String eId, String OrganizationNO,String eDate) {
        String sql = "SELECT * from DCP_STOCK_DAY_STATIC  "
                + " where EID='" + eId + "'  "
                + " and organizationno='" + OrganizationNO + "'  "
                + " and EDATE='" + PosPub.GetStringDate(eDate, -1) + "' ";
        return sql;
    }

    /**
     * 门店最小流水入账日期
     * @param eId 企业编号
     * @param OrganizationNO 组织编号
     * @return String 最小日期
     */
    private String GetHandMinStock_Detail_Date_SQL(String eId, String OrganizationNO) {
        String sql=" select to_char(min(accountdate),'yyyymmdd') as accountdate from dcp_stock_detail "
                + " where eid='"+eId+"' and organizationno='"+OrganizationNO+"' ";
        return sql;
    }

    /**
     * 汇总数据-库存流水账档
     * @param eId 企业编号
     * @param OrganizationNO 组织编号
     * @param CurEdate 本次日结日期
     * @return String sql
     */
    private String GetEDateDCP_STOCK_DETAIL_SQL(String eId,String OrganizationNO,String CurEdate ) {
        String sqlbuf = " select serialno,eid,organizationno,billtype,billno,item,stocktype,bdate,pluno,featureno,warehouse,"
                + " batchno,unit,qty,baseunit,baseqty,unitratio,price,amt,distriprice,distriamt,"
                + " to_char(accountdate,'YYYYMMDD') as accountdate,"
                + " createopid,createopname,createtime,lastmodiopid,lastmodiopname,lastmoditime,proddate,"
                + " OLDPRICE,OLDAMT,LOCATION,ITEM2,btype,costcode "
                + " from dcp_stock_detail"
                + " where eid='" + eId + "' and organizationno='" + OrganizationNO + "' and to_char(accountdate,'yyyymmdd')<='" + CurEdate + "' ";
        //sqlbuf.append(" and account_date='"+CurEdate+"'");
        // 只删除日结当天的流水   BY JZMA 20191101
        // 之前的也日结掉，增加一个备注说明  BY JZMA 20191106

        return sqlbuf;
    }

    /**
     * 异常处理：只获取日结档数据
     * @param eId 企业编号
     * @param OrganizationNO 组织编号
     * @param CurEdate  日期日期
     * @param history 1:历史表
     * @return String 获取SQL
     */
    private String GetExceptionEDate_SQL(String eId,String OrganizationNO, String CurEdate,String history ) {
        StringBuffer sqlbuf=new StringBuffer();
        //历史表
        if (history.equals("1")) {
            sqlbuf.append(" select count(*) as num "
                    + " from dcp_stock_day_static a "
                    + " left join dcp_stock_detail_static b on a.eid=b.eid and a.organizationno=b.organizationno "
                    + " and a.warehouse=b.warehouse and a.pluno=b.pluno "
                    + " and (trim(a.featureno)=trim(b.featureno) or (trim(a.featureno) is null and trim(b.featureno) is null )) "
                    + " and (trim(a.batchno)=trim(b.batchno) or (trim(a.batchno) is null and trim(b.batchno) is null )) "
                    + " and b.billtype='00' and b.eid='"+eId+"' and b.organizationno='"+OrganizationNO+"' "
                    + " and to_char(b.accountdate,'yyyymmdd')='"+CurEdate+"' "
                    + " where a.eid='"+eId+"' and a.organizationno='"+OrganizationNO+"' "
                    + " and a.edate='"+ PosPub.GetStringDate(CurEdate, -1)+"' ");
        } else {
            sqlbuf.append(" "
                    + " select count(*) as num "
                    + " from dcp_stock_day a "
                    + " left join dcp_stock_detail b on a.eid=b.eid and a.organizationno=b.organizationno "
                    + " and a.warehouse=b.warehouse and a.pluno=b.pluno "
                    + " and (trim(a.featureno)=trim(b.featureno) or (trim(a.featureno) is null and trim(b.featureno) is null )) "
                    + " and (trim(a.batchno)=trim(b.batchno) or (trim(a.batchno) is null and trim(b.batchno) is null )) "
                    + " and b.billtype='00' and b.eid='"+eId+"' and b.organizationno='"+OrganizationNO+"' "
                    + " and to_char(b.accountdate,'yyyymmdd')='"+CurEdate+"' "
                    + " where a.eid='"+eId+"' and a.organizationno='"+OrganizationNO+"' "
                    + " and a.edate='"+PosPub.GetStringDate(CurEdate, -1)+"' ");
        }

        return sqlbuf.toString();
    }

    /**
     * 汇总数据-日结档
     * @param eId 企业编号
     * @param OrganizationNO 组织编号
     * @param CurEdate 本次日结日期
     * @return String 查询SQL
     */
    private String GetEDate_SQL(String eId,String OrganizationNO,String CurEdate ) {
        String sqlbuf = " "
                + " select a.*,a.price*a.qty as amt,a.priorityprice*a.qty as priorityamt, b.baseunit"
                + " from ("
                + " select pluno,featureno,batchno,max(proddate) as proddate,max(bdate) as bdate,organizationno,warehouse,"
                + " sum(begin_qty) as begin_qty,sum(begin_amt) as begin_amt,sum(begin_distriamt) as begin_distriamt,"
                + " max(begin_price) as begin_price, "
                + " sum(stockin_qty) as stockin_qty,sum(stockin_amt) as stockin_amt,sum(stockin_distriamt) as stockin_distriamt, "
                + " sum(stockout_qty) as stockout_qty,sum(stockout_amt) as stockout_amt,sum(stockout_distriamt) as stockout_distriamt,"
                + " sum(sstockin_qty) as sstockin_qty, sum(sstockin_amt) as sstockin_amt,sum(sstockin_distriamt) as sstockin_distriamt,"
                + " sum(sstockout_qty) as sstockout_qty,sum(sstockout_amt) as sstockout_amt,sum(sstockout_distriamt) as sstockout_distriamt,"
                + " sum(pstockin_qty) as pstockin_qty,sum(pstockin_amt) as pstockin_amt,sum(pstockin_distriamt) as pstockin_distriamt,"
                + " sum(material_qty) as material_qty,sum(material_amt) as material_amt,sum(material_distriamt) as material_distriamt,"
                + " sum(transferout_qty) as transferout_qty,sum(transferout_amt) as transferout_amt,sum(transferout_distriamt) as transferout_distriamt,"
                + " sum(transferin_qty) as transferin_qty,sum(transferin_amt) as transferin_amt,sum(transferin_distriamt) as transferin_distriamt,"
                + " sum(loss_qty) as loss_qty,sum(loss_amt) as loss_amt,sum(loss_distriamt) as loss_distriamt,"
                + " sum(diff_qty) as diff_qty,sum(diff_amt) as diff_amt,sum(diff_distriamt) as diff_distriamt,"
                + " sum(otherin_qty) as otherin_qty,sum(otherin_amt) as otherin_amt,sum(otherin_distriamt) as otherin_distriamt,"
                + " sum(otherout_qty) as otherout_qty,sum(otherout_amt) as otherout_amt,sum(otherout_distriamt) as otherout_distriamt,"
                + " sum(adjust_qty) as adjust_qty,sum(adjust_amt) as adjust_amt,sum(adjust_distriamt) as adjust_distriamt,"
                + " sum(onorder_qty) as onorder_qty,sum(onorder_amt) as onorder_amt,sum(onorder_distriamt) as onorder_distriamt,"
                + " sum(sale_qty) as sale_qty,sum(sale_amt) as sale_amt,sum(sale_distriamt) as sale_distriamt,"
                + " sum(rdiff_qty) as rdiff_qty,sum(rdiff_amt) as rdiff_amt,sum(rdiff_distriamt) as rdiff_distriamt,"
                + " sum(tdiff_qty) as tdiff_qty,sum(tdiff_amt) as tdiff_amt,sum(tdiff_distriamt) as tdiff_distriamt,"
                + " sum(begin_qty) + sum(stockin_qty) - sum(stockout_qty) + sum(sstockin_qty) - sum(sstockout_qty) "
                + " + sum(pstockin_qty) - sum(material_qty) - sum(transferout_qty) + sum(transferin_qty) - sum(loss_qty) "
                + " + sum(diff_qty) + sum(adjust_qty) + sum(otherin_qty) - sum(otherout_qty) - sum(sale_qty) + sum(rdiff_qty) "
                + " + sum(tdiff_qty) + sum(transferwarein_qty) - sum(transferwareout_qty) + sum(in_transit_add_qty) "
                + " - sum(in_transit_minus_qty) + sum(combin_qty) - sum(combin_material_qty) + sum(disass_material_qty) "
                + " - sum(disass_qty) + sum(ldiff_qty) + sum(zhhb_qty) - sum(zhhb_material_qty) - sum(zhcj_qty) "
                + " + sum(zhcj_material_qty) - sum(jfdh_qty) + sum(jfdh_cancel_qty) - sum(kczzs_qty) + sum(kczzs_cancel_qty) "
                + " as qty,  "
                + " sum(begin_distriamt) + sum(stockin_distriamt) - sum(stockout_distriamt) + sum(sstockin_distriamt) - sum(sstockout_distriamt) "
                + " + sum(pstockin_distriamt) - sum(material_distriamt) - sum(transferout_distriamt) + sum(transferin_distriamt) - sum(loss_distriamt) "
                + " + sum(diff_distriamt) + sum(adjust_distriamt) + sum(otherin_distriamt) - sum(otherout_distriamt) - sum(sale_distriamt) + sum(rdiff_distriamt) "
                + " + sum(tdiff_distriamt) + sum(transferwarein_distriamt) - sum(transferwareout_distriamt) + sum(in_transit_add_distriamt) "
                + " - sum(in_transit_minus_distriamt) + sum(combin_distriamt) - sum(combin_material_distriamt) + sum(disass_material_distriamt) "
                + " - sum(disass_distriamt) + sum(ldiff_distriamt) + sum(zhhb_distriamt) - sum(zhhb_material_distriamt) - sum(zhcj_distriamt) "
                + " + sum(zhcj_material_distriamt) - sum(jfdh_distriamt) + sum(jfdh_cancel_distriamt) - sum(kczzs_distriamt) + sum(kczzs_cancel_distriamt) "
                + " as distriamt, "
                //【ID1027148】【货郎3.0】商品990100274的零售价为0.5元，为什么库存日结表C1022店的PRICE是200元？ by jinzma 20220707
                + " max(price) as price,"
                //  + " max(distriprice) as distriprice,"
                + " CASE WHEN sum(stockin_qty)<> 0 THEN round(sum(stockin_distriamt)/sum(stockin_qty),4) ELSE max(distriprice) END AS  distriprice, "
                + " max(priorityprice) as priorityprice,"
                + " max(prioritydistriprice) as prioritydistriprice,"
                + " sum(transferwareout_qty) as transferwareout_qty,sum(transferwareout_amt) as transferwareout_amt,sum(transferwareout_distriamt) as transferwareout_distriamt,"
                + " sum(transferwarein_qty) as transferwarein_qty,sum(transferwarein_amt) as transferwarein_amt,sum(transferwarein_distriamt) as transferwarein_distriamt,"
                + " sum(in_transit_add_qty) as in_transit_add_qty,sum(in_transit_add_amt) as in_transit_add_amt,sum(in_transit_add_distriamt) as in_transit_add_distriamt,"
                + " sum(in_transit_minus_qty) as in_transit_minus_qty,sum(in_transit_minus_amt) as in_transit_minus_amt,sum(in_transit_minus_distriamt) as in_transit_minus_distriamt,"
                + " sum(combin_qty) as combin_qty,sum(combin_amt) as combin_amt,sum(combin_distriamt) as combin_distriamt,"
                + " sum(combin_material_qty) as combin_material_qty,sum(combin_material_amt) as combin_material_amt,sum(combin_material_distriamt) as combin_material_distriamt,"
                + " sum(disass_qty) as disass_qty,sum(disass_amt) as disass_amt,sum(disass_distriamt) as disass_distriamt,"
                + " sum(disass_material_qty) as disass_material_qty,sum(disass_material_amt) as disass_material_amt,sum(disass_material_distriamt) as disass_material_distriamt,"
                + " sum(ldiff_qty) as ldiff_qty,sum(ldiff_amt) as ldiff_amt,sum(ldiff_distriamt) as ldiff_distriamt,"
                + " sum(zhhb_qty) as zhhb_qty,sum(zhhb_amt) as zhhb_amt,sum(zhhb_distriamt) as zhhb_distriamt, "
                + " sum(zhhb_material_qty) as zhhb_material_qty,sum(zhhb_material_amt) as zhhb_material_amt,sum(zhhb_material_distriamt) as zhhb_material_distriamt,"
                + " sum(zhcj_qty) as zhcj_qty,sum(zhcj_amt) as zhcj_amt,sum(zhcj_distriamt) as zhcj_distriamt,"
                + " sum(zhcj_material_qty) as zhcj_material_qty,sum(zhcj_material_amt) as zhcj_material_amt,sum(zhcj_material_distriamt) as zhcj_material_distriamt,"
                + " sum(jfdh_qty) as jfdh_qty,sum(jfdh_amt) as jfdh_amt,sum(jfdh_distriamt) as jfdh_distriamt,"
                + " sum(jfdh_cancel_qty) as jfdh_cancel_qty,sum(jfdh_cancel_amt) as jfdh_cancel_amt,sum(jfdh_cancel_distriamt) as jfdh_cancel_distriamt,"
                + " sum(kczzs_qty) as kczzs_qty,sum(kczzs_amt) as kczzs_amt,sum(kczzs_distriamt) as kczzs_distriamt,"
                + " sum(kczzs_cancel_qty) as kczzs_cancel_qty,sum(kczzs_cancel_amt) as kczzs_cancel_amt,sum(kczzs_cancel_distriamt) as kczzs_cancel_distriamt "
                + " "
                + " from ("
                + " select pluno, nvl(featureno, N' ') as featureno,nvl(batchno, N' ') as batchno, proddate,"
                + " to_char(accountdate, 'yyyymmdd') as bdate, organizationno,warehouse, baseunit,"
                + " distriprice,price,"
                //【ID1038292】【罗森尼娜3.0】有一个商品DCP_STOCK_DAY_STATIC和DCP_STOCK_DAY的单价和金额不对  by jinzma 20240108
                //单价 = 单价/单位转换率  避免库存流水里面的单价因为单位不同，单价出现偏差 ,供货价暂不出来，代码好像是没有用到prioritydistriprice这个字段
                //+ " price as priorityprice,"
                + " price/decode(unitratio,0,1,unitratio) as priorityprice,"
                + " distriprice as prioritydistriprice,"
                + " case when billtype = '00' then baseqty else 0 end as begin_qty,"
                + " case when billtype = '00' then amt else 0 end as begin_amt,"
                + " case when billtype = '00' then distriamt else 0 end as begin_distriamt,"
                + " case when billtype = '00' then price else 0 end as begin_price,"
                + " case when billtype = '01' then baseqty else 0 end as stockin_qty,"
                + " case when billtype = '01' then amt else 0 end as stockin_amt,"
                + " case when billtype = '01' then distriamt else 0 end as stockin_distriamt,"
                + " case when billtype = '02' then baseqty else 0 end as transferin_qty,"
                + " case when billtype = '02' then amt else 0 end as transferin_amt,"
                + " case when billtype = '02' then distriamt else 0 end as transferin_distriamt,"
                + " case when billtype = '03' then baseqty else 0 end as stockout_qty,"
                + " case when billtype = '03' then amt else 0 end as stockout_amt,"
                + " case when billtype = '03' then distriamt else 0 end as stockout_distriamt,"
                + " case when billtype = '04' then baseqty else 0 end as transferout_qty,"
                + " case when billtype = '04' then amt else 0 end as transferout_amt,"
                + " case when billtype = '04' then distriamt else 0 end as transferout_distriamt,"
                + " case when billtype = '05' then baseqty else 0 end as sstockin_qty,"
                + " case when billtype = '05' then amt else 0 end as sstockin_amt,"
                + " case when billtype = '05' then distriamt else 0 end as sstockin_distriamt,"
                + " case when billtype = '06' then baseqty else 0 end as sstockout_qty,"
                + " case when billtype = '06' then amt else 0 end as sstockout_amt,"
                + " case when billtype = '06' then distriamt else 0 end as sstockout_distriamt,"
                + " case when billtype = '07' then baseqty else 0 end as loss_qty,"
                + " case when billtype = '07' then amt else 0 end as loss_amt,"
                + " case when billtype = '07' then distriamt else 0 end as loss_distriamt,"
                + " case when billtype = '08' then baseqty else 0 end as pstockin_qty,"
                + " case when billtype = '08' then amt else 0 end as pstockin_amt,"
                + " case when billtype = '08' then distriamt else 0 end as pstockin_distriamt,"
                + " case when billtype = '09' then baseqty else 0 end as diff_qty,"
                + " case when billtype = '09' then amt else 0 end as diff_amt,"
                + " case when billtype = '09' then distriamt else 0 end as diff_distriamt,"
                + " case when billtype = '10' then baseqty else 0 end as adjust_qty,"
                + " case when billtype = '10' then amt else 0 end as adjust_amt,"
                + " case when billtype = '10' then distriamt else 0 end as adjust_distriamt,"
                + " case when billtype = '11' then baseqty else 0 end as material_qty,"
                + " case when billtype = '11' then amt else 0 end as material_amt,"
                + " case when billtype = '11' then distriamt else 0 end as material_distriamt,"
                + " case when billtype = '12' then baseqty else 0 end as in_transit_add_qty,"
                + " case when billtype = '12' then amt else 0 end as in_transit_add_amt,"
                + " case when billtype = '12' then distriamt else 0 end as in_transit_add_distriamt,"
                + " case when billtype = '13' then baseqty else 0 end as in_transit_minus_qty,"
                + " case when billtype = '13' then amt else 0 end as in_transit_minus_amt,"
                + " case when billtype = '13' then distriamt else 0 end as in_transit_minus_distriamt,"
                + " case when billtype = '14' then baseqty else 0 end as otherin_qty,"
                + " case when billtype = '14' then amt else 0 end as otherin_amt,"
                + " case when billtype = '14' then distriamt else 0 end as otherin_distriamt,"
                + " case when billtype = '15' then baseqty else 0 end as otherout_qty,"
                + " case when billtype = '15' then amt else 0 end as otherout_amt,"
                + " case when billtype = '15' then distriamt else 0 end as otherout_distriamt,"
                + " case when billtype = '16' then baseqty else 0 end as tdiff_qty,"
                + " case when billtype = '16' then amt else 0 end as tdiff_amt,"
                + " case when billtype = '16' then distriamt else 0 end as tdiff_distriamt,"
                + " case when billtype = '17' then baseqty else 0 end as rdiff_qty,"
                + " case when billtype = '17' then amt else 0 end as rdiff_amt,"
                + " case when billtype = '17' then distriamt else 0 end as rdiff_distriamt,"
                + " case when billtype = '18' then baseqty else 0 end as transferwareout_qty,"
                + " case when billtype = '18' then amt else 0 end as transferwareout_amt,"
                + " case when billtype = '18' then distriamt else 0 end as transferwareout_distriamt,"
                + " case when billtype = '19' then baseqty else 0 end as transferwarein_qty,"
                + " case when billtype = '19' then amt else 0 end as transferwarein_amt,"
                + " case when billtype = '19' then distriamt else 0 end as transferwarein_distriamt,"
                + " case when billtype = '20' then baseqty"
                + "      when billtype = '21' then -baseqty else 0 end as sale_qty,"
                + " case when billtype = '20' then amt "
                + "      when billtype = '21' then -amt else 0 end as sale_amt,"
                + " case when billtype = '20' then distriamt "
                + "      when billtype = '21' then -distriamt else 0 end as sale_distriamt,"
                + " case when billtype = '22' then baseqty"
                + "      when billtype = '23' then -baseqty else 0 end as onorder_qty,"
                + " case when billtype = '22' then amt"
                + "      when billtype = '23' then -amt else 0 end as onorder_amt,"
                + " case when billtype = '22' then distriamt"
                + "      when billtype = '23' then -distriamt else 0 end as onorder_distriamt,"
                + " case when billtype = '30' then baseqty else 0 end as combin_qty,"
                + " case when billtype = '30' then amt else 0 end as combin_amt,"
                + " case when billtype = '30' then distriamt else 0 end as combin_distriamt,"
                + " case when billtype = '31' then baseqty else 0 end as combin_material_qty,"
                + " case when billtype = '31' then amt else 0 end as combin_material_amt,"
                + " case when billtype = '31' then distriamt else 0 end as combin_material_distriamt,"
                + " case when billtype = '32' then baseqty else 0 end as disass_qty,"
                + " case when billtype = '32' then amt else 0 end as disass_amt,"
                + " case when billtype = '32' then distriamt else 0 end as disass_distriamt,"
                + " case when billtype = '33' then baseqty else 0 end as disass_material_qty,"
                + " case when billtype = '33' then amt else 0 end as disass_material_amt,"
                + " case when billtype = '33' then distriamt else 0 end as disass_material_distriamt,"
                + " case when billtype = '34' then baseqty else 0 end as ldiff_qty,"
                + " case when billtype = '34' then amt else 0 end as ldiff_amt,"
                + " case when billtype = '34' then distriamt else 0 end as ldiff_distriamt,"
                + " case when billtype = '35' then baseqty else 0 end as zhhb_qty,"
                + " case when billtype = '35' then amt else 0 end as zhhb_amt,"
                + " case when billtype = '35' then distriamt else 0 end as zhhb_distriamt,"
                + " case when billtype = '36' then baseqty else 0 end as zhhb_material_qty,"
                + " case when billtype = '36' then amt else 0 end as zhhb_material_amt,"
                + " case when billtype = '36' then distriamt else 0 end as zhhb_material_distriamt,"
                + " case when billtype = '37' then baseqty else 0 end as zhcj_qty,"
                + " case when billtype = '37' then amt else 0 end as zhcj_amt,"
                + " case when billtype = '37' then distriamt else 0 end as zhcj_distriamt,"
                + " case when billtype = '38' then baseqty else 0 end as zhcj_material_qty,"
                + " case when billtype = '38' then amt else 0 end as zhcj_material_amt,"
                + " case when billtype = '38' then distriamt else 0 end as zhcj_material_distriamt,"
                + " case when billtype = '39' then baseqty else 0 end as jfdh_qty,"
                + " case when billtype = '39' then amt else 0 end as jfdh_amt,"
                + " case when billtype = '39' then distriamt else 0 end as jfdh_distriamt,"
                + " case when billtype = '40' then baseqty else 0 end as jfdh_cancel_qty,"
                + " case when billtype = '40' then amt else 0 end as jfdh_cancel_amt,"
                + " case when billtype = '40' then distriamt else 0 end as jfdh_cancel_distriamt,"
                + " case when billtype = '41' then baseqty else 0 end as kczzs_qty,"
                + " case when billtype = '41' then amt else 0 end as kczzs_amt,"
                + " case when billtype = '41' then distriamt else 0 end as kczzs_distriamt,"
                + " case when billtype = '42' then baseqty else 0 end as kczzs_cancel_qty,"
                + " case when billtype = '42' then amt else 0 end as kczzs_cancel_amt,"
                + " case when billtype = '42' then distriamt else 0 end as kczzs_cancel_distriamt"

                + " from dcp_stock_detail"
                + " where eid = '" + eId + "' and organizationno = '" + OrganizationNO + "' and to_char(accountdate, 'yyyymmdd') <= '" + CurEdate + "' "
                + " "
                + " union all"
                + " select a.pluno,a.featureno,a.batchno,a.proddate,to_char(a.edate) as bdate,a.organizationno,a.warehouse,a.baseunit,"
                + " a.distriprice,a.price,"
                + " 0 as priorityprice,0 as prioritydistriprice,"
                + " case when b.billtype = '00' then 0 else a.qty end as begin_qty,"
                + " case when b.billtype = '00' then 0 else a.amt end as begin_amt,"
                + " case when b.billtype = '00' then 0 else a.distriamt end as begin_distriamt,"
                + " case when b.billtype = '00' then 0 else a.price end as begin_price,"
                + " 0 as stockin_qty,0 as stockin_amt,0 as stockin_distriamt,"
                + " 0 as stockout_qty,0 as stockout_amt,0 as stockout_distriamt,"
                + " 0 as sstockin_qty,0 as sstockin_amt,0 as sstockin_distriamt,"
                + " 0 as sstockout_qty,0 as sstockout_amt,0 as sstockout_distriamt,"
                + " 0 as pstockin_qty,0 as pstockin_amt,0 as pstockin_distriamt,"
                + " 0 as material_qty,0 as material_amt,0 as material_distriamt,"
                + " 0 as transferout_qty,0 as transferout_amt,0 as transferout_distriamt,"
                + " 0 as transferin_qty,0 as transferin_amt,0 as transferin_distriamt,"
                + " 0 as loss_qty,0 as loss_amt,0 as loss_distriamt,"
                + " 0 as diff_qty,0 as diff_amt,0 as diff_distriamt,"
                + " 0 as otherin_qty,0 as otherin_amt,0 as otherin_distriamt,"
                + " 0 as otherout_qty,0 as otherout_amt,0 as otherout_distriamt,"
                + " 0 as adjust_qty,0 as adjust_amt,0 as adjust_distriamt,"
                + " 0 as rdiff_qty,0 as rdiff_amt,0 as rdiff_distriamt,"
                + " 0 as tdiff_qty,0 as tdiff_amt,0 as tdiff_distriamt,"
                + " 0 as onorder_qty, 0 as onorder_amt,0 as onorder_distriamt,"
                + " 0 as sale_qty, 0 as sale_amt,0 as onorder_distriamt,"
                + " 0 as transferwareout_qty, 0 as transferwareout_amt,0 as transferwareout_distriamt,"
                + " 0 as transferwarein_qty,  0 as transferwarein_amt,0 as transferwarein_distriamt,"
                + " 0 as in_transit_add_qty,0 as in_transit_add_amt,0 as in_transit_add_distriamt,"
                + " 0 as in_transit_minus_qty, 0 as in_transit_minus_amt,0 as in_transit_minus_distriamt,"
                + " 0 as combin_qty, 0 as combin_amt,0 as combin_distriamt,"
                + " 0 as combin_material_qty,0 as combin_material_amt,0 as combin_material_distriamt,"
                + " 0 as disass_qty, 0 as disass_amt,0 as disass_distriamt,"
                + " 0 as disass_material_qty,0 as disass_material_amt,0 as disass_material_distriamt,"
                + " 0 as ldiff_qty, 0 as ldiff_amt,0 as ldiff_distriamt,"
                + " 0 as zhhb_qty, 0 as zhhb_amt, 0 as zhhb_distriamt,"
                + " 0 as zhhb_materia_qty, 0 as zhhb_materia_amt,0 as zhhb_materia_distriamt,"
                + " 0 as zhcj_qty, 0 as zhcj_amt,0 as zhcj_distriamt,"
                + " 0 as zhcj_material_qty, 0 as zhcj_material_amt,0 as zhcj_material_distriamt,"
                + " 0 as jfdh_qty, 0 as jfdh_amt,0 as jfdh_distriamt,"
                + " 0 as jfdh_cancel_qty, 0 as jfdh_cancel_amt,0 as jfdh_cancel_distriamt,"
                + " 0 as kczzs_qty,0 as kczzs_amt,0 as kczzs_distriamt,"
                + " 0 as kczzs_cancel_qty,0 as kczzs_cancel_amt,0 as kczzs_cancel_distriamt "
//                + " 0 as TRANSFERIN_DISTRIAMT, 0 as STOCKIN_DISTRIAMT , 0 as STOCKOUT_DISTRIAMT  , 0 as TRANSFEROUT_DISTRIAMT , 0 as SSTOCKIN_DISTRIAMT  ,"
//                + " 0 as SSTOCKOUT_DISTRIAMT , 0 as LOSS_DISTRIAMT    , 0 as PSTOCKIN_DISTRIAMT  , 0 as DIFF_DISTRIAMT      ,   0 as ADJUST_DISTRIAMT    ,"
//                + " 0 as MATERIAL_DISTRIAMT  , 0 as IN_TRANSIT_ADD_DISTRIAMT  ,0 as IN_TRANSIT_MINUS_DISTRIAMT,0 as OTHERIN_DISTRIAMT   ,0 as OTHEROUT_DISTRIAMT  ,"
//                + " 0 as TDIFF_DISTRIAMT     , 0 as RDIFF_DISTRIAMT   , 0 as TRANSFERWAREOUT_DISTRIAMT,0 as TRANSFERWAREIN_DISTRIAMT , 0 as SALE_DISTRIAMT      ,"
//                + " 0 as ONORDER_DISTRIAMT   , 0 as COMBIN_DISTRIAMT  , 0 as COMBIN_MATERIAL_DISTRIAMT,0 as DISASS_DISTRIAMT ,0 as DISASS_MATERIAL_DISTRIAMT  ,"
//                + " 0 as LDIFF_DISTRIAMT     , 0 as ZHHB_DISTRIAMT    , 0 as ZHHB_MATERIAL_DISTRIAMT  ,0 as ZHCJ_DISTRIAMT   ,0 as ZHCJ_MATERIAL_DISTRIAMT   ,"
//                + " 0 as JFDH_DISTRIAMT      , 0 as JFDH_CANCEL_DISTRIAMT,0 as KCZZS_DISTRIAMT,0 as KCZZS_CANCEL_DISTRIAMT   ,0 as DISTRIAMT          "

                + " from dcp_stock_day a "
                + " left join dcp_stock_detail b "
                + " on a.eid = b.eid and a.organizationno = b.organizationno and a.warehouse = b.warehouse"
                + " and a.pluno = b.pluno"
                + " and (trim(a.batchno)=trim(b.batchno) or (trim(a.batchno) is null and trim(b.batchno) is null))"
                + " and (trim(a.featureno)=trim(b.featureno) or (trim(a.featureno) is null and trim(b.featureno) is null))"
                + " and b.billtype = '00'"
                + " and b.eid = '" + eId + "'"
                + " and b.organizationno = '" + OrganizationNO + "'"
                + " and to_char(b.accountdate, 'yyyymmdd') <= '" + CurEdate + "'"
                + " "
                + " where a.eid = '" + eId + "'"
                + " and a.organizationno = '" + OrganizationNO + "'"
                + " and a.edate = '" + PosPub.GetStringDate(CurEdate, -1) + "'"
                + " ) "
                + " group by pluno,featureno,batchno,organizationno,warehouse"
                + ") a"
                + ""
                + " left join dcp_goods b"
                + " on b.eid = '" + eId + "' and a.pluno = b.pluno"
                + " ";

        //		logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"日结:"+sqlbuf.toString());
        return sqlbuf;

    }

    /**
     * docTotalProcess 单据汇总处理
     * @param eId         企业编号
     * @param shopId      门店ID
     * @param accountDate 入账日期
     */
    private void docTotalProcess(String eId,String shopId,String accountDate,DCP_ShopEDateProcessReq req ) {
        StringBuffer sb = new StringBuffer();
        try {
            //删除120天之前的历史资料
            String delDate = PosPub.GetStringDate(accountDate, -120)  ;
            String sql=" delete from DCP_DOCTOTAL where EID = '"+eId+"' and ORGANIZATIONNO ='"+shopId+"' "
                    + " and ACCOUNT_DATE < '" + delDate + "'  ";
            ExecBean exec=new ExecBean(sql);
            this.addProcessData(new DataProcessBean(exec));

            sql=" delete from DCP_DOCTOTAL where EID = '"+eId+"' and ORGANIZATIONNO ='"+shopId+"' "
                    + " and ACCOUNT_DATE = '" + accountDate + "' "
                    + " and DOC_TYPE in ('1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18', "
                    + " '19','20','21','22','23','24','25','26','27','28','29' ) ";

            ExecBean exec1=new ExecBean(sql);
            this.addProcessData(new DataProcessBean(exec1));

            sb.append("select DOCNAME,DOCTYPE,DATAQTY from ( ");

            //--1.要货单
            sb.append(" select 'PORDER' as DOCNAME,N'1' as docType,sum(A)+sum(B) AS dataQty from ( "
                    + " select count(*) over(partition by a.porderno) A,count(*) as B "
                    + " from DCP_porder a inner join DCP_porder_detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.porderno=b.porderno  "
                    + " and a.status>='2' and a.process_status<>'E'  "
                    + " and a.account_date='"+accountDate+"' "
                    + " left join DCP_ORG c on a.EID=c.EID and a.receipt_org=c.organizationno and c.org_form='2' "
                    + " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' and c.organizationno is null group by a.porderno) " );

            //-- 2.配送收货 4.调拨入 6.其他入 10.移仓入
            sb.append(" union all "
                    +" select 'STOCKIN' as DOCNAME, N'4' as docType ,sum(A)+sum(B) AS dataQty from ( "
                    +"  select count(*) over(partition by a.stockinno) A,count(*) as B from DCP_STOCKIN a "
                    +"  inner join DCP_STOCKIN_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and "
                    + " a.stockinno=b.stockinno and a.BDATE=b.BDATE and a.status>='2' and a.process_status<>'E' "
                    + " and a.account_date='"+accountDate+"'  and a.doc_Type='1' "
                    +"  where a.EID='"+eId+"' and a.SHOPID='"+shopId+"'  group by a.stockinno) " );

            sb.append(" union all "
                    +" select 'STOCKIN' as DOCNAME, N'6' as docType ,sum(A)+sum(B) AS dataQty from ( "
                    +"  select count(*) over(partition by a.stockinno) A,count(*) as B from DCP_STOCKIN a "
                    +"  inner join DCP_STOCKIN_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and "
                    + " a.stockinno=b.stockinno and a.BDATE=b.BDATE and a.status>='2' and a.process_status<>'E' "
                    + " and a.account_date='"+accountDate+"'  and a.doc_Type='3' "
                    +"  where a.EID='"+eId+"' and a.SHOPID='"+shopId+"'  group by a.stockinno) " );

            sb.append(" union all "
                    +" select 'STOCKIN' as DOCNAME, N'10' as docType ,sum(A)+sum(B) AS dataQty from ( "
                    +"  select count(*) over(partition by a.stockinno) A,count(*) as B from DCP_STOCKIN a "
                    +"  inner join DCP_STOCKIN_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and "
                    + " a.stockinno=b.stockinno and a.BDATE=b.BDATE and a.status>='2' and a.process_status<>'E' "
                    + " and a.account_date='"+accountDate+"'  and a.doc_Type='4' "
                    +"  where a.EID='"+eId+"' and a.SHOPID='"+shopId+"'  group by a.stockinno) " );

            sb.append(" union all "
                    +" select 'STOCKIN' as DOCNAME, N'2' as docType ,sum(A)+sum(B) AS dataQty from ( "
                    +"  select count(*) over(partition by a.stockinno) A,count(*) as B from DCP_STOCKIN a "
                    +"  inner join DCP_STOCKIN_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and "
                    + " a.stockinno=b.stockinno and a.BDATE=b.BDATE and a.status>='2' and a.process_status<>'E' "
                    + " and a.account_date='"+accountDate+"'  and a.doc_Type='0' "
                    +"  where a.EID='"+eId+"' and a.SHOPID='"+shopId+"'  group by a.stockinno) " );


            //-- 3.退货出库 5.调拨出 7.其他出 11.移仓出
            sb.append(" union all  "
                    +"  select 'STOCKOUT' as DOCNAME, N'5' as docType, sum(A)+sum(B) AS dataQty from ( "
                    +"  select count(*) over(partition by a.stockOUTno) A,count(*) as B from DCP_STOCKOUT a "
                    +"  inner join DCP_STOCKOUT_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.stockOUTno=b.stockOUTno and a.BDATE=b.BDATE "
                    + " and a.status>='2' and a.process_status<>'E'  and a.account_date='"+accountDate+"' and a.Doc_Type='1'  "
                    +"  where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.stockOUTno ) ");

            sb.append(" union all  "
                    +"  select 'STOCKOUT' as DOCNAME, N'7' as docType, sum(A)+sum(B) AS dataQty from ( "
                    +"  select count(*) over(partition by a.stockOUTno) A,count(*) as B from DCP_STOCKOUT a "
                    +"  inner join DCP_STOCKOUT_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.stockOUTno=b.stockOUTno and a.BDATE=b.BDATE "
                    + " and a.status>='2' and a.process_status<>'E'  and a.account_date='"+accountDate+"' and a.Doc_Type='3'  "
                    +"  where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.stockOUTno ) ");

            sb.append(" union all  "
                    +"  select 'STOCKOUT' as DOCNAME, N'11' as docType, sum(A)+sum(B) AS dataQty from ( "
                    +"  select count(*) over(partition by a.stockOUTno) A,count(*) as B from DCP_STOCKOUT a "
                    +"  inner join DCP_STOCKOUT_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.stockOUTno=b.stockOUTno and a.BDATE=b.BDATE "
                    + " and a.status>='2' and a.process_status<>'E'  and a.account_date='"+accountDate+"' and a.Doc_Type='4'  "
                    +"  where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.stockOUTno ) ");

            sb.append(" union all  "
                    +"  select 'STOCKOUT' as DOCNAME, N'3' as docType, sum(A)+sum(B) AS dataQty from ( "
                    +"  select count(*) over(partition by a.stockOUTno) A,count(*) as B from DCP_STOCKOUT a "
                    +"  inner join DCP_STOCKOUT_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.stockOUTno=b.stockOUTno and a.BDATE=b.BDATE "
                    + " and a.status>='2' and a.process_status<>'E'  and a.account_date='"+accountDate+"' and (a.Doc_Type='0' or a.Doc_Type='2')  "
                    +"  where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.stockOUTno ) ");

            //--8.采购入
            sb.append("union all "
                    + " select 'SSTOCKIN' as DOCNAME,N'8'as docType,sum(A)+sum(B) AS dataQty from ("
                    + " select count(*) over(partition by a.SSTOCKINNO) A,count(*) as B "
                    + " from DCP_SSTOCKIN a inner join DCP_SSTOCKIN_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.SSTOCKINNO=b.SSTOCKINNO and a.BDATE=b.BDATE "
                    + " and a.status>='2' and a.process_status<>'E' "
                    + " and a.account_date='"+accountDate+"' "
                    + " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.SSTOCKINNO) ");

            //--9.采购退
            sb.append("union all "
                    + " select 'SSTOCKOUT' as DOCNAME,N'9'as docType,sum(A)+sum(B) AS dataQty from ( "
                    + " select count(*) over(partition by a.SSTOCKOUTNO) A,count(*) as B "
                    + " from DCP_SSTOCKOUT a inner join  DCP_SSTOCKOUT_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.SSTOCKOUTNO=b.SSTOCKOUTNO and a.BDATE=b.BDATE "
                    + " and a.status>='2' and a.process_status<>'E' "
                    + " and a.account_date='"+accountDate+"' "
                    + " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.SSTOCKOUTNO) ");

            //--12.盘点
            sb.append("union all "
                    + " select 'STOCKTAKE' as DOCNAME,N'12'as docType,sum(A)+sum(B) AS dataQty from ( "
                    + " select count(*) over(partition by a.STOCKTAKENO) A,count(*) as B from DCP_STOCKTAKE a "
                    + " inner join DCP_STOCKTAKE_DETAIL b on a.eid=b.eid and a.SHOPID=b.SHOPID and a.STOCKTAKENO=b.STOCKTAKENO "
                    + " and a.status>='2' and a.process_status<>'E' and a.account_date='"+accountDate+"' "
                    + " where a.eid='"+eId+"'  and a.SHOPID='"+shopId+"' and (a.doc_type<>'0' or a.notgoodsmode<>'1') group by a.STOCKTAKENO "
                    + " union all "
                    + " select count(*) over(partition by a.STOCKTAKENO) A,count(*) as B from DCP_STOCKTAKE a "
                    + " inner join DCP_STOCKTAKE_DETAIL b on a.eid=b.eid and a.SHOPID=b.SHOPID and a.STOCKTAKENO=b.STOCKTAKENO "
                    + " and a.status>='2' and a.process_status<>'E' and a.account_date='"+accountDate+"' "
                    + " where a.eid='"+eId+"'  and a.SHOPID='"+shopId+"' and (a.doc_type='0' and a.notgoodsmode='1') group by a.STOCKTAKENO "
                    + " union all "
                    + " select 0 as A,count(*) as B  from DCP_adjust_detail a "
                    + " inner join DCP_adjust b on a.eid=b.eid and a.SHOPID=b.SHOPID and a.adjustno=b.adjustno "
                    + " inner join DCP_STOCKTAKE d on b.eid=d.eid and b.SHOPID=d.SHOPID and b.ofno=d.stocktakeno "
                    + " and d.status>='2' and d.process_status<>'E' and d.account_date='"+accountDate+"' and (d.doc_type='0' and d.notgoodsmode='1') "
                    + " left join DCP_stocktake_detail c on c.eid=a.eid and c.SHOPID=a.SHOPID "
                    + " and c.stocktakeno=b.ofno and c.pluno||c.batch_no||c.prod_date =a.pluno||a.batch_no||a.prod_date "
                    + " where a.eid='"+eId+"'  and a.organizationno='"+shopId+"'  and c.pluno is null  )");

            //--13.完工入库 14.组合单 15.拆解单 18.转换合并 29.转换拆解
            // 这段查询有效能问题，改成一条SQL查所有类型的 BY JZMA 20190920
            sb.append(" union all "
                    + " select 'PSTOCKIN' as DOCNAME,  "
                    + " case when docType='0' then N'13'  when docType='1' then N'14'  when docType='2' then N'15'  when docType='3' then N'18'  when docType='4' then N'29'  end as docType, "
                    + " sum(A)+sum(B)+sum(C) AS dataQty from ( "
                    + " select a.Doc_Type as docType ,count(*) over( partition by a.PStockInno,a.doc_type) A,count(*) as B,0 as c from DCP_PStockIn a "
                    + " inner join DCP_PStockIn_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.PStockInno=b.PStockInno "
                    + " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' "
                    + " and ((a.doc_type in ('0','1','3') and (b.scrap_qty <>0 or b.baseqty <> 0)) or a.doc_type not in ('0','1','3')) "
                    + " and a.status>='2' and a.process_status<>'E' "
                    + " and a.account_date='"+accountDate+"' "
                    + " group by a.PStockInno,a.Doc_Type "
                    + " union all "
                    + " select b.Doc_Type as docType,0 as A,0 as B,count(*) as C from DCP_pstockin_material a  "
                    + " inner join DCP_pstockin b  on a.EID=b.EID and a.SHOPID=b.SHOPID and a.pstockinno=b.pstockinno "
                    + " inner join dcp_pstockin_detail c on a.EID=c.EID and a.SHOPID=c.SHOPID and a.PStockInno=c.PStockInno and a.mitem=c.item "
                    + " where a.EID='"+eId+"' and b.SHOPID='"+shopId+"'"
                    + " and b.status>='2' and b.process_status<>'E' "
                    + " and b.account_date='"+accountDate+"' "
                    + " and ((b.doc_type in ('0','1','3') and (c.scrap_qty <>0 or c.baseqty <> 0)) or b.doc_type not in ('0','1','3'))  "
                    + " group by a.PStockInno,b.Doc_Type"
                    + " )  group by docType "  );
            sb.append("union all "
                    + " select 'LSTOCKOUT' as DOCNAME,N'16'as docType,sum(A)+sum(B) AS dataQty from ( "
                    + " select count(*) over(partition by a.lstockoutno) A,count(*) as B "
                    + " from DCP_lstockout a inner join  DCP_lstockout_detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.lstockoutno=b.lstockoutno and a.BDATE=b.BDATE "
                    + " and a.status>='2' and a.process_status<>'E'  "
                    + " and a.account_date='"+accountDate+"' "
                    + " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.lstockoutno) ");

            //--17.费用单
            sb.append("union all "
                    + " select 'BFEE' as DOCNAME,N'17'as docType,sum(A)+sum(B) AS dataQty from ( "
                    + " select count(*) over(partition by a.Bfeeno) A,count(*) as B "
                    + " from DCP_Bfee a inner join DCP_Bfee_Detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.Bfeeno=b.Bfeeno "
                    + " and a.status>='2' and a.process_status<>'E'  "
                    + " and a.account_date='"+accountDate+"' "
                    + " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.Bfeeno)  ");

            //--19.销售单
            sb.append(" union all "
                    + " select 'DCP_SALE' as DOCNAME, N'19' as docType ,sum(A)+sum(B)+sum(C)+SUM(D) AS dataQty from ("
                    + " select count(*) over(partition by a.SALENO) A,count(*) as B,0 as C,0 as D from DCP_SALE a "
                    + " inner join DCP_SALE_DETAIL b on a.EID=b.EID and a.SHOPID=b.SHOPID and A.SALENO = B.SALENO ");

            //【ID1029413】【菲尔雪V3.2.0.1】DCP_DOCTOTAL的零售单数据统计不到，实际笔数808 门店：23001 日期：20221031 by jinzma 20221101
            //是否分区标记 0:未分区 1:已分区
            if (PosPub.tablePartition_dcp_sale.equals("1")){
                sb.append(" and a.partition_date=b.partition_date");
            }
            sb.append(" where A.status='100' AND B.status='100' AND A.EID ='"+eId+"' AND A.SHOPID ='"+shopId+"' ");

            //销售单判断分区字段
            if (PosPub.tablePartition_dcp_sale.equals("1")){
                sb.append(" and a.partition_date ='"+accountDate+"' ");
            }else{
                sb.append(" and a.bdate ='"+accountDate+"' ");
            }
            sb.append(" group by a.SALENO "

                    + " union all "
                    + " SELECT 0 as A,0 as B,count(*) as C,0 as D FROM DCP_SALE_PAY A "
                    + " WHERE A.EID ='"+eId+"' AND A.SHOPID ='"+shopId+"'  ");

            //销售单判断分区字段
            if (PosPub.tablePartition_dcp_sale.equals("1")) {
                sb.append( " AND A.PARTITION_DATE="+accountDate+" ");
            }else{
                sb.append( " AND A.bdate="+accountDate+" ");
            }

            sb.append(" union all "
                    + " SELECT 0 as A,0 as B,0 as C,count(*) as D FROM DCP_SALE_DETAIL_AGIO A "
                    + " INNER JOIN DCP_SALE B ON A.EID=B.EID AND A.SHOPID=B.SHOPID ");

            //【ID1029413】【菲尔雪V3.2.0.1】DCP_DOCTOTAL的零售单数据统计不到，实际笔数808 门店：23001 日期：20221031 by jinzma 20221101
            //是否分区标记 0:未分区 1:已分区
            if (PosPub.tablePartition_dcp_sale.equals("1")){
                sb.append(" and a.partition_date=b.partition_date");
            }
            sb.append(" AND A.SALENO=B.SALENO     "
                    //	+ " WHERE  A.status='100' AND"
                    + " WHERE  "
                    + " A.EID ='"+eId+"' AND A.SHOPID ='"+shopId+"' " );
            //销售单判断分区字段
            if (PosPub.tablePartition_dcp_sale.equals("1")) {
                sb.append( "AND B.PARTITION_DATE="+accountDate+" ");
            }else{
                sb.append( "AND B.bdate="+accountDate+" ");
            }
            sb.append( ")");

            //--20.销售订单
            sb.append(" union all "
                    + " select 'DCP_ORDER' as DOCNAME, N'20' as docType ,sum(A)+sum(B)+sum(C) AS dataQty from ("
                    + " select count(*) over(partition by a.ORDERNO) A,count(*) as B,0 as C from dcp_order a "
                    + " inner join dcp_order_DETAIL b on a.EID=b.EID and A.ORDERNO = B.ORDERNO ");
            //【ID1029413】【菲尔雪V3.2.0.1】DCP_DOCTOTAL的零售单数据统计不到，实际笔数808 门店：23001 日期：20221031 by jinzma 20221101
            //是否分区标记 0:未分区 1:已分区
            if (PosPub.tablePartition_dcp_order.equals("1")){
                sb.append(" and a.partition_date=b.partition_date ");
            }
            sb.append(" where A.status='100'  AND A.EID ='"+eId+"' AND A.SHOP ='"+shopId+"' ");

            if (PosPub.tablePartition_dcp_order.equals("1")){
                sb.append(" AND A.partition_date ='"+accountDate+"' ");
            }else{
                sb.append(" AND A.bdate ='"+accountDate+"' ");
            }

            sb.append(" group by a.ORDERNO "
                    + " union all "
                    + " SELECT 0 as A,0 as B,count(*) as C FROM dcp_order_PAY A "
                    + " WHERE A.status='100' AND A.EID ='"+eId+"' AND A.SHOPID ='"+shopId+"' ");

            if (PosPub.tablePartition_dcp_order.equals("1")){
                sb.append(" AND A.partition_date ='"+accountDate+"' ");
            }else{
                sb.append(" AND A.bdate ='"+accountDate+"' ");
            }
            sb.append(" )");

            //21.大客户收款单
            sb.append(" union all"
                    + " SELECT 'DCP_CUSTOMER_RETURN' as DOCNAME, N'21' as docType,SUM(S) AS dataQty FROM ( "
                    + " SELECT COUNT(*) S FROM DCP_CUSTOMER_RETURN A "
                    + " WHERE A.EID ='"+eId+"' AND A.SHOPID='"+shopId+"' AND A.BDATE ='"+accountDate+"' "
                    + " UNION ALL "
                    + " SELECT COUNT(*) S FROM DCP_CUSTOMER_RETURN_DETAIL A LEFT JOIN DCP_CUSTOMER_RETURN B ON A.RETURNNO = B.RETURNNO "
                    + " WHERE A.EID ='"+eId+"' AND A.SHOPID='"+shopId+"' AND A.BDATE ='"+accountDate+"'  "
                    + " UNION ALL "
                    + " SELECT COUNT(*) S FROM DCP_CUSTOMER_RETURN_PAY A LEFT JOIN DCP_CUSTOMER_RETURN B ON A.RETURNNO = B.RETURNNO "
                    + " WHERE A.EID ='"+eId+"' AND A.SHOPID='"+shopId+"' AND B.BDATE ='"+accountDate+"' ) "
                    + " ");

            //22.班次记录信息
            sb.append(" union all"
                    + " select 'DCP_SQUAD' as docname, N'22' as doctype,sum(a)+sum(b) as dataqty from ("
                    + " select count(*) as a,0 as b from dcp_squad a "
                    + " where a.eid ='"+eId+"' and a.shopid ='"+shopId+"' and a.bdate ='"+accountDate+"' and a.status = '100' and a.endsquad = 'Y'"
                    + " union all"
                    + " select 0 as a,count(*) as b from dcp_squad a"
                    + " inner join dcp_squad_detail b"
                    + " on a.eid=b.eid and a.shopid=b.shopid and a.machine=b.machine and a.bdate=b.bdate and a.squadno=b.squadno and a.opno=b.opno"
                    + " where a.eid ='"+eId+"' and a.shopid ='"+shopId+"' and a.bdate ='"+accountDate+"' and a.status = '100' and a.endsquad = 'Y'"
                    + " )");
            //23.卡充值信息
            sb.append(" union all ");
            sb.append(""
                    + " select 'CRM_CARDRECHARGE' as docname, N'23' as doctype,sum(a) dataqty from ("
                    + " select count(*) as a  from CRM_CARDRECHARGE  "
                    + " where eid= '"+eId+"' "
                    + " and NVL(SHOPID,COMPANYID)='"+shopId+"' "
                    + " and status=100 "
                    + " and to_char(BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(*) as a from CRM_CARDRECHARGEITEM a"
                    + " left join CRM_CARDRECHARGE b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100 and a.eid='"+eId+"'  and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    + " and to_char(b.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(a.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(a.eid) as a from CRM_CARDRECHARGEPAY a"
                    + " left join CRM_CARDRECHARGE b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100 and a.eid='"+eId+"'  and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    + " and to_char(b.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(a.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " )");
            //24.卡销售信息
            sb.append(" union all"
                    + " select 'CRM_CARDSALE' as docname, N'24' as doctype,sum(a) dataqty from ("
                    + " select count(*) as a  from CRM_CARDSALE  "
                    + " where eid= '"+eId+"' "
                    + " and NVL(SHOPID,COMPANYID)='"+shopId+"' "
                    + " and status=100 "
                    + " and to_char(BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(*) as a from CRM_CARDSALEITEM a"
                    + " left join CRM_CARDSALE b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100 "
                    + " and a.eid ='"+eId+"' "
                    + " and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    + " and to_char(b.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(a.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(*) as a from CRM_CARDSALEPAY a"
                    + " left join CRM_CARDSALE b on b.eid=a.eid and b.billno=a.billno "
                    + " where b.status=100 "
                    + " and a.eid='"+eId+"' "
                    + " and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    + " and to_char(b.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(a.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " )"
            );
            //25.券销售信息
            sb.append(" union all"
                    + " select 'CRM_COUPONSALE' as docname, N'25' as doctype,sum(a) dataqty from ("
                    + " select count(*) as a from CRM_COUPONSALE"
                    + " where eid='"+eId+"' "
                    + " and NVL(SHOPID,COMPANYID)='"+shopId+"' "
                    + " and status=100 "
                    + " and to_char(BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(*) as a from CRM_COUPONSALEITEM2 a"
                    + " left join CRM_COUPONSALE b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100 "
                    + " and a.eid='"+eId+"' "
                    + " and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    + " and to_char(b.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //	+ " and to_char(a.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(a.eid) as COUNT from CRM_COUPONSALEPAY a"
                    + " left join CRM_COUPONSALE b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100"
                    + " and a.eid='"+eId+"' "
                    + " and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    + " and to_char(b.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(a.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + ")"
            );
            //26.券销退信息
            sb.append(" union all"
                    + " select 'CRM_COUPONREJECT' as docname, N'26' as doctype,sum(a) dataqty from ("
                    + " select count(*) as a from CRM_COUPONREJECT"
                    + " where eid='"+eId+"' "
                    + " and NVL(SHOPID,COMPANYID)='"+shopId+"' "
                    + " and status=100 "
                    + " and to_char(BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(*) as a from CRM_COUPONREJECTITEM a"
                    + " left join CRM_COUPONREJECT b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100 "
                    + " and a.eid='"+eId+"' "
                    + " and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    + " and to_char(B.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(a.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(*) as a from CRM_COUPONREJECTPAY a"
                    + " left join CRM_COUPONREJECT b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100"
                    + " and a.eid='"+eId+"' "
                    + " and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    //+ " and to_char(a.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " and to_char(B.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    + ")"
            );
            //27.消费单信息
            sb.append(" union all"
                    + " select 'CRM_COUPONSALE' as docname, N'27' as doctype,sum(a) dataqty from ("
                    + " select count(*) as a from CRM_CONSUME"
                    + " where eid='"+eId+"' "
                    + " and NVL(SHOPID,COMPANYID)='"+shopId+"' "
                    + " and status=100 "
                    //+ " and to_char(LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " and to_char(BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(*) as a from CRM_CONSUME_CARD a"
                    + " left join CRM_CONSUME b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100 "
                    + " and a.eid='"+eId+"' "
                    + " and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    //+ " and to_char(b.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + " and to_char(b.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    + " union all "
                    + " select count(*) as a from CRM_CONSUME_COUPON a"
                    + " left join CRM_CONSUME b on b.eid=a.eid and b.billno=a.billno"
                    + " where b.status=100"
                    + " and a.eid='"+eId+"' "
                    + " and NVL(b.SHOPID,b.COMPANYID)='"+shopId+"' "
                    + " and to_char(b.BILLDATE,'YYYYMMDD') = '"+accountDate+"' "
                    //+ " and to_char(b.LASTMODITIME,'YYYYMMDD') = '"+accountDate+"' "
                    + ")"
            );
            //--28.差异申述
            sb.append("union all "
                    + " select 'DIFFERENCE' as DOCNAME,N'28'as docType,sum(A)+sum(B) AS dataQty from ( "
                    + " select count(*) over(partition by a.DIFFERENCENO) A,count(*) as B "
                    + " from DCP_difference a inner join DCP_difference_detail b on a.EID=b.EID and a.SHOPID=b.SHOPID and a.DIFFERENCENO=b.DIFFERENCENO "
                    + " and a.status>='2' "
                    + " and a.process_status<>'E' "
                    + " and a.account_date='"+accountDate+"' "
                    + " where a.EID='"+eId+"' and a.SHOPID='"+shopId+"' group by a.DIFFERENCENO)  ");
            sb.append(" )" );
            List<Map<String, Object>> getQdata = this.doQueryData(sb.toString(),null);
            if (getQdata != null && !getQdata.isEmpty()) {
                boolean doc13=false;
                boolean doc14=false;
                boolean doc15=false;
                boolean doc18=false;
                boolean doc29=false;

                for (Map<String, Object> oneData :getQdata ) {
                    //String docName = oneData.get("DOCNAME").toString();
                    String docType = oneData.get("DOCTYPE").toString();
                    String dataQty = oneData.get("DATAQTY").toString();

                    if (Check.Null(dataQty)) dataQty="0"; //单据笔数为零的也保存
                    if (docType.equals("13")) doc13=true;
                    if (docType.equals("14")) doc14=true;
                    if (docType.equals("15")) doc15=true;
                    if (docType.equals("18")) doc18=true;
                    if (docType.equals("29")) doc29=true;

                    if (!Check.Null(docType) && !Check.Null(dataQty) )
                    {
                        insertDocTotal(eId,shopId,docType,accountDate,dataQty);
                    }
                }
                if (!doc13) insertDocTotal(eId,shopId,"13",accountDate,"0");
                if (!doc14) insertDocTotal(eId,shopId,"14",accountDate,"0");
                if (!doc15) insertDocTotal(eId,shopId,"15",accountDate,"0");
                if (!doc18) insertDocTotal(eId,shopId,"18",accountDate,"0");
                if (!doc29) insertDocTotal(eId,shopId,"29",accountDate,"0");

            }
        } catch (Exception e) {
            logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"单据汇总资料保存失败：" + e.getMessage() + "\r\n");
        }
    }

    /**
     * insertDocTotal 单据汇总表写入
     * @param eId  企业编号
     * @param shopId  门店编号
     * @param docType  单据类型
     * @param accountDate 入账日期
     * @param dataQty 笔数
     */
    private void insertDocTotal(String eId,String shopId,String docType,String accountDate,String dataQty ) {
        String[] columns = {"EID", "ORGANIZATIONNO","DOC_TYPE","ACCOUNT_DATE","DATA_QTY","STATUS"
        };
        DataValue[]	insValue = new DataValue[]{
                new DataValue(eId, Types.VARCHAR),
                new DataValue(shopId, Types.VARCHAR),
                new DataValue(docType, Types.VARCHAR),
                new DataValue(accountDate, Types.VARCHAR),
                new DataValue(dataQty, Types.VARCHAR),
                new DataValue("100", Types.VARCHAR),
        };
        InsBean ib = new InsBean("DCP_DOCTOTAL", columns);
        ib.addValues(insValue);
        this.addProcessData(new DataProcessBean(ib));
    }


    /**
     * docTotalProcess 单据汇总处理
     * @param eId         企业编号
     * @param shopId      门店ID
     * @param accountDate 入账日期
     */
    private void REP_SALE_GOODS_DAY_Process(String eId,String shopId,String accountDate,DCP_ShopEDateProcessReq req ) {
        StringBuffer sb = new StringBuffer();
        try {
            //删除120天之前的历史资料
            String delDate = PosPub.GetStringDate(accountDate, -366)  ;
            String sql=" delete from REP_SALE_GOODS_DAY where EID = '"+eId+"' and SHOPID ='"+shopId+"' "
                    + " and  TO_CHAR(SALEDATE,'YYYYMMDD') < '" + delDate + "'  ";
            ExecBean exec=new ExecBean(sql);
            this.addProcessData(new DataProcessBean(exec));
            sql=" delete from REP_SALE_GOODS_DAY where EID = '"+eId+"' and SHOPID ='"+shopId+"' "
                    + " and  TO_CHAR(SALEDATE,'YYYYMMDD') = '" + accountDate + "' ";
            ExecBean exec1=new ExecBean(sql);
            this.addProcessData(new DataProcessBean(exec1));
            sb.append("	SELECT a.EID,a.SHOPID,b.PLUNO,b.UNIT,"
                    + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.qty else b.qty end )) as qty,"
                    + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.amt else amt end)) as amt,"
                    + " sum((case when a.type='1' or a.type='2' or a.type='4' then -(b.qty*b.oldprice) else b.qty*b.oldprice end )) as old_amt,"
                    + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.disc else b.disc end)) as disc ,"
                    + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.AMT_MERRECEIVE else AMT_MERRECEIVE end)) as AMT_MERRECEIVE,"
                    + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.DISC_MERRECEIVE else DISC_MERRECEIVE end)) as DISC_MERRECEIVE,"
                    + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.AMT_CUSTPAYREAL else AMT_CUSTPAYREAL end)) as AMT_CUSTPAYREAL,"
                    + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.DISC_CUSTPAYREAL else DISC_CUSTPAYREAL end)) as DISC_CUSTPAYREAL,"
                    + " COUNT(*) AS CT,"
                    + "'',	'',	SYSDATE,	'',		SYSDATE "
                    + " FROM dcp_sale a "
                    + " INNER JOIN dcp_sale_detail b ON a.eid=b.eid AND a.shopid=b.shopid AND a.saleno=b.saleno " );
            //【ID1029413】【菲尔雪V3.2.0.1】DCP_DOCTOTAL的零售单数据统计不到，实际笔数808 门店：23001 日期：20221031 by jinzma 20221101
            //是否分区标记 0:未分区 1:已分区
            if (PosPub.tablePartition_dcp_sale.equals("1")) {
                sb.append(" and a.partition_date=b.partition_date");
            }
            sb.append(" WHERE a.eid='"+eId+"' and a.SHOPID='"+shopId+"' ");
            if (PosPub.tablePartition_dcp_sale.equals("1")) {
                sb.append(" AND a.partition_date='"+accountDate+"' ");
            }else{
                sb.append(" AND a.bdate='"+accountDate+"' ");
            }
            sb.append(" AND a.type!=16 "
                    + " AND (b.PackageMaster='N' or b.PackageMaster is null) "//非套餐+套餐子商品
                    + " GROUP BY a.EID,a.SHOPID,b.PLUNO,b.UNIT "
            );
            List<Map<String, Object>> getQdata = this.doQueryData(sb.toString(),null);
            String[] columns = {"EID", "SALEDATE","SHOPID","PLUNO","UNIT",
                    "QTY","AMT","ORIAMT","DISCAMT",
                    "AMT_MERRECEIVE","DISC_MERRECEIVE","AMT_CUSTPAYREAL","DISC_CUSTPAYREAL",
                    "CT",
                    "REMARK","CREATEOPID","CREATETIME","LASTMODIOPID","LASTMODITIME"};
            if (getQdata != null && !getQdata.isEmpty()) {
                for (Map<String, Object> oneData :getQdata ) {
                    Calendar cal = Calendar.getInstance();//获得当前时间
                    SimpleDateFormat dfa=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String saledate =accountDate.substring(0,4)+"-"+accountDate.substring(4,6)+"-"+accountDate.substring(6,8)
                            +" 00:00:00";
                    String now = dfa.format(cal.getTime());
                    DataValue[]	insValue = new DataValue[]{
                            new DataValue(oneData.get("EID").toString(), Types.VARCHAR),
                            new DataValue(saledate, Types.DATE),
                            new DataValue(oneData.get("SHOPID").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("UNIT").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("QTY").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("AMT").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("OLD_AMT").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("DISC").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("AMT_MERRECEIVE").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("DISC_MERRECEIVE").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("AMT_CUSTPAYREAL").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("DISC_CUSTPAYREAL").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("CT").toString(), Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(now, Types.DATE),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(now, Types.DATE),
                    };
                    InsBean ib = new InsBean("REP_SALE_GOODS_DAY", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }

            this.doExecuteDataToDB();

        } catch (Exception e) {
            logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"商品销售日汇总档保存失败：" + e.getMessage() + "\r\n");
        }
    }

    //
    private void updateStockdetailPriceProcess(String eId, String shopId, String startEdate){

        try {
        	ArrayList<DataProcessBean> DataPB = new ArrayList<DataProcessBean>();
            String sql = "SELECT  DISTINCT PLUNO  FROM DCP_STOCK_DETAIL"
            		+ " WHERE EID='"+eId+"' AND  ORGANIZATIONNO='"+shopId+"' "
            				+ " AND BILLTYPE in ('20','21') and to_char(ACCOUNTDATE,'yyyymmdd')<='"+startEdate+"' AND nvl(DISTRIPRICE,0)=0 ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (CollectionUtils.isEmpty(getQData)){
            	return;
            }
            sql = "select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' and belfirm is not null ";
            List<Map<String, Object>> getBelfirmQData = this.doQueryData(sql,null);
            if (CollectionUtils.isEmpty(getBelfirmQData)){
                logger.error("更新库存异动流水进货价格及进货金额失败：门店："+shopId+" 所属公司未设置"+ "\r\n");
                return;
            }
            String belfirm = getBelfirmQData.get(0).get("BELFIRM").toString();
            //sql = "select pluno,baseunit as punit,baseunit,N'1'as unitratio,price,supprice from dcp_goods where eid='"+eId+"'  ";  //and status='100'
            sql = "select pluno,baseunit as punit,baseunit,N'1'as unitratio,price,supprice from dcp_goods where eid='"+eId+"'  "
            		+ " AND PLUNO IN ( SELECT  DISTINCT PLUNO  FROM DCP_STOCK_DETAIL WHERE EID='"+eId+"' AND  ORGANIZATIONNO='"+shopId+"'  AND BILLTYPE in ('20','21') and to_char(ACCOUNTDATE,'yyyymmdd')<='"+startEdate+"' AND nvl(DISTRIPRICE,0)=0 ) ";
            List<Map<String, Object>> getGoodsList = this.doQueryData(sql,null);
            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eId, belfirm, shopId, getGoodsList,belfirm);
            for (Map<String, Object> goods:getGoodsList) {
                String pluNo = goods.get("PLUNO").toString();
                String baseUnit = goods.get("BASEUNIT").toString();
                //商品取价
                Map<String, Object> condiV= new HashMap<>();
                condiV.put("PLUNO",pluNo);
                condiV.put("PUNIT",baseUnit);
                List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPrice, condiV, false);
                condiV.clear();
                String price="0";
                String distriPrice="0";
                String defPrice="0";
                String defDistriPrice="0";
                //用商品基础资料来兜底
                if (!Check.Null(goods.get("PRICE").toString())){
                    defPrice = goods.get("PRICE").toString();
                }
                if (!Check.Null(goods.get("SUPPRICE").toString())){
                    defDistriPrice = goods.get("SUPPRICE").toString();
                }
                if(priceList!=null && priceList.size()>0 ) {
                    price=priceList.get(0).get("PRICE").toString();
                    distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                }
                if (new BigDecimal(price).compareTo(BigDecimal.ZERO)==0 && new BigDecimal(defPrice).compareTo(BigDecimal.ZERO)>0){
                    price=defPrice;
                }
                if (new BigDecimal(distriPrice).compareTo(BigDecimal.ZERO)==0 && new BigDecimal(defDistriPrice).compareTo(BigDecimal.ZERO)>0){
                    distriPrice=defDistriPrice;
                }
                String execSql="UPDATE DCP_STOCK_DETAIL SET DISTRIPRICE="+distriPrice+"*UNITRATIO,DISTRIAMT="+distriPrice+"*UNITRATIO*QTY "
                		+ " WHERE EID='"+eId+"' AND ORGANIZATIONNO='"+shopId+"' AND BILLTYPE IN ('20','21') "
                				+ " AND to_char(ACCOUNTDATE,'yyyymmdd')<='"+startEdate+"' AND nvl(DISTRIPRICE,0)=0 " ;
				ExecBean execBean1 = new ExecBean(execSql);
				DataPB.add(new DataProcessBean(execBean1));    
            }
			if (DataPB.size()>0)
			{
				this.dao.useTransactionProcessData(DataPB);
			}
            this.doExecuteDataToDB();
        }catch (Exception e) {
            logger.error("更新库存异动流水进货价格及进货金额失败：" + e);
        }
    }

    /**
     * 销售数量汇总（统计一个月内的）
     * @param eId         企业编号
     * @param shopId      门店ID
     */
    private void SumSaleQty(String eId,String shopId,DCP_ShopEDateProcessReq req) {
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        try {

            //当天已经汇总过就不再汇总
            {
                String sql = " select sdate from dcp_sale_goods where eid='" + eId + "' and shopid='" + shopId + "' ";
                List<Map<String, Object>> getQdata = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(getQdata)){
                    if (sDate.equals(getQdata.get(0).get("SDATE").toString())){
                        return;
                    }
                }
            }

            //删除DCP_SALE_GOODS
            DelBean db1 = new DelBean("DCP_SALE_GOODS");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            //新增DCP_SALE_GOODS(门店)
            String beginDate = PosPub.GetStringDate(sDate, -30)  ;
            String[] columns = {"EID","SHOPID","PLUNO","QTY","SDATE"};
            StringBuffer sb = new StringBuffer();
            {
                sb.append("	SELECT a.EID,a.SHOPID,b.PLUNO,"
                        + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.qty else b.qty end )) as qty"
                        + " FROM dcp_sale a "
                        + " INNER JOIN dcp_sale_detail b ON a.eid=b.eid AND a.shopid=b.shopid AND a.saleno=b.saleno ");
                //【ID1029413】【菲尔雪V3.2.0.1】DCP_DOCTOTAL的零售单数据统计不到，实际笔数808 门店：23001 日期：20221031 by jinzma 20221101
                //是否分区标记 0:未分区 1:已分区
                if (PosPub.tablePartition_dcp_sale.equals("1")) {
                    sb.append(" and a.partition_date=b.partition_date");
                }
                sb.append(" WHERE a.eid='" + eId + "' and a.SHOPID='" + shopId + "' ");

                if (PosPub.tablePartition_dcp_sale.equals("1")) {
                    sb.append(" AND a.partition_date>='" + beginDate + "' and a.partition_date<='" + sDate + "'");
                } else {
                    sb.append(" AND a.bdate>='" + beginDate + "' and a.bdate<='" + sDate + "' ");
                }
                sb.append(" GROUP BY a.EID,a.SHOPID,b.PLUNO "
                );
            }
            List<Map<String, Object>> getQdata = this.doQueryData(sb.toString(),null);
            if (getQdata != null && !getQdata.isEmpty()) {
                for (Map<String, Object> oneData :getQdata ) {
                    DataValue[]	insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("QTY").toString(), Types.VARCHAR),
                            new DataValue(sDate, Types.VARCHAR)
                    };
                    InsBean ib = new InsBean("DCP_SALE_GOODS", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }

            this.doExecuteDataToDB();


        } catch (Exception e) {
            logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"销售数量汇总（统计一个月内的）保存失败：" + e.getMessage() + "\r\n");
        }
    }

    /**
     * 销售数量汇总（统计一个月内的）
     * @param eId         企业编号
     */
    private void SumSaleQty(String eId,DCP_ShopEDateProcessReq req) {
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        try {

            //当天已经汇总过就不再汇总
            {
                String sql = " select sdate from dcp_sale_goods where eid='" + eId + "' and shopid=' ' ";
                List<Map<String, Object>> getQdata = this.doQueryData(sql, null);
                if (!CollectionUtils.isEmpty(getQdata)){
                    if (sDate.equals(getQdata.get(0).get("SDATE").toString())){
                        return;
                    }
                }
            }

            //删除DCP_SALE_GOODS
            DelBean db1 = new DelBean("DCP_SALE_GOODS");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(" ", Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            //新增DCP_SALE_GOODS(门店)
            String beginDate = PosPub.GetStringDate(sDate, -30)  ;
            String[] columns = {"EID","SHOPID","PLUNO","QTY","SDATE"};
            StringBuffer sb = new StringBuffer();
            {
                sb.append("	SELECT a.EID,b.PLUNO,"
                        + " sum((case when a.type='1' or a.type='2' or a.type='4' then -b.qty else b.qty end )) as qty"
                        + " FROM dcp_sale a "
                        + " INNER JOIN dcp_sale_detail b ON a.eid=b.eid AND a.shopid=b.shopid AND a.saleno=b.saleno ");
                //【ID1029413】【菲尔雪V3.2.0.1】DCP_DOCTOTAL的零售单数据统计不到，实际笔数808 门店：23001 日期：20221031 by jinzma 20221101
                //是否分区标记 0:未分区 1:已分区
                if (PosPub.tablePartition_dcp_sale.equals("1")) {
                    sb.append(" and a.partition_date=b.partition_date");
                }
                sb.append(" WHERE a.eid='" + eId + "' ");

                if (PosPub.tablePartition_dcp_sale.equals("1")) {
                    sb.append(" AND a.partition_date>='" + beginDate + "' and a.partition_date<='" + sDate + "'");
                } else {
                    sb.append(" AND a.bdate>='" + beginDate + "' and a.bdate<='" + sDate + "' ");
                }
                sb.append(" GROUP BY a.EID,b.PLUNO "
                );
            }
            List<Map<String, Object>> getQdata = this.doQueryData(sb.toString(),null);
            if (getQdata != null && !getQdata.isEmpty()) {
                for (Map<String, Object> oneData :getQdata ) {
                    DataValue[]	insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(" ", Types.VARCHAR),
                            new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR),
                            new DataValue(oneData.get("QTY").toString(), Types.VARCHAR),
                            new DataValue(sDate, Types.VARCHAR)
                    };
                    InsBean ib = new InsBean("DCP_SALE_GOODS", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }

            this.doExecuteDataToDB();


        } catch (Exception e) {
            logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+""+"销售数量汇总（统计一个月内的）保存失败：" + e.getMessage() + "\r\n");
        }
    }

	//【ID1038854】【海鹏3.0】进销存汇总表，查询一段时间的期末库存进货金额，超级大，一个加工门店月底存货金额达到了2000多万！ by jinzma 20240130
	private void GoodsPriceProcess(String eId, String shopId, String startEdate){
	
	    //DCP_STOCK_DAY_PRICE
	    String[] columns1 = {"EID","ORGANIZATIONNO","PLUNO","BASEUNIT","FEATURENO","PRICE","DISTRIPRICE","EDATE"};
	    try {
	
	        String sql = "select pluno from dcp_stock_day_price where eid='"+eId+"' and organizationno='"+shopId+"' and edate='"+startEdate+"' ";
	        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
	        if (!CollectionUtils.isEmpty(getQData)){
	            logger.error("商品每日零售价和供货价记录保存失败：门店："+shopId+" 资料已存在"+ "\r\n");
	            return;
	        }
	
	
	        sql = "select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' and belfirm is not null ";
	        List<Map<String, Object>> getBelfirmQData = this.doQueryData(sql,null);
	        if (CollectionUtils.isEmpty(getBelfirmQData)){
	            logger.error("商品每日零售价和供货价记录保存失败：门店："+shopId+" 所属公司未设置"+ "\r\n");
	            return;
	        }
	        String belfirm = getBelfirmQData.get(0).get("BELFIRM").toString();
	
	        sql = "select pluno,baseunit as punit,baseunit,N'1'as unitratio,price,supprice from dcp_goods where eid='"+eId+"'  ";  //and status='100'
	        List<Map<String, Object>> getGoodsList = this.doQueryData(sql,null);
	
	        MyCommon mc = new MyCommon();
	        List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eId, belfirm, shopId, getGoodsList,belfirm);
	
	        for (Map<String, Object> goods:getGoodsList) {
	
	            String pluNo = goods.get("PLUNO").toString();
	            String baseUnit = goods.get("BASEUNIT").toString();
	            //商品取价
	            Map<String, Object> condiV= new HashMap<>();
	            condiV.put("PLUNO",pluNo);
	            condiV.put("PUNIT",baseUnit);
	            List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPrice, condiV, false);
	            condiV.clear();
	
	            String price="0";
	            String distriPrice="0";
	            String defPrice="0";
	            String defDistriPrice="0";
	            //用商品基础资料来兜底
	            if (!Check.Null(goods.get("PRICE").toString())){
	                defPrice = goods.get("PRICE").toString();
	            }
	            if (!Check.Null(goods.get("SUPPRICE").toString())){
	                defDistriPrice = goods.get("SUPPRICE").toString();
	            }
	
	            if(priceList!=null && priceList.size()>0 ) {
	                price=priceList.get(0).get("PRICE").toString();
	                distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
	            }
	            if (new BigDecimal(price).compareTo(BigDecimal.ZERO)==0 && new BigDecimal(defPrice).compareTo(BigDecimal.ZERO)>0){
	                price=defPrice;
	            }
	            if (new BigDecimal(distriPrice).compareTo(BigDecimal.ZERO)==0 && new BigDecimal(defDistriPrice).compareTo(BigDecimal.ZERO)>0){
	                distriPrice=defDistriPrice;
	            }
	
	            DataValue[] insValue1 = new DataValue[]{
	                    new DataValue(eId, Types.VARCHAR),
	                    new DataValue(shopId, Types.VARCHAR),
	                    new DataValue(pluNo, Types.VARCHAR),
	                    new DataValue(baseUnit, Types.VARCHAR),
	                    new DataValue(" ", Types.VARCHAR),
	                    new DataValue(price, Types.VARCHAR),
	                    new DataValue(distriPrice, Types.VARCHAR),
	                    new DataValue(startEdate, Types.VARCHAR),
	            };
	            InsBean ib1 = new InsBean("DCP_STOCK_DAY_PRICE", columns1);
	            ib1.addValues(insValue1);
	            this.addProcessData(new DataProcessBean(ib1));
	
	        }
	
	        this.doExecuteDataToDB();
	
	    }catch (Exception e) {
	        logger.error("商品每日零售价和供货价记录保存失败：" + e.getMessage() + "\r\n");
	    }
	
	}


}
