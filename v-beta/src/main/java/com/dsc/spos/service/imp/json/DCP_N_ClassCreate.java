package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_N_ClassCreateReq;
import com.dsc.spos.json.cust.req.DCP_N_ClassCreateReq.range;
import com.dsc.spos.json.cust.req.DCP_N_ClassCreateReq.className;
import com.dsc.spos.json.cust.req.DCP_N_ClassCreateReq.displayName;
import com.dsc.spos.json.cust.res.DCP_N_ClassCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_N_ClassCreate
 * 服务说明：N-销售分组创建
 * @author jinzma
 * @since  2024-04-17
 */
public class DCP_N_ClassCreate extends SPosAdvanceService<DCP_N_ClassCreateReq, DCP_N_ClassCreateRes> {
    @Override
    protected void processDUID(DCP_N_ClassCreateReq req, DCP_N_ClassCreateRes res) throws Exception {

        try {
            String eId = req.geteId();

            //清缓存
            String posUrl = PosPub.getPOS_INNER_URL(eId);
            String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                    " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
            List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
            String apiUserCode = "";
            String apiUserKey = "";
            if (result != null && result.size() == 2) {
                for (Map<String, Object> map : result) {
                    if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                        apiUserCode = map.get("ITEMVALUE").toString();
                    } else {
                        apiUserKey = map.get("ITEMVALUE").toString();
                    }
                }
            }
            PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey, eId);


            //必传字段
            String classType = req.getRequest().getClassType();
            String classNo = req.getRequest().getClassNo();
            String status = req.getRequest().getStatus();
            String remind = req.getRequest().getRemind();
            String label = req.getRequest().getLabel();
            String isPublic = req.getRequest().getIsPublic();  //分类性质：1共用分组2.指定分组

            List<className> className_lang = req.getRequest().getClassName_lang();
            List<displayName> displayName_lang = req.getRequest().getDisplayName_lang();
            List<range> rangeList = req.getRequest().getRangeList();
            String sorId = req.getRequest().getSortId();
            String beginDate = req.getRequest().getBeginDate();//yyyy-mm-dd
            if (beginDate != null) {
                beginDate = beginDate.replace("-", "");
            }
            String endDate = req.getRequest().getEndDate();//yyyy-mm-dd
            if (endDate != null) {
                endDate = endDate.replace("-", "");
            }
            int subClassCount = 0;

            //非必须
            String levelId = req.getRequest().getLevelId();
            String upClassNo = req.getRequest().getUpClassNo();
            String memo = req.getRequest().getMemo();
            String goodsSortType = req.getRequest().getGoodsSortType();
            String classImage = req.getRequest().getClassImage();
            String remindType = req.getRequest().getRemindType();  //提醒类型，0.必须 1.提醒，空值为不提醒
            String labelName = req.getRequest().getLabelName();
            String isShare = req.getRequest().getIsShare();

            if (Check.Null(remindType)){
                remindType = "-1";
            }

            // 是否可分享：0.否 1.是 默认否
            if (Check.Null(isShare)) {
                isShare = "0";
            }

            //String restrictCompany = req.getRequest().getRestrictCompany();
            String restrictShop = req.getRequest().getRestrictShop();
            if (restrictShop == null || restrictShop.isEmpty()) {
                restrictShop = "0";
            }
            String restrictChannel = req.getRequest().getRestrictChannel();
            if (restrictChannel == null || restrictChannel.isEmpty()) {
                restrictChannel = "0";
            }
            String restrictAppType = req.getRequest().getRestrictAppType();
            if (restrictAppType == null || restrictAppType.isEmpty()) {
                restrictAppType = "0";
            }
            String restrictPeriod = req.getRequest().getRestrictPeriod();
            if (restrictPeriod == null || restrictPeriod.isEmpty()) {
                restrictPeriod = "0";
            }

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


            String sql = " select * from DCP_CLASS where eid='"+eId+"' and CLASSTYPE='"+classType+"' and CLASSNO='"+classNo+"' ";
            List<Map<String, Object>> mapList = this.doQueryData(sql, null);
            if (mapList!=null && !mapList.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "该菜单类型：" + classType + " 菜单编码：" + classNo + "已存在！");
            }

            if (isPublic.equals("1")){
                restrictShop = "0";
                restrictChannel = "0";
                restrictAppType = "0";
                restrictPeriod = "0";
            }


            String[] columns_class = {"EID", "CLASSTYPE", "CLASSNO", "LEVELID", "UPCLASSNO", "SUBCLASSCOUNT", "SORTID",
                    "MEMO", "STATUS", "BEGINDATE", "ENDDATE", "GOODSSORTTYPE", "RESTRICTSHOP", "RESTRICTCHANNEL",
                    "RESTRICTAPPTYPE", "RESTRICTPERIOD", "CREATETIME", "REMIND", "REMINDTYPE", "LABEL", "LABELNAME", "ISSHARE","ISPUBLIC"
            };

            DataValue[] insValue_class = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(classType, Types.VARCHAR),
                            new DataValue(classNo, Types.VARCHAR),
                            new DataValue(levelId, Types.VARCHAR),
                            new DataValue(upClassNo, Types.VARCHAR),
                            new DataValue(subClassCount, Types.VARCHAR),
                            new DataValue(sorId, Types.VARCHAR),
                            new DataValue(memo, Types.VARCHAR),
                            new DataValue(status, Types.VARCHAR),
                            new DataValue(beginDate, Types.VARCHAR),
                            new DataValue(endDate, Types.VARCHAR),
                            new DataValue(goodsSortType, Types.VARCHAR),
                            new DataValue(restrictShop, Types.VARCHAR), //修改人、时间等信息
                            new DataValue(restrictChannel, Types.VARCHAR),
                            new DataValue(restrictAppType, Types.VARCHAR),
                            new DataValue(restrictPeriod, Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                            new DataValue(remind, Types.VARCHAR),
                            new DataValue(remindType, Types.VARCHAR),
                            new DataValue(label, Types.VARCHAR),
                            new DataValue(labelName, Types.VARCHAR),
                            new DataValue(isShare, Types.VARCHAR),
                            new DataValue(isPublic, Types.VARCHAR),

                    };

            InsBean ib_class = new InsBean("DCP_CLASS", columns_class);
            ib_class.addValues(insValue_class);
            this.addProcessData(new DataProcessBean(ib_class));

            String[] columns_class_lang =
                    {
                            "EID",
                            "CLASSTYPE",
                            "CLASSNO",
                            "LANG_TYPE",
                            "CLASSNAME",
                            "DISPLAYNAME",
                            "LASTMODITIME"

                    };

            for (className lang : className_lang) {
                String langType = lang.getLangType();
                String className = lang.getName();
                String displayName = "";

                if (displayName_lang != null) {
                    for (displayName item : displayName_lang) {
                        if (langType.equals(item.getLangType())) {
                            displayName = item.getName();
                            break;
                        }
                    }
                }

                DataValue[] insValue1;
                insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(classType, Types.VARCHAR),
                        new DataValue(classNo, Types.VARCHAR),
                        new DataValue(langType, Types.VARCHAR),
                        new DataValue(className, Types.VARCHAR),
                        new DataValue(displayName, Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };

                InsBean ib1 = new InsBean("DCP_CLASS_LANG", columns_class_lang);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));

            }

            String[] columns_class_range =
                    {
                            "EID",
                            "CLASSTYPE",
                            "CLASSNO",
                            "RANGETYPE",
                            "ID",
                            "NAME",
                            "LASTMODITIME"

                    };


            for (range par : rangeList) {
                DataValue[] insValue1 = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(classType, Types.VARCHAR),
                        new DataValue(classNo, Types.VARCHAR),
                        new DataValue(par.getRangeType(), Types.VARCHAR),
                        new DataValue(par.getId(), Types.VARCHAR),
                        new DataValue(par.getName(), Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE)
                };

                InsBean ib1 = new InsBean("DCP_CLASS_RANGE", columns_class_range);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));

            }

            if (!Check.Null(classImage)) {
                // 新增 分组图片
                String[] columns_class_image =
                        {
                                "EID",
                                "CLASSNO",
                                "CLASSIMAGE"
                        };
                DataValue[]  insValue_class_image = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(classNo, Types.VARCHAR),
                        new DataValue(classImage, Types.VARCHAR)
                };

                InsBean ib2 = new InsBean("DCP_CLASS_IMAGE", columns_class_image);
                ib2.addValues(insValue_class_image);
                this.addProcessData(new DataProcessBean(ib2));
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_N_ClassCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_N_ClassCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_N_ClassCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_N_ClassCreateReq req) throws Exception {

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(req.getRequest()==null) {
            errMsg.append("request不能为空 ");
            isFail = true;
        }else {
            if (Check.Null(req.getRequest().getClassType())) {
                errMsg.append("类型不能为空值 ，");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getClassNo())) {
                errMsg.append("编码不能为空值 ，");
                isFail = true;
            }
            if (Check.Null(req.getRequest().getIsPublic())) {
                errMsg.append("分类性质不能为空值 ，");
                isFail = true;
            }

            List<range> rangeList = req.getRequest().getRangeList();
            List<className> className_lang = req.getRequest().getClassName_lang();

            if (className_lang == null) {
                errMsg.append("分类名称多语言不能为空 ");
                isFail = true;
            }

            if ("2".equals(req.getRequest().getIsPublic())) {

                if (Check.Null(req.getRequest().getRestrictShop())) {
                    errMsg.append("适用门店不能为空值 ，");
                    isFail = true;
                }
                if (Check.Null(req.getRequest().getRestrictChannel())) {
                    errMsg.append("适用渠道不能为空值 ，");
                    isFail = true;
                }
                if (Check.Null(req.getRequest().getRestrictPeriod())) {
                    errMsg.append("适用时段不能为空值 ，");
                    isFail = true;
                }

                if (isFail) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }

                boolean bCompany = false;
                boolean bShop = false;
                boolean bChannel = false;
                boolean bPeriod = false;


                if (rangeList == null || rangeList.isEmpty()) {
                    if ("0".equals(req.getRequest().getRestrictShop())) {
                        bShop = true;
                    }
                    if ("0".equals(req.getRequest().getRestrictChannel())) {
                        bChannel = true;
                    }
                    if ("0".equals(req.getRequest().getRestrictPeriod())) {
                        bPeriod = true;
                    }
                }else {
                    for (range range : rangeList) {
                        //适用范围：1-公司2-门店3-渠道4-应用 5-时段
                        if (Check.Null(range.getRangeType())) {
                            errMsg.append("适用范围列表-类型rangeType不能为空值 ，");
                            isFail = true;
                        }else {
                            if (range.getRangeType().equals("1")) {
                                bCompany = true;
                            }
                            if (range.getRangeType().equals("2")) {
                                bShop = true;
                            }
                            if (range.getRangeType().equals("3")) {
                                bChannel = true;
                            }
                            if (range.getRangeType().equals("5")) {
                                bPeriod = true;
                            }

                            if (Check.Null(range.getId())){
                                errMsg.append("id不能为空值 ，");
                                isFail = true;
                            }
                            if (Check.Null(range.getName())){
                                errMsg.append("name不能为空值 ，");
                                isFail = true;
                            }
                        }

                        if (isFail) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                        }

                    }
                }

                if (!bCompany) {
                    errMsg.append("未设置适用公司 ，");
                    isFail = true;
                }
                if (!bShop) {
                    errMsg.append("未设置适用门店 ，");
                    isFail = true;
                }
                if (!bChannel) {
                    errMsg.append("未设置适用渠道 ，");
                    isFail = true;
                }
                if (!bPeriod) {
                    errMsg.append("未设置适用时段 ，");
                    isFail = true;
                }

            }

        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;


    }

    @Override
    protected TypeToken<DCP_N_ClassCreateReq> getRequestType() {
        return new TypeToken<DCP_N_ClassCreateReq>(){};
    }

    @Override
    protected DCP_N_ClassCreateRes getResponseType() {
        return new DCP_N_ClassCreateRes();
    }


}
