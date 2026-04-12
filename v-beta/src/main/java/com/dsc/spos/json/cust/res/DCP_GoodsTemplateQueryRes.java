package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_GoodsTemplateQueryRes extends JsonRes
{
	
	private List<level1Elm> datas ;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}



	public class level1Elm
	{
		private String templateId;//模板编码
		private String templateName;//模板名称
		private String templateType;//模板类型：COMMON-通用模板SPECIAL-专用模板
			
    	private String memo;//备注
		private String status;//状态：-1未启用100已启用0已禁用
		private String createtime;//创建时间YYYY-MM-DD HH:MI:SS，降序第一列
		private String createopid;//创建人编号
		private String createopname;//创建人名称
		private String lastmoditime;//最后修改时间YYYY-MM-DD HH:MI:SS
		private String lastmodiopid;//最后修改人编码
		private String lastmodiname;//最后修改人名称
        private String redisUpdateSuccess;//同步缓存是否成功Y/N

        private String createDeptId;
        private String createDeptName;

		private List<levelTemplate> templateName_lang;
		private List<levelRange> rangeList;
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		public String getTemplateType() {
			return templateType;
		}
		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}
	
		
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getCreatetime() {
			return createtime;
		}
		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}
		public String getCreateopid() {
			return createopid;
		}
		public void setCreateopid(String createopid) {
			this.createopid = createopid;
		}
		public String getCreateopname() {
			return createopname;
		}
		public void setCreateopname(String createopname) {
			this.createopname = createopname;
		}
		public String getLastmoditime() {
			return lastmoditime;
		}
		public void setLastmoditime(String lastmoditime) {
			this.lastmoditime = lastmoditime;
		}
		public String getLastmodiopid() {
			return lastmodiopid;
		}
		public void setLastmodiopid(String lastmodiopid) {
			this.lastmodiopid = lastmodiopid;
		}
		public String getLastmodiname() {
			return lastmodiname;
		}
		public void setLastmodiname(String lastmodiname) {
			this.lastmodiname = lastmodiname;
		}

        public String getRedisUpdateSuccess()
        {
            return redisUpdateSuccess;
        }

        public void setRedisUpdateSuccess(String redisUpdateSuccess)
        {
            this.redisUpdateSuccess = redisUpdateSuccess;
        }

        public List<levelRange> getRangeList()
		{
			return rangeList;
		}
		public void setRangeList(List<levelRange> rangeList)
		{
			this.rangeList = rangeList;
		}
		public List<levelTemplate> getTemplateName_lang()
		{
			return templateName_lang;
		}
		public void setTemplateName_lang(List<levelTemplate> templateName_lang)
		{
			this.templateName_lang = templateName_lang;
		}


        public String getCreateDeptId() {
            return createDeptId;
        }

        public void setCreateDeptId(String createDeptId) {
            this.createDeptId = createDeptId;
        }

        public String getCreateDeptName() {
            return createDeptName;
        }

        public void setCreateDeptName(String createDeptName) {
            this.createDeptName = createDeptName;
        }
    }
	
	public class levelTemplate
	{
		private String langType ;
		private String name ;
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	public class levelRange
	{
		//private String rangeType ;//1-公司2-门店3-渠道4-应用
		private String id ;//编号
		private String name ;//名称
		/*public String getRangeType() {
			return rangeType;
		}
		public void setRangeType(String rangeType) {
			this.rangeType = rangeType;
		}*/
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}	

	}

}
