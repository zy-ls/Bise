<template>
  <div class="admin-layout fade-in">
    <aside class="admin-sidebar">
      <div class="brand">
        <span class="prompt">root@system:~#</span>
        <span class="title">ADMIN_PANEL</span>
      </div>
      <nav class="sidebar-nav">
        <div class="nav-item" :class="{active: activeMenu === 'dashboard'}" @click="activeMenu = 'dashboard'">
          <el-icon><DataLine /></el-icon> <span>系统流量监控</span>
        </div>
        <div class="nav-spacer-line"></div>
        <div class="nav-group-title">RESOURCE (资源建设)</div>
        
        <div class="nav-item" :class="{active: activeMenu === 'resource_upload'}" @click="activeMenu = 'resource_upload'">
          <el-icon><Upload /></el-icon> <span>官方文献上传</span>
        </div>
        
        <div class="nav-item" :class="{active: activeMenu === 'banner_config'}" @click="activeMenu = 'banner_config'">
          <el-icon><Picture /></el-icon> <span>推荐位轮播图</span>
        </div>
        <div class="nav-group-title">SECURITY (风控治理)</div>
        <div class="nav-item" :class="{active: activeMenu === 'audit'}" @click="activeMenu = 'audit'">
          <el-icon><BellFilled /></el-icon> <span>违规举报审批</span>
          <span class="badge" v-if="stats.pendingAudits > 0">{{ stats.pendingAudits }}</span>
        </div>
        <div class="nav-item" :class="{active: activeMenu === 'config'}" @click="activeMenu = 'config'">
          <el-icon><Filter /></el-icon> <span>敏感词防火墙</span>
        </div>
        <div class="nav-spacer-line"></div>
        <div class="nav-group-title">OPS & SYSTEM (系统运维)</div>
        <div class="nav-item" :class="{active: activeMenu === 'user_manage'}" @click="activeMenu = 'user_manage'">
          <el-icon><User /></el-icon> <span>用户角色权限</span>
        </div>
        <div class="nav-item" :class="{active: activeMenu === 'announcement'}" @click="activeMenu = 'announcement'">
          <el-icon><Notification /></el-icon> <span>系统公告发布</span>
        </div>
        <div class="nav-spacer"></div>
        <div class="nav-item back-btn" @click="router.push('/note/square')">
          <el-icon><Back /></el-icon> <span>返回前台首页</span>
        </div>
      </nav>
    </aside>

    <main class="admin-main">
      <header class="admin-header">
        <h2>{{ menuTitles[activeMenu] }}</h2>
        <div class="admin-profile"><span class="pulse-dot"></span> System Online - {{ currentTime }}</div>
      </header>

      <div class="dashboard-content">
        <div v-if="activeMenu === 'dashboard'">
          <div class="stats-grid">
            <div class="stat-card"><div class="stat-icon blue"><el-icon><UserFilled /></el-icon></div><div class="stat-info"><div class="label">Total Nodes</div><div class="value">{{ stats.totalUsers }}</div></div></div>
            <div class="stat-card"><div class="stat-icon purple"><el-icon><Connection /></el-icon></div><div class="stat-info"><div class="label">Active Hubs</div><div class="value">{{ stats.totalGroups }}</div></div></div>
            <div class="stat-card"><div class="stat-icon green"><el-icon><Document /></el-icon></div><div class="stat-info"><div class="label">Public Data</div><div class="value">{{ stats.totalNotes }}</div></div></div>
            <div class="stat-card warning-card" :class="{'has-warning': stats.pendingAudits > 0}"><div class="stat-icon red"><el-icon><BellFilled /></el-icon></div><div class="stat-info"><div class="label">Pending Audits</div><div class="value">{{ stats.pendingAudits }}</div></div></div>
          </div>
          <div class="chart-container"><div class="chart-header"><h3><el-icon><TrendCharts /></el-icon> 7-Day Global Data Ingestion</h3></div><div ref="trendChartRef" class="trend-chart"></div></div>
        </div>

        <div v-else-if="activeMenu === 'resource_upload'" class="module-container fade-in">
          <div style="margin-bottom: 20px;">
            <el-button type="primary" class="geek-btn inject-btn" @click="litDialogVisible = true">
              <el-icon><Plus /></el-icon> UPLOAD LITERATURE (发布官方文献)
            </el-button>
          </div>
          <div class="geek-table-wrapper">
            <el-table :data="literatureList" class="geek-table">
              <el-table-column prop="title" label="Literature Title (文献标题)" width="250" />
              <el-table-column prop="description" label="Description (简介)" show-overflow-tooltip />
              <el-table-column prop="targetUrl" label="Link (直达链接)" width="200" show-overflow-tooltip />
              <el-table-column label="Status (广场展示状态)" width="150">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" active-color="#10b981" @change="saveLiterature(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="Action" width="120">
                <template #default="scope">
                  <el-button type="danger" size="small" class="action-kill" @click="deleteLiterature(scope.row.id)">REMOVE</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <div v-else-if="activeMenu === 'banner_config'" class="module-container fade-in">
          <div style="margin-bottom: 20px;"><el-button type="primary" class="geek-btn inject-btn" @click="bannerDialogVisible = true"><el-icon><Plus /></el-icon> DEPLOY NEW BANNER</el-button></div>
          <div class="geek-table-wrapper">
            <el-table :data="bannerList" class="geek-table">
              <el-table-column label="Preview" width="280"><template #default="scope"><el-image :src="scope.row.imgUrl" style="width: 240px; height: 120px; border-radius: 8px; object-fit: cover; border: 1px solid #334155;" /></template></el-table-column>
              <el-table-column prop="title" label="Title" width="200" />
              <el-table-column prop="targetUrl" label="Route" />
              <el-table-column label="Status" width="150"><template #default="scope"><el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-text="ON" inactive-text="OFF" active-color="#10b981" @change="saveBanner(scope.row)" /></template></el-table-column>
              <el-table-column label="Action" width="120"><template #default="scope"><el-button type="danger" size="small" class="action-kill" @click="deleteBanner(scope.row.id)">REMOVE</el-button></template></el-table-column>
            </el-table>
          </div>
        </div>

        <div v-else-if="activeMenu === 'audit'" class="module-container fade-in">
          <el-table :data="reportList" class="geek-table">
            <el-table-column prop="reportId" label="工单 ID" width="100" />
            <el-table-column prop="noteId" label="嫌疑笔记" width="120" />
            <el-table-column prop="reason" label="被举报原因" width="200" />
            <el-table-column label="状态" width="120">
              <template #default="scope">
                <span v-if="scope.row.status === 0" class="tag-pending">待处理</span>
                <span v-else-if="scope.row.status === 1" class="tag-banned">已封禁</span>
                <span v-else class="tag-rejected">已驳回</span>
              </template>
            </el-table-column>
            <el-table-column label="ACTIONS" min-width="200">
              <template #default="scope">
                <div v-if="scope.row.status === 0">
                  <el-button type="danger" size="small" class="action-kill" @click="handleReport(scope.row, 1)">KILL</el-button>
                  <el-button type="info" size="small" class="action-ignore" @click="handleReport(scope.row, 2)">IGNORE</el-button>
                </div>
                <span v-else class="done-text">Task Executed.</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else-if="activeMenu === 'config'" class="module-container fade-in">
          <div class="word-input-section">
            <input v-model="newWord" class="geek-input" placeholder="输入违规拦截词..." @keyup.enter="handleAddWord" />
            <el-button type="primary" class="geek-btn inject-btn" @click="handleAddWord">INJECT</el-button>
          </div>
          <div class="word-tags-pool">
            <el-tag v-for="word in sensitiveWords" :key="word.wordId" closable type="danger" effect="dark" @close="handleDeleteWord(word.wordId)" class="geek-tag">{{ word.word }}</el-tag>
          </div>
        </div>
        <div v-else-if="activeMenu === 'announcement'" class="module-container fade-in">
          <div style="margin-bottom: 20px;">
            <el-button type="primary" class="geek-btn inject-btn" @click="annDialogVisible = true">
              <el-icon><Notification /></el-icon> BROADCAST MESSAGE (发布新公告)
            </el-button>
          </div>
          <div class="geek-table-wrapper">
            <el-table :data="announcementList" class="geek-table">
              <el-table-column prop="title" label="Title (公告标题)" width="200" />
              <el-table-column prop="content" label="Content (正文内容)" show-overflow-tooltip />
              <el-table-column prop="createTime" label="Date (发布时间)" width="160" />
              <el-table-column label="Status (状态)" width="120">
                <template #default="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-color="#10b981" @change="saveAnnouncement(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="Action" width="120">
                <template #default="scope">
                  <el-button type="danger" size="small" class="action-kill" @click="deleteAnnouncement(scope.row.id)">撤回</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </main>

    <el-dialog v-model="bannerDialogVisible" title=">_ Config Banner" width="500px" custom-class="geek-dialog">
      <el-form label-position="top">
        <el-form-item label="Banner Image"><el-upload class="geek-avatar-uploader" action="http://localhost:8080/file/uploadImage" :show-file-list="false" :on-success="(res) => { if(res.code===200) bannerForm.imgUrl = res.data }"><img v-if="bannerForm.imgUrl" :src="bannerForm.imgUrl" style="width: 100%; height: 180px; object-fit: cover;" /><el-icon v-else class="avatar-uploader-icon" style="font-size: 28px; color: #3b82f6;"><Plus /></el-icon></el-upload></el-form-item>
        <el-form-item label="Title"><el-input v-model="bannerForm.title" /></el-form-item>
        <el-form-item label="Target URL"><el-input v-model="bannerForm.targetUrl" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="bannerDialogVisible = false">CANCEL</el-button><el-button type="primary" @click="saveNewBanner">DEPLOY</el-button></template>
    </el-dialog>

    <el-dialog v-model="litDialogVisible" title=">_ Upload Literature / 发布文献" width="500px" custom-class="geek-dialog">
      <el-form label-position="top">
        <el-form-item label="Literature Title (文献标题)">
          <el-input v-model="litForm.title" placeholder="例如：Vue 3 源码解析(官方版)" />
        </el-form-item>
        <el-form-item label="Description (简介)">
          <el-input type="textarea" :rows="3" v-model="litForm.description" placeholder="一句话描述该文献..." />
        </el-form-item>
        <el-form-item label="Target URL (外部链接或PDF直达路径)">
          <el-input v-model="litForm.targetUrl" placeholder="例如：https://..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="geek-btn action-ignore" @click="litDialogVisible = false">CANCEL</el-button>
        <el-button type="primary" class="geek-btn inject-btn" @click="saveNewLiterature">DEPLOY (上架)</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="annDialogVisible" title=">_ System Broadcast / 系统全网广播" width="500px" custom-class="geek-dialog">
      <el-form label-position="top">
        <el-form-item label="Broadcast Title (广播标题)"><el-input v-model="annForm.title" placeholder="例如：系统今晚停机维护通知" /></el-form-item>
        <el-form-item label="Message Body (广播正文)"><el-input type="textarea" :rows="4" v-model="annForm.content" placeholder="输入你想对全站用户说的话..." /></el-form-item>
      </el-form>
      <template #footer>
        <el-button class="geek-btn action-ignore" @click="annDialogVisible = false">CANCEL</el-button>
        <el-button type="primary" class="geek-btn inject-btn" @click="saveNewAnnouncement">EXECUTE (发射)</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import * as echarts from 'echarts'
import { DataLine, Back, UserFilled, Connection, Document, BellFilled, TrendCharts, Filter, Picture, Plus, Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// ========== 系统公告逻辑 ==========
const announcementList = ref([])
const annDialogVisible = ref(false)
const annForm = ref({ title: '', content: '', status: 1 })

const loadAnnouncements = async () => {
  const res = await axios.get('http://localhost:8080/announcement/list')
  if (res.data.code === 200) announcementList.value = res.data.data
}

const saveNewAnnouncement = async () => {
  if (!annForm.value.title || !annForm.value.content) return ElMessage.warning('标题和内容不能为空！')
  await saveAnnouncement(annForm.value)
  annDialogVisible.value = false
  annForm.value = { title: '', content: '', status: 1 }
}

const saveAnnouncement = async (ann) => {
  const res = await axios.post('http://localhost:8080/announcement/save', ann)
  if (res.data.code === 200) { ElMessage.success('公告配置已生效！'); loadAnnouncements() }
}

const deleteAnnouncement = async (id) => {
  const res = await axios.delete(`http://localhost:8080/announcement/delete/${id}`)
  if (res.data.code === 200) loadAnnouncements()
}

// 别忘了在 onMounted 里面加上 loadAnnouncements()

const router = useRouter()
const currentTime = ref(new Date().toLocaleTimeString())
let timer = setInterval(() => { currentTime.value = new Date().toLocaleTimeString() }, 1000)

const activeMenu = ref('dashboard')
const menuTitles = {
  dashboard: 'Global Data Ingestion / 系统流量统计',
  resource_upload: 'Literature Center / 官方文献管理',
  banner_config: 'Resource Recommendation / 推荐位轮播图部署',
  audit: 'Violation Report Pool / 违规工单审批池',
  config: 'DFA FireWall Engine / 敏感词防火墙维护'
}

// ========== 0. 大屏统计 ==========
const stats = ref({ totalUsers: 0, totalGroups: 0, totalNotes: 0, pendingAudits: 0 })
const trendChartRef = ref(null)
let trendChart = null
const loadAdminStats = async () => {
  try {
    const res = await axios.get('http://localhost:8080/admin/stats/overview')
    if (res.data.code === 200) { Object.assign(stats.value, res.data.data); nextTick(() => { if (activeMenu.value === 'dashboard' && res.data.data.trendData) initTrendChart(res.data.data.trendData) }) }
  } catch (e) {}
}
const initTrendChart = (trendData) => { /* 图表逻辑保持不变 */ }

// ========== 1. 官方文献管理逻辑 ==========
const literatureList = ref([])
const litDialogVisible = ref(false)
const litForm = ref({ title: '', description: '', targetUrl: '', status: 1 })

const loadLiteratures = async () => {
  const res = await axios.get('http://localhost:8080/literature/list')
  if (res.data.code === 200) literatureList.value = res.data.data
}

const saveNewLiterature = async () => {
  if (!litForm.value.title || !litForm.value.targetUrl) return ElMessage.warning('标题和链接为必填项！')
  await saveLiterature(litForm.value)
  litDialogVisible.value = false
  litForm.value = { title: '', description: '', targetUrl: '', status: 1 }
}

const saveLiterature = async (lit) => {
  const res = await axios.post('http://localhost:8080/literature/save', lit)
  if (res.data.code === 200) { ElMessage.success('配置已生效！'); loadLiteratures() }
}

const deleteLiterature = async (id) => {
  const res = await axios.delete(`http://localhost:8080/literature/delete/${id}`)
  if (res.data.code === 200) { ElMessage.success('已移除文献'); loadLiteratures() }
}

// ========== 2. 轮播图/审批/敏感词逻辑 (保持原样) ==========
const bannerList = ref([]); const bannerDialogVisible = ref(false); const bannerForm = ref({ imgUrl: '', title: '', targetUrl: '', status: 1 })
const loadBanners = async () => { const res = await axios.get('http://localhost:8080/banner/list'); if (res.data.code === 200) bannerList.value = res.data.data }
const saveNewBanner = async () => { if (!bannerForm.value.imgUrl) return; await saveBanner(bannerForm.value); bannerDialogVisible.value = false; bannerForm.value = { imgUrl: '', title: '', targetUrl: '', status: 1 } }
const saveBanner = async (banner) => { const res = await axios.post('http://localhost:8080/banner/save', banner); if (res.data.code === 200) loadBanners() }
const deleteBanner = async (id) => { const res = await axios.delete(`http://localhost:8080/banner/delete/${id}`); if (res.data.code === 200) loadBanners() }

const reportList = ref([]); const loadReports = async () => { const res = await axios.get('http://localhost:8080/report/list'); if (res.data.code === 200) { reportList.value = res.data.data; stats.value.pendingAudits = reportList.value.filter(r => r.status === 0).length } }
const handleReport = async (report, targetStatus) => { const res = await axios.post(`http://localhost:8080/report/process?reportId=${report.reportId}&status=${targetStatus}&noteId=${report.noteId}`); if (res.data.code === 200) { loadReports(); loadAdminStats() } }

const sensitiveWords = ref([]); const newWord = ref(''); const loadWords = async () => { const res = await axios.get('http://localhost:8080/admin/word/list'); if (res.data.code === 200) sensitiveWords.value = res.data.data }
const handleAddWord = async () => { if (!newWord.value.trim()) return; await axios.post('http://localhost:8080/admin/word/add', { word: newWord.value.trim() }); newWord.value = ''; loadWords() }
const handleDeleteWord = async (id) => { await axios.delete(`http://localhost:8080/admin/word/delete/${id}`); loadWords() }

const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 16) : ''
const handleResize = () => { if (trendChart) trendChart.resize() }

onMounted(() => { loadAdminStats(); loadBanners(); loadLiteratures(); loadReports(); loadWords();loadAnnouncements(); window.addEventListener('resize', handleResize) })
onUnmounted(() => { clearInterval(timer); window.removeEventListener('resize', handleResize); if (trendChart) trendChart.dispose() })
</script>

<style scoped>
/* 保持你原本暗黑极客风的所有CSS，无需修改 */
.admin-layout { display: flex; height: 100vh; width: 100vw; background-color: #0f172a; color: #e2e8f0; font-family: monospace; overflow: hidden; }
.admin-sidebar { width: 260px; background-color: #1e293b; border-right: 1px solid #334155; display: flex; flex-direction: column; }
.brand { padding: 24px; border-bottom: 1px solid #334155; }
.brand .prompt { color: #10b981; font-size: 14px; display: block; margin-bottom: 4px; }
.brand .title { color: #f8fafc; font-size: 20px; font-weight: bold; }
.sidebar-nav { display: flex; flex-direction: column; padding: 20px 0; flex: 1; }
.nav-group-title { color: #475569; font-size: 12px; font-weight: bold; padding: 16px 24px 8px 24px; letter-spacing: 1px; }
.nav-item { padding: 16px 24px; display: flex; align-items: center; gap: 12px; color: #94a3b8; cursor: pointer; transition: all 0.2s; }
.nav-item:hover { background-color: rgba(59, 130, 246, 0.1); color: #e2e8f0; }
.nav-item.active { background-color: #3b82f6; color: #fff; border-right: 4px solid #60a5fa; }
.badge { background: #ef4444; color: white; font-size: 12px; padding: 2px 8px; border-radius: 12px; margin-left: auto; }
.nav-spacer { flex: 1; }
.nav-spacer-line { height: 1px; background: #334155; margin: 10px 24px; }
.back-btn { border-top: 1px solid #334155; color: #cbd5e1; }
.admin-main { flex: 1; display: flex; flex-direction: column; overflow-y: auto; }
.admin-header { height: 70px; padding: 0 32px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #334155; background-color: rgba(30, 41, 59, 0.5); }
.admin-header h2 { font-size: 18px; color: #f8fafc; }
.admin-profile { display: flex; align-items: center; gap: 8px; color: #10b981; }
.pulse-dot { width: 8px; height: 8px; background-color: #10b981; border-radius: 50%; box-shadow: 0 0 8px #10b981; animation: pulse 2s infinite; }
.dashboard-content { padding: 32px; }
.module-container { background: #1e293b; border: 1px solid #334155; border-radius: 12px; padding: 24px; min-height: 500px; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; margin-bottom: 32px; }
.stat-card { background: #1e293b; border: 1px solid #334155; border-radius: 12px; padding: 24px; display: flex; align-items: center; gap: 20px; }
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; justify-content: center; align-items: center; font-size: 28px; }
.stat-icon.blue { background: rgba(59, 130, 246, 0.1); color: #3b82f6; }
.stat-icon.purple { background: rgba(168, 85, 247, 0.1); color: #a855f7; }
.stat-icon.green { background: rgba(16, 185, 129, 0.1); color: #10b981; }
.stat-icon.red { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
.stat-info .label { color: #94a3b8; font-size: 13px; margin-bottom: 8px; }
.stat-info .value { color: #f8fafc; font-size: 28px; font-weight: bold; }
.chart-container { background: #1e293b; border: 1px solid #334155; border-radius: 12px; padding: 24px; }
.trend-chart { width: 100%; height: 350px; }
.tag-pending { color: #eab308; background: rgba(234, 179, 8, 0.1); padding: 4px 8px; border-radius: 4px; font-size: 12px;}
.tag-banned { color: #ef4444; background: rgba(239, 68, 68, 0.1); padding: 4px 8px; border-radius: 4px; font-size: 12px;}
.tag-rejected { color: #94a3b8; background: rgba(148, 163, 184, 0.1); padding: 4px 8px; border-radius: 4px; font-size: 12px;}
.done-text { color: #64748b; font-size: 13px; }
.action-kill { background: transparent; border: 1px solid #ef4444; color: #ef4444; }
.action-ignore { background: transparent; border: 1px solid #94a3b8; color: #cbd5e1; }
.word-input-section { display: flex; gap: 16px; margin-bottom: 30px; }
.geek-input { background: #0f172a; border: 1px solid #3b82f6; color: #10b981; padding: 12px 20px; border-radius: 8px; flex: 1; outline: none; }
.geek-btn { border-radius: 8px; }
.inject-btn { background: #3b82f6; border: none; color: white;}
.word-tags-pool { display: flex; flex-wrap: wrap; gap: 12px; padding: 20px; background: #0f172a; border-radius: 8px; border: 1px dashed #334155; min-height: 200px; align-content: flex-start; }
.empty-words { color: #475569; width: 100%; text-align: center; margin-top: 40px; }
:deep(.el-table) { background-color: transparent !important; color: #cbd5e1 !important; border-radius: 12px;}
:deep(.el-table th.el-table__cell) { background-color: #0f172a !important; color: #3b82f6 !important; border-bottom: 1px solid #334155 !important; }
:deep(.el-table tr) { background-color: transparent !important; }
:deep(.el-table td.el-table__cell) { border-bottom: 1px solid #334155 !important; }
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) { background-color: rgba(59, 130, 246, 0.1) !important; }
.fade-in { animation: fadeIn 0.3s ease-in; }
@keyframes pulse { 0% { opacity: 1; transform: scale(1); } 50% { opacity: 0.5; transform: scale(1.5); } 100% { opacity: 1; transform: scale(1); } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>