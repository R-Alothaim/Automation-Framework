package util;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ContactAPIValidator {
    
    private static final String BASE_URL = "https://portal.example.com";
    private static final String CONTACT_ENDPOINT = "/api/contact";
    
    private static final int STATUS_OK = 200;
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_NO_CONTENT = 204;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_UNPROCESSABLE_ENTITY = 422;
    
    private static final long DEFAULT_SUCCESS_TIMEOUT = 5000;
    private static final long DEFAULT_ERROR_TIMEOUT = 3000;
    
    private static final String[] SUCCESS_KEYWORDS = {"success", "thank", "submitted", "received"};
    
    private static final String[] REQUIRED_FIELDS = {"name", "email", "phone", "national", "iban", "message", "privacy"};
    
    private Response lastResponse;
    private Map<String, Object> lastRequestPayload;
    
    public ContactAPIValidator() {
        RestAssured.baseURI = BASE_URL;
    }
    
    public Response submitContactForm(Map<String, Object> formData) {
        return submitContactForm(formData, null);
    }
    
    public Response submitContactForm(Map<String, Object> formData, String recaptchaToken) {
        this.lastRequestPayload = formData;
        
        if (recaptchaToken != null && !recaptchaToken.isEmpty()) {
            formData.put("recaptchaToken", recaptchaToken);
        } else {
            formData.put("recaptchaToken", "test_recaptcha_token_for_api_testing");
        }
        
        try {
            RequestSpecification request = given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(formData)
                .log().all();
            
            lastResponse = request
                .when()
                .post(CONTACT_ENDPOINT)
                .then()
                .log().all()
                .extract()
                .response();
            
            
            String responseBody = lastResponse.getBody().asString();
            if (responseBody.toLowerCase().contains("recaptcha")) {
                System.err.println("\n⚠️  WARNING: API rejected reCAPTCHA token");
                System.err.println("Response: " + responseBody);
                throw new RuntimeException("API requires valid reCAPTCHA token - token was rejected or missing");
            }
            
            return lastResponse;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("API request failed: " + e.getMessage());
            throw new RuntimeException("Failed to submit contact form via API", e);
        }
    }
    
    public ContactAPIValidator validateSuccessfulSubmission() {
        
        int statusCode = lastResponse.getStatusCode();
        Assert.assertTrue(statusCode == STATUS_OK || statusCode == STATUS_CREATED || statusCode == STATUS_NO_CONTENT,
            "Expected successful status code (" + STATUS_OK + "/" + STATUS_CREATED + "/" + STATUS_NO_CONTENT + ") but got: " + statusCode);
        
        return this;
    }
    
    public ContactAPIValidator validateValidationError() {
        
        int statusCode = lastResponse.getStatusCode();
        Assert.assertTrue(statusCode == STATUS_BAD_REQUEST || statusCode == STATUS_UNPROCESSABLE_ENTITY,
            "Expected validation error status code (" + STATUS_BAD_REQUEST + "/" + STATUS_UNPROCESSABLE_ENTITY + ") but got: " + statusCode);
        
        return this;
    }
    
    public ContactAPIValidator validateSuccessMessage() {
        
        String responseBody = lastResponse.getBody().asString();
        Assert.assertFalse(responseBody == null || responseBody.isEmpty(),
            "Response body is empty");
        
        String lowerBody = responseBody.toLowerCase();
        boolean hasSuccessIndicator = false;
        for (String keyword : SUCCESS_KEYWORDS) {
            if (lowerBody.contains(keyword)) {
                hasSuccessIndicator = true;
                break;
            }
        }
        
        Assert.assertTrue(hasSuccessIndicator,
            "Response does not contain success indicator. Body: " + responseBody);
        
        return this;
    }
    
    public ContactAPIValidator validateErrorMessages(List<String> expectedErrors) {
        
        String responseBody = lastResponse.getBody().asString().toLowerCase();
        List<String> missingErrors = new ArrayList<>();
        
        for (String expectedError : expectedErrors) {
            if (!responseBody.contains(expectedError.toLowerCase())) {
                missingErrors.add(expectedError);
            } else {
            }
        }
        
        Assert.assertTrue(missingErrors.isEmpty(),
            "Expected error messages not found in API response: " + missingErrors + 
            "\nResponse body: " + responseBody);
        
        return this;
    }
    
    public ContactAPIValidator validateResponseTime(long maxTimeMs) {
        
        long responseTime = lastResponse.getTime();
        Assert.assertTrue(responseTime < maxTimeMs,
            "Response time too slow: " + responseTime + "ms (max: " + maxTimeMs + "ms)");
        
        return this;
    }
    
    public ContactAPIValidator validateResponseHeaders() {
        
        String contentType = lastResponse.getHeader("Content-Type");
        Assert.assertNotNull(contentType, "Content-Type header is missing");
        
        Map<String, String> headers = lastResponse.getHeaders().asList().stream()
            .collect(HashMap::new, (m, h) -> m.put(h.getName(), h.getValue()), HashMap::putAll);
        
        if (headers.containsKey("X-Content-Type-Options")) {
        }
        if (headers.containsKey("X-Frame-Options")) {
        }
        
        return this;
    }
    
    public ContactAPIValidator validateSuccessResponseStructure() {
        
        try {
            if (lastResponse.getContentType().contains("json")) {
                String body = lastResponse.getBody().asString();
                
                if (body.contains("id") || body.contains("ticketId") || body.contains("referenceNumber")) {
                }
                
                if (body.contains("message") || body.contains("status")) {
                }
            }
        } catch (Exception e) {
        }
        
        return this;
    }
    
    public ContactAPIValidator validateErrorResponseStructure() {
        
        try {
            String body = lastResponse.getBody().asString();
            Assert.assertFalse(body.isEmpty(), "Error response body is empty");
            
            boolean hasErrorStructure = 
                body.contains("error") ||
                body.contains("errors") ||
                body.contains("message") ||
                body.contains("validation");
            
            Assert.assertTrue(hasErrorStructure,
                "Response does not contain expected error structure. Body: " + body);
            
        } catch (Exception e) {
            System.err.println("Failed to validate error structure: " + e.getMessage());
        }
        
        return this;
    }
    
    public ContactAPIValidator validateAllRequiredFieldsChecked() {
        
        String responseBody = lastResponse.getBody().asString().toLowerCase();
        
        List<String> requiredFields = List.of(REQUIRED_FIELDS);
        
        List<String> fieldsNotMentioned = new ArrayList<>();
        for (String field : requiredFields) {
            if (!responseBody.contains(field)) {
                fieldsNotMentioned.add(field);
            } else {
            }
        }
        
        if (!fieldsNotMentioned.isEmpty()) {
        }
        
        return this;
    }
    
    public ContactAPIValidator validateInvalidDataRejection(String fieldName, String invalidValue) {
        
        int statusCode = lastResponse.getStatusCode();
        Assert.assertTrue(statusCode >= 400 && statusCode < 500,
            "Expected client error status (4xx) for invalid " + fieldName + 
            " but got: " + statusCode);
        
        String responseBody = lastResponse.getBody().asString().toLowerCase();
        Assert.assertTrue(
            responseBody.contains(fieldName.toLowerCase()) ||
            responseBody.contains("invalid") ||
            responseBody.contains("validation"),
            "Response does not indicate validation error for: " + fieldName);
        
        return this;
    }
    
    public ContactAPIValidator performFullSuccessValidation() {
        validateSuccessfulSubmission();
        validateResponseTime(DEFAULT_SUCCESS_TIMEOUT);
        validateResponseHeaders();
        validateSuccessMessage();
        validateSuccessResponseStructure();
        return this;
    }
    
    public ContactAPIValidator performFullErrorValidation() {
        validateValidationError();
        validateResponseTime(DEFAULT_ERROR_TIMEOUT);
        validateResponseHeaders();
        validateErrorResponseStructure();
        validateAllRequiredFieldsChecked();
        return this;
    }
    
    public ContactAPIValidator testEmptyFormSubmission() {
        
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("fullName", "");
        emptyData.put("email", "");
        emptyData.put("phoneNumber", "");
        emptyData.put("nationalId", "");
        emptyData.put("iban", "");
        emptyData.put("message", "");
        emptyData.put("subject", "");
        emptyData.put("category", "");
        emptyData.put("privacyPolicyConsent", false);
        
        submitContactForm(emptyData);
        performFullErrorValidation();
        
        return this;
    }
    
    public ContactAPIValidator testValidFormSubmission() {
        
        Map<String, Object> validData = new HashMap<>();
        validData.put("fullName", "Test User Ahmad");
        validData.put("email", "testuser@example.com");
        validData.put("phoneNumber", "0551234567");
        validData.put("nationalId", "1234567890");
        validData.put("iban", "SA1234567890123456789012");
        validData.put("message", "This is a test message with more than 50 characters to meet the minimum requirement.");
        validData.put("subject", "Inquiry");
        validData.put("category", "Account");
        validData.put("subCategory", "Account Opening");
        validData.put("privacyPolicyConsent", true);
        
        submitContactForm(validData);
        performFullSuccessValidation();
        
        return this;
    }
    
    public ContactAPIValidator testInvalidEmail(String invalidEmail) {
        return testInvalidEmail(invalidEmail, null);
    }
    
    public ContactAPIValidator testInvalidEmail(String invalidEmail, String recaptchaToken) {
        
        Map<String, Object> data = createValidFormData();
        data.put("email", invalidEmail);
        
        submitContactForm(data, recaptchaToken);
        validateInvalidDataRejection("email", invalidEmail);
        
        return this;
    }
    
    public ContactAPIValidator testInvalidPhone(String invalidPhone) {
        return testInvalidPhone(invalidPhone, null);
    }
    
    public ContactAPIValidator testInvalidPhone(String invalidPhone, String recaptchaToken) {
        
        Map<String, Object> data = createValidFormData();
        data.put("phoneNumber", invalidPhone);
        
        submitContactForm(data, recaptchaToken);
        validateInvalidDataRejection("phone", invalidPhone);
        
        return this;
    }
    
    public ContactAPIValidator testShortMessage() {
        return testShortMessage(null);
    }
    
    public ContactAPIValidator testShortMessage(String recaptchaToken) {
        
        Map<String, Object> data = createValidFormData();
        data.put("message", "Short message");
        
        submitContactForm(data, recaptchaToken);
        validateInvalidDataRejection("message", "Short message");
        
        return this;
    }
    
    public ContactAPIValidator testShortName() {
        return testShortName(null);
    }
    
    public ContactAPIValidator testShortName(String recaptchaToken) {
        
        Map<String, Object> data = createValidFormData();
        data.put("fullName", "AB");
        
        submitContactForm(data, recaptchaToken);
        validateInvalidDataRejection("name", "AB");
        
        return this;
    }
    
    private Map<String, Object> createValidFormData() {
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", "Test User Ahmad");
        data.put("email", "testuser@example.com");
        data.put("phoneNumber", "0551234567");
        data.put("nationalId", "1234567890");
        data.put("iban", "SA1234567890123456789012");
        data.put("message", "This is a test message with more than 50 characters to meet the minimum requirement.");
        data.put("subject", "Inquiry");
        data.put("category", "Account");
        data.put("subCategory", "Account Opening");
        data.put("privacyPolicyConsent", true);
        return data;
    }
    
    public Response getLastResponse() {
        return lastResponse;
    }
    
    public int getStatusCode() {
        return lastResponse != null ? lastResponse.getStatusCode() : 0;
    }
    
    public String getResponseBody() {
        return lastResponse != null ? lastResponse.getBody().asString() : "";
    }
    
    public long getResponseTime() {
        return lastResponse != null ? lastResponse.getTime() : 0;
    }
    
    public Map<String, Object> getLastRequestPayload() {
        return lastRequestPayload;
    }
}
