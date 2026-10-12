public class Widened {
    private Integer value;
    public Integer getValue() {
        return value;
    }
    public void setValue(Number value) {
        this.value = value.intValue();
    }
}
