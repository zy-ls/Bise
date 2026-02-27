import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 定义状态：用户信息
  const user = ref({})

  // 定义动作：设置用户信息
  const setUser = (newUser) => {
    user.value = newUser
  }

  // 定义动作：清除用户信息 (退出登录用)
  const removeUser = () => {
    user.value = {}
  }

  return { user, setUser, removeUser }
}, {
  persist: true // 开启持久化 (刷新页面数据不丢失，需要安装插件，下面会讲)
})