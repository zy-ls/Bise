import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import LayoutContainer from '../views/layout/LayoutContainer.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/',
      component: LayoutContainer,
      redirect: '/note', // 访问主页时，自动去“我的笔记”
      children: [
        {
          path: '/note',
          component: () => import('../views/note/NoteList.vue')
        },
        {
          path: '/note/edit',
          component: () => import('../views/note/NoteEdit.vue')
        },
                {
          path:'/note/detail',
          component:()=>import('../views/note/NoteDetail.vue')
        },
        {
          path: '/note/square',
          component: () => import('../views/note/NoteSquare.vue')
        },
        {
          path: '/note/collect',
          component: () => import('../views/note/MyCollect.vue')
        },
        {
          path: '/resource',
          component: () => import('../views/resource/ResourceList.vue')
        },
        {
          path: '/chat',
          component: () => import('../views/chat/ChatView.vue')
        },
        {
          path: '/user/profile',
          component: () => import('../views/user/UserProfile.vue')
        },
        {
          path: '/todo',
          name: 'todo',
          component: () => import('../views/todo/TodoView.vue')
        }
      ]
    }
  ]
})

export default router