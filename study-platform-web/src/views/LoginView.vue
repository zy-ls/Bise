<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>💻 计算机学习交流平台</h2>
        </div>
      </template>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0px">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入账号 (admin)" 
            prefix-icon="User" 
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            placeholder="请输入密码 (123456)" 
            prefix-icon="Lock" 
            show-password 
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin" size="large">
            立即登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 登录逻辑
const handleLogin = () => {
  // 1. 校验表单
  loginFormRef.value.validate((valid) => {
    if (!valid) return
    
    loading.value = true
    
    // 2. 发送请求给后端 (注意端口是 8080)
    axios.post('http://localhost:8080/sys-user/login', loginForm)
      .then(res => {
        const result = res.data
        if (result.code === 200) {
          ElMessage.success('登录成功！')
          console.log('用户信息:', result.data)
          userStore.setUser(result.data)
          
          // TODO: 这里以后要存 Token 和用户信息到 Pinia
          // localStorage.setItem('user', JSON.stringify(result.data))
          
          // 3. 跳转到首页 (后面我们会创建 HomeView)
          router.push('/')
        } else {
          ElMessage.error(result.msg || '登录失败')
        }
      })
      .catch(err => {
        console.error(err)
        ElMessage.error('无法连接到服务器')
      })
      .finally(() => {
        loading.value = false
      })
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%);
}

.login-card {
  width: 400px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.card-header {
  text-align: center;
}

.login-btn {
  width: 100%;
}
</style>