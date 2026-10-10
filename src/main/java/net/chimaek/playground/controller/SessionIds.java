package net.chimaek.playground.controller;

import java.util.regex.Pattern;

final class SessionIds {

    private static final Pattern ALLOWED = Pattern.compile("[A-Za-z0-9-]{1,64}");

    private SessionIds() {
    }

    static void require(String sessionId) {
        if (sessionId == null || !ALLOWED.matcher(sessionId).matches()) {
            throw new IllegalArgumentException("sessionId는 1~64자의 영문, 숫자, 하이픈만 허용합니다.");
        }
    }
}
