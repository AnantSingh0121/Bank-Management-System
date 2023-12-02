import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    }
});
api.interceptors.request.use(
    (config) => {
        const token = sessionStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            sessionStorage.clear();
            window.location.href = '/';
        }
        return Promise.reject(error);
    }
);

const endpoints = {
    register(data) {
        return api.post('/auth/register', data);
    },
    login(data) {
        return api.post('/auth/login', data);
    },

    getProfile(customerId) {
        return api.get(`/customer/profile?customerId=${customerId}`);
    },
    updateProfile(customerId, data) {
        return api.put(`/customer/profile?customerId=${customerId}`, data);
    },

    createAccount(customerId, data) {
        return api.post(`/account/create?customerId=${customerId}`, data);
    },
    getAccount(accountNo) {
        return api.get(`/account/${accountNo}`);
    },
    getBalance(accountNo) {
        return api.get(`/account/balance/${accountNo}`);
    },
    getCustomerAccounts(customerId) {
        return api.get(`/account/customer/${customerId}`);
    },

    deposit(data) {
        return api.post('/transaction/deposit', data);
    },
    withdraw(data) {
        return api.post('/transaction/withdraw', data);
    },
    transfer(data) {
        return api.post('/transaction/transfer', data);
    },
    getTransactionHistory(accountNo, limit = 50) {
        return api.get(`/transaction/history/${accountNo}?limit=${limit}`);
    },

    applyLoan(data) {
        return api.post('/loan/apply', data);
    },
    getCustomerLoans(customerId) {
        return api.get(`/loan/customer/${customerId}`);
    },
    getAllLoans() {
        return api.get('/loan/all');
    },
    updateLoanStatus(loanId, status) {
        return api.put(`/loan/${loanId}/status?status=${status}`);
    },

    getAllCustomers() {
        return api.get('/admin/customers');
    },
    approveKYC(customerId, status) {
        return api.put(`/admin/kyc/approve/${customerId}?status=${status}`);
    },
    getAllTransactions() {
        return api.get('/admin/transactions/all');
    },
    getDashboardStats() {
        return api.get('/admin/dashboard/stats');
    }
};

export default endpoints;
