import { StrictMode, Suspense } from "react"
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import "./lib/i18n";

createRoot(document.getElementById('root')!).render(
    // TODO: 30/12/2025 Tests
    // TODO: 30/12/2025 Documentation
    // TODO: 30/12/2025 SOLID
  <StrictMode>
    <Suspense fallback={<div className="min-h-screen bg-gray-900 text-white flex items-center justify-center">Loading...</div>}>
      <App />
    </Suspense>
  </StrictMode>,
)
