/*
 * Copyright 2014 Kaazing Corporation, All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.nuklei.amqp_1_0.codec.types;

import static java.lang.Long.highestOneBit;

import java.util.function.Consumer;

import org.kaazing.nuklei.Flyweight;

import uk.co.real_logic.agrona.BitUtil;
import uk.co.real_logic.agrona.MutableDirectBuffer;

/*
 * See AMQP 1.0 specification, section 1.6.10 "long"
 */
public final class LongType extends Type {

    private static final int OFFSET_KIND = 0;
    private static final int SIZEOF_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int OFFSET_VALUE = OFFSET_KIND + SIZEOF_KIND;
    private static final int SIZEOF_VALUE_MAX = BitUtil.SIZE_OF_LONG;
    
    static final int SIZEOF_LONG_MAX = SIZEOF_KIND + SIZEOF_VALUE_MAX;

    private static final short WIDTH_KIND_1 = 0x55;
    private static final short WIDTH_KIND_8 = 0x81;

    @Override
    public Kind kind() {
        return Kind.LONG;
    }

    @Override
    public LongType watch(Consumer<Flyweight> observer) {
        super.watch(observer);
        return this;
    }

    @Override
    public LongType wrap(MutableDirectBuffer buffer, int offset) {
        super.wrap(buffer, offset);
        return this;
    }

    public LongType set(long value) {
        switch ((int) highestOneBit(value)) {
        case 0:
        case 1:
        case 2:
        case 4:
        case 8:
        case 16:
        case 32:
        case 64:
        case 128:
            widthKind(WIDTH_KIND_1);
            uint8Put(buffer(), offset() + OFFSET_VALUE, (short) value);
            break;
        default:
            widthKind(WIDTH_KIND_8);
            int64Put(buffer(), offset() + OFFSET_VALUE, value);
            break;
        }

        notifyChanged();
        return this;
    }

    public long get() {
        switch (widthKind()) {
        case WIDTH_KIND_1:
            return uint8Get(buffer(), offset() + OFFSET_VALUE);
        case WIDTH_KIND_8:
            return int64Get(buffer(), offset() + OFFSET_VALUE);
        default:
            throw new IllegalStateException();
        }
    }

    public int limit() {
        switch (widthKind()) {
        case WIDTH_KIND_1:
            return offset() + OFFSET_VALUE + 1;
        case WIDTH_KIND_8:
            return offset() + OFFSET_VALUE + 8;
        default:
            throw new IllegalStateException();
        }
    }

    private void widthKind(short value) {
        uint8Put(buffer(), offset() + OFFSET_KIND, value);
    }

    private short widthKind() {
        return uint8Get(buffer(), offset() + OFFSET_KIND);
    }
}
