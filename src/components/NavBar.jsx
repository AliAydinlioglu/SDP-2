import { useAuth } from '../context/auth';
import Notifications from './Notifications';

export default function NavBar() {
  const {isAuthed} = useAuth();

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light fixed-top">
      <div className="container-fluid">
        <a className="navbar-brand" href="/">Delaware</a>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" 
          aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav">
            <li className="nav-item">
              <a className="nav-link" href="/dashboard">Dashboard</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/sites">Sites</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/machines">Machines</a>
            </li>
          </ul>
          <ul className="navbar-nav ms-auto me-3">
            <li className="nav-item nav-link">
              <Notifications />
            </li>
            { isAuthed ? (
              <li className="nav-item">
                <a className="nav-link" href="/logout">Logout</a>
              </li>
            ): (
              <li className="nav-item">
                <a className="nav-link" href="/login">Login</a>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
}