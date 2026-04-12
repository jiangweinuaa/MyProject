package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComGpCodeQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpCodeQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpCodeQueryRes.Datas;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpCodeQueryRes.Chat;
import com.dsc.spos.json.cust.res.DCP_ISVWeComGpCodeQueryRes.Shop;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_ISVWeComGpCodeQuery
 * 服务说明：社群活码查询
 * @author jinzma
 * @since  2024-02-28
 */
public class DCP_ISVWeComGpCodeQuery extends SPosBasicService<DCP_ISVWeComGpCodeQueryReq, DCP_ISVWeComGpCodeQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComGpCodeQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            errMsg.append("request不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_ISVWeComGpCodeQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComGpCodeQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComGpCodeQueryRes getResponseType() {
        return new DCP_ISVWeComGpCodeQueryRes();
    }

    @Override
    protected DCP_ISVWeComGpCodeQueryRes processJson(DCP_ISVWeComGpCodeQueryReq req) throws Exception {
        DCP_ISVWeComGpCodeQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        try{
            String eId = req.geteId();
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords=0;								//总笔数
            int totalPages=0;								//总页数
            if (!CollectionUtils.isEmpty(getQData)){
                //图片地址
                String ISHTTPS = PosPub.getPARA_SMS(dao,eId,"","ISHTTPS");
                String httpStr = ISHTTPS.equals("1") ? "https://" : "http://";
                String DomainName = PosPub.getPARA_SMS(dao,eId,"","DomainName");
                String imagePath = httpStr + DomainName + "/resource/image/";
                if (DomainName.endsWith("/")) {
                    imagePath = httpStr + DomainName + "resource/image/";
                }
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                getQData = getQData.stream().distinct().collect(Collectors.toList());

                for (Map<String, Object> oneData : getQData) {
                    String logoUrl = "";
                    if (!Check.Null(oneData.get("LOGO").toString())){
                        logoUrl = imagePath + oneData.get("LOGO").toString();
                    }
                    Datas datas = res.new Datas();
                    String gpCodeId = oneData.get("GPCODEID").toString();

                    datas.setGpCodeId(gpCodeId);
                    datas.setName(oneData.get("NAME").toString());
                    datas.setGpCodeUrl(oneData.get("GPCODEURL").toString());
                    datas.setAutoCreate(oneData.get("AUTOCREATE").toString());
                    datas.setBaseName(oneData.get("BASENAME").toString());
                    datas.setBaseId(oneData.get("BASEID").toString());
                    datas.setRemark(oneData.get("REMARK").toString());
                    datas.setLogo(oneData.get("LOGO").toString());
                    datas.setLogoUrl(logoUrl);
                    datas.setCreateTime(oneData.get("CREATETIME").toString());
                    datas.setLastModiTime(oneData.get("LASTMODITIME").toString());

                    datas.setChatList(new ArrayList<>());
                    sql = " select a.chatid,b.name from dcp_isvwecom_gpcode_gpid a"
                            + " left join dcp_isvwecom_groupchat b on a.eid=b.eid and a.chatid=b.chatid"
                            + " where a.eid='"+eId+"' and a.gpcodeid='"+gpCodeId+"' "
                            + " order by a.chatid ";
                    List<Map<String, Object>> getChatQData=this.doQueryData(sql, null);
                    for (Map<String, Object> chatData : getChatQData) {
                        Chat chat = res.new Chat();
                        chat.setChatId(chatData.get("CHATID").toString());
                        chat.setChatName(chatData.get("NAME").toString());

                        datas.getChatList().add(chat);
                    }

                    datas.setShopList(new ArrayList<>());
                    sql = " select a.shopid,b.org_name from dcp_isvwecom_gpcode_shop a"
                            + " left join dcp_org_lang b on a.eid=b.eid and a.shopid=b.organizationno and b.lang_type='zh_CN'"
                            + " where a.eid='"+eId+"' and a.gpcodeid='"+gpCodeId+"' "
                            + " order by a.shopid ";
                    List<Map<String, Object>> getShopQData=this.doQueryData(sql, null);
                    for (Map<String, Object> shopData : getShopQData) {
                        Shop shop = res.new Shop();
                        shop.setShopId(shopData.get("SHOPID").toString());
                        shop.setShopName(shopData.get("ORG_NAME").toString());

                        datas.getShopList().add(shop);
                    }

                    res.getDatas().add(datas);

                }


            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ISVWeComGpCodeQueryReq req) throws Exception {
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String shopId = req.getRequest().getShopId();
        String chatId = req.getRequest().getChatId();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        StringBuffer sqlBuf = new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();

        sqlBuf.append(" select * from ("
                + " select count(distinct a.gpcodeid) over() num,dense_rank() over(order by a.createtime desc,a.gpcodeid) as rn,"
                + " a.gpcodeid,a.name,a.gpcodeurl,a.autocreate,a.basename,a.baseid,a.remark,a.logo,"
                + " to_char(a.createtime,'yyyy-mm-dd hh24:mi:ss') as createtime,"
                + " to_char(a.lastmoditime,'yyyy-mm-dd hh24:mi:ss') as lastmoditime"
                + " from dcp_isvwecom_gpcode a"
                + " left join dcp_isvwecom_gpcode_shop b on a.eid=b.eid and a.gpcodeid=b.gpcodeid"
                + " left join dcp_org_lang b1 on a.eid=b1.eid and b.shopid=b1.organizationno and b1.lang_type='zh_CN'"
                + " left join dcp_isvwecom_gpcode_gpid c on a.eid=c.eid and a.gpcodeid=c.gpcodeid"
                + " left join dcp_isvwecom_groupchat c1 on a.eid=c.eid and c.chatid=c1.chatid"
                + " where a.eid='"+eId+"'");

        if (!Check.Null(shopId)){
            sqlBuf.append(" and b.shopid='"+shopId+"' ");
        }
        if (!Check.Null(chatId)){
            sqlBuf.append(" and c.chatid='"+chatId+"' ");
        }
        if (!Check.Null(beginDate) && !Check.Null(endDate)){
            sqlBuf.append(" and a.createtime>=to_date('"+beginDate+" 00:00:01','yyyy-MM-dd HH24:mi:ss') "
                    + " and a.createtime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd HH24:mi:ss') ");
        }
        if (!Check.Null(keyTxt)){
            sqlBuf.append(" and (a.name like '%"+keyTxt+"%' or b1.org_name like '%"+keyTxt+"%' or c1.name like '%"+keyTxt+"%')");
        }


        sqlBuf.append(" ) a");
        sqlBuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" ");
        sqlBuf.append(" order by a.rn ");

        return sqlBuf.toString();

    }
}
