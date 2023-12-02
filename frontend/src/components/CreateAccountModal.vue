<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content glass-card fade-in">
      <div class="modal-header">
        <h2 class="text-xl font-semibold">Create New Account</h2>
        <button @click="$emit('close')" class="close-btn">&times;</button>
      </div>

      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div v-if="success" class="alert alert-success">
        Account created successfully!
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="input-group">
          <label>Account Type</label>
          <select v-model="accountType" required>
            <option value="">Select account type</option>
            <option value="SAVINGS">Savings Account</option>
            <option value="CURRENT">Current Account</option>
          </select>
        </div>

        <div class="alert alert-info">
          <p class="text-sm">
            <strong>Note:</strong> Your new account will be created with a zero balance. 
            You can deposit money after creation.
          </p>
        </div>

        <div class="flex gap-md">
          <button type="submit" class="btn btn-primary" style="flex: 1;" :disabled="loading">
            {{ loading ? 'Creating...' : 'Create Account' }}
          </button>
          <button type="button" @click="$emit('close')" class="btn btn-secondary" style="flex: 1;">
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useCustomerStore } from '../stores/customer';

export default {
  name: 'CreateAccountModal',
  emits: ['close', 'success'],
  setup(props, { emit }) {
    const authStore = useAuthStore();
    const customerStore = useCustomerStore();

    const accountType = ref('');
    const error = ref('');
    const success = ref(false);
    const loading = ref(false);

    const handleSubmit = async () => {
      error.value = '';
      loading.value = true;

      try {
        await customerStore.createAccount(authStore.user.customerId, accountType.value);
        success.value = true;
        
        setTimeout(() => {
          emit('success');
        }, 1500);
      } catch (err) {
        error.value = err.response?.data?.message || 'Failed to create account. Please try again.';
      } finally {
        loading.value = false;
      }
    };

    return {
      accountType,
      error,
      success,
      loading,
      handleSubmit
    };
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-lg);
}

.modal-content {
  max-width: 500px;
  width: 100%;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 2rem;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}
</style>
