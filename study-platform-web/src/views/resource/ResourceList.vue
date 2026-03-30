<template>
  <div class="literature-container">
    
    <div class="banner-section" v-if="bannerList.length > 0">
      <el-carousel :interval="4000" type="card" height="300px" trigger="click">
        <el-carousel-item v-for="item in bannerList" :key="item.id">
          <div class="carousel-content" @click="goTarget(item.targetUrl)">
            <img :src="item.imgUrl" class="carousel-img" />
            <div class="carousel-title" v-if="item.title">{{ item.title }}</div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="square-header">
      <h1 class="page-title">
        <span class="prompt">>_</span> Literature & Resources 
        <span class="blink">_</span>
      </h1>
      <div class="search-box">
        <el-input 
          v-model="searchKeyword" 
          placeholder="Enter keyword to search resources..." 
          class="geek-search-input"
          @keyup.enter="searchResources"
        >
          <template #append>
            <el-button @click="searchResources"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div class="resource-list-area">
      <el-empty v-if="resourceList.length === 0" description="System Info: 官方资源库暂无数据" />
      <div class="resource-grid" v-else>
        <div class="resource-card" v-for="res in resourceList" :key="res.id">
           <h3>{{ res.title }}</h3>
           <p class="desc">{{ res.description }}</p>
           
           <div class="card-footer">
             <span class="date-text">Posted: {{ formatDate(res.createTime) }}</span>
             <el-button type="primary" size="small" plain @click="goTarget(res.targetUrl)">
               阅读文献
             </el-button>
           </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()

// ================= 1. 轮播图加载逻辑 =================
const bannerList = ref([])
const loadActiveBanners = async () => {
  const res = await axios.get('http://localhost:8080/banner/list?status=1')
  if (res.data.code === 200) bannerList.value = res.data.data
}

// ================= 2. 官方文献加载逻辑 =================
const searchKeyword = ref('')
const resourceList = ref([])

const loadActiveLiteratures = async () => {
  // 只请求 status = 1 (上架) 的文献
  const res = await axios.get('http://localhost:8080/literature/list?status=1')
  if (res.data.code === 200) {
    resourceList.value = res.data.data
  }
}

// 暂未接入搜索接口，仅前端过滤演示
const searchResources = () => {
  if (!searchKeyword.value) {
    loadActiveLiteratures()
  } else {
    resourceList.value = resourceList.value.filter(item => 
      item.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) || 
      item.description.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
}

// --- 通用辅助函数 ---
const goTarget = (url) => {
  if (!url) return
  if (url.startsWith('http://') || url.startsWith('https://')) {
    window.open(url, '_blank')
  } else {
    router.push(url)
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 10)
}

onMounted(() => {
  loadActiveBanners()
  loadActiveLiteratures() // 💡 页面加载时自动获取真实文献
})
</script>

<style scoped>
.literature-container { padding: 20px; max-width: 1200px; margin: 0 auto; }
.banner-section { margin-bottom: 40px; margin-top: 10px; }
.carousel-content { position: relative; width: 100%; height: 100%; cursor: pointer; border-radius: 12px; overflow: hidden; box-shadow: 0 10px 20px rgba(0,0,0,0.3); }
.carousel-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s ease; }
.carousel-content:hover .carousel-img { transform: scale(1.05); }
.carousel-title { position: absolute; bottom: 0; left: 0; right: 0; padding: 15px 20px; background: linear-gradient(transparent, rgba(15, 23, 42, 0.95)); color: #f8fafc; font-family: monospace; font-weight: bold; font-size: 16px; letter-spacing: 1px; }
:deep(.el-carousel__indicator.is-active button) { background-color: #3b82f6; width: 30px; }

.square-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; border-bottom: 1px dashed #334155; padding-bottom: 20px; }
.page-title { color: #e2e8f0; font-family: monospace; font-size: 24px; margin: 0; }
.prompt { color: #10b981; }
.blink { animation: blinker 1s linear infinite; color: #3b82f6; }
@keyframes blinker { 50% { opacity: 0; } }

.search-box { width: 350px; }
:deep(.geek-search-input .el-input__wrapper) { background-color: #1e293b; box-shadow: 0 0 0 1px #334155 inset; }
:deep(.geek-search-input .el-input__inner) { color: #e2e8f0; font-family: monospace; }
:deep(.geek-search-input .el-input-group__append) { background-color: #3b82f6; color: white; border: none; }

.resource-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 24px; }
.resource-card {
  background-color: #1e293b; border: 1px solid #334155; border-radius: 8px; padding: 24px; color: #94a3b8;
  display: flex; flex-direction: column; justify-content: space-between; min-height: 180px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.resource-card:hover { transform: translateY(-3px); box-shadow: 0 8px 16px rgba(0,0,0,0.2); border-color: #3b82f6; }
.resource-card h3 { color: #f8fafc; margin-top: 0; font-size: 18px; margin-bottom: 12px; }
.desc { flex-grow: 1; font-size: 14px; line-height: 1.6; margin-bottom: 20px; }
.card-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px dashed #334155; padding-top: 16px; }
.date-text { font-size: 12px; color: #64748b; font-family: monospace; }
</style>