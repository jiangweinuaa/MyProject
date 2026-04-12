package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dsc.spos.json.cust.req.DCP_GoodsFeatureQueryReq;
import com.dsc.spos.json.cust.req.DCP_GoodsFeatureQueryReq.*;
import com.dsc.spos.json.cust.res.DCP_GoodsFeatureQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @apiNote 商品明细获取
 * @author jinzma
 * @since  2019-07-16
 */
public class DCP_GoodsFeatureQuery extends SPosBasicService<DCP_GoodsFeatureQueryReq,DCP_GoodsFeatureQueryRes> {
    Logger logger = LogManager.getLogger(DCP_GoodsFeatureQuery.class.getName());
    @Override
    protected boolean isVerifyFail(DCP_GoodsFeatureQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_GoodsFeatureQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsFeatureQueryReq>(){};
    }

    @Override
    protected DCP_GoodsFeatureQueryRes getResponseType() {
        return new DCP_GoodsFeatureQueryRes();
    }

    @Override
    protected DCP_GoodsFeatureQueryRes processJson(DCP_GoodsFeatureQueryReq req) throws Exception {
        DCP_GoodsFeatureQueryRes res =this.getResponse();
        //取得 SQL
        String sql;
        levelElm request = req.getRequest();
        String shopId = req.getShopId();
        String eId = req.geteId();
        String companyId = req.getBELFIRM();
        String orgForm = req.getOrg_Form();
        String langType = req.getLangType();
        String billType =request.getBillType(); //单据类型：0-要货类 2-完工入库/加工模板 3-采购单 4-转换合并 5-组合拆解 6-计划报单 7-退货出库 9-盘点单 10-销售类 11-拼胚 12-盘点子任务单 14-调拨出库单
        if (Check.Null(billType))
            billType="-1"; // 单据类型为空得时候，塞个默认值
        String reqSupplierId = request.getSupplierId();
        String bringBackReceiptOrg = request.getBringBackReceiptOrg();
        if (Check.Null(bringBackReceiptOrg))
            bringBackReceiptOrg="N";
        String defReceiptOrg = "";
        String defReceiptOrgName = "";

        String queryStockqty=req.getRequest().getQueryStockqty();
        String queryStockWarehouse=req.getRequest().getQueryStockWarehouse();

        String keyTxt = req.getRequest().getKeyTxt();
        String purchaseType = req.getRequest().getPurchaseType();
        String[] tags = req.getRequest().getTag();   //商品标签

        String ISHTTPS = req.getRequest().getISHTTPS();                           //启用https：1启用，0不启用
        String DomainName = req.getRequest().getDomainName();                     //图片服务地址或域名
        String CheckSuppGoods = req.getRequest().getCheckSuppGoods();             //Y是，N否：采购入库作业
        String IsStockMultipleUnit = req.getRequest().getIsStockMultipleUnit();   //是否启用盘点多单位盘点：Y是，N否，盘点作业

        if(Check.Null(CheckSuppGoods)) {
            CheckSuppGoods=PosPub.getPARA_SMS(dao,eId , shopId, "CheckSuppGoods");
        }
        if(Check.Null(CheckSuppGoods)) {
            CheckSuppGoods="N";
        }
        if (Check.Null(IsStockMultipleUnit) && billType.equals("9"))   //仅盘点才需查询 by jinzma 20210719
            IsStockMultipleUnit = PosPub.getPARA_SMS(dao, eId, shopId, "IsStockMultipleUnit");
        if (Check.Null(IsStockMultipleUnit))
            IsStockMultipleUnit="N";

        // 拼接返回图片路径
        if (Check.Null(ISHTTPS)) {
            ISHTTPS = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
        }
        String httpStr=ISHTTPS.equals("1")?"https://":"http://";
        if (Check.Null(DomainName)) {
            DomainName = PosPub.getPARA_SMS(dao, eId, "", "DomainName");
        }
        if (DomainName.endsWith("/")) {
            DomainName = httpStr+DomainName+"resource/image/";
        }else{
            DomainName = httpStr+DomainName+"/resource/image/";
        }

        ///if Org_Form==0 公司时，所属公司BELFIRM 就等于组织自己  BY JZMA 20200720
        if (Check.Null(companyId)) {
            ///组织类型 0-公司  1-组织  2-门店 3-其它
            if (orgForm.equals("0")) {
                companyId=shopId;
            }else {
                sql=" select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                companyId = getQData.get(0).get("BELFIRM").toString();
            }
        }

        try {
            //查詢資料
            int totalRecords = 0;											//总笔数
            int totalPages = 0;												//总页数
            res.setDatas(new DCP_GoodsFeatureQueryRes().new levelElm());
            res.getDatas().setPluList(new ArrayList<>());


            //【ID1032519】【3.0罗森尼娜】盘点优化--服务端  by jinzma 20230414  盘点单单独新增的一个按钮
            {
                level1Elm stockTake = request.getStockTake();
                if (stockTake != null) {
                    String wareHouse = stockTake.getWareHouse();
                    if (!Check.Null(wareHouse)) {
                        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                        sDate = PosPub.GetStringDate(sDate, -365);
                        //获取库存不为零
                        sql = " select pluno from dcp_stock "
                                + " where eid='"+eId+"' and organizationno='"+shopId+"' and warehouse='"+wareHouse+"' and qty<>0 ";
                        List<Map<String, Object>> getQDataPlu1 = this.doQueryData(sql, null);

                        //获取异动指定日期之后
                        sql = " select b.pluno from ("
                                + " select a.pluno,max(b.confirm_date||b.confirm_time) as confirmdate from dcp_stocktake_detail a"
                                + " inner join dcp_stocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno"
                                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and b.warehouse='"+wareHouse+"' and b.status='2' and b.bdate>='"+sDate+"'"
                                + " group by a.pluno"
                                + " )a"
                                + " inner join dcp_stock b on b.eid='"+eId+"' and b.organizationno='"+shopId+"' and a.pluno=b.pluno"
                                + " where nvl(b.lastmoditime,b.createtime)>to_date(a.confirmdate,'yyyymmddhh24miss') and b.qty='0' and b.warehouse='"+wareHouse+"'"
                                + " ";
                        List<Map<String, Object>> getQDataPlu2 = this.doQueryData(sql, null);

                        //资料合并
                        List<Map<String, Object>> getQDataPlu = new ArrayList<>(CollectionUtils.union(getQDataPlu1,getQDataPlu2));

                        //获取异动指定日期之后（日期!=当天的才查历史流水）
                        /*if(!stockDate.equals(sDate)){
                            sql = " select pluno from dcp_stock_detail_static "
                                    + " where eid='"+eId+"' and organizationno='"+shopId+"' and warehouse='"+wareHouse+"' and createtime>to_date('"+stockDate+" "+stockTime+"','yyyymmddhh24miss') ";
                            List<Map<String, Object>> getQDataPlu3 = this.doQueryData(sql, null);
                            getQDataPlu = new ArrayList<>(CollectionUtils.union(getQDataPlu,getQDataPlu3));
                        }*/

                        //同PLUNO合并
                        getQDataPlu = getQDataPlu.stream().distinct().collect(Collectors.toList());

                        if (CollectionUtils.isEmpty(getQDataPlu)){
                            res.setPageNumber(req.getPageNumber());
                            res.setPageSize(req.getPageSize());
                            res.setTotalRecords(totalRecords);
                            res.setTotalPages(totalPages);
                            return res;
                        }

                        String[] plu  = new String[getQDataPlu.size()];
                        int i = 0;
                        for (Map<String, Object>onePlu:getQDataPlu){
                            plu[i] = onePlu.get("PLUNO").toString();
                            i++;
                        }

                        request.setPluNo(plu);

                    }
                }
            }

            //【ID1033123】【标准产品3.0】商品标签及商城副标题展示---中台服务   by jinzma 20230530
            {
                //查商品标签，优先级最高
                if (tags !=null && tags.length>0 ){

                    String tagNos = getString(tags);
                    sql = " select id from dcp_tagtype_detail where eid='"+eId+"' and taggrouptype='GOODS' and tagno in ("+tagNos+") ";

                    List<Map<String, Object>> getTags = this.doQueryData(sql, null);
                    if (!CollectionUtils.isEmpty(getTags)){
                        String[] plus = new String[getTags.size()];
                        int i = 0;
                        for (Map<String, Object> tag:getTags){
                            plus[i] = tag.get("ID").toString();
                            i++;
                        }
                        req.getRequest().setPluNo(plus);
                        req.getRequest().setKeyTxt("");
                    }else{
                        res.setPageNumber(req.getPageNumber());
                        res.setPageSize(req.getPageSize());
                        res.setTotalRecords(totalRecords);
                        res.setTotalPages(totalPages);
                        return res;
                    }
                }

                //keyTxt 单独查询，结果直接塞入pluNo
                if (!Check.Null(keyTxt)) {
                    sql = " select distinct a.pluno from dcp_goods a"
                            + " left join dcp_goods_barcode b on a.eid=b.eid and b.status='100' and a.pluno=b.pluno and lower(b.plubarcode)=lower('"+keyTxt+"')"
                            + " left join dcp_goods_lang c on a.eid=c.eid and a.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' and lower(c.plu_name) like lower('%"+keyTxt+"%')"
                            + " left join dcp_goods_unit_lang d on a.eid=d.eid and a.pluno=d.pluno and d.lang_type='"+req.getLangType()+"' and lower(d.spec) like lower('%"+keyTxt+"%')"
                            + " left join (select a.id from dcp_tagtype_detail a"
                            + "     inner join dcp_tagtype_lang b on a.eid=b.eid and a.taggrouptype=b.taggrouptype and a.tagno=b.tagno and b.lang_type='"+req.getLangType()+"'"
                            + "     where a.eid='"+eId+"' and a.taggrouptype='GOODS' and b.tagname like '%"+keyTxt+"%' "
                            + "  )e on a.pluno=e.id"
                            + " where a.eid='"+eId+"' "
                            + " and ( "
                            + " a.pluno=b.pluno"
                            + " or lower(a.pluno) like lower('%"+keyTxt+"%') "
                            + " or a.pluno=c.pluno"
                            + " or a.pluno=d.pluno"
                            + " or lower(a.shortcut_code) like lower('%"+keyTxt+"%')"
                            + " or a.pluno=e.id "
                            + " )";
                    List<Map<String, Object>> getKeyTxt = this.doQueryData(sql, null);
                    if (!CollectionUtils.isEmpty(getKeyTxt)){
                        String[] plus = new String[getKeyTxt.size()];
                        int i = 0;
                        for (Map<String, Object> key:getKeyTxt){
                            plus[i] = key.get("PLUNO").toString();
                            i++;
                        }
                        req.getRequest().setPluNo(plus);
                        req.getRequest().setKeyTxt("");
                    }else{
                        res.setPageNumber(req.getPageNumber());
                        res.setPageSize(req.getPageSize());
                        res.setTotalRecords(totalRecords);
                        res.setTotalPages(totalPages);
                        return res;
                    }

                    //单独查和关联查在157环境测试发现单独查耗时更多
                   /* select pluno from dcp_goods a where a.eid='' and (lower(a.pluno) like lower('%%') or lower(a.shortcut_code) like lower('%%'));
                    select pluno from dcp_goods_barcode a where a.eid='' and a.status='100' and lower(a.plubarcode)=lower('');
                    select pluno from dcp_goods_lang a where a.eid='' and a.lang_type='zh_CN' and lower(a.plu_name) like lower('%%');
                    select pluno from dcp_goods_unit_lang a where a.eid='' and a.lang_type='zh_CN' and lower(a.spec) like lower('%%');
                    select a.id from dcp_tagtype_detail a
                    inner join dcp_tagtype_lang b on a.eid=b.eid and a.taggrouptype=b.taggrouptype and a.tagno=b.tagno and b.lang_type='zh_CN'
                    where a.eid='' and a.taggrouptype='GOODS'  and b.tagname like '%%'*/

                }

            }


            // 退货单默认供货组织查询 , bringBackReceiptOrg == "Y"  BY JZMA 20200611
            if (bringBackReceiptOrg.equals("Y")) {
                if (Check.Null(reqSupplierId)) {
                    sql = " select receipt_org,c.org_name from dcp_ptemplate a "
                            + " left join dcp_ptemplate_shop b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type "
                            + " left join dcp_org_lang c on a.eid=c.eid and a.receipt_org=c.organizationno and c.lang_type='"+langType+"' "
                            + " inner join dcp_org d on a.eid=d.eid and a.receipt_org=d.organizationno and d.org_form='0' "
                            + " where a.eid='"+eId+"' and  a.doc_type='0' and  (b.shopid='"+shopId+"' or a.shoptype='1') "
                            + " and c.org_name is not null  "
                            + " order by a.tran_time desc " ;
                    List<Map<String, Object>> getQDataReceiptOrg = this.doQueryData(sql, null);
                    if (getQDataReceiptOrg != null && !getQDataReceiptOrg.isEmpty()) {
                        defReceiptOrg = getQDataReceiptOrg.get(0).get("RECEIPT_ORG").toString();
                        defReceiptOrgName = getQDataReceiptOrg.get(0).get("ORG_NAME").toString();
                        reqSupplierId = defReceiptOrg ;
                    }else {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "默认组织查询失败，请手工选择收货组织 ");
                    }
                }
            }

            ///是否条码判断并处理  by jinzma 20210326
            ///【ID1016366】【货郎】移动门店 库存盘点界面，客户反应商品加载慢
            if (!Check.Null(keyTxt) && PosPub.isNumeric(keyTxt) && keyTxt.length()==13) {
                sql = " select pluno,plubarcode from dcp_goods_barcode"
                        + " where eid='"+eId+"' and status='100' and lower(plubarcode)=lower('"+keyTxt+"')";
                List<Map<String, Object>> getBarcode = this.doQueryData(sql, null);
                if (getBarcode != null && !getBarcode.isEmpty()) {
                    //【ID1028434】【货郎3.0.16】当条码在其他门店存在自建品号时，移动门店要货搜索就不到这个商品了，
                    // 门店没办法通过条码找到商品，向总部要货，很不方便  by jinzma 20220915
                    String[] plus = new String[getBarcode.size()];
                    int i = 0;
                    for (Map<String, Object> barcode:getBarcode){
                        plus[i] = barcode.get("PLUNO").toString();
                        i++;
                    }
                    req.getRequest().setPluNo(plus);
                    req.getRequest().setKeyTxt("");
                }
            }

            //sql = fun_goodlist_dcp_no_price(req,companyId,reqSupplierId,CheckSuppGoods,IsStockMultipleUnit);
            //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"商品SQL:"+sql+"\r\n");
            //List<Map<String, Object>> getQData = this.doQueryData(sql, null);

            //处理==绑定变量SQL的写法  以下注释，万一有客户硬解析慢再启用 20221012 by jinzma
            List<DataValue> lstDV = new ArrayList<>();
            sql = fun_goodlist_dcp_no_price(req,companyId,reqSupplierId,CheckSuppGoods,IsStockMultipleUnit,lstDV);
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"商品SQL:"+sql+"\r\n");
            List<Map<String, Object>> getQData = this.executeQuerySQL_BindSQL(sql,lstDV);

            if (getQData != null && !getQData.isEmpty()) {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                /* 有資料，取得詳細內容 //要货单或退货单且供应商未从模板上取到值，取商品默认的供货组织
                if ((billType.equals("0")||billType.equals("7")) && Check.Null(reqSupplierId)) {
                    reqSupplierId = getQData.get(0).get("SUPPLIERID").toString();
                }*/

                //商品取价计算
                Map<String, Boolean> getPriceCondition = new HashMap<>(); //查詢條件
                getPriceCondition.put("PLUNO", true);
                getPriceCondition.put("PUNIT", true);
                getPriceCondition.put("BASEUNIT", true);

                //调用过滤函数
                List<Map<String, Object>> getPriceQHeader=MapDistinct.getMap(getQData,getPriceCondition);
                List<Map<String, Object>> yhList=new ArrayList<>();
                if("0".equals(billType)){
                    String yhSql="select c.pluno,a.RECEIPT_ORG,a.ptemplateno " +
                            " from DCP_PTEMPLATE a" +
                            " left join DCP_PTEMPLATE_SHOP b on a.eid=b.eid and a.ptemplateno=b.ptemplateno " +
                            " left join DCP_PTEMPLATE_DETAIL c on c.eid=a.eid and c.ptemplateno=a.ptemplateno " +
                            " where a.eid='"+eId+"' and a.DOC_TYPE='0' and a.status='100' and c.status='100'";
                    yhSql+=" and (a.SHOPTYPE='1' or (b.SHOPID='"+req.getOrganizationNO()+"' and b.status='100')) ";
                    if(Check.NotNull(req.getRequest().getTemplateNo())){
                        yhSql+= " and a.ptemplateno='"+req.getRequest().getTemplateNo()+"'";
                    }

                    yhList = this.doQueryData(yhSql, null);

                    List<Map<String, Object>> finalYhList = yhList;
                    getPriceQHeader.forEach(x->{
                        String pluno = x.get("PLUNO").toString();
                        if(finalYhList.size()>0){
                            List<Map<String, Object>> filterRows = finalYhList.stream().filter(y -> y.get("PLUNO").toString().equals(pluno)).collect(Collectors.toList());
                            if(filterRows.size()>0){
                                x.put("SUPPLIERID", filterRows.get(0).get("RECEIPT_ORG").toString());
                            }else{
                                x.put("SUPPLIERID", "");
                            }
                        }else{
                            x.put("SUPPLIERID", "");
                        }
                    });
                }
                MyCommon mc = new MyCommon();
                List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getPriceQHeader,companyId);
                /*
                 if(billType.equals("3") && !Check.Null(reqSupplierId)) //采购模板
                 {
                 	getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getQData, 2, reqSupplierId);
                 }
                 else if((billType.equals("0") || billType.equals("7")) && !Check.Null(reqSupplierId))  //要货模板或退货
                 {
                 	getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getQData, 3, reqSupplierId); ////0-无 ，1-公司，2-供应商  ，3-公司或供应商
                 }
                 else if (orgForm.equals("2"))  ///组织类型 0-公司  1-组织  2-门店 3-其它
                 {
                 	//getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getQData, 0, "");
                 	getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getQData, 1, companyId);
                 }
                */



                DCP_GoodsFeatureQueryRes.levelElm oneLv = new DCP_GoodsFeatureQueryRes().new levelElm();
                oneLv.setReceiptOrg(defReceiptOrg);
                oneLv.setReceiptOrgName(defReceiptOrgName);

                //单头主键字段
                Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                condition.put("PLUNO", true);
                //condition.put("FEATURENO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData,condition);
                oneLv.setPluList(new ArrayList<>());
                for (Map<String, Object> oneData : getQHeader) {
                    DCP_GoodsFeatureQueryRes.level1Elm oneLv1 = new DCP_GoodsFeatureQueryRes().new level1Elm();
                    // 取得第一層資料庫搜尋結果
                    String pluNo= oneData.get("PLUNO").toString();
                    String pluName= oneData.get("PLU_NAME").toString();
                    // 增加商品图 BY WANGZY 2020-12-8
                    String listImage = oneData.get("LISTIMAGE").toString();
                    String pluType= oneData.get("PLUTYPE").toString();
                    String spec= oneData.get("SPEC").toString();
                    String category= oneData.get("CATEGORY").toString();
                    String warmType= oneData.get("WARMTYPE").toString();
                    String virtual= oneData.get("VIRTUAL").toString();
                    String stockManageType= oneData.get("STOCKMANAGETYPE").toString();
                    String memo= oneData.get("MEMO").toString();
                    String status = oneData.get("STATUS").toString();

                    String unit= oneData.get("PUNIT").toString();
                    String unitName= oneData.get("UNAME").toString();
                    String unitRatio= oneData.get("UNITRATIO").toString();
                    String unitUdLength= oneData.get("UDLENGTH").toString();

                    //【ID1018102】【3.0货郎】门店自采获取外部供应商采购价(服务端) by jinzma 20210616
                    String suptemplatePluno = oneData.get("SUPTEMPLATE_PLUNO").toString();
                    String suptemplatePrice = oneData.get("SUPTEMPLATE_PRICE").toString();
                    String suptemplatePunit = oneData.get("SUPTEMPLATE_PUNIT").toString();
                    String suptemplateUDlength = oneData.get("SUPTEMPLATE_UDLENGTH").toString();
                    String suptemplateUnitratio = oneData.get("SUPTEMPLATE_UNITRATIO").toString();
                    String suptemplatePunitname = oneData.get("SUPTEMPLATE_PUNITNAME").toString();
                    if (!Check.Null(suptemplatePluno)){
                        unit = suptemplatePunit;
                        unitName = suptemplatePunitname;
                        unitRatio = suptemplateUnitratio;
                        unitUdLength = suptemplateUDlength;
                    }

                    String weight= oneData.get("WEIGHT").toString();
                    String volume= oneData.get("VOLUME").toString();
                    String isBatch= oneData.get("ISBATCH").toString();
                    String shelfLife= oneData.get("SHELFLIFE").toString();
                    String stockInValidDay= oneData.get("STOCKINVALIDDAY").toString();
                    String stockOutValidDay= oneData.get("STOCKOUTVALIDDAY").toString();
                    String checkValidDay= oneData.get("CHECKVALIDDAY").toString();
                    //String supplierType= oneData.get("SUPPLIERTYPE").toString();
                    //String receiptOrg= oneData.get("RECEIPTORG").toString();
                    //String supplierId= oneData.get("SUPPLIERID").toString();
                    String safeQty= oneData.get("SAFEQTY").toString();
                    String canPurchase= oneData.get("CANPURCHASE").toString();
                    String taxCode= oneData.get("TAXCODE").toString();
                    String canRequire= oneData.get("CANREQUIRE").toString();
                    String minQty= oneData.get("MINQTY").toString();
                    String maxQty= oneData.get("MAXQTY").toString();
                    String mulQty= oneData.get("MULQTY").toString();
                    String canRequireBack= oneData.get("CANREQUIREBACK").toString();
                    String isAutoSubtract= oneData.get("IS_AUTO_SUBTRACT").toString();
                    String canEstimate= oneData.get("CANESTIMATE").toString();
                    String clearType= oneData.get("CLEARTYPE").toString();
                    String categoryName= oneData.get("CATEGORY_NAME").toString();
                    String warningQty = oneData.get("WARNINGQTY").toString();
                    String baseUnit = oneData.get("BASEUNIT").toString();
                    String baseUnitName  = oneData.get("BASEUNITNAME").toString();
                    String isHoliday =  oneData.get("ISHOLIDAY").toString();
                    //【ID1018829】商品查询服务-新增爆品 by jinzma 20210702
                    String isHotGoods = oneData.get("ISHOTGOODS").toString();
                    String mainBarCode = oneData.get("MAINBARCODE").toString();
                    if (Check.Null(isHotGoods)){
                        isHotGoods="N";
                    }
                    //【ID1018735】【3.0货郎】移动门店新增新品查看和筛选功能 by jinzma 20210702
                    String isNewGoods = oneData.get("ISNEWGOODS").toString();
                    if (Check.Null(isNewGoods)){
                        isNewGoods="N";
                    }
                    BigDecimal totHistoryStockQty_b = new BigDecimal("0"); //历史已盘点总数量（feature节点的数量汇总）

                    condition.clear();
                    condition.put("PLUNO", true);
                    condition.put("FEATURENO", true);
                    //调用过滤函数
                    List<Map<String, Object>> getQFeature=MapDistinct.getMap(getQData,condition);
                    oneLv1.setFeature(new ArrayList<>());
                    for (Map<String, Object> oneDataDetail : getQFeature) {
                        String featureNo= oneDataDetail.get("FEATURENO").toString();
                        if(pluNo.equals(oneDataDetail.get("PLUNO")) && !Check.Null(featureNo)) {
                            DCP_GoodsFeatureQueryRes.level2Elm oneLv2 = new DCP_GoodsFeatureQueryRes().new level2Elm();
                            String featureName= oneDataDetail.get("FEATURENAME").toString();
                            String attrId1= oneDataDetail.get("ATTRID1").toString();
                            String attrId1Name= oneDataDetail.get("ATTRVALUEID1").toString();
                            String attrValueId1= oneDataDetail.get("ATTR1NAME").toString();
                            String attrValueId1Name= oneDataDetail.get("AVALUE1NAME").toString();
                            String attrId2= oneDataDetail.get("ATTRID2").toString();
                            String attrId2Name= oneDataDetail.get("ATTRVALUEID2").toString();
                            String attrValueId2= oneDataDetail.get("ATTR2NAME").toString();
                            String attrValueId2Name= oneDataDetail.get("AVALUE2NAME").toString();
                            String attrId3= oneDataDetail.get("ATTRID3").toString();
                            String attrId3Name= oneDataDetail.get("ATTRVALUEID3").toString();
                            String attrValueId3= oneDataDetail.get("ATTR3NAME").toString();
                            String attrValueId3Name= oneDataDetail.get("AVALUE3NAME").toString();
                            String historyStockQty="0";
                            //12-盘点子任务单
                            if (billType.equals("12")){
								/*String subPunit=oneDataDetail.get("SUB_PUNIT").toString();
								String subPqty=oneDataDetail.get("SUB_PQTY").toString();
								String subUnitRatio=oneDataDetail.get("SUB_UNITRATIO").toString();

								if (Check.Null(subPqty) || Check.Null(subUnitRatio)){
									historyStockQty="0";
								}else{
									if (subPunit.equals(unit)){
										historyStockQty = subPqty;
									}else{
										if (Check.Null(unitRatio)||unitRatio.equals("0")){
											historyStockQty="0";
										}else {
											BigDecimal subPqty_b = new BigDecimal(subPqty);
											BigDecimal subUnitRatio_b = new BigDecimal(subUnitRatio);
											BigDecimal unitRatio_b = new BigDecimal(unitRatio);

											if (!PosPub.isNumeric(unitUdLength)) {
												unitUdLength="6";
											}

											BigDecimal historyStockQty_b = subPqty_b.multiply(subUnitRatio_b.divide(unitRatio_b,6,BigDecimal.ROUND_HALF_UP));
											historyStockQty_b = historyStockQty_b.setScale(Integer.parseInt(unitUdLength),BigDecimal.ROUND_HALF_UP );
											historyStockQty = historyStockQty_b.toPlainString();
										}
									}
								}*/

                                //【ID1016322】【货郎3.0】多PDA盘点-优化项 BY jinzma 20210324
                                //String subBaseUnit =oneDataDetail.get("SUB_BASEUNIT").toString();
                                historyStockQty =oneDataDetail.get("SUB_BASEQTY").toString();
                                if (Check.Null(historyStockQty)){
                                    historyStockQty="0";
                                }
                                totHistoryStockQty_b = totHistoryStockQty_b.add(new BigDecimal(historyStockQty));
                            }

                            oneLv2.setFeatureNo(featureNo);
                            oneLv2.setFeatureName(featureName);
                            oneLv2.setAttrId1(attrId1);
                            oneLv2.setAttrId1Name(attrId1Name);
                            oneLv2.setAttrId2(attrId2);
                            oneLv2.setAttrId2Name(attrId2Name);
                            oneLv2.setAttrId3(attrId3);
                            oneLv2.setAttrId3Name(attrId3Name);
                            oneLv2.setAttrValueId1(attrValueId1);
                            oneLv2.setAttrValueId1Name(attrValueId1Name);
                            oneLv2.setAttrValueId2(attrValueId2);
                            oneLv2.setAttrValueId2Name(attrValueId2Name);
                            oneLv2.setAttrValueId3(attrValueId3);
                            oneLv2.setAttrValueId3Name(attrValueId3Name);
                            oneLv2.setHistoryStockQty(historyStockQty);
                            oneLv2.setLen(oneDataDetail.getOrDefault("LEN", "").toString());
                            oneLv2.setWidth(oneDataDetail.getOrDefault("WIDTH", "").toString());
                            oneLv2.setHeight(oneDataDetail.getOrDefault("HEIGHT", "").toString());
                            oneLv2.setVolumeUnit(oneDataDetail.getOrDefault("VOLUMEUNIT_BARCODE", "").toString());
                            oneLv2.setVolumeUnitName(oneDataDetail.getOrDefault("VOLUMEUNITNAME_BARCODE", "").toString());
                            oneLv2.setWeight(oneDataDetail.getOrDefault("WEIGHT_BARCODE", "").toString());
                            oneLv2.setWeightUnit(oneDataDetail.getOrDefault("WEIGHTUNIT_BARCODE", "").toString());
                            oneLv2.setWeightUnitName(oneDataDetail.getOrDefault("WEIGHTUNITNAME_BARCODE", "").toString());
                            oneLv2.setStockqty("999999");
                            oneLv1.getFeature().add(oneLv2);
                        }
                    }

                    //9-盘点计划单-支持多单位盘点
                    oneLv1.setUnitList(new ArrayList<>());
                    if (billType.equals("9") && IsStockMultipleUnit.equals("Y")) {
                        condition.clear();
                        condition.put("PLUNO", true);
                        condition.put("UNITLIST_UNIT", true);
                        //调用过滤函数 (存在多个特征码此处需要过滤)
                        List<Map<String, Object>> getQUnitlist=MapDistinct.getMap(getQData,condition);
                        int i=0;
                        for (Map<String, Object> oneDataUnit : getQUnitlist) {
                            String unitList_unit= oneDataUnit.get("UNITLIST_UNIT").toString();
                            if(pluNo.equals(oneDataUnit.get("PLUNO").toString()) && !Check.Null(unitList_unit)) {
                                if (i==0){
                                    DCP_GoodsFeatureQueryRes.level2ElmUnit oneLv2 = new DCP_GoodsFeatureQueryRes().new level2ElmUnit();
                                    oneLv2.setUnit(unit);
                                    oneLv2.setUnitName(unitName);
                                    oneLv2.setUnitRatio(unitRatio);
                                    oneLv2.setUnitUdLength(unitUdLength);
                                    oneLv1.getUnitList().add(oneLv2);
                                    i=1;
                                }

                                if (!unitList_unit.equals(unit)){
                                    DCP_GoodsFeatureQueryRes.level2ElmUnit oneLv2 = new DCP_GoodsFeatureQueryRes().new level2ElmUnit();
                                    oneLv2.setUnit(unitList_unit);
                                    oneLv2.setUnitName(oneDataUnit.get("UNITLIST_UNITNAME").toString());
                                    oneLv2.setUnitRatio(oneDataUnit.get("UNITLIST_UNITRATIO").toString());
                                    oneLv2.setUnitUdLength(oneDataUnit.get("UNITLIST_UNITUDLENGTH").toString());
                                    oneLv1.getUnitList().add(oneLv2);
                                }
                            }
                        }
                    }

                    //12-盘点子任务单
                    if (billType.equals("12")){
                        if (totHistoryStockQty_b.compareTo(BigDecimal.ZERO)==0) {
							/*String subPunit=oneData.get("SUB_PUNIT").toString();
							String subPqty=oneData.get("SUB_PQTY").toString();
							String subUnitRatio=oneData.get("SUB_UNITRATIO").toString();

							if (Check.Null(subPqty) || Check.Null(subUnitRatio)){
								totHistoryStockQty_b = new BigDecimal("0");
							}else {
								if (subPunit.equals(unit)) {
									totHistoryStockQty_b = new BigDecimal(subPqty);
								} else {
									if (Check.Null(unitRatio) || unitRatio.equals("0")) {
										totHistoryStockQty_b = new BigDecimal("0");
									} else {
										BigDecimal subPqty_b = new BigDecimal(subPqty);
										BigDecimal subUnitRatio_b = new BigDecimal(subUnitRatio);
										BigDecimal unitRatio_b = new BigDecimal(unitRatio);
										if (!PosPub.isNumeric(unitUdLength)) {
											unitUdLength = "6";
										}
										BigDecimal historyStockQty_b = subPqty_b.multiply(subUnitRatio_b.divide(unitRatio_b, 6, BigDecimal.ROUND_HALF_UP));
										totHistoryStockQty_b = historyStockQty_b.setScale(Integer.parseInt(unitUdLength), BigDecimal.ROUND_HALF_UP);
									}
								}
							}*/

                            //【ID1016322】【货郎3.0】多PDA盘点-优化项 BY jinzma 20210324
                            String historyStockQty = oneData.get("SUB_BASEQTY").toString();
                            if (Check.Null(historyStockQty)){
                                historyStockQty="0";
                            }
                            totHistoryStockQty_b = new BigDecimal(historyStockQty);
                        }
                    }

                    //商品取价
                    Map<String, Object> condiV= new HashMap<>();
                    condiV.put("PLUNO",pluNo);
                    condiV.put("PUNIT",unit);
                    List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPrice, condiV, false);
                    condiV.clear();
                    String price="0";
                    String distriPrice="0";
                    if(priceList!=null && priceList.size()>0 ) {
                        price=priceList.get(0).get("PRICE").toString();
                        distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                    }

                    //【ID1018102】【3.0货郎】门店自采获取外部供应商采购价(服务端) by jinzma 202010616
                    if (!Check.Null(purchaseType) && billType.equals("3")) {
                        distriPrice="0";
                        if (!Check.Null(suptemplatePluno)) {
                            distriPrice = suptemplatePrice;
                            //零售价转换处理
                            if (!suptemplatePunit.equals(oneData.get("PUNIT").toString())) {
                                if (PosPub.isNumericType(price) && PosPub.isNumericType(unitRatio) && PosPub.isNumericType(suptemplateUnitratio)) {
                                    BigDecimal price_b = new BigDecimal(price);
                                    BigDecimal unitRatio_b = new BigDecimal(unitRatio);
                                    BigDecimal suptemplateUnitratio_b = new BigDecimal(suptemplateUnitratio);
                                    price_b = price_b.divide(unitRatio_b,6, RoundingMode.HALF_UP).multiply(suptemplateUnitratio_b);
                                    price_b = price_b.setScale(2, RoundingMode.HALF_UP);
                                    price = price_b.toPlainString();
                                }else{
                                    price="0";
                                }
                            }
                        }
                    }

                    oneLv1.setCanEstimate(canEstimate);
                    oneLv1.setCanPurchase(canPurchase);
                    oneLv1.setCanRequire(canRequire);
                    oneLv1.setCanRequireBack(canRequireBack);
                    oneLv1.setCategory(category);
                    oneLv1.setCategoryName(categoryName);
                    oneLv1.setCheckValidDay(checkValidDay);
                    oneLv1.setClearType(clearType);
                    oneLv1.setDistriPrice(distriPrice);
                    oneLv1.setIsAutoSubtract(isAutoSubtract);
                    oneLv1.setIsBatch(isBatch);
                    oneLv1.setMaxQty(maxQty);
                    oneLv1.setMemo(memo);
                    oneLv1.setMinQty(minQty);
                    oneLv1.setMulQty(mulQty);
                    oneLv1.setPluName(pluName);
                    if(!Check.Null(listImage)){
                        oneLv1.setListImage(DomainName+listImage);
                    }else{
                        oneLv1.setListImage("");
                    }
                    oneLv1.setPluNo(pluNo);
                    oneLv1.setPluType(pluType);
                    oneLv1.setPrice(price);
                    oneLv1.setSafeQty(safeQty);
                    oneLv1.setShelfLife(shelfLife);
                    oneLv1.setSpec(spec);
                    oneLv1.setMainBarCode(mainBarCode);
                    oneLv1.setStatus(status);
                    oneLv1.setStockInValidDay(stockInValidDay);
                    oneLv1.setStockManageType(stockManageType);
                    oneLv1.setStockOutValidDay(stockOutValidDay);
                    oneLv1.setTaxCode(taxCode);
                    oneLv1.setUnit(unit);
                    oneLv1.setUnitName(unitName);
                    oneLv1.setUnitRatio(unitRatio);
                    oneLv1.setUnitUdLength(unitUdLength);
                    oneLv1.setVirtual(virtual);
                    oneLv1.setVolume(volume);
                    oneLv1.setWarmType(warmType);
                    oneLv1.setWarningQty(warningQty);
                    oneLv1.setWeight(weight);
                    oneLv1.setBaseUnit(baseUnit);
                    oneLv1.setBaseUnitName(baseUnitName);
                    oneLv1.setIsHoliday(isHoliday);
                    oneLv1.setHistoryStockQty(totHistoryStockQty_b.toPlainString());
                    oneLv1.setMaxOrderSpec(oneData.get("MAXORDERSPEC").toString());
                    oneLv1.setIsHotGoods(isHotGoods);
                    oneLv1.setIsNewGoods(isNewGoods);
                    oneLv1.setLen(oneData.getOrDefault("LEN", "").toString());
                    oneLv1.setWidth(oneData.getOrDefault("WIDTH", "").toString());
                    oneLv1.setHeight(oneData.getOrDefault("HEIGHT", "").toString());
                    oneLv1.setVolumeUnit(oneData.getOrDefault("VOLUMEUNIT_BARCODE", "").toString());
                    oneLv1.setVolumeUnitName(oneData.getOrDefault("VOLUMEUNITNAME_BARCODE", "").toString());
                    oneLv1.setWeight(oneData.getOrDefault("WEIGHT_BARCODE", "").toString());
                    oneLv1.setWeightUnit(oneData.getOrDefault("WEIGHTUNIT_BARCODE", "").toString());
                    oneLv1.setWeightUnitName(oneData.getOrDefault("WEIGHTUNITNAME_BARCODE", "").toString());
                    oneLv1.setSelfBuiltShopId(oneData.get("SELFBUILTSHOPID").toString());
                    oneLv1.setStockqty("999999");
                    oneLv1.setMainSupplierId(oneData.get("MAINSUPPLIERID").toString());
                    oneLv1.setMainSupplierName(oneData.get("MAINSUPPLIERNAME").toString());
                    oneLv1.setMainSupplierAbbr(oneData.get("MAINSUPPLIERABBR").toString());
                    oneLv1.setStandardPrice(oneData.get("STANDARDPRICE").toString());
                    //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221110
                    oneLv1.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                    //【ID1033473】【货郎3.3.0.1】移动门店 商品 菜单 增加总部库存排序-服务端 by jinzma 20230606
                    oneLv1.setHeadStockQty(oneData.get("HEADSTOCKQTY").toString());
                    oneLv1.setPickUnit(oneData.get("PICKUNIT").toString());
                    oneLv1.setPickUName(oneData.get("PICKUNAME").toString());
                    oneLv.getPluList().add(oneLv1);
                }

                res.setDatas(oneLv);
            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);


            //处理库存量
            if (queryStockqty!=null && queryStockqty.equals("Y"))
            {
                //
                JSONArray pluList=new JSONArray();
                for (DCP_GoodsFeatureQueryRes.level1Elm p1 : res.getDatas().getPluList())
                {
                    if (p1.getStockManageType()!=null && (p1.getStockManageType().equals("2") || p1.getStockManageType().equals("3")))
                    {
                        //改成0
                        p1.setStockqty("0");

                        if (p1.getFeature() != null && p1.getFeature().size()>0)
                        {
                            //有特征码
                            for (DCP_GoodsFeatureQueryRes.level2Elm f1 : p1.getFeature())
                            {
                                JSONObject plu=new JSONObject();
                                plu.put("pluNo",p1.getPluNo());
                                plu.put("featureNo",f1.getFeatureNo());
                                pluList.put(plu);
                            }
                        }
                        else
                        {
                            //无特征码
                            JSONObject plu=new JSONObject();
                            plu.put("pluNo",p1.getPluNo());
                            plu.put("featureNo","");
                            pluList.put(plu);
                        }
                    }
                }
                if (pluList != null && pluList.length()>0)
                {
                    JSONObject req_SOLD = new JSONObject();
                    req_SOLD.put("serviceId","DCP_GoodsStockQuery");
                    req_SOLD.put("token", req.getToken());
                    // //JOB调订转销用到，没token
                    req_SOLD.put("eId",eId);
                    req_SOLD.put("eShop",shopId);

                    //
                    JSONObject request_SOLD = new JSONObject();
                    request_SOLD.put("eId", eId);
                    request_SOLD.put("queryOrgId", shopId);
                    request_SOLD.put("queryType", "DCP");

                    //【ID1034198】【罗森尼娜3.0】移仓单、调拨出库单等单据的库存量计算错误  by jinzma 20230621
                    if (!Check.Null(queryStockWarehouse)){
                        request_SOLD.put("warehouse",queryStockWarehouse);
                    }
                    request_SOLD.put("pluList", pluList);
                    req_SOLD.put("request", request_SOLD);

                    String str_SOLD = req_SOLD.toString();// 将json对象转换为字符串

                    //内部调内部
                    DispatchService ds = DispatchService.getInstance();
                    String resbody_SOLD = ds.callService(str_SOLD, this.dao);

                    if (!resbody_SOLD.equals(""))
                    {
                        JSONObject jsonres_SOLD = new JSONObject(resbody_SOLD);
                        boolean success = jsonres_SOLD.getBoolean("success");
                        if (success)
                        {
                            JSONObject datas_SOLD=jsonres_SOLD.optJSONObject("datas");
                            if (datas_SOLD != null) {
                                JSONArray res_pluList = datas_SOLD.optJSONArray("pluList");
                                if (res_pluList != null && res_pluList.length() > 0) {
                                    for (int ri = 0; ri < res_pluList.length(); ri++) {
                                        String r_pluno = res_pluList.getJSONObject(ri).get("pluNo").toString();
                                        String r_baseunit = res_pluList.getJSONObject(ri).get("baseUnit").toString();
                                        String r_baseqty = res_pluList.getJSONObject(ri).get("baseQty").toString();
                                        //汇总特征码库存设置
                                        List<DCP_GoodsFeatureQueryRes.level1Elm> goods = res.getDatas().getPluList().stream().filter(c -> c.getPluNo().equals(r_pluno)).collect(Collectors.toList());
                                        goods.stream().forEach(d -> {
                                            d.setStockqty(r_baseqty);
                                        });
                                        //特征码库量存设置
                                        JSONArray res_featureList = res_pluList.getJSONObject(ri).getJSONArray("featureList");
                                        if (res_featureList != null && res_featureList.length() > 0) {
                                            for (int si = 0; si < res_featureList.length(); si++) {
                                                String r_featureNo = res_featureList.getJSONObject(si).get("featureNo").toString();
                                                String r_baseQty = res_featureList.getJSONObject(si).get("baseQty").toString();
                                                goods.stream().forEach(d -> {
                                                    d.getFeature().stream().filter(d1 -> d1.getFeatureNo().equals(r_featureNo)).forEach(d2 -> d2.setStockqty(r_baseQty));
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }



            return res;
        }catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_GoodsFeatureQueryReq req) throws Exception {
        return null;
    }

    private String getString(String[] str) {
        StringBuffer str2 = new StringBuffer();
        if (str!=null && str.length>0) {
            for (String s:str) {
                str2.append("'").append(s).append("'").append(",");
            }
            if (str2.length()>0) {
                str2 = new StringBuffer(str2.substring(0, str2.length() - 1));
            }
        }
        return str2.toString();
    }

    private String fun_goodlist_dcp_no_price(DCP_GoodsFeatureQueryReq req,String companyId,String supplierId,String CheckSuppGoods,String IsStockMultipleUnit, List<DataValue> lstDV)throws Exception{
        String eId = req.geteId();
        String shopId = req.getShopId();
        String orgForm = req.getOrg_Form();   ////组织类型 0-公司  1-组织  2-门店 3-其它
        String langType = req.getLangType();
        levelElm request = req.getRequest();
        //查詢條件
        String keyTxt = request.getKeyTxt();
        String status = request.getStatus();
        String virtual = request.getVirtual();
        String isBatch = request.getIsBatch();
        String[] brand = request.getBrand();
        String[] series = request.getSeries();
        String[] attrGroup = request.getAttrGroup();
        String[] attrValue = request.getAttrValue();
        String isHotGoods = request.getIsHotGoods();   //爆品
        String isNewGoods = request.getIsNewGoods();   //新品
        String pluType = request.getPluType();  // 普通商品NORMAL 特征商品FEATURE 套餐商品 PACKAGE
        //【ID1018538】【3.0货郎】商品分类查询/商品列表查询服务响应提速优化 by jinzma 20210628  in ---> 关联表
        String[] plu  = request.getPluNo();
        String withPlu = "";
        if (plu !=null && plu.length>0 ) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPlu = "";
            for(String s :plu) {
                ///要货单商品导入 商品编号要小写处理，有客户商品编号带字母
                //sJoinPlu += s.toLowerCase()+",";
                //【ID1033883】【詹记V3306】升级后，部分商品不能调拨，不能拼胚 by jinzma 20230608 去掉小写转换
                sJoinPlu += s +",";
            }
            map.put("PLU", sJoinPlu);
            withPlu = mc.getFormatSourceMultiColWith(map);
        }

        String[] category = request.getCategory();
        String withCategory = "";
        if (category !=null && category.length>0 ) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinCategory = "";
            for(String s :category) {
                sJoinCategory += s+",";
            }
            map.put("CATEGORY", sJoinCategory);
            withCategory = mc.getFormatSourceMultiColWith(map);
        }

        // String categorys = getString(category);
        // String pluNos=getString(pluNo);
        // if (pluNos.length()>0)
        // pluNos=pluNos.toLowerCase();

        //【ID1018539】【3.0货郎】商品查询服务支持条码查询 by jinzma 20210617
        String[] pluBarcode = request.getPluBarcode();  //["pluBarcode1","pluBarcode2"]
        String withPluBarcode = "";
        if (pluBarcode !=null && pluBarcode.length>0 ) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPluBarcode = "";
            for(String s :pluBarcode) {
                sJoinPluBarcode += s+",";
            }
            map.put("PLUBARCODE", sJoinPluBarcode);
            withPluBarcode = mc.getFormatSourceMultiColWith(map);
        }

        String billType =request.getBillType();
        String billNo = request.getBillNo();
        String stockTakeNo = request.getStockTakeNo();
        String templateNo = request.getTemplateNo();
        String templateType = request.getTemplateType();
        String brands = getString(brand);
        String seriess = getString(series);
        String attrGroups = getString(attrGroup);
        String attrValues = getString(attrValue);
        String distriPriceRank = request.getDistriPriceRank();   //标准进货价排序（0从低到高，1从高到低）
        String purchaseType = request.getPurchaseType();         // 采购类型：1 门店自采  3门店直供
        String weekOfDay = this.getWeekDay();
        String day = this.getDay();
        String doubleDay = "1";    //单日
        if(Integer.parseInt(day) % 2==0) {
            doubleDay = "2";//双日
        }
        //searchScope：0、全部 1、总部和当前自建门店 2、仅总部 3、全部自建门店 4、仅当前自建门店  by jinzma 20220310
        String searchScope = request.getSearchScope();
        if (Check.Null(searchScope)){
            searchScope="0";
        }
        String selfBuiltShopId = request.getSelfBuiltShopId(); //自建门店
        //【ID1033473】【货郎3.3.0.1】移动门店 商品 菜单 增加总部库存排序-服务端 by jinzma 20230606
        String isHeadStock = request.getIsHeadStock(); //是否查询总部库存Y/N
        String stockSort = request.getStockSort();     //总部库存排序 0 从小到大  1从大到小
        if (Check.Null(isHeadStock)){
            isHeadStock = "N";
        }
        if (!isHeadStock.equals("Y")){
            stockSort = "";
        }
        
        
		/* 20210602 与红艳沟通确认以下逻辑    by jinzma
           采购单供应商逻辑调整说明
           checkSuppGoods 是否检查供应商
           supplierId     供应商ID
           purchaseType   采购类型
           if 采购类型 == 直供        then 供应商必传且检查采购模板对应该供应商的商品和价格
                                    （采购模板按当前生效门店取最新的采购模板）
           if 采购类型 == 自采
              且供应商检核参数 = Y   then 供应商必传且返回采购模板对应该供应商的商品和价格
              且供应商检核参数 = N   then 供应商必传且返回采购模板对应该供应商的商品和价格或者模板生效外的其他商品但返回价格为零
           注：（采购模板编号 != null then 上述采购模板编号==传入的采购模板编号且商品必须在模板范围内（自采且供应商检核参数=N除外））
		*/


        //計算起啟位置
        int pageSize = req.getPageSize();
        int startRow = (req.getPageNumber() - 1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer();
        //单据类型：0-要货类 2-完工入库/加工模板 3-采购单 4-转换合并 5-组合拆解 6-计划报单 7-退货出库 9-盘点单  10-销售类 11-拼胚 12-盘点子任务单 14调拨出库单
        String unit = "a.baseunit";
        String unitUse="";
        if (!Check.Null(billType)){
            switch (billType) {
                case "0":
                    unit="a.punit";
                    unitUse = " and goodsunit.punit_use='Y' ";
                    break;
                case "7":
                    //【ID1026798】【詹记】退货出库默认单位需要调整---服务端 by jinzma 20220627
                    //unit="a.punit";
                    //【ID1031735】【罗森尼娜3.0】退货出库要求默认为要货单位
//                    unit="a.wunit";
//                    unitUse = "";     //退货出库不判断单位是否可用
                    unit="nvl(a.runit,a.wunit) ";
                    unitUse = " ";
                    break;
                case "2":
                case "5":
                case "4":
                    unit="a.prod_unit";
                    unitUse = " and goodsunit.prod_unit_use='Y' ";
                    break;
                case "3":
                    unit="a.purunit";
                    unitUse = " and goodsunit.purunit_use='Y' ";
                    break;
                case "9":
                case "12":
                    unit="a.cunit";
                    unitUse = " and goodsunit.cunit_use='Y' ";
                    break;
                default:
                    break;
            }
            if (billType.equals("0") && !Check.Null(templateNo)){
                unit="p2.punit";
                unitUse = " ";
            }

        } else {
            billType="-1"; // 单据类型为空得时候，塞个默认值
        }

        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        /*sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("   //+ index(b IDX_DCP_GOODSTEMPLATE_GOODS01)
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

        String tempSql="with goodstemplate as ( SELECT * " +
                " FROM (" +
                "    SELECT a.TEMPLATETYPE,c.*," +
                "       " +
                "        b.RANGETYPE,ROW_NUMBER() OVER (" +
                "            PARTITION BY c.pluno " +
                "            ORDER BY " +
                "                CASE WHEN b.RANGETYPE = 2 THEN 0 ELSE 1 END," +
                "                a.CREATETIME DESC" +
                "        ) AS rn" +
                "    FROM " +
                "        DCP_GOODSTEMPLATE a  " +
                "        JOIN DCP_GOODSTEMPLATE_RANGE b " +
                "            ON a.EID = b.EID AND a.TEMPLATEID = b.TEMPLATEID  " +
                "        INNER JOIN dcp_goodstemplate_goods c " +
                "            ON c.eid = a.eid AND c.TEMPLATEID = a.TEMPLATEID AND c.status = '100' " +
                "    WHERE " +
                "        a.EID = '99' " +
                "        AND b.ID IN ('"+req.getShopId()+"', '"+req.getBELFIRM()+"') " +
                "        AND a.STATUS = 100  " +
                "        AND b.RANGETYPE IN (1, 2)" +
                " )" +
                " WHERE rn = 1 " +
                " ORDER BY pluno ) ";

        sqlbuf.append(tempSql);


        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        //sqlbuf.append(" "
        //        + " with goodstemplate as ("
        //        + " select b.* from dcp_goodstemplate a"
        //        + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
        //        + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"') "
        //        + " )");

        //?问号参数赋值处理
        //lstDV.add(new DataValue(eId, Types.VARCHAR));
        //lstDV.add(new DataValue(shopId, Types.VARCHAR));


        if (!Check.Null(withPlu)) {
            sqlbuf.append(" ,plu as (" + withPlu + ")");
        }
        if (!Check.Null(withCategory)) {
            sqlbuf.append(" ,category as (" + withCategory + ")");
        }
        if (!Check.Null(withPluBarcode)) {
            sqlbuf.append(" ,barcode as (" + withPluBarcode + ")");
        }

        //【ID1018102】 【3.0货郎】门店自采获取外部供应商采购价(服务端)  by jinzma 20210616
        if (!Check.Null(purchaseType) && billType.equals("3")){
            sqlbuf.append(" "
                    + " ,supTemplate as ("
                    + " select b.pluno as supTemplate_pluno,b.price as supTemplate_price,b.punit as supTemplate_punit,"
                    + " c.udlength as supTemplate_udlength,d.unitratio as supTemplate_unitratio,e.uname as supTemplate_punitname"
                    + " from ("
                    + " select a.*,row_number() over (order by a.tran_time desc) as rn from dcp_ptemplate a"
                    + " left join dcp_ptemplate_shop b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.shopid='"+shopId+"'"
                    + " where a.eid='"+eId+"' and a.doc_type='3' and a.status='100' and a.supplier='"+supplierId+"'"
                    + " ");
            if (!Check.Null(templateNo)){
                sqlbuf.append(" and a.ptemplateno='"+templateNo+"'");
            }else{
                sqlbuf.append(" "
                        + " and (a.shoptype='1' or (a.shoptype='2' and b.shopid is not null ))"
                        + " and ((a.time_type='0' ) or (a.time_type='1' and a.time_value like '%" + doubleDay + "%')"
                        + " or (a.time_type='2' and a.time_value like '%" + weekOfDay + "%')"
                        + " or (a.time_type='3' and ';'||a.time_value||';' like '%%;" + Integer.valueOf(day) + ";%%')"
                        + " or (a.time_type='3' and a.time_value like '%%" + day + "%%'))"
                        + " ");
            }
            sqlbuf.append("	)a");
            sqlbuf.append(" "
                    + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type"
                    + " inner join dcp_unit c on a.eid=c.eid and b.punit=c.unit and c.status='100'"
                    + " inner join dcp_goods_unit d on a.eid=d.eid and b.pluno=d.pluno and b.punit=d.ounit and d.purunit_use='Y'"
                    + " left  join dcp_unit_lang e on a.eid=e.eid and b.punit=e.unit and e.lang_type='"+langType+"'"
                    + " where a.rn='1'"
                    + " )");
        }
        //0-完工入库 1-转换合并 2-组合拆解
        if (billType.equals("2")||billType.equals("4")||billType.equals("5"))  {
            String bomType="0";
            if (billType.equals("4"))
                bomType="1";
            if (billType.equals("5"))
                bomType="2";
            sqlbuf.append(" ,bom as ("
                    //BOM生产倍量（mulqty）取值错误修正 by jinzma 20220609    // distinct a.pluno
                    + " select * from ("
                    + " select row_number() over (partition by a.pluno,a.unit order by effdate desc) as rn,a.pluno,a.unit,a.mulqty "
                    + " from dcp_bom a "
                    + " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"' "
                    + " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno "
                    + " and trunc(c.material_bdate)<=trunc(sysdate) and (trunc(c.material_edate)>=trunc(sysdate)   or c.material_edate is null )"
                    + " inner join dcp_goods_unit d on a.eid=d.eid and a.pluno=d.pluno and a.unit=d.ounit and d.prod_unit_use='Y'"
                    + " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and a.bomtype = '"+bomType+"' "
                    + " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))"
                    + " ) a where a.rn=1"
                    + " ) "
                    + " ");
        }

        sqlbuf.append(" select goods.*, "
                + " feature.featureno,feature.featurename,"
                + " feature.attrid1,feature.attrvalueid1,feature.attr1name,feature.avalue1name,"
                + " feature.attrid2,feature.attrvalueid2,feature.attr2name,feature.avalue2name,"
                + " feature.attrid3,feature.attrvalueid3,feature.attr3name,feature.avalue3name,"
                + " clang.category_name,image.listImage,"
                + " bar.len,bar.width,bar.height,bar.volumeunit volumeunit_barcode,bar.weight weight_barcode,"
                + " bar.weightunit weightunit_barcode,V1.UNAME VOLUMEUNITNAME_BARCODE,V2.UNAME WEIGHTUNITNAME_BARCODE " );

        //9-盘点计划单-支持多单位盘点
        if (billType.equals("9") && IsStockMultipleUnit.equals("Y")) {
            sqlbuf.append(" ,unitlist.* ");
        }

        // 12-盘点子任务单
        if (!Check.Null(billType) && billType.equals("12")) {
            sqlbuf.append(",substocktake.punit as sub_punit,substocktake.pqty as sub_pqty,substocktake.unit_ratio as sub_unitratio,"
                    + " substocktake.baseunit as sub_baseunit,substocktake.baseqty as sub_baseqty ");
        }

        //【ID1033473】【货郎3.3.0.1】移动门店 商品 菜单 增加总部库存排序-服务端 by jinzma 20230606
        if (isHeadStock.equals("Y")){
            sqlbuf.append(",headstock.headstockqty ");
        }else{
            sqlbuf.append(",N'' as headstockqty ");
        }

        sqlbuf.append(" from (");

        //【ID1017693】【3.0货郎】移动门店商品页增加价格排序筛选 标准进货价排序（0从低到高，1从高到低） by jinzma 20210527
        //【ID1033473】【货郎3.3.0.1】移动门店 商品 菜单 增加总部库存排序-服务端 by jinzma 20230606
        if (Check.Null(distriPriceRank) && Check.Null(stockSort)){
            sqlbuf.append(" select row_number() over(order by a.selfbuiltshopid desc,a.pluno) rn, ");
        } else if ("0".equals(distriPriceRank)){
            sqlbuf.append(" select row_number() over(order by a.selfbuiltshopid desc,a.supprice,a.pluno) rn, ");
        } else if ("1".equals(distriPriceRank)){
            sqlbuf.append(" select row_number() over(order by a.selfbuiltshopid desc,a.supprice desc,a.pluno) rn, ");
        } else if ("0".equals(stockSort)){
            sqlbuf.append(" select row_number() over(order by a.selfbuiltshopid desc,headstock.headstockqty,a.pluno) rn, ");
        } else if ("1".equals(stockSort)){
            sqlbuf.append(" select row_number() over(order by a.selfbuiltshopid desc,headstock.headstockqty desc,a.pluno) rn, ");
        } else{
            sqlbuf.append(" select row_number() over(order by a.selfbuiltshopid desc,a.pluno) rn, ");
        }

        sqlbuf.append(" "
                + " count(*) over() num,a.eid,a.pluno,glang.plu_name,a.plutype,gul.spec,a.category,a.warmType,"
                + " a.virtual,a.stockManageType,a.memo,a.status,"+unit+" as punit,a.isHoliday,a.mainbarcode, "   //UNIT处理 给Punit是考虑取价公用函数
                + " ulang.uname,"
                + " goodsunit.unitratio,"
                + " unit.udlength, goodsunit.weight,goodsunit.volume,a.isBatch,"
                + " a.shelfLife,a.stockInValidDay,a.stockOutValidDay,a.checkValidDay,"
                + " a.selfbuiltshopid,a.price as standardprice,"
                + " b.supplierType,b.supplierId,"
                + " b.safeqty,b.canpurchase,a.taxcode,b.canrequire,b.isnewgoods,"
                + " b.canRequireBack,b.is_auto_subtract,b.CANESTIMATE,b.clearType,b.warningqty,"
                + " a.baseunit,baseunitlang.uname as baseunitname,a.maxorderspec,a.ishotgoods,buludlength.udlength as baseunitudlength,"
                + " a.supplier as mainsupplierid,mainsupplang.supplier_name as mainsuppliername,mainsupplang.abbr as mainsupplierabbr,"
                + " a.PICKUNIT,pickunitlang.uname as pickuname,");

        if (!Check.Null(templateNo) && !Check.Null(templateType) && Check.Null(purchaseType) && !billType.equals("3")) {
            sqlbuf.append(" nvl(p1.receipt_org,p1.supplier) as ptemplate_receipt_org,");
            sqlbuf.append(" nvl(p2.min_qty,b.minqty) as minqty,nvl(p2.max_qty,b.maxqty) as maxqty,nvl(p2.mul_qty,b.mulqty) as mulqty,");
        }else{
            //完工入库、组合拆解和转换合并  //BOM生产倍量（mulqty）取值错误修正 by jinzma 20220609
            if (billType.equals("2") || billType.equals("4") || billType.equals("5")) {
                sqlbuf.append(" '' as ptemplate_receipt_org,b.minqty as minqty,b.maxqty as maxqty,bom.mulqty as mulqty, ");
            }else {
                sqlbuf.append(" '' as ptemplate_receipt_org,b.minqty as minqty,b.maxqty as maxqty,b.mulqty as mulqty, ");
            }
        }


        //采购类型：1 门店自采  3门店直供
        if (!Check.Null(purchaseType) && (purchaseType.equals("1") || purchaseType.equals("3")) && billType.equals("3")){
            sqlbuf.append(" supTemplate.* ");
        }else{
            sqlbuf.append(" N'' as suptemplate_pluno,N'' as suptemplate_price,N'' as suptemplate_punit,N'' as suptemplate_unitratio,"
                    + " N'' as suptemplate_udlength,N'' as suptemplate_punitname");
        }

        sqlbuf.append(" from dcp_goods a ");

        //【ID1024512】 //【货郎】总部商品信息查询  by jinzma 20220407
        sqlbuf.append(" left  join dcp_supplier_lang mainsupplang on a.eid=mainsupplang.eid and a.supplier=mainsupplang.supplier and mainsupplang.lang_type='"+langType+"' ");

        if (!Check.Null(withPlu)) {
            sqlbuf.append(" inner join plu on plu.plu=a.pluno ");
        }

        if (!Check.Null(withCategory)) {
            sqlbuf.append(" inner join category on category.category=a.category ");
        }
        // 小凤：非门店类的商品查询都不关联商品模板  BY JZMA 20200730
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错 by jinzma 20230207
        if (orgForm.equals("2")) {
            sqlbuf.append(" inner join goodstemplate b on b.pluno=a.pluno ");
        }else {
            sqlbuf.append(" left  join goodstemplate b on b.pluno=a.pluno ");
        }

        sqlbuf.append(" ");

        if (billType.equals("0") && Check.Null(templateNo))  //0-要货类
            sqlbuf.append(" and b.canrequire='Y' ");

        if (billType.equals("6")) // 6-计划报单
            sqlbuf.append(" and b.cansale='Y' ");

        if (billType.equals("7")) // 7-可退仓
            sqlbuf.append(" and b.canrequireback='Y' ");

        if (billType.equals("14")) // 14调拨出库单
            sqlbuf.append(" and b.isallot='Y' ");

        //【ID1022162】[货郎]商品信息综合查询+标价签采集 by jinzma 20211119
        if (billType.equals("10")) // 10销售单
            sqlbuf.append(" and b.cansale='Y' ");

        //【ID1018102】 【3.0货郎】门店自采获取外部供应商采购价(服务端)  by jinzma 20210616
        if (billType.equals("3") && !Check.Null(purchaseType) && (purchaseType.equals("1")||purchaseType.equals("3")) ) {
            if ((purchaseType.equals("3")) || CheckSuppGoods.equals("Y")) {
                sqlbuf.append(" inner join supTemplate on supTemplate.supTemplate_pluno = a.pluno");
            }

            if ((purchaseType.equals("1")) && CheckSuppGoods.equals("N")) {
                sqlbuf.append(" left  join supTemplate on supTemplate.supTemplate_pluno = a.pluno");
            }
        }

        if (!Check.Null(templateNo) && !Check.Null(templateType) && Check.Null(purchaseType) && !billType.equals("3")) {
            sqlbuf.append(" "
                    + " inner join dcp_ptemplate p1 on p1.eid=a.eid and p1.ptemplateno='"+templateNo+"' and p1.doc_type='"+templateType+"' "
                    + " inner join dcp_ptemplate_detail p2 on p2.eid=p1.eid and p2.ptemplateno=p1.ptemplateno and p2.doc_type=p1.doc_type "
                    //+ " and p2.pluno=a.pluno   "
                    //【ID1023028】【货郎】移动门店- 盘点录入，按照分类类型的模版盘点，搜索查询不到商品 by jinzma 20211231
                    // 这段逻辑其实没必要改，正常PDA盘点就不应该传模板编号，以下是红艳原本规划
                    //20210308 红艳：
                    //请求增加stockTakeNo；当盘点子任务为按模板盘时，stockTakeNo必传
                    //后端：stockTakeNo前端传了，关联盘点单的盘点明细范围
                    + " and (p2.pluno=a.pluno or (p2.pluno=a.category and p1.doc_type='1' and p1.rangeway='1')) "
            );
        }

//        else {
//            sqlbuf.append(" "
//                    + " left join dcp_ptemplate p1 on 1<>1 "
//                    + " left join dcp_ptemplate_detail p2 on 1<>1 "
//            );
//        }

        //9-盘点计划单
        if (billType.equals("9") && !Check.Null(billNo)) {
            sqlbuf.append(" "
                    + " inner join dcp_stocktask_list stock "
                    + " on stock.eid=a.eid and stock.shopid='"+shopId+"' and stock.stocktaskno='"+billNo+"' and stock.pluno=a.pluno"
                    + " ");
        }
        // 12-盘点子任务单且是模板范围盘点
        if (billType.equals("12") && !Check.Null(stockTakeNo)) {
            sqlbuf.append(" "
                    + " inner join dcp_stocktake_detail stocktake "
                    + " on stocktake.eid=a.eid and stocktake.shopid='"+shopId+"' and stocktake.stocktakeno='"+stockTakeNo+"' and stocktake.pluno=a.pluno"
                    + " ");
        }
        // 11-拼胚
        if (!Check.Null(billType) && billType.equals("11")) {
            sqlbuf.append(" "
                    + " inner join dcp_pinpei_goods pinpei "
                    + " on pinpei.eid=a.eid and pinpei.pluno=a.pluno and pinpei.status='100'"
                    + " ");
        }
        //完工入库、组合拆解和转换合并
        //BOM生产倍量（mulqty）取值错误修正 by jinzma 20220609
        if (billType.equals("2") || billType.equals("4") || billType.equals("5")) {
            sqlbuf.append(" inner join bom on bom.pluno=a.pluno and bom.unit="+unit+" ");
        }

        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" left join (select eid,pluno,plubarcode from dcp_goods_barcode"
                    + " where status='100' and lower(plubarcode)=lower('" + keyTxt + "') group by eid,pluno,plubarcode"
                    + " )bar on bar.eid=a.eid and bar.pluno=a.pluno ");
        }

        if (!Check.Null(withPluBarcode)) {
            sqlbuf.append(" "
                    + " inner join ("
                    + " select a.pluno from dcp_goods_barcode a"
                    + " inner join barcode b on a.plubarcode=b.plubarcode"
                    + " where a.eid='"+eId+"' and a.status='100' group by a.pluno"
                    + " )goodsbarcode on goodsbarcode.pluno=a.pluno"
                    + " ");
        }

        sqlbuf.append(" "
                + " left join dcp_goods_lang glang on glang.eid=a.eid and glang.pluno=a.pluno and glang.lang_type='"+langType+"' "
                + " left join dcp_goods_unit_lang gul on gul.eid=a.eid and gul.pluno=a.pluno and gul.ounit="+unit+" and gul.lang_type='"+langType+"'");

        if (!Check.Null(attrValues)) {
            sqlbuf.append(" inner join ("
                    + " select eid,pluno from dcp_goods_attr_value"
                    + " where attrvalueid in ("+attrValues+") group by eid,pluno "
                    + " ) gav on gav.eid=a.eid and gav.pluno=a.pluno");
        }
        sqlbuf.append(" inner join dcp_goods_unit goodsunit "
                + " on goodsunit.eid=a.eid and goodsunit.pluno=a.pluno and goodsunit.unit=a.baseunit and goodsunit.ounit="+unit+unitUse );  //单位是否可用判断
        sqlbuf.append(" inner join dcp_unit unit on unit.eid=a.eid and unit.unit="+unit );
        sqlbuf.append(" left  join dcp_unit_lang ulang on ulang.eid=a.eid and ulang.unit="+unit+" and ulang.lang_type='"+langType+"'  ");
        sqlbuf.append(" left  join dcp_unit_lang baseunitlang on baseunitlang.eid=a.eid and baseunitlang.unit=a.baseunit ");
        sqlbuf.append(" and baseunitlang.lang_type='"+langType+"'  ");
        sqlbuf.append(" left  join dcp_unit_lang pickunitlang on pickunitlang.eid=a.eid and pickunitlang.unit=a.pickunit ");
        sqlbuf.append(" and pickunitlang.lang_type='"+langType+"'  ");

        //【ID1018894】 【3.0货郎】盘点单无法提交 by jinzma 20210701
        sqlbuf.append( " left join ("
                + " select pluno from dcp_goods_feature where eid='"+eId+"' and status='100' group by pluno"
                + " ) gfeature on a.pluno=gfeature.pluno and a.plutype='FEATURE' "
                + " ");

        //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221110
        sqlbuf.append(" left join dcp_unit buludlength on a.eid=buludlength.eid and a.baseunit=buludlength.unit ");

        //【ID1033473】【货郎3.3.0.1】移动门店 商品 菜单 增加总部库存排序-服务端 by jinzma 20230606
        if (isHeadStock.equals("Y")){
            sqlbuf.append(" left join ("
                    + " select pluno,sum(availableqty) as headstockqty from dcp_headquarter_stock "
                    + " where eid='"+eId+"' group by pluno "
                    + " ) headstock on a.pluno=headstock.pluno ");
        }


        //【ID1018894】 【3.0货郎】盘点单无法提交 by jinzma 20210701
        sqlbuf.append(" where a.eid='"+eId+"' "
                + " and ((a.plutype='FEATURE' and gfeature.pluno is not null) or a.plutype<>'FEATURE')");

        if (!Check.Null(status)) {
            //【ID1028991】 //【嘉利轩3.0】出库类的单据无法选取商品报错  by jinzma 20221012
            //sqlbuf.append(" and a.status='"+status+"'");
            if (status.equals("100")) {
                sqlbuf.append(" and a.status > 0 ");
            }else{
                sqlbuf.append(" and a.status < 0 ");
            }
        }

        if (!Check.Null(brands))
            sqlbuf.append(" and a.brand in ("+brands+")");
        if (!Check.Null(seriess))
            sqlbuf.append(" and a.series in ("+seriess+")");
        if (!Check.Null(attrGroups))
            sqlbuf.append(" and a.attrGroupid in ("+attrGroups+")");
        if (!Check.Null(isHotGoods))
            sqlbuf.append(" and a.ishotgoods ='"+isHotGoods+"'");
        if (!Check.Null(isNewGoods))
            sqlbuf.append(" and b.isnewgoods='"+isNewGoods+"'");
        if(!Check.Null(pluType))
            sqlbuf.append(" and a.plutype = '"+pluType+"'");
        
        /*
        【ID1018538】 //【3.0货郎】商品分类查询/商品列表查询服务响应提速优化  by jinzma 20210628  in --> 关联表
        if (!Check.Null(categorys))
            sqlbuf.append(" and a.category in ("+categorys+")");
        if (!Check.Null(pluNos))
            sqlbuf.append(" and a.pluno in ("+pluNos+")");
        */


        if (!Check.Null(virtual)) {
            //【ID1035264】【嘉华3.0】中台不能选择到虚拟商品和套餐商品-----服务端优化  by jinzma 20230807
            if (virtual.equals("Y")){
                sqlbuf.append(" and (a.virtual='Y' or a.plutype='PACKAGE') ");   // Y：虚拟商品或套餐商品;
            }
            if (virtual.equals("N")){
                sqlbuf.append(" and a.virtual='N' and a.plutype<>'PACKAGE' ");   // N：过滤掉虚拟商品和套餐商品;
            }
        }
        if (!Check.Null(isBatch))
            sqlbuf.append(" and a.isbatch='"+isBatch+"'");
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and ("
                    + " (lower(bar.plubarcode)=lower('"+keyTxt+"')) or"
                    + " (lower(a.pluno) like lower('%%"+keyTxt+"%%')) or"
                    + " (lower(glang.plu_name) like lower('%%"+keyTxt+"%%')) or"
                    + " (lower(gul.spec) like lower('%%"+keyTxt+"%%')) or"
                    + " (lower(a.shortcut_code) like lower('%%"+keyTxt+"%%'))"
                    + " )");
        }

        //【ID1023729】货郎3.0加盟店可以自己采购商品，定价，做促销，管理自己的会员（后端云中台服务） by jinzma 20220217
        //searchScope by jinzma 20220310
        switch (searchScope){
            case "0":    //0、全部
                break;
            case "1":    //1、总部和当前自建门店
                sqlbuf.append(" and (a.selfbuiltshopid is null or a.selfbuiltshopid = '"+selfBuiltShopId+"')");
                break;
            case "2":    //2、仅总部
                sqlbuf.append(" and a.selfbuiltshopid is null");
                break;
            case "3":    //3、全部自建门店
                sqlbuf.append(" and a.selfbuiltshopid is not null");
                break;
            case "4":    //4、仅当前自建门店
                sqlbuf.append(" and a.selfbuiltshopid = '"+selfBuiltShopId+"'");
                break;
        }

        sqlbuf.append(" )goods ");

        sqlbuf.append(" left join ("
                + " select a.eid,a.pluno,a.featureno,flang.featurename,"
                + " a.attrid1,a.attrvalueid1,alang1.attrname as attr1name,avlang1.attrvaluename as avalue1name,"
                + " a.attrid2,a.attrvalueid2,alang2.attrname as attr2name,avlang2.attrvaluename as avalue2name,"
                + " a.attrid3,a.attrvalueid3,alang3.attrname as attr3name,avlang3.attrvaluename as avalue3name "
                + " from dcp_goods_feature a "
                + " left join dcp_goods_feature_lang flang on flang.eid=a.eid and flang.pluno=a.pluno and flang.featureno=a.featureno and flang.lang_type='"+langType+"' "
                + " left join dcp_attribution_lang alang1 on alang1.eid=a.eid and alang1.attrid=a.attrid1 and alang1.lang_type='"+langType+"' "
                + " left join dcp_attribution_lang alang2 on alang2.eid=a.eid and alang2.attrid=a.attrid2 and alang2.lang_type='"+langType+"' "
                + " left join dcp_attribution_lang alang3 on alang3.eid=a.eid and alang3.attrid=a.attrid3 and alang3.lang_type='"+langType+"' "
                + " left join dcp_attribution_value_lang avlang1 on avlang1.eid=a.eid and avlang1.attrid=a.attrid1 and avlang1.attrvalueid=a.attrvalueid1 and avlang1.lang_type='"+langType+"'"
                + " left join dcp_attribution_value_lang avlang2 on avlang2.eid=a.eid and avlang2.attrid=a.attrid2 and avlang2.attrvalueid=a.attrvalueid2 and avlang2.lang_type='"+langType+"'"
                + " left join dcp_attribution_value_lang avlang3 on avlang3.eid=a.eid and avlang3.attrid=a.attrid3 and avlang3.attrvalueid=a.attrvalueid3 and avlang3.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.status='100'"
                + " ) feature on feature.eid=goods.eid and feature.pluno=goods.pluno and goods.plutype='FEATURE' "
                + " ");

        // 12-盘点子任务单
        if (!Check.Null(billType) && billType.equals("12")) {
            sqlbuf.append(" "
                    + " left join dcp_substocktake_detail substocktake"
                    + " on substocktake.eid='"+eId+"' and substocktake.shopid='"+shopId+"' and substocktake.substocktakeno='"+billNo+"'"
                    + " and substocktake.pluno=goods.pluno "
                    + " and (substocktake.featureno = feature.featureno or (substocktake.featureno=' ' and feature.featureno is null))"
                    + " ");
        }

        //9-盘点计划单-支持多单位盘点
        if (billType.equals("9") && IsStockMultipleUnit.equals("Y")) {
            sqlbuf.append(" "
                    + " left join ("
                    + " select a.pluno,a.ounit as unitlist_unit,a.unit,a.unitratio as unitlist_unitratio,"
                    + " b.udlength as unitlist_unitudlength,"
                    + " c.uname as unitlist_unitname"
                    + " from dcp_goods_unit a"
                    + " left join dcp_unit b on a.eid=b.eid and a.ounit=b.unit and b.status='100'"
                    + " left join dcp_unit_lang c on a.eid=c.eid and a.ounit=c.unit and c.lang_type='"+langType+"'"
                    + " where a.eid='"+eId+"' and a.cunit_use='Y'"
                    + " ) unitlist on unitlist.pluno = goods.pluno and unitlist.unit=goods.baseunit"
                    + " ");
        }

        //【ID1033473】【货郎3.3.0.1】移动门店 商品 菜单 增加总部库存排序-服务端 by jinzma 20230606
        if (isHeadStock.equals("Y")){
            sqlbuf.append(" left join ("
                    + " select pluno,featureno,sum(availableqty) as headstockqty from dcp_headquarter_stock "
                    + " where eid='"+eId+"' group by pluno,featureno "
                    + " ) headstock on goods.pluno=headstock.pluno and nvl(feature.featureno,' ')=headstock.featureno  ");
        }

        sqlbuf.append(" "
                + " left join dcp_goodsimage image on image.eid=goods.eid and image.pluno=goods.pluno and image.apptype='ALL' "
                + " left join dcp_category_lang clang on clang.eid=goods.eid and clang.category=goods.category and clang.lang_type='"+langType+"' "
                + " ");
        //添加长宽高重
        sqlbuf.append(""
                + " left join dcp_goods_barcode bar on goods.eid=bar.eid and goods.pluno=bar.pluno and goods.punit=bar.unit "
                + " and (bar.pluno=feature.pluno or feature.pluno is null) and (feature.featureno is null or bar.featureno=feature.featureno)"
                + " and bar.status='100'"
                + " left join DCP_UNIT_LANG  V1 ON bar.EID=V1.EID AND bar.VOLUMEUNIT=V1.UNIT AND V1.LANG_TYPE='"+langType+"'"
                + " LEFT JOIN DCP_UNIT_LANG  V2 ON bar.EID=V2.EID AND bar.WEIGHTUNIT=V2.UNIT AND V2.LANG_TYPE='"+langType+"' "
                + "");


        if (!Check.Null(withPluBarcode)) {
            sqlbuf.append(" "
                    + " inner join ("
                    + " select dgb.plubarcode,dgb.pluno,dgb.unit,dgb.featureno from dcp_goods_barcode dgb"
                    + " inner join barcode on dgb.plubarcode = barcode.plubarcode"
                    + " where dgb.eid='"+eId+"' "
                    + " )plubarcode "
                    + " on plubarcode.pluno=goods.pluno "
                    + " and ("
                    + " (goods.plutype='FEATURE' and plubarcode.featureno<>' ' and plubarcode.featureno=feature.featureno)"
                    + " or (goods.plutype<>'FEATURE' and plubarcode.featureno=' ')"
                    + " )"
                    + " ");
        }else{
            if (billType.equals("9") && IsStockMultipleUnit.equals("Y")) {
                //【ID1026070】【詹记3.0】门店多单位盘点，偶发性出现录入盘点单，保存时整单数量为0。录入数量，保存数量串单位，导致库存混乱。
                //增加盘点多单位顺序排序  by jinzma 20220520
                sqlbuf.append(" where rn > "+startRow+" and rn <="+(startRow+pageSize) + " order by rn,unitlist.unitlist_unit ");
            }else{
                sqlbuf.append(" where rn > "+startRow+" and rn <="+(startRow+pageSize) + " order by rn ");
            }
        }

        return sqlbuf.toString();
    }

    private String getWeekDay() throws Exception{
        String weekOfDay="";
        String sql = "select to_char(sysdate,'D') as week from dual";
        List<Map<String, Object>> getWeekDay = this.doQueryData(sql, null);
        if (getWeekDay != null && !getWeekDay.isEmpty()) {
            Map<String, Object> strWeekDay = getWeekDay.get(0);
            String weekDay = strWeekDay.get("WEEK").toString();
            switch (weekDay) {
                case "1":
                    weekOfDay="周日";
                    break;
                case "2":
                    weekOfDay="周一";
                    break;
                case "3":
                    weekOfDay="周二";
                    break;
                case "4":
                    weekOfDay="周三";
                    break;
                case "5":
                    weekOfDay="周四";
                    break;
                case "6":
                    weekOfDay="周五";
                    break;
                case "7":
                    weekOfDay="周六";
                    break;
                default:
                    break;
            }
        }
        return weekOfDay;
    }

    private String getDay() throws Exception{
        String day="";
        String sql = "select to_char(sysdate,'dd') as day from dual";
        List<Map<String, Object>> getDay = this.doQueryData(sql, null);
        if (getDay != null && !getDay.isEmpty()) {
            day=getDay.get(0).get("DAY").toString();
        }
        return day;
    }


}
