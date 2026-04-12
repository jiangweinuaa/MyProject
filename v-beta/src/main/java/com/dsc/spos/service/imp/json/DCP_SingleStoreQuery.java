package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SingleStoreQueryReq;
import com.dsc.spos.json.cust.res.DCP_SingleStoreQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_SingleStoreQuery extends SPosAdvanceService<DCP_SingleStoreQueryReq, DCP_SingleStoreQueryRes> {

    @Override
    protected void processDUID(DCP_SingleStoreQueryReq req, DCP_SingleStoreQueryRes res) throws Exception {
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        List<DCP_SingleStoreQueryRes.Shop> shopList = new ArrayList<>();
        for (Map<String, Object> oneData1 : getQData)
        {
            DCP_SingleStoreQueryRes.Shop shop = new DCP_SingleStoreQueryRes.Shop();
            shop.setShopId(oneData1.get("ORGANIZATIONNO").toString());
            shop.setShopName(oneData1.get("ORG_NAME").toString());
            shop.setEnableSingleStore(oneData1.get("ENABLESINGLESTORE").toString());
            shopList.add(shop);
        }
        DCP_SingleStoreQueryRes.level1Elm datas = res.new level1Elm();
        datas.setShopList(shopList);
        res.setDatas(datas);
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected String getQuerySql(DCP_SingleStoreQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT do.ORGANIZATIONNO, dol.ORG_NAME, NVL(ds.ENABLESINGLESTORE, 'N') ENABLESINGLESTORE FROM DCP_ORG do inner JOIN DCP_ORG_LANG dol ON do.EID = dol.EID AND do.ORGANIZATIONNO = dol.ORGANIZATIONNO AND dol.LANG_TYPE = '"+req.getLangType()+"' LEFT JOIN DCP_SINGLESTORE ds ON do.EID = ds.EID AND do.ORGANIZATIONNO = ds.SHOPID WHERE do.EID='"+req.geteId()+"' ");
        if (!Check.Null(req.getRequest().getEnableStatus()))
        {
            sqlbuf.append( " AND NVL(ds.ENABLESINGLESTORE, 'N') = '"+req.getRequest().getEnableStatus()+"' ");
        }
        if (CollectionUtil.isNotEmpty(req.getRequest().getShopList()))
        {
            String shopIds = req.getRequest().getShopList().stream().map(c -> c.getShopId()).collect(Collectors.joining("','"));
            sqlbuf.append( " and do.ORGANIZATIONNO in ('"+shopIds+"') ");
        }
        return sqlbuf.toString();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SingleStoreQueryReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SingleStoreQueryReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SingleStoreQueryReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SingleStoreQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_SingleStoreQueryReq> getRequestType() {
        return new TypeToken<DCP_SingleStoreQueryReq>(){};
    }

    @Override
    protected DCP_SingleStoreQueryRes getResponseType() {
        return new DCP_SingleStoreQueryRes();
    }
}
