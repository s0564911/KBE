package htwb.ai;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunMeTest {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    String[] args = new String[1];

    @BeforeEach
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    public void couldNotFindClassTest() {
        args[0] = "blub";
        RunMeMain.main(args);
        String testString = "Error: Could not find class blub\r\n" +
                "Usage: java -jar runmerunner-KBE.jar className\r\n";
        assertEquals(testString, errContent.toString());
    }

    @Test
    public void couldNotInstantiateClassTest() {
        args[0] = "java.lang.Number";
        RunMeMain.main(args);
        String testString = "Error: Could not instantiate class java.lang.Number\r\n" +
                "Usage: java -jar runmerunner-KBE.jar className\r\n";
        assertEquals(testString, errContent.toString());
    }

    @Test
    public void couldNotFindConstructorTest() {
        args[0] = "java.io.PrintStream";
        RunMeMain.main(args);
        String testString = "Error: Could not find constructor of class java.io.PrintStream\r\n" +
                "Error: Could not instantiate class java.io.PrintStream\r\n" +
                "Usage: java -jar runmerunner-KBE.jar className\r\n";
        assertEquals(testString, errContent.toString());
    }

    @Test
    public void couldNotAccessConstructorTest() {
        args[0] = "htwb.ai.TestClassPrivateConstructor";
        RunMeMain.main(args);
        String testString = "Error: Could not access constructor of class htwb.ai.TestClassPrivateConstructor\r\n" +
                "Error: Could not instantiate class htwb.ai.TestClassPrivateConstructor\r\n" +
                "Usage: java -jar runmerunner-KBE.jar className\r\n";
        assertEquals(testString, errContent.toString());
    }
}
