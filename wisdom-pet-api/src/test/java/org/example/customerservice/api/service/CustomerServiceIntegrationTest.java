package org.example.customerservice.api.service;

import org.example.customerservice.api.web.error.BadRequestException;
import org.example.customerservice.api.web.error.ConflictException;
import org.example.customerservice.api.web.error.NotFoundException;
import org.example.customerservice.api.web.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class CustomerServiceIntegrationTest {

    @Autowired
    CustomerService customerService;

    @Test
    void testGetAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(5, customers.size());
    }

    @Test
    void testFindByEmailAddress() {
        Customer customer = customerService.findByEmailAddress("penatibus.et@lectusa.com");
        assertEquals("Cally", customer.getFirstName());
        assertEquals("Reynolds", customer.getLastName());
        assertEquals("penatibus.et@lectusa.com", customer.getEmailAddress());
        assertEquals("(901) 166-8355", customer.getPhoneNumber());
        assertEquals("556 Lakewood Park, Bismarck, ND 58505", customer.getAddress());
    }

    @Test
    void testAddCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("Test");
        customer.setEmailAddress("test_customer_01@test.com");
        customer.setPhoneNumber("123-456-7890");
        customer.setAddress("123 Test St, Test City, TS 12345");
        Customer newCustomer = customerService.addCustomer(customer);

        assertEquals(customer.getFirstName(), newCustomer.getFirstName());
        assertEquals(customer.getLastName(), newCustomer.getLastName());
        assertEquals(customer.getEmailAddress(), newCustomer.getEmailAddress());
        assertEquals(customer.getPhoneNumber(), newCustomer.getPhoneNumber());
        assertEquals(customer.getAddress(), newCustomer.getAddress());

        customerService.deleteCustomer(newCustomer.getCustomerId());
    }

    @Test
    void testGetCustomerById() {
        Customer customer = customerService.getCustomer("054b145c-ddbc-4136-a2bd-7bf45ed1bef7");
        assertEquals("Cally", customer.getFirstName());
        assertEquals("Reynolds", customer.getLastName());
        assertEquals("penatibus.et@lectusa.com", customer.getEmailAddress());
        assertEquals("(901) 166-8355", customer.getPhoneNumber());
        assertEquals("556 Lakewood Park, Bismarck, ND 58505", customer.getAddress());
    }

    @Test
    void testUpdateCustomer() {
        Customer customer = customerService.getCustomer("9ac775c3-a1d3-4a0e-a2df-3e4ee8b3a49a");
        customer.setFirstName("Badeau");
        customer.setLastName("Roy");
        customer.setEmailAddress("updated_email@ultricesposuere.edu");

        Customer updatedCustomer = customerService.updateCustomer(customer);

        assertEquals("Badeau", updatedCustomer.getFirstName());
        assertEquals("Roy", updatedCustomer.getLastName());
        assertEquals("updated_email@ultricesposuere.edu", updatedCustomer.getEmailAddress());
        assertEquals("(982) 231-7357", updatedCustomer.getPhoneNumber());
        assertEquals("4829 Badeau Parkway, Chattanooga, TN 37405", updatedCustomer.getAddress());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        assertThrows(NotFoundException.class,
                () -> customerService.getCustomer("38124691-9643-4f10-90a0-d980bca0b00d"));
    }

    @Test
    void testAddCustomerConflict() {
        Customer customer = new Customer();
        customer.setFirstName("Cally");
        customer.setLastName("Reynolds");
        customer.setEmailAddress("sit@vitaealiquetnec.net");
        customer.setPhoneNumber("(982) 231-7357");
        customer.setAddress("4829 Badeau Parkway, Chattanooga, TN 37405");

        assertThrows(ConflictException.class, () -> customerService.addCustomer(customer));
    }

    @Test
    void testGetCustomerBadRequest() {
        assertThrows(BadRequestException.class,
                () -> customerService.getCustomer("38124691-9643-4f10"));
    }


// No need as it's implicitly covered in testAddCustomer
//    @Test
//    void testDeleteCustomer() {
//        customerService.deleteCustomer("9ac775c3-a1d3-4a0e-a2df-3e4ee8b3a49a");
//        List<Customer> customers = customerService.getAllCustomers();
//        assertEquals(4, customers.size());
//    }
}