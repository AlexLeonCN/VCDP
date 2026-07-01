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
        <el-aside width="260px" class="workspace-aside">
          <div class="tree-title">工程对象树</div>
          <el-tree :data="treeData" node-key="id" default-expand-all />
        </el-aside>

        <el-main class="workspace-main" v-loading="loading">
          <el-card v-if="project">
            <h2>{{ project.name }}</h2>
            <p v-if="project.description" class="description">{{ project.description }}</p>
            <p v-else class="description muted">该工程暂无描述。</p>
            <el-divider />
            <p>请从左侧工程对象树进入 ECU、网络接口、PDU、Signal 等工程内实例管理。</p>
            <p>后续新增的所有实例都应携带当前工程 ID：{{ project.id }}。</p>
          </el-card>

          <el-empty v-else-if="!loading" description="工程不存在或已被删除">
            <el-button type="primary" @click="goHome">返回工程列表</el-button>
          </el-empty>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import { fetchProject } from '../api';

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
    const project = ref(null);
    const loading = ref(false);
    const treeData = [
      {
        id: 'project-root',
        label: '当前工程',
        children: [
          { id: 'ecu', label: 'ECU 实例' },
          { id: 'interface', label: '网络接口实例' },
          { id: 'pdu', label: 'PDU 实例' },
          { id: 'signal', label: 'Signal 实例' }
        ]
      }
    ];

    const loadProject = async () => {
      loading.value = true;
      try {
        project.value = await fetchProject(props.id);
      } catch (error) {
        ElMessage.error(error.message || '加载工程失败');
      } finally {
        loading.value = false;
      }
    };

    const goHome = () => {
      router.push('/home');
    };

    onMounted(loadProject);

    return {
      ArrowLeft,
      goHome,
      loading,
      project,
      treeData
    };
  }
};
</script>

<style scoped>
.workspace-page,
.workspace-layout {
  min-height: 100vh;
}

.workspace-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
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
  color: #303133;
}

.project-scope {
  padding: 4px 10px;
  border-radius: 999px;
  background: #ecf5ff;
  color: #409eff;
  font-size: 13px;
  font-weight: 600;
}

.workspace-aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
  padding: 16px;
}

.tree-title {
  margin-bottom: 12px;
  color: #303133;
  font-weight: 700;
}

.workspace-main {
  background: #f5f7fa;
  padding: 24px;
}

.workspace-main .el-card {
  max-width: 960px;
}

.workspace-main h2 {
  margin: 0 0 12px;
  color: #303133;
}

.description {
  color: #606266;
  line-height: 1.8;
}

.muted {
  color: #909399;
}
</style>
