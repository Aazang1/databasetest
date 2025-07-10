<template>
  <div class="notification-app">
    <div class="app-header">
      <h1 class="app-title">通知中心</h1>
    </div>

    <div class="notification-content">
      <el-card shadow="hover" class="notification-card">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="全部通知" name="all">
            <el-table
                :data="notifications"
                style="width: 100%"
                stripe
                row-class-name="notification-row"
                v-loading="loading"
            >
              <el-table-column prop="title" label="标题" width="180">
                <template #default="{row}">
                  <span :class="{'unread-title': !row.read}">{{ row.title }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="content" label="内容" />
              <el-table-column prop="createdAt" label="发布时间" width="180">
                <template #default="scope">
                  {{ formatDate(scope.row.createdAt) }}
                </template>
              </el-table-column>

              <el-table-column prop="readAt" label="状态" width="120">
                <template #default="{row}">
                  <el-tag
                      :type="row.read ? 'info' : 'danger'"
                      :effect="row.read ? 'light' : 'dark'"
                      round
                  >
                    {{ row.read ? '已读' : '未读' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="senderName" label="发布人" width="120" />
              <el-table-column label="操作" width="200" align="center">
                <template #default="scope">

                  <el-button
                      size="small"
                      @click="showNotificationDetail(scope.row.notificationId)"
                      class="action-btn"
                  >
                    <el-icon><View /></el-icon> 查看详情
                  </el-button>
                  <el-button
                      size="small"
                      @click="markAsRead(scope.row.id)"
                      v-if="!scope.row.read"
                      class="action-btn"
                  >
                    <el-icon><Reading /></el-icon> 标记已读
                  </el-button>
                  <el-button
                      size="small"
                      type="danger"
                      @click="confirmDelete(scope.row.id)"
                      class="action-btn"
                  >
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="未读通知" name="unread">
            <el-table
                :data="unreadNotifications"
                style="width: 100%"
                stripe
                row-class-name="notification-row"
                v-loading="loading"
            >
              <el-table-column prop="title" label="标题" width="180">
                <template #default="{row}">
                  <span class="unread-title">{{ row.title }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="content" label="内容" />
              <el-table-column prop="senderid" label="发布人" width="120" />
              <el-table-column prop="createdAt" label="发布时间" width="180" />
              <el-table-column label="操作" width="200" align="center">
                <template #default="scope">
                  <el-button
                      size="small"
                      @click="markAsRead(scope.row.id)"
                      class="action-btn"
                  >
                    <el-icon><Reading /></el-icon> 标记已读
                  </el-button>
                  <el-button
                      size="small"
                      type="danger"
                      @click="confirmDelete(scope.row.id)"
                      class="action-btn"
                  >
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 发布通知对话框 -->
    <el-dialog
        v-model="showCreateDialog"
        title="发布通知"
        width="50%"
        class="notification-dialog"
        :close-on-click-modal="false"
    >
      <el-form
          :model="newNotification"
          :rules="rules"
          ref="notificationForm"
          label-width="100px"
          label-position="top"
      >
        <el-form-item label="通知标题" prop="title">
          <el-input
              v-model="newNotification.title"
              placeholder="请输入通知标题"
              clearable
          />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input
              v-model="newNotification.content"
              type="textarea"
              :rows="4"
              placeholder="请输入通知内容"
              resize="none"
          />
        </el-form-item>
        <el-form-item label="通知类型" prop="type">
          <el-select
              v-model="newNotification.type"
              placeholder="请选择通知类型"
              class="full-width"
          >
            <el-option label="系统通知" value="系统通知" />
            <el-option label="提醒通知" value="提醒通知" />
            <el-option label="公告" value="公告" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收人员" prop="receiverIds">
          <el-select
              v-model="newNotification.receiverid"
              multiple
              filterable
              placeholder="请选择接收人员"
              class="full-width"
          >
            <el-option
                v-for="user in userList"
                :key="user.id"
                :label="user.username"
                :value="user.username"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleCreate" size="large">发布</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加待办对话框 -->
    <el-dialog
        v-model="showCreateToduDialog"
        :title="isEditing ? '编辑待办' : '新增待办'"
        width="600px"
        class="todo-dialog"
    >
      <el-form :model="currentTodo" label-width="100px" label-position="top">
        <el-form-item label="标题" prop="title" required>
          <el-input v-model="currentTodo.title" placeholder="请输入待办事项标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
              v-model="currentTodo.description"
              type="textarea"
              :rows="3"
              placeholder="请输入详细描述"
              resize="none"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status" required>
          <el-select
              v-model="currentTodo.status"
              placeholder="请选择状态"
              class="full-width"
          >
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority" required>
          <el-select
              v-model="currentTodo.priority"
              placeholder="请选择优先级"
              class="full-width"
          >
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker
              v-model="currentTodo.dueDate"
              type="datetime"
              placeholder="选择截止日期"
              class="full-width"
              :shortcuts="dateShortcuts"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateToduDialog = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleSubmit" size="large">确认</el-button>
        </span>
      </template>
    </el-dialog>



    <el-dialog
        v-model="showDetailDialog"
        title="通知详情"
        width="50%"
        class="detail-dialog"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="detail-content" v-loading="loading">
        <h3 class="detail-title">{{ currentNotification.title }}</h3>
        <div class="detail-meta">
      <span class="detail-sender">
        <el-icon><User /></el-icon>
        发布人: {{ currentNotification.senderid || '系统' }}
      </span>
          <el-divider direction="vertical" />
          <span class="detail-time">
        <el-icon><Clock /></el-icon>
        发布时间: {{ formatDate(currentNotification.createdAt) }}
      </span>
          <el-divider direction="vertical" />
          <el-tag
              :type="currentNotification.read ? 'info' : 'danger'"
              class="detail-status"
              :icon="currentNotification.read ? 'CircleCheck' : 'Warning'"
          >
            {{ currentNotification.read ? '已读' : '未读' }}
          </el-tag>
        </div>
        <el-divider />
        <div class="detail-body">
          <el-scrollbar height="300px">
            <pre style="white-space: pre-wrap; font-family: inherit;">{{ currentNotification.content }}</pre>
          </el-scrollbar>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button
              @click="showDetailDialog = false"
              size="large"
              plain
          >
            <el-icon><Close /></el-icon> 关闭
          </el-button>
          <el-button
              type="primary"
              @click="markAsReadAndClose(currentNotification.id)"
              size="large"
              v-if="!currentNotification.read"
          >
            <el-icon><CircleCheck /></el-icon> 标记已读并关闭
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import NotificationAPI, {getNotificationsById} from '@/api/notification'
import UserAPI from '@/api/auth.js'
import { ElMessage, ElMessageBox } from "element-plus";
import TodoAPI from "@/api/todo.js";
import {Plus, Reading, Delete, View, User, Clock, Close, CircleCheck} from '@element-plus/icons-vue'

const store = useStore()
const activeTab = ref('all')
const showCreateDialog = ref(false)
const loading = ref(false)
const username = localStorage.getItem('username');

const newNotification = ref({
  senderid: username,
  title: '',
  content: '',
  type: '系统通知',
  receiverid: ''
})

const userList = ref([])
const notifications = ref([])
const unreadNotifications = computed(() => {
  return notifications.value.filter(notification => notification.read === false);
});

const rules = {
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { max: 50, message: '标题长度不能超过50个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ],
  receiverIds: [
    { required: true, message: '请选择至少一个接收人', trigger: 'change' }
  ]
}

const dateShortcuts = [
  {
    text: '今天',
    value: new Date(),
  },
  {
    text: '明天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24)
      return date
    },
  },
  {
    text: '一周后',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 7)
      return date
    },
  },
]

const fetchUsers = async () => {
  try {
    loading.value = true
    const res = UserAPI.getalluesrs()
    userList.value = await res
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const fetchNotifications = async () => {
  try {
    loading.value = true
    const res = NotificationAPI.getAllNotifications(username);
    notifications.value = await res;
  } catch (error) {
    console.error('获取通知失败:', error)
    ElMessage.error('获取通知失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    fetchUsers(),
    fetchNotifications()
  ])
})

const markAsRead = async (id) => {
  try {
    await NotificationAPI.markAsRead(id)
    store.commit('markNotificationAsRead', id)
    ElMessage.success('标记为已读成功')
  } catch (error) {
    console.error('标记为已读失败:', error)
    ElMessage.error('标记为已读失败')
  }
  await fetchNotifications()
}

const showCreateToduDialog = ref(false)
const isEditing = ref(false)
const currentTodo = ref({
  userid: username,
  title: '',
  description: '',
  status: '未开始',
  priority: '中',
  dueDate: null
})

const handleEdit = (todo) => {
  isEditing.value = true
  currentTodo.value = { ...todo }
  showCreateToduDialog.value = true
}

const confirmDelete = (id) => {
  ElMessageBox.confirm(
      '确定要删除这条通知吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'delete-confirm-dialog'
      }
  ).then(() => {
    handleDelete(id)
  }).catch(() => {
    // 用户点击了取消，不做任何操作
  })
}

const handleDelete = async (id) => {
  try {
    await NotificationAPI.deleteNotification(id)
    store.commit('removeNotification', id)
    ElMessage.success('删除成功')
    await fetchNotifications()
  } catch (error) {
    console.error('删除通知失败:', error)
    ElMessage.error('删除失败')
  }
}

const handleCreate = async () => {
  try {
    if (!newNotification.value.title.trim()) {
      ElMessage.warning('请填写通知标题')
      return
    }

    const res = await NotificationAPI.createNotification(newNotification.value)
    console.log(res);
    showCreateDialog.value = false
    await fetchNotifications()
    newNotification.value = {
      senderid: username,
      title: '',
      content: '',
      type: '系统通知',
      receiverid: ''
    }
    ElMessage.success("发布成功")
  } catch (error) {
    console.error('发布通知失败:', error)
    ElMessage.error('发布失败')
  }
}

const handleSubmit = async () => {
  try {
    if (!currentTodo.value.title.trim()) {
      ElMessage.warning('请填写待办标题')
      return
    }

    await TodoAPI.createTodo(currentTodo.value)
    ElMessage.success("操作成功")
    showCreateToduDialog.value = false
    currentTodo.value = {
      userid: username,
      title: '',
      description: '',
      status: '未开始',
      priority: '中',
      dueDate: null
    }
  } catch (error) {
    console.error('保存待办事项失败:', error)
    ElMessage.error('操作失败')
  }
}


const showDetailDialog = ref(false)
const currentNotification = ref({
  id: null,
  title: '',
  content: '',
  type: '',
  createdAt: '',
  senderid: '',
  read: false
})
const formatDateTime = (dateString) => {
  if (!dateString) return '未知时间'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).replace(/\//g, '-')
}
// 查看详情方法

const formatDate = (dateString) => {
  if (!dateString) return '未设置'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}
const showNotificationDetail = async (id) => {
  try {
    loading.value = true
    const res = await NotificationAPI.getNotificationsById(id)
    currentNotification.value = res
    showDetailDialog.value = true
  } catch (error) {
    console.error('获取通知详情失败:', error)
    ElMessage.error('获取通知详情失败')
  } finally {
    loading.value = false
  }
}

// 标记已读并关闭方法
const markAsReadAndClose = async (id) => {
  try {
    await markAsRead(id)
    showDetailDialog.value = false
  } catch (error) {
    console.error('操作失败:', error)
  }
}
</script>

<style scoped>
.notification-app {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.app-title {
  color: #303133;
  font-size: 24px;
  font-weight: 500;
  margin: 0;
}

.add-btn {
  font-weight: 500;
}

.notification-content {
  margin-top: 20px;
}

.notification-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.notification-row {
  transition: all 0.3s;
}

.notification-row:hover {
  background-color: #f5f7fa !important;
}

.unread-title {
  font-weight: bold;
  color: #303133;
}

.action-btn {
  padding: 5px 10px;
}

.full-width {
  width: 100%;
}

.custom-tabs .el-tabs__header {
  margin: 0;
}

.custom-tabs .el-tabs__nav-wrap::after {
  height: 1px;
}

.notification-dialog .el-dialog__header {
  border-bottom: 1px solid #ebeef5;
  margin-right: 0;
}

.notification-dialog .el-dialog__footer {
  border-top: 1px solid #ebeef5;
  padding: 16px 20px;
}

.todo-dialog .el-dialog__header {
  border-bottom: 1px solid #ebeef5;
  margin-right: 0;
}

.todo-dialog .el-dialog__footer {
  border-top: 1px solid #ebeef5;
  padding: 16px 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

<style>
.delete-confirm-dialog .el-message-box__content {
  padding: 20px;
}

.delete-confirm-dialog .el-message-box__btns {
  padding: 10px 20px;
}

.notification-card .el-card__body {
  padding: 0;
}

.el-table .cell {
  word-break: break-word;
}

.detail-dialog {
  border-radius: 8px;
}

.detail-content {
  padding: 0 20px;
}

.detail-title {
  color: #303133;
  font-size: 18px;
  margin-bottom: 16px;
  text-align: center;
}

.detail-meta {
  display: flex;
  align-items: center;
  color: #909399;
  font-size: 14px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-body {
  color: #606266;
  line-height: 1.6;
  font-size: 15px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
