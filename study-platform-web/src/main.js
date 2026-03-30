import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 引入 Quill 编辑器
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

import App from './App.vue'
import router from './router'
import axios from 'axios'

// 🛡️ 给所有 Axios 请求统一加上身份头
// 🛡️ 给所有 Axios 请求统一加上身份头
axios.interceptors.request.use(config => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const parsedData = JSON.parse(userStr)
    // 💡 终极修复：提取真实的 role，塞给后端
    const currentRole = parsedData.user?.role || parsedData.role
    
    if (currentRole) {
      config.headers['X-User-Role'] = currentRole 
    }
  }
  return config
})

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 注册 Quill 组件
app.component('QuillEditor', QuillEditor)

app.mount('#app')