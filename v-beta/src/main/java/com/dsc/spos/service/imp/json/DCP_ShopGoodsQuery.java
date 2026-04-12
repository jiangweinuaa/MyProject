package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ShopGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_ShopGoodsQueryRes;
import com.dsc.spos.json.cust.res.DCP_ShopGoodsQueryRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ShopGoodsQueryRes.level2Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
/*
 * 服务函数：DCP_ShopGoodsQuery
 * 服务说明：门店商品查询
 * @author jinzma
 * @since  2021-03-19
 */
public class DCP_ShopGoodsQuery extends SPosBasicService<DCP_ShopGoodsQueryReq, DCP_ShopGoodsQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ShopGoodsQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String shopId = req.getRequest().getShopId();
        if(Check.Null(shopId)){
            errMsg.append("门店ID不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_ShopGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_ShopGoodsQueryReq>(){};
    }

    @Override
    protected DCP_ShopGoodsQueryRes getResponseType() {
        return new DCP_ShopGoodsQueryRes();
    }

    @Override
    protected DCP_ShopGoodsQueryRes processJson(DCP_ShopGoodsQueryReq req) throws Exception {
        DCP_ShopGoodsQueryRes res = this.getResponse();
        level1Elm oneLv1 = res.new level1Elm();
        String eId = req.geteId();
        String shopId = req.getRequest().getShopId();  //门店从前端传入
        String companyId = "";
        try {
            String sql=" select org_form,belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData==null || getQData.isEmpty()){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店在组织表(DCP_ORG)中不存在!");
            }else {
                String orgForm = getQData.get(0).get("ORG_FORM").toString();
                companyId = getQData.get(0).get("BELFIRM").toString();
                if (Check.Null(companyId) && orgForm.equals("0")){
                    companyId = shopId;
                }
            }
            getQData.clear();
            sql=this.getQuerySql(req,eId,shopId,companyId);
            getQData=this.doQueryData(sql, null);
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            if (getQData != null && getQData.isEmpty() == false)
            {
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                String templateId = "";
                String templateName = "";
                oneLv1.setGoodsList(new ArrayList<level2Elm>());

                for (Map<String, Object> oneData : getQData) {
                    level2Elm oneLv2 = res.new level2Elm();
                    //处理templateId和templateName
                    if (Check.Null(templateId)){
                        templateId = oneData.get("TEMPLATEID").toString();
                        templateName = oneData.get("TEMPLATENAME").toString();
                    }
                    String pluNo = oneData.get("PLUNO").toString();
                    String pluName = oneData.get("PLU_NAME").toString();
                    String warningQty = oneData.get("WARNINGQTY").toString();
                    String safeQty = oneData.get("SAFEQTY").toString();
                    String canSale = oneData.get("CANSALE").toString();
                    String canFree = oneData.get("CANFREE").toString();
                    String canOrder = oneData.get("CANORDER").toString();
                    String canReturn = oneData.get("CANRETURN").toString();
                    String canPurchase = oneData.get("CANPURCHASE").toString();
                    String canRequire = oneData.get("CANREQUIRE").toString();
                    String minQty = oneData.get("MINQTY").toString();
                    String maxQty = oneData.get("MAXQTY").toString();
                    String multiQty = oneData.get("MULQTY").toString();
                    String canRequireBack = oneData.get("CANREQUIREBACK").toString();
                    String isAutoSubtract = oneData.get("IS_AUTO_SUBTRACT").toString();

                    //是否可用Y/N：商品状态为100且模板商品状态为100时可用，否则不可用
                    String canUse ="Y";
                    String templateStatus = oneData.get("TEMPLATESTATUS").toString();
                    String goodsStatus = oneData.get("GOODSSTATUS").toString();
                    if (Check.Null(goodsStatus) || Check.Null(templateStatus) || !templateStatus.equals("100") || !goodsStatus.equals("100"))
                        canUse="N";

                    oneLv2.setPluNo(pluNo);
                    oneLv2.setPluName(pluName);
                    oneLv2.setCanUse(canUse);
                    oneLv2.setWarningQty(warningQty);
                    oneLv2.setSafeQty(safeQty);
                    oneLv2.setCanSale(canSale);
                    oneLv2.setCanFree(canFree);
                    oneLv2.setCanOrder(canOrder);
                    oneLv2.setCanReturn(canReturn);
                    oneLv2.setCanPurchase(canPurchase);
                    oneLv2.setCanRequire(canRequire);
                    oneLv2.setMinQty(minQty);
                    oneLv2.setMaxQty(maxQty);
                    oneLv2.setMultiQty(multiQty);
                    oneLv2.setCanRequireBack(canRequireBack);
                    oneLv2.setIsAutoSubtract(isAutoSubtract);

                    oneLv1.getGoodsList().add(oneLv2);
                }

                oneLv1.setTemplateId(templateId);
                oneLv1.setTemplateName(templateName);

            }else{
                totalRecords = 0;
                totalPages = 0;
            }

            res.setDatas(oneLv1);
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ShopGoodsQueryReq req) throws Exception {
        return null;
    }

    private String getQuerySql(DCP_ShopGoodsQueryReq req,String eId,String shopId,String companyId) throws Exception {
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;

        StringBuffer sqlbuf = new StringBuffer();
       /* sqlbuf.append(""
                + " with goodstemplate as ("
                + " select b.*,c.templatename from ("
                + " select a.*,row_number() over (order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                + " where a.eid='"+eId+"' and a.status='100'"
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " )a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid"
                + " left  join dcp_goodstemplate_lang c on c.eid=a.eid and c.templateid=a.templateid and c.lang_type='"+langType+"'"
                + " where a.rn=1 )"
                + " ");*/
    
        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.*,c.templatename from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                + " left  join dcp_goodstemplate_lang  c on c.eid=a.eid and c.templateid=a.templateid and c.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"') "
                + " )"
                + " ");

        sqlbuf.append(""
                + " select * from ("
                + " select count(*) over() num,row_number() over (order by a.pluno) rn,"
                + " a.pluno,c.plu_name,"
                + " b.templateid,b.templatename,b.warningqty,b.safeqty,b.cansale,b.canfree,b.canorder,b.canreturn,b.canpurchase,"
                + " b.canrequire,b.canrequireback,b.minqty,b.maxqty,b.mulqty,b.is_auto_subtract,"
                + " nvl(b.status,N'0') as templatestatus,a.status as goodsstatus"
                + " from dcp_goods a"
                + " left join goodstemplate b on a.eid=b.eid and a.pluno=b.pluno"
                + " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"'"
                + " ");

        if(!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.pluno like '%"+keyTxt+"%' or a.shortcut_code like '%"+keyTxt+"%' or c.plu_name like '%"+keyTxt+"%')");
        }
        sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
        return sqlbuf.toString();
    }

}
