# FastUUIDParser

`FastUUIDParser` is, as its name implies, a fast UUID parser for Java. It's a little more than four times faster than the stock JDK implementation in benchmarks and produces much less garbage collection pressure. It is intended for applications that spend a lot of time and energy parsing UUIDs.

## How it works

Let's take a look at the OpenJDK implementation of [`UUID#fromString(String)`](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html#fromString-java.lang.String-):

```java
public static UUID More ...fromString(String name) {
    String[] components = name.split("-");
    if (components.length != 5)
        throw new IllegalArgumentException("Invalid UUID string: "+name);
    for (int i=0; i<5; i++)
        components[i] = "0x"+components[i];

    long mostSigBits = Long.decode(components[0]).longValue();
    mostSigBits <<= 16;
    mostSigBits |= Long.decode(components[1]).longValue();
    mostSigBits <<= 16;
    mostSigBits |= Long.decode(components[2]).longValue();

    long leastSigBits = Long.decode(components[3]).longValue();
    leastSigBits <<= 48;
    leastSigBits |= Long.decode(components[4]).longValue();

    return new UUID(mostSigBits, leastSigBits);
}
```

If you're just dealing with UUIDs every now and then, this is just fine. If you're doing a lot of UUID parsing, though, there are a few things we might be concerned about here:

1. This implementation starts off by creating an array of (presumably) five sub-strings. This can be a bit slow in its own right, but beyond that, it also creates five new strings that will need to be garbage-collected eventually.
2. For each of those substrings, this implementation performs a string concatenation, which requires still more string allocation and eventual garbage collection.
3. Eventually, this implementation needs to convert hexadecimal stings into numbers. It does so with [`Long#decode(String)`](https://docs.oracle.com/javase/8/docs/api/java/lang/Long.html#decode-java.lang.String-) rather than using [`Long#parseLong(String, int)`](https://docs.oracle.com/javase/8/docs/api/java/lang/Long.html#parseLong-java.lang.String-int-), which means somebody else needs to do the work of figuring out which radix to use when parsing the strings. This seems unnecessary since we know for sure that we're dealing with hexadecimal strings.

It turns out a lot of these issues are interrelated, and we can untangle them to get a significant performance boost. By recognizing that we're always dealing with hexadecimal strings, for example, we can immediately resolve the third issue. Once we've done that, we don't need to concatenate strings to prepend `"0x"` to the beginning of each of our substrings. That alone [speeds things up by about 50%](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8192784) and cuts the number of string allocations (and presumably garbage collection pressure) in half.

That leaves the first problem: can we find a way to parse a UUID without breaking it into substrings first? It turns out we can! Here we have to move away from the handy parsing tools that the JDK provides us, though, and write some of our own. We can even go further and, because we know for sure that we're dealing with hexadecimal strings of a fixed length, we can write a parser that drops a lot of error-checking and flexibility and picks up a lot of speed in return. That's exactly what `FastUUIDParser` provides, and the result is that it can parse UUIDs a little more than four times faster than the default JDK implementation and, aside from the finished UUID, doesn't create anything on the heap that will need to get garbage-collected later.

Here are some benchmark results:

| Benchmark                          | Throughput                              |
|------------------------------------|-----------------------------------------|
| `UUID#fromString(String)`          | 1,633,510.644 ± 10,136.510 UUIDs/second |
| `FastUUIDParser#parseUUID(String)` | 6,691,400.272 ± 43,419.636 UUIDs/second |
