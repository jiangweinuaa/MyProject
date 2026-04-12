package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ISVWeComQrCodeQueryReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComQrCodeQueryRes;
import com.dsc.spos.json.cust.res.DCP_ISVWeComQrCodeQueryRes.Datas;
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
 * 服务函数：DCP_ISVWeComQrCodeQuery
 * 服务说明：个人活码查询
 * @author jinzma
 * @since  2024-02-26
 */
public class DCP_ISVWeComQrCodeQuery extends SPosBasicService<DCP_ISVWeComQrCodeQueryReq, DCP_ISVWeComQrCodeQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComQrCodeQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ISVWeComQrCodeQueryReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComQrCodeQueryReq>(){};
    }

    @Override
    protected DCP_ISVWeComQrCodeQueryRes getResponseType() {
        return new DCP_ISVWeComQrCodeQueryRes();
    }

    @Override
    protected DCP_ISVWeComQrCodeQueryRes processJson(DCP_ISVWeComQrCodeQueryReq req) throws Exception {
        DCP_ISVWeComQrCodeQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        try {
            String eId = req.geteId();
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            int totalRecords=0;								//总笔数
            int totalPages=0;									//总页数
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
                    String qrCodeId = oneData.get("QRCODEID").toString();

                    datas.setQrCodeId(qrCodeId);
                    datas.setName(oneData.get("NAME").toString());
                    datas.setRemark(oneData.get("REMARK").toString());
                    datas.setQrCodeUrl(oneData.get("QRCODEURL").toString());
                    datas.setCreateTime(oneData.get("CREATETIME").toString());
                    datas.setAutoPass(oneData.get("AUTOPASS").toString());
                    datas.setLogo(oneData.get("LOGO").toString());
                    datas.setLogoUrl(logoUrl);
                    datas.setLastModiTime(oneData.get("LASTMODITIME").toString());

                    datas.setUserList(new ArrayList<>());
                    sql = " select a.userid,b.opname from dcp_isvwecom_qrcode_user a"
                            + " left join dcp_isvwecom_staffs b on a.eid=b.eid and a.userid=b.userid"
                            + " where a.eid='"+eId+"' and a.qrcodeid='"+qrCodeId+"' "
                            + " order by a.userid ";
                    List<Map<String, Object>> getUserQData=this.doQueryData(sql, null);
                    for (Map<String, Object> userData : getUserQData) {
                        DCP_ISVWeComQrCodeQueryRes.User user = res.new User();
                        user.setUserId(userData.get("USERID").toString());
                        user.setUserName(userData.get("OPNAME").toString());

                        datas.getUserList().add(user);
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
    protected String getQuerySql(DCP_ISVWeComQrCodeQueryReq req) throws Exception {
        String eId = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String userId = req.getRequest().getUserId();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();

        StringBuffer sqlBuf = new StringBuffer();

        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * req.getPageSize();

        sqlBuf.append(" select * from ("
                + " select count(distinct a.qrcodeid) over() num,"
                + " dense_rank() over(order by a.createtime desc,a.qrcodeid) as rn,"
                + " a.qrcodeid,a.name,a.remark,a.qrcodeurl,a.autopass,a.logo,"
                + " to_char(a.createtime,'yyyy-mm-dd hh24:mi:ss') as createtime,"
                + " to_char(a.lastmoditime,'yyyy-mm-dd hh24:mi:ss') as lastmoditime"
                + " from dcp_isvwecom_qrcode a"
                + " left join dcp_isvwecom_qrcode_user b on a.eid=b.eid and a.qrcodeid=b.qrcodeid"
                + " left join dcp_isvwecom_staffs b1 on a.eid=b1.eid and b.userid=b1.userid"
                + " where a.eid='"+eId+"'"
                + " ");
        if (!Check.Null(userId)){
            sqlBuf.append(" and b.userid='"+userId+"' ");
        }

        if (!Check.Null(keyTxt)){
            sqlBuf.append(" and (a.name like '%"+keyTxt+"%' or b1.opname like '%"+keyTxt+"%' ) ");
        }
        if (!Check.Null(beginDate) && !Check.Null(endDate)){
            sqlBuf.append(" and a.createtime>=to_date('"+beginDate+" 00:00:01','yyyy-MM-dd HH24:mi:ss') "
                    + " and a.createtime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd HH24:mi:ss') ");
        }

        sqlBuf.append(" ) a");
        sqlBuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" ");
        sqlBuf.append(" order by a.rn ");

        return sqlBuf.toString();


    }
}
