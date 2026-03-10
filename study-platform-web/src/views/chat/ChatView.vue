<template>
  <div class="geek-chat-container">
    <div class="chat-card">
      
      <aside class="session-list">
        <div class="list-header">
          <span class="cmd-icon">⚡</span> <span>Active Sessions</span>
        </div>
        <div class="contacts-wrapper">
          <div 
            v-for="user in contacts" 
            :key="user.userId"
            class="contact-item"
            :class="{ active: currentChatUser?.userId === user.userId }"
            @click="selectUser(user)"
          >
            <el-avatar :size="40" :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="avatar-border"/>
            <div class="contact-info">
              <div class="name">{{ user.nickname || user.username }}</div>
              <div class="status-text">
                <span class="status-dot"></span> Node Online
              </div>
            </div>
          </div>
        </div>
      </aside>

      <main class="chat-pane">
        
        <div v-if="!currentChatUser" class="empty-state">
          <div class="terminal-text">
            <p><span class="prompt">[root@lingsheng ~]#</span> systemctl status chat-server</p>
            <p class="success">● chat-server.service - Lingsheng Secure Communication Protocol</p>
            <p>   Loaded: loaded (/usr/lib/systemd/system/chat-server.service)</p>
            <p>   Active: <span class="success">active (running)</span></p>
            <br>
            <p><span class="prompt">[root@lingsheng ~]#</span> echo "Please select a node from the left panel to establish a secure connection..."</p>
            <p class="blink-cursor">_</p>
          </div>
        </div>

        <template v-else>
          <header class="chat-header">
            <div class="header-left">
              <div class="pulse-dot"></div>
              <span class="header-text">
                Connected to: <strong class="target-name">{{ currentChatUser.nickname || currentChatUser.username }}</strong>
              </span>
            </div>
            <span class="protocol-text">[ WSS Encrypted Channel ]</span>
          </header>

          <div class="message-list" ref="msgListRef">
            <div 
              v-for="msg in messageList" 
              :key="msg.messageId"
              class="message-wrapper"
              :class="{ 'is-me': msg.senderId === myUserId }"
            >
              <el-avatar v-if="msg.senderId !== myUserId" :size="36" :src="currentChatUser.avatar" class="msg-avatar" />
              
              <div class="bubble-container">
                <div class="bubble">
                  {{ msg.content }}
                </div>
              </div>

              <el-avatar v-if="msg.senderId === myUserId" :size="36" :src="userStore.user.avatar" class="msg-avatar" />
            </div>
          </div>

          <footer class="chat-input-area">
            <div class="input-wrapper">
              <span class="prompt-symbol">➜</span>
              <input 
                v-model="inputContent" 
                class="geek-input" 
                placeholder="Type message or command..." 
                @keyup.enter="handleSend"
                autocomplete="off"
              />
              <button class="send-btn" @click="handleSend" :disabled="!inputContent.trim()">
                EXECUTE ↵
              </button>
            </div>
          </footer>
        </template>
        
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const myUserId = userStore.user.userId

const contacts = ref([])          
const currentChatUser = ref(null) 
const messageList = ref([])       
const inputContent = ref('')      
const msgListRef = ref(null)      

let timer = null 

// 🔥 清除未读红点，并立刻通知导航栏刷新
const clearUnreadDot = () => {
  if (!myUserId) return;
  axios.post(`http://localhost:8080/chat/markRead?userId=${myUserId}`)
    .then(res => {
      window.dispatchEvent(new Event('refresh-unread'))
    }).catch(err => console.error('清除红点失败', err))
}

// 1. 获取联系人
const loadContacts = async () => {
  const res = await axios.get(`http://localhost:8080/chat/contacts?userId=${myUserId}`)
  if (res.data.code === 200) {
    contacts.value = res.data.data
  }
}

// 2. 选中某人
const selectUser = (user) => {
  currentChatUser.value = user
  loadHistory()
  clearUnreadDot() 
}

// 3. 加载历史记录
const loadHistory = () => {
  if (!currentChatUser.value) return

  axios.get('http://localhost:8080/chat/history', {
    params: {
      senderId: myUserId,
      receiverId: currentChatUser.value.userId
    }
  }).then(res => {
    if (res.data.code === 200) {
      const oldLength = messageList.value.length
      const newLength = res.data.data.length
      messageList.value = res.data.data
      if (newLength > oldLength) scrollToBottom()
    }
  })
}

// 4. 发送消息
const handleSend = () => {
  if (!inputContent.value.trim()) return
  if (!currentChatUser.value) return

  const msg = {
    senderId: myUserId,
    receiverId: currentChatUser.value.userId,
    content: inputContent.value
  }

  axios.post('http://localhost:8080/chat/send', msg)
    .then(res => {
      if (res.data.code === 200) {
        inputContent.value = '' 
        loadHistory() 
        scrollToBottom() 
        
        const exist = contacts.value.find(u => u.userId === currentChatUser.value.userId)
        if (!exist) {
          loadContacts() 
        }
      } else {
        ElMessage.error('发送失败')
      }
    })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (msgListRef.value) {
      msgListRef.value.scrollTop = msgListRef.value.scrollHeight
    }
  })
}

onMounted(async () => {
  clearUnreadDot()
  await loadContacts()

  const targetId = route.query.targetId
  if (targetId) {
    const existUser = contacts.value.find(u => u.userId == targetId)
    if (existUser) {
      selectUser(existUser)
    } else {
      axios.get(`http://localhost:8080/sys-user/info/${targetId}`).then(res => {
        if (res.data.code === 200) {
          const newUser = res.data.data
          contacts.value.unshift(newUser)
          selectUser(newUser)
        }
      })
    }
  }

  timer = setInterval(() => {
    if (currentChatUser.value) {
      loadHistory()
      clearUnreadDot() 
    }
  }, 1500)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
/* 容器居中与背景 */
.geek-chat-container {
  height: calc(100vh - 100px);
  padding: 10px 0;
  display: flex;
  justify-content: center;
  animation: fadeIn 0.3s ease-out;
}

/* 主体卡片：类似 IDE 的分屏设计 */
.chat-card {
  width: 100%;
  max-width: 1100px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
  display: flex;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

/* ================= 左侧：会话列表 ================= */
.session-list {
  width: 280px;
  background-color: #f8fafc;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.list-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  font-size: 15px;
  color: #1e293b;
  border-bottom: 1px solid #e2e8f0;
  background: #f1f5f9;
}
.cmd-icon { margin-right: 8px; color: #3b82f6; }

.contacts-wrapper {
  flex: 1;
  overflow-y: auto;
}
.contacts-wrapper::-webkit-scrollbar { width: 4px; }
.contacts-wrapper::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }

.contact-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  border-bottom: 1px solid #f1f5f9;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}
.contact-item:hover { background-color: #f1f5f9; }
.contact-item.active {
  background-color: #eff6ff;
  border-left-color: #3b82f6;
}

.avatar-border { border: 2px solid #ffffff; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.contact-info { margin-left: 12px; }
.contact-info .name { font-size: 14px; font-weight: 600; color: #334155; margin-bottom: 4px; }
.contact-info .status-text { font-size: 12px; color: #64748b; display: flex; align-items: center; }

.status-dot {
  width: 6px;
  height: 6px;
  background-color: #10b981;
  border-radius: 50%;
  margin-right: 6px;
  box-shadow: 0 0 4px #10b981;
}

/* ================= 右侧：聊天面板 ================= */
.chat-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
}

/* 状态1：空闲终端 */
.empty-state {
  flex: 1;
  background-color: #1e293b;
  color: #a6accd;
  padding: 40px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  display: flex;
  flex-direction: column;
}
.prompt { color: #82aaff; font-weight: bold; }
.success { color: #c3e88d; }
.blink-cursor { color: #fff; animation: blink 1s step-end infinite; font-weight: bold; font-size: 16px; }

/* 状态2：通信头部 */
.chat-header {
  height: 60px;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e2e8f0;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(4px);
}
.header-left { display: flex; align-items: center; }
.pulse-dot {
  width: 8px; height: 8px; background: #3b82f6; border-radius: 50%; margin-right: 10px;
  box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.7);
  animation: pulse 2s infinite;
}
.header-text { font-size: 15px; color: #64748b; }
.target-name { color: #0f172a; font-weight: bold; }
.protocol-text { font-family: 'Courier New', Courier, monospace; font-size: 12px; color: #94a3b8; }

/* 消息流 */
.message-list {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: #f8fafc;
  background-image: linear-gradient(to right, rgba(0, 0, 0, 0.02) 1px, transparent 1px), linear-gradient(to bottom, rgba(0, 0, 0, 0.02) 1px, transparent 1px);
  background-size: 20px 20px;
}
.message-list::-webkit-scrollbar { width: 6px; }
.message-list::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }

.message-wrapper {
  display: flex;
  margin-bottom: 24px;
  align-items: flex-start;
  animation: slideUp 0.3s ease-out;
}
.msg-avatar { border: 2px solid #fff; box-shadow: 0 2px 6px rgba(0,0,0,0.05); margin: 0 12px; }

.bubble {
  background-color: #ffffff;
  color: #334155;
  padding: 12px 16px;
  border-radius: 2px 12px 12px 12px; /* 极客风：左上角直角 */
  font-size: 14px;
  line-height: 1.6;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  border: 1px solid #e2e8f0;
  max-width: 500px;
  word-break: break-all;
}

/* 我的消息 */
.is-me { flex-direction: row-reverse; }
.is-me .bubble {
  background-color: #3b82f6;
  color: #ffffff;
  border: none;
  border-radius: 12px 2px 12px 12px; /* 右上角直角 */
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

/* 底部输入区 */
.chat-input-area {
  padding: 16px 24px;
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
}
.input-wrapper {
  display: flex;
  align-items: center;
  background: #f1f5f9;
  border-radius: 8px;
  padding: 6px 6px 6px 16px;
  border: 1px solid transparent;
  transition: all 0.3s;
}
.input-wrapper:focus-within {
  background: #ffffff;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59,130,246,0.1);
}
.prompt-symbol {
  color: #3b82f6;
  font-weight: bold;
  margin-right: 12px;
  font-family: 'Courier New', Courier, monospace;
}
.geek-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 15px;
  color: #334155;
  padding: 8px 0;
}
.geek-input::placeholder { color: #94a3b8; }

.send-btn {
  background-color: #3b82f6;
  color: #ffffff;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  font-size: 13px;
  transition: all 0.2s;
}
.send-btn:hover:not(:disabled) { background-color: #2563eb; transform: translateY(-1px); }
.send-btn:disabled { background-color: #94a3b8; cursor: not-allowed; opacity: 0.7; }

/* 动画效果 */
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }
@keyframes pulse { 0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.7); } 70% { transform: scale(1); box-shadow: 0 0 0 6px rgba(59, 130, 246, 0); } 100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(59, 130, 246, 0); } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
@keyframes slideUp { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
</style>