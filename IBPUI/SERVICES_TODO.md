# IBPUI 服务调用实现清单

> 基于 V3XUI 全面对比，记录所有需要实现的服务调用

---

## ✅ 已完成

### 认证模块
- [x] 登录 - `DCP_LoginRetail`
- [x] 登出 - `DCP_Logout`
- [x] 获取用户信息 - `DCP_UserInfoQuery`
- [x] 获取菜单权限 - 登录返回 `myPower`

### 系统管理
- [x] 角色管理 - `DCP_RoleListQuery`, `DCP_RoleCreate`, `DCP_RoleUpdate`, `DCP_RoleDelete`
- [x] 菜单管理 - `DCP_MenuTreeQuery`, `DCP_MenuCreate`, `DCP_MenuUpdate`, `DCP_MenuDelete`
- [x] 部门管理 - `DCP_DeptTreeQuery`, `DCP_DeptCreate`, `DCP_DeptUpdate`, `DCP_DeptDelete`
- [x] 岗位管理 - `DCP_PostListQuery`, `DCP_PostCreate`, `DCP_PostUpdate`, `DCP_PostDelete`

### 首页
- [x] 待办事项 - `DCP_ToDoListQueryNew`
- [x] 服务器日期 - `DCP_ServerCurDateQuery`
- [x] 系统监控 - `DCP_MainInfoQuery`
- [x] 版本信息 - `CMS_VersionList`
- [x] 异常报告 - `DCP_MainErrorInfoQuery`
- [x] 收藏功能 - `DCP_CollectionUpdate`
- [x] AI Token - `DCP_GetAI_AccessToken`

---

## 📋 待实现 - 核心业务模块 (P0)

### 商品管理 (`/goods-manage`)
- [ ] 商品列表 - `DCP_GoodsListQuery`
- [ ] 商品详情 - `DCP_GoodsDetailQuery`
- [ ] 商品创建 - `DCP_GoodsCreate`
- [ ] 商品更新 - `DCP_GoodsUpdate`
- [ ] 商品删除 - `DCP_GoodsDelete`
- [ ] 商品分类 - `DCP_GoodsClassQuery`
- [ ] SKU 管理 - `DCP_GoodsSkuQuery`
- [ ] 商品上下架 - `DCP_GoodsStatusUpdate`

### 库存管理 (`/stock-manage`)
- [ ] 库存查询 - `DCP_StockListQuery`
- [ ] 入库单 - `DCP_StockInListQuery`
- [ ] 出库单 - `DCP_StockOutListQuery`
- [ ] 库存调拨 - `DCP_StockTransferQuery`
- [ ] 库存盘点 - `DCP_StockCheckQuery`
- [ ] 库存预警 - `DCP_StockWarningQuery`

### 订单管理 (`/order-manage`)
- [ ] 订单列表 - `DCP_OrderListQuery`
- [ ] 订单详情 - `DCP_OrderDetailQuery`
- [ ] 订单发货 - `DCP_OrderDelivery`
- [ ] 订单退款 - `DCP_OrderRefund`
- [ ] 订单取消 - `DCP_OrderCancel`

---

## 📋 待实现 - 重要业务模块 (P1)

### 会员管理 (`/vip`)
- [ ] 会员列表 - `DCP_MemberListQuery`
- [ ] 会员详情 - `DCP_MemberDetailQuery`
- [ ] 会员等级 - `DCP_MemberLevelQuery`
- [ ] 会员积分 - `DCP_MemberPointQuery`
- [ ] 会员储值 - `DCP_MemberValueQuery`

### 优惠券 (`/coupon`)
- [ ] 优惠券列表 - `DCP_CouponListQuery`
- [ ] 优惠券创建 - `DCP_CouponCreate`
- [ ] 优惠券发放 - `DCP_CouponSend`
- [ ] 优惠券使用 - `DCP_CouponUseQuery`

### 营销活动 (`/marketActivity`)
- [ ] 活动列表 - `DCP_ActivityListQuery`
- [ ] 活动创建 - `DCP_ActivityCreate`
- [ ] 促销规则 - `DCP_PromRuleQuery`

### 财务管理 (`/financial-manage`)
- [ ] 收款单 - `DCP_ReceiptListQuery`
- [ ] 付款单 - `DCP_PaymentListQuery`
- [ ] 对账单 - `DCP_ReconciliationQuery`

### 采购管理 (`/purchasing-manage`)
- [ ] 采购申请 - `DCP_PurchaseApplyQuery`
- [ ] 采购订单 - `DCP_PurchaseOrderQuery`
- [ ] 采购入库 - `DCP_PurchaseInQuery`

---

## 📋 待实现 - 辅助模块 (P2)

### 基础数据 (`/base-data`)
- [ ] 供应商 - `DCP_SupplierListQuery`
- [ ] 客户 - `DCP_CustomerListQuery`
- [ ] 仓库 - `DCP_WarehouseQuery`

### 物流配送 (`/logistics-manage`)
- [ ] 配送单 - `DCP_DeliveryListQuery`
- [ ] 物流跟踪 - `DCP_LogisticsQuery`

### 系统设置 (`/system-setting`)
- [ ] 参数配置 - `DCP_ParamQuery`
- [ ] 字典管理 - `DCP_DictionaryQuery`

---

## 📝 实现规范

### API 文件结构
```typescript
// src/api/[module].ts
import { post } from '@/utils/PostService'

export function getList(params: any) {
  return post(1, {
    serviceId: 'DCP_XXXListQuery',
    request: params
  })
}
```

### 页面组件结构
```typescript
// src/views/[module]/List.vue
import { getList } from '@/api/[module]'

onMounted(() => {
  loadList()
})

const loadList = async () => {
  const res = await getList({ page: 1, pageSize: 10 })
  if (res.success) {
    tableData.value = res.datas
  }
}
```

---

## 🎯 优先级说明

- **P0**: 核心业务，必须优先完成（商品、库存、订单）
- **P1**: 重要业务，高优先级（会员、营销、财务）
- **P2**: 辅助功能，低优先级（基础数据、物流）

---

*最后更新：2026-04-17*
