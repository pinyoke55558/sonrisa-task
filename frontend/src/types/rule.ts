import type { CategoryDto } from './category'

export type ChannelType = 'EMAIL' | 'SLACK'

export type TargetDto = {
  id: number
  channel: ChannelType
  destination: string
}

export type TargetRequest = {
  channel: ChannelType
  destination: string
}

export type RuleDto = {
  id: number
  name: string
  enabled: boolean
  keywords: string[]
  categories: CategoryDto[]
  targets: TargetDto[]
  owner: string | null
  version: number
}

export type RuleSaveRequest = {
  name: string
  enabled?: boolean
  keywords: string[]
  categoryIds?: number[]
  targets: TargetRequest[]
}

export type RuleUpdateRequest = RuleSaveRequest & {
  version: number
}
