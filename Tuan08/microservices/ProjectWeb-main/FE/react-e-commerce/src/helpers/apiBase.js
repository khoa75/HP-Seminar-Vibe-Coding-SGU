const apiBaseUrl = (import.meta.env.VITE_API_URL || "http://localhost:8181/v1/api").replace(/\/$/, "");

export default apiBaseUrl;
