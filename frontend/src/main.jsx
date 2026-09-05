// Purpose: Frontend entry point.
// Role: Mounts the root <App/> component into the page's #root element. This is
//       the only file that touches ReactDOM directly.
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './App.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
