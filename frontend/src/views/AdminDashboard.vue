<template>
  <div class="container">
    <div class="flex justify-between items-center mb-xl">
      <h1 class="text-3xl font-bold">Admin Dashboard</h1>
      <button @click="handleLogout" class="btn btn-secondary">Logout</button>
    </div>

    <!-- Stats Grid -->
    <div class="dashboard-grid">
      <div class="glass-card">
        <p class="text-muted uppercase text-sm">Total Customers</p>
        <h3 class="text-3xl font-bold">{{ stats.totalCustomers || 0 }}</h3>
      </div>
      <div class="glass-card">
        <p class="text-muted uppercase text-sm">Total Transactions</p>
        <h3 class="text-3xl font-bold">{{ stats.totalTransactions || 0 }}</h3>
      </div>
      <div class="glass-card">
        <p class="text-muted uppercase text-sm">Pending KYC</p>
        <h3 class="text-3xl font-bold text-warning">{{ stats.pendingKYC || 0 }}</h3>
      </div>
    </div>

    <!-- Loan Requests -->
    <div class="glass-card mb-xl">
      <h2 class="text-xl font-bold mb-md">Loan Requests</h2>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>Loan ID</th>
              <th>Customer ID</th>
              <th>Amount</th>
              <th>Purpose</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="loan in pendingLoans" :key="loan.loanId">
              <td class="font-mono">{{ loan.loanId.substring(0, 8) }}...</td>
              <td class="font-mono">{{ loan.customerId.substring(0, 8) }}...</td>
              <td class="font-bold">₹{{ formatCurrency(loan.amount) }}</td>
              <td>{{ loan.purpose }}</td>
              <td><span class="badge badge-warning">PENDING</span></td>
              <td>
                <div class="flex gap-sm">
                  <button @click="updateLoan(loan.loanId, 'APPROVED')" class="btn btn-success" style="padding: 0.25rem 0.5rem; font-size: 0.8rem;">Approve</button>
                  <button @click="updateLoan(loan.loanId, 'REJECTED')" class="btn btn-danger" style="padding: 0.25rem 0.5rem; font-size: 0.8rem;">Reject</button>
                </div>
              </td>
            </tr>
            <tr v-if="pendingLoans.length === 0">
              <td colspan="6" class="text-center text-muted">No pending loan requests</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Customer Management -->
    <div class="glass-card mb-xl">
      <h2 class="text-xl font-bold mb-md">Customer Management</h2>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>KYC Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="customer in customers" :key="customer.customerId">
              <td>{{ customer.name }}</td>
              <td>{{ customer.email }}</td>
              <td>{{ customer.phone }}</td>
              <td>
                <span :class="['badge', getKycBadgeClass(customer.kycStatus)]">
                  {{ customer.kycStatus }}
                </span>
              </td>
              <td>
                <div v-if="customer.kycStatus === 'PENDING'" class="flex gap-sm">
                  <button @click="approveKYC(customer.customerId, 'APPROVED')" class="btn btn-success" style="padding: 0.25rem 0.5rem; font-size: 0.8rem;">
                    Approve
                  </button>
                  <button @click="approveKYC(customer.customerId, 'REJECTED')" class="btn btn-danger" style="padding: 0.25rem 0.5rem; font-size: 0.8rem;">
                    Reject
                  </button>
                </div>
                <span v-else class="text-muted text-sm">No actions</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Recent Transactions -->
    <div class="glass-card">
      <h2 class="text-xl font-bold mb-md">Recent Transactions</h2>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>Time</th>
              <th>Type</th>
              <th>Account</th>
              <th>Amount</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="txn in recentTransactions" :key="txn.txnId">
              <td>{{ formatDate(txn.txnTimestamp) }}</td>
              <td>
                <span :class="['badge', getTxnBadgeClass(txn.txnType)]">
                  {{ txn.txnType }}
                </span>
              </td>
              <td class="font-mono">{{ txn.accountNo }}</td>
              <td class="font-bold">₹{{ formatCurrency(txn.amount) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { useToast } from '../composables/useToast';
import api from '../api';

export default {
  name: 'AdminDashboard',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const { success, error } = useToast();
    
    const stats = ref({});
    const customers = ref([]);
    const transactions = ref([]);
    const loans = ref([]);

    const recentTransactions = computed(() => {
      return [...transactions.value]
        .sort((a, b) => new Date(b.txnTimestamp) - new Date(a.txnTimestamp))
        .slice(0, 10);
    });

    const pendingLoans = computed(() => {
      return loans.value.filter(l => l.status === 'PENDING');
    });

    onMounted(() => {
      fetchData();
    });

    const fetchData = async () => {
      try {
        const [statsRes, customersRes, txnsRes, loansRes] = await Promise.all([
          api.getDashboardStats(),
          api.getAllCustomers(),
          api.getAllTransactions(),
          api.getAllLoans()
        ]);
        
        stats.value = statsRes.data;
        customers.value = customersRes.data;
        transactions.value = txnsRes.data;
        loans.value = loansRes.data;
      } catch (err) {
        console.error('Failed to fetch admin data', err);
        error('Failed to load dashboard data');
      }
    };

    const handleLogout = () => {
      authStore.logout();
      router.push('/');
    };

    const approveKYC = async (customerId, status) => {
      try {
        await api.approveKYC(customerId, status);
        await fetchData(); 
        success(`KYC ${status.toLowerCase()} successfully`);
      } catch (err) {
        error('Failed to update KYC status');
      }
    };

    const updateLoan = async (loanId, status) => {
      try {
        await api.updateLoanStatus(loanId, status);
        await fetchData();
        success(`Loan ${status.toLowerCase()} successfully`);
      } catch (err) {
        error('Failed to update loan status');
      }
    };

    const formatCurrency = (value) => new Intl.NumberFormat('en-IN').format(value);
    const formatDate = (date) => new Date(date).toLocaleString();

    const getKycBadgeClass = (status) => {
      const map = { 'APPROVED': 'badge-success', 'PENDING': 'badge-warning', 'REJECTED': 'badge-error' };
      return map[status] || 'badge-info';
    };

    const getTxnBadgeClass = (type) => {
      if (type.includes('DEPOSIT') || type.includes('IN')) return 'badge-success';
      if (type.includes('WITHDRAW') || type.includes('OUT')) return 'badge-error';
      return 'badge-info';
    };

    return {
      stats,
      customers,
      recentTransactions,
      loans,
      pendingLoans,
      handleLogout,
      approveKYC,
      updateLoan,
      formatCurrency,
      formatDate,
      getKycBadgeClass,
      getTxnBadgeClass
    };
  }
};
</script>
