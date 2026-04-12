package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.MES_PurchaseReturnCreateReq;
import com.dsc.spos.json.cust.res.MES_PurchaseReturnCreateRes;
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
 * 采退单创建
 * </p>
 *
 */

public class MES_PurchaseReturnCreate extends SPosAdvanceService<MES_PurchaseReturnCreateReq, MES_PurchaseReturnCreateRes>
{
        Logger logger = LogManager.getLogger(MES_PurchaseReturnCreate.class.getName());

        @Override
        protected void processDUID(MES_PurchaseReturnCreateReq req, MES_PurchaseReturnCreateRes res) throws Exception
        {

                String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String bdate =	new SimpleDateFormat("yyyyMMdd").format(new Date());


                String sql="select * from MES_PURCHASERETURN where eid='"+req.getRequest().getEId()+"' " +
                        "and ORGANIZATIONNO='"+req.getRequest().getOrganizationNo()+"' " +
                        "and OFNO='"+req.getRequest().getErpReturnNo()+"' ";

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

                String  billNo= PosPub.getBillNo(dao, req.getRequest().getEId(), req.getRequest().getOrganizationNo(), "CGTH");
                int detail_item=0;

                String[] columnsName_MES_PURCHASERETURN_DETAIL = {
                        "EID","ORGANIZATIONNO","RETURNNO","OFNO","ITEM","OITEM","PLUNO","FEATURENO",
                        "PLUNAME","PUNIT","PQTY","BASEUNIT","BASEQTY","WAREHOUSENO",
                        "BATCHNO","STATUS","AQTY","OOFNO","OOITEM"
                };
                for (MES_PurchaseReturnCreateReq.DetailList detail : req.getRequest().getDetailList())
                {

                        detail_item+=1;
                        DataValue[] insValue_MES_PURCHASERETURN_DETAIL = new DataValue[]
                                {
                                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(billNo, Types.VARCHAR),
                                        new DataValue(req.getRequest().getErpReturnNo(), Types.VARCHAR), //ofno
                                        new DataValue(detail_item, Types.INTEGER), //item
                                        new DataValue(detail.getErpItem(), Types.INTEGER), //OITEM
                                        new DataValue(detail.getPluNo(), Types.VARCHAR),
                                        new DataValue(detail.getFeatureNo(), Types.VARCHAR),
                                        new DataValue(detail.getPluName(), Types.VARCHAR),
                                        new DataValue(detail.getPUnit(), Types.VARCHAR), //PUNIT
                                        new DataValue(detail.getPQty(), Types.DOUBLE), //PQTY
                                        new DataValue(detail.getBaseUnit(), Types.VARCHAR),
                                        new DataValue(detail.getBaseQty(), Types.DOUBLE),
                                        new DataValue(detail.getWarehouseNo(), Types.VARCHAR),
                                        new DataValue(detail.getBatchNo(), Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(0, Types.DOUBLE),
                                        new DataValue(detail.getSourceStockInNo(), Types.VARCHAR),
                                        new DataValue(detail.getSourceStockInItem(), Types.VARCHAR),
                                };
                        InsBean ib_MES_PURCHASERETURN_DETAIL = new InsBean("MES_PURCHASERETURN_DETAIL", columnsName_MES_PURCHASERETURN_DETAIL);
                        ib_MES_PURCHASERETURN_DETAIL.addValues(insValue_MES_PURCHASERETURN_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_MES_PURCHASERETURN_DETAIL));

                }

                //
                String[] columnsName_MES_PURCHASERETURN = {
                        "EID","ORGANIZATIONNO","RETURNNO","OFNO","BDATE",
                        "SUPPLIER","PURCHASER","DEPARTMENT","MEMO","TOT_QTY","TOT_CQTY","CREATEOPID",
                        "CREATEOPNAME","CREATETIME","STATUS","LASTMODTIME"
                };

                DataValue[] insValue_MES_PURCHASERETURN = new DataValue[]
                        {
                                new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                new DataValue(billNo, Types.VARCHAR),
                                new DataValue(req.getRequest().getErpReturnNo(), Types.VARCHAR),
                                new DataValue(req.getRequest().getBDate(), Types.VARCHAR),
                                new DataValue(req.getRequest().getSupplier(), Types.VARCHAR),
                                new DataValue(req.getRequest().getPurchaser(), Types.VARCHAR),
                                new DataValue(req.getRequest().getDepartment(), Types.VARCHAR),
                                new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
                                new DataValue(req.getRequest().getTot_Qty(), Types.DOUBLE),
                                new DataValue(req.getRequest().getTot_CQty(), Types.DOUBLE),
                                new DataValue(req.getRequest().getCreateBy(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateByName(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateTime(), Types.DATE),
                                new DataValue(0, Types.INTEGER),
                                new DataValue(lastmoditime, Types.DATE)
                        };
                InsBean ib_MES_PURCHASERETURN = new InsBean("MES_PURCHASERETURN", columnsName_MES_PURCHASERETURN);
                ib_MES_PURCHASERETURN.addValues(insValue_MES_PURCHASERETURN);
                this.addProcessData(new DataProcessBean(ib_MES_PURCHASERETURN));



                this.doExecuteDataToDB();

                logger.info("\n*********MES_PurchaseReturnCreate MES ERP 下发采退单创建成功erpReturnNo="+req.getRequest().getErpReturnNo()+"************\n");

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                res.setDoc_no("");
                res.setOrg_no(req.getRequest().getOrganizationNo());
        }

        @Override
        protected List<InsBean> prepareInsertData(MES_PurchaseReturnCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected List<UptBean> prepareUpdateData(MES_PurchaseReturnCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected List<DelBean> prepareDeleteData(MES_PurchaseReturnCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected boolean isVerifyFail(MES_PurchaseReturnCreateReq req) throws Exception
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
                        if (Check.Null(req.getRequest().getErpReturnNo()))
                        {
                                isFail = true;
                                errMsg.append("erpReturnNo不能为空,");
                        }
                        if (Check.Null(req.getRequest().getBDate()))
                        {
                                isFail = true;
                                errMsg.append("bDate不能为空,");
                        }
                        if (Check.Null(req.getRequest().getSupplier()))
                        {
                                isFail = true;
                                errMsg.append("supplier不能为空,");
                        }
                        if (Check.Null(req.getRequest().getPurchaser()))
                        {
                                isFail = true;
                                errMsg.append("purchaser不能为空,");
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

                                for (MES_PurchaseReturnCreateReq.DetailList detail : req.getRequest().getDetailList())
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
                                        if (Check.Null(detail.getPUnit()))
                                        {
                                                isFail = true;
                                                errMsg.append("pUnit不能为空,");
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
                                        if (Check.Null(detail.getBatchNo()))
                                        {
                                                isFail = true;
                                                errMsg.append("batchNo不能为空,");
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
        protected TypeToken<MES_PurchaseReturnCreateReq> getRequestType()
        {
                return new TypeToken<MES_PurchaseReturnCreateReq>(){};
        }

        @Override
        protected MES_PurchaseReturnCreateRes getResponseType()
        {
                return new MES_PurchaseReturnCreateRes();
        }
}
