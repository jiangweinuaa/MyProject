package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.MES_ComposeDisCreateReq;
import com.dsc.spos.json.cust.res.MES_ComposeDisCreateRes;
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
 * 合并拆分创建
 * </p>
 */

public class MES_ComposeDisCreate extends SPosAdvanceService<MES_ComposeDisCreateReq, MES_ComposeDisCreateRes> {
    Logger logger = LogManager.getLogger(MES_ComposeDisCreate.class.getName());

    @Override
    protected void processDUID(MES_ComposeDisCreateReq req, MES_ComposeDisCreateRes res) throws Exception {

        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String bdate = new SimpleDateFormat("yyyyMMdd").format(new Date());


        String sql = "select * from MES_COMPOSEDIS where eid='" + req.getRequest().getEId() + "' " +
                "and ORGANIZATIONNO='" + req.getRequest().getOrganizationNo() + "' " +
                "and OFNO='" + req.getRequest().getErpDocNo() + "' and type='"+req.getRequest().getType()+"' ";

        List<Map<String, Object>> data = this.doQueryData(sql, null);

        if (data != null && data.size() > 0) {
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setDoc_no(data.get(0).get("OFNO").toString());
            res.setOrg_no(data.get(0).get("ORGANIZATIONNO").toString());
            return;
        }

        String billNo = "";
        if ("0".equals(req.getRequest().getType())) {
            billNo = PosPub.getBillNo(dao, req.getRequest().getEId(), req.getRequest().getOrganizationNo(), "ZHZH");
        } else {
            billNo = PosPub.getBillNo(dao, req.getRequest().getEId(), req.getRequest().getOrganizationNo(), "CJZH");
        }

        int detail_item = 0;
        String[] columnsName_MES_COMPOSEDIS_DETAIL = {
                "EID", "ORGANIZATIONNO", "DOCNO", "OFNO", "ITEM", "SUBPLUNO", "UNIT", "OITEM",
                "QTY", "WAREHOUSENO", "BATCHNO", "BASEQTY", "BASEUNIT", "FEATURENO"
        };
        for (MES_ComposeDisCreateReq.DetailList detail : req.getRequest().getDetailList()) {

            detail_item += 1;
            DataValue[] insValue_MES_COMPOSEDIS_DETAIL = new DataValue[]
                    {
                            new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                            new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                            new DataValue(billNo, Types.VARCHAR),
                            new DataValue(req.getRequest().getErpDocNo(), Types.VARCHAR),
                            new DataValue(detail_item, Types.INTEGER),
                            new DataValue(detail.getSubPluNo(), Types.VARCHAR),
                            new DataValue(detail.getUnit(), Types.VARCHAR),
                            new DataValue(detail_item, Types.INTEGER),
                            new DataValue(detail.getQty(), Types.DOUBLE),
                            new DataValue(detail.getWarehouseNo(), Types.VARCHAR),
                            new DataValue(detail.getBatchNo(), Types.VARCHAR),
                            new DataValue(detail.getBaseQty(), Types.DOUBLE),
                            new DataValue(detail.getBaseUnit(), Types.VARCHAR),
                            new DataValue(detail.getFeatureNo(), Types.VARCHAR)
                    };
            InsBean ib_MES_COMPOSEDIS_DETAIL = new InsBean("MES_COMPOSEDIS_DETAIL", columnsName_MES_COMPOSEDIS_DETAIL);
            ib_MES_COMPOSEDIS_DETAIL.addValues(insValue_MES_COMPOSEDIS_DETAIL);
            this.addProcessData(new DataProcessBean(ib_MES_COMPOSEDIS_DETAIL));

        }

        //
        String[] columnsName_MES_COMPOSEDIS = {
                "EID", "ORGANIZATIONNO", "DOCNO", "OFNO", "TYPE", "BDATE", "PLUNO",
                "PUNIT", "PQTY", "WAREHOUSENO", "BATCHNO", "CREATEOPID", "CREATEOPNAME", "CREATETIME",
                "STATUS", "BASEQTY", "BASEUNIT", "FEATURENO", "LASTMODITIME"
        };

        DataValue[] insValue_MES_COMPOSEDIS = new DataValue[]
                {
                        new DataValue(req.getRequest().getEId(), Types.VARCHAR),
                        new DataValue(req.getRequest().getOrganizationNo(), Types.VARCHAR),
                        new DataValue(billNo, Types.VARCHAR),
                        new DataValue(req.getRequest().getErpDocNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBDate(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPluNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPUnit(), Types.VARCHAR),
                        new DataValue(req.getRequest().getPQty(), Types.DOUBLE),
                        new DataValue(req.getRequest().getWarehouseNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getBatchNo(), Types.VARCHAR),
                        new DataValue(req.getRequest().getCreateBy(), Types.VARCHAR),
                        new DataValue(req.getRequest().getCreateByName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getCreateTime(), Types.DATE),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue(req.getRequest().getBaseQty(), Types.DOUBLE),
                        new DataValue(req.getRequest().getBaseUnit(), Types.VARCHAR),
                        new DataValue(req.getRequest().getFeatureNo(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };
        InsBean ib_MES_COMPOSEDIS = new InsBean("MES_COMPOSEDIS", columnsName_MES_COMPOSEDIS);
        ib_MES_COMPOSEDIS.addValues(insValue_MES_COMPOSEDIS);
        this.addProcessData(new DataProcessBean(ib_MES_COMPOSEDIS));


        this.doExecuteDataToDB();

        logger.info("\n*********MES_ComposeDisCreate MES ERP 下发组合拆解单创建成功erpDocNo=" + req.getRequest().getErpDocNo() + "************\n");

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        res.setDoc_no("");
        res.setOrg_no(req.getRequest().getOrganizationNo());
    }

    @Override
    protected List<InsBean> prepareInsertData(MES_ComposeDisCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(MES_ComposeDisCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(MES_ComposeDisCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(MES_ComposeDisCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        } else {
            if (Check.Null(req.getRequest().getEId())) {
                isFail = true;
                errMsg.append("eId不能为空,");
            }
            if (Check.Null(req.getRequest().getOrganizationNo())) {
                isFail = true;
                errMsg.append("organizationNo不能为空,");
            }
            if (Check.Null(req.getRequest().getErpDocNo())) {
                isFail = true;
                errMsg.append("erpDocNo不能为空,");
            }
            if (Check.Null(req.getRequest().getType())) {
                isFail = true;
                errMsg.append("type不能为空,");
            }
            if (Check.Null(req.getRequest().getBDate())) {
                isFail = true;
                errMsg.append("bDate不能为空,");
            }
            if (Check.Null(req.getRequest().getPluNo())) {
                isFail = true;
                errMsg.append("pluNo不能为空,");
            }
            if (Check.Null(req.getRequest().getPUnit())) {
                isFail = true;
                errMsg.append("pUnit不能为空,");
            }
            if (Check.Null(req.getRequest().getPQty())) {
                isFail = true;
                errMsg.append("pQty不能为空,");
            }
            if (Check.Null(req.getRequest().getBaseUnit())) {
                isFail = true;
                errMsg.append("baseUnit不能为空,");
            }
            if (Check.Null(req.getRequest().getBaseQty())) {
                isFail = true;
                errMsg.append("baseQty不能为空,");
            }
            if (Check.Null(req.getRequest().getWarehouseNo())) {
                isFail = true;
                errMsg.append("warehouseNo不能为空,");
            }
            if (Check.Null(req.getRequest().getCreateBy())) {
                isFail = true;
                errMsg.append("createBy不能为空,");
            }
            if (Check.Null(req.getRequest().getCreateByName())) {
                isFail = true;
                errMsg.append("createByName不能为空,");
            }
            if (Check.Null(req.getRequest().getCreateTime())) {
                isFail = true;
                errMsg.append("createTime不能为空,");
            }

            if (req.getRequest().getDetailList() == null || req.getRequest().getDetailList().size() == 0) {
                isFail = true;
                errMsg.append("detailList不能为空,");
            } else {

                for (MES_ComposeDisCreateReq.DetailList detail : req.getRequest().getDetailList()) {
                    if (Check.Null(detail.getErpItem())) {
                        isFail = true;
                        errMsg.append("erpItem不能为空,");
                    }
                    if (Check.Null(detail.getSubPluNo())) {
                        isFail = true;
                        errMsg.append("subPluNo不能为空,");
                    }
                    if (Check.Null(detail.getUnit())) {
                        isFail = true;
                        errMsg.append("unit不能为空,");
                    }
                    if (Check.Null(detail.getFeatureNo())) {
                        isFail = true;
                        errMsg.append("featureNo不能为空,");
                    }
                    if (Check.Null(detail.getBaseQty())) {
                        isFail = true;
                        errMsg.append("baseQty不能为空,");
                    }
                    if (Check.Null(detail.getBaseUnit())) {
                        isFail = true;
                        errMsg.append("baseUnit不能为空,");
                    }
                    if (Check.Null(detail.getWarehouseNo())) {
                        isFail = true;
                        errMsg.append("warehouseNo不能为空,");
                    }
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<MES_ComposeDisCreateReq> getRequestType() {
        return new TypeToken<MES_ComposeDisCreateReq>() {
        };
    }

    @Override
    protected MES_ComposeDisCreateRes getResponseType() {
        return new MES_ComposeDisCreateRes();
    }
}
