package com.example.employee_manager.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee(1L, "Pasindu Pramuditha", "pramudithapasindu45@gmail.com", "0771234567", "Engineering", 80000.0);
    }

    @Test
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        List<Employee> list = employeeService.getAllEmployees();
        assertEquals(1, list.size());
        assertEquals("Pasindu Pramuditha", list.get(0).getFullName());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testSearchEmployeesWithKeyword() {
        when(employeeRepository.findByFullNameContainingIgnoreCase("Pasindu")).thenReturn(List.of(employee));
        List<Employee> result = employeeService.searchEmployees("Pasindu");
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findByFullNameContainingIgnoreCase("Pasindu");
    }

    @Test
    void testSearchEmployeesWithEmptyKeyword() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        List<Employee> result = employeeService.searchEmployees("   ");
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Optional<Employee> found = employeeService.getEmployeeById(1L);
        assertTrue(found.isPresent());
        assertEquals("Pasindu Pramuditha", found.get().getFullName());
    }

    @Test
    void testSaveEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Employee saved = employeeService.saveEmployee(employee);
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testDeleteEmployee() {
        employeeService.deleteEmployee(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExistsByEmail() {
        when(employeeRepository.existsByEmail("pramudithapasindu45@gmail.com")).thenReturn(true);
        assertTrue(employeeService.existsByEmail("pramudithapasindu45@gmail.com"));
        assertFalse(employeeService.existsByEmail("other@gmail.com"));
    }

    @Test
    void testExistsByEmailAndIdNot() {
        when(employeeRepository.existsByEmailAndIdNot("pramudithapasindu45@gmail.com", 2L)).thenReturn(true);
        assertTrue(employeeService.existsByEmailAndIdNot("pramudithapasindu45@gmail.com", 2L));
        assertFalse(employeeService.existsByEmailAndIdNot("pramudithapasindu45@gmail.com", 1L));
    }
}
