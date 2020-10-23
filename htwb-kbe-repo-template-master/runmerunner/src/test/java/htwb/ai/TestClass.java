package htwb.ai;

/*
Allgemeine Testklasse
 */
public class TestClass {

    public void testWithoutRM() {}
    void testNoRM() {}

    @RunMe public void findMe0() {System.err.print("0");}
    @RunMe void findMe1(String s) {System.err.print("0");}
    @RunMe static void findMe2() {System.err.print("0");}
    @RunMe private void findMe3() {System.err.print("0");}
    @RunMe public String findMe4() {System.err.print("0"); return "hello";}
}
