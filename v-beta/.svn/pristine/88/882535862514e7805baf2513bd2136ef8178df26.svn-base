package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TouchMenuUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TouchMenuUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_TouchMenuUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_TouchMenuUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_TouchMenuUpdate extends SPosAdvanceService<DCP_TouchMenuUpdateReq, DCP_TouchMenuUpdateRes> {

    @Override
    protected void processDUID(DCP_TouchMenuUpdateReq req, DCP_TouchMenuUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        StringBuffer errMsg = new StringBuffer("");
        String eId = req.geteId();
        level1Elm request = req.getRequest();
        String oprType = request.getOprType();  // 操作类型：1-创建 2-修改

        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 创建时间格式化

        String opNO = req.getOpNO();
        String opName = req.getOpName();
        String lastmodiTime = dfs.format(new Date());

        try {
            String menuNo = request.getMenuNo();
            List<level2Elm> menuName_lang = request.getMenuName_lang();
            String memo = request.getMemo();
            String priority = request.getPriority();
            String status = request.getStatus();
            String beginDate = request.getBeginDate();
            String endDate = request.getEndDate();
            String restrictShop = request.getRestrictShop();
            String restrictChannel = request.getRestrictChannel();
            String restrictPeriod = request.getRestrictPeriod();
            List<DCP_TouchMenuUpdateReq.level3Elm> rangeList = request.getRangeList();

            String[] columns_lang = {
                    "EID", "MENUNO", "LANG_TYPE", "MENUNAME", "LASTMODITIME"};
            String[] columns_range = {
                    "EID", "MENUNO", "RANGETYPE", "ID", "NAME", "LASTMODITIME"};

            String sql = null;
            sql = this.getQuerySql(req);
            String[] conditionValues = {eId, menuNo}; //查詢條件
            List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

            if (oprType.equals("1")) {
                // 创建
                if (CollectionUtils.isEmpty(getQData)) {
                    String[] columns1 = {
                            "EID", "MENUNO", "PRIORITY", "MEMO", "STATUS", "BEGINDATE",
                            "ENDDATE", "RESTRICTSHOP", "RESTRICTCHANNEL", "RESTRICTPERIOD", "CREATEOPID", "CREATEOPNAME", "CREATETIME", "LASTMODIOPID",
                            "LASTMODIOPNAME", "LASTMODITIME"};

                    DataValue[] insValue = null;

                    for (level2Elm level2Elm : menuName_lang) {
                        insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(menuNo, Types.VARCHAR),
                                new DataValue(level2Elm.getLangType(), Types.VARCHAR),
                                new DataValue(level2Elm.getName(), Types.VARCHAR),
                                new DataValue(lastmodiTime, Types.DATE)
                        };
                        InsBean ib1 = new InsBean("DCP_TOUCHMENU_LANG", columns_lang);
                        ib1.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib1));
                    }

                    for (DCP_TouchMenuUpdateReq.level3Elm level3Elm : rangeList) {
                        insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(menuNo, Types.VARCHAR),
                                new DataValue(level3Elm.getRangeType(), Types.VARCHAR),
                                new DataValue(level3Elm.getId(), Types.VARCHAR),
                                new DataValue(level3Elm.getName(), Types.VARCHAR),
                                new DataValue(lastmodiTime, Types.DATE)
                        };
                        InsBean ib2 = new InsBean("DCP_TOUCHMENU_RANGE", columns_range);
                        ib2.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib2));
                    }

                    insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(menuNo, Types.VARCHAR),
                            new DataValue(priority, Types.VARCHAR),
                            new DataValue(memo, Types.VARCHAR),
                            new DataValue(status, Types.VARCHAR),
                            new DataValue(beginDate, Types.VARCHAR),
                            new DataValue(endDate, Types.VARCHAR),
                            new DataValue(restrictShop, Types.VARCHAR),
                            new DataValue(restrictChannel, Types.VARCHAR),
                            new DataValue(restrictPeriod, Types.VARCHAR),
                            new DataValue(opNO, Types.VARCHAR),
                            new DataValue(opName, Types.VARCHAR),
                            new DataValue(lastmodiTime, Types.DATE),
                            new DataValue(opNO, Types.VARCHAR),
                            new DataValue(opName, Types.VARCHAR),
                            new DataValue(lastmodiTime, Types.DATE)
                    };
                    InsBean ib3 = new InsBean("DCP_TOUCHMENU", columns1);
                    ib3.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib3));
                }else{
                    errMsg.append("菜单编号已存在，请重新输入！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }

            } else if (oprType.equals("2")) {
                // 修改
                if (getQData != null && getQData.isEmpty() == false) {
                    //删除单身
                    DelBean db1 = new DelBean("DCP_TOUCHMENU_RANGE");
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("MENUNO", new DataValue(menuNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));
                    //删除单身
                    DelBean db2 = new DelBean("DCP_TOUCHMENU_LANG");
                    db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db2.addCondition("MENUNO", new DataValue(menuNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db2));

                    DataValue[] insValue = null;

                    for (level2Elm level2Elm : menuName_lang) {
                        insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(menuNo, Types.VARCHAR),
                                new DataValue(level2Elm.getLangType(), Types.VARCHAR),
                                new DataValue(level2Elm.getName(), Types.VARCHAR),
                                new DataValue(lastmodiTime, Types.DATE)
                        };
                        InsBean ib1 = new InsBean("DCP_TOUCHMENU_LANG", columns_lang);
                        ib1.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib1));
                    }

                    for (DCP_TouchMenuUpdateReq.level3Elm level3Elm : rangeList) {
                        insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(menuNo, Types.VARCHAR),
                                new DataValue(level3Elm.getRangeType(), Types.VARCHAR),
                                new DataValue(level3Elm.getId(), Types.VARCHAR),
                                new DataValue(level3Elm.getName(), Types.VARCHAR),
                                new DataValue(lastmodiTime, Types.DATE)
                        };
                        InsBean ib2 = new InsBean("DCP_TOUCHMENU_RANGE", columns_range);
                        ib2.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib2));
                    }

                    //更新单头
                    UptBean ub1 = null;
                    ub1 = new UptBean("DCP_TOUCHMENU");
                    ub1.addUpdateValue("MENUNO", new DataValue(menuNo, Types.VARCHAR));
                    ub1.addUpdateValue("PRIORITY", new DataValue(priority, Types.VARCHAR));
                    ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
                    ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                    ub1.addUpdateValue("BEGINDATE", new DataValue(beginDate, Types.VARCHAR));
                    ub1.addUpdateValue("ENDDATE", new DataValue(endDate, Types.VARCHAR));
                    ub1.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
                    ub1.addUpdateValue("RESTRICTCHANNEL", new DataValue(restrictChannel, Types.VARCHAR));
                    ub1.addUpdateValue("RESTRICTPERIOD", new DataValue(restrictPeriod, Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmodiTime, Types.DATE));

                    // condition
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("MENUNO", new DataValue(menuNo, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));


                } else {
                    errMsg.append("菜单编号不存在，请重新输入！");
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }

            }
            
            //门店触屏菜单添加复制的功能
            String copyGoodsMenuNo = request.getCopyGoodsMenuNo();
            if(StringUtils.isNotBlank(copyGoodsMenuNo)) {
            	copyGoodsMenu(copyGoodsMenuNo, menuNo, eId, lastmodiTime);
            }

            this.doExecuteDataToDB();

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }
    
    public void copyGoodsMenu(String copyGoodsMenuNo, String menuNo, String eId,  String lastmodiTime) throws Exception {
    	DataValue[] insValue = null;
    	String[] conditionValues = {eId, copyGoodsMenuNo}; //查詢條件
    	//复制门店触屏菜单-分类
    	String classSlq = "select ITEM, CLASSIMAGE, GOODSSORTTYPE, REMINDTYPE,LABELNAME, STATUS from DCP_TOUCHMENU_CLASS where EID= ? and MENUNO = ?";
        List<Map<String, Object>> getClassData = this.doQueryData(classSlq, conditionValues);
        String[] columns_class = {"EID", "MENUNO", "ITEM", "CLASSIMAGE", "GOODSSORTTYPE", "REMINDTYPE", "LABELNAME", "STATUS", "LASTMODITIME"};
        for (Map<String, Object> map : getClassData) {
        	insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(menuNo, Types.VARCHAR),
                    new DataValue(map.get("ITEM"), Types.VARCHAR),
                    new DataValue(map.get("CLASSIMAGE"), Types.VARCHAR),
                    new DataValue(map.get("GOODSSORTTYPE"), Types.VARCHAR),
                    new DataValue(map.get("REMINDTYPE"), Types.VARCHAR),
                    new DataValue(map.get("LABELNAME"), Types.VARCHAR),
                    new DataValue(map.get("STATUS"), Types.VARCHAR),
                    new DataValue(lastmodiTime, Types.DATE)
            };
            InsBean classIb = new InsBean("DCP_TOUCHMENU_CLASS", columns_class);
            classIb.addValues(insValue);
            this.addProcessData(new DataProcessBean(classIb));
		}
    	
    	
    	//复制门店触屏菜单-分类商品
        String classGoodsSlq = "select ITEM, SUBITEM, PLUTYPE, PLUNO, UNIT, PRICE, REMINDTYPE from DCP_TOUCHMENU_CLASS_GOODS where EID= ? and MENUNO = ?";
        List<Map<String, Object>> getClassGoodsData = this.doQueryData(classGoodsSlq, conditionValues);
        String[] columns_classGoods = {"EID", "MENUNO", "ITEM", "SUBITEM", "PLUTYPE", "PLUNO", "UNIT", "PRICE", "LASTMODITIME", "REMINDTYPE"};
        for (Map<String, Object> map : getClassGoodsData) {
        	insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(menuNo, Types.VARCHAR),
                    new DataValue(map.get("ITEM"), Types.VARCHAR),
                    new DataValue(map.get("SUBITEM"), Types.VARCHAR),
                    new DataValue(map.get("PLUTYPE"), Types.VARCHAR),
                    new DataValue(map.get("PLUNO"), Types.VARCHAR),
                    new DataValue(map.get("UNIT"), Types.VARCHAR),
                    new DataValue(map.get("PRICE"), Types.VARCHAR),
                    new DataValue(lastmodiTime, Types.DATE),
                    new DataValue(map.get("REMINDTYPE"), Types.VARCHAR)
            };
            InsBean classGoodsIb = new InsBean("DCP_TOUCHMENU_CLASS_GOODS", columns_classGoods);
            classGoodsIb.addValues(insValue);
            this.addProcessData(new DataProcessBean(classGoodsIb));
		}
    	
    	//复制门店触屏菜单-分类商品显示名称
        String classGoodsLangSlq = "select ITEM, SUBITEM, LANG_TYPE, DISPNAME from DCP_TOUCHMENU_CLASS_GOODS_LANG where EID= ? and MENUNO = ?";
        List<Map<String, Object>> getClassGoodsLangData = this.doQueryData(classGoodsLangSlq, conditionValues);
        String[] columns_classGoodsLang = {"EID", "MENUNO", "ITEM", "SUBITEM", "LANG_TYPE", "DISPNAME", "LASTMODITIME"};
        for (Map<String, Object> map : getClassGoodsLangData) {
        	insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(menuNo, Types.VARCHAR),
                    new DataValue(map.get("ITEM"), Types.VARCHAR),
                    new DataValue(map.get("SUBITEM"), Types.VARCHAR),
                    new DataValue(map.get("LANG_TYPE"), Types.VARCHAR),
                    new DataValue(map.get("DISPNAME"), Types.VARCHAR),
                    new DataValue(lastmodiTime, Types.DATE)
            };
            InsBean classGoodsLangIb = new InsBean("DCP_TOUCHMENU_CLASS_GOODS_LANG", columns_classGoodsLang);
            classGoodsLangIb.addValues(insValue);
            this.addProcessData(new DataProcessBean(classGoodsLangIb));
		}
    	
    	//复制门店触屏菜单-分类名称
        String classLangSlq = "select ITEM, LANG_TYPE, CLASSNAME from DCP_TOUCHMENU_CLASS_LANG where EID= ? and MENUNO = ?";
        List<Map<String, Object>> getClassLangData = this.doQueryData(classLangSlq, conditionValues);
        String[] columns_classLang = {"EID", "MENUNO", "ITEM", "LANG_TYPE", "CLASSNAME", "LASTMODITIME"};
        for (Map<String, Object> map : getClassLangData) {
        	insValue = new DataValue[]{
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(menuNo, Types.VARCHAR),
                    new DataValue(map.get("ITEM"), Types.VARCHAR),
                    new DataValue(map.get("LANG_TYPE"), Types.VARCHAR),
                    new DataValue(map.get("CLASSNAME"), Types.VARCHAR),
                    new DataValue(lastmodiTime, Types.DATE)
            };
            InsBean classLangIb = new InsBean("DCP_TOUCHMENU_CLASS_LANG", columns_classLang);
            classLangIb.addValues(insValue);
            this.addProcessData(new DataProcessBean(classLangIb));
		}
    	
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TouchMenuUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TouchMenuUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TouchMenuUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TouchMenuUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根

        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        level1Elm request = req.getRequest();
        String menuNo = request.getMenuNo();
        String oprType = request.getOprType();
        List<level2Elm> menuName_lang = request.getMenuName_lang();
        String priority = request.getPriority();
        String status = request.getStatus();
        List<DCP_TouchMenuUpdateReq.level3Elm> rangeList = request.getRangeList();
        String restrictShop = request.getRestrictShop();
        String restrictChannel = request.getRestrictChannel();
        String restrictPeriod = request.getRestrictPeriod();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();

        if (Check.Null(oprType)) {
            errMsg.append("操作类型不可为空值,");
            isFail = true;
        }

        if (Check.Null(menuNo)) {
            errMsg.append("菜单编码不可为空值,");
            isFail = true;
        }

        if (CollectionUtils.isEmpty(menuName_lang)) {
            errMsg.append("菜单名称多语言不可为空值,");
            isFail = true;
        }

        if (Check.Null(menuNo)) {
            errMsg.append("菜单编码不可为空值,");
            isFail = true;
        }

        if (Check.Null(priority)) {
            errMsg.append("优先级不可为空值,");
            isFail = true;
        }

        if (Check.Null(status)) {
            errMsg.append("状态不可为空值,");
            isFail = true;
        }

        if (Check.Null(beginDate)) {
            errMsg.append("生效日期不可为空值,");
            isFail = true;
        }

        if (Check.Null(endDate)) {
            errMsg.append("失效日期不可为空值,");
            isFail = true;
        }

        if (Check.Null(restrictShop)) {
            errMsg.append("适用门店不可为空值,");
            isFail = true;
        }

        if (Check.Null(restrictChannel)) {
            errMsg.append("适用渠道不可为空值,");
            isFail = true;
        }

        if (Check.Null(restrictPeriod)) {
            errMsg.append("适用时段不可为空值,");
            isFail = true;
        }

        String applyCompany = "0";
        String applyShop = "0";
        String applyChannel = "0";
        String applyPeriod = "0";
        for (DCP_TouchMenuUpdateReq.level3Elm level3Elm : rangeList) {
            switch (level3Elm.getRangeType()) {
                case "1":
                    // 适用公司
                    applyCompany = "1";
                    break;
                case "2":
                    // 适用门店
                    applyShop = "1";
                    break;
                case "3":
                    // 适用渠道
                    applyChannel = "1";
                    break;
                case "5":
                    // 适用时段
                    applyPeriod = "1";
                    break;
            }
            if (Check.Null(level3Elm.getName())) {
                errMsg.append("名称不可为空值,");
                isFail = true;
            }

            if (Check.Null(level3Elm.getId())) {
                errMsg.append("编码不可为空值,");
                isFail = true;
            }
        }

        if (applyCompany.equals("0")) {
            errMsg.append("请设置适用公司。,");
            isFail = true;
        }

        if (restrictShop.equals("1") || restrictShop.equals("2")) {
            if (applyShop.equals("0")) {
                errMsg.append("请设置适用门店或排除门店。,");
                isFail = true;
            }
        }

        if (restrictChannel.equals("1") || restrictChannel.equals("2")) {
            if (applyChannel.equals("0")) {
                errMsg.append("请设置适用渠道或排除渠道。,");
                isFail = true;
            }
        }

        if (restrictPeriod.equals("1") && applyPeriod.equals("0")) {
            errMsg.append("请设置适用时段。,");
            isFail = true;
        }


        if (isFail) {
            errMsg.deleteCharAt(errMsg.length() - 1);
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_TouchMenuUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_TouchMenuUpdateReq>() {
        };
    }

    @Override
    protected DCP_TouchMenuUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_TouchMenuUpdateRes();
    }

    @Override
    protected String getQuerySql(DCP_TouchMenuUpdateReq req) throws Exception {
        String sql = null;
        sql = " select *  from DCP_TOUCHMENU  where EID= ? and MENUNO = ?  ";
        return sql;
    }


}
