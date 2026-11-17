import type { ButtonHTMLAttributes } from 'react'

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'secondary'
  size?: 'small' | 'medium'
}

const variants = {
  primary: 'border-accent bg-accent text-white hover:bg-accent-strong',
  secondary: 'border-line-strong bg-white text-ink hover:bg-hover',
}

const sizes = {
  small: 'px-2.5 py-0.5 text-xs',
  medium: 'px-3.5 py-1.5 text-[13px]',
}

export function Button({ variant = 'secondary', size = 'medium', className = '', type = 'button', ...props }: ButtonProps) {
  return (
    <button
      type={type}
      className={`cursor-pointer rounded-md border ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    />
  )
}
