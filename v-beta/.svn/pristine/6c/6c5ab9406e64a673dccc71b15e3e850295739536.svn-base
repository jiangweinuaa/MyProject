package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.ISV_WeComStaffActiveInfoSyncReq;
import com.dsc.spos.json.cust.res.ISV_WeComStaffActiveInfoSyncRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.wecom.ISVWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务函数：ISV_WeComStaffActiveInfoSync
 * 服务说明：更新企微员工激活信息
 * @author jinzma
 * @since  2023-09-12
 */
public class ISV_WeComStaffActiveInfoSync extends SPosAdvanceService<ISV_WeComStaffActiveInfoSyncReq, ISV_WeComStaffActiveInfoSyncRes> {
    @Override
    protected void processDUID(ISV_WeComStaffActiveInfoSyncReq req, ISV_WeComStaffActiveInfoSyncRes res) throws Exception {
        try{
            List<ISV_WeComStaffActiveInfoSyncRes.level1Elm> datas = new ArrayList<>();

            ISVWeComUtils IsvWeComUtils = new ISVWeComUtils();
            String corpId = req.getRequest().getCorpId();
            String cursor = "";
            String ListActivedAccount = IsvWeComUtils.getListActivedAccount(dao,corpId,cursor);
            if (!Check.Null(ListActivedAccount)){
                //每次返回1000，最多调2次，总计2000 ，后面可以搞成循环
                for (int count=0; count<2; count++){
                    if (!Check.Null(ListActivedAccount)){
                        JSONObject resObject=new JSONObject(ListActivedAccount);
                        JSONArray accountList = resObject.getJSONArray("account_list");
                        if (accountList!=null && accountList.length()>0){
                            for (int i = 0; i < accountList.length(); i++) {
                                ISV_WeComStaffActiveInfoSyncRes.level1Elm account = res.new level1Elm();
                                account.setUserid(accountList.getJSONObject(i).opt("userid").toString());
                                account.setType(accountList.getJSONObject(i).opt("type").toString());
                                account.setActive_time(accountList.getJSONObject(i).opt("active_time").toString());
                                account.setExpire_time(accountList.getJSONObject(i).opt("expire_time").toString());
                                datas.add(account);
                            }
                        }
                        
                        //判断是否需要再次调用
                        String has_more = resObject.opt("has_more").toString();  //企微文档错误，合理的解释应该是是否有更多资料
                        if (!"0".equals(has_more)){
                            cursor = resObject.optString("next_cursor");
                            if (!Check.Null(cursor)){
                                ListActivedAccount = IsvWeComUtils.getListActivedAccount(dao,corpId,cursor);
                            }
                        }else{
                            break;
                        }
                    }
                }
            }
            
            res.setDatas(datas);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(ISV_WeComStaffActiveInfoSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(ISV_WeComStaffActiveInfoSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(ISV_WeComStaffActiveInfoSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(ISV_WeComStaffActiveInfoSyncReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<ISV_WeComStaffActiveInfoSyncReq> getRequestType() {
        return new TypeToken<ISV_WeComStaffActiveInfoSyncReq>(){};
    }
    
    @Override
    protected ISV_WeComStaffActiveInfoSyncRes getResponseType() {
        return new ISV_WeComStaffActiveInfoSyncRes();
    }
}
