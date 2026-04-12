package com.dsc.spos.foreign.erp;

public class Response {
    /**
     * srvver : 1.0
     * srvcode : 000
     * std_data : {"execution":{"code":"0","sql_code":"","description":""},"parameter":{"doc_no":"ERP对应的单号","org_no":"ERP对应的组织"}}
     */
    private String srvver;
    private String srvcode;
    private StdDataBean std_data;

    public String getSrvver() {
        return srvver;
    }

    public void setSrvver(String srvver) {
        this.srvver = srvver;
    }

    public String getSrvcode() {
        return srvcode;
    }

    public void setSrvcode(String srvcode) {
        this.srvcode = srvcode;
    }

    public StdDataBean getStd_data() {
        return std_data;
    }

    public void setStd_data(StdDataBean std_data) {
        this.std_data = std_data;
    }

    public static class StdDataBean {
        private ExecutionBean execution;
        private ParameterBean parameter;

        public ExecutionBean getExecution() {
            return execution;
        }

        public void setExecution(ExecutionBean execution) {
            this.execution = execution;
        }

        public ParameterBean getParameter() {
            return parameter;
        }

        public void setParameter(ParameterBean parameter) {
            this.parameter = parameter;
        }

        public static class ExecutionBean {
            private String code;
            private String sql_code;
            private String description;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getSql_code() {
                return sql_code;
            }

            public void setSql_code(String sql_code) {
                this.sql_code = sql_code;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }

        public static class ParameterBean {
            private String doc_no;
            private String org_no;

            public String getDoc_no() {
                return doc_no;
            }

            public void setDoc_no(String doc_no) {
                this.doc_no = doc_no;
            }

            public String getOrg_no() {
                return org_no;
            }

            public void setOrg_no(String org_no) {
                this.org_no = org_no;
            }
        }
    }
}
