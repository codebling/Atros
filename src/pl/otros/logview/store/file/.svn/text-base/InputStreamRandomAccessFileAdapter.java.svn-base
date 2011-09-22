package pl.otros.logview.store.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class InputStreamRandomAccessFileAdapter extends InputStream {

  RandomAccessFile accessFile;

  public InputStreamRandomAccessFileAdapter(RandomAccessFile accessFile) {
    super();
    this.accessFile = accessFile;
  }

  public void setPosition(long position) throws IOException {
    accessFile.seek(position);
  }

  @Override
  public int read() throws IOException {
    return accessFile.read();
  }

  @Override
  public int read(byte[] b) throws IOException {

    return accessFile.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return accessFile.read(b, off, len);
  }

  @Override
  public int available() throws IOException {
    long available = accessFile.length() - accessFile.getFilePointer();
    return (int) available;
  }

}
