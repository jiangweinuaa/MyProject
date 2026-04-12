package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DeliveryBind_OpenReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryBind_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_DeliveryBind_Open
 *    說明：配送员绑定
 * 服务说明配送员绑定
 * @author wangzyc
 * @since  2021/5/19
 */
public class DCP_DeliveryBind_Open extends SPosAdvanceService<DCP_DeliveryBind_OpenReq, DCP_DeliveryBind_OpenRes> {
    @Override
    protected void processDUID(DCP_DeliveryBind_OpenReq req, DCP_DeliveryBind_OpenRes res) throws Exception {
        DCP_DeliveryBind_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String opNo = request.getOpNo();
        String openId = request.getOpenId();


        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createDate = dfDate.format(cal.getTime());
        String opNO = req.getApiUser().getUserCode();
        String opName = req.getApiUser().getUserName();


        try{
            String sql = this.getDelivery(req);
            List<Map<String, Object>> getDeliveryInfo = this.doQueryData(sql, null);

            if(!CollectionUtils.isEmpty(getDeliveryInfo)){
                // 更新配送员的登录信息
                UptBean ub1 = null;
                ub1 = new UptBean("DCP_DELIVERYMAN");
                //Value
                ub1.addUpdateValue("OPENID", new DataValue(openId, Types.VARCHAR));
                ub1.addUpdateValue("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addUpdateValue("LOGINTIME", new DataValue(createDate, Types.DATE));

                ub1.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(createDate, Types.DATE));
                // condition
                ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                ub1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
                this.doExecuteDataToDB();
            }else{
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("当前员工不存在，请重新操作");
                return;
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        }catch (Exception e){
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败！" +e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DeliveryBind_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DeliveryBind_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DeliveryBind_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DeliveryBind_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        DCP_DeliveryBind_OpenReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getOpNo())) {
            errCt++;
            errMsg.append("配送员编号不能为Null, ");
            isFail = true;
        }
        if (Check.Null(request.getShopId())) {
            errCt++;
            errMsg.append("最近一次登录门店不能为Null, ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DeliveryBind_OpenReq> getRequestType() {
        return new TypeToken<DCP_DeliveryBind_OpenReq>(){};
    }

    @Override
    protected DCP_DeliveryBind_OpenRes getResponseType() {
        return new DCP_DeliveryBind_OpenRes();
    }

    /**
     * 查询配送员是否存在
     * @param req
     * @return
     */
    private String getDelivery(DCP_DeliveryBind_OpenReq req){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("select * from DCP_DELIVERYMAN " +
                " where EID = '"+req.geteId()+"' and OPNO = '"+req.getRequest().getOpNo()+"'");
        sql = sqlbuf.toString();
        return sql;
    }
}
