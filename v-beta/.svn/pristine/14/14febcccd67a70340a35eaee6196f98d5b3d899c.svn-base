package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveTimeCreateReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_ReserveTimeCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 预约时间新增
 * @author: wangzyc
 * @create: 2021-07-20
 */
public class DCP_ReserveTimeCreate extends SPosAdvanceService<DCP_ReserveTimeCreateReq, DCP_ReserveTimeCreateRes> {
    @Override
    protected void processDUID(DCP_ReserveTimeCreateReq req, DCP_ReserveTimeCreateRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getRequest().getShopId();

        DCP_ReserveTimeCreateReq.level2Elm reserveTime = req.getRequest().getReserveTime();
        String beginTime = reserveTime.getBeginTime();
        String endTime = reserveTime.getEndTime();
        String cycle = reserveTime.getCycle();
        String status = reserveTime.getStatus();

        SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
        String[] conditionValues = { eId,shopId };
        // ******************************* 预约时间检核 是否重复 Begin********************************
        StringBuffer sqlbuf = new StringBuffer("");
        try {
            sqlbuf.append("select * from DCP_RESERVETIME where EID = ? and shopId = ? ");
            List<Map<String, Object>> datas = this.doQueryData(sqlbuf.toString(), conditionValues);
            if(!CollectionUtils.isEmpty(datas)){
                Date pBeginTime = hm.parse(beginTime);
                Date pEndTime = hm.parse(endTime);
                String msgError = "该门店时间段范围已包含当前所设时间，请重新选择";
                for (Map<String, Object> data : datas) {
                    String begintime = data.get("BEGINTIME").toString();
                    String endtime = data.get("ENDTIME").toString();
                    String CYCLE = data.get("CYCLE").toString();
                    // 如果周期一样 则时间段不可重复
                    if(cycle.equals(CYCLE)){
                        Date pBeginTime2 = hm.parse(begintime);
                        Date pEndTime2 = hm.parse(endtime);
                        // pBeginDate>pBeginDate2  a=1;     pBeginDate=pBeginDate2  a=0;   pBeginDate<pBeginDate2  a=-1;
                        int a = pBeginTime.compareTo(pBeginTime2);
                        int b = pBeginTime2.compareTo(pEndTime);
                        int c = pBeginTime.compareTo(pEndTime);

                        // beginDate1<=beginDate2< endDate1，则判断重复
                        if((a<=0)&&(b<0)||c==0){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,msgError );
                        }
                    }
                }
            }

            //

            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastmodiopId = req.getOpNO();
            String lastmodiopName = req.getOpName();
            Date time = new Date();
            String lastmoditime = dfs.format(time);
            sqlbuf.setLength(0);
            sqlbuf.append("select MAX(to_number(ITEM)) ITEM from DCP_RESERVETIME where EID = ? and shopId = ? ");
            List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), conditionValues);
            int item = 1;
            if (!CollectionUtils.isEmpty(data)) {
                String itemStr = data.get(0).get("ITEM").toString();
                if (!Check.Null(itemStr)) {
                    item = Integer.parseInt(itemStr) + 1;
                }
            }

            String[] columns = {"EID", "SHOPID","ITEM", "CYCLE", "BEGINTIME", "ENDTIME", "STATUS", "CREATEOPID", "CREATEOPNAME",
                    "CREATETIME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME"};
            DataValue[] insValue1 = null;

            insValue1 = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(item, Types.VARCHAR),
                    new DataValue(cycle, Types.VARCHAR),
                    new DataValue(beginTime, Types.VARCHAR),
                    new DataValue(endTime, Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE),
                    new DataValue(lastmodiopId, Types.VARCHAR),
                    new DataValue(lastmodiopName, Types.VARCHAR),
                    new DataValue(lastmoditime, Types.DATE)
            };

            InsBean ib1 = new InsBean("DCP_RESERVETIME", columns);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveTimeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveTimeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveTimeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveTimeCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveTimeCreateReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空 ");
            isFail = true;
        }

        if (request.getReserveTime() == null) {
            errMsg.append("预约时间不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveTimeCreateReq> getRequestType() {
        return new TypeToken<DCP_ReserveTimeCreateReq>() {
        };
    }

    @Override
    protected DCP_ReserveTimeCreateRes getResponseType() {
        return new DCP_ReserveTimeCreateRes();
    }
}
