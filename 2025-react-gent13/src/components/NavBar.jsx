import useSWR from 'swr';
import { useAuth } from '../context/auth';
import Notifications from './Notifications';
import { getAll } from '../api';
import AsyncData from './AsyncData';

export default function NavBar() {
  const {isAuthed, user} = useAuth();
  const isAdmin = user?.rol === 'ADMINISTRATOR';

  const {
    meldingen = [],
    meldingenError,
    meldingenLoading,
  } = useSWR('meldingen', getAll);

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light fixed-top">
      <div className="container-fluid">
        <a className="navbar-brand" href="/">Delaware</a>
        {isAuthed && user?.rol && (
          <span className="navbar-text ms-auto me-3">
            {user.rol.toLowerCase()}
          </span>
        )}
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" 
          aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav">
            {!isAdmin && 
            <li className="nav-item">
              <a className="nav-link" href="/dashboard">Dashboard</a>
            </li>}
            <li className="nav-item">
              <a className="nav-link" href="/sites">Sites</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/machines">Machines</a>
            </li>
            <li className='nav-item'>
              {isAdmin && <a className='nav-link' href='/users'>Users</a>}
            </li>
            <li className='nav-item'>
              <a className='nav-link' href='/maintenances'>Maintenances</a>
            </li>
          </ul>
          <ul className="navbar-nav ms-auto">
            <li className="nav-item nav-link">
              <AsyncData loading={meldingenLoading} error={meldingenError}>
                <Notifications meldingen={meldingen} />
              </AsyncData>
            </li>
            { isAuthed ? (
              <li className="nav-item">
                <a className="nav-link" href="/logout">Logout</a>
              </li>
            ): (
              <li className="nav-item">
                <a className="nav-link" data-cy="logout_btn" href="/login">Login</a>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
}