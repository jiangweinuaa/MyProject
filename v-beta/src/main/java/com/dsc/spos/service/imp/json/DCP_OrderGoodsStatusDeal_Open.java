package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsStatusDeal_OpenReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsStatusDeal_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_OrderGoodsStatusDeal_Open
 *   說明：订单商品待确认处理
 * 服务说明：订单商品待确认处理
 * @author wangzyc
 * @since  2021-4-28
 */
public class DCP_OrderGoodsStatusDeal_Open extends SPosAdvanceService<DCP_OrderGoodsStatusDeal_OpenReq, DCP_OrderGoodsStatusDeal_OpenRes> {
    @Override
    protected void processDUID(DCP_OrderGoodsStatusDeal_OpenReq req, DCP_OrderGoodsStatusDeal_OpenRes res) throws Exception {
        String eId = req.geteId();
        DCP_OrderGoodsStatusDeal_OpenReq.level1Elm request = req.getRequest();
        String oprType = request.getOprType(); // oprType = 1/oprType =2
        String shopNo = request.getShopNo();
        String modifyBy = req.getOpNO();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String modifyDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String modifyTime = df.format(cal.getTime());

        try {
            String sql = this.getProdOrder(req);
            List<Map<String, Object>> getProdOrder = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getProdOrder)){
                for (Map<String, Object> orderNo : getProdOrder) {
                    String processtaskNo = orderNo.get("PROCESSTASKNO").toString();
                    if(!Check.Null(oprType)){

                       if(oprType.equals("1")){
                           // oprType=1，将ISREFUND刷成Y，再次查询时，排除ISREFUND=Y的数据；并修改商品状态goodsStatus3.已退单；
                           UptBean ub = null;
                           ub = new UptBean("DCP_PROCESSTASK");
                           ub.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                           ub.addUpdateValue("ISREFUND", new DataValue("Y", Types.VARCHAR));
                           ub.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                           ub.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
                           ub.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
                           ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                           // condition
                           ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                           ub.addCondition("SHOPID", new DataValue(shopNo, Types.VARCHAR));
                           ub.addCondition("PROCESSTASKNO", new DataValue(processtaskNo, Types.VARCHAR));
                           this.addProcessData(new DataProcessBean(ub));

                           UptBean ub2 = null;
                           ub2 = new UptBean("DCP_PROCESSTASK_DETAIL");
                           ub2.addUpdateValue("GOODSSTATUS", new DataValue("3", Types.VARCHAR));
                           // condition
                           ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                           ub2.addCondition("SHOPID", new DataValue(shopNo, Types.VARCHAR));
                           ub2.addCondition("PROCESSTASKNO", new DataValue(processtaskNo, Types.VARCHAR));
                           this.addProcessData(new DataProcessBean(ub2));
                       }

                       else if(oprType.equals("2")){
                           // oprType=2，将ISMODIFYSHIP刷成Y，再次查询时，排除ISMODIFYTIME=Y的数据；
                           UptBean ub = null;
                           ub = new UptBean("DCP_PROCESSTASK");
                           ub.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                           ub.addUpdateValue("ISMODIFYSHIP", new DataValue("Y", Types.VARCHAR));
                           ub.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                           ub.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
                           ub.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
                           ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                           // condition
                           ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                           ub.addCondition("SHOPID", new DataValue(shopNo, Types.VARCHAR));
                           ub.addCondition("PROCESSTASKNO", new DataValue(processtaskNo, Types.VARCHAR));
                           this.addProcessData(new DataProcessBean(ub));
                       }
                   }

                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行失败:"+e.getMessage());
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderGoodsStatusDeal_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderGoodsStatusDeal_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderGoodsStatusDeal_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderGoodsStatusDeal_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String shopNo = req.getRequest().getShopNo();
        List<String> orderList = req.getRequest().getOrderList();

        if (Check.Null(shopNo) )
        {
            errMsg.append("下订门店不可为空值, ");
            isFail = true;
        }
        if(CollectionUtils.isEmpty(orderList)){
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderGoodsStatusDeal_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderGoodsStatusDeal_OpenReq>(){};
    }

    @Override
    protected DCP_OrderGoodsStatusDeal_OpenRes getResponseType() {
        return new DCP_OrderGoodsStatusDeal_OpenRes();
    }

    /**
     * 查询 符合要求的生产订单
     * @param req
     * @return
     */
    private String getProdOrder(DCP_OrderGoodsStatusDeal_OpenReq req){
        DCP_OrderGoodsStatusDeal_OpenReq.level1Elm request = req.getRequest();
        String oprType = request.getOprType();
        List<String> orderList = request.getOrderList();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT a.PROCESSTASKNO  FROM DCP_PROCESSTASK a " +
                " WHERE a.eid = '"+req.geteId()+"' AND a.SHOPID = '"+request.getShopNo()+"'");
        if(!CollectionUtils.isEmpty(orderList)){
            sqlbuf.append(" AND a.OFNO IN (");
            for (String orderNo : orderList) {
                sqlbuf.append("'"+orderNo+"',");
            }
            sqlbuf.deleteCharAt(sqlbuf.length()-1);
            sqlbuf.append(" )");
        }
        if(!Check.Null(oprType)){
            if(oprType.equals("1")){
                sqlbuf.append(" AND (a.ISREFUND !='Y' OR a.ISREFUND IS null)");
            }else if(oprType.equals("2")){
                sqlbuf.append(" AND (a.ISMODIFYSHIP !='Y' OR a.ISMODIFYSHIP IS null)");
            }
        }
        sql = sqlbuf.toString();
        return sql;
    }
}
