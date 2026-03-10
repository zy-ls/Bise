<template>
  <div class="geek-detail-container">
    <article class="article-card">
      
      <header class="article-header">
        <h1 class="title">{{ note.title }}</h1>
        
        <div class="article-tags" v-if="note.tags">
          <span class="geek-tag" v-for="(tag, index) in note.tags.split(',')" :key="index">
            {{ tag.trim() }}
          </span>
        </div>
        
        <div class="article-meta">
          <div class="author-info">
            <el-avatar 
              :size="36" 
              :src="note.authorAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
              class="author-avatar"
            />
            <div class="author-text">
              <span class="author-name">{{ note.authorName || '匿名极客 ' + (note.userId || '') }}</span>
              <span class="time">{{ formatTime(note.createTime) }}</span>
            </div>
          </div>

          <div class="action-group">
            <el-button 
              class="geek-btn chat-btn" 
              plain 
              @click="handleChat"
            >
              <el-icon><ChatDotRound /></el-icon> <span>Private Msg</span>
            </el-button>

            <el-button 
              class="geek-btn collect-btn" 
              :class="{ 'is-active': isCollected }"
              plain 
              @click="handleCollect"
            >
              <el-icon v-if="isCollected"><StarFilled /></el-icon>
              <el-icon v-else><Star /></el-icon>
              <span>{{ isCollected ? 'Collected' : 'Collect' }}</span>
            </el-button>
          </div>
        </div>
      </header>

      <main class="article-body ql-editor" v-html="note.contentHtml"></main>

      <section class="comment-section">
        <div class="section-title">
          <span class="cmd-prompt">~/comments</span> 
          <span class="count">({{ commentList.length }} threads)</span>
        </div>
        
        <div class="comment-input-box">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="System.out.println('友善的评论是交流的起点...');"
            class="geek-textarea"
          />
          <div class="input-actions">
            <el-button class="submit-btn" type="primary" @click="submitComment">
              > 执行发送
            </el-button>
          </div>
        </div>

        <div v-if="commentList.length === 0" class="empty-terminal">
          [root@localhost ~]# cat comments.log<br>
          cat: comments.log: No such file or directory (暂无评论，快来抢沙发)
        </div>

        <div class="comment-list" v-else>
          <div v-for="item in commentList" :key="item.commentId" class="comment-item">
            <el-avatar 
              :size="36" 
              :src="item.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
              class="comment-avatar"
            />
            <div class="comment-main">
              <div class="comment-user">
                <span class="username">{{ item.nickname || '用户 ' + item.userId }}</span>
                <span class="time">{{ formatTime(item.createTime) }}</span>
              </div>
              <div class="comment-content">
                {{ item.content }}
              </div>
            </div>
          </div>
        </div>
      </section>

    </article>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { ChatDotRound, Star, StarFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const noteId = route.query.id

const note = ref({})
const commentList = ref([])
const newComment = ref('')
const isCollected = ref(false)

// 1. 加载笔记详情
const loadNote = () => {
  axios.get(`http://localhost:8080/note-info/detail/${noteId}`)
    .then(res => {
      if (res.data.code === 200) {
        note.value = res.data.data
      }
    })
}

// 2. 加载评论列表
const loadComments = () => {
  axios.get(`http://localhost:8080/comment/list?noteId=${noteId}`)
    .then(res => {
      if (res.data.code === 200) {
        commentList.value = res.data.data
      }
    })
}

// 3. 提交评论
const submitComment = () => {
  if (!newComment.value.trim()) return ElMessage.warning('Error: 评论内容不能为空')
  
  axios.post('http://localhost:8080/comment/save', {
    noteId: noteId,
    userId: userStore.user.userId,
    content: newComment.value,
    type: 1 
  }).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('Process finished: 评论成功')
      newComment.value = ''
      loadComments()
    }
  })
}

// 4. 跳转去聊天
const handleChat = () => {
  if (!note.value.userId) return
  if (note.value.userId === userStore.user.userId) {
    return ElMessage.warning('System Prompt: 无法与自己建立通信连接')
  }
  router.push(`/chat?targetId=${note.value.userId}`)
}

// 5. 检查收藏状态
const checkCollectStatus = () => {
  axios.get('http://localhost:8080/collect/check', {
    params: { userId: userStore.user.userId, noteId: noteId }
  }).then(res => {
    if (res.data.code === 200) {
      isCollected.value = res.data.data
    }
  })
}

// 6. 点击收藏/取消
const handleCollect = () => {
  axios.post('http://localhost:8080/collect/toggle', {
    userId: userStore.user.userId,
    noteId: noteId
  }).then(res => {
    if (res.data.code === 200) {
      isCollected.value = !isCollected.value
      ElMessage.success(res.data.data)
    }
  })
}

// 时间格式化工具
const formatTime = (time) => time ? time.replace('T', ' ') : ''

// 帮您将两个 onMounted 合并为一个，代码更规范
onMounted(() => {
  if (noteId) {
    loadNote()
    loadComments()
    checkCollectStatus()
  }
})
</script>

<style scoped>
/* 1. 整体容器：收窄一点让阅读更聚焦，背景透明融入外层网格 */
.geek-detail-container { 
  max-width: 960px; 
  margin: 0 auto; 
  animation: fadeIn 0.4s ease-out;
}

/* 核心文章卡片：去除默认边框，使用极客风微阴影 */
.article-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  padding: 40px;
  margin-bottom: 40px;
}

/* -------- 头部区域 -------- */
.article-header {
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 24px;
  margin-bottom: 30px;
}

.title { 
  margin: 0 0 16px 0; 
  font-size: 32px; 
  color: #0f172a; 
  font-weight: 800; 
  line-height: 1.4;
  letter-spacing: 0.5px;
}

/* 标签区 */
.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;
}
.geek-tag {
  background: #f1f5f9;
  color: #475569;
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 6px;
  font-family: 'Courier New', Courier, monospace;
}
.geek-tag::before { content: '<'; color: #94a3b8; }
.geek-tag::after { content: '/>'; color: #94a3b8; }

/* 元数据与操作区 */
.article-meta { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.author-avatar {
  border: 2px solid #f1f5f9;
}
.author-text {
  display: flex;
  flex-direction: column;
}
.author-name {
  font-weight: 600;
  color: #334155;
  font-size: 15px;
}
.time {
  color: #94a3b8;
  font-size: 13px;
  font-family: 'Courier New', Courier, monospace;
  margin-top: 2px;
}

/* 极客风操作按钮 */
.action-group {
  display: flex;
  gap: 12px;
}
.geek-btn {
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  border-radius: 8px;
  transition: all 0.3s;
}
.chat-btn {
  color: #3b82f6;
  border-color: #bfdbfe;
  background-color: #eff6ff;
}
.chat-btn:hover { background-color: #dbeafe; }

.collect-btn {
  color: #64748b;
  border-color: #e2e8f0;
  background-color: #f8fafc;
}
.collect-btn:hover { color: #f59e0b; border-color: #fde68a; background-color: #fffbeb; }
.collect-btn.is-active { color: #d97706; border-color: #fde68a; background-color: #fef3c7; }


/* -------- 正文阅读区优化 -------- */
.article-body { 
  min-height: 200px; 
  font-size: 16px; 
  line-height: 1.8; 
  color: #334155;
  letter-spacing: 0.3px;
}

/* 覆盖 Quill 的默认样式，让文章排版更高级 */
:deep(.article-body p) { margin-bottom: 1em; }
:deep(.article-body h2), :deep(.article-body h3) { color: #0f172a; margin-top: 1.5em; margin-bottom: 0.8em; font-weight: 700; }
:deep(.article-body code) { background: #f1f5f9; color: #ef4444; padding: 2px 6px; border-radius: 4px; font-family: monospace; }
:deep(.article-body blockquote) { border-left: 4px solid #3b82f6; background: #f8fafc; padding: 10px 16px; margin: 0 0 16px 0; color: #64748b; }

/* 图片约束 */
:deep(.article-body img) {
  max-width: 100%; 
  height: auto;    
  display: block;
  margin: 24px auto; 
  border-radius: 8px; 
  box-shadow: 0 4px 12px rgba(0,0,0,0.08); 
}


/* -------- 评论区终端风 -------- */
.comment-section { 
  margin-top: 60px; 
  background: #f8fafc;
  border-radius: 12px;
  padding: 30px;
  border: 1px solid #e2e8f0;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 24px;
  font-family: 'Courier New', Courier, monospace;
}
.cmd-prompt { color: #10b981; margin-right: 8px; }
.count { color: #94a3b8; font-size: 14px; }

/* 评论输入 */
.comment-input-box {
  background: #ffffff;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 30px;
  transition: border-color 0.3s;
}
.comment-input-box:focus-within { border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1); }

:deep(.geek-textarea .el-textarea__inner) {
  border: none !important;
  box-shadow: none !important;
  padding: 0;
  font-size: 15px;
  color: #334155;
  resize: none;
}
.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
.submit-btn {
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
}

/* 评论列表 */
.empty-terminal {
  font-family: monospace;
  color: #94a3b8;
  padding: 20px;
  background: #1e293b;
  border-radius: 8px;
  line-height: 1.5;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.comment-item {
  display: flex;
  gap: 16px;
}
.comment-avatar { border: 2px solid #ffffff; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.comment-main {
  flex: 1;
  background: #ffffff;
  padding: 16px;
  border-radius: 0 12px 12px 12px;
  border: 1px solid #e2e8f0;
}
.comment-user { 
  display: flex; 
  justify-content: space-between;
  margin-bottom: 8px; 
}
.comment-user .username { font-weight: 600; color: #3b82f6; font-size: 14px; }
.comment-content { font-size: 15px; color: #475569; line-height: 1.6; }

/* 渐入动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>