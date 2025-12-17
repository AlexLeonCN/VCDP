import { defineStore } from 'pinia';
import { login as loginApi, register as registerApi, logout as logoutApi } from '../api';

export const useUserStore = defineStore('user', {
  state: () => {
    // 从 localStorage 恢复用户信息
    const savedUserInfo = localStorage.getItem('userInfo');
    return {
      token: localStorage.getItem('token') || '',
      userInfo: savedUserInfo ? JSON.parse(savedUserInfo) : null,
      isLoggedIn: !!localStorage.getItem('token')
    };
  },

  getters: {
    username: (state) => state.userInfo?.username || '',
    isAuthenticated: (state) => state.isLoggedIn || !!state.token
  },

  actions: {
    /**
     * 登录
     */
    async login(username, password) {
      try {
        const data = await loginApi(username, password);
        
        // 判断登录成功：检查 success 字段，或者 HTTP 状态码为 200
        if (data.success !== false) {
          // 保存 token（如果返回了）
          const token = data.data?.token || data.token || data.accessToken;
          if (token) {
            this.token = token;
            localStorage.setItem('token', token);
          }

          // 标记为已登录（即使没有 token，也认为登录成功）
          this.isLoggedIn = true;

          // 保存用户信息（如果返回了）
          let userInfo = null;
          if (data.data?.userInfo) {
            userInfo = data.data.userInfo;
          } else if (data.userInfo) {
            userInfo = data.userInfo;
          } else if (data.user) {
            userInfo = data.user;
          } else if (data.username) {
            // 如果只返回了用户名，也保存
            userInfo = { username: data.username };
          }
          
          if (userInfo) {
            this.userInfo = userInfo;
            // 持久化到 localStorage
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
          }

          return { success: true, message: data.message || '登录成功' };
        } else {
          return { success: false, message: data.message || '登录失败，请检查用户名和密码' };
        }
      } catch (error) {
        console.error('登录错误:', error);
        return { 
          success: false, 
          message: error.message || '网络错误，请稍后重试' 
        };
      }
    },

    /**
     * 注册
     */
    async register(username, password, email, nickname) {
      try {
        const data = await registerApi(username, password, email, nickname);
        
        // 判断注册成功：检查 success 字段
        if (data.success !== false) {
          // 保存 token（如果返回了）
          const token = data.data?.token || data.token || data.accessToken;
          if (token) {
            this.token = token;
            localStorage.setItem('token', token);
          }

          // 标记为已登录
          this.isLoggedIn = true;

          // 保存用户信息（如果返回了）
          let userInfo = null;
          if (data.data?.userInfo) {
            userInfo = data.data.userInfo;
          } else if (data.userInfo) {
            userInfo = data.userInfo;
          } else if (data.user) {
            userInfo = data.user;
          } else if (data.username) {
            // 如果只返回了用户名，也保存
            userInfo = { username: data.username };
          }
          
          if (userInfo) {
            this.userInfo = userInfo;
            // 持久化到 localStorage
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
          }

          return { success: true, message: data.message || '注册成功' };
        } else {
          return { success: false, message: data.message || '注册失败，请检查输入信息' };
        }
      } catch (error) {
        console.error('注册错误:', error);
        return { 
          success: false, 
          message: error.message || '网络错误，请稍后重试' 
        };
      }
    },

    /**
     * 退出登录
     */
    async logout() {
      try {
        // 调用后端退出接口（如果存在）
        await logoutApi();
      } catch (error) {
        console.error('退出登录接口调用失败:', error);
      } finally {
        // 无论接口是否成功，都清除本地状态
        this.token = '';
        this.isLoggedIn = false;
        this.userInfo = null;
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
      }
    },

    /**
     * 清除用户信息（用于 token 过期等情况）
     */
    clearUserInfo() {
      this.token = '';
      this.isLoggedIn = false;
      this.userInfo = null;
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    }
  }
});

