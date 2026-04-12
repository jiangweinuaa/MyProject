package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_POrderCreateReq;
import com.dsc.spos.json.cust.req.DCP_POrderCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_POrderCreateReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_POrderUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_POrderCreateRes;
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
 * POrderCreate 專用的 response json
 * 說明：要货单保存
 * 服务说明：要货单保存
 *
 * @author panjing
 * @since 2016-10-08
 */
public class DCP_POrderCreate extends SPosAdvanceService<DCP_POrderCreateReq, DCP_POrderCreateRes> {
    @Override
    protected boolean isVerifyFail(DCP_POrderCreateReq req) throws Exception {
        levelElm request = req.getRequest();
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        List<level1Elm> jsonDatas = request.getDatas();
        //必传值不为空
        String rDate = request.getrDate();
        String rTime = request.getrTime();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt = request.getTotDistriAmt();
        String totCqty = request.getTotCqty();

        String ptemplateNo = request.getpTemplateNo();
        if (Check.Null(ptemplateNo) && Check.Null(request.getReceiptOrgNo())) {
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
        } else {
            if (!PosPub.isNumeric(rDate)) {
                errMsg.append("需求日期格式错误:" + rDate + ", ");
                isFail = true;
            }
        }

        if (Check.Null(rTime)) {
            errMsg.append("需求时间不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (level1Elm par : jsonDatas) {
            String item = par.getItem();
            String pluNo = par.getPluNo();
            String punit = par.getPunit();
            String pqty = par.getPqty();
            String price = par.getPrice();
            String amt = par.getAmt();
            String baseUnit = par.getBaseUnit();
            String baseQty = par.getBaseQty();
            String unitRatio = par.getUnitRatio();

            if (Check.Null(item)) {
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pluNo)) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }
            if (Check.Null(punit)) {
                errMsg.append("商品" + pluNo + "要货单位不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pqty)) {
                errMsg.append("商品" + pluNo + "要货数量不可为空值, ");
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
    protected TypeToken<DCP_POrderCreateReq> getRequestType() {
        return new TypeToken<DCP_POrderCreateReq>() {
        };
    }

    @Override
    protected DCP_POrderCreateRes getResponseType() {
        return new DCP_POrderCreateRes();
    }

    @Override
    protected void processDUID(DCP_POrderCreateReq req, DCP_POrderCreateRes res) throws Exception {

        levelElm request = req.getRequest();
        String porderNO;
        String receiptOrg = "";
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String pTemplateNO = request.getpTemplateNo();
        String isAdd = request.getIsAdd();
        String memo = request.getMemo();
        String porderID = request.getPorderID();
        String status = "0";
        double PRedictAMT = request.getPRedictAMT();
        String BeginDate = request.getBeginDate();
        String EndDate = request.getEndDate();
        double avgsaleAMT = request.getAvgsaleAMT();
        double modifRatio = request.getModifRatio();
        String StrISUrgentOrder = request.getISUrgentOrder();

        if (Check.Null(StrISUrgentOrder)) {
            StrISUrgentOrder = "N";
        }
        //需求日期
        String rDate = request.getrDate();
        String rTime = request.getrTime();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt = request.getTotDistriAmt();
        String uTotDistriAmt = request.getuTotDistriAmt(); // 原进货金额合计
        String totCqty = request.getTotCqty();

        Calendar tempcalPre = Calendar.getInstance();//获得当前时间
        Calendar tempcalAdd = Calendar.getInstance();//获得当前时间

        SimpleDateFormat tempdfPre = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat tempdfAdd = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat tempdfAdd1 = new SimpleDateFormat("yyyyMMdd");

        if (!Check.Null(req.getRequest().getpTemplateNo())) {
            //【ID1026796】【霸王3.0】追加要货需要控制要货截止时间---服务端 by jinzma 20220627
            String isAppend = req.getRequest().getIsAppend();
            if (Check.Null(isAppend) || isAppend.equals("Y")) {
                String isAddSql = " select * from DCP_PORDER Where eId = '" + eId + "' and shopId = '" + shopId + "' "
                        + " and status = '2' and rDate = '" + rDate + "' and ptemplateNo ='" + pTemplateNO + "' ";
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
        DCP_InterSettleDataGenerateReq generateReq = new DCP_InterSettleDataGenerateReq();

        if (!checkGuid(req)) {
            if (isAdd.equals("Y")) {
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
            } else if (isAdd.equals("N") && (pTemplateNO == null || pTemplateNO.isEmpty())) {
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

            String rdate_type = "";
            String rdate_add = "";
            String rdate_values = "";
            String rdate_times = "";
            String rtimestype = "";
            String bccode = "";

            String sql = " SELECT RECEIPT_ORG,RDATE_TYPE,RDATE_ADD,RDATE_VALUES,REVOKE_DAY,REVOKE_TIME,RDATE_TIMES,RTIMESTYPE,BCCODE  FROM DCP_PTEMPLATE "
                    + " WHERE EID='" + eId + "' AND  PTEMPLATENO='" + pTemplateNO + "' AND DOC_TYPE='0' ";
            List<Map<String, Object>> getQDate = this.doQueryData(sql, null);
            if (getQDate != null && !getQDate.isEmpty()) {
                receiptOrg = getQDate.get(0).get("RECEIPT_ORG").toString();

                rdate_type = getQDate.get(0).get("RDATE_TYPE").toString();//0或空为不管控 1为按周 2为按月
                rdate_add = getQDate.get(0).get("RDATE_ADD").toString();//0或空为不管  当RDATE_TYPE为1时代表需要控制几周 当RDATE_TYPE为1时代表需要控制下月
                rdate_values = getQDate.get(0).get("RDATE_VALUES").toString();//当RDATE_TYPE为1时填入 1,2代表周一，周二等 当RDATE_TYPE为2时填入1,2代表1号，2号
                rdate_times = getQDate.get(0).get("RDATE_TIMES").toString();//0或空为不限制  1为限制1次
                rtimestype = getQDate.get(0).get("RTIMESTYPE").toString();//1按周期控制次数 2按需求日期天控制次数
                bccode = getQDate.get(0).get("BCCODE").toString();//模板班次号
            }
            if (Check.Null(pTemplateNO)) {
                receiptOrg = request.getReceiptOrgNo();
            }

            String createBy = req.getOpNO();
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String bDate = request.getbDate();
            if (Check.Null(bDate)) {
                bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
            }
            String createDate = df.format(cal.getTime());
            df = new SimpleDateFormat("HHmmss");
            String createTime = df.format(cal.getTime());

            //*******************RDATE_TYPE控制处理****************
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
                    String sTodayisWeek = mycommon.getWeekOfDate(mydf.parse(createDate));
                    int iTodayisWeek = Integer.parseInt(sTodayisWeek);//5

                    String sMustmaxDate;
                    String sMustminDate;

                    if (numWeek != 0) {
                        //那周的起始范围
                        int maxDays = (7 - iTodayisWeek) + numWeek * 7;
                        int minDays = maxDays - 6;
                        sMustmaxDate = PosPub.GetStringDate(createDate, maxDays);
                        sMustminDate = PosPub.GetStringDate(createDate, minDays);

                        if (mydf.parse(rDate).compareTo(mydf.parse(sMustminDate)) < 0
                                || mydf.parse(rDate).compareTo(mydf.parse(sMustmaxDate)) > 0) {
                            //直接抛异常
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此要货模板当前时间已经过了要货截止时间，需求日期必须间隔在" + numWeek + "周范围内！");
                        }
                    } else {
                        //0不管控
                        //本周的起始范围
                        int maxDays = (7 - iTodayisWeek) + 1;
                        int minDays = maxDays - 6;
                        //
                        sMustmaxDate = PosPub.GetStringDate(createDate, maxDays);
                        sMustminDate = PosPub.GetStringDate(createDate, minDays);
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
                            sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + req.geteId() + "' AND A.SHOPID='" + req.getShopId() + "' AND PTEMPLATENO='" + pTemplateNO + "' AND RDATE>='" + sMustminDate + "' AND RDATE<='" + sMustmaxDate + "' ";
                        } else {
                            sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + req.geteId() + "' AND A.SHOPID='" + req.getShopId() + "' AND PTEMPLATENO='" + pTemplateNO + "' AND RDATE='" + rDate + "' ";
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
                } else if (rdate_type.equals("2")) {  //按月
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
                    Calendar calMonth = Calendar.getInstance();//获得当前时间
                    if (numMonth != 0) {
                        calMonth.set(Calendar.MONTH, iTodayisMonth + numMonth);
                        int iDays = calMonth.getActualMaximum(Calendar.DATE);
                        calMonth.set(Calendar.DATE, iDays);
                        //那个月的起始日期
                        sMustmaxDate = mydf.format(calMonth.getTime());
                        calMonth.set(Calendar.DATE, 1);
                        sMustminDate = mydf.format(calMonth.getTime());
                        if (mydf.parse(rDate).compareTo(mydf.parse(sMustminDate)) < 0
                                || mydf.parse(rDate).compareTo(mydf.parse(sMustmaxDate)) > 0) {
                            //直接抛异常
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此要货模板当前时间已经过了要货截止时间，需求日期必须间隔在" + numMonth + "月范围内！");
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
                            sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + req.geteId() + "' AND A.SHOPID='" + req.getShopId() + "' AND PTEMPLATENO='" + pTemplateNO + "' AND RDATE>='" + sMustminDate + "' AND RDATE<='" + sMustmaxDate + "' ";
                        } else {
                            sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + req.geteId() + "' AND A.SHOPID='" + req.getShopId() + "' AND PTEMPLATENO='" + pTemplateNO + "' AND RDATE='" + rDate + "' ";
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

            porderNO = getPorderNO(req);

            generateReq.setRequest(generateReq.new Request());
            generateReq.setToken(req.getToken());
            generateReq.seteId(req.geteId());
            generateReq.setServiceId("DCP_InterSettleDataGenerate");
            generateReq.getRequest().setOrganizationNo(organizationNO);
            generateReq.getRequest().setBillNo(porderNO);
            generateReq.getRequest().setSupplyOrgNo(receiptOrg);
            generateReq.getRequest().setDetail(new ArrayList<>());
            generateReq.getRequest().setReturnSupplyPrice("Y");


            for (level1Elm par : request.getDatas()) {
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

                // ADD oType 新增 枚举值 6 要货购物车要货   当 oType ==6 时 创建要货单成功后 删除购物车对应商品
                if (oType.equals("6")) {
                    DelBean db1 = new DelBean("DCP_SHOPPINGCART");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("OPNO", new DataValue(createBy, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("PLUNO", new DataValue(par.getPluNo(), Types.VARCHAR));
                    db1.addCondition("FEATURENO", new DataValue(par.getFeatureNo(), Types.VARCHAR));
                    db1.addCondition("PUNIT", new DataValue(par.getPunit(), Types.VARCHAR));
                    db1.addCondition("BASEUNIT", new DataValue(par.getBaseUnit(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                }

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
                        if (Float.parseFloat(par.getPqty()) == 0) {
                            continue;
                        }
                        int insColCt = 0;
                        String[] columnsName = {
                                "porderNO", "SHOPID", "item", "pluNO", "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
                                "price", "amt", "EID", "organizationNO", "Detail_status",
                                "REF_WQTY", "REF_SQTY", "REF_PQTY", "SO_QTY", "MUL_QTY", "MIN_QTY", "MAX_QTY", "propQty", "MEMO"
                                , "KQTY", "KADJQTY", "PROPADJQTY", "DISTRIPRICE", "DISTRIAMT", "BDATE", "HEADSTOCKQTY", "FEATURENO", "UDISTRIPRICE",
                                "ISNEWGOODS", "ISHOTGOODS", "SUPPLIERTYPE", "SUPPLIERID"
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
                                    keyVal = par.getBaseUnit();     //baseUnit
                                    break;
                                case 7:
                                    keyVal = par.getBaseQty();  //baseqty
                                    break;
                                case 8:
                                    keyVal = par.getUnitRatio();   //unitRatio
                                    break;
                                case 9:
                                    keyVal = par.getPrice();    //price
                                    if (par.getPrice() == null || par.getPrice().isEmpty()) {
                                        keyVal = "0";
                                    }
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
                                    if (par.getRefWQty() == null || par.getRefWQty().isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 15:
                                    keyVal = par.getRefSQty();
                                    if (par.getRefSQty() == null || par.getRefSQty().isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 16:
                                    keyVal = par.getRefPQty();
                                    if (par.getRefPQty() == null || par.getRefPQty().isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 17:
                                    keyVal = par.getSoQty();
                                    if (par.getSoQty() == null || par.getSoQty().isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 18:
                                    keyVal = par.getMulQty();
                                    if (par.getMulQty() == null || par.getMulQty().isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 19:
                                    keyVal = par.getMinQty();
                                    if (par.getMinQty() == null || par.getMinQty().isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 20:
                                    keyVal = par.getMaxQty();
                                    if (par.getMaxQty() == null || par.getMaxQty().isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 21:
                                    keyVal = Double.toString(par.getPropQty());
                                    if (keyVal == null || keyVal.isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 22:
                                    keyVal = par.getMemo();
                                    if (par.getMemo() == null || par.getMemo().isEmpty()) {
                                        keyVal = "";
                                    }
                                    break;
                                case 23:
                                    keyVal = par.getkQty();
                                    if (keyVal == null || keyVal.isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 24:
                                    keyVal = par.getkAdjQty();
                                    if (keyVal == null || keyVal.isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 25:
                                    keyVal = par.getPropAdjQty();
                                    if (keyVal == null || keyVal.isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 26:
                                    keyVal = par.getDistriPrice();
                                    if (keyVal == null || keyVal.isEmpty()) {
                                        keyVal = "0";
                                    }
                                    break;
                                case 27:
                                    keyVal = par.getDistriAmt();    //amt
                                    if (Check.Null(keyVal)) keyVal = "0";
                                    break;
                                case 28:
                                    keyVal = bDate;
                                    break;
                                case 29:
                                    keyVal = par.getHeadStockQty();
                                    if (Check.Null(keyVal)) keyVal = "0";
                                    break;
                                case 30:
                                    keyVal = par.getFeatureNo();
                                    if (Check.Null(keyVal))
                                        keyVal = " ";
                                    break;
                                case 31:
                                    keyVal = par.getuDistriPrice();
                                    break;
                                case 32:
                                    keyVal = par.getIsNewGoods();
                                    if (Check.Null(keyVal)) {
                                        keyVal = "N";
                                    }
                                    break;
                                case 33:
                                    keyVal = par.getIsHotGoods();
                                    if (Check.Null(keyVal)) {
                                        keyVal = "N";
                                    }
                                    break;
                                case 34:
                                    keyVal = par.getSupplierType();
                                    break;
                                case 35:
                                    keyVal = par.getSupplierId();
                                    break;
                                default:
                                    break;
                            }

                            if (keyVal != null) {
                                insColCt++;
                                if (i == 2) {
                                    columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                } else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 14 || i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20 || i == 21) {
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
                                if (insColCt >= insValue2.length) break;
                            }
                        }
                        InsBean ib2 = new InsBean("DCP_PORDER_DETAIL", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));
                    }

                     */
            String[] columns1 = {
                    "SHOPID", "OrganizationNO", "EID", "porderNO", "BDate", "MEMO", "Status", "CreateBy",
                    "Create_Date", "Create_time", "TOT_PQTY", "TOT_AMT", "TOT_CQTY", "rDate", "rTime",
                    "Porder_ID", "PTEMPLATENO", "IS_ADD",
                    "PRedictAMT", "BeginDate", "EndDate", "avgsaleAMT", "modifRatio", "ISUrgentOrder",
                    "ISPREDICT", "RECEIPT_ORG", "TOT_DISTRIAMT", "ISFORECAST", "OFNO", "OTYPE", "UTOTDISTRIAMT",
                    "CREATE_CHATUSERID", "BCCODE", "UPDATE_TIME", "TRAN_TIME"
                    , "SUPPLIERTYPE", "EMPLOYEEID", "DEPARTID", "OWNOPID", "OWNDEPTID", "CREATEDEPTID"
            };
            DataValue[] insValue1 = new DataValue[]{
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(organizationNO, Types.VARCHAR),
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(porderNO, Types.VARCHAR),
                    new DataValue(bDate, Types.VARCHAR),
                    new DataValue(memo, Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(createBy, Types.VARCHAR),
                    new DataValue(createDate, Types.VARCHAR),
                    new DataValue(createTime, Types.VARCHAR),
                    new DataValue(totPqty, Types.VARCHAR),
                    new DataValue(totAmt, Types.VARCHAR),
                    new DataValue(totCqty, Types.VARCHAR),
                    new DataValue(rDate, Types.VARCHAR),
                    new DataValue(rTime, Types.VARCHAR),
                    new DataValue(porderID, Types.VARCHAR),
                    new DataValue(pTemplateNO, Types.VARCHAR),
                    new DataValue(isAdd, Types.VARCHAR),
                    new DataValue(PRedictAMT, Types.DOUBLE),
                    new DataValue(BeginDate, Types.VARCHAR),
                    new DataValue(EndDate, Types.VARCHAR),
                    new DataValue(avgsaleAMT, Types.DOUBLE),
                    new DataValue(modifRatio, Types.DOUBLE),
                    new DataValue(StrISUrgentOrder, Types.VARCHAR),
                    new DataValue(StrISPRedict, Types.VARCHAR),
                    new DataValue(receiptOrg, Types.VARCHAR),
                    new DataValue(totDistriAmt, Types.VARCHAR),
                    new DataValue(isForecast, Types.VARCHAR),
                    new DataValue(ofNo, Types.VARCHAR),
                    new DataValue(oType, Types.VARCHAR),
                    new DataValue(uTotDistriAmt, Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                    new DataValue(bccode, Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    new DataValue(req.getRequest().getSupplierType(), Types.VARCHAR),
                    new DataValue(req.getRequest().getEmployeeID(), Types.VARCHAR),
                    new DataValue(req.getRequest().getDepartID(), Types.VARCHAR),
                    new DataValue(req.getOpNO(), Types.VARCHAR),
                    new DataValue(req.getDepartmentNo(), Types.VARCHAR),
                    new DataValue(req.getDepartmentNo(), Types.VARCHAR),
            };

            InsBean ib1 = new InsBean("DCP_PORDER", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                    /*
                    // ADD oType 新增 枚举值 6 要货购物车要货   当 oType ==6 时 创建要货单成功后 删除购物车对应商品
                    if (oType.equals("6")) {
                        for (level1Elm jsonData : req.getRequest().getDatas()) {
                            DelBean db1 = new DelBean("DCP_SHOPPINGCART");
                            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            db1.addCondition("OPNO", new DataValue(createBy, Types.VARCHAR));
                            db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            db1.addCondition("PLUNO", new DataValue(jsonData.getPluNo(), Types.VARCHAR));
                            db1.addCondition("FEATURENO", new DataValue(jsonData.getFeatureNo(), Types.VARCHAR));
                            db1.addCondition("PUNIT", new DataValue(jsonData.getPunit(), Types.VARCHAR));
                            db1.addCondition("BASEUNIT", new DataValue(jsonData.getBaseUnit(), Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(db1));
                        }
                    }

                     */

        } else {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
        }

        this.doExecuteDataToDB();

        res.setDatas(new ArrayList<>());
        DCP_POrderCreateRes.level1Elm oneLv1 = res.new level1Elm();
        oneLv1.setPorderNo(porderNO);
        res.getDatas().add(oneLv1);

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

                        List<DCP_POrderCreateReq.level1Elm> collect = request.getDatas().stream().filter(x -> x.getItem().equals(item)).collect(Collectors.toList());
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
             //   res.setServiceDescription("内部交易结算执行失败");
           // }
        }

    }

    protected String GetOptionalTime(DCP_POrderCreateReq req) throws Exception {
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

    @Override
    protected List<InsBean> prepareInsertData(DCP_POrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_POrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_POrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_POrderCreateReq req) throws Exception {
        return null;
    }

    private String getPorderNO(DCP_POrderCreateReq req) throws Exception {

        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：YHSQ
         */

//        StringBuffer sqlbuf = new StringBuffer();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String porderNO = PosPub.getBillNo(dao, eId, shopId, organizationNO + "-YHSQ");
        //billNo = getProcessTaskNO(eId,"001", req.getRequest().getOrgNo()+"-JYDX"); DCP_SupLicenseApplyCreateReq
//        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
//        String[] conditionValues = { organizationNO, eId, shopId }; // 查询要货单号
//        sqlbuf.append(""
//                + "select PorderNO  from ( "
//                + "select max(PorderNO) as  PorderNO "
//                + "  from DCP_Porder " + " where OrganizationNO = ? " + " and EID = ? " + " and SHOPID = ? "
//                + " and PorderNO like '%%" + bDate + "%%' "); // 假資料
//        sqlbuf.append(" ) TBL ");
//        
//        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), conditionValues);
//        
//        if (getQData != null && !getQData.isEmpty()) {
//            porderNO = (String) getQData.get(0).get("PORDERNO");
//            if (porderNO != null && porderNO.length() > 0) {
//                long i;
//                porderNO = porderNO.substring(4);
//                i = Long.parseLong(porderNO) + 1;
//                porderNO = i + "";
//                porderNO = "YHSQ" + porderNO;
//            } else {
//                porderNO = "YHSQ" + bDate + "00001";
//            }
//        } else {
//            porderNO = "YHSQ" + bDate + "00001";
//        }

        return porderNO;
    }

    private boolean checkGuid(DCP_POrderCreateReq req) throws Exception {
        levelElm request = req.getRequest();
        String guid = request.getPorderID();
        boolean existGuid = false;
        String sql = ""
                + " select PORDER_ID "
                + " from DCP_Porder "
                + " where PORDER_ID = '" + guid + "' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && !getQData.isEmpty()) {
            existGuid = true;
        }
        return existGuid;
    }
}
