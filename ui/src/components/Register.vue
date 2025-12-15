<template>
  <div class="register-page">
    <!-- 新增的醒目网站标题 -->
    <h1 class="site-title">VCDP-车辆通信设计平台</h1>

    <el-card class="register-card">
      <h2 class="title">注册</h2>

      <el-form 
        :model="form" 
        :rules="rules" 
        ref="registerFormRef" 
        @submit.prevent="handleRegister"
        label-width="0"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
            @keyup.enter="handleRegister"
          ></el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input 
            v-model="form.email" 
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            clearable
            @keyup.enter="handleRegister"
          ></el-input>
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input 
            v-model="form.nickname" 
            placeholder="请输入昵称（可选）"
            :prefix-icon="UserFilled"
            clearable
            @keyup.enter="handleRegister"
          ></el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码（8-20位）"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleRegister"
          ></el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请确认密码"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleRegister"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister" 
            :loading="loading"
            style="width: 100%;"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>

        <el-form-item>
          <div class="link-text">
            已有账号？
            <el-link type="primary" @click="goToLogin">立即登录</el-link>
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
import { User, Lock, Message, UserFilled } from '@element-plus/icons-vue';
import { useUserStore } from '../stores/user';

export default {
  name: 'Register',
  setup() {
    const router = useRouter();
    const userStore = useUserStore();
    const registerFormRef = ref(null);
    const loading = ref(false);

    const form = reactive({
      username: '',
      email: '',
      nickname: '',
      password: '',
      confirmPassword: ''
    });

    // 自定义验证：确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== form.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    // 表单验证规则
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      nickname: [
        { max: 50, message: '昵称长度不能超过 50 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, max: 20, message: '密码长度在 8 到 20 个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: 'blur' }
      ]
    };

    const handleRegister = async () => {
      if (!registerFormRef.value) return;

      // 表单验证
      await registerFormRef.value.validate(async (valid) => {
        if (!valid) {
          return false;
        }

        loading.value = true;
        try {
          const result = await userStore.register(
            form.username,
            form.password,
            form.email,
            form.nickname
          );
          
          if (result.success) {
            ElMessage.success(result.message || '注册成功！');
            // 延迟一下确保状态已更新，然后跳转到首页
            setTimeout(() => {
              router.push('/home').catch(err => {
                console.error('路由跳转失败:', err);
                window.location.href = '/home';
              });
            }, 100);
          } else {
            ElMessage.error(result.message || '注册失败，请检查输入信息');
          }
        } catch (error) {
          console.error('注册异常:', error);
          ElMessage.error(error.message || '注册失败，请稍后重试');
        } finally {
          loading.value = false;
        }
      });
    };

    const goToLogin = () => {
      router.push('/login');
    };

    return {
      form,
      rules,
      registerFormRef,
      loading,
      handleRegister,
      goToLogin,
      User,
      Lock,
      Message,
      UserFilled
    };
  }
};
</script>

<style scoped>
/* 页面整体布局居中 */
.register-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 60px;
}

/* 顶部平台名称 */
.site-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 30px;
  color: #2d7eff;
  letter-spacing: 2px;
}

.register-card {
  width: 360px;
  padding: 30px 20px;
  border-radius: 8px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
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

