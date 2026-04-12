package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.thirdpart.wecom.entity.ExternalUseridToPending;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * *****************企微获取unionid与第三方external_userid关联关系*****************
 * @author jinzma
 * 2024-02-22
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WeCom_UnionIdGet extends InitJob {

    Logger logger = LogManager.getLogger(WeCom_UnionIdGet.class.getName());
    //一天执行一次
    static boolean bRun = false;  //标记此服务是否正在执行中

    public String doExe() {

        logger.info("\r\n*************** WeCom_UnionIdGet 企微获取unionid START ****************\r\n");

        /* 企微获取unionid JOB方案说明：
        1、查会员资料写入dcp_isvwecom_member  （openid 取 crm_memberidendity，这个字段写入后不能修改！！！！）
        2、过滤所有有效期超过90天的，向企微重新获取新的pending写入dcp_isvwecom_member  （openid 取 crm_memberidendity，这个字段写入后不能修改！！！！）
        3、查所有未关联到会员unionid的外部客户资料，向企微获取对应的pending，保存在内存中，写表没意义
        4、通过pengding将会员和外部企业客户进行关联，写入dcp_isvwecom_member
        */
        try{

            //系统日期和时间
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());

            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();

            //每天 000001 - 015959 执行
            if (sTime.compareTo("000001") >= 0 && sTime.compareTo("055959") < 0) {
                if(bRun) {
                    logger.info("\r\n*************** WeCom_UnionIdGet 企微获取unionid 正在执行中  ****************\r\n");
                    return "";
                }
                bRun=true;

                String sql = " select eid,appid from dcp_isvwecom_set ";
                List<Map<String, Object>> getWeComSet = StaticInfo.dao.executeQuerySQL(sql, null);
                if (CollectionUtil.isNotEmpty(getWeComSet)){
                    for (Map<String, Object>oneWeComSet :getWeComSet ){
                        String eId = oneWeComSet.get("EID").toString();
                        String appId = oneWeComSet.get("APPID").toString();

                        List<DataProcessBean> data = new ArrayList<>();

                        //查询会员资料并写入DCP_ISVWECOM_MEMBER
                        {
                            sql = " select a.memberid,a.unionid from crm_member a"
                                    + " left join dcp_isvwecom_member b on a.eid=b.eid and a.memberid=b.memberid"
                                    + " where a.eid='"+eId+"' and a.unionid is not null and b.memberid is null";
                            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
                            if (CollectionUtil.isNotEmpty(getQData)){
                                String[] columns = {"EID", "MEMBERID", "UNIONID", "EXTERNALUSERID", "PENDINGID", "VALIDDATE","OPENID"};

                                for (Map<String, Object> oneQData:getQData) {
                                    String memberId = oneQData.get("MEMBERID").toString();
                                    sql = " select openid from crm_memberidendity"
                                            + " where eid='" + eId + "' and memberid='" + memberId + "' and appid='" + appId + "' ";
                                    List<Map<String, Object>> getOpenQData = this.doQueryData(sql, null);
                                    if (CollectionUtil.isNotEmpty(getOpenQData)) {
                                        String openId = getOpenQData.get(0).get("OPENID").toString();

                                        DataValue[] insValue = new DataValue[]{
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(memberId, Types.VARCHAR),
                                                new DataValue(oneQData.get("UNIONID").toString(), Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue(openId, Types.VARCHAR),   //一个会员只能一个openid，且不允许变更，因为pengding是绑定openid的，变更后对应不上外部客户了
                                        };
                                        // 插入DCP_ISVWECOM_MEMBER
                                        InsBean ib = new InsBean("DCP_ISVWECOM_MEMBER", columns);
                                        ib.addValues(insValue);
                                        data.add(new DataProcessBean(ib));

                                        StaticInfo.dao.useTransactionProcessData(data);
                                        data.clear();
                                    }
                                }
                            }
                        }


                        //查询所有超过90天的pending，重新获取新的pending
                        {
                            sql = " select * from dcp_isvwecom_member "
                                    + " where eid='"+eId+"' and externaluserid is null and pendingid is not null and validdate<'"+sDate+"' ";
                            List<Map<String, Object>> getMember = StaticInfo.dao.executeQuerySQL(sql, null);
                            if (CollectionUtil.isNotEmpty(getMember)){
                                //该接口有调用频率限制，当subject_type为0时，按企业作如下的限制：10万次/小时、48万次/天、750万次/月；（注意这里是所有服务商共用企业额度的）
                                //每秒限制10次
                                int i = 0;
                                for (Map<String, Object> member :getMember) {
                                    if (i > 10){
                                        Thread.sleep(1500);
                                        i=0;
                                    }
                                    String memberId = member.get("MEMBERID").toString();
                                    String unionId = member.get("UNIONID").toString();
                                    String openId = member.get("OPENID").toString();
                                    String pendingId = member.get("PENDINGID").toString();

                                    //不再主动调用了，避免一下子几十万的会员最后全部都不能获取到外部客户ID，改成在商城里面去调用，商城弹出二维码给用户去添加企微外部客户才调
                                    //超过90天有效期的，需要重新再调用企微，是否能调用成功不确定，文档里面没有说明，公司也没有测试账号

                                    //unionid转换为第三方external_userid
                                    JSONObject jsonObject = dcpWeComUtils.unionid_to_external_userid(StaticInfo.dao,unionId,openId);
                                    if (jsonObject!=null){
                                        String external_userid = jsonObject.optString("external_userid","");
                                        String pending_id = jsonObject.optString("pending_id","");

                                        if (!Check.Null(external_userid) || (!Check.Null(pending_id))) {
                                            //更新 DCP_ISVWECOM_MEMBER
                                            UptBean ub = new UptBean("DCP_ISVWECOM_MEMBER");
                                            ub.addUpdateValue("EXTERNALUSERID", new DataValue(external_userid, Types.VARCHAR));

                                            //pending_id 有效期是90天，如果企微返回变了，重新记录有效期
                                            if (!Check.Null(pending_id) && !pending_id.equals(pendingId)) {
                                                ub.addUpdateValue("PENDINGID", new DataValue(pending_id, Types.VARCHAR));
                                                ub.addUpdateValue("VALIDDATE", new DataValue(PosPub.GetStringDate(sDate, 90), Types.VARCHAR));
                                            }

                                            //Condition
                                            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                            ub.addCondition("MEMBERID", new DataValue(memberId, Types.VARCHAR));
                                            data.add(new DataProcessBean(ub));

                                            StaticInfo.dao.useTransactionProcessData(data);
                                            data.clear();
                                        }
                                    }

                                    i++;
                                }
                            }
                        }


                        //查所有未关联到会员unionid的外部客户资料，向企微获取对应的pending，保存在内存中，然后比对会员的pending，进行关联
                        {

                            //有效期在90天内的
                            sql = " select a.externaluserid from dcp_isvwecom_custom a"
                                    + " left join dcp_isvwecom_member b on a.eid=b.eid and a.externaluserid=b.externaluserid"
                                    + " where a.eid='" + eId + "' and b.externaluserid is null";
                            List<Map<String, Object>> getCustom = StaticInfo.dao.executeQuerySQL(sql, null);
                            if (CollectionUtil.isNotEmpty(getCustom)) {
                                //该企业的外部联系人ID，最多可同时查询100个外部联系人
                                int a = 0;
                                String[] external_userid_Array = new String[100];
                                for (Map<String, Object> oneCustom : getCustom) {

                                    external_userid_Array[a] = oneCustom.get("EXTERNALUSERID").toString();

                                    if (a == 99) {
                                        //通过 external_userid 匹配 pending_id
                                        ExternalUseridToPending externalUseridToPending = dcpWeComUtils.external_userid_to_pending_id(StaticInfo.dao, external_userid_Array);
                                        if ("0".equals(externalUseridToPending.getErrcode())) {
                                            List<ExternalUseridToPending.Result> resultList = externalUseridToPending.getResult();
                                            if (CollectionUtil.isNotEmpty(resultList)) {
                                                for (ExternalUseridToPending.Result result : resultList) {
                                                    String external_userid = result.getExternal_userid();
                                                    String pending_id = result.getPending_id();

                                                    if (!Check.Null(external_userid) && !Check.Null(pending_id)) {
                                                        sql = " select * from dcp_isvwecom_member a "
                                                                + " where a.eid='" + eId + "' and a.pendingid='" + pending_id + "' ";  //and a.externaluserid is not null 加了可能有问题，理论上不会出现
                                                        List<Map<String, Object>> getMember = StaticInfo.dao.executeQuerySQL(sql, null);
                                                        if (CollectionUtil.isNotEmpty(getMember)) {
                                                            String memberId = getMember.get(0).get("MEMBERID").toString();

                                                            //更新 DCP_ISVWECOM_MEMBER
                                                            UptBean ub = new UptBean("DCP_ISVWECOM_MEMBER");
                                                            ub.addUpdateValue("EXTERNALUSERID", new DataValue(external_userid, Types.VARCHAR));

                                                            //Condition
                                                            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                                            ub.addCondition("MEMBERID", new DataValue(memberId, Types.VARCHAR));

                                                            data.add(new DataProcessBean(ub));
                                                            StaticInfo.dao.useTransactionProcessData(data);
                                                            data.clear();
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        external_userid_Array = new String[100];
                                        a = 0;
                                    } else {
                                        a++;
                                    }
                                }
                            }
                        }
                    }
                }
            }



        }catch (Exception e){
            logger.error("\r\n*************** WeCom_UnionIdGet 企微获取unionid 异常: "+e.getMessage());
        }
        finally {
            bRun=false;
            logger.info("\r\n*************** WeCom_UnionIdGet 企微获取unionid END ****************\r\n");
        }


        return "";

    }

}
