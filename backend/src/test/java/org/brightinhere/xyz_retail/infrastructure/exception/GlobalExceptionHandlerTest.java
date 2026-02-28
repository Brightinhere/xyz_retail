package org.brightinhere.xyz_retail.infrastructure.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestErrorController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("OrderNotFoundException -> 404 ORDER_NOT_FOUND")
    void orderNotFound() throws Exception {
        mockMvc.perform(get("/test/order").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Order not found"))
                .andExpect(jsonPath("$.path").value("/test/order"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("ProductNotFoundException -> 404 PRODUCT_NOT_FOUND")
    void productNotFound() throws Exception {
        mockMvc.perform(get("/test/product").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    @DisplayName("InventoryNotFoundException -> 404 INVENTORY_NOT_FOUND")
    void inventoryNotFound() throws Exception {
        mockMvc.perform(get("/test/inventory").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("INVENTORY_NOT_FOUND"));
    }

    @Test
    @DisplayName("InsufficientStockException -> 409 INSUFFICIENT_STOCK")
    void insufficientStock() throws Exception {
        mockMvc.perform(get("/test/stock").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_STOCK"));
    }

    @Test
    @DisplayName("InvalidOrderStateException -> 409 INVALID_ORDER_STATE")
    void invalidOrderState() throws Exception {
        mockMvc.perform(get("/test/state").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("INVALID_ORDER_STATE"))
                .andExpect(jsonPath("$.message").value("Invalid order state"));
    }

    @Test
    @DisplayName("IllegalArgumentException -> 400 BAD_REQUEST")
    void illegalArgument() throws Exception {
        mockMvc.perform(get("/test/illegal").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Bad argument"));
    }

    @Test
    @DisplayName("Error response contains required fields")
    void errorResponseContract() throws Exception {
        mockMvc.perform(get("/test/order").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.path").isString());
    }

    @TestConfiguration
    static class TestControllerConfig {

        @Bean
        TestErrorController testErrorController() {
            return new TestErrorController();
        }
    }

    @RestController
    @RequestMapping("/test")
    static class TestErrorController {

        @GetMapping("/order")
        public void order() {
            throw new OrderNotFoundException();
        }

        @GetMapping("/product")
        public void product() {
            throw new ProductNotFoundException();
        }

        @GetMapping("/inventory")
        public void inventory() {
            throw new InventoryNotFoundException( );
        }

        @GetMapping("/stock")
        public void stock() {
            throw new InsufficientStockException();
        }

        @GetMapping("/state")
        public void state() {
            throw new InvalidOrderStateException("Invalid order state");
        }

        @GetMapping("/illegal")
        public void illegal() {
            throw new IllegalArgumentException("Bad argument");
        }

    }
}