<template>
  <div class="geek-profile-container">
    
    <div class="profile-header-card">
      <div class="avatar-section">
        <el-avatar :size="90" :src="userStore.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="geek-avatar" />
        <div class="user-info">
          <h1 class="username">
            <span class="prompt">~ $ whoami</span><br/>
            {{ userStore.user.nickname || userStore.user.username }}
          </h1>
          <p class="role-badge" :class="userStore.user.role === 'ADMIN' ? 'admin' : 'user'">
            [{{ userStore.user.role === 'ADMIN' ? 'ROOT_ADMIN' : 'DEVELOPER' }}]
          </p>
          <p class="user-email" v-if="userStore.user.email">
            <el-icon><Message /></el-icon> {{ userStore.user.email }}
          </p>
        </div>
      </div>
      
      <div class="action-section">
        <el-button class="geek-btn edit-btn" @click="openEditDialog">
          ⚙️ CONFIG PROFILE
        </el-button>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="dashboard-card contribution-card">
        <div class="card-title">
          <el-icon><Calendar /></el-icon> Activity Map / 活跃矩阵
        </div>
        
        <div class="contribution-graph-wrapper">
          <ul class="months">
            <li>Jan</li><li>Feb</li><li>Mar</li><li>Apr</li><li>May</li><li>Jun</li>
            <li>Jul</li><li>Aug</li><li>Sep</li><li>Oct</li><li>Nov</li><li>Dec</li>
          </ul>
          <div class="graph-layout">
            <ul class="days">
              <li>Mon</li><li>Wed</li><li>Fri</li>
            </ul>
            <div class="squares">
              <el-tooltip
                v-for="(day, index) in contributionData"
                :key="index"
                :content="`${day.date} : ${day.count} contributions`"
                placement="top"
                effect="dark"
              >
                <div class="square" :data-level="day.level"></div>
              </el-tooltip>
            </div>
          </div>
          <div class="graph-legend">
            <span>Less</span>
            <div class="square" data-level="0"></div>
            <div class="square" data-level="1"></div>
            <div class="square" data-level="2"></div>
            <div class="square" data-level="3"></div>
            <div class="square" data-level="4"></div>
            <span>More</span>
          </div>
        </div>
      </div>

      <div class="dashboard-card radar-card">
        <div class="card-title">
          <el-icon><DataAnalysis /></el-icon> Skill Radar / 能力雷达
        </div>
        <div ref="radarChartRef" class="radar-chart"></div>
      </div>
    </div>

    <el-dialog
      v-model="editDialogVisible"
      title=">_ Update User Profile"
      width="450px"
      custom-class="geek-dialog"
      :show-close="false"
    >
      <div class="dialog-body">
        
        <div class="input-group">
          <label class="geek-label">Avatar (点击图片上传头像) :</label>
          <el-upload
            class="geek-avatar-uploader"
            action="http://localhost:8080/file/uploadImage" 
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            name="file" 
          >
            <img v-if="imageUrl" :src="imageUrl" class="uploaded-avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <p class="upload-tip">Format: JPG, PNG. Max: 2MB.</p>
        </div>

        <div class="input-group mt-6">
          <label class="geek-label">Nickname (昵称) :</label>
          <input v-model="editForm.nickname" class="geek-input" placeholder="输入新的昵称" />
        </div>
        <div class="input-group mt-4">
          <label class="geek-label">Email (邮箱) :</label>
          <input v-model="editForm.email" class="geek-input" placeholder="输入联系邮箱" />
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button class="geek-btn cancel-btn" @click="editDialogVisible = false">ABORT (Esc)</el-button>
          <el-button class="geek-btn confirm-btn" type="primary" @click="submitEdit">EXECUTE (↵)</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import * as echarts from 'echarts'
import { Calendar, DataAnalysis, Plus,Message } from '@element-plus/icons-vue' // 💡 引入了 Plus 图标
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const radarChartRef = ref(null)
let radarChart = null

// --- 修改资料逻辑 ---
const editDialogVisible = ref(false)
const editForm = ref({
  userId: '',
  nickname: '',
  avatar: '', 
  email: ''
})

// 💡 新增：用于存放预览头像的临时 URL
const imageUrl = ref('')

const openEditDialog = () => {
  // 回显当前数据
  editForm.value = {
    userId: userStore.user.userId,
    nickname: userStore.user.nickname || '',
    avatar: userStore.user.avatar || '',
    email: userStore.user.email || ''
  }
  // 回显预览图
  imageUrl.value = userStore.user.avatar || ''
  editDialogVisible.value = true
}

// ====== 💡 核心新增：Element Plus 上传处理逻辑 ======

// 1. 头像上传成功的回调 (后端会返回图片的真实网络链接)
const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 200) {
      // 1. 生成本地的 blob 预览图 (这一步让用户感觉图片立刻变了)
      imageUrl.value = URL.createObjectURL(uploadFile.raw)
      // 2. 将后端返回的真实图片的 URL 填入 editForm，以便最终提交保存
      editForm.value.avatar = response.data 
  } else {
      ElMessage.error(`Upload error: ${response.msg}`)
  }
}

// 2. 前端安全校验：校验文件格式和大小
const beforeAvatarUpload = (rawFile) => {
  const allowedTypes = ['image/jpeg', 'image/png']
  if (!allowedTypes.includes(rawFile.type)) {
    ElMessage.error('Error: Avatar must be JPG or PNG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) { 
    ElMessage.error('Error: Avatar picture size can not exceed 2MB!')
    return false
  }
  return true
}

// ====== 💡 核心新增结束 ======

const submitEdit = async () => {
  try {
    const res = await axios.post('http://localhost:8080/sys-user/update', editForm.value)
    if (res.data.code === 200) {
      ElMessage.success('Process finished: 资料更新成功')
      
      // 动态更新 Pinia 状态
      userStore.user.nickname = res.data.data.nickname
      userStore.user.avatar = res.data.data.avatar
      userStore.user.email = res.data.data.email
      // 同步更新本地存储
      localStorage.setItem('user', JSON.stringify(res.data.data))
      
      editDialogVisible.value = false
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) {
    ElMessage.error('System Error: 网络异常')
  }
}

// --- 1. 获取真实 GitHub 绿格子数据 ---
const contributionData = ref([])

const loadContributionData = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/sys-user/stats/activity?userId=${userStore.user.userId}`)
    const activeDays = res.data.data || [] 
    
    const activeMap = {}
    activeDays.forEach(item => {
      if (item.date) {
        const dateStr = String(item.date).split('T')[0]
        activeMap[dateStr] = item.count
      }
    })

    const data = []
    const today = new Date()
    for (let i = 364; i >= 0; i--) {
      const d = new Date(today.getTime() - i * 24 * 60 * 60 * 1000)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const dateStr = `${year}-${month}-${day}` 
      
      const count = activeMap[dateStr] || 0 

      let level = 0
      if (count > 0 && count <= 2) level = 1
      else if (count > 2 && count <= 4) level = 2
      else if (count > 4 && count <= 6) level = 3
      else if (count > 6) level = 4

      data.push({ date: dateStr, count: count, level: level })
    }
    contributionData.value = data
  } catch (e) {
    console.error('Failed to load activity data', e)
  }
}

// --- 2. 获取真实雷达图数据并初始化 ECharts ---
const loadRadarDataAndInit = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/sys-user/stats/radar?userId=${userStore.user.userId}`)
    const radarData = res.data.data || []
    
    if (radarData.length === 0) {
      radarData.push({ name: 'System', value: 30 })
    }

    const indicators = radarData.map(item => ({ name: item.name, max: 100, min: 0 }))
    const values = radarData.map(item => item.value)

    if (!radarChartRef.value) return
    radarChart = echarts.init(radarChartRef.value)
    
    const option = {
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item' },
      radar: {
        indicator: indicators,
        shape: 'polygon',
        radius: '65%',
        splitNumber: 4,
        axisName: { color: '#94a3b8', fontFamily: 'monospace', fontSize: 13 },
        splitLine: { lineStyle: { color: ['rgba(59, 130, 246, 0.2)'] } },
        splitArea: { show: false },
        axisLine: { lineStyle: { color: 'rgba(59, 130, 246, 0.3)' } }
      },
      series: [
        {
          name: 'Capability Profile',
          type: 'radar',
          data: [
            {
              value: values,
              name: 'Skill Points',
              symbol: 'circle',
              symbolSize: 6,
              itemStyle: { color: '#10b981' },
              areaStyle: { color: 'rgba(16, 185, 129, 0.3)' },
              lineStyle: { color: '#10b981', width: 2 }
            }
          ]
        }
      ]
    }
    radarChart.setOption(option)
  } catch (e) {
    console.error('Failed to load radar data', e)
  }
}

const handleResize = () => {
  if (radarChart) radarChart.resize()
}

onMounted(() => {
  loadContributionData()
  setTimeout(() => {
    loadRadarDataAndInit()
    window.addEventListener('resize', handleResize)
  }, 100)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (radarChart) radarChart.dispose()
})
</script>

<style scoped>
.geek-profile-container { max-width: 1200px; margin: 0 auto; padding: 20px 0; animation: fadeIn 0.5s ease-out; }

/* ================ 顶部个人卡片 ================ */
.profile-header-card { background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); border-radius: 16px; padding: 40px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 10px 30px rgba(0,0,0,0.1); margin-bottom: 24px; border: 1px solid #334155; flex-wrap: wrap; gap: 24px; }
.avatar-section { display: flex; align-items: center; gap: 24px; }
.geek-avatar { border: 4px solid #3b82f6; box-shadow: 0 0 20px rgba(59, 130, 246, 0.4); }

.user-info .prompt { font-size: 14px; color: #10b981; font-family: monospace; font-weight: normal; }
.user-info .username { margin: 0 0 8px 0; font-size: 32px; color: #f8fafc; font-weight: 800; font-family: 'Courier New', Courier, monospace; }
.role-badge { display: inline-block; font-family: monospace; font-weight: bold; font-size: 14px; margin: 0; }
.role-badge.admin { color: #ef4444; }
.role-badge.user { color: #3b82f6; }
.user-email { margin: 8px 0 0 0; color: #94a3b8; font-family: 'Courier New', Courier, monospace; font-size: 14px; display: flex; align-items: center; gap: 6px; }

/* 极客按钮样式 */
.geek-btn { font-family: 'Courier New', Courier, monospace; font-weight: bold; border-radius: 8px; transition: all 0.3s; }
.edit-btn { background: transparent; color: #3b82f6; border: 1px solid #3b82f6; padding: 12px 24px; }
.edit-btn:hover { background: #3b82f6; color: #fff; box-shadow: 0 0 15px rgba(59, 130, 246, 0.4); }

/* ================ 仪表盘网格 ================ */
.dashboard-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 24px; }
.dashboard-card { background: #ffffff; border-radius: 16px; padding: 24px; box-shadow: 0 4px 12px rgba(0,0,0,0.02); border: 1px solid #e2e8f0; }
.card-title { font-size: 18px; font-weight: bold; color: #1e293b; margin-bottom: 20px; display: flex; align-items: center; gap: 8px; font-family: monospace; }

/* ================ 绿格子图 ================ */
.contribution-card { overflow-x: auto; }
.contribution-graph-wrapper { display: flex; flex-direction: column; gap: 10px; min-width: 800px; }
.months { display: flex; padding-left: 30px; margin: 0; list-style: none; color: #64748b; font-size: 12px; font-family: monospace; }
.months li { flex: 1; }
.graph-layout { display: flex; gap: 10px; }
.days { display: flex; flex-direction: column; justify-content: space-between; margin: 0; padding: 0; list-style: none; color: #64748b; font-size: 12px; font-family: monospace; height: 105px; padding-top: 8px; }
.squares { display: grid; grid-template-rows: repeat(7, 1fr); grid-auto-flow: column; gap: 4px; }
.square { width: 12px; height: 12px; background-color: #f1f5f9; border-radius: 3px; border: 1px solid rgba(27,31,35,0.06); transition: transform 0.1s; }
.square:hover { transform: scale(1.2); z-index: 10; cursor: pointer; border-color: rgba(0,0,0,0.5); }
.square[data-level="1"] { background-color: #9be9a8; }
.square[data-level="2"] { background-color: #40c463; }
.square[data-level="3"] { background-color: #30a14e; }
.square[data-level="4"] { background-color: #216e39; }
.graph-legend { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #64748b; justify-content: flex-end; margin-top: 10px; font-family: monospace; }
.graph-legend span { margin: 0 4px; }

/* ================ 雷达图 ================ */
.radar-chart { height: 300px; width: 100%; }

/* ================ 弹窗样式 ================ */
.dialog-body { background: #1e293b; padding: 24px; border-radius: 8px; }
.input-group { display: flex; flex-direction: column; gap: 8px; }
.mt-4 { margin-top: 16px; }
.mt-6 { margin-top: 24px; }
.geek-label { color: #10b981; font-family: 'Courier New', Courier, monospace; font-size: 13px; font-weight: bold; }
.geek-input { background: #0f172a; border: 1px solid #334155; color: #e2e8f0; padding: 10px 12px; border-radius: 6px; font-family: 'Courier New', Courier, monospace; outline: none; transition: border-color 0.3s; }
.geek-input:focus { border-color: #3b82f6; }
.cancel-btn { background: transparent; border: 1px solid #cbd5e1; color: #64748b; }
.cancel-btn:hover { background: #f1f5f9; color: #0f172a; }
.confirm-btn { background: #3b82f6; border: none; color: #fff;}

/* ================ 💡 新增：极客头像上传组件专属样式 ================ */
.geek-avatar-uploader { display: flex; align-items: center; gap: 16px; margin-top: 8px; }
:deep(.el-upload) {
  border: 2px dashed #334155;
  border-radius: 50%; /* 完美圆形 */
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  width: 90px;
  height: 90px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #0f172a; /* 和输入框一样的深色 */
}
:deep(.el-upload:hover) {
  border-color: #3b82f6;
  background-color: #1e293b;
}
.avatar-uploader-icon { font-size: 28px; color: #64748b; }
.uploaded-avatar { width: 90px; height: 90px; display: block; border-radius: 50%; }
.upload-tip { margin: 8px 0 0 0; font-size: 11px; color: #64748b; font-family: monospace; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
@media (max-width: 1024px) { .dashboard-grid { grid-template-columns: 1fr; } }
</style>