import DataTable, { type TableProps, type ThemeProp } from 'react-data-table-component'
import { LoaderCircle } from 'lucide-react'

const THEME: ThemeProp = {
  '--rdt-color-scheme': 'light',
  '--rdt-font-family': 'var(--font-sans)',
  '--rdt-color-bg': 'var(--color-white)',
  '--rdt-color-header-bg': 'var(--color-sidebar)',
  '--rdt-color-footer-bg': 'var(--color-white)',
  '--rdt-color-text-primary': 'var(--color-ink)',
  '--rdt-color-text-secondary': 'var(--color-muted)',
  '--rdt-color-primary': 'var(--color-accent)',
  '--rdt-color-divider': 'var(--color-line)',
  '--rdt-col-sep-color': 'var(--color-line)',
  '--rdt-color-highlight': 'var(--color-hover)',
  '--rdt-color-highlight-text': 'var(--color-ink)',
  '--rdt-color-selected': 'var(--color-accent-soft)',
  '--rdt-color-selected-text': 'var(--color-accent-strong)',
  '--rdt-color-striped': 'var(--color-sidebar)',
  '--rdt-color-striped-text': 'var(--color-ink)',
  '--rdt-color-btn': 'var(--color-muted)',
  '--rdt-color-btn-hover': 'var(--color-hover)',
  '--rdt-color-btn-focus': 'var(--color-accent-soft)',
  '--rdt-color-btn-disabled': 'var(--color-line-strong)',
  '--rdt-color-scrollbar-thumb': 'var(--color-line-strong)',
  '--rdt-color-scrollbar-thumb-hover': 'var(--color-muted)',
  '--rdt-color-pin-border': 'var(--color-line)',
  '--rdt-font-size': '13px',
  '--rdt-font-size-header': '13px',
  '--rdt-header-height': '40px',
  '--rdt-row-height': '40px',
  '--rdt-border-radius': '6px',
}

const NO_DATA = <div className="flex w-full justify-center bg-white px-4 py-8 text-[13px] text-muted">No data</div>

const PROGRESS = (
  <div className="flex w-full justify-center bg-white px-4 py-8 text-muted">
    <LoaderCircle size={20} className="animate-spin" aria-label="Loading" />
  </div>
)

export type AppTableProps<T> = TableProps<T> & {
  onClick?: (row: T) => void
}

const WRAPPER_CLASS = String.raw`overflow-hidden rounded-md border border-line [&_.rdt\_headRow]:font-medium [&_.rdt\_headRow]:text-muted`

function Table<T>({ className, onClick, ...props }: AppTableProps<T>) {
  return (
    <div className={`${WRAPPER_CLASS} ${className ?? ''}`}>
      <DataTable<T>
        theme={THEME}
        highlightOnHover
        responsive
        persistTableHead
        noDataComponent={NO_DATA}
        progressComponent={PROGRESS}
        onRowClicked={onClick && ((row) => onClick(row))}
        pointerOnHover={!!onClick}
        {...props}
      />
    </div>
  )
}

export default Table
