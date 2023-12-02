import { defineStore } from 'pinia';
import api from '../api';

export const useCustomerStore = defineStore('customer', {
    state: () => ({
        profile: null,
        accounts: [],
        selectedAccount: null,
        transactions: []
    }),

    actions: {
        async fetchProfile(customerId) {
            try {
                const response = await api.getProfile(customerId);
                this.profile = response.data;
            } catch (error) {
                throw error;
            }
        },

        async updateProfile(customerId, data) {
            try {
                const response = await api.updateProfile(customerId, data);
                this.profile = response.data;
            } catch (error) {
                throw error;
            }
        },

        async fetchAccounts(customerId) {
            try {
                console.log('Fetching accounts for customer:', customerId);
                const response = await api.getCustomerAccounts(customerId);
                console.log('Accounts response:', response.data);
                this.accounts = response.data;
                if (this.accounts.length > 0 && !this.selectedAccount) {
                    this.selectedAccount = this.accounts[0];
                }
            } catch (error) {
                console.error('Error fetching accounts:', error);
                throw error;
            }
        },

        async createAccount(customerId, accountType) {
            try {
                const response = await api.createAccount(customerId, { accountType });
                this.accounts.push(response.data);
                return response.data;
            } catch (error) {
                throw error;
            }
        },

        async fetchTransactions(accountNo) {
            try {
                const response = await api.getTransactionHistory(accountNo);
                this.transactions = response.data;
            } catch (error) {
                throw error;
            }
        },

        selectAccount(account) {
            this.selectedAccount = account;
        }
    }
});
