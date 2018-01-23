[![Build Status](https://travis-ci.org/jchambers/fast-uuid.svg?branch=master)](https://travis-ci.org/jchambers/fast-uuid)

# fast-uuid

`fast-uuid` is a Java library for quickly and efficiently parsing and writing UUIDs. In benchmarks, it's a little more than four times faster at parsing UUIDs and six times faster at writing UUIDs than the stock JDK implementation. It is intended for applications that work with large quantities of UUIDs or that work with UUIDs in performance-sensitive code.

## Usage

Using `fast-uuid` is simple. To parse UUIDs:

```java
UUID uuid = FastUUID.parseUUID(uuidStringOrCharacterSequence);
```

To convert UUIDs to strings:

```java
String uuidString = FastUUID.toString(uuid);
```

## How it works

### Parsing UUIDs

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

### UUIDs to strings

We've shown that we can significantly improve upon the stock `UUID#fromString(String)` implementation. Can we achieve similar gains in going from a `UUID` to a `String`? Let's take a look at the stock implementation of `UUID#toString()`:

```java
public String toString() {
    return (digits(mostSigBits >> 32, 8) + "-" +
            digits(mostSigBits >> 16, 4) + "-" +
            digits(mostSigBits, 4) + "-" +
            digits(leastSigBits >> 48, 4) + "-" +
            digits(leastSigBits, 12));
}

private static String digits(long val, int digits) {
    long hi = 1L << (digits * 4);
    return Long.toHexString(hi | (val & (hi - 1))).substring(1);
}
```

As before, we might notice a few areas of concern:

1. We're performing a lot of string concatenations. Each of those requires allocating space for a new string and ultimately garbage-collecting the intermediate strings. If we can find a way to do less concatenation, we might see some performance gains.
2. Furthermore, every call to `digits` produces two new strings (via the calls to `toHexString` and `substring`) that ultimately get discarded.

As before, we know some things about UUIDs that help us avoid some general-case error checking and trade some flexibility for performance. For example, we know that the string representation of a UUID will always be exactly 36 characters long (32 hexadecimal digits and four dashes). That means we can pre-allocate space by way of (for example) a [`StringBuilder`](https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html). That alone will save us quite a few string allocations and yield significant performance improvements.

As with UUID parsing, we can go further and write our own "to hexadecimal" method that uses our knowledge about the size and structure of UUID strings to place digits in exactly the right place in the finished string, reducing the need to get substrings and perform concatenations. In the end, this lets us convert UUIDs to strings more than six times faster (and, again, with much less garbage-collection pressure) than the stock implementation.

Some benchmark results:

| Benchmark                       | Throughput                           |
|---------------------------------|--------------------------------------|
| `UUID#toString()`               | 3,098,019.503 ± 13,478.983 UUIDs/s   |
| `FastUUIDParser#toString(UUID)` | 20,992,715.489 ± 113,547.630 UUIDs/s |

## License

`fast-uuid` is published under the [MIT license](https://github.com/jchambers/fast-uuid/blob/master/LICENSE).
