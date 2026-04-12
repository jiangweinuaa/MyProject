package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_LoginDelivery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_LoginDelivery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_LoginDelivery_Open
 * 說明：配送员登录
 * 服务说明：配送员登录
 *
 * @author wangzyc
 * @since 2021/4/23
 */
public class DCP_LoginDelivery_Open extends SPosBasicService<DCP_LoginDelivery_OpenReq, DCP_LoginDelivery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_LoginDelivery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

        if (Check.Null(req.getRequest().getOpNo())) {
            errCt++;
            errMsg.append("账号不能为Null, ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getPassword())) {
            errCt++;
            errMsg.append("密码不能为Null, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_LoginDelivery_OpenReq> getRequestType() {
        return new TypeToken<DCP_LoginDelivery_OpenReq>() {
        };
    }

    @Override
    protected DCP_LoginDelivery_OpenRes getResponseType() {
        return new DCP_LoginDelivery_OpenRes();
    }

    @Override
    protected DCP_LoginDelivery_OpenRes processJson(DCP_LoginDelivery_OpenReq req) throws Exception {
        DCP_LoginDelivery_OpenRes res = null;
        res = this.getResponse();
        DCP_LoginDelivery_OpenRes.level1Elm level1Elm = res.new level1Elm();
        res.setDatas(level1Elm);

        try {
            String sql = doLogin(req);
            List<Map<String, Object>> data = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(data)) {

                sql = "SELECT * FROM  PLATFORM_BASESETTEMP  WHERE eid = '"+req.geteId()+"' AND ITEM in('BaLoginSmsAlerts')";
                List<Map<String, Object>> getUrl = this.doQueryData(sql, null);

                        DCP_LoginDelivery_OpenRes.level2Elm level2Elm = res.new level2Elm();
                        String opNo = data.get(0).get("OPNO").toString();
                        String opName = data.get(0).get("OPNAME").toString();
                        String ipone = data.get(0).get("PHONE").toString();
                        String viewAbleDay = data.get(0).get("VIEWABLEDAY").toString();
                        String dcpService = "";
                        String crmService = "";
                        String smsAlerts = "";
                        String posService = "";
                       if(!CollectionUtils.isEmpty(getUrl)){
                           for (Map<String, Object> url : getUrl) {
                               String item = url.get("ITEM").toString();
                               String value = url.get("ITEMVALUE").toString();
//                               if(item.equals("PlatformCentreURL")){
//                                   dcpService = value;
//                               }else if(item.equals("CrmUrl")){
//                                   crmService = value;
//                               }else if(item.equals("PosUrl")){
//                                   posService = value;
                               //}else 
                            	   if(item.equals("BaLoginSmsAlerts")){
                                   smsAlerts = value;
                               }
                           }
                       }
                       dcpService=PosPub.getDCP_URL(req.geteId());
                       crmService = PosPub.getCRM_URL(req.geteId());
                       posService= PosPub.getPOS_URL(req.geteId());
                       level1Elm.setOpNo(opNo);
                       level1Elm.setOpName(opName);
                       level1Elm.setIpone(ipone);
                       level1Elm.setViewAbleDay(viewAbleDay);
                       level1Elm.setDcpService(dcpService);
                       level1Elm.setCrmService(crmService);
                       level1Elm.setPosService(posService);
                       level1Elm.setShopList(new ArrayList<DCP_LoginDelivery_OpenRes.level2Elm>());
                for (Map<String, Object> getOrg : data) {
                    DCP_LoginDelivery_OpenRes.level2Elm lv2 = res.new level2Elm();
                    String org = getOrg.get("ORG").toString();
                    String org_name = getOrg.get("ORG_NAME").toString();
                    lv2.setShopId(org);
                    lv2.setShopName(org_name);
                    lv2.setSmsAlerts(smsAlerts);
                    level1Elm.getShopList().add(lv2);
                }
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "登录失败，用户名或密码错误！");
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_LoginDelivery_OpenReq req) throws Exception {
        return null;
    }

    /**
     * 查询账号密码是否存在
     *
     * @param req
     * @return
     */
    private String doLogin(DCP_LoginDelivery_OpenReq req) {
        DCP_LoginDelivery_OpenReq.level1Elm request = req.getRequest();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_DELIVERYMAN a " +
                " LEFT JOIN DCP_DELIVERYMAN_ORG b ON a.eid = b.EID AND a.OPNO = b.OPNO " +
                " LEFT JOIN DCP_ORG_LANG c ON a.EID = c.EID AND c.ORGANIZATIONNO = b.ORG AND c.LANG_TYPE = '" + req.getLangType() + "' " +
                " WHERE a.status = '100' and a.eid = '" + req.geteId() + "' and a.OPNO = '" + request.getOpNo() + "' and a.PASSWORD = '" + request.getPassword() + "'");
        sql = sqlbuf.toString();
        return sql;
    }
}
