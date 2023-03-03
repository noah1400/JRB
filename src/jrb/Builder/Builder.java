package jrb.Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import jrb.Exceptions.BuilderException;
import jrb.Exceptions.ImplementationException;
import jrb.Exceptions.JRBException;
import jrb.Exceptions.SyntaxException;
import jrb.Interfaces.TestMethodProvider;
import jrb.Mapper.SimpleMapper;

public class Builder extends TestMethodProvider {
    
    public final static int METHOD_TYPE_BEGIN = 0b00001;
    public final static int METHOD_TYPE_CHARACTER = 0b00010;
    public final static int METHOD_TYPE_GROUP = 0b00100;
    public final static int METHOD_TYPE_QUANTIFIER = 0b01000;
    public final static int METHOD_TYPE_ANCHOR = 0b10000;
    public final static int METHOD_TYPE_UNKNOWN = 0b11111;
    public final static int METHOD_TYPES_ALLOWED_FOR_CHARACTERS = METHOD_TYPE_BEGIN | METHOD_TYPE_CHARACTER | METHOD_TYPE_GROUP | METHOD_TYPE_QUANTIFIER | METHOD_TYPE_ANCHOR;

    /*
     * RegEx being built
     */
    protected ArrayList<String> regEx = new ArrayList<String>();

    /*
     * Raw modifiers to apply on get();
     */
    protected String modifiers = "";

    /*
     * Type  of last method, to avoid invalid builds;
     */
    protected int lastMethodType  = METHOD_TYPE_BEGIN;

    /*
     * Map method names to actual modifiers
     */
    protected Map<String, String> modifierMapper = new HashMap<String, String>(){{
        put("multiLine", "m");
        put("singleLine", "s");
        put("caseInsensitive", "i");
        put("unicode", "u");
        put("allLazy", "U");
    }};

    /*
     * Desired group if any
     */
    protected String group = "%s";

    /*
     * String to implode with
     */
    protected String implodeString = "";

    /**
     * CHARACTERS
     */

    /**
     * Add raw Regular Expression to  current expression
     * @throws BuilderException
     */
    public Builder raw(String regularExpression) throws BuilderException {
        this.lastMethodType = METHOD_TYPE_UNKNOWN;

        this.add(regularExpression);

        if (!this.isValid(null)) {
            this.revertLast();
            throw new BuilderException("Adding raw would invalidate this regular expression. Reverted.");
        }

        return this;
    }

    /**
     * Literal match one of these characters
     * @param string chars
     * @return Builder
     * @throws JRBException
     */
    public Builder oneOf(String chars) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, "OneOf");

        chars = this.escape(chars);

        return this.add("[" + chars + "]");
    }

    /**
     * Literally match anything but one  these characters
     * @param chars
     * @return Builder
     * @throws JRBException
     */
    public Builder notOneOf(String chars) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        chars = this.escape(chars);

        return this.add("[^" + chars + "]");
    }

    /**
     * Literally match all of these characters in that  order.
     * @param string chars
     * @return Builder
     * @throws JRBException
     */
    public Builder literally(String chars) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("(?:" + this.escape(chars) + ")");
    }


    /**
     * Match any digit (in given span). If no span is given, match any digit.
     * @param min
     * @param max
     * @return
     * @throws JRBException
     */
    public Builder digit(int min, int max) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("[" + min + "-" + max + "]");
    }
    public Builder digit() {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("\\d");
    }
    public Builder digit(Object o, Object o1) {
        return this.digit((int)o, (int)o1);
    }
    public Builder digit(Object o) {
        return this.digit();
    }

    /**
     * Match any non-digit (in given span). If no span is given, match any non-digit.
     * @param min
     * @param max
     * @return Builder
     * @throws JRBException
     */
    public Builder notDigit(int min, int max) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("[^" + min + "-" + max + "]");
    }
    public Builder notDigit() {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("\\D");
    }

    /**
     * Match any uppercase letter (in given span). If no span is given, match any uppercase letter.
     * @param min
     * @param max
     * @return Builder
     * @throws JRBException
     */
    public Builder uppercaseLetter(String min, String max) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("[" + min + "-" + max + "]");
    }
    public Builder uppercaseLetter() {
        return this.uppercaseLetter("A", "Z");
    }
    public Builder uppercaseLetter(Object o, Object o1) {
        return this.uppercaseLetter((String)o, (String)o1);
    }
    public Builder uppercaseLetter(Object o) {
        return this.uppercaseLetter();
    }

    /**
     * Match any lowercase letter (in given span). If no span is given, match any lowercase letter.
     * @param min
     * @param max
     * @return Builder
     * @throws JRBException
     */
    public Builder letter(String  min, String max) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("[" + min + "-" + max + "]");
    }
    public Builder letter() {
        return this.letter("a", "z");
    }
    public Builder letter(Object o, Object o1) {
        return this.letter((String)o, (String)o1);
    }
    public Builder letter(Object o) {
        return this.letter();
    }

    /**
     * Match any non-uppercase letter (in given span). If no span is given, match any non-uppercase letter.
     * @param min
     * @param max
     * @return Builder
     * @throws JRBException
     */
    public Builder notUppercaseLetter(String min, String max) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("[^" + min + "-" + max + "]");
    }
    public Builder notUppercaseLetter() {
        return this.notUppercaseLetter("A", "Z");
    }

    public Builder notLetter(String min, String max) {
        this.validateAndAddMethodType(METHOD_TYPE_CHARACTER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.add("[^" + min + "-" + max + "]");
    }
    public Builder notLetter()  {
        return this.notLetter("a", "z");
    }

    /**
     * GROUPS
     */

    /**
     * Match any of these conditions
     * 
     * @param Closure|Builder|String conditions Anonymous function with its Builder as a first parameter, Builder or String
     * @return Builder
     * @throws JRBException
     */
    public Builder anyOf(String conditions) {
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new EitherOf(), conditions);
    }
    public Builder anyOf(Builder conditions) {
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new EitherOf(), conditions);
    }
    public Builder anyOf(Consumer<Builder> conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new EitherOf(), conditions);
    }
    @SuppressWarnings("unchecked")
    public Builder anyOf(Object conditions) {
        if (conditions instanceof String) {
            return this.anyOf((String) conditions);
        } else if (conditions instanceof Builder) {
            return this.anyOf((Builder) conditions);
        } else if (conditions instanceof Consumer) {
            return this.anyOf((Consumer<Builder>) conditions);
        } else {
            return null;
        }
    }

    /**
     * Match all of these conditions, but in a non-capturing group
     * @param Closure|Builder|String conditions Anonymous function with its Builder as a first parameter, Builder or String
     * @return Builder
     * @throws JRBException
     */
    public Builder group(String conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new NonCapture(), conditions);
    }
    public Builder group(Builder conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new NonCapture(), conditions);
    }
    public Builder group(Consumer<Builder> conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new NonCapture(), conditions);
    }

    /**
     * Match all of these conditions. Basically reverts back to the default mode, if coming from anyOf, etc.
     * 
     * @param Closure|Builder|String conditions Anonymous function with its Builder as a first parameter, Builder or String
     * @return Builder
     * @throws JRBException
     */
    public Builder and(String conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Builder(), conditions);
    }
    public Builder and(Builder conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Builder(), conditions);
    }
    public Builder and(Consumer<Builder> conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Builder(), conditions);
    }

    /**
     * Positive lookbehind. Match the previous condition only if given conditions already occurred.
     * 
     * @param Closure|Builder|String conditions Anonymous function with its Builder as a first parameter, Builder or String
     * @return Builder
     * @throws JRBException
     */
    public Builder ifAlreadyHad(String conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        String condition = this.revertLast();

        this.addClosure(new PositiveLookbehind(), conditions);

        return this.add(condition);
    }
    public Builder ifAlreadyHad(Builder conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        String condition = this.revertLast();

        this.addClosure(new PositiveLookbehind(), conditions);

        return this.add(condition);
    }
    public Builder ifAlreadyHad(Consumer<Builder> conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        String condition = this.revertLast();

        this.addClosure(new PositiveLookbehind(), conditions);

        return this.add(condition);
    }

    /**
     * Negative lookbehind. Match the previous condition only if given conditions did not already occur.
     * 
     * @param Closure|Builder|String conditions
     * @return Builder
     * @throws JRBException
     */
    public Builder ifNotAlreadyHad(String conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        String condition = this.revertLast();

        this.addClosure(new NegativeLookbehind(), conditions);

        return this.add(condition);
    }
    public Builder ifNotAlreadyHad(Builder conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        String condition = this.revertLast();

        this.addClosure(new NegativeLookbehind(), conditions);

        return this.add(condition);
    }
    public Builder ifNotAlreadyHad(Consumer<Builder> conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        String condition = this.revertLast();

        this.addClosure(new NegativeLookbehind(), conditions);

        return this.add(condition);
    }

    /**
     * Positive lookahead. Match the previous condition only if followed by given conditions.
     * 
     * @param conditions
     * @return
     * @throws JRBException
     */
    public Builder ifFollowedBy(String conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new PositiveLookahead(), conditions);
    }
    public Builder ifFollowedBy(Builder conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new PositiveLookahead(), conditions);
    }
    public Builder ifFollowedBy(Consumer<Builder> conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new PositiveLookahead(), conditions);
    }

    /**
     * Negative lookahead. Match the previous condition only if not followed by given conditions.
     * 
     * @param conditions
     * @return
     * @throws JRBException
     */
    public Builder ifNotFollowedBy(String conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new NegativeLookahead(), conditions);
    }
    public Builder ifNotFollowedBy(Builder conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new NegativeLookahead(), conditions);
    }
    public Builder ifNotFollowedBy(Consumer<Builder> conditions){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new NegativeLookahead(), conditions);
    }
    /**
     * Create capture group for given conditions.
     * 
     * @param Closure|Builder|string conditions Anonymous function with its Builder as a first parameter.
     * @param String name
     * @return Builder
     * @throws JRBException
     */
    public Builder capture(String conditions, String name){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        Capture builder = new Capture();

        if (name != null) {
            builder.setName(name);
        }

        return this.addClosure(builder, conditions);
    }
    public Builder capture(String conditions){
        return this.capture(conditions, null);
    }
    public Builder capture(Builder conditions, String name){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        Capture builder = new Capture();

        if (name != null) {
            builder.setName(name);
        }

        return this.addClosure(builder, conditions);
    }
    public Builder capture(Builder conditions){
        return this.capture(conditions, null);
    }
    public Builder capture(Consumer<Builder> conditions, String name){
        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        Capture builder = new Capture();

        if (name != null) {
            builder.setName(name);
        }

        return this.addClosure(builder, conditions);
    }
    public Builder capture(Consumer<Builder> conditions){
        return this.capture(conditions, null);
    }

    /**
     * QUANTIFIERS
     */

    /**
     * Make the last or given condition optional.
     * 
     * @param null|Closure|Builder|String conditions Anonymous function with its Builder as a first parameter, Builder or String
     * @return Builder
     * @throws JRBException
     */
    public Builder optional(String conditions) {
        this.validateAndAddMethodType(METHOD_TYPE_QUANTIFIER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Optional(), conditions);
    }
    public Builder optional(Builder conditions) {
        this.validateAndAddMethodType(METHOD_TYPE_QUANTIFIER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Optional(), conditions);
    }
    public Builder optional(Consumer<Builder> conditions) {
        this.validateAndAddMethodType(METHOD_TYPE_QUANTIFIER, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Optional(), conditions);
    }
    public Builder optional() {
        return this.add("?");
    }

    /**
     * Previous match must occur so often.
     * 
     * @param int min
     * @param int max
     * @return Builder
     * @throws JRBException
     */
    public Builder between(int min, int max){
        this.validateAndAddMethodType(METHOD_TYPE_QUANTIFIER, METHOD_TYPE_CHARACTER | METHOD_TYPE_GROUP, null);

        return this.add(String.format("{%d,%d}", min, max));
    }

    /**
     * Previous match must occur at least this often.
     * 
     * @param min
     * @return Builder
     * @throws JRBException
     */
    public Builder atLeast(int min) {
        this.validateAndAddMethodType(METHOD_TYPE_QUANTIFIER, METHOD_TYPE_CHARACTER | METHOD_TYPE_GROUP, null);

        return this.add(String.format("{%d,}", min));
    }

    /**
     * Previous match must occur exactly once.
     * 
     * @return Builder
     * @throws JRBException
     */
    public Builder once(){
        return this.exactly(1);
    }
    public Builder once(Object o){
        return this.once();
    }

    /**
     * Previous match must occur exactly twice.
     * 
     * @return Builder
     * @throws JRBException
     */
    public Builder twice(){
        return this.exactly(2);
    }
    public Builder twice(Object o){
        return this.twice();
    }

    /**
     * Previous match must occur exactly this often.
     * 
     * @param int times
     * @return Builder
     * @throws JRBException
     */
    public Builder exactly(int times){
        this.validateAndAddMethodType(METHOD_TYPE_QUANTIFIER, METHOD_TYPE_CHARACTER | METHOD_TYPE_GROUP, null);

        return this.add(String.format("{%d}", times));
    }
    public Builder exactly(Object o) {
        return this.exactly((int) o);
    }


    /**
     * Get first match instead of last (lazy).
     * @return
     * @throws ImplementationException
     */
    public Builder lazy() throws ImplementationException {
        return this.firstMatch();
    }
    public Builder lazy(Object o) throws ImplementationException {
        return this.lazy();
    }

    /**
     * Applay laziness to the last match.
     * 
     * @return Builder
     * @throws ImplementationException
     */
    public Builder firstMatch() throws ImplementationException {

        this.lastMethodType = METHOD_TYPE_QUANTIFIER;

        if (this.getModifiers().indexOf("+*}?", this.getRawRegex().length() - 1) == -1) {
            if (this.regEx.get(this.regEx.size() - 1).endsWith(")") && this.getModifiers().indexOf("+*}?", this.getRawRegex().length() - 2) != -1) {
                return this.add(this.revertLast().substring(0, this.revertLast().length() - 1) + "?)");
            }

            throw new ImplementationException("Cannot apply laziness at this point. Only applicable after quantifiers.");
        }

        return this.add("?");
    }
    public Builder firstMatch(Object o) throws ImplementationException {
        return this.firstMatch();
    }

    /**
     * Match up to the given condition.
     * 
     * @param toCondition
     * @return  Builder
     * @throws JRBException
     */
    public Builder until(String toCondition){
        try{
            this.lazy();
        }catch(ImplementationException e){
        }

        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Builder(), toCondition);
    }
    public Builder until(Builder toCondition){
        try{
            this.lazy();
        }catch(ImplementationException e){
        }

        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Builder(), toCondition);
    }
    public Builder until(Consumer<Builder> toCondition){
        try{
            this.lazy();
        }catch(ImplementationException e){
        }

        this.validateAndAddMethodType(METHOD_TYPE_GROUP, METHOD_TYPES_ALLOWED_FOR_CHARACTERS, null);

        return this.addClosure(new Builder(), toCondition);
    }
    @SuppressWarnings("unchecked")
    public Builder until(Object o) {
        if (o instanceof String) {
            return this.until((String) o);
        } else if (o instanceof Builder) {
            return this.until((Builder) o);
        } else if (o instanceof Consumer) {
            return this.until((Consumer<Builder>) o);
        }
        return null;
    }

    /**
     * METHODS
     */

    public Builder startsWith(){
        return this.addFromMapper("startsWith");
    }
    public Builder startsWith(Object obj){
        return this.startsWith();
    }
    public Builder mustEnd(){
        return this.addFromMapper("mustEnd");
    }
    public Builder mustEnd(Object obj){
        return this.mustEnd();
    }
    public Builder onceOrMore(){
        return this.addFromMapper("onceOrMore");
    }
    public Builder onceOrMore(Object obj){
        return this.onceOrMore();
    }
    public Builder neverOrMore(){
        return this.addFromMapper("neverOrMore");
    }
    public Builder neverOrMore(Object obj){
        return this.neverOrMore();
    }
    public Builder any(){
        return this.addFromMapper("any");
    }
    public Builder any(Object obj){
        return this.any();
    }
    public Builder tab(){
        return this.addFromMapper("tab");
    }
    public Builder tab(Object obj){
        return this.tab();
    }
    public Builder newLine(){
        return this.addFromMapper("newLine");
    }
    public Builder newLine(Object obj){
        return this.newLine();
    }
    public Builder whitespace(){
        return this.addFromMapper("whitespace");
    }
    public Builder whitespace(Object obj){
        return this.whitespace();
    }
    public Builder noWhitespace(){
        return this.addFromMapper("noWhitespace");
    }
    public Builder noWhitespace(Object obj){
        return this.noWhitespace();
    }
    public Builder anyCharacter(){
        return this.addFromMapper("anyCharacter");
    }
    public Builder anyCharacter(Object obj){
        return this.anyCharacter();
    }
    public Builder noCharacter(){
        return this.addFromMapper("noCharacter");
    }
    public Builder noCharacter(Object obj){
        return this.noCharacter();
    }
    public Builder backslash(){
        return this.addFromMapper("backslash");
    }
    public Builder backslash(Object obj){
        return this.backslash();
    }

    /**
     * MODIFIERS
     */

    public Builder multiLine(){
        return this.addUniqueModifier(this.modifierMapper.get("multiLine"));
    }
    public Builder multiLine(Object obj){
        return this.multiLine();
    }
    public Builder singleLine(){
        return this.addUniqueModifier(this.modifierMapper.get("singleLine"));
    }
    public Builder singleLine(Object obj){
        return this.singleLine();
    }
    public Builder caseInsensitive(){
        return this.addUniqueModifier(this.modifierMapper.get("caseInsensitive"));
    }
    public Builder caseInsensitive(Object obj){
        return this.caseInsensitive();
    }
    public Builder unicode(){
        return this.addUniqueModifier(this.modifierMapper.get("unicode"));
    }
    public Builder unicode(Object obj){
        return this.unicode();
    }
    public Builder allLazy(){
        return this.addUniqueModifier(this.modifierMapper.get("allLazy"));
    }
    public Builder allLazy(Object obj){
        return this.allLazy();
    }


    /**
     * INTERNAL METHODS
     */
    
    /**
     * Escape all characters in string
    * 
    */
    protected String escape(String string) {
        return string.replaceAll("([\\\\\\.\\[\\]\\(\\)\\{\\}\\+\\*\\?\\^\\$\\|\\-])", "\\\\$1");
    }
    protected String escape(Object o) {
        return this.escape(o.toString());
    }

    /**
     * Get raw regular expression without delimiter or  modifiers
     */
    protected String getRawRegex() {
        return String.format(this.getGroup(), String.join(this.getImplodeString(), this.regEx));
    }

    /**
     * Get all set modifiers
     */
    protected String getModifiers() {
        return this.modifiers;
    }
    
    /**
     * Add condition to the expression query
     */
    protected Builder add(String condition) {
        this.regEx.add(condition);
        return this;
    }

    /**
     * Validate method call. This will throw an exception if the called method makes no sense at this point.
     * Will add the current type as the last method type.
     *
     * @param int $type
     * @param int $allowed
     * @param string|null $methodName Optional. If not supplied, the calling method name will be used.
     * @throws ImplementationException
     */
    protected void validateAndAddMethodType(int type, int allowed, String methodName) {
        if ( (allowed & this.lastMethodType) != 0) {
            this.lastMethodType = type;

            return;
        }

        String humanText = "";
        switch (this.lastMethodType) {
            case METHOD_TYPE_BEGIN:
                humanText = "at the beginning";
                break;
            case METHOD_TYPE_CHARACTER:
                humanText = "after a literal character";
                break;
            case METHOD_TYPE_GROUP:
                humanText = "after a group";
                break;
            case METHOD_TYPE_QUANTIFIER:
                humanText = "after a quantifier";
                break;
            case METHOD_TYPE_ANCHOR:
                humanText = "after an anchor";
                break;
        }

        try {
            throw new ImplementationException(String.format(
                "Method `%s` is not allowed %s.",
                methodName != null ? methodName : new Exception().getStackTrace()[1].getMethodName(),
                humanText != null ? humanText : "here"
            ));
        } catch (ImplementationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Add the value from the simple mapper array to the regular expression.
     *
     * @param string $name
     * @throws BuilderException
     * @return Builder
     */
    protected Builder addFromMapper(String name){

        if (!SimpleMapper.mapper.containsKey(name)) {
            try {
                throw new BuilderException("Unknown mapper.");
            } catch (BuilderException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        this.validateAndAddMethodType(
            SimpleMapper.mapper.get(name).type,
            SimpleMapper.mapper.get(name).allowed,
            name
        );

        return this.add(SimpleMapper.mapper.get(name).add);
    }

    /**
     * Add a specific unique modifier. This will ignore all modifiers already set.
     *
     * @param string $modifier
     * @return Builder
     */
    protected Builder addUniqueModifier(String modifier) {

        if (!this.modifiers.contains(modifier)) {
            this.modifiers += modifier;
        }

        return this;
    }

    /**
     * Build the given Closure or string and append it to the current expression.
     *
     * @param Builder $builder
     * @param Closure|Builder|string $conditions Either a closure, literal character string or another Builder instance.
     * @return Builder
     */
    protected Builder addClosure(Builder builder, String conditions) {
        builder.literally(conditions);
        return this.add(builder.get("", false));
    }

    protected Builder addClosure(Builder builder, Builder conditions) {
        try {
            builder.raw(((Builder) conditions).get("", false));
        } catch (BuilderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this.add(builder.get("", false));
    }

    protected Builder addClosure(Builder builder, Consumer<Builder> conditions) {
        conditions.accept(builder);
        return this.add(builder.get("", false));
    }

    /**
     * Get and remove last added element.
     * @return string
     */
    protected String revertLast() {
        return this.regEx.remove(this.regEx.size() - 1);
    }

    /**
     * get
     * @throws SyntaxException
     */
    public String get(String delimiter, boolean ignoreInvalid) {
        if (delimiter == null || delimiter.isEmpty()) {
            return this.getRawRegex();
        }

        String regEx = String.format(
            "%s%s%s%s",
            delimiter,
            this.getRawRegex().replaceAll(delimiter, "\\" + delimiter),
            delimiter,
            this.getModifiers()
        );

        if (!ignoreInvalid && !this.isValid(regEx)) {
            try {
                throw new SyntaxException("Generated expression seems to be invalid.");
            } catch (SyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return regEx;
    }
    public String get() {
        return this.get("/", false);
    }

    /**
     * MAGIC METHODS
     */
    
    /**
     * Try adding modifiers if their methods are defined in the modifierMapper attribute.
     *
     * @param $name
     * @param $arguments
     * @throws ImplementationException
     * @return Builder
     */
    public Builder call(String name, Object[] arguments) throws JRBException
    {
        if (SimpleMapper.mapper.containsKey(name)) {
            // Simple mapper exists, add its character to the regex
            return this.addFromMapper(name);
        }

        if (modifierMapper.containsKey(name)) {
            // Modifier exists, add it
            return this.addUniqueModifier(modifierMapper.get(name));
        }

        throw new ImplementationException(String.format(
            "Call to undefined or invalid method %s:%s()",
            this.getClass().getName(),
            name
        ));
    }

    /**
     * Builde and return the resulting regular expression.
     */
    public String toString() {
        return this.get("/", false);
    }

    /**
     * Get group string
     */
    protected String getGroup() {
        return this.group;
    }

    /**
     * Get implode string
     */
    protected String getImplodeString() {
        return this.implodeString;
    }

}
