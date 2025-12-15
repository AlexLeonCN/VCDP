<template>
  <div class="login-page">
    <!-- 新增的醒目网站标题 -->
    <h1 class="site-title">VCDP-车辆通信设计平台</h1>

    <el-card class="login-card">
      <h2 class="title">登录</h2>

      <el-form 
        :model="form" 
        :rules="rules" 
        ref="loginFormRef" 
        @submit.prevent="handleLogin"
        label-width="0"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
            @keyup.enter="handleLogin"
          ></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading"
            style="width: 100%;"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="link-text">
            还没有账号？
            <el-link type="primary" @click="goToRegister">立即注册</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import { useUserStore } from '../stores/user';

export default {
  name: 'Login',
  setup() {
    const router = useRouter();
    const userStore = useUserStore();
    const loginFormRef = ref(null);
    const loading = ref(false);

    const form = reactive({
      username: '',
      password: ''
    });

    // 表单验证规则
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, max: 20, message: '密码长度在 8 到 20 个字符', trigger: 'blur' }
      ]
    };

    const handleLogin = async () => {
      if (!loginFormRef.value) return;

      // 表单验证
      await loginFormRef.value.validate(async (valid) => {
        if (!valid) {
          return false;
        }

        loading.value = true;
        try {
          const result = await userStore.login(form.username, form.password);
          
          if (result.success) {
            ElMessage.success(result.message || '登录成功！');
            // 延迟一下确保状态已更新，然后跳转到首页
            setTimeout(() => {
              router.push('/home').catch(err => {
                // 如果路由跳转失败，尝试强制跳转
                console.error('路由跳转失败:', err);
                window.location.href = '/home';
              });
            }, 100);
          } else {
            ElMessage.error(result.message || '登录失败，请检查用户名和密码');
          }
        } catch (error) {
          console.error('登录异常:', error);
          ElMessage.error(error.message || '登录失败，请稍后重试');
        } finally {
          loading.value = false;
        }
      });
    };

    const goToRegister = () => {
      router.push('/register');
    };

    return {
      form,
      rules,
      loginFormRef,
      loading,
      handleLogin,
      goToRegister,
      User,
      Lock
    };
  }
};
</script>

<style scoped>
/* 页面整体布局居中 */
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
}

/* 顶部平台名称 */
.site-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 30px;
  color: #2d7eff;
  letter-spacing: 2px;
}

.login-card {
  width: 360px;
  padding: 30px 20px;
  border-radius: 8px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.error-msg {
  color: #f56c6c;
  font-size: 13px;
  text-align: center;
  margin-bottom: 10px;
}

.link-text {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.link-text .el-link {
  margin-left: 5px;
}
</style>