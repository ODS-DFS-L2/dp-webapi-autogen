package net.ouranos.application.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import net.ouranos.domain.model.Parts;

/**
 * Unit tests for the {@link PartsApi} class.
 */
public class PartsApiTest {

    private PartsApi partsApi = new PartsApi() {

        @Override
        public ResponseEntity<Void> partsDelete(UUID xTracking, String queryParam) {
            throw new UnsupportedOperationException("Unimplemented method 'partsDelete'");
        }

        @Override
        public ResponseEntity<Object> partsGet(UUID xTracking) {
            throw new UnsupportedOperationException("Unimplemented method 'partsGet'");
        }

        @Override
        public ResponseEntity<Object> partsPut(UUID xTracking, Parts parts) {
            throw new UnsupportedOperationException("Unimplemented method 'partsPut'");
        }

        @Override
        public ResponseEntity<Object> partsPost(UUID xTracking, Parts parts) {
            throw new UnsupportedOperationException("Unimplemented method 'partsPut'");
        }
        
    };

    /**
        * test getRequest method.
    */
    @Test
    public void testGetRequest() {
        // Act
        Optional<NativeWebRequest> response = partsApi.getRequest();

        // Assert
        assertFalse(response.isPresent());
    }
}