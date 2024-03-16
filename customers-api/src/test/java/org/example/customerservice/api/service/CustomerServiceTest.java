package org.example.customerservice.api.service;

import org.example.customerservice.api.data.entity.CustomerEntity;
import org.example.customerservice.api.data.repository.CustomerRepository;
import org.example.customerservice.api.web.error.ConflictException;
import org.example.customerservice.api.web.model.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @ParameterizedTest
    @CsvSource({"0, 0", "1,1", "3,3"})
    void getAllCustomers(int numberOfCustomersToMock, int expectedNumberOfCustomers) {
        Mockito.doReturn(getMockCustomers(numberOfCustomersToMock)).when(customerRepository).findAll();
        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(expectedNumberOfCustomers, customers.size());
    }

    @Test
    void findByEmailAddress() {
        CustomerEntity customerEntity = getMockCustomerEntity();
        when(customerRepository.findByEmailAddress(customerEntity.getEmailAddress())).thenReturn(customerEntity);
        Customer customer = customerService.findByEmailAddress(customerEntity.getEmailAddress());

        assertNotNull(customer);
        assertEquals("FirstName", customer.getFirstName());
    }

    @Test
    void addCustomer() {
        CustomerEntity customerEntity = getMockCustomerEntity();
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
        Customer addedCustomer = customerService.addCustomer(new Customer(
                customerEntity.getCustomerId().toString(),
                customerEntity.getFirstName(),
                customerEntity.getLastName(),
                customerEntity.getEmailAddress(),
                customerEntity.getPhoneNumber(),
                customerEntity.getAddress()
        ));

        assertNotNull(addedCustomer);
        assertEquals("LastName", addedCustomer.getLastName());
    }

    @Test
    void addCustomerExisting() {
        CustomerEntity customerEntity = getMockCustomerEntity();
        when(customerRepository.findByEmailAddress(customerEntity.getEmailAddress())).thenReturn(customerEntity);
        Customer customer = new Customer(
                customerEntity.getCustomerId().toString(),
                customerEntity.getFirstName(),
                customerEntity.getLastName(),
                customerEntity.getEmailAddress(),
                customerEntity.getPhoneNumber(),
                customerEntity.getAddress()
        );
        Assertions.assertThrows(ConflictException.class, () -> customerService.addCustomer(customer), "Should throw customer already exists exception");
    }

    @Test
    void getCustomer() {
        CustomerEntity customerEntity = getMockCustomerEntity();
        Optional<CustomerEntity> optionalEntity = Optional.of(customerEntity);
        Mockito.doReturn(optionalEntity).when(customerRepository)
                .findById(customerEntity.getCustomerId());
        String expectedCustomerId = customerEntity.getCustomerId().toString();
        Customer customer = customerService.getCustomer(expectedCustomerId);
        assertNotNull(customer);

        assertEquals("FirstName", customer.getFirstName());
        assertEquals("LastName", customer.getLastName());
    }

    @Test
    void getCustomerDoesNotExist() {
        CustomerEntity customerEntity = getMockCustomerEntity();
        Optional<CustomerEntity> optionalEntity = Optional.empty(); // no customer found - create empty optional
        Mockito.doReturn(optionalEntity).when(customerRepository)
                .findById(customerEntity.getCustomerId());

        String expectedCustomerId = customerEntity.getCustomerId().toString();
        assertThrows(RuntimeException.class, () -> customerService.getCustomer(expectedCustomerId));
    }

    @Test
    void updateCustomer() {
        CustomerEntity customerEntity = getMockCustomerEntity();
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
        Customer updatedCustomer = customerService.updateCustomer(new Customer(
                customerEntity.getCustomerId().toString(),
                customerEntity.getFirstName(),
                customerEntity.getLastName(),
                customerEntity.getEmailAddress(),
                customerEntity.getPhoneNumber(),
                customerEntity.getAddress()
        ));

        assertNotNull(updatedCustomer);
        assertEquals(customerEntity.getCustomerId().toString(), updatedCustomer.getCustomerId());
    }

    @Test
    void deleteCustomer() {
        UUID customerId = UUID.randomUUID();
        doNothing().when(customerRepository).deleteById(customerId);
        assertDoesNotThrow(() -> customerService.deleteCustomer(customerId.toString()));
    }

    private CustomerEntity getMockCustomerEntity() {
        return new CustomerEntity(
                UUID.randomUUID(),
                "FirstName",
                "LastName",
                "testemail@test.com",
                "123-456-7890",
                "123 Test St, Test City, TS, 12345"
        );
    }

    private Iterable<CustomerEntity> getMockCustomers(int count) {
        List<CustomerEntity> customers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            customers.add(new CustomerEntity(
                    UUID.randomUUID(),
                    "FirstName" + i,
                    "LastName" + i,
                    "test" + i + "email@test.com",
                    "123-456-7890",
                    "123 Test St, Test City, TS, 12345"
            ));
        }
        return customers;
    }
}