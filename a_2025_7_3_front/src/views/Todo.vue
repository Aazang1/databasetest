<template>
  <div class="todo-container">
    <div class="todo-header">
      <h1>待办事项</h1>
      <el-button type="primary" @click="showCreateDialog = true">新增待办</el-button>
    </div>

    <el-table :data="todos" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="priority" label="优先级" />
      <el-table-column prop="dueDate" label="截止日期" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreateDialog" :title="isEditing ? '编辑待办' : '新增待办'">
      <el-form :model="currentTodo" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="currentTodo.title" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="currentTodo.description" type="textarea" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="currentTodo.status" placeholder="请选择状态">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="currentTodo.priority" placeholder="请选择优先级">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker v-model="currentTodo.dueDate" type="datetime" placeholder="选择截止日期" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import TodoAPI from '@/api/todo'
import { ElMessage, ElMessageBox } from "element-plus";

const username = localStorage.getItem('username');
const todos = ref([])
const showCreateDialog = ref(false)
const isEditing = ref(false)
const currentTodo = ref({
  userid:username,
  title: '',
  description: '',
  status: '未开始',
  priority: '中',
  dueDate: null
})

const fetchTodos = async () => {
  try {
    console.log("username"+username)
    todos.value = await TodoAPI.getAllTodos(username)
  } catch (error) {
    console.error('获取待办事项失败:', error)
  }
}

onMounted(fetchTodos)

const handleEdit = (todo) => {
  isEditing.value = true
  currentTodo.value = { ...todo }
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
    title: '',
    description: '',
    status: 'NOT_STARTED',
    priority: 'MEDIUM',
    dueDate: null
  }
  isEditing.value = false
}

const handleSubmit = async () => {
  try {
    console.log(currentTodo.value)
    if (isEditing.value) {
      await TodoAPI.updateTodo(currentTodo.value.id, currentTodo.value)
    } else {
      await TodoAPI.createTodo(currentTodo.value)
    }
    ElMessage.success("操作成功")
    showCreateDialog.value = false
    await fetchTodos()
    resetForm()
  } catch (error) {
    console.error('保存待办事项失败:', error)
  }
}
</script>

<style scoped>
/* 样式保持不变 */
</style>
