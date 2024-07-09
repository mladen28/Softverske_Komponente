package Serialization;

public class DayMapping {
    private String custom;
    private String original;

    public DayMapping(String custom, String original) {
        this.custom = custom;
        this.original = original;
    }
    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
