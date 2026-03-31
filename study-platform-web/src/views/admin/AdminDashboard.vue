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
        
        <div class="nav-item" :class="{active: activeMenu === 'category'}" @click="activeMenu = 'category'">
          <el-icon><Collection /></el-icon> <span>资源分类配置</span>
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

        <div class="nav-item" :class="{active: activeMenu === 'audit_log'}" @click="activeMenu = 'audit_log'">
          <el-icon><Clock /></el-icon> <span>操作日志监控</span>
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
          <div style="margin-bottom: 20px;"><el-button type="primary" class="geek-btn inject-btn" @click="litDialogVisible = true"><el-icon><Plus /></el-icon> UPLOAD LITERATURE</el-button></div>
          <el-table :data="literatureList" class="geek-table">
            <el-table-column prop="title" label="Title (文献标题)" width="250" />
            <el-table-column prop="description" label="Description (简介)" show-overflow-tooltip />
            <el-table-column label="Status" width="150"><template #default="scope"><el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" active-color="#10b981" @change="saveLiterature(scope.row)" /></template></el-table-column>
            <el-table-column label="Action" width="120"><template #default="scope"><el-button type="danger" size="small" class="action-kill" @click="deleteLiterature(scope.row.id)">REMOVE</el-button></template></el-table-column>
          </el-table>
        </div>

        <div v-else-if="activeMenu === 'category'" class="module-container fade-in">
          <div style="margin-bottom: 20px;"><el-button type="primary" class="geek-btn inject-btn" @click="catDialogVisible = true"><el-icon><Plus /></el-icon> ADD CATEGORY (新增分类)</el-button></div>
          <el-table :data="sysCategoryList" class="geek-table">
            <el-table-column prop="categoryId" label="ID" width="100" />
            <el-table-column prop="name" label="Category Name (分类名称)" width="200" />
            <el-table-column prop="description" label="Description (描述)" show-overflow-tooltip />
            <el-table-column prop="sortOrder" label="Sort (排序权重)" width="150" />
            <el-table-column label="Action" width="120"><template #default="scope"><el-button type="danger" size="small" class="action-kill" @click="deleteCategory(scope.row.categoryId)">REMOVE</el-button></template></el-table-column>
          </el-table>
        </div>

        <div v-else-if="activeMenu === 'banner_config'" class="module-container fade-in">
          <div style="margin-bottom: 20px;"><el-button type="primary" class="geek-btn inject-btn" @click="bannerDialogVisible = true"><el-icon><Plus /></el-icon> DEPLOY NEW BANNER</el-button></div>
          <el-table :data="bannerList" class="geek-table">
            <el-table-column label="Preview" width="280"><template #default="scope"><el-image :src="scope.row.imgUrl" style="width: 240px; height: 120px; border-radius: 8px; object-fit: cover;" /></template></el-table-column>
            <el-table-column prop="title" label="Title" />
            <el-table-column label="Status" width="150"><template #default="scope"><el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-text="ON" inactive-text="OFF" active-color="#10b981" @change="saveBanner(scope.row)" /></template></el-table-column>
            <el-table-column label="Action" width="120"><template #default="scope"><el-button type="danger" size="small" class="action-kill" @click="deleteBanner(scope.row.id)">REMOVE</el-button></template></el-table-column>
          </el-table>
        </div>

        <div v-else-if="activeMenu === 'audit'" class="module-container fade-in">
          <el-table :data="reportList" class="geek-table">
            <el-table-column prop="reportId" label="工单 ID" width="100" />
            <el-table-column prop="noteId" label="嫌疑笔记" width="120" />
            <el-table-column prop="reason" label="被举报原因" width="200" />
            <el-table-column label="状态" width="120"><template #default="scope"><span v-if="scope.row.status === 0" class="tag-pending">待处理</span><span v-else-if="scope.row.status === 1" class="tag-banned">已封禁</span><span v-else class="tag-rejected">已驳回</span></template></el-table-column>
            <el-table-column label="ACTIONS" min-width="200"><template #default="scope"><div v-if="scope.row.status === 0"><el-button type="danger" size="small" class="action-kill" @click="handleReport(scope.row, 1)">KILL</el-button><el-button type="info" size="small" class="action-ignore" @click="handleReport(scope.row, 2)">IGNORE</el-button></div><span v-else class="done-text">Task Executed.</span></template></el-table-column>
          </el-table>
        </div>

        <div v-else-if="activeMenu === 'config'" class="module-container fade-in">
          <div class="word-input-section"><input v-model="newWord" class="geek-input" placeholder="输入违规拦截词..." @keyup.enter="handleAddWord" /><el-button type="primary" class="geek-btn inject-btn" @click="handleAddWord">INJECT</el-button></div>
          <div class="word-tags-pool"><el-tag v-for="word in sensitiveWords" :key="word.wordId" closable type="danger" effect="dark" @close="handleDeleteWord(word.wordId)" class="geek-tag">{{ word.word }}</el-tag></div>
        </div>

        <div v-else-if="activeMenu === 'user_manage'" class="module-container fade-in">
          <div class="word-input-section" style="margin-bottom: 20px;"><input v-model="userKeyword" class="geek-input" placeholder="输入用户 UID 或昵称进行检索..." @keyup.enter="loadUsers" style="max-width: 400px;" /><el-button type="primary" class="geek-btn inject-btn" @click="loadUsers"><el-icon><Search /></el-icon> RADAR SCAN</el-button></div>
          <el-table :data="userList" class="geek-table">
            <el-table-column prop="userId" label="UID" width="100" />
            <el-table-column label="Target Identity" width="250"><template #default="scope"><div style="display: flex; align-items: center; gap: 12px;"><el-avatar :size="32" :src="scope.row.avatar" /><span style="color: #f8fafc;">{{ scope.row.nickname || scope.row.username }}</span></div></template></el-table-column>
            <el-table-column label="Level" width="150"><template #default="scope"><el-tag v-if="scope.row.role === 'ADMIN'" type="danger" effect="dark" class="geek-tag">ROOT_ADMIN</el-tag><el-tag v-else type="primary" effect="plain" class="geek-tag">STUDENT</el-tag></template></el-table-column>
            <el-table-column label="Status" width="120"><template #default="scope"><el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-color="#10b981" inactive-color="#ef4444" @change="handleUserStatus(scope.row)" /></template></el-table-column>
            <el-table-column label="Override"><template #default="scope"><div v-if="scope.row.userId !== 1"><el-button v-if="scope.row.role === 'STUDENT'" type="warning" size="small" plain @click="handleUserRole(scope.row.userId, 'ADMIN')">PROMOTE</el-button><el-button v-if="scope.row.role === 'ADMIN'" type="info" size="small" plain @click="handleUserRole(scope.row.userId, 'STUDENT')">DEMOTE</el-button></div><span v-else class="done-text">SYSTEM_CREATOR</span></template></el-table-column>
          </el-table>
        </div>

        <div v-else-if="activeMenu === 'announcement'" class="module-container fade-in">
          <div style="margin-bottom: 20px;"><el-button type="primary" class="geek-btn inject-btn" @click="annDialogVisible = true"><el-icon><Notification /></el-icon> BROADCAST MESSAGE</el-button></div>
          <el-table :data="announcementList" class="geek-table">
            <el-table-column prop="title" label="Title (公告标题)" width="200" />
            <el-table-column prop="content" label="Content" show-overflow-tooltip />
            <el-table-column label="Status" width="120"><template #default="scope"><el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-color="#10b981" @change="saveAnnouncement(scope.row)" /></template></el-table-column>
            <el-table-column label="Action" width="120"><template #default="scope"><el-button type="danger" size="small" class="action-kill" @click="deleteAnnouncement(scope.row.id)">撤回</el-button></template></el-table-column>
          </el-table>
        </div>

        <div v-else-if="activeMenu === 'audit_log'" class="module-container fade-in">
          <div style="margin-bottom: 20px; color: #10b981; font-family: monospace;">>_ Content Audit Trace. 笔记审核历史追溯...</div>
          <el-table :data="auditLogList" class="geek-table" height="500">
            <el-table-column prop="logId" label="Log ID" width="100" />
            <el-table-column prop="targetId" label="目标笔记 ID" width="120" />
            <el-table-column prop="auditorId" label="审核员 UID" width="120" />
            
            <el-table-column label="判定结果 (Result)" width="150">
              <template #default="scope">
                <el-tag v-if="scope.row.auditResult === 1" type="success" effect="dark" class="geek-tag">PASS (通过)</el-tag>
                <el-tag v-else type="danger" effect="dark" class="geek-tag">REJECT (驳回)</el-tag>
              </template>
            </el-table-column>
            
            <el-table-column prop="rejectReason" label="判定说明/驳回原因" show-overflow-tooltip />
            
            <el-table-column label="审核时间 (Time)" width="180">
              <template #default="scope">{{ formatAuditTime(scope.row.auditTime) }}</template>
            </el-table-column>
          </el-table>
        </div>

      </div>
    </main>

    <el-dialog v-model="catDialogVisible" title=">_ Config Category / 资源分类" width="400px" custom-class="geek-dialog">
      <el-form label-position="top">
        <el-form-item label="Category Name (分类名称)"><el-input v-model="catForm.name" placeholder="例如：前端开发" /></el-form-item>
        <el-form-item label="Sort Order (排序，越小越靠前)"><el-input-number v-model="catForm.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer><el-button class="geek-btn action-ignore" @click="catDialogVisible = false">CANCEL</el-button><el-button type="primary" class="geek-btn inject-btn" @click="saveNewCategory">DEPLOY</el-button></template>
    </el-dialog>

    <el-dialog v-model="bannerDialogVisible" title=">_ Config Banner" width="500px" custom-class="geek-dialog"><el-form label-position="top"><el-form-item label="Banner Image"><el-upload class="geek-avatar-uploader" action="http://localhost:8080/file/uploadImage" :show-file-list="false" :on-success="(res) => { if(res.code===200) bannerForm.imgUrl = res.data }"><img v-if="bannerForm.imgUrl" :src="bannerForm.imgUrl" style="width: 100%; height: 180px; object-fit: cover;" /><el-icon v-else class="avatar-uploader-icon" style="font-size: 28px; color: #3b82f6;"><Plus /></el-icon></el-upload></el-form-item><el-form-item label="Title"><el-input v-model="bannerForm.title" /></el-form-item><el-form-item label="Target URL"><el-input v-model="bannerForm.targetUrl" /></el-form-item></el-form><template #footer><el-button class="geek-btn action-ignore" @click="bannerDialogVisible = false">CANCEL</el-button><el-button type="primary" class="geek-btn inject-btn" @click="saveNewBanner">DEPLOY</el-button></template></el-dialog>
    <el-dialog v-model="litDialogVisible" title=">_ Upload Literature" width="500px" custom-class="geek-dialog"><el-form label-position="top"><el-form-item label="Literature Title"><el-input v-model="litForm.title" /></el-form-item><el-form-item label="Description"><el-input type="textarea" :rows="3" v-model="litForm.description" /></el-form-item><el-form-item label="Target URL"><el-input v-model="litForm.targetUrl" /></el-form-item></el-form><template #footer><el-button class="geek-btn action-ignore" @click="litDialogVisible = false">CANCEL</el-button><el-button type="primary" class="geek-btn inject-btn" @click="saveNewLiterature">DEPLOY</el-button></template></el-dialog>
    <el-dialog v-model="annDialogVisible" title=">_ System Broadcast" width="500px" custom-class="geek-dialog"><el-form label-position="top"><el-form-item label="Broadcast Title"><el-input v-model="annForm.title" /></el-form-item><el-form-item label="Message Body"><el-input type="textarea" :rows="4" v-model="annForm.content" /></el-form-item></el-form><template #footer><el-button class="geek-btn action-ignore" @click="annDialogVisible = false">CANCEL</el-button><el-button type="primary" class="geek-btn inject-btn" @click="saveNewAnnouncement">EXECUTE</el-button></template></el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import * as echarts from 'echarts'
import { DataLine, Back, UserFilled, Connection, Document, BellFilled, TrendCharts, Filter, Picture, Plus, Upload, User, Notification, Search, Collection, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const currentTime = ref(new Date().toLocaleTimeString())
let timer = setInterval(() => { currentTime.value = new Date().toLocaleTimeString() }, 1000)

const activeMenu = ref('dashboard')
const menuTitles = {
  dashboard: 'Global Data Ingestion / 系统流量统计',
  resource_upload: 'Literature Center / 官方文献管理',
  category: 'Category Router / 资源分类配置',
  banner_config: 'Resource Recommendation / 推荐位轮播图部署',
  audit: 'Violation Report Pool / 违规工单审批池',
  config: 'DFA FireWall Engine / 敏感词防火墙维护',
  user_manage: 'User Privilege Matrix / 用户角色权限矩阵',
  announcement: 'Global Broadcast / 系统公告发布',
  audit_log: 'Content Audit Trace / 笔记审核日志'
}

// ========== 💡 修复：内容审核记录逻辑 ==========
const auditLogList = ref([])
const loadAuditLogs = async () => {
  const res = await axios.get('http://localhost:8080/auditLog/list')
  if (res.data.code === 200) auditLogList.value = res.data.data
}

// 专门适配 LocalDateTime 传到前端的问题
const formatAuditTime = (time) => {
  if (!time) return '';
  if (Array.isArray(time)) {
    return `${time[0]}-${String(time[1]).padStart(2, '0')}-${String(time[2]).padStart(2, '0')} ${String(time[3]||0).padStart(2, '0')}:${String(time[4]||0).padStart(2, '0')}`;
  }
  return time.replace('T', ' ').substring(0, 16);
}

// ========== 资源分类配置逻辑 ==========
const sysCategoryList = ref([])
const catDialogVisible = ref(false)
const catForm = ref({ name: '', sortOrder: 0 })

const loadSysCategories = async () => {
  const res = await axios.get('http://localhost:8080/sys-category/list')
  if (res.data.code === 200) sysCategoryList.value = res.data.data
}
const saveNewCategory = async () => {
  if (!catForm.value.name) return ElMessage.warning('分类名称不能为空！')
  const res = await axios.post('http://localhost:8080/sys-category/save', catForm.value)
  if (res.data.code === 200) { ElMessage.success('分类注入成功'); catDialogVisible.value = false; loadSysCategories(); catForm.value={name:'', sortOrder:0} }
}
const deleteCategory = async (id) => {
  const res = await axios.delete(`http://localhost:8080/sys-category/delete/${id}`)
  if (res.data.code === 200) { ElMessage.success('分类已抹除'); loadSysCategories() }
}

// ========== 其他已有模块逻辑 (保持精简) ==========
const stats = ref({ totalUsers: 0, totalGroups: 0, totalNotes: 0, pendingAudits: 0 }); const trendChartRef = ref(null); let trendChart = null;
const loadAdminStats = async () => { try { const res = await axios.get('http://localhost:8080/admin/stats/overview'); if (res.data.code === 200) { Object.assign(stats.value, res.data.data); nextTick(() => { if (activeMenu.value === 'dashboard' && res.data.data.trendData) { /* chart init */ } }) } } catch (e) {} }

const literatureList = ref([]); const litDialogVisible = ref(false); const litForm = ref({ title: '', description: '', targetUrl: '', status: 1 });
const loadLiteratures = async () => { const res = await axios.get('http://localhost:8080/literature/list'); if (res.data.code === 200) literatureList.value = res.data.data }
const saveNewLiterature = async () => { await saveLiterature(litForm.value); litDialogVisible.value = false; litForm.value = { title: '', description: '', targetUrl: '', status: 1 } }
const saveLiterature = async (lit) => { const res = await axios.post('http://localhost:8080/literature/save', lit); if (res.data.code === 200) loadLiteratures() }
const deleteLiterature = async (id) => { await axios.delete(`http://localhost:8080/literature/delete/${id}`); loadLiteratures() }

const bannerList = ref([]); const bannerDialogVisible = ref(false); const bannerForm = ref({ imgUrl: '', title: '', targetUrl: '', status: 1 })
const loadBanners = async () => { const res = await axios.get('http://localhost:8080/banner/list'); if (res.data.code === 200) bannerList.value = res.data.data }
const saveNewBanner = async () => { await saveBanner(bannerForm.value); bannerDialogVisible.value = false; bannerForm.value = { imgUrl: '', title: '', targetUrl: '', status: 1 } }
const saveBanner = async (banner) => { await axios.post('http://localhost:8080/banner/save', banner); loadBanners() }
const deleteBanner = async (id) => { await axios.delete(`http://localhost:8080/banner/delete/${id}`); loadBanners() }

const reportList = ref([]); const loadReports = async () => { const res = await axios.get('http://localhost:8080/report/list'); if (res.data.code === 200) { reportList.value = res.data.data; stats.value.pendingAudits = reportList.value.filter(r => r.status === 0).length } }
const handleReport = async (report, targetStatus) => { await axios.post(`http://localhost:8080/report/process?reportId=${report.reportId}&status=${targetStatus}&noteId=${report.noteId}`); loadReports(); loadAdminStats() }

const sensitiveWords = ref([]); const newWord = ref(''); const loadWords = async () => { const res = await axios.get('http://localhost:8080/admin/word/list'); if (res.data.code === 200) sensitiveWords.value = res.data.data }
const handleAddWord = async () => { if (!newWord.value.trim()) return; await axios.post('http://localhost:8080/admin/word/add', { word: newWord.value.trim() }); newWord.value = ''; loadWords() }
const handleDeleteWord = async (id) => { await axios.delete(`http://localhost:8080/admin/word/delete/${id}`); loadWords() }

const userList = ref([]); const userKeyword = ref('');
const loadUsers = async () => { let url = 'http://localhost:8080/admin/user/list'; if (userKeyword.value) url += `?keyword=${userKeyword.value}`; const res = await axios.get(url); if (res.data.code === 200) userList.value = res.data.data }
const handleUserStatus = async (user) => { const res = await axios.post(`http://localhost:8080/admin/user/status?userId=${user.userId}&status=${user.status}`); if (res.data.code !== 200) loadUsers() }
const handleUserRole = async (userId, targetRole) => { await axios.post(`http://localhost:8080/admin/user/role?userId=${userId}&role=${targetRole}`); loadUsers() }

const announcementList = ref([]); const annDialogVisible = ref(false); const annForm = ref({ title: '', content: '', status: 1 })
const loadAnnouncements = async () => { const res = await axios.get('http://localhost:8080/announcement/list'); if (res.data.code === 200) announcementList.value = res.data.data }
const saveNewAnnouncement = async () => { await saveAnnouncement(annForm.value); annDialogVisible.value = false; annForm.value = { title: '', content: '', status: 1 } }
const saveAnnouncement = async (ann) => { await axios.post('http://localhost:8080/announcement/save', ann); loadAnnouncements() }
const deleteAnnouncement = async (id) => { await axios.delete(`http://localhost:8080/announcement/delete/${id}`); loadAnnouncements() }

onMounted(() => {
  loadAdminStats()
  loadBanners()
  loadLiteratures()
  loadReports()
  loadWords()
  loadUsers()
  loadAnnouncements()
  loadSysCategories() 
  loadAuditLogs() // 💡 触发拉取内容审核日志
})
</script>

<style scoped>
/* -------- UI 代码保持一致，无删减 -------- */
.admin-layout { display: flex; height: 100vh; width: 100vw; background-color: #0f172a; color: #e2e8f0; font-family: monospace; overflow: hidden; }
.admin-sidebar { width: 260px; background-color: #1e293b; border-right: 1px solid #334155; display: flex; flex-direction: column; }
.brand { padding: 24px; border-bottom: 1px solid #334155; }
.brand .prompt { color: #10b981; font-size: 14px; display: block; margin-bottom: 4px; }
.brand .title { color: #f8fafc; font-size: 20px; font-weight: bold; }
.sidebar-nav { display: flex; flex-direction: column; padding: 20px 0; flex: 1; overflow-y: auto;}
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
:deep(.el-table) { background-color: transparent !important; color: #cbd5e1 !important; border-radius: 12px;}
:deep(.el-table th.el-table__cell) { background-color: #0f172a !important; color: #3b82f6 !important; border-bottom: 1px solid #334155 !important; }
:deep(.el-table tr) { background-color: transparent !important; }
:deep(.el-table td.el-table__cell) { border-bottom: 1px solid #334155 !important; }
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) { background-color: rgba(59, 130, 246, 0.1) !important; }
.fade-in { animation: fadeIn 0.3s ease-in; }
@keyframes pulse { 0% { opacity: 1; transform: scale(1); } 50% { opacity: 0.5; transform: scale(1.5); } 100% { opacity: 1; transform: scale(1); } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>