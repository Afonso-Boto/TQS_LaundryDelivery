package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.account.*;
import com.qourier.qourier_app.account.login.LoginRequest;
import com.qourier.qourier_app.account.login.LoginResult;
import com.qourier.qourier_app.account.register.AdminRegisterRequest;
import com.qourier.qourier_app.account.register.CustomerRegisterRequest;
import com.qourier.qourier_app.account.register.RiderRegisterRequest;
import com.qourier.qourier_app.data.AccountRole;
import com.qourier.qourier_app.data.AccountState;
import com.qourier.qourier_app.data.dto.AccountDTO;
import com.qourier.qourier_app.data.dto.CustomerDTO;
import com.qourier.qourier_app.data.dto.RiderDTO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.qourier.qourier_app.data.AccountRole.*;

@Log
@Controller
public class WebController {

    public static String COOKIE_ID = "id";
    private static final int TABLE_SIZE = 10;
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_INDEX = "redirect:/index";

    @Value("${spring.datasource.adminemail}")
    private String adminEmail;

    @Value("${spring.datasource.adminpass}")
    private String adminPass;

    private final AccountManager accountManager;

    @Autowired
    public WebController(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute LoginRequest user, HttpServletResponse response) {
        LoginResult result = accountManager.login(user);
        if (result.equals(LoginResult.WRONG_CREDENTIALS)
                || result.equals(LoginResult.NON_EXISTENT_ACCOUNT)) {
            log.warning("WRONG CREDS");
            return "login";
        }

        // Set cookie for customer
        setCookie(response, user.getEmail());

        return REDIRECT_INDEX;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // See if we are logged in or not
        if (hasCookie(request)) {
            Cookie jwtTokenCookie = new Cookie(COOKIE_ID, "null");

            jwtTokenCookie.setMaxAge(0);
            jwtTokenCookie.setSecure(false);
            jwtTokenCookie.setHttpOnly(true);

            // Set cookie onto user
            response.addCookie(jwtTokenCookie);
            return REDIRECT_LOGIN;
        } else {
            return "error";
        }
    }

    @PostMapping("/register_customer")
    public String registerCustomerPost(
            @ModelAttribute CustomerRegisterRequest request, HttpServletResponse response) {
        if (!accountManager.registerCustomer(request)) return "register_customer";

        // Set cookie for customer
        setCookie(response, request.getEmail());

        return REDIRECT_INDEX;
    }

    @PostMapping("/register_rider")
    public String registerRiderPost(
            @ModelAttribute RiderRegisterRequest request, HttpServletResponse response) {
        if (!accountManager.registerRider(request)) return "register_rider";

        // Set cookie for rider
        setCookie(response, request.getEmail());

        return REDIRECT_INDEX;
    }

    @GetMapping("/")
    public String rootToIndex() {
        return REDIRECT_INDEX;
    }

    @GetMapping("/index")
    public String index(Model model, HttpServletRequest request) {

        // Verify if cookie role is right or not
        if (!verifyCookie(request, ADMIN)
                && !verifyCookie(request, CUSTOMER)
                && !verifyCookie(request, RIDER)) return REDIRECT_LOGIN;

        model.addAttribute("role", getRoleFromCookie(request));
        return "index";
    }

    @GetMapping("/progress")
    public String progress(Model model, HttpServletRequest request) {
        AccountRole role = ADMIN;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role)) return REDIRECT_LOGIN;

        model.addAttribute("role", role);
        return "progress";
    }

    @GetMapping("/accounts")
    public String accounts(
            Model model,
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "rider", name = "type")
                    AccountRole accountRole,
            @RequestParam(required = false, defaultValue = "true", name = "active")
                    boolean active) {
        AccountRole role = ADMIN;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role)) return REDIRECT_LOGIN;

        fillModelWithRiderCustomerQueries(
                model,
                pageNumber,
                accountRole,
                (active) ? List.of(AccountState.ACTIVE) : List.of(AccountState.SUSPENDED));

        model.addAllAttributes(
                Map.of(
                        "role", role,
                        "filterActive", active));
        return "accounts";
    }

    @GetMapping("/applications")
    public String applications(
            Model model,
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "rider", name = "type")
                    AccountRole accountRole,
            @RequestParam(required = false, defaultValue = "true", name = "pending")
                    boolean pending) {
        AccountRole role = ADMIN;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role)) return REDIRECT_LOGIN;

        fillModelWithRiderCustomerQueries(
                model,
                pageNumber,
                accountRole,
                pending ? List.of(AccountState.PENDING) : List.of(AccountState.REFUSED));

        model.addAllAttributes(
                Map.of(
                        "role", role,
                        "filterPending", pending,
                        "hasher", (Function<String, String>) DigestUtils::sha256Hex));
        return "applications";
    }

    @GetMapping("/monitor")
    public String monitor(Model model, HttpServletRequest request) {
        AccountRole role = ADMIN;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role)) return REDIRECT_LOGIN;

        model.addAttribute("role", role);
        return "monitor";
    }

    @GetMapping("/login")
    public String loginGet(Model model, HttpServletRequest request) {

        // Verify if logged in already
        if (getRoleFromCookie(request) != null) return REDIRECT_INDEX;

        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/deliveries")
    public String deliveries(Model model, HttpServletRequest request) {
        AccountRole role = RIDER;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role)) return REDIRECT_LOGIN;

        // TODO pass right message to show
        AccountState state = accountManager.getAccount(getIdFromCookie(request)).getState();

        switch (state) {
            case PENDING:
                model.addAttribute("msg", "Account permission to access resource pending");
                break;
            case REFUSED:
                model.addAttribute("msg", "Account was refused to access the pretended resource");
                break;
            case SUSPENDED:
                model.addAttribute("msg", "Account suspended");
                break;
            case ACTIVE:
                break;
            default:
                model.addAttribute("msg", "An error has occurred");
        }
        model.addAttribute("role", role);
        model.addAttribute("permitted", state.equals(AccountState.ACTIVE));
        return "deliveries";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {

        // Verify if cookie role is right or not
        if (!verifyCookie(request, ADMIN)
                && !verifyCookie(request, CUSTOMER)
                && !verifyCookie(request, RIDER)) return REDIRECT_LOGIN;

        String email = getIdFromCookie(request);
        String view;

        if (getRoleFromCookie(request) == RIDER) {
            RiderDTO riderProfile = accountManager.getRiderAccount(email);
            model.addAttribute("rider", riderProfile);
            view = "profile_rider";
        } else {
            CustomerDTO customerProfile = accountManager.getCustomerAccount(email);
            model.addAttribute("customer", customerProfile);
            view = "profile_customer";
        }

        model.addAttribute("role", getRoleFromCookie(request));
        return view;
    }

    @GetMapping("/profile/{id}")
    public String profileById(Model model, HttpServletRequest request, @PathVariable String id) {

        // Verify if cookie role is right or not
        if (!verifyCookie(request, ADMIN)
                && !verifyCookie(request, CUSTOMER)
                && !verifyCookie(request, RIDER)) return REDIRECT_LOGIN;

        AccountRole role = getRoleFromCookie(request);

        if (role != ADMIN) return "redirect:/profile";

        AccountDTO account = accountManager.getAccount(id);
        String profileView = REDIRECT_INDEX;
        if (account.getRole().equals(RIDER)) {
            profileView = "profile_rider";
            model.addAttribute("rider", accountManager.getRiderAccount(id));
        } else if (account.getRole().equals(CUSTOMER)) {
            profileView = "profile_customer";
            model.addAttribute("customer", accountManager.getCustomerAccount(id));
        }

        model.addAttribute("role", getRoleFromCookie(request));
        return profileView;
    }

    @GetMapping("/delivery_management")
    public String deliveryManagement(Model model, HttpServletRequest request) {
        AccountRole role = CUSTOMER;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role)) return REDIRECT_LOGIN;

        // TODO pass right message to show
        AccountState state = accountManager.getAccount(getIdFromCookie(request)).getState();

        switch (state) {
            case PENDING:
                model.addAttribute("msg", "Account permission to access resource pending");
                break;
            case REFUSED:
                model.addAttribute("msg", "Account was refused to access the pretended resource");
                break;
            case SUSPENDED:
                model.addAttribute("msg", "Account suspended");
                break;
            case ACTIVE:
                break;
            default:
                model.addAttribute("msg", "An error has occurred");
        }

        model.addAttribute("role", role);
        model.addAttribute("permitted", state.equals(AccountState.ACTIVE));
        return "delivery_management";
    }

    @GetMapping("/register_rider")
    public String registerRiderGet(Model model, HttpServletRequest request) {
        model.addAttribute("riderRegisterRequest", new RiderRegisterRequest());
        return "register_rider";
    }

    @GetMapping("/register_customer")
    public String registerCustomerGet(Model model, HttpServletRequest request) {
        model.addAttribute("customerRegisterRequest", new CustomerRegisterRequest());
        return "register_customer";
    }

    @Bean
    SmartInitializingSingleton smartInitializingSingleton(ApplicationContext context) {
        return () -> {
            accountManager.registerAdmin(new AdminRegisterRequest(adminEmail, adminPass, "admin"));
            log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            log.info("ADMIN ACCOUNT REGISTERED");
            log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        };
    }

    // setCookie to user
    public void setCookie(HttpServletResponse response, String email) {
        // Create cookie
        Cookie jwtTokenCookie = new Cookie(COOKIE_ID, email);

        jwtTokenCookie.setMaxAge(86400);
        jwtTokenCookie.setSecure(false);
        jwtTokenCookie.setHttpOnly(true);

        // Set cookie onto person
        response.addCookie(jwtTokenCookie);
    }

    // Get cookies
    private boolean verifyCookie(HttpServletRequest request, AccountRole role) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals(COOKIE_ID)
                    && accountManager.getAccount(cookie.getValue()).getRole().equals(role))
                return true;
        return false;
    }

    // Verify cookie presence
    private boolean hasCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals(COOKIE_ID) && !cookie.getValue().isEmpty()) return true;
        return false;
    }

    // Get role
    private AccountRole getRoleFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals(COOKIE_ID)) {
                String email = cookie.getValue();
                return accountManager.getAccount(email).getRole();
            }
        return null;
    }

    // Get ID
    private String getIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals(COOKIE_ID)) {
                return cookie.getValue();
            }
        return null;
    }

    private void fillModelWithRiderCustomerQueries(
            Model model,
            int pageNumber,
            AccountRole accountRole,
            Collection<AccountState> queryStates) {
        PageRequest pageRequest = PageRequest.of(pageNumber, TABLE_SIZE);
        List<RiderDTO> riderList = new ArrayList<>();
        List<CustomerDTO> customerList = new ArrayList<>();
        int pageNumberMax;

        if (accountRole.equals(RIDER)) {
            AccountManager.RiderDTOQueryResult queryResult =
                    accountManager.queryRidersByState(pageRequest, queryStates);
            riderList = queryResult.getResult();
            pageNumberMax = queryResult.getTotalPages();
        } else {
            AccountManager.CustomerDTOQueryResult queryResult =
                    accountManager.queryCustomersByState(pageRequest, queryStates);
            customerList = queryResult.getResult();
            pageNumberMax = queryResult.getTotalPages();
        }

        model.addAllAttributes(
                Map.of(
                        "riderAppList", riderList,
                        "customerAppList", customerList,
                        "filterType", accountRole.name(),
                        "filterPage", pageNumber,
                        "filterPageMax", pageNumberMax));
    }
}
