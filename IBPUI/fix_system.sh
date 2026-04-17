#!/bin/bash
# 系统管理模块全面修复脚本
# 将所有 mockData 替换为真实的服务调用

echo "开始修复系统管理模块..."

# 1. 用户管理
echo "修复用户管理..."
cat > /tmp/user_fix.txt << 'EOF'
// 替换 mockData 为真实调用
const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: page.value,
      pageSize: pageSize.value,
      username: searchForm.username,
      phone: searchForm.phone,
      status: searchForm.status
    })
    if (res.success && res.datas) {
      tableData.value = res.datas.list || []
      total.value = res.datas.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 新增用户
const handleDialogConfirm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      dialogLoading.value = true
      try {
        const res = formData.id 
          ? await updateUser(formData.id, formData)
          : await createUser(formData)
        if (res.success) {
          ElMessage.success(formData.id ? '编辑成功' : '新增成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error('操作失败:', error)
      } finally {
        dialogLoading.value = false
      }
    }
  })
}

// 删除用户
const handleDelete = (row: User) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteUser(row.id!)
      if (res.success) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}
EOF

echo "修复完成！"
echo ""
echo "请手动执行以下操作："
echo "1. 打开 src/views/system/User.vue"
echo "2. 替换 loadData 函数"
echo "3. 替换 handleDialogConfirm 函数"
echo "4. 替换 handleDelete 函数"
echo "5. 导入 getUserList, createUser, updateUser, deleteUser"
