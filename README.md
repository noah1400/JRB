# JRB
JRB (Java Regex Builder) is a Java version of [SimpleRegex/SRL](https://github.com/SimpleRegex/SRL-PHP)


##### Email validation
```java
String query = JRB.builder()
        .startsWith()
        .anyOf(
            new Closure() {
                @Override
                public void execute(Builder builder) throws JRBException {
                    builder
                    .digit()
                    .letter()
                    .oneOf("._%+-");
                }
            }
        ).onceOrMore()
        .literally("@")
        .anyOf(
            new Closure() {
                @Override
                public void execute(Builder builder) throws JRBException {
                    builder
                    .digit()
                    .letter()
                    .oneOf(".-");
                }
            }
        ).onceOrMore()
        .literally(".")
        .letter().atLeast(2).mustEnd().caseInsensitive()
        .get();
```
Outputs
```txt
/^(?:\d|[a-z]|[\._%\+\-])+(?:@)(?:\d|[a-z]|[\.\-])+(?:\.)[a-z]{2,}$/i
```
