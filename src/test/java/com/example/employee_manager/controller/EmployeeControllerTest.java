package com.example.employee_manager.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee employee;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setViewResolvers(viewResolver)
                .build();

        employee = new Employee(1L, "Kasun Perera", "kasun.perera@example.com", "0712345678", "Human Resources", 85000.0);
    }

    @Test
    void testListEmployees() throws Exception {
        when(employeeService.searchEmployees(null)).thenReturn(List.of(employee));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attribute("totalEmployees", 1));
    }

    @Test
    void testSaveEmployeeSuccess() throws Exception {
        when(employeeService.existsByEmail("kasun.perera@example.com")).thenReturn(false);
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/employees")
                        .param("fullName", "Kasun Perera")
                        .param("email", "kasun.perera@example.com")
                        .param("phone", "0712345678")
                        .param("department", "Human Resources")
                        .param("salary", "85000.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeService).saveEmployee(any(Employee.class));
    }

    @Test
    void testSaveEmployeeValidationErrors() throws Exception {
        // Empty fields, invalid email, 5-digit phone, 0 salary (as specified in Task 8)
        mockMvc.perform(post("/employees")
                        .param("fullName", "")
                        .param("email", "not-an-email")
                        .param("phone", "12345")
                        .param("department", "")
                        .param("salary", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("employee", "fullName", "email", "phone", "department", "salary"));
    }

    @Test
    void testSaveEmployeeDuplicateEmail() throws Exception {
        when(employeeService.existsByEmail("kasun.perera@example.com")).thenReturn(true);

        mockMvc.perform(post("/employees")
                        .param("fullName", "Kasun Perera")
                        .param("email", "kasun.perera@example.com")
                        .param("phone", "0712345678")
                        .param("department", "Human Resources")
                        .param("salary", "85000.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees"))
                .andExpect(model().attributeHasFieldErrorCode("employee", "email", "duplicate"));
    }

    @Test
    void testShowEditForm() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/employees/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-employee"))
                .andExpect(model().attributeExists("employee"));
    }

    @Test
    void testUpdateEmployeeSuccess() throws Exception {
        when(employeeService.existsByEmailAndIdNot("kasun.updated@example.com", 1L)).thenReturn(false);
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/employees/1/update")
                        .param("fullName", "Kasun Perera")
                        .param("email", "kasun.updated@example.com")
                        .param("phone", "0712345678")
                        .param("department", "Finance")
                        .param("salary", "90000.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(post("/employees/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(employeeService).deleteEmployee(1L);
    }
}
