import type { ReactNode } from 'react'

export function Detail({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div>
      <dt className="mb-0.5 text-xs text-muted">{label}</dt>
      <dd className="m-0 text-[13px]">{children}</dd>
    </div>
  )
}
