package com.ecommerce.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 消息響應 DTO
 * 
 * 此類用於封裝簡單的消息響應，通常用於返回操作結果或錯誤信息。
 * 提供一個統一的格式來返回各種消息給客戶端。
 */
@Data  // Lombok 註解，自動生成 getter、setter、equals、hashCode 和 toString 方法
@AllArgsConstructor  // Lombok 註解，自動生成全參構造函數
public class MessageResponse {
    
    /**
     * 消息內容
     * 
     * 包含要返回給客戶端的消息文本
     */
    private String message;
}