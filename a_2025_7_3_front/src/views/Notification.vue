<template>
  <div class="notification-container">
    <div class="notification-header">
      <h1>通知</h1>
      <el-button type="primary" @click="showCreateDialog = true">发布通知</el-button>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="全部通知" name="all">
        <el-table :data="notifications" style="width: 100%">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="content" label="内容" />
          <el-table-column prop="createdAt" label="发布时间" />
          <el-table-column prop="readAt" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.readAt ? 'info' : 'danger'">
                {{ scope.row.readAt ? '已读' : '未读' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="senderid" label="发布人" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" @click="markAsRead(scope.row.id)" v-if="!scope.row.readAt">标记为已读</el-button>
              <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="未读通知" name="unread">
        <el-table :data="unreadNotifications" style="width: 100%">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="content" label="内容" />
          <el-table-column prop="senderid" label="发布人" />
          <el-table-column prop="createdAt" label="发布时间" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" @click="markAsRead(scope.row.id)">标记为已读</el-button>
              <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="showCreateDialog" title="发布通知">
      <el-form :model="newNotification" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="newNotification.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="newNotification.content" type="textarea" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="newNotification.type" placeholder="请选择类型">
            <el-option label="系统通知" value="系统通知" />
            <el-option label="提醒通知" value="提醒通知" />
            <el-option label="公告" value="公告" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收人" prop="content">
          <el-input v-model="newNotification.receiverid"  />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {useStore} from 'vuex'
import NotificationAPI from '@/api/notification'
import {ElMessage, ElMessageBox} from "element-plus";

const store = useStore()
const activeTab = ref('all')
const showCreateDialog = ref(false)
const username = localStorage.getItem('username');

const newNotification = ref({
  senderid: username,
  title: '',
  content: '',
  type: '系统通知',
  receiverid:''
})

const notifications = ref([])
const unreadNotifications = computed(() => store.getters.unreadNotifications)

const fetchNotifications = async () => {
  try {
    const res = NotificationAPI.getAllNotifications(username);
    notifications.value = await res;
    console.log(notifications.value)
    console.log(res)
  } catch (error) {
    console.error('获取通知失败:', error)
  }
}

onMounted(fetchNotifications)

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

const confirmDelete = (id) => {
  ElMessageBox.confirm(
      '确定要删除这条通知吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
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
    await NotificationAPI.createNotification(newNotification.value)
    showCreateDialog.value = false
    await fetchNotifications()
    newNotification.value = {
      title: '',
      content: '',
      type: 'SYSTEM'
    }
    ElMessage.success("发布成功")
  } catch (error) {
    console.error('发布通知失败:', error)
    ElMessage.error('发布失败')
  }
}
</script>

<style scoped>
/* 样式保持不变 */
</style>
