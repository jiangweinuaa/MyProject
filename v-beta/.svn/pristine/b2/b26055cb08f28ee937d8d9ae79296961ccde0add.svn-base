package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TouchMenuClassGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TouchMenuClassGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: 门店触屏菜单商品设置修改
 * @author: wangzyc
 * @create: 2021-06-16
 */
public class DCP_TouchMenuClassGoodsUpdate extends SPosAdvanceService<DCP_TouchMenuClassGoodsUpdateReq, DCP_TouchMenuClassGoodsUpdateRes> {
    @Override
    protected void processDUID(DCP_TouchMenuClassGoodsUpdateReq req, DCP_TouchMenuClassGoodsUpdateRes res) throws Exception {
        String eId = req.geteId();
        DCP_TouchMenuClassGoodsUpdateReq.level1Elm request = req.getRequest();
        String[] columns = {
                "EID", "MENUNO", "ITEM", "CLASSIMAGE", "GOODSSORTTYPE","REMINDTYPE","LABELNAME","STATUS","LASTMODITIME"};
        String[] columns_lang = {
                "EID", "MENUNO", "ITEM", "LANG_TYPE", "CLASSNAME","LASTMODITIME"};
        String[] columns_goods = {
                "EID", "MENUNO", "ITEM", "SUBITEM", "PLUTYPE",
                "PLUNO","UNIT","PRICE","LASTMODITIME","REMINDTYPE"};
        String[] columns_goods_lang = {
                "EID", "MENUNO", "ITEM", "SUBITEM", "LANG_TYPE","DISPNAME","LASTMODITIME"};


        try {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 创建时间格式化
            String opNO = req.getOpNO();
            String opName = req.getOpName();
            String lastmodiTime = dfs.format(new Date());

            String menuNo = request.getMenuNo();
            //删除分类
            DelBean db1 = new DelBean("DCP_TOUCHMENU_CLASS");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("MENUNO", new DataValue(menuNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
            //删除分类名称
            DelBean db2 = new DelBean("DCP_TOUCHMENU_CLASS_LANG");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("MENUNO", new DataValue(menuNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            //删除分类商品
            DelBean db3 = new DelBean("DCP_TOUCHMENU_CLASS_GOODS");
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("MENUNO", new DataValue(menuNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            //删除分类商品
            DelBean db4 = new DelBean("DCP_TOUCHMENU_CLASS_GOODS_LANG");
            db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db4.addCondition("MENUNO", new DataValue(menuNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db4));

            DataValue[] insValue = null;
            for (DCP_TouchMenuClassGoodsUpdateReq.level2Elm level2Elm : request.getClassList()) {
                String item = level2Elm.getItem();


                insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(menuNo, Types.VARCHAR),
                        new DataValue(item, Types.VARCHAR),
                        new DataValue(level2Elm.getClassImage(), Types.VARCHAR),
                        new DataValue(level2Elm.getGoodsSortType(), Types.VARCHAR),
                        new DataValue(level2Elm.getRemindType(), Types.VARCHAR),
                        new DataValue(level2Elm.getLabelName(), Types.VARCHAR),
                        new DataValue(level2Elm.getStatus(), Types.VARCHAR),
                        new DataValue(lastmodiTime, Types.DATE)
                };
                InsBean ib1 = new InsBean("DCP_TOUCHMENU_CLASS", columns);
                ib1.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib1));

                for (DCP_TouchMenuClassGoodsUpdateReq.level3Elm level3Elm : level2Elm.getClassName_lang()) {

                    insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(menuNo, Types.VARCHAR),
                            new DataValue(item, Types.VARCHAR),
                            new DataValue(level3Elm.getLangType(), Types.VARCHAR),
                            new DataValue(level3Elm.getName(), Types.VARCHAR),
                            new DataValue(lastmodiTime, Types.DATE)
                    };
                    InsBean ib2 = new InsBean("DCP_TOUCHMENU_CLASS_LANG", columns_lang);
                    ib2.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib2));
                }

                for (DCP_TouchMenuClassGoodsUpdateReq.level4Elm level4Elm : level2Elm.getGoodsList())
                {
                    insValue = new DataValue[]
                            {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(menuNo, Types.VARCHAR) ,
                            new DataValue(item, Types.VARCHAR),
                            new DataValue(level4Elm.getSubItem(), Types.VARCHAR),
                            new DataValue(level4Elm.getPluType(), Types.VARCHAR),
                            new DataValue(level4Elm.getPluNo(), Types.VARCHAR),
                            new DataValue(level4Elm.getUnitId(), Types.VARCHAR),
                            new DataValue(level4Elm.getPrice(), Types.VARCHAR),
                            new DataValue(lastmodiTime, Types.DATE),
                            new DataValue(level4Elm.getRemindType(), Types.VARCHAR)
                    };
                    InsBean ib3= new InsBean("DCP_TOUCHMENU_CLASS_GOODS", columns_goods);
                    ib3.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib3));

                    for (DCP_TouchMenuClassGoodsUpdateReq.level3Elm level3Elm : level4Elm.getDispName_lang()) {

                        insValue = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(menuNo, Types.VARCHAR),
                                new DataValue(item, Types.VARCHAR),
                                new DataValue(level4Elm.getSubItem(), Types.VARCHAR),
                                new DataValue(level3Elm.getLangType(), Types.VARCHAR),
                                new DataValue(level3Elm.getName(), Types.VARCHAR),
                                new DataValue(lastmodiTime, Types.DATE)
                        };
                        InsBean ib4 = new InsBean("DCP_TOUCHMENU_CLASS_GOODS_LANG", columns_goods_lang);
                        ib4.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib4));
                    }
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
    protected List<InsBean> prepareInsertData(DCP_TouchMenuClassGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TouchMenuClassGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TouchMenuClassGoodsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TouchMenuClassGoodsUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_TouchMenuClassGoodsUpdateReq.level1Elm request = req.getRequest();
        String menuNo = request.getMenuNo();
        List<DCP_TouchMenuClassGoodsUpdateReq.level2Elm> classList = request.getClassList();

        if (Check.Null(menuNo)) {
            isFail = true;
            errMsg.append("菜单编码不可为空值, ");
        }

        if (CollectionUtils.isEmpty(classList)) {
            isFail = true;
            errMsg.append("菜单分类不可为空值, ");
        }else{
            for (DCP_TouchMenuClassGoodsUpdateReq.level2Elm level2Elm : classList) {
                if (Check.Null(level2Elm.getItem())) {
                    isFail = true;
                    errMsg.append("分类项次不可为空值, ");
                }

                if (Check.Null(level2Elm.getClassName())) {
                    isFail = true;
                    errMsg.append("分类名称不可为空值, ");
                }

                if (CollectionUtils.isEmpty(level2Elm.getClassName_lang())) {
                    isFail = true;
                    errMsg.append("分类名称多语言不可为空值, ");
                }

                if (Check.Null(level2Elm.getGoodsSortType())) {
                    isFail = true;
                    errMsg.append("商品排序不可为空值, ");
                }

                if (Check.Null(level2Elm.getRemindType())) {
                    isFail = true;
                    errMsg.append("提醒类型不可为空值, ");
                }

                if (Check.Null(level2Elm.getStatus())) {
                    isFail = true;
                    errMsg.append("分类状态不可为空值, ");
                }

                if (CollectionUtils.isEmpty(level2Elm.getGoodsList())) {
                    isFail = true;
                    errMsg.append("子项次商品不可为空值, ");
                }else{
                    for (DCP_TouchMenuClassGoodsUpdateReq.level4Elm level4Elm : level2Elm.getGoodsList()) {

                        if (Check.Null(level4Elm.getSubItem())) {
                            isFail = true;
                            errMsg.append("子项次Item不可为空值, ");
                        }

//                        if (Check.Null(level4Elm.getPluType())) {
//                            isFail = true;
//                            errMsg.append("商品类型不可为空值, ");
//                        }

                        if (Check.Null(level4Elm.getPluNo())) {
                            isFail = true;
                            errMsg.append("商品编码不可为空值, ");
                        }

                        if (Check.Null(level4Elm.getDispName())) {
                            isFail = true;
                            errMsg.append("商品显示名称不可为空值, ");
                        }
                        //默认值-1
                        if (Check.Null(level4Elm.getRemindType()))
                        {
                            level4Elm.setRemindType("-1");
                        }

//                        if (Check.Null(level4Elm.getPrice())) {
//                            isFail = true;
//                            errMsg.append("销售单位价格不可为空值, ");
//                        }

//                        if (Check.Null(level4Elm.getUnitId())) {
//                            isFail = true;
//                            errMsg.append("销售单位编码不可为空值, ");
//                        }
//
//                        if (Check.Null(level4Elm.getUnitName())) {
//                            isFail = true;
//                            errMsg.append("销售单位名称不可为空值, ");
//                        }
                    }
                }
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_TouchMenuClassGoodsUpdateReq> getRequestType() {
        return new TypeToken<DCP_TouchMenuClassGoodsUpdateReq>(){};
    }

    @Override
    protected DCP_TouchMenuClassGoodsUpdateRes getResponseType() {
        return new DCP_TouchMenuClassGoodsUpdateRes() ;
    }
}
