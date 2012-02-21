package ch.raffael.util.cli;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Throwables;

import ch.raffael.util.common.Token;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CmdLineParser {

    private final Tokenizer tokenizer;
    private final HandlerWrapper handler;
    private final PrintWriter output;
    private Token token;

    public CmdLineParser(CharSequence source, PrintWriter output, CmdLineHandler handler) {
        this.output = output;
        this.tokenizer = new Tokenizer(source);
        this.handler = new HandlerWrapper(handler);
    }

    public void parse() throws CmdLineException {
        this.token = new Token();
        CmdToken tok = tokenizer.nextToken();
        TokenType.WORD.checkType(tok);
        CmdToken next = tokenizer.nextToken();
        CmdLineHandler.Mode mode;
        if ( next.getType() == TokenType.COLON ) {
            next = tokenizer.nextToken();
            TokenType.WORD.checkType(next);
            mode = handler.command(output, token, tok.getValue(), next.getValue());
        }
        else {
            tokenizer.pushback(next);
            mode = handler.command(output, token, null, tok.getValue());
        }
        tok = tokenizer.nextToken();
        if ( mode == CmdLineHandler.Mode.END_OF_LINE ) {
            handler.end(output, token, tokenizer.toEndOfLine(tok));
            return;
        }
        while ( tok.getType() != TokenType.END ) {
            tokenizer.pushback(tok);
            if ( !argument() ) {
                return;
            }
            tok = tokenizer.nextToken();
        }
        handler.end(output, token, null);
    }

    public boolean argument() throws CmdLineException {
        CmdToken tok = tokenizer.nextToken();
        TokenType.WORD.checkType(tok);
        CmdToken next = tokenizer.nextToken();
        switch ( next.getType() ) {
            case WORD:
                tokenizer.pushback(next);
                if ( handler.value(output, token, null, tok.getValue()) == CmdLineHandler.Mode.END_OF_LINE ) {
                    handler.end(output, token, tokenizer.toEndOfLine(tok));
                    return false;
                }
                else {
                    return true;
                }
            case COLON:
                return namedArgument(tok);
            case COMMA:
                if ( handler.value(output, token, null, toStringArray(list(tok))) == CmdLineHandler.Mode.END_OF_LINE ) {
                    handler.end(output, token, tokenizer.toEndOfLine(tok));
                    return false;
                }
                else {
                    return true;
                }
            case END:
                tokenizer.pushback(next);
                if ( handler.value(output, token, null, tok.getValue()) == CmdLineHandler.Mode.END_OF_LINE ) {
                    handler.end(output, token, tok.getValue());
                    return false;
                }
                else {
                    return true;
                }
        }
        throw new RuntimeException("Unreachable code reached");
    }

    public boolean namedArgument(CmdToken name) throws CmdLineException {
        CmdToken tok = tokenizer.nextToken();
        TokenType.WORD.checkType(tok);
        CmdToken next = tokenizer.nextToken();
        CmdLineHandler.Mode mode;
        if ( next.getType() == TokenType.COMMA ) {
            mode = handler.value(output, token, name.getValue(), toStringArray(list(tok)));
        }
        else {
            tokenizer.pushback(next);
            mode = handler.value(output, token, name.getValue(), tok.getValue());
        }
        if ( mode == CmdLineHandler.Mode.END_OF_LINE ) {
            handler.end(output, token, tokenizer.toEndOfLine(tok));
            return false;
        }
        else {
            return true;
        }
    }

    public List<CmdToken> list(CmdToken first) throws CmdLineSyntaxException {
        List<CmdToken> list = new LinkedList<CmdToken>();
        list.add(first);
        CmdToken tok;
        do {
            tok = tokenizer.nextToken();
            TokenType.WORD.checkType(tok);
            list.add(tok);
            tok = tokenizer.nextToken();
        }
        while ( tok.getType() == TokenType.COMMA );
        tokenizer.pushback(tok);
        return list;
    }

    public String[] toStringArray(List<CmdToken> list) {
        String[] result = new String[list.size()];
        int index = 0;
        for ( CmdToken tok : list ) {
            result[index++] = tok.getValue();
        }
        return result;
    }

    private static enum TokenType {

        WORD, COLON, COMMA, END;

        public void checkType(CmdToken tok) throws CmdLineSyntaxException {
            if ( tok.getType() != this ) {
                throw new CmdLineSyntaxException(this + " expected, but got " + tok.getType());
            }
        }
    }

    private static class Tokenizer {

        private final CharSequence source;
        private int cursor = 0;
        private int tokenStart;
        private CmdToken pushback = null;
        private boolean atEnd;
        private boolean endTokenReturned = false;
        private StringBuilder buf;

        private Tokenizer(CharSequence source) {
            this.source = source;
        }

        public CmdToken nextToken() throws CmdLineSyntaxException {
            if ( pushback != null ) {
                CmdToken ret = pushback;
                pushback = null;
                return ret;
            }
            if ( endTokenReturned ) {
                throw new IllegalStateException("End of input reached");
            }
            skipWhitespace();
            if ( atEnd ) {
                endTokenReturned = true;
                return new CmdToken(TokenType.END, cursor, null);
            }
            switch ( getChar() ) {
                case '"':
                    return quotedWord('"');
                case '\'':
                    return quotedWord('\'');
                case ':':
                    move();
                    return new CmdToken(TokenType.COLON, cursor-1, ":");
                case ',':
                    move();
                    return new CmdToken(TokenType.COMMA, cursor-1, ",");
                default:
                    return word();
            }
        }

        public String toEndOfLine(CmdToken from) throws CmdLineSyntaxException {
            StringBuilder buf = new StringBuilder();
            StringBuilder wsBuf = new StringBuilder();
            cursor = from.start;
            while ( cursor < source.length() ) {
                char c = source.charAt(cursor);
                if ( Character.isWhitespace(c) ) {
                    wsBuf.append(c);
                }
                else {
                    if ( wsBuf.length() > 0 ) {
                        buf.append(wsBuf);
                        wsBuf.setLength(0);
                    }
                    buf.append(c);
                }
                cursor++;
            }
            atEnd = true;
            return buf.toString();
        }

        public void pushback(CmdToken cmdToken) {
            if ( pushback != null ) {
                throw new IllegalStateException("Pushback buffer full");
            }
            pushback = cmdToken;
        }

        public boolean hasMoreTokens() {
            return !endTokenReturned && pushback == null;
        }

        private CmdToken quotedWord(char delimiter) throws CmdLineSyntaxException {
            int start = cursor;
            StringBuilder buf = getBuffer();
            while ( true ) {
                if ( !move() ) {
                    throw new CmdLineSyntaxException("Unterminated string");
                }
                char c = getChar();
                if ( c == delimiter ) {
                    move();
                    if ( !atEnd && getChar() == delimiter ) {
                        buf.append(delimiter);
                    }
                    else {
                        return new CmdToken(TokenType.WORD, start, buf.toString(), true);
                    }
                }
                //else if ( c == '\\' ) {
                //    if ( !move() ) {
                //        throw new CmdLineSyntaxException("Unterminated string");
                //    }
                //    buf.append(escape());
                //}
                else {
                    buf.append(c);
                }
            }
        }

        //private char escape() throws CmdLineSyntaxException {
        //    char c = getChar();
        //    if ( c == 'u' ) {
        //        int val = 0;
        //        for ( int i = 0; i < 4; i++ ) {
        //            if ( !move() ) {
        //                throw new CmdLineSyntaxException("Unterminated string");
        //            }
        //            c = Character.toUpperCase(getChar());
        //            val <<= 4;
        //            if ( c >= '0' || c <= '9' ) {
        //                val |= c - '0';
        //            }
        //            else if ( c >= 'A' || c <= 'F' ) {
        //                val |= c - 'A' + 10;
        //            }
        //            else {
        //                throw new CmdLineSyntaxException("Invalid unicode sequence");
        //            }
        //        }
        //        return (char)val;
        //    }
        //    else if ( c >= '0' && c <= '7' ) {
        //        int val = 0;
        //        for ( int i = 0; i < 3; i++ ) {
        //            val <<= 3;
        //            if ( c >= '0' && c <= '7' ) {
        //                val |= c - '0';
        //                if ( !move() ) {
        //                    throw new CmdLineSyntaxException("Unterminated string");
        //                }
        //                c = getChar();
        //            }
        //            else if ( i != 2 ) {
        //                throw new CmdLineSyntaxException("Invalid octal escape sequence");
        //            }
        //        }
        //        return (char)val;
        //    }
        //    else {
        //        switch ( c ) {
        //            case 'n':
        //                return '\n';
        //            case 'r':
        //                return '\r';
        //            case 't':
        //                return '\t';
        //            case 'f':
        //                return '\f';
        //            case '"':
        //                return '"';
        //            case '\'':
        //                return '\'';
        //            case '\\':
        //                return '\\';
        //            default:
        //                throw new CmdLineSyntaxException("Invalid escape sequence");
        //        }
        //    }
        //}

        private CmdToken word() {
            int start = cursor;
            StringBuilder buf = getBuffer();
            while ( !Character.isWhitespace(getChar()) && getChar() != ':' && getChar() != ',' ) {
                buf.append(getChar());
                if ( !move() ) {
                    break;
                }
            }
            return new CmdToken(TokenType.WORD, start, buf.toString(), false);
        }

        private char getChar() {
            return source.charAt(cursor);
        }

        private StringBuilder getBuffer() {
            if ( buf == null ) {
                buf = new StringBuilder();
            }
            else {
                buf.setLength(0);
            }
            return buf;
        }

        private void skipWhitespace() {
            skipWhitespace(null);
        }
        private void skipWhitespace(StringBuilder buf) {
            if ( atEnd ) {
                return;
            }
            while ( Character.isWhitespace(getChar()) ) {
                if ( buf != null ) {
                    buf.append(getChar());
                }
                if ( !move() ) {
                    return;
                }
            }
        }

        private boolean move() {
            return move(1);
        }

        private boolean move(int offset) {
            cursor += offset;
            if ( cursor < 0 ) {
                throw new IllegalStateException("Cursor < 0");
            }
            if ( cursor >= source.length() ) {
                atEnd = true;
            }
            else {
                char c = source.charAt(cursor);
                if ( Character.isISOControl(c) || !Character.isDefined(c) ) {
                    throw new IllegalArgumentException("Illegal character at position " + cursor);
                }
            }
            return !atEnd;
        }
    }

    private static class CmdToken {

        private final TokenType type;
        private final int start;
        private final String value;
        private final boolean quoted;

        private CmdToken(TokenType type, int start, String value) {
            this(type, start, value, false);
        }

        private CmdToken(TokenType type, int start, String value, boolean quoted) {
            this.type = type;
            this.start = start;
            this.value = value;
            this.quoted = quoted;
        }

        @Override
        public String toString() {
            return type + ":" + start + ":'" + value + "'";
        }

        public TokenType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

    private class HandlerWrapper implements CmdLineHandler {
        private final CmdLineHandler delegate;
        private HandlerWrapper(CmdLineHandler delegate) {
            this.delegate = delegate;
        }
        @Override
        public Mode command(PrintWriter output, Token token, String prefix, String cmd) throws CmdLineException {
            try {
                return delegate.command(output, token, prefix, cmd);
            }
            catch ( Exception e ) {
                throw propagate(e);
            }
        }
        @Override
        public Mode value(PrintWriter output, Token token, String name, String value) throws CmdLineException {
            try {
                return delegate.value(output, token, name, value);
            }
            catch ( Exception e ) {
                throw propagate(e);
            }
        }
        @Override
        public Mode value(PrintWriter output, Token token, String name, String[] value) throws CmdLineException {
            try {
                return delegate.value(output, token, name, value);
            }
            catch ( Exception e ) {
                throw propagate(e);
            }
        }
        @Override
        public void end(PrintWriter output, Token token, String endOfLine) throws CmdLineException {
            try {
                delegate.end(output, token, endOfLine);
            }
            catch ( Exception e ) {
                throw propagate(e);
            }
        }

        @Override
        public void help(PrintWriter output) {
            delegate.help(output);
        }

        private CmdLineException propagate(Throwable exception) throws CmdLineException {
            Throwables.propagateIfPossible(exception, CmdLineException.class);
            return new CmdLineException("Exception handling command: " + exception, exception);
        }
    }
    
}
