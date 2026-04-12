package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComMomentDataReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMomentDataRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComMomentDataRes.Datas;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.GetMomentTask;
import com.dsc.spos.thirdpart.wecom.entity.GetMomentTaskResult;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_ISVWeComMomentData
 * 服务说明：朋友圈任务数据查询
 * @author jinzma
 * @since  2024-03-07
 */
public class DCP_ISVWeComMomentData extends SPosAdvanceService<DCP_ISVWeComMomentDataReq, DCP_ISVWeComMomentDataRes> {
    @Override
    protected void processDUID(DCP_ISVWeComMomentDataReq req, DCP_ISVWeComMomentDataRes res) throws Exception {
        try{
            String eId = req.geteId();
            Datas datas = res.new Datas();
            String momentMsgId = req.getRequest().getMomentMsgId();
            String momentId;
            int sendUser = 0;
            int notSendUser = 0;

            //资料检查
            {
                String sql = "select status,momentid,jobid from dcp_isvwecom_moment where eid='"+eId+"' and momentmsgid='"+momentMsgId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务ID "+momentMsgId+" 不存在 ");
                }

                momentId = getQData.get(0).get("MOMENTID").toString();
                if (Check.Null(momentId)){
                    //此处优化一下，直接去调企微获取momentId
                    String jobId = getQData.get(0).get("JOBID").toString();
                    DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                    GetMomentTaskResult get_moment_task_result = dcpWeComUtils.get_moment_task_result(StaticInfo.dao,jobId);

                    if (get_moment_task_result !=null){
                        //任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成
                        if ("3".equals(get_moment_task_result.getStatus())) {

                            GetMomentTaskResult.Result result = get_moment_task_result.getResult();
                            momentId = result.getMoment_id();
                            if (!Check.Null(momentId)){

                                //更新 DCP_ISVWECOM_MOMENT
                                UptBean ub = new UptBean("DCP_ISVWECOM_MOMENT");
                                ub.addUpdateValue("MOMENTID", new DataValue(momentId, Types.VARCHAR));

                                //Condition
                                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub.addCondition("JOBID", new DataValue(jobId, Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(ub));

                                this.doExecuteDataToDB();   //此处先提交，避免后续接口异常导致未保存，后续又要再查询

                            }

                        }
                    }


                    if (Check.Null(momentId)) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务未获取到momentId,请查询企微日志 ");
                    }
                }
            }

            //删除 DCP_ISVWECOM_MOMENT_TASK 重新获取最新资料
            {
                DelBean db = new DelBean("DCP_ISVWECOM_MOMENT_TASK");
                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db.addCondition("MOMENTMSGID", new DataValue(momentMsgId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));
            }


            //调企微
            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();

            GetMomentTask getMomentTask = dcpWeComUtils.get_moment_task(dao,momentId);
            if (getMomentTask == null) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "朋友圈任务查询调用企微接口失败 ");
            }

            List<GetMomentTask.Task> task_list = getMomentTask.getTask_list();
            if (CollectionUtil.isNotEmpty(task_list)){
                String[] columns = {"EID", "MOMENTMSGID", "USERID", "STATUS"};

                //统计结果
                List<String> sendUserList = new ArrayList<>();
                List<String> notSendUserList = new ArrayList<>();

                for (GetMomentTask.Task task:task_list){

                    String status = task.getPublish_status();   //成员发表状态。0:未发表 1：已发表
                    if ("1".equals(status)){
                        sendUserList.add(task.getUserid());
                    }else {
                        notSendUserList.add(task.getUserid());
                    }


                    //新增 DCP_ISVWECOM_MOMENT_TASK
                    InsBean ib = new InsBean("DCP_ISVWECOM_MOMENT_TASK", columns);
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(momentMsgId, Types.VARCHAR),
                            new DataValue(task.getUserid(), Types.VARCHAR),
                            new DataValue(status, Types.VARCHAR),
                    };
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));

                }

                sendUserList = sendUserList.stream().distinct().collect(Collectors.toList());
                notSendUserList = notSendUserList.stream().distinct().collect(Collectors.toList());

                sendUser = sendUserList.size();
                notSendUser = notSendUserList.size();

            }




            //赋值
            datas.setSendUser(String.valueOf(sendUser));
            datas.setNotSendUser(String.valueOf(notSendUser));

            res.setDatas(datas);


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");



        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComMomentDataReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComMomentDataReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComMomentDataReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComMomentDataReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getMomentMsgId())){
                errMsg.append("momentMsgId不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComMomentDataReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComMomentDataReq>(){};
    }

    @Override
    protected DCP_ISVWeComMomentDataRes getResponseType() {
        return new DCP_ISVWeComMomentDataRes();
    }
}
