package com.nex.impl.log.writer;

import com.nex.slice.HeapSlice;
import com.nex.util.Closeables;
import com.nex.util.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static java.util.Objects.requireNonNull;

/**
 * 使用file channel的log writer
 *
 * @author xu.zx
 */
public class FileChannelLogWriter implements LogWriter {

  private final File file;

  private final FileChannel fileChannel;

  public FileChannelLogWriter(File file) throws FileNotFoundException {
    this(file, true);
  }

  public FileChannelLogWriter(File file, boolean append) throws FileNotFoundException {
    requireNonNull(file, "file is null");
    this.file = file;
    this.fileChannel = new FileOutputStream(file, append).getChannel();
  }

  @Override
  public void close() {
    Closeables.closeQuietly(fileChannel);
  }

  @Override
  public File getFile() {
    return file;
  }

  @Override
  public long getFileSize() throws IOException {
    return fileChannel.size();
  }

  @Override
  public void append(HeapSlice record)
      throws IOException {
    record.writeBytes(0, fileChannel, record.length());
  }

  @Override
  public void append(byte[] record) throws IOException {
    IOUtils.writeBytes(record, fileChannel);
  }

  @Override
  public void append(byte[] key, byte[] vlogSeq) throws IOException {
    throw new UnsupportedOperationException();
  }
}