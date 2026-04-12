package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;

import com.dsc.spos.scheduler.job.AutoSStockIn;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.utils.Check;
import com.dsc.spos.json.cust.req.MES_ErpStockTakeAddReq;
import com.dsc.spos.json.cust.res.MES_ErpStockTakeAddRes;

import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.SPosAdvanceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * erp下发盘点单
 * </p>
 *
 */

public class MES_ErpStockTakeAdd extends SPosAdvanceService<MES_ErpStockTakeAddReq, MES_ErpStockTakeAddRes>
{
    Logger logger = LogManager.getLogger(MES_ErpStockTakeAdd.class.getName());

    @Override
    protected void processDUID(MES_ErpStockTakeAddReq req, MES_ErpStockTakeAddRes res) throws Exception
    {

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


        String sql="select * from DCP_STOCKTAKE where eid='"+req.getRequest().getEId()+"' " +
                "and LOAD_DOCNO='"+req.getRequest().getOfNo()+"' " ;

        List<Map<String, Object>>  datas_stocktake=this.doQueryData(sql, null);

        if (datas_stocktake != null && datas_stocktake.size()>0)
        {
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "docNo单号已经存在！");
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

            res.setDoc_no(datas_stocktake.get(0).get("STOCKTAKENO").toString());
            res.setOrg_no(datas_stocktake.get(0).get("SHOPID").toString());
            return;
        }


        String bdate =	new SimpleDateFormat("yyyyMMdd").format(new Date());
        String stime =	new SimpleDateFormat("HHmmss").format(new Date());
        String uptime =	new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        String billNo="";
        List<MES_ErpStockTakeAddReq.DetailList> detailList =req.getRequest().getDetailList();
        if (detailList != null && detailList.size()>0)
        {

            billNo= PosPub.getBillNo(dao,req.getRequest().getEId(),req.getRequest().getOrganizationNo(),"KCPD");

            int detail_item=0;

            for (MES_ErpStockTakeAddReq.DetailList data : detailList)
            {

                //原本规格是有批号和库位的，但是ERP不支持，全是******
                //现在改成按照批号库存表1笔商品展开成多笔批号+库位记录
                //过滤0的，0是没库存的，很久以前的不要了，不然太多了

                String sql_batch="select * from MES_BATCH_STOCK_DETAIL a  " +
                        "where a.eid='"+req.getRequest().getEId()+"' " +
                        "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                        "and a.warehouse='"+req.getRequest().getWarehouseNo()+"' " +
                        "and a.pluno='"+data.getPluNo()+"' " +
                        "and a.featureno='"+data.getFeatureNo()+"' " +
                        "and a.qty<>0 ";
                List<Map<String, Object>>  datas_batch=this.doQueryData(sql_batch, null);
                if (datas_batch != null && datas_batch.size()>0)
                {
                    for (Map<String, Object> datasBatch : datas_batch)
                    {
                        detail_item+=1;


                        //1、盘点明细
                        String[] columnsName_DCP_STOCKTAKE_DETAIL = {
                                "EID","ORGANIZATIONNO","SHOPID","STOCKTAKENO","WAREHOUSE","ITEM","PLUNO",
                                "BATCH_NO","PROD_DATE","BASEUNIT","OITEM","PQTY","REF_BASEQTY","PRICE",
                                "DISTRIPRICE","BASEQTY","AMT","DISTRIAMT","PUNIT","UNIT_RATIO"
                                ,"MEMO","BDATE","FEATURENO","FQTY","FBASEQTY","RQTY","RBASEQTY","PARTITION_DATE","LOCATION"
                        };
                        DataValue[] insValue_DCP_STOCKTAKE_DETAIL = new DataValue[]
                                {
                                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(billNo, Types.VARCHAR),
                                        new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR),
                                        new DataValue(detail_item, Types.VARCHAR),
                                        new DataValue(data.getPluNo(), Types.VARCHAR),
                                        new DataValue(datasBatch.get("BATCHNO").toString(), Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(data.getBaseUnit(), Types.VARCHAR),
                                        new DataValue(data.getOItem(), Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(data.getDistriPrice(), Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(data.getBaseUnit(), Types.VARCHAR),
                                        new DataValue(1, Types.VARCHAR),//UNIT_RATIO
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(data.getBDate(), Types.VARCHAR),
                                        new DataValue(data.getFeatureNo(), Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(bdate, Types.VARCHAR),
                                        new DataValue(datasBatch.get("LOCATION").toString(), Types.VARCHAR)
                                };
                        InsBean ib_DCP_STOCKTAKE_DETAIL = new InsBean("DCP_STOCKTAKE_DETAIL", columnsName_DCP_STOCKTAKE_DETAIL);
                        ib_DCP_STOCKTAKE_DETAIL.addValues(insValue_DCP_STOCKTAKE_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_DCP_STOCKTAKE_DETAIL));


                        //2、多单位
                        List<MES_ErpStockTakeAddReq.UnitList> unitList=data.getUnitList();
                        if (unitList != null && unitList.size()>0)
                        {
                            int unit_item=0;
                            for (MES_ErpStockTakeAddReq.UnitList unit : unitList)
                            {
                                unit_item+=1;

                                String[] columnsName_DCP_STOCKTAKE_DETAIL_UNIT = {
                                        "EID","ORGANIZATIONNO","SHOPID","STOCKTAKENO","ITEM",
                                        "OITEM","PQTY","PUNIT","PARTITION_DATE","UNIT_RATIO"
                                };
                                DataValue[] insValue_DCP_STOCKTAKE_DETAIL_UNIT = new DataValue[]
                                        {
                                                new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                                new DataValue(billNo, Types.VARCHAR),
                                                new DataValue(unit_item, Types.INTEGER),
                                                new DataValue(detail_item, Types.INTEGER),
                                                new DataValue(0, Types.FLOAT),
                                                new DataValue(unit.getPUnit(), Types.VARCHAR),
                                                new DataValue(bdate, Types.VARCHAR),
                                                new DataValue(unit.getUnitRatio(), Types.VARCHAR)
                                        };
                                InsBean ib_DCP_STOCKTAKE_DETAIL_UNIT = new InsBean("DCP_STOCKTAKE_DETAIL_UNIT", columnsName_DCP_STOCKTAKE_DETAIL_UNIT);
                                ib_DCP_STOCKTAKE_DETAIL_UNIT.addValues(insValue_DCP_STOCKTAKE_DETAIL_UNIT);
                                this.addProcessData(new DataProcessBean(ib_DCP_STOCKTAKE_DETAIL_UNIT));
                            }
                        }
                    }
                }
                else
                {
                    //查不到记录：批号和库位都给空格

                    detail_item+=1;


                    //1、盘点明细
                    String[] columnsName_DCP_STOCKTAKE_DETAIL = {
                            "EID","ORGANIZATIONNO","SHOPID","STOCKTAKENO","WAREHOUSE","ITEM","PLUNO",
                            "BATCH_NO","PROD_DATE","BASEUNIT","OITEM","PQTY","REF_BASEQTY","PRICE",
                            "DISTRIPRICE","BASEQTY","AMT","DISTRIAMT","PUNIT","UNIT_RATIO"
                            ,"MEMO","BDATE","FEATURENO","FQTY","FBASEQTY","RQTY","RBASEQTY","PARTITION_DATE","LOCATION"
                    };
                    DataValue[] insValue_DCP_STOCKTAKE_DETAIL = new DataValue[]
                            {
                                    new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                    new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                    new DataValue(billNo, Types.VARCHAR),
                                    new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR),
                                    new DataValue(detail_item, Types.VARCHAR),
                                    new DataValue(data.getPluNo(), Types.VARCHAR),
                                    new DataValue(" ", Types.VARCHAR),//批号给空
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(data.getBaseUnit(), Types.VARCHAR),
                                    new DataValue(data.getOItem(), Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(data.getDistriPrice(), Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(data.getBaseUnit(), Types.VARCHAR),
                                    new DataValue(1, Types.VARCHAR),//UNIT_RATIO
                                    new DataValue("", Types.VARCHAR),
                                    new DataValue(data.getBDate(), Types.VARCHAR),
                                    new DataValue(data.getFeatureNo(), Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(0, Types.VARCHAR),
                                    new DataValue(bdate, Types.VARCHAR),
                                    new DataValue(" ", Types.VARCHAR)//库位也给空
                            };
                    InsBean ib_DCP_STOCKTAKE_DETAIL = new InsBean("DCP_STOCKTAKE_DETAIL", columnsName_DCP_STOCKTAKE_DETAIL);
                    ib_DCP_STOCKTAKE_DETAIL.addValues(insValue_DCP_STOCKTAKE_DETAIL);
                    this.addProcessData(new DataProcessBean(ib_DCP_STOCKTAKE_DETAIL));


                    //2、多单位
                    List<MES_ErpStockTakeAddReq.UnitList> unitList=data.getUnitList();
                    if (unitList != null && unitList.size()>0)
                    {
                        int unit_item=0;
                        for (MES_ErpStockTakeAddReq.UnitList unit : unitList)
                        {
                            unit_item+=1;

                            String[] columnsName_DCP_STOCKTAKE_DETAIL_UNIT = {
                                    "EID","ORGANIZATIONNO","SHOPID","STOCKTAKENO","ITEM",
                                    "OITEM","PQTY","PUNIT","PARTITION_DATE","UNIT_RATIO"
                            };
                            DataValue[] insValue_DCP_STOCKTAKE_DETAIL_UNIT = new DataValue[]
                                    {
                                            new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                            new DataValue(billNo, Types.VARCHAR),
                                            new DataValue(unit_item, Types.INTEGER),
                                            new DataValue(detail_item, Types.INTEGER),
                                            new DataValue(0, Types.FLOAT),
                                            new DataValue(unit.getPUnit(), Types.VARCHAR),
                                            new DataValue(bdate, Types.VARCHAR),
                                            new DataValue(unit.getUnitRatio(), Types.VARCHAR)
                                    };
                            InsBean ib_DCP_STOCKTAKE_DETAIL_UNIT = new InsBean("DCP_STOCKTAKE_DETAIL_UNIT", columnsName_DCP_STOCKTAKE_DETAIL_UNIT);
                            ib_DCP_STOCKTAKE_DETAIL_UNIT.addValues(insValue_DCP_STOCKTAKE_DETAIL_UNIT);
                            this.addProcessData(new DataProcessBean(ib_DCP_STOCKTAKE_DETAIL_UNIT));
                        }
                    }
                }







            }



            //3、盘点单头
            String[] columnsName_DCP_STOCKTAKE = {
                    "EID","ORGANIZATIONNO","SHOPID","WAREHOUSE","STOCKTAKE_ID","STOCKTAKENO",
                    "CREATEBY","CREATE_DATE","CREATE_TIME",
                    "OTYPE","DOC_TYPE","MEMO","LOAD_DOCTYPE","LOAD_DOCNO","TOT_CQTY","BDATE","IS_BTAKE",
                    "PROCESS_STATUS","TOT_PQTY","TOT_AMT","TOT_DISTRIAMT","OFNO","PTEMPLATENO","TASKWAY",
                    "NOTGOODSMODE","IS_ADJUST_STOCK","UPDATE_TIME","PROCESS_ERP_NO","PROCESS_ERP_ORG",
                    "STATUS","SUBSTOCKIMPORT","CREATE_CHATUSERID","MODIFY_CHATUSERID",
                    "ACCOUNT_CHATUSERID","PARTITION_DATE"

            };

            DataValue[] insValue_DCP_STOCKTAKE = new DataValue[]
                    {
                            new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR),
                            new DataValue(PosPub.getGUID(false), Types.VARCHAR),
                            new DataValue(billNo, Types.VARCHAR),
                            new DataValue(req.getOpNO(), Types.VARCHAR),
                            new DataValue(bdate, Types.VARCHAR),
                            new DataValue(stime, Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),//OTYPE
                            new DataValue("0", Types.VARCHAR),//DOC_TYPE
                            new DataValue("", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue(req.getRequest().getOfNo(), Types.VARCHAR),
                            new DataValue(req.getRequest().getTotCQty(), Types.VARCHAR),
                            new DataValue(bdate, Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(req.getRequest().getTotQty(), Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue(req.getRequest().getOfNo(), Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),//TASKWAY
                            new DataValue("N", Types.VARCHAR),
                            new DataValue("Y", Types.VARCHAR),
                            new DataValue(uptime, Types.VARCHAR),
                            new DataValue(req.getRequest().getOfNo(), Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),//STATUS
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(bdate, Types.VARCHAR)
                    };
            InsBean ib_DCP_STOCKTAKE = new InsBean("DCP_STOCKTAKE", columnsName_DCP_STOCKTAKE);
            ib_DCP_STOCKTAKE.addValues(insValue_DCP_STOCKTAKE);
            this.addProcessData(new DataProcessBean(ib_DCP_STOCKTAKE));

        }

        this.doExecuteDataToDB();

        logger.info(" *********MES_ErpStockTakeAdd MES ERP 下发盘点单成功erpno="+req.getRequest().getOfNo()+"************ ");


        res.setDoc_no(billNo);
        res.setOrg_no(req.getOrganizationNO());

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(MES_ErpStockTakeAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(MES_ErpStockTakeAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(MES_ErpStockTakeAddReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(MES_ErpStockTakeAddReq req) throws Exception
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
            if (Check.Null(req.getRequest().getBDate()))
            {
                isFail = true;
                errMsg.append("bDate不能为空,");
            }
            if (Check.Null(req.getRequest().getOrganizationNo()))
            {
                isFail = true;
                errMsg.append("organizationNo不能为空,");
            }
            if (Check.Null(req.getRequest().getWarehouseNo()))
            {
                isFail = true;
                errMsg.append("warehouseNo不能为空,");
            }
            if (Check.Null(req.getRequest().getOfNo()))
            {
                isFail = true;
                errMsg.append("ofNo不能为空,");
            }
            if (Check.Null(req.getRequest().getStatus()))
            {
                isFail = true;
                errMsg.append("status不能为空,");
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

            List<MES_ErpStockTakeAddReq.DetailList> detailList =req.getRequest().getDetailList();

            if (detailList == null || detailList.size()==0)
            {
                isFail = true;
                errMsg.append("detailList不能为空,");
            }
            else
            {
                for (MES_ErpStockTakeAddReq.DetailList data : detailList)
                {
                    if (Check.Null(data.getBDate()))
                    {
                        isFail = true;
                        errMsg.append("bDate不能为空,");
                    }
                    if (Check.Null(data.getDistriPrice()))
                    {
                        isFail = true;
                        errMsg.append("distriPrice不能为空,");
                    }
                    if (Check.Null(data.getFeatureNo()))
                    {
                        data.setFeatureNo(" ");
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
                    if (Check.Null(data.getBaseUnit()))
                    {
                        isFail = true;
                        errMsg.append("baseUnit不能为空,");
                    }

                    List<MES_ErpStockTakeAddReq.UnitList> unitList=data.getUnitList();
                    if (unitList != null && unitList.size()>0)
                    {
                        for (MES_ErpStockTakeAddReq.UnitList unit : unitList)
                        {
                            if (Check.Null(unit.getOItem()))
                            {
                                isFail = true;
                                errMsg.append("unitList.oItem不能为空,");
                            }
                            if (Check.Null(unit.getPUnit()))
                            {
                                isFail = true;
                                errMsg.append("unitList.pUnit不能为空,");
                            }
                            if (Check.Null(unit.getUnitRatio()))
                            {
                                isFail = true;
                                errMsg.append("unitList.unitRatio不能为空,");
                            }
                        }
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
    protected TypeToken<MES_ErpStockTakeAddReq> getRequestType()
    {
        return new TypeToken<MES_ErpStockTakeAddReq>(){};
    }

    @Override
    protected MES_ErpStockTakeAddRes getResponseType()
    {
        return new MES_ErpStockTakeAddRes();
    }
}
