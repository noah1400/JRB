package jrb.Language.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import jrb.Exceptions.SyntaxException;

public class ParenthesesParser {
    
    protected String string = "";

    public ParenthesesParser(String string) {
        this.setString(string);
    }

    public ParenthesesParser setString(String string) {
        
        if  (!this.string.isBlank()) {
            return this;
        }

        if (string.charAt(0) == '(' && string.charAt(string.length()-1) == ')') {
            string = string.substring(1, string.length()-1);
        }

        this.string = string;

        return this;
    }

    public String[] parse() throws SyntaxException {
        return this.parseString(this.string);
    }

    protected String[] parseString(String string) throws SyntaxException {

        int openCount = 0;
        int openPos = 0;
        int closePos = 0;
        boolean inString = false;
        char stringChar = ' ';
        boolean backslash = false;
        List<StringPosition> stringPositions = new ArrayList<>();
        int stringLength = string.length();

        outerLoop:
        for (int i = 0; i < stringLength; i++) {
            char c = string.charAt(i);

            if (inString) {
                if ( c == stringChar && (string.charAt(i - 1) != '\\' || (string.charAt(i - 1) == '\\' && string.charAt(i - 2) == '\\'))) {
                    inString = false;
                    stringChar = ' ';
                    stringPositions.get(stringPositions.size() - 1).end = i - 1;
                }
                continue;
            }

            if (backslash) {
                backslash = false;
                continue;
            }

            switch (c) {
                case '\\':
                    backslash = true;
                    break;
                case '"':
                case '\'':
                    inString = true;
                    stringChar = c;
                    stringPositions.add(new StringPosition(i, -1));
                    break;
                case '(':
                    openCount++;
                    if (openPos == 0) {
                        openPos = i;
                    }
                    break;
                case ')':
                    openCount--;
                    if (openCount == 0) {
                        closePos = i;
                        break outerLoop;
                    }
                    break;
            }
        }

        if (openCount != 0) {
            throw new SyntaxException("Non-matching parentheses found. openCount=" + openCount);
        }

        if (closePos == 0) {
            openPos = closePos = stringLength;
        }

        Object[] ret = this.createLiterallyObjects(string, openPos, stringPositions);

        if (openPos != closePos) {
            // PHP:
            // $ret = array_merge(
            //     $ret, // First part is definitely without parentheses, since we'll match the first pair.
            //     // This is the inner part of the parentheses pair. There may be some more nested pairs, so we'll check them.
            //     [$this->parseString(substr($string, $openPos + 1, $closePos - $openPos - 1))],
            //     // Last part of the string wasn't checked at all, so we'll have to re-check it.
            //     $this->parseString(substr($string, $closePos + 1))
            // );
            // transform to java:
            ret = Stream.concat(
                Arrays.stream(ret),
                Stream.concat(
                    Stream.of(this.parseString(string.substring(openPos + 1, closePos))),
                    Arrays.stream(this.parseString(string.substring(closePos + 1)))
                )
            ).toArray();
        }
        
        ArrayList<String> ret4 = new ArrayList<>();
        for (Object obj : ret) {
            if (obj instanceof String) {
                if (!((String) obj).isBlank()) {
                    ret4.add((String) obj);
                }
            }
        }
        return ret4.toArray(new String[0]);
    }

    private Object[] createLiterallyObjects(String string2, int openPos, List<StringPosition> stringPositions) throws SyntaxException {


        String firstRaw = string2.substring(0, openPos);
        List<Object> ret = new ArrayList<>();
        ret.add(firstRaw.trim());
        int pointer = 0;

        for (StringPosition stringPosition : stringPositions) {
            if (stringPosition.end == StringPosition.EMPTY) {
                throw new SyntaxException("Invalid string ending found.");
            }
            
            if (stringPosition.end < firstRaw.length()) {
                
                ret.remove(ret.size() - 1);

                ret.add(firstRaw.substring(pointer, stringPosition.start).trim());

                ret.add(new Literally(
                    firstRaw.substring(stringPosition.start + 1, stringPosition.end)
                ));

                ret.add(firstRaw.substring(stringPosition.end + 2).trim());

                pointer = stringPosition.end + 2;
            }
        }

        return ret.toArray();
    }
}
