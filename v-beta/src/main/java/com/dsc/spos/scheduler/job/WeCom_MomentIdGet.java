package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.GetMomentTaskResult;
import com.dsc.spos.utils.Check;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * *****************企微获取朋友圈任务创建结果*****************
 * 由于发表任务的创建是异步执行的，应用需要再调用该接口以获取创建的结果。
 * https://developer.work.weixin.qq.com/document/path/96351
 * @author jinzma
 * 2024-02-22
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WeCom_MomentIdGet extends InitJob{

    Logger logger = LogManager.getLogger(WeCom_MomentIdGet.class.getName());
    static boolean bRun = false;  //标记此服务是否正在执行中

    public String doExe() {

        logger.info("\r\n*************** WeCom_MomentIdGet 企微获取MomentId START ****************\r\n");

        try{

            if(bRun) {
                logger.info("\r\n*************** WeCom_MomentIdGet 企微获取MomentId 正在执行中  ****************\r\n");
                return "";
            }
            bRun=true;


            String sql = " select eid,jobid from dcp_isvwecom_moment "
                    + " where jobid is not null and momentid is null and status='100'  and createtime+2 >= sysdate";  //这个jobid 只有24小时有效，超过就不管了
            List<Map<String, Object>> getWeComMoment = StaticInfo.dao.executeQuerySQL(sql, null);
            if (CollectionUtil.isNotEmpty(getWeComMoment)){
                DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                for (Map<String, Object>oneWeComMoment :getWeComMoment ){
                    List<DataProcessBean> data = new ArrayList<>();

                    String eId = oneWeComMoment.get("EID").toString();
                    String jobId = oneWeComMoment.get("JOBID").toString();

                    GetMomentTaskResult get_moment_task_result = dcpWeComUtils.get_moment_task_result(StaticInfo.dao,jobId);

                    if (get_moment_task_result !=null){
                        //任务状态，整型，1表示开始创建任务，2表示正在创建任务中，3表示创建任务已完成
                        if ("3".equals(get_moment_task_result.getStatus())) {

                            GetMomentTaskResult.Result result = get_moment_task_result.getResult();
                            String momentId = result.getMoment_id();
                            if (!Check.Null(momentId)){

                                //更新 DCP_ISVWECOM_MOMENT
                                UptBean ub = new UptBean("DCP_ISVWECOM_MOMENT");
                                ub.addUpdateValue("MOMENTID", new DataValue(momentId, Types.VARCHAR));


                                //Condition
                                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub.addCondition("JOBID", new DataValue(jobId, Types.VARCHAR));
                                data.add(new DataProcessBean(ub));

                                StaticInfo.dao.useTransactionProcessData(data);

                            }

                        }
                    }
                }
            }




        }catch (Exception e){
            logger.error("\r\n*************** WeCom_MomentIdGet 企微获取MomentId 异常: "+e.getMessage());
        }
        finally {
            bRun=false;
            logger.info("\r\n*************** WeCom_MomentIdGet 企微获取MomentId END ****************\r\n");
        }


        return "";

    }

}
