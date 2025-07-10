<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <div class="auth-header">
        <h2>欢迎注册</h2>
        <p>加入我们，开启您的旅程</p>
      </div>
      <el-form
          :model="form"
          :rules="rules"
          ref="registerForm"
          @submit.prevent="handleRegister"
          class="auth-form"
      >
        <el-form-item label="姓名" prop="name">
          <el-input
              v-model="form.name"
              placeholder="姓名"
              prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="form.username"
              placeholder="用户名"
              prefix-icon="UserFilled"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
              v-model="form.email"
              placeholder="邮箱"
              prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              native-type="submit"
              :loading="loading"
              class="submit-btn"
          >
            立即注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        <span>已有账号？</span>
        <router-link to="/" class="login-link">立即登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthAPI from '@/api/auth'
import { ElMessage } from "element-plus";

const router = useRouter()
const registerForm = ref(null)
const loading = ref(false)

const form = ref({
  name: '',
  username: '',
  email: '',
  password: ''
})

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6个字符', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await registerForm.value.validate()
    loading.value = true

    await AuthAPI.register(form.value)
    ElMessage.success("注册成功")

    await router.push('/')
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error(error.response?.data?.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: none;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
}

.auth-header h2 {
  color: #303133;
  font-size: 24px;
  margin-bottom: 8px;
}

.auth-header p {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.auth-form {
  padding: 0 20px;
}

:deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 10px;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  color: #909399;
  font-size: 14px;
}

.login-link {
  color: #409eff;
  text-decoration: none;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-input__inner) {
  padding-left: 36px;
}
</style>
