package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AdvisorCreateReq;
import com.dsc.spos.json.cust.res.DCP_AdvisorCreateRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 顾问新增
 * @author: wangzyc
 * @create: 2021-07-14
 */
public class DCP_AdvisorCreate extends SPosAdvanceService<DCP_AdvisorCreateReq, DCP_AdvisorCreateRes> {
    @Override
    protected void processDUID(DCP_AdvisorCreateReq req, DCP_AdvisorCreateRes res) throws Exception {
        DCP_AdvisorCreateReq.level1Elm request = req.getRequest();
        String eId = req.geteId();

        try {
            if(checkOpNo(req)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "顾问已存在，请检查");
            }

            // ******************************************* schedulingList校验 同一周期的时间段是否重复 Begin **********************************
            List<DCP_AdvisorCreateReq.level2Elm> schedulingList = request.getSchedulingList();
            if (!CollectionUtils.isEmpty(schedulingList)) {
                // 排班时间段 不允许重复
                StringBuffer errMsg = new StringBuffer("");

                //初始化一个map
                Map<String, List<DCP_AdvisorCreateReq.level2Elm>> map = new HashMap<>();
                for (DCP_AdvisorCreateReq.level2Elm scheduling : schedulingList) {
                    String key = scheduling.getCycle();
                    if (map.containsKey(key)) {
                        //map中存在以此id作为的key，将数据存放当前key的map中
                        map.get(key).add(scheduling);
                    } else {
                        //map中不存在以此id作为的key，新建key用来存放数据
                        List<DCP_AdvisorCreateReq.level2Elm> schedulings = new ArrayList<>();
                        schedulings.add(scheduling);
                        map.put(key, schedulings);
                    }
                }

                Set<String> timeIntervas = new HashSet<>();
                boolean isRepeat = false;
                for (String m : map.keySet()) {
                    List<DCP_AdvisorCreateReq.level2Elm> level2Elms = map.get(m);
                    timeIntervas.clear();
                    int prompt = 0;
                    for (DCP_AdvisorCreateReq.level2Elm lv2 : level2Elms) {
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
            }

                // ******************************************* schedulingList校验周期与时间段的去重 End **********************************

                // DCP_ADVISORSET
                String[] columnsOne = {
                        "EID", "OPNO", "OPNAME", "HEADIMAGE", "PROFESSIONAL", "ABILITY", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME", "STATUS"
                };
                // DCP_ADVISORSET_SCHEDULING
                String[] columnsTwo = {
                        "EID", "OPNO", "SHOPID", "ITEM", "TIMEINTERVAL", "CYCLE", "APPOINTMENTS"
                };
                // DCP_ADVISORSET_RESTTIME
                String[] columnsThree = {
                        "EID", "OPNO", "SHOPID", "ITEM", "CYCLETYPE", "RESTTIME", "STATUS"
                };
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String lastmodiopId = req.getOpNO();
                String lastmodiopName = req.getOpName();
                Date time = new Date();
                String lastmoditime = df.format(time);

                String opNo = request.getOpNo();
                String opName = request.getOpName();
                String headImage = request.getHeadImageName();
                String professionalId = request.getProfessionalId();
                String ability = request.getAbility();
                String status = request.getStatus();


                DataValue[] insValue1 = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(opName, Types.VARCHAR),
                                new DataValue(headImage, Types.VARCHAR),
                                new DataValue(professionalId, Types.VARCHAR),
                                new DataValue(ability, Types.VARCHAR),
                                new DataValue(lastmodiopId, Types.VARCHAR),
                                new DataValue(lastmodiopName, Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE),
                                new DataValue(status, Types.VARCHAR)
                        };
                InsBean ib1 = new InsBean("DCP_ADVISORSET", columnsOne);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));

                if (!CollectionUtils.isEmpty(schedulingList)) {
                    for (DCP_AdvisorCreateReq.level2Elm lv2 : schedulingList) {
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
                        InsBean ib2 = new InsBean("DCP_ADVISORSET_SCHEDULING", columnsTwo);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }

                List<DCP_AdvisorCreateReq.level3Elm> restTimeList = request.getRestTimeList();
                if (!CollectionUtils.isEmpty(restTimeList)) {
                    for (DCP_AdvisorCreateReq.level3Elm lv3 : restTimeList) {
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
                        InsBean ib3 = new InsBean("DCP_ADVISORSET_RESTTIME", columnsThree);
                        ib3.addValues(insValue3);
                        this.addProcessData(new DataProcessBean(ib3));
                    }
                }

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
    protected List<InsBean> prepareInsertData(DCP_AdvisorCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AdvisorCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AdvisorCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AdvisorCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_AdvisorCreateReq.level1Elm request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getOpNo())) {
            errMsg.append("员工编号不能为空值, ");
            isFail = true;

        }
        if (Check.Null(request.getOpName())) {
            errMsg.append("员工名称不能为空值, ");
            isFail = true;

        }
        if (Check.Null(request.getProfessionalId())) {
            errMsg.append("职称代号不能为空值, ");
            isFail = true;

        }
        if (Check.Null(request.getStatus())) {
            errMsg.append("状态不能为空值, ");
            isFail = true;

        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AdvisorCreateReq> getRequestType() {
        return new TypeToken<DCP_AdvisorCreateReq>() {
        };
    }

    @Override
    protected DCP_AdvisorCreateRes getResponseType() {
        return new DCP_AdvisorCreateRes();
    }

    /**
     * 效验顾问是否存在
     * @param req
     * @return
     */
    private boolean checkOpNo(DCP_AdvisorCreateReq req) throws Exception {
        boolean repeat = false;
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from DCP_ADVISORSET where eid = '"+req.geteId()+"' and opno = '"+req.getRequest().getOpNo()+"'");
        List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), null);
        if(!CollectionUtils.isEmpty(data)){
            repeat = true;
        }
        return  repeat;
    }
}