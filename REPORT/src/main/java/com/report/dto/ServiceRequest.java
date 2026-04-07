package com.report.dto;

import java.io.Serializable;

/**
 * 统一服务请求封装
 * 格式：{"serviceId":"xxx","request":{},"sign":{}}
 */
public class ServiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务 ID
     */
    private String serviceId;

    /**
     * 请求参数
     */
    private Object request;

    /**
     * 签名信息
     */
    private SignInfo sign;

    /**
     * 页码 (分页用，从 1 开始)
     */
    private Integer pageNumber;

    /**
     * 每页大小 (分页用)
     */
    private Integer pageSize;

    public ServiceRequest() {
    }

    public ServiceRequest(String serviceId, Object request, SignInfo sign) {
        this.serviceId = serviceId;
        this.request = request;
        this.sign = sign;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public SignInfo getSign() {
        return sign;
    }

    public void setSign(SignInfo sign) {
        this.sign = sign;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "serviceId='" + serviceId + '\'' +
                ", request=" + request +
                ", sign=" + sign +
                '}';
    }
}
