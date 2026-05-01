package com.nineties.alumni.security;

import java.util.List;

public record CurrentUser(String userId, int trustLevel, List<String> roles) {}
