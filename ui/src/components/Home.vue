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
        </div>
      </el-header>

      <el-main>
        <section class="project-toolbar">
          <div>
            <h2>工程列表</h2>
            <p>所有 ECU、网络接口、PDU、Signal 等实例都将在进入具体工程后创建和管理。</p>
          </div>
          <div class="toolbar-actions">
            <el-button type="primary" :icon="Plus" @click="openCreateDialog">新增工程</el-button>
            <el-button
              type="danger"
              :icon="Delete"
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
            >
              批量删除
            </el-button>
          </div>
        </section>

        <el-empty v-if="!loading && projects.length === 0" description="暂无工程，请新增工程" />

        <div v-else v-loading="loading" class="project-grid">
          <el-tooltip
            v-for="project in projects"
            :key="project.id"
            :content="project.description"
            :disabled="!project.description"
            placement="top"
          >
            <article class="project-card" @click="enterProject(project)">
              <el-checkbox
                class="project-select"
                :model-value="selectedIds.includes(project.id)"
                @click.stop
                @change="checked => toggleSelection(project.id, checked)"
              />

              <div class="project-actions" @click.stop>
                <el-button circle text :icon="Edit" @click="openEditDialog(project)" />
                <el-button circle text type="danger" :icon="Delete" @click="handleDelete(project)" />
              </div>

              <h3 class="project-name">{{ project.name }}</h3>
            </article>
          </el-tooltip>
        </div>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next, jumper, total"
            :current-page="page"
            :page-size="size"
            :total="total"
            @current-change="handlePageChange"
          />
        </div>
      </el-main>
    </el-container>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增工程' : '编辑工程'"
      width="460px"
      destroy-on-close
    >
      <el-form ref="projectFormRef" :model="projectForm" :rules="rules" label-width="80px">
        <el-form-item label="工程名称" prop="name">
          <el-input v-model="projectForm.name" maxlength="100" show-word-limit placeholder="请输入工程名称" />
        </el-form-item>
        <el-form-item label="工程描述" prop="description">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            maxlength="500"
            show-word-limit
            :rows="4"
            placeholder="可选，鼠标悬浮工程卡片时展示"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitProject">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import logoImg from '../assets/logo.png';
import { batchDeleteProjects, createProject, deleteProject, fetchProjects, updateProject } from '../api';

export default {
  name: 'Home',
  setup() {
    const router = useRouter();
    const projects = ref([]);
    const selectedIds = ref([]);
    const loading = ref(false);
    const saving = ref(false);
    const page = ref(1);
    const size = ref(12);
    const total = ref(0);
    const dialogVisible = ref(false);
    const dialogMode = ref('create');
    const editingProjectId = ref(null);
    const projectFormRef = ref(null);
    const projectForm = reactive({
      name: '',
      description: ''
    });

    const rules = {
      name: [
        { required: true, message: '请输入工程名称', trigger: 'blur' },
        { min: 1, max: 100, message: '工程名称长度不能超过 100 个字符', trigger: 'blur' }
      ],
      description: [
        { max: 500, message: '工程描述长度不能超过 500 个字符', trigger: 'blur' }
      ]
    };

    const goToHome = () => {
      router.push('/home');
    };

    const loadProjects = async () => {
      loading.value = true;
      try {
        const data = await fetchProjects({ page: page.value, size: size.value });
        projects.value = data.records || [];
        total.value = data.total || 0;
        selectedIds.value = selectedIds.value.filter(id => projects.value.some(project => project.id === id));
      } catch (error) {
        ElMessage.error(error.message || '加载工程失败');
      } finally {
        loading.value = false;
      }
    };

    const resetForm = () => {
      projectForm.name = '';
      projectForm.description = '';
      editingProjectId.value = null;
      projectFormRef.value?.clearValidate?.();
    };

    const openCreateDialog = () => {
      dialogMode.value = 'create';
      resetForm();
      dialogVisible.value = true;
    };

    const openEditDialog = (project) => {
      dialogMode.value = 'edit';
      editingProjectId.value = project.id;
      projectForm.name = project.name;
      projectForm.description = project.description || '';
      dialogVisible.value = true;
    };

    const submitProject = async () => {
      if (!projectFormRef.value) return;
      await projectFormRef.value.validate(async valid => {
        if (!valid) return;
        saving.value = true;
        try {
          const payload = {
            name: projectForm.name,
            description: projectForm.description
          };
          if (dialogMode.value === 'create') {
            await createProject(payload);
            ElMessage.success('工程创建成功');
            page.value = 1;
          } else {
            await updateProject(editingProjectId.value, payload);
            ElMessage.success('工程更新成功');
          }
          dialogVisible.value = false;
          await loadProjects();
        } catch (error) {
          ElMessage.error(error.message || '保存工程失败');
        } finally {
          saving.value = false;
        }
      });
    };

    const toggleSelection = (id, checked) => {
      if (checked) {
        if (!selectedIds.value.includes(id)) {
          selectedIds.value = [...selectedIds.value, id];
        }
      } else {
        selectedIds.value = selectedIds.value.filter(selectedId => selectedId !== id);
      }
    };

    const handleDelete = async (project) => {
      try {
        await ElMessageBox.confirm(`确定删除工程“${project.name}”吗？`, '删除工程', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });
        await deleteProject(project.id);
        ElMessage.success('工程已删除');
        selectedIds.value = selectedIds.value.filter(id => id !== project.id);
        if (projects.value.length === 1 && page.value > 1) {
          page.value -= 1;
        }
        await loadProjects();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || '删除工程失败');
        }
      }
    };

    const handleBatchDelete = async () => {
      if (selectedIds.value.length === 0) return;
      try {
        await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个工程吗？`, '批量删除工程', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });
        await batchDeleteProjects(selectedIds.value);
        ElMessage.success('批量删除完成');
        selectedIds.value = [];
        page.value = 1;
        await loadProjects();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || '批量删除失败');
        }
      }
    };

    const handlePageChange = async (nextPage) => {
      page.value = nextPage;
      await loadProjects();
    };

    const enterProject = (project) => {
      router.push(`/projects/${project.id}`);
    };

    onMounted(loadProjects);

    return {
      Delete,
      Edit,
      Plus,
      dialogMode,
      dialogVisible,
      goToHome,
      handleBatchDelete,
      handleDelete,
      handlePageChange,
      enterProject,
      loadProjects,
      loading,
      logoImg,
      openCreateDialog,
      openEditDialog,
      page,
      projectForm,
      projectFormRef,
      projects,
      rules,
      saving,
      selectedIds,
      size,
      submitProject,
      toggleSelection,
      total
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

:deep(.el-container) {
  width: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-main) {
  width: 100%;
  background-color: #f5f7fa;
  padding: 24px;
  flex: 1;
  overflow-y: auto;
}

.project-toolbar {
  max-width: 1200px;
  margin: 0 auto 20px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.project-toolbar h2 {
  margin: 0 0 8px;
  color: #303133;
}

.project-toolbar p {
  margin: 0;
  color: #606266;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.project-grid {
  max-width: 1200px;
  min-height: 260px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
}

.project-card {
  position: relative;
  min-height: 150px;
  padding: 24px 16px 18px;
  border: 1px solid #e4e7ed;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.project-card:hover {
  transform: translateY(-3px);
  border-color: #409eff;
  box-shadow: 0 10px 24px rgba(64, 158, 255, 0.18);
}

.project-select {
  position: absolute;
  top: 10px;
  left: 12px;
}

.project-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.project-card:hover .project-actions {
  opacity: 1;
}

.project-name {
  max-width: 100%;
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination-wrapper {
  max-width: 1200px;
  margin: 24px auto 0;
  display: flex;
  justify-content: flex-end;
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

  .project-toolbar {
    flex-direction: column;
  }

  .toolbar-actions {
    justify-content: flex-start;
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
