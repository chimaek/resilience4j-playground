export function openEventSource(sessionId) {
    return new EventSource(`/api/stream?sessionId=${sessionId}`);
}
