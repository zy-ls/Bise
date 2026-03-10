<template>
  <div class="geek-group-detail">
    
    <header class="group-banner">
      <div class="banner-content">
        <div class="hub-icon">{{ groupInfo.name ? groupInfo.name.substring(0, 1).toUpperCase() : '#' }}</div>
        <div class="group-info">
          <div class="title-row">
            <h1 class="title">{{ groupInfo.name }}</h1>
            
            <el-button 
              v-if="userStore.user.userId === groupInfo.creatorId"
              type="danger" 
              plain 
              size="small" 
              class="danger-btn"
              @click="handleDeleteGroup"
            >
              <el-icon><Delete /></el-icon> DISBAND HUB (解散圈子)
            </el-button>

          </div>
          <p class="desc">{{ groupInfo.description || '暂无简介' }}</p>
        </div>
      </div>
    </header>

    <div class="publish-area">
      <el-input
        v-model="newPostContent"
        type="textarea"
        :rows="3"
        placeholder="在圈子里分享你的技术见解或提问..."
        class="geek-textarea"
      />
      <div class="publish-action">
        <el-button type="primary" class="publish-btn" @click="submitPost">
          EXECUTE ( 发布 )
        </el-button>
      </div>
    </div>

    <div class="post-feed">
      <div v-if="postList.length === 0" class="empty-state">
        [ 终端日志 ]: 当前圈子非常安静，快来发布第一条动态吧。
      </div>

      <div v-for="post in postList" :key="post.postId" class="post-card">
        <div class="post-header">
          <el-avatar :size="36" :src="post.authorAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
          <div class="author-meta">
            <span class="author-name">{{ post.authorName }}</span>
            <span class="post-time">{{ formatTime(post.createTime) }}</span>
          </div>
        </div>
        <div class="post-body">
          {{ post.content }}
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue' // 引入删除图标

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const groupId = route.query.id

const groupInfo = ref({})
const postList = ref([])
const newPostContent = ref('')

// 1. 获取小组详情
const loadGroupInfo = () => {
  axios.get(`http://localhost:8080/study-group/detail/${groupId}`).then(res => {
    if (res.data.code === 200) groupInfo.value = res.data.data
  })
}

// 2. 获取帖子列表
const loadPosts = () => {
  axios.get(`http://localhost:8080/group-post/list?groupId=${groupId}`).then(res => {
    if (res.data.code === 200) postList.value = res.data.data
  })
}

// 3. 发布帖子
const submitPost = () => {
  if (!newPostContent.value.trim()) return ElMessage.warning('内容不能为空')
  
  axios.post('http://localhost:8080/group-post/publish', {
    groupId: groupId,
    userId: userStore.user.userId,
    content: newPostContent.value
  }).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('发布成功')
      newPostContent.value = ''
      loadPosts() 
    }
  })
}

// 4. 💡 新增：解散圈子逻辑
const handleDeleteGroup = () => {
  ElMessageBox.confirm(
    'WARNING: 解散圈子将永久清空内所有的成员记录和讨论帖子，此操作不可逆！确定要执行吗？', 
    'System Prompt', 
    {
      confirmButtonText: 'CONFIRM (确定)',
      cancelButtonText: 'ABORT (取消)',
      type: 'error',
    }
  ).then(() => {
    // 发送删除请求，带上当前登录用户的 ID 进行权限核验
    axios.delete(`http://localhost:8080/study-group/delete/${groupId}?userId=${userStore.user.userId}`)
      .then(res => {
        if (res.data.code === 200) {
          ElMessage.success(res.data.data || '圈子已解散')
          // 删除成功后，将用户安全地送回广场大厅
          router.push('/group/square')
        } else {
          ElMessage.error(res.data.msg)
        }
      })
  }).catch(() => {})
}

const formatTime = (time) => time ? time.replace('T', ' ') : ''

onMounted(() => {
  if (groupId) {
    loadGroupInfo()
    loadPosts()
  }
})
</script>

<style scoped>
.geek-group-detail { max-width: 800px; margin: 0 auto; padding: 20px 0; animation: fadeIn 0.4s ease-out; }

/* 头部 Banner */
.group-banner { background: linear-gradient(135deg, #1e293b 0%, #334155 100%); border-radius: 12px; padding: 40px; margin-bottom: 30px; color: #fff; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
.banner-content { display: flex; align-items: flex-start; gap: 24px; }
.hub-icon { width: 70px; height: 70px; background: #3b82f6; border-radius: 16px; font-size: 32px; font-weight: bold; display: flex; justify-content: center; align-items: center; border: 4px solid rgba(255,255,255,0.2); font-family: 'Courier New', Courier, monospace; flex-shrink: 0;}
.group-info { flex: 1; }
.title-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.title { margin: 0; font-size: 28px; font-weight: 800; }
.desc { margin: 0; color: #94a3b8; font-size: 15px; }

/* 极客风危险按钮 */
.danger-btn { font-family: 'Courier New', Courier, monospace; font-weight: bold; }

/* 发布区 */
.publish-area { background: #fff; padding: 20px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.03); margin-bottom: 30px; border: 1px solid #e2e8f0; }
:deep(.geek-textarea .el-textarea__inner) { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 15px; color: #334155; box-shadow: none; padding: 12px; }
:deep(.geek-textarea .el-textarea__inner:focus) { border-color: #3b82f6; background: #fff; }
.publish-action { display: flex; justify-content: flex-end; margin-top: 16px; }
.publish-btn { font-family: 'Courier New', Courier, monospace; font-weight: bold; padding: 10px 24px; border-radius: 8px; }

/* 帖子列表 */
.post-feed { display: flex; flex-direction: column; gap: 20px; }
.empty-state { background: #1e293b; color: #94a3b8; padding: 30px; border-radius: 8px; text-align: center; font-family: monospace; }
.post-card { background: #fff; border-radius: 12px; padding: 24px; border: 1px solid #f1f5f9; box-shadow: 0 2px 8px rgba(0,0,0,0.02); transition: all 0.3s; }
.post-card:hover { border-color: #bfdbfe; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.post-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.author-meta { display: flex; flex-direction: column; }
.author-name { font-weight: 600; color: #1e293b; font-size: 15px; }
.post-time { font-size: 12px; color: #94a3b8; font-family: 'Courier New', Courier, monospace; margin-top: 2px; }
.post-body { font-size: 15px; color: #475569; line-height: 1.6; white-space: pre-wrap; word-break: break-all; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>