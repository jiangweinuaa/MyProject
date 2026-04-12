package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_N_OrderParaQueryReq;
import com.dsc.spos.json.cust.res.DCP_N_OrderParaQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_N_OrderParaQuery
 * 服务说明：N_订单参数查询 代码全部继承DCP_N_OrderParaQuery
 * @author jinzma
 * @since  2024-05-23
 */
public class DCP_N_OrderParaQuery extends SPosBasicService<DCP_N_OrderParaQueryReq, DCP_N_OrderParaQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_N_OrderParaQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_N_OrderParaQueryReq> getRequestType() {
        return new TypeToken<DCP_N_OrderParaQueryReq>(){};
    }

    @Override
    protected DCP_N_OrderParaQueryRes getResponseType() {
        return new DCP_N_OrderParaQueryRes();
    }

    @Override
    protected DCP_N_OrderParaQueryRes processJson(DCP_N_OrderParaQueryReq req) throws Exception {

        DCP_N_OrderParaQueryRes res = this.getResponse();
        DCP_N_OrderParaQueryRes.responseDatas datas = res.new responseDatas();
        datas.setOrgList(new ArrayList<>());

        try
        {

            int pageNumber = req.getPageNumber();
            int pageSize = req.getPageSize();
            int startRow = (pageNumber - 1) * pageSize;
            int totalRecords = 0; // 总笔数
            int totalPages = 0;
            String scurdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String langType = req.getLangType();
            if (langType == null || langType.isEmpty())
            {
                langType = "zh_CN";
            }
            String skeytxt = "";
            if (req.getRequest() != null)
            {
                skeytxt = req.getRequest().getKeyTxt();
            }
            String sql = "";
            StringBuilder sqlBuffer = new StringBuilder();

            sqlBuffer.append(" select * from (");
            sqlBuffer.append(" select  COUNT(*) OVER() NUM , row_number() OVER(ORDER BY a.organizationNo) rn , "
                    + " A.*,  c.org_name as orgname from DCP_ORG A  "
                    + " left join DCP_ORG_lang C on A.EID=C.EID and A.OrganizationNo=C.OrganizationNo  and C.lang_type='" + langType + "' ");
            if("1".equals(req.getRequest().getBusinessType())||
                    "0".equals(req.getRequest().getBusinessType())
            )
            {
                sqlBuffer.append("inner join (SELECT DISTINCT EID,SHOPID  FROM Platform_CregisterDetail WHERE PRODUCTTYPE='46' AND bdate <= '"+scurdate+"' AND eDate >= '"+scurdate+"' AND SHOPID IS NOT NULL ) D "
                        + " ON A.EID=D.EID AND A.OrganizationNo=D.SHOPID ");
            }
            sqlBuffer.append(" where  A.status='100' and A.EID='" + req.geteId() + "' ");
            if (skeytxt != null && !skeytxt.isEmpty())
            {
                sqlBuffer.append(
                        " and (A.OrganizationNo like '%%" + skeytxt + "%%' OR C.org_name like '%%" + skeytxt + "%%')");
            }

            sqlBuffer.append(" ) where  rn>" + startRow + " and rn<=" + (startRow + pageSize) + " order by rn ");

            sql = sqlBuffer.toString();
            List<Map<String, Object>> allorg = this.doQueryData(sql, null);

            if (allorg != null && !allorg.isEmpty())
            {
                String num = allorg.get(0).getOrDefault("NUM", "0").toString();
                totalRecords = Integer.parseInt(num);
                // 算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

                for (Map<String, Object> map : allorg)
                {

                    DCP_N_OrderParaQueryRes.level1Elm oneLv1 = res.new level1Elm();

                    oneLv1.setOrganizationNo(map.get("ORGANIZATIONNO").toString());
                    oneLv1.setOrganizationName(map.get("ORGNAME").toString());
                    oneLv1.setOrgForm(map.get("ORG_FORM").toString());
                    oneLv1.setShopBeginTime(map.get("SHOPBEGINTIME").toString());
                    oneLv1.setShopEndTime(map.get("SHOPENDTIME").toString());
                    oneLv1.setProvince(map.get("PROVINCE").toString());
                    oneLv1.setCity(map.get("CITY").toString());
                    oneLv1.setCounty(map.get("COUNTY").toString());
                    oneLv1.setStreet(map.get("STREET").toString());
                    oneLv1.setAddress(map.get("ADDRESS").toString());
                    oneLv1.setPhone(map.get("PHONE").toString());

                    datas.getOrgList().add(oneLv1);

                }


            }

            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);

            res.setDatas(datas);
            return res;


        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }

        
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_N_OrderParaQueryReq req) throws Exception {
        return "";
    }
}
