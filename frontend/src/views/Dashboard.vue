<template>
  <el-container class="dashboard-container">
    <el-header class="header">
      <h2>Job Hunt Manager (V1) - 岗位投递台账</h2>
      <div class="user-actions">
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <el-main>
      <!-- 搜索与操作栏 -->
      <div class="toolbar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="关键词">
            <el-input v-model="searchForm.keyword" placeholder="公司名或岗位名" clearable></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 150px">
              <el-option v-for="(label, val) in statusMap" :key="val" :label="label" :value="parseInt(val + '')"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="success" @click="openAddDialog">新增投递</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 台账表格 -->
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="companyName" label="公司名称" width="180"></el-table-column>
        <el-table-column prop="jobTitle" label="岗位名称" width="150"></el-table-column>
        <el-table-column prop="channel" label="投递渠道" width="120"></el-table-column>
        <el-table-column label="当前状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ statusMap[scope.row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="投递时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="120"></el-table-column>
        <el-table-column prop="salaryRange" label="薪资" width="120"></el-table-column>
        
        <el-table-column label="操作" fixed="right" min-width="220">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openStatusDialog(scope.row)">改签</el-button>
            <el-button link type="warning" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button link type="success" size="small" @click="openInterviewDrawer(scope.row)">复盘</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="searchForm.size"
          v-model:current-page="searchForm.current"
          @current-change="loadData"
        />
      </div>
    </el-main>

    <!-- 新增/编辑投递弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑岗位投递' : '新增岗位投递'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="form.companyName"></el-input>
        </el-form-item>
        <el-form-item label="岗位名称" prop="jobTitle">
          <el-input v-model="form.jobTitle"></el-input>
        </el-form-item>
        <el-form-item label="投递渠道" prop="channel">
          <el-select v-model="form.channel" placeholder="如：Boss直聘, 官网, 牛客" style="width: 100%" filterable allow-create>
            <el-option label="Boss直聘" value="Boss直聘"></el-option>
            <el-option label="企业官网" value="企业官网"></el-option>
            <el-option label="牛客网" value="牛客网"></el-option>
            <el-option label="拉勾网" value="拉勾网"></el-option>
            <el-option label="猎聘" value="猎聘"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="投递时间" prop="applyTime">
          <!-- 这里统一转为 SpringBoot 解析的本地时间字符串格式 -->
          <el-date-picker v-model="form.applyTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="初始状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option v-for="(label, val) in statusMap" :key="val" :label="label" :value="parseInt(val + '')"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工作地点" prop="location">
          <el-input v-model="form.location" placeholder="选填"></el-input>
        </el-form-item>
        <el-form-item label="预期薪资" prop="salaryRange">
          <el-input v-model="form.salaryRange" placeholder="选填"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">提交保存</el-button>
      </template>
    </el-dialog>

    <!-- 状态流转弹窗 -->
    <el-dialog v-model="statusDialogVisible" title="更新台账状态" width="300px">
      <el-form>
        <el-form-item label="流转状态至">
           <el-select v-model="targetStatus" style="width: 100%">
            <el-option v-for="(label, val) in statusMap" :key="val" :label="label" :value="parseInt(val + '')"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatusUpdate" :loading="submitLoading">确定更新</el-button>
      </template>
    </el-dialog>

    <!-- 面试复盘抽屉 -->
    <el-drawer v-model="drawerVisible" title="面试记录与复盘" size="40%">
      <div style="margin-bottom: 15px;">
        <el-button type="primary" size="small" @click="openInterviewForm">新增面试记录</el-button>
      </div>

      <el-timeline v-if="interviews.length > 0">
        <el-timeline-item
          v-for="item in interviews"
          :key="item.id"
          :timestamp="formatTime(item.scheduleTime)"
          placement="top"
          :type="getInterviewType(item.recordType)"
        >
          <el-card>
            <h4>{{ recordTypeMap[item.recordType] || '其他环节' }}</h4>
            <p v-if="item.coreQuestions"><strong>核心问题：</strong>{{ item.coreQuestions }}</p>
            <p v-if="item.summary"><strong>复盘总结：</strong>{{ item.summary }}</p>
            <p v-if="item.failReason" style="color: #F56C6C"><strong>挂经/教训：</strong>{{ item.failReason }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无面试记录" />
    </el-drawer>

    <!-- 新增面试复盘弹窗 -->
    <el-dialog v-model="interviewDialogVisible" title="添加面试/笔试记录" width="500px">
      <el-form :model="interviewForm" :rules="interviewRules" ref="interviewFormRef" label-width="100px">
        <el-form-item label="环节类型" prop="recordType">
          <el-select v-model="interviewForm.recordType" style="width: 100%">
            <el-option v-for="(label, val) in recordTypeMap" :key="val" :label="label" :value="parseInt(val + '')"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日程时间" prop="scheduleTime">
          <el-date-picker v-model="interviewForm.scheduleTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="核心考点" prop="coreQuestions">
          <el-input type="textarea" :rows="3" v-model="interviewForm.coreQuestions" placeholder="如：八股文提问、手撕代码题目..."></el-input>
        </el-form-item>
        <el-form-item label="复盘感悟" prop="summary">
          <el-input type="textarea" :rows="2" v-model="interviewForm.summary" placeholder="回答不好的点，需要补充的知识..."></el-input>
        </el-form-item>
        <el-form-item label="失败教训" prop="failReason">
          <el-input type="textarea" :rows="2" v-model="interviewForm.failReason" placeholder="如果挂了或者面得很差，简单记录原因..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="interviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitInterview" :loading="submitLoading">提交保存</el-button>
      </template>
    </el-dialog>

  </el-container>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import request from '../utils/request';

const router = useRouter();

// ==== 字典定义 ====
const statusMap: Record<number, string> = {
  0: '待投递', 
  1: '已投递', 
  2: '简历初筛', 
  3: '笔试', 
  4: '一面', 
  5: '二面', 
  6: '终面', 
  7: 'Offer', 
  8: '挂/放弃'
};

const getStatusType = (status: number) => {
  if (status >= 7 && status !== 8) return 'success';
  if (status === 8) return 'info';
  if (status >= 3) return 'warning';
  return 'primary';
};

const formatTime = (isoStr: string) => {
  if (!isoStr) return '';
  return isoStr.replace('T', ' ');
};

// ==== 列表查询体系 ====
const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);

const searchForm = reactive({
  keyword: '',
  status: undefined as number | undefined,
  current: 1,
  size: 10
});

const loadData = async () => {
  loading.value = true;
  try {
    const res: any = await request.get('/applications', { params: searchForm });
    tableData.value = res.records;
    total.value = res.total;
  } catch (error) {
    // 拦截器已处理过报错弹窗
  } finally {
    loading.value = false;
  }
};

// ==== 新增与表单体系 ====
const dialogVisible = ref(false);
const submitLoading = ref(false);
const formRef = ref<FormInstance>();

const form = reactive({
  id: undefined as number | undefined,
  companyName: '',
  jobTitle: '',
  channel: '',
  status: 1, // 默认'已投递'
  applyTime: '',
  location: '',
  salaryRange: '',
  jobType: 1, // 默认校招
});

const rules = reactive<FormRules>({
  companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
  jobTitle: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  channel: [{ required: true, message: '请选择或输入投递渠道', trigger: 'change' }],
  applyTime: [{ required: true, message: '请选择投递时间', trigger: 'change' }],
  status: [{ required: true, message: '请选择当前阶段状态', trigger: 'change' }],
});

const openAddDialog = () => {
  if(formRef.value) formRef.value.resetFields();
  form.id = undefined;
  
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  form.applyTime = now.toISOString().slice(0, 19);
  
  dialogVisible.value = true;
};

const openEditDialog = (row: any) => {
  if(formRef.value) formRef.value.resetFields();
  // 回填数据
  form.id = row.id;
  form.companyName = row.companyName;
  form.jobTitle = row.jobTitle;
  form.channel = row.channel;
  form.status = row.status;
  form.applyTime = row.applyTime;
  form.location = row.location;
  form.salaryRange = row.salaryRange;
  
  dialogVisible.value = true;
};

const submitForm = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        if (form.id) {
          // 编辑模式
          await request.put(`/applications/${form.id}`, form);
          ElMessage.success('更新台账成功！');
        } else {
          // 新增模式
          await request.post('/applications', form);
          ElMessage.success('录入台账成功！');
        }
        dialogVisible.value = false;
        loadData(); // 刷新数据
      } catch (error) { } 
      finally { submitLoading.value = false; }
    }
  });
};

// ==== 状态快捷流转体系 ====
const statusDialogVisible = ref(false);
const currentOperateId = ref<number | undefined>();
const targetStatus = ref<number | undefined>();

const openStatusDialog = (row: any) => {
  currentOperateId.value = row.id;
  targetStatus.value = row.status;
  statusDialogVisible.value = true;
};

const submitStatusUpdate = async () => {
  if (targetStatus.value === undefined || !currentOperateId.value) return;
  submitLoading.value = true;
  try {
    await request.put(`/applications/${currentOperateId.value}/status`, { status: targetStatus.value });
    ElMessage.success('阶段状态更新成功');
    statusDialogVisible.value = false;
    loadData();
  } catch (error) { }
  finally { submitLoading.value = false; }
};

// ==== 删除体系 ====
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要删除该条投递记录吗？(支持找回，仅防误触)', '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await request.delete(`/applications/${id}`);
      ElMessage.success('记录已移除');
      loadData();
    } catch (error) { }
  }).catch(() => { });
};

// ==== 面试复盘与记录体系 (新引入) ====
const drawerVisible = ref(false);
const interviews = ref<any[]>([]);
const currentAppId = ref<number | undefined>();

const recordTypeMap: Record<number, string> = {
  1: '笔试/机试', 
  2: '一面(技术/初试)', 
  3: '二面(技术/复试)', 
  4: '终面(技术总监)', 
  5: 'HR面', 
  6: '主管面', 
  7: '其他'
};

const getInterviewType = (type: number) => {
  return type === 1 ? 'info' : type >= 5 ? 'success' : 'primary';
};

const loadInterviews = async (appId: number) => {
  try {
    const res: any = await request.get(`/interviews/application/${appId}`);
    interviews.value = res || [];
  } catch (error) {}
};

const openInterviewDrawer = (row: any) => {
  currentAppId.value = row.id;
  drawerVisible.value = true;
  loadInterviews(row.id);
};

const interviewDialogVisible = ref(false);
const interviewFormRef = ref<FormInstance>();
const interviewForm = reactive({
  applicationId: undefined as number | undefined,
  recordType: 2, // 默认一面
  scheduleTime: '',
  coreQuestions: '',
  summary: '',
  failReason: ''
});

const interviewRules = reactive<FormRules>({
  recordType: [{ required: true, message: '请选择环节类型', trigger: 'change' }],
  scheduleTime: [{ required: true, message: '请选择日程时间', trigger: 'change' }],
});

const openInterviewForm = () => {
  if (interviewFormRef.value) interviewFormRef.value.resetFields();
  interviewForm.applicationId = currentAppId.value;
  
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  interviewForm.scheduleTime = now.toISOString().slice(0, 19);

  interviewDialogVisible.value = true;
};

const submitInterview = () => {
  interviewFormRef.value?.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        await request.post('/interviews', interviewForm);
        ElMessage.success('面经与复盘记录保存成功');
        interviewDialogVisible.value = false;
        if (currentAppId.value) loadInterviews(currentAppId.value);
      } catch (error) {}
      finally { submitLoading.value = false; }
    }
  });
};

// ==== 其他 ====
const handleLogout = () => {
  localStorage.removeItem('token');
  router.push('/login');
};

// 初始化拉取数据
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
  background-color: #f1f5f9;
}

.header {
  background-color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05), 0 1px 2px rgba(0,0,0,0.05);
  position: relative;
  z-index: 10;
}

.header h2 {
  color: #1e293b;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

:deep(.el-main) {
  padding: 24px 30px;
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.toolbar {
  background-color: #fff;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.el-form-item {
  margin-bottom: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  background: #fff;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

/* 优化表格样式 */
:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

:deep(.el-table th.el-table__cell) {
  background-color: #f8fafc;
  color: #475569;
  font-weight: 600;
  height: 50px;
}
</style>
