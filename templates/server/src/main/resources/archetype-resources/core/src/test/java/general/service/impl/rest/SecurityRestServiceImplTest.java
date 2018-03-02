package ${package}.general.service.impl.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.oasp.module.service.common.api.client.config.ServiceClientConfigBuilder;

import ${package}.general.service.api.rest.SecurityRestService;
import ${package}.general.service.base.test.RestServiceTest;

/**
 * This class tests the login functionality of {@link SecurityRestServiceImpl}.
 */
@RunWith(SpringRunner.class)
public class SecurityRestServiceImplTest extends RestServiceTest {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(SecurityRestServiceImplTest.class);

  /**
   * Test the login functionality as it will be used from a JavaScript client.
   */
  @Test
  public void testLogin() {

    String login = "waiter";
    String password = "waiter";

    ResponseEntity<String> postResponse = login(login, password);
    LOG.debug("Body: " + postResponse.getBody());
    assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(postResponse.getHeaders().containsKey(HttpHeaders.SET_COOKIE)).isTrue();
  }

  /**
   * This test depends on the successful login which is tested by {@link #testLogin()}. Then, a csrf token is queried
   * and checked.
   */
  @Test
  @DirtiesContext
  public void testGetCsrfToken() {

    String login = "waiter";
    String password = "waiter";
    SecurityRestService securityService = getServiceClientFactory().create(SecurityRestService.class,
        new ServiceClientConfigBuilder().authBasic().userLogin(login).userPassword(password).buildMap());
    CsrfToken csrfToken = securityService.getCsrfToken(null, null);
    assertThat(csrfToken.getHeaderName()).isEqualTo("X-CSRF-TOKEN");
    assertThat(csrfToken.getParameterName()).isEqualTo("_csrf");
    assertThat(csrfToken.getToken()).isNotNull();
    LOG.debug("Csrf Token: {}", csrfToken.getToken());
  }

  /**
   * Performs the login as required by a JavaScript client.
   *
   * @param userName the username of the user
   * @param tmpPassword the password of the user
   * @return @ {@link ResponseEntity} containing containing a cookie in its header.
   */
  private ResponseEntity<String> login(String userName, String tmpPassword) {

    String tmpUrl = "http://localhost:" + String.valueOf(this.port) + "/services/rest/login";

    HttpEntity<String> postRequest = new HttpEntity<>(
        "{\"j_username\": \"" + userName + "\", \"j_password\": \"" + tmpPassword + "\"}", new HttpHeaders());

    ResponseEntity<String> postResponse = new RestTemplate().exchange(tmpUrl, HttpMethod.POST, postRequest, String.class);
    return postResponse;
  }

}
