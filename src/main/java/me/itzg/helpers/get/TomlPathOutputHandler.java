package me.itzg.helpers.get;

import com.jayway.jsonpath.PathNotFoundException;

import io.ous.jtoml.JToml;
import io.ous.jtoml.Toml;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import me.itzg.helpers.http.LoggingResponseHandler;
import me.itzg.helpers.http.OutputResponseHandler;
import org.apache.hc.core5.http.HttpEntity;

class TomlPathOutputHandler extends LoggingResponseHandler<Path> implements OutputResponseHandler {

  private final PrintWriter writer;
  private final String tomlPath;
  private final String tomlValueWhenMissing;

  public TomlPathOutputHandler(PrintWriter writer, String tomlPath, String tomlValueWhenMissing) {
    this.writer = writer;
    this.tomlPath = tomlPath;
    this.tomlValueWhenMissing = tomlValueWhenMissing;
  }

  @Override
  public Path handleEntity(HttpEntity entity) throws IOException {
    final Toml toml = JToml.parse(entity.getContent());

    final String[] pathParts = tomlPath.split("\\.");
    final Object[] pathPartsMinusFirst = new String[pathParts.length - 1];
    System.arraycopy(pathParts, 1, pathPartsMinusFirst, 0, pathPartsMinusFirst.length);

    String result = toml.getString(pathParts[0], pathPartsMinusFirst);
    if (result == null) {
      if (tomlValueWhenMissing != null) {
        result = tomlValueWhenMissing;
      } else {
        throw new PathNotFoundException("Missing property in path " + tomlPath);
      }
    }

    writer.println(result);

    // no filename to return
    return null;
  }

}
