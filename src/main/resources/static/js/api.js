/**
 * API Client Module
 * Handles REST API communication, JWT authentication, and token management.
 */

// Auto-detect backend URL: uses relative '/api' on port 8080, or 'http://localhost:8080/api' if opened via Live Server or file
const API_BASE = (window.location.protocol === 'file:' || (window.location.port && window.location.port !== '8080'))
    ? 'http://localhost:8080/api'
    : '/api';

const Api = {
    // -------------------------------------------------------------
    // Token & Auth Storage
    // -------------------------------------------------------------
    getToken() {
        return localStorage.getItem('token');
    },

    setToken(token) {
        localStorage.setItem('token', token);
    },

    getUser() {
        const u = localStorage.getItem('user');
        return u ? JSON.parse(u) : null;
    },

    setUser(user) {
        localStorage.setItem('user', JSON.stringify(user));
    },

    clearAuth() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    isAuthenticated() {
        return !!this.getToken();
    },

    // -------------------------------------------------------------
    // Core Fetch Wrapper
    // -------------------------------------------------------------
    async request(endpoint, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(`${API_BASE}${endpoint}`, config);

            // Handle unauthorized session
            if (response.status === 401 && !endpoint.includes('/auth/login')) {
                this.clearAuth();
                window.dispatchEvent(new CustomEvent('auth:expired'));
                throw new Error('Your session has expired. Please log in again.');
            }

            const data = await response.json();

            if (!response.ok) {
                const message = (data && data.message) ? data.message : `Request failed with status ${response.status}`;
                const err = new Error(message);
                err.details = (data && data.details) ? data.details : [];
                throw err;
            }

            return data;
        } catch (error) {
            console.error(`API Error on ${endpoint}:`, error);
            throw error;
        }
    },

    // -------------------------------------------------------------
    // Authentication Endpoints
    // -------------------------------------------------------------
    async login(username, password) {
        const res = await this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        if (res.success && res.data) {
            this.setToken(res.data.token);
            this.setUser({
                username: res.data.username,
                fullName: res.data.fullName,
                email: res.data.email,
                role: res.data.role
            });
        }
        return res;
    },

    async getCurrentUser() {
        return await this.request('/auth/me');
    },

    logout() {
        this.clearAuth();
        window.dispatchEvent(new CustomEvent('auth:logout'));
    },

    // -------------------------------------------------------------
    // Dashboard Endpoints
    // -------------------------------------------------------------
    async getDashboardStats() {
        return await this.request('/dashboard/stats');
    },

    // -------------------------------------------------------------
    // Students Endpoints
    // -------------------------------------------------------------
    async getStudents(keyword = '', department = '') {
        const params = new URLSearchParams();
        if (keyword) params.append('keyword', keyword);
        if (department) params.append('department', department);
        const query = params.toString() ? `?${params.toString()}` : '';
        return await this.request(`/students${query}`);
    },

    async getStudent(id) {
        return await this.request(`/students/${id}`);
    },

    async getDepartments() {
        return await this.request('/students/departments');
    },

    async createStudent(studentData) {
        return await this.request('/students', {
            method: 'POST',
            body: JSON.stringify(studentData)
        });
    },

    async updateStudent(id, studentData) {
        return await this.request(`/students/${id}`, {
            method: 'PUT',
            body: JSON.stringify(studentData)
        });
    },

    async deleteStudent(id) {
        return await this.request(`/students/${id}`, {
            method: 'DELETE'
        });
    },

    // -------------------------------------------------------------
    // Subjects Endpoints
    // -------------------------------------------------------------
    async getSubjects(department = '') {
        const query = department ? `?department=${encodeURIComponent(department)}` : '';
        return await this.request(`/subjects${query}`);
    },

    async createSubject(subjectData) {
        return await this.request('/subjects', {
            method: 'POST',
            body: JSON.stringify(subjectData)
        });
    },

    // -------------------------------------------------------------
    // Attendance Endpoints
    // -------------------------------------------------------------
    async markBatchAttendance(batchData) {
        return await this.request('/attendance/mark-batch', {
            method: 'POST',
            body: JSON.stringify(batchData)
        });
    },

    async getAttendanceByStudent(studentId) {
        return await this.request(`/attendance/by-student/${studentId}`);
    },

    async filterAttendance(studentId, subjectId, startDate, endDate) {
        const params = new URLSearchParams();
        if (studentId) params.append('studentId', studentId);
        if (subjectId) params.append('subjectId', subjectId);
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);
        const query = params.toString() ? `?${params.toString()}` : '';
        return await this.request(`/attendance/filter${query}`);
    },

    // -------------------------------------------------------------
    // Marks Endpoints
    // -------------------------------------------------------------
    async saveMark(markData) {
        return await this.request('/marks', {
            method: 'POST',
            body: JSON.stringify(markData)
        });
    },

    async getMarksByStudent(studentId) {
        return await this.request(`/marks/by-student/${studentId}`);
    },

    async filterMarks(studentId, subjectId, examType) {
        const params = new URLSearchParams();
        if (studentId) params.append('studentId', studentId);
        if (subjectId) params.append('subjectId', subjectId);
        if (examType) params.append('examType', examType);
        const query = params.toString() ? `?${params.toString()}` : '';
        return await this.request(`/marks/filter${query}`);
    },

    async deleteMark(id) {
        return await this.request(`/marks/${id}`, {
            method: 'DELETE'
        });
    },

    // -------------------------------------------------------------
    // Reports Endpoints
    // -------------------------------------------------------------
    async getStudentReport(studentId) {
        return await this.request(`/reports/student/${studentId}`);
    },

    async getBatchReports(department = '') {
        const query = department ? `?department=${encodeURIComponent(department)}` : '';
        return await this.request(`/reports/batch${query}`);
    }
};

window.Api = Api;
