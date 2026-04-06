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

    @Override
    public String toString() {
        return "ServiceRequest{" +
                "serviceId='" + serviceId + '\'' +
                ", request=" + request +
                ", sign=" + sign +
                '}';
    }
}
