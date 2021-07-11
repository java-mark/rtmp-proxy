package org.mark.rtmp.proxy.amf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class AMF0Declare {
    private AMF0Declare() {
    }

    public enum MarkerType {
        NUMBER(0x00),
        BOOLEAN(0x01),
        STRING(0x02),
        OBJECT(0x03),
        MOVIE_CLIP(0x04), // reserved.
        NULL(0x05),
        UNDEFINED(0x06),
        REFERENCE(0x07),
        ECMA_Array(0x08),
        OBJECT_END(0x09),
        STRICT_ARRAY(0x0A),
        DATE(0x0B),
        LONG_STRING(0x0C),
        UNSUPPORTED(0x0D),
        RECORD_SET(0x0E),  // reserved.
        XML_DOCUMENT(0x0F),  // unsupported
        TYPED_OBJECT(0x10);  // unsupported

        @Getter
        private final int id;

        MarkerType(int id) {
            this.id = id;
        }
    }

    public static abstract class Amf0Marker {
        @Getter
        protected final MarkerType markerType;

        public Amf0Marker(MarkerType markerType) {
            this.markerType = markerType;
        }

        public abstract ByteBuf encode();
    }

    public static class NumberMarker extends Amf0Marker {

        @Getter
        private final long number;

        public NumberMarker(long number) {
            super(MarkerType.NUMBER);
            this.number = number;
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer(1 + 8);
            res.writeByte(markerType.getId());
            res.writeLong(number);
            return res;
        }
    }

    public static class BooleanMarker extends Amf0Marker {

        public static final byte BOOLEAN_TRUE = 0x01;
        public static final byte BOOLEAN_FALSE = 0x00;

        @Getter
        private final boolean aBoolean;

        public BooleanMarker(boolean value) {
            super(MarkerType.BOOLEAN);
            this.aBoolean = value;
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            res.writeByte(this.aBoolean ? BOOLEAN_TRUE : BOOLEAN_FALSE);
            return res;
        }
    }

    public static class StringMarker extends Amf0Marker {

        @Getter
        private final String content;

        public StringMarker(String content) {
            super(MarkerType.STRING);
            this.content = content;
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            res.writeShort(bytes.length);
            res.writeBytes(bytes);
            return res;
        }

        public ByteBuf encodeWithoutType() {
            ByteBuf res = Unpooled.buffer();
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            res.writeShort(bytes.length);
            res.writeBytes(bytes);
            return res;
        }
    }

    public static class ObjectMarker extends Amf0Marker {

        @Getter
        LinkedHashMap<String, Amf0Marker> data;

        public ObjectMarker(LinkedHashMap<String, Amf0Marker> data) {
            super(MarkerType.OBJECT);
            this.data = data;
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            for (String s : data.keySet()) {
                res.writeBytes(new StringMarker(s).encodeWithoutType());
                res.writeBytes(data.get(s).encode());
            }
            res.writeBytes(ObjectEndMarker.OBJECT_END_MARKER);
            return res;
        }
    }

    public static class NULLMarker extends Amf0Marker {
        public NULLMarker() {
            super(MarkerType.NULL);
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            return res;
        }
    }

    public static class UndefinedMarker extends Amf0Marker {
        public UndefinedMarker() {
            super(MarkerType.UNDEFINED);
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            return res;
        }
    }

    public static class ReferenceMarker extends Amf0Marker {

        public ReferenceMarker() {
            super(MarkerType.REFERENCE);
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            return res;
        }
    }

    public static class ECMAArrayMarker extends Amf0Marker {

        @Getter
        LinkedHashMap<String, Amf0Marker> data;

        public ECMAArrayMarker(LinkedHashMap<String, Amf0Marker> data) {
            super(MarkerType.ECMA_Array);
            this.data = data;
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            res.writeInt(data.size());
            for (String s : data.keySet()) {
                res.writeBytes(new StringMarker(s).encodeWithoutType());
                res.writeBytes(data.get(s).encode());
            }
            res.writeBytes(ObjectEndMarker.OBJECT_END_MARKER);
            return res;
        }
    }

    public static class ObjectEndMarker extends Amf0Marker {
        public static final byte[] OBJECT_END_MARKER = new byte[]{0x00, 0x00, 0x09};

        public ObjectEndMarker(ByteBuf byteBuf) {
            super(MarkerType.OBJECT_END);
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            res.writeBytes(OBJECT_END_MARKER);
            return res;
        }
    }

    public static class StaticArrayMarker extends Amf0Marker {

        public final List<Amf0Marker> data;

        public StaticArrayMarker(List<Amf0Marker> data) {
            super(MarkerType.STRICT_ARRAY);
            this.data = data;
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            res.writeInt(data.size());
            for (Amf0Marker datum : data) {
                res.writeBytes(datum.encode());
            }

            return res;
        }
    }

    public static class DateMarker extends Amf0Marker {

        private static final byte[] DEFAULT_TIME_ZONE = new byte[]{0x0, 0x0};

        @Getter
        private final Date date;

        public DateMarker(Date date) {
            super(MarkerType.DATE);
            this.date = date;
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            res.writeLong(date.getTime());
            res.writeBytes(DEFAULT_TIME_ZONE);
            return res;
        }
    }

    public static class LongStringMarker extends Amf0Marker {
        @Getter
        private final String content;

        public LongStringMarker(String content) {
            super(MarkerType.LONG_STRING);
            this.content = content;
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            res.writeInt(bytes.length);
            res.writeBytes(bytes);
            return res;
        }
    }

    public static class UnsupportedMarker extends Amf0Marker {

        public UnsupportedMarker() {
            super(MarkerType.UNSUPPORTED);
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            return res;
        }
    }

    public static class XMLDocumentMarker extends Amf0Marker {

        public XMLDocumentMarker() {
            super(MarkerType.XML_DOCUMENT);
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            return res;
        }
    }

    public static class TypedObjectMarker extends Amf0Marker {

        public TypedObjectMarker() {
            super(MarkerType.TYPED_OBJECT);
        }

        @Override
        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer();
            res.writeByte(markerType.getId());
            return res;
        }
    }
}
