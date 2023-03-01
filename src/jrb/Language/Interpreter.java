package jrb.Language;

import jrb.Builder.Builder;
import jrb.Builder.NonCapture;
import jrb.Exceptions.InterpreterException;
import jrb.Exceptions.SyntaxException;
import jrb.Interfaces.TestMethodProvider;
import jrb.Language.Helpers.Cache;
import jrb.Language.Helpers.Literally;
import jrb.Language.Helpers.Matcher;
import jrb.Language.Helpers.Method;
import jrb.Language.Helpers.ParenthesesParser;

public class Interpreter extends TestMethodProvider{

    /*
     * The raw JRL query
     */
    protected String rawQuery;

    /*
     * The resolved but not executed JRL query
     */
    protected Object[] resolvedQuery;

    protected Matcher matcher;

    /*
     * The resolved and executed JRL query
     */
    protected Builder builder;

    public Interpreter(String query) throws SyntaxException, InterpreterException {
        // PHP: $this->rawQuery = rtrim(trim($query), ';');
        this.rawQuery = query.trim().replaceAll(";$", "");
        this.matcher = Matcher.getInstance();

        // search for the JRL query in the local cache before building it.
        if (Cache.has(this.rawQuery)) {
            this.builder = Cache.get(this.rawQuery);
        } else {
            this.build();
        }
    }

    public void build() throws SyntaxException, InterpreterException {
        this.resolve();

        this.builder = Interpreter.buildQuery(this.resolvedQuery, null);

        // add the JRL query to the local cache
        Cache.add(this.rawQuery, this.builder);
    }

    protected void resolve() throws InterpreterException {
        this.resolvedQuery = this.resolveQuery((new ParenthesesParser(this.rawQuery)).parse());
    }

    protected Object[] resolveQuery(Object[] query) throws InterpreterException {
        for (int i = 0; i < query.length; i++) {
            if (query[i] instanceof String) {
                query[i] = ((String)query[i]).replace(",", " ");
                if (((String)query[i]).isEmpty()) {
                    // PHP: array_splice($query, $i, 0);
                    Object[] temp = new Object[query.length - 1];
                    System.arraycopy(query, 0, temp, 0, i);
                    System.arraycopy(query, i + 1, temp, i, query.length - i - 1);
                    query = temp;
                    continue; 
                }
                try {
                    Method method = this.matcher.match((String)query[i]);

                    String leftOver = ((String)query[i]).replaceFirst("(?i)" + method.getOriginal(), "");
                    query[i] = method;
                    if (!leftOver.isEmpty()) {
                        // PHP: array_splice($query, $i + 1, 0, trim($leftOver));
                        Object[] temp = new Object[query.length + 1];
                        System.arraycopy(query, 0, temp, 0, i + 1);
                        temp[i + 1] = leftOver.trim();
                        System.arraycopy(query, i + 1, temp, i + 2, query.length - i - 1);
                        query = temp;
                    }
                } catch (Exception e) {
                    String[] split = ((String)query[i]).split("[\\s]+", 2);
                    query[i] = split[0].trim();
                    if (split.length > 1) {
                        Object[] temp = new Object[query.length + 1];
                        System.arraycopy(query, 0, temp, 0, i + 1);
                        temp[i + 1] = split[1].trim();
                        System.arraycopy(query, i + 1, temp, i + 2, query.length - i - 1);
                        query = temp;
                    }
                }
            } else if (query[i] instanceof Object[]) {
                query[i] = this.resolveQuery((Object[])query[i]);
            } else if (!(query[i] instanceof Literally)) {
                // PHP: throw new InterpreterException('Unexpected statement: ' . json_encode($query[$i]));
                throw new InterpreterException("Unexpected statement: " + query[i].toString());
            }
        }
        return query;
    }

    public static Builder buildQuery(Object[] query, Builder builder) throws SyntaxException {
        if (builder == null) {
            builder = new Builder();
        }
        for (int i = 0; i < query.length; i++) {
            Object method = query[i];
            if (method instanceof Object[]) {
                builder.and(buildQuery(query, new NonCapture()));
                continue;
            }

            if (!(method instanceof Method)) {
                throw new SyntaxException("Unexpected statement: " + method.toString());
            }

            Object[] parameters = new Object[0];

            // PHP: while (isset($query[$i + 1]) && !($query[$i + 1] instanceof Method))
            while (i + 1 < query.length && !(query[i + 1] instanceof Method)) {
                // PHP: $parameters[] = $query[$i + 1];
                Object[] temp = new Object[parameters.length + 1];
                System.arraycopy(parameters, 0, temp, 0, parameters.length);
                temp[parameters.length] = query[i + 1];
                parameters = temp;
                // PHP: array_splice($query, $i + 1, 1);
                Object[] temp2 = new Object[query.length - 1];
                System.arraycopy(query, 0, temp2, 0, i + 1);
                System.arraycopy(query, i + 2, temp2, i + 1, query.length - i - 2);
                query = temp2;
            }

            try {
                ((Method) method).setParameters(parameters).callMethodOn(builder);
            } catch (Exception e) {
                throw new SyntaxException("Invalid parameter given for " + ((Method) method).getOriginal()+".");
            }
        }
        return builder;
    }



    @Override
    public String get(String delimiter, boolean ignoreInvalid) {
        return this.builder.get(delimiter, ignoreInvalid);
    }
    
    public Builder getBuilder() {
        return this.builder;
    }

    public String getRawQuery() {
        return this.rawQuery;
    }
}
