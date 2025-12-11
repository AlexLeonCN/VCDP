<template>
  <el-card class="login-card">
    <h2 class="title">登录</h2>
    <el-form :model="form" ref="loginForm" @submit.prevent="login">
      <el-form-item prop="username">
        <el-input v-model="form.username" placeholder="用户名"></el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input v-model="form.password" type="password" placeholder="密码"></el-input>
      </el-form-item>

      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

      <el-form-item>
        <el-button type="primary" @click="login" style="width: 100%;">登录</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
export default {
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      errorMsg: ''
    };
  },
  methods: {
    async login() {
      this.errorMsg = '';
      if (!this.form.username || !this.form.password) {
        this.errorMsg = '用户名和密码不能为空';
        return;
      }
      try {
        const res = await fetch('/api/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(this.form)
        });
        const data = await res.json();
        if (data.success) {
          this.$message({
            message: '登录成功！',
            type: 'success'
          });
          window.location.href = '/';
        } else {
          this.errorMsg = data.message;
        }
      } catch (e) {
        this.errorMsg = '网络错误，请稍后重试';
      }
    }
  }
};
</script>

<style scoped>
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
</style>