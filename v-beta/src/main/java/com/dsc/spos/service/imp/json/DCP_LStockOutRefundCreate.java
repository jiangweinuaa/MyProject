package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_LStockOutRefundCreateReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutRefundCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 报损出库红冲创建
 */
public class DCP_LStockOutRefundCreate extends SPosAdvanceService<DCP_LStockOutRefundCreateReq, DCP_LStockOutRefundCreateRes>
{

    @Override
    protected void processDUID(DCP_LStockOutRefundCreateReq req, DCP_LStockOutRefundCreateRes res) throws Exception
    {
        String lStockOutNO = "";
        String shopId = req.getShopId();
        String eId = req.geteId();
        String bDate = req.getRequest().getbDate();
        String memo = req.getRequest().getMemo();
        String createBy = req.getOpNO();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String createDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String createTime = df.format(cal.getTime());
        String warehouse = req.getRequest().getWarehouse();
        String pTemplateNO = req.getRequest().getpTemplateNo();
        String lStockOutID = req.getRequest().getlStockOutID();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totDistriAmt = req.getRequest().getTotDistriAmt();
        String totCqty = req.getRequest().getTotCqty();
        String kdsLStockOut = req.getRequest().getKdsLStockOut();
        //kds需要获取报损单号，用于报损单提交确认  by jinzma 20221122
        DCP_LStockOutRefundCreateRes.level1Elm lv1 = res.new level1Elm();
        if(Check.Null(req.getRequest().getFeeObjectId())||Check.Null(req.getRequest().getFeeObjectType())){
            req.getRequest().setFeeObjectType("1");
            req.getRequest().setFeeObjectId(req.getDepartmentNo());
        }

        //try {

            //KDS会把shopid放入request，此处特殊处理  by jinzma 20221111
            if ("Y".equals(kdsLStockOut)){
                req.setShopId(req.getRequest().getShopId());
                shopId = req.getRequest().getShopId();
                //【ID1031320】【霸王3.0】报损出库单没有存用户信息 by jinzma 20230224
                createBy = "admin";
            }

            if (!checkGuid(req)) {
                lStockOutNO =this.getOrderNO(req,"BSHC");// getLStockOutNO(req);
                lv1.setlStockOutNo(lStockOutNO);
                String[] columns1 = {
                        "SHOPID", "ORGANIZATIONNO", "BDATE", "LSTOCKOUT_ID", "CREATEBY", "CREATE_DATE", "CREATE_TIME", "TOT_PQTY",
                        "TOT_AMT", "TOT_CQTY", "EID", "PROCESS_STATUS", "LSTOCKOUTNO", "MEMO", "STATUS", "WAREHOUSE",
                        "PTEMPLATENO", "TOT_DISTRIAMT", "CREATE_CHATUSERID","LSTOCKOUTNO_ORIGIN","DOC_TYPE","FEEOBJECTTYPE","FEEOBJECTID",
                        "FEE","BFEENO","EMPLOYEEID","DEPARTID"
                };

                //新增单身（多笔）
                List<DCP_LStockOutRefundCreateReq.level1Elm> datas = req.getRequest().getDatas();
                for (DCP_LStockOutRefundCreateReq.level1Elm par : datas) {
                    int insColCt = 0;
                    String[] columnsName = {
                            "LSTOCKOUTNO", "SHOPID", "item", "pluNO",
                            "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "BSNO", "WAREHOUSE",
                            "BATCH_NO", "PROD_DATE", "DISTRIPRICE", "DISTRIAMT", "BDATE", "FEATURENO",
                            "ITEM_ORIGIN","PQTY_ORIGIN","LOCATION","EXPDATE"
                    };

                    DataValue[] columnsVal = new DataValue[columnsName.length];
                    for (int i = 0; i < columnsVal.length; i++) {
                        String keyVal = null;
                        switch (i) {
                            case 0:
                                keyVal = lStockOutNO;
                                break;
                            case 1:
                                keyVal = shopId;
                                break;
                            case 2:
                                keyVal = par.getItem(); //item
                                break;
                            case 3:
                                keyVal = par.getPluNo(); //pluNO
                                break;
                            case 4:
                                keyVal = par.getPunit(); //punit
                                break;
                            case 5:
                                keyVal = par.getPqty(); //pqty
                                break;
                            case 6:
                                keyVal = par.getBaseUnit();     //wunit
                                break;
                            case 7:
                                keyVal = par.getBaseQty();   //wqty
                                break;
                            case 8:
                                keyVal = par.getUnitRatio();     //unitRatio
                                break;
                            case 9:
                                keyVal = par.getPrice();   //price
                                break;
                            case 10:
                                keyVal = par.getAmt();    //amt
                                break;
                            case 11:
                                keyVal = eId;
                                break;
                            case 12:
                                keyVal = shopId;
                                break;
                            case 13:
                                keyVal = par.getBsNo();//bsNO
                                break;
                            case 14:
                                keyVal = par.getWarehouse();
                                break;
                            case 15:
                                keyVal = par.getBatchNo();
                                break;
                            case 16:
                                keyVal = par.getProdDate();
                                break;
                            case 17:
                                keyVal = par.getDistriPrice();
                                if (Check.Null(keyVal))
                                    keyVal = "0";
                                break;
                            case 18:
                                keyVal = par.getDistriAmt();
                                if (Check.Null(keyVal))
                                    keyVal = "0";
                                break;
                            case 19:
                                keyVal = bDate;
                                break;
                            case 20:
                                keyVal = par.getFeatureNo();
                                if (Check.Null(keyVal))
                                    keyVal = " ";
                                break;
                            case 21:
                                keyVal = par.getItem_origin();
                                break;
                            case 22:
                                keyVal = par.getPqty_origin();
                                break;

                            case 23:
                                keyVal = par.getLocation();
                                if(Check.Null(keyVal)){
                                    keyVal=" ";
                                }
                                break;

                            case 24:
                                keyVal = par.getExpDate();
                                break;
                            default:
                                break;
                        }

                        if (keyVal != null) {
                            insColCt++;
                            if (i == 2 || i == 21) {
                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                            } else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 11 || i == 18 || i == 22) {
                                columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                            } else {
                                columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                            }
                        } else {
                            columnsVal[i] = null;
                        }
                    }

                    String[] columns2 = new String[insColCt];
                    DataValue[] insValue2 = new DataValue[insColCt];
                    // 依照傳入參數組譯要insert的欄位與數值；
                    insColCt = 0;

                    for (int i = 0; i < columnsVal.length; i++) {
                        if (columnsVal[i] != null) {
                            columns2[insColCt] = columnsName[i];
                            insValue2[insColCt] = columnsVal[i];
                            insColCt++;
                            if (insColCt >= insValue2.length)
                                break;
                        }
                    }
                    InsBean ib2 = new InsBean("DCP_LSTOCKOUT_DETAIL", columns2);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));


                    //更新原单的明细已红冲数量
                    UptBean ub_DCP_LSTOCKOUT = new UptBean("DCP_LSTOCKOUT_DETAIL");
                    ub_DCP_LSTOCKOUT.addUpdateValue("PQTY_REFUND", new DataValue(par.getPqty(), Types.VARCHAR));
                    // condition
                    ub_DCP_LSTOCKOUT.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_LSTOCKOUT.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub_DCP_LSTOCKOUT.addCondition("LSTOCKOUTNO", new DataValue(req.getRequest().getlStockoutNo_origin(), Types.VARCHAR));
                    ub_DCP_LSTOCKOUT.addCondition("ITEM", new DataValue(par.getItem_origin(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub_DCP_LSTOCKOUT));

                }

                //新增报损图片
                List<DCP_LStockOutRefundCreateReq.level1ElmFileList> fileList = req.getRequest().getFileList();
                int item = 1;
                if (fileList != null && !fileList.isEmpty()) {
                    for (DCP_LStockOutRefundCreateReq.level1ElmFileList par : fileList) {
                        String[] columns = {
                                "EID", "ORGANIZATIONNO", "SHOPID", "LSTOCKOUTNO", "ITEM", "FILENAME",
                        };
                        DataValue[] insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(lStockOutNO, Types.VARCHAR),
                                new DataValue(item, Types.INTEGER),
                                new DataValue(par.getFileName(), Types.VARCHAR),
                        };
                        InsBean ib = new InsBean("DCP_LSTOCKOUT_IMAGE", columns);
                        ib.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib));
                        item++;
                    }
                }

                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(bDate, Types.VARCHAR),
                        new DataValue(lStockOutID, Types.VARCHAR),
                        new DataValue(createBy, Types.VARCHAR),
                        new DataValue(createDate, Types.VARCHAR),
                        new DataValue(createTime, Types.VARCHAR),
                        new DataValue(totPqty, Types.VARCHAR),
                        new DataValue(totAmt, Types.VARCHAR),
                        new DataValue(totCqty, Types.VARCHAR),
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue("N", Types.VARCHAR),
                        new DataValue(lStockOutNO, Types.VARCHAR),
                        new DataValue(memo, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),   ///status
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(pTemplateNO, Types.VARCHAR),
                        new DataValue(totDistriAmt, Types.VARCHAR),
                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getlStockoutNo_origin(), Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue(req.getRequest().getFeeObjectType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getFeeObjectId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getFee(), Types.VARCHAR),
                        new DataValue(req.getRequest().getFeeBillNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getDepartId(), Types.VARCHAR),

                };

                InsBean ib1 = new InsBean("DCP_LSTOCKOUT", columns1);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


                //更新原单的退单号
                UptBean ub_DCP_LSTOCKOUT = new UptBean("DCP_LSTOCKOUT");
                ub_DCP_LSTOCKOUT.addUpdateValue("LSTOCKOUTNO_REFUND", new DataValue(lStockOutNO, Types.VARCHAR));
                // condition
                ub_DCP_LSTOCKOUT.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub_DCP_LSTOCKOUT.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub_DCP_LSTOCKOUT.addCondition("LSTOCKOUTNO", new DataValue(req.getRequest().getlStockoutNo_origin(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub_DCP_LSTOCKOUT));


                // ************************* KDS后厨报损  Begin ***************************
                // 如开启参数 则视为KDS端
                if ("Y".equals(kdsLStockOut)) {
                    // 若kdsLStockOut=Y时，报损单创建成功后同步处理加工任务明细清零操作；

                    for (DCP_LStockOutRefundCreateReq.level1Elm data : datas) {
                        //【ID1032606】【开发环境3.0】KDS报损，数量为小数，报损单创建成功后，可用数量不对  by jinzma 20230418
                        //获取 KDS报错商品 加工任务档的单号
                        StringBuffer sqlbuf = new StringBuffer();
                        sqlbuf.append(" select processtaskno,item,availqty from dcp_processtask_detail"
                                              + " where eid='"+eId+"' and shopid='"+shopId+"' and pluno='"+data.getPluNo()+"' and punit='"+data.getPunit()+"' "
                                              + " and isrefundorder is null and goodsstatus='2' and availqty>0 and bdate='"+createDate+"'");
                        if (!Check.Null(data.getPluBarCode())){
                            sqlbuf.append(" and plubarcode='"+data.getPluBarCode()+"'");
                        }
                        List<Map<String, Object>> getPlunoDetails = this.doQueryData(sqlbuf.toString(), null);

                        BigDecimal pqty_b = new BigDecimal(data.getPqty()); //实际报损数量
                        for (Map<String, Object> plunoDetail : getPlunoDetails) {
                            if (pqty_b.compareTo(BigDecimal.ZERO) <= 0){
                                break;
                            }

                            String availQty = plunoDetail.get("AVAILQTY").toString();
                            if (PosPub.isNumericType(availQty)) {

                                BigDecimal availQty_b = new BigDecimal(availQty);      //可用数量
                                //pqty_b =1.7   availQty_b =1
                                if (availQty_b.compareTo(pqty_b)>=0){
                                    availQty_b = availQty_b.subtract(pqty_b);
                                    pqty_b = new BigDecimal("0");
                                }else{
                                    pqty_b = pqty_b.subtract(availQty_b);
                                    availQty_b = new BigDecimal("0");
                                }


                                UptBean ub1 = new UptBean("DCP_PROCESSTASK_DETAIL");
                                ub1.addUpdateValue("AVAILQTY", new DataValue(availQty_b.toPlainString(), Types.VARCHAR));
                                // condition
                                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                ub1.addCondition("ITEM", new DataValue(plunoDetail.get("ITEM").toString(), Types.VARCHAR));
                                ub1.addCondition("PROCESSTASKNO", new DataValue(plunoDetail.get("PROCESSTASKNO").toString(), Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub1));
                            }
                        }
                    }
                }
                // ************************* KDS后厨报损  End ***************************

                this.doExecuteDataToDB();

                res.setDatas(lv1);
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
            }
        //} catch (Exception e) {
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_LStockOutRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_LStockOutRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_LStockOutRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_LStockOutRefundCreateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        //必传值不为空
        String bDate = req.getRequest().getbDate();
        String lStockOutID = req.getRequest().getlStockOutID();
        String warehouse = req.getRequest().getWarehouse();
        List<DCP_LStockOutRefundCreateReq.level1Elm> datas = req.getRequest().getDatas();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totDistriAmt = req.getRequest().getTotDistriAmt();
        String totCqty = req.getRequest().getTotCqty();
        String lStockoutNo_origin = req.getRequest().getlStockoutNo_origin();

        //String feeObjectType = req.getRequest().getFeeObjectType();
        //String feeObjectId = req.getRequest().getFeeObjectId();
        String employeeId = req.getRequest().getEmployeeId();
        String departId = req.getRequest().getDepartId();

        if (Check.Null(lStockoutNo_origin)) {
            errMsg.append("原单号不可为空值, ");
            isFail = true;
        }
        
        if (Check.Null(bDate)) {
            errMsg.append("单据日期不可为空值, ");
            isFail = true;
        }

        if (Check.Null(lStockOutID)) {
            errMsg.append("报损单红冲guid不可为空值, ");
            isFail = true;
        }else{
            //【ID1030251】【3.0】KDS项目--报损报错 by jinzma 20221208
            if (lStockOutID.length()>32){
                errMsg.append("报损单红冲guid长度不可超过32, ");
                isFail = true;
            }
        }
        if (Check.Null(warehouse)) {
            errMsg.append("仓库不可为空值, ");
            isFail = true;
        }

        if (Check.Null(totPqty)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totAmt)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totDistriAmt)) {
            errMsg.append("合计进货金额可为空值, ");
            isFail = true;
        }
        if (Check.Null(totCqty)) {
            errMsg.append("合计品种数量不可为空值, ");
            isFail = true;
        }

        //if(Check.Null(feeObjectType)){
        //    errMsg.append("费用对象类型不可为空值, ");
        //    isFail = true;
        //}
        //if(Check.Null(feeObjectId)){
        //    errMsg.append("费用对象不可为空值, ");
        //    isFail = true;
        //}
        if(Check.Null(employeeId)){
            errMsg.append("员工不可为空值, ");
            isFail = true;
        }
        if(Check.Null(departId)){
            errMsg.append("部门不可为空值, ");
            isFail = true;
        }

        for (DCP_LStockOutRefundCreateReq.level1Elm par : datas) {
            String baseUnit = par.getBaseUnit();
            String baseQty = par.getBaseQty();
            String unitRatio = par.getUnitRatio();
            String pluNo = par.getPluNo();
            String item_origin = par.getItem_origin();
            String pqty_origin = par.getPqty_origin();

            if (Check.Null(par.getItem())) {
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }

            if (Check.Null(par.getPluNo())) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }

            if (Check.Null(par.getPunit())) {
                errMsg.append("报损单红冲位不可为空值, ");
                isFail = true;
            }

            if(Check.Null(par.getLocation())){
                //errMsg.append("库位编号不可为空值, ");
                //isFail = true;
            }
            //if(Check.Null(par.getExpDate())){
               // errMsg.append( "有效日期不可为空值, ");
               // isFail = true;
            //}

            if (Check.Null(par.getPqty())) {
                errMsg.append("报损数量不可为空值, ");
                isFail = true;
            } else {
                if (!PosPub.isNumericTypeMinus(par.getPqty())) {
                    errMsg.append("报损数量必须为数值, ");
                    isFail = true;
                } else {
                    BigDecimal qty = new BigDecimal(par.getPqty());
                    if (qty.compareTo(BigDecimal.ZERO) == 0) {
                        errMsg.append("报损数量不能为零, ");
                        isFail = true;
                    }
                }
            }

            if (Check.Null(par.getWarehouse())) {
                errMsg.append("仓库不可为空值, ");
                isFail = true;
            }

            if (baseUnit == null) {
                errMsg.append("商品" + pluNo + "基本单位不可为空值, ");
                isFail = true;
            }
            if (baseQty == null) {
                errMsg.append("商品" + pluNo + "基本数量不可为空值, ");
                isFail = true;
            }
            if (unitRatio == null) {
                errMsg.append("商品" + pluNo + "单位转换率不可为空值, ");
                isFail = true;
            }

            if (Check.Null(item_origin)) {
                errMsg.append("来源项次不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pqty_origin)) {
                errMsg.append("来源数量不可为空值, ");
                isFail = true;
            }
            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_LStockOutRefundCreateReq> getRequestType()
    {
        return new TypeToken<DCP_LStockOutRefundCreateReq>(){};
    }

    @Override
    protected DCP_LStockOutRefundCreateRes getResponseType()
    {
        return new DCP_LStockOutRefundCreateRes();
    }

    private String getLStockOutNO(DCP_LStockOutRefundCreateReq req) throws Exception {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String lStockOutNO = null;
        String shopId = req.getShopId();
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String[] conditionValues = {eId, shopId}; // 查询要货单号
        lStockOutNO = "BSCK" + bDate;
        sqlbuf.append("" + "select lStockOutNO  from ( " + "select max(lStockOutNO) as lStockOutNO "
                              + "  from DCP_LSTOCKOUT " + " where EID = ? " + " and SHOPID = ? "
                              + " and lStockOutNO like '%%" + lStockOutNO + "%%' "); // 假資料
        sqlbuf.append(" ) TBL ");
        sql = sqlbuf.toString();

        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

        if (getQData != null && getQData.isEmpty() == false) {
            lStockOutNO = (String) getQData.get(0).get("LSTOCKOUTNO");
            if (lStockOutNO != null && lStockOutNO.length() > 0) {
                long i;
                lStockOutNO = lStockOutNO.substring(4);
                i = Long.parseLong(lStockOutNO) + 1;
                lStockOutNO = i + "";
                lStockOutNO = "BSCK" + lStockOutNO;
            } else {
                lStockOutNO = "BSCK" + bDate + "00001";
            }
        } else {
            lStockOutNO = "BSCK" + bDate + "00001";
        }

        return lStockOutNO;
    }

    private boolean checkGuid(DCP_LStockOutRefundCreateReq req) throws Exception {
        boolean existGuid;
        String guid = req.getRequest().getlStockOutID();
        String sql = "select LSTOCKOUT_ID from DCP_LSTOCKOUT where LSTOCKOUT_ID = '" + guid + "' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {
            existGuid = true;
        } else {
            existGuid = false;
        }
        return existGuid;
    }


}
