package fun.lance.core.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamSource {
    InputStream getINputStream() throws IOException;
}
