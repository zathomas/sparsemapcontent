package uk.co.tfd.sm.proxy.http;


import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class CapturedRequest {

  private Map<String, String> headers = new HashMap<String, String>();
  private String requestBody;
  private String contentType;
  private String method;
  private byte[] byteBody;

  /**
   * @param request
   * @throws IOException
   */
  public CapturedRequest(HttpServletRequest request) throws IOException {
    for (Enumeration<?> names = request.getHeaderNames(); names.hasMoreElements();) {
      String name = (String) names.nextElement();
      headers.put(name, request.getHeader(name));
    }
    contentType = request.getContentType();
    if (request.getContentLength() > 0) {
      byteBody = new byte[request.getContentLength()];
      InputStream in = request.getInputStream();
      in.read(byteBody);
      requestBody = new String(byteBody);
    }
    method = request.getMethod();
  }

  /**
   * @param string
   * @return
   */
  public String getHeader(String name) {
    return headers.get(name);
  }

  /**
   * @return
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * @return the requestBody
   */
  public String getRequestBody() {
    return requestBody;
  }

  /**
   * @return the method
   */
  public String getMethod() {
    return method;
  }

  /**
   * @return
   */
  public byte[] getRequestBodyAsByteArray() {
    return byteBody;
  }
}
