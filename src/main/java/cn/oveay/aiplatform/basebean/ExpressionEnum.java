package cn.oveay.aiplatform.basebean;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/18 19:06
 * 文件说明：
 */
public enum ExpressionEnum {
    neutral ("neutral", "中性"),
    happiness ("happiness", "高兴"),
    surprise ("surprise", "惊讶"),
    sadness ("sadness", "伤心"),
    anger ("anger", "生气"),
    disgust ("disgust", "厌恶"),
    fear ("fear", "害怕");

    String name;
    String enName;

    ExpressionEnum(String enName, String name) {
        this.enName = enName;
        this.name = name;
    }

    public static String getNameByEnName(String enName) {
        for (ExpressionEnum expressionEnum : ExpressionEnum.values()) {
            if (expressionEnum.enName.equals(enName)) {
                return expressionEnum.name;
            }
        }
        return "";
    }
}
