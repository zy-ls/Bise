<template>
  <div class="square-container">
    
    <aside class="category-sidebar">
      <div class="sidebar-header">
        <el-icon><Monitor /></el-icon>
        <span>探索频道</span>
      </div>
      <ul class="category-list">
        <li 
          class="category-item" 
          :class="{ active: activeCategoryId === null }"
          @click="handleCategoryChange(null)"
        >
          <span class="cmd-icon">~</span> <span class="cat-text">全部领域</span>
        </li>
        <li 
          v-for="cat in categoryList" 
          :key="cat.categoryId"
          class="category-item"
          :class="{ active: activeCategoryId === cat.categoryId }"
          @click="handleCategoryChange(cat.categoryId)"
        >
          <span class="cmd-icon">#</span> <span class="cat-text">{{ cat.name }}</span>
        </li>
      </ul>
    </aside>

    <main class="content-area">
      
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="输入关键字、技术栈或标签进行全库检索..."
          class="geek-input"
          @keyup.enter="loadNotes"
          clearable
          @clear="loadNotes"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <div class="search-btn" @click="loadNotes">
              Search (↵)
            </div>
          </template>
        </el-input>
      </div>

      <div class="note-grid" v-if="noteList.length > 0">
        <div 
          v-for="note in noteList" 
          :key="note.noteId || note.id" 
          class="note-card"
          @click="$router.push(`/note/detail?id=${note.noteId || note.id}`)"
        >
          <div class="card-header">
            <h3 class="note-title">{{ note.title }}</h3>
          </div>
          <div class="card-body">
            <p class="note-summary">
              {{ note.summary || (note.content ? note.content.substring(0, 110) + '...' : '暂无摘要内容...') }}
            </p>
          </div>
          
          <div class="card-tags" v-if="note.tags">
            <span class="geek-tag" v-for="(tag, index) in note.tags.split(',')" :key="index">
              {{ tag.trim() }}
            </span>
          </div>

          <div class="card-footer">
            <div class="author-info">
              <el-avatar :size="22" :src="note.authorAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              <span class="author-name">{{ note.authorName || '匿名极客' }}</span>
            </div>
            <span class="date">{{ formatDate(note.createTime) }}</span>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-state">
        <el-empty description="[ 404 Not Found ] : 当前频道暂无匹配数据" />
      </div>
      
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { Monitor, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const categoryList = ref([])      
const noteList = ref([])          
const activeCategoryId = ref(null)
const keyword = ref('')           

const loadCategories = async () => {
  try {
    const res = await axios.get('http://localhost:8080/sys-category/list')
    if (res.data.code === 200) {
      categoryList.value = res.data.data
    }
  } catch (error) {
    ElMessage.error('探索频道加载异常')
  }
}

// 💡 修复查询逻辑：智能路由 (Smart Routing)
const loadNotes = async () => {
  try {
    let url = ''
    
    // 场景 A：如果用户输入了搜索词，强制调用 OpenSearch 高级检索
    if (keyword.value && keyword.value.trim() !== '') {
      url = `http://localhost:8080/search/note?keyword=${keyword.value}`
      if (activeCategoryId.value !== null) {
        url += `&categoryId=${activeCategoryId.value}`
      }
    } 
    // 场景 B：如果只是日常浏览(无搜索词)，直接查 MySQL 获取最全的实时列表
    else {
      url = 'http://localhost:8080/note-info/list'
      if (activeCategoryId.value !== null) {
        url += `?categoryId=${activeCategoryId.value}` // 注意这里是问号
      }
    }

    const res = await axios.get(url)
    if (res.data.code === 200) {
      noteList.value = res.data.data
    }
  } catch (error) {
    ElMessage.error('数据检索失败，请检查网络链路')
  }
}

const handleCategoryChange = (id) => {
  activeCategoryId.value = id
  loadNotes()
}

const formatDate = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  loadCategories()
  loadNotes()
})
</script>

<style scoped>
/* -------- 样式部分保持你之前觉得好看的极客风不变 -------- */
.square-container {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  padding: 10px 0;
  animation: fadeIn 0.4s ease-out;
}

.category-sidebar {
  width: 220px;
  background: #fff;
  border-radius: 12px;
  padding: 20px 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  position: sticky;
  top: 80px; 
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #1e293b;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #e2e8f0;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  color: #64748b;
  font-size: 14px;
  transition: all 0.3s ease;
}

.cmd-icon {
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  color: #94a3b8;
  margin-right: 10px;
  font-size: 16px;
}

.category-item:hover {
  background-color: #f8fafc;
  color: #3b82f6;
  transform: translateX(4px);
}

.category-item.active {
  background-color: #eff6ff;
  color: #2563eb;
  font-weight: bold;
}

.category-item.active .cmd-icon {
  color: #3b82f6;
}

.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-bar {
  background: #fff;
  padding: 6px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

:deep(.geek-input .el-input__wrapper) {
  box-shadow: none !important;
  background: transparent;
}

:deep(.geek-input .el-input-group__append) {
  background: transparent;
  border: none;
  padding: 0;
}

.search-btn {
  background-color: #2563eb;
  color: #fff;
  padding: 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  border-radius: 8px;
  cursor: pointer;
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  transition: background 0.3s;
}

.search-btn:hover {
  background-color: #1d4ed8;
}

.note-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.note-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.note-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.08);
  border-color: #bfdbfe;
}

.card-header .note-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #1e293b;
  font-weight: bold;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-body .note-summary {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px 0;
  flex: 1; 
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.geek-tag {
  background: #f1f5f9;
  color: #475569;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
}

.geek-tag::before { content: '<'; color: #94a3b8; }
.geek-tag::after { content: '/>'; color: #94a3b8; }

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f1f5f9;
  padding-top: 12px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-size: 13px;
  color: #475569;
  font-weight: 500;
}

.date {
  font-size: 12px;
  color: #94a3b8;
  font-family: 'Courier New', Courier, monospace;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 40px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}
</style>