<template>
  <div class="notification-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我发布的通知</span>
          <el-button type="primary" @click="showCreateDialog = true">发布通知</el-button>
        </div>
      </template>

      <el-table :data="notifications" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{row}">
            <el-tag :type="getTagType(row.type)">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="senderName" label="发送者" width="120" />
        <el-table-column prop="content" label="内容" width="200" />
        <el-table-column label="接收者">
          <template #default="{row}">
            <template v-if="row.receiverNames && row.receiverNames.length > 0">
              <el-tag
                  v-for="receiver in row.receiverNames"
                  :key="receiver"
                  class="receiver-tag"
              >
                {{ receiver }}
              </el-tag>
            </template>
            <span v-else class="no-receivers">无接收者</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{row}">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 编辑/创建对话框 -->
      <el-dialog
          v-model="showCreateDialog"
          title="发布通知"
          width="50%"
          :close-on-click-modal="false"
      >
        <el-form
            :model="newNotification"
            :rules="rules"
            ref="notificationForm"
            label-width="100px"
        >
          <el-form-item label="通知标题" prop="title">
            <el-input v-model="newNotification.title" placeholder="请输入通知标题" />
          </el-form-item>
          <el-form-item label="通知内容" prop="content">
            <el-input
                v-model="newNotification.content"
                type="textarea"
                :rows="4"
                placeholder="请输入通知内容"
            />
          </el-form-item>
          <el-form-item label="通知类型" prop="type">
            <el-select
                v-model="newNotification.type"
                placeholder="请选择通知类型"
                style="width: 100%"
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
                style="width: 100%"
            >
              <el-option
                  v-for="user in userList"
                  :key="user.id"
                  :label="user.name"
                  :value="user.username"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="handleCreate">发布</el-button>
        </template>
      </el-dialog>

    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNotificationsBySender,
  createNotification,
  // updateNotification,
  deleteNotification
} from '@/api/notification'
import NotificationAPI from "@/api/notification.js";
import UserAPI from '@/api/auth.js'
const username = localStorage.getItem('username')
const notifications = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const showCreateDialog = ref(false)
const newNotification = ref({
  senderid: username,
  title: '',
  content: '',
  type: '系统通知',
  receiverid:''
})

const userList = ref([])
// 表单数据
const form = reactive({
  title: '',
  type: '',
  content: '',
  receiverid: []
})

// 接收者选项（可以根据实际情况从API获取）
const receiverOptions = ref(['user1', 'user2', 'user3', 'user4'])

// 获取通知列表
const fetchData = async () => {
  try {
    loading.value = true
    const response = await getNotificationsBySender(username)
    notifications.value = response
  } catch (error) {
    console.error('获取通知失败:', error)
    ElMessage.error('获取通知失败')
  } finally {
    loading.value = false
  }
}

// 标签类型映射
const getTagType = (type) => {
  const map = {
    '系统通知': 'primary',
    '提醒通知': 'warning',
    'SYSTEM': 'danger'
  }
  return map[type] || 'info'
}

// 创建新通知
const handleCreate = async () => {
  try {
    console.log(newNotification)
    const res= await NotificationAPI.createNotification(newNotification.value)
    console.log(res);
    showCreateDialog.value = false
    // await fetchNotifications()
    newNotification.value = {
      title: '',
      content: '',
      type: '系统通知'
    }

    ElMessage.success("发布成功")
  } catch (error) {
    console.error('发布通知失败:', error)
    ElMessage.error('发布失败')
  }
}
// 编辑通知
const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, {
    title: row.title,
    type: row.type,
    content: row.content,
    receiverid: Array.isArray(row.receiverid) ? row.receiverid : []
  })
  showCreateDialog.value = true
}

// 删除通知
const handleDelete = (row) => {
  ElMessageBox.confirm(
      `确定要删除通知 "${row.title}" 吗?`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    try {
      await deleteNotification(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消删除
  })
}


const fetchUsers = async () => {
  try {
    const res =  UserAPI.getalluesrs()
    userList.value =  await  res
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  }
}
// 重置表单
const resetForm = () => {
  Object.assign(form, {
    title: '',
    type: '',
    content: '',
    receiverid: []
  })
}

// 提交表单
const submitForm = async () => {
  if (!form.title || !form.type || !form.content) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    const notificationData = {
      title: form.title,
      type: form.type,
      content: form.content,
      senderid: username,
      receiverid: form.receiverid
    }

    if (isEdit.value) {
      await updateNotification(currentId.value, notificationData)
      ElMessage.success('更新成功')
    } else {
      await createNotification(notificationData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 初始化加载数据
fetchData()
fetchUsers()
</script>

<style scoped>
.notification-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.receiver-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.no-receivers {
  color: #999;
  font-size: 12px;
  font-style: italic;
}
</style>
