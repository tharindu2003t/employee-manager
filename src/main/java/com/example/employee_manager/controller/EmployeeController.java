package com.example.employee_manager.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;

import jakarta.validation.Valid;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Redirect root to /employees
    @GetMapping("/")
    public String index() {
        return "redirect:/employees";
    }

    // GET /employees: Show the form and table of all employees (Read)
    // Supports optional keyword search (Task 11 Challenge)
    @GetMapping("/employees")
    public String listEmployees(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Employee> employees = employeeService.searchEmployees(keyword);
        model.addAttribute("employees", employees);
        model.addAttribute("totalEmployees", employees.size());
        model.addAttribute("keyword", keyword != null ? keyword : "");
        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new Employee());
        }
        return "employees";
    }

    // POST /employees: Save a new employee (Create)
    @PostMapping("/employees")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult result,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // Check duplicate email
        if (employee.getEmail() != null && employeeService.existsByEmail(employee.getEmail())) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }

        if (result.hasErrors()) {
            List<Employee> employees = employeeService.searchEmployees(keyword);
            model.addAttribute("employees", employees);
            model.addAttribute("totalEmployees", employees.size());
            model.addAttribute("keyword", keyword != null ? keyword : "");
            return "employees";
        }

        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee saved successfully!");
        return "redirect:/employees";
    }

    // GET /employees/{id}/edit: Show the edit form filled with employee's data
    @GetMapping("/employees/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
        if (employeeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found with id: " + id);
            return "redirect:/employees";
        }
        model.addAttribute("employee", employeeOpt.get());
        return "edit-employee";
    }

    // POST /employees/{id}/update: Save the changes (Update)
    @PostMapping("/employees/{id}/update")
    public String updateEmployee(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        // Check duplicate email (if another employee has the same email)
        if (employee.getEmail() != null && employeeService.existsByEmailAndIdNot(employee.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }

        if (result.hasErrors()) {
            employee.setId(id);
            return "edit-employee";
        }

        employee.setId(id);
        employeeService.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
        return "redirect:/employees";
    }

    // POST /employees/{id}/delete: Remove the employee (Delete)
    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee removed successfully!");
        return "redirect:/employees";
    }

    // Optional convenience: GET /employees/{id}/delete
    @GetMapping("/employees/{id}/delete")
    public String deleteEmployeeGet(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        return deleteEmployee(id, redirectAttributes);
    }
}
