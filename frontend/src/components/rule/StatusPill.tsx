export function StatusPill({ enabled }: { enabled: boolean }) {
  return enabled ? (
    <span className="rounded-md bg-green-50 px-2 py-0.5 text-[11px] text-green-800">Enabled</span>
  ) : (
    <span className="rounded-md bg-hover px-2 py-0.5 text-[11px] text-muted">Disabled</span>
  )
}
