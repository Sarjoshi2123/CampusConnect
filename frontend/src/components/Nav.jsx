// Purpose: Top-level navigation between the app's screens.
// Role: Plain tab buttons — no routing library, since the scope is a fixed,
//       small set of screens. Highlights the active tab and calls back to
//       App.jsx to switch which screen is rendered.
const SCREENS = [
  { key: 'create-exam', label: 'Create Exam' },
  { key: 'create-rooms', label: 'Create Rooms' },
  { key: 'register', label: 'Register Student' },
  { key: 'reschedule', label: 'Reschedule' },
  { key: 'check-in', label: 'Check In' },
  { key: 'utilization', label: 'Room Utilization' },
  { key: 'no-show', label: 'Mark No-Show' },
]

export default function Nav({ active, onSelect }) {
  return (
    <nav className="app-nav">
      {SCREENS.map((screen) => (
        <button
          key={screen.key}
          className={active === screen.key ? 'active' : ''}
          onClick={() => onSelect(screen.key)}
        >
          {screen.label}
        </button>
      ))}
    </nav>
  )
}
