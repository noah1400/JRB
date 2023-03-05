package jrb.Language;

import java.util.ArrayList;
import java.util.List;

import jrb.Builder.Builder;
import jrb.Builder.Capture;
import jrb.Exceptions.InterpreterException;
import jrb.Exceptions.SyntaxException;
import jrb.Interfaces.TestMethodProvider;
import jrb.Language.Commands.Command;
import jrb.Language.Commands.Anchors.BeginWith;
import jrb.Language.Commands.Anchors.MustEnd;
import jrb.Language.Commands.Characters.AnyCharacter;
import jrb.Language.Commands.Characters.Anything;
import jrb.Language.Commands.Characters.Backslash;
import jrb.Language.Commands.Characters.Digit;
import jrb.Language.Commands.Characters.Letter;
import jrb.Language.Commands.Characters.Literally;
import jrb.Language.Commands.Characters.NewLine;
import jrb.Language.Commands.Characters.NoCharacter;
import jrb.Language.Commands.Characters.OneOf;
import jrb.Language.Commands.Characters.Raw;
import jrb.Language.Commands.Characters.Tab;
import jrb.Language.Commands.Characters.Whitespace;
import jrb.Language.Commands.Flags.AllLazy;
import jrb.Language.Commands.Flags.CaseInsensitive;
import jrb.Language.Commands.Flags.MultiLine;
import jrb.Language.Commands.Groups.CaptureAs;
import jrb.Language.Commands.Quantifiers.AtLeast;
import jrb.Language.Commands.Quantifiers.Between;
import jrb.Language.Commands.Quantifiers.Exactly;
import jrb.Language.Commands.Quantifiers.NeverOrMore;
import jrb.Language.Commands.Quantifiers.OnceOrMore;
import jrb.Language.Commands.Quantifiers.Optional;
import jrb.Language.Helpers.Cache;
import jrb.Language.Helpers.Tokenizer.Token;
import jrb.Language.Helpers.Tokenizer.Tokenizer;

public class Interpreter extends TestMethodProvider {

    /*
     * The raw JRL query
     */
    protected String rawQuery;

    /*
     * The resolved but not executed JRL query
     */
    protected ArrayList<Token> tokenizedQuery;

    /*
     * The resolved and executed JRL query
     */
    protected Builder builder;

    public Interpreter(String query) throws SyntaxException, InterpreterException {
        // PHP: $this->rawQuery = rtrim(trim($query), ';');
        this.rawQuery = query.trim().replaceAll(";$", "");

        // search for the JRL query in the local cache before building it.
        if (Cache.has(this.rawQuery)) {
            this.builder = Cache.get(this.rawQuery);
        } else {
            this.build();
        }
    }

    public void build() throws SyntaxException, InterpreterException {
        this.resolve();

        this.builder = this.buildQuery(tokenizedQuery, null);

        // add the JRL query to the local cache
        Cache.add(this.rawQuery, this.builder);
    }

    protected void resolve() throws SyntaxException, InterpreterException {
        this.tokenizedQuery = new Tokenizer(rawQuery).tokenize();
    }

    private ArrayList<Command> resolveQuery(List<Token> list)
            throws InterpreterException, SyntaxException, IndexOutOfBoundsException {
        ArrayList<Command> commands = new ArrayList<Command>();
        for (int i = 0; i < list.size(); i++) {
            Token token = list.get(i);

            // Anchors
            if (token.matches("starts") || token.matches("begin")) {
                if (this.inBounds(i+1, list) && list.get(i + 1).matches("with")) {

                    i++; // skip the next token because it's already been used
                    commands.add(new BeginWith());
                    continue;
                } else {
                    throw new SyntaxException("Expected 'with' after 'starts' or 'begin' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            } else if (token.matches("must")) {
                if (this.inBounds(i+1, list) && list.get(i + 1).matches("end")) {
                    commands.add(new MustEnd());
                    i++; // skip the next token because it's already been used
                    continue;
                } else {
                    throw new SyntaxException("Expected 'end' after 'must' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            }

            // Flags
            else if (token.matches("case")) {
                if (this.inBounds(i+1, list) && list.get(i + 1).matches("insensitive")) {
                    CaseInsensitive cI = new CaseInsensitive();
                    i++; // last token of the command
                    i = addQuantifier(cI, i, list); // returns the last token of the quantifier
                    commands.add(cI);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'insensitive' after 'case' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            } else if (token.matches("multi")) {
                if (this.inBounds(i+1, list) && list.get(i + 1).matches("line")) {
                    MultiLine mL = new MultiLine();
                    i++; // last token of the command
                    i = addQuantifier(mL, i, list); // returns the last token of the quantifier
                    commands.add(mL);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'line' after 'multi' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            } else if (token.matches("all")) {
                if (this.inBounds(i+1, list) && list.get(i + 1).matches("lazy")) {
                    AllLazy aL = new AllLazy();
                    i++; // last token of the command
                    i = addQuantifier(aL, i, list); // returns the last token of the quantifier
                    commands.add(aL);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'lazy' after 'all' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            }

            // Characters
            else if (token.matches("literally")) {
                if (this.inBounds(i+1, list) && list.get(i + 1).matches(Token.T_STRING)) {
                    Literally literally = new Literally(list.get(i + 1).raw);
                    i++; // last token of the command
                    i = addQuantifier(literally, i, list); // returns the last token of the quantifier
                    commands.add(literally);
                    continue;
                } else {
                    throw new SyntaxException("Expected a string after 'literally' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            } else if (token.matches("one")) {
                if (this.inBounds(i+2, list) && list.get(i + 1).matches("of") && list.get(i + 2).matches(Token.T_STRING)) {
                    OneOf oneOf = new OneOf(list.get(i + 2).raw);
                    i += 2; // last token of the command
                    i = addQuantifier(oneOf, i, list); // returns the last token of the quantifier
                    commands.add(oneOf);
                    continue;
                } else {
                    throw new SyntaxException("Expected a string after 'one of' but got '"
                            + list.get(i + 2).raw + "' at position " + list.get(i + 2).position);
                }
            } else if (token.matches("uppercase")) {
                if (this.inBounds(i+1, list) && list.get(i + 1).matches("letter")) {
                    // check for span
                    // from a to z
                    if (this.inBoundsPeak(i+5, list)) {
                        // from a to z
                        Token from = list.get(i + 2);
                        Token a = list.get(i + 3);
                        Token to = list.get(i + 4);
                        Token z = list.get(i + 5);
                        if (to.matches("to") && a.character() && z.character()) {
                            Letter letter = new Letter(true);
                            letter.setSpan(a.raw, z.raw);
                            i += 5; // last token of the command
                            i = addQuantifier(letter, i, list); // returns the last token of the quantifier
                            commands.add(letter);
                            continue;
                        } else {
                            throw new SyntaxException(
                                    "Expected 'from' and 'to' and two characters after 'letter' but got '"
                                            + from.raw + "' and '" + a.raw + "' at position " + a.position);
                        }
                    } else {
                        Letter letter = new Letter(true);
                        i += 1; // last token of the command
                        i = addQuantifier(letter, i, list); // returns the last token of the quantifier
                        commands.add(letter);
                    }
                } else {
                    throw new SyntaxException("Expected 'letter' after 'uppercase' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            } else if (token.matches("letter")) {
                // check for span
                // from a to z
                Token from = list.get(i + 1);
                if (from.matches("from")) {
                    Token a = list.get(i + 2);
                    Token to = list.get(i + 3);
                    Token z = list.get(i + 4);

                    if (to.matches("to") && a.character() && z.character()) {

                        Letter letter = new Letter(false);
                        letter.setSpan(a.raw, z.raw);
                        i += 4; // last token of the command
                        i = addQuantifier(letter, i, list); // returns the last token of the quantifier
                        commands.add(letter);
                        continue;
                    } else {
                        throw new SyntaxException("Expected 'from' and 'to' and two characters after 'letter' but got '"
                                + from.raw + "' and '" + a.raw + "' at position " + a.position);
                    }
                } else {
                    Letter letter = new Letter(false);
                    i = addQuantifier(letter, i, list); // returns the last token of the quantifier
                    commands.add(letter);
                    continue;
                }

            } else if (token.matches("any")) {
                if (list.get(i + 1).matches("character")) {
                    AnyCharacter anyCharacter = new AnyCharacter();
                    i++; // last token of the command
                    i = addQuantifier(anyCharacter, i, list); // returns the last token of the quantifier
                    commands.add(anyCharacter);
                    continue;
                } else {
                    // DO NOTHING
                    // Since token can still match Any Of (see below)
                }
            } else if (token.matches("no")) {
                if (list.get(i + 1).matches("character")) {
                    NoCharacter noCharacter = new NoCharacter();
                    i++; // last token of the command
                    i = addQuantifier(noCharacter, i, list); // returns the last token of the quantifier
                    commands.add(noCharacter);
                    continue;
                } else if (list.get(i + 1).matches("whitespace")) {
                    Whitespace whitespace = new Whitespace(true);
                    i++; // last token of the command
                    i = addQuantifier(whitespace, i, list); // returns the last token of the quantifier
                    commands.add(whitespace);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'character' or 'whitespace' after 'no' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            } else if (token.matches("digit")) {
                Digit digit = new Digit();
                Token from = list.get(i + 1);
                if (from.matches("from")) {
                    Token a = list.get(i + 2);
                    Token to = list.get(i + 3);
                    Token z = list.get(i + 4);
                    if (to.matches("to") && a.number() && z.number()) {
                        digit.setSpan(a.raw, z.raw);
                        i += 4; // last token of the command
                        i = addQuantifier(digit, i, list); // returns the last token of the quantifier
                        commands.add(digit);
                        continue;
                    } else {
                        throw new SyntaxException("Expected 'from' and 'to' and two numbers after 'digit' but got '"
                                + from.raw + "', '" + to.raw + "' and '" + a.raw + "' and '" + z.raw + "' at position "
                                + a.position);
                    }
                } else {
                    i = addQuantifier(digit, i, list); // returns the last token of the quantifier
                    commands.add(digit);
                    continue;
                }
            } else if (token.matches("anything")) {
                Anything anything = new Anything();
                i = addQuantifier(anything, i, list); // returns the last token of the quantifier
                commands.add(anything);
                continue;
            } else if (token.matches("new")) {
                if (list.get(i + 1).matches("line")) {
                    NewLine newLine = new NewLine();
                    i++; // last token of the command
                    i = addQuantifier(newLine, i, list); // returns the last token of the quantifier
                    commands.add(newLine);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'line' after 'new' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            } else if (token.matches("whitespace")) {
                Whitespace whitespace = new Whitespace(false);
                i = addQuantifier(whitespace, i, list); // returns the last token of the quantifier
                commands.add(whitespace);
                continue;
            } else if (token.matches("tab")) {
                Tab tab = new Tab();
                i = addQuantifier(tab, i, list); // returns the last token of the quantifier
                commands.add(tab);
                continue;
            } else if (token.matches("backslash")) {
                Backslash backslash = new Backslash();
                i = addQuantifier(backslash, i, list); // returns the last token of the quantifier
                commands.add(backslash);
                continue;
            } else if (token.matches("raw")) {
                if (list.get(i + 1).matches(Token.T_STRING)) {
                    Raw raw = new Raw(list.get(i + 1).raw);
                    i++; // last token of the command
                    i = addQuantifier(raw, i, list); // returns the last token of the quantifier
                    commands.add(raw);
                    continue;
                } else {
                    throw new SyntaxException("Expected a string after 'raw' but got '"
                            + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
                }
            } 
            
            // Groups:
            else if (token.matches("capture")) {
                this.inBounds(i+1, list); // another token needs to exist after capture
                i++;
                if (list.get(i).matches(Token.T_STRING)) {
                    ArrayList<Command> gCommands = new ArrayList<>();
                    gCommands.add(new Literally(list.get(i).raw));
                    String name = "";
                    if (this.inBoundsPeak(i+1, list) && list.get(i+1).matches("as")){
                        this.inBounds(i+2, list); // another token needs to exist after as
                        i++;
                        i++;
                        name = list.get(i).raw;
                    }
                    CaptureAs capture = null;
                    if (name.isEmpty()){
                        capture = new CaptureAs(gCommands);
                    }else{
                        capture = new CaptureAs(gCommands, name);
                    }
                    i = addQuantifier(capture, i, list); // returns the last token of the quantifier
                    commands.add(capture);
                } else if (list.get(i).matches(Token.T_LPAREN)){
                    this.inBounds(i+1, list); // another token needs to exist after (
                    int open = i;
                    int close = this.findMatchingParenthesis(i, list);
                    i = close;
                    ArrayList<Command> gCommands = this.resolveQuery(list.subList(open+1, close));
                    String name = "";
                    if (this.inBoundsPeak(i+1, list) && list.get(i+1).matches("as")){
                        this.inBounds(i+2, list); // another token needs to exist after as
                        i++;
                        i++;
                        name = list.get(i).raw;
                    }
                    CaptureAs capture = null;
                    if (name.isEmpty()){
                        capture = new CaptureAs(gCommands);
                    }else{
                        capture = new CaptureAs(gCommands, name);
                    }
                    i = addQuantifier(capture, i, list); // returns the last token of the quantifier
                    commands.add(capture);
                } else {
                    throw new SyntaxException("Expected a string or a group after 'capture' but got '"
                            + list.get(i).raw + "' at position " + list.get(i).position);
                }
            }
            
            
            else {
                throw new SyntaxException("Unexpected token '" + token.raw + "' at position " + token.position);
            }
        }

        return commands;
    }

    private int findMatchingParenthesis(int i, List<Token> list) throws SyntaxException {
        int open = 0;
        int close = 0;
        for (int j = i; j < list.size(); j++) {
            if (list.get(j).matches(Token.T_LPAREN)) {
                open++;
            } else if (list.get(j).matches(Token.T_RPAREN)) {
                close++;
            }
            if (open == close) {
                return j;
            }
        }
        throw new SyntaxException("No matching parenthesis found for '(' at position " + list.get(i).position);
    }

    /*
     * Add a quantifier to the last command in the commands array
     * 
     * @param command The command to add the quantifier to
     * 
     * @param position The index of the last token of the command in the tokens
     * 
     * position+1 The index of the first token of the quantifier if it exists
     * 
     * @return The index of the last token of the quantifier if it exists
     */
    private int addQuantifier(Command command, int position, List<Token> list) throws SyntaxException {
        int i = position + 1; // would be first token of the quantifier

        if (!this.inBoundsPeak(i, list)){
            return position;
        }

        Token token = list.get(i);
        if (token.matches("exactly")) {
            this.inBounds(i + 2, list);
            if (list.get(i + 1).matches(Token.T_NUMBER)
                    && (list.get(i + 2).matches("times") || list.get(i + 2).matches("time"))) {
                command.setQuantifier(new Exactly(Integer.parseInt(list.get(i + 1).raw)));
                // return
                // the index of the last token of the quantifier (the "times" or "time" token)
                return i + 2;
            }
        } else if (token.matches("between")) {
            this.inBounds(i + 4, list);
            Token x = list.get(i + 1);
            Token and = list.get(i + 2);
            Token y = list.get(i + 3);
            Token times = list.get(i + 4);
            if (x.matches(Token.T_NUMBER) && and.matches("and") && y.matches(Token.T_NUMBER)
                    && times.matches("times")) {
                command.setQuantifier(new Between(Integer.parseInt(x.raw), Integer.parseInt(y.raw)));
                // return
                // the index of the last token of the quantifier (the "times" token)
                return i + 4;
            } else {
                throw new SyntaxException("Expected '<number> and <number> times' after 'between' but got '"
                        + x.raw + "', '" + and.raw + "' and '" + y.raw + "' and '" + times.raw + "' at position "
                        + x.position);
            }
        } else if (token.matches("optional")) {
            command.setQuantifier(new Optional());
            // return
            // the index of the last token of the quantifier (the "optional" token)
            return i;
        } else if (token.matches("once") || token.matches("never")) {
            if (this.inBounds(i+2, list) && list.get(i + 1).matches("or") && list.get(i + 2).matches("more")) {
                if (token.matches("once")) {
                    command.setQuantifier(new OnceOrMore());
                    // return
                    // the index of the last token of the quantifier (the "more" token)
                    return i + 2;
                } else if (token.matches("never")) {
                    command.setQuantifier(new NeverOrMore());
                    // return
                    // the index of the last token of the quantifier (the "more" token)
                    return i + 2;
                }
            } else if (token.matches("once")) {
                command.setQuantifier(new Exactly(1));
                return i;
            } else {
                throw new SyntaxException("Expected 'or more' after 'never' but got '"
                        + list.get(i + 1).raw + "' at position " + list.get(i + 1).position);
            }
        } else if (token.matches("twice")) {
            command.setQuantifier(new Exactly(2));
            return i;
        } else if (token.matches("at")) {
            this.inBounds(i+1, list);
            this.inBounds(i+2, list);
            this.inBounds(i+3, list);
            Token least = list.get(i + 1);
            Token x = list.get(i + 2);
            Token times = list.get(i + 3);
            if (least.matches("least") && x.matches(Token.T_NUMBER) && times.matches("times")) {
                command.setQuantifier(new AtLeast(Integer.parseInt(x.raw)));
                // return
                // the index of the last token of the quantifier (the "times" token)
                return i + 3;
            } else {
                throw new SyntaxException("Expected 'at least' and a number and 'times' after 'at least' but got '"
                        + least.raw + "', '" + x.raw + "' and '" + times.raw + "' at position "
                        + least.position);
            }
        }
        // if no quantifier was found, return the index of the last token of the command
        return position;
    }

    /*
     * Checks if the the index is in bounds of the tokens array
     * 
     */
    private boolean inBounds(int i, List<Token> list) throws SyntaxException {
        if (i >= 0 && i < list.size()) {
            return true;
        } else {
            if (list.isEmpty()){
                throw new SyntaxException("Invalid end of query");
            }else{
                throw new SyntaxException("Invalid end of query at position " + list.get(list.size() - 1).position);
            }
        }
    }

    /*
     * Checks if the the index is in bounds of the tokens array
     * Same as inBounds but doesn't throw an exception
     * 
     */
    private boolean inBoundsPeak(int i, List<Token> list) {
        if (i >= 0 && i < list.size()) {
            return true;
        } else {
            return false;
        }
    }

    public Builder buildQuery(ArrayList<Token> tokens, Builder builder)
            throws SyntaxException, InterpreterException {

        ArrayList<Command> commands = this.resolveQuery(tokens);
        if (builder == null) {
            builder = new Builder();
        }
        for (Command command : commands) {
            builder = command.callMethodOn(builder);
        }
        return builder;
    }

    @Override
    public String get(String delimiter, boolean ignoreInvalid) {
        return this.builder.get(delimiter, ignoreInvalid);
    }

    public String get() {
        return this.builder.get("/", false);
    }

    public Builder getBuilder() {
        return this.builder;
    }

    public String getRawQuery() {
        return this.rawQuery;
    }
}
