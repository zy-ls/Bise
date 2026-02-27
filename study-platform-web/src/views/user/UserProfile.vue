<template>
  <el-card class="profile-card">
    <template #header>
      <div class="card-header">
        <span>👤 个人资料</span>
      </div>
    </template>

    <el-row :gutter="40">
      <el-col :span="8" class="avatar-col">
        <el-upload
          class="avatar-uploader"
          action="http://localhost:8080/resource/upload"
          :data="{ userId: userStore.user.userId }"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          <div class="upload-text">点击修改头像</div>
        </el-upload>
      </el-col>

      <el-col :span="16">
        <el-form :model="userForm" label-width="80px" size="large">
          <el-form-item label="账号">
            <el-input v-model="userForm.username" disabled />
          </el-form-item>
          
          <el-form-item label="昵称">
            <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          
          <el-form-item label="邮箱">
            <el-input v-model="userForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          
          <el-form-item label="手机号">
            <el-input v-model="userForm.mobile" placeholder="请输入手机号" />
          </el-form-item>

          <el-form-item label="性别">
            <el-radio-group v-model="userForm.sex">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="0">女</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleUpdate">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()

const userForm = reactive({
  userId: null,
  username: '',
  nickname: '',
  email: '',
  mobile: '',
  sex: 1,
  avatar: ''
})

// 初始化：从 Pinia 或 后端获取最新数据
const initData = () => {
  // 也可以直接用 userStore.user，但为了数据实时性，建议查一次后端
  const userId = userStore.user.userId
  axios.get(`http://localhost:8080/sys-user/info/${userId}`)
    .then(res => {
      if (res.data.code === 200) {
        Object.assign(userForm, res.data.data) // 将后端数据填入表单
      }
    })
}

// 头像上传成功回调
const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    // 注意：这里我们复用了资料上传接口，它返回的是 fileUrl (UUID)
    // 我们需要把它拼成完整的图片地址
    // 如果你的 ResourceController 直接返回了完整的 http 地址最好
    // 这里假设返回的是 UUID，我们拼接一下下载地址作为图片源
    const fileUuid = response.data.fileUrl
    userForm.avatar = `http://localhost:8080/resource/download/${fileUuid}`
    ElMessage.success('头像上传成功，别忘了点保存哦')
  } else {
    ElMessage.error('上传失败')
  }
}

// 上传前校验
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像必须是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

// 保存修改
const handleUpdate = () => {
  axios.post('http://localhost:8080/sys-user/update', userForm)
    .then(res => {
      if (res.data.code === 200) {
        ElMessage.success('修改成功')
        // 更新 Pinia 里的缓存，这样右上角的头像和名字会立刻变
        userStore.setUser(res.data.data)
      } else {
        ElMessage.error(res.data.msg || '修改失败')
      }
    })
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.profile-card {
  max-width: 800px;
  margin: 20px auto;
}
.avatar-col {
  text-align: center;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px; 
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}
.upload-text {
  margin-top: 10px;
  color: #666;
  font-size: 12px;
}
</style>