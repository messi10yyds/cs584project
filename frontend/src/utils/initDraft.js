const KEY = "initDraft";

export function loadInitDraft() {
    try {
        return JSON.parse(localStorage.getItem(KEY)) || {};
    } catch {
        return {};
    }
}

export function saveInitDraft(partial) {
    const prev = loadInitDraft();
    const next = { ...prev, ...partial };
    localStorage.setItem(KEY, JSON.stringify(next));
    return next;
}

export function clearInitDraft() {
    localStorage.removeItem(KEY);
}