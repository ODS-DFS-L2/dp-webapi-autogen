package net.ouranos.common.context;

import java.util.UUID;

public record ApiContext (String apiUrl, Object datamodel, UUID xTracking, String queryParam, String token) {}