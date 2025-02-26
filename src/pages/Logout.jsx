import { useEffect } from 'react';
import { useAuth } from '../context/auth.js';

export default function Logout() {
  const { isAuthed, logout } = useAuth();

  useEffect(() => {
    logout();
  }, [logout]);

  if (isAuthed) {
    return (
      <div className='container'>
        <div className='row'>
          <div className='col-12'>
            <h1>Logging out...</h1>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className='container'>
      <div className='row'>
        <div className='col-12'>
          <h1>U bent succesvol uitgelogd</h1>
        </div>
      </div>
    </div>
  );
}
