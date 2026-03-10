<template>
  <div class="geek-group-container">
    
    <header class="page-header">
      <div class="header-left">
        <el-icon class="cmd-icon"><Connection /></el-icon>
        <div class="title-area">
          <h1 class="main-title">Study Hub / 极客圈子</h1>
          <p class="sub-title">Connect with other nodes, share knowledge, and build together.</p>
        </div>
      </div>
      <div class="header-right">
        <el-button class="geek-btn create-btn" @click="createDialogVisible = true">
          <span class="btn-symbol">+</span> INIT NEW HUB
        </el-button>
      </div>
    </header>

    <main class="group-grid" v-if="groupList.length > 0">
      <div 
        v-for="group in groupList" 
        :key="group.groupId" 
        class="group-card"
        @click="goToDetail(group.groupId)"
      >
        
        <div class="card-banner">
          <div class="hub-icon">
            {{ group.name ? group.name.substring(0, 1).toUpperCase() : '#' }}
          </div>
        </div>
        
        <div class="card-content">
          <h3 class="group-name">{{ group.name }}</h3>
          <p class="group-desc">{{ group.description || 'The creator left no description. / 暂无简介' }}</p>
          
          <div class="card-footer">
            <span class="date-text">Created: {{ formatDate(group.createTime) }}</span>
            <el-button class="join-btn" plain size="small" @click.stop="handleJoin(group.groupId)">
              CONNECT >
            </el-button>
          </div>
        </div>

      </div>
    </main>

    <div v-else class="empty-state">
      <el-empty description="[ System ]: No Hubs found. Why not initialize one?" />
    </div>

    <el-dialog
      v-model="createDialogVisible"
      title=">_ Initialize New Hub"
      width="450px"
      custom-class="geek-dialog"
      :show-close="false"
    >
      <div class="dialog-body">
        <div class="input-group">
          <label class="geek-label">Hub Name (Required) :</label>
          <input v-model="newGroup.name" class="geek-input" placeholder="e.g. Java冲刺小分队" />
        </div>
        <div class="input-group mt-4">
          <label class="geek-label">Description (Optional) :</label>
          <textarea v-model="newGroup.description" class="geek-textarea" rows="3" placeholder="What is this hub for?"></textarea>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button class="geek-btn cancel-btn" @click="createDialogVisible = false">ABORT (Esc)</el-button>
          <el-button class="geek-btn confirm-btn" type="primary" @click="submitCreate">EXECUTE (↵)</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router' // 引入路由
import { ElMessage } from 'element-plus'
import { Connection } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter() // 实例化路由
const groupList = ref([])
const createDialogVisible = ref(false)

// 新建小组的表单数据
const newGroup = ref({
  name: '',
  description: ''
})

// 1. 加载小组大厅列表
const loadGroups = () => {
  axios.get('http://localhost:8080/study-group/list').then(res => {
    if (res.data.code === 200) {
      groupList.value = res.data.data
    }
  })
}

// 2. 提交创建小组
const submitCreate = () => {
  if (!newGroup.value.name.trim()) {
    return ElMessage.warning('System Error: Hub Name is required')
  }

  axios.post('http://localhost:8080/study-group/create', {
    name: newGroup.value.name,
    description: newGroup.value.description,
    creatorId: userStore.user.userId
  }).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('Process finished: 圈子创建成功')
      createDialogVisible.value = false
      newGroup.value = { name: '', description: '' } // 清空表单
      loadGroups() // 刷新列表
    } else {
      ElMessage.error(res.data.msg)
    }
  })
}

// 3. 加入小组
const handleJoin = (groupId) => {
  axios.post('http://localhost:8080/study-group/join', null, {
    params: {
      groupId: groupId,
      userId: userStore.user.userId
    }
  }).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('Connection established: 成功加入圈子')
    } else {
      ElMessage.warning(`Warning: ${res.data.msg}`)
    }
  })
}

// 4. 跳转到详情页
const goToDetail = (groupId) => {
  router.push(`/group/detail?id=${groupId}`)
}

// 时间格式化
const formatDate = (dateStr) => {
  if (!dateStr) return 'Unknown'
  return dateStr.split('T')[0]
}

onMounted(() => {
  loadGroups()
})
</script>

<style scoped>
/* 整体容器 */
.geek-group-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
  animation: fadeIn 0.4s ease-out;
}

/* 顶部 Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  margin-bottom: 30px;
  border: 1px solid #f1f5f9;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}
.cmd-icon {
  font-size: 36px;
  color: #3b82f6;
  background: #eff6ff;
  padding: 10px;
  border-radius: 12px;
}
.main-title { margin: 0 0 8px 0; font-size: 24px; color: #0f172a; font-weight: 800; }
.sub-title { margin: 0; font-size: 14px; color: #64748b; font-family: 'Courier New', Courier, monospace; }

/* 极客风按钮通用样式 */
.geek-btn {
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  border-radius: 8px;
  transition: all 0.3s;
}
.create-btn {
  background: #10b981;
  color: #fff;
  border: none;
  padding: 12px 24px;
}
.create-btn:hover { background: #059669; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3); }
.btn-symbol { margin-right: 6px; font-weight: 900; }

/* 网格布局 */
.group-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

/* 圈子卡片 */
.group-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  border: 1px solid #f1f5f9;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  cursor: pointer; /* 💡 增加鼠标指针变成小手的效果 */
}
.group-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  border-color: #bfdbfe;
}

/* 卡片顶部 Banner */
.card-banner {
  height: 80px;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  position: relative;
}
.hub-icon {
  position: absolute;
  bottom: -20px;
  left: 20px;
  width: 50px;
  height: 50px;
  background: #3b82f6;
  color: #fff;
  font-size: 24px;
  font-weight: bold;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
  border: 4px solid #fff;
  font-family: 'Courier New', Courier, monospace;
}

/* 卡片内容 */
.card-content {
  padding: 32px 20px 20px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.group-name { margin: 0 0 10px 0; font-size: 18px; color: #1e293b; font-weight: bold; }
.group-desc { margin: 0 0 20px 0; font-size: 14px; color: #64748b; line-height: 1.5; flex: 1; }

/* 卡片底部 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px dashed #e2e8f0;
  padding-top: 16px;
}
.date-text { font-size: 12px; color: #94a3b8; font-family: 'Courier New', Courier, monospace; }
.join-btn { border-color: #3b82f6; color: #3b82f6; font-family: 'Courier New', Courier, monospace; font-weight: bold; transition: all 0.2s; }
.join-btn:hover { background: #3b82f6; color: #fff; }

/* 弹窗终端风表单 */
.dialog-body { background: #1e293b; padding: 24px; border-radius: 8px; }
.input-group { display: flex; flex-direction: column; gap: 8px; }
.mt-4 { margin-top: 16px; }
.geek-label { color: #10b981; font-family: 'Courier New', Courier, monospace; font-size: 13px; font-weight: bold; }
.geek-input, .geek-textarea {
  background: #0f172a;
  border: 1px solid #334155;
  color: #e2e8f0;
  padding: 10px 12px;
  border-radius: 6px;
  font-family: 'Courier New', Courier, monospace;
  outline: none;
  transition: border-color 0.3s;
}
.geek-input:focus, .geek-textarea:focus { border-color: #3b82f6; }
.geek-textarea { resize: none; }

.cancel-btn { background: transparent; border: 1px solid #cbd5e1; color: #64748b; }
.cancel-btn:hover { background: #f1f5f9; color: #0f172a; }
.confirm-btn { background: #3b82f6; border: none; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>