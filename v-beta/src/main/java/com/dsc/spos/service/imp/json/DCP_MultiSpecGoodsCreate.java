package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq.attr;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq.attrValue;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq.masterPluName_lang;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsCreateReq.subGoods;
import com.dsc.spos.json.cust.res.DCP_MultiSpecGoodsCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_MultiSpecGoodsCreate  extends SPosAdvanceService<DCP_MultiSpecGoodsCreateReq,DCP_MultiSpecGoodsCreateRes>{

    @Override
    protected void processDUID(DCP_MultiSpecGoodsCreateReq req, DCP_MultiSpecGoodsCreateRes res) throws Exception {
        try{

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
            PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,eId);

            String curLangType = req.getLangType();
            if(curLangType==null||curLangType.isEmpty())
            {
                curLangType = "zh_CN";
            }

            String masterPluNo = req.getRequest().getMasterPluNo();
            String status = req.getRequest().getStatus();
            String memo = req.getRequest().getMemo();
            String attrGroupId = req.getRequest().getAttrGroupId();
            String attrGroupName = req.getRequest().getAttrGroupName();
            BigDecimal minPrice = new BigDecimal("0");
            BigDecimal maxPrice = new BigDecimal("0");

            List<masterPluName_lang> masterPluName_langList = req.getRequest().getMasterPluName_lang();
            List<attr> attrList = req.getRequest().getAttrList();
            List<subGoods> subGoodsList = req.getRequest().getSubGoodsList();

            String lastmoditime = null;//req.getRequset().getLastmoditime();
            if(lastmoditime==null||lastmoditime.isEmpty())
            {
                lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }

            if(isExistMultiSpecGoods(eId, masterPluNo))
            {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("多规格商品编码："+masterPluNo+"已存在！");
                return;
            }
            if(isExistMultiSpecGoodsLang(eId, masterPluNo))
            {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("多规格商品编码："+masterPluNo+"多语言信息已存在！");
                return;
            }



            String[] columns_lang =
                    {
                            "EID",
                            "MASTERPLUNO",
                            "LANG_TYPE",
                            "MASTERPLUNAME",
                            "LASTMODITIME"

                    };
            //DCP_MULTISPECGOODS_LANG
            for (masterPluName_lang lang : masterPluName_langList)
            {
                DataValue[] insValue1 = null;

                insValue1 = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(masterPluNo, Types.VARCHAR),
                                new DataValue(lang.getLangType(), Types.VARCHAR),
                                new DataValue(lang.getName(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };
                InsBean ib1 = new InsBean("DCP_MSPECGOODS_LANG", columns_lang);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));
            }

            String[] columns_attr =
                    {
                            "EID",
                            "MASTERPLUNO",
                            "ATTRID",
                            "SORTID",
                            "LASTMODITIME"

                    };

            String[] columns_attrValue =
                    {
                            "EID",
                            "MASTERPLUNO",
                            "ATTRID",
                            "ATTRVALUEID",
                            "LASTMODITIME"
                    };
            //DCP_MULTISPECGOODS_ATTR
            int i = 1;
            for (attr par : attrList)
            {
                String attrId = par.getAttrId();
                DataValue[] insValue1 = null;

                insValue1 = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(masterPluNo, Types.VARCHAR),
                                new DataValue(attrId, Types.VARCHAR),
                                new DataValue(i, Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };

                InsBean ib1 = new InsBean("DCP_MSPECGOODS_ATTR", columns_attr);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));
                i++;

                List<attrValue> attrValueList = par.getAttrValueList();
                if(attrValueList!=null&&attrValueList.isEmpty()==false)
                {
                    for (attrValue item : attrValueList)
                    {
                        DataValue[] insValue_attrValue = null;

                        insValue_attrValue = new DataValue[]
                                {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(masterPluNo, Types.VARCHAR),
                                        new DataValue(attrId, Types.VARCHAR),
                                        new DataValue(item.getAttrValueId(), Types.VARCHAR),
                                        new DataValue(lastmoditime, Types.DATE)
                                };

                        InsBean ib_attrValue = new InsBean("DCP_MSPECGOODS_ATTR_VALUE", columns_attrValue);
                        ib_attrValue.addValues(insValue_attrValue);
                        this.addProcessData(new DataProcessBean(ib_attrValue));
                    }
                }
            }

            String[] columns_subGoods =
                    {
                            "EID",
                            "MASTERPLUNO",
                            "FEATURENO",
                            //"FEATURENAME",
                            "ATTRID1",
                            "ATTRVALUEID1",
                            "ATTRID2",
                            "ATTRVALUEID2",
                            "ATTRID3",
                            "ATTRVALUEID3",
                            "PLUNO",
                            "UNIT",
                            "PRICE",
                            "LASTMODITIME"

                    };
            //DCP_MSPECGOODS_SUBGOODS

            String[] columns_subGoods_lang =
                    {
                            "EID",
                            "MASTERPLUNO",
                            "FEATURENO",
                            "LANG_TYPE",
                            "FEATURENAME",
                            "LASTMODITIME"

                    };
            //DCP_MSPECGOODS_SUBGOODS_lang


            for (subGoods par : subGoodsList)
            {
                BigDecimal curPrice = new BigDecimal("0");
                if (PosPub.isNumericType(par.getPrice())){
                    curPrice =  new BigDecimal(par.getPrice());
                }
                if (minPrice.compareTo(BigDecimal.ZERO)==0){
                    minPrice = curPrice;
                }else{
                    if (minPrice.compareTo(curPrice)==1){
                        minPrice = curPrice;
                    }
                }
                if (maxPrice.compareTo(BigDecimal.ZERO)==0){
                    maxPrice = curPrice;
                }else{
                    if (maxPrice.compareTo(curPrice)==-1){
                        maxPrice = curPrice;
                    }
                }

                DataValue[] insValue1 = null;
                insValue1 = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(masterPluNo, Types.VARCHAR),
                                new DataValue(par.getFeatureNo(), Types.VARCHAR),
                                //new DataValue(par.getFeatureName(), Types.VARCHAR),
                                new DataValue(par.getAttrId1(), Types.VARCHAR),
                                new DataValue(par.getAttrValueId1(), Types.VARCHAR),
                                new DataValue(par.getAttrId2(), Types.VARCHAR),
                                new DataValue(par.getAttrValueId2(), Types.VARCHAR),
                                new DataValue(par.getAttrId3(), Types.VARCHAR),
                                new DataValue(par.getAttrValueId3(), Types.VARCHAR),
                                new DataValue(par.getPluNo(), Types.VARCHAR),
                                new DataValue(par.getUnit(), Types.VARCHAR),
                                new DataValue(curPrice, Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };

                InsBean ib1 = new InsBean("DCP_MSPECGOODS_SUBGOODS", columns_subGoods);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));

                DataValue[] insValue1_lang = null;

                insValue1_lang = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(masterPluNo, Types.VARCHAR),
                                new DataValue(par.getFeatureNo(), Types.VARCHAR),
                                new DataValue(curLangType, Types.VARCHAR),
                                new DataValue(par.getFeatureName(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };

                InsBean ib1_lang = new InsBean("DCP_MSPECGOODS_SUBGOODS_LANG", columns_subGoods_lang);
                ib1_lang.addValues(insValue1_lang);
                this.addProcessData(new DataProcessBean(ib1_lang));
            }


            String[] columns_pluno =
                    {
                            "EID",
                            "MASTERPLUNO",
                            "ATTRGROUPID",
                            "STATUS",
                            "MEMO",
                            "MINPRICE",
                            "MAXPRICE",
                            "CREATETIME"

                    };
            DataValue[] insValue_pluno = null;

            insValue_pluno = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(masterPluNo, Types.VARCHAR),
                            new DataValue(attrGroupId, Types.VARCHAR),
                            new DataValue(status, Types.VARCHAR),
                            new DataValue(memo, Types.VARCHAR),
                            new DataValue(minPrice.toPlainString(), Types.VARCHAR),
                            new DataValue(maxPrice.toPlainString(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE)
                    };

            InsBean ib_pluno = new InsBean("DCP_MSPECGOODS", columns_pluno);
            ib_pluno.addValues(insValue_pluno);
            this.addProcessData(new DataProcessBean(ib_pluno));

            this.doExecuteDataToDB();//

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MultiSpecGoodsCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MultiSpecGoodsCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MultiSpecGoodsCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MultiSpecGoodsCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        //必传字段
        String masterPluNo = req.getRequest().getMasterPluNo();
        String status = req.getRequest().getStatus();
        String attrGroupId = req.getRequest().getAttrGroupId();
        String attrGroupName = req.getRequest().getAttrGroupName();


        List<attr> attrList = req.getRequest().getAttrList();
        List<subGoods> subGoodsList = req.getRequest().getSubGoodsList();
        List<masterPluName_lang> masterPluName_lang = req.getRequest().getMasterPluName_lang();
        if (Check.Null(masterPluNo))
        {
            errMsg.append("多规格商品编码不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status))
        {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if (Check.Null(attrGroupId))
        {
            errMsg.append("商品属性分组编码不可为空值, ");
            isFail = true;
        }
        if (Check.Null(attrGroupName))
        {
            errMsg.append("商品属性分组名称不可为空值, ");
            isFail = true;
        }
        if(masterPluName_lang==null||masterPluName_lang.isEmpty())
        {
            errMsg.append("商品名称多语言信息不可为空, ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if(subGoodsList==null||subGoodsList.isEmpty())
        {
            errMsg.append("子商品信息不可为空, ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(attrList==null||attrList.isEmpty())
        {
            errMsg.append("属性信息不可为空, ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (masterPluName_lang par : masterPluName_lang)
        {

            if(Check.Null(par.getLangType()))
            {
                errMsg.append("多语言别不可为空值, ");
                isFail = true;
            }
            if(Check.Null(par.getName()))
            {
                errMsg.append("多语言名称不可为空值, ");
                isFail = true;
            }
        }

        for (attr par : attrList)
        {

            if(Check.Null(par.getAttrId()))
            {
                errMsg.append("属性编码不可为空值, ");
                isFail = true;
            }
            if(Check.Null(par.getAttrName()))
            {
                errMsg.append("属性名称不可为空值, ");
                isFail = true;
            }

            List<attrValue> attrValueList = par.getAttrValueList();
            if(attrValueList==null||attrValueList.isEmpty())
            {
                errMsg.append("规格不可为空, ");
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }

            for (attrValue item : attrValueList)
            {
                if(Check.Null(item.getAttrValueId()))
                {
                    errMsg.append("规格编码不可为空值, ");
                    isFail = true;
                }
                if(Check.Null(item.getAttrValueName()))
                {
                    errMsg.append("规格名称不可为空值, ");
                    isFail = true;
                }

            }

        }

        for (subGoods par : subGoodsList)
        {
            if(Check.Null(par.getPluNo()))
            {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }
            if(Check.Null(par.getUnit()))
            {
                errMsg.append("单位不可为空值, ");
                isFail = true;
            }
            if(Check.Null(par.getFeatureNo()))
            {
                errMsg.append("特征码编码不可为空值, ");
                isFail = true;
            }
            if(Check.Null(par.getFeatureName()))
            {
                errMsg.append("特征码名称不可为空值, ");
                isFail = true;
            }
            if(Check.Null(par.getAttrId1()))
            {
                errMsg.append("属属性1编码不可为空值, ");
                isFail = true;
            }
            if(Check.Null(par.getAttrValueId1()))
            {
                errMsg.append("属属性1的规格编码不可为空值, ");
                isFail = true;
            }

        }






        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_MultiSpecGoodsCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_MultiSpecGoodsCreateReq>(){} ;
    }

    @Override
    protected DCP_MultiSpecGoodsCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_MultiSpecGoodsCreateRes();
    }

    private boolean isExistMultiSpecGoods(String eid,String masterPluNo) throws Exception
    {
        boolean nRet = false;
        String sql =" select * from DCP_MSPECGOODS where eid='"+eid+"' and MASTERPLUNO='"+masterPluNo+"' ";
        List<Map<String, Object>> getData = this.doQueryData(sql, null);
        if(getData!=null&&getData.isEmpty()==false)
        {
            nRet = true;
        }


        return nRet;

    }

    private boolean isExistMultiSpecGoodsLang(String eid,String masterPluNo) throws Exception
    {
        boolean nRet = false;
        String sql =" select * from DCP_MSPECGOODS_LANG "
                + "where eid='"+eid+"' and MASTERPLUNO='"+masterPluNo+"'";
        List<Map<String, Object>> getData = this.doQueryData(sql, null);
        if(getData!=null&&getData.isEmpty()==false)
        {
            nRet = true;
        }


        return nRet;

    }


}
