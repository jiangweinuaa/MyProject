package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessTask0ConfirmReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTask0ConfirmRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ProcessTask0Confirm extends SPosAdvanceService<DCP_ProcessTask0ConfirmReq, DCP_ProcessTask0ConfirmRes>
{


    @Override
    protected void processDUID(DCP_ProcessTask0ConfirmReq req, DCP_ProcessTask0ConfirmRes res) throws Exception
    {

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sysDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sysTime =	new SimpleDateFormat("HHmmss").format(new Date());

        List<DCP_ProcessTask0ConfirmReq.level2> dataList = req.getRequest().getDataList();

        for (DCP_ProcessTask0ConfirmReq.level2 level2 : dataList)
        {
            //1.更新单据状态为1.确定
            UptBean up1 = new UptBean("DCP_PROCESSTASK0");
            up1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
            up1.addUpdateValue("CONFIRMBY", new DataValue(req.getRequest().getOpNo(), Types.VARCHAR));
            up1.addUpdateValue("CONFIRM_DATE", new DataValue(sysDate, Types.VARCHAR));
            up1.addUpdateValue("CONFIRM_TIME", new DataValue(sysTime, Types.VARCHAR));
            up1.addUpdateValue("LASTMODIOPID", new DataValue(req.getRequest().getOpNo(), Types.VARCHAR));
            up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));


            up1.addCondition("EID", new DataValue(req.getRequest().geteId(),Types.VARCHAR));
            up1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(),Types.VARCHAR));
            up1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(),Types.VARCHAR));
            up1.addCondition("PROCESSTASKNO", new DataValue(level2.getProcessTaskNo(),Types.VARCHAR));

            this.addProcessData(new DataProcessBean(up1));


            //2.将单日加工任务单按加工批次拆分成多张任务单，生成加工任务单 DCP_PROCESSTASK、加工任务单明细 DCP_PROCESSTASK_DETAIL
            String sql_Data="select a.*,b.ptemplateno,b.memo,b.pdate,b.warehouse,b.materialwarehouse,b.processtaskno,b.processplanno,b.PROCESSPLANNAME,c.plu_name, " +
                    "d1.dtname dtname1,d1.begin_time begin_time1,d1.end_time end_time1, " +
                    "d2.dtname dtname2,d2.begin_time begin_time2,d2.end_time end_time2, " +
                    "d3.dtname dtname3,d3.begin_time begin_time3,d3.end_time end_time3, " +
                    "d4.dtname dtname4,d4.begin_time begin_time4,d4.end_time end_time4, " +
                    "d5.dtname dtname5,d5.begin_time begin_time5,d5.end_time end_time5, " +
                    "d6.dtname dtname6,d6.begin_time begin_time6,d6.end_time end_time6, " +
                    "d7.dtname dtname7,d7.begin_time begin_time7,d7.end_time end_time7, " +
                    "d8.dtname dtname8,d8.begin_time begin_time8,d8.end_time end_time8, " +
                    "d9.dtname dtname9,d9.begin_time begin_time9,d9.end_time end_time9, " +
                    "d10.dtname dtname10,d10.begin_time begin_time10,d10.end_time end_time10  " +
                    "from Dcp_Processtask0_Detail a " +
                    "inner join DCP_PROCESSTASK0 b on a.eid=b.eid and a.organizationno=b.organizationno and a.processtaskno=b.processtaskno " +
                    "left join dcp_goods_lang c on a.eid=c.eid and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' " +
                    "left join DCP_DINNERTIME d1 on d1.eid=a.eid and d1.shopid=a.shopid and d1.dtno=a.dtno1 " +
                    "left join DCP_DINNERTIME d2 on d2.eid=a.eid and d2.shopid=a.shopid and d2.dtno=a.dtno2 " +
                    "left join DCP_DINNERTIME d3 on d3.eid=a.eid and d3.shopid=a.shopid and d3.dtno=a.dtno3 " +
                    "left join DCP_DINNERTIME d4 on d4.eid=a.eid and d4.shopid=a.shopid and d4.dtno=a.dtno4 " +
                    "left join DCP_DINNERTIME d5 on d5.eid=a.eid and d5.shopid=a.shopid and d5.dtno=a.dtno5 " +
                    "left join DCP_DINNERTIME d6 on d6.eid=a.eid and d6.shopid=a.shopid and d6.dtno=a.dtno6 " +
                    "left join DCP_DINNERTIME d7 on d7.eid=a.eid and d7.shopid=a.shopid and d7.dtno=a.dtno7 " +
                    "left join DCP_DINNERTIME d8 on d8.eid=a.eid and d8.shopid=a.shopid and d8.dtno=a.dtno8 " +
                    "left join DCP_DINNERTIME d9 on d9.eid=a.eid and d9.shopid=a.shopid and d9.dtno=a.dtno9 " +
                    "left join DCP_DINNERTIME d10 on d10.eid=a.eid and d10.shopid=a.shopid and d10.dtno=a.dtno10 " +
                    "where a.eid='"+req.getRequest().geteId()+"' " +
                    "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                    "and a.processtaskno='"+level2.getProcessTaskNo()+"' " +
                    "order by a.item ";

            List<Map<String, Object>> getQData = this.doQueryData(sql_Data, null);
            if (getQData != null && getQData.size()>0)
            {
                //****固定10个批次，找出每个批次，有数量的记录

                //第1批
                List<Map<String, Object>> getQData_QTY1=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY1"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY1 != null && getQData_QTY1.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY1.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY1)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY1").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY1"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY1"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));

                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY1").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY1"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY1"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY1.get(0).get("DTNAME1").toString()+"*"+getQData_QTY1.get(0).get("BEGIN_TIME1").toString()+"*"+getQData_QTY1.get(0).get("END_TIME1").toString()+"*"+getQData_QTY1.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY1.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY1.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY1.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY1.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY1.get(0).get("DTNO1").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY1.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY1.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY1.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }

                //第2批
                List<Map<String, Object>> getQData_QTY2=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY2"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY2 != null && getQData_QTY2.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY2.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY2)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY2").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY2"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY2"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));


                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY2").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY2"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY2"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY2.get(0).get("DTNAME2").toString()+"*"+getQData_QTY2.get(0).get("BEGIN_TIME2").toString()+"*"+getQData_QTY2.get(0).get("END_TIME2").toString()+"*"+getQData_QTY2.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY2.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY2.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY2.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY2.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY2.get(0).get("DTNO2").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY2.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY2.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY2.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }


                //第3批
                List<Map<String, Object>> getQData_QTY3=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY3"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY3 != null && getQData_QTY3.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY3.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY3)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY3").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY3"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY3"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));

                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY3").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY3"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY3"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY3.get(0).get("DTNAME3").toString()+"*"+getQData_QTY3.get(0).get("BEGIN_TIME3").toString()+"*"+getQData_QTY3.get(0).get("END_TIME3").toString()+"*"+getQData_QTY3.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY3.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY3.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY3.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY3.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY3.get(0).get("DTNO3").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY3.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY3.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY3.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }


                //第4批
                List<Map<String, Object>> getQData_QTY4=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY4"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY4 != null && getQData_QTY4.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY4.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY4)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY4").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY4"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY4"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));

                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY4").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY4"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY4"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY4.get(0).get("DTNAME4").toString()+"*"+getQData_QTY4.get(0).get("BEGIN_TIME4").toString()+"*"+getQData_QTY4.get(0).get("END_TIME4").toString()+"*"+getQData_QTY4.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY4.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY4.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY4.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY4.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY4.get(0).get("DTNO4").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY4.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY4.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY4.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }


                //第5批
                List<Map<String, Object>> getQData_QTY5=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY5"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY5 != null && getQData_QTY5.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY5.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY5)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY5").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY5"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY5"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));


                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY5").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY5"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY5"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY5.get(0).get("DTNAME5").toString()+"*"+getQData_QTY5.get(0).get("BEGIN_TIME5").toString()+"*"+getQData_QTY5.get(0).get("END_TIME5").toString()+"*"+getQData_QTY5.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY5.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY5.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY5.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY5.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY5.get(0).get("DTNO5").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY5.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY5.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY5.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }


                //第6批
                List<Map<String, Object>> getQData_QTY6=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY6"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY6 != null && getQData_QTY6.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY6.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY6)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY6").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY6"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY6"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));


                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY6").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY6"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY6"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY6.get(0).get("DTNAME6").toString()+"*"+getQData_QTY6.get(0).get("BEGIN_TIME6").toString()+"*"+getQData_QTY6.get(0).get("END_TIME6").toString()+"*"+getQData_QTY6.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY6.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY6.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY6.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY6.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY6.get(0).get("DTNO6").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY6.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY6.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY6.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }


                //第7批
                List<Map<String, Object>> getQData_QTY7=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY7"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY7 != null && getQData_QTY7.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY7.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY7)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY7").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY7"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY7"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));


                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY7").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY7"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY7"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY7.get(0).get("DTNAME7").toString()+"*"+getQData_QTY7.get(0).get("BEGIN_TIME7").toString()+"*"+getQData_QTY7.get(0).get("END_TIME7").toString()+"*"+getQData_QTY7.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY7.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY7.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY7.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY7.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY7.get(0).get("DTNO7").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY7.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY7.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY7.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }


                //第8批
                List<Map<String, Object>> getQData_QTY8=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY8"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY8 != null && getQData_QTY8.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY8.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY8)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY8").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY8"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY8"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));


                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY8").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY8"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY8"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY8.get(0).get("DTNAME8").toString()+"*"+getQData_QTY8.get(0).get("BEGIN_TIME8").toString()+"*"+getQData_QTY8.get(0).get("END_TIME8").toString()+"*"+getQData_QTY8.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY8.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY8.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY8.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY8.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY8.get(0).get("DTNO8").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY8.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY8.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY8.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }


                //第9批
                List<Map<String, Object>> getQData_QTY9=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY9"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY9 != null && getQData_QTY9.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY9.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY9)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY9").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY9"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY9"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));


                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY9").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY9"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY9"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY9.get(0).get("DTNAME9").toString()+"*"+getQData_QTY9.get(0).get("BEGIN_TIME9").toString()+"*"+getQData_QTY9.get(0).get("END_TIME9").toString()+"*"+getQData_QTY9.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY9.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY9.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY9.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY9.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY9.get(0).get("DTNO9").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY9.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY9.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY9.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }


                //第10批
                List<Map<String, Object>> getQData_QTY10=getQData.stream().filter(p-> Convert.toBigDecimal(p.get("PQTY10"), BigDecimal.ZERO).compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
                if (getQData_QTY10 != null && getQData_QTY10.size()>0)
                {
                    //加工任务单号
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "JGRW");
                    //
                    long TOT_CQTY=getQData_QTY10.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");

                    String[] columnsName_DCP_PROCESSTASK_DETAIL = {
                            "PROCESSTASKNO", "SHOPID","OITEM", "item", "pluNO","PLUNAME",
                            "punit", "pqty", "baseunit", "baseqty","unit_Ratio",
                            "price", "amt", "EID", "organizationNO", "mul_Qty",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };

                    int v_item=0;
                    for (Map<String, Object> map : getQData_QTY10)
                    {
                        v_item+=1;
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY10").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY10"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY10"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));


                        DataValue[] insValue_DCP_PROCESSTASK_DETAIL = new DataValue[]
                                {
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("ITEM").toString(), Types.VARCHAR),
                                        new DataValue(v_item, Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLU_NAME").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY10").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("PRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY10"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(Convert.toBigDecimal(map.get("DISTRIPRICE"),BigDecimal.ZERO).multiply(Convert.toBigDecimal(map.get("PQTY10"),BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP), Types.VARCHAR),//原表金额是总数量金额，这里是批次金额
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(" ", Types.VARCHAR)//FEATURENO

                                };

                        InsBean ib_DCP_PROCESSTASK_DETAIL = new InsBean("DCP_PROCESSTASK_DETAIL", columnsName_DCP_PROCESSTASK_DETAIL);
                        ib_DCP_PROCESSTASK_DETAIL.addValues(insValue_DCP_PROCESSTASK_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK_DETAIL));

                    }
                    //备注
                    String s_processTask_memo=getQData_QTY10.get(0).get("DTNAME10").toString()+"*"+getQData_QTY10.get(0).get("BEGIN_TIME10").toString()+"*"+getQData_QTY10.get(0).get("END_TIME10").toString()+"*"+getQData_QTY10.get(0).get("MEMO").toString();
                    //
                    String[] columns_DCP_PROCESSTASK = {
                            "SHOPID", "ORGANIZATIONNO","BDATE","CREATEBY", "CREATE_DATE", "CREATE_TIME",
                            "TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
                            "PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","CONFIRMBY","CONFIRM_DATE",
                            "CONFIRM_TIME","DTNO","PROCESSPLANNO","PROCESSPLANNAME","TASK0NO"
                    };
                    DataValue[] insValue_DCP_PROCESSTASK = new DataValue[]{
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),
                            new DataValue(TOT_CQTY, Types.VARCHAR),
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
                            new DataValue(s_processTask_memo, Types.VARCHAR),//加工批次名称+时间段+DCP_PROCESSTASK0.MEMO
                            new DataValue("6", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(getQData_QTY10.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY10.get(0).get("PDATE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY10.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY10.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                            new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(sysTime, Types.VARCHAR),
                            new DataValue(getQData_QTY10.get(0).get("DTNO10").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY10.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY10.get(0).get("PROCESSPLANNAME").toString(), Types.VARCHAR),
                            new DataValue(getQData_QTY10.get(0).get("PROCESSTASKNO").toString(), Types.VARCHAR),

                    };
                    InsBean ib1_DCP_PROCESSTASK = new InsBean("DCP_PROCESSTASK", columns_DCP_PROCESSTASK);
                    ib1_DCP_PROCESSTASK.addValues(insValue_DCP_PROCESSTASK);
                    this.addProcessData(new DataProcessBean(ib1_DCP_PROCESSTASK));
                }



            }


            //3.更新要货单DCP_PORDER_DETAIL表PRODUCTQTY
            String sql_DCP_PROCESSTASK0_SOURCE="select * from DCP_PROCESSTASK0_SOURCE a " +
                    "where a.eid='"+req.getRequest().geteId()+"' " +
                    "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                    "and a.processtaskno='"+level2.getProcessTaskNo()+"' ";
            List<Map<String, Object>> getQData_DCP_PROCESSTASK0_SOURCE = this.doQueryData(sql_DCP_PROCESSTASK0_SOURCE, null);
            if (getQData_DCP_PROCESSTASK0_SOURCE != null && getQData_DCP_PROCESSTASK0_SOURCE.size()>0)
            {
                for (Map<String, Object> map : getQData_DCP_PROCESSTASK0_SOURCE)
                {
                    UptBean up_DCP_PORDER_DETAIL = new UptBean("DCP_PORDER_DETAIL");
                    up_DCP_PORDER_DETAIL.addUpdateValue("PRODUCTQTY", new DataValue(map.get("SHAREQTY").toString(), Types.VARCHAR, DataValue.DataExpression.UpdateSelf));

                    up_DCP_PORDER_DETAIL.addCondition("EID", new DataValue(req.getRequest().geteId(),Types.VARCHAR));
                    up_DCP_PORDER_DETAIL.addCondition("SHOPID", new DataValue(map.get("OSHOP").toString(),Types.VARCHAR));
                    up_DCP_PORDER_DETAIL.addCondition("PORDERNO", new DataValue(map.get("OFNO").toString(),Types.VARCHAR));
                    up_DCP_PORDER_DETAIL.addCondition("ITEM", new DataValue(map.get("OITEM").toString(),Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(up_DCP_PORDER_DETAIL));
                }
            }
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessTask0ConfirmReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessTask0ConfirmReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessTask0ConfirmReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessTask0ConfirmReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(req.getRequest().geteId()))
        {
            errMsg.append("eId不能为空 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getOrganizationNo()))
        {
            errMsg.append("organizationNo不能为空 ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getOpNo()))
        {
            errMsg.append("opNo不能为空 ");
            isFail = true;
        }



        List<DCP_ProcessTask0ConfirmReq.level2> dataList = req.getRequest().getDataList();

        if(dataList==null || dataList.size()==0)
        {
            errMsg.append("dataList必须有记录 ");
            isFail = true;
        }
        else
        {
            for (DCP_ProcessTask0ConfirmReq.level2 level2 : dataList)
            {
                if (Check.Null(level2.getProcessTaskNo()))
                {
                    errMsg.append("processTaskNo不能为空 ");
                    isFail = true;
                }
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProcessTask0ConfirmReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessTask0ConfirmReq>(){};
    }

    @Override
    protected DCP_ProcessTask0ConfirmRes getResponseType()
    {
        return new DCP_ProcessTask0ConfirmRes();
    }
}
