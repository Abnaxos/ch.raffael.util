package ch.raffael.util.cli;

import java.io.PrintWriter;

import com.google.common.io.NullOutputStream;

import ch.raffael.util.common.Token;

import static org.easymock.EasyMock.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestCmdLineParser {

    private final PrintWriter output = new PrintWriter(new NullOutputStream());
    CmdLineHandler handler;
    private Token token;

    @BeforeMethod
    public void initMock() {
        handler = createStrictMock(CmdLineHandler.class);
    }

    @BeforeMethod
    public void newToken() {
        token = new Token();
    }

    @Test
    public void testBasic() throws Exception {
        expect(handler.command(output, token, "module", "command")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, "named", "value 'with' escape")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(same(output), same(token), (String)same(null), aryEq(new String[] { "unnamed", "list" }))).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, null, "other unnamed")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(same(output), same(token), eq("list"), aryEq(new String[] { "named", "the list", "more" }))).andReturn(CmdLineHandler.Mode.PARSE);
        handler.end(output, token, null);
        parse("module:command named:'value ''with'' escape' unnamed,list 'other unnamed' list:named,'the list', more");
    }

    @Test
    public void testUnqualifiedCommand() throws Exception {
        expect(handler.command(output, token, null, "theCommand")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, null, "value")).andReturn(CmdLineHandler.Mode.PARSE);
        handler.end(output, token, null);
        parse("theCommand value");
    }

    @Test
    public void testEndAfterCommand() throws Exception {
        expect(handler.command(output, token, "prefix", "command")).andReturn(CmdLineHandler.Mode.END_OF_LINE);
        handler.end(output, token, "And this is 'the'' unchanged  end");
        parse("prefix:command   And this is 'the'' unchanged  end   ");
    }

    @Test
    public void testEndUnnamed() throws Exception {
        expect(handler.command(output, token, "prefix", "command")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, "name", "value")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, null, "To")).andReturn(CmdLineHandler.Mode.END_OF_LINE);
        handler.end(output, token, "To the end from here");
        parse("prefix:command name:value To the end from here");
    }

    @Test
    public void testEndNamed() throws Exception {
        expect(handler.command(output, token, "prefix", "command")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, "name", "value")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, "end", "To")).andReturn(CmdLineHandler.Mode.END_OF_LINE);
        handler.end(output, token, "To the end from here");
        String source = "prefix:command name:value end:To the end from here";
        parse(source);
    }

    @Test
    public void testEndUnnamedList() throws Exception {
        expect(handler.command(output, token, "prefix", "command")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, "name", "value")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(same(output), same(token), (String)isNull(), aryEq(new String[] { "Now", "to" }))).andReturn(CmdLineHandler.Mode.END_OF_LINE);
        handler.end(output, token, "Now, to the end from here");
        String source = "prefix:command name:value Now, to the end from here";
        parse(source);
    }

    @Test
    public void testEndNamedList() throws Exception {
        expect(handler.command(output, token, "prefix", "command")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(output, token, "name", "value")).andReturn(CmdLineHandler.Mode.PARSE);
        expect(handler.value(same(output), same(token), eq("end"), aryEq(new String[] { "Now", "to" }))).andReturn(CmdLineHandler.Mode.END_OF_LINE);
        handler.end(output, token, "Now, to the end from here");
        String source = "prefix:command name:value end:Now, to the end from here";
        parse(source);
    }

    @Test(expectedExceptions = CmdLineSyntaxException.class)
    public void testFailIncompleteCommand() throws Exception {
        fail("test:");
    }

    @Test(expectedExceptions = CmdLineSyntaxException.class)
    public void testFailIncompleteArg() throws Exception {
        fail("test:command fail:");
    }

    @Test(expectedExceptions = CmdLineSyntaxException.class)
    public void testFailIncompleteList() throws Exception {
        fail("test:command a,b,c,");
    }

    @Test(expectedExceptions = CmdLineSyntaxException.class)
    public void testFailUnterminatedQuote() throws Exception {
        fail("test:command \"unterminated");
    }

    private void parse(String source) throws Exception {
        replay(handler);
        CmdLineParser parser = new CmdLineParser(source, output, new LoggingHandler(handler));
        parser.parse();
        verify(handler);
    }

    private void fail(String source) throws Exception {
        CmdLineParser parser = new CmdLineParser(source, output, new LoggingHandler(handler));
        parser.parse();
    }

    private static class LoggingHandler implements CmdLineHandler {
        private final CmdLineHandler delegate;
        private LoggingHandler(CmdLineHandler delegate) {
            this.delegate = delegate;
        }
        public Mode command(PrintWriter output, Token token, String prefix, String cmd) throws Exception {
            System.out.println("command: " + prefix + ":" + cmd);
            return delegate.command(output, token, prefix, cmd);
        }

        public Mode value(PrintWriter output, Token token, String name, String value) throws Exception {
            System.out.println("value: " + name + ":" + value);
            return delegate.value(output, token, name, value);
        }

        public Mode value(PrintWriter output, Token token, String name, String[] value) throws Exception {
            System.out.println("value: " + name + ":" + array(value));
            return delegate.value(output, token, name, value);
        }
        public void end(PrintWriter output, Token token, String endOfLine) throws Exception {
            System.out.println("end: " + endOfLine);
            delegate.end(output, token, endOfLine);
        }

        @Override
        public void help(PrintWriter output) {
            System.out.println("help");
            delegate.help(output);
        }

        private String array(String[] strings) {
            StringBuilder buf = new StringBuilder();
            buf.append('[');
            boolean first = true;
            for ( String s : strings ) {
                if ( first ) {
                    first = false;
                }
                else {
                    buf.append(',');
                }
                buf.append(s);
            }
            buf.append(']');
            return buf.toString();
        }
    }
}
