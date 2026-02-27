<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="el-aside__logo"></div>
      <el-menu
        active-text-color="#ffd04b"
        background-color="#232323"
        text-color="#fff"
        router
        :default-active="$route.path"
      >
        <el-menu-item index="/note">
          <el-icon><Management /></el-icon>
          <span>我的笔记</span>
        </el-menu-item>

        <el-menu-item index="/note/collect">
          <el-icon><Star /></el-icon>
          <span>我的收藏</span>
        </el-menu-item>
        
        <el-menu-item index="/note/square">
          <el-icon><collection /></el-icon> <span>笔记广场</span>
        </el-menu-item>
        
        <el-menu-item index="/resource">
          <el-icon><Files /></el-icon>
          <span>资料广场</span>
        </el-menu-item>

        <el-menu-item index="/chat">
          <el-icon><ChatDotRound /></el-icon>
          <span>消息中心</span>
        </el-menu-item>


        <el-sub-menu index="/user">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>个人中心</span>
          </template>
          <el-menu-item index="/user/profile">基本资料</el-menu-item>
          <el-menu-item index="/user/password">重置密码</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div>当前用户：<strong>{{ userStore.user.nickname || userStore.user.username }}</strong></div>
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <el-avatar :src="userStore.user.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">基本资料</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>
      
      <el-footer>计算机学习交流平台 ©2025 Created by LiuTong</el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import { Management, Files, ChatDotRound, UserFilled, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确认退出登录吗?', '提示', {
      type: 'warning'
    }).then(() => {
      userStore.removeUser()
      router.push('/login')
      ElMessage.success('已退出登录')
    })
  } else if (command === 'profile') {
    router.push('/user/profile')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.el-aside {
  background-color: #232323;
}
.el-aside__logo {
  height: 60px;
  background: url("https://element-plus.org/images/element-plus-logo.svg") no-repeat center / 120px auto;
}
.el-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #dcdfe6;
}
.el-footer {
  text-align: center;
  color: #999;
  line-height: 60px;
  font-size: 12px;
}
</style>