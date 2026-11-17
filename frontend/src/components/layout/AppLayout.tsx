import { Outlet } from 'react-router'
import { keycloak, signOut } from '../../auth/keycloak'
import { Sidebar } from './Sidebar'

export function AppLayout() {
  return (
    <div className="grid min-h-screen grid-cols-[220px_1fr]">
      <Sidebar isAdmin={keycloak.hasRealmRole('ADMIN')} onSignOut={signOut} />
      <main className="bg-white px-6 py-5">
        <Outlet />
      </main>
    </div>
  )
}
