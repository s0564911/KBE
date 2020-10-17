package htwb.ai;

public class TestClass {

    // ToDo: JUnit dis in 'test.java.htwb.ai.RunMeTest'

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
