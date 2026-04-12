package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.MES_SalesReturnCreateReq;
import com.dsc.spos.json.cust.res.MES_SalesReturnCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

;

/**
 * <p>
 * 销退单创建
 * </p>
 *
 */

public class MES_SalesReturnCreate extends SPosAdvanceService<MES_SalesReturnCreateReq, MES_SalesReturnCreateRes>
{
        Logger logger = LogManager.getLogger(MES_SalesReturnCreate.class.getName());

        @Override
        protected void processDUID(MES_SalesReturnCreateReq req, MES_SalesReturnCreateRes res) throws Exception
        {

                String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String bdate =	new SimpleDateFormat("yyyyMMdd").format(new Date());


                String sql="select * from MES_SALESRETURN where eid='"+req.getRequest().getEId()+"' " +
                        "and ORGANIZATIONNO='"+req.getRequest().getOrganizationNo()+"' " +
                        "and OFNO='"+req.getRequest().getErpSaleNo()+"' ";

                List<Map<String, Object>>  data=this.doQueryData(sql, null);

                if (data != null && data.size()>0)
                {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");
                        res.setDoc_no(data.get(0).get("OFNO").toString());
                        res.setOrg_no(data.get(0).get("ORGANIZATIONNO").toString());
                        return;
                }

                String  billNo= PosPub.getBillNo(dao, req.getRequest().getEId(), req.getRequest().getOrganizationNo(), "XHTH");
                int detail_item=0;

                String[] columnsName_MES_SALESRETURN_DETAIL = {
                        "EID","ORGANIZATIONNO","SALENO","OFNO","ITEM","OITEM","PLUNO","FEATURENO",
                        "PLUNAME","UNIT","PQTY","AQTY","BASEUNIT","BASEQTY","WAREHOUSENO",
                        "STATUS","BATCHNO"
                };
                for (MES_SalesReturnCreateReq.DetailList detail : req.getRequest().getDetailList())
                {

                        detail_item+=1;
                        DataValue[] insValue_MES_SALESRETURN_DETAIL = new DataValue[]
                                {
                                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(billNo, Types.VARCHAR),
                                        new DataValue(req.getRequest().getErpSaleNo(), Types.VARCHAR),
                                        new DataValue(detail_item, Types.INTEGER),
                                        new DataValue(detail.getErpItem(), Types.INTEGER),
                                        new DataValue(detail.getPluNo(), Types.VARCHAR),
                                        new DataValue(detail.getFeatureNo(), Types.VARCHAR),
                                        new DataValue(detail.getPluName(), Types.VARCHAR),
                                        new DataValue(detail.getUnit(), Types.VARCHAR),
                                        new DataValue(detail.getPQty(), Types.DOUBLE),
                                        new DataValue(0, Types.DOUBLE),
                                        new DataValue(detail.getBaseUnit(), Types.VARCHAR),
                                        new DataValue(detail.getBaseQty(), Types.DOUBLE),
                                        new DataValue(detail.getWarehouseNo(), Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(detail.getBatchNo(), Types.VARCHAR)
                                };
                        InsBean ib_MES_SALESRETURN_DETAIL = new InsBean("MES_SALESRETURN_DETAIL", columnsName_MES_SALESRETURN_DETAIL);
                        ib_MES_SALESRETURN_DETAIL.addValues(insValue_MES_SALESRETURN_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_MES_SALESRETURN_DETAIL));

                }

                //
                String[] columnsName_MES_SALESRETURN = {
                        "EID","ORGANIZATIONNO","SALENO","OFNO","BDATE",
                        "CUSTOMER","SALESMAN","DEPARTMENT","SALESTELE","MEMO","TOT_QTY","TOT_CQTY","CREATEOPID",
                        "CREATEOPNAME","CREATETIME","STATUS","LASTMODTIME"
                };

                DataValue[] insValue_MES_SALESRETURN = new DataValue[]
                        {
                                new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                new DataValue(billNo, Types.VARCHAR),
                                new DataValue(req.getRequest().getErpSaleNo(), Types.VARCHAR),
                                new DataValue(req.getRequest().getBDate(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCustomer(), Types.VARCHAR),
                                new DataValue(req.getRequest().getSalesMan(), Types.VARCHAR),
                                new DataValue(req.getRequest().getDepartment(), Types.VARCHAR),
                                new DataValue(req.getRequest().getSalesManTel(), Types.VARCHAR),
                                new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
                                new DataValue(req.getRequest().getTot_Qty(), Types.DOUBLE),
                                new DataValue(req.getRequest().getTot_CQty(), Types.DOUBLE),
                                new DataValue(req.getRequest().getCreateBy(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateByName(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateTime(), Types.DATE),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE),
                        };
                InsBean ib_MES_SALESRETURN = new InsBean("MES_SALESRETURN", columnsName_MES_SALESRETURN);
                ib_MES_SALESRETURN.addValues(insValue_MES_SALESRETURN);
                this.addProcessData(new DataProcessBean(ib_MES_SALESRETURN));



                this.doExecuteDataToDB();

                logger.info("\n*********MES_SalesReturnCreate MES ERP 下发销退单创建成功erpSaleNo="+req.getRequest().getErpSaleNo()+"************\n");

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                res.setDoc_no("");
                res.setOrg_no(req.getRequest().getOrganizationNo());
        }

        @Override
        protected List<InsBean> prepareInsertData(MES_SalesReturnCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected List<UptBean> prepareUpdateData(MES_SalesReturnCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected List<DelBean> prepareDeleteData(MES_SalesReturnCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected boolean isVerifyFail(MES_SalesReturnCreateReq req) throws Exception
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
                        if (Check.Null(req.getRequest().getErpSaleNo()))
                        {
                                isFail = true;
                                errMsg.append("erpSaleNo不能为空,");
                        }
                        if (Check.Null(req.getRequest().getBDate()))
                        {
                                isFail = true;
                                errMsg.append("bDate不能为空,");
                        }
                        if (Check.Null(req.getRequest().getCustomer()))
                        {
                                isFail = true;
                                errMsg.append("customer不能为空,");
                        }
                        if (Check.Null(req.getRequest().getSalesMan()))
                        {
                                isFail = true;
                                errMsg.append("salesMan不能为空,");
                        }
                        if (Check.Null(req.getRequest().getSalesManTel()))
                        {
                                isFail = true;
                                errMsg.append("salesManTel不能为空,");
                        }
                        if (Check.Null(req.getRequest().getDepartment()))
                        {
                                isFail = true;
                                errMsg.append("department不能为空,");
                        }
                        if (Check.Null(req.getRequest().getMemo()))
                        {
                                isFail = true;
                                errMsg.append("memo不能为空,");
                        }
                        if (Check.Null(req.getRequest().getTot_Qty()))
                        {
                                isFail = true;
                                errMsg.append("tot_Qty不能为空,");
                        }
                        if (Check.Null(req.getRequest().getTot_CQty()))
                        {
                                isFail = true;
                                errMsg.append("tot_CQty不能为空,");
                        }
                        if (Check.Null(req.getRequest().getCreateBy()))
                        {
                                isFail = true;
                                errMsg.append("createBy不能为空,");
                        }
                        if (Check.Null(req.getRequest().getCreateByName()))
                        {
                                isFail = true;
                                errMsg.append("createByName不能为空,");
                        }
                        if (Check.Null(req.getRequest().getCreateTime()))
                        {
                                isFail = true;
                                errMsg.append("createTime不能为空,");
                        }

                        if (req.getRequest().getDetailList()==null || req.getRequest().getDetailList().size()==0)
                        {
                                isFail = true;
                                errMsg.append("detailList不能为空,");
                        }
                        else
                        {

                                for (MES_SalesReturnCreateReq.DetailList detail : req.getRequest().getDetailList())
                                {
                                        if (Check.Null(detail.getErpItem()))
                                        {
                                                isFail = true;
                                                errMsg.append("erpItem不能为空,");
                                        }
                                        if (Check.Null(detail.getPluNo()))
                                        {
                                                isFail = true;
                                                errMsg.append("pluNo不能为空,");
                                        }
                                        if (Check.Null(detail.getPluName()))
                                        {
                                                isFail = true;
                                                errMsg.append("pluName不能为空,");
                                        }
                                        if (Check.Null(detail.getUnit()))
                                        {
                                                isFail = true;
                                                errMsg.append("unit不能为空,");
                                        }
                                        if (Check.Null(detail.getFeatureNo()))
                                        {
                                                isFail = true;
                                                errMsg.append("featureNo不能为空,");
                                        }
                                        if (Check.Null(detail.getPQty()))
                                        {
                                                isFail = true;
                                                errMsg.append("pQty不能为空,");
                                        }
                                        if (Check.Null(detail.getBaseQty()))
                                        {
                                                isFail = true;
                                                errMsg.append("baseQty不能为空,");
                                        }
                                        if (Check.Null(detail.getBaseUnit()))
                                        {
                                                isFail = true;
                                                errMsg.append("baseUnit不能为空,");
                                        }
                                        if (Check.Null(detail.getWarehouseNo()))
                                        {
                                                isFail = true;
                                                errMsg.append("warehouseNo不能为空,");
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
        protected TypeToken<MES_SalesReturnCreateReq> getRequestType()
        {
                return new TypeToken<MES_SalesReturnCreateReq>(){};
        }

        @Override
        protected MES_SalesReturnCreateRes getResponseType()
        {
                return new MES_SalesReturnCreateRes();
        }
}
