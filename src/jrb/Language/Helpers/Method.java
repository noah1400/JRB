package jrb.Language.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import jrb.Builder.Builder;
import jrb.Exceptions.SyntaxException;
import jrb.Language.Interpreter;

public abstract class Method {
    
    /*
     * Contains the original method name (case-sensitive)
     */
    protected String original;

    /*
     * Contains the method name to excute
     */
    protected String methodName;

    /*
     * Containse the parsed parameters to pass on execution
     */
    protected Map<String, Object> parameters = new HashMap<>();

    private ArrayList<String> executedCallbacks = new ArrayList<>();

    public Method(String original, String methodName) {
        this.original = original;
        this.methodName = methodName;
    }

    public String getOriginal() {
        return this.original;
    }

    @SuppressWarnings("unchecked")
    public Builder callMethodOn(Builder builder) {
        
        // PHP: $response = $builder->{$this->methodName}(...$this->parameters);
        
        try {
            Builder response = (Builder)builder.getClass().getMethod(this.methodName, Object.class).invoke(builder, this.parameters);
            // PHP:
            // foreach ($this->parameters as $k => $parameter) {
            //     if ($parameter instanceof Closure && !in_array($k, $this->executedCallbacks)) {
            //         // Callback wasn't executed, but expected to. Assuming parentheses without method, so let's "and" it.
            //         $builder->group($parameter);
            //     }
            // }
            // Transform to Java:
            for (Map.Entry<String, Object> entry : this.parameters.entrySet()) {
                if (entry.getValue() instanceof Consumer && !this.parameters.containsKey(entry.getKey())) {
                    // Callback wasn't executed, but expected to. Assuming parentheses without method, so let's "and" it.
                    builder.group((Consumer<Builder>)entry.getValue());
                }
            }
            
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Method setParameters(Object[] parameters) {
        for (Map.Entry<String, Object> entry : this.parameters.entrySet()) {
            if (entry.getValue() instanceof Literally) {
                this.parameters.put(entry.getKey(), ((Literally)entry.getValue()).getString());
            } else if (entry.getValue() instanceof Object[]) {

                Consumer<Builder> cb = (Consumer<Builder>)query -> {
                    this.executedCallbacks.add(entry.getKey());
                    try {
                        Interpreter.buildQuery((Object[])entry.getValue(), query);
                    } catch (SyntaxException e) {
                        e.printStackTrace();
                    }
                };
                this.parameters.put(entry.getKey(), cb);
            }
        }
        return this;
    }
}
