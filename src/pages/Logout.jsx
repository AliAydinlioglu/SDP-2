import { useEffect } from 'react';
import { useAuth } from '../context/auth.js';
import { useNavigate } from 'react-router';

export default function Logout() {
  const { isAuthed, logout } = useAuth();

  const navigate = useNavigate();
  const handleOnClick = () => {
    navigate('/notifications');
  };

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
    <div className='alert-container'>
      <div className='alert alert-info'>U have been succesfully logged out</div>
      <button className='btn' onClick={handleOnClick}>Log in</button>
    </div>
  );
}
