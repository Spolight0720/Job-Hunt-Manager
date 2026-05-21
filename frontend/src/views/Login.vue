<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="brand">
        <div class="logo-circle">
          <span>🤪</span>
        </div>
        <h2>Job-Hunt-Manager</h2>
        <p>投简历 · 等Offer · 不内耗</p>
      </div>
      <el-card class="login-card" shadow="never">
        <el-form :model="form" :rules="rules" ref="formRef" size="large">
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="请输入你的求职大名"
              class="custom-input"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              type="password" 
              v-model="form.password" 
              placeholder="输入你的上岸密码" 
              show-password 
              @keyup.enter="handleLogin"
              class="custom-input"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              class="submit-btn" 
              @click="handleLogin" 
              :loading="loading"
            >立 即 上 岸</el-button>
          </el-form-item>
          <div class="bottom-action">
            <span>还没开户？</span>
            <el-button link type="primary" @click="handleRegister" :loading="loading">注册求职账号</el-button>
          </div>
        </el-form>
      </el-card>
    </div>
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
        ElMessage.success('登录成功！祝你早日上岸～');
        router.push('/');
      } catch (error) {
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
        ElMessage.success('注册成功！');
      } catch (error) {
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
/* 1. 背景优化：清晰度提升 + 透明度调整 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  /* 图片路径保持你原来的配置 */
  background: url('/background.png') center center / cover no-repeat;
  /* 降低遮罩透明度，提升背景清晰度 */
  background-color: rgba(0, 0, 0, 0.45);
  background-blend-mode: darken;
  /* 提升渲染清晰度 */
  image-rendering: -webkit-optimize-contrast;
  image-rendering: crisp-edges;
}

.login-wrapper {
  width: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transform: translateY(-5%);
}

.brand {
  text-align: center;
  margin-bottom: 30px;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

.logo-circle {
  width: 70px;
  height: 70px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: #ff7a9c;
  font-size: 30px;
  font-weight: bold;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.brand h2 {
  font-size: 28px;
  margin: 0 0 8px;
  font-weight: 700;
}

.brand p {
  margin: 0;
  font-size: 15px;
  opacity: 0.95;
}

/* 2. 卡片透明度调整：降低透明度，更清晰看到背景 */
.login-card {
  width: 100%;
  border-radius: 20px;
  border: none;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15);
}

:deep(.login-card .el-card__body) {
  padding: 35px 30px;
}

/* 3. 输入框优化：背景更透明，和整体风格统一 */
:deep(.custom-input .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.7);
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 4. 按钮配色优化：更柔和的渐变，和秋招主题匹配 */
.submit-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  border-radius: 10px;
  margin-bottom: 8px;
  background: linear-gradient(90deg, #ffb6c1, #ffc0cb);
  border: none;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.3);
}

.bottom-action {
  text-align: center;
  font-size: 14px;
  color: #666;
}
</style>