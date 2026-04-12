package com.dsc.spos.progress.imp;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.req.DCP_invClosingDateProcessReq;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import org.apache.commons.collections4.CollectionUtils;

public class InvClosingDateProcess extends ProgressService<DCP_invClosingDateProcessReq> {

    public InvClosingDateProcess(DCP_invClosingDateProcessReq req) {
        super(req);
        setType(ProgressType.ProgressType_A);
        setMaxStep(3);
    }

    @Override
    public void runProgress() throws Exception {
        DsmDAO dao = StaticInfo.dao;
        DCP_invClosingDateProcessReq req = getReq();

        String eid = req.geteId();
        String corp = req.getRequest().getCorp();
        String accountId = req.getRequest().getAccountID();
        String account = req.getRequest().getAccount();

//        incStep();
//        setStepDescription("正在查询帐套信息");
//        List<Map<String, Object>> qData = dao.executeQuerySQL(getQueryAccountSetting(req), null);
//
//        String invcurrentyear = qData.get(0).get("INVCURRENTYEAR").toString();
//        String invcurrentperiod = qData.get(0).get("INVCURRENTPERIOD").toString();

        incStep();
        setStepDescription("正在写入帐套信息");

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(eid));
        condition.add("ACCOUNTID", DataValues.newString(accountId));
        condition.add("ACCOUNT", DataValues.newString(account));
        condition.add("CORP", DataValues.newString(corp));

        String nowDate = DateFormatUtils.getNowDate();
        String closingDate = DateFormatUtils.getDate(req.getRequest().getInvClosingDate());
        String nextPeriod = DateFormatUtils.getPlainDate(DateFormatUtils.addMonth(closingDate, 1));

        ColumnDataValue dcp_acount_setting = new ColumnDataValue();
        dcp_acount_setting.add("INVCLOSINGDATE", DataValues.newDate(closingDate));
        dcp_acount_setting.add("INVCURRENTYEAR", DataValues.newString(nextPeriod.substring(0, 4)));
        dcp_acount_setting.add("INVCURRENTPERIOD", DataValues.newString(nextPeriod.substring(4, 6)));

        addProcessBean(new DataProcessBean(DataBeans.getUptBean("DCP_ACOUNT_SETTING", condition, dcp_acount_setting)));

        incStep();
        setStepDescription("正在进行数据持久化");
        if (CollectionUtils.isNotEmpty(this.getPData())) {
            dao.useTransactionProcessData(this.getPData());
        }

        incStep();
        setStepDescription("执行成功！");

    }

    @Override
    public void beforeRun() {

    }

    @Override
    public void afterRun() {

    }

    private String getQueryAccountSetting(DCP_invClosingDateProcessReq req) {

        StringBuilder sb = new StringBuilder();

        String eid = req.geteId();
        String corp = req.getRequest().getCorp();
        String accountId = req.getRequest().getAccountID();
        String account = req.getRequest().getAccount();

        sb.append(" SELECT a.* FROM  DCP_ACOUNT_SETTING a");

        sb.append(" WHERE a.EID = '").append(eid).append("'")
                .append(" AND a.CORP = '").append(corp).append("'")
                .append(" AND a.ACCOUNTID = '").append(accountId).append("'")
                .append(" AND a.ACCOUNT = '").append(account).append("'");

        return sb.toString();
    }


}
