package cn.oveay.aiplatform.utils.token;

/**
 * oveashop
 * 存储Reids获得结果的容器
 *
 * @author OVAE
 * @version 0.2.1
 * CreateDate: 2019-08-28 09:17:41
 */
public class TokenResult {
    private String result;
    private Boolean getFlag;
    private Long createTime;

    TokenResult(String result) {
        this.result = result;
        this.getFlag = false;
        createTime = System.currentTimeMillis();
    }

    String getResult() {
        return result;
    }

    void setResult(String result) {
        this.result = result;
    }

    public Boolean getGetFlag() {
        return getFlag;
    }

    public void setGetFlag(Boolean getFlag) {
        this.getFlag = getFlag;
    }

    Long getCreateTime() {
        return createTime;
    }

    void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TokenResult{" +
                "result='" + result + '\'' +
                ", getFlag=" + getFlag +
                ", createTime=" + createTime +
                '}';
    }
}
