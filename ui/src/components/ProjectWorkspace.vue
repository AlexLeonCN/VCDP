<template>
  <div class="workspace-page">
    <el-container class="workspace-layout" direction="vertical">
      <el-header class="workspace-header">
        <div class="header-left">
          <el-button text :icon="ArrowLeft" @click="goHome">返回工程列表</el-button>
        </div>
        <span class="project-scope" title="返回首页" @click="goHome">VCDP 车辆通信设计平台</span>
      </el-header>

      <el-container class="workspace-body">
        <el-aside width="228px" class="workspace-aside">
          <div class="aside-project">
            <div class="project-mark">{{ projectInitial }}</div>
            <div class="project-meta">
              <strong>{{ project?.name || '工程工作区' }}</strong>
              <span>{{ project?.description || '车辆通信设计' }}</span>
            </div>
          </div>

          <el-menu
            :default-active="activeMenu"
            class="workspace-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="overview">
              <el-icon><Monitor /></el-icon>
              <span>概览</span>
            </el-menu-item>
            <el-sub-menu index="topology">
              <template #title>
                <el-icon><Share /></el-icon>
                <span>拓扑配置</span>
              </template>
              <el-menu-item index="ecu">
                <el-icon><Cpu /></el-icon>
                <span>ECU配置</span>
              </el-menu-item>
            </el-sub-menu>
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
import { ArrowLeft, Cpu, Monitor, Share } from '@element-plus/icons-vue';
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

    const projectInitial = computed(() => {
      const name = project.value?.name?.trim();
      return name ? name.slice(0, 1).toUpperCase() : '工';
    });

    const activeMenu = computed(() => (route.path.endsWith('/ecu') ? 'ecu' : 'overview'));

    const handleMenuSelect = (index) => {
      if (index === 'ecu') {
        router.push(`/projects/${props.id}/ecu`);
      } else if (index === 'overview') {
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
      Cpu,
      Monitor,
      Share,
      activeMenu,
      projectInitial,
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

.workspace-body {
  flex: 1;
  min-height: 0;
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

.project-scope {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(0, 212, 255, 0.12);
  border: 1px solid rgba(0, 212, 255, 0.28);
  color: var(--tech-accent);
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.08em;
  cursor: pointer;
  transition: background 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.project-scope:hover {
  background: rgba(0, 212, 255, 0.2);
  border-color: rgba(0, 212, 255, 0.5);
  box-shadow: 0 0 12px rgba(0, 212, 255, 0.18);
}

.workspace-aside {
  display: flex;
  flex-direction: column;
  background: rgba(8, 14, 26, 0.72);
  border-right: 1px solid var(--tech-border);
  padding: 16px 10px;
}

.aside-project {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 4px 16px;
  padding: 10px;
  border-radius: 10px;
  background: rgba(0, 212, 255, 0.06);
  border: 1px solid rgba(0, 212, 255, 0.14);
}

.project-mark {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: rgba(0, 212, 255, 0.16);
  color: var(--tech-accent);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0;
}

.project-meta {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.project-meta strong {
  color: var(--tech-text);
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.04em;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.project-meta span {
  color: var(--tech-muted);
  font-size: 12px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.workspace-menu {
  flex: 1;
  border-right: none;
  background: transparent;
}

:deep(.workspace-menu .el-menu-item),
:deep(.workspace-menu .el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  padding: 0 12px !important;
  color: var(--tech-muted);
  border-radius: 8px;
  margin-bottom: 4px;
  position: relative;
}

:deep(.workspace-menu .el-menu-item:hover),
:deep(.workspace-menu .el-sub-menu__title:hover) {
  background: rgba(0, 212, 255, 0.06);
  color: var(--tech-text);
}

:deep(.workspace-menu .el-menu-item.is-active) {
  background: rgba(0, 212, 255, 0.12);
  color: var(--tech-accent);
}

:deep(.workspace-menu .el-sub-menu.is-active > .el-sub-menu__title) {
  color: var(--tech-accent);
}

:deep(.workspace-menu .el-menu-item.is-active::before) {
  content: "";
  position: absolute;
  left: 0;
  top: 10px;
  bottom: 10px;
  width: 3px;
  border-radius: 999px;
  background: var(--tech-accent);
}

:deep(.workspace-menu .el-sub-menu .el-menu) {
  background: transparent;
}

:deep(.workspace-menu .el-sub-menu .el-menu-item) {
  padding-left: 44px !important;
  min-width: 0;
}

:deep(.workspace-menu .el-sub-menu__icon-arrow) {
  color: var(--tech-muted);
}

.workspace-main {
  background: transparent;
  padding: 24px;
}
</style>
