package sh.blake.niouring;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The type {@code AbstractIoUringSocket}.
 */
public abstract class AbstractIoUringChannel {
    private final long fd;
    private boolean closed = false;
    private final Map<Long, ByteBuffer> readBufferMap = new HashMap<>();
    private final Map<Long, ByteBuffer> writeBufferMap = new HashMap<>();
    private Consumer<ByteBuffer> readHandler;
    private Consumer<ByteBuffer> writeHandler;
    private Consumer<Exception> exceptionHandler;
    private boolean writePending = false;
    private boolean readPending = false;

    /**
     * Instantiates a new {@code AbstractIoUringSocket}.
     *
     * @param fd the fd
     */
    AbstractIoUringChannel(long fd) {
        this.fd = fd;
    }

    /**
     * Closes the socket.
     */
    public void close() {
        if (closed) {
            return;
        }
        if (!isReadPending() && !isWritePending()) {
            AbstractIoUringChannel.close(fd);
            closed = true;
        } else {
            // let's queue this for closure upon completion?
            throw new RuntimeException("Cannot close with pending I/O events"); // or can we?
        }
    }

    /**
     * Gets the file descriptor.
     *
     * @return the long
     */
    long fd() {
        return fd;
    }

    /**
     * Checks if a write operation is currently pending.
     *
     * @return whether write is pending
     */
    public boolean isWritePending() {
        return writePending;
    }

    /**
     * Sets write pending.
     *
     * @param writePending the write pending
     */
    void setWritePending(boolean writePending) {
        this.writePending = writePending;
    }

    /**
     * Checks if a read operation is currently pending.
     *
     * @return whether read is pending
     */
    public boolean isReadPending() {
        return readPending;
    }

    /**
     * Sets read pending.
     *
     * @param readPending the read pending
     */
    void setReadPending(boolean readPending) {
        this.readPending = readPending;
    }

    /**
     * Gets the read handler.
     *
     * @return the read handler
     */
    Consumer<ByteBuffer> readHandler() {
        return readHandler;
    }

    /**
     * Sets the handler to be called when a read operation completes.
     *
     * @param readHandler the read handler
     * @return this instance
     */
    public AbstractIoUringChannel onRead(Consumer<ByteBuffer> readHandler) {
        this.readHandler = readHandler;
        return this;
    }

    /**
     * Gets the write handler.
     *
     * @return the write handler
     */
    Consumer<ByteBuffer> writeHandler() {
        return writeHandler;
    }

    /**
     * Sets the handler to be called when a write operation completes.
     *
     * @param writeHandler the write handler
     * @return this instance
     */
    public AbstractIoUringChannel onWrite(Consumer<ByteBuffer> writeHandler) {
        this.writeHandler = writeHandler;
        return this;
    }

    /**
     * Gets the exception handler.
     *
     * @return the exception handler
     */
    Consumer<Exception> exceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Gets read buffer map.
     *
     * @return the read buffer map
     */
    Map<Long, ByteBuffer> readBufferMap() {
        return readBufferMap;
    }

    /**
     * Gets write buffer map.
     *
     * @return the write buffer map
     */
    Map<Long, ByteBuffer> writeBufferMap() {
        return writeBufferMap;
    }

    /**
     * Sets the handler to be called when an exception is caught while handling I/O for the socket.
     *
     * @param exceptionHandler the exception handler
     * @return this instance
     */
    public AbstractIoUringChannel onException(Consumer<Exception> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public boolean isClosed() {
        return closed;
    }

    public static native void close(long fd);
}
