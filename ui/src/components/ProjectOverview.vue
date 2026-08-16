<template>
  <div class="overview-wrapper">
    <el-card v-if="project">
      <h2>{{ project.name }}</h2>
      <p v-if="project.description" class="description">{{ project.description }}</p>
      <p v-else class="description muted">该工程暂无描述。</p>
      <el-divider />
      <p>请从左侧菜单进入拓扑配置等工程内实例管理。</p>
      <p>后续新增的所有实例都应携带当前工程 ID：{{ project.id }}。</p>
    </el-card>

    <el-empty v-else-if="!loading" description="工程不存在或已被删除">
      <el-button type="primary" @click="goHome">返回工程列表</el-button>
    </el-empty>
  </div>
</template>

<script>
import { useRouter } from 'vue-router';

export default {
  name: 'ProjectOverview',
  props: {
    project: {
      type: Object,
      default: null
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  setup() {
    const router = useRouter();
    const goHome = () => {
      router.push('/home');
    };

    return {
      goHome
    };
  }
};
</script>

<style scoped>
.overview-wrapper {
  max-width: 960px;
}

.overview-wrapper h2 {
  margin: 0 0 12px;
  color: var(--tech-text);
  letter-spacing: 0.08em;
}

.description {
  color: var(--tech-muted);
  line-height: 1.8;
}

.muted {
  color: #6b8296;
}

.overview-wrapper p {
  color: var(--tech-muted);
}

.overview-wrapper :deep(.el-card__body) {
  color: var(--tech-muted);
}
</style>
