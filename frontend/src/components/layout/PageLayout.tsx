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
      <header className="mb-4 flex items-start justify-between">
        <div>
          <h1 className="m-0 text-lg font-semibold">{title}</h1>
          <p className="mt-0.5 text-xs text-muted">{description}</p>
        </div>
        {user && <span className="rounded-md border border-line px-2.5 py-1 text-xs text-muted">{user}</span>}
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
    <section className="mt-4.5 border-t border-line pt-3.5 first-of-type:mt-0 first-of-type:border-t-0 first-of-type:pt-0">
      <div className="mb-2 flex items-center justify-between">
        <h2 className="m-0 text-sm font-semibold">{title}</h2>
        {actions}
      </div>
      {children}
    </section>
  )
}

export function EmptyState({ message }: { message: string }) {
  return <p className="m-0 rounded-md border border-dashed border-line-strong p-6 text-center text-muted">{message}</p>
}
