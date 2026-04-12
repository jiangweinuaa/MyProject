package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessTask0UpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTask0UpdateRes;
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

public class DCP_ProcessTask0Update extends SPosAdvanceService<DCP_ProcessTask0UpdateReq, DCP_ProcessTask0UpdateRes>
{


    @Override
    protected void processDUID(DCP_ProcessTask0UpdateReq req, DCP_ProcessTask0UpdateRes res) throws Exception
    {

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sysDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sysTime =	new SimpleDateFormat("HHmmss").format(new Date());


        //单日加工任务单
        String processTaskno= req.getRequest().getProcessTaskNo();

        //
        String sql_Exist="select EID from DCP_PROCESSTASK0 a  " +
                "where a.eid='"+req.getRequest().geteId()+"' " +
                "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                "and a.PROCESSTASKNO='"+processTaskno+"' ";
        List<Map<String, Object>> getQData_Exist = this.doQueryData(sql_Exist, null);
        if (getQData_Exist == null || getQData_Exist.size()==0)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找不到记录！");
        }

        //SA说先删后插
        DelBean db1 = new DelBean("DCP_PROCESSTASK0_DETAIL");
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db1.addCondition("PROCESSTASKNO", new DataValue(processTaskno, Types.VARCHAR));

        this.addProcessData(new DataProcessBean(db1));


        db1 = new DelBean("DCP_PROCESSTASK0_SOURCE");
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db1.addCondition("ORGANIZATIONNO", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db1.addCondition("SHOPID", new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR));
        db1.addCondition("PROCESSTASKNO", new DataValue(processTaskno, Types.VARCHAR));

        this.addProcessData(new DataProcessBean(db1));


        //
        long TOT_CQTY=req.getRequest().getDetailList().stream().filter(PosPub.distinctByKeys(p->p.getPluNo())).count();

        BigDecimal bdm_TOT_PQTY=new BigDecimal("0");
        BigDecimal bdm_TOT_AMT=new BigDecimal("0");
        BigDecimal bdm_TOT_DISTRIAMT=new BigDecimal("0");


        String[] columnsName_DCP_PROCESSTASK0_DETAIL = {
                "EID","ORGANIZATIONNO","SHOPID","PROCESSTASKNO","ITEM","PQTY","PUNIT","BASEQTY",
                "PLUNO","PRICE","BASEUNIT","UNIT_RATIO","AMT","DISTRIPRICE","DISTRIAMT","PDATE","BDATE",
                "DTNO1","PQTY1","DTNO2","PQTY2","DTNO3","PQTY3","DTNO4","PQTY4","DTNO5",
                "PQTY5","DTNO6","PQTY6","DTNO7","PQTY7","DTNO8","PQTY8","DTNO9","PQTY9",
                "DTNO10","PQTY10","MUL_QTY","ADVICEQTY","ASKQTY","FEATURENO"
        };

        String[] columnsName_DCP_PROCESSTASK0_SOURCE = {
                "EID","ORGANIZATIONNO","SHOPID","PROCESSTASKNO","ITEM","MITEM",
                "OTYPE", "OFNO","OSHOP","OITEM","PLUNO","PUNIT","ASKQTY","SHAREQTY"

        };

        int detailItem=0;

        for (DCP_ProcessTask0UpdateReq.level2 level2 : req.getRequest().getDetailList())
        {

            bdm_TOT_PQTY=bdm_TOT_PQTY.add(new BigDecimal(level2.getpQty()));
            bdm_TOT_AMT=bdm_TOT_AMT.add(new BigDecimal(level2.getAmt()));
            bdm_TOT_DISTRIAMT=bdm_TOT_DISTRIAMT.add(new BigDecimal(level2.getDistriAmt()));


            DataValue[] insValue_DCP_PROCESSTASK0_DETAIL = new DataValue[]
                    {
                            new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(processTaskno, Types.VARCHAR),
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
                            new DataValue(level2.getAdviceQty(), Types.VARCHAR),
                            new DataValue(level2.getAskQty(), Types.VARCHAR),
                            new DataValue(level2.getFeatureNo(), Types.VARCHAR)
                    };

            InsBean ib_DCP_PROCESSTASK0_DETAIL = new InsBean("DCP_PROCESSTASK0_DETAIL", columnsName_DCP_PROCESSTASK0_DETAIL);
            ib_DCP_PROCESSTASK0_DETAIL.addValues(insValue_DCP_PROCESSTASK0_DETAIL);
            this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK0_DETAIL));


            //分摊来源单
            if (level2.getSourceList() != null && level2.getSourceList().size()>0)
            {
                for (DCP_ProcessTask0UpdateReq.level3 level3 : level2.getSourceList())
                {
                    detailItem+=1;

                    DataValue[] insValue_DCP_PROCESSTASK0_SOURCE = new DataValue[]
                            {
                                    new DataValue(req.getRequest().geteId(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                    new DataValue(processTaskno, Types.VARCHAR),
                                    new DataValue(detailItem, Types.VARCHAR),
                                    new DataValue(level2.getItem(), Types.VARCHAR),
                                    new DataValue(level3.getoType(), Types.VARCHAR),
                                    new DataValue(level3.getoFNo(), Types.VARCHAR),
                                    new DataValue(level3.getoShop(), Types.VARCHAR),
                                    new DataValue(level3.getoItem(), Types.VARCHAR),
                                    new DataValue(level3.getPluNo(), Types.VARCHAR),
                                    new DataValue(level3.getpUnit(), Types.VARCHAR),
                                    new DataValue(level3.getAskQty(), Types.VARCHAR),
                                    new DataValue(level3.getShareQty(), Types.VARCHAR)
                            };

                    InsBean ib_DCP_PROCESSTASK0_SOURCE = new InsBean("DCP_PROCESSTASK0_SOURCE", columnsName_DCP_PROCESSTASK0_SOURCE);
                    ib_DCP_PROCESSTASK0_SOURCE.addValues(insValue_DCP_PROCESSTASK0_SOURCE);
                    this.addProcessData(new DataProcessBean(ib_DCP_PROCESSTASK0_SOURCE));
                }
            }

        }


        UptBean up1 = new UptBean("DCP_PROCESSTASK0");
        up1.addUpdateValue("WAREHOUSE", new DataValue(req.getRequest().getWarehouse(), Types.VARCHAR));
        up1.addUpdateValue("MATERIALWAREHOUSE", new DataValue(req.getRequest().getMaterialWarehouse(), Types.VARCHAR));
        up1.addUpdateValue("PDATE", new DataValue(req.getRequest().getpDate(), Types.VARCHAR));
        up1.addUpdateValue("PTEMPLATENO", new DataValue(req.getRequest().getpTemplateNo(), Types.VARCHAR));
        up1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));
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
        up1.addCondition("PROCESSTASKNO", new DataValue(processTaskno,Types.VARCHAR));

        this.addProcessData(new DataProcessBean(up1));


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");



    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessTask0UpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessTask0UpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessTask0UpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProcessTask0UpdateReq req) throws Exception
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
        if(Check.Null(req.getRequest().getpDate()))
        {
            errMsg.append("pDate不能为空值 ");
            isFail = true;
        }

        if (req.getRequest().getDetailList()==null ||req.getRequest().getDetailList().size()==0)
        {
            errMsg.append("detailList不能为空值 ");
            isFail = true;
        }
        else
        {
            for (DCP_ProcessTask0UpdateReq.level2 level2 : req.getRequest().getDetailList())
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
                if(Check.Null(level2.getAdviceQty()))
                {
                    errMsg.append("adviceQty不能为空值 ");
                    isFail = true;
                }
                if(Check.Null(level2.getAskQty()))
                {
                    errMsg.append("askQty不能为空值 ");
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
    protected TypeToken<DCP_ProcessTask0UpdateReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessTask0UpdateReq>(){};
    }

    @Override
    protected DCP_ProcessTask0UpdateRes getResponseType()
    {
        return new DCP_ProcessTask0UpdateRes();
    }


}
