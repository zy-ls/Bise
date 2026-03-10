<template>
  <div class="editor-container">
    <el-card>
      <template #header>
        <div class="header-actions">
          <el-input 
            v-model="form.title" 
            placeholder="请输入笔记标题..." 
            size="large" 
            class="title-input"
          />
          
          <el-select 
            v-model="form.categoryId" 
            placeholder="选择探索频道(分类)" 
            size="large"
            class="category-select"
          >
            <el-option 
              v-for="cat in categoryList" 
              :key="cat.categoryId" 
              :label="cat.name" 
              :value="cat.categoryId" 
            />
          </el-select>

          <el-input 
            v-model="form.tags" 
            placeholder="技术标签 (如: Vue3, 架构)" 
            size="large" 
            class="tags-input"
          >
            <template #prefix>
              <span style="font-family: monospace; font-weight: bold; color: #3b82f6;">#</span>
            </template>
          </el-input>

          <el-button type="primary" size="large" @click="handleSave" class="publish-btn">
            {{ form.noteId ? '保存修改' : 'EXECUTE (发布)' }}
          </el-button>
        </div>
      </template>

      <div class="editor-wrapper" style="border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden;">
        <QuillEditor 
          v-if="isReady"
          theme="snow" 
          v-model:content="form.contentHtml" 
          contentType="html" 
          :toolbar="toolbarOptions" 
          :modules="editorModules"
          style="height: 500px; font-size: 16px;" 
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import BlotFormatter from 'quill-blot-formatter'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isReady = ref(false)

// 用于存储后台获取的分类列表
const categoryList = ref([])

const toolbarOptions = [
  ['bold', 'italic', 'underline', 'strike'],
  ['blockquote', 'code-block'],
  [{ 'header': 1 }, { 'header': 2 }],
  [{ 'list': 'ordered'}, { 'list': 'bullet' }],
  [{ 'script': 'sub'}, { 'script': 'super' }],
  [{ 'indent': '-1'}, { 'indent': '+1' }],
  [{ 'direction': 'rtl' }],
  [{ 'size': ['small', false, 'large', 'huge'] }],
  [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
  [{ 'color': [] }, { 'background': [] }],
  [{ 'font': [] }],
  [{ 'align': [] }],
  ['clean'],
  ['image']
]

const editorModules = [
  {
    name: 'blotFormatter',
    module: BlotFormatter,
    options: {}
  }
]

const form = reactive({
  noteId: null,
  title: '',
  contentHtml: '',
  categoryId: null, // 新增分类ID绑定
  tags: ''          // 新增标签绑定
})

// 加载分类列表 (复用我们在广场写过的接口)
const loadCategories = () => {
  axios.get('http://localhost:8080/sys-category/list').then(res => {
    if (res.data.code === 200) {
      categoryList.value = res.data.data
    }
  })
}

onMounted(() => {
  loadCategories() // 页面初始化时加载分类

  setTimeout(() => {
    isReady.value = true
  }, 100)

  const id = route.query.id
  if (id) {
    loadDetail(id)
  }
})

const loadDetail = (id) => {
  axios.get(`http://localhost:8080/note-info/detail/${id}`)
    .then(res => {
      if (res.data.code === 200) {
        const data = res.data.data
        form.noteId = data.noteId
        form.title = data.title
        form.contentHtml = data.contentHtml
        form.categoryId = data.categoryId // 详情中回显分类
        form.tags = data.tags             // 详情中回显标签
      } else {
        ElMessage.error('加载笔记失败')
      }
    })
}

const handleSave = () => {
  if (!form.title) return ElMessage.warning('Error: 标题不能为空')
  if (!form.categoryId) return ElMessage.warning('Error: 请选择一个探索频道(分类)')
  
  if (!form.contentHtml || form.contentHtml === '<p><br></p>') {
      return ElMessage.warning('Error: 正文内容不能为空')
  }

  const noteData = {
    noteId: form.noteId,
    userId: userStore.user.userId,
    title: form.title,
    contentHtml: form.contentHtml,
    categoryId: form.categoryId, // 提交分类
    tags: form.tags,             // 提交标签
    status: 2  // 🛑 核心修复：状态必须是 2 (已发布)，这样才能同步进 OpenSearch 和笔记广场
  }

  axios.post('http://localhost:8080/note-info/save', noteData)
    .then(res => {
      if (res.data.code === 200) {
        ElMessage.success(form.noteId ? '笔记修改成功' : '笔记已成功发布到广场')
        router.push('/note/square') // 发布成功后直接跳去极客风的广场看看效果
      } else {
        ElMessage.error(res.data.msg)
      }
    })
}
</script>

<style scoped>
.editor-container {
  max-width: 1300px;
  margin: 0 auto;
  animation: fadeIn 0.4s ease-out;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px; /* 使用 gap 让各个输入框之间保持完美间距 */
  flex-wrap: wrap;
}

.title-input {
  flex: 3;
  min-width: 300px;
}

.category-select {
  flex: 1;
  min-width: 180px;
}

.tags-input {
  flex: 1.5;
  min-width: 200px;
}

.publish-btn {
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  padding: 0 24px;
}

.editor-wrapper {
  margin-top: 10px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0,0,0,0.02);
}

/* 覆盖 Quill 编辑器默认边框 */
:deep(.ql-toolbar.ql-snow) {
  border: none;
  border-bottom: 1px solid #e2e8f0;
  background-color: #f8fafc;
}
:deep(.ql-container.ql-snow) {
  border: none;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>