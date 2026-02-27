<template>
  <div class="lit-container">
    <div class="header">
      <h2>📚 文献资料库</h2>
      <el-button type="primary" @click="dialogVisible = true">上传新文献</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="6" v-for="item in list" :key="item.id">
        <el-card class="lit-card" shadow="hover" @click="goRead(item.id)">
          <div class="icon-wrapper">
            <el-icon :size="50" color="#F56C6C"><Document /></el-icon> </div>
          <h4>{{ item.title }}</h4>
          <p class="author">作者: {{ item.author || '佚名' }}</p>
          <p class="size">{{ item.fileSize || '未知大小' }}</p>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="上传文献" width="500px">
      <el-form :model="form">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入文献标题" />
        </el-form-item>
        <el-form-item label="原作者">
          <el-input v-model="form.author" placeholder="文献的撰写人" />
        </el-form-item>
        
        <el-form-item label="文件">
          <el-upload
            action="http://localhost:8080/file/upload"
            :limit="1"
            :on-success="handleSuccess"
          >
            <el-button type="primary">选择PDF文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="submitLiterature" type="primary">确认发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Document } from '@element-plus/icons-vue' // 记得引入图标
import { ElMessage } from 'element-plus'

const list = ref([])
const dialogVisible = ref(false)
const form = ref({ title: '', author: '', filePath: '' })
const router = useRouter()

// 1. 加载列表
const loadData = () => {
  axios.get('http://localhost:8080/literature/list').then(res => {
    list.value = res.data.data
  })
}

// 2. 上传成功回调
const handleSuccess = (res) => {
  if(res.code === 200) {
    form.value.filePath = res.data // 拿到后端返回的 D:/xxx/xxx.pdf
    ElMessage.success('文件上传就绪')
  }
}

// 3. 提交表单到文献库
const submitLiterature = () => {
  axios.post('http://localhost:8080/literature/save', form.value).then(res => {
    if(res.data.code === 200) {
      ElMessage.success('发布成功')
      dialogVisible.value = false
      loadData() // 刷新列表
    }
  })
}

// 4. 去阅读页
const goRead = (id) => {
  router.push(`/literature/read?id=${id}`)
}

onMounted(() => loadData())
</script>

<style scoped>
.lit-container { padding: 20px; }
.header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.lit-card { cursor: pointer; text-align: center; margin-bottom: 20px;}
.author { color: #999; font-size: 12px; }
</style>