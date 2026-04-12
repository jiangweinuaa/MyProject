package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ProcessTaskUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ProcessTaskUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_ProcessTaskUpdate extends SPosAdvanceService<DCP_ProcessTaskUpdateReq, DCP_ProcessTaskUpdateRes> {
    @Override
    protected void processDUID(DCP_ProcessTaskUpdateReq req, DCP_ProcessTaskUpdateRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String modifyBy = req.getOpNO();
        String modifyDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String modifyTime = new SimpleDateFormat("HHmmss").format(new Date());
        String processTaskNO = req.getRequest().getProcessTaskNo();
        String bDate = req.getRequest().getBDate();
        String memo = req.getRequest().getMemo();
        String pTemplateNO = req.getRequest().getPTemplateNo();
        String pDate = req.getRequest().getPDate();
        String warehouse = req.getRequest().getWarehouse();
        String materialWarehouseNO =req.getRequest().getMaterialWarehouseNo();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totDistriAmt=req.getRequest().getTotDistriAmt();
        String totCqty = req.getRequest().getTotCqty();
        String dtNo = req.getRequest().getDtNo();


        try {
            String sqlKey=""
                    + " select * from DCP_PROCESSTASK "
                    + " where EID='"+eId+"' and organizationNO='"+organizationNO+"' and processTaskNO='"+processTaskNO+"' "
                    + " and status='5' ";
            List<Map<String, Object>> getQDataKey = this.doQueryData(sqlKey,null);
            if (getQDataKey!=null && getQDataKey.isEmpty()==false) {
                String keyDate=getQDataKey.get(0).get("BDATE").toString();

                //删除原有单身
                DelBean db1 = new DelBean("DCP_PROCESSTASK_DETAIL");
                db1.addCondition("processTaskNO", new DataValue(processTaskNO, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //新增新的单身（多条记录）
                List<level1Elm> datas = req.getRequest().getDetailList();
                for (level1Elm par : datas) {
                    int insColCt = 0;
                    String[] columnsName = {
                            "PROCESSTASKNO", "SHOPID", "item", "pluNO",
                            "punit", "pqty", "baseunit", "baseqty", "unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO","MINQTY","DISPTYPE","SEMIWOTYPE","ODDVALUE","REMAINTYPE","BOMNO","VERSIONNUM","BEGINDATE","ENDDATE","GOODSSTATUS","DISPATCHSTATUS"
                    };
                    DataValue[] columnsVal = new DataValue[columnsName.length];

                    for (int i = 0; i < columnsVal.length; i++) {
                        String keyVal = null;
                        switch (i) {
                            case 0:
                                keyVal = processTaskNO;
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
                                keyVal = par.getPrice();    //price
                                if(Check.Null(keyVal))
                                    keyVal = "0";
                                break;
                            case 10:
                                keyVal = par.getAmt();    //amt
                                break;
                            case 11:
                                keyVal = eId;
                                break;
                            case 12:
                                keyVal = organizationNO;
                                break;
                            case 13:
                                keyVal = par.getMulQty();
                                break;
                            case 14:
                                keyVal=par.getDistriPrice();
                                if(Check.Null(keyVal))
                                    keyVal = "0";
                                break;
                            case 15:
                                keyVal = par.getDistriAmt();
                                if (Check.Null(keyVal))
                                    keyVal="0";
                                break;
                            case 16:
                                keyVal = bDate;
                                break;
                            case 17:
                                keyVal = par.getFeatureNo();
                                if (Check.Null(keyVal))
                                    keyVal=" ";
                                break;

                            case 18:
                                keyVal = par.getMinQty();
                                break;

                            case 19:
                                keyVal = par.getDispType();
                                break;
                            case 20:
                                keyVal = par.getSemiWoType();
                                break;
                            case 21:
                                keyVal = par.getOddValue();
                                break;
                            case 22:
                                keyVal = par.getRemainType();
                                break;
                            case 23:
                                keyVal = par.getBomNo();
                                break;
                            case 24:
                                keyVal = par.getVersionNum();
                                break;
                            case 25:
                                keyVal = par.getBeginDate();
                                break;
                            case 26:
                                keyVal = par.getEndDate();
                                break;

                            case 27:
                                keyVal = "0";
                                break;

                            case 28:
                                keyVal = "0";
                                break;

                            default:
                                break;
                        }
                        if (keyVal != null) {
                            insColCt++;
                            if (i == 2){
                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                            }else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13|| i == 14){
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
                            if (insColCt >= insValue2.length)
                                break;
                        }
                    }

                    InsBean ib2 = new InsBean("DCP_PROCESSTASK_DETAIL", columns2);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));
                }

                UptBean ub1 = new UptBean("DCP_PROCESSTASK");
                //add Value
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
                ub1.addUpdateValue("pTemplateNO", new DataValue(pTemplateNO, Types.VARCHAR));
                ub1.addUpdateValue("pDate", new DataValue(pDate, Types.VARCHAR));
                ub1.addUpdateValue("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
                ub1.addUpdateValue("MATERIALWAREHOUSE", new DataValue(materialWarehouseNO, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("DTNO", new DataValue(dtNo, Types.VARCHAR));
                ub1.addUpdateValue("EMPLOYEEID", new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR));
                ub1.addUpdateValue("DEPARTID", new DataValue(req.getRequest().getDepartId(), Types.VARCHAR));
                ub1.addUpdateValue("PRODTYPE", new DataValue(req.getRequest().getProdType(), Types.VARCHAR));

                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("processTaskNO", new DataValue(processTaskNO, Types.VARCHAR));
                ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));
                
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }
    
            this.doExecuteDataToDB();
    
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessTaskUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessTaskUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessTaskUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessTaskUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String bDate = req.getRequest().getBDate();
        List<DCP_ProcessTaskUpdateReq.level1Elm> datas = req.getRequest().getDetailList();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totDistriAmt=req.getRequest().getTotDistriAmt();
        String totCqty = req.getRequest().getTotCqty();
        String processTaskNO = req.getRequest().getProcessTaskNo();
        String warehouse = req.getRequest().getWarehouse();
        String materialWarehouseNo = req.getRequest().getMaterialWarehouseNo();
        String pDate = req.getRequest().getPDate();

        if(Check.Null(bDate)){
            errMsg.append("营业日期不可为空值, ");
            isFail = true;
        }
        if(Check.Null(processTaskNO)){
            errMsg.append("加工任务单单号不可为空值, ");
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
        //if (Check.Null(warehouse)) {
        //    errMsg.append("加工仓库不可为空值, ");
        //    isFail = true;
        //}
        //if (Check.Null(materialWarehouseNo)) {
        ///    errMsg.append("原料仓库不可为空值, ");
         //   isFail = true;
        //}
        if (Check.Null(pDate)) {
            errMsg.append("生产日期不可为空值, ");
            isFail = true;
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for(DCP_ProcessTaskUpdateReq.level1Elm par : datas) {
            String pluNo = par.getPluNo();
 
            if (Check.Null(par.getItem())) {
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getPluNo())) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getPunit())) {
                errMsg.append("报损单位不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getPqty())) {
                errMsg.append("加工数量不可为空值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(par.getPqty())) {
                errMsg.append("商品"+pluNo+"任务数不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getPunit())) {
                errMsg.append("商品"+pluNo+"任务数量单位不可为空值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(par.getBaseQty())) {
                errMsg.append("商品"+pluNo+"基本数量不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getBaseUnit())) {
                errMsg.append("商品"+pluNo+"基本单位不可为空值, ");
                isFail = true;
            }
            if (!PosPub.isNumericType(par.getUnitRatio())) {
                errMsg.append("商品"+pluNo+"单位转换率不可为空值, ");
                isFail = true;
            }

            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessTaskUpdateReq> getRequestType() {
        return new TypeToken<DCP_ProcessTaskUpdateReq>(){};
    }

    @Override
    protected DCP_ProcessTaskUpdateRes getResponseType() {
        return new DCP_ProcessTaskUpdateRes();
    }


}
