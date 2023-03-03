package jrb.Language;

import java.util.ArrayList;

import jrb.Builder.Builder;
import jrb.Exceptions.InterpreterException;
import jrb.Exceptions.SyntaxException;
import jrb.Interfaces.TestMethodProvider;
import jrb.Language.Commands.Command;
import jrb.Language.Commands.Anchors.BeginWith;
import jrb.Language.Commands.Anchors.MustEnd;
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

    private static ArrayList<Command> resolveQuery(ArrayList<Token> tokens) throws InterpreterException, SyntaxException {
        ArrayList<Command> commands = new ArrayList<Command>();
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Anchors
            if (token.matches("starts") || token.matches("begin"))  {
                if (tokens.get(i + 1).matches("with")) {
                    
                    i++; // skip the next token because it's already been used
                    commands.add(new BeginWith());
                    continue;
                }else{
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
                    Command cI = new CaseInsensitive();
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
                    Command mL = new MultiLine();
                    i++; // last token of the command
                    i = addQuantifier(mL, i, tokens); // returns the last token of the quantifier
                    commands.add(mL);
                    continue;
                } else {
                    throw new SyntaxException("Expected 'line' after 'multi' but got '"
                            + tokens.get(i + 1).raw + "' at position " + tokens.get(i + 1).position);
                }
            } else if (token.matches("all")) {
                if (tokens.get(i+1).matches("lazy")) {
                    Command aL = new AllLazy();
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

        }
    }

    /*
     * Add a quantifier to the last command in the commands array
     * 
     * @param command The command to add the quantifier to
     * @param position The index of the last token of the command in the tokens
     * 
     * position+1 The index of the first token of the quantifier if it exists
     * 
     * @return The index of the last token of the quantifier if it exists
     */
    private static int addQuantifier(Command command, int position, ArrayList<Token> tokens) {
        int i = position + 1; // would be first token of the quantifier

        Token token = tokens.get(i);
        if (token.matches("exactly")) {
            if (tokens.get(i+1).matches(Token.T_NUMBER)
            && (tokens.get(i+2).matches("times") || tokens.get(i+2).matches("time"))){
                command.setQuantifier(new Exactly(Integer.parseInt(tokens.get(i+1).raw)));
                // return
                // the index of the last token of the quantifier (the "times" or "time" token)
                return i+2;
            }
        } else if (token.matches("between")) {
            Token x = tokens.get(i+1);
            Token and = tokens.get(i+2);
            Token y = tokens.get(i+3);
            Token times = tokens.get(i+4);
            if (x.matches(Token.T_NUMBER) && and.matches("and") && y.matches(Token.T_NUMBER)
                    && times.matches("times")) {
                command.setQuantifier(new Between(Integer.parseInt(x.raw), Integer.parseInt(y.raw)));
                // return
                // the index of the last token of the quantifier (the "times" token)
                return i+4;
            }
        }  else if (token.matches("optional")) {
            command.setQuantifier(new Optional());
            // return
            // the index of the last token of the quantifier (the "optional" token)
            return i;
        } else if (token.matches("once") || token.matches("never")) {
            if (tokens.get(i+1).matches("or") && tokens.get(i + 2).matches("more")) {
                if (token.matches("once")) {
                    command.setQuantifier(new OnceOrMore());
                    // return
                    // the index of the last token of the quantifier (the "more" token)
                    return i+2;
                } else if (token.matches("never")) {
                    command.setQuantifier(new NeverOrMore());
                    // return
                    // the index of the last token of the quantifier (the "more" token)
                    return i+2;
                }
            }else if (token.matches("once")){
                command.setQuantifier(new Exactly(1));
                return i;
            }
        } else if (token.matches("at")) {
            Token least = tokens.get(i+1);
            Token x = tokens.get(i+2);
            Token times = tokens.get(i+3);
            if (least.matches("least") && x.matches(Token.T_NUMBER) && times.matches("times")) {
                command.setQuantifier(new AtLeast(Integer.parseInt(x.raw)));
                // return
                // the index of the last token of the quantifier (the "times" token)
                return i+3;
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
