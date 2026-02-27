<template>
  <el-card class="note-list-card">
    <template #header>
      <div class="card-header">
        <span>📒 我的笔记列表</span>
        <el-button type="primary" icon="Plus" @click="handleCreate">新建笔记</el-button>
      </div>
    </template>

    <el-table :data="tableData" style="width: 100%" v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="180" />
      
      <el-table-column prop="categoryName" label="分类" width="120">
        <template #default="scope">
          <el-tag size="small" type="info">{{ scope.row.categoryId || '默认' }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">私有</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="warning">审核中</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="success">已发布</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="danger">已驳回</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="createTime" label="创建时间" width="180" />

      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button link type="success" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

// 1. 新加：引入 useRouter
import { useRouter } from 'vue-router' 

const userStore = useUserStore()

// 2. 新加：初始化 router
const router = useRouter() 

const tableData = ref([])
const loading = ref(false)

// 获取笔记列表
const fetchNotes = () => {
  loading.value = true
  const userId = userStore.user.userId 

  axios.get(`http://localhost:8080/note-info/my-list?userId=${userId}`)
    .then(res => {
      if (res.data.code === 200) {
        tableData.value = res.data.data
      } else {
        ElMessage.error('获取列表失败')
      }
    })
    .finally(() => {
      loading.value = false
    })
}

// 点击新建
const handleCreate = () => {
  // 跳转到编辑页
  router.push('/note/edit') 
}

// 点击编辑
const handleEdit = (row) => {
  // 跳转到编辑页，并通过 URL 参数传递 id
  // 例如：/note/edit?id=5
  router.push(`/note/edit?id=${row.noteId}`)
}

// 点击删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这篇笔记吗？', '提示', { type: 'warning' })
    .then(() => {
      // 注意：你的后端实体类叫 Info，ID 字段名可能是 id 或者 noteId
      // 请检查数据库，如果是 note_id，MyBatisPlus 生成的属性名通常是 noteId
      axios.delete(`http://localhost:8080/note-info/delete/${row.noteId}`)
        .then(res => {
          if (res.data.code === 200) {
            ElMessage.success('删除成功')
            fetchNotes() // 刷新列表
          }
        })
    })
}

const handleView = (row) => {
  router.push(`/note/detail?id=${row.noteId}`)
}

onMounted(() => {
  fetchNotes()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>