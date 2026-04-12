package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_TouchMenuClassGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_TouchMenuClassGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 门店触屏菜单商品设置查询
 * @author: wangzyc
 * @create: 2021-06-17
 */
public class DCP_TouchMenuClassGoodsQuery extends SPosBasicService<DCP_TouchMenuClassGoodsQueryReq, DCP_TouchMenuClassGoodsQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_TouchMenuClassGoodsQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String menuNo = req.getRequest().getMenuNo();
        if (Check.Null(menuNo)) {
            isFail = true;
            errMsg.append("菜单编码不可为空值, ");
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_TouchMenuClassGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_TouchMenuClassGoodsQueryReq>() {
        };
    }

    @Override
    protected DCP_TouchMenuClassGoodsQueryRes getResponseType() {
        return new DCP_TouchMenuClassGoodsQueryRes();
    }

    @Override
    protected DCP_TouchMenuClassGoodsQueryRes processJson(DCP_TouchMenuClassGoodsQueryReq req) throws Exception {
        DCP_TouchMenuClassGoodsQueryRes res = null;
        res = this.getResponse();

        try {
            String langType = req.getLangType();
            DCP_TouchMenuClassGoodsQueryRes.level1Elm level1Elm = res.new level1Elm();
            res.setDatas(level1Elm);

            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getDataDetails = this.doQueryData(sql, null);

            // 过滤
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
//            condition.put("MENUNO", true);
            condition.put("ITEM", true);
            // 调用过滤函数
            List<Map<String, Object>> getClass = MapDistinct.getMap(getDataDetails, condition);

            // 过滤
            Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查詢條件
//            condition2.put("MENUNO", true);
            condition2.put("ITEM", true);
            condition2.put("CLASS_LANG_TYPE", true);
            // 调用过滤函数
            List<Map<String, Object>> getClass_lang = MapDistinct.getMap(getDataDetails, condition2);

            // 过滤
            Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); // 查詢條件
//            condition3.put("MENUNO", true);
            condition3.put("ITEM", true);
            condition3.put("SUBITEM", true);
            condition3.put("PLUNO", true);
            // 调用过滤函数
            List<Map<String, Object>> getClass_Goods = MapDistinct.getMap(getDataDetails, condition3);

            // 过滤
            Map<String, Boolean> condition4 = new HashMap<String, Boolean>(); // 查詢條件
//            condition4.put("MENUNO", true);
            condition4.put("ITEM", true);
            condition4.put("SUBITEM", true);
            condition4.put("LANG_TYPE", true);
            // 调用过滤函数
            List<Map<String, Object>> getClass_Goods_lang = MapDistinct.getMap(getDataDetails, condition4);

            if (!CollectionUtils.isEmpty(getDataDetails)) {
                String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=ISHTTPS.equals("1")?"https://":"http://";
                String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");

                String menuno = getDataDetails.get(0).get("MENUNO").toString();
                String menuName = getDataDetails.get(0).get("MENUNAME").toString();

                level1Elm.setMenuNo(menuno);
                level1Elm.setMenuName(menuName);
                level1Elm.setClassList(new ArrayList<DCP_TouchMenuClassGoodsQueryRes.level2Elm>());

                if(!CollectionUtils.isEmpty(getClass)) {

                    for (Map<String, Object> oneData : getClass) {
                        DCP_TouchMenuClassGoodsQueryRes.level2Elm level2Elm = res.new level2Elm();
                        String item = oneData.get("ITEM").toString();

                        if(Check.Null(item)){
                            continue;
                        }

                        String classImage = oneData.get("CLASSIMAGE").toString();
                        String goodsSortType = oneData.get("GOODSSORTTYPE").toString();
                        String remindType = oneData.get("REMINDTYPE").toString();
                        String labelName = oneData.get("LABELNAME").toString();
                        String status = oneData.get("STATUS").toString();

                        level2Elm.setItem(item);
                        level2Elm.setClassImage(classImage);

                        if(!Check.Null(classImage)){
                            // 拼接返回图片路径
                            if (DomainName.endsWith("/"))
                            {
                                level2Elm.setClassImageUrl(httpStr+DomainName+"resource/image/" +classImage);
                            }
                            else
                            {
                                level2Elm.setClassImageUrl(httpStr+DomainName+"/resource/image/" +classImage);
                            }
                        }else{
                            level2Elm.setClassImageUrl("");
                        }
                        level2Elm.setGoodsSortType(goodsSortType);
                        level2Elm.setRemindType(remindType);
                        level2Elm.setLabelName(labelName);
                        level2Elm.setStatus(status);
                        level2Elm.setClassName("");

                        level2Elm.setClassName_lang(new ArrayList<DCP_TouchMenuClassGoodsQueryRes.level3Elm>());
                        for (Map<String, Object> oneData2 : getClass_lang) {
                            String menuno2 = oneData2.get("MENUNO").toString();
                            String item2 = oneData2.get("ITEM").toString();


                            if (menuno2.equals(menuno) && item.equals(item2)) {
                                String class_lang_type = oneData2.get("CLASS_LANG_TYPE").toString();
                                String classname = oneData2.get("CLASSNAME").toString();

                                if (langType.equals(class_lang_type)) {
                                    level2Elm.setClassName(classname);
                                }

                                DCP_TouchMenuClassGoodsQueryRes.level3Elm level3Elm = res.new level3Elm();
                                level3Elm.setLangType(class_lang_type);
                                level3Elm.setName(classname);

                                level2Elm.getClassName_lang().add(level3Elm);
                            }
                        }

                        level2Elm.setGoodsList(new ArrayList<DCP_TouchMenuClassGoodsQueryRes.level4Elm>());
                        for (Map<String, Object> oneData3 : getClass_Goods) {
                            DCP_TouchMenuClassGoodsQueryRes.level4Elm level4Elm = res.new level4Elm();
                            String menuno3 = oneData3.get("MENUNO").toString();
                            String item3 = oneData3.get("ITEM").toString();
                            String subItem = oneData3.get("SUBITEM").toString();

                            if (menuno3.equals(menuno) && item.equals(item3)) {

                                if(Check.Null(subItem)){
                                    continue;
                                }

                                String pluType = oneData3.get("PLUTYPE").toString();
                                String pluno = oneData3.get("PLUNO").toString();
                                String unit = oneData3.get("UNIT").toString();
                                String price = oneData3.get("PRICE").toString();
                                String uname = oneData3.get("UNAME").toString();

                                level4Elm.setSubItem(subItem);
                                level4Elm.setPluType(pluType);
                                level4Elm.setPluNo(pluno);
                                level4Elm.setUnitId(unit);
                                level4Elm.setPrice(price);
                                level4Elm.setUnitName(uname);
                                level4Elm.setDispName("");
                                level4Elm.setRemindType(oneData3.get("E_REMINDTYPE").toString());

                                level4Elm.setDispName_lang(new ArrayList<DCP_TouchMenuClassGoodsQueryRes.level3Elm>());
                                for (Map<String, Object> oneData4 : getClass_Goods_lang) {
                                    String menuno4 = oneData4.get("MENUNO").toString();
                                    String item4 = oneData4.get("ITEM").toString();
                                    String subItem4 = oneData4.get("SUBITEM").toString();


                                    if (menuno4.equals(menuno) && item.equals(item4) && subItem.equals(subItem4)) {
                                        String goods_lang_type = oneData4.get("LANG_TYPE").toString();
                                        String dispname = oneData4.get("DISPNAME").toString();

                                        if (langType.equals(goods_lang_type)) {
                                            level4Elm.setDispName(dispname);
                                        }

                                        DCP_TouchMenuClassGoodsQueryRes.level3Elm level3Elm = res.new level3Elm();
                                        level3Elm.setLangType(goods_lang_type);
                                        level3Elm.setName(dispname);

                                        level4Elm.getDispName_lang().add(level3Elm);
                                    }
                                }
                                level2Elm.getGoodsList().add(level4Elm);
                            }
                        }
                        level1Elm.getClassList().add(level2Elm);
                    }
                    res.setDatas(level1Elm);
                }
            }

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }


        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_TouchMenuClassGoodsQueryReq req) throws Exception {
        String sql = null;
        String eId = req.geteId();
        String langType = req.getLangType();
        String menuNO = req.getRequest().getMenuNo();

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT a.MENUNO, b.MENUNAME, c.ITEM, d.LANG_TYPE Class_LANG_TYPE, d.CLASSNAME , c.CLASSIMAGE, c.GOODSSORTTYPE, c.REMINDTYPE, " +
                " c.LABELNAME, c.STATUS , e.SUBITEM,CASE WHEN NVL(e.PLUTYPE,'NORMAL')='MULTISPEC' THEN e.PLUTYPE ELSE g.PLUTYPE END AS  PLUTYPE, e.PLUNO, f.LANG_TYPE, f.DISPNAME , e.UNIT, e.PRICE,e.REMINDTYPE as E_REMINDTYPE, h.UNAME " +
                " FROM DCP_TOUCHMENU a " +
                " LEFT JOIN DCP_TOUCHMENU_LANG b ON a.EID = b.EID AND a.MENUNO = b.MENUNO AND b.LANG_TYPE = '" + langType + "' " +
                " LEFT JOIN DCP_TOUCHMENU_CLASS c ON a.EID = c.EID AND a.MENUNO = c.MENUNO " +
                " LEFT JOIN DCP_TOUCHMENU_CLASS_LANG d ON a.EID = d.EID AND a.MENUNO = d.MENUNO AND c.ITEM = d.ITEM " +
                " LEFT JOIN DCP_TOUCHMENU_CLASS_GOODS e ON a.EID = e.EID AND a.MENUNO = e.MENUNO AND c.ITEM = e.ITEM " +
                " LEFT JOIN DCP_TOUCHMENU_CLASS_GOODS_LANG f ON a.EID = f.EID AND a.MENUNO = f.MENUNO AND c.ITEM = f.ITEM AND e.SUBITEM = f.SUBITEM " +
                " LEFT JOIN DCP_UNIT_LANG h ON a.EID = h.EID AND e.UNIT = h.UNIT AND h.LANG_TYPE = '" + langType + "' "
              + " LEFT JOIN DCP_GOODS g on e.EID=g.EID and e.PLUNO=g.pluno " +
                " WHERE a.eid = '" + eId + "'");
        if (!Check.Null(menuNO)) {
            sqlbuf.append(" AND a.MENUNO LIKE '%%"+menuNO+"%%'");
        }
        sqlbuf.append(" ORDER BY c.item,e.SUBITEM");
        sql = sqlbuf.toString();
        return sql;
    }
}
