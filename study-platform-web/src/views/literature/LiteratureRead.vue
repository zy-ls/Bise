<template>
  <div class="read-container">
    <div class="top-bar">
      <el-button @click="router.back()">返回列表</el-button>
      <h3>{{ info.title }}</h3>
    </div>
    
    <div class="pdf-box" v-if="previewUrl">
      <vue-pdf-app :pdf="previewUrl" theme="light"></vue-pdf-app>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import VuePdfApp from "vue3-pdf-app"
import "vue3-pdf-app/dist/icons/main.css";

const route = useRoute()
const router = useRouter()
const info = ref({})
const previewUrl = ref('')

onMounted(() => {
  const id = route.query.id
  // 1. 获取文献详情（拿到 filePath）
  axios.get(`http://localhost:8080/literature/${id}`).then(res => {
    info.value = res.data.data
    // 2. 构造流预览地址 (复用之前的 FileController)
    // 注意：这里我们传的是物理路径，后端通过 path 参数读取
    if(info.value.filePath) {
      previewUrl.value = `http://localhost:8080/file/preview?path=${info.value.filePath}`
    }
  })
})
</script>

<style scoped>
.read-container { height: 100vh; display: flex; flex-direction: column; }
.top-bar { padding: 10px 20px; background: #fff; border-bottom: 1px solid #ddd; display: flex; align-items: center; gap: 20px;}
.pdf-box { flex: 1; overflow: hidden; }
</style>