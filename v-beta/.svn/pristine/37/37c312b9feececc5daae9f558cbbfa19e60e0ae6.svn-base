package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_FaPiaoProjDeleteReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private List<project> projectIdList;

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
