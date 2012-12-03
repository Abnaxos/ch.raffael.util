package ch.raffael.util.cli;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CmdLineTokenizer {

    private String quoteChars = "\"'";
    private char escapeChar = '\\';
    private String commentChars = "#";
    private boolean javaEscapesEnabled = true;

    private String source;
    private int position = 0;
    private int current = -1;
    private boolean escaped;
    private final StringBuilder buf = new StringBuilder();

    public CmdLineTokenizer() {
        reset(null);
    }

    private void reset(String source) {
        this.source = source;
        position = 0;
        current = -1;
        escaped = false;
    }

    public String getQuoteChars() {
        return quoteChars;
    }

    public void setQuoteChars(String quoteChars) {
        this.quoteChars = quoteChars;
    }

    public char getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(char escapeChar) {
        this.escapeChar = escapeChar;
    }

    public String getCommentChars() {
        return commentChars;
    }

    public void setCommentChars(String commentChars) {
        this.commentChars = commentChars;
    }

    public boolean isJavaEscapesEnabled() {
        return javaEscapesEnabled;
    }

    public void setJavaEscapesEnabled(boolean javaEscapesEnabled) {
        this.javaEscapesEnabled = javaEscapesEnabled;
    }

    public CmdLine toCmdLine(String source) throws CmdLineSyntaxException {
        List<String> words = new ArrayList<String>();
        reset(source);
        next();
        for ( String word = nextWord(); word != null; word = nextWord() ) {
            words.add(word);
        }
        if ( words.isEmpty() ) {
            return null;
        }
        else {
            return new CmdLine(words.get(0), words.subList(1, words.size()).toArray(new String[words.size() - 1]));
        }
    }

    private String nextWord() throws CmdLineSyntaxException {
        buf.setLength(0);
        while ( true ) {
            if ( current < 0 ) {
                return null;
            }
            else if ( commentChars.indexOf(current) >= 0 ) {
                if ( escaped ) {
                    break;
                }
                else {
                    current = -1;
                    return null;
                }
            }
            else if ( !isWhitespace(current) || escaped ) {
                break;
            }
            next();
        }
        if ( quoteChars.indexOf(current) >= 0 && !escaped ) {
            int quote = current;
            while ( true ) {
                next();
                if ( current < 0 ) {
                    throw new CmdLineSyntaxException("Unterminated quote");
                }
                else if ( current == quote && !escaped ) {
                    next();
                    return buf.toString();
                }
                else {
                    buf.append((char)current);
                }
            }
        }
        else {
            while ( true ) {
                buf.append((char)current);
                next();
                if ( current < 0 ) {
                    return buf.toString();
                }
                else if ( isWhitespace(current) && !escaped ) {
                    return buf.toString();
                }
                else if ( commentChars.indexOf(current) >= 0 && !escaped ) {
                    return buf.toString();
                }
            }
        }
    }

    private void next() throws CmdLineSyntaxException {
        if ( position >= source.length() ) {
            current = -1;
            return;
        }
        escaped = false;
        current = source.charAt(position);
        position++;
        if ( current != escapeChar ) {
            return;
        }
        if ( position >= source.length() ) {
            current = -1;
            throw new CmdLineSyntaxException("Unterminated escape sequence");
        }
        escaped = true;
        current = source.charAt(position);
        position++;
        if ( current == escapeChar ) {
            return;
        }
        else if ( quoteChars.indexOf(current) >= 0 ) {
            return;
        }
        else if ( isWhitespace(current) ) {
            return;
        }
        else if ( commentChars.indexOf(current) >= 0 ) {
            return;
        }
        else if ( javaEscapesEnabled ) {
            if ( current >= '0' && current <= '7' ) {
                // octal escape
                current = current - '0';
                for ( int i = 0; i < 2; i++ ) {
                    if ( position >= source.length() ) {
                        return;
                    }
                    char c = source.charAt(position);
                    position++;
                    if ( c >= '0' && c <= '7' ) {
                        int val = 8 * current + (c - '0');
                        if ( val > 255 ) {
                            position--;
                            return;
                        }
                        current = val;
                    }
                    else {
                        position--;
                        return;
                    }
                }
                return;
            }
            else if ( current == 'u' ) {
                // unicode
                current = 0;
                for ( int i = 0; i < 4; i++ ) {
                    if ( position >= source.length() ) {
                        throw new CmdLineSyntaxException("Invalid escape sequence");
                    }
                    current = current * 16 + hexDigit(source.charAt(position));
                    position++;
                }
                return;
            }
            else {
                switch ( current ) {
                    case 'n':
                        current = '\n';
                        return;
                    case 'r':
                        current = '\r';
                        return;
                    case 't':
                        current = '\t';
                        return;
                    case 'b':
                        current = '\b';
                        return;
                    case 'f':
                        current = '\f';
                        return;
                }
            }
        }
        throw new CmdLineSyntaxException("Invalid escape sequence");
    }

    private static int hexDigit(int c) throws CmdLineSyntaxException {
        if ( c >= '0' && c <= '9' ) {
            return c - '0';
        }
        c = toUpperCase(c);
        if ( c >= 'A' && c <= 'F' ) {
            return c - 'A' + 10;
        }
        else {
            throw new CmdLineSyntaxException("Invalid escape sequence");
        }
    }

}
