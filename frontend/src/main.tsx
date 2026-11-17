import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router'
import './index.css'
import App from './App.tsx'
import { initKeycloak } from './auth/keycloak'

const queryClient = new QueryClient()
const root = createRoot(document.getElementById('root')!)

initKeycloak()
  .then(() =>
    root.render(
      <StrictMode>
        <QueryClientProvider client={queryClient}>
          <BrowserRouter>
            <App />
          </BrowserRouter>
          <ReactQueryDevtools initialIsOpen={false} />
        </QueryClientProvider>
      </StrictMode>,
    ),
  )
  .catch(() =>
    root.render(
      <p className="p-6 text-muted">Sign-in is currently unavailable. Check that Keycloak is running and reload the page.</p>,
    ),
  )
