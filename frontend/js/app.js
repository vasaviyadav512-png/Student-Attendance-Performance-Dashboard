/**
 * Application Controller
 * Handles View Routing, UI Rendering, Chart.js Visualizations, and Modal Operations.
 */

// Global Chart Instances
let attendanceChartInstance = null;
let performanceChartInstance = null;

// Global State
let allStudentsList = [];
let allSubjectsList = [];
let currentEditingStudentId = null;

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

function initApp() {
    setupAuthListeners();
    setupNavigation();

    if (Api.isAuthenticated()) {
        showApp();
    } else {
        showLogin();
    }
}

// -----------------------------------------------------------------
// Authentication Flow
// -----------------------------------------------------------------
function setupAuthListeners() {
    window.addEventListener('auth:expired', () => {
        showToast('warning', 'Session Expired', 'Please login again to continue.');
        showLogin();
    });

    window.addEventListener('auth:logout', () => {
        showLogin();
    });

    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('login-username').value.trim();
            const password = document.getElementById('login-password').value.trim();
            const btn = document.getElementById('login-btn');

            if (!username || !password) {
                showToast('error', 'Validation Error', 'Please enter username and password.');
                return;
            }

            try {
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Logging in...';
                await Api.login(username, password);
                showToast('success', 'Welcome!', 'Logged in successfully.');
                showApp();
            } catch (err) {
                showToast('error', 'Login Failed', err.message || 'Invalid credentials');
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-box-arrow-in-right me-2"></i>Sign In';
            }
        });
    }

    document.getElementById('logout-btn')?.addEventListener('click', () => {
        Api.logout();
    });
}

function quickFillLogin(user, pass) {
    document.getElementById('login-username').value = user;
    document.getElementById('login-password').value = pass;
    document.getElementById('login-form').dispatchEvent(new Event('submit'));
}

function showLogin() {
    document.getElementById('login-container').classList.remove('d-none');
    document.getElementById('app-container').classList.add('d-none');
}

function showApp() {
    document.getElementById('login-container').classList.add('d-none');
    document.getElementById('app-container').classList.remove('d-none');

    // Update user profile badges
    const user = Api.getUser();
    if (user) {
        document.getElementById('user-name-display').textContent = user.fullName || user.username;
        document.getElementById('user-role-badge').textContent = user.role;
        document.getElementById('user-avatar-initial').textContent = (user.fullName || user.username).charAt(0).toUpperCase();
        
        // Hide/Show Admin-only features if any
        const isAdmin = user.role === 'ADMIN';
        document.querySelectorAll('.admin-only').forEach(el => {
            el.style.display = isAdmin ? '' : 'none';
        });
    }

    // Load initial reference data & Dashboard
    loadSubjects();
    navigateTo('dashboard');
}

// -----------------------------------------------------------------
// Navigation & Views
// -----------------------------------------------------------------
function setupNavigation() {
    document.querySelectorAll('.nav-link[data-view]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const view = link.getAttribute('data-view');
            navigateTo(view);
        });
    });

    // Mobile sidebar toggle
    document.getElementById('sidebar-toggle')?.addEventListener('click', () => {
        document.getElementById('sidebar').classList.toggle('show');
    });
}

function navigateTo(viewName) {
    // Update active nav link
    document.querySelectorAll('.nav-link[data-view]').forEach(link => {
        if (link.getAttribute('data-view') === viewName) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

    // Hide all views, show targeted view
    document.querySelectorAll('.view-section').forEach(section => {
        section.classList.add('d-none');
    });

    const targetSection = document.getElementById(`view-${viewName}`);
    if (targetSection) {
        targetSection.classList.remove('d-none');
    }

    // Update header title
    const titles = {
        'dashboard': 'Dashboard Overview',
        'students': 'Student Management',
        'attendance': 'Attendance Management',
        'marks': 'Academic Performance & Marks',
        'reports': 'Performance Reports & Transcript'
    };
    document.getElementById('header-view-title').textContent = titles[viewName] || 'Dashboard';

    // Auto-close sidebar on mobile
    if (window.innerWidth < 992) {
        document.getElementById('sidebar').classList.remove('show');
    }

    // Trigger view-specific loader
    switch (viewName) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'students':
            loadStudentsView();
            break;
        case 'attendance':
            loadAttendanceView();
            break;
        case 'marks':
            loadMarksView();
            break;
        case 'reports':
            loadReportsView();
            break;
    }
}

// -----------------------------------------------------------------
// 1. DASHBOARD MODULE
// -----------------------------------------------------------------
async function loadDashboard() {
    try {
        const res = await Api.getDashboardStats();
        if (!res.success || !res.data) return;

        const data = res.data;

        // KPI Counters
        document.getElementById('stat-total-students').textContent = data.totalStudents;
        document.getElementById('stat-avg-attendance').textContent = `${data.averageAttendance}%`;
        document.getElementById('stat-avg-marks').textContent = `${data.averageMarks}%`;
        document.getElementById('stat-students-risk').textContent = data.studentsAtRiskCount;

        // Render Charts
        renderAttendanceChart(data.totalPresent, data.totalAbsent);
        renderPerformanceChart(data.subjectLabels, data.subjectAverages);

        // Render Recent Attendance Table
        const recentBody = document.getElementById('dashboard-recent-attendance');
        if (recentBody) {
            if (!data.recentAttendance || data.recentAttendance.length === 0) {
                recentBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-3">No recent attendance records found</td></tr>';
            } else {
                recentBody.innerHTML = data.recentAttendance.map(a => `
                    <tr>
                        <td><strong>${escapeHtml(a.studentRoll)}</strong></td>
                        <td>${escapeHtml(a.studentName)}</td>
                        <td><span class="badge bg-light text-dark">${escapeHtml(a.subjectCode)}</span></td>
                        <td>${a.attendanceDate}</td>
                        <td>
                            <span class="badge ${a.status === 'PRESENT' ? 'badge-present' : 'badge-absent'}">
                                ${a.status}
                            </span>
                        </td>
                    </tr>
                `).join('');
            }
        }

        // Render At-Risk List Table
        const riskBody = document.getElementById('dashboard-risk-students');
        if (riskBody) {
            if (!data.studentsAtRisk || data.studentsAtRisk.length === 0) {
                riskBody.innerHTML = '<tr><td colspan="4" class="text-center text-success py-3"><i class="bi bi-check-circle me-1"></i>All students are in good standing!</td></tr>';
            } else {
                riskBody.innerHTML = data.studentsAtRisk.map(s => `
                    <tr>
                        <td><strong>${escapeHtml(s.studentId)}</strong></td>
                        <td>${escapeHtml(s.name)}</td>
                        <td>
                            <span class="badge ${s.attendancePercentage < 75 ? 'bg-danger' : 'bg-success'}">
                                ${s.attendancePercentage}%
                            </span>
                        </td>
                        <td>
                            <span class="badge-risk">
                                <i class="bi bi-exclamation-triangle-fill"></i> ${escapeHtml(s.riskReason)}
                            </span>
                        </td>
                    </tr>
                `).join('');
            }
        }

    } catch (err) {
        showToast('error', 'Dashboard Error', err.message);
    }
}

function renderAttendanceChart(present, absent) {
    const ctx = document.getElementById('attendanceDoughnutChart');
    if (!ctx) return;

    if (attendanceChartInstance) {
        attendanceChartInstance.destroy();
    }

    const total = present + absent;
    if (total === 0) {
        present = 1;
        absent = 0;
    }

    attendanceChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Present', 'Absent'],
            datasets: [{
                data: [present, absent],
                backgroundColor: ['#10b981', '#ef4444'],
                hoverBackgroundColor: ['#059669', '#dc2626'],
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'bottom' },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const val = context.raw || 0;
                            const pct = total > 0 ? ((val / total) * 100).toFixed(1) : 0;
                            return ` ${context.label}: ${val} (${pct}%)`;
                        }
                    }
                }
            },
            cutout: '68%'
        }
    });
}

function renderPerformanceChart(labels, averages) {
    const ctx = document.getElementById('performanceBarChart');
    if (!ctx) return;

    if (performanceChartInstance) {
        performanceChartInstance.destroy();
    }

    performanceChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels && labels.length ? labels : ['No Subjects'],
            datasets: [{
                label: 'Class Average Marks (%)',
                data: averages && averages.length ? averages : [0],
                backgroundColor: '#3b82f6',
                borderRadius: 6,
                maxBarThickness: 45
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        callback: val => `${val}%`
                    }
                }
            },
            plugins: {
                legend: { display: false }
            }
        }
    });
}

// -----------------------------------------------------------------
// 2. STUDENTS MANAGEMENT MODULE
// -----------------------------------------------------------------
async function loadStudentsView() {
    await populateDepartmentFilters();
    await fetchAndRenderStudents();
}

async function populateDepartmentFilters() {
    try {
        const res = await Api.getDepartments();
        if (res.success && res.data) {
            const depts = res.data;
            const selectFilter = document.getElementById('student-filter-department');
            if (selectFilter) {
                const currentVal = selectFilter.value;
                selectFilter.innerHTML = '<option value="">All Departments</option>' +
                    depts.map(d => `<option value="${escapeHtml(d)}">${escapeHtml(d)}</option>`).join('');
                selectFilter.value = currentVal;
            }
        }
    } catch (e) {
        console.error('Failed to load departments:', e);
    }
}

async function fetchAndRenderStudents() {
    const keyword = document.getElementById('student-search-input')?.value.trim() || '';
    const department = document.getElementById('student-filter-department')?.value || '';
    const tbody = document.getElementById('students-table-body');

    try {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4"><span class="spinner-border spinner-border-sm me-2"></span>Loading students...</td></tr>';
        const res = await Api.getStudents(keyword, department);

        if (!res.success || !res.data || res.data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted py-4">No student records found</td></tr>';
            allStudentsList = [];
            return;
        }

        allStudentsList = res.data;
        tbody.innerHTML = res.data.map(s => {
            const attBadge = s.attendancePercentage < 75
                ? `<span class="badge bg-danger">${s.attendancePercentage}% (Low)</span>`
                : `<span class="badge bg-success">${s.attendancePercentage}%</span>`;

            const marksBadge = s.averageMarksPercentage < 40
                ? `<span class="badge bg-danger">${s.averageMarksPercentage}% (Fail)</span>`
                : `<span class="badge bg-primary">${s.averageMarksPercentage}%</span>`;

            const statusBadge = s.atRisk
                ? `<span class="badge-risk" title="${escapeHtml(s.riskReason)}"><i class="bi bi-exclamation-triangle-fill"></i> At Risk</span>`
                : `<span class="badge-good"><i class="bi bi-check-circle-fill"></i> Normal</span>`;

            return `
                <tr>
                    <td><strong>${escapeHtml(s.studentId)}</strong></td>
                    <td>${escapeHtml(s.name)}</td>
                    <td>${escapeHtml(s.email)}</td>
                    <td>${escapeHtml(s.department)}</td>
                    <td>Year ${s.year} - Sec ${escapeHtml(s.section)}</td>
                    <td>${attBadge}</td>
                    <td>${marksBadge}</td>
                    <td>${statusBadge}</td>
                    <td class="text-end">
                        <button class="btn btn-sm btn-outline-info me-1" onclick="viewStudentReport(${s.id})" title="View Report">
                            <i class="bi bi-file-earmark-person"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditStudentModal(${s.id})" title="Edit Student">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger admin-only" onclick="confirmDeleteStudent(${s.id}, '${escapeHtml(s.name)}')" title="Delete Student">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        }).join('');

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center text-danger py-4">${err.message}</td></tr>`;
    }
}

function openAddStudentModal() {
    currentEditingStudentId = null;
    document.getElementById('studentModalTitle').textContent = 'Add New Student';
    document.getElementById('student-form').reset();
    document.getElementById('student-form-id').value = '';
    const modal = new bootstrap.Modal(document.getElementById('studentModal'));
    modal.show();
}

async function openEditStudentModal(id) {
    try {
        const res = await Api.getStudent(id);
        if (!res.success || !res.data) return;

        const s = res.data;
        currentEditingStudentId = s.id;
        document.getElementById('studentModalTitle').textContent = 'Edit Student Details';
        document.getElementById('student-form-id').value = s.id;
        document.getElementById('student-roll').value = s.studentId;
        document.getElementById('student-name').value = s.name;
        document.getElementById('student-email').value = s.email;
        document.getElementById('student-department').value = s.department;
        document.getElementById('student-year').value = s.year;
        document.getElementById('student-section').value = s.section;

        const modal = new bootstrap.Modal(document.getElementById('studentModal'));
        modal.show();
    } catch (err) {
        showToast('error', 'Error', err.message);
    }
}

async function saveStudent(e) {
    e.preventDefault();
    const id = document.getElementById('student-form-id').value;
    const studentData = {
        studentId: document.getElementById('student-roll').value.trim(),
        name: document.getElementById('student-name').value.trim(),
        email: document.getElementById('student-email').value.trim(),
        department: document.getElementById('student-department').value.trim(),
        year: parseInt(document.getElementById('student-year').value, 10),
        section: document.getElementById('student-section').value.trim().toUpperCase()
    };

    try {
        if (id) {
            await Api.updateStudent(id, studentData);
            showToast('success', 'Updated', 'Student details updated successfully.');
        } else {
            await Api.createStudent(studentData);
            showToast('success', 'Created', 'New student registered successfully.');
        }

        bootstrap.Modal.getInstance(document.getElementById('studentModal')).hide();
        fetchAndRenderStudents();
        populateDepartmentFilters();
    } catch (err) {
        let msg = err.message;
        if (err.details && err.details.length) {
            msg += ': ' + err.details.join(', ');
        }
        showToast('error', 'Save Failed', msg);
    }
}

function confirmDeleteStudent(id, name) {
    if (confirm(`Are you sure you want to delete student "${name}"? All associated attendance and marks will also be permanently deleted.`)) {
        deleteStudent(id);
    }
}

async function deleteStudent(id) {
    try {
        await Api.deleteStudent(id);
        showToast('success', 'Deleted', 'Student record removed.');
        fetchAndRenderStudents();
    } catch (err) {
        showToast('error', 'Delete Failed', err.message);
    }
}

// -----------------------------------------------------------------
// 3. ATTENDANCE MANAGEMENT MODULE
// -----------------------------------------------------------------
async function loadAttendanceView() {
    // Populate Subject dropdowns
    await loadSubjects();
    populateSubjectDropdowns();

    // Set today's date in date picker if empty
    const dateInput = document.getElementById('attendance-mark-date');
    if (dateInput && !dateInput.value) {
        dateInput.value = new Date().toISOString().split('T')[0];
    }

    // Load roll-call list
    loadRollCallList();

    // Populate history filter students
    populateStudentDropdown('attendance-filter-student');
    filterAttendanceRecords();
}

async function loadSubjects() {
    try {
        const res = await Api.getSubjects();
        if (res.success && res.data) {
            allSubjectsList = res.data;
        }
    } catch (e) {
        console.error('Failed to load subjects:', e);
    }
}

function populateSubjectDropdowns() {
    const markSub = document.getElementById('attendance-mark-subject');
    const filterSub = document.getElementById('attendance-filter-subject');
    const marksModalSub = document.getElementById('mark-subject-select');
    const marksFilterSub = document.getElementById('marks-filter-subject');

    const options = allSubjectsList.map(s => 
        `<option value="${s.id}">${escapeHtml(s.subjectCode)} - ${escapeHtml(s.subjectName)}</option>`
    ).join('');

    if (markSub) markSub.innerHTML = options;
    if (filterSub) filterSub.innerHTML = '<option value="">All Subjects</option>' + options;
    if (marksModalSub) marksModalSub.innerHTML = options;
    if (marksFilterSub) marksFilterSub.innerHTML = '<option value="">All Subjects</option>' + options;
}

function populateStudentDropdown(selectId) {
    const select = document.getElementById(selectId);
    if (!select) return;

    select.innerHTML = '<option value="">All Students</option>' +
        allStudentsList.map(s => `<option value="${s.id}">${escapeHtml(s.studentId)} - ${escapeHtml(s.name)}</option>`).join('');
}

async function loadRollCallList() {
    const tbody = document.getElementById('attendance-rollcall-body');
    if (!tbody) return;

    if (allStudentsList.length === 0) {
        const res = await Api.getStudents();
        if (res.success && res.data) allStudentsList = res.data;
    }

    if (allStudentsList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-3">No students found. Add students first.</td></tr>';
        return;
    }

    tbody.innerHTML = allStudentsList.map(s => `
        <tr data-student-id="${s.id}">
            <td><strong>${escapeHtml(s.studentId)}</strong></td>
            <td>${escapeHtml(s.name)}</td>
            <td>Year ${s.year} - Sec ${escapeHtml(s.section)}</td>
            <td>
                <div class="attendance-toggle">
                    <input type="radio" id="att-p-${s.id}" name="status-${s.id}" value="PRESENT" checked>
                    <label for="att-p-${s.id}">Present</label>
                    <input type="radio" id="att-a-${s.id}" name="status-${s.id}" value="ABSENT">
                    <label for="att-a-${s.id}">Absent</label>
                </div>
            </td>
            <td>
                <input type="text" class="form-control form-control-sm att-remark" placeholder="Optional remark (e.g. sick leave)">
            </td>
        </tr>
    `).join('');
}

function markAllRollCall(status) {
    document.querySelectorAll('#attendance-rollcall-body tr').forEach(row => {
        const sid = row.getAttribute('data-student-id');
        const radio = row.querySelector(`input[name="status-${sid}"][value="${status}"]`);
        if (radio) radio.checked = true;
    });
}

async function submitBatchAttendance() {
    const subjectId = document.getElementById('attendance-mark-subject').value;
    const date = document.getElementById('attendance-mark-date').value;

    if (!subjectId || !date) {
        showToast('error', 'Required Fields', 'Please select both Subject and Attendance Date.');
        return;
    }

    const records = [];
    document.querySelectorAll('#attendance-rollcall-body tr').forEach(row => {
        const studentId = parseInt(row.getAttribute('data-student-id'), 10);
        const checkedRadio = row.querySelector(`input[name="status-${studentId}"]:checked`);
        const status = checkedRadio ? checkedRadio.value : 'PRESENT';
        const remarks = row.querySelector('.att-remark')?.value.trim() || '';

        records.push({
            studentId,
            status,
            remarks: remarks || (status === 'PRESENT' ? 'Present' : 'Absent')
        });
    });

    try {
        const btn = document.getElementById('btn-save-attendance');
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Saving...';

        await Api.markBatchAttendance({
            subjectId: parseInt(subjectId, 10),
            attendanceDate: date,
            records
        });

        showToast('success', 'Attendance Saved', `Recorded attendance for ${records.length} students on ${date}.`);
        filterAttendanceRecords();
    } catch (err) {
        showToast('error', 'Attendance Error', err.message);
    } finally {
        const btn = document.getElementById('btn-save-attendance');
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-check2-circle me-1"></i>Save Attendance';
    }
}

async function filterAttendanceRecords() {
    const studentId = document.getElementById('attendance-filter-student')?.value || '';
    const subjectId = document.getElementById('attendance-filter-subject')?.value || '';
    const startDate = document.getElementById('attendance-filter-start-date')?.value || '';
    const endDate = document.getElementById('attendance-filter-end-date')?.value || '';
    const tbody = document.getElementById('attendance-history-body');

    if (!tbody) return;

    try {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center py-3"><span class="spinner-border spinner-border-sm me-2"></span>Filtering records...</td></tr>';
        const res = await Api.filterAttendance(studentId, subjectId, startDate, endDate);

        if (!res.success || !res.data || res.data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-3">No attendance records match your filter criteria</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.map(a => `
            <tr>
                <td><strong>${escapeHtml(a.studentRoll)}</strong></td>
                <td>${escapeHtml(a.studentName)}</td>
                <td><span class="badge bg-light text-dark border">${escapeHtml(a.subjectCode)}</span> ${escapeHtml(a.subjectName)}</td>
                <td>${a.attendanceDate}</td>
                <td>
                    <span class="badge ${a.status === 'PRESENT' ? 'badge-present' : 'badge-absent'}">
                        ${a.status}
                    </span>
                </td>
                <td>${escapeHtml(a.remarks || '-')}</td>
            </tr>
        `).join('');

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center text-danger py-3">${err.message}</td></tr>`;
    }
}

// -----------------------------------------------------------------
// 4. ACADEMIC PERFORMANCE & MARKS MODULE
// -----------------------------------------------------------------
async function loadMarksView() {
    await loadSubjects();
    populateSubjectDropdowns();
    populateStudentDropdown('mark-student-select');
    populateStudentDropdown('marks-filter-student');
    filterMarksRecords();
}

function openAddMarkModal() {
    document.getElementById('mark-form').reset();
    document.getElementById('mark-form-grade-preview').textContent = '-';
    document.getElementById('mark-form-pct-preview').textContent = '-';
    const modal = new bootstrap.Modal(document.getElementById('markModal'));
    modal.show();
}

function calculateMarkPreview() {
    const obtained = parseFloat(document.getElementById('mark-obtained').value);
    const max = parseFloat(document.getElementById('mark-max').value);
    const gradeSpan = document.getElementById('mark-form-grade-preview');
    const pctSpan = document.getElementById('mark-form-pct-preview');

    if (!isNaN(obtained) && !isNaN(max) && max > 0) {
        if (obtained > max) {
            pctSpan.textContent = 'Obtained cannot exceed max!';
            pctSpan.className = 'text-danger fw-bold';
            gradeSpan.textContent = 'Invalid';
            gradeSpan.className = 'badge bg-danger';
            return;
        }

        const pct = (obtained / max) * 100.0;
        pctSpan.textContent = `${pct.toFixed(1)}%`;
        pctSpan.className = 'text-primary fw-bold';

        let grade = 'F';
        let gradeClass = 'badge-grade-f';

        if (pct >= 90.0) { grade = 'A+'; gradeClass = 'badge-grade-a'; }
        else if (pct >= 80.0) { grade = 'A'; gradeClass = 'badge-grade-a'; }
        else if (pct >= 70.0) { grade = 'B+'; gradeClass = 'badge-grade-b'; }
        else if (pct >= 60.0) { grade = 'B'; gradeClass = 'badge-grade-b'; }
        else if (pct >= 50.0) { grade = 'C'; gradeClass = 'badge-grade-c'; }
        else if (pct >= 40.0) { grade = 'D'; gradeClass = 'badge-grade-d'; }

        gradeSpan.textContent = grade;
        gradeSpan.className = `badge ${gradeClass}`;
    } else {
        gradeSpan.textContent = '-';
        gradeSpan.className = 'badge bg-secondary';
        pctSpan.textContent = '-';
    }
}

async function saveMark(e) {
    e.preventDefault();
    const studentId = document.getElementById('mark-student-select').value;
    const subjectId = document.getElementById('mark-subject-select').value;
    const examType = document.getElementById('mark-exam-type').value;
    const marksObtained = parseFloat(document.getElementById('mark-obtained').value);
    const maxMarks = parseFloat(document.getElementById('mark-max').value);
    const remarks = document.getElementById('mark-remarks').value.trim();

    if (!studentId || !subjectId || !examType || isNaN(marksObtained) || isNaN(maxMarks)) {
        showToast('error', 'Validation Error', 'Please complete all required fields.');
        return;
    }

    if (marksObtained > maxMarks) {
        showToast('error', 'Invalid Marks', 'Marks obtained cannot exceed maximum marks.');
        return;
    }

    try {
        await Api.saveMark({
            studentId: parseInt(studentId, 10),
            subjectId: parseInt(subjectId, 10),
            examType,
            marksObtained,
            maxMarks,
            remarks: remarks || 'Exam evaluation'
        });

        showToast('success', 'Saved', 'Marks recorded and grade calculated automatically.');
        bootstrap.Modal.getInstance(document.getElementById('markModal')).hide();
        filterMarksRecords();
    } catch (err) {
        showToast('error', 'Save Failed', err.message);
    }
}

async function filterMarksRecords() {
    const studentId = document.getElementById('marks-filter-student')?.value || '';
    const subjectId = document.getElementById('marks-filter-subject')?.value || '';
    const examType = document.getElementById('marks-filter-exam')?.value || '';
    const tbody = document.getElementById('marks-table-body');

    if (!tbody) return;

    try {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center py-3"><span class="spinner-border spinner-border-sm me-2"></span>Loading marks...</td></tr>';
        const res = await Api.filterMarks(studentId, subjectId, examType);

        if (!res.success || !res.data || res.data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center text-muted py-3">No exam marks records found</td></tr>';
            return;
        }

        tbody.innerHTML = res.data.map(m => {
            let gradeBadgeClass = 'badge-grade-b';
            if (m.grade.startsWith('A')) gradeBadgeClass = 'badge-grade-a';
            else if (m.grade === 'C') gradeBadgeClass = 'badge-grade-c';
            else if (m.grade === 'D') gradeBadgeClass = 'badge-grade-d';
            else if (m.grade === 'F') gradeBadgeClass = 'badge-grade-f';

            return `
                <tr>
                    <td><strong>${escapeHtml(m.studentRoll)}</strong></td>
                    <td>${escapeHtml(m.studentName)}</td>
                    <td><span class="badge bg-light text-dark border">${escapeHtml(m.subjectCode)}</span> ${escapeHtml(m.subjectName)}</td>
                    <td><span class="badge bg-secondary">${m.examType}</span></td>
                    <td>${m.marksObtained} / ${m.maxMarks}</td>
                    <td><strong>${m.percentage}%</strong></td>
                    <td><span class="badge ${gradeBadgeClass}">${m.grade}</span></td>
                    <td>${escapeHtml(m.remarks || '-')}</td>
                    <td class="text-end">
                        <button class="btn btn-sm btn-outline-danger admin-only" onclick="deleteMarkRecord(${m.id})" title="Delete Record">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        }).join('');

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="9" class="text-center text-danger py-3">${err.message}</td></tr>`;
    }
}

async function deleteMarkRecord(id) {
    if (confirm('Are you sure you want to delete this mark record?')) {
        try {
            await Api.deleteMark(id);
            showToast('success', 'Deleted', 'Mark record removed.');
            filterMarksRecords();
        } catch (err) {
            showToast('error', 'Error', err.message);
        }
    }
}

// -----------------------------------------------------------------
// 5. REPORTS & TRANSCRIPT MODULE
// -----------------------------------------------------------------
async function loadReportsView() {
    populateStudentDropdown('report-student-select');
    const select = document.getElementById('report-student-select');
    if (select && select.options.length > 1 && !select.value) {
        select.selectedIndex = 1;
        generateSelectedReport();
    }
}

async function generateSelectedReport() {
    const studentId = document.getElementById('report-student-select')?.value;
    const container = document.getElementById('report-card-container');

    if (!studentId) {
        container.innerHTML = '<div class="alert alert-info text-center">Please select a student from the dropdown above to view the performance transcript.</div>';
        return;
    }

    try {
        container.innerHTML = '<div class="text-center py-5"><span class="spinner-border spinner-border-lg me-2"></span>Compiling official report card...</div>';
        const res = await Api.getStudentReport(studentId);

        if (!res.success || !res.data) {
            container.innerHTML = '<div class="alert alert-warning text-center">No report data found for this student.</div>';
            return;
        }

        const r = res.data;
        const s = r.student;

        let standingClass = 'text-success';
        if (r.academicStanding.includes('Remedial') || r.academicStanding.includes('Fail')) {
            standingClass = 'text-danger';
        } else if (r.academicStanding.includes('Distinction')) {
            standingClass = 'text-primary';
        }

        let marksRows = '<tr><td colspan="5" class="text-center text-muted">No exam marks entered for this student</td></tr>';
        if (r.marks && r.marks.length > 0) {
            marksRows = r.marks.map(m => `
                <tr>
                    <td><strong>${escapeHtml(m.subjectCode)}</strong></td>
                    <td>${escapeHtml(m.subjectName)}</td>
                    <td>${m.examType}</td>
                    <td>${m.marksObtained} / ${m.maxMarks} (${m.percentage}%)</td>
                    <td><span class="badge ${m.grade.startsWith('A') ? 'badge-grade-a' : (m.grade === 'F' ? 'badge-grade-f' : 'badge-grade-b')}">${m.grade}</span></td>
                </tr>
            `).join('');
        }

        container.innerHTML = `
            <div class="report-paper">
                <div class="report-header">
                    <h2>DEPARTMENT OF COMPUTER SCIENCE & ENGINEERING</h2>
                    <p class="fw-bold">STUDENT ATTENDANCE & ACADEMIC PERFORMANCE REPORT CARD</p>
                    <p class="text-muted small">Generated on: ${r.reportDate} | Academic Year: 2026-2027</p>
                </div>

                <div class="report-meta-grid">
                    <div class="report-meta-item"><strong>Student Name:</strong> ${escapeHtml(s.name)}</div>
                    <div class="report-meta-item"><strong>Roll Number / ID:</strong> ${escapeHtml(s.studentId)}</div>
                    <div class="report-meta-item"><strong>Department:</strong> ${escapeHtml(s.department)}</div>
                    <div class="report-meta-item"><strong>Year & Section:</strong> Year ${s.year}, Section ${escapeHtml(s.section)}</div>
                    <div class="report-meta-item"><strong>Official Email:</strong> ${escapeHtml(s.email)}</div>
                    <div class="report-meta-item"><strong>Academic Status:</strong> <span class="fw-bold ${standingClass}">${r.academicStanding}</span></div>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <div class="card h-100 border ${r.attendancePercentage < 75 ? 'border-danger bg-danger-subtle' : 'border-success bg-success-subtle'}">
                            <div class="card-body">
                                <h6 class="fw-bold mb-2"><i class="bi bi-calendar-check me-2"></i>Attendance Summary</h6>
                                <p class="mb-1"><strong>Classes Attended:</strong> ${r.classesAttended} / ${r.totalClasses}</p>
                                <p class="mb-1"><strong>Attendance Rate:</strong> <span class="fs-5 fw-bold ${r.attendancePercentage < 75 ? 'text-danger' : 'text-success'}">${r.attendancePercentage}%</span></p>
                                <p class="mb-0 small ${r.attendancePercentage < 75 ? 'text-danger fw-bold' : 'text-muted'}">${r.attendanceStatus}</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card h-100 border border-primary bg-primary-subtle">
                            <div class="card-body">
                                <h6 class="fw-bold mb-2"><i class="bi bi-mortarboard me-2"></i>Academic Summary</h6>
                                <p class="mb-1"><strong>Overall Percentage:</strong> <span class="fs-5 fw-bold text-primary">${r.overallPercentage}%</span></p>
                                <p class="mb-1"><strong>Cumulative Grade:</strong> <span class="badge ${r.overallGrade.startsWith('A') ? 'badge-grade-a' : 'badge-grade-b'} fs-6">${r.overallGrade}</span></p>
                                <p class="mb-0 small text-muted">Evaluation based on all completed semester and midterm examinations.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <h6 class="fw-bold mb-3"><i class="bi bi-card-checklist me-2"></i>Subject-wise Performance Breakdown</h6>
                <div class="table-responsive mb-4">
                    <table class="table table-bordered table-sm align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th>Subject Code</th>
                                <th class="text-start">Subject Title</th>
                                <th>Exam Type</th>
                                <th>Score (Obtained / Max)</th>
                                <th>Grade</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${marksRows}
                        </tbody>
                    </table>
                </div>

                <div class="report-signatures">
                    <div class="signature-line">Class Teacher / Advisor</div>
                    <div class="signature-line">Head of Department</div>
                    <div class="signature-line">Controller of Examinations</div>
                </div>
            </div>
        `;

    } catch (err) {
        container.innerHTML = `<div class="alert alert-danger text-center">${err.message}</div>`;
    }
}

function viewStudentReport(studentId) {
    navigateTo('reports');
    setTimeout(() => {
        const select = document.getElementById('report-student-select');
        if (select) {
            select.value = studentId;
            generateSelectedReport();
        }
    }, 150);
}

function printReportCard() {
    window.print();
}

// -----------------------------------------------------------------
// Helpers & Toasts
// -----------------------------------------------------------------
function showToast(type, title, message) {
    const toastEl = document.getElementById('appToast');
    if (!toastEl) return;

    const header = document.getElementById('toast-title');
    const body = document.getElementById('toast-body');

    header.textContent = title;
    body.textContent = message;

    toastEl.className = 'toast align-items-center text-white border-0';
    if (type === 'success') toastEl.classList.add('bg-success');
    else if (type === 'error') toastEl.classList.add('bg-danger');
    else if (type === 'warning') toastEl.classList.add('bg-warning');
    else toastEl.classList.add('bg-primary');

    const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
    toast.show();
}

function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
