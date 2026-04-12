package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessPlanCreateTaskReq;
import com.dsc.spos.json.cust.req.DCP_ProcessTask0ConfirmReq;
import com.dsc.spos.json.cust.res.DCP_ProcessPlanCreateTaskRes;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ProcessPlanCreateTask extends SPosAdvanceService<DCP_ProcessPlanCreateTaskReq, DCP_ProcessPlanCreateTaskRes>
{

    @Override
    protected void processDUID(DCP_ProcessPlanCreateTaskReq req, DCP_ProcessPlanCreateTaskRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sysDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sysTime =	new SimpleDateFormat("HHmmss").format(new Date());


        //校验计划单状态STATUS是否为'1.确定'，仅为1可执行生单
        String sql_DCP_PROCESSPLAN="select * from DCP_PROCESSPLAN a " +
                "where a.eid='"+req.getRequest().geteId()+"' " +
                "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                "and a.processplanno='"+req.getRequest().getProcessPlanNo()+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql_DCP_PROCESSPLAN, null);
        if (getQData == null || getQData.size()==0)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找不到记录");
        }
        if (!getQData.get(0).get("STATUS").toString().equals("1"))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "计划单必须为【已确认】状态");
        }

        int b_year= Convert.toInt(req.getRequest().getpDateBegin().substring(0, 4),0);
        int b_month= Convert.toInt(req.getRequest().getpDateBegin().substring(4, 6),0);
        int b_day= Convert.toInt(req.getRequest().getpDateBegin().substring(6, 8),0);

        int e_year= Convert.toInt(req.getRequest().getpDateEnd().substring(0, 4),0);
        int e_month= Convert.toInt(req.getRequest().getpDateEnd().substring(4, 6),0);
        int e_day= Convert.toInt(req.getRequest().getpDateEnd().substring(6, 8),0);

        LocalDate startDate = LocalDate.of(b_year, b_month, b_day);
        LocalDate endDate = LocalDate.of(e_year, e_month, e_day);

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        //
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");

        //明细
        String sql_DCP_PROCESSPLAN_DETAIL="select * from DCP_PROCESSPLAN_DETAIL a " +
                "where a.eid='"+req.getRequest().geteId()+"' " +
                "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                "and a.processplanno='"+req.getRequest().getProcessPlanNo()+"' " +
                "order by a.item ";
        List<Map<String, Object>> getQData_PROCESSPLAN_DETAIL =null;



        String[] columnsName_DCP_PROCESSTASK0_DETAIL = {
                "EID","ORGANIZATIONNO","SHOPID","PROCESSTASKNO","ITEM","PQTY","PUNIT","BASEQTY",
                "PLUNO","PRICE","BASEUNIT","UNIT_RATIO","AMT","DISTRIPRICE","DISTRIAMT","PDATE","BDATE",
                "DTNO1","PQTY1","DTNO2","PQTY2","DTNO3","PQTY3","DTNO4","PQTY4","DTNO5",
                "PQTY5","DTNO6","PQTY6","DTNO7","PQTY7","DTNO8","PQTY8","DTNO9","PQTY9",
                "DTNO10","PQTY10","MUL_QTY","ADVICEQTY","ASKQTY"
        };


        boolean insert_ok=false;
        //简单处理个提示信息,这个提示1笔任务不产生才提示出来
        StringBuffer sb_Tip=new StringBuffer("");

        //两个日期相同，就是0
        for (long i = 0; i <=days; i++)
        {
            String v_date=startDate.plusDays(i).format(formatter1);
            //1.判断生产日期pDate是否存在当前生产计划单生产的任务单，如果存在则当前生产日期跳过生单
            String sql_dcp_processtask0="select * from dcp_processtask0 a " +
                    "where a.pdate='"+v_date+"' " +
                    "and a.eid='"+req.getRequest().geteId()+"' " +
                    "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                    "and a.processplanno='"+req.getRequest().getProcessPlanNo()+"' ";
            List<Map<String, Object>> getQData_processtask0 = this.doQueryData(sql_dcp_processtask0, null);
            if (getQData_processtask0 != null && getQData_processtask0.size()>0)
            {
                continue;//存在记录，就跳过
            }
            //startDate.plusDays(i).getDayOfWeek().getValue()这个是周几
            //直接判断周几是否勾选
            if(getQData.get(0).get("WEEKDAY"+startDate.plusDays(i).getDayOfWeek().getValue()+"").toString().equals("1"))
            {
                if (getQData_PROCESSPLAN_DETAIL == null)
                {
                    //只需查1次明细,不为空，下次就不查了
                    getQData_PROCESSPLAN_DETAIL = this.doQueryData(sql_DCP_PROCESSPLAN_DETAIL, null);
                }

                //2.将生产计划单 DCP_PROCESSPLAN、生产计划单明细 DCP_PROCESSPLAN_DETAIL表数据对应生成DCP_PROCESSTASK0、DCP_PROCESSTASK0_DETAIL数据
                if (getQData_PROCESSPLAN_DETAIL != null && getQData_PROCESSPLAN_DETAIL.size()>0)
                {
                    //单日加工任务单
                    String processTaskno= PosPub.getBillNo(dao, req.getRequest().geteId(), req.getRequest().getOrganizationNo(), "DJRW");

                    //
                    long TOT_CQTY=getQData_PROCESSPLAN_DETAIL.stream().filter(PosPub.distinctByKeys(p->p.get("PLUNO").toString())).count();

                    BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
                    BigDecimal bdm_TOT_AMT=new BigDecimal("0");
                    BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");
                    int detailitem=0;

                    for (Map<String, Object> map : getQData_PROCESSPLAN_DETAIL)
                    {
                        bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(map.get("PQTY").toString()));
                        bdm_TOT_AMT=bdm_TOT_AMT.add(new BigDecimal(map.get("AMT").toString()));
                        bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(new BigDecimal(map.get("DISTRIAMT").toString()));
                        detailitem+=1;

                        DataValue[] insValue_DCP_PROCESSTASK0_DETAIL = new DataValue[]
                                {
                                        new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(processTaskno, Types.VARCHAR),
                                        new DataValue(detailitem, Types.VARCHAR),
                                        new DataValue(map.get("PQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEQTY").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PLUNO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PRICE").toString(), Types.VARCHAR),
                                        new DataValue(map.get("BASEUNIT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("UNIT_RATIO").toString(), Types.VARCHAR),
                                        new DataValue(map.get("AMT").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIPRICE").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DISTRIAMT").toString(), Types.VARCHAR),
                                        new DataValue(v_date, Types.VARCHAR),
                                        new DataValue(sysDate, Types.VARCHAR),
                                        new DataValue(map.get("DTNO1").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY1").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO2").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY2").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO3").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY3").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO4").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY4").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO5").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY5").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO6").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY6").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO7").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY7").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO8").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY8").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO9").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY9").toString(), Types.VARCHAR),
                                        new DataValue(map.get("DTNO10").toString(), Types.VARCHAR),
                                        new DataValue(map.get("PQTY10").toString(), Types.VARCHAR),
                                        new DataValue(map.get("MUL_QTY").toString(), Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR)
                                };

                        InsBean ib_DCP_PROCESSTASK0_DETAIL = new InsBean("DCP_PROCESSTASK0_DETAIL", columnsName_DCP_PROCESSTASK0_DETAIL);
                        ib_DCP_PROCESSTASK0_DETAIL.addValues(insValue_DCP_PROCESSTASK0_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK0_DETAIL));

                    }

                    //单头
                    String[] columnsName_DCP_PROCESSTASK0 = {
                            "EID","ORGANIZATIONNO","SHOPID","PROCESSTASKNO","CREATE_DATE","CREATE_TIME","CREATEBY","PTEMPLATENO",
                            "LOAD_DOCTYPE","LOAD_DOCNO","TOT_CQTY","TOT_PQTY","TOT_AMT","TOT_DISTRIAMT","PDATE",
                            "BDATE","MEMO","WAREHOUSE","MATERIALWAREHOUSE","STATUS","CREATETIME","CREATEOPID","LASTMODIOPID","LASTMODITIME",
                            "PROCESSPLANNO"
                    };

                    DataValue[] insValue_DCP_PROCESSTASK0 = new DataValue[]
                            {
                                    new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                    new DataValue(processTaskno, Types.VARCHAR),
                                    new DataValue(sysDate, Types.VARCHAR),
                                    new DataValue(sysTime, Types.VARCHAR),
                                    new DataValue(req.getOpNO(), Types.VARCHAR),
                                    new DataValue(getQData.get(0).get("PTEMPLATENO").toString(), Types.VARCHAR),
                                    new DataValue("", Types.VARCHAR),//LOAD_DOCTYPE
                                    new DataValue("", Types.VARCHAR),//LOAD_DOCNO
                                    new DataValue(TOT_CQTY, Types.VARCHAR),//TOT_CQTY
                                    new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR),//TOT_PQTY
                                    new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR),//TOT_AMT
                                    new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR),//TOT_DISTRIAMT
                                    new DataValue(v_date, Types.VARCHAR),
                                    new DataValue(sysDate, Types.VARCHAR),
                                    new DataValue(getQData.get(0).get("MEMO").toString(), Types.VARCHAR),
                                    new DataValue(getQData.get(0).get("WAREHOUSE").toString(), Types.VARCHAR),
                                    new DataValue(getQData.get(0).get("MATERIALWAREHOUSE").toString(), Types.VARCHAR),
                                    new DataValue("0", Types.VARCHAR),//状态 0新建 1确定 2作废
                                    new DataValue(lastmoditime, Types.DATE),
                                    new DataValue(req.getOpNO(), Types.VARCHAR),
                                    new DataValue(req.getOpNO(), Types.VARCHAR),
                                    new DataValue(lastmoditime, Types.DATE),
                                    new DataValue(getQData.get(0).get("PROCESSPLANNO").toString(), Types.VARCHAR)
                            };
                    InsBean ib_DCP_PROCESSTASK0 = new InsBean("DCP_PROCESSTASK0", columnsName_DCP_PROCESSTASK0);
                    ib_DCP_PROCESSTASK0.addValues(insValue_DCP_PROCESSTASK0);
                    this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK0));

                    //先提交，才能调确认服务
                    this.doExecuteDataToDB();
                    insert_ok=true;


                    //3.单日加工任务单确认
                    DCP_ProcessTask0ConfirmReq dcp_processTask0ConfirmReq=new DCP_ProcessTask0ConfirmReq();
                    DCP_ProcessTask0ConfirmReq.levelRequest request=dcp_processTask0ConfirmReq.new levelRequest();
                    dcp_processTask0ConfirmReq.setLangType(req.getLangType());
                    dcp_processTask0ConfirmReq.setRequestId(req.getRequestId());
                    request.seteId(req.geteId());
                    request.setOrganizationNo(req.getOrganizationNO());
                    request.setOpNo(req.getOpNO());
                    List<DCP_ProcessTask0ConfirmReq.level2> level2List=new ArrayList<>();
                    DCP_ProcessTask0ConfirmReq.level2 level2=dcp_processTask0ConfirmReq.new level2();
                    level2.setProcessTaskNo(processTaskno);
                    level2List.add(level2);
                    request.setDataList(level2List);
                    dcp_processTask0ConfirmReq.setRequest(request);

                    DCP_ProcessTask0ConfirmRes dcp_processTask0ConfirmRes=new DCP_ProcessTask0ConfirmRes();
                    //调用
                    DCP_ProcessTask0Confirm dcp_processTask0Confirm=new DCP_ProcessTask0Confirm();
                    dcp_processTask0Confirm.setDao(this.dao);
                    dcp_processTask0Confirm.processDUID(dcp_processTask0ConfirmReq,dcp_processTask0ConfirmRes);

                }
            }
            else
            {
                sb_Tip.append(v_date+"周"+startDate.plusDays(i).getDayOfWeek().getValue()+",计划单没设置\r\n");
            }
        }


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription(insert_ok==false?sb_Tip.toString():"服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessPlanCreateTaskReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessPlanCreateTaskReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessPlanCreateTaskReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessPlanCreateTaskReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if(Check.Null(req.getRequest().geteId()))
        {
            errMsg.append("eId不能为空值 ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getOrganizationNo()))
        {
            errMsg.append("organizationNo不能为空值 ");
            isFail = true;
        }

        if(Check.Null(req.getRequest().getProcessPlanNo()))
        {
            errMsg.append("processPlanNo不能为空值 ");
            isFail = true;
        }

        if(Check.Null(req.getRequest().getpDateBegin()))
        {
            errMsg.append("pDateBegin不能为空值 ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getpDateEnd()))
        {
            errMsg.append("pDateEnd不能为空值 ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProcessPlanCreateTaskReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessPlanCreateTaskReq>(){};
    }

    @Override
    protected DCP_ProcessPlanCreateTaskRes getResponseType()
    {
        return new DCP_ProcessPlanCreateTaskRes();
    }


}
