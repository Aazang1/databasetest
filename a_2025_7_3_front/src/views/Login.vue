<template>
  <div class="auth-container">
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
    </div>
    <el-card class="auth-card">
      <div class="logo">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 4C13.0609 4 14.0783 4.42143 14.8284 5.17157C15.5786 5.92172 16 6.93913 16 8V10H8V8C8 6.93913 8.42143 5.92172 9.17157 5.17157C9.92172 4.42143 10.9391 4 12 4Z" fill="#409EFF"/>
          <path d="M4 10V18C4 18.5304 4.21071 19.0391 4.58579 19.4142C4.96086 19.7893 5.46957 20 6 20H18C18.5304 20 19.0391 19.7893 19.4142 19.4142C19.7893 19.0391 20 18.5304 20 18V10H4Z" fill="#409EFF"/>
          <path d="M12 12C12.5523 12 13 12.4477 13 13V15C13 15.5523 12.5523 16 12 16C11.4477 16 11 15.5523 11 15V13C11 12.4477 11.4477 12 12 12Z" fill="#FFFFFF"/>
        </svg>
      </div>
      <h2 class="title">欢迎登录</h2>
      <el-form :model="form" :rules="rules" ref="loginForm" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              class="custom-input"
              show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              native-type="submit"
              :loading="loading"
              class="login-btn"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        还没有账号？<router-link to="/register" class="register-link">立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import AuthAPI from '@/api/auth'
import {ElMessage} from "element-plus";

const store = useStore()
const router = useRouter()
const loginForm = ref(null)
const loading = ref(false)
const res =ref({})

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
    await loginForm.value.validate()
    loading.value = true
    const res = await AuthAPI.login(form.value)
    console.log(res)

    localStorage.setItem('username', form.value.username)
    localStorage.setItem('password', form.value.password)

  if(res==="登录成功") {
    ElMessage.success("登录成功")
    const response =await  AuthAPI.getusers(form.value.username)
    localStorage.setItem('name', response.name)
    console.log(response)

    await router.push('/test')
  }
  else if(res==="用户不存在")
  {
    ElMessage.error("用户不存在")

  }
  else
  {
    ElMessage.error("密码错误")

  }



}


</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  position: relative;
  overflow: hidden;
}

.background {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(45deg, #e6f7ff, #b3e0ff);
}

.shape:first-child {
  height: 300px;
  width: 300px;
  top: -100px;
  left: -100px;
}

.shape:last-child {
  height: 200px;
  width: 200px;
  bottom: -50px;
  right: -50px;
}

.auth-card {
  width: 380px;
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 2;
  background: rgba(255, 255, 255, 0.95);
  border: none;
}

.logo {
  text-align: center;
  margin-bottom: 20px;
}

.logo svg {
  width: 60px;
  height: 60px;
}

.title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 500;
}

.custom-input {
  border-radius: 8px;
}

.custom-input :deep(.el-input__inner) {
  height: 44px;
  line-height: 44px;
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  letter-spacing: 2px;
  margin-top: 10px;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.register-link {
  color: #409EFF;
  text-decoration: none;
  margin-left: 5px;
  font-weight: 500;
}

.register-link:hover {
  text-decoration: underline;
}
</style>
