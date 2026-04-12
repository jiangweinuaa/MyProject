package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WoProdRepStatDeleteReq;
import com.dsc.spos.json.cust.res.DCP_WoProdRepStatDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_WoProdRepStatDelete extends SPosAdvanceService<DCP_WoProdRepStatDeleteReq, DCP_WoProdRepStatDeleteRes> {
    @Override
    protected void processDUID(DCP_WoProdRepStatDeleteReq req, DCP_WoProdRepStatDeleteRes res) throws Exception {

        List<Map<String, Object>> getData = this.doQueryData(String.format("SELECT * FROM dcp_woprodrepstat WHERE eid='%s' and corp='%s' and porderNo='%s'",
                req.geteId(),
                req.getRequest().getCorp(),
                req.getRequest().getPorderNo()
        ), null);

        if (getData == null || getData.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到需要删除的数据！");
        } else {
            if (!"-1".equals(getData.get(0).get("STATUS").toString())) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态不为-1未审核,不可删除!");
            }
        }

        String wDate = DateFormatUtils.getPlainDate(getData.get(0).get("WDATE").toString());
        String costCenterNo = getData.get(0).get("COSTCENTERNO").toString();
        String year = wDate.substring(0, 4);
        String period = wDate.substring(4, 6);

        String querySql = " SELECT * FROM DCP_HREXPSTAT WHERE EID='" + req.geteId() + "' " +
                " AND CORP='" + req.getRequest().getCorp() + "'" +
                " AND COSTCENTERNO='" + costCenterNo + "'" +
                " AND YEAR=" + year +
                " AND PERIOD=" + period;
        getData = doQueryData(querySql, null);
        if (CollectionUtils.isNotEmpty(getData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据已经完成工时费用计算不可删除!");
        }


        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        condition.add("PORDERNO", DataValues.newString(req.getRequest().getPorderNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("dcp_woprodrepstat", condition)));
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_WoProdRepStatDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WoProdRepStatDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WoProdRepStatDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_WoProdRepStatDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_WoProdRepStatDeleteReq> getRequestType() {
        return new TypeToken<DCP_WoProdRepStatDeleteReq>() {
        };
    }

    @Override
    protected DCP_WoProdRepStatDeleteRes getResponseType() {
        return new DCP_WoProdRepStatDeleteRes();
    }
}
