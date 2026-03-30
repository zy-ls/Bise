import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import LayoutContainer from '../views/layout/LayoutContainer.vue'
// 💡 B端后台控制台
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    // 💡 独立的全屏后台路由
    {
      path: '/admin',
      name: 'admin',
      component: AdminDashboard
    },
    {
      path: '/',
      component: LayoutContainer,
      redirect: '/note', // 访问主页时，自动去“我的笔记”
      children: [
        // --- 笔记体系 ---
        {
          path: '/note',
          component: () => import('../views/note/NoteList.vue')
        },
        {
          path: '/note/edit',
          component: () => import('../views/note/NoteEdit.vue')
        },
        {
          path: '/note/detail',
          component: () => import('../views/note/NoteDetail.vue')
        },
        {
          path: '/note/square',
          component: () => import('../views/note/NoteSquare.vue')
        },
        {
          path: '/note/collect',
          component: () => import('../views/note/MyCollect.vue')
        },
        // --- 资源与文献体系 ---
        {
          path: '/resource',
          component: () => import('../views/resource/ResourceList.vue')
        },
        {
          path: '/literature/square',
          component: () => import('../views/literature/LiteratureSquare.vue')
        },
        {
          path: '/literature/read',
          component: () => import('../views/literature/LiteratureRead.vue')
        },
        // --- 圈子与社交体系 ---
        {
          path: '/chat',
          component: () => import('../views/chat/ChatView.vue')
        },
        {
          path: '/group/square',
          name: 'GroupSquare',
          component: () => import('../views/group/GroupSquare.vue')
        },
        {
          path: '/group/detail',
          name: 'GroupDetail',
          component: () => import('../views/group/GroupDetail.vue')
        },
        // --- 个人与待办体系 ---
        {
          path: '/todo',
          name: 'todo',
          component: () => import('../views/todo/TodoView.vue')
        },
        {
          path: '/user/profile',
          component: () => import('../views/user/UserProfile.vue')
        }
      ]
    }
  ]
})



// 🛡️ 核心新增：全局路由守卫 (前端安全防线)
router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  let currentRole = null

  if (userStr) {
    const parsedData = JSON.parse(userStr)
    // 💡 终极修复：自动兼容 Pinia 的多层嵌套，提取出真实的 role
    currentRole = parsedData.user?.role || parsedData.role
  }

  // 如果目标页面是管理员控制台
  if (to.path === '/admin') {
    if (currentRole === 'ADMIN') {
      next() // 身份核实完毕，放行！
    } else {
      import('element-plus').then(({ ElMessage }) => {
        ElMessage.error(`Access Denied: 权限不足，系统拦截！(检测到角色: ${currentRole || '未登录'})`)
      })
      next('/note') // 踢回大厅
    }
  } else {
    next()
  }
})

export default router