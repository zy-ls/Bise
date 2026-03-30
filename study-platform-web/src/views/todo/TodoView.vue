<template>
  <div class="geek-todo-container">
    
    <header class="terminal-header">
      <div class="terminal-title">
        <span class="dot red"></span>
        <span class="dot yellow"></span>
        <span class="dot green"></span>
        <span class="title-text">root@study-hub:~ / task-manager</span>
      </div>
      <div class="terminal-body">
        <div class="input-line">
          <span class="prompt">➜</span>
          <span class="command">add-task</span>
          <input 
            v-model="newTaskTitle" 
            class="geek-input" 
            placeholder="[ 输入你的学习计划，按 Enter 生成任务... ]" 
            @keyup.enter="handleAddTask"
            autocomplete="off"
          />
          <el-button class="execute-btn" @click="handleAddTask" :disabled="!newTaskTitle.trim()">
            EXECUTE ↵
          </el-button>
        </div>
      </div>
    </header>

    <main class="kanban-board">
      
      <div 
        class="kanban-column" 
        @dragover.prevent 
        @drop="handleDrop($event, 0)"
      >
        <div class="column-header todo-header">
          <span class="icon">📝</span> 
          <span class="title">PENDING / 待办</span>
          <span class="count">{{ todoList.length }}</span>
        </div>
        <div class="column-body">
          <div 
            v-for="item in todoList" 
            :key="item.todoId" 
            class="task-card"
            draggable="true"
            @dragstart="handleDragStart($event, item)"
          >
            <div class="task-content">{{ item.content }}</div>
            <div class="task-footer">
              <span class="date">{{ formatDate(item.createTime) }}</span>
              <el-icon class="delete-icon" @click="handleDelete(item.todoId)"><Delete /></el-icon>
            </div>
          </div>
          <div v-if="todoList.length === 0" class="empty-slot">Drop tasks here...</div>
        </div>
      </div>

      <div 
        class="kanban-column" 
        @dragover.prevent 
        @drop="handleDrop($event, 1)"
      >
        <div class="column-header doing-header">
          <span class="icon">⚡</span> 
          <span class="title">IN PROGRESS / 进行中</span>
          <span class="count">{{ doingList.length }}</span>
        </div>
        <div class="column-body">
          <div 
            v-for="item in doingList" 
            :key="item.todoId" 
            class="task-card doing-card"
            draggable="true"
            @dragstart="handleDragStart($event, item)"
          >
            <div class="task-content">{{ item.content }}</div>
            <div class="task-footer">
              <span class="date">{{ formatDate(item.createTime) }}</span>
              <el-icon class="delete-icon" @click="handleDelete(item.todoId)"><Delete /></el-icon>
            </div>
          </div>
          <div v-if="doingList.length === 0" class="empty-slot">Drop tasks here...</div>
        </div>
      </div>

      <div 
        class="kanban-column" 
        @dragover.prevent 
        @drop="handleDrop($event, 2)"
      >
        <div class="column-header done-header">
          <span class="icon">✅</span> 
          <span class="title">COMPLETED / 已完成</span>
          <span class="count">{{ doneList.length }}</span>
        </div>
        <div class="column-body">
          <div 
            v-for="item in doneList" 
            :key="item.todoId" 
            class="task-card done-card"
            draggable="true"
            @dragstart="handleDragStart($event, item)"
          >
            <div class="task-content">{{ item.content }}</div>
            <div class="task-footer">
              <span class="date">{{ formatDate(item.createTime) }}</span>
              <el-icon class="delete-icon" @click="handleDelete(item.todoId)"><Delete /></el-icon>
            </div>
          </div>
          <div v-if="doneList.length === 0" class="empty-slot">Drop tasks here...</div>
        </div>
      </div>

    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const userStore = useUserStore()
const allTasks = ref([])
const newTaskTitle = ref('')

// 1. 💡 修改点：利用计算属性根据 isCompleted 字段分组 (0-待办, 1-进行中, 2-已完成)
const todoList = computed(() => allTasks.value.filter(t => t.isCompleted === 0))
const doingList = computed(() => allTasks.value.filter(t => t.isCompleted === 1))
const doneList = computed(() => allTasks.value.filter(t => t.isCompleted === 2))

// 2. 获取当前用户的所有待办
const loadTasks = () => {
  axios.get(`http://localhost:8080/todo/list?userId=${userStore.user.userId}`)
    .then(res => {
      if (res.data.code === 200) {
        allTasks.value = res.data.data || []
      }
    })
}

// 3. 添加新任务
const handleAddTask = () => {
  if (!newTaskTitle.value.trim()) return

  // 💡 修改点：提交的参数改为 content 和 isCompleted
  axios.post('http://localhost:8080/todo/save', {
    userId: userStore.user.userId,
    content: newTaskTitle.value, 
    isCompleted: 0 
  }).then(res => {
    if (res.data.code === 200) {
      newTaskTitle.value = ''
      loadTasks()
      ElMessage.success('Task Instantiated: 任务已生成')
    } else {
      ElMessage.error(res.data.msg || '添加失败')
    }
  })
}

// 4. 删除任务
const handleDelete = (todoId) => {
  axios.delete(`http://localhost:8080/todo/delete/${todoId}`)
    .then(res => {
      if (res.data.code === 200) {
        loadTasks()
      }
    })
}

// ====== 原生拖拽核心逻辑 ======
let currentDraggingTask = null

const handleDragStart = (event, task) => {
  currentDraggingTask = task
  event.dataTransfer.effectAllowed = 'move' 
  setTimeout(() => event.target.style.opacity = '0.5', 0)
}

const handleDrop = (event, targetStatus) => {
  document.querySelectorAll('.task-card').forEach(el => el.style.opacity = '1')
  
  if (!currentDraggingTask) return
  
  // 💡 修改点：判断 isCompleted 状态
  if (currentDraggingTask.isCompleted === targetStatus) {
    currentDraggingTask = null
    return
  }

  // 1. 乐观更新（让前端先变化，极致流畅感）
  const taskToUpdate = allTasks.value.find(t => t.todoId === currentDraggingTask.todoId)
  if (taskToUpdate) {
    taskToUpdate.isCompleted = targetStatus
  }

  // 2. 💡 修改点：异步通知后端更新数据库，使用 isCompleted
  axios.post('http://localhost:8080/todo/save', {
    todoId: currentDraggingTask.todoId,
    isCompleted: targetStatus
  }).catch(() => {
    ElMessage.error('System Error: 状态同步失败')
    loadTasks()
  }).finally(() => {
    currentDraggingTask = null
  })
}

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split('T')[0]
}

onMounted(() => {
  loadTasks()
  document.addEventListener('dragend', () => {
    document.querySelectorAll('.task-card').forEach(el => el.style.opacity = '1')
  })
})
</script>

<style scoped>
/* -------- 样式没有任何改变，完美保持极客终端风 -------- */
.geek-todo-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
  animation: fadeIn 0.4s ease-out;
}

.terminal-header {
  background: #1e293b;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  margin-bottom: 30px;
}
.terminal-title {
  background: #0f172a;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.dot { width: 12px; height: 12px; border-radius: 50%; }
.red { background: #ef4444; }
.yellow { background: #eab308; }
.green { background: #22c55e; }
.title-text {
  color: #94a3b8;
  font-family: monospace;
  font-size: 13px;
  margin-left: 10px;
}

.terminal-body { padding: 24px 30px; }
.input-line {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #0f172a;
  padding: 12px 20px;
  border-radius: 8px;
  border: 1px solid #334155;
}
.input-line:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}
.prompt { color: #10b981; font-weight: bold; font-family: monospace; font-size: 18px; }
.command { color: #3b82f6; font-family: monospace; font-weight: bold; font-size: 16px; }
.geek-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #e2e8f0;
  font-family: monospace;
  font-size: 16px;
}
.geek-input::placeholder { color: #475569; }
.execute-btn {
  background: transparent;
  color: #3b82f6;
  border: 1px solid #3b82f6;
  font-family: monospace;
  font-weight: bold;
}
.execute-btn:hover:not(:disabled) { background: #3b82f6; color: #fff; }

.kanban-board {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.kanban-column {
  flex: 1;
  background: #f8fafc;
  border-radius: 12px;
  min-height: 500px;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.column-header {
  padding: 16px 20px;
  border-radius: 12px 12px 0 0;
  display: flex;
  align-items: center;
  font-family: monospace;
  font-weight: bold;
  font-size: 15px;
  color: #fff;
}
.todo-header { background: #64748b; }
.doing-header { background: #3b82f6; }
.done-header { background: #10b981; }

.column-header .title { flex: 1; margin-left: 10px; }
.column-header .count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 13px;
}

.column-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-slot {
  height: 80px;
  border: 2px dashed #cbd5e1;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #94a3b8;
  font-family: monospace;
  font-size: 13px;
}

.task-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid #64748b;
  box-shadow: 0 2px 6px rgba(0,0,0,0.03);
  cursor: grab;
  transition: transform 0.2s, box-shadow 0.2s;
}
.task-card:active { cursor: grabbing; transform: scale(0.98); }
.task-card:hover { box-shadow: 0 6px 16px rgba(0,0,0,0.08); }

.doing-card { border-left-color: #3b82f6; }
.done-card { border-left-color: #10b981; opacity: 0.8; }
.done-card .task-content { text-decoration: line-through; color: #94a3b8; }

.task-content {
  font-size: 15px;
  color: #334155;
  line-height: 1.5;
  margin-bottom: 12px;
  word-break: break-all;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px dashed #e2e8f0;
  padding-top: 10px;
}
.date { font-family: monospace; font-size: 12px; color: #94a3b8; }
.delete-icon { color: #cbd5e1; cursor: pointer; transition: color 0.2s; }
.delete-icon:hover { color: #ef4444; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>