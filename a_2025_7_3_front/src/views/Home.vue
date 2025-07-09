<template>
  <el-container class="layout-container">
    <!-- 左侧导航栏 -->
    <el-aside width="200px" class="aside">
      <div class="logo">
        <span>个人待办与通知管理系统</span>
      </div>
      <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
      >
        <!-- 首页 -->
        <el-menu-item index="/test">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <!-- 待办事项 -->
        <el-sub-menu>
          <template #title>
            <el-icon><Calendar /></el-icon>
            <span>待办事项管理</span>
          </template>

          <el-menu-item index="/todos">
            <el-icon><List /></el-icon>
            <span>我的待办</span>
          </el-menu-item>

          <el-menu-item index="/todos/settime">
            <el-icon><AlarmClock /></el-icon>
            <span>定时待办管理</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 通知管理 -->
        <el-sub-menu index="notifications-group">
          <template #title>
            <el-icon><Bell /></el-icon>
            <span>通知管理</span>
          </template>

          <el-menu-item index="/notifications/receive">
            <el-icon><Download /></el-icon>
            <span>我收到的通知</span>
          </el-menu-item>

          <el-menu-item index="/notifications/send">
            <el-icon><Upload /></el-icon>
            <span>我发出的通知</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/test' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <!-- 消息通知组件 -->
          <message-notification class="notification-icon" />

          <!-- 用户角色显示 -->
<!--          <el-tag class="role-tag" type="info" effect="plain">{{ userRole }}</el-tag>-->

          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
<!--              <span class="username">{{ realName }}</span>-->
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">个人信息</el-dropdown-item>
                <el-dropdown-item>修改密码</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  List,
  Document,
  DataLine,
  Notebook, Clock, CircleCheck, HomeFilled, Calendar, Bell, Download, Upload, AlarmClock
} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";


const route = useRoute()
const router = useRouter()
// 弹出框相关
const dialogVisible = ref(false)
// 选项卡激活状态

const username = localStorage.getItem('username');
// 当前激活的菜单
const activeMenu = computed(() => {
  // 根据查询参数设置不同的激活菜单
  if (route.path === '/maintenance' && route.query.type) {
    return `/maintenance?type=${route.query.type}`
  }
  return route.path
})
// 打开弹出框
const openDialog = () => {
  dialogVisible.value = true
}
// 当前路由信息
const currentRoute = computed(() => {
  return route
})

// 查看个人信息
const handleProfile = () => {
  router.push('/profile')
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  ElMessage.success("退出成功")
  router.push('/')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #2b2f3a;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.logo span {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.notification-icon {
  margin-right: 20px;
}

.role-tag {
  margin-right: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 10px;
  color: #606266;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
