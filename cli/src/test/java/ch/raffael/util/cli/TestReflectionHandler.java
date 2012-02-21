package ch.raffael.util.cli;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.*;

import com.google.common.base.Suppliers;

import static org.testng.Assert.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestReflectionHandler {

    private static final Pattern CMD_RE = Pattern.compile("\\s*([^\\s]+:)?([^\\s]+)(\\s.*|$)");

    @Test
    public void testCall() throws Exception {
        test("cmd", new Verifiable() {
            @Command(name = "cmd")
            public void cmd() {
                called("cmd");
            }
        });
    }

    @Test
    public void testOneStringArg() throws Exception {
        test("cmd arg", new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name="arg") String arg) {
                assertEquals(arg, "arg", "Argument value mismatch");
                called("cmd");
            }
        });
    }

    @Test
    public void testOneIntArg() throws Exception {
        test("cmd 42", new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name="arg") int arg) {
                assertEquals(arg, 42, "Argument value mismatch");
                called("cmd");
            }
        });
    }

    @Test
    public void testOneArgDefault() throws Exception {
        test("cmd", new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name="arg") int arg) {
                assertEquals(arg, 0, "Argument value mismatch");
                called("cmd");
            }
        });
    }

    @Test
    public void testTwoArgs() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "int") int intval, @Argument(name = "str") String strval) {
                assertEquals(intval, 42, "int argument mismatch");
                assertEquals(strval, "hello", "string argument mismatch");
                called("cmd");
            }
        };
        test("cmd 42 hello", cmd);
        test("cmd str:hello 42", cmd);
    }

    @Test
    public void testRequiredMissing() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "int", required = true) int intval, @Argument(name = "str") String strval) {
                assertEquals(intval, 42, "int argument mismatch");
                assertEquals(strval, "hello", "string argument mismatch");
                called("cmd");
            }
        };
        test("cmd str:hello", cmd, "Argument .*int.* is required");
        cmd.verifyNot("cmd");
    }

    @Test
    public void testEndArg() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name="int") int intarg, @Argument(name="end", mode= Argument.Mode.END) String endarg) {
                assertEquals(intarg, 42, "int argument mismatch");
                assertEquals(endarg, "this is the end", "end argument mismatch");
                called("cmd");
            }
        };
        test("cmd 42 this is the end", cmd);
    }

    @Test
    public void testPreferredEndArg() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd1")
            public void cmd1(@Argument(name = "int") int intarg, @Argument(name = "end", mode = Argument.Mode.END_PREFER) String endarg) {
                assertEquals(intarg, 0, "int argument mismatch");
                assertEquals(endarg, "this is the end", "end argument mismatch");
                called("cmd1");
            }
            @Command(name = "cmd2")
            public void cmd2(@Argument(name = "int") int intarg, @Argument(name = "end", mode = Argument.Mode.END_PREFER) String endarg) {
                assertEquals(intarg, 42, "int argument mismatch");
                assertEquals(endarg, "this is the end", "end argument mismatch");
                called("cmd2");
            }
        };
        test("cmd1 this is the end", cmd);
        test("cmd2 int:42 this is the end", cmd);
    }

    @Test
    public void testInvalidValue() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name="int") int intval) {
                fail("Call to cmd() not expected");
            }
        };
        test("cmd noint", cmd, "Invalid .* value");
    }

    @Test
    public void testFlag() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "flag") boolean flag, @Argument(name = "arg") String arg) {
                assertEquals(flag, true, "flag mismatch");
                assertEquals(arg, "stringarg", "string argument mismatch");
                called("cmd");
            }
        };
        test("cmd flag stringarg", cmd);
        test("cmd stringarg flag:true", cmd);
        test("cmd stringarg flag", cmd);
    }

    @Test
    public void testNoFlag() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmdTrue")
            public void cmdTrue(@Argument(name = "noflag", requireName = true) boolean noflag, @Argument(name = "arg") String arg) {
                assertEquals(noflag, true, "noflag mismatch");
                assertEquals(arg, "stringarg", "string argument mismatch");
                called("cmdTrue");
            }
            @Command(name = "cmdFalse")
            public void cmdFalse(@Argument(name = "noflag", requireName = true) boolean noflag, @Argument(name = "arg") String arg) {
                assertEquals(noflag, false, "noflag mismatch");
                assertEquals(arg, "stringarg", "string argument mismatch");
                called("cmdFalse");
            }
            @Command(name = "cmdStr")
            public void cmdStr(@Argument(name = "noflag", requireName = true) boolean noflag, @Argument(name = "arg") String arg) {
                assertEquals(noflag, false, "noflag mismatch");
                assertEquals(arg, "noflag", "string argument mismatch");
                called("cmdStr");
            }
        };
        test("cmdTrue noflag:yes stringarg", cmd);
        test("cmdFalse noflag:no stringarg", cmd);
        test("cmdStr noflag", cmd);
    }

    @Test
    public void testFlagPreferredEnd() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "flag") boolean flag, @Argument(name = "end", mode = Argument.Mode.END_PREFER) String endarg) {
                assertEquals(flag, true, "flag mismatch");
                assertEquals(endarg, "this is the end", "end argument mismatch");
                called("cmd");
            }
        };
        test("cmd flag this is the end", cmd);
    }

    @Test
    public void testTooManyValues() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "arg") String arg) {
                fail("Unexpected command call");
            }
        };
        test("cmd one arg:two", cmd, "Too many values");
    }

    @Test
    public void testSingleToArray() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "list") String[] strings) {
                assertEquals(strings, new String[] { "elemA" });
                called("cmd");
            }
        };
        test("cmd elemA", cmd);
    }

    @Test
    public void testSingleToArrayAppend() throws Exception {
        Verifiable cmd=new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name="list") String[] strings) {
                assertEquals(strings, new String[] { "elemA", "elemB" });
                called("cmd");
            }
        };
        test("cmd elemA list:elemB", cmd);
    }

    @Test
    public void testList() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name="list") String[] list) {
                assertEquals(list, new String[] { "elemA", "elemB", "elemC" });
                called("cmd");
            }
        };
        test("cmd elemA, elemB, elemC", cmd);
    }

    @Test
    public void testListAppend() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name="list") String[] list) {
                assertEquals(list, new String[] { "elemA", "elemB", "elemC", "elemD" });
                called("cmd");
            }
        };
        test("cmd elemA, elemB list:elemC, elemD", cmd);
    }

    @Test
    public void testGuessByList() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "single") String single, @Argument(name = "list") String[] list) {
                assertEquals(single, "just one", "single argument mismatch");
                assertEquals(list, new String[] { "hello", "world" });
                called("cmd");
            }
        };
        test("cmd hello,world 'just one'", cmd);
    }

    @Test
    public void testTooManyList() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "single") String single) {
                fail("Command should not have been called");
            }
        };
        test("cmd a,b,c", cmd, "Cannot match argument");
        test("cmd single:a,b,c", cmd, "Too many values");
    }

    @Test
    public void testAppendAtEnd() throws Exception {
        Verifiable cmd = new Verifiable() {
            @Command(name = "cmd")
            public void cmd(@Argument(name = "first") String first, @Argument(name = "list") String[] list) {
                assertEquals(first, "one", "first argument mismatch");
                assertEquals(list, new String[] { "two", "three", "four", "five", "six" }, "list mismatch");
                called("cmd");
            }
        };
        test("cmd one two three four, five six", cmd);
    }

    private void test(String cmd, Object target) throws Exception {
        test(cmd, target, null);
    }

    private void test(String cmd, Object target, String expectedError) throws Exception {
        Matcher matcher = CMD_RE.matcher(cmd);
        assertTrue(matcher.matches(), "Invalid command line");
        String cmdName = matcher.group(2);
        for ( Method m : target.getClass().getMethods() ) {
            Command a = m.getAnnotation(Command.class);
            if ( a != null ) {
                if ( a.name().equals(cmdName) ) {
                    //System.out.println("Calling: " + m);
                    ReflectionHandler handler = new ReflectionHandler(Suppliers.ofInstance(target), m, m);
                    CmdLineParser parser = new CmdLineParser(cmd, new PrintWriter(new OutputStreamWriter(System.out)), handler);
                    try {
                        parser.parse();
                        if ( expectedError != null ) {
                            fail("Error containing '" + expectedError + "' expected, there was no error");
                        }
                        if ( target instanceof Verifiable ) {
                            ((Verifiable)target).verify(a.name());
                        }
                    }
                    catch ( CmdLineSyntaxException e ) {
                        if ( expectedError != null ) {
                            System.err.println("Error: " + e);
                            assertTrue(Pattern.compile(expectedError, Pattern.CASE_INSENSITIVE).matcher(e.getMessage()).find(), "Error containing '" + expectedError + "' expected");
                        }
                        else {
                            throw e;
                        }
                    }
                    return;
                }
            }
        }
        fail("No matching method");
    }

    private abstract class Verifiable {
        private final Set<String> called = new HashSet<String>();
        protected void called(String cmd) {
            called.add(cmd);
        }
        public void verify(String... cmds) {
            for ( String cmd : cmds ) {
                assertTrue(called.remove(cmd), "Called: " + cmd);
            }
        }
        public void verifyNot(String... cmds) {
            for ( String cmd : cmds ) {
                assertFalse(called.remove(cmd), "Not called: " + cmd);
            }
        }
    }

}
