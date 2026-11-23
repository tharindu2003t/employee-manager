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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;

@ExtendWith(MockitoExtension.class)
public class EmployeeRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeRestController employeeRestController;

    private Employee employee;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeRestController).build();
        employee = new Employee(1L, "Kasun Perera", "kasun.perera@example.com", "0712345678", "Human Resources", 85000.0);
    }

    @Test
    void testGetAllEmployees() throws Exception {
        when(employeeService.searchEmployees(null)).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Kasun Perera"))
                .andExpect(jsonPath("$[0].email").value("kasun.perera@example.com"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Kasun Perera"));
    }

    @Test
    void testCreateEmployeeSuccess() throws Exception {
        when(employeeService.existsByEmail("kasun.perera@example.com")).thenReturn(false);
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        String json = """
            {
                "fullName": "Kasun Perera",
                "email": "kasun.perera@example.com",
                "phone": "0712345678",
                "department": "Human Resources",
                "salary": 85000.0
            }
        """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Kasun Perera"));
    }

    @Test
    void testCreateEmployeeValidationFailure() throws Exception {
        String invalidJson = """
            {
                "fullName": "",
                "email": "invalid-email",
                "phone": "123",
                "department": "",
                "salary": -500.0
            }
        """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fullName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.department").exists())
                .andExpect(jsonPath("$.salary").exists());
    }

    @Test
    void testUpdateEmployee() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));
        when(employeeService.existsByEmailAndIdNot("kasun.perera@example.com", 1L)).thenReturn(false);
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        String json = """
            {
                "fullName": "Kasun Perera",
                "email": "kasun.perera@example.com",
                "phone": "0712345678",
                "department": "IT",
                "salary": 95000.0
            }
        """;

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteEmployee() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee removed successfully"));

        verify(employeeService).deleteEmployee(1L);
    }
}
