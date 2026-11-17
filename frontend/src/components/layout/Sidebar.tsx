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

const linkClass = 'block rounded-md px-2.5 py-2 text-left'
const inactiveLinkClass = `${linkClass} text-muted hover:bg-hover`
const activeLinkClass = `${linkClass} bg-accent-soft font-medium text-accent-strong`

function SidebarLink({ to, label }: NavItem) {
  return (
    <NavLink to={to} end className={({ isActive }) => (isActive ? activeLinkClass : inactiveLinkClass)}>
      {label}
    </NavLink>
  )
}

export function Sidebar({ isAdmin, onSignOut }: SidebarProps) {
  return (
    <nav className="flex flex-col gap-0.5 border-r border-line bg-sidebar px-2.5 py-4" aria-label="Main navigation">
      <div className="mx-2.5 mb-3.5 font-semibold">World Event Alerts</div>

      {userItems.map((item) => (
        <SidebarLink key={item.to} {...item} />
      ))}

      {isAdmin && (
        <>
          <div className="mx-2.5 mt-3.5 mb-1 text-[11px] text-muted">Admin</div>
          {adminItems.map((item) => (
            <SidebarLink key={item.to} {...item} />
          ))}
        </>
      )}

      <div className="flex-1" />
      <button type="button" className={`${inactiveLinkClass} cursor-pointer`} onClick={onSignOut}>
        Sign out
      </button>
    </nav>
  )
}
