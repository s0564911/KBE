package htwb.ai;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.expectNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
//import org.junit.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RunMeMain.class)
public class RunMeTest {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    String[] args = new String[1];

    @Mock
    private TestClass testClass;

    private RunMeMain runMeMain;

    @Before
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    public void couldNotFindClass() {
        args[0] = "blub";
        RunMeMain.main(args);
        String testString = "Error: Could not find class blub\r\n" +
                "Usage: java -jar runmerunner-KBE.jar className\r\n";
        assertEquals(testString, errContent.toString());
    }
    @Test
    public void couldNotInstantiateClass() {
        args[0] = "java.lang.Number";
        RunMeMain.main(args);
        String testString = "Error: Could not instantiate class java.lang.Number\r\n" +
                "Usage: java -jar runmerunner-KBE.jar className\r\n";
        assertEquals(testString, errContent.toString());
    }
    @Test
    public void couldNotFindConstructor() {
        args[0] = "java.io.PrintStream";
        RunMeMain.main(args);
        String testString = "Error: Could not find constructor of class java.io.PrintStream\r\n" +
                "Error: Could not instantiate class java.io.PrintStream\r\n" +
                "Usage: java -jar runmerunner-KBE.jar className\r\n";
        assertEquals(testString, errContent.toString());
    }
    @Test
    public void couldNotAccessConstructor() {
        args[0] = "htwb.ai.TestClassPrivateConstructor";
        RunMeMain.main(args);
        String testString = "Error: Could not access constructor of class htwb.ai.TestClassPrivateConstructor\r\n" +
                "Error: Could not instantiate class htwb.ai.TestClassPrivateConstructor\r\n" +
                "Usage: java -jar runmerunner-KBE.jar className\r\n";
        assertEquals(testString, errContent.toString());
    }

    // ToDo

    @Test
    public void areMethodsInvoked() throws Exception{
        args[0] = "htwb.ai.TestClass";

        runMeMain = new RunMeMain();

        expectNew(TestClass.class).andReturn(testClass);

//        Mockito.verify(testClass, Mockito.times(1)).findMe0();
    }
}
