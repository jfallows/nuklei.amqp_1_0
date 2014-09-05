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
package org.kaazing.nuklei.amqp_1_0.codec.transport;

import static java.nio.ByteOrder.BIG_ENDIAN;

import org.kaazing.nuklei.FlyweightBE;
import org.kaazing.nuklei.amqp_1_0.codec.messaging.Performative;
import org.kaazing.nuklei.amqp_1_0.codec.types.ULongType;
import org.kaazing.nuklei.concurrent.AtomicBuffer;

public final class Frame extends FlyweightBE {

    private static final int OFFSET_LENGTH = 0;
    private static final int SIZEOF_LENGTH = 4;

    private static final int OFFSET_DATA_OFFSET = OFFSET_LENGTH + SIZEOF_LENGTH;
    private static final int SIZEOF_DATA_OFFSET = 1;

    private static final int OFFSET_TYPE = OFFSET_DATA_OFFSET + SIZEOF_DATA_OFFSET;
    private static final int SIZEOF_TYPE = 1;

    private static final int OFFSET_CHANNEL = OFFSET_TYPE + SIZEOF_TYPE;
    private static final int SIZEOF_CHANNEL = 2;

    private static final int OFFSET_PERFORMATIVE = OFFSET_CHANNEL + SIZEOF_CHANNEL;

    private final ULongType.Descriptor performative;
    
    public Frame() {
        performative = new ULongType.Descriptor();
    }

    @Override
    public Frame wrap(AtomicBuffer buffer, int offset) {
        super.wrap(buffer, offset);
        performative.wrap(buffer, offset + OFFSET_PERFORMATIVE);
        return this;
    }

    public Frame setLength(long value) {
        uint32Put(buffer(), offset() + OFFSET_LENGTH, value, BIG_ENDIAN);
        return this;
    }
    
    public long getLength() {
        return uint32Get(buffer(), offset() + OFFSET_LENGTH, BIG_ENDIAN);
    }

    public Frame setDataOffset(int value) {
        uint8Put(buffer(), offset() + OFFSET_DATA_OFFSET, (short) value);
        return this;
    }

    public int getDataOffset() {
        return uint8Get(buffer(), offset() + OFFSET_DATA_OFFSET);
    }

    public Frame setType(int value) {
        uint8Put(buffer(), offset() + OFFSET_TYPE, (short) value);
        return this;
    }

    public int getType() {
        return uint8Get(buffer(), offset() + OFFSET_TYPE);
    }

    public Frame setChannel(int value) {
        uint16Put(buffer(), offset() + OFFSET_CHANNEL, value);
        return this;
    }

    public int getChannel() {
        return uint16Get(buffer(), offset() + OFFSET_CHANNEL);
    }

    public Frame setPerformative(Performative value) {
        performative.set(Performative.WRITE, value);
        return this;
    }

    public Performative getPerformative() {
        return performative.get(Performative.READ);
    }
    
    public int limit() {
        return performative.limit();
    }
}