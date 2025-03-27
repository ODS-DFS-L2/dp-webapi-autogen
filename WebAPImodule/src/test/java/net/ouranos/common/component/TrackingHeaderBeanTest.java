package net.ouranos.common.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TrackingHeaderBeanTest {

    private MockHttpServletRequest request;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testTrackingHeaderBean_withHeader() {
        // テストデータ
        UUID expectedUUID = UUID.randomUUID();
        request.addHeader("x-Tracking", expectedUUID.toString());

        // 実行
        TrackingHeaderBean trackingHeaderBean = new TrackingHeaderBean();
        UUID actualUUID = trackingHeaderBean.getUUID();

        // 検証
        assertEquals(expectedUUID, actualUUID, "ヘッダーから取得したUUIDが期待通りであること");
    }

    @Test
    public void testTrackingHeaderBean_withoutHeader() {
        // 実行
        TrackingHeaderBean trackingHeaderBean = new TrackingHeaderBean();
        UUID actualUUID = trackingHeaderBean.getUUID();

        // 検証
        assertNotNull(actualUUID, "UUIDが生成されていること");
    }
}