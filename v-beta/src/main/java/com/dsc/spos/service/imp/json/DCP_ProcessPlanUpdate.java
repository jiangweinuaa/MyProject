package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessPlanUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProcessPlanUpdateRes;
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

public class DCP_ProcessPlanUpdate extends SPosAdvanceService<DCP_ProcessPlanUpdateReq, DCP_ProcessPlanUpdateRes>
{


    @Override
    protected void processDUID(DCP_ProcessPlanUpdateReq req, DCP_ProcessPlanUpdateRes res) throws Exception
    {
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sysDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sysTime =	new SimpleDateFormat("HHmmss").format(new Date());


        //生产计划单
        String processplanno= req.getRequest().getProcessPlanNo();

        //
        String sql_Exist="select EID from dcp_processplan a  " +
                "where a.eid='"+req.getRequest().geteId()+"' " +
                "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                "and a.processplanno='"+processplanno+"' ";
        List<Map<String, Object>> getQData_Exist = this.doQueryData(sql_Exist, null);
        if (getQData_Exist == null || getQData_Exist.size()==0)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找不到记录！");
        }

        //校验当前日期范围+周几是否存在生产计划单，不允许重复设置
        StringBuffer sb=new StringBuffer("");
        sb.append("select PROCESSPLANNO " +
                          "from dcp_processplan " +
                          "where ( " +
                          "(pdate_begin<=to_date('"+req.getRequest().getpDateBegin()+"','YYYY-MM-DD') and pdate_end>=to_date('"+req.getRequest().getpDateBegin()+"','YYYY-MM-DD')) " +
                          "or " +
                          "(pdate_begin<=to_date('"+req.getRequest().getpDateEnd()+"','YYYY-MM-DD') and pdate_end>=to_date('"+req.getRequest().getpDateEnd()+"','YYYY-MM-DD')) " +
                          ") " );


        //
        StringBuffer sb_Week=new StringBuffer();
        if (req.getRequest().getWeekDay1() == 1)
        {
            sb_Week.append("weekday1='1' or ");
        }
        if (req.getRequest().getWeekDay2() == 1)
        {
            sb_Week.append("weekday2='1' or ");
        }
        if (req.getRequest().getWeekDay3() == 1)
        {
            sb_Week.append("weekday3='1' or ");
        }
        if (req.getRequest().getWeekDay4() == 1)
        {
            sb_Week.append("weekday4='1' or ");
        }
        if (req.getRequest().getWeekDay5() == 1)
        {
            sb_Week.append("weekday5='1' or ");
        }
        if (req.getRequest().getWeekDay6() == 1)
        {
            sb_Week.append("weekday6='1' or ");
        }
        if (req.getRequest().getWeekDay7() == 1)
        {
            sb_Week.append("weekday7='1' or ");
        }

        //如果有选中星期
        if (sb_Week.length()>0)
        {
            //把最后拼接的 or 去掉
            sb_Week.delete(sb_Week.length()-3,sb_Week.length());

            sb.append("and " +
                              "( " +
                              sb_Week.toString() +
                              " ) ");
        }

        sb.append("and eid='"+req.getRequest().geteId()+"' " +
                          "and organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                          "and processplanno<>'"+req.getRequest().getProcessPlanNo()+"' ");//排除当前单号

        List<Map<String, Object>> getQData = this.doQueryData(sb.toString(), null);
        if (getQData != null && getQData.size()>0)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前日期范围内+周几生效，与单号"+getQData.get(0).get("PROCESSPLANNO").toString()+"重复");
        }

        //SA说先删后插
        DelBean db1 = new DelBean("dcp_processplan_detail");
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db1.addCondition("PROCESSPLANNO", new DataValue(processplanno, Types.VARCHAR));

        this.addProcessData(new DataProcessBean(db1));


        //
        long TOT_CQTY=req.getRequest().getDetailList().stream().filter(PosPub.distinctByKeys(p->p.getPluNo())).count();

        BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
        BigDecimal bdm_TOT_AMT=new BigDecimal("0");
        BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");


        String[] columnsName_DCP_PROCESSPLAN_DETAIL = {
                "EID","ORGANIZATIONNO","SHOPID","PROCESSPLANNO","ITEM","PQTY","PUNIT","BASEQTY",
                "PLUNO","PRICE","BASEUNIT","UNIT_RATIO","AMT","DISTRIPRICE","DISTRIAMT","BDATE",
                "DTNO1","PQTY1","DTNO2","PQTY2","DTNO3","PQTY3","DTNO4","PQTY4","DTNO5",
                "PQTY5","DTNO6","PQTY6","DTNO7","PQTY7","DTNO8","PQTY8","DTNO9","PQTY9",
                "DTNO10","PQTY10","MUL_QTY","FEATURENO","ADVICEQTY"
        };


        for (DCP_ProcessPlanUpdateReq.level2 level2 : req.getRequest().getDetailList())
        {
//            //转换基准单位
//            Map<String, Object> map=PosPub.getBaseQty(dao, req.getRequest().geteId(),
//                                                      level2.getPluNo(), level2.getpUnit(),
//                                                      String.valueOf(level2.getpQty()));
//
//            String baseQty=map.get("baseQty").toString();
//            String baseUnit=map.get("baseUnit").toString();
//            String unitRatio=map.get("unitRatio").toString();


            bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(level2.getpQty()));
            bdm_TOT_AMT=bdm_TOT_AMT.add(new BigDecimal(level2.getAmt()));
            bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(new BigDecimal(level2.getDistriAmt()));


            DataValue[] insValue_DCP_PROCESSPLAN_DETAIL = new DataValue[]
                    {
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(processplanno, Types.VARCHAR),
                            new DataValue(level2.getItem(), Types.VARCHAR),
                            new DataValue(level2.getpQty(), Types.VARCHAR),
                            new DataValue(level2.getpUnit(), Types.VARCHAR),
                            new DataValue(level2.getBaseQty(), Types.VARCHAR),
                            new DataValue(level2.getPluNo(), Types.VARCHAR),
                            new DataValue(level2.getPrice(), Types.VARCHAR),
                            new DataValue(level2.getBaseUnit(), Types.VARCHAR),
                            new DataValue(level2.getUnitRatio(), Types.VARCHAR),
                            new DataValue(level2.getAmt(), Types.VARCHAR),
                            new DataValue(level2.getDistriPrice(), Types.VARCHAR),
                            new DataValue(level2.getDistriAmt(), Types.VARCHAR),
                            new DataValue(sysDate, Types.VARCHAR),
                            new DataValue(level2.getDtNo1(), Types.VARCHAR),
                            new DataValue(level2.getpQty1(), Types.VARCHAR),
                            new DataValue(level2.getDtNo2(), Types.VARCHAR),
                            new DataValue(level2.getpQty2(), Types.VARCHAR),
                            new DataValue(level2.getDtNo3(), Types.VARCHAR),
                            new DataValue(level2.getpQty3(), Types.VARCHAR),
                            new DataValue(level2.getDtNo4(), Types.VARCHAR),
                            new DataValue(level2.getpQty4(), Types.VARCHAR),
                            new DataValue(level2.getDtNo5(), Types.VARCHAR),
                            new DataValue(level2.getpQty5(), Types.VARCHAR),
                            new DataValue(level2.getDtNo6(), Types.VARCHAR),
                            new DataValue(level2.getpQty6(), Types.VARCHAR),
                            new DataValue(level2.getDtNo7(), Types.VARCHAR),
                            new DataValue(level2.getpQty7(), Types.VARCHAR),
                            new DataValue(level2.getDtNo8(), Types.VARCHAR),
                            new DataValue(level2.getpQty8(), Types.VARCHAR),
                            new DataValue(level2.getDtNo9(), Types.VARCHAR),
                            new DataValue(level2.getpQty9(), Types.VARCHAR),
                            new DataValue(level2.getDtNo10(), Types.VARCHAR),
                            new DataValue(level2.getpQty10(), Types.VARCHAR),
                            new DataValue(level2.getMulQty(), Types.VARCHAR),
                            new DataValue(level2.getFeatureNo(), Types.VARCHAR),
                            new DataValue(level2.getAdviceQty(), Types.VARCHAR)
                    };

            InsBean ib_DCP_PROCESSPLAN_DETAIL = new InsBean("DCP_PROCESSPLAN_DETAIL", columnsName_DCP_PROCESSPLAN_DETAIL);
            ib_DCP_PROCESSPLAN_DETAIL.addValues(insValue_DCP_PROCESSPLAN_DETAIL);
            this.addProcessData(new DataProcessBean(ib_DCP_PROCESSPLAN_DETAIL));

        }


        UptBean up1 = new UptBean("DCP_PROCESSPLAN");
        up1.addUpdateValue("WAREHOUSE", new DataValue(req.getRequest().getWarehouse(), Types.VARCHAR));
        up1.addUpdateValue("MATERIALWAREHOUSE", new DataValue(req.getRequest().getMaterialWarehouse(), Types.VARCHAR));
        up1.addUpdateValue("PDATE_BEGIN", new DataValue(req.getRequest().getpDateBegin(), Types.DATE));
        up1.addUpdateValue("PDATE_END", new DataValue(req.getRequest().getpDateEnd(), Types.DATE));
        up1.addUpdateValue("PTEMPLATENO", new DataValue(req.getRequest().getpTemplateNo(), Types.VARCHAR));
        up1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));
        up1.addUpdateValue("WEEKDAY1", new DataValue(req.getRequest().getWeekDay1(), Types.INTEGER));
        up1.addUpdateValue("WEEKDAY2", new DataValue(req.getRequest().getWeekDay2(), Types.INTEGER));
        up1.addUpdateValue("WEEKDAY3", new DataValue(req.getRequest().getWeekDay3(), Types.INTEGER));
        up1.addUpdateValue("WEEKDAY4", new DataValue(req.getRequest().getWeekDay4(), Types.INTEGER));
        up1.addUpdateValue("WEEKDAY5", new DataValue(req.getRequest().getWeekDay5(), Types.INTEGER));
        up1.addUpdateValue("WEEKDAY6", new DataValue(req.getRequest().getWeekDay6(), Types.INTEGER));
        up1.addUpdateValue("WEEKDAY7", new DataValue(req.getRequest().getWeekDay7(), Types.INTEGER));
        up1.addUpdateValue("AUTOCREATETASK", new DataValue(req.getRequest().getAutoCreateTask(), Types.INTEGER));
        up1.addUpdateValue("TOT_CQTY", new DataValue(TOT_CQTY, Types.VARCHAR));
        up1.addUpdateValue("TOT_PQTY", new DataValue(bdm_TOT_PQTY.toPlainString(), Types.VARCHAR));
        up1.addUpdateValue("TOT_AMT", new DataValue(bdm_TOT_AMT.toPlainString(), Types.VARCHAR));
        up1.addUpdateValue("TOT_DISTRIAMT", new DataValue(bdm_TOT_DISTRIAMT.toPlainString(), Types.VARCHAR));
        up1.addUpdateValue("MODIFYBY", new DataValue(req.getRequest().getOpNo(), Types.VARCHAR));
        up1.addUpdateValue("MODIFY_DATE", new DataValue(sysDate, Types.VARCHAR));
        up1.addUpdateValue("MODIFY_TIME", new DataValue(sysTime, Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPID", new DataValue(req.getRequest().getOpNo(), Types.VARCHAR));
        up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));


        up1.addCondition("EID", new DataValue(req.getRequest().geteId(),Types.VARCHAR));
        up1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(),Types.VARCHAR));
        up1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(),Types.VARCHAR));
        up1.addCondition("PROCESSPLANNO", new DataValue(processplanno,Types.VARCHAR));

        this.addProcessData(new DataProcessBean(up1));


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessPlanUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessPlanUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessPlanUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessPlanUpdateReq req) throws Exception
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
        if(Check.Null(req.getRequest().getOpNo()))
        {
            errMsg.append("opNo不能为空值 ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getWarehouse()))
        {
            errMsg.append("warehouse不能为空值 ");
            isFail = true;
        }
        if(Check.Null(req.getRequest().getMaterialWarehouse()))
        {
            errMsg.append("materialWarehouse不能为空值 ");
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

        if(Check.Null(req.getRequest().getAutoCreateTask()))
        {
            errMsg.append("autoCreateTask不能为空值 ");
            isFail = true;
        }

        if (req.getRequest().getDetailList()==null ||req.getRequest().getDetailList().size()==0)
        {
            errMsg.append("detailList不能为空值 ");
            isFail = true;
        }
        else
        {
            for (DCP_ProcessPlanUpdateReq.level2 level2 : req.getRequest().getDetailList())
            {
                if(Check.Null(level2.getItem()))
                {
                    errMsg.append("item不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getPluNo()))
                {
                    errMsg.append("pluNo不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getPluName()))
                {
                    errMsg.append("pluName不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getpUnit()))
                {
                    errMsg.append("pUnit不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getpQty()))
                {
                    errMsg.append("pQty不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getPrice()))
                {
                    errMsg.append("price不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getAmt()))
                {
                    errMsg.append("amt不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getDistriPrice()))
                {
                    errMsg.append("distriPrice不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getDistriAmt()))
                {
                    errMsg.append("distriAmt不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getBaseQty()))
                {
                    errMsg.append("baseQty不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getBaseUnit()))
                {
                    errMsg.append("baseUnit不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getFeatureNo()))
                {
                    level2.setFeatureNo(" ");
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
    protected TypeToken<DCP_ProcessPlanUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessPlanUpdateReq>(){};
    }

    @Override
    protected DCP_ProcessPlanUpdateRes getResponseType()
    {
        return new DCP_ProcessPlanUpdateRes();
    }



}
