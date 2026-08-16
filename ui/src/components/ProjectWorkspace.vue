<template>
  <div class="workspace-page">
    <el-container class="workspace-layout">
      <el-header class="workspace-header">
        <div class="header-left">
          <el-button text :icon="ArrowLeft" @click="goHome">返回工程列表</el-button>
          <el-divider direction="vertical" />
          <h1>{{ project?.name || '工程工作区' }}</h1>
        </div>
        <span class="project-scope">当前工程实例</span>
      </el-header>

      <el-container>
        <el-aside width="240px" class="workspace-aside">
          <div class="menu-title">工程菜单</div>
          <el-menu :default-active="activeMenu" class="workspace-menu" @select="handleMenuSelect">
            <el-menu-item index="overview">
              <el-icon><HomeFilled /></el-icon>
              <span>工程概览</span>
            </el-menu-item>
            <el-menu-item index="ecu">
              <el-icon><Setting /></el-icon>
              <span>拓扑配置</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <el-main class="workspace-main" v-loading="loading">
          <router-view v-slot="{ Component }">
            <component :is="Component" :project="project" :loading="loading" />
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, HomeFilled, Setting } from '@element-plus/icons-vue';
import { fetchProject, toIdString } from '../api';

export default {
  name: 'ProjectWorkspace',
  props: {
    id: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const router = useRouter();
    const route = useRoute();
    const project = ref(null);
    const loading = ref(false);

    const loadProject = async () => {
      loading.value = true;
      try {
        const loaded = await fetchProject(props.id);
        project.value = loaded
          ? { ...loaded, id: toIdString(loaded.id) }
          : null;
      } catch (error) {
        ElMessage.error(error.message || '加载工程失败');
      } finally {
        loading.value = false;
      }
    };

    const goHome = () => {
      router.push('/home');
    };

    const activeMenu = computed(() => (route.path.endsWith('/ecu') ? 'ecu' : 'overview'));

    const handleMenuSelect = (index) => {
      if (index === 'ecu') {
        router.push(`/projects/${props.id}/ecu`);
      } else {
        router.push(`/projects/${props.id}`);
      }
    };

    onMounted(loadProject);
    watch(
      () => props.id,
      () => {
        loadProject();
      }
    );

    return {
      ArrowLeft,
      HomeFilled,
      Setting,
      activeMenu,
      goHome,
      handleMenuSelect,
      loading,
      project
    };
  }
};
</script>

<style scoped>
.workspace-page,
.workspace-layout {
  min-height: 100vh;
  background: transparent;
}

.workspace-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--tech-border);
  background: rgba(8, 14, 26, 0.82);
  backdrop-filter: blur(16px);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left h1 {
  margin: 0;
  font-size: 20px;
  color: var(--tech-text);
  letter-spacing: 0.08em;
}

.project-scope {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(0, 212, 255, 0.12);
  border: 1px solid rgba(0, 212, 255, 0.28);
  color: var(--tech-accent);
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.workspace-aside {
  background: rgba(8, 14, 26, 0.72);
  border-right: 1px solid var(--tech-border);
  padding: 16px 12px;
}

.menu-title {
  margin: 4px 12px 12px;
  color: var(--tech-muted);
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.16em;
}

.workspace-menu {
  border-right: none;
  background: transparent;
}

:deep(.workspace-menu .el-menu-item) {
  color: var(--tech-muted);
  border-radius: 8px;
  margin-bottom: 4px;
}

:deep(.workspace-menu .el-menu-item.is-active) {
  background: rgba(0, 212, 255, 0.12);
  color: var(--tech-accent);
}

.workspace-main {
  background: transparent;
  padding: 24px;
}
</style>
