package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApWrtOffStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ApWrtOffStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Constant;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_ApWrtOffStatusUpdate extends SPosAdvanceService<DCP_ApWrtOffStatusUpdateReq, DCP_ApWrtOffStatusUpdateRes> {

    @Override
    protected void processDUID(DCP_ApWrtOffStatusUpdateReq req, DCP_ApWrtOffStatusUpdateRes res) throws Exception {

        String eId = req.geteId();
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());
        List<DCP_ApWrtOffStatusUpdateReq.WrtOffListLevel> wrtOffList = req.getRequest().getWrtOffList();

        String accSql="select a.*,to_char(a.APCLOSINGDATE,'yyyyMMdd') as apclosingdates  from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' and a.accountid='"+req.getRequest().getAccountId()+"'  and a.ACCTTYPE='1' and a.status='100' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if(accList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的账套");
        }
        String apClosingDate = accList.get(0).get("APCLOSINGDATES").toString();
        if(Integer.parseInt(apClosingDate)<Integer.parseInt(createDate)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于应付关账日期");
        }
        if(CollUtil.isNotEmpty(wrtOffList)) {

            String noStr = wrtOffList.stream().map(x -> "'" + x.getWrtOffNo() + "'").distinct().collect(Collectors.joining(","));
            String sql="select * from DCP_APWRTOFF a where a.eid='"+req.getRequest().getEId()+"' " +
                    " and a.wrtOffNo in ("+noStr+") ";
            List<Map<String, Object>> list = this.doQueryData(sql, null);

            String detailSql="select a.* from DCP_APBILLWRTOFF a " +
                    " where a.eid='"+req.getRequest().getEId()+"' " +
                    " and a.accountid='"+req.getRequest().getAccountId()+"' and a.wrtOffNo in ("+noStr+")  ";
            List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

            //查询所有的前置单据
            String sSql="select a.wrtoffno,a.item,c.item as apItem,c.Apno,d.* " +
                    " from dcp_apbillwrtoff a" +
                    " left join dcp_appred b on a.eid=b.eid and a.accountid=b.accountid and a.WRTOFFBILLNO=b.apno and a.WRTOFFBILLitem=b.item " +
                    " left join DCP_APBILLDETAIL c on c.eid=a.eid and c.accountid=a.accountid and c.apno=b.apno " +
                    " inner join dcp_settledata d on c.eid=d.eid and d.billno=c.sourceno and d.item=c.sourceitem " +
                    " and a.accountid='"+req.getRequest().getAccountId()+"' and a.wrtOffNo in ("+noStr+")  ";
            List<Map<String, Object>> settList = this.doQueryData(sSql, null);


            if (Constant.OPR_TYPE_CONFIRM.equals(req.getRequest().getOpType())) {

                //● 审核时，判断：FCYDRTATAMT原币借方金额合计是否相等 FCYCRTATAMT原币贷方金额合计，不相等提示前端，差异金额且不得审核；
                //● 【审核】时当【付款明细】中类型为 10 付款，需将该笔单据更新写入：DCP_BANKCASHFLOWS 银存异动表，TASKID 作业类型=4：应付核销单；取消时需同步删除；

                //审核要生成待抵 其他应收单据 todo
                //    3.4.10. 21预付待抵转应收；---》 D 借 其他应收
                //    3.4.11. 22转付第三方；---》 C 贷 其他应付
                //    3.4.12. 23预付转待抵；---》 D 借 预付待抵

                if(CollUtil.isNotEmpty(list)){
                    for (DCP_ApWrtOffStatusUpdateReq.WrtOffListLevel wrtOffListLevel:wrtOffList){
                        List<Map<String, Object>> filterRows = list.stream().filter(x -> x.get("WRTOFFNO").toString().equals(wrtOffListLevel.getWrtOffNo())).collect(Collectors.toList());
                        if(filterRows.size()>0){
                            BigDecimal fcydrtatamt = new BigDecimal(filterRows.get(0).get("FCYDRTATAMT").toString());
                            BigDecimal fcycrtatamt = new BigDecimal(filterRows.get(0).get("FCYCRTATAMT").toString());
                            if(fcydrtatamt.compareTo(fcycrtatamt)!=0){
                                continue;
                            }

                            //● 回写已核销金额表及字段：
                            //  ○ DCP_APPERD 多账期： LCYPMTREVAMT 本币冲销金额，原币付款冲销金额FCYPMTREVAMT
                            List<Map<String, Object>> filterDetails = detailList.stream().filter(x -> x.get("WRTOFFNO").toString().equals(wrtOffListLevel.getWrtOffNo())).collect(Collectors.toList());
                            //filterDetails group by WRTOFFBILLNO 汇总FCYREVAMT LCYREVAMT
                            Map<String, Map<String, BigDecimal>> apSummary = filterDetails.stream()
                                    .collect(Collectors.groupingBy(
                                            d -> d.get("WRTOFFBILLNO").toString(),
                                            Collectors.reducing(
                                                    new AbstractMap.SimpleImmutableEntry<>(BigDecimal.ZERO, BigDecimal.ZERO),
                                                    d -> new AbstractMap.SimpleImmutableEntry<>(
                                                            Optional.ofNullable(d.get("FCYREVAMT").toString()).map(BigDecimal::new).orElse(BigDecimal.ZERO),
                                                            Optional.ofNullable(d.get("LCYREVAMT").toString()).map(BigDecimal::new).orElse(BigDecimal.ZERO)
                                                    ),
                                                    (e1, e2) -> new AbstractMap.SimpleImmutableEntry<>(
                                                            e1.getKey().add(e2.getKey()),
                                                            e1.getValue().add(e2.getValue())
                                                    )
                                            )
                                    ))
                                    .entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> {
                                                Map<String, BigDecimal> map = new HashMap<>();
                                                map.put("FCYREVAMT", e.getValue().getKey());
                                                map.put("LCYREVAMT", e.getValue().getValue());
                                                return map;
                                            }
                                    ));

                            //apSummary 循环更新前置应付单
                            for (Map.Entry<String, Map<String, BigDecimal>> entry : apSummary.entrySet()) {
                                String billNo = entry.getKey();
                                Map<String, BigDecimal> singleMap = entry.getValue();
                                BigDecimal fcyrevamt = new BigDecimal(singleMap.get("FCYREVAMT").toString());
                                BigDecimal lcyrevamt = new BigDecimal(singleMap.get("LCYREVAMT").toString());
                                //  ○ DCP_APBILL 应付单： LCYREVAMT 本币冲销金额，原币冲销金额FCYREVAMT

                                UptBean ub1 = new UptBean("DCP_APBILL");
                                ub1.addUpdateValue("FCYREVAMT", new DataValue(fcyrevamt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                ub1.addUpdateValue("LCYREVAMT", new DataValue(lcyrevamt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
                                ub1.addCondition("APNO", new DataValue(billNo, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub1));

                            }

                            //  ○ DCP_APPERD 多账期： LCYPMTREVAMT 本币冲销金额，原币付款冲销金额FCYPMTREVAMT
                            for (Map<String, Object> singleDetail:filterRows){
                                BigDecimal fcyRevAmt = new BigDecimal(singleDetail.get("FCYREVAMT").toString());
                                BigDecimal lcyRevAmt = new BigDecimal(singleDetail.get("LCYREVAMT").toString());
                                UptBean ub1 = new UptBean("DCP_APPERD");
                                ub1.addUpdateValue("FCYPMTREVAMT", new DataValue(fcyRevAmt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                ub1.addUpdateValue("LCYPMTREVAMT", new DataValue(lcyRevAmt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
                                ub1.addCondition("APNO", new DataValue(singleDetail.get("WRTOFFBILLNO"), Types.VARCHAR));
                                ub1.addCondition("ITEM", new DataValue(singleDetail.get("WRTOFFBILLITEM"), Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub1));
                            }

                            //  ○ DCP_SETTLEDATA 结算底稿：PAIDAMT 已付款金额，UNPAIDAMT未付款金额=BILLAMT 含税-PAIDAMT已付金额
                            // 先把负向的加上  冲销正向的  查明细关联应付
                            List<Map<String, Object>> filterSettle = settList.stream().filter(x -> x.get("WRTOFFNO").toString().equals(wrtOffListLevel.getWrtOffNo())).collect(Collectors.toList());
                            List<String> wrtOffItemList = filterSettle.stream().map(x -> x.get("WRTOFFITEM").toString()).distinct().collect(Collectors.toList());

                            for(String offItem:wrtOffItemList){
                                List<Map<String, Object>> wrtoffitemFilter = filterRows.stream().filter(x -> x.get("ITEM").toString().equals(offItem)).collect(Collectors.toList());
                                if(wrtoffitemFilter.size()>0){
                                    //核销金额
                                    BigDecimal fcyRevAmt = new BigDecimal(wrtoffitemFilter.get(0).get("FCYREVAMT").toString());
                                    //过滤settle
                                    List<Map<String, Object>> filterSettle2 = filterSettle.stream().filter(x -> x.get("ITEM").toString().equals(offItem)).collect(Collectors.toList());
                                    if(filterSettle2.size()>0){
                                        //filterSettle2 根据DIRECTION 从小到大排序  -1 的加  1的减
                                        filterSettle2.sort(new Comparator<Map<String, Object>>() {
                                            @Override
                                            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                                                return o1.get("DIRECTION").toString().compareTo(o2.get("DIRECTION").toString());
                                            }
                                        });

                                        for (Map<String, Object> singleSettle:filterSettle2){
                                            String direction = singleSettle.get("DIRECTION").toString();
                                            BigDecimal paidAmt = new BigDecimal(singleSettle.get("PAIDAMT").toString());
                                            BigDecimal unPaidAmt = new BigDecimal(singleSettle.get("UNPAIDAMT").toString());
                                            BigDecimal billAmt = new BigDecimal(singleSettle.get("BILLAMT").toString());
                                            //目前单据金额减去已付款金额  待付款金额
                                            BigDecimal subtract = billAmt.subtract(paidAmt);
                                            if(direction.equals("-1")){
                                                fcyRevAmt=fcyRevAmt.add(subtract);
                                            }else{
                                                fcyRevAmt=fcyRevAmt.subtract( subtract);
                                            }

                                            //核销额没有了就干掉
                                            if(fcyRevAmt.compareTo(BigDecimal.ZERO)<=0){
                                                break;
                                            }

                                            if(fcyRevAmt.compareTo(subtract)<=0){
                                                paidAmt=paidAmt.add(fcyRevAmt);
                                                fcyRevAmt=new BigDecimal(0);
                                            }else{
                                                paidAmt=billAmt;
                                                fcyRevAmt=fcyRevAmt.subtract(subtract);
                                            }

                                            BigDecimal nowUnPaidAmt=billAmt.subtract(paidAmt);
                                            UptBean ub1 = new UptBean("DCP_APPERD");
                                            ub1.addUpdateValue("PAIDAMT", new DataValue(paidAmt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                            ub1.addUpdateValue("UNPAIDAMT", new DataValue(nowUnPaidAmt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                            ub1.addCondition("BILLNO", new DataValue(singleSettle.get("BILLNO").toString(), Types.VARCHAR));
                                            ub1.addCondition("ITEM", new DataValue(singleSettle.get("ITEM"), Types.VARCHAR));
                                            this.addProcessData(new DataProcessBean(ub1));
                                        }


                                    }
                                }

                            }


                        }
                    }
                }
            } else if (Constant.OPR_TYPE_UNCONFIRM.equals(req.getRequest().getOpType())) {

                //● 审核时，判断：FCYDRTATAMT原币借方金额合计是否相等 FCYCRTATAMT原币贷方金额合计，不相等提示前端，差异金额且不得审核；
                //● 【审核】时当【付款明细】中类型为 10 付款，需将该笔单据更新写入：DCP_BANKCASHFLOWS 银存异动表，TASKID 作业类型=4：应付核销单；取消时需同步删除；

                //反审核要删除待抵 其他应收单据 todo
                //    3.4.10. 21预付待抵转应收；---》 D 借 其他应收
                //    3.4.11. 22转付第三方；---》 C 贷 其他应付
                //    3.4.12. 23预付转待抵；---》 D 借 预付待抵

                if(CollUtil.isNotEmpty(list)){
                    for (DCP_ApWrtOffStatusUpdateReq.WrtOffListLevel wrtOffListLevel:wrtOffList){
                        List<Map<String, Object>> filterRows = list.stream().filter(x -> x.get("WRTOFFNO").toString().equals(wrtOffListLevel.getWrtOffNo())).collect(Collectors.toList());
                        if(filterRows.size()>0){


                            //● 回写已核销金额表及字段：
                            //  ○ DCP_APPERD 多账期： LCYPMTREVAMT 本币冲销金额，原币付款冲销金额FCYPMTREVAMT
                            List<Map<String, Object>> filterDetails = detailList.stream().filter(x -> x.get("WRTOFFNO").toString().equals(wrtOffListLevel.getWrtOffNo())).collect(Collectors.toList());
                            //filterDetails group by WRTOFFBILLNO 汇总FCYREVAMT LCYREVAMT
                            Map<String, Map<String, BigDecimal>> apSummary = filterDetails.stream()
                                    .collect(Collectors.groupingBy(
                                            d -> d.get("WRTOFFBILLNO").toString(),
                                            Collectors.reducing(
                                                    new AbstractMap.SimpleImmutableEntry<>(BigDecimal.ZERO, BigDecimal.ZERO),
                                                    d -> new AbstractMap.SimpleImmutableEntry<>(
                                                            Optional.ofNullable(d.get("FCYREVAMT").toString()).map(BigDecimal::new).orElse(BigDecimal.ZERO),
                                                            Optional.ofNullable(d.get("LCYREVAMT").toString()).map(BigDecimal::new).orElse(BigDecimal.ZERO)
                                                    ),
                                                    (e1, e2) -> new AbstractMap.SimpleImmutableEntry<>(
                                                            e1.getKey().add(e2.getKey()),
                                                            e1.getValue().add(e2.getValue())
                                                    )
                                            )
                                    ))
                                    .entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> {
                                                Map<String, BigDecimal> map = new HashMap<>();
                                                map.put("FCYREVAMT", e.getValue().getKey());
                                                map.put("LCYREVAMT", e.getValue().getValue());
                                                return map;
                                            }
                                    ));

                            //apSummary 循环更新前置应付单
                            for (Map.Entry<String, Map<String, BigDecimal>> entry : apSummary.entrySet()) {
                                String billNo = entry.getKey();
                                Map<String, BigDecimal> singleMap = entry.getValue();
                                BigDecimal fcyrevamt = new BigDecimal(singleMap.get("FCYREVAMT").toString());
                                BigDecimal lcyrevamt = new BigDecimal(singleMap.get("LCYREVAMT").toString());
                                //  ○ DCP_APBILL 应付单： LCYREVAMT 本币冲销金额，原币冲销金额FCYREVAMT

                                UptBean ub1 = new UptBean("DCP_APBILL");
                                ub1.addUpdateValue("FCYREVAMT", new DataValue(fcyrevamt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                ub1.addUpdateValue("LCYREVAMT", new DataValue(lcyrevamt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
                                ub1.addCondition("APNO", new DataValue(billNo, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub1));

                            }

                            //  ○ DCP_APPERD 多账期： LCYPMTREVAMT 本币冲销金额，原币付款冲销金额FCYPMTREVAMT
                            for (Map<String, Object> singleDetail:filterRows){
                                BigDecimal fcyRevAmt = new BigDecimal(singleDetail.get("FCYREVAMT").toString());
                                BigDecimal lcyRevAmt = new BigDecimal(singleDetail.get("LCYREVAMT").toString());
                                UptBean ub1 = new UptBean("DCP_APPERD");
                                ub1.addUpdateValue("FCYPMTREVAMT", new DataValue(fcyRevAmt, Types.DECIMAL, DataValue.DataExpression.SubSelf));
                                ub1.addUpdateValue("LCYPMTREVAMT", new DataValue(lcyRevAmt, Types.DECIMAL, DataValue.DataExpression.SubSelf));
                                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
                                ub1.addCondition("APNO", new DataValue(singleDetail.get("WRTOFFBILLNO"), Types.VARCHAR));
                                ub1.addCondition("ITEM", new DataValue(singleDetail.get("WRTOFFBILLITEM"), Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub1));
                            }

                            //  ○ DCP_SETTLEDATA 结算底稿：PAIDAMT 已付款金额，UNPAIDAMT未付款金额=BILLAMT 含税-PAIDAMT已付金额
                            // 先把负向的加上  冲销正向的  查明细关联应付
                            List<Map<String, Object>> filterSettle = settList.stream().filter(x -> x.get("WRTOFFNO").toString().equals(wrtOffListLevel.getWrtOffNo())).collect(Collectors.toList());
                            List<String> wrtOffItemList = filterSettle.stream().map(x -> x.get("WRTOFFITEM").toString()).distinct().collect(Collectors.toList());

                            for(String offItem:wrtOffItemList){
                                List<Map<String, Object>> wrtoffitemFilter = filterRows.stream().filter(x -> x.get("ITEM").toString().equals(offItem)).collect(Collectors.toList());
                                if(wrtoffitemFilter.size()>0){
                                    //核销金额
                                    BigDecimal fcyRevAmt = new BigDecimal(wrtoffitemFilter.get(0).get("FCYREVAMT").toString());
                                    //过滤settle
                                    List<Map<String, Object>> filterSettle2 = filterSettle.stream().filter(x -> x.get("ITEM").toString().equals(offItem)).collect(Collectors.toList());
                                    if(filterSettle2.size()>0){
                                        //filterSettle2 根据DIRECTION 从小到大排序  -1 的加  1的减
                                        filterSettle2.sort(new Comparator<Map<String, Object>>() {
                                            @Override
                                            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                                                return o1.get("DIRECTION").toString().compareTo(o2.get("DIRECTION").toString());
                                            }
                                        });

                                        for (Map<String, Object> singleSettle:filterSettle2){
                                            String direction = singleSettle.get("DIRECTION").toString();
                                            BigDecimal paidAmt = new BigDecimal(singleSettle.get("PAIDAMT").toString());
                                            BigDecimal unPaidAmt = new BigDecimal(singleSettle.get("UNPAIDAMT").toString());
                                            BigDecimal billAmt = new BigDecimal(singleSettle.get("BILLAMT").toString());
                                            //目前单据金额减去已付款金额  待付款金额
                                            BigDecimal subtract = billAmt.subtract(paidAmt);
                                            if(direction.equals("-1")){
                                                fcyRevAmt=fcyRevAmt.add(subtract);
                                            }else{
                                                fcyRevAmt=fcyRevAmt.subtract( subtract);
                                            }

                                            //核销额没有了就干掉
                                            if(fcyRevAmt.compareTo(BigDecimal.ZERO)<=0){
                                                break;
                                            }

                                            if(fcyRevAmt.compareTo(billAmt)<=0){
                                                paidAmt=billAmt.subtract(fcyRevAmt);
                                                fcyRevAmt=new BigDecimal(0);
                                            }else{
                                                paidAmt=new BigDecimal(0);
                                                fcyRevAmt=fcyRevAmt.subtract(billAmt);
                                            }

                                            BigDecimal nowUnPaidAmt=billAmt.subtract(paidAmt);
                                            UptBean ub1 = new UptBean("DCP_APPERD");
                                            ub1.addUpdateValue("PAIDAMT", new DataValue(paidAmt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                            ub1.addUpdateValue("UNPAIDAMT", new DataValue(nowUnPaidAmt, Types.DECIMAL, DataValue.DataExpression.UpdateSelf));
                                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                            ub1.addCondition("BILLNO", new DataValue(singleSettle.get("BILLNO").toString(), Types.VARCHAR));
                                            ub1.addCondition("ITEM", new DataValue(singleSettle.get("ITEM"), Types.VARCHAR));
                                            this.addProcessData(new DataProcessBean(ub1));
                                        }


                                    }
                                }

                            }


                        }
                    }
                }


            } else if (Constant.OPR_TYPE_CANCEL.equals(req.getRequest().getOpType())) {
                for(DCP_ApWrtOffStatusUpdateReq.WrtOffListLevel wrtOffListLevel:wrtOffList){
                    UptBean ub1 = new UptBean("DCP_APWRTOFF");

                    ub1.addUpdateValue("STATUS", DataValues.newString("-1"));
                    ub1.addUpdateValue("CANCELBY", DataValues.newString(req.getOpNO()));
                    ub1.addUpdateValue("CANCEL_DATE", DataValues.newString(createDate));
                    ub1.addUpdateValue("CANCEL_TIME", DataValues.newString(createTime));

                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    //ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getac(), Types.VARCHAR));
                    ub1.addCondition("WRTOFFNO", new DataValue(wrtOffListLevel.getWrtOffNo(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }
            }
        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApWrtOffStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApWrtOffStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApWrtOffStatusUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApWrtOffStatusUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ApWrtOffStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ApWrtOffStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ApWrtOffStatusUpdateRes getResponseType() {
        return new DCP_ApWrtOffStatusUpdateRes();
    }
}

