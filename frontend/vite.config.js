import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const target =
    process.env.NODE_ENV === 'production'
        ? 'https://burza-app-bwgngrahgvcvdddf.westeurope-01.azurewebsites.net'
        : 'http://localhost:8080'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target,
        changeOrigin: true,
      },
    },
  },
})
