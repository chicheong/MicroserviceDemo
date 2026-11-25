package com.demo.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProductItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static ProductItem getProductItemSample1() {
        return new ProductItem().id(1L).code("code1").quantity(1).currency("currency1");
    }

    public static ProductItem getProductItemSample2() {
        return new ProductItem().id(2L).code("code2").quantity(2).currency("currency2");
    }

    public static ProductItem getProductItemRandomSampleGenerator() {
        return new ProductItem()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .quantity(intCount.incrementAndGet())
            .currency(UUID.randomUUID().toString());
    }
}
