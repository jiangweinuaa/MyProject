# 系统管理模块 - 服务调用修复清单

## 🔍 问题汇总

所有系统管理页面目前都使用 **mockData**，没有从服务读取数据！

## ✅ 修复方案

### 1. 用户管理 (`/system/user`)

**当前问题：**
```typescript
const mockData: Role[] = [...] // ❌ 静态数据
```

**修复为：**
```typescript
import { getUserList } from '@/api/system'

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: page.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    if (res.success) {
      tableData.value = res.datas?.list || []
      total.value = res.datas?.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}
```

### 2. 角色管理 (`/system/role`)

**需要修复：**
- [ ] `getRoleList()` - 替换 mockData
- [ ] `createRole()` - 实际调用
- [ ] `updateRole()` - 实际调用
- [ ] `deleteRole()` - 实际调用
- [ ] `getRoleMenus()` - 权限加载
- [ ] `saveRoleMenus()` - 权限保存

### 3. 菜单管理 (`/system/menu`)

**需要修复：**
- [ ] `getMenuTree()` - 替换 mockMenuTree
- [ ] `createMenu()` - 实际调用
- [ ] `updateMenu()` - 实际调用
- [ ] `deleteMenu()` - 实际调用

### 4. 部门管理 (`/system/dept`)

**需要修复：**
- [ ] `getDeptTree()` - 替换 mockDeptTree
- [ ] `createDept()` - 实际调用
- [ ] `updateDept()` - 实际调用
- [ ] `deleteDept()` - 实际调用

### 5. 岗位管理 (`/system/post`)

**需要修复：**
- [ ] `getPostList()` - 替换 mockData
- [ ] `createPost()` - 实际调用
- [ ] `updatePost()` - 实际调用
- [ ] `deletePost()` - 实际调用

---

## 📝 统一规范

### API 调用格式
```typescript
// 列表查询
const res = await getXxxList({
  page: 1,
  pageSize: 10,
  ...searchParams
})

// 成功判断
if (res.success && res.datas) {
  tableData.value = res.datas.list || res.datas
  total.value = res.datas.total || 0
}

// 新增/编辑/删除
const res = await createXxx(data)
if (res.success) {
  ElMessage.success('操作成功')
  loadData() // 刷新列表
}
```

### 错误处理
```typescript
try {
  const res = await getXxx()
  if (!res.success) {
    ElMessage.error(res.serviceDescription || '操作失败')
    return
  }
  // 处理成功逻辑
} catch (error) {
  console.error('请求失败:', error)
  ElMessage.error('网络错误')
}
```

---

## 🎯 修复优先级

1. **用户管理** - 最常用
2. **角色管理** - 权限核心
3. **菜单管理** - 导航核心
4. **部门管理** - 组织架构
5. **岗位管理** - 人员管理

---

*创建时间：2026-04-17*
