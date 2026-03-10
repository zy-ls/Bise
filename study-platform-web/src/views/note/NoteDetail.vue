<template>
  <div class="note-detail-container">
    <el-card>
      <template #header>
        <div class="note-header">
          <h1 class="title">{{ note.title }}</h1>
          
          <div class="note-meta">
            <div class="author-info">
              <el-avatar 
                :size="30" 
                :src="note.authorAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
              />
              <span class="author-name">{{ note.authorName || '用户 ' + note.userId }}</span>
              
              <el-button 
                type="primary" 
                link 
                size="small" 
                @click="handleChat" 
                style="margin-left: 10px;"
              >
                <el-icon><ChatDotRound /></el-icon> 私信TA
              </el-button>

              <el-button 
                :type="isCollected ? 'warning' : 'info'" 
                link 
                size="small" 
                @click="handleCollect"
              >
                <el-icon v-if="isCollected"><StarFilled /></el-icon>
                <el-icon v-else><Star /></el-icon>
                {{ isCollected ? '已收藏' : '收藏' }}
              </el-button>
            </div>

            <span class="time">{{ formatTime(note.createTime) }}</span>
          </div>
        </div>
      </template>

      <div class="note-content ql-editor" v-html="note.contentHtml"></div>

      <el-divider />

      <div class="comment-section">
        <h3>💬 评论 ({{ commentList.length }})</h3>
        
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="友善的评论是交流的起点..."
          />
          <el-button type="primary" style="margin-top: 10px; float: right;" @click="submitComment">
            发表评论
          </el-button>
        </div>
        <div style="clear: both; margin-bottom: 30px;"></div>

        <div v-if="commentList.length === 0" style="text-align: center; color: #999; margin: 20px 0;">
          暂无评论，快来抢沙发吧~
        </div>

        <div v-for="item in commentList" :key="item.commentId" class="comment-item">
          <div class="comment-user">
            <el-avatar 
              :size="30" 
              :src="item.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
            />
            <span class="username">{{ item.nickname || '用户 ' + item.userId }}</span>
            <span class="time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="comment-content">
            {{ item.content }}
          </div>
          <el-divider border-style="dashed" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router' // 引入 useRouter 用于跳转
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import '@vueup/vue-quill/dist/vue-quill.snow.css' // 引入 Quill 样式确保正文显示正常
import { ChatDotRound, Star, StarFilled } from '@element-plus/icons-vue'


const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const noteId = route.query.id

const note = ref({})
const commentList = ref([])
const newComment = ref('')
const isCollected = ref(false) // 收藏状态

// 1. 加载笔记详情
const loadNote = () => {
  axios.get(`http://localhost:8080/note-info/detail/${noteId}`)
    .then(res => {
      if (res.data.code === 200) {
        note.value = res.data.data
        
        // ⚠️ 如果后端 /detail 接口没给你返回 authorName，这里可以临时补救一下：
        // 如果你需要显示作者名但后端没传，可以在这里再调一次查用户的接口。
        // 但最好的方式是去后端 InfoController 修改 getNoteDetail 方法。
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
  if (!newComment.value.trim()) return ElMessage.warning('写点什么吧')
  
  axios.post('http://localhost:8080/comment/save', {
    noteId: noteId,
    userId: userStore.user.userId,
    content: newComment.value,
    type: 1 
  }).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('评论成功')
      newComment.value = ''
      loadComments() // 刷新评论列表
    }
  })
}

// 4. 跳转去聊天 (私信TA)
const handleChat = () => {
  if (!note.value.userId) return
  
  // 防止自己给自己发消息
  if (note.value.userId === userStore.user.userId) {
    return ElMessage.warning('不能跟自己聊天哦')
  }

  // 跳转到 ChatView，并带上目标 ID
  router.push(`/chat?targetId=${note.value.userId}`)
}

// 时间格式化工具
const formatTime = (time) => time ? time.replace('T', ' ') : ''

onMounted(() => {
  if (noteId) {
    loadNote()
    loadComments()
  }
})


// 检查收藏状态
const checkCollectStatus = () => {
  // 注意：这里需要传入 userId 和 noteId
  axios.get('http://localhost:8080/collect/check', {
    params: { userId: userStore.user.userId, noteId: noteId }
  }).then(res => {
    if (res.data.code === 200) {
      isCollected.value = res.data.data
    }
  })
}

// 点击收藏/取消
const handleCollect = () => {
  axios.post('http://localhost:8080/collect/toggle', {
    userId: userStore.user.userId,
    noteId: noteId
  }).then(res => {
    if (res.data.code === 200) {
      isCollected.value = !isCollected.value
      ElMessage.success(res.data.data) // 提示收藏成功或取消
    }
  })
}

onMounted(() => {
  if (noteId) {
    loadNote()
    loadComments()
    checkCollectStatus() // 👈 记得调用这个！
  }
})
</script>

<style scoped>
/* 1. 扩大整体容器宽度，让排版更舒展 */
.note-detail-container { 
  max-width: 1200px; /* 从 900px 扩大到 1200px */
  margin: 20px auto; /* 增加上下边距，让页面不那么贴边 */
}

.note-header { text-align: center; margin-bottom: 30px; }
.title { margin: 0 0 15px 0; font-size: 28px; color: #333; font-weight: bold; }

.note-meta { 
  display: flex; 
  justify-content: center; 
  align-items: center; 
  gap: 20px; 
  color: #999; 
  font-size: 14px; 
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-weight: bold;
  color: #555;
}

/* 2. 优化正文排版间距 */
.note-content { 
  min-height: 300px; 
  padding: 20px 40px; /* 增加左右内边距，像真正的文章一样 */
  font-size: 16px; 
  line-height: 2; /* 增大行高，提升阅读体验 */
  color: #333;
  overflow-x: hidden; /* 防止内容过宽导致页面出现横向滚动条 */
}

/* 3. 核心修复：强制限制富文本内容中的图片大小 */
:deep(.note-content img) {
  max-width: 100%; /* 图片最大宽度不能超过外层容器 */
  height: auto;    /* 高度自动等比缩放，防止图片变形 */
  display: block;
  margin: 15px auto; /* 图片居中显示，并增加上下间距 */
  border-radius: 6px; /* 稍微加点圆角，更好看 */
  box-shadow: 0 2px 10px rgba(0,0,0,0.05); /* 加一点淡淡的阴影 */
}

/* 评论区样式保持或微调 */
.comment-section { margin-top: 40px; padding: 0 20px; }
.comment-item { padding: 15px 0; }
.comment-user { display: flex; align-items: center; gap: 10px; font-size: 14px; color: #666; margin-bottom: 8px; }
.comment-content { padding-left: 40px; font-size: 15px; color: #333; line-height: 1.6; }
.username { font-weight: 500; color: #409EFF; }
</style>