package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_POrderUpdateReq;
import com.dsc.spos.json.cust.req.DCP_POrderUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_POrderUpdateReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_POrderUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服務函數：POrderUpdate
 * 說明：出货单修改保存
 * 服务说明：出货单修改保存
 *
 * @author panjing
 * @since 2016-10-08
 */
public class DCP_POrderUpdate extends SPosAdvanceService<DCP_POrderUpdateReq, DCP_POrderUpdateRes> {
    @Override
    protected boolean isVerifyFail(DCP_POrderUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        List<level1Elm> jsonDatas = request.getDatas();
        //必传值不为空
        String porderNO = request.getPorderNo();
        String rDate = request.getrDate();
        String rTime = request.getrTime();
        String status = request.getStatus();

        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt = request.getTotDistriAmt();
        String totCqty = request.getTotCqty();

        if (Check.Null(request.getpTemplateNo()) && Check.Null(request.getReceiptOrgNo())) {
            errMsg.append("无模板要货时发货组织必传且不允许为空, ");
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
        if (Check.Null(rDate)) {
            errMsg.append("需求日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(rTime)) {
            errMsg.append("需求时间不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if (Check.Null(porderNO)) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (level1Elm par : jsonDatas) {
            //必传值不为空
            String item = par.getItem();
            String pluNo = par.getPluNo();
            String punit = par.getPunit();
            String pqty = par.getPqty();
            String baseUnit = par.getBaseUnit();
            String baseQty = par.getBaseQty();
            String unitRatio = par.getUnitRatio();
            String price = par.getPrice();
            String amt = par.getAmt();

            if (Check.Null(item)) {
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pluNo)) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }
            if (Check.Null(punit)) {
                errMsg.append("商品" + pluNo + "单位不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pqty)) {
                errMsg.append("商品" + pluNo + "数量不可为空值, ");
                isFail = true;
            }
            if (price == null) {
                errMsg.append("商品" + pluNo + "单价不可为空值, ");
                isFail = true;
            }
            if (amt == null) {
                errMsg.append("商品" + pluNo + "金额不可为空值, ");
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

            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_POrderUpdateReq> getRequestType() {
        return new TypeToken<DCP_POrderUpdateReq>() {
        };
    }

    @Override
    protected DCP_POrderUpdateRes getResponseType() {
        return new DCP_POrderUpdateRes();
    }

    @Override
    protected void processDUID(DCP_POrderUpdateReq req, DCP_POrderUpdateRes res) throws Exception {
        StringBuffer errMsg = new StringBuffer();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        levelElm request = req.getRequest();

        String StrISUrgentOrder = request.getISUrgentOrder();
        if (Check.Null(StrISUrgentOrder)) {
            StrISUrgentOrder = "N";
        }

        String memo = request.getMemo();
        String rDate = request.getrDate();
        String rTime = request.getrTime();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt = request.getTotDistriAmt();
        String uTotDistriAmt = request.getuTotDistriAmt();// 原进货金额合计
        String totCqty = request.getTotCqty();
        //是否追单要货
        String isAdd = request.getIsAdd();
        //要货模板
        String pTemplateNo = request.getpTemplateNo();

        Calendar tempcalPre = Calendar.getInstance();//获得当前时间
        SimpleDateFormat tempdfPre = new SimpleDateFormat("yyyyMMdd");
        Calendar tempcalAdd = Calendar.getInstance();//获得当前时间
        SimpleDateFormat tempdfAdd = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat tempdfAdd1 = new SimpleDateFormat("yyyyMMdd");
        DCP_InterSettleDataGenerateReq generateReq = new DCP_InterSettleDataGenerateReq();

        //try {

            if (!Check.Null(req.getRequest().getpTemplateNo())) {
                //【ID1026796】【霸王3.0】追加要货需要控制要货截止时间---服务端 by jinzma 20220627
                String isAppend = req.getRequest().getIsAppend();
                if (Check.Null(isAppend) || isAppend.equals("Y")) {
                    String isAddSql = "select * from DCP_PORDER Where eId = '" + eId + "' and shopId = '" + shopId + "' "
                            + " and status = '2' and rDate = '" + rDate + "' and ptemplateNo = '" + pTemplateNo + "'  ";
                    List<Map<String, Object>> addDatas = this.doQueryData(isAddSql, null);
                    if (addDatas != null && addDatas.size() > 0) {
                        isAdd = "Y";
                        //StrISUrgentOrder = "Y";
                    } else {
                        isAdd = "N";
                        //StrISUrgentOrder = "N";
                    }
                } else {
                    isAdd = "N";
                }
            }

            //要货默认需求日期   Pre_Demand_Days  参数里已经没有了
            tempcalPre.add(Calendar.DATE, 0);
            String preDemandDate = tempdfPre.format(tempcalPre.getTime());

            //追加要货默认需求日期
            int addDemandDates;
            String addDemandDate = PosPub.getPARA_SMS(dao, eId, shopId, "Add_Demand_Days");

            if (addDemandDate == null || Check.Null(addDemandDate) || !PosPub.isNumeric(addDemandDate)) {
                tempcalAdd.add(Calendar.DATE, 0);
                addDemandDate = tempdfAdd.format(tempcalAdd.getTime());
            } else {
                addDemandDates = Integer.parseInt(addDemandDate);
                tempcalAdd.add(Calendar.DATE, addDemandDates);
                addDemandDate = tempdfAdd1.format(tempcalAdd.getTime());
            }

            //rdate不能小于今天的日期
            Date d3 = tempdfAdd.parse(rDate);
            String sdate = tempdfAdd.format(Calendar.getInstance().getTime());
            Date d4 = tempdfAdd.parse(sdate);
            if (d3.compareTo(d4) < 0) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需求日期不能小于当天");
            }

            if (isAdd.equals("Y")) {
                //rDate = addDemandDate;
                Date d1 = tempdfAdd.parse(rDate);
                Date d2 = tempdfAdd.parse(addDemandDate);
                if (d1.compareTo(d2) < 0) {
                    if (!StrISUrgentOrder.equals("Y")) {
                        String tempStr = "此要货模板当前时间已经过了要货截止时间，需求日期不能小于"
                                + addDemandDate.substring(4, 6) + "月" + addDemandDate.substring(6, 8) + "日";
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, tempStr);
                    } else {
                        request.setISUrgentOrder("Y");
                    }
                } else {
                    request.setISUrgentOrder("N");
                }
            } else if (isAdd.equals("N") && (pTemplateNo == null || pTemplateNo.isEmpty())) {
                //rDate = preDemandDate;
                Date d1 = tempdfAdd.parse(rDate);
                Date d2 = tempdfAdd.parse(preDemandDate);
                if (d1.compareTo(d2) < 0) {
                    if (!StrISUrgentOrder.equals("Y")) {
                        String tempStr = "此要货模板当前时间已经过了要货截止时间，需求日期不能小于"
                                + preDemandDate.substring(4, 6) + "月" + preDemandDate.substring(6, 8) + "日";
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, tempStr);
                    } else {
                        request.setISUrgentOrder("Y");
                    }
                } else {
                    request.setISUrgentOrder("N");
                }

            } else if (isAdd.equals("N")) {
                //直接改成查询前置日期和前置时间
                String optionalTime = this.GetOptionalTime(req);
                SimpleDateFormat dfDate_cur = new SimpleDateFormat("yyyyMMdd");
                String sallcurtime = rDate;
                //需求日期+现在的时间
                Date d1 = dfDate_cur.parse(sallcurtime);
                Date d2 = dfDate_cur.parse(optionalTime);
                if (d1.compareTo(d2) < 0) {
                    if (!StrISUrgentOrder.equals("Y")) {
                        String tempDate = tempdfAdd.format(d2);
                        String tempStr;
                        if (tempDate.length() < 8) {
                            tempStr = "此要货模板当前时间已经过了要货截止时间,需求日期不能小于" + tempDate;
                        } else {
                            tempStr = "此要货模板当前时间已经过了要货截止时间，需求日期不能小于"
                                    + tempDate.substring(4, 6) + "月" + tempDate.substring(6, 8) + "日";
                        }
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, tempStr);
                    } else {
                        request.setISUrgentOrder("Y");
                    }
                } else {
                    request.setISUrgentOrder("N");
                }

            }


            //查询模板对应的发货组织编号
            String pTemplateNO = request.getpTemplateNo();
            String receiptOrg = "";
            String rdate_type = "";
            String rdate_add = "";
            String rdate_values = "";
            String rdate_times = "";
            String rtimestype = "";

            String sqlTemp = " SELECT RECEIPT_ORG,RDATE_TYPE,RDATE_ADD,RDATE_VALUES,REVOKE_DAY,REVOKE_TIME,RDATE_TIMES,RTIMESTYPE  FROM DCP_PTEMPLATE "
                    + " WHERE EID='" + eId + "' AND  PTEMPLATENO='" + pTemplateNO + "' AND DOC_TYPE='0' ";
            List<Map<String, Object>> getQDate = this.doQueryData(sqlTemp, null);
            if (getQDate != null && !getQDate.isEmpty()) {
                receiptOrg = getQDate.get(0).get("RECEIPT_ORG").toString();

                rdate_type = getQDate.get(0).get("RDATE_TYPE").toString();//0或空为不管控 1为按周 2为按月
                rdate_add = getQDate.get(0).get("RDATE_ADD").toString();//0或空为不管  当RDATE_TYPE为1时代表需要控制几周 当RDATE_TYPE为1时代表需要控制下月
                rdate_values = getQDate.get(0).get("RDATE_VALUES").toString();//当RDATE_TYPE为1时填入 1,2代表周一，周二等 当RDATE_TYPE为2时填入1,2代表1号，2号
                rdate_times = getQDate.get(0).get("RDATE_TIMES").toString();//0或空为不限制  1为限制1次
                rtimestype = getQDate.get(0).get("RTIMESTYPE").toString();//1按周期控制次数 2按需求日期天控制次数
            }

            if (Check.Null(pTemplateNO)) {
                receiptOrg = request.getReceiptOrgNo();
            }

            //*******************RDATE_TYPE控制处理****************
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat rdTypedf = new SimpleDateFormat("yyyyMMdd");
            String cursysDate = rdTypedf.format(cal.getTime());

            String modifyBy = req.getOpNO();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String modifyDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String modifyTime = df.format(cal.getTime());
            double PRedictAMT = request.getPRedictAMT();
            String BeginDate = request.getBeginDate();
            String EndDate = request.getEndDate();
            double avgsaleAMT = request.getAvgsaleAMT();
            double modifRatio = request.getModifRatio();
            String porderNO = request.getPorderNo();

            //校验此要货单是否存在
            String sql = this.getQuerySql(req);
            String[] conditionValues = {organizationNO, eId, shopId, porderNO}; //查詢條件
            List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

            if (getQData != null && !getQData.isEmpty()) {
                if (!getQData.get(0).get("STATUS").toString().equals("0")) {
                    errMsg.append("此单已确认，不允许修改！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }

                String erpno = getQData.get(0).get("PROCESS_ERP_NO").toString();
                //撤销后的单据此字段有值，无需管控
                if (erpno == null || erpno.length() == 0) {
                    if (pTemplateNO != null && !pTemplateNO.equals("")) {
                        //按周
                        if (rdate_type.equals("1")) {
                            if (rDate.length() != 8) {
                                //直接抛异常
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需求日期格式不正确！");
                            }

                            //需求日期必须在范围
                            SimpleDateFormat mydf = new SimpleDateFormat("yyyyMMdd");
                            MyCommon mycommon = new MyCommon();

                            if (!PosPub.isNumeric(rdate_add)) {
                                rdate_add = "0";
                            }
                            if (!PosPub.isNumeric(rdate_times)) {
                                rdate_times = "0";
                            }
                            int numWeek = Integer.parseInt(rdate_add);

                            //今天是周几
                            String sTodayisWeek = mycommon.getWeekOfDate(cal.getTime());
                            int iTodayisWeek = Integer.parseInt(sTodayisWeek);//5

                            String sMustmaxDate;
                            String sMustminDate;

                            if (numWeek != 0) {
                                //那周的起始范围
                                int maxDays = (7 - iTodayisWeek) + numWeek * 7;
                                int minDays = maxDays - 6;

                                //
                                sMustmaxDate = PosPub.GetStringDate(cursysDate, maxDays);
                                sMustminDate = PosPub.GetStringDate(cursysDate, minDays);

                                if (mydf.parse(rDate).compareTo(mydf.parse(sMustminDate)) < 0 || mydf.parse(rDate).compareTo(mydf.parse(sMustmaxDate)) > 0) {
                                    //直接抛异常
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需求日期必须间隔在 " + numWeek + "周范围内！");
                                }
                            } else {
                                //0不管控
                                //本周的起始范围
                                int maxDays = (7 - iTodayisWeek) + 1;
                                int minDays = maxDays - 6;
                                //
                                sMustmaxDate = PosPub.GetStringDate(cursysDate, maxDays);
                                sMustminDate = PosPub.GetStringDate(cursysDate, minDays);
                            }

                            String sWeek = mycommon.getWeekOfDate(mydf.parse(rDate));
                            if (!rdate_values.contains(sWeek)) {
                                //直接抛异常
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板已控制周" + rdate_values + "才能要货！");
                            }

                            int numRdate_times = Integer.parseInt(rdate_times);
                            if (numRdate_times != 0) {
                                String sqlRdateTimes;
                                if (rtimestype.equals("") || rtimestype.equals("1")) {
                                    sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + req.geteId() + "' AND A.SHOPID='" + req.getShopId() + "' AND PTEMPLATENO='" + pTemplateNO + "' AND RDATE>='" + sMustminDate + "' AND RDATE<='" + sMustmaxDate + "'  and porderno<>'" + request.getPorderNo() + "' ";
                                } else {
                                    sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + req.geteId() + "' AND A.SHOPID='" + req.getShopId() + "' AND PTEMPLATENO='" + pTemplateNO + "' AND RDATE='" + rDate + "' and porderno<>'" + request.getPorderNo() + "' ";
                                }

                                List<Map<String, Object>> getQTimes = this.doQueryData(sqlRdateTimes, null);
                                int iCountTimes = 0;
                                if (getQTimes != null && !getQTimes.isEmpty()) {
                                    String numTimes = getQTimes.get(0).get("NUM").toString();
                                    if (PosPub.isNumeric(numTimes)) {
                                        iCountTimes = Integer.parseInt(numTimes);
                                    }
                                }

                                //达到次数上限
                                if (iCountTimes >= numRdate_times) {
                                    //直接抛异常
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板已限制使用次数，不能超过" + numRdate_times + "次！");
                                }
                            }

                        } else if (rdate_type.equals("2")) { //按月

                            if (rDate.length() != 8) {
                                //直接抛异常
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需求日期格式不正确！");
                            }

                            //需求日期必须在范围
                            SimpleDateFormat mydf = new SimpleDateFormat("yyyyMMdd");

                            if (!PosPub.isNumeric(rdate_add)) {
                                rdate_add = "0";
                            }
                            if (!PosPub.isNumeric(rdate_times)) {
                                rdate_times = "0";
                            }
                            int numMonth = Integer.parseInt(rdate_add);

                            //今天几月
                            int iTodayisMonth = cal.get(Calendar.MONTH);

                            String sMustmaxDate;
                            String sMustminDate;

                            //
                            Calendar calMonth = Calendar.getInstance();//获得当前时间

                            if (numMonth != 0) {
                                calMonth.set(Calendar.MONTH, iTodayisMonth + numMonth);
                                int iDays = calMonth.getActualMaximum(Calendar.DATE);
                                calMonth.set(Calendar.DATE, iDays);
                                //那个月的起始日期
                                sMustmaxDate = mydf.format(calMonth.getTime());

                                calMonth.set(Calendar.DATE, 1);
                                sMustminDate = mydf.format(calMonth.getTime());

                                if (mydf.parse(rDate).compareTo(mydf.parse(sMustminDate)) < 0 || mydf.parse(rDate).compareTo(mydf.parse(sMustmaxDate)) > 0) {
                                    //直接抛异常
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需求日期必须间隔在 " + numMonth + "月范围内！");
                                }
                            } else {
                                //0不管控
                                int iDays = cal.getActualMaximum(Calendar.DATE);

                                //那个月的起始日期
                                calMonth.set(Calendar.DATE, iDays);
                                sMustmaxDate = mydf.format(calMonth.getTime());

                                calMonth.set(Calendar.DATE, 1);
                                sMustminDate = mydf.format(calMonth.getTime());
                            }

                            //需求日期那天是几号
                            calMonth.setTime(mydf.parse(rDate));
                            int iThatisDay = calMonth.get(Calendar.DATE);

                            if (!rdate_values.contains(String.valueOf(iThatisDay))) {
                                //直接抛异常
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板已控制每月" + rdate_values + "日才能要货！");
                            }

                            int numRdate_times = Integer.parseInt(rdate_times);
                            if (numRdate_times != 0) {
                                String sqlRdateTimes;
                                if (rtimestype.equals("") || rtimestype.equals("1")) {
                                    sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + req.geteId() + "' AND A.SHOPID='" + req.getShopId() + "' AND PTEMPLATENO='" + pTemplateNO + "' AND RDATE>='" + sMustminDate + "' AND RDATE<='" + sMustmaxDate + "'  and porderno<>'" + request.getPorderNo() + "' ";
                                } else {
                                    sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + req.geteId() + "' AND A.SHOPID='" + req.getShopId() + "' AND PTEMPLATENO='" + pTemplateNO + "' AND RDATE='" + rDate + "' and porderno<>'" + request.getPorderNo() + "' ";
                                }

                                List<Map<String, Object>> getQTimes = this.doQueryData(sqlRdateTimes, null);
                                int iCountTimes = 0;
                                if (getQTimes != null && !getQTimes.isEmpty()) {
                                    String numTimes = getQTimes.get(0).get("NUM").toString();
                                    if (PosPub.isNumeric(numTimes)) {
                                        iCountTimes = Integer.parseInt(numTimes);
                                    }
                                }

                                //达到次数上限
                                if (iCountTimes >= numRdate_times) {
                                    //直接抛异常
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板已限制使用次数，不能超过" + numRdate_times + "次！");
                                }
                            }

                        }
                    }
                }

                //【ID1031024】【詹记】DCP_PORDER_DETAIL.BDATE 调整 by jinzma 20230203
                //String keyDate=getQData.get(0).get("BDATE").toString();
                String bDate = getQData.get(0).get("BDATE").toString();


                //删除原来单身
                DelBean db1 = new DelBean("DCP_PORDER_DETAIL");
                db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("porderNO", new DataValue(porderNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                generateReq.setRequest(generateReq.new Request());
                generateReq.setToken(req.getToken());
                generateReq.seteId(req.geteId());
                generateReq.setServiceId("DCP_InterSettleDataGenerate");
                generateReq.getRequest().setOrganizationNo(organizationNO);
                generateReq.getRequest().setBillNo(porderNO);
                generateReq.getRequest().setSupplyOrgNo(receiptOrg);
                generateReq.getRequest().setDetail(new ArrayList<>());
                generateReq.getRequest().setReturnSupplyPrice("Y");


                for (DCP_POrderUpdateReq.level1Elm par : request.getDatas()) {
                    ColumnDataValue dcp_porder_detail = new ColumnDataValue();
                    dcp_porder_detail.add("porderNO", DataValues.newString(porderNO));
                    dcp_porder_detail.add("SHOPID", DataValues.newString(shopId));
                    dcp_porder_detail.add("item", DataValues.newString(par.getItem()));
                    dcp_porder_detail.add("pluNO", DataValues.newString(par.getPluNo()));
                    dcp_porder_detail.add("punit", DataValues.newString(par.getPunit()));
                    dcp_porder_detail.add("pqty", DataValues.newString(par.getPqty()));
                    dcp_porder_detail.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));
                    dcp_porder_detail.add("BASEQTY", DataValues.newString(par.getBaseQty()));
                    dcp_porder_detail.add("unit_Ratio", DataValues.newString(par.getUnitRatio()));
                    dcp_porder_detail.add("price", DataValues.newString(StringUtils.toString(par.getPrice(), "0")));
                    dcp_porder_detail.add("amt", DataValues.newString(par.getAmt()));
                    dcp_porder_detail.add("EID", DataValues.newString(eId));
                    dcp_porder_detail.add("organizationNO", DataValues.newString(organizationNO));
                    dcp_porder_detail.add("Detail_status", DataValues.newString("0"));
                    dcp_porder_detail.add("REF_WQTY", DataValues.newString(StringUtils.toString(par.getRefWQty(), "0")));
                    dcp_porder_detail.add("REF_SQTY", DataValues.newString(StringUtils.toString(par.getRefSQty(), "0")));
                    dcp_porder_detail.add("REF_PQTY", DataValues.newString(StringUtils.toString(par.getRefPQty(), "0")));
                    dcp_porder_detail.add("SO_QTY", DataValues.newString(StringUtils.toString(par.getSoQty(), "0")));
                    dcp_porder_detail.add("MUL_QTY", DataValues.newString(StringUtils.toString(par.getMulQty(), "0")));
                    dcp_porder_detail.add("MIN_QTY", DataValues.newString(StringUtils.toString(par.getMinQty(), "0")));
                    dcp_porder_detail.add("MAX_QTY", DataValues.newString(StringUtils.toString(par.getMaxQty(), "0")));
                    dcp_porder_detail.add("propQty", DataValues.newString(StringUtils.toString(par.getPropQty(), "0")));
                    dcp_porder_detail.add("MEMO", DataValues.newString(par.getMemo()));
                    dcp_porder_detail.add("KQTY", DataValues.newString(StringUtils.toString(par.getkQty(), "0")));
                    dcp_porder_detail.add("KADJQTY", DataValues.newString(StringUtils.toString(par.getkAdjQty(), "0")));
                    dcp_porder_detail.add("PROPADJQTY", DataValues.newString(StringUtils.toString(par.getPropAdjQty(), "0")));
                    dcp_porder_detail.add("DISTRIPRICE", DataValues.newString(StringUtils.toString(par.getDistriPrice(), "0")));
                    dcp_porder_detail.add("DISTRIAMT", DataValues.newString(StringUtils.toString(par.getDistriAmt(), "0")));
                    dcp_porder_detail.add("BDATE", DataValues.newString(bDate));
                    dcp_porder_detail.add("HEADSTOCKQTY", DataValues.newString(StringUtils.toString(par.getHeadStockQty(), "0")));
                    dcp_porder_detail.add("FEATURENO", DataValues.newString(StringUtils.toString(par.getFeatureNo(), " ")));
                    dcp_porder_detail.add("UDISTRIPRICE", DataValues.newString(par.getuDistriPrice()));
                    dcp_porder_detail.add("ISNEWGOODS", DataValues.newString(StringUtils.toString(par.getFeatureNo(), "N")));
                    dcp_porder_detail.add("ISHOTGOODS", DataValues.newString(StringUtils.toString(par.getFeatureNo(), "N")));
                    dcp_porder_detail.add("SUPPLIERTYPE", DataValues.newString(par.getSupplierType()));
                    dcp_porder_detail.add("SUPPLIERID", DataValues.newString(par.getSupplierId()));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PORDER_DETAIL", dcp_porder_detail)));


                    DCP_InterSettleDataGenerateReq.Detail genDetail = generateReq.new Detail();
                    generateReq.getRequest().getDetail().add(genDetail);
                    genDetail.setReceiveOrgNo(organizationNO);
                    genDetail.setItem(par.getItem());
                    genDetail.setPluNo(par.getPluNo());
                    genDetail.setFeatureNo(StringUtils.toString(par.getFeatureNo(), " "));
                    genDetail.setPUnit(par.getPunit());
                    genDetail.setPQty(par.getPqty());
                    genDetail.setReceivePrice(par.getDistriPrice());
                    genDetail.setReceiveAmt(par.getDistriAmt());

                }
                /*
				//新增單身 (多筆)
				List<level1Elm> jsonDatas = request.getDatas();
				for (level1Elm par : jsonDatas) {
					
					if(Float.parseFloat(par.getPqty())==0) {
						continue;
					}
					int insColCt = 0;
					String[] columnsName = {
							"porderNO", "SHOPID", "item","pluNO", "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
							"price", "amt", "EID", "organizationNO","Detail_status",
							"REF_WQTY","REF_SQTY","REF_PQTY","SO_QTY","MUL_QTY","MIN_QTY","MAX_QTY","propQty","MEMO"
							,"KQTY" , "KADJQTY", "PROPADJQTY","DISTRIPRICE","DISTRIAMT","BDATE",
							"HEADSTOCKQTY","FEATURENO","UDISTRIPRICE","ISNEWGOODS","ISHOTGOODS","REVIEW_QTY","REASON"
							,"SUPPLIERTYPE","SUPPLIERID"
					};
					
					DataValue[] columnsVal = new DataValue[columnsName.length];
					
					for (int i = 0; i < columnsVal.length; i++) {
						String keyVal = null;
						switch (i) {
							case 0:
								keyVal = porderNO;
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
								keyVal = par.getBaseQty();  //wqty
								break;
							case 8:
								keyVal = par.getUnitRatio();    //unitRatio
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
								keyVal = organizationNO;
								break;
							case 13:
								keyVal = "0";    //Detail_status
								break;
							case 14:
								keyVal = par.getRefWQty();
								break;
							case 15:
								keyVal = par.getRefSQty();
								break;
							case 16:
								keyVal = par.getRefPQty();
								break;
							case 17:
								keyVal = par.getSoQty();
								break;
							case 18:
								keyVal = par.getMulQty();
								break;
							case 19:
								keyVal = par.getMinQty();
								break;
							case 20:
								keyVal = par.getMaxQty();
								break;
							case 21:
								keyVal = Double.toString(par.getPropQty());
								if(keyVal==null || keyVal.isEmpty()){
									keyVal = "0";
								}
								break;
							case 22:
								keyVal = par.getMemo();
								if(par.getMemo()==null || par.getMemo().isEmpty()){
									keyVal = "";
								}
								break;
							case 23:
								keyVal = par.getkQty() ;
								if(keyVal==null || keyVal.isEmpty()){
									keyVal = "0";
								}
								break;
							case 24:
								keyVal = par.getkAdjQty();
								if(keyVal==null || keyVal.isEmpty()){
									keyVal = "0";
								}
								break;
							case 25:
								keyVal = par.getPropAdjQty();
								if(keyVal==null || keyVal.isEmpty()){
									keyVal = "0";
								}
								break;
							case 26:
								keyVal = par.getDistriPrice();
								if(keyVal==null || keyVal.isEmpty()){
									keyVal = "0";
								}
								break;
							case 27:
								keyVal = par.getDistriAmt();
								if(keyVal==null || keyVal.isEmpty()){
									keyVal = "0";
								}
								break;
							case 28:
								keyVal = bDate;
								break;
							case 29:
								keyVal = par.getHeadStockQty();
								if (Check.Null(keyVal)) keyVal="0";
								break;
							case 30:
								keyVal = par.getFeatureNo();
								if (Check.Null(keyVal))
									keyVal=" ";
								break;
							case 31:
								keyVal = par.getuDistriPrice();
								break;
							case 32:
								keyVal = par.getIsNewGoods();
								if (Check.Null(keyVal)){
									keyVal="N";
								}
								break;
							case 33:
								keyVal = par.getIsHotGoods();
								if (Check.Null(keyVal)){
									keyVal="N";
								}
								break;
                            case 34:
                                keyVal = par.getReviewQty();
                                if (Check.Null(keyVal)){
                                    keyVal="0";
                                }
                                break;
                            case 35:
                                keyVal = par.getReason();
                                if (Check.Null(keyVal)){
                                    keyVal="";
                                }
                                break;
                            case 36: 
                                keyVal = par.getSupplierType();     
                                break;
                             case 37:
                                 keyVal = par.getSupplierId();     
                                 break;
							default:
								break;
						}
						
						if (keyVal != null) {
							insColCt++;
							if (i == 2 ){
								columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
							}else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10|| i == 14|| i == 15|| i == 16|| i == 17|| i == 18 || i == 19 || i == 20|| i == 21 ){
								columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
							}else{
								columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
							}
						} else {
							columnsVal[i] = null;
						}
					}
					String[]    columns2  = new String[insColCt];
					DataValue[] insValue2 = new DataValue[insColCt];
					// 依照傳入參數組譯要insert的欄位與數值；
					insColCt = 0;
					
					for (int i=0;i<columnsVal.length;i++){
						if (columnsVal[i] != null){
							columns2[insColCt] = columnsName[i];
							insValue2[insColCt] = columnsVal[i];
							insColCt ++;
							if (insColCt >= insValue2.length) break;
						}
					}
					
					InsBean ib2 = new InsBean("DCP_PORDER_DETAIL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2));
				}
				
				*/
                //更新单头
                UptBean ub1 = new UptBean("DCP_PORDER");

                //【ID1024487】【霸王3.0】移动门店要货申请单据日期问题 注释BDATE更新 by jinzma 20220316
                //ub1.addUpdateValue("BDate", new DataValue(bDate, Types.VARCHAR));

                ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("modifyBy", new DataValue(modifyBy, Types.VARCHAR));
                ub1.addUpdateValue("modify_Date", new DataValue(modifyDate, Types.VARCHAR));
                ub1.addUpdateValue("modify_Time", new DataValue(modifyTime, Types.VARCHAR));
                ub1.addUpdateValue("tot_Pqty", new DataValue(totPqty, Types.VARCHAR));
                ub1.addUpdateValue("tot_Amt", new DataValue(totAmt, Types.VARCHAR));
                ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                // 更新原进货金额合计
                ub1.addUpdateValue("UTOTDISTRIAMT", new DataValue(uTotDistriAmt, Types.VARCHAR));

                ub1.addUpdateValue("tot_Cqty", new DataValue(totCqty, Types.VARCHAR));
                ub1.addUpdateValue("rDate", new DataValue(rDate, Types.VARCHAR));
                ub1.addUpdateValue("rTime", new DataValue(rTime, Types.VARCHAR));
                ub1.addUpdateValue("ptemplateno", new DataValue(pTemplateNO, Types.VARCHAR));
                ub1.addUpdateValue("RECEIPT_ORG", new DataValue(receiptOrg, Types.VARCHAR));
                ub1.addUpdateValue("IS_ADD", new DataValue(isAdd, Types.VARCHAR));
                ub1.addUpdateValue("memo", new DataValue(memo, Types.VARCHAR));
                ub1.addUpdateValue("PRedictAMT", new DataValue(PRedictAMT, Types.DOUBLE));
                ub1.addUpdateValue("BeginDate", new DataValue(BeginDate, Types.VARCHAR));
                ub1.addUpdateValue("EndDate", new DataValue(EndDate, Types.VARCHAR));
                ub1.addUpdateValue("avgsaleAMT", new DataValue(avgsaleAMT, Types.DOUBLE));
                ub1.addUpdateValue("modifRatio", new DataValue(modifRatio, Types.DOUBLE));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("SUPPLIERTYPE", new DataValue(request.getSupplierType(), Types.VARCHAR));
                ub1.addUpdateValue("EMPLOYEEID", new DataValue(request.getEmployeeID(), Types.VARCHAR));
                ub1.addUpdateValue("DEPARTID", new DataValue(request.getDepartID(), Types.VARCHAR));  //add by 01029 20250217
                //StrISUrgentOrder = request.getISUrgentOrder()==null?"N":request.getISUrgentOrder();
                String StrISPRedict = request.getISPRedict() == null ? "N" : request.getISPRedict();

                // 用于要货单显示 营业预估的调整量和预估量
                String isForecast = request.getIsForecast() == null ? "N" : request.getIsForecast();
                String ofNo = "";
                if (!Check.Null(request.getOfNo())) {
                    ofNo = request.getOfNo();
                }

                String oType = "0";//普通要货
                if (!Check.Null(req.getRequest().getoType())) {
                    oType = req.getRequest().getoType();
                }

                ub1.addUpdateValue("ISUrgentOrder", new DataValue(StrISUrgentOrder, Types.VARCHAR));
                ub1.addUpdateValue("ISPRedict", new DataValue(StrISPRedict, Types.VARCHAR));
                ub1.addUpdateValue("ISFORECAST", new DataValue(isForecast, Types.VARCHAR));
                ub1.addUpdateValue("OFNO", new DataValue(ofNo, Types.VARCHAR));
                ub1.addUpdateValue("OTYPE", new DataValue(oType, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                // condition
                ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("porderNO", new DataValue(porderNO, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));

            } else {
                errMsg.append("单据不存在，请重新输入 ");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

            //判断收货组织法人<>供货组织法人才需要生成内部结算
            Map<String, String> corpData = PosPub.getCorpByOrgNo(organizationNO, receiptOrg);
            if (!StringUtils.equals(corpData.get(organizationNO),
                    corpData.get(receiptOrg))) {

                generateReq.getRequest().setBillType(DCP_InterSettleDataGenerate.BillType.BillType10000.getType());
                ServiceAgentUtils<DCP_InterSettleDataGenerateReq, DCP_InterSettleDataGenerateRes> agentUtils = new ServiceAgentUtils<>();

                DCP_InterSettleDataGenerateRes dcp_interSettleDataGenerateRes = agentUtils.agentService(generateReq, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                });

                if(dcp_interSettleDataGenerateRes.isSuccess()){
                    List<DCP_InterSettleDataGenerateRes.SupPriceDetail> supPriceDetail = dcp_interSettleDataGenerateRes.getSupPriceDetail();
                    if(supPriceDetail.size()>0){
                        for(DCP_InterSettleDataGenerateRes.SupPriceDetail supd:supPriceDetail) {
                            String item = supd.getItem();
                            String receivePrice = supd.getReceivePrice();

                            List<level1Elm> collect = request.getDatas().stream().filter(x -> x.getItem().equals(item)).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(collect)){
                                BigDecimal pQty = new BigDecimal(collect.get(0).getPqty());
                                BigDecimal receiveAmt = pQty.multiply(new BigDecimal(receivePrice));

                                UptBean ub1 = new UptBean("DCP_PORDER_DETAIL");
                                ub1.addUpdateValue("DISTRIPRICE", DataValues.newString(receivePrice));
                                ub1.addUpdateValue("DISTRIAMT",DataValues.newString(receiveAmt));

                                ub1.addCondition("EID", DataValues.newString(eId));
                                ub1.addCondition("PORDERNO",DataValues.newString(porderNO));
                                ub1.addCondition("ITEM",DataValues.newString(item));
                                this.addProcessData(new DataProcessBean(ub1));
                            }
                        }
                    }
                    this.doExecuteDataToDB();

                }else{
                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("内部交易结算执行失败");
                }


                //if (!agentUtils.agentServiceSuccess(generateReq, new TypeToken<DCP_InterSettleDataGenerateRes>() {



                //})) {
                //    res.setSuccess(true);
                //    res.setServiceStatus("000");
                //    res.setServiceDescription("内部交易结算执行失败");
                //}
            }

        //} catch (Exception e) {
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_POrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_POrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_POrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_POrderUpdateReq req) throws Exception {
        String sql = ""
                + " select porderNO,Status,BDATE,PROCESS_ERP_NO "
                + " from DCP_porder "
                + " where OrganizationNO = ? "
                + " and EID = ? "
                + " and SHOPID = ? "
                + " and porderNO = ? ";
        return sql;
    }

    protected String GetOptionalTime(DCP_POrderUpdateReq req) throws Exception {
        String eId = req.geteId();
        levelElm request = req.getRequest();
        String ptemplateNO = request.getpTemplateNo();
        if (ptemplateNO != null && !ptemplateNO.isEmpty()) {
            String sql = "select PRE_DAY,OPTIONAL_TIME from DCP_ptemplate where ptemplateno='" + ptemplateNO + "' and EID='" + eId + "'";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                String prdate = getQData.get(0).get("PRE_DAY").toString();
                String prtime = getQData.get(0).get("OPTIONAL_TIME").toString();
                Calendar cal = Calendar.getInstance();//获得当前时间
                SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
                String submitTime = dfTime.format(cal.getTime());
                if (prdate == null || prdate.equals("")) {
                    prdate = "0";//默认当前日期
                }
                if (prtime == null || prtime.equals("")) {
                    prtime = "235959";
                }
                //过了当天的要货时间，最大能要货的时间需要+1
                if (Integer.parseInt(submitTime) > Integer.parseInt(prtime)) {
                    int irdate = Integer.parseInt(prdate) + 1;
                    prdate = String.valueOf(irdate);
                }
                //当天只能要多少天的多少货
                Calendar cal_cur = Calendar.getInstance();//获得当前时间
                cal_cur.add(Calendar.DAY_OF_YEAR, Integer.parseInt(prdate));
                SimpleDateFormat dfDate_cur = new SimpleDateFormat("yyyyMMdd");
                String sdate = dfDate_cur.format(cal_cur.getTime());
                return sdate;

            } else {
                return "";
            }
        } else {
            return "";
        }
    }

}