package com.yudianbank.tms.model.vo;

import java.io.Serializable;

/**
 * 保存货源与司机ID的映射关系
 *
 * @author Song Lea
 */
public class GoodsDriverRelevanceVO implements Serializable {

    private static final long serialVersionUID = -1414738944863688130L;

    private int id;
    private String loginId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    @Override
    public String toString() {
        return "GoodsDriverRelevanceVO{" +
                "id=" + id +
                ", loginId='" + loginId + '\'' +
                '}';
    }
}