<template>
  <div class="auth-container">
    <div class="auth-card glass-card fade-in">
      <div class="text-center mb-xl">
        <h1 class="text-3xl font-bold mb-sm">Create Account</h1>
        <p class="text-gray-300">Join our secure banking platform</p>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="input-group">
          <label>Full Name</label>
          <input 
            v-model="formData.name" 
            type="text" 
            placeholder="Enter your full name"
            required
          />
        </div>

        <div class="input-group">
          <label>Email</label>
          <input 
            v-model="formData.email" 
            type="email" 
            placeholder="Enter your email"
            required
          />
        </div>

        <div class="input-group">
          <label>Phone</label>
          <input 
            v-model="formData.phone" 
            type="tel" 
            placeholder="Enter your phone number"
            required
          />
        </div>

        <div class="input-group">
          <label>Address</label>
          <textarea 
            v-model="formData.address" 
            placeholder="Enter your address"
            rows="2"
            required
          ></textarea>
        </div>

        <div class="input-group">
          <label>Username</label>
          <input 
            v-model="formData.username" 
            type="text" 
            placeholder="Choose a username"
            required
          />
        </div>

        <div class="input-group">
          <label>Password</label>
          <input 
            v-model="formData.password" 
            type="password" 
            placeholder="Choose a strong password"
            required
          />
        </div>

        <button type="submit" class="btn btn-primary" style="width: 100%;" :disabled="loading">
          {{ loading ? 'Creating Account...' : 'Register' }}
        </button>
      </form>

      <div class="text-center mt-lg">
        <p class="text-gray-300">
          Already have an account? 
          <router-link to="/" class="text-primary-500" style="text-decoration: none; font-weight: 600;">
            Sign in here
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { useToast } from '../composables/useToast';

export default {
  name: 'Register',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const { success, error: showError } = useToast();
    
    const formData = ref({
      name: '',
      email: '',
      phone: '',
      address: '',
      username: '',
      password: ''
    });
    
    const loading = ref(false);

    const handleRegister = async () => {
      loading.value = true;

      try {
        await authStore.register(formData.value);
        success('Registration successful! Redirecting...');
        
        setTimeout(() => {
          router.push('/customer');
        }, 1500);
      } catch (err) {
        showError(err || 'Registration failed. Please try again.');
      } finally {
        loading.value = false;
      }
    };

    return {
      formData,
      loading,
      handleRegister
    };
  }
};
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-lg);
}

.auth-card {
  max-width: 500px;
  width: 100%;
}

.text-primary-500 {
  color: var(--primary-500);
}
</style>
