package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;

import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.utils.Check;
import com.dsc.spos.json.cust.req.MES_SalesOrderCreateReq;
import com.dsc.spos.json.cust.res.MES_SalesOrderCreateRes;

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
 * 销售订单创建
 * </p>
 *
 */

public class MES_SalesOrderCreate extends SPosAdvanceService<MES_SalesOrderCreateReq, MES_SalesOrderCreateRes>
{
        Logger logger = LogManager.getLogger(MES_SalesOrderCreate.class.getName());

        @Override
        protected void processDUID(MES_SalesOrderCreateReq req, MES_SalesOrderCreateRes res) throws Exception
        {

                String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String bdate =	new SimpleDateFormat("yyyyMMdd").format(new Date());



                String sql="select * from MES_ORDER where eid='"+req.getRequest().getEId()+"' " +
                        "and ORGANIZATIONNO='"+req.getRequest().getOrganizationNo()+"' " +
                        "and OFNO='"+req.getRequest().getErpOrderNo()+"' ";

                List<Map<String, Object>>  data=this.doQueryData(sql, null);

                if (data != null && data.size()>0)
                {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");
                        res.setDoc_no(data.get(0).get("ORDERNO").toString());
                        res.setOrg_no(data.get(0).get("ORGANIZATIONNO").toString());
                        return;
                }


                String  Billno= PosPub.getBillNo(dao, req.getRequest().getEId(), req.getRequest().getOrganizationNo(), "XSDD");

                int detail_item=0;

                //
                String[] columnsName_MES_ORDER_DETAIL = {
                        "EID","ORGANIZATIONNO","ORDERNO","OFNO",
                        "ITEM","OITEM","PLUNO","FEATURENO","PLUNAME","UNIT","PQTY",
                        "RDATE","AQTY","BASEUNIT","BASEQTY","WAREHOUSENO"
                };
                for (MES_SalesOrderCreateReq.OrderDetailList orderDetail : req.getRequest().getOrderDetailList())
                {

                        detail_item+=1;
                        DataValue[] insValue_MES_ORDER_DETAIL = new DataValue[]
                                {
                                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(Billno, Types.VARCHAR),
                                        new DataValue(req.getRequest().getErpOrderNo(), Types.VARCHAR),
                                        new DataValue(detail_item, Types.INTEGER),
                                        new DataValue(orderDetail.getErpItem(), Types.VARCHAR),
                                        new DataValue(orderDetail.getPluNo(), Types.VARCHAR),
                                        new DataValue(orderDetail.getFeatureNo(), Types.VARCHAR),
                                        new DataValue(orderDetail.getPluName(), Types.VARCHAR),
                                        new DataValue(orderDetail.getUnit(), Types.VARCHAR),
                                        new DataValue(orderDetail.getPQty(), Types.VARCHAR),
                                        new DataValue(orderDetail.getRDate(), Types.VARCHAR),
                                        new DataValue(0, Types.VARCHAR),
                                        new DataValue(orderDetail.getBaseUnit(), Types.VARCHAR),
                                        new DataValue(orderDetail.getBaseQty(), Types.VARCHAR),
                                        new DataValue(orderDetail.getWarehouseNo(), Types.VARCHAR)
                                };
                        InsBean ib_MES_ORDER_DETAIL = new InsBean("MES_ORDER_DETAIL", columnsName_MES_ORDER_DETAIL);
                        ib_MES_ORDER_DETAIL.addValues(insValue_MES_ORDER_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_MES_ORDER_DETAIL));

                }


                //
                String[] columnsName_MES_ORDER = {
                        "EID","ORGANIZATIONNO","ORDERNO","OFNO",
                        "BDATE","CUSTOMER","SALESMAN","DEPARTMENT","ADDRESS","CONTACTNAME","CONTACTTELE",
                        "SALESTELE","MEMO","TOT_QTY","TOT_CQTY","CREATEOPID","CREATEOPNAME","CREATETIME",
                        "MODIFYOPID","MODIFYOPNAME","MODIFYTIME",
                        "CONFIRMOPID","CONFIRMNAME","CONFIRMTIME","STATUS"
                };

                DataValue[] insValue_MES_ORDER = new DataValue[]
                        {
                                new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                new DataValue(Billno, Types.VARCHAR),
                                new DataValue(req.getRequest().getErpOrderNo(), Types.VARCHAR),
                                new DataValue(bdate, Types.VARCHAR),
                                new DataValue(req.getRequest().getCustomer(), Types.VARCHAR),
                                new DataValue(req.getRequest().getSalesMan(), Types.VARCHAR),
                                new DataValue(req.getRequest().getDepartment(), Types.VARCHAR),
                                new DataValue(req.getRequest().getAddress(), Types.VARCHAR),
                                new DataValue(req.getRequest().getContactName(), Types.VARCHAR),
                                new DataValue(req.getRequest().getContactTele(), Types.VARCHAR),
                                new DataValue(req.getRequest().getSalesManTel(), Types.VARCHAR),
                                new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
                                new DataValue(req.getRequest().getTot_Qty(), Types.VARCHAR),
                                new DataValue(req.getRequest().getTot_CQty(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateBy(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateByName(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateTime(), Types.DATE),
                                new DataValue(req.getRequest().getCreateBy(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateByName(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateTime(), Types.DATE),
                                new DataValue(req.getRequest().getCreateBy(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateByName(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateTime(), Types.DATE),
                                new DataValue("0", Types.VARCHAR)
                        };
                InsBean ib_MES_ORDER = new InsBean("MES_ORDER", columnsName_MES_ORDER);
                ib_MES_ORDER.addValues(insValue_MES_ORDER);
                this.addProcessData(new DataProcessBean(ib_MES_ORDER));



                this.doExecuteDataToDB();

                logger.info("\n*********MES_SalesOrderCreate MES ERP 下发销售订单成功erpno="+req.getRequest().getErpOrderNo()+"************\n");

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                res.setDoc_no(Billno);
                res.setOrg_no(req.getRequest().getOrganizationNo());
        }

        @Override
        protected List<InsBean> prepareInsertData(MES_SalesOrderCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected List<UptBean> prepareUpdateData(MES_SalesOrderCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected List<DelBean> prepareDeleteData(MES_SalesOrderCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected boolean isVerifyFail(MES_SalesOrderCreateReq req) throws Exception
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
                        if (Check.Null(req.getRequest().getErpOrderNo()))
                        {
                                isFail = true;
                                errMsg.append("erpOrderNo不能为空,");
                        }
                        if (Check.Null(req.getRequest().getBDate()))
                        {
                                isFail = true;
                                errMsg.append("bDate不能为空,");
                        }
                        if (Check.Null(req.getRequest().getAddress()))
                        {
                                isFail = true;
                                errMsg.append("address不能为空,");
                        }
                        if (Check.Null(req.getRequest().getContactName()))
                        {
                                isFail = true;
                                errMsg.append("contactName不能为空,");
                        }
                        if (Check.Null(req.getRequest().getContactTele()))
                        {
                                isFail = true;
                                errMsg.append("contactTele不能为空,");
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
                        if (Check.Null(req.getRequest().getCustomer()))
                        {
                                isFail = true;
                                errMsg.append("customer不能为空,");
                        }
                        if (Check.Null(req.getRequest().getDepartment()))
                        {
                                isFail = true;
                                errMsg.append("department不能为空,");
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
                        if (req.getRequest().getOrderDetailList()==null || req.getRequest().getOrderDetailList().size()==0)
                        {
                                isFail = true;
                                errMsg.append("orderDetailList不能为空,");
                        }
                        else
                        {

                                for (MES_SalesOrderCreateReq.OrderDetailList orderDetail : req.getRequest().getOrderDetailList())
                                {
                                        if (Check.Null(orderDetail.getBaseQty()))
                                        {
                                                isFail = true;
                                                errMsg.append("baseQty不能为空,");
                                        }
                                        if (Check.Null(orderDetail.getBaseUnit()))
                                        {
                                                isFail = true;
                                                errMsg.append("baseUnit不能为空,");
                                        }
                                        if (Check.Null(orderDetail.getErpItem()))
                                        {
                                                isFail = true;
                                                errMsg.append("erpItem不能为空,");
                                        }
                                        if (Check.Null(orderDetail.getFeatureNo()))
                                        {
                                                orderDetail.setFeatureNo(" ");
                                        }
                                        if (Check.Null(orderDetail.getPluName()))
                                        {
                                                isFail = true;
                                                errMsg.append("pluName不能为空,");
                                        }
                                        if (Check.Null(orderDetail.getPluNo()))
                                        {
                                                isFail = true;
                                                errMsg.append("pluNo不能为空,");
                                        }
                                        if (Check.Null(orderDetail.getPQty()))
                                        {
                                                isFail = true;
                                                errMsg.append("pQty不能为空,");
                                        }
                                        if (Check.Null(orderDetail.getRDate()))
                                        {
                                                isFail = true;
                                                errMsg.append("rDate不能为空,");
                                        }
                                        if (Check.Null(orderDetail.getUnit()))
                                        {
                                                isFail = true;
                                                errMsg.append("unit不能为空,");
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
        protected TypeToken<MES_SalesOrderCreateReq> getRequestType()
        {
                return new TypeToken<MES_SalesOrderCreateReq>(){};
        }

        @Override
        protected MES_SalesOrderCreateRes getResponseType()
        {
                return new MES_SalesOrderCreateRes();
        }
}
