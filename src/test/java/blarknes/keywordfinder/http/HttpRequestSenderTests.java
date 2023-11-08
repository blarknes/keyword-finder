package blarknes.keywordfinder.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.Test;

import blarknes.keywordfinder.api.search.exception.HttpErrorResponseException;
import lombok.val;

@SuppressWarnings({"rawtypes", "unchecked"})
public class HttpRequestSenderTests {

    private final HttpClient mockHttpClient = mock(HttpClient.class);
    private final HttpResponse mockHttpResponse = mock(HttpResponse.class);

    private final HttpRequestSender httpRequestSender = new HttpRequestSender(mockHttpClient);

    private static final String URL = "https://example.com/";

    @Test
    void doGetRequest_shouldReturnResponseBodyAsListOfLinesWhenRequestIs2xxSuccessful() throws IOException, InterruptedException {
        // given
        val expectedResponseBody = List.of("<html>", "Successful Request", "</html>");
        val responseBodyMock = expectedResponseBody.stream();

        when(this.mockHttpResponse.statusCode()).thenReturn(200);
        when(this.mockHttpResponse.body()).thenReturn(responseBodyMock);
        when(this.mockHttpClient.send(any(), any())).thenReturn(this.mockHttpResponse);

        // when
        val response = this.httpRequestSender.doGetRequest(URL);

        // then
        assertEquals(expectedResponseBody, response);
    }

    @Test
    void doGetRequest_shouldThrowHttpErrorResponseExceptionWhenStatusIsNot2xxSuccessful() throws IOException, InterruptedException {
        // given
        when(this.mockHttpResponse.statusCode()).thenReturn(403);
        when(this.mockHttpClient.send(any(), any())).thenReturn(this.mockHttpResponse);

        // when
        val throwable = catchThrowable(() -> this.httpRequestSender.doGetRequest(URL));

        // then
        assertThat(throwable).isInstanceOf(HttpErrorResponseException.class);
    }

}
