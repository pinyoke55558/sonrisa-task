import type { ReactNode } from 'react'

type PageLayoutProps = {
  title: string
  description: string
  user?: string
  children: ReactNode
}

export function PageLayout({ title, description, user, children }: PageLayoutProps) {
  return (
    <>
      <header className="page-header">
        <div>
          <h1>{title}</h1>
          <p>{description}</p>
        </div>
        {user && <span className="page-user">{user}</span>}
      </header>
      {children}
    </>
  )
}

type SectionProps = {
  title: string
  actions?: ReactNode
  children: ReactNode
}

export function Section({ title, actions, children }: SectionProps) {
  return (
    <section className="section">
      <div className="section-header">
        <h2>{title}</h2>
        {actions}
      </div>
      {children}
    </section>
  )
}

export function EmptyState({ message }: { message: string }) {
  return <p className="empty-state">{message}</p>
}
