<template>
  <div class="ecu-page">
    <section class="ecu-toolbar">
      <div>
        <h2>ECU列表</h2>
        <p>管理当前工程下的 ECU 基础配置与通信接口配置。</p>
      </div>
      <div class="toolbar-actions">
        <el-button type="primary" :icon="Plus" @click="openCreateDialog">新增 ECU</el-button>
        <el-button :icon="Refresh" @click="loadEcus">加载 ECU</el-button>
        <el-button type="danger" :icon="Delete" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>
    </section>

    <el-empty v-if="!loading && ecus.length === 0" description="暂无 ECU 配置，请新增" />

    <div v-else v-loading="loading" class="ecu-grid">
      <article v-for="ecu in ecus" :key="ecu.id" class="ecu-card" @click="openViewDialog(ecu)">
        <el-checkbox
          class="ecu-select"
          :model-value="selectedIds.includes(ecu.id)"
          @click.stop
          @change="checked => toggleSelection(ecu.id, checked)"
        />

        <div class="ecu-actions" @click.stop>
          <el-button circle text :icon="View" @click="openViewDialog(ecu)" />
          <el-button circle text :icon="Edit" @click="openEditDialog(ecu)" />
          <el-button circle text type="danger" :icon="Delete" @click="handleDelete(ecu)" />
        </div>

        <h3 class="ecu-name">{{ ecu.name }}</h3>
        <p class="ecu-type">款型：{{ ecu.type || '未设置' }}</p>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="980px"
      destroy-on-close
      class="ecu-dialog"
    >
      <el-tabs v-model="activeTab">
        <el-tab-pane label="ECU基础配置" name="base">
          <el-form ref="ecuFormRef" :model="ecuForm" :rules="ecuRules" label-width="140px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="名称" prop="name">
                  <el-input v-model="ecuForm.name" :disabled="isView" maxlength="100" show-word-limit />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="款型">
                  <el-input v-model="ecuForm.type" :disabled="isView" maxlength="100" show-word-limit />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="MAC地址" prop="mac">
                  <MacAddressInput v-model="ecuForm.mac" :disabled="isView" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="IP地址" prop="ip">
                  <IpAddressInput v-model="ecuForm.ip" :disabled="isView" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="端口号" prop="port">
                  <el-input-number v-model="ecuForm.port" :disabled="isView" :min="0" controls-position="right" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="设备索引" prop="index">
                  <el-input-number v-model="ecuForm.index" :disabled="isView" :min="0" controls-position="right" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="描述">
                  <el-input
                    v-model="ecuForm.desc"
                    :disabled="isView"
                    type="textarea"
                    maxlength="500"
                    show-word-limit
                    :rows="3"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="转发表通信配置" name="forward">
          <el-form ref="forwardFormRef" :model="forwardForm" :rules="forwardRules" label-width="170px">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="pFlash空间起始地址" prop="pFlashMemoryStartAddress">
                  <el-input v-model="forwardForm.pFlashMemoryStartAddress" :disabled="isView" placeholder="十六进制，如 0x1000" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="pFlash空间大小" prop="pFlashMemorySizeLimit">
                  <el-input v-model="forwardForm.pFlashMemorySizeLimit" :disabled="isView" placeholder="十六进制" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="RAM空间起始地址" prop="ramMemoryStartAddress">
                  <el-input v-model="forwardForm.ramMemoryStartAddress" :disabled="isView" placeholder="十六进制" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="RAM空间大小" prop="ramMemorySizeLimit">
                  <el-input v-model="forwardForm.ramMemorySizeLimit" :disabled="isView" placeholder="十六进制" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="CAN接口配置" name="can">
          <div class="interface-toolbar">
            <el-button type="primary" :icon="Plus" :disabled="isView" @click="addCanInterface">
              新增 CAN 接口
            </el-button>
          </div>
          <el-table v-if="canInterfaces.length > 0" :data="canInterfaces" border>
            <el-table-column label="CAN接口名称" min-width="140">
              <template #default="{ row }">
                <el-input v-model="row.interfaceName" :disabled="isView" />
              </template>
            </el-table-column>
            <el-table-column label="通道ID" width="130">
              <template #default="{ row }">
                <el-input-number v-model="row.channelId" :disabled="isView" :min="0" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="端口号" width="130">
              <template #default="{ row }">
                <el-input-number v-model="row.port" :disabled="isView" :min="0" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="接口类型" width="140">
              <template #default="{ row }">
                <el-select v-model="row.type" :disabled="isView" placeholder="请选择">
                  <el-option
                    v-for="option in canInterfaceTypeOptions"
                    :key="option.code"
                    :label="option.name"
                    :value="option.code"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="连接类型" width="160">
              <template #default="{ row }">
                <el-select v-model="row.connType" :disabled="isView" placeholder="请选择">
                  <el-option
                    v-for="option in canConnTypeOptions"
                    :key="option.code"
                    :label="option.name"
                    :value="option.code"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ $index }">
                <el-button text type="danger" :disabled="isView" @click="removeCanInterface($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无 CAN 接口配置" />
        </el-tab-pane>

        <el-tab-pane label="LIN接口配置" name="lin">
          <div class="interface-toolbar">
            <el-button type="primary" :icon="Plus" :disabled="isView" @click="addLinInterface">
              新增 LIN 接口
            </el-button>
          </div>
          <el-table v-if="linInterfaces.length > 0" :data="linInterfaces" border>
            <el-table-column label="LIN接口名称" min-width="160">
              <template #default="{ row }">
                <el-input v-model="row.interfaceName" :disabled="isView" />
              </template>
            </el-table-column>
            <el-table-column label="通道ID" width="160">
              <template #default="{ row }">
                <el-input-number v-model="row.channelId" :disabled="isView" :min="0" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="端口号" width="160">
              <template #default="{ row }">
                <el-input-number v-model="row.port" :disabled="isView" :min="0" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ $index }">
                <el-button text type="danger" :disabled="isView" @click="removeLinInterface($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无 LIN 接口配置" />
        </el-tab-pane>

        <el-tab-pane label="ETH接口配置" name="eth">
          <div class="interface-toolbar">
            <el-button type="primary" :icon="Plus" :disabled="isView" @click="addEthInterface">
              新增 ETH 接口
            </el-button>
          </div>
          <el-table v-if="ethInterfaces.length > 0" :data="ethInterfaces" border>
            <el-table-column label="ETH接口名称" min-width="140">
              <template #default="{ row }">
                <el-input v-model="row.interfaceName" :disabled="isView" />
              </template>
            </el-table-column>
            <el-table-column label="通道ID" width="130">
              <template #default="{ row }">
                <el-input-number v-model="row.channelId" :disabled="isView" :min="0" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="端口号" width="130">
              <template #default="{ row }">
                <el-input-number v-model="row.port" :disabled="isView" :min="0" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="接口类型" width="140">
              <template #default="{ row }">
                <el-select v-model="row.type" :disabled="isView" placeholder="请选择">
                  <el-option
                    v-for="option in ethInterfaceTypeOptions"
                    :key="option.code"
                    :label="option.name"
                    :value="option.code"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ $index }">
                <el-button text type="danger" :disabled="isView" @click="removeEthInterface($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无 ETH 接口配置" />
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ isView ? '关闭' : '取消' }}</el-button>
        <el-button v-if="!isView" type="primary" :loading="saving" @click="submitEcu">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Edit, Plus, Refresh, View } from '@element-plus/icons-vue';
import {
  batchDeleteEcus,
  createEcu,
  deleteEcu,
  fetchCanConnTypes,
  fetchCanInterfaceTypes,
  fetchEcu,
  fetchEcus,
  fetchEthInterfaceTypes,
  updateEcu
} from '../api';
import MacAddressInput from './MacAddressInput.vue';
import IpAddressInput from './IpAddressInput.vue';

const MAC_REGEX = /^[0-9A-F]{12}$/;
const HEX_REGEX = /^(0x)?[0-9a-fA-F]+$/;
const IPV4_REGEX =
  /^(25[0-5]|2[0-4]\d|1?\d?\d)\.(25[0-5]|2[0-4]\d|1?\d?\d)\.(25[0-5]|2[0-4]\d|1?\d?\d)\.(25[0-5]|2[0-4]\d|1?\d?\d)$/;

export default {
  name: 'EcuList',
  components: {
    MacAddressInput,
    IpAddressInput
  },
  setup() {
    const route = useRoute();
    const projectId = computed(() => route.params.id);
    const ecus = ref([]);
    const selectedIds = ref([]);
    const loading = ref(false);
    const saving = ref(false);
    const page = ref(1);
    const size = ref(12);
    const total = ref(0);
    const dialogVisible = ref(false);
    const dialogMode = ref('create');
    const activeTab = ref('base');
    const ecuFormRef = ref(null);
    const forwardFormRef = ref(null);

    const ecuForm = reactive({
      id: '',
      projectId: '',
      name: '',
      type: '',
      desc: '',
      mac: '',
      ip: '',
      port: null,
      index: null
    });

    const forwardForm = reactive({
      id: '',
      ecuId: '',
      projectId: '',
      pFlashMemoryStartAddress: '',
      pFlashMemorySizeLimit: '',
      ramMemoryStartAddress: '',
      ramMemorySizeLimit: ''
    });

    const canInterfaces = ref([]);
    const linInterfaces = ref([]);
    const ethInterfaces = ref([]);
    const canInterfaceTypeOptions = ref([]);
    const canConnTypeOptions = ref([]);
    const ethInterfaceTypeOptions = ref([]);

    const isView = computed(() => dialogMode.value === 'view');
    const dialogTitle = computed(() => {
      if (dialogMode.value === 'create') return '新增 ECU';
      if (dialogMode.value === 'edit') return '编辑 ECU';
      return '查看 ECU';
    });

    const ecuRules = {
      name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
      mac: [
        { required: true, message: '请输入 MAC 地址', trigger: 'change' },
        {
          validator: (_, value, callback) => {
            const normalized = normalizeMac(value);
            if (!normalized || !MAC_REGEX.test(normalized)) {
              callback(new Error('MAC 格式不正确'));
            } else {
              callback();
            }
          },
          trigger: 'change'
        }
      ],
      ip: [
        { required: true, message: '请输入 IP 地址', trigger: 'change' },
        {
          validator: (_, value, callback) => {
            if (!IPV4_REGEX.test(value || '')) {
              callback(new Error('IP 格式不正确'));
            } else {
              callback();
            }
          },
          trigger: 'change'
        }
      ],
      port: [{ required: true, message: '请输入端口号', trigger: 'change' }],
      index: [{ required: true, message: '请输入设备索引', trigger: 'change' }]
    };

    const createHexRule = message => [
      { required: true, message, trigger: 'blur' },
      {
        validator: (_, value, callback) => {
          if (!HEX_REGEX.test(value || '')) {
            callback(new Error('请输入大于0的十六进制'));
          } else {
            callback();
          }
        },
        trigger: 'blur'
      }
    ];

    const forwardRules = {
      pFlashMemoryStartAddress: createHexRule('请输入 pFlash 空间起始地址'),
      pFlashMemorySizeLimit: createHexRule('请输入 pFlash 空间大小'),
      ramMemoryStartAddress: createHexRule('请输入 RAM 空间起始地址'),
      ramMemorySizeLimit: createHexRule('请输入 RAM 空间大小')
    };

    const loadEnumOptions = async () => {
      try {
        const [canTypes, canConnTypes, ethTypes] = await Promise.all([
          fetchCanInterfaceTypes(),
          fetchCanConnTypes(),
          fetchEthInterfaceTypes()
        ]);
        canInterfaceTypeOptions.value = canTypes || [];
        canConnTypeOptions.value = canConnTypes || [];
        ethInterfaceTypeOptions.value = ethTypes || [];
      } catch (error) {
        ElMessage.error(error.message || '加载枚举失败');
      }
    };

    const loadEcus = async () => {
      loading.value = true;
      try {
        const data = await fetchEcus(projectId.value, { page: page.value, size: size.value });
        ecus.value = data.records || [];
        total.value = data.total || 0;
        selectedIds.value = selectedIds.value.filter(id => ecus.value.some(ecu => ecu.id === id));
      } catch (error) {
        ElMessage.error(error.message || '加载 ECU 失败');
      } finally {
        loading.value = false;
      }
    };

    const handlePageChange = async nextPage => {
      page.value = nextPage;
      await loadEcus();
    };

    const resetForms = () => {
      ecuForm.id = '';
      ecuForm.projectId = projectId.value || '';
      ecuForm.name = '';
      ecuForm.type = '';
      ecuForm.desc = '';
      ecuForm.mac = '';
      ecuForm.ip = '';
      ecuForm.port = null;
      ecuForm.index = null;
      forwardForm.id = '';
      forwardForm.ecuId = '';
      forwardForm.projectId = projectId.value || '';
      forwardForm.pFlashMemoryStartAddress = '';
      forwardForm.pFlashMemorySizeLimit = '';
      forwardForm.ramMemoryStartAddress = '';
      forwardForm.ramMemorySizeLimit = '';
      canInterfaces.value = [];
      linInterfaces.value = [];
      ethInterfaces.value = [];
      ecuFormRef.value?.clearValidate?.();
      forwardFormRef.value?.clearValidate?.();
      activeTab.value = 'base';
    };

    const applyDetail = detail => {
      const ecu = detail.ecu || {};
      const forwardInfo = detail.forwardInfo || {};
      ecuForm.id = ecu.id || '';
      ecuForm.projectId = ecu.projectId || projectId.value || '';
      ecuForm.name = ecu.name || '';
      ecuForm.type = ecu.type || '';
      ecuForm.desc = ecu.desc || '';
      ecuForm.mac = normalizeMac(ecu.mac || '');
      ecuForm.ip = ecu.ip || '';
      ecuForm.port = ecu.port ?? null;
      ecuForm.index = ecu.index ?? null;
      forwardForm.id = forwardInfo.id || '';
      forwardForm.ecuId = forwardInfo.ecuId || ecu.id || '';
      forwardForm.projectId = forwardInfo.projectId || projectId.value || '';
      forwardForm.pFlashMemoryStartAddress = forwardInfo.pFlashMemoryStartAddress || '';
      forwardForm.pFlashMemorySizeLimit = forwardInfo.pFlashMemorySizeLimit || '';
      forwardForm.ramMemoryStartAddress = forwardInfo.ramMemoryStartAddress || '';
      forwardForm.ramMemorySizeLimit = forwardInfo.ramMemorySizeLimit || '';
      canInterfaces.value = (detail.canInterfaces || []).map(item => ({ ...item }));
      linInterfaces.value = (detail.linInterfaces || []).map(item => ({ ...item }));
      ethInterfaces.value = (detail.ethInterfaces || []).map(item => ({ ...item }));
    };

    const openCreateDialog = () => {
      dialogMode.value = 'create';
      resetForms();
      dialogVisible.value = true;
    };

    const openViewDialog = async ecu => {
      dialogMode.value = 'view';
      await loadDetail(ecu.id);
      dialogVisible.value = true;
    };

    const openEditDialog = async ecu => {
      dialogMode.value = 'edit';
      await loadDetail(ecu.id);
      dialogVisible.value = true;
    };

    const loadDetail = async ecuId => {
      resetForms();
      try {
        const detail = await fetchEcu(projectId.value, ecuId);
        applyDetail(detail);
      } catch (error) {
        ElMessage.error(error.message || '加载 ECU 详情失败');
      }
    };

    const submitEcu = async () => {
      if (!ecuFormRef.value || !forwardFormRef.value) return;
      try {
        await ecuFormRef.value.validate();
        await forwardFormRef.value.validate();
        const interfaceError = validateInterfaces();
        if (interfaceError) {
          ElMessage.error(interfaceError);
          return;
        }
        saving.value = true;
        const payload = buildPayload();
        if (dialogMode.value === 'create') {
          await createEcu(projectId.value, payload);
          ElMessage.success('ECU 创建成功');
          page.value = 1;
        } else {
          await updateEcu(projectId.value, ecuForm.id, payload);
          ElMessage.success('ECU 更新成功');
        }
        dialogVisible.value = false;
        await loadEcus();
      } catch (error) {
        if (error) {
          ElMessage.error(error.message || '保存 ECU 失败');
        }
      } finally {
        saving.value = false;
      }
    };

    const buildPayload = () => ({
      ecu: {
        id: ecuForm.id || undefined,
        projectId: projectId.value,
        name: ecuForm.name,
        type: ecuForm.type,
        desc: ecuForm.desc,
        mac: normalizeMac(ecuForm.mac),
        ip: ecuForm.ip,
        port: ecuForm.port,
        index: ecuForm.index
      },
      forwardInfo: {
        id: forwardForm.id || undefined,
        ecuId: ecuForm.id || undefined,
        projectId: projectId.value,
        pFlashMemoryStartAddress: forwardForm.pFlashMemoryStartAddress,
        pFlashMemorySizeLimit: forwardForm.pFlashMemorySizeLimit,
        ramMemoryStartAddress: forwardForm.ramMemoryStartAddress,
        ramMemorySizeLimit: forwardForm.ramMemorySizeLimit
      },
      canInterfaces: canInterfaces.value.map(item => ({
        interfaceName: item.interfaceName,
        channelId: item.channelId,
        port: item.port,
        type: item.type,
        connType: item.connType
      })),
      linInterfaces: linInterfaces.value.map(item => ({
        interfaceName: item.interfaceName,
        channelId: item.channelId,
        port: item.port
      })),
      ethInterfaces: ethInterfaces.value.map(item => ({
        interfaceName: item.interfaceName,
        channelId: item.channelId,
        port: item.port,
        type: item.type
      }))
    });

    const validateInterfaces = () => {
      const names = new Set();
      const ports = new Set();

      for (const item of canInterfaces.value) {
        if (!item.interfaceName) return '请填写 CAN 接口名称';
        if (item.channelId === null || item.channelId === undefined) return '请填写 CAN 接口通道ID';
        if (item.port === null || item.port === undefined) return '请填写 CAN 接口端口号';
        if (item.type === null || item.type === undefined) return '请选择 CAN 接口类型';
        if (item.connType === null || item.connType === undefined) return '请选择 CAN 接口连接类型';
        if (names.has(item.interfaceName)) return '接口名称不可重复';
        if (ports.has(item.port)) return '接口端口号不可重复';
        names.add(item.interfaceName);
        ports.add(item.port);
      }
      for (const item of linInterfaces.value) {
        if (!item.interfaceName) return '请填写 LIN 接口名称';
        if (item.channelId === null || item.channelId === undefined) return '请填写 LIN 接口通道ID';
        if (item.port === null || item.port === undefined) return '请填写 LIN 接口端口号';
        if (names.has(item.interfaceName)) return '接口名称不可重复';
        if (ports.has(item.port)) return '接口端口号不可重复';
        names.add(item.interfaceName);
        ports.add(item.port);
      }
      for (const item of ethInterfaces.value) {
        if (!item.interfaceName) return '请填写 ETH 接口名称';
        if (item.channelId === null || item.channelId === undefined) return '请填写 ETH 接口通道ID';
        if (item.port === null || item.port === undefined) return '请填写 ETH 接口端口号';
        if (item.type === null || item.type === undefined) return '请选择 ETH 接口类型';
        if (names.has(item.interfaceName)) return '接口名称不可重复';
        if (ports.has(item.port)) return '接口端口号不可重复';
        names.add(item.interfaceName);
        ports.add(item.port);
      }
      return '';
    };

    const handleDelete = async ecu => {
      try {
        await ElMessageBox.confirm(`确定删除 ECU “${ecu.name}”吗？`, '删除 ECU', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });
        await deleteEcu(projectId.value, ecu.id);
        ElMessage.success('ECU 已删除');
        selectedIds.value = selectedIds.value.filter(id => id !== ecu.id);
        if (ecus.value.length === 1 && page.value > 1) {
          page.value -= 1;
        }
        await loadEcus();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || '删除 ECU 失败');
        }
      }
    };

    const handleBatchDelete = async () => {
      if (selectedIds.value.length === 0) return;
      try {
        await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个 ECU 吗？`, '批量删除 ECU', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });
        await batchDeleteEcus(projectId.value, selectedIds.value);
        ElMessage.success('批量删除完成');
        selectedIds.value = [];
        page.value = 1;
        await loadEcus();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.message || '批量删除失败');
        }
      }
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

    const addCanInterface = () => {
      canInterfaces.value = [
        ...canInterfaces.value,
        { interfaceName: '', channelId: null, port: null, type: null, connType: null }
      ];
    };

    const removeCanInterface = index => {
      canInterfaces.value.splice(index, 1);
    };

    const addLinInterface = () => {
      linInterfaces.value = [...linInterfaces.value, { interfaceName: '', channelId: null, port: null }];
    };

    const removeLinInterface = index => {
      linInterfaces.value.splice(index, 1);
    };

    const addEthInterface = () => {
      ethInterfaces.value = [...ethInterfaces.value, { interfaceName: '', channelId: null, port: null, type: null }];
    };

    const removeEthInterface = index => {
      ethInterfaces.value.splice(index, 1);
    };

    const normalizeMac = value => {
      if (!value) return '';
      return String(value).replace(/[^0-9a-fA-F]/g, '').toUpperCase();
    };

    onMounted(async () => {
      await loadEnumOptions();
      await loadEcus();
    });

    watch(projectId, async () => {
      page.value = 1;
      selectedIds.value = [];
      await loadEcus();
    });

    return {
      Delete,
      Edit,
      Plus,
      Refresh,
      View,
      activeTab,
      addCanInterface,
      addEthInterface,
      addLinInterface,
      canConnTypeOptions,
      canInterfaceTypeOptions,
      canInterfaces,
      dialogTitle,
      dialogVisible,
      ecuForm,
      ecuFormRef,
      ecuRules,
      ecus,
      ethInterfaceTypeOptions,
      ethInterfaces,
      forwardForm,
      forwardFormRef,
      forwardRules,
      handleBatchDelete,
      handleDelete,
      handlePageChange,
      isView,
      linInterfaces,
      loadEcus,
      openCreateDialog,
      openEditDialog,
      openViewDialog,
      page,
      removeCanInterface,
      removeEthInterface,
      removeLinInterface,
      saving,
      selectedIds,
      size,
      submitEcu,
      toggleSelection,
      total
    };
  }
};
</script>

<style scoped>
.ecu-page {
  width: 100%;
}

.ecu-toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 20px;
}

.ecu-toolbar h2 {
  margin: 0 0 8px;
  color: #303133;
}

.ecu-toolbar p {
  margin: 0;
  color: #909399;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.ecu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.ecu-card {
  position: relative;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px 18px 18px;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.ecu-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.ecu-select {
  position: absolute;
  top: 12px;
  left: 12px;
}

.ecu-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
}

.ecu-name {
  margin: 24px 0 8px;
  font-size: 18px;
  color: #303133;
}

.ecu-type {
  margin: 0;
  color: #606266;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.interface-toolbar {
  margin-bottom: 12px;
}
</style>
