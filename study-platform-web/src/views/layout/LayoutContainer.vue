<template>
  <div class="client-layout">
    <header class="top-navbar">
      <div class="nav-container">
        
        <div class="logo-area" @click="$router.push('/note/square')">
          <span class="cmd-prompt">➜</span>
          <span class="logo-text">LINGSHENG</span>
          <span class="blink-cursor">_</span>
        </div>

        <el-menu 
          :default-active="$route.path" 
          router 
          mode="horizontal" 
          class="center-menu"
          :ellipsis="false"
        >
          <el-menu-item index="/note/square">
            <el-icon><Collection /></el-icon>笔记广场
          </el-menu-item>
          <el-menu-item index="/resource">
            <el-icon><Files /></el-icon>资料广场
          </el-menu-item>
          <el-menu-item index="/note">
            <el-icon><Management /></el-icon>我的笔记
          </el-menu-item>
          <el-menu-item index="/note/collect">
            <el-icon><Star /></el-icon>我的收藏
          </el-menu-item>
          <el-menu-item index="/todo">
            <el-icon><DocumentChecked /></el-icon>待办计划
          </el-menu-item>
          <el-menu-item index="/group/square">
            <el-icon><ChatSquare /></el-icon>技术社区
          </el-menu-item>
        </el-menu>

        <div class="user-actions">
          
          <el-badge :value="unreadCount" :max="99" :hidden="unreadCount === 0" class="msg-badge">
            <div class="action-btn" @click="$router.push('/chat')" title="消息中心">
              <el-icon :size="20"><ChatDotRound /></el-icon>
            </div>
          </el-badge>
          
          <el-button 
          v-if="userStore.user.role === 'ADMIN'" 
          type="danger" 
          plain 
          class="geek-admin-btn"
          @click="router.push('/admin')"
          style="margin-right: 15px; font-family: monospace; font-weight: bold;"
        >
          <el-icon><Monitor /></el-icon> [ ROOT_CONSOLE ]
        </el-button>

          <el-dropdown @command="handleCommand" trigger="click">
            <div class="avatar-wrapper">
              <el-avatar :size="36" :src="userStore.user.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
              <span class="nickname">{{ userStore.user.nickname || userStore.user.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile" icon="User">基本资料</el-dropdown-item>
                <el-dropdown-item command="password" icon="Lock">重置密码</el-dropdown-item>
                <el-dropdown-item command="logout" icon="SwitchButton" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

      </div>
    </header>

    <main class="main-content">
      <div class="page-container">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
    
    <footer class="client-footer">
      <div class="footer-code">[root@localhost ~]# systemctl status lingsheng-platform</div>
      <div class="footer-copy">© 2026 Created by LiuTong | All Systems Operational.</div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatSquare } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const unreadCount = ref(0) 
let timer = null 

const fetchUnreadCount = () => {
  if (!userStore.user.userId) return
  
  axios.get(`http://localhost:8080/chat/unreadCount?userId=${userStore.user.userId}`)
    .then(res => {
      if (res.data.code === 200) {
        unreadCount.value = res.data.data
      }
    })
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/user/profile')
  } else if (command === 'password') {
    router.push('/user/password')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要离线吗？', 'System Prompt', {
      confirmButtonText: '确定 (Enter)',
      cancelButtonText: '取消 (Esc)',
      type: 'warning'
    }).then(() => {
      // 👈 核心修改：使用你 store 里真实存在的 removeUser 方法
      userStore.removeUser() 
      
      router.push('/login')
      ElMessage.success('已断开连接')
    }).catch(() => {})
  }
}

onMounted(() => {
  fetchUnreadCount()
  timer = setInterval(() => { fetchUnreadCount() }, 10000)
  window.addEventListener('refresh-unread', fetchUnreadCount)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('refresh-unread', fetchUnreadCount)
})
</script>

<style scoped>
/* 🚀 彩蛋 1：网格背景 (Blueprint Grid) */
.client-layout {
  min-height: 100vh;
  background-color: #f8fafc;
  background-image: 
    linear-gradient(to right, rgba(0, 0, 0, 0.03) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.03) 1px, transparent 1px);
  background-size: 20px 20px; /* 20px的小网格 */
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.top-navbar {
  height: 60px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
}

.nav-container {
  width: 100%;
  max-width: 1200px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

/* 🚀 彩蛋 2：终端风 Logo */
.logo-area {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
  font-family: 'Courier New', Courier, monospace;
}
.cmd-prompt {
  color: #10b981; /* 终端绿 */
  font-weight: 900;
  font-size: 18px;
  margin-right: 6px;
}
.logo-text {
  font-size: 20px;
  font-weight: 900;
  color: #1e293b;
  letter-spacing: 1px;
}
/* 闪烁光标动画 */
.blink-cursor {
  color: #3b82f6;
  font-weight: 900;
  font-size: 20px;
  animation: blink 1s step-end infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* 🚀 彩蛋 3：代码注释风菜单 */
.center-menu {
  flex: 1;
  justify-content: center;
  border-bottom: none !important;
  height: 60px;
  background: transparent;
}
:deep(.el-menu-item) {
  font-size: 15px;
  color: #475569 !important;
  font-weight: 500;
  padding: 0 20px;
  transition: all 0.3s;
}
/* 选中或悬停时，利用伪元素加上 '// ' */
:deep(.el-menu-item.is-active::before),
:deep(.el-menu-item:hover::before) {
  content: '// ';
  color: #3b82f6;
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  margin-right: 4px;
}
:deep(.el-menu-item.is-active) {
  color: #3b82f6 !important;
  font-weight: bold;
  border-bottom: 3px solid #3b82f6 !important;
  background-color: transparent !important;
}
:deep(.el-menu-item:hover) {
  color: #3b82f6 !important;
  background-color: rgba(59, 130, 246, 0.05) !important;
}

/* 右侧操作区 */
.user-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}
.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px; /* 改成圆角矩形，更有键盘按键感 */
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s;
}
.action-btn:hover {
  background: #e0e7ff;
  color: #3b82f6;
  transform: translateY(-1px);
}
.msg-badge { margin-top: 4px; }

/* 头像区域 */
.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}
.avatar-wrapper:hover {
  background: #f1f5f9;
}
.nickname {
  font-size: 14px;
  color: #334155;
  font-weight: bold;
}

/* 主体内容区 */
.main-content {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 24px 0;
}
.page-container {
  width: 100%;
  max-width: 1200px;
  padding: 0 20px;
}

/* 🚀 彩蛋 4：服务器终端底部 */
.client-footer {
  text-align: center;
  padding: 30px 0;
  font-family: 'Courier New', Courier, monospace;
}
.footer-code {
  color: #10b981;
  font-size: 13px;
  margin-bottom: 6px;
}
.footer-copy {
  color: #94a3b8;
  font-size: 12px;
}

/* 渐变动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>