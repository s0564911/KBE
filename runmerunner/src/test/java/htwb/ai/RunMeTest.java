package htwb.ai;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/*
UnitTests fuer den runmerunner. Testet anhand der Konsolenausgaben der main-Klasse und der aufzurufenden Methoden
 */
public class RunMeTest {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private final PrintStream originalOut = System.out;
    String[] args = new String[1];

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /*
    Testet den succesful default case anhand der (err-) Ausgabe der aufzurufenden Methoden
        -> von fuenf annotierten Methoden koennen drei aufgerufen werden, Ausgabe: "000"
     */
    @Test
    public void succesfulDefaultCaseTest() {
        args[0] = "htwb.ai.TestClass";
        RunMeMain.main(args);
        String testString = "000";
        assertEquals(testString, errContent.toString());
        assertNotEquals("", outContent.toString());
    }

    /*
    Testet die Error- und Usage-Meldung, wenn eine nicht findbare Klasse als Parameter uebergeben wird
     */
    @Test
    public void couldNotFindClassTest() {
        args[0] = "blub";
        RunMeMain.main(args);
        String testString = "Error: Could not find class blub" + System.lineSeparator() +
                "Usage: java -jar runmerunner-KBE.jar className" + System.lineSeparator();
        assertEquals(testString, errContent.toString());
        assertEquals("", outContent.toString());
    }

    /*
        Testet die Error- und Usage-Meldung, wenn kein Parameter uebergeben wird
    */
    @Test
    public void couldNotFindEmptyClassNameTest() {
        args[0] = "";
        RunMeMain.main(args);
        String testString = "Error: Could not find class " + System.lineSeparator() +
                "Usage: java -jar runmerunner-KBE.jar className" + System.lineSeparator();
        assertEquals(testString, errContent.toString());
        assertEquals("", outContent.toString());
    }

    /*
    Testet die Error- und Usage-Meldung, wenn eine nicht instanziierbare Klasse als Parameter uebergeben wird
     */
    @Test
    public void couldNotInstantiateClassTest() {
        args[0] = "java.lang.Number";
        RunMeMain.main(args);
        String testString = "Error: Could not instantiate class java.lang.Number" + System.lineSeparator() +
                "Usage: java -jar runmerunner-KBE.jar className" + System.lineSeparator();
        assertEquals(testString, errContent.toString());
        assertEquals("", outContent.toString());
    }

    /*
    Testet die Error- und Usage-Meldung, wenn eine nicht Klasse als Parameter uebergeben wird,
    deren Konstruktor nicht gefunden werden konnte
     */
    @Test
    public void couldNotFindConstructorTest() {
        args[0] = "java.io.PrintStream";
        RunMeMain.main(args);
        String testString = "Error: Could not find constructor of class java.io.PrintStream" + System.lineSeparator() +
                "Error: Could not instantiate class java.io.PrintStream" + System.lineSeparator() +
                "Usage: java -jar runmerunner-KBE.jar className" + System.lineSeparator();
        assertEquals(testString, errContent.toString());
        assertEquals("", outContent.toString());
    }

    /*
        Testet die Error- und Usage-Meldung, wenn eine nicht Klasse als Parameter uebergeben wird,
        deren Konstruktor nicht aufgerufen werden konnte
    */
    @Test
    public void couldNotAccessConstructorTest() {
        args[0] = "htwb.ai.TestClassPrivateConstructor";
        RunMeMain.main(args);
        String testString = "Error: Could not access constructor of class htwb.ai.TestClassPrivateConstructor" + System.lineSeparator() +
                "Error: Could not instantiate class htwb.ai.TestClassPrivateConstructor" + System.lineSeparator() +
                "Usage: java -jar runmerunner-KBE.jar className" + System.lineSeparator();
        assertEquals(testString, errContent.toString());
        assertEquals("", outContent.toString());
    }

    /*
    Testet, ob der Aufruf einer Klasse ohne Methoden ohne Fehler durchlaeuft. (Coverage)
     */
    @Test
    public void classWithoutMethodsTest() {
        args[0] = "htwb.ai.EmptyTestClass";
        RunMeMain.main(args);
        assertNotEquals("", outContent.toString());
        assertEquals("", errContent.toString());
    }
}
