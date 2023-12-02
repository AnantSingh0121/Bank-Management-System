<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content glass-card fade-in">
      <div class="modal-header">
        <h2 class="text-xl font-semibold">{{ title }}</h2>
        <button @click="$emit('close')" class="close-btn">&times;</button>
      </div>

      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div v-if="success" class="alert alert-success">
        {{ successMessage }}
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="input-group">
          <label>Account Number</label>
          <input 
            type="text" 
            :value="account.accountNo" 
            disabled
            style="opacity: 0.6;"
          />
        </div>

        <div class="input-group">
          <label>Amount (₹)</label>
          <input 
            v-model.number="formData.amount" 
            type="number" 
            step="0.01"
            min="1"
            placeholder="Enter amount"
            required
          />
        </div>

        <div v-if="type === 'transfer'" class="input-group">
          <label>To Account Number</label>
          <input 
            v-model="formData.toAccount" 
            type="text" 
            placeholder="Enter destination account number"
            required
          />
        </div>

        <div class="input-group">
          <label>Description (Optional)</label>
          <input 
            v-model="formData.description" 
            type="text" 
            placeholder="Add a note"
          />
        </div>

        <div class="flex gap-md">
          <button type="submit" class="btn btn-primary" style="flex: 1;" :disabled="loading">
            {{ loading ? 'Processing...' : submitText }}
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
import { ref, computed } from 'vue';
import api from '../api';

export default {
  name: 'TransactionModal',
  props: {
    type: {
      type: String,
      required: true,
      validator: (value) => ['deposit', 'withdraw', 'transfer'].includes(value)
    },
    account: {
      type: Object,
      required: true
    }
  },
  emits: ['close', 'success'],
  setup(props, { emit }) {
    const formData = ref({
      amount: '',
      toAccount: '',
      description: ''
    });

    const error = ref('');
    const success = ref(false);
    const loading = ref(false);

    const title = computed(() => {
      const titles = {
        deposit: 'Deposit Money',
        withdraw: 'Withdraw Money',
        transfer: 'Transfer Money'
      };
      return titles[props.type];
    });

    const submitText = computed(() => {
      const texts = {
        deposit: 'Deposit',
        withdraw: 'Withdraw',
        transfer: 'Transfer'
      };
      return texts[props.type];
    });

    const successMessage = computed(() => {
      return `${props.type.charAt(0).toUpperCase() + props.type.slice(1)} successful!`;
    });

    const handleSubmit = async () => {
      error.value = '';
      loading.value = true;

      try {
        const payload = {
          accountNo: props.account.accountNo,
          amount: formData.value.amount,
          description: formData.value.description
        };

        if (props.type === 'transfer') {
          payload.toAccount = formData.value.toAccount;
        }

        if (props.type === 'deposit') {
          await api.deposit(payload);
        } else if (props.type === 'withdraw') {
          await api.withdraw(payload);
        } else if (props.type === 'transfer') {
          await api.transfer(payload);
        }

        success.value = true;
        
        setTimeout(() => {
          emit('success');
        }, 1500);
      } catch (err) {
        error.value = err.response?.data?.message || `${props.type} failed. Please try again.`;
      } finally {
        loading.value = false;
      }
    };

    return {
      formData,
      error,
      success,
      loading,
      title,
      submitText,
      successMessage,
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
  max-height: 90vh;
  overflow-y: auto;
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
