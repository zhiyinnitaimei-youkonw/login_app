# login_app 项目交接文档

> 生成日期: 2026-06-16 | Android Studio 21.3.1 | Gradle 7.4 | AGP 7.3.1

---

## 1. 项目概述

模拟喜马拉雅登录 + 淘宝商城 + 收货地址管理的 Android 教学演示 App。

- **包名:** `com.example.login_app`
- **最低SDK:** 21 (Android 5.0)
- **目标SDK:** 32 (Android 12L)
- **语言:** Java 8
- **数据库:** SQLite (本地持久化)
- **邮件:** SMTP (QQ邮箱 SSL 465)

---

## 2. 功能清单

| 模块 | 功能 | 状态 |
|------|------|------|
| 登录 | 邮箱注册(SMTP验证码) + 密码登录 + 记住密码 | 正常 |
| 登录 | 密码找回(SMTP验证码→重置) | 正常 |
| 登录 | 输入校验(邮箱格式/密码8位数字+字母) | 正常 |
| 商城 | GridView商品网格 → 点击进详情页 | 正常 |
| 商城 | 商品详情(图/名/价/量/加入购物车/立即购买) | 正常 |
| 商城 | ListView购物车(数量加减/删除/合计/结算) | 正常 |
| 支付 | 支付成功页(立即购买/购物车结算均跳转) | 正常 |
| 个人中心 | 头像选择(6色Dialog) + 昵称编辑 + 退出登录 | 正常 |
| 收货地址 | 地址列表/新增/编辑/删除/设为默认 | 有Bug |
| 数据持久化 | SQLite四表(user/cart/profile/address) | 正常 |
| 数据持久化 | SharedPreferences(已废弃→改用SQLite) | - |

---

## 3. 项目结构

```
login_app/
├── app/
│   ├── build.gradle              # 依赖: appcompat/material/constraintlayout/android-mail
│   ├── src/main/
│   │   ├── AndroidManifest.xml   # 9个Activity + INTERNET权限
│   │   ├── java/com/example/login_app/
│   │   │   ├── LoginActivity.java          # 登录/注册(邮箱+SMTP)
│   │   │   ├── ForgotPasswordActivity.java # 找回密码
│   │   │   ├── ProductListActivity.java    # 商城首页(GridView)
│   │   │   ├── ProductDetailActivity.java  # 商品详情
│   │   │   ├── CartActivity.java           # 购物车(ListView)
│   │   │   ├── PaymentSuccessActivity.java # 支付成功
│   │   │   ├── UserProfileActivity.java    # 个人中心
│   │   │   ├── AddressListActivity.java    # 地址列表
│   │   │   ├── AddressEditActivity.java    # 地址编辑
│   │   │   ├── Product.java               # 商品实体
│   │   │   ├── Address.java               # 地址实体
│   │   │   ├── DatabaseHelper.java        # DDL(SQLiteOpenHelper)
│   │   │   ├── UserDao.java               # 用户DML
│   │   │   ├── CartManager.java           # 购物车管理(单例+DB)
│   │   │   ├── AddressManager.java        # 地址管理(单例+DB)
│   │   │   ├── CartAdapter.java           # 购物车适配器
│   │   │   ├── ProductAdapter.java        # 商品适配器
│   │   │   └── MailSender.java            # SMTP邮件(Thread+Handler)
│   │   └── res/
│   │       ├── layout/            # 12个布局文件
│   │       ├── drawable/          # 图标/背景/头像
│   │       └── values/            # colors/strings/themes
├── build.gradle                   # 根构建(AGP 7.3.1)
├── settings.gradle                # 阿里云镜像源
├── gradle.properties              # JVM配置
└── gradle/wrapper/                # Gradle 7.4
```

---

## 4. 数据库设计 (SQLite v3)

```sql
-- 用户表 (注册信息)
CREATE TABLE user (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    remember INTEGER DEFAULT 0,
    updated_at TEXT DEFAULT (datetime('now','localtime'))
);

-- 购物车表
CREATE TABLE cart (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL UNIQUE,
    product_name TEXT,
    product_price REAL,
    quantity INTEGER DEFAULT 1
);

-- 用户资料表
CREATE TABLE profile (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    nickname TEXT,
    avatar_res_id INTEGER DEFAULT 0
);

-- 收货地址表
CREATE TABLE address (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT, phone TEXT,
    province TEXT, city TEXT, district TEXT, detail TEXT,
    is_default INTEGER DEFAULT 0
);
```

---

## 5. 页面导航流程

```
LoginActivity (登录/注册)
    ├── ForgotPasswordActivity (找回密码)
    └── ProductListActivity (商城首页)
            ├── ProductDetailActivity (商品详情)
            │       └── PaymentSuccessActivity (支付成功)
            ├── CartActivity (购物车)
            │       └── PaymentSuccessActivity (支付成功)
            └── UserProfileActivity (个人中心)
                    └── AddressListActivity (地址列表)
                            └── AddressEditActivity (新增/编辑)
```

---

## 6. Git版本分支

| 分支 | 提交 | 内容 |
|------|------|------|
| `v1-login` | 8e3022d | 登录界面设计 |
| `v2-shopping-cart` | e29f7aa | 购物车设计(GridView+ListView) |
| `v3-cart-upgrade` | a2b057a | 购物车升级(Adapter提取+布局重命名) |
| `v4-address-profile` | 2c07c12 | 收货地址+个人中心+头像 |
| `v5-login-dual-mode` | 4e5051b | 登录双模式(RadioButton) |
| `v6-sqlite-persist` | 5a19eba | SQLite持久化 |
| `main` | 最新 | 含后续Bug修复 |

**GitHub:** `https://github.com/zhiyinnitaimei-youkonw/login_app`
**认证:** 用户名 `3133587426`, Token 已配置至本地git remote（见 `git remote -v`）

---

## 7. 已知问题

| # | 问题 | 状态 |
|---|------|------|
| 1 | 收货地址增删改均不生效(操作后回到列表数据未变) | 排查中 |
| 2 | SMTP邮件依赖`android-mail:1.6.7`+`android-activation:1.6.7` | 已修复 |
| 3 | 商品无真实图片(仅占位icon) | 可接受 |
| 4 | 验证码支持SMTP失败自动回退为模拟弹窗 | 已处理 |

---

## 8. 构建与运行

### 环境要求
- Android Studio 21.3.1+
- JDK 8
- Gradle 7.4 (wrapper自动下载)

### 运行步骤
1. Android Studio → File → Open → 选择 `E:\Android\login_app`
2. Sync Project with Gradle Files
3. OnePlus PKR110 真机 USB调试模式连接
4. Run (Shift+F10)

### 首次运行注意
- 需先卸载旧版App再安装(数据库版本升级可能有残留)
- 如SMTP闪退: 检查手机网络,确认QQ邮箱授权码有效
- 如地址页闪退: 查看Logcat过滤 `AddrList`/`AddrEdit`

---

## 9. 关键技术点

| 技术 | 应用 |
|------|------|
| **RadioGroup+RadioButton** | 登录/注册模式切换 |
| **EditText+TextWatcher** | 邮箱/密码输入校验 |
| **CheckBox+SharedPreferences** | 记住密码(已升级为SQLite) |
| **AlertDialog** | 操作反馈/验证码展示/删除确认 |
| **RelativeLayout** | 忘记密码右对齐叠加 |
| **SQLiteOpenHelper** | DDL建表+版本迁移(onUpgrade) |
| **ContentValues+Cursor** | DML增删改查 |
| **DAO模式** | UserDao封装DB操作 |
| **单例模式** | CartManager/AddressManager |
| **ViewHolder模式** | ProductAdapter/CartAdapter |
| **Thread+Handler** | SMTP异步发送(替代AsyncTask) |
| **Intent+startActivityForResult** | 页面参数传递(phone→new_password) |
| **SMTP SSL 465** | QQ邮箱发送验证码(凭据外部化) |

---

## 10. 凭据安全说明

SMTP邮箱凭据 **不在Git历史中**（已通过 `git filter-branch` 清洗所有历史提交）。

| 凭据 | 存储位置 | Git状态 |
|------|---------|---------|
| QQ邮箱账号密码 | `app/src/main/assets/mail.properties` | `.gitignore` 排除 |
| GitHub Token | 本地 `git remote` | 不进入仓库 |
| 配置模板 | `app/src/main/assets/mail.properties.template` | 已提交（无真实值） |

**本地部署时**：复制 `mail.properties.template` → `mail.properties`，填入真实QQ邮箱授权码即可。
