# 智慧電商微服務平台

這是一個基於 Spring Boot 和 Spring Cloud 的微服務架構電商平台，用於展示 Java 後端開發能力和微服務架構經驗。

## 技術棧

- **Java 版本**: 17
- **Spring Boot**: 3.2.3
- **Spring Cloud**: 2023.0.0
- **資料庫**: MySQL, MongoDB, Redis
- **訊息佇列**: Kafka
- **容器化**: Docker, Kubernetes
- **CI/CD**: Jenkins

## 專案架構

專案包含以下微服務模組：

- **服務註冊與發現中心** (Service Registry): 使用 Eureka Server 實現服務註冊和發現
- **配置中心** (Config Server): 集中管理所有微服務的配置
- **API 閘道** (API Gateway): 實現路由、負載平衡、服務發現和安全控制
- **用戶服務** (User Service): 用戶註冊、登入、權限管理
- **產品服務** (Product Service): 產品資訊管理、分類、搜尋
- **訂單服務** (Order Service): 訂單創建、管理、狀態追蹤 (第二階段)
- **支付服務** (Payment Service): 整合支付閘道、支付狀態追蹤 (第二階段)
- **通知服務** (Notification Service): 郵件、簡訊通知 (第三階段)
- **分析服務** (Analytics Service): 處理使用者行為和訂單資料 (第三階段)

## 詳細開發進度

### 第一階段：基礎架構搭建
- [x] 設定 Spring Boot 專案
- [x] 建立父級 Maven 專案
- [x] 建立服務註冊與發現中心 (Service Registry)
- [x] 建立配置中心 (Config Server)
- [x] 建立 API 閘道 (API Gateway)

### 用戶服務 (User Service)
- [x] 用戶實體類和角色實體類
- [x] 儲存庫介面
- [x] DTO 類
- [x] JWT 安全配置
- [x] 用戶詳情服務
- [x] 控制器和服務層
- [x] 資料庫初始化器
- [x] API 文檔 (Swagger)
- [x] 單元測試
- [x] 整合測試

### 產品服務 (Product Service)
- [x] 產品實體類和產品詳細信息實體類
- [x] 產品類別實體類
- [x] 儲存庫介面
- [x] DTO 類
- [x] 服務層介面
- [ ] 服務層實現類
- [ ] 控制器
- [ ] 資料庫初始化器
- [ ] 單元測試
- [ ] 整合測試
- [ ] API 文檔 (Swagger)

### 第二階段：微服務擴展 (待開始)
#### 訂單服務 (Order Service)
- [ ] 訂單實體類
- [ ] 儲存庫介面
- [ ] DTO 類
- [ ] 服務層
- [ ] 控制器
- [ ] 與產品服務的整合

#### 支付服務 (Payment Service)
- [ ] 支付實體類
- [ ] 儲存庫介面
- [ ] DTO 類
- [ ] 服務層
- [ ] 控制器
- [ ] 模擬支付閘道整合

#### Redis 整合
- [ ] 配置 Redis
- [ ] 實現分散式快取
- [ ] 實現分散式鎖

### 第三階段：進階功能 (待開始)
#### Kafka 整合
- [ ] 配置 Kafka
- [ ] 實現事件驅動架構

#### 通知服務 (Notification Service)
- [ ] 通知實體類
- [ ] 儲存庫介面
- [ ] 服務層
- [ ] 控制器
- [ ] 郵件發送功能
- [ ] 簡訊發送功能

#### 分析服務 (Analytics Service)
- [ ] 資料收集
- [ ] 資料處理
- [ ] 報表生成

### 第四階段：DevOps 整合 (待開始)
#### 測試與 CI/CD
- [x] 測試與 CI/CD 規劃
- [x] 單元測試框架整合
- [x] 整合測試框架整合
- [ ] 效能測試框架整合
- [ ] GitHub Actions 工作流配置
- [ ] Docker 容器化
- [ ] Kubernetes 部署配置

#### 監控與日誌
- [ ] 整合 Spring Boot Actuator
- [ ] 整合 Prometheus
- [ ] 整合 Grafana
- [ ] 集中式日誌系統

## 如何運行

### 前置條件
- JDK 17
- Maven 3.8+
- Git
- MySQL 8.0+
- Docker (用於運行整合測試)

### 啟動順序
1. 啟動服務註冊中心 (Service Registry)
   ```
   cd service-registry
   mvn spring-boot:run
   ```

2. 啟動配置中心 (Config Server)
   ```
   cd config-server
   mvn spring-boot:run
   ```

3. 啟動 API 閘道 (API Gateway)
   ```
   cd api-gateway
   mvn spring-boot:run
   ```

4. 啟動用戶服務 (User Service)
   ```
   cd user-service
   mvn spring-boot:run
   ```

### 訪問服務
- 服務註冊中心: http://localhost:8761
- API 閘道: http://localhost:8080
- 用戶服務: http://localhost:8081/api/auth/signup (註冊)
- 用戶服務: http://localhost:8081/api/auth/signin (登入)
- 用戶服務 API 文檔: http://localhost:8081/swagger-ui.html

### 運行測試
```
# 運行單元測試
cd user-service
mvn test

# 運行整合測試 (需要 Docker)
cd user-service
mvn test -Dtest=*IntegrationTest
```

## 下一步計劃
1. 完成產品服務的服務層實現類和控制器
2. 配置 MySQL 和 MongoDB 資料庫
3. 為產品服務添加 API 文檔和測試
4. 設置基本的 CI/CD 流程
5. 開始實現訂單服務和支付服務

## 開發者

待補充...