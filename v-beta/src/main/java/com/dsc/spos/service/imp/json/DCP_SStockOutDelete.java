package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SStockOutDeleteReq;
import com.dsc.spos.json.cust.req.DCP_SStockOutDeleteReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_SStockOutDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：SStockOutDeleteDCP
 *    說明：自采出库单删除
 * 服务说明：自采出库单删除
 * @author JZMA
 * @since  2018-11-20
 */
public class DCP_SStockOutDelete extends SPosAdvanceService<DCP_SStockOutDeleteReq, DCP_SStockOutDeleteRes>{

    @Override
    protected boolean isVerifyFail(DCP_SStockOutDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        String sStockOutNO = request.getsStockOutNo();
        if (Check.Null(sStockOutNO)) {
            isFail = true;
            errMsg.append("自采退货单单号不可为空值, ");
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected void processDUID(DCP_SStockOutDeleteReq req, DCP_SStockOutDeleteRes res) throws Exception {
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String shopId = req.getShopId();
        levelElm request = req.getRequest();
        String sStockOutNO = request.getsStockOutNo();
        try
        {
            String sql ="select status from DCP_sstockout "
                    + " where EID='"+eId+"' and organizationno='"+organizationNO+"' and sstockoutno='"+sStockOutNO+"' "
                    + " and status='0' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);

            if (getQData != null && getQData.isEmpty() == false)
            {
                //DCP_SSTOCKOUT
                DelBean db1 = new DelBean("DCP_SSTOCKOUT");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                db1.addCondition("SStockOutNO", new DataValue(sStockOutNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //DCP_SSTOCKOUT_DETAIL
                DelBean db2 = new DelBean("DCP_SSTOCKOUT_DETAIL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                db2.addCondition("SStockOutNO", new DataValue(sStockOutNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //DCP_SSTOCKOUT_IMAGE    /// EID, ORGANIZATIONNO, SHOPID, SSTOCKOUTNO, ITEM
                DelBean db3 = new DelBean("DCP_SSTOCKOUT_IMAGE");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db3.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db3.addCondition("SSTOCKOUTNO", new DataValue(sStockOutNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

                DelBean db4 = new DelBean("DCP_SSTOCKOUT_BATCH");
                db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db4.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db4.addCondition("SSTOCKOUTNO", new DataValue(sStockOutNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db4));

                DelBean db5 = new DelBean("DCP_TRANSACTION");
                db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db5.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                db5.addCondition("BILLNO", new DataValue(sStockOutNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db5));

                String detailSql="select a.ofno as NOTICENO,a.oitem as NOTICEITEM " +
                        " from DCP_SSTOCKOUT_DETAIL a " +
                        " where a.eid='"+eId+"' " +
                        " and a.organizationno='"+organizationNO+"' " +
                        " and a.SSTOCKOUTNO='"+sStockOutNO+"' "
                        ;
                List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

                if(CollUtil.isNotEmpty(detailList)){
                    List<Map<String, Object>> noticeNos = detailList.stream().filter(x -> Check.NotNull(x.get("NOTICENO").toString())).distinct().collect(Collectors.toList());
                    for (Map<String, Object> noticeMap : noticeNos){
                        String noticeNo = noticeMap.get("NOTICENO").toString();
                        UptBean ub2 = new UptBean("DCP_STOCKOUTNOTICE");
                        //add Value
                        ub2.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));//待出货

                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                        ub2.addCondition("BILLNO", new DataValue(noticeNo, Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub2));
                    }
                }
                for (Map<String, Object> detailMap : detailList){
                    String noticeNo = detailMap.get("NOTICENO").toString();
                    String noticeItem = detailMap.get("NOTICEITEM").toString();

                    if(Check.NotNull(noticeNo)&&Check.NotNull(noticeItem)) {

                        UptBean ub2 = new UptBean("DCP_STOCKOUTNOTICE_DETAIL");
                        //add Value
                        ub2.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));//待出货

                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                        ub2.addCondition("BILLNO", new DataValue(noticeNo, Types.VARCHAR));
                        ub2.addCondition("ITEM", new DataValue(noticeItem, Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub2));
                    }

                }

                //删除内部结算
                ColumnDataValue condition = new ColumnDataValue();
                condition.add("EID", req.geteId());
                condition.add("BILLNO", req.getRequest().getsStockOutNo());

                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition)));



                this.doExecuteDataToDB();

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }
        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SStockOutDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SStockOutDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SStockOutDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected TypeToken<DCP_SStockOutDeleteReq> getRequestType() {
        return new TypeToken<DCP_SStockOutDeleteReq>(){};
    }

    @Override
    protected DCP_SStockOutDeleteRes getResponseType() {
        return new DCP_SStockOutDeleteRes();
    }

}
