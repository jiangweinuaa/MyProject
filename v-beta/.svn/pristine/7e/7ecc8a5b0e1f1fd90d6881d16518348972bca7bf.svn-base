package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.cust.req.DCP_POrderQueryReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_POrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_POrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：POrderGet
 *    說明：要货单查询
 * 服务说明：要货单查询
 * @author panjing
 * @since  2016-10-8
 */
public class DCP_POrderQuery extends SPosBasicService<DCP_POrderQueryReq, DCP_POrderQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_POrderQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        
        if (Check.Null(beginDate)) {
            errMsg.append("开始日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(endDate)) {
            errMsg.append("截止日期不可为空值, ");
            isFail = true;
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_POrderQueryReq> getRequestType() {
        return new TypeToken<DCP_POrderQueryReq>(){};
    }
    
    @Override
    protected DCP_POrderQueryRes getResponseType() {
        return new DCP_POrderQueryRes();
    }
    
    @Override
    protected DCP_POrderQueryRes processJson(DCP_POrderQueryReq req) throws Exception {
        
        //查詢資料
        DCP_POrderQueryRes res = this.getResponse();
        List<String> outPorderNos =new ArrayList<>();
        if("1".equals(req.getRequest().getSearchScope())) {
            String querySqlAll = this.getQuerySqlAll(req);
            List<Map<String, Object>> getQAllData = this.doQueryData(querySqlAll, null);
            //过滤掉有通知单且未过废 且数量未全部通知的
            if (CollUtil.isNotEmpty(getQAllData)) {
                MyCommon cm = new MyCommon();
                StringBuffer sJoinPorderNo = new StringBuffer("");
                for (Map<String, Object> map : getQAllData) {
                    String pOrderNo = map.get("PORDERNO").toString();
                    sJoinPorderNo.append(pOrderNo + ",");
                }
                Map<String, String> mapPorderNo = new HashMap<String, String>();
                mapPorderNo.put("PORDERNO", sJoinPorderNo.toString());

                String withasSql_porderno = "";
                withasSql_porderno = cm.getFormatSourceMultiColWith(mapPorderNo);
                mapPorderNo = null;

                String noticeSql = "with p AS ( " + withasSql_porderno + ") " +
                        " select distinct porderno from (" +
                        " select a.porderno,a.item,a.REVIEW_QTY,sum(nvl(b.pqty,0)) as pqty from dcp_porder_detail a" +
                        " inner join p on p.porderno=a.porderno " +
                        " inner join dcp_stockoutnotice_detail b on a.eid=b.eid and a.porderno=b.sourcebillno and a.item=b.oitem " +
                        " left join dcp_stockoutnotice c on b.eid=c.eid and b.billno=c.billno " +
                        " where c.status!='3' and  a.eid='" + req.geteId() + "'" +
                        " group by a.porderno,a.item,a.REVIEW_QTY" +
                        " ) a where a.REVIEW_QTY<=a.pqty ";
                List<Map<String, Object>> noticeList = this.doQueryData(noticeSql, null);
                outPorderNos = noticeList.stream().map(x -> x.get("PORDERNO").toString()).collect(Collectors.toList());

            }
        }

        //try {
            //给分页字段赋值
            String sql = this.getQuerySql_Count(req,outPorderNos);		//查询总笔数
            String[] conditionValues_Count = {  }; //查詢條件
            List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
            int totalRecords;								//总笔数
            int totalPages;									//总页数
            if (getQData_Count != null && !getQData_Count.isEmpty()) {
                Map<String, Object> oneData_Count = getQData_Count.get(0);
                String num = oneData_Count.get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
            }
            else
            {
                totalRecords = 0;
                totalPages = 0;
            }
            
            Calendar tempcalPre = Calendar.getInstance();//获得当前时间
            SimpleDateFormat tempdfPre=new SimpleDateFormat("yyyyMMdd");
            String _DATE = tempdfPre.format(tempcalPre.getTime());
            
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            res.setSysDate(_DATE);
            
            //单头
            sql = this.getQuerySql(req,outPorderNos);				//查询要货单单头数据
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty())
            {
                //【ID1020090】【货郎】要货单提交时，存在失效商品（ERP生命周期变化），提交提示停留，由顾客主动取消。2.要货单里可以提示失效商品。 by jinzma 20210827
                String companyId = req.getBELFIRM();
                if (Check.Null(companyId)) {
                    String getCompanySql=" select belfirm from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getShopId()+"' ";
                    List<Map<String, Object>> getQCompanyId = this.doQueryData(getCompanySql, null);
                    companyId = getQCompanyId.get(0).get("BELFIRM").toString();
                    
                }
                
                
                
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
                
                // 有資料，取得詳細內容
                //单号
                StringBuffer sJoinPorderno=new StringBuffer();
                //模板
                StringBuffer sJoinPtemplateno=new StringBuffer();
                
                for (Map<String, Object> mapAll : getQData)
                {
                    sJoinPorderno.append(mapAll.get("PORDERNO").toString()+",");
                    sJoinPtemplateno.append(mapAll.get("PTEMPLATENO").toString()+",");
                }
                
                //
                Map<String, String> map= new HashMap<>();
                map.put("PORDERNO", sJoinPorderno.toString());
                map.put("PTEMPLATENO", sJoinPtemplateno.toString());
                
                //
                MyCommon cm=new MyCommon();
                String withasSql_Porderno_Ptemplateno=cm.getFormatSourceMultiColWith(map);
                
                //处理==绑定变量SQL的写法
                List<DataValue> lstDV=new ArrayList<>();
                
                sql = this.getQueryDetailSql(req,withasSql_Porderno_Ptemplateno,lstDV,companyId);
                
                //List<Map<String, Object>> getQDetailData = this.executeQuerySQL_BindSQL(sql, lstDV);
                List<Map<String, Object>> getQDetailData = null;
                res.setDatas(new ArrayList<>());
                
                //【ID1018250】【3.0货郎】要货撤销无要货模板下也要支持
                String revoke_Day_para = PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(),"revoke_Day");
                String revoke_Time_para = PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(),"revoke_Time");
                
                for (Map<String, Object> oneData : getQData)
                {
                    DCP_POrderQueryRes.level1Elm oneLv1 = res.new level1Elm();
                    oneLv1.setDatas(new ArrayList<>());
                    
                    //【ID1018250】【3.0货郎】要货撤销无要货模板下也要支持
                    String pTemplateNO = oneData.get("PTEMPLATENO").toString();
                    String revoke_Day = oneData.get("REVOKE_DAY").toString();
                    String revoke_Time = oneData.get("REVOKE_TIME").toString();
                    
                    if (Check.Null(pTemplateNO)) {
                        revoke_Day = revoke_Day_para ;
                        revoke_Time = revoke_Time_para;
                    }
                    
                    // 取得第一層資料庫搜尋結果
                    String porderNO = oneData.get("PORDERNO").toString();
                    String processERPNo = oneData.get("PROCESSERPNO").toString();
                    String bDate = oneData.get("BDATE").toString();
                    String memo = oneData.get("MEMO").toString();
                    
                    String createBy =oneData.get("CREATEBY").toString();
                    String createByName =oneData.get("CREATENAME").toString();
                    String createDate =oneData.get("CREATEDATE").toString();
                    String createTime =oneData.get("CREATETIME").toString();
                    String modifyBy =oneData.get("MODIFYBY").toString();
                    String modifyByName =oneData.get("MODIFYNAME").toString();
                    String modifyDate =oneData.get("MODIFYDATE").toString();
                    String modifyTime =oneData.get("MODIFYTIME").toString();
                    String submitBy =oneData.get("SUBMITBY").toString();
                    String submitByName =oneData.get("SUBMITNAME").toString();
                    String submitDate =oneData.get("SUBMITDATE").toString();
                    String submitTime =oneData.get("SUBMITTIME").toString();
                    String confirmBy =oneData.get("CONFIRMBY").toString();
                    String confirmByName =oneData.get("CONFIRMNAME").toString();
                    String confirmDate =oneData.get("CONFIRMDATE").toString();
                    String confirmTime =oneData.get("CONFIRMTIME").toString();
                    String cancelBy =oneData.get("CANCELBY").toString();
                    String cancelByName =oneData.get("CANCELNAME").toString();
                    String cancelDate =oneData.get("CANCELDATE").toString();
                    String cancelTime =oneData.get("CANCELTIME").toString();
                    String accountBy =oneData.get("ACCOUNTBY").toString();
                    String accountByName =oneData.get("ACCOUNTNAME").toString();
                    String accountDate =oneData.get("ACCOUNTDATE").toString();
                    String accountTime =oneData.get("ACCOUNTTIME").toString();
                    String cal_type =oneData.get("CAL_TYPE").toString();
                    String materal_type=oneData.get("MATERAL_TYPE").toString();
                    String optionalTime = oneData.get("OPTIONAL_TIME").toString();
                    String OFNO = oneData.getOrDefault("OFNO","").toString();
                    String pTemplateName = oneData.get("PTEMPLATENAME").toString();
                    String preDay = oneData.get("PREDAY").toString();
                    String isAdd = oneData.get("ISADD").toString();
                    String status = oneData.get("STATUS").toString();
                    String totPqty = oneData.get("TOTPQTY").toString();
                    String totAmt = oneData.get("TOTAMT").toString();
                    String totDistriAmt = oneData.get("TOT_DISTRIAMT").toString();
                    
                    String uTotDistriAmt = oneData.get("UTOTDISTRIAMT").toString();
                    
                    String totCqty = oneData.get("TOTCQTY").toString();
                    String rDate = oneData.get("RDATE").toString();
                    String rTime = oneData.get("RTIME").toString();
                    String PRedictAMT = oneData.get("PREDICTAMT").toString();
                    if(PRedictAMT==null||PRedictAMT.equals(""))
                    {
                        PRedictAMT="0";
                    }
                    String BeginDate = oneData.get("BEGINDATE").toString();
                    String ISPRedict="Y";
                    if(BeginDate==null||BeginDate.equals("")) {
                        ISPRedict="N";
                    }
                    
                    String isForecast = oneData.getOrDefault("ISFORECAST","N").toString();
                    String EndDate = oneData.get("ENDDATE").toString();
                    String avgsaleAMT = oneData.get("AVGSALEAMT").toString();
                    if(avgsaleAMT==null||avgsaleAMT.equals(""))
                    {
                        avgsaleAMT="0";
                    }
                    String modifRatio = oneData.get("MODIFRATIO").toString();
                    if(modifRatio==null||modifRatio.equals(""))
                    {
                        modifRatio="0";
                    }
                    if (Check.Null(totDistriAmt)) totDistriAmt="0";
                    
                    if (Check.Null(uTotDistriAmt)) uTotDistriAmt="0";
                    
                    String reason = oneData.get("REASON").toString();
                    String isAddGoods = oneData.get("ISADDGOODS").toString();
                    if (Check.Null(isAddGoods) || !isAddGoods.equals("Y") )
                    {
                        isAddGoods="N";
                    }
                    String isShowHeadStockQty = oneData.get("ISSHOWHEADSTOCKQTY").toString();
                    if (Check.Null(isShowHeadStockQty)|| !isShowHeadStockQty.equals("Y"))
                        isShowHeadStockQty="N";
                    String receiptOrgNO = oneData.get("RECEIPT_ORG").toString();
                    
                    String oType = oneData.get("OTYPE").toString();
                    
                    if(Check.Null(oType)){
                        oType = "0";
                    }

                    String organizationNo = oneData.get("ORGANIZATIONNO").toString();
                    String organizationName = oneData.get("ORGANIZATIONNAME").toString();

                    // 處理調整回傳值；
                    oneLv1.setOrganizationNo(organizationNo);
                    oneLv1.setOrganizationName(organizationName);
                    oneLv1.setPorderNo(porderNO);
                    oneLv1.setProcessERPNo(processERPNo);
                    oneLv1.setbDate(bDate);
                    oneLv1.setMemo(memo);
                    oneLv1.setStatus(status);
                    oneLv1.setTotPqty(totPqty);
                    oneLv1.setTotAmt(totAmt);
                    oneLv1.setTotCqty(totCqty);
                    oneLv1.setTotDistriAmt(totDistriAmt);
                    
                    oneLv1.setuTotDistriAmt(uTotDistriAmt);
                    
                    oneLv1.setrDate(rDate);
                    oneLv1.setrTime(rTime);
                    oneLv1.setpTemplateNo(pTemplateNO);
                    oneLv1.setpTemplateName(pTemplateName);
                    oneLv1.setPreDay(preDay);
                    oneLv1.setIsAdd(isAdd);
                    oneLv1.setPRedictAMT(Double.parseDouble(PRedictAMT) );
                    oneLv1.setBeginDate(BeginDate);
                    oneLv1.setEndDate(EndDate);
                    oneLv1.setAvgsaleAMT(Double.parseDouble(avgsaleAMT));
                    oneLv1.setModifRatio(Double.parseDouble(modifRatio));
                    oneLv1.setISPRedict(ISPRedict);
                    oneLv1.setIsForecast(isForecast);
                    oneLv1.setOptionalTime(optionalTime);
                    oneLv1.setCreateBy(createBy);
                    oneLv1.setCreateByName(createByName);
                    oneLv1.setCreateDate(createDate);
                    oneLv1.setCreateTime(createTime);
                    oneLv1.setModifyBy(modifyBy);
                    oneLv1.setModifyByName(modifyByName);
                    oneLv1.setModifyDate(modifyDate);
                    oneLv1.setModifyTime(modifyTime);
                    oneLv1.setSubmitBy(submitBy);
                    oneLv1.setSubmitByName(submitByName);
                    oneLv1.setSubmitDate(submitDate);
                    oneLv1.setSubmitTime(submitTime);
                    oneLv1.setConfirmBy(confirmBy);
                    oneLv1.setConfirmByName(confirmByName);
                    oneLv1.setConfirmDate(confirmDate);
                    oneLv1.setConfirmTime(confirmTime);
                    oneLv1.setCancelBy(cancelBy);
                    oneLv1.setCancelByName(cancelByName);
                    oneLv1.setCancelDate(cancelDate);
                    oneLv1.setCancelTime(cancelTime);
                    oneLv1.setAccountBy(accountBy);
                    oneLv1.setAccountByName(accountByName);
                    oneLv1.setAccountDate(accountDate);
                    oneLv1.setAccountTime(accountTime);
                    oneLv1.setCal_type(cal_type);
                    oneLv1.setMateral_type(materal_type);
                    oneLv1.setRdate_Type(oneData.get("RDATE_TYPE").toString());
                    oneLv1.setRdate_Add(oneData.get("RDATE_ADD").toString());
                    oneLv1.setRdate_Values(oneData.get("RDATE_VALUES").toString());
                    oneLv1.setRevoke_Day(revoke_Day);
                    oneLv1.setRevoke_Time(revoke_Time);
                    oneLv1.setRdate_Times(oneData.get("RDATE_TIMES").toString());
                    oneLv1.setOfNo(OFNO);
                    String UPDATE_TIME;
                    oneLv1.setReceiptOrgNo(receiptOrgNO);
                    oneLv1.setReceiptOrgName(oneData.get("RECEIPTORGNAME").toString());
                    oneLv1.setSupplierType(oneData.get("SUPPLIERTYPE").toString());
                    oneLv1.setEmployeeID(oneData.get("EMPLOYEEID").toString());
					oneLv1.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
					oneLv1.setDepartID(oneData.get("DEPARTID").toString());
					oneLv1.setDepartName(oneData.get("DEPARTNAME").toString());
					oneLv1.setOwnOpID(oneData.get("OWNOPID").toString());
					oneLv1.setOwnOpName(oneData.get("OWNOPNAME").toString());
					oneLv1.setOwnDeptID(oneData.get("OWNDEPTID").toString());
					oneLv1.setOwnDeptName(oneData.get("OWNDEPTNAME").toString());
					oneLv1.setCreateDeptID(oneData.get("CREATEDEPTID").toString());
					oneLv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
                    
                    SimpleDateFormat simptemp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat allsimptemp=new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    if(oneData.get("UPDATE_TIME")==null||oneData.get("UPDATE_TIME").toString().isEmpty())
                    {
                        UPDATE_TIME=allsimptemp.format(Calendar.getInstance().getTime());
                    }
                    else
                    {
                        UPDATE_TIME=oneData.get("UPDATE_TIME").toString();
                    }
                    oneLv1.setUpdate_time(simptemp.format(allsimptemp.parse(UPDATE_TIME)));
                    oneLv1.setProcess_status(oneData.get("PROCESS_STATUS").toString());
                    oneLv1.setIsUrgentOrder(oneData.get("ISURGENTORDER") == null ? "N" : oneData.get("ISURGENTORDER").toString());
                    oneLv1.setReason(reason);
                    oneLv1.setIsAddGoods(isAddGoods);
                    oneLv1.setIsShowHeadStockQty(isShowHeadStockQty);
                    
                    
                    oneLv1.setoType(oType);
                    oneLv1.setStockCare("N");
                    oneLv1.setNotRepeatGoods("N");
                    
                    //
                    Map<String, Object> condiV= new HashMap<>();
                    condiV.put("PORDERNO", porderNO);
                    //List<Map<String, Object>> detailList= MapDistinct.getWhereMap(getQDetailData, condiV, true);
                    List<Map<String, Object>> detailList= null; //modi by 01029 20250217
                    if (detailList!=null && detailList.size()>0)
                    {
                        for (Map<String, Object> oneQDetailData : detailList)
                        {
                            DCP_POrderQueryRes.level2Elm oneLv2 = res.new level2Elm();
                            String item = oneQDetailData.get("ITEM").toString();
                            int i_item;
                            if (item == null || item.length()==0 )
                            {
                                i_item=0;
                            }
                            else
                            {
                                float f= Float.parseFloat(item);
                                
                                i_item=(int) Math.floor(f);
                            }
                            
                            String pluNO = oneQDetailData.get("PLUNO").toString();
                            String pluName = oneQDetailData.get("PLUNAME").toString();
                            String detail_memo = oneQDetailData.get("MEMO").toString();
                            String punit = oneQDetailData.get("PUNIT").toString();
                            String punitName = oneQDetailData.get("PUNITNAME").toString();
                            String price = oneQDetailData.get("PRICE").toString();
                            String pqty = oneQDetailData.get("PQTY").toString();
                            ///3.0
                            String listImage = oneQDetailData.get("LISTIMAGE").toString();
                            if (!Check.Null(listImage)){
                                listImage = domainName+listImage;
                            }
                            String spec = oneQDetailData.get("SPEC").toString();
                            String baseUnit = oneQDetailData.get("BASEUNIT").toString();
                            String baseUnitName = oneQDetailData.get("BASEUNITNAME").toString();
                            String baseQty = oneQDetailData.get("BASEQTY").toString();
                            String unitRatio = oneQDetailData.get("UNIT_RATIO").toString();
                            String featureNo = oneQDetailData.get("FEATURENO").toString();
                            String featureName = oneQDetailData.get("FEATURENAME").toString();
                            
                            // 新增原进货价
                            String uDistriPrice = oneQDetailData.get("UDISTRIPRICE").toString();
                            
                            //【ID1018735】【3.0货郎】移动门店新增新品查看和筛选功能  by jinzma 20210702
                            String isNewGoods = oneQDetailData.get("ISNEWGOODS").toString();
                            //【ID1018821】商品查询服务 by jinzma 20210702
                            String isHotGoods = oneQDetailData.get("ISHOTGOODS").toString();
                            
                            //新增配送价 BY JZMA 20190718
                            String distriPrice = oneQDetailData.get("DISTRIPRICE").toString();
                            String amt = oneQDetailData.get("AMT").toString();
                            String distriAmt = oneQDetailData.get("DISTRIAMT").toString();
                            if (Check.Null(distriAmt))
                                distriAmt="0";
                            String stockInqty = oneQDetailData.get("STOCKINQTY").toString();
                            if (Check.Null(stockInqty))
                                stockInqty="0";
                            String detailStatus = oneQDetailData.get("DETAILSTATUS").toString();
                            String minQty = oneQDetailData.get("MINQTY").toString();
                            float f_minQty;
                            if (minQty == null || minQty.length()==0 ){
                                f_minQty=0;
                            } else
                                f_minQty =  Float.parseFloat(minQty);
                            String maxQty = oneQDetailData.get("MAXQTY").toString();
                            float f_maxQty;
                            if (maxQty == null || maxQty.length()==0 ){
                                f_maxQty=9999;
                            } else
                                f_maxQty =  Float.parseFloat(maxQty);
                            String mulQty = oneQDetailData.get("MULQTY").toString();
                            float f_mulQty;
                            if (mulQty == null || mulQty.length()==0 ){
                                f_mulQty=0;
                            } else
                                f_mulQty =  Float.parseFloat(mulQty);
                            String refSQty = oneQDetailData.get("REFSQTY").toString();
                            float f_refSQty;
                            if (refSQty == null || refSQty.length()==0 ){
                                f_refSQty=0;
                            } else
                                f_refSQty =  Float.parseFloat(refSQty);
                            String refWQty = oneQDetailData.get("REFWQTY").toString();
                            float f_refWQty;
                            if (refWQty == null || refWQty.length()==0 ){
                                f_refWQty=0;
                            } else
                                f_refWQty =  Float.parseFloat(refWQty);
                            String refPQty = oneQDetailData.get("REFPQTY").toString();
                            float f_refPQty;
                            if (refPQty == null || refPQty.length()==0 ){
                                f_refPQty=0;
                            } else
                                f_refPQty =  Float.parseFloat(refPQty);
                            String soQty = oneQDetailData.get("SOQTY").toString();
                            float f_soQty;
                            if (soQty == null || soQty.length()==0 ){
                                f_soQty=0;
                            } else
                                f_soQty =  Float.parseFloat(soQty);
                            String propQty=oneQDetailData.get("PROPQTY").toString();
                            if(propQty==null||propQty.equals(""))
                            {
                                propQty="0";
                            }
                            String kQty=oneQDetailData.get("KQTY").toString();
                            float f_kQty=0;
                            if(!Check.Null(kQty))
                            {
                                f_kQty =  Float.parseFloat(kQty);
                            }
                            String kAdjQty=oneQDetailData.get("KADJQTY").toString();
                            float f_kAdjQty=0;
                            if(!Check.Null(kAdjQty))
                            {
                                f_kAdjQty =  Float.parseFloat(kAdjQty);
                            }
                            String propAdjQty=oneQDetailData.get("PROPADJQTY").toString();
                            float f_propAdjQty=0;
                            if(!Check.Null(propAdjQty))
                            {
                                f_propAdjQty =  Float.parseFloat(propAdjQty);
                            }
                            
                            String punitUDLength = "2";//默认2
                            String punitUDLength_db = oneQDetailData.get("PUNIT_UDLENGTH").toString();
                            try
                            {
                                int punitUDLength_i = Integer.parseInt(punitUDLength_db);
                                punitUDLength = punitUDLength_i+"";
                            }
                            catch (Exception ignored)
                            {
                            
                            }
                            String groupNO = oneQDetailData.get("GROUPNO").toString();
                            String groupType = oneQDetailData.get("GROUPTYPE").toString();
                            String groupReachCount = oneQDetailData.get("GROUPREACHCOUNT").toString();
                            String headStockQty = oneQDetailData.get("HEADSTOCKQTY").toString();
                            if (Check.Null(headStockQty))
                                headStockQty="0";
                            String canRequire  = oneQDetailData.get("CANREQUIRE").toString();
                            if (Check.Null(canRequire)){
                                canRequire="N";
                            }
                            
                            oneLv2.setGroupNo(groupNO);
                            oneLv2.setGroupType(groupType);
                            oneLv2.setGroupReachCount(groupReachCount);
                            
                            // 處理調整回傳值；
                            oneLv2.setItem(i_item);
                            oneLv2.setPluNo(pluNO);
                            oneLv2.setPluName(pluName);
                            oneLv2.setPunit(punit);
                            oneLv2.setPunitName(punitName);
                            oneLv2.setPqty(pqty);
                            oneLv2.setPrice(price);
                            oneLv2.setDistriPrice(distriPrice);
                            oneLv2.setuDistriPrice(uDistriPrice);
                            oneLv2.setAmt(amt);
                            oneLv2.setStockInqty(stockInqty);
                            oneLv2.setDetailStatus(detailStatus);
                            oneLv2.setMinQty(f_minQty);
                            oneLv2.setMaxQty(f_maxQty);
                            oneLv2.setMulQty(f_mulQty);
                            oneLv2.setRefSQty(f_refSQty);
                            oneLv2.setRefWQty(f_refWQty);
                            oneLv2.setRefPQty(f_refPQty);
                            oneLv2.setSoQty(f_soQty);
                            oneLv2.setPropQty(Float.parseFloat(propQty));
                            oneLv2.setMemo(detail_memo);
                            oneLv2.setkAdjQty(f_kAdjQty);
                            oneLv2.setkQty(f_kQty);
                            oneLv2.setPropAdjQty(f_propAdjQty);
                            oneLv2.setDistriAmt(distriAmt);
                            oneLv2.setPunitUdLength(punitUDLength);
                            oneLv2.setReview_Qty(oneQDetailData.get("REVIEW_QTY").toString());
                            oneLv2.setHeadStockQty(headStockQty);
                            oneLv2.setListImage(listImage);
                            oneLv2.setFeatureNo(featureNo);
                            oneLv2.setFeatureName(featureName);
                            oneLv2.setSpec(spec);
                            oneLv2.setBaseUnit(baseUnit);
                            oneLv2.setBaseUnitName(baseUnitName);
                            oneLv2.setBaseQty(baseQty);
                            //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221108
                            oneLv2.setBaseUnitUdLength(oneQDetailData.get("BASEUNITUDLENGTH").toString());
                            oneLv2.setUnitRatio(unitRatio);
                            oneLv2.setMaxOrderSpec(oneQDetailData.get("MAXORDERSPEC").toString());
                            oneLv2.setIsHotGoods(isHotGoods);
                            oneLv2.setIsNewGoods(isNewGoods);
                            oneLv2.setCanRequire(canRequire);
                            //【ID1033707】【潮品3.0】门店要货量管控：当前补货申请量+当前库存+配送在途+要货在途量不可超过设定库存上限值----服务端 by jinzma 20230607
                            oneLv2.setWarningQty(oneQDetailData.get("WARNINGQTY").toString());
                            
                            
                            oneLv1.getDatas().add(oneLv2);
                        }
                    }
                    res.getDatas().add(oneLv1);
                }
            }
            else
            {
                res.setDatas(new ArrayList<>());
            }
            return res;
        //}
        //catch (Exception e)
        //{
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
       // }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        //調整查出來的資料
    }
    
    @Override
    protected String getQuerySql(DCP_POrderQueryReq req) throws Exception {
        
        levelElm request = req.getRequest();
        StringBuffer sqlbuf = new StringBuffer();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String langType = req.getLangType();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String queryType = req.getRequest().getQueryType();
        String searchScope = request.getSearchScope();

        //2021-05-12 wangzyc 新增porderNO 用于精确查询 非必传
        String porderNo = request.getPOrderNo();
        
        ///新增日期类型dateType  枚举: bDate：单据日期,rDate：需求日期   BY JZMA 20200327
        String dateType = request.getDateType();
        if (Check.Null(dateType) || !dateType.equals("rDate"))  dateType="bDate";
        
        //計算起啟位置
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * pageSize);
        
        sqlbuf.append(""
                + "SELECT rn,EID,processERPNO,porderNO,SHOPID,pTemplateName,Optional_Time,preDay,bDate,memo,status"
                + " ,completeDate,totPqty,totAmt,TOT_DISTRIAMT,UTOTDISTRIAMT,totCqty,rDate,rTime,pTemplateNO,isAdd,PRedictAMT,BeginDate,"
                + " EndDate,avgsaleAMT,modifRatio,PROCESS_STATUS,UPDATE_TIME,ISURGENTORDER "
                + " ,CREATEBY,CREATEDATE,CREATETIME,CREATENAME"
                + " ,MODIFYBY,MODIFYDATE,MODIFYTIME,MODIFYNAME"
                + " ,SUBMITBY,SUBMITDATE,SUBMITTIME,SUBMITNAME"
                + " ,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,CONFIRMNAME"
                + " ,CANCELBY,CANCELDATE,CANCELTIME,CANCELNAME"
                + " ,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME,ACCOUNTNAME  "
                + " ,CAL_TYPE,MATERAL_TYPE,ISPREDICT,REASON , ISFORECAST ,OFNO "
                + " , rdate_type, rdate_add, rdate_values, revoke_day, revoke_time, rdate_times,ISADDGOODS,"
                + " ISSHOWHEADSTOCKQTY,RECEIPT_ORG , OTYPE"
                
                + " ,SUPPLIERTYPE,RECEIPTORGNAME,EMPLOYEEID , EMPLOYEENAME "
                + "  ,DEPARTID , DEPARTNAME  ,OWNOPID, OWNOPNAME   "
				+ "  ,OWNDEPTID, OWNDEPTNAME,CREATEDEPTID, CREATEDEPTNAME,organizationno,organizationname "
                + " from ( "
                + " SELECT ROWNUM rn,EID,processERPNO,porderNO,pTemplateName,Optional_Time,preDay,SHOPID,bDate,memo,status"
                + " ,completeDate,totPqty,totAmt,TOT_DISTRIAMT,UTOTDISTRIAMT,totCqty,rDate,rTime,ptemplateno,isadd,PRedictAMT,BeginDate,EndDate,"
                + " avgsaleAMT,modifRatio,PROCESS_STATUS,UPDATE_TIME,ISURGENTORDER "
                + " ,CREATEBY,CREATEDATE,CREATETIME,CREATENAME"
                + " ,MODIFYBY,MODIFYDATE,MODIFYTIME,MODIFYNAME"
                + " ,SUBMITBY,SUBMITDATE,SUBMITTIME,SUBMITNAME"
                + " ,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,CONFIRMNAME"
                + " ,CANCELBY,CANCELDATE,CANCELTIME,CANCELNAME"
                + " ,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME,ACCOUNTNAME"
                + " ,CAL_TYPE,MATERAL_TYPE,ISPREDICT,REASON,ISFORECAST , OFNO "
                + " , rdate_type, rdate_add, rdate_values, revoke_day, revoke_time, rdate_times,ISADDGOODS,"
                + " ISSHOWHEADSTOCKQTY,RECEIPT_ORG , OTYPE "
                + " ,SUPPLIERTYPE,RECEIPTORGNAME,EMPLOYEEID , EMPLOYEENAME "
                + "  ,DEPARTID , DEPARTNAME  ,OWNOPID, OWNOPNAME   "
				+ "  ,OWNDEPTID, OWNDEPTNAME,CREATEDEPTID, CREATEDEPTNAME,organizationno,organizationname  "
                + " from ( "
                + " SELECT distinct A.EID , a.process_erp_no as processERPNO ,A.PORDERNO as porderNO,C.PTEMPLATE_NAME AS pTemplateName,C.Optional_Time,"
                + " C.PRE_DAY AS preDay,A.SHOPID as SHOPID,A.BDATE as bDate,A.MEMO as memo,A.STATUS as status,"
                + " A.COMPLETE_DATE as completeDate,A.TOT_DISTRIAMT,A.UTOTDISTRIAMT,"
                + " A.TOT_PQTY as totPqty,A.TOT_AMT as totamt,A.TOT_CQTY as totcqty,a.rDate,a.rTime,is_add as isadd,a.ptemplateno,"
                + " a.PRedictAMT,a.BeginDate,a.EndDate,a.avgsaleAMT,a.modifRatio,A.PROCESS_STATUS,A.UPDATE_TIME,A.ISURGENTORDER  "
                + " ,A.CREATEBY AS CREATEBY,A.CREATE_DATE AS CREATEDATE,A.CREATE_TIME AS CREATETIME,b0.OP_NAME as CREATENAME"
                + " ,A.MODIFYBY AS MODIFYBY,A.MODIFY_DATE AS MODIFYDATE,A.MODIFY_TIME AS MODIFYTIME,b1.OP_NAME as MODIFYNAME"
                + " ,A.SUBMITBY AS SUBMITBY,A.SUBMIT_DATE AS SUBMITDATE,A.SUBMIT_TIME AS SUBMITTIME,b2.OP_NAME as SUBMITNAME"
                + " ,A.CONFIRMBY AS CONFIRMBY,A.CONFIRM_DATE AS CONFIRMDATE,A.CONFIRM_TIME AS CONFIRMTIME,b3.OP_NAME as CONFIRMNAME"
                + " ,A.CANCELBY AS CANCELBY,A.CANCEL_DATE AS CANCELDATE,A.CANCEL_TIME AS CANCELTIME,b4.OP_NAME as CANCELNAME"
                + " ,A.ACCOUNTBY AS ACCOUNTBY,A.ACCOUNT_DATE AS ACCOUNTDATE,A.ACCOUNT_TIME AS ACCOUNTTIME,b5.OP_NAME as ACCOUNTNAME "
                + " ,NVL(C.CAL_TYPE,'1') AS CAL_TYPE,NVL(C.MATERAL_TYPE,'1') AS MATERAL_TYPE "
                + " ,A.ISPREDICT AS ISPREDICT,A.REASON AS REASON, A.ISFORECAST,a.OFNO  "
                + " , c.rdate_type, c.rdate_add, c.rdate_values, c.revoke_day, c.revoke_time, c.rdate_times,c.ISADDGOODS,"
                + " c.ISSHOWHEADSTOCKQTY,c.RECEIPT_ORG  , A.OTYPE "
                + " ,a.SUPPLIERTYPE,J1.ORG_NAME AS RECEIPTORGNAME,a.organizationno,j2.org_name as organizationname,a.EMPLOYEEID , p1.NAME as EMPLOYEENAME "
                + "  ,a.DEPARTID , d5.DEPARTNAME  ,a.OWNOPID, p4.NAME as OWNOPNAME   "
				+ "  ,d4.DEPARTNO as OWNDEPTID, d4.DEPARTNAME as OWNDEPTNAME,a.CREATEDEPTID,d.DEPARTNAME as CREATEDEPTNAME"
                + " FROM DCP_PORDER A "
                + " LEFT JOIN DCP_PTEMPLATE C ON A.EID=C.EID AND A.PTEMPLATENO=C.PTEMPLATENO AND DOC_TYPE='0' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b0 ON A.EID=b0.EID AND A.CREATEBY=b0.OPNO AND b0.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b1 ON A.EID=b1.EID AND A.modifyby=b1.OPNO AND b1.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b2 ON A.EID=b2.EID AND A.SubmitBy=b2.OPNO AND b2.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b3 ON A.EID=b3.EID AND A.ConfirmBy=b3.OPNO AND b3.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b4 ON A.EID=b4.EID AND A.CancelBy=b4.OPNO AND b4.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b5 ON A.EID=b5.EID AND A.AccountBy=b5.OPNO AND b5.LANG_TYPE='"+langType+"' "
                
                + " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEBY "
                +"  left join DCP_DEPARTMENT_LANG d  on a.eid=d.eid and a.CREATEDEPTID=d.DEPARTNO and d.lang_type='"+langType+"' "
            	+ " left join DCP_EMPLOYEE  p1 on a.eid=p1.eid and p1.EMPLOYEENO=a.EMPLOYEEID "
				+ "  left join DCP_DEPARTMENT_LANG d1  on p1.eid=d1.eid and p1.DEPARTMENTNO=d1.DEPARTNO and d1.lang_type='"+langType+"' "
				 
				+ " left join DCP_EMPLOYEE  p4 on a.eid=p4.eid and p4.EMPLOYEENO=a.OWNOPID "
				+"  left join DCP_DEPARTMENT_LANG d4  on a.eid=d4.eid and a.OWNDEPTID=d4.DEPARTNO and d4.lang_type='"+langType+"' "
				//+ " left join DCP_EMPLOYEE  p5 on a.eid=p5.eid and p5.EMPLOYEENO=a.DEPARTID "
				+"  left join DCP_DEPARTMENT_LANG d5  on a.eid=d5.eid and a.DEPARTID=d5.DEPARTNO and d5.lang_type='"+langType+"' "
				+"   LEFT JOIN DCP_ORG_LANG j1 on j1.EID=c.EID AND j1.ORGANIZATIONNO=c.RECEIPT_ORG AND j1.LANG_TYPE='" + langType +"' " +
                        " left join dcp_org_lang j2 on j2.eid=a.eid and j2.organizationno=a.organizationno and j2.lang_type='"+langType+"' "
        		);
        if("1".equals(searchScope)){
            sqlbuf.append(" left join DCP_PORDER_DETAIL pd on pd.eid=a.eid and pd.organizationno=a.organizationno and pd.porderno=a.porderno " +
                    " left join DCP_STOCKOUTNOTICE_DETAIL sd on sd.eid=a.eid  and sd.sourcebillno=pd.porderno and sd.oitem=pd.item "+
                    " left join dcp_stockoutnotice ds on ds.eid=a.eid and ds.billno=sd.billno  "
            );
        }
        if (dateType.equals("bDate"))
            sqlbuf.append(" WHERE A.BDATE BETWEEN "+beginDate +" AND " +endDate+ " ");
        if (dateType.equals("rDate"))
            sqlbuf.append(" WHERE A.RDATE BETWEEN "+beginDate +" AND " +endDate+ " ");
        
        //2021-05-12 wangzyc 新增porderNO 用于精确查询 非必传
        if(!Check.Null(porderNo)){
            sqlbuf.append(" AND a.PORDERNO = '"+porderNo+"'");
        }
        if(Check.NotNull(request.getPTemplateNo())){
            sqlbuf.append(" AND a.PTEMPLATENO = '"+request.getPTemplateNo()+"'");
        }

        if(!Check.Null(request.getReceiptOrgNo())){
            sqlbuf.append(" AND a.RECEIPT_ORG='"+request.getReceiptOrgNo()+"' ");
        }
        if(!Check.Null(request.getSupplierType())){
            sqlbuf.append(" AND a.SUPPLIERTYPE='"+request.getSupplierType()+"' ");
        }
        if("1".equals(searchScope)){
            sqlbuf.append( " and  (sd.billno is null or ds.status='3' or pd.REVIEW_QTY-nvl(sd.pqty,0)>0) ");
        }
        
        if (status != null) {
            sqlbuf.append("  AND a.status = '"+status+"' ");
        }

        if("Y".equals(req.getRequest().getIsInclTemplate())){
            sqlbuf.append(" and a.PTEMPLATENO  is not null ");
        }
        if("N".equals(req.getRequest().getIsInclTemplate())){
            sqlbuf.append(" and a.PTEMPLATENO  is  null ");
        }

        String[] orgList = req.getRequest().getOrgList();
        if(orgList!=null&&orgList.length>0){
            String orgStr = "";
            for(String s:orgList){
                orgStr+="'"+s+"',";
            }
            orgStr=orgStr.substring(0,orgStr.length()-1);
            sqlbuf.append(" and a.organizationno in ("+orgStr+") ");
        }

        if (keyTxt != null && keyTxt.length()!=0) {
            sqlbuf.append(""
                    + "AND (A.TOT_AMT like '%%"+ keyTxt +"%%'  "
                    + "OR  A.TOT_PQTY like '%%"+ keyTxt +"%%'   "
                    + "OR  A.PORDERNO like '%%"+ keyTxt +"%%'    "
                    + "OR  A.MEMO like '%%"+ keyTxt +"%%'   "
                    + "OR  C.PTEMPLATENO like '%%"+ keyTxt +"%%'  "
                    + "OR  C.PTEMPLATE_NAME like '%%"+ keyTxt +"%%' "
                    + " OR  A.process_ERP_No like '%%"+keyTxt+"%%')   "
            );
        }
        sqlbuf.append(" 	 AND A.EID='"+eId+"' ");
        if(Check.Null(queryType)||"0".equals(queryType)){
            sqlbuf.append(" AND A.SHOPID='"+shopId+"'");
        }else if("1".equals(queryType)){
            sqlbuf.append(" AND a.RECEIPT_ORG='"+req.getOrganizationNO()+"' ");
        }
        if (dateType.equals("bDate"))
            sqlbuf.append(" order by status ASC,bDate DESC, porderNO DESC ");
        if (dateType.equals("rDate"))
            sqlbuf.append(" order by status ASC,rDate DESC, porderNO DESC ");
        
        sqlbuf.append(" ) TBL ");
        sqlbuf.append(") where 1=1 AND (rn > " + startRow + " AND rn <= " + (startRow+pageSize) + " ) ");
        
        
        return sqlbuf.toString();
        
    }

    protected String getQuerySql(DCP_POrderQueryReq req,List<String> outPorderNos ) throws Exception {

        levelElm request = req.getRequest();
        StringBuffer sqlbuf = new StringBuffer();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String langType = req.getLangType();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String queryType = req.getRequest().getQueryType();
        String searchScope = request.getSearchScope();

        //2021-05-12 wangzyc 新增porderNO 用于精确查询 非必传
        String porderNo = request.getPOrderNo();

        ///新增日期类型dateType  枚举: bDate：单据日期,rDate：需求日期   BY JZMA 20200327
        String dateType = request.getDateType();
        if (Check.Null(dateType) || !dateType.equals("rDate"))  dateType="bDate";

        //計算起啟位置
        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * pageSize);

        sqlbuf.append(""
                + "SELECT rn,EID,processERPNO,porderNO,SHOPID,pTemplateName,Optional_Time,preDay,bDate,memo,status"
                + " ,completeDate,totPqty,totAmt,TOT_DISTRIAMT,UTOTDISTRIAMT,totCqty,rDate,rTime,pTemplateNO,isAdd,PRedictAMT,BeginDate,"
                + " EndDate,avgsaleAMT,modifRatio,PROCESS_STATUS,UPDATE_TIME,ISURGENTORDER "
                + " ,CREATEBY,CREATEDATE,CREATETIME,CREATENAME"
                + " ,MODIFYBY,MODIFYDATE,MODIFYTIME,MODIFYNAME"
                + " ,SUBMITBY,SUBMITDATE,SUBMITTIME,SUBMITNAME"
                + " ,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,CONFIRMNAME"
                + " ,CANCELBY,CANCELDATE,CANCELTIME,CANCELNAME"
                + " ,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME,ACCOUNTNAME  "
                + " ,CAL_TYPE,MATERAL_TYPE,ISPREDICT,REASON , ISFORECAST ,OFNO "
                + " , rdate_type, rdate_add, rdate_values, revoke_day, revoke_time, rdate_times,ISADDGOODS,"
                + " ISSHOWHEADSTOCKQTY,RECEIPT_ORG , OTYPE"

                + " ,SUPPLIERTYPE,RECEIPTORGNAME,EMPLOYEEID , EMPLOYEENAME "
                + "  ,DEPARTID , DEPARTNAME  ,OWNOPID, OWNOPNAME   "
                + "  ,OWNDEPTID, OWNDEPTNAME,CREATEDEPTID, CREATEDEPTNAME,organizationno,organizationname "
                + " from ( "
                + " SELECT ROWNUM rn,EID,processERPNO,porderNO,pTemplateName,Optional_Time,preDay,SHOPID,bDate,memo,status"
                + " ,completeDate,totPqty,totAmt,TOT_DISTRIAMT,UTOTDISTRIAMT,totCqty,rDate,rTime,ptemplateno,isadd,PRedictAMT,BeginDate,EndDate,"
                + " avgsaleAMT,modifRatio,PROCESS_STATUS,UPDATE_TIME,ISURGENTORDER "
                + " ,CREATEBY,CREATEDATE,CREATETIME,CREATENAME"
                + " ,MODIFYBY,MODIFYDATE,MODIFYTIME,MODIFYNAME"
                + " ,SUBMITBY,SUBMITDATE,SUBMITTIME,SUBMITNAME"
                + " ,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,CONFIRMNAME"
                + " ,CANCELBY,CANCELDATE,CANCELTIME,CANCELNAME"
                + " ,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME,ACCOUNTNAME"
                + " ,CAL_TYPE,MATERAL_TYPE,ISPREDICT,REASON,ISFORECAST , OFNO "
                + " , rdate_type, rdate_add, rdate_values, revoke_day, revoke_time, rdate_times,ISADDGOODS,"
                + " ISSHOWHEADSTOCKQTY,RECEIPT_ORG , OTYPE "
                + " ,SUPPLIERTYPE,RECEIPTORGNAME,EMPLOYEEID , EMPLOYEENAME "
                + "  ,DEPARTID , DEPARTNAME  ,OWNOPID, OWNOPNAME   "
                + "  ,OWNDEPTID, OWNDEPTNAME,CREATEDEPTID, CREATEDEPTNAME,organizationno,organizationname  "
                + " from ( "
                + " SELECT distinct A.EID , a.process_erp_no as processERPNO ,A.PORDERNO as porderNO,C.PTEMPLATE_NAME AS pTemplateName,C.Optional_Time,"
                + " C.PRE_DAY AS preDay,A.SHOPID as SHOPID,A.BDATE as bDate,A.MEMO as memo,A.STATUS as status,"
                + " A.COMPLETE_DATE as completeDate,A.TOT_DISTRIAMT,A.UTOTDISTRIAMT,"
                + " A.TOT_PQTY as totPqty,A.TOT_AMT as totamt,A.TOT_CQTY as totcqty,a.rDate,a.rTime,is_add as isadd,a.ptemplateno,"
                + " a.PRedictAMT,a.BeginDate,a.EndDate,a.avgsaleAMT,a.modifRatio,A.PROCESS_STATUS,A.UPDATE_TIME,A.ISURGENTORDER  "
                + " ,A.CREATEBY AS CREATEBY,A.CREATE_DATE AS CREATEDATE,A.CREATE_TIME AS CREATETIME,b0.OP_NAME as CREATENAME"
                + " ,A.MODIFYBY AS MODIFYBY,A.MODIFY_DATE AS MODIFYDATE,A.MODIFY_TIME AS MODIFYTIME,b1.OP_NAME as MODIFYNAME"
                + " ,A.SUBMITBY AS SUBMITBY,A.SUBMIT_DATE AS SUBMITDATE,A.SUBMIT_TIME AS SUBMITTIME,b2.OP_NAME as SUBMITNAME"
                + " ,A.CONFIRMBY AS CONFIRMBY,A.CONFIRM_DATE AS CONFIRMDATE,A.CONFIRM_TIME AS CONFIRMTIME,b3.OP_NAME as CONFIRMNAME"
                + " ,A.CANCELBY AS CANCELBY,A.CANCEL_DATE AS CANCELDATE,A.CANCEL_TIME AS CANCELTIME,b4.OP_NAME as CANCELNAME"
                + " ,A.ACCOUNTBY AS ACCOUNTBY,A.ACCOUNT_DATE AS ACCOUNTDATE,A.ACCOUNT_TIME AS ACCOUNTTIME,b5.OP_NAME as ACCOUNTNAME "
                + " ,NVL(C.CAL_TYPE,'1') AS CAL_TYPE,NVL(C.MATERAL_TYPE,'1') AS MATERAL_TYPE "
                + " ,A.ISPREDICT AS ISPREDICT,A.REASON AS REASON, A.ISFORECAST,a.OFNO  "
                + " , c.rdate_type, c.rdate_add, c.rdate_values, c.revoke_day, c.revoke_time, c.rdate_times,c.ISADDGOODS,"
                + " c.ISSHOWHEADSTOCKQTY,c.RECEIPT_ORG  , A.OTYPE "
                + " ,a.SUPPLIERTYPE,J1.ORG_NAME AS RECEIPTORGNAME,a.organizationno,j2.org_name as organizationname,a.EMPLOYEEID , p1.NAME as EMPLOYEENAME "
                + "  ,a.DEPARTID , d5.DEPARTNAME  ,a.OWNOPID, p4.NAME as OWNOPNAME   "
                + "  ,d4.DEPARTNO as OWNDEPTID, d4.DEPARTNAME as OWNDEPTNAME,a.CREATEDEPTID,d.DEPARTNAME as CREATEDEPTNAME"
                + " FROM DCP_PORDER A "
                + " LEFT JOIN DCP_PTEMPLATE C ON A.EID=C.EID AND A.PTEMPLATENO=C.PTEMPLATENO AND DOC_TYPE='0' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b0 ON A.EID=b0.EID AND A.CREATEBY=b0.OPNO AND b0.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b1 ON A.EID=b1.EID AND A.modifyby=b1.OPNO AND b1.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b2 ON A.EID=b2.EID AND A.SubmitBy=b2.OPNO AND b2.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b3 ON A.EID=b3.EID AND A.ConfirmBy=b3.OPNO AND b3.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b4 ON A.EID=b4.EID AND A.CancelBy=b4.OPNO AND b4.LANG_TYPE='"+langType+"' "
                + " LEFT JOIN PLATFORM_STAFFS_LANG b5 ON A.EID=b5.EID AND A.AccountBy=b5.OPNO AND b5.LANG_TYPE='"+langType+"' "

                + " left join DCP_EMPLOYEE  p on a.eid=p.eid and p.EMPLOYEENO=a.CREATEBY "
                +"  left join DCP_DEPARTMENT_LANG d  on a.eid=d.eid and a.CREATEDEPTID=d.DEPARTNO and d.lang_type='"+langType+"' "
                + " left join DCP_EMPLOYEE  p1 on a.eid=p1.eid and p1.EMPLOYEENO=a.EMPLOYEEID "
                + "  left join DCP_DEPARTMENT_LANG d1  on p1.eid=d1.eid and p1.DEPARTMENTNO=d1.DEPARTNO and d1.lang_type='"+langType+"' "

                + " left join DCP_EMPLOYEE  p4 on a.eid=p4.eid and p4.EMPLOYEENO=a.OWNOPID "
                +"  left join DCP_DEPARTMENT_LANG d4  on a.eid=d4.eid and a.OWNDEPTID=d4.DEPARTNO and d4.lang_type='"+langType+"' "
                //+ " left join DCP_EMPLOYEE  p5 on a.eid=p5.eid and p5.EMPLOYEENO=a.DEPARTID "
                +"  left join DCP_DEPARTMENT_LANG d5  on a.eid=d5.eid and a.DEPARTID=d5.DEPARTNO and d5.lang_type='"+langType+"' "
                +"   LEFT JOIN DCP_ORG_LANG j1 on j1.EID=c.EID AND j1.ORGANIZATIONNO=c.RECEIPT_ORG AND j1.LANG_TYPE='" + langType +"' " +
                " left join dcp_org_lang j2 on j2.eid=a.eid and j2.organizationno=a.organizationno "
        );
        //if("1".equals(searchScope)){
        //    sqlbuf.append(" left join DCP_PORDER_DETAIL pd on pd.eid=a.eid and pd.organizationno=a.organizationno and pd.porderno=a.porderno " +
        //            " left join DCP_STOCKOUTNOTICE_DETAIL sd on sd.eid=a.eid  and sd.sourcebillno=pd.porderno and sd.oitem=pd.item "+
        //            " left join dcp_stockoutnotice ds on ds.eid=a.eid and ds.billno=sd.billno  "
        //    );
        //}
        if (dateType.equals("bDate"))
            sqlbuf.append(" WHERE A.BDATE BETWEEN "+beginDate +" AND " +endDate+ " ");
        if (dateType.equals("rDate"))
            sqlbuf.append(" WHERE A.RDATE BETWEEN "+beginDate +" AND " +endDate+ " ");

        //2021-05-12 wangzyc 新增porderNO 用于精确查询 非必传
        if(!Check.Null(porderNo)){
            sqlbuf.append(" AND a.PORDERNO = '"+porderNo+"'");
        }
        if(Check.NotNull(request.getPTemplateNo())){
            sqlbuf.append(" AND a.PTEMPLATENO = '"+request.getPTemplateNo()+"'");
        }

        if(!Check.Null(request.getReceiptOrgNo())){
            sqlbuf.append(" AND a.RECEIPT_ORG='"+request.getReceiptOrgNo()+"' ");
        }
        if(!Check.Null(request.getSupplierType())){
            sqlbuf.append(" AND a.SUPPLIERTYPE='"+request.getSupplierType()+"' ");
        }
        //if("1".equals(searchScope)){
        //    sqlbuf.append( " and  (sd.billno is null or ds.status='3' or pd.REVIEW_QTY-nvl(sd.pqty,0)>0) ");
        //}

        if (status != null) {
            sqlbuf.append("  AND a.status = '"+status+"' ");
        }

        if("Y".equals(req.getRequest().getIsInclTemplate())){
            sqlbuf.append(" and a.PTEMPLATENO  is not null ");
        }
        if("N".equals(req.getRequest().getIsInclTemplate())){
            sqlbuf.append(" and a.PTEMPLATENO  is  null ");
        }

        String[] orgList = req.getRequest().getOrgList();
        if(orgList!=null&&orgList.length>0){
            String orgStr = "";
            for(String s:orgList){
                orgStr+="'"+s+"',";
            }
            orgStr=orgStr.substring(0,orgStr.length()-1);
            sqlbuf.append(" and a.organizationno in ("+orgStr+") ");
        }

        if(outPorderNos.size()>0){
            String pOrderStr = "";
            for(String s:outPorderNos){
                pOrderStr+="'"+s+"',";
            }
            pOrderStr=pOrderStr.substring(0,pOrderStr.length()-1);
            sqlbuf.append(" and a.porderno not in ("+pOrderStr+") ");
        }

        if (keyTxt != null && keyTxt.length()!=0) {
            sqlbuf.append(""
                    + "AND (A.TOT_AMT like '%%"+ keyTxt +"%%'  "
                    + "OR  A.TOT_PQTY like '%%"+ keyTxt +"%%'   "
                    + "OR  A.PORDERNO like '%%"+ keyTxt +"%%'    "
                    + "OR  A.MEMO like '%%"+ keyTxt +"%%'   "
                    + "OR  C.PTEMPLATENO like '%%"+ keyTxt +"%%'  "
                    + "OR  C.PTEMPLATE_NAME like '%%"+ keyTxt +"%%' "
                    + " OR  A.process_ERP_No like '%%"+keyTxt+"%%')   "
            );
        }
        sqlbuf.append(" 	 AND A.EID='"+eId+"' ");
        if(Check.Null(queryType)||"0".equals(queryType)){
            sqlbuf.append(" AND A.SHOPID='"+shopId+"'");
        }else if("1".equals(queryType)){
            sqlbuf.append(" AND a.RECEIPT_ORG='"+req.getOrganizationNO()+"' ");
        }
        if (dateType.equals("bDate"))
            sqlbuf.append(" order by status ASC,bDate DESC, porderNO DESC ");
        if (dateType.equals("rDate"))
            sqlbuf.append(" order by status ASC,rDate DESC, porderNO DESC ");

        sqlbuf.append(" ) TBL ");
        sqlbuf.append(") where 1=1 AND (rn > " + startRow + " AND rn <= " + (startRow+pageSize) + " ) ");


        return sqlbuf.toString();

    }
    
    private String getQueryDetailSql(DCP_POrderQueryReq req,String withasPorder_Ptemplateno,List<DataValue> lstDV,String companyId) {
        StringBuffer sqlbuf = new StringBuffer();
        String shopId = req.getShopId();
        String eId =req.geteId();
        String langType = req.getLangType();
        
        //绑定变量=SQL处理
        if (lstDV == null) {
            lstDV=new ArrayList<>();
        }
        DataValue dv;
        
        /*sqlbuf.append( " with goodstemplate as ("
                + " select b.pluno as goodstemplate_pluno,b.canrequire from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                + " where a.eid='"+eId+"' and a.status='100'"
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 and b.canrequire='Y' )");*/
        
        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.pluno as goodstemplate_pluno,b.canrequire,b.warningqty from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"') and b.canrequire='Y'  "
                + " )"
                + " ");
        
        sqlbuf.append(""
                + "SELECT  porderno,item,pluNO ,pluName,baseUnit, punit,pqty,BASEQTY,UNIT_RATIO,price,DISTRIPRICE,UDISTRIPRICE,amt,"
                + " punitName,baseUnitName,stockInqty ,detailStatus,minQty,mulQty,maxQty,refSQty,refWQty,refPQty,soQty,propQty,"
                + " memo,kqty,kadjqty,propadjqty,DISTRIAMT,punit_udlength,groupNO,groupType,groupReachCount,REVIEW_QTY,HEADSTOCKQTY,"
                + " featurename,spec,listimage,featureno,MAXORDERSPEC,isnewgoods,ishotgoods,goodstemplate.canrequire,baseunitudlength,"
                + " goodstemplate.warningqty  "
                + " from ( "
                + " with p1 as ("+withasPorder_Ptemplateno +") "
                + "	SELECT  a.porderno,a.ITEM,a.PLUNO ,b.maxorderspec,c.PLU_NAME as pluName, a.baseUnit, a.PUNIT,a.PQTY,a.BASEQTY,"
                + " a.UNIT_RATIO,a.price,a.DISTRIPRICE,UDISTRIPRICE,a.amt,d.UNAME as punitName, dw.UNAME as baseUnitName,"
                + " a.stockin_qty as stockInqty ,a.detail_status as detailStatus ,nvl(e.minQTY,a.min_qty) as minqty,"
                + " nvl(e.mulQty,a.mul_Qty) as mulqty,nvl(e.maxQty,a.max_Qty) as maxqty,a.isnewgoods,a.ishotgoods, "
                + " a.ref_sqty as refsqty,a.ref_wqty as refwqty,a.ref_pqty as refpqty,a.so_qty as soqty,a.propQty,a.memo,"
                + " a.kqty,a.kadjqty,a.propadjqty,a.DISTRIAMT,h.udlength as punit_udlength, gr.groupNO,gr.groupType,"
                + " gr.groupReachCount,a.REVIEW_QTY,a.HEADSTOCKQTY,fn.featurename,gul.spec,image.listimage,a.featureno,"
                + " bul.udlength as baseunitudlength "
                + " FROM DCP_PORDER_DETAIL a "
                + " INNER JOIN p1 on p1.porderno=a.porderno "
                + " INNER JOIN DCP_GOODS b ON a.PLUNO=b.PLUNO AND a.EID=b.EID "
                + " LEFT JOIN DCP_GOODS_LANG  c ON a.PLUNO=c.PLUNO AND  a.EID=c.EID  and c.LANG_TYPE=? "
                + "	LEFT JOIN DCP_UNIT_LANG d ON a.PUNIT=d.UNIT AND a.EID=d.EID and d.LANG_TYPE=? "
                + "	LEFT JOIN DCP_UNIT_LANG dw ON a.baseUnit=dw.UNIT AND a.EID=dw.EID and dw.LANG_TYPE=? "
                + " LEFT JOIN DCP_UNIT H ON a.PUNIT=H.UNIT AND a.EID=H.EID "
                //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221108
                + " left join dcp_unit bul on a.eid=bul.eid and a.baseunit=bul.unit"
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and a.pluno=fn.pluno and a.featureno=fn.featureno"
                + " and fn.lang_type=? "
                + " left join DCP_GOODS_UNIT_LANG gul on a.eid=gul.eid and a.pluno=gul.pluno and a.punit=gul.ounit and gul.lang_type=? "
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL' "
                + " LEFT  JOIN "
                + "("
                + "select EID, pluno,a.PTEMPLATENO,max(min_qty) as minqty,max(mul_qty) as mulqty,max(max_qty) as maxqty "
                + "from "
                + "(select distinct PTEMPLATENO from  p1 where PTEMPLATENO<>' ') p1 "
                + "INNER JOIN DCP_ptemplate_detail a on a.ptemplateno=p1.PTEMPLATENO "
                + " where EID=? and doc_type='0'  and status='100' "
                + " group by pluno,EID,a.PTEMPLATENO "
                + ") e on  a.pluno=e.pluno and a.EID=e.EID and p1.PTEMPLATENO=e.PTEMPLATENO "
                + " LEFT JOIN "
                + "(SELECT A.EID,A.pluno,A.porder_group as groupNO,b.grouptype as groupType,NVL(B.reachcount,0) as groupReachCount,a.PTEMPLATENO "
                + " from "
                + "(select distinct PTEMPLATENO from  p1 where PTEMPLATENO<>' ') p1 "
                + " INNER JOIN DCP_ptemplate_detail  A  on A.ptemplateno=p1.PTEMPLATENO "
                + " LEFT join DCP_ptemplate_detail_group B on A.EID=B.EID and A.PORDER_GROUP=B.GROUPNO and b.status='100' "
                + " WHERE A.EID=? and a.status='100' and a.doc_type='0' "
                + ") gr "
                + " on a.pluno=gr.pluno and a.EID=gr.EID and p1.PTEMPLATENO=gr.PTEMPLATENO "
                + " WHERE a.SHOPID=? AND a.EID=?	AND c.LANG_TYPE=? AND d.LANG_TYPE=? "
                + " order by a.porderno,a.item asc "
                + " ) TBL "
                + " left join goodstemplate on TBL.pluNO=goodstemplate.goodstemplate_pluno"
        
        );
        
        //?问号参数赋值处理
        dv=new DataValue(langType, Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(eId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(eId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(shopId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(eId,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        //?问号参数赋值处理
        dv=new DataValue(langType,Types.VARCHAR);
        lstDV.add(dv);
        
        return sqlbuf.toString();
    }
    
    protected String getQuerySql_Count(DCP_POrderQueryReq req,List<String> outPorderNos) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        levelElm request = req.getRequest();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String shopId=req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String isTemplateControl = request.getIsTemplateControl();
        String searchScope = request.getSearchScope();
        String queryType = request.getQueryType();

        //2021-05-12 wangzyc 新增porderNo 用于精确查询 非必传
        String porderNo = request.getPOrderNo();
        
        ///新增日期类型dateType  枚举: bDate：单据日期,rDate：需求日期   BY JZMA 20200327
        String dateType = request.getDateType();
        if (Check.Null(dateType) || !dateType.equals("rDate"))  dateType="bDate";
        
        sqlbuf.append(""
                + "SELECT num "
                + " from ("
                + " SELECT count(*) as num "
                + " FROM ("
                + " SELECT distinct A.PORDERNO as porderNO,A.SHOPID as SHOPID,A.BDATE as bDate,A.MEMO as memo,A.STATUS as status,A.CREATEBY as createBy,"
                + " A.CREATE_DATE as createDate,A.CREATE_TIME as createTime,A.SUBMITBY as submitBy,A.SUBMIT_DATE as submitDate,"
                + " A.SUBMIT_TIME as submitTime,A.COMPLETE_DATE as completeDate,B.OP_NAME as createByName,"
                + " A.TOT_PQTY as totqty,A.TOT_AMT as totamt,A.TOT_CQTY as totcqty "
                + " FROM DCP_PORDER A "
                + " LEFT JOIN PLATFORM_STAFFS_LANG B ON A.EID=B.EID  AND A.CREATEBY=B.OPNO and b.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN DCP_PTEMPLATE C ON A.EID=C.EID AND A.PTEMPLATENO=C.PTEMPLATENO AND DOC_TYPE='0' "
        );
        if("Y".equals(isTemplateControl)){
            sqlbuf.append(" inner join DCP_PTEMPLATE_EMPLOYEE pe on pe.eid=a.eid and pe.ptemplateno=a.ptemplateno and pe.employeeid='"+req.getEmployeeNo()+"' ");
        }

        //if("1".equals(searchScope)){
        //    sqlbuf.append(" left join DCP_PORDER_DETAIL d on d.eid=a.eid and d.organizationno=a.organizationno and d.porderno=a.porderno " +
        //            " left join DCP_STOCKOUTNOTICE_DETAIL sd on sd.eid=a.eid  and sd.sourcebillno=d.porderno and sd.oitem=d.item "+
          //          " left join dcp_stockoutnotice ds on ds.eid=a.eid and ds.billno=sd.billno  "

          //         );
        //}


        if (dateType.equals("bDate"))
            sqlbuf.append(" WHERE A.BDATE BETWEEN "+beginDate +" AND " +endDate+ " ");
        if (dateType.equals("rDate"))
            sqlbuf.append(" WHERE A.RDATE BETWEEN "+beginDate +" AND " +endDate+ " ");
        
        //2021-05-12 wangzyc 新增porderNO 用于精确查询 非必传
        if(!Check.Null(porderNo)){
            sqlbuf.append(" AND a.PORDERNO = '"+porderNo+"'");
        }
        if(Check.NotNull(request.getPTemplateNo())){
            sqlbuf.append(" AND a.PTEMPLATENO = '"+request.getPTemplateNo()+"'");
        }
        if(!Check.Null(request.getReceiptOrgNo())){
            sqlbuf.append(" AND a.RECEIPT_ORG='"+request.getReceiptOrgNo()+"' ");
        }
        if(!Check.Null(request.getSupplierType())){
            sqlbuf.append(" AND a.SUPPLIERTYPE='"+request.getSupplierType()+"' ");
        }

        //if("1".equals(searchScope)){
        //    sqlbuf.append( " and  (sd.billno is null or ds.status='3' or d.REVIEW_QTY-nvl(sd.pqty,0)>0) ");
        //}
        if (status != null) {
            sqlbuf.append("  AND a.status = '"+status+"' ");
        }

        if("Y".equals(req.getRequest().getIsInclTemplate())){
            sqlbuf.append(" and a.PTEMPLATENO  is not null ");
        }
        if("N".equals(req.getRequest().getIsInclTemplate())){
            sqlbuf.append(" and a.PTEMPLATENO  is  null ");
        }

        String[] orgList = req.getRequest().getOrgList();
        if(orgList!=null&&orgList.length>0){
            String orgStr = "";
            for(String s:orgList){
                orgStr+="'"+s+"',";
            }
            orgStr=orgStr.substring(0,orgStr.length()-1);
            sqlbuf.append(" and a.organizationno in ("+orgStr+") ");
        }

        if(outPorderNos.size()>0){
            String pOrderStr = "";
            for(String s:outPorderNos){
                pOrderStr+="'"+s+"',";
            }
            pOrderStr=pOrderStr.substring(0,pOrderStr.length()-1);
            sqlbuf.append(" and a.porderno not in ("+pOrderStr+") ");
        }


        if (keyTxt != null && keyTxt.length()!=0) {
            sqlbuf.append(""
                    + "AND (A.TOT_AMT like '%%"+ keyTxt +"%%'  "
                    + "OR  A.TOT_PQTY like '%%"+ keyTxt +"%%'   "
                    + "OR  A.PORDERNO like '%%"+ keyTxt +"%%'    "
                    + "OR  A.MEMO like '%%"+ keyTxt +"%%'   "
                    + "OR  C.PTEMPLATENO like '%%"+ keyTxt +"%%'  "
                    + "OR  C.PTEMPLATE_NAME like '%%"+ keyTxt +"%%' "
                    + " OR  A.process_ERP_No like '%%"+keyTxt+"%%')   "
            );
        }

        if(Check.Null(queryType)||"0".equals(queryType)){
            sqlbuf.append(" AND A.SHOPID='"+shopId+"'");
        }else if("1".equals(queryType)){
            sqlbuf.append(" AND a.RECEIPT_ORG='"+req.getOrganizationNO()+"' ");
        }

        sqlbuf.append(" 	 AND A.EID='"+eId+"'	 "
                + ")" // AND B.LANG_TYPE='"+langType+"'
        );


        
        sqlbuf.append(" ) TBL ");
        
        
        return sqlbuf.toString();
    }
    protected String getQuerySqlAll(DCP_POrderQueryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        levelElm request = req.getRequest();
        String status = request.getStatus();
        String keyTxt = request.getKeyTxt();
        String shopId=req.getShopId();
        String eId = req.geteId();
        String langType = req.getLangType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String isTemplateControl = request.getIsTemplateControl();
        String searchScope = request.getSearchScope();
        String queryType = request.getQueryType();

        //2021-05-12 wangzyc 新增porderNo 用于精确查询 非必传
        String porderNo = request.getPOrderNo();

        ///新增日期类型dateType  枚举: bDate：单据日期,rDate：需求日期   BY JZMA 20200327
        String dateType = request.getDateType();
        if (Check.Null(dateType) || !dateType.equals("rDate"))  dateType="bDate";

        sqlbuf.append(""
                + "SELECT distinct PORDERNO "
                + " from ("
                + " SELECT * "
                + " FROM ("
                + " SELECT distinct A.PORDERNO as porderNO,A.SHOPID as SHOPID,A.BDATE as bDate,A.MEMO as memo,A.STATUS as status,A.CREATEBY as createBy,"
                + " A.CREATE_DATE as createDate,A.CREATE_TIME as createTime,A.SUBMITBY as submitBy,A.SUBMIT_DATE as submitDate,"
                + " A.SUBMIT_TIME as submitTime,A.COMPLETE_DATE as completeDate,B.OP_NAME as createByName,"
                + " A.TOT_PQTY as totqty,A.TOT_AMT as totamt,A.TOT_CQTY as totcqty "
                + " FROM DCP_PORDER A "
                + " LEFT JOIN PLATFORM_STAFFS_LANG B ON A.EID=B.EID  AND A.CREATEBY=B.OPNO and b.lang_type='"+req.getLangType()+"' "
                + " LEFT JOIN DCP_PTEMPLATE C ON A.EID=C.EID AND A.PTEMPLATENO=C.PTEMPLATENO AND DOC_TYPE='0' "
        );
        if("Y".equals(isTemplateControl)){
            sqlbuf.append(" inner join DCP_PTEMPLATE_EMPLOYEE pe on pe.eid=a.eid and pe.ptemplateno=a.ptemplateno and pe.employeeid='"+req.getEmployeeNo()+"' ");
        }


        if (dateType.equals("bDate"))
            sqlbuf.append(" WHERE A.BDATE BETWEEN "+beginDate +" AND " +endDate+ " ");
        if (dateType.equals("rDate"))
            sqlbuf.append(" WHERE A.RDATE BETWEEN "+beginDate +" AND " +endDate+ " ");

        //2021-05-12 wangzyc 新增porderNO 用于精确查询 非必传
        if(!Check.Null(porderNo)){
            sqlbuf.append(" AND a.PORDERNO = '"+porderNo+"'");
        }
        if(Check.NotNull(request.getPTemplateNo())){
            sqlbuf.append(" AND a.PTEMPLATENO = '"+request.getPTemplateNo()+"'");
        }
        if(!Check.Null(request.getReceiptOrgNo())){
            sqlbuf.append(" AND a.RECEIPT_ORG='"+request.getReceiptOrgNo()+"' ");
        }
        if(!Check.Null(request.getSupplierType())){
            sqlbuf.append(" AND a.SUPPLIERTYPE='"+request.getSupplierType()+"' ");
        }

        if (status != null) {
            sqlbuf.append("  AND a.status = '"+status+"' ");
        }

        if("Y".equals(req.getRequest().getIsInclTemplate())){
            sqlbuf.append(" and a.PTEMPLATENO  is not null ");
        }
        if("N".equals(req.getRequest().getIsInclTemplate())){
            sqlbuf.append(" and a.PTEMPLATENO  is  null ");
        }

        String[] orgList = req.getRequest().getOrgList();
        if(orgList!=null&&orgList.length>0){
            String orgStr = "";
            for(String s:orgList){
                orgStr+="'"+s+"',";
            }
            orgStr=orgStr.substring(0,orgStr.length()-1);
            sqlbuf.append(" and a.organizationno in ("+orgStr+") ");
        }

        if (keyTxt != null && keyTxt.length()!=0) {
            sqlbuf.append(""
                    + "AND (A.TOT_AMT like '%%"+ keyTxt +"%%'  "
                    + "OR  A.TOT_PQTY like '%%"+ keyTxt +"%%'   "
                    + "OR  A.PORDERNO like '%%"+ keyTxt +"%%'    "
                    + "OR  A.MEMO like '%%"+ keyTxt +"%%'   "
                    + "OR  C.PTEMPLATENO like '%%"+ keyTxt +"%%'  "
                    + "OR  C.PTEMPLATE_NAME like '%%"+ keyTxt +"%%' "
                    + " OR  A.process_ERP_No like '%%"+keyTxt+"%%')   "
            );
        }
        if(Check.Null(queryType)||"0".equals(queryType)){
            sqlbuf.append(" AND A.SHOPID='"+shopId+"'");
        }else if("1".equals(queryType)){
            sqlbuf.append(" AND a.RECEIPT_ORG='"+req.getOrganizationNO()+"' ");
        }
        sqlbuf.append(" 	 AND A.EID='"+eId+"'	 "
                + ")"
        );



        sqlbuf.append(" ) TBL ");


        return sqlbuf.toString();
    }




}
