package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComStaffUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ISVWeComStaffUpdateReq.Shop;
import com.dsc.spos.json.cust.res.DCP_ISVWeComStaffUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ISVWeComStaffUpdate
 * 服务说明：更新企微员工列表
 * @author jinzma
 * @since  2023-09-13
 */
public class DCP_ISVWeComStaffUpdate extends SPosAdvanceService<DCP_ISVWeComStaffUpdateReq, DCP_ISVWeComStaffUpdateRes> {
    @Override
    protected void processDUID(DCP_ISVWeComStaffUpdateReq req, DCP_ISVWeComStaffUpdateRes res) throws Exception {
        try{
            String eId = req.geteId();
            String type = req.getRequest().getType();  //类型：0绑定 1解绑(删除)
            String opNo = req.getRequest().getOpNo();

            if (type.equals("0")){
                String sql = "select opno,userid from dcp_isvwecom_staffs a where a.eid='"+eId+"' and a.opno='"+opNo+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                String userId = "";
                if (!CollectionUtils.isEmpty(getQData)){
                    userId = getQData.get(0).get("USERID").toString();
                }

                //通过手机号调企微获取员工userId
                String telephone = req.getRequest().getTelephone();
                if (Check.Null(userId)) {
                    DCPWeComUtils dcpWeComUtils = new DCPWeComUtils();
                    userId = dcpWeComUtils.getUserid(dao, telephone);
                    if (Check.Null(userId)) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信获取USERID失败,详情请查询日志获取");
                    }
                }

                if (CollectionUtils.isEmpty(getQData)) {
                    //插入DCP_ISVWECOM_STAFFS
                    String[] columns1 = {
                            "EID", "OPNO", "OPNAME", "USERID", "TELEPHONE", "STATUS", "ACCOUNTTYPE", "ACTIVETIME", "EXPIRETIME", "LASTMODITIME"
                    };
                    DataValue[] insValue1 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(opNo, Types.VARCHAR),
                            new DataValue(req.getRequest().getOpName(), Types.VARCHAR),
                            new DataValue(userId, Types.VARCHAR),
                            new DataValue(telephone, Types.VARCHAR),
                            new DataValue("100", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    };

                    InsBean ib1 = new InsBean("DCP_ISVWECOM_STAFFS", columns1);
                    ib1.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib1));
                }

                //删除DCP_ISVWECOM_STAFFS_SHOP
                DelBean db2 = new DelBean("DCP_ISVWECOM_STAFFS_SHOP");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("USERID", new DataValue(userId, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(db2));

                //插入DCP_ISVWECOM_STAFFS_SHOP
                if (!CollectionUtils.isEmpty(req.getRequest().getShopList())) {
                    String[] columns2 = {"EID", "USERID", "SHOPID"};

                    for (Shop shop : req.getRequest().getShopList()) {
                        DataValue[] insValue2 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(userId, Types.VARCHAR),
                                new DataValue(shop.getShopId(), Types.VARCHAR),
                        };
                        InsBean ib2 = new InsBean("DCP_ISVWECOM_STAFFS_SHOP", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }
            }

            if (type.equals("1")){
                String sql = "select opno,userid from dcp_isvwecom_staffs a where a.eid='"+eId+"' and a.opno='"+opNo+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (CollectionUtils.isEmpty(getQData)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "员工编号:"+opNo+" 不存在,无法解绑");
                }
                String userId = getQData.get(0).get("USERID").toString();

                //删除DCP_ISVWECOM_STAFFS
                DelBean db1 = new DelBean("DCP_ISVWECOM_STAFFS");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(db1));

                //删除DCP_ISVWECOM_STAFFS_SHOP
                DelBean db2 = new DelBean("DCP_ISVWECOM_STAFFS_SHOP");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("USERID", new DataValue(userId, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(db2));

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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComStaffUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComStaffUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComStaffUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ISVWeComStaffUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }else{
            if (Check.Null(req.getRequest().getOpNo())){
                errMsg.append("用户编号不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getOpName())){
                errMsg.append("用户名称不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getTelephone())){
                errMsg.append("手机号不能为空,");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getType())){
                errMsg.append("类型不能为空,");
                isFail = true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComStaffUpdateReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComStaffUpdateReq>(){};
    }

    @Override
    protected DCP_ISVWeComStaffUpdateRes getResponseType() {
        return new DCP_ISVWeComStaffUpdateRes();
    }
}
