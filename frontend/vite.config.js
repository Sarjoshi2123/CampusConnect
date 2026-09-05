// Purpose: Vite build/dev-server configuration for the CampusConnect frontend.
// Role: Enables the React plugin and proxies /api requests during `npm run dev`
//       to the Spring Boot backend (default port 8080), so the browser sees
//       same-origin requests and the backend needs no CORS configuration at all.
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
