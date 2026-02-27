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
          <el-button type="primary" size="large" @click="handleSave">
            {{ form.noteId ? '保存修改' : '发布笔记' }}
          </el-button>
        </div>
      </template>

      <div class="editor-wrapper" style="border: 1px solid #ccc;">
        <QuillEditor 
          v-if="isReady"
          theme="snow" 
          v-model:content="form.contentHtml" 
          contentType="html" 
          :toolbar="toolbarOptions" 
          style="height: 450px;" 
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

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isReady = ref(false)

// 🛠️ 定义工具栏配置：越全越好
const toolbarOptions = [
  ['bold', 'italic', 'underline', 'strike'],        // 加粗、斜体、下划线、删除线
  ['blockquote', 'code-block'],                     // 引用、代码块
  [{ 'header': 1 }, { 'header': 2 }],               // 标题 H1, H2
  [{ 'list': 'ordered'}, { 'list': 'bullet' }],     // 列表
  [{ 'script': 'sub'}, { 'script': 'super' }],      // 上标/下标
  [{ 'indent': '-1'}, { 'indent': '+1' }],          // 缩进
  [{ 'direction': 'rtl' }],                         // 文本方向
  [{ 'size': ['small', false, 'large', 'huge'] }],  // 字体大小
  [{ 'header': [1, 2, 3, 4, 5, 6, false] }],        // 标题下拉选择
  [{ 'color': [] }, { 'background': [] }],          // 字体颜色、背景色
  [{ 'font': [] }],                                 // 字体
  [{ 'align': [] }],                                // 对齐方式
  ['clean'],                                        // 清除格式
  ['image']                                         // 图片上传 (默认是转Base64)
]

const form = reactive({
  noteId: null,
  title: '',
  contentHtml: '' 
})

onMounted(() => {
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
      } else {
        ElMessage.error('加载笔记失败')
      }
    })
}

const handleSave = () => {
  if (!form.title) return ElMessage.warning('标题不能为空')
  
  if (!form.contentHtml || form.contentHtml === '<p><br></p>') {
      return ElMessage.warning('内容不能为空')
  }

  const noteData = {
    noteId: form.noteId,
    userId: userStore.user.userId,
    title: form.title,
    contentHtml: form.contentHtml,
    status: 1 
  }

  axios.post('http://localhost:8080/note-info/save', noteData)
    .then(res => {
      if (res.data.code === 200) {
        ElMessage.success(form.noteId ? '修改成功' : '发布成功')
        router.push('/note')
      } else {
        ElMessage.error(res.data.msg)
      }
    })
}
</script>

<style scoped>
.title-input {
  width: 600px;
  margin-right: 20px;
}
.header-actions {
  display: flex;
  align-items: center;
}
.editor-wrapper {
  margin-top: 10px;
  background-color: #fff; /* 防止背景透明 */
}
</style>