package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CurrencyAlterReq;
import com.dsc.spos.json.cust.res.DCP_CurrencyAlterRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DCP_CurrencyAlter extends SPosAdvanceService<DCP_CurrencyAlterReq, DCP_CurrencyAlterRes> {

    @Override
    protected void processDUID(DCP_CurrencyAlterReq req, DCP_CurrencyAlterRes res) throws Exception {
        // TODO Auto-generated method stub
        //String sql = null;
        try {
            //String eId = req.geteId();
            DCP_CurrencyAlterReq.levelRequest request = req.getRequest();
            String oprType = request.getOprType();//I insert U update

            if(oprType.equals("I")){
                processOnCreate(req,res);
            }else{
                processOnUpdate(req,res);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败");
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CurrencyAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CurrencyAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CurrencyAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CurrencyAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        DCP_CurrencyAlterReq.levelRequest request = req.getRequest();
        String oprType = request.getOprType();
        String nation = request.getNation();
        String currency = request.getCurrency();
        String minValue = request.getMinValue();
        String symbol = request.getSymbol();
        String priceDigit = request.getPriceDigit();
        String amountDigit = request.getAmountDigit();
        String costAmountDigit = request.getCostAmountDigit();
        String costPriceDigit = request.getCostPriceDigit();
        String status = request.getStatus();

        if (Check.Null(oprType)||!(oprType.equals("I")||oprType.equals("U")))
        {
            errMsg.append("操作类型不可为空值, ");
            isFail = true;
        }
        if (Check.Null(nation))
        {
            errMsg.append("国家地区不可为空值, ");
            isFail = true;
        }
        if(Check.Null(currency)){
            errMsg.append("币种编号不可为空值, ");
            isFail = true;
        }

        if(Check.Null(minValue)){
            minValue="1";//枚举值：1.分 2.角 3.元
            request.setMinValue("1");
            //errMsg.append("最小面额不可为空值, ");
            //isFail = true;
        }
        if(Check.Null(symbol)){
            errMsg.append("货币符号不可为空值, ");
            isFail = true;
        }
        if(Check.Null(priceDigit)){
            errMsg.append("单价小数位数不可为空值, ");
            isFail = true;
        }
        if(Check.Null(amountDigit)){
            errMsg.append("金额小数位数不可为空值, ");
            isFail = true;
        }
        if(Check.Null(costAmountDigit)){
            errMsg.append("成本单价小数位数不可为空值, ");
            isFail = true;
        }
        if(Check.Null(costPriceDigit)){
            errMsg.append("成本金额小数位数不可为空值, ");
            isFail = true;
        }
        if(Check.Null(status)){
            errMsg.append("状态码不可为空值, ");
            isFail = true;
        }

        if(minValue.equals("1")||minValue.equals("2")||minValue.equals("3")){

        }else{
            errMsg.append("最小面额必须为1.分 ,2.角,3.元 ");
            isFail=true;
        }

        String regex = "^-?\\d+(\\.0+)?$";
        Pattern pattern = Pattern.compile(regex);
        if(pattern.matcher(priceDigit).matches()){
            if(getValueFromString(priceDigit)>=0&&getValueFromString(priceDigit)<=10){

            }else{
                errMsg.append("单价小数位数必须大于等于0且小于等于10, ");
                isFail = true;
            }
        }else{
            errMsg.append("单价小数位数必须大于等于0且小于等于10, ");
            isFail = true;
        }

        if(pattern.matcher(amountDigit).matches()){
            if(getValueFromString(amountDigit)>=0&&getValueFromString(amountDigit)<=10){

            }else{
                errMsg.append("金额小数位数必须大于等于0且小于等于10, ");
                isFail = true;
            }
        }else{
            errMsg.append("金额小数位数必须大于等于0且小于等于10, ");
            isFail=true;
        }

        if(pattern.matcher(costPriceDigit).matches()){
            if(getValueFromString(costPriceDigit)>=0&&getValueFromString(costPriceDigit)<=10){

            }else{
                errMsg.append("成本单价小数位数必须大于等于0且小于等于10, ");
                isFail = true;
            }
        }else{
            errMsg.append("成本单价小数位数必须大于等于0且小于等于10, ");
        }

        if(pattern.matcher(costAmountDigit).matches()){
            if(getValueFromString(costAmountDigit)>=0&&getValueFromString(costAmountDigit)<=10){

            }else{
                errMsg.append("成本金额小数位数必须大于等于0且小于等于10, ");
                isFail = true;
            }
        }else{
            errMsg.append("成本金额小数位数必须大于等于0且小于等于10, ");
        }


        List<DCP_CurrencyAlterReq.level1Elm> currencyNameLangList = request.getCurrencyName_lang();

        if(currencyNameLangList==null||currencyNameLangList.isEmpty())
        {
            errMsg.append("多语言资料不能为空 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (DCP_CurrencyAlterReq.level1Elm oneData : currencyNameLangList)
        {
            String langType = oneData.getLangType();

            if(Check.Null(langType)){
                errMsg.append("多语言类型不能为空值 ");
                isFail = true;
            }
        }

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    public Integer getValueFromString(String value){
        double num=Double.parseDouble(value);
        int intValue = (int) num;
        return intValue;
    }

    @Override
    protected TypeToken<DCP_CurrencyAlterReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_CurrencyAlterReq>(){};
    }

    @Override
    protected DCP_CurrencyAlterRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_CurrencyAlterRes();
    }

    /**
     * 验证是否已存在
     * @param eId
     * @param nation
     * @param currency
     * @return
     */
    private String isRepeat(String eId, String nation,String currency){
        String sql = "select currency from DCP_CURRENCY where EID = '"+eId+"' and NATION = '"+nation+"' and CURRENCY='"+currency+"'";
        return sql;
    }

    /**
     * 验证多语言信息是否重复
     * @param nation
     * @param eId
     * @param langType
     * @return
     */
    private String isRepeatLang(String nation,String currency, String eId, String langType ){
        String sql = null;
        sql = String.format("SELECT * FROM DCP_CURRENCY_LANG WHERE "
                + " CURRENCY = '%s' "
                + " AND NATION = '%s' "
                + " and EID = '%s' "
                + " and lang_Type = '%s' " ,currency,nation,eId,langType);
        return sql;
    }

    private void processOnCreate(DCP_CurrencyAlterReq req, DCP_CurrencyAlterRes res) throws Exception{
        String sql="";
        String eId = req.geteId();
        DCP_CurrencyAlterReq.levelRequest request = req.getRequest();
        String nation = request.getNation();
        String currency = request.getCurrency();
        String minValue = request.getMinValue();
        String symbol = request.getSymbol();
        String priceDigit = request.getPriceDigit();
        String amountDigit = request.getAmountDigit();
        String costPriceDigit = request.getCostPriceDigit();
        String costAmountDigit = request.getCostAmountDigit();
        String status = request.getStatus();
        String employeeNo = req.getEmployeeNo();
        String departmentNo=req.getDepartmentNo();
        String departmentName = req.getDepartmentName();
        String employeeName = req.getEmployeeName();

        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        sql = this.isRepeat(eId, nation,currency);
        List<Map<String, Object>> currencyDatas = this.doQueryData(sql, null);
        if(currencyDatas.isEmpty()){
            String[] columns1 = { "EID","NATION", "CURRENCY", "MINVALUE", "SYMBOL","PRICEDIGIT", "AMOUNTDIGIT","COSTPRICEDIGIT","COSTAMOUNTDIGIT","STATUS","CREATEOPID","CREATEDEPTID","CREATETIME","LASTMODIOPID","LASTMODITIME"
            ,"CREATEOPNAME","CREATEDEPTNAME","LASTMODIOPNAME"};
            DataValue[] insValue1 = null;
            insValue1 = new DataValue[] {
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(nation, Types.VARCHAR),
                    new DataValue(currency, Types.VARCHAR),
                    new DataValue(minValue, Types.VARCHAR),
                    new DataValue(symbol, Types.VARCHAR),
                    new DataValue(priceDigit, Types.DOUBLE),
                    new DataValue(amountDigit, Types.DOUBLE),
                    new DataValue(costPriceDigit, Types.DOUBLE),
                    new DataValue(costAmountDigit, Types.DOUBLE),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(employeeNo, Types.VARCHAR),
                    new DataValue(departmentNo, Types.VARCHAR),
                    new DataValue(nowTime, Types.DATE),
                    new DataValue(employeeNo, Types.VARCHAR),
                    new DataValue(nowTime, Types.DATE),
                    new DataValue(employeeName, Types.VARCHAR),
                    new DataValue(departmentName, Types.VARCHAR),
                    new DataValue(employeeName, Types.VARCHAR)
            };

            InsBean ib1 = new InsBean("DCP_CURRENCY", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


            List<DCP_CurrencyAlterReq.level1Elm> getLangDatas = req.getRequest().getCurrencyName_lang();
            for(DCP_CurrencyAlterReq.level1Elm oneLv1: getLangDatas){

                String[] columnsName = {
                        "EID","NATION","CURRENCY","LANG_TYPE","NAME"
                };

                String langType = oneLv1.getLangType();
                String currencyName = oneLv1.getName();

                sql = this.isRepeatLang(nation,currency, eId, langType);
                List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);

                if(detailDatas.isEmpty()){

                    DataValue[] insValueDetail = new DataValue[]
                            {
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(nation, Types.VARCHAR),
                                    new DataValue(currency, Types.VARCHAR),
                                    new DataValue(langType, Types.VARCHAR),
                                    new DataValue(currencyName, Types.VARCHAR)
                            };
                    InsBean ib2 = new InsBean("DCP_CURRENCY_LANG", columnsName);
                    ib2.addValues(insValueDetail);
                    this.addProcessData(new DataProcessBean(ib2));

                }else{
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("服务执行失败: 国家地区为 "+nation+"币种编码为  "+currency+" , 多语言类型为 "+langType+" 的信息已存在");
                    return;
                }
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }else{
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 国家地区为 "+nation+"币种编码为  "+currency+"  的信息已存在");
            return;
        }

    }

    private void processOnUpdate(DCP_CurrencyAlterReq req, DCP_CurrencyAlterRes res) throws Exception{
        String sql="";
        String eId = req.geteId();
        DCP_CurrencyAlterReq.levelRequest request = req.getRequest();
        String nation = request.getNation();
        String currency = request.getCurrency();
        String minValue = request.getMinValue();
        String symbol = request.getSymbol();
        String priceDigit = request.getPriceDigit();
        String amountDigit = request.getAmountDigit();
        String costPriceDigit = request.getCostPriceDigit();
        String costAmountDigit = request.getCostAmountDigit();
        String status = request.getStatus();

        String employeeNo = req.getEmployeeNo();
        String employeeName = req.getEmployeeName();
        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        UptBean ub1 = null;
        ub1 = new UptBean("DCP_CURRENCY");

        ub1.addUpdateValue("MINVALUE", new DataValue(minValue, Types.VARCHAR));
        ub1.addUpdateValue("SYMBOL", new DataValue(symbol, Types.VARCHAR));
        ub1.addUpdateValue("PRICEDIGIT", new DataValue(priceDigit, Types.DOUBLE));
        ub1.addUpdateValue("AMOUNTDIGIT", new DataValue(amountDigit, Types.DOUBLE));
        ub1.addUpdateValue("COSTPRICEDIGIT", new DataValue(costPriceDigit, Types.DOUBLE));
        ub1.addUpdateValue("COSTAMOUNTDIGIT", new DataValue(costAmountDigit, Types.DOUBLE));
        ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(employeeNo, Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(nowTime, Types.DATE));
        ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(employeeName, Types.VARCHAR));
        // condition
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("NATION", new DataValue(nation, Types.VARCHAR));
        ub1.addCondition("CURRENCY", new DataValue(currency, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));


        DelBean db2 = new DelBean("DCP_CURRENCY_LANG");
        db2.addCondition("NATION", new DataValue(nation, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("CURRENCY", new DataValue(currency, Types.VARCHAR));

        this.addProcessData(new DataProcessBean(db2));

        List<DCP_CurrencyAlterReq.level1Elm> getLangDatas = req.getRequest().getCurrencyName_lang();
        for(DCP_CurrencyAlterReq.level1Elm oneLv1: getLangDatas){

            String[] columnsName = {
                    "EID","NATION","CURRENCY","LANG_TYPE","NAME"
            };

            String langType = oneLv1.getLangType();
            String currencyName = oneLv1.getName();
            DataValue[] insValueDetail = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(nation, Types.VARCHAR),
                            new DataValue(currency, Types.VARCHAR),
                            new DataValue(langType, Types.VARCHAR),
                            new DataValue(currencyName, Types.VARCHAR)
                    };
            InsBean ib2 = new InsBean("DCP_CURRENCY_LANG", columnsName);
            ib2.addValues(insValueDetail);
            this.addProcessData(new DataProcessBean(ib2));
        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

}
