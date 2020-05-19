package cn.oveay.aiplatform.basebean;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/19 11:04
 * 文件说明：不同车型
 */
public enum CarEnum {
    car ("car", "轿车"),
    MPV_mian ("MPV_mian", "多用途汽车"),
    others ("others", "其他车型"),
    SUV ("SUV", "越野车");

    String name;
    String enName;

    private CarEnum(String enName, String name) {
        this.enName = enName;
        this.name = name;
    }

    public static String getNameByEnName(String enName) {
        for (CarEnum carEnum : CarEnum.values()) {
            if (carEnum.enName.equals(enName)) {
                return carEnum.name;
            }
        }
        return "";
    }
}
