<template>
  <div class="todo-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>📝 我的学习计划与待办</span>
        </div>
      </template>
      
      <div class="add-task" style="display: flex; gap: 10px; margin-bottom: 20px;">
        <el-input 
          v-model="newTaskContent" 
          placeholder="输入新的学习任务..." 
          @keyup.enter="addTask"
          size="large"
          style="flex: 1;"
        />
        <el-date-picker
          v-model="newTaskDeadline"
          type="datetime"
          placeholder="设置截止时间(可选)"
          value-format="YYYY-MM-DD HH:mm:ss"
          size="large"
          style="width: 220px;"
        />
        <el-button type="primary" size="large" @click="addTask">添加任务</el-button>
      </div>

      <el-divider />
      
      <div v-if="todoList.length === 0" class="empty-text">
        暂无待办任务，快来制定你的学习计划吧！
      </div>
      
      <div v-for="item in todoList" :key="item.todoId" class="todo-item">
        <div style="display: flex; align-items: center; gap: 15px;">
          <el-checkbox 
            :model-value="item.isCompleted === 1" 
            @change="toggleTask(item.todoId)"
            size="large"
          />
          <span :class="{ 'completed-text': item.isCompleted === 1 }">
            {{ item.content }}
          </span>
          <el-tag v-if="item.deadline" size="small" type="warning" effect="plain">
            截止: {{ item.deadline.replace('T', ' ') }}
          </el-tag>
        </div>
        <el-button type="danger" link @click="deleteTask(item.todoId)">删除</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const userStore = useUserStore()
const todoList = ref([])
const newTaskContent = ref('')
const newTaskDeadline = ref('') // 新增：绑定的截止时间

const loadTodoList = () => {
  if (!userStore.user.userId) return;
  axios.get(`http://localhost:8080/todo/list?userId=${userStore.user.userId}`)
    .then(res => {
      if (res.data.code === 200) {
        todoList.value = res.data.data
      }
    })
}

const addTask = () => {
  if (!newTaskContent.value.trim()) {
    return ElMessage.warning('任务内容不能为空')
  }
  
  // 组装数据，携带 deadline
  const payload = {
    userId: userStore.user.userId,
    content: newTaskContent.value,
    deadline: newTaskDeadline.value || null
  }

  axios.post('http://localhost:8080/todo/save', payload).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('添加成功')
      newTaskContent.value = ''
      newTaskDeadline.value = '' // 清空时间
      loadTodoList()
    }
  })
}

const toggleTask = (id) => {
  axios.post(`http://localhost:8080/todo/toggle/${id}`).then(res => {
    if (res.data.code === 200) loadTodoList()
  })
}

const deleteTask = (id) => {
  axios.delete(`http://localhost:8080/todo/delete/${id}`).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadTodoList()
    }
  })
}

onMounted(() => {
  loadTodoList()
})
</script>

<style scoped>
.todo-container { max-width: 800px; margin: 30px auto; }
.card-header { font-size: 20px; font-weight: bold; color: #333; }
.todo-item { display: flex; justify-content: space-between; align-items: center; padding: 15px 10px; border-bottom: 1px dashed #ebeef5; transition: all 0.3s; }
.todo-item:hover { background-color: #f9fafc; }
.completed-text { text-decoration: line-through; color: #999; }
.empty-text { text-align: center; color: #999; margin: 40px 0; }
</style>