package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_OrgQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrgQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

public class DCP_OrgQuery extends SPosBasicService<DCP_OrgQueryReq, DCP_OrgQueryRes>{

	@Override
	protected boolean isVerifyFail(DCP_OrgQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrgQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrgQueryReq>(){};
	}

	@Override
	protected DCP_OrgQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrgQueryRes();
	}

	@Override
	protected DCP_OrgQueryRes processJson(DCP_OrgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;

		//查询条件
		String eId = req.geteId();;
		String langType = req.getLangType();
		String keyTxt = req.getRequest().getKeyTxt();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String _DATE = df.format(cal.getTime());

		//2018-11-12 0 不分页， 1 分页 
		String getType = req.getRequest().getGetType();

		//查询资料
		DCP_OrgQueryRes res = null;
		res = this.getResponse();

		//判断一下status
		String statussql=""; 
		if(req.getRequest().getStatus()!=null&&!req.getRequest().getStatus().isEmpty())
		{
			statussql=" and A.status='"+req.getRequest().getStatus()+"' "; 
		}

		//给分页字段赋值
		sql = this.getQuerySql_Count(req);	//查询总笔数
		String[] conditionValues_Count = {};
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, conditionValues_Count);
		int totalRecords;	//总笔数
		int totalPages;		//总页数
		if (getQData_Count != null && getQData_Count.isEmpty() == false)
		{
			Map<String, Object> oneData = getQData_Count.get(0);
			String num = oneData.get("NUM").toString();
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
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);

		sql = null;
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//计算起始位置
		int startRow = ((pageNumber - 1) * pageSize);
		startRow = ((pageNumber - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

		//选取组织查询
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select rn,organizationNO,orgForm,orgName,upOrg,upOrgName from (select ROWNUM rn,organizationNO,orgForm,orgName,upOrg,upOrgName from "
				+" (select distinct a.organizationNO,a.org_form as orgForm,b.org_name as orgName,nvl(c.up_org,' ') as upOrg,nvl(c.org_name,' ') as upOrgName "
				+ " from DCP_ORG a  "
				+ " inner join DCP_ORG_lang b on a.EID=b.EID and a.organizationno=b.organizationno "
				+ " LEFT JOIN  ( "
				+ " select distinct p2.EID,p2.organizationno,p2.up_org,b.org_name from DCP_ORG_level p2 inner join "
				+ " (select EID,organizationno,up_org,max(version) A1 "
				+ " from DCP_ORG_level where EID='"+eId+"'  "
				+ " and LBDATE<='"+_DATE+"' and (LEDate>='"+_DATE+"' or LEDate is null)   "
				+ " group by EID,organizationno,up_org "
				+ " ) p1 on p1.EID=p2.EID and p2.organizationno=p1.organizationno "
				+ " and p1.A1=p2.version "
				+ " inner join DCP_ORG_lang b on p2.EID=b.EID and p2.organizationno=b.organizationno  and b.lang_type='"+langType+"'"
				+ " where p2.EID='"+eId+"'   "
				+ " ) c on a.EID=c.EID and a.organizationno=c.organizationno  "
				+ " where a.EID='"+eId+"' "
				+ statussql + " and b.lang_type='"+langType+"'  "
				+ "");

		if (Check.isNotEmpty(req.getRequest().getIsCorp())){
			sqlbuf.append(" and a.IS_CORP='").append(req.getRequest().getIsCorp()).append("'");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" and （a.organizationno like '%%"+ keyTxt +"%%' or b.org_name like '%%"+ keyTxt +"%%')");
		}

        String checkOpno=req.getOpNO();
        if(Check.NotNull(req.getRequest().getEmployeeId())){
            checkOpno=req.getRequest().getEmployeeId();
        }
        String pSSql="SELECT ORGRANGE FROM PLATFORM_STAFFS where eid='"+eId+"' and opno='"+checkOpno+"' ";
        List<Map<String, Object>> pSDatas = this.doQueryData(pSSql, null);
        String orgRange = pSDatas.get(0).get("ORGRANGE").toString();
        if("1".equals(orgRange)){
            String pfSql="SELECT SHOPID,ISEXPAND FROM PLATFORM_STAFFS_SHOP where eid='"+eId+"' and opno='"+checkOpno+"' ";
            List<Map<String, Object>> pfDatas = this.doQueryData(pfSql, null);
            if(pfDatas.size()<=0){
                sqlbuf.append(" and 1=2 ");
            }else{
                String orgSql = "select * from DCP_ORG_LEVEL where eid='" + req.geteId() + "' ";
                List<Map<String, Object>> list = this.doQueryData(orgSql, null);
                List<String> orgs=new ArrayList();
                for (Map<String, Object> map : pfDatas){
                    String shopId = map.get("SHOPID").toString();
					String isExpand = map.get("ISEXPAND").toString();
					if("1".equals(isExpand)){
						addChildren(shopId,orgs,list);
					}else{
						if(!orgs.contains(shopId)){
							orgs.add(shopId);
						}
					}
                }
                if (orgs.size()>0){
                    String orgStr = PosPub.getArrayStrSQLIn(orgs.toArray(new String[orgs.size()]));
                    sqlbuf.append(" and a.organizationno in (").append(orgStr).append(") ");

                }

            }

        }



		sqlbuf.append(") order by organizationno asc ) " );

		// 当getType ==1 的时候分页， 否则不分页
		if(getType!= null && getType.equals("1")){
			sqlbuf.append( "where rn>" + startRow + " AND rn <= " + (startRow+pageSize) + "");
		}

		//sqlbuf.append(")");
		//sqlbuf.append(") tbl");
		sql = sqlbuf.toString();

		String[] condCountValues = {};
		List<Map<String, Object>> datas = this.doQueryData(sql, condCountValues);
		if (datas != null && datas.isEmpty() == false)
		{
			res.setDatas(new ArrayList<DCP_OrgQueryRes.level1Elm>());
			for (Map<String, Object> data : datas) 
			{
				DCP_OrgQueryRes.level1Elm oneLv1 = res.new level1Elm();

				String organizationNO_D = data.get("ORGANIZATIONNO").toString();
				String orgName = data.get("ORGNAME").toString();
				String upOrg = data.get("UPORG").toString();
				String upOrgName = data.get("UPORGNAME").toString();
				String orgForm = data.get("ORGFORM").toString();

				oneLv1.setOrganizationNo(organizationNO_D);
				oneLv1.setOrgName(orgName);
				oneLv1.setUpOrg(upOrg);
				oneLv1.setUpOrgName(upOrgName);
				oneLv1.setOrgForm(orgForm);

				res.getDatas().add(oneLv1);
				oneLv1 = null;
			}
		}
		else
		{
			res.setDatas(new ArrayList<DCP_OrgQueryRes.level1Elm>());
		}

		return res;
	}

    public void addChildren(String organizationNo,List<String> orgs,List<Map<String, Object>> list ) throws Exception{
        if(!orgs.contains(organizationNo)){
            orgs.add(organizationNo);
        }
        List<Map<String, Object>> downOrgs = list.stream().filter(x -> x.get("UP_ORG").toString().equals(organizationNo)).distinct().collect(Collectors.toList());
        for (Map<String, Object> oneOrg : downOrgs){
            String thisOrgNo = oneOrg.get("ORGANIZATIONNO").toString();
            if(!orgs.contains(thisOrgNo)){
                orgs.add(thisOrgNo);
            }
            addChildren(thisOrgNo,orgs,list);

        }
    }


    @Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrgQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getQuerySql_Count(DCP_OrgQueryReq req) throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String langType = req.getLangType();
		String eId = req.geteId();;
		//String getType = this.getGetType();
		String keyTxt = req.getRequest().getKeyTxt();
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String _DATE = df.format(cal.getTime());
		//判断一下status
		String statussql=""; 
		if(req.getRequest().getStatus()!=null&&!req.getRequest().getStatus().isEmpty())
		{
			statussql=" and A.status='"+req.getRequest().getStatus()+"' "; 
		}
		sqlbuf.append("select num from (select count(*) num from "
				+" (select distinct a.organizationNO,a.org_form as orgForm,b.org_name as orgName,c.up_org as upOrg,c.org_name as upOrgName "
				+ " from DCP_ORG a  "
				+ " inner join DCP_ORG_lang b on a.EID=b.EID and a.organizationno=b.organizationno "
				+ " LEFT JOIN  ( "
				+ " select distinct p2.EID,p2.organizationno,p2.up_org,b.org_name from DCP_ORG_level p2 inner join "
				+ " (select EID,organizationno,up_org,max(version) A1 "
				+ " from DCP_ORG_level where EID='"+eId+"'  "
				+ " and LBDATE<='"+_DATE+"'  and (LEDate>='"+_DATE+"' or LEDate is null)   "
				+ " group by EID,organizationno,up_org "
				+ " ) p1 on   p1.EID=p2.EID and p2.organizationno=p1.organizationno "
				+ " and p1.A1=p2.version "
				+ " inner join DCP_ORG_lang b on p2.EID=b.EID and p2.organizationno=b.organizationno and b.lang_type='"+langType+"' "
				+ " where   p2.EID='"+eId+"'   "
				+ " ) c on a.EID=c.EID and a.organizationno=c.organizationno  "
				+ " where a.EID='"+eId+"'  "
				+ statussql + " and b.lang_type='"+langType+"'  "
				);
		if (Check.isNotEmpty(req.getRequest().getIsCorp())){
			sqlbuf.append(" and a.IS_CORP='").append(req.getRequest().getIsCorp()).append("'");
		}
		if (keyTxt != null && !keyTxt.isEmpty())
		{
			sqlbuf.append(" and (a.organizationno like '%%"+ keyTxt +"%%' or b.org_name like '%%"+ keyTxt +"%%')");
		}			
		if(!StringUtils.isEmpty(req.getRequest().getEmployeeId())) {
			sqlbuf.append(" and a.organizationNO IN (SELECT SHOPID FROM PLATFORM_STAFFS_SHOP WHERE EID = '"+eId+"' AND OPNO = '"+req.getRequest().getEmployeeId()+"')");
		}

		sqlbuf.append(" ) ");
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();

		return sql;
	}
}
