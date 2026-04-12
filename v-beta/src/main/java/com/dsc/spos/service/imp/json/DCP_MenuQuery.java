package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_AccountQueryReq;
import com.dsc.spos.json.cust.req.DCP_MenuQueryReq;
import com.dsc.spos.json.cust.res.DCP_AccountQueryRes;
import com.dsc.spos.json.cust.res.DCP_MenuQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_MenuQuery extends SPosBasicService<DCP_MenuQueryReq, DCP_MenuQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_MenuQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected TypeToken<DCP_MenuQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_MenuQueryReq>() {
        };
    }

    @Override
    protected DCP_MenuQueryRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_MenuQueryRes();
    }

    List<Map<String, Object>> allFuncDatas = new ArrayList<Map<String, Object>>();

    @Override
    protected DCP_MenuQueryRes processJson(DCP_MenuQueryReq req) throws Exception {
        // TODO Auto-generated method stub

        String sql = null;
        // 查詢資料
        DCP_MenuQueryRes res = null;
        res = this.getResponse();
        sql = this.getQuerySql(req);

        String[] conditionValues1 = {}; // 查詢條件

        List<Map<String, Object>> getFstData = this.doQueryData(sql, conditionValues1);
        if (getFstData != null && getFstData.isEmpty() == false) {
            // 单头主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("FMODULARNO", true);
            // 调用过滤函数
            List<Map<String, Object>> getFstMenuDatas = MapDistinct.getMap(getFstData, condition);

            res.setDatas(new ArrayList<DCP_MenuQueryRes.level1Elm>());
            for (Map<String, Object> fstMenuData : getFstMenuDatas) {
                String fModularLevel = fstMenuData.get("FMODULARLEVEL").toString();
                if (fModularLevel != null && fModularLevel.equals("1")) {
                    // 取出第一层
                    String fModularNO = fstMenuData.get("FMODULARNO").toString();
                    String fModularName = fstMenuData.get("MODULAR_CHSMSG").toString();
                    String fModularNameCht = fstMenuData.get("MODULAR_CHTMSG").toString();
                    String fModularNameEng = fstMenuData.get("MODULAR_ENGMSG").toString();
                    String fUpperModular = fstMenuData.get("FUPPERMODULAR").toString();

                    String fSType = fstMenuData.get("FSTYPE").toString();
                    String fFType = fstMenuData.get("FFTYPE").toString();
                    String fProName = fstMenuData.get("FPRONAME").toString();
                    String fParameter = fstMenuData.get("FPARAMETER").toString();
                    String fstatus = fstMenuData.get("FSTATUS").toString();
                    String fUpModularName    = fstMenuData.get("FUPMODULARNAME").toString();
                    // 2018-11-07 yyy 添加priority字段，用于上下移动排序
                    String priority = fstMenuData.get("PRIORITY").toString();
                    String fOnSale  = fstMenuData.get("MODULAR_ONSALE").toString();

                    DCP_MenuQueryRes.level1Elm oneLv1 = res.new level1Elm();

                    oneLv1.setChildren(new ArrayList<DCP_MenuQueryRes.level1Elm>());
                    oneLv1.setFunction(new ArrayList<DCP_MenuQueryRes.function>());
                    // 设置响应
                    oneLv1.setModularNo(fModularNO);
                    oneLv1.setModularName(fModularName);
                    oneLv1.setModularNameCht(fModularNameCht);
                    oneLv1.setModularNameEng(fModularNameEng);
                    oneLv1.setUpperModular(fUpperModular);
                    oneLv1.setModularLevel(fModularLevel);
                    oneLv1.setsType(fSType);
                    oneLv1.setfType(fFType);
                    oneLv1.setProName(fProName);
                    oneLv1.setParameter(fParameter);
                    oneLv1.setStatus(fstatus);
                    oneLv1.setUpModularName(fUpModularName);
                    oneLv1.setPriority(priority);
                    oneLv1.setOnSale(fOnSale);;


                    oneLv1.setRFUNCNO(fstMenuData.getOrDefault("RFUNCNO","").toString());
                    oneLv1.setRPATTERN(fstMenuData.getOrDefault("RPATTERN","").toString());
                    oneLv1.setREGEDISTREX(fstMenuData.getOrDefault("REGEDISTREX","").toString());

                    if(req.getOpNO().equals("NRC")||fModularNO.equals("24") || fModularNO.equals("12"))
                    {
                        oneLv1.setInsert_inner("Y");
                        oneLv1.setModify_inner("Y");
                        oneLv1.setDelete_inner("Y");
                        oneLv1.setIsReport("Y");

                    }else
                    {
                        oneLv1.setInsert_inner("N");
                        oneLv1.setModify_inner("N");
                        oneLv1.setDelete_inner("N");
                        oneLv1.setIsReport("N");
                    }
                    // 添加一级菜单的func
                    for (Map<String, Object> fstFuncDatas : getFstMenuDatas) {
                        // 过滤属于此单头的明细
                        if (fModularNO.equals(fstFuncDatas.get("FMODULARNO")) == false)
                            continue;
                        // 在这里过滤除属于第一级的func
                        DCP_MenuQueryRes.function fstFunc = res.new function();

                        String funcNO = fstFuncDatas.get("FUNCNO").toString();
                        if (funcNO.trim().equals(""))
                            continue;// 过滤掉空值

                        String funcName = fstFuncDatas.get("FUNCTION_CHSMSG").toString();
                        String funcName_CHT=fstFuncDatas.get("FUNCTION_CHTMSG").toString();
                        String funcName_ENG=fstFuncDatas.get("FUNCTION_ENGMSG").toString();
                        String funcFType = fstFuncDatas.get("FUNCFTYPE").toString();
                        String funcProName = fstFuncDatas.get("FUNCPRONAME").toString();
                        String funcParameter = fstFuncDatas.get("FUNCPARAMETER").toString();
                        String funcStatus = fstFuncDatas.get("FUNCSTATUS").toString();
                        String funcOnSale = fstFuncDatas.get("FUNCTION_ONSALE").toString();
                        // 2019-01-23 yuanyy， 金大爷让加的字段， 前端需控制：若为POS菜单， 显示
                        // 并维护此列（是否需要审批 APPROVENEED）；
                        // String approveNeed =
                        // fstFuncDatas.get("APPROVENEED").toString();

                        fstFunc.setFuncStatus(funcStatus);
                        fstFunc.setFuncFType(funcFType);
                        fstFunc.setFuncName(funcName);
                        fstFunc.setFuncNameCht(funcName_CHT);
                        fstFunc.setFuncNameEng(funcName_ENG);
                        fstFunc.setFuncNo(funcNO);
                        fstFunc.setFuncParameter(funcParameter);
                        fstFunc.setFuncProName(funcProName);
                        fstFunc.setOnSale(funcOnSale);
                        // fstFunc.setApproveNeed(approveNeed);
                        oneLv1.getFunction().add(fstFunc);

                    } // 添加一级菜单func结束

                    setChildrenDatas(oneLv1, getFstData);
                    res.getDatas().add(oneLv1);
                }

            } // 判断level1Elm 结束

        } else {
            res.setDatas(new ArrayList<DCP_MenuQueryRes.level1Elm>());
        }

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_MenuQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        String eId = req.geteId();
      //  DCP_MenuQueryReq.level1Elm request = req.getRequest();

        // 計算起啟位置
        //String status = request.getStatus();
       // String keyTxt = request.getKeyTxt();
      //  String sType = request.getsType();
        String langType = req.getLangType();
      //  if (status == null)
       //     status = "";
      //  if (keyTxt == null)
       //     keyTxt = "";
     //   if (sType == null)
      //      sType = "";

        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(
                "  SELECT fmodularno,fmodularlevel,fuppermodular,fupModularName,fstype,fftype,fproname,fparameter ,  fstatus,"
                        + "EID , priority "
                        + ",funcNO,  funcftype , funcProName,funcparameter ,funcstatus "
                        + ",RFUNCNO , RPATTERN , REGEDISTREX ,modular_engmsg,modular_chtmsg,modular_chsmsg "
                        + ",function_engmsg,function_chsmsg,function_chtmsg ,function_onsale,modular_onsale "
                        + " FROM " + "(SELECT " + "a.modularno AS fmodularno,"
        );
        if (langType.equals("zh_TW")) {
            sqlbuf.append("b.chtmsg  AS fupModularName,");
        } else if (langType.equals("zh_EN"))
            sqlbuf.append("b.engmsg  AS fupModularName,");
        else
            sqlbuf.append("b.chsmsg  AS fupModularName,");
        sqlbuf.append("  a.modularlevel AS fmodularlevel ,"
                + " a.uppermodular AS fuppermodular,  a.stype AS  fstype ,"
                + " a.ftype AS fftype , a.proname AS fproname, a.parameter AS fparameter, a.status AS fstatus ,"
                + "	a.EID as EID , a.priority "
                + " ,c.funcNO,  c.ftype AS funcftype,  c.proname AS funcProName "
                + " , c.parameter AS funcparameter, c.status AS funcstatus "
                + " , a.RFUNCNO , a.RPATTERN , a.REGEDISTREX  "
                + " ,a.chtmsg as modular_chtmsg,a.engmsg as modular_engmsg,a.chsmsg as modular_chsmsg,nvl(a.onsale,'Y') as modular_onsale ,c.chsmsg as function_chsmsg,c.chtmsg as function_chtmsg"
                + " ,c.engmsg as function_engmsg,  nvl(c.onsale,'Y') as function_onsale "
                + " FROM   DCP_MODULAR a   left  join DCP_MODULAR b  on a.EID=b.EID and a.uppermodular = b.modularno  "
                + " LEFT JOIN DCP_MODULAR_function c ON a.EID = c.EID AND a.modularno = c.modularno and c.status='100' "
                + " WHERE a.EID = '" + eId + "' and a.ONSALE='Y' AND c.ONSALE='Y') a   " + " WHERE EID = '"
                + eId + "' ");

       // if (status != null && status.length() > 0) {
       //     sqlbuf.append(" and fstatus='" + status + "' ");
       // }
      //  if (keyTxt != null && keyTxt.length() > 0) {
         //   sqlbuf.append(" START WITH (fmodularno LIKE  '%%" + keyTxt + "%%'  OR fupModularName  LIKE '%%" + keyTxt
        //            + "%%' )  " + " CONNECT BY PRIOR  fuppermodular = fmodularno   ");
       // }
       // if (sType != null && sType.length() > 0) {
       //     sqlbuf.append(" AND FSTYPE='" + sType + "' ");
       // }
        sqlbuf.append(" ORDER BY priority , fmodularno , funcNO ");

        sql = sqlbuf.toString();
        return sql;
    }

    protected List<Map<String, Object>> getChildDatas(List<Map<String, Object>> allMenuDatas, String modularNO) {
        List<Map<String, Object>> menuDataTemp = new ArrayList<>();
        for (Map<String, Object> map : allMenuDatas) {
            if (map.get("FUPPERMODULAR").toString().equals(modularNO)) {
                menuDataTemp.add(map);
            }
        }
        return menuDataTemp;
    }

    // 这里写一个递归的调用当前的方法
    protected void setChildrenDatas(DCP_MenuQueryRes.level1Elm oneLv2, List<Map<String, Object>> allMenuDatas)
            throws Exception {

        try {

            List<Map<String, Object>> upModularList = getChildDatas(allMenuDatas, oneLv2.getModularNo());

            // 主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
            condition.put("FMODULARNO", true);
            // 调用过滤函数
            List<Map<String, Object>> upModularList2 = MapDistinct.getMap(upModularList, condition);

            if (upModularList2 != null && !upModularList2.isEmpty()) {
                for (Map<String, Object> menuDatas : upModularList2) {
                    DCP_MenuQueryRes.level1Elm lv1 = new DCP_MenuQueryRes(). new level1Elm();
                    lv1.setChildren(new ArrayList<DCP_MenuQueryRes.level1Elm>());
                    lv1.setFunction(new ArrayList<DCP_MenuQueryRes.function>());

                    String fModularNO = menuDatas.get("FMODULARNO").toString();
                    String sType = menuDatas.get("FSTYPE").toString();

                    // 设置响应
                    lv1.setModularNo(fModularNO);
                    lv1.setModularName(menuDatas.get("MODULAR_CHSMSG").toString());
                    lv1.setModularNameCht(menuDatas.get("MODULAR_CHTMSG").toString());
                    lv1.setModularNameEng(menuDatas.get("MODULAR_ENGMSG").toString());
                    lv1.setUpperModular(menuDatas.get("FUPPERMODULAR").toString());
                    lv1.setModularLevel(menuDatas.get("FMODULARLEVEL").toString());
                    lv1.setsType(menuDatas.get("FSTYPE").toString());
                    lv1.setfType(menuDatas.get("FFTYPE").toString());
                    lv1.setProName(menuDatas.get("FPRONAME").toString());
                    lv1.setParameter(menuDatas.get("FPARAMETER").toString());
                    lv1.setStatus(menuDatas.get("FSTATUS").toString());
                    lv1.setUpModularName(menuDatas.get("FUPMODULARNAME").toString());
                    lv1.setOnSale(menuDatas.get("MODULAR_ONSALE").toString());
                    // 2018-11-07 yyy 添加priority字段，用于上下移动排序
                    lv1.setPriority(menuDatas.get("PRIORITY").toString());
                    lv1.setRFUNCNO(menuDatas.getOrDefault("RFUNCNO","").toString());
                    lv1.setRPATTERN(menuDatas.getOrDefault("RPATTERN","").toString());
                    lv1.setREGEDISTREX(menuDatas.getOrDefault("REGEDISTREX","").toString());

                    if(fModularNO.startsWith("1203") || fModularNO.startsWith("1204") || fModularNO.startsWith("24") ){ // 如果菜单编码以 1203开头， 说明都是报表
                        lv1.setIsReport("Y");
                    }else{
                        lv1.setIsReport("N");
                    }


                    if(oneLv2.getModularNo().equals("1203")|| oneLv2.getModularNo().equals("1204"))
                    {
                        lv1.setInsert_inner("Y");
                        lv1.setModify_inner("Y");
                        lv1.setDelete_inner("Y");
                    }else
                    {
                        lv1.setInsert_inner(oneLv2.getInsert_inner().toString());
                        lv1.setModify_inner(oneLv2.getModify_inner().toString());
                        lv1.setDelete_inner(oneLv2.getDelete_inner().toString());
                    }

                    for (Map<String, Object> fstFuncDatas : upModularList) {
                        // 过滤属于此单头的明细
                        if (fModularNO.equals(fstFuncDatas.get("FMODULARNO")) == false)
                            continue;
                        // 在这里过滤除属于第一级的function
                        DCP_MenuQueryRes.function fstFunc = new DCP_MenuQueryRes(). new function();

                        String funcNO = fstFuncDatas.get("FUNCNO").toString();
                        if (funcNO.trim().equals(""))
                            continue;// 过滤掉空值

                        // 2019-01-23 yuanyy， 金大爷让加的字段， 前端需控制：若为POS菜单， 显示
                        // 并维护此列（是否需要审批 APPROVENEED）；
                        // String approveNeed =
                        // fstFuncDatas.get("APPROVENEED").toString();

                        String funcName = fstFuncDatas.get("FUNCTION_CHSMSG").toString();
                        String funcName_CHT = fstFuncDatas.get("FUNCTION_CHTMSG").toString();
                        String funcName_ENG = fstFuncDatas.get("FUNCTION_ENGMSG").toString();
                        String funcFType = fstFuncDatas.get("FUNCFTYPE").toString();
                        String funcProName = fstFuncDatas.get("FUNCPRONAME").toString();
                        String funcParameter = fstFuncDatas.get("FUNCPARAMETER").toString();
                        String funcStatus = fstFuncDatas.get("FUNCSTATUS").toString();
                        String funcOnSale = fstFuncDatas.get("FUNCTION_ONSALE").toString();
                        fstFunc.setFuncStatus(funcStatus);
                        fstFunc.setFuncFType(funcFType);
                        fstFunc.setFuncName(funcName);
                        fstFunc.setFuncNameCht(funcName_CHT);
                        fstFunc.setFuncNameEng(funcName_ENG);
                        fstFunc.setFuncNo(funcNO);
                        fstFunc.setFuncParameter(funcParameter);
                        fstFunc.setFuncProName(funcProName);
                        fstFunc.setOnSale(funcOnSale);
                        // fstFunc.setApproveNeed(approveNeed);
                        lv1.getFunction().add(fstFunc);
                    }

                    setChildrenDatas(lv1, allMenuDatas);
                    oneLv2.getChildren().add(lv1);
                }

            }
        } catch (Exception e) {

        }

    }

}
