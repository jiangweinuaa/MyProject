package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CreatePdaPricetagReq;
import com.dsc.spos.json.cust.res.DCP_CreatePdaPricetagRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @description: PDA标价签采集
 * @author: wangzyc
 * @create: 2021-11-18
 */
public class DCP_CreatePdaPricetag extends SPosAdvanceService<DCP_CreatePdaPricetagReq, DCP_CreatePdaPricetagRes> {
    @Override
    protected void processDUID(DCP_CreatePdaPricetagReq req, DCP_CreatePdaPricetagRes res) throws Exception {
        String eId = req.geteId();
        DCP_CreatePdaPricetagReq.level1Elm request = req.getRequest();
        String shopId = req.getShopId();
        String orgId = request.getOrgId();
        if(Check.Null(orgId)){
            orgId = shopId;
        }
        String pluNo = request.getPluNo();
        String barcode = request.getBarcode();
        String terminalId=Check.Null(request.getTerminalId())?"":request.getTerminalId();
        String createBy = req.getOpNO();
        String createByName = req.getOpName();
        if(Check.Null(createBy)){
            createBy = req.getApiUserCode();
            createByName = req.getApiUserCode();
        }

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = df.format(cal.getTime());

        try {
            // 生成单号
            String billNo = getGUID(req);

            // 查询下商品的详细信息
            String sql = getPlunoDeatils(req);
            List<Map<String, Object>> getPlunoDeatils = this.doQueryData(sql, null);
            if (!CollectionUtils.isEmpty(getPlunoDeatils)) {
                String[] columns = {"EID", "BILLNO", "SERIALNO ", "SHOPID", "PLUNO", "PLUNAME", "FEATURENO", "FEATURENAME", "BARCODE", "SUNIT", "SPEC",
                        "PRINTCOUNT", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME","TERMINALID"};
                int item = 0;
                for (Map<String, Object> getPlunoDeatil : getPlunoDeatils) {
                    item++;
                    String featureNo = getPlunoDeatil.get("FEATURENO").toString();
                    String featureName = getPlunoDeatil.get("FEATURENAME").toString();
                    String pluName = getPlunoDeatil.get("PLU_NAME").toString();
                    String unit = getPlunoDeatil.get("UNIT").toString();
                    String spec = getPlunoDeatil.get("SPEC").toString();

                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(billNo, Types.VARCHAR),
                            new DataValue(item, Types.VARCHAR),
                            new DataValue(orgId, Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(pluName, Types.VARCHAR),
                            new DataValue(featureNo, Types.VARCHAR),
                            new DataValue(featureName, Types.VARCHAR),
                            new DataValue(barcode, Types.VARCHAR),
                            new DataValue(unit, Types.VARCHAR),
                            new DataValue(spec, Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(createBy, Types.VARCHAR),
                            new DataValue(createByName, Types.VARCHAR),
                            new DataValue(createTime, Types.DATE),
                            new DataValue(createBy, Types.VARCHAR),
                            new DataValue(createByName, Types.VARCHAR),
                            new DataValue(createTime, Types.DATE),
                            new DataValue(terminalId, Types.VARCHAR)      
                    };
                    InsBean ib = new InsBean("DCP_SCAN_PRICETAG_GOODS", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));

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
    protected List<InsBean> prepareInsertData(DCP_CreatePdaPricetagReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CreatePdaPricetagReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CreatePdaPricetagReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CreatePdaPricetagReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_CreatePdaPricetagReq.level1Elm request = req.getRequest();

        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getPluNo())) {
            errMsg.append("品号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getBarcode())) {
            errMsg.append("条码不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getOrgType())) {
            errMsg.append("组织类型不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CreatePdaPricetagReq> getRequestType() {
        return new TypeToken<DCP_CreatePdaPricetagReq>() {
        };
    }

    @Override
    protected DCP_CreatePdaPricetagRes getResponseType() {
        return new DCP_CreatePdaPricetagRes();
    }

    /**
     * 查询商品的基础信息
     *
     * @param req
     * @return
     */
    protected String getPlunoDeatils(DCP_CreatePdaPricetagReq req) {
        String sql = "";
        DCP_CreatePdaPricetagReq.level1Elm request = req.getRequest();
        String langType = req.getLangType();
        if(Check.Null(langType)){
            langType = "zh_CN";
        }
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.PLUNO, a.FEATURENO, a.UNIT, c.PLU_NAME, b.SPEC , d.FEATURENAME " +
                " FROM DCP_GOODS_BARCODE a " +
                " LEFT JOIN DCP_GOODS_UNIT_LANG b ON a.EID = b.EID AND a.PLUNO = b.PLUNO AND a.UNIT = b.OUNIT AND b.LANG_TYPE = '" + langType + "' " +
                " LEFT JOIN DCP_GOODS_LANG c ON a.EID = c.EID AND a.PLUNO = c.PLUNO AND c.LANG_TYPE = '" + langType + "' " +
                " LEFT JOIN DCP_GOODS_FEATURE_LANG d ON a.EID = d.EID AND a.PLUNO = d.PLUNO AND a.FEATURENO = d.FEATURENO AND d.LANG_TYPE = '" + langType + "' " +
                " WHERE a.EID = '" + req.geteId() + "' AND a.PLUBARCODE = '" + request.getBarcode() + "' and  a.PLUNO  = '" + request.getPluNo() + "' and a.status = '100' ");
        if(!Check.Null(request.getFeatureNo())){
            sqlbuf.append(" AND a.FEATURENO  = '"+request.getFeatureNo()+"'");
        }
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 生成一个不重复的GUID
     *
     * @param req
     * @return
     * @throws Exception
     */
    private String getGUID(DCP_CreatePdaPricetagReq req) throws Exception {
        // 生成GUID
        String GUID = null;
        boolean existGUID = true;
        do {
            GUID = PosPub.getGUID(false);
            if (!checkExist(req, GUID)) {
                existGUID = false;
            }
        } while (existGUID);
        return GUID;
    }

    /**
     * 查看将要保存的数据是否存在
     *
     * @param req
     * @param GUID
     * @return
     * @throws Exception
     */
    private boolean checkExist(DCP_CreatePdaPricetagReq req, String GUID) throws Exception {

        String sql = null;
        boolean exist = false;
        String eId = req.geteId();

        sql = " select * from DCP_SCAN_PRICETAG_GOODS where EID='" + eId + "'  and BILLNO= '" + GUID + "'  ";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && getQData.isEmpty() == false) {
            exist = true;
        }
        return exist;
    }
}
