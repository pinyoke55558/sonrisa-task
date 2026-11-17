import { Link } from 'react-router'
import { PageLayout } from '../components/layout/PageLayout'

export function NotFoundPage() {
  return (
    <PageLayout title="Page not found" description="The page you're looking for doesn't exist.">
      <Link to="/rules">Go to my rules</Link>
    </PageLayout>
  )
}
