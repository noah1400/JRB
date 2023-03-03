# JRB

JRB (Java Regex Builder) is a Java version of [SimpleRegex/SRL](https://github.com/SimpleRegex/SRL-PHP)

Email validation

```java
String query = JRB.builder()
        .startsWith()
        .anyOf(
            JRB.builder().digit().letter().oneOf("._%+-")
        ).onceOrMore()
        .literally("@")
        .anyOf(
            (Builder builder) -> {
                builder
                .digit()
                .letter()
                .oneOf(".-");
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

Using the Language:

```txt
starts with digit between 3 and 5 times, letter twice, must end, multi line, case insensitive
```

Outputs:

```txt
/^\d{3,5}[a-z]{2}$/mi
```