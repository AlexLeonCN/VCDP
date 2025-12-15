import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '../stores/user';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import Home from '../components/Home.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      requiresGuest: true // 已登录用户不能访问登录页
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: {
      requiresGuest: true // 已登录用户不能访问注册页
    }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: {
      requiresAuth: true // 需要登录才能访问
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫：检查是否需要登录
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();

  // 开发环境下打印路由信息
  if (process.env.NODE_ENV === 'development') {
    console.log('路由守卫:', {
      to: to.path,
      from: from.path,
      isAuthenticated: userStore.isAuthenticated,
      isLoggedIn: userStore.isLoggedIn,
      hasToken: !!userStore.token
    });
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    if (!userStore.isAuthenticated) {
      console.warn('未登录，跳转到登录页');
      next('/login');
    } else {
      next();
    }
  }
  // 检查是否已登录（已登录用户不能访问登录页和注册页）
  else if (to.meta.requiresGuest) {
    if (userStore.isAuthenticated) {
      console.log('已登录，跳转到首页');
      next('/home');
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;

