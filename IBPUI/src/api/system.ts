import { post } from '@/utils/PostService'

// ==================== 用户管理 ====================
export interface User {
  id?: number
  username: string
  phone?: string
  email?: string
  roleId?: number
  roleName?: string
  status?: number
  createTime?: string
}

export interface Role {
  id?: number
  roleName: string
  roleCode: string
  description?: string
  status?: number
  createTime?: string
  updateTime?: string
}

export interface Menu {
  id?: number
  parentId: number
  name: string
  path?: string
  component?: string
  icon?: string
  type: number // 1:目录 2:菜单 3:按钮
  permission?: string
  sort?: number
  visible?: number
  createTime?: string
  children?: Menu[]
}

export interface Dept {
  id?: number
  parentId: number
  deptName: string
  deptCode: string
  leader?: string
  phone?: string
  email?: string
  sort?: number
  status?: number
  createTime?: string
  children?: Dept[]
}

export interface Post {
  id?: number
  postCode: string
  postName: string
  description?: string
  sort?: number
  status?: number
  createTime?: string
}

// ==================== 角色管理 ====================
export function getRoleList(params: any) {
  return post(1, {
    serviceId: 'DCP_RoleListQuery',
    request: params
  })
}

export function getRoleDetail(id: number) {
  return post(1, {
    serviceId: 'DCP_RoleDetailQuery',
    request: { id }
  })
}

export function createRole(data: Role) {
  return post(1, {
    serviceId: 'DCP_RoleCreate',
    request: data
  })
}

export function updateRole(id: number, data: Role) {
  return post(1, {
    serviceId: 'DCP_RoleUpdate',
    request: { id, ...data }
  })
}

export function deleteRole(id: number) {
  return post(1, {
    serviceId: 'DCP_RoleDelete',
    request: { id }
  })
}

export function getRoleMenus(roleId: number) {
  return post(1, {
    serviceId: 'DCP_RoleMenuQuery',
    request: { roleId }
  })
}

export function saveRoleMenus(roleId: number, menuIds: number[]) {
  return post(1, {
    serviceId: 'DCP_RoleMenuSave',
    request: { roleId, menuIds }
  })
}

// ==================== 菜单管理 ====================
export function getMenuTree() {
  return post(1, {
    serviceId: 'DCP_MenuTreeQuery',
    request: {}
  })
}

export function getMenuDetail(id: number) {
  return post(1, {
    serviceId: 'DCP_MenuDetailQuery',
    request: { id }
  })
}

export function createMenu(data: Menu) {
  return post(1, {
    serviceId: 'DCP_MenuCreate',
    request: data
  })
}

export function updateMenu(id: number, data: Menu) {
  return post(1, {
    serviceId: 'DCP_MenuUpdate',
    request: { id, ...data }
  })
}

export function deleteMenu(id: number) {
  return post(1, {
    serviceId: 'DCP_MenuDelete',
    request: { id }
  })
}

// ==================== 部门管理 ====================
export function getDeptTree() {
  return post(1, {
    serviceId: 'DCP_DeptTreeQuery',
    request: {}
  })
}

export function getDeptDetail(id: number) {
  return post(1, {
    serviceId: 'DCP_DeptDetailQuery',
    request: { id }
  })
}

export function createDept(data: Dept) {
  return post(1, {
    serviceId: 'DCP_DeptCreate',
    request: data
  })
}

export function updateDept(id: number, data: Dept) {
  return post(1, {
    serviceId: 'DCP_DeptUpdate',
    request: { id, ...data }
  })
}

export function deleteDept(id: number) {
  return post(1, {
    serviceId: 'DCP_DeptDelete',
    request: { id }
  })
}

// ==================== 岗位管理 ====================
export function getPostList(params: any) {
  return post(1, {
    serviceId: 'DCP_PostListQuery',
    request: params
  })
}

export function getPostDetail(id: number) {
  return post(1, {
    serviceId: 'DCP_PostDetailQuery',
    request: { id }
  })
}

export function createPost(data: Post) {
  return post(1, {
    serviceId: 'DCP_PostCreate',
    request: data
  })
}

export function updatePost(id: number, data: Post) {
  return post(1, {
    serviceId: 'DCP_PostUpdate',
    request: { id, ...data }
  })
}

export function deletePost(id: number) {
  return post(1, {
    serviceId: 'DCP_PostDelete',
    request: { id }
  })
}

// ==================== 用户管理 ====================
export function getUserList(params: any) {
  return post(1, {
    serviceId: 'DCP_UserListQuery',
    request: params
  })
}

export function getUserDetail(id: number) {
  return post(1, {
    serviceId: 'DCP_UserDetailQuery',
    request: { id }
  })
}

export function createUser(data: User) {
  return post(1, {
    serviceId: 'DCP_UserCreate',
    request: data
  })
}

export function updateUser(id: number, data: User) {
  return post(1, {
    serviceId: 'DCP_UserUpdate',
    request: { id, ...data }
  })
}

export function deleteUser(id: number) {
  return post(1, {
    serviceId: 'DCP_UserDelete',
    request: { id }
  })
}
