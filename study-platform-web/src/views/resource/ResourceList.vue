<template>
  <div class="resource-container">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>📂 资料广场</span>
          <el-button type="primary" icon="Upload" @click="dialogVisible = true">上传资料</el-button>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="fileName" label="文件名称" min-width="200">
          <template #default="scope">
            <el-icon><Document /></el-icon>
            <span style="margin-left: 8px">{{ scope.row.fileName }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="scope">
            {{ formatSize(scope.row.fileSize) }}
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="上传时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="downloadCount" label="下载热度" width="100" align="center" />

        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" link icon="Download" @click="handleDownload(scope.row)">
              下载
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="上传新资料" width="400px">
      <el-upload
        class="upload-demo"
        drag
        :action="uploadUrl"
        :data="{ userId: userStore.user.userId }" 
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :show-file-list="false"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到这里 或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 PDF, Word, PPT 等格式，文件大小不超过 50MB
          </div>
        </template>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Document, UploadFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()
const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)

// 上传接口地址
const uploadUrl = 'http://localhost:8080/resource/upload'

// 获取列表
const fetchList = () => {
  loading.value = true
  axios.get('http://localhost:8080/resource/list')
    .then(res => {
      if (res.data.code === 200) {
        tableData.value = res.data.data
      }
    })
    .finally(() => {
      loading.value = false
    })
}

// 下载文件
const handleDownload = (row) => {
  // 直接通过浏览器打开下载链接
  const url = `http://localhost:8080/resource/download/${row.fileUrl}`
  window.open(url, '_blank')
}

// 上传成功回调
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('上传成功！')
    dialogVisible.value = false // 关闭弹窗
    fetchList() // 刷新列表
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// 上传失败回调
const handleUploadError = () => {
  ElMessage.error('上传出错，请检查网络或文件大小')
}

// 工具函数：格式化文件大小 (Bytes -> KB/MB)
const formatSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 工具函数：简单格式化时间 (去掉 T 字符)
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>