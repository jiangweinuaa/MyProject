package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_LStockOutRefundUpdateReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutRefundUpdateRes;
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

public class DCP_LStockOutRefundUpdate extends SPosAdvanceService<DCP_LStockOutRefundUpdateReq, DCP_LStockOutRefundUpdateRes>
{
    
    @Override
    protected void processDUID(DCP_LStockOutRefundUpdateReq req, DCP_LStockOutRefundUpdateRes res) throws Exception
    {
        String shopId = req.getShopId();
        String eId = req.geteId();
        String bDate = req.getRequest().getbDate();
        String modifyBy = req.getOpNO();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String modifyDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String modifyTime = df.format(cal.getTime());
        String lStockOutNO = req.getRequest().getlStockOutNo();
        String memo = req.getRequest().getMemo();
        String warehouse = req.getRequest().getWarehouse();
        String pTemplateNO = req.getRequest().getpTemplateNo();
        String totPqty =req.getRequest().getTotPqty();
        String totAmt=req.getRequest().getTotAmt();
        String totDistriAmt=req.getRequest().getTotDistriAmt();
        String totCqty=req.getRequest().getTotCqty();
        if(Check.Null(req.getRequest().getFeeObjectId())||Check.Null(req.getRequest().getFeeObjectType())){
            req.getRequest().setFeeObjectType("1");
            req.getRequest().setFeeObjectId(req.getDepartmentNo());
        }
        //try {
            String sqlKey=" select BDATE from DCP_LSTOCKOUT "
                    + " where EID='"+eId+"' and organizationNO='"+shopId+"' and lStockOutNO='"+lStockOutNO+"' and status='0' ";
            List<Map<String, Object>> getQDataKey = this.doQueryData(sqlKey, null);
            if (getQDataKey!=null && getQDataKey.isEmpty()==false) {
                //删除原有单身
                DelBean db1 = new DelBean("DCP_LSTOCKOUT_DETAIL");
                db1.addCondition("lStockOutNO", new DataValue(lStockOutNO, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("organizationNO", new DataValue(shopId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //删除报损图片
                DelBean db2 = new DelBean("DCP_LSTOCKOUT_IMAGE");
                db2.addCondition("LSTOCKOUTNO", new DataValue(lStockOutNO, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                List<DCP_LStockOutRefundUpdateReq.level1Elm> datas = req.getRequest().getDatas();
                for (DCP_LStockOutRefundUpdateReq.level1Elm par : datas) {
                    int insColCt = 0;
                    String[] columnsName = {
                            "LSTOCKOUTNO","SHOPID","item", "pluNO",
                            "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "bsNO", "WAREHOUSE",
                            "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO",
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
                                keyVal =par.getUnitRatio();     //unitRatio
                                break;
                            case 9:
                                keyVal = par.getPrice();    //price
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
                                keyVal = par.getBsNo();
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
                                keyVal=par.getDistriPrice();
                                if(Check.Null(keyVal))
                                    keyVal = "0";
                                break;
                            case 18:
                                keyVal = par.getDistriAmt();
                                if (Check.Null(keyVal))
                                    keyVal="0";
                                break;
                            case 19:
                                keyVal = bDate;
                                break;
                            case 20:
                                keyVal = par.getFeatureNo();
                                if (Check.Null(keyVal))
                                    keyVal=" ";
                                break;
                            case 21:
                                keyVal = par.getItem_origin();
                                break;
                            case 22:
                                keyVal = par.getPqty_origin();
                                break;
                            case 23:
                                keyVal=par.getLocation();
                                if(Check.Null(keyVal)){
                                    keyVal=" ";
                                }
                                break;
                            case 24:
                                keyVal=par.getExpDate();
                                break;
                            default:
                                break;
                        }

                        if (keyVal != null) {
                            insColCt++;
                            if (i == 2|| i == 21){
                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                            }else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 11 || i == 18 || i == 22){
                                columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                            }else{
                                columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                            }
                        }
                        else {
                            columnsVal[i] = null;
                        }
                    }

                    String[] columns2  = new String[insColCt];
                    DataValue[] insValue2 = new DataValue[insColCt];

                    insColCt = 0;
                    for (int i = 0; i < columnsVal.length; i++){
                        if (columnsVal[i] != null){
                            columns2[insColCt] = columnsName[i];
                            insValue2[insColCt] = columnsVal[i];
                            insColCt ++;
                            if (insColCt >= insValue2.length) break;
                        }
                    }

                    InsBean ib2 = new InsBean("DCP_LSTOCKOUT_DETAIL", columns2);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));//增加单身

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
                List<DCP_LStockOutRefundUpdateReq.level1ElmFileList> fileList = req.getRequest().getFileList();
                int item=1;
                if ( fileList != null && !fileList.isEmpty() ) {
                    for (DCP_LStockOutRefundUpdateReq.level1ElmFileList par : fileList) {
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
                //更新单头
                UptBean ub1 = new UptBean("DCP_LSTOCKOUT");
                ub1.addUpdateValue("bDate", new DataValue(bDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
                ub1.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
                ub1.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                ub1.addUpdateValue("memo", new DataValue(memo, Types.VARCHAR));
                ub1.addUpdateValue("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
                ub1.addUpdateValue("PTEMPLATENO", new DataValue(pTemplateNO, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                ub1.addUpdateValue("FEEOBJECTTYPE", new DataValue(req.getRequest().getFeeObjectType(), Types.VARCHAR));
                ub1.addUpdateValue("FEEOBJECTID", new DataValue(req.getRequest().getFeeObjectId(), Types.VARCHAR));
                ub1.addUpdateValue("FEE", new DataValue(req.getRequest().getFee(), Types.VARCHAR));
                ub1.addUpdateValue("BFEENO", new DataValue(req.getRequest().getFeeBillNo(), Types.VARCHAR));
                ub1.addUpdateValue("EMPLOYEEID", new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR));
                ub1.addUpdateValue("DEPARTID", new DataValue(req.getRequest().getDepartId(), Types.VARCHAR));


                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("lStockOutNO", new DataValue(lStockOutNO, Types.VARCHAR));
                ub1.addCondition("organizationNO", new DataValue(shopId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

                this.doExecuteDataToDB();

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }

        //}catch(Exception e){
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_LStockOutRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_LStockOutRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_LStockOutRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_LStockOutRefundUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        //必传值不为空
        String bDate = req.getRequest().getbDate();
        String lStockOutID = req.getRequest().getlStockOutID();
        String warehouse = req.getRequest().getWarehouse();
        List<DCP_LStockOutRefundUpdateReq.level1Elm> datas = req.getRequest().getDatas();
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

        for (DCP_LStockOutRefundUpdateReq.level1Elm par : datas) {
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
            if(Check.Null(par.getExpDate())){
               // errMsg.append( "有效日期不可为空值, ");
               // isFail = true;
            }

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
    protected TypeToken<DCP_LStockOutRefundUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_LStockOutRefundUpdateReq>(){};
    }

    @Override
    protected DCP_LStockOutRefundUpdateRes getResponseType()
    {
        return new DCP_LStockOutRefundUpdateRes();
    }
    
    
    
}
