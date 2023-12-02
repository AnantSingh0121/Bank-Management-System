<template>
  <Transition name="toast">
    <div v-if="visible" :class="['toast', `toast-${type}`]">
      <div class="toast-icon">
        <span v-if="type === 'success'">✓</span>
        <span v-else-if="type === 'error'">✕</span>
        <span v-else-if="type === 'warning'">⚠</span>
        <span v-else>ℹ</span>
      </div>
      <div class="toast-content">
        <p class="toast-message">{{ message }}</p>
      </div>
    </div>
  </Transition>
</template>

<script>
import { ref, watch } from 'vue';

export default {
  name: 'Toast',
  props: {
    message: {
      type: String,
      required: true
    },
    type: {
      type: String,
      default: 'info',
      validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
    },
    duration: {
      type: Number,
      default: 3000
    },
    show: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const visible = ref(props.show);

    watch(() => props.show, (newVal) => {
      visible.value = newVal;
      if (newVal) {
        setTimeout(() => {
          visible.value = false;
          emit('close');
        }, props.duration);
      }
    });

    return { visible };
  }
};
</script>

<style scoped>
.toast {
  position: fixed;
  top: 2rem;
  right: 2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-radius: var(--radius-lg);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.8);
  min-width: 300px;
  max-width: 500px;
  z-index: 9999;
}

.toast-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  font-size: 1.25rem;
  font-weight: bold;
  flex-shrink: 0;
}

.toast-success .toast-icon {
  background: #d1fae5;
  color: #065f46;
}

.toast-error .toast-icon {
  background: #fee2e2;
  color: #991b1b;
}

.toast-warning .toast-icon {
  background: #fef3c7;
  color: #92400e;
}

.toast-info .toast-icon {
  background: #dbeafe;
  color: #1e40af;
}

.toast-content {
  flex: 1;
}

.toast-message {
  margin: 0;
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--text-main);
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100px);
}
</style>
