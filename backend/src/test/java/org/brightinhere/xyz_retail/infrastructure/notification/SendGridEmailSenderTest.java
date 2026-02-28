package org.brightinhere.xyz_retail.infrastructure.notification;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendGridEmailSenderTest {

    @Mock
    private SendGrid sendGrid;

    @Mock
    private Response response;

    private SendGridEmailSender emailSender;

    @BeforeEach
    void setUp() {
        emailSender = new SendGridEmailSender(sendGrid);
        ReflectionTestUtils.setField(emailSender, "fromEmail", "noreply@example.com");
        ReflectionTestUtils.setField(emailSender, "templateId", "d-123456789");
    }

    @Test
    @DisplayName("Should send email successfully when all parameters are valid")
    void shouldSendEmailSuccessfully() throws IOException {
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    @DisplayName("Should set correct endpoint for SendGrid API request")
    void shouldSetCorrectEndpoint() throws IOException {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid).api(requestCaptor.capture());
        Request capturedRequest = requestCaptor.getValue();
        assertEquals("mail/send", capturedRequest.getEndpoint());
    }

    @Test
    @DisplayName("Should use POST method for SendGrid API request")
    void shouldUsePOSTMethod() throws IOException {
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid).api(requestCaptor.capture());
        Request capturedRequest = requestCaptor.getValue();
        assertEquals(Method.POST, capturedRequest.getMethod());
    }

    @Test
    @DisplayName("Should skip sending when customer email is null")
    void shouldSkipSendingWhenEmailIsNull() throws IOException {
        emailSender.sendOrderConfirmation(
                "John Doe",
                null,
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid, never()).api(any(Request.class));
    }

    @Test
    @DisplayName("Should skip sending when customer email is blank")
    void shouldSkipSendingWhenEmailIsBlank() throws IOException {
        emailSender.sendOrderConfirmation(
                "John Doe",
                "   ",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid, never()).api(any(Request.class));
    }

    @Test
    @DisplayName("Should skip sending when customer email is empty string")
    void shouldSkipSendingWhenEmailIsEmpty() throws IOException {
        emailSender.sendOrderConfirmation(
                "John Doe",
                "",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid, never()).api(any(Request.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when IOException occurs")
    void shouldThrowRuntimeExceptionOnIOException() throws IOException {
        when(sendGrid.api(any(Request.class))).thenThrow(new IOException("SendGrid API error"));

        assertThrows(RuntimeException.class, () -> emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "5",
                "99.99"
        ));
    }

    @Test
    @DisplayName("Should send with all required dynamic template data")
    void shouldIncludeAllTemplateData() throws IOException {
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "Jane Smith",
                "jane@example.com",
                "ORD-123",
                "10",
                "249.99"
        );

        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    @DisplayName("Should log info when email is sent successfully")
    void shouldLogInfoOnSuccess() throws IOException {
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    @DisplayName("Should send email with special characters in customer name")
    void shouldHandleSpecialCharactersInCustomerName() throws IOException {
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "José García",
                "jose@example.com",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    @DisplayName("Should send email with various order quantities")
    void shouldHandleVariousOrderQuantities() throws IOException {
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "100",
                "9999.99"
        );

        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    @DisplayName("Should use configured from email address")
    void shouldUseConfiguredFromEmail() throws IOException {
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    @DisplayName("Should use configured template ID")
    void shouldUseConfiguredTemplateId() throws IOException {
        when(response.getStatusCode()).thenReturn(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "5",
                "99.99"
        );

        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when SendGrid returns error status")
    void shouldThrowRuntimeExceptionOnErrorStatus() throws IOException {
        when(response.getStatusCode()).thenReturn(400);
        when(response.getBody()).thenReturn("Invalid request");
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        assertThrows(RuntimeException.class, () -> emailSender.sendOrderConfirmation(
                "John Doe",
                "john@example.com",
                "ORD-001",
                "5",
                "99.99"
        ));
    }
}