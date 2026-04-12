package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.MES_StockOutApplicationCreateReq;
import com.dsc.spos.json.cust.res.MES_StockOutApplicationCreateRes;
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
 * 出库单申请
 * </p>
 *
 */

public class MES_StockOutApplicationCreate extends SPosAdvanceService<MES_StockOutApplicationCreateReq, MES_StockOutApplicationCreateRes>
{
        Logger logger = LogManager.getLogger(MES_StockOutApplicationCreate.class.getName());

        public static String parseDateToStr(final String format, final Date date) {
                return new SimpleDateFormat(format).format(date);
        }

        @Override
        protected void processDUID(MES_StockOutApplicationCreateReq req, MES_StockOutApplicationCreateRes res) throws Exception
        {

                String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String bdate = new SimpleDateFormat("yyyyMMdd").format(new Date());


                String sql="select * from MES_TRANSAPPLICATION where eid='"+req.getRequest().getEId()+"' " +
                        "and ORGANIZATIONNO='"+req.getRequest().getOrganizationNo()+"' " +
                        "and OFNO='"+req.getRequest().getErpStockOutNo()+"' ";

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

                String  billNo= PosPub.getBillNo(dao, req.getRequest().getEId(), req.getRequest().getOrganizationNo(), "CKSQ");
                int detail_item=0;

                String[] columnsName_MES_TRANSAPPLICATION_DETAIL = {
                        "EID","APPLICATIONNO","ORGANIZATIONNO","ITEM",
                        "PLUNO","POQTY","UNIT","MEMO",
                        "FEATURENO","STATUS","OFNO","OITEM","BASEQTY","BASEUNIT"
                };
                for (MES_StockOutApplicationCreateReq.DetailList detail : req.getRequest().getDetailList())
                {
                        detail_item+=1;
                        DataValue[] insValue_MES_TRANSAPPLICATION_DETAIL = new DataValue[]
                                {
                                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                        new DataValue(billNo, Types.VARCHAR),
                                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                        new DataValue(detail_item, Types.INTEGER),
                                        new DataValue(detail.getPluNo(), Types.VARCHAR),
                                        new DataValue(detail.getPQty(), Types.VARCHAR),
                                        new DataValue(detail.getUnit(), Types.VARCHAR),
                                        new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
                                        new DataValue(detail.getFeatureNo(), Types.VARCHAR),
                                        new DataValue(0, Types.INTEGER),
                                        new DataValue(req.getRequest().getErpStockOutNo(), Types.VARCHAR),
                                        new DataValue(detail_item, Types.INTEGER),
                                        new DataValue(detail.getBaseQty(), Types.VARCHAR),
                                        new DataValue(detail.getBaseUnit(), Types.VARCHAR)
                                };
                        InsBean ib_MES_TRANSAPPLICATION_DETAIL = new InsBean("MES_TRANSAPPLICATION_DETAIL", columnsName_MES_TRANSAPPLICATION_DETAIL);
                        ib_MES_TRANSAPPLICATION_DETAIL.addValues(insValue_MES_TRANSAPPLICATION_DETAIL);
                        this.addProcessData(new DataProcessBean(ib_MES_TRANSAPPLICATION_DETAIL));

                }


                //
                String[] columnsName_MES_TRANSAPPLICATION = {
                        "EID","APPLICATIONTYPE","APPLICATIONNO","ORGANIZATIONNO",
                        "TOT_CQTY","TOT_PQTY","BDATE","CREATEOPID","CREATEOPNAME","CREATETIME",
                        "LASTMODITIME","STATUS","MES_RECIPIENT","MES_DEPARTMENT","WAREHOUSE","MEMO","OFNO"
                };

                DataValue[] insValue_MES_TRANSAPPLICATION = new DataValue[]
                        {
                                new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                                new DataValue("3", Types.VARCHAR),
                                new DataValue(billNo, Types.VARCHAR),
                                new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                                new DataValue(req.getRequest().getTot_CQty(), Types.VARCHAR),
                                new DataValue(req.getRequest().getTot_Qty(), Types.VARCHAR),
                                new DataValue(req.getRequest().getBDate(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateBy(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateByName(), Types.VARCHAR),
                                new DataValue(req.getRequest().getCreateTime(), Types.DATE),
                                new DataValue(lastmoditime, Types.DATE),
                                new DataValue(0, Types.VARCHAR),
                                new DataValue(req.getRequest().getMes_recipient(), Types.VARCHAR),
                                new DataValue(req.getRequest().getMes_department(), Types.VARCHAR),
                                new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR),
                                new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
                                new DataValue(req.getRequest().getErpStockOutNo(), Types.VARCHAR)
                        };
                InsBean ib_MES_TRANSAPPLICATION = new InsBean("MES_TRANSAPPLICATION", columnsName_MES_TRANSAPPLICATION);
                ib_MES_TRANSAPPLICATION.addValues(insValue_MES_TRANSAPPLICATION);
                this.addProcessData(new DataProcessBean(ib_MES_TRANSAPPLICATION));



                this.doExecuteDataToDB();

                logger.info("\n*********MES_StockOutApplicationCreate MES ERP 下发出库单申请成功erpStockOutNo="+req.getRequest().getErpStockOutNo()+"************\n");

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                res.setDoc_no("");
                res.setOrg_no(req.getRequest().getOrganizationNo());
        }

        @Override
        protected List<InsBean> prepareInsertData(MES_StockOutApplicationCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected List<UptBean> prepareUpdateData(MES_StockOutApplicationCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected List<DelBean> prepareDeleteData(MES_StockOutApplicationCreateReq req) throws Exception
        {
                return null;
        }

        @Override
        protected boolean isVerifyFail(MES_StockOutApplicationCreateReq req) throws Exception
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
                        if (Check.Null(req.getRequest().getErpStockOutNo()))
                        {
                                isFail = true;
                                errMsg.append("erpStockOutNo不能为空,");
                        }
                        if (Check.Null(req.getRequest().getBDate()))
                        {
                                isFail = true;
                                errMsg.append("bDate不能为空,");
                        }
                        if (Check.Null(req.getRequest().getWarehouseNo()))
                        {
                                isFail = true;
                                errMsg.append("warehouseNo不能为空,");
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
                        if (Check.Null(req.getRequest().getMes_recipient()))
                        {
                                isFail = true;
                                errMsg.append("mes_recipient不能为空,");
                        }
                        if (Check.Null(req.getRequest().getMes_department()))
                        {
                                isFail = true;
                                errMsg.append("mes_department不能为空,");
                        }

                        if (req.getRequest().getDetailList()==null || req.getRequest().getDetailList().size()==0)
                        {
                                isFail = true;
                                errMsg.append("detailList不能为空,");
                        }
                        else
                        {

                                for (MES_StockOutApplicationCreateReq.DetailList detail : req.getRequest().getDetailList())
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
        protected TypeToken<MES_StockOutApplicationCreateReq> getRequestType()
        {
                return new TypeToken<MES_StockOutApplicationCreateReq>(){};
        }

        @Override
        protected MES_StockOutApplicationCreateRes getResponseType()
        {
                return new MES_StockOutApplicationCreateRes();
        }
}
