package org.example.customerservice.api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.customerservice.api.web.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
class CustomerControllerTest {

    // Creates a sudo dispatcher servlet
    // It runs on separate container/mock servlet container/web server
    @Autowired
    MockMvc mockMvc;

    @Test
    void getCustomers() throws Exception {
        this.mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("054b145c-ddbc-4136-a2bd-7bf45ed1bef7")))
                .andExpect(content().string(containsString("9ac775c3-a1d3-4a0e-a2df-3e4ee8b3a49a")))
                .andExpect(content().string(containsString("c04ca077-8c40-4437-b77a-41f510f3f185")))
                .andExpect(content().string(containsString("3b6c3ecc-fad7-49db-a14a-f396ed866e50")))
                .andExpect(content().string(containsString("38124691-9643-4f10-90a0-d980bca0b27d")));
    }

    @Test
    void addCustomer() throws Exception {
        Customer customer = new Customer(
                "",
                "John",
                "John",
                "john.doe@example.com",
                "(901) 166-8355",
                "123 Main Street, Bismarck, ND 58501");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(customer);

        this.mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("John"))
                .andExpect(jsonPath("$.emailAddress").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("(901) 166-8355"))
                .andExpect(jsonPath("$.address").value("123 Main Street, Bismarck, ND 58501"));
    }


    @Test
    void addCustomerConflict() throws Exception {
        Customer customer = new Customer(
                "3b6c3ecc-fad7-49db-a14a-f396ed866e50",
                "Brooke",
                "Perkins",
                "sit@vitaealiquetnec.net",
                "(340) 732-9367",
                "87 Brentwood Park, Dallas, TX 75358");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(customer);
        this.mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void getCustomer() throws Exception {
        this.mockMvc.perform(get("/customers/054b145c-ddbc-4136-a2bd-7bf45ed1bef7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("054b145c-ddbc-4136-a2bd-7bf45ed1bef7")))
                .andExpect(content().string(containsString("Cally")))
                .andExpect(content().string(containsString("Reynolds")))
                .andExpect(content().string(containsString("penatibus.et@lectusa.com")))
                .andExpect(content().string(containsString("(901) 166-8355")))
                .andExpect(content().string(containsString("556 Lakewood Park, Bismarck, ND 58505")));
    }

    @Test
    void getCustomerNotFound() throws Exception {
        this.mockMvc.perform(get("/customers/38124691-9643-4f10-90a0-d980bca0b00d"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomerBadRequestEmptyId() throws Exception {
        this.mockMvc.perform(get("/customers/ "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCustomerBadRequestInvalidUUID() throws Exception {
        this.mockMvc.perform(get("/customers/38124691--00-9643-4f10-90a0-d980bca0b00d"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCustomer() throws Exception {
        this.mockMvc.perform(put("/customers/054b145c-ddbc-4136-a2bd-7bf45ed1bef7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerId": "054b145c-ddbc-4136-a2bd-7bf45ed1bef7",
                                    "firstName": "Cally",
                                    "lastName": "Reynolds",
                                    "emailAddress": "penatibus.et@lectusa.com",
                                    "phoneNumber": "(901) 166-8356",
                                    "address": "560 Lakewood Park, Bismarck, ND 58505"
                                }"""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Cally"))
                .andExpect(jsonPath("$.lastName").value("Reynolds"))
                .andExpect(jsonPath("$.emailAddress").value("penatibus.et@lectusa.com"))
                .andExpect(jsonPath("$.phoneNumber").value("(901) 166-8356"))
                .andExpect(jsonPath("$.address").value("560 Lakewood Park, Bismarck, ND 58505"));
    }

    @Test
    void updateCustomerBadRequest() throws Exception {
        this.mockMvc.perform(put("/customers/054b145c-ddbc-4136-a2bd-7bf45ed1be00")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "customerId": "054b145c-ddbc-4136-a2bd-7bf45ed1bef7",
                                    "firstName": "Cally",
                                    "lastName": "Reynolds",
                                    "emailAddress": "penatibus.et@lectusa.com",
                                    "phoneNumber": "(901) 166-8356",
                                    "address": "560 Lakewood Park, Bismarck, ND 58505"
                                }"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCustomer() throws Exception {
        this.mockMvc.perform(delete("/customers/054b145c-ddbc-4136-a2bd-7bf45ed1bef7"))
                .andExpect(status().isResetContent());
    }
}