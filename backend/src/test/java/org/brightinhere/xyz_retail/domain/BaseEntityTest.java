package org.brightinhere.xyz_retail.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BaseEntityTest {

    private TestEntity testEntity;

    @BeforeEach
    void setUp() {
        testEntity = new TestEntity();}

    @Test
    void onCreate_setsCreatedAtAndUpdatedAtToCurrentTime() {
        LocalDateTime beforeCall = LocalDateTime.now();
        testEntity.onCreate();
        LocalDateTime afterCall = LocalDateTime.now();

        assertNotNull(testEntity.getCreatedAt());
        assertNotNull(testEntity.getUpdatedAt());
        assertTrue(testEntity.getCreatedAt().isAfter(beforeCall.minusSeconds(1)));
        assertTrue(testEntity.getCreatedAt().isBefore(afterCall.plusSeconds(1)));
        assertEquals(testEntity.getCreatedAt(), testEntity.getUpdatedAt());
    }

    @Test
    void onCreate_createdAtAndUpdatedAtAreSame() {
        testEntity.onCreate();

        assertEquals(testEntity.getCreatedAt(), testEntity.getUpdatedAt());
    }

    @Test
    void onUpdate_updatesOnlyUpdatedAt() {
        testEntity.onCreate();
        LocalDateTime originalCreatedAt = testEntity.getCreatedAt();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        testEntity.onUpdate();

        assertEquals(originalCreatedAt, testEntity.getCreatedAt());
        assertNotNull(testEntity.getUpdatedAt());
        assertTrue(testEntity.getUpdatedAt().isAfter(originalCreatedAt));
    }

    @Test
    void onUpdate_withoutPriorCreation_setsUpdatedAt() {
        LocalDateTime beforeCall = LocalDateTime.now();
        testEntity.onUpdate();
        LocalDateTime afterCall = LocalDateTime.now();

        assertNull(testEntity.getCreatedAt());
        assertNotNull(testEntity.getUpdatedAt());
        assertTrue(testEntity.getUpdatedAt().isAfter(beforeCall.minusSeconds(1)));
        assertTrue(testEntity.getUpdatedAt().isBefore(afterCall.plusSeconds(1)));
    }

    @Test
    void multipleUpdates_onlyUpdatedAtChanges() {
        testEntity.onCreate();
        LocalDateTime originalCreatedAt = testEntity.getCreatedAt();
        LocalDateTime firstUpdatedAt = testEntity.getUpdatedAt();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        testEntity.onUpdate();
        LocalDateTime secondUpdatedAt = testEntity.getUpdatedAt();

        assertEquals(originalCreatedAt, testEntity.getCreatedAt());
        assertTrue(secondUpdatedAt.isAfter(firstUpdatedAt));
    }

    /**
     * Concrete implementation of BaseEntity for testing purposes
     */
    private static class TestEntity extends BaseEntity {
    }
}