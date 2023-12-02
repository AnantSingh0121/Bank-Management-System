import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const routes = [
    {
        path: '/',
        name: 'Login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue')
    },
    {
        path: '/customer',
        name: 'CustomerDashboard',
        component: () => import('../views/CustomerDashboard.vue'),
        meta: { requiresAuth: true, role: 'CUSTOMER' }
    },
    {
        path: '/admin',
        name: 'AdminDashboard',
        component: () => import('../views/AdminDashboard.vue'),
        meta: { requiresAuth: true, role: 'ADMIN' }
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next('/');
    } else if (to.meta.role && authStore.user?.role !== to.meta.role) {
        next('/');
    } else if ((to.name === 'Login' || to.name === 'Register') && authStore.isAuthenticated) {
        if (authStore.user.role === 'ADMIN') {
            next('/admin');
        } else {
            next('/customer');
        }
    } else {
        next();
    }
});

export default router;
