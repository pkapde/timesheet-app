# Timesheet Management System

A comprehensive Spring Boot application for employee timesheet management with role-based access control, reporting, and approval workflows.

## Features Implemented

### Core Features
- **Employee Management**: Role-based access (Employee, Manager, Admin)
- **Timesheet Entry**: Daily/weekly timesheet logging with project allocation
- **Approval Workflow**: Submit → Manager Approval → Status tracking
- **Project Management**: Create and manage projects with active/inactive status
- **Reporting & Analytics**: Employee-wise, project-wise, and manager reports
- **Excel Export**: Generate and download timesheet reports

### User Roles
1. **Employee**: Create timesheets, log hours, submit for approval
2. **Manager**: Approve/reject timesheets, view team reports, manage projects
3. **Admin**: Full system access, user management, system configuration

## Technology Stack
- **Backend**: Spring Boot 2.7.15
- **Database**: H2 (development), PostgreSQL ready
- **Security**: Spring Security with role-based authorization
- **API**: RESTful APIs with comprehensive endpoints
- **Export**: Apache POI for Excel generation
- **Validation**: Bean validation with custom constraints

## Project Structure
```
src/main/java/com/timesheet/
├── TimesheetApplication.java          # Main Spring Boot application
├── config/
│   └── SecurityConfig.java           # Security configuration
├── controller/                       # REST API endpoints
│   ├── AuthController.java
│   ├── EmployeeController.java
│   ├── ProjectController.java
│   ├── ReportController.java
│   └── TimesheetController.java
├── dto/                             # Data Transfer Objects
│   ├── EmployeeDTO.java
│   ├── EntryCreateDTO.java
│   ├── ProjectDTO.java
│   ├── ReportDTO.java
│   ├── TimesheetCreateDTO.java
│   ├── TimesheetDTO.java
│   └── TimesheetEntryDTO.java
├── entity/                          # JPA Entities
│   ├── Employee.java
│   ├── Project.java
│   ├── Timesheet.java
│   └── TimesheetEntry.java
├── enums/                           # Enumerations
│   ├── TimesheetStatus.java
│   └── UserRole.java
├── exception/                       # Exception handling
│   └── GlobalExceptionHandler.java
├── repository/                      # Data access layer
│   ├── EmployeeRepository.java
│   ├── ProjectRepository.java
│   ├── TimesheetEntryRepository.java
│   └── TimesheetRepository.java
└── service/                         # Business logic layer
    ├── DataInitializationService.java
    ├── EmployeeService.java
    ├── ProjectService.java
    ├── ReportService.java
    └── TimesheetService.java
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Timesheet Management
- `POST /api/timesheets` - Create new timesheet
- `POST /api/timesheets/{id}/entries` - Add timesheet entry
- `PUT /api/timesheets/{id}/submit` - Submit timesheet for approval
- `PUT /api/timesheets/{id}/approve` - Approve timesheet (Manager)
- `PUT /api/timesheets/{id}/reject` - Reject timesheet (Manager)
- `GET /api/timesheets/my-timesheets` - Get employee's timesheets
- `GET /api/timesheets/pending-approvals` - Get pending approvals (Manager)

### Employee Management
- `GET /api/employees` - List all employees (Admin/Manager)
- `POST /api/employees` - Create employee (Admin)
- `PUT /api/employees/{id}` - Update employee (Admin)
- `GET /api/employees/subordinates/{managerId}` - Get team members

### Project Management
- `GET /api/projects/active` - Get active projects
- `POST /api/projects` - Create project (Manager/Admin)
- `PUT /api/projects/{id}` - Update project (Manager/Admin)
- `PUT /api/projects/{id}/deactivate` - Deactivate project

### Reports & Analytics
- `GET /api/reports/employee/{id}` - Employee timesheet report
- `GET /api/reports/project/{id}` - Project hours report
- `GET /api/reports/employee/{id}/export` - Export employee report to Excel
- `GET /api/reports/project/{id}/export` - Export project report to Excel

## Database Schema

### Core Entities
1. **Employee**: User management with hierarchical structure
2. **Project**: Project definitions with manager assignment
3. **Timesheet**: Weekly timesheet containers
4. **TimesheetEntry**: Individual time entries with project allocation

### Key Relationships
- Employee → Manager (self-referencing)
- Timesheet → Employee (many-to-one)
- TimesheetEntry → Timesheet (many-to-one)
- TimesheetEntry → Project (many-to-one)
- Project → ProjectManager (many-to-one)

## Sample Data
The application comes with pre-loaded sample data:

### Users
- **Admin**: admin@timesheet.com / admin123
- **Manager**: manager@timesheet.com / manager123
- **Employee 1**: employee1@timesheet.com / emp123
- **Employee 2**: employee2@timesheet.com / emp123

### Projects
- E-commerce Platform (PROJ001)
- Mobile App Development (PROJ002)
- Database Migration (PROJ003)

## Running the Application

1. **Prerequisites**: Java 11+, Maven
2. **Build**: `mvn clean install`
3. **Run**: `mvn spring-boot:run`
4. **Access**: http://localhost:8080/timesheet
5. **H2 Console**: http://localhost:8080/timesheet/h2-console

## Configuration
- **Database**: H2 in-memory (development), PostgreSQL ready for production
- **Security**: Role-based access control with method-level security
- **Logging**: Debug level for development
- **CORS**: Configured for cross-origin requests

## Key Features Alignment with PRD

✅ **Employee timesheet logging** - Complete with validation
✅ **Manager approval workflow** - Submit/Approve/Reject flow
✅ **Role-based access control** - Employee/Manager/Admin roles
✅ **Reporting dashboard** - Employee, project, and manager reports
✅ **Excel export functionality** - Apache POI integration
✅ **Project management** - Create, update, activate/deactivate
✅ **Data security** - Spring Security with encrypted passwords
✅ **RESTful APIs** - Comprehensive API coverage
✅ **Scalable architecture** - Modular Spring Boot design

This implementation provides a solid foundation that can be easily extended with additional features like mobile apps, advanced analytics, and integrations with external systems.
