package me.itzg.helpers.get;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import me.itzg.helpers.http.LatchingUrisInterceptor;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

@Slf4j
class DeriveFilenameHandler implements HttpClientResponseHandler<String> {

  final FilenameExtractor filenameExtractor;

  public DeriveFilenameHandler(LatchingUrisInterceptor interceptor) {
    filenameExtractor = new FilenameExtractor(interceptor);
  }

  @Override
  public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
    final String filename = filenameExtractor.extract(response);

    EntityUtils.consume(response.getEntity());

    return filename;
  }
}
