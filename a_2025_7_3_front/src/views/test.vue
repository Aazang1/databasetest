<template>
  <div class="home-container">
    <h1 class="welcome-title">欢迎回来, {{ username }}</h1>

    <!-- 统计卡片区域 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover" class="stat-card">
            <div class="card-content">
              <div class="card-icon todo-icon">
                <i class="el-icon-notebook-2"></i>
              </div>
              <div class="card-text">
                <h3>待办事项</h3>
                <p class="stat-value">{{ todoCount }}</p>
                <el-link type="primary" :underline="false" @click="$router.push('/todos')">
                  查看全部 <i class="el-icon-arrow-right"></i>
                </el-link>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="stat-card">
            <div class="card-content">
              <div class="card-icon notification-icon">
                <i class="el-icon-bell"></i>
              </div>
              <div class="card-text">
                <h3>未读通知</h3>
                <p class="stat-value" :class="{ 'has-unread': unreadCount > 0 }">
                  {{ unreadCount }}
                </p>
                <el-link type="primary" :underline="false" @click="$router.push('/notifications')">
                  查看全部 <i class="el-icon-arrow-right"></i>
                </el-link>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近待办区域 -->
    <div class="recent-section">
      <div class="section-header">
        <h2>最近待办</h2>
        <el-link type="primary" :underline="false" @click="$router.push('/todos')">
          查看更多 <i class="el-icon-arrow-right"></i>
        </el-link>
      </div>
      <el-table :data="recentTodos" style="width: 100%" empty-text="暂无待办事项">
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{row}">
            <el-tag :type="row.status === '已完成' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="截止日期" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{row}">
            <el-button size="mini" @click="handleTodoDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 最近通知区域 -->
    <div class="recent-section">
      <div class="section-header">
        <h2>最近通知</h2>
        <el-link type="primary" :underline="false" @click="$router.push('/notifications')">
          查看更多 <i class="el-icon-arrow-right"></i>
        </el-link>
      </div>
      <div class="notification-list">
        <div v-for="item in recentNotifications" :key="item.id" class="notification-item">
          <div class="notification-content">
            <i class="el-icon-message" :class="{ 'unread': item.read === false }"></i>
            <div>
              <p class="notification-title">{{ item.title }}</p>
              <p class="notification-time">{{ formatTime(item.createdAt) }}</p>
            </div>
          </div>
          <el-tag v-if="item.read === false" type="danger" size="mini">未读</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useStore} from 'vuex'
import TodoAPI from '@/api/todo'
import NotificationAPI from "@/api/notification.js"

const store = useStore()
const router = useRouter()
const recentTodos = ref([])
const unreadTasks = ref([])
const recentNotifications = ref([])
const username = localStorage.getItem('username')

// 统计未完成待办
const todoCount = computed(() =>
    recentTodos.value.filter(todo =>
        todo.status !== "已完成" && todo.id
    ).length
)

// 统计未读通知 - 修复了字符串和布尔值比较问题
const unreadCount = computed(() =>
    unreadTasks.value.filter(notification =>
        notification.read === false || notification.read === "false"
    ).length
)

// 格式化时间显示
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString()
}

// 处理待办详情
const handleTodoDetail = (todo) => {
  router.push(`/todo/${todo.id}`)
}

onMounted(async () => {
  try {
    // 获取待办事项
    const todos = await TodoAPI.getAllTodos(username)
    recentTodos.value = todos.slice(0, 5)

    // 获取通知
    const res = await NotificationAPI.getAllNotifications(username)
    unreadTasks.value = res
    recentNotifications.value = res.slice(0, 3)

    console.log('通知数据:', unreadTasks.value)
  } catch (error) {
    console.error('初始化数据失败:', error)
  }
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.welcome-title {
  color: #303133;
  margin-bottom: 30px;
  font-size: 28px;
}

.stats-container {
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 28px;
  color: white;
}

.todo-icon {
  background-color: #67C23A;
}

.notification-icon {
  background-color: #409EFF;
}

.card-text h3 {
  margin: 0 0 5px 0;
  color: #606266;
  font-size: 16px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin: 10px 0;
  color: #303133;
}

.has-unread {
  color: #F56C6C;
}

.recent-section {
  margin-bottom: 30px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.notification-list {
  margin-top: 15px;
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.notification-content {
  display: flex;
  align-items: center;
}

.notification-content i {
  font-size: 20px;
  margin-right: 15px;
  color: #909399;
}

.notification-content .unread {
  color: #F56C6C;
}

.notification-title {
  margin: 0;
  color: #303133;
}

.notification-time {
  margin: 5px 0 0;
  font-size: 12px;
  color: #909399;
}
</style>
