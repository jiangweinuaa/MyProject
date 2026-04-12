package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ServiceItemsCreateReq;
import com.dsc.spos.json.cust.res.DCP_ServiceItemsCreateRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 服务项目新增
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ServiceItemsCreate extends SPosAdvanceService<DCP_ServiceItemsCreateReq, DCP_ServiceItemsCreateRes> {
    @Override
    protected void processDUID(DCP_ServiceItemsCreateReq req, DCP_ServiceItemsCreateRes res) throws Exception {
        DCP_ServiceItemsCreateReq.level1Elm request = req.getRequest();
        try {
            if (checkItemsNo(req)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "('" + request.getItemsName() + "')已存在，请勿重复添加");
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastmodiopId = req.getOpNO();
            String lastmodiopName = req.getOpName();
            Date time = new Date();
            String lastmoditime = df.format(time);

            String[] columnsOne = {
                    "EID", "ITEMSTYPE", "ITEMSNO", "ITEMSNAME", "SERVICETIME",
                    "COUPONTYPEID", "QTY", "SERVICEINTRODUCTION", "SERVICENOTE", "MEMO", "STATUS", "CREATEOPID", "CREATEOPNAME", "CREATETIME",
                    "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME", "RESERVETYPE", "PRICE", "VIPPRICE", "CARDPRICE"
            };

            DataValue[] insValuelev = {
                    new DataValue(req.geteId(), Types.VARCHAR),
                    new DataValue(request.getItemsType(), Types.VARCHAR),
                    new DataValue(request.getItemsNo(), Types.VARCHAR),
                    new DataValue(request.getItemsName(), Types.VARCHAR),
                    new DataValue(request.getServiceTime(), Types.VARCHAR),
                    new DataValue(request.getCouponTypeId(), Types.VARCHAR),
                    new DataValue(request.getQty(), Types.VARCHAR),
                    new DataValue(request.getServiceIntroduction(), Types.VARCHAR),
                    new DataValue(request.getServiceNote(), Types.VARCHAR),
                    new DataValue(request.getMemo(), Types.VARCHAR),
                    new DataValue(request.getStatus(), Types.VARCHAR),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE),
                    new DataValue(request.getReserveType(), Types.VARCHAR),
                    new DataValue(request.getPrice(), Types.VARCHAR),
                    new DataValue(request.getVipPrice(), Types.VARCHAR),
                    new DataValue(request.getCardPrice(), Types.VARCHAR)
            };
            InsBean ins = new InsBean("DCP_SERVICEITEMS", columnsOne);
            ins.addValues(insValuelev);
            this.addProcessData(new DataProcessBean(ins));


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            // TODO: handle exception
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ServiceItemsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ServiceItemsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ServiceItemsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ServiceItemsCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ServiceItemsCreateReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getItemsType())) {
            errMsg.append("项目类型不能为空,");
            isFail = true;
        }
        if (Check.Null(request.getItemsNo())) {
            errMsg.append("项目编号不能为空,");
            isFail = true;
        }
        if (Check.Null(request.getItemsName())) {
            errMsg.append("项目名称不能为空,");
            isFail = true;
        }
        if (Check.Null(request.getServiceTime())) {
            errMsg.append("服务时长(分钟)不能为空,");
            isFail = true;
        }
//        if (Check.Null(request.getCouponTypeId())) {
//            errMsg.append("券类型编号不能为空,");
//            isFail = true;
//        }
//        if (Check.Null(request.getQty())) {
//            errMsg.append("赠送张数不能为空,");
//            isFail = true;
//        }
        if (Check.Null(request.getStatus())) {
            errMsg.append("状态不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getReserveType())) {
            errMsg.append("支持预约方式不能为空,");
            isFail = true;
        }
        if (Check.Null(request.getCouponTypeId()) || Check.Null(request.getQty())) {
            errMsg.append("体验券或服务次数不可为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ServiceItemsCreateReq> getRequestType() {
        return new TypeToken<DCP_ServiceItemsCreateReq>() {
        };
    }

    @Override
    protected DCP_ServiceItemsCreateRes getResponseType() {
        return new DCP_ServiceItemsCreateRes();
    }

    /**
     * 检查 项目编号 是否已存在
     *
     * @param req
     * @return
     */
    private boolean checkItemsNo(DCP_ServiceItemsCreateReq req) throws Exception {
        boolean isRepeat = false;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" select * from DCP_SERVICEITEMS where eid = '" + req.geteId() + "' and ITEMSNO = '" + req.getRequest().getItemsNo() + "'");
        List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), null);
        if (!CollectionUtils.isEmpty(data)) {
            isRepeat = true;
        }
        return isRepeat;
    }
}
