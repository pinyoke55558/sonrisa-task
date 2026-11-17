import { NavLink } from 'react-router'

type NavItem = {
  to: string
  label: string
}

const userItems: NavItem[] = [
  { to: '/rules', label: 'My rules' },
  { to: '/events', label: 'Events' },
  { to: '/categories', label: 'Categories' },
]

const adminItems: NavItem[] = [
  { to: '/simulate', label: 'Simulate event' },
  { to: '/rules/all', label: 'All rules' },
]

type SidebarProps = {
  isAdmin: boolean
  onSignOut: () => void
}

function SidebarLink({ to, label }: NavItem) {
  return (
    <NavLink to={to} end className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
      {label}
    </NavLink>
  )
}

export function Sidebar({ isAdmin, onSignOut }: SidebarProps) {
  return (
    <nav className="sidebar" aria-label="Main navigation">
      <div className="sidebar-brand">World Event Alerts</div>

      {userItems.map((item) => (
        <SidebarLink key={item.to} {...item} />
      ))}

      {isAdmin && (
        <>
          <div className="sidebar-group">Admin</div>
          {adminItems.map((item) => (
            <SidebarLink key={item.to} {...item} />
          ))}
        </>
      )}

      <div className="sidebar-spacer" />
      <button type="button" className="nav-link sign-out" onClick={onSignOut}>
        Sign out
      </button>
    </nav>
  )
}
