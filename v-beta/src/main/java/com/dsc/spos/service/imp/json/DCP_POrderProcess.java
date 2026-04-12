package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_POrderProcessReq;
import com.dsc.spos.json.cust.req.DCP_POrderProcessReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_POrderProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * 服務函數：POrderProcess
 * 說明：要货单处理
 * 服务说明：要货单处理
 *
 * @author panjing
 * @since 2016-10-08
 */
public class DCP_POrderProcess extends SPosAdvanceService<DCP_POrderProcessReq, DCP_POrderProcessRes> {
    @Override
    protected boolean isVerifyFail(DCP_POrderProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        String porderNO = request.getPorderNo();
        String status = request.getStatus();
        
        if (Check.Null(porderNO)) {
            errMsg.append("单据编号不可为空值, ");
            isFail = true;
        }
        
        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_POrderProcessReq> getRequestType() {
        return new TypeToken<DCP_POrderProcessReq>() {};
    }
    
    @Override
    protected DCP_POrderProcessRes getResponseType() {
        return new DCP_POrderProcessRes();
    }
    
    @Override
    protected void processDUID(DCP_POrderProcessReq req, DCP_POrderProcessRes res) throws Exception {
        Logger logger = LogManager.getLogger(DCP_POrderProcess.class.getName());
        String eId = req.geteId();
        String shopId = req.getShopId();
        levelElm request = req.getRequest();
        //查询一下要货单
        String porderheadsql = getQuerySql(req);
        String isAdd = "N";
        String pTemplateNo = "";
        String receiptOrg = "";
        String rDate = "";
        String StrISUrgentOrder = request.getISUrgentOrder() == null ? "N" : request.getISUrgentOrder();
        String erpno = "";
        
        try {
            List<Map<String, Object>> pOrderList = this.doQueryData(porderheadsql, null);
            if (pOrderList != null && !pOrderList.isEmpty()) {
                isAdd = pOrderList.get(0).get("IS_ADD").toString();
                pTemplateNo = pOrderList.get(0).get("PTEMPLATENO").toString();
                rDate = pOrderList.get(0).get("RDATE").toString();
                receiptOrg = pOrderList.get(0).get("RECEIPT_ORG").toString();
                erpno = pOrderList.get(0).get("PROCESS_ERP_NO").toString();
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "要货单不存在或要货单单身为空");
            }
            
            //【ID1018414】【3.0货郎】要货单提交服务关联商品模板检核商品是否可要货，存在不可要货商品，报错提示
            /*String companyId = req.getBELFIRM();
            if (Check.Null(companyId)) {
                String getCompanySql=" select belfirm from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getShopId()+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(getCompanySql, null);
                companyId = getQData.get(0).get("BELFIRM").toString();
                
            }*/
            
            
            /* + " with goodstemplate as ("
                    + " select b.* from ("
                    + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                    + " from dcp_goodstemplate a"
                    + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                    + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+req.getShopId()+"'"
                    + " where a.eid='"+req.geteId()+"' and a.status='100'"
                    + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                    + " ) a"
                    + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                    + " where a.rn=1 and b.canrequire='Y' )"*/
            
            
            //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
            // 商品模板表
            //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
            /*" with goodstemplate as ("
                  + " select b.* from dcp_goodstemplate a"
                  + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                  + " where a.eid='"+req.geteId()+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+req.geteId()+"','"+req.getShopId()+"') and b.canrequire='Y'  "
                  + " )"
                  + " "
                  + " select a.pluno from dcp_porder_detail a"
                  + " left join goodstemplate on a.pluno=goodstemplate.pluno"
                  + " where a.eid='"+req.geteId()+"' and a.shopid='"+req.getShopId()+"' and a.porderno='"+req.getRequest().getPorderNo()+"' "
                  + " and goodstemplate.pluno is null"
                  + " ";*/
            
            //sql 优化  by jinzma 20231023
            String canrequireSql = " select a.pluno from dcp_porder_detail a"
                    + " left join dcp_goodstemplate_goods b on a.eid=b.eid and b.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')"
                    + " and a.pluno=b.pluno and b.status='100' and b.canrequire='Y'"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' "
                    + " and a.porderno='"+req.getRequest().getPorderNo()+"' and b.pluno is null";
            List<Map<String, Object>> canrequirelist = this.doQueryData(canrequireSql, null);
            if (canrequirelist != null && !canrequirelist.isEmpty()) {
                StringBuffer canrequirePlus = new StringBuffer();
                for (Map<String, Object> canrequire : canrequirelist){
                    canrequirePlus.append("商品编号："+canrequire.get("PLUNO").toString()+"不可要货,");
                }
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, canrequirePlus.toString());
            }
            
            //【ID1036519】【霸王3.3.0.6】客户想要根据要货模板管控当天商品的要货最大数量--服务端  by jinzma 20231023
            String POrder_Temp_Check = PosPub.getPARA_SMS(dao,eId,shopId,"POrder_Temp_Check");
            if ("Y".equals(POrder_Temp_Check) && !Check.Null(pTemplateNo)){
                String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                //查要货模板商品最大要货数量
                String sql = " select a.pluno,a.max_qty*b.unitratio as max_qty from dcp_ptemplate_detail a"
                        + " inner join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.punit=b.ounit"
                        + " where a.eid='"+eId+"' and a.doc_type='0' and a.ptemplateno='"+pTemplateNo+"' ";
                List<Map<String, Object>>getPTemplateMaxQty = this.doQueryData(sql, null);
                //查已确定提交的要货单单身
                sql = " select b.pluno,sum(b.baseqty) as baseqty from dcp_porder a"
                        + " inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno"
                        + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status>0 and a.status<>'5' "
                        + " and a.ptemplateno='"+pTemplateNo+"' and a.confirm_date='"+sDate+"'"
                        + " group by b.pluno";
                List<Map<String, Object>>getPOrderQty = this.doQueryData(sql, null);
                
                for (Map<String, Object> oneData:pOrderList){
                    String pluNo = oneData.get("PLUNO").toString();      //要货单商品编号
                    //【ID1037080】【霸王3.3.1.0】门店管理，移动门店，要货模板限制要货数量文案优化 by jinzma 20231106
                    String pluName = oneData.get("PLU_NAME").toString();
                    BigDecimal baseQty_b = new BigDecimal(oneData.get("BASEQTY").toString());  //要货单基准单位数量
                    
                    //查要货商品模板最大要货数
                    List<Map<String, Object>> pTemplateMaxQty = getPTemplateMaxQty.stream().filter(
                            s -> s.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(pTemplateMaxQty)) {
                        BigDecimal maxQty_b = new BigDecimal(pTemplateMaxQty.get(0).get("MAX_QTY").toString());
                        //查已要货数量
                        BigDecimal pOrderQty_b = new BigDecimal("0");
                        List<Map<String, Object>> pOrderQty = getPOrderQty.stream().filter(
                                s -> s.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(pOrderQty)) {
                            pOrderQty_b = new BigDecimal(pOrderQty.get(0).get("BASEQTY").toString());
                        }
                        
                        //判断本次要货和已完成的要货数量是否大于模板最大要货数
                        if (baseQty_b.add(pOrderQty_b).compareTo(maxQty_b) > 0){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品:"+pluName +"不可要货, 本次要货数量:"+baseQty_b+" 当天已要货数量:"+pOrderQty_b+" 超过模板最大要货数"+maxQty_b);
                        }
                    }
                }
            }
            
            
            
            Calendar tempcalPre = Calendar.getInstance();//获得当前时间
            SimpleDateFormat tempdfPre = new SimpleDateFormat("yyyyMMdd");
            Calendar tempcalAdd = Calendar.getInstance();//获得当前时间
            SimpleDateFormat tempdfAdd = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat tempdfAdd1 = new SimpleDateFormat("yyyyMMdd");
            
            //要货默认需求日期   Pre_Demand_Days  参数里已经没有了
            String preDemandDate = "";
            tempcalPre.add(Calendar.DATE, 0);
            preDemandDate = tempdfPre.format(tempcalPre.getTime());
            
            //追加要货默认需求日期
            String addDemandDate = "";
            int addDemandDates;
            addDemandDate = PosPub.getPARA_SMS(dao, eId, shopId, "Add_Demand_Days");
            
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
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此要货模板当前时间已经过了要货截止时间，需求日期不能小于当天");
            }
            
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
            } else if (isAdd.equals("N") && (pTemplateNo == null || pTemplateNo.isEmpty())) {
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
                String optionalTime = this.GetOptionalTime(req, pTemplateNo, rDate);
                SimpleDateFormat dfDate_cur = new SimpleDateFormat("yyyyMMdd");
                String sallcurtime = rDate;
                //需求日期+现在的时间
                //rDate = optionalTime;
                Date d1 = dfDate_cur.parse(sallcurtime);
                Date d2 = dfDate_cur.parse(optionalTime);
                
                if (d1.compareTo(d2) < 0) {
                    if (!StrISUrgentOrder.equals("Y")) {
                        String tempDate = tempdfAdd.format(d2);
                        String tempStr = "";
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
            
            //【ID1018368】【3.0货郎】商品列表页/详情页/购物车页/要货单页显示总部库存 与红艳讨论这段管控拿掉 by jinzma 20210610
            //String isShowHeadStockQty = "N";  //是否显示总部库存 BY JZMA 20200423
            
            //*******************RDATE_TYPE控制处理****************
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat rdTypedf = new SimpleDateFormat("yyyyMMdd");
            String cursysDate = rdTypedf.format(cal.getTime());
            
            //撤销后的单据此字段有值，无需管控
            if (erpno == null || erpno.isEmpty()) {
                if (pTemplateNo != null && !pTemplateNo.isEmpty()) {
                    String rdate_type = "";
                    String rdate_add = "";
                    String rdate_values = "";
                    String rdate_times = "";
                    String rtimestype = "";
                    
                    String sqlTemp = " SELECT RECEIPT_ORG,RDATE_TYPE,RDATE_ADD,RDATE_VALUES,REVOKE_DAY,REVOKE_TIME,RDATE_TIMES,"
                            + " RTIMESTYPE,ISSHOWHEADSTOCKQTY  FROM DCP_PTEMPLATE "
                            + " WHERE EID='" + eId + "' AND  PTEMPLATENO='" + pTemplateNo + "' AND DOC_TYPE='0' ";
                    List<Map<String, Object>> getQDate = this.doQueryData(sqlTemp, null);
                    if (getQDate != null && !getQDate.isEmpty()) {
                        receiptOrg = getQDate.get(0).get("RECEIPT_ORG").toString();
                        //isShowHeadStockQty = getQDate.get(0).get("ISSHOWHEADSTOCKQTY").toString();
                        rdate_type = getQDate.get(0).get("RDATE_TYPE").toString();//0或空为不管控 1为按周 2为按月
                        rdate_add = getQDate.get(0).get("RDATE_ADD").toString();//0或空为不管  当RDATE_TYPE为1时代表需要控制几周 当RDATE_TYPE为1时代表需要控制下月
                        rdate_values = getQDate.get(0).get("RDATE_VALUES").toString();//当RDATE_TYPE为1时填入 1,2代表周一，周二等 当RDATE_TYPE为2时填入1,2代表1号，2号
                        rdate_times = getQDate.get(0).get("RDATE_TIMES").toString();//0或空为不限制  1为限制1次
                        rtimestype = getQDate.get(0).get("RTIMESTYPE").toString();//1按周期控制次数 2按需求日期天控制次数
                    }
                    
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
                        
                        String sMustmaxDate = "";
                        String sMustminDate = "";
                        
                        if (numWeek != 0) {
                            //那周的起始范围
                            int maxDays = (7 - iTodayisWeek) + numWeek * 7;
                            int minDays = maxDays - 6;
                            
                            sMustmaxDate = PosPub.GetStringDate(cursysDate, maxDays);
                            sMustminDate = PosPub.GetStringDate(cursysDate, minDays);
                            
                            if (mydf.parse(rDate).compareTo(mydf.parse(sMustminDate)) < 0 || mydf.parse(rDate).compareTo(mydf.parse(sMustmaxDate)) > 0) {
                                //直接抛异常
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此要货模板当前时间已经过了要货截止时间，需求日期必须间隔在 " + numWeek + "周范围内！");
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
                            String sqlRdateTimes = "";
                            if (rtimestype.isEmpty() || rtimestype.equals("1")) {
                                sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND PTEMPLATENO='" + pTemplateNo + "' AND RDATE>='" + sMustminDate + "' AND RDATE<='" + sMustmaxDate + "' and porderno<>'" + request.getPorderNo() + "' ";
                            } else {
                                sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND PTEMPLATENO='" + pTemplateNo + "' AND RDATE='" + rDate + "' and porderno<>'" + request.getPorderNo() + "' ";
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
                        
                        mycommon = null;
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
                        
                        String sMustmaxDate = "";
                        String sMustminDate = "";
                        
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
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此要货模板当前时间已经过了要货截止时间，需求日期必须间隔在 " + numMonth + "月范围内！");
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
                            String sqlRdateTimes = "";
                            if (rtimestype.isEmpty() || rtimestype.equals("1")) {
                                sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND PTEMPLATENO='" + pTemplateNo + "' AND RDATE>='" + sMustminDate + "' AND RDATE<='" + sMustmaxDate + "' and porderno<>'" + request.getPorderNo() + "' ";
                            } else {
                                sqlRdateTimes = "SELECT COUNT(*) NUM FROM DCP_PORDER A WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND PTEMPLATENO='" + pTemplateNo + "' AND RDATE='" + rDate + "' and porderno<>'" + request.getPorderNo() + "' ";
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
            
            //【ID1018368】【3.0货郎】商品列表页/详情页/购物车页/要货单页显示总部库存 与红艳讨论这段管控拿掉 by jinzma 20210610
//            else {
//                if (pTemplateNo != null && pTemplateNo.equals("") == false) {
//                    String sqlTemp = " SELECT RECEIPT_ORG,RDATE_TYPE,RDATE_ADD,RDATE_VALUES,REVOKE_DAY,REVOKE_TIME,RDATE_TIMES,RTIMESTYPE,ISSHOWHEADSTOCKQTY  FROM DCP_PTEMPLATE "
//                            + " WHERE EID='" + eId + "' AND  PTEMPLATENO='" + pTemplateNo + "' AND DOC_TYPE='0' ";
//                    List<Map<String, Object>> getQDate = this.doQueryData(sqlTemp, null);
//                    if (getQDate != null && getQDate.isEmpty() == false) {
//                        isShowHeadStockQty = getQDate.get(0).get("ISSHOWHEADSTOCKQTY").toString();
//                    }
//                }
//            }
            
            //发货组织为门店类型的配送组织(卫星店)，PROCESS_STATUS=="Y"
            
            String processStatus = "N";
            if (!Check.Null(receiptOrg)) {
                //【ID1017676】【3.0】要货模板查询服务排除当前请求机构，本机构不能向本机构自己要货/要货单提交校验不能向本机构自己要货 by jinzma 20210517
                if (receiptOrg.equals(shopId)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "要货门店和发货机构一致,不能要货 ");
                }
                
                //查询模板对应的发货组织编号
                String sql = " select * from DCP_ORG "
                        + " where EID='" + eId + "'  AND ORGANIZATIONNO='" + receiptOrg + "' AND org_form='2' and status='100' AND ISDISTBR='Y' ";
                List<Map<String, Object>> getQDate = this.doQueryData(sql, null);
                if (getQDate != null && !getQDate.isEmpty()) {
                    processStatus = "Y";
                }
            }
            
            //【ID1018368】【3.0货郎】商品列表页/详情页/购物车页/要货单页显示总部库存 与红艳讨论这段管控拿掉 by jinzma 20210610
            //管控要货是否检核总部库存 BY JZMA 20200423
//            if (!Check.Null(isShowHeadStockQty) && isShowHeadStockQty.equals("Y")) {
//                MyCommon myComm = new MyCommon();
//                String sql = " select pluno,baseqty,FEATURENO,N'' as BATCHNO from DCP_porder_detail "
//                        + " where EID='" + eId + "' and SHOPID='" + shopId + "' and PORDERNO='" + request.getPorderNo() + "' ";
//                List<Map<String, Object>> goods = this.doQueryData(sql, null);
//                List<Map<String, Object>> stocks = myComm.getErpStock(eId, receiptOrg, goods);
//                if (stocks == null || stocks.isEmpty())
//                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "总部库存查询失败，不能要货 ");
//
//                for (Map<String, Object> oneData : goods) {
//                    String goodsPluNo = oneData.get("PLUNO").toString();
//                    String goodsFeatureNo = oneData.get("FEATURENO").toString();
//                    if (Check.Null(goodsFeatureNo)) //特征码为空给空格
//                        goodsFeatureNo = " ";
//                    String goodsBaseQty = oneData.get("BASEQTY").toString();
//                    BigDecimal goodsBaseQtyB = new BigDecimal(goodsBaseQty);
//                    boolean isExist = false;
//
//                    //ERP返回的库存数量合并计算（根据商品编号合并）
//                    BigDecimal stockBaseQtyB = new BigDecimal("0");
//                    for (Map<String, Object> oneStock : stocks) {
//                        String stockFeatureNo = oneStock.get("FEATURENO").toString();
//                        if (Check.Null(stockFeatureNo))
//                            stockFeatureNo = " ";    //特征码为空给空格
//                        if (goodsPluNo.equals(oneStock.get("PLUNO").toString()) && goodsFeatureNo.equals(stockFeatureNo)) {
//                            isExist = true;
//                            String stockBaseQty = oneStock.get("BASEQTY").toString();
//                            if (Check.Null(stockBaseQty) || !PosPub.isNumericType(stockBaseQty))
//                                stockBaseQty = "0";
//                            stockBaseQtyB = stockBaseQtyB.add(new BigDecimal(stockBaseQty));
//                        }
//                    }
//                    if (!isExist)
//                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品：" + goodsPluNo + "总部库存未查询到，不能要货 ");
//
//                    if (goodsBaseQtyB.compareTo(stockBaseQtyB) > 0) {
//                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品：" + goodsPluNo + "总部库存不足，不能要货 ");
//                    }
//                }
//            }
            
            // 新增 erp额度扣减 requisition.credit.deduct By wangzyc 2021/1/26
            // 若erp扣减服务执行成功则执行要货单确认，若erp扣减失败，则服务返回“ERP额度扣减失败，请重新提交”；
            
            // 判断是否开启信用额度 若开启则执行erp额度扣减，否则不执行；
            
            List<Map<String, Object>> maps = this.doQueryData(isEnableCredit(req), null);
            Map<String, Object> stringObjectMap = maps.get(0);
            String enableCredit = stringObjectMap.get("ENABLECREDIT").toString();
            SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
            
            String confirmBy = req.getOpNO();
            String confirmDate = dfDate.format(cal.getTime());
            String confirmTime = dfTime.format(cal.getTime());
            
            String submitBy = req.getOpNO();
            String submitDate = dfDate.format(cal.getTime());
            String submitTime = dfTime.format(cal.getTime());
            
            String accountBy = req.getOpNO();
            String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
            String accountTime = dfTime.format(cal.getTime());
            
            if (enableCredit.equals("Y")) {
                JSONObject payload = new JSONObject();
                JSONObject std_data = new JSONObject();
                JSONObject parameter = new JSONObject();
                
                JSONObject header = new JSONObject();
                
                // 取出单头 实际只有一条数据
                List<Map<String, Object>> pOrders = this.doQueryData(getPOrder(req), null);
                Map<String, Object> pOrder = pOrders.get(0);
                
                // 给单头赋值
                header.put("requisition_date", pOrder.get("RDATE").toString()); // 需求日期
                header.put("create_date", pOrder.get("BDATE").toString()); // 单据日期
                header.put("po_template_no", pOrder.get("PTEMPLATENO").toString()); // 要货模板编号
                header.put("is_add_po", pOrder.get("IS_ADD").toString()); // 追加要货标识
                header.put("is_urgent_order", request.getISUrgentOrder()); // 紧急要货标识
                header.put("front_no", request.getPorderNo()); // 前端单号
                header.put("remark", pOrder.get("MEMO").toString()); // 备注
                header.put("status", request.getStatus()); // 状态
                header.put("creator", pOrder.get("CREATEBY").toString()); // 建立者
                header.put("create_datetime", pOrder.get("CREATE_DATE").toString()); // 建立日期
                header.put("modify_no", pOrder.get("MODIFYBY").toString()); // 修改人
                header.put("modify_datetime", pOrder.get("MODIFY_DATE").toString()); // 修改日期
                header.put("approve_no", req.getOpNO()); // 审核人
                header.put("approve_datetime", dfDate.format(cal.getTime())); // 审核日期
                header.put("site_no", shopId); // 门店
                header.put("OFNO", pOrder.get("OFNO").toString()); // 来源单号
                header.put("load_shop", pOrder.get("LOAD_SHOP").toString()); // 来源门店
                header.put("oType", pOrder.get("OTYPE").toString()); // 单据类型
                header.put("version", "3.0");
                
                String totDistriamt = pOrder.get("TOT_DISTRIAMT").toString();
                
                // 给单身赋值
                List<Map<String, Object>> orderDetails = this.doQueryData(getPOrderDetail(req), null);
                List<Map<String, Object>> requisition_detailList = new ArrayList<>();
                
                Integer seq = 1;
                for (Map<String, Object> orderDetail : orderDetails) {
                    Map<String, Object> requisition_detail = new HashMap<>();
                    requisition_detail.put("seq", seq); // 项次
                    requisition_detail.put("item_no", orderDetail.get("PLUNO").toString()); // 商品编号
                    requisition_detail.put("base_unit", orderDetail.get("BASEUNIT").toString()); // 基本单位
                    requisition_detail.put("packing_unit", orderDetail.get("PUNIT").toString()); // 包装单位
                    requisition_detail.put("base_qty", orderDetail.get("BASEQTY").toString()); // 基本单位数量
                    requisition_detail.put("so_qty", orderDetail.get("SO_QTY").toString()); // 销售订单数
                    requisition_detail.put("packing_qty", orderDetail.get("PQTY").toString()); // 包装数量
                    requisition_detail.put("price", orderDetail.get("PRICE").toString()); // 单价
                    requisition_detail.put("amount", orderDetail.get("AMT").toString()); // 金额
                    requisition_detail.put("distri_price", orderDetail.get("DISTRIPRICE").toString()); // 进货单价
                    requisition_detail.put("distri_amount", orderDetail.get("DISTRIAMT").toString()); // 进货金额
                    requisition_detail.put("warehouse_no", orderDetail.get("WAREHOUSE").toString()); // 仓库代号
                    requisition_detail.put("remark", orderDetail.get("MEMO").toString()); // 备注
                    requisition_detail.put("feature_no", orderDetail.get("FEATURENO").toString().trim()); // 商品特征码
                    requisition_detailList.add(requisition_detail);
                    seq++;
                }
                
                header.put("requisition_detail", requisition_detailList);
                parameter.put("requisition", header);
                std_data.put("parameter", parameter);
                payload.put("std_data", std_data);
                
                String str = payload.toString();// 将json对象转换为字符串
                
                logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******额度扣减服务requisition.credit.deduct请求参数：  " + "\r\n" + "门店=" + shopId + "\r\n" + str + "******\r\n");
                String resbody = HttpSend.Send(str, "requisition.credit.deduct", eId, shopId, shopId, "门店=" + shopId);
                logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******额度扣减服务requisition.credit.deduct返回参数：  " + "\r\n" + "门店=" + shopId + "\r\n" + resbody + "******\r\n");
                
                if (!Check.Null(resbody)) {
                    JSONObject jsonres = new JSONObject(resbody);
                    JSONObject std_data_res = jsonres.getJSONObject("std_data");
                    JSONObject execution_res = std_data_res.getJSONObject("execution");
                    String code = execution_res.getString("code");
                    
                    //【ID1024783】【霸王3.0】要货单信用额度提交，报错提示问题 by jinzma 20220328
                    String description = execution_res.optString("description");
                    
                    //【ID1023434】【霸王3.0】门店要货增加配送费功能
                    JSONObject parameter_res = std_data_res.optJSONObject("parameter");
                    String distributionFee = "";      //配送费用
                    String remaining_amount = "";     //剩余额度
                    if (parameter_res!=null) {
                        distributionFee = parameter_res.optString("distributionFee");      //配送费用
                        remaining_amount = parameter_res.optString("remaining_amount");    //剩余额度
                    }
                    
                    
                    if (code.equals("0")) {
                        //返回成功写数据库才做判断，避免放上面误以为配送费为零元  by jinzma 20220120
                        if (!PosPub.isNumericType(distributionFee)) {
                            distributionFee = "0";
                        }
                        
                        ///提交修改要货单单头
                        String porderNO = request.getPorderNo();
                        String status = request.getStatus();
                        if (status.equals("2")) {
                            UptBean ub1 = new UptBean("DCP_PORDER");
                            ub1.addUpdateValue("Status", new DataValue(status, Types.VARCHAR));
                            ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                            ub1.addUpdateValue("ConfirmBy", new DataValue(confirmBy, Types.VARCHAR));
                            ub1.addUpdateValue("Confirm_Date", new DataValue(confirmDate, Types.VARCHAR));
                            ub1.addUpdateValue("Confirm_Time", new DataValue(confirmTime, Types.VARCHAR));
                            ub1.addUpdateValue("submitBy", new DataValue(submitBy, Types.VARCHAR));
                            ub1.addUpdateValue("submit_Date", new DataValue(submitDate, Types.VARCHAR));
                            ub1.addUpdateValue("submit_Time", new DataValue(submitTime, Types.VARCHAR));
                            ub1.addUpdateValue("ACCOUNTBY", new DataValue(accountBy, Types.VARCHAR));
                            ub1.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.VARCHAR));
                            ub1.addUpdateValue("ACCOUNT_TIME", new DataValue(accountTime, Types.VARCHAR));
                            ub1.addUpdateValue("ISUrgentOrder", new DataValue(StrISUrgentOrder, Types.VARCHAR));
                            ub1.addUpdateValue("PROCESS_STATUS", new DataValue(processStatus, Types.VARCHAR));
                            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ub1.addUpdateValue("DISTRIBUTIONFEE", new DataValue(distributionFee, Types.VARCHAR));  //配送费用 by jinzma 20220120
                            
                            // condition
                            ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("PORDERNO", new DataValue(porderNO, Types.VARCHAR));
                            
                            this.addProcessData(new DataProcessBean(ub1));
                            
                        }
                        this.doExecuteDataToDB();
                        
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");
                        
                    } else {
                        if (PosPub.isNumericType(remaining_amount) && PosPub.isNumericType(distributionFee)) {
                            description = "ERP额度扣减失败,当前额度:" + remaining_amount + "元,额度不足,要货总额:" + totDistriamt + "元,配送费用:" + distributionFee + "元 ";
                        }else{
                            //【ID1024783】【霸王3.0】要货单信用额度提交，报错提示问题
                            if (Check.Null(description)){
                                description = "ERP额度扣减失败，请重新提交";
                            }else{
                                description = "ERP额度扣减失败,错误讯息:"+description+",请联系ERP处理";
                            }
                        }
                        
                        res.setSuccess(false);
                        res.setServiceStatus("200");
                        //res.setServiceDescription("ERP额度扣减失败，请重新提交");
                        res.setServiceDescription(description);
                    }
                }else{
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("ERP额度扣减失败，请重新提交");
                }
                
            } else {
                ///提交修改要货单单头
                UptBean ub1 = null;
                String porderNO = request.getPorderNo();
                String status = request.getStatus();
                if (status.equals("2")) {
                    
                    ub1 = new UptBean("DCP_PORDER");
                    ub1.addUpdateValue("Status", new DataValue(status, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                    ub1.addUpdateValue("ConfirmBy", new DataValue(confirmBy, Types.VARCHAR));
                    ub1.addUpdateValue("Confirm_Date", new DataValue(confirmDate, Types.VARCHAR));
                    ub1.addUpdateValue("Confirm_Time", new DataValue(confirmTime, Types.VARCHAR));
                    ub1.addUpdateValue("submitBy", new DataValue(submitBy, Types.VARCHAR));
                    ub1.addUpdateValue("submit_Date", new DataValue(submitDate, Types.VARCHAR));
                    ub1.addUpdateValue("submit_Time", new DataValue(submitTime, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNTBY", new DataValue(accountBy, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.VARCHAR));
                    ub1.addUpdateValue("ACCOUNT_TIME", new DataValue(accountTime, Types.VARCHAR));
                    ub1.addUpdateValue("ISUrgentOrder", new DataValue(StrISUrgentOrder, Types.VARCHAR));
                    ub1.addUpdateValue("PROCESS_STATUS", new DataValue(processStatus, Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    // condition
                    ub1.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("PORDERNO", new DataValue(porderNO, Types.VARCHAR));
                    
                    this.addProcessData(new DataProcessBean(ub1));
                }
                this.doExecuteDataToDB();
                
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            }
            
            
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
        
    }
    
    protected String GetOptionalTime(DCP_POrderProcessReq req, String ptemplateNO, String rdate) throws Exception {
        String sql = null;
        String eId = req.geteId();
        
        if (ptemplateNO != null && !ptemplateNO.isEmpty()) {
            sql = "select PRE_DAY,OPTIONAL_TIME from DCP_ptemplate where ptemplateno='" + ptemplateNO + "' and EID='" + eId + "'";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                String prdate = getQData.get(0).get("PRE_DAY").toString();
                String prtime = getQData.get(0).get("OPTIONAL_TIME").toString();
                Calendar cal = Calendar.getInstance();//获得当前时间
                SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
                String submitTime = dfTime.format(cal.getTime());
                if (prdate == null || prdate.isEmpty()) {
                    prdate = "0";//默认当前日期
                }
                if (prtime == null || prtime.isEmpty()) {
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
                
            } else
                return "";
        } else
            return "";
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_POrderProcessReq req) throws Exception {
        return null;
    }
    
    protected List<UptBean> prepareUpdateData1(DCP_POrderProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_POrderProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_POrderProcessReq req) throws Exception {
        return null;
    }
    
    //查询单头
    protected String getQuerySql(DCP_POrderProcessReq req) throws Exception {
        levelElm request = req.getRequest();
        String sqlbuf = " select a.is_add,a.ptemplateno,a.rdate,a.receipt_org,a.process_erp_no,b.pluno,b.featureno,b.baseqty,c.plu_name"
                + " from dcp_porder a"
                //【ID1024237】【霸王3.0】移动门店要货数量为0提交问题  by jinzma 20220304
                + " inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno"
                + " left  join dcp_goods_lang c on a.eid=c.eid and b.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' "
                + " where a.eid='"+req.geteId()+"' and a.shopid='"+req.getShopId()+"' and a.porderno ='"+request.getPorderNo()+"' ";
        return sqlbuf;
    }
    
    /**
     * 查询是否启用信用额度
     */
    private String isEnableCredit(DCP_POrderProcessReq req) {
        String  sql =" select  ENABLECREDIT from DCP_ORG "
                + " where eid = '" + req.geteId() + "'  and ORGANIZATIONNO = '" + req.getOrganizationNO() + "' and STATUS = '100' ";
        return sql;
    }
    
    /**
     * 查询要货单 单头
     */
    private String getPOrder(DCP_POrderProcessReq req) {
        String sql = "SELECT * FROM DCP_PORDER a "
                + " WHERE A.SHOPID = '" + req.getShopId() + "' AND A.EID = '" + req.geteId() + "' and ORGANIZATIONNO = '" + req.getOrganizationNO() + "' AND a.PORDERNO = '" + req.getRequest().getPorderNo() + "'" ;
        return sql;
    }
    
    /**
     * 查询要货单单身
     */
    private String getPOrderDetail(DCP_POrderProcessReq req) {
        String sql ="SELECT * FROM DCP_PORDER_DETAIL "
                + " WHERE eid = '" + req.geteId() + "' AND SHOPID = '" + req.getShopId() + "' AND PORDERNO = '" + req.getRequest().getPorderNo() + "'";
        return sql;
    }
    
    
}

