<template>
  <div class="home-page">
    <el-container>
      <!-- Dashboard 顶栏 -->
      <el-header class="dashboard-header">
        <div class="header-content">
          <!-- 左侧：车辆图标 + 标题 -->
          <div class="header-left">
            <div class="logo-container" @click="goToHome">
              <img :src="logoImg" alt="VCDP Logo" class="car-icon" />
            </div>
            <h1 class="site-title">VCDP-车辆通信设计平台</h1>
          </div>

          <!-- 右侧：用户下拉菜单 -->
          <div class="header-right">
            <el-dropdown @command="handleCommand" trigger="click">
              <span class="user-dropdown">
                <el-avatar :size="32" class="user-avatar">
                  {{ userStore.username ? userStore.username.charAt(0).toUpperCase() : 'U' }}
                </el-avatar>
                <span class="username">{{ userStore.username || '用户' }}</span>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowDown, SwitchButton } from '@element-plus/icons-vue';
import { useUserStore } from '../stores/user';
import logoImg from '../assets/logo.png';

export default {
  name: 'Home',
  components: {
    ArrowDown,
    SwitchButton
  },
  setup() {
    const router = useRouter();
    const userStore = useUserStore();
    const logoutLoading = ref(false);

    // 返回主页
    const goToHome = () => {
      router.push('/home');
    };

    // 下拉菜单命令处理
    const handleCommand = async (command) => {
      if (command === 'logout') {
        await handleLogout();
      }
    };

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

    // 用户信息已在登录/注册时获取，无需单独调用接口

    return {
      userStore,
      logoutLoading,
      goToHome,
      handleCommand,
      handleLogout,
      logoImg
    };
  }
};
</script>

<style scoped>
.home-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Dashboard 顶栏样式 */
.dashboard-header {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
  flex-shrink: 0;
}

:deep(.el-header) {
  width: 100%;
  padding: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  width: 100%;
  padding: 0 24px;
  max-width: 100vw;
  box-sizing: border-box;
}

/* 左侧：图标 + 标题 */
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  min-width: 0; /* 允许 flex 子元素收缩 */
  overflow: hidden; /* 防止内容溢出 */
}

.logo-container {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden; /* 确保图片被裁剪成圆形 */
}

.logo-container:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.car-icon {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 填充整个容器，保持比例并裁剪多余部分 */
  border-radius: 50%; /* 确保图片也是圆形 */
}

.site-title {
  font-size: 26px;
  font-weight: 800;
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: 3px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-family: 'Microsoft YaHei', 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-shrink: 1;
}

/* 右侧：用户下拉菜单 */
.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0; /* 防止右侧菜单被压缩 */
  margin-left: 16px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.25);
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: bold;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.username {
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.dropdown-icon {
  color: #ffffff;
  font-size: 14px;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-icon {
  transform: translateY(2px);
}

/* 下拉菜单样式优化 */
:deep(.el-dropdown-menu) {
  margin-top: 8px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f5f7fa;
  color: #409eff;
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 1px solid #e4e7ed;
}

/* 主内容区域 */
:deep(.el-container) {
  width: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-main) {
  width: 100%;
  background-color: #f5f7fa;
  padding: 20px;
  flex: 1;
  overflow-y: auto;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-header {
    height: 56px;
  }

  .header-content {
    padding: 0 16px;
  }

  .site-title {
    font-size: 16px;
    letter-spacing: 1px;
    max-width: 200px; /* 限制标题最大宽度，避免挤压 */
  }
  
  .logo-container {
    width: 36px;
    height: 36px;
    flex-shrink: 0;
  }
  
  
  .username {
    display: none;
  }

  .user-dropdown {
    padding: 4px 8px;
  }

  .user-avatar {
    width: 28px !important;
    height: 28px !important;
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 0 12px;
  }

  .site-title {
    font-size: 14px;
    letter-spacing: 0.5px;
    max-width: 150px;
  }

  .header-left {
    gap: 12px;
  }

  .logo-container {
    width: 32px;
    height: 32px;
  }

}
</style>

