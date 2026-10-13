package dev.parkcommerce.admin;

import dev.parkcommerce.core.Failure;
import dev.parkcommerce.core.Pricing;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CommerceViewPreviewErrorTest {
    @Test void previewShowsOnlyKnownServerValidationThatCanPassTheLocalRecords() {
        // DatePicker has no minimum; Request accepts a past date, then the service rejects it.
        for(String detail:new String[]{"Visit date is in the past in the park time zone",
                "Price variant is not offered by this product revision"}) {
            assertEquals("PREVIEW INPUT — "+detail,CommerceView.previewError(response("INVALID_INPUT",detail)));
        }
    }

    @Test void combinedPartyLimitFailsBeforeTheHttpCallWithAnActionableMessage() {
        var failure=assertThrows(Failure.class,()->new Pricing.Request("lantern-evening",LocalDate.of(2030,10,31),20,1));
        assertEquals("A party must have between 1 and 20 tickets",failure.getMessage());
    }

    @Test void previewDoesNotEchoArbitraryOrMalformedServerText() {
        for(String body:new String[]{
            "{\"code\":\"INVALID_INPUT\",\"detail\":\"token=private-data\"}",
            "{\"code\":\"OTHER\",\"detail\":\"Visit date is in the past in the park time zone\"}",
            "<html>private-data</html>"}) {
            String shown=CommerceView.previewError(badRequest(body));
            assertTrue(shown.startsWith("Preview rejected (HTTP 400)"));
            assertFalse(shown.contains("private-data"));
        }
        var serverError=HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR,"Server Error",HttpHeaders.EMPTY,
            "private-data".getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);
        assertEquals("Preview rejected (HTTP 500). Check the date, party, rates, and employee credential.",
            CommerceView.previewError(serverError));
    }

    private static HttpClientErrorException response(String code,String detail) {
        return badRequest("{\"code\":\""+code+"\",\"detail\":\""+detail+"\"}");
    }

    private static HttpClientErrorException badRequest(String body) {
        return HttpClientErrorException.create(HttpStatus.BAD_REQUEST,"Bad Request",HttpHeaders.EMPTY,
            body.getBytes(StandardCharsets.UTF_8),StandardCharsets.UTF_8);
    }
}
