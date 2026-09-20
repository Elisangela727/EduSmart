import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [react()],
  preview: {
    allowedHosts: [
      'pure-celebration-production-d4da.up.railway.app',
    ],
  },
})