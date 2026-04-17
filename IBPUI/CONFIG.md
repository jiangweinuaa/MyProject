# IBPUI 配置文件说明

## 📁 配置文件

### `public/config_retail.js` - 主配置文件
- **完全复制** V3XUI 的 `config_retail.js`
- 保持相同的配置结构和命名 (`var Config = {...}`)
- **无 export**，使用全局变量 `Config`
- 通过 `<script>` 标签在 `index.html` 中加载

### `src/config.js` - 开发用配置
- 与 `public/config_retail.js` 内容相同
- 供 TypeScript 代码引用（类型提示）

---

## 🔧 配置结构（与 V3XUI 完全一致）

```javascript
var Config = {
  sms: {      // 门店管理系统 (DCP)
    http: {
      postUrl: "http://localhost:8080/dcpService_v3x/DCP/services/invoke",
      goodImagesUrl: "...",
      reportImagesUrl: "...",
      dualUrl: "...",
      GDkey: "...",
      documentUploadUrl: "...",
    }
  },
  crm: {      // 会员管理系统
    http: {
      postUrl: "...",
      uploadUrl: "...",
    }
  },
  supplier: { // 供应商平台
    http: { postUrl: "" }
  },
  sale: {     // 营销管理系统
    http: {
      postUrl: "...",
      basicmarketUrl: "...",
      basicmarketSign: "abcd",
    }
  },
  mes: {      // MES 系统
    apiUserCode: "digiwin",
    http: { postUrl: "..." }
  },
  eip: {      // EIP 系统
    apiUserCode: "digiwin",
    http: { postUrl: "..." }
  },
  isWechat: false,
  httpsAuto: true,
}
```

---

## 📝 服务编号 (system 参数)

| system | 服务 | 配置节点 | 用途 |
|--------|------|----------|------|
| **1** | SMS/DCP | `Config.sms.http.postUrl` | 登录、商品、订单、库存、菜单、角色等 |
| **2** | CRM | `Config.crm.http.postUrl` | 会员管理、积分、储值 |
| **3** | CRM Upload | `Config.crm.http.uploadUrl` | CRM 文件上传 |
| **4** | Supplier | `Config.supplier.http.postUrl` | 供应商平台 |
| **5** | Sale | `Config.sale.http.postUrl` | 营销活动、优惠券 |
| **6** | MES | `Config.mes.http.postUrl` | 生产执行系统 |
| **7** | EIP | `Config.eip.http.postUrl` | 企业信息平台 |

---

## 🚀 使用方式

### 在代码中使用（与 V3XUI 一致）

```typescript
import { post } from '@/utils/PostService'

// 调用 DCP 服务 (system: 1)
const result = await post(1, {
  serviceId: 'DCP_LoginRetail',
  request: {
    eId: '99',
    opNo: 'admin',
    password: 'xxx'
  }
})

// 调用 CRM 服务 (system: 2)
const memberResult = await post(2, {
  serviceId: 'CRM_MemberQuery',
  request: { memberId: '123' }
})

// 调用 MES 服务 (system: 6)
const mesResult = await post(6, {
  serviceId: 'MES_ProductionQuery',
  request: {}
})
```

### 直接访问 Config（全局变量）

```typescript
// 在浏览器控制台或任何地方
console.log(Config.sms.http.postUrl)
console.log(Config.isWechat)
console.log(Config.mes.apiUserCode)
```

---

## 🔄 与 V3XUI 的对比

| 项目 | V3XUI (Angular) | IBPUI (Vue3) |
|------|-----------------|--------------|
| **配置文件** | `config_retail.js` | `public/config_retail.js` |
| **加载方式** | `<script>` 标签 | `<script>` 标签 |
| **全局变量** | `Config` | `Config` |
| **服务调用** | `PostService.post(system, postData)` | `post(system, postData)` |
| **system 参数** | 1-7 | 1-7 (完全相同) |
| **配置节点** | `Config.sms.http.postUrl` | `Config.sms.http.postUrl` (完全相同) |

---

## ⚙️ 修改后端地址

### 开发环境
修改 `public/config_retail.js`：

```javascript
sms: {
  http: {
    postUrl: "http://你的服务器:8080/dcpService_v3x/DCP/services/invoke",
  }
}
```

### 生产环境
修改 `public/config_retail.js`，部署时更新。

---

## 📋 完整的调用示例

```typescript
import { post, postWithHeader } from '@/utils/PostService'

// 1. DCP 登录服务
const login = await post(1, {
  serviceId: 'DCP_LoginRetail',
  request: {
    eId: '99',
    opNo: 'admin',
    password: Md5.hashStr('admin123456')
  }
})
// 请求地址：Config.sms.http.postUrl

// 2. CRM 会员查询
const member = await post(2, {
  serviceId: 'CRM_MemberDetailQuery',
  request: { memberId: '123' }
})
// 请求地址：Config.crm.http.postUrl

// 3. CRM 文件上传
const upload = await post(3, {
  serviceId: 'CRM_Upload',
  request: { file: '...' }
})
// 请求地址：Config.crm.http.uploadUrl

// 4. 营销查询
const promotion = await post(5, {
  serviceId: 'PROM_RulesQuery',
  request: { shopId: '001' }
})
// 请求地址：Config.sale.http.postUrl

// 5. MES 服务（带特殊 header）
const mes = await postWithHeader(6, {
  orderNo: 'MO20260417001'
}, 'MES_OrderQuery')
// 请求地址：Config.mes.http.postUrl

// 6. EIP 服务（带特殊 header）
const eip = await postWithHeader(7, {
  empId: 'E001'
}, 'EIP_EmployeeQuery')
// 请求地址：Config.eip.http.postUrl
```

---

## ✅ 已完全复制 V3XUI 的配置

- ✅ 配置文件结构完全相同
- ✅ 配置节点命名完全相同
- ✅ 服务调用方式完全相同
- ✅ system 参数映射完全相同
- ✅ 支持所有服务模块 (SMS/CRM/Supplier/Sale/MES/EIP)
- ✅ 使用全局变量 `Config`（无 export）
- ✅ 通过 `<script>` 标签加载

---

*最后更新：2026-04-17*
