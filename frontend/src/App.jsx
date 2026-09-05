// Purpose: Root application component.
// Role: Renders the nav and switches between screens with plain useState (no
//       routing library — the scope is a small, fixed set of screens).
import { useState } from 'react'
import Nav from './components/Nav.jsx'
import CreateExamScreen from './components/CreateExamScreen.jsx'
import CreateRoomsScreen from './components/CreateRoomsScreen.jsx'
import RegisterStudentScreen from './components/RegisterStudentScreen.jsx'
import RescheduleScreen from './components/RescheduleScreen.jsx'
import CheckInScreen from './components/CheckInScreen.jsx'
import RoomUtilizationScreen from './components/RoomUtilizationScreen.jsx'
import NoShowScreen from './components/NoShowScreen.jsx'

export default function App() {
  const [active, setActive] = useState('create-exam')

  return (
    <div className="app-layout">
      <h1>CampusConnect</h1>
      <Nav active={active} onSelect={setActive} />

      {active === 'create-exam' && <CreateExamScreen />}
      {active === 'create-rooms' && <CreateRoomsScreen />}
      {active === 'register' && <RegisterStudentScreen />}
      {active === 'reschedule' && <RescheduleScreen />}
      {active === 'check-in' && <CheckInScreen />}
      {active === 'utilization' && <RoomUtilizationScreen />}
      {active === 'no-show' && <NoShowScreen />}
    </div>
  )
}
