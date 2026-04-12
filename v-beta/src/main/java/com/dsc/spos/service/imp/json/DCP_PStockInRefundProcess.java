package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PStockInRefundProcessReq;
import com.dsc.spos.json.cust.res.DCP_PStockInRefundProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

public class DCP_PStockInRefundProcess extends SPosAdvanceService<DCP_PStockInRefundProcessReq, DCP_PStockInRefundProcessRes>
{

    @Override
    protected void processDUID(DCP_PStockInRefundProcessReq req, DCP_PStockInRefundProcessRes res) throws Exception
    {
        // TODO Auto-generated method stub
        String isBatchNo=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");


        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String confirmBy = req.getOpNO();
        String accountBy = req.getOpNO();
        String submitBy = req.getOpNO();

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String confirmDate = df.format(cal.getTime());
        String accountDate = req.getRequest().getAccountDate();
        String submitDate = df.format(cal.getTime());
        if(Check.Null(accountDate)){
            accountDate = confirmDate;//PosPub.getAccountDate_SMS(dao, eId, shopId);
        }
        String lastmodifyDatetime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(cal.getTime());


        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
        String accountDateF = df1.format(df2.parse(accountDate));

        df = new SimpleDateFormat("HHmmss");
        String confirmTime = df.format(cal.getTime());
        String accountTime = df.format(cal.getTime());
        String submitTime = df.format(cal.getTime());
        String pStockInNO = req.getRequest().getpStockInNo();
        String pStockInNO_origin = req.getRequest().getpStockInNo_origin();

        String pSql="select * from dcp_pstockin a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"'";
        List<Map<String, Object>> getQData = this.doQueryData(pSql, null);

        if(getQData.size()<=0){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在！");
        }

        try
        {
            //判断是否已提交审核
            String sql = this.GetDCP_PSTOCKIN_SQL(req);
            List<Map<String, Object>> getQData1 = this.doQueryData(sql,null);
            if(getQData1 != null && getQData1.isEmpty() == false)
            {
                String docType = getQData1.get(0).get("DOC_TYPE").toString();
                String keyDate=getQData1.get(0).get("ACCOUNT_DATE").toString();
                String ofNO = getQData1.get(0).get("OFNO").toString();
                String oofNo=getQData1.get(0).get("OOFNO").toString();//任务单号
                String batchSql = "select a.oitem,sum(a.pqty) as pqty ,sum(a.baseqty) as baseqty from DCP_PSTOCKOUT_BATCH a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' group by a.oitem ";
                List<Map<String, Object>> getBData = this.doQueryData(batchSql, null);
                String batchNormalSql = "select a.* from DCP_PSTOCKOUT_BATCH a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.pstockinno='"+pStockInNO+"' order by a.item ";
                List<Map<String, Object>> getBDataN = this.doQueryData(batchNormalSql, null);

                Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                }


                UptBean ub1 = new UptBean("DCP_PSTOCKIN");
                //add Value
                ub1.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
                ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("ConfirmBy", new DataValue(confirmBy, Types.VARCHAR));
                ub1.addUpdateValue("Confirm_Date", new DataValue(confirmDate, Types.VARCHAR));
                ub1.addUpdateValue("Confirm_Time", new DataValue(confirmTime, Types.VARCHAR));
                ub1.addUpdateValue("accountBy", new DataValue(accountBy, Types.VARCHAR));

                if (keyDate.equals(accountDate)==false) //分区字段需要更新
                {
                    ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));

                    //更新单身DCP_PSTOCKIN_DETAIL.ACCOUNT_DATE
                    UptBean ub = new UptBean("DCP_PSTOCKIN_DETAIL");
                    ub.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                    // condition
                    ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                    ub.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub));

                    //更新单身DCP_PSTOCKIN_MATERIAL.ACCOUNT_DATE
                    UptBean ub2 = new UptBean("DCP_PSTOCKIN_MATERIAL");
                    ub2.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                    // condition
                    ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub2.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                    ub2.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub2));
                }

                ub1.addUpdateValue("account_Time", new DataValue(accountTime, Types.VARCHAR));
                ub1.addUpdateValue("SUBMITBY", new DataValue(submitBy, Types.VARCHAR));
                ub1.addUpdateValue("SUBMIT_DATE", new DataValue(submitDate, Types.VARCHAR));
                ub1.addUpdateValue("SUBMIT_TIME", new DataValue(submitTime, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                //condition
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));

                //红冲的单子，需要回写原单状态
                if(!Check.Null(pStockInNO_origin))
                {
                    UptBean ub3 = new UptBean("DCP_PSTOCKIN");
                    ub3.addUpdateValue("refundstatus", new DataValue("2", Types.VARCHAR));//为空 0-新建   2-已完成
                    ub3.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub3.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    //condition
                    ub3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub3.addCondition("PSTOCKINNO", new DataValue(pStockInNO_origin, Types.VARCHAR));
                    ub3.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub3));
                }

                //1.加入库存流水账信息
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("ITEM", true);
                //调用过滤函数
                List<Map<String, Object>> getQDetail=MapDistinct.getMap(getQData1, condition);

                String stockType = "";     //成品库存方向  1 OR -1
                String stockDocType = "";  //成品库存单据类型
                String matStockType = "";     //原料库存方向  1 OR -1
                String matStockDocType = "";  //原料库存单据类型

                switch (docType) {
                    case "0":  //完工入库
                        stockType="1";
                        stockDocType="08";
                        matStockType="-1";
                        matStockDocType="11";  //加工原料倒扣
                        break;
                    case "1":  //组合单
                        stockType="1";
                        stockDocType="30";
                        matStockType="-1";
                        matStockDocType="31";  //组合原料出库
                        break;
                    case "2":  //拆解单
                        stockType="-1";
                        stockDocType="32";
                        matStockType="1";
                        matStockDocType="33";  //拆解原料入库
                        break;
                    case "3":  //转换合并
                        stockType="1";
                        stockDocType="35";
                        matStockType="-1";
                        matStockDocType="36";
                        break;
                    case "4":  //转换拆解
                        stockType="-1";
                        stockDocType="37";
                        matStockType="1";
                        matStockDocType="38"; //转换拆解入库 （原料）
                        break;
                    default:
                        break;
                }

                for (Map<String, Object> oneData : getQDetail)
                {
                    //红充单提交的时候，更新原单的红充数
                    if(!Check.Null(pStockInNO_origin))
                    {
                        String oItem = oneData.get("OITEM").toString();
                        UptBean ub4 = new UptBean("DCP_pstockin_detail");
                        //add Value PSTOCKINNO_REFUND
                        ub4.addUpdateValue("PQTY_REFUND", new DataValue(oneData.get("PQTY").toString(), Types.VARCHAR));//0-新建
                        ub4.addUpdateValue("SCRAP_QTY_REFUND", new DataValue(oneData.get("SCRAP_QTY").toString(), Types.VARCHAR));//0-新建

                        //condition
                        ub4.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub4.addCondition("PSTOCKINNO", new DataValue(pStockInNO_origin, Types.VARCHAR));
                        ub4.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                        //ub2.addCondition("PLUNO", new DataValue(oneData.get("PLUNO").toString(), Types.VARCHAR));
                        ub4.addCondition("item", new DataValue(oItem, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub4));
                    }

                    //报废不写库存流水   BY JZMA 20190802  有报废时录入数一定是零，报废单独一条记录
                    BigDecimal pqty_b = new BigDecimal(oneData.get("PQTY").toString());
                    if (pqty_b.compareTo(BigDecimal.ZERO)!=0)
                    {
                        String featureNo = oneData.get("FEATURENO").toString();  ///特征码为空给空格
                        if (Check.Null(featureNo))
                            featureNo =" ";

                        String procedure="SP_DCP_StockChange";
                        Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1,eId);                                      //--企业ID
                        inputParameter.put(2,shopId);                                   //--组织
                        inputParameter.put(3,stockDocType);                             //--单据类型
                        inputParameter.put(4,pStockInNO);	                              //--单据号
                        inputParameter.put(5,oneData.get("ITEM").toString());           //--单据行号
                        inputParameter.put(6,stockType);                                //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(7,oneData.get("BDATE").toString());          //--营业日期 yyyy-MM-dd
                        inputParameter.put(8,oneData.get("PLUNO").toString());          //--品号
                        inputParameter.put(9,featureNo);                                //--特征码
                        inputParameter.put(10,oneData.get("WAREHOUSE").toString());     //--仓库
                        inputParameter.put(11,oneData.get("BATCH_NO").toString());      //--批号
                        inputParameter.put(12,oneData.get("PUNIT").toString());         //--交易单位
                        inputParameter.put(13,oneData.get("PQTY").toString());          //--交易数量
                        inputParameter.put(14,oneData.get("BASEUNIT").toString());      //--基准单位
                        inputParameter.put(15,oneData.get("BASEQTY").toString());       //--基准数量
                        inputParameter.put(16,oneData.get("UNIT_RATIO").toString());    //--换算比例
                        inputParameter.put(17,oneData.get("PRICE").toString());         //--零售价
                        inputParameter.put(18,oneData.get("AMT").toString());           //--零售金额
                        inputParameter.put(19,oneData.get("DISTRIPRICE").toString());   //--进货价
                        inputParameter.put(20,oneData.get("DISTRIAMT").toString());     //--进货金额
                        inputParameter.put(21,accountDate);                             //--入账日期 yyyy-MM-dd
                        inputParameter.put(22,oneData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(23,oneData.get("BDATE").toString());         //--单据日期
                        inputParameter.put(24,oneData.get("BSNO").toString());          //--异动原因
                        inputParameter.put(25,oneData.get("MEMO").toString());          //--异动描述
                        inputParameter.put(26,accountBy);                               //--操作员

                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));
                    }

                    // 原料明细加入库存流水账
                    for (Map<String, Object> matOneData : getQData1)
                    {
                        String matMItem = matOneData.get("MAT_MITEM").toString();
                        String isBuckle = matOneData.get("MAT_ISBUCKLE").toString();  //是否扣料件
                        BigDecimal matPqty = new BigDecimal(matOneData.get("MAT_PQTY").toString());
                        if (Check.Null(isBuckle)||!isBuckle.equals("N")) {
                            isBuckle = "Y";
                        }


                        List<Map<String, Object>> filterB = getBData.stream().filter(x -> x.get("OITEM").toString().equals(matMItem)).collect(Collectors.toList());
                        List<Map<String, Object>> filterBn = getBDataN.stream().filter(x -> x.get("OITEM").toString().equals(matMItem)).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(filterB)){
                            String batchPQty = filterB.get(0).get("PQTY").toString();
                            if(matPqty.compareTo(new BigDecimal(batchPQty))!=0){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "子件数量与批号明细数量只和不等，不可过账！");
                            }
                        }
                        if(CollUtil.isNotEmpty(filterBn)){
                            for (Map<String, Object> map : filterBn){
                                String procedure = "SP_DCP_StockChange_v3";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                            //--企业ID
                                inputParameter.put(2, shopId);                                         //--组织
                                inputParameter.put(3, matStockDocType);                                //--单据类型
                                inputParameter.put(4, pStockInNO);                                        //--单据号
                                inputParameter.put(5, String.valueOf(map.get("ITEM").toString()));
                                inputParameter.put(6, matStockType);                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(7, matOneData.get("BDATE").toString());             //--营业日期 yyyy-MM-dd
                                inputParameter.put(8, map.get("PLUNO").toString());         //--品号
                                inputParameter.put(9, map.get("FEATURENO").toString());                                   //--特征码
                                inputParameter.put(10, map.get("WAREHOUSE").toString());    //--仓库
                                inputParameter.put(11, map.get("BATCHNO").toString());     //--批号
                                inputParameter.put(12, map.get("LOCATION").toString());
                                inputParameter.put(13, map.get("PUNIT").toString());        //--交易单位
                                inputParameter.put(14, map.get("PQTY").toString());         //--交易数量
                                inputParameter.put(15, map.get("BASEUNIT").toString());     //--基准单位
                                inputParameter.put(16, map.get("BASEQTY").toString());      //--基准数量
                                inputParameter.put(17, map.get("UNIT_RATIO").toString());   //--换算比例
                                inputParameter.put(18, matOneData.get("MAT_PRICE").toString());        //--零售价
                                inputParameter.put(19, matOneData.get("MAT_AMT").toString());          //--零售金额
                                inputParameter.put(20, matOneData.get("MAT_DISTRIPRICE").toString());  //--进货价
                                inputParameter.put(21, matOneData.get("MAT_DISTRIAMT").toString());    //--进货金额
                                inputParameter.put(22, accountDate);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(23, map.get("PRODDATE").toString());    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(24, matOneData.get("BDATE").toString());            //--单据日期
                                inputParameter.put(25, "组合入库红冲原料扣库存");             //--异动原因
                                inputParameter.put(26, "");             //--异动描述
                                inputParameter.put(27, accountBy);          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }
                        }
                        else{
                            if (matMItem.equals(oneData.get("ITEM").toString())&&isBuckle.equals("Y"))  //成品项次相同且必须是扣料件
                            {
                                String matFeatureNo=matOneData.get("MAT_FEATURENO").toString();  //特征码为空给空格
                                if (Check.Null(matFeatureNo))
                                    matFeatureNo = " ";
                                String procedure="SP_DCP_StockChange";
                                Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1,eId);                                            //--企业ID
                                inputParameter.put(2,shopId);                                         //--组织
                                inputParameter.put(3,matStockDocType);                                //--单据类型
                                inputParameter.put(4,pStockInNO);	                                    //--单据号
                                inputParameter.put(5,matOneData.get("MAT_ITEM").toString());          //--单据行号
                                inputParameter.put(6,matStockType);                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(7,matOneData.get("BDATE").toString());             //--营业日期 yyyy-MM-dd
                                inputParameter.put(8,matOneData.get("MAT_PLUNO").toString());         //--品号
                                inputParameter.put(9,matFeatureNo);                                   //--特征码
                                inputParameter.put(10,matOneData.get("MAT_WAREHOUSE").toString());    //--仓库
                                inputParameter.put(11,matOneData.get("MAT_BATCH_NO").toString());     //--批号
                                inputParameter.put(12,matOneData.get("MAT_PUNIT").toString());        //--交易单位
                                inputParameter.put(13,matOneData.get("MAT_PQTY").toString());         //--交易数量
                                inputParameter.put(14,matOneData.get("MAT_BASEUNIT").toString());     //--基准单位
                                inputParameter.put(15,matOneData.get("MAT_BASEQTY").toString());      //--基准数量
                                inputParameter.put(16,matOneData.get("MAT_UNIT_RATIO").toString());   //--换算比例
                                inputParameter.put(17,matOneData.get("MAT_PRICE").toString());        //--零售价
                                inputParameter.put(18,matOneData.get("MAT_AMT").toString());          //--零售金额
                                inputParameter.put(19,matOneData.get("MAT_DISTRIPRICE").toString());  //--进货价
                                inputParameter.put(20,matOneData.get("MAT_DISTRIAMT").toString());    //--进货金额
                                inputParameter.put(21,accountDate);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(22,matOneData.get("MAT_PROD_DATE").toString());    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(23,matOneData.get("BDATE").toString());            //--单据日期
                                inputParameter.put(24,matOneData.get("BSNO").toString());             //--异动原因
                                inputParameter.put(25,matOneData.get("MEMO").toString());             //--异动描述
                                inputParameter.put(26,accountBy);          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                            }
                        }


                    }
                }


                if(Check.Null(oofNo)){

                    String detailSql="select a.*,b.oddvalue,b.REMAINTYPE,b.SEMIWOTYPE,b.MINQTY from dcp_pstockin_detail a " +
                            " left join dcp_bom b on a.eid=b.eid and a.bomno=b.bomno " +
                            " where a.eid='"+req.geteId()+"' " +
                            " and a.organizationno='"+req.getOrganizationNO()+"'" +
                            " and a.pstockinno='"+pStockInNO+"' ";
                    List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

                    //生成任务单
                    String taskNo = this.getOrderNO(req, "JGRW");

                    int taskItem=0;
                    for (Map<String, Object> detail : detailList){
                        taskItem++;
                        ColumnDataValue detailColumns=new ColumnDataValue();
                        detailColumns.add("EID", DataValues.newString(eId));
                        detailColumns.add("PROCESSTASKNO", DataValues.newString(taskNo));
                        detailColumns.add("SHOPID", DataValues.newString(shopId));
                        detailColumns.add("ITEM", DataValues.newString(taskItem));
                        detailColumns.add("PLUNO", DataValues.newString(detail.get("PLUNO").toString()));
                        detailColumns.add("PUNIT", DataValues.newString(detail.get("PUNIT").toString()));
                        detailColumns.add("PQTY", DataValues.newString(detail.get("PQTY").toString()));
                        detailColumns.add("PSTOCKIN QTY", DataValues.newString(detail.get("PQTY").toString()));
                        detailColumns.add("BASEUNIT", DataValues.newString(detail.get("BASEUNIT").toString()));
                        detailColumns.add("BASEQTY", DataValues.newString(detail.get("BASEQTY").toString()));
                        detailColumns.add("UNIT_RATIO", DataValues.newString(detail.get("UNIT_RATIO").toString()));
                        detailColumns.add("PRICE", DataValues.newString(detail.get("PRICE").toString()));
                        detailColumns.add("AMT", DataValues.newString(detail.get("AMT").toString()));
                        detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                        detailColumns.add("MUL_QTY", DataValues.newString(detail.get("MUL_QTY").toString()));
                        detailColumns.add("DISTRIPRICE", DataValues.newString(detail.get("DISTRIPRICE").toString()));
                        detailColumns.add("DISTRIAMT", DataValues.newString(detail.get("DISTRIAMT").toString()));
                        detailColumns.add("BDATE", DataValues.newString(accountDate));
                        detailColumns.add("FEATURENO", DataValues.newString(detail.get("FEATURENO").toString()));
                        detailColumns.add("MINQTY", DataValues.newString(detail.get("MINQTY").toString()));
                        detailColumns.add("DISPTYPE", DataValues.newString(detail.get("DISPTYPE").toString()));
                        detailColumns.add("SEMIWOTYPE", DataValues.newString(detail.get("SEMIWOTYPE").toString()));
                        detailColumns.add("ODDVALUE", DataValues.newString(detail.get("ODDVALUE").toString()));
                        detailColumns.add("REMAINTYPE", DataValues.newString(detail.get("REMAINTYPE").toString()));
                        detailColumns.add("BOMNO", DataValues.newString(detail.get("BOMNO").toString()));
                        detailColumns.add("VERSIONNUM", DataValues.newString(detail.get("VERSIONNUM").toString()));
                        detailColumns.add("BEGINDATE", DataValues.newString(accountDate));
                        detailColumns.add("ENDDATE", DataValues.newString(accountDate));

                        String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                        DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib=new InsBean("DCP_PROCESSTASK_DETAIL",detailColumnNames);
                        ib.addValues(detailDataValues);
                        this.addProcessData(new DataProcessBean(ib));
                    }


                    ColumnDataValue mainColumns=new ColumnDataValue();
                    mainColumns.add("EID", DataValues.newString(eId));

                    BigDecimal totPqty = new BigDecimal(getQData.get(0).get("TOT_PQTY").toString());
                    BigDecimal totAmt = new BigDecimal(getQData.get(0).get("TOT_AMT").toString());
                    BigDecimal totCqty = new BigDecimal(getQData.get(0).get("TOT_CQTY").toString());
                    String memo = getQData.get(0).get("MEMO").toString();
                    String pTemplateNo = getQData.get(0).get("PTEMPLATENO").toString();
                    String warehouse = getQData.get(0).get("WAREHOUSE").toString();
                    String employeeId = getQData.get(0).get("EMPLOYEEID").toString();
                    String departId = getQData.get(0).get("DEPARTID").toString();
                    Object prodType = getQData.get(0).get("PRODTYPE");
                    BigDecimal totDistriamt = new BigDecimal(getQData.get(0).get("TOT_DISTRIAMT").toString());
                    mainColumns.add("SHOPID", DataValues.newString(shopId));
                    mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                    mainColumns.add("BDATE", DataValues.newString(accountDate));
                    mainColumns.add("PROCESSTASKNO", DataValues.newString(taskNo));
                    mainColumns.add("CREATEBY", DataValues.newString(eId));
                    mainColumns.add("CREATE_DATE", DataValues.newString(accountDate));
                    mainColumns.add("CREATE_TIME", DataValues.newString(accountTime));
                    mainColumns.add("TOT_PQTY", DataValues.newString(totPqty));
                    mainColumns.add("TOT_AMT", DataValues.newString(totAmt));
                    mainColumns.add("TOT_CQTY", DataValues.newString(totCqty));
                    mainColumns.add("EID", DataValues.newString(eId));
                    mainColumns.add("MEMO", DataValues.newString(memo));
                    mainColumns.add("STATUS", DataValues.newString("2"));
                    mainColumns.add("PROCESS_STATUS", DataValues.newString("N"));
                    mainColumns.add("PTEMPLATENO", DataValues.newString(pTemplateNo));
                    mainColumns.add("PDATE", DataValues.newString(accountDate));
                    mainColumns.add("WAREHOUSE", DataValues.newString(warehouse));
                    mainColumns.add("MATERIALWAREHOUSE", DataValues.newString(""));
                    mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriamt));
                    mainColumns.add("CREATE_CHATUSERID", DataValues.newString(req.getChatUserId()));
                    mainColumns.add("DTNO", DataValues.newString(""));
                    mainColumns.add("EMPLOYEEID", DataValues.newString(employeeId));
                    mainColumns.add("DEPARTID", DataValues.newString(departId));
                    mainColumns.add("PRODTYPE", DataValues.newString(prodType));

                    String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                    DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib=new InsBean("DCP_PROCESSTASK",mainColumnNames);
                    ib.addValues(mainDataValues);
                    this.addProcessData(new DataProcessBean(ib));


                }
                else{
                    //有前置的任务单据 得更新pstockinqty

                    //查询出完工入库明细，去更新加工任务明细的数量
                    String sql_PSTOCKIN_DETAIL = "select * from DCP_PSTOCKIN_DETAIL a " +
                            "where a.eid='" + req.geteId() + "' " +
                            "and a.organizationno='" + req.getOrganizationNO() + "' " +
                            "and a.pstockinno='" + pStockInNO + "' ";
                    List<Map<String, Object>> getQData_PSTOCKIN_DETAIL = this.doQueryData(sql_PSTOCKIN_DETAIL, null);
                    if (getQData_PSTOCKIN_DETAIL != null && getQData_PSTOCKIN_DETAIL.size() > 0) {
                        for (Map<String, Object> map : getQData_PSTOCKIN_DETAIL) {
                            UptBean ub_DCP_PROCESSTASK_DETAIL = new UptBean("DCP_PROCESSTASK_DETAIL");
                            //add Value
                            ub_DCP_PROCESSTASK_DETAIL.addUpdateValue("PSTOCKIN_QTY", new DataValue(Convert.toDouble(map.get("PQTY"), 0d), Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                            ub_DCP_PROCESSTASK_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub_DCP_PROCESSTASK_DETAIL.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                            ub_DCP_PROCESSTASK_DETAIL.addCondition("PROCESSTASKNO", new DataValue(oofNo, Types.VARCHAR));
                            ub_DCP_PROCESSTASK_DETAIL.addCondition("ITEM", new DataValue(Convert.toInt(map.get("OOITEM"), 0), Types.VARCHAR));
                            //更新
                            this.addProcessData(new DataProcessBean(ub_DCP_PROCESSTASK_DETAIL));
                        }
                    }


                }

                //只搞完工入库单 没用了
                if (docType.equals("0") && !Check.Null(ofNO))
                {
                    //查询出完工入库明细，去更新加工任务明细的数量
                    String sql_PSTOCKIN_DETAIL="select * from DCP_PSTOCKIN_DETAIL a " +
                            "where a.eid='"+req.geteId()+"' " +
                            "and a.organizationno='"+req.getOrganizationNO()+"' " +
                            "and a.pstockinno='"+pStockInNO+"' ";
                    List<Map<String, Object>> getQData_PSTOCKIN_DETAIL = this.doQueryData(sql_PSTOCKIN_DETAIL, null);
                    if (getQData_PSTOCKIN_DETAIL != null && getQData_PSTOCKIN_DETAIL.size()>0)
                    {
                        for (Map<String, Object> map : getQData_PSTOCKIN_DETAIL)
                        {
                            UptBean ub_DCP_PROCESSTASK_DETAIL =new UptBean("DCP_PROCESSTASK_DETAIL");
                            //add Value
                            ub_DCP_PROCESSTASK_DETAIL.addUpdateValue("PSTOCKIN_QTY", new DataValue(Convert.toDouble(map.get("PQTY"), 0d), Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                            ub_DCP_PROCESSTASK_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub_DCP_PROCESSTASK_DETAIL.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                            ub_DCP_PROCESSTASK_DETAIL.addCondition("PROCESSTASKNO", new DataValue(ofNO, Types.VARCHAR));
                            ub_DCP_PROCESSTASK_DETAIL.addCondition("ITEM", new DataValue(Convert.toInt(map.get("OITEM"),0), Types.VARCHAR));
                            //更新
                            this.addProcessData(new DataProcessBean(ub_DCP_PROCESSTASK_DETAIL));
                        }
                    }
                }

                if("0".equals(docType)){
                    String batchSql1="select a.*,b.price,b.DISTRIPRICE from DCP_PSTOCKOUT_BATCH a " +
                            " inner join DCP_PSTOCKIN_MATERIAL b on a.eid=b.eid and a.organizationno=b.organizationno and a.oitem=b.item and a.pstockinno=b.pstockinno " +
                            " inner join DCP_PSTOCKIN_DETAIL c on c.eid=b.eid and c.organizationno=b.organizationno and b.pstockinno=c.pstockinno and b.MITEM=c.item " +
                            " inner join dcp_pstockin d on a.eid=d.eid and a.organizationno=d.organizationno and d.pstockinno=a.pstockinno " +
                            " where d.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' and a.pstockinno='"+pStockInNO+"' ";
                    List<Map<String, Object>> batchData = this.doQueryData(batchSql1, null);
                    String mStockoutNo = this.getOrderNO(req, "SCKL");
                    int mItem=0;
                    for (Map<String, Object> singleBatch:batchData){
                        mItem++;

                        BigDecimal price = new BigDecimal(singleBatch.get("PRICE").toString());
                        BigDecimal distriPrice = new BigDecimal(singleBatch.get("DISTRIPRICE").toString());
                        BigDecimal pqty = new BigDecimal(singleBatch.get("PQTY").toString());
                        BigDecimal amt = price.multiply(pqty);
                        BigDecimal distriAmt = distriPrice.multiply(pqty);

                        ColumnDataValue detailColumns=new ColumnDataValue();
                        detailColumns.add("EID", DataValues.newString(eId));
                        detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                        detailColumns.add("MSTOCKOUTNO", DataValues.newString(mStockoutNo));
                        detailColumns.add("SHOPID", DataValues.newString(organizationNO));
                        detailColumns.add("WAREHOUSE", DataValues.newString(singleBatch.get("WAREHOUSE").toString()));
                        detailColumns.add("ITEM", DataValues.newString(mItem));
                        detailColumns.add("PLUNO", DataValues.newString(singleBatch.get("PLUNO").toString()));
                        detailColumns.add("PUNIT", DataValues.newString(singleBatch.get("PUNIT").toString()));
                        detailColumns.add("PQTY", DataValues.newString(singleBatch.get("PQTY").toString()));
                        detailColumns.add("BASEUNIT", DataValues.newString(singleBatch.get("BASEUNIT").toString()));
                        detailColumns.add("BASEQTY", DataValues.newString(singleBatch.get("BASEQTY").toString()));
                        detailColumns.add("UNIT_RATIO", DataValues.newString(singleBatch.get("UNIT_RATIO").toString()));
                        detailColumns.add("PRICE", DataValues.newString(singleBatch.get("PRICE").toString()));
                        detailColumns.add("AMT", DataValues.newString(amt));
                        detailColumns.add("DISTRIPRICE", DataValues.newString(singleBatch.get("DISTRIPRICE").toString()));
                        detailColumns.add("DISTRIAMT", DataValues.newString(distriAmt));
                        detailColumns.add("BATCHNO", DataValues.newString(singleBatch.get("BATCHNO").toString()));
                        detailColumns.add("PRODDATE", DataValues.newString(singleBatch.get("PRODDATE").toString()));
                        detailColumns.add("EXPDATE", DataValues.newString(singleBatch.get("EXPDATE").toString()));
                        detailColumns.add("ISBUCKLE", DataValues.newString("Y"));
                        detailColumns.add("FEATURENO", DataValues.newString(singleBatch.get("FEATURENO").toString()));
                        detailColumns.add("LOCATION", DataValues.newString(singleBatch.get("LOCATION").toString()));
                        detailColumns.add("OTYPE", DataValues.newString("0"));
                        detailColumns.add("OFNO", DataValues.newString(pStockInNO));
                        detailColumns.add("OITEM", DataValues.newString(singleBatch.get("ITEM").toString()));
                        detailColumns.add("OOTYPE", DataValues.newString(""));
                        detailColumns.add("OOFNO", DataValues.newString(""));
                        detailColumns.add("OOITEM", DataValues.newString(""));
                        detailColumns.add("LOAD_DOCITEM", DataValues.newString(""));
                        detailColumns.add("PITEM", DataValues.newString(""));
                        detailColumns.add("PROCESSNO", DataValues.newString(""));
                        detailColumns.add("SITEM", DataValues.newString(""));
                        detailColumns.add("ZITEM", DataValues.newString(singleBatch.get("OITEM").toString()));//原料项次

                        String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                        DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib=new InsBean("DCP_MSTOCKOUT_DETAIL",detailColumnNames);
                        ib.addValues(detailDataValues);
                        this.addProcessData(new DataProcessBean(ib));

                        //库存异动
                        String procedure = "SP_DCP_StockChange_v3";
                        Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1, eId);                                            //--企业ID
                        inputParameter.put(2, shopId);                                         //--组织
                        inputParameter.put(3, "31");                                //--单据类型
                        inputParameter.put(4, mStockoutNo);                                        //--单据号
                        inputParameter.put(5, String.valueOf(mItem));
                        inputParameter.put(6, "-1");                                   //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(7, accountDateF);             //--营业日期 yyyy-MM-dd
                        inputParameter.put(8, singleBatch.get("PLUNO").toString());         //--品号
                        inputParameter.put(9, singleBatch.get("FEATURENO").toString());                                   //--特征码
                        inputParameter.put(10, singleBatch.get("WAREHOUSE").toString());    //--仓库
                        inputParameter.put(11, singleBatch.get("BATCHNO").toString());  //--批号
                        inputParameter.put(12, singleBatch.get("LOCATION").toString());
                        inputParameter.put(13, singleBatch.get("PUNIT").toString());        //--交易单位
                        inputParameter.put(14, singleBatch.get("PQTY").toString());         //--交易数量
                        inputParameter.put(15, singleBatch.get("BASEUNIT").toString());     //--基准单位
                        inputParameter.put(16, singleBatch.get("BASEQTY").toString());      //--基准数量
                        inputParameter.put(17, singleBatch.get("UNIT_RATIO").toString());   //--换算比例
                        inputParameter.put(18, price);        //--零售价
                        inputParameter.put(19, amt.toString());          //--零售金额
                        inputParameter.put(20, distriPrice);  //--进货价
                        inputParameter.put(21, distriAmt.toString());    //--进货金额
                        inputParameter.put(22, accountDateF);                                   //--入账日期 yyyy-MM-dd
                        inputParameter.put(23, singleBatch.get("PRODDATE").toString());    //--批号的生产日期 yyyy-MM-dd
                        inputParameter.put(24, accountDate);            //--单据日期
                        inputParameter.put(25, "完工入库扣料扣库存");             //--异动原因
                        inputParameter.put(26, "完工入库扣料扣库存");             //--异动描述
                        inputParameter.put(27, accountBy);          //--操作员

                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));


                    }

                    BigDecimal totPqty = new BigDecimal(getQData.get(0).get("TOT_PQTY").toString());
                    BigDecimal totAmt = new BigDecimal(getQData.get(0).get("TOT_AMT").toString());
                    BigDecimal totCqty = new BigDecimal(getQData.get(0).get("TOT_CQTY").toString());
                    String memo = getQData.get(0).get("MEMO").toString();
                    String warehouse = getQData.get(0).get("WAREHOUSE").toString();
                    String employeeId = getQData.get(0).get("EMPLOYEEID").toString();
                    String departId = getQData.get(0).get("DEPARTID").toString();
                    BigDecimal totDistriamt = new BigDecimal(getQData.get(0).get("TOT_DISTRIAMT").toString());

                    ColumnDataValue mainColumns=new ColumnDataValue();
                    mainColumns.add("EID", DataValues.newString(eId));
                    mainColumns.add("MSTOCKOUTNO", DataValues.newString(mStockoutNo));
                    mainColumns.add("SHOPID", DataValues.newString(organizationNO));
                    mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                    mainColumns.add("WAREHOUSE", DataValues.newString(warehouse));
                    mainColumns.add("DOC_TYPE", DataValues.newString("0"));
                    mainColumns.add("BDATE", DataValues.newString(accountDate));
                    mainColumns.add("SDATE", DataValues.newString(confirmDate));
                    mainColumns.add("ACCOUNT_DATE", DataValues.newString(accountDate));
                    mainColumns.add("OTYPE", DataValues.newString("0"));
                    mainColumns.add("OFNO", DataValues.newString(pStockInNO));
                    mainColumns.add("OOTYPE", DataValues.newString("0"));
                    mainColumns.add("OOFNO", DataValues.newString(oofNo));
                    mainColumns.add("MEMO", DataValues.newString(memo));
                    mainColumns.add("STATUS", DataValues.newString("2"));
                    mainColumns.add("ADJUSTSTATUS", DataValues.newString("1"));
                    mainColumns.add("OMSTOCKOUTNO", DataValues.newString(""));
                    mainColumns.add("TOT_CQTY", DataValues.newString(totCqty));
                    mainColumns.add("TOT_PQTY", DataValues.newString(totPqty));
                    mainColumns.add("TOT_AMT", DataValues.newString(totAmt));
                    mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriamt));
                    mainColumns.add("LOAD_DOCTYPE", DataValues.newString(""));
                    mainColumns.add("LOAD_DOCNO", DataValues.newString(""));
                    mainColumns.add("AUTOPROCESS", DataValues.newString(0));
                    mainColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                    mainColumns.add("CREATETIME", DataValues.newDate(lastmodifyDatetime));
                    mainColumns.add("CONFIRMOPID", DataValues.newString(req.getOpNO()));
                    mainColumns.add("CONFIRMTIME", DataValues.newDate(lastmodifyDatetime));
                    mainColumns.add("ACCOUNTOPID", DataValues.newString(req.getOpNO()));
                    mainColumns.add("ACCOUNTTIME", DataValues.newDate(lastmodifyDatetime));
                    mainColumns.add("EMPLOYEEID", DataValues.newString(employeeId));
                    mainColumns.add("DEPARTID", DataValues.newString(departId));


                    String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                    DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                    InsBean ib=new InsBean("DCP_MSTOCKOUT",mainColumnNames);
                    ib.addValues(mainDataValues);
                    this.addProcessData(new DataProcessBean(ib));

                }


                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                //***********调用库存同步给三方，这是个异步，不会影响效能*****************
                try
                {
                    //只搞完工入库单
                    if (docType.equals("0") && !Check.Null(ofNO))
                    {
                        //如果全部完工才更新
                        String sql_DCP_PROCESSTASK_DETAIL="select * from DCP_PROCESSTASK_DETAIL a " +
                                "where a.eid='"+eId+"' " +
                                "and a.organizationno='"+organizationNO+"' " +
                                "and a.processtaskno='"+ofNO+"' " +
                                "and nvl(a.pstockin_qty,0)<nvl(a.pqty,0) ";
                        List<Map<String, Object>> getQData_PROCESSTASK_DETAIL = this.doQueryData(sql_DCP_PROCESSTASK_DETAIL, null);
                        if (getQData_PROCESSTASK_DETAIL != null && getQData_PROCESSTASK_DETAIL.size()==0)
                        {
                            UptBean ub2 = null;
                            ub2 = new UptBean("DCP_PROCESSTASK");
                            //add Value
                            ub2.addUpdateValue("status", new DataValue("6", Types.VARCHAR));
                            ub2.addUpdateValue("Complete_Date", new DataValue(confirmDate, Types.VARCHAR));
                            ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            //condition
                            ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub2.addCondition("PROCESSTASKNO", new DataValue(ofNO, Types.VARCHAR));
                            ub2.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub2));
                        }
                    }

                    WebHookService.stockSync(eId,shopId,pStockInNO);
                }
                catch (Exception e)
                {

                }
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }
        }
        catch (Exception e)
        {
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
            //【ID1032555】【乐沙儿3.3.0.3】在门店管理出库单据点确定时，存在负库存时的提示返回error executing work，
            // 需要能够返回SP_DCP_StockChange返回的报错，提示门店  by jinzma 20230526
            String description=e.getMessage();
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                description = errors.toString();

                if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
                    description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
                }

            } catch (Exception ignored) {

            }

            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);

        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PStockInRefundProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PStockInRefundProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PStockInRefundProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PStockInRefundProcessReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String pStockInNO = req.getRequest().getpStockInNo();
        if(Check.Null(pStockInNO)){
            errMsg.append("完工入库单单号不可为空值, ");
            isFail = true;
        }
        String pStockInNO_origin = req.getRequest().getpStockInNo_origin();
        if(Check.Null(pStockInNO_origin)){
            errMsg.append("原完工入库单号（pStockInNO_origin）不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PStockInRefundProcessReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_PStockInRefundProcessReq>(){};
    }

    @Override
    protected DCP_PStockInRefundProcessRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_PStockInRefundProcessRes();
    }

    protected String GetDCP_PSTOCKIN_SQL(DCP_PStockInRefundProcessReq req) throws Exception {
        String sql=" "
                + " select a.pstockinno,a.bdate,a.otype,a.ofno,a.memo,a.load_doctype,a.load_docno,a.doc_type,a.account_date,"
                + " b.warehouse,b.item,b.oitem,b.pluno,b.punit,b.pqty,b.baseunit,b.unit_ratio,b.baseqty,b.batch_no,"
                + " b.prod_date,b.price,b.amt,b.scrap_qty,b.bsno,b.distriprice,b.distriamt,b.featureno,"
                + " c.warehouse as mat_warehouse,c.item as mat_item,c.mpluno as mat_mpluno,c.mitem as mat_mitem,"
                + " c.pluno as mat_pluno,c.punit mat_punit,c.pqty as mat_pqty,c.baseunit as mat_baseunit,"
                + " c.baseqty as mat_baseqty,c.unit_ratio as mat_unit_ratio,c.finalprodbaseqty as mat_finalprodbaseqty,"
                + " c.rawmaterialbaseqty as mat_rawmaterialbaseqty,c.price as mat_price,c.amt as mat_amt,"
                + " c.batch_no as mat_batch_no,c.prod_date as mat_prod_date,c.distriprice as mat_distriprice,"
                + " c.distriamt as mat_distriamt,c.isbuckle as mat_isbuckle, c.featureno as mat_featureno"
                + " from dcp_pstockin a"
                + " inner join dcp_pstockin_detail b on a.eid=b.eid and a.organizationno=b.organizationno "
                + "  and a.pstockinno=b.pstockinno and a.account_date=b.account_date "
                + " inner join dcp_pstockin_material c on a.eid=c.eid and a.organizationno=c.organizationno "
                + "  and a.pstockinno=c.pstockinno and a.account_date=c.account_date and b.item=c.mitem "
                + " where a.pstockinno='"+req.getRequest().getpStockInNo()+"' "
                + " and a.eid='"+req.geteId()+"' and a.shopid='"+req.getShopId()+"' and status='0' ";
        return sql;
    }


}
