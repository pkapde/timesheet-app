// Global Variables
let currentUser = null;
let currentTimesheet = null;
let projects = [];
let employees = [];

// API Base URL
const API_BASE = '/timesheet/api';

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
});

function initializeApp() {
    // Always start by showing login section and hiding everything else
    showLoginSection();

    // Clear any existing user session first
    currentUser = null;

    // Check if user has a valid saved session
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        try {
            const parsedUser = JSON.parse(savedUser);
            // Validate the saved user data
            if (parsedUser && parsedUser.id && parsedUser.email && parsedUser.role) {
                // Verify the session is still valid by making a test API call
                validateUserSession(parsedUser);
            } else {
                // Invalid user data, clear it
                localStorage.removeItem('currentUser');
                showLoginSection();
            }
        } catch (error) {
            console.error('Invalid saved user data:', error);
            localStorage.removeItem('currentUser');
            showLoginSection();
        }
    }
}

// Validate user session with backend
async function validateUserSession(user) {
    try {
        // Test the session by making an API call
        const response = await fetch(`${API_BASE}/employees/${user.id}`);

        if (response.ok) {
            // Session is valid, set current user and show main app
            currentUser = user;
            showMainApp();
        } else {
            // Session is invalid, clear it and show login
            console.warn('User session expired or invalid');
            localStorage.removeItem('currentUser');
            currentUser = null;
            showLoginSection();
        }
    } catch (error) {
        console.error('Error validating user session:', error);
        // On error, clear session and show login
        localStorage.removeItem('currentUser');
        currentUser = null;
        showLoginSection();
    }
}

function setupEventListeners() {
    // Login form
    document.getElementById('loginForm').addEventListener('submit', handleLogin);

    // Create timesheet form
    document.getElementById('createTimesheetForm').addEventListener('submit', handleCreateTimesheet);

    // Add entry form
    document.getElementById('addEntryForm').addEventListener('submit', handleAddEntry);

    // Create employee form
    document.getElementById('createEmployeeForm').addEventListener('submit', handleCreateEmployee);

    // Edit employee form
    document.getElementById('editEmployeeForm').addEventListener('submit', handleEditEmployee);

    // Create project form
    document.getElementById('createProjectForm').addEventListener('submit', handleCreateProject);

    // Week date auto-calculation
    document.getElementById('weekStartDate').addEventListener('change', calculateWeekEndDate);
}

// Authentication Functions
async function handleLogin(e) {
    e.preventDefault();
    showLoading(true);

    const formData = new FormData(e.target);
    const email = formData.get('email');
    const password = formData.get('password');

    try {
        // Create URLSearchParams for form data
        const params = new URLSearchParams();
        params.append('email', email);
        params.append('password', password);

        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        });

        if (response.ok) {
            const user = await response.json();
            currentUser = user;
            localStorage.setItem('currentUser', JSON.stringify(user));
            showToast('Login successful!', 'success');
            showMainApp();
        } else {
            const error = await response.json();
            showToast(error.message || 'Invalid credentials. Please try again.', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showToast('Login failed. Please check your connection.', 'error');
    }

    showLoading(false);
}

function logout() {
    currentUser = null;
    localStorage.removeItem('currentUser');
    showLoginSection();
    showToast('Logged out successfully', 'success');
}

function fillLogin(email, password) {
    document.getElementById('email').value = email;
    document.getElementById('password').value = password;
}

// UI Navigation Functions
function showMainApp() {
    document.getElementById('loginSection').classList.remove('active');
    document.querySelector('.navbar').style.display = 'block';
    updateUserInterface();
    showSection('dashboard');
    loadDashboardData();
}

function showLoginSection() {
    document.getElementById('loginSection').classList.add('active');
    document.querySelector('.navbar').style.display = 'none';
    // Hide all other sections
    document.querySelectorAll('.section:not(#loginSection)').forEach(section => {
        section.classList.remove('active');
    });
}

function showSection(sectionName) {
    // Authentication guard - prevent navigation without login
    if (!currentUser) {
        console.warn('Attempted to navigate to', sectionName, 'without authentication');
        showToast('Please login to access this section', 'warning');
        showLoginSection();
        return;
    }

    // Role-based access control
    if (!hasAccessToSection(sectionName, currentUser.role)) {
        showToast('You do not have permission to access this section', 'warning');
        showSection('dashboard'); // Redirect to dashboard
        return;
    }

    // Hide all sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });

    // Show selected section
    document.getElementById(sectionName).classList.add('active');

    // Load section-specific data
    switch(sectionName) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'timesheets':
            loadTimesheets();
            break;
        case 'projects':
            loadProjects();
            break;
        case 'approvals':
            loadPendingApprovals();
            break;
        case 'reports':
            loadReportData();
            break;
        case 'employees':
            loadEmployees();
            break;
        default:
            console.warn('Unknown section:', sectionName);
            showSection('dashboard');
    }
}

// Role-based access control function
function hasAccessToSection(sectionName, userRole) {
    const sectionPermissions = {
        'dashboard': ['EMPLOYEE', 'MANAGER', 'ADMIN'],
        'timesheets': ['EMPLOYEE', 'MANAGER', 'ADMIN'],
        'projects': ['EMPLOYEE', 'MANAGER', 'ADMIN'],
        'approvals': ['MANAGER', 'ADMIN'],
        'reports': ['MANAGER', 'ADMIN'],
        'employees': ['ADMIN', 'MANAGER']
    };

    const allowedRoles = sectionPermissions[sectionName] || [];
    return allowedRoles.includes(userRole);
}

function updateUserInterface() {
    if (!currentUser) return;

    // Update welcome message
    document.getElementById('userWelcome').textContent = `Welcome, ${currentUser.fullName}`;

    // Show/hide menu items based on role
    const role = currentUser.role;

    // Hide all role-specific elements first
    document.querySelectorAll('.employee-only, .manager-only, .admin-only').forEach(el => {
        el.style.display = 'none';
    });

    // Show elements based on role
    if (role === 'EMPLOYEE') {
        document.querySelectorAll('.employee-only').forEach(el => {
            el.style.display = el.tagName === 'LI' ? 'list-item' : 'block';
        });
    } else if (role === 'MANAGER') {
        document.querySelectorAll('.employee-only, .manager-only').forEach(el => {
            el.style.display = el.tagName === 'LI' ? 'list-item' : 'block';
        });
    } else if (role === 'ADMIN') {
        document.querySelectorAll('.employee-only, .manager-only, .admin-only').forEach(el => {
            el.style.display = el.tagName === 'LI' ? 'list-item' : 'block';
        });
    }
}

// Dashboard Functions
async function loadDashboardData() {
    showLoading(true);

    try {
        // Load dashboard statistics
        await Promise.all([
            loadWeeklyHours(),
            loadPendingCounts(),
            loadActiveProjectsCount(),
            loadRecentActivity()
        ]);
    } catch (error) {
        console.error('Dashboard loading error:', error);
        showToast('Failed to load dashboard data', 'error');
    }

    showLoading(false);
}

async function loadWeeklyHours() {
    // This would calculate current week hours for the employee
    document.getElementById('weekHours').textContent = '40.0';
}

async function loadPendingCounts() {
    try {
        if (currentUser.role === 'EMPLOYEE') {
            // Load employee's timesheets
            const response = await fetch(`${API_BASE}/timesheets/employee/${currentUser.id}`);
            if (response.ok) {
                const timesheets = await response.json();
                const pending = timesheets.filter(t => t.status === 'DRAFT').length;
                document.getElementById('pendingSubmissions').textContent = pending;
            }
        } else if (currentUser.role === 'MANAGER' || currentUser.role === 'ADMIN') {
            // Load manager's pending approvals
            const response = await fetch(`${API_BASE}/timesheets/pending-approvals?managerId=${currentUser.id}`);
            if (response.ok) {
                const approvals = await response.json();
                document.getElementById('pendingApprovals').textContent = approvals.length;
            }
        }
    } catch (error) {
        console.error('Error loading pending counts:', error);
    }
}

async function loadActiveProjectsCount() {
    try {
        const response = await fetch(`${API_BASE}/projects/active`);
        if (response.ok) {
            const activeProjects = await response.json();
            document.getElementById('activeProjects').textContent = activeProjects.length;
            projects = activeProjects; // Cache for later use
        }
    } catch (error) {
        console.error('Error loading projects:', error);
    }
}

async function loadRecentActivity() {
    const activityContainer = document.getElementById('recentActivity');

    if (!currentUser) {
        activityContainer.innerHTML = '<p style="text-align: center; color: #666;">Please login to view recent activities</p>';
        return;
    }

    try {
        let apiUrl = '';

        // Determine which API endpoint to use based on user role
        if (currentUser.role === 'MANAGER' || currentUser.role === 'ADMIN') {
            apiUrl = `${API_BASE}/activities/manager/${currentUser.id}?limit=5`;
        } else {
            apiUrl = `${API_BASE}/activities/recent/${currentUser.id}?limit=5`;
        }

        console.log('Fetching activities from:', apiUrl);

        const response = await fetch(apiUrl);

        if (response.ok) {
            const activities = await response.json();
            console.log('Loaded activities:', activities);

            if (activities && activities.length > 0) {
                activityContainer.innerHTML = activities.map(activity => `
                    <div class="activity-item">
                        <div class="activity-icon" style="background-color: ${activity.iconColor}20; color: ${activity.iconColor}">
                            <i class="fas ${activity.icon}"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">${activity.title}</div>
                            <div class="activity-time">${activity.time}</div>
                        </div>
                    </div>
                `).join('');
            } else {
                activityContainer.innerHTML = `
                    <div class="activity-item">
                        <div class="activity-icon" style="background-color: #17a2b820; color: #17a2b8">
                            <i class="fas fa-info-circle"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">No recent activities</div>
                            <div class="activity-time">Start using the system to see activities here</div>
                        </div>
                    </div>
                `;
            }
        } else {
            console.error('Failed to load activities:', response.status);
            // Fallback to a default message
            activityContainer.innerHTML = `
                <div class="activity-item">
                    <div class="activity-icon" style="background-color: #ffc10720; color: #ffc107">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="activity-content">
                        <div class="activity-title">Unable to load recent activities</div>
                        <div class="activity-time">Please try refreshing the page</div>
                    </div>
                </div>
            `;
        }
    } catch (error) {
        console.error('Error loading recent activities:', error);
        activityContainer.innerHTML = `
            <div class="activity-item">
                <div class="activity-icon" style="background-color: #dc354520; color: #dc3545">
                    <i class="fas fa-exclamation-circle"></i>
                </div>
                <div class="activity-content">
                    <div class="activity-title">Error loading activities</div>
                    <div class="activity-time">Check your connection and try again</div>
                </div>
            </div>
        `;
    }
}

// Timesheet Functions
async function loadTimesheets() {
    showLoading(true);

    try {
        const response = await fetch(`${API_BASE}/timesheets/employee/${currentUser.id}`);
        if (response.ok) {
            const timesheets = await response.json();
            displayTimesheets(timesheets);
        } else {
            showToast('Failed to load timesheets', 'error');
        }
    } catch (error) {
        console.error('Error loading timesheets:', error);
        showToast('Failed to load timesheets', 'error');
    }

    showLoading(false);
}

function displayTimesheets(timesheets) {
    const container = document.getElementById('timesheetList');

    if (timesheets.length === 0) {
        container.innerHTML = `
            <div class="card">
                <div style="text-align: center; padding: 40px; color: #666;">
                    <i class="fas fa-calendar-alt" style="font-size: 3rem; margin-bottom: 16px; opacity: 0.5;"></i>
                    <h3>No timesheets found</h3>
                    <p>Create your first timesheet to get started</p>
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = timesheets.map(timesheet => `
        <div class="timesheet-item">
            <div class="timesheet-info">
                <h4>Week of ${formatDate(timesheet.weekStartDate)} - ${formatDate(timesheet.weekEndDate)}</h4>
                <div class="timesheet-meta">
                    Total Hours: ${timesheet.totalHours || 0} |
                    ${timesheet.submittedAt ? 'Submitted: ' + formatDateTime(timesheet.submittedAt) : 'Not submitted'}
                </div>
            </div>
            <div class="timesheet-actions">
                <span class="status-badge status-${timesheet.status.toLowerCase()}">${timesheet.status}</span>
                <button class="btn btn-primary" onclick="openTimesheetEntries(${timesheet.id})">
                    <i class="fas fa-edit"></i> ${timesheet.status === 'DRAFT' ? 'Edit' : 'View'}
                </button>
            </div>
        </div>
    `).join('');
}

// Timesheet Creation and Management
function showCreateTimesheetModal() {
    document.getElementById('createTimesheetModal').classList.add('active');

    // Set default week start date to current Monday
    const today = new Date();
    const monday = new Date(today.setDate(today.getDate() - today.getDay() + 1));
    document.getElementById('weekStartDate').value = monday.toISOString().split('T')[0];
    calculateWeekEndDate();
}

function calculateWeekEndDate() {
    const startDate = document.getElementById('weekStartDate').value;
    if (startDate) {
        const start = new Date(startDate);
        const end = new Date(start);
        end.setDate(start.getDate() + 6);
        document.getElementById('weekEndDate').value = end.toISOString().split('T')[0];
    }
}

async function handleCreateTimesheet(e) {
    e.preventDefault();
    showLoading(true);

    const formData = new FormData(e.target);
    const timesheetData = {
        weekStartDate: formData.get('weekStartDate'),
        weekEndDate: formData.get('weekEndDate')
    };

    try {
        const response = await fetch(`${API_BASE}/timesheets?employeeId=${currentUser.id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(timesheetData)
        });

        if (response.ok) {
            const newTimesheet = await response.json();
            showToast('Timesheet created successfully!', 'success');
            closeModal('createTimesheetModal');
            loadTimesheets();
            openTimesheetEntries(newTimesheet.id);
        } else {
            const errorData = await response.json().catch(() => ({ message: 'Failed to create timesheet' }));
            showToast(errorData.message || 'Failed to create timesheet', 'error');
        }
    } catch (error) {
        console.error('Error creating timesheet:', error);
        showToast('Failed to create timesheet', 'error');
    }

    showLoading(false);
}

async function openTimesheetEntries(timesheetId) {
    showLoading(true);
    currentTimesheet = timesheetId;

    try {
        // Load timesheet details
        const response = await fetch(`${API_BASE}/timesheets/${timesheetId}`);
        if (response.ok) {
            const timesheet = await response.json();
            displayTimesheetEntries(timesheet);
            document.getElementById('timesheetEntryModal').classList.add('active');
        } else {
            showToast('Failed to load timesheet details', 'error');
        }
    } catch (error) {
        console.error('Error loading timesheet:', error);
        showToast('Failed to load timesheet details', 'error');
    }

    showLoading(false);
}

function displayTimesheetEntries(timesheet) {
    // Update timesheet info
    document.getElementById('timesheetInfo').innerHTML = `
        <div class="card">
            <h4>Week of ${formatDate(timesheet.weekStartDate)} - ${formatDate(timesheet.weekEndDate)}</h4>
            <div style="display: flex; gap: 16px; margin-top: 12px;">
                <span class="status-badge status-${timesheet.status.toLowerCase()}">${timesheet.status}</span>
                <span><strong>Total Hours:</strong> ${timesheet.totalHours || 0}</span>
                ${timesheet.submittedAt ? `<span><strong>Submitted:</strong> ${formatDateTime(timesheet.submittedAt)}</span>` : ''}
            </div>
        </div>
    `;

    // Populate project dropdown
    const projectSelect = document.getElementById('entryProject');
    projectSelect.innerHTML = '<option value="">Select Project</option>' +
        projects.map(project => `<option value="${project.id}">${project.projectCode} - ${project.projectName}</option>`).join('');

    // Display existing entries
    const entriesList = document.getElementById('entriesList');
    if (timesheet.entries && timesheet.entries.length > 0) {
        entriesList.innerHTML = `
            <h4 style="margin-bottom: 16px;">Time Entries</h4>
            ${timesheet.entries.map(entry => `
                <div class="entry-item">
                    <div class="entry-details">
                        <div class="entry-project">${entry.projectCode} - ${entry.projectName}</div>
                        <div class="entry-meta">
                            ${formatDate(entry.workDate)} | ${entry.taskDescription || 'No description'}
                        </div>
                    </div>
                    <div class="entry-hours">${entry.hoursWorked}h</div>
                </div>
            `).join('')}
        `;
    } else {
        entriesList.innerHTML = '<p style="text-align: center; color: #666; padding: 20px;">No entries yet. Add your first time entry below.</p>';
    }

    // Show/hide submit button based on status
    const submitBtn = document.getElementById('submitTimesheetBtn');
    submitBtn.style.display = timesheet.status === 'DRAFT' ? 'inline-flex' : 'none';

    // Disable form if not draft
    const addEntryForm = document.getElementById('addEntryForm');
    const formElements = addEntryForm.querySelectorAll('input, select, textarea, button');
    formElements.forEach(element => {
        element.disabled = timesheet.status !== 'DRAFT';
    });
}

async function handleAddEntry(e) {
    e.preventDefault();
    showLoading(true);

    const formData = new FormData(e.target);
    const entryData = {
        projectId: parseInt(formData.get('projectId')),
        workDate: formData.get('workDate'),
        hoursWorked: parseFloat(formData.get('hoursWorked')),
        taskDescription: formData.get('taskDescription')
    };

    try {
        const response = await fetch(`${API_BASE}/timesheets/${currentTimesheet}/entries`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(entryData)
        });

        if (response.ok) {
            showToast('Time entry added successfully!', 'success');
            e.target.reset();
            // Reload timesheet entries
            openTimesheetEntries(currentTimesheet);
        } else {
            const errorData = await response.json().catch(() => ({ message: 'Failed to add entry' }));
            showToast(errorData.message || 'Failed to add entry', 'error');
        }
    } catch (error) {
        console.error('Error adding entry:', error);
        showToast('Failed to add entry', 'error');
    }

    showLoading(false);
}

async function submitTimesheet() {
    if (!confirm('Are you sure you want to submit this timesheet for approval? You will not be able to make changes after submission.')) {
        return;
    }

    showLoading(true);

    try {
        const response = await fetch(`${API_BASE}/timesheets/${currentTimesheet}/submit?employeeId=${currentUser.id}`, {
            method: 'PUT'
        });

        if (response.ok) {
            showToast('Timesheet submitted for approval!', 'success');
            closeModal('timesheetEntryModal');
            loadTimesheets();
            loadDashboardData();
        } else {
            showToast('Failed to submit timesheet', 'error');
        }
    } catch (error) {
        console.error('Error submitting timesheet:', error);
        showToast('Failed to submit timesheet', 'error');
    }

    showLoading(false);
}

// Project Functions
async function loadProjects() {
    showLoading(true);

    try {
        const response = await fetch(`${API_BASE}/projects`);
        if (response.ok) {
            const allProjects = await response.json();
            displayProjects(allProjects);
        } else {
            showToast('Failed to load projects', 'error');
        }
    } catch (error) {
        console.error('Error loading projects:', error);
        showToast('Failed to load projects', 'error');
    }

    showLoading(false);
}

function displayProjects(projectList) {
    const container = document.getElementById('projectsGrid');

    if (projectList.length === 0) {
        container.innerHTML = `
            <div class="card" style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                <i class="fas fa-project-diagram" style="font-size: 3rem; margin-bottom: 16px; opacity: 0.5; color: #666;"></i>
                <h3>No projects found</h3>
                <p>Projects will appear here once they are created</p>
            </div>
        `;
        return;
    }

    container.innerHTML = projectList.map(project => `
        <div class="project-card">
            <div class="project-header">
                <div class="project-code">${project.projectCode}</div>
                <div class="${project.isActive ? 'active-badge' : 'inactive-badge'}">
                    ${project.isActive ? 'Active' : 'Inactive'}
                </div>
            </div>
            <h4 style="margin-bottom: 8px; color: #333;">${project.projectName}</h4>
            <p style="color: #666; margin-bottom: 16px; line-height: 1.5;">
                ${project.description || 'No description available'}
            </p>
            ${project.projectManagerName ? `
                <div style="color: #666; font-size: 0.9rem;">
                    <i class="fas fa-user"></i> Manager: ${project.projectManagerName}
                </div>
            ` : ''}
            ${project.startDate ? `
                <div style="color: #666; font-size: 0.9rem; margin-top: 8px;">
                    <i class="fas fa-calendar"></i>
                    ${formatDate(project.startDate)} - ${project.endDate ? formatDate(project.endDate) : 'Ongoing'}
                </div>
            ` : ''}
        </div>
    `).join('');
}

// Approval Functions (for managers)
async function loadPendingApprovals() {
    if (currentUser.role !== 'MANAGER' && currentUser.role !== 'ADMIN') return;

    showLoading(true);

    try {
        const response = await fetch(`${API_BASE}/timesheets/pending-approvals?managerId=${currentUser.id}`);
        if (response.ok) {
            const approvals = await response.json();
            displayPendingApprovals(approvals);
        } else {
            showToast('Failed to load pending approvals', 'error');
        }
    } catch (error) {
        console.error('Error loading approvals:', error);
        showToast('Failed to load pending approvals', 'error');
    }

    showLoading(false);
}

function displayPendingApprovals(approvals) {
    const container = document.getElementById('approvalsList');

    if (approvals.length === 0) {
        container.innerHTML = `
            <div class="card" style="text-align: center; padding: 40px;">
                <i class="fas fa-check-circle" style="font-size: 3rem; margin-bottom: 16px; opacity: 0.5; color: #28a745;"></i>
                <h3>No pending approvals</h3>
                <p>All timesheets have been reviewed</p>
            </div>
        `;
        return;
    }

    container.innerHTML = approvals.map(timesheet => `
        <div class="card" style="margin-bottom: 20px;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                <div>
                    <h4>${timesheet.employeeName}</h4>
                    <div style="color: #666; margin: 8px 0;">
                        Week of ${formatDate(timesheet.weekStartDate)} - ${formatDate(timesheet.weekEndDate)}
                    </div>
                    <div style="color: #666;">
                        <strong>Total Hours:</strong> ${timesheet.totalHours || 0} |
                        <strong>Submitted:</strong> ${formatDateTime(timesheet.submittedAt)}
                    </div>
                </div>
                <div style="display: flex; gap: 12px;">
                    <button class="btn btn-success" onclick="approveTimesheet(${timesheet.id})">
                        <i class="fas fa-check"></i> Approve
                    </button>
                    <button class="btn btn-warning" onclick="rejectTimesheet(${timesheet.id})">
                        <i class="fas fa-times"></i> Reject
                    </button>
                    <button class="btn btn-secondary" onclick="openTimesheetEntries(${timesheet.id})">
                        <i class="fas fa-eye"></i> View Details
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

async function approveTimesheet(timesheetId) {
    if (!confirm('Are you sure you want to approve this timesheet?')) return;

    showLoading(true);

    try {
        const response = await fetch(`${API_BASE}/timesheets/${timesheetId}/approve?managerId=${currentUser.id}`, {
            method: 'PUT'
        });

        if (response.ok) {
            showToast('Timesheet approved successfully!', 'success');
            loadPendingApprovals();
            loadDashboardData();
        } else {
            showToast('Failed to approve timesheet', 'error');
        }
    } catch (error) {
        console.error('Error approving timesheet:', error);
        showToast('Failed to approve timesheet', 'error');
    }

    showLoading(false);
}

async function rejectTimesheet(timesheetId) {
    const comment = prompt('Please provide a reason for rejection:');
    if (!comment) return;

    showLoading(true);

    try {
        const params = new URLSearchParams();
        params.append('managerId', currentUser.id);
        params.append('comment', comment);

        const response = await fetch(`${API_BASE}/timesheets/${timesheetId}/reject?${params}`, {
            method: 'PUT'
        });

        if (response.ok) {
            showToast('Timesheet rejected', 'success');
            loadPendingApprovals();
            loadDashboardData();
        } else {
            showToast('Failed to reject timesheet', 'error');
        }
    } catch (error) {
        console.error('Error rejecting timesheet:', error);
        showToast('Failed to reject timesheet', 'error');
    }

    showLoading(false);
}

// Employee Management Functions
async function loadEmployees() {
    if (currentUser.role !== 'ADMIN' && currentUser.role !== 'MANAGER') return;

    showLoading(true);

    try {
        const response = await fetch(`${API_BASE}/employees`);
        if (response.ok) {
            const allEmployees = await response.json();
            displayEmployees(allEmployees);
        } else {
            showToast('Failed to load employees', 'error');
        }
    } catch (error) {
        console.error('Error loading employees:', error);
        showToast('Failed to load employees', 'error');
    }

    showLoading(false);
}

function displayEmployees(employeeList) {
    const container = document.getElementById('employeesTable');

    if (employeeList.length === 0) {
        container.innerHTML = `
            <div class="card" style="text-align: center; padding: 40px;">
                <i class="fas fa-users" style="font-size: 3rem; margin-bottom: 16px; opacity: 0.5; color: #666;"></i>
                <h3>No employees found</h3>
                <p>Employees will appear here once they are added</p>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <table class="table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Manager</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${employeeList.map(employee => `
                    <tr>
                        <td>${employee.fullName}</td>
                        <td>${employee.email}</td>
                        <td><span class="status-badge status-${employee.role.toLowerCase()}">${employee.role}</span></td>
                        <td>${employee.managerName || 'N/A'}</td>
                        <td>
                            <button class="btn btn-secondary" onclick="editEmployee(${employee.id})" style="margin-right: 8px;">
                                <i class="fas fa-edit"></i> Edit
                            </button>
                            ${currentUser.role === 'ADMIN' ? `
                                <button class="btn btn-warning" onclick="deleteEmployee(${employee.id})">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            ` : ''}
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

function showCreateEmployeeModal() {
    document.getElementById('createEmployeeModal').classList.add('active');

    // Load managers for dropdown
    loadManagersForDropdown('empManager');
}

async function loadManagersForDropdown(selectId) {
    try {
        const response = await fetch(`${API_BASE}/employees`);
        if (response.ok) {
            const allEmployees = await response.json();
            const managers = allEmployees.filter(emp => emp.role === 'MANAGER' || emp.role === 'ADMIN');

            const select = document.getElementById(selectId);
            select.innerHTML = '<option value="">Select Manager (Optional)</option>' +
                managers.map(manager => `<option value="${manager.id}">${manager.fullName}</option>`).join('');
        }
    } catch (error) {
        console.error('Error loading managers:', error);
    }
}

async function handleCreateEmployee(e) {
    e.preventDefault();
    showLoading(true);

    const formData = new FormData(e.target);
    const employeeData = {
        email: formData.get('email'),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        role: formData.get('role'),
        managerId: formData.get('managerId') || null
    };

    const password = formData.get('password');

    try {
        // Construct the URL properly to ensure it goes to /api/employees
        const apiUrl = `${API_BASE}/employees`;
        const params = new URLSearchParams();
        params.append('password', password);

        console.log('Creating employee at URL:', `${apiUrl}?${params.toString()}`);
        console.log('Employee data:', employeeData);

        const response = await fetch(`${apiUrl}?${params.toString()}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(employeeData)
        });

        if (response.ok) {
            const newEmployee = await response.json();
            showToast('Employee created successfully!', 'success');
            closeModal('createEmployeeModal');
            loadEmployees();
            e.target.reset();
        } else {
            const errorText = await response.text();
            console.error('Employee creation failed:', errorText);
            console.error('Response status:', response.status);
            console.error('Response URL:', response.url);
            showToast('Failed to create employee: ' + errorText, 'error');
        }
    } catch (error) {
        console.error('Error creating employee:', error);
        showToast('Failed to create employee: ' + error.message, 'error');
    }

    showLoading(false);
}

async function deleteEmployee(employeeId) {
    if (!confirm('Are you sure you want to delete this employee? This action cannot be undone.')) {
        return;
    }

    showLoading(true);

    try {
        const response = await fetch(`${API_BASE}/employees/${employeeId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showToast('Employee deleted successfully!', 'success');
            loadEmployees();
        } else {
            showToast('Failed to delete employee', 'error');
        }
    } catch (error) {
        console.error('Error deleting employee:', error);
        showToast('Failed to delete employee', 'error');
    }

    showLoading(false);
}

// Project Management Functions
function showCreateProjectModal() {
    document.getElementById('createProjectModal').classList.add('active');

    // Load managers for project manager dropdown
    loadManagersForDropdown('projManager');
}

async function handleCreateProject(e) {
    e.preventDefault();
    showLoading(true);

    const formData = new FormData(e.target);
    const projectData = {
        projectCode: formData.get('projectCode'),
        projectName: formData.get('projectName'),
        description: formData.get('description'),
        startDate: formData.get('startDate') || null,
        endDate: formData.get('endDate') || null,
        projectManagerId: formData.get('projectManagerId') || null,
        isActive: formData.get('isActive') !== null
    };

    try {
        const response = await fetch(`${API_BASE}/projects`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(projectData)
        });

        if (response.ok) {
            showToast('Project created successfully!', 'success');
            closeModal('createProjectModal');
            loadProjects();
            loadActiveProjectsCount(); // Refresh dashboard
            e.target.reset();
        } else {
            const errorData = await response.json().catch(() => ({ message: 'Failed to create project' }));
            showToast(errorData.message || 'Failed to create project', 'error');
        }
    } catch (error) {
        console.error('Error creating project:', error);
        showToast('Failed to create project', 'error');
    }

    showLoading(false);
}

// Report Generation Functions
async function generateReport() {
    const reportType = document.getElementById('reportType').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!startDate || !endDate) {
        showToast('Please select start and end dates', 'warning');
        return;
    }

    if (new Date(startDate) > new Date(endDate)) {
        showToast('Start date cannot be after end date', 'warning');
        return;
    }

    showLoading(true);

    try {
        let apiUrl = '';
        let entityId = '';

        if (reportType === 'employee') {
            entityId = document.getElementById('reportEmployee').value;
            if (!entityId) {
                showToast('Please select an employee', 'warning');
                showLoading(false);
                return;
            }
            apiUrl = `${API_BASE}/reports/employee/${entityId}?startDate=${startDate}&endDate=${endDate}`;
        } else {
            entityId = document.getElementById('reportProject').value;
            if (!entityId) {
                showToast('Please select a project', 'warning');
                showLoading(false);
                return;
            }
            apiUrl = `${API_BASE}/reports/project/${entityId}?startDate=${startDate}&endDate=${endDate}`;
        }

        console.log('Making report request to:', apiUrl);

        const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        console.log('Report response status:', response.status);
        console.log('Report response headers:', response.headers);

        if (response.ok) {
            const reportData = await response.json();
            console.log('Report data received:', reportData);
            displayReportResults(reportData, reportType);
            showToast('Report generated successfully!', 'success');
        } else {
            const errorText = await response.text();
            console.error('Report generation failed:', errorText);
            console.error('Response status:', response.status);
            showToast(`Failed to generate report: ${response.status} - ${errorText}`, 'error');
        }
    } catch (error) {
        console.error('Error generating report:', error);
        showToast('Failed to generate report: ' + error.message, 'error');
    }

    showLoading(false);
}

function displayReportResults(reportData, reportType) {
    const container = document.getElementById('reportResults');

    container.innerHTML = `
        <div class="report-summary">
            <h3>Report Summary</h3>
            <div class="summary-cards" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 24px;">
                <div class="card">
                    <div class="card-body" style="text-align: center;">
                        <div class="stat-number text-primary">${reportData.totalHours || 0}</div>
                        <div class="stat-label">Total Hours</div>
                    </div>
                </div>
                <div class="card">
                    <div class="card-body" style="text-align: center;">
                        <div class="stat-number text-info">${reportData.entries ? reportData.entries.length : 0}</div>
                        <div class="stat-label">Time Entries</div>
                    </div>
                </div>
                <div class="card">
                    <div class="card-body" style="text-align: center;">
                        <div class="stat-number text-success">${formatDate(reportData.startDate)} - ${formatDate(reportData.endDate)}</div>
                        <div class="stat-label">Date Range</div>
                    </div>
                </div>
            </div>

            ${reportData.projectHours || reportData.employeeHours ? `
                <div class="breakdown-section">
                    <h4>${reportType === 'employee' ? 'Hours by Project' : 'Hours by Employee'}</h4>
                    <div class="breakdown-items" style="display: grid; gap: 8px; margin-bottom: 24px;">
                        ${Object.entries(reportData.projectHours || reportData.employeeHours || {}).map(([name, hours]) => `
                            <div class="breakdown-item" style="display: flex; justify-content: space-between; align-items: center; padding: 12px; background: #f8f9fa; border-radius: 6px;">
                                <span>${name}</span>
                                <span class="badge" style="background: #667eea; color: white; padding: 4px 8px; border-radius: 12px;">${hours}h</span>
                            </div>
                        `).join('')}
                    </div>
                </div>
            ` : ''}

            ${reportData.entries && reportData.entries.length > 0 ? `
                <div class="detailed-entries">
                    <h4>Detailed Time Entries</h4>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>${reportType === 'employee' ? 'Project' : 'Employee'}</th>
                                <th>Hours</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${reportData.entries.map(entry => `
                                <tr>
                                    <td>${formatDate(entry.workDate)}</td>
                                    <td>${reportType === 'employee' ? entry.projectName : entry.employeeName}</td>
                                    <td><span class="entry-hours">${entry.hoursWorked}h</span></td>
                                    <td>${entry.taskDescription || 'N/A'}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            ` : '<p style="text-align: center; color: #666; padding: 20px;">No time entries found for the selected criteria.</p>'}
        </div>
    `;
}

async function exportReport() {
    const reportType = document.getElementById('reportType').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!startDate || !endDate) {
        showToast('Please select start and end dates', 'warning');
        return;
    }

    showLoading(true);

    try {
        let apiUrl = '';
        let entityId = '';
        let filename = '';

        if (reportType === 'employee') {
            entityId = document.getElementById('reportEmployee').value;
            if (!entityId) {
                showToast('Please select an employee', 'warning');
                showLoading(false);
                return;
            }
            apiUrl = `${API_BASE}/reports/employee/${entityId}/export?startDate=${startDate}&endDate=${endDate}`;
            filename = `employee_report_${entityId}_${startDate}_${endDate}.xlsx`;
        } else {
            entityId = document.getElementById('reportProject').value;
            if (!entityId) {
                showToast('Please select a project', 'warning');
                showLoading(false);
                return;
            }
            apiUrl = `${API_BASE}/reports/project/${entityId}/export?startDate=${startDate}&endDate=${endDate}`;
            filename = `project_report_${entityId}_${startDate}_${endDate}.xlsx`;
        }

        const response = await fetch(apiUrl);
        if (response.ok) {
            const blob = await response.blob();

            // Create download link
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);

            showToast('Report exported successfully!', 'success');
        } else {
            showToast('Failed to export report', 'error');
        }
    } catch (error) {
        console.error('Error exporting report:', error);
        showToast('Failed to export report', 'error');
    }

    showLoading(false);
}

// Debug function to test API endpoints
async function testReportEndpoint() {
    if (!currentUser) {
        showToast('Please login first', 'warning');
        return;
    }

    console.log('Testing report endpoint with current user:', currentUser);

    // Test with a simple date range
    const today = new Date();
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    const startDate = lastMonth.toISOString().split('T')[0];
    const endDate = today.toISOString().split('T')[0];

    // First, let's check if we have any employees
    try {
        const employeesResponse = await fetch(`${API_BASE}/employees`);
        console.log('Employees endpoint status:', employeesResponse.status);

        if (employeesResponse.ok) {
            const employees = await employeesResponse.json();
            console.log('Available employees:', employees);

            if (employees.length > 0) {
                // Test with the first employee
                const testEmployeeId = employees[0].id;
                console.log('Testing report with employee ID:', testEmployeeId);

                const reportUrl = `${API_BASE}/reports/employee/${testEmployeeId}?startDate=${startDate}&endDate=${endDate}`;
                console.log('Report URL:', reportUrl);

                const reportResponse = await fetch(reportUrl, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    }
                });

                console.log('Report response status:', reportResponse.status);
                console.log('Report response headers:', Array.from(reportResponse.headers.entries()));

                if (reportResponse.ok) {
                    const reportData = await reportResponse.json();
                    console.log('Report data:', reportData);
                    showToast('Report test successful! Check console for details.', 'success');
                } else {
                    const errorText = await reportResponse.text();
                    console.error('Report test failed:', errorText);
                    showToast(`Report test failed: ${reportResponse.status} - ${errorText}`, 'error');
                }
            } else {
                showToast('No employees found. Create some employees first.', 'warning');
            }
        } else {
            console.error('Failed to fetch employees:', employeesResponse.status);
        }
    } catch (error) {
        console.error('Error testing report endpoint:', error);
        showToast('Error testing report endpoint: ' + error.message, 'error');
    }
}

// Debug function to check available data
async function checkAvailableData() {
    console.log('=== Checking Available Data ===');

    try {
        // Check employees
        const employeesResponse = await fetch(`${API_BASE}/employees`);
        if (employeesResponse.ok) {
            const employees = await employeesResponse.json();
            console.log('Employees count:', employees.length);
            console.log('Employees:', employees);
        }

        // Check projects
        const projectsResponse = await fetch(`${API_BASE}/projects`);
        if (projectsResponse.ok) {
            const projects = await projectsResponse.json();
            console.log('Projects count:', projects.length);
            console.log('Projects:', projects);
        }

        // Check timesheets if user is logged in
        if (currentUser) {
            const timesheetsResponse = await fetch(`${API_BASE}/timesheets/employee/${currentUser.id}`);
            if (timesheetsResponse.ok) {
                const timesheets = await timesheetsResponse.json();
                console.log('Timesheets count:', timesheets.length);
                console.log('Timesheets:', timesheets);

                // Check timesheet entries
                if (timesheets.length > 0) {
                    for (const timesheet of timesheets) {
                        const entriesResponse = await fetch(`${API_BASE}/timesheets/${timesheet.id}`);
                        if (entriesResponse.ok) {
                            const timesheetDetails = await entriesResponse.json();
                            console.log(`Timesheet ${timesheet.id} entries:`, timesheetDetails.entries);
                        }
                    }
                }
            }
        }

        showToast('Data check complete. See console for details.', 'info');
    } catch (error) {
        console.error('Error checking data:', error);
        showToast('Error checking data: ' + error.message, 'error');
    }
}

// Utility Functions
function showLoading(show) {
    const spinner = document.getElementById('loadingSpinner');
    if (show) {
        spinner.classList.add('active');
    } else {
        spinner.classList.remove('active');
    }
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <div style="display: flex; align-items: center; gap: 12px;">
            <i class="fas ${getToastIcon(type)}"></i>
            <span>${message}</span>
        </div>
    `;

    container.appendChild(toast);

    // Remove toast after 5 seconds
    setTimeout(() => {
        toast.remove();
    }, 5000);
}

function getToastIcon(type) {
    switch(type) {
        case 'success': return 'fa-check-circle';
        case 'error': return 'fa-exclamation-circle';
        case 'warning': return 'fa-exclamation-triangle';
        default: return 'fa-info-circle';
    }
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Close modals when clicking outside
window.addEventListener('click', function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('active');
    }
});

// Additional functions for features
async function loadReportData() {
    try {
        const [employeesResponse, projectsResponse] = await Promise.all([
            fetch(`${API_BASE}/employees`),
            fetch(`${API_BASE}/projects`)
        ]);

        if (employeesResponse.ok) {
            employees = await employeesResponse.json();
            const employeeSelect = document.getElementById('reportEmployee');
            if (employeeSelect) {
                employeeSelect.innerHTML = '<option value="">Select Employee</option>' +
                    employees.map(emp => `<option value="${emp.id}">${emp.fullName}</option>`).join('');
            }
        }

        if (projectsResponse.ok) {
            const projectsList = await projectsResponse.json();
            const projectSelect = document.getElementById('reportProject');
            if (projectSelect) {
                projectSelect.innerHTML = '<option value="">Select Project</option>' +
                    projectsList.map(proj => `<option value="${proj.id}">${proj.projectCode} - ${proj.projectName}</option>`).join('');
            }
        }
    } catch (error) {
        console.error('Error loading report data:', error);
    }
}

function updateReportForm() {
    const reportType = document.getElementById('reportType');
    const employeeGroup = document.getElementById('employeeSelectGroup');
    const projectGroup = document.getElementById('projectSelectGroup');

    if (reportType && employeeGroup && projectGroup) {
        if (reportType.value === 'employee') {
            employeeGroup.style.display = 'block';
            projectGroup.style.display = 'none';
        } else {
            employeeGroup.style.display = 'none';
            projectGroup.style.display = 'block';
        }
    }
}

// Additional utility functions
function editEmployee(employeeId) {
    loadEmployeeForEdit(employeeId);
}

async function loadEmployeeForEdit(employeeId) {
    try {
        showLoading(true);
        const response = await fetch(`${API_BASE}/employees/${employeeId}`);

        if (response.ok) {
            const employee = await response.json();
            populateEditEmployeeForm(employee);
            document.getElementById('editEmployeeModal').classList.add('active');
        } else {
            const errorText = await response.text();
            showToast('Failed to load employee details: ' + errorText, 'error');
        }
    } catch (error) {
        console.error('Error loading employee for edit:', error);
        showToast('Failed to load employee details: ' + error.message, 'error');
    }

    showLoading(false);
}

function populateEditEmployeeForm(employee) {
    document.getElementById('editEmployeeId').value = employee.id;
    document.getElementById('editEmail').value = employee.email;
    document.getElementById('editFirstName').value = employee.firstName;
    document.getElementById('editLastName').value = employee.lastName;
    document.getElementById('editRole').value = employee.role;

    // Load managers dropdown for edit form
    loadManagersForDropdown('editEmpManager');

    // Set manager if exists (delay to ensure dropdown is populated)
    setTimeout(() => {
        if (employee.managerId) {
            document.getElementById('editEmpManager').value = employee.managerId;
        }
    }, 100);
}

async function handleEditEmployee(e) {
    e.preventDefault();
    showLoading(true);

    const formData = new FormData(e.target);
    const employeeId = formData.get('employeeId');

    const employeeData = {
        email: formData.get('email'),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        role: formData.get('role'),
        managerId: formData.get('managerId') || null
    };

    try {
        console.log('Updating employee ID:', employeeId, 'with data:', employeeData);

        const response = await fetch(`${API_BASE}/employees/${employeeId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(employeeData)
        });

        if (response.ok) {
            const updatedEmployee = await response.json();
            showToast('Employee updated successfully!', 'success');
            closeModal('editEmployeeModal');
            loadEmployees();
            e.target.reset();
        } else {
            const errorText = await response.text();
            console.error('Employee update failed:', errorText);
            showToast('Failed to update employee: ' + errorText, 'error');
        }
    } catch (error) {
        console.error('Error updating employee:', error);
        showToast('Failed to update employee: ' + error.message, 'error');
    }

    showLoading(false);
}
