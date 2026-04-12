package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BeforeDishCreate_OpenReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_BeforeDishCreate_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: KDS预制菜品创建
 * @author: wangzyc
 * @create: 2021-10-08
 */
public class DCP_BeforeDishCreate_Open extends SPosAdvanceService<DCP_BeforeDishCreate_OpenReq, DCP_BeforeDishCreate_OpenRes> {
    @Override
    protected void processDUID(DCP_BeforeDishCreate_OpenReq req, DCP_BeforeDishCreate_OpenRes res) throws Exception {
        DCP_BeforeDishCreate_OpenReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String shopId = request.getShopId();
        String userId = request.getUserId();
        String userName = request.getUserName();
        String langType = req.getLangType();
        List<DCP_BeforeDishCreate_OpenReq.level2Elm> goodsList = request.getGoodsList();

        try
        {
            // 获取门店默认出货成本仓
            String sql = "select OUT_COST_WAREHOUSE from DCP_ORG where eid = '" + eId + "' and ORGANIZATIONNO = '" + shopId + "' ";
            List<Map<String, Object>> getOUT_COST_WAREHOUSE = this.doQueryData(sql, null);
            String OUT_COST_WAREHOUSE = "";
            if (!CollectionUtils.isEmpty(getOUT_COST_WAREHOUSE)) {
                OUT_COST_WAREHOUSE = getOUT_COST_WAREHOUSE.get(0).get("OUT_COST_WAREHOUSE").toString();
            }


            String[] columns_Processtask_Detail = {
                    "EID", "SHOPID", "ORGANIZATIONNO", "PROCESSTASKNO", "ITEM", "MUL_QTY", "PQTY",  "PUNIT",
                    "BASEQTY", "PLUNO", "PLUNAME", "PRICE", "BASEUNIT", "UNIT_RATIO", "AMT", "DISTRIPRICE", "DISTRIAMT", "BDATE", "FEATURENO",
                    "GOODSSTATUS", "FINALCATEGORY", "PLUBARCODE", "AVAILQTY"
            };
            String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
            // 生成预制单号
            String beforNo = this.getBeforNo(req);
            String[] conditionValues = null;
            StringBuffer sqlbuf = this.getPlunoDetail();
            String processTaskNO = this.getProcessTaskNO(req);
            int tot_cqty = 0;
            int tot_pqty = 0;
            int item =0;


            //预制菜做个免配菜处理逻辑,查菜品控制表
            StringBuffer sJoinPluno=new StringBuffer("");
            StringBuffer sJoinCategory=new StringBuffer("");
            //优化查询多pluno一起查
            for (DCP_BeforeDishCreate_OpenReq.level2Elm lv2 : goodsList)
            {
                sJoinPluno.append(lv2.getPluNo()+",");
                sJoinCategory.append(lv2.getCategoryId()+",");
            }
            //
            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("PLUNO", sJoinPluno.toString());
            mapOrder.put("CATEGORY", sJoinCategory.toString());

            //
            MyCommon cm=new MyCommon();
            String withasSql_Pluno=cm.getFormatSourceMultiColWith(mapOrder);
            mapOrder=null;

            //1、菜品控制表
            String KDS_Control_SQL = "with a AS ( " +
                    withasSql_Pluno + " ) " +
                    "select  b.* from DCP_KDSDISHES_CONTROL b " +
                    "inner join a on a.pluno=b.pluno and a.category=b.category " +
                    "where b.eid='" + eId + "' and b.shopid='" + shopId + "'";
            List<Map<String, Object>> getData_KDS_Control = this.doQueryData(KDS_Control_SQL, null);

            //2、总部菜品控制表
            String KDS_HQControl_SQL = "with a AS ( " +
                    withasSql_Pluno + " ) " +
                    "select  b.* from DCP_HQKDSDISHES_CONTROL b " +
                    "inner join a on (b.goodstype=2 and  a.pluno=b.id) or (b.goodstype=1 and a.category=b.id) " +
                    "where b.eid='" + eId + "' order by b.goodstype desc ";
            List<Map<String, Object>> getData_KDS_HQControl = this.doQueryData(KDS_HQControl_SQL, null);

            for (DCP_BeforeDishCreate_OpenReq.level2Elm lv2 : goodsList)
            {

                //预制菜做个免配菜处理逻辑,查菜品控制表
                /**
                 * KDS 免配菜Y/N/空
                 */
                String KDS_unside="";
                /**
                 * KDS 免制作---这个在预制菜先不判断了
                 */
                String KDS_uncook="";
                /**
                 * KDS 免传菜---这个在预制菜先不判断了
                 */
                String KDS_uncall="";

                //门店找到标记
                boolean bKDS_shopOK=false;
                if (getData_KDS_Control != null && getData_KDS_Control.size() > 0)
                {
                    //根据品类及品号查找
                    List<Map<String, Object>> getData_KDS_temp = getData_KDS_Control.stream().filter(p -> p.get("CATEGORY").toString().equals(lv2.getCategoryId()) && p.get("PLUNO").toString().equals(lv2.getPluNo())).collect(Collectors.toList());
                    if (getData_KDS_temp != null && getData_KDS_temp.size() > 0)
                    {
                        KDS_unside= Convert.toStr(getData_KDS_temp.get(0).get("UNSIDE"), "");
                        KDS_uncook=Convert.toStr(getData_KDS_temp.get(0).get("UNCOOK"),"");
                        KDS_uncall=Convert.toStr(getData_KDS_temp.get(0).get("UNCALL"),"");
                        //全部空表示未设置，此条记录无效，取总部
                        if (!KDS_unside.equals("")|| !KDS_uncook.equals("")||!KDS_uncall.equals(""))
                        {
                            bKDS_shopOK=true;
                        }
                    }

                    //门店找不到，再找
                    if (!bKDS_shopOK)
                    {
                        if (getData_KDS_HQControl != null && getData_KDS_HQControl.size()>0)
                        {
                            //根据品类及品号查找，门店没有，再查总部的
                            getData_KDS_temp = getData_KDS_HQControl.stream().filter(p -> (p.get("GOODSTYPE").toString().equals("2") && p.get("ID").toString().equals(lv2.getPluNo())) ||
                                    (p.get("GOODSTYPE").toString().equals("1") && p.get("ID").toString().equals(lv2.getCategoryId()))).collect(Collectors.toList());
                            if (getData_KDS_temp != null && getData_KDS_temp.size() > 0)
                            {
                                KDS_unside=Convert.toStr(getData_KDS_temp.get(0).get("UNSIDE"),"");
                                KDS_uncook=Convert.toStr(getData_KDS_temp.get(0).get("UNCOOK"),"");
                                KDS_uncall=Convert.toStr(getData_KDS_temp.get(0).get("UNCALL"),"");
                            }
                        }
                    }
                }

                //加工任务明细商品状态,暂时只管免配菜吧，预制菜特殊
                String processTaskDetail_goodsStatus="0";
                if ("Y".equals(KDS_unside))
                {
                    processTaskDetail_goodsStatus="1";
                }


                tot_cqty++;
                String pluNo = lv2.getPluNo();
                String unitId = lv2.getUnitId();
                String categoryId = lv2.getCategoryId();
                String pluBarCode = lv2.getPluBarCode();
                String qty = lv2.getQty();
                int pqty = Integer.parseInt(qty);
                tot_pqty+=pqty;

                // 查询下商品的信息
                conditionValues = new String[]{langType,langType,langType,eId, pluNo, categoryId};
                List<Map<String, Object>> getPlunoDetail = this.doQueryData(sqlbuf.toString(), conditionValues);

                String plunoMult_sql = getPlunoMultQty(eId, shopId, pluNo);
                List<Map<String, Object>> getMuilQty = this.doQueryData(plunoMult_sql, null);
                String mulqty = "0";
                if(!org.apache.cxf.common.util.CollectionUtils.isEmpty(getMuilQty)){
                    mulqty = getMuilQty.get(0).get("MULQTY").toString();
                }
                conditionValues = null;
                if(!CollectionUtils.isEmpty(getPlunoDetail))
                {
                    Map<String, Object> plunoDetail = getPlunoDetail.get(0); // 这里应该只有一条数据
                    String pluno = plunoDetail.get("PLUNO").toString();
                    String plu_name = plunoDetail.get("PLU_NAME").toString();
                    String punit = plunoDetail.get("PUNIT").toString();
                    String pUName = plunoDetail.get("PUNAME").toString();
                    String baseUnit = plunoDetail.get("BASEUNIT").toString();
                    String baseUname = plunoDetail.get("BASEUNAME").toString();
                    String unitRatio = plunoDetail.get("UNITRATIO").toString();
                    String baseQty = plunoDetail.get("QTY").toString(); // 基准单位数量 单份

                    //单份数量1拆分到明细
                    for(int i =0;i<pqty;i++)
                    {
                        item++;
                        DataValue[] insValueDetail = new DataValue[]
                                {
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(processTaskNO, Types.VARCHAR),
                                        new DataValue(item, Types.VARCHAR),
                                        new DataValue(mulqty, Types.VARCHAR), // 倍量 默认0
                                        new DataValue(1, Types.VARCHAR), // 数量 以单份维度存储
                                        new DataValue(punit, Types.VARCHAR),
                                        new DataValue(baseQty, Types.VARCHAR),
                                        new DataValue(pluNo, Types.VARCHAR),
                                        new DataValue(plu_name, Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(baseUnit, Types.VARCHAR),
                                        new DataValue(unitRatio, Types.VARCHAR), // 单位转换率
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue(bDate, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(processTaskDetail_goodsStatus, Types.VARCHAR), // goodsStatus 菜品状态
                                        new DataValue(categoryId, Types.VARCHAR), // 末级分类
                                        new DataValue(pluBarCode, Types.VARCHAR), // 条码
                                        new DataValue("1", Types.VARCHAR) // 剩余可用数量
                                };
                        InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
                        ib1.addValues(insValueDetail);
                        this.addProcessData(new DataProcessBean(ib1));
                    }
                }

            }

            // 写单头
            String[] columns_Processtask = {
                    "SHOPID", "PROCESSTASKNO", "EID", "ORGANIZATIONNO", "CREATE_TIME", "CREATE_DATE", "CREATEBY",
                    "STATUS", "TOT_CQTY", "PROCESS_STATUS", "BDATE", "TOT_PQTY", "MEMO", "UPDATE_TIME", "WAREHOUSE",
                    "MATERIALWAREHOUSE", "OTYPE", "CREATEDATETIME", "TOT_AMT", "TOT_DISTRIAMT"
            };
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
            String createDate = dateFormat.format(now);//格式化然后放入字符串中
            dateFormat = new SimpleDateFormat("HHmmss");
            String createTime = dateFormat.format(now);
            String createDateTime = timeFormat.format(now);

            DataValue[] insValue = new DataValue[]
                    {
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(processTaskNO, Types.VARCHAR),
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(createTime, Types.VARCHAR),
                            new DataValue(createDate, Types.VARCHAR),
                            new DataValue(userId, Types.VARCHAR),
                            new DataValue("6", Types.VARCHAR), // status 默认6
                            new DataValue(tot_cqty, Types.VARCHAR), // status 默认6
                            new DataValue("N", Types.VARCHAR),
                            new DataValue(bDate, Types.VARCHAR), // TOT_CQTy
                            new DataValue(tot_pqty, Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),
                            new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR),
                            new DataValue(OUT_COST_WAREHOUSE, Types.VARCHAR),
                            new DataValue(OUT_COST_WAREHOUSE, Types.VARCHAR), // MATERIALWAREHOUSE 原料仓库 取默认出货仓库
                            new DataValue("BEFORE", Types.VARCHAR), // 单据类型 此处为预制单 Before
                            new DataValue(createDateTime, Types.VARCHAR), // 生产日期
                            new DataValue("0", Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR),
                    };

            InsBean ib2 = new InsBean("DCP_PROCESSTASK", columns_Processtask);
            ib2.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib2));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BeforeDishCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BeforeDishCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BeforeDishCreate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BeforeDishCreate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_BeforeDishCreate_OpenReq.level1Elm request = req.getRequest();
        List<DCP_BeforeDishCreate_OpenReq.level2Elm> goodsList = request.getGoodsList();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getUserId())) {
            errMsg.append("员工编号不能为空,");
            isFail = true;
        }
        if (Check.Null(request.getUserName())) {
            errMsg.append("员工名称不能为空,");
            isFail = true;
        }

        if (CollectionUtils.isEmpty(goodsList)) {
            errMsg.append("菜品列表不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BeforeDishCreate_OpenReq> getRequestType() {
        return new TypeToken<DCP_BeforeDishCreate_OpenReq>() {
        };
    }

    @Override
    protected DCP_BeforeDishCreate_OpenRes getResponseType() {
        return new DCP_BeforeDishCreate_OpenRes();
    }

    /**
     * 生成预制菜品编号
     *
     * @param req
     * @return
     */
    protected String getBeforNo(DCP_BeforeDishCreate_OpenReq req) {
        // 预制单号生成规则：YZD+[渠道类型]+[门店编号]+YYYYMMDD+6位随机数字；
        int i = (int) ((Math.random() * 9 + 1) * 100000); // 生成6位随机数
        String beforNo = "YZD" + req.getApiUser().getChannelId() + req.getRequest().getShopId() + new SimpleDateFormat("yyyyMMdd").format(new Date()) + i;
        return beforNo;
    }

    private String getProcessTaskNO(DCP_BeforeDishCreate_OpenReq req) throws Exception  {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String processTaskNO = null;
        String shopId = req.getRequest().getShopId();
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','JGRW') PROCESSTASKNO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            processTaskNO = (String) getQData.get(0).get("PROCESSTASKNO");
        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return processTaskNO;
    }

    protected StringBuffer getPlunoDetail() {
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.PLUNO, b.PLU_NAME, a.PUNIT, c.UNAME AS pUname, a.BASEUNIT , d.UNAME AS baseUname, e.UNITRATIO,e.QTY " +
                              " FROM DCP_GOODS a " +
                              " LEFT JOIN DCP_GOODS_LANG b ON a.EID = b.EID AND a.PLUNO = b.PLUNO AND b.LANG_TYPE = ? " +
                              " LEFT JOIN DCP_UNIT_LANG c ON a.EID = c.EID AND a.PUNIT = c.UNIT AND c.LANG_TYPE = ? " +
                              " LEFT JOIN DCP_UNIT_LANG d ON a.EID = d.EID AND a.BASEUNIT = d.UNIT AND d.LANG_TYPE = ? " +
                              " LEFT JOIN DCP_GOODS_UNIT e ON a.EID = e.EID AND a.PLUNO = e.PLUNO AND a.PUNIT = e.OUNIT AND a.BASEUNIT = e.UNIT " +
                              " WHERE a.EID = ? AND a.PLUNO = ? and a.CATEGORY = ?");
        return sqlbuf;
    }

    /**
     * 查询商品倍量
     * @param
     * @param pluno
     * @return
     */
    private String getPlunoMultQty(String eId,String shopId ,String pluno){
        String bomSql = ""
                + " select distinct a.pluno ,A.eid,  A.Bomno , A.bomType , A.Restrictshop , A.Pluno , A.UNIT , A.MULQTY , A.EFFDATE , "
                + " C.material_PluNo AS materialPluNo , C.material_Unit AS materialUnit , c.material_Qty AS materialQty , C.isBuckle ,"
                + " C.Qty , C.isreplace , c.sortId ,  to_char(B.SHOPID ) AS shopId , d.unitRatio ,"
                + " E.BOM_UNIT , E.Baseunit , E.sunit "
                + " from dcp_bom a "
                + " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"' "
                + " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno and trunc(c.material_bdate)<=trunc(sysdate) "
                + " and trunc(c.material_edate)>=trunc(sysdate) "
                + " inner join dcp_goods_unit d on a.eid=d.eid and a.pluno=d.pluno and a.unit=d.ounit and d.prod_unit_use='Y' "
                + " INNER JOIN Dcp_Goods e ON C.eId = E.EID AND C.MATERIAL_Pluno = E.Pluno AND E.Status = '100' "
                + " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and bomtype = '0'  "
                + " AND A.pluNo = '"+pluno+"'"
                + " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))";
        return bomSql;
    }

}
