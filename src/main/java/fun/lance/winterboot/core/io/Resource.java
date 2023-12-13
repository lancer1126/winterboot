package fun.lance.winterboot.core.io;

import fun.lance.winterboot.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public interface Resource extends InputStreamSource {

    boolean exists();

    default boolean isReadable() {
        return this.exists();
    }

    default boolean isOpen() {
        return false;
    }

    default boolean isFile() {
        return false;
    }

    URL getURL() throws IOException;

    URI getURI() throws IOException;

    File getFile() throws IOException;

    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(this.getInputStream());
    }

//    default byte[] getContentAsByteArray() throws IOException {
//        return FileCopyUtils.copyToByteArray(this.getInputStream());
//    }
//
//    default String getContentAsString(Charset charset) throws IOException {
//        return FileCopyUtils.copyToString(new InputStreamReader(this.getInputStream(), charset));
//    }

    long contentLength() throws IOException;

    long lastModified() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    @Nullable
    String getFilename();

    String getDescription();
}
