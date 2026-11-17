import { Outlet } from 'react-router'
import { Sidebar } from './Sidebar'

export function AppLayout() {
  const isAdmin = true

  return (
    <div className="app">
      <Sidebar isAdmin={isAdmin} onSignOut={() => undefined} />
      <main className="main">
        <Outlet />
      </main>
    </div>
  )
}
