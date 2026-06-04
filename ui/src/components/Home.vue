<template>
  <div class="home-page">
    <el-container>
      <el-header class="dashboard-header">
        <div class="header-content">
          <div class="header-left">
            <div class="logo-container" @click="goToHome">
              <img :src="logoImg" alt="VCDP Logo" class="car-icon" />
            </div>
            <h1 class="site-title">VCDP-车辆通信设计平台</h1>
          </div>

          <div class="header-right">
            <span class="local-mode-badge">本地单机模式</span>
          </div>
        </div>
      </el-header>

      <el-main>
        <el-card>
          <h2>欢迎使用 VCDP 车辆通信设计平台</h2>
          <p>当前应用以本地单机工具方式运行，无需登录或角色权限配置。</p>
          <p>这里将是您的车辆通信设计工作区域。</p>
          <el-divider />
          <div class="info-section">
            <h3>系统信息</h3>
            <p><strong>运行模式：</strong>本地单机</p>
            <p><strong>数据范围：</strong>仅面向当前本机工具使用</p>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { useRouter } from 'vue-router';
import logoImg from '../assets/logo.png';

export default {
  name: 'Home',
  setup() {
    const router = useRouter();

    const goToHome = () => {
      router.push('/home');
    };

    return {
      goToHome,
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

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  min-width: 0;
  overflow: hidden;
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
  overflow: hidden;
}

.logo-container:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.car-icon {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
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

.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: 16px;
}

.local-mode-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

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
    max-width: 200px;
  }

  .logo-container {
    width: 36px;
    height: 36px;
    flex-shrink: 0;
  }

  .local-mode-badge {
    font-size: 12px;
    padding: 4px 8px;
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
