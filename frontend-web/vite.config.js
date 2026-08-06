// cruise-management-system/frontend-web/vite.config.js
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: "0.0.0.0", // BẮT BUỘC: Cho phép Docker map cổng ra ngoài máy thật
    port: 5173,
    strictPort: true,
    watch: {
      usePolling: true, // Giúp Hot-reload hoạt động mượt mà trên Docker (Windows/WSL)
    },
  },
});
