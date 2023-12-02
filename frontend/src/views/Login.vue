<template>
  <div class="auth-container">
    <div class="auth-card glass-card fade-in">
      <div class="text-center mb-xl">
        <h1 class="text-3xl font-bold mb-sm">Welcome Back</h1>
        <p class="text-gray-300">Sign in to your banking account</p>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label>Username</label>
          <input 
            v-model="credentials.username" 
            type="text" 
            placeholder="Enter your username"
            required
          />
        </div>

        <div class="input-group">
          <label>Password</label>
          <input 
            v-model="credentials.password" 
            type="password" 
            placeholder="Enter your password"
            required
          />
        </div>

        <button type="submit" class="btn btn-primary" style="width: 100%;" :disabled="loading">
          {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>

      <div class="text-center mt-lg">
        <p class="text-gray-300">
          Don't have an account? 
          <router-link to="/register" class="text-primary-500" style="text-decoration: none; font-weight: 600;">
            Register here
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
  name: 'Login',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const { error: showError } = useToast();
    
    const credentials = ref({
      username: '',
      password: ''
    });
    
    const loading = ref(false);

    const handleLogin = async () => {
      loading.value = true;

      try {
        const response = await authStore.login(credentials.value);
        
        if (response.role === 'ADMIN') {
          router.push('/admin');
        } else {
          router.push('/customer');
        }
      } catch (err) {
        showError(err || 'Login failed. Please check your credentials.');
      } finally {
        loading.value = false;
      }
    };

    return {
      credentials,
      loading,
      handleLogin
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
  max-width: 450px;
  width: 100%;
}

.text-primary-500 {
  color: var(--primary-500);
}
</style>
