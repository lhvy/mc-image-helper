package me.itzg.helpers.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.itzg.helpers.get.ContentTypeValidator;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;

@Slf4j
public class SaveToFileHandler extends LoggingResponseHandler<Path> implements OutputResponseHandler {

  private final Path outputFile;
  private final boolean logProgressEach;
  private ContentTypeValidator contentTypeValidator;

  public SaveToFileHandler(Path outputFile, boolean logProgressEach) {
    this.outputFile = outputFile;
    this.logProgressEach = logProgressEach;
  }

  @Override
  public void setExpectedContentTypes(List<String> contentTypes) {
    if (contentTypes != null) {
      contentTypeValidator = new ContentTypeValidator(contentTypes);
    }
  }

  @Override
  public Path handleResponse(ClassicHttpResponse response) throws IOException {
    if (contentTypeValidator != null) {
      contentTypeValidator.validate(response);
    }
    return super.handleResponse(response);
  }

  @Override
  public Path handleEntity(HttpEntity entity) throws IOException {
    try (OutputStream out = Files.newOutputStream(outputFile)) {
      entity.writeTo(out);
    }
    if (logProgressEach) {
      log.info("Downloaded {}", outputFile);
    }
    return outputFile;
  }
}
