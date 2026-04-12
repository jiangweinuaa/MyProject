package com.dsc.spos.scheduler.job;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONArray;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.Check;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DCP_GoodsShelfStatusJob extends InitJob {

    public static String JOB_GROUP_NAME = "DCP_GoodsShelfStatusJob";

    Logger logger = LogManager.getLogger(DCP_GoodsShelfStatusJob.class.getName());

    @Override
    public String doExe(Object param) throws Exception {
        try
        {
            List<DataProcessBean> data = new ArrayList<DataProcessBean>();
            JobDataMap map = Convert.convert(JobDataMap.class, param);
            logger.info("\r\n***********商品渠道/门店自动上下架处理开始:" + JSONArray.toJSONString(map));
            String shopid = map.getOrDefault("SHOPID","").toString();

            // 根据门店传参区分门店上下架还是渠道上下架
            if(Check.Null(shopid)){
                // 根据渠道上下架
                //修改DCP_GOODS_SHELF_RANGE.STATUS
                UptBean ub =new UptBean("DCP_GOODS_SHELF_RANGE");
                ub.addUpdateValue("STATUS", new DataValue("ON".equals(map.get("TYPE").toString())?100:0, Types.INTEGER));
                ub.addCondition("EID", new DataValue(map.get("EID").toString(), Types.VARCHAR));
                ub.addCondition("PLUNO", new DataValue(map.get("PLUNO").toString(), Types.VARCHAR));
                ub.addCondition("CHANNELID", new DataValue(map.get("CHANNELID").toString(), Types.VARCHAR));
                data.add(new DataProcessBean(ub));
                StaticInfo.dao.useTransactionProcessData(data);
                logger.info("\r\n***********商品渠道自动上下架处理结束");
            }else {
                // 根据门店上下架

                //修改DCP_GOODS_SHELF_RANGE.STATUS
                UptBean ub =new UptBean("DCP_GOODS_SHELF_RANGE");
                ub.addUpdateValue("STATUS", new DataValue("ON".equals(map.get("TYPE").toString())?100:0, Types.INTEGER));
                ub.addCondition("EID", new DataValue(map.get("EID").toString(), Types.VARCHAR));
                ub.addCondition("PLUNO", new DataValue(map.get("PLUNO").toString(), Types.VARCHAR));
                ub.addCondition("CHANNELID", new DataValue(map.get("CHANNELID").toString(), Types.VARCHAR));
                ub.addCondition("SHOPID", new DataValue(shopid, Types.VARCHAR));
                data.add(new DataProcessBean(ub));
                StaticInfo.dao.useTransactionProcessData(data);
                logger.info("\r\n***********商品门店自动上下架处理结束");
            }


        }
        catch (Exception e)
        {
            logger.error("\r\n***********商品渠道/门店自动上下架处理异常:" + e.getMessage());
        }
        return super.doExe(param);
    }
}