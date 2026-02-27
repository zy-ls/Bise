<template>
  <div class="collect-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>🌟 我的收藏箱</span>
        </div>
      </template>

      <div v-loading="loading">
        <el-empty v-if="noteList.length === 0" description="还没收藏任何笔记，快去广场逛逛吧" />
        
        <div v-for="note in noteList" :key="note.noteId" class="note-item" @click="goDetail(note.noteId)">
          <div class="note-main">
            <h3 class="note-title">
               <el-tag size="small" type="warning" effect="dark" style="margin-right: 5px">收藏</el-tag>
               {{ note.title }}
            </h3>
            <p class="note-summary">点击查看详情...</p>
            
            <div class="note-meta">
              <span>发布时间：{{ formatTime(note.createTime) }}</span>
              <el-button type="danger" link size="small" @click.stop="handleUncollect(note.noteId)">取消收藏</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const noteList = ref([])
const loading = ref(false)

// 加载收藏列表
const fetchCollectList = () => {
  loading.value = true
  axios.get(`http://localhost:8080/collect/list?userId=${userStore.user.userId}`)
    .then(res => {
      if (res.data.code === 200) {
        noteList.value = res.data.data
      }
    })
    .finally(() => loading.value = false)
}

// 跳转详情
const goDetail = (id) => {
  router.push(`/note/detail?id=${id}`)
}

// 取消收藏 (阻止冒泡 .stop 防止触发跳转)
const handleUncollect = (noteId) => {
  ElMessageBox.confirm('确定要移除这条收藏吗？', '提示', { type: 'warning' })
    .then(() => {
      // 调用 toggle 接口，再次调用即为取消
      axios.post('http://localhost:8080/collect/toggle', {
        userId: userStore.user.userId,
        noteId: noteId
      }).then(res => {
        if (res.data.code === 200) {
          ElMessage.success('已移除')
          fetchCollectList() // 刷新列表
        }
      })
    })
}

const formatTime = (time) => time ? time.replace('T', ' ') : ''

onMounted(() => {
  fetchCollectList()
})
</script>

<style scoped>
.note-item {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}
.note-item:hover { background-color: #fafafa; }
.note-title { margin: 0 0 10px 0; font-size: 18px; color: #333; }
.note-meta { display: flex; justify-content: space-between; align-items: center; color: #999; font-size: 12px; margin-top: 10px;}
</style>