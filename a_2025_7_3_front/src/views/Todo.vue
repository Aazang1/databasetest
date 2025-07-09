<template>
  <div class="todo-app">
    <div class="app-header">
      <h1 class="app-title">待办事项管理</h1>
      <el-button type="primary" size="large" @click="showCreateDialog = true" class="add-btn">
        <el-icon><Plus /></el-icon> 新增待办
      </el-button>
    </div>

    <div class="todo-content">
      <el-card shadow="hover" class="todo-card">
        <el-table :data="todos" style="width: 100%" stripe row-class-name="todo-row">
          <el-table-column label="完成" width="80" align="center">
            <template #default="{row}">
              <el-checkbox
                  v-model="row.status"
                  :true-label="'已完成'"
                  :false-label="'未开始'"
                  @change="toggleTodoStatus(row)"
                  class="status-checkbox"
              />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" width="180">
            <template #default="{row}">
              <span :class="{
                'todo-title': true,
                'completed': row.status === '已完成',
                'high-priority': row.priority === '高',
                'medium-priority': row.priority === '中',
                'low-priority': row.priority === '低'
              }">
                {{ row.title }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="status" label="状态" width="120" sortable>
            <template #header>
              <div class="sort-header">
                <span>状态</span>
                <div class="sort-icons">
                  <el-icon @click="sortBy('status', 'asc')"><CaretTop /></el-icon>
                  <el-icon @click="sortBy('status', 'desc')"><CaretBottom /></el-icon>
                </div>
              </div>
            </template>
            <template #default="{row}">
              <el-tag :type="getStatusTagType(row.status)" effect="light" round>
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="priority" label="优先级" width="120" sortable>
            <template #header>
              <div class="sort-header">
                <span>优先级</span>
                <div class="sort-icons">
                  <el-icon @click="sortBy('priority', 'asc')"><CaretTop /></el-icon>
                  <el-icon @click="sortBy('priority', 'desc')"><CaretBottom /></el-icon>
                </div>
              </div>
            </template>
            <template #default="{row}">
              <el-tag :type="getPriorityTagType(row.priority)" effect="dark">
                {{ row.priority }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="dueDate" label="截止日期" width="180" sortable>
            <template #header>
              <div class="sort-header">
                <span>截止日期</span>
                <div class="sort-icons">
                  <el-icon @click.stop="sortBy('dueDate', 'asc')"><CaretTop /></el-icon>
                  <el-icon @click.stop="sortBy('dueDate', 'desc')"><CaretBottom /></el-icon>
                </div>
              </div>
            </template>
            <template #default="scope">
              {{ formatDate(scope.row.dueDate) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" align="center">
            <template #default="scope">
              <el-button size="small" @click="openDetailDialog(scope.row)" class="action-btn">
                <el-icon><View /></el-icon> 详情
              </el-button>
              <el-button size="small" @click="handleEdit(scope.row)" class="action-btn">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)" class="action-btn">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <el-dialog
        v-model="showCreateDialog"
        :title="isEditing ? '编辑待办事项' : '新增待办事项'"
        width="600px"
        class="todo-dialog"
        :close-on-click-modal="false"
    >
      <el-form :model="currentTodo" label-width="100px" label-position="left">
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
          <el-select v-model="currentTodo.status" placeholder="请选择状态" class="full-width">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority" required>
          <el-select v-model="currentTodo.priority" placeholder="请选择优先级" class="full-width">
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
          <el-button @click="showCreateDialog = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleSubmit" size="large">
            {{ isEditing ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>


    <el-dialog
        v-model="detailDialogVisible"
        :title="currentDetail ? currentDetail.title : '待办事项详情'"
        width="680px"
        class="detail-dialog"
        :close-on-click-modal="false"
    >
      <div v-if="currentDetail" class="detail-content">
        <el-card shadow="never" class="detail-card">
          <div class="detail-header">
            <el-tag :type="getStatusTagType(currentDetail.status)" effect="light" round class="status-tag">
              <el-icon v-if="currentDetail.status === '已完成'"><Check /></el-icon>
              <el-icon v-else-if="currentDetail.status === '进行中'"><Refresh /></el-icon>
              <el-icon v-else><Clock /></el-icon>
              {{ currentDetail.status }}
            </el-tag>
            <el-tag :type="getPriorityTagType(currentDetail.priority)" class="priority-tag">
              <el-icon><Flag /></el-icon>
              {{ currentDetail.priority }}优先级
            </el-tag>
            <div class="due-date" v-if="currentDetail.dueDate">
              <el-icon><Calendar /></el-icon>
              <span>截止: {{ formatDate(currentDetail.dueDate) }}</span>
            </div>
          </div>

          <div class="detail-body">
            <h3 class="detail-section-title">描述详情</h3>
            <div class="description-box">
              <template v-if="currentDetail.description">
                <p v-for="(paragraph, index) in currentDetail.description.split('\n')" :key="index">
                  {{ paragraph || '&nbsp;' }}
                </p>
              </template>
              <el-empty v-else description="无描述内容" :image-size="80" />
            </div>

            <el-divider />

            <div class="meta-info">
              <div class="meta-item">
                <span class="meta-label">创建时间</span>
                <span class="meta-value">{{ formatDate(currentDetail.createdAt) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">更新时间</span>
                <span class="meta-value">{{ formatDate(currentDetail.updatedAt) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false" size="large">关闭</el-button>
        <el-button
            type="primary"
            @click="handleEdit(currentDetail)"
            size="large"
            v-if="currentDetail"
        >
          <el-icon><Edit /></el-icon> 编辑
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import TodoAPI from '@/api/todo'
import { ElMessage, ElMessageBox } from "element-plus";
import {Plus, Edit, Delete, Check, Clock, Calendar, Refresh, View} from '@element-plus/icons-vue'
const username = localStorage.getItem('username');
const todos = ref([])
const showCreateDialog = ref(false)
const isEditing = ref(false)
const currentTodo = ref({
  userid: username,
  title: '',
  description: '',
  status: '未开始',
  priority: '中',
  dueDate: null
})
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

const sortBy = (field, order) => {
  todos.value.sort((a, b) => {
    // 处理状态排序（中文排序）
    if (field === 'status') {
      const statusOrder = ['未开始', '进行中', '已完成']
      const aIndex = statusOrder.indexOf(a.status)
      const bIndex = statusOrder.indexOf(b.status)
      return order === 'asc' ? aIndex - bIndex : bIndex - aIndex
    }

    // 处理优先级排序（中文排序）
    if (field === 'priority') {
      const priorityOrder = ['低', '中', '高']
      const aIndex = priorityOrder.indexOf(a.priority)
      const bIndex = priorityOrder.indexOf(b.priority)
      return order === 'asc' ? aIndex - bIndex : bIndex - aIndex
    }

    // 处理日期排序
    if (field === 'dueDate') {
      const aDate = a.dueDate ? new Date(a.dueDate).getTime() : 0
      const bDate = b.dueDate ? new Date(b.dueDate).getTime() : 0
      return order === 'asc' ? aDate - bDate : bDate - aDate
    }

    // 默认排序
    if (a[field] < b[field]) return order === 'asc' ? -1 : 1
    if (a[field] > b[field]) return order === 'asc' ? 1 : -1
    return 0
  })
}
const getStatusTagType = (status) => {
  switch(status) {
    case '未开始': return 'info'
    case '进行中': return 'warning'
    case '已完成': return 'success'
    default: return ''
  }
}

const getPriorityTagType = (priority) => {
  switch(priority) {
    case '高': return 'danger'
    case '中': return 'warning'
    case '低': return 'success'
    default: return ''
  }
}

const fetchTodos = async () => {
  try {
    todos.value = await TodoAPI.getAllTodos(username)
  } catch (error) {
    console.error('获取待办事项失败:', error)
    ElMessage.error('获取待办事项失败')
  }
}



onMounted(fetchTodos)

const toggleTodoStatus = async (todo) => {
  try {
    // 更新状态为当前复选框的值
    const newStatus = todo.status
    await TodoAPI.updateTodo(todo.id, {
      ...todo,
      status: newStatus
    })
    ElMessage.success(`已标记为${newStatus}`)
    await fetchTodos()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
    // 如果失败，恢复原来的状态
    await fetchTodos()
  }
}

const handleEdit = (todo) => {
  isEditing.value = true
  currentTodo.value = {...todo}
  showCreateDialog.value = true
}

const confirmDelete = (id) => {
  ElMessageBox.confirm(
      '确定要删除这条待办事项吗？',
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
    await TodoAPI.deleteTodo(id)
    ElMessage.success('删除成功')
    await fetchTodos()
  } catch (error) {
    console.error('删除待办事项失败:', error)
    ElMessage.error('删除失败')
  }
}

const resetForm = () => {
  currentTodo.value = {
    userid: username,
    title: '',
    description: '',
    status: '未开始',
    priority: '中',
    dueDate: null
  }
  isEditing.value = false
}

const handleSubmit = async () => {
  try {
    if (!currentTodo.value.title.trim()) {
      ElMessage.warning('请填写标题')
      return
    }

    if (isEditing.value) {
      await TodoAPI.updateTodo(currentTodo.value.id, currentTodo.value)
      ElMessage.success('更新成功')
    } else {
      await TodoAPI.createTodo(currentTodo.value)
      ElMessage.success('创建成功')
    }

    showCreateDialog.value = false
    await fetchTodos()
    resetForm()
  } catch (error) {
    console.error('保存待办事项失败:', error)
    ElMessage.error('操作失败')
  }
}
const detailDialogVisible = ref(false)
const currentDetail = ref(null)

const openDetailDialog = async (todo) => {
  try {
    // 直接从当前行数据获取详情，避免额外请求
    currentDetail.value = {
      ...todo,
      createdAt: todo.createdAt || new Date().toISOString(),
      updatedAt: todo.updatedAt || new Date().toISOString()
    }
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}


</script>

<style scoped>
.todo-app {
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

.todo-content {
  margin-top: 20px;
}

.todo-card {
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.todo-row {
  transition: all 0.3s;
}

.todo-row:hover {
  background-color: #f5f7fa !important;
}

.status-checkbox {
  transform: scale(1.2);
}

.todo-title {
  transition: all 0.3s;
}

.todo-title.completed {
  text-decoration: line-through;
  color: #909399;
  opacity: 0.8;
}

.high-priority {
  font-weight: bold;
  color: #f56c6c;
}

.medium-priority {
  font-weight: bold;
  color: #e6a23c;
}

.low-priority {
  font-weight: bold;
  color: #67c23a;
}

.action-btn {
  padding: 5px 10px;
}

.full-width {
  width: 100%;
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


.sort-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sort-icons {
  display: flex;
  flex-direction: column;
  margin-left: 8px;
}

.sort-icons .el-icon {
  font-size: 12px;
  cursor: pointer;
  color: #c0c4cc;
  transition: color 0.2s;
}

.sort-icons .el-icon:hover {
  color: #409eff;
}
</style>

<style>

.detail-content {
  padding: 0 20px;
}

.detail-dialog {
  --detail-primary-color: #409eff;
  --detail-success-color: #67c23a;
  --detail-warning-color: #e6a23c;
  --detail-danger-color: #f56c6c;
  --detail-info-color: #909399;
}

.detail-dialog .el-dialog__header {
  border-bottom: 1px solid var(--el-border-color-light);
  padding: 18px 20px;
  margin-right: 0;
}

.detail-dialog .el-dialog__headerbtn {
  top: 18px;
}

.detail-dialog .el-dialog__body {
  padding: 10px;
}

.detail-dialog .el-dialog__footer {
  border-top: 1px solid var(--el-border-color-light);
  padding: 16px 20px;
}

.detail-card {
  border: none;
  border-radius: 8px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding-bottom: 16px;
  border-bottom: 1px dashed var(--el-border-color-light);
  margin-bottom: 16px;
}

.status-tag {
  font-size: 14px;
  padding: 6px 12px;
}

.status-tag .el-icon {
  margin-right: 4px;
}

.priority-tag {
  font-size: 14px;
  padding: 6px 12px;
}

.priority-tag .el-icon {
  margin-right: 4px;
}

.due-date {
  display: flex;
  align-items: center;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-left: auto;
}

.due-date .el-icon {
  margin-right: 6px;
  font-size: 16px;
}

.detail-body {
  padding: 0 8px;
}

.detail-section-title {
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
}

.detail-section-title::before {
  content: "";
  display: inline-block;
  width: 4px;
  height: 14px;
  background-color: var(--detail-primary-color);
  border-radius: 2px;
  margin-right: 8px;
}

.description-box {
  background-color: var(--el-fill-color-lighter);
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 20px;
  min-height: 100px;
}

.description-box p {
  margin: 0 0 8px 0;
  line-height: 1.6;
  color: var(--el-text-color-primary);
}

.description-box p:last-child {
  margin-bottom: 0;
}

.meta-info {
  display: flex;
  gap: 24px;
}

.meta-item {
  display: flex;
  flex-direction: column;
}

.meta-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-bottom: 4px;
}

.meta-value {
  font-size: 14px;
  color: var(--el-text-color-primary);
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .due-date {
    margin-left: 0;
  }

  .meta-info {
    flex-direction: column;
    gap: 12px;
  }
}

.detail-dialog .el-dialog__header {
  border-bottom: 1px solid #ebeef5;
  margin-right: 0;
}

.detail-dialog .el-dialog__footer {
  border-top: 1px solid #ebeef5;
  padding: 16px 20px;
}
.delete-confirm-dialog .el-message-box__content {
  padding: 20px;
}

.delete-confirm-dialog .el-message-box__btns {
  padding: 10px 20px;
}

.todo-card .el-card__body {
  padding: 0;
}
</style>
