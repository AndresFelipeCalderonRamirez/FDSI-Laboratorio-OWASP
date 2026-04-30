package edu.eci.cvds.ecireserves.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ApiResponseTest {
    @Test
    void testApiResponseSuccess() {
        ApiResponse<String> response = new ApiResponse<>(true, "Operation successful", "TestData");
        
        assertTrue(response.isSuccess());
        assertEquals("Operation successful", response.getMessage());
        assertEquals("TestData", response.getData());
    }

    @Test
    void testApiResponseFailure() {
        ApiResponse<Integer> response = new ApiResponse<>(false, "Operation failed", 404);
        
        assertFalse(response.isSuccess());
        assertEquals("Operation failed", response.getMessage());
        assertEquals(404, response.getData());
    }

    @Test
    void testApiResponseWithNullData() {
        ApiResponse<Object> response = new ApiResponse<>(true, "No data", null);
        
        assertTrue(response.isSuccess());
        assertEquals("No data", response.getMessage());
        assertNull(response.getData());
    }
}
