package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AdvisorCreateReq;
import com.dsc.spos.json.cust.req.DCP_AdvisorUpdateReq;
import com.dsc.spos.json.cust.res.DCP_AdvisorUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 顾问修改
 * @author: wangzyc
 * @create: 2021-07-14
 */
public class DCP_AdvisorUpdate extends SPosAdvanceService<DCP_AdvisorUpdateReq, DCP_AdvisorUpdateRes> {
    @Override
    protected void processDUID(DCP_AdvisorUpdateReq req, DCP_AdvisorUpdateRes res) throws Exception {
        String eId = req.geteId();
        DCP_AdvisorUpdateReq.level1Elm request = req.getRequest();
        String opNo = request.getOpNo();

        try {
            // ******************************************* schedulingList校验 同一周期的时间段是否重复 Begin **********************************
            List<DCP_AdvisorUpdateReq.level2Elm> schedulingList = request.getSchedulingList();
            if (!CollectionUtils.isEmpty(schedulingList)) {
                // 排班时间段 不允许重复
                StringBuffer errMsg = new StringBuffer("");

                //初始化一个map
                Map<String, List<DCP_AdvisorUpdateReq.level2Elm>> map = new HashMap<>();
                for (DCP_AdvisorUpdateReq.level2Elm scheduling : schedulingList) {
                    String key = scheduling.getCycle();
                    if (map.containsKey(key)) {
                        //map中存在以此id作为的key，将数据存放当前key的map中
                        map.get(key).add(scheduling);
                    } else {
                        //map中不存在以此id作为的key，新建key用来存放数据
                        List<DCP_AdvisorUpdateReq.level2Elm> schedulings = new ArrayList<>();
                        schedulings.add(scheduling);
                        map.put(key, schedulings);
                    }
                }

                Set<String> timeIntervas = new HashSet<>();
                boolean isRepeat = false;
                for (String m : map.keySet()) {
                    List<DCP_AdvisorUpdateReq.level2Elm> level2Elms = map.get(m);
                    timeIntervas.clear();
                    int prompt = 0;
                    for (DCP_AdvisorUpdateReq.level2Elm lv2 : level2Elms) {
                        String timeInterval = lv2.getTimeInterval();
                        if (timeIntervas.contains(timeInterval)) {
                            isRepeat = true;
                            if(prompt == 0){
                                prompt = 1;
                                errMsg.append("周期：" + m + "的时间段:" + timeInterval + " 重复,请检查! <br/>");
                            }
                        } else {
                            timeIntervas.add(timeInterval);
                        }
                    }
                }

                if (isRepeat) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }

                // ******************************************* schedulingList校验周期与时间段的去重 End **********************************
            }
            // DCP_ADVISORSET_SCHEDULING
            String[] columnsOne = {
                    "EID", "OPNO", "SHOPID", "ITEM", "TIMEINTERVAL", "CYCLE", "APPOINTMENTS"
            };
            // DCP_ADVISORSET_RESTTIME
            String[] columnsTwo = {
                    "EID", "OPNO", "SHOPID", "ITEM", "CYCLETYPE", "RESTTIME", "STATUS"
            };

            // DCP_ADVISORSET_SCHEDULING
            DelBean db2 = new DelBean("DCP_ADVISORSET_SCHEDULING");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));
            if(!CollectionUtils.isEmpty(schedulingList)){
                for (DCP_AdvisorUpdateReq.level2Elm lv2 : schedulingList) {
                    DataValue[] insValue2 = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(opNo, Types.VARCHAR),
                                    new DataValue(lv2.getShopId(), Types.VARCHAR),
                                    new DataValue(lv2.getItem(), Types.VARCHAR),
                                    new DataValue(lv2.getTimeInterval(), Types.VARCHAR),
                                    new DataValue(lv2.getCycle(), Types.VARCHAR),
                                    new DataValue(lv2.getAppointments(), Types.VARCHAR)
                            };
                    InsBean ib2 = new InsBean("DCP_ADVISORSET_SCHEDULING", columnsOne);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }

            // DCP_ADVISORSET_RESTTIME
            DelBean db3 = new DelBean("DCP_ADVISORSET_RESTTIME");
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            List<DCP_AdvisorUpdateReq.level3Elm> restTimeList = request.getRestTimeList();
            if(!CollectionUtils.isEmpty(restTimeList)){
                for (DCP_AdvisorUpdateReq.level3Elm lv3 : restTimeList) {
                    DataValue[] insValue3 = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(opNo, Types.VARCHAR),
                                    new DataValue(lv3.getShopId(), Types.VARCHAR),
                                    new DataValue(lv3.getItem(), Types.VARCHAR),
                                    new DataValue(lv3.getCycleType(), Types.VARCHAR),
                                    new DataValue(lv3.getRestTime(), Types.VARCHAR),
                                    new DataValue(lv3.getStatus(), Types.VARCHAR)
                            };
                    InsBean ib3 = new InsBean("DCP_ADVISORSET_RESTTIME", columnsTwo);
                    ib3.addValues(insValue3);
                    this.addProcessData(new DataProcessBean(ib3));
                }
            }

            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            UptBean ub1 = null;
            ub1 = new UptBean("DCP_ADVISORSET");
            ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
            ub1.addUpdateValue("HEADIMAGE", new DataValue(request.getHeadImageName(), Types.VARCHAR));
            ub1.addUpdateValue("ABILITY", new DataValue(request.getAbility(), Types.VARCHAR));
            ub1.addUpdateValue("PROFESSIONAL", new DataValue(request.getProfessionalId(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
            //condition
            ub1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
        } catch (SPosCodeException e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AdvisorUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AdvisorUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AdvisorUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AdvisorUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_AdvisorUpdateReq.level1Elm request = req.getRequest();

        if(request==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if(Check.Null(request.getOpNo())){
            errMsg.append("员工编号不能为空值 ");
            isFail = true;

        }

        if(Check.Null(request.getStatus())){
            errMsg.append("状态不能为空值 ");
            isFail = true;

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_AdvisorUpdateReq> getRequestType() {
        return new TypeToken<DCP_AdvisorUpdateReq>(){};
    }

    @Override
    protected DCP_AdvisorUpdateRes getResponseType() {
        return new DCP_AdvisorUpdateRes();
    }
}
