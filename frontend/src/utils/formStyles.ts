const baseInputClass = 'w-full rounded-md border bg-white px-2.5 py-1.5 text-[13px] text-ink focus:outline-none'

export const inputClass = `${baseInputClass} border-line-strong focus:border-accent`

const invalidInputClass = `${baseInputClass} border-red-600 focus:border-red-600`

export function fieldInputClass(error?: string) {
  return error ? invalidInputClass : inputClass
}
