package com.dsc.spos.utils.wxcp;

import cn.hutool.core.util.StrUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ConvertUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceOkHttpImpl;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业微信工具类
 *
 * @date 2020-12-25
 */
@Slf4j
public class WxCpServiceUtils {
    // key:corpid
    private static Map<Integer, WxCpService> wxCpServiceMap = new HashMap<>();
    
    public static WxCpService getWxCpService(Integer agentId) throws DispatchService.SPosCodeException {
        if (wxCpServiceMap.get(agentId) == null) {
            synchronized (WxCpServiceUtils.class) {
                if (wxCpServiceMap.get(agentId) == null) {
                    initAgentConfig(agentId);
                }
            }
        }
        
        return wxCpServiceMap.get(agentId);
    }
    
    /**
     * 获取企业微信配置 只有一笔数据
     * @return
     * @throws Exception
     */
    public static WxCpConfig getWxCpConfigFromDB() throws Exception {
        List<Map<String, Object>> maps = StaticInfo.dao.executeQuerySQL("select EID, CORPID, AGENTID, SECRET from DCP_ENTERPRISECHATSET", new String[]{});
        if (maps.isEmpty()) {
            return null;
        }
        
        return ConvertUtils.mapToBean(maps.get(0), WxCpConfig.class);
    }
    
    private static void initAgentConfig(Integer agentId) throws DispatchService.SPosCodeException {
        try {
            List<Map<String, Object>> maps = StaticInfo.dao.executeQuerySQL(
                    "select EID, CORPID, AGENTID, SECRET from DCP_ENTERPRISECHATSET where agentId = ?",
                    new String[]{String.valueOf(agentId)});
            if (maps.isEmpty()) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E202, "未找到企业微信接入配置，agentId:" + agentId);
            }
            WxCpService service = new WxCpServiceOkHttpImpl();
            WxCpDefaultConfigImpl configStorage = new WxCpDefaultConfigImpl();
            configStorage.setCorpId(maps.get(0).get("CORPID").toString());
            configStorage.setAgentId(agentId);
            configStorage.setCorpSecret(maps.get(0).get("SECRET").toString());
            service.setWxCpConfigStorage(configStorage);
            wxCpServiceMap.put(configStorage.getAgentId(), service);
        } catch (Exception e) {
            log.error("initAgentConfig error", e);
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E202,
                    StrUtil.format("初始化企业微信接入配置异常，agentId: {}，异常信息：{}", agentId, e.getMessage()));
        }
    }
    
    public static void writeLoginLog(WxCpServiceUtils.WxCpLoginLog loginLog) {
        try {
            List<DataProcessBean> beans = new ArrayList<>();
            String[] columns = {
                    "EID", "LOGINTIME", "CORPID", "AGENTID",
                    "APPTYPE", "SHOPID", "MACHINEID", "USERID", "STATUS",
                    "MACHINECODE","NAME"
            };
            DataValue[] insValue2 = new DataValue[]{
                    new DataValue(loginLog.getEId(), Types.VARCHAR),
                    new DataValue(loginLog.getLoginTime(), Types.VARCHAR),
                    new DataValue(loginLog.getCorpId(), Types.VARCHAR),
                    new DataValue(loginLog.getAgentId(), Types.VARCHAR),
                    new DataValue(loginLog.getAppType(), Types.VARCHAR),
                    new DataValue(loginLog.getShopId(), Types.VARCHAR),
                    new DataValue(loginLog.getMachineId(), Types.VARCHAR),
                    new DataValue(loginLog.getUserId(), Types.VARCHAR),
                    new DataValue(loginLog.getStatus(), Types.VARCHAR),
                    new DataValue(loginLog.getMachineCode(), Types.VARCHAR),
                    new DataValue(loginLog.getName(), Types.VARCHAR),
            };
            InsBean ib2 = new InsBean("DCP_ENTERPRISECHATLOGINLOG", columns);
            ib2.addValues(insValue2);
            beans.add(new DataProcessBean(ib2));
            StaticInfo.dao.useTransactionProcessData(beans);
        } catch (Exception e) {
            log.error("写企业微信登录日志异常", e);
        }
    }
    
    @Data
    public static class WxCpConfig{
        private String eId;
        private String corpId;
        private String agentId;
        private String secret;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WxCpUserRedisData{
        private Integer errcode;
        private String errmsg;
        private WxCpUser user;
    }
    
    @Data
    public static class WxCpLoginLog{
        private String eId;
        private String loginTime;
        private String corpId;
        private String agentId;
        private String appType;
        private String shopId;
        private String machineId;
        private String userId;
        private String status;
        private String machineCode;
        private String name;
    }
}
