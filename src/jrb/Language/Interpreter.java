package jrb.Language;

import java.util.ArrayList;

import jrb.Builder.Builder;
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

        this.builder = Interpreter.buildQuery(tokenizedQuery, null);

        // add the JRL query to the local cache
        Cache.add(this.rawQuery, this.builder);
    }

    protected void resolve() throws SyntaxException, InterpreterException {
        this.tokenizedQuery = new Tokenizer(rawQuery).tokenize();
    }

    private static ArrayList<Command> resolveQuery(ArrayList<Token> tokens)
            throws InterpreterException, SyntaxException, IndexOutOfBoundsException {
        ArrayList<Command> commands = new ArrayList<Command>();
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Anchors
            if (token.matches("starts") || token.matches("begin")) {
                if (tokens.get(i + 1).matches("with")) {

                    i++; // skip the next token because it's already been used
                    commands.add(new BeginWith());
                    continue;
                } else {
                    throw new SyntaxException("Expected 'with' after 'starts' or 'begin' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else if (token.matches("must")) {
                if (tokens.get(i + 1).matches("end")) {
                    commands.add(new MustEnd());
                    i++; // skip the next token because it's already been used
                    continue;
                } else {
                    throw new SyntaxException("Expected 'end' after 'must' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            }

            // Flags
            else if (token.matches("case")) {
                if (tokens.get(i + 1).matches("insensitive")) {
                    CaseInsensitive cI = new CaseInsensitive();
                    i++; // last token of the command
                    i = addQuantifier(cI, i, tokens); // returns the last token of the quantifier
                    commands.add(cI);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'insensitive' after 'case' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else if (token.matches("multi")) {
                if (tokens.get(i + 1).matches("line")) {
                    MultiLine mL = new MultiLine();
                    i++; // last token of the command
                    i = addQuantifier(mL, i, tokens); // returns the last token of the quantifier
                    commands.add(mL);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'line' after 'multi' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else if (token.matches("all")) {
                if (tokens.get(i + 1).matches("lazy")) {
                    AllLazy aL = new AllLazy();
                    i++; // last token of the command
                    i = addQuantifier(aL, i, tokens); // returns the last token of the quantifier
                    commands.add(aL);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'lazy' after 'all' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            }

            // Characters
            else if (token.matches("literally")) {
                if (tokens.get(i + 1).matches(Token.T_STRING)) {
                    Literally literally = new Literally(tokens.get(i + 1).raw);
                    i++; // last token of the command
                    i = addQuantifier(literally, i, tokens); // returns the last token of the quantifier
                    commands.add(literally);
                    continue;
                } else {
                    throw new SyntaxException("Expected a string after 'literally' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else if (token.matches("one")) {
                if (tokens.get(i + 1).matches("of") && tokens.get(i + 2).matches(Token.T_STRING)) {
                    OneOf oneOf = new OneOf(tokens.get(i + 2).raw);
                    i += 2; // last token of the command
                    i = addQuantifier(oneOf, i, tokens); // returns the last token of the quantifier
                    commands.add(oneOf);
                    continue;
                } else {
                    throw new SyntaxException("Expected a string after 'one of' but got '"
                            + tokens.get(i + 2).raw + "' at position " + tokens.get(i + 2).position);
                }
            } else if (token.matches("uppercase")) {
                if (tokens.get(i + 1).matches("letter")) {
                    // check for span
                    // from a to z
                    Token from = tokens.get(i + 2);
                    if (from.matches("from")) {
                        // from a to z
                        Token a = tokens.get(i + 3);
                        Token to = tokens.get(i + 4);
                        Token z = tokens.get(i + 5);
                        if (to.matches("to") && a.character() && z.character()) {
                            Letter letter = new Letter(true);
                            letter.setSpan(a.raw, z.raw);
                            i += 5; // last token of the command
                            i = addQuantifier(letter, i, tokens); // returns the last token of the quantifier
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
                        i = addQuantifier(letter, i, tokens); // returns the last token of the quantifier
                        commands.add(letter);
                    }
                } else {
                    throw new SyntaxException("Expected 'letter' after 'uppercase' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else if (token.matches("letter")) {
                // check for span
                // from a to z
                Token from = tokens.get(i + 1);
                if (from.matches("from")) {
                    Token a = tokens.get(i + 2);
                    Token to = tokens.get(i + 3);
                    Token z = tokens.get(i + 4);

                    if (to.matches("to") && a.character() && z.character()) {

                        Letter letter = new Letter(false);
                        letter.setSpan(a.raw, z.raw);
                        i += 4; // last token of the command
                        System.out.println("i: " + i + " " + tokens.get(i).raw);
                        i = addQuantifier(letter, i, tokens); // returns the last token of the quantifier
                        commands.add(letter);
                        continue;
                    } else {
                        throw new SyntaxException("Expected 'from' and 'to' and two characters after 'letter' but got '"
                                + from.raw + "' and '" + a.raw + "' at position " + a.position);
                    }
                } else {
                    Letter letter = new Letter(false);
                    i = addQuantifier(letter, i, tokens); // returns the last token of the quantifier
                    commands.add(letter);
                    continue;
                }

            } else if (token.matches("any")) {
                if (tokens.get(i + 1).matches("character")) {
                    AnyCharacter anyCharacter = new AnyCharacter();
                    i++; // last token of the command
                    i = addQuantifier(anyCharacter, i, tokens); // returns the last token of the quantifier
                    commands.add(anyCharacter);
                    continue;
                } else {
                    // DO NOTHING
                    // Since token can still match Any Of (see below)
                }
            } else if (token.matches("no")) {
                if (tokens.get(i + 1).matches("character")) {
                    NoCharacter noCharacter = new NoCharacter();
                    i++; // last token of the command
                    i = addQuantifier(noCharacter, i, tokens); // returns the last token of the quantifier
                    commands.add(noCharacter);
                    continue;
                } else if (tokens.get(i + 1).matches("whitespace")) {
                    Whitespace whitespace = new Whitespace(true);
                    i++; // last token of the command
                    i = addQuantifier(whitespace, i, tokens); // returns the last token of the quantifier
                    commands.add(whitespace);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'character' or 'whitespace' after 'no' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else if (token.matches("digit")) {
                Digit digit = new Digit();
                Token from = tokens.get(i + 1);
                if (from.matches("from")) {
                    Token a = tokens.get(i + 2);
                    Token to = tokens.get(i + 3);
                    Token z = tokens.get(i + 4);
                    if (to.matches("to") && a.number() && z.number()) {
                        digit.setSpan(a.raw, z.raw);
                        i += 4; // last token of the command
                        i = addQuantifier(digit, i, tokens); // returns the last token of the quantifier
                        commands.add(digit);
                        continue;
                    } else {
                        throw new SyntaxException("Expected 'from' and 'to' and two numbers after 'digit' but got '"
                                + from.raw + "', '" + to.raw + "' and '" + a.raw + "' and '" + z.raw + "' at position "
                                + a.position);
                    }
                } else {
                    i = addQuantifier(digit, i, tokens); // returns the last token of the quantifier
                    commands.add(digit);
                    continue;
                }
            } else if (token.matches("anything")) {
                Anything anything = new Anything();
                i = addQuantifier(anything, i, tokens); // returns the last token of the quantifier
                commands.add(anything);
                continue;
            } else if (token.matches("new")) {
                if (tokens.get(i + 1).matches("line")) {
                    NewLine newLine = new NewLine();
                    i++; // last token of the command
                    i = addQuantifier(newLine, i, tokens); // returns the last token of the quantifier
                    commands.add(newLine);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'line' after 'new' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else if (token.matches("whitespace")) {
                Whitespace whitespace = new Whitespace(false);
                i = addQuantifier(whitespace, i, tokens); // returns the last token of the quantifier
                commands.add(whitespace);
                continue;
            } else if (token.matches("tab")) {
                Tab tab = new Tab();
                i = addQuantifier(tab, i, tokens); // returns the last token of the quantifier
                commands.add(tab);
                continue;
            } else if (token.matches("backslash")) {
                Backslash backslash = new Backslash();
                i = addQuantifier(backslash, i, tokens); // returns the last token of the quantifier
                commands.add(backslash);
                continue;
            } else if (token.matches("raw")) {
                if (tokens.get(i + 1).matches(Token.T_STRING)) {
                    Raw raw = new Raw(tokens.get(i + 1).raw);
                    i++; // last token of the command
                    i = addQuantifier(raw, i, tokens); // returns the last token of the quantifier
                    commands.add(raw);
                    continue;
                } else {
                    throw new SyntaxException("Expected a string after 'raw' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else {
                throw new SyntaxException("Unexpected token '" + token.raw + "' at position " + token.position);
            }
        }

        return commands;
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
    private static int addQuantifier(Command command, int position, ArrayList<Token> tokens) throws SyntaxException {
        int i = position + 1; // would be first token of the quantifier

        if (i >= tokens.size()) {
            return position;
        }

        System.out.println("addQuantifier: " + tokens.get(i).raw);

        Token token = tokens.get(i);
        if (token.matches("exactly")) {
            if (tokens.get(i + 1).matches(Token.T_NUMBER)
                    && (tokens.get(i + 2).matches("times") || tokens.get(i + 2).matches("time"))) {
                command.setQuantifier(new Exactly(Integer.parseInt(tokens.get(i + 1).raw)));
                // return
                // the index of the last token of the quantifier (the "times" or "time" token)
                return i + 2;
            }
        } else if (token.matches("between")) {

            Token x = tokens.get(i + 1);
            Token and = tokens.get(i + 2);
            Token y = tokens.get(i + 3);
            Token times = tokens.get(i + 4);
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
            if (tokens.get(i + 1).matches("or") && tokens.get(i + 2).matches("more")) {
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
                        + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
            }
        } else if (token.matches("twice")) {
            command.setQuantifier(new Exactly(2));
            return i;
        } else if (token.matches("at")) {
            Token least = tokens.get(i + 1);
            Token x = tokens.get(i + 2);
            Token times = tokens.get(i + 3);
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

    public static Builder buildQuery(ArrayList<Token> tokens, Builder builder)
            throws SyntaxException, InterpreterException {

        ArrayList<Command> commands = Interpreter.resolveQuery(tokens);
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
