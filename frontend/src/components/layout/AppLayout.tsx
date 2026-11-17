import { Outlet } from 'react-router'
import { Sidebar } from './Sidebar'

export function AppLayout() {
  const isAdmin = true

  return (
    <div className="grid min-h-screen grid-cols-[220px_1fr]">
      <Sidebar isAdmin={isAdmin} onSignOut={() => undefined} />
      <main className="bg-white px-6 py-5">
        <Outlet />
      </main>
    </div>
  )
}
