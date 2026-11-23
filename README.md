# Employee Management System (Spring Boot + MySQL + Lombok)

A full-stack Spring Boot CRUD web application developed for **COSC 32133 / BECS 32263 Full-Stack Software Development - Lab Sheet 05** (Department of Statistics & Computer Science, University of Kelaniya).

The application allows HR officers to manage employee records (Create, Read, Update, Delete) through a Thymeleaf web interface and via REST endpoints with Postman. All data is persisted in a MySQL database.

---

## 1. Technologies & Dependencies

- **Java**: 21 (OpenJDK)
- **Framework**: Spring Boot 4.2.0 (Spring MVC, Spring Data JPA)
- **Database**: MySQL Community Server / MariaDB (XAMPP)
- **View / Template Engine**: Thymeleaf + Bootstrap 5
- **Productivity / Boilerplate**: Project Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- **Validation**: Jakarta Bean Validation (`@NotBlank`, `@Email`, `@Pattern`, `@Positive`)
- **Testing**: JUnit 5, Mockito, Spring Test (MockMvc)
- **Build Tool**: Apache Maven Wrapper (`./mvnw`)

---

## 2. Layered Architecture

The application strictly adheres to the standard Spring layered architecture:

- **Entity Layer** (`Employee.java`): Maps to the MySQL `employees` table. Contains validation rules and uses Lombok annotations to generate getters/setters/constructors at compile time.
- **Repository Layer** (`EmployeeRepository.java`): Extends `JpaRepository<Employee, Long>`. Includes automated query derivation for email uniqueness and Task 11 name search: `findByFullNameContainingIgnoreCase(String keyword)`.
- **Service Layer** (`EmployeeService.java`): Encapsulates business logic, search filtering, and duplicate validation between controller and repository.
- **Controller Layer**:
  - `EmployeeController.java`: Handles Web requests, validation binding (`@Valid`, `BindingResult`), flash notifications, and Post-Redirect-Get navigation.
  - `EmployeeRestController.java`: Exposes RESTful JSON endpoints (`/api/employees`) for testing with Postman.
- **View Layer** (`employees.html`, `edit-employee.html`): Responsive Thymeleaf UI built with Bootstrap 5 featuring live employee counters, inline validation error alerts, and search filters.

---

## 3. URLs Handled by the Controllers

### Web Application (Thymeleaf UI)

| HTTP Method | URL | Description |
|---|---|---|
| `GET` | `/employees` | Show registration form and table of all employees (Read & Search) |
| `POST` | `/employees` | Save a new employee with validation (Create) |
| `GET` | `/employees/{id}/edit` | Show the edit form populated with employee data |
| `POST` | `/employees/{id}/update` | Save updated employee details (Update) |
| `POST` | `/employees/{id}/delete` | Remove the employee record from MySQL (Delete) |
| `GET` | `/` | Redirects directly to `/employees` |

### REST API (Postman / JSON Endpoints)

| HTTP Method | URL | Description |
|---|---|---|
| `GET` | `/api/employees` | Get all employees (or filter using `?keyword=Name`) |
| `GET` | `/api/employees/{id}` | Get specific employee details by ID |
| `POST` | `/api/employees` | Create employee from JSON request body |
| `PUT` | `/api/employees/{id}` | Update employee from JSON request body |
| `DELETE` | `/api/employees/{id}` | Delete employee record by ID |

---

## 4. Prerequisites & Setup

1. **JDK 17 or 21**: Verify with `java -version`.
2. **MySQL / XAMPP**: Start MySQL module on port 3306.
3. **Database Creation**:
   ```sql
   CREATE DATABASE employee_db;
   USE employee_db;
   SHOW DATABASES;
   ```
4. **Database Credentials**:
   Copy `secrets.properties.example` to `secrets.properties` in the project root if using custom passwords:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
   *(Note: `secrets.properties` is in `.gitignore` to prevent credentials from ever being pushed to GitHub).*

---

## 5. Running the Application

In the project root directory:

```bash
# Using Maven Wrapper (Windows PowerShell)
./mvnw.cmd spring-boot:run

# Using Maven Wrapper (Linux / macOS)
./mvnw spring-boot:run
```

Once started, open your web browser and navigate to:
**`http://localhost:8080/employees`**

---

## 6. Running Tests

Execute the comprehensive test suite (22 tests covering validation, duplicate checking, services, web controllers, and REST endpoints):

```bash
./mvnw.cmd test
```

---

## 7. Postman CRUD Testing

1. **Create Employee**:
   - `POST` `http://localhost:8080/api/employees`
   - Headers: `Content-Type: application/json`
   - Body:
     ```json
     {
       "fullName": "Saman Kumara",
       "email": "saman.kumara@example.com",
       "phone": "0723456789",
       "department": "Operations",
       "salary": 78000.0
     }
     ```
2. **Read All Employees**:
   - `GET` `http://localhost:8080/api/employees`
3. **Search Employees by Name (Task 11)**:
   - `GET` `http://localhost:8080/api/employees?keyword=Saman`
4. **Update Employee**:
   - `PUT` `http://localhost:8080/api/employees/1`
   - Body: Updated JSON with new department or salary.
5. **Delete Employee**:
   - `DELETE` `http://localhost:8080/api/employees/1`
