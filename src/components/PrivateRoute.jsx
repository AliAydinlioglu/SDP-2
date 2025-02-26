import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/auth'; 

export default function PrivateRoute(){
  const {ready, isAuthed} = useAuth();

  const {pathname} = useLocation();

  if (!ready) {
    return (
      <div className='container'>
        <div className='row'>
          <div className='col-12'>
            <h1>Loading...</h1>
            <p>
              Wacht even terwijl we je gegevens controleren en de
              website laden.
            </p>
          </div>
        </div>
      </div>
    );
  }
    
  if (isAuthed) {
    return <Outlet />;
  }
    
  return <Navigate replace to={`/login?redirect=${pathname}`} />;
}