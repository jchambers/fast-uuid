[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.eatthepath/fast-uuid/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.eatthepath/fast-uuid)

# fast-uuid

`fast-uuid` is a Java library for quickly and efficiently parsing and writing UUIDs. It yields the most dramatic performance gains when compared to Java 8 and older; in benchmarks, it's a little more than fourteen times faster at parsing UUIDs and six times faster at writing UUIDs than the stock JDK implementation. Compared to Java 9 and newer, it's about six times faster when it comes to parsing UUIDs and offers no benefits for writing UUIDs.

This library is intended for applications that work with large quantities of UUIDs or that work with UUIDs in performance-sensitive code, and probably won't be helpful in applications that work with UUIDs infrequently.

## Usage

Using `fast-uuid` is simple. To parse UUIDs:

```java
UUID uuid = FastUUID.parseUUID(uuidStringOrCharacterSequence);
```

To convert UUIDs to strings:

```java
String uuidString = FastUUID.toString(uuid);
```

## Getting fast-uuid

For users of Maven (or Maven-compaitble build tools like Gradle), `fast-uuid` is [available via Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.eatthepath%22%20a%3A%22fast-uuid%22). You can add it to your project with the following Maven dependency declaration:

```xml
<dependency>
    <groupId>com.eatthepath</groupId>
    <artifactId>fast-uuid</artifactId>
    <version>0.2.0</version>
</dependency>
```

For users managing their own dependencies, you can add `fast-uuid` to your project by adding the `fast-uuid` jar file from the [latest release](https://github.com/jchambers/fast-uuid/releases) to your classpath. `fast-uuid` has no additional dependencies.

## How it works

### Parsing UUIDs

Let's take a look at the OpenJDK implementation of [`UUID#fromString(String)`](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html#fromString-java.lang.String-) for Java 8 and older:

```java
public static UUID fromString(String name) {
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
3. Eventually, this implementation needs to convert hexadecimal strings into numbers. It does so with [`Long#decode(String)`](https://docs.oracle.com/javase/8/docs/api/java/lang/Long.html#decode-java.lang.String-) rather than using [`Long#parseLong(String, int)`](https://docs.oracle.com/javase/8/docs/api/java/lang/Long.html#parseLong-java.lang.String-int-), which means somebody else needs to do the work of figuring out which radix to use when parsing the strings. This seems unnecessary since we know for sure that we're dealing with hexadecimal strings.

It turns out a lot of these issues are interrelated, and we can untangle them to get a significant performance boost. By recognizing that we're always dealing with hexadecimal strings, for example, we can immediately resolve the third issue. Once we've done that, we don't need to concatenate strings to prepend `"0x"` to the beginning of each of our substrings. That alone [speeds things up by about 50%](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8192784) and cuts the number of string allocations (and presumably garbage collection pressure) in half.

That leaves the first problem: can we find a way to parse a UUID without breaking it into substrings first? It turns out we can! Here we have to move away from the handy parsing tools that the JDK provides us, though, and write some of our own. We can even go further and, because we know for sure that we're dealing with hexadecimal strings of a fixed length, we can write a parser that drops a lot of error-checking and flexibility and picks up a lot of speed in return. That's exactly what `FastUUID` provides, and the result is that it can parse UUIDs a little more than four times faster than the default JDK implementation and, aside from the finished UUID, doesn't create anything on the heap that will need to get garbage-collected later.

Here are some benchmark results under Java 8:

| Benchmark                          | Throughput              | Margin of error        |
|------------------------------------|------------------------:|-----------------------:|
| `UUID#fromString(String)`          |  1,402,810 UUIDs/second |  ± 47,330 UUIDs/second |
| `FastUUID#parseUUID(String)`       | 19,736,169 UUIDs/second | ± 247,028 UUIDs/second |

The Java 9 implementation (some comments have been removed in the interest of brevity) improves the situation significantly:

```java
public static UUID fromString(String name) {
    int len = name.length();
    if (len > 36) {
        throw new IllegalArgumentException("UUID string too large");
    }

    int dash1 = name.indexOf('-', 0);
    int dash2 = name.indexOf('-', dash1 + 1);
    int dash3 = name.indexOf('-', dash2 + 1);
    int dash4 = name.indexOf('-', dash3 + 1);
    int dash5 = name.indexOf('-', dash4 + 1);

    if (dash4 < 0 || dash5 >= 0) {
        throw new IllegalArgumentException("Invalid UUID string: " + name);
    }

    long mostSigBits = Long.parseLong(name, 0, dash1, 16) & 0xffffffffL;
    mostSigBits <<= 16;
    mostSigBits |= Long.parseLong(name, dash1 + 1, dash2, 16) & 0xffffL;
    mostSigBits <<= 16;
    mostSigBits |= Long.parseLong(name, dash2 + 1, dash3, 16) & 0xffffL;
    long leastSigBits = Long.parseLong(name, dash3 + 1, dash4, 16) & 0xffffL;
    leastSigBits <<= 48;
    leastSigBits |= Long.parseLong(name, dash4 + 1, len, 16) & 0xffffffffffffL;

    return new UUID(mostSigBits, leastSigBits);
}
```

This implementation does away with the string concatenation (and resulting string allocation) entirely. The obvious gains are gone, but we might still be able to improve the situation by using a more application-specific parser than `Long#parseLong(String, int, int, int)`. As it turns out, using an application-specific parser makes a surprisingly significant difference. In benchmarks, a specialized parser is about six times faster than the Java 9 implementation of `UUID#fromString(String)`.

| Benchmark                          | Throughput              | Margin of error        |
|------------------------------------|------------------------:|-----------------------:|
| `UUID#fromString(String)`          |  2,613,730 UUIDs/second |  ± 25,278 UUIDs/second |
| `FastUUID#parseUUID(String)`       | 16,796,302 UUIDs/second | ± 191,695 UUIDs/second |

### UUIDs to strings

We've shown that we can significantly improve upon the stock `UUID#fromString(String)` implementation. Can we achieve similar gains in going from a `UUID` to a `String`? Let's take a look at the stock implementation of `UUID#toString()` from Java 8:

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

Some benchmark results under Java 8:

| Benchmark                       | Throughput              | Margin of error        |
|---------------------------------|------------------------:|-----------------------:|
| `UUID#toString()`               |  2,620,932 UUIDs/second |  ± 21,128 UUIDs/second |
| `FastUUID#toString(UUID)`       | 17,449,401 UUIDs/second | ± 221,382 UUIDs/second |

Java 9 uses a native method to convert UUIDs to strings, and our optimized implementation is actually a bit slower than the native approach. As a result, we just pass calls to `FastUUID#toString(UUID)` through to `UUID#toString()` under Java 9 and newer.

## Benchmarking

Because `fast-uuid` is a performance-oriented project, it includes [jmh](http://openjdk.java.net/projects/code-tools/jmh/) benchmarks to compare its performance against the stock JDK implementation. To run the the benchmarks:

```bash
cd fast-uuid
mvn clean install

cd benchmark
mvn clean install -U

java -jar target/benchmarks.jar
```

## License

`fast-uuid` is published under the [MIT license](https://github.com/jchambers/fast-uuid/blob/master/LICENSE).
