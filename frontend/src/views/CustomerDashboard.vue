<template>
  <div class="container">
    <div class="flex justify-between items-center mb-xl">
      <div>
        <h1 class="text-3xl font-bold mb-xs">Welcome back, {{ customerName }}</h1>
        <p class="text-muted">Manage your accounts and transactions</p>
      </div>
      <button @click="handleLogout" class="btn btn-secondary">Logout</button>
    </div>

    <!-- KYC Warning -->
    <div v-if="kycStatus !== 'APPROVED'" class="alert alert-warning mb-lg">
      <div class="flex items-center gap-md">
        <span class="text-2xl">⚠️</span>
        <div>
          <h3 class="font-bold">KYC Verification Pending</h3>
          <p>Your account features are restricted until your KYC is approved. You cannot make deposits, withdrawals, transfers or apply for loans.</p>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="glass-card mb-xl">
      <h2 class="text-xl font-bold mb-md">Quick Actions</h2>
      <div class="flex gap-md flex-wrap">
        <button @click="openModal('deposit')" class="btn btn-primary" :disabled="kycStatus !== 'APPROVED'">
          Deposit
        </button>
        <button @click="openModal('withdraw')" class="btn btn-primary" :disabled="kycStatus !== 'APPROVED'">
          Withdraw
        </button>
        <button @click="openModal('transfer')" class="btn btn-primary" :disabled="kycStatus !== 'APPROVED'">
          Transfer
        </button>
        <button @click="openCreateAccountModal" class="btn btn-secondary">
          Open New Account
        </button>
        <button @click="openLoanModal" class="btn btn-secondary" :disabled="kycStatus !== 'APPROVED'">
          Request Loan
        </button>
      </div>
    </div>

    <!-- Accounts Grid -->
    <div class="dashboard-grid">
      <div v-for="account in accounts" :key="account.accountNo" class="glass-card">
        <div class="flex justify-between items-start mb-md">
          <div>
            <p class="text-muted text-sm uppercase">Account Number</p>
            <p class="font-mono font-bold">{{ account.accountNo }}</p>
          </div>
          <span :class="['badge', account.status === 'ACTIVE' ? 'badge-success' : 'badge-error']">
            {{ account.status }}
          </span>
        </div>
        <div class="mb-lg">
          <p class="text-muted text-sm uppercase">Available Balance</p>
          <h3 class="text-3xl font-bold">₹{{ formatCurrency(account.balance) }}</h3>
        </div>
        <div class="flex justify-between items-center text-sm text-muted">
          <span>{{ account.accountType }}</span>
          <button @click="viewHistory(account.accountNo)" class="text-primary hover:underline">
            View History
          </button>
        </div>
      </div>
    </div>

    <!-- Loans Section -->
    <div v-if="loans.length > 0" class="glass-card mb-xl">
      <h2 class="text-xl font-bold mb-md">My Loans</h2>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>Loan ID</th>
              <th>Amount</th>
              <th>Purpose</th>
              <th>Status</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="loan in loans" :key="loan.loanId">
              <td class="font-mono">{{ loan.loanId.substring(0, 8) }}...</td>
              <td class="font-bold">₹{{ formatCurrency(loan.amount) }}</td>
              <td>{{ loan.purpose }}</td>
              <td>
                <span :class="['badge', getStatusBadgeClass(loan.status)]">
                  {{ loan.status }}
                </span>
              </td>
              <td>{{ formatDate(loan.appliedAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Transaction History -->
    <div v-if="selectedAccount" class="glass-card">
      <div class="flex justify-between items-center mb-md">
        <h2 class="text-xl font-bold">Transaction History</h2>
        <button @click="selectedAccount = null" class="btn btn-secondary text-sm">Close</button>
      </div>
      
      <div v-if="transactions.length === 0" class="text-center py-lg text-muted">
        No transactions found for this account.
      </div>

      <div v-else class="table-container">
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Type</th>
              <th>Description</th>
              <th>Amount</th>
              <th>Balance</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="txn in transactions" :key="txn.txnId">
              <td>{{ formatDate(txn.txnTimestamp) }}</td>
              <td>
                <span :class="['badge', getTxnBadgeClass(txn.txnType)]">
                  {{ txn.txnType }}
                </span>
              </td>
              <td>{{ txn.description || '-' }}</td>
              <td :class="isCredit(txn.txnType) ? 'text-success' : 'text-error'">
                {{ isCredit(txn.txnType) ? '+' : '-' }}₹{{ formatCurrency(txn.amount) }}
              </td>
              <td class="font-mono">₹{{ formatCurrency(txn.balanceAfter) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modals -->
    <TransactionModal 
      v-if="showTransactionModal" 
      :type="transactionType" 
      :account="selectedAccountForAction"
      @close="closeTransactionModal"
      @success="handleTransactionSuccess"
    />

    <CreateAccountModal
      v-if="showCreateAccountModal"
      @close="showCreateAccountModal = false"
      @success="handleAccountCreated"
    />

    <!-- Loan Modal -->
    <div v-if="showLoanModal" class="modal-overlay" @click.self="showLoanModal = false">
      <div class="modal-content glass-card fade-in">
        <div class="modal-header">
          <h2 class="text-xl font-bold">Request Loan</h2>
          <button @click="showLoanModal = false" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="submitLoanRequest">
          <div class="input-group">
            <label>Amount (₹)</label>
            <input v-model.number="loanForm.amount" type="number" min="1000" required placeholder="Enter amount">
          </div>
          <div class="input-group">
            <label>Purpose</label>
            <textarea v-model="loanForm.purpose" required placeholder="Why do you need this loan?" rows="3"></textarea>
          </div>
          <button type="submit" class="btn btn-primary w-full" :disabled="loanLoading">
            {{ loanLoading ? 'Submitting...' : 'Submit Request' }}
          </button>
        </form>
      </div>
    </div>

  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useCustomerStore } from '../stores/customer';
import { useAuthStore } from '../stores/auth';
import { useToast } from '../composables/useToast';
import TransactionModal from '../components/TransactionModal.vue';
import CreateAccountModal from '../components/CreateAccountModal.vue';
import api from '../api';

export default {
  name: 'CustomerDashboard',
  components: {
    TransactionModal,
    CreateAccountModal
  },
  setup() {
    const router = useRouter();
    const customerStore = useCustomerStore();
    const authStore = useAuthStore();
    const { success, error: showError } = useToast();

    const transactions = ref([]);
    const selectedAccount = ref(null);
    const showTransactionModal = ref(false);
    const showCreateAccountModal = ref(false);
    const transactionType = ref('deposit');
    const selectedAccountForAction = ref(null);
    
    const loans = ref([]);
    const showLoanModal = ref(false);
    const loanForm = ref({ amount: '', purpose: '' });
    const loanLoading = ref(false);

    const customerName = computed(() => customerStore.profile?.name || 'Customer');
    const accounts = computed(() => customerStore.accounts);
    const kycStatus = computed(() => customerStore.profile?.kycStatus);

    onMounted(async () => {
      try {
        console.log('CustomerDashboard mounted, user:', authStore.user);
        await customerStore.fetchProfile(authStore.user.customerId);
        await customerStore.fetchAccounts(authStore.user.customerId);
        console.log('Accounts loaded:', customerStore.accounts);
        fetchLoans();
      } catch (error) {
        console.error('Error loading dashboard data:', error);
        showError('Failed to load dashboard data. Please try refreshing the page.');
      }
    });

    const fetchLoans = async () => {
      try {
        const response = await api.getCustomerLoans(authStore.user.customerId);
        loans.value = response.data;
      } catch (error) {
        console.error('Failed to fetch loans', error);
      }
    };

    const handleLogout = () => {
      authStore.logout();
      router.push('/');
    };

    const formatCurrency = (value) => {
      return new Intl.NumberFormat('en-IN').format(value);
    };

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleString();
    };

    const viewHistory = async (accountNo) => {
      selectedAccount.value = accountNo;
      try {
        const response = await api.getTransactionHistory(accountNo);
        transactions.value = response.data;
      } catch (error) {
        console.error('Failed to fetch history', error);
      }
    };

    const openModal = (type) => {
      if (accounts.value.length === 0) {
        showError('Please create an account first.');
        return;
      }
      selectedAccountForAction.value = accounts.value[0]; 
      transactionType.value = type;
      showTransactionModal.value = true;
    };

    const closeTransactionModal = () => {
      showTransactionModal.value = false;
      selectedAccountForAction.value = null;
    };

    const handleTransactionSuccess = async () => {
      closeTransactionModal();
      await customerStore.fetchAccounts(authStore.user.customerId);
      if (selectedAccount.value) {
        viewHistory(selectedAccount.value);
      }
    };

    const openCreateAccountModal = () => {
      showCreateAccountModal.value = true;
    };

    const handleAccountCreated = async () => {
      showCreateAccountModal.value = false;
      await customerStore.fetchAccounts(authStore.user.customerId);
    };

    const openLoanModal = () => {
      showLoanModal.value = true;
    };

    const submitLoanRequest = async () => {
      loanLoading.value = true;
      try {
        await api.applyLoan({
          customerId: authStore.user.customerId,
          amount: loanForm.value.amount,
          purpose: loanForm.value.purpose
        });
        showLoanModal.value = false;
        loanForm.value = { amount: '', purpose: '' };
        fetchLoans();
        success('Loan request submitted successfully! Check your email for confirmation.');
      } catch (error) {
        showError(error.response?.data?.message || 'Failed to submit loan request');
      } finally {
        loanLoading.value = false;
      }
    };
    const getStatusBadgeClass = (status) => {
      const map = {
        'APPROVED': 'badge-success',
        'PENDING': 'badge-warning',
        'REJECTED': 'badge-error'
      };
      return map[status] || 'badge-info';
    };

    const getTxnBadgeClass = (type) => {
      if (type.includes('DEPOSIT') || type.includes('IN')) return 'badge-success';
      if (type.includes('WITHDRAW') || type.includes('OUT')) return 'badge-error';
      return 'badge-info';
    };

    const isCredit = (type) => {
      return type === 'DEPOSIT' || type === 'TRANSFER_IN';
    };

    return {
      customerName,
      accounts,
      kycStatus,
      transactions,
      selectedAccount,
      showTransactionModal,
      showCreateAccountModal,
      transactionType,
      selectedAccountForAction,
      loans,
      showLoanModal,
      loanForm,
      loanLoading,
      handleLogout,
      formatCurrency,
      formatDate,
      viewHistory,
      openModal,
      closeTransactionModal,
      handleTransactionSuccess,
      openCreateAccountModal,
      handleAccountCreated,
      openLoanModal,
      submitLoanRequest,
      getStatusBadgeClass,
      getTxnBadgeClass,
      isCredit
    };
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 1rem;
  width: 100%;
  max-width: 500px;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}
.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}
textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-input);
}
</style>
