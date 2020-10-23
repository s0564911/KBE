package htwb.ai;

public class TestClass {

    public void testWithoutRM() {}
    void testNoRM() {}
    @RunMe
    public void findMe0() {}
    @RunMe
    void findMe1(String s) {}
    @RunMe
    static void findMe2() {}
    @RunMe
    private void findMe3() {}
}
