<template>
  <div class="square-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span>🌍 笔记广场</span>
            <el-input
              v-model="keyword"
              placeholder="搜索笔记标题或内容..."
              class="search-input"
              clearable
              @clear="fetchSquareData"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
          <el-button type="primary" plain @click="router.push('/note/edit')">我也要发一篇</el-button>
        </div>
      </template>

      <div v-loading="loading">
        <el-empty v-if="noteList.length === 0" description="暂时还没有人发布笔记哦" />
        
        <div 
          v-for="note in noteList" 
          :key="note.noteId || note.id" 
          class="note-item" 
          @click="goDetail(note)"
        >
          <div class="note-main">
            <h3 class="note-title">{{ note.title }}</h3>
            
            <p class="note-summary">
              {{ (note.summary || note.content || '暂无内容').substring(0, 100) }}...
            </p>
            
            <div class="note-meta">
              <div class="author-box">
                <el-avatar :size="24" :src="note.authorAvatar || note.userAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                <span class="author-name">{{ note.authorName || note.userName || note.nickname || '匿名' }}</span>
              </div>
              <span class="time">{{ formatTime(note.createTime) }}</span>
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
import axios from 'axios'
import { Search } from '@element-plus/icons-vue' 

const keyword = ref('')
const router = useRouter()
const noteList = ref([])
const loading = ref(false)

// 1. 获取默认列表 (走 MySQL)
const fetchSquareData = () => {
  loading.value = true
  axios.get('http://localhost:8080/note-info/list')
    .then(res => {
      if (res.data.code === 200) {
        noteList.value = res.data.data
        console.log("MySQL数据:", res.data.data) // 调试用
      }
    })
    .finally(() => loading.value = false)
}

// 2. 搜索 (走 OpenSearch)
const handleSearch = () => {
  // 如果关键词为空，这就回退到查全部
  if (!keyword.value || !keyword.value.trim()) {
    fetchSquareData()
    return
  }
  
  loading.value = true
  console.log("正在搜索:", keyword.value)

  axios.get(`http://localhost:8080/search/note?keyword=${keyword.value}`)
    .then(res => {
      console.log("OpenSearch返回:", res.data) // 🟢 请按F12看这里有没有数据
      if (res.data.code === 200) {
        noteList.value = res.data.data
      }
    })
    .catch(err => {
      console.error("搜索报错:", err)
    })
    .finally(() => loading.value = false)
}

// 3. 跳转详情 (兼容两种 ID)
const goDetail = (note) => {
  // 优先取 noteId，没有就取 id
  const targetId = note.noteId || note.id
  if (targetId) {
    router.push(`/note/detail?id=${targetId}`)
  } else {
    console.error("无法获取笔记ID", note)
  }
}

// 4. 时间格式化 (修复报错版)
const formatTime = (time) => {
  if (!time) return '';
  
  // 情况A：是数字（OpenSearch 时间戳）
  if (typeof time === 'number') {
    const date = new Date(time);
    const Y = date.getFullYear() + '-';
    const M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    const D = (date.getDate() < 10 ? '0'+date.getDate() : date.getDate()) + ' ';
    const h = (date.getHours() < 10 ? '0'+date.getHours() : date.getHours()) + ':';
    const m = (date.getMinutes() < 10 ? '0'+date.getMinutes() : date.getMinutes()) + ':';
    const s = (date.getSeconds() < 10 ? '0'+date.getSeconds() : date.getSeconds());
    return Y+M+D+h+m+s;
  }
  
  // 情况B：是字符串（MySQL 格式）
  if (typeof time === 'string') {
    return time.replace("T", " ");
  }
  
  return time;
}

onMounted(() => {
  fetchSquareData()
})
</script>

<style scoped>
.square-container { padding: 20px; max-width: 1000px; margin: 0 auto; }
.note-item { padding: 20px; border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: all 0.3s; }
.note-item:hover { background-color: #fafafa; transform: translateY(-2px); }
.note-title { margin: 0 0 10px 0; font-size: 18px; color: #333; font-weight: bold; }
.note-summary { color: #666; font-size: 14px; margin-bottom: 12px; line-height: 1.6; }
.note-meta { display: flex; justify-content: space-between; align-items: center; color: #999; font-size: 13px; }
.author-box { display: flex; align-items: center; gap: 8px; }
.author-name { color: #555; font-weight: 500; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-left { display: flex; align-items: center; gap: 20px; }
.search-input { width: 300px; }
</style>