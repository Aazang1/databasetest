<template>
  <div class="task-manager-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>Todo定时任务管理</span>
          <div>
            <el-button type="primary" @click="showCreateDialog">新增任务</el-button>
            <el-button @click="refreshTasks">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 任务列表表格 -->
      <el-table :data="tasks" v-loading="loading" style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" width="180" />
        <el-table-column prop="cronExpression" label="Cron表达式" width="150">
          <template #default="{ row }">
            <el-tooltip :content="parseCronExpression(row.cronExpression)" placement="top">
              <span>{{ row.cronExpression }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="todoTitle" label="Todo标题" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.active ? 'success' : 'danger'">
              {{ row.active ? '活跃' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="editTask(row)">编辑</el-button>
            <el-button
                size="small"
                :type="row.active ? 'warning' : 'success'"
                @click="toggleTaskStatus(row.id, !row.active)"
            >
              {{ row.active ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="confirmDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑任务对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEditMode ? '编辑任务' : '创建任务'"
        width="50%"
        @closed="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="form.taskName" placeholder="如：每日晨会提醒" />
        </el-form-item>

        <el-form-item label="执行频率" prop="cronExpression">
          <el-radio-group v-model="frequencyType" @change="handleFrequencyChange">
            <el-radio-button label="simple">简单模式</el-radio-button>
            <el-radio-button label="advanced">高级模式</el-radio-button>
          </el-radio-group>

          <!-- 简单模式 -->
          <div v-if="frequencyType === 'simple'" class="frequency-selector">
            <div class="frequency-option">
              <el-radio v-model="simpleFrequency" label="daily">每天</el-radio>
              <el-time-picker
                  v-model="dailyTime"
                  format="HH:mm"
                  value-format="HH:mm"
                  placeholder="选择时间"
                  @change="updateSimpleCron"
                  v-if="simpleFrequency === 'daily'"
              />
            </div>

            <div class="frequency-option">
              <el-radio v-model="simpleFrequency" label="weekly">每周</el-radio>
              <div v-if="simpleFrequency === 'weekly'" class="weekly-options">
                <el-time-picker
                    v-model="weeklyTime"
                    format="HH:mm"
                    value-format="HH:mm"
                    placeholder="选择时间"
                    @change="updateSimpleCron"
                />
                <el-checkbox-group v-model="selectedWeekDays" @change="updateSimpleCron">
                  <el-checkbox v-for="day in weekDays" :key="day.value" :label="day.value">
                    {{day.label}}
                  </el-checkbox>
                </el-checkbox-group>
              </div>
            </div>

            <div class="frequency-option">
              <el-radio v-model="simpleFrequency" label="monthly">每月</el-radio>
              <div v-if="simpleFrequency === 'monthly'" class="monthly-options">
                <el-time-picker
                    v-model="monthlyTime"
                    format="HH:mm"
                    value-format="HH:mm"
                    placeholder="选择时间"
                    @change="updateSimpleCron"
                />
                <el-input-number
                    v-model="monthlyDay"
                    :min="1"
                    :max="31"
                    controls-position="right"
                    @change="updateSimpleCron"
                />
                <span>日</span>
              </div>
            </div>
          </div>

          <!-- 高级模式 -->
          <div v-else class="advanced-mode">
            <el-input v-model="form.cronExpression" placeholder="如：0 0 9 * * ?">
              <template #append>
                <el-button @click="showCronHelp">表达式帮助</el-button>
              </template>
            </el-input>
            <div class="cron-description">
              {{ parseCronExpression(form.cronExpression) }}
            </div>
          </div>
        </el-form-item>

        <el-form-item label="Todo标题" prop="todoTitle">
          <el-input v-model="form.todoTitle" placeholder="如：准备每日晨会" />
        </el-form-item>

        <el-form-item label="Todo描述">
          <el-input
              v-model="form.todoDescription"
              type="textarea"
              :rows="3"
              placeholder="任务详细描述"
          />
        </el-form-item>

        <el-form-item label="初始状态">
          <el-select v-model="form.todoStatus" style="width: 100%">
            <el-option label="待办" value="待办" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>

        <el-form-item label="优先级">
          <el-select v-model="form.todoPriority" style="width: 100%">
            <el-option label="高" value="高" />
            <el-option label="中" value="中" />
            <el-option label="低" value="低" />
          </el-select>
        </el-form-item>

        <el-form-item label="到期天数">
          <el-input-number
              v-model="form.daysUntilDue"
              :min="0"
              :max="30"
              controls-position="right"
          />
        </el-form-item>

        <el-form-item label="激活状态">
          <el-switch v-model="form.active" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">
            {{ isEditMode ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import axios from 'axios'

// 配置axios基础URL
axios.defaults.baseURL = 'http://localhost:8080/api'

// 响应式数据
const tasks = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEditMode = ref(false)
const currentTaskId = ref(null)
const formRef = ref(null)
const username = localStorage.getItem('username')

// Cron表达式相关状态
const frequencyType = ref('simple') // simple 或 advanced
const simpleFrequency = ref('daily') // daily, weekly, monthly
const dailyTime = ref('09:00')
const weeklyTime = ref('09:00')
const monthlyTime = ref('09:00')
const monthlyDay = ref(1)
const selectedWeekDays = ref([1]) // 默认周一

const weekDays = [
  { value: 1, label: '周一' },
  { value: 2, label: '周二' },
  { value: 3, label: '周三' },
  { value: 4, label: '周四' },
  { value: 5, label: '周五' },
  { value: 6, label: '周六' },
  { value: 0, label: '周日' }
]

// 表单数据
const form = ref({
  taskName: '',
  cronExpression: '0 0 9 * * ?',
  todoTitle: '',
  todoDescription: '',
  todoStatus: '待办',
  todoPriority: '中',
  daysUntilDue: 1,
  active: true,
  userId: username
})

// 表单验证规则
const rules = {
  taskName: [
    {required: true, message: '请输入任务名称', trigger: 'blur'},
    {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
  ],
  cronExpression: [
    {required: true, message: '请输入Cron表达式', trigger: 'blur'}
  ],
  todoTitle: [
    {required: true, message: '请输入Todo标题', trigger: 'blur'},
    {min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur'}
  ]
}

// 生命周期钩子
onMounted(() => {
  fetchTasks()
})

// API调用方法
const fetchTasks = async () => {
  loading.value = true
  try {
    const response = await axios.get('/scheduled-tasks', {
      params: {
        userid: username // 替换成实际的用户ID
      }
    });
    tasks.value = response.data
  } catch (error) {
    ElMessage.error('获取任务列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const createTask = async (taskData) => {
  try {
    const response = await axios.post('/scheduled-tasks', taskData)
    return response.data
  } catch (error) {
    throw error
  }
}

const updateTask = async (id, taskData) => {
  try {
    const response = await axios.put(`/scheduled-tasks/${id}`, taskData)
    return response.data
  } catch (error) {
    throw error
  }
}

const deleteTask = async (id) => {
  try {
    await axios.delete(`/scheduled-tasks/${id}`)
    return {success: true}
  } catch (error) {
    throw error
  }
}

const toggleTaskStatus = async (id, active) => {
  try {
    await axios.put(`/scheduled-tasks/${id}/status`, null, {
      params: {active}
    })

    await fetchTasks()
    ElMessage.success("成功")
    return {success: true, active}
  } catch (error) {
    throw error
  }
}

// Cron表达式相关方法
const handleFrequencyChange = (type) => {
  if (type === 'simple') {
    updateSimpleCron()
  }
}

const updateSimpleCron = () => {
  let cron = '0 '

  switch (simpleFrequency.value) {
    case 'daily':
      const [dailyHour, dailyMinute] = dailyTime.value.split(':')
      cron += `${dailyMinute || '0'} ${dailyHour || '0'} * * ?`
      break

    case 'weekly':
      const [weeklyHour, weeklyMinute] = weeklyTime.value.split(':')
      cron += `${weeklyMinute || '0'} ${weeklyHour || '0'} ? * `
      cron += selectedWeekDays.value.sort().join(',') || '*'
      break

    case 'monthly':
      const [monthlyHour, monthlyMinute] = monthlyTime.value.split(':')
      cron += `${monthlyMinute || '0'} ${monthlyHour || '0'} ${monthlyDay.value} * ?`
      break
  }

  form.value.cronExpression = cron
}

const tryParseSimpleCron = (cron) => {
  const parts = cron.split(' ')
  if (parts.length < 6) return false

  const [second, minute, hour, dayOfMonth, month, dayOfWeek] = parts

  // 检查是否是每天模式
  if (dayOfMonth === '*' && month === '*' && dayOfWeek === '?') {
    simpleFrequency.value = 'daily'
    dailyTime.value = `${hour.padStart(2, '0')}:${minute.padStart(2, '0')}`
    return true
  }

  // 检查是否是每周模式
  if (dayOfMonth === '?' && month === '*' && dayOfWeek !== '*' && dayOfWeek !== '?') {
    simpleFrequency.value = 'weekly'
    weeklyTime.value = `${hour.padStart(2, '0')}:${minute.padStart(2, '0')}`
    selectedWeekDays.value = dayOfWeek.split(',').map(Number)
    return true
  }

  // 检查是否是每月模式
  if (month === '*' && dayOfMonth !== '*' && dayOfMonth !== '?' && dayOfWeek === '?') {
    simpleFrequency.value = 'monthly'
    monthlyTime.value = `${hour.padStart(2, '0')}:${minute.padStart(2, '0')}`
    monthlyDay.value = parseInt(dayOfMonth)
    return true
  }

  return false
}

// 组件方法
const refreshTasks = () => {
  fetchTasks()
  ElMessage.success('任务列表已刷新')
}

const showCreateDialog = () => {
  isEditMode.value = false
  dialogVisible.value = true
}

const editTask = (task) => {
  isEditMode.value = true
  currentTaskId.value = task.id
  form.value = {...task}

  // 尝试解析Cron表达式为简单模式
  if (tryParseSimpleCron(task.cronExpression)) {
    frequencyType.value = 'simple'
  } else {
    frequencyType.value = 'advanced'
  }

  dialogVisible.value = true
}

const resetForm = () => {
  formRef.value?.resetFields()
  form.value = {
    taskName: '',
    cronExpression: '0 0 9 * * ?',
    todoTitle: '',
    todoDescription: '',
    todoStatus: '待办',
    todoPriority: '中',
    daysUntilDue: 1,
    active: true,
    userId: username
  }
  frequencyType.value = 'simple'
  simpleFrequency.value = 'daily'
  dailyTime.value = '09:00'
  weeklyTime.value = '09:00'
  monthlyTime.value = '09:00'
  monthlyDay.value = 1
  selectedWeekDays.value = [1]
  currentTaskId.value = null
}

const submitForm = async () => {
  try {
    await formRef.value.validate()

    if (isEditMode.value) {
      // 更新任务
      const updatedTask = await updateTask(currentTaskId.value, form.value)
      const index = tasks.value.findIndex(t => t.id === currentTaskId.value)
      if (index !== -1) {
        tasks.value.splice(index, 1, updatedTask)
      }
      ElMessage.success('任务更新成功')
    } else {
      // 创建任务
      const newTask = await createTask(form.value)
      tasks.value.unshift(newTask)
      ElMessage.success('任务创建成功')
    }

    dialogVisible.value = false
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  }
}

const confirmDelete = (id) => {
  ElMessageBox.confirm('确定要删除这个任务吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTask(id)
      tasks.value = tasks.value.filter(task => task.id !== id)
      ElMessage.success('任务已删除')
    } catch (error) {
      ElMessage.error('删除失败: ' + error.message)
    }
  }).catch(() => {
    // 用户取消了删除
  })
}

const showCronHelp = () => {
  ElMessageBox.alert(
      `常用Cron表达式示例：
    \n0 0 9 * * ? —— 每天上午9点
    \n0 0 12 * * ? —— 每天中午12点
    \n0 0 18 ? * MON-FRI —— 每周一到周五下午6点
    \n0 0 10 1 * ? —— 每月1日上午10点
    \n0 0 8 1 1 ? —— 每年1月1日上午8点`,
      'Cron表达式帮助'
  )
}

const parseCronExpression = (cron) => {
  // 简化的Cron表达式解析
  const parts = cron.split(' ')
  if (parts.length < 6) return '无效的Cron表达式'

  const [second, minute, hour, dayOfMonth, month, dayOfWeek] = parts

  let description = ''

  // 每天固定时间
  if (dayOfMonth === '*' && month === '*' && dayOfWeek === '?') {
    description = `每天 ${hour}:${minute}:${second}`
  }
  // 每周特定几天
  else if (dayOfMonth === '?' && month === '*' && dayOfWeek !== '*') {
    const days = dayOfWeek.split(',').map(d => {
      const dayMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return dayMap[parseInt(d)] || d
    }).join(',')
    description = `每周 ${days} ${hour}:${minute}:${second}`
  }
  // 每月特定几天
  else if (dayOfMonth !== '*' && dayOfMonth !== '?' && month === '*') {
    description = `每月 ${dayOfMonth}日 ${hour}:${minute}:${second}`
  }
  // 每年特定月份
  else if (month !== '*' && month !== '?') {
    const months = month.split(',').map(m => {
      const monthMap = [
        '1月', '2月', '3月', '4月', '5月', '6月',
        '7月', '8月', '9月', '10月', '11月', '12月'
      ]
      return monthMap[parseInt(m) - 1] || m
    }).join(',')
    description = `每年 ${months} ${dayOfMonth}日 ${hour}:${minute}:${second}`
  }

  return description || '自定义计划'
}
</script>

<style scoped>
.task-manager-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-table {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.frequency-selector {
  margin-top: 10px;
}

.frequency-option {
  margin-bottom: 15px;
}

.weekly-options,
.monthly-options {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.weekly-options .el-checkbox-group {
  margin-left: 15px;
}

.monthly-options > * {
  margin-right: 10px;
}

.cron-description {
  margin-top: 8px;
  color: #666;
  font-size: 14px;
}
</style>
