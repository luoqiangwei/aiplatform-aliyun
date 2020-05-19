package cn.oveay.aiplatform.basebean;

import java.math.BigDecimal;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/19 12:11
 * 文件说明：
 */
public enum CarIssueEnum {
    car ("car", BigDecimal.valueOf(100), BigDecimal.valueOf(100000000)),
    MPV_mian ("MPV_mian", BigDecimal.valueOf(600), BigDecimal.valueOf(600000000)),
    others ("others", BigDecimal.valueOf(50), BigDecimal.valueOf(5000000)),
    SUV ("SUV", BigDecimal.valueOf(500), BigDecimal.valueOf(500000000));

    String name;
    BigDecimal insurers;
    BigDecimal aoc;

    private CarIssueEnum(String name, BigDecimal insurers, BigDecimal aoc) {
        this.name = name;
        this.insurers = insurers;
        this.aoc = aoc;
    }

    public static BigDecimal getInsurersByName(String name) {
        for (CarIssueEnum carIssueEnum : CarIssueEnum.values()) {
            if (carIssueEnum.name.equals(name)) {
                return carIssueEnum.insurers;
            }
        }
        return BigDecimal.valueOf(0);
    }

    public static BigDecimal getAOCByName(String name) {
        for (CarIssueEnum carIssueEnum : CarIssueEnum.values()) {
            if (carIssueEnum.name.equals(name)) {
                return carIssueEnum.aoc;
            }
        }
        return BigDecimal.valueOf(0);
    }
}
