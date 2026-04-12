package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_FaPiaoProjEnableReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        /**
         * 1-启用2-禁用
         */
        private String oprType;
        private List<project> projectIdList;

        public String getOprType() {
            return oprType;
        }

        public void setOprType(String oprType) {
            this.oprType = oprType;
        }

        public List<project> getProjectIdList() {
            return projectIdList;
        }

        public void setProjectIdList(List<project> projectIdList) {
            this.projectIdList = projectIdList;
        }
    }

    public class project
    {
        private String projectId;

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }
    }
}
