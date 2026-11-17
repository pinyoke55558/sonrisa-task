import { Navigate, Route, Routes } from 'react-router'
import { AppLayout } from './components/layout/AppLayout'
import { AllRulesPage } from './pages/AllRulesPage'
import { CategoriesPage } from './pages/CategoriesPage'
import { EventsPage } from './pages/EventsPage'
import { NotFoundPage } from './pages/NotFoundPage'
import { RulesPage } from './pages/RulesPage'
import { SimulateEventPage } from './pages/SimulateEventPage'

function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<Navigate to="/rules" replace />} />
        <Route path="rules" element={<RulesPage />} />
        <Route path="rules/all" element={<AllRulesPage />} />
        <Route path="events" element={<EventsPage />} />
        <Route path="categories" element={<CategoriesPage />} />
        <Route path="simulate" element={<SimulateEventPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
