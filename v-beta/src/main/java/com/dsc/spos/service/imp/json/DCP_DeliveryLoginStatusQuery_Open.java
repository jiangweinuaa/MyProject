package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DeliveryLoginStatusQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryLoginStatusQuery_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服務函數：DCP_DeliveryLoginStatusQuery_Open
 *    說明：查询配送员登录状态
 * 服务说明 查询配送员登录状态
 * @author wangzyc
 * @since  2021/5/19
 */
@Data
public class DCP_DeliveryLoginStatusQuery_Open  extends SPosAdvanceService<DCP_DeliveryLoginStatusQuery_OpenReq, DCP_DeliveryLoginStatusQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_DeliveryLoginStatusQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        DCP_DeliveryLoginStatusQuery_OpenReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getOpenId())) {
            errCt++;
            errMsg.append("微信用户ID不能为Null, ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DeliveryLoginStatusQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_DeliveryLoginStatusQuery_OpenReq>(){};
    }

    @Override
    protected DCP_DeliveryLoginStatusQuery_OpenRes getResponseType() {
        return new DCP_DeliveryLoginStatusQuery_OpenRes();
    }

    @Override
    protected DCP_DeliveryLoginStatusQuery_OpenRes processJson(DCP_DeliveryLoginStatusQuery_OpenReq req) throws Exception {
        DCP_DeliveryLoginStatusQuery_OpenRes res = null;
        res = this.getResponse();
        res.setDatas(res.new level1Elm());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createDate = dfDate.format(cal.getTime());
        String createOpId = req.getApiUser().getUserCode();
        String createOpName = req.getApiUser().getUserName();


        try{
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getInfo = this.doQueryData(sql, null);

            if(!CollectionUtils.isEmpty(getInfo)){
                // 根据 openId 检索 应该只有一个配送员
                Map<String, Object> delivery = getInfo.get(0);
                DCP_DeliveryLoginStatusQuery_OpenRes.level1Elm level1Elm = res.new level1Elm();

                String opNo = delivery.get("OPNO").toString();
                String opName = delivery.get("OPNAME").toString();
                String ipone = delivery.get("PHONE").toString();
                String viewAbleDay = delivery.get("VIEWABLEDAY").toString();
                String shopId = delivery.get("SHOPID").toString();
                String loginTime = delivery.get("LOGINTIME").toString();
                Date loginDateTime = df.parse(loginTime);

                level1Elm.setOpNo(opNo);
                level1Elm.setOpName(opName);
                level1Elm.setIpone(ipone);
                level1Elm.setViewAbleDay(viewAbleDay);
                level1Elm.setShopList(new ArrayList<DCP_DeliveryLoginStatusQuery_OpenRes.level2Elm>());

                for (Map<String, Object> getOrg : getInfo) {
                    DCP_DeliveryLoginStatusQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                    String org = getOrg.get("ORG").toString();
                    String org_name = getOrg.get("ORG_NAME").toString();
                    lv2.setShopId(org);
                    lv2.setShopName(org_name);
                    level1Elm.getShopList().add(lv2);
                }

                sql = this.getSystemParameter(req);
                String BaLoginValidity = "";
                String crmService = "";
                List<Map<String, Object>> getParameter = this.doQueryData(sql, null);
                if(!CollectionUtils.isEmpty(getParameter)){
                    for (Map<String, Object> url : getParameter) {
                        String item = url.get("ITEM").toString();
                        String value = url.get("ITEMVALUE").toString();
                        if(item.equals("BaLoginValidity")){
                            BaLoginValidity = value;
                        }//else if(item.equals("CrmUrl")){
                          //  crmService = value;
                       // }
                    }
                }
                crmService= PosPub.getCRM_URL(req.geteId());
                level1Elm.setCrmService(crmService);
                // 若参数为空则不限制有效时间 若不为空则根据参数去判断是否过期
                if(!Check.Null(BaLoginValidity)){

                    String date = df.format(cal.getTime());
                    cal.setTime(loginDateTime);
                    cal.set(Calendar.DATE, cal.get(Calendar.DATE) + Integer.parseInt(BaLoginValidity));
                    String timeout = df.format(cal.getTime());

                    //  过期
                    if(date.compareTo(timeout)==0 || date.compareTo(timeout)>0){
                        shopId = "";

                        UptBean ub1 = null;
                        ub1 = new UptBean("DCP_DELIVERYMAN");
                        //Value
                        ub1.addUpdateValue("OPENID", new DataValue("", Types.VARCHAR));
                        ub1.addUpdateValue("SHOPID", new DataValue("", Types.VARCHAR));

                        ub1.addUpdateValue("LASTMODIOPID", new DataValue(createOpId, Types.VARCHAR));
                        ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(createOpName, Types.VARCHAR));
                        ub1.addUpdateValue("LASTMODITIME", new DataValue(createDate, Types.DATE));
                        // condition
                        ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                        ub1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub1));
                        this.doExecuteDataToDB();
                    }
                }
                level1Elm.setShopId(shopId);
                res.setDatas(level1Elm);
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        }catch (Exception e){
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败！" +e.getMessage());
        }

        return res;
    }


    @Override
    protected String getQuerySql(DCP_DeliveryLoginStatusQuery_OpenReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_DELIVERYMAN a  " +
                " LEFT JOIN DCP_DELIVERYMAN_ORG b ON  a.EID  = b.EID AND a.OPNO = b.OPNO  " +
                " LEFT JOIN DCP_ORG_LANG c ON a.EID = c.EID AND c.ORGANIZATIONNO = b.ORG AND c.LANG_TYPE = '" + req.getLangType() + "'" +
                " where a.EID  = '"+req.geteId()+"'  and a.OPENID = '"+req.getRequest().getOpenId()+"' and a.status = '100'");
        sql = sqlbuf.toString();
        return sql;
    }

    @Override
    protected void processDUID(DCP_DeliveryLoginStatusQuery_OpenReq req, DCP_DeliveryLoginStatusQuery_OpenRes res) throws Exception {

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DeliveryLoginStatusQuery_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DeliveryLoginStatusQuery_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DeliveryLoginStatusQuery_OpenReq req) throws Exception {
        return null;
    }

    /**
     * 查询系统参数
     * @param req
     * @return
     */
    private String getSystemParameter(DCP_DeliveryLoginStatusQuery_OpenReq req){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM  PLATFORM_BASESETTEMP  WHERE  ONSALE = 'Y' and eid = '"+req.geteId()+"' AND ITEM in('BaLoginValidity')");
        sql = sqlbuf.toString();
        return sql;
    }
}
