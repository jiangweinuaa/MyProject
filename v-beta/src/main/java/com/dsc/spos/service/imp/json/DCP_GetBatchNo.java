package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_GetBatchNoReq;
import com.dsc.spos.json.cust.res.DCP_GetBatchNoRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_GetBatchNo extends SPosBasicService<DCP_GetBatchNoReq, DCP_GetBatchNoRes> {
    @Override
    protected boolean isVerifyFail(DCP_GetBatchNoReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_GetBatchNoReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_GetBatchNoReq>(){};
    }

    @Override
    protected DCP_GetBatchNoRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_GetBatchNoRes();
    }

    @Override
    protected DCP_GetBatchNoRes processJson(DCP_GetBatchNoReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_GetBatchNoRes res = this.getResponse();

        String sql_goods = "select BATCHRULES,ISBATCH,SHELFLIFE from DCP_GOODS " +
                "WHERE EID='" + req.geteId() + "' AND PLUNO='" + req.getRequest().getPluNo() + "' ";
        List<Map<String, Object>> getGoods = dao.executeQuerySQL(sql_goods, null);
        if (getGoods != null && getGoods.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品表DCP_GOODS查不到记录！");
        }
        Map<String, Object> goodsInfo = getGoods.get(0);

        //4.判断商品批号生成规则，根据规则取当前序列号位数
        String batchRules = StringUtils.toString(goodsInfo.get("BATCHRULES"), "1");
        //批号生成规则: 1.生产日期+2位流水码 2.生产日期+3位流水码 3.生产日期+4位流水码 4.系统日期+4位流水码

        //5.获取正确日期
        String productDate = DateFormatUtils.getNowPlainDate();

        //6.取流水码长度
        int len = 0;
        switch (batchRules) {
                case "1":
                    len = 2;
                    break;
                case "2":
                    len = 3;
                    break;
                case "3":
                case "4":
                    len = 4;
                    break;
        }
        String batchno = PosPub.getCodeSertial(req.geteId(), "BATCH", productDate, len);

        res.setBatchNo(batchno);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_GetBatchNoReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();

        return sqlbuf.toString();
    }


}
