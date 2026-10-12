package test;

public class D {
    @Deprecated(forRemoval = true)
    public static final int OLD = 1;

    @Deprecated(since = "2", forRemoval = true)
    public void old(String value) { }

    @Deprecated
    public void plain() { }

    @Deprecated(forRemoval = true)
    public String getName() {
        return "";
    }

    public void setName(String name) { }

    @Deprecated(forRemoval = true)
    public void hook() { }
}
