package htwb.ai;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void couldNotFindClassTest() {
        args[0] = "blub";
        RunMeMain.main(args);
        String testString = "Error: Could not find class blub" + System.lineSeparator() +
                "Usage: java -jar runmerunner-KBE.jar className" + System.lineSeparator() ;
        assertEquals(testString, errContent.toString());
    }

    @Test
    public void couldNotInstantiateClassTest() {
        args[0] = "java.lang.Number";
        RunMeMain.main(args);
        String testString = "Error: Could not instantiate class java.lang.Number"+ System.lineSeparator() +
                "Usage: java -jar runmerunner-KBE.jar className"+ System.lineSeparator();
        assertEquals(testString, errContent.toString());
    }

    @Test
    public void couldNotFindConstructorTest() {
        args[0] = "java.io.PrintStream";
        RunMeMain.main(args);
        String testString = "Error: Could not find constructor of class java.io.PrintStream"+ System.lineSeparator() +
                "Error: Could not instantiate class java.io.PrintStream"+ System.lineSeparator() +
                "Usage: java -jar runmerunner-KBE.jar className"+ System.lineSeparator();
        assertEquals(testString, errContent.toString());
    }

    @Test
    public void couldNotAccessConstructorTest() {
        args[0] = "htwb.ai.TestClassPrivateConstructor";
        RunMeMain.main(args);
        String testString = "Error: Could not access constructor of class htwb.ai.TestClassPrivateConstructor"+ System.lineSeparator() +
                "Error: Could not instantiate class htwb.ai.TestClassPrivateConstructor" + System.lineSeparator()+
                "Usage: java -jar runmerunner-KBE.jar className"+ System.lineSeparator();
        assertEquals(testString, errContent.toString());
    }
    @Test
    public void succesfulDefaultCaseTest() {
    	args[0] = "htwb.ai.TestClass";
    	RunMeMain.main(args);
    	String testString = "Analyzed class 'htwb.ai.TestClass':"+ System.lineSeparator() + 
    			"Methods without @RunMe: "+ System.lineSeparator() + 
    			"  testWithoutRM"+ System.lineSeparator() + 
    			"  testNoRM\n" + 
    			"Methods with @RunMe: "+ System.lineSeparator() + 
    			"  findMe0"+ System.lineSeparator() + 
    			"  findMe2"+ System.lineSeparator() + 
    			"  findMe4"+ System.lineSeparator() + 
    			"not invocable:"+ System.lineSeparator() + 
    			"  findMe3: IllegalAccessException"+ System.lineSeparator() + 
    			"needing arguments to be run:"+ System.lineSeparator() + 
    			"  findMe1";
    	assertEquals(testString, outContent.toString());
    }
  
}
