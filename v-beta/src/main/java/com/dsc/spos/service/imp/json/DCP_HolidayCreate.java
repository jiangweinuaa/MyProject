package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HolidayCreateReq;
import com.dsc.spos.json.cust.res.DCP_HolidayCreateRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 门店休息日新增
 * @author: wangzyc
 * @create: 2021-07-27
 */
public class DCP_HolidayCreate extends SPosAdvanceService<DCP_HolidayCreateReq, DCP_HolidayCreateRes> {
    @Override
    protected void processDUID(DCP_HolidayCreateReq req, DCP_HolidayCreateRes res) throws Exception {
        String eId = req.geteId();
        DCP_HolidayCreateReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String memo = request.getMemo();

        try {
            String[] conditionValues = {eId, shopId};
            StringBuffer sqlbuf = new StringBuffer("");
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastmodiopId = req.getOpNO();
            String lastmodiopName = req.getOpName();
            Date time = new Date();
            String lastmoditime = dfs.format(time);

            sqlbuf.append("select MAX(ITEM) ITEM from DCP_RESERVEHOLIDAY where EID = ? and shopId = ? ");
            List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), conditionValues);
            int item = 1;
            if (!CollectionUtils.isEmpty(data)) {
                String itemStr = data.get(0).get("ITEM").toString();
                if (!Check.Null(itemStr)) {
                    item = Integer.parseInt(itemStr) + 1;
                }
            }

            String[] columns = {"EID", "SHOPID", "ITEM", "BEGINDATE", "ENDDATE", "MEMO", "CREATEOPID", "CREATEOPNAME",
                    "CREATETIME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME"};
            DataValue[] insValue1 = null;

            insValue1 = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(item, Types.VARCHAR),
                    new DataValue(beginDate, Types.DATE),
                    new DataValue(endDate, Types.DATE),
                    new DataValue(memo, Types.VARCHAR),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };

            InsBean ib1 = new InsBean("DCP_RESERVEHOLIDAY", columns);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_HolidayCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_HolidayCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_HolidayCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_HolidayCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_HolidayCreateReq.level1Elm request = req.getRequest();

        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空值 ");
            isFail = true;
        }

        if (Check.Null(request.getBeginDate())) {
            errMsg.append("开始日期不能为空值 ");
            isFail = true;
        }

        if (Check.Null(request.getEndDate())) {
            errMsg.append("结束日期不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_HolidayCreateReq> getRequestType() {
        return new TypeToken<DCP_HolidayCreateReq>() {
        };
    }

    @Override
    protected DCP_HolidayCreateRes getResponseType() {
        return new DCP_HolidayCreateRes();
    }
}
