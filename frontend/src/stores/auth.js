import { defineStore } from 'pinia';
import api from '../api';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: sessionStorage.getItem('token') || null,
        user: JSON.parse(sessionStorage.getItem('user') || 'null'),
        isAuthenticated: !!sessionStorage.getItem('token')
    }),

    actions: {
        async register(userData) {
            try {
                const response = await api.register(userData);
                this.setAuth(response.data);
                return response.data;
            } catch (error) {
                throw error.response?.data?.message || 'Registration failed';
            }
        },

        async login(credentials) {
            try {
                const response = await api.login(credentials);
                this.setAuth(response.data);
                return response.data;
            } catch (error) {
                throw error.response?.data?.message || 'Login failed';
            }
        },

        setAuth(data) {
            this.token = data.token;
            this.user = {
                username: data.username,
                role: data.role,
                customerId: data.customerId
            };
            this.isAuthenticated = true;

            sessionStorage.setItem('token', data.token);
            sessionStorage.setItem('user', JSON.stringify(this.user));
        },

        logout() {
            this.token = null;
            this.user = null;
            this.isAuthenticated = false;
            sessionStorage.clear();
        }
    }
});
