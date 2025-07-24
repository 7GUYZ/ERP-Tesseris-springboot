package com.jakdang.labs.api.jihun.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 엑셀 다운로드 성능 최적화 설정
 * 
 * 주요 최적화 포인트:
 * 1. JPA 배치 처리 최적화
 * 2. 메모리 사용량 최적화
 * 3. 대용량 데이터 처리 최적화
 * 4. 쿼리 성능 최적화
 */
@Configuration
@EnableTransactionManagement
public class ExcelDownloadConfig {

    /**
     * 엑셀 다운로드 성능 최적화 설정
     * 
     * 성능 최적화 내용:
     * - 배치 크기 최적화
     * - 메모리 사용량 최적화
     * - 쿼리 실행 최적화
     * - 대용량 데이터 처리 최적화
     */
    @Bean
    public ExcelDownloadProperties excelDownloadProperties() {
        return ExcelDownloadProperties.builder()
            .batchSize(10000)           // 배치 크기 (메모리 효율성)
            .chunkSize(50000)           // 청크 크기 (네트워크 효율성)
            .maxMemoryUsage(512)        // 최대 메모리 사용량 (MB)
            .queryTimeout(300)          // 쿼리 타임아웃 (초)
            .enableQueryCache(true)     // 쿼리 캐시 활성화
            .enableBatchProcessing(true) // 배치 처리 활성화
            .build();
    }

    /**
     * 엑셀 다운로드 성능 최적화 속성 클래스
     */
    public static class ExcelDownloadProperties {
        private final int batchSize;
        private final int chunkSize;
        private final int maxMemoryUsage;
        private final int queryTimeout;
        private final boolean enableQueryCache;
        private final boolean enableBatchProcessing;

        private ExcelDownloadProperties(Builder builder) {
            this.batchSize = builder.batchSize;
            this.chunkSize = builder.chunkSize;
            this.maxMemoryUsage = builder.maxMemoryUsage;
            this.queryTimeout = builder.queryTimeout;
            this.enableQueryCache = builder.enableQueryCache;
            this.enableBatchProcessing = builder.enableBatchProcessing;
        }

        public static Builder builder() {
            return new Builder();
        }

        public int getBatchSize() { return batchSize; }
        public int getChunkSize() { return chunkSize; }
        public int getMaxMemoryUsage() { return maxMemoryUsage; }
        public int getQueryTimeout() { return queryTimeout; }
        public boolean isEnableQueryCache() { return enableQueryCache; }
        public boolean isEnableBatchProcessing() { return enableBatchProcessing; }

        public static class Builder {
            private int batchSize = 10000;
            private int chunkSize = 50000;
            private int maxMemoryUsage = 512;
            private int queryTimeout = 300;
            private boolean enableQueryCache = true;
            private boolean enableBatchProcessing = true;

            public Builder batchSize(int batchSize) {
                this.batchSize = batchSize;
                return this;
            }

            public Builder chunkSize(int chunkSize) {
                this.chunkSize = chunkSize;
                return this;
            }

            public Builder maxMemoryUsage(int maxMemoryUsage) {
                this.maxMemoryUsage = maxMemoryUsage;
                return this;
            }

            public Builder queryTimeout(int queryTimeout) {
                this.queryTimeout = queryTimeout;
                return this;
            }

            public Builder enableQueryCache(boolean enableQueryCache) {
                this.enableQueryCache = enableQueryCache;
                return this;
            }

            public Builder enableBatchProcessing(boolean enableBatchProcessing) {
                this.enableBatchProcessing = enableBatchProcessing;
                return this;
            }

            public ExcelDownloadProperties build() {
                return new ExcelDownloadProperties(this);
            }
        }
    }
} 