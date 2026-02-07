package com.nineties.alumni.security;

import java.util.List;

public record CurrentUser(long userId, int trustLevel, List<String> roles) {}
