<template>
  <div class="chat-container">
    <el-card class="box-card" :body-style="{ padding: '0px', height: '100%' }">
      <div class="chat-layout">
        
        <div class="contact-list">
          <div class="list-header">💬 在线用户</div>
          <div 
            v-for="user in contacts" 
            :key="user.userId"
            class="contact-item"
            :class="{ active: currentChatUser?.userId === user.userId }"
            @click="selectUser(user)"
          >
            <el-avatar :size="40" :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <div class="contact-info">
              <div class="name">{{ user.nickname || user.username }}</div>
              <div class="status-text">点击聊天</div>
            </div>
          </div>
        </div>

        <div class="chat-window">
          <div class="chat-header">
            <span v-if="currentChatUser">正在与 <b>{{ currentChatUser.nickname || currentChatUser.username }}</b> 聊天</span>
            <span v-else>未选择联系人</span>
          </div>

          <div class="message-list" ref="msgListRef">
            <div v-if="!currentChatUser" class="empty-tip">👈 请在左侧选择一位好友开始聊天</div>
            
            <div 
              v-else
              v-for="msg in messageList" 
              :key="msg.messageId"
              class="message-item"
              :class="{ 'my-message': msg.senderId === myUserId }"
            >
              <el-avatar v-if="msg.senderId !== myUserId" :size="36" :src="currentChatUser.avatar" class="avatar-left" />
              
              <div class="bubble">
                {{ msg.content }}
              </div>

              <el-avatar v-if="msg.senderId === myUserId" :size="36" :src="userStore.user.avatar" class="avatar-right" />
            </div>
          </div>

          <div class="chat-input" v-if="currentChatUser">
            <el-input 
              v-model="inputContent" 
              placeholder="请输入消息，按 Enter 发送..." 
              @keyup.enter="handleSend"
            >
              <template #append>
                <el-button type="primary" @click="handleSend">发送</el-button>
              </template>
            </el-input>
          </div>
        </div>

      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router' // 引入 useRoute
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute() // 获取路由参数
const userStore = useUserStore()
const myUserId = userStore.user.userId

const contacts = ref([])          
const currentChatUser = ref(null) 
const messageList = ref([])       
const inputContent = ref('')      
const msgListRef = ref(null)      

let timer = null 

// 1. 获取联系人 (注意：改成了 async/await，为了等待数据返回)
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
  scrollToBottom()
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
        
        // 发送成功后，如果这个人不在列表里（是新朋友），刷新一下列表
        const exist = contacts.value.find(u => u.userId === currentChatUser.value.userId)
        if (!exist) {
          loadContacts() // 重新拉取列表，他就会出现在里面了
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

// 🔥 核心修改：挂载逻辑
onMounted(async () => {
  // 1. 先加载已有的联系人
  await loadContacts()

  // 2. 检查是不是从笔记页面跳过来的 (URL里有没有 targetId)
  const targetId = route.query.targetId
  if (targetId) {
    // 2.1 看看这个人是不是已经是熟人了
    const existUser = contacts.value.find(u => u.userId == targetId)
    
    if (existUser) {
      // 如果是熟人，直接选中
      selectUser(existUser)
    } else {
      // 2.2 如果是新朋友，需要去后台查一下他的头像和名字
      axios.get(`http://localhost:8080/sys-user/info/${targetId}`).then(res => {
        if (res.data.code === 200) {
          const newUser = res.data.data
          // 临时把他加到列表最上面，方便我聊天
          contacts.value.unshift(newUser)
          // 选中他
          selectUser(newUser)
        }
      })
    }
  }

  // 3. 启动轮询
  timer = setInterval(() => {
    if (currentChatUser.value) {
      loadHistory()
    }
  }, 1500)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.chat-container { height: calc(100vh - 120px); } /* 减去顶部导航的高度 */
.box-card { height: 100%; display: flex; flex-direction: column; }

.chat-layout { display: flex; height: 600px; width: 100%; }

/* 左侧样式 */
.contact-list { width: 260px; border-right: 1px solid #eee; background-color: #f7f7f7; overflow-y: auto; }
.list-header { padding: 15px; font-weight: bold; background: #fff; border-bottom: 1px solid #eee; }
.contact-item { display: flex; align-items: center; padding: 12px; cursor: pointer; transition: 0.2s; border-bottom: 1px solid #f0f0f0;}
.contact-item:hover { background-color: #e6f1fc; }
.contact-item.active { background-color: #cce5ff; } /* 选中高亮 */
.contact-info { margin-left: 10px; }
.contact-info .name { font-size: 14px; font-weight: 500; color: #333; }
.contact-info .status-text { font-size: 12px; color: #999; }

/* 右侧样式 */
.chat-window { flex: 1; display: flex; flex-direction: column; background-color: #fff; }
.chat-header { padding: 0 20px; height: 50px; line-height: 50px; border-bottom: 1px solid #eee; font-size: 15px; background-color: #fafafa;}

.message-list { flex: 1; padding: 20px; overflow-y: auto; background-color: #f2f2f2; }
.empty-tip { text-align: center; color: #999; margin-top: 50px; }

.message-item { display: flex; margin-bottom: 15px; align-items: flex-start; }
.avatar-left { margin-right: 10px; }
.avatar-right { margin-left: 10px; }

.bubble { 
  background-color: #fff; 
  padding: 10px 14px; 
  border-radius: 6px; 
  max-width: 60%; 
  line-height: 1.5;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
  word-break: break-all;
}

/* 我发的消息：靠右，气泡变绿 */
.my-message { flex-direction: row-reverse; }
.my-message .bubble { background-color: #95ec69; }

.chat-input { padding: 15px; border-top: 1px solid #eee; background: #fff; }
</style>