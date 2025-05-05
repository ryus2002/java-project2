# 智慧電商微服務平台 - 專案重點整理

本文件旨在幫助新手開發者快速理解智慧電商微服務平台的核心概念、關鍵技術和重要代碼片段，讓您能夠更快地上手這個項目。

## 目錄

1. [微服務架構概述](#1-微服務架構概述)
2. [核心服務詳解](#2-核心服務詳解)
3. [關鍵技術實現](#3-關鍵技術實現)
4. [安全認證機制](#4-安全認證機制)
5. [數據庫設計與訪問](#5-數據庫設計與訪問)
6. [API設計與文檔](#6-api設計與文檔)
7. [測試策略](#7-測試策略)
8. [常見問題與解決方案](#8-常見問題與解決方案)

## 1. 微服務架構概述

### 1.1 整體架構圖

```
┌─────────────────┐      ┌─────────────────┐
│                 │      │                 │
│  客戶端應用     │◄────►│   API 閘道      │
│  (Web/Mobile)   │      │  (API Gateway)  │
│                 │      │                 │
└─────────────────┘      └────────┬────────┘
                                  │
                                  ▼
┌─────────────────┐      ┌────────┴────────┐      ┌─────────────────┐
│                 │      │                 │      │                 │
│  服務註冊中心   │◄────►│  配置中心       │◄────►│  監控與日誌     │
│ (Service Registry)     │ (Config Server) │      │                 │
│                 │      │                 │      │                 │
└─────────────────┘      └─────────────────┘      └─────────────────┘
        ▲                        ▲                        ▲
        │                        │                        │
        ▼                        ▼                        ▼
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│                 │      │                 │      │                 │
│   用戶服務      │      │   產品服務      │      │   訂單服務      │
│ (User Service)  │◄────►│(Product Service)│◄────►│ (Order Service) │
│                 │      │                 │      │                 │
└─────────────────┘      └─────────────────┘      └─────────────────┘
        ▲                        ▲                        ▲
        │                        │                        │
        ▼                        ▼                        ▼
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│                 │      │                 │      │                 │
│   支付服務      │      │   通知服務      │      │   分析服務      │
│(Payment Service)│◄────►│(Notification    │◄────►│(Analytics       │
│                 │      │ Service)        │      │ Service)        │
└─────────────────┘      └─────────────────┘      └─────────────────┘
```

### 1.2 微服務通信方式

本項目採用了以下幾種微服務通信方式：

1. **同步通信**：使用 REST API 和 Feign Client 進行服務間的直接調用
2. **異步通信**：計劃使用 Kafka 進行事件驅動的通信（第三階段）
3. **服務發現**：通過 Eureka Server 實現服務的自動註冊與發現

### 1.3 關鍵配置文件

每個微服務都有自己的配置文件，但配置中心集中管理所有服務的配置。以下是一些關鍵配置：

**服務註冊中心配置 (service-registry/src/main/resources/application.yml)**:

```yaml
server:
  port: 8761  # Eureka Server 默認端口

eureka:
  client:
    register-with-eureka: false  # 不將自己註冊到 Eureka
    fetch-registry: false        # 不從 Eureka 獲取註冊信息
  server:
    wait-time-in-ms-when-sync-empty: 0  # 等待時間
```

**配置中心配置 (config-server/src/main/resources/application.yml)**:

```yaml
server:
  port: 8888  # 配置服務器端口

spring:
  cloud:
    config:
      server:
        git:
          uri: ${CONFIG_GIT_URI:https://github.com/yourusername/ecommerce-config}  # 配置倉庫
          default-label: ${CONFIG_GIT_BRANCH:main}  # 默認分支
          search-paths: ${CONFIG_SEARCH_PATHS:/*}   # 搜索路徑
```

**API 閘道配置 (api-gateway/src/main/resources/application.yml)**:

```yaml
server:
  port: 8080  # API 閘道端口

spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE  # 負載均衡到 USER-SERVICE
          predicates:
            - Path=/api/users/**, /api/auth/**  # 路徑匹配
        - id: product-service
          uri: lb://PRODUCT-SERVICE
          predicates:
            - Path=/api/products/**
```

## 2. 核心服務詳解

### 2.1 服務註冊與發現中心 (Service Registry)

服務註冊中心使用 Eureka Server 實現，負責管理所有微服務的註冊和發現。

**關鍵代碼 (ServiceRegistryApplication.java)**:

```java
@SpringBootApplication
@EnableEurekaServer  // 啟用 Eureka 服務器
public class ServiceRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
```

### 2.2 配置中心 (Config Server)

配置中心使用 Spring Cloud Config Server 實現，集中管理所有微服務的配置。

**關鍵代碼 (ConfigServerApplication.java)**:

```java
@SpringBootApplication
@EnableConfigServer  // 啟用配置服務器
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### 2.3 API 閘道 (API Gateway)

API 閘道使用 Spring Cloud Gateway 實現，提供路由、負載平衡和安全控制功能。

**關鍵代碼 (ApiGatewayApplication.java)**:

```java
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user-service", r -> r.path("/api/users/**", "/api/auth/**")
                .uri("lb://USER-SERVICE"))
            .route("product-service", r -> r.path("/api/products/**")
                .uri("lb://PRODUCT-SERVICE"))
            .build();
    }
}
```

### 2.4 用戶服務 (User Service)

用戶服務負責用戶管理、認證和授權功能，是整個系統的核心服務之一。

**關鍵實體類 (User.java)**:

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;
    
    @NotBlank
    @Size(max = 50)
    @Email
    @Column(unique = true)
    private String email;
    
    @NotBlank
    @Size(max = 120)
    private String password;
    
    @NotBlank
    @Size(max = 50)
    private String firstName;
    
    @NotBlank
    @Size(max = 50)
    private String lastName;
    
    private boolean enabled = true;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", 
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    // 構造函數、Getter 和 Setter 方法省略
}
```

**角色實體類 (Role.java)**:

```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
    
    public enum ERole {
        ROLE_USER,
        ROLE_MODERATOR,
        ROLE_ADMIN
    }
    
    // 構造函數、Getter 和 Setter 方法省略
}
```

**用戶服務控制器 (AuthController.java)**:

```java
@RestController
@RequestMapping("/api/auth")
@Tag(name = "認證", description = "用戶認證相關 API")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/signin")
    @Operation(summary = "用戶登入", description = "使用用戶名和密碼進行登入，返回 JWT 令牌")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 驗證用戶名和密碼
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            )
        );
        
        // 設置安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 生成 JWT 令牌
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // 獲取用戶詳情
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // 獲取用戶角色
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());
        
        // 返回 JWT 令牌和用戶信息
        return ResponseEntity.ok(new JwtResponse(
            jwt, 
            userDetails.getId(), 
            userDetails.getUsername(), 
            userDetails.getEmail(), 
            roles
        ));
    }
    
    @PostMapping("/signup")
    @Operation(summary = "用戶註冊", description = "註冊新用戶，默認分配 ROLE_USER 角色")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Username is already taken!"));
        }
        
        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is already in use!"));
        }
        
        // 創建新用戶
        User user = new User(
            signUpRequest.getUsername(), 
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getFirstName(),
            signUpRequest.getLastName()
        );
        
        // 設置用戶角色
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null) {
            // 默認分配 ROLE_USER 角色
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            // 根據請求分配角色
            strRoles.forEach(role -> {
                switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                    break;
                case "mod":
                    Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(modRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }
        
        user.setRoles(roles);
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
```

### 2.5 產品服務 (Product Service)

產品服務負責產品信息的管理，包括產品的增刪改查、分類管理等功能。

**產品實體類 (Product.java)**:

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @NotBlank
    @Size(max = 500)
    private String description;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
    
    @Min(0)
    private Integer stockQuantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductDetail productDetail;
    
    // 構造函數、Getter 和 Setter 方法省略
}
```

**產品類別實體類 (Category.java)**:

```java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String name;
    
    @Size(max = 200)
    private String description;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();
    
    // 構造函數、Getter 和 Setter 方法省略
}
```

## 3. 關鍵技術實現

### 3.1 JWT 安全認證

本項目使用 JWT (JSON Web Token) 進行用戶認證和授權。JWT 是一種基於 JSON 的開放標準，用於在網絡應用環境間傳遞聲明。

**JWT 工具類 (JwtUtils.java)**:

```java
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    @Value("${ecommerce.app.jwtSecret}")
    private String jwtSecret;
    
    @Value("${ecommerce.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    
    // 從 JWT 令牌生成用戶名
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    
    // 驗證 JWT 令牌
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        
        return false;
    }
    
    // 生成 JWT 令牌
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        
        return Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
}
```

**JWT 請求過濾器 (AuthTokenFilter.java)**:

```java
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 從請求中獲取 JWT 令牌
            String jwt = parseJwt(request);
            
            // 驗證 JWT 令牌
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 從 JWT 令牌獲取用戶名
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                
                // 加載用戶詳情
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // 創建身份驗證對象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 設置安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    // 從請求頭中解析 JWT 令牌
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        
        return null;
    }
}
```

### 3.2 Spring Security 配置

Spring Security 是一個強大的安全框架，用於處理認證和授權。以下是本項目的 Spring Security 配置：

**WebSecurityConfig.java**:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers("/api/auth/**").permitAll()  // 允許所有人訪問認證相關的 URL
                    .requestMatchers("/api/test/**").permitAll()  // 允許所有人訪問測試相關的 URL
                    .requestMatchers("/swagger-ui/**").permitAll()  // 允許所有人訪問 Swagger UI
                    .requestMatchers("/v3/api-docs/**").permitAll()  // 允許所有人訪問 API 文檔
                    .requestMatchers("/h2-console/**").permitAll()  // 允許所有人訪問 H2 控制台
                    .anyRequest().authenticated()  // 其他所有請求都需要認證
            );
        
        // 允許 H2 控制台的 frame 顯示
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        
        // 添加 JWT 過濾器
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

## 4. 數據庫設計與訪問

### 4.1 實體關係圖 (ERD)

```
┌───────────────┐       ┌───────────────┐       ┌───────────────┐
│     users     │       │  user_roles   │       │     roles     │
├───────────────┤       ├───────────────┤       ├───────────────┤
│ id            │       │ user_id       │───┐   │ id            │
│ username      │   ┌───│ role_id       │   │   │ name          │
│ email         │   │   └───────────────┘   └──►└───────────────┘
│ password      │◄──┘
│ first_name    │
│ last_name     │
│ enabled       │
└───────────────┘

┌───────────────┐       ┌───────────────┐       ┌───────────────┐
│   products    │       │product_details│       │  categories   │
├───────────────┤       ├───────────────┤       ├───────────────┤
│ id            │◄──┐   │ id            │       │ id            │
│ name          │   │   │ product_id    │◄──┐   │ name          │
│ description   │   │   │ image_url     │   │   │ description   │
│ price         │   │   │ dimensions    │   │   └───────────────┘
│ stock_quantity│   │   │ weight        │   │           ▲
│ category_id   │───┘   │ material      │   │           │
└───────────────┘       │ specifications│   └───────────┘
        │               └───────────────┘
        ▼
┌───────────────┐
│  order_items  │
├───────────────┤
│ id            │
│ order_id      │◄──┐
│ product_id    │   │
│ quantity      │   │
│ price         │   │
└───────────────┘   │
                    │
┌───────────────┐   │
│    orders     │   │
├───────────────┤   │
│ id            │───┘
│ user_id       │
│ order_date    │
│ status        │
│ total_amount  │
└───────────────┘
```

### 4.2 資料庫初始化器

系統啟動時自動創建預設角色和管理員用戶：

**DatabaseInitializer.java**:

```java
@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // 初始化角色
        initRoles();
        
        // 創建管理員用戶
        createAdminUser();
    }
    
    private void initRoles() {
        // 檢查角色是否已存在
        if (roleRepository.count() == 0) {
            // 創建用戶角色
            Role userRole = new Role();
            userRole.setName(Role.ERole.ROLE_USER);
            roleRepository.save(userRole);
            
            // 創建版主角色
            Role modRole = new Role();
            modRole.setName(Role.ERole.ROLE_MODERATOR);
            roleRepository.save(modRole);
            
            // 創建管理員角色
            Role adminRole = new Role();
            adminRole.setName(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            
            System.out.println("Roles initialized successfully!");
        }
    }
    
    private void createAdminUser() {
        // 檢查管理員用戶是否已存在
        if (!userRepository.existsByUsername("admin")) {
            // 創建管理員用戶
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEnabled(true);
            
            // 設置管理員角色
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
            roles.add(adminRole);
            adminUser.setRoles(roles);
            
            // 保存管理員用戶
            userRepository.save(adminUser);
            
            System.out.println("Admin user created successfully!");
        }
    }
}
```

## 5. API設計與文檔

### 5.1 Swagger UI 整合

本項目使用 Swagger UI 提供 API 文檔，方便開發者和使用者了解和測試 API。

**OpenApiConfig.java**:

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("電商微服務 API")
                        .description("智慧電商微服務平台 API 文檔")
                        .version("1.0")
                        .contact(new Contact()
                                .name("開發團隊")
                                .email("team@example.com")
                                .url("https://example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("項目文檔")
                        .url("https://github.com/yourusername/ecommerce-microservices"));
    }
}
```

### 5.2 RESTful API 設計原則

本項目遵循 RESTful API 設計原則，主要包括：

1. **使用適當的 HTTP 方法**：
   - GET：獲取資源
   - POST：創建資源
   - PUT：更新資源
   - DELETE：刪除資源

2. **使用適當的 HTTP 狀態碼**：
   - 200 OK：請求成功
   - 201 Created：資源創建成功
   - 400 Bad Request：請求無效
   - 401 Unauthorized：未授權
   - 403 Forbidden：禁止訪問
   - 404 Not Found：資源不存在
   - 500 Internal Server Error：服務器錯誤

3. **使用適當的 URL 設計**：
   - 使用名詞而不是動詞
   - 使用複數形式
   - 使用層次結構表示資源關係

4. **使用適當的請求和響應格式**：
   - 使用 JSON 格式
   - 使用適當的字段名稱
   - 使用適當的數據類型

## 6. 測試策略

### 6.1 單元測試

單元測試用於測試單個組件的功能，確保每個組件都能正常工作。

**UserServiceTest.java**:

```java
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    public void testFindByUsername() {
        // 準備測試數據
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        // 設置 Mock 行為
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
        // 執行測試
        Optional<User> result = userService.findByUsername("testuser");
        
        // 驗證結果
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        
        // 驗證 Mock 方法被調用
        verify(userRepository, times(1)).findByUsername("testuser");
    }
    
    @Test
    public void testCreateUser() {
        // 準備測試數據
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("password");
        
        Role role = new Role();
        role.setId(1L);
        role.setName(ERole.ROLE_USER);
        
        // 設置 Mock 行為
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // 執行測試
        User result = userService.createUser(user);
        
        // 驗證結果
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        
        // 驗證 Mock 方法被調用
        verify(passwordEncoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findByName(ERole.ROLE_USER);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
```

### 6.2 整合測試

整合測試用於測試多個組件的交互，確保系統作為一個整體能夠正常工作。

**AuthControllerIntegrationTest.java**:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    public void setup() {
        // 清除數據
        userRepository.deleteAll();
        
        // 初始化角色
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);
            roleRepository.save(userRole);
            
            Role modRole = new Role();
            modRole.setName(ERole.ROLE_MODERATOR);
            roleRepository.save(modRole);
            
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }
    }
    
    @Test
    public void testSignup() throws Exception {
        // 準備測試數據
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        
        // 執行測試
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
        
        // 驗證用戶是否創建成功
        Optional<User> user = userRepository.findByUsername("testuser");
        assertTrue(user.isPresent());
        assertEquals("test@example.com", user.get().getEmail());
        assertEquals("Test", user.get().getFirstName());
        assertEquals("User", user.get().getLastName());
    }
    
    @Test
    public void testSignin() throws Exception {
        // 準備測試數據
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        
        userRepository.save(user);
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
        
        // 執行測試
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
```

## 7. 常見問題與解決方案

### 7.1 無法訪問 Swagger UI 或 H2 控制台

**問題**：配置了 Spring Security 後，無法訪問 Swagger UI 或 H2 控制台。

**解決方案**：在 WebSecurityConfig 中添加以下配置，允許訪問這些資源：

```java
.authorizeHttpRequests(auth -> 
    auth.requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/test/**").permitAll()
        .requestMatchers("/swagger-ui/**").permitAll()  // 允許訪問 Swagger UI
        .requestMatchers("/v3/api-docs/**").permitAll()  // 允許訪問 API 文檔
        .requestMatchers("/h2-console/**").permitAll()  // 允許訪問 H2 控制台
        .anyRequest().authenticated()
);

// 允許 H2 控制台的 frame 顯示
http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
```

### 7.2 JWT 令牌過期或無效

**問題**：使用 JWT 令牌訪問 API 時，返回 401 Unauthorized 錯誤。

**解決方案**：
1. 檢查 JWT 令牌是否過期，如果過期，需要重新登入獲取新的令牌。
2. 確保在請求頭中正確設置 Authorization 頭，格式為 "Bearer {token}"。
3. 檢查 JWT 令牌的簽名是否正確，確保使用了正確的密鑰。

### 7.3 跨域請求問題

**問題**：前端應用無法訪問後端 API，出現跨域請求錯誤。

**解決方案**：在 API 閘道或各個微服務中配置 CORS，允許跨域請求：

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("*");  // 允許所有來源，生產環境應該限制來源
        corsConfig.addAllowedMethod("*");  // 允許所有 HTTP 方法
        corsConfig.addAllowedHeader("*");  // 允許所有請求頭
        corsConfig.setMaxAge(3600L);       // 預檢請求的有效期
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
```

### 7.4 數據庫連接問題

**問題**：應用啟動時無法連接到數據庫。

**解決方案**：
1. 檢查數據庫連接配置是否正確，包括 URL、用戶名和密碼。
2. 確保數據庫服務正在運行。
3. 檢查數據庫防火牆設置，確保應用可以訪問數據庫。
4. 如果使用 Docker，確保容器網絡配置正確。

## 8. 總結與最佳實踐

### 8.1 微服務設計原則

1. **單一職責原則**：每個微服務應該只負責一個特定的業務功能。
2. **自治原則**：微服務應該能夠獨立部署、擴展和運行。
3. **彈性原則**：微服務應該能夠處理故障，不影響整個系統的運行。
4. **可觀測性原則**：微服務應該提供監控和日誌記錄功能，方便排查問題。

### 8.2 代碼質量最佳實踐

1. **編寫單元測試和整合測試**：確保代碼質量和功能正確性。
2. **使用代碼靜態分析工具**：如 SonarQube，檢查代碼質量問題。
3. **遵循編碼規範**：如 Google Java Style Guide，保持代碼風格一致。
4. **使用依賴注入**：減少組件間的耦合，提高可測試性。
5. **使用版本控制**：使用 Git 進行版本控制，遵循 Git Flow 工作流。

### 8.3 安全最佳實踐

1. **使用 HTTPS**：保護數據傳輸安全。
2. **實施身份驗證和授權**：使用 JWT 和 Spring Security。
3. **防止常見的安全漏洞**：如 SQL 注入、XSS 攻擊等。
4. **保護敏感數據**：如密碼、信用卡信息等。
5. **定期更新依賴**：修復已知的安全漏洞。

### 8.4 性能優化建議

1. **使用緩存**：如 Redis，減少數據庫訪問。
2. **實施數據庫優化**：如索引、查詢優化等。
3. **使用異步處理**：處理耗時操作，提高響應速度。
4. **實施負載均衡**：分散請求，提高系統容量。
5. **監控系統性能**：及時發現和解決性能問題。
```

這個文件現在包含了詳細的專案重點整理，涵蓋了微服務架構概述、核心服務詳解、關鍵技術實現、安全認證機制、數據庫設計與訪問、API設計與文檔、測試策略以及常見問題與解決方案。每個部分都包含了詳細的代碼片段和解釋，幫助新手開發者快速理解和上手這個專案。


