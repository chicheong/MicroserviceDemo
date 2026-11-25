package com.demo.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Product getProductSample1() {
        return new Product()
            .id(1L)
            .name("name1")
            .code("code1")
            .brand("brand1")
            .description("description1")
            .content("content1")
            .remark("remark1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static Product getProductSample2() {
        return new Product()
            .id(2L)
            .name("name2")
            .code("code2")
            .brand("brand2")
            .description("description2")
            .content("content2")
            .remark("remark2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static Product getProductRandomSampleGenerator() {
        return new Product()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .brand(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString())
            .remark(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
