package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProcessTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_ProcessTemplateCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ProcessTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服務函數：STakeTemplateCreateDCP
 *    說明：盘点模板新增
 * 服务说明：盘点模板新增
 * @author panjing
 * @since  2016-09-20
 */
public class DCP_ProcessTemplateCreate extends SPosAdvanceService<DCP_ProcessTemplateCreateReq, DCP_ProcessTemplateCreateRes>
{

    @Override
    protected boolean isVerifyFail(DCP_ProcessTemplateCreateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        List<level1Elm> jsonDatas = req.getRequest().getDatas();
        String templateID = req.getRequest().getTemplateID();
        String templateName = req.getRequest().getTemplateName();
        String timeType = req.getRequest().getTimeType();
        String v_status = req.getRequest().getStatus();

        /** 必傳，門店編號，僅允許為單筆 */
        if (Check.Null(templateID)) {
            errCt++;
            errMsg.append("模板ID不可为空值, ");
            isFail = true;
        }

        if (Check.Null(templateName)) {
            errCt++;
            errMsg.append("模板名称不可为空值, ");
            isFail = true;
        }

        if (Check.Null(timeType)) {
            errCt++;
            errMsg.append("周期类型不可为空值, ");
            isFail = true;
        }

        if (Check.Null(v_status)) {
            errCt++;
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (level1Elm par : jsonDatas) {
            //必传值不为空

            String item = par.getItem();
            String pluNO = par.getPluNo();
            String punit = par.getPunit();
            String status = par.getStatus();

            if (Check.Null(item)) {
                errCt++;
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }

            if (Check.Null(pluNO)) {
                errCt++;
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }

            if (Check.Null(punit)) {
                errCt++;
                errMsg.append("单位不可为空值, ");
                isFail = true;
            }

            if (Check.Null(status)) {
                errCt++;
                errMsg.append("明细状态不可为空值, ");
                isFail = true;
            }

            if (isFail){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProcessTemplateCreateReq> getRequestType() {
        return new TypeToken<DCP_ProcessTemplateCreateReq>(){};
    }

    @Override
    protected DCP_ProcessTemplateCreateRes getResponseType() {
        return new DCP_ProcessTemplateCreateRes();
    }

    @Override
    protected void processDUID(DCP_ProcessTemplateCreateReq req,DCP_ProcessTemplateCreateRes res) throws Exception {
        String eId = req.geteId();
        String templateName = req.getRequest().getTemplateName();
        String timeType = req.getRequest().getTimeType();
        String timeValue = req.getRequest().getTimeValue();
        String status = req.getRequest().getStatus();
        String createBy = req.getOpNO();
        String templateID = req.getRequest().getTemplateID();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
        String createDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String createTime = df.format(cal.getTime());
        String templateNO ="";
        String shopType = req.getRequest().getShopType();
        //向下兼容，前端可能没更新，默认值2
        if(shopType==null||shopType.isEmpty())
        {
            shopType = "2";
        }
        //解析日期值使用 BY JZMA 20200407
        if (timeType.equals("3")&&timeValue.length()>1)
        {
            if (!timeValue.substring(0, 1).equals(";"))
            {
                timeValue = ";" + timeValue;
            }
            if (!timeValue.substring(timeValue.length()-1, timeValue.length()).equals(";"))
            {
                timeValue = timeValue+";" ;
            }
            int i =0 ;
            while (timeValue.contains(";;")) {
                timeValue=timeValue.replaceAll(";;", ";");
                if (i>=100) break;
                i++;
            }
        }

        try
        {
            if (checkGuid(req) == false){
                templateNO = gettemplateNO(req);
                String[] columns1 = {
                        "EID", "PTEMPLATENO","DOC_TYPE","PTEMPLATE_NAME","PTEMPLATE_ID", "TIME_TYPE",
                        "TIME_VALUE","TOT_CQTY", "PRE_DAY", "CREATEBY", "CREATE_DATE",
                        "CREATE_TIME", "STATUS","SHOPTYPE"
                };
                DataValue[] insValue = null;

                //新增單身 (多筆)
                List<level1Elm> jsonDatas = req.getRequest().getDatas();
                for (level1Elm par : jsonDatas) {
                    int insColCt = 0;
                    String[] columnsName = {
                            "EID", "PTEMPLATENO", "DOC_TYPE", "ITEM", "PLUNO",
                            "PUNIT", "MIN_QTY", "MAX_QTY", "MUL_QTY",
                            "STATUS","CATEGORYNO","SORTID"
                    };

                    DataValue[] columnsVal = new DataValue[columnsName.length];
                    for (int i = 0; i < columnsVal.length; i++) {
                        String keyVal = null;
                        switch (i) {
                            case 0:
                                keyVal = eId;
                                break;
                            case 1:
                                keyVal = templateNO;
                                break;
                            case 2:
                                keyVal = "2";
                                break;
                            case 3:
                                keyVal = par.getItem();
                                break;
                            case 4:
                                keyVal = par.getPluNo(); //pluNO
                                break;
                            case 5:
                                keyVal = par.getPunit();
                                break;
                            case 6:
                                keyVal = "0";
                                break;
                            case 7:
                                keyVal = "0";
                                break;
                            case 8:
                                keyVal = "0";
                                break;
                            case 9:
                                keyVal = par.getStatus();
                                break;
                            case 10:
                                keyVal = par.getCategory();
                                break;
                            case 11:
                                keyVal = par.getSortId();
                                break;
                            default:
                                break;
                        }

                        if (keyVal != null) {
                            insColCt++;
                            if (i == 3 ){
                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                            }else if (i == 6 || i == 7 || i == 8 ){
                                columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                            }else{
                                columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                            }
                        } else {
                            columnsVal[i] = null;
                        }
                    }

                    String[] columns  = new String[insColCt];
                    insValue = new DataValue[insColCt];
                    // 依照傳入參數組譯要insert的欄位與數值；
                    insColCt = 0;
                    for (int i = 0; i < columnsVal.length; i++){
                        if (columnsVal[i] != null){
                            columns[insColCt] = columnsName[i];
                            insValue[insColCt] = columnsVal[i];
                            insColCt ++;
                            if (insColCt >= insValue.length) break;
                        }
                    }
                    InsBean ib = new InsBean("DCP_PTEMPLATE_DETAIL", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }

                insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(templateNO, Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue(templateName, Types.VARCHAR),
                        new DataValue(templateID, Types.VARCHAR),
                        new DataValue(timeType, Types.VARCHAR),
                        new DataValue(timeValue, Types.VARCHAR),
                        new DataValue("0", Types.FLOAT),
                        new DataValue("0", Types.INTEGER),
                        new DataValue(createBy, Types.VARCHAR),
                        new DataValue(createDate, Types.VARCHAR),
                        new DataValue(createTime, Types.VARCHAR),
                        new DataValue(status, Types.VARCHAR),
                        new DataValue(shopType, Types.VARCHAR)
                };

                InsBean ib1 = new InsBean("DCP_PTEMPLATE", columns1);
                ib1.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                this.doExecuteDataToDB();
            }

            res.setTemplateNO(templateNO);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProcessTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProcessTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProcessTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_ProcessTemplateCreateReq req) throws Exception {
        String sql = null;
        return sql;
    }

    private String gettemplateNO(DCP_ProcessTemplateCreateReq req) throws Exception {

        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String templateNO = null;
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer("");
        String[] conditionValues = {eId}; // 查询盘点单号

        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd");
        templateNO = "SCMB" + matter.format(dt);

        sqlbuf.append("" + "select PTEMPLATENO  from ( " + "select max(PTEMPLATENO) as  PTEMPLATENO "
                              + "  from DCP_PTEMPLATE " + " where DOC_TYPE='2' and  EID = ? "
                              + " and PTEMPLATENO like '%%" + templateNO + "%%' "); // 假資料
        sqlbuf.append(" ) TBL ");
        sql = sqlbuf.toString();

        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
        if (getQData != null && getQData.isEmpty() == false) {

            templateNO = (String) getQData.get(0).get("PTEMPLATENO");

            if (templateNO != null && templateNO.length() > 0) {
                long i;
                templateNO = templateNO.substring(4, templateNO.length());
                i = Long.parseLong(templateNO) + 1;
                templateNO = i + "";
                templateNO = "SCMB" + templateNO;
            } else {
                templateNO = "SCMB" + matter.format(dt) + "00001";
            }
        } else {
            templateNO = "SCMB" + matter.format(dt) + "00001";
        }

        return templateNO;
    }

    private boolean checkGuid(DCP_ProcessTemplateCreateReq req) throws Exception
    {
        String sql = null;
        String guid = req.getRequest().getTemplateID();
        boolean existGuid;
        String[] conditionValues = { guid };
        sql = "select *  from DCP_PTEMPLATE  where PTEMPLATE_ID = ? ";

        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
        if (getQData != null && getQData.isEmpty() == false) {
            existGuid = true;
        } else {
            existGuid =  false;
        }
        return existGuid;
    }

}
