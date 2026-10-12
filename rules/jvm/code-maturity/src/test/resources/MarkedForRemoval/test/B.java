package test;

@Deprecated(forRemoval = true)
public class B implements A {
    @Override public void foo() { }
    public void bar() { }
}
