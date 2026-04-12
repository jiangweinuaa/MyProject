package com.dsc.spos.scheduler.job;


import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
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
 * *****************企微QrCode永久码删除*****************
 * @author jinzma
 * 2024-02-22
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WeCom_QrCodeDelete extends InitJob {
    Logger logger = LogManager.getLogger(WeCom_QrCodeDelete.class.getName());
    //一天执行一次
    static boolean bRun = false;           //标记此服务是否正在执行中

    public String doExe() {

        logger.info("\r\n*************** WeCom_QrCodeDelete 企微QrCode永久码删除 START ****************\r\n");

        try{

            //系统日期和时间
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            String dateTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();

            //每天 000001 - 015959 执行
            if (sTime.compareTo("000001") >= 0 && sTime.compareTo("055959") < 0) {
                if(bRun) {
                    logger.info("\r\n*************** WeCom_QrCodeDelete 企微QrCode永久码删除 正在执行中  ****************\r\n");
                    return "";
                }
                bRun=true;

                String sql = " select * from dcp_isvwecom_tempqrcode where wecomdel='0' and createtime<=sysdate-2  ";
                List<Map<String, Object>> getQrCode = StaticInfo.dao.executeQuerySQL(sql, null);
                if (CollectionUtil.isNotEmpty(getQrCode)){
                    for (Map<String, Object>oneQrCode :getQrCode ){
                        List<DataProcessBean> data = new ArrayList<>();

                        String eId = oneQrCode.get("EID").toString();
                        String memberId = oneQrCode.get("MEMBERID").toString();
                        String config_id = oneQrCode.get("CONFIGID").toString();
                        try {
                            //调企微删除永久码
                            if (dcpWeComUtils.del_contact_way(StaticInfo.dao, config_id)) {
                                //更新 DCP_ISVWECOM_MEMBER
                                UptBean ub = new UptBean("DCP_ISVWECOM_TEMPQRCODE");

                                ub.addUpdateValue("WECOMDEL", new DataValue("1", Types.VARCHAR));
                                ub.addUpdateValue("LASTMODITIME", new DataValue(dateTime,Types.DATE));


                                //Condition
                                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub.addCondition("MEMBERID", new DataValue(memberId, Types.VARCHAR));
                                data.add(new DataProcessBean(ub));

                                StaticInfo.dao.useTransactionProcessData(data);
                            }
                        }catch (Exception e){
                            logger.error("\r\n*************** WeCom_QrCodeDelete 企微QrCode永久码删除调企微接口异常 config_id:"+config_id+" "+e.getMessage());
                        }
                    }
                }
            }



        }catch (Exception e){
            logger.error("\r\n*************** WeCom_QrCodeDelete 企微QrCode永久码删除 异常: "+e.getMessage());
        }
        finally {
            bRun=false;
            logger.info("\r\n*************** WeCom_QrCodeDelete 企微QrCode永久码删除 END ****************\r\n");
        }


        return "";

    }


}
