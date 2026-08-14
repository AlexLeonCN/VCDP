<template>
  <div class="home-page">
    <el-container>
      <el-header class="dashboard-header">
        <div class="header-content">
          <h1 class="site-title" @click="goToHome">
            VCDP
            <span>车辆通信设计平台</span>
          </h1>
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
          <article
            v-for="project in projects"
            :key="project.id"
            class="project-card"
            @click="enterProject(project)"
          >
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
            <p class="project-desc">{{ project.description || '暂无描述' }}</p>
          </article>
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
            placeholder="可选，工程描述"
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
import { batchDeleteProjects, createProject, deleteProject, fetchProjects, toIdString, updateProject } from '../api';

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
        projects.value = (data.records || []).map(project => ({
          ...project,
          id: toIdString(project.id)
        }));
        total.value = Number(data.total) || 0;
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
      editingProjectId.value = toIdString(project.id);
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
      const selectedId = toIdString(id);
      if (checked) {
        if (!selectedIds.value.includes(selectedId)) {
          selectedIds.value = [...selectedIds.value, selectedId];
        }
      } else {
        selectedIds.value = selectedIds.value.filter(currentId => currentId !== selectedId);
      }
    };

    const handleDelete = async (project) => {
      try {
        await ElMessageBox.confirm(`确定删除工程“${project.name}”吗？`, '删除工程', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });
        await deleteProject(toIdString(project.id));
        ElMessage.success('工程已删除');
        selectedIds.value = selectedIds.value.filter(id => id !== toIdString(project.id));
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
      router.push(`/projects/${toIdString(project.id)}`);
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
  background: rgba(8, 14, 26, 0.82);
  border-bottom: 1px solid var(--tech-border);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(16px);
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
  align-items: center;
  height: 100%;
  width: 100%;
  padding: 0 28px;
  max-width: 100vw;
  box-sizing: border-box;
}

.site-title {
  margin: 0;
  cursor: pointer;
  font-family: "Orbitron", "Segoe UI", sans-serif;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: var(--tech-accent);
  text-shadow: 0 0 18px rgba(0, 212, 255, 0.35);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.site-title span {
  margin-left: 14px;
  font-family: "Rajdhani", "PingFang SC", "Microsoft YaHei", sans-serif;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.28em;
  color: var(--tech-muted);
  text-shadow: none;
}

:deep(.el-container) {
  width: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-main) {
  width: 100%;
  background: transparent;
  padding: 28px;
  flex: 1;
  overflow-y: auto;
}

.project-toolbar {
  max-width: 1200px;
  margin: 0 auto 24px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.project-toolbar h2 {
  margin: 0 0 8px;
  color: var(--tech-text);
  letter-spacing: 0.12em;
}

.project-toolbar p {
  margin: 0;
  color: var(--tech-muted);
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
  border: 1px solid var(--tech-border);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(14, 28, 46, 0.92), rgba(8, 16, 28, 0.92));
  box-shadow: inset 0 1px 0 rgba(0, 212, 255, 0.08);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.project-card:hover {
  transform: translateY(-3px);
  border-color: var(--tech-border-strong);
  box-shadow: 0 0 24px rgba(0, 212, 255, 0.14);
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
  color: var(--tech-text);
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.project-desc {
  max-width: 100%;
  margin: 10px 0 0;
  color: var(--tech-muted);
  font-size: 13px;
  line-height: 1.5;
  text-align: center;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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
    letter-spacing: 0.12em;
  }

  .site-title span {
    display: none;
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
  }
}
</style>
