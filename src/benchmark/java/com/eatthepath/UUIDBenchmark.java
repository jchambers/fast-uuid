/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Jon Chambers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.eatthepath;

import com.eatthepath.uuid.FastUUID;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.UUID;

@State(Scope.Thread)
public class UUIDBenchmark {

    private static final int PREGENERATED_UUID_COUNT = 100_000;

    private final UUID[] uuids = new UUID[PREGENERATED_UUID_COUNT];
    private final String[] uuidStrings = new String[PREGENERATED_UUID_COUNT];

    // Using a long here because on very fast machines the int will overflow
    private long i = 0;

    @Setup
    public void setup() {
        for (int i = 0; i < this.uuidStrings.length; i++) {
            final UUID uuid = UUID.randomUUID();

            this.uuids[i] = uuid;
            this.uuidStrings[i] = uuid.toString();
        }
    }

    @Benchmark
    public UUID benchmarkUUIDFromString() {
        resetCounterIfNecessary();
        return UUID.fromString(this.uuidStrings[(int) (this.i++ % PREGENERATED_UUID_COUNT)]);
    }

    @Benchmark
    public UUID benchmarkUUIDFromFastParser() {
        resetCounterIfNecessary();
        return FastUUID.parseUUID(this.uuidStrings[(int) (this.i++ % PREGENERATED_UUID_COUNT)]);
    }

    // Checking if type-check to String won't affect performance
    @Benchmark
    public UUID benchmarkUUIDFromCharSequenceFastParser() {
        resetCounterIfNecessary();
        return FastUUID.parseUUID((CharSequence) this.uuidStrings[(int) (this.i++ % PREGENERATED_UUID_COUNT)]);
    }

    @Benchmark
    public String benchmarkUUIDToString() {
        resetCounterIfNecessary();
        return this.uuids[(int) (this.i++ % PREGENERATED_UUID_COUNT)].toString();
    }

    @Benchmark
    public String benchmarkFastParserToString() {
        resetCounterIfNecessary();
        return FastUUID.toString(this.uuids[(int) (this.i++ % PREGENERATED_UUID_COUNT)]);
    }

    private void resetCounterIfNecessary() {
        if (this.i == Long.MAX_VALUE) {
            this.i = 0;
        }
    }
}
