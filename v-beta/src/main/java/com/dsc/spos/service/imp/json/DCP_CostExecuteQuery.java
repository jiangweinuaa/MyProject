package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CostExecuteQueryReq;
import com.dsc.spos.json.cust.res.DCP_CostExecuteQueryRes;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.progress.ProgressServiceFactory;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DCP_CostExecuteQuery extends SPosAdvanceService<DCP_CostExecuteQueryReq, DCP_CostExecuteQueryRes> {
    @Override
    protected void processDUID(DCP_CostExecuteQueryReq req, DCP_CostExecuteQueryRes res) throws Exception {

        DCP_CostExecuteQueryRes.Datas oneData = res.new Datas();

        oneData.setYear(req.getRequest().getYear());
        oneData.setPeriod(req.getRequest().getPeriod());
        oneData.setAccountID(req.getRequest().getAccountID());
        oneData.setAccount(req.getRequest().getAccount());
        oneData.setMainTaskId(req.getRequest().getMainTaskId());

        res.setDatas(oneData);

        String year = req.getRequest().getYear();
        String period = req.getRequest().getPeriod();
        String accountId = req.getRequest().getAccountID();
        String account = req.getRequest().getAccount();

        String mainTaskId = req.getRequest().getMainTaskId();
        String type = req.getRequest().getType();

        ProgressServiceFactory factory = ProgressServiceFactory.getInstance();

        if (StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(mainTaskId)) {

            ProgressService<?> service = factory.getProgress(type, mainTaskId);
            if (null != service) {
                oneData.setInputPrg(service.getNowStep() + "/" + service.getMaxStep());
                oneData.setImpStateInfo(service.getStepDescription());
                res.setSuccess(true);
                res.setServiceDescription("服务执行成功!");
            } else {
                res.setSuccess(false);
                res.setServiceDescription("未查询到对应的任务Id" + req.getRequest().getMainTaskId());
            }
        } else if (StringUtils.isNotEmpty(type)) {
            List<ProgressService<?>> list = factory.getProgressListByType(type);
            if (CollectionUtils.isNotEmpty(list)) {
                res.setServiceDescription("服务执行成功!");
                oneData.setCostList(new ArrayList<>());

                for (ProgressService<?> service : list) {
                    DCP_CostExecuteQueryRes.CostList oneCost = res.new CostList();

                    oneCost.setType(service.getType().getType());
                    oneCost.setMainTaskId(service.getProgressName());
                    oneCost.setInputPrg(service.getNowStep() + "/" + service.getMaxStep());
                    oneCost.setImpStateInfo(service.getStepDescription());

                    oneData.getCostList().add(oneCost);
                }
            } else {
                res.setSuccess(false);
                res.setServiceDescription("未查询到对应的任务Id" + req.getRequest().getMainTaskId());
            }
        } else {

            List<ProgressService<?>> list = factory.getProgressServices();
            if (CollectionUtils.isNotEmpty(list)) {
                res.setServiceDescription("服务执行成功!");
                oneData.setCostList(new ArrayList<>());

                for (ProgressService<?> service : list) {
                    DCP_CostExecuteQueryRes.CostList oneCost = res.new CostList();

                    oneCost.setType(service.getType().getType());
                    oneCost.setMainTaskId(service.getType().getType() + service.getProgressName());
                    oneCost.setInputPrg(service.getNowStep() + "/" + service.getMaxStep());
                    oneCost.setImpStateInfo(service.getStepDescription());

                    oneData.getCostList().add(oneCost);
                }

            } else {
                res.setSuccess(false);
                res.setServiceDescription("未查询到对应的任务Id" + req.getRequest().getMainTaskId());

            }
        }
    }

    @Override
    protected String getQuerySql(DCP_CostExecuteQueryReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT * FROM DCP_COSTEXECUTE a");

        querySql.append(" WHERE a.EID='").append(req.geteId()).append("'");
        if (StringUtils.isNotEmpty(req.getRequest().getMainTaskId())) {
            querySql.append(" AND a.MAINTASKID='").append(req.getRequest().getMainTaskId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getType())) {
            querySql.append(" AND a.TYPE='").append(req.getRequest().getType()).append("'");
        }
        querySql.append(" AND ROWNUM = 1 ");//只取任务第一笔

        querySql.append(" ORDER BY ENDTIME DESC,STARTTIME DESC ");

        return querySql.toString();

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CostExecuteQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CostExecuteQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CostExecuteQueryReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CostExecuteQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CostExecuteQueryReq> getRequestType() {
        return new TypeToken<DCP_CostExecuteQueryReq>() {
        };
    }

    @Override
    protected DCP_CostExecuteQueryRes getResponseType() {
        return new DCP_CostExecuteQueryRes();
    }
}
