<template>
  <div id="app">
    <router-view />
    <Toast
      v-for="toast in toasts"
      :key="toast.id"
      :message="toast.message"
      :type="toast.type"
      :duration="toast.duration"
      :show="toast.show"
    />
  </div>
</template>

<script>
import Toast from './components/Toast.vue';
import { useToast } from './composables/useToast';

export default {
  name: 'App',
  components: {
    Toast
  },
  setup() {
    const { toasts } = useToast();
    return { toasts };
  }
};
</script>
