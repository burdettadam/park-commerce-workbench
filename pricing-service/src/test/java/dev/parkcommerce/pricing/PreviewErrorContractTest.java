package dev.parkcommerce.pricing;

import dev.parkcommerce.core.Failure;
import dev.parkcommerce.support.ApiErrors;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

/** Exercises error presentation without requiring a database or running server. */
class PreviewErrorContractTest {
    private final ApiErrors errors=new ApiErrors();

    @Test void previewPreservesValidatedOverlapAndPartyMessages() {
        for(String message:new String[]{"Overlapping date ranges for ADULT","A party must have between 1 and 20 tickets"}) {
            var response=errors.invalid(wrapped(Failure.invalid(message)),request("/api/admin/price-books/preview"));
            assertEquals(HttpStatus.BAD_REQUEST.value(),response.getStatus());
            assertEquals("INVALID_INPUT",response.getProperties().get("code"));
            assertEquals(message,response.getDetail());
        }
    }

    @Test void previewGivesRepairHintWithoutLeakingAnUnknownConversionFailure() {
        var response=errors.invalid(wrapped(new IllegalStateException("private conversion detail")),request("/api/admin/price-books/preview"));
        assertEquals(HttpStatus.BAD_REQUEST.value(),response.getStatus());
        assertTrue(response.getDetail().contains("whole-number ticket counts"));
        assertFalse(response.getDetail().contains("private conversion detail"));
    }

    @Test void unrelatedRoutesRetainGenericMalformedRequestResponse() {
        var response=errors.invalid(wrapped(Failure.invalid("Overlapping date ranges for ADULT")),request("/api/quotes"));
        assertEquals("Malformed or incomplete request",response.getDetail());
    }

    private static HttpMessageNotReadableException wrapped(Throwable cause) {
        return new HttpMessageNotReadableException("Cannot read request",cause,new MockHttpInputMessage(new byte[0]));
    }

    private static MockHttpServletRequest request(String path) {
        return new MockHttpServletRequest("POST",path);
    }
}
