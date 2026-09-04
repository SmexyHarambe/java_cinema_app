import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    // Wajib 0.0.0.0 agar bisa diakses dari laptop via IP VM (mis. http://192.168.74.157:3000).
    // Tanpa ini Vite hanya listen di localhost VM dan browser laptop tidak bisa connect.
    host: '0.0.0.0',
    port: 3000,
    strictPort: true,
    proxy: {
      '/api': {
        // Backend jalan di VM yang sama, jadi localhost di sini = VM itu sendiri (benar).
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },
  preview: {
    host: '0.0.0.0',
    port: 3000,
  },
})
