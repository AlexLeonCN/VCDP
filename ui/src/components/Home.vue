<template>
  <div class="home-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1 class="site-title">VCDP-车辆通信设计平台</h1>
          <div class="user-info">
            <span v-if="userStore.username" class="username">
              欢迎，{{ userStore.username }}
            </span>
            <el-button type="danger" @click="handleLogout" :loading="logoutLoading">
              退出登录
            </el-button>
          </div>
        </div>
      </el-header>
      <el-main>
        <el-card>
          <h2>欢迎使用 VCDP 车辆通信设计平台</h2>
          <p>您已成功登录！</p>
          <p>这里将是您的主要工作区域。</p>
          <el-divider />
          <div class="info-section">
            <h3>系统信息</h3>
            <p><strong>登录状态：</strong>{{ userStore.isAuthenticated ? '已登录' : '未登录' }}</p>
            <p v-if="userStore.userInfo"><strong>用户信息：</strong>{{ JSON.stringify(userStore.userInfo, null, 2) }}</p>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../stores/user';

export default {
  name: 'Home',
  setup() {
    const router = useRouter();
    const userStore = useUserStore();
    const logoutLoading = ref(false);

    // 退出登录
    const handleLogout = async () => {
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );

        logoutLoading.value = true;
        await userStore.logout();
        ElMessage.success('已退出登录');
        router.push('/login');
      } catch (error) {
        // 用户取消操作
        if (error !== 'cancel') {
          ElMessage.error('退出登录失败');
        }
      } finally {
        logoutLoading.value = false;
      }
    };

    // 组件挂载时获取用户信息
    onMounted(() => {
      if (userStore.isAuthenticated && !userStore.userInfo) {
        userStore.fetchUserInfo();
      }
    });

    return {
      userStore,
      logoutLoading,
      handleLogout
    };
  }
};
</script>

<style scoped>
.home-page {
  height: 100vh;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #606266;
  font-size: 14px;
}

.site-title {
  font-size: 24px;
  font-weight: 700;
  color: #2d7eff;
  margin: 0;
  letter-spacing: 2px;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}

.el-card {
  max-width: 1200px;
  margin: 0 auto;
}

.el-card h2 {
  margin-top: 0;
  color: #303133;
}

.el-card p {
  color: #606266;
  line-height: 1.8;
}

.info-section {
  margin-top: 20px;
}

.info-section h3 {
  color: #303133;
  margin-bottom: 15px;
}

.info-section p {
  margin: 8px 0;
}

.info-section pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>

