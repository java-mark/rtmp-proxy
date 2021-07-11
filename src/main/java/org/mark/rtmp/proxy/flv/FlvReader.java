package org.mark.rtmp.proxy.flv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.BasicConfigurator;
import org.mark.rtmp.proxy.utils.Common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FlvReader {

    private final InputStream inputStream;

    private Header header;

    public FlvReader(File file) throws FileNotFoundException {
        inputStream = new FileInputStream(file);
    }

    public void decode() throws IOException {
        decodeHeader();

        List<Tag> tagList = new ArrayList<>();

        while (inputStream.available() > 0) {
            var tag = decodeTag();

            tagList.add(tag);

        }

        log.info("tag size={}", tagList.size());
    }

    public void decodeHeader() throws IOException {
        String signature = new String(inputStream.readNBytes(3));
        int version = inputStream.read();
        assert version > 0;

        byte flags = inputStream.readNBytes(1)[0];
        int dataOffset = Common.readInt(inputStream.readNBytes(4));

        header = new Header(signature, version, flags, dataOffset);
        // read first preview tag size, it's always equal to zero
        inputStream.readNBytes(4);
    }

    public Tag decodeTag() throws IOException {
        int type = inputStream.read();

        int tagDataSize = Common.readIntMedium(inputStream.readNBytes(3));

        int timestamp = Common.readInt(inputStream.readNBytes(4));

        int streamId = Common.readIntMedium(inputStream.readNBytes(3));

        byte[] tagData = inputStream.readNBytes(tagDataSize);

        int tagSize = Common.readIntMedium(inputStream.readNBytes(4));

        return new Tag(type, tagDataSize, timestamp, streamId, tagData, tagSize);
    }

    @Getter
    @AllArgsConstructor
    public static class Tag {
        // 1 bytes
        private final int type;

        // 3 bytes
        private final int tagDataSize;

        // 4 bytes(timestamp(3) + extended(1))
        private final int timestamp;

        // 3 bytes, always zero
        private final int streamId;

        // tag data, size equal to tag data size
        private final byte[] tagData;

        // 4 bytes
        private final int tagSize;
    }

    public static class ScriptTag {

    }

    @Getter
    @AllArgsConstructor
    public static class Header {
        // 3 byte
        private final String signature;

        // 1 byte
        private final int version;

        // 1 byte
        private final byte flags;

        // 4 byte
        private final int dataOffset;
    }

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        FlvReader reader = new FlvReader(
                new File("/Users/pengjian05/Downloads/dongfengpo.flv"));
        reader.decode();
    }
}
