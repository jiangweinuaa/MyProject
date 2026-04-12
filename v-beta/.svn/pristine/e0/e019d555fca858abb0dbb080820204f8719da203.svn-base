package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.dsc.spos.json.cust.req.DCP_BomQueryReq;
import com.dsc.spos.json.cust.res.DCP_BomQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_BomQuery extends SPosBasicService<DCP_BomQueryReq, DCP_BomQueryRes>{

    private List<DCP_BomQueryReq.DcpCategory> allCategories = new ArrayList<>();

    @Override
	protected boolean isVerifyFail(DCP_BomQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (Check.Null(req.getRequest().getBomType()))
		{
			errMsg.append("bom类型不能为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getRestrictShop()))
		{
			errMsg.append("适用门店类型不能为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_BomQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BomQueryReq>(){};
	}

	@Override
	protected DCP_BomQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BomQueryRes();
	}

	@Override
	protected DCP_BomQueryRes processJson(DCP_BomQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		//查詢資料未加生产单位
		DCP_BomQueryRes res = this.getResponse();
		String eId = req.geteId();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		//查詢資料
		String sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_BomQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
		{
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			for (Map<String, Object> map : getQData)
			{
				DCP_BomQueryRes.level1Elm lv1 = res.new level1Elm();
				lv1.setBomNo(map.get("BOMNO").toString());
				lv1.setBomType(map.get("BOMTYPE").toString());
				lv1.setPluNo(map.get("PLUNO").toString());
				lv1.setPluName(map.get("PLU_NAME").toString());
				lv1.setUnit(map.get("UNIT").toString());
				lv1.setUnitName(map.get("UNAME").toString());
				lv1.setStatus(map.get("STATUS").toString());
				lv1.setMulQty(map.get("MULQTY").toString());
				lv1.setEffDate(map.get("EFFDATE").toString());
				lv1.setMemo(map.get("MEMO").toString());

				lv1.setBatchQty(map.get("BATCHQTY").toString());
                lv1.setSpec(map.get("SPEC").toString());
                lv1.setCategory(map.get("CATEGORY").toString());
                lv1.setCategoryName(map.get("CATEGORYNAME").toString());
                lv1.setBaseUnit(map.get("BASEUNIT").toString());
                lv1.setBaseUnitName(map.get("BASEUNITNAME").toString());
                lv1.setProdType(map.get("PRODTYPE").toString());
                lv1.setRestrictShop(map.get("RESTRICTSHOP").toString());
                lv1.setCreateOpId(map.get("CREATEOPID").toString());
                lv1.setCreateOpName(map.get("CREATEOPNAME").toString());
                lv1.setCreateTime(map.get("CREATETIME").toString());
                lv1.setCreateDeptId(map.get("CREATEDEPTID").toString());
                lv1.setCreateDeptName(map.get("CREATEDEPTNAME").toString());
                lv1.setLastModiOpId(map.get("LASTMODIOPID").toString());
                lv1.setLastModiOpName(map.get("LASTMODIOPNAME").toString());
                lv1.setLastModiTime(map.get("LASTMODITIME").toString());
				lv1.setVersionNum(map.get("VERSIONNUM").toString());
                lv1.setFixedLossQty(map.get("FIXEDLOSSQTY").toString());
                lv1.setIsProcessEnable(map.get("ISPROCESSENABLE").toString());
                lv1.setInWGroupNo(map.get("INWGROUPNO").toString());
                lv1.setContainType(map.get("CONTAINTYPE").toString());
                lv1.setRemainType(map.get("REMAINTYPE").toString());

                lv1.setMinQty(map.get("MINQTY").toString());
                lv1.setOddValue(map.get("ODDVALUE").toString());
                lv1.setProductExceed(map.get("PRODUCTEXCEED").toString());
                lv1.setProcRate(map.get("PROCRATE").toString());
                lv1.setDispType(map.get("DISPTYPE").toString());
                lv1.setSemiwoType(map.get("SEMIWOTYPE").toString());
                lv1.setSemiwoDeptType(map.get("SEMIWODEPTTYPE").toString());
                lv1.setFixPreDays(map.get("FIXPREDAYS").toString());
                lv1.setSdlaborTime(map.get("SDLABORTIME").toString());
                lv1.setSdmachineTime(map.get("SDMACHINETIME").toString());
                lv1.setStandardHours(map.get("STANDARDHOURS").toString());
                lv1.setIsCoByProduct(map.get("ISCOBYPRODUCT").toString());


                res.getDatas().add(lv1);
			}
			
			
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_BomQueryReq req) throws Exception {
		// TODO Auto-generated method stub
	
		String eId= req.geteId();		
		String bomType = req.getRequest().getBomType();
		String restrictShop = req.getRequest().getRestrictShop();//0所有组织 1指定组织
		String shopId = req.getRequest().getShopId();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();

        String category = req.getRequest().getCategory();
        String pGroupNo = req.getRequest().getPGroupNo();
        String materialPluNo = req.getRequest().getMaterialPluNo();

        String categorySql="select * from dcp_category a where a.eid='"+req.geteId()+"' ";
        List<Map<String, Object>> list = this.doQueryData(categorySql, null);

        String sql = null;
		String orgForm = req.getOrg_Form();
		StringBuffer sqlbuf = new StringBuffer("");
		String langType=req.getLangType();

		if(Check.Null(bomType)|| bomType.isEmpty())
		{
			bomType="0";
		}
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		String orgValue=" a.restrictShop as restrictShop";
		if("0".equals(restrictShop)){
			//orgValue=" '全部组织' as restrictShop";
		}
		if("1".equals(restrictShop)){
			//orgValue=" ol.org_name as restrictShop ";
		}

		sqlbuf.append("SELECT * FROM ( "
				+ " select count(distinct a.bomno) over() num, dense_rank() over( order by a.bomno,a.pluno) rn,a.* from ( "
				+ " select distinct a.bomno,a.bomtype,a.pluno,a.unit,a.mulqty,to_char(a.effdate,'yyyyMMdd') effdate,a.memo,a.status,G.PLU_NAME,u.uname,a.batchqty,b.spec,"
                + " d.category,d.category_name as categoryname ,c.baseunit,u1.uname as baseunitname,a.prodType,a.createtime,a.lastModiTime,a.createOpId,e1.op_name as createopname,  "
				+ " a.lastModiOpId,e2.op_name as lastModiOpname,a.createDeptId,d1.departname as createDeptname,"+orgValue+",a.versionnum,"
                + " a.FIXEDLOSSQTY,a.INWGROUPNO,a.ISPROCESSENABLE,a.REMAINTYPE,a.CONTAINTYPE,a.minQty,a.oddValue,a.productExceed,a.procRate,a.dispType,a.semiwoType,a.semiwoDeptType,a.fixPreDays,a.sdlaborTime,a.sdmachineTime,a.standardHours,a.isCoByProduct  "
                + " from dcp_bom a"
				+ " left join dcp_goods_lang g on a.eid=g.eid and a.pluno=g.pluno and g.lang_type='"+langType+"' "
				+ " left join dcp_unit_lang u on a.eid = u.eid and a.unit=u.unit and u.lang_type='"+langType+"' "
                + " left join DCP_GOODS_UNIT_LANG b on b.eid=a.eid and b.pluno=a.pluno and b.ounit=a.unit and b.lang_type='"+langType+"' "
                + " left join dcp_goods c on c.eid=a.eid and c.pluno=a.pluno "
                + " left join DCP_CATEGORY_LANG d on d.eid=a.eid and d.category=c.category and d.lang_type='"+langType+"' "
                + " left join dcp_unit_lang u1 on a.eid = u1.eid and c.baseunit=u1.unit and u1.lang_type='"+langType+"' "
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+langType+"' "
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+langType+"' "
                + " left join DCP_DEPARTMENT_LANG d1 on d1.eid=a.eid and d1.departno=a.createDeptId and d1.lang_type='"+langType+"' "
				);
		if(restrictShop!=null&&restrictShop.equals("1")&&Check.NotNull(shopId))//&&shopId!=null&&shopId.isEmpty()==false
		{
			sqlbuf.append(" inner join dcp_bom_range  r on a.eid = r.eid and a.bomno = r.bomno  " +
					" left join dcp_org_lang ol on ol.eid=a.eid and ol.ORGANIZATIONNO=r.ORGANIZATIONNO ");//and r.shopid='"+shopId+"'
		}
        if(Check.NotNull(pGroupNo)){
            sqlbuf.append(" left join MES_PRODUCT_GROUP_GOODS pgg on pgg.eid=a.eid and pgg.pluno=a.pluno ");
        }
        if(Check.NotNull(materialPluNo)){
            sqlbuf.append(" left join DCP_BOM_MATERIAL bm on bm.eid=a.eid and bm.bomno=a.bomno ");
        }
		
		sqlbuf.append(" where a.eid='"+eId+"' and a.bomType='"+bomType+"' and a.restrictshop='"+restrictShop+"' ");


        //if(Check.NotNull(category)){
        //    sqlbuf.append(" and c.category='"+category+"' ");
        //}

		if(Check.NotNull(category)){
			for (Map<String, Object> map : list){
				DCP_BomQueryReq.DcpCategory cat = req.new DcpCategory();
				cat.setCategory(map.get("CATEGORY").toString());
				cat.setCategoryLevel(map.get("CATEGORYLEVEL").toString());
				cat.setUpCategory(map.get("UP_CATEGORY").toString());
				cat.setEid(map.get("EID").toString());
				allCategories.add(cat);
			}

			Set<String> subCats = getAllSubCategories(req.geteId(),category);
			subCats.add(category);
			if(subCats.size()>0){
				String subcatStr = subCats.stream().map(x -> "'" + x + "'").distinct().collect(Collectors.joining(","));
				sqlbuf.append(" and c.category in ("+subcatStr+") ");
			}
		}

        if(Check.NotNull(pGroupNo)){
            sqlbuf.append(" and pgg.pgroupno='"+pGroupNo+"' ");
        }
        if(Check.NotNull(materialPluNo)){
            sqlbuf.append(" and bm.MATERIAL_PLUNO='"+materialPluNo+"' ");
        }

		if(status!=null&&status.length()>0)
		{
			sqlbuf.append(" and a.status='"+status+"' ");
		}
		if(keyTxt!=null&&keyTxt.length()>0)
		{
			sqlbuf.append(" and (a.bomno like '%%"+keyTxt+"%%' or  a.pluno like '%%"+keyTxt+"%%' or g.plu_name like '%%"+keyTxt+"%%')");
		}
        sqlbuf.append(") a ");
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		
		sql = sqlbuf.toString();
		return sql;
	}

    /**
     * 根据根分类编码，获取其所有下级分类的 category 编码
     *
     * @param eid        企业编号
     * @param rootCategory 根分类编码
     * @return 所有下级分类的 category 编码集合
     */
    public Set<String> getAllSubCategories(String eid, String rootCategory) {
        // 构建 map：key = (eid, upCategory)，value = 所有直接子节点
        Map<String, List<DCP_BomQueryReq.DcpCategory>> childrenMap = allCategories.stream()
                .filter(c -> Objects.nonNull(c.getUpCategory())) // 有上级才参与映射
                .collect(Collectors.groupingBy(DCP_BomQueryReq.DcpCategory::getUpCategory));

        Set<String> result = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(rootCategory);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            List<DCP_BomQueryReq.DcpCategory> children = childrenMap.get(current);
            if (children != null) {
                for (DCP_BomQueryReq.DcpCategory child : children) {
                    if (Objects.equals(child.getEid(), eid)) { // 确保属于同一企业
                        if (result.add(child.getCategory())) { // 防止重复
                            stack.push(child.getCategory());
                        }
                    }
                }
            }
        }

        return result;
    }




}


