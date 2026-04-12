package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveItemsCreateReq;
import com.dsc.spos.json.cust.res.DCP_ReserveItemsCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 预约项目新增
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ReserveItemsCreate extends SPosAdvanceService<DCP_ReserveItemsCreateReq, DCP_ReserveItemsCreateRes> {
    @Override
    protected void processDUID(DCP_ReserveItemsCreateReq req, DCP_ReserveItemsCreateRes res) throws Exception {
        String eId = req.geteId();
        DCP_ReserveItemsCreateReq.level1Elm request = req.getRequest();

        try {
            String itemsNo = request.getItemsNo();
            String shopId = request.getShopId();
            String status = request.getStatus();

            if (checkItemsNo(req)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "('预约项目编号："+itemsNo+ "')已存在，请勿重复添加");
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastmodiopId = req.getOpNO();
            String lastmodiopName = req.getOpName();
            Date time = new Date();
            String lastmoditime = df.format(time);

            List<DCP_ReserveItemsCreateReq.level2Elm> opList = request.getOpList();

            String[] columnsOne = {
                    "EID", "ITEMSNO", "SHOPID", "STATUS", "CREATEOPID", "CREATEOPNAME", "CREATETIME",
                    "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME"
            };

            String[] columnsTwo = {
                    "EID", "ITEMSNO", "SHOPID", "OPNO"
            };

            if(!CollectionUtils.isEmpty(opList)){
                for (DCP_ReserveItemsCreateReq.level2Elm lv2 : opList) {
                    DataValue[] insValuelev = {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(itemsNo, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(lv2.getOpNo(), Types.VARCHAR)
                    };
                    InsBean ins = new InsBean("DCP_RESERVEADVISOR", columnsTwo);
                    ins.addValues(insValuelev);
                    this.addProcessData(new DataProcessBean(ins));
                }
            }

            DataValue[] insValuele2 = {
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(itemsNo, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };
            InsBean ins = new InsBean("DCP_RESERVEITEMS", columnsOne);
            ins.addValues(insValuele2);
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
    protected List<InsBean> prepareInsertData(DCP_ReserveItemsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveItemsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveItemsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveItemsCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveItemsCreateReq.level1Elm request = req.getRequest();
        if(request==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(Check.Null(request.getShopId())){
            errMsg.append("所属门店不能为空 ");
            isFail = true;
        }

        if(Check.Null(request.getItemsNo())){
            errMsg.append("项目编号不能为空");
            isFail = true;
        }
        if(Check.Null(request.getStatus())){
            errMsg.append("状态不能为空");
            isFail = true;
        }
        if(CollectionUtils.isEmpty(request.getOpList())){
            errMsg.append("分配顾问不能为空");
            isFail = true;
        }


        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveItemsCreateReq> getRequestType() {
        return new TypeToken<DCP_ReserveItemsCreateReq>(){};
    }

    @Override
    protected DCP_ReserveItemsCreateRes getResponseType() {
        return new DCP_ReserveItemsCreateRes() ;
    }
    /**
     * 检查 项目编号 是否已存在
     *
     * @param req
     * @return
     */
    private boolean checkItemsNo(DCP_ReserveItemsCreateReq req) throws Exception {
        boolean isRepeat = false;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" select * from DCP_RESERVEITEMS where eid = '"+req.geteId()+"' and ITEMSNO = '"+req.getRequest().getItemsNo()+"' and shopid = '"+req.getRequest().getShopId()+"'");
        List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), null);
        if (!CollectionUtils.isEmpty(data)) {
            isRepeat = true;
        }
        return isRepeat;
    }

}
