package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_KdsCookUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_KdsCookUpdate_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * @description: KDS机器人修改
 * @author: wangzyc
 * @create: 2021-09-13
 */
public class DCP_KdsCookUpdate_Open extends SPosAdvanceService<DCP_KdsCookUpdate_OpenReq, DCP_KdsCookUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_KdsCookUpdate_OpenReq req, DCP_KdsCookUpdate_OpenRes res) throws Exception {
        DCP_KdsCookUpdate_OpenReq.level1Elm request = req.getRequest();

        try {
            String sortId = request.getSortId();
            String status = request.getStatus();
            String cookId = request.getCookId();
            String cookName = request.getCookName();
            String toSortId = request.getToSortId();
            String shopId = request.getShopId();
            String machineId = request.getMachineId();
            String eId = req.geteId();

            String cookSQL = getCookSQL(req);
            List<Map<String, Object>> data = this.doQueryData(cookSQL, null);
            if(CollectionUtils.isEmpty(data)){
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("找不到该机器信息！");
            }
            else
            {
                if(Check.Null(toSortId))
                {
                    // 只是修改基础信息 不做上下移动
                    UptBean ub1 = new UptBean("DCP_KDSCOOKSET");
                    ub1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("COOKID",new DataValue(cookId, Types.VARCHAR));
                    ub1.addUpdateValue("COOKNAME",new DataValue(cookName, Types.VARCHAR));
                    ub1.addUpdateValue("STATUS",new DataValue(status, Types.VARCHAR));
                    ub1.addUpdateValue("MACHINEID",new DataValue(machineId, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));

                    //禁用机器人要清空加工任务中，此机器人已分配的菜品（待制作状态1的）
                    if ("Y".equals(status)==false)
                    {
                        UptBean ub_processTaskDetail = new UptBean("dcp_processtask_detail");
                        ub_processTaskDetail.addUpdateValue("COOKNAME",new DataValue("", Types.VARCHAR));
                        ub_processTaskDetail.addUpdateValue("COOKID",new DataValue("", Types.VARCHAR));

                        ub_processTaskDetail.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                        ub_processTaskDetail.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                        ub_processTaskDetail.addCondition("COOKID",new DataValue(cookId, Types.VARCHAR));
                        ub_processTaskDetail.addCondition("goodsstatus",new DataValue("1", Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub_processTaskDetail));
                    }
                }
                else
                {
                    // 做上移下移
                    int toSt = 0;
                    int st = 0;
                    if(Check.Null(sortId) || Check.Null(toSortId)){
                        res.setSuccess(false);
                        res.setServiceStatus("100");
                        res.setServiceDescription("服务执行失败：当前顺序 和 调整顺序  均不能为空！！！");
                        return ;
                    }else{
                        toSt = Integer.parseInt(toSortId);
                        st = Integer.parseInt(sortId);

                        UptBean ub2 = new UptBean("DCP_KDSCOOKSET");

                        if(toSt > st){ //相当于 下移
                            String[] sortList = new String[toSt-st];
                            for(int i=0; i < toSt-st ; i++) {
                                sortList[i] = String.valueOf(st +i+ 1);
                            }
                            String str1 = StringUtils.join(sortList,",");

                            ub2.addCondition("SORTID", new DataValue(str1, Types.VARCHAR, DataValue.DataExpression.IN));
                            ub2.addUpdateValue("SORTID ", new DataValue(1, Types.INTEGER, DataValue.DataExpression.SubSelf));
                        }else { //相当于上移
                            String[] sortList = new String[st-toSt];
                            for(int i=0; i < st- toSt ; i++) {
                                sortList[i] = String.valueOf(toSt + i);
                            }
                            String str1 = StringUtils.join(sortList,",");
                            ub2.addCondition("SORTID", new DataValue(str1, Types.ARRAY, DataValue.DataExpression.IN));//Greater表示大于
                            ub2.addUpdateValue("SORTID ", new DataValue(1, Types.INTEGER, DataValue.DataExpression.UpdateSelf));
                        }

                        ub2.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));

                        UptBean ub3 = new UptBean("DCP_KDSCOOKSET");
                        ub3.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                        ub3.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                        ub3.addCondition("COOKID",new DataValue(cookId, Types.VARCHAR));

                        ub3.addUpdateValue("SORTID ", new DataValue(toSortId,Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub2));
                        this.addProcessData(new DataProcessBean(ub3));
                    }
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_KdsCookUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_KdsCookUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_KdsCookUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_KdsCookUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsCookUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsCookUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_KdsCookUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_KdsCookUpdate_OpenRes getResponseType() {
        return new DCP_KdsCookUpdate_OpenRes();
    }

    protected String getCookSQL(DCP_KdsCookUpdate_OpenReq req)
    {
        String sql = null;
        DCP_KdsCookUpdate_OpenReq.level1Elm request = req.getRequest();
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT * FROM DCP_KDSCOOKSET WHERE EID='"+req.geteId()+"' AND SHOPID ='"+request.getShopId()+"'  and COOKID = '"+request.getCookId()+"'");
        sql = sqlbuf.toString();
        return sql;
    }

}
