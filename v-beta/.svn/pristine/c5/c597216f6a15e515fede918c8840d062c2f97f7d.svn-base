package com.dsc.spos.foreign.erp;


public class Request<T> {
    private StdDataBean<T> std_data;

    public static class StdDataBean<T> {
        private ParameterBean<T> parameter;

        public ParameterBean getParameter() {
            return parameter;
        }

        public void setParameter(ParameterBean parameter) {
            this.parameter = parameter;
        }
    }

    public static class ParameterBean<T> {
        private T request;

        public T getRequest() {
            return request;
        }
    }
}
