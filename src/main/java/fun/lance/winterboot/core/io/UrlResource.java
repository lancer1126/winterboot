package fun.lance.winterboot.core.io;

import fun.lance.winterboot.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class UrlResource extends AbstractFileResolvingResource {

    private final URI uri;
    private final URL url;
    private volatile String cleanedUrl;

    public UrlResource(URL url) {
        Assert.notNull(url, "URL must not be null");
        this.uri = null;
        this.url = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        return null;
    }

    @Override
    public URI getURI() throws IOException {
        return null;
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public long contentLength() throws IOException {
        return 0;
    }

    @Override
    public long lastModified() throws IOException {
        return 0;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
