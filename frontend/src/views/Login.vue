<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>Job Hunt Manager (V1)</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>

        <el-form-item>
          <div class="action-buttons">
            <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
            <el-button @click="handleRegister" :loading="loading">注册</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import request from '../utils/request';

const router = useRouter();
const formRef = ref<FormInstance>();

const form = reactive({
  username: '',
  password: '',
});

const rules = reactive<FormRules>({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
});

const loading = ref(false);

const handleLogin = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res: any = await request.post('/auth/login', form);
        localStorage.setItem('token', res.token);
        ElMessage.success('登录成功');
        router.push('/');
      } catch (error) {
        // 异常已在拦截器处理
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleRegister = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await request.post('/auth/register', form);
        ElMessage.success('注册成功，请直接点击登录');
      } catch (error) {
        // 异常已在拦截器处理
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-weight: bold;
  font-size: 1.2rem;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
</style>
