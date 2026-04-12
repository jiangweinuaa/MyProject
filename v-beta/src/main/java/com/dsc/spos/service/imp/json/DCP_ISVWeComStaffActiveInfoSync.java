package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComStaffActiveInfoSyncReq;
import com.dsc.spos.json.cust.req.ISV_WeComStaffActiveInfoSyncReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComStaffActiveInfoSyncRes;
import com.dsc.spos.json.cust.res.ISV_WeComStaffActiveInfoSyncRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComStaffActiveInfoSync
 * 服务说明：更新企微员工激活信息
 * @author jinzma
 * @since  2023-09-12
 */
public class DCP_ISVWeComStaffActiveInfoSync extends SPosAdvanceService<DCP_ISVWeComStaffActiveInfoSyncReq, DCP_ISVWeComStaffActiveInfoSyncRes> {
    @Override
    protected void processDUID(DCP_ISVWeComStaffActiveInfoSyncReq req, DCP_ISVWeComStaffActiveInfoSyncRes res) throws Exception {
        try{
            String eId = req.geteId();
            String isvUrl = PosPub.getISV_URL();
            
            String sql=" select corpid from dcp_isvwecom_empower ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getQData)) {
                String corpId = getQData.get(0).get("CORPID").toString();
                
                ISV_WeComStaffActiveInfoSyncReq reqIsv = new ISV_WeComStaffActiveInfoSyncReq();
                ISV_WeComStaffActiveInfoSyncReq.levelElm levelElm = reqIsv.new levelElm();
                levelElm.setCorpId(corpId);
                reqIsv.setRequest(levelElm);
                reqIsv.setServiceId("ISV_WeComStaffActiveInfoSync");
                ParseJson pj = new ParseJson();
                String reqStr = pj.beanToJson(reqIsv);
                String resStr = HttpSend.doPost(isvUrl, reqStr, null,"");
          
                ISV_WeComStaffActiveInfoSyncRes res_isv = pj.jsonToBean(resStr, new TypeToken<ISV_WeComStaffActiveInfoSyncRes>() {
                });
                if (res_isv.isSuccess()) {
                    //回写员工激活信息
                    List<ISV_WeComStaffActiveInfoSyncRes.level1Elm> accountList = res_isv.getDatas();
                    if (!CollectionUtils.isEmpty(accountList)) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        for (ISV_WeComStaffActiveInfoSyncRes.level1Elm account:accountList){
                            long seconds;
                            seconds = Long.parseLong(account.getActive_time()) * 1000L;  //单位： 毫秒
                            Date activeTime = new Date();
                            activeTime.setTime(seconds);
                            
                            seconds = Long.parseLong(account.getExpire_time()) * 1000L;  //单位： 毫秒
                            Date expireTime = new Date();
                            expireTime.setTime(seconds);
                            
                            UptBean ub = new UptBean("DCP_ISVWECOM_STAFFS");
                            ub.addUpdateValue("ACCOUNTTYPE", new DataValue(account.getType(), Types.VARCHAR));
                            ub.addUpdateValue("ACTIVETIME", new DataValue(df.format(activeTime), Types.DATE));
                            ub.addUpdateValue("EXPIRETIME", new DataValue(df.format(expireTime), Types.DATE));
                            ub.addUpdateValue("LASTMODITIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                            
                            ub.addCondition("USERID", new DataValue(account.getUserid(), Types.VARCHAR));
                            
                            this.addProcessData(new DataProcessBean(ub));
                        }
                    }
                    
                }
            }
            
            this.doExecuteDataToDB();
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_ISVWeComStaffActiveInfoSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComStaffActiveInfoSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComStaffActiveInfoSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComStaffActiveInfoSyncReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ISVWeComStaffActiveInfoSyncReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComStaffActiveInfoSyncReq>(){};
    }
    
    @Override
    protected DCP_ISVWeComStaffActiveInfoSyncRes getResponseType() {
        return new DCP_ISVWeComStaffActiveInfoSyncRes();
    }
}
