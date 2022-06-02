package com.qourier.qourier_app;

import com.qourier.qourier_app.account.login.LoginRequest;
import com.qourier.qourier_app.account.register.CustomerRegisterRequest;
import com.qourier.qourier_app.account.register.RiderRegisterRequest;
import com.qourier.qourier_app.controller.WebController;
import com.qourier.qourier_app.data.Customer;
import com.qourier.qourier_app.data.Rider;
import com.qourier.qourier_app.data.dto.CustomerDTO;
import com.qourier.qourier_app.data.dto.RiderDTO;
import com.qourier.qourier_app.repository.AccountRepository;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static com.qourier.qourier_app.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations= "/application-test.properties")
@AutoConfigureMockMvc
@Testcontainers
class WebOperationsIntegrationTest {

    @LocalServerPort
    int randomServerPort;

    @Value("${spring.datasource.adminemail}")
    private String adminEmail;
    @Value("${spring.datasource.adminpass}")
    private String adminPass;

    @Container
    public static MySQLContainer container = new MySQLContainer("mysql:8.0.29")
            .withUsername("demo")
            .withPassword("demopass")
            .withDatabaseName("test_db");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @Autowired private MockMvc mvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private RiderRepository riderRepository;
    @Autowired private CustomerRepository customerRepository;



    @Test
    void givenAdminAccountInitialized_whenLogIntoAdmin_thenLogin() throws Exception {
        LoginRequest request = new LoginRequest(adminEmail, adminPass);

        mvc.perform(post("/login")
                        .param("email", request.getEmail())
                        .param("password", request.getPassword()))
                .andExpect(status().isFound())
                .andExpect(cookie().exists(WebController.COOKIE_ID));
    }

    @Test
    void givenAccountDoesNotExist_whenLogIntoAccount_thenNoLogin() throws Exception {
        LoginRequest request = new LoginRequest("goa@gsioa.com", "hsdshbmosd");

        mvc.perform(post("/login")
                        .param("email", request.getEmail())
                        .param("password", request.getPassword()))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(WebController.COOKIE_ID));
    }

    @Test
    void givenAccountExists_whenLogIntoAccount_thenLogin() throws Exception {
        String riderEmail = "le@rider.xyz";
        String riderPass = "********";
        LoginRequest request = new LoginRequest(riderEmail, riderPass);

        riderRepository.save(createSampleRider(riderEmail, riderPass));

        mvc.perform(post("/login")
                        .param("email", request.getEmail())
                        .param("password", request.getPassword()))
                .andExpect(status().isFound())
                .andExpect(cookie().exists(WebController.COOKIE_ID));
    }

    @Test
    void givenAccountExists_whenLogIntoAccountWithWrongCredentials_thenNoLogin() throws Exception {
        String riderEmail = "le@riderz.xyz";
        String riderPass = "asterisks";
        LoginRequest request = new LoginRequest(riderEmail, riderPass);

        riderRepository.save(createSampleRider(riderEmail, riderPass + "oops"));

        mvc.perform(post("/login")
                        .param("email", request.getEmail())
                        .param("password", request.getPassword()))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(WebController.COOKIE_ID));
    }

    @Test
    void givenAccountExists_whenRegisterSameEmail_thenNoRegister() throws Exception {
        String riderAccountEmail = "gioas@ngioasd.pt";
        String riderAccountPass = "beepbeepboop";
        String customerAccountEmail = "goasg@jg90aw.pls";
        String customerAccountPass = "g9a0wga9";

        Rider rider = createSampleRider(riderAccountEmail, riderAccountPass);
        riderRepository.save(rider);
        Customer customer = createSampleCustomer(customerAccountEmail, customerAccountPass);
        customerRepository.save(customer);

        RiderRegisterRequest riderRegister = new RiderRegisterRequest(
                rider.getEmail(),
                "2yt19gv2189",
                "ASNUINIOA",
                "3589023523589"
        );
        CustomerRegisterRequest customerRegister = new CustomerRegisterRequest(
                customer.getEmail(),
                "571092hf2910",
                "AISgnosnagio",
                "Electronics"
        );

        mvc.perform(post("/register_rider")
                        .param("email", riderRegister.getEmail())
                        .param("password", riderRegister.getPassword())
                        .param("name", riderRegister.getName())
                        .param("citizenId", riderRegister.getCitizenId()))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(WebController.COOKIE_ID));
        mvc.perform(post("/register_customer")
                        .param("email", customerRegister.getEmail())
                        .param("password", customerRegister.getPassword())
                        .param("name", customerRegister.getName())
                        .param("servType", customerRegister.getServType()))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(WebController.COOKIE_ID));

        assertThat(riderRepository.findById(riderAccountEmail))
                .isPresent()
                .map(RiderDTO::fromEntity)
                .get()
                .isEqualTo(RiderDTO.fromEntity(rider));
        assertThat(customerRepository.findById(customerAccountEmail))
                .isPresent()
                .map(CustomerDTO::fromEntity)
                .get()
                .isEqualTo(CustomerDTO.fromEntity(customer));
    }

    @Test
    void whenRegisterNewAccount_thenRegisterSuccessfully() throws Exception {
        String riderAccountEmail = "gioas@ngioasd.pt";
        String riderAccountPass = "beepbeepboop";
        String customerAccountEmail = "goasg@jg90aw.pls";
        String customerAccountPass = "g9a0wga9";

        Rider rider = createSampleRider(riderAccountEmail, riderAccountPass);
        Customer customer = createSampleCustomer(customerAccountEmail, customerAccountPass);

        RiderRegisterRequest riderRegister = new RiderRegisterRequest(
                rider.getAccount().getEmail(),
                rider.getAccount().getPassword(),
                rider.getAccount().getName(),
                rider.getCitizenId()
        );
        CustomerRegisterRequest customerRegister = new CustomerRegisterRequest(
                customer.getAccount().getEmail(),
                customer.getAccount().getPassword(),
                customer.getAccount().getName(),
                customer.getServType()
        );

        mvc.perform(post("/register_rider")
                        .param("email", riderRegister.getEmail())
                        .param("password", riderRegister.getPassword())
                        .param("name", riderRegister.getName())
                        .param("citizenId", riderRegister.getCitizenId()))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(WebController.COOKIE_ID));
        mvc.perform(post("/register_customer")
                        .param("email", customerRegister.getEmail())
                        .param("password", customerRegister.getPassword())
                        .param("name", customerRegister.getName())
                        .param("servType", customerRegister.getServType()))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(WebController.COOKIE_ID));

        Optional<Rider> riderSaved = riderRepository.findById(riderAccountEmail);
        Optional<Customer> customerSaved = customerRepository.findById(customerAccountEmail);


        assertThat(riderSaved).isPresent();
        assertThat(customerSaved).isPresent();

        // Synchronize registration times
        rider.getAccount().setRegistrationTime(riderSaved.get().getAccount().getRegistrationTime());
        customer.getAccount().setRegistrationTime(customerSaved.get().getAccount().getRegistrationTime());

        assertThat(riderSaved)
                .map(RiderDTO::fromEntity)
                .get()
                .isEqualTo(RiderDTO.fromEntity(rider));
        assertThat(customerSaved)
                .map(CustomerDTO::fromEntity)
                .get()
                .isEqualTo(CustomerDTO.fromEntity(customer));
    }

}
