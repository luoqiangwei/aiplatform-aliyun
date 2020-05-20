package cn.oveay.aiplatform.utils.token;

/**
 * oveashop
 * 记录了token发出者发出请求后存储结果的位置
 *
 * @author OVAE
 * @version 1.0.1
 * CreateDate: 2019-08-28 08:23:37
 */
public class TokenLocation {
    private Integer saveSlot;
    private Integer saveSeat;

    public TokenLocation(Integer saveSlot, Integer saveSeat) {
        this.saveSlot = saveSlot;
        this.saveSeat = saveSeat;
    }

    public Integer getSaveSlot() {
        return saveSlot;
    }

    public void setSaveSlot(Integer saveSlot) {
        this.saveSlot = saveSlot;
    }

    public Integer getSaveSeat() {
        return saveSeat;
    }

    public void setSaveSeat(Integer saveSeat) {
        this.saveSeat = saveSeat;
    }
}
