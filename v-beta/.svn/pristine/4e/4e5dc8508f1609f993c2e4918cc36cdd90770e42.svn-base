package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.UserBehaviorData;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * *****************获取「联系客户统计」数据*****************
 * @author jinzma
 * 2024-02-23
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WeCom_UserBehaviorGet extends InitJob{
    Logger logger = LogManager.getLogger(WeCom_UserBehaviorGet.class.getName());
    static boolean bRun = false;  //标记此服务是否正在执行中

    public String doExe() {

        logger.info("\r\n*************** WeCom_UserBehaviorGet 企微获取「联系客户统计」 START ****************\r\n");

        try{

            //系统日期和时间
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());

            //每天 000001 - 015959 执行
            if (sTime.compareTo("000001") >= 0 && sTime.compareTo("055959") < 0) {
                if(bRun) {
                    logger.info("\r\n*************** WeCom_UserBehaviorGet 企微获取「联系客户统计」 正在执行中  ****************\r\n");
                    return "";
                }
                bRun=true;

                //获取DCP_ISVWECOM_STAFFS表中ACCOUNTTYPE为2的账号
                String sql = " select eid,userid from dcp_isvwecom_staffs where accounttype='2'  ";
                List<Map<String, Object>> getUser = StaticInfo.dao.executeQuerySQL(sql, null);
                if (CollectionUtil.isNotEmpty(getUser)){
                    DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                    for (Map<String, Object> oneUser:getUser ){
                        String eId = oneUser.get("EID").toString();
                        String userId = oneUser.get("USERID").toString();

                        //查询需要进行关联的用户资料
                        sql = "select max(stattime) as stattime from dcp_isvwecom_behavior_data "
                                + "where eid='"+eId+"' and userid='"+userId+"' ";
                        List<Map<String, Object>> getmaxStattime = StaticInfo.dao.executeQuerySQL(sql, null);
                        String maxStattime = getmaxStattime.get(0).get("STATTIME").toString();

                        //首次客户统计的日期默认为昨天
                        String stattimeDate = PosPub.GetStringDate(sDate,-1);
                        if (!Check.Null(maxStattime)){
                            stattimeDate = PosPub.GetStringDate(maxStattime,1); //向企微拉取的日期是数据库里已经存在的日期+1天
                        }
                        while (PosPub.compare_date(stattimeDate,sDate) < 0){

                            //调企微
                            UserBehaviorData userBehaviorData = dcpWeComUtils.getUserBehaviorData(StaticInfo.dao,userId,stattimeDate);
                            if (userBehaviorData !=null ) {
                                List<UserBehaviorData.Behavior> behavior_data = userBehaviorData.getBehavior_data();

                                List<DataProcessBean> data = new ArrayList<>();
                                //删除 DCP_ISVWECOM_BEHAVIOR_DATA （避免日期出现跳跃导致资料新增异常，此处先删后插）
                                DelBean db1 = new DelBean("DCP_ISVWECOM_BEHAVIOR_DATA");
                                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                db1.addCondition("USERID", new DataValue(userId, Types.VARCHAR));
                                db1.addCondition("STATTIME", new DataValue(stattimeDate, Types.VARCHAR));

                                data.add(new DataProcessBean(db1));

                                //新增 DCP_ISVWECOM_BEHAVIOR_DATA
                                String[] columns1 = {"EID", "USERID", "STATTIME", "NEWAPPLYCNT", "NEWCONTACTCNT", "CHATCNT",
                                        "MESSAGECNT", "REPLYPERCENTAGE", "AVGREPLYTIME", "NEGATIVEFEEDBACKCNT"};
                                DataValue[] insValue1 = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue(stattimeDate, Types.VARCHAR),
                                        new DataValue(behavior_data.get(0).getNew_apply_cnt(), Types.VARCHAR),
                                        new DataValue(behavior_data.get(0).getNew_contact_cnt(), Types.VARCHAR),
                                        new DataValue(behavior_data.get(0).getChat_cnt(), Types.VARCHAR),
                                        new DataValue(behavior_data.get(0).getMessage_cnt(), Types.VARCHAR),
                                        new DataValue(behavior_data.get(0).getReply_percentage(), Types.VARCHAR),
                                        new DataValue(behavior_data.get(0).getAvg_reply_time(), Types.VARCHAR),
                                        new DataValue(behavior_data.get(0).getNegative_feedback_cnt(), Types.VARCHAR),
                                };
                                InsBean ib1 = new InsBean("DCP_ISVWECOM_BEHAVIOR_DATA", columns1);
                                ib1.addValues(insValue1);
                                data.add(new DataProcessBean(ib1));

                                StaticInfo.dao.useTransactionProcessData(data);

                            }else {
                                logger.error("\r\n*************** WeCom_UserBehaviorGet 企微获取「联系客户统计」异常,返回null ");
                                break;
                            }

                            stattimeDate = PosPub.GetStringDate(stattimeDate,1);
                        }
                    }
                }
            }



        }catch (Exception e){
            logger.error("\r\n*************** WeCom_UserBehaviorGet 企微获取「联系客户统计」 异常: "+e.getMessage());
        }
        finally {
            bRun=false;
            logger.info("\r\n*************** WeCom_UserBehaviorGet 企微获取「联系客户统计」 END ****************\r\n");
        }


        return "";

    }

}
