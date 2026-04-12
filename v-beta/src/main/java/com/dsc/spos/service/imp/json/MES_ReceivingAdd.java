package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.MES_ReceivingAddReq;
import com.dsc.spos.json.cust.res.MES_ReceivingAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ERP下发的采购通知单
 */
public class MES_ReceivingAdd extends SPosAdvanceService<MES_ReceivingAddReq, MES_ReceivingAddRes>
{
    Logger logger = LogManager.getLogger(MES_ReceivingAdd.class.getName());

    @Override
    protected void processDUID(MES_ReceivingAddReq req, MES_ReceivingAddRes res) throws Exception
    {

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sql="select * from MES_RECEIVING where eid='"+req.getRequest().getEId()+"' " +
                "and OFNO='"+req.getRequest().getDocNo()+"' " ;

        List<Map<String, Object>>  data=this.doQueryData(sql, null);

        if (data != null && data.size()>0)
        {
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "docNo单号已经存在！");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setDoc_no(data.get(0).get("RECEIVINGNO").toString());
            res.setOrg_no(data.get(0).get("ORGANIZATIONNO").toString());
            return;
        }

        //产生单号
        String billno= PosPub.getBillNo(dao,req.getRequest().getEId(),req.getRequest().getOrganizationNo(),"SHTZ");


        List<MES_ReceivingAddReq.level1> datas=req.getRequest().getDatas();

        if (datas != null && datas.size()>0)
        {

            int detail_item=0;

            for (MES_ReceivingAddReq.level1 level1 : datas)
            {

                //
                detail_item+=1;

                //1、单身
                String[] columnsName_MES_RECEIVING_DETAIL = {
                        "EID","ORGANIZATIONNO","RECEIVINGNO","WAREHOUSENO","OITEM","ITEM","RDATE","PLUNO","FEATURENO",
                        "QTY","AQTY","WQTY","REQTY","UNIT","BASEUNIT","BASEQTY","UNITRATIO","DISTRIPRICE","DISTRIAMT",
                        "ISGIVE"
                };


                String v_date=null;
                if (!Check.Null(level1.getRDate()) && level1.getRDate().length()==8)
                {
                    v_date=level1.getRDate().substring(0,4)+"-"+level1.getRDate().substring(4,6) +"-"+level1.getRDate().substring(6,8);
                }

                DataValue[] insValue_MES_RECEIVING_DETAIL = new DataValue[]
                        {
                                new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                new DataValue(billno, Types.VARCHAR),
                                new DataValue(level1.getWarehouseNo(), Types.VARCHAR),
                                new DataValue(level1.getOItem(), Types.VARCHAR),
                                new DataValue(detail_item, Types.VARCHAR),
                                new DataValue(v_date, Types.DATE),
                                new DataValue(level1.getPluNo(), Types.VARCHAR),
                                new DataValue(level1.getFeatureNo(), Types.VARCHAR),
                                new DataValue(level1.getQty(), Types.VARCHAR),
                                new DataValue(0, Types.FLOAT),
                                new DataValue(0, Types.FLOAT),
                                new DataValue(0, Types.FLOAT),
                                new DataValue(level1.getUnit(), Types.VARCHAR),
                                new DataValue(level1.getBaseUnit(), Types.VARCHAR),
                                new DataValue(level1.getBaseQty(), Types.VARCHAR),
                                new DataValue(level1.getUnitRatio(), Types.VARCHAR),
                                new DataValue(level1.getDistriPrice(), Types.VARCHAR),
                                new DataValue(level1.getDistriAmount(), Types.VARCHAR),
                                new DataValue(level1.getIsGive(), Types.VARCHAR),
                        };
                InsBean ib_MES_RECEIVING_DETAIL = new InsBean("MES_RECEIVING_DETAIL", columnsName_MES_RECEIVING_DETAIL);
                ib_MES_RECEIVING_DETAIL.addValues(insValue_MES_RECEIVING_DETAIL);
                this.addProcessData(new DataProcessBean(ib_MES_RECEIVING_DETAIL));


            }


            //2、单头
            String[] columnsName_MES_RECEIVING = {
                    "EID","ORGANIZATIONNO","RECEIVINGNO","OFNO","SUPPLIER","TOT_QTY","TOT_CQTY","STATUS","OPSTATUS",
                    "DEPARTMENT","CREATEOPID","CREATEOPNAME","CREATETIME"
            };


            DataValue[] insValue_MES_RECEIVING = new DataValue[]
                    {
                            new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(billno, Types.VARCHAR),
                            new DataValue(req.getRequest().getDocNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getSupplierNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getTotQty(), Types.VARCHAR),
                            new DataValue(req.getRequest().getTotCQty(), Types.VARCHAR),
                            new DataValue(req.getRequest().getStatus(), Types.INTEGER),//状态(0新建 1确认)
                            new DataValue(0, Types.INTEGER),//（0待收货 1完成）操作状态
                            new DataValue(req.getRequest().getDepartment(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                    };
            InsBean ib_MES_RECEIVING = new InsBean("MES_RECEIVING", columnsName_MES_RECEIVING);
            ib_MES_RECEIVING.addValues(insValue_MES_RECEIVING);
            this.addProcessData(new DataProcessBean(ib_MES_RECEIVING));


        }

        this.doExecuteDataToDB();

        logger.info("\n*********MES_ReceivingAdd MES ERP 下发采购通知单成功erpno="+req.getRequest().getDocNo()+"************\n");

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        res.setDoc_no(billno);
        res.setOrg_no(req.getRequest().getOrganizationNo());
    }

    @Override
    protected List<InsBean> prepareInsertData(MES_ReceivingAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(MES_ReceivingAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(MES_ReceivingAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(MES_ReceivingAddReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
        }
        else
        {
            if (Check.Null(req.getRequest().getEId()))
            {
                isFail = true;
                errMsg.append("eId不能为空,");
            }
            if (Check.Null(req.getRequest().getOrganizationNo()))
            {
                isFail = true;
                errMsg.append("organizationNo不能为空,");
            }
            if (Check.Null(req.getRequest().getDocNo()))
            {
                isFail = true;
                errMsg.append("docNo不能为空,");
            }
            if (Check.Null(req.getRequest().getDepartment()))
            {
                isFail = true;
                errMsg.append("department不能为空,");
            }
            if (Check.Null(req.getRequest().getSupplierNo()))
            {
                isFail = true;
                errMsg.append("supplierNo不能为空,");
            }
            if (Check.Null(req.getRequest().getTotCQty()))
            {
                isFail = true;
                errMsg.append("totCQty不能为空,");
            }
            if (Check.Null(req.getRequest().getTotQty()))
            {
                isFail = true;
                errMsg.append("totQty不能为空,");
            }

            List<MES_ReceivingAddReq.level1> datas=req.getRequest().getDatas();
            if (datas == null || datas.size()==0)
            {
                isFail = true;
                errMsg.append("datas不能为空,");
            }
            else
            {
                for (MES_ReceivingAddReq.level1 data : datas)
                {
                    if (Check.Null(data.getFeatureNo()))
                    {
                        data.setFeatureNo(" ");
                    }
                    if (Check.Null(data.getBaseQty()))
                    {
                        isFail = true;
                        errMsg.append("baseQty不能为空,");
                    }
                    if (Check.Null(data.getBaseUnit()))
                    {
                        isFail = true;
                        errMsg.append("baseUnit不能为空,");
                    }
                    if (Check.Null(data.getDistriAmount()))
                    {
                        isFail = true;
                        errMsg.append("distriAmount不能为空,");
                    }
                    if (Check.Null(data.getDistriPrice()))
                    {
                        isFail = true;
                        errMsg.append("distriPrice不能为空,");
                    }

                    if (Check.Null(data.getOItem()))
                    {
                        isFail = true;
                        errMsg.append("oItem不能为空,");
                    }
                    if (Check.Null(data.getPluNo()))
                    {
                        isFail = true;
                        errMsg.append("pluNo不能为空,");
                    }
                    if (Check.Null(data.getQty()))
                    {
                        isFail = true;
                        errMsg.append("qty不能为空,");
                    }
                    if (Check.Null(data.getRDate()))
                    {
                        isFail = true;
                        errMsg.append("rDate不能为空,");
                    }
                    if (Check.Null(data.getUnit()))
                    {
                        isFail = true;
                        errMsg.append("unit不能为空,");
                    }
                    if (Check.Null(data.getUnitRatio()))
                    {
                        isFail = true;
                        errMsg.append("unitRatio不能为空,");
                    }
                    if (Check.Null(data.getWarehouseNo()))
                    {
                        //仓库在ERP不是必须，在采购的时候，一个组织下的3个仓库地址在一起，但是不知道收货到那个仓
                        //所以反正货来了，仓库人员自己收吧
                        data.setWarehouseNo("");
                    }
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
    protected TypeToken<MES_ReceivingAddReq> getRequestType()
    {
        return new TypeToken<MES_ReceivingAddReq>(){};
    }

    @Override
    protected MES_ReceivingAddRes getResponseType()
    {
        return new MES_ReceivingAddRes();
    }


}
