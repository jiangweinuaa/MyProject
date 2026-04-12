package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ChannelStockDetailReq;
import com.dsc.spos.json.cust.res.DCP_ChannelStockDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_ChannelStockDetail
 * 服务说明：渠道分配库存查询
 * @author wangzyc 2021-03-16
 */
public class DCP_ChannelStockDetail extends SPosBasicService<DCP_ChannelStockDetailReq, DCP_ChannelStockDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_ChannelStockDetailReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ChannelStockDetailReq> getRequestType() {
        return new TypeToken<DCP_ChannelStockDetailReq>(){};
    }
    
    @Override
    protected DCP_ChannelStockDetailRes getResponseType() {
        return new DCP_ChannelStockDetailRes();
    }
    
    @Override
    protected DCP_ChannelStockDetailRes processJson(DCP_ChannelStockDetailReq req) throws Exception {
        DCP_ChannelStockDetailRes res = this.getResponse();
        String eId = req.geteId();
        
        int totalRecords = 0; //总笔数
        int totalPages = 0;
        
        if (req.getPageNumber() == 0) {
            req.setPageNumber(1);
        }
        
        if (req.getPageSize() == 0) {
            req.setPageSize(1000);
        }
        res.setDatas(new DCP_ChannelStockDetailRes().new level1Elm());
        DCP_ChannelStockDetailRes.level1Elm lv1 = res.new level1Elm();
        lv1.setPluList(new ArrayList<>());
        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> datas = this.doQueryData(sql, null);
            
            if(!CollectionUtils.isEmpty(datas)){
                totalRecords = Integer.parseInt(datas.get(0).get("NUM").toString());
                
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                Map<String, Boolean> condition1 = new HashMap<>(); //查詢條件
                condition1.put("EID", true);
                condition1.put("FEATURENO", true);
                condition1.put("ORGANIZATIONNO", true);
                condition1.put("PLUNO", true);
                condition1.put("SUNIT", true);
                condition1.put("WAREHOUSE", true);
                condition1.put("BASEUNIT", true);
                //调用过滤函数
                List<Map<String, Object>> getPlunos= MapDistinct.getMap(datas, condition1);
                
                // 拿到商品单位值预留几位
                sql = "SELECT UNIT,UDLENGTH FROM DCP_UNIT  WHERE EID = '"+req.geteId()+"' and status = '100'";
                List<Map<String, Object>> units = this.doQueryData(sql, null);
                
                String ISHTTPS= PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=ISHTTPS.equals("1")?"https://":"http://";
                
                String DomainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                for (Map<String, Object> getpluno : getPlunos) {
                    DCP_ChannelStockDetailRes.level2Elm lv2 = res.new level2Elm();
                    String organizationno = getpluno.get("ORGANIZATIONNO").toString();
                    String org_name = getpluno.get("ORG_NAME").toString();
                    String pluno = getpluno.get("PLUNO").toString();
                    String plu_name = getpluno.get("PLU_NAME").toString();
                    String featureno = getpluno.get("FEATURENO").toString();
                    String featurename = getpluno.get("FEATURENAME").toString();
                    String baseunit = getpluno.get("BASEUNIT").toString();
                    String warehouse = getpluno.get("WAREHOUSE").toString();
                    String warehouse_name = getpluno.get("WAREHOUSE_NAME").toString();
                    String sunit = getpluno.get("SUNIT").toString();
                    String uname = getpluno.get("UNAME").toString();
                    String listimage = getpluno.get("LISTIMAGE").toString();
                    String avalibleQty = getpluno.get("AVALIBLEQTY").toString();
                    
                    lv2.setOrganizationNo(organizationno);
                    lv2.setOrganizationName(org_name);
                    lv2.setPluNo(pluno);
                    lv2.setPluName(plu_name);
                    lv2.setFeatureNo(featureno.equals(" ")?"":featureno);
                    lv2.setFeatureName(featurename);
                    lv2.setBaseUnit(baseunit);
                    lv2.setWarehouse(warehouse);
                    lv2.setWarehouseName(warehouse_name);
                    lv2.setSUnit(sunit);
                    lv2.setSUnitName(uname);
                    // 拼接URL 返回
                    if(!Check.Null(listimage)){
                        //
                        if (DomainName.endsWith("/"))
                        {
                            lv2.setListImage(httpStr+DomainName+"resource/image/" +listimage);
                        }
                        else
                        {
                            lv2.setListImage(httpStr+DomainName+"/resource/image/"+listimage);
                        }
                    }else{
                        lv2.setListImage(listimage);
                    }
                    String unitConvert = PosPub.getUnitConvert(dao, eId, pluno, baseunit, sunit, avalibleQty + "");
                    // 保留几位
                    String sUnitRetain = "";
                    
                    for (Map<String, Object> unit : units) {
                        String unit1 = unit.get("UNIT").toString();
                        int udlength = Integer.parseInt(unit.get("UDLENGTH").toString());
                        StringBuffer reta = new StringBuffer("0");
                        if(unit1.equals(sunit)){
                            if(udlength!=0){
                                reta.append(".");
                                for(int i2 =0;i2<=udlength;i2++){
                                    reta.append("0");
                                }
                            }
                            sUnitRetain =  reta.toString();
                        }
                    }
                    // 现在的基准单位数量
                    String strsUnit = new DecimalFormat(sUnitRetain).format(Double.parseDouble(unitConvert));
                    lv2.setAvalibleQty(strsUnit);
                    
                    lv2.setChannelList(new ArrayList<>());
                    for (Map<String, Object> data : datas) {
                        String organizationno2 = data.get("ORGANIZATIONNO").toString();
                        String pluno2 = data.get("PLUNO").toString();
                        String featureno2 = data.get("FEATURENO").toString();
                        String sunit2 = data.get("SUNIT").toString();
                        String baseunit2 = data.get("BASEUNIT").toString();
                        String warehouse2 = data.get("WAREHOUSE").toString();
                        // 过滤不属于当前商品的资料
                        if(organizationno2.equals(organizationno)&&pluno.equals(pluno2)&&featureno.equals(featureno2)&&sunit.equals(sunit2)&&baseunit2.equals(baseunit)&&warehouse.equals(warehouse2)){
                            String channelid = data.get("CHANNELID").toString();
                            String channelname = data.get("CHANNELNAME").toString();
                            String onlineqty = data.get("ONLINEQTY").toString();
                            if(!Check.Null(channelid)){
                                DCP_ChannelStockDetailRes.level3Elm lv3 = res.new level3Elm();
                                lv3.setChannelId(channelid);
                                lv3.setChannelName(channelname);
                                lv3.setOnlineQty(onlineqty);
                                lv2.getChannelList().add(lv3);
                            }
                        }
                    }
                    lv1.getPluList().add(lv2);
                }
                res.setDatas(lv1);
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
            
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败！" + e.getMessage());
        }
        
        return res;
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_ChannelStockDetailReq req) throws Exception {
        Logger logger = LogManager.getLogger(DCP_ChannelStockDetail.class.getName());
        DCP_ChannelStockDetailReq.level1Elm request = req.getRequest();
        String langType = req.getLangType();
        String shopId = req.getShopId();
        String eId = req.geteId();
        String companyId = req.getBELFIRM();
        String org_form = req.getOrg_Form(); // 0:公司  2:门店
        if(org_form.equals("0")){
            companyId = shopId;
        }else if(Check.Null(companyId)||companyId.equals("null")){
            // 查询下组织表 在服务器上回出现 Token 中的 request 的 belfirm 为null 的情况 本地有值
            String sql = "select BELFIRM from DCP_ORG where EID = '"+eId+"' and ORG_FORM = '2' and ORGANIZATIONNO = '"+shopId+"'";
            List<Map<String, Object>> getBelfirm = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getBelfirm)){
                companyId =  getBelfirm.get(0).get("BELFIRM").toString();
            }
        }
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        
        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;
        StringBuffer sqlBuffer  = new StringBuffer();
        // 商品模板表
       /* sqlBuffer.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                //and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店
                + " where a.eid='"+eId+"' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 "
                + " )"
                + " ");*/
    
    
        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        sqlBuffer.append(" "
                + " with goodstemplate as ("
                + " select b.* from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')  "
                + " )");
        
        sqlBuffer.append("SELECT * FROM ( " +
                " SELECT count(DISTINCT gt.eid||gt.pluno||do.ORGANIZATIONNO||dgf.FEATURENO) OVER () AS num, ROW_NUMBER() OVER (ORDER BY gt.eid, gt.pluno, do.ORGANIZATIONNO, dgf.FEATURENO) AS rn , " +
                " gt.EID,do.organizationNo,b.ORG_NAME, gt.PLUNO, c.PLU_NAME, dgf.FEATURENO, d.FEATURENAME , e.BASEUNIT, do.OUT_COST_WAREHOUSE as WAREHOUSE, e.SUNIT, f.UNAME, " +
                " g.WAREHOUSE_NAME , h.LISTIMAGE , NVL(a.QTY - a.LOCKQTY - a.ONLINEQTY, 0) AS avalibleQty , k.CHANNELID, j.CHANNELNAME, i.ONLINEQTY " +
                " FROM goodstemplate gt " +
                " RIGHT JOIN dcp_org do ON gt.eid = do.eid AND do.ORG_FORM = '2' " +
                " LEFT JOIN dcp_goods_feature dgf ON gt.pluno = dgf.PLUNO AND gt.eid = dgf.EID " +
                " LEFT JOIN dcp_stock a ON a.eid = gt.eid AND gt.pluno = a.pluno AND do.ORGANIZATIONNO = a.ORGANIZATIONNO AND do.OUT_COST_WAREHOUSE = a.WAREHOUSE AND NVL(dgf.FEATURENO ,' ')  = a.FEATURENO " +
                " LEFT JOIN DCP_ORG_LANG b ON gt.EID = b.EID AND do.ORGANIZATIONNO = b.ORGANIZATIONNO AND b.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODS_LANG c ON gt.EID = c.EID AND gt.PLUNO = c.PLUNO AND c.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODS_FEATURE_LANG d ON gt.EID = d.EID AND dgf.FEATURENO = d.FEATURENO AND d.LANG_TYPE = '"+langType+"' AND gt.PLUNO = d.PLUNO " +
                " RIGHT JOIN DCP_GOODS e ON gt.EID = e.EID AND gt.PLUNO = e.PLUNO AND e.STATUS = '100' " +
                " LEFT JOIN DCP_UNIT_LANG f ON gt.EID = f.EID AND e.SUNIT = f.UNIT AND f.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_WAREHOUSE_LANG g ON gt.EID = g.EID AND do.OUT_COST_WAREHOUSE = g.WAREHOUSE AND do.ORGANIZATIONNO = g.ORGANIZATIONNO AND g.STATUS = '100' AND g.LANG_TYPE = '"+langType+"' " +
                " LEFT JOIN DCP_GOODSIMAGE h ON gt.EID = h.EID AND h.APPTYPE = 'ALL' AND gt.PLUNO = h.PLUNO  " +
                " LEFT JOIN DCP_STOCK_CHANNEL i ON a.EID = i.EID AND do.ORGANIZATIONNO = i.ORGANIZATIONNO AND a.PLUNO = i.PLUNO AND a.FEATURENO = i.FEATURENO AND a.WAREHOUSE = i.WAREHOUSE " +
                " LEFT JOIN DCP_STOCK_ALLOCATION_RULE k ON a.EID = k.EID AND a.ORGANIZATIONNO = k.ORGANIZATIONNO AND a.PLUNO = k.PLUNO AND a.FEATURENO = k.FEATURENO AND i.CHANNELID  = k.CHANNELID " +
                " LEFT JOIN CRM_CHANNEL j ON gt.EID = j.EID AND k.CHANNELID = j.CHANNELID " +
                " WHERE gt.eid = '"+eId+"'");
        String pluNo = request.getPluNo();
        if(!Check.Null(pluNo)){
            sqlBuffer.append(" and gt.pluno = '"+pluNo+"'");
        }
        
        String keyTxt = request.getKeyTxt();
        if(!Check.Null(keyTxt)){
            sqlBuffer.append(" and (gt.pluno like  '%%"+keyTxt+"%%' OR c.PLU_NAME LIKE '%%"+keyTxt+"%%')");
        }
        
        List<DCP_ChannelStockDetailReq.level2Elm> organizationList = request.getOrganizationList();
        if(!CollectionUtils.isEmpty(organizationList)){
            sqlBuffer.append(" and do.ORGANIZATIONNO IN ( ");
            for (DCP_ChannelStockDetailReq.level2Elm level2Elm : organizationList) {
                sqlBuffer.append("'"+level2Elm.getOrganizationNo()+"',");
            }
            sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
            sqlBuffer.append(")");
        }
        
        String ruleType = request.getRuleType();
        if(!Check.Null(ruleType)){
            if(ruleType.equals("0")){
                sqlBuffer.append(" and i.CHANNELID is NOT NULL");
            }else if(ruleType.equals("1")){
                sqlBuffer.append(" and i.CHANNELID is NULL");
            }
        }
        
        sqlBuffer.append(") WHERE rn > "+startRow+" AND rn <= "+(startRow+pageSize)+"");
        logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******渠道库存查询请求SQL:"+ sqlBuffer +"******\r\n");
        return sqlBuffer.toString();
    }
    
}
